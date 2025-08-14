package com.example.healthmanagement.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmanagement.R;
import com.example.healthmanagement.adapter.MenuAdapter;
import com.example.healthmanagement.bean.Menu;
import com.example.healthmanagement.ui.activity.MenuDetailActivity;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;

public class MenuFragment extends Fragment {
    MySqliteOpenHelper helper = null;
    private LinearLayout llEmpty;
    private MenuAdapter menuAdapter;
    private List<Menu> menuList;
    private Activity myActivity;
    private RecyclerView rvList;
    private String[] state = {"0", DiskLruCache.VERSION_1, ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4"};
    private TabLayout tabTitle;
    private String[] title = {"早餐", "午餐", "下午茶", "晚餐", "夜宵"};
    private String typeId = "0";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.myActivity = (Activity) context;// 获取宿主Activity，用于UI操作和跳转
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载 fragment_menu 布局
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        this.helper = new MySqliteOpenHelper(this.myActivity); // 初始化数据库助手
        this.tabTitle = (TabLayout) view.findViewById(R.id.tab_title); // 获取 TabLayout
        this.rvList = (RecyclerView) view.findViewById(R.id.rv_menu_list); // 获取 RecyclerView
        this.llEmpty = (LinearLayout) view.findViewById(R.id.ll_empty); // 获取空布局
        initView(); // 初始化视图
        return view; // 返回视图
    }

    private void initView() {
        this.tabTitle.setTabMode(0); // 设置 TabLayout 模式
        for (int i = 0; i < this.title.length; i++) {
            // 为每个标题添加 Tab
            TabLayout tabLayout = this.tabTitle;
            tabLayout.addTab(tabLayout.newTab().setText(this.title[i]));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.myActivity); // 创建线性布局管理器
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 设置垂直方向
        this.rvList.setLayoutManager(layoutManager); // 设置 RecyclerView 的布局管理器
        MenuAdapter menuAdapter2 = new MenuAdapter(); // 创建菜单适配器
        this.menuAdapter = menuAdapter2;
        this.rvList.setAdapter(menuAdapter2); // 设置适配器
        loadData(); // 加载数据
        this.menuAdapter.setItemListener(new MenuAdapter.ItemListener() {
            @Override
            public void ItemClick(Menu menu) {
                // 点击菜单项时跳转到菜单详情页面
                Intent intent = new Intent(MenuFragment.this.myActivity, MenuDetailActivity.class);
                intent.putExtra("url", menu.getUrl()); // 传递 URL
                MenuFragment.this.startActivity(intent);
            }
        });
        // 设置 Tab 选中监听器
        this.tabTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 更新选中的类型 ID，并重新加载数据
                MenuFragment menuFragment = MenuFragment.this;
                menuFragment.typeId = menuFragment.state[tab.getPosition()];
                MenuFragment.this.loadData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Tab 取消选择时的处理
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Tab 重复选择时的处理
            }
        });
    }

    private void loadData() {
        this.menuList = new ArrayList<>(); // 初始化菜单列表
        // 从数据库中查询菜单数据
        Cursor cursor = this.helper.getWritableDatabase().rawQuery("select * from menu where typeId =" + this.typeId, null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                // 从游标中读取菜单数据
                this.menuList.add(new Menu(Integer.valueOf(cursor.getInt(0)), Integer.valueOf(cursor.getInt(1)), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
            }
        }
        Collections.reverse(this.menuList); // 反转菜单列表顺序
        List<Menu> list = this.menuList;
        if (list == null || list.size() <= 0) {
            // 如果菜单列表为空，显示空布局
            this.rvList.setVisibility(View.GONE);
            this.llEmpty.setVisibility(View.VISIBLE);
            return;
        }
        // 显示菜单列表和隐藏空布局
        this.rvList.setVisibility(View.VISIBLE);
        this.llEmpty.setVisibility(View.GONE);
        this.menuAdapter.addItem(this.menuList); // 更新适配器数据
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // 在活动恢复时重新加载数据
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理返回结果
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            loadData(); // 根据结果重新加载数据
        }
    }
}
