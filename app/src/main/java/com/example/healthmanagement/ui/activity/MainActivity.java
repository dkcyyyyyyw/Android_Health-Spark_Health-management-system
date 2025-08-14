package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthmanagement.R;
import com.example.healthmanagement.ui.fragment.HomeFragment;
import com.example.healthmanagement.ui.fragment.MenuFragment;
import com.example.healthmanagement.ui.fragment.UserFragment;

public class MainActivity extends AppCompatActivity {
    private Fragment[] fragments = {null, null, null};
    private LinearLayout llContent;
    private Activity myActivity;
    private RadioButton rbHome;
    private RadioButton rbMenu;
    private RadioButton rbUser;
    private long time = 0;
    private TextView tvTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.myActivity = this;
        setContentView(R.layout.activity_main);
        this.tvTitle = (TextView) findViewById(R.id.title);
        this.llContent = (LinearLayout) findViewById(R.id.ll_main_content);
        this.rbHome = (RadioButton) findViewById(R.id.rb_main_home);
        this.rbMenu = (RadioButton) findViewById(R.id.rb_main_menu);
        this.rbUser = (RadioButton) findViewById(R.id.rb_main_user);
        initView();
        setViewListener();
    }

    private void setViewListener() {
        this.rbHome.setOnClickListener(v -> {
            MainActivity.this.tvTitle.setText("首页");
            MainActivity.this.switchFragment(0);
        });
        this.rbMenu.setOnClickListener(v -> {
            MainActivity.this.tvTitle.setText("健康菜谱");
            MainActivity.this.switchFragment(1);
        });
        this.rbUser.setOnClickListener(v -> {
            MainActivity.this.tvTitle.setText("我的");
            MainActivity.this.switchFragment(2);
        });
    }

    private void initView() {
        // 获取并设置首页图标
        Drawable iconHome = getResources().getDrawable(R.drawable.selector_main_rb_home);
        iconHome.setBounds(0, 0, 68, 68); // 设置图标的边界
        this.rbHome.setCompoundDrawables(null, iconHome, null, null); // 设置图标到按钮上
        this.rbHome.setCompoundDrawablePadding(5); // 设置图标与文本之间的间距

        // 获取并设置菜单图标
        Drawable iconMenu = getResources().getDrawable(R.drawable.selector_main_rb_menu);
        iconMenu.setBounds(0, 0, 68, 68);
        this.rbMenu.setCompoundDrawables(null, iconMenu, null, null);
        this.rbMenu.setCompoundDrawablePadding(5);

        // 获取并设置用户图标
        Drawable iconUser = getResources().getDrawable(R.drawable.selector_main_rb_user);
        iconUser.setBounds(0, 0, 68, 68);
        this.rbUser.setCompoundDrawables(null, iconUser, null, null);
        this.rbUser.setCompoundDrawablePadding(5);

        // 初始化时显示第一个Fragment
        switchFragment(0);
        this.rbHome.setChecked(true); // 设置首页为选中状态
    }

    private void switchFragment(int fragmentIndex) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); // 开始Fragment事务
        Fragment[] fragmentArr = this.fragments; // 获取Fragment数组

        // 如果当前Fragment为空，则创建新的Fragment并添加
        if (fragmentArr[fragmentIndex] == null) {
            switch (fragmentIndex) {
                case 0:
                    fragmentArr[fragmentIndex] = new HomeFragment(); // 创建首页Fragment
                    break;
                case 1:
                    fragmentArr[fragmentIndex] = new MenuFragment(); // 创建菜单Fragment
                    break;
                case 2:
                    fragmentArr[fragmentIndex] = new UserFragment(); // 创建用户Fragment
                    break;
            }
            transaction.add(R.id.ll_main_content, this.fragments[fragmentIndex]); // 添加Fragment到主内容区域
        }

        // 遍历所有Fragment，将非当前Fragment隐藏
        int i = 0;
        while (true) {
            Fragment[] fragmentArr2 = this.fragments;
            if (i < fragmentArr2.length) {
                if (!(fragmentIndex == i || fragmentArr2[i] == null)) {
                    transaction.hide(fragmentArr2[i]); // 隐藏非当前Fragment
                }
                i++;
            } else {
                transaction.show(fragmentArr2[fragmentIndex]); // 显示当前Fragment
                transaction.commit(); // 提交事务
                return; // 返回
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return false;
        }
        exit();
        return false;
    }

    public void exit() {
        if (System.currentTimeMillis() - this.time > 2000) {
            this.time = System.currentTimeMillis();
            Toast.makeText(this.myActivity, "再点击一次退出应用程序", Toast.LENGTH_SHORT).show();
            return;
        }
        finish();
    }
}