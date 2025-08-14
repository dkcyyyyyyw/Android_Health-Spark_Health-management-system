package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmanagement.R;
import com.example.healthmanagement.adapter.EatRecordAdapter;
import com.example.healthmanagement.bean.EatRecordVo;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DietActivity extends AppCompatActivity {
    private FloatingActionButton btnAdd; // 添加记录按钮
    protected String day; // 当前天
    private EatRecordAdapter eatRecordAdapter; // 餐记录适配器
    private List<EatRecordVo> eatRecordVoList; // 餐记录数据列表
    MySqliteOpenHelper helper = null; // SQLite数据库帮助类
    private LinearLayout llEmpty; // 空状态视图
    protected String month; // 当前月
    private Activity myActivity; // 当前活动
    private RecyclerView rvList; // 显示餐记录的RecyclerView
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日"); // 日期格式化
    private TextView tvDate; // 日期显示文本
    private TextView tvHeat; // 热量显示文本
    private Integer userId; // 用户ID
    protected String year; // 当前年

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet); // 设置布局
        this.myActivity = this; // 初始化当前活动
        this.helper = new MySqliteOpenHelper(this); // 初始化数据库帮助类
        this.rvList = findViewById(R.id.rv_list); // 获取RecyclerView
        this.tvDate = findViewById(R.id.tv_date); // 获取日期显示文本
        this.tvHeat = findViewById(R.id.tv_heat); // 获取热量显示文本
        this.llEmpty = findViewById(R.id.ll_empty); // 获取空状态视图
        this.btnAdd = findViewById(R.id.btn_add); // 获取添加按钮
        this.userId = (Integer) SPUtils.get(this, SPUtils.USER_ID, 0); // 获取用户ID

        initView(); // 初始化视图

        // 设置添加按钮的点击事件
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DietActivity.this.myActivity, AddEatRecordActivity.class);
                intent.putExtra("date", DietActivity.this.tvDate.getText().toString()); // 传递当前日期
                DietActivity.this.startActivityForResult(intent, 100); // 启动添加记录活动
            }
        });

        // 设置日期文本框的变化监听
        this.tvDate.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                Date date = new Date(); // 获取当前日期
                try {
                    date = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).parse(s.toString()); // 解析输入的日期
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 更新年、月、日
                DietActivity.this.year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(date);
                DietActivity.this.month = new SimpleDateFormat("MM", Locale.getDefault()).format(date);
                DietActivity.this.day = new SimpleDateFormat("dd", Locale.getDefault()).format(date);
            }
        });

        // 设置点击日期文本框时弹出日期选择器
        this.tvDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DietActivity.this.showDatePickerDialog(); // 显示日期选择对话框
            }
        });
    }

    // 初始化视图
    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.myActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 设置为垂直排列
        this.rvList.setLayoutManager(layoutManager); // 设置RecyclerView的布局管理器

        EatRecordAdapter eatRecordAdapter2 = new EatRecordAdapter(); // 创建适配器
        this.eatRecordAdapter = eatRecordAdapter2; // 初始化适配器
        this.rvList.setAdapter(eatRecordAdapter2); // 设置RecyclerView的适配器

        // 设置适配器的项点击监听
        this.eatRecordAdapter.setItemListener(new EatRecordAdapter.ItemListener() {
            @Override
            public void ItemClick(EatRecordVo eatRecordVo) {
                Intent intent = new Intent(DietActivity.this.myActivity, AddEatRecordActivity.class);
                intent.putExtra("date", DietActivity.this.tvDate.getText().toString()); // 传递日期
                intent.putExtra("eatRecordId", eatRecordVo.getId()); // 传递餐记录ID
                intent.putExtra("timeId", eatRecordVo.getTimeId()); // 传递时间ID
                DietActivity.this.startActivityForResult(intent, 100); // 启动添加记录活动
            }

            @Override
            public void ItemLongClick(final EatRecordVo eatRecordVo) {
                // 创建删除确认对话框
                AlertDialog.Builder dialog = new AlertDialog.Builder(DietActivity.this.myActivity);
                dialog.setMessage("确认要删除该数据吗");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = DietActivity.this.helper.getWritableDatabase(); // 获取可写数据库
                        // 删除餐记录及其细节
                        db.execSQL("delete from eat_record where id=?", new Object[]{eatRecordVo.getId()});
                        db.execSQL("delete from eat_record_details where eatRecordId=?", new Object[]{eatRecordVo.getId()});
                        db.close(); // 关闭数据库
                        Toast.makeText(DietActivity.this.myActivity, "删除成功", Toast.LENGTH_SHORT).show();
                        DietActivity.this.loadData(); // 重新加载数据
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

        Date date = new Date(); // 获取当前日期
        // 更新当前年、月、日
        this.year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(date);
        this.month = new SimpleDateFormat("MM", Locale.getDefault()).format(date);
        this.day = new SimpleDateFormat("dd", Locale.getDefault()).format(date);
        this.tvDate.setText(this.sf.format(date)); // 设置当前日期显示
        loadData(); // 加载数据
    }


    private void loadData() {
        this.eatRecordVoList = new ArrayList<>(); // 初始化餐记录列表
        SQLiteDatabase db = this.helper.getWritableDatabase(); // 获取可写数据库

        // 查询指定日期和用户ID的餐记录
        Cursor cursor = db.rawQuery("select * from eat_record where date =? and userId = " + this.userId,
                new String[]{this.tvDate.getText().toString()});

        // 检查查询结果
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                Integer dbId = cursor.getInt(0); // 获取记录ID
                Integer timeId = cursor.getInt(2); // 获取时间ID
                String date = cursor.getString(3); // 获取日期

                // 查询该餐记录的热量和营养成分
                Cursor cursor1 = db.rawQuery("select sum(f.heat) kcal,sum(f.fat) fat,sum(f.protein) protein,sum(f.carbon) carbon " +
                        "from eat_record e,eat_record_details er,food_energy f where er.eatRecordId = e.id and er.foodEnergyId = f.id and er.eatRecordId =" + dbId, null);

                // 初始化营养成分
                double kcal = Utils.DOUBLE_EPSILON;
                double fat = Utils.DOUBLE_EPSILON;
                double protein = Utils.DOUBLE_EPSILON;
                double carbon = Utils.DOUBLE_EPSILON;

                // 检查查询结果
                if (cursor1 != null && cursor1.getColumnCount() > 0) {
                    cursor1.moveToFirst(); // 移动到第一行
                    kcal = cursor1.getDouble(0); // 获取总热量
                    fat = cursor1.getDouble(1); // 获取总脂肪
                    protein = cursor1.getDouble(2); // 获取总蛋白质
                    carbon = cursor1.getDouble(3); // 获取总碳水化合物
                }

                // 添加餐记录到列表中
                this.eatRecordVoList.add(new EatRecordVo(dbId, timeId, kcal, fat, protein, carbon, date));
            }
        }

        // 反转列表顺序，使最近的记录在前
        Collections.reverse(this.eatRecordVoList);

        // 判断列表是否为空并更新视图
        if (this.eatRecordVoList == null || this.eatRecordVoList.size() <= 0) {
            this.rvList.setVisibility(View.GONE); // 隐藏记录列表
            this.llEmpty.setVisibility(View.VISIBLE); // 显示空状态视图
        } else {
            this.rvList.setVisibility(View.VISIBLE); // 显示记录列表
            this.llEmpty.setVisibility(View.GONE); // 隐藏空状态视图
            this.eatRecordAdapter.addItem(this.eatRecordVoList); // 更新适配器数据
        }

        // 计算总热量摄入
        double kcalAll = Utils.DOUBLE_EPSILON;
        for (EatRecordVo energy : this.eatRecordVoList) {
            kcalAll += energy.getKcal(); // 累加每条记录的热量
        }

        // 更新热量显示文本
        this.tvHeat.setText(String.format("已摄入 %.2f Kcal", kcalAll));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果返回的是添加记录的结果，重新加载数据
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            loadData();
        }
    }

    // 显示日期选择对话框
    protected void showDatePickerDialog() {
        final int yearNum = Integer.parseInt(this.year); // 获取当前年
        final int monthNum = Integer.parseInt(this.month); // 获取当前月
        final int dayNum = Integer.parseInt(this.day); // 获取当前天

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int yearSet, int monthSet, int daySet) {
                // 检查选择的日期是否合理
                if (yearSet < yearNum || monthSet < monthNum || daySet <= dayNum) {
                    Calendar c = Calendar.getInstance();
                    c.set(yearSet, monthSet, daySet); // 设置日历
                    DietActivity.this.tvDate.setText(new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(c.getTime())); // 显示选择的日期
                    DietActivity.this.loadData(); // 重新加载数据
                    return;
                }

                // 提示日期选择过于超前
                Toast.makeText(DietActivity.this.myActivity, "你选择的日期太超前了。", Toast.LENGTH_SHORT).show();
            }
        }, yearNum, monthNum - 1, dayNum).show(); // 显示日期选择器
    }

    // 统计营养成分
    public void statistics(View view) {
        double fatAll = Utils.DOUBLE_EPSILON; // 初始化总脂肪
        for (EatRecordVo energy : this.eatRecordVoList) {
            fatAll += Double.valueOf(energy.getFat().doubleValue()).doubleValue(); // 累加脂肪
        }

        double proteinAll = Utils.DOUBLE_EPSILON; // 初始化总蛋白质
        for (EatRecordVo energy2 : this.eatRecordVoList) {
            proteinAll += Double.valueOf(energy2.getProtein().doubleValue()).doubleValue(); // 累加蛋白质
        }

        double carbonAll = Utils.DOUBLE_EPSILON; // 初始化总碳水化合物
        for (EatRecordVo energy3 : this.eatRecordVoList) {
            carbonAll += Double.valueOf(energy3.getCarbon().doubleValue()).doubleValue(); // 累加碳水化合物
        }

        // 启动营养分析活动并传递统计数据
        Intent intent = new Intent(this, DietAnalysisActivity.class);
        intent.putExtra("fatAll", fatAll);
        intent.putExtra("proteinAll", proteinAll);
        intent.putExtra("carbonAll", carbonAll);
        startActivity(intent);
    }

    // 返回按钮
    public void back(View view) {
        finish(); // 关闭活动
    }
}