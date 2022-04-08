/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.purchaseinquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPIZone;
import my.ep.persistence.entity.sc.directpurchase.ScPRIRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;
// TODO: Auto-generated Javadoc

/**
 * The Class PurchaseInquiryManagedBean.
 *
 * @author sri
 */
@ManagedBean(name="purchaseInquiryMB")
@SessionScoped
public class PurchaseInquiryManagedBean extends BaseManagedBean{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The method. */
	private String method = ParameterConst.METHOD_PRI;
	
	/** The stock availability. */
	private String stockAvailability;
	
	/** The procurement type. */
	private String procurementType;
	
	/** The justification type. */
	private String justificationType;
	
	/** The price type. */
	private String priceType;
	
	/** The pm org profile. */
	private PmOrgProfile pmOrgProfile;
	
	/** The sc request supplier item. */
	private ScRequestSupplierItem scRequestSupplierItem = new ScRequestSupplierItem();
	
	/** The sc pr inquiry. */
	private ScPrInquiry scPrInquiry = new ScPrInquiry();
	
	/** The supplier item. */
	private CmSupplierItem supplierItem;
	
	/** The approval users. */
	private	DualListModel<PmUser>	approvalUsers;
	
	/** The sc request supplier items. */
	private List<ScRequestSupplierItem>	scRequestSupplierItems;
	
	/** The request note dtl. */
	private ScRequestNoteDtl requestNoteDtl = new ScRequestNoteDtl();
	
	/** The just type option. */
	private boolean justTypeOption;
	
	/** The proc type option. */
	private boolean procTypeOption;
	
	/** The just dropdown. */
	private SelectItem[] justDropdown;
	
	/** The price types. */
	private SelectItem[] priceTypes; // price type drop down 
	
	/** The pri status. */
	private PmStatus priStatus;
	
	/** The stock available. */
	private Integer stockAvailable;
	
	/** The stock availability flag. */
	private boolean stockAvailabilityFlag;
	
	/** The procurement types. */
	private SelectItem[] procurementTypes; // for procurement type dropdown
	
	/** The title checker. */
	private boolean titleChecker = Boolean.TRUE;
	
	/** The view only. */
	private boolean viewOnly;
	
	private boolean disProcTyp = Boolean.FALSE;
	
	/** The sc pi zone. */
	private ScPIZone scPIZone = new ScPIZone(); 
	
	/** The request note. */
	private	ScRequestNote	requestNote;
    
    /** The zone dtl map. */
    private	Map<String, TplSpecZoneDtl>	zoneDtlMap;
    
	/** The nav to previous page. */
	private String navToPreviousPage;
    
    /** The action. */
    private String action;
    
	/** The dp obj. */
	private Object[] dpObj = new Object[6];
	
	/** The tracking dairy view. */
	private boolean trackingDairyView;
	
	/** The show button. */
	private boolean showButton;
	
	 /** The show back button. */
     private boolean showBackButton;
     
     /** The supplier item prices. */
     private List<ScSupplierItemPrice>	supplierItemPrices;
     
     //29557 - start
     private ScSupplierItemPrice supplierItemStandardPrice;
     
     private ScSupplierItemPrice supplierItemPromotionPrice;
     
     private List<ScSupplierItemPrice>	supplierItemNormalPrices;
     
     private List<ScSupplierItemPrice>	supplierItemPromoPrices;
     
     private List<ScSupplierItemPrice>	supplierItemTierStandardPrices;
     
     private List<ScSupplierItemPrice>	supplierItemTierPromotionPrices;
     
     private List<ScSupplierItemPrice>	supplierItemZonalStandardPrices;
     
     private List<ScSupplierItemPrice>	supplierItemZonalPromotionPrices;
     
     private List<ScSupplierItemPrice>	supplierItemZonalTierStandardPrices;
     
     private List<ScSupplierItemPrice>	supplierItemZonalTierPromotionPrices;
     
 	 private List<ScSupplierItemPrice>	deleteSupplierItemTierStandardPrices;
     
     private List<ScSupplierItemPrice>	deleteSupplierItemTierPromotionPrices;
     
     private List<ScSupplierItemPrice>	deleteSupplierItemZonalStandardPrices;
     
     private List<ScSupplierItemPrice>	deleteSupplierItemZonalPromotionPrices;
     
     private List<ScSupplierItemPrice>	deleteSupplierItemZonalTierStandardPrices;
     
     private List<ScSupplierItemPrice>	deleteSupplierItemZonalTierPromotionPrices;
     
