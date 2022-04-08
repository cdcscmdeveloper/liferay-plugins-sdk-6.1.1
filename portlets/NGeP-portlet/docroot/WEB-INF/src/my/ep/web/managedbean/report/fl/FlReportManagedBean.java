/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.managedbean.report.ReportBaseManagedBean;
/**
 * Managed Bean for FlReport.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
public class FlReportManagedBean extends ReportBaseManagedBean{

	private static final long serialVersionUID = 1L;
	private PmOrgProfile currentActiveOrg;
	private PmOrgProfile ptj;
	private PmOrgProfile ministry;
	private PmOrgProfile ptjGroup;
	private PmOrgProfile selectedOrgProfile;
	
	private boolean disableOrgFields;
	private boolean supplier;
	
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
	private String group;
	private String requisitioner;
	private String approver;
	private String receivingOfficer;
	private String acknowledgeOfficer;
	private String payOfficer;
	private String supplierCompName;
	private String supplierId;
	
	private Date fromDate;
	private Date toDate;
	
	private Long statusDescId;
	/*29554 - Beginning*/
	private String userRole="";
	
	private List<String> highRoleCodeList = new ArrayList<String>();
	private List<SelectItem> requisitionerList = new ArrayList<SelectItem>();
	private List<SelectItem> approverList = new ArrayList<SelectItem>();
	private List<SelectItem> receivingOfficerList = new ArrayList<SelectItem>();
	private List<SelectItem> acknowledgeOfficerList = new ArrayList<SelectItem>();
	private List<SelectItem> payOfficerList = new ArrayList<SelectItem>();
	
	
	public FlReportManagedBean(){}

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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	public Long getStatusDescId() {
		return statusDescId;
	}

	public void setStatusDescId(Long statusDescId) {
		this.statusDescId = statusDescId;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	public List<String> getHighRoleCodeList() {
		if(highRoleCodeList == null){
			setHighRoleCodeList(new ArrayList<String>());
		}
		return highRoleCodeList;
	}
	public void setHighRoleCodeList(List<String> highRoleCodeList) {
		this.highRoleCodeList = highRoleCodeList;
	}

	public List<SelectItem> getRequisitionerList() {
		return requisitionerList;
	}

	public void setRequisitionerList(List<SelectItem> requisitionerList) {
		this.requisitionerList = requisitionerList;
	}

	public String getRequisitioner() {
		return requisitioner;
	}

	public void setRequisitioner(String requisitioner) {
		this.requisitioner = requisitioner;
	}

	public List<SelectItem> getApproverList() {
		return approverList;
	}

	public void setApproverList(List<SelectItem> approverList) {
		this.approverList = approverList;
	}

	public List<SelectItem> getReceivingOfficerList() {
		return receivingOfficerList;
	}

	public void setReceivingOfficerList(List<SelectItem> receivingOfficerList) {
		this.receivingOfficerList = receivingOfficerList;
	}

	public List<SelectItem> getAcknowledgeOfficerList() {
		return acknowledgeOfficerList;
	}

	public void setAcknowledgeOfficerList(List<SelectItem> acknowledgeOfficerList) {
		this.acknowledgeOfficerList = acknowledgeOfficerList;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public String getReceivingOfficer() {
		return receivingOfficer;
	}

	public void setReceivingOfficer(String receivingOfficer) {
		this.receivingOfficer = receivingOfficer;
	}

	public String getAcknowledgeOfficer() {
		return acknowledgeOfficer;
	}

	public void setAcknowledgeOfficer(String acknowledgeOfficer) {
		this.acknowledgeOfficer = acknowledgeOfficer;
	}

	public List<SelectItem> getPayOfficerList() {
		return payOfficerList;
	}

	public void setPayOfficerList(List<SelectItem> payOfficerList) {
		this.payOfficerList = payOfficerList;
	}

	public String getPayOfficer() {
		return payOfficer;
	}

	public void setPayOfficer(String payOfficer) {
		this.payOfficer = payOfficer;
	}

	public String getSupplierCompName() {
		return supplierCompName;
	}

	public void setSupplierCompName(String supplierCompName) {
		this.supplierCompName = supplierCompName;
	}
	
	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public boolean isSupplier() {
		return supplier;
	}

	public void setSupplier(boolean supplier) {
		this.supplier = supplier;
	}
    /*Logan - 29554 - Beginning*/
	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	/*29554 - Ends*/
	
}
