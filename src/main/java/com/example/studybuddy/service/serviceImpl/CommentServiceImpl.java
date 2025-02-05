package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.dto.request.CommentCreateRequestDto;
import com.example.studybuddy.dto.request.CommentUpdateRequestDto;
import com.example.studybuddy.dto.response.CommentResponseDto;
import com.example.studybuddy.exception.BadRequestException;
import com.example.studybuddy.exception.ResourceNotFoundException;
import com.example.studybuddy.model.Post;
import com.example.studybuddy.model.User;
import com.example.studybuddy.repository.CommentRepository;
import com.example.studybuddy.repository.PostRepository;
import com.example.studybuddy.repository.UserRepository;
import com.example.studybuddy.service.CommentService;
import com.example.studybuddy.model.Comment;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CommentResponseDto> getAllCommentsWithParam(Long postId) {
        User user = getCurrentUser();
        postRepository.findById(postId).orElseThrow(()->new ResourceNotFoundException("Post not found with "+postId));
        List<Comment> comments=commentRepository.findByUserIdAndPostId(user.getId(),postId);
        return comments.stream().map(c->modelMapper.map(c, CommentResponseDto.class)).toList();
    }

    @Override
    public CommentResponseDto getOneCommentById(Long commentId) {
        Comment comment=commentRepository.findById(commentId).orElseThrow(()->new ResourceNotFoundException("Comment not found with "+commentId));
        return modelMapper.map(comment, CommentResponseDto.class);
    }

    @Override
    public CommentResponseDto createOneComment(CommentCreateRequestDto request) {
        User user = getCurrentUser();
        Post post=postRepository.findById(request.getPostId()).orElseThrow(()->new ResourceNotFoundException("Post not found with "+request.getPostId()));
        Comment commentToSave = new Comment();
        commentToSave.setPost(post);
        commentToSave.setUser(user);
        commentToSave.setText(request.getText());
        commentToSave.setCreateDate(new Date());
        commentRepository.save(commentToSave);
        return modelMapper.map(commentToSave, CommentResponseDto.class);
    }

    @Override
    public CommentResponseDto updateOneCommentById(Long commentId, CommentUpdateRequestDto request) {
        User user = getCurrentUser();
        Comment comment=commentRepository.findById(commentId)
                .orElseThrow(()->new ResourceNotFoundException("Comment not found with "+commentId));
        if (!comment.getUser().equals(user)) {
            throw new BadRequestException("You do not have permission to update this comment");
        }
        modelMapper.map(request,comment);
        comment.setUser(user);
        Comment updatedComment=commentRepository.save(comment);
        return modelMapper.map(updatedComment, CommentResponseDto.class);
    }

    @Override
    public void deleteOneCommentById(Long commentId) {
        User user = getCurrentUser();
        Comment comment=commentRepository.findById(commentId)
                .orElseThrow(()->new ResourceNotFoundException("Comment not found with "+commentId));
        if (!comment.getUser().equals(user)) {
            throw new BadRequestException("You do not have permission to delete this comment");
        }
        commentRepository.deleteById(commentId);

    }

    @Override
    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
