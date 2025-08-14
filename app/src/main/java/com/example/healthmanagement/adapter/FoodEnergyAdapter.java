package com.example.healthmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.FoodEnergy;
import java.util.ArrayList;
import java.util.List;

public class FoodEnergyAdapter extends RecyclerView.Adapter<FoodEnergyAdapter.ViewHolder> {
    private List<FoodEnergy> list = new ArrayList<>();
    private Context mActivity;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        this.mActivity = context;
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_food_energy_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        FoodEnergy foodEnergy = this.list.get(i);
        if (foodEnergy != null) {
            viewHolder.tv_food.setText(foodEnergy.getFood());
            viewHolder.tv_quantity.setText(foodEnergy.getQuantity());
            viewHolder.tv_heat.setText(foodEnergy.getHeat());
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_food;
        private TextView tv_heat;
        private TextView tv_quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_food = itemView.findViewById(R.id.tv_food);
            this.tv_quantity = itemView.findViewById(R.id.tv_quantity);
            this.tv_heat = itemView.findViewById(R.id.tv_heat);
        }
    }
}
