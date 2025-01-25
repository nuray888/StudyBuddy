package com.example.studybuddy.dto.request;

import lombok.Data;

@Data
public class CommentCreateRequestDto {
//    Long id;
    Long userId;
    Long postId;
    String text;

}