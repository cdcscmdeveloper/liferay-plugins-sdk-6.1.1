package my.ep.web.bean.pm;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

public class ProfileManagementSearchBean {

	private Long selectedOrgType;
	private PmOrgProfile selectedOrgProfile;
	private Boolean companyProfile;
	private Boolean marketResearch;
	private Long appStatusId;
	private String identificationNo;
	private String userName;
	
	public Long getSelectedOrgType() {
		return selectedOrgType;
	}
	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}
	public void setSelectedOrgType(Long selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}
	public String getIdentificationNo() {
		return identificationNo;
	}
	public String getUserName() {
		return userName;
	}
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Boolean getCompanyProfile() {
		return companyProfile;
	}
	public Boolean getMarketResearch() {
		return marketResearch;
	}
	public void setCompanyProfile(Boolean companyProfile) {
		this.companyProfile = companyProfile;
	}
	public void setMarketResearch(Boolean marketResearch) {
		this.marketResearch = marketResearch;
	}
	public void setCompanyProfile(boolean companyProfile) {
		this.companyProfile = companyProfile;
	}
	public void setMarketResearch(boolean marketResearch) {
		this.marketResearch = marketResearch;
	}
	public Long getAppStatusId() {
		return appStatusId;
	}
	public void setAppStatusId(Long appStatusId) {
		this.appStatusId = appStatusId;
	}
	
	
}
