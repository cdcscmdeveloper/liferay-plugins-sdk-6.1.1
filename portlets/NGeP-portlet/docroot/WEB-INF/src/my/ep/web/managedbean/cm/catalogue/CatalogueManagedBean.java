package my.ep.web.managedbean.cm.catalogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.cm.catalog.CmFavoriteSupplier;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.CmWishlist;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.PricingVO;

@ManagedBean(name = "catalogueMB")
@SessionScoped
public class CatalogueManagedBean extends BaseManagedBean {



	private String itemName;
	private String searchType="S";
	private String searchInd="and";
	private boolean advanceSearchInd=false;
	private List<AdvanceSearchVO> advanceSearchList;
	private List<SelectItem> searchCriteria;
	private List<CmSupplierItem> selectedSupplierItemList;
	private CmSupplierItem[] selectedSupplierItems;
	private String navigationURL;

	private List<SelectItem> pricingCriteria;
	private List<CmPricing> pricingList;
	private List<PricingVO> zonalPricingList;
	private List<PricingVO> districtPricingList;
	private PricingVO selectedPricing;
	private boolean districtIND = false;

	private CmUnspsc unspsc;

	private List<CmSupplierItem> maintainItemList;
	private CmSupplierItem selectedMaintainItem;
	private CmSupplierItem[] selectedMaintainItems;
	
	private List<CmSupplierItem> supplierItemList;
	
	private List<CmWishlist> wishLists;
	
	private Integer compareIndex;
	
	private CmSupplierItem catalogueObj;
	
	private List<CmSupplierItem> rejectedItemList;
	private List<CmSupplierItem> approvalItemList;
	private List<CmSupplierItem> approvedItemList;
	private List<CmSupplierItem> publishedItemList;
	private List<CmSupplierItem> expiredItemList;
	
	
	private CmSupplierItem[] selectedRejectedItems;
	private CmSupplierItem[] selectedApprovalItems;
	private CmSupplierItem[] selectedApprovedItems;
	private CmSupplierItem[] selectedPublishedItems;
	private CmSupplierItem[] selectedExpiredItems;
	
	private List<CmSupplierItem> cartList;
	private List<CmSupplierItem> tempList;
	
	private List<CmFavoriteSupplier>favoriteSupplierList;
	
	private List<SelectItem> paymentTermCriteria;
	
	private List<String> compareLabel;
	
	private PmUom selectedLovUom;
	
	private Map tempMap = new HashMap();
	
	private List<CmPricing> promotionPriceList;
	private List<CmPricing> tierPriceList;
	private List<CmPricing> promotionTierPriceList;
	
	private List<PmState> stateList;

	private List<PmDistrict> districtList;
	
	
	

