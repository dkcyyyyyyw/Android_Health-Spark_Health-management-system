package com.example.healthmanagement.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import java.util.ArrayList;
import java.util.List;

public abstract class BindAdapter<VB extends ViewBinding, Data> extends RecyclerView.Adapter<BindHolder<VB>> {
    private List<Data> data = new ArrayList<>();

    // 绑定数据的方法
    public abstract void bind(VB vb, Data dataItem, int position);

    // 创建 ViewHolder 的方法
    public abstract VB createHolder(ViewGroup parent);

    // 重写 onBindViewHolder 方法，确保使用类型安全的 ViewHolder
    @Override
    public void onBindViewHolder(BindHolder<VB> holder, int position) {
        bind(holder.getVb(), data.get(position), position);
    }

    // 创建 ViewHolder
    @Override
    public BindHolder<VB> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BindHolder<>(createHolder(parent));
    }

    // 返回数据项数量
    @Override
    public int getItemCount() {
        return data.size();
    }

    // 获取数据列表
    public List<Data> getData() {
        return data;
    }

    // 设置数据列表，并通知 RecyclerView 更新
    public void setData(List<Data> newData) {
        data.clear();
        if (newData != null) {
            data.addAll(newData);
        }
        notifyDataSetChanged();
    }
}
