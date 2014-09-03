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

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.VCtContract;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

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
public class ContractDetailsManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;

	private String taskName;
	private String[] action;
	private String processName;
	private String itemType;
	private String showItem;
	private String popupForImportedItems ="L";
	private String ctName;
	private String ctLOANo;
	private String ctNumber;
	private String ctCircularNo;
	private String ctSupplierId;
	private String ctPhysicalNo;
	private String ctCircularName;
	private String ctSupplierName;
	private String supplierAddress;
	private	String	zoneName;
	private String supplierCompanyName;
	private String supplierLocation;
	private String contractType;
	private String externalPageUrl;
	private String ptjCodeName;
	private String ptjSearchtype;
	private String ptjCodeNameSearchValues;
	private String frequencySchedule;
	
	private int selectedIndex;
	private int ctContractStatus;
	private Long terminationTypeId;
	private Long ministryProfileId;
	private Long departmentProfileId;
    private double advancePaymentAmt;
    private BigDecimal paidAdvancePaymentAmt;
    private BigDecimal paidInvoiceAmt;
	private int selectedTabIndex =0;
	
	private MenuModel fulfilmentMenuModel;
	
	private Date ctDateStart;
	private Date ctDateEnd;
	
	private boolean bpkApprover=true;
	private boolean serviceItem;
	private boolean productItem = true;
	private boolean itemPricing = true;
	private boolean itemCeilingQuantity;
	private boolean directPurchase;
	private boolean scheduleType;
	private boolean chkProgressive;
	private boolean chkSchedule;
	private boolean chkScheduleNulValue;
	private boolean chkProgressiveNulValue;
	private boolean disableScheduleRadio;
	private boolean disableProgessiveRadio;
	private boolean updateSupplierAddress;
	private boolean updateCompanyName;
	private boolean showZoneDetails;
	private boolean chkButton;
	private boolean updateContract;
	private boolean viewContract = false;
	private boolean validateScheduleDate;
	private boolean chkJustification;
	private boolean flgPaymentTracking;
	private boolean chkConceValue;
	private boolean tabMenuShowForZone;
	private boolean tabMenuShowFactored;

	private CtContractVer contractVer;
	private CtContract ctContract;
	private List<VCtContract> contractList;
	private List<CtAgency> listOfAgency;
	private List<PmOrgProfile> pmOrgProfileList;
	private CtContractItem selectedItem;
	private CtContractItem selectedCCItem;
	private CtContractItem selectedServiceItem;
	private List<CtContractItem> ctItemList;
	private List<CtContractItem> ctItemServiceList;
	private CtDeduction ctDeductionDel;
	private CtDeduction selectedDeduction;
	private List<CtDeductionClause> ctDeductionClauseList;
	private CtSchedule ctSchedulesDel;
	private CtProgressivePayment ctProgressivePaymentDel;
	private DualListModel<String> approverModel;
	private Map<Long, PmUserGroup> userGroupMap;
	private HashMap<String, PmUserGroupUser> approverMap;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private SmAddress smAddress;
	private CtPerformanceBond ctPerformanceBond;
	private CtContractZone selectedCtZone;
	private TreeNode ctZoneTrees ;
	private List<CtContractZoneDtl> ctContractZoneDtls;
	private Map<String, PmUser> userMap;
	private SmSupplierBank smSupplierBanks;
	private Map<String,Object> radioButtonSchedule;
	private List<String> finalListOfSideMenu;
	private String fileDirectory;
	private CtAgency selectedAgency;
	
	public String[] getAction() {
		return action;
	}
	
	public void setAction(String[] action) {
		this.action = action;
	}
	
	public String getTaskName() {
		return taskName;
	}
	
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getProcessName() {
		return processName;
	}
	
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public CtContractVer getContractVer() {
		return contractVer;
	}
	
	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}
	
	public CtDeduction getCtDeductionDel() {
		return ctDeductionDel;
	}
	
	public void setCtDeductionDel(CtDeduction ctDeductionDel) {
		this.ctDeductionDel = ctDeductionDel;
	}
	
	public String getShowItem() {
		return showItem;
	}
	
	public void setShowItem(String showItem) {
		this.showItem = showItem;
	}
	
	public boolean isItemPricing() {
		return itemPricing;
	}
	
	public void setItemPricing(boolean itemPricing) {
		this.itemPricing = itemPricing;
	}
	
	public boolean isItemCeilingQuantity() {
		return itemCeilingQuantity;
	}
	
	public void setItemCeilingQuantity(boolean itemCeilingQuantity) {
		this.itemCeilingQuantity = itemCeilingQuantity;
	}

	public boolean isChkSchedule() {
		return chkSchedule;
	}

	public void setChkSchedule(boolean chkSchedule) {
		this.chkSchedule = chkSchedule;
	}

	public boolean isChkProgressive() {
		return chkProgressive;
	}

	public void setChkProgressive(boolean chkProgressive) {
		this.chkProgressive = chkProgressive;
	}

	public CtSchedule getCtSchedulesDel() {
		return ctSchedulesDel;
	}

	public void setCtSchedulesDel(CtSchedule ctSchedulesDel) {
		this.ctSchedulesDel = ctSchedulesDel;
	}

	public CtProgressivePayment getCtProgressivePaymentDel() {
		return ctProgressivePaymentDel;
	}

	public void setCtProgressivePaymentDel(
			CtProgressivePayment ctProgressivePaymentDel) {
		this.ctProgressivePaymentDel = ctProgressivePaymentDel;
	}

	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}

	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
			remarkList = new ArrayList<CtRemark>();
		return remarkList;
	}

	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	public List<CtAttachment> getAttachmentList() {
		if(attachmentList == null)
		   attachmentList = new ArrayList<CtAttachment>();
		return attachmentList;
	}

	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getCtNumber() {
		return ctNumber;
	}

	public void setCtNumber(String ctNumber) {
		this.ctNumber = ctNumber;
	}

	public String getCtName() {
		return ctName;
	}

	public void setCtName(String ctName) {
		this.ctName = ctName;
	}

	public String getCtCircularNo() {
		return ctCircularNo;
	}

	public void setCtCircularNo(String ctCircularNo) {
		this.ctCircularNo = ctCircularNo;
	}

	public String getCtCircularName() {
		return ctCircularName;
	}

	public void setCtCircularName(String ctCircularName) {
		this.ctCircularName = ctCircularName;
	}

	public String getCtSupplierId() {
		return ctSupplierId;
	}

	public void setCtSupplierId(String ctSupplierId) {
		this.ctSupplierId = ctSupplierId;
	}

	public String getCtSupplierName() {
		return ctSupplierName;
	}

	public void setCtSupplierName(String ctSupplierName) {
		this.ctSupplierName = ctSupplierName;
	}

	public String getCtLOANo() {
		return ctLOANo;
	}

	public void setCtLOANo(String ctLOANo) {
		this.ctLOANo = ctLOANo;
	}

	public List<VCtContract> getContractList() {
		 if(contractList == null)
			 contractList = new ArrayList<VCtContract>();
		return contractList;
	}

	public void setContractList(List<VCtContract> contractList) {
		this.contractList = contractList;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public CtContract getCtContract() {
		return ctContract;
	}

	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}
	
	public HashMap<String, PmUserGroupUser> getApproverMap() {
		if(approverMap == null){
			setApproverMap(new HashMap<String,PmUserGroupUser>());
		}
		return approverMap;
	}

	public void setApproverMap(HashMap<String, PmUserGroupUser> approverMap) {
		this.approverMap = approverMap;
	}

	public Map<Long, PmUserGroup> getUserGroupMap() {
		return userGroupMap;
	}

	public void setUserGroupMap(Map<Long, PmUserGroup> userGroupMap) {
		this.userGroupMap = userGroupMap;
	}
	

	public List<CtContractItem> getCtItemList() {
		if(ctItemList == null)
			ctItemList = new ArrayList<CtContractItem>();
		return ctItemList;
	}

	public void setCtItemList(List<CtContractItem> ctItemList) {
		this.ctItemList = ctItemList;
	}

	public List<CtContractItem> getCtItemServiceList() {
		if(ctItemServiceList == null)
			ctItemServiceList = new ArrayList<CtContractItem>();
		return ctItemServiceList;
	}

	public void setCtItemServiceList(List<CtContractItem> ctItemServiceList) {
		this.ctItemServiceList = ctItemServiceList;
	}

	public boolean isBpkApprover() {
		return bpkApprover;
	}

	public void setBpkApprover(boolean bpkApprover) {
		this.bpkApprover = bpkApprover;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public boolean isServiceItem() {
		return serviceItem;
	}

	public void setServiceItem(boolean serviceItem) {
		this.serviceItem = serviceItem;
	}

	public boolean isProductItem() {
		return productItem;
	}

	public void setProductItem(boolean productItem) {
		this.productItem = productItem;
	}

	public CtContractItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(CtContractItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public CtContractItem getSelectedServiceItem() {
		return selectedServiceItem;
	}

	public void setSelectedServiceItem(CtContractItem selectedServiceItem) {
		this.selectedServiceItem = selectedServiceItem;
	}

	public String getSupplierAddress() {
		return supplierAddress;
	}

	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	public SmAddress getSmAddress() {
		return smAddress;
	}

	public void setSmAddress(SmAddress smAddress) {
		this.smAddress = smAddress;
	}
	
	public String getSupplierCompanyName() {
		return supplierCompanyName;
	}

	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}

	public boolean isUpdateCompanyName() {
		return updateCompanyName;
	}
	
	public void setUpdateCompanyName(boolean updateCompanyName) {
		this.updateCompanyName = updateCompanyName;
	}

	public boolean isUpdateSupplierAddress() {
		return updateSupplierAddress;
	}
	
	public void setUpdateSupplierAddress(boolean updateSupplierAddress) {
		this.updateSupplierAddress = updateSupplierAddress;
	}

	public CtPerformanceBond getCtPerformanceBond() {
		return ctPerformanceBond;
	}

	public void setCtPerformanceBond(CtPerformanceBond ctPerformanceBond) {
		this.ctPerformanceBond = ctPerformanceBond;
	}

	public CtContractZone getSelectedCtZone() {
		return selectedCtZone;
	}

	public void setSelectedCtZone(CtContractZone selectedCtZone) {
		this.selectedCtZone = selectedCtZone;
	}

	public List<CtContractZoneDtl> getCtContractZoneDtls() {
		return ctContractZoneDtls;
	}

	public void setCtContractZoneDtls(List<CtContractZoneDtl> ctContractZoneDtls) {
		this.ctContractZoneDtls = ctContractZoneDtls;
	}

	public boolean isShowZoneDetails() {
		return showZoneDetails;
	}

	public void setShowZoneDetails(boolean showZoneDetails) {
		this.showZoneDetails = showZoneDetails;
	}

	public boolean isDisableScheduleRadio() {
		return disableScheduleRadio;
	}

	public void setDisableScheduleRadio(boolean disableScheduleRadio) {
		this.disableScheduleRadio = disableScheduleRadio;
	}

	public boolean isDisableProgessiveRadio() {
		return disableProgessiveRadio;
	}

	public void setDisableProgessiveRadio(boolean disableProgessiveRadio) {
		this.disableProgessiveRadio = disableProgessiveRadio;
	}

	public boolean isChkButton() {
		return chkButton;
	}

	public void setChkButton(boolean chkButton) {
		this.chkButton = chkButton;
	}

	public boolean isChkScheduleNulValue() {
		return chkScheduleNulValue;
	}

	public void setChkScheduleNulValue(boolean chkScheduleNulValue) {
		this.chkScheduleNulValue = chkScheduleNulValue;
	}

	public boolean isChkProgressiveNulValue() {
		return chkProgressiveNulValue;
	}

	public void setChkProgressiveNulValue(boolean chkProgressiveNulValue) {
		this.chkProgressiveNulValue = chkProgressiveNulValue;
	}

	public Long getMinistryProfileId() {
		return ministryProfileId;
	}

	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}

	public Long getDepartmentProfileId() {
		return departmentProfileId;
	}

	public void setDepartmentProfileId(Long departmentProfileId) {
		this.departmentProfileId = departmentProfileId;
	}

	public List<PmOrgProfile> getPmOrgProfileList() {
		   if(pmOrgProfileList == null)
			   pmOrgProfileList = new ArrayList<PmOrgProfile>();
			return pmOrgProfileList;
	}

	public void setPmOrgProfileList(List<PmOrgProfile> pmOrgProfileList) {
		this.pmOrgProfileList = pmOrgProfileList;
	}

	public List<CtAgency> getListOfAgency() {
		if(listOfAgency == null)
			listOfAgency = new ArrayList<CtAgency>();
		return listOfAgency;
	}

	public void setListOfAgency(List<CtAgency> listOfAgency) {
		this.listOfAgency = listOfAgency;
	}

	public CtDeduction getSelectedDeduction() {
		return selectedDeduction;
	}

	public void setSelectedDeduction(CtDeduction selectedDeduction) {
		this.selectedDeduction = selectedDeduction;
	}

	public List<CtDeductionClause> getCtDeductionClauseList() {
		if(ctDeductionClauseList==null) {
			ctDeductionClauseList = new ArrayList<CtDeductionClause>();
		}
		return ctDeductionClauseList;
	}

	public void setCtDeductionClauseList(List<CtDeductionClause> ctDeductionClauseList) {
		this.ctDeductionClauseList = ctDeductionClauseList;
	}

	public String getCtPhysicalNo() {
		return ctPhysicalNo;
	}

	public void setCtPhysicalNo(String ctPhysicalNo) {
		this.ctPhysicalNo = ctPhysicalNo;
	}

	public boolean isViewContract() {
		return viewContract;
	}

	public void setViewContract(boolean viewContract) {
		this.viewContract = viewContract;
	}

	public boolean isScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(boolean scheduleType) {
		this.scheduleType = scheduleType;
	}

	public CtContractItem getSelectedCCItem() {
		return selectedCCItem;
	}

	public void setSelectedCCItem(CtContractItem selectedCCItem) {
		this.selectedCCItem = selectedCCItem;
	}

	public boolean isDirectPurchase() {
		return directPurchase;
	}

	public void setDirectPurchase(boolean directPurchase) {
		this.directPurchase = directPurchase;
	}

	public boolean isUpdateContract() {
		return updateContract;
	}

	public void setUpdateContract(boolean updateContract) {
		this.updateContract = updateContract;
	}

	public String getSupplierLocation() {
		return supplierLocation;
	}

	public void setSupplierLocation(String supplierLocation) {
		this.supplierLocation = supplierLocation;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getPopupForImportedItems() {
		return popupForImportedItems;
	}

	public void setPopupForImportedItems(String popupForImportedItems) {
		this.popupForImportedItems = popupForImportedItems;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public boolean isValidateScheduleDate() {
		return validateScheduleDate;
	}

	public void setValidateScheduleDate(boolean validateScheduleDate) {
		this.validateScheduleDate = validateScheduleDate;
	}

	public TreeNode getCtZoneTrees() {
		return ctZoneTrees;
	}

	public void setCtZoneTrees(TreeNode ctZoneTrees) {
		this.ctZoneTrees = ctZoneTrees;
	}

	public boolean isChkJustification() {
		return chkJustification;
	}

	public void setChkJustification(boolean chkJustification) {
		this.chkJustification = chkJustification;
	}

	public boolean isFlgPaymentTracking() {
		return flgPaymentTracking;
	}

	public void setFlgPaymentTracking(boolean flgPaymentTracking) {
		this.flgPaymentTracking = flgPaymentTracking;
	}

	public double getAdvancePaymentAmt() {
		return advancePaymentAmt;
	}

	public void setAdvancePaymentAmt(double advancePaymentAmt) {
		this.advancePaymentAmt = advancePaymentAmt;
	}

	public BigDecimal getPaidAdvancePaymentAmt() {
		return paidAdvancePaymentAmt;
	}

	public void setPaidAdvancePaymentAmt(BigDecimal paidAdvancePaymentAmt) {
		this.paidAdvancePaymentAmt = paidAdvancePaymentAmt;
	}

	public BigDecimal getPaidInvoiceAmt() {
		return paidInvoiceAmt;
	}

	public void setPaidInvoiceAmt(BigDecimal paidInvoiceAmt) {
		this.paidInvoiceAmt = paidInvoiceAmt;
	}

	public Map<String, PmUser> getUserMap() {
		if(userMap==null) {
			userMap = new HashMap<String, PmUser>();
		}
		return userMap;
	}

	public void setUserMap(Map<String, PmUser> userMap) {
		this.userMap = userMap;
	}

	public SmSupplierBank getSmSupplierBanks() {
		return smSupplierBanks;
	}

	public void setSmSupplierBanks(SmSupplierBank smSupplierBanks) {
		this.smSupplierBanks = smSupplierBanks;
	}

	public int getCtContractStatus() {
		return ctContractStatus;
	}

	public void setCtContractStatus(int ctContractStatus) {
		this.ctContractStatus = ctContractStatus;
	}

	public boolean isChkConceValue() {
		return chkConceValue;
	}

	public void setChkConceValue(boolean chkConceValue) {
		this.chkConceValue = chkConceValue;
	}

	public Date getCtDateStart() {
		return ctDateStart;
	}

	public void setCtDateStart(Date ctDateStart) {
		this.ctDateStart = ctDateStart;
	}

	public Date getCtDateEnd() {
		return ctDateEnd;
	}

	public void setCtDateEnd(Date ctDateEnd) {
		this.ctDateEnd = ctDateEnd;
	}
	
	public Long getTerminationTypeId() {
		return terminationTypeId;
	}

	public void setTerminationTypeId(Long terminationTypeId) {
		this.terminationTypeId = terminationTypeId;
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	public Map<String, Object> getRadioButtonSchedule() {
		if(radioButtonSchedule == null){
			radioButtonSchedule = new LinkedHashMap<String,Object>();
			FacesScopes face = new FacesScopes();
			radioButtonSchedule.put(ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), "S");
			radioButtonSchedule.put(ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), "P");
		}
		return radioButtonSchedule;
	}

	public void setRadioButtonSchedule(Map<String, Object> radioButtonSchedule) {
		this.radioButtonSchedule = radioButtonSchedule;
	}

	public String getFrequencySchedule() {
		return frequencySchedule;
	}

	public void setFrequencySchedule(String frequencySchedule) {
		this.frequencySchedule = frequencySchedule;
	}
	
	public String getExternalPageUrl() {
		return externalPageUrl;
	}
	
	public void setExternalPageUrl(String externalPageUrl) {
		this.externalPageUrl = externalPageUrl;
	}

	public String getPtjCodeName() {
		return ptjCodeName;
	}

	public void setPtjCodeName(String ptjCodeName) {
		this.ptjCodeName = ptjCodeName;
	}

	public String getPtjSearchtype() {
		return ptjSearchtype;
	}

	public void setPtjSearchtype(String ptjSearchtype) {
		this.ptjSearchtype = ptjSearchtype;
	}

	public String getPtjCodeNameSearchValues() {
		return ptjCodeNameSearchValues;
	}

	public void setPtjCodeNameSearchValues(String ptjCodeNameSearchValues) {
		this.ptjCodeNameSearchValues = ptjCodeNameSearchValues;
	}
	
	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	public boolean isTabMenuShowForZone() {
		return tabMenuShowForZone;
	}

	public void setTabMenuShowForZone(boolean tabMenuShowForZone) {
		this.tabMenuShowForZone = tabMenuShowForZone;
	}

	public boolean isTabMenuShowFactored() {
		return tabMenuShowFactored;
	}

	public void setTabMenuShowFactored(boolean tabMenuShowFactored) {
		this.tabMenuShowFactored = tabMenuShowFactored;
	}

	public MenuModel getFulfilmentMenuModel() {
		return fulfilmentMenuModel;
	}

	public void setFulfilmentMenuModel(MenuModel fulfilmentMenuModel) {
		this.fulfilmentMenuModel = fulfilmentMenuModel;
	}

	public List<String> getFinalListOfSideMenu() {
		return finalListOfSideMenu;
	}

	public void setFinalListOfSideMenu(List<String> finalListOfSideMenu) {
		this.finalListOfSideMenu = finalListOfSideMenu;
	}

	public CtAgency getSelectedAgency() {
		return selectedAgency;
	}

	public void setSelectedAgency(CtAgency selectedAgency) {
		this.selectedAgency = selectedAgency;
	}
}
