package my.ep.web.integration.eterimaan;

/*
 * author @Ganga prasad
*/
import java.math.BigDecimal;
import java.util.Date;

public class ReceiptDetails {

	private int recordType;              //Default Value two(2)
	private int recordNumber;
	private String receiptNumber;
	private String kodPenjenisanTerimaan;
	private String votDana;
	private String pegawaiPegawalDipertanggung;
	private String kumpulanPtjDipertanggung;
	private String programAktiviti;
	private String projek;
	private String kodAkaun;
	private String paymentMethod;
	private String referenceNo;
	private String spickCode;
	private String swiftCode;
	private String branchName;
	private Date paymentDate;
	private BigDecimal amount;

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public int getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(int recordNumber) {
		this.recordNumber = recordNumber;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public String getKodPenjenisanTerimaan() {
		return kodPenjenisanTerimaan;
	}

	public void setKodPenjenisanTerimaan(String kodPenjenisanTerimaan) {
		this.kodPenjenisanTerimaan = kodPenjenisanTerimaan;
	}

	public String getVotDana() {
		return votDana;
	}

	public void setVotDana(String votDana) {
		this.votDana = votDana;
	}

	public String getPegawaiPegawalDipertanggung() {
		return pegawaiPegawalDipertanggung;
	}

	public void setPegawaiPegawalDipertanggung(
			String pegawaiPegawalDipertanggung) {
		this.pegawaiPegawalDipertanggung = pegawaiPegawalDipertanggung;
	}

	public String getKumpulanPtjDipertanggung() {
		return kumpulanPtjDipertanggung;
	}

	public void setKumpulanPtjDipertanggung(String kumpulanPtjDipertanggung) {
		this.kumpulanPtjDipertanggung = kumpulanPtjDipertanggung;
	}

	public String getProgramAktiviti() {
		return programAktiviti;
	}

	public void setProgramAktiviti(String programAktiviti) {
		this.programAktiviti = programAktiviti;
	}

	public String getProjek() {
		return projek;
	}

	public void setProjek(String projek) {
		this.projek = projek;
	}

	public String getKodAkaun() {
		return kodAkaun;
	}

	public void setKodAkaun(String kodAkaun) {
		this.kodAkaun = kodAkaun;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getSpickCode() {
		return spickCode;
	}

	public void setSpickCode(String spickCode) {
		this.spickCode = spickCode;
	}

	public String getSwiftCode() {
		return swiftCode;
	}

	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
