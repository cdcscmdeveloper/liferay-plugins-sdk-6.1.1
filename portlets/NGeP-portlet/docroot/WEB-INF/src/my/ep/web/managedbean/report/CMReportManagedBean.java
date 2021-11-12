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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sm.SmSupplier;
// #29592 - start
import my.ep.web.bean.pm.ProfileManagementSearchBean;
// #29592 - end
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.managedbean.BaseManagedBean;


@ManagedBean(name = "cmReportManagedBean")
@SessionScoped
public class CMReportManagedBean extends ReportBaseManagedBean {
	
	private CmUnspsc selectedCmUNSPSC;
	private String itemType;
	private PmOrgProfile selectedOrgProfile;
	private String regType;
	private String mofNo;
	private String busRegNo;
	private String icNo;
	private String epNo;
	private String group;
	private Date dateFrom;
	private Date dateTo;
	private String requestNo;
	
	private CmItem selectedCmItem;
	
	// #29592 - start
	private ProfileManagementSearchBean pmSearchBean;
	private PmOrgProfile selectedMinistryOrgProfile;
	private PmOrgProfile selectedKumpulanPTJOrgProfile;
	private PmOrgProfile selectedPTJOrgProfile;
	private String docNoSearchInput;
	// #29592 - end	
	
	//#29640 - Start
	private String requestType;
	private String docStatus;
	private Date minDate;
	//#29640 - End

	/**
	 * @return the requestNo
	 */
	public String getRequestNo() {
		return requestNo;
	}

	/**
	 * @param requestNo the requestNo to set
	 */
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateFrom;
	}

	/**
	 * @param dateFrom the dateFrom to set
	 */
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	/**
	 * @return the dateTo
	 */
	public Date getDateTo() {
		return dateTo;
	}

	/**
	 * @param dateTo the dateTo to set
	 */
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
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
	 * @return the mofNo
	 */
	public String getMofNo() {
		return mofNo;
	}

	/**
	 * @param mofNo the mofNo to set
	 */
	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

	/**
	 * @return the busRegNo
	 */
	public String getBusRegNo() {
		return busRegNo;
	}

	/**
	 * @param busRegNo the busRegNo to set
	 */
	public void setBusRegNo(String busRegNo) {
		this.busRegNo = busRegNo;
	}

	/**
	 * @return the icNo
	 */
	public String getIcNo() {
		return icNo;
	}

	/**
	 * @param icNo the icNo to set
	 */
	public void setIcNo(String icNo) {
		this.icNo = icNo;
	}

	/**
	 * @return the regType
	 */
	public String getRegType() {
		return regType;
	}

	/**
	 * @param regType the regType to set
	 */
	public void setRegType(String regType) {
		this.regType = regType;
	}

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
	 * @return the itemType
	 */
	public String getItemType() {
		return itemType;
	}

	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	/**
	 * @return the selectedCmUNSPSC
	 */
	public CmUnspsc getSelectedCmUNSPSC() {
		if(selectedCmUNSPSC==null){
			selectedCmUNSPSC = new CmUnspsc();
		}
		return selectedCmUNSPSC;
	}

	/**
	 * @param selectedCmUNSPSC the selectedCmUNSPSC to set
	 */
	public void setSelectedCmUNSPSC(CmUnspsc selectedCmUNSPSC) {
		this.selectedCmUNSPSC = selectedCmUNSPSC;
	}

	/**
	 * @return the selectedCmItem
	 */
	public CmItem getSelectedCmItem() {
		if(selectedCmItem==null){
			selectedCmItem = new CmItem();
		}
		return selectedCmItem;
	}

	/**
	 * @param selectedCmItem the selectedCmItem to set
	 */
	public void setSelectedCmItem(CmItem selectedCmItem) {
		this.selectedCmItem = selectedCmItem;
	}

	// #29592 - start
	public String getDocNoSearchInput() {
		return docNoSearchInput;
	}

	public void setDocNoSearchInput(String docNoSearchInput) {
		this.docNoSearchInput = docNoSearchInput;
	}

	public PmOrgProfile getSelectedMinistryOrgProfile() {
		return selectedMinistryOrgProfile;
	}

	public void setSelectedMinistryOrgProfile(PmOrgProfile selectedMinistryOrgProfile) {
		this.selectedMinistryOrgProfile = selectedMinistryOrgProfile;
	}

	public PmOrgProfile getSelectedKumpulanPTJOrgProfile() {
		return selectedKumpulanPTJOrgProfile;
	}

	public void setSelectedKumpulanPTJOrgProfile(PmOrgProfile selectedKumpulanPTJOrgProfile) {
		this.selectedKumpulanPTJOrgProfile = selectedKumpulanPTJOrgProfile;
	}

	public PmOrgProfile getSelectedPTJOrgProfile() {
		return selectedPTJOrgProfile;
	}

	public void setSelectedPTJOrgProfile(PmOrgProfile selectedPTJOrgProfile) {
		this.selectedPTJOrgProfile = selectedPTJOrgProfile;
	}

	public ProfileManagementSearchBean getPmSearchBean() {
		if (pmSearchBean == null) {
			pmSearchBean = new ProfileManagementSearchBean();
		}
		return pmSearchBean;
	}

	public void setPmSearchBean(ProfileManagementSearchBean pmSearchBean) {
		this.pmSearchBean = pmSearchBean;
	}
	// #29592 - end
	
	//#29640 - Start
	/**
	 * @return the requestType
	 */
	public String getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	/**
	 * @return the docStatus
	 */
	public String getDocStatus() {
		return docStatus;
	}

	/**
	 * @param docStatus the docStatus to set
	 */
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	
	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}
	//#29640 - End
}

