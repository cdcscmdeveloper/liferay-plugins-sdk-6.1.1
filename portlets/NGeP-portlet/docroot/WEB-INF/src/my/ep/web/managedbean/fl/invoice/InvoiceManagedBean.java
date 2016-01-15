/**
 * 
 */
package my.ep.web.managedbean.fl.invoice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.payment.FlInvoiceItemSummary;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.TaxInvoiceVO;

import org.primefaces.model.TreeNode;

// TODO: Auto-generated Javadoc
/**
 * Managed Bean for invoice.
 *
 * @author KK LIM
 * @since 30/10/2013
 */
@ManagedBean(name = "invMB")
@SessionScoped
public class InvoiceManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The invoice. */
	private FlInvoice invoice;
	
	/** The inv item. */
	private FlInvoiceItem invItem = new FlInvoiceItem();
	
	/** The Doc process. */
	private String DocProcess;
	
	/** The button val. */
	private String buttonVal;
	
	/** The contract no. */
	private String contractNo;
	
	/** The loa no. */
	private String loaNo;
	
	/** The title. */
	private String title;
	
	private String physicalLoaNo;
	
	private String physicalContractNo;
	
	/** The is dan. */
	private boolean isDan = false;
	
	/** The is update dan. */
	private boolean isUpdateDan = false;
	
	/** The sum ordered amt. */
	private BigDecimal sumOrderedAmt;
	
	/** The exp date. */
	private String expDate;
	
	/** The eff date. */
	private String effDate;
	
	/** The status id. */
	private Long statusId;
	
	/** The factoring check. */
	private String factoringCheck="false";
	
	/** The doc type. */
	private String docType;
	
	/** The supplier bank account map. */
	private Map<String, SmSupplierBank> supplierBankAccountMap;
	
	/** The decision list. */
	private List<PmStatusDesc>decisionList;
	
	// Inv Items
	/** The charge line item root. */
	private TreeNode chargeLineItemRoot ;
	private TreeNode outOfScopeSectionItemRoot ;
	private TreeNode taxSectionItemRoot ;
	
	/** The charge line selected item address. */
	private FlFulfilmentItemAddr chargeLineSelectedItemAddress;
	
	/** The selected nodes. */
	private TreeNode[] selectedNodes;
	
	/** The selected node. */
	private TreeNode selectedNode;
	
	/** The selected item charge section. */
	private FlFulfilmentItemAddr selectedItemChargeSection;
	
	//grand total
	/** The ordered amt grand total. */
	private BigDecimal orderedAmtGrandTotal ;
	
	/** The received amt grand total. */
	private BigDecimal receivedAmtGrandTotal ;
	
	/** The inv amt grand total. */
	private BigDecimal invAmtGrandTotal ;
	
	private BigDecimal oosReceivedAmtGrandTotal ;
	
	private BigDecimal oosInvAmtGrandTotal ;
	
	/** The service charge. */
	private String serviceCharge ;
	
	//tab menu
	/** The selected tab index. */
	private int selectedTabIndex;
	
	/** The pm user. */
	private PmUser pmUser;
	
	//getSuplier address details
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
	
	/** The modification cancel. */
	private boolean modificationCancel;
	
	/** The doc maintain remarks. */
	private boolean docMaintainRemarks = false;
	
	/** The account no name. */
	private String accountNoName;
	
	/** The fulfilment request. */
	private FlFulfilmentRequest fulfilmentRequest;
	
	//gpki signing
	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;
	
	/** The auto update. */
	private boolean autoUpdate = true;
	
	/** The text before sign. */
	private String textBeforeSign;
	
	/** The button action. */
	private String buttonAction;
	
	/** The invoice id. */
	private Long invoiceId;
	
	/** The back to document. */
	private String backToDocument;
	
	/** The spki backing bean. */
    @ManagedProperty(value="#{spkiBackingBean}")
    private SpkiBackingBean spkiBackingBean;
    
    private boolean reliefDeclarationCheck = false;
    
    private FlInvoiceItemSummary invItemSummary;
    
    private boolean taxItemTableClause = false;
    private boolean oosItemTableClause = false;
	
	private BigDecimal gstAmount;
	private BigDecimal invoiceAmountWithoutGST;
	private List<TaxInvoiceVO> taxInvoiceList;
	
	private List<FlDeliveryOrder> delOrderList;
	
	private boolean hyperLinkPage = false;
	
	private boolean docMaintain = false;
	
	private String backToPrevious;
	private boolean isTaxInvoice = false;
	
	private boolean isTaxable=false;
	
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
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}
	
	/**
	 * Gets the invoice.
	 *
	 * @return the invoice
	 */
	public FlInvoice getInvoice() {
		return invoice;
	}
	
	/**
	 * Sets the invoice.
	 *
	 * @param invoice the invoice to set
	 */
	public void setInvoice(FlInvoice invoice) {
		this.invoice = invoice;
	}
	
	/**
	 * Gets the doc process.
	 *
	 * @return the docProcess
	 */
	public String getDocProcess() {
		return DocProcess;
	}
	
	/**
	 * Sets the doc process.
	 *
	 * @param docProcess the docProcess to set
	 */
	public void setDocProcess(String docProcess) {
		DocProcess = docProcess;
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
	 * @param buttonVal the buttonVal to set
	 */
	public void setButtonVal(String buttonVal) {
		this.buttonVal = buttonVal;
	}
	
	/**
	 * Checks if is dan.
	 *
	 * @return the isDan
	 */
	public boolean isDan() {
		return isDan;
	}
	
	/**
	 * Sets the dan.
	 *
	 * @param isDan the isDan to set
	 */
	public void setDan(boolean isDan) {
		this.isDan = isDan;
	}
	
	/**
	 * Checks if is update dan.
	 *
	 * @return the isUpdateDan
	 */
	public boolean isUpdateDan() {
		return isUpdateDan;
	}
	
	/**
	 * Sets the update dan.
	 *
	 * @param isUpdateDan the isUpdateDan to set
	 */
	public void setUpdateDan(boolean isUpdateDan) {
		this.isUpdateDan = isUpdateDan;
	}
	
	/**
	 * Gets the factoring check.
	 *
	 * @return the factoringCheck
	 */
	public String getFactoringCheck() {
		return factoringCheck;
	}
	
	/**
	 * Sets the factoring check.
	 *
	 * @param factoringCheck the factoringCheck to set
	 */
	public void setFactoringCheck(String factoringCheck) {
		this.factoringCheck = factoringCheck;
	}
	
	/**
	 * Gets the inv item.
	 *
	 * @return the invItem
	 */
	public FlInvoiceItem getInvItem() {
		return invItem;
	}
	
	/**
	 * Sets the inv item.
	 *
	 * @param invItem the invItem to set
	 */
	public void setInvItem(FlInvoiceItem invItem) {
		this.invItem = invItem;
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
	 * @param chargeLineItemRoot the chargeLineItemRoot to set
	 */
	public void setChargeLineItemRoot(TreeNode chargeLineItemRoot) {
		this.chargeLineItemRoot = chargeLineItemRoot;
	}
	
	/**
	 * Gets the charge line selected item address.
	 *
	 * @return the chargeLineSelectedItemAddress
	 */
	public FlFulfilmentItemAddr getChargeLineSelectedItemAddress() {
		return chargeLineSelectedItemAddress;
	}
	
	/**
	 * Sets the charge line selected item address.
	 *
	 * @param chargeLineSelectedItemAddress the chargeLineSelectedItemAddress to set
	 */
	public void setChargeLineSelectedItemAddress(
			FlFulfilmentItemAddr chargeLineSelectedItemAddress) {
		this.chargeLineSelectedItemAddress = chargeLineSelectedItemAddress;
	}
	
	/**
	 * Gets the selected nodes.
	 *
	 * @return the selectedNodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}
	
	/**
	 * Sets the selected nodes.
	 *
	 * @param selectedNodes the selectedNodes to set
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
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
	 * @param selectedNode the selectedNode to set
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	
	/**
	 * Gets the selected item charge section.
	 *
	 * @return the selectedItemChargeSection
	 */
	public FlFulfilmentItemAddr getSelectedItemChargeSection() {
		return selectedItemChargeSection;
	}
	
	/**
	 * Sets the selected item charge section.
	 *
	 * @param selectedItemChargeSection the selectedItemChargeSection to set
	 */
	public void setSelectedItemChargeSection(
			FlFulfilmentItemAddr selectedItemChargeSection) {
		this.selectedItemChargeSection = selectedItemChargeSection;
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
	 * @param orderedAmtGrandTotal the orderedAmtGrandTotal to set
	 */
	public void setOrderedAmtGrandTotal(BigDecimal orderedAmtGrandTotal) {
		this.orderedAmtGrandTotal = orderedAmtGrandTotal;
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
	 * @param fulfilmentRequest the fulfilmentRequest to set
	 */
	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
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
	 * @param receivedAmtGrandTotal the receivedAmtGrandTotal to set
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
	 * @param invAmtGrandTotal the invAmtGrandTotal to set
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
	 * @param serviceCharge the serviceCharge to set
	 */
	public void setServiceCharge(String serviceCharge) {
		this.serviceCharge = serviceCharge;
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
	 * @param selectedTabIndex the selectedTabIndex to set
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
	 * @param pmUser the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}
	
	/**
	 * Gets the supplier bank account map.
	 *
	 * @return the supplierBankAccountMap
	 */
	public Map<String, SmSupplierBank> getSupplierBankAccountMap() {
		if(supplierBankAccountMap==null) {
			supplierBankAccountMap = new HashMap<String, SmSupplierBank>();
		}
		return supplierBankAccountMap;
	}
	
	/**
	 * Sets the supplier bank account map.
	 *
	 * @param supplierBankAccountMap the supplierBankAccountMap to set
	 */
	public void setSupplierBankAccountMap(
			Map<String, SmSupplierBank> supplierBankAccountMap) {
		this.supplierBankAccountMap = supplierBankAccountMap;
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
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
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
	 * @param phoneNo the phoneNo to set
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
	 * @param faxNo the faxNo to set
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
	 * @param contactEmail the contactEmail to set
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
	 * @param contactPerson the contactPerson to set
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
	 * @param contactPhoneNo the contactPhoneNo to set
	 */
	public void setContactPhoneNo(String contactPhoneNo) {
		this.contactPhoneNo = contactPhoneNo;
	}

	/**
	 * Gets the contract no.
	 *
	 * @return the contractNo
	 */
	public String getContractNo() {
		return contractNo;
	}

	/**
	 * Sets the contract no.
	 *
	 * @param contractNo the contractNo to set
	 */
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	/**
	 * Gets the loa no.
	 *
	 * @return the loaNo
	 */
	public String getLoaNo() {
		return loaNo;
	}

	/**
	 * Sets the loa no.
	 *
	 * @param loaNo the loaNo to set
	 */
	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Checks if is modification cancel.
	 *
	 * @return the modificationCancel
	 */
	public boolean isModificationCancel() {
		return modificationCancel;
	}

	/**
	 * Sets the modification cancel.
	 *
	 * @param modificationCancel the modificationCancel to set
	 */
	public void setModificationCancel(boolean modificationCancel) {
		this.modificationCancel = modificationCancel;
	}

	/**
	 * Checks if is doc maintain remarks.
	 *
	 * @return the docMaintainRemarks
	 */
	public boolean isDocMaintainRemarks() {
		return docMaintainRemarks;
	}

	/**
	 * Sets the doc maintain remarks.
	 *
	 * @param docMaintainRemarks the docMaintainRemarks to set
	 */
	public void setDocMaintainRemarks(boolean docMaintainRemarks) {
		this.docMaintainRemarks = docMaintainRemarks;
	}

	/**
	 * Gets the decision list.
	 *
	 * @return the decisionList
	 */
	public List<PmStatusDesc> getDecisionList() {
		return decisionList;
	}

	/**
	 * Sets the decision list.
	 *
	 * @param decisionList the decisionList to set
	 */
	public void setDecisionList(List<PmStatusDesc> decisionList) {
		this.decisionList = decisionList;
	}

	/**
	 * Gets the sum ordered amt.
	 *
	 * @return the sumOrderedAmt
	 */
	public BigDecimal getSumOrderedAmt() {
		return sumOrderedAmt;
	}

	/**
	 * Sets the sum ordered amt.
	 *
	 * @param sumOrderedAmt the sumOrderedAmt to set
	 */
	public void setSumOrderedAmt(BigDecimal sumOrderedAmt) {
		this.sumOrderedAmt = sumOrderedAmt;
	}

	/**
	 * Gets the exp date.
	 *
	 * @return the expDate
	 */
	public String getExpDate() {
		return expDate;
	}

	/**
	 * Sets the exp date.
	 *
	 * @param expDate the expDate to set
	 */
	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	/**
	 * Gets the eff date.
	 *
	 * @return the effDate
	 */
	public String getEffDate() {
		return effDate;
	}

	/**
	 * Sets the eff date.
	 *
	 * @param effDate the effDate to set
	 */
	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}

	/**
	 * Gets the status id.
	 *
	 * @return the statusId
	 */
	public Long getStatusId() {
		return statusId;
	}

	/**
	 * Sets the status id.
	 *
	 * @param statusId the statusId to set
	 */
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	/**
	 * Gets the account no name.
	 *
	 * @return the accountNoName
	 */
	public String getAccountNoName() {
		return accountNoName;
	}

	/**
	 * Sets the account no name.
	 *
	 * @param accountNoName the accountNoName to set
	 */
	public void setAccountNoName(String accountNoName) {
		this.accountNoName = accountNoName;
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
	 * Gets the invoice id.
	 *
	 * @return the invoice id
	 */
	public Long getInvoiceId() {
		return invoiceId;
	}
	
	/**
	 * Sets the invoice id.
	 *
	 * @param invoiceId the new invoice id
	 */
	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	/**
	 * Gets the back to document.
	 *
	 * @return the backToDocument
	 */
	public String getBackToDocument() {
		return backToDocument;
	}
	
	/**
	 * Sets the back to document.
	 *
	 * @param backToDocument the backToDocument to set
	 */
	public void setBackToDocument(String backToDocument) {
		this.backToDocument = backToDocument;
	}

	/**
	 * @return the spkiBackingBean
	 */
	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	/**
	 * @param spkiBackingBean the spkiBackingBean to set
	 */
	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	/**
	 * @return the reliefDeclarationCheck
	 */
	public boolean isReliefDeclarationCheck() {
		return reliefDeclarationCheck;
	}

	/**
	 * @param reliefDeclarationCheck the reliefDeclarationCheck to set
	 */
	public void setReliefDeclarationCheck(boolean reliefDeclarationCheck) {
		this.reliefDeclarationCheck = reliefDeclarationCheck;
	}

	/**
	 * @return the invItemSummary
	 */
	public FlInvoiceItemSummary getInvItemSummary() {
		return invItemSummary;
	}

	/**
	 * @param invItemSummary the invItemSummary to set
	 */
	public void setInvItemSummary(FlInvoiceItemSummary invItemSummary) {
		this.invItemSummary = invItemSummary;
	}

	public BigDecimal getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}

	public BigDecimal getInvoiceAmountWithoutGST() {
		return invoiceAmountWithoutGST;
	}

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

	/**
	 * @return the isTaxable
	 */
	public boolean isTaxable() {
		return isTaxable;
	}

	/**
	 * @param isTaxable the isTaxable to set
	 */
	public void setTaxable(boolean isTaxable) {
		this.isTaxable = isTaxable;
	}

	/**
	 * @return the outOfScopeSectionItemRoot
	 */
	public TreeNode getOutOfScopeSectionItemRoot() {
		return outOfScopeSectionItemRoot;
	}

	/**
	 * @param outOfScopeSectionItemRoot the outOfScopeSectionItemRoot to set
	 */
	public void setOutOfScopeSectionItemRoot(TreeNode outOfScopeSectionItemRoot) {
		this.outOfScopeSectionItemRoot = outOfScopeSectionItemRoot;
	}

	/**
	 * @return the taxSectionItemRoot
	 */
	public TreeNode getTaxSectionItemRoot() {
		return taxSectionItemRoot;
	}

	/**
	 * @param taxSectionItemRoot the taxSectionItemRoot to set
	 */
	public void setTaxSectionItemRoot(TreeNode taxSectionItemRoot) {
		this.taxSectionItemRoot = taxSectionItemRoot;
	}

	/**
	 * @return the oosReceivedAmtGrandTotal
	 */
	public BigDecimal getOosReceivedAmtGrandTotal() {
		return oosReceivedAmtGrandTotal;
	}

	/**
	 * @param oosReceivedAmtGrandTotal the oosReceivedAmtGrandTotal to set
	 */
	public void setOosReceivedAmtGrandTotal(BigDecimal oosReceivedAmtGrandTotal) {
		this.oosReceivedAmtGrandTotal = oosReceivedAmtGrandTotal;
	}

	/**
	 * @return the oosInvAmtGrandTotal
	 */
	public BigDecimal getOosInvAmtGrandTotal() {
		return oosInvAmtGrandTotal;
	}

	/**
	 * @param oosInvAmtGrandTotal the oosInvAmtGrandTotal to set
	 */
	public void setOosInvAmtGrandTotal(BigDecimal oosInvAmtGrandTotal) {
		this.oosInvAmtGrandTotal = oosInvAmtGrandTotal;
	}

	/**
	 * @return the taxItemTableClause
	 */
	public boolean isTaxItemTableClause() {
		return taxItemTableClause;
	}

	/**
	 * @param taxItemTableClause the taxItemTableClause to set
	 */
	public void setTaxItemTableClause(boolean taxItemTableClause) {
		this.taxItemTableClause = taxItemTableClause;
	}

	/**
	 * @return the oosItemTableClause
	 */
	public boolean isOosItemTableClause() {
		return oosItemTableClause;
	}

	/**
	 * @param oosItemTableClause the oosItemTableClause to set
	 */
	public void setOosItemTableClause(boolean oosItemTableClause) {
		this.oosItemTableClause = oosItemTableClause;
	}

	/**
	 * @return the isTaxInvoice
	 */
	public boolean isTaxInvoice() {
		return isTaxInvoice;
	}

	/**
	 * @param isTaxInvoice the isTaxInvoice to set
	 */
	public void setTaxInvoice(boolean isTaxInvoice) {
		this.isTaxInvoice = isTaxInvoice;
	}

	/**
	 * @return the physicalLoaNo
	 */
	public String getPhysicalLoaNo() {
		return physicalLoaNo;
	}

	/**
	 * @param physicalLoaNo the physicalLoaNo to set
	 */
	public void setPhysicalLoaNo(String physicalLoaNo) {
		this.physicalLoaNo = physicalLoaNo;
	}

	/**
	 * @return the physicalContractNo
	 */
	public String getPhysicalContractNo() {
		return physicalContractNo;
	}

	/**
	 * @param physicalContractNo the physicalContractNo to set
	 */
	public void setPhysicalContractNo(String physicalContractNo) {
		this.physicalContractNo = physicalContractNo;
	}
}
