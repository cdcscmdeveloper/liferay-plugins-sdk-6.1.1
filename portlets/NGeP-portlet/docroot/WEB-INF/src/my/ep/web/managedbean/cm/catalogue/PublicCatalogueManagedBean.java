package my.ep.web.managedbean.cm.catalogue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.VCmPubSuppItem;
import my.ep.type.aut.SearchOperator;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.cm.catalogue.PublicCatalogueBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;


/**
 * The Class PublicCatalogueManagedBean.
 * @author hketan
 */
@ManagedBean(name = "pCatalogueMB")
@SessionScoped
public class PublicCatalogueManagedBean extends BaseManagedBean {
	
	//#10747,#35739 and SonarLint fixing - Start
	private static Logger log = Logger
			.getLogger(PublicCatalogueManagedBean.class);
	//#10747,#35739 and SonarLint fixing - End

	/** The item name. */
	private String itemName;

	/** The family list. */
	private List <CmUnspsc> familyList;

	/** The catalogue list. */
	private List <VCmPubSuppItem> catalogueList;

	/** The popular commodity list. */
	private List <CmUnspsc> popularCommodityList;

	/** The front page. */
	private boolean frontPage = true;

	/** The price form. */
	private BigDecimal priceForm;

	/** The price to. */
	private BigDecimal priceTo;
	
	//#10747 - Shafie, Soo - Start
	/** The delivery from. */
	private Integer deliveryFrom;

	/** The delivery to. */
	private Integer deliveryTo;
	
	/** The item per page in thumbnail/list view. */
	private int itemPerPage;
	
	/** The sort by in thumbnail/list view. */
	private int sortBy;
	
	/** The visibility of the view. */
	private boolean visible = true;
	//#10747 - Shafie, Soo - End

	/** The family code. */
	private String familyCode;

	/** The commodity code. */
	private String commodityCode;

	/** The family code list. */
	private List<String> familyCodeList;

	/** The brand criteria. */
	private List<SelectItem> brandCriteria;

	/** The brand id. */
	private Long brandId;

	/** The key word. */
	private boolean keyWord = true;

	/** The unspsc title. */
	private String unspscTitle;

	/** The total number of cataloges. */
	private long totalNumberOfCataloges;

	/** The last search item name. */
	private String lastSearchItemName;

	/** The clone item names. */
	private List<String> cloneItemNames = new ArrayList<>();// SonarLint Issues Fixing...


	private int totalRecord = 0;

	private CmUnspsc selectedUnspscObj;

	private boolean cloneNameFlag = false;
	
    //#10747,#35739 and SonarLint fixing - Start
	private PagingDataModel<Object[]> searchDataModel = new SearchPagingDataModel();
	
	private boolean singleCommoditySelected = false;
	
	private boolean converterVisible = false;
	//#10747,#35739 and SonarLint fixing - End
	
	private SearchFilterBean keyWordFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	// #10747,#35739 and SonarLint fixing- Start
	private class SearchPagingDataModel extends PagingDataModel<Object[]> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("catalogueGridViewID");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			PublicCatalogueBackingBean bb = new PublicCatalogueBackingBean();

			bb.getManagedBean().setFamilyList(null);

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			ICatalogueSO catalogueSO = BusinessDelegate.getInstance()
					.getServiceObject("cm", ICatalogueSO.class);
			ICodificationSO codificationSO = BusinessDelegate.getInstance()
					.getServiceObject("cm", ICodificationSO.class);

