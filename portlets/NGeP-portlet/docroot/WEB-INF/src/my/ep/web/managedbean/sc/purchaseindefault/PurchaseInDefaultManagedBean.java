/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.purchaseindefault;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.circular.dtl.CtCircularPidReason;
import my.ep.persistence.entity.ct.dtl.CtPidReason;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;

// TODO: Auto-generated Javadoc

/**
 * The Class PurchaseInDefaultManagedBean.
 * 
 * @author sri
 * @since 29/01/14
 */
@ManagedBean(name = "purchaseInDefaultMB")
@SessionScoped
public class PurchaseInDefaultManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The method. */
	private String method = ParameterConst.METHOD_PID;

	/** The purchase in default. */
	private ScPurchaseInDefault purchaseInDefault = new ScPurchaseInDefault();

	/** The selected pid items. */
	private List<ScRequestItem> selectedPidItems = new ArrayList<ScRequestItem>();

	/** The supplier items. */
	private List<ScRequestSupplierItem> supplierItems = new ArrayList<ScRequestSupplierItem>();

	/** The justification type. */
	private String justificationType;

	/** The visible order qty. */
	private boolean visibleOrderQty;

	/** The edit flag. */
	private boolean editFlag; // flat for editing fields in pid

	/** The pid btn flag. */
	private boolean pidBtnFlag = Boolean.TRUE;

	/** The sc request supplier item. */
	private ScRequestSupplierItem scRequestSupplierItem;

	/** The list pricing items. */
	private List<CmPricing> listPricingItems = new ArrayList<>();

	/** The approval users. */
	private DualListModel<PmUser> approvalUsers = new DualListModel<PmUser>();

	/** The supplier item. */
	private CmSupplierItem supplierItem;

	/** The justifications. */
	private SelectItem[] justifications; // justification drop down
	
	/** The source methods. */
	private SelectItem[] sourceMethods; // source methods drop down

	/** The pid status. */
	private PmStatus pidStatus;

	/** The page nav to request note. */
	private String pageNavToRequestNote;

	/** The pm reason. */
	private PmReason pmReason;

	/** The check contract item. */
	private boolean checkContractItem;
	
	private boolean checkCentralContractItem;

	/** The title checker. */
	private boolean titleChecker = Boolean.TRUE;

	/** The action. */
	private String action;

	/** The auto update. */
	private boolean autoUpdate = Boolean.TRUE;

	/** The condition field. */
	private String conditionField;

	/** The v cm supplier item. */
	private VCmSupplierItem vCmSupplierItem;
	
	/** The sm gst supplier. */
	private SmGstSupplier smGstSupplier;
	
	/** The dp obj. */
	private Object[] dpObj = new Object[6];
	
	/** The tracking dairy view. */
	private boolean trackingDairyView;
	
	/** The approver List by group */
	private List<PmUser> approverList;
	
	/** The user group list. */
	private List<PmUserGroup> userGroupList;
	
	/** The selected approver id. */
	private Long selectedApproverId;
	
	/** The user group id. */
	private Long userGroupId;
	
	/** The back up approvers */
	private List<PmUser> backupApprovers;
	
    /** The pid justifications. */
    private List<PmReasonDesc> pidJustifications;
    
    /** The Approved by. */
    private PmUser approvedBy;
	
    /** The ct reason. */
    private CtPidReason ctReason;
	
	/** The panel reason. */
	private CtCircularPidReason panelReason;
	
	/** The is contract type as ministry. */
	private boolean isContractTypeAsMinistry;
	
	/** The codify flag for selected contract / panel item */
	private boolean isCodified = true;

	/**
	 * Instantiates a new purchase in default managed bean.
	 */
	public PurchaseInDefaultManagedBean() {
	}

	/**
	 * Gets the purchase in default.
	 * 
	 * @return the purchase in default
	 */
	public ScPurchaseInDefault getPurchaseInDefault() {
		return purchaseInDefault;
	}

	/**
	 * Sets the purchase in default.
	 * 
	 * @param purchaseInDefault
	 *            the new purchase in default
	 */
	public void setPurchaseInDefault(ScPurchaseInDefault purchaseInDefault) {
		this.purchaseInDefault = purchaseInDefault;
	}

	/**
	 * Gets the selected pid items.
	 * 
	 * @return the selected pid items
	 */
	public List<ScRequestItem> getSelectedPidItems() {
		return selectedPidItems;
	}

	/**
	 * Sets the selected pid items.
	 * 
	 * @param selectedPidItems
	 *            the new selected pid items
	 */
	public void setSelectedPidItems(List<ScRequestItem> selectedPidItems) {
		this.selectedPidItems = selectedPidItems;
	}

	/**
	 * Gets the supplier items.
	 * 
	 * @return the supplier items
	 */
	public List<ScRequestSupplierItem> getSupplierItems() {
		return supplierItems;
	}

	/**
	 * Sets the supplier items.
	 * 
	 * @param supplierItems
	 *            the new supplier items
	 */
	public void setSupplierItems(List<ScRequestSupplierItem> supplierItems) {
		this.supplierItems = supplierItems;
	}

	/**
	 * Gets the sc request supplier item.
	 * 
	 * @return the scRequestSupplierItem
	 */
	public ScRequestSupplierItem getScRequestSupplierItem() {
		return scRequestSupplierItem;
	}

	/**
	 * Sets the sc request supplier item.
	 * 
	 * @param scRequestSupplierItem
	 *            the scRequestSupplierItem to set
	 */
	public void setScRequestSupplierItem(
			ScRequestSupplierItem scRequestSupplierItem) {
		this.scRequestSupplierItem = scRequestSupplierItem;
	}

	/**
	 * Gets the list pricing items.
	 * 
	 * @return the listPricingItems
	 */
	public List<CmPricing> getListPricingItems() {
		return listPricingItems;
	}

	/**
	 * Sets the list pricing items.
	 * 
	 * @param listPricingItems
	 *            the listPricingItems to set
	 */
	public void setListPricingItems(List<CmPricing> listPricingItems) {
		this.listPricingItems = listPricingItems;
	}

	/**
	 * Gets the approval users.
	 * 
	 * @return the approvalUsers
	 */
	public DualListModel<PmUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * Sets the approval users.
	 * 
	 * @param approvalUsers
	 *            the approvalUsers to set
	 */
	public void setApprovalUsers(DualListModel<PmUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

	/**
	 * Gets the supplier item.
	 * 
	 * @return the supplierItem
	 */
	public CmSupplierItem getSupplierItem() {
		return supplierItem;
	}

	/**
	 * Sets the supplier item.
	 * 
	 * @param supplierItem
	 *            the supplierItem to set
	 */
	public void setSupplierItem(CmSupplierItem supplierItem) {
		this.supplierItem = supplierItem;
	}

	/**
	 * Checks if is edits the flag.
	 * 
	 * @return the editFlag
	 */
	public boolean isEditFlag() {
		return editFlag;
	}

	/**
	 * Sets the edits the flag.
	 * 
	 * @param editFlag
	 *            the editFlag to set
	 */
	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}

	/**
	 * Gets the justification type.
	 * 
	 * @return the justificationType
	 */
	public String getJustificationType() {
		return justificationType;
	}

	/**
	 * Sets the justification type.
	 * 
	 * @param justificationType
	 *            the justificationType to set
	 */
	public void setJustificationType(String justificationType) {
		this.justificationType = justificationType;
	}

	/**
	 * Checks if is visible order qty.
	 * 
	 * @return the visibleOrderQty
	 */
	public boolean isVisibleOrderQty() {
		return visibleOrderQty;
	}

	/**
	 * Sets the visible order qty.
	 * 
	 * @param visibleOrderQty
	 *            the visibleOrderQty to set
	 */
	public void setVisibleOrderQty(boolean visibleOrderQty) {
		this.visibleOrderQty = visibleOrderQty;
	}

	/**
	 * Gets the method.
	 * 
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the method.
	 * 
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Gets the justifications.
	 * 
	 * @return the justifications
	 */
	public SelectItem[] getJustifications() {
		return justifications;
	}

	/**
	 * Sets the justifications.
	 * 
	 * @param justifications
	 *            the justifications to set
	 */
	public void setJustifications(SelectItem[] justifications) {
		this.justifications = justifications;
	}

	/**
	 * @return the sourceMethods
	 */
	public SelectItem[] getSourceMethods() {
		return sourceMethods;
	}

	/**
	 * @param sourceMethods the sourceMethods to set
	 */
	public void setSourceMethods(SelectItem[] sourceMethods) {
		this.sourceMethods = sourceMethods;
	}

	/**
	 * Gets the pid status.
	 * 
	 * @return the pidStatus
	 */
	public PmStatus getPidStatus() {
		return pidStatus;
	}

	/**
	 * Sets the pid status.
	 * 
	 * @param pidStatus
	 *            the pidStatus to set
	 */
	public void setPidStatus(PmStatus pidStatus) {
		this.pidStatus = pidStatus;
	}

	/**
	 * Gets the page nav to request note.
	 * 
	 * @return the pageNavToRequestNote
	 */
	public String getPageNavToRequestNote() {
		return pageNavToRequestNote;
	}

	/**
	 * Sets the page nav to request note.
	 * 
	 * @param pageNavToRequestNote
	 *            the pageNavToRequestNote to set
	 */
	public void setPageNavToRequestNote(String pageNavToRequestNote) {
		this.pageNavToRequestNote = pageNavToRequestNote;
	}

	/**
	 * Checks if is pid btn flag.
	 * 
	 * @return the pidBtnFlag
	 */
	public boolean isPidBtnFlag() {
		return pidBtnFlag;
	}

	/**
	 * Sets the pid btn flag.
	 * 
	 * @param pidBtnFlag
	 *            the pidBtnFlag to set
	 */
	public void setPidBtnFlag(boolean pidBtnFlag) {
		this.pidBtnFlag = pidBtnFlag;
	}

	/**
	 * Gets the pm reason.
	 * 
	 * @return the pmReason
	 */
	public PmReason getPmReason() {
		return pmReason;
	}

	/**
	 * Sets the pm reason.
	 * 
	 * @param pmReason
	 *            the pmReason to set
	 */
	public void setPmReason(PmReason pmReason) {
		this.pmReason = pmReason;
	}

	/**
	 * Checks if is check contract item.
	 * 
	 * @return the checkContractItem
	 */
	public boolean isCheckContractItem() {
		return checkContractItem;
	}

	/**
	 * Sets the check contract item.
	 * 
	 * @param checkContractItem
	 *            the checkContractItem to set
	 */
	public void setCheckContractItem(boolean checkContractItem) {
		this.checkContractItem = checkContractItem;
	}

	/**
	 * @return the checkCentralContractItem
	 */
	public boolean isCheckCentralContractItem() {
		return checkCentralContractItem;
	}

	/**
	 * @param checkCentralContractItem the checkCentralContractItem to set
	 */
	public void setCheckCentralContractItem(boolean checkCentralContractItem) {
		this.checkCentralContractItem = checkCentralContractItem;
	}

	/**
	 * Checks if is title checker.
	 * 
	 * @return the titleChecker
	 */
	public boolean isTitleChecker() {
		return titleChecker;
	}

	/**
	 * Sets the title checker.
	 * 
	 * @param titleChecker
	 *            the titleChecker to set
	 */
	public void setTitleChecker(boolean titleChecker) {
		this.titleChecker = titleChecker;
	}

	/**
	 * Gets the action.
	 * 
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 * 
	 * @param action
	 *            the new action
	 */
	public void setAction(String action) {
		this.action = action;
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
	 * @param autoUpdate
	 *            the new auto update
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * Gets the v cm supplier item.
	 * 
	 * @return the v cm supplier item
	 */
	public VCmSupplierItem getvCmSupplierItem() {
		return vCmSupplierItem;
	}

	/**
	 * Sets the v cm supplier item.
	 * 
	 * @param vCmSupplierItem
	 *            the new v cm supplier item
	 */
	public void setvCmSupplierItem(VCmSupplierItem vCmSupplierItem) {
		this.vCmSupplierItem = vCmSupplierItem;
	}

	/**
	 * Gets the condition field.
	 * 
	 * @return the condition field
	 */
	public String getConditionField() {
		return conditionField;
	}

	/**
	 * Sets the condition field.
	 * 
	 * @param conditionField
	 *            the new condition field
	 */
	public void setConditionField(String conditionField) {
		this.conditionField = conditionField;
	}

	/**
	 * @return the smGstSupplier
	 */
	public SmGstSupplier getSmGstSupplier() {
		return smGstSupplier;
	}

	/**
	 * @param smGstSupplier the smGstSupplier to set
	 */
	public void setSmGstSupplier(SmGstSupplier smGstSupplier) {
		this.smGstSupplier = smGstSupplier;
	}

	/**
	 * @return the dpObj
	 */
	public Object[] getDpObj() {
		return dpObj;
	}

	/**
	 * @param dpObj the dpObj to set
	 */
	public void setDpObj(Object[] dpObj) {
		this.dpObj = dpObj;
	}

	/**
	 * @return the trackingDairyView
	 */
	public boolean isTrackingDairyView() {
		return trackingDairyView;
	}

	/**
	 * @param trackingDairyView the trackingDairyView to set
	 */
	public void setTrackingDairyView(boolean trackingDairyView) {
		this.trackingDairyView = trackingDairyView;
	}

	/**
	 * @return the approverList
	 */
	public List<PmUser> getApproverList() {
		return approverList;
	}

	/**
	 * @param approverList the approverList to set
	 */
	public void setApproverList(List<PmUser> approverList) {
		this.approverList = approverList;
	}

	/**
	 * @return the userGroupList
	 */
	public List<PmUserGroup> getUserGroupList() {
		return userGroupList;
	}

	/**
	 * @param userGroupList the userGroupList to set
	 */
	public void setUserGroupList(List<PmUserGroup> userGroupList) {
		this.userGroupList = userGroupList;
	}

	/**
	 * @return the selectedApproverId
	 */
	public Long getSelectedApproverId() {
		return selectedApproverId;
	}

	/**
	 * @param selectedApproverId the selectedApproverId to set
	 */
	public void setSelectedApproverId(Long selectedApproverId) {
		this.selectedApproverId = selectedApproverId;
	}

	/**
	 * @return the userGroupId
	 */
	public Long getUserGroupId() {
		return userGroupId;
	}

	/**
	 * @param userGroupId the userGroupId to set
	 */
	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	/**
	 * @return the backupApprovers
	 */
	public List<PmUser> getBackupApprovers() {
		return backupApprovers;
	}

	/**
	 * @param backupApprovers the backupApprovers to set
	 */
	public void setBackupApprovers(List<PmUser> backupApprovers) {
		this.backupApprovers = backupApprovers;
	}

	/**
	 * @return the pidJustifications
	 */
	public List<PmReasonDesc> getPidJustifications() {
		return pidJustifications;
	}

	/**
	 * @param pidJustifications the pidJustifications to set
	 */
	public void setPidJustifications(List<PmReasonDesc> pidJustifications) {
		this.pidJustifications = pidJustifications;
	}

	/**
	 * @return the approvedBy
	 */
	public PmUser getApprovedBy() {
		return approvedBy;
	}

	/**
	 * @param approvedBy the approvedBy to set
	 */
	public void setApprovedBy(PmUser approvedBy) {
		this.approvedBy = approvedBy;
	}

	/**
	 * @return the ctReason
	 */
	public CtPidReason getCtReason() {
		return ctReason;
	}

	/**
	 * @param ctReason the ctReason to set
	 */
	public void setCtReason(CtPidReason ctReason) {
		this.ctReason = ctReason;
	}

	/**
	 * @return the panelReason
	 */
	public CtCircularPidReason getPanelReason() {
		return panelReason;
	}

	/**
	 * @param panelReason the panelReason to set
	 */
	public void setPanelReason(CtCircularPidReason panelReason) {
		this.panelReason = panelReason;
	}

	/**
	 * @return the isContractTypeAsMinistry
	 */
	public boolean isContractTypeAsMinistry() {
		return isContractTypeAsMinistry;
	}

	/**
	 * @param isContractTypeAsMinistry the isContractTypeAsMinistry to set
	 */
	public void setContractTypeAsMinistry(boolean isContractTypeAsMinistry) {
		this.isContractTypeAsMinistry = isContractTypeAsMinistry;
	}

	/**
	 * @return the isCodified
	 */
	public boolean isCodified() {
		return isCodified;
	}

	/**
	 * @param isCodified the isCodified to set
	 */
	public void setCodified(boolean isCodified) {
		this.isCodified = isCodified;
	}
	
	
}
