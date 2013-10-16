package my.ep.web.managedbean.cm.catalogue;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.CmWishlist;
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
	//private List<PricingVO> supplierItemList;
	private List<CmSupplierItem> selectedSupplierItemList;
	private CmSupplierItem[] selectedSupplierItems;
	private String navigationURL;

	private List<SelectItem> pricingCriteria;
	private List<PricingVO> pricingList;
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

	public List<PricingVO> getPricingList() {

		if(pricingList==null)
			pricingList = new ArrayList<PricingVO>();
		return pricingList;
	}

	public void setPricingList(List<PricingVO> pricingList) {
		this.pricingList = pricingList;
	}


	public List<SelectItem> getPricingCriteria() {
		pricingCriteria = new ArrayList<SelectItem>();
		pricingCriteria.add(new SelectItem("normal", "Normal"));
		pricingCriteria.add(new SelectItem("promotion", "Promotion"));
		pricingCriteria.add(new SelectItem("tier", "Tier"));
		pricingCriteria.add(new SelectItem("promotionTier", "Promotion Tier"));
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



}
