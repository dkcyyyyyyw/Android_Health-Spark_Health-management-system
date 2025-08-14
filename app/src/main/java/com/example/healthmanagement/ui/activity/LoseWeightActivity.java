package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmanagement.R;
import com.example.healthmanagement.adapter.LoseWeightAdapter;
import com.example.healthmanagement.bean.LoseWeightBean;
import com.example.healthmanagement.util.OkHttpTool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.config.PictureConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoseWeightActivity extends AppCompatActivity {
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private LoseWeightAdapter mNewsAdapter;
    private Activity myActivity;
    private RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.myActivity = this;
        setContentView(R.layout.activity_lose_weight);
        this.rvList = findViewById(R.id.rv_list);
        initView();
    }

    private void initView() {
        // 创建一个线性布局管理器，设置方向为垂直
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.myActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.rvList.setLayoutManager(layoutManager); // 将布局管理器应用于 RecyclerView

        // 初始化适配器
        this.mNewsAdapter = new LoseWeightAdapter();
        this.rvList.setAdapter(this.mNewsAdapter); // 将适配器设置到 RecyclerView

        loadData(); // 加载数据
    }

    //通过聚合数据 API 获取健康类资讯并展示在 RecyclerView 中
    private void loadData() {
        // 创建请求参数的映射
        Map<String, Object> map = new HashMap<>();
        map.put("type", "jiankang"); // 设置请求类型
        map.put(PictureConfig.EXTRA_PAGE, 1); // 设置当前页
        map.put("page_size", 1000); // 设置每页的大小
        map.put("key", "7d9559b3ff2cf59806ee5ee5aabd3cdb"); // 设置API访问的密钥

        // 发起GET请求
        OkHttpTool.httpGet("http://v.juhe.cn/toutiao/index", map, new OkHttpTool.ResponseCallback() {
            @Override
            public void onResponse(final boolean isSuccess, final int responseCode, final String response, Exception exception) {
                // 在主线程中运行UI更新
                LoseWeightActivity.this.myActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isSuccess && responseCode == 200) {
                            try {
                                // 解析JSON响应数据
                                List<LoseWeightBean> list = (List) LoseWeightActivity.this.gson.fromJson(
                                        new JSONObject(new JSONObject(response).getString("result")).getString("data"),
                                        new TypeToken<List<LoseWeightBean>>() {
                                        }.getType()
                                );
                                // 如果列表不为空，则添加项到适配器
                                if (list.size() > 0) {
                                    LoseWeightActivity.this.mNewsAdapter.addItem(list);
                                }
                                // 提示加载成功
                                Toast.makeText(LoseWeightActivity.this.myActivity, "加载成功", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace(); // 打印异常
                            }
                        }
                    }
                });
            }
        });
    }


    public void back(View view) {
        finish();
    }
}
