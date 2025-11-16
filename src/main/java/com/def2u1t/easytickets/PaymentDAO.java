package com.def2u1t.easytickets;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    /* 仅保存路径，不维持连接 */
    private final String dbPath;
    public PaymentDAO()               { this(null); }
    public PaymentDAO(String dbPath)  { this.dbPath = dbPath == null ? "" : dbPath; }

    /* 工具：拿连接 */
    private Connection getConn() throws SQLException {
        return dbPath.isEmpty() ? DBUtil.getConnection()
                : DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    /** 某发票的全部回款，按日期倒序 */
    public List<PaymentRecord> getPaymentsByInvoiceId(int invoiceId) throws SQLException {
        String sql = "SELECT payment_date, customer_name, contract_no, amount, purpose " +
                "FROM payment_records " +
                "WHERE invoice_id = ? " +
                "ORDER BY payment_date DESC";
        List<PaymentRecord> list = new ArrayList<>();
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PaymentRecord(
                            rs.getString("payment_date"),
                            rs.getString("customer_name"),
                            rs.getString("contract_no"),
                            rs.getDouble("amount"),
                            rs.getString("purpose")));
                }
            }
        }
        return list;
    }

    /** 最近一笔回款日期 */
    public String getLatestPaymentDate(int invoiceId) throws SQLException {
        String sql = "SELECT MAX(payment_date) FROM payment_records WHERE invoice_id = ?";
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    /** 已回款总和 */
    public double getPaidTotal(int invoiceId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM payment_records WHERE invoice_id = ?";
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    /** 插入新回款（给“添加回款”按钮用） */
    public void insertPayment(int invoiceId, PaymentRecord p) throws SQLException {
        String sql = "INSERT INTO payment_records(payment_date, customer_name, contract_no, amount, purpose, invoice_id) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getPaymentDate());
            ps.setString(2, p.getCustomerName());
            ps.setString(3, p.getContractNo());
            ps.setDouble(4, p.getAmount());
            ps.setString(5, p.getRemark());
            ps.setInt(6, invoiceId);
            ps.executeUpdate();
        }
    }

    /** 关闭资源——不再持有连接，可空实现或直接删除 */
    public void close() throws SQLException {
        // 无长期连接，无需处理
    }
}
