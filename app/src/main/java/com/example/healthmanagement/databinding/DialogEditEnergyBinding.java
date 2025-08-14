package com.example.healthmanagement.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.viewbinding.ViewBinding;
import com.example.healthmanagement.R;

public final class DialogEditEnergyBinding implements ViewBinding {
    public final Button btnCancel;
    public final Button btnSure;
    public final EditText etCarbon;
    public final EditText etFat;
    public final EditText etHeat;
    public final EditText etName;
    public final EditText etProtein;
    public final EditText etQuantity;
    private final LinearLayout rootView;

    private DialogEditEnergyBinding(LinearLayout rootView2, Button btnCancel2, Button btnSure2, EditText etCarbon2, EditText etFat2, EditText etHeat2, EditText etName2, EditText etProtein2, EditText etQuantity2) {
        this.rootView = rootView2;
        this.btnCancel = btnCancel2;
        this.btnSure = btnSure2;
        this.etCarbon = etCarbon2;
        this.etFat = etFat2;
        this.etHeat = etHeat2;
        this.etName = etName2;
        this.etProtein = etProtein2;
        this.etQuantity = etQuantity2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static DialogEditEnergyBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, null, false);
    }

    public static DialogEditEnergyBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.dialog_edit_energy, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static DialogEditEnergyBinding bind(View rootView2) {
        int id = R.id.btn_cancel;
        Button btnCancel2 = (Button) rootView2.findViewById(R.id.btn_cancel);
        if (btnCancel2 != null) {
            id = R.id.btn_sure;
            Button btnSure2 = (Button) rootView2.findViewById(R.id.btn_sure);
            if (btnSure2 != null) {
                id = R.id.et_carbon;
                EditText etCarbon2 = (EditText) rootView2.findViewById(R.id.et_carbon);
                if (etCarbon2 != null) {
                    id = R.id.et_fat;
                    EditText etFat2 = (EditText) rootView2.findViewById(R.id.et_fat);
                    if (etFat2 != null) {
                        id = R.id.et_heat;
                        EditText etHeat2 = (EditText) rootView2.findViewById(R.id.et_heat);
                        if (etHeat2 != null) {
                            id = R.id.et_name;
                            EditText etName2 = (EditText) rootView2.findViewById(R.id.et_name);
                            if (etName2 != null) {
                                id = R.id.et_protein;
                                EditText etProtein2 = (EditText) rootView2.findViewById(R.id.et_protein);
                                if (etProtein2 != null) {
                                    id = R.id.et_quantity;
                                    EditText etQuantity2 = (EditText) rootView2.findViewById(R.id.et_quantity);
                                    if (etQuantity2 != null) {
                                        return new DialogEditEnergyBinding((LinearLayout) rootView2, btnCancel2, btnSure2, etCarbon2, etFat2, etHeat2, etName2, etProtein2, etQuantity2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
