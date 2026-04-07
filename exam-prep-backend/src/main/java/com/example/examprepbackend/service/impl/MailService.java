package com.example.examprepbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    @Async
    public void sendEmail(String toEmail, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ngoquangtruongx4@gmail.com"); // email gửi đi
        message.setTo(toEmail);            // email người nhận
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }
}





