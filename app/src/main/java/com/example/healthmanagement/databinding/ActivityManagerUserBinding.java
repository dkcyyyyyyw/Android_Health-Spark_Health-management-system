package com.example.healthmanagement.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.example.healthmanagement.R;

public final class ActivityManagerUserBinding implements ViewBinding {
    public final ImageView ivAdd;
    private final LinearLayout rootView;
    public final RecyclerView rvUser;
    public final TextView tvTitle;

    private ActivityManagerUserBinding(LinearLayout rootView2, ImageView ivAdd2, RecyclerView rvUser2, TextView tvTitle2) {
        this.rootView = rootView2;
        this.ivAdd = ivAdd2;
        this.rvUser = rvUser2;
        this.tvTitle = tvTitle2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ActivityManagerUserBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static ActivityManagerUserBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_manager_user, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityManagerUserBinding bind(View rootView2) {
        int id = R.id.iv_add;
        ImageView ivAdd2 = (ImageView) rootView2.findViewById(R.id.iv_add);
        if (ivAdd2 != null) {
            id = R.id.rv_user;
            RecyclerView rvUser2 = (RecyclerView) rootView2.findViewById(R.id.rv_user);
            if (rvUser2 != null) {
                id = R.id.tv_title;
                TextView tvTitle2 = (TextView) rootView2.findViewById(R.id.tv_title);
                if (tvTitle2 != null) {
                    return new ActivityManagerUserBinding((LinearLayout) rootView2, ivAdd2, rvUser2, tvTitle2);
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
