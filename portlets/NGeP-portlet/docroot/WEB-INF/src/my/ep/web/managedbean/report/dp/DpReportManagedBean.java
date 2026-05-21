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
package my.ep.web.managedbean.report.dp;

import java.util.Date;
import java.util.HashMap;//#29558
import java.util.List;//#29558
import java.util.Map;//#29558

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.managedbean.report.ReportBaseManagedBean;

/**
 * The Class DpReportManagedBean.
 * 
 * @author:AnJie
 * @since 03.05.2017
 */

public abstract class DpReportManagedBean extends ReportBaseManagedBean {
	private static final long serialVersionUID = 1L;

	private Date dateFrom;
	private Date dateTo;
	private Long status;
	private String docNo;
	private String groupBy;
	private PmOrgProfile selectedOrgProfile;
	
	/** The sc pi dtls prc rpt group. */
	private String scPIDtlsPrcRptGroup;
	
	private boolean isPtjEnable = false;
	private boolean isRequisitionerEnable = false;
	
	private String scRequisitioner;
	private String scPtj;
	
	private List<String> scStatusManyMenu;//#29558
	private List<String> scStatusManyMenuTemp;//#29558
	private Map<String, String> hash_scStatusManyMenuId = new HashMap<String, String>();//#29558
	
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

	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * @param docNo the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * @return the groupBy
	 */
	public String getGroupBy() {
		return groupBy;
	}

	/**
	 * @param groupBy the groupBy to set
	 */
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	/**
	 * @return the scPIDtlsPrcRptGroup
	 */
	public String getScPIDtlsPrcRptGroup() {
		return scPIDtlsPrcRptGroup;
	}

	/**
	 * @param scPIDtlsPrcRptGroup the scPIDtlsPrcRptGroup to set
	 */
	public void setScPIDtlsPrcRptGroup(String scPIDtlsPrcRptGroup) {
		this.scPIDtlsPrcRptGroup = scPIDtlsPrcRptGroup;
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
	 * @return the isPtjEnable
	 */
	public boolean isPtjEnable() {
		return isPtjEnable;
	}

	/**
	 * @param isPtjEnable the isPtjEnable to set
	 */
	public void setPtjEnable(boolean isPtjEnable) {
		this.isPtjEnable = isPtjEnable;
	}

	/**
	 * @return the isRequisitionerEnable
	 */
	public boolean isRequisitionerEnable() {
		return isRequisitionerEnable;
	}

	/**
	 * @param isRequisitionerEnable the isRequisitionerEnable to set
	 */
	public void setRequisitionerEnable(boolean isRequisitionerEnable) {
		this.isRequisitionerEnable = isRequisitionerEnable;
	}

	/**
	 * @return the scRequisitioner
	 */
	public String getScRequisitioner() {
		return scRequisitioner;
	}

	/**
	 * @param scRequisitioner the scRequisitioner to set
	 */
	public void setScRequisitioner(String scRequisitioner) {
		this.scRequisitioner = scRequisitioner;
	}

	/**
	 * @return the scPtj
	 */
	public String getScPtj() {
		return scPtj;
	}

	/**
	 * @param scPtj the scPtj to set
	 */
	public void setScPtj(String scPtj) {
		this.scPtj = scPtj;
	}
	
	// 29558 - Start
	/**
	 * @return the scStatusManyMenu
	 */
	public List<String> getScStatusManyMenu() {
		return scStatusManyMenu;
	}

	/**
	 * @param scStatusManyMenu the scStatusManyMenu to set
	 */
	public void setScStatusManyMenu(List<String> scStatusManyMenu) {
		this.scStatusManyMenu = scStatusManyMenu;
	}

	/**
	 * @return the scStatusManyMenuTemp
	 */
	public List<String> getScStatusManyMenuTemp() {
		return scStatusManyMenuTemp;
	}

	/**
	 * @param scStatusManyMenuTemp the scStatusManyMenuTemp to set
	 */
	public void setScStatusManyMenuTemp(List<String> scStatusManyMenuTemp) {
		this.scStatusManyMenuTemp = scStatusManyMenuTemp;
	}

	/**
	 * @return the hash_scStatusManyMenuId
	 */
	public Map<String, String> getHash_scStatusManyMenuId() {
		return hash_scStatusManyMenuId;
	}

	/**
	 * @param hash_scStatusManyMenuId the hash_scStatusManyMenuId to set
	 */
	public void setHash_scStatusManyMenuId(Map<String, String> hash_scStatusManyMenuId) {
		this.hash_scStatusManyMenuId = hash_scStatusManyMenuId;
	}
	
	// 29558 - End 

}
