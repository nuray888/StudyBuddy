package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.dto.request.PostRequestDto;
import com.example.studybuddy.dto.response.MatchResponseDto;
import com.example.studybuddy.dto.response.PostPageAndSortDto;
import com.example.studybuddy.dto.response.PostPageAndSortDto2;
import com.example.studybuddy.dto.response.PostResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final MatchServiceImpl matchServiceImpl;
    private final EmailServiceImpl emailServiceImpl;


    @Value("${gemini.api.key}")
    private String geminiApiKey;


    public PostPageAndSortDto2 getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Post> postPage = repository.findAll(pageDetails);
        List<Post> posts = postPage.getContent();

        if (posts.isEmpty()) {
            throw new APIException("There is no post");
        }
        List<PostResponseDto> postDtos = posts.stream().map(p -> modelMapper.map(p, PostResponseDto.class)).toList();
        PostPageAndSortDto2 response = new PostPageAndSortDto2();
        response.setPosts(postDtos);
        response.setLastPage(postPage.isLast());
        response.setPageNumber(postPage.getNumber());
        response.setPageSize(postPage.getSize());
        response.setTotalPages(postPage.getTotalPages());
        response.setTotalElements(postPage.getTotalElements());
        return response;
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
        return response.getBody() != null && response.getBody().contains("yes");
    }


    public PostResponseDto create(PostRequestDto postRequestDto) {
        User user = getCurrentUser();

        if (!isStudyRelated(postRequestDto.getTopic(), postRequestDto.getSubTopic())) {
            throw new APIException("Topic and subt" +
                    "opic must be study related");
        }

        Post post = modelMapper.map(postRequestDto, Post.class);
        post.setUser(user);
        post.setCreateDate(new Date());

        Post savedPost = repository.save(post);

//
//        List<MatchResponseDto> matches = matchServiceImpl.findMatches(user.getId());
//
//
//        for (MatchResponseDto match : matches) {
//            emailServiceImpl.sendMatchNotification(match.getMatchedUserEmail(), user.getUserName());
//            emailServiceImpl.sendMatchNotification(user.getEmail(), match.getMatchedUserName());
//        }


        PostResponseDto response = modelMapper.map(savedPost, PostResponseDto.class);
//        response.setMatches(matches);
        return response;
    }


    @Override
    public PostPageAndSortDto findByID(Long id) {
        Post post = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found with " + id));
        return modelMapper.map(post, PostPageAndSortDto.class);
    }


    @Override
    public PostPageAndSortDto update(Long id, PostRequestDto postRequestDto) {
        User user = getCurrentUser();
        Post post = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found with " + id));

        if(!post.getUser().equals(user)) {
            throw new RuntimeException("You do not have permission to update this post");
        }

        modelMapper.map(postRequestDto, post);
        post.setUser(user);
        Post updatedPost = repository.save(post);
        return modelMapper.map(updatedPost, PostPageAndSortDto.class);
    }

    @Override
    public PostRequestDto delete(Long id) {
        User user = getCurrentUser();
        Post post = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found with " + id));

        if(!post.getUser().equals(user)) {
            throw new RuntimeException("You do not have permission to delete this post");
        }
        repository.deleteById(id);
        return modelMapper.map(post, PostRequestDto.class);
    }

    public List<PostPageAndSortDto> findPostsByUserId(Long userId) {
        List<Post> posts = repository.findByUserId(userId);
        if (posts.isEmpty()) {
            throw new APIException("This user does not have posts");
        }
        return posts.stream().map(post -> modelMapper.map(post, PostPageAndSortDto.class)).toList();

    }

    @Override
    public List<PostPageAndSortDto> findByKeyword(String topic) {
        List<Post> posts = repository.findByTopicLikeIgnoreCase(topic);
        return posts.stream().map(p -> modelMapper.map(p, PostPageAndSortDto.class)).toList();
    }

    @Override
    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
