package com.example.studybuddy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchRequestDto {
    private Long requesterId;
    private Long receiverId;
}