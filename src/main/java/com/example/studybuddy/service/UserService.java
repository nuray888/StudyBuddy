package com.example.studybuddy.service;

import com.example.studybuddy.dto.request.LoginRequestDto;
import com.example.studybuddy.dto.request.UserRequestDto;
import com.example.studybuddy.dto.request.UserUpdateRequestDto;
import com.example.studybuddy.dto.response.UserResponseDto;
import com.example.studybuddy.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    List<UserResponseDto> getAll();
    String create(UserRequestDto userRequestDto);

    UserResponseDto findByID(Long id);
    UserResponseDto update(UserUpdateRequestDto userRequestDto);
    UserRequestDto delete(Long id);
    void updateLastSeen(Long id);

//    List<MatchResponse> findMatches(Long id);

    User getCurrentUser();









}
