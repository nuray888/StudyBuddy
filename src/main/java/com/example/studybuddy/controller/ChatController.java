package com.example.studybuddy.controller;

import com.example.studybuddy.model.ChatMessage;
import com.example.studybuddy.model.ChatNotification;
import com.example.studybuddy.service.ChatMessageService;
import com.example.studybuddy.service.MatchService;
import com.example.studybuddy.service.serviceImpl.ChatRoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageServiceImpl;
    private final ChatRoomServiceImpl chatRoomService;
    private final MatchService matchService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage){

        boolean canChat=matchService.canUsersChat(chatMessage.getSenderId(),chatMessage.getRecipientId());
        if(!canChat){
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(chatMessage.getSenderId()),"queue/errors",
                    "You are not allowed to chat with this user"
            );
            return;
        }

        var chatId=chatRoomService
                .getChatRoomId(chatMessage.getSenderId(),chatMessage.getRecipientId(),true);
        chatMessage.setChatId(chatId.get());

        ChatMessage saved = chatMessageServiceImpl.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatMessage.getRecipientId()),"/queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSenderId(),
                        saved.getSenderName()));
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(
            @PathVariable("senderId") Long senderId,
            @PathVariable("recipientId") Long recipientId
    ) {
        List<ChatMessage> chatMessages = chatMessageServiceImpl.findChatMessages(senderId, recipientId);
        return ResponseEntity.ok(chatMessages);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage ( @PathVariable Long id) {
        return ResponseEntity
                .ok(chatMessageServiceImpl.findById(id));
    }

    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(
            @PathVariable Long senderId,
            @PathVariable Long recipientId) {

        return ResponseEntity
                .ok(chatMessageServiceImpl.countNewMessages(senderId, recipientId));
    }



}