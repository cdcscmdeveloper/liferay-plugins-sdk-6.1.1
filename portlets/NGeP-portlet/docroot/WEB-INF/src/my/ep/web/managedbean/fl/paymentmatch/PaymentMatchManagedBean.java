/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.paymentmatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.persistence.entity.fl.payment.FlPaymentDeduction;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.web.bean.pm.VotSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.DisciplinaryActionVO;
import my.ep.web.vo.TaxInvoiceVO;

import org.primefaces.model.TreeNode;

/**
 * Payment Match Managed Bean.
 *
 * @author KK LIM
 * @since 30/10/2013
 */
@ManagedBean(name = "pmMB")
@SessionScoped
public class PaymentMatchManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private FlPaymentAdvice paymentAdvice;
	private List<FlPaymentDeduction> selectedDeductions;
	private CtDeduction ctDeduction;
	private List<CtDeductionClause> ctDeductionClauseList;
	private List<PmGlAccount> glAccountList;
	private List<PmVotFund> votFundList;
	private PmVotFund feeVotFund;
	private List<CtDeduction> ctDeductionList;
	private List<SelectItem> populateDeductionList;
	private String docType;
	private TreeNode chargeLineItemRoot;
	private TreeNode selectedNode;
	private VotSearchBean votSearchBean;
	private Object[] selectedFundObj;
	private Object[] selectedVotObj;
	private FlPaymentDeduction selectedDeduction;
	private List<SelectItem> populateSubSetiaList;

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
	private String invoiceDate;
	private FlFulfilmentRequest fulfilmentRequest;
	private BigDecimal orderedAmtGrandTotal;
	private BigDecimal receivedAmtGrandTotal;
	private BigDecimal invAmtGrandTotal;
	private BigDecimal canAmtGrandTotal;
	private BigDecimal danAmtGrandTotal;
	private String serviceCharge;
	private BigDecimal deductionAmt;
	private boolean haveAdvancedPymt;
	private BigDecimal paidAmt;
	private BigDecimal balanceAmt;
	private BigDecimal paidPerc;
	private int numberOfCO;
	private boolean showDeductionTab;
	private String buttonVal;
	private PmUser pmUser;
	private Long selectedVotFundId;
	private String searchVotField;
	private Long selectedVotValue;

	// getSuplier address details
	private String supplierAddress;
	private String phoneNo;
	private String faxNo;
	private String contactEmail;
	private String contactPerson;
	private String contactPhoneNo;

	// tab menu
	private int selectedTabIndex;
	private String adjustmentDocNo;
	private String adjustmentDanDocNo;
	private String adjustmentCanDocNo;
	private String stopInstrNo;
	
	//gpki signing
	private PmDigiSign pmDigiSign;
	private boolean autoUpdate = true;
	private String textBeforeSign;
	private String buttonAction;
	private boolean validated = false;
	
	
	private List<FlDeliveryOrder> delOrderList;
	private List<FlFulfilmentNote> fulfilmentNoteList;
	private boolean gfmasError=false;
	private String gfmasErrorContent;
	private String gfmasErrorCode;
	private String gfmasErrorDesc;
	
	private BigDecimal gstAmount;
	private BigDecimal invoiceAmountWithoutGST;
	private List<TaxInvoiceVO> taxInvoiceList;
	Map<Long, CtDeduction> ctDeductionMap = new HashMap<Long, CtDeduction>();
	
	private boolean hyperLinkPage = false;
	private boolean taxInvoice = false;
	
	//start 21229
	private Long ministryProfileId;
	private Long jabatanProfileId;
	private String ptjCodeName;
	private String ptjCodeNameSearchValues;
	
	private PmOrgProfile selectedPmOrgProfile;
		
	private List<FlRemark> remarkReviseList;
	private boolean hideReviseRemarks = false;
	private boolean queryDisplay = false;
	private String queryPaymentDesc;
	//end 21229
	//26279 - start
	private String disciplinaryActionMessage;
	private List<DisciplinaryActionVO> disciplineVO;
	private SmDisciplinaryAction smDisciplinaryAction;
	//26279 - end
	//24893 - start
	private PmOrgProfile chargePTJOrg;
	//24893 - end
	
	//28221 - start
	private PmUser suppInvoiceSubmitted;
	private boolean suppInvoiceActive = true;
	private Long newSupplierUserId;
	//28221 - end

	//24956 start
    private boolean CtTypeMinistry;
    //24956 end

	//29551 - start
	private boolean showDeductionClause;
	private Long selectedDeductionClauseParent;
	//29551 - end
	
	//32678-start
	private String backToPrevious;
	private List<FlRemark> previousRemarkList;
	private List<FlAttachment> previousAttachmentList;
	private boolean docMaintain = false;
	private boolean prevHideRemarks = false;
	//32678-end

	/**
	 * Gets the doc type.
	 *
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * Sets the doc type.
	 *
	 * @param docType            the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * Gets the payment advice.
	 *
	 * @return the paymentAdvice
	 */
	public FlPaymentAdvice getPaymentAdvice() {
		return paymentAdvice;
	}

	/**
	 * Sets the payment advice.
	 *
	 * @param paymentAdvice            the paymentAdvice to set
	 */
	public void setPaymentAdvice(FlPaymentAdvice paymentAdvice) {
		this.paymentAdvice = paymentAdvice;
	}

	/**
	 * Gets the supplier address.
	 *
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * Sets the supplier address.
	 *
	 * @param supplierAddress            the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * Gets the charge line item root.
	 *
	 * @return the chargeLineItemRoot
	 */
	public TreeNode getChargeLineItemRoot() {
		return chargeLineItemRoot;
	}

	/**
	 * Sets the charge line item root.
	 *
	 * @param chargeLineItemRoot            the chargeLineItemRoot to set
	 */
	public void setChargeLineItemRoot(TreeNode chargeLineItemRoot) {
		this.chargeLineItemRoot = chargeLineItemRoot;
	}

	/**
	 * Gets the fulfilment request.
	 *
	 * @return the fulfilmentRequest
	 */
	public FlFulfilmentRequest getFulfilmentRequest() {
		return fulfilmentRequest;
	}

	/**
	 * Sets the fulfilment request.
	 *
	 * @param fulfilmentRequest            the fulfilmentRequest to set
	 */
	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
	}

	/**
	 * Gets the ordered amt grand total.
	 *
	 * @return the orderedAmtGrandTotal
	 */
	public BigDecimal getOrderedAmtGrandTotal() {
		return orderedAmtGrandTotal;
	}

	/**
	 * Sets the ordered amt grand total.
	 *
	 * @param orderedAmtGrandTotal            the orderedAmtGrandTotal to set
	 */
	public void setOrderedAmtGrandTotal(BigDecimal orderedAmtGrandTotal) {
		this.orderedAmtGrandTotal = orderedAmtGrandTotal;
	}

	/**
	 * Gets the received amt grand total.
	 *
	 * @return the receivedAmtGrandTotal
	 */
	public BigDecimal getReceivedAmtGrandTotal() {
		return receivedAmtGrandTotal;
	}

	/**
	 * Sets the received amt grand total.
	 *
	 * @param receivedAmtGrandTotal            the receivedAmtGrandTotal to set
	 */
	public void setReceivedAmtGrandTotal(BigDecimal receivedAmtGrandTotal) {
		this.receivedAmtGrandTotal = receivedAmtGrandTotal;
	}

	/**
	 * Gets the inv amt grand total.
	 *
	 * @return the invAmtGrandTotal
	 */
	public BigDecimal getInvAmtGrandTotal() {
		return invAmtGrandTotal;
	}

	/**
	 * Sets the inv amt grand total.
	 *
	 * @param invAmtGrandTotal            the invAmtGrandTotal to set
	 */
	public void setInvAmtGrandTotal(BigDecimal invAmtGrandTotal) {
		this.invAmtGrandTotal = invAmtGrandTotal;
	}

	/**
	 * Gets the service charge.
	 *
	 * @return the serviceCharge
	 */
	public String getServiceCharge() {
		return serviceCharge;
	}

	/**
	 * Sets the service charge.
	 *
	 * @param serviceCharge            the serviceCharge to set
	 */
	public void setServiceCharge(String serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	/**
	 * Gets the selected node.
	 *
	 * @return the selectedNode
	 */
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * Sets the selected node.
	 *
	 * @param selectedNode            the selectedNode to set
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * Gets the vot fund code.
	 *
	 * @return the votFundCode
	 */
	public String getVotFundCode() {
		return votFundCode;
	}

	/**
	 * Sets the vot fund code.
	 *
	 * @param votFundCode            the votFundCode to set
	 */
	public void setVotFundCode(String votFundCode) {
		this.votFundCode = votFundCode;
	}

	/**
	 * Gets the project code.
	 *
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return projectCode;
	}

	/**
	 * Sets the project code.
	 *
	 * @param projectCode            the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	/**
	 * Gets the setia code.
	 *
	 * @return the setiaCode
	 */
	public String getSetiaCode() {
		return setiaCode;
	}

	/**
	 * Sets the setia code.
	 *
	 * @param setiaCode            the setiaCode to set
	 */
	public void setSetiaCode(String setiaCode) {
		this.setiaCode = setiaCode;
	}

	/**
	 * Gets the sub setia code.
	 *
	 * @return the subSetiaCode
	 */
	public String getSubSetiaCode() {
		return subSetiaCode;
	}

	/**
	 * Sets the sub setia code.
	 *
	 * @param subSetiaCode            the subSetiaCode to set
	 */
	public void setSubSetiaCode(String subSetiaCode) {
		this.subSetiaCode = subSetiaCode;
	}

	/**
	 * Gets the prg activity code.
	 *
	 * @return the prgActivityCode
	 */
	public String getPrgActivityCode() {
		return prgActivityCode;
	}

	/**
	 * Sets the prg activity code.
	 *
	 * @param prgActivityCode            the prgActivityCode to set
	 */
	public void setPrgActivityCode(String prgActivityCode) {
		this.prgActivityCode = prgActivityCode;
	}

	/**
	 * Gets the deduction amt.
	 *
	 * @return the deductionAmt
	 */
	public BigDecimal getDeductionAmt() {
		return deductionAmt;
	}

	/**
	 * Sets the deduction amt.
	 *
	 * @param deductionAmt            the deductionAmt to set
	 */
	public void setDeductionAmt(BigDecimal deductionAmt) {
		this.deductionAmt = deductionAmt;
	}

	/**
	 * Gets the button val.
	 *
	 * @return the buttonVal
	 */
	public String getButtonVal() {
		return buttonVal;
	}

	/**
	 * Sets the button val.
	 *
	 * @param buttonVal            the buttonVal to set
	 */
	public void setButtonVal(String buttonVal) {
		this.buttonVal = buttonVal;
	}

	/**
	 * Gets the selected tab index.
	 *
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * Sets the selected tab index.
	 *
	 * @param selectedTabIndex            the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * Gets the pm user.
	 *
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * Sets the pm user.
	 *
	 * @param pmUser            the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	/**
	 * Gets the phone no.
	 *
	 * @return the phoneNo
	 */
	public String getPhoneNo() {
		return phoneNo;
	}

	/**
	 * Sets the phone no.
	 *
	 * @param phoneNo            the phoneNo to set
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	/**
	 * Gets the fax no.
	 *
	 * @return the faxNo
	 */
	public String getFaxNo() {
		return faxNo;
	}

	/**
	 * Sets the fax no.
	 *
	 * @param faxNo            the faxNo to set
	 */
	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	/**
	 * Gets the contact email.
	 *
	 * @return the contactEmail
	 */
	public String getContactEmail() {
		return contactEmail;
	}

	/**
	 * Sets the contact email.
	 *
	 * @param contactEmail            the contactEmail to set
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	/**
	 * Gets the contact person.
	 *
	 * @return the contactPerson
	 */
	public String getContactPerson() {
		return contactPerson;
	}

	/**
	 * Sets the contact person.
	 *
	 * @param contactPerson            the contactPerson to set
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	/**
	 * Gets the contact phone no.
	 *
	 * @return the contactPhoneNo
	 */
	public String getContactPhoneNo() {
		return contactPhoneNo;
	}

	/**
	 * Sets the contact phone no.
	 *
	 * @param contactPhoneNo            the contactPhoneNo to set
	 */
	public void setContactPhoneNo(String contactPhoneNo) {
		this.contactPhoneNo = contactPhoneNo;
	}

	/**
	 * Gets the asset ind code.
	 *
	 * @return the assetIndCode
	 */
	public String getAssetIndCode() {
		return assetIndCode;
	}

	/**
	 * Sets the asset ind code.
	 *
	 * @param assetIndCode            the assetIndCode to set
	 */
	public void setAssetIndCode(String assetIndCode) {
		this.assetIndCode = assetIndCode;
	}

	/**
	 * Gets the gl account code.
	 *
	 * @return the glAccountCode
	 */
	public String getGlAccountCode() {
		return glAccountCode;
	}

	/**
	 * Sets the gl account code.
	 *
	 * @param glAccountCode            the glAccountCode to set
	 */
	public void setGlAccountCode(String glAccountCode) {
		this.glAccountCode = glAccountCode;
	}

	/**
	 * Gets the vot fund type.
	 *
	 * @return the votFundType
	 */
	public String getVotFundType() {
		return votFundType;
	}

	/**
	 * Sets the vot fund type.
	 *
	 * @param votFundType            the votFundType to set
	 */
	public void setVotFundType(String votFundType) {
		this.votFundType = votFundType;
	}

	/**
	 * Gets the vot fund id.
	 *
	 * @return the votFundId
	 */
	public Long getVotFundId() {
		return votFundId;
	}

	/**
	 * Sets the vot fund id.
	 *
	 * @param votFundId            the votFundId to set
	 */
	public void setVotFundId(Long votFundId) {
		this.votFundId = votFundId;
	}

	/**
	 * Gets the asset ind id.
	 *
	 * @return the assetIndId
	 */
	public Long getAssetIndId() {
		return assetIndId;
	}

	/**
	 * Sets the asset ind id.
	 *
	 * @param assetIndId            the assetIndId to set
	 */
	public void setAssetIndId(Long assetIndId) {
		this.assetIndId = assetIndId;
	}

	/**
	 * Gets the gl account id.
	 *
	 * @return the glAccountId
	 */
	public Long getGlAccountId() {
		return glAccountId;
	}

	/**
	 * Sets the gl account id.
	 *
	 * @param glAccountId            the glAccountId to set
	 */
	public void setGlAccountId(Long glAccountId) {
		this.glAccountId = glAccountId;
	}

	/**
	 * Gets the project id.
	 *
	 * @return the projectId
	 */
	public Long getProjectId() {
		return projectId;
	}

	/**
	 * Sets the project id.
	 *
	 * @param projectId            the projectId to set
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	/**
	 * Gets the setia id.
	 *
	 * @return the setiaId
	 */
	public Long getSetiaId() {
		return setiaId;
	}

	/**
	 * Sets the setia id.
	 *
	 * @param setiaId            the setiaId to set
	 */
	public void setSetiaId(Long setiaId) {
		this.setiaId = setiaId;
	}

	/**
	 * Gets the sub setia id.
	 *
	 * @return the subSetiaId
	 */
	public Long getSubSetiaId() {
		return subSetiaId;
	}

	/**
	 * Sets the sub setia id.
	 *
	 * @param subSetiaId            the subSetiaId to set
	 */
	public void setSubSetiaId(Long subSetiaId) {
		this.subSetiaId = subSetiaId;
	}

	/**
	 * Gets the prg activity id.
	 *
	 * @return the prgActivityId
	 */
	public Long getPrgActivityId() {
		return prgActivityId;
	}

	/**
	 * Sets the prg activity id.
	 *
	 * @param prgActivityId            the prgActivityId to set
	 */
	public void setPrgActivityId(Long prgActivityId) {
		this.prgActivityId = prgActivityId;
	}

	/**
	 * Gets the financing type code.
	 *
	 * @return the financingTypeCode
	 */
	public String getFinancingTypeCode() {
		return financingTypeCode;
	}

	/**
	 * Sets the financing type code.
	 *
	 * @param financingTypeCode            the financingTypeCode to set
	 */
	public void setFinancingTypeCode(String financingTypeCode) {
		this.financingTypeCode = financingTypeCode;
	}

	/**
	 * Gets the adjustment doc no.
	 *
	 * @return the adjustment doc no
	 */
	public String getAdjustmentDocNo() {
		return adjustmentDocNo;
	}

	/**
	 * Sets the adjustment doc no.
	 *
	 * @param adjustmentDocNo the new adjustment doc no
	 */
	public void setAdjustmentDocNo(String adjustmentDocNo) {
		this.adjustmentDocNo = adjustmentDocNo;
	}

	/**
	 * Gets the stop instr no.
	 *
	 * @return the stop instr no
	 */
	public String getStopInstrNo() {
		return stopInstrNo;
	}

	/**
	 * Sets the stop instr no.
	 *
	 * @param stopInstrNo the new stop instr no
	 */
	public void setStopInstrNo(String stopInstrNo) {
		this.stopInstrNo = stopInstrNo;
	}

	/**
	 * Gets the selected deductions.
	 *
	 * @return the selectedDeductions
	 */
	public List<FlPaymentDeduction> getSelectedDeductions() {
		return selectedDeductions;
	}

	/**
	 * Sets the selected deductions.
	 *
	 * @param selectedDeductions            the selectedDeductions to set
	 */
	public void setSelectedDeductions(
			List<FlPaymentDeduction> selectedDeductions) {
		this.selectedDeductions = selectedDeductions;
	}

	/**
	 * Gets the deduction id.
	 *
	 * @return the deductionId
	 */
	public Long getDeductionId() {
		return deductionId;
	}

	/**
	 * Sets the deduction id.
	 *
	 * @param deductionId            the deductionId to set
	 */
	public void setDeductionId(Long deductionId) {
		this.deductionId = deductionId;
	}

	/**
	 * Gets the ct deduction.
	 *
	 * @return the ctDeduction
	 */
	public CtDeduction getCtDeduction() {
		return ctDeduction;
	}

	/**
	 * Sets the ct deduction.
	 *
	 * @param ctDeduction            the ctDeduction to set
	 */
	public void setCtDeduction(CtDeduction ctDeduction) {
		this.ctDeduction = ctDeduction;
	}

	/**
	 * Gets the ct deduction clause list.
	 *
	 * @return the ctDeductionClauseList
	 */
	public List<CtDeductionClause> getCtDeductionClauseList() {
		return ctDeductionClauseList;
	}

	/**
	 * Sets the ct deduction clause list.
	 *
	 * @param ctDeductionClauseList            the ctDeductionClauseList to set
	 */
	public void setCtDeductionClauseList(
			List<CtDeductionClause> ctDeductionClauseList) {
		this.ctDeductionClauseList = ctDeductionClauseList;
	}

	/**
	 * Gets the gl account list.
	 *
	 * @return the glAccountList
	 */
	public List<PmGlAccount> getGlAccountList() {
		return glAccountList;
	}

	/**
	 * Sets the gl account list.
	 *
	 * @param glAccountList            the glAccountList to set
	 */
	public void setGlAccountList(List<PmGlAccount> glAccountList) {
		this.glAccountList = glAccountList;
	}

	/**
	 * Gets the vot fund list.
	 *
	 * @return the votFundList
	 */
	public List<PmVotFund> getVotFundList() {
		return votFundList;
	}

	/**
	 * Sets the vot fund list.
	 *
	 * @param votFundList            the votFundList to set
	 */
	public void setVotFundList(List<PmVotFund> votFundList) {
		this.votFundList = votFundList;
	}

	/**
	 * Gets the adjustment dan doc no.
	 *
	 * @return the adjustmentDanDocNo
	 */
	public String getAdjustmentDanDocNo() {
		return adjustmentDanDocNo;
	}

	/**
	 * Gets the adjustment can doc no.
	 *
	 * @return the adjustmentCanDocNo
	 */
	public String getAdjustmentCanDocNo() {
		return adjustmentCanDocNo;
	}

	/**
	 * Sets the adjustment dan doc no.
	 *
	 * @param adjustmentDanDocNo            the adjustmentDanDocNo to set
	 */
	public void setAdjustmentDanDocNo(String adjustmentDanDocNo) {
		this.adjustmentDanDocNo = adjustmentDanDocNo;
	}

	/**
	 * Sets the adjustment can doc no.
	 *
	 * @param adjustmentCanDocNo            the adjustmentCanDocNo to set
	 */
	public void setAdjustmentCanDocNo(String adjustmentCanDocNo) {
		this.adjustmentCanDocNo = adjustmentCanDocNo;
	}

	/**
	 * Gets the fee vot fund.
	 *
	 * @return the feeVotFund
	 */
	public PmVotFund getFeeVotFund() {
		return feeVotFund;
	}

	/**
	 * Sets the fee vot fund.
	 *
	 * @param feeVotFund            the feeVotFund to set
	 */
	public void setFeeVotFund(PmVotFund feeVotFund) {
		this.feeVotFund = feeVotFund;
	}

	/**
	 * Gets the pm digi sign.
	 *
	 * @return the pm digi sign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * Sets the pm digi sign.
	 *
	 * @param pmDigiSign the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Checks if is auto update.
	 *
	 * @return true, if is auto update
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * Sets the auto update.
	 *
	 * @param autoUpdate the new auto update
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * Gets the button action.
	 *
	 * @return the button action
	 */
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * Sets the button action.
	 *
	 * @param buttonAction the new button action
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	/**
	 * Gets the text before sign.
	 *
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.
	 *
	 * @param textBeforeSign the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * Gets the ct deduction list.
	 *
	 * @return the ctDeductionList
	 */
	public List<CtDeduction> getCtDeductionList() {
		return ctDeductionList;
	}

	/**
	 * Sets the ct deduction list.
	 *
	 * @param ctDeductionList the ctDeductionList to set
	 */
	public void setCtDeductionList(List<CtDeduction> ctDeductionList) {
		this.ctDeductionList = ctDeductionList;
	}

	/**
	 * Gets the populate deduction list.
	 *
	 * @return the populateDeductionList
	 */
	public List<SelectItem> getPopulateDeductionList() {
		if(populateDeductionList == null){
			populateDeductionList = new ArrayList<SelectItem>();
		}
		return populateDeductionList;
	}

	/**
	 * Sets the populate deduction list.
	 *
	 * @param populateDeductionList the populateDeductionList to set
	 */
	public void setPopulateDeductionList(List<SelectItem> populateDeductionList) {
		this.populateDeductionList = populateDeductionList;
	}

	/**
	 * Checks if is validated.
	 *
	 * @return true, if is validated
	 */
	public boolean isValidated() {
		return validated;
	}

	/**
	 * Sets the validated.
	 *
	 * @param validated the new validated
	 */
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * Checks if is have advanced pymt.
	 *
	 * @return the haveAdvancedPymt
	 */
	public boolean isHaveAdvancedPymt() {
		return haveAdvancedPymt;
	}

	/**
	 * Sets the have advanced pymt.
	 *
	 * @param haveAdvancedPymt the haveAdvancedPymt to set
	 */
	public void setHaveAdvancedPymt(boolean haveAdvancedPymt) {
		this.haveAdvancedPymt = haveAdvancedPymt;
	}

	/**
	 * Gets the paid amt.
	 *
	 * @return the paidAmt
	 */
	public BigDecimal getPaidAmt() {
		return paidAmt;
	}

	/**
	 * Sets the paid amt.
	 *
	 * @param paidAmt the paidAmt to set
	 */
	public void setPaidAmt(BigDecimal paidAmt) {
		this.paidAmt = paidAmt;
	}

	/**
	 * Gets the balance amt.
	 *
	 * @return the balanceAmt
	 */
	public BigDecimal getBalanceAmt() {
		return balanceAmt;
	}

	/**
	 * Sets the balance amt.
	 *
	 * @param balanceAmt the balanceAmt to set
	 */
	public void setBalanceAmt(BigDecimal balanceAmt) {
		this.balanceAmt = balanceAmt;
	}

	/**
	 * Gets the paid perc.
	 *
	 * @return the paidPerc
	 */
	public BigDecimal getPaidPerc() {
		return paidPerc;
	}

	/**
	 * Sets the paid perc.
	 *
	 * @param paidPerc the paidPerc to set
	 */
	public void setPaidPerc(BigDecimal paidPerc) {
		this.paidPerc = paidPerc;
	}

	/**
	 * Gets the number of co.
	 *
	 * @return the numberOfCO
	 */
	public int getNumberOfCO() {
		return numberOfCO;
	}

	/**
	 * Sets the number of co.
	 *
	 * @param numberOfCO the numberOfCO to set
	 */
	public void setNumberOfCO(int numberOfCO) {
		this.numberOfCO = numberOfCO;
	}

	/**
	 * Checks if is show deduction tab.
	 *
	 * @return the showDeductionTab
	 */
	public boolean isShowDeductionTab() {
		return showDeductionTab;
	}

	/**
	 * Sets the show deduction tab.
	 *
	 * @param showDeductionTab the showDeductionTab to set
	 */
	public void setShowDeductionTab(boolean showDeductionTab) {
		this.showDeductionTab = showDeductionTab;
	}

	/**
	 * Gets the can amt grand total.
	 *
	 * @return the canAmtGrandTotal
	 */
	public BigDecimal getCanAmtGrandTotal() {
		return canAmtGrandTotal;
	}

	/**
	 * Gets the dan amt grand total.
	 *
	 * @return the danAmtGrandTotal
	 */
	public BigDecimal getDanAmtGrandTotal() {
		return danAmtGrandTotal;
	}

	/**
	 * Sets the can amt grand total.
	 *
	 * @param canAmtGrandTotal the canAmtGrandTotal to set
	 */
	public void setCanAmtGrandTotal(BigDecimal canAmtGrandTotal) {
		this.canAmtGrandTotal = canAmtGrandTotal;
	}

	/**
	 * Sets the dan amt grand total.
	 *
	 * @param danAmtGrandTotal the danAmtGrandTotal to set
	 */
	public void setDanAmtGrandTotal(BigDecimal danAmtGrandTotal) {
		this.danAmtGrandTotal = danAmtGrandTotal;
	}

	/**
	 * Gets the invoice date.
	 *
	 * @return the invoiceDate
	 */
	public String getInvoiceDate() {
		return invoiceDate;
	}

	/**
	 * Sets the invoice date.
	 *
	 * @param newDate the invoiceDate to set
	 */
	public void setInvoiceDate(String newDate) {
		this.invoiceDate = newDate;
	}

	/**
	 * @return the delOrderList
	 */
	public List<FlDeliveryOrder> getDelOrderList() {
		return delOrderList;
	}

	/**
	 * @param delOrderList the delOrderList to set
	 */
	public void setDelOrderList(List<FlDeliveryOrder> delOrderList) {
		this.delOrderList = delOrderList;
	}

	/**
	 * @return the fulfilmentNoteList
	 */
	public List<FlFulfilmentNote> getFulfilmentNoteList() {
		return fulfilmentNoteList;
	}

	/**
	 * @param fulfilmentNoteList the fulfilmentNoteList to set
	 */
	public void setFulfilmentNoteList(List<FlFulfilmentNote> fulfilmentNoteList) {
		this.fulfilmentNoteList = fulfilmentNoteList;
	}

	/**
	 * @return the gfmasError
	 */
	public boolean isGfmasError() {
		return gfmasError;
	}

	/**
	 * @param gfmasError the gfmasError to set
	 */
	public void setGfmasError(boolean gfmasError) {
		this.gfmasError = gfmasError;
	}

	/**
	 * @return the gfmasErrorContent
	 */
	public String getGfmasErrorContent() {
		return gfmasErrorContent;
	}

	/**
	 * @param gfmasErrorContent the gfmasErrorContent to set
	 */
	public void setGfmasErrorContent(String gfmasErrorContent) {
		this.gfmasErrorContent = gfmasErrorContent;
	}

	/**
	 * @return the gstAmount
	 */
	public BigDecimal getGstAmount() {
		return gstAmount;
	}

	/**
	 * @param gstAmount the gstAmount to set
	 */
	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}

	/**
	 * @return the invoiceAmountWithoutGST
	 */
	public BigDecimal getInvoiceAmountWithoutGST() {
		return invoiceAmountWithoutGST;
	}

	/**
	 * @param invoiceAmountWithoutGST the invoiceAmountWithoutGST to set
	 */
	public void setInvoiceAmountWithoutGST(BigDecimal invoiceAmountWithoutGST) {
		this.invoiceAmountWithoutGST = invoiceAmountWithoutGST;
	}

	/**
	 * @return the taxInvoiceList
	 */
	public List<TaxInvoiceVO> getTaxInvoiceList() {
		return taxInvoiceList;
	}

	/**
	 * @param taxInvoiceList the taxInvoiceList to set
	 */
	public void setTaxInvoiceList(List<TaxInvoiceVO> taxInvoiceList) {
		this.taxInvoiceList = taxInvoiceList;
	}

	/**
	 * @return the hyperLinkPage
	 */
	public boolean isHyperLinkPage() {
		return hyperLinkPage;
	}

	/**
	 * @param hyperLinkPage the hyperLinkPage to set
	 */
	public void setHyperLinkPage(boolean hyperLinkPage) {
		this.hyperLinkPage = hyperLinkPage;
	}

	/**
	 * @return the taxInvoice
	 */
	public boolean isTaxInvoice() {
		return taxInvoice;
	}

	/**
	 * @param taxInvoice the taxInvoice to set
	 */
	public void setTaxInvoice(boolean taxInvoice) {
		this.taxInvoice = taxInvoice;
	}

	public String getGfmasErrorCode() {
		return gfmasErrorCode;
	}

	public void setGfmasErrorCode(String gfmasErrorCode) {
		this.gfmasErrorCode = gfmasErrorCode;
	}

	public String getGfmasErrorDesc() {
		return gfmasErrorDesc;
	}

	public void setGfmasErrorDesc(String gfmasErrorDesc) {
		this.gfmasErrorDesc = gfmasErrorDesc;
	}
	
	public Map<Long, CtDeduction> getCtDeductionMap() {
		return ctDeductionMap;
	}

	public void setCtDeductionMap(Map<Long, CtDeduction> ctDeductionMap) {
		this.ctDeductionMap = ctDeductionMap;
	}

	public Long getSelectedVotFundId() {
		return selectedVotFundId;
	}

	public void setSelectedVotFundId(Long selectedVotFundId) {
		this.selectedVotFundId = selectedVotFundId;
	}

	public VotSearchBean getVotSearchBean() {
		return votSearchBean;
	}

	public void setVotSearchBean(VotSearchBean votSearchBean) {
		this.votSearchBean = votSearchBean;
	}

	public Long getSelectedVotValue() {
		return selectedVotValue;
	}

	public void setSelectedVotValue(Long selectedVotValue) {
		this.selectedVotValue = selectedVotValue;
	}

	public String getSearchVotField() {
		return searchVotField;
	}

	public void setSearchVotField(String searchVotField) {
		this.searchVotField = searchVotField;
	}

	public void setSelectedFundObj(Object[] selectedFundObj) {
		this.selectedFundObj = selectedFundObj;
	}

	public void setSelectedVotObj(Object[] selectedVotObj) {
		this.selectedVotObj = selectedVotObj;
	}

	public Object[] getSelectedFundObj() {
		return selectedFundObj;
	}

	public Object[] getSelectedVotObj() {
		return selectedVotObj;
	}

	public FlPaymentDeduction getSelectedDeduction() {
		return selectedDeduction;
	}

	public void setSelectedDeduction(FlPaymentDeduction selectedDeduction) {
		this.selectedDeduction = selectedDeduction;
	}

	public List<SelectItem> getPopulateSubSetiaList() {
		if(populateSubSetiaList == null){
			populateSubSetiaList = new ArrayList<SelectItem>();
		}
		return populateSubSetiaList;
	}

	public void setPopulateSubSetiaList(List<SelectItem> populateSubSetiaList) {
		this.populateSubSetiaList = populateSubSetiaList;
	}

	//start 21229
	public Long getMinistryProfileId() {
		return ministryProfileId;
	}

	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}

	public Long getJabatanProfileId() {
		return jabatanProfileId;
	}

	public void setJabatanProfileId(Long jabatanProfileId) {
		this.jabatanProfileId = jabatanProfileId;
	}

	public String getPtjCodeName() {
		return ptjCodeName;
	}

	public void setPtjCodeName(String ptjCodeName) {
		this.ptjCodeName = ptjCodeName;
	}

	public String getPtjCodeNameSearchValues() {
		return ptjCodeNameSearchValues;
	}

	public void setPtjCodeNameSearchValues(String ptjCodeNameSearchValues) {
		this.ptjCodeNameSearchValues = ptjCodeNameSearchValues;
	}

	public PmOrgProfile getSelectedPmOrgProfile() {
		return selectedPmOrgProfile;
	}

	public void setSelectedPmOrgProfile(PmOrgProfile selectedPmOrgProfile) {
		this.selectedPmOrgProfile = selectedPmOrgProfile;
	}

	public List<FlRemark> getRemarkReviseList() {
		return remarkReviseList;
	}

	public void setRemarkReviseList(List<FlRemark> remarkReviseList) {
		this.remarkReviseList = remarkReviseList;
	}

	public boolean isHideReviseRemarks() {
		return hideReviseRemarks;
	}

	public void setHideReviseRemarks(boolean hideReviseRemarks) {
		this.hideReviseRemarks = hideReviseRemarks;
	}

	public boolean isQueryDisplay() {
		return queryDisplay;
	}

	public void setQueryDisplay(boolean queryDisplay) {
		this.queryDisplay = queryDisplay;
	}

	public String getQueryPaymentDesc() {
		return queryPaymentDesc;
	}

	public void setQueryPaymentDesc(String queryPaymentDesc) {
		this.queryPaymentDesc = queryPaymentDesc;
	}
	
	//end 21229
	//26279 - start
	public String getDisciplinaryActionMessage() {
		return disciplinaryActionMessage;
	}

	public void setDisciplinaryActionMessage(String disciplinaryActionMessage) {
		this.disciplinaryActionMessage = disciplinaryActionMessage;
	}	

	public List<DisciplinaryActionVO> getDisciplineVO() {
		return disciplineVO;
	}

	public void setDisciplineVO(List<DisciplinaryActionVO> disciplineVO) {
		this.disciplineVO = disciplineVO;
	}

	public SmDisciplinaryAction getSmDisciplinaryAction() {
		return smDisciplinaryAction;
	}

	public void setSmDisciplinaryAction(SmDisciplinaryAction smDisciplinaryAction) {
		this.smDisciplinaryAction = smDisciplinaryAction;
	}
	//26279 - end
	//24893 - start
	public PmOrgProfile getChargePTJOrg() {
		return chargePTJOrg;
	}

	public void setChargePTJOrg(PmOrgProfile chargePTJOrg) {
		this.chargePTJOrg = chargePTJOrg;
	}
	//24893 - end
	//28221 - start


	public PmUser getSuppInvoiceSubmitted() {
		return suppInvoiceSubmitted;
	}

	public boolean isSuppInvoiceActive() {
		return suppInvoiceActive;
	}

	public void setSuppInvoiceActive(boolean suppInvoiceActive) {
		this.suppInvoiceActive = suppInvoiceActive;
	}

	public void setSuppInvoiceSubmitted(PmUser suppInvoiceSubmitted) {
		this.suppInvoiceSubmitted = suppInvoiceSubmitted;
	}

	public Long getNewSupplierUserId() {
		return newSupplierUserId;
	}

	public void setNewSupplierUserId(Long newSupplierUserId) {
		this.newSupplierUserId = newSupplierUserId;
	}
	
	//28221 - end
	
	//24956 - start
	public boolean isCtTypeMinistry() {
		return CtTypeMinistry;
	}

	public void setCtTypeMinistry(boolean ctTypeMinistry) {
		CtTypeMinistry = ctTypeMinistry;
	}
	//24956 - end

	//29551 - start
	/**
	 * @return the isShowDeductionClause
	 */
	public boolean isShowDeductionClause() {
		return showDeductionClause;
	}

	/**
	 * @param showDeductionClause the isShowDeductionClause to set
	 */
	public void setShowDeductionClause(boolean showDeductionClause) {
		this.showDeductionClause = showDeductionClause;
	}

	/**
	 * @return the selectedDeductionClauseParent
	 */
	public Long getSelectedDeductionClauseParent() {
		return selectedDeductionClauseParent;
	}

	/**
	 * @param selectedDeductionClauseParent the selectedDeductionClauseParent to set
	 */
	public void setSelectedDeductionClauseParent(Long selectedDeductionClauseParent) {
		this.selectedDeductionClauseParent = selectedDeductionClauseParent;
	}
	//29551 - end
	
	// 32678 - start
	/**
	 * @return the backToPrevious
	 */
	public String getBackToPrevious() {
		return backToPrevious;
	}

	/**
	 * @param backToPrevious the backToPrevious to set
	 */
	public void setBackToPrevious(String backToPrevious) {
		this.backToPrevious = backToPrevious;
	}

	/**
	 * @return the docMaintain
	 */
	public boolean isDocMaintain() {
		return docMaintain;
	}

	/**
	 * @param docMaintain the docMaintain to set
	 */
	public void setDocMaintain(boolean docMaintain) {
		this.docMaintain = docMaintain;
	}
	
	public List<FlRemark> getPreviousRemarkList() {
		return previousRemarkList;
	}

	public void setPreviousRemarkList(List<FlRemark> previousRemarkList) {
		this.previousRemarkList = previousRemarkList;
	}

	public List<FlAttachment> getPreviousAttachmentList() {
		return previousAttachmentList;
	}

	public void setPreviousAttachmentList(List<FlAttachment> previousAttachmentList) {
		this.previousAttachmentList = previousAttachmentList;
	}
	
	public boolean isPrevHideRemarks() {
		return prevHideRemarks;
	}

	public void setPrevHideRemarks(boolean prevHideRemarks) {
		this.prevHideRemarks = prevHideRemarks;
	}
	
	//32678-End


}
