package com.example.studybuddy.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendMatchNotification(String to, String matchedUserName);
}
