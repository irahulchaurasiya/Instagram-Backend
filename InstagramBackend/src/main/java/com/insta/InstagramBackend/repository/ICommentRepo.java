package com.insta.InstagramBackend.repository;

import com.insta.InstagramBackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICommentRepo extends JpaRepository<Comment, Integer> {
}
