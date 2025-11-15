package com.def2u1t.easytickets;

public class Item {
    private int id;
    private String customerName;
    private String invoiceDate;
    private String contractNo;
    private double invoiceAmount;
    private double paymentAmount;
    private double unpaidAmount;
    private String invoiceType;
    private String invoiceMethod;
    private String note;
    private String lastPaymentDate;
    private String actualPaymentTerm;

    // 构造方法
    public Item() {}

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }

    public double getInvoiceAmount() { return invoiceAmount; }
    public void setInvoiceAmount(double invoiceAmount) { this.invoiceAmount = invoiceAmount; }

    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }

    public double getUnpaidAmount() { return unpaidAmount; }
    public void setUnpaidAmount(double unpaidAmount) { this.unpaidAmount = unpaidAmount; }

    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }

    public String getInvoiceMethod() { return invoiceMethod; }
    public void setInvoiceMethod(String invoiceMethod) { this.invoiceMethod = invoiceMethod; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getLastPaymentDate() { return lastPaymentDate; }
    public void setLastPaymentDate(String lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }

    public String getActualPaymentTerm() { return actualPaymentTerm; }
    public void setActualPaymentTerm(String actualPaymentTerm) { this.actualPaymentTerm = actualPaymentTerm; }
}
