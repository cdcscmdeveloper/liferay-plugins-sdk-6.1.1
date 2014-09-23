/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefaultAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;
/**
 * 
 * @author sri
 * 
 */
@ManagedBean(name="purchaseInDefaultMB")
@SessionScoped
public class PurchaseInDefaultManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private String method = ParameterConst.METHOD_PID;
	private	ScPurchaseInDefault	purchaseInDefault = new ScPurchaseInDefault();
	private	List<ScRequestItem>	selectedPidItems	=	new ArrayList<ScRequestItem>();
	private	List<ScRequestSupplierItem>	supplierItems	=	new ArrayList<ScRequestSupplierItem>();
	
    private String justificationType;
    private boolean visibleOrderQty;
    private boolean editFlag; // flat for editing fields in pid
    private boolean pidBtnFlag = Boolean.TRUE;
    
	private ScRequestSupplierItem scRequestSupplierItem;
	private	List<CmPricing> listPricingItems	=	new ArrayList<>();
	private	DualListModel<PmUser>	approvalUsers = new DualListModel<PmUser>();
	private CmSupplierItem supplierItem;
	private SelectItem[] justifications; //justification drop down 
	private List<ScPurchaseInDefaultAttachment> attachments;
	private String processName;
	private PmStatus pidStatus;
	private String pageNavToRequestNote;
	private PmReason pmReason;
	private boolean checkContractItem;
	
	private boolean titleChecker = Boolean.TRUE;
    
	public PurchaseInDefaultManagedBean() {
	}
	
	public ScPurchaseInDefault getPurchaseInDefault() {
		return purchaseInDefault;
	}
	public void setPurchaseInDefault(ScPurchaseInDefault purchaseInDefault) {
		this.purchaseInDefault = purchaseInDefault;
	}
	public List<ScRequestItem> getSelectedPidItems() {
		return selectedPidItems;
	}
	public void setSelectedPidItems(List<ScRequestItem> selectedPidItems) {
		this.selectedPidItems = selectedPidItems;
	}
	public List<ScRequestSupplierItem> getSupplierItems() {
		return supplierItems;
	}
	public void setSupplierItems(List<ScRequestSupplierItem> supplierItems) {
		this.supplierItems = supplierItems;
	}
	
	/**
	 * @return the scRequestSupplierItem
	 */
	public ScRequestSupplierItem getScRequestSupplierItem() {
		return scRequestSupplierItem;
	}
	/**
	 * @param scRequestSupplierItem the scRequestSupplierItem to set
	 */
	public void setScRequestSupplierItem(ScRequestSupplierItem scRequestSupplierItem) {
		this.scRequestSupplierItem = scRequestSupplierItem;
	}

	/**
	 * @return the listPricingItems
	 */
	public List<CmPricing> getListPricingItems() {
		return listPricingItems;
	}
	/**
	 * @param listPricingItems the listPricingItems to set
	 */
	public void setListPricingItems(List<CmPricing> listPricingItems) {
		this.listPricingItems = listPricingItems;
	}
	/**
	 * @return the approvalUsers
	 */
	public DualListModel<PmUser> getApprovalUsers() {
		return approvalUsers;
	}
	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(DualListModel<PmUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}
	/**
	 * @return the supplierItem
	 */
	public CmSupplierItem getSupplierItem() {
		return supplierItem;
	}
	/**
	 * @param supplierItem the supplierItem to set
	 */
	public void setSupplierItem(CmSupplierItem supplierItem) {
		this.supplierItem = supplierItem;
	}

	/**
	 * @return the editFlag
	 */
	public boolean isEditFlag() {
		return editFlag;
	}
	/**
	 * @param editFlag the editFlag to set
	 */
	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}
	/**
	 * @return the justificationType
	 */
	public String getJustificationType() {
		return justificationType;
	}
	/**
	 * @param justificationType the justificationType to set
	 */
	public void setJustificationType(String justificationType) {
		this.justificationType = justificationType;
	}
	
	/**
	 * @return the visibleOrderQty
	 */
	public boolean isVisibleOrderQty() {
		return visibleOrderQty;
	}
	/**
	 * @param visibleOrderQty the visibleOrderQty to set
	 */
	public void setVisibleOrderQty(boolean visibleOrderQty) {
		this.visibleOrderQty = visibleOrderQty;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the justifications
	 */
	public SelectItem[] getJustifications() {
		return justifications;
	}

	/**
	 * @param justifications the justifications to set
	 */
	public void setJustifications(SelectItem[] justifications) {
		this.justifications = justifications;
	}

	/**
	 * @return the attachments
	 */
	public List<ScPurchaseInDefaultAttachment> getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(List<ScPurchaseInDefaultAttachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * @return the pidStatus
	 */
	public PmStatus getPidStatus() {
		return pidStatus;
	}

	/**
	 * @param pidStatus the pidStatus to set
	 */
	public void setPidStatus(PmStatus pidStatus) {
		this.pidStatus = pidStatus;
	}

	/**
	 * @return the pageNavToRequestNote
	 */
	public String getPageNavToRequestNote() {
		return pageNavToRequestNote;
	}

	/**
	 * @param pageNavToRequestNote the pageNavToRequestNote to set
	 */
	public void setPageNavToRequestNote(String pageNavToRequestNote) {
		this.pageNavToRequestNote = pageNavToRequestNote;
	}

	/**
	 * @return the pidBtnFlag
	 */
	public boolean isPidBtnFlag() {
		return pidBtnFlag;
	}

	/**
	 * @param pidBtnFlag the pidBtnFlag to set
	 */
	public void setPidBtnFlag(boolean pidBtnFlag) {
		this.pidBtnFlag = pidBtnFlag;
	}

	/**
	 * @return the pmReason
	 */
	public PmReason getPmReason() {
		return pmReason;
	}

	/**
	 * @param pmReason the pmReason to set
	 */
	public void setPmReason(PmReason pmReason) {
		this.pmReason = pmReason;
	}

	/**
	 * @return the checkContractItem
	 */
	public boolean isCheckContractItem() {
		return checkContractItem;
	}

	/**
	 * @param checkContractItem the checkContractItem to set
	 */
	public void setCheckContractItem(boolean checkContractItem) {
		this.checkContractItem = checkContractItem;
	}

	/**
	 * @return the titleChecker
	 */
	public boolean isTitleChecker() {
		return titleChecker;
	}

	/**
	 * @param titleChecker the titleChecker to set
	 */
	public void setTitleChecker(boolean titleChecker) {
		this.titleChecker = titleChecker;
	}

}
