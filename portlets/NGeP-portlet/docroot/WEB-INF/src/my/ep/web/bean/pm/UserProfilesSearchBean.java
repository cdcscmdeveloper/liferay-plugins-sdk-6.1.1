package my.ep.web.bean.pm;

/**
 * Search Bean for Rejection Reason
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
public class UserProfilesSearchBean {
	
	private String userName;
	private String userRole;	
	private String userOrganizationName;	
	private Long userStatus;
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
	 * @return the userStatus
	 */
	public Long getUserStatus() {
		return userStatus;
	}
	/**
	 * @param userStatus the userStatus to set
	 */
	public void setUserStatus(Long userStatus) {
		this.userStatus = userStatus;
	}

	
}
