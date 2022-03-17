/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.web.bean.pm.ProfileManagementSearchBean; //#28833 -shafie

@ManagedBean(name = "pmReportsManagedBean")
@SessionScoped
public class PmReportsManagedBean extends ReportBaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private String reportName;
	private String zoneName;
	private List<PmZone> zoneList;
	private Long status;
	//#28833 -shafie -start
	private boolean govtSeller; 
	private ProfileManagementSearchBean pmSearchBean;
	private PmOrgProfile selectedPTJ;
	private PmOrgProfile selectedKumpulanPTJ;
	private PmOrgProfile selectedMinistry;
	//#28833 -shafie -end
	private String contractNo;
	private String contractPhyscialNo;
	private String loaNo;
	private String loaPhyscialNo;
	private CmUnspsc selectedUnspscItem;
	private Date fromDate;
	private Date toDate;
	private String monthPicked; //shafie #29486
	private PmOrgProfile selectedOrgProfile;
	private String bizRegNo;
	private String epNo;
	private String serviceType;
	private boolean isNotFirstRun;
	private int searchOrgCategory;
	private boolean isDisabledOrgFields;
	private PmParameter selectedOrgType;
	private PmOrgProfile currentActiveOrg;
	private PmRoleDesc selectedRoleDesc;
	private List<PmRoleDesc> roleDescResults;
	private Long selectedRoleOptionSearch;
	private String userRoleNameSearch;
	private String selectedRoleCode;
	private PmUser selectedPmUser;
	private String applicantName;
	private String selectedAccessType;
	private Long selectedApplicationStatus;
	
	public PmReportsManagedBean(){}

	/**
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * @return the zoneList
	 */
	public List<PmZone> getZoneList() {
		return zoneList;
	}

	/**
	 * @param zoneList the zoneList to set
	 */
	public void setZoneList(List<PmZone> zoneList) {
		this.zoneList = zoneList;
	}

	/**
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}
	
	//shafie - #28833 start ---------------
	
	/**
	 * @return the govtSeller
	 */
	public boolean isGovtSeller() {
		return govtSeller;
	}

	/**
	 * @param govtSeller the govtSeller to set
	 */
	public void setGovtSeller(boolean govtSeller) {
		this.govtSeller = govtSeller;
	}
	
	//shafie - #28833 end -----------------

	/**
	 * @return the contractNo
	 */
	public String getContractNo() {
		return contractNo;
	}

	/**
	 * @param contractNo the contractNo to set
	 */
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	/**
	 * @return the contractPhyscialNo
	 */
	public String getContractPhyscialNo() {
		return contractPhyscialNo;
	}

	/**
	 * @param contractPhyscialNo the contractPhyscialNo to set
	 */
	public void setContractPhyscialNo(String contractPhyscialNo) {
		this.contractPhyscialNo = contractPhyscialNo;
	}

	/**
	 * @return the loaNo
	 */
	public String getLoaNo() {
		return loaNo;
	}

	/**
	 * @param loaNo the loaNo to set
	 */
	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}

	/**
	 * @return the loaPhyscialNo
	 */
	public String getLoaPhyscialNo() {
		return loaPhyscialNo;
	}

	/**
	 * @param loaPhyscialNo the loaPhyscialNo to set
	 */
	public void setLoaPhyscialNo(String loaPhyscialNo) {
		this.loaPhyscialNo = loaPhyscialNo;
	}

	/**
	 * @return the selectedUnspscItem
	 */
	public CmUnspsc getSelectedUnspscItem() {
		return selectedUnspscItem;
	}

	/**
	 * @param selectedUnspscItem the selectedUnspscItem to set
	 */
	public void setSelectedUnspscItem(CmUnspsc selectedUnspscItem) {
		this.selectedUnspscItem = selectedUnspscItem;
	}

	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	//shafie #29486 start
	
	/**
	 * @return the monthPicked
	 */
	public String getmonthPicked() {
		return monthPicked;
	}

	/**
	 * @param monthPicked the monthPicked to set
	 */
	public void setmonthPicked(String monthPicked) {
		this.monthPicked = monthPicked;
	}
	
	//shafie #29486 end

	/**
	 * @return the selectedOrgProfile
	 */
	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	/**
	 * @param selectedOrgProfile the selectedOrgProfile to set
	 */
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	/**
	 * @return the bizRegNo
	 */
	public String getBizRegNo() {
		return bizRegNo;
	}

	/**
	 * @param bizRegNo the bizRegNo to set
	 */
	public void setBizRegNo(String bizRegNo) {
		this.bizRegNo = bizRegNo;
	}

	/**
	 * @return the epNo
	 */
	public String getEpNo() {
		return epNo;
	}

	/**
	 * @param epNo the epNo to set
	 */
	public void setEpNo(String epNo) {
		this.epNo = epNo;
	}

	/**
	 * @return the applicantName
	 */
	public String getApplicantName() {
		return applicantName;
	}

	/**
	 * @param applicantName the applicantName to set
	 */
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	/**
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @return the isNotFirstRun
	 */
	public boolean isNotFirstRun() {
		return isNotFirstRun;
	}

	/**
	 * @param isNotFirstRun the isNotFirstRun to set
	 */
	public void setNotFirstRun(boolean isNotFirstRun) {
		this.isNotFirstRun = isNotFirstRun;
	}

	/**
	 * @return the searchOrgCategory
	 */
	public int getSearchOrgCategory() {
		return searchOrgCategory;
	}

	/**
	 * @param searchOrgCategory the searchOrgCategory to set
	 */
	public void setSearchOrgCategory(int searchOrgCategory) {
		this.searchOrgCategory = searchOrgCategory;
	}

	/**
	 * @return the isDisabledOrgFields
	 */
	public boolean isDisabledOrgFields() {
		return isDisabledOrgFields;
	}

	/**
	 * @param isDisabledOrgFields the isDisabledOrgFields to set
	 */
	public void setDisabledOrgFields(boolean isDisabledOrgFields) {
		this.isDisabledOrgFields = isDisabledOrgFields;
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
	 * @param selectedOrgType the selectedOrgType to set
	 */
	public void setSelectedOrgType(PmParameter selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	/**
	 * @return the currentActiveOrg
	 */
	public PmOrgProfile getCurrentActiveOrg() {
		return currentActiveOrg;
	}

	/**
	 * @param currentActiveOrg the currentActiveOrg to set
	 */
	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
	}

	/**
	 * @return the selectedRoleDesc
	 */
	public PmRoleDesc getSelectedRoleDesc() {
		return selectedRoleDesc;
	}

	/**
	 * @param selectedRoleDesc the selectedRoleDesc to set
	 */
	public void setSelectedRoleDesc(PmRoleDesc selectedRoleDesc) {
		this.selectedRoleDesc = selectedRoleDesc;
	}

	/**
	 * @return the roleDescResults
	 */
	public List<PmRoleDesc> getRoleDescResults() {
		if(roleDescResults == null){
			roleDescResults = new ArrayList<PmRoleDesc>();
		}
		return roleDescResults;
	}

	/**
	 * @param roleDescResults the roleDescResults to set
	 */
	public void setRoleDescResults(List<PmRoleDesc> roleDescResults) {
		this.roleDescResults = roleDescResults;
	}

	/**
	 * @return the selectedRoleOptionSearch
	 */
	public Long getSelectedRoleOptionSearch() {
		return selectedRoleOptionSearch;
	}

	/**
	 * @param selectedRoleOptionSearch the selectedRoleOptionSearch to set
	 */
	public void setSelectedRoleOptionSearch(Long selectedRoleOptionSearch) {
		this.selectedRoleOptionSearch = selectedRoleOptionSearch;
	}

	/**
	 * @return the userRoleNameSearch
	 */
	public String getUserRoleNameSearch() {
		return userRoleNameSearch;
	}

	/**
	 * @param userRoleNameSearch the userRoleNameSearch to set
	 */
	public void setUserRoleNameSearch(String userRoleNameSearch) {
		this.userRoleNameSearch = userRoleNameSearch;
	}

	/**
	 * @return the selectedRoleCode
	 */
	public String getSelectedRoleCode() {
		return selectedRoleCode;
	}

	/**
	 * @param selectedRoleCode the selectedRoleCode to set
	 */
	public void setSelectedRoleCode(String selectedRoleCode) {
		this.selectedRoleCode = selectedRoleCode;
	}

	/**
	 * @return the selectedPmUser
	 */
	public PmUser getSelectedPmUser() {
		return selectedPmUser;
	}

	/**
	 * @param selectedPmUser the selectedPmUser to set
	 */
	public void setSelectedPmUser(PmUser selectedPmUser) {
		this.selectedPmUser = selectedPmUser;
	}

	/**
	 * @return the selectedAccessType
	 */
	public String getSelectedAccessType() {
		return selectedAccessType;
	}

	/**
	 * @param selectedAccessType the selectedAccessType to set
	 */
	public void setSelectedAccessType(String selectedAccessType) {
		this.selectedAccessType = selectedAccessType;
	}

	/**
	 * @return the selectedApplicationStatus
	 */
	public Long getSelectedApplicationStatus() {
		return selectedApplicationStatus;
	}

	/**
	 * @param selectedApplicationStatus the selectedApplicationStatus to set
	 */
	public void setSelectedApplicationStatus(Long selectedApplicationStatus) {
		this.selectedApplicationStatus = selectedApplicationStatus;
	}
	
	//shafie - #28833 -start
	/**
	 * @return the selectedPTJ
	 */
	public PmOrgProfile getSelectedPTJ() {
		return selectedPTJ;
	}

	/**
	 * @param selectedPTJ the selectedPTJ to set
	 */
	public void setSelectedPTJ(PmOrgProfile selectedPTJ) {
		this.selectedPTJ = selectedPTJ;
	}

	/**
	 * @return the selectedKumpulanPTJ
	 */
	public PmOrgProfile getSelectedKumpulanPTJ() {
		return selectedKumpulanPTJ;
	}

	/**
	 * @param selectedKumpulanPTJ the selectedKumpulanPTJ to set
	 */
	public void setSelectedKumpulanPTJ(PmOrgProfile selectedKumpulanPTJ) {
		this.selectedKumpulanPTJ = selectedKumpulanPTJ;
	}

	/**
	 * @return the selectedMinistry
	 */
	public PmOrgProfile getSelectedMinistry() {
		return selectedMinistry;
	}

	/**
	 * @param selectedMinistry the selectedMinistry to set
	 */
	public void setSelectedMinistry(PmOrgProfile selectedMinistry) {
		this.selectedMinistry = selectedMinistry;
	}		
	
	/**
	 * @return the pmSearchBean
	 */
	public ProfileManagementSearchBean getPmSearchBean() {
		if(pmSearchBean==null)
			pmSearchBean = new ProfileManagementSearchBean();
		return pmSearchBean;
	}

	/**
	 * @param pmSearchBean the pmSearchBean to set
	 */
	public void setPmSearchBean(ProfileManagementSearchBean pmSearchBean) {
		this.pmSearchBean = pmSearchBean;
	}
	//shafie #28833 end
	
}
