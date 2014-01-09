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
import my.ep.persistence.entity.ct.assoc.CtAssocContract;
import my.ep.persistence.entity.ct.assoc.CtAssociation;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
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

	private static Logger log = Logger.getLogger(ContractDetailsManagedBean.class);
	private static final long serialVersionUID = 1L;
	
	private String taskName;
	private String[] action;
	private String processName;
	private int selectedIndex;
	private String chkRenderPage ="BPM";
	
	private String showItem ="Item  Pricing";
	private boolean itemPricing = true;
	private boolean itemCeilingQuantity;
	private boolean amendDisable;
	private boolean approvrLetter;
	private boolean amendAckw;
	private boolean bpkApprover=true;
	
	private String itemType;
	private boolean serviceItem;
	private boolean productItem = true;
	private CtContractItem selectedItem;
	private CtContractItem SelectedServiceItem;
	private boolean amendmentAction;
	
	
	private CtContractVer contractVer;
	private CtDeduction ctDeductionDel = new CtDeduction();
	private List<CtDeductionClause> ctDeductionClauseList;
	private CtDeductionClause ctDeductionClauseDel = new CtDeductionClause();
	private CtDeduction[] selectedDeductionList;
	
	private String scheduleOption="1";
	private boolean chkSchedule = true;
	private boolean chkProgressive;
	private boolean chkScheduleNulValue;
	private boolean chkProgressiveNulValue;
	private CtSchedule ctSchedulesDel = new CtSchedule();
	private CtProgressivePayment ctProgressivePaymentDel = new CtProgressivePayment();
	
	private DualListModel<String> approverModel;
	private HashMap<String, PmUserGroupUser> approverMap;
	private Map<Long, PmUserGroup> userGroupMap;
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtContract> contractList = new ArrayList<CtContract>();
	private CtContract ctContract;
	
	private boolean disciplinaryAction;
	private boolean disciplinaryValue;
	private boolean terminateNotice;
	private boolean terminateRejectLetter;
	private boolean terminateWarningLetter;
	private CtTermination termination;
	private CtTerminationReason terminationReason;

	private boolean activeSuspension;
	private boolean booleanSuspendValue = false;
	
	private CtAssociation ctAssociation = new CtAssociation();
	private CtAssocContract ctDelAssocContract = new CtAssocContract();
	private List<CtAssocContract> associationList;
	private List<CtContract> associateSearchList;
	private CtContract assocoiateCtContract;
	private List<CtContractItem> ctContractItem;

	private String ctNumber;
	private String ctName;
	private String ctCircularNo;
	private String ctCircularName;
	private String ctSupplierId;
	private String ctSupplierName;
	private String ctDateStart;
	private String ctDateEnd;
	private String ctLOANo;
	private String ctProductCode;
	private String ctContractStatus;
	
	
	private String projectName;
	private String projectDescription;
	private String suspensionType;
	private boolean activation = true;
	private boolean suspension;
	
	private String supplierAddress;
	private CtPerformanceBond ctPerformanceBond;
	private CtPerformanceBond[] selectedBondList;
	private CtContractZone selectedCtZone;
	private List<CtContractZoneDtl> ctContractZoneDtls;
	private boolean showZoneDetails;
	
	
	private boolean disableScheduleRadio;
	private boolean disableProgessiveRadio;
	private boolean chkButton;
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
	
	public List<CtDeductionClause> getCtDeductionClauseList() {
		if(ctDeductionClauseList == null)
			ctDeductionClauseList = new ArrayList<CtDeductionClause>();
		return ctDeductionClauseList;
	}
	
	public void setCtDeductionClauseList(
			List<CtDeductionClause> ctDeductionClauseList) {
		this.ctDeductionClauseList = ctDeductionClauseList;
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

	public String getCtProductCode() {
		return ctProductCode;
	}

	public void setCtProductCode(String ctProductCode) {
		this.ctProductCode = ctProductCode;
	}

	public String getCtContractStatus() {
		return ctContractStatus;
	}

	public void setCtContractStatus(String ctContractStatus) {
		this.ctContractStatus = ctContractStatus;
	}

	public List<CtContract> getContractList() {
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

	public boolean isTerminateNotice() {
		return terminateNotice;
	}

	public void setTerminateNotice(boolean terminateNotice) {
		this.terminateNotice = terminateNotice;
	}

	public boolean isTerminateRejectLetter() {
		return terminateRejectLetter;
	}

	public void setTerminateRejectLetter(boolean terminateRejectLetter) {
		this.terminateRejectLetter = terminateRejectLetter;
	}

	public boolean isTerminateWarningLetter() {
		return terminateWarningLetter;
	}

	public void setTerminateWarningLetter(boolean terminateWarningLetter) {
		this.terminateWarningLetter = terminateWarningLetter;
	}

	public CtTermination getTermination() {
		return termination;
	}

	public void setTermination(CtTermination termination) {
		this.termination = termination;
	}

	public CtTerminationReason getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(CtTerminationReason terminationReason) {
		this.terminationReason = terminationReason;
	}

	public boolean isActiveSuspension() {
		return activeSuspension;
	}

	public void setActiveSuspension(boolean activeSuspension) {
		this.activeSuspension = activeSuspension;
	}

	public boolean isBooleanSuspendValue() {
		return booleanSuspendValue;
	}

	public void setBooleanSuspendValue(boolean booleanSuspendValue) {
		this.booleanSuspendValue = booleanSuspendValue;
	}

	public CtAssociation getCtAssociation() {
		return ctAssociation;
	}

	public void setCtAssociation(CtAssociation ctAssociation) {
		this.ctAssociation = ctAssociation;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public List<CtAssocContract> getAssociationList() {
		if(associationList == null)
			associationList = new ArrayList<CtAssocContract>();
		return associationList;
	}

	public void setAssociationList(List<CtAssocContract> associationList) {
		this.associationList = associationList;
	}

	public List<CtContract> getAssociateSearchList() {
		if(associateSearchList == null)
			associateSearchList = new ArrayList<CtContract>();
		return associateSearchList;
	}

	public void setAssociateSearchList(List<CtContract> associateSearchList) {
		this.associateSearchList = associateSearchList;
	}

	public CtContract getAssocoiateCtContract() {
		return assocoiateCtContract;
	}

	public void setAssocoiateCtContract(CtContract assocoiateCtContract) {
		this.assocoiateCtContract = assocoiateCtContract;
	}
	
	/**
	 * @return the approverMap
	 */
	public HashMap<String, PmUserGroupUser> getApproverMap() {
		if(approverMap == null){
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

	public Map<Long, PmUserGroup> getUserGroupMap() {
		return userGroupMap;
	}

	public void setUserGroupMap(Map<Long, PmUserGroup> userGroupMap) {
		this.userGroupMap = userGroupMap;
	}

	public String getChkRenderPage() {
		return chkRenderPage;
	}

	public void setChkRenderPage(String chkRenderPage) {
		this.chkRenderPage = chkRenderPage;
	}

	public boolean isAmendDisable() {
		return amendDisable;
	}

	public void setAmendDisable(boolean amendDisable) {
		this.amendDisable = amendDisable;
	}

	public boolean isAmendAckw() {
		return amendAckw;
	}

	public void setAmendAckw(boolean amendAckw) {
		this.amendAckw = amendAckw;
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

	public boolean isApprovrLetter() {
		return approvrLetter;
	}

	public void setApprovrLetter(boolean approvrLetter) {
		this.approvrLetter = approvrLetter;
	}

	public String getSuspensionType() {
		return suspensionType;
	}

	public void setSuspensionType(String suspensionType) {
		this.suspensionType = suspensionType;
	}

	public boolean isActivation() {
		return activation;
	}

	public void setActivation(boolean activation) {
		this.activation = activation;
	}

	public boolean isSuspension() {
		return suspension;
	}

	public void setSuspension(boolean suspension) {
		this.suspension = suspension;
	}

	public boolean isDisciplinaryAction() {
		return disciplinaryAction;
	}

	public void setDisciplinaryAction(boolean disciplinaryAction) {
		this.disciplinaryAction = disciplinaryAction;
	}

	public boolean isDisciplinaryValue() {
		return disciplinaryValue;
	}

	public void setDisciplinaryValue(boolean disciplinaryValue) {
		this.disciplinaryValue = disciplinaryValue;
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
		return SelectedServiceItem;
	}

	public void setSelectedServiceItem(CtContractItem selectedServiceItem) {
		SelectedServiceItem = selectedServiceItem;
	}

	public boolean isAmendmentAction() {
		return amendmentAction;
	}

	public void setAmendmentAction(boolean amendmentAction) {
		this.amendmentAction = amendmentAction;
	}

	public CtDeduction[] getSelectedDeductionList() {
		return selectedDeductionList;
	}

	public void setSelectedDeductionList(CtDeduction[] selectedDeductionList) {
		this.selectedDeductionList = selectedDeductionList;
	}

	public CtAssocContract getCtDelAssocContract() {
		return ctDelAssocContract;
	}

	public void setCtDelAssocContract(CtAssocContract ctDelAssocContract) {
		this.ctDelAssocContract = ctDelAssocContract;
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

	public CtPerformanceBond[] getSelectedBondList() {
		return selectedBondList;
	}

	public void setSelectedBondList(CtPerformanceBond[] selectedBondList) {
		this.selectedBondList = selectedBondList;
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
	
}
