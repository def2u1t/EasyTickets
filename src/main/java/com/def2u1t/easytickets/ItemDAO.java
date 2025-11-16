package com.def2u1t.easytickets;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private Connection connection;

    public ItemDAO(String dbPath) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    /* ======================
       获取全部开票记录
       ====================== */
    public List<Item> getAllItems() throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM invoice_records ORDER BY invoice_date DESC";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Item item = new Item();

            item.setId(rs.getInt("id"));
            item.setInvoiceDate(rs.getString("invoice_date"));
            item.setCustomerName(rs.getString("customer_name"));
            item.setContractNo(rs.getString("contract_no"));

            item.setInvoiceAmount(rs.getDouble("invoice_amount")); // 唯一FLOAT

            item.setInvoiceType(rs.getString("invoice_type"));
            item.setInvoiceMethod(rs.getString("invoice_method"));

            item.setInvoicePayment(rs.getString("invoice_payment")); // TEXT
            item.setActualInvoicePayment(rs.getString("actual_invoice_payment")); // TEXT

            item.setMark(rs.getString("mark"));

            list.add(item);
        }
        return list;
    }


    /* ======================
       根据 ID 获取单条记录
       ====================== */
    public Item getItemById(int id) throws SQLException {
        String sql = "SELECT * FROM invoice_records WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

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
        return null;
    }


    /* ======================
       更新记录
       ====================== */
    public void updateItem(Item item) throws SQLException {
        String sql =
                "UPDATE invoice_records SET " +
                        "invoice_date=?, customer_name=?, contract_no=?, invoice_amount=?, " +
                        "invoice_type=?, invoice_method=?, invoice_payment=?, actual_invoice_payment=?, mark=? " +
                        "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);

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


    /* ======================
       回款记录（如你需要）
       ====================== */
    public List<PaymentRecord> getPaymentsByInvoiceId(int invoiceId) throws SQLException {
        List<PaymentRecord> payments = new ArrayList<>();
        String sql =
                "SELECT payment_date, customer_name, contract_no, amount, remark " +
                        "FROM payment_record WHERE invoice_id=?";

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
