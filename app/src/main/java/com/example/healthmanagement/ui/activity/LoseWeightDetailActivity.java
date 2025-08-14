package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmanagement.R;

public class LoseWeightDetailActivity extends AppCompatActivity {
    private Activity myActivity; // 当前活动的引用
    private String url; // 用于加载的URL
    private WebView webView; // 用于显示网页内容的WebView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.myActivity = this; // 初始化当前活动引用
        setContentView(R.layout.activity_lose_weight_detail); // 设置活动的布局文件
        this.url = getIntent().getStringExtra("url"); // 获取传递过来的URL
        this.webView = findViewById(R.id.webView); // 获取WebView组件
        initData(); // 初始化数据
    }

    private void initData() {
        WebSettings webSettings = this.webView.getSettings(); // 获取WebView的设置
        webSettings.setJavaScriptEnabled(true); // 启用JavaScript支持
        webSettings.setUseWideViewPort(true); // 支持使用宽视口
        webSettings.setLoadWithOverviewMode(true); // 使用概览模式加载网页
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setBuiltInZoomControls(true); // 启用内置缩放控制
        webSettings.setDisplayZoomControls(false); // 隐藏缩放控制的显示
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // 设置缓存模式
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 允许JavaScript打开窗口
        webSettings.setLoadsImagesAutomatically(true); // 自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8"); // 设置默认文本编码为UTF-8
        webSettings.setDomStorageEnabled(true); // 启用DOM存储功能

        // 设置WebViewClient以处理页面加载
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url) {
                wv.loadUrl(url); // 加载新的URL
                return true; // 返回true表示不需要其他应用处理
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 处理SSL错误，继续加载
            }
        });

        this.webView.loadUrl(this.url); // 加载指定的URL
    }

    public void back(View view) {
        finish();
    }
}
