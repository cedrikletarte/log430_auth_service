package com.brokerx.auth_service.application.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Asynchronously sends an OTP verification code to the specified email address.
     */
    @Async
    public void sendOtp(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your BrokerX verification code");
        message.setText("Your verification code is: " + code + "\nThis code expires in 10 minutes.");
        mailSender.send(message);
    }
}
