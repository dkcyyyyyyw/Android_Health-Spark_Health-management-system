package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.User;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;

public class PersonActivity extends AppCompatActivity {
    private Button btnSave;
    private EditText etAddress;
    private EditText etNickName;
    private EditText etPhone;
    MySqliteOpenHelper helper = null;
    private boolean isManager = false;
    private Activity mActivity;
    User mUser = null;
    private RadioGroup rgSex;
    private TextView tvAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        this.isManager = getIntent().getBooleanExtra("isManager", false);
        this.mActivity = this;
        this.helper = new MySqliteOpenHelper(this.mActivity);
        this.tvAccount = findViewById(R.id.tv_account);
        this.etNickName = findViewById(R.id.et_nickName);
        this.etPhone = findViewById(R.id.et_phone);
        this.etAddress = findViewById(R.id.et_address);
        this.rgSex = findViewById(R.id.rg_sex);
        this.btnSave = findViewById(R.id.btn_save);
        initView();
    }

    private void initView() {
        Integer userId;
        // 根据是否是管理员选择获取用户 ID 的方式
        if (this.isManager) {
            userId = getIntent().getIntExtra("userid", 1); // 从 Intent 中获取用户 ID
        } else {
            userId = (Integer) SPUtils.get(this.mActivity, SPUtils.USER_ID, 1); // 从 SharedPreferences 获取用户 ID
        }

        SQLiteDatabase db = this.helper.getWritableDatabase(); // 获取可写数据库
        // 查询用户信息
        Cursor cursor = db.rawQuery("select * from user where id = ?", new String[]{String.valueOf(userId)});
        // 检查查询结果
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                // 从游标中获取用户信息
                Integer dbId = cursor.getInt(0);
                String dbAccount = cursor.getString(1);
                String dbPassword = cursor.getString(2);
                String dbName = cursor.getString(3);
                String dbSex = cursor.getString(4);
                String dbPhone = cursor.getString(5);
                String dbAddress = cursor.getString(6);
                String dbPhoto = cursor.getString(7);

                // 设置 UI 组件的文本
                this.tvAccount.setText(dbAccount);
                this.etNickName.setText(dbName);
                this.etPhone.setText(dbPhone);
                this.etAddress.setText(dbAddress);
                // 根据性别选择相应的单选按钮
                this.rgSex.check("男".equals(dbSex) ? R.id.rb_man : R.id.rb_woman);
                // 创建 User 对象
                this.mUser = new User(dbId, dbAccount, dbPassword, dbName, dbSex, dbPhone, dbAddress, dbPhoto);
            }
        }
        db.close(); // 关闭数据库连接

        // 设置保存按钮的点击事件
        this.btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = PersonActivity.this.helper.getWritableDatabase(); // 获取可写数据库
                String nickName = PersonActivity.this.etNickName.getText().toString(); // 获取昵称
                String phone = PersonActivity.this.etPhone.getText().toString(); // 获取手机号
                String address = PersonActivity.this.etAddress.getText().toString(); // 获取地址

                // 验证输入
                if ("".equals(nickName)) {
                    Toast.makeText(PersonActivity.this.mActivity, "昵称不能为空", Toast.LENGTH_SHORT).show();
                } else if ("".equals(phone)) {
                    Toast.makeText(PersonActivity.this.mActivity, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else if (phone.length() != 11) {
                    Toast.makeText(PersonActivity.this.mActivity, "手机号格式错误", Toast.LENGTH_SHORT).show();
                } else if ("".equals(address)) {
                    Toast.makeText(PersonActivity.this.mActivity, "地址不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    // 更新用户信息
                    db.execSQL("update user set name = ?, phone = ?, address = ?, sex = ? where id = ?", new Object[]{
                            nickName,
                            phone,
                            address,
                            PersonActivity.this.rgSex.getCheckedRadioButtonId() == R.id.rb_man ? "男" : "女",
                            PersonActivity.this.mUser.getId() // 获取当前用户 ID
                    });
                    Toast.makeText(PersonActivity.this, "更新成功", Toast.LENGTH_SHORT).show(); // 显示更新成功的提示
                    db.close(); // 关闭数据库连接
                    PersonActivity.this.setResult(Activity.RESULT_OK); // 设置结果为成功
                    PersonActivity.this.finish(); // 结束当前活动
                }
            }
        });
    }


    public void back(View view) {
        finish();
    }
}
