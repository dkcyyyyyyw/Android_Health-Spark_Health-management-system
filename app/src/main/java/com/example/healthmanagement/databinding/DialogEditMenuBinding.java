package com.example.healthmanagement.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.viewbinding.ViewBinding;
import com.example.healthmanagement.R;

public final class DialogEditMenuBinding implements ViewBinding {
    public final Button btnCancel;
    public final Button btnSure;
    public final EditText etImage;
    public final EditText etName;
    public final EditText etUrl;
    private final LinearLayout rootView;

    private DialogEditMenuBinding(LinearLayout rootView2, Button btnCancel2, Button btnSure2, EditText etImage2, EditText etName2, EditText etUrl2) {
        this.rootView = rootView2;
        this.btnCancel = btnCancel2;
        this.btnSure = btnSure2;
        this.etImage = etImage2;
        this.etName = etName2;
        this.etUrl = etUrl2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static DialogEditMenuBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static DialogEditMenuBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.dialog_edit_menu, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static DialogEditMenuBinding bind(View rootView2) {
        int id = R.id.btn_cancel;
        Button btnCancel2 = (Button) rootView2.findViewById(R.id.btn_cancel);
        if (btnCancel2 != null) {
            id = R.id.btn_sure;
            Button btnSure2 = (Button) rootView2.findViewById(R.id.btn_sure);
            if (btnSure2 != null) {
                id = R.id.et_image;
                EditText etImage2 = (EditText) rootView2.findViewById(R.id.et_image);
                if (etImage2 != null) {
                    id = R.id.et_name;
                    EditText etName2 = (EditText) rootView2.findViewById(R.id.et_name);
                    if (etName2 != null) {
                        id = R.id.et_url;
                        EditText etUrl2 = (EditText) rootView2.findViewById(R.id.et_url);
                        if (etUrl2 != null) {
                            return new DialogEditMenuBinding((LinearLayout) rootView2, btnCancel2, btnSure2, etImage2, etName2, etUrl2);
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
