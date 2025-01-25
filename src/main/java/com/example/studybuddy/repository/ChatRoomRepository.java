package com.example.studybuddy.repository;

import com.example.studybuddy.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    Optional<ChatRoom> getBySenderIdAndRecipientId(Long senderId, Long recipientId);
}
