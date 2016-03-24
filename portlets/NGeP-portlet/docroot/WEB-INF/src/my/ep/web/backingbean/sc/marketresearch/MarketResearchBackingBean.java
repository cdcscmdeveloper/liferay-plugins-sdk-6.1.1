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


import java.io.IOException;
import java.math.BigDecimal;
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
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
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
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sc.marketresearch.MarketResearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;



// TODO: Auto-generated Javadoc
/**
 * The Class MarketResearchBackingBean.
 */
@ManagedBean(name = "marketResearchBackingBean")
@ViewScoped
public class MarketResearchBackingBean extends
		BaseBackingBean<MarketResearchManagedBean> {

	/** The log. */
	private static Logger log = Logger
			.getLogger(MarketResearchBackingBean.class);

	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/sc/marketresearch/marketResearchSearchMain";

	/** The search market research. */
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

	/**
	 * Search market research.
	 */
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

	/**
	 * The Class SearchMarketResearchPagingDataModel.
	 */
	private class SearchMarketResearchPagingDataModel extends
			PagingDataModel<Object[]> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search market research paging data model.
		 */
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

			try {
				marketResearchSO = (IMarketResearchSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION,
								IMarketResearchSO.class);			
				
				MarketResearchResDTO marketResearchResDTO = marketResearchSO
						.searchMarketResearchData(getMarketResearchData(searchBean));
			
				getManagedBean().setLazy(true);
				
				if (marketResearchResDTO.isSuccessful()) {

					ISearchResultBean<Object[]> searchResult = marketResearchResDTO
							.getMarketResearchResultBean();
					
					for (Object[] result : searchResult.getResultList()) {
						
						log.info("LOA Acknowledgement Date == > " + result[0]);
						log.info("File Name == > " + result[2]);
						log.info("Category == > " + result[3]);
						
						if (result[3].equals(new BigDecimal(1))) {

							String loaSupplierTechinical = (String) result[1];
							log.info("Supplier Uploaded Document (Technical) == > "
									+ loaSupplierTechinical);
						} else {
							String loaSupplierFinancial = (String) result[1];
							log.info("Supplier Uploaded Document (Financial)  == > "
									+ loaSupplierFinancial);
						}

					}					
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
	 * Download report.
	 * 
	 * @param filePath
	 *            the file path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void downloadReport(String filePath) throws IOException {
		debugLogger("downloadReport");	
		//To download file from local system
		    //downloadFile(filePath);
		
		//To download file from server
		downloadFile(getActualFilePath(filePath));
	}
		
	/**
	 * To get Actual file path for attachement.
	 * 
	 * @param filePath
	 *            the file path
	 * @return filePath.
	 */
	protected String getActualFilePath(String filePath) {
		String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		StringBuilder actualFilePath = new StringBuilder(docRepoBaseDir);
		actualFilePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		actualFilePath.append(filePath);

		return actualFilePath.toString();
	}
	
	/**
	 * Gets the market research data.
	 * 
	 * @param searchBean
	 *            the search bean
	 * @return the market research data
	 */
	/*private MarketResearchReqDTO getMarketResearchData(
			ISearchBean<Object[]> searchBean) {

		debugLogger("searchMarketResearchData");

		MarketResearchReqDTO reqDTO=new MarketResearchReqDTO();
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("YYYY");	
		

		if (StringUtils.isNotBlank(getManagedBean().getProcurementType().toString())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("ritem.item_type_id",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getProcurementType()));
		}
		if (StringUtils.isNotBlank(getManagedBean().getProcurementYear().toString())) {
			List<String> procYearlist=new ArrayList<String>();
			procYearlist.add(simpleDateformat.format(getManagedBean().getProcurementYear()));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("extract(YEAR FROM lo.ISSUE_DATE)",
							SearchOperator.OPERATOR_IN.value(),
							procYearlist));
		}

		if (StringUtils.isNotBlank(getManagedBean().getProcTitle())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("sq.QT_TITLE",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getProcTitle()));
		}
		if (StringUtils.isNotBlank(getManagedBean().getItemName())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("tqs.CL_ITEM",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getItemName()));
		}

		if (StringUtils.isNotBlank(getManagedBean().getSpecification())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("tqs.CL_SPEC_DETAIL",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSpecification()));
		}		

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("sa.DOC_TYPE",
						SearchOperator.OPERATOR_EQUALS.value(), "QPC"));
		
		
		if (StringUtils.isNotBlank(getManagedBean().getSpecification())) {
			List<Integer> categoryList = new ArrayList<Integer>();
			categoryList.add(1);
			categoryList.add(2);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("sqc.DOC_CATEGORY",
							SearchOperator.OPERATOR_IN.value(), categoryList));
		}

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
					new SearchFilterBean("cat.CATEGORY_L3_ID",
							SearchOperator.OPERATOR_IN.value(),categoryL3L));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("GROUP BY lo.ACK_DATE,sa.FILE_NAME,sa.FILE_PATH,sqc.DOC_CATEGORY"));
			
			if (selectionType
					.equals(SourcingConstants.CATEGORY_TYPE_ALL_CATEGORIES)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("HAVING COUNT(cat.CATEGORY_L3_ID)",
								SearchOperator.OPERATOR_EQUALS.value(),
								categoryL3L.size()));
			}

		} else {
			throw new EpException(MessageConst.ERROR_789001);
		}
		
		reqDTO.setMarketResearchBean(searchBean);
		return reqDTO;
}*/
			
	private MarketResearchReqDTO getMarketResearchData(
			ISearchBean<Object[]> searchBean) {
        log.info("getMarketResearchData");
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
		
		if (StringUtils.isNotBlank(this.getManagedBean().getSelectionType())
				|| CollectionUtils.isNotEmpty(scQtcategoryList)) {
			
			for (ScQtCategory scQtCategory : scQtcategoryList) {

				categoryL3L.add(scQtCategory.getPmCategoryL3()
						.getCategoryL3Id());
			}
		}
		debugLogger("Category Code Selection(CategoryL3-Count)===> "
				+ scQtcategoryList.size());	
		
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
		category.setPmCategoryL1Name(
				pmCategoryL2.getPmCategoryL1().getCategoryName());
		category.setPmCategoryL2Name(
				pmCategoryL2.getCategoryName());
		category.setPmCategoryL3Name(
				pmCategoryL3.getCategoryName());
		
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
	 * Gets the search market research.
	 * 
	 * @return the searchMarketResearch
	 */
	public IPagingDataModel<Object[]> getSearchMarketResearch() {
		return searchMarketResearch;
	}

	/**
	 * Sets the search market research.
	 * 
	 * @param searchMarketResearch
	 *            the searchMarketResearch to set
	 */
	public void setSearchMarketResearch(
			IPagingDataModel<Object[]> searchMarketResearch) {
		this.searchMarketResearch = searchMarketResearch;
	}

}
