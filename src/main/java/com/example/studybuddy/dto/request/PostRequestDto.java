package com.example.studybuddy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {
    @NotBlank
    @Size(min = 4,message = "Post name must contain at least 4 character")
    private String topic;
    @NotBlank
    @Size(min=3,message = "subTopic name must contain at least 3 character")
    private String subTopic;
    private Long userId;


}
