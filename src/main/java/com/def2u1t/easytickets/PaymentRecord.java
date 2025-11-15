package com.def2u1t.easytickets;

public class PaymentRecord {
    private String paymentDate;
    private String customerName;
    private String contractNo;
    private double amount;
    private String remark;

    public PaymentRecord(String paymentDate, String customerName, String contractNo, double amount, String remark) {
        this.paymentDate = paymentDate;
        this.customerName = customerName;
        this.contractNo = contractNo;
        this.amount = amount;
        this.remark = remark;
    }

    public String getPaymentDate() { return paymentDate; }
    public String getCustomerName() { return customerName; }
    public String getContractNo() { return contractNo; }
    public double getAmount() { return amount; }
    public String getRemark() { return remark; }
}
