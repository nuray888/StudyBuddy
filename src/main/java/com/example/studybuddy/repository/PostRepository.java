package com.example.studybuddy.repository;

import com.example.studybuddy.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findByUserId(Long userId);

    List<Post> findByTopicAndSubTopic(String topic, String subTopic);

    List<Post> findByTopicLikeIgnoreCase(String topic);

;
}
