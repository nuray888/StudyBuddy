package com.example.studybuddy.repository;

import com.example.studybuddy.dto.response.PostResponse;
import com.example.studybuddy.model.Post;
import com.example.studybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findByUserId(Long userId);
}
