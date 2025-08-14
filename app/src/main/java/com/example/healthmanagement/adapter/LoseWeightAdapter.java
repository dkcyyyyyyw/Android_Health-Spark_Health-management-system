package com.example.healthmanagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.LoseWeightBean;
import com.example.healthmanagement.ui.activity.LoseWeightDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class LoseWeightAdapter extends RecyclerView.Adapter<LoseWeightAdapter.ViewHolder> {
    private List<LoseWeightBean> list = new ArrayList<>();
    private Context mActivity;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        this.mActivity = context;
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_lose_weight_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final LoseWeightBean loseWeightBean = this.list.get(i);
        if (loseWeightBean != null) {
            viewHolder.title.setText(loseWeightBean.getTitle());
            viewHolder.author_name.setText(loseWeightBean.getAuthor_name());
            viewHolder.date.setText(loseWeightBean.getDate());
            Glide.with(this.mActivity).load(loseWeightBean.getThumbnail_pic_s()).into(viewHolder.thumbnail_pic_s);
            viewHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mActivity, LoseWeightDetailActivity.class);
                intent.putExtra("url", loseWeightBean.getUrl());
                mActivity.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void addItem(List<LoseWeightBean> listAdd) {
        this.list.clear();
        if (listAdd != null) {
            this.list.addAll(listAdd);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView author_name;
        private TextView date;
        private ImageView thumbnail_pic_s;
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            author_name = itemView.findViewById(R.id.author_name);
            date = itemView.findViewById(R.id.date);
            thumbnail_pic_s = itemView.findViewById(R.id.thumbnail_pic_s);
        }
    }
}
