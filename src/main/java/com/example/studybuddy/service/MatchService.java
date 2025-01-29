package com.example.studybuddy.service;

import com.example.studybuddy.dto.request.MatchRequestDto;
import com.example.studybuddy.dto.response.MatchResponseDto;
import com.example.studybuddy.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface MatchService {
    List<MatchResponseDto> findMatches();
    MatchResponseDto respondToMatch(Long matchId, boolean accept);
    boolean canUsersChat(Long userId1, Long userId2);
    User getCurrentUser();
    MatchResponseDto requestMatch(MatchRequestDto matchRequestDto);
}
