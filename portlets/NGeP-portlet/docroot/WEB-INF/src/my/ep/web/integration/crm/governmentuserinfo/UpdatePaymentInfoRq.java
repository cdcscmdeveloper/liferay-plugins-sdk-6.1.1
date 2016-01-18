/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.integration.crm.governmentuserinfo;

public class UpdatePaymentInfoRq {
	
	private String supplierID;
	private String billID;
	private String transactionID;
	private String transactionDate;
	private String receiptNo;
	private String paymentMethod;
	private String totalAmount;
	
	public String getSupplierID() {
		return supplierID;
	}
	
	public void setSupplierID(String supplierID) {
		this.supplierID = supplierID;
	}
	
	public String getBillID() {
		return billID;
	}
	
	public void setBillID(String billID) {
		this.billID = billID;
	}
	
	public String getTransactionID() {
		return transactionID;
	}
	
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	
	public String getTransactionDate() {
		return transactionDate;
	}
	
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	public String getReceiptNo() {
		return receiptNo;
	}
	
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	
	public String getPaymentMethod() {
		return paymentMethod;
	}
	
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	public String getTotalAmount() {
		return totalAmount;
	}
	
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
}
