/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.backingbean.cm.catalogue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.VCmPubSuppItem;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.PublicCatalogueManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;


/**
 * Class PublicCatalogueBackingBean.
 * @author hketan
 */
@ManagedBean(name = "pCatalogueBackingBean")
@RequestScoped
public class PublicCatalogueBackingBean extends
		BaseBackingBean<PublicCatalogueManagedBean> {

	/** The log. */
	private static Logger log = Logger
			.getLogger(PublicCatalogueBackingBean.class);

	/**
	 * Auto complete item name.
	 * 
	 * @param query
	 *           (String query(mandatory))
	 * @return List<String>
	 */
	public List<String> autoCompleteItemName(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setItemName(query);

		CodificationResDTO resDTO = codificationSO.searchItemNameByAuto(reqDTO);

		List<String> itemNames = new ArrayList<String>();
		if (resDTO.getItemList() != null && resDTO.getItemList().size() > 0) {
			for (CmItem item : resDTO.getItemList()) {
				itemNames.add(item.getItemName());
			}
		}

		return itemNames;
	}

	/**
	 * Find popular commodity.
	 */
	public void findPopularCommodity() {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ICodificationSO codeSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CodificationReqDTO codeReqDTO = new CodificationReqDTO();

		this.getManagedBean().setPopularCommodityList(null);
		CatalogueResDTO resDTO = catalogueSO.findPopularCommodity(reqDTO);

		if (resDTO.getList() != null && resDTO.getList().size() > 0) {
			for (int i = 0; i < resDTO.getList().size(); i++) {

				Object[] obj = (Object[]) resDTO.getList().get(i);

				String commodityCode = (String) obj[0];

				log.info("*********commodityCode**********"	+ commodityCode);
				codeReqDTO.setUnspscCode(commodityCode);

				CodificationResDTO codeResDTO = codeSO
						.searchUnspscObj(codeReqDTO);

				this.getManagedBean().getPopularCommodityList()
						.add(codeResDTO.getUnspscObj());// .set Popular
														// commodityCode
			}
		}

	}

	/**
	 * Search catalogue.
	 * 
	 * @return string
	 */
	public String searchCatalogue() {
	
		FacesContext facesContext = FacesContext.getCurrentInstance();
      if(this.getManagedBean().getItemName() == null || this.getManagedBean().getItemName().isEmpty()){
    	  facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
						MessageUtils.getMessage(MessageConst.ERROR_716008,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
    	  return null;
      	}
      
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		String classCode = "";
		String familyCode = "";
		this.getManagedBean().setCatalogueList(null);
		this.getManagedBean().setFamilyList(null);
		this.getManagedBean().setFrontPage(false);
		this.getManagedBean().setFamilyCodeList(null);
		this.getManagedBean().setKeyWord(true);
		this.getManagedBean().setCloneItemNames(null);
		this.getManagedBean().setBrandId(null);
		this.getManagedBean().setPriceForm(null);
		this.getManagedBean().setPriceTo(null);
		this.getManagedBean().getBrandCriteria().clear();
		this.getManagedBean().setCommodityCode(null);
		this.getManagedBean().setFamilyCode(null);
		
		try {
			reqDTO.setItemName(this.getManagedBean().getItemName());
			CatalogueResDTO resDTO = catalogueSO
					.searchPublicCatalogueBy(reqDTO);

			List<String> itemNames = new ArrayList<String>();
			for (VCmPubSuppItem cat : resDTO.getViewPublicSuppItemResultBean()
					.getResultList()) {

				if (itemNames != null && itemNames.size() > 0) {
					if (!itemNames.contains(cat.getCommodityCode())) {
						itemNames.add(cat.getCommodityCode());
					}
				} else {
					itemNames
							.add(cat.getCommodityCode());
				}
			}

			this.getManagedBean().setCloneItemNames(itemNames);
			log.info("itemNames.size(): " + itemNames.size());

			this.getManagedBean().setFamilyList(new ArrayList<CmUnspsc>());
			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
			for (String code : itemNames) {
				log.debug("*******code***********" + code);
//				if(this.getManagedBean().getFamilyList().size() < 10){
				codeReqDTO.setUnspscCode(code);
				CodificationResDTO codeResDTO = codificationSO
						.searchUnspscObj(codeReqDTO);
				this.getManagedBean().getFamilyList()
						.add(codeResDTO.getUnspscObj());

				if (code != null && code.trim().length() > 0) {
					this.getManagedBean().getFamilyCodeList().add(code);
				}
//				}
			}

			if(this.getManagedBean().getFamilyCodeList().size() == 1){
				CmUnspsc commodity = this.getManagedBean().getFamilyList().get(0);
				this.getManagedBean().setCommodityCode(commodity.getUnspscCode());
				log.debug("getCmBrand:" + commodity.getCmBrand().size());

				for (CmBrand brand : commodity.getCmBrand()) {
					log.debug("getBrandName:" + brand.getBrandName());
					this.getManagedBean()
							.getBrandCriteria()
							.add(new SelectItem(brand.getBrandId(), brand
									.getBrandName()));
				}
			}
			for (VCmPubSuppItem view : resDTO.getViewPublicSuppItemResultBean()
					.getResultList()) {
				this.getManagedBean().getCatalogueList()
						.add(view);
			}

		} finally {
			catalogueSO = null;
			codificationSO = null;
		}
		return "publicCatalogueView?faces-redirect=true";
	}

	/**
	 * Clear serach.
	 */
	public void clearSerach() {
		this.getManagedBean().setItemName(null);
	}

	/**
	 * Pre render public cat view.
	 */
	public void preRenderPublicCatView() {

		this.getManagedBean().setFrontPage(true);
		this.getManagedBean().setItemName(null);
		findPopularCommodity();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "publicCatalogueView");
	}

	/**
	 * Search catalogue by price.
	 */
	public void searchCatalogueByPrice() {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = new CatalogueResDTO();
		this.getManagedBean().setFrontPage(false);
		this.getManagedBean().setCatalogueList(null);

		String subString = "";

		log.info("getCommodityCode:" + this.getManagedBean().getCommodityCode());
		log.info("this.getManagedBean().getBrandId()=" + this.getManagedBean().getBrandId());
		log.info("this.getManagedBean().getPriceForm()=" + this.getManagedBean().getPriceForm());
		log.info("this.getManagedBean().getPriceTo()="	+ this.getManagedBean().getPriceTo());
		reqDTO.setCommodityCode(this.getManagedBean().getCommodityCode());

		if(this.getManagedBean().getItemName() != null && !this.getManagedBean().getItemName().trim().isEmpty()){
			reqDTO.setItemName(this.getManagedBean().getItemName());
		}
		if (this.getManagedBean().getPriceForm() != null
				&& this.getManagedBean().getPriceForm()
						.compareTo(new BigDecimal("0.00")) > 0) {
			reqDTO.setPriceFrom(this.getManagedBean().getPriceForm());
		}

		if (this.getManagedBean().getPriceTo() != null
				&& this.getManagedBean().getPriceTo()
						.compareTo(new BigDecimal("0.00")) > 0) {
			reqDTO.setPriceTo(this.getManagedBean().getPriceTo());
		}

		if (this.getManagedBean().getFamilyList().size() > 1) {
			for (CmUnspsc commodity : this.getManagedBean().getFamilyList()) {
				reqDTO.getSubStringFamilyCode().add(commodity.getUnspscCode());
			}

			resDTO = catalogueSO.findPubCatalogueByFamilyLevel(reqDTO);

		} else {
			if (this.getManagedBean().getBrandId() != null) {
				if (!this.getManagedBean().getBrandId().equals(new Long(0))) {
					log.info("brand selected: " + this.getManagedBean().getBrandId());

					reqDTO.setBrandId(this.getManagedBean().getBrandId());
				}
			}

			resDTO = catalogueSO.findPubCatalogueByCommodityCode(reqDTO);

		}

		log.debug("ViewPublicSuppItemResultBean().getResultList(): " + resDTO.getViewPublicSuppItemResultBean().getResultList());

		for (VCmPubSuppItem view : resDTO.getViewPublicSuppItemResultBean().getResultList()) {
			this.getManagedBean().getCatalogueList().add(view);
		}

	}

	/**
	 * Search catalogue by family level.
	 * 
	 * @param commodity
	 *            (CmUnspsc commodity(mandatory))
	 */
	public void searchCatalogueByFamilyLevel(CmUnspsc commodity) {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		this.getManagedBean().setFrontPage(false);
		this.getManagedBean().setCatalogueList(null);
		this.getManagedBean().getBrandCriteria().clear();
		this.getManagedBean().setBrandId(null);
		this.getManagedBean().setPriceTo(null);
		this.getManagedBean().setPriceForm(null);
		
		log.info("getUnspscCode******" + commodity.getUnspscCode());
		String subString = "";

		this.getManagedBean().setKeyWord(false);
		this.getManagedBean().setUnspscTitle(commodity.getUnspscTitle());

		if (commodity.getUnspscCode() != null
				&& commodity.getUnspscCode().trim().length() > 0) {

			this.getManagedBean().setFamilyCode(commodity.getUnspscCode());
			this.getManagedBean().setCommodityCode(commodity.getUnspscCode());
		}
		log.info("getFamilyCode:" + this.getManagedBean().getFamilyCode());

		reqDTO.getSubStringFamilyCode().add(
				this.getManagedBean().getFamilyCode());
		
		if(this.getManagedBean().getItemName() != null && !this.getManagedBean().getItemName().trim().isEmpty()){
			reqDTO.setItemName(this.getManagedBean().getItemName());
		}

		 if (this.getManagedBean().getPriceForm() != null
		 && this.getManagedBean().getPriceForm()
		 .compareTo(new BigDecimal("0.00")) > 0) {
		 reqDTO.setPriceFrom(this.getManagedBean().getPriceForm());
		 }
		
		 if (this.getManagedBean().getPriceTo() != null
		 && this.getManagedBean().getPriceTo()
		 .compareTo(new BigDecimal("0.00")) > 0) {
		 reqDTO.setPriceTo(this.getManagedBean().getPriceTo());
		 }

		 ICodificationSO codificationSO = null;
		 CodificationResDTO cdResDTO = new CodificationResDTO();
		 try {
			 codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		
			CodificationReqDTO cdReqDTO = new CodificationReqDTO();
			cdReqDTO.setUnspscId(commodity.getUnspscId());
			cdResDTO = codificationSO.retrieveBrandByCommodityId(cdReqDTO);
		 } finally {
			 codificationSO = null;
		 }
		log.debug("getCmBrand:" + cdResDTO.getBrandList().size());

		for (CmBrand brand : cdResDTO.getBrandList()) {
			log.debug("getBrandName:" + brand.getBrandName());
			this.getManagedBean()
					.getBrandCriteria()
					.add(new SelectItem(brand.getBrandId(), brand
							.getBrandName()));
		}

		CatalogueResDTO resDTO = catalogueSO
				.findPubCatalogueByFamilyLevel(reqDTO);

		log.debug("***********************"
				+ resDTO.getViewPublicSuppItemResultBean().getResultList());

		for (VCmPubSuppItem view : resDTO.getViewPublicSuppItemResultBean()
				.getResultList()) {

			this.getManagedBean().getCatalogueList()
					.add(view);
		}

		this.getManagedBean().setFamilyList(null);
		this.getManagedBean().getFamilyList().add(commodity);

	}

	/**
	 * Navigate to supplier item det.
	 * 
	 * @param vpsi
	 *            (VCmPubSuppItem vpsi(mandatory))
	 * @return String(publicCatalogueDetail.xhtml)
	 */
	public String navigateToSupplierItemDet(VCmPubSuppItem vpsi) {
		log.info("...............navigateToSupplierItemDet................");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueResDTO resDTO = null;
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		reqDTO.setSupplierItemId(vpsi.getSupplierItemId());
		resDTO = catalogueSO.getSupplierItem(reqDTO);
		


		CatalogueBackingBean bb = new CatalogueBackingBean();

		try {
			bb.getManagedBean().setSelectedMaintainItem(resDTO.getSupplierItem());

		} finally {
			catalogueSO = null;
		}

		return "publicCatalogueDetail.xhtml?faces-redirect=true";
	}

	/**
	 * Cancel to public search.
	 * 
	 * @return String(publicCatalogueView)
	 */
	public String cancelToPublicSearch() {
		log.debug("..................cancelToPublicSearch..............");
		return "publicCatalogueView?faces-redirect=true";
	}

	/**
	 * Reset catalogue.
	 */
	public void resetCatalogue() {

		this.getManagedBean().setPriceForm(null);
		this.getManagedBean().setPriceTo(null);
		this.getManagedBean().setBrandId(0L);
	}

	/**
	 * Back to list.
	 */
	public void backToList() {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
	
		if (this.getManagedBean().getCloneItemNames().size() > 0) {
			this.getManagedBean().setFamilyList(null);
			this.getManagedBean().setBrandId(null);
			this.getManagedBean().getBrandCriteria().clear();
		}
		for (String code : this.getManagedBean().getCloneItemNames()) {

			log.info("*******code***********" + code);

			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
			codeReqDTO.setUnspscCode(code);
			CodificationResDTO codeResDTO = codificationSO
					.searchUnspscObj(codeReqDTO);

			this.getManagedBean().getFamilyList()
					.add(codeResDTO.getUnspscObj());

		}
		if(this.getManagedBean().getCloneItemNames().size()>0){//refresh search for category list
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			CatalogueResDTO resDTO = new CatalogueResDTO();
			if(this.getManagedBean().getItemName() != null && !this.getManagedBean().getItemName().trim().isEmpty()){
				reqDTO.setItemName(this.getManagedBean().getItemName());
			}
			if (this.getManagedBean().getPriceForm() != null
					&& this.getManagedBean().getPriceForm()
							.compareTo(new BigDecimal("0.00")) > 0) {
				reqDTO.setPriceFrom(this.getManagedBean().getPriceForm());
			}

			if (this.getManagedBean().getPriceTo() != null
					&& this.getManagedBean().getPriceTo()
							.compareTo(new BigDecimal("0.00")) > 0) {
				reqDTO.setPriceTo(this.getManagedBean().getPriceTo());
			}

			if (this.getManagedBean().getFamilyList().size() > 1) {
				this.getManagedBean().setCatalogueList(null);
//				for (CmUnspsc commodity : this.getManagedBean().getFamilyList()) {//ByPass this condition clause
//					reqDTO.getSubStringFamilyCode().add(commodity.getUnspscCode());
//				}

				resDTO = catalogueSO.findPubCatalogueByFamilyLevel(reqDTO);

			} 

			log.debug("ViewPublicSuppItemResultBean().getResultList(): "	+ resDTO.getViewPublicSuppItemResultBean().getResultList());

			for (VCmPubSuppItem view : resDTO.getViewPublicSuppItemResultBean()
					.getResultList()) {
				
				this.getManagedBean().getCatalogueList()
						.add(view);
			}

		}


	}

	/**
	 * Search catalogue by commodity code.
	 * 
	 * @param commodityCode
	 *           (CmUnspsc commodityCode(mandatory))
	 */
	public void searchCatalogueByCommodityCode(CmUnspsc commodityCode) {
		log.debug("..............searchCatalogueByCommodityCode..................");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		this.getManagedBean().setFrontPage(false);
		this.getManagedBean().setCatalogueList(null);
		this.getManagedBean().setBrandId(null);
		this.getManagedBean().setPriceForm(null);
		this.getManagedBean().setPriceTo(null);
		this.getManagedBean().getBrandCriteria().clear();

		log.info("getUnspscCode******" + commodityCode.getUnspscCode());
		String subString = "";

		this.getManagedBean().setKeyWord(false);
		this.getManagedBean().setUnspscTitle(commodityCode.getUnspscTitle());

		if (commodityCode.getUnspscCode() != null
				&& commodityCode.getUnspscCode().trim().length() > 0) {

			this.getManagedBean().setCommodityCode(commodityCode.getUnspscCode());
		}
		log.info("CommodityCode:" + this.getManagedBean().getCommodityCode());

		reqDTO.setCommodityCode(this.getManagedBean().getCommodityCode());

		CatalogueResDTO resDTO = catalogueSO.findPubCatalogueByCommodityCode(reqDTO);

		log.debug("resDTO.getViewPublicSuppItemResultBean().getResultList(): "	+ resDTO.getViewPublicSuppItemResultBean().getResultList());

		for (VCmPubSuppItem view : resDTO.getViewPublicSuppItemResultBean().getResultList()) {
			this.getManagedBean().getCatalogueList().add(view);
		}

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		
		CodificationReqDTO cdReqDTO = new CodificationReqDTO();
		cdReqDTO.setUnspscId(commodityCode.getUnspscId());
		CodificationResDTO cdResDTO = codificationSO.retrieveBrandByCommodityId(cdReqDTO);
		
		log.info("getCmBrand:" + cdResDTO.getBrandList().size());

		for (CmBrand brand : cdResDTO.getBrandList()) {
			log.debug("getBrandName:" + brand.getBrandName());
			this.getManagedBean()
					.getBrandCriteria()
					.add(new SelectItem(brand.getBrandId(), brand
							.getBrandName()));
		}

		this.getManagedBean().setFamilyList(null);
		this.getManagedBean().getFamilyList().add(commodityCode);
	}

}
