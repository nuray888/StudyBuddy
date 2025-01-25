package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.dto.response.MatchResponseDto;
import com.example.studybuddy.exception.ResourceNotFoundException;
import com.example.studybuddy.model.Match;
import com.example.studybuddy.model.MatchStatus;
import com.example.studybuddy.model.Post;
import com.example.studybuddy.model.User;
import com.example.studybuddy.repository.MatchRepository;
import com.example.studybuddy.repository.PostRepository;
import com.example.studybuddy.repository.UserRepository;
import com.example.studybuddy.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MatchRepository matchRepository;
    private final EmailServiceImpl emailServiceImpl;

    public List<MatchResponseDto> findMatches(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        List<Post> userPosts = user.getPosts();
        List<MatchResponseDto> matches = new ArrayList<>();

        for (Post post : userPosts) {
            List<Post> matchingPosts = postRepository.findByTopicAndSubTopic(post.getTopic(), post.getSubTopic());

            for (Post matchPost : matchingPosts) {
                if (!matchPost.getUser().getId().equals(userId)) {
                    User matchedUser = matchPost.getUser();
                    matches.add(new MatchResponseDto(
                            matchedUser.getId(),
                            matchedUser.getUserName(),
                            matchedUser.getEmail()
                    ));
                }
            }
        }
        return matches;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MatchResponseDto requestMatch(Long requesterId, Long receiverId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + requesterId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + receiverId));

        if (matchRepository.existsByRequesterAndReceiver(requester, receiver)) {
            throw new IllegalStateException("Match proposal already exists between these users");
        }

        Match match = new Match();
        match.setRequester(requester);
        match.setReceiver(receiver);
        match.setStatus(MatchStatus.PENDING);
        matchRepository.save(match);

        emailServiceImpl.sendMatchNotification(receiver.getEmail(), requester.getUserName());
        return new MatchResponseDto(receiver.getId(), receiver.getUserName(), receiver.getEmail());
    }

    @Transactional
    public void respondToMatch(Long matchId, boolean isAccepted) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id " + matchId));

        match.setStatus(isAccepted ? MatchStatus.ACCEPTED : MatchStatus.REJECTED);
        matchRepository.save(match);
    }


    public boolean canUsersChat(Long userId1, Long userId2) {
        return matchRepository.existsByRequesterIdAndReceiverIdAndStatus(userId1, userId2, MatchStatus.ACCEPTED) ||
                matchRepository.existsByRequesterIdAndReceiverIdAndStatus(userId2, userId1, MatchStatus.ACCEPTED);
    }

}
