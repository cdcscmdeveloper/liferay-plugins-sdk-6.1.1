/**
 * 
 */
package my.ep.web.managedbean.fl.paymentmatch;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.persistence.entity.fl.payment.FlPaymentDeduction;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.TreeNode;

/**
 * @author admin
 *
 */
@ManagedBean(name = "pmMB")
@SessionScoped
public class PaymentMatchManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FlPaymentAdvice paymentAdvice;
	private List<FlPaymentDeduction> selectedDeductions;
	private CtDeduction ctDeduction;
	private List<CtDeductionClause> ctDeductionClauseList;
	private List<PmGlAccount> glAccountList;
	private List<PmVotFund> votFundList;
	
	// for Deduction panel display	
	private String docType;
	
	
	private TreeNode chargeLineItemRoot ;
	private TreeNode selectedNode;
	
	// set chargeline for selected items
		private String votFundCode;
		private String assetIndCode;
		private String glAccountCode;
		private String projectCode;
		private String setiaCode;
		private String subSetiaCode;
		private String prgActivityCode;
		private String votFundType;
		
		private Long votFundId;
		private Long assetIndId;
		private Long glAccountId;
		private Long projectId;
		private Long setiaId;
		private Long subSetiaId;
		private Long prgActivityId;
		private String financingTypeCode;
		private Long deductionId;
		
	
	private FlFulfilmentRequest fulfilmentRequest;
	
	private BigDecimal orderedAmtGrandTotal ;
	private BigDecimal receivedAmtGrandTotal ;
	private BigDecimal invAmtGrandTotal ;
	private String serviceCharge ;
	
	private BigDecimal deductionAmt ;
	
	private String buttonVal;
	private PmUser pmUser;
	
	//getSuplier address details
	private String supplierAddress;
	private String phoneNo;
	private String faxNo;
	private String contactEmail;
	private String contactPerson;
	private String contactPhoneNo;
	private String branchName;
	
	//tab menu
	private int selectedTabIndex;
	
	private String adjustmentDocNo;
	private String stopInstrNo;
	
	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the paymentAdvice
	 */
	public FlPaymentAdvice getPaymentAdvice() {
		return paymentAdvice;
	}

	/**
	 * @param paymentAdvice the paymentAdvice to set
	 */
	public void setPaymentAdvice(FlPaymentAdvice paymentAdvice) {
		this.paymentAdvice = paymentAdvice;
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
	 * @return the chargeLineItemRoot
	 */
	public TreeNode getChargeLineItemRoot() {
		return chargeLineItemRoot;
	}
	/**
	 * @param chargeLineItemRoot the chargeLineItemRoot to set
	 */
	public void setChargeLineItemRoot(TreeNode chargeLineItemRoot) {
		this.chargeLineItemRoot = chargeLineItemRoot;
	}
	/**
	 * @return the fulfilmentRequest
	 */
	public FlFulfilmentRequest getFulfilmentRequest() {
		return fulfilmentRequest;
	}
	/**
	 * @param fulfilmentRequest the fulfilmentRequest to set
	 */
	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
	}
	/**
	 * @return the orderedAmtGrandTotal
	 */
	public BigDecimal getOrderedAmtGrandTotal() {
		return orderedAmtGrandTotal;
	}
	/**
	 * @param orderedAmtGrandTotal the orderedAmtGrandTotal to set
	 */
	public void setOrderedAmtGrandTotal(BigDecimal orderedAmtGrandTotal) {
		this.orderedAmtGrandTotal = orderedAmtGrandTotal;
	}
	/**
	 * @return the receivedAmtGrandTotal
	 */
	public BigDecimal getReceivedAmtGrandTotal() {
		return receivedAmtGrandTotal;
	}
	/**
	 * @param receivedAmtGrandTotal the receivedAmtGrandTotal to set
	 */
	public void setReceivedAmtGrandTotal(BigDecimal receivedAmtGrandTotal) {
		this.receivedAmtGrandTotal = receivedAmtGrandTotal;
	}
	/**
	 * @return the invAmtGrandTotal
	 */
	public BigDecimal getInvAmtGrandTotal() {
		return invAmtGrandTotal;
	}
	/**
	 * @param invAmtGrandTotal the invAmtGrandTotal to set
	 */
	public void setInvAmtGrandTotal(BigDecimal invAmtGrandTotal) {
		this.invAmtGrandTotal = invAmtGrandTotal;
	}
	/**
	 * @return the serviceCharge
	 */
	public String getServiceCharge() {
		return serviceCharge;
	}
	/**
	 * @param serviceCharge the serviceCharge to set
	 */
	public void setServiceCharge(String serviceCharge) {
		this.serviceCharge = serviceCharge;
	}
	/**
	 * @return the selectedNode
	 */
	public TreeNode getSelectedNode() {
		return selectedNode;
	}
	/**
	 * @param selectedNode the selectedNode to set
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	/**
	 * @return the votFundCode
	 */
	public String getVotFundCode() {
		return votFundCode;
	}
	/**
	 * @param votFundCode the votFundCode to set
	 */
	public void setVotFundCode(String votFundCode) {
		this.votFundCode = votFundCode;
	}
	/**
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return projectCode;
	}
	/**
	 * @param projectCode the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	/**
	 * @return the setiaCode
	 */
	public String getSetiaCode() {
		return setiaCode;
	}
	/**
	 * @param setiaCode the setiaCode to set
	 */
	public void setSetiaCode(String setiaCode) {
		this.setiaCode = setiaCode;
	}
	/**
	 * @return the subSetiaCode
	 */
	public String getSubSetiaCode() {
		return subSetiaCode;
	}
	/**
	 * @param subSetiaCode the subSetiaCode to set
	 */
	public void setSubSetiaCode(String subSetiaCode) {
		this.subSetiaCode = subSetiaCode;
	}
	/**
	 * @return the prgActivityCode
	 */
	public String getPrgActivityCode() {
		return prgActivityCode;
	}
	/**
	 * @param prgActivityCode the prgActivityCode to set
	 */
	public void setPrgActivityCode(String prgActivityCode) {
		this.prgActivityCode = prgActivityCode;
	}
	/**
	 * @return the deductionAmt
	 */
	public BigDecimal getDeductionAmt() {
		return deductionAmt;
	}
	/**
	 * @param deductionAmt the deductionAmt to set
	 */
	public void setDeductionAmt(BigDecimal deductionAmt) {
		this.deductionAmt = deductionAmt;
	}
	/**
	 * @return the buttonVal
	 */
	public String getButtonVal() {
		return buttonVal;
	}
	/**
	 * @param buttonVal the buttonVal to set
	 */
	public void setButtonVal(String buttonVal) {
		this.buttonVal = buttonVal;
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
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}
	/**
	 * @param pmUser the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}
	/**
	 * @return the phoneNo
	 */
	public String getPhoneNo() {
		return phoneNo;
	}
	/**
	 * @param phoneNo the phoneNo to set
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	/**
	 * @return the faxNo
	 */
	public String getFaxNo() {
		return faxNo;
	}
	/**
	 * @param faxNo the faxNo to set
	 */
	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}
	/**
	 * @return the contactEmail
	 */
	public String getContactEmail() {
		return contactEmail;
	}
	/**
	 * @param contactEmail the contactEmail to set
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	/**
	 * @return the contactPerson
	 */
	public String getContactPerson() {
		return contactPerson;
	}
	/**
	 * @param contactPerson the contactPerson to set
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	/**
	 * @return the contactPhoneNo
	 */
	public String getContactPhoneNo() {
		return contactPhoneNo;
	}
	/**
	 * @param contactPhoneNo the contactPhoneNo to set
	 */
	public void setContactPhoneNo(String contactPhoneNo) {
		this.contactPhoneNo = contactPhoneNo;
	}
	/**
	 * @return the assetIndCode
	 */
	public String getAssetIndCode() {
		return assetIndCode;
	}
	/**
	 * @param assetIndCode the assetIndCode to set
	 */
	public void setAssetIndCode(String assetIndCode) {
		this.assetIndCode = assetIndCode;
	}
	/**
	 * @return the glAccountCode
	 */
	public String getGlAccountCode() {
		return glAccountCode;
	}
	/**
	 * @param glAccountCode the glAccountCode to set
	 */
	public void setGlAccountCode(String glAccountCode) {
		this.glAccountCode = glAccountCode;
	}
	/**
	 * @return the votFundType
	 */
	public String getVotFundType() {
		return votFundType;
	}
	/**
	 * @param votFundType the votFundType to set
	 */
	public void setVotFundType(String votFundType) {
		this.votFundType = votFundType;
	}
	/**
	 * @return the votFundId
	 */
	public Long getVotFundId() {
		return votFundId;
	}
	/**
	 * @param votFundId the votFundId to set
	 */
	public void setVotFundId(Long votFundId) {
		this.votFundId = votFundId;
	}
	/**
	 * @return the assetIndId
	 */
	public Long getAssetIndId() {
		return assetIndId;
	}
	/**
	 * @param assetIndId the assetIndId to set
	 */
	public void setAssetIndId(Long assetIndId) {
		this.assetIndId = assetIndId;
	}
	/**
	 * @return the glAccountId
	 */
	public Long getGlAccountId() {
		return glAccountId;
	}
	/**
	 * @param glAccountId the glAccountId to set
	 */
	public void setGlAccountId(Long glAccountId) {
		this.glAccountId = glAccountId;
	}
	/**
	 * @return the projectId
	 */
	public Long getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return the setiaId
	 */
	public Long getSetiaId() {
		return setiaId;
	}
	/**
	 * @param setiaId the setiaId to set
	 */
	public void setSetiaId(Long setiaId) {
		this.setiaId = setiaId;
	}
	/**
	 * @return the subSetiaId
	 */
	public Long getSubSetiaId() {
		return subSetiaId;
	}
	/**
	 * @param subSetiaId the subSetiaId to set
	 */
	public void setSubSetiaId(Long subSetiaId) {
		this.subSetiaId = subSetiaId;
	}
	/**
	 * @return the prgActivityId
	 */
	public Long getPrgActivityId() {
		return prgActivityId;
	}
	/**
	 * @param prgActivityId the prgActivityId to set
	 */
	public void setPrgActivityId(Long prgActivityId) {
		this.prgActivityId = prgActivityId;
	}
	/**
	 * @return the financingTypeCode
	 */
	public String getFinancingTypeCode() {
		return financingTypeCode;
	}
	/**
	 * @param financingTypeCode the financingTypeCode to set
	 */
	public void setFinancingTypeCode(String financingTypeCode) {
		this.financingTypeCode = financingTypeCode;
	}
	/**
	 * @return the branchName
	 */
	public String getBranchName() {
		return branchName;
	}
	/**
	 * @param branchName the branchName to set
	 */
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getAdjustmentDocNo() {
		return adjustmentDocNo;
	}

	public void setAdjustmentDocNo(String adjustmentDocNo) {
		this.adjustmentDocNo = adjustmentDocNo;
	}

	public String getStopInstrNo() {
		return stopInstrNo;
	}

	public void setStopInstrNo(String stopInstrNo) {
		this.stopInstrNo = stopInstrNo;
	}

	/**
	 * @return the selectedDeductions
	 */
	public List<FlPaymentDeduction> getSelectedDeductions() {
		return selectedDeductions;
	}

	/**
	 * @param selectedDeductions the selectedDeductions to set
	 */
	public void setSelectedDeductions(List<FlPaymentDeduction> selectedDeductions) {
		this.selectedDeductions = selectedDeductions;
	}

	/**
	 * @return the deductionId
	 */
	public Long getDeductionId() {
		return deductionId;
	}

	/**
	 * @param deductionId the deductionId to set
	 */
	public void setDeductionId(Long deductionId) {
		this.deductionId = deductionId;
	}

	/**
	 * @return the ctDeduction
	 */
	public CtDeduction getCtDeduction() {
		return ctDeduction;
	}

	/**
	 * @param ctDeduction the ctDeduction to set
	 */
	public void setCtDeduction(CtDeduction ctDeduction) {
		this.ctDeduction = ctDeduction;
	}

	/**
	 * @return the ctDeductionClauseList
	 */
	public List<CtDeductionClause> getCtDeductionClauseList() {
		return ctDeductionClauseList;
	}

	/**
	 * @param ctDeductionClauseList the ctDeductionClauseList to set
	 */
	public void setCtDeductionClauseList(
			List<CtDeductionClause> ctDeductionClauseList) {
		this.ctDeductionClauseList = ctDeductionClauseList;
	}

	/**
	 * @return the glAccountList
	 */
	public List<PmGlAccount> getGlAccountList() {
		return glAccountList;
	}

	/**
	 * @param glAccountList the glAccountList to set
	 */
	public void setGlAccountList(List<PmGlAccount> glAccountList) {
		this.glAccountList = glAccountList;
	}

	/**
	 * @return the votFundList
	 */
	public List<PmVotFund> getVotFundList() {
		return votFundList;
	}

	/**
	 * @param votFundList the votFundList to set
	 */
	public void setVotFundList(List<PmVotFund> votFundList) {
		this.votFundList = votFundList;
	}
}
