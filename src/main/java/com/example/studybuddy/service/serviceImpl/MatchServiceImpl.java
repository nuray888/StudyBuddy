package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.dto.request.MatchRequestDto;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MatchRepository matchRepository;
    private final EmailServiceImpl emailServiceImpl;
//    private final CurrencyValidatorForMonetaryAmount currencyValidatorForMonetaryAmount;

//    public List<MatchResponseDto> findMatches() {
//        User user=getCurrentUser();
//        Long userId=user.getId();
//
//        List<Post> userPosts = user.getPosts();
//        List<MatchResponseDto> matches = new ArrayList<>();
//
//        for (Post post : userPosts) {
//            List<Post> matchingPosts = postRepository.findByTopicAndSubTopic(post.getTopic().toLowerCase(), post.getSubTopic().toLowerCase());
//
//            for (Post matchPost : matchingPosts) {
//                if (!matchPost.getUser().getId().equals(userId)) {
//                    User matchedUser = matchPost.getUser();
//                    matches.add(new MatchResponseDto(
//                            matchedUser.getId(),
//                            matchedUser.getUserName(),
//                            matchedUser.getEmail()
//                    ));
//                }
//            }
//        }
//        return matches;
//    }
public List<MatchResponseDto> findMatches() {
    User user = getCurrentUser();
    Long userId = user.getId();

    List<Post> userPosts = user.getPosts();
    List<MatchResponseDto> matches = new ArrayList<>();
    Set<Long> addedUserIds = new HashSet<>();

    for (Post post : userPosts) {
        List<Post> matchingPosts = postRepository.findByTopicAndSubTopic(
                post.getTopic().toLowerCase(),
                post.getSubTopic().toLowerCase()
        );

        for (Post matchPost : matchingPosts) {
            User matchedUser = matchPost.getUser();
            Long matchedUserId = matchedUser.getId();

            if (!matchedUserId.equals(userId) && addedUserIds.add(matchedUserId)) {
                matches.add(new MatchResponseDto(
                        matchedUserId,
                        matchedUser.getUserName(),
                        matchedUser.getEmail()
                ));
            }
        }
    }
    return matches;
}


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MatchResponseDto requestMatch(MatchRequestDto matchRequestDto) {
        Long receiverId=matchRequestDto.getReceiverId();
        User user=getCurrentUser();
        Long requesterId=user.getId();
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

        emailServiceImpl.sendMatchNotification(receiver.getEmail(), requester.getUserName(),false);
        return new MatchResponseDto(receiver.getId(), receiver.getUserName(), receiver.getEmail());
    }

//    @Transactional
//    public void respondToMatch(Long matchId, boolean isAccepted) {
//        Match match = matchRepository.findById(matchId)
//                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id " + matchId));
//
//        match.setStatus(isAccepted ? MatchStatus.ACCEPTED : MatchStatus.REJECTED);
//        matchRepository.save(match);
//    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MatchResponseDto respondToMatch(Long matchId, boolean accept) {
        User user = getCurrentUser();
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id " + matchId));

        if (!match.getReceiver().getId().equals(user.getId())) {
            throw new IllegalStateException("You are not authorized to respond to this match request");
        }


        match.setStatus(accept ? MatchStatus.ACCEPTED : MatchStatus.REJECTED);
        matchRepository.save(match);


        emailServiceImpl.sendMatchNotification(match.getRequester().getEmail(), user.getUserName(), accept);

        return new MatchResponseDto(match.getReceiver().getId(), match.getReceiver().getUserName(), match.getReceiver().getEmail());
    }


//    @Transactional(isolation = Isolation.SERIALIZABLE)
//    public void respondToMatch(Long matchId, boolean accept) {
//        User user = getCurrentUser();
//        Match match = matchRepository.findById(matchId)
//                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id " + matchId));
//
//        // Alıcı olup olmadığını kontrol et
//        if (!match.getReceiver().getId().equals(user.getId())) {
//            throw new IllegalStateException("You are not authorized to respond to this match request");
//        }
//
//        // Durumu güncelle
//        match.setStatus(accept ? MatchStatus.ACCEPTED : MatchStatus.REJECTED);
//        matchRepository.save(match);
//
//        // Yanıt e-posta bildirimi
//        emailServiceImpl.sendMatchNotification(match.getRequester().getEmail(), user.getUserName(), accept);
//
//        new MatchResponseDto(match.getReceiver().getId(), match.getReceiver().getUserName(), match.getReceiver().getEmail());
//    }



    public boolean canUsersChat(Long userId1, Long userId2) {
        return matchRepository.existsByRequesterIdAndReceiverIdAndStatus(userId1, userId2, MatchStatus.ACCEPTED) ||
                matchRepository.existsByRequesterIdAndReceiverIdAndStatus(userId2, userId1, MatchStatus.ACCEPTED);
    }

    @Override
    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


}
