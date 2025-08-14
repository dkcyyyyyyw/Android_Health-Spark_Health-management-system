package com.example.healthmanagement.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.healthmanagement.adapter.BindAdapter;
import com.example.healthmanagement.bean.User;
import com.example.healthmanagement.databinding.ActivityManagerUserBinding;
import com.example.healthmanagement.databinding.ItemUserBinding;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;

public class ManagerUserActivity extends AppCompatActivity {
    // 创建适配器，用于绑定用户数据
    private BindAdapter<ItemUserBinding, User> adapter = new BindAdapter<ItemUserBinding, User>() {

        // 创建 ViewHolder
        @Override
        public ItemUserBinding createHolder(ViewGroup parent) {
            return ItemUserBinding.inflate(ManagerUserActivity.this.getLayoutInflater(), parent, false);
        }

        // 绑定用户数据到视图
        public void bind(ItemUserBinding itemUserBinding, final User user, int position) {
            // 加载用户头像
            Glide.with(itemUserBinding.ivFace).load(user.getPhoto()).into(itemUserBinding.ivFace);
            // 设置用户信息
            itemUserBinding.tvName.setText(user.getName());
            itemUserBinding.tvAccount.setText(user.getAccount());
            itemUserBinding.tvAddress.setText(user.getAddress());
            itemUserBinding.tvPhone.setText(user.getPhone());
            itemUserBinding.tvSex.setText(user.getSex());

            // 设置点击事件
            itemUserBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // 弹出编辑和删除对话框
                    new AlertDialog.Builder(ManagerUserActivity.this)
                            .setItems(new CharSequence[]{"编辑", "删除"}, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        // 编辑用户信息
                                        ManagerUserActivity.this.startActivity(new Intent(ManagerUserActivity.this, PersonActivity.class)
                                                .putExtra("isManager", true)
                                                .putExtra("userid", user.getId()));
                                    } else if (which == 1) {
                                        // 删除用户
                                        ManagerUserActivity.this.helper.getWritableDatabase()
                                                .delete("user", "id = ?", new String[]{String.valueOf(user.getId())});
                                        ManagerUserActivity.this.refresh(); // 刷新列表
                                    }
                                }
                            }).show(); // 显示对话框
                }
            });
        }
    };
    private ActivityManagerUserBinding binding;
    private MySqliteOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerUserBinding inflate = ActivityManagerUserBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        this.helper = new MySqliteOpenHelper(this);
        this.binding.rvUser.setAdapter(this.adapter);
        this.binding.ivAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ManagerUserActivity.this.startActivity(new Intent(ManagerUserActivity.this, RegisterActivity.class)
                        .putExtra("isManager", true));
            }
        });
    }

    public void back(View view) {
        finish();
    }

    public void refresh() {
        this.adapter.getData().clear();
        Cursor cursor = this.helper.getWritableDatabase().rawQuery("select * from user", null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                this.adapter.getData().add(new User(
                        Integer.valueOf(cursor.getInt(cursor.getColumnIndex("id"))),
                        cursor.getString(cursor.getColumnIndex(SPUtils.IF_ADMIN)),
                        cursor.getString(cursor.getColumnIndex("password")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("sex")),
                        cursor.getString(cursor.getColumnIndex("phone")),
                        cursor.getString(cursor.getColumnIndex("address")),
                        cursor.getString(cursor.getColumnIndex("photo"))
                ));
            }
        }
        this.adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}
