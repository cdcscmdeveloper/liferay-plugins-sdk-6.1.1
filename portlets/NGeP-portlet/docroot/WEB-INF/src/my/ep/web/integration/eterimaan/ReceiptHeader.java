package my.ep.web.integration.eterimaan;

import java.math.BigDecimal;
import java.util.Date;


/*
 * author @Ganga prasad
*/
public class ReceiptHeader {

	private int recordType;                // //Default Value One(1)
	private String receiptType; 
	private String receiptNumberNew;
	private Date receiptDateNew;
	private Date receiptPostingDateNew;
	private String receiptNumberOld;
	private String chequeNumberOld;
	private String spickCodeOld;
	private int year;
	private String accountingOffice;
	private String pegawaiPengawalPembeyar;
	private String kumpulanPtjNPembeyar;
	private String currency;
	private String description1;
	private String description2;
	private String customerIdCode;
	private String entityCode;
	private String name1;
	private String name2;
	private String alternateGlReconAcc;
	private BigDecimal receiptAmount;
	private String bankInStatus;
	private int receiptDetailsCount;
	private int billingCount;

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public String getReceiptNumberNew() {
		return receiptNumberNew;
	}

	public void setReceiptNumberNew(String receiptNumberNew) {
		this.receiptNumberNew = receiptNumberNew;
	}

	public Date getReceiptDateNew() {
		return receiptDateNew;
	}

	public void setReceiptDateNew(Date receiptDateNew) {
		this.receiptDateNew = receiptDateNew;
	}

	public Date getReceiptPostingDateNew() {
		return receiptPostingDateNew;
	}

	public void setReceiptPostingDateNew(Date receiptPostingDateNew) {
		this.receiptPostingDateNew = receiptPostingDateNew;
	}

	public String getReceiptNumberOld() {
		return receiptNumberOld;
	}

	public void setReceiptNumberOld(String receiptNumberOld) {
		this.receiptNumberOld = receiptNumberOld;
	}

	public String getChequeNumberOld() {
		return chequeNumberOld;
	}

	public void setChequeNumberOld(String chequeNumberOld) {
		this.chequeNumberOld = chequeNumberOld;
	}

	public String getSpickCodeOld() {
		return spickCodeOld;
	}

	public void setSpickCodeOld(String spickCodeOld) {
		this.spickCodeOld = spickCodeOld;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getAccountingOffice() {
		return accountingOffice;
	}

	public void setAccountingOffice(String accountingOffice) {
		this.accountingOffice = accountingOffice;
	}

	public String getPegawaiPengawalPembeyar() {
		return pegawaiPengawalPembeyar;
	}

	public void setPegawaiPengawalPembeyar(String pegawaiPengawalPembeyar) {
		this.pegawaiPengawalPembeyar = pegawaiPengawalPembeyar;
	}

	public String getKumpulanPtjNPembeyar() {
		return kumpulanPtjNPembeyar;
	}

	public void setKumpulanPtjNPembeyar(String kumpulanPtjNPembeyar) {
		this.kumpulanPtjNPembeyar = kumpulanPtjNPembeyar;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDescription1() {
		return description1;
	}

	public void setDescription1(String description1) {
		this.description1 = description1;
	}

	public String getDescription2() {
		return description2;
	}

	public void setDescription2(String description2) {
		this.description2 = description2;
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

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getAlternateGlReconAcc() {
		return alternateGlReconAcc;
	}

	public void setAlternateGlReconAcc(String alternateGlReconAcc) {
		this.alternateGlReconAcc = alternateGlReconAcc;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getBankInStatus() {
		return bankInStatus;
	}

	public void setBankInStatus(String bankInStatus) {
		this.bankInStatus = bankInStatus;
	}

	public int getReceiptDetailsCount() {
		return receiptDetailsCount;
	}

	public void setReceiptDetailsCount(int receiptDetailsCount) {
		this.receiptDetailsCount = receiptDetailsCount;
	}

	public int getBillingCount() {
		return billingCount;
	}

	public void setBillingCount(int billingCount) {
		this.billingCount = billingCount;
	}

}
