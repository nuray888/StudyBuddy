package com.example.studybuddy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String userName;
    private String email;
    private List<PostPageAndSortDto> posts;

    public UserResponseDto(Long id, String userName) {
        this.id=id;
        this.userName=userName;
    }
}
