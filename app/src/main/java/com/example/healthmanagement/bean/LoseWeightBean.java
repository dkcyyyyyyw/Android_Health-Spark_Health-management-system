package com.example.healthmanagement.bean;

public class LoseWeightBean {
    private String author_name;
    private String category;
    private String date;
    private String is_content;
    private String thumbnail_pic_s;
    private String title;
    private String uniquekey;
    private String url;

    public String getUniquekey() {
        return this.uniquekey;
    }

    public void setUniquekey(String uniquekey2) {
        this.uniquekey = uniquekey2;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title2) {
        this.title = title2;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date2) {
        this.date = date2;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category2) {
        this.category = category2;
    }

    public String getAuthor_name() {
        return this.author_name;
    }

    public void setAuthor_name(String author_name2) {
        this.author_name = author_name2;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url2) {
        this.url = url2;
    }

    public String getThumbnail_pic_s() {
        return this.thumbnail_pic_s;
    }

    public void setThumbnail_pic_s(String thumbnail_pic_s2) {
        this.thumbnail_pic_s = thumbnail_pic_s2;
    }

    public String getIs_content() {
        return this.is_content;
    }

    public void setIs_content(String is_content2) {
        this.is_content = is_content2;
    }
}
