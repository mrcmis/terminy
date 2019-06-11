package com.fis.is.terminy.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationUnit {

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime start_hour;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime end_hour;
    private int id;

    public void  setId(int id) {this.id = id;}
    public int getId() {return id;}

    public LocalTime getStart_hour() {return start_hour;}
    public void setStart_hour(LocalTime start_hour) {this.start_hour = start_hour;}

    public LocalTime getEnd_hour() {return end_hour;}
    public void  setEnd_hour(LocalTime end_hour) {this.end_hour = end_hour;}

    public ReservationUnit() {}
    public ReservationUnit(int id, LocalTime start_hour, LocalTime end_hour)
    {
        this.id = id;
        this.start_hour = start_hour;
        this.end_hour = end_hour;
    }
}
