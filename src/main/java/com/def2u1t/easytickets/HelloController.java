package com.def2u1t.easytickets;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class HelloController {

    /* ===================== 注入区 ===================== */
    @FXML private ListView<String> dataListView;

    @FXML private TextField tfInvoiceDate;
    @FXML private TextField tfCustomerName;
    @FXML private TextField tfContractNo;
    @FXML private TextField tfInvoiceType;
    @FXML private TextField tfInvoiceMethod;
    @FXML private TextField tfRemark;
    @FXML private TextField tfPaymentTerm;
    @FXML private TextField tfActualPaymentTerm;
    @FXML private TextField tfInvoiceAmount;
    @FXML private TextField tfPaidAmount;     // 新增
    @FXML private TextField tfUnpaidAmount;   // 新增
    @FXML private TextField tfLastPaymentDate;
    @FXML private TableView<PaymentRecord> paymentTable;
    @FXML private TableColumn<PaymentRecord, String> colPaymentDate;
    @FXML private TableColumn<PaymentRecord, String> colCustomerName;
    @FXML private TableColumn<PaymentRecord, String> colContractNo;
    @FXML private TableColumn<PaymentRecord, Double> colAmount;
    @FXML private TableColumn<PaymentRecord, String> colRemark;

    /* ===================== 数据区 ===================== */
    private final ItemDAO itemDao = new ItemDAO("data/data.db");
    private final ObservableList<String> invoiceNames = FXCollections.observableArrayList();
    private final ObservableList<PaymentRecord> paymentRecords = FXCollections.observableArrayList();
    private Item currentItem;

    private final DecimalFormat moneyFmt = new DecimalFormat("#,##0.00");

    public HelloController() throws SQLException {}

    /* ===================== 初始化 ===================== */
    @FXML
    public void initialize() {
        try {
            setupTableColumns();
            loadInvoiceList();

            /* 选中事件 */
            dataListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    int id = Integer.parseInt(newVal.split(" - ")[0]);
                    try {
                        loadInvoiceDetails(id);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        colPaymentDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colContractNo.setCellValueFactory(new PropertyValueFactory<>("contractNo"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colRemark.setCellValueFactory(new PropertyValueFactory<>("remark"));
        paymentTable.setItems(paymentRecords);
    }

    private void loadInvoiceList() throws SQLException {
        invoiceNames.clear();
        for (Item i : itemDao.getAllItems()) {
            invoiceNames.add(i.getId() + " - " + i.getCustomerName());
        }
        dataListView.setItems(invoiceNames);


    }

    private void loadInvoiceDetails(int id) throws SQLException {
        currentItem = itemDao.getItemById(id);
        if (currentItem == null) return;

        /* ---------- 基本信息 ---------- */
        tfInvoiceDate.setText(currentItem.getInvoiceDate());
        tfCustomerName.setText(currentItem.getCustomerName());
        tfContractNo.setText(currentItem.getContractNo());
        tfInvoiceType.setText(currentItem.getInvoiceType());
        tfInvoiceMethod.setText(currentItem.getInvoiceMethod());
        tfRemark.setText(currentItem.getMark());
        tfPaymentTerm.setText(currentItem.getInvoicePayment());
        tfActualPaymentTerm.setText(currentItem.getActualInvoicePayment());

        double invoiceAmount = currentItem.getInvoiceAmount();
        tfInvoiceAmount.setText(moneyFmt.format(invoiceAmount));

        /* ---------- 回款相关 ---------- */
        PaymentDAO paymentDao = new PaymentDAO("data/data.db");

        /* 1. 表格数据（近期在前） */
        paymentRecords.setAll(paymentDao.getPaymentsByInvoiceId(id));

        /* 2. 最近回款日期 */
        String latestDate = paymentDao.getLatestPaymentDate(id);
        tfLastPaymentDate.setText(latestDate == null ? "" : latestDate);

        /* 3. 已回款 & 剩余 */
        double paidTotal = paymentDao.getPaidTotal(id);
        tfPaidAmount.setText(moneyFmt.format(paidTotal));
        tfUnpaidAmount.setText(moneyFmt.format(invoiceAmount - paidTotal));

        paymentDao.close();
    }

    /* ===================== 保存按钮 ===================== */
    @FXML
    private void saveCurrentItem() {
        if (currentItem == null) return;
        try {
            currentItem.setInvoiceDate(tfInvoiceDate.getText());
            currentItem.setCustomerName(tfCustomerName.getText());
            currentItem.setContractNo(tfContractNo.getText());
            currentItem.setInvoiceType(tfInvoiceType.getText());
            currentItem.setInvoiceMethod(tfInvoiceMethod.getText());
            currentItem.setMark(tfRemark.getText());
            currentItem.setInvoicePayment(tfPaymentTerm.getText());
            currentItem.setActualInvoicePayment(tfActualPaymentTerm.getText());
            currentItem.setInvoiceAmount(Double.parseDouble(tfInvoiceAmount.getText().replace(",", "")));

            itemDao.updateItem(currentItem);
            loadInvoiceList();          // 刷新左侧
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
