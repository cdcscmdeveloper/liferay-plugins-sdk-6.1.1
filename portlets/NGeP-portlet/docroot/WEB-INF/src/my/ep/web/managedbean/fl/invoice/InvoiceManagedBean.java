/**
 * 
 */
package my.ep.web.managedbean.fl.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * Managed Bean for invoice
 * @author KK LIM
 * @since 30/10/2013
 *
 */
@ManagedBean(name = "invMB")
@SessionScoped
public class InvoiceManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private FlInvoice invoice;
	private FlInvoiceItem invItem = new FlInvoiceItem();
	
	private String DocProcess;
	private String buttonVal;
	private String contractNo;
	private String loaNo;
	private String title;
	
	private boolean isDan = false;
	private boolean isUpdateDan = false;
	private BigDecimal sumOrderedAmt;
	private String expDate;
	private String effDate;
	private Long statusId;
	
	private String factoringCheck="false";
	private String docType;
	private Map<String, SmSupplierBank> supplierBankAccountMap;
	private List<PmStatusDesc>decisionList;
	
	// Inv Items
	private TreeNode chargeLineItemRoot ;
	private FlFulfilmentItemAddr chargeLineSelectedItemAddress;
	private TreeNode[] selectedNodes;
	private TreeNode selectedNode;
	private FlFulfilmentItemAddr selectedItemChargeSection;
	
	//grand total
	private BigDecimal orderedAmtGrandTotal ;
	private BigDecimal receivedAmtGrandTotal ;
	private BigDecimal invAmtGrandTotal ;
	private String serviceCharge ;
	
	//tab menu
	private int selectedTabIndex;
	
	private PmUser pmUser;
	
	//getSuplier address details
	private String branchName;
	private String supplierAddress;
	private String phoneNo;
	private String faxNo;
	private String contactEmail;
	private String contactPerson;
	private String contactPhoneNo;
	private boolean modificationCancel;
	private boolean docMaintainRemarks = false;
	
	private FlFulfilmentRequest fulfilmentRequest;
	
	public void populateChargeLineData(){
		this.chargeLineItemRoot = new DefaultTreeNode("root", null);
		
		//Map<Long, List<FlInvoiceItem>> invoiceItemsMap = new HashMap<Long, List<FlInvoiceItem>>();
		Map<String, List<FlInvoiceItem>> invoiceItemsMap = new TreeMap<String, List<FlInvoiceItem>>();
		List<FlInvoiceItem> flInvoiceItemList = null;
		for (FlInvoiceItem flInvoiceItem : invoice.getFlInvoiceItem()) {
			if (invoiceItemsMap.containsKey(flInvoiceItem.getFlFulfilmentItem().getCmItem().getExtensionCode())) {
				flInvoiceItemList = invoiceItemsMap.get(flInvoiceItem.getFlFulfilmentItem().getCmItem().getItemId());
			} else {
				flInvoiceItemList = new ArrayList<FlInvoiceItem>();
			}
			flInvoiceItemList.add(flInvoiceItem);
			invoiceItemsMap.put(flInvoiceItem.getFlFulfilmentItem().getCmItem().getExtensionCode(), flInvoiceItemList);
		}
		
		BigDecimal grandTotalOrderAmt = new BigDecimal(0);
		BigDecimal grandTotalReceivedAmt = new BigDecimal(0);
		BigDecimal grandTotalInvAmt = new BigDecimal(0);
		//for(Entry<Long, List<FlInvoiceItem>> itemEntry : invoiceItemsMap.entrySet()){
		for(Entry<String, List<FlInvoiceItem>> itemEntry : invoiceItemsMap.entrySet()){	
			List<FlInvoiceItem> invoiceItems = itemEntry.getValue();
			FlInvoiceItem flInvoiceItem = (FlInvoiceItem) invoiceItems.get(0).cloneEntity(); // parent entity, no need stored in DB.
			flInvoiceItem.setParent(true);
			TreeNode childItemNode = new DefaultTreeNode(flInvoiceItem.getFlFulfilmentItem().getCmItem().getExtensionCode(), flInvoiceItem, chargeLineItemRoot);
			
			BigDecimal totalOrderAmt = new BigDecimal(0);
			BigDecimal totalReceiveAmt = new BigDecimal(0);
			BigDecimal totalRecallAmt = new BigDecimal(0);
			BigDecimal totalInvAmt = new BigDecimal(0);
			for(FlInvoiceItem chargeLineItem : invoiceItems) {
				new DefaultTreeNode(chargeLineItem.getFlFulfilmentItem().getCmItem().getExtensionCode(),chargeLineItem,childItemNode);
				totalOrderAmt=totalOrderAmt.add(chargeLineItem.getOrderedAmt());
				totalReceiveAmt=totalReceiveAmt.add(chargeLineItem.getReceivedAmt());
				totalRecallAmt=totalRecallAmt.add(chargeLineItem.getRecalledAmt());
				totalInvAmt=totalInvAmt.add(chargeLineItem.getInvAmt());
			}			

			flInvoiceItem.setOrderedAmt(totalOrderAmt);
			flInvoiceItem.setReceivedAmt(totalReceiveAmt);
			flInvoiceItem.setRecalledAmt(totalRecallAmt);
			flInvoiceItem.setInvAmt(totalInvAmt);
			
			grandTotalOrderAmt = grandTotalOrderAmt.add(totalOrderAmt);
			grandTotalReceivedAmt = grandTotalReceivedAmt.add(totalReceiveAmt);
			grandTotalInvAmt = grandTotalInvAmt.add(totalInvAmt);
		}
		setOrderedAmtGrandTotal(grandTotalOrderAmt);
		setReceivedAmtGrandTotal(grandTotalReceivedAmt);
		setInvAmtGrandTotal(grandTotalInvAmt);
	}
	
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
	 * @return the invoice
	 */
	public FlInvoice getInvoice() {
		return invoice;
	}
	/**
	 * @param invoice the invoice to set
	 */
	public void setInvoice(FlInvoice invoice) {
		this.invoice = invoice;
	}
	/**
	 * @return the docProcess
	 */
	public String getDocProcess() {
		return DocProcess;
	}
	/**
	 * @param docProcess the docProcess to set
	 */
	public void setDocProcess(String docProcess) {
		DocProcess = docProcess;
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
	 * @return the isDan
	 */
	public boolean isDan() {
		return isDan;
	}
	/**
	 * @param isDan the isDan to set
	 */
	public void setDan(boolean isDan) {
		this.isDan = isDan;
	}
	/**
	 * @return the isUpdateDan
	 */
	public boolean isUpdateDan() {
		return isUpdateDan;
	}
	/**
	 * @param isUpdateDan the isUpdateDan to set
	 */
	public void setUpdateDan(boolean isUpdateDan) {
		this.isUpdateDan = isUpdateDan;
	}
	/**
	 * @return the factoringCheck
	 */
	public String getFactoringCheck() {
		return factoringCheck;
	}
	/**
	 * @param factoringCheck the factoringCheck to set
	 */
	public void setFactoringCheck(String factoringCheck) {
		this.factoringCheck = factoringCheck;
	}
	/**
	 * @return the invItem
	 */
	public FlInvoiceItem getInvItem() {
		return invItem;
	}
	/**
	 * @param invItem the invItem to set
	 */
	public void setInvItem(FlInvoiceItem invItem) {
		this.invItem = invItem;
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
	 * @return the chargeLineSelectedItemAddress
	 */
	public FlFulfilmentItemAddr getChargeLineSelectedItemAddress() {
		return chargeLineSelectedItemAddress;
	}
	/**
	 * @param chargeLineSelectedItemAddress the chargeLineSelectedItemAddress to set
	 */
	public void setChargeLineSelectedItemAddress(
			FlFulfilmentItemAddr chargeLineSelectedItemAddress) {
		this.chargeLineSelectedItemAddress = chargeLineSelectedItemAddress;
	}
	/**
	 * @return the selectedNodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}
	/**
	 * @param selectedNodes the selectedNodes to set
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
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
	 * @return the selectedItemChargeSection
	 */
	public FlFulfilmentItemAddr getSelectedItemChargeSection() {
		return selectedItemChargeSection;
	}
	/**
	 * @param selectedItemChargeSection the selectedItemChargeSection to set
	 */
	public void setSelectedItemChargeSection(
			FlFulfilmentItemAddr selectedItemChargeSection) {
		this.selectedItemChargeSection = selectedItemChargeSection;
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
	 * @return the supplierBankAccountMap
	 */
	public Map<String, SmSupplierBank> getSupplierBankAccountMap() {
		if(supplierBankAccountMap==null) {
			supplierBankAccountMap = new HashMap<String, SmSupplierBank>();
		}
		return supplierBankAccountMap;
	}
	/**
	 * @param supplierBankAccountMap the supplierBankAccountMap to set
	 */
	public void setSupplierBankAccountMap(
			Map<String, SmSupplierBank> supplierBankAccountMap) {
		this.supplierBankAccountMap = supplierBankAccountMap;
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

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	/**
	 * @return the contractNo
	 */
	public String getContractNo() {
		return contractNo;
	}

	/**
	 * @param contractNo the contractNo to set
	 */
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	/**
	 * @return the loaNo
	 */
	public String getLoaNo() {
		return loaNo;
	}

	/**
	 * @param loaNo the loaNo to set
	 */
	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the modificationCancel
	 */
	public boolean isModificationCancel() {
		return modificationCancel;
	}

	/**
	 * @param modificationCancel the modificationCancel to set
	 */
	public void setModificationCancel(boolean modificationCancel) {
		this.modificationCancel = modificationCancel;
	}

	/**
	 * @return the docMaintainRemarks
	 */
	public boolean isDocMaintainRemarks() {
		return docMaintainRemarks;
	}

	/**
	 * @param docMaintainRemarks the docMaintainRemarks to set
	 */
	public void setDocMaintainRemarks(boolean docMaintainRemarks) {
		this.docMaintainRemarks = docMaintainRemarks;
	}

	/**
	 * @return the decisionList
	 */
	public List<PmStatusDesc> getDecisionList() {
		return decisionList;
	}

	/**
	 * @param decisionList the decisionList to set
	 */
	public void setDecisionList(List<PmStatusDesc> decisionList) {
		this.decisionList = decisionList;
	}

	/**
	 * @return the sumOrderedAmt
	 */
	public BigDecimal getSumOrderedAmt() {
		return sumOrderedAmt;
	}

	/**
	 * @param sumOrderedAmt the sumOrderedAmt to set
	 */
	public void setSumOrderedAmt(BigDecimal sumOrderedAmt) {
		this.sumOrderedAmt = sumOrderedAmt;
	}

	/**
	 * @return the expDate
	 */
	public String getExpDate() {
		return expDate;
	}

	/**
	 * @param expDate the expDate to set
	 */
	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	/**
	 * @return the effDate
	 */
	public String getEffDate() {
		return effDate;
	}

	/**
	 * @param effDate the effDate to set
	 */
	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}

	/**
	 * @return the statusId
	 */
	public Long getStatusId() {
		return statusId;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}



}
