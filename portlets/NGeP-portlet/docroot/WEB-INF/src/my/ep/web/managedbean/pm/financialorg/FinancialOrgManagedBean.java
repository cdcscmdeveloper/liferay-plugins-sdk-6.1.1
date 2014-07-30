package my.ep.web.managedbean.pm.financialorg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialBranch;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmState;
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
	private String orgName;
	private String regNumber;
	private String regTypeId;
	private String status;
	private String serviceType;
	private int financialActiveIndex;

	// Financial Svc
	private PmFinancialSvc financialSvc;
	private List<PmFinancialSvc> pmFinancialSvc;
	private List<PmFinancialSvc> selectedPmFinancialSvc;
	private boolean byFactoring = true;
	private int activeIndex = 0;
	// Financial Org
	private List<PmFinancialOrg> pmFinancialOrg;
	private PmFinancialOrg financialOrg = new PmFinancialOrg();

	// Financial Acc
	private PmFinancialAcc financialAcc;
	private List<PmFinancialAcc> pmFinancialAcc;
	private List<PmFinancialAcc> selectedPmFinancialAcc;

	// Financial Branch
	private PmFinancialBranch financialBranch;
	private List<PmFinancialBranch> pmFinancialBranch;
	private List<PmFinancialBranch> selectedPmFinancialBranch;
	// Financial Org address
	private PmAddressType pmAddressType;;
	private List<PmAddressType> addressType;
	private PmAddress pmAddress;
	private PmContact contactPerson;
	private List<PmContact> pmContact;
	private List<PmState> states;
	private List<PmCountry> countries;
	private List<PmDistrict> districts;
	private List<PmCity> citys;
	
	// Financial Branch Address
	private PmAddressType branchAddressType;;
	private List<PmAddressType> branchAddressTypeList;
	private PmAddress branchAddress;
	private PmContact branchContactPerson;
	private List<PmContact> branchContact;	

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
	 * @return the branchAddressTypeList
	 */
	public List<PmAddressType> getBranchAddressTypeList() {
		return branchAddressTypeList;
	}

	/**
	 * @param branchAddressTypeList
	 *            the branchAddressTypeList to set
	 */
	public void setBranchAddressTypeList(
			List<PmAddressType> branchAddressTypeList) {
		this.branchAddressTypeList = branchAddressTypeList;
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

	public FinancialOrgManagedBean() {
		
		pmAddress = new PmAddress();
		pmAddressType = new PmAddressType();
		contactPerson = new PmContact();
		pmAddress.setPmCity(new PmCity());
		pmAddress.setPmDistrict(new PmDistrict());
		pmAddress.setPmCountry(new PmCountry());
		pmAddress.setPmState(new PmState());
		pmAddressType.setPmAddress(pmAddress);
		financialOrg.setPmCountry(new PmCountry());
		// Branch Address
		branchAddress = new PmAddress();
		branchAddressType = new PmAddressType();
		branchContactPerson = new PmContact();
		branchAddress.setPmCity(new PmCity());
		branchAddress.setPmDistrict(new PmDistrict());
		branchAddress.setPmCountry(new PmCountry());
		branchAddress.setPmState(new PmState());
		branchAddressType.setPmAddress(branchAddress);
		
		

	}

	public PmFinancialSvc getFinancialSvc() {
		return financialSvc;
	}

	public void setFinancialSvc(PmFinancialSvc financialSvc) {
		this.financialSvc = financialSvc;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getRegNumber() {
		return regNumber;
	}

	public void setRegNumber(String regNumber) {
		this.regNumber = regNumber;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public int getFinancialActiveIndex() {
		return financialActiveIndex;
	}

	public void setFinancialActiveIndex(int financialActiveIndex) {
		this.financialActiveIndex = financialActiveIndex;
	}

	public PmAddressType getPmAddressType() {
		return pmAddressType;
	}

	public void setPmAddressType(PmAddressType pmAddressType) {
		this.pmAddressType = pmAddressType;
	}

	public List<PmState> getStates() {
		return states;
	}

	public void setStates(List<PmState> states) {
		this.states = states;
	}

	public List<PmCountry> getCountries() {
		return countries;
	}

	public void setCountries(List<PmCountry> countries) {
		this.countries = countries;
	}

	public List<PmDistrict> getDistricts() {
		return districts;
	}

	public void setDistricts(List<PmDistrict> districts) {
		this.districts = districts;
	}

	public List<PmCity> getCitys() {
		return citys;
	}

	public void setCitys(List<PmCity> citys) {
		this.citys = citys;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public List<PmFinancialBranch> getSelectedPmFinancialBranch() {
		return selectedPmFinancialBranch;
	}

	public void setSelectedPmFinancialBranch(
			List<PmFinancialBranch> selectedPmFinancialBranch) {
		this.selectedPmFinancialBranch = selectedPmFinancialBranch;
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
	 * @return the regTypeId
	 */
	public String getRegTypeId() {
		return regTypeId;
	}

	/**
	 * @param regTypeId
	 *            the regTypeId to set
	 */
	public void setRegTypeId(String regTypeId) {
		this.regTypeId = regTypeId;
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

}
