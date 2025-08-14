package com.example.healthmanagement.bean;

import android.content.ContentValues;
import com.luck.picture.lib.config.PictureConfig;
import java.io.Serializable;

public class FoodEnergy implements Serializable {
    private Double carbon;
    private int count;
    private Double fat;
    private String food;
    private String heat;
    private Integer id;
    private Double protein;
    private String quantity;
    private Integer typeId;

    public int getCount() {
        return this.count;
    }

    public void setCount(int count2) {
        this.count = count2;
    }

    public FoodEnergy() {
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

    public String getFood() {
        return this.food;
    }

    public void setFood(String food2) {
        this.food = food2;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public void setQuantity(String quantity2) {
        this.quantity = quantity2;
    }

    public String getHeat() {
        return this.heat;
    }

    public void setHeat(String heat2) {
        this.heat = heat2;
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

    public FoodEnergy(Integer id2, Integer typeId2, String food2, String quantity2, String heat2, Double fat2, Double protein2, Double carbon2) {
        this.id = id2;
        this.typeId = typeId2;
        this.food = food2;
        this.quantity = quantity2;
        this.heat = heat2;
        this.fat = fat2;
        this.protein = protein2;
        this.carbon = carbon2;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("food", this.food);
        contentValues.put("quantity", this.quantity);
        contentValues.put("heat", this.heat);
        contentValues.put("fat", this.fat);
        contentValues.put("protein", this.protein);
        contentValues.put("carbon", this.carbon);
        contentValues.put("typeId", this.typeId);
        contentValues.put(PictureConfig.EXTRA_DATA_COUNT, Integer.valueOf(this.count));
        return contentValues;
    }
}
