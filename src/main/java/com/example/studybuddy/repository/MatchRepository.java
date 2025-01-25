package com.example.studybuddy.repository;

import com.example.studybuddy.model.Match;
import com.example.studybuddy.model.MatchStatus;
import com.example.studybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {

    boolean existsByRequesterAndReceiver(User requester, User receiver);
    boolean existsByRequesterIdAndReceiverIdAndStatus(Long requesterId, Long receiverId, MatchStatus status);


}