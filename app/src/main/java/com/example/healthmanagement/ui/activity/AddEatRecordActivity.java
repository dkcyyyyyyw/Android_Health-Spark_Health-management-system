package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmanagement.R;
import com.example.healthmanagement.adapter.AddFoodEnergyAdapter;
import com.example.healthmanagement.bean.FoodEnergy;
import com.example.healthmanagement.enums.TimeBucketEnum;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AddEatRecordActivity extends AppCompatActivity {
    private String date = ""; // 日期
    private Integer eatRecordId; // 饮食记录ID
    private AddFoodEnergyAdapter foodEnergyAdapter; // 食物能量适配器
    private List<FoodEnergy> foodEnergyList = new ArrayList<>(); // 食物能量列表
    MySqliteOpenHelper helper = null; // 数据库助手
    private LinearLayout llEmpty; // 空数据布局
    private Activity myActivity; // 当前活动
    private RecyclerView rvList; // RecyclerView用于显示食物能量
    private String[] state = {"0", "1", "2", "3"}; // 状态数组，代表不同餐点
    private TabLayout tabTitle; // 标签布局
    private String timeId = "0"; // 时间ID
    private String[] title = {"早餐", "午餐", "晚餐", "加餐"}; // 餐点标题
    private TextView tvTips; // 提示文本
    private TextView tvTotal; // 总摄入量文本
    private Integer userId; // 用户ID

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_record_add); // 设置布局
        this.myActivity = this;
        this.helper = new MySqliteOpenHelper(this); // 初始化数据库助手
        this.tabTitle = findViewById(R.id.tab_title); // 获取标签布局
        this.rvList = findViewById(R.id.rv_list); // 获取RecyclerView
        this.tvTips = findViewById(R.id.tv_tips); // 获取提示文本
        this.tvTotal = findViewById(R.id.tv_total); // 获取总摄入量文本
        this.llEmpty = findViewById(R.id.ll_empty); // 获取空数据布局
        this.userId = (Integer) SPUtils.get(this, SPUtils.USER_ID, 0); // 获取用户ID
        String valueOf = String.valueOf(getIntent().getIntExtra("timeId", 0)); // 获取时间ID
        this.timeId = valueOf;
        this.tvTips.setText(String.format("建议摄入%s大卡", TimeBucketEnum.getTips(Integer.valueOf(valueOf)))); // 设置提示文本
        this.eatRecordId = Integer.valueOf(getIntent().getIntExtra("eatRecordId", 0)); // 获取饮食记录ID
        this.date = getIntent().getStringExtra("date"); // 获取日期
        initView(); // 初始化视图
    }

    private void initView() {
        this.tabTitle.setTabMode(TabLayout.MODE_FIXED); // 设置标签模式
        for (String tabTitle : this.title) {
            this.tabTitle.addTab(this.tabTitle.newTab().setText(tabTitle)); // 添加标签
        }
        this.tabTitle.getTabAt(Integer.parseInt(this.timeId)).select(); // 选择当前时间标签
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.myActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 设置布局为垂直
        this.rvList.setLayoutManager(layoutManager); // 设置RecyclerView的布局管理器
        this.foodEnergyAdapter = new AddFoodEnergyAdapter(1); // 初始化适配器
        this.rvList.setAdapter(foodEnergyAdapter); // 设置适配器
        loadData(); // 加载数据
        this.tabTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                AddEatRecordActivity.this.timeId = AddEatRecordActivity.this.state[tab.getPosition()]; // 更新时间ID
                AddEatRecordActivity.this.tvTips.setText(String.format("建议摄入%s大卡", TimeBucketEnum.getTips(Integer.valueOf(AddEatRecordActivity.this.timeId)))); // 更新提示文本
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        this.foodEnergyAdapter.setItemListener(new AddFoodEnergyAdapter.ItemListener() {
            @Override
            public void ItemClick(FoodEnergy foodEnergy) {
                AddEatRecordActivity.this.foodEnergyList.remove(foodEnergy); // 移除食物能量
                AddEatRecordActivity.this.foodList(); // 更新食物列表
            }
        });
    }

    private void loadData() {
        // 从数据库加载饮食记录和对应的食物能量数据
        Cursor cursor = this.helper.getWritableDatabase().rawQuery("select f.* from eat_record e,eat_record_details er,food_energy f where er.eatRecordId = e.id and er.foodEnergyId = f.id and er.eatRecordId =" + this.eatRecordId, null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                this.foodEnergyList.add(new FoodEnergy(Integer.valueOf(cursor.getInt(0)), Integer.valueOf(cursor.getInt(1)), cursor.getString(2), cursor.getString(3), cursor.getString(4), Double.valueOf(cursor.getDouble(5)), Double.valueOf(cursor.getDouble(6)), Double.valueOf(cursor.getDouble(7))));
            }
        }
        Collections.reverse(this.foodEnergyList); // 反转列表顺序
        foodList(); // 更新食物列表
    }

    private void foodList() {
        List<FoodEnergy> list = this.foodEnergyList; // 获取食物能量列表
        if (list == null || list.size() <= 0) {
            this.rvList.setVisibility(View.GONE); // 如果列表为空，隐藏RecyclerView
            this.llEmpty.setVisibility(View.VISIBLE); // 显示空数据布局
        } else {
            this.rvList.setVisibility(View.VISIBLE); // 显示RecyclerView
            this.llEmpty.setVisibility(View.GONE); // 隐藏空数据布局
            this.foodEnergyAdapter.addItem(this.foodEnergyList); // 更新适配器的数据
        }
        double total = Utils.DOUBLE_EPSILON; // 初始化总摄入量
        for (FoodEnergy energy : this.foodEnergyList) {
            total += Double.valueOf(energy.getHeat()); // 计算总摄入量
        }
        this.tvTotal.setText(String.format("总摄入量：%.2f Kcal", Double.valueOf(total))); // 更新总摄入量文本
    }

    // 添加食物能量记录
    public void add(View view) {
        startActivityForResult(new Intent(this.myActivity, AddFoodEnergyActivity.class), 100); // 启动添加食物能量活动
    }

    // 确认添加记录
    public void ok(View view) {
        SQLiteDatabase db = this.helper.getWritableDatabase();
        if (this.eatRecordId.intValue() > 0) {
            // 如果饮食记录ID有效，更新饮食记录
            db.execSQL("delete from eat_record_details where eatRecordId = ?", new Object[]{this.eatRecordId}); // 删除旧的食物能量记录
            db.execSQL("update eat_record set timeId = ? where id =?", new Object[]{this.timeId, this.eatRecordId}); // 更新时间ID
        } else {
            // 如果饮食记录ID无效，插入新的饮食记录
            db.execSQL("insert into eat_record (userId,timeId,date)values(?,?,?);", new Object[]{this.userId, this.timeId, this.date});
            Cursor cur = db.rawQuery("select LAST_INSERT_ROWID() ", null); // 获取插入记录的ID
            cur.moveToFirst();
            this.eatRecordId = Integer.valueOf(cur.getInt(0)); // 更新饮食记录ID
        }
        // 插入食物能量记录
        Iterator<FoodEnergy> it = this.foodEnergyList.iterator();
        while (it.hasNext()) {
            db.execSQL("insert into eat_record_details (eatRecordId,foodEnergyId)values(?,?);", new Object[]{this.eatRecordId, it.next().getId()});
        }
        db.close(); // 关闭数据库
        setResult(Activity.RESULT_OK); // 设置结果为成功
        finish(); // 结束活动
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            FoodEnergy foodEnergy = (FoodEnergy) data.getSerializableExtra("foodEnergy");
            Log.e("foodEnergy", foodEnergy.getFood());
            this.foodEnergyList.add(foodEnergy);
            foodList();
        }
    }

    public void back(View view) {
        finish();
    }
}
