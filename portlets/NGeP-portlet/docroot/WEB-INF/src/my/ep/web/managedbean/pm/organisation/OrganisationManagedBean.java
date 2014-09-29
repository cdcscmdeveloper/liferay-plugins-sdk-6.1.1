package my.ep.web.managedbean.pm.organisation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
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
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "organisationMB")
@SessionScoped
public class OrganisationManagedBean extends BaseManagedBean implements
		Serializable {
	private static final long serialVersionUID = 1L;
	private int activeIndex = 0;
	private int organisationActiveIndex;
	// Organization Search
	private String orgTypeCode;
	private String orgCode;
	private String orgName;
	private String orgStatus;
	private String orgType;
	// Org Details
	private PmOrgProfile orgProfile = new PmOrgProfile();
	private PmOrgValidity orgValidity = new PmOrgValidity();
	private Date currentDate = new Date();
	private List<PmZone> zoneList;
	private PmZone zone= new PmZone();
	private Long zoneId;
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
	private PmAddress deliveryAddress;
	private PmContact deliveryContactPerson;
	private List<PmAddressType> deliveryAddressTypeList;
	private List<PmAddressType> selectedDelAddressTypes;
	private PmAddressType selectedDeliveryAddressType;
//
	private List<PmPtjGroup> ptjGroup;
	private PmPtjGroup pmPtjGroup;
	private List<PmAgOffice> agOffice;
	private List<PmOrgProfile> pmOrgProfile;
	private List<PmOrgProfile> orgProfileList;
	//
	private List<PmFinancialOrg> pmFinancialOrg = new ArrayList<PmFinancialOrg>();
	private List<PmFinancialAcc> pmFinancialAcc =  new ArrayList<PmFinancialAcc>();
	private List<PmFinancialAcc> selectedPmFinancialAcc;
	
	private List<PmOrgProfile> selectedPmOrgProfile;
	
	public OrganisationManagedBean() {

		pmAddressType = new PmAddressType();
		officeContactPerson= new PmContact();
		
		pmAddressType.setPmAddress(officeAddress);
		officeAddressTypeList= new ArrayList<PmAddressType>();
		contactList= new ArrayList<PmContact>();
		ptjGroup= new ArrayList<PmPtjGroup>();
		agOffice= new ArrayList<PmAgOffice>();

	}

	/**
	 * @return the contactList
	 */
	public List<PmContact> getContactList() {
		return contactList;
	}

	/**
	 * @param contactList the contactList to set
	 */
	public void setContactList(List<PmContact> contactList) {
		this.contactList = contactList;
	}

	/**
	 * @return the deliveryAddress
	 */
	public PmAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @return the deliveryAddressTypeList
	 */
	public List<PmAddressType> getDeliveryAddressTypeList() {
		return deliveryAddressTypeList;
	}

	/**
	 * @param deliveryAddressTypeList
	 *            the deliveryAddressTypeList to set
	 */
	public void setDeliveryAddressTypeList(
			List<PmAddressType> deliveryAddressTypeList) {
		this.deliveryAddressTypeList = deliveryAddressTypeList;
	}

	/**
	 * @param deliveryAddress
	 *            the deliveryAddress to set
	 */
	public void setDeliveryAddress(PmAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
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
	 * @return the organisationActiveIndex
	 */
	public int getOrganisationActiveIndex() {
		return organisationActiveIndex;
	}

	/**
	 * @param organisationActiveIndex
	 *            the organisationActiveIndex to set
	 */
	public void setOrganisationActiveIndex(int organisationActiveIndex) {
		this.organisationActiveIndex = organisationActiveIndex;
	}

	/**
	 * @return the orgTypeCode
	 */
	public String getOrgTypeCode() {
		return orgTypeCode;
	}

	/**
	 * @param orgTypeCode
	 *            the orgTypeCode to set
	 */
	public void setOrgTypeCode(String orgTypeCode) {
		this.orgTypeCode = orgTypeCode;
	}

	/**
	 * @return the orgCode
	 */
	public String getOrgCode() {
		return orgCode;
	}

	/**
	 * @param orgCode
	 *            the orgCode to set
	 */
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}

	/**
	 * @param orgName
	 *            the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	/**
	 * @return the orgStatus
	 */
	public String getOrgStatus() {
		return orgStatus;
	}

	/**
	 * @param orgStatus
	 *            the orgStatus to set
	 */
	public void setOrgStatus(String orgStatus) {
		this.orgStatus = orgStatus;
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
	 * @return the selectedDelAddressTypes
	 */
	public List<PmAddressType> getSelectedDelAddressTypes() {
		return selectedDelAddressTypes;
	}

	/**
	 * @param selectedDelAddressTypes
	 *            the selectedDelAddressTypes to set
	 */
	public void setSelectedDelAddressTypes(
			List<PmAddressType> selectedDelAddressTypes) {
		this.selectedDelAddressTypes = selectedDelAddressTypes;
	}

	/**
	 * @return the selectedDeliveryAddressType
	 */
	public PmAddressType getSelectedDeliveryAddressType() {
		return selectedDeliveryAddressType;
	}

	/**
	 * @param selectedDeliveryAddressType
	 *            the selectedDeliveryAddressType to set
	 */
	public void setSelectedDeliveryAddressType(
			PmAddressType selectedDeliveryAddressType) {
		this.selectedDeliveryAddressType = selectedDeliveryAddressType;
	}

	/**
	 * @return the zone
	 */
	public PmZone getZone() {
		return zone;
	}

	/**
	 * @param zone the zone to set
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
	 * @param officeAddressTypeList the officeAddressTypeList to set
	 */
	public void setOfficeAddressTypeList(List<PmAddressType> officeAddressTypeList) {
		this.officeAddressTypeList = officeAddressTypeList;
	}

	/**
	 * @return the ptjGroup
	 */
	public List<PmPtjGroup> getPtjGroup() {
		return ptjGroup;
	}

	/**
	 * @param ptjGroup the ptjGroup to set
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
	 * @param pmPtjGroup the pmPtjGroup to set
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
	 * @param agOffice the agOffice to set
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
	 * @param pmOrgProfile the pmOrgProfile to set
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
	 * @param orgProfileList the orgProfileList to set
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
	 * @param pmFinancialOrg the pmFinancialOrg to set
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
	 * @param pmFinancialAcc the pmFinancialAcc to set
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
	 * @param selectedPmFinancialAcc the selectedPmFinancialAcc to set
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
	 * @param selectedPmOrgProfile the selectedPmOrgProfile to set
	 */
	public void setSelectedPmOrgProfile(List<PmOrgProfile> selectedPmOrgProfile) {
		this.selectedPmOrgProfile = selectedPmOrgProfile;
	}
	

}
