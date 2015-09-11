package my.ep.web.integration.phis;

import java.math.BigDecimal;
import java.util.Date;

/*
 * @author P Ganga prasad
 */

public class PaymentEffNotify {

	private String contractOrderNo;
	private String paymenyReferenceNo;
	private String invoiceNo;
	private String invoiceRefNo;
	BigDecimal amountPaid;
	private Date paymentDate;

	public String getContractOrderNo() {
		return contractOrderNo;
	}

	public void setContractOrderNo(String contractOrderNo) {
		this.contractOrderNo = contractOrderNo;
	}

	public String getPaymenyReferenceNo() {
		return paymenyReferenceNo;
	}

	public void setPaymenyReferenceNo(String paymenyReferenceNo) {
		this.paymenyReferenceNo = paymenyReferenceNo;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceRefNo() {
		return invoiceRefNo;
	}

	public void setInvoiceRefNo(String invoiceRefNo) {
		this.invoiceRefNo = invoiceRefNo;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

}
