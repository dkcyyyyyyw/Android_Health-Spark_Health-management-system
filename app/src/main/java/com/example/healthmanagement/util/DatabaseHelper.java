package com.example.healthmanagement.util;

import android.content.Context;
import java.sql.Connection;

public class DatabaseHelper {
    private Context context;

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    // 移除直接获取连接的逻辑，仅作为入口
    public void triggerDataSync() {
        new DataSyncHelper(context).syncUserTableToMySQL();
    }
}