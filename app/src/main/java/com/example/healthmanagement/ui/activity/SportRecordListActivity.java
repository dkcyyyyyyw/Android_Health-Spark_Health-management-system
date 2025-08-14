package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthmanagement.R;
import com.example.healthmanagement.adapter.SportRecordAdapter;
import com.example.healthmanagement.bean.SportRecord;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SportRecordListActivity extends AppCompatActivity {
    MySqliteOpenHelper helper = null;
    private LinearLayout llEmpty;
    private Activity myActivity;
    private RecyclerView rvList;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sf1 = new SimpleDateFormat("HH:mm:ss");
    private SportRecordAdapter sportRecordAdapter;
    private List<SportRecord> sportRecordList;
    private Integer userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_record_list);
        this.myActivity = this;
        this.helper = new MySqliteOpenHelper(this);
        this.llEmpty = (LinearLayout) findViewById(R.id.ll_empty);
        this.rvList = (RecyclerView) findViewById(R.id.rv_list);
        this.userId = (Integer) SPUtils.get(this, SPUtils.USER_ID, 0);
        initView();
    }

    private void initView() {
        // 初始化 RecyclerView 的布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.myActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 设置为垂直方向
        this.rvList.setLayoutManager(layoutManager); // 为 RecyclerView 设置布局管理器

        // 创建适配器并设置给 RecyclerView
        SportRecordAdapter sportRecordAdapter2 = new SportRecordAdapter();
        this.sportRecordAdapter = sportRecordAdapter2;
        this.rvList.setAdapter(sportRecordAdapter2); // 设置适配器

        // 设置适配器的点击事件监听器
        this.sportRecordAdapter.setItemListener(new SportRecordAdapter.ItemListener() {
            @Override
            public void ItemClick(SportRecord sportRecord) {
                // 单击事件处理（当前未实现）
            }

            @Override
            public void ItemLongClick(SportRecord sportRecord) {
                // 长按事件处理（当前未实现）
            }
        });

        loadData(); // 加载数据
    }

    private void loadData() {
        this.sportRecordList = new ArrayList<>(); // 初始化运动记录列表

        // 查询数据库获取用户的运动记录
        Cursor cursor = this.helper.getWritableDatabase().rawQuery("select * from sport_record where userId = " + this.userId + " order by date", null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            // 遍历查询结果并添加到列表
            while (cursor.moveToNext()) {
                this.sportRecordList.add(new SportRecord(Integer.valueOf(cursor.getInt(0)),
                        Integer.valueOf(cursor.getInt(1)),
                        Integer.valueOf(cursor.getInt(2)),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)));
            }
        }

        // 反转列表，使最新记录在最上面
        Collections.reverse(this.sportRecordList);
        List<SportRecord> list = this.sportRecordList;

        // 检查列表是否为空
        if (list == null || list.size() <= 0) {
            this.rvList.setVisibility(View.GONE); // 隐藏 RecyclerView
            this.llEmpty.setVisibility(View.VISIBLE); // 显示空视图
            return;
        }

        // 显示 RecyclerView 和隐藏空视图
        this.rvList.setVisibility(View.VISIBLE);
        this.llEmpty.setVisibility(View.GONE);

        // 将数据添加到适配器
        this.sportRecordAdapter.addItem(this.sportRecordList);
    }

    @Override
    public void onResume() {
        super.onResume(); // 调用父类的 onResume 方法
        loadData(); // 当活动恢复时加载数据
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // 调用父类的 onActivityResult 方法
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            loadData(); // 如果返回码是 OK，则重新加载数据
        }
    }

    public void back(View view) {
        finish();
    }
}
