package com.example.studybuddy.service;

import com.example.studybuddy.dto.response.MatchResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface MatchService {
    List<MatchResponseDto> findMatches(Long userId);
    MatchResponseDto requestMatch(Long requesterId, Long receiverId);
    void respondToMatch(Long matchId, boolean isAccepted);
    boolean canUsersChat(Long userId1, Long userId2);
}
