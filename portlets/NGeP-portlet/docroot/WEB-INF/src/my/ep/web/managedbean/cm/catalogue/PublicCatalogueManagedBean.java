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
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.VCmPubSuppItem;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "pCatalogueMB")
@SessionScoped
public class PublicCatalogueManagedBean extends BaseManagedBean {

	private String itemName;
	private List <CmUnspsc> familyList;
	private List <VCmPubSuppItem> catalogueList;
	private List <CmUnspsc> popularCommodityList;
	private boolean frontPage = true;
	private BigDecimal priceForm;
	private BigDecimal priceTo;
	private String familyCode;
	private String commodityCode;
	private List<String> familyCodeList;
	private List<SelectItem> brandCriteria;
	private Long brandId;
	private boolean keyWord = true;
	private String unspscTitle;
	
	private List<String> cloneItemNames = new ArrayList<String>();
	
	



	public String getItemName() {
		return itemName;
	}

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

	public List<VCmPubSuppItem> getCatalogueList() {
		if(this.catalogueList == null){
			this.catalogueList = new ArrayList<VCmPubSuppItem>();
		}
		return catalogueList;
	}

	public void setCatalogueList(List<VCmPubSuppItem> catalogueList) {
		this.catalogueList = catalogueList;
	}

	public boolean isFrontPage() {
		return frontPage;
	}


	public void setFrontPage(boolean frontPage) {
		this.frontPage = frontPage;
	}


	public BigDecimal getPriceForm() {
		return priceForm;
	}


	public void setPriceForm(BigDecimal priceForm) {
		this.priceForm = priceForm;
	}


	public BigDecimal getPriceTo() {
		return priceTo;
	}


	public void setPriceTo(BigDecimal priceTo) {
		this.priceTo = priceTo;
	}


	public String getFamilyCode() {
		return familyCode;
	}


	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}


	public List<String> getFamilyCodeList() {
		if(familyCodeList==null){
			familyCodeList = new ArrayList<String>();
		}
		return familyCodeList;
	}


	public void setFamilyCodeList(List<String> familyCodeList) {
		this.familyCodeList = familyCodeList;
	}


	public List<CmUnspsc> getPopularCommodityList() {
		if(popularCommodityList==null)
			popularCommodityList = new ArrayList<CmUnspsc>();
		return popularCommodityList;
	}


	public void setPopularCommodityList(List<CmUnspsc> popularCommodityList) {
		this.popularCommodityList = popularCommodityList;
	}


	public List<SelectItem> getBrandCriteria() {
		if (brandCriteria == null)
			brandCriteria = new ArrayList<SelectItem>();
		return brandCriteria;
	}


	public void setBrandCriteria(List<SelectItem> brandCriteria) {
		this.brandCriteria = brandCriteria;
	}


	public Long getBrandId() {
		return brandId;
	}


	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}


	public boolean isKeyWord() {
		return keyWord;
	}


	public void setKeyWord(boolean keyWord) {
		this.keyWord = keyWord;
	}


	public String getUnspscTitle() {
		return unspscTitle;
	}


	public void setUnspscTitle(String unspscTitle) {
		this.unspscTitle = unspscTitle;
	}


	public List<String> getCloneItemNames() {
		
		if(cloneItemNames==null)
			cloneItemNames = new ArrayList<String>();
		return cloneItemNames;
	}


	public void setCloneItemNames(List<String> cloneItemNames) {
		this.cloneItemNames = cloneItemNames;
	}

	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	
	
	
	
	
	







}
