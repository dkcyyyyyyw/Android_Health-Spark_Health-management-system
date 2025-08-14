package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.HealthRecord;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineHealthRecordActivity extends AppCompatActivity {
    private List<HealthRecord> healthRecordList;
    MySqliteOpenHelper helper = null;
    private LineChart lineCalorie;
    private LineChart lineHeartRate;
    private LineChart lineWeight;
    private List<Entry> listCalorie = null;
    private List<Entry> listHeartRate = null;
    private List<Entry> listWeight = null;
    private Activity myActivity;
    private TextView tvCalorie;
    private TextView tvHeartRate;
    private TextView tvWeight;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_record_line); // 设置活动的布局文件
        this.myActivity = this; // 保存当前活动的引用
        this.helper = new MySqliteOpenHelper(this); // 初始化数据库帮助类
        this.tvWeight = findViewById(R.id.tv_weight); // 获取体重文本视图
        this.lineWeight = findViewById(R.id.line_weight); // 获取体重折线图视图
        this.tvCalorie = findViewById(R.id.tv_calorie); // 获取卡路里文本视图
        this.lineCalorie = findViewById(R.id.line_calorie); // 获取卡路里折线图视图
        this.tvHeartRate = findViewById(R.id.tv_heart_rate); // 获取心率文本视图
        this.lineHeartRate = findViewById(R.id.line_heart_rate); // 获取心率折线图视图
        this.userId = (Integer) SPUtils.get(this, SPUtils.USER_ID, 1); // 获取用户ID，默认为1
        initView(); // 初始化视图
    }

    private void initView() {
        loadData(); // 加载数据
    }

    private void loadData() {
        this.healthRecordList = new ArrayList<>(); // 初始化健康记录列表
        // 查询数据库，获取该用户最近5条健康记录，按日期降序排列
        Cursor cursor = this.helper.getWritableDatabase().rawQuery("select * from health_record where userId = " + this.userId + " order by date desc limit 0, 5", null);

        // 处理查询结果
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                // 创建健康记录对象并添加到列表
                this.healthRecordList.add(new HealthRecord(
                        Integer.valueOf(cursor.getInt(0)),
                        Integer.valueOf(cursor.getInt(1)),
                        Float.valueOf(cursor.getFloat(2)),
                        Float.valueOf(cursor.getFloat(3)),
                        Float.valueOf(cursor.getFloat(4)),
                        cursor.getString(5),
                        cursor.getString(6)
                ));
            }
        }
        cursor.close(); // 关闭游标
        Collections.reverse(this.healthRecordList); // 反转列表顺序，使其按时间排序

        List<HealthRecord> list = this.healthRecordList;
        // 如果有健康记录，则更新文本视图和折线图
        if (list != null && list.size() > 0) {
            tvCalorie.setText(String.format("卡路里：%s KCAL", list.get(list.size() - 1).getKcal())); // 显示最新卡路里
            tvWeight.setText(String.format("体重：%s KG", list.get(list.size() - 1).getWeight())); // 显示最新体重
            tvHeartRate.setText(String.format("心率：%s BPM", list.get(list.size() - 1).getHeartRate())); // 显示最新心率

            // 初始化折线图数据列表
            this.listCalorie = new ArrayList<>();
            this.listWeight = new ArrayList<>();
            this.listHeartRate = new ArrayList<>();

            // 将健康记录中的数据填充到折线图数据列表中
            for (int i = 0; i < this.healthRecordList.size(); i++) {
                this.listCalorie.add(new Entry((float) i, this.healthRecordList.get(i).getKcal().floatValue())); // 卡路里数据
                this.listWeight.add(new Entry((float) i, this.healthRecordList.get(i).getWeight().floatValue())); // 体重数据
                this.listHeartRate.add(new Entry((float) i, this.healthRecordList.get(i).getHeartRate().floatValue())); // 心率数据
            }

            // 初始化折线图
            initLineChart(this.listCalorie, this.lineCalorie, "卡路里");
            initLineChart(this.listWeight, this.lineWeight, "体重");
            initLineChart(this.listHeartRate, this.lineHeartRate, "心率");
        }
    }

    private void initLineChart(List<Entry> entries, LineChart lcMain, String label) {
        // 创建折线数据集
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(Color.parseColor("#FF0E9CFF")); // 设置折线颜色
        dataSet.setValueTextColor(Color.parseColor("#FF333333")); // 设置值的颜色
        dataSet.setFillColor(Color.parseColor("#FFCFEBFF")); // 设置填充颜色
        dataSet.setMode(LineDataSet.Mode.LINEAR); // 设置折线模式
        dataSet.setDrawValues(false); // 不绘制数据点的值
        dataSet.setDrawFilled(true); // 绘制填充区域
        dataSet.setDrawCircles(false); // 不绘制数据点的圆圈

        // 设置折线图的数据
        lcMain.setData(new LineData(dataSet));
        XAxis xAxis = lcMain.getXAxis(); // 获取X轴
        xAxis.setDrawAxisLine(false); // 不绘制轴线
        xAxis.setDrawGridLines(true); // 绘制网格线
        xAxis.enableGridDashedLine(3.0f, 3.0f, 0.0f); // 设置网格线为虚线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置X轴位置
        xAxis.setAxisMinimum(entries.get(0).getX()); // 设置X轴最小值
        xAxis.setLabelCount(this.healthRecordList.size(), true); // 设置标签数量
        // 设置X轴标签格式
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return healthRecordList.get((int) value).getDate(); // 返回日期作为标签
            }
        });
        lcMain.invalidate(); // 刷新折线图
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // 在活动恢复时重新加载数据
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果返回码是100且结果为OK，重新加载数据
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            loadData();
        }
    }

    public void back(View view) {
        finish(); // 关闭当前活动
    }

}
