package com.def2u1t.easytickets;

public class PaymentRecord {
    private final String paymentDate;
    private final String customerName;
    private final String contractNo;
    private final double amount;
    private final String purpose;   // 对应你表里的 purpose

    public PaymentRecord(String paymentDate,
                         String customerName,
                         String contractNo,
                         double amount,
                         String purpose) {
        this.paymentDate = paymentDate;
        this.customerName = customerName;
        this.contractNo = contractNo;
        this.amount = amount;
        this.purpose = purpose;
    }

    /* ============= getter ============= */
    public String getPaymentDate() { return paymentDate; }
    public String getCustomerName() { return customerName; }
    public String getContractNo() { return contractNo; }
    public double getAmount() { return amount; }
    public String getRemark() { return purpose; }  // 为了兼容界面列名
}
