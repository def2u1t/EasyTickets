package com.def2u1t.easytickets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbTestMain {
    public static void main(String[] args) {
        // 触发 DatabaseManager 的 static 初始化块
        try {
            // 插入一条测试数据
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO items(name, description) VALUES (?, ?)")) {
                ps.setString(1, "测试项1");
                ps.setString(2, "这是第一条测试记录");
                ps.executeUpdate();
            }

            // 查询并打印
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT id, name, description FROM items ORDER BY id DESC");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    System.out.printf("id=%d, name=%s, desc=%s%n",
                            rs.getInt("id"), rs.getString("name"), rs.getString("description"));
                }
            }

            System.out.println("数据库测试完成，查看项目根目录下 data/data.db 文件。");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
