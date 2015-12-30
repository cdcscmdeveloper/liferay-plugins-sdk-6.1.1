package my.ep.web.integration.eperunding;

import java.util.Date;

/*
 *@author P Ganga prasad
 */
public class Branch {
	private String branchName;
	private String GSTNo;
	private Date GSTEffectiveDate;
	private Date GSTEndDate;
	private String address1;
	private String address2;
	private String address3;
	private String postcode;
	private String city;
	private String district;
	private String state;
	private String country;

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getGSTNo() {
		return GSTNo;
	}

	public void setGSTNo(String gSTNo) {
		GSTNo = gSTNo;
	}

	public Date getGSTEffectiveDate() {
		return GSTEffectiveDate;
	}

	public void setGSTEffectiveDate(Date gSTEffectiveDate) {
		GSTEffectiveDate = gSTEffectiveDate;
	}

	public Date getGSTEndDate() {
		return GSTEndDate;
	}

	public void setGSTEndDate(Date gSTEndDate) {
		GSTEndDate = gSTEndDate;
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

}
