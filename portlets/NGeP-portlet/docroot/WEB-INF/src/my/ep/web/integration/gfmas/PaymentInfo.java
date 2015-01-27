package my.ep.web.integration.gfmas;

public class PaymentInfo {
	private String paymentRefNo;
	private String paymentDate;
	private String[] invoiceNoList;
	/**
	 * @return the paymentRefNo
	 */
	public String getPaymentRefNo() {
		return paymentRefNo;
	}
	/**
	 * @param paymentRefNo the paymentRefNo to set
	 */
	public void setPaymentRefNo(String paymentRefNo) {
		this.paymentRefNo = paymentRefNo;
	}
	/**
	 * @return the paymentDate
	 */
	public String getPaymentDate() {
		return paymentDate;
	}
	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	/**
	 * @return the invoiceNoList
	 */
	public String[] getInvoiceNoList() {
		return invoiceNoList;
	}
	/**
	 * @param invoiceNoList the invoiceNoList to set
	 */
	public void setInvoiceNoList(String[] invoiceNoList) {
		this.invoiceNoList = invoiceNoList;
	}

	

}
