package com.fis.is.terminy.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

public class CompanyScheduleHelper {

    private Long id;
    private String day;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime start_hour;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime end_hour;

    private String workplaceName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDay() {return day;}
    public void setDay(String day) {this.day = day;}

    public LocalTime getStart_hour() {return start_hour;}
    public void setStart_hour(LocalTime start_hour) {this.start_hour = start_hour;}

    public LocalTime getEnd_hour() {return  end_hour;}
    public void setEnd_hour(LocalTime end_hour) {this.end_hour = end_hour;}

    public String getWorkplaceName() {return this.workplaceName;}
    public void setCompany(String workplaceName) {this.workplaceName = workplaceName;}

}
