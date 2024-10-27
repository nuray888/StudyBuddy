package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.dto.request.UserRequest;
import com.example.studybuddy.dto.response.UserResponse;
import com.example.studybuddy.exception.APIException;
import com.example.studybuddy.exception.ResourceNotFoundException;
import com.example.studybuddy.model.User;
import com.example.studybuddy.repository.UserRepository;
import com.example.studybuddy.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor

public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getAll() {
        List<User> all = repository.findAll();
        if (all.isEmpty())
            throw new APIException("There is no user");
        List<UserResponse> list = all.stream()
                .map(s -> modelMapper.map(s, UserResponse.class))
                .toList();
        return list;
    }

    @Override
    public String create(UserRequest userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        User userFromDB=userRepository.findByUserName(user.getUserName());
        if(userFromDB!=null)
            throw new APIException("User with "+user.getUserName()+" has already existed");
        repository.save(user);
        return "Successful";
    }

    @Override
    public UserResponse findByID(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with " + id));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse update(Long id, UserRequest userRequest) {
        User user = repository.findById(id).orElseThrow();
        modelMapper.map(userRequest, user);
        User updatedUser = repository.save(user);
        return modelMapper.map(updatedUser, UserResponse.class);
    }

    @Override
    public String delete(Long id) {
        User user = repository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found with "+id));
        repository.delete(user);
        return "Successfully deleted";
    }

    @Override
    public void updateLastSeen(User user) {
        user.setLastSeen(LocalDate.from(LocalDateTime.now()));
        repository.save(user);
    }

}
