package com.example.healthmanagement.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "healthmanagement.db";
    public static final int DB_VERSION = 1;
    private Context context;

    public MySqliteOpenHelper(Context context2) {
        super(context2, DB_NAME, (SQLiteDatabase.CursorFactory) null, 1);
        this.context = context2;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user(id integer primary key autoincrement,account, password,name,sex, phone,address,photo)");
        db.execSQL("create table menu(id integer primary key autoincrement,typeId, title,img,url)");
        db.execSQL("create table food_energy(id integer primary key autoincrement,typeId, food,quantity,heat,fat,protein,carbon,count integer default 0)");
        db.execSQL("create table weight_record(id integer primary key autoincrement,userId,weight,date, time)");
        db.execSQL("create table eat_record(id integer primary key autoincrement,userId,timeId,date)");
        db.execSQL("create table eat_record_details(id integer primary key autoincrement,eatRecordId,foodEnergyId)");
        db.execSQL("create table health_record(id integer primary key autoincrement,userId,kcal,weight,heartRate,date,time)");
        db.execSQL("create table sport_record(id integer primary key autoincrement,userId,step,remark,date,time)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}