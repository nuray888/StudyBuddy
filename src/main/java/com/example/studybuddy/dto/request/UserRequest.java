package com.example.studybuddy.dto.request;

import com.example.studybuddy.model.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {
    private String userName;
    private String email;
    private LocalDate lastSeen;
    private Status status;
}
