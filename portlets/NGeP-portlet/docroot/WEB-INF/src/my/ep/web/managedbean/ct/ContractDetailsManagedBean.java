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
import my.ep.persistence.entity.ct.assoc.CtAssociation;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.suspend.CtSuspendActive;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationClause;
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
	private boolean amendAckw;
	
	private CtContractVer contractVer;
	private CtDeduction ctDeductionDel = new CtDeduction();
	private List<CtDeductionClause> ctDeductionClauseList;
	private CtDeductionClause ctDeductionClauseDel = new CtDeductionClause();
	
	private String scheduleOption;
	private boolean chkSchedule;
	private boolean chkProgressive;
	private CtSchedule ctSchedulesDel = new CtSchedule();
	private CtProgressivePayment ctProgressivePaymentDel = new CtProgressivePayment();
	
	private DualListModel<String> approverModel;
	private HashMap<String, PmUserGroupUser> approverMap;
	private Map<Long, PmUserGroup> userGroupMap;
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtContract> contractList = new ArrayList<CtContract>();
	private CtContract ctContract;
	
	private boolean terminateNotice;
	private boolean terminateRejectLetter;
	private boolean terminateWarningLetter;
	private CtTermination termination;
	private CtTerminationReason terminationReason;
	private List<CtTerminationClause> terminationClauseList;
	private CtTerminationClause terminationClause = new CtTerminationClause();
	
	private boolean activeSuspension;
	private boolean booleanSuspendValue = false;
	private CtSuspendActive ctSuspendActive;
	
	private CtAssociation association = new CtAssociation();
	private List<CtAssociation> associationList;
	private List<CtContractVer> associateSearchList;
	private CtContractVer assocoiateContractVer;
	private List<CtContractVer> assocoiateContractVerList;
	
	private String contractNo;
	private String contractName;
	private String circularNo;
	private String circularName;
	private String supplierId;
	private String supplierName;
	private String dateStart;
	private String dateEnd;
	private String LOANo;
	private String productCode;
	private String contractStatus;
	
	private String projectName;
	private String projectDescription;
	
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

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getCircularNo() {
		return circularNo;
	}

	public void setCircularNo(String circularNo) {
		this.circularNo = circularNo;
	}

	public String getCircularName() {
		return circularName;
	}

	public void setCircularName(String circularName) {
		this.circularName = circularName;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getDateStart() {
		return dateStart;
	}

	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getLOANo() {
		return LOANo;
	}

	public void setLOANo(String lOANo) {
		LOANo = lOANo;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
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
	
	public List<CtTerminationClause> getTerminationClauseList() {
		if(terminationClauseList == null)
			terminationClauseList = new ArrayList<CtTerminationClause>();
		return terminationClauseList;
	}

	public void setTerminationClauseList(
			List<CtTerminationClause> terminationClauseList) {
		this.terminationClauseList = terminationClauseList;
	}

	public CtTerminationClause getTerminationClause() {
		return terminationClause;
	}

	public void setTerminationClause(CtTerminationClause terminationClause) {
		this.terminationClause = terminationClause;
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

	public CtAssociation getAssociation() {
		return association;
	}

	public void setAssociation(CtAssociation association) {
		this.association = association;
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

	public List<CtAssociation> getAssociationList() {
		if(associationList == null)
			associationList = new ArrayList<CtAssociation>();
		return associationList;
	}

	public void setAssociationList(List<CtAssociation> associationList) {
		this.associationList = associationList;
	}

	public List<CtContractVer> getAssociateSearchList() {
		if(associateSearchList == null)
			associateSearchList = new ArrayList<CtContractVer>();
		return associateSearchList;
	}

	public void setAssociateSearchList(List<CtContractVer> associateSearchList) {
		this.associateSearchList = associateSearchList;
	}

	public CtContractVer getAssocoiateContractVer() {
		return assocoiateContractVer;
	}

	public void setAssocoiateContractVer(CtContractVer assocoiateContractVer) {
		this.assocoiateContractVer = assocoiateContractVer;
	}

	public List<CtContractVer> getAssocoiateContractVerList() {
		if(assocoiateContractVerList == null)
			assocoiateContractVerList = new ArrayList<CtContractVer>();
		return assocoiateContractVerList;
	}

	public void setAssocoiateContractVerList(
			List<CtContractVer> assocoiateContractVerList) {
		this.assocoiateContractVerList = assocoiateContractVerList;
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

	public CtSuspendActive getCtSuspendActive() {
		return ctSuspendActive;
	}

	public void setCtSuspendActive(CtSuspendActive ctSuspendActive) {
		this.ctSuspendActive = ctSuspendActive;
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
	
}
