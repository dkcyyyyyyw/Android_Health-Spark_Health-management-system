package com.example.healthmanagement.bean;

import java.io.Serializable;

public class HealthRecord implements Serializable {
    private String date;
    private Float heartRate;
    private Integer id;
    private Float kcal;
    private String time;
    private Integer userId;
    private Float weight;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id2) {
        this.id = id2;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId2) {
        this.userId = userId2;
    }

    public Float getKcal() {
        return this.kcal;
    }

    public void setKcal(Float kcal2) {
        this.kcal = kcal2;
    }

    public Float getWeight() {
        return this.weight;
    }

    public void setWeight(Float weight2) {
        this.weight = weight2;
    }

    public Float getHeartRate() {
        return this.heartRate;
    }

    public void setHeartRate(Float heartRate2) {
        this.heartRate = heartRate2;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date2) {
        this.date = date2;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time2) {
        this.time = time2;
    }

    public HealthRecord(Integer id2, Integer userId2, Float kcal2, Float weight2, Float heartRate2, String date2, String time2) {
        this.id = id2;
        this.userId = userId2;
        this.kcal = kcal2;
        this.weight = weight2;
        this.heartRate = heartRate2;
        this.date = date2;
        this.time = time2;
    }
}
