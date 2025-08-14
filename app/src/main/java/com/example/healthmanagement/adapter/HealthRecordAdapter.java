package com.example.healthmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.HealthRecord;
import java.util.ArrayList;
import java.util.List;

public class HealthRecordAdapter extends RecyclerView.Adapter<HealthRecordAdapter.ViewHolder> {
    private List<HealthRecord> list = new ArrayList<>();
    private Context mActivity;
    private ItemListener mItemListener;

    public interface ItemListener {
        void ItemClick(HealthRecord healthRecord);
        void ItemLongClick(HealthRecord healthRecord);
    }

    public void setItemListener(ItemListener itemListener) {
        this.mItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        this.mActivity = context;
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_health_record_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final HealthRecord healthRecord = this.list.get(i);
        if (healthRecord != null) {
            viewHolder.tv_heartRate.setText(String.format("心率：%s", healthRecord.getHeartRate()));
            viewHolder.tv_kcal.setText(String.format("卡路里：%s", healthRecord.getKcal()));
            viewHolder.tv_weight.setText(String.format("体重：%s", healthRecord.getWeight()));
            viewHolder.tv_date.setText(String.format("记录日期：%s", healthRecord.getDate()));
            viewHolder.itemView.setOnClickListener(v -> {
                if (mItemListener != null) {
                    mItemListener.ItemClick(healthRecord);
                }
            });
            viewHolder.itemView.setOnLongClickListener(v -> {
                if (mItemListener != null) {
                    mItemListener.ItemLongClick(healthRecord);
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void addItem(List<HealthRecord> listAdd) {
        this.list.clear();
        if (listAdd != null) {
            this.list.addAll(listAdd);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_date;
        private TextView tv_heartRate;
        private TextView tv_kcal;
        private TextView tv_weight;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_heartRate = itemView.findViewById(R.id.tv_heartRate);
            tv_kcal = itemView.findViewById(R.id.tv_kcal);
            tv_weight = itemView.findViewById(R.id.tv_weight);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }
}
