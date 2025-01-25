package com.example.studybuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
@Data
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester; // User who initiated the match request

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // User who received the match request

    @Enumerated(EnumType.STRING)
    private MatchStatus status; // Match status (PENDING, ACCEPTED, REJECTED)


}
