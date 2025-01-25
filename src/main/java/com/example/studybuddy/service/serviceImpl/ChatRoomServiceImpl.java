package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.model.ChatRoom;
import com.example.studybuddy.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl  {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<Long> getChatRoomId(Long senderId, Long recipientId, boolean createNewRoomIfNotExists) {
        return chatRoomRepository.getBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                        return Optional.of(createChatId(senderId, recipientId));
                    }
                    return Optional.empty();
                });
    }

    private Long createChatId(Long senderId, Long recipientId) {
        Long chatId = generateUniqueChatId(senderId, recipientId);

        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }

    private Long generateUniqueChatId(Long senderId, Long recipientId) {
        return Math.min(senderId, recipientId) * 1000 + Math.max(senderId, recipientId);
    }
}