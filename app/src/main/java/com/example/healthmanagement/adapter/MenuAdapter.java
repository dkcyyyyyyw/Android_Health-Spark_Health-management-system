package com.example.healthmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.Menu;
import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private List<Menu> list = new ArrayList<>();
    private Context mActivity;
    private ItemListener mItemListener;

    public interface ItemListener {
        void ItemClick(Menu menu);
    }

    public void setItemListener(ItemListener itemListener) {
        this.mItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mActivity = viewGroup.getContext();
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_rv_menu_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Menu menu = list.get(i);
        if (menu != null) {
            viewHolder.title.setText(menu.getTitle());
            Glide.with(mActivity).asBitmap().load(menu.getImg()).into(viewHolder.img);
            viewHolder.itemView.setOnClickListener(v -> {
                if (mItemListener != null) {
                    mItemListener.ItemClick(menu);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(List<Menu> listAdd) {
        list.clear();
        if (listAdd != null) {
            list.addAll(listAdd);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
        }
    }
}
