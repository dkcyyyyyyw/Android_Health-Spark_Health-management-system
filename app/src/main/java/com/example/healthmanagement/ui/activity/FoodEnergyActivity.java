package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmanagement.R;
import com.example.healthmanagement.adapter.FoodEnergyAdapter;
import com.example.healthmanagement.bean.FoodEnergy;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;

public class FoodEnergyActivity extends AppCompatActivity {
    private FoodEnergyAdapter foodEnergyAdapter;
    private List<FoodEnergy> foodEnergyList;
    MySqliteOpenHelper helper = null;
    private LinearLayout llEmpty;
    private Activity myActivity;
    private RecyclerView rvList;
    private String[] state = {"0", DiskLruCache.VERSION_1, ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4", "5"};
    private TabLayout tabTitle;
    private String[] title = {"五谷类", "肉类", "蛋类", "蔬果类", "海鲜类", "奶类"};
    private String typeId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_energy); // 设置布局
        myActivity = this; // 初始化当前活动
        helper = new MySqliteOpenHelper(this); // 初始化数据库帮助类
        tabTitle = findViewById(R.id.tab_title); // 获取标签标题
        rvList = findViewById(R.id.rv_list); // 获取列表视图
        llEmpty = findViewById(R.id.ll_empty); // 获取空视图
        initView(); // 初始化视图
    }

    // 初始化视图
    private void initView() {
        tabTitle.setTabMode(TabLayout.MODE_FIXED); // 设置标签模式为固定模式
        for (String s : title) {
            tabTitle.addTab(tabTitle.newTab().setText(s)); // 添加标签
        }

        // 设置列表的布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(myActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 设置为垂直方向
        rvList.setLayoutManager(layoutManager); // 应用布局管理器

        foodEnergyAdapter = new FoodEnergyAdapter(); // 初始化适配器
        rvList.setAdapter(foodEnergyAdapter); // 设置适配器
        loadData(); // 加载数据

        // 设置标签选择监听
        tabTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                typeId = state[tab.getPosition()]; // 获取选中标签的ID
                loadData(); // 根据选中标签加载数据
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 标签未选中时的逻辑（可选）
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 标签重新选中时的逻辑（可选）
            }
        });
    }

    // 加载数据
    private void loadData() {
        foodEnergyList = new ArrayList<>(); // 初始化食物能量列表
        Cursor cursor = helper.getWritableDatabase().rawQuery("select * from food_energy where typeId =" + typeId, null); // 查询数据库
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                // 创建食物能量对象并添加到列表
                foodEnergyList.add(new FoodEnergy(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getDouble(6),
                        cursor.getDouble(7)
                ));
            }
        }
        Collections.reverse(foodEnergyList); // 反转列表顺序
        // 判断列表是否为空
        if (foodEnergyList == null || foodEnergyList.size() <= 0) {
            rvList.setVisibility(View.GONE); // 隐藏列表
            llEmpty.setVisibility(View.VISIBLE); // 显示空视图
            return;
        }
        rvList.setVisibility(View.VISIBLE); // 显示列表
        llEmpty.setVisibility(View.GONE); // 隐藏空视图
        foodEnergyAdapter.addItem(foodEnergyList); // 更新适配器数据
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // 在活动恢复时加载数据
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadData(); // 如果返回的数据请求成功，重新加载数据
        }
    }

    // 返回按钮事件
    public void back(View view) {
        finish(); // 关闭当前活动
    }
}