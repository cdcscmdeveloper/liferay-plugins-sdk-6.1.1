/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.TreeNode;

import my.ep.persistence.entity.ct.CtAddress;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;

/**
 * Managed Bean For Contract
 * 
 * @author kieyun
 * @since 13/11/2013	 
 */
@ManagedBean(name="ctManagedBean")
@SessionScoped
public class ContractManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private String fileDirectory;
	private int attachFileIndex;
	private boolean viewContract = false;
	private boolean commonView = false;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtAttachment> deleteAttachmentList;
	private CtContractVer contractVer;
	private CtContractVer agreementContractVer;
	private boolean updateCompanyName;
	private String supplierCompanyName;
	private String supplierBranchName;
	private String supplierRegNo;
	private String supplierMofNo;
	private String supplierAddress;
	private boolean updateSupplierAddress;
	private CtAddress ctAddress;
	private boolean directPurchase;
	private boolean gstCheckingGUI;
	private String branchInfoFilePath;
	
	/*Schedule start*/
	private String frequencySchedule;
	private boolean disableScheduleRadio;
	private Map<String,Object> radioButtonSchedule;
	private boolean chkProgressive;
	private boolean chkSchedule;
	private boolean progSchedButtonDeactive;
	private List<String> yearList;
	
	/*Deduction start*/
	private CtDeduction selectedDeduction;
	private boolean flgPaymentTracking;
	private boolean ButtonHideActive = true;
	private boolean buttonHideDedActive = true;
	private boolean buttonHideSchActive = true;
	
	/*Payment tracking start*/
	private BigDecimal gstTotalAmount;
	private BigDecimal paidInvoiceAmt;
	private double advancePaymentAmt;
	private BigDecimal paidAdvancePaymentAmt;
	
	/*Zone start*/
	private TreeNode ctZoneTrees;
	
	/*Agency start*/
	private CtAgency selectedAgency;
	private int index;
	private List<CtAgencyAddress> selectedListOfAddress;
	private Long ministryProfileId;
	private Long jabatanProfileId;
	private String ptjCodeName;
	private String ptjCodeNameSearchValues;
	private List<PmAddressType> addressTypeList;
	private List<CtContractItem> ctItemList;
	
	private CtContractAmount ctContractAmount;	
	private boolean disableGstAmount;	
	private boolean disableContractAmount = true;
	private boolean chkConceValue;
	private boolean chkJustification;
	
	 private boolean selectAll;
	 private CtPerformanceBond ctPerformanceBond;
	 
	 private CtContractItem selectedItem;
	 private String showItem;
	 private String showServiceItem;
	 private List<CtContractItem> ctItemServiceList;
	 private CtContractItem selectedServiceItem;
	 
	 private boolean disableAmendDetails;
	 private CtCircularVer ctCircularVer;
	// private List<CtPidReason> selectedPidReason;
	 
	  private boolean updateContract = false;
	  private boolean serviceTaxAlertFlg = false;
	  
	  private CtContractItem selectedItemForSpec;
	
	/**
	 * @return the fileDirectory
	 */
	public String getFileDirectory() {
		return fileDirectory;
	}
	
	/**
	 * @param fileDirectory the fileDirectory to set
	 */
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}	
	
	/**
	 * @return the attachFileIndex
	 */
	public int getAttachFileIndex() {
		return attachFileIndex;
	}
	
	/**
	 * @param attachFileIndex the attachFileIndex to set
	 */
	public void setAttachFileIndex(int attachFileIndex) {
		this.attachFileIndex = attachFileIndex;
	}
	
	/**
	 * @return the viewContract
	 */
	public boolean isViewContract() {
		return viewContract;
	}
	
	/**
	 * @param viewContract the viewContract to set
	 */
	public void setViewContract(boolean viewContract) {
		this.viewContract = viewContract;
	}
	
	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if (remarkList == null) {
			remarkList = new ArrayList<CtRemark>();
		}
		return remarkList;
	}
	
	/**
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}
	
	/**
	 * @return the attachmentList
	 */
	public List<CtAttachment> getAttachmentList() {
		if (attachmentList == null) {
			attachmentList = new ArrayList<CtAttachment>();
		}
		return attachmentList;
	}
	
	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * @return the supplierCompanyName
	 */
	public String getSupplierCompanyName() {
		return supplierCompanyName;
	}

	/**
	 * @param supplierCompanyName the supplierCompanyName to set
	 */
	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}

	/**
	 * @return the updateCompanyName
	 */
	public boolean isUpdateCompanyName() {
		return updateCompanyName;
	}

	/**
	 * @param updateCompanyName the updateCompanyName to set
	 */
	public void setUpdateCompanyName(boolean updateCompanyName) {
		this.updateCompanyName = updateCompanyName;
	}

	/**
	 * @return the supplierBranchName
	 */
	public String getSupplierBranchName() {
		return supplierBranchName;
	}

	/**
	 * @param supplierBranchName the supplierBranchName to set
	 */
	public void setSupplierBranchName(String supplierBranchName) {
		this.supplierBranchName = supplierBranchName;
	}	

	/**
	 * @return the supplierRegNo
	 */
	public String getSupplierRegNo() {
		return supplierRegNo;
	}

	/**
	 * @param supplierRegNo the supplierRegNo to set
	 */
	public void setSupplierRegNo(String supplierRegNo) {
		this.supplierRegNo = supplierRegNo;
	}

	/**
	 * @return the supplierMofNo
	 */
	public String getSupplierMofNo() {
		return supplierMofNo;
	}

	/**
	 * @param supplierMofNo the supplierMofNo to set
	 */
	public void setSupplierMofNo(String supplierMofNo) {
		this.supplierMofNo = supplierMofNo;
	}

	/**
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the updateSupplierAddress
	 */
	public boolean isUpdateSupplierAddress() {
		return updateSupplierAddress;
	}

	/**
	 * @param updateSupplierAddress the updateSupplierAddress to set
	 */
	public void setUpdateSupplierAddress(boolean updateSupplierAddress) {
		this.updateSupplierAddress = updateSupplierAddress;
	}

	/**
	 * @return the ctAddress
	 */
	public CtAddress getCtAddress() {
		return ctAddress;
	}

	/**
	 * @param ctAddress the ctAddress to set
	 */
	public void setCtAddress(CtAddress ctAddress) {
		this.ctAddress = ctAddress;
	}

	/**
	 * @return the directPurchase
	 */
	public boolean isDirectPurchase() {
		return directPurchase;
	}

	/**
	 * @param directPurchase the directPurchase to set
	 */
	public void setDirectPurchase(boolean directPurchase) {
		this.directPurchase = directPurchase;
	}

	/**
	 * @return the gstCheckingGUI
	 */
	public boolean isGstCheckingGUI() {
		return gstCheckingGUI;
	}

	/**
	 * @param gstCheckingGUI the gstCheckingGUI to set
	 */
	public void setGstCheckingGUI(boolean gstCheckingGUI) {
		this.gstCheckingGUI = gstCheckingGUI;
	}

	/**
	 * @return the branchInfoFilePath
	 */
	public String getBranchInfoFilePath() {
		return branchInfoFilePath;
	}

	/**
	 * @param branchInfoFilePath the branchInfoFilePath to set
	 */
	public void setBranchInfoFilePath(String branchInfoFilePath) {
		this.branchInfoFilePath = branchInfoFilePath;
	}	
	
	/**
	 * @return the radioButtonSchedule
	 */
	public Map<String, Object> getRadioButtonSchedule() {
		if (radioButtonSchedule == null) {
			radioButtonSchedule = new LinkedHashMap<String,Object>();
			FacesScopes face = new FacesScopes();
			radioButtonSchedule.put(ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), "S");
			radioButtonSchedule.put(ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), "P");
		}
		return radioButtonSchedule;
	}

	/**
	 * @param radioButtonSchedule the radioButtonSchedule to set
	 */
	public void setRadioButtonSchedule(Map<String, Object> radioButtonSchedule) {
		this.radioButtonSchedule = radioButtonSchedule;
	}
	
	/**
	 * @return the frequencySchedule
	 */
	public String getFrequencySchedule() {
		return frequencySchedule;
	}

	/**
	 * @param frequencySchedule the frequencySchedule to set
	 */
	public void setFrequencySchedule(String frequencySchedule) {
		this.frequencySchedule = frequencySchedule;
	}

	/**
	 * @return the disableScheduleRadio
	 */
	public boolean isDisableScheduleRadio() {
		return disableScheduleRadio;
	}

	/**
	 * @param disableScheduleRadio the disableScheduleRadio to set
	 */
	public void setDisableScheduleRadio(boolean disableScheduleRadio) {
		this.disableScheduleRadio = disableScheduleRadio;
	}
	
	/**
	 * @return the chkProgressive
	 */
	public boolean isChkProgressive() {
		return chkProgressive;
	}

	/**
	 * @param chkProgressive the chkProgressive to set
	 */
	public void setChkProgressive(boolean chkProgressive) {
		this.chkProgressive = chkProgressive;
	}

	/**
	 * @return the chkSchedule
	 */
	public boolean isChkSchedule() {
		return chkSchedule;
	}

	/**
	 * @param chkSchedule the chkSchedule to set
	 */
	public void setChkSchedule(boolean chkSchedule) {
		this.chkSchedule = chkSchedule;
	}

	/**
	 * @return the progSchedButtonDeactive
	 */
	public boolean isProgSchedButtonDeactive() {
		return progSchedButtonDeactive;
	}

	/**
	 * @param progSchedButtonDeactive the progSchedButtonDeactive to set
	 */
	public void setProgSchedButtonDeactive(boolean progSchedButtonDeactive) {
		this.progSchedButtonDeactive = progSchedButtonDeactive;
	}

	/**
	 * @return the contractVer
	 */
	public CtContractVer getContractVer() {
		return contractVer;
	}

	/**
	 * @param contractVer the contractVer to set
	 */
	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}
	
	/**
	 * @return the yearList
	 */
	public List<String> getYearList() {
		return yearList;
	}

	/**
	 * @param yearList the yearList to set
	 */
	public void setYearList(List<String> yearList) {
		this.yearList = yearList;
	}
	
	/**
	 * @return the selectedDeduction
	 */
	public CtDeduction getSelectedDeduction() {
		return selectedDeduction;
	}

	/**
	 * @param selectedDeduction the selectedDeduction to set
	 */
	public void setSelectedDeduction(CtDeduction selectedDeduction) {
		this.selectedDeduction = selectedDeduction;
	}

	/**
	 * @return the flgPaymentTracking
	 */
	public boolean isFlgPaymentTracking() {
		return flgPaymentTracking;
	}

	/**
	 * @param flgPaymentTracking the flgPaymentTracking to set
	 */
	public void setFlgPaymentTracking(boolean flgPaymentTracking) {
		this.flgPaymentTracking = flgPaymentTracking;
	}

	/**
	 * @return the gstTotalAmount
	 */
	public boolean isButtonHideActive() {
		return ButtonHideActive;
	}

	/**
	 * @param buttonHideActive the buttonHideActive to set
	 */
	public void setButtonHideActive(boolean buttonHideActive) {
		ButtonHideActive = buttonHideActive;
	}
	
	/**
	 * @return the gstTotalAmount
	 */
	public BigDecimal getGstTotalAmount() {
		return gstTotalAmount;
	}

	/**
	 * @param gstTotalAmount the gstTotalAmount to set
	 */
	public void setGstTotalAmount(BigDecimal gstTotalAmount) {
		this.gstTotalAmount = gstTotalAmount;
	}
	
	/**
	 * @return the paidInvoiceAmt
	 */
	public BigDecimal getPaidInvoiceAmt() {
		return paidInvoiceAmt;
	}

	/**
	 * @param paidInvoiceAmt the paidInvoiceAmt to set
	 */
	public void setPaidInvoiceAmt(BigDecimal paidInvoiceAmt) {
		this.paidInvoiceAmt = paidInvoiceAmt;
	}
	
	/**
	 * @return the advancePaymentAmt
	 */
	public double getAdvancePaymentAmt() {
		return advancePaymentAmt;
	}

	/**
	 * @param advancePaymentAmt the advancePaymentAmt to set
	 */
	public void setAdvancePaymentAmt(double advancePaymentAmt) {
		this.advancePaymentAmt = advancePaymentAmt;
	}
	
	/**
	 * @return the paidAdvancePaymentAmt
	 */
	public BigDecimal getPaidAdvancePaymentAmt() {
		return paidAdvancePaymentAmt;
	}

	/**
	 * @param paidAdvancePaymentAmt the paidAdvancePaymentAmt to set
	 */
	public void setPaidAdvancePaymentAmt(BigDecimal paidAdvancePaymentAmt) {
		this.paidAdvancePaymentAmt = paidAdvancePaymentAmt;
	}
	
	/**
	 * @return the ctZoneTrees
	 */
	public TreeNode getCtZoneTrees() {
		return ctZoneTrees;
	}

	/**
	 * @param ctZoneTrees the ctZoneTrees to set
	 */
	public void setCtZoneTrees(TreeNode ctZoneTrees) {
		this.ctZoneTrees = ctZoneTrees;
	}
	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * @return the selectedListOfAddress
	 */
	public List<CtAgencyAddress> getSelectedListOfAddress() {
		return selectedListOfAddress;
	}

	/**
	 * @param selectedListOfAddress the selectedListOfAddress to set
	 */
	public void setSelectedListOfAddress(List<CtAgencyAddress> selectedListOfAddress) {
		this.selectedListOfAddress = selectedListOfAddress;
	}
	
	/**
	 * @return the selectedAgency
	 */
	public CtAgency getSelectedAgency() {
		return selectedAgency;
	}

	/**
	 * @param selectedAgency the selectedAgency to set
	 */
	public void setSelectedAgency(CtAgency selectedAgency) {
		this.selectedAgency = selectedAgency;
	}	
	
	/**
	 * @return the ptjCodeName
	 */
	public String getPtjCodeName() {
		return ptjCodeName;
	}

	/**
	 * @param ptjCodeName the ptjCodeName to set
	 */
	public void setPtjCodeName(String ptjCodeName) {
		this.ptjCodeName = ptjCodeName;
	}

	/**
	 * @return the ptjCodeNameSearchValues
	 */
	public String getPtjCodeNameSearchValues() {
		return ptjCodeNameSearchValues;
	}

	/**
	 * @param ptjCodeNameSearchValues the ptjCodeNameSearchValues to set
	 */
	public void setPtjCodeNameSearchValues(String ptjCodeNameSearchValues) {
		this.ptjCodeNameSearchValues = ptjCodeNameSearchValues;
	}
	
	/**
	 * @return the ministryProfileId
	 */
	public Long getMinistryProfileId() {
		return ministryProfileId;
	}

	/**
	 * @param ministryProfileId the ministryProfileId to set
	 */
	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}
	
	/**
	 * @return the jabatanProfileId
	 */
	public Long getJabatanProfileId() {
		return jabatanProfileId;
	}

	/**
	 * @param jabatanProfileId the jabatanProfileId to set
	 */
	public void setJabatanProfileId(Long jabatanProfileId) {
		this.jabatanProfileId = jabatanProfileId;
	}	
	
	/**
	 * @return the addressTypeList
	 */
	public List<PmAddressType> getAddressTypeList() {
		return addressTypeList;
	}

	/**
	 * @param addressTypeList the addressTypeList to set
	 */
	public void setAddressTypeList(List<PmAddressType> addressTypeList) {
		this.addressTypeList = addressTypeList;
	}
	
	/**
	 * @return the ctItemList
	 */
	public List<CtContractItem> getCtItemList() {
		if (ctItemList == null) {
			ctItemList = new ArrayList<CtContractItem>();
		}
		return ctItemList;
	}

	/**
	 * @param ctItemList the ctItemList to set
	 */
	public void setCtItemList(List<CtContractItem> ctItemList) {
		this.ctItemList = ctItemList;
	}
	
	/**
	 * @return the ctContractAmount
	 */
	public CtContractAmount getCtContractAmount() {
		return ctContractAmount;
	}

	/**
	 * @param ctContractAmount the ctContractAmount to set
	 */
	public void setCtContractAmount(CtContractAmount ctContractAmount) {
		this.ctContractAmount = ctContractAmount;
	}
	
	/**
	 * @return boolean disableGstAmount
	 */
	public boolean isDisableGstAmount() {
		return disableGstAmount;
	}
 
	/**
	 * @param disableGstAmount the disableGstAmount to set
	 */
	public void setDisableGstAmount(boolean disableGstAmount) {
		this.disableGstAmount = disableGstAmount;
	}
		
	/**
	 * @return the disableContractAmount
	 */
	public boolean isDisableContractAmount() {
		return disableContractAmount;
	}

	/**
	 * @param disableContractAmount the disableContractAmount to set
	 */
	public void setDisableContractAmount(boolean disableContractAmount) {
		this.disableContractAmount = disableContractAmount;
	}

	/**
	 * @return the chkConceValue
	 */
	public boolean isChkConceValue() {
		return chkConceValue;
	}

	/**
	 * @param chkConceValue the chkConceValue to set
	 */
	public void setChkConceValue(boolean chkConceValue) {
		this.chkConceValue = chkConceValue;
	}
	
	/**
	 * @return the chkJustification
	 */
	public boolean isChkJustification() {
		return chkJustification;
	}

	/**
	 * @param chkJustification the chkJustification to set
	 */
	public void setChkJustification(boolean chkJustification) {
		this.chkJustification = chkJustification;
	}
	
	/**
	 * @return the agreementContractVer
	 */
	public CtContractVer getAgreementContractVer() {
		return agreementContractVer;
	}

	/**
	 * @param agreementContractVer the agreementContractVer to set
	 */
	public void setAgreementContractVer(CtContractVer agreementContractVer) {
		this.agreementContractVer = agreementContractVer;
	}
	
	/**
	 * @return the selectAll
	 */
	public boolean isSelectAll() {
		return selectAll;
	}

	/**
	 * @param selectAll the selectAll to set
	 */
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	
	/**
	 * @return the ctPerformanceBond
	 */
	public CtPerformanceBond getCtPerformanceBond() {
		return ctPerformanceBond;
	}

	/**
	 * @param ctPerformanceBond the ctPerformanceBond to set
	 */
	public void setCtPerformanceBond(CtPerformanceBond ctPerformanceBond) {
		this.ctPerformanceBond = ctPerformanceBond;
	}
	
	/**
	 * @return the selectedItem
	 */
	public CtContractItem getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelectedItem(CtContractItem selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	/**
	 * @return the showItem
	 */
	public String getShowItem() {
		return showItem;
	}

	/**
	 * @param showItem the showItem to set
	 */
	public void setShowItem(String showItem) {
		this.showItem = showItem;
	}	
	
	/**
	 * @return the showServiceItem
	 */
	public String getShowServiceItem() {
		return showServiceItem;
	}

	/**
	 * @param showServiceItem the showServiceItem to set
	 */
	public void setShowServiceItem(String showServiceItem) {
		this.showServiceItem = showServiceItem;
	}

	/**
	 * @return the ctItemServiceList
	 */
	public List<CtContractItem> getCtItemServiceList() {
		if (ctItemServiceList == null) {
			ctItemServiceList = new ArrayList<CtContractItem>();
		}
		return ctItemServiceList;
	}

	/**
	 * @param ctItemServiceList the ctItemServiceList to set
	 */
	public void setCtItemServiceList(List<CtContractItem> ctItemServiceList) {
		this.ctItemServiceList = ctItemServiceList;
	}

	/**
	 * @return the selectedServiceItem
	 */
	public CtContractItem getSelectedServiceItem() {
		return selectedServiceItem;
	}

	/**
	 * @param selectedServiceItem the selectedServiceItem to set
	 */
	public void setSelectedServiceItem(CtContractItem selectedServiceItem) {
		this.selectedServiceItem = selectedServiceItem;
	}

	/**
	 * @return the disableAmendDetails
	 */
	public boolean isDisableAmendDetails() {
		return disableAmendDetails;
	}

	/**
	 * @param disableAmendDetails the disableAmendDetails to set
	 */
	public void setDisableAmendDetails(boolean disableAmendDetails) {
		this.disableAmendDetails = disableAmendDetails;
	}
	
	/**
	 * @return the ctCircularVer
	 */
	public CtCircularVer getCtCircularVer() {
		return ctCircularVer;
	}

	/**
	 * @param ctCircularVer the ctCircularVer to set
	 */
	public void setCtCircularVer(CtCircularVer ctCircularVer) {
		this.ctCircularVer = ctCircularVer;
	}

	/**
	 * @return the updateContract
	 */
	public boolean isUpdateContract() {
		return updateContract;
	}

	/**
	 * @param updateContract the updateContract to set
	 */
	public void setUpdateContract(boolean updateContract) {
		this.updateContract = updateContract;
	}

	/**
	 * @return the selectedPidReason
	 *//*
	public List<CtPidReason> getSelectedPidReason() {
		return selectedPidReason;
	}

	*//**
	 * @param selectedPidReason the selectedPidReason to set
	 *//*
	public void setSelectedPidReason(List<CtPidReason> selectedPidReason) {
		this.selectedPidReason = selectedPidReason;
	}
*/
	/**
	 * @return the serviceTaxAlertFlg
	 */
	public boolean isServiceTaxAlertFlg() {
		return serviceTaxAlertFlg;
	}

	/**
	 * @param serviceTaxAlertFlg the serviceTaxAlertFlg to set
	 */
	public void setServiceTaxAlertFlg(boolean serviceTaxAlertFlg) {
		this.serviceTaxAlertFlg = serviceTaxAlertFlg;
	}

	/**
	 * @return the buttonHideDedActive
	 */
	public boolean isButtonHideDedActive() {
		return buttonHideDedActive;
	}

	/**
	 * @param buttonHideDedActive the buttonHideDedActive to set
	 */
	public void setButtonHideDedActive(boolean buttonHideDedActive) {
		this.buttonHideDedActive = buttonHideDedActive;
	}

	/**
	 * @return the buttonHideSchActive
	 */
	public boolean isButtonHideSchActive() {
		return buttonHideSchActive;
	}

	/**
	 * @param buttonHideSchActive the buttonHideSchActive to set
	 */
	public void setButtonHideSchActive(boolean buttonHideSchActive) {
		this.buttonHideSchActive = buttonHideSchActive;
	}
	
	/**
	 * @return the deleteAttachmentList
	 */
	public List<CtAttachment> getDeleteAttachmentList() {
		if(deleteAttachmentList == null)
			   deleteAttachmentList = new ArrayList<CtAttachment>();
		return deleteAttachmentList;
	}

	/**
	 * @param deleteAttachmentList the deleteAttachmentList to set
	 */
	public void setDeleteAttachmentList(List<CtAttachment> deleteAttachmentList) {
		this.deleteAttachmentList = deleteAttachmentList;
	}

	/**
	 * @return the selectedItemForSpec
	 */
	public CtContractItem getSelectedItemForSpec() {
		return selectedItemForSpec;
	}

	/**
	 * @param selectedItemForSpec the selectedItemForSpec to set
	 */
	public void setSelectedItemForSpec(CtContractItem selectedItemForSpec) {
		this.selectedItemForSpec = selectedItemForSpec;
	}

	/**
	 * @return the commonView
	 */
	public boolean isCommonView() {
		return commonView;
	}

	/**
	 * @param commonView the commonView to set
	 */
	public void setCommonView(boolean commonView) {
		this.commonView = commonView;
	}
}
