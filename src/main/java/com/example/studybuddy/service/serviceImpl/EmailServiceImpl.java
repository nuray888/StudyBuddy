package com.example.studybuddy.service.serviceImpl;

import com.example.studybuddy.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class EmailServiceImpl implements EmailService {
@Value("${mail}")
    private String from;
    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

//    public void sendMatchNotification(String to, String matchedUserName){
//        SimpleMailMessage message=new SimpleMailMessage();
//        message.setFrom(from);
//        message.setTo(to);
//        message.setSubject("You have a new request!!");
//        message.setText("You have one request from "+matchedUserName);
//        javaMailSender.send(message);
//
//    }
//    public void respondMatchNotification(String to, String matchedUserName){
//        SimpleMailMessage message=new SimpleMailMessage();
//        message.setFrom(from);
//        message.setTo(to);
//        message.setSubject("Respond to the request!!");
//        message.setText("You have one request from "+matchedUserName);
//        javaMailSender.send(message);;
//    }

    public void sendMatchNotification(String to, String matchedUserName, boolean isResponse) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        if (isResponse) {
            message.setSubject("Respond to the request!!");
            message.setText("You have one response from " + matchedUserName);
        } else {

            message.setSubject("You have a new request!!");
            message.setText("You have one request from " + matchedUserName);
        }
        javaMailSender.send(message);
    }


}
