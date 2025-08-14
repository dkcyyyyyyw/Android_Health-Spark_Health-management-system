package com.example.healthmanagement.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.User;
import com.example.healthmanagement.ui.activity.LoginActivity;
import com.example.healthmanagement.ui.activity.PasswordActivity;
import com.example.healthmanagement.ui.activity.PersonActivity;
import com.example.healthmanagement.util.GlideEngine;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.util.List;

public class UserFragment extends Fragment {
    private Button btnLogout;
    private RequestOptions headerRO = ((RequestOptions) new RequestOptions().circleCrop());
    MySqliteOpenHelper helper = null;
    private String imagePath = "";
    private ImageView ivPhoto;
    private LinearLayout llPerson;
    private LinearLayout llSecurity;
    private Activity mActivity;
    private User mUser = null;
    private TextView tvNickName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载 fragment_user 布局
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        this.helper = new MySqliteOpenHelper(this.mActivity); // 初始化数据库助手
        this.ivPhoto = (ImageView) view.findViewById(R.id.iv_photo); // 获取头像 ImageView
        this.tvNickName = (TextView) view.findViewById(R.id.tv_nickName); // 获取昵称 TextView
        this.llPerson = (LinearLayout) view.findViewById(R.id.person); // 获取个人信息布局
        this.llSecurity = (LinearLayout) view.findViewById(R.id.security); // 获取安全设置布局
        this.btnLogout = (Button) view.findViewById(R.id.logout); // 获取注销按钮
        initData(); // 初始化数据
        initView(); // 初始化视图
        return view; // 返回视图
    }

    private void initData() {
        int i = 0;
        SQLiteDatabase db = this.helper.getWritableDatabase(); // 打开可写数据库
        Cursor cursor = db.rawQuery("select * from user where id = ?", new String[]{String.valueOf((Integer) SPUtils.get(this.mActivity, SPUtils.USER_ID, 0))});
        // 查询当前用户信息
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                // 从游标中读取用户数据
                this.mUser = new User(Integer.valueOf(cursor.getInt(i)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
                i = 0;
            }
        }
        db.close(); // 关闭数据库
        this.tvNickName.setText(this.mUser.getName()); // 设置昵称
        // 使用 Glide 加载用户头像
        Glide.with(this.mActivity)
                .load(this.mUser.getPhoto())
                .apply(this.headerRO.error("男".equals(this.mUser.getSex()) ? R.drawable.ic_default_man : R.drawable.ic_default_woman))
                .into(this.ivPhoto);
    }

    private void initView() {
        // 设置头像点击事件
        this.ivPhoto.setOnClickListener(v -> {
            UserFragment.this.selectClick();
        });
        // 设置个人信息点击事件
        this.llPerson.setOnClickListener(v -> {
            UserFragment.this.startActivity(new Intent(UserFragment.this.mActivity, PersonActivity.class));
        });
        // 设置安全设置点击事件
        this.llSecurity.setOnClickListener(v -> {
            UserFragment.this.startActivity(new Intent(UserFragment.this.mActivity, PasswordActivity.class));
        });
        // 设置注销按钮点击事件
        this.btnLogout.setOnClickListener(v -> {
            SPUtils.remove(UserFragment.this.mActivity, SPUtils.USER_ID); // 清除用户 ID
            SPUtils.remove(UserFragment.this.mActivity, SPUtils.IF_ADMIN); // 清除管理员标志
            UserFragment.this.startActivity(new Intent(UserFragment.this.mActivity, LoginActivity.class)); // 跳转到登录页面
            UserFragment.this.mActivity.finish(); // 关闭当前活动
        });
    }

    private void selectClick() {
        final SQLiteDatabase db = this.helper.getWritableDatabase(); // 打开可写数据库
        // 打开图片选择器
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(1)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        String path;
                        int i = 0;
                        while (true) {
                            boolean isCutPath = false;
                            if (i >= result.size()) {
                                break; // 如果已处理所有结果，退出循环
                            }
                            LocalMedia media = result.get(i);
                            boolean compressPath = media.isCompressed() || (media.isCut() && media.isCompressed());
                            if (media.isCut() && !media.isCompressed()) {
                                isCutPath = true; // 标记为剪裁路径
                            }
                            // 根据条件获取图片路径
                            if (isCutPath) {
                                path = media.getCutPath();
                            } else if (compressPath) {
                                path = media.getCompressPath();
                            } else if (!TextUtils.isEmpty(media.getAndroidQToPath())) {
                                path = media.getAndroidQToPath();
                            } else if (!TextUtils.isEmpty(media.getRealPath())) {
                                path = media.getRealPath();
                            } else if (Build.VERSION.SDK_INT >= 29) {
                                path = PictureFileUtils.getPath(UserFragment.this.mActivity, Uri.parse(media.getPath()));
                            } else {
                                path = media.getPath();
                            }
                            UserFragment.this.imagePath = path; // 保存图片路径
                            i++;
                        }
                        // 使用 Glide 加载新的头像
                        Glide.with(UserFragment.this.mActivity)
                                .load(UserFragment.this.imagePath)
                                .apply(UserFragment.this.headerRO.error("男".equals(UserFragment.this.mUser.getSex()) ? R.drawable.ic_default_man : R.drawable.ic_default_woman))
                                .into(UserFragment.this.ivPhoto);
                        // 更新数据库中的用户头像
                        db.execSQL("update user set photo = ? where id = ?", new Object[]{UserFragment.this.imagePath, UserFragment.this.mUser.getId()});
                        Toast.makeText(UserFragment.this.mActivity, "更新成功", Toast.LENGTH_SHORT).show(); // 显示更新成功的提示
                    }

                    @Override
                    public void onCancel() {
                        // 处理取消选择
                    }
                });
    }
}
