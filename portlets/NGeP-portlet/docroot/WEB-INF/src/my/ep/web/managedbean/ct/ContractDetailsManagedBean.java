/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
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
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;
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
	
	private CtContractVer contractVer;
	private CtContractItem selectedItem;
	private CtContractItem selectedCCItem;
	private CtDeduction selectedDeduction;
	private boolean directPurchase;
	
	private List<CtDeductionClause> ctDeductionClauseList;
	private List<CtDeductionClause> selectedDeductionClauses;
	 
	private String taskName;
	private String[] action;
	private int selectedIndex;
	private String processName;
	private boolean bpkApprover=true;
	
	private String itemType;
	private boolean serviceItem;
	private boolean productItem = true;
	private boolean itemPricing = true;
	private boolean itemCeilingQuantity;
	private String showItem;
	private CtContractItem selectedServiceItem;
	private List<CtContractItem> ctContractItem;
	
	private CtDeduction ctDeductionDel;
	private CtDeductionClause ctDeductionClauseDel;
	
	private boolean chkProgressive;
	private boolean chkSchedule = true;
	private boolean chkScheduleNulValue;
	private boolean chkProgressiveNulValue;
	
	private String scheduleOption="1";
	private CtSchedule ctSchedulesDel;
	private boolean scheduleType;
	private CtProgressivePayment ctProgressivePaymentDel;
	
	private DualListModel<String> approverModel;
	private Map<Long, PmUserGroup> userGroupMap;
	private HashMap<String, PmUserGroupUser> approverMap;
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	
	private CtContract ctContract;
	private List<CtContract> contractList;
	
	 List<PmOrgProfile> pmOrgProfileList;
	
	private String ctName;
	private String ctLOANo;
	private String ctNumber;
	private String ctDateEnd;
	private String ctDateStart;
	private String ctCircularNo;
	private String ctSupplierId;
	private String ctPhysicalNo;
	private String ctCircularName;
	private String ctSupplierName;
	private Long ctContractStatus;
	
	private String supplierAddress;
	private CtPerformanceBond ctPerformanceBond;
	
	private CtContractZone selectedCtZone;
	private List<CtContractZoneDtl> ctContractZoneDtls;
	
	private Long ministryProfileId;
	private Long departmentProfileId;
	
	private boolean chkButton;
	private boolean showZoneDetails;
	private boolean disableScheduleRadio;
	private boolean disableProgessiveRadio;
	private boolean viewContract = false;
	private List<CtAgency> listOfAgency;
	private boolean updateContract;
	
	
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
	
	public CtDeductionClause getCtDeductionClauseDel() {
		return ctDeductionClauseDel;
	}
	
	public void setCtDeductionClauseDel(CtDeductionClause ctDeductionClauseDel) {
		this.ctDeductionClauseDel = ctDeductionClauseDel;
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

	public String getScheduleOption() {
		return scheduleOption;
	}

	public void setScheduleOption(String scheduleOption) {
		this.scheduleOption = scheduleOption;
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

	public String getCtDateStart() {
		return ctDateStart;
	}

	public void setCtDateStart(String ctDateStart) {
		this.ctDateStart = ctDateStart;
	}

	public String getCtDateEnd() {
		return ctDateEnd;
	}

	public void setCtDateEnd(String ctDateEnd) {
		this.ctDateEnd = ctDateEnd;
	}

	public String getCtLOANo() {
		return ctLOANo;
	}

	public void setCtLOANo(String ctLOANo) {
		this.ctLOANo = ctLOANo;
	}

	public Long getCtContractStatus() {
		return ctContractStatus;
	}

	public void setCtContractStatus(Long ctContractStatus) {
		this.ctContractStatus = ctContractStatus;
	}

	public List<CtContract> getContractList() {
		 if(contractList == null)
			 contractList = new ArrayList<CtContract>();
		return contractList;
	}

	public void setContractList(List<CtContract> contractList) {
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
	
	public List<CtContractItem> getCtContractItem() {
		return ctContractItem;
	}

	public void setCtContractItem(List<CtContractItem> ctContractItem) {
		this.ctContractItem = ctContractItem;
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

	public List<CtDeductionClause> getSelectedDeductionClauses() {
		if(selectedDeductionClauses==null) {
			selectedDeductionClauses = new ArrayList<CtDeductionClause>();
		}
		return selectedDeductionClauses;
	}

	public void setSelectedDeductionClauses(List<CtDeductionClause> selectedDeductionClauses) {
		this.selectedDeductionClauses = selectedDeductionClauses;
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
	
	
	
}
