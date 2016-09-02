/**
 * 
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
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.persistence.entity.fl.payment.FlPaymentDeduction;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.TaxInvoiceVO;

import org.primefaces.model.TreeNode;

// TODO: Auto-generated Javadoc
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

	/** The payment advice. */
	private FlPaymentAdvice paymentAdvice;
	
	/** The selected deductions. */
	private List<FlPaymentDeduction> selectedDeductions;
	
	/** The ct deduction. */
	private CtDeduction ctDeduction;
	
	/** The ct deduction clause list. */
	private List<CtDeductionClause> ctDeductionClauseList;
	
	/** The gl account list. */
	private List<PmGlAccount> glAccountList;
	
	/** The vot fund list. */
	private List<PmVotFund> votFundList;
	
	/** The fee vot fund. */
	private PmVotFund feeVotFund;
	
	/** The ct deduction list. */
	private List<CtDeduction> ctDeductionList;
	
	/** The populate deduction list. */
	private List<SelectItem> populateDeductionList;

	// for Deduction panel display
	/** The doc type. */
	private String docType;

	/** The charge line item root. */
	private TreeNode chargeLineItemRoot;
	
	/** The selected node. */
	private TreeNode selectedNode;

	// set chargeline for selected items
	/** The vot fund code. */
	private String votFundCode;
	
	/** The asset ind code. */
	private String assetIndCode;
	
	/** The gl account code. */
	private String glAccountCode;
	
	/** The project code. */
	private String projectCode;
	
	/** The setia code. */
	private String setiaCode;
	
	/** The sub setia code. */
	private String subSetiaCode;
	
	/** The prg activity code. */
	private String prgActivityCode;
	
	/** The vot fund type. */
	private String votFundType;

	/** The vot fund id. */
	private Long votFundId;
	
	/** The asset ind id. */
	private Long assetIndId;
	
	/** The gl account id. */
	private Long glAccountId;
	
	/** The project id. */
	private Long projectId;
	
	/** The setia id. */
	private Long setiaId;
	
	/** The sub setia id. */
	private Long subSetiaId;
	
	/** The prg activity id. */
	private Long prgActivityId;
	
	/** The financing type code. */
	private String financingTypeCode;
	
	/** The deduction id. */
	private Long deductionId;
	
	/** The invoice date. */
	private String invoiceDate;

	/** The fulfilment request. */
	private FlFulfilmentRequest fulfilmentRequest;

	/** The ordered amt grand total. */
	private BigDecimal orderedAmtGrandTotal;
	
	/** The received amt grand total. */
	private BigDecimal receivedAmtGrandTotal;
	
	/** The inv amt grand total. */
	private BigDecimal invAmtGrandTotal;
	
	/** The can amt grand total. */
	private BigDecimal canAmtGrandTotal;
	
	/** The dan amt grand total. */
	private BigDecimal danAmtGrandTotal;
	
	/** The service charge. */
	private String serviceCharge;

	/** The deduction amt. */
	private BigDecimal deductionAmt;
	
	/** The have advanced pymt. */
	private boolean haveAdvancedPymt;
	
	/** The paid amt. */
	private BigDecimal paidAmt;
	
	/** The balance amt. */
	private BigDecimal balanceAmt;
	
	/** The paid perc. */
	private BigDecimal paidPerc;
	
	/** The number of co. */
	private int numberOfCO;
	
	/** The show deduction tab. */
	private boolean showDeductionTab;

	/** The button val. */
	private String buttonVal;
	
	/** The pm user. */
	private PmUser pmUser;

	// getSuplier address details
	/** The supplier address. */
	private String supplierAddress;
	
	/** The phone no. */
	private String phoneNo;
	
	/** The fax no. */
	private String faxNo;
	
	/** The contact email. */
	private String contactEmail;
	
	/** The contact person. */
	private String contactPerson;
	
	/** The contact phone no. */
	private String contactPhoneNo;

	// tab menu
	/** The selected tab index. */
	private int selectedTabIndex;

	/** The adjustment doc no. */
	private String adjustmentDocNo;
	
	/** The adjustment dan doc no. */
	private String adjustmentDanDocNo;
	
	/** The adjustment can doc no. */
	private String adjustmentCanDocNo;
	
	/** The stop instr no. */
	private String stopInstrNo;
	
	//gpki signing
	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;
	
	/** The auto update. */
	private boolean autoUpdate = true;
	
	/** The text before sign. */
	private String textBeforeSign;
	
	/** The button action. */
	private String buttonAction;
	
	/** The validated. */
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
}
