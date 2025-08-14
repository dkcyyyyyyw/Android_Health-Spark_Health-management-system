package com.example.healthmanagement.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import com.example.healthmanagement.R;
//用于绑定 activity_manager.xml 布局文件中的视图组件
public final class ActivityManagerBinding implements ViewBinding {
    public final LinearLayout llDiet;
    public final LinearLayout llFoodEnergy;
    public final LinearLayout llOut;
    public final LinearLayout llPerson;
    private final LinearLayout rootView;
    public final TextView tvTitle;

    private ActivityManagerBinding(LinearLayout rootView2, LinearLayout llDiet2, LinearLayout llFoodEnergy2, LinearLayout llOut2, LinearLayout llPerson2, TextView tvTitle2) {
        this.rootView = rootView2;
        this.llDiet = llDiet2;
        this.llFoodEnergy = llFoodEnergy2;
        this.llOut = llOut2;
        this.llPerson = llPerson2;
        this.tvTitle = tvTitle2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ActivityManagerBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ActivityManagerBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_manager, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityManagerBinding bind(View rootView2) {
        int id = R.id.ll_diet;
        LinearLayout llDiet2 = (LinearLayout) rootView2.findViewById(R.id.ll_diet);
        if (llDiet2 != null) {
            id = R.id.ll_food_energy;
            LinearLayout llFoodEnergy2 = (LinearLayout) rootView2.findViewById(R.id.ll_food_energy);
            if (llFoodEnergy2 != null) {
                id = R.id.ll_out;
                LinearLayout llOut2 = (LinearLayout) rootView2.findViewById(R.id.ll_out);
                if (llOut2 != null) {
                    id = R.id.ll_person;
                    LinearLayout llPerson2 = (LinearLayout) rootView2.findViewById(R.id.ll_person);
                    if (llPerson2 != null) {
                        id = R.id.tv_title;
                        TextView tvTitle2 = (TextView) rootView2.findViewById(R.id.tv_title);
                        if (tvTitle2 != null) {
                            return new ActivityManagerBinding((LinearLayout) rootView2, llDiet2, llFoodEnergy2, llOut2, llPerson2, tvTitle2);
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
