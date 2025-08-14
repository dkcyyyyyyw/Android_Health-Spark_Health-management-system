package com.example.healthmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.EatRecordVo;
import com.example.healthmanagement.enums.TimeBucketEnum;
import java.util.ArrayList;
import java.util.List;

public class EatRecordAdapter extends RecyclerView.Adapter<EatRecordAdapter.ViewHolder> {
    private Integer[] images = {
            R.drawable.ic_breakfast,
            R.drawable.ic_lunch,
            R.drawable.ic_dinner,
            R.drawable.ic_snacks
    };
    private List<EatRecordVo> list = new ArrayList<>();
    private Context mActivity;
    private ItemListener mItemListener;

    public interface ItemListener {
        void ItemClick(EatRecordVo eatRecordVo);
        void ItemLongClick(EatRecordVo eatRecordVo);
    }

    public void setItemListener(ItemListener itemListener) {
        this.mItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        this.mActivity = context;
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_eat_record_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final EatRecordVo eatRecordVo = this.list.get(i);
        if (eatRecordVo != null) {
            viewHolder.tvName.setText(TimeBucketEnum.getName(eatRecordVo.getTimeId()));
            viewHolder.tvTips.setText(String.format("建议摄入%s大卡", TimeBucketEnum.getTips(eatRecordVo.getTimeId())));
            viewHolder.tvKcal.setText(String.format("已摄入%.2f大卡", eatRecordVo.getKcal()));
            viewHolder.image.setImageResource(images[eatRecordVo.getTimeId()]);
            viewHolder.itemView.setOnClickListener(view -> {
                if (mItemListener != null) {
                    mItemListener.ItemClick(eatRecordVo);
                }
            });
            viewHolder.itemView.setOnLongClickListener(view -> {
                if (mItemListener != null) {
                    mItemListener.ItemLongClick(eatRecordVo);
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

    public void addItem(List<EatRecordVo> listAdd) {
        list.clear();
        if (listAdd != null) {
            list.addAll(listAdd);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView tvKcal;
        private TextView tvName;
        private TextView tvTips;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTips = itemView.findViewById(R.id.tv_tips);
            tvKcal = itemView.findViewById(R.id.tv_kcal);
            image = itemView.findViewById(R.id.image);
        }
    }
}
