package my.ep.web.bean.pm;

/**
 * Search Bean for Rejection Reason
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
public class UserProfilesSearchBean {
	private String loginId;
	private String identificationNo;
	private String userName;
	private String userRole;	
	private String userOrganizationName;	
	private Long userOrgStatus;
	private Long selectedOrgProfileId;
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the userRole
	 */
	public String getUserRole() {
		return userRole;
	}
	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	/**
	 * @return the userOrganizationName
	 */
	public String getUserOrganizationName() {
		return userOrganizationName;
	}
	/**
	 * @param userOrganizationName the userOrganizationName to set
	 */
	public void setUserOrganizationName(String userOrganizationName) {
		this.userOrganizationName = userOrganizationName;
	}
	/**
	 * @return the userOrgStatus
	 */
	public Long getUserOrgStatus() {
		return userOrgStatus;
	}
	/**
	 * @param userOrgStatus the userOrgStatus to set
	 */
	public void setUserOrgStatus(Long userOrgStatus) {
		this.userOrgStatus = userOrgStatus;
	}
	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	/**
	 * @return the identificationNo
	 */
	public String getIdentificationNo() {
		return identificationNo;
	}
	/**
	 * @param identificationNo the identificationNo to set
	 */
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}
	public Long getSelectedOrgProfileId() {
		return selectedOrgProfileId;
	}
	public void setSelectedOrgProfileId(Long selectedOrgProfileId) {
		this.selectedOrgProfileId = selectedOrgProfileId;
	}
	
	
	
	
}
