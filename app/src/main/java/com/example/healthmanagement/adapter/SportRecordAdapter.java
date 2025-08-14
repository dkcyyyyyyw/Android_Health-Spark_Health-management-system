package com.example.healthmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.SportRecord;
import java.util.ArrayList;
import java.util.List;

public class SportRecordAdapter extends RecyclerView.Adapter<SportRecordAdapter.ViewHolder> {
    private List<SportRecord> list = new ArrayList<>();
    private Context mActivity;
    private ItemListener mItemListener;

    public interface ItemListener {
        void ItemClick(SportRecord sportRecord);
        void ItemLongClick(SportRecord sportRecord);
    }

    public void setItemListener(ItemListener itemListener) {
        this.mItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mActivity = viewGroup.getContext();
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_rv_sport_record_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        SportRecord sportRecord = list.get(i);
        if (sportRecord != null) {
            viewHolder.tv_step.setText(String.format("步数：%s", sportRecord.getStep()));
            viewHolder.tv_remark.setText(String.format("备注：%s", sportRecord.getRemark()));
            viewHolder.tv_date.setText(String.format("日期：%s", sportRecord.getDate()));

            viewHolder.itemView.setOnClickListener(v -> {
                if (mItemListener != null) {
                    mItemListener.ItemClick(sportRecord);
                }
            });

            viewHolder.itemView.setOnLongClickListener(v -> {
                if (mItemListener != null) {
                    mItemListener.ItemLongClick(sportRecord);
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(List<SportRecord> listAdd) {
        list.clear();
        if (listAdd != null) {
            list.addAll(listAdd);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_date;
        private TextView tv_remark;
        private TextView tv_step;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_step = itemView.findViewById(R.id.tv_step);
            tv_remark = itemView.findViewById(R.id.tv_remark);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }
}
