package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmanagement.R;
import com.example.healthmanagement.adapter.WeightRecordAdapter;
import com.example.healthmanagement.bean.WeightRecord;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class WeightRecordActivity extends AppCompatActivity {
    private FloatingActionButton btnAdd;
    MySqliteOpenHelper helper = null;
    private LinearLayout llEmpty;
    private Activity myActivity;
    private RecyclerView rvList;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sf1 = new SimpleDateFormat("HH:mm:ss");
    private TextView tvWeight;
    private Integer userId;
    private WeightRecordAdapter weightRecordAdapter;
    private List<WeightRecord> weightRecordList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 调用父类的 onCreate 方法
        setContentView(R.layout.activity_weight); // 设置布局文件
        this.myActivity = this; // 获取当前活动的引用
        this.helper = new MySqliteOpenHelper(this); // 初始化数据库助手
        this.rvList = (RecyclerView) findViewById(R.id.rv_list); // 获取 RecyclerView
        this.tvWeight = (TextView) findViewById(R.id.tv_weight); // 获取显示体重的 TextView
        this.llEmpty = (LinearLayout) findViewById(R.id.ll_empty); // 获取空视图布局
        this.btnAdd = (FloatingActionButton) findViewById(R.id.btn_add); // 获取添加按钮
        this.userId = (Integer) SPUtils.get(this, SPUtils.USER_ID, 0); // 获取用户 ID
        initView(); // 初始化视图

        // 设置添加按钮的点击事件
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 创建对话框用于输入体重
                AlertDialog.Builder builder = new AlertDialog.Builder(WeightRecordActivity.this.myActivity);
                View view = WeightRecordActivity.this.getLayoutInflater().inflate(R.layout.dialog_weight, (ViewGroup) null);
                final EditText editText = (EditText) view.findViewById(R.id.et_value); // 获取输入框

                // 设置对话框属性
                builder.setTitle("今日体重").setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String weightValue = editText.getText().toString().trim(); // 获取输入的体重值
                        if (TextUtils.isEmpty(weightValue)) { // 检查输入是否为空
                            Toast.makeText(WeightRecordActivity.this.myActivity, "值不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SQLiteDatabase db = WeightRecordActivity.this.helper.getWritableDatabase(); // 获取可写数据库
                        WeightRecord weightRecord = null;

                        // 查询数据库以检查是否已有记录
                        Cursor cursor = db.rawQuery("select * from weight_record where date = ? and userId = " + WeightRecordActivity.this.userId,
                                new String[]{WeightRecordActivity.this.sf.format(new Date())});
                        if (cursor != null && cursor.getColumnCount() > 0) {
                            while (cursor.moveToNext()) {
                                weightRecord = new WeightRecord(Integer.valueOf(cursor.getInt(0)),
                                        Integer.valueOf(cursor.getInt(1)),
                                        cursor.getString(2),
                                        cursor.getString(3),
                                        cursor.getString(4));
                            }
                        }

                        // 如果已有记录，则更新，否则插入新记录
                        if (weightRecord != null) {
                            db.execSQL("update weight_record set weight = ?, time = ? where id = ?",
                                    new Object[]{weightValue, WeightRecordActivity.this.sf1.format(new Date()), weightRecord.getId()});
                        } else {
                            db.execSQL("insert into weight_record (userId, weight, date, time) values (?, ?, ?, ?);",
                                    new Object[]{WeightRecordActivity.this.userId, weightValue,
                                            WeightRecordActivity.this.sf.format(new Date()),
                                            WeightRecordActivity.this.sf1.format(new Date())});
                        }
                        WeightRecordActivity.this.loadData(); // 重新加载数据
                    }
                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // 取消时关闭对话框
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            public void onDismiss(DialogInterface dialog) {
                                // 对话框关闭时的回调
                            }
                        }).create();
                builder.show(); // 显示对话框
            }
        });
    }

    private void initView() {
        // 初始化 RecyclerView 的布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.myActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 设置为垂直方向
        this.rvList.setLayoutManager(layoutManager); // 为 RecyclerView 设置布局管理器

        // 创建适配器并设置给 RecyclerView
        WeightRecordAdapter weightRecordAdapter2 = new WeightRecordAdapter();
        this.weightRecordAdapter = weightRecordAdapter2;
        this.rvList.setAdapter(weightRecordAdapter2); // 设置适配器
        loadData(); // 加载数据
    }

    private void loadData() {
        this.weightRecordList = new ArrayList<>(); // 初始化体重记录列表
        WeightRecord weightRecord = null;

        // 查询数据库获取用户的体重记录
        Cursor cursor = this.helper.getWritableDatabase().rawQuery("select * from weight_record where userId = " + this.userId, null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            // 遍历查询结果并添加到列表
            while (cursor.moveToNext()) {
                weightRecord = new WeightRecord(Integer.valueOf(cursor.getInt(0)),
                        Integer.valueOf(cursor.getInt(1)),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4));
                this.weightRecordList.add(weightRecord);
            }
        }

        // 反转列表，使最新记录在最上面
        Collections.reverse(this.weightRecordList);
        List<WeightRecord> list = this.weightRecordList;

        // 检查列表是否为空
        if (list == null || list.size() <= 0) {
            this.rvList.setVisibility(View.GONE); // 隐藏 RecyclerView
            this.llEmpty.setVisibility(View.VISIBLE); // 显示空视图
            this.tvWeight.setText("暂无体重"); // 设置提示文本
            return;
        }

        // 显示 RecyclerView 和隐藏空视图
        this.rvList.setVisibility(View.VISIBLE);
        this.llEmpty.setVisibility(View.GONE);
        this.tvWeight.setText(String.format("%s KG", weightRecord.getWeight())); // 显示最新体重
        this.weightRecordAdapter.addItem(this.weightRecordList); // 将数据添加到适配器
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
