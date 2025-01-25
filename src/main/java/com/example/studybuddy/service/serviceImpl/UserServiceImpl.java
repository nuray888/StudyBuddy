package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.dto.ExceptionDTO;
import com.example.studybuddy.dto.request.LoginRequestDto;
import com.example.studybuddy.dto.request.UserRequestDto;
import com.example.studybuddy.dto.request.UserUpdateRequestDto;
import com.example.studybuddy.dto.response.LoginResponseDto;
import com.example.studybuddy.dto.response.UserResponseDto;
import com.example.studybuddy.exception.APIException;
import com.example.studybuddy.exception.ResourceNotFoundException;
import com.example.studybuddy.model.Authority;
import com.example.studybuddy.model.User;
import com.example.studybuddy.repository.PostRepository;
import com.example.studybuddy.repository.UserRepository;
import com.example.studybuddy.service.UserService;
import com.example.studybuddy.utility.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j

public class UserServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<UserResponseDto> getAll() {
        List<User> all = userRepository.findAll();
        if (all.isEmpty())
            throw new APIException("There is no user");
        List<UserResponseDto> list = all.stream()
                .map(s -> modelMapper.map(s, UserResponseDto.class))
                .toList();
        return list;
    }

    @Override
    public String create(UserRequestDto userRequestDto) {
        if (userRepository.findByUserName(userRequestDto.getUserName()).isPresent()) {
            throw new ResourceNotFoundException("User already exists");
        }
        User user = modelMapper.map(userRequestDto, User.class);

        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));


        Authority authority = new Authority("USER");
        Set<Authority> authoritySet = Set.of(authority);
        user.setAuthorities(authoritySet);

//        if (user != null)
//            throw new APIException("User with " + user.getUserName() + " has already existed");
        userRepository.save(user);
        return "Successfully";
    }

    @Override
    public UserResponseDto findByID(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with " + id));
        return modelMapper.map(user, UserResponseDto.class);
    }

    @Override
    public UserResponseDto update(UserUpdateRequestDto userUpdateRequestDto) {
        User currentUser = getCurrentUser();

        // Update only the email if provided
        if (userUpdateRequestDto.getEmail() != null && !userUpdateRequestDto.getEmail().isEmpty()) {
            currentUser.setEmail(userUpdateRequestDto.getEmail());
        } else {
            throw new APIException("Email cannot be null or empty");
        }

        // Save the updated user
        userRepository.save(currentUser);

        // Return the updated user as a DTO
        return modelMapper.map(currentUser, UserResponseDto.class);
    }

    @Override
    public UserRequestDto delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with " + id));
        userRepository.delete(user);
        return modelMapper.map(user, UserRequestDto.class);
    }

    @Override
    public void updateLastSeen(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
//        user.setLastSeen(LocalDate.from(LocalDateTime.now()));
        userRepository.save(user);
    }

//    @Override
//    public List<MatchResponse> findMatches(Long id) {
//        findMatches(id);
//    }


    @Override
    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

}
