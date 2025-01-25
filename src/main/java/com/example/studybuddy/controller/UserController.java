package com.example.studybuddy.controller;

import com.example.studybuddy.dto.request.LoginRequestDto;
import com.example.studybuddy.dto.request.UserRequestDto;
import com.example.studybuddy.dto.request.UserUpdateRequestDto;
import com.example.studybuddy.dto.response.UserResponseDto;
import com.example.studybuddy.exception.APIException;
import com.example.studybuddy.model.ChatMessage;
import com.example.studybuddy.service.serviceImpl.ChatMessageServiceImpl;
import com.example.studybuddy.service.serviceImpl.MatchServiceImpl;
import com.example.studybuddy.service.serviceImpl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceImpl service;
    private final MatchServiceImpl matchService;
    private final ChatMessageServiceImpl chatMessageServiceImpl;


    @GetMapping("/get-all")
    public ResponseEntity<List<UserResponseDto>> getAllController() {
        List<UserResponseDto> all = service.getAll();
        return new ResponseEntity<>(all, HttpStatus.OK);

    }

    @PostMapping("/new")
    public ResponseEntity<String> create(@RequestBody @Valid UserRequestDto userRequestDto) {
        String s = service.create(userRequestDto);
        return new ResponseEntity<>(s, HttpStatus.CREATED);
    }


    @GetMapping("/find/by/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        UserResponseDto byID = service.findByID(id);
        return new ResponseEntity<>(byID, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<UserRequestDto> deleteControllerById(@PathVariable Long id) {
        UserRequestDto delete = service.delete(id);
        return new ResponseEntity<>(delete, HttpStatus.OK);

    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDto> updateController(@RequestBody @Valid UserUpdateRequestDto userRequestDto) {
        UserResponseDto update = service.update(userRequestDto);
        return new ResponseEntity<>(update, HttpStatus.OK);
    }


    @GetMapping("{userId}/lastSeen")
    public void lastSeen(@PathVariable Long userId) {

        service.updateLastSeen(userId);
    }

    @PostMapping("/{senderId}/send-message/{recipientId}")
    public ResponseEntity<String> sendTestMessage(@PathVariable Long senderId, @PathVariable Long recipientId, @RequestBody String message) {

       // Context daxilinden userId goturulmelidir ve evez olunmalidir


        boolean canChat=matchService.canUsersChat(senderId,recipientId);
        if(!canChat){
            throw new APIException("You can't send messages to this user.");
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(senderId);
        chatMessage.setRecipientId(recipientId);
        chatMessage.setContent(message);

        chatMessageServiceImpl.save(chatMessage);


        return new ResponseEntity<>("Message sent", HttpStatus.OK);
    }
//    @PostMapping("send-message/{recipientId}")
//    public ResponseEntity<String> sendTestMessage(@PathVariable Long recipientId,@RequestBody String message){
//        Long senderId=getCurrent
//    }





    @GetMapping("/{userId}/chat-with/{otherUserId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable Long userId, @PathVariable Long otherUserId) {
        List<ChatMessage> chatMessages = chatMessageServiceImpl.findChatMessages(userId, otherUserId);
        return new ResponseEntity<>(chatMessages, HttpStatus.OK);
    }



}
