package com.def2u1t.easytickets;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private Connection connection;

    public ItemDAO(String dbPath) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    // 获取所有开票
    public List<Item> getAllItems() throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM invoice ORDER BY invoice_date DESC";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Item item = new Item();
            item.setId(rs.getInt("id"));
            item.setCustomerName(rs.getString("customer_name"));
            item.setInvoiceDate(rs.getString("invoice_date"));
            item.setContractNo(rs.getString("contract_no"));
            item.setInvoiceAmount(rs.getDouble("invoice_amount"));
            item.setPaymentAmount(rs.getDouble("payment_amount"));
            item.setUnpaidAmount(rs.getDouble("unpaid_amount"));
            item.setInvoiceType(rs.getString("invoice_type"));
            item.setInvoiceMethod(rs.getString("invoice_method"));
            item.setNote(rs.getString("note"));
            item.setLastPaymentDate(rs.getString("last_payment_date"));
            item.setActualPaymentTerm(rs.getString("actual_payment_term"));
            list.add(item);
        }
        return list;
    }

    // 根据 id 查询开票详情
    public Item getItemById(int id) throws SQLException {
        String sql = "SELECT * FROM invoice WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Item item = new Item();
            item.setId(rs.getInt("id"));
            item.setCustomerName(rs.getString("customer_name"));
            item.setInvoiceDate(rs.getString("invoice_date"));
            item.setContractNo(rs.getString("contract_no"));
            item.setInvoiceAmount(rs.getDouble("invoice_amount"));
            item.setPaymentAmount(rs.getDouble("payment_amount"));
            item.setUnpaidAmount(rs.getDouble("unpaid_amount"));
            item.setInvoiceType(rs.getString("invoice_type"));
            item.setInvoiceMethod(rs.getString("invoice_method"));
            item.setNote(rs.getString("note"));
            item.setLastPaymentDate(rs.getString("last_payment_date"));
            item.setActualPaymentTerm(rs.getString("actual_payment_term"));
            return item;
        }
        return null;
    }
    public void updateItem(Item item) throws SQLException {
        String sql = "UPDATE invoice SET customer_name=?, invoice_date=?, contract_no=?, invoice_amount=?, payment_amount=?, unpaid_amount=?, invoice_type=?, invoice_method=?, note=?, last_payment_date=?, actual_payment_term=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, item.getCustomerName());
        ps.setString(2, item.getInvoiceDate());
        ps.setString(3, item.getContractNo());
        ps.setDouble(4, item.getInvoiceAmount());
        ps.setDouble(5, item.getPaymentAmount());
        ps.setDouble(6, item.getUnpaidAmount());
        ps.setString(7, item.getInvoiceType());
        ps.setString(8, item.getInvoiceMethod());
        ps.setString(9, item.getNote());
        ps.setString(10, item.getLastPaymentDate());
        ps.setString(11, item.getActualPaymentTerm());
        ps.setInt(12, item.getId());
        ps.executeUpdate();
    }
    // 根据开票 id 获取回款记录
    public List<PaymentRecord> getPaymentsByInvoiceId(int invoiceId) throws SQLException {
        List<PaymentRecord> payments = new ArrayList<>();
        String sql = "SELECT payment_date, customer_name, contract_no, amount, remark FROM payment_record WHERE invoice_id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, invoiceId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            payments.add(new PaymentRecord(
                    rs.getString("payment_date"),
                    rs.getString("customer_name"),
                    rs.getString("contract_no"),
                    rs.getDouble("amount"),
                    rs.getString("remark")
            ));
        }
        return payments;
    }
}
