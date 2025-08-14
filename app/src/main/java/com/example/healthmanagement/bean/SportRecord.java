package com.example.healthmanagement.bean;

import java.io.Serializable;

public class SportRecord implements Serializable {
    private String date;
    private Integer id;
    private String remark;
    private Integer step;
    private String time;
    private Integer userId;

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

    public Integer getStep() {
        return this.step;
    }

    public void setStep(Integer step2) {
        this.step = step2;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark2) {
        this.remark = remark2;
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

    public SportRecord(Integer id2, Integer userId2, Integer step2, String remark2, String date2, String time2) {
        this.id = id2;
        this.userId = userId2;
        this.step = step2;
        this.remark = remark2;
        this.date = date2;
        this.time = time2;
    }
}
