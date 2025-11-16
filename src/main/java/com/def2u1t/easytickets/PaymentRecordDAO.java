package com.def2u1t.easytickets;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRecordDAO {

    private final Connection conn;

    public PaymentRecordDAO(String dbPath) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    /* 根据开票 id 查全部回款 */
    public List<PaymentRecord> getPaymentsByInvoiceId(int contract_no) throws SQLException {
        String sql = "SELECT payment_date, customer_name, contract_no, amount, remark " +
                "FROM payment_record WHERE contract_no = ?";
        List<PaymentRecord> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(4, contract_no);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PaymentRecord(
                        rs.getString("payment_date"),
                        rs.getString("customer_name"),
                        rs.getString("contract_no"),
                        rs.getDouble("amount"),
                        rs.getString("remark")));
            }
        }
        return list;
    }

    /* 新增一条回款 */
    public void insertPayment(PaymentRecord p, int invoiceId) throws SQLException {
        String sql = "INSERT INTO payment_record(invoice_id, payment_date, customer_name, contract_no, amount, remark) " +
                "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setString(2, p.getPaymentDate());
            ps.setString(3, p.getCustomerName());
            ps.setString(4, p.getContractNo());
            ps.setDouble(5, p.getAmount());
            ps.setString(6, p.getRemark());
            ps.executeUpdate();
        }
    }

    /* 删除回款（可选） */
    public void deletePayment(int paymentId) throws SQLException {
        String sql = "DELETE FROM payment_record WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.executeUpdate();
        }
    }

    public void close() throws SQLException {
        conn.close();
    }
}
