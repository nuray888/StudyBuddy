package com.example.studybuddy.controller;

import com.example.studybuddy.dto.request.UserRequest;
import com.example.studybuddy.dto.response.UserResponse;
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

    @GetMapping("/get-all")
    public ResponseEntity<List<UserResponse>> getAllController() {
        List<UserResponse> all = service.getAll();
        return new ResponseEntity<>(all, HttpStatus.OK);

    }

    @PostMapping("/new")
    public ResponseEntity<String> create(@RequestBody @Valid UserRequest userRequest) {
        String s = service.create(userRequest);
        return new ResponseEntity<>(s,HttpStatus.CREATED);
    }

    @GetMapping("/find/by/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id){
        UserResponse byID = service.findByID(id);
        return new ResponseEntity<>(byID,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteControllerById(@PathVariable Long id) {
        String delete = service.delete(id);
        return new ResponseEntity<>(delete,HttpStatus.OK);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateController(@PathVariable Long id, @RequestBody @Valid UserRequest userRequest) {
        UserResponse update = service.update(id, userRequest);
        return new ResponseEntity<>(update,HttpStatus.OK);
    }



}
