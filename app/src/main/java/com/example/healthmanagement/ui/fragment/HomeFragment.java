package com.example.healthmanagement.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.BMIResult;
import com.example.healthmanagement.ui.activity.DietActivity;
import com.example.healthmanagement.ui.activity.FoodEnergyActivity;
import com.example.healthmanagement.ui.activity.HealthRecordActivity;
import com.example.healthmanagement.ui.activity.LoseWeightActivity;
import com.example.healthmanagement.ui.activity.SportRecordActivity;
import com.example.healthmanagement.ui.activity.WeightRecordActivity;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.OkHttpTool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private Button btnCalculate;
    private EditText etHeight;
    private EditText etWeight;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    MySqliteOpenHelper helper = null;
    private LinearLayout ll_diet;
    private LinearLayout ll_food_energy;
    private LinearLayout ll_health_record;
    private LinearLayout ll_lose_weight;
    private LinearLayout ll_sport_record;
    private LinearLayout ll_weight;
    private Activity myActivity;
    private RadioGroup rgSex;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.myActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        this.helper = new MySqliteOpenHelper(this.myActivity);
        this.ll_food_energy = (LinearLayout) view.findViewById(R.id.ll_food_energy);
        this.ll_weight = (LinearLayout) view.findViewById(R.id.ll_weight);
        this.ll_diet = (LinearLayout) view.findViewById(R.id.ll_diet);
        this.ll_lose_weight = (LinearLayout) view.findViewById(R.id.ll_lose_weight);
        this.ll_health_record = (LinearLayout) view.findViewById(R.id.ll_health_record);
        this.ll_sport_record = (LinearLayout) view.findViewById(R.id.ll_sport_record);
        this.ll_lose_weight = (LinearLayout) view.findViewById(R.id.ll_lose_weight);
        this.etHeight = (EditText) view.findViewById(R.id.et_height);
        this.etWeight = (EditText) view.findViewById(R.id.et_weight);
        this.rgSex = (RadioGroup) view.findViewById(R.id.rg_sex);
        this.btnCalculate = (Button) view.findViewById(R.id.btn_calculate);
        initView();
        this.ll_food_energy.setOnClickListener(v -> {
            HomeFragment.this.startActivity(new Intent(HomeFragment.this.myActivity, FoodEnergyActivity.class));
        });
        this.ll_weight.setOnClickListener(v -> {
            HomeFragment.this.startActivity(new Intent(HomeFragment.this.myActivity, WeightRecordActivity.class));
        });
        this.ll_diet.setOnClickListener(v -> {
            HomeFragment.this.startActivity(new Intent(HomeFragment.this.myActivity, DietActivity.class));
        });
        this.ll_lose_weight.setOnClickListener(v -> {
            HomeFragment.this.startActivity(new Intent(HomeFragment.this.myActivity, LoseWeightActivity.class));
        });
        this.ll_health_record.setOnClickListener(v -> {
            HomeFragment.this.startActivity(new Intent(HomeFragment.this.myActivity, HealthRecordActivity.class));
        });
        this.ll_sport_record.setOnClickListener(v -> {
            HomeFragment.this.startActivity(new Intent(HomeFragment.this.myActivity, SportRecordActivity.class));
        });
        this.btnCalculate.setOnClickListener(v ->
        {
            HomeFragment.this.calculate();
        });
        return view;
    }

    private void initView() {
    }

    private void calculate() {
        // 获取用户输入的身高和体重
        String height = this.etHeight.getText().toString();
        String weight = this.etWeight.getText().toString();

        // 检查身高和体重是否为空
        if ("".equals(height)) {
            Toast.makeText(this.myActivity, "身高不能为空", Toast.LENGTH_SHORT).show(); // 提示身高不能为空
        } else if ("".equals(weight)) {
            Toast.makeText(this.myActivity, "体重不能为空", Toast.LENGTH_SHORT).show(); // 提示体重不能为空
        } else {
            // 根据选中的性别获取性别值（1: 男，2: 女）
            int sex = this.rgSex.getCheckedRadioButtonId() == R.id.rb_man ? 1 : 2;

            // 创建参数 Map
            Map<String, Object> map = new HashMap<>();
            map.put("sex", Integer.valueOf(sex)); // 性别
            map.put("height", Integer.valueOf(height)); // 身高
            map.put("weight", Double.valueOf(weight)); // 体重
            map.put("key", "ab77a66ba46de591e239ff3a052fb36c"); // API 密钥

            //调用第三方 API 计算 BMI 指数，并展示结果对话框
            // 发送 GET 请求到 BMI 计算 API
            OkHttpTool.httpGet("http://apis.juhe.cn/fapig/calculator/weight", map, new OkHttpTool.ResponseCallback() {

                @Override
                public void onResponse(final boolean isSuccess, final int responseCode, final String response, Exception exception) {
                    // 在主线程中处理响应
                    HomeFragment.this.myActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            // 检查请求是否成功
                            if (isSuccess && responseCode == 200) {
                                try {
                                    // 解析 JSON 响应
                                    JSONObject jsonObject = new JSONObject(response);
                                    String result = jsonObject.getString("result"); // 结果
                                    String reason = jsonObject.getString("reason"); // 说明

                                    // 判断响应状态是否成功
                                    if ("SUCCES".equals(reason)) {
                                        // 解析 BMI 结果
                                        BMIResult bmiResult = (BMIResult) HomeFragment.this.gson.fromJson(result, BMIResult.class);
                                        if (bmiResult != null) {
                                            // 显示 BMI 结果的对话框
                                            new AlertDialog.Builder(HomeFragment.this.myActivity)
                                                    .setTitle("BMI指数")
                                                    .setMessage("等级：" + bmiResult.getLevel() + "\n\n" +
                                                            "等级描述：" + bmiResult.getLevelMsg() + "\n\n" +
                                                            "标准体重：" + bmiResult.getIdealWeight() + "\n\n" +
                                                            "正常体重范围：" + bmiResult.getNormalWeight() + "\n\n" +
                                                            "BMI指数：" + bmiResult.getBmi() + "\n\n" +
                                                            "BMI指数范围：" + bmiResult.getNormalBMI() + "\n\n" +
                                                            "相关疾病发病危险：" + bmiResult.getDanger())
                                                    .setIcon(R.drawable.ic_bmi)
                                                    .create()
                                                    .show();
                                        } else {
                                            Toast.makeText(HomeFragment.this.myActivity, "计算失败", Toast.LENGTH_SHORT).show();
                                        }
                                        return;
                                    }
                                    // 如果响应不成功，提示相应信息
                                    Toast.makeText(HomeFragment.this.myActivity, reason, Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace(); // 捕获 JSON 解析异常
                                }
                            }
                        }
                    });
                }
            });
        }
    }
}