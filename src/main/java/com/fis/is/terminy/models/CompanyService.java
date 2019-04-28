package com.fis.is.terminy.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Time;

@Entity
public class CompanyService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(unique = true)
    @NotBlank(message = "NB")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "NB")
    private Long price;

    @Column(unique = true)
    @NotBlank(message = "NB")
    private Time duration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
