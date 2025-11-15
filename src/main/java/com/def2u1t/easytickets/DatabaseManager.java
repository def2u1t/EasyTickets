package com.def2u1t.easytickets;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_FOLDER = "data";
    private static final String DB_FILENAME = "data.db";
    private static final String DB_PATH = DB_FOLDER + File.separator + DB_FILENAME;
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_PATH;

    static {
        try {
            // 确保目录存在
            File dir = new File(DB_FOLDER);
            if (!dir.exists()) {
                boolean ok = dir.mkdirs();
                if (!ok) {
                    System.err.println("无法创建数据库目录：" + dir.getAbsolutePath());
                }
            }

            // 加载 JDBC 驱动（通常不强制要求，但写上更保险）
            Class.forName("org.sqlite.JDBC");

            // 创建数据库连接（若文件不存在，SQLite 会自动创建）
            try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
                initTables(conn);
            }

            System.out.println("SQLite 初始化完成，数据库路径：" + new File(DB_PATH).getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("初始化数据库失败", e);
        }
    }

    private static void initTables(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT
                );
                """;
        conn.createStatement().execute(sql);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }
}
