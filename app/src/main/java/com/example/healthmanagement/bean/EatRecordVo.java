package com.example.healthmanagement.bean;

import java.io.Serializable;

public class EatRecordVo implements Serializable {
    private Double carbon;
    private String date;
    private Double fat;
    private Integer id;
    private Double kcal;
    private Double protein;
    private Integer timeId;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id2) {
        this.id = id2;
    }

    public Integer getTimeId() {
        return this.timeId;
    }

    public void setTimeId(Integer timeId2) {
        this.timeId = timeId2;
    }

    public Double getKcal() {
        return this.kcal;
    }

    public void setKcal(Double kcal2) {
        this.kcal = kcal2;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date2) {
        this.date = date2;
    }

    public Double getFat() {
        return this.fat;
    }

    public void setFat(Double fat2) {
        this.fat = fat2;
    }

    public Double getProtein() {
        return this.protein;
    }

    public void setProtein(Double protein2) {
        this.protein = protein2;
    }

    public Double getCarbon() {
        return this.carbon;
    }

    public void setCarbon(Double carbon2) {
        this.carbon = carbon2;
    }

    public EatRecordVo(Integer id2, Integer timeId2, Double kcal2, Double fat2, Double protein2, Double carbon2, String date2) {
        this.id = id2;
        this.timeId = timeId2;
        this.kcal = kcal2;
        this.fat = fat2;
        this.protein = protein2;
        this.carbon = carbon2;
        this.date = date2;
    }
}
