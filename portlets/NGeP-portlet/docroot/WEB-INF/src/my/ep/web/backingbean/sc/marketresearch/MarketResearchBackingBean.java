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
package my.ep.web.backingbean.sc.marketresearch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.marketresearch.IMarketResearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.marketresearch.MarketResearchReqDTO;
import my.ep.dto.sc.marketresearch.MarketResearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sc.marketresearch.MarketResearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "marketResearchBackingBean")
@ViewScoped
public class MarketResearchBackingBean extends
		BaseBackingBean<MarketResearchManagedBean> {

	/** The log. */
	private static Logger log = Logger
			.getLogger(MarketResearchBackingBean.class);

	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/sc/marketresearch/marketResearchSearchMain";

	private IPagingDataModel<Object[]> searchMarketResearch = new SearchMarketResearchPagingDataModel();

	/**
	 * Instantiates a new suppliers review backing bean.
	 */
	public MarketResearchBackingBean() {

		log.info("inside MarketResearchBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

	}

	/**
	 * Pre loading suppliers review.
	 */
	public void preLoadingMarketResearch() {
		log.info("preLoadingMarketResearch method");
		FacesContext fc = FacesContext.getCurrentInstance();
		resetMarketResearch();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * reset search bean fields.
	 */
	public void resetMarketResearch() {
		debugLogger("resetMarketResearch");
		this.getManagedBean().setLazy(false);
		this.getManagedBean().setProcurementType(0);
		this.getManagedBean().setItemName(null);
		this.getManagedBean().setProcTitle(null);
		this.getManagedBean().setProcurementYear(null);
		this.getManagedBean().setSpecification(null);
		this.getManagedBean().setSelectionType(null);
		this.getManagedBean().setQtCategoryList(null);
		searchMarketResearch = new SearchMarketResearchPagingDataModel();
	}

	public void searchMarketResearch() {
		
        log.info("searchMarketResearch");
		List<ScQtCategory> scQtcategoryList = this.getManagedBean()
				.getQtCategoryList();
		if (StringUtils.isBlank(this.getManagedBean().getSelectionType())
				|| CollectionUtils.isEmpty(scQtcategoryList)) {
			throw new EpException(MessageConst.ERROR_789001);
		}
		searchMarketResearch.reloadData();
	}

	private class SearchMarketResearchPagingDataModel extends
			PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public SearchMarketResearchPagingDataModel() {
			super("marketdetailsId", getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IMarketResearchSO marketResearchSO = null;

			//ISearchBean<Object[]> iSearchBean = getMarketResearchData(searchBean);
			/*MarketResearchReqDTO reqDTO = new MarketResearchReqDTO();*/

			try {
				marketResearchSO = (IMarketResearchSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION,
								IMarketResearchSO.class);

			/*	reqDTO.setMarketResearchBean(getMarketResearchData(searchBean));*/
				
				MarketResearchResDTO marketResearchResDTO = marketResearchSO
						.searchMarketResearchData(getMarketResearchData(searchBean));
				getManagedBean().setLazy(true);
				if (marketResearchResDTO.isSuccessful()) {

					return marketResearchResDTO.getMarketResearchResultBean();

				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);

			} finally {
				marketResearchSO = null;
			}
			return null;
		}
	}

	/**
	 * Gets the market research data.
	 * 
	 * @param searchBean
	 *            the search bean
	 * @return the market research data
	 */
	/*private ISearchBean<Object[]> getMarketResearchData(
			ISearchBean<Object[]> searchBean) {

		debugLogger("searchMarketResearchData");

		SimpleDateFormat simpleDateformat = new SimpleDateFormat("YYYY");

		if (StringUtils.isNotBlank(getManagedBean().getProcurementType().toString())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("ritem.item_type_id",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getProcurementType()));
		}
		if (StringUtils.isNotBlank(getManagedBean().getProcurementYear().toString())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("lo.issue_date",
							SearchOperator.OPERATOR_IN.value(),
							simpleDateformat.format(getManagedBean()
									.getProcurementYear())));
		}

		if (StringUtils.isNotBlank(getManagedBean().getProcTitle())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("sq.qt_title",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getProcTitle()));
		}
		if (StringUtils.isNotBlank(getManagedBean().getItemName())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("itemName",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getItemName()));
		}

		if (StringUtils.isNotBlank(getManagedBean().getSpecification())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("specification",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSpecification()));
		}

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("sa.doc_type",
						SearchOperator.OPERATOR_EQUALS.value(), "QPC"));

		List<ScQtCategory> scQtcategoryList = this.getManagedBean()
				.getQtCategoryList();

		List<Long> categoryL3L = new ArrayList<Long>();

		String selectionType = this.getManagedBean().getSelectionType();

		if (selectionType != null
				&& (scQtcategoryList != null && !scQtcategoryList.isEmpty())) {

			for (ScQtCategory scQtCategory : scQtcategoryList) {

				categoryL3L.add(scQtCategory.getPmCategoryL3()
						.getCategoryL3Id());
			}
			debugLogger("Category Code Selection(CategoryL3-Count)----> "
					+ scQtcategoryList.size());

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("selectionType",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSelectionType()));

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("cat.category_l3_id",
							SearchOperator.OPERATOR_IN.value(), categoryL3L));

			if (selectionType
					.equals(SourcingConstants.CATEGORY_TYPE_ALL_CATEGORIES)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("cat.category_l3_id",
								SearchOperator.OPERATOR_EQUALS.value(),
								categoryL3L.size()));
			}

		} else {
			throw new EpException(MessageConst.ERROR_789001);
		}
}*/
			
	private MarketResearchReqDTO getMarketResearchData(
			ISearchBean<Object[]> searchBean) {

		Integer procurementType = this.getManagedBean().getProcurementType();
		Date procurementYear = this.getManagedBean().getProcurementYear();
		String procTitle = this.getManagedBean().getProcTitle();
		String itemName = this.getManagedBean().getItemName();
		String specification = this.getManagedBean().getSpecification();
		List<Long> categoryL3L = new ArrayList<Long>();

		String selectionType = this.getManagedBean().getSelectionType();

		List<ScQtCategory> scQtcategoryList = this.getManagedBean()
				.getQtCategoryList();

		MarketResearchReqDTO reqDTO = new MarketResearchReqDTO();

		SimpleDateFormat simpleDateformat = new SimpleDateFormat("YYYY");
		
		for (ScQtCategory scQtCategory : scQtcategoryList) {

			categoryL3L.add(scQtCategory.getPmCategoryL3()
					.getCategoryL3Id());
		}
		debugLogger("Category Code Selection(CategoryL3-Count)===> "
				+ scQtcategoryList.size());
		
		/*if (selectionType != null
				&& (scQtcategoryList != null && !scQtcategoryList.isEmpty())) {*/			
		/*//} /*else {
			throw new EpException(MessageConst.ERROR_789001);
		}*/
		
		reqDTO.setSelectionType(selectionType);
		reqDTO.setDocType("QPC");
		reqDTO.setCategoryL3IdList(categoryL3L);
		reqDTO.setProcurementType(procurementType);
		reqDTO.setProcurementYear(simpleDateformat.format(procurementYear));
		reqDTO.setProcurementTitle(procTitle);
		reqDTO.setItemName(itemName);
		reqDTO.setSpecification(specification);
		reqDTO.setMarketResearchBean(searchBean);

		return reqDTO;
	}

	/**
	 * Adds the new category action.
	 * 
	 * @param pmCategoryL3
	 *            the pm category l3
	 */
	public void addNewCategoryAction(PmCategoryL3 pmCategoryL3) {
		debugLogger("addNewCategoryAction");
		List<ScQtCategory> qtCategoryList = this.getManagedBean()
				.getQtCategoryList();
		if (qtCategoryList == null) {
			qtCategoryList = new ArrayList<ScQtCategory>();
			this.getManagedBean().setQtCategoryList(qtCategoryList);
		}
		PmCategoryL2 pmCategoryL2 = pmCategoryL3.getPmCategoryL2();
		ScQtCategory category = new ScQtCategory();
		category.setPmCategoryL1(pmCategoryL2.getPmCategoryL1());
		category.setPmCategoryL2(pmCategoryL2);
		category.setPmCategoryL3(pmCategoryL3);
		qtCategoryList.add(category);
	}

	/**
	 * Delete category codes.
	 */
	public void deleteCategoryCodes() {
		debugLogger("deleteCategoryCodes");
		List<ScQtCategory> openList = this.getManagedBean().getQtCategoryList();
		if (null == openList) {
			openList = new ArrayList<ScQtCategory>();
		}
		List<ScQtCategory> openSelectList = this.getManagedBean()
				.getSelectedCategoryList();
		if (null == openSelectList || openSelectList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		if (openList.size() == openSelectList.size()) {
			openList.clear();
		} else {
			for (ScQtCategory category : openSelectList)
				openList.remove(category);
		}
	}

	/**
	 * Gets the role type dropdown values.display item type(product/Service)in
	 * template spec dropdown.
	 * 
	 * @param itemType
	 *            (String itemType)
	 * @return SelectItem[] .
	 */
	public SelectItem[] getRoleTypeDropdownValues(String itemType) {
		debugLogger("getRoleTypeDropdownValues");
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();

		recommendList.add(ParameterConst.ITEM_TYPE_PRODUCT);
		recommendList.add(ParameterConst.ITEM_TYPE_SERVICE);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_ITEM_TYPE, searchBean);
		return items;
	}

	/**
	 * @return the searchMarketResearch
	 */
	public IPagingDataModel<Object[]> getSearchMarketResearch() {
		return searchMarketResearch;
	}

	/**
	 * @param searchMarketResearch
	 *            the searchMarketResearch to set
	 */
	public void setSearchMarketResearch(
			IPagingDataModel<Object[]> searchMarketResearch) {
		this.searchMarketResearch = searchMarketResearch;
	}

}
