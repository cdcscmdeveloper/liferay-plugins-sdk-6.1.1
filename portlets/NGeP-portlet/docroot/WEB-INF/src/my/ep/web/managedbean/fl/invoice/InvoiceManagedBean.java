/**
 * 
 */
package my.ep.web.managedbean.fl.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for invoice
 * @author KK LIM
 * @since 30/10/2013
 *
 */
@ManagedBean(name = "invMB")
@SessionScoped
public class InvoiceManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FlInvoice invoice = new FlInvoice();
	private FlInvoiceItem invItem = new FlInvoiceItem();
	
	private String DocProcess;
	private String buttonVal;
	
	private boolean isDan = false;
	private boolean isUpdateDan = false;
	
	private String factoringCheck="false";
	private String docType;
	
	// Inv Items
	private TreeNode chargeLineItemRoot ;
	private FlFulfilmentItemAddr chargeLineSelectedItemAddress;
	//private  List<TreeNode> selectedNodes;
	private TreeNode[] selectedNodes;
	private TreeNode selectedNode;
	private FlFulfilmentItemAddr selectedItemChargeSection;
	//private Long pmPrjSetia;
	
	//grand total
	private BigDecimal orderedAmtGrandTotal ;
	private BigDecimal receivedAmtGrandTotal ;
	private BigDecimal invAmtGrandTotal ;
	private String serviceCharge ;
	
	//tab menu
	private int selectedTabIndex;
	
	private PmUser pmUser;
	
	
	private FlFulfilmentRequest fulfilmentRequest;
	
	public void populateChargeLineData(){
		 this.chargeLineItemRoot = new DefaultTreeNode("root", null);
			Map<Long, List<FlFulfilmentItemAddr>> chargeLineItemsMap=processChargeLineItems();
			prepareChargeLineTreeNode(chargeLineItemsMap,chargeLineItemRoot);
	}
	public Map<Long, List<FlFulfilmentItemAddr>> processChargeLineItems(){
		Map<Long, List<FlFulfilmentItemAddr>> chargeLineItemsMap=new HashMap<Long, List<FlFulfilmentItemAddr>>();
			if(null !=this.getFulfilmentRequest().getFlDeliveryAddresses()){
				for(FlDeliveryAddress  deliveryAddress :this.getInvoice().getFlFulfilmentOrder().getFlFulfilmentRequest().getFlDeliveryAddresses()){	
					if(null!=deliveryAddress.getFlFulfilmentItemAddr()){
						for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
							itemAddress.setChargeOrderedAmt(itemAddress.getOrderedAmt());
							itemAddress.setChargeOrderedQty(itemAddress.getOrderedQty());
							itemAddress.setChargeReceivedAmt(itemAddress.getReceivedAmt());
							itemAddress.setChargeReceivedQty(itemAddress.getReceivedQty());
							//itemAddress.setChargeRecalledAmt(itemAddress.getRecalledAmt());
							//itemAddress.setChargeRecalledQty(itemAddress.getRecalledQty());
							if(null==itemAddress.getChargeInvAmt()){
								itemAddress.setChargeInvAmt(itemAddress.getReceivedAmt());
							}
							itemAddress.setChargeUnitPrice(itemAddress.getUnitPrice());
							if(chargeLineItemsMap.containsKey(itemAddress.getScRequestItem().getRequestNoteItemId())){
								//code for calculating duplicate charge line order amount and quantity for same unit price 
								//Condition for checking unit price same.
								boolean notContained=true;
								for(FlFulfilmentItemAddr containedItem:chargeLineItemsMap.get(itemAddress.getScRequestItem().getRequestNoteItemId())){
									if(containedItem.getChargeUnitPrice().equals(itemAddress.getUnitPrice())){
										//BigDecimal chargeOrderedAmt=containedItem.getChargeOrderedAmt();
										containedItem.setChargeOrderedAmt( containedItem.getChargeOrderedAmt().add(itemAddress.getOrderedAmt()));
										containedItem.setChargeOrderedQty(containedItem.getChargeOrderedQty().add(itemAddress.getOrderedQty()));
										containedItem.setChargeReceivedAmt(containedItem.getChargeReceivedAmt().add(itemAddress.getReceivedAmt()));
										containedItem.setChargeReceivedQty(containedItem.getChargeReceivedQty().add(itemAddress.getReceivedQty()));
										//containedItem.setChargeRecalledAmt(containedItem.getChargeRecalledAmt().add(itemAddress.getRecalledAmt()));
										//containedItem.setChargeRecalledQty(containedItem.getChargeRecalledQty().add(itemAddress.getRecalledQty()));
										containedItem.setChargeInvAmt(containedItem.getChargeInvAmt().add(itemAddress.getChargeInvAmt()));
										notContained=false;
									}
								}
								if(notContained){
									chargeLineItemsMap.get(itemAddress.getScRequestItem().getRequestNoteItemId()).add(itemAddress);
								}
							} else {
								List<FlFulfilmentItemAddr> itemAddressList=new ArrayList<FlFulfilmentItemAddr>();
								itemAddressList.add(itemAddress);
								//Copy order amount ,qty to charge line fields and set.
								chargeLineItemsMap.put(itemAddress.getScRequestItem().getRequestNoteItemId(),itemAddressList);
							}
						}
					}
				}
			}
			return chargeLineItemsMap;
		}
	
	
	private void prepareChargeLineTreeNode(
			Map<Long, List<FlFulfilmentItemAddr>> chargeLineItemsMap,TreeNode chargeLineItemRoot) {
		BigDecimal grandTotalOrderAmt=new BigDecimal(0);
		BigDecimal grandTotalReceivedAmt=new BigDecimal(0);
		BigDecimal grandTotalInvAmt=new BigDecimal(0);
		for(Entry<Long, List<FlFulfilmentItemAddr>> itemEntry:chargeLineItemsMap.entrySet()){
			List<FlFulfilmentItemAddr> itemAddress=itemEntry.getValue();
			FlFulfilmentItemAddr flItemAddress=getFulfilmentItemAddress(itemAddress.get(0));
			if(null!=flItemAddress){
				//Do create New Item Address and copy the first element data manually.
				
				TreeNode childItemNode=new DefaultTreeNode(flItemAddress.getScRequestItem().getRequestNoteItemId().toString(),flItemAddress,chargeLineItemRoot);
				BigDecimal totalOrderAmt=new BigDecimal(0);
				BigDecimal totalReceiveAmt=new BigDecimal(0);
				BigDecimal totalRecallAmt=new BigDecimal(0);
				BigDecimal totalInvAmt=new BigDecimal(0);
				for(FlFulfilmentItemAddr itemAddr:itemAddress){
					new DefaultTreeNode(itemAddr.getScRequestItem().getRequestNoteItemId().toString(),itemAddr,childItemNode);
					totalOrderAmt=totalOrderAmt.add(itemAddr.getChargeOrderedAmt());
					totalReceiveAmt=totalReceiveAmt.add(itemAddr.getChargeReceivedAmt());
					//totalRecallAmt=totalRecallAmt.add(itemAddr.getChargeRecalledAmt());
					totalInvAmt=totalInvAmt.add(itemAddr.getChargeReceivedAmt());
				}
				flItemAddress.setChargeOrderedAmt(totalOrderAmt);
				flItemAddress.setChargeReceivedAmt(totalReceiveAmt);
				flItemAddress.setChargeRecalledAmt(totalRecallAmt);
				flItemAddress.setChargeInvAmt(totalReceiveAmt);
				grandTotalOrderAmt=grandTotalOrderAmt.add(totalOrderAmt);
				setOrderedAmtGrandTotal(grandTotalOrderAmt);
				grandTotalReceivedAmt=grandTotalReceivedAmt.add(totalReceiveAmt);
				setReceivedAmtGrandTotal(grandTotalReceivedAmt);
				grandTotalInvAmt=grandTotalInvAmt.add(totalReceiveAmt);
				setInvAmtGrandTotal(grandTotalInvAmt);
			}
			//this.getInvoice().setInvoiceAmtOri(totalReceiveAmt);
		}
	}
	
	private FlFulfilmentItemAddr getFulfilmentItemAddress(
			FlFulfilmentItemAddr flFulfilmentItemAddr) {
		FlFulfilmentItemAddr itemAddress = new FlFulfilmentItemAddr();
		// Do Convertions

		//itemAddress.setFlFulfilmentItem(new FlFulfilmentItem());
		//itemAddress.getFlFulfilmentItem().setScRequestNoteDtl(new ScRequestNoteDtl());
		//itemAddress.getFlFulfilmentItem().getScRequestNoteDtl().setScRequestSupplierItem(new ScRequestSupplierItem());
		//itemAddress.getFlFulfilmentItem().getScRequestNoteDtl().getScRequestSupplierItem().setRequestNoteItem(new ScRequestItem());
		itemAddress.setScRequestItem(new ScRequestItem());
		itemAddress.setParent(true);
		itemAddress.getScRequestItem().setRequestNoteItemId(flFulfilmentItemAddr.getScRequestItem().getRequestNoteItemId());
		itemAddress.getScRequestItem().setItemType(flFulfilmentItemAddr.getScRequestItem().getItemType());
		itemAddress.getScRequestItem().setItemName(flFulfilmentItemAddr.getScRequestItem().getItemName());
		itemAddress.setUnitPrice(flFulfilmentItemAddr.getUnitPrice());
		itemAddress.setOrderedQty(flFulfilmentItemAddr.getOrderedQty());
		itemAddress.setOrderedAmt(flFulfilmentItemAddr.getOrderedAmt());
		itemAddress.setReceivedQty(flFulfilmentItemAddr.getReceivedQty());
		itemAddress.setReceivedAmt(flFulfilmentItemAddr.getReceivedAmt());
		//itemAddress.setRecalledAmt(flFulfilmentItemAddr.getRecalledAmt());
		//itemAddress.setRecalledQty(flFulfilmentItemAddr.getRecalledQty());
		/*if("N".equalsIgnoreCase(flFulfilmentItemAddr.getCoaValue()) || null==flFulfilmentItemAddr.getCoaValue()){
			itemAddress.setCoaValue("N");
		}*/
		if(null !=flFulfilmentItemAddr.getPmVotFund()){
			if(null !=flFulfilmentItemAddr.getPmVotFund().getVotFundId()){
			itemAddress.setCoaValue("Y");
			}
		}else{
			itemAddress.setCoaValue("N");
		}
		
		return itemAddress;
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
}
