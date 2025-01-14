package com.example.studybuddy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPageAndSortDto {
    private Long id;
    private String topic;
    private String subTopic;
    private Date createDate;
    private String userName;
    private List<MatchResponseDto> matches;


    //pagination
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;

}
