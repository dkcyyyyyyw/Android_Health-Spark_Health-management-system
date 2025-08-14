package com.example.healthmanagement.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmanagement.R;
import com.example.healthmanagement.adapter.AddFoodEnergyAdapter;
import com.example.healthmanagement.bean.FoodEnergy;
import com.example.healthmanagement.databinding.DialogEditEnergyBinding;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;

public class AddFoodEnergyActivity extends AppCompatActivity {
    private static final int VISIBLE = View.VISIBLE;
    private static final int GONE = View.GONE;
    private EditText etSearch;
    private AddFoodEnergyAdapter foodEnergyAdapter;
    private List<FoodEnergy> foodEnergyList;
    MySqliteOpenHelper helper = null;
    private boolean isManager = false;
    private LinearLayout llEmpty;
    private Activity myActivity;
    private RecyclerView rvList;
    private String[] state = {"0", DiskLruCache.VERSION_1, ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4", "5"};
    private TabLayout tabTitle;
    private String[] title = {"五谷类", "肉类", "蛋类", "蔬果类", "海鲜类", "奶类"};
    private String typeId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_energy);
        boolean booleanExtra = getIntent().getBooleanExtra("isManager", false);
        this.isManager = booleanExtra;
        if (booleanExtra) {
            View viewById = findViewById(R.id.iv_add);
            viewById.setVisibility(VISIBLE);
            viewById.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AddFoodEnergyActivity.this.showAddDialog();
                }
            });
        }
        this.myActivity = this;
        this.helper = new MySqliteOpenHelper(this);
        this.tabTitle = (TabLayout) findViewById(R.id.tab_title);
        this.etSearch = (EditText) findViewById(R.id.et_search);
        this.rvList = (RecyclerView) findViewById(R.id.rv_list);
        this.llEmpty = (LinearLayout) findViewById(R.id.ll_empty);
        this.etSearch.setVisibility(VISIBLE);
        initView();
        this.etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                AddFoodEnergyActivity.this.loadData();
            }
        });
    }

    private void showAddDialog() {
        final DialogEditEnergyBinding binding = DialogEditEnergyBinding.inflate(getLayoutInflater());
        final AlertDialog show = new AlertDialog.Builder(this).setView(binding.getRoot()).show();
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                show.dismiss();
            }
        });
        binding.btnSure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String carbon = binding.etCarbon.getText().toString();
                String fat = binding.etFat.getText().toString();
                String heat = binding.etHeat.getText().toString();
                String name = binding.etName.getText().toString();
                String protein = binding.etProtein.getText().toString();
                String quantity = binding.etQuantity.getText().toString();
                if (!carbon.isEmpty() && !fat.isEmpty() && !heat.isEmpty() && !name.isEmpty() && !protein.isEmpty() && !quantity.isEmpty()) {
                    FoodEnergy foodEnergy = new FoodEnergy();
                    foodEnergy.setTypeId(Integer.valueOf(Integer.parseInt(AddFoodEnergyActivity.this.typeId)));
                    foodEnergy.setFood(name);
                    foodEnergy.setCarbon(Double.valueOf(Double.parseDouble(carbon)));
                    foodEnergy.setFat(Double.valueOf(Double.parseDouble(fat)));
                    foodEnergy.setHeat(heat);
                    foodEnergy.setProtein(Double.valueOf(Double.parseDouble(protein)));
                    foodEnergy.setQuantity(quantity);
                    AddFoodEnergyActivity.this.helper.getWritableDatabase().insert("food_energy", null, foodEnergy.toContentValues());
                    show.dismiss();
                    AddFoodEnergyActivity.this.loadData();
                }
            }
        });
    }

    private void initView() {
        this.tabTitle.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (int i = 0; i < this.title.length; i++) {
            TabLayout tabLayout = this.tabTitle;
            tabLayout.addTab(tabLayout.newTab().setText(this.title[i]));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.myActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.rvList.setLayoutManager(layoutManager);
        AddFoodEnergyAdapter addFoodEnergyAdapter = new AddFoodEnergyAdapter(0, this.isManager);
        this.foodEnergyAdapter = addFoodEnergyAdapter;
        this.rvList.setAdapter(addFoodEnergyAdapter);
        loadData();
        this.tabTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                AddFoodEnergyActivity addFoodEnergyActivity = AddFoodEnergyActivity.this;
                addFoodEnergyActivity.typeId = addFoodEnergyActivity.state[tab.getPosition()];
                AddFoodEnergyActivity.this.loadData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        this.foodEnergyAdapter.setItemListener(new AddFoodEnergyAdapter.ItemListener() {
            @Override
            public void ItemClick(final FoodEnergy foodEnergy) {
                if (AddFoodEnergyActivity.this.isManager) {
                    new AlertDialog.Builder(AddFoodEnergyActivity.this).setItems(new CharSequence[]{"编辑", "删除"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                AddFoodEnergyActivity.this.showEditDialog(foodEnergy);
                            } else if (which == 1) {
                                AddFoodEnergyActivity.this.helper.getWritableDatabase().delete("food_energy", "id = ?", new String[]{foodEnergy.getId() + ""});
                                AddFoodEnergyActivity.this.loadData();
                            }
                        }
                    }).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("foodEnergy", foodEnergy);
                SQLiteDatabase writableDatabase = AddFoodEnergyActivity.this.helper.getWritableDatabase();
                foodEnergy.setCount(foodEnergy.getCount() + 1);
                writableDatabase.update("food_energy", foodEnergy.toContentValues(), "id = ?", new String[]{foodEnergy.getId() + ""});
                AddFoodEnergyActivity.this.setResult(Activity.RESULT_OK, intent);
                AddFoodEnergyActivity.this.finish();
            }
        });
    }

    private void showEditDialog(final FoodEnergy foodEnergy) {
        final DialogEditEnergyBinding binding = DialogEditEnergyBinding.inflate(getLayoutInflater());
        binding.etCarbon.setText(foodEnergy.getCarbon() + "");
        binding.etFat.setText(foodEnergy.getFat() + "");
        binding.etHeat.setText(foodEnergy.getHeat());
        binding.etName.setText(foodEnergy.getFood());
        binding.etProtein.setText(foodEnergy.getProtein() + "");
        binding.etQuantity.setText(foodEnergy.getQuantity());
        final AlertDialog show = new AlertDialog.Builder(this).setView(binding.getRoot()).show();
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                show.dismiss();
            }
        });
        binding.btnSure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String carbon = binding.etCarbon.getText().toString();
                String fat = binding.etFat.getText().toString();
                String heat = binding.etHeat.getText().toString();
                String name = binding.etName.getText().toString();
                String protein = binding.etProtein.getText().toString();
                String quantity = binding.etQuantity.getText().toString();
                if (!carbon.isEmpty() && !fat.isEmpty() && !heat.isEmpty() && !name.isEmpty() && !protein.isEmpty() && !quantity.isEmpty()) {
                    foodEnergy.setFood(name);
                    foodEnergy.setCarbon(Double.valueOf(Double.parseDouble(carbon)));
                    foodEnergy.setFat(Double.valueOf(Double.parseDouble(fat)));
                    foodEnergy.setHeat(heat);
                    foodEnergy.setProtein(Double.valueOf(Double.parseDouble(protein)));
                    foodEnergy.setQuantity(quantity);
                    AddFoodEnergyActivity.this.helper.getWritableDatabase().update("food_energy", foodEnergy.toContentValues(), "id = ?", new String[]{foodEnergy.getId() + ""});
                    show.dismiss();
                    AddFoodEnergyActivity.this.loadData();
                }
            }
        });
    }

    private void loadData() {
        String key = this.etSearch.getText().toString().trim();
        this.foodEnergyList = new ArrayList<>();
        Cursor cursor = this.helper.getWritableDatabase().rawQuery("SELECT * FROM food_energy WHERE typeId = " + this.typeId + " AND food LIKE '%" + key + "%' ORDER BY count ASC", null);

        if (cursor != null && cursor.getColumnCount() > 0) {
            while (cursor.moveToNext()) {
                Integer dbId = cursor.getInt(0);
                Integer typeId2 = cursor.getInt(1);
                String food = cursor.getString(2);
                String quantity = cursor.getString(3);
                String heat = cursor.getString(4);
                Double fat = cursor.getDouble(5);
                Double protein = cursor.getDouble(6);
                Double carbon = cursor.getDouble(7);
                int count2 = cursor.getInt(8);

                FoodEnergy foodEnergy = new FoodEnergy(dbId, typeId2, food, quantity, heat, fat, protein, carbon);
                foodEnergy.setCount(count2);
                this.foodEnergyList.add(foodEnergy);
            }
        }

        Collections.reverse(this.foodEnergyList);
        if (this.foodEnergyList == null || this.foodEnergyList.isEmpty()) {
            this.rvList.setVisibility(View.GONE);
            this.llEmpty.setVisibility(View.VISIBLE);
        } else {
            this.rvList.setVisibility(View.VISIBLE);
            this.llEmpty.setVisibility(View.GONE);
            this.foodEnergyAdapter.addItem(this.foodEnergyList);
        }
    }

    public void back(View view) {
        finish();
    }
}