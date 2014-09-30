package my.ep.web.managedbean.pm.category;

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
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "categoryPM")
@SessionScoped
public class CategoryManagedBean extends BaseManagedBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private int activeIndex = 0;
	private int organisationActiveIndex;
	
	private String mainCatCode;
	private String subCatCode;
	private String subDivCode;
	private String mainCatDesc;
	private String subCatDesc;
	private String subDivDesc;
	
	public String getMainCatCode() {
		return mainCatCode;
	}

	public void setMainCatCode(String mainCatCode) {
		this.mainCatCode = mainCatCode;
	}

	public String getSubCatCode() {
		return subCatCode;
	}

	public void setSubCatCode(String subCatCode) {
		this.subCatCode = subCatCode;
	}

	public String getSubDivCode() {
		return subDivCode;
	}

	public void setSubDivCode(String subDivCode) {
		this.subDivCode = subDivCode;
	}

	public String getMainCatDesc() {
		return mainCatDesc;
	}

	public void setMainCatDesc(String mainCatDesc) {
		this.mainCatDesc = mainCatDesc;
	}

	public String getSubCatDesc() {
		return subCatDesc;
	}

	public void setSubCatDesc(String subCatDesc) {
		this.subCatDesc = subCatDesc;
	}

	public String getSubDivDesc() {
		return subDivDesc;
	}

	public void setSubDivDesc(String subDivDesc) {
		this.subDivDesc = subDivDesc;
	}
	
	
	
	
	
	
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

	public CategoryManagedBean() {

		pmAddressType = new PmAddressType();
		officeContactPerson= new PmContact();
		
		pmAddressType.setPmAddress(officeAddress);
		officeAddressTypeList= new ArrayList<PmAddressType>();
		contactList= new ArrayList<PmContact>();

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

}
