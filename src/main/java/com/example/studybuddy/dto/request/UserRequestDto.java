package com.example.studybuddy.dto.request;

import lombok.Data;

@Data
public class UserRequestDto {
    private String userName;
    private String email;
//    private LocalDate lastSeen;
//    private Status status;
    private String password;
}
