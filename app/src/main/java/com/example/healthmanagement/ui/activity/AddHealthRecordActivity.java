package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.HealthRecord;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddHealthRecordActivity extends AppCompatActivity {
    protected String day; // 当前天
    private EditText et_calorie; // 卡路里输入框
    private EditText et_heart_rate; // 心率输入框
    private EditText et_weight; // 体重输入框
    MySqliteOpenHelper helper = null; // SQLite数据库帮助类
    private HealthRecord mHealthRecord; // 健康记录对象
    protected String month; // 当前月
    private Activity myActivity; // 当前活动
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd"); // 日期格式化
    private SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 日期时间格式化
    private TextView tvDate; // 日期显示文本
    private Integer userId; // 用户ID
    protected String year; // 当前年

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_record_add); // 设置布局
        myActivity = this; // 初始化当前活动
        helper = new MySqliteOpenHelper(this); // 初始化数据库帮助类
        et_weight = findViewById(R.id.et_weight); // 找到体重输入框
        et_heart_rate = findViewById(R.id.et_heart_rate); // 找到心率输入框
        et_calorie = findViewById(R.id.et_calorie); // 找到卡路里输入框
        tvDate = findViewById(R.id.tv_date); // 找到日期显示文本
        mHealthRecord = (HealthRecord) getIntent().getSerializableExtra("healthRecord"); // 从意图中获取健康记录

        Date date = new Date(); // 获取当前日期
        year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(date); // 获取当前年
        month = new SimpleDateFormat("MM", Locale.getDefault()).format(date); // 获取当前月
        day = new SimpleDateFormat("dd", Locale.getDefault()).format(date); // 获取当前天
        tvDate.setText(sf.format(date)); // 设置当前日期显示

        // 如果存在健康记录，填充输入框
        if (mHealthRecord != null) {
            et_weight.setText(String.valueOf(mHealthRecord.getWeight()));
            et_heart_rate.setText(String.valueOf(mHealthRecord.getHeartRate()));
            et_calorie.setText(String.valueOf(mHealthRecord.getKcal()));
            tvDate.setText(mHealthRecord.getDate());
        }

        userId = (Integer) SPUtils.get(this, SPUtils.USER_ID, 0); // 获取用户ID
        // 设置日期文本框的变化监听
        tvDate.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                Date date = new Date(); // 获取当前日期
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(s.toString()); // 解析输入的日期
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(date); // 更新年
                month = new SimpleDateFormat("MM", Locale.getDefault()).format(date); // 更新月
                day = new SimpleDateFormat("dd", Locale.getDefault()).format(date); // 更新天
            }
        });

        // 设置点击日期文本框时弹出日期选择器
        tvDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDatePickerDialog(); // 显示日期选择对话框
            }
        });
    }

    // 保存健康记录
    public void save(View view) {
        String weight = et_weight.getText().toString(); // 获取体重
        String heart_rate = et_heart_rate.getText().toString(); // 获取心率
        String calorie = et_calorie.getText().toString(); // 获取卡路里
        String date = tvDate.getText().toString(); // 获取日期

        // 检查输入是否为空
        if ("".equals(weight)) {
            Toast.makeText(myActivity, "体重不能为空", Toast.LENGTH_SHORT).show();
        } else if ("".equals(heart_rate)) {
            Toast.makeText(myActivity, "心率不能为空", Toast.LENGTH_SHORT).show();
        } else if ("".equals(calorie)) {
            Toast.makeText(myActivity, "卡路里不能为空", Toast.LENGTH_SHORT).show();
        } else {
            HealthRecord healthRecord = null; // 声明健康记录对象
            SQLiteDatabase db = helper.getWritableDatabase(); // 获取可写数据库
            // 查询数据库中是否已存在该日期的记录
            Cursor cursor = db.rawQuery("SELECT * FROM health_record WHERE date = ?", new String[]{date});
            if (cursor != null && cursor.getColumnCount() > 0) {
                while (cursor.moveToNext()) {
                    // 创建健康记录对象
                    healthRecord = new HealthRecord(Integer.valueOf(cursor.getInt(0)), Integer.valueOf(cursor.getInt(1)),
                            Float.valueOf(cursor.getFloat(2)), Float.valueOf(cursor.getFloat(3)),
                            Float.valueOf(cursor.getFloat(4)), cursor.getString(5), cursor.getString(6));
                }
            }

            // 如果没有传入健康记录，执行插入操作
            if (mHealthRecord == null) {
                if (healthRecord == null) {
                    // 插入新记录
                    db.execSQL("INSERT INTO health_record(userId, kcal, weight, heartRate, date, time) VALUES(?, ?, ?, ?, ?, ?)",
                            new Object[]{userId, calorie, weight, heart_rate, date, sf1.format(new Date())});
                    Toast.makeText(myActivity, "保存成功", Toast.LENGTH_SHORT).show();
                    finish(); // 关闭活动
                } else {
                    Toast.makeText(myActivity, "该记录日期已存在", Toast.LENGTH_SHORT).show();
                }
            } else if (mHealthRecord.getDate().equals(healthRecord.getDate())) { // 如果是编辑现有记录
                db.execSQL("UPDATE health_record SET kcal=?, weight=?, heartRate=?, date=? WHERE id=?",
                        new Object[]{calorie, weight, heart_rate, date, mHealthRecord.getId()});
                Toast.makeText(myActivity, "保存成功", Toast.LENGTH_SHORT).show();
                finish(); // 关闭活动
            } else {
                Toast.makeText(myActivity, "该记录日期已存在", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 显示日期选择对话框
    public void showDatePickerDialog() {
        final int yearNum = Integer.parseInt(year); // 当前年
        final int monthNum = Integer.parseInt(month); // 当前月
        final int dayNum = Integer.parseInt(day); // 当前天
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int yearSet, int monthSet, int daySet) {
                // 检查选择的日期是否合规
                if (yearSet < yearNum || monthSet < monthNum || daySet <= dayNum) {
                    Calendar c = Calendar.getInstance();
                    c.set(yearSet, monthSet, daySet); // 设置日历
                    AddHealthRecordActivity.this.tvDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(c.getTime())); // 显示选择的日期
                    return;
                }
                Toast.makeText(AddHealthRecordActivity.this.myActivity, "你选择的日期太超前了。", Toast.LENGTH_SHORT).show();
            }
        }, yearNum, monthNum - 1, dayNum).show(); // 显示日期选择器
    }

    // 返回按钮
    public void back(View view) {
        finish(); // 关闭活动
    }
}
