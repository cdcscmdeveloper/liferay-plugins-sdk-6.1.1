package my.ep.web.integration.eperunding;

/*
 * @author P Ganga prasad
 */
public class AssociatedCompany {

	private String mOFNo;
	private String companyName;
	private String associatedCompanySSMNO;
	private String associatedCompanyMOFNO;
	private String shareholding;
	private String address1;
	private String address2;
	private String address3;
	private String postcode;
	private String city;
	private String district;
	private String state;
	private String country;
	private String telCountryCode;
	private String telAreaCode;
	private String telNo;
	private String faxCountryCode;
	private String faxAreaCode;
	private String faxNo;

	public String getmOFNo() {
		return mOFNo;
	}

	public void setmOFNo(String mOFNo) {
		this.mOFNo = mOFNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

/*	public String getsSMNO() {
		return associatedCompanySSMNO;
	}

	public void setsSMNO(String sSMNO) {
		this.associatedCompanySSMNO = sSMNO;
	}*/
	
	

	public String getAssociatedCompanyMOFNO() {
		return associatedCompanyMOFNO;
	}

	public String getAssociatedCompanySSMNO() {
		return associatedCompanySSMNO;
	}

	public void setAssociatedCompanySSMNO(String associatedCompanySSMNO) {
		this.associatedCompanySSMNO = associatedCompanySSMNO;
	}

	public void setAssociatedCompanyMOFNO(String associatedCompanyMOFNO) {
		this.associatedCompanyMOFNO = associatedCompanyMOFNO;
	}

	public String getShareholding() {
		return shareholding;
	}

	public void setShareholding(String shareholding) {
		this.shareholding = shareholding;
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

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTelCountryCode() {
		return telCountryCode;
	}

	public void setTelCountryCode(String telCountryCode) {
		this.telCountryCode = telCountryCode;
	}

	public String getTelAreaCode() {
		return telAreaCode;
	}

	public void setTelAreaCode(String telAreaCode) {
		this.telAreaCode = telAreaCode;
	}

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public String getFaxCountryCode() {
		return faxCountryCode;
	}

	public void setFaxCountryCode(String faxCountryCode) {
		this.faxCountryCode = faxCountryCode;
	}

	public String getFaxAreaCode() {
		return faxAreaCode;
	}

	public void setFaxAreaCode(String faxAreaCode) {
		this.faxAreaCode = faxAreaCode;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AssociatedCompany) {
			AssociatedCompany temp = (AssociatedCompany) obj;
			if (this.mOFNo.equalsIgnoreCase(temp.mOFNo)
					&& this.companyName.equalsIgnoreCase(temp.companyName)
					&& this.associatedCompanySSMNO.equalsIgnoreCase(temp.associatedCompanySSMNO))
				return true;
		}
		return false;

	}

	@Override
	public int hashCode() {

		return (this.mOFNo.hashCode() + this.companyName.hashCode() + this.associatedCompanySSMNO
				.hashCode());
	}

}
