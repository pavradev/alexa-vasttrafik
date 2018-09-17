package com.github.pavradev.alexaskills.vasttrafik.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Departure {
    String name;

    String sname;

    String type;

    @JsonFormat(pattern = "HH:mm")
    LocalTime time;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    LocalTime rtTime; //the actual time

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate rtDate; //the actual date

    String direction;

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public LocalTime getRtTime() {
        return rtTime;
    }

    public void setRtTime(LocalTime rtTime) {
        this.rtTime = rtTime;
    }

    public LocalDate getRtDate() {
        return rtDate;
    }

    public void setRtDate(LocalDate rtDate) {
        this.rtDate = rtDate;
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(this.getDate(), this.getTime());
    }

    public LocalDateTime getRtDateTime() {
        return LocalDateTime.of(this.getRtDate(), this.getRtTime());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Departure{" +
                "name='" + name + '\'' +
                ", sname='" + sname + '\'' +
                ", type='" + type + '\'' +
                ", time=" + time +
                ", date=" + date +
                ", rtTime=" + rtTime +
                ", rtDate=" + rtDate +
                ", direction='" + direction + '\'' +
                '}';
    }
}
