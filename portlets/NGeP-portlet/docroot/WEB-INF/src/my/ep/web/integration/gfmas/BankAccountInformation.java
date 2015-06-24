package my.ep.web.integration.gfmas;

public class BankAccountInformation {

	private int recordType;
	private int recordNumber;
	private String bankCountry;
	private String bankKey;
	private String bankAccount;
	private String currency;
	private String bankActionIndicator; // version 2.7

	private int kodBankIndicator; // version 2.7     values 0,1,3

	// for APOVE
	private String AccountHolderName;

	private String swiftCode;
	private String status;
	private String bankName;

	public String getSwiftCode() {
		return swiftCode;
	}

	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

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

	public String getBankCountry() {
		return bankCountry;
	}

	public void setBankCountry(String bankCountry) {
		this.bankCountry = bankCountry;
	}

	public String getBankKey() {
		return bankKey;
	}

	public void setBankKey(String bankKey) {
		this.bankKey = bankKey;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAccountHolderName() {
		return AccountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		AccountHolderName = accountHolderName;
	}

	public String getBankActionIndicator() {
		return bankActionIndicator;
	}

	public void setBankActionIndicator(String bankActionIndicator) {
		this.bankActionIndicator = bankActionIndicator;
	}

	public int getKodBankIndicator() {
		return kodBankIndicator;
	}

	public void setKodBankIndicator(int kodBankIndicator) {
		this.kodBankIndicator = kodBankIndicator;
	}

}
