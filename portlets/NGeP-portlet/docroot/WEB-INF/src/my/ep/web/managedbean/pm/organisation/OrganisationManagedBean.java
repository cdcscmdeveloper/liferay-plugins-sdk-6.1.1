package my.ep.web.managedbean.pm.organisation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
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
	private String orgTypeCode;
	private String orgCode;
	private String orgName;
	private String orgStatus;
	private String orgType;
	private String status;
	private PmOrgValidity orgValidity;
	private PmOrgProfile orgProfile;
	private PmOrgValidity pmOrgValidity;
	private List<PmPtjGroup> ptjGroup;
	private PmPtjGroup pmPtjGroup;
	private List<PmAgOffice> agOffice;
	private List<PmOrgProfile> pmOrgProfile;
	private List<PmOrgProfile> selectedPmOrgProfile;
	private List<PmOrgValidity> pmorgValidityList;
	private List<PmOrgValidity> selectedOrgValidityList;
	private List<PmState> states;
	private List<PmCountry> countries;
	private List<PmDistrict> districts;
	private List<PmCity> citys;
	private List<PmZone> zones;
	private Long zoneId;
	private PmAddressType pmAddressType;
	private PmAddress pmAddress;	
	private PmContact contactPerson;
	private List<PmContact> pmContacts;
	private List<PmAddress> address;
	private List<PmAddress> selectAddress;
	private PmAddress selectedAddress;
	private List<PmAddressType> addressTypes;
	private List<PmAddressType>selectedAddressTypes;
	private PmAddressType selectedPmAddressType;
	private List<SelectItem> selections;
	private Date expDate;
	private Date effDate;
	private Date currentDate= new Date();

	public OrganisationManagedBean() {
		setPmOrgValidity(new PmOrgValidity());		
		orgProfile = new PmOrgProfile();
		pmAddress = new PmAddress();
		pmPtjGroup = new PmPtjGroup();
		pmAddressType = new PmAddressType();
		contactPerson = new PmContact();
		//addressTypes.add(pmAddressType);
		pmAddress.setPmCity(new PmCity());
		pmAddress.setPmDistrict(new PmDistrict());
		pmAddress.setPmCountry(new PmCountry());
		pmAddress.setPmState(new PmState());
		pmAddressType.setPmAddress(pmAddress);
		pmAddressType.setPmContacts(pmContacts);
		orgProfile.setPmOrgValidity(pmOrgValidity);

	}

	/**
	 * @return the states
	 */
	public List<PmState> getStates() {
		return states;
	}

	/**
	 * @param states
	 *            the states to set
	 */
	public void setStates(List<PmState> states) {
		this.states = states;
	}

	/**
	 * @return the countries
	 */
	public List<PmCountry> getCountries() {
		return countries;
	}

	/**
	 * @param countries
	 *            the countries to set
	 */
	public void setCountries(List<PmCountry> countries) {
		this.countries = countries;
	}

	/**
	 * @return the districts
	 */
	public List<PmDistrict> getDistricts() {
		return districts;
	}

	/**
	 * @param districts
	 *            the districts to set
	 */
	public void setDistricts(List<PmDistrict> districts) {
		this.districts = districts;
	}

	/**
	 * @return the citys
	 */
	public List<PmCity> getCitys() {
		return citys;
	}

	/**
	 * @param citys
	 *            the citys to set
	 */
	public void setCitys(List<PmCity> citys) {
		this.citys = citys;
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
	 * @return the contactPerson
	 */
	public PmContact getContactPerson() {
		return contactPerson;
	}

	/**
	 * @param contactPerson
	 *            the contactPerson to set
	 */
	public void setContactPerson(PmContact contactPerson) {
		this.contactPerson = contactPerson;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
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
	 * @return the address
	 */
	public List<PmAddress> getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(List<PmAddress> address) {
		this.address = address;
	}

	/**
	 * @return the addressTypes
	 */
	public List<PmAddressType> getAddressTypes() {
		return addressTypes;
	}

	/**
	 * @param addressTypes
	 *            the addressTypes to set
	 */
	public void setAddressTypes(List<PmAddressType> addressTypes) {
		this.addressTypes = addressTypes;
	}

	/**
	 * @return the selectAddress
	 */
	public List<PmAddress> getSelectAddress() {
		return selectAddress;
	}

	/**
	 * @param selectAddress
	 *            the selectAddress to set
	 */
	public void setSelectAddress(List<PmAddress> selectAddress) {
		this.selectAddress = selectAddress;
	}

	/**
	 * @return the selections
	 */
	public List<SelectItem> getSelections() {
		return selections;
	}

	/**
	 * @param selections
	 *            the selections to set
	 */
	public void setSelections(List<SelectItem> selections) {
		this.selections = selections;
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
	 * @return the pmorgValidityList
	 */
	public List<PmOrgValidity> getPmorgValidityList() {
		return pmorgValidityList;
	}

	/**
	 * @param pmorgValidityList
	 *            the pmorgValidityList to set
	 */
	public void setPmorgValidityList(List<PmOrgValidity> pmorgValidityList) {
		this.pmorgValidityList = pmorgValidityList;
	}

	/**
	 * @return the selectedOrgValidityList
	 */
	public List<PmOrgValidity> getSelectedOrgValidityList() {
		return selectedOrgValidityList;
	}

	/**
	 * @param selectedOrgValidityList
	 *            the selectedOrgValidityList to set
	 */
	public void setSelectedOrgValidityList(
			List<PmOrgValidity> selectedOrgValidityList) {
		this.selectedOrgValidityList = selectedOrgValidityList;
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
	 * @return the pmOrgValidity
	 */
	public PmOrgValidity getPmOrgValidity() {
		return pmOrgValidity;
	}

	/**
	 * @param pmOrgValidity
	 *            the pmOrgValidity to set
	 */
	public void setPmOrgValidity(PmOrgValidity pmOrgValidity) {
		this.pmOrgValidity = pmOrgValidity;
	}

	/**
	 * @return the expDate
	 */
	public Date getExpDate() {
		return expDate;
	}

	/**
	 * @param expDate
	 *            the expDate to set
	 */
	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}

	/**
	 * @return the effDate
	 */
	public Date getEffDate() {
		return effDate;
	}

	/**
	 * @param effDate
	 *            the effDate to set
	 */
	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}

	/**
	 * @return the pmContacts
	 */
	public List<PmContact> getPmContacts() {
		return pmContacts;
	}

	/**
	 * @param pmContacts the pmContacts to set
	 */
	public void setPmContacts(List<PmContact> pmContacts) {
		this.pmContacts = pmContacts;
	}

	/**
	 * @return the selectedAddressTypes
	 */
	public List<PmAddressType> getSelectedAddressTypes() {
		return selectedAddressTypes;
	}

	/**
	 * @param selectedAddressTypes the selectedAddressTypes to set
	 */
	public void setSelectedAddressTypes(List<PmAddressType> selectedAddressTypes) {
		this.selectedAddressTypes = selectedAddressTypes;
	}

	/**
	 * @return the zones
	 */
	public List<PmZone> getZones() {
		return zones;
	}

	/**
	 * @param zones the zones to set
	 */
	public void setZones(List<PmZone> zones) {
		this.zones = zones;
	}

	/**
	 * @return the zoneId
	 */
	public Long getZoneId() {
		return zoneId;
	}

	/**
	 * @param zoneId the zoneId to set
	 */
	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	/**
	 * @return the orgStatus
	 */
	public String getOrgStatus() {
		return orgStatus;
	}

	/**
	 * @param orgStatus the orgStatus to set
	 */
	public void setOrgStatus(String orgStatus) {
		this.orgStatus = orgStatus;
	}

	/**
	 * @return the orgTypeCode
	 */
	public String getOrgTypeCode() {
		return orgTypeCode;
	}

	/**
	 * @param orgTypeCode the orgTypeCode to set
	 */
	public void setOrgTypeCode(String orgTypeCode) {
		this.orgTypeCode = orgTypeCode;
	}

	/**
	 * @return the currentDate
	 */
	public Date getCurrentDate() {
		return currentDate;
	}

	/**
	 * @param currentDate the currentDate to set
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	/**
	 * @return the selectedAddress
	 */
	public PmAddress getSelectedAddress() {
		return selectedAddress;
	}

	/**
	 * @param selectedAddress the selectedAddress to set
	 */
	public void setSelectedAddress(PmAddress selectedAddress) {
		this.selectedAddress = selectedAddress;
	}

	/**
	 * @return the selectedPmAddressType
	 */
	public PmAddressType getSelectedPmAddressType() {
		return selectedPmAddressType;
	}

	/**
	 * @param selectedPmAddressType the selectedPmAddressType to set
	 */
	public void setSelectedPmAddressType(PmAddressType selectedPmAddressType) {
		this.selectedPmAddressType = selectedPmAddressType;
	}



}
