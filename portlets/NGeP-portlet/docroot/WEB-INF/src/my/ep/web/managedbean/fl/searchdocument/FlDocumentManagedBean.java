package my.ep.web.managedbean.fl.searchdocument;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.CmWishlist;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.PricingVO;

@ManagedBean(name = "documentSearchMB")
@SessionScoped
public class FlDocumentManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String docType;
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
	
	private List<FlFulfilmentRequest> documentItemList;
	private FlFulfilmentRequest selectedDocItem;
	
	private List<CmWishlist> wishLists;
	
	private Integer compareIndex;
	
	private CmSupplierItem catalogueObj;
	
	private List<CmSupplierItem> rejectedItemList;
	private List<CmSupplierItem> approvalItemList;
	private List<CmSupplierItem> approvedItemList;
	private List<CmSupplierItem> publishedItemList;
	
	private CmSupplierItem[] selectedRejectedItems;
	private CmSupplierItem[] selectedApprovalItems;
	private CmSupplierItem[] selectedApprovedItems;
	private CmSupplierItem[] selectedPublishedItems;
	
	private String tempLoginId;
	private String tempDocType;
	
	/**
	 * @return the tempLoginId
	 */
	public String getTempLoginId() {
		return tempLoginId;
	}

	/**
	 * @param tempLoginId the tempLoginId to set
	 */
	public void setTempLoginId(String tempLoginId) {
		this.tempLoginId = tempLoginId;
	}

	private FlFulfilmentRequest fulfilmentRequest=new FlFulfilmentRequest();
	
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

	/**
	 * @return the selectedDocItem
	 */
	public FlFulfilmentRequest getSelectedDocItem() {
		return selectedDocItem;
	}

	/**
	 * @param selectedDocItem the selectedDocItem to set
	 */
	public void setSelectedDocItem(FlFulfilmentRequest selectedDocItem) {
		this.selectedDocItem = selectedDocItem;
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
		return selectedMaintainItem;
	}

	public void setSelectedMaintainItem(CmSupplierItem selectedMaintainItem) {
		this.selectedMaintainItem = selectedMaintainItem;
	}

	public CmSupplierItem[] getSelectedMaintainItems() {
		return selectedMaintainItems;
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

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
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

	/**
	 * @return the documentItemList
	 */
	public List<FlFulfilmentRequest> getDocumentItemList() {
		if(documentItemList==null){
			documentItemList=new ArrayList<FlFulfilmentRequest>();
		}
		
		return documentItemList;
	}

	/**
	 * @param documentItemList the documentItemList to set
	 */
	public void setDocumentItemList(List<FlFulfilmentRequest> documentItemList) {
		this.documentItemList = documentItemList;
	}

	/**
	 * @return the tempDocType
	 */
	public String getTempDocType() {
		return tempDocType;
	}

	/**
	 * @param tempDocType the tempDocType to set
	 */
	public void setTempDocType(String tempDocType) {
		this.tempDocType = tempDocType;
	}
	
}
