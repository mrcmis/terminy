package com.fis.is.terminy.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.*;

@Entity
public class Reservations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="service_id", nullable = false)
    private CompanyService service;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Column
    private LocalTime start_hour = LocalTime.now();

    @Column
    private LocalTime end_hour = LocalTime.now();

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Company getCompany() {return company;}
    public void setCompany(Company company) {this.company = company;}

    public Client getClient() {return client;}
    public void setClient(Client client) {this.client = client;}

    public CompanyService getService() {return service;}
    public void setService(CompanyService service) {this.service = service;}

    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}

    public LocalTime getStart_hour() {return start_hour;}
    public void setStart_hour(LocalTime start_hour) {this.start_hour = start_hour;}

    public LocalTime getEnd_hour() {return  end_hour;}
    public void setEnd_hour(LocalTime end_hour) {this.end_hour = end_hour;}

}
