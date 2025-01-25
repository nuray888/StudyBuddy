package com.example.studybuddy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponseDto {
    private Long matchedUserId;
    private String matchedUserName;
    private String matchedUserEmail;
}


//like,comment,kaydedilenler,
//42
