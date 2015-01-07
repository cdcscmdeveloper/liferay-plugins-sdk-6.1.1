package my.ep.web.managedbean.pm.financialorg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialBranch;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.bean.pm.FinancialOrgSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "financialOrgManagedBean")
@SessionScoped
public class FinancialOrgManagedBean extends BaseManagedBean implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Financial Search
	private FinancialOrgSearchBean financialOrgSearchBean;

	private int selectedTabIndex;

	// Financial Svc
	;
	private PmFinancialSvc financialSvc;
	private List<PmFinancialSvc> pmFinancialSvc;
	private List<PmFinancialSvc> pmFinancialSvcUpdate;
	private List<PmFinancialSvc> selectedPmFinancialSvc;
	private PmFinancialSvc selectedFinancialSvc;
	private boolean byFactoring = true;
	private int activeIndex = 0;
	private boolean enableUpdateStatus;
	
	
	// Financial Org
	private List<PmFinancialOrg> pmFinancialOrg = new ArrayList<PmFinancialOrg>();
	private PmFinancialOrg financialOrg = new PmFinancialOrg();

	// Financial Acc
	private PmFinancialAcc financialAcc;
	private List<PmFinancialAcc> pmFinancialAcc = new ArrayList<PmFinancialAcc>();
	private List<PmFinancialAcc> selectedPmFinancialAcc;

	// Financial Branch
	private PmFinancialBranch financialBranch;
	private List<PmFinancialBranch> pmFinancialBranch;
	private PmFinancialBranch selectedPmFinancialBranch;
	// Financial Org address
	private PmAddressType pmAddressType;;
	private List<PmAddressType> addressType;
	private PmAddress pmAddress;
	private PmContact contactPerson;
	private List<PmContact> pmContact;
	private List<PmState> stateList;
	private List<PmCountry> countryList;
	private List<PmDistrict> districtList;
	private List<PmCity> cityList;
	private List<PmDivision> divisionList;

	// Financial Branch Address
	private List<PmState> branchStateList;
	private List<PmCountry> branchCountryList;
	private List<PmDistrict> branchDistrictList;
	private List<PmCity> branchCityList;
	private List<PmDivision> branchDivisionList;
	private PmAddressType branchAddressType;;
	private List<PmAddressType> branchAddressList;
	private PmAddress branchAddress;
	private PmContact branchContactPerson;
	private List<PmContact> branchContact;
	private boolean branchAddressreq = false;

	// Financial Branch Acc
	private PmFinancialAcc financialBranchAcc;
	private List<PmFinancialAcc> pmFinancialBranchAcc;
	private List<PmFinancialAcc> selectedPmFinancialBranchAcc;

	private List<PmOrgProfile> orgProfileList;
	private boolean showParentMsg;
	private String serviceType;
	private List<String> serviceTypes;

	private Map<String, Object> mapObject = new HashMap<String, Object>();

	private Date currentDate = new Date();
	private boolean dropDownValue = false;

	public FinancialOrgManagedBean() {
		financialSvc = new PmFinancialSvc();
		addressType = new ArrayList<PmAddressType>();
		pmContact = new ArrayList<PmContact>();
		pmAddress = new PmAddress();
		pmAddressType = new PmAddressType();
		contactPerson = new PmContact();
		// contactPerson.setPmAddressType(pmAddressType);
		pmAddress.setPmCity(new PmCity());
		pmAddress.setPmDistrict(new PmDistrict());
		pmAddress.setPmCountry(new PmCountry());
		pmAddress.setPmState(new PmState());
		// pmAddress.setPmDivision(new PmDivision());
		pmAddressType.setPmAddress(pmAddress);
		financialOrg.setPmCountry(new PmCountry());
		// Branch Address
		branchAddressList = new ArrayList<PmAddressType>();
		branchContact = new ArrayList<PmContact>();
		branchAddress = new PmAddress();
		branchAddressType = new PmAddressType();
		branchContactPerson = new PmContact();
		branchAddressType.setPmAddress(branchAddress);
		// branchContactPerson.setPmAddressType(branchAddressType);
		branchAddress.setPmCity(new PmCity());
		branchAddress.setPmDistrict(new PmDistrict());
		branchAddress.setPmCountry(new PmCountry());
		branchAddress.setPmState(new PmState());
		// branchAddress.setPmDivision(new PmDivision());
		// branchAddressType.setPmAddress(branchAddress);
		orgProfileList = new ArrayList<PmOrgProfile>();

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
	 * @return the citysList
	 */
	public List<PmCity> getCityList() {
		return cityList;
	}

	/**
	 * @param citysList
	 *            the citysList to set
	 */
	public void setCityList(List<PmCity> cityList) {
		this.cityList = cityList;
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
	 * @return the financialBranchAcc
	 */
	public PmFinancialAcc getFinancialBranchAcc() {
		return financialBranchAcc;
	}

	/**
	 * @param financialBranchAcc
	 *            the financialBranchAcc to set
	 */
	public void setFinancialBranchAcc(PmFinancialAcc financialBranchAcc) {
		this.financialBranchAcc = financialBranchAcc;
	}

	/**
	 * @return the pmFinancialBranchAcc
	 */
	public List<PmFinancialAcc> getPmFinancialBranchAcc() {
		return pmFinancialBranchAcc;
	}

	/**
	 * @param pmFinancialBranchAcc
	 *            the pmFinancialBranchAcc to set
	 */
	public void setPmFinancialBranchAcc(
			List<PmFinancialAcc> pmFinancialBranchAcc) {
		this.pmFinancialBranchAcc = pmFinancialBranchAcc;
	}

	/**
	 * @return the selectedPmFinancialBranchAcc
	 */
	public List<PmFinancialAcc> getSelectedPmFinancialBranchAcc() {
		return selectedPmFinancialBranchAcc;
	}

	/**
	 * @param selectedPmFinancialBranchAcc
	 *            the selectedPmFinancialBranchAcc to set
	 */
	public void setSelectedPmFinancialBranchAcc(
			List<PmFinancialAcc> selectedPmFinancialBranchAcc) {
		this.selectedPmFinancialBranchAcc = selectedPmFinancialBranchAcc;
	}

	/**
	 * @return the branchAddressType
	 */
	public PmAddressType getBranchAddressType() {
		return branchAddressType;
	}

	/**
	 * @param branchAddressType
	 *            the branchAddressType to set
	 */
	public void setBranchAddressType(PmAddressType branchAddressType) {
		this.branchAddressType = branchAddressType;
	}

	/**
	 * @return the branchAddressList
	 */
	public List<PmAddressType> getBranchAddressList() {
		return branchAddressList;
	}

	/**
	 * @param branchAddressList
	 *            the branchAddressList to set
	 */
	public void setBranchAddressList(List<PmAddressType> branchAddressList) {
		this.branchAddressList = branchAddressList;
	}

	/**
	 * @return the branchAddress
	 */
	public PmAddress getBranchAddress() {
		return branchAddress;
	}

	/**
	 * @param branchAddress
	 *            the branchAddress to set
	 */
	public void setBranchAddress(PmAddress branchAddress) {
		this.branchAddress = branchAddress;
	}

	/**
	 * @return the branchContactPerson
	 */
	public PmContact getBranchContactPerson() {
		return branchContactPerson;
	}

	/**
	 * @param branchContactPerson
	 *            the branchContactPerson to set
	 */
	public void setBranchContactPerson(PmContact branchContactPerson) {
		this.branchContactPerson = branchContactPerson;
	}

	/**
	 * @return the branchContact
	 */
	public List<PmContact> getBranchContact() {
		return branchContact;
	}

	/**
	 * @param branchContact
	 *            the branchContact to set
	 */
	public void setBranchContact(List<PmContact> branchContact) {
		this.branchContact = branchContact;
	}

	public PmAddress getPmAddress() {
		return pmAddress;
	}

	public void setPmAddress(PmAddress pmAddress) {
		this.pmAddress = pmAddress;
	}

	public List<PmContact> getPmContact() {
		return pmContact;
	}

	public void setPmContact(List<PmContact> pmContact) {
		this.pmContact = pmContact;
	}

	public PmFinancialSvc getFinancialSvc() {
		return financialSvc;
	}

	public void setFinancialSvc(PmFinancialSvc financialSvc) {
		this.financialSvc = financialSvc;
	}

	public PmAddressType getPmAddressType() {
		return pmAddressType;
	}

	public void setPmAddressType(PmAddressType pmAddressType) {
		this.pmAddressType = pmAddressType;
	}

	public List<PmFinancialSvc> getPmFinancialSvc() {
		return pmFinancialSvc;
	}

	public void setPmFinancialSvc(List<PmFinancialSvc> pmFinancialSvc) {
		this.pmFinancialSvc = pmFinancialSvc;
	}

	public List<PmFinancialSvc> getSelectedPmFinancialSvc() {
		return selectedPmFinancialSvc;
	}

	public void setSelectedPmFinancialSvc(
			List<PmFinancialSvc> selectedPmFinancialSvc) {
		this.selectedPmFinancialSvc = selectedPmFinancialSvc;
	}

	public PmFinancialAcc getFinancialAcc() {
		return financialAcc;
	}

	public void setFinancialAcc(PmFinancialAcc financialAcc) {
		this.financialAcc = financialAcc;
	}

	public List<PmFinancialAcc> getPmFinancialAcc() {
		return pmFinancialAcc;
	}

	public void setPmFinancialAcc(List<PmFinancialAcc> pmFinancialAcc) {
		this.pmFinancialAcc = pmFinancialAcc;
	}

	public List<PmFinancialAcc> getSelectedPmFinancialAcc() {
		return selectedPmFinancialAcc;
	}

	public void setSelectedPmFinancialAcc(
			List<PmFinancialAcc> selectedPmFinancialAcc) {
		this.selectedPmFinancialAcc = selectedPmFinancialAcc;
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

	public PmFinancialBranch getFinancialBranch() {
		return financialBranch;
	}

	public void setFinancialBranch(PmFinancialBranch financialBranch) {
		this.financialBranch = financialBranch;
	}

	public List<PmFinancialBranch> getPmFinancialBranch() {
		return pmFinancialBranch;
	}

	public void setPmFinancialBranch(List<PmFinancialBranch> pmFinancialBranch) {
		this.pmFinancialBranch = pmFinancialBranch;
	}

	/**
	 * @return the byFactoring
	 */
	public boolean isByFactoring() {
		return byFactoring;
	}

	/**
	 * @param byFactoring
	 *            the byFactoring to set
	 */
	public void setByFactoring(boolean byFactoring) {
		this.byFactoring = byFactoring;
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
	 * @return the addressType
	 */
	public List<PmAddressType> getAddressType() {
		return addressType;
	}

	/**
	 * @param addressType
	 *            the addressType to set
	 */
	public void setAddressType(List<PmAddressType> addressType) {
		this.addressType = addressType;
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
	 * @return the pmFinancialSvcUpdate
	 */
	public List<PmFinancialSvc> getPmFinancialSvcUpdate() {
		return pmFinancialSvcUpdate;
	}

	/**
	 * @param pmFinancialSvcUpdate
	 *            the pmFinancialSvcUpdate to set
	 */
	public void setPmFinancialSvcUpdate(
			List<PmFinancialSvc> pmFinancialSvcUpdate) {
		this.pmFinancialSvcUpdate = pmFinancialSvcUpdate;
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
	 * @return the selectedFinancialSvc
	 */
	public PmFinancialSvc getSelectedFinancialSvc() {
		return selectedFinancialSvc;
	}

	/**
	 * @param selectedFinancialSvc
	 *            the selectedFinancialSvc to set
	 */
	public void setSelectedFinancialSvc(PmFinancialSvc selectedFinancialSvc) {
		this.selectedFinancialSvc = selectedFinancialSvc;
	}

	/**
	 * @return the financialOrgSearchBean
	 */
	public FinancialOrgSearchBean getFinancialOrgSearchBean() {
		if (financialOrgSearchBean == null)
			financialOrgSearchBean = new FinancialOrgSearchBean();
		return financialOrgSearchBean;
	}

	/**
	 * @param financialOrgSearchBean
	 *            the financialOrgSearchBean to set
	 */
	public void setFinancialOrgSearchBean(
			FinancialOrgSearchBean financialOrgSearchBean) {
		this.financialOrgSearchBean = financialOrgSearchBean;
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
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType
	 *            the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @return the branchStateList
	 */
	public List<PmState> getBranchStateList() {
		return branchStateList;
	}

	/**
	 * @param branchStateList
	 *            the branchStateList to set
	 */
	public void setBranchStateList(List<PmState> branchStateList) {
		this.branchStateList = branchStateList;
	}

	/**
	 * @return the branchCountryList
	 */
	public List<PmCountry> getBranchCountryList() {
		return branchCountryList;
	}

	/**
	 * @param branchCountryList
	 *            the branchCountryList to set
	 */
	public void setBranchCountryList(List<PmCountry> branchCountryList) {
		this.branchCountryList = branchCountryList;
	}

	/**
	 * @return the branchDistrictList
	 */
	public List<PmDistrict> getBranchDistrictList() {
		return branchDistrictList;
	}

	/**
	 * @param branchDistrictList
	 *            the branchDistrictList to set
	 */
	public void setBranchDistrictList(List<PmDistrict> branchDistrictList) {
		this.branchDistrictList = branchDistrictList;
	}

	/**
	 * @return the branchCityList
	 */
	public List<PmCity> getBranchCityList() {
		return branchCityList;
	}

	/**
	 * @param branchCityList
	 *            the branchCityList to set
	 */
	public void setBranchCityList(List<PmCity> branchCityList) {
		this.branchCityList = branchCityList;
	}

	/**
	 * @return the branchDivisionList
	 */
	public List<PmDivision> getBranchDivisionList() {
		return branchDivisionList;
	}

	/**
	 * @param branchDivisionList
	 *            the branchDivisionList to set
	 */
	public void setBranchDivisionList(List<PmDivision> branchDivisionList) {
		this.branchDivisionList = branchDivisionList;
	}

	/**
	 * @return the selectedPmFinancialBranch
	 */
	public PmFinancialBranch getSelectedPmFinancialBranch() {
		return selectedPmFinancialBranch;
	}

	/**
	 * @param selectedPmFinancialBranch
	 *            the selectedPmFinancialBranch to set
	 */
	public void setSelectedPmFinancialBranch(
			PmFinancialBranch selectedPmFinancialBranch) {
		this.selectedPmFinancialBranch = selectedPmFinancialBranch;
	}

	/**
	 * @return the serviceTypes
	 */
	public List<String> getServiceTypes() {
		return serviceTypes;
	}

	/**
	 * @param serviceTypes
	 *            the serviceTypes to set
	 */
	public void setServiceTypes(List<String> serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	/**
	 * @return the branchAddressreq
	 */
	public boolean isBranchAddressreq() {
		return branchAddressreq;
	}

	/**
	 * @param branchAddressreq
	 *            the branchAddressreq to set
	 */
	public void setBranchAddressreq(boolean branchAddressreq) {
		this.branchAddressreq = branchAddressreq;
	}

	public boolean isDropDownValue() {
		return dropDownValue;
	}

	public void setDropDownValue(boolean dropDownValue) {
		this.dropDownValue = dropDownValue;
	}

	public Map<String, Object> getMapObject() {
		return mapObject;
	}

	public void setMapObject(Map<String, Object> mapObject) {
		this.mapObject = mapObject;
	}

	/**
	 * @return the enableUpdateStatus
	 */
	public boolean isEnableUpdateStatus() {
		return enableUpdateStatus;
	}

	/**
	 * @param enableUpdateStatus the enableUpdateStatus to set
	 */
	public void setEnableUpdateStatus(boolean enableUpdateStatus) {
		this.enableUpdateStatus = enableUpdateStatus;
	}

}
