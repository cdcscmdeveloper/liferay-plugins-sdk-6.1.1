package my.ep.web.integration.eperunding;

import java.math.BigDecimal;

public class CompanyOwner {

	private String mOFNo;
	private String companyName;
	private String sSMNo;
	private String companyOwnerMOFNo;
	private BigDecimal shareholdingAmount;
	private BigDecimal shareholdingPercentage;
	private String address1;
	private String address2;
	private String address3;
	private String postcode;
	private String city;
	private String state;
	private String district;
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

	public String getsSMNo() {
		return sSMNo;
	}

	public void setsSMNo(String sSMNo) {
		this.sSMNo = sSMNo;
	}

	public String getCompanyOwnerMOFNo() {
		return companyOwnerMOFNo;
	}

	public void setCompanyOwnerMOFNo(String companyOwnerMOFNo) {
		this.companyOwnerMOFNo = companyOwnerMOFNo;
	}

	public BigDecimal getShareholdingAmount() {
		return shareholdingAmount;
	}

	public void setShareholdingAmount(BigDecimal shareholdingAmount) {
		this.shareholdingAmount = shareholdingAmount;
	}

	public BigDecimal getShareholdingPercentage() {
		return shareholdingPercentage;
	}

	public void setShareholdingPercentage(BigDecimal shareholdingPercentage) {
		this.shareholdingPercentage = shareholdingPercentage;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
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
	    if(obj instanceof CompanyOwner)
	    {
	    	CompanyOwner temp = (CompanyOwner) obj;
	        if(this.mOFNo.equalsIgnoreCase(temp.mOFNo)&& this.companyName.equalsIgnoreCase(temp.companyName)&& this.sSMNo.equalsIgnoreCase(temp.sSMNo))
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {

	    return (this.mOFNo.hashCode()+this.companyName.hashCode()+this.sSMNo.hashCode());        
	}

}
