package my.ep.web.managedbean.pm.profilepersonalization;


import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserNotification;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmCriteria;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.bean.pm.AddressList;
import my.ep.web.bean.pm.PreferenceList;
import my.ep.web.bean.pm.RoleList;
import my.ep.web.bean.pm.UserProfilesSearchBean;
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
	private List<PmUserNotification> notifications;
	
	
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

	/**
	 * @return the notifications
	 */
	public List<PmUserNotification> getNotifications() {
		return notifications;
	}

	/**
	 * @param notifications the notifications to set
	 */
	public void setNotifications(List<PmUserNotification> notifications) {
		this.notifications = notifications;
	}




}	
	

