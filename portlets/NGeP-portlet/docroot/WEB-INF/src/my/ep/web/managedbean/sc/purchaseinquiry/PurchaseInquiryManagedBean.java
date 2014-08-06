/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.purchaseinquiry;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.directpurchase.ScPRIRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;
/**
 * 
 * @author sri
 *
 */
@ManagedBean(name="purchaseInquiryMB")
@SessionScoped
public class PurchaseInquiryManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private String method = ParameterConst.METHOD_PRI;
	private String stockAvailability;
	private String procurementType;
	private String justificationType;
	private String priceType;
	private PmOrgProfile pmOrgProfile;
	
	private ScRequestSupplierItem scRequestSupplierItem = new ScRequestSupplierItem();
	private ScPrInquiry scPrInquiry = new ScPrInquiry();
	private CmSupplierItem supplierItem;
	private	DualListModel<PmUser>	approvalUsers;
	private List<ScRequestSupplierItem>	scRequestSupplierItems;
	private ScRequestNoteDtl requestNoteDtl = new ScRequestNoteDtl();
	
	private boolean justTypeOption;
	private boolean procTypeOption;
	private SelectItem[] justDropdown;
	private SelectItem[] priceTypes; // price type drop down 
	private String processName;
	private String priStatus;
	private boolean requisitionerFlag;
	private boolean supplierFlag;
	private Integer stockAvailable;
	private boolean stockAvailabilityFlag;
	private String pageNavToRequestNote;
	
	private boolean titleChecker = Boolean.TRUE;
	
	public PurchaseInquiryManagedBean() {
		List<ScPRIRemark> remarks = new ArrayList<ScPRIRemark>();
		scPrInquiry.setRemarks(remarks);
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
	 * @return the priceType
	 */
	public String getPriceType() {
		return priceType;
	}

	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	/**
	 * @return the scPrInquiry
	 */
	public ScPrInquiry getScPrInquiry() {
		return scPrInquiry;
	}

	/**
	 * @param scPrInquiry the scPrInquiry to set
	 */
	public void setScPrInquiry(ScPrInquiry scPrInquiry) {
		this.scPrInquiry = scPrInquiry;
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
	 * @return the scRequestSupplierItems
	 */
	public List<ScRequestSupplierItem> getScRequestSupplierItems() {
		return scRequestSupplierItems;
	}
	/**
	 * @param scRequestSupplierItems the scRequestSupplierItems to set
	 */
	public void setScRequestSupplierItems(
			List<ScRequestSupplierItem> scRequestSupplierItems) {
		this.scRequestSupplierItems = scRequestSupplierItems;
	}

	/**
	 * @return the procurementType
	 */
	public String getProcurementType() {
		return procurementType;
	}

	/**
	 * @param procurementType the procurementType to set
	 */
	public void setProcurementType(String procurementType) {
		this.procurementType = procurementType;
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
	 * @return the requestNoteDtl
	 */
	public ScRequestNoteDtl getRequestNoteDtl() {
		return requestNoteDtl;
	}

	/**
	 * @param requestNoteDtl the requestNoteDtl to set
	 */
	public void setRequestNoteDtl(ScRequestNoteDtl requestNoteDtl) {
		this.requestNoteDtl = requestNoteDtl;
	}

	/**
	 * @return the justTypeOption
	 */
	public boolean isJustTypeOption() {
		return justTypeOption;
	}

	/**
	 * @param justTypeOption the justTypeOption to set
	 */
	public void setJustTypeOption(boolean justTypeOption) {
		this.justTypeOption = justTypeOption;
	}

	/**
	 * @return the procTypeOption
	 */
	public boolean isProcTypeOption() {
		return procTypeOption;
	}

	/**
	 * @param procTypeOption the procTypeOption to set
	 */
	public void setProcTypeOption(boolean procTypeOption) {
		this.procTypeOption = procTypeOption;
	}

	/**
	 * @return the justDropdown
	 */
	public SelectItem[] getJustDropdown() {
		return justDropdown;
	}

	/**
	 * @param justDropdown the justDropdown to set
	 */
	public void setJustDropdown(SelectItem[] justDropdown) {
		this.justDropdown = justDropdown;
	}

	/**
	 * @return the priceTypes
	 */
	public SelectItem[] getPriceTypes() {
		return priceTypes;
	}

	/**
	 * @param priceTypes the priceTypes to set
	 */
	public void setPriceTypes(SelectItem[] priceTypes) {
		this.priceTypes = priceTypes;
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
	 * @return the priStatus
	 */
	public String getPriStatus() {
		return priStatus;
	}

	/**
	 * @param priStatus the priStatus to set
	 */
	public void setPriStatus(String priStatus) {
		this.priStatus = priStatus;
	}

	/**
	 * @return the requisitionerFlag
	 */
	public boolean isRequisitionerFlag() {
		return requisitionerFlag;
	}

	/**
	 * @param requisitionerFlag the requisitionerFlag to set
	 */
	public void setRequisitionerFlag(boolean requisitionerFlag) {
		this.requisitionerFlag = requisitionerFlag;
	}

	/**
	 * @return the supplierFlag
	 */
	public boolean isSupplierFlag() {
		return supplierFlag;
	}

	/**
	 * @param supplierFlag the supplierFlag to set
	 */
	public void setSupplierFlag(boolean supplierFlag) {
		this.supplierFlag = supplierFlag;
	}

	/**
	 * @return the stockAvailable
	 */
	public Integer getStockAvailable() {
		return stockAvailable;
	}

	/**
	 * @param stockAvailable the stockAvailable to set
	 */
	public void setStockAvailable(Integer stockAvailable) {
		this.stockAvailable = stockAvailable;
	}

	/**
	 * @return the pmOrgProfile
	 */
	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}

	/**
	 * @param pmOrgProfile the pmOrgProfile to set
	 */
	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
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

	/**
	 * @return the stockAvailabilityFlag
	 */
	public boolean isStockAvailabilityFlag() {
		return stockAvailabilityFlag;
	}

	/**
	 * @param stockAvailabilityFlag the stockAvailabilityFlag to set
	 */
	public void setStockAvailabilityFlag(boolean stockAvailabilityFlag) {
		this.stockAvailabilityFlag = stockAvailabilityFlag;
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
	
}
