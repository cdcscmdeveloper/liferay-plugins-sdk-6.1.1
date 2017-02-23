package my.ep.web.vo;

import java.io.Serializable;

import my.ep.persistence.entity.sm.SmCompanyAddress;

public class SmSupplierBasicInfoVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mofNo;
	private String startDate;
	private String endDate;
	private String epNo;
	private String regNo;
	private SmCompanyAddress address;
	private String adminName;
	private String adminPhoneNo;
	private String adminEmail;
	private String companyName;
	private String addressLine;
	private String postCodeStateCityCountry;
	private String gstNum;
	private String gstRegDate;
	private String gstEndDate;
	
	public String getMofNo() {
		return mofNo;
	}
	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getEpNo() {
		return epNo;
	}
	public void setEpNo(String epNo) {
		this.epNo = epNo;
	}
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public SmCompanyAddress getAddress() {
		return address;
	}
	public void setAddress(SmCompanyAddress address) {
		this.address = address;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public String getAdminPhoneNo() {
		return adminPhoneNo;
	}
	public void setAdminPhoneNo(String adminPhoneNo) {
		this.adminPhoneNo = adminPhoneNo;
	}
	public String getAdminEmail() {
		return adminEmail;
	}
	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getAddressLine() {
		return addressLine;
	}
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	public String getPostCodeStateCityCountry() {
		return postCodeStateCityCountry;
	}
	public void setPostCodeStateCityCountry(String postCodeStateCityCountry) {
		this.postCodeStateCityCountry = postCodeStateCityCountry;
	}
	public String getGstNum() {
		return gstNum;
	}
	public void setGstNum(String gstNum) {
		this.gstNum = gstNum;
	}
	public String getGstRegDate() {
		return gstRegDate;
	}
	public void setGstRegDate(String gstRegDate) {
		this.gstRegDate = gstRegDate;
	}
	public String getGstEndDate() {
		return gstEndDate;
	}
	public void setGstEndDate(String gstEndDate) {
		this.gstEndDate = gstEndDate;
	}
}
