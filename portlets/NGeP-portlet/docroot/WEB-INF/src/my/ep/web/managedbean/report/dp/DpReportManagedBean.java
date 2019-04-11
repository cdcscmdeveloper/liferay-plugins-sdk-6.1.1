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

}
