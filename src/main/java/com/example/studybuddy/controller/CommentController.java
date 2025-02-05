package com.example.studybuddy.controller;


import java.util.List;

import com.example.studybuddy.dto.request.CommentCreateRequestDto;
import com.example.studybuddy.dto.request.CommentUpdateRequestDto;
import com.example.studybuddy.dto.response.CommentResponseDto;
import com.example.studybuddy.service.serviceImpl.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentServiceImpl service;
    
    @GetMapping("get-all")
    public ResponseEntity<List<CommentResponseDto>> getAllComments(@RequestParam Long postId) {
        List<CommentResponseDto> allCommentsWithParam = service.getAllCommentsWithParam(postId);
        return new ResponseEntity<>(allCommentsWithParam, HttpStatus.OK);
    }

    @PostMapping("new-comment")
    public ResponseEntity<CommentResponseDto> createOneComment(@RequestBody CommentCreateRequestDto request) {
        CommentResponseDto oneComment = service.createOneComment(request);
        return new ResponseEntity<>(oneComment,HttpStatus.CREATED);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> getOneComment(@PathVariable Long commentId) {
        CommentResponseDto oneCommentById = service.getOneCommentById(commentId);
        return new ResponseEntity<>(oneCommentById,HttpStatus.OK);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateOneComment(@PathVariable Long commentId, @RequestBody CommentUpdateRequestDto request) {
        CommentResponseDto commentResponseDto = service.updateOneCommentById(commentId, request);
        return new ResponseEntity<>(commentResponseDto,HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public String deleteOneComment(@PathVariable Long commentId) {
        service.deleteOneCommentById(commentId);
        return "Successfully deleted";
    }
}