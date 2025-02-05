package com.example.studybuddy.dto.request;

import lombok.Data;

@Data
public class CommentCreateRequestDto {
    private Long postId;
    private String text;

}