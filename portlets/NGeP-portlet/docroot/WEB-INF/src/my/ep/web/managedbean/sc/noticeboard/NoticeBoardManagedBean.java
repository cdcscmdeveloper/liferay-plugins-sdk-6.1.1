package my.ep.web.managedbean.sc.noticeboard;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.component.tabview.Tab;

/**
 * @author sri
 * NoticeBoard Managed Bean
 */
@ManagedBean(name = "noticeBoardManagedBean")
@SessionScoped
public class NoticeBoardManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	
  // search filter parameters
	private Long minstryId;
	private Long jabatanId;
	private Long ptjId;	
	private Long categoryId;
	private Long stateId;
	private Long districtId;	
	private Long categoryL1Id; 
	private Long categoryL2Id; 
	private Long categoryL3Id; 

	
	
	private List<ScQt> scQtSearchlist; // notice board search listing
	private ScQt scQt;
	private ScQt selectedScQt ;
	private PmOrgProfile ministryOrgProfile;
	private PmOrgProfile deportmentOrgProfile;
	private PmOrgProfile pmOrgProfile;
	private PmState pmState;
	private PmDistrict pmDistrict;
	private PmCategoryL1 pmCategoryL1;
	private PmCategoryL2 pmCategoryL2;
	private PmCategoryL3 pmCategoryL3;
	private PmStatus pmStatus; 
	
	
	// Qtn/Tender Details Listings
	private List<ScQtCategory> categoryList; // listing for Category
	private List<ScQtLocCriteria> locationList; // listing for Location
	private List<ScQtZoneCriteria> zoneList; // listing for Zone
	private ScQtCriteria scQtCriteria;
	private String multipleLocality;
	private String allStates;
	private boolean locVisible ;
	
    // Drop down lists for notice board
	private List<PmOrgProfile> ministryList;
	private List<PmOrgProfile> departmentList;
	private List<PmOrgProfile> divisionList;
	private List<PmCategoryL1> categoryL1s;
	private List<PmCategoryL2> categoryL2s;
	private List<PmCategoryL3> categoryL3s;
	private List<PmState> states;
	private List<PmDistrict> districts;
	
	
     // dynamic tabs
	private List<Tab> tabList ;	
	private Integer activeTabIndex = 0;
	private Integer tabIndex = 0;
	
	public ISearchBean<ScQt> searchBean ;
	
	
	

  public NoticeBoardManagedBean(){
	 // scQt = new ScQt();
	  //selectedScQt = new ScQt();
	 
  }
	

	/**
	 * @return the selectedScQt
	 */
	public ScQt getSelectedScQt() {
		return selectedScQt;
	}

	/**
	 * @param selectedScQt the selectedScQt to set
	 */
	public void setSelectedScQt(ScQt selectedScQt) {
		this.selectedScQt = selectedScQt;
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		
		this.scQt = scQt;
	}

	/**
	 * @return the scQtSearchlist
	 */
	public List<ScQt> getScQtSearchlist() {
		return scQtSearchlist;
	}

	/**
	 * @param scQtSearchlist the scQtSearchlist to set
	 */
	public void setScQtSearchlist(List<ScQt> scQtSearchlist) {
		this.scQtSearchlist = scQtSearchlist;
	}

	/**
	 * @return the minstryId
	 */
	public Long getMinstryId() {
		return minstryId;
	}

	/**
	 * @param minstryId the minstryId to set
	 */
	public void setMinstryId(Long minstryId) {
		this.minstryId = minstryId;
	}

	/**
	 * @return the jabatanId
	 */
	public Long getJabatanId() {
		return jabatanId;
	}

	/**
	 * @param jabatanId the jabatanId to set
	 */
	public void setJabatanId(Long jabatanId) {
		this.jabatanId = jabatanId;
	}

	/**
	 * @return the ptjId
	 */
	public Long getPtjId() {
		return ptjId;
	}

	/**
	 * @param ptjId the ptjId to set
	 */
	public void setPtjId(Long ptjId) {
		this.ptjId = ptjId;
	}

	/**
	 * @return the categoryId
	 */
	public Long getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the stateId
	 */
	public Long getStateId() {
		return stateId;
	}

	/**
	 * @param stateId the stateId to set
	 */
	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	/**
	 * @return the districtId
	 */
	public Long getDistrictId() {
		return districtId;
	}

	/**
	 * @param districtId the districtId to set
	 */
	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	/**
	 * @return the categoryList
	 */
	public List<ScQtCategory> getCategoryList() {
		return categoryList;
	}

	/**
	 * @param categoryList the categoryList to set
	 */
	public void setCategoryList(List<ScQtCategory> categoryList) {
		this.categoryList = categoryList;
	}

	/**
	 * @return the locationList
	 */
	public List<ScQtLocCriteria> getLocationList() {
		return locationList;
	}

	/**
	 * @param locationList the locationList to set
	 */
	public void setLocationList(List<ScQtLocCriteria> locationList) {
		this.locationList = locationList;
	}

	/**
	 * @return the zoneList
	 */
	public List<ScQtZoneCriteria> getZoneList() {
		return zoneList;
	}

	/**
	 * @param zoneList the zoneList to set
	 */
	public void setZoneList(List<ScQtZoneCriteria> zoneList) {
		this.zoneList = zoneList;
	}

	/**
	 * @return the scQtCriteria
	 */
	public ScQtCriteria getScQtCriteria() {
		return scQtCriteria;
	}

	/**
	 * @param scQtCriteria the scQtCriteria to set
	 */
	public void setScQtCriteria(ScQtCriteria scQtCriteria) {
		this.scQtCriteria = scQtCriteria;
	}

	/**
	 * @return the multipleLocality
	 */
	public String getMultipleLocality() {
		return multipleLocality;
	}

	/**
	 * @param multipleLocality the multipleLocality to set
	 */
	public void setMultipleLocality(String multipleLocality) {
		this.multipleLocality = multipleLocality;
	}

	/**
	 * @return the allStates
	 */
	public String getAllStates() {
		return allStates;
	}

	/**
	 * @param allStates the allStates to set
	 */
	public void setAllStates(String allStates) {
		this.allStates = allStates;
	}

	/**
	 * @return the locVisible
	 */
	public boolean isLocVisible() {
		return locVisible;
	}

	/**
	 * @param locVisible the locVisible to set
	 */
	public void setLocVisible(boolean locVisible) {
		this.locVisible = locVisible;
	}

	/**
	 * @return the ministryList
	 */
	public List<PmOrgProfile> getMinistryList() {
		return ministryList;
	}


	/**
	 * @param ministryList the ministryList to set
	 */
	public void setMinistryList(List<PmOrgProfile> ministryList) {
		this.ministryList = ministryList;
	}

	/**
	 * @return the departmentList
	 */
	public List<PmOrgProfile> getDepartmentList() {
		return departmentList;
	}

	/**
	 * @param departmentList the departmentList to set
	 */
	public void setDepartmentList(List<PmOrgProfile> departmentList) {
		this.departmentList = departmentList;
	}

	/**
	 * @return the divisionList
	 */
	public List<PmOrgProfile> getDivisionList() {
		return divisionList;
	}

	/**
	 * @param divisionList the divisionList to set
	 */
	public void setDivisionList(List<PmOrgProfile> divisionList) {
		this.divisionList = divisionList;
	}

	/**
	 * @return the ministryOrgProfile
	 */
	public PmOrgProfile getMinistryOrgProfile() {
		return ministryOrgProfile;
	}

	/**
	 * @param ministryOrgProfile the ministryOrgProfile to set
	 */
	public void setMinistryOrgProfile(PmOrgProfile ministryOrgProfile) {
		this.ministryOrgProfile = ministryOrgProfile;
	}

	/**
	 * @return the tabList
	 */
	public List<Tab> getTabList() {
		return tabList;
	}

	/**
	 * @param tabList the tabList to set
	 */
	public void setTabList(List<Tab> tabList) {
		this.tabList = tabList;
	}

	/**
	 * @return the deportmentOrgProfile
	 */
	public PmOrgProfile getDeportmentOrgProfile() {
		return deportmentOrgProfile;
	}

	/**
	 * @param deportmentOrgProfile the deportmentOrgProfile to set
	 */
	public void setDeportmentOrgProfile(PmOrgProfile deportmentOrgProfile) {
		this.deportmentOrgProfile = deportmentOrgProfile;
	}

	/**
	 * @return the pmOrgProfile
	 */
	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}

	/**
	 * @param pmOrgProfile the pmOrgProfile to set
	 */
	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
	}

	/**
	 * @return the activeTabIndex
	 */
	public Integer getActiveTabIndex() {
		return activeTabIndex;
	}

	/**
	 * @param activeTabIndex the activeTabIndex to set
	 */
	public void setActiveTabIndex(Integer activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}

	/**
	 * @return the tabIndex
	 */
	public Integer getTabIndex() {
		return tabIndex;
	}

	/**
	 * @param tabIndex the tabIndex to set
	 */
	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}


	/**
	 * @return the categoryL1s
	 */
	public List<PmCategoryL1> getCategoryL1s() {
		return categoryL1s;
	}


	/**
	 * @param categoryL1s the categoryL1s to set
	 */
	public void setCategoryL1s(List<PmCategoryL1> categoryL1s) {
		this.categoryL1s = categoryL1s;
	}


	/**
	 * @return the categoryL2s
	 */
	public List<PmCategoryL2> getCategoryL2s() {
		return categoryL2s;
	}


	/**
	 * @param categoryL2s the categoryL2s to set
	 */
	public void setCategoryL2s(List<PmCategoryL2> categoryL2s) {
		this.categoryL2s = categoryL2s;
	}


	/**
	 * @return the categoryL3s
	 */
	public List<PmCategoryL3> getCategoryL3s() {
		return categoryL3s;
	}


	/**
	 * @param categoryL3s the categoryL3s to set
	 */
	public void setCategoryL3s(List<PmCategoryL3> categoryL3s) {
		this.categoryL3s = categoryL3s;
	}


	/**
	 * @return the states
	 */
	public List<PmState> getStates() {
		return states;
	}


	/**
	 * @param states the states to set
	 */
	public void setStates(List<PmState> states) {
		this.states = states;
	}


	/**
	 * @return the districts
	 */
	public List<PmDistrict> getDistricts() {
		return districts;
	}


	/**
	 * @param districts the districts to set
	 */
	public void setDistricts(List<PmDistrict> districts) {
		this.districts = districts;
	}


	/**
	 * @return the categoryL1Id
	 */
	public Long getCategoryL1Id() {
		return categoryL1Id;
	}


	/**
	 * @param categoryL1Id the categoryL1Id to set
	 */
	public void setCategoryL1Id(Long categoryL1Id) {
		this.categoryL1Id = categoryL1Id;
	}


	/**
	 * @return the categoryL2Id
	 */
	public Long getCategoryL2Id() {
		return categoryL2Id;
	}


	/**
	 * @param categoryL2Id the categoryL2Id to set
	 */
	public void setCategoryL2Id(Long categoryL2Id) {
		this.categoryL2Id = categoryL2Id;
	}


	/**
	 * @return the categoryL3Id
	 */
	public Long getCategoryL3Id() {
		return categoryL3Id;
	}


	/**
	 * @param categoryL3Id the categoryL3Id to set
	 */
	public void setCategoryL3Id(Long categoryL3Id) {
		this.categoryL3Id = categoryL3Id;
	}


	/**
	 * @return the pmState
	 */
	public PmState getPmState() {
		return pmState;
	}


	/**
	 * @param pmState the pmState to set
	 */
	public void setPmState(PmState pmState) {
		this.pmState = pmState;
	}


	/**
	 * @return the pmDistrict
	 */
	public PmDistrict getPmDistrict() {
		return pmDistrict;
	}


	/**
	 * @param pmDistrict the pmDistrict to set
	 */
	public void setPmDistrict(PmDistrict pmDistrict) {
		this.pmDistrict = pmDistrict;
	}


	/**
	 * @return the pmCategoryL1
	 */
	public PmCategoryL1 getPmCategoryL1() {
		return pmCategoryL1;
	}


	/**
	 * @param pmCategoryL1 the pmCategoryL1 to set
	 */
	public void setPmCategoryL1(PmCategoryL1 pmCategoryL1) {
		this.pmCategoryL1 = pmCategoryL1;
	}


	/**
	 * @return the pmCategoryL2
	 */
	public PmCategoryL2 getPmCategoryL2() {
		return pmCategoryL2;
	}


	/**
	 * @param pmCategoryL2 the pmCategoryL2 to set
	 */
	public void setPmCategoryL2(PmCategoryL2 pmCategoryL2) {
		this.pmCategoryL2 = pmCategoryL2;
	}


	/**
	 * @return the pmCategoryL3
	 */
	public PmCategoryL3 getPmCategoryL3() {
		return pmCategoryL3;
	}


	/**
	 * @param pmCategoryL3 the pmCategoryL3 to set
	 */
	public void setPmCategoryL3(PmCategoryL3 pmCategoryL3) {
		this.pmCategoryL3 = pmCategoryL3;
	}


	/**
	 * @return the pmStatus
	 */
	public PmStatus getPmStatus() {
		return pmStatus;
	}


	/**
	 * @param pmStatus the pmStatus to set
	 */
	public void setPmStatus(PmStatus pmStatus) {
		this.pmStatus = pmStatus;
	}

	/**
	 * @return the searchBean
	 */
	public ISearchBean<ScQt> getSearchBean() {
		return searchBean;
	}

	/**
	 * @param searchBean the searchBean to set
	 */
	public void setSearchBean(ISearchBean<ScQt> searchBean) {
		this.searchBean = searchBean;
	}


	
	
	
}