			try {
				// filter catalog list
				searchFilterBean(bb, reqDTO);
				searchFamilyUnspscandBrandFilterBean(bb, reqDTO);

				log.info("itemName:" + reqDTO.getItemName());

				reqDTO.setPublicCatalogSearchBean(searchBean);
				// #10747 - Soo - Start
				reqDTO.setSortBy(bb.getManagedBean().getSortBy());
				// #10747 - Soo - End
				CatalogueResDTO resDTO = catalogueSO.searchCatalogueBy(reqDTO);

				int totalRecords = resDTO.getViewPublicSuppItemResultBean()
						.getTotalRecord();
				log.info("*******size*******" + totalRecords);
				log.info("isCloneNameFlag:"
						+ bb.getManagedBean().isCloneNameFlag());

				CodificationReqDTO codeReqDTO = new CodificationReqDTO();
				if (bb.getManagedBean().isSingleCommoditySelected()) {
					bb.getManagedBean().setFamilyList(null);
					codeReqDTO.setUnspscCode(bb.getManagedBean()
							.getSelectedUnspscObj().getUnspscCode());
					CodificationResDTO codeResDTO = codificationSO
							.searchUnspscObj(codeReqDTO);
					bb.getManagedBean().getFamilyList()
							.add(codeResDTO.getUnspscObj());

					CmUnspsc commodity = codeResDTO.getUnspscObj();
					bb.getManagedBean().setCommodityCode(
							commodity.getUnspscCode());
					for (CmBrand brand : commodity.getCmBrand()) {

						bb.getManagedBean()
								.getBrandCriteria()
								.add(new SelectItem(brand.getBrandId(), brand
										.getBrandName()));
					}

				} else {
					CatalogueResDTO resDTO1 = catalogueSO
							.searchPublicCatalogueByNamePriceDeliveryTerm(reqDTO); // #10747-Shafie

					if (bb.getManagedBean().isCloneNameFlag()) {
						bb.getManagedBean().setFamilyList(
								new ArrayList<CmUnspsc>());
						List<String> itemNames = new ArrayList<>();

						getFamilyCodeList(bb, resDTO1, itemNames);

					} else {

						bb.getManagedBean().setFamilyList(
								new ArrayList<CmUnspsc>());
						List<String> itemNames = new ArrayList<>();

						getFamilyCodeFamilyList(bb, resDTO1, itemNames);

					}
				}

				log.info("***************getFamilyList().size()*************"
						+ bb.getManagedBean().getFamilyList().size());
				log.info("getCloneItemNames:"
						+ bb.getManagedBean().getCloneItemNames().size());
				bb.getManagedBean().setTotalRecord(totalRecords);

				RequestContext.getCurrentInstance().execute(
						"refreshCatalogue();");

				return resDTO.getViewPublicSuppItemResultBean();

			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(
						ModuleCodeConst.CATALOGUE_MANAGEMENT, null, null,
						"PublicCatalogueManagedBean.SearchPagingDataModel",
						null, e);
			}
			return new SearchResultBean();
		}

	}

	public PagingDataModel<Object[]> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(PagingDataModel<Object[]> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	// #10747,#35739 and SonarLint fixing- End

	/**
	 * Gets the last search item name.
	 * 
	 * @return the last search item name
	 */
	public String getLastSearchItemName() {
		return lastSearchItemName;
	}

	/**
	 * Sets the last search item name.
	 * 
	 * @param lastSearchItemName
	 *            the new last search item name
	 */
	public void setLastSearchItemName(String lastSearchItemName) {
		this.lastSearchItemName = lastSearchItemName;
	}

	/**
	 * Gets the total number of cataloges.
	 * 
	 * @return the total number of cataloges
	 */
	public long getTotalNumberOfCataloges() {
		return totalNumberOfCataloges;
	}

	/**
	 * Sets the total number of cataloges.
	 * 
	 * @param totalNumberOfCataloges
	 *            the new total number of cataloges
	 */
	public void setTotalNumberOfCataloges(long totalNumberOfCataloges) {
		this.totalNumberOfCataloges = totalNumberOfCataloges;
	}

	/**
	 * Gets the item name.
	 * 
	 * @return the item name
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Sets the item name.
	 * 
	 * @param itemName
	 *            the new item name
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}


	
	// SonarLint Issues Fixing- Start
	public List<CmUnspsc> getFamilyList() {
		if(familyList==null)
			familyList = new ArrayList<>();
		return familyList;
	}

	public void setFamilyList(List<CmUnspsc> familyList) {
		this.familyList = familyList;
	}

	/**
	 * Gets the catalogue list.
	 * 
	 * @return the catalogue list
	 */
	public List<VCmPubSuppItem> getCatalogueList() {
		if(this.catalogueList == null){
			this.catalogueList = new ArrayList<>();
		}
		return catalogueList;
	}

	/**
	 * Sets the catalogue list.
	 * 
	 * @param catalogueList
	 *            the new catalogue list
	 */
	public void setCatalogueList(List<VCmPubSuppItem> catalogueList) {
		this.catalogueList = catalogueList;
	}

	/**
	 * Checks if is front page.
	 * 
	 * @return true, if is front page
	 */
	public boolean isFrontPage() {
		return frontPage;
	}


	/**
	 * Sets the front page.
	 * 
	 * @param frontPage
	 *            the new front page
	 */
	public void setFrontPage(boolean frontPage) {
		this.frontPage = frontPage;
	}


	/**
	 * Gets the price form.
	 * 
	 * @return the price form
	 */
	public BigDecimal getPriceForm() {
		return priceForm;
	}


	/**
	 * Sets the price form.
	 * 
	 * @param priceForm
	 *            the new price form
	 */
	public void setPriceForm(BigDecimal priceForm) {
		this.priceForm = priceForm;
	}


	/**
	 * Gets the price to.
	 * 
	 * @return the price to
	 */
	public BigDecimal getPriceTo() {
		return priceTo;
	}


	/**
	 * Sets the price to.
	 * 
	 * @param priceTo
	 *            the new price to
	 */
	public void setPriceTo(BigDecimal priceTo) {
		this.priceTo = priceTo;
	}


	//#10747 - Shafie, Soo - Start 
	/**
	 *  Gets the delivery from.
	 * 
	 * @return the delivery from
	 */
	public Integer getDeliveryFrom() {
		return deliveryFrom;
	}


	/**
	 * Sets the delivery from.
	 * 
	 * @param deliveryFrom
	 *            the new delivery from
	 */
	public void setDeliveryFrom(Integer deliveryFrom) {
		this.deliveryFrom = deliveryFrom;
	}


	/**
	 * Gets the delivery to.
	 * 
	 * @return the delivery to
	 */
	public Integer getDeliveryTo() {
		return deliveryTo;
	}


	/**
	 * Sets the delivery to.
	 * 
	 * @param deliveryTo
	 *            the new delivery to
	 */
	public void setDeliveryTo(Integer deliveryTo) {
		this.deliveryTo = deliveryTo;
	}


	/**
	 * Gets the item per page in thumbnail/list view.
	 * 
	 * @return the item per page
	 */
	public int getItemPerPage() {
		return itemPerPage;
	}


	/**
	 * Sets the item per page in thumbnail/list view.
	 * 
	 * @param itemPerPage
	 *            the new item per page
	 */
	public void setItemPerPage(int itemPerPage) {
		this.itemPerPage = itemPerPage;
	}


	/**
	 * Gets the sort by in thumbnail/list view.
	 * 
	 * @return the sort by
	 */
	public int getSortBy() {
		return sortBy;
	}


	/**
	 * Sets the sort by in thumbnail/list view.
	 * 
	 * @param sortBy
	 *            the new sort by
	 */
	public void setSortBy(int sortBy) {
		this.sortBy = sortBy;
	}


	/**
	 * Checks the visibility of the view.
	 * 
	 * @return true, if is visible thumbnail view
	 */
	public boolean isVisible() {
		return visible;
	}


	/**
	 * Sets the visibility of the view.
	 * 
	 * @param visible
	 *            the new visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	//#10747 - Shafie, Soo - End


	/**
	 * Gets the family code.
	 * 
	 * @return the family code
	 */
	public String getFamilyCode() {
		return familyCode;
	}


	/**
	 * Sets the family code.
	 * 
	 * @param familyCode
	 *            the new family code
	 */
	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}


	/**
	 * Gets the family code list.
	 * 
	 * @return the family code list
	 */
	public List<String> getFamilyCodeList() {
		if(familyCodeList==null){
			familyCodeList = new ArrayList<>();
		}
		return familyCodeList;
	}


	/**
	 * Sets the family code list.
	 * 
	 * @param familyCodeList
	 *            the new family code list
	 */
	public void setFamilyCodeList(List<String> familyCodeList) {
		this.familyCodeList = familyCodeList;
	}


	/**
	 * Gets the popular commodity list.
	 * 
	 * @return the popular commodity list
	 */
	public List<CmUnspsc> getPopularCommodityList() {
		if(popularCommodityList==null)
			popularCommodityList = new ArrayList<>();
		return popularCommodityList;
	}


	/**
	 * Sets the popular commodity list.
	 * 
	 * @param popularCommodityList
	 *            the new popular commodity list
	 */
	public void setPopularCommodityList(List<CmUnspsc> popularCommodityList) {
		this.popularCommodityList = popularCommodityList;
	}


	/**
	 * Gets the brand criteria.
	 * 
	 * @return the brand criteria
	 */
	public List<SelectItem> getBrandCriteria() {
		if (brandCriteria == null)
			brandCriteria = new ArrayList<>();
		return brandCriteria;
	}


	/**
	 * Sets the brand criteria.
	 * 
	 * @param brandCriteria
	 *            the new brand criteria
	 */
	public void setBrandCriteria(List<SelectItem> brandCriteria) {
		this.brandCriteria = brandCriteria;
	}


	/**
	 * Gets the brand id.
	 * 
	 * @return the brand id
	 */
	public Long getBrandId() {
		return brandId;
	}


	/**
	 * Sets the brand id.
	 * 
	 * @param brandId
	 *            the new brand id
	 */
	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}


	/**
	 * Checks if is key word.
	 * 
	 * @return true, if is key word
	 */
	public boolean isKeyWord() {
		return keyWord;
	}


	/**
	 * Sets the key word.
	 * 
	 * @param keyWord
	 *            the new key word
	 */
	public void setKeyWord(boolean keyWord) {
		this.keyWord = keyWord;
	}


	/**
	 * Gets the unspsc title.
	 * 
	 * @return the unspsc title
	 */
	public String getUnspscTitle() {
		return unspscTitle;
	}


	/**
	 * Sets the unspsc title.
	 * 
	 * @param unspscTitle
	 *            the new unspsc title
	 */
	public void setUnspscTitle(String unspscTitle) {
		this.unspscTitle = unspscTitle;
	}


	/**
	 * Gets the clone item names.
	 * 
	 * @return the clone item names
	 */
	public List<String> getCloneItemNames() {

		if(cloneItemNames==null)
			cloneItemNames = new ArrayList<>();
		return cloneItemNames;
	}


	/**
	 * Sets the clone item names.
	 * 
	 * @param cloneItemNames
	 *            the new clone item names
	 */
	public void setCloneItemNames(List<String> cloneItemNames) {
		this.cloneItemNames = cloneItemNames;
	}

	/**
	 * Gets the commodity code.
	 * 
	 * @return the commodity code
	 */
	public String getCommodityCode() {
		return commodityCode;
	}

	/**
	 * Sets the commodity code.
	 * 
	 * @param commodityCode
	 *            the new commodity code
	 */
	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public CmUnspsc getSelectedUnspscObj() {
		return selectedUnspscObj;
	}

	public void setSelectedUnspscObj(CmUnspsc selectedUnspscObj) {
		this.selectedUnspscObj = selectedUnspscObj;
	}
	
	/**
	 * Reset searchDataModel
	 * 
	 * @param SearchPagingDataModel
	 *            the new SearchPagingDataModel
	 */
	public void reset(){
		this.setSearchDataModel(new SearchPagingDataModel());
	}
	// SonarLint Issues Fixing- End

	public boolean isCloneNameFlag() {
		return cloneNameFlag;
	}

	public void setCloneNameFlag(boolean cloneNameFlag) {
		this.cloneNameFlag = cloneNameFlag;
	}

	public SearchFilterBean getKeyWordFilterBean() {
		return keyWordFilterBean;
	}

	public void setKeyWordFilterBean(SearchFilterBean keyWordFilterBean) {
		this.keyWordFilterBean = keyWordFilterBean;
	}
	
	// #10747 and SonarLint fixing- Start
	public boolean isSingleCommoditySelected() {
		return singleCommoditySelected;
	}

	public void setSingleCommoditySelected(boolean singleCommoditySelected) {
		this.singleCommoditySelected = singleCommoditySelected;
	}
	
	/**
	 * Checks the Converter visible.
	 * 
	 * @return true, if is Converter visible view
	 */
	public boolean istConverterVisible() {
		return converterVisible;
	}


	/**
	 * Sets the Converter visible.
	 * 
	 * @param converterVisible
	 *            the new converterVisible
	 */
	public void setConverterVisible(boolean converterVisible) {
		this.converterVisible = converterVisible;
	}

	/**
	 * @param bb
	 * @param reqDTO
	 */
	public void searchFilterBean(PublicCatalogueBackingBean bb,
			CatalogueReqDTO reqDTO) {

		if (isItemKeywordNotNull(bb) && bb.getManagedBean().isKeyWord()) {
			reqDTO.setItemName(bb.getManagedBean().getItemName());
		}
		if (isUnspscNotNull(bb) && !bb.getManagedBean().isKeyWord()) {
			reqDTO.setItemName(bb.getManagedBean().getUnspscTitle());
		}

		if (isPriceFromNotNull(bb)) {
			reqDTO.setPriceFrom(bb.getManagedBean().getPriceForm());
		}
		if (isPriceToNotNull(bb)) {
			reqDTO.setPriceTo(bb.getManagedBean().getPriceTo());
		}

		// #10747 - Shafie, Soo - Start
		if (isDeliveryFromNotNull(bb)) {
			reqDTO.setDeliveryFrom(bb.getManagedBean().getDeliveryFrom());
		}
		if (isDeliveryToNotNull(bb)) {
			reqDTO.setDeliveryTo(bb.getManagedBean().getDeliveryTo());
		}
		// #10747 - Shafie, Soo - End
	}

	/**
	 * @param bb
	 * @param reqDTO
	 */
	public void searchFamilyUnspscandBrandFilterBean(
			PublicCatalogueBackingBean bb, CatalogueReqDTO reqDTO) {

		if (bb.getManagedBean().getFamilyList().size() > 1) {
			for (CmUnspsc commodity : bb.getManagedBean().getFamilyList()) {
				reqDTO.getSubStringFamilyCode().add(commodity.getUnspscCode());

			}

			if (isSubStringFamilyCodeNotNull(reqDTO)) {
				reqDTO.getSubStringFamilyCode();
			}
		} else {
			if (isBrandIdNotNull(bb)) {
				reqDTO.setBrandId(bb.getManagedBean().getBrandId());
			}
		}

		bb.getManagedBean().setSingleCommoditySelected(false);
		if (isUnspscObjectdNotNull(bb)) {
			reqDTO.setUnspscCode(bb.getManagedBean().getSelectedUnspscObj()
					.getUnspscCode());
			bb.getManagedBean().setSingleCommoditySelected(true);
		}
	}

	/**
	 * @param bb
	 * @param resDTO1
	 * @param itemNames
	 */
	public void getFamilyCodeList(PublicCatalogueBackingBean bb,
			CatalogueResDTO resDTO1, List<String> itemNames) {
		for (CmUnspsc cat : resDTO1.getCommodityList()) {

			itemNames.add(cat.getUnspscCode());

			bb.getManagedBean().getFamilyList().add(cat);
		}

		bb.getManagedBean().setCloneItemNames(itemNames);

		for (String code : itemNames) {

			if (code != null && code.trim().length() > 0) {
				bb.getManagedBean().getFamilyCodeList().add(code);
			}

		}

		if (bb.getManagedBean().getFamilyCodeList().size() == 1) {
			CmUnspsc commodity = bb.getManagedBean().getFamilyList().get(0);
			bb.getManagedBean().setCommodityCode(commodity.getUnspscCode());
			for (CmBrand brand : commodity.getCmBrand()) {

				bb.getManagedBean()
						.getBrandCriteria()
						.add(new SelectItem(brand.getBrandId(), brand
								.getBrandName()));
			}
		}

	}

	/**
	 * @param bb
	 * @param resDTO1
	 * @param itemNames
	 */
	public void getFamilyCodeFamilyList(PublicCatalogueBackingBean bb,
			CatalogueResDTO resDTO1, List<String> itemNames) {
		for (CmUnspsc cat : resDTO1.getCommodityList()) {

			itemNames.add(cat.getUnspscCode());
			bb.getManagedBean().getFamilyList().add(cat);
		}

		for (String code : itemNames) {

			if (code != null && code.trim().length() > 0) {
				bb.getManagedBean().getFamilyCodeList().add(code);
			}

		}

		if (bb.getManagedBean().getFamilyCodeList().size() == 1
				|| bb.getManagedBean().getFamilyList().size() == 1) {
			CmUnspsc commodity = bb.getManagedBean().getFamilyList().get(0);
			bb.getManagedBean().setCommodityCode(commodity.getUnspscCode());
			for (CmBrand brand : commodity.getCmBrand()) {

				bb.getManagedBean()
						.getBrandCriteria()
						.add(new SelectItem(brand.getBrandId(), brand
								.getBrandName()));
			}
		}

	}

	private boolean isItemKeywordNotNull(PublicCatalogueBackingBean bb) {
		return bb.getManagedBean().getItemName() != null
				&& bb.getManagedBean().getItemName().trim().length() > 0;
	}

	private boolean isUnspscNotNull(PublicCatalogueBackingBean bb) {
		return bb.getManagedBean().getUnspscTitle() != null
				&& bb.getManagedBean().getUnspscTitle().trim().length() > 0;
	}

	private boolean isPriceFromNotNull(PublicCatalogueBackingBean bb) {
		return bb.getManagedBean().getPriceForm() != null
				&& bb.getManagedBean().getPriceForm()
						.compareTo(new BigDecimal("0.00")) > 0;
	}

	private boolean isPriceToNotNull(PublicCatalogueBackingBean bb) {
		return bb.getManagedBean().getPriceTo() != null
				&& bb.getManagedBean().getPriceTo()
						.compareTo(new BigDecimal("0.00")) > 0;
	}

	private boolean isDeliveryFromNotNull(PublicCatalogueBackingBean bb) {
		return bb.getManagedBean().getDeliveryFrom() != null
				&& bb.getManagedBean().getDeliveryFrom() > 0;
	}

	private boolean isDeliveryToNotNull(PublicCatalogueBackingBean bb) {
		return bb.getManagedBean().getDeliveryTo() != null
				&& bb.getManagedBean().getDeliveryTo() > 0;
	}

	private boolean isSubStringFamilyCodeNotNull(CatalogueReqDTO reqDTO) {
		return reqDTO.getSubStringFamilyCode() != null
				&& !reqDTO.getSubStringFamilyCode().isEmpty();
	}

	private boolean isUnspscObjectdNotNull(PublicCatalogueBackingBean bb) {
		return bb.getManagedBean().getSelectedUnspscObj() != null
				&& bb.getManagedBean().getSelectedUnspscObj().getUnspscCode() != null
				&& bb.getManagedBean().getSelectedUnspscObj().getUnspscCode()
						.trim().length() > 0;
	}

	private boolean isBrandIdNotNull(PublicCatalogueBackingBean bb) {
		return bb.getManagedBean().getBrandId() != null
				&& !bb.getManagedBean().getBrandId().equals(new Long(0));
	}
	//#10747 and SonarLint fixing- End
}
