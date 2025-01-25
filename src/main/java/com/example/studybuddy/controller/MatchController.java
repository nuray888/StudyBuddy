package com.example.studybuddy.controller;

import com.example.studybuddy.dto.request.MatchRequestDto;
import com.example.studybuddy.dto.response.MatchResponseDto;
import com.example.studybuddy.service.serviceImpl.MatchServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchServiceImpl matchServiceImpl;

    @PostMapping("/request")
    public MatchResponseDto requestMatch(@RequestBody MatchRequestDto matchRequestDto) {
        return matchServiceImpl.requestMatch(matchRequestDto.getRequesterId(), matchRequestDto.getReceiverId());
    }

    @PostMapping("/{matchId}/respond")
    public void respondToMatch(@PathVariable Long matchId, @RequestParam boolean isAccepted) {
        matchServiceImpl.respondToMatch(matchId, isAccepted);
    }

    @GetMapping("/can-chat")
    public boolean canUsersChat(@RequestParam Long userId1, @RequestParam Long userId2) {
        return matchServiceImpl.canUsersChat(userId1, userId2);
    }
}


