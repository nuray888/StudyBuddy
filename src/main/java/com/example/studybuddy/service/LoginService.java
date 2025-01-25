package com.example.studybuddy.service;

import com.example.studybuddy.dto.request.LoginRequestDto;
import com.example.studybuddy.dto.response.LoginResponseDto;
import org.springframework.http.ResponseEntity;

public interface LoginService {

    ResponseEntity<?> login(LoginRequestDto requestDto);
}
