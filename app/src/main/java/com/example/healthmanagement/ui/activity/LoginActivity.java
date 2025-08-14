package com.example.healthmanagement.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.User;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText etAccount;
    private EditText etPassword;
    MySqliteOpenHelper helper = null;
    private TextView tvRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 设置登录活动的布局
        this.helper = new MySqliteOpenHelper(this); // 初始化数据库帮助类
        this.etAccount = (EditText) findViewById(R.id.et_account); // 获取账号输入框
        this.etPassword = (EditText) findViewById(R.id.et_password); // 获取密码输入框
        this.tvRegister = (TextView) findViewById(R.id.tv_register); // 获取注册文本视图
        this.btnLogin = (Button) findViewById(R.id.btn_login); // 获取登录按钮

        // 注册点击事件，跳转到注册活动
        this.tvRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // 注册登录按钮的点击事件
        this.btnLogin.setOnClickListener(v -> {
            String account = LoginActivity.this.etAccount.getText().toString(); // 获取输入的账号
            String password = LoginActivity.this.etPassword.getText().toString(); // 获取输入的密码

            // 校验输入
            if ("".equals(account)) {
                Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show(); // 账号不能为空提示
            } else if ("".equals(password)) {
                Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show(); // 密码不能为空提示
            } else if (!account.equals("admin") || !password.equals("123456")) { // 如果不是默认账号和密码
                User mUser = null; // 初始化用户对象
                SQLiteDatabase db = LoginActivity.this.helper.getWritableDatabase(); // 获取可写数据库
                // 查询数据库中的用户
                Cursor cursor = db.rawQuery("select * from user where account = ?", new String[]{account});
                if (cursor != null && cursor.getColumnCount() > 0) {
                    while (cursor.moveToNext()) {
                        // 创建用户对象
                        mUser = new User(
                                Integer.valueOf(cursor.getInt(0)),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getString(6),
                                cursor.getString(7)
                        );
                    }
                }
                db.close(); // 关闭数据库

                // 根据查询结果进行判断
                if (mUser == null) {
                    Toast.makeText(LoginActivity.this, "账号不存在", Toast.LENGTH_SHORT).show(); // 提示账号不存在
                } else if (!password.equals(mUser.getPassword())) {
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show(); // 提示密码错误
                } else {
                    SPUtils.put(LoginActivity.this, SPUtils.USER_ID, mUser.getId()); // 保存用户ID到SharedPreferences
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class)); // 跳转到主活动
                    LoginActivity.this.finish(); // 关闭当前活动
                }
            } else { // 如果是默认的管理员账号
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, ManagerActivity.class)); // 跳转到管理活动
                LoginActivity.this.finish(); // 关闭当前活动
            }
        });
    }
}