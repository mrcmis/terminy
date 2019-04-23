package com.fis.is.terminy.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Company extends BaseEntity {
    @Column
    @NotBlank(message = "NB")
    private String phone;

    @NotBlank(message = "NB")
    @Email
    private String mail;

    @Column
    @NotBlank(message = "NB")
    private String name;

    @Column
    @NotBlank(message = "NB")
    private String codedName;

    @Column
    @NotNull
    private boolean mailNotification;

    @Column
    @NotNull
    private boolean reportsGeneration;

    @Column
    @NotNull
    private boolean blockingUsers;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodedName() {
        return codedName;
    }

    public void setCodedName(String codedName) {
        this.codedName = codedName;
    }

    public boolean isMailNotification() {
        return mailNotification;
    }

    public void setMailNotification(boolean mailNotification) {
        this.mailNotification = mailNotification;
    }

    public boolean isBlockingUsers() {
        return blockingUsers;
    }

    public void setBlockingUsers(boolean blockingUsers) {
        this.blockingUsers = blockingUsers;
    }

    public boolean isReportsGeneration() {
        return reportsGeneration;
    }

    public void setReportsGeneration(boolean reportsGeneration) {
        this.reportsGeneration = reportsGeneration;
    }
}
