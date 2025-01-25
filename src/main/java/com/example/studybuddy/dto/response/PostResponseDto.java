package com.example.studybuddy.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostResponseDto {
    private Long id;
    private String topic;
    private String subTopic;
    private Date createDate;
    private String userName;
    private List<MatchResponseDto> matches;
}
