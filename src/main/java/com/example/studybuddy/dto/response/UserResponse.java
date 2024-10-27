package com.example.studybuddy.dto.response;

import com.example.studybuddy.model.Status;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String userName;
    private String email;
    private LocalDate lastSeen;
    private Status status;
    private List<PostResponse> posts;
//    private Long avatarId;
}
