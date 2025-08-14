package com.example.healthmanagement.enums;

public enum TimeBucketEnum {  //定义了一个公共枚举类，枚举类型是一种特殊的类，用于表示固定的一组常量。
    Breakfast("早餐", "500", 0),//定义早餐枚举项，包含三个参数：描述（desc）、提示信息（tips）、编码（code）。
    Lunch("午餐", "600", 1),
    Dinner("晚餐", "450", 2),
    Snacks("加餐", "400", 3);
    
    private int code;//用于标识枚举项的唯一编码（如早餐对应 0，午餐对应 1）
    private String desc;//枚举项的描述（如 “早餐”“午餐”）。
    private String tips;//枚举项的提示信息（如 “500” 可能代表建议热量摄入值）。

    private TimeBucketEnum(String desc2, String tips2, Integer code2) {
        this.desc = desc2;
        this.tips = tips2;
        this.code = code2.intValue();
    }

    public static String getName(Integer code2) {
        TimeBucketEnum[] values = values();
        for (TimeBucketEnum c : values) {
            if (c.getCode() == code2.intValue()) {
                return c.desc;
            }
        }
        return null;
    }

    public static String getTips(Integer code2) {
        TimeBucketEnum[] values = values();
        for (TimeBucketEnum c : values) {
            if (c.getCode() == code2.intValue()) {
                return c.tips;
            }
        }
        return null;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc2) {
        this.desc = desc2;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code2) {
        this.code = code2;
    }

    public String getTips() {
        return this.tips;
    }

    public void setTips(String tips2) {
        this.tips = tips2;
    }
}
