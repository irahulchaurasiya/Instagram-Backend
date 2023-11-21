package com.insta.InstagramBackend.service;

import com.insta.InstagramBackend.model.*;
import com.insta.InstagramBackend.repository.IUserRepo;
import com.insta.InstagramBackend.service.utility.emailUtility.EmailHandler;
import com.insta.InstagramBackend.service.utility.hashingUtility.PasswordEncrypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserService {

    @Autowired
    IUserRepo userRepo;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    public String signUpUser(User user) {

        String newEmail = user.getUserEmail();

        User existingUser = userRepo.findFirstByUserEmail(newEmail);

        if(existingUser != null)
        {
            return "email already in use";
        }

        String signUpPassword = user.getUserPassword();

        try {
            String encryptedPassword = PasswordEncrypter.encryptPassword(signUpPassword);

            user.setUserPassword(encryptedPassword);
            user.setBlueTick(false);

            // patient table - save patient
            userRepo.save(user);
            return "Insta user registered";

        } catch (NoSuchAlgorithmException e) {

            return "Internal Server issues while saving password, try again later!!!";
        }
    }


    public String signInUser(String email ,String password) {

        User existingUser = userRepo.findFirstByUserEmail(email);

        if(existingUser == null)
        {
            return "Not a valid email, Please sign up first !!!";
        }

        //password should be matched

        try {
            String encryptedPassword = PasswordEncrypter.encryptPassword(password);

            if(existingUser.getUserPassword().equals(encryptedPassword))
            {
                // return a token for this sign in
                AuthenticationToken token = new AuthenticationToken(existingUser);
                if (EmailHandler.sendEmail(email , "OTP after login",token.getTokenValue()))
                {
                    authenticationService.createToken(token);
                    return "Check email for otp/token";
                }
                else
                {
                    return "error while generating token";
                }
            }
            else {
                //password was wrong!!!
                return "Invalid Credentials!!!";
            }

        } catch (NoSuchAlgorithmException e) {

            return "Internal Server issues while saving password, try again later!!!";
        }

    }


    public String signOutUser(String email, String token) {
        if (authenticationService.authenticate(email,token)){
            authenticationService.deleteToken(token);
            return "Sign Out Successful!!!";
        }
        else {
            return "Un Authenticated Access";
        }
    }


    public String createInstaPost(Post post, String email) {

        User postOwner = userRepo.findFirstByUserEmail(email);
        post.setPostOwner(postOwner);
        return postService.createInstaPost(post);
    }

    public String removeInstaPost(Integer postId,String email) {

        User user = userRepo.findFirstByUserEmail(email);
        return postService.removeInstaPost(postId,user);
    }

    public String addComment(Comment comment,String commenterEmail) {

        boolean postValid = postService.validatePost(comment.getInstaPost());
        if(postValid) {
            User commenter = userRepo.findFirstByUserEmail(commenterEmail);
            comment.setCommenter(commenter);
            return commentService.addComment(comment);
        }
        else {
            return "Cannot comment on Invalid Post!!";
        }
    }

    boolean authorizeCommentRemover(String email,Comment comment)
    {
        String  commentOwnerEmail = comment.getCommenter().getUserEmail();
        String  postOwnerEmail  = comment.getInstaPost().getPostOwner().getUserEmail();

        return postOwnerEmail.equals(email) || commentOwnerEmail.equals(email);
    }

    public String removeInstaComment(Integer commentId, String email) {
        Comment comment  = commentService.findComment(commentId);
        if(comment!=null)
        {
            if(authorizeCommentRemover(email,comment))
            {
                commentService.removeComment(comment);
                return "comment removed successfully";
            }
            else
            {
                return "Unauthorized delete detected...Not allowed!!!!";
            }

        }
        else
        {
            return "Invalid Comment";
        }
    }

    public String addLike(Like like, String likeEmail) {

        Post instaPost = like.getInstaPost();
        boolean postValid = postService.validatePost(instaPost);

        if(postValid) {


            User liker = userRepo.findFirstByUserEmail(likeEmail);
            if(likeService.isLikeAllowedOnThisPost(instaPost,liker))
            {
                like.setLiker(liker);
                return likeService.addLike(like);
            }
            else {
                return "Already Liked!!";
            }

        }
        else {
            return "Cannot like on Invalid Post!!";
        }


    }

    public String getLikeCountByPost(Integer postId, String userEmail) {

        Post validPost = postService.getPostById(postId);

        if(validPost != null)
        {
            Integer likeCountForPost =  likeService.getLikeCountForPost(validPost);
            return String.valueOf(likeCountForPost);
        }
        else {
            return "Cannot like on Invalid Post!!";
        }
    }

    private boolean authorizeLikeRemover(String potentialLikeRemover, Like like) {

        String  likeOwnerEmail = like.getLiker().getUserEmail();
        return potentialLikeRemover.equals(likeOwnerEmail);
    }

    public String removeInstaLike(Integer likeId, String likerEmail) {

        Like like  = likeService.findLike(likeId);
        if(like!=null)
        {
            if(authorizeLikeRemover(likerEmail,like))
            {
                likeService.removeLike(like);
                return "like removed successfully";
            }
            else
            {
                return "Unauthorized delete detected...Not allowed!!!!";
            }

        }
        else
        {
            return "Invalid like";
        }
    }


    public String followUser(Follow follow, String followerEmail) {


        User followTargetUser = userRepo.findById(follow.getCurrentUser().getUserId()).orElse(null);

        User follower = userRepo.findFirstByUserEmail(followerEmail);

        if(followTargetUser!=null)
        {
            if(followService.isFollowAllowed(followTargetUser,follower))
            {
                followService.startFollowing(follow,follower);
                return follower.getUserHandle()  + " is now following " + followTargetUser.getUserHandle();
            }
            else {
                return follower.getUserHandle()  + " already follows " + followTargetUser.getUserHandle();
            }
        }
        else {
            return "User to be followed is Invalid!!!";
        }


    }

    private boolean authorizeUnfollow(String email, Follow follow) {

        String  targetEmail = follow.getCurrentUser().getUserEmail();
        String  followerEmail  = follow.getCurrentUserFollower().getUserEmail();

        return targetEmail.equals(email) || followerEmail.equals(email);
    }

    public String unFollowUser(Integer followId, String followerEmail) {

        Follow follow  = followService.findFollow(followId);
        if(follow != null)
        {
            if(authorizeUnfollow(followerEmail,follow))
            {
                followService.unfollow(follow);
                return follow.getCurrentUser().getUserHandle() + " unfollowed by " + followerEmail;
            }
            else
            {
                return "Unauthorized unfollow detected...Not allowed!!!!";
            }

        }
        else
        {
            return "Invalid follow mapping";
        }
    }

    public String addBlueTick(Integer userId) {
        User user = userRepo.findFirstByUserId(userId);
        if(user.getUserId().equals(userId))
        {
            user.setBlueTick(true);
            userRepo.save(user);
            return user.getUserHandle() + " is now Verified!!!";
        }
        else {
            return "Unknown Error occurred while verifying user!!!";
        }

    }
}
