package com.example.healthmanagement.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.User;
import com.example.healthmanagement.util.DBUtils;
import com.example.healthmanagement.util.GlideEngine;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.PictureFileUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private Button btnRegister;
    private EditText etAccount, etAddress, etNickName, etPassword, etPasswordSure, etPhone;
    private RequestOptions headerRO = new RequestOptions().circleCrop();
    private MySqliteOpenHelper helper;
    private String imagePath = "";
    private boolean isManager = false;
    private ImageView ivPhoto;
    private RadioGroup rgSex;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        setupListeners();
    }

    private void initViews() {
        isManager = getIntent().getBooleanExtra("isManager", false);
        helper = new MySqliteOpenHelper(this);

        etAccount = findViewById(R.id.et_account);
        etNickName = findViewById(R.id.et_nickName);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etPassword = findViewById(R.id.et_password);
        etPasswordSure = findViewById(R.id.et_password_sure);
        ivPhoto = findViewById(R.id.iv_photo);
        rgSex = findViewById(R.id.rg_sex);
        tvLogin = findViewById(R.id.tv_login);
        btnRegister = findViewById(R.id.btn_register);

        if (isManager) {
            tvLogin.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        ivPhoto.setOnClickListener(v -> selectClick());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> handleRegistration());
    }

    private void handleRegistration() {
        String account = etAccount.getText().toString();
        String nickName = etNickName.getText().toString();
        String phone = etPhone.getText().toString();
        String address = etAddress.getText().toString();
        String password = etPassword.getText().toString();
        String passwordSure = etPasswordSure.getText().toString();
        String sex = rgSex.getCheckedRadioButtonId() == R.id.rb_man ? "男" : "女";

        if (!validateInput()) {
            return;
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        User existingUser = checkUserExists(db, account);

        if (existingUser == null) {
            long sqliteResult = insertIntoSQLite(db, account, password, nickName, sex, phone, address, imagePath);
            if (sqliteResult != -1) {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                new Thread(() -> insertIntoMySQL(account, password, nickName, sex, phone, address, imagePath)).start();
                finish();
            } else {
                Toast.makeText(this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "该账号已存在", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(imagePath)) {
            showToast("请上传头像");
            return false;
        } else if (TextUtils.isEmpty(etAccount.getText())) {
            showToast("用户名不能为空");
            return false;
        } else if (TextUtils.isEmpty(etNickName.getText())) {
            showToast("昵称不能为空");
            return false;
        } else if (TextUtils.isEmpty(etPhone.getText())) {
            showToast("手机号不能为空");
            return false;
        } else if (etPhone.length() != 11) {
            showToast("手机号格式错误");
            return false;
        } else if (TextUtils.isEmpty(etAddress.getText())) {
            showToast("地址不能为空");
            return false;
        } else if (TextUtils.isEmpty(etPassword.getText())) {
            showToast("密码不能为空");
            return false;
        } else if (!etPassword.getText().toString().equals(etPasswordSure.getText().toString())) {
            showToast("两次输入的密码不一致");
            return false;
        }
        return true;
    }

    private User checkUserExists(SQLiteDatabase db, String account) {
        User user = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM user WHERE account = ?", new String[]{account});
            if (cursor.moveToFirst()) {
                user = new User(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("account")),
                        cursor.getString(cursor.getColumnIndex("password")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("sex")),
                        cursor.getString(cursor.getColumnIndex("phone")),
                        cursor.getString(cursor.getColumnIndex("address")),
                        cursor.getString(cursor.getColumnIndex("photo"))
                );
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return user;
    }

    private long insertIntoSQLite(SQLiteDatabase db, String account, String password, String name, String sex, String phone, String address, String photo) {
        ContentValues values = new ContentValues();
        values.put("account", account);
        values.put("password", password);
        values.put("name", name);
        values.put("sex", sex);
        values.put("phone", phone);
        values.put("address", address);
        values.put("photo", photo);
        return db.insert("user", null, values);
    }

    private void insertIntoMySQL(String account, String password, String name, String sex, String phone, String address, String photo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtils.getConn();
            if (conn == null) {
                showToastOnUiThread("MySQL连接失败，数据可能未同步");
                return;
            }

            String sql = "INSERT INTO user (account, password, name, sex, phone, address, photo) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, account);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, sex);
            pstmt.setString(5, phone);
            pstmt.setString(6, address);
            pstmt.setString(7, photo);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                android.util.Log.d("MySQL", "数据同步成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showToastOnUiThread("MySQL同步失败: " + e.getMessage());
        } finally {
            closeResources(pstmt, conn);
        }
    }

    private void selectClick() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(1)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        if (result != null && !result.isEmpty()) {
                            LocalMedia media = result.get(0);
                            String path;

                            if (media.isCut() && !media.isCompressed()) {
                                path = media.getCutPath();
                            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                                path = media.getCompressPath();
                            } else if (!TextUtils.isEmpty(media.getAndroidQToPath())) {
                                path = media.getAndroidQToPath();
                            } else if (!TextUtils.isEmpty(media.getRealPath())) {
                                path = media.getRealPath();
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                path = PictureFileUtils.getPath(RegisterActivity.this, Uri.parse(media.getPath()));
                            } else {
                                path = media.getPath();
                            }

                            imagePath = path;
                            Glide.with(RegisterActivity.this).load(imagePath).apply(headerRO).into(ivPhoto);
                        }
                    }

                    @Override
                    public void onCancel() {
                        // 用户取消选择
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showToastOnUiThread(String message) {
        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    private void closeResources(PreparedStatement pstmt, Connection conn) {
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void back(View view) {
        finish();
    }
}