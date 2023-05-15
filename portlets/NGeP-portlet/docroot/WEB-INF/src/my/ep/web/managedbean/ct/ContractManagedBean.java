/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.CtAddress;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sc.common.ScRequestItemSpecOffer;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmSupplierBank; //Fix - #33674
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
	private boolean staxCheckingGUI;
	private boolean newPk7;
	private boolean disableAgent = false;
	private String branchInfoFilePath;
	private List<CtAgreement> ctAgreementList;

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
	private boolean ownerOrgProfile = true;

	/*Payment tracking start*/
	private BigDecimal gstTotalAmount;
	private BigDecimal paidInvoiceAmt;
	private BigDecimal paidsumInvoiceAmt;
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
	private boolean disableStaxAmount;	
	private boolean disableContractAmount = true;
	private boolean bondWaiver;
	private CtContractAmount ctTestBondWaiverAmount;

	private boolean selectAll;
	private CtPerformanceBond ctPerformanceBond;

	private CtContractItem selectedItem;
	private CtContractItem serviceselectedItem;
	private String showItem;
	private String showServiceItem;
	private List<CtContractItem> ctItemServiceList;
	private CtContractItem selectedServiceItem;

	private boolean disableAmendDetails;
	private CtCircularVer ctCircularVer;

	private boolean updateContract = false;
	private boolean serviceTaxAlertFlg = false;
	//#26083 - basri
	private boolean notificationSetting = false;
		
	private CtContractItem selectedItemForSpec;

	// Change Log
	private Map<String, CtAgency> newAgencyMap;
	private Map<String, CtAgency> updateAgencyMap;
	private Map<String, CtAgency> deleteAgencyMap;
	private Map<String, CtContractZone> updateZoneMap;	
	private Map<Long, CtContractItem> updateItemMap;		  
	private List<PmOrgProfile> agencySelected;
	
	private boolean contractExpired;
	//LOG : 13847, by Fahmi 22032019 start
	private Date waiverStartDate;
	private Date waiverEndDate;
	private String referenceNo;
	private CtAttachment ctAttachment;
	private List<ScLoa> bondDetail;
	// #24333 - Start
	private boolean extendContract = false;
	private boolean disableOriginalBond = false;
	// #24333 - End
	
	// Start - 28479	
	private boolean isErr100033 = false;
	private boolean isErr100034 = false;
	private boolean minQtyCheck = false;
	// End - 28479	
	
	private boolean hasAmendmentLoaNo = false; //15851
	private boolean fromViewPage = false;//15851
	private boolean amendFromViewPage = false;//15851
	private boolean viewSCL = false;//15851
	
	private boolean viewSCLFromContract = false;//34142
	private String[] outcomesFromContract = null;//34142, 15851 04/12/2022
	
	//Bug 34142, 15851 - shaqimi start 04/12/2022
	private boolean amendFromTaskList = false;
	private boolean viewContractFromAgreement = false;
	private boolean backFromSCL = false;
	private String docTypeFromTaskList = null;
	private Long contractIdFromTaskList = null;
	private Long contractVerIdFromTaskList = null;
	private String taskNameFromTaskList = null;
	private String taskIdFromTaskList = null;
	private String docTypeFromAgreementTaskList = null;
	private Long contractIdFromAgreementTaskList = null;
	private Long docIdFromAgreementTaskList = null;
	private Long agreementIdFromAgreementTaskList = null;
	private String taskIdFromAgreementTaskList = null;
	private Long contractVerIdFromAgreementTaskList = null;
	private boolean backFromContract = false;
	private Long statusIdFromTaskList = null;
	private boolean inPage = false;
	//Bug 34142, 15851 - shaqimi end 04/12/2022

	//#28854 - start
	private List<String> itemSpecification;
	private String newOfferSpec;
	private String supplierOffer;
	private List<ScRequestItemSpecOffer> scRequestItemSpecOfferList;
	//#28854 - end	
	
	//#13847 - start
	private boolean disableDeferment = false;
	private CtBondWaiverDetail bondWaiverLatestTemp;
	//13847 end
	
	//#29983 start
	private String businessRegNoNew;
	//#29983 end
	
	//Start Fix - #31740
	private Map<Long, CtAgency> selectedAgencyChanges;
	
	private Map<Long, CtAgency> agencyDataDeletion;
	//End Fix - #31740
	//Start Fix - #33674
	private String bankInfoDetails;
	private Map<String, SmSupplierBank> supplierBankAccNo;
	private Map<String, SmSupplierBank> supplierBankName;
	private Long applID;
	//End Fix - #33674
	
	
	//#33126 hafiz start
	private boolean inCtPerformance = false;
	private boolean fromUpdateButton = false;
	
	private String itemstatus;
	private PmUom selectedSearchItem;
	private String skuno;
	private String typeofgoods;
	private boolean resetval = true;
	private boolean servicecollapse = false;
	private boolean prodcollapse = false;
	private boolean readOnly = true;

	private boolean itemProductExist;
	private boolean itemServiceExist;
	private boolean itemProductNotExist;
	private boolean itemServiceNotExist;

	private String itemcode;
	private String itemtype;

	private IPagingDataModel<CtContractItem> contractListingPagingDataModel = new ContractListingPagingDataModel();
	private IPagingDataModel<CtContractItem> ServiceListingPagingDataModel = new ServiceListingPagingDataModel();

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isServicecollapse() {
		return servicecollapse;
	}

	public void setServicecollapse(boolean servicecollapse) {
		this.servicecollapse = servicecollapse;
	}

	public boolean isProdcollapse() {
		return prodcollapse;
	}

	public void setProdcollapse(boolean prodcollapse) {
		this.prodcollapse = prodcollapse;
	}

	public boolean isResetval() {
		return resetval;
	}

	public void setResetval(boolean resetval) {
		this.resetval = resetval;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public String getTypeofgoods() {
		return typeofgoods;
	}

	public void setTypeofgoods(String typeofgoods) {
		this.typeofgoods = typeofgoods;
	}

	public String getSkuno() {
		return skuno;
	}

	public void setSkuno(String skuno) {
		this.skuno = skuno;
	}

	public String getItemcode() {
		return itemcode;
	}

	public void setItemcode(String itemcode) {
		this.itemcode = itemcode;
	}

	public String getItemstatus() {
		return itemstatus;
	}

	public void setItemstatus(String itemstatus) {
		this.itemstatus = itemstatus;
	}

	private String itemname;

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}
	
	// #33126 hafiz end
	
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
	 * @return the pk7
	 */
	public boolean isNewPk7() {
		return newPk7;
	}

	/**
	 * @param pk7 the pk7 to set
	 */
	public void setNewPk7(boolean newPk7) {
		this.newPk7 = newPk7;
	}
	
	/**
	 * @return the disableAgent button
	 */
	public boolean isDisableAgent() {
		return disableAgent;
	}

	/**
	 * @param disableAgent the disableAgent button to set
	 */
	public void setDisableAgent(boolean disableAgent) {
		this.disableAgent = disableAgent;
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
	 * @return the staxCheckingGUI
	 */
	public boolean isStaxCheckingGUI() {
		return staxCheckingGUI;
	}

	/**
	 * @param staxCheckingGUI the staxCheckingGUI to set
	 */
	public void setStaxCheckingGUI(boolean staxCheckingGUI) {
		this.staxCheckingGUI = staxCheckingGUI;
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
		/**	Enhancement #25657
		 *  This is temporary solution until full version of progressive payment to be developed.
		 *
		if (radioButtonSchedule == null) {
			radioButtonSchedule = new LinkedHashMap<String,Object>();
			FacesScopes face = new FacesScopes();
			radioButtonSchedule.put(ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), "S");
			radioButtonSchedule.put(ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), "P");
		 }
		*/
		if (getFrequencySchedule().equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_SCHEDULE)) {
			radioButtonSchedule = new LinkedHashMap<String,Object>();
			FacesScopes face = new FacesScopes();
			radioButtonSchedule.put(ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), "S");
			
		 }
		else if (getFrequencySchedule().equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_PROGRESSIVE)) {
			radioButtonSchedule = new LinkedHashMap<String,Object>();
			FacesScopes face = new FacesScopes();
			radioButtonSchedule.put(ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), "S");
			radioButtonSchedule.put(ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), "P");
		 }
		else if (radioButtonSchedule == null){
			radioButtonSchedule = new LinkedHashMap<String,Object>();
			FacesScopes face = new FacesScopes();
			radioButtonSchedule.put(ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), "S");

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
	 * @return the paidsumInvoiceAmt
	 */
	public BigDecimal getPaidsumInvoiceAmt() {
		return paidsumInvoiceAmt;
	}

	/**
	 * @param paidsumInvoiceAmt the paidsumInvoiceAmt to set
	 */
	public void setPaidsumInvoiceAmt(BigDecimal paidsumInvoiceAmt) {
		this.paidsumInvoiceAmt = paidsumInvoiceAmt;
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
		if(ctContractAmount == null){
			ctContractAmount = new CtContractAmount();
		}
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
	 * @return boolean disableStaxAmount
	 */
	public boolean isDisableStaxAmount() {
		return disableStaxAmount;
	}

	/**
	 * @param disableStaxAmount the disableStaxAmount to set
	 */
	public void setDisableStaxAmount(boolean disableStaxAmount) {
		this.disableStaxAmount = disableStaxAmount;
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

	public boolean isBondWaiver() {
		return bondWaiver;
	}

	public void setBondWaiver(boolean bondWaiver) {
		this.bondWaiver = bondWaiver;
	}

	public Map<String, CtAgency> getNewAgencyMap() {
		if(newAgencyMap==null) {
			newAgencyMap = new HashMap<String, CtAgency>();
		}
		return newAgencyMap;
	}

	public void setNewAgencyMap(Map<String, CtAgency> newAgencyMap) {
		this.newAgencyMap = newAgencyMap;
	}

	public Map<String, CtAgency> getUpdateAgencyMap() {
		if(updateAgencyMap==null) {
			updateAgencyMap = new HashMap<String, CtAgency>();
		}
		return updateAgencyMap;
	}

	public void setUpdateAgencyMap(Map<String, CtAgency> updateAgencyMap) {
		this.updateAgencyMap = updateAgencyMap;
	}

	public Map<String, CtAgency> getDeleteAgencyMap() {
		if(deleteAgencyMap==null) {
			deleteAgencyMap = new HashMap<String, CtAgency>();
		}		
		return deleteAgencyMap;
	}

	public void setDeleteAgencyMap(Map<String, CtAgency> deleteAgencyMap) {
		this.deleteAgencyMap = deleteAgencyMap;
	}

	public Map<Long, CtContractItem> getUpdateItemMap() {
		if(updateItemMap==null) {
			updateItemMap = new HashMap<Long, CtContractItem>();
		}
		return updateItemMap;
	}

	public void setUpdateItemMap(Map<Long, CtContractItem> updateItemMap) {
		this.updateItemMap = updateItemMap;
	}

	public boolean isOwnerOrgProfile() {
		return ownerOrgProfile;
	}

	public void setOwnerOrgProfile(boolean ownerOrgProfile) {
		this.ownerOrgProfile = ownerOrgProfile;
	}

	public Map<String, CtContractZone> getUpdateZoneMap() {
		if(updateZoneMap==null) {
			updateZoneMap = new HashMap<String, CtContractZone>();
		}
		return updateZoneMap;
	}

	public void setUpdateZoneMap(Map<String, CtContractZone> updateZoneMap) {
		this.updateZoneMap = updateZoneMap;
	}

	public CtContractAmount getCtTestBondWaiverAmount() {
		return ctTestBondWaiverAmount;
	}

	public void setCtTestBondWaiverAmount(CtContractAmount ctTestBondWaiverAmount) {
		this.ctTestBondWaiverAmount = ctTestBondWaiverAmount;
	}

	public List<PmOrgProfile> getAgencySelected() {
		return agencySelected;
	}

	public void setAgencySelected(List<PmOrgProfile> agencySelected) {
		this.agencySelected = agencySelected;
	}

	public boolean isContractExpired() {
		return contractExpired;
	}

	public void setContractExpired(boolean contractExpired) {
		this.contractExpired = contractExpired;
	}

	/**
	 * @return the ctAgreementList
	 */
	public List<CtAgreement> getCtAgreementList() {
		return ctAgreementList;
	}

	/**
	 * @param ctAgreementList the ctAgreementList to set
	 */
	public void setCtAgreementList(List<CtAgreement> ctAgreementList) {
		this.ctAgreementList = ctAgreementList;
	}
	
	//13847 start
	public Date getWaiverStartDate() {
		return waiverStartDate;
	}

	public void setWaiverStartDate(Date waiverStartDate) {
		this.waiverStartDate = waiverStartDate;
	}

	public Date getWaiverEndDate() {
		return waiverEndDate;
	}

	public void setWaiverEndDate(Date waiverEndDate) {
		this.waiverEndDate = waiverEndDate;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public CtAttachment getCtAttachment() {
		return ctAttachment;
	}

	public void setCtAttachment(CtAttachment ctAttachment) {
		this.ctAttachment = ctAttachment;
	}
	//13847 end
	
	public CtContractItem getServiceselectedItem() {
		return serviceselectedItem;
	}

	public void setServiceselectedItem(CtContractItem serviceselectedItem) {
		this.serviceselectedItem = serviceselectedItem;
	}

	public List<ScLoa> getBondDetail() {
		if(bondDetail == null){
			bondDetail = new ArrayList<ScLoa>();
	}
		return bondDetail;
	}

	public void setBondDetail(List<ScLoa> bondDetail) {
		this.bondDetail = bondDetail;
	}

	public boolean isExtendContract() {
		return extendContract;
	}

	public void setExtendContract(boolean extendContract) {
		this.extendContract = extendContract;
	}

	public boolean isNotificationSetting() {
		return notificationSetting;
	}

	public void setNotificationSetting(boolean notificationSetting) {
		this.notificationSetting = notificationSetting;
	}
	
	// Start - 28479
	public boolean isErr100033() {
		return isErr100033;
	}
	public void setErr100033(boolean isErr100033) {
		this.isErr100033 = isErr100033;
	}
	public boolean isErr100034() {
		return isErr100034;
	}
	public void setErr100034(boolean isErr100034) {
		this.isErr100034 = isErr100034;
	}
	public boolean isMinQtyCheck() {
		return minQtyCheck;
	}
	public void setMinQtyCheck(boolean minQtyCheck) {
		this.minQtyCheck = minQtyCheck;
	}

	//#15851 - start
	public boolean isHasAmendmentLoaNo() {
		return hasAmendmentLoaNo;
	}

	public void setHasAmendmentLoaNo(boolean hasAmendmentLoaNo) {
		this.hasAmendmentLoaNo = hasAmendmentLoaNo;
	}
	//#15851 - end
	
	// End - 28479
	
	/* #28854 start*/
	public String getNewOfferSpec() {
		return newOfferSpec;
	}

	public void setNewOfferSpec(String newOfferSpec) {
		this.newOfferSpec = newOfferSpec;
	}

	public String getSupplierOffer() {
		return supplierOffer;
	}

	public void setSupplierOffer(String supplierOffer) {
		this.supplierOffer = supplierOffer;
	}	

	public List<String> getItemSpecification() {
		return itemSpecification;
	}

	public void setItemSpecification(List<String> itemSpecification) {
		this.itemSpecification = itemSpecification;
	}

	public List<ScRequestItemSpecOffer> getScRequestItemSpecOfferList() {
		return scRequestItemSpecOfferList;
	}
	public void setScRequestItemSpecOfferList(List<ScRequestItemSpecOffer> scRequestItemSpecOfferList) {
		this.scRequestItemSpecOfferList = scRequestItemSpecOfferList;
	}	
	/* #28854 end*/
	
	//#13847 start
	public boolean isDisableDeferment() {
		return disableDeferment;
	}

	public void setDisableDeferment(boolean disableDeferment) {
		this.disableDeferment = disableDeferment;
	}

	public CtBondWaiverDetail getBondWaiverLatestTemp() {
		return bondWaiverLatestTemp;
	}

	public void setBondWaiverLatestTemp(CtBondWaiverDetail bondWaiverLatestTemp) {
		this.bondWaiverLatestTemp = bondWaiverLatestTemp;
	}
	//#13847 end

	//#29983 start
	/**
	 * @return the businessRegNoNew
	 */
	public String getBusinessRegNoNew() {
		return businessRegNoNew;
	}

	/**
	 * @param businessRegNoNew the businessRegNoNew to set
	 */
	public void setBusinessRegNoNew(String businessRegNoNew) {
		this.businessRegNoNew = businessRegNoNew;
	}
	//#29983 end

	public boolean isDisableOriginalBond() {
		return disableOriginalBond;
	}

	public void setDisableOriginalBond(boolean disableOriginalBond) {
		this.disableOriginalBond = disableOriginalBond;
	}

    // Start Fix - #31740
	/**
	 * Populate only changes related to agency
	 */
	public Map<Long, CtAgency> getSelectedAgencyChanges() {
		if(selectedAgencyChanges==null) {
			selectedAgencyChanges = new HashMap<Long, CtAgency>();
		}
		return selectedAgencyChanges;
	}

	public void setSelectedAgencyChanges(Map<Long, CtAgency> selectedAgencyChanges) {
		this.selectedAgencyChanges = selectedAgencyChanges;
	}
	

	public Map<Long, CtAgency> getAgencyDataDeletion() {
		if(agencyDataDeletion==null) {
			agencyDataDeletion = new HashMap<Long, CtAgency>();
		}
		return agencyDataDeletion;
	}

	public void setAgencyDataDeletion(Map<Long, CtAgency> agencyDataDeletion) {
		this.agencyDataDeletion = agencyDataDeletion;
	}

	//Enhancement 15851 - shaqimi start
	public boolean isFromViewPage() {
		return fromViewPage;
	}

	public void setFromViewPage(boolean fromViewPage) {
		this.fromViewPage = fromViewPage;
	}

	public boolean isAmendFromViewPage() {
		return amendFromViewPage;
	}

	public void setAmendFromViewPage(boolean amendFromViewPage) {
		this.amendFromViewPage = amendFromViewPage;
	}

	public boolean isViewSCL() {
		return viewSCL;
	}

	public void setViewSCL(boolean viewSCL) {
		this.viewSCL = viewSCL;
	}
	//Enhancement 15851 - shaqimi end
	// End Fix - #31740

	// Start Fix - #33674
	public String getBankInfoDetails() {
		return bankInfoDetails;
	}

	public void setBankInfoDetails(String bankInfoDetails) {
		this.bankInfoDetails = bankInfoDetails;
	}

	public Map<String, SmSupplierBank> getSupplierBankAccNo() {
		if (supplierBankAccNo == null) {
			supplierBankAccNo = new HashMap<String, SmSupplierBank>();
		}
		return supplierBankAccNo;
	}

	public void setSupplierBankAccNo(Map<String, SmSupplierBank> supplierBankAccNo) {
		this.supplierBankAccNo = supplierBankAccNo;
	}

	public Map<String, SmSupplierBank> getSupplierBankName() {
		if (supplierBankName == null) {
			supplierBankName = new HashMap<String, SmSupplierBank>();
		}
		return supplierBankName;
	}

	public void setSupplierBankName(Map<String, SmSupplierBank> supplierBankName) {
		this.supplierBankName = supplierBankName;
	}

	public Long getApplID() {
		return applID;
	}

	public void setApplID(Long applID) {
		this.applID = applID;
	}
    //End Fix - #33674
	//Bug 34142 - shaqimi start 16112022
	public boolean isViewSCLFromContract() {
		return viewSCLFromContract;
	}

	public void setViewSCLFromContract(boolean viewSCLFromContract) {
		this.viewSCLFromContract = viewSCLFromContract;
	}
	//Bug 34142 - shaqimi end 16112022
	//Bug 34142, 15851 - shaqimi start 04/12/2022
	public String[] getOutcomesFromContract() {
		return outcomesFromContract;
	}

	public void setOutcomesFromContract(String[] outcomesFromContract) {
		this.outcomesFromContract = outcomesFromContract;
	}
	//Bug 34142, 15851 - shaqimi end 04/12/2022
	//Bug 34142, 15851 - shaqimi start 04/12/2022
	public boolean isAmendFromTaskList() {
		return amendFromTaskList;
	}

	public void setAmendFromTaskList(boolean amendFromTaskList) {
		this.amendFromTaskList = amendFromTaskList;
	}
	
	public boolean isBackFromSCL() {
		return backFromSCL;
	}

	public void setBackFromSCL(boolean backFromSCL) {
		this.backFromSCL = backFromSCL;
	}

	public String getDocTypeFromTaskList() {
		return docTypeFromTaskList;
	}

	public void setDocTypeFromTaskList(String docTypeFromTaskList) {
		this.docTypeFromTaskList = docTypeFromTaskList;
	}

	public String getTaskNameFromTaskList() {
		return taskNameFromTaskList;
	}

	public void setTaskNameFromTaskList(String taskNameFromTaskList) {
		this.taskNameFromTaskList = taskNameFromTaskList;
	}

	public Long getContractIdFromTaskList() {
		return contractIdFromTaskList;
	}

	public void setContractIdFromTaskList(Long contractIdFromTaskList) {
		this.contractIdFromTaskList = contractIdFromTaskList;
	}

	public Long getContractVerIdFromTaskList() {
		return contractVerIdFromTaskList;
	}

	public void setContractVerIdFromTaskList(Long contractVerIdFromTaskList) {
		this.contractVerIdFromTaskList = contractVerIdFromTaskList;
	}
	
	public boolean isViewContractFromAgreement() {
		return viewContractFromAgreement;
	}

	public void setViewContractFromAgreement(boolean viewContractFromAgreement) {
		this.viewContractFromAgreement = viewContractFromAgreement;
	}

	public String getTaskIdFromTaskList() {
		return taskIdFromTaskList;
	}

	public void setTaskIdFromTaskList(String taskIdFromTaskList) {
		this.taskIdFromTaskList = taskIdFromTaskList;
	}

	public String getDocTypeFromAgreementTaskList() {
		return docTypeFromAgreementTaskList;
	}

	public void setDocTypeFromAgreementTaskList(String docTypeFromAgreementTaskList) {
		this.docTypeFromAgreementTaskList = docTypeFromAgreementTaskList;
	}

	public Long getContractIdFromAgreementTaskList() {
		return contractIdFromAgreementTaskList;
	}

	public void setContractIdFromAgreementTaskList(Long contractIdFromAgreementTaskList) {
		this.contractIdFromAgreementTaskList = contractIdFromAgreementTaskList;
	}

	public Long getDocIdFromAgreementTaskList() {
		return docIdFromAgreementTaskList;
	}

	public void setDocIdFromAgreementTaskList(Long docIdFromAgreementTaskList) {
		this.docIdFromAgreementTaskList = docIdFromAgreementTaskList;
	}

	public Long getAgreementIdFromAgreementTaskList() {
		return agreementIdFromAgreementTaskList;
	}

	public void setAgreementIdFromAgreementTaskList(Long agreementIdFromAgreementTaskList) {
		this.agreementIdFromAgreementTaskList = agreementIdFromAgreementTaskList;
	}

	public String getTaskIdFromAgreementTaskList() {
		return taskIdFromAgreementTaskList;
	}

	public void setTaskIdFromAgreementTaskList(String taskIdFromAgreementTaskList) {
		this.taskIdFromAgreementTaskList = taskIdFromAgreementTaskList;
	}

	public Long getContractVerIdFromAgreementTaskList() {
		return contractVerIdFromAgreementTaskList;
	}

	public void setContractVerIdFromAgreementTaskList(Long contractVerIdFromAgreementTaskList) {
		this.contractVerIdFromAgreementTaskList = contractVerIdFromAgreementTaskList;
	}

	public boolean isBackFromContract() {
		return backFromContract;
	}

	public void setBackFromContract(boolean backFromContract) {
		this.backFromContract = backFromContract;
	}

	public Long getStatusIdFromTaskList() {
		return statusIdFromTaskList;
	}

	public void setStatusIdFromTaskList(Long statusIdFromTaskList) {
		this.statusIdFromTaskList = statusIdFromTaskList;
	}

	public boolean isInPage() {
		return inPage;
	}

	public void setInPage(boolean inPage) {
		this.inPage = inPage;
	}
	//Bug 34142, 15851 - shaqimi end 04/12/2022
	
	// #33126 hafiz start

	public boolean isInCtPerformance() {
		return inCtPerformance;
	}

	public void setInCtPerformance(boolean inCtPerformance) {
		this.inCtPerformance = inCtPerformance;
	}

	public boolean isFromUpdateButton() {
		return fromUpdateButton;
	}

	public void setFromUpdateButton(boolean fromUpdateButton) {
		this.fromUpdateButton = fromUpdateButton;
	}

	public IPagingDataModel<CtContractItem> getServiceListingPagingDataModel() {
		return ServiceListingPagingDataModel;
	}

	public void setServiceListingPagingDataModel(IPagingDataModel<CtContractItem> serviceListingPagingDataModel) {
		ServiceListingPagingDataModel = serviceListingPagingDataModel;
	}

	/**
	 * @return the circularListingPagingDataModel
	 */
	public IPagingDataModel<CtContractItem> getContractListingPagingDataModel() {
		return contractListingPagingDataModel;
	}

	/**
	 * @param circularListingPagingDataModel
	 *            the circularListingPagingDataModel to set
	 */
	public void setContractListingPagingDataModel(IPagingDataModel<CtContractItem> circularListingPagingDataModel) {
		this.contractListingPagingDataModel = circularListingPagingDataModel;
	}

	/**
	 * @param circularListingPagingDataModel
	 *            the circularListingPagingDataModel to reset
	 */
	public void resetContractListingPagingDataModel() {
		this.contractListingPagingDataModel = new ContractListingPagingDataModel();
	}

	/**
	 * @param serviceListingPagingDataModel
	 *            the serviceListingPagingDataModel to reset
	 */
	public void resetServiceListingPagingDataModel() {
		this.ServiceListingPagingDataModel = new ServiceListingPagingDataModel();
	}

	public PmUom getSelectedSearchItem() {
		if (selectedSearchItem == null)
			selectedSearchItem = new PmUom();
		return selectedSearchItem;
	}

	public void setSelectedSearchItem(PmUom selectedSearchItem) {
		this.selectedSearchItem = selectedSearchItem;
	}

	/**
	 * @return the itemProductExist
	 */
	public boolean isItemProductExist() {
		return itemProductExist;
	}

	/**
	 * @param itemProductExist
	 *            the itemProductExist to set
	 */
	public void setItemProductExist(boolean itemProductExist) {
		this.itemProductExist = itemProductExist;
	}

	/**
	 * @return the itemServiceExist
	 */
	public boolean isItemServiceExist() {
		return itemServiceExist;
	}

	/**
	 * @param itemServiceExist
	 *            the itemServiceExist to set
	 */
	public void setItemServiceExist(boolean itemServiceExist) {
		this.itemServiceExist = itemServiceExist;
	}

	public boolean isItemProductNotExist() {
		return itemProductNotExist;
	}

	public void setItemProductNotExist(boolean itemProductNotExist) {
		this.itemProductNotExist = itemProductNotExist;
	}

	public boolean isItemServiceNotExist() {
		return itemServiceNotExist;
	}

	public void setItemServiceNotExist(boolean itemServiceNotExist) {
		this.itemServiceNotExist = itemServiceNotExist;
	}

	/**
	 * Pagination for list items product
	 */

	private class ContractListingPagingDataModel extends PagingDataModel<CtContractItem> {
		private static final long serialVersionUID = 1L;

		private Logger log = EpLogger.getLogger(ContractListingPagingDataModel.class);

		public ContractListingPagingDataModel() {
			super("itemTable", isReadOnly());
		}

		public ContractListingPagingDataModel(String table) {
			super("itemTable", false);
		}

		@Override
		public ISearchResultBean<CtContractItem> pageChangeListener(ISearchBean<CtContractItem> searchBean) {
			try {
				// log.info("*****ContractListingPagingDataModel******"+"servicecollapse--->"+servicecollapse+"prodcollapse"+prodcollapse);
				// log.info("*****ServiceListingPagingDataModel******"+"isServicecollapse--->"
				// +isServicecollapse()+"isProdcollapse" +isProdcollapse());
				// private String contractId = null;
				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO ctReqDTO = new ContractReqDTO();

				ContractBackingBean contractBb = new ContractBackingBean();

				Long contId = contractBb.getManagedBean().getContractVer().getCtContract().getContractId();
				Long contractVerId = null;
				contractVerId = contractBb.getManagedBean().getContractVer().getContractVerId();
				// ctReqDTO.setContractId(contId);
				// contractVerId =
				// contractSO.getLatestContractVer(ctReqDTO).getCtContractVer().getContractVerId();

				if (contractVerId == null) {
					ctReqDTO.setContractId(contId);
					contractVerId = contractSO.getLatestContractVer(ctReqDTO).getCtContractVer().getContractVerId();
				}
				if (contId != null) {
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContract.contractId",
							SearchOperator.OPERATOR_EQUALS.value(), contId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (contractVerId != null) {

					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.contractVerId",
							SearchOperator.OPERATOR_EQUALS.value(), contractVerId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getItemname() != null && getItemname().length() > 0) {

					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.itemName",
							SearchOperator.OPERATOR_LIKE.value(), getItemname());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}

				SearchFilterBean filterBean1 = new SearchFilterBean("itemType.parameterCode",
						SearchOperator.OPERATOR_LIKE.value(), ParameterConst.ITEM_TYPE_PRODUCT);
				filterBean1.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean1);

				if (getItemcode() != null && getItemcode().length() > 0) {

					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.cmItem.extensionCode",
							SearchOperator.OPERATOR_LIKE.value(), getItemcode());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getItemstatus() != null && getItemstatus().length() > 0) {

					if (getItemstatus().equals(ParameterConst.RECORD_STATUS_INACTIVE)
							|| getItemstatus().equals(ParameterConst.RECORD_STATUS_ACTIVE)) {

						SearchFilterBean filterBean = new SearchFilterBean("recordStatus",
								SearchOperator.OPERATOR_LIKE.value(), getItemstatus()); // Fix
																						// -
																						// #31042
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
					if (getItemstatus().equals(ParameterConst.RECORD_STATUS_ALL)) {

						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("recordStatusFlg"); // Fix -
																		// #31042
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(
								ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
						// filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}
				// #20997 - Start-Fixing
				if (getSelectedSearchItem().getUomName() != null
						&& getSelectedSearchItem().getUomName().trim().length() > 0) {

					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.uom.uomName",
							SearchOperator.OPERATOR_LIKE.value(), getSelectedSearchItem().getUomName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				// #20997 - End-Fixing

				if (getSkuno() != null && getSkuno().length() > 0) {

					SearchFilterBean filterBean = new SearchFilterBean("sku", SearchOperator.OPERATOR_LIKE.value(),
							getSkuno());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getTypeofgoods() != null && getTypeofgoods().length() > 0) {

					if (getTypeofgoods().equals(ParameterConst.LOCAL_GOODS_ID)
							|| getTypeofgoods().equals(ParameterConst.IMPORTED_GOODS_ID)) {

						SearchFilterBean filterBean = new SearchFilterBean("itemOrigin.parameterId",
								SearchOperator.OPERATOR_EQUALS.value(), getTypeofgoods());
						searchBean.getDataFilterBeans().add(filterBean);

					}
					if (getTypeofgoods().equals(ParameterConst.BOTH_GOODS_ID)) {
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("itemOrigin.parameterId");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.LOCAL_GOODS_ID,
								ParameterConst.IMPORTED_GOODS_ID)));
						// filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);

					}
				}

				// prodcollapse=true;

				ContractItemReqDTO reqDTO = new ContractItemReqDTO();
				reqDTO.setContractItemSearchBean(searchBean);
				ContractItemResDTO resDTO = null;
				// CircularBackingBean cbb=new CircularBackingBean();
				// setResetval(isProdcollapse());

				// log.info("*****ContractListingPagingDataModel Resetval()******"+isResetval());
				// log.info("*****ContractListingPagingDataModel Resetval()******"+cbb.getManagedBean().isResetval());
				// if(isResetval()){
				log.info("*****ContractListingPagingDataModel ParameterCode *** "
						+ contractBb.getManagedBean().getContractVer().getCtContract().getContractType()
								.getParameterCode());
				if (contractBb.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode()
						.equals(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
					if (getItemtype() != null && getItemtype().length() > 0) {
						if (getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT)) {
							setProdcollapse(isResetval());
							// log.info("*****ContractListingPagingDataModel ***Prodcollapse()******"+isProdcollapse());
						}
					} else {
						setProdcollapse(isResetval());
					}
				}

				// if(isProdcollapse()){

				IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);

				resDTO = contractItemSO.searchProductServiceItemDtls(reqDTO);
				// contractBb.getManagedBean().setProdcollapse(true);
				// log.info("*****ContractListingPagingDataModel getContractItemResultBean().getResultList().size()******"+resDTO.getContractItemResultBean().getResultList().size());
				if (resDTO.getContractItemResultBean().getResultList().size() > 0) {
					// contractBb.getManagedBean().setProdcollapse(false);
					readOnly = false;
				}
				return resDTO.getContractItemResultBean();
				// }
				// }
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
			}
			return null;
		}
	}

	/**
	 * Pagination for list items service
	 */

	private class ServiceListingPagingDataModel extends PagingDataModel<CtContractItem> {

		private static final long serialVersionUID = 1L;

		private Logger log = EpLogger.getLogger(ServiceListingPagingDataModel.class);

		public ServiceListingPagingDataModel() {
			super("itemServiceTable", isReadOnly());
		}

		public ServiceListingPagingDataModel(String table) {
			super(table, false);
		}

		@Override
		public ISearchResultBean<CtContractItem> pageChangeListener(ISearchBean<CtContractItem> searchBean) {
			try {
				// log.info("*****ServiceListingPagingDataModel******"+"servicecollapse--->"+servicecollapse+"prodcollapse"+prodcollapse);
				// log.info("*****ServiceListingPagingDataModel******"+"isServicecollapse--->"+isServicecollapse()+"isProdcollapse"+isProdcollapse());
				// private String contractId = null;
				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO ctReqDTO = new ContractReqDTO();

				ContractBackingBean contractBb = new ContractBackingBean();

				Long contId = contractBb.getManagedBean().getContractVer().getCtContract().getContractId();
				Long contractVerId = null;
				contractVerId = contractBb.getManagedBean().getContractVer().getContractVerId();
				log.info("**SuspensionProductListingPagingDataModel******ContractID **--> : " + contId);
				log.info("**SuspensionProductListingPagingDataModel******ContractVerID **--> : " + contractVerId);

				if (contractVerId == null) {
					ctReqDTO.setContractId(contId);
					contractVerId = contractSO.getLatestContractVer(ctReqDTO).getCtContractVer().getContractVerId();
				}
				// if(contractBb.getManagedBean().getContractVer().getCtContract().getCurrentContractVer().getContractVerId()
				// .equals(contractBb.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId())){
				// contractVerId=contractBb.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId();
				// }else {
				// contractVerId=contractBb.getManagedBean().getContractVer().getCtContract().getCurrentContractVer().getContractVerId();
				// }

				if (contId != null) {

					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContract.contractId",
							SearchOperator.OPERATOR_EQUALS.value(), contId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (contractVerId != null) {

					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.contractVerId",
							SearchOperator.OPERATOR_EQUALS.value(), contractVerId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getItemname() != null && getItemname().length() > 0) {

					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.itemName",
							SearchOperator.OPERATOR_LIKE.value(), getItemname());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}

				SearchFilterBean filterBean1 = new SearchFilterBean("itemType.parameterCode",
						SearchOperator.OPERATOR_LIKE.value(), ParameterConst.ITEM_TYPE_SERVICE);
				filterBean1.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean1);

				if (getItemcode() != null && getItemcode().length() > 0) {

					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.cmItem.extensionCode",
							SearchOperator.OPERATOR_LIKE.value(), getItemcode());
					filterBean.setCaseInsensitive(true); // scRequestItem.itemType.parameterId
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getItemstatus() != null && getItemstatus().length() > 0) {

					if (getItemstatus().equals(ParameterConst.RECORD_STATUS_INACTIVE)
							|| getItemstatus().equals(ParameterConst.RECORD_STATUS_ACTIVE)) {
						SearchFilterBean filterBean = new SearchFilterBean("recordStatus",
								SearchOperator.OPERATOR_LIKE.value(), getItemstatus()); // #
																						// Fix
																						// -
																						// #31042
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
					if (getItemstatus().equals(ParameterConst.RECORD_STATUS_ALL)) {
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("recordStatusFlg"); // # Fix -
																		// #31042
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(
								ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
						// filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}
				// #20997 - Start-Fixing
				if (getSelectedSearchItem().getUomName() != null
						&& getSelectedSearchItem().getUomName().trim().length() > 0) {

					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.uom.uomName",
							SearchOperator.OPERATOR_LIKE.value(), getSelectedSearchItem().getUomName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				// #20997 - End-Fixing
				if (getSkuno() != null && getSkuno().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("sku", SearchOperator.OPERATOR_LIKE.value(),
							getSkuno());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getTypeofgoods() != null && getTypeofgoods().length() > 0) {

					if (getTypeofgoods().equals(ParameterConst.LOCAL_GOODS_ID)
							|| getTypeofgoods().equals(ParameterConst.IMPORTED_GOODS_ID)) {
						SearchFilterBean filterBean = new SearchFilterBean("itemOrigin.parameterId",
								SearchOperator.OPERATOR_EQUALS.value(), getTypeofgoods());
						searchBean.getDataFilterBeans().add(filterBean);

					}
					if (getTypeofgoods().equals(ParameterConst.BOTH_GOODS_ID)) {
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("itemOrigin.parameterId");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.LOCAL_GOODS_ID,
								ParameterConst.IMPORTED_GOODS_ID)));
						// filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);

					}
				}

				// servicecollapse=true;
				ContractItemReqDTO reqDTO = new ContractItemReqDTO();
				reqDTO.setContractItemSearchBean(searchBean);
				ContractItemResDTO resDTO = null;
				// CircularBackingBean cbb=new CircularBackingBean();
				// log.info("*****ServiceListingPagingDataModel Resetval()******"+isResetval());
				// log.info("*****ServiceListingPagingDataModel Resetval()******"+cbb.getManagedBean().isResetval());
				if (isResetval()) {
					// log.info("*****ServiceListingPagingDataModel ParameterCode *** "+contractBb.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode());
					if (contractBb.getManagedBean().getContractVer().getCtContract().getContractType()
							.getParameterCode().equals(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
						if (getItemtype() != null && getItemtype().length() > 0) {
							if (getItemtype().equals(ParameterConst.ITEM_TYPE_SERVICE)) {
								setServicecollapse(isResetval());
								// log.info("*****ServiceListingPagingDataModel *** isServicecollapse()******"+isServicecollapse());
							}
						} else {
							setServicecollapse(isResetval());
						}
					}

					// if(isServicecollapse()){
					IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);

					resDTO = contractItemSO.searchProductServiceItemDtls(reqDTO);
					resDTO.getContractItemResultBean();
					// contractBb.getManagedBean().setServicecollapse(true);
					// log.info("*****ServiceListingPagingDataModel getContractItemResultBean().getResultList().size()******"+resDTO.getContractItemResultBean().getResultList().size());
					if (resDTO.getContractItemResultBean().getResultList().size() > 0) {
						// servicecollapse=false;
						// contractBb.getManagedBean().setServicecollapse(false);
						readOnly = false;
						// contractBb.getManagedBean().setReadOnly(false);contractBb.getManagedBean().isReadOnly()

					}

					return resDTO.getContractItemResultBean();
					// }
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
			}
			return null;
		}

	}
	
	// #33126 hafiz end
}
