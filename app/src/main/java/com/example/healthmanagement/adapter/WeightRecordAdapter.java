package com.example.healthmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.WeightRecord;
import java.util.ArrayList;
import java.util.List;

public class WeightRecordAdapter extends RecyclerView.Adapter<WeightRecordAdapter.ViewHolder> {
    private List<WeightRecord> list = new ArrayList<>();
    private Context mActivity;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mActivity = viewGroup.getContext();
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_rv_weight_record_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        WeightRecord weightRecord = list.get(i);
        if (weightRecord != null) {
            viewHolder.tv_weight.setText(String.format("%s KG", weightRecord.getWeight()));
            viewHolder.tv_time.setText(String.format("%s %s", weightRecord.getDate(), weightRecord.getTime()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(List<WeightRecord> listAdd) {
        list.clear();
        if (listAdd != null) {
            list.addAll(listAdd);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_time;
        private TextView tv_weight;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_weight = itemView.findViewById(R.id.tv_weight);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
