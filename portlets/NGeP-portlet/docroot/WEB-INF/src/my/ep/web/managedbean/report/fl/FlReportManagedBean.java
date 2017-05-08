/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.managedbean.report.ReportBaseManagedBean;
/**
 * Managed Bean for FlReport.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
public abstract class FlReportManagedBean extends ReportBaseManagedBean{

	private static final long serialVersionUID = 1L;
	private PmOrgProfile currentActiveOrg;
	private PmOrgProfile selectedMinistry;
	private PmOrgProfile selectedKumpulanPTJ;
	private PmOrgProfile selectedPTJ;
	private PmOrgProfile ptj;
	private PmOrgProfile ministry;
	private PmOrgProfile ptjGroup;
	
	private boolean disableOrgFields;
	
	private String selectedYear;
	private String contractNo;
	private String physicalContractNo;
	private String contractRequestNo;
	private String supplierName;
	private String panelName;
	private String loaNo;
	private String physicalLoaNo;
	private String purchaseOrderNo;
	private String pkpNo;
	
	public FlReportManagedBean(){}

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
	 * @return the disableOrgFields
	 */
	public boolean isDisableOrgFields() {
		return disableOrgFields;
	}

	/**
	 * @param disableOrgFields the disableOrgFields to set
	 */
	public void setDisableOrgFields(boolean disableOrgFields) {
		this.disableOrgFields = disableOrgFields;
	}

	/**
	 * @return the selectedYear
	 */
	public String getSelectedYear() {
		return selectedYear;
	}

	/**
	 * @param selectedYear the selectedYear to set
	 */
	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
	}

	public PmOrgProfile getPtj() {
		return ptj;
	}

	public void setPtj(PmOrgProfile ptj) {
		this.ptj = ptj;
	}

	public PmOrgProfile getMinistry() {
		return ministry;
	}

	public void setMinistry(PmOrgProfile ministry) {
		this.ministry = ministry;
	}

	public PmOrgProfile getPtjGroup() {
		return ptjGroup;
	}

	public void setPtjGroup(PmOrgProfile ptjGroup) {
		this.ptjGroup = ptjGroup;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getPhysicalContractNo() {
		return physicalContractNo;
	}

	public void setPhysicalContractNo(String physicalContractNo) {
		this.physicalContractNo = physicalContractNo;
	}

	public String getContractRequestNo() {
		return contractRequestNo;
	}

	public void setContractRequestNo(String contractRequestNo) {
		this.contractRequestNo = contractRequestNo;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getPanelName() {
		return panelName;
	}

	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}

	public String getLoaNo() {
		return loaNo;
	}

	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}

	public String getPhysicalLoaNo() {
		return physicalLoaNo;
	}

	public void setPhysicalLoaNo(String physicalLoaNo) {
		this.physicalLoaNo = physicalLoaNo;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public String getPkpNo() {
		return pkpNo;
	}

	public void setPkpNo(String pkpNo) {
		this.pkpNo = pkpNo;
	}
}
