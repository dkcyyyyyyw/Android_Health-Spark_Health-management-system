package com.example.healthmanagement.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSyncHelper {
    private static final String TAG = "DataSyncHelper";
    private final Context context;

    public DataSyncHelper(Context context) {
        this.context = context;
    }

    /**
     * 同步 SQLite 的 user 表到 MySQL（适配 5.1.49）
     */
    public void syncUserTableToMySQL() {
        // 1. 获取 SQLite 数据库连接
        MySqliteOpenHelper sqliteHelper = new MySqliteOpenHelper(context);
        SQLiteDatabase sqliteDb = sqliteHelper.getReadableDatabase();
        Cursor cursor = null;

        // 2. 获取 MySQL 数据库连接
        Connection mysqlConn = DBUtils.getConn();
        PreparedStatement pstmt = null;

        try {
            // 查询 SQLite 中的用户数据
            cursor = sqliteDb.rawQuery("SELECT * FROM user", null);

            if (mysqlConn == null) {
                Log.e(TAG, "MySQL 连接失败，无法同步");
                return;
            }

            // 3. 准备插入语句（不支持 ON DUPLICATE KEY，直接插入）
            String insertSql = "INSERT INTO user (" +
                    "id, account, password, name, sex, phone, address, photo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = mysqlConn.prepareStatement(insertSql);

            // 4. 批量插入（每 100 条提交一次，避免内存溢出）
            int batchSize = 100;
            int count = 0;

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // 按 SQLite 表字段顺序取值（需与 User 类字段顺序一致）
                    pstmt.setInt(1, cursor.getInt(cursor.getColumnIndex("id")));
                    pstmt.setString(2, cursor.getString(cursor.getColumnIndex("account")));
                    pstmt.setString(3, cursor.getString(cursor.getColumnIndex("password")));
                    pstmt.setString(4, cursor.getString(cursor.getColumnIndex("name")));
                    pstmt.setString(5, cursor.getString(cursor.getColumnIndex("sex")));
                    pstmt.setString(6, cursor.getString(cursor.getColumnIndex("phone")));
                    pstmt.setString(7, cursor.getString(cursor.getColumnIndex("address")));
                    pstmt.setString(8, cursor.getString(cursor.getColumnIndex("photo")));

                    pstmt.addBatch();
                    if (++count % batchSize == 0) {
                        pstmt.executeBatch();
                    }
                } while (cursor.moveToNext());
                // 执行剩余批次
                pstmt.executeBatch();
            }

            Log.d(TAG, "同步完成，共插入 " + count + " 条数据");
        } catch (SQLException e) {
            Log.e(TAG, "同步失败：" + e.getMessage(), e);
            // 手动回滚（如需事务，需先开启 mysqlConn.setAutoCommit(false)）
            if (mysqlConn != null) {
                try {
                    mysqlConn.rollback();
                } catch (SQLException ex) {
                    Log.e(TAG, "回滚失败", ex);
                }
            }
        } finally {
            // 5. 关闭所有资源
            if (cursor != null) cursor.close();
            if (sqliteDb != null) sqliteDb.close();
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    Log.e(TAG, "关闭语句失败", e);
                }
            }
            DBUtils.close(mysqlConn); // 调用封装的关闭方法
        }
    }
}
