package com.example.studybuddy.controller;

import com.example.studybuddy.config.AppConstants;
import com.example.studybuddy.dto.request.PostRequestDto;
import com.example.studybuddy.dto.response.PostPageAndSortDto;
import com.example.studybuddy.dto.response.PostPageAndSortDto2;
import com.example.studybuddy.dto.response.PostResponseDto;
import com.example.studybuddy.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    @GetMapping("/get-all")
    public ResponseEntity<PostPageAndSortDto2> getAllController(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_POSTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        var all = service.getAll(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<PostResponseDto> create(@Valid @RequestBody PostRequestDto postRequestDto) {
        PostResponseDto postResponse = service.create(postRequestDto);
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @GetMapping("/find/by/{id}")
    public ResponseEntity<PostPageAndSortDto> findById(@PathVariable Long id) {
        PostPageAndSortDto byID = service.findByID(id);
        return new ResponseEntity<>(byID, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<PostRequestDto> deleteControllerById(@PathVariable Long id) {
        PostRequestDto delete = service.delete(id);
        return new ResponseEntity<>(delete, HttpStatus.OK);
    }

    @PutMapping("/edit-post")
    public ResponseEntity<PostPageAndSortDto> updateController(@RequestParam Long id, @Valid @RequestBody PostRequestDto postRequestDto) {
        PostPageAndSortDto update = service.update(id, postRequestDto);
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostPageAndSortDto>> findPostsByUserId(@PathVariable Long userId) {
        List<PostPageAndSortDto> postsByUserId = service.findPostsByUserId(userId);
        return new ResponseEntity<>(postsByUserId, HttpStatus.OK);
    }

    @GetMapping("/public/posts/keyword/{keyword}")
    public ResponseEntity<List<PostPageAndSortDto>> getPostsByKeyword(String topic) {
        List<PostPageAndSortDto> byKeyword = service.findByKeyword('%' + topic + '%');
        return new ResponseEntity<>(byKeyword, HttpStatus.FOUND);
    }


}