	public List<PmDistrict> getDistrictList() {
		if(districtList==null)
			districtList = new ArrayList<PmDistrict>();
		return districtList;
	}

	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}

	public List<PmState> getStateList() {
		if(stateList==null)
			stateList = new ArrayList<PmState>();
		return stateList;
	}

	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	public List<CmPricing> getPromotionPriceList() {
		if(promotionPriceList==null)
			promotionPriceList = new ArrayList<CmPricing>();
		return promotionPriceList;
	}

	public void setPromotionPriceList(List<CmPricing> promotionPriceList) {
		this.promotionPriceList = promotionPriceList;
	}

	public List<CmPricing> getTierPriceList() {
		if(tierPriceList==null)
			tierPriceList = new ArrayList<CmPricing>();
		return tierPriceList;
	}

	public void setTierPriceList(List<CmPricing> tierPriceList) {
		this.tierPriceList = tierPriceList;
	}

	public List<CmPricing> getPromotionTierPriceList() {
		if(promotionTierPriceList==null)
			promotionTierPriceList = new ArrayList<CmPricing>();
		return promotionTierPriceList;
	}

	public void setPromotionTierPriceList(List<CmPricing> promotionTierPriceList) {
		this.promotionTierPriceList = promotionTierPriceList;
	}

	public PmUom getSelectedLovUom() {
		if(selectedLovUom==null)
			selectedLovUom = new PmUom();
		return selectedLovUom;
	}

	public void setSelectedLovUom(PmUom selectedLovUom) {
		this.selectedLovUom = selectedLovUom;
	}

	public List<String> getCompareLabel() {
		compareLabel = new ArrayList<String>();
		compareLabel.add("Item Name");
		compareLabel.add("Price");
		compareLabel.add("Supplier Name");
		compareLabel.add("UOM");
		compareLabel.add("Payment Term");
		compareLabel.add("Additional Desc");
		
		
		
		return compareLabel;
	}

	public void setCompareLabel(List<String> compareLabel) {
		this.compareLabel = compareLabel;
	}

	public List<SelectItem> getPaymentTermCriteria() {
		paymentTermCriteria = new ArrayList<SelectItem>();
		paymentTermCriteria.add(new SelectItem("01", "3 MONTHS"));
		paymentTermCriteria.add(new SelectItem("02", "6 MONTHS"));
		paymentTermCriteria.add(new SelectItem("03", "9 MONTHS"));
		paymentTermCriteria.add(new SelectItem("04", "12 MONTHS"));
		return paymentTermCriteria;
	}

	public void setPaymentTermCriteria(List<SelectItem> paymentTermCriteria) {
		this.paymentTermCriteria = paymentTermCriteria;
	}

	public List<CmFavoriteSupplier> getFavoriteSupplierList() {
		if(favoriteSupplierList==null)
			favoriteSupplierList = new ArrayList<CmFavoriteSupplier>();
		return favoriteSupplierList;
	}

	public void setFavoriteSupplierList(
			List<CmFavoriteSupplier> favoriteSupplierList) {
		this.favoriteSupplierList = favoriteSupplierList;
	}

	public Map getTempMap() {
		return tempMap;
	}

	public void setTempMap(Map tempMap) {
		this.tempMap = tempMap;
	}

	public List<CmSupplierItem> getTempList() {
		if(tempList==null)
			tempList = new ArrayList<CmSupplierItem>();
		return tempList;
	}

	public void setTempList(List<CmSupplierItem> tempList) {
		this.tempList = tempList;
	}

	public List<CmSupplierItem> getCartList() {
		if(cartList==null)
			cartList = new ArrayList<CmSupplierItem>();
		return cartList;
	}

	public void setCartList(List<CmSupplierItem> cartList) {
		this.cartList = cartList;
	}

	public CmSupplierItem[] getSelectedRejectedItems() {
		return selectedRejectedItems;
	}

	public void setSelectedRejectedItems(CmSupplierItem[] selectedRejectedItems) {
		this.selectedRejectedItems = selectedRejectedItems;
	}

	public CmSupplierItem[] getSelectedApprovalItems() {
		return selectedApprovalItems;
	}

	public void setSelectedApprovalItems(CmSupplierItem[] selectedApprovalItems) {
		this.selectedApprovalItems = selectedApprovalItems;
	}

	public CmSupplierItem[] getSelectedApprovedItems() {
		return selectedApprovedItems;
	}

	public void setSelectedApprovedItems(CmSupplierItem[] selectedApprovedItems) {
		this.selectedApprovedItems = selectedApprovedItems;
	}

	public CmSupplierItem[] getSelectedPublishedItems() {
		return selectedPublishedItems;
	}

	public void setSelectedPublishedItems(CmSupplierItem[] selectedPublishedItems) {
		this.selectedPublishedItems = selectedPublishedItems;
	}

	public List<CmSupplierItem> getRejectedItemList() {
		if(rejectedItemList==null)
			rejectedItemList = new ArrayList<CmSupplierItem>();
		return rejectedItemList;
	}

	public void setRejectedItemList(List<CmSupplierItem> rejectedItemList) {
		this.rejectedItemList = rejectedItemList;
	}

	public List<CmSupplierItem> getApprovalItemList() {
		if(approvalItemList==null)
			approvalItemList = new ArrayList<CmSupplierItem>();
		return approvalItemList;
	}

	public void setApprovalItemList(List<CmSupplierItem> approvalItemList) {
		this.approvalItemList = approvalItemList;
	}

	public List<CmSupplierItem> getApprovedItemList() {
		if(approvedItemList==null)
			approvedItemList = new ArrayList<CmSupplierItem>();
		return approvedItemList;
	}

	public void setApprovedItemList(List<CmSupplierItem> approvedItemList) {
		this.approvedItemList = approvedItemList;
	}

	public List<CmSupplierItem> getPublishedItemList() {
		if(publishedItemList==null)
			publishedItemList = new ArrayList<CmSupplierItem>();
		return publishedItemList;
	}

	public void setPublishedItemList(List<CmSupplierItem> publishedItemList) {
		this.publishedItemList = publishedItemList;
	}

	public CmSupplierItem getCatalogueObj() {
		return catalogueObj;
	}

	public void setCatalogueObj(CmSupplierItem catalogueObj) {
		this.catalogueObj = catalogueObj;
	}

	public Integer getCompareIndex() {
		return compareIndex;
	}

	public void setCompareIndex(Integer compareIndex) {
		this.compareIndex = compareIndex;
	}

	public List<CmWishlist> getWishLists() {
		if(wishLists==null)
			wishLists = new ArrayList<CmWishlist>();
		return wishLists;
	}

	public void setWishLists(List<CmWishlist> wishLists) {
		this.wishLists = wishLists;
	}

	public List<CmSupplierItem> getMaintainItemList() {
		if(maintainItemList==null)
			maintainItemList = new ArrayList<CmSupplierItem>();
		return maintainItemList;
	}

	public void setMaintainItemList(List<CmSupplierItem> maintainItemList) {
		this.maintainItemList = maintainItemList;
	}

	public CmSupplierItem getSelectedMaintainItem() {
		if(selectedMaintainItem==null)
			selectedMaintainItem = new CmSupplierItem();
		return selectedMaintainItem;
	}

	public void setSelectedMaintainItem(CmSupplierItem selectedMaintainItem) {
		this.selectedMaintainItem = selectedMaintainItem;
	}

	public CmSupplierItem[] getSelectedMaintainItems() {
		return selectedMaintainItems;
	}

	public void setSelectedMaintainItems(CmSupplierItem[] selectedMaintainItems) {
		this.selectedMaintainItems = selectedMaintainItems;
	}

	public CmUnspsc getUnspsc() {
		if(unspsc==null)
			unspsc = new CmUnspsc();
		return unspsc;
	}

	public void setUnspsc(CmUnspsc unspsc) {
		this.unspsc = unspsc;
	}

	

	public List<CmSupplierItem> getSupplierItemList() {
		if(supplierItemList==null)
			supplierItemList = new ArrayList<CmSupplierItem>();
		return supplierItemList;
	}

	public void setSupplierItemList(List<CmSupplierItem> supplierItemList) {
		this.supplierItemList = supplierItemList;
	}

	

	public CmSupplierItem[] getSelectedSupplierItems() {
		return selectedSupplierItems;
	}

	public void setSelectedSupplierItems(CmSupplierItem[] selectedSupplierItems) {
		this.selectedSupplierItems = selectedSupplierItems;
	}

	public String getSearchInd() {
		return searchInd;
	}

	public void setSearchInd(String searchInd) {
		this.searchInd = searchInd;
	}

	public PricingVO getSelectedPricing() {
		if(selectedPricing==null)
			selectedPricing = new PricingVO();
		return selectedPricing;
	}

	public void setSelectedPricing(PricingVO selectedPricing) {
		this.selectedPricing = selectedPricing;
	}

	public List<PricingVO> getDistrictPricingList() {
		if(districtPricingList==null)
			districtPricingList=new ArrayList<PricingVO>();
		return districtPricingList;
	}

	public void setDistrictPricingList(List<PricingVO> districtPricingList) {
		this.districtPricingList = districtPricingList;
	}

	public List<CmPricing> getPricingList() {

		if(pricingList==null)
			pricingList = new ArrayList<CmPricing>();
		return pricingList;
	}

	public void setPricingList(List<CmPricing> pricingList) {
		this.pricingList = pricingList;
	}


	public List<SelectItem> getPricingCriteria() {
		pricingCriteria = new ArrayList<SelectItem>();
		pricingCriteria.add(new SelectItem("01", "NORMAL"));
		pricingCriteria.add(new SelectItem("02", "PROMOTION"));
		pricingCriteria.add(new SelectItem("03", "TIER"));
		pricingCriteria.add(new SelectItem("04", "PROMOTION TIER"));
		return pricingCriteria;
	}

	public void setPricingCriteria(List<SelectItem> pricingCriteria) {
		this.pricingCriteria = pricingCriteria;
	}

	public List<PricingVO> getZonalPricingList() {
		if(zonalPricingList==null)
			zonalPricingList = new ArrayList<PricingVO>();
		return zonalPricingList;
	}

	public void setZonalPricingList(List<PricingVO> zonalPricingList) {
		this.zonalPricingList = zonalPricingList;
	}

	public boolean isDistrictIND() {
		return districtIND;
	}

	public void setDistrictIND(boolean districtIND) {
		this.districtIND = districtIND;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public boolean isAdvanceSearchInd() {
		return advanceSearchInd;
	}

	public void setAdvanceSearchInd(boolean advanceSearchInd) {
		this.advanceSearchInd = advanceSearchInd;
	}

	public List<AdvanceSearchVO> getAdvanceSearchList() {
		return advanceSearchList;
	}

	public void setAdvanceSearchList(List<AdvanceSearchVO> advanceSearchList) {
		this.advanceSearchList = advanceSearchList;
	}

	public List<SelectItem> getSearchCriteria() {
		searchCriteria = new ArrayList<SelectItem>();
		searchCriteria.add(new SelectItem("Supplier", "Supplier Name"));
		searchCriteria.add(new SelectItem("Item", "Item Code"));
		searchCriteria.add(new SelectItem("State", "State"));
		searchCriteria.add(new SelectItem("District", "District"));

		return searchCriteria;
	}

	public void setSearchCriteria(List<SelectItem> searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public List<CmSupplierItem> getSelectedSupplierItemList() {
		if(selectedSupplierItemList==null)
			selectedSupplierItemList = new ArrayList<CmSupplierItem>();
		return selectedSupplierItemList;
	}

	public void setSelectedSupplierItemList(List<CmSupplierItem> selectedSupplierItemList) {
		this.selectedSupplierItemList = selectedSupplierItemList;
	}

	public String getNavigationURL() {
		return navigationURL;
	}

	public void setNavigationURL(String navigationURL) {
		this.navigationURL = navigationURL;
	}

	public List<CmSupplierItem> getExpiredItemList() {
		if(expiredItemList==null)
			expiredItemList = new ArrayList<CmSupplierItem>();
		return expiredItemList;
	}

	public void setExpiredItemList(List<CmSupplierItem> expiredItemList) {
		this.expiredItemList = expiredItemList;
	}

	public CmSupplierItem[] getSelectedExpiredItems() {
		return selectedExpiredItems;
	}

	public void setSelectedExpiredItems(CmSupplierItem[] selectedExpiredItems) {
		this.selectedExpiredItems = selectedExpiredItems;
	}

	

}
