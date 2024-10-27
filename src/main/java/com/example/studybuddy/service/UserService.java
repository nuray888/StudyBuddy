package com.example.studybuddy.service;

import com.example.studybuddy.dto.request.UserRequest;
import com.example.studybuddy.dto.response.UserResponse;
import com.example.studybuddy.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    List<UserResponse> getAll();
    String create(UserRequest userRequest);
    UserResponse findByID(Long id);
    UserResponse update(Long id, UserRequest userRequest);
    String delete(Long id);
    public void updateLastSeen(User user);








}
