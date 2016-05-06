package my.ep.web.managedbean.pm.profilepersonalization;


import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.web.bean.pm.ChallengeQAList;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;


/**
 * @author DK
 *
 */
@ManagedBean(name="profilePersonalizationManagedBean")
@SessionScoped
public class ProfilePersonalizationManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log= EpLogger.getLogger(ProfilePersonalizationManagedBean.class);	
	
	
	private PmUser selectedUser;
	private PmAddress userAddress; //Address Type - C
	private boolean divisionFlagC;
	private boolean deletePortrait;
	private int selectedTabIndex;
	private boolean addressEntered;
	private PmOrgProfile currentProfile;
	private PmUserOrg currentUserOrg;
	private List<PmUserRole> userActiveRoles;
	private List<ChallengeQAList> challengeQAList;
	private SelectItem[] challengeQADDList;
	private String currentPassword;
	private boolean profileChanged = false;
	private String passwordPolicy;
	private String roles;
	private List<String> rolesList;
	private Date softCertExpDate;
	private Date mofExpDate;
	private int roleTableScrollHeight;
	
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
	
	
	

public boolean isProfileChanged() {
		return profileChanged;
	}

	public void setProfileChanged(boolean profileChanged) {
		this.profileChanged = profileChanged;
	}

/**
	 * @return the currentUserOrg
	 */
	public PmUserOrg getCurrentUserOrg() {
		return currentUserOrg;
	}

	/**
	 * @param currentUserOrg the currentUserOrg to set
	 */
	public void setCurrentUserOrg(PmUserOrg currentUserOrg) {
		this.currentUserOrg = currentUserOrg;
	}

/**
	 * @return the currentProfile
	 */
	public PmOrgProfile getCurrentProfile() {
		if(currentProfile==null){
			currentProfile = new PmOrgProfile();
			currentProfile.setPmOrgValidity(new PmOrgValidity());
		}
		return currentProfile;
	}

	/**
	 * @param currentProfile the currentProfile to set
	 */
	public void setCurrentProfile(PmOrgProfile currentProfile) {
		this.currentProfile = currentProfile;
	}

/**
	 * @return the deletePortrait
	 */
	public boolean isDeletePortrait() {
		return deletePortrait;
	}

	/**
	 * @param deletePortrait the deletePortrait to set
	 */
	public void setDeletePortrait(boolean deletePortrait) {
		this.deletePortrait = deletePortrait;
	}

	//
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
	 * @return the userAddress
	 */
	public PmAddress getUserAddress() {
		return userAddress;
	}

	/**
	 * @param userAddress the userAddress to set
	 */
	public void setUserAddress(PmAddress userAddress) {
		this.userAddress = userAddress;
	}

	/**
	 * @return the divisionFlagC
	 */
	public boolean isDivisionFlagC() {
		return divisionFlagC;
	}

	/**
	 * @param divisionFlagC the divisionFlagC to set
	 */
	public void setDivisionFlagC(boolean divisionFlagC) {
		this.divisionFlagC = divisionFlagC;
	}
	
	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	public List<PmUserRole> getUserActiveRoles() {
		return userActiveRoles;
	}

	public void setUserActiveRoles(List<PmUserRole> userActiveRoles) {
		this.userActiveRoles = userActiveRoles;
	}

	/**
	 * @return the challengeQADDList
	 */
	public SelectItem[] getChallengeQADDList() {
		return challengeQADDList;
	}

	/**
	 * @param challengeQADDList the challengeQADDList to set
	 */
	public void setChallengeQADDList(SelectItem[] challengeQADDList) {
		this.challengeQADDList = challengeQADDList;
	}

	/**
	 * @return the challengeQAList
	 */
	public List<ChallengeQAList> getChallengeQAList() {
		return challengeQAList;
	}

	/**
	 * @return the rolesList
	 */
	public List<String> getRolesList() {
		return rolesList;
	}

	/**
	 * @param rolesList the rolesList to set
	 */
	public void setRolesList(List<String> rolesList) {
		this.rolesList = rolesList;
	}

	/**
	 * @param challengeQAList the challengeQAList to set
	 */
	public void setChallengeQAList(List<ChallengeQAList> challengeQAList) {
		this.challengeQAList = challengeQAList;
	}

	/**
	 * @return the currentPassword
	 */
	public String getCurrentPassword() {
		return currentPassword;
	}

	/**
	 * @param currentPassword the currentPassword to set
	 */
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	/**
	 * @return the passwordPolicy
	 */
	public String getPasswordPolicy() {
		return passwordPolicy;
	}

	/**
	 * @param passwordPolicy the passwordPolicy to set
	 */
	public void setPasswordPolicy(String passwordPolicy) {
		this.passwordPolicy = passwordPolicy;
	}

	/**
	 * @return the softCertExpDate
	 */
	public Date getSoftCertExpDate() {
		return softCertExpDate;
	}

	/**
	 * @param softCertExpDate the softCertExpDate to set
	 */
	public void setSoftCertExpDate(Date softCertExpDate) {
		this.softCertExpDate = softCertExpDate;
	}

	/**
	 * @return the roles
	 */
	public String getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(String roles) {
		this.roles = roles;
	}

	/**
	 * @return the mofExpDate
	 */
	public Date getMofExpDate() {
		return mofExpDate;
	}

	/**
	 * @param mofExpDate the mofExpDate to set
	 */
	public void setMofExpDate(Date mofExpDate) {
		this.mofExpDate = mofExpDate;
	}

	/**
	 * @return the roleTableScrollHeight
	 */
	public int getRoleTableScrollHeight() {
		return roleTableScrollHeight;
	}

	/**
	 * @param roleTableScrollHeight the roleTableScrollHeight to set
	 */
	public void setRoleTableScrollHeight(int roleTableScrollHeight) {
		this.roleTableScrollHeight = roleTableScrollHeight;
	}

}	
	

