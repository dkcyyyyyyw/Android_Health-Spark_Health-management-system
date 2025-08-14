package com.example.healthmanagement.bean;

import java.io.Serializable;

public class User implements Serializable {
    private String account;
    private String address;
    private Integer id;
    private String name;
    private String password;
    private String phone;
    private String photo;
    private String sex;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id2) {
        this.id = id2;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account2) {
        this.account = account2;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password2) {
        this.password = password2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex2) {
        this.sex = sex2;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone2) {
        this.phone = phone2;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address2) {
        this.address = address2;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo2) {
        this.photo = photo2;
    }

    public User(Integer id2, String account2, String password2, String name2, String sex2, String phone2, String address2, String photo2) {
        this.id = id2;
        this.account = account2;
        this.password = password2;
        this.name = name2;
        this.sex = sex2;
        this.phone = phone2;
        this.address = address2;
        this.photo = photo2;
    }
}
