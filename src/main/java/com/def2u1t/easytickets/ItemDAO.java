package com.def2u1t.easytickets;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    /* 兼容老代码：保留无参 / 传路径构造，但只存路径，不维持连接 */
    private final String dbPath;

    public ItemDAO()               { this(null); }
    public ItemDAO(String dbPath)  { this.dbPath = dbPath == null ? "" : dbPath; }

    /* 工具方法：真正拿连接 */
    private Connection getConn() throws SQLException {
        // 如果构造时给了路径就用路径，否则走统一工具
        return dbPath.isEmpty() ? DBUtil.getConnection()
                : DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    /* ================ 查全部 ================ */
    public List<Item> getAllItems() throws SQLException {
        String sql = "SELECT * FROM invoice_records ORDER BY invoice_date DESC";
        List<Item> list = new ArrayList<>();

        try (Connection c = getConn();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setInvoiceDate(rs.getString("invoice_date"));
                item.setCustomerName(rs.getString("customer_name"));
                item.setContractNo(rs.getString("contract_no"));
                item.setInvoiceAmount(rs.getDouble("invoice_amount"));
                item.setInvoiceType(rs.getString("invoice_type"));
                item.setInvoiceMethod(rs.getString("invoice_method"));
                item.setInvoicePayment(rs.getString("invoice_payment"));
                item.setActualInvoicePayment(rs.getString("actual_invoice_payment"));
                item.setMark(rs.getString("mark"));
                list.add(item);
            }
        }
        return list;
    }

    /* ================ 单条查询 ================ */
    public Item getItemById(int id) throws SQLException {
        String sql = "SELECT * FROM invoice_records WHERE id = ?";
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getInt("id"));
                    item.setInvoiceDate(rs.getString("invoice_date"));
                    item.setCustomerName(rs.getString("customer_name"));
                    item.setContractNo(rs.getString("contract_no"));
                    item.setInvoiceAmount(rs.getDouble("invoice_amount"));
                    item.setInvoiceType(rs.getString("invoice_type"));
                    item.setInvoiceMethod(rs.getString("invoice_method"));
                    item.setInvoicePayment(rs.getString("invoice_payment"));
                    item.setActualInvoicePayment(rs.getString("actual_invoice_payment"));
                    item.setMark(rs.getString("mark"));
                    return item;
                }
            }
        }
        return null;
    }

    /* ================ 插入 ================ */
    public void insertItem(Item item) throws SQLException {
        String sql = "INSERT INTO invoice_records (invoice_date, customer_name, contract_no, " +
                "invoice_amount, invoice_type, invoice_method, invoice_payment, " +
                "actual_invoice_payment, mark) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, item.getInvoiceDate());
            ps.setString(2, item.getCustomerName());
            ps.setString(3, item.getContractNo());
            ps.setDouble(4, item.getInvoiceAmount());
            ps.setString(5, item.getInvoiceType());
            ps.setString(6, item.getInvoiceMethod());
            ps.setString(7, item.getInvoicePayment());
            ps.setString(8, item.getActualInvoicePayment());
            ps.setString(9, item.getMark());

            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    item.setId(gk.getInt(1));
                }
            }
        }
    }

    /* ================ 更新 ================ */
    public void updateItem(Item item) throws SQLException {
        String sql = "UPDATE invoice_records SET invoice_date=?, customer_name=?, contract_no=?, " +
                "invoice_amount=?, invoice_type=?, invoice_method=?, invoice_payment=?, " +
                "actual_invoice_payment=?, mark=? WHERE id=?";
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getInvoiceDate());
            ps.setString(2, item.getCustomerName());
            ps.setString(3, item.getContractNo());
            ps.setDouble(4, item.getInvoiceAmount());
            ps.setString(5, item.getInvoiceType());
            ps.setString(6, item.getInvoiceMethod());
            ps.setString(7, item.getInvoicePayment());
            ps.setString(8, item.getActualInvoicePayment());
            ps.setString(9, item.getMark());
            ps.setInt(10, item.getId());
            ps.executeUpdate();
        }
    }

    /* ================ 删除 ================ */
    public void deleteItem(int invoiceId) throws SQLException {
        String sql = "DELETE FROM invoice_records WHERE id = ?";
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.executeUpdate();
        }
    }

    /* ================ 回款查询示例 ================ */
    public List<PaymentRecord> getPaymentsByInvoiceId(int invoiceId) throws SQLException {
        List<PaymentRecord> list = new ArrayList<>();
        String sql = "SELECT payment_date, customer_name, contract_no, amount, remark " +
                "FROM payment_record WHERE invoice_id = ?";
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
                            rs.getString("remark")));
                }
            }
        }
        return list;
    }
}
