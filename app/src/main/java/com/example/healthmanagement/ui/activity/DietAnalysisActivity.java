package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmanagement.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.Utils;
import com.yalantis.ucrop.view.CropImageView;

import java.util.ArrayList;

public class DietAnalysisActivity extends AppCompatActivity {
    private double all;
    private double carbonAll;
    private double fatAll;
    private ImageView iv_carbon;
    private ImageView iv_fat;
    private ImageView iv_protein;
    private Activity myActivity;
    private PieChart pieChart;
    private double proteinAll;
    private TextView tv_carbon;
    private TextView tv_fat;
    private TextView tv_protein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_analysis); // 设置布局
        myActivity = this; // 初始化当前活动

        // 初始化视图组件
        pieChart = findViewById(R.id.pieChart); // 饼图视图
        iv_fat = findViewById(R.id.iv_fat); // 脂肪图标
        iv_protein = findViewById(R.id.iv_protein); // 蛋白质图标
        iv_carbon = findViewById(R.id.iv_carbon); // 碳水图标
        tv_fat = findViewById(R.id.tv_fat); // 脂肪文本
        tv_protein = findViewById(R.id.tv_protein); // 蛋白质文本
        tv_carbon = findViewById(R.id.tv_carbon); // 碳水文本

        // 从意图中获取营养成分的总值
        fatAll = getIntent().getDoubleExtra("fatAll", Utils.DOUBLE_EPSILON);
        proteinAll = getIntent().getDoubleExtra("proteinAll", Utils.DOUBLE_EPSILON);
        carbonAll = getIntent().getDoubleExtra("carbonAll", Utils.DOUBLE_EPSILON);
        all = proteinAll + fatAll + carbonAll; // 计算总营养成分

        // 根据脂肪摄入量设置相应的图标
        if (fatAll < 33.0) {
            iv_fat.setImageResource(R.drawable.ic_flat); // 低摄入
        } else if (fatAll > 49.0) {
            iv_fat.setImageResource(R.drawable.ic_high); // 高摄入
        } else {
            iv_fat.setImageResource(R.drawable.ic_suitable); // 合适摄入
        }

        // 根据蛋白质摄入量设置相应的图标
        if (proteinAll < 37.0) {
            iv_protein.setImageResource(R.drawable.ic_flat); // 低摄入
        } else if (proteinAll > 74.0) {
            iv_protein.setImageResource(R.drawable.ic_high); // 高摄入
        } else {
            iv_protein.setImageResource(R.drawable.ic_suitable); // 合适摄入
        }

        // 根据碳水化合物摄入量设置相应的图标
        if (carbonAll < 166.0) {
            iv_carbon.setImageResource(R.drawable.ic_flat); // 低摄入
        } else if (carbonAll > 239.0) {
            iv_carbon.setImageResource(R.drawable.ic_high); // 高摄入
        } else {
            iv_carbon.setImageResource(R.drawable.ic_suitable); // 合适摄入
        }

        // 显示各项营养成分的值
        tv_fat.setText(String.valueOf(fatAll));
        tv_protein.setText(String.valueOf(proteinAll));
        tv_carbon.setText(String.valueOf(carbonAll));
        initView(); // 初始化视图
    }

    // 初始化视图
    private void initView() {
        showChart(pieChart, getPieData()); // 显示饼图
    }

    // 显示饼图
    private void showChart(PieChart pieChart2, PieData pieData) {
        Description description = new Description(); // 创建描述
        description.setText(""); // 设置描述文本为空
        pieChart2.setDescription(description); // 设置饼图描述
        pieChart2.setHoleRadius(60.0f); // 设置孔的半径
        pieChart2.setTransparentCircleRadius(0.0f); // 设置透明圈的半径
        pieChart2.setCenterTextSize(16.0f); // 设置中心文本大小
        pieChart2.setCenterTextColor(-16776961); // 设置中心文本颜色
        pieChart2.setRotationAngle(90.0f); // 设置初始旋转角度
        pieChart2.setRotationEnabled(true); // 允许旋转
        pieChart2.setUsePercentValues(true); // 使用百分比显示
        pieChart2.setData(pieData); // 设置数据

        // 设置图例
        Legend legend = pieChart2.getLegend();
        legend.setXEntrySpace(7.0f); // 设置X轴间距
        legend.setYEntrySpace(15.0f); // 设置Y轴间距
        pieChart2.animateXY(CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION); // 动画效果
    }

    // 获取饼图数据
    private PieData getPieData() {
        float q1 = 1.0f; // 脂肪比例
        float q2 = 1.0f; // 蛋白质比例
        float q3 = 1.0f; // 碳水比例
        ArrayList<PieEntry> yArrayList = new ArrayList<>(); // 数据列表

        // 计算各项营养成分的比例
        if (all != Utils.DOUBLE_EPSILON) {
            q1 = Float.valueOf(String.format("%.2f", fatAll / all)).floatValue(); // 计算脂肪比例
            q2 = Float.valueOf(String.format("%.2f", proteinAll / all)).floatValue(); // 计算蛋白质比例
            q3 = Float.valueOf(String.format("%.2f", carbonAll / all)).floatValue(); // 计算碳水比例
        }

        // 添加数据条目
        yArrayList.add(new PieEntry(q1, "脂肪"));
        yArrayList.add(new PieEntry(q2, "蛋白质"));
        yArrayList.add(new PieEntry(q3, "碳水"));

        // 创建饼图数据集
        PieDataSet pieDataSet = new PieDataSet(yArrayList, "");
        pieDataSet.setDrawValues(true); // 显示值
        pieDataSet.setValueFormatter(new PercentFormatter()); // 设置格式化器
        pieDataSet.setSliceSpace(1.0f); // 切片间距

        // 设置颜色
        ArrayList<Integer> colorList = new ArrayList<>();
        colorList.add(getColor(R.color.colorPrimary)); // 脂肪颜色
        colorList.add(getColor(R.color.colorRed)); // 蛋白质颜色
        colorList.add(getColor(R.color.colorBlue)); // 碳水颜色
        pieDataSet.setColors(colorList); // 应用颜色
        pieDataSet.setValueTextColor(-1); // 设置值文本颜色为白色
        pieDataSet.setValueTextSize(15.0f); // 设置值文本大小

        return new PieData(pieDataSet); // 返回饼图数据
    }

    // 返回按钮
    public void back(View view) {
        finish(); // 关闭当前活动
    }
}