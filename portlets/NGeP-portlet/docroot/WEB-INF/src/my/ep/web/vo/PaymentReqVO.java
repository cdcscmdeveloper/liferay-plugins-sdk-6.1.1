package my.ep.web.vo;

import java.io.Serializable;

public class PaymentReqVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String billAmt;
	private String orderId;
	private String billName;
	private String billEmail;
	private String billMobile;
	private String billDesc;
	private String country;
	private String cancelurl;
	private String merchantCode;
	
	public String getBillAmt() {
		return billAmt;
	}
	public void setBillAmt(String billAmt) {
		this.billAmt = billAmt;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getBillName() {
		return billName;
	}
	public void setBillName(String billName) {
		this.billName = billName;
	}
	public String getBillEmail() {
		return billEmail;
	}
	public void setBillEmail(String billEmail) {
		this.billEmail = billEmail;
	}
	public String getBillMobile() {
		return billMobile;
	}
	public void setBillMobile(String billMobile) {
		this.billMobile = billMobile;
	}
	public String getBillDesc() {
		return billDesc;
	}
	public void setBillDesc(String billDesc) {
		this.billDesc = billDesc;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCancelurl() {
		return cancelurl;
	}
	public void setCancelurl(String cancelurl) {
		this.cancelurl = cancelurl;
	}
	public String getMerchantCode() {
		return merchantCode;
	}
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
}
