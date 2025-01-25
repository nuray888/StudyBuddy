package com.example.studybuddy.service;

import com.example.studybuddy.dto.request.PostRequestDto;
import com.example.studybuddy.dto.response.PostPageAndSortDto;
import com.example.studybuddy.dto.response.PostPageAndSortDto2;
import com.example.studybuddy.dto.response.PostResponseDto;
import com.example.studybuddy.model.User;

import java.util.List;

public interface PostService {

    PostPageAndSortDto2 getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    PostResponseDto create(PostRequestDto postRequestDto);
    PostPageAndSortDto findByID(Long id);
    PostPageAndSortDto update(Long id, PostRequestDto postRequestDto);
    PostRequestDto delete(Long id);
    List<PostPageAndSortDto> findPostsByUserId(Long userId);

    List<PostPageAndSortDto> findByKeyword(String topic);
    User getCurrentUser();

}
