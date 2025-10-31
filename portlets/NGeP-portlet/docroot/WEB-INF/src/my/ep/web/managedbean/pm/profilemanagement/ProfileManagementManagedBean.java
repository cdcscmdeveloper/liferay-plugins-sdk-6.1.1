/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.profilemanagement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

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
 * Managed Bean for Profile Management
 * 
 * @author RAIN POH
 * @since 06/08/2014
 * 
 */
@ManagedBean(name = "profileManagementManagedBean")
@SessionScoped
public class ProfileManagementManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(ProfileManagementManagedBean.class);
	private UserProfilesSearchBean userProfileSearchBean;
	private PmUser selectedUser;
	private PmAddress userAddress; // Address Type - C
	private boolean divisionFlagC;
	private PmUserOrg selectedUserOrg;
	private List<PmUserRole> deleteUserRoles;
	private List<PmRoleDesc> selectedRoles;
	private PmRoleDesc selectedRole;
	private int selectedTabIndex;
	private List<PmAddressType> selectedReceivingAddrTypes; // Address Type - R
	private List<PmAddressType> selectedDeliveringAddrTypes; // Address Type - D
	private List<PmAddressType> deleteReceivingAddrTypes; // Address Type - D
	private boolean receivingAddressFlag;
	private Map<PmUserAttachment, FileUploadEvent> fileEventMapper;
	private List<PmUserAttachment> deletePmUserAttachments;
	private boolean deletePortrait;
	private boolean addressEntered;
	private PmOrgProfile currentActiveOrg;
	private String additionalOrgFilter;
	private PmRoleDesc selectedPmRoleDesc;
	private PmOrgProfile selectedOrgProfile;
	private PmParameter selectedOrgType;
	private PmUserOrg activeUserOrg;
	private List<PmUserRole> activeUserRoleFor = new ArrayList<PmUserRole>();
	private Integer searchOrgCategory;
	private int orgCategory = 0;
	private boolean isLazy = false;
	private PmOrgProfile selectedCurrentOrgProfile;
	private Boolean isNewUser;
	private PmUser pmUserOriginal;
	private PmUserOrg pmUserOrgOriginal;
	private Map<String, String> roleCodeToLetterNoMap = new HashMap<String, String>();
	private Map<String, BigDecimal> roleCodeToApprovalLimitMap = new HashMap<String, BigDecimal>();
	private boolean isActiveUser;
	private Integer roleTabActiveIndex = 0;
	private boolean isDisabledOrgFields = false;
	private List<PmRoleDesc> roleDescResults;
	private PmRoleDesc selectedRoleDesc;
	private PmUserRole selectedDeleteUserRole;
	private PmUserAttachment selectedDeleteAttachment;
	private PmAddressType selectedDeleteReceivingAddr;
	private String searchRoleName;
	private Integer selectedRoleSearchOption;
	private boolean isPmUserExist = false;
	private SelectItem[] orgTypeList;
	//#35489 - Start
	private boolean isOrgUnitStatusChange = false;
	private boolean isUserStatusChange = false;
	//#35489 - End
	private boolean suppGovSellerUser = false; // #36489

	/************ AUDIT #28895-35509 *************/
	private Long brandNewUserOrgId;
	private PmUser oldAuditPmUser;
	private PmUserOrg oldAuditPmUserOrg;
	private List<PmUserRole> oldAuditPmUserRoles;
	private List<PmAddressType> oldAuditPmAddressTypes;
	private PmUser newAuditPmUser;
	private PmUserOrg newAuditPmUserOrg;
	private List<PmUserRole> newAuditPmUserRoles;
	private List<PmAddressType> newAuditPmAddressTypes;
	/************ AUDIT #28895-35509 *************/
	
	public ProfileManagementManagedBean(){}
	
	/**
	 * @return the additionalOrgFilter
	 */
	public String getAdditionalOrgFilter() {
		return additionalOrgFilter;
	}

	/**
	 * @param additionalOrgFilter
	 *            the additionalOrgFilter to set
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
	 * @param currentActiveOrg
	 *            the currentActiveOrg to set
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
	 * @param addressEntered
	 *            the addressEntered to set
	 */
	public void setAddressEntered(boolean addressEntered) {
		this.addressEntered = addressEntered;
	}

	/**
	 * @return the fileEventMapper
	 */
	public Map<PmUserAttachment, FileUploadEvent> getFileEventMapper() {
		return fileEventMapper;
	}

	/**
	 * @param fileEventMapper
	 *            the fileEventMapper to set
	 */
	public void setFileEventMapper(
			Map<PmUserAttachment, FileUploadEvent> fileEventMapper) {
		this.fileEventMapper = fileEventMapper;
	}

	/**
	 * @return the receivingAddressFlag
	 */
	public boolean isReceivingAddressFlag() {
		return receivingAddressFlag;
	}

	/**
	 * @param receivingAddressFlag
	 *            the receivingAddressFlag to set
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
	 * @param deleteReceivingAddrTypes
	 *            the deleteReceivingAddrTypes to set
	 */
	public void setDeleteReceivingAddrTypes(
			List<PmAddressType> deleteReceivingAddrTypes) {
		this.deleteReceivingAddrTypes = deleteReceivingAddrTypes;
	}

	/**
	 * @return the selectedReceivingAddrTypes
	 */
	public List<PmAddressType> getSelectedReceivingAddrTypes() {
		return selectedReceivingAddrTypes;
	}

	/**
	 * @param selectedReceivingAddrTypes
	 *            the selectedReceivingAddrTypes to set
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
	 * @param selectedDeliveringAddrTypes
	 *            the selectedDeliveringAddrTypes to set
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
	 * @param selectedUser
	 *            the selectedUser to set
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
	 * @param userAddress
	 *            the userAddress to set
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
	 * @param divisionFlagC
	 *            the divisionFlagC to set
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
	 * @param selectedUserOrg
	 *            the selectedUserOrg to set
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
	 * @param deleteUserRoles
	 *            the deleteUserRoles to set
	 */
	public void setDeleteUserRoles(List<PmUserRole> deleteUserRoles) {
		this.deleteUserRoles = deleteUserRoles;
	}

	// /**
	// * @return the availableRoles
	// */
	// public List<PmRoleDesc> getAvailableRoles() {
	// return availableRoles;
	// }
	//
	// /**
	// * @param availableRoles the availableRoles to set
	// */
	// public void setAvailableRoles(List<PmRoleDesc> availableRoles) {
	// this.availableRoles = availableRoles;
	// }

	/**
	 * @return the selectedRoles
	 */
	public List<PmRoleDesc> getSelectedRoles() {
		return selectedRoles;
	}

	/**
	 * @param selectedRoles
	 *            the selectedRoles to set
	 */
	public void setSelectedRoles(List<PmRoleDesc> selectedRoles) {
		this.selectedRoles = selectedRoles;
	}

	/**
	 * @return the userProfileSearchBean
	 */
	public UserProfilesSearchBean getUserProfileSearchBean() {
		if (userProfileSearchBean == null)
			userProfileSearchBean = new UserProfilesSearchBean();
		return userProfileSearchBean;
	}

	/**
	 * @param userProfileSearchBean
	 *            the userProfileSearchBean to set
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
	 * @param selectedTabIndex
	 *            the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * @return the deletePmUserAttachments
	 */
	public List<PmUserAttachment> getDeletePmUserAttachments() {
		return deletePmUserAttachments;
	}

	/**
	 * @param deletePmUserAttachments
	 *            the deletePmUserAttachments to set
	 */
	public void setDeletePmUserAttachments(
			List<PmUserAttachment> deletePmUserAttachments) {
		this.deletePmUserAttachments = deletePmUserAttachments;
	}

	/**
	 * @return the deletePortrait
	 */
	public boolean isDeletePortrait() {
		return deletePortrait;
	}

	/**
	 * @param deletePortrait
	 *            the deletePortrait to set
	 */
	public void setDeletePortrait(boolean deletePortrait) {
		this.deletePortrait = deletePortrait;
	}

	/**
	 * @return the selectedRole
	 */
	public PmRoleDesc getSelectedRole() {
		return selectedRole;
	}

	/**
	 * @param selectedRole
	 *            the selectedRole to set
	 */
	public void setSelectedRole(PmRoleDesc selectedRole) {
		this.selectedRole = selectedRole;
	}

	/**
	 * @return the selectedPmRoleDesc
	 */
	public PmRoleDesc getSelectedPmRoleDesc() {
		log.info("...........getSelectedPmRoleDesc..........");
		if (selectedPmRoleDesc == null) {
			this.selectedPmRoleDesc = new PmRoleDesc();
		}
		return selectedPmRoleDesc;
	}

	/**
	 * @param selectedPmRoleDesc
	 *            the selectedPmRoleDesc to set
	 */
	public void setSelectedPmRoleDesc(PmRoleDesc selectedPmRoleDesc) {
		log.info("...........setSelectedPmRoleDesc..........");
		if (selectedPmRoleDesc != null) {
			log.info("...........selectedPmRoleDesc.getRoleName="
					+ selectedPmRoleDesc.getRoleName());
			log.info("...........selectedPmRoleDesc.getRoleDescId="
					+ selectedPmRoleDesc.getRoleDescId());
		}
		this.selectedPmRoleDesc = selectedPmRoleDesc;

	}

	/**
	 * @return the selectedOrgProfile
	 */
	public PmOrgProfile getSelectedOrgProfile() {
		if (selectedOrgProfile == null) {
			this.selectedOrgProfile = new PmOrgProfile();
		}
		return selectedOrgProfile;
	}

	/**
	 * @param selectedOrgProfile
	 *            the selectedOrgProfile to set
	 */
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}
	
	/**
	 * @return the selectedOrgType
	 */
	public PmParameter getSelectedOrgType() {
		if (this.selectedOrgType == null) {
			this.selectedOrgType = new PmParameter();
		}
		return selectedOrgType;
	}

	/**
	 * @param selectedOrgType
	 *            the selectedOrgType to set
	 */
	public void setSelectedOrgType(PmParameter selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	/**
	 * @return the searchOrgCategory
	 */
	public Integer getSearchOrgCategory() {
		return searchOrgCategory;
	}

	/**
	 * @param searchOrgCategory
	 *            the searchOrgCategory to set
	 */
	public void setSearchOrgCategory(Integer searchOrgCategory) {
		this.searchOrgCategory = searchOrgCategory;
	}

	/**
	 * @return the orgCategory
	 */
	public int getOrgCategory() {
		return orgCategory;
	}

	/**
	 * @param orgCategory
	 *            the orgCategory to set
	 */
	public void setOrgCategory(int orgCategory) {
		this.orgCategory = orgCategory;
	}

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * @param isLazy
	 *            the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * @return the selectedCurrentOrgProfile
	 */
	public PmOrgProfile getSelectedCurrentOrgProfile() {
		return selectedCurrentOrgProfile;
	}

	/**
	 * @param selectedCurrentOrgProfile
	 *            the selectedCurrentOrgProfile to set
	 */
	public void setSelectedCurrentOrgProfile(
			PmOrgProfile selectedCurrentOrgProfile) {
		this.selectedCurrentOrgProfile = selectedCurrentOrgProfile;
	}

	/**
	 * @return the isNewUser
	 */
	public Boolean getIsNewUser() {
		return isNewUser;
	}

	/**
	 * @param isNewUser
	 *            the isNewUser to set
	 */
	public void setIsNewUser(Boolean isNewUser) {
		this.isNewUser = isNewUser;
	}

	/**
	 * @return the isActiveUser
	 */
	public boolean isActiveUser() {
		return isActiveUser;
	}

	/**
	 * @param isActiveUser
	 *            the isActiveUser to set
	 */
	public void setActiveUser(boolean isActiveUser) {
		this.isActiveUser = isActiveUser;
	}

	/**
	 * @return the roleTabActiveIndex
	 */
	public Integer getRoleTabActiveIndex() {
		return roleTabActiveIndex;
	}

	/**
	 * @param roleTabActiveIndex
	 *            the roleTabActiveIndex to set
	 */
	public void setRoleTabActiveIndex(Integer roleTabActiveIndex) {
		this.roleTabActiveIndex = roleTabActiveIndex;
	}

	/**
	 * @return the isDisabledOrgFields
	 */
	public boolean isDisabledOrgFields() {
		return isDisabledOrgFields;
	}

	/**
	 * @param isDisabledOrgFields
	 *            the isDisabledOrgFields to set
	 */
	public void setDisabledOrgFields(boolean isDisabledOrgFields) {
		this.isDisabledOrgFields = isDisabledOrgFields;
	}

	/**
	 * @return the roleDescResults
	 */
	public List<PmRoleDesc> getRoleDescResults() {
		if (roleDescResults == null) {
			roleDescResults = new ArrayList<PmRoleDesc>();
		}
		return roleDescResults;
	}

	/**
	 * @param roleDescResults
	 *            the roleDescResults to set
	 */
	public void setRoleDescResults(List<PmRoleDesc> roleDescResults) {
		this.roleDescResults = roleDescResults;
	}

	/**
	 * @return the selectedRoleDesc
	 */
	public PmRoleDesc getSelectedRoleDesc() {
		return selectedRoleDesc;
	}

	/**
	 * @param selectedRoleDesc
	 *            the selectedRoleDesc to set
	 */
	public void setSelectedRoleDesc(PmRoleDesc selectedRoleDesc) {
		this.selectedRoleDesc = selectedRoleDesc;
	}

	/**
	 * @return the selectedDeleteUserRole
	 */
	public PmUserRole getSelectedDeleteUserRole() {
		return selectedDeleteUserRole;
	}

	/**
	 * @param selectedDeleteUserRole
	 *            the selectedDeleteUserRole to set
	 */
	public void setSelectedDeleteUserRole(PmUserRole selectedDeleteUserRole) {
		this.selectedDeleteUserRole = selectedDeleteUserRole;
	}

	/**
	 * @return the selectedDeleteAttachment
	 */
	public PmUserAttachment getSelectedDeleteAttachment() {
		return selectedDeleteAttachment;
	}

	/**
	 * @param selectedDeleteAttachment
	 *            the selectedDeleteAttachment to set
	 */
	public void setSelectedDeleteAttachment(
			PmUserAttachment selectedDeleteAttachment) {
		this.selectedDeleteAttachment = selectedDeleteAttachment;
	}

	/**
	 * @return the selectedDeleteReceivingAddr
	 */
	public PmAddressType getSelectedDeleteReceivingAddr() {
		return selectedDeleteReceivingAddr;
	}

	/**
	 * @param selectedDeleteReceivingAddr
	 *            the selectedDeleteReceivingAddr to set
	 */
	public void setSelectedDeleteReceivingAddr(
			PmAddressType selectedDeleteReceivingAddr) {
		this.selectedDeleteReceivingAddr = selectedDeleteReceivingAddr;
	}

	/**
	 * @return the searchRoleName
	 */
	public String getSearchRoleName() {
		return searchRoleName;
	}

	/**
	 * @param searchRoleName
	 *            the searchRoleName to set
	 */
	public void setSearchRoleName(String searchRoleName) {
		this.searchRoleName = searchRoleName;
	}

	/**
	 * @return the selectedRoleSearchOption
	 */
	public Integer getSelectedRoleSearchOption() {
		return selectedRoleSearchOption;
	}

	/**
	 * @param selectedRoleSearchOption
	 *            the selectedRoleSearchOption to set
	 */
	public void setSelectedRoleSearchOption(Integer selectedRoleSearchOption) {
		this.selectedRoleSearchOption = selectedRoleSearchOption;
	}

	/**
	 * @return the isPmUserExist
	 */
	public boolean isPmUserExist() {
		return isPmUserExist;
	}

	/**
	 * @param isPmUserExist
	 *            the isPmUserExist to set
	 */
	public void setPmUserExist(boolean isPmUserExist) {
		this.isPmUserExist = isPmUserExist;
	}

	/**
	 * @return the activeUserOrg
	 */
	public PmUserOrg getActiveUserOrg() {
		return activeUserOrg;
	}

	/**
	 * @param activeUserOrg the activeUserOrg to set
	 */
	public void setActiveUserOrg(PmUserOrg activeUserOrg) {
		this.activeUserOrg = activeUserOrg;
	}

	/**
	 * @return the activeUserRoleFor
	 */
	public List<PmUserRole> getActiveUserRoleFor() {
		return activeUserRoleFor;
	}

	/**
	 * @param activeUserRoleFor the activeUserRoleFor to set
	 */
	public void setActiveUserRoleFor(List<PmUserRole> activeUserRoleFor) {
		this.activeUserRoleFor = activeUserRoleFor;
	}

	/**
	 * @return the pmUserOriginal
	 */
	public PmUser getPmUserOriginal() {
		return pmUserOriginal;
	}

	/**
	 * @param pmUserOriginal the pmUserOriginal to set
	 */
	public void setPmUserOriginal(PmUser pmUserOriginal) {
		this.pmUserOriginal = pmUserOriginal;
	}

	/**
	 * @return the pmUserOrgOriginal
	 */
	public PmUserOrg getPmUserOrgOriginal() {
		return pmUserOrgOriginal;
	}

	/**
	 * @param pmUserOrgOriginal the pmUserOrgOriginal to set
	 */
	public void setPmUserOrgOriginal(PmUserOrg pmUserOrgOriginal) {
		this.pmUserOrgOriginal = pmUserOrgOriginal;
	}

	/**
	 * @return the roleCodeToLetterNoMap
	 */
	public Map<String, String> getRoleCodeToLetterNoMap() {
		return roleCodeToLetterNoMap;
	}

	/**
	 * @param roleCodeToLetterNoMap the roleCodeToLetterNoMap to set
	 */
	public void setRoleCodeToLetterNoMap(Map<String, String> roleCodeToLetterNoMap) {
		this.roleCodeToLetterNoMap = roleCodeToLetterNoMap;
	}

	/**
	 * @return the roleCodeToApprovalLimitMap
	 */
	public Map<String, BigDecimal> getRoleCodeToApprovalLimitMap() {
		return roleCodeToApprovalLimitMap;
	}

	/**
	 * @param roleCodeToApprovalLimitMap the roleCodeToApprovalLimitMap to set
	 */
	public void setRoleCodeToApprovalLimitMap(
			Map<String, BigDecimal> roleCodeToApprovalLimitMap) {
		this.roleCodeToApprovalLimitMap = roleCodeToApprovalLimitMap;
	}

	public SelectItem[] getOrgTypeList() {
		return orgTypeList;
	}

	public void setOrgTypeList(SelectItem[] orgTypeList) {
		this.orgTypeList = orgTypeList;
	}
	
	//#35489 - Start
	public boolean isOrgUnitStatusChange() {
		return isOrgUnitStatusChange;
	}

	public void setOrgUnitStatusChange(boolean isOrgUnitStatusChange) {
		this.isOrgUnitStatusChange = isOrgUnitStatusChange;
	}
	
	public boolean isUserStatusChange() {
		return isUserStatusChange;
	}

	public void setUserStatusChange(boolean isUserStatusChange) {
		this.isUserStatusChange = isUserStatusChange;
	}
	//#35489 - End
	
	//#36489 - Start
	/**
	 * @return the suppGovSellerUser
	 */
	public boolean isSuppGovSellerUser() {
		return suppGovSellerUser;
	}

	/**
	 * @param suppGovSellerUser
	 *            the suppGovSellerUser to set
	 */
	public void setSuppGovSellerUser(boolean suppGovSellerUser) {
		this.suppGovSellerUser = suppGovSellerUser;
	}
	//#36489 - End

	/************ START: AUDIT #35509 *************/
	public PmUser getOldAuditPmUser() {
		return oldAuditPmUser;
	}
	
	public void setOldAuditPmUser(PmUser oldAuditPmUser) {
		this.oldAuditPmUser = oldAuditPmUser;
	}
	
	public PmUserOrg getOldAuditPmUserOrg() {
		return oldAuditPmUserOrg;
	}

	public void setOldAuditPmUserOrg(PmUserOrg oldAuditPmUserOrg) {
		this.oldAuditPmUserOrg = oldAuditPmUserOrg;
	}

	/**
	 * @return the oldAuditPmUserRole
	 */
	public List<PmUserRole> getOldAuditPmUserRoles() {
		return oldAuditPmUserRoles;
	}

	/**
	 * @param oldAuditPmUserRole the oldAuditPmUserRole to set
	 */
	public void setOldAuditPmUserRoles(List<PmUserRole> oldAuditPmUserRoles) {
		this.oldAuditPmUserRoles = oldAuditPmUserRoles;
	}

	/**
	 * @return the newAuditPmUser
	 */
	public PmUser getNewAuditPmUser() {
		return newAuditPmUser;
	}

	/**
	 * @param newAuditPmUser the newAuditPmUser to set
	 */
	public void setNewAuditPmUser(PmUser newAuditPmUser) {
		this.newAuditPmUser = newAuditPmUser;
	}

	/**
	 * @return the newAuditPmUserOrg
	 */
	public PmUserOrg getNewAuditPmUserOrg() {
		return newAuditPmUserOrg;
	}

	/**
	 * @param newAuditPmUserOrg the newAuditPmUserOrg to set
	 */
	public void setNewAuditPmUserOrg(PmUserOrg newAuditPmUserOrg) {
		this.newAuditPmUserOrg = newAuditPmUserOrg;
	}

	/**
	 * @return the newAuditPmUserRole
	 */
	public List<PmUserRole> getNewAuditPmUserRoles() {
		return newAuditPmUserRoles;
	}

	/**
	 * @param newAuditPmUserRole the newAuditPmUserRole to set
	 */

	public void setNewAuditPmUserRoles(List<PmUserRole> newAuditPmUserRoles) {
		this.newAuditPmUserRoles = newAuditPmUserRoles;
	}

	/**
	 * @return the oldAuditPmAddressTypes
	 */
	public List<PmAddressType> getOldAuditPmAddressTypes() {
		return oldAuditPmAddressTypes;
	}

	/**
	 * @param oldAuditPmAddressTypes the oldAuditPmAddressTypes to set
	 */
	public void setOldAuditPmAddressTypes(List<PmAddressType> oldAuditPmAddressTypes) {
		this.oldAuditPmAddressTypes = oldAuditPmAddressTypes;
	}

	/**
	 * @return the newAuditPmAddressTypes
	 */
	public List<PmAddressType> getNewAuditPmAddressTypes() {
		return newAuditPmAddressTypes;
	}

	/**
	 * @param newAuditPmAddressTypes the newAuditPmAddressTypes to set
	 */
	public void setNewAuditPmAddressTypes(List<PmAddressType> newAuditPmAddressTypes) {
		this.newAuditPmAddressTypes = newAuditPmAddressTypes;
	}

	/**
	 * @return the brandNewUserOrgId
	 */
	public Long getBrandNewUserOrgId() {
		return brandNewUserOrgId;
	}

	/**
	 * @param brandNewUserOrgId the brandNewUserOrgId to set
	 */
	public void setBrandNewUserOrgId(Long brandNewUserOrgId) {
		this.brandNewUserOrgId = brandNewUserOrgId;
	}
	/************ END: AUDIT #35509 *************/
}
