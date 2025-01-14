package com.example.studybuddy.config;

import com.example.studybuddy.dto.request.PostRequestDto;
import com.example.studybuddy.model.Post;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(PostRequestDto.class, Post.class)
                .addMapping(src -> null, Post::setId);
         return mapper;
    }

}

