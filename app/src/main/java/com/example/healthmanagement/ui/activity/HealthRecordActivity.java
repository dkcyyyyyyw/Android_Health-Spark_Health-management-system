package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmanagement.R;
import com.example.healthmanagement.adapter.HealthRecordAdapter;
import com.example.healthmanagement.bean.HealthRecord;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HealthRecordActivity extends AppCompatActivity {
    private FloatingActionButton btnAdd;
    private Integer count;
    private HealthRecordAdapter healthRecordAdapter;
    private List<HealthRecord> healthRecordList;
    MySqliteOpenHelper helper = null;
    private LinearLayout llEmpty;
    private Activity myActivity;
    private RecyclerView rvList;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sf1 = new SimpleDateFormat("HH:mm:ss");
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_record); // 设置布局
        this.myActivity = this; // 初始化当前活动
        this.helper = new MySqliteOpenHelper(this); // 初始化数据库帮助类
        this.llEmpty = findViewById(R.id.ll_empty); // 获取空视图
        this.rvList = findViewById(R.id.rv_list); // 获取列表视图
        this.btnAdd = findViewById(R.id.btn_add); // 获取添加按钮
        this.userId = (Integer) SPUtils.get(this, SPUtils.USER_ID, 0); // 获取用户ID
        initView(); // 初始化视图

        // 设置添加按钮的点击事件
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HealthRecordActivity.this.startActivity(new Intent(HealthRecordActivity.this.myActivity, AddHealthRecordActivity.class));
            }
        });
    }

    // 初始化视图
    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.myActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 设置为垂直方向
        this.rvList.setLayoutManager(layoutManager); // 应用布局管理器
        this.healthRecordAdapter = new HealthRecordAdapter(); // 初始化适配器
        this.rvList.setAdapter(this.healthRecordAdapter); // 设置适配器

        // 设置适配器的点击事件
        this.healthRecordAdapter.setItemListener(new HealthRecordAdapter.ItemListener() {
            @Override
            public void ItemClick(HealthRecord healthRecord) {
                Intent intent = new Intent(HealthRecordActivity.this.myActivity, AddHealthRecordActivity.class);
                intent.putExtra("healthRecord", healthRecord); // 传递健康记录
                HealthRecordActivity.this.startActivity(intent); // 启动添加健康记录活动
            }

            @Override
            public void ItemLongClick(final HealthRecord healthRecord) {
                // 长按删除确认对话框
                AlertDialog.Builder dialog = new AlertDialog.Builder(HealthRecordActivity.this.myActivity);
                dialog.setMessage("确认要删除该数据吗");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = HealthRecordActivity.this.helper.getWritableDatabase();
                        if (db.isOpen()) {
                            db.execSQL("delete from health_record where id = " + healthRecord.getId()); // 删除记录
                            db.close(); // 关闭数据库
                        }
                        Toast.makeText(HealthRecordActivity.this.myActivity, "删除成功", Toast.LENGTH_SHORT).show();
                        HealthRecordActivity.this.loadData(); // 重新加载数据
                    }
                });
                dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // 关闭对话框
                    }
                });
                dialog.show(); // 显示对话框
            }
        });
        loadData(); // 加载数据
    }

    // 加载数据
    private void loadData() {
        this.healthRecordList = new ArrayList<>(); // 初始化健康记录列表
        Cursor cursor = this.helper.getWritableDatabase().rawQuery("select * from health_record where userId = " + this.userId + " order by date", null); // 查询数据库
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                // 创建健康记录对象并添加到列表
                this.healthRecordList.add(new HealthRecord(Integer.valueOf(cursor.getInt(0)), Integer.valueOf(cursor.getInt(1)), Float.valueOf(cursor.getFloat(2)), Float.valueOf(cursor.getFloat(3)), Float.valueOf(cursor.getFloat(4)), cursor.getString(5), cursor.getString(6)));
            }
        }
        Collections.reverse(this.healthRecordList); // 反转列表顺序

        // 判断列表是否为空
        if (this.healthRecordList == null || this.healthRecordList.size() <= 0) {
            this.rvList.setVisibility(View.GONE); // 隐藏列表
            this.llEmpty.setVisibility(View.VISIBLE); // 显示空视图
            return;
        }
        this.rvList.setVisibility(View.VISIBLE); // 显示列表
        this.llEmpty.setVisibility(View.GONE); // 隐藏空视图
        this.healthRecordAdapter.addItem(this.healthRecordList); // 更新适配器数据
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // 活动恢复时加载数据
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            loadData(); // 请求成功时重新加载数据
        }
    }

    // 处理线性图表按钮点击
    public void line(View view) {
        Cursor cursor = this.helper.getWritableDatabase().rawQuery("select count(*) from health_record where userId = " + this.userId + " order by date desc limit 0,5", null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            cursor.moveToNext();
            this.count = Integer.valueOf(cursor.getInt(0)); // 获取记录数量
        }
        if (this.count.intValue() > 1) {
            startActivity(new Intent(this.myActivity, LineHealthRecordActivity.class)); // 启动线性图表活动
        } else {
            Toast.makeText(this.myActivity, "数据少于2条", Toast.LENGTH_SHORT).show(); // 提示数据不足
        }
    }

    // 返回按钮事件
    public void back(View view) {
        finish(); // 关闭当前活动
    }
}