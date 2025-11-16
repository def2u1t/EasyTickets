package com.def2u1t.easytickets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {
    /* 运行时可写目录 */
    private static final String DIR  = System.getProperty("user.home") + "/.easytickets";
    private static final String FILE = DIR + "data/data.db";

    public static Connection getConnection() throws SQLException {
        try {
            Files.createDirectories(Paths.get(DIR));
        } catch (IOException e) {
            throw new SQLException("无法创建数据库目录", e);
        }
        String url = "jdbc:sqlite:" + FILE +
                "?journal_mode=WAL&busy_timeout=5000";   // 降低锁概率
        return DriverManager.getConnection(url);
    }
}