     private boolean enableTierNormalDel = Boolean.FALSE;
     
     private boolean enableTierPromoDel = Boolean.FALSE;
     
     private boolean enableZonalTierNormalDel = Boolean.FALSE;
     
     private boolean enableZonalTierPromoDel = Boolean.FALSE;
     
     private boolean enableZonalNormalDel = Boolean.FALSE;
     
     private boolean enableZonalPromoDel = Boolean.FALSE;
          
     private List<TplSpecZoneDtl> supplierZoneList;
     
     /** The pricing tab index. */
 	private Integer pricingTabIndex = 0;

 	/** The normal pricing index. */
 	private Integer normalPricingIndex = 0;

 	/** The tier pricing index. */
 	private Integer tierPricingIndex = 0;

 	/** The zonal pricing index. */
 	private Integer zonalPricingIndex = 0;

 	/** The zonal tier pricing index. */
 	private Integer zonalTierPricingIndex = 0;
     //29557 - end
     
     /** The auto update. */
 	private boolean autoUpdate = Boolean.TRUE;
 	
 	/** The disabled remark. */
	 private boolean disabledRemark;
    
    /** The spki backing bean. */
    @ManagedProperty(value="#{spkiBackingBean}")
    private SpkiBackingBean spkiBackingBean;
    
    /**
     * Gets the spki backing bean.
     *
     * @return the spki backing bean
     */
    public SpkiBackingBean getSpkiBackingBean() {
    	return spkiBackingBean;
    }

