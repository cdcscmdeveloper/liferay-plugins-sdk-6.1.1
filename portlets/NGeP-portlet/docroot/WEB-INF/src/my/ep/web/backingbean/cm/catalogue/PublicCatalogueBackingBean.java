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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.constant.MessageConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.VCmPubSuppItem;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.PublicCatalogueManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;


/**
 * Class PublicCatalogueBackingBean.
 * @author hketan
 */
@ManagedBean(name = "pCatalogueBackingBean")
@RequestScoped
public class PublicCatalogueBackingBean extends
BaseBackingBean<PublicCatalogueManagedBean> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	public void searchCatalogue() {

		System.out.println("*************start***************");
		if(this.getManagedBean().getItemName() == null || this.getManagedBean().getItemName().isEmpty()){

			throw new EpException(MessageConst.ERROR_716008);
		}


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
		this.getManagedBean().setSelectedUnspscObj(null);
		this.getManagedBean().setCloneNameFlag(true);
		this.getManagedBean().reset();

		this.getManagedBean().getSearchDataModel().reloadData();
		System.out.println("*************end***************");
	}

	/**
	 * Clear serach.
	 */
	public void clearSerach() {
		this.getManagedBean().setItemName(null);
		this.getManagedBean().reset();
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

	
		this.getManagedBean().setFrontPage(false);
		this.getManagedBean().setCatalogueList(null);
		this.getManagedBean().setCloneNameFlag(false);
		this.getManagedBean().setBrandCriteria(null);
	

		this.getManagedBean().getSearchDataModel().reloadData();


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
		this.getManagedBean().setSelectedUnspscObj(null);
		this.getManagedBean().setCloneNameFlag(false);
		this.getManagedBean().setFamilyList(null);


		this.getManagedBean().setKeyWord(false);
		this.getManagedBean().setUnspscTitle(commodity.getUnspscTitle());

		if (commodity.getUnspscCode() != null
				&& commodity.getUnspscCode().trim().length() > 0) {

			this.getManagedBean().setSelectedUnspscObj(commodity);
		}


/*		ICodificationSO codificationSO = null;
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
		}*/

		this.getManagedBean().getSearchDataModel().reloadData();








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

		this.getManagedBean().setSelectedUnspscObj(null);	
		this.getManagedBean().setBrandId(null);
		this.getManagedBean().setPriceTo(null);
		this.getManagedBean().setPriceForm(null);
		
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
		
		this.getManagedBean().getSearchDataModel().reloadData();



	}

	/**
	 * Search catalogue by commodity code.
	 * 
	 * @param commodityCode
	 *           (CmUnspsc commodityCode(mandatory))
	 */
	public void searchCatalogueByCommodityCode(CmUnspsc commodityCode) {
		log.info("..............searchCatalogueByCommodityCode..................");

		this.getManagedBean().setFrontPage(false);
		this.getManagedBean().setCatalogueList(null);
		this.getManagedBean().setBrandId(null);
		this.getManagedBean().setPriceForm(null);
		this.getManagedBean().setPriceTo(null);
		this.getManagedBean().getBrandCriteria().clear();
		this.getManagedBean().setCloneNameFlag(false);
		this.getManagedBean().setFamilyList(null);
		this.getManagedBean().setCloneItemNames(null);
		this.getManagedBean().reset();

		log.info("getUnspscCode******" + commodityCode.getUnspscCode());

		this.getManagedBean().setKeyWord(false);
		this.getManagedBean().setUnspscTitle(commodityCode.getUnspscTitle());

		if (commodityCode.getUnspscCode() != null
				&& commodityCode.getUnspscCode().trim().length() > 0) {

			this.getManagedBean().setCommodityCode(commodityCode.getUnspscCode());
			this.getManagedBean().setSelectedUnspscObj(commodityCode);
		}
		
		
/*		ICodificationSO codificationSO = null;
		CodificationResDTO cdResDTO = new CodificationResDTO();
		try {
			codificationSO = (ICodificationSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICodificationSO.class);

			CodificationReqDTO cdReqDTO = new CodificationReqDTO();
			cdReqDTO.setUnspscId(commodityCode.getUnspscId());
			cdResDTO = codificationSO.retrieveBrandByCommodityId(cdReqDTO);
		} finally {
			codificationSO = null;
		}
		log.info("getCmBrand:" + cdResDTO.getBrandList().size());

		for (CmBrand brand : cdResDTO.getBrandList()) {
			log.info("getBrandName:" + brand.getBrandName());
			this.getManagedBean()
			.getBrandCriteria()
			.add(new SelectItem(brand.getBrandId(), brand
					.getBrandName()));
		}*/
		
		
		
		this.getManagedBean().getSearchDataModel().reloadData();


	}





}
