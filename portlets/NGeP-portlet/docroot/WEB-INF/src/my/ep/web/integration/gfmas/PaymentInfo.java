package my.ep.web.integration.gfmas;

public class PaymentInfo {

	private String paymentRefNo;
	private String paymentDate;
	private String[] invoiceArray;

	public String getPaymentRefNo() {
		return paymentRefNo;
	}

	public void setPaymentRefNo(String paymentRefNo) {
		this.paymentRefNo = paymentRefNo;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String[] getInvoiceArray() {
		return invoiceArray;
	}

	public void setInvoiceArray(String[] invoiceArray) {
		this.invoiceArray = invoiceArray;
	}

}
