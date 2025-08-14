package com.example.healthmanagement.bean;

public class BMIResult {
    private double bmi;
    private String danger;
    private double idealWeight;
    private int level;
    private String levelMsg;
    private String normalBMI;
    private String normalWeight;

    public void setIdealWeight(double idealWeight2) {
        this.idealWeight = idealWeight2;
    }

    public double getIdealWeight() {
        return this.idealWeight;
    }

    public void setNormalWeight(String normalWeight2) {
        this.normalWeight = normalWeight2;
    }

    public String getNormalWeight() {
        return this.normalWeight;
    }

    public void setLevel(int level2) {
        this.level = level2;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevelMsg(String levelMsg2) {
        this.levelMsg = levelMsg2;
    }

    public String getLevelMsg() {
        return this.levelMsg;
    }

    public void setDanger(String danger2) {
        this.danger = danger2;
    }

    public String getDanger() {
        return this.danger;
    }

    public void setBmi(double bmi2) {
        this.bmi = bmi2;
    }

    public double getBmi() {
        return this.bmi;
    }

    public void setNormalBMI(String normalBMI2) {
        this.normalBMI = normalBMI2;
    }

    public String getNormalBMI() {
        return this.normalBMI;
    }
}
