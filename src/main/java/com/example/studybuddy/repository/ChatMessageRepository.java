package com.example.studybuddy.repository;

import com.example.studybuddy.model.ChatMessage;
import com.example.studybuddy.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findByChatId(Long s);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage c SET c.status = ?3 WHERE c.senderId = ?1 AND c.recipientId = ?2")
    void updateStatuses(String senderId, String recipientId, MessageStatus status);

    Long countBySenderIdAndRecipientIdAndStatus(Long senderId, Long recipientId, MessageStatus status);
}
