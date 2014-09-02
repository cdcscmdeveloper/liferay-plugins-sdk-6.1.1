package my.ep.web.managedbean.pm.profilemanagement;


import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;


/**
 * @author DK
 *
 */
@ManagedBean(name="profileManagementManagedBean")
@SessionScoped
public class ProfileManagementManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log= EpLogger.getLogger(ProfileManagementManagedBean.class);	
	private boolean dummy; //TODO remove
	private UserProfilesSearchBean userProfileSearchBean;
	private PmUser selectedUser;
	private PmAddress userAddress; //Address Type - C
	private boolean divisionFlagC;
	private PmUserOrg selectedUserOrg;
	private List<PmUserRole> deleteUserRoles;
	private List<PmRoleDesc> selectedRoles;
	private int selectedTabIndex;
	private List<PmAddressType> selectedReceivingAddrTypes; //Address Type - R
	private List<PmAddressType> selectedDeliveringAddrTypes; //Address Type - D
	private List<PmAddressType> deleteReceivingAddrTypes; //Address Type - D
	private boolean receivingAddressFlag;
	private List<FileUploadEvent> fileUploadEvents;
	

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
	 * @return the deleteReceivingAddrTypes
	 */
	public List<PmAddressType> getDeleteReceivingAddrTypes() {
		return deleteReceivingAddrTypes;
	}
	/**
	 * @param deleteReceivingAddrTypes the deleteReceivingAddrTypes to set
	 */
	public void setDeleteReceivingAddrTypes(
			List<PmAddressType> deleteReceivingAddrTypes) {
		this.deleteReceivingAddrTypes = deleteReceivingAddrTypes;
	}
	/**
	 * @return the dummy
	 */
	public boolean isDummy() {
		return dummy;
	}
	/**
	 * @param dummy the dummy to set
	 */
	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}

	

	/**
	 * @return the selectedReceivingAddrTypes
	 */
	public List<PmAddressType> getSelectedReceivingAddrTypes() {
		return selectedReceivingAddrTypes;
	}
	/**
	 * @param selectedReceivingAddrTypes the selectedReceivingAddrTypes to set
	 */
	public void setSelectedReceivingAddrTypes(
			List<PmAddressType> selectedReceivingAddrTypes) {
		this.selectedReceivingAddrTypes = selectedReceivingAddrTypes;
	}
	/**
	 * @return the selectedDeliveringAddrTypes
	 */
	public List<PmAddressType> getSelectedDeliveringAddrTypes() {
		return selectedDeliveringAddrTypes;
	}
	/**
	 * @param selectedDeliveringAddrTypes the selectedDeliveringAddrTypes to set
	 */
	public void setSelectedDeliveringAddrTypes(
			List<PmAddressType> selectedDeliveringAddrTypes) {
		this.selectedDeliveringAddrTypes = selectedDeliveringAddrTypes;
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
	 * @return the deleteUserRoles
	 */
	public List<PmUserRole> getDeleteUserRoles() {
		return deleteUserRoles;
	}

	/**
	 * @param deleteUserRoles the deleteUserRoles to set
	 */
	public void setDeleteUserRoles(List<PmUserRole> deleteUserRoles) {
		this.deleteUserRoles = deleteUserRoles;
	}

//	/**
//	 * @return the availableRoles
//	 */
//	public List<PmRoleDesc> getAvailableRoles() {
//		return availableRoles;
//	}
//
//	/**
//	 * @param availableRoles the availableRoles to set
//	 */
//	public void setAvailableRoles(List<PmRoleDesc> availableRoles) {
//		this.availableRoles = availableRoles;
//	}

	/**
	 * @return the selectedRoles
	 */
	public List<PmRoleDesc> getSelectedRoles() {
		return selectedRoles;
	}

	/**
	 * @param selectedRoles the selectedRoles to set
	 */
	public void setSelectedRoles(List<PmRoleDesc> selectedRoles) {
		this.selectedRoles = selectedRoles;
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
	 * @return the fileUploadEvents
	 */
	public List<FileUploadEvent> getFileUploadEvents() {
		return fileUploadEvents;
	}
	/**
	 * @param fileUploadEvents the fileUploadEvents to set
	 */
	public void setFileUploadEvents(List<FileUploadEvent> fileUploadEvents) {
		this.fileUploadEvents = fileUploadEvents;
	}
	
	

}	
	

