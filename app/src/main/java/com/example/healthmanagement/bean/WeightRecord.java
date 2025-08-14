package com.example.healthmanagement.bean;

import java.io.Serializable;

public class WeightRecord implements Serializable {
    private String date;
    private Integer id;
    private String time;
    private Integer userId;
    private String weight;

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

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weight2) {
        this.weight = weight2;
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

    public WeightRecord(Integer id2, Integer userId2, String weight2, String date2, String time2) {
        this.id = id2;
        this.userId = userId2;
        this.weight = weight2;
        this.date = date2;
        this.time = time2;
    }
}
