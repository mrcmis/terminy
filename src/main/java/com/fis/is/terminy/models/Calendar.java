package com.fis.is.terminy.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public class Calendar {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime start_hour;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime end_hour;
    private Long workplaceId;

    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}

    public void setStart_hour(LocalTime start_hour) {this.start_hour = start_hour;}
    public LocalTime getStart_hour() {return  start_hour;}

    public void  setEnd_hour(LocalTime end_hour) {this.end_hour = end_hour;}
    public LocalTime getEnd_hour() {return  end_hour;}

    public Long getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(Long workplaceId) {
        this.workplaceId = workplaceId;
    }

    public String getDayName()
    {
        String dayName = date.getDayOfWeek().name();
        switch (dayName)
        {
            case "MONDAY":
                return "Poniedziałek";
            case "TUESDAY":
                return "Wtorek";
            case "WEDNESDAY":
                return "Środa";
            case "THURSDAY":
                return "Czwartek";
            case "FRIDAY":
                return "Piątek";
            case "SATURDAY":
                return "Sobota";
        }
        return "Niedziela";
    }
}
