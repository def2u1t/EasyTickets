package com.def2u1t.easytickets;

public class Item {
    private int id;
    private String invoiceDate;
    private String customerName;
    private String contractNo;
    private double invoiceAmount;
    private String invoiceType;
    private String invoiceMethod;
    private String invoicePayment;
    private String actualInvoicePayment;
    private String mark;

    public Item() {}

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }

    public double getInvoiceAmount() { return invoiceAmount; }
    public void setInvoiceAmount(double invoiceAmount) { this.invoiceAmount = invoiceAmount; }

    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }

    public String getInvoiceMethod() { return invoiceMethod; }
    public void setInvoiceMethod(String invoiceMethod) { this.invoiceMethod = invoiceMethod; }

    public String getInvoicePayment() { return invoicePayment; }
    public void setInvoicePayment(String invoicePayment) { this.invoicePayment = invoicePayment; }

    public String getActualInvoicePayment() { return actualInvoicePayment; }
    public void setActualInvoicePayment(String actualInvoicePayment) { this.actualInvoicePayment = actualInvoicePayment; }

    public String getMark() { return mark; }
    public void setMark(String mark) { this.mark = mark; }


}
