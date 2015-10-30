/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.VCtContract;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DualListModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;

/**
 * Managed Bean For Contract Details
 * 
 * @author Alam
 * @since 23/9/2013	 
 */
@ManagedBean(name ="contractDetailsManagedBean")
@SessionScoped
public class ContractDetailsManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private String[] action;
	private String taskName;
	private String itemType;
	private String showItem;
	private String popupForImportedItems = "L";
	private String searchContractNo;
	private String searchContractName;
	private Long searchMinistryProfileId;
	private PmOrgProfile searchPtjProfile;
	private String searchCircularNo;
	private String searchCircularName;
    private Date searchCtStartDate;
	private Date searchCtEndDate;
	private String searchSupplierName;
	private String searchLoaNo;
	private String searchLoaPhysicalNo;
	private String searchCtPhysicalNo;
	private int searchStatus;
	private Long searchTerminationType;
	private Long searchFulfilmentStatus;
	private int searchContractStatus;
	//private String supplierAddress;
	//private String supplierCompanyName;
	//private String supplierBranchName;
	private String externalPageUrl;
	private String ptjCodeName;
	private String ptjCodeNameSearchValues;
	private String frequencySchedule;
	private String warningPercentage;
	private String textBeforeSign;
	private String buttonAction;	
	private String gotoPreviewPage;	
	private String financialAccNo;
	private String fileDirectory;
	private Long ministryProfileId;
	private Long jabatanProfileId;
	private Long loaContractVerId;
    private BigDecimal paidAdvancePaymentAmt;
    private BigDecimal paidInvoiceAmt;
    private BigDecimal gstTotalAmount;
	private Date asAtDate = null;
	private int selectedTabIndex = 0;
	private int attachFileIndex;
    private int firstRowSelected;
    private double advancePaymentAmt;
    private double performanceBondRuleAmt;	
	private boolean selectAll;
	private boolean directPurchase;
	private boolean chkProgressive;
	private boolean chkSchedule;
	private boolean disableScheduleRadio;
	//private boolean updateSupplierAddress;
	//private boolean updateCompanyName;
	private boolean updateContract = false;
	private boolean viewContract = false;
	private boolean chkJustification;
	private boolean flgPaymentTracking;
	private boolean chkConceValue;
	private boolean progSchedButtonDeactive;
	private boolean autoUpdate = true;
	private boolean buttonHideActive = true;
	private boolean disableGstAmount;
	private List<CtContractItem> ctItemList;
	private List<CtContractItem> ctItemServiceList;
	private List<CtContractZoneDtl> ctContractZoneDtls;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<String> finalListOfSideMenu;
	private List<String> yearList;
	private List<CtAgencyAddress> selectedListOfAddress;
	private List<PmAddressType> addressTypeList;
	private SelectItem[] loaContractVerList;
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUserGroup> userGroupMap;
	private Map<Long, PmUser> userMap;
	private Map<String,Object> radioButtonSchedule;
	private HashMap<String, PmUserGroupUser> approverMap;
	private CtContractVer contractVer;
	private CtContractVer agreementContractVer;
	private CtContract ctContract;
	private CtCircularVer ctCircularVer;
	private CtContractItem selectedItem;
	private CtContractItem selectedServiceItem;
	private CtDeduction selectedDeduction;
	private CtPerformanceBond ctPerformanceBond;
	private CtContractZone selectedCtZone;
	private CtAgency selectedAgency;
	private CtContractAmount ctContractAmount;	
	private TreeNode ctZoneTrees;
	private MenuModel fulfilmentMenuModel;	
	//private SmAddress smAddress;
	private PmDigiSign pmDigiSign;	
	private IPagingDataModel<VCtContract> contractDataModel = new ContractPagingDataModel();
	
	public int deductionIndex =99;
	public int scheduleIndex =99;
	public int factoring = 99;
	public int paymentTracking = 99;
	public int itemIndex = 99;
	public int settingIndex = 101;
	private int index;
	
	
	/**
	 * @return the action
	 */
	public String[] getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String[] action) {
		this.action = action;
	}
	
	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
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
	 * @return the popupForImportedItems
	 */
	public String getPopupForImportedItems() {
		return popupForImportedItems;
	}

	/**
	 * @param popupForImportedItems the popupForImportedItems to set
	 */
	public void setPopupForImportedItems(String popupForImportedItems) {
		this.popupForImportedItems = popupForImportedItems;
	}

	/**
	 * @return the searchContractName
	 */
	public String getSearchContractName() {
		return searchContractName;
	}

	/**
	 * @param searchContractName the searchContractName to set
	 */
	public void setSearchContractName(String searchContractName) {
		this.searchContractName = searchContractName;
	}

	/**
	 * @return the searchLoaPhysicalNo
	 */
	public String getSearchLoaPhysicalNo() {
		
		return searchLoaPhysicalNo;
	}
	
	/**
	 * @param searchLoaPhysicalNo the searchLoaPhysicalNo to set
	 */
	public void setSearchLoaPhysicalNo(String searchLoaPhysicalNo) {
		this.searchLoaPhysicalNo = searchLoaPhysicalNo;
	}

	/**
	 * @return the searchContractNo
	 */
	public String getSearchContractNo() {
		return searchContractNo;
	}

	/**
	 * @param searchContractNo the searchContractNo to set
	 */
	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}
	
	/**
	 * @return the searchCircularNo
	 */
	public String getSearchCircularNo() {
		return searchCircularNo;
	}

	/**
	 * @param searchCircularNo the searchCircularNo to set
	 */
	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}

	/**
	 * @return the searchLoaNo
	 */
	public String getSearchLoaNo() {
		return searchLoaNo;
	}

	/**
	 * @param searchLoaNo the searchLoaNo to set
	 */
	public void setSearchLoaNo(String searchLoaNo) {
		this.searchLoaNo = searchLoaNo;
	}

	/**
	 * @return the searchCtPhysicalNo
	 */
	public String getSearchCtPhysicalNo() {
		
		return searchCtPhysicalNo;
	}

	/**
	 * @param searchCtPhysicalNo the searchCtPhysicalNo to set
	 */
	public void setSearchCtPhysicalNo(String searchCtPhysicalNo) {
		this.searchCtPhysicalNo = searchCtPhysicalNo;
	}

	/**
	 * @return the searchCircularName
	 */
	public String getSearchCircularName() {
		return searchCircularName;
	}

	/**
	 * @param searchCircularName the searchCircularName to set
	 */
	public void setSearchCircularName(String searchCircularName) {
		this.searchCircularName = searchCircularName;
	}

	/**
	 * @return the searchSupplierName
	 */
	public String getSearchSupplierName() {
		return searchSupplierName;
	}

	/**
	 * @param searchSupplierName the searchSupplierName to set
	 */
	public void setSearchSupplierName(String searchSupplierName) {
		this.searchSupplierName = searchSupplierName;
	}

	/**
	 * @return the supplierAddress
	 *//*
	public String getSupplierAddress() {
		return supplierAddress;
	}

	*//**
	 * @param supplierAddress the supplierAddress to set
	 *//*
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}*/
	

	/**
	 * @return the supplierBranchName
	 *//*
	public String getSupplierBranchName() {
		return supplierBranchName;
	}
*/
	/**
	 * @param supplierBranchName the supplierBranchName to set
	 *//*
	public void setSupplierBranchName(String supplierBranchName) {
		this.supplierBranchName = supplierBranchName;
	}
	*/
	/**
	 * @return the supplierCompanyName
	 *//*
	public String getSupplierCompanyName() {
		return supplierCompanyName;
	}	

	*//**
	 * @param supplierCompanyName the supplierCompanyName to set
	 *//*
	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}
*/
	/**
	 * @return the externalPageUrl
	 */
	public String getExternalPageUrl() {
		return externalPageUrl;
	}

	/**
	 * @param externalPageUrl the externalPageUrl to set
	 */
	public void setExternalPageUrl(String externalPageUrl) {
		this.externalPageUrl = externalPageUrl;
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
	 * @return the warningPercentage
	 */
	public String getWarningPercentage() {
		return warningPercentage;
	}

	/**
	 * @param warningPercentage the warningPercentage to set
	 */
	public void setWarningPercentage(String warningPercentage) {
		this.warningPercentage = warningPercentage;
	}

	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * @return the buttonAction
	 */
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	/**
	 * @return the gotoPreviewPage
	 */
	public String getGotoPreviewPage() {
		return gotoPreviewPage;
	}

	/**
	 * @param gotoPreviewPage the gotoPreviewPage to set
	 */
	public void setGotoPreviewPage(String gotoPreviewPage) {
		this.gotoPreviewPage = gotoPreviewPage;
	}

	/**
	 * @return the financialAccNo
	 */
	public String getFinancialAccNo() {
		return financialAccNo;
	}

	/**
	 * @param financialAccNo the financialAccNo to set
	 */
	public void setFinancialAccNo(String financialAccNo) {
		this.financialAccNo = financialAccNo;
	}

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
	 * @return the searchFulfilmentStatus
	 */
	public Long getSearchFulfilmentStatus() {
		return searchFulfilmentStatus;
	}

	/**
	 * @param searchFulfilmentStatus the searchFulfilmentStatus to set
	 */
	public void setSearchFulfilmentStatus(Long searchFulfilmentStatus) {
		this.searchFulfilmentStatus = searchFulfilmentStatus;
	}

	/**
	 * @return the searchContractStatus
	 */
	public int getSearchContractStatus() {
		return searchContractStatus;
	}

	/**
	 * @param searchContractStatus the searchContractStatus to set
	 */
	public void setSearchContractStatus(int searchContractStatus) {
		this.searchContractStatus = searchContractStatus;
	}

	/**
	 * @return the searchTerminationType
	 */
	public Long getSearchTerminationType() {
		return searchTerminationType;
	}

	/**
	 * @param searchTerminationType the searchTerminationType to set
	 */
	public void setSearchTerminationType(Long searchTerminationType) {
		this.searchTerminationType = searchTerminationType;
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
	 * @return the loaContractVerId
	 */
	public Long getLoaContractVerId() {
		return loaContractVerId;
	}

	/**
	 * @param loaContractVerId the loaContractVerId to set
	 */
	public void setLoaContractVerId(Long loaContractVerId) {
		this.loaContractVerId = loaContractVerId;
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
	 * @return the searchCtStartDate
	 */
	public Date getSearchCtStartDate() {
		return searchCtStartDate;
	}

	/**
	 * @param searchCtStartDate the searchCtStartDate to set
	 */
	public void setSearchCtStartDate(Date searchCtStartDate) {
		this.searchCtStartDate = searchCtStartDate;
	}

	/**
	 * @return the searchCtEndDate
	 */
	public Date getSearchCtEndDate() {
		return searchCtEndDate;
	}

	/**
	 * @param searchCtEndDate the searchCtEndDate to set
	 */
	public void setSearchCtEndDate(Date searchCtEndDate) {
		this.searchCtEndDate = searchCtEndDate;
	}	

	/**
	 * @return the asAtDate
	 */
	public Date getAsAtDate() {
		return asAtDate;
	}

	/**
	 * @param asAtDate the asAtDate to set
	 */
	public void setAsAtDate(Date asAtDate) {
		this.asAtDate = asAtDate;
	}

	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * @return the searchStatus
	 */
	public int getSearchStatus() {
		return searchStatus;
	}

	/**
	 * @param searchStatus the searchStatus to set
	 */
	public void setSearchStatus(int searchStatus) {
		this.searchStatus = searchStatus;
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
	 * @return the firstRowSelected
	 */
	public int getFirstRowSelected() {
		return firstRowSelected;
	}

	/**
	 * @param firstRowSelected the firstRowSelected to set
	 */
	public void setFirstRowSelected(int firstRowSelected) {
		this.firstRowSelected = firstRowSelected;
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
	 * @return the performanceBondRuleAmt
	 */
	public double getPerformanceBondRuleAmt() {
		return performanceBondRuleAmt;
	}

	/**
	 * @param performanceBondRuleAmt the performanceBondRuleAmt to set
	 */
	public void setPerformanceBondRuleAmt(double performanceBondRuleAmt) {
		this.performanceBondRuleAmt = performanceBondRuleAmt;
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
	 * @return the updateSupplierAddress
	 *//*
	public boolean isUpdateSupplierAddress() {
		return updateSupplierAddress;
	}

	*//**
	 * @param updateSupplierAddress the updateSupplierAddress to set
	 *//*
	public void setUpdateSupplierAddress(boolean updateSupplierAddress) {
		this.updateSupplierAddress = updateSupplierAddress;
	}
*/
	/**
	 * @return the updateCompanyName
	 *//*
	public boolean isUpdateCompanyName() {
		return updateCompanyName;
	}

	*//**
	 * @param updateCompanyName the updateCompanyName to set
	 *//*
	public void setUpdateCompanyName(boolean updateCompanyName) {
		this.updateCompanyName = updateCompanyName;
	}
*/
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
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the buttonHideActive
	 */
	public boolean isButtonHideActive() {
		return buttonHideActive;
	}

	/**
	 * @param buttonHideActive the buttonHideActive to set
	 */
	public void setButtonHideActive(boolean buttonHideActive) {
		this.buttonHideActive = buttonHideActive;
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
	 * @return the ctContractZoneDtls
	 */
	public List<CtContractZoneDtl> getCtContractZoneDtls() {
		return ctContractZoneDtls;
	}

	/**
	 * @param ctContractZoneDtls the ctContractZoneDtls to set
	 */
	public void setCtContractZoneDtls(List<CtContractZoneDtl> ctContractZoneDtls) {
		this.ctContractZoneDtls = ctContractZoneDtls;
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
		if(attachmentList == null) {
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
	 * @return the finalListOfSideMenu
	 */
	public List<String> getFinalListOfSideMenu() {
		return finalListOfSideMenu;
	}

	/**
	 * @param finalListOfSideMenu the finalListOfSideMenu to set
	 */
	public void setFinalListOfSideMenu(List<String> finalListOfSideMenu) {
		this.finalListOfSideMenu = finalListOfSideMenu;
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
	 * @return the loaContractVerList
	 */
	public SelectItem[] getLoaContractVerList() {
		return loaContractVerList;
	}

	/**
	 * @param loaContractVerList the loaContractVerList to set
	 */
	public void setLoaContractVerList(SelectItem[] loaContractVerList) {
		this.loaContractVerList = loaContractVerList;
	}

	/**
	 * @return the approverModel
	 */
	public DualListModel<PmUser> getApproverModel() {
		return approverModel;
	}

	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<PmUser> approverModel) {
		this.approverModel = approverModel;
	}

	/**
	 * @return the userGroupMap
	 */
	public Map<Long, PmUserGroup> getUserGroupMap() {
		return userGroupMap;
	}

	/**
	 * @param userGroupMap the userGroupMap to set
	 */
	public void setUserGroupMap(Map<Long, PmUserGroup> userGroupMap) {
		this.userGroupMap = userGroupMap;
	}

	/**
	 * @return the userMap
	 */
	public Map<Long, PmUser> getUserMap() {
		if (userMap == null) {
			userMap = new HashMap<Long, PmUser>();
		}
		return userMap;
	}

	/**
	 * @param userMap the userMap to set
	 */
	public void setUserMap(Map<Long, PmUser> userMap) {
		this.userMap = userMap;
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
	 * @return the approverMap
	 */
	public HashMap<String, PmUserGroupUser> getApproverMap() {
		if (approverMap == null) {
			setApproverMap(new HashMap<String,PmUserGroupUser>());
		}
		return approverMap;
	}

	/**
	 * @param approverMap the approverMap to set
	 */
	public void setApproverMap(HashMap<String, PmUserGroupUser> approverMap) {
		this.approverMap = approverMap;
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
	 * @return the ctContract
	 */
	public CtContract getCtContract() {
		return ctContract;
	}

	/**
	 * @param ctContract the ctContract to set
	 */
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
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
	 * @return the selectedCtZone
	 */
	public CtContractZone getSelectedCtZone() {
		return selectedCtZone;
	}

	/**
	 * @param selectedCtZone the selectedCtZone to set
	 */
	public void setSelectedCtZone(CtContractZone selectedCtZone) {
		this.selectedCtZone = selectedCtZone;
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
	 * @return the deductionIndex
	 */
	public int getDeductionIndex() {
		return deductionIndex;
	}

	/**
	 * @param deductionIndex the deductionIndex to set
	 */
	public void setDeductionIndex(int deductionIndex) {
		this.deductionIndex = deductionIndex;
	}

	/**
	 * @return the scheduleIndex
	 */
	public int getScheduleIndex() {
		return scheduleIndex;
	}

	/**
	 * @param scheduleIndex the scheduleIndex to set
	 */
	public void setScheduleIndex(int scheduleIndex) {
		this.scheduleIndex = scheduleIndex;
	}

	/**
	 * @return the factoring
	 */
	public int getFactoring() {
		return factoring;
	}

	/**
	 * @param factoring the factoring to set
	 */
	public void setFactoring(int factoring) {
		this.factoring = factoring;
	}

	/**
	 * @return the paymentTracking
	 */
	public int getPaymentTracking() {
		return paymentTracking;
	}

	/**
	 * @param paymentTracking the paymentTracking to set
	 */
	public void setPaymentTracking(int paymentTracking) {
		this.paymentTracking = paymentTracking;
	}

	/**
	 * @return the itemIndex
	 */
	public int getItemIndex() {
		return itemIndex;
	}

	/**
	 * @param itemIndex the itemIndex to set
	 */
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}

	/**
	 * @return the settingIndex
	 */
	public int getSettingIndex() {
		return settingIndex;
	}

	/**
	 * @param settingIndex the settingIndex to set
	 */
	public void setSettingIndex(int settingIndex) {
		this.settingIndex = settingIndex;
	}

	/**
	 * @return the fulfilmentMenuModel
	 */
	public MenuModel getFulfilmentMenuModel() {
		return fulfilmentMenuModel;
	}

	/**
	 * @param fulfilmentMenuModel the fulfilmentMenuModel to set
	 */
	public void setFulfilmentMenuModel(MenuModel fulfilmentMenuModel) {
		this.fulfilmentMenuModel = fulfilmentMenuModel;
	}

	/**
	 * @return the smAddress
	 *//*
	public SmAddress getSmAddress() {
		return smAddress;
	}

	*//**
	 * @param smAddress the smAddress to set
	 *//*
	public void setSmAddress(SmAddress smAddress) {
		this.smAddress = smAddress;
	}
	*/
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
	 * @return the pmDigiSign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * @param pmDigiSign the pmDigiSign to set
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}	

	/**
	 * @return the searchMinistryProfile
	 */
	public Long getSearchMinistryProfileId() {
		return searchMinistryProfileId;
	}

	/**
	 * @param searchMinistryProfile the searchMinistryProfileId to set
	 */
	public void setSearchMinistryProfileId(Long searchMinistryProfileId) {
		this.searchMinistryProfileId = searchMinistryProfileId;
	}

	/**
	 * @return the searchPtjProfile
	 */
	public PmOrgProfile getSearchPtjProfile() {
		return searchPtjProfile;
	}

	/**
	 * @param searchPtjProfile the searchPtjProfile to set
	 */
	public void setSearchPtjProfile(PmOrgProfile searchPtjProfile) {
		this.searchPtjProfile = searchPtjProfile;
	}

	/**
	 * @return the contractDataModel
	 */
	public IPagingDataModel<VCtContract> getContractDataModel() {
		return contractDataModel;
	}

	/**
	 * @param contractDataModel the contractDataModel to set
	 */
	public void setContractDataModel(IPagingDataModel<VCtContract> contractDataModel) {
		this.contractDataModel = contractDataModel;
	}

	/**
	 * Pagination for available contract search list
	 */
	private class ContractPagingDataModel extends PagingDataModel<VCtContract> {
		private static final long serialVersionUID = 1L;

		public ContractPagingDataModel() {
			super("contractTbl", false);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<VCtContract> pageChangeListener(ISearchBean<VCtContract> searchBean) {
			IContractSO contractSO = null;
			boolean isSupplierUser = false;
			try {
				FacesScopes fc = new FacesScopes();
				if (fc.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
						|| fc.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
						|| fc.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
						|| fc.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
					// supplier
					isSupplierUser = true;
					SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
					supplierSearchReqDTO.setUserId(fc.getUserSessionBean().getUserId());
					ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);	
					SupplierSearchResDTO supplierSearchResDTO = supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
					
					if (supplierSearchResDTO.getResultBean() != null && supplierSearchResDTO.getResultBean().getResultList() != null 
			        		&& !supplierSearchResDTO.getResultBean().getResultList().isEmpty()) {
						List<Long> supplierIdList = new ArrayList<Long>();
		  		        for (SmSupplier supplier : supplierSearchResDTO.getResultBean().getResultList()) {
		  		        	supplierIdList.add(supplier.getSupplierId());
		  		        }
						searchBean.getDataFilterBeans().add(new SearchFilterBean("SUPPLIER_ID",SearchOperator.OPERATOR_IN.value(), 
								supplierIdList));
					}	
				}
				
				if (StringUtils.isNotBlank(getSearchContractNo())) {
					SearchFilterBean filterBean = new SearchFilterBean("CONTRACT_NO",SearchOperator.OPERATOR_LIKE.value(),
													getSearchContractNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (StringUtils.isNotBlank(getSearchContractName())) {
					SearchFilterBean filterBean = new SearchFilterBean("CONTRACT_NAME",SearchOperator.OPERATOR_LIKE.value(),
													getSearchContractName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (getSearchPtjProfile() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("PTJ_ORG_ID", SearchOperator.OPERATOR_EQUALS.value(),
							getSearchPtjProfile().getOrgProfileId()));
					
				} else if (getSearchMinistryProfileId() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("MINISTRY_ORG_ID", SearchOperator.OPERATOR_EQUALS.value(),
							getSearchMinistryProfileId()));
				}
				if (StringUtils.isNotBlank(getSearchCircularNo())) {
					SearchFilterBean filterBean = new SearchFilterBean("CIRCULAR_NO",SearchOperator.OPERATOR_LIKE.value(),
													getSearchCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (StringUtils.isNotBlank(getSearchCircularName())) {
					SearchFilterBean filterBean = new SearchFilterBean("CIRCULAR_NAME",SearchOperator.OPERATOR_LIKE.value(),
													getSearchCircularName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (StringUtils.isNotBlank(getSearchLoaNo())) {
					SearchFilterBean filterBean = new SearchFilterBean("LOA_NO",SearchOperator.OPERATOR_LIKE.value(),
							getSearchLoaNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (StringUtils.isNotBlank(getSearchSupplierName())) {
					SearchFilterBean filterBean = new SearchFilterBean("COMPANY_NAME",SearchOperator.OPERATOR_LIKE.value(),
													getSearchSupplierName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (getSearchCtStartDate() != null && getSearchCtEndDate() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("EFF_DATE",SearchOperator.OPERATOR_GREATER_EQUAL.value(), getSearchCtStartDate()));	
					searchBean.getDataFilterBeans().add(new SearchFilterBean("EXP_DATE",SearchOperator.OPERATOR_LESS_EQUAL.value(), getSearchCtEndDate()));		
				
				}
				if (getSearchCtStartDate() != null && getSearchCtEndDate() == null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("EFF_DATE",SearchOperator.OPERATOR_EQUALS.value(), getSearchCtStartDate()));					
				}
				if (getSearchCtStartDate() == null && getSearchCtEndDate() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("EXP_DATE",SearchOperator.OPERATOR_EQUALS.value(), getSearchCtEndDate()));	
				}
				if (StringUtils.isNotBlank(getSearchCtPhysicalNo())) {
				   	SearchFilterBean filterBean = new SearchFilterBean("CONTRACT_PHYSICAL_NO",SearchOperator.OPERATOR_LIKE.value(),
				   									getSearchCtPhysicalNo().trim());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);					
				}
				if (StringUtils.isNotBlank(getSearchLoaPhysicalNo())) {
					SearchFilterBean filterBean = new SearchFilterBean("LOA_PHYSICAL_NO",SearchOperator.OPERATOR_LIKE.value(),
													getSearchLoaPhysicalNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);					
				}
				if (getSearchStatus() > 0) {
					if (getSearchStatus() == ContractConst.STATUS_ALL) {
						searchBean.getDataFilterBeans().add(new SearchMultiFilterBean(
								new SearchFilterBean(
										"STATUS",
										SearchOperator.OPERATOR_EQUALS.value(),
										ContractConst.STATUS_COMPLETE),
								new SearchFilterBean(
										"STATUS",
										SearchOperator.OPERATOR_EQUALS.value(), 
										ContractConst.STATUS_INCOMPLETE,
								SearchCondition.CONDITION_OR.value())));
						
					} else {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("STATUS", 
								SearchOperator.OPERATOR_EQUALS.value(), getSearchStatus()));
					}
				}
				if (getSearchTerminationType() != null) {
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterId(getSearchTerminationType());
					reqDTO.setLanguageCode(fc.getUserLocale().getLanguage());
					
					IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
					ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
					String parameterCode = resDTO.getPmParameter().getParameterCode();
					
					if (parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("IS_TERMINATE",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					} else if (parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("IS_MUTUAL_TERMINATE",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					} else if (parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("IS_REMEDY",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					}
				}
				if (getSearchFulfilmentStatus() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("CURRENT_STATUS_ID", 
							SearchOperator.OPERATOR_EQUALS.value(), getSearchFulfilmentStatus()));
				}
				if (getSearchContractStatus() > 0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("CONTRACT_STATUS", 
							SearchOperator.OPERATOR_EQUALS.value(), getSearchContractStatus()));
				}
				searchBean.setUseEntityClass(true);
				
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO reqDTO = new ContractReqDTO();
				
				reqDTO.setFromSupplier(isSupplierUser);
				reqDTO.setvCtContractSearchBean(searchBean);
				ContractResDTO resDTO = contractSO.searchContractDocument(reqDTO);
				setSelectedTabIndex(0);
					
				if (resDTO.isSuccessful()) {
					return resDTO.getvCtContractResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
