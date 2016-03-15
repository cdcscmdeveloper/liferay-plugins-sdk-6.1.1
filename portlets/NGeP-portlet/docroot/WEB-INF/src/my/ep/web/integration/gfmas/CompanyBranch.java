package my.ep.web.integration.gfmas;

import java.util.Date;
import java.util.List;

/*
 * @authot P GANGA PRASAD
 */

public class CompanyBranch {

	private int recordType;
	private String name;
	private String address;
	private String address2;
	private String address3;
	private String postCode;
	private String city;
	private String region;
	private String country;
	private String telephoneNo;
	private String faxNo;
	private String emailAddress;
	private String glReconAccount;
	private String currency;
	private String incoTerm;
	private String paymentTerm;
	private int bankAccountInfoCount;
	private int status;
	private String mobileNo;
	private String indicatorCode;
	private String accountActionIndicator;
	private String referenceNoGST;
	private Date GSTEffectiveDate;
	private Date GSTEffectiveEndDate;

	List<BankAccountInformation> bankAccountInfoList;

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTelephoneNo() {
		return telephoneNo;
	}

	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getGlReconAccount() {
		return glReconAccount;
	}

	public void setGlReconAccount(String glReconAccount) {
		this.glReconAccount = glReconAccount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getIncoTerm() {
		return incoTerm;
	}

	public void setIncoTerm(String incoTerm) {
		this.incoTerm = incoTerm;
	}

	public String getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public int getBankAccountInfoCount() {
		return bankAccountInfoCount;
	}

	public void setBankAccountInfoCount(int bankAccountInfoCount) {
		this.bankAccountInfoCount = bankAccountInfoCount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getIndicatorCode() {
		return indicatorCode;
	}

	public void setIndicatorCode(String indicatorCode) {
		this.indicatorCode = indicatorCode;
	}

	public String getAccountActionIndicator() {
		return accountActionIndicator;
	}

	public void setAccountActionIndicator(String accountActionIndicator) {
		this.accountActionIndicator = accountActionIndicator;
	}

	public String getReferenceNoGST() {
		return referenceNoGST;
	}

	public void setReferenceNoGST(String referenceNoGST) {
		this.referenceNoGST = referenceNoGST;
	}

	public Date getGSTEffectiveDate() {
		return GSTEffectiveDate;
	}

	public void setGSTEffectiveDate(Date gSTEffectiveDate) {
		GSTEffectiveDate = gSTEffectiveDate;
	}

	public Date getGSTEffectiveEndDate() {
		return GSTEffectiveEndDate;
	}

	public void setGSTEffectiveEndDate(Date gSTEffectiveEndDate) {
		GSTEffectiveEndDate = gSTEffectiveEndDate;
	}

	public List<BankAccountInformation> getBankAccountInfoList() {
		return bankAccountInfoList;
	}

	public void setBankAccountInfoList(
			List<BankAccountInformation> bankAccountInfoList) {
		this.bankAccountInfoList = bankAccountInfoList;
	}

}
