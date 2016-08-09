package my.ep.web.managedbean.cm.catalogue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.VCmPubSuppItem;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.cm.catalogue.PublicCatalogueBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;


/**
 * The Class PublicCatalogueManagedBean.
 * @author hketan
 */
@ManagedBean(name = "pCatalogueMB")
@SessionScoped
public class PublicCatalogueManagedBean extends BaseManagedBean {

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
	private List<String> cloneItemNames = new ArrayList<String>();

	private int totalRecord = 0;

	private CmUnspsc selectedUnspscObj;

	private boolean cloneNameFlag = false;



	private IPagingDataModel<VCmPubSuppItem> searchDataModel = new SearchPagingDataModel();

	private String itemTypeParameter;
	
	private SearchFilterBean keyWordFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());


	private class SearchPagingDataModel extends PagingDataModel<VCmPubSuppItem> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("catalogueGridViewID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<VCmPubSuppItem> pageChangeListener(	ISearchBean<VCmPubSuppItem> searchBean) {




			PublicCatalogueBackingBean bb = new PublicCatalogueBackingBean();


			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);
			ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICodificationSO.class);
			
			try {


				if (bb.getManagedBean().getItemName() != null && bb.getManagedBean().getItemName().trim().length()>0) {
					System.out.println("bb.getManagedBean().getItemName()"+bb.getManagedBean().getItemName());
					SearchFilterBean itemNameFilterBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());

					itemNameFilterBean.setSearchValue(bb.getManagedBean().getItemName());
					itemNameFilterBean.setSearchField("itemName");
					itemNameFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
					itemNameFilterBean.setCaseInsensitive(true);
					//searchBean.getDataFilterBeans().add(itemNameFilterBean);
					
					
					keyWordFilterBean.setSearchField("keyword");
					keyWordFilterBean.setSearchValue(bb.getManagedBean().getItemName());
					keyWordFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
					keyWordFilterBean.setCaseInsensitive(true);
					
					SearchMultiFilterBean multi = new SearchMultiFilterBean(itemNameFilterBean
							, keyWordFilterBean);
					searchBean.getDataFilterBeans().add(multi);

				}

				
				if (bb.getManagedBean().getPriceForm() != null
						&& bb.getManagedBean().getPriceForm()
								.compareTo(new BigDecimal("0.00")) > 0) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("price",
									SearchOperator.OPERATOR_GREATER_EQUAL.value(),
									bb.getManagedBean().getPriceForm()));
				}

				if (bb.getManagedBean().getPriceTo() != null
						&& bb.getManagedBean().getPriceTo()
								.compareTo(new BigDecimal("0.00")) > 0) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("price",
									SearchOperator.OPERATOR_LESS_EQUAL.value(), bb.getManagedBean().getPriceTo()));
				}
				
				
				if (bb.getManagedBean().getFamilyList().size() > 1) {
					for (CmUnspsc commodity : bb.getManagedBean().getFamilyList()) {
						reqDTO.getSubStringFamilyCode().add(commodity.getUnspscCode());
	
					}
					
					if (reqDTO.getSubStringFamilyCode() != null && reqDTO.getSubStringFamilyCode().size() > 0) {

						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("commodityCode",
										SearchOperator.OPERATOR_IN.value(), reqDTO
										.getSubStringFamilyCode()));
					}

				

				} else {
					if (bb.getManagedBean().getBrandId() != null) {
						if (!bb.getManagedBean().getBrandId().equals(new Long(0))) {
							
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean("brandId",
											SearchOperator.OPERATOR_EQUALS.value(), bb.getManagedBean().getBrandId()));
						}
					}

					

				}
				
				

				boolean singleCommoditySelected = false;
				if(bb.getManagedBean().getSelectedUnspscObj()!=null && bb.getManagedBean().getSelectedUnspscObj().getUnspscCode() !=null && bb.getManagedBean().getSelectedUnspscObj().getUnspscCode().trim().length()>0){
					System.out.println("********commodityCode*******"+bb.getManagedBean().getSelectedUnspscObj().getUnspscCode());
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("commodityCode",
									SearchOperator.OPERATOR_EQUALS.value(), bb.getManagedBean().getSelectedUnspscObj().getUnspscCode()));

					singleCommoditySelected = true;
				}


				reqDTO.setPublicCatalogSearchBean(searchBean);

				CatalogueResDTO resDTO = catalogueSO
						.searchCatalogueBy(reqDTO);

				int totalRecord = resDTO.getViewPublicSuppItemResultBean().getTotalRecord();
				System.out.println("*******size*******"+totalRecord);
				System.out.println("isCloneNameFlag:"+bb.getManagedBean().isCloneNameFlag());
				CatalogueReqDTO reqDTO1 = new CatalogueReqDTO();
				if (bb.getManagedBean().getItemName() != null && bb.getManagedBean().getItemName().trim().length()>0) {
					reqDTO1.setItemName(bb.getManagedBean().getItemName());
				}
				
				if (bb.getManagedBean().getPriceForm() != null
						&& bb.getManagedBean().getPriceForm()
								.compareTo(new BigDecimal("0.00")) > 0) {
					reqDTO1.setPriceFrom(bb.getManagedBean().getPriceForm());
				}
				if (bb.getManagedBean().getPriceTo() != null
						&& bb.getManagedBean().getPriceTo()
								.compareTo(new BigDecimal("0.00")) > 0) {
					reqDTO1.setPriceTo(bb.getManagedBean().getPriceTo());
				}
				CodificationReqDTO codeReqDTO = new CodificationReqDTO();
				if(singleCommoditySelected){
					bb.getManagedBean().setFamilyList(null);
					codeReqDTO.setUnspscCode(bb.getManagedBean().getSelectedUnspscObj().getUnspscCode());
					CodificationResDTO codeResDTO = codificationSO
							.searchUnspscObj(codeReqDTO);
					bb.getManagedBean().getFamilyList().add(codeResDTO.getUnspscObj());
				
						CmUnspsc commodity = codeResDTO.getUnspscObj();
						bb.getManagedBean().setCommodityCode(commodity.getUnspscCode());
						System.out.println("commodity.getCmBrand():"+commodity.getCmBrand().size());
						for (CmBrand brand : commodity.getCmBrand()) {

							bb.getManagedBean()
							.getBrandCriteria()
							.add(new SelectItem(brand.getBrandId(), brand
									.getBrandName()));
						}
						
					
				}else{
				
					CatalogueResDTO resDTO1 = catalogueSO.searchPublicCatalogueByNamePrice(reqDTO1);
					if(bb.getManagedBean().isCloneNameFlag()){
						List<String> itemNames = new ArrayList<String>();
	
						bb.getManagedBean().setFamilyList(new ArrayList<CmUnspsc>());
						for (CmUnspsc cat : resDTO1.getCommodityList()) {
							System.out.println("cat.getCommodityCode():"+cat.getUnspscCode());
						
							itemNames.add(cat.getUnspscCode());
							
							
							bb.getManagedBean().getFamilyList().add(cat);
						}
	
	
						bb.getManagedBean().setCloneItemNames(itemNames);
	
						
						
						for (String code : itemNames) {
	
							if (code != null && code.trim().length() > 0) {
								bb.getManagedBean().getFamilyCodeList().add(code);
							}
	
						}
	
						if(bb.getManagedBean().getFamilyCodeList().size() == 1){
							CmUnspsc commodity = bb.getManagedBean().getFamilyList().get(0);
							bb.getManagedBean().setCommodityCode(commodity.getUnspscCode());
							System.out.println("commodity.getCmBrand():"+commodity.getCmBrand().size());
							for (CmBrand brand : commodity.getCmBrand()) {
	
								bb.getManagedBean()
								.getBrandCriteria()
								.add(new SelectItem(brand.getBrandId(), brand
										.getBrandName()));
							}
						}
					}else{
						bb.getManagedBean().setFamilyList(new ArrayList<CmUnspsc>());
						List<String> itemNames = new ArrayList<String>();
							
						for (CmUnspsc cat : resDTO1.getCommodityList()) {
							
							itemNames.add(cat.getUnspscCode());
							bb.getManagedBean().getFamilyList().add(cat);
						}
						
	
						for (String code : itemNames) {
	
							if (code != null && code.trim().length() > 0) {
								bb.getManagedBean().getFamilyCodeList().add(code);
							}
	
						}
	
						if(bb.getManagedBean().getFamilyCodeList().size() == 1|| bb.getManagedBean().getFamilyList().size()==1){
							CmUnspsc commodity = bb.getManagedBean().getFamilyList().get(0);
							bb.getManagedBean().setCommodityCode(commodity.getUnspscCode());
							System.out.println("commodity.getCmBrand():"+commodity.getCmBrand().size());
							for (CmBrand brand : commodity.getCmBrand()) {
	
								bb.getManagedBean()
								.getBrandCriteria()
								.add(new SelectItem(brand.getBrandId(), brand
										.getBrandName()));
							}
						}		
						
					}
				}


				System.out.println("***************getFamilyList().size()*************"+bb.getManagedBean().getFamilyList().size());
				System.out.println("getCloneItemNames:"+bb.getManagedBean().getCloneItemNames().size());
				bb.getManagedBean().setTotalRecord(totalRecord);

				RequestContext.getCurrentInstance().execute("refreshCatalogue();");

				return resDTO.getViewPublicSuppItemResultBean();


			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				catalogueSO = null;
				
			}
			return new SearchResultBean();
		}

	}






	public IPagingDataModel<VCmPubSuppItem> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<VCmPubSuppItem> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

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


	

	public List<CmUnspsc> getFamilyList() {
		if(familyList==null)
			familyList = new ArrayList<CmUnspsc>();
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
			this.catalogueList = new ArrayList<VCmPubSuppItem>();
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
			familyCodeList = new ArrayList<String>();
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
			popularCommodityList = new ArrayList<CmUnspsc>();
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
			brandCriteria = new ArrayList<SelectItem>();
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
			cloneItemNames = new ArrayList<String>();
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


	public void reset(){

		this.setSearchDataModel(new SearchPagingDataModel());

	}

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
	
	






}
