package com.fis.is.terminy.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void send(String receiverMail, EmailContent emailContent) {
        SimpleMailMessage message = new SimpleMailMessage();

        String sender = "Terminy";
        message.setFrom(sender);
        message.setTo(receiverMail);
        message.setSubject(emailContent.getSubject());
        message.setText(emailContent.getText());
        emailSender.send(message);
    }
}
