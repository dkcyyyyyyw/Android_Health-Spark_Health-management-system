package com.example.healthmanagement.ui.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmanagement.R;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OpenActivity extends AppCompatActivity {

    private MySqliteOpenHelper helper;
    private Button in;
    private Boolean isFirst;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open); // 设置活动布局为打开界面

        helper = new MySqliteOpenHelper(this); // 初始化数据库帮助类
        in = findViewById(R.id.in); // 获取显示倒计时的 TextView

        // 从 SharedPreferences 中获取用户 ID 和是否首次启动标志
        userId = (Integer) SPUtils.get(this, SPUtils.USER_ID, 0);
        isFirst = (Boolean) SPUtils.get(this, SPUtils.IF_FIRST, true);

        // 创建倒计时器，设置为 5000 毫秒
        final CountDownTimer timer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                in.setText((millisUntilFinished / 1000) + "秒"); // 更新倒计时显示
            }

            public void onFinish() {
                finishView(); // 倒计时结束时调用 finishView 方法
            }
        };

        timer.start(); // 启动倒计时器

        in.setOnClickListener(v -> { // 设置点击事件，点击时取消倒计时
            timer.cancel();
            finishView(); // 直接调用 finishView 方法
        });

        if (isFirst) {
            initializeData(); // 如果是首次启动，初始化数据
        }
    }

    private void initializeData() {
        SQLiteDatabase db = helper.getWritableDatabase(); // 获取可写数据库
        SPUtils.put(this, SPUtils.IF_FIRST, false); // 设置首次启动标志为 false

        // 从 assets 文件夹加载 JSON 数据
        //JSON 数据先通过 loadJsonFromAssets 方法从 assets 文件夹读取出来，然后可能会利用 JSON 解析库进行解析，最终用于数据展示或者处理
        String rewardJson = loadJsonFromAssets("db.json");
        if (rewardJson != null) {
            try {
                JSONObject jsonObject = new JSONObject(rewardJson); // 解析 JSON 对象
                insertMenuData(db, jsonObject.getJSONArray("menu")); // 插入菜单数据
                insertFoodEnergyData(db, jsonObject.getJSONArray("food_energy")); // 插入食物能量数据
            } catch (JSONException e) {
                e.printStackTrace(); // 打印异常信息
            } finally {
                db.close(); // 关闭数据库连接
            }
        }
    }

    private String loadJsonFromAssets(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line); // 读取每一行并追加到 StringBuilder
            }
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
        }
        return stringBuilder.toString(); // 返回读取的 JSON 字符串
    }

    private void insertMenuData(SQLiteDatabase db, JSONArray menuList) throws JSONException {
        // 遍历菜单列表，插入每一项数据
        for (int i = 0; i < menuList.length(); i++) {
            JSONObject menuItem = menuList.getJSONObject(i);
            int typeId = menuItem.getInt("typeId");
            String title = menuItem.getString("title");
            String img = menuItem.getString("img");
            String url = menuItem.getString("url");

            // 插入菜单数据的 SQL 语句
            String insertMenuSql = "INSERT INTO menu(typeId, title, img, url) VALUES(?, ?, ?, ?)";
            db.execSQL(insertMenuSql, new Object[]{typeId, title, img, url}); // 执行插入操作
        }
    }

    private void insertFoodEnergyData(SQLiteDatabase db, JSONArray foodEnergyList) throws JSONException {
        // 遍历食物能量列表，插入每一项数据
        for (int i = 0; i < foodEnergyList.length(); i++) {
            JSONObject foodEnergyItem = foodEnergyList.getJSONObject(i);
            int typeId = foodEnergyItem.getInt("typeId");
            String food = foodEnergyItem.getString("food");
            String quantity = foodEnergyItem.getString("quantity");
            String heat = foodEnergyItem.getString("heat");
            double fat = foodEnergyItem.getDouble("fat");
            double protein = foodEnergyItem.getDouble("protein");
            double carbon = foodEnergyItem.getDouble("carbon");

            // 插入食物能量数据的 SQL 语句
            String insertFoodEnergySql = "INSERT INTO food_energy(typeId, food, quantity, heat, fat, protein, carbon) VALUES(?, ?, ?, ?, ?, ?, ?)";
            db.execSQL(insertFoodEnergySql, new Object[]{typeId, food, quantity, heat, fat, protein, carbon}); // 执行插入操作
        }
    }

    private void finishView() {
        // 根据用户 ID 判断跳转到不同的活动
        Intent intent = (userId > 0) ? new Intent(this, MainActivity.class) : new Intent(this, LoginActivity.class);
        startActivity(intent); // 启动目标活动
        finish(); // 关闭当前活动
    }

}
