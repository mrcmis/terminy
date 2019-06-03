package com.fis.is.terminy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("emailServ")
public class EmailServImpl implements EmailServ {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendEmail(SimpleMailMessage email){
        mailSender.send(email);
    }
}
