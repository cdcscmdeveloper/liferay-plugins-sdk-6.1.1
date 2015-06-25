package my.ep.web.integration.eterimaan;

/*
 * author @Ganga prasad
*/

public class CustomerInformation {

	private int recordType;
	private String customerIdCode;             // SSM code, ROS code etc.
	private String entityCode;                 //ROS, SSM  
	private String name1;
	private String name2;
	private String address1;
	private String address2;
	private String address3;
	private String postcode;
	private String city;
	private String region;
	private String country;
	private String telNo;
	private String faxNo;
	private String emailAddress;
	private int amount;
	private String description;
	private String description2;
	private String altGlReconAcc;                  //Mandatory if Bill Type is ‘D’
	// If GL Reconciliation account is different from ‘A0271900’ External System need to pass the Alternate GL Reconciliation account. 


	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
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

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
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

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
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

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription2() {
		return description2;
	}

	public void setDescription2(String description2) {
		this.description2 = description2;
	}

	public String getAltGlReconAcc() {
		return altGlReconAcc;
	}

	public void setAltGlReconAcc(String altGlReconAcc) {
		this.altGlReconAcc = altGlReconAcc;
	}

}
