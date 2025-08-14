package com.example.healthmanagement.util;

import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {
    private static final String TAG = "mysql-party-JDBCUtils";
    // MySQL 5.1.49 驱动类名（无需修改，与原代码一致）
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String dbName = "healthmanagement";
    private static final String user = "root";
    private static final String password = "123456";
    private static final String ip = "172.20.10.4";

    public static Connection getConn() {
        Connection connection = null;
        try {
            Class.forName(driver);
            // 新增参数：处理时区和字符集（关键适配步骤）
            String url = "jdbc:mysql://" + ip + ":3306/" + dbName +
                    "?user=" + user +
                    "&password=" + password +
                    "&useSSL=false" +
                    "&autoReconnect=true" +
                    "&useLegacyDatetimeCode=true" + // 兼容旧版时区处理
                    "&characterEncoding=utf8"; // 防止中文乱码
            connection = DriverManager.getConnection(url);
            Log.d(TAG, "MySQL 连接成功（5.1.49 适配）");
        } catch (Exception e) {
            Log.e(TAG, "MySQL 连接失败", e);
            throw new RuntimeException(e);
        }
        return connection;
    }

    // 新增连接关闭方法（避免资源泄漏）
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                Log.e(TAG, "关闭 MySQL 连接失败", e);
            }
        }
    }
}