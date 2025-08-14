package com.example.healthmanagement.adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public class BindHolder<VB extends ViewBinding> extends RecyclerView.ViewHolder {
    private VB vb;

    public BindHolder(VB vb2) {
        super(vb2.getRoot());
        this.vb = vb2;
    }

    public VB getVb() {
        return this.vb;
    }
}
