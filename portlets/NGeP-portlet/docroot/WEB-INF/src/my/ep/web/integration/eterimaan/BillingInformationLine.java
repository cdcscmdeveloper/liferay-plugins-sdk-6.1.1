package my.ep.web.integration.eterimaan;

import java.math.BigDecimal;
import java.util.Date;

/*
 * author @Ganga prasad
*/
public class BillingInformationLine {

	private int recordType;                        //Default Value Three (3)
	private String recordNumber;
	private String receiptNumber;
	private String billDebitCreditNoteNum;
	private Date billDebitCreditNoteDate;
	private String customerIdCode;
	private String entityCode;
	private BigDecimal amountOfbillDebitCreditNote;
	private BigDecimal amountReceivedbillDebitCreditNoteNum;

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public String getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(String recordNumber) {
		this.recordNumber = recordNumber;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public String getBillDebitCreditNoteNum() {
		return billDebitCreditNoteNum;
	}

	public void setBillDebitCreditNoteNum(String billDebitCreditNoteNum) {
		this.billDebitCreditNoteNum = billDebitCreditNoteNum;
	}

	public Date getBillDebitCreditNoteDate() {
		return billDebitCreditNoteDate;
	}

	public void setBillDebitCreditNoteDate(Date billDebitCreditNoteDate) {
		this.billDebitCreditNoteDate = billDebitCreditNoteDate;
	}

	public String getCustomerIdCode() {
		return customerIdCode;
	}

	public void setCustomerIdCode(String customerIdCode) {
		this.customerIdCode = customerIdCode;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public BigDecimal getAmountOfbillDebitCreditNote() {
		return amountOfbillDebitCreditNote;
	}

	public void setAmountOfbillDebitCreditNote(
			BigDecimal amountOfbillDebitCreditNote) {
		this.amountOfbillDebitCreditNote = amountOfbillDebitCreditNote;
	}

	public BigDecimal getAmountReceivedbillDebitCreditNoteNum() {
		return amountReceivedbillDebitCreditNoteNum;
	}

	public void setAmountReceivedbillDebitCreditNoteNum(
			BigDecimal amountReceivedbillDebitCreditNoteNum) {
		this.amountReceivedbillDebitCreditNoteNum = amountReceivedbillDebitCreditNoteNum;
	}

}
