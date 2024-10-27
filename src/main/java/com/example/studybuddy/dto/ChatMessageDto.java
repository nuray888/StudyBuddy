package com.example.studybuddy.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data

public class ChatMessageDto {
    private String sender;
    private String content;
    private LocalDateTime timestamp;

}
