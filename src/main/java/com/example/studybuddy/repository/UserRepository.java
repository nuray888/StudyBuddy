package com.example.studybuddy.repository;

import com.example.studybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String userName);

    User findUserNameById(Long recipientId);
}