    /**
     * Sets the spki backing bean.
     *
     * @param spkiBackingBean the new spki backing bean
     */
    public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
    	this.spkiBackingBean = spkiBackingBean;
    }

	/**
	 * Gets the zone dtl map.
	 *
	 * @return the zone dtl map
	 */
	public Map<String, TplSpecZoneDtl> getZoneDtlMap() {
		return zoneDtlMap;
	}

	/**
	 * Sets the zone dtl map.
	 *
	 * @param zoneDtlMap the zone dtl map
	 */
	public void setZoneDtlMap(Map<String, TplSpecZoneDtl> zoneDtlMap) {
		this.zoneDtlMap = zoneDtlMap;
	}

	/**
	 * Instantiates a new purchase inquiry managed bean.
	 */
	public PurchaseInquiryManagedBean() {
		List<ScPRIRemark> remarks = new ArrayList<ScPRIRemark>();
		scPrInquiry.setRemarks(remarks);
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
	 * @param scRequestSupplierItem the scRequestSupplierItem to set
	 */
	public void setScRequestSupplierItem(ScRequestSupplierItem scRequestSupplierItem) {
		this.scRequestSupplierItem = scRequestSupplierItem;
	}

	/**
	 * Gets the stock availability.
	 *
	 * @return the stockAvailability
	 */
	public String getStockAvailability() {
		return stockAvailability;
	}

	/**
	 * Sets the stock availability.
	 *
	 * @param stockAvailability the stockAvailability to set
	 */
	public void setStockAvailability(String stockAvailability) {
		this.stockAvailability = stockAvailability;
	}

	/**
	 * Gets the price type.
	 *
	 * @return the priceType
	 */
	public String getPriceType() {
		return priceType;
	}

	/**
	 * Sets the price type.
	 *
	 * @param priceType the priceType to set
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	/**
	 * Gets the sc pr inquiry.
	 *
	 * @return the scPrInquiry
	 */
	public ScPrInquiry getScPrInquiry() {
		return scPrInquiry;
	}

	/**
	 * Sets the sc pr inquiry.
	 *
	 * @param scPrInquiry the scPrInquiry to set
	 */
	public void setScPrInquiry(ScPrInquiry scPrInquiry) {
		this.scPrInquiry = scPrInquiry;
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
	 * @param approvalUsers the approvalUsers to set
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
	 * @param supplierItem the supplierItem to set
	 */
	public void setSupplierItem(CmSupplierItem supplierItem) {
		this.supplierItem = supplierItem;
	}
	
	/**
	 * Gets the sc request supplier items.
	 *
	 * @return the scRequestSupplierItems
	 */
	public List<ScRequestSupplierItem> getScRequestSupplierItems() {
		return scRequestSupplierItems;
	}
	
	/**
	 * Sets the sc request supplier items.
	 *
	 * @param scRequestSupplierItems the scRequestSupplierItems to set
	 */
	public void setScRequestSupplierItems(
			List<ScRequestSupplierItem> scRequestSupplierItems) {
		this.scRequestSupplierItems = scRequestSupplierItems;
	}

	/**
	 * Gets the procurement type.
	 *
	 * @return the procurementType
	 */
	public String getProcurementType() {
		return procurementType;
	}

	/**
	 * Sets the procurement type.
	 *
	 * @param procurementType the procurementType to set
	 */
	public void setProcurementType(String procurementType) {
		this.procurementType = procurementType;
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
	 * @param justificationType the justificationType to set
	 */
	public void setJustificationType(String justificationType) {
		this.justificationType = justificationType;
	}

	/**
	 * Gets the request note dtl.
	 *
	 * @return the requestNoteDtl
	 */
	public ScRequestNoteDtl getRequestNoteDtl() {
		return requestNoteDtl;
	}

	/**
	 * Sets the request note dtl.
	 *
	 * @param requestNoteDtl the requestNoteDtl to set
	 */
	public void setRequestNoteDtl(ScRequestNoteDtl requestNoteDtl) {
		this.requestNoteDtl = requestNoteDtl;
	}

	/**
	 * Checks if is just type option.
	 *
	 * @return the justTypeOption
	 */
	public boolean isJustTypeOption() {
		return justTypeOption;
	}

	/**
	 * Sets the just type option.
	 *
	 * @param justTypeOption the justTypeOption to set
	 */
	public void setJustTypeOption(boolean justTypeOption) {
		this.justTypeOption = justTypeOption;
	}

	/**
	 * Checks if is proc type option.
	 *
	 * @return the procTypeOption
	 */
	public boolean isProcTypeOption() {
		return procTypeOption;
	}

	/**
	 * Sets the proc type option.
	 *
	 * @param procTypeOption the procTypeOption to set
	 */
	public void setProcTypeOption(boolean procTypeOption) {
		this.procTypeOption = procTypeOption;
	}

	/**
	 * Gets the just dropdown.
	 *
	 * @return the justDropdown
	 */
	public SelectItem[] getJustDropdown() {
		return justDropdown;
	}

	/**
	 * Sets the just dropdown.
	 *
	 * @param justDropdown the justDropdown to set
	 */
	public void setJustDropdown(SelectItem[] justDropdown) {
		this.justDropdown = justDropdown;
	}

	/**
	 * Gets the price types.
	 *
	 * @return the priceTypes
	 */
	public SelectItem[] getPriceTypes() {
		return priceTypes;
	}

	/**
	 * Sets the price types.
	 *
	 * @param priceTypes the priceTypes to set
	 */
	public void setPriceTypes(SelectItem[] priceTypes) {
		this.priceTypes = priceTypes;
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
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Gets the pri status.
	 *
	 * @return the priStatus
	 */
	public PmStatus getPriStatus() {
		return priStatus;
	}

	/**
	 * Sets the pri status.
	 *
	 * @param priStatus the priStatus to set
	 */
	public void setPriStatus(PmStatus priStatus) {
		this.priStatus = priStatus;
	}

	/**
	 * Gets the stock available.
	 *
	 * @return the stockAvailable
	 */
	public Integer getStockAvailable() {
		return stockAvailable;
	}

	/**
	 * Sets the stock available.
	 *
	 * @param stockAvailable the stockAvailable to set
	 */
	public void setStockAvailable(Integer stockAvailable) {
		this.stockAvailable = stockAvailable;
	}

	/**
	 * Gets the pm org profile.
	 *
	 * @return the pmOrgProfile
	 */
	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}

	/**
	 * Sets the pm org profile.
	 *
	 * @param pmOrgProfile the pmOrgProfile to set
	 */
	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
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
	 * @param titleChecker the titleChecker to set
	 */
	public void setTitleChecker(boolean titleChecker) {
		this.titleChecker = titleChecker;
	}

	/**
	 * Checks if is stock availability flag.
	 *
	 * @return the stockAvailabilityFlag
	 */
	public boolean isStockAvailabilityFlag() {
		return stockAvailabilityFlag;
	}

	/**
	 * Sets the stock availability flag.
	 *
	 * @param stockAvailabilityFlag the stockAvailabilityFlag to set
	 */
	public void setStockAvailabilityFlag(boolean stockAvailabilityFlag) {
		this.stockAvailabilityFlag = stockAvailabilityFlag;
	}

	/**
	 * Gets the procurement types.
	 *
	 * @return the procurementTypes
	 */
	public SelectItem[] getProcurementTypes() {
		return procurementTypes;
	}

	/**
	 * Sets the procurement types.
	 *
	 * @param procurementTypes the procurementTypes to set
	 */
	public void setProcurementTypes(SelectItem[] procurementTypes) {
		this.procurementTypes = procurementTypes;
	}


	/**
	 * @return the viewOnly
	 */
	public boolean isViewOnly() {
		return viewOnly;
	}

	/**
	 * @param viewOnly the viewOnly to set
	 */
	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}

	/**
	 * Gets the sc pi zone.
	 *
	 * @return the scPIZone
	 */
	public ScPIZone getScPIZone() {
		return scPIZone;
	}

	/**
	 * Sets the sc pi zone.
	 *
	 * @param scPIZone the scPIZone to set
	 */
	public void setScPIZone(ScPIZone scPIZone) {
		this.scPIZone = scPIZone;
	}

	/**
	 * Gets the request note.
	 *
	 * @return the requestNote
	 */
	public ScRequestNote getRequestNote() {
		return requestNote;
	}

	/**
	 * Sets the request note.
	 *
	 * @param requestNote the requestNote to set
	 */
	public void setRequestNote(ScRequestNote requestNote) {
		this.requestNote = requestNote;
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
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the navToPreviousPage
	 */
	public String getNavToPreviousPage() {
		return navToPreviousPage;
	}

	/**
	 * @param navToPreviousPage the navToPreviousPage to set
	 */
	public void setNavToPreviousPage(String navToPreviousPage) {
		this.navToPreviousPage = navToPreviousPage;
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
	 * @return the showButton
	 */
	public boolean isShowButton() {
		return showButton;
	}

	/**
	 * @param showButton the showButton to set
	 */
	public void setShowButton(boolean showButton) {
		this.showButton = showButton;
	}

	/**
	 * @return the showBackButton
	 */
	public boolean isShowBackButton() {
		return showBackButton;
	}

	/**
	 * @param showBackButton the showBackButton to set
	 */
	public void setShowBackButton(boolean showBackButton) {
		this.showBackButton = showBackButton;
	}

	/**
	 * @return the supplierItemPrices
	 */
	public List<ScSupplierItemPrice> getSupplierItemPrices() {
		return supplierItemPrices;
	}

	/**
	 * @param supplierItemPrices the supplierItemPrices to set
	 */
	public void setSupplierItemPrices(List<ScSupplierItemPrice> supplierItemPrices) {
		this.supplierItemPrices = supplierItemPrices;
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
	 * @return the disabledRemark
	 */
	public boolean isDisabledRemark() {
		return disabledRemark;
	}

	/**
	 * @param disabledRemark the disabledRemark to set
	 */
	public void setDisabledRemark(boolean disabledRemark) {
		this.disabledRemark = disabledRemark;
	}

	public boolean isDisProcTyp() {
		return disProcTyp;
	}

	public void setDisProcTyp(boolean disProcTyp) {
		this.disProcTyp = disProcTyp;
	}
	
	//29557 - start
	public ScSupplierItemPrice getSupplierItemStandardPrice() {
		return supplierItemStandardPrice;
	}

	public void setSupplierItemStandardPrice(ScSupplierItemPrice supplierItemStandardPrice) {
		this.supplierItemStandardPrice = supplierItemStandardPrice;
	}

	public ScSupplierItemPrice getSupplierItemPromotionPrice() {
		return supplierItemPromotionPrice;
	}

	public void setSupplierItemPromotionPrice(ScSupplierItemPrice supplierItemPromotionPrice) {
		this.supplierItemPromotionPrice = supplierItemPromotionPrice;
	}

	public List<ScSupplierItemPrice> getSupplierItemTierStandardPrices() {
		if (supplierItemTierStandardPrices == null) 
				supplierItemTierStandardPrices = new ArrayList<ScSupplierItemPrice>();
		
		return supplierItemTierStandardPrices;
	}

	public void setSupplierItemTierStandardPrices(List<ScSupplierItemPrice> supplierItemTierStandardPrices) {
		this.supplierItemTierStandardPrices = supplierItemTierStandardPrices;
	}

	public List<ScSupplierItemPrice> getSupplierItemTierPromotionPrices() {
		if (supplierItemTierPromotionPrices == null) 
				supplierItemTierPromotionPrices = new ArrayList<ScSupplierItemPrice>();
		
		return supplierItemTierPromotionPrices;
	}

	public void setSupplierItemTierPromotionPrices(List<ScSupplierItemPrice> supplierItemTierPromotionPrices) {
		this.supplierItemTierPromotionPrices = supplierItemTierPromotionPrices;
	}

	public List<ScSupplierItemPrice> getSupplierItemZonalStandardPrices() {
		if (supplierItemZonalStandardPrices == null) 
				supplierItemZonalStandardPrices = new ArrayList<ScSupplierItemPrice>();
		
		return supplierItemZonalStandardPrices;
	}

	public void setSupplierItemZonalStandardPrices(List<ScSupplierItemPrice> supplierItemZonalStandardPrices) {
		this.supplierItemZonalStandardPrices = supplierItemZonalStandardPrices;
	}

	public List<ScSupplierItemPrice> getSupplierItemZonalPromotionPrices() {
		if (supplierItemZonalPromotionPrices == null) 
				supplierItemZonalPromotionPrices = new ArrayList<ScSupplierItemPrice>();
		
		return supplierItemZonalPromotionPrices;
	}

	public void setSupplierItemZonalPromotionPrices(List<ScSupplierItemPrice> supplierItemZonalPromotionPrices) {
		this.supplierItemZonalPromotionPrices = supplierItemZonalPromotionPrices;
	}

	public List<ScSupplierItemPrice> getSupplierItemZonalTierStandardPrices() {
		if (supplierItemZonalTierStandardPrices == null) 
				supplierItemZonalTierStandardPrices = new ArrayList<ScSupplierItemPrice>();
		
		return supplierItemZonalTierStandardPrices;
	}

	public void setSupplierItemZonalTierStandardPrices(List<ScSupplierItemPrice> supplierItemZonalTierStandardPrices) {
		this.supplierItemZonalTierStandardPrices = supplierItemZonalTierStandardPrices;
	}

	public List<ScSupplierItemPrice> getSupplierItemZonalTierPromotionPrices() {
		if (supplierItemZonalTierPromotionPrices == null) 
				supplierItemZonalTierPromotionPrices = new ArrayList<ScSupplierItemPrice>();
		
		return supplierItemZonalTierPromotionPrices;
	}

	public void setSupplierItemZonalTierPromotionPrices(List<ScSupplierItemPrice> supplierItemZonalTierPromotionPrices) {
		this.supplierItemZonalTierPromotionPrices = supplierItemZonalTierPromotionPrices;
	}

	public Integer getPricingTabIndex() {
		return pricingTabIndex;
	}

	public void setPricingTabIndex(Integer pricingTabIndex) {
		this.pricingTabIndex = pricingTabIndex;
	}

	public Integer getNormalPricingIndex() {
		return normalPricingIndex;
	}

	public void setNormalPricingIndex(Integer normalPricingIndex) {
		this.normalPricingIndex = normalPricingIndex;
	}

	public Integer getTierPricingIndex() {
		return tierPricingIndex;
	}

	public void setTierPricingIndex(Integer tierPricingIndex) {
		this.tierPricingIndex = tierPricingIndex;
	}

	public Integer getZonalPricingIndex() {
		return zonalPricingIndex;
	}

	public void setZonalPricingIndex(Integer zonalPricingIndex) {
		this.zonalPricingIndex = zonalPricingIndex;
	}

	public Integer getZonalTierPricingIndex() {
		return zonalTierPricingIndex;
	}

	public void setZonalTierPricingIndex(Integer zonalTierPricingIndex) {
		this.zonalTierPricingIndex = zonalTierPricingIndex;
	}

	public List<ScSupplierItemPrice> getDeleteSupplierItemTierStandardPrices() {
		return deleteSupplierItemTierStandardPrices;
	}

	public void setDeleteSupplierItemTierStandardPrices(List<ScSupplierItemPrice> deleteSupplierItemTierStandardPrices) {
		this.deleteSupplierItemTierStandardPrices = deleteSupplierItemTierStandardPrices;
	}

	public List<ScSupplierItemPrice> getDeleteSupplierItemTierPromotionPrices() {
		return deleteSupplierItemTierPromotionPrices;
	}

	public void setDeleteSupplierItemTierPromotionPrices(List<ScSupplierItemPrice> deleteSupplierItemTierPromotionPrices) {
		this.deleteSupplierItemTierPromotionPrices = deleteSupplierItemTierPromotionPrices;
	}

	public List<ScSupplierItemPrice> getDeleteSupplierItemZonalStandardPrices() {
		return deleteSupplierItemZonalStandardPrices;
	}

	public void setDeleteSupplierItemZonalStandardPrices(List<ScSupplierItemPrice> deleteSupplierItemZonalStandardPrices) {
		this.deleteSupplierItemZonalStandardPrices = deleteSupplierItemZonalStandardPrices;
	}

	public List<ScSupplierItemPrice> getDeleteSupplierItemZonalPromotionPrices() {
		return deleteSupplierItemZonalPromotionPrices;
	}

	public void setDeleteSupplierItemZonalPromotionPrices(List<ScSupplierItemPrice> deleteSupplierItemZonalPromotionPrices) {
		this.deleteSupplierItemZonalPromotionPrices = deleteSupplierItemZonalPromotionPrices;
	}

	public List<ScSupplierItemPrice> getDeleteSupplierItemZonalTierStandardPrices() {
		return deleteSupplierItemZonalTierStandardPrices;
	}

	public void setDeleteSupplierItemZonalTierStandardPrices(
			List<ScSupplierItemPrice> deleteSupplierItemZonalTierStandardPrices) {
		this.deleteSupplierItemZonalTierStandardPrices = deleteSupplierItemZonalTierStandardPrices;
	}

	public List<ScSupplierItemPrice> getDeleteSupplierItemZonalTierPromotionPrices() {
		return deleteSupplierItemZonalTierPromotionPrices;
	}

	public void setDeleteSupplierItemZonalTierPromotionPrices(
			List<ScSupplierItemPrice> deleteSupplierItemZonalTierPromotionPrices) {
		this.deleteSupplierItemZonalTierPromotionPrices = deleteSupplierItemZonalTierPromotionPrices;
	}

	public boolean isEnableTierNormalDel() {
		return enableTierNormalDel;
	}

	public void setEnableTierNormalDel(boolean enableTierNormalDel) {
		this.enableTierNormalDel = enableTierNormalDel;
	}

	public boolean isEnableTierPromoDel() {
		return enableTierPromoDel;
	}

	public void setEnableTierPromoDel(boolean enableTierPromoDel) {
		this.enableTierPromoDel = enableTierPromoDel;
	}

	public boolean isEnableZonalTierNormalDel() {
		return enableZonalTierNormalDel;
	}

	public void setEnableZonalTierNormalDel(boolean enableZonalTierNormalDel) {
		this.enableZonalTierNormalDel = enableZonalTierNormalDel;
	}

	public boolean isEnableZonalTierPromoDel() {
		return enableZonalTierPromoDel;
	}

	public void setEnableZonalTierPromoDel(boolean enableZonalTierPromoDel) {
		this.enableZonalTierPromoDel = enableZonalTierPromoDel;
	}

	public boolean isEnableZonalNormalDel() {
		return enableZonalNormalDel;
	}

	public void setEnableZonalNormalDel(boolean enableZonalNormalDel) {
		this.enableZonalNormalDel = enableZonalNormalDel;
	}

	public boolean isEnableZonalPromoDel() {
		return enableZonalPromoDel;
	}

	public void setEnableZonalPromoDel(boolean enableZonalPromoDel) {
		this.enableZonalPromoDel = enableZonalPromoDel;
	}

	public List<TplSpecZoneDtl> getSupplierZoneList() {
		if (supplierZoneList == null)
			supplierZoneList = new ArrayList<TplSpecZoneDtl>();

		return supplierZoneList;
	}
	
	public void setSupplierZoneList(List<TplSpecZoneDtl> supplierZoneList) {
		this.supplierZoneList = supplierZoneList;
	}

	public List<ScSupplierItemPrice> getSupplierItemNormalPrices() {
		if (supplierItemNormalPrices == null)
			supplierItemNormalPrices = new ArrayList<ScSupplierItemPrice>();

		return supplierItemNormalPrices;
	}

	public void setSupplierItemNormalPrices(List<ScSupplierItemPrice> supplierItemNormalPrices) {
		this.supplierItemNormalPrices = supplierItemNormalPrices;
	}

	public List<ScSupplierItemPrice> getSupplierItemPromoPrices() {
		if (supplierItemPromoPrices == null)
			supplierItemPromoPrices = new ArrayList<ScSupplierItemPrice>();

		return supplierItemPromoPrices;
	}

	public void setSupplierItemPromoPrices(List<ScSupplierItemPrice> supplierItemPromoPrices) {
		this.supplierItemPromoPrices = supplierItemPromoPrices;
	}
	//29557 - end	
}
