package my.ep.web.managedbean.sc.purchaseinquiry;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.ss.directpurchase.ScPRIRemark;
import my.ep.persistence.entity.ss.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
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
	
	private String stockAvailability;
	private Long orgProfileId;
	
	private String priceType;
	private ScRequestSupplierItem scRequestSupplierItem = new ScRequestSupplierItem();
	private ScPrInquiry scPrInquiry = new ScPrInquiry();
	private CmSupplierItem supplierItem;
	
	private	DualListModel<PmUser>	approvalUsers;
	private	List<CmPricing>	priceTypes = new ArrayList<CmPricing>();
	private List<PmOrgProfile> userDivisionList;
	
	private List<ScRequestSupplierItem>	scRequestSupplierItems;
	
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
	 * @return the priceTypes
	 */
	public List<CmPricing> getPriceTypes() {
		return priceTypes;
	}


	/**
	 * @param priceTypes the priceTypes to set
	 */
	public void setPriceTypes(List<CmPricing> priceTypes) {
		this.priceTypes = priceTypes;
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
	 * @return the userDivisionList
	 */
	public List<PmOrgProfile> getUserDivisionList() {
		return userDivisionList;
	}

	/**
	 * @param userDivisionList the userDivisionList to set
	 */
	public void setUserDivisionList(List<PmOrgProfile> userDivisionList) {
		this.userDivisionList = userDivisionList;
	}

	/**
	 * @return the orgProfileId
	 */
	public Long getOrgProfileId() {
		return orgProfileId;
	}

	/**
	 * @param orgProfileId the orgProfileId to set
	 */
	public void setOrgProfileId(Long orgProfileId) {
		this.orgProfileId = orgProfileId;
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

	
	
	
}
