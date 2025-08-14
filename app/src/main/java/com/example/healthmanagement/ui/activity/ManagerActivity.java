package com.example.healthmanagement.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmanagement.databinding.ActivityManagerBinding;

public class ManagerActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 使用视图绑定创建布局
        ActivityManagerBinding binding = ActivityManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // 设置活动的根布局

        // 设置饮食管理的点击事件
        binding.llDiet.setOnClickListener(v -> {
            // 启动 MenuManagerActivity
            ManagerActivity.this.startActivity(new Intent(ManagerActivity.this, MenuManagerActivity.class));
        });

        // 设置食物能量管理的点击事件
        binding.llFoodEnergy.setOnClickListener(v -> {
            // 启动 AddFoodEnergyActivity，并传递 "isManager" 参数
            ManagerActivity.this.startActivity(new Intent(ManagerActivity.this, AddFoodEnergyActivity.class).putExtra("isManager", true));
        });

        // 设置用户管理的点击事件
        binding.llPerson.setOnClickListener(v -> {
            // 启动 ManagerUserActivity
            ManagerActivity.this.startActivity(new Intent(ManagerActivity.this, ManagerUserActivity.class));
        });

        // 设置退出登录的点击事件
        binding.llOut.setOnClickListener(v -> {
            // 启动 LoginActivity，并结束当前活动
            ManagerActivity.this.startActivity(new Intent(ManagerActivity.this, LoginActivity.class));
            ManagerActivity.this.finish(); // 结束当前活动
        });
    }
    public void back(View view) {
        finish();
    }
}