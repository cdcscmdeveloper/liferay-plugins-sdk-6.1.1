package my.ep.web.managedbean.cm.catalogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.persistence.entity.cm.catalog.CmFavoriteSupplier;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.CmWishlist;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.PricingVO;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "catalogueMB")
@SessionScoped
public class CatalogueManagedBean extends BaseManagedBean {



	private String itemName;
	private String itemCode;
	private String supplierName;
	private String contractNo;

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

	private CmSupplierItem supplierItemObj;

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

	private List<PmStatusDesc> statusDescList;

	private VCmSupplierItem[] selectViewItems;

	private List<VCmSupplierItem> viewSupplierItemList;
	private boolean contractedExist = false;

	private long[] stateId;

	private long[] districtId;

	private List<Long> districtLs;

	private List<Long> stateLs;

	private Integer tabActiveIndex = 0;


	private SearchFilterBean itemNameFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean itemCodeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean supplierNameFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean contractNoFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean circularNoFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean natureOfBusinessFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());

	private SearchFilterBean epNoFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean mofNoFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean bizRegNoFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());

	private SearchFilterBean districtFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
	private SearchFilterBean stateFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());

	private IPagingDataModel<VCmSupplierItem> searchDataModel = new SearchPagingDataModel();



	private class SearchPagingDataModel extends PagingDataModel<VCmSupplierItem> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("supplierItemID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<VCmSupplierItem> pageChangeListener(
				ISearchBean<VCmSupplierItem> searchBean) {

			if(StringUtils.isBlank((String)itemNameFilterBean.getSearchValue())
					&&StringUtils.isBlank((String)itemCodeFilterBean.getSearchValue())
					&&StringUtils.isBlank((String)supplierNameFilterBean.getSearchValue())
					&&StringUtils.isBlank((String)contractNoFilterBean.getSearchValue())
					&&StringUtils.isBlank((String)circularNoFilterBean.getSearchValue())
					&&StringUtils.isBlank((String)mofNoFilterBean.getSearchValue())
					&&StringUtils.isBlank((String)epNoFilterBean.getSearchValue())
					&&StringUtils.isBlank((String)bizRegNoFilterBean.getSearchValue())
					&&districtId == null) {
				System.out.println("******SearchResultBean******");
				return new SearchResultBean();
			}

			IViewSupplierItemSO viewSO = null;

			try {	

				viewSO = (IViewSupplierItemSO) BusinessDelegate.getInstance().getServiceObject("cm",IViewSupplierItemSO.class);


				if (StringUtils.isNotBlank((String)itemNameFilterBean.getSearchValue())) {
					itemNameFilterBean.setSearchField("itemName");
					searchBean.getDataFilterBeans().add(itemNameFilterBean);
				}

				if (StringUtils.isNotBlank((String)itemCodeFilterBean.getSearchValue())) {
					itemCodeFilterBean.setSearchField("itemCode");
					searchBean.getDataFilterBeans().add(itemCodeFilterBean);
				}

				if (StringUtils.isNotBlank((String)supplierNameFilterBean.getSearchValue())) {
					supplierNameFilterBean.setSearchField("companyName");
					searchBean.getDataFilterBeans().add(supplierNameFilterBean);
				}

				if (StringUtils.isNotBlank((String)contractNoFilterBean.getSearchValue())) {
					contractNoFilterBean.setSearchField("contractNo");
					searchBean.getDataFilterBeans().add(contractNoFilterBean);
				}

				if (StringUtils.isNotBlank((String)circularNoFilterBean.getSearchValue())) {
					circularNoFilterBean.setSearchField("circularNo");
					searchBean.getDataFilterBeans().add(circularNoFilterBean);
				}

				if (StringUtils.isNotBlank((String)epNoFilterBean.getSearchValue())) {
					epNoFilterBean.setSearchField("epNo");
					searchBean.getDataFilterBeans().add(epNoFilterBean);
				}

				if (StringUtils.isNotBlank((String)mofNoFilterBean.getSearchValue())) {
					mofNoFilterBean.setSearchField("mofNo");
					searchBean.getDataFilterBeans().add(mofNoFilterBean);
				}

				if (StringUtils.isNotBlank((String)bizRegNoFilterBean.getSearchValue())) {
					bizRegNoFilterBean.setSearchField("bizRegNo");
					searchBean.getDataFilterBeans().add(bizRegNoFilterBean);
				}

				setDistrictLs(new ArrayList<Long>());
				System.out.println("districtId:"+districtId);
				for(Long district : districtId){
					System.out.println("district:"+district);
					districtLs.add(district);
				}

				System.out.println("districtLs.size():"+districtLs.size());
				if(districtLs != null && districtLs.size()>0){
					districtFilterBean.setSearchField("districtId");
					districtFilterBean.setSearchValue(districtLs);
					searchBean.getDataFilterBeans().add(districtFilterBean);
				}

				setStateLs(new ArrayList<Long>());
				for(Long state : stateId){
					System.out.println("state:"+state);
					stateLs.add(state);
				}
				System.out.println("districtLs"+districtLs);
				if(districtLs.isEmpty()){
					if(stateLs!=null && stateLs.size()>0){
						stateFilterBean.setSearchField("stateId");
						stateFilterBean.setSearchValue(stateLs);
						searchBean.getDataFilterBeans().add(stateFilterBean);

					}

				}
				System.out.println("stateLs"+stateLs);





				CatalogueReqDTO reqDTO = new CatalogueReqDTO();
				reqDTO.setViewSearchBean(searchBean);
				System.out.println("*********b4***********");
				CatalogueResDTO resDTO = viewSO.searchViewSupplierItems(reqDTO);
				setViewSupplierItemList(resDTO.getViewSupplierItemList());
				System.out.println("*********after***********");
				return resDTO.getViewSuppItemResultBean();

			} finally {
				viewSO = null;
			}
		}

	}


	public void searchCatalogue(){
		searchDataModel.reloadData();
	}


	public SearchFilterBean getItemNameFilterBean() {
		return itemNameFilterBean;
	}



	public void setItemNameFilterBean(SearchFilterBean itemNameFilterBean) {
		this.itemNameFilterBean = itemNameFilterBean;
	}



	public IPagingDataModel<VCmSupplierItem> getSearchDataModel() {
		return searchDataModel;
	}



	public void setSearchDataModel(
			IPagingDataModel<VCmSupplierItem> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}




	public VCmSupplierItem[] getSelectViewItems() {
		return selectViewItems;
	}

	public void setSelectViewItems(VCmSupplierItem[] selectViewItems) {
		this.selectViewItems = selectViewItems;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

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
		compareLabel.add("Action");
		compareLabel.add("Item Name");
		compareLabel.add("Item Code");
		compareLabel.add("Price");
		compareLabel.add("Supplier Name");
		compareLabel.add("SKU No");
		compareLabel.add("Min Order");
		compareLabel.add("Max Order");
		compareLabel.add("Delivery Term");
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



	public CmSupplierItem getSupplierItemObj() {
		return supplierItemObj;
	}


	public void setSupplierItemObj(CmSupplierItem supplierItemObj) {
		this.supplierItemObj = supplierItemObj;
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
		searchCriteria.add(new SelectItem("Contract No", "Contract No"));
		searchCriteria.add(new SelectItem("nature of business", "nature of business"));
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

	public List<PmStatusDesc> getStatusDescList() {
		if(statusDescList==null)
			statusDescList = new ArrayList<PmStatusDesc>();
		return statusDescList;
	}

	public void setStatusDescList(List<PmStatusDesc> statusDescList) {
		this.statusDescList = statusDescList;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}


	public SearchFilterBean getItemCodeFilterBean() {
		return itemCodeFilterBean;
	}


	public void setItemCodeFilterBean(SearchFilterBean itemCodeFilterBean) {
		this.itemCodeFilterBean = itemCodeFilterBean;
	}


	public SearchFilterBean getSupplierNameFilterBean() {
		return supplierNameFilterBean;
	}


	public void setSupplierNameFilterBean(SearchFilterBean supplierNameFilterBean) {
		this.supplierNameFilterBean = supplierNameFilterBean;
	}


	public SearchFilterBean getContractNoFilterBean() {
		return contractNoFilterBean;
	}


	public void setContractNoFilterBean(SearchFilterBean contractNoFilterBean) {
		this.contractNoFilterBean = contractNoFilterBean;
	}


	public SearchFilterBean getCircularNoFilterBean() {
		return circularNoFilterBean;
	}


	public void setCircularNoFilterBean(SearchFilterBean circularNoFilterBean) {
		this.circularNoFilterBean = circularNoFilterBean;
	}


	public SearchFilterBean getNatureOfBusinessFilterBean() {
		return natureOfBusinessFilterBean;
	}


	public void setNatureOfBusinessFilterBean(
			SearchFilterBean natureOfBusinessFilterBean) {
		this.natureOfBusinessFilterBean = natureOfBusinessFilterBean;
	}


	public SearchFilterBean getEpNoFilterBean() {
		return epNoFilterBean;
	}


	public void setEpNoFilterBean(SearchFilterBean epNoFilterBean) {
		this.epNoFilterBean = epNoFilterBean;
	}


	public SearchFilterBean getMofNoFilterBean() {
		return mofNoFilterBean;
	}


	public void setMofNoFilterBean(SearchFilterBean mofNoFilterBean) {
		this.mofNoFilterBean = mofNoFilterBean;
	}


	public SearchFilterBean getBizRegNoFilterBean() {
		return bizRegNoFilterBean;
	}


	public void setBizRegNoFilterBean(SearchFilterBean bizRegNoFilterBean) {
		this.bizRegNoFilterBean = bizRegNoFilterBean;
	}


	public List<VCmSupplierItem> getViewSupplierItemList() {
		if(viewSupplierItemList==null)
			viewSupplierItemList = new ArrayList<VCmSupplierItem>();
		return viewSupplierItemList;
	}


	public void setViewSupplierItemList(List<VCmSupplierItem> viewSupplierItemList) {
		this.viewSupplierItemList = viewSupplierItemList;
	}


	public boolean isContractedExist() {
		return contractedExist;
	}


	public void setContractedExist(boolean contractedExist) {
		this.contractedExist = contractedExist;
	}


	public long[] getStateId() {
		return stateId;
	}


	public void setStateId(long[] stateId) {
		this.stateId = stateId;
	}


	public long[] getDistrictId() {
		return districtId;
	}


	public void setDistrictId(long[] districtId) {
		this.districtId = districtId;
	}


	public SearchFilterBean getDistrictFilterBean() {
		return districtFilterBean;
	}


	public void setDistrictFilterBean(SearchFilterBean districtFilterBean) {
		this.districtFilterBean = districtFilterBean;
	}


	public List<Long> getDistrictLs() {
		if(districtLs==null)
			districtLs = new ArrayList<Long>();
		return districtLs;
	}


	public void setDistrictLs(List<Long> districtLs) {
		this.districtLs = districtLs;
	}


	public List<Long> getStateLs() {
		if(stateLs==null)
			stateLs = new ArrayList<Long>();
		return stateLs;
	}


	public void setStateLs(List<Long> stateLs) {
		this.stateLs = stateLs;
	}


	public SearchFilterBean getStateFilterBean() {
		return stateFilterBean;
	}


	public void setStateFilterBean(SearchFilterBean stateFilterBean) {
		this.stateFilterBean = stateFilterBean;
	}


	public Integer getTabActiveIndex() {
		return tabActiveIndex;
	}


	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	





}
