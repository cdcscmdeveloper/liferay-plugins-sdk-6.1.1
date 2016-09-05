/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.organisation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Organization
 * 
 * @author venugopal
 * @since 30/07/2014
 * 
 */
@ManagedBean(name = "organisationMB")
@SessionScoped
public class OrganisationManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private int activeIndex = 0;

	private int selectedTabIndex;
	// Organization Search
	private OrganisationSearchBean organisationSearchBean;
	private Integer searchOrgCategory = 0;
	private int orgCategory = 0;
	private String orgType;
	private Long orgTypeId;
	private SelectItem[] orgTypeList;
	// Org Details
	private PmOrgProfile orgProfile;
	private Date currentDate = new Date();
	private List<PmZone> zoneList;
	private PmZone zone = new PmZone();

	// Office Address
	private List<PmAddressType> officeAddressTypeList;
	private PmAddressType pmAddressType;
	private PmAddress officeAddress;
	private PmContact officeContactPerson;

	// Delivery Address
	private List<PmAddressType> deliveryAddressList;
	private PmAddressType selectedDeliveryAddress;
	private PmAddressType deliveryAddress;
	private List<PmAddressType> addressTypesList = new ArrayList<PmAddressType>();

	private PmContact deliveryContactPerson;
	private List<PmAddressType> selectedAddressTypes;

	private List<PmAddress> addressList;
	private PmAddress selectedPmAddress;
	private PmAddress pmAddress = new PmAddress();
	private List<PmOrgProfile> orgProfileList;
	private PmFinancialOrg financialOrg = new PmFinancialOrg();
	private List<PmFinancialOrg> financialBanks = new ArrayList<PmFinancialOrg>();
	private List<PmFinancialAcc> pmFinancialAcc = new ArrayList<PmFinancialAcc>();

	private List<SmSupplierBank> smSupplierBanks = new ArrayList<SmSupplierBank>();
	private List<SmSupplierBank> deletedSupplierBankList = new ArrayList<SmSupplierBank>();
	private List<SmPersonnel> smPersonnels = new ArrayList<SmPersonnel>();
	private List<SmPersonnel> deletedSmPersonnelList = new ArrayList<SmPersonnel>();

	private List<PmProfileGroup> profileGroup;
	private List<PmProfileGroup> selectedProfileGroups;
	private PmProfileGroup selectedProfileGroup;
	private PmProfileGroup pmProfileGroup;
	private PmOrgProfile authOrgProfile;
	private List<PmUser> supplierUserList;
	private List<PmUser> supplierUsers;
	private List<PmUser> selectedSupplierUsers;

	private PmUser user;
	private boolean showParentMsg;
	private boolean emptyMinistry;

	private boolean islazy = false;
	
	private SmGstSupplier gstSupplier;
	
	private Long selectedStateId;
	
	private SmSupplierBank selectedDeletePaymentBank;
	private SmPersonnel selectedDeleteSupplierUser;
	private PmProfileGroup selectedDeleteOrgUnit;
	
	private boolean notFirstRun;
	private Long selectedZoneId;

	public OrganisationManagedBean() {

		pmAddressType = new PmAddressType();
		deliveryAddress = new PmAddressType();
		officeContactPerson = new PmContact();
		orgProfile = new PmOrgProfile();
		pmAddressType.setPmAddress(new PmAddress());
		officeAddressTypeList = new ArrayList<PmAddressType>();
		orgProfile.setParentOrgProfile(new PmOrgProfile());
		deliveryAddress.setPmAddress(new PmAddress());
		deliveryContactPerson = new PmContact();

	}

	/**
	 * @return the officeAddress
	 */
	public PmAddress getOfficeAddress() {
		return officeAddress;
	}

	/**
	 * @param officeAddress
	 *            the officeAddress to set
	 */
	public void setOfficeAddress(PmAddress officeAddress) {
		this.officeAddress = officeAddress;
	}

	/**
	 * @return the officeContactPerson
	 */
	public PmContact getOfficeContactPerson() {
		return officeContactPerson;
	}

	/**
	 * @param officeContactPerson
	 *            the officeContactPerson to set
	 */
	public void setOfficeContactPerson(PmContact officeContactPerson) {
		this.officeContactPerson = officeContactPerson;
	}

	/**
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * @param activeIndex
	 *            the activeIndex to set
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	/**
	 * @return the orgType
	 */
	public String getOrgType() {
		return orgType;
	}

	/**
	 * @param orgType
	 *            the orgType to set
	 */
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	/**
	 * @return the orgProfile
	 */
	public PmOrgProfile getOrgProfile() {
		return orgProfile;
	}

	/**
	 * @param orgProfile
	 *            the orgProfile to set
	 */
	public void setOrgProfile(PmOrgProfile orgProfile) {
		this.orgProfile = orgProfile;
	}

	/**
	 * @return the currentDate
	 */
	public Date getCurrentDate() {
		return currentDate;
	}

	/**
	 * @param currentDate
	 *            the currentDate to set
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	/**
	 * @return the zoneList
	 */
	public List<PmZone> getZoneList() {
		return zoneList;
	}

	/**
	 * @param zoneList
	 *            the zoneList to set
	 */
	public void setZoneList(List<PmZone> zoneList) {
		this.zoneList = zoneList;
	}

	/**
	 * @return the pmAddressType
	 */
	public PmAddressType getPmAddressType() {
		return pmAddressType;
	}

	/**
	 * @param pmAddressType
	 *            the pmAddressType to set
	 */
	public void setPmAddressType(PmAddressType pmAddressType) {
		this.pmAddressType = pmAddressType;
	}

	/**
	 * @return the zone
	 */
	public PmZone getZone() {
		return zone;
	}

	/**
	 * @param zone
	 *            the zone to set
	 */
	public void setZone(PmZone zone) {
		this.zone = zone;
	}

	/**
	 * @return the officeAddressTypeList
	 */
	public List<PmAddressType> getOfficeAddressTypeList() {
		return officeAddressTypeList;
	}

	/**
	 * @param officeAddressTypeList
	 *            the officeAddressTypeList to set
	 */
	public void setOfficeAddressTypeList(
			List<PmAddressType> officeAddressTypeList) {
		this.officeAddressTypeList = officeAddressTypeList;
	}

	/**
	 * @return the orgProfileList
	 */
	public List<PmOrgProfile> getOrgProfileList() {
		return orgProfileList;
	}

	/**
	 * @param orgProfileList
	 *            the orgProfileList to set
	 */
	public void setOrgProfileList(List<PmOrgProfile> orgProfileList) {
		this.orgProfileList = orgProfileList;
	}

	/**
	 * @return the pmFinancialAcc
	 */
	public List<PmFinancialAcc> getPmFinancialAcc() {
		return pmFinancialAcc;
	}

	/**
	 * @param pmFinancialAcc
	 *            the pmFinancialAcc to set
	 */
	public void setPmFinancialAcc(List<PmFinancialAcc> pmFinancialAcc) {
		this.pmFinancialAcc = pmFinancialAcc;
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
	 * @return the financialOrg
	 */
	public PmFinancialOrg getFinancialOrg() {
		return financialOrg;
	}

	/**
	 * @param financialOrg
	 *            the financialOrg to set
	 */
	public void setFinancialOrg(PmFinancialOrg financialOrg) {
		this.financialOrg = financialOrg;
	}

	/**
	 * @return the organisationSearchBean
	 */
	public OrganisationSearchBean getOrganisationSearchBean() {
		if (organisationSearchBean == null) {
			organisationSearchBean = new OrganisationSearchBean();
		}
		return organisationSearchBean;
	}

	/**
	 * @param organisationSearchBean
	 *            the organisationSearchBean to set
	 */
	public void setOrganisationSearchBean(
			OrganisationSearchBean organisationSearchBean) {
		this.organisationSearchBean = organisationSearchBean;
	}

	/**
	 * @return the orgTypeId
	 */
	public Long getOrgTypeId() {
		return orgTypeId;
	}

	/**
	 * @param orgTypeId
	 *            the orgTypeId to set
	 */
	public void setOrgTypeId(Long orgTypeId) {
		this.orgTypeId = orgTypeId;
	}

	/**
	 * @return the deliveryAddressList
	 */
	public List<PmAddressType> getDeliveryAddressList() {
		return deliveryAddressList;
	}

	/**
	 * @param deliveryAddressList
	 *            the deliveryAddressList to set
	 */
	public void setDeliveryAddressList(List<PmAddressType> deliveryAddressList) {
		this.deliveryAddressList = deliveryAddressList;
	}

	/**
	 * @return the selectedDeliveryAddress
	 */
	public PmAddressType getSelectedDeliveryAddress() {
		return selectedDeliveryAddress;
	}

	/**
	 * @param selectedDeliveryAddress
	 *            the selectedDeliveryAddress to set
	 */
	public void setSelectedDeliveryAddress(PmAddressType selectedDeliveryAddress) {
		this.selectedDeliveryAddress = selectedDeliveryAddress;
	}

	/**
	 * @return the deliveryAddress
	 */
	public PmAddressType getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress
	 *            the deliveryAddress to set
	 */
	public void setDeliveryAddress(PmAddressType deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the addressTypesList
	 */
	public List<PmAddressType> getAddressTypesList() {
		return addressTypesList;
	}

	/**
	 * @param addressTypesList
	 *            the addressTypesList to set
	 */
	public void setAddressTypesList(List<PmAddressType> addressTypesList) {
		this.addressTypesList = addressTypesList;
	}

	/**
	 * @return the profileGroup
	 */
	public List<PmProfileGroup> getProfileGroup() {
		return profileGroup;
	}

	/**
	 * @param profileGroup
	 *            the profileGroup to set
	 */
	public void setProfileGroup(List<PmProfileGroup> profileGroup) {
		this.profileGroup = profileGroup;
	}

	/**
	 * @return the selectedProfileGroups
	 */
	public List<PmProfileGroup> getSelectedProfileGroups() {
		return selectedProfileGroups;
	}

	/**
	 * @param selectedProfileGroups
	 *            the selectedProfileGroups to set
	 */
	public void setSelectedProfileGroups(
			List<PmProfileGroup> selectedProfileGroups) {
		this.selectedProfileGroups = selectedProfileGroups;
	}

	/**
	 * @return the selectedProfileGroup
	 */
	public PmProfileGroup getSelectedProfileGroup() {
		return selectedProfileGroup;
	}

	/**
	 * @param selectedProfileGroup
	 *            the selectedProfileGroup to set
	 */
	public void setSelectedProfileGroup(PmProfileGroup selectedProfileGroup) {
		this.selectedProfileGroup = selectedProfileGroup;
	}

	/**
	 * @return the supplierUsers
	 */
	public List<PmUser> getSupplierUsers() {
		if(supplierUsers == null){
			supplierUsers = new ArrayList<PmUser>();
		}
		return supplierUsers;
	}

	/**
	 * @param supplierUsers
	 *            the supplierUsers to set
	 */
	public void setSupplierUsers(List<PmUser> supplierUsers) {
		this.supplierUsers = supplierUsers;
	}

	/**
	 * @return the supplierUserList
	 */
	public List<PmUser> getSupplierUserList() {
		return supplierUserList;
	}

	/**
	 * @param supplierUserList
	 *            the supplierUserList to set
	 */
	public void setSupplierUserList(List<PmUser> supplierUserList) {
		this.supplierUserList = supplierUserList;
	}

	/**
	 * @return the selectedSupplierUsers
	 */
	public List<PmUser> getSelectedSupplierUsers() {
		return selectedSupplierUsers;
	}

	/**
	 * @param selectedSupplierUsers
	 *            the selectedSupplierUsers to set
	 */
	public void setSelectedSupplierUsers(List<PmUser> selectedSupplierUsers) {
		this.selectedSupplierUsers = selectedSupplierUsers;
	}

	/**
	 * @return the user
	 */
	public PmUser getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(PmUser user) {
		this.user = user;
	}

	/**
	 * @return the showParentMsg
	 */
	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	/**
	 * @param showParentMsg
	 *            the showParentMsg to set
	 */
	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	/**
	 * @return the authOrgProfile
	 */
	public PmOrgProfile getAuthOrgProfile() {
		return authOrgProfile;
	}

	/**
	 * @param authOrgProfile
	 *            the authOrgProfile to set
	 */
	public void setAuthOrgProfile(PmOrgProfile authOrgProfile) {
		this.authOrgProfile = authOrgProfile;
	}

	/**
	 * @return the selectedPmAddress
	 */
	public PmAddress getSelectedPmAddress() {
		return selectedPmAddress;
	}

	/**
	 * @param selectedPmAddress
	 *            the selectedPmAddress to set
	 */
	public void setSelectedPmAddress(PmAddress selectedPmAddress) {
		this.selectedPmAddress = selectedPmAddress;
	}

	/**
	 * @return the pmAddress
	 */
	public PmAddress getPmAddress() {
		return pmAddress;
	}

	/**
	 * @param pmAddress
	 *            the pmAddress to set
	 */
	public void setPmAddress(PmAddress pmAddress) {
		this.pmAddress = pmAddress;
	}

	/**
	 * @return the pmProfileGroup
	 */
	public PmProfileGroup getPmProfileGroup() {
		return pmProfileGroup;
	}

	/**
	 * @param pmProfileGroup
	 *            the pmProfileGroup to set
	 */
	public void setPmProfileGroup(PmProfileGroup pmProfileGroup) {
		this.pmProfileGroup = pmProfileGroup;
	}

	public List<SmSupplierBank> getSmSupplierBanks() {
		return smSupplierBanks;
	}

	public void setSmSupplierBanks(List<SmSupplierBank> smSupplierBanks) {
		this.smSupplierBanks = smSupplierBanks;
	}

	public List<SmPersonnel> getSmPersonnels() {
		return smPersonnels;
	}

	public void setSmPersonnels(List<SmPersonnel> smPersonnels) {
		this.smPersonnels = smPersonnels;
	}

	public List<SmSupplierBank> getDeletedSupplierBankList() {
		return deletedSupplierBankList;
	}

	public void setDeletedSupplierBankList(
			List<SmSupplierBank> deletedSupplierBankList) {
		this.deletedSupplierBankList = deletedSupplierBankList;
	}

	public List<SmPersonnel> getDeletedSmPersonnelList() {
		return deletedSmPersonnelList;
	}

	public void setDeletedSmPersonnelList(
			List<SmPersonnel> deletedSmPersonnelList) {
		this.deletedSmPersonnelList = deletedSmPersonnelList;
	}

	/**
	 * @return the addressList
	 */
	public List<PmAddress> getAddressList() {
		return addressList;
	}

	/**
	 * @param addressList
	 *            the addressList to set
	 */
	public void setAddressList(List<PmAddress> addressList) {
		this.addressList = addressList;
	}

	/**
	 * @return the deliveryContactPerson
	 */
	public PmContact getDeliveryContactPerson() {
		return deliveryContactPerson;
	}

	/**
	 * @param deliveryContactPerson
	 *            the deliveryContactPerson to set
	 */
	public void setDeliveryContactPerson(PmContact deliveryContactPerson) {
		this.deliveryContactPerson = deliveryContactPerson;
	}

	/**
	 * @return the selectedAddressTypes
	 */
	public List<PmAddressType> getSelectedAddressTypes() {
		return selectedAddressTypes;
	}

	/**
	 * @param selectedAddressTypes
	 *            the selectedAddressTypes to set
	 */
	public void setSelectedAddressTypes(List<PmAddressType> selectedAddressTypes) {
		this.selectedAddressTypes = selectedAddressTypes;
	}

	/**
	 * @return the financialBanks
	 */
	public List<PmFinancialOrg> getFinancialBanks() {
		return financialBanks;
	}

	/**
	 * @param financialBanks
	 *            the financialBanks to set
	 */
	public void setFinancialBanks(List<PmFinancialOrg> financialBanks) {
		this.financialBanks = financialBanks;
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
	 * @return the orgTypeList
	 */
	public SelectItem[] getOrgTypeList() {
		return orgTypeList;
	}

	/**
	 * @param orgTypeList
	 *            the orgTypeList to set
	 */
	public void setOrgTypeList(SelectItem[] orgTypeList) {
		this.orgTypeList = orgTypeList;
	}

	/**
	 * @return the emptyMinistry
	 */
	public boolean isEmptyMinistry() {
		return emptyMinistry;
	}

	/**
	 * @param emptyMinistry
	 *            the emptyMinistry to set
	 */
	public void setEmptyMinistry(boolean emptyMinistry) {
		this.emptyMinistry = emptyMinistry;
	}

	/**
	 * @return the islazy
	 */
	public boolean isIslazy() {
		return islazy;
	}

	/**
	 * @param islazy
	 *            the islazy to set
	 */
	public void setIslazy(boolean islazy) {
		this.islazy = islazy;
	}

	/**
	 * @return the gstSupplier
	 */
	public SmGstSupplier getGstSupplier() {
		return gstSupplier;
	}

	/**
	 * @param gstSupplier the gstSupplier to set
	 */
	public void setGstSupplier(SmGstSupplier gstSupplier) {
		this.gstSupplier = gstSupplier;
	}

	/**
	 * @return the selectedStateId
	 */
	public Long getSelectedStateId() {
		return selectedStateId;
	}

	/**
	 * @param selectedStateId the selectedStateId to set
	 */
	public void setSelectedStateId(Long selectedStateId) {
		this.selectedStateId = selectedStateId;
	}

	/**
	 * @return the selectedDeletePaymentBank
	 */
	public SmSupplierBank getSelectedDeletePaymentBank() {
		return selectedDeletePaymentBank;
	}

	/**
	 * @param selectedDeletePaymentBank the selectedDeletePaymentBank to set
	 */
	public void setSelectedDeletePaymentBank(SmSupplierBank selectedDeletePaymentBank) {
		this.selectedDeletePaymentBank = selectedDeletePaymentBank;
	}

	/**
	 * @return the selectedDeleteSupplierUser
	 */
	public SmPersonnel getSelectedDeleteSupplierUser() {
		return selectedDeleteSupplierUser;
	}

	/**
	 * @param selectedDeleteSupplierUser the selectedDeleteSupplierUser to set
	 */
	public void setSelectedDeleteSupplierUser(SmPersonnel selectedDeleteSupplierUser) {
		this.selectedDeleteSupplierUser = selectedDeleteSupplierUser;
	}

	/**
	 * @return the selectedDeleteOrgUnit
	 */
	public PmProfileGroup getSelectedDeleteOrgUnit() {
		return selectedDeleteOrgUnit;
	}

	/**
	 * @param selectedDeleteOrgUnit the selectedDeleteOrgUnit to set
	 */
	public void setSelectedDeleteOrgUnit(PmProfileGroup selectedDeleteOrgUnit) {
		this.selectedDeleteOrgUnit = selectedDeleteOrgUnit;
	}

	/**
	 * @return the notFirstRun
	 */
	public boolean isNotFirstRun() {
		return notFirstRun;
	}

	/**
	 * @param notFirstRun the notFirstRun to set
	 */
	public void setNotFirstRun(boolean notFirstRun) {
		this.notFirstRun = notFirstRun;
	}

	/**
	 * @return the selectedZoneId
	 */
	public Long getSelectedZoneId() {
		return selectedZoneId;
	}

	/**
	 * @param selectedZoneId the selectedZoneId to set
	 */
	public void setSelectedZoneId(Long selectedZoneId) {
		this.selectedZoneId = selectedZoneId;
	}
}
