package com.insta.InstagramBackend.repository;

import com.insta.InstagramBackend.model.Follow;
import com.insta.InstagramBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFollowRepo extends JpaRepository<Follow, Integer> {

    List<Follow> findByCurrentUserAndCurrentUserFollower(User targetUser, User follower);
}
