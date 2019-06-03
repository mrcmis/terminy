package com.fis.is.terminy.services;

import org.springframework.mail.SimpleMailMessage;

public interface EmailServ {
    void sendEmail(SimpleMailMessage email);
}
