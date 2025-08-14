package com.example.healthmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.FoodEnergy;
import java.util.ArrayList;
import java.util.List;

public class AddFoodEnergyAdapter extends RecyclerView.Adapter<AddFoodEnergyAdapter.ViewHolder> {
    private boolean isManager;
    private List<FoodEnergy> list;
    private Context mActivity;
    private ItemListener mItemListener;
    private Integer type;

    public interface ItemListener {
        void ItemClick(FoodEnergy foodEnergy);
    }

    public void setItemListener(ItemListener itemListener) {
        this.mItemListener = itemListener;
    }

    public AddFoodEnergyAdapter(int type2) {
        this(type2, false);
    }

    public AddFoodEnergyAdapter(int type2, boolean isManager2) {
        this.list = new ArrayList<>();
        this.type = type2;
        this.isManager = isManager2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        this.mActivity = context;
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_food_energy_add_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final FoodEnergy foodEnergy = this.list.get(i);
        if (foodEnergy != null) {
            viewHolder.tv_food.setText(foodEnergy.getFood());
            viewHolder.tv_quantity.setText(foodEnergy.getQuantity());
            viewHolder.tv_heat.setText(foodEnergy.getHeat());
            viewHolder.image.setImageResource(this.type == 0 ? R.drawable.i_add : R.drawable.i_sub);
            if (this.isManager) {
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.itemView.setOnClickListener(v -> {
                    if (this.mItemListener != null) {
                        this.mItemListener.ItemClick(foodEnergy);
                    }
                });
            }
            viewHolder.image.setOnClickListener(v -> {
                if (this.mItemListener != null) {
                    this.mItemListener.ItemClick(foodEnergy);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void addItem(List<FoodEnergy> listAdd) {
        this.list.clear();
        if (listAdd != null) {
            this.list.addAll(listAdd);
        }
        notifyDataSetChanged();
    }

    public void addItem(FoodEnergy foodEnergy) {
        if (foodEnergy != null) {
            this.list.add(foodEnergy);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView tv_food;
        private TextView tv_heat;
        private TextView tv_quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_food = itemView.findViewById(R.id.tv_food);
            this.tv_quantity = itemView.findViewById(R.id.tv_quantity);
            this.tv_heat = itemView.findViewById(R.id.tv_heat);
            this.image = itemView.findViewById(R.id.image);
        }
    }
}
