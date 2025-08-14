package com.example.healthmanagement.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthmanagement.R;
import com.example.healthmanagement.adapter.MenuAdapter;
import com.example.healthmanagement.bean.Menu;
import com.example.healthmanagement.databinding.DialogEditMenuBinding;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.internal.cache.DiskLruCache;

public class MenuManagerActivity extends AppCompatActivity {
    MySqliteOpenHelper helper = null;
    private LinearLayout llEmpty;
    private MenuAdapter menuAdapter;
    private List<Menu> menuList;
    private RecyclerView rvList;
    private String[] state = {"0", DiskLruCache.VERSION_1, ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4"};
    private TabLayout tabTitle;
    private String[] title = {"早餐", "午餐", "下午茶", "晚餐", "夜宵"};
    private String typeId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_menu); // 设置布局
        findViewById(R.id.rl_title).setVisibility(View.VISIBLE); // 显示标题栏
        findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() { // 添加按钮点击事件
            public final void onClick(View view) {
                showAddDialog(); // 显示添加菜单对话框
            }
        });
        this.helper = new MySqliteOpenHelper(this); // 初始化数据库帮助类
        this.tabTitle = (TabLayout) findViewById(R.id.tab_title); // 获取 TabLayout
        this.rvList = (RecyclerView) findViewById(R.id.rv_menu_list); // 获取 RecyclerView
        this.llEmpty = (LinearLayout) findViewById(R.id.ll_empty); // 获取空视图
        initView(); // 初始化视图
    }

    private void showAddDialog() {
        final DialogEditMenuBinding binding = DialogEditMenuBinding.inflate(getLayoutInflater()); // 创建对话框布局
        final AlertDialog show = new AlertDialog.Builder(this).setView(binding.getRoot()).show(); // 显示对话框
        binding.btnCancel.setOnClickListener(new View.OnClickListener() { // 取消按钮点击事件
            public void onClick(View v) {
                show.dismiss(); // 关闭对话框
            }
        });
        binding.btnSure.setOnClickListener(new View.OnClickListener() { // 确定按钮点击事件
            public void onClick(View v) {
                String url = binding.etUrl.getText().toString(); // 获取 URL
                String img = binding.etImage.getText().toString(); // 获取图片链接
                String title = binding.etName.getText().toString(); // 获取标题
                // 确保所有字段都不为空
                if (!url.isEmpty() && !img.isEmpty() && !title.isEmpty()) {
                    Menu menu = new Menu(); // 创建新菜单项
                    menu.setUrl(url);
                    menu.setImg(img);
                    menu.setTitle(title);
                    menu.setTypeId(Integer.valueOf(Integer.parseInt(MenuManagerActivity.this.typeId))); // 设置类型 ID
                    MenuManagerActivity.this.helper.getWritableDatabase().insert("menu", null, menu.toContentValues()); // 插入数据库
                    show.dismiss(); // 关闭对话框
                    MenuManagerActivity.this.loadData(); // 重新加载数据
                }
            }
        });
    }

    private void initView() {
        this.tabTitle.setTabMode(TabLayout.MODE_FIXED); // 设置 Tab 模式为固定模式
        // 添加 Tab
        for (int i = 0; i < this.title.length; i++) {
            TabLayout tabLayout = this.tabTitle;
            tabLayout.addTab(tabLayout.newTab().setText(this.title[i])); // 添加 Tab
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this); // 创建线性布局管理器
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 设置垂直方向
        this.rvList.setLayoutManager(layoutManager); // 设置 RecyclerView 的布局管理器
        this.menuAdapter = new MenuAdapter(); // 创建适配器
        this.rvList.setAdapter(menuAdapter); // 设置适配器
        loadData(); // 加载数据

        // 设置适配器的项点击监听
        this.menuAdapter.setItemListener(new MenuAdapter.ItemListener() {
            @Override
            public void ItemClick(final Menu menu) {
                // 显示选项对话框
                new AlertDialog.Builder(MenuManagerActivity.this).setItems(new CharSequence[]{"查看详情", "编辑", "删除"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // 查看详情
                            Intent intent = new Intent(MenuManagerActivity.this, MenuDetailActivity.class);
                            intent.putExtra("url", menu.getUrl());
                            MenuManagerActivity.this.startActivity(intent);
                        } else if (which == 1) {
                            // 编辑菜单项
                            MenuManagerActivity.this.showEditDialog(menu);
                        } else if (which == 2) {
                            // 删除菜单项
                            MenuManagerActivity.this.helper.getWritableDatabase().delete("menu", "id = ?", new String[]{menu.getId() + ""});
                            MenuManagerActivity.this.loadData(); // 重新加载数据
                        }
                    }
                }).show(); // 显示对话框
            }
        });

        // 设置 Tab 选择监听
        this.tabTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                MenuManagerActivity menuManagerActivity = MenuManagerActivity.this;
                menuManagerActivity.typeId = menuManagerActivity.state[tab.getPosition()]; // 设置当前类型 ID
                MenuManagerActivity.this.loadData(); // 重新加载数据
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showEditDialog(final Menu menu) {
        final DialogEditMenuBinding binding = DialogEditMenuBinding.inflate(getLayoutInflater()); // 创建编辑对话框布局
        binding.etImage.setText(menu.getImg()); // 填充现有图片链接
        binding.etName.setText(menu.getTitle()); // 填充现有标题
        binding.etUrl.setText(menu.getUrl()); // 填充现有 URL
        final AlertDialog show = new AlertDialog.Builder(this).setView(binding.getRoot()).show(); // 显示对话框
        binding.btnCancel.setOnClickListener(new View.OnClickListener() { // 取消按钮点击事件
            public void onClick(View v) {
                show.dismiss(); // 关闭对话框
            }
        });
        binding.btnSure.setOnClickListener(new View.OnClickListener() { // 确定按钮点击事件
            public void onClick(View v) {
                String url = binding.etUrl.getText().toString(); // 获取 URL
                String img = binding.etImage.getText().toString(); // 获取图片链接
                String title = binding.etName.getText().toString(); // 获取标题
                // 确保所有字段都不为空
                if (!url.isEmpty() && !img.isEmpty() && !title.isEmpty()) {
                    menu.setUrl(url);
                    menu.setImg(img);
                    menu.setTitle(title);
                    // 更新数据库中的菜单项
                    MenuManagerActivity.this.helper.getWritableDatabase().update("menu", menu.toContentValues(), "id = ?", new String[]{menu.getId() + ""});
                    show.dismiss(); // 关闭对话框
                    MenuManagerActivity.this.loadData(); // 重新加载数据
                }
            }
        });
    }

    private void loadData() {
        this.menuList = new ArrayList(); // 初始化菜单列表
        Cursor cursor = this.helper.getWritableDatabase().rawQuery("select * from menu where typeId =" + this.typeId, null); // 查询菜单项
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                // 将查询结果添加到菜单列表
                this.menuList.add(new Menu(Integer.valueOf(cursor.getInt(0)), Integer.valueOf(cursor.getInt(1)), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            }
        }
        Collections.reverse(this.menuList); // 反转菜单列表顺序
        if (this.menuList == null || this.menuList.size() <= 0) {
            this.rvList.setVisibility(View.GONE); // 隐藏列表
            this.llEmpty.setVisibility(View.VISIBLE); // 显示空视图
            return;
        }
        this.rvList.setVisibility(View.VISIBLE); // 显示列表
        this.llEmpty.setVisibility(View.GONE); // 隐藏空视图
        this.menuAdapter.addItem(this.menuList); // 更新适配器
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // 在恢复活动时重新加载数据
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果返回结果是从添加菜单项的活动中
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadData(); // 重新加载数据
        }
    }

}
