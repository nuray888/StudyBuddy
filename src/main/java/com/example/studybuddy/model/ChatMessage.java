package com.example.studybuddy.model;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class ChatMessage {
    public ChatMessage() {
    }
    private String content;
    private String sender;

}
