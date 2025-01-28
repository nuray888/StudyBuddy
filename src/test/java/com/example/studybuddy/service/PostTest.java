package com.example.studybuddy.service;

import com.example.studybuddy.repository.PostRepository;
import com.example.studybuddy.service.serviceImpl.PostServiceImpl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class PostTest {
    @InjectMocks
    private PostServiceImpl postService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PostRepository postRepository;

//   @Test
//    public void getAllTest() {
//        Post post1 = new Post(1L, "Math", "Linear");
//        Post post2 = new Post(2L, "Java", "Inheritance");
//        List<Post> posts = List.of(post1, post2);
//        List<PostResponse> expectedData = posts.stream()
//                .map(s -> modelMapper.map(s, PostResponse.class))
//                .toList();
//        when(postRepository.findAll()).thenReturn(posts);
//        List<PostResponse> actual = postService.getAll();
//        Assertions.assertEquals(expectedData, actual);
//    }


}
