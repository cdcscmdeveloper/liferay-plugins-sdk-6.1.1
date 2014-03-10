/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.directpurchase.ScPIDRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefaultAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
    private boolean visibleRemarkAttach = true;
    private boolean visibleOrderQty = true;
    private boolean editFlag; // flat for editing fields in pid
    
	private ScRequestSupplierItem scRequestSupplierItem;
	private	List<CmPricing> listPricingItems	=	new ArrayList<>();
	private	DualListModel<PmUser>	approvalUsers;
	private CtContract contract = new CtContract();
	private CtContractItem ctContractItem;
	private CmSupplierItem supplierItem;
	private SelectItem[] justifications; //justification drop down 
	private List<ScPurchaseInDefaultAttachment> attachments;
    
	public PurchaseInDefaultManagedBean() {
		initialInformation();
		List<ScPIDRemark> remarks = new ArrayList<ScPIDRemark>();
		   purchaseInDefault.setRemarks(remarks);
	}
	
    public void initialInformation(){
		IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(RoleCode.BPK_APPROVER.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		List<PmUser> availableApprover	=	userResDTO.getUserList();
		List<PmUser> selectedApprover	=	new ArrayList<PmUser>();
		approvalUsers =	new DualListModel<PmUser>(availableApprover, selectedApprover);
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
	 * @return the contract
	 */
	public CtContract getContract() {
		return contract;
	}
	/**
	 * @param contract the contract to set
	 */
	public void setContract(CtContract contract) {
		this.contract = contract;
	}
	/**
	 * @return the ctContractItem
	 */
	public CtContractItem getCtContractItem() {
		return ctContractItem;
	}
	/**
	 * @param ctContractItem the ctContractItem to set
	 */
	public void setCtContractItem(CtContractItem ctContractItem) {
		this.ctContractItem = ctContractItem;
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
	 * @return the visibleRemarkAttach
	 */
	public boolean isVisibleRemarkAttach() {
		return visibleRemarkAttach;
	}
	/**
	 * @param visibleRemarkAttach the visibleRemarkAttach to set
	 */
	public void setVisibleRemarkAttach(boolean visibleRemarkAttach) {
		this.visibleRemarkAttach = visibleRemarkAttach;
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
	
}
