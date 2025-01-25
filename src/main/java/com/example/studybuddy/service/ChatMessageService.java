package com.example.studybuddy.service;

import com.example.studybuddy.model.ChatMessage;
import com.example.studybuddy.model.MessageStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface ChatMessageService {
    ChatMessage save(ChatMessage chatMessage);
    List<ChatMessage> findChatMessages(Long senderId, Long recipientId);
    ChatMessage findById(Long id);
    void updateStatuses(String senderId, String recipientId, MessageStatus status);
    Long countNewMessages(Long senderId, Long recipientId);





}
