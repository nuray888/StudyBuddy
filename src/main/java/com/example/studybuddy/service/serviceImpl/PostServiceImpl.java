package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.dto.request.PostRequest;
import com.example.studybuddy.dto.response.PostResponse;
import com.example.studybuddy.exception.APIException;
import com.example.studybuddy.exception.ResourceNotFoundException;
import com.example.studybuddy.model.Post;
import com.example.studybuddy.model.User;
import com.example.studybuddy.repository.PostRepository;
import com.example.studybuddy.repository.UserRepository;
import com.example.studybuddy.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
        private final PostRepository repository;
        private final ModelMapper modelMapper;
        private final UserRepository userRepository;
        private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

        public List<PostResponse> getAll () {
            List<Post> all = repository.findAll();
            if (all.isEmpty())
                throw new APIException("There is no post");
            List<PostResponse> list = all.stream()
                    .map(s -> modelMapper.map(s, PostResponse.class))
                    .toList();
            return list;
        }

        public boolean isStudyRelated(String topic, String subtopic) {
            String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=%s", geminiApiKey);
            String requestBody = String.format("{\"contents\":[{\"parts\":[{\"text\":\"Is the topic '%s' and subtopic '%s' related to study? Respond with 'yes' or 'no'\"}]}]}", topic, subtopic);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response;
            try {
                response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            } catch (HttpClientErrorException e) {
                throw new RuntimeException("Error calling Gemini API: " + e.getResponseBodyAsString());
            }
            return response.getBody() != null && response.getBody().contains("yes"); // Adjust accordingly
        }


    public PostResponse create(PostRequest postRequest) {
        User user = userRepository.findById(postRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!isStudyRelated(postRequest.getTopic(), postRequest.getSubTopic())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic and subtopic must be study-related.");
        }

        Post post = modelMapper.map(postRequest, Post.class);
        post.setUser(user);
        post.setCreateDate(new Date()); // This line sets the create date directly

        Post savedPost = repository.save(post);

        PostResponse response = modelMapper.map(savedPost, PostResponse.class);
        response.setCreateDate(savedPost.getCreateDate());
        return response;
    }


    @Override
    public PostResponse findByID(Long id) {
        Post post = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found with " + id));
        return modelMapper.map(post, PostResponse.class);
    }


    @Override
    public PostResponse update(Long id, PostRequest postRequest) {
        Post post = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not foun with " + id));
        modelMapper.map(postRequest, post);
        Post updatedPost = repository.save(post);
        return modelMapper.map(updatedPost, PostResponse.class);
    }

    @Override
    public String delete(Long id) {
        Post post = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found with " + id));
        repository.delete(post);
        return "Successfully deleted";
    }

    public List<PostResponse> findPostsByUserId(Long userId) {
        List<Post> posts = repository.findByUserId(userId);
        return posts.stream()
                .map(post -> {
                    PostResponse response = modelMapper.map(post, PostResponse.class);
                    response.setUserName(post.getUser().getUserName());
                    return response;
                }).toList();
    }
}
