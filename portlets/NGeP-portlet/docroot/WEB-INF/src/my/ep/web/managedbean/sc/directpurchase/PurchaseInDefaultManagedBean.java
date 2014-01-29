package my.ep.web.managedbean.sc.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.ss.directpurchase.ScPIDRemark;
import my.ep.persistence.entity.ss.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.ss.directpurchase.ScRequestItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DualListModel;

@ManagedBean(name="purchaseInDefaultMB")
@SessionScoped
public class PurchaseInDefaultManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private	ScPurchaseInDefault	purchaseInDefault = new ScPurchaseInDefault();
	private	List<ScRequestItem>	selectedPidItems	=	new ArrayList<ScRequestItem>();
	private	List<ScRequestSupplierItem>	supplierItems	=	new ArrayList<ScRequestSupplierItem>();
	
    private String stockAvailability;
    
    
	private ScRequestSupplierItem scRequestSupplierItem;
	private	List<CmPricing> listPricingItems	=	new ArrayList<>();
	private	DualListModel<PmUser>	approvalUsers;
	private CtContract contract;
	private CtContractItem ctContractItem;
	private CmSupplierItem supplierItem;

    
	public PurchaseInDefaultManagedBean() {
		initialInformation();
		List<ScPIDRemark> remarks = new ArrayList<ScPIDRemark>();
		   purchaseInDefault.setRemarks(remarks);
	}
	public void initialInformation(){
		IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(RoleCode.APPROVER.value());
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
	 * @return the stockAvailability
	 */
	public String getStockAvailability() {
		return stockAvailability;
	}
	/**
	 * @param stockAvailability the stockAvailability to set
	 */
	public void setStockAvailability(String stockAvailability) {
		this.stockAvailability = stockAvailability;
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
	
	
}
