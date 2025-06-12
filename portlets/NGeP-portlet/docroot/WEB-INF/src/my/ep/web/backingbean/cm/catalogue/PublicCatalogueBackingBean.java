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
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.PublicCatalogueManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.component.datagrid.DataGrid;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

/**
 * Class PublicCatalogueBackingBean.
 * 
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
	 *            (String query(mandatory))
	 * @return List<String>
	 */
	// SonarLint Issues Fixing..
	public List<String> autoCompleteItemName(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);

		ICodificationSO codificationSO = BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setItemName(query);

		CodificationResDTO resDTO = codificationSO.searchItemNameByAuto(reqDTO);

		List<String> itemNames = new ArrayList<>();
		if (resDTO.getItemList() != null && !resDTO.getItemList().isEmpty()) {
			for (CmItem item : resDTO.getItemList()) {
				itemNames.add(item.getItemName());
			}
		}

		return itemNames;
	}

	/**
	 * Find popular commodity.
	 */
	// SonarLint Issues Fixing
	public void findPopularCommodity() {

		ICatalogueSO catalogueSO = BusinessDelegate.getInstance().getServiceObject("cm",
				ICatalogueSO.class);
		ICodificationSO codeSO = BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CodificationReqDTO codeReqDTO = new CodificationReqDTO();

		this.getManagedBean().setPopularCommodityList(null);
		CatalogueResDTO resDTO = catalogueSO.findPopularCommodity(reqDTO);

		if (resDTO.getList() != null && !resDTO.getList().isEmpty()) {
			for (int i = 0; i < resDTO.getList().size(); i++) {

				Object[] obj = (Object[]) resDTO.getList().get(i);

				String commodityCode = (String) obj[0];

				log.info("*********commodityCode**********" + commodityCode);
				codeReqDTO.setUnspscCode(commodityCode);

				CodificationResDTO codeResDTO = codeSO.searchUnspscObj(codeReqDTO);

				this.getManagedBean().getPopularCommodityList().add(codeResDTO.getUnspscObj());//set Popular commodityCode
			}
		}

	}

	/**
	 * Search catalogue.
	 * 
	 * @return string
	 */
	public void searchCatalogue() {
		// #10747 and sonarLint fixing issues- Start
		log.info("*************start***************");
		if (this.getManagedBean().getItemName() == null
				|| this.getManagedBean().getItemName().isEmpty()) {

			throw new EpException(MessageConst.ERROR_716008);
		}

		if (this.getManagedBean().getItemName() != null
				&& this.getManagedBean().getItemName().trim().length() > 0
				&& this.getManagedBean().getItemName().trim().length() < 3) {
			throw new EpException(MessageConst.ERROR_716019);
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
		// #10747- Start
		this.getManagedBean().setDeliveryFrom(null);
		this.getManagedBean().setDeliveryTo(null);
		// #10747- End
		this.getManagedBean().getBrandCriteria().clear();
		this.getManagedBean().setCommodityCode(null);
		this.getManagedBean().setFamilyCode(null);
		this.getManagedBean().setSelectedUnspscObj(null);
		this.getManagedBean().setCloneNameFlag(true);
		this.getManagedBean().reset();

		this.getManagedBean().getSearchDataModel().reloadData();
		log.info("*************end***************");
		// #10747 and sonarLint fixing issues- End
	}

	/**
	 * Clear serach.
	 */
	public void clearSerach() {
		this.getManagedBean().setItemName(null);
		this.getManagedBean().setFrontPage(true);
		this.getManagedBean().reset();
	}

	/**
	 * Pre render public cat view.
	 */
	public void preRenderPublicCatView() {
		// #10747 - Soo - Start
		log.info("prerender here");

		this.getManagedBean().setFrontPage(true);
		this.getManagedBean().setItemName(null);
		this.getManagedBean().setItemPerPage(15);
		this.getManagedBean().setSortBy(0);
		this.getManagedBean().setVisible(true);
		findPopularCommodity();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "publicCatalogueView");

		this.getManagedBean().getSearchDataModel().reloadData();
		// #10747 - Soo - End
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
	// remove unused Code - SonarLint Issues 
	public void searchCatalogueByFamilyLevel(CmUnspsc commodity) {

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

		if (commodity.getUnspscCode() != null && commodity.getUnspscCode().trim().length() > 0) {

			this.getManagedBean().setSelectedUnspscObj(commodity);
		}

		this.getManagedBean().getSearchDataModel().reloadData();
	}

	// #10747,#35739- Start
	/**
	 * Navigate to supplier item det.
	 * 
	 * @param supplierItemId
	 *            (VCmPubSuppItem vpsi(mandatory))
	 * @return String(publicCatalogueDetail.xhtml)
	 */
	public String navigateToSupplierItemDet(long supplierItemId) {
		log.info("...............navigateToSupplierItemDet................");
		ICatalogueSO catalogueSO = BusinessDelegate.getInstance()
				.getServiceObject("cm", ICatalogueSO.class);
		CatalogueResDTO resDTO;
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		reqDTO.setSupplierItemId(supplierItemId);
		resDTO = catalogueSO.getSupplierItem(reqDTO);

		CatalogueBackingBean bb = new CatalogueBackingBean();

		try {
			bb.getManagedBean().setSelectedMaintainItem(
					resDTO.getSupplierItem());

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.CATALOGUE_MANAGEMENT, null, null,
					"PublicCatalogueBackingBean.navigateToSupplierItemDet",
					null, e);
		}
		return "publicCatalogueDetail.xhtml?faces-redirect=true";
	}

	// #10747,#35739- End

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
		// #10747 - Soo - Start
		this.getManagedBean().setDeliveryFrom((Integer) null);
		this.getManagedBean().setDeliveryTo((Integer) null);
		// #10747 - Soo - End
		this.getManagedBean().setBrandId(0L);
		// #10747- Start
        this.getManagedBean().setConverterVisible(true);
        this.getManagedBean().setBrandCriteria(null);
		RequestContext.getCurrentInstance().reset("form:catalogueTabId:resultpl");
		
		this.getManagedBean().getSearchDataModel().reloadData();
		// #10747- End
	}

	// #10747 - Soo - Start
	// #10747 and SonarLint fixing- Start
	/**
	 * @param itemPerPage
	 */
	public void itemPerPage(int itemPerPage) {
		log.info("itemPerPage" + itemPerPage);

		if (this.getManagedBean().isVisible()) {

			DataGrid datagrid = (DataGrid) FacesContext.getCurrentInstance()
					.getViewRoot()
					.findComponent("form:catalogueTabId:catalogueGridViewID");

			datagrid.setRows(itemPerPage);
			datagrid.setFirst(datagrid.getPage() * itemPerPage);

		} else {

			DataTable datatable = (DataTable) FacesContext.getCurrentInstance()
					.getViewRoot()
					.findComponent("form:catalogueTabId:catalogueListingTable");

			datatable.setRows(itemPerPage);
			datatable.setFirst(datatable.getPage() * itemPerPage);
		}

		this.getManagedBean().getSearchDataModel().reloadData();
	}

	/**
	 * @param sortBy
	 */
	public void sortOrderBy(int sortBy) {
		log.info("sortBy" + sortBy);
		this.getManagedBean().getSearchDataModel().reloadData();
	}

	/**
	 * @param thumbnailView
	 */
	public void thumbnailView(boolean thumbnailView) {
		log.info("thumbnailView" + thumbnailView);
		this.getManagedBean().setVisible(thumbnailView);
		itemPerPage(this.getManagedBean().getItemPerPage());
	}
	// #10747 - Soo - End
	
	/**
	 * Back to list.
	 */
	public void backToList() {

		ICodificationSO codificationSO = BusinessDelegate.getInstance()
				.getServiceObject("cm", ICodificationSO.class);

		this.getManagedBean().setSelectedUnspscObj(null);
		this.getManagedBean().setBrandId(null);
		this.getManagedBean().setPriceTo(null);
		this.getManagedBean().setPriceForm(null);
		// #10747- Start
		this.getManagedBean().setKeyWord(true);
		// #10747- End

		if (!this.getManagedBean().getCloneItemNames().isEmpty()) {
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
	 *            (CmUnspsc commodityCode(mandatory))
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

			this.getManagedBean().setCommodityCode(
					commodityCode.getUnspscCode());
			this.getManagedBean().setSelectedUnspscObj(commodityCode);
		}

		this.getManagedBean().getSearchDataModel().reloadData();
	}

}
