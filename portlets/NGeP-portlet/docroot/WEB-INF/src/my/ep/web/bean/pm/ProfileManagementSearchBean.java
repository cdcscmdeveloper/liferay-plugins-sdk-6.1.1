package my.ep.web.bean.pm;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

public class ProfileManagementSearchBean {

	private Long selectedOrgType;
	private PmOrgProfile selectedOrgProfile;
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
	
	
}
