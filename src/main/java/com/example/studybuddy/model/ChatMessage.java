package com.example.studybuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_messages")
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(nullable = false)
    private String content;

    @Column(name = "TIMESTAMP", nullable = false)
    private LocalDateTime timestamp;
    @Column(name = "sender_name", nullable = false)
    private String senderName;
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @PrePersist
    public void onPrePersist() {
        this.timestamp = LocalDateTime.now();
    }

    private MessageStatus status;

}