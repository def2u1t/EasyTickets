package com.def2u1t.easytickets;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.util.List;

public class HelloController {

    /* ========== 注入区 ========== */
    @FXML private ListView<String> dataListView;

    @FXML private TextField tfInvoiceDate, tfCustomerName, tfContractNo, tfInvoiceType,
            tfInvoiceMethod, tfNote, tfLastPaymentDate, tfActualPaymentTerm;

    /* 3 个金额用数字框更合理，若保持 TextField 则需字符串<->Double 转换 */
    @FXML private TextField tfInvoiceAmount, tfPaymentAmount, tfUnpaidAmount;

    @FXML private TableView<PaymentRecord> paymentTable;
    @FXML private TableColumn<PaymentRecord, String> colPaymentDate, colCustomerName,
            colContractNo, colRemark;
    @FXML private TableColumn<PaymentRecord, Double> colAmount;

    /* ========== 数据区 ========== */
    private final ItemDAO itemDao = new ItemDAO("data/data.db");
    private final ObservableList<String> invoiceNames = FXCollections.observableArrayList();
    private final ObservableList<PaymentRecord> paymentRecords = FXCollections.observableArrayList();
    private Item currentItem;          // 当前开票对象

    public HelloController() throws SQLException {
    }

    /* ========== 初始化 ========== */
    @FXML
    public void initialize() {
        try {
            setupTableColumns();
            loadInvoiceList();

            // 选中事件
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

    /* ========== 表格列绑定 ========== */
    private void setupTableColumns() {
        colPaymentDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colContractNo.setCellValueFactory(new PropertyValueFactory<>("contractNo"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colRemark.setCellValueFactory(new PropertyValueFactory<>("remark"));
        paymentTable.setItems(paymentRecords);
    }

    /* ========== 加载左侧列表 ========== */
    private void loadInvoiceList() throws SQLException {
        invoiceNames.clear();
        for (Item i : itemDao.getAllItems()) {
            invoiceNames.add(i.getId() + " - " + i.getCustomerName());
        }
        dataListView.setItems(invoiceNames);
    }

    /* ========== 加载右侧详情 + 回款 ========== */
    private void loadInvoiceDetails(int id) throws SQLException {

        currentItem = itemDao.getItemById(id);
        if (currentItem == null) return;

        /* ---- 字符串字段 ---- */
        tfInvoiceDate.setText(currentItem.getInvoiceDate());
        tfCustomerName.setText(currentItem.getCustomerName());
        tfContractNo.setText(currentItem.getContractNo());
        tfInvoiceType.setText(currentItem.getInvoiceType());
        tfInvoiceMethod.setText(currentItem.getInvoiceMethod());
        tfNote.setText(currentItem.getNote());
        tfLastPaymentDate.setText(currentItem.getLastPaymentDate());
        tfActualPaymentTerm.setText(currentItem.getActualPaymentTerm());

        /* ---- 数字字段 ---- */
        tfInvoiceAmount.setText(String.valueOf(currentItem.getInvoiceAmount()));
        tfPaymentAmount.setText(String.valueOf(currentItem.getPaymentAmount()));
        tfUnpaidAmount.setText(String.valueOf(currentItem.getUnpaidAmount()));

        /* ---- 回款记录 ---- */
        paymentRecords.setAll(itemDao.getPaymentsByInvoiceId(id));
    }

    /* ========== 保存按钮 ========== */
    @FXML
    private void saveCurrentItem() {
        if (currentItem == null) return;

        try {
            /* 把界面值写回对象 */
            currentItem.setInvoiceDate(tfInvoiceDate.getText());
            currentItem.setCustomerName(tfCustomerName.getText());
            currentItem.setContractNo(tfContractNo.getText());
            currentItem.setInvoiceType(tfInvoiceType.getText());
            currentItem.setInvoiceMethod(tfInvoiceMethod.getText());
            currentItem.setNote(tfNote.getText());
            currentItem.setLastPaymentDate(tfLastPaymentDate.getText());
            currentItem.setActualPaymentTerm(tfActualPaymentTerm.getText());

            currentItem.setInvoiceAmount(Double.parseDouble(tfInvoiceAmount.getText()));
            currentItem.setPaymentAmount(Double.parseDouble(tfPaymentAmount.getText()));
            currentItem.setUnpaidAmount(Double.parseDouble(tfUnpaidAmount.getText()));

            itemDao.updateItem(currentItem);
            loadInvoiceList();          // 刷新左侧
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
