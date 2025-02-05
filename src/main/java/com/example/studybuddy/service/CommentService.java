package com.example.studybuddy.service;

import com.example.studybuddy.dto.request.CommentCreateRequestDto;
import com.example.studybuddy.dto.request.CommentUpdateRequestDto;
import com.example.studybuddy.dto.response.CommentResponseDto;
import com.example.studybuddy.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    List<CommentResponseDto> getAllCommentsWithParam(Long postId);
    CommentResponseDto getOneCommentById(Long commentId);
    CommentResponseDto createOneComment(CommentCreateRequestDto request);
    CommentResponseDto updateOneCommentById(Long commentId, CommentUpdateRequestDto request);
    void deleteOneCommentById(Long commentId);
    User getCurrentUser();

}
