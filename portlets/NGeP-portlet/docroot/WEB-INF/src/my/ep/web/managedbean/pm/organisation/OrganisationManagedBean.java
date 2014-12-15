package my.ep.web.managedbean.pm.organisation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmPtjGroup;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "organisationMB")
@SessionScoped
public class OrganisationManagedBean extends BaseManagedBean implements
		Serializable {
	private static final long serialVersionUID = 1L;
	private int activeIndex = 0;

	private int selectedTabIndex;
	// Organization Search
	private OrganisationSearchBean organisationSearchBean;

	private String orgType;
	private Long orgTypeId;
	// Org Details
	private PmOrgProfile orgProfile;
	private PmOrgValidity orgValidity = new PmOrgValidity();
	private Date currentDate = new Date();
	private List<PmZone> zoneList;
	private PmZone zone = new PmZone();
	private Long zoneId;
	private Long ptjGroupId;
	private Long agOfficeId;
	private Long parentOrgProfileId;
	private PmAgOffice pmAgOffice;
	private PmOrgProfile parentOrgProfile;
	// Office Address
	private List<PmAddressType> officeAddressTypeList;
	private List<PmContact> contactList;
	private PmAddressType pmAddressType;
	private PmAddress officeAddress;
	private PmContact officeContactPerson;
	private List<PmCountry> countryList;
	private List<PmState> stateList;
	private List<PmDivision> divisionList;
	private List<PmCity> cityList;
	private List<PmDistrict> districtList;

	// Delivery Address
	private List<PmAddressType> deliveryAddressList;
	private PmAddressType selectedDeliveryAddress;
	private PmAddressType deliveryAddress;
	private List<PmAddressType> addressTypesList;
	private Map<Long, List<PmAddressType>> manualAddressMap;
	
	private List<PmAddress> pmAddressList;
	private PmAddress selectedPmAddress;
	private PmAddress pmAddress;
	
	//
	private List<PmPtjGroup> ptjGroup;
	private PmPtjGroup pmPtjGroup;
	private List<PmAgOffice> agOffice;
	private List<PmOrgProfile> pmOrgProfile;
	private List<PmOrgProfile> orgProfileList;
	//
	private PmFinancialOrg financialOrg = new PmFinancialOrg();
	private List<PmFinancialOrg> pmFinancialOrg = new ArrayList<PmFinancialOrg>();
	private List<PmFinancialAcc> pmFinancialAcc = new ArrayList<PmFinancialAcc>();
	private List<PmFinancialAcc> selectedPmFinancialAcc;

	private List<PmOrgProfile> selectedPmOrgProfile;

	private List<PmPtjGroup> ptjGroupList;
	private List<PmPtjGroup> selectedPtjGroup;
	private PmPtjGroup selectedPtj= new PmPtjGroup();
	
	private List<PmProfileGroup> profileGroup;
	private List<PmProfileGroup> selectedProfileGroups;
	private PmProfileGroup selectedProfileGroup;
	private PmOrgProfile authOrgProfile;
    private List<PmUser> supplierUserList;
	private List<PmUser> supplierUsers;
	private List<PmUser> selectedSupplierUsers;
	private List<PmUser> selectedUsers;
	private PmUser user;	
	private boolean showParentMsg;

	public OrganisationManagedBean() {

		pmAddressType = new PmAddressType();
		
		officeContactPerson = new PmContact();
		parentOrgProfile= new PmOrgProfile();
		orgProfile = new PmOrgProfile();
		pmAddressType.setPmAddress(officeAddress);
		officeAddressTypeList = new ArrayList<PmAddressType>();
		contactList = new ArrayList<PmContact>();
		ptjGroup = new ArrayList<PmPtjGroup>();
		agOffice = new ArrayList<PmAgOffice>();
		orgProfile.setPmAgOffice(new PmAgOffice());
		orgProfile.setPmPtjGroup(new PmPtjGroup());
		orgProfile.setParentOrgProfile(new PmOrgProfile());
		

	}

	/**
	 * @return the contactList
	 */
	public List<PmContact> getContactList() {
		return contactList;
	}

	/**
	 * @param contactList
	 *            the contactList to set
	 */
	public void setContactList(List<PmContact> contactList) {
		this.contactList = contactList;
	}

	

	/**
	 * @return the countryList
	 */
	public List<PmCountry> getCountryList() {
		return countryList;
	}

	/**
	 * @param countryList
	 *            the countryList to set
	 */
	public void setCountryList(List<PmCountry> countryList) {
		this.countryList = countryList;
	}

	/**
	 * @return the stateList
	 */
	public List<PmState> getStateList() {
		return stateList;
	}

	/**
	 * @param stateList
	 *            the stateList to set
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * @return the divisionList
	 */
	public List<PmDivision> getDivisionList() {
		return divisionList;
	}

	/**
	 * @param divisionList
	 *            the divisionList to set
	 */
	public void setDivisionList(List<PmDivision> divisionList) {
		this.divisionList = divisionList;
	}

	/**
	 * @return the cityList
	 */
	public List<PmCity> getCityList() {
		return cityList;
	}

	/**
	 * @param cityList
	 *            the cityList to set
	 */
	public void setCityList(List<PmCity> cityList) {
		this.cityList = cityList;
	}

	/**
	 * @return the districtList
	 */
	public List<PmDistrict> getDistrictList() {
		return districtList;
	}

	/**
	 * @param districtList
	 *            the districtList to set
	 */
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
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
	 * @return the orgValidity
	 */
	public PmOrgValidity getOrgValidity() {
		return orgValidity;
	}

	/**
	 * @param orgValidity
	 *            the orgValidity to set
	 */
	public void setOrgValidity(PmOrgValidity orgValidity) {
		this.orgValidity = orgValidity;
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
	 * @return the zoneId
	 */
	public Long getZoneId() {
		return zoneId;
	}

	/**
	 * @param zoneId
	 *            the zoneId to set
	 */
	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
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
	 * @return the ptjGroup
	 */
	public List<PmPtjGroup> getPtjGroup() {
		return ptjGroup;
	}

	/**
	 * @param ptjGroup
	 *            the ptjGroup to set
	 */
	public void setPtjGroup(List<PmPtjGroup> ptjGroup) {
		this.ptjGroup = ptjGroup;
	}

	/**
	 * @return the pmPtjGroup
	 */
	public PmPtjGroup getPmPtjGroup() {
		return pmPtjGroup;
	}

	/**
	 * @param pmPtjGroup
	 *            the pmPtjGroup to set
	 */
	public void setPmPtjGroup(PmPtjGroup pmPtjGroup) {
		this.pmPtjGroup = pmPtjGroup;
	}

	/**
	 * @return the agOffice
	 */
	public List<PmAgOffice> getAgOffice() {
		return agOffice;
	}

	/**
	 * @param agOffice
	 *            the agOffice to set
	 */
	public void setAgOffice(List<PmAgOffice> agOffice) {
		this.agOffice = agOffice;
	}

	/**
	 * @return the pmOrgProfile
	 */
	public List<PmOrgProfile> getPmOrgProfile() {
		return pmOrgProfile;
	}

	/**
	 * @param pmOrgProfile
	 *            the pmOrgProfile to set
	 */
	public void setPmOrgProfile(List<PmOrgProfile> pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
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
	 * @return the pmFinancialOrg
	 */
	public List<PmFinancialOrg> getPmFinancialOrg() {
		return pmFinancialOrg;
	}

	/**
	 * @param pmFinancialOrg
	 *            the pmFinancialOrg to set
	 */
	public void setPmFinancialOrg(List<PmFinancialOrg> pmFinancialOrg) {
		this.pmFinancialOrg = pmFinancialOrg;
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
	 * @return the selectedPmFinancialAcc
	 */
	public List<PmFinancialAcc> getSelectedPmFinancialAcc() {
		return selectedPmFinancialAcc;
	}

	/**
	 * @param selectedPmFinancialAcc
	 *            the selectedPmFinancialAcc to set
	 */
	public void setSelectedPmFinancialAcc(
			List<PmFinancialAcc> selectedPmFinancialAcc) {
		this.selectedPmFinancialAcc = selectedPmFinancialAcc;
	}

	/**
	 * @return the selectedPmOrgProfile
	 */
	public List<PmOrgProfile> getSelectedPmOrgProfile() {
		return selectedPmOrgProfile;
	}

	/**
	 * @param selectedPmOrgProfile
	 *            the selectedPmOrgProfile to set
	 */
	public void setSelectedPmOrgProfile(List<PmOrgProfile> selectedPmOrgProfile) {
		this.selectedPmOrgProfile = selectedPmOrgProfile;
	}

	/**
	 * @return the ptjGroupList
	 */
	public List<PmPtjGroup> getPtjGroupList() {
		return ptjGroupList;
	}

	/**
	 * @param ptjGroupList
	 *            the ptjGroupList to set
	 */
	public void setPtjGroupList(List<PmPtjGroup> ptjGroupList) {
		this.ptjGroupList = ptjGroupList;
	}

	/**
	 * @return the selectedPtjGroup
	 */
	public List<PmPtjGroup> getSelectedPtjGroup() {
		return selectedPtjGroup;
	}

	/**
	 * @param selectedPtjGroup
	 *            the selectedPtjGroup to set
	 */
	public void setSelectedPtjGroup(List<PmPtjGroup> selectedPtjGroup) {
		this.selectedPtjGroup = selectedPtjGroup;
	}


	/**
	 * @return the selectedUsers
	 */
	public List<PmUser> getSelectedUsers() {
		return selectedUsers;
	}

	/**
	 * @param selectedUsers
	 *            the selectedUsers to set
	 */
	public void setSelectedUsers(List<PmUser> selectedUsers) {
		this.selectedUsers = selectedUsers;
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
	 * @return the selectedPtj
	 */
	public PmPtjGroup getSelectedPtj() {
		return selectedPtj;
	}

	/**
	 * @param selectedPtj
	 *            the selectedPtj to set
	 */
	public void setSelectedPtj(PmPtjGroup selectedPtj) {
		this.selectedPtj = selectedPtj;
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
		if(organisationSearchBean== null){
			organisationSearchBean= new OrganisationSearchBean();
		}
		return organisationSearchBean;
	}

	/**
	 * @param organisationSearchBean the organisationSearchBean to set
	 */
	public void setOrganisationSearchBean(OrganisationSearchBean organisationSearchBean) {
		this.organisationSearchBean = organisationSearchBean;
	}
	

	/**
	 * @return the orgTypeId
	 */
	public Long getOrgTypeId() {
		return orgTypeId;
	}

	/**
	 * @param orgTypeId the orgTypeId to set
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
	 * @param deliveryAddressList the deliveryAddressList to set
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
	 * @param selectedDeliveryAddress the selectedDeliveryAddress to set
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
	 * @param deliveryAddress the deliveryAddress to set
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
	 * @param addressTypesList the addressTypesList to set
	 */
	public void setAddressTypesList(List<PmAddressType> addressTypesList) {
		this.addressTypesList = addressTypesList;
	}

	/**
	 * @return the manualAddressMap
	 */
	public Map<Long, List<PmAddressType>> getManualAddressMap() {
		return manualAddressMap;
	}

	/**
	 * @param manualAddressMap the manualAddressMap to set
	 */
	public void setManualAddressMap(Map<Long, List<PmAddressType>> manualAddressMap) {
		this.manualAddressMap = manualAddressMap;
	}

	/**
	 * @return the ptjGroupId
	 */
	public Long getPtjGroupId() {
		return ptjGroupId;
	}

	/**
	 * @param ptjGroupId the ptjGroupId to set
	 */
	public void setPtjGroupId(Long ptjGroupId) {
		this.ptjGroupId = ptjGroupId;
	}

	/**
	 * @return the agOfficeId
	 */
	public Long getAgOfficeId() {
		return agOfficeId;
	}

	/**
	 * @param agOfficeId the agOfficeId to set
	 */
	public void setAgOfficeId(Long agOfficeId) {
		this.agOfficeId = agOfficeId;
	}

	/**
	 * @return the parentOrgProfileId
	 */
	public Long getParentOrgProfileId() {
		return parentOrgProfileId;
	}

	/**
	 * @param parentOrgProfileId the parentOrgProfileId to set
	 */
	public void setParentOrgProfileId(Long parentOrgProfileId) {
		this.parentOrgProfileId = parentOrgProfileId;
	}

	/**
	 * @return the pmAgOffice
	 */
	public PmAgOffice getPmAgOffice() {
		return pmAgOffice;
	}

	/**
	 * @param pmAgOffice the pmAgOffice to set
	 */
	public void setPmAgOffice(PmAgOffice pmAgOffice) {
		this.pmAgOffice = pmAgOffice;
	}

	/**
	 * @return the parentOrgProfile
	 */
	public PmOrgProfile getParentOrgProfile() {
		return parentOrgProfile;
	}

	/**
	 * @param parentOrgProfile the parentOrgProfile to set
	 */
	public void setParentOrgProfile(PmOrgProfile parentOrgProfile) {
		this.parentOrgProfile = parentOrgProfile;
	}

	/**
	 * @return the profileGroup
	 */
	public List<PmProfileGroup> getProfileGroup() {
		return profileGroup;
	}

	/**
	 * @param profileGroup the profileGroup to set
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
	 * @param selectedProfileGroups the selectedProfileGroups to set
	 */
	public void setSelectedProfileGroups(List<PmProfileGroup> selectedProfileGroups) {
		this.selectedProfileGroups = selectedProfileGroups;
	}

	/**
	 * @return the selectedProfileGroup
	 */
	public PmProfileGroup getSelectedProfileGroup() {
		return selectedProfileGroup;
	}

	/**
	 * @param selectedProfileGroup the selectedProfileGroup to set
	 */
	public void setSelectedProfileGroup(PmProfileGroup selectedProfileGroup) {
		this.selectedProfileGroup = selectedProfileGroup;
	}

	/**
	 * @return the supplierUsers
	 */
	public List<PmUser> getSupplierUsers() {
		return supplierUsers;
	}

	/**
	 * @param supplierUsers the supplierUsers to set
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
	 * @param supplierUserList the supplierUserList to set
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
	 * @param selectedSupplierUsers the selectedSupplierUsers to set
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
	 * @param user the user to set
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
	 * @param showParentMsg the showParentMsg to set
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
	 * @param authOrgProfile the authOrgProfile to set
	 */
	public void setAuthOrgProfile(PmOrgProfile authOrgProfile) {
		this.authOrgProfile = authOrgProfile;
	}

	/**
	 * @return the pmAddressList
	 */
	public List<PmAddress> getPmAddressList() {
		return pmAddressList;
	}

	/**
	 * @param pmAddressList the pmAddressList to set
	 */
	public void setPmAddressList(List<PmAddress> pmAddressList) {
		this.pmAddressList = pmAddressList;
	}

	/**
	 * @return the selectedPmAddress
	 */
	public PmAddress getSelectedPmAddress() {
		return selectedPmAddress;
	}

	/**
	 * @param selectedPmAddress the selectedPmAddress to set
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
	 * @param pmAddress the pmAddress to set
	 */
	public void setPmAddress(PmAddress pmAddress) {
		this.pmAddress = pmAddress;
	}

	


	

}
