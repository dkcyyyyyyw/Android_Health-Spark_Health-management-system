package com.example.healthmanagement.bean;

import android.content.ContentValues;
import java.io.Serializable;

public class Menu implements Serializable {
    private Integer id;
    private String img;
    private String title;
    private Integer typeId;
    private String url;

    public Menu() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id2) {
        this.id = id2;
    }

    public Integer getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Integer typeId2) {
        this.typeId = typeId2;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title2) {
        this.title = title2;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img2) {
        this.img = img2;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url2) {
        this.url = url2;
    }

    public Menu(Integer id2, Integer typeId2, String title2, String img2, String url2) {
        this.id = id2;
        this.typeId = typeId2;
        this.title = title2;
        this.img = img2;
        this.url = url2;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("typeId", this.typeId);
        contentValues.put("title", this.title);
        contentValues.put("img", this.img);
        contentValues.put("url", this.url);
        return contentValues;
    }
}
