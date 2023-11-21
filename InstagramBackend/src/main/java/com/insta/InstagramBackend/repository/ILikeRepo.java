package com.insta.InstagramBackend.repository;

import com.insta.InstagramBackend.model.Like;
import com.insta.InstagramBackend.model.Post;
import com.insta.InstagramBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILikeRepo extends JpaRepository<Like, Integer> {

    List<Like> findByInstaPostAndLiker(Post instaPost, User liker);

    List<Like> findByInstaPost(Post validPost);
}
