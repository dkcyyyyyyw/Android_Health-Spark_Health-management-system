package com.example.healthmanagement.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmanagement.R;
import com.example.healthmanagement.bean.SportRecord;
import com.example.healthmanagement.util.MySqliteOpenHelper;
import com.example.healthmanagement.util.SPUtils;
import com.example.healthmanagement.view.SportStepView;
import com.github.mikephil.charting.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SportRecordActivity extends AppCompatActivity {
    private Sensor accelerometerSensor;
    MySqliteOpenHelper helper = null;
    private boolean isRuning = false;
    private double lastSpeed = Utils.DOUBLE_EPSILON;
    private boolean motiveState = true;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            double speed = Math.sqrt((double) ((event.values[0] * event.values[0]) + (event.values[1] * event.values[1]) + (event.values[2] * event.values[2])));
            if (SportRecordActivity.this.motiveState) {
                if (speed >= SportRecordActivity.this.lastSpeed) {
                    SportRecordActivity.this.lastSpeed = speed;
                } else if (Math.abs(speed - SportRecordActivity.this.lastSpeed) > 2.0d) {
                    SportRecordActivity.this.motiveState = false;
                    SportRecordActivity.this.lastSpeed = speed;
                }
            }
            if (SportRecordActivity.this.motiveState) {
                return;
            }
            if (speed <= SportRecordActivity.this.lastSpeed) {
                SportRecordActivity.this.lastSpeed = speed;
            } else if (Math.abs(speed - SportRecordActivity.this.lastSpeed) > 2.0d) {
                SportRecordActivity.this.motiveState = true;
                SportRecordActivity.this.lastSpeed = speed;
                if (SportRecordActivity.this.isRuning) {
                    SportRecordActivity.access(SportRecordActivity.this);
                    SportRecordActivity.this.sportStepCount.setCurrentCount(5000, SportRecordActivity.this.step);
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private SensorManager sensorManager;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sf1 = new SimpleDateFormat("HH:mm:ss");
    private SportStepView sportStepCount;
    private int step = 0;
    private TextView stepTv;
    private Integer userId;

    static int access(SportRecordActivity x0) {
        int i = x0.step;
        x0.step = i + 1;
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_record);
        this.helper = new MySqliteOpenHelper(this);
        this.stepTv = (TextView) findViewById(R.id.sportStepCountInfo);
        this.sportStepCount = (SportStepView) findViewById(R.id.sportStepCount);
        SensorManager sensorManager2 = (SensorManager) getSystemService("sensor");
        this.sensorManager = sensorManager2;
        Sensor defaultSensor = sensorManager2.getDefaultSensor(1);
        this.accelerometerSensor = defaultSensor;
        this.sensorManager.registerListener(this.sensorEventListener, defaultSensor, 2);
        this.userId = (Integer) SPUtils.get(this, SPUtils.USER_ID, 0);
    }

    public void onAction(View view) {
        Button button = (Button) view; // 获取点击的按钮
        // 如果按钮文本为“开始”，则进行相应操作
        if ("开始".equals(button.getText().toString())) {
            button.setText("停止"); // 修改按钮文本为“停止”
            this.stepTv.setText("正在记步..."); // 更新显示文本
            this.isRuning = true; // 设置状态为正在运行
            return;
        }
        // 如果按钮文本为“停止”，则进行相应操作
        button.setText("开始"); // 修改按钮文本为“开始”
        this.stepTv.setText("未开始"); // 更新显示文本
        this.isRuning = false; // 设置状态为未运行
    }

    @Override
    public void onDestroy() {
        super.onDestroy(); // 调用父类的 onDestroy 方法
        this.sensorManager.unregisterListener(this.sensorEventListener); // 注销传感器监听器
    }

    // 打卡方法
    public void card(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // 创建对话框构建器
        View view = getLayoutInflater().inflate(R.layout.dialog_sport, (ViewGroup) null); // 加载自定义对话框布局
        final EditText editText = (EditText) view.findViewById(R.id.et_value); // 获取输入框

        // 设置对话框属性
        builder.setTitle("打卡").setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String remarkValue = editText.getText().toString().trim(); // 获取输入的备注值
                SQLiteDatabase db = SportRecordActivity.this.helper.getWritableDatabase(); // 获取可写数据库
                SportRecord sportRecord = null; // 初始化运动记录对象

                // 查询是否已有打卡记录
                Cursor cursor = db.rawQuery("select * from sport_record where date = ? and userId = " + SportRecordActivity.this.userId, new String[]{SportRecordActivity.this.sf.format(new Date())});
                if (cursor != null && cursor.getColumnCount() > 0) {
                    while (cursor.moveToNext()) {
                        // 创建 SportRecord 对象
                        sportRecord = new SportRecord(Integer.valueOf(cursor.getInt(0)), Integer.valueOf(cursor.getInt(1)), Integer.valueOf(cursor.getInt(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                    }
                }
                // 如果已有打卡记录，提示用户
                if (sportRecord != null) {
                    Toast.makeText(SportRecordActivity.this, "你今天已经打卡，请明天再来", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 插入打卡记录
                db.execSQL("insert into sport_record (userId, step, remark, date, time) values(?,?,?,?,?);", new Object[]{
                        SportRecordActivity.this.userId, Integer.valueOf(SportRecordActivity.this.step), remarkValue, SportRecordActivity.this.sf.format(new Date()), SportRecordActivity.this.sf1.format(new Date())
                });
                db.close(); // 关闭数据库连接
                Toast.makeText(SportRecordActivity.this, "打卡成功", Toast.LENGTH_SHORT).show(); // 提示打卡成功
            }
        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // 取消时关闭对话框
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        // 对话框关闭时的回调
                    }
                })
                .create(); // 创建对话框
        builder.show(); // 显示对话框
    }

    public void sport(View view) {
        startActivity(new Intent(this, SportRecordListActivity.class));
    }

    public void back(View view) {
        finish();
    }
}
