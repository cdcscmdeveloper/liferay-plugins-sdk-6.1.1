package my.ep.web.integration.eterimaan;

import java.util.Date;

/*
 * author @Ganga prasad
*/

public class BillHeader {

	private int recordType;
	private String billType;
	private String  billRCreditRDebitNumber; // Total reBill/Credit Note/ Debit Note number in External Systems.
	private String billReference;
	private Date billReferenceDate;
	private Date billRCreditRDebitDate; // BILL/CREDIT NOTE/DEBIT NOTE DATE
	private Date postingDate;
	private String ABTIndicator; // AKAUN BELUM TERIMA
	private String year;
	private String accountingOffice;
	private String pegawaiPengawalPembayar;
	private String kumpulanPtj; // KUMPULAN PTJ/PTJ PEMBAYAR
	private String currency;
	private int custTotal; // Customer Information total records
	private int chargeLineTotal; // ChargeLine Total records

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getBillRCreditRDebitNumber() {
		return billRCreditRDebitNumber;
	}

	public void setBillRCreditRDebitNumber(String billRCreditRDebitNumber) {
		this.billRCreditRDebitNumber = billRCreditRDebitNumber;
	}

	public String getBillReference() {
		return billReference;
	}

	public void setBillReference(String billReference) {
		this.billReference = billReference;
	}

	public Date getBillReferenceDate() {
		return billReferenceDate;
	}

	public void setBillReferenceDate(Date billReferenceDate) {
		this.billReferenceDate = billReferenceDate;
	}

	public Date getBillRCreditRDebitDate() {
		return billRCreditRDebitDate;
	}

	public void setBillRCreditRDebitDate(Date billRCreditRDebitDate) {
		this.billRCreditRDebitDate = billRCreditRDebitDate;
	}

	public Date getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	public String getABTIndicator() {
		return ABTIndicator;
	}

	public void setABTIndicator(String aBTIndicator) {
		ABTIndicator = aBTIndicator;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getAccountingOffice() {
		return accountingOffice;
	}

	public void setAccountingOffice(String accountingOffice) {
		this.accountingOffice = accountingOffice;
	}

	public String getPegawaiPengawalPembayar() {
		return pegawaiPengawalPembayar;
	}

	public void setPegawaiPengawalPembayar(String pegawaiPengawalPembayar) {
		this.pegawaiPengawalPembayar = pegawaiPengawalPembayar;
	}

	public String getKumpulanPtj() {
		return kumpulanPtj;
	}

	public void setKumpulanPtj(String kumpulanPtj) {
		this.kumpulanPtj = kumpulanPtj;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getCustTotal() {
		return custTotal;
	}

	public void setCustTotal(int custTotal) {
		this.custTotal = custTotal;
	}

	public int getChargeLineTotal() {
		return chargeLineTotal;
	}

	public void setChargeLineTotal(int chargeLineTotal) {
		this.chargeLineTotal = chargeLineTotal;
	}

}
