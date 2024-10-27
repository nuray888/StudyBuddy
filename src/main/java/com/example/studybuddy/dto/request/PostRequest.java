package com.example.studybuddy.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    @NotBlank
    @Size(min = 4,message = "Post name must contain at least 4 character")
    private String topic;
    private String subTopic;
    private Long userId;

}
