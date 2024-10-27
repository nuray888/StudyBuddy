package com.example.studybuddy.controller;
import com.example.studybuddy.dto.request.PostRequest;
import com.example.studybuddy.dto.response.PostResponse;
import com.example.studybuddy.exception.BadRequestException;
import com.example.studybuddy.service.serviceImpl.PostServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostServiceImpl service;


    @GetMapping("/get-all")
    public ResponseEntity<List<PostResponse>> getAllController() {
        List<PostResponse> all = service.getAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest postRequest) {
            PostResponse postResponse = service.create(postRequest);
            return new ResponseEntity<>(postResponse, HttpStatus.CREATED);

    }

    @GetMapping("/find/by/{id}")
    public ResponseEntity<PostResponse> findById(@PathVariable Long id){
        PostResponse byID = service.findByID(id);
        return new ResponseEntity<>(byID,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteControllerById(@PathVariable Long id) {
        String delete = service.delete(id);
        return new ResponseEntity<>(delete,HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<PostResponse> updateController(@RequestParam Long id, @Valid @RequestBody PostRequest postRequest) {
        PostResponse update = service.update(id, postRequest);
        return new ResponseEntity<>(update,HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> findPostsByUserId(@PathVariable Long userId){
        List<PostResponse> postsByUserId = service.findPostsByUserId(userId);
        return new ResponseEntity<>(postsByUserId,HttpStatus.OK);
    }



}
