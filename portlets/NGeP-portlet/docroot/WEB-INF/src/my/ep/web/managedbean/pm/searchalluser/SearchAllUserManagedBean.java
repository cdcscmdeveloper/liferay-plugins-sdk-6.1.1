package my.ep.web.managedbean.pm.searchalluser;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserAttachment;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;


/**
 * @author DK
 *
 */
@ManagedBean(name="searchAllUserManagedBean")
@SessionScoped
public class SearchAllUserManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log= EpLogger.getLogger(SearchAllUserManagedBean.class);	

	
	private List<String[]> resultStatus;
	
	
    // user profile	search	
	private String loginId;
	private String resultLoginId;
	private List<String> pmRoleList;
	private List<String> liferayRoleList;
	private List<String> oimRoleList;
	
	private UserProfilesSearchBean userProfileSearchBean;
	private PmUser selectedUser;	
	private PmUserOrg selectedUserOrg;
	private boolean receivingAddressFlag;
	private boolean addressEntered;
	private PmOrgProfile currentActiveOrg;
	private String additionalOrgFilter;
	private PmOrgProfile selectedOrgProfile;
	private PmParameter selectedOrgType;
	private PmUserOrg activeUserOrg;
	private boolean isLazy = false;
	
	
	public SearchAllUserManagedBean(){}

	/**
	 * @return the resultStatus
	 */
	public List<String[]> getResultStatus() {
		return resultStatus;
	}
	/**
	 * @param resultStatus the resultStatus to set
	 */
	public void setResultStatus(List<String[]> resultStatus) {
		this.resultStatus = resultStatus;
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
	 * @return the pmRoleList
	 */
	public List<String> getPmRoleList() {
		return pmRoleList;
	}
	/**
	 * @param pmRoleList the pmRoleList to set
	 */
	public void setPmRoleList(List<String> pmRoleList) {
		this.pmRoleList = pmRoleList;
	}
	/**
	 * @return the liferayRoleList
	 */
	public List<String> getLiferayRoleList() {
		return liferayRoleList;
	}
	/**
	 * @param liferayRoleList the liferayRoleList to set
	 */
	public void setLiferayRoleList(List<String> liferayRoleList) {
		this.liferayRoleList = liferayRoleList;
	}
	/**
	 * @return the oimRoleList
	 */
	public List<String> getOimRoleList() {
		return oimRoleList;
	}
	/**
	 * @param oimRoleList the oimRoleList to set
	 */
	public void setOimRoleList(List<String> oimRoleList) {
		this.oimRoleList = oimRoleList;
	}
	/**
	 * @return the resultLoginId
	 */
	public String getResultLoginId() {
		return resultLoginId;
	}
	/**
	 * @param resultLoginId the resultLoginId to set
	 */
	public void setResultLoginId(String resultLoginId) {
		this.resultLoginId = resultLoginId;
	}
	/**
	 * @return the additionalOrgFilter
	 */
	public String getAdditionalOrgFilter() {
		return additionalOrgFilter;
	}
	/**
	 * @param additionalOrgFilter the additionalOrgFilter to set
	 */
	public void setAdditionalOrgFilter(String additionalOrgFilter) {
		this.additionalOrgFilter = additionalOrgFilter;
	}
	/**
	 * @return the currentActiveOrg
	 */
	public PmOrgProfile getCurrentActiveOrg() {
		return currentActiveOrg;
	}
	/**
	 * @param currentActiveOrg the currentActiveOrg to set
	 */
	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
	}
	/**
	 * @return the addressEntered
	 */
	public boolean isAddressEntered() {
		return addressEntered;
	}
	/**
	 * @param addressEntered the addressEntered to set
	 */
	public void setAddressEntered(boolean addressEntered) {
		this.addressEntered = addressEntered;
	}
	/**
	 * @return the receivingAddressFlag
	 */
	public boolean isReceivingAddressFlag() {
		return receivingAddressFlag;
	}
	/**
	 * @param receivingAddressFlag the receivingAddressFlag to set
	 */
	public void setReceivingAddressFlag(boolean receivingAddressFlag) {
		this.receivingAddressFlag = receivingAddressFlag;
	}

	/**
	 * @return the selectedUser
	 */
	public PmUser getSelectedUser() {
		return selectedUser;
	}


	/**
	 * @param selectedUser the selectedUser to set
	 */
	public void setSelectedUser(PmUser selectedUser) {
		this.selectedUser = selectedUser;
	}



	/**
	 * @return the selectedUserOrg
	 */
	public PmUserOrg getSelectedUserOrg() {
		return selectedUserOrg;
	}

	/**
	 * @param selectedUserOrg the selectedUserOrg to set
	 */
	public void setSelectedUserOrg(PmUserOrg selectedUserOrg) {
		this.selectedUserOrg = selectedUserOrg;
	}

	/**
	 * @return the userProfileSearchBean
	 */
	public UserProfilesSearchBean getUserProfileSearchBean() {
		if(userProfileSearchBean==null)
			userProfileSearchBean = new UserProfilesSearchBean();
		return userProfileSearchBean;
	}

	/**
	 * @param userProfileSearchBean the userProfileSearchBean to set
	 */
	public void setUserProfileSearchBean(
			UserProfilesSearchBean userProfileSearchBean) {
		this.userProfileSearchBean = userProfileSearchBean;
	}
	
	
	public PmOrgProfile getSelectedOrgProfile() {
		if(selectedOrgProfile == null){
			this.selectedOrgProfile = new PmOrgProfile();
		}
		return selectedOrgProfile;
	}
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}
	public PmUserOrg getActiveUserOrg() {
		return activeUserOrg;
	}
	public void setActiveUserOrg(PmUserOrg activeUserOrg) {
		this.activeUserOrg = activeUserOrg;
	}
	public PmParameter getSelectedOrgType() {
		if(this.selectedOrgType == null){
			this.selectedOrgType = new PmParameter();
		}
		return selectedOrgType;
	}
	public void setSelectedOrgType(PmParameter selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}
	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}
	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	
	
}	
	

