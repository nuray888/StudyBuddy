package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.exception.ResourceNotFoundException;
import com.example.studybuddy.model.ChatMessage;
import com.example.studybuddy.model.MessageStatus;
import com.example.studybuddy.model.User;
import com.example.studybuddy.repository.ChatMessageRepository;
import com.example.studybuddy.repository.UserRepository;
import com.example.studybuddy.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomServiceImpl chatRoomServiceImpl;
    private final UserRepository userRepository;

    public ChatMessage save(ChatMessage chatMessage) {
        Long chatId = chatRoomServiceImpl.getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow(() -> new IllegalArgumentException("Chat room ID not found"));
        chatMessage.setChatId(chatId);
        User recipientUser=userRepository.findUserNameById(chatMessage.getRecipientId());
        chatMessage.setRecipientName(recipientUser.getUserName());
        User senderUser=userRepository.findUserNameById(chatMessage.getSenderId());
        chatMessage.setSenderName(senderUser.getUserName());
        chatMessage.setStatus(MessageStatus.RECEIVED);
        repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        Long chatId = chatRoomServiceImpl.getChatRoomId(senderId, recipientId, false)
                .orElseThrow(() -> new IllegalArgumentException("Chat room ID not found"));
        return repository.findByChatId(chatId);
    }

    public ChatMessage findById(Long id){
        return repository.findById(id).map(chatMessage -> {
            chatMessage.setStatus(MessageStatus.DELIVERED);
            return repository.save(chatMessage);
        }).orElseThrow(()->new ResourceNotFoundException("can't find message ("+id+")"));
    }

    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
        repository.updateStatuses(senderId, recipientId, status);
    }
    public Long countNewMessages(Long senderId, Long recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

}
