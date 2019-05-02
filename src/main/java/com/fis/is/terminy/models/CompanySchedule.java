package com.fis.is.terminy.models;


import javax.persistence.*;
import java.time.LocalTime;

@Entity
public class CompanySchedule {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

    @Column
    private String day;

    @Column
    private LocalTime start_hour;

    @Column
    private LocalTime end_hour;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="company_id", nullable = false)
    private Company company;

    public String getDay() {return day;}
    public void setDay(String day) {this.day = day;}

    public LocalTime getStart_hour() {return start_hour;}
    public void setStart_hour(LocalTime start_hour) {this.start_hour = start_hour;}

    public LocalTime getEnd_hour() {return  end_hour;}
    public void setEnd_hour(LocalTime end_hour) {this.end_hour = end_hour;}

    public Company getCompany() {return company;}
    public void setCompany(Company company) {this.company = company;}



}
