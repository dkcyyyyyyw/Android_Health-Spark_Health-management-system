package com.example.healthmanagement.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import com.example.healthmanagement.R;

public final class ItemUserBinding implements ViewBinding {
    public final ImageView ivFace;
    private final LinearLayout rootView;
    public final TextView tvAccount;
    public final TextView tvAddress;
    public final TextView tvName;
    public final TextView tvPhone;
    public final TextView tvSex;

    private ItemUserBinding(LinearLayout rootView2, ImageView ivFace2, TextView tvAccount2, TextView tvAddress2, TextView tvName2, TextView tvPhone2, TextView tvSex2) {
        this.rootView = rootView2;
        this.ivFace = ivFace2;
        this.tvAccount = tvAccount2;
        this.tvAddress = tvAddress2;
        this.tvName = tvName2;
        this.tvPhone = tvPhone2;
        this.tvSex = tvSex2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ItemUserBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ItemUserBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.item_user, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ItemUserBinding bind(View rootView2) {
        int id = R.id.iv_face;
        ImageView ivFace2 = (ImageView) rootView2.findViewById(R.id.iv_face);
        if (ivFace2 != null) {
            id = R.id.tv_account;
            TextView tvAccount2 = (TextView) rootView2.findViewById(R.id.tv_account);
            if (tvAccount2 != null) {
                id = R.id.tv_address;
                TextView tvAddress2 = (TextView) rootView2.findViewById(R.id.tv_address);
                if (tvAddress2 != null) {
                    id = R.id.tv_name;
                    TextView tvName2 = (TextView) rootView2.findViewById(R.id.tv_name);
                    if (tvName2 != null) {
                        id = R.id.tv_phone;
                        TextView tvPhone2 = (TextView) rootView2.findViewById(R.id.tv_phone);
                        if (tvPhone2 != null) {
                            id = R.id.tv_sex;
                            TextView tvSex2 = (TextView) rootView2.findViewById(R.id.tv_sex);
                            if (tvSex2 != null) {
                                return new ItemUserBinding((LinearLayout) rootView2, ivFace2, tvAccount2, tvAddress2, tvName2, tvPhone2, tvSex2);
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
