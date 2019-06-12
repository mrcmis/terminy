package com.fis.is.terminy.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class CompanyReservationsHelper {
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime start_hour;

    private String serviceName;

    private String name;
    private String surname;

    private String phone;
    private String mail;

    public LocalDate getDate() {return this.date;}
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getServiceName() {return this.serviceName;}
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {return this.name;}
    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {return this.mail;}
    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getSurname() {return this.surname;}
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {return this.phone;}
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalTime getStart_hour() {return this.start_hour;}
    public void setStart_hour(LocalTime start_hour) {
        this.start_hour = start_hour;
    }

    public Long getId() {return this.id;}
    public void setId(Long id) {this.id = id;}
}
