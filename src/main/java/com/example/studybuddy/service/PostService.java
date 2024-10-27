package com.example.studybuddy.service;

import com.example.studybuddy.dto.request.PostRequest;
import com.example.studybuddy.dto.response.PostResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

    public List<PostResponse> getAll();
    public PostResponse create(PostRequest postRequest);
    public PostResponse findByID(Long id);
    public PostResponse update(Long id, PostRequest postRequest);
    public String delete(Long id);
    public List<PostResponse> findPostsByUserId(Long userId);

}
