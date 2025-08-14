package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.load.Key;
import com.example.healthmanagement.R;

public class MenuDetailActivity extends AppCompatActivity {
    private Activity myActivity;
    private String url;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.myActivity = this; // 将当前活动实例赋值给 myActivity
        setContentView(R.layout.activity_menu_detail); // 设置活动的布局
        this.url = getIntent().getStringExtra("url"); // 从意图中获取 URL
        this.webView = findViewById(R.id.webView); // 找到 WebView 组件
        initData(); // 初始化数据和 WebView 设置
    }

    private void initData() {
        WebSettings webSettings = this.webView.getSettings(); // 获取 WebView 设置
        webSettings.setJavaScriptEnabled(true); // 启用 JavaScript
        webSettings.setDomStorageEnabled(true); // 启用 DOM 存储
        webSettings.setAppCacheEnabled(true); // 启用应用缓存
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // 设置缓存模式
        webSettings.setAppCachePath(getApplicationContext().getCacheDir().getPath()); // 设置缓存路径
        WebView.setWebContentsDebuggingEnabled(true); // 启用 WebView 调试

        // 设置 WebView 客户端
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // 加载请求的 URL
                view.loadUrl(request.getUrl().toString());
                return true; // 表示已经处理了该请求
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // 处理网页加载错误
                Log.e("WebViewError", "Error: " + description + " URL: " + failingUrl);
                // 加载错误页面
                view.loadData("<html><body><h1>网页无法打开</h1><p>位于 " + failingUrl + " 的网页无法加载，因为：" + description + "</p></body></html>", "text/html", Key.STRING_CHARSET_NAME);
            }
        });

        // 根据 URL 加载网页
        if (this.url != null) {
            this.webView.loadUrl(this.url); // 加载有效的 URL
        } else {
            // 加载无效 URL 的提示页面
            this.webView.loadData("<html><body><h1>无效的URL</h1></body></html>", "text/html", Key.STRING_CHARSET_NAME);
        }
    }

    public void back(View view) {
        finish();
    }
}
