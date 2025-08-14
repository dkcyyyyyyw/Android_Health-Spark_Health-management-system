package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthmanagement.R;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;

public class PasswordActivity extends AppCompatActivity {
    private Activity activity;
    private EditText etNewPassword;
    MySqliteOpenHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
        setContentView(R.layout.activity_password);
        this.helper = new MySqliteOpenHelper(this.activity);
        this.etNewPassword = (EditText) findViewById(R.id.et_new_password);
    }

    public void save(View v) {
        SQLiteDatabase db = this.helper.getWritableDatabase();

        String newPassword = this.etNewPassword.getText().toString();

        if (newPassword.isEmpty()) {
            Toast.makeText(this.activity, "新密码为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取用户 ID
        Integer userId = (Integer) SPUtils.get(this, SPUtils.USER_ID, 0);

        // 执行更新
        db.execSQL("UPDATE user SET password = ? WHERE id = ?", new Object[]{newPassword, userId});

        Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
        finish();
    }


    public void back(View view) {
        finish();
    }
}
