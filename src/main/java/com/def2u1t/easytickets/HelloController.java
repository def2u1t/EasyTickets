package com.def2u1t.easytickets;

import javafx.collections.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.*;
import javafx.scene.control.*;


public class HelloController {

    /* ===================== 注入区 ===================== */
    @FXML private ListView<String> dataListView;
    @FXML private TableView<Item> table;
    @FXML private TextField tfInvoiceDate;
    @FXML private TextField tfCustomerName;
    @FXML private TextField tfContractNo;
    @FXML private TextField tfInvoiceType;
    @FXML private TextField tfInvoiceMethod;
    @FXML private TextField tfRemark;
    @FXML private TextField tfPaymentTerm;
    @FXML private TextField tfActualPaymentTerm;
    @FXML private TextField tfInvoiceAmount;
    @FXML private TextField tfPaidAmount;
    @FXML private TextField tfUnpaidAmount;
    @FXML private TextField tfLastPaymentDate;
    @FXML private TableView<PaymentRecord> paymentTable;
    @FXML private TableColumn<PaymentRecord, String> colPaymentDate;
    @FXML private TableColumn<PaymentRecord, String> colCustomerName;
    @FXML private TableColumn<PaymentRecord, String> colContractNo;
    @FXML private TableColumn<PaymentRecord, Double> colAmount;
    @FXML private TableColumn<PaymentRecord, String> colRemark;
    private final ObservableList<PaymentRecord> paymentRecords =
            FXCollections.observableArrayList();
    /* ===================== 数据区 ===================== */
    private final ItemDAO dao = new ItemDAO("data/data.db");
    private final PaymentDAO paymentDao = new PaymentDAO("data/data.db");
    private final DecimalFormat moneyFmt = new DecimalFormat("#,##0.00");
    private Item currentItem;

    public HelloController() throws SQLException {}

    /* ===================== 初始化 ===================== */
    @FXML
    public void initialize() {
        try {
            setupTableColumns();
            refreshLeftList();   // 首次加载左侧
            dataListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    int id = Integer.parseInt(newVal.split(" \\| ")[0]);
                    try { loadInvoiceDetails(id); } catch (SQLException e) { throw new RuntimeException(e); }
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    /* ===================== 左侧刷新 ===================== */
    private void refreshLeftList() {
        Task<List<String>> task = new Task<>() {
            @Override protected List<String> call() throws Exception {
                List<Item> all = dao.getAllItems();
                List<String> tmp = new ArrayList<>();
                for (Item i : all) tmp.add(i.getId() + " | " + i.getCustomerName());
                return tmp;
            }
        };
        task.setOnSucceeded(e -> dataListView.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(e -> new Alert(Alert.AlertType.ERROR, "刷新左侧列表失败：" + task.getException()).show());
        new Thread(task).start();
    }

    /* ===================== 新增按钮 ===================== */
    @FXML
    private void onAdd() {
        TextInputDialog dateDlg = new TextInputDialog(LocalDate.now().toString());
        dateDlg.setTitle("新增发票"); dateDlg.setHeaderText("请输入开票日期 (yyyy-MM-dd)");
        Optional<String> dateOpt = dateDlg.showAndWait(); if (dateOpt.isEmpty()) return;

        TextInputDialog customerDlg = new TextInputDialog();
        customerDlg.setTitle("新增发票"); customerDlg.setHeaderText("客户名称");
        Optional<String> customerOpt = customerDlg.showAndWait(); if (customerOpt.isEmpty()) return;

        TextInputDialog contractDlg = new TextInputDialog();
        contractDlg.setTitle("新增发票"); contractDlg.setHeaderText("合同号");
        Optional<String> contractOpt = contractDlg.showAndWait(); if (contractOpt.isEmpty()) return;

        TextInputDialog amountDlg = new TextInputDialog("0.00");
        amountDlg.setTitle("新增发票"); amountDlg.setHeaderText("开票金额");
        Optional<String> amountOpt = amountDlg.showAndWait(); if (amountOpt.isEmpty()) return;

        double amount; LocalDate date;
        try {
            amount = Double.parseDouble(amountOpt.get().trim());
            date = LocalDate.parse(dateOpt.get().trim());
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "金额或日期格式错误！").show(); return;
        }

        Item newItem = new Item();
        newItem.setInvoiceDate(date.toString());
        newItem.setCustomerName(customerOpt.get().trim());
        newItem.setContractNo(contractOpt.get().trim());
        newItem.setInvoiceAmount(amount);
        newItem.setInvoiceType("普票");
        newItem.setInvoiceMethod("打票不销账");
        newItem.setInvoicePayment("0");
        newItem.setActualInvoicePayment("0");
        newItem.setMark("");

        Task<Void> insertTask = new Task<>() {
            @Override protected Void call() throws Exception { dao.insertItem(newItem); return null; }
        };
        insertTask.setOnSucceeded(e -> {
            table.getItems().add(newItem);
            table.scrollTo(newItem); table.getSelectionModel().select(newItem);
            refreshLeftList();   // 刷新左侧
        });
        insertTask.setOnFailed(e -> new Alert(Alert.AlertType.ERROR, "新增失败：" + insertTask.getException()).show());
        new Thread(insertTask).start();
    }

    /* ===================== 删除按钮 ===================== */
    @FXML
    private void onDelete() {
        if (currentItem == null) { new Alert(Alert.AlertType.WARNING, "请先选择要删除的发票！").show(); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "确定删除发票 " + currentItem.getContractNo() + " ？", ButtonType.OK, ButtonType.CANCEL);
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            dao.deleteItem(currentItem.getId());
            table.getItems().remove(currentItem);
            currentItem = null;
            refreshLeftList();   // 刷新左侧
        } catch (SQLException e) { new Alert(Alert.AlertType.ERROR, "删除失败：" + e.getMessage()).show(); }
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
            dao.updateItem(currentItem);
            refreshLeftList();   // 刷新左侧
        } catch (Exception e) { new Alert(Alert.AlertType.ERROR, "保存失败：" + e.getMessage()).show(); }
    }

    /* ===================== 明细加载 ===================== */
    private void loadInvoiceDetails(int id) throws SQLException {
        currentItem = dao.getItemById(id);
        if (currentItem == null) return;
        tfInvoiceDate.setText(currentItem.getInvoiceDate());
        tfCustomerName.setText(currentItem.getCustomerName());
        tfContractNo.setText(currentItem.getContractNo());
        tfInvoiceType.setText(currentItem.getInvoiceType());
        tfInvoiceMethod.setText(currentItem.getInvoiceMethod());
        tfRemark.setText(currentItem.getMark());
        tfPaymentTerm.setText(currentItem.getInvoicePayment());
        tfActualPaymentTerm.setText(currentItem.getActualInvoicePayment());
        double invAmt = currentItem.getInvoiceAmount();
        tfInvoiceAmount.setText(moneyFmt.format(invAmt));

        paymentRecords.setAll(paymentDao.getPaymentsByInvoiceId(id));
        String latest = paymentDao.getLatestPaymentDate(id);
        tfLastPaymentDate.setText(latest == null ? "" : latest);
        double paid = paymentDao.getPaidTotal(id);
        tfPaidAmount.setText(moneyFmt.format(paid));
        tfUnpaidAmount.setText(moneyFmt.format(invAmt - paid));
    }

    /* ===================== 工具 ===================== */
    private void setupTableColumns() {
        colPaymentDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colContractNo.setCellValueFactory(new PropertyValueFactory<>("contractNo"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colRemark.setCellValueFactory(new PropertyValueFactory<>("remark"));
        paymentTable.setItems(paymentRecords);
    }
}
