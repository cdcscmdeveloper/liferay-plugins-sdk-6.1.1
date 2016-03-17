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
package my.ep.web.backingbean.pm.category;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmBasicCategory;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCategoryRestr;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmMachineCategory;
import my.ep.persistence.entity.pm.common.PmMachineSection;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.CategoryCodePublicSearchBean;
import my.ep.web.managedbean.pm.category.CategoryManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

// TODO: Auto-generated Javadoc
/**
 * The Class CategoryBackingBean.
 * 
 * @author hktan
 */
@ManagedBean(name = "categoryBackingBean")
@ViewScoped
// @RequestScoped
public class CategoryBackingBean extends BaseBackingBean<CategoryManagedBean> {

	/** The log. */
	private static Logger log = Logger.getLogger(CategoryBackingBean.class);

	/** The search category. */
	private IPagingDataModel<Object[]> searchCategory = new SearchCategoryPagingDataModel();

	/** The search lov paging data model. */
	private IPagingDataModel<PmCategoryL3> searchLOVPagingDataModel = new SearchLOVPagingDataModel();

	/** The search lov machine paging data model. */
	private IPagingDataModel<PmMachine> searchLOVMachinePagingDataModel = new SearchLOVMachinePagingDataModel();
	
	/** The public search category. */
	private IPagingDataModel<PmCategoryL3> publicSearchCategoryPagingDataModel = new PublicSearchCategoryPagingDataModel();

	/** The flag. */
	private boolean flag;

	/** The result. */
	boolean result = false;
    private String originalCategoryName;
    private boolean categoryNameChanged = false;
	private DecimalFormat amountDF = null;
    
    
	public boolean isCategoryNameChanged() {
		return categoryNameChanged;
	}

	public void setCategoryNameChanged(boolean categoryNameChanged) {
		this.categoryNameChanged = categoryNameChanged;
	}

	public String getOriginalCategoryName() {
		return originalCategoryName;
	}

	public void setOriginalCategoryName(String originalCategoryName) {
		this.originalCategoryName = originalCategoryName;
	}

	//Authorization 
	private ISearchBean<PmParameterDesc> facilityTypeSearchBean;
	
	//Param Type as key
	private Map<String, List<PmParameterDesc>> parameterDescMap = 
			new HashMap<String, List<PmParameterDesc>>();


	public void setFacilityTypeSearchBean(
			ISearchBean<PmParameterDesc> facilityTypeSearchBean) {
		this.facilityTypeSearchBean = facilityTypeSearchBean;
	}
	
	public void categoryNameChanges(ValueChangeEvent event){
		log.info("...event.getOldValue()="+(String) event.getOldValue());
		log.info("...event.getNewValue()="+(String) event.getNewValue());
		if(originalCategoryName != null && !originalCategoryName.isEmpty()){
			if(originalCategoryName.equalsIgnoreCase((String)event.getNewValue())){
				setCategoryNameChanged(false);
			}else{
				setCategoryNameChanged(true);
			}
		}else{//initial original category desc
			originalCategoryName = (String) event.getOldValue();
		}
	}
	/**
	 * Instantiates a new category backing bean.
	 */
	public CategoryBackingBean() {

	}

	/**
	 * Search category.
	 */
	public void SearchCategory() {
		boolean hasSearchValue = false;

		if (getManagedBean().getPmCategoryLevel() != null
				&& getManagedBean().getPmCategoryLevel().intValue() > 0) {
			hasSearchValue = true;
		}

		if (!hasSearchValue) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									MessageUtils
											.getMessage(
													MessageConst.ERROR_721110,
													new String[] { ResourceBundleUtils
															.getMessage(
																	"lbl.pm.category.level",
																	ResourceBundleUtils.BUNDLE_COMMON_LABELS,
																	new FacesScopes()
																			.getUserLocale()) },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			// throw new EpException(MessageConst.ERROR_721110);
		}

		searchCategory.reloadData();
	}

	/**
	 * The Class SearchCategoryPagingDataModel.
	 */
	private class SearchCategoryPagingDataModel extends
			PagingDataModel<Object[]> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search category paging data model.
		 */
		public SearchCategoryPagingDataModel() {
			super("searchCategory");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			ICategoryCodeSO categoryCodeSO;
			try {

				categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

				if (getManagedBean().getMainCatCode() != null) {
					reqDTO.setMainCatCode(getManagedBean().getMainCatCode());
				}
				if (getManagedBean().getMainCatDesc() != null) {
					reqDTO.setMainCatDesc(getManagedBean().getMainCatDesc());
				}
				if (getManagedBean().getStatus() != null) {
					reqDTO.setStatus(getManagedBean().getStatus());
				}
				if (getManagedBean().getSubCatCode() != null) {
					reqDTO.setSubCatCode(getManagedBean().getSubCatCode());
				}
				if (getManagedBean().getSubCatDesc() != null) {
					reqDTO.setSubCatDesc(getManagedBean().getSubCatDesc());
				}

				if (getManagedBean().getSubDivCode() != null) {
					reqDTO.setSubDivCode(getManagedBean().getSubDivCode());
				}
				if (getManagedBean().getSubDivDesc() != null) {
					reqDTO.setSubDivDesc(getManagedBean().getSubDivDesc());
				}

				if (getManagedBean().getPmCategoryLevel() != null
						&& getManagedBean().getPmCategoryLevel().intValue() > 0) {

					reqDTO.setPmCategoryLevel(getManagedBean()
							.getPmCategoryLevel());
					reqDTO.setSearchBeanObject(searchBean);
					CategoryCodeResDTO resDTO = categoryCodeSO
							.searchAllCategory(reqDTO);

					if (resDTO.isSuccessful()) {
						// resDTO.getCategoryResultBean().getResultList();

						return resDTO.getCategoryResultBean();
					}
				}/*
				 * else{ FacesContext.getCurrentInstance().addMessage( null, new
				 * FacesMessage(FacesMessage.SEVERITY_INFO, null,
				 * MessageUtils.getMessage(MessageConst.INFO_700004, new
				 * String[]
				 * {"",ResourceBundleUtils.getMessage("lbl.pm.sub.category"
				 * ,ResourceBundleUtils.BUNDLE_COMMON_LABELS,new
				 * FacesScopes().getUserLocale())},
				 * MessageUtils.BUNDLE_COMMON_MESSAGES, new
				 * FacesScopes().getUserLocale()))); }
				 */

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				categoryCodeSO = null;
			}

			return null;

		}
	}

	/**
	 * The Class SearchLOVPagingDataModel.
	 */
	private class SearchLOVPagingDataModel extends
			PagingDataModel<PmCategoryL3> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search lov paging data model.
		 */
		public SearchLOVPagingDataModel() {
			super("lovTb2");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<PmCategoryL3> pageChangeListener(
				ISearchBean<PmCategoryL3> searchBean) {
			ICategoryCodeSO categoryCodeSO;
			try {
				categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

				/*
				 * if (getManagedBean().getSearchValue()[0] != null &&
				 * !getManagedBean().getSearchValue()[0].equals("")) {
				 * if(getManagedBean().getSearchFieldValue()[0].equals("1"))
				 * searchBean.getDataFilterBeans().add( new
				 * SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryL1Code",
				 * Integer
				 * .parseInt(getManagedBean().getSearchOperatorValue()[i]),
				 * getManagedBean().getSearchValue()[i])); }
				 */

				/*
				 * if(getManagedBean().getSearchFieldValue()[i].equals("1"))
				 * searchBean.getDataFilterBeans().add( new
				 * SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryL1Code",
				 * Integer
				 * .parseInt(getManagedBean().getSearchOperatorValue()[i]),
				 * getManagedBean().getSearchValue()[i])); else
				 * if(getManagedBean().getSearchFieldValue()[i].equals("2"))
				 * searchBean.getDataFilterBeans().add( new
				 * SearchFilterBean("pmCategoryL2.categoryL2Code",
				 * Integer.parseInt
				 * (getManagedBean().getSearchOperatorValue()[i]),
				 * getManagedBean().getSearchValue()[i])); /*else
				 * if(getManagedBean().getSearchFieldValue()[i].equals("4"))
				 * searchBean.getDataFilterBeans().add( new
				 * SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryName",
				 * Integer
				 * .parseInt(getManagedBean().getSearchOperatorValue()[i]),
				 * getManagedBean().getSearchValue()[i])); else
				 * if(getManagedBean().getSearchFieldValue()[i].equals("5"))
				 * searchBean.getDataFilterBeans().add( new
				 * SearchFilterBean("pmCategoryL2.categoryName",
				 * Integer.parseInt
				 * (getManagedBean().getSearchOperatorValue()[i]),
				 * getManagedBean().getSearchValue()[i]));
				 */

				for (int i = 0; i < 3; i++) {
					if (getManagedBean().getSearchValue()[i] != null
							&& !getManagedBean().getSearchValue()[i].equals("")) {
						if (getManagedBean().getSearchFieldValue()[i] != null
								&& !getManagedBean().getSearchFieldValue()[i]
										.equals("")) {

							if (getManagedBean().getSearchFieldValue()[i]
									.equals("1"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("categoryL3Code",
												SearchOperator.OPERATOR_EQUALS
														.value(),
												getManagedBean()
														.getSearchValue()[i]));
							else if (getManagedBean().getSearchFieldValue()[i]
									.equals("2"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("categoryName",
												SearchOperator.OPERATOR_LIKE
														.value(),
												getManagedBean()
														.getSearchValue()[i]));
						}
					}
				}

				reqDTO.setSearchBean(searchBean);
				CategoryCodeResDTO resDTO = categoryCodeSO
						.searchAllCategoryL3(reqDTO);

				if (resDTO.isSuccessful()) {
					// resDTO.getCategoryResultBean().getResultList();
					return resDTO.getCategoryL3ResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				categoryCodeSO = null;
			}
			return null;
		}
	}

	/**
	 * The Class SearchLOVMachinePagingDataModel.
	 */
	private class SearchLOVMachinePagingDataModel extends
			PagingDataModel<PmMachine> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search lov machine paging data model.
		 */
		public SearchLOVMachinePagingDataModel() {
			super("lovTb22");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<PmMachine> pageChangeListener(
				ISearchBean<PmMachine> searchBean) {
			ICategoryCodeSO categoryCodeSO;
			try {
				categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

				/*
				 * if (getManagedBean().getSearchValue()[0] != null &&
				 * !getManagedBean().getSearchValue()[0].equals("")) {
				 * if(getManagedBean().getSearchFieldValue()[0].equals("1"))
				 * searchBean.getDataFilterBeans().add( new
				 * SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryL1Code",
				 * Integer
				 * .parseInt(getManagedBean().getSearchOperatorValue()[i]),
				 * getManagedBean().getSearchValue()[i])); }
				 */

				for (int i = 0; i < 2; i++) {
					if (getManagedBean().getSearchValue()[i] != null
							&& !getManagedBean().getSearchValue()[i].equals("")) {
						if (getManagedBean().getSearchFieldValue()[i] != null
								&& !getManagedBean().getSearchFieldValue()[i]
										.equals("")) {
							if (getManagedBean().getSearchFieldValue()[i]
									.equals("1"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("machineCode",
												SearchOperator.OPERATOR_EQUALS
														.value(),
												getManagedBean()
														.getSearchValue()[i]
														.toUpperCase()));
							else if (getManagedBean().getSearchFieldValue()[i]
									.equals("2"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("machineDesc",
												SearchOperator.OPERATOR_LIKE
														.value(),
												getManagedBean()
														.getSearchValue()[i]
														.toUpperCase()));
						}
					}
				}

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmMachineSection.categoryType.parameterType",
								SearchOperator.OPERATOR_EQUALS.value(), "CAT"));

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmMachineSection.categoryType.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(), "PC"));

				reqDTO.setSearchMachineBean(searchBean);
				CategoryCodeResDTO resDTO = categoryCodeSO
						.searchAllPmMachine(reqDTO);

				if (resDTO.isSuccessful()) {
					// resDTO.getCategoryResultBean().getResultList();
					return resDTO.getPmMachineResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				categoryCodeSO = null;
			}
			return null;
		}
	}

	/**
	 * Adds the category.
	 * 
	 * @return the string
	 */
	public String addCategory() {
		this.getManagedBean().setPmCategoryL1(new PmCategoryL1());
		this.getManagedBean().setCatLevel("1");
		this.getManagedBean().setNewCategory(true);
		return "/xhtml/pm/category/categoryl1.xhtml";
	}

	/**
	 * Adds the sub category.
	 * 
	 * @return the string
	 */
	public String addSubCategory() {
		PmCategoryL2 pmCategoryL2 = new PmCategoryL2();
		pmCategoryL2.setPmCategoryL1(this.getManagedBean().getPmCategoryL1());

		this.getManagedBean().setPmCategoryL2(pmCategoryL2);
		this.getManagedBean().setCatLevel("2");
		this.getManagedBean().setNewCategory(true);
		return "/xhtml/pm/category/categoryl2.xhtml";
	}

	/**
	 * Adds the sub category.
	 * 
	 * @param id
	 *            (Long id(mandatory))
	 * @return String(/xhtml/pm/category/categoryl2.xhtml?faces-redirect=true)
	 */
	public String addSubCategory(Long id) {
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						ICategoryCodeSO.class);
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

			log.info("addSubCategory parent id = " + id);

			reqDTO.setCategoryL1Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO
					.retrieveCategoryL1DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL1());

				PmCategoryL2 pmCategoryL2 = new PmCategoryL2();
				pmCategoryL2.setPmCategoryL1(this.getManagedBean()
						.getPmCategoryL1());
				this.getManagedBean().setPmCategoryL2(pmCategoryL2);
				this.getManagedBean().setCatLevel("2");
				this.getManagedBean().setNewCategory(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			categoryCodeSO = null;
		}

		return "/xhtml/pm/category/categoryl2.xhtml";
	}

	/**
	 * Adds the sub division.
	 * 
	 * @return string(/xhtml/pm/category/categoryl3.xhtml)
	 */
	public String addSubDivision() {
		PmCategoryL3 pmCategoryL3 = new PmCategoryL3();
		pmCategoryL3.setPmCategoryL2(this.getManagedBean().getPmCategoryL2());

		pmCategoryL3.setCategoryL3Id(null);
		pmCategoryL3.setSupplierTypeCodeVal(0);
		pmCategoryL3.setCategoryTypeCodeVal(0);

		this.getManagedBean().setPmCategoryL3(pmCategoryL3);
		// this.getManagedBean().setPmBasicCategoryList(new
		// ArrayList<PmBasicCategory>());
		// this.getManagedBean().setPmBasicCategoryList(null);
		this.getManagedBean().setCatLevel("3");
		this.getManagedBean().setNewCategory(true);
		reloadSelectionPmCategoryL3();
		return "/xhtml/pm/category/categoryl3.xhtml";
	}

	/**
	 * Adds the sub division.
	 * 
	 * @param id
	 *            (Long id(Mandatory))
	 * @return string(/xhtml/pm/category/categoryl3.xhtml?faces-redirect=true)
	 */
	public String addSubDivision(Long id) {
		ICategoryCodeSO categoryCodeSO;
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);

			reqDTO.setCategoryL2Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO
					.retrieveCategoryL2DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setPmCategoryL2(resDTO.getCategoryL2());

				PmCategoryL3 pmCategoryL3 = new PmCategoryL3();
				pmCategoryL3.setPmCategoryL2(this.getManagedBean()
						.getPmCategoryL2());

				pmCategoryL3.setCategoryL3Id(null);
				pmCategoryL3.setSupplierTypeCodeVal(0);
				pmCategoryL3.setCategoryTypeCodeVal(0);

				this.getManagedBean().setPmCategoryL3(pmCategoryL3);
				this.getManagedBean().setPmCategoryL1(
						this.getManagedBean().getPmCategoryL2()
								.getPmCategoryL1());
				// this.getManagedBean().setPmBasicCategoryList(new
				// ArrayList<PmBasicCategory>());
				// this.getManagedBean().setPmBasicCategoryList(null);
				this.getManagedBean().setCatLevel("3");
				this.getManagedBean().setNewCategory(true);
				reloadSelectionPmCategoryL3();
				updateSpecialCategory();

				if (CollectionUtils.isNotEmpty(this.getManagedBean()
						.getBasicCategoryList())) {
					this.getManagedBean().getBasicCategoryList().clear();
				}
				if (CollectionUtils.isNotEmpty(this.getManagedBean()
						.getPmMachineCategoryList())) {
					this.getManagedBean().getPmMachineCategoryList().clear();
				}
				if (CollectionUtils.isNotEmpty(this.getManagedBean()
						.getCategoryRestrList())) {
					this.getManagedBean().getCategoryRestrList().clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			categoryCodeSO = null;
		}

		return "/xhtml/pm/category/categoryl3.xhtml?faces-redirect=true";
	}

	/**
	 * Adds the basic category.
	 */
	public void addBasicCategory() {

		List<SelectItem> searchField = new ArrayList<SelectItem>();

		/*
		 * searchField.add(new SelectItem(1,
		 * ResourceBundleUtils.getMessage("lbl.pm.main.category.code"
		 * ,ResourceBundleUtils.BUNDLE_COMMON_LABELS,new
		 * FacesScopes().getUserLocale()))); searchField.add(new SelectItem(2,
		 * ResourceBundleUtils
		 * .getMessage("lbl.pm.main.category",ResourceBundleUtils
		 * .BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		 * 
		 * searchField.add(new SelectItem(1,
		 * ResourceBundleUtils.getMessage("lbl.pm.sub.category.code"
		 * ,ResourceBundleUtils.BUNDLE_COMMON_LABELS,new
		 * FacesScopes().getUserLocale()))); searchField.add(new SelectItem(2,
		 * ResourceBundleUtils
		 * .getMessage("lbl.pm.sub.category",ResourceBundleUtils
		 * .BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		 */
		searchField.add(new SelectItem(1, ResourceBundleUtils.getMessage(
				"lbl.pm.sub.div.code",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())));
		searchField.add(new SelectItem(2, ResourceBundleUtils.getMessage(
				"lbl.pm.sub.div.name.det",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())));

		List<SelectItem> searchOperator = new ArrayList<SelectItem>();

		SelectItem item1 = new SelectItem(
				SearchOperator.OPERATOR_EQUALS.value(),
				ResourceBundleUtils.getMessage("lbl.operator.equals",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		searchOperator.add(item1);

		/*
		 * SelectItem item2 = new
		 * SelectItem(SearchOperator.OPERATOR_STARTS_WITH.value(),
		 * ResourceBundleUtils
		 * .getMessage("lbl.operator.startWith",ResourceBundleUtils
		 * .BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())); SelectItem
		 * item3 = new SelectItem(SearchOperator.OPERATOR_END_WITH.value(),
		 * ResourceBundleUtils
		 * .getMessage("lbl.operator.endWith",ResourceBundleUtils
		 * .BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		 *//*
			 * searchField.add(new SelectItem(4,
			 * ResourceBundleUtils.getMessage("lbl.pm.main.category.name"
			 * ,ResourceBundleUtils.BUNDLE_COMMON_LABELS,new
			 * FacesScopes().getUserLocale()))); searchField.add(new
			 * SelectItem(5,
			 * ResourceBundleUtils.getMessage("lbl.pm.sub.category.name"
			 * ,ResourceBundleUtils.BUNDLE_COMMON_LABELS,new
			 * FacesScopes().getUserLocale())));
			 *//*
				 * searchField.add(new SelectItem(1,
				 * ResourceBundleUtils.getMessage
				 * ("lbl.pm.main.category.code",ResourceBundleUtils
				 * .BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
				 * searchField.add(new SelectItem(2,
				 * ResourceBundleUtils.getMessage
				 * ("lbl.pm.sub.category.code",ResourceBundleUtils
				 * .BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
				 *//*
					 * searchOperator.add(item2); searchOperator.add(item3);
					 */

		this.getManagedBean().setSearchField(searchField);
		this.getManagedBean().setSearchOperator(searchOperator);
		this.getManagedBean().getPmCategoryL3();

	}

	/**
	 * Adds the pm machine.
	 */
	public void addPmMachine() {

		List<SelectItem> searchField = new ArrayList<SelectItem>();

		searchField.add(new SelectItem(1, ResourceBundleUtils.getMessage(
				"lbl.pm.machine.code",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())));
		searchField.add(new SelectItem(2, ResourceBundleUtils.getMessage(
				"lbl.pm.machine.desc",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())));

		List<SelectItem> searchOperator = new ArrayList<SelectItem>();

		SelectItem item1 = new SelectItem(
				SearchOperator.OPERATOR_EQUALS.value(),
				ResourceBundleUtils.getMessage("lbl.operator.equals",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		searchOperator.add(item1);

		/*
		 * SelectItem item2 = new
		 * SelectItem(SearchOperator.OPERATOR_STARTS_WITH.value(),
		 * ResourceBundleUtils
		 * .getMessage("lbl.operator.startWith",ResourceBundleUtils
		 * .BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())); SelectItem
		 * item3 = new SelectItem(SearchOperator.OPERATOR_END_WITH.value(),
		 * ResourceBundleUtils
		 * .getMessage("lbl.operator.endWith",ResourceBundleUtils
		 * .BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		 *//*
			 * searchOperator.add(item2); searchOperator.add(item3);
			 */

		this.getManagedBean().setSearchField(searchField);
		this.getManagedBean().setSearchOperator(searchOperator);
	}

	/**
	 * Edits the category.
	 * 
	 * @param id
	 *            (Long id(mandatory))
	 * @return string(/xhtml/pm/category/categoryl1.xhtml?faces-redirect=true)
	 */
	public String editCategory(Long id) {
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						ICategoryCodeSO.class);
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

			log.info("editCategory id = " + id);

			reqDTO.setCategoryL1Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO
					.retrieveCategoryL1DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("resDTO.getCategoryL1().getPmCategoryL2s().size() = "
						+ resDTO.getCategoryL1().getPmCategoryL2s().size());

				this.getManagedBean().setCatLevel("1");
				PmCategoryL1 oldCategoryL1 = (PmCategoryL1) BeanUtils
						.cloneBean(resDTO.getCategoryL1());
				this.getManagedBean().setOldCategoryL1(oldCategoryL1);
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL1());
				this.getManagedBean().setNewCategory(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			categoryCodeSO = null;
		}
		return "/xhtml/pm/category/categoryl1.xhtml";
	}

	/**
	 * Pre render pm categoryl1.
	 */
	public void preRenderPmCategoryl1() {
		log.info("In preRenderPmCategoryl1");
		ICategoryCodeSO categoryCodeSO;
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);

			/*
			 * if(this.getManagedBean().isNewCategory()){
			 * reqDTO.setCategoryL2Id(
			 * this.getManagedBean().getPmCategoryL2().getCategoryL2Id());
			 * CategoryCodeResDTO resDTO =
			 * categoryCodeSO.retrieveCategoryL2DtlById(reqDTO);
			 * reqDTO.setCategoryL1Id
			 * (resDTO.getCategoryL2().getPmCategoryL1().getCategoryL1Id()); }
			 */
			reqDTO.setCategoryL1Id(this.getManagedBean().getPmCategoryL1()
					.getCategoryL1Id());

			CategoryCodeResDTO resDTO = categoryCodeSO
					.retrieveCategoryL1DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCatLevel("1");
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL1());
				this.getManagedBean().setNewCategory(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			categoryCodeSO = null;
		}
		// return "/xhtml/pm/category/categoryl1.xhtml";
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"/xhtml/pm/category/categoryl1.xhtml");
	}

	/**
	 * Pre load pm categoryl2.
	 * 
	 * @param categoryL3Id
	 *            the category l3 id
	 */
	public void preLoadPmCategoryl2(Long categoryL3Id) {
		ICategoryCodeSO categoryCodeSO;
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);

			reqDTO.setCategoryL2Id(categoryL3Id);
			CategoryCodeResDTO resDTO = categoryCodeSO
					.retrieveCategoryL2DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setPmCategoryL2(resDTO.getCategoryL2());

				PmCategoryL3 pmCategoryL3 = new PmCategoryL3();
				pmCategoryL3.setPmCategoryL2(this.getManagedBean()
						.getPmCategoryL2());

				pmCategoryL3.setCategoryL3Id(null);
				pmCategoryL3.setSupplierTypeCodeVal(0);
				pmCategoryL3.setCategoryTypeCodeVal(0);

				this.getManagedBean().setPmCategoryL3(pmCategoryL3);
				this.getManagedBean().setCatLevel("2");
				this.getManagedBean().setNewCategory(false);
				// reloadSelectionPmCategoryL3();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			categoryCodeSO = null;
		}
	}

	/**
	 * Edits the sub category.
	 * 
	 * @param id
	 *            (Long id(mandatory))
	 * @return string(/xhtml/pm/category/categoryl2.xhtml?faces-redirect=true)
	 */
	public String editSubCategory(Long id) {
		ICategoryCodeSO categoryCodeSO;
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);

			reqDTO.setCategoryL2Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO
					.retrieveCategoryL2DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCatLevel("2");
				this.getManagedBean().setPmCategoryL2(resDTO.getCategoryL2());
				this.getManagedBean().setPmCategoryL1(
						resDTO.getCategoryL2().getPmCategoryL1());
				PmCategoryL2 oldCategoryL2 = (PmCategoryL2) BeanUtils
						.cloneBean(resDTO.getCategoryL2());
				this.getManagedBean().setOldCategoryL2(oldCategoryL2);
				this.getManagedBean().setNewCategory(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			categoryCodeSO = null;
		}
		return "/xhtml/pm/category/categoryl2.xhtml";
	}

	/**
	 * Edits the sub division.
	 * 
	 * @param id
	 *            Long id(mandatory))
	 * @return string(/xhtml/pm/category/categoryl3.xhtml?faces-redirect=true)
	 */
	public String editSubDivision(Long id) {
		log.info("***editSubDivision id***" + id);

		loadPmCategoryL3Data(id);

		return "/xhtml/pm/category/categoryl3.xhtml?faces-redirect=true";
	}

	/**
	 * Load pm category l3 data.
	 * 
	 * @param id
	 *            the id
	 */
	private void loadPmCategoryL3Data(Long id) {

		List<PmMachineCategory> removedDuplicatePmMachineCategory = new ArrayList<PmMachineCategory>();
		Set<PmMachineCategory> pmMachineCategorySet = null;
		List<PmBasicCategory> removedDuplicatePmBasicCategory = new ArrayList<PmBasicCategory>();
		Set<PmBasicCategory> pmBasicCategorySet = null;
		ICategoryCodeSO categoryCodeSO;

		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);

			reqDTO.setCategoryL3Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO
					.retrieveCategoryL3DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("***editSubDivision - Category Name***"
						+ resDTO.getCategoryL3().getCategoryName());
				this.getManagedBean().setCatLevel("3");
				resDTO.getCategoryL3()
						.setBasicCategoryReqValue(
								resDTO.getCategoryL3().getBasicCategoryReq() == 1 ? true
										: false);
				resDTO.getCategoryL3().setHomeOperatedValue(
						resDTO.getCategoryL3().getHomeOperated() == 1 ? true
								: false);
				this.getManagedBean().setPmCategoryL3(resDTO.getCategoryL3());
				this.getManagedBean().setNewCategory(false);

				reloadSelectionPmCategoryL3();
				updateSpecialCategory();

				for (PmBasicCategory pmBasicCategory : this.getManagedBean()
						.getPmCategoryL3().getPmBasicCategory()) {

					reqDTO.setCategoryL3Id(pmBasicCategory.getBasicCategoryL3());

					CategoryCodeResDTO bresDTO = categoryCodeSO
							.retrieveCategoryL3DtlById(reqDTO);

					pmBasicCategory.setBasicCategoryL1desc(bresDTO
							.getCategoryL3().getPmCategoryL2()
							.getPmCategoryL1().getCategoryName());
					pmBasicCategory.setBasicCategoryL2desc(bresDTO
							.getCategoryL3().getPmCategoryL2()
							.getCategoryName());
					pmBasicCategory.setBasicCategoryL3desc(bresDTO
							.getCategoryL3().getCategoryName());
					pmBasicCategory.setCodeConcatenate(bresDTO.getCategoryL3()
							.getPmCategoryL2().getPmCategoryL1()
							.getCategoryL1Code()
							+ bresDTO.getCategoryL3().getPmCategoryL2()
									.getCategoryL2Code()
							+ bresDTO.getCategoryL3().getCategoryL3Code());
				}
				this.getManagedBean().setBasicCategoryList(
						new ArrayList<PmBasicCategory>());
				this.getManagedBean().setPmMachineCategoryList(
						new ArrayList<PmMachineCategory>());
				this.getManagedBean().setCategoryRestrList(
						new ArrayList<PmCategoryRestr>());

				if (this.getManagedBean().getPmCategoryL3() != null
						&& this.getManagedBean().getPmCategoryL3()
								.getPmBasicCategory() != null) {

					pmBasicCategorySet = new LinkedHashSet<PmBasicCategory>(
							this.getManagedBean().getPmCategoryL3()
									.getPmBasicCategory());
					removedDuplicatePmBasicCategory.clear();
					removedDuplicatePmBasicCategory.addAll(pmBasicCategorySet);

					for (PmBasicCategory removedDpmBasicCategory : removedDuplicatePmBasicCategory) {
						if (!removedDpmBasicCategory.getRecordStatusFlg()
								.equals(DbValueConst.RECORD_STATUS_DELETED)) {
							removedDpmBasicCategory
									.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							this.getManagedBean().getBasicCategoryList()
									.add(removedDpmBasicCategory);
						}
					}
				}
				log.info("save--MACHINE_Update-->"
						+ this.getManagedBean().getPmCategoryL3()
								.getPmMachineCategory().size());
				if (this.getManagedBean().getPmCategoryL3() != null
						&& this.getManagedBean().getPmCategoryL3()
								.getPmMachineCategory() != null) {

					pmMachineCategorySet = new LinkedHashSet<PmMachineCategory>(
							this.getManagedBean().getPmCategoryL3()
									.getPmMachineCategory());
					removedDuplicatePmMachineCategory.clear();
					removedDuplicatePmMachineCategory
							.addAll(pmMachineCategorySet);

					for (PmMachineCategory pmMachineCategory : removedDuplicatePmMachineCategory) {
						if (!pmMachineCategory.getRecordStatusFlg().equals(
								DbValueConst.RECORD_STATUS_DELETED)) {
							pmMachineCategory
									.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							this.getManagedBean().getPmMachineCategoryList()
									.add(pmMachineCategory);
						}
					}
				}
				if (this.getManagedBean().getPmCategoryL3() != null
						&& this.getManagedBean().getPmCategoryL3()
								.getPmCategoryRestrs() != null) {
					for (PmCategoryRestr pmCategoryRestr : this
							.getManagedBean().getPmCategoryL3()
							.getPmCategoryRestrs()) {
						if (!pmCategoryRestr.getRecordStatusFlg().equals(
								DbValueConst.RECORD_STATUS_DELETED)) {
							pmCategoryRestr
									.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							this.getManagedBean().getCategoryRestrList()
									.add(pmCategoryRestr);
						}
					}
				}

				this.getManagedBean().setPmCategoryL2(
						resDTO.getCategoryL3().getPmCategoryL2());
				this.getManagedBean().setPmCategoryL3(resDTO.getCategoryL3());
				this.getManagedBean().setPmCategoryL1(
						this.getManagedBean().getPmCategoryL2()
								.getPmCategoryL1());

				PmCategoryL3 oldCategoryL3 = (PmCategoryL3) BeanUtils
						.cloneBean(resDTO.getCategoryL3());
				this.getManagedBean().setOldCategoryL3(oldCategoryL3);
				log.info("***editSubDivision - managed bean L3 - Category Name***"
						+ this.getManagedBean().getPmCategoryL3()
								.getCategoryName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			categoryCodeSO = null;
		}
	}

	/**
	 * Gets the pm action desc.
	 * 
	 * @param messageId
	 *            the message id
	 * @param params
	 *            the params
	 * @return the pm action desc
	 */
	private String getPmActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params,
				MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
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
	 * Save category.
	 * 
	 * @return string
	 * @throws Exception 
	 */
	public String saveCategory() throws Exception {

		ICategoryCodeSO categoryCodeSO;
		String returnUrl = null;
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);

			this.getManagedBean()
					.getPmCategoryL1()
					.setCategoryName(
							this.getManagedBean().getPmCategoryL1()
									.getCategoryName().toUpperCase());

			validatePmCategoryL1();

			if (this.getManagedBean().getPmCategoryL1().getPmCategoryL2s() != null
					&& !this.getManagedBean().getPmCategoryL1()
							.getPmCategoryL2s().isEmpty()) {
				for (PmCategoryL2 pmCategoryL2 : this.getManagedBean()
						.getPmCategoryL1().getPmCategoryL2s()) {
					pmCategoryL2.setRecordStatusFlg(this.getManagedBean()
							.getPmCategoryL1().getRecordStatusFlg());
				}
			}

			reqDTO.setPmCategoryL1(this.getManagedBean().getPmCategoryL1());
			reqDTO.setCategoryLevel(this.getManagedBean().getCatLevel());
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());

			trackingDiaryForCreateAndUpdateOfMainCategory(reqDTO);

			CategoryCodeResDTO resDTO = categoryCodeSO.saveCategory(reqDTO);
			PmCategoryL1 oldCategoryL1 = (PmCategoryL1) BeanUtils
					.cloneBean(resDTO.getCategoryL1());
			this.getManagedBean().setOldCategoryL1(oldCategoryL1);

			if (!this.getManagedBean().isNewCategory()) {
				reloadPmCategoryL1(this.getManagedBean().getPmCategoryL1()
						.getCategoryL1Id());
			}
			if (resDTO.isSuccessful()) {
				if (this.getManagedBean().isNewCategory()) {
					this.getManagedBean().setNewCategory(false);
					returnUrl = "/xhtml/pm/category/search.xhtml";
					result = resDTO.isSuccessful();
					// resetCategory();
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_100200,
															new String[] {
																	ResourceBundleUtils
																			.getMessage(
																					"lbl.pm.main.category",
																					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
																					new FacesScopes()
																							.getUserLocale()),
																	resDTO.getCategoryL1()
																			.getCategoryL1Code() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				}else{
					returnUrl = "/xhtml/pm/category/categoryl1.xhtml";
					result = resDTO.isSuccessful();
					// resetCategory();
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_100201,
															new String[] {
																	ResourceBundleUtils
																			.getMessage(
																					"lbl.pm.main.category",
																					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
																					new FacesScopes()
																							.getUserLocale()),
																	resDTO.getCategoryL1()
																			.getCategoryL1Code() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				}
			}
		} catch (Exception e) {
			throw(e);
		} finally {
			categoryCodeSO = null;
		}

		return returnUrl;
	}

	/**
	 * Tracking diary for create and update of main category.
	 * 
	 * @param reqDTO
	 *            the req dto
	 */
	private void trackingDiaryForCreateAndUpdateOfMainCategory(
			CategoryCodeReqDTO reqDTO) {

		String[] msgParam = new String[] { this.getManagedBean()
				.getPmCategoryL1().getCategoryL1Code() };

		String messageId = "";

		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		StatusReqDTO statusReqDTO = new StatusReqDTO();

		statusReqDTO.setStatusId(this.getManagedBean().getPmCategoryL1()
				.getRecordStatusFlg().longValue());
		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		System.out.println("statusResDTO.getPmStatus().getStatusId():"
				+ statusResDTO.getPmStatus().getStatusId());
		debugLogger("statusResDTO.getPmStatus().getStatusId():"
				+ statusResDTO.getPmStatus().getStatusId());

		if (this.getManagedBean().isNewCategory()) {
			messageId = MessageConst.TD_80700;
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
		} else {
			messageId = MessageConst.TD_80701;

			/*
			 * if(oldPmCategoryL1!=null && newPmCategoryL1!=null &&
			 * !oldPmCategoryL1 .getRecordStatusFlg().equals(newPmCategoryL1.
			 * getRecordStatusFlg ())){
			 * if(this.getManagedBean().getPmCategoryL1().getRecordStatusFlg
			 * ().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
			 * recordStatusMessageId = MessageConst.TD_80703;
			 * 
			 * } else{ recordStatusMessageId = MessageConst.TD_80702; }
			 * reqDTO.getTrackDairyVOs().add(PmCommonUtil.
			 * assignTrackingDairyForPmCategoryL1
			 * (this.getManagedBean().getPmCategoryL1().getCategoryL1Id(),
			 * this.getManagedBean().getPmCategoryL1().getCategoryL1Code(),
			 * getPmActionDesc(recordStatusMessageId,msgParam),null,null)); }
			 */
		}
		reqDTO.setTrackingStatus(true);

		reqDTO.getTrackDairyVOs().add(
				PmCommonUtil.assignTrackingDairyForPmCategoryL1(
						this.getManagedBean().getPmCategoryL1()
								.getCategoryL1Id(),
						this.getManagedBean().getPmCategoryL1()
								.getCategoryL1Code(),
						statusResDTO.getPmStatus() != null ? statusResDTO
								.getPmStatus() : new PmStatus(),
						getPmActionDesc(messageId, msgParam),
						ParameterConst.MAIN_CATEGORY_PARAM_CODE));
	}

	/**
	 * Reload pm category l1.
	 * 
	 * @param categoryL1Id
	 *            the category l1 id
	 */
	private void reloadPmCategoryL1(Long categoryL1Id) {
		log.info("In preRenderPmCategoryl1");
		ICategoryCodeSO categoryCodeSO;
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);
			reqDTO.setCategoryL1Id(categoryL1Id);

			CategoryCodeResDTO resDTO = categoryCodeSO
					.retrieveCategoryL1DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCatLevel("1");
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL1());
				this.getManagedBean().setNewCategory(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			categoryCodeSO = null;
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"/xhtml/pm/category/categoryl1.xhtml");
	}

	/**
	 * Gets the record status list.
	 * 
	 * @return the record status list
	 */
	public static SelectItem[] getRecordStatusList() {
		return PmCommonUtil.getCommonStatusOptions();
	}

	/**
	 * Save sub category.
	 * 
	 * @return the string
	 * @throws Exception 
	 */
	public String saveSubCategory() throws Exception {

		ICategoryCodeSO categoryCodeSO;
		String returnUrl = null;
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);

			reqDTO.setCategoryLevel(this.getManagedBean().getCatLevel());
			this.getManagedBean()
					.getPmCategoryL2()
					.setCategoryName(
							this.getManagedBean().getPmCategoryL2()
									.getCategoryName().toUpperCase());
			
			validatePmCategoryL2();
				

			if (this.getManagedBean().getPmCategoryL2().getPmCategoryL3s() != null
					&& this.getManagedBean().getPmCategoryL2()
							.getPmCategoryL3s().size() > 0) {
				for (PmCategoryL3 pmCategoryL3 : this.getManagedBean()
						.getPmCategoryL2().getPmCategoryL3s()) {
					pmCategoryL3.setRecordStatusFlg(this.getManagedBean()
							.getPmCategoryL2().getRecordStatusFlg());

				}
			}

			reqDTO.setPmCategoryL2(this.getManagedBean().getPmCategoryL2());
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
			reqDTO.setCategoryLevel(this.getManagedBean().getCatLevel());
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());

			trackingDiaryForCreateAndUpdateOfSubCategory(reqDTO);

			CategoryCodeResDTO resDTO = categoryCodeSO.saveSubCategory(reqDTO);
			preLoadPmCategoryl2(this.getManagedBean().getPmCategoryL2()
					.getCategoryL2Id());
			PmCategoryL2 oldCategoryL2 = (PmCategoryL2) BeanUtils
					.cloneBean(resDTO.getCategoryL2());
			this.getManagedBean().setOldCategoryL2(oldCategoryL2);
			
			if (resDTO.isSuccessful()) {
				if (this.getManagedBean().isNewCategory()) {
					this.getManagedBean().setPmCategoryL2(
							resDTO.getCategoryL2());
					this.getManagedBean().getPmCategoryL1().getPmCategoryL2s()
							.add(this.getManagedBean().getPmCategoryL2());
					reqDTO.setPmCategoryL1(this.getManagedBean()
							.getPmCategoryL1());
					categoryCodeSO.saveCategory(reqDTO);
				}

				if (this.getManagedBean().isNewCategory()) {
					returnUrl = "/xhtml/pm/category/search.xhtml";
					this.getManagedBean().setNewCategory(false);
		
					result = resDTO.isSuccessful();
					// resetCategory();
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_100200,
															new String[] {
																	ResourceBundleUtils
																			.getMessage(
																					"lbl.pm.sub.category",
																					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
																					new FacesScopes()
																							.getUserLocale()),
																	resDTO.getCategoryL2()
																			.getCategoryL2Code() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				} else {
					returnUrl =  "/xhtml/pm/category/categoryl2.xhtml";
					result = resDTO.isSuccessful();
					// resetCategory();
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_100201,
															new String[] {
																	ResourceBundleUtils
																			.getMessage(
																					"lbl.pm.sub.category",
																					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
																					new FacesScopes()
																							.getUserLocale()),
																	resDTO.getCategoryL2()
																			.getCategoryL2Code() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				}

			}

		} catch (Exception e) {
			throw(e);
		} finally {
			categoryCodeSO = null;
		}

		return returnUrl;

	}

	/**
	 * Tracking diary for create and update of sub category.
	 * 
	 * @param reqDTO
	 *            the req dto
	 */
	private void trackingDiaryForCreateAndUpdateOfSubCategory(
			CategoryCodeReqDTO reqDTO) {

		String[] msgParam = new String[] {
				this.getManagedBean().getPmCategoryL2().getCategoryL2Code(),
				this.getManagedBean().getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code() };

		String messageId = "";

		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		StatusReqDTO statusReqDTO = new StatusReqDTO();

		statusReqDTO.setStatusId(this.getManagedBean().getPmCategoryL2()
				.getRecordStatusFlg().longValue());
		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		System.out.println("statusResDTO.getPmStatus().getStatusId():"
				+ statusResDTO.getPmStatus().getStatusId());
		debugLogger("statusResDTO.getPmStatus().getStatusId():"
				+ statusResDTO.getPmStatus().getStatusId());

		if (this.getManagedBean().isNewCategory()) {

			messageId = MessageConst.TD_80704;
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
		} else {
			messageId = MessageConst.TD_80705;

			/*
			 * if(oldPmCategoryL2!=null && newPmCategoryL2!=null &&
			 * !oldPmCategoryL2 .getRecordStatusFlg().equals(newPmCategoryL2.
			 * getRecordStatusFlg ())){
			 * if(this.getManagedBean().getPmCategoryL2().getRecordStatusFlg
			 * ().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
			 * recordStatusMessageId = MessageConst.TD_80706; } else{
			 * recordStatusMessageId = MessageConst.TD_80707; }
			 * reqDTO.getTrackDairyVOs
			 * ().add(PmCommonUtil.assignTrackingDairyForPmCategoryL2
			 * (this.getManagedBean().getPmCategoryL2().getCategoryL2Id(),
			 * this.getManagedBean().getPmCategoryL2().getCategoryL2Code(),
			 * this.getManagedBean().getPmCategoryL2().getPmCategoryL1().
			 * getCategoryL1Id(), getPmActionDesc(recordStatusMessageId,msgParam
			 * ),null,null,ParameterConst.MAIN_CATEGORY_PARAM_CODE)); }
			 */
		}
		reqDTO.setTrackingStatus(true);

		reqDTO.getTrackDairyVOs().add(
				PmCommonUtil.assignTrackingDairyForPmCategoryL2(
						this.getManagedBean().getPmCategoryL2()
								.getCategoryL2Id(),
						this.getManagedBean().getPmCategoryL2()
								.getCategoryL2Code(),
						this.getManagedBean().getPmCategoryL2()
								.getPmCategoryL1().getCategoryL1Id(),
						statusResDTO.getPmStatus() != null ? statusResDTO
								.getPmStatus() : new PmStatus(),
						getPmActionDesc(messageId, msgParam),
						ParameterConst.SUB_CATEGORY_PARAM_CODE,
						ParameterConst.MAIN_CATEGORY_PARAM_CODE));
	}

	/**
	 * Pre render search.
	 */
	public void preRenderSearch() {
		resetSearchCriteriaResult();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/pm/category/search.xhtml");
	}

	/**
	 * Save sub division.
	 * 
	 * @return the string
	 * @throws Exception 
	 */
	public String saveSubDivision() throws Exception {

		ICategoryCodeSO categoryCodeSO;
		IParameterSO parameterSO;
		String returnUrl = null;

		// check the duplicate restriction
		if (!checkCategoryRestrDuplicateCompanyType()) {
			throw new EpException(MessageConst.ERROR_775006);
		}

		// validation for fields
		validatePmCategoryL3();

		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);

			this.getManagedBean()
					.getPmCategoryL3()
					.setCategoryName(
							this.getManagedBean().getPmCategoryL3()
									.getCategoryName().toUpperCase());
			this.getManagedBean()
					.getPmCategoryL3()
					.setBasicCategoryReq(
							this.getManagedBean().getPmCategoryL3()
									.isBasicCategoryReqValue() ? 1 : 0);
			this.getManagedBean()
					.getPmCategoryL3()
					.setHomeOperated(
							this.getManagedBean().getPmCategoryL3()
									.isHomeOperatedValue() ? 1 : 0);
			this.getManagedBean()
					.getPmCategoryL3()
					.setRecordStatusFlg(
							this.getManagedBean().getPmCategoryL3()
									.getRecordStatusFlg());

			if (this.getManagedBean().isNewCategory()) {
				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				// supplier type
				parameterReqDTO.setParameterId(this.getManagedBean()
						.getPmCategoryL3().getSupplierTypeCodeVal());
				ParameterResDTO parameterResDTO = parameterSO
						.retrieveParameterById(parameterReqDTO);
				this.getManagedBean().getPmCategoryL3()
						.setSupplierTypeCode(parameterResDTO.getPmParameter());

				// Special Category
				parameterReqDTO.setParameterId(this.getManagedBean()
						.getPmCategoryL3().getCategoryTypeCodeVal());
				parameterResDTO = parameterSO
						.retrieveParameterById(parameterReqDTO);
				this.getManagedBean().getPmCategoryL3()
						.setCategoryTypeCode(parameterResDTO.getPmParameter());
			}
			// Basic category and Category Restr data information
			getBasicCategoryData();
			getCategoryRestrData();

			reqDTO.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
			reqDTO.setCategoryLevel(this.getManagedBean().getCatLevel());
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());

			PmCategoryL3 pmCategoryL3 = this.getManagedBean().getPmCategoryL3();
			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusReqDTO statusReqDTO = new StatusReqDTO();

			StatusResDTO statusResDTO = trackingDiaryForCreateAndUpdateOfSubDivision(
					reqDTO, pmCategoryL3, statusSO, statusReqDTO);

			getAllCategoryDetailsForTracking(reqDTO, statusResDTO);
			CategoryCodeResDTO resDTO = categoryCodeSO.saveSubDivision(reqDTO);

			PmCategoryL3 oldCategoryL3 = (PmCategoryL3) BeanUtils
					.cloneBean(resDTO.getCategoryL3());
			this.getManagedBean().setOldCategoryL3(oldCategoryL3);

			if (resDTO.isSuccessful()) {
				reset();
				if (this.getManagedBean().isNewCategory()) {
					this.getManagedBean().setPmCategoryL3(
							resDTO.getCategoryL3());
					this.getManagedBean().getPmCategoryL2().getPmCategoryL3s()
							.add(this.getManagedBean().getPmCategoryL3());
					reqDTO.setPmCategoryL2(this.getManagedBean()
							.getPmCategoryL2());
					categoryCodeSO.saveSubCategory(reqDTO);
				}

				// reqDTO.setPmCategoryL1(this.getManagedBean().getPmCategoryL1());
				// categoryCodeSO.saveCategory(reqDTO);
				if (this.getManagedBean().isNewCategory()) {
					this.getManagedBean().setNewCategory(false);
					returnUrl = "/xhtml/pm/category/search.xhtml";
					result = resDTO.isSuccessful();
					// resetCategory();
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_100200,
															new String[] {
																	ResourceBundleUtils
																			.getMessage(
																					"lbl.pm.sub.div",
																					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
																					new FacesScopes()
																							.getUserLocale()),
																	this.getManagedBean()
																			.getPmCategoryL3()
																			.getCategoryL3Code() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				} else {
					loadPmCategoryL3Data(this.getManagedBean().getPmCategoryL3()
							.getCategoryL3Id());
					returnUrl =  "/xhtml/pm/category/categoryl3.xhtml";					
					result = resDTO.isSuccessful();
					// resetCategory();
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_100201,
															new String[] {
																	ResourceBundleUtils
																			.getMessage(
																					"lbl.pm.sub.div",
																					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
																					new FacesScopes()
																							.getUserLocale()),
																	this.getManagedBean()
																			.getPmCategoryL3()
																			.getCategoryL3Code() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				}
			}

		} catch (Exception e) {
			throw (e);
		} finally {
			categoryCodeSO = null;
			parameterSO = null;
		}
		return returnUrl;
	}

	/**
	 * Tracking diary for create and update of sub division.
	 * 
	 * @param reqDTO
	 *            the req dto
	 * @param pmCategoryL3
	 *            the pm category l3
	 * @param statusSO
	 *            the status so
	 * @param statusReqDTO
	 *            the status req dto
	 * @return the status res dto
	 */
	private StatusResDTO trackingDiaryForCreateAndUpdateOfSubDivision(
			CategoryCodeReqDTO reqDTO, PmCategoryL3 pmCategoryL3,
			IStatusSO statusSO, StatusReqDTO statusReqDTO) {
		String messageId;
		String[] msgParam = new String[] { pmCategoryL3.getCategoryL3Code(),
				this.getManagedBean().getPmCategoryL1().getCategoryL1Code(),
				this.getManagedBean().getPmCategoryL2().getCategoryL2Code() };

		if (this.getManagedBean().isNewCategory()) {

			messageId = MessageConst.TD_80708;
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
		} else {
			messageId = MessageConst.TD_80709;

		}

		statusReqDTO.setStatusId(this.getManagedBean().getPmCategoryL3()
				.getRecordStatusFlg().longValue());
		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		System.out.println("statusResDTO.getPmStatus().getStatusId():"
				+ statusResDTO.getPmStatus().getStatusId());
		debugLogger("statusResDTO.getPmStatus().getStatusId():"
				+ statusResDTO.getPmStatus().getStatusId());

		reqDTO.setTrackingStatus(true);

		reqDTO.getTrackDairyVOs().add(
				PmCommonUtil.assignTrackingDairyForPmCategoryL3(
						this.getManagedBean().getPmCategoryL3()
								.getCategoryL3Id(),
						this.getManagedBean().getPmCategoryL2()
								.getCategoryL2Id(),
						this.getManagedBean().getPmCategoryL3()
								.getCategoryL3Code(),
						statusResDTO.getPmStatus() != null ? statusResDTO
								.getPmStatus() : new PmStatus(),
						getPmActionDesc(messageId, msgParam),
						ParameterConst.SUB_DIVISION_CATEGORY_PARAM_CODE,
						ParameterConst.SUB_DIVISION_CATEGORY_PARAM_CODE));
		return statusResDTO;
	}

	/**
	 * Gets the category restr data.
	 * 
	 * @return the category restr data
	 */
	private void getCategoryRestrData() {

		List<PmCategoryRestr> pmCategoryRestrList = this.getManagedBean()
				.getPmCategoryL3().getPmCategoryRestrs();

		List<PmCategoryRestr> tobeAddedPmCategoryRestr = this.getManagedBean()
				.getTobeAddedCategoryRestrList();

		if (CollectionUtils.isNotEmpty(tobeAddedPmCategoryRestr)) {

			if (pmCategoryRestrList == null) {
				pmCategoryRestrList = new ArrayList<PmCategoryRestr>();
				pmCategoryRestrList.addAll(tobeAddedPmCategoryRestr);
				tobeAddedPmCategoryRestr.clear();
			} else {
				pmCategoryRestrList.addAll(tobeAddedPmCategoryRestr);
				tobeAddedPmCategoryRestr.clear();
			}
		}
	}

	/**
	 * Gets the machine category data.
	 * 
	 * @return the machine category data
	 */

	private void getMachineCategoryData() {

		List<PmMachineCategory> pmMachineCategoryList = this.getManagedBean()
				.getPmCategoryL3().getPmMachineCategory();

		List<PmMachineCategory> tobeAddedPmMachineCategory = this
				.getManagedBean().getTobeAddedPmMachineCategoryList();

		if (CollectionUtils.isNotEmpty(tobeAddedPmMachineCategory)) {

			if (pmMachineCategoryList == null) {
				pmMachineCategoryList = new ArrayList<PmMachineCategory>();
				pmMachineCategoryList.addAll(tobeAddedPmMachineCategory);
				tobeAddedPmMachineCategory.clear();
			} else {
				pmMachineCategoryList.addAll(tobeAddedPmMachineCategory);
				tobeAddedPmMachineCategory.clear();
			}
		} else if (CollectionUtils.isEmpty(this.getManagedBean()
				.getPmMachineCategoryList())
				&& CollectionUtils.isEmpty(tobeAddedPmMachineCategory)
				&& this.getManagedBean().isCheckSpecialCategory()) {

			throw new EpException(MessageConst.ERROR_775008 ,new String[]{ResourceBundleUtils.getMessage("lbl.pm.special.equipment",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}
	}

	/**
	 * Gets the basic category data.
	 * 
	 * @return the basic category data
	 */
	private void getBasicCategoryData() {

		List<PmBasicCategory> pmBasicCategoryList = this.getManagedBean()
				.getPmCategoryL3().getPmBasicCategory();

		List<PmBasicCategory> tobeAddedPmBasicCategory = this.getManagedBean()
				.getTobeAddedPmBasicCategoryList();

		if (CollectionUtils.isNotEmpty(tobeAddedPmBasicCategory)) {

			if (pmBasicCategoryList == null) {
				pmBasicCategoryList = new ArrayList<PmBasicCategory>();
				pmBasicCategoryList.addAll(tobeAddedPmBasicCategory);
				tobeAddedPmBasicCategory.clear();
			} else {
				pmBasicCategoryList.addAll(tobeAddedPmBasicCategory);
				tobeAddedPmBasicCategory.clear();
			}
		}
	}

	/**
	 * Gets the all category details for tracking.
	 * 
	 * @param reqDTO
	 *            the req dto
	 * @param statusResDTO
	 *            the status res dto
	 * @return the all category details for tracking
	 */
	private void getAllCategoryDetailsForTracking(CategoryCodeReqDTO reqDTO,
			StatusResDTO statusResDTO) {

		String[] commonMsgParam = new String[] {
				this.getManagedBean().getPmCategoryL1().getCategoryL1Code(),
				this.getManagedBean().getPmCategoryL2().getCategoryL2Code(),
				this.getManagedBean().getPmCategoryL3().getCategoryL3Code() };

		getBasicCategoryForTrackingDairy(reqDTO, statusResDTO, commonMsgParam);
		getMachineCategoryForTrackingDairy(reqDTO, statusResDTO, commonMsgParam);
		getCategoryRestrForTrackingDiary(reqDTO, statusResDTO, commonMsgParam);

	}

	/**
	 * Gets the category restr for tracking diary.
	 * 
	 * @param reqDTO
	 *            the req dto
	 * @param statusResDTO
	 *            the status res dto
	 * @param commonMsgParam
	 *            the common msg param
	 * @return the category restr for tracking diary
	 */
	private void getCategoryRestrForTrackingDiary(CategoryCodeReqDTO reqDTO,
			StatusResDTO statusResDTO, String[] commonMsgParam) {
		String statusMessageId;
		if (CollectionUtils.isNotEmpty(this.getManagedBean()
				.getCategoryRestrList())
				|| CollectionUtils.isEmpty(this.getManagedBean()
						.getCategoryRestrList())) {
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getDeletedPmPmCategoryRestrList())) {
				for (PmCategoryRestr catResstr : this.getManagedBean()
						.getDeletedPmPmCategoryRestrList()) {
					if (!this.getManagedBean().getCategoryRestrList()
							.contains(catResstr)) {

						statusMessageId = MessageConst.TD_80713;
						reqDTO.setTrackingStatus(true);
						trackingDairyData(reqDTO, statusResDTO,
								statusMessageId, commonMsgParam);
					}
				}
				this.getManagedBean().getDeletedPmPmCategoryRestrList().clear();
			}
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getCategoryRestrList())) {
				if (!this.getManagedBean().isNewCategory()) {
					for (PmCategoryRestr pmCategoryRestr : this
							.getManagedBean().getCategoryRestrList()) {
						if (pmCategoryRestr.isBrandNew()) {
							statusMessageId = MessageConst.TD_80712;
							reqDTO.setTrackingStatus(true);
							trackingDairyData(reqDTO, statusResDTO,
									statusMessageId, commonMsgParam);
						}
					}
				} else {
					for (PmCategoryRestr pmCategoryRestr : this
							.getManagedBean().getCategoryRestrList()) {
						if (pmCategoryRestr.isBrandNew()) {
							statusMessageId = MessageConst.TD_80712;
							reqDTO.setTrackingStatus(true);
							trackingDairyData(reqDTO, statusResDTO,
									statusMessageId, commonMsgParam);
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the machine category for tracking dairy.
	 * 
	 * @param reqDTO
	 *            the req dto
	 * @param statusResDTO
	 *            the status res dto
	 * @param commonMsgParam
	 *            the common msg param
	 * @return the machine category for tracking dairy
	 */
	private void getMachineCategoryForTrackingDairy(CategoryCodeReqDTO reqDTO,
			StatusResDTO statusResDTO, String[] commonMsgParam) {
		String statusMessageId;
		if (CollectionUtils.isNotEmpty(this.getManagedBean()
				.getPmMachineCategoryList())
				|| CollectionUtils.isEmpty(this.getManagedBean()
						.getPmMachineCategoryList())) {
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getDeletedPmMachineCategoryList())) {
				for (PmMachineCategory deleMachine : this.getManagedBean()
						.getDeletedPmMachineCategoryList()) {
					if (!this.getManagedBean().getPmMachineCategoryList()
							.contains(deleMachine)) {
						statusMessageId = MessageConst.TD_80715;
						reqDTO.setTrackingStatus(true);
						trackingDairyData(reqDTO, statusResDTO,
								statusMessageId, commonMsgParam);
					}
				}
				this.getManagedBean().getDeletedPmMachineCategoryList().clear();
			}
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getPmMachineCategoryList())) {
				if (!this.getManagedBean().isNewCategory()) {
					for (PmMachineCategory deleMachine : this.getManagedBean()
							.getPmMachineCategoryList()) {
						if (deleMachine.isBrandNew()) {
							statusMessageId = MessageConst.TD_80714;
							reqDTO.setTrackingStatus(true);
							trackingDairyData(reqDTO, statusResDTO,
									statusMessageId, commonMsgParam);
						}
					}
				} else {
					for (PmMachineCategory deleMachine : this.getManagedBean()
							.getPmMachineCategoryList()) {
						if (deleMachine.isBrandNew()) {
							statusMessageId = MessageConst.TD_80714;
							reqDTO.setTrackingStatus(true);
							trackingDairyData(reqDTO, statusResDTO,
									statusMessageId, commonMsgParam);
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the basic category for tracking dairy.
	 * 
	 * @param reqDTO
	 *            the req dto
	 * @param statusResDTO
	 *            the status res dto
	 * @param commonMsgParam
	 *            the common msg param
	 * @return the basic category for tracking dairy
	 */
	private void getBasicCategoryForTrackingDairy(CategoryCodeReqDTO reqDTO,
			StatusResDTO statusResDTO, String[] commonMsgParam) {
		String statusMessageId;
		if (CollectionUtils.isNotEmpty(this.getManagedBean()
				.getBasicCategoryList())
				|| CollectionUtils.isEmpty(this.getManagedBean()
						.getBasicCategoryList())) {
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getDeletedPmBasicCategoryList())) {
				for (PmBasicCategory deleBasic : this.getManagedBean()
						.getDeletedPmBasicCategoryList()) {
					if (!this.getManagedBean().getBasicCategoryList()
							.contains(deleBasic)) {

						statusMessageId = MessageConst.TD_80717;
						reqDTO.setTrackingStatus(true);
						trackingDairyData(reqDTO, statusResDTO,
								statusMessageId, commonMsgParam);
					}
				}
				this.getManagedBean().getDeletedPmBasicCategoryList().clear();
			}
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getBasicCategoryList())) {
				if (!this.getManagedBean().isNewCategory()) {
					for (PmBasicCategory pmBasicCategory : this
							.getManagedBean().getBasicCategoryList()) {
						if (pmBasicCategory.isBrandNew()) {
							statusMessageId = MessageConst.TD_80716;
							reqDTO.setTrackingStatus(true);
							trackingDairyData(reqDTO, statusResDTO,
									statusMessageId, commonMsgParam);
						}
					}
				} else {
					for (PmBasicCategory pmBasicCategory : this
							.getManagedBean().getBasicCategoryList()) {
						if (pmBasicCategory.isBrandNew()) {
							statusMessageId = MessageConst.TD_80716;
							reqDTO.setTrackingStatus(true);
							trackingDairyData(reqDTO, statusResDTO,
									statusMessageId, commonMsgParam);
						}
					}
				}
			}
		}
	}

	/**
	 * Tracking dairy data.
	 * 
	 * @param reqDTO
	 *            the req dto
	 * @param statusResDTO
	 *            the status res dto
	 * @param statusMessageId
	 *            the status message id
	 * @param commonMsgParam
	 *            the common msg param
	 */
	private void trackingDairyData(CategoryCodeReqDTO reqDTO,
			StatusResDTO statusResDTO, String statusMessageId,
			String[] commonMsgParam) {
		reqDTO.getTrackDairyVOs().add(
				PmCommonUtil.assignTrackingDairyForPmBasicCategory(
						this.getManagedBean().getPmCategoryL3()
								.getCategoryL3Id(),
						this.getManagedBean().getPmCategoryL2()
								.getCategoryL2Id(),
						this.getManagedBean().getPmCategoryL3()
								.getCategoryL3Code(),
						statusResDTO.getPmStatus() != null ? statusResDTO
								.getPmStatus() : new PmStatus(),
						getPmActionDesc(statusMessageId, commonMsgParam),
						ParameterConst.SUB_DIVISION_CATEGORY_PARAM_CODE,
						ParameterConst.SUB_CATEGORY_PARAM_CODE));
	}

	/**
	 * Delete basic category.
	 * 
	 * @param pmBasicCategory
	 *            the pm basic category
	 */
	public void deleteBasicCategory(PmBasicCategory pmBasicCategory) {
		log.info("In deleteBasicCategory");
		// ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO)
		// BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

		/*
		 * if(this.getManagedBean().getSelectedBasicCategory() != null &&
		 * !this.getManagedBean().getSelectedBasicCategory().isEmpty()) {
		 * log.info
		 * ("before this.getManagedBean().getSelectedBasicCategory().size() = "
		 * +this.getManagedBean().getSelectedBasicCategory().size());
		 * //CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		 * for(PmBasicCategory pmBasicCategory:
		 * this.getManagedBean().getSelectedBasicCategory()) {
		 */

		// reqDTO.setPmBasicCategory(pmBasicCategory);
		// categoryCodeSO.deleteBasicCategory(reqDTO);

		pmBasicCategory.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
		if (CollectionUtils.isEmpty(this.getManagedBean()
				.getDeletedpmBasicCategory())) {
			this.getManagedBean().setDeletedpmBasicCategory(
					new ArrayList<PmBasicCategory>());
		}
		this.getManagedBean().getDeletedpmBasicCategory().add(pmBasicCategory);

		if (CollectionUtils.isNotEmpty(this.getManagedBean()
				.getBasicCategoryList())) {
			this.getManagedBean().getBasicCategoryList()
					.remove(pmBasicCategory);
		}
		this.getManagedBean().getDeletedPmBasicCategoryList()
				.add(pmBasicCategory);

	}

	/**
	 * Delete pm machine.
	 * 
	 * @param pmMachineCategory
	 *            the pm machine category
	 */
	public void deletePmMachine(PmMachineCategory pmMachineCategory) {
		log.info("In deletePmMachine");
		// ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO)
		// BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

		/*
		 * if(this.getManagedBean().getSelectedpmMachineCategory() != null &&
		 * !this.getManagedBean().getSelectedpmMachineCategory().isEmpty()) {
		 * log.info(
		 * "before this.getManagedBean().getSelectedpmMachineCategory().size() = "
		 * +this.getManagedBean().getSelectedpmMachineCategory().size());
		 * //CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		 * for(PmMachineCategory pmMachineCategory:
		 * this.getManagedBean().getSelectedpmMachineCategory()) {
		 */

		// reqDTO.setPsmBasicCategory(pmBasicCategory);
		// categoryCodeSO.deleteBasicCategory(reqDTO);

		pmMachineCategory
				.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);

		if (CollectionUtils.isEmpty(this.getManagedBean()
				.getDeletedpmMachineCategory())) {
			this.getManagedBean().setDeletedpmMachineCategory(
					new ArrayList<PmMachineCategory>());
			this.getManagedBean().getDeletedpmMachineCategory()
					.add(pmMachineCategory);
		}

		if (CollectionUtils.isNotEmpty(this.getManagedBean()
				.getPmMachineCategoryList())) {

			this.getManagedBean().getPmMachineCategoryList()
					.remove(pmMachineCategory);

			/*
			 * if (CollectionUtils.isEmpty(this.getManagedBean()
			 * .getTobeAddedPmMachineCategoryList())) {
			 * 
			 * this.getManagedBean().setTobeAddedPmMachineCategoryList( new
			 * ArrayList<PmMachineCategory>());
			 * this.getManagedBean().getTobeAddedPmMachineCategoryList()
			 * .add(pmMachineCategory); } else {
			 * 
			 * this.getManagedBean().getTobeAddedPmMachineCategoryList()
			 * .add(pmMachineCategory); }
			 */
		}
		this.getManagedBean().getDeletedPmMachineCategoryList()
				.add(pmMachineCategory);

		// log.info("deletePmMachine this.getManagedBean().getSelectedpmMachineCategory().size() = "+this.getManagedBean().getSelectedpmMachineCategory().size());
	}

	/**
	 * Validate pm category l1.
	 * 	
	 */
	private void validatePmCategoryL1() {
		ICategoryCodeSO categoryCodeSO = null;
		
		try {
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);
			
			if (StringUtils.isBlank(this.getManagedBean().getPmCategoryL1().getCategoryL1Code())) {
				throw new EpException(MessageConst.ERROR_775009);
		
			}
			if (StringUtils.isBlank(this.getManagedBean().getPmCategoryL1().getCategoryName())) {
				throw new EpException(MessageConst.ERROR_775010);			
			}
			if (StringUtils.isNotBlank(this.getManagedBean().getPmCategoryL1().getCategoryL1Code())) {
				try {
					Integer.valueOf(this.getManagedBean().getPmCategoryL1()
							.getCategoryL1Code());
				} catch (NumberFormatException e) {
					throw new EpException(MessageConst.ERROR_100004,new String[] { ResourceBundleUtils
							.getMessage(
									"lbl.pm.category.code",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes()
											.getUserLocale()) });
				}

			}
			if (this.getManagedBean().isNewCategory()) {
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setMainCatCode(this.getManagedBean().getPmCategoryL1()
						.getCategoryL1Code());
				CategoryCodeResDTO resDTO = categoryCodeSO
						.retrieveCategoryL1ByCode(reqDTO);
				if (resDTO.getCategoryL1() != null) {
					throw new EpException(MessageConst.ERROR_775001);
				}

				CategoryCodeResDTO resDTODesc = retrieveCategoryL1ListByCatCodeDesc(categoryCodeSO);

				if (resDTODesc.getCategoryL1List() != null
						&& resDTODesc.getCategoryL1List().size() > 0 && this.getManagedBean().getPmCategoryL1()
						.getRecordStatusFlg().intValue() != DbValueConst.RECORD_STATUS_SUSPENDED) {
					for (PmCategoryL1 pmCategoryL1 : resDTODesc
							.getCategoryL1List()) {
						if (pmCategoryL1.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {
							throw new EpException(MessageConst.ERROR_770218,new String[] {
									PmCommonUtil.getResourceBundleLabels("lbl.pm.main.category"),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only"),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only")
							});
						}
					}
				}
			}
		}finally {
			categoryCodeSO = null;
		}
	}

	private CategoryCodeResDTO retrieveCategoryL1ListByCatCodeDesc(
			ICategoryCodeSO categoryCodeSO) {
		CategoryCodeReqDTO reqDTOUpdate = new CategoryCodeReqDTO();
		reqDTOUpdate.setMainCatDesc(this.getManagedBean().getPmCategoryL1()
				.getCategoryName());
		reqDTOUpdate.setRecordStatusFlg(this.getManagedBean().getPmCategoryL1()
				.getRecordStatusFlg());
		CategoryCodeResDTO resDTODesc = categoryCodeSO
				.retrieveCategoryL1ListByCodeDesc(reqDTOUpdate);
		return resDTODesc;
	}

	/**
	 * Validate pm category l2.
	 * 
	 * 
	 */
	private void validatePmCategoryL2() {
		ICategoryCodeSO categoryCodeSO = null;
		try {
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);
			
			if (StringUtils.isBlank(this.getManagedBean().getPmCategoryL2().getCategoryL2Code())) {
				throw new EpException(MessageConst.ERROR_775009);			
			}
			if (StringUtils.isBlank(this.getManagedBean().getPmCategoryL2().getCategoryName())) {
				throw new EpException(MessageConst.ERROR_775010);			
			}
			if (StringUtils.isNotBlank(this.getManagedBean().getPmCategoryL2().getCategoryL2Code())) {
				try {
					Integer.valueOf(this.getManagedBean().getPmCategoryL2()
							.getCategoryL2Code());
				} catch (NumberFormatException e) {
					throw new EpException(MessageConst.ERROR_100004,new String[] { ResourceBundleUtils
							.getMessage(
									"lbl.pm.category.code",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes()
											.getUserLocale()) });
				}

			}
			if (this.getManagedBean().isNewCategory()) {
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setCategoryL1Id(this.getManagedBean().getPmCategoryL1()
						.getCategoryL1Id());
				reqDTO.setSubCatCode(this.getManagedBean().getPmCategoryL2()
						.getCategoryL2Code());
				CategoryCodeResDTO resDTO = categoryCodeSO
						.retrieveCategoryL2ListByCode(reqDTO);
				if (resDTO.getCategoryL2List() != null
						&& resDTO.getCategoryL2List().size() > 0) {
					throw new EpException(MessageConst.ERROR_775002);
				}

				CategoryCodeResDTO resDTODesc = retrieveCategoryL2ListByCatCodeDesc(categoryCodeSO);

				if (resDTODesc.getCategoryL2List() != null
						&& resDTODesc.getCategoryL2List().size() > 0) {
					for (PmCategoryL2 pmCategoryL2 : resDTODesc
							.getCategoryL2List()) {
						if (pmCategoryL2.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
							throw new EpException(MessageConst.ERROR_770219,new String[] { 
									PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.category"),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only"),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.main.category.desc"),
									this.getManagedBean().getPmCategoryL1().getCategoryName(),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only")
							});
						}
					}
				}
			} 
		} finally {
			categoryCodeSO = null;
		}
	}

	private CategoryCodeResDTO retrieveCategoryL2ListByCatCodeDesc(
			ICategoryCodeSO categoryCodeSO) {
		CategoryCodeReqDTO reqDTOUpdate = new CategoryCodeReqDTO();
		reqDTOUpdate.setCategoryL1Id(this.getManagedBean().getPmCategoryL1()
				.getCategoryL1Id());
		reqDTOUpdate.setCategoryL2Id(this.getManagedBean().getPmCategoryL2()
				.getCategoryL2Id());
		reqDTOUpdate.setSubCatDesc(this.getManagedBean().getPmCategoryL2()
				.getCategoryName());
		reqDTOUpdate.setRecordStatusFlg(this.getManagedBean().getPmCategoryL2()
				.getRecordStatusFlg());
		CategoryCodeResDTO resDTODescUpdate = categoryCodeSO
				.retrieveCategoryL2ListByCodeDesc(reqDTOUpdate);
		return resDTODescUpdate;
	}

	/**
	 * Validate pm category l3.
	 * 
	 * 
	 */
	private void validatePmCategoryL3() {
		ICategoryCodeSO categoryCodeSO = null;
		
		try {
			if (this.getManagedBean().getPmCategoryL3().getPaidUpCapital() == null) {
				throw new EpException(MessageConst.ERROR_775013);
			}

//		if (this.getManagedBean().getPmCategoryL3().getPaidUpCapital() != null) {
//			try {
////				Integer.valueOf(this.getManagedBean().getPmCategoryL3().getPaidUpCapital());
//			} catch (NumberFormatException e) {
//				FacesContext
//						.getCurrentInstance()
//						.addMessage(
//								null,
//								new FacesMessage(
//										FacesMessage.SEVERITY_ERROR,
//										null,
//										MessageUtils
//												.getMessage(
//														MessageConst.ERROR_100003,
//														new String[] { ResourceBundleUtils
//																.getMessage(
//																		"lbl.pm.paid.capital",
//																		ResourceBundleUtils.BUNDLE_COMMON_LABELS,
//																		new FacesScopes()
//																				.getUserLocale()) },
//														MessageUtils.BUNDLE_COMMON_MESSAGES,
//														new FacesScopes()
//																.getUserLocale())));
//				return false;
//			}
//		}

			if (StringUtils.isBlank(this.getManagedBean().getPmCategoryL3().getCategoryL3Code())) {
				throw new EpException(MessageConst.ERROR_775009);
			}
	
			if (this.getManagedBean().isNewCategory()) {
				if (StringUtils.isNotBlank(this.getManagedBean().getPmCategoryL3().getCategoryL3Code())) {
					try {
						Integer.valueOf(this.getManagedBean().getPmCategoryL3()
								.getCategoryL3Code());
					} catch (NumberFormatException e) {
						throw new EpException(MessageConst.ERROR_100004,
								new String[] { ResourceBundleUtils
								.getMessage(
										"lbl.pm.category.code",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes()
												.getUserLocale()) });
					}
				}
			}

			if (this.getManagedBean().isNewCategory()
					&& this.getManagedBean().getPmCategoryL3()
							.getCategoryTypeCodeVal() == 0) {
				throw new EpException(MessageConst.ERROR_775012);
			}
			if (!this.getManagedBean().isNewCategory()
					&& (this.getManagedBean().getPmCategoryL3()
							.getCategoryTypeCode() == null || this.getManagedBean()
							.getPmCategoryL3().getCategoryTypeCode()
							.getParameterId() == 0)) {
				throw new EpException(MessageConst.ERROR_775012);				
			}

			if (this.getManagedBean().getPmCategoryL3().getCategoryName() == null
					|| this.getManagedBean().getPmCategoryL3().getCategoryName()
							.isEmpty()) {
				throw new EpException(MessageConst.ERROR_775010);
			}

			if (this.getManagedBean().isNewCategory()
					&& this.getManagedBean().getPmCategoryL3()
							.getSupplierTypeCodeVal() == 0) {
				throw new EpException(MessageConst.ERROR_775011);				
			}
			if (!this.getManagedBean().isNewCategory()
					&& (this.getManagedBean().getPmCategoryL3()
							.getSupplierTypeCode() == null || this.getManagedBean()
							.getPmCategoryL3().getSupplierTypeCode()
							.getParameterId() == 0)) {
				throw new EpException(MessageConst.ERROR_775011);				
			}

			// disabled the bumipercentage: by andrew
			/*
			 * if(this.getManagedBean().getPmCategoryL3().getBumiPercentage()==null
			 * || this.getManagedBean().getPmCategoryL3().getBumiPercentage()==0){
			 * FacesContext.getCurrentInstance().addMessage(null, new
			 * FacesMessage(FacesMessage.SEVERITY_ERROR, null,
			 * MessageUtils.getMessage( MessageConst.ERROR_715008,
			 * MessageUtils.BUNDLE_COMMON_MESSAGES, new
			 * FacesScopes().getUserLocale()))); return false; }
			 */
			if (this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue()
					&& (this.getManagedBean().getBasicCategoryList() == null || this
							.getManagedBean().getBasicCategoryList().size() == 0)) {
				throw new EpException(MessageConst.ERROR_775014,
						new String[]{ResourceBundleUtils.getMessage("lbl.pm.category.grouping",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}

		// machine category data information
		getMachineCategoryData();

		if (this.getManagedBean().getPmCategoryL3().isByCompType()
				&& (this.getManagedBean().getCategoryRestrList() == null || this
						.getManagedBean().getCategoryRestrList().size() == 0)) {
			throw new EpException(MessageConst.ERROR_775015,new String[]{ResourceBundleUtils.getMessage("lbl.pm.company.type.tab",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}

	
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);
			if (this.getManagedBean().isNewCategory()) {
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setCategoryL2Id(this.getManagedBean().getPmCategoryL3()
						.getPmCategoryL2().getCategoryL2Id());
				reqDTO.setSubDivCode(this.getManagedBean().getPmCategoryL3()
						.getCategoryL3Code());
				CategoryCodeResDTO resDTO = categoryCodeSO
						.retrieveCategoryL3ListByCode(reqDTO);
				if (resDTO.getCategoryL3List() != null
						&& resDTO.getCategoryL3List().size() > 0) {
					throw new EpException(MessageConst.ERROR_775003);
				}

				CategoryCodeResDTO resDTODesc = retrieveCategoryL3ListByCatCodeDesc(categoryCodeSO);
				if (resDTODesc.getCategoryL3List() != null
						&& resDTODesc.getCategoryL3List().size() > 0) {
					for (PmCategoryL3 pmCategoryL3 : resDTODesc
							.getCategoryL3List()) {
						if (pmCategoryL3.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
							throw new EpException(MessageConst.ERROR_770219,new String[] { 
									PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.div"),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only"),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.category.desc"),
									this.getManagedBean().getPmCategoryL2().getCategoryName(),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only")
							});
						}
					}
				}
			} else if(isCategoryNameChanged()){
				CategoryCodeResDTO resDTOUpdate = retrieveCategoryL3ListByCatCodeDesc(categoryCodeSO);
				if (resDTOUpdate.getCategoryL3List() != null
						&& resDTOUpdate.getCategoryL3List().size() > 0
						&& this.getManagedBean().getPmCategoryL3()
								.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_SUSPENDED) {
					for (PmCategoryL3 pmCategoryL3 : resDTOUpdate
							.getCategoryL3List()) {
						if (pmCategoryL3.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
							throw new EpException(MessageConst.ERROR_770219,new String[] { 
									PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.div"),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only"),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.category.desc"),
									this.getManagedBean().getPmCategoryL2().getCategoryName(),
									PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only")
							});
						}
					}
				}
			}

		} finally {
			categoryCodeSO = null;
		}
	}

	private CategoryCodeResDTO retrieveCategoryL3ListByCatCodeDesc(
			ICategoryCodeSO categoryCodeSO) {
		CategoryCodeReqDTO reqDTODesc = new CategoryCodeReqDTO();
		reqDTODesc.setCategoryL2Id(this.getManagedBean().getPmCategoryL3()
				.getPmCategoryL2().getCategoryL2Id());
		reqDTODesc.setCategoryL1Id(this.getManagedBean().getPmCategoryL3()
				.getPmCategoryL2().getPmCategoryL1().getCategoryL1Id());
		reqDTODesc.setSubCatDesc(this.getManagedBean().getPmCategoryL3()
				.getCategoryName());
		CategoryCodeResDTO resDTODesc = categoryCodeSO
				.retrieveCategoryL3ListByCodeDesc(reqDTODesc);
		return resDTODesc;
	}

	/*
	 * public PmCategoryL3 getPmCategoryL3(Long id){ ICategoryCodeSO
	 * categoryCodeSO; try{ CategoryCodeReqDTO reqDTO = new
	 * CategoryCodeReqDTO(); categoryCodeSO = (ICategoryCodeSO)
	 * BusinessDelegate.
	 * getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT
	 * ,ICategoryCodeSO.class);
	 * 
	 * reqDTO.setCategoryL3Id(id); CategoryCodeResDTO resDTO =
	 * categoryCodeSO.retrieveCategoryL3DtlById(reqDTO);
	 * 
	 * if (resDTO.isSuccessful()) { return resDTO.getCategoryL3(); }
	 * }catch(Exception e){ e.printStackTrace(); }finally { categoryCodeSO =
	 * null; }
	 * 
	 * return null; }
	 */

	/**
	 * Populate to save lov.
	 */
	public void populateToSaveLOV() {
		log.info("In populateToSaveLOV");
		List<PmBasicCategory> tempPmBasicCategoryList = new ArrayList<PmBasicCategory>();
		Set<PmBasicCategory> pmPmBasicCategorySet = null;
		Set<PmBasicCategory> tobeAddedPmBasicCategorySet = null;
		ICategoryCodeSO categoryCodeSO;

		categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICategoryCodeSO.class);

		if (this.getManagedBean().getSelectedLOV() != null
				&& !this.getManagedBean().getSelectedLOV().isEmpty()) {

			if (this.getManagedBean().getPmCategoryL3() != null
					&& this.getManagedBean().getPmCategoryL3()
							.getPmBasicCategory() == null) {
				this.getManagedBean().getPmCategoryL3()
						.setPmBasicCategory(new ArrayList<PmBasicCategory>());
			}
			for (PmCategoryL3 pmCategoryL3 : this.getManagedBean()
					.getSelectedLOV()) {

				PmBasicCategory pmBasicCategory = new PmBasicCategory();

				pmBasicCategory.setPmCategoryL3(this.getManagedBean()
						.getPmCategoryL3());
				pmBasicCategory.setBasicCategoryL3(pmCategoryL3
						.getCategoryL3Id());
				pmBasicCategory.setBasicCategoryL2(pmCategoryL3
						.getPmCategoryL2().getCategoryL2Id());
				pmBasicCategory.setBasicCategoryL1(pmCategoryL3
						.getPmCategoryL2().getPmCategoryL1().getCategoryL1Id());
				pmBasicCategory.setCreatedDate(new Timestamp(new Date()
						.getTime()));

				// for table display
				pmBasicCategory.setBasicCategoryL1desc(pmCategoryL3
						.getPmCategoryL2().getPmCategoryL1().getCategoryName());
				pmBasicCategory.setBasicCategoryL2desc(pmCategoryL3
						.getPmCategoryL2().getCategoryName());
				pmBasicCategory.setBasicCategoryL3desc(pmCategoryL3
						.getCategoryName());
				pmBasicCategory.setCodeConcatenate(pmCategoryL3
						.getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code()
						+ pmCategoryL3.getPmCategoryL2().getCategoryL2Code()
						+ pmCategoryL3.getCategoryL3Code());

				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
				reqDTO.setCategoryL1Id(pmBasicCategory.getBasicCategoryL1());
				reqDTO.setCategoryL2Id(pmBasicCategory.getBasicCategoryL2());
				reqDTO.setCategoryL3Id(pmBasicCategory.getBasicCategoryL3());

				CategoryCodeResDTO resDTO = categoryCodeSO
						.retrievePmBasicCategoryByIds(reqDTO);

				if (resDTO.getPmBasicCategory() != null) {
					pmBasicCategory = resDTO.getPmBasicCategory();

					if (pmBasicCategory.getRecordStatusFlg().equals(
							DbValueConst.RECORD_STATUS_DELETED)) {

						pmBasicCategory
								.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
						pmBasicCategory.setBasicCategoryL1desc(pmCategoryL3
								.getPmCategoryL2().getPmCategoryL1()
								.getCategoryName());
						pmBasicCategory.setBasicCategoryL2desc(pmCategoryL3
								.getPmCategoryL2().getCategoryName());
						pmBasicCategory.setBasicCategoryL3desc(pmCategoryL3
								.getCategoryName());
						pmBasicCategory.setCodeConcatenate(pmCategoryL3
								.getPmCategoryL2().getPmCategoryL1()
								.getCategoryL1Code()
								+ pmCategoryL3.getPmCategoryL2()
										.getCategoryL2Code()
								+ pmCategoryL3.getCategoryL3Code());
						pmBasicCategory.setBrandNew(true);
					} else {
						pmBasicCategory.setBasicCategoryL1desc(pmCategoryL3
								.getPmCategoryL2().getPmCategoryL1()
								.getCategoryName());
						pmBasicCategory.setBasicCategoryL2desc(pmCategoryL3
								.getPmCategoryL2().getCategoryName());
						pmBasicCategory.setBasicCategoryL3desc(pmCategoryL3
								.getCategoryName());
						pmBasicCategory.setCodeConcatenate(pmCategoryL3
								.getPmCategoryL2().getPmCategoryL1()
								.getCategoryL1Code()
								+ pmCategoryL3.getPmCategoryL2()
										.getCategoryL2Code()
								+ pmCategoryL3.getCategoryL3Code());
						pmBasicCategory.setBrandNew(true);
					}
				}
				if (CollectionUtils.isNotEmpty(this.getManagedBean()
						.getBasicCategoryList())) {
					for (PmBasicCategory showPmBasicCategory : this
							.getManagedBean().getBasicCategoryList()) {
						if (showPmBasicCategory.getBasicCategoryL3().equals(
								pmBasicCategory.getBasicCategoryL3())) {
							resetLOV();
							throw new EpException(MessageConst.ERROR_770165);
						}
						if (!tempPmBasicCategoryList.contains(pmBasicCategory)) {
							tempPmBasicCategoryList.add(pmBasicCategory);
						}
					}
					this.getManagedBean().getBasicCategoryList()
							.addAll(tempPmBasicCategoryList);
					pmPmBasicCategorySet = new LinkedHashSet<PmBasicCategory>(
							this.getManagedBean().getBasicCategoryList());
					this.getManagedBean().getBasicCategoryList().clear();
					this.getManagedBean().getBasicCategoryList()
							.addAll(pmPmBasicCategorySet);
				} else {
					this.getManagedBean().setBasicCategoryList(
							(new ArrayList<PmBasicCategory>()));
					tempPmBasicCategoryList.add(pmBasicCategory);
					this.getManagedBean().getBasicCategoryList()
							.addAll(tempPmBasicCategoryList);
				}
				if (CollectionUtils.isEmpty(this.getManagedBean()
						.getTobeAddedPmBasicCategoryList())) {
					this.getManagedBean().setTobeAddedPmBasicCategoryList(
							new ArrayList<PmBasicCategory>());
					this.getManagedBean().getTobeAddedPmBasicCategoryList()
							.addAll(tempPmBasicCategoryList);
				} else {
					this.getManagedBean().getTobeAddedPmBasicCategoryList()
							.addAll(tempPmBasicCategoryList);
					tobeAddedPmBasicCategorySet = new LinkedHashSet<PmBasicCategory>(
							this.getManagedBean()
									.getTobeAddedPmBasicCategoryList());
					this.getManagedBean().getTobeAddedPmBasicCategoryList()
							.clear();
					this.getManagedBean().getTobeAddedPmBasicCategoryList()
							.addAll(tobeAddedPmBasicCategorySet);
				}

			}
			tempPmBasicCategoryList.clear();
		}
		resetLOV();
		this.getManagedBean().setBasicCategoryRequired(true);
	}

	/**
	 * Populate to save pm machine lov.
	 */
	public void populateToSavePmMachineLOV() {

		log.info("In populateToSavePmMachineLOV");
		List<PmMachineCategory> tempPmMachineCategoryList = new ArrayList<PmMachineCategory>();

		CategoryCodeResDTO resDTO = new CategoryCodeResDTO();

		HashSet<PmMachineCategory> pmMachineCategorieSet = null;

		HashSet<PmMachineCategory> tobeAddedPmMachineCategorieSet = null;

		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						ICategoryCodeSO.class);

		if (this.getManagedBean().getSelectedpmMachine() != null
				&& !this.getManagedBean().getSelectedpmMachine().isEmpty()) {

			if (this.getManagedBean().getPmCategoryL3().getPmMachineCategory() == null) {

				this.getManagedBean()
						.getPmCategoryL3()
						.setPmMachineCategory(
								new ArrayList<PmMachineCategory>());
			}

			for (PmMachine pmMachine : this.getManagedBean()
					.getSelectedpmMachine()) {

				PmMachineCategory pmMachineCategory = new PmMachineCategory();
				pmMachineCategory.setPmCategoryL3(this.getManagedBean()
						.getPmCategoryL3());

				pmMachineCategory.setCreatedDate(new Timestamp(new Date()
						.getTime()));
				pmMachineCategory.setCategoryL1id(this.getManagedBean()
						.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Id());
				pmMachineCategory.setCategoryL2id(this.getManagedBean()
						.getPmCategoryL3().getPmCategoryL2().getCategoryL2Id());
				pmMachineCategory.setPmMachine(pmMachine);
				pmMachineCategory.setDisplaySeq(0);

				// Check if the pmMachineCategory already exists in DB with
				// categoryL1id, categoryL2id, categoryL3id, machineId
				// If an object is return then add that object to below machine
				// category list

				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setPmMachineId(pmMachine.getMachineId());
				reqDTO.setCategoryL1Id(pmMachineCategory.getCategoryL1id());
				reqDTO.setCategoryL2Id(pmMachineCategory.getCategoryL2id());
				reqDTO.setCategoryL3Id(pmMachineCategory.getPmCategoryL3()
						.getCategoryL3Id());

				resDTO = categoryCodeSO.retrievePmMachineByCategoryIds(reqDTO);

				if (resDTO.getPmMachine() != null) {
					pmMachineCategory = resDTO.getPmMachineCategory();
					pmMachineCategory
							.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					pmMachineCategory.setBrandNew(true);
					pmMachineCategory.setDisplaySeq(0);

				}

				if (CollectionUtils.isEmpty(this.getManagedBean()
						.getPmMachineCategoryList())) {
					this.getManagedBean().setPmMachineCategoryList(
							new ArrayList<PmMachineCategory>());
					tempPmMachineCategoryList.add(pmMachineCategory);
					this.getManagedBean().getPmMachineCategoryList()
							.addAll(tempPmMachineCategoryList);
				} else {
					for (PmMachineCategory showPmMachineCategory : this
							.getManagedBean().getPmMachineCategoryList()) {
						if (pmMachineCategory
								.getPmMachine()
								.getMachineCode()
								.equals(showPmMachineCategory.getPmMachine()
										.getMachineCode())) {
							resetPmMachineLOV();
							throw new EpException(MessageConst.ERROR_770164);
						}
						if (!tempPmMachineCategoryList
								.contains(pmMachineCategory)) {
							tempPmMachineCategoryList.add(pmMachineCategory);
						}
					}

					this.getManagedBean().getPmMachineCategoryList()
							.addAll(tempPmMachineCategoryList);
					pmMachineCategorieSet = new LinkedHashSet<PmMachineCategory>(
							this.getManagedBean().getPmMachineCategoryList());
					this.getManagedBean().getPmMachineCategoryList().clear();
					this.getManagedBean().getPmMachineCategoryList()
							.addAll(pmMachineCategorieSet);

				}

				if (CollectionUtils.isEmpty(this.getManagedBean()
						.getTobeAddedPmMachineCategoryList())) {
					this.getManagedBean().setTobeAddedPmMachineCategoryList(
							new ArrayList<PmMachineCategory>());
					this.getManagedBean().getTobeAddedPmMachineCategoryList()
							.addAll(tempPmMachineCategoryList);
				} else {
					this.getManagedBean().getTobeAddedPmMachineCategoryList()
							.addAll(tempPmMachineCategoryList);
					tobeAddedPmMachineCategorieSet = new LinkedHashSet<PmMachineCategory>(
							this.getManagedBean()
									.getTobeAddedPmMachineCategoryList());
					this.getManagedBean().getTobeAddedPmMachineCategoryList()
							.clear();
					this.getManagedBean().getTobeAddedPmMachineCategoryList()
							.addAll(tobeAddedPmMachineCategorieSet);
				}
			}
			tempPmMachineCategoryList.clear();
		}
		resetPmMachineLOV();

	}

	/**
	 * Reload selection pm category l3.
	 */
	public void reloadSelectionPmCategoryL3() {
		IParameterSO parameterSO;
		try {

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			parameterSO = (IParameterSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale()
					.getLanguage());

			ISearchBean<PmParameterDesc> searchBean = reqDTO
					.getParameterDescSearchBean();

			if (searchBean == null) {
				searchBean = new SearchBean<PmParameterDesc>();
			}

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), "O"));

			reqDTO.setParameterDescSearchBean(searchBean);
			reqDTO.setParameterType("ST");
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterDescListByType(reqDTO);
			this.getManagedBean()
					.setSupplierType(resDTO.getParameterDescList());

			reqDTO.setParameterType("CAT");
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), "-"));

			resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);

			this.getManagedBean().setSpecialCategory(
					resDTO.getParameterDescList());

			// default value for Special Category
			if (this.getManagedBean().isNewCategory()) {
				reqDTO.setParameterType("CAT");
				reqDTO.setParameterCode("-");
				resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				this.getManagedBean()
						.getPmCategoryL3()
						.setCategoryTypeCodeVal(
								resDTO.getPmParameter().getParameterId());
			}

			// default value for Special Group
			if (this.getManagedBean().getPmCategoryL3()
					.isBasicCategoryReqValue()) {
				this.getManagedBean().setDisplayCategoryGrouping(true);
			} else {
				this.getManagedBean().setDisplayCategoryGrouping(false);
			}

			this.getManagedBean().setSelectedBasicCategory(
					new ArrayList<PmBasicCategory>());
			this.getManagedBean().setSelectedpmMachineCategory(
					new ArrayList<PmMachineCategory>());
			this.getManagedBean().setDeletedpmMachineCategory(
					new ArrayList<PmMachineCategory>());
			this.getManagedBean().setSelectedPmCategoryRestr(
					new ArrayList<PmCategoryRestr>());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			parameterSO = null;
		}
	}

	/**
	 * Reset category.
	 */
	public void resetCategory() {
		resetSearchCriteriaResult();
		searchCategory.reloadData();
	}

	/**
	 * Reset lov.
	 */
	public void resetLOV() {
		getManagedBean().setSearchValue(new String[3]);
		getManagedBean().setSearchFieldValue(new String[3]);
		getManagedBean().setSearchOperatorValue(new String[3]);
		this.getSearchLOVPagingDataModel().resetData();
		this.getManagedBean().setSelectedLOV(new ArrayList<PmCategoryL3>());
	}

	/**
	 * Reset pm machine lov.
	 */
	public void resetPmMachineLOV() {
		getManagedBean().setSearchValue(new String[3]);
		getManagedBean().setSearchFieldValue(new String[3]);
		getManagedBean().setSearchOperatorValue(new String[3]);
		this.getSearchLOVMachinePagingDataModel().resetData();
		this.getManagedBean().setSelectedpmMachine(new ArrayList<PmMachine>());
	}

	/**
	 * Update category level.
	 */
	public void updateCategoryLevel() {
		log.info("getManagedBean().getPmCategoryLevel() "
				+ getManagedBean().getPmCategoryLevel());
	}

	/**
	 * Update special group.
	 */
	public void updateSpecialGroup() {
		log.info("getManagedBean().updateSpecialGroup() ");
		if (this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue()) {
			this.getManagedBean().setDisplayCategoryGrouping(true);
		} else {
			this.getManagedBean().setDisplayCategoryGrouping(false);
		}

		if (this.getManagedBean().getPmCategoryL3().isBrandNew()) {
			if (this.getManagedBean().getPmCategoryL3()
					.isBasicCategoryReqValue()
					|| this.getManagedBean().isBasicCategoryRequired()) {
				if (CollectionUtils.isNotEmpty(this.getManagedBean()
						.getBasicCategoryList())) {
					this.getManagedBean().getBasicCategoryList().clear();

				}
			}
		}

	}

	/**
	 * Update special category.
	 */
	/*
	 * public void updateSpecialCategory() {
	 * 
	 * List<PmParameterDesc> paramDescList = this.getManagedBean()
	 * .getSpecialCategory(); PmParameter pmParameter =
	 * this.getManagedBean().getPmCategoryL3() .getCategoryTypeCode();
	 * 
	 * if (paramDescList != null && !paramDescList.isEmpty()) {
	 * 
	 * for (PmParameterDesc PmParameterDesc : paramDescList) {
	 * 
	 * if (!this.getManagedBean().isNewCategory()) { if
	 * (pmParameter.getParameterId().equals(
	 * PmParameterDesc.getPmParameter().getParameterId())) {
	 * 
	 * if (PmParameterDesc .getPmParameter() .getParameterCode()
	 * .equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_PRINTING)) {
	 * 
	 * this.getManagedBean() .setCheckSpecialCategory(true);
	 * 
	 * } else { this.getManagedBean().setCheckSpecialCategory(false); } } } else
	 * { if (PmParameterDesc .getPmParameter() .getParameterCode()
	 * .equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_PRINTING)) {
	 * 
	 * this.getManagedBean().setCheckSpecialCategory(false);
	 * 
	 * } else { this.getManagedBean().setCheckSpecialCategory(true); } } } } }
	 */

	/**
	 * Update special category.
	 */
	public void updateSpecialCategory() {

		List<PmParameterDesc> paramDescList = this.getManagedBean()
				.getSpecialCategory();

		PmCategoryL3 categoryL3 = this.getManagedBean().getPmCategoryL3();

		Long parameterId = 0L;

		this.getManagedBean().setCheckSpecialCategory(false);
		/*
		 * parameterId = categoryL3.getCategoryTypeCodeVal() != 0 ? categoryL3
		 * .getCategoryTypeCodeVal() : categoryL3
		 * .getCategoryTypeCode().getParameterId();
		 */

		if (categoryL3.getCategoryTypeCodeVal() != 0) {

			parameterId = categoryL3.getCategoryTypeCodeVal();

		} else if (categoryL3.getCategoryTypeCode() != null) {

			parameterId = categoryL3.getCategoryTypeCode().getParameterId();
		}

		if (parameterId != 0) {
			if (paramDescList != null && !paramDescList.isEmpty()) {
				for (PmParameterDesc PmParameterDesc : paramDescList) {

					PmParameter pmParameter = PmParameterDesc.getPmParameter();

					if (pmParameter.getParameterId().equals(parameterId)) {
						String parameterCode = pmParameter.getParameterCode();

						if (parameterCode
								.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_PRINTING)) {
							if (!this.getManagedBean().isNewCategory()) {
								this.getManagedBean().setCheckSpecialCategory(
										true);
							} else {
								this.getManagedBean().setCheckSpecialCategory(
										true);
							}
						}
						
						if (parameterCode
								.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_NORMAL)) {
							
								this.getManagedBean().setFacilityTypeChoosable(false);
							
						}else{
							
							this.getManagedBean().setFacilityTypeChoosable(true);
						}
					}
				}
			}
		}
	}

	/**
	 * Update machine.
	 */
	public void updateMachine() {
		if (this.getManagedBean().getPmMachine() != null) {
			log.info("this.getManagedBean().getPmMachine().size() = "
					+ this.getManagedBean().getPmMachine().size());

			ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);

			for (PmMachine pmMachine : this.getManagedBean().getPmMachine()) {
				PmMachineCategory pmMachineCategory = new PmMachineCategory();
				pmMachineCategory.setPmCategoryL3(this.getManagedBean()
						.getPmCategoryL3());

				// parameter
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setPmMachineId(pmMachine.getMachineId());
				CategoryCodeResDTO resDTO = categoryCodeSO
						.retrievePmMachine(reqDTO);
				// pmMachineCategory.setCategoryTypeCode(resDTO.getPmMachine().getCategoryTypeCode());

				// log.info("resDTO.getPmMachine().getCategoryTypeCode().getParameterId() "+resDTO.getPmMachine().getCategoryTypeCode().getParameterId());

				pmMachineCategory.setCreatedDate(new Timestamp(new Date()
						.getTime()));
				pmMachineCategory.setCategoryL1id(this.getManagedBean()
						.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Id());
				pmMachineCategory.setCategoryL2id(this.getManagedBean()
						.getPmCategoryL3().getPmCategoryL2().getCategoryL2Id());
				pmMachineCategory.setPmMachine(resDTO.getPmMachine());

				this.getManagedBean().getPmCategoryL3().getPmMachineCategory()
						.add(pmMachineCategory);

				log.info("this.getManagedBean().getPmCategoryL3().getPmMachineCategory().size() "
						+ this.getManagedBean().getPmCategoryL3()
								.getPmMachineCategory().size());
			}
		}
	}

	/**
	 * Search.
	 */
	public void search() {
		searchCategory.reloadData();
	}

	/**
	 * Search lov.
	 */
	public void searchLOV() {

		searchLOVPagingDataModel.reloadData();

	}

	/**
	 * Search pm machine lov.
	 */
	public void searchPmMachineLOV() {

		searchLOVMachinePagingDataModel.reloadData();

	}

	/**
	 * Back to search.
	 * 
	 * @return the string
	 */
	public String backToSearch() {
		// resetCategory();
		return "/xhtml/pm/category/search.xhtml";
	}

	/**
	 * Gets the search category.
	 * 
	 * @return the search category
	 */
	public IPagingDataModel<Object[]> getSearchCategory() {
		return searchCategory;
	}

	/**
	 * Sets the search category paging data model.
	 * 
	 * @param searchCategory
	 *            the new search category paging data model
	 */
	public void setSearchCategoryPagingDataModel(
			IPagingDataModel<Object[]> searchCategory) {
		this.searchCategory = searchCategory;
	}

	/**
	 * Gets the search lov paging data model.
	 * 
	 * @return the search lov paging data model
	 */
	public IPagingDataModel<PmCategoryL3> getSearchLOVPagingDataModel() {
		return searchLOVPagingDataModel;
	}

	/**
	 * Sets the search lov paging data model.
	 * 
	 * @param searchLOVPagingDataModel
	 *            the new search lov paging data model
	 */
	public void setSearchLOVPagingDataModel(
			IPagingDataModel<PmCategoryL3> searchLOVPagingDataModel) {
		this.searchLOVPagingDataModel = searchLOVPagingDataModel;
	}

	/**
	 * Reset.
	 */
	public void reset() {
		this.getManagedBean().setDeletedpmBasicCategory(null);
		this.getManagedBean().setDeletedpmMachineCategory(null);
		this.getManagedBean().setDeletedpmRestrCategory(null);

	}

	/**
	 * Gets the faces scopes.
	 * 
	 * @return the faces scopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * Checks if is flag.
	 * 
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * Sets the flag.
	 * 
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 * Gets the search lov machine paging data model.
	 * 
	 * @return the search lov machine paging data model
	 */
	public IPagingDataModel<PmMachine> getSearchLOVMachinePagingDataModel() {
		return searchLOVMachinePagingDataModel;
	}

	/**
	 * Sets the search lov machine paging data model.
	 * 
	 * @param searchLOVMachinePagingDataModel
	 *            the new search lov machine paging data model
	 */
	public void setSearchLOVMachinePagingDataModel(
			IPagingDataModel<PmMachine> searchLOVMachinePagingDataModel) {
		this.searchLOVMachinePagingDataModel = searchLOVMachinePagingDataModel;
	}

	/**
	 * Adds the category restr.
	 */
	public void addCategoryRestr() {

		log.info("Inside of addCategoryRestr() ");
		if (this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs() == null) {

			this.getManagedBean().getPmCategoryL3()
					.setPmCategoryRestrs(new ArrayList<PmCategoryRestr>());
		}
		try {
			PmCategoryRestr restr = new PmCategoryRestr();
			restr.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
			restr.setCompanyTypeId(new PmParameter());
			restr.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);

			log.info("company--->" + restr.getCompanyTypeId().getParameterId());

			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getCategoryRestrList())) {
				this.getManagedBean().getCategoryRestrList().add(restr);
			} else {
				this.getManagedBean().setCategoryRestrList(
						(new ArrayList<PmCategoryRestr>()));
				this.getManagedBean().getCategoryRestrList().add(restr);
			}
			if (CollectionUtils.isEmpty(this.getManagedBean()
					.getTobeAddedCategoryRestrList())) {
				this.getManagedBean().setTobeAddedCategoryRestrList(
						new ArrayList<PmCategoryRestr>());
				this.getManagedBean().getTobeAddedCategoryRestrList()
						.add(restr);
			} else {
				this.getManagedBean().getTobeAddedCategoryRestrList()
						.add(restr);
			}
			this.getManagedBean().setCategoryRestrRequired(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Delete category restr.
	 * 
	 * @param pmCategoryRestr
	 *            the pm category restr
	 */
	public void deleteCategoryRestr(PmCategoryRestr pmCategoryRestr) {

		/* if (!pmCategoryRestr.isBrandNew()) { */
		pmCategoryRestr.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
		if (this.getManagedBean().getDeletedpmRestrCategory() == null) {
			this.getManagedBean().setDeletedpmRestrCategory(
					new ArrayList<PmCategoryRestr>());
		}
		this.getManagedBean().getDeletedpmRestrCategory().add(pmCategoryRestr);
		this.getManagedBean().getCategoryRestrList().remove(pmCategoryRestr);
		this.getManagedBean().getDeletedPmPmCategoryRestrList()
				.add(pmCategoryRestr);
		// this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs().remove(pmCategoryRestr);
		/*
		 * } else{ System.out.println("************************************");
		 */
		// this.getManagedBean().getDeletedpmRestrCategory().add(pmCategoryRestr);
		// this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs().remove(pmCategoryRestr);
		// }

	}

	/**
	 * Gets the populate company type list.
	 * 
	 * @return List<PmParameterDesc>
	 */
	public List<PmParameterDesc> getPopulateCompanyTypeList() {
		if (this.getManagedBean().getCompanyTypeList() == null
				|| this.getManagedBean().getCompanyTypeList().size() < 1) {
			// List<SelectItem> populateCompanyTypeList = new
			// ArrayList<SelectItem>();
			IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale()
					.getLanguage());
			reqDTO.setParameterType(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE);
			ParameterResDTO resDTO = reqSO
					.retrieveParameterDescListByType(reqDTO);

			log.info("*****PopulateCompanyTypeList****"
					+ resDTO.getParameterDescList().size());

			List<PmParameterDesc> parameterDesc = resDTO.getParameterDescList();

			this.getManagedBean().setCompanyTypeList(parameterDesc);
		}

		return this.getManagedBean().getCompanyTypeList();
	}

	/**
	 * Check category restr duplicate company type.
	 * 
	 * @return true, if successful
	 */
	public boolean checkCategoryRestrDuplicateCompanyType() {

		if (this.getManagedBean().getCategoryRestrList() != null
				&& this.getManagedBean().getCategoryRestrList().size() > 0) {
			List<String> companyTypeLs = new ArrayList<String>();
			for (PmCategoryRestr catRestr : this.getManagedBean()
					.getCategoryRestrList()) {
				String paramId = catRestr.getCompanyTypeId().getParameterId()
						.toString();
				if (companyTypeLs.contains(paramId)) {
					return false;
				}
				companyTypeLs.add(paramId);
			}
		}

		return true;
	}

	/**
	 * Reset search criteria result.
	 */
	private void resetSearchCriteriaResult() {
		this.getManagedBean().setMainCatCode(null);
		this.getManagedBean().setMainCatDesc(null);
		this.getManagedBean().setSubCatCode(null);
		this.getManagedBean().setSubCatDesc(null);
		this.getManagedBean().setSubDivCode(null);
		this.getManagedBean().setSubDivDesc(null);
		this.getManagedBean().setStatus(null);
		this.getManagedBean().setPmCategoryLevel(0);
	}

	/*
	 * public void deleteBasicCategory() { List<PmBasicCategory> categoryList =
	 * this.getManagedBean() .getBasicCategoryList();
	 * 
	 * List<PmBasicCategory> removedCategoryList=new
	 * ArrayList<PmBasicCategory>(); removedCategoryList.addAll(categoryList);
	 * 
	 * if(removedCategoryList!=null && !removedCategoryList.isEmpty()){ for
	 * (PmBasicCategory category : removedCategoryList) { if
	 * (category.isSelected()) { removedCategoryList.remove(category); } else{
	 * throw new EpException(MessageConst.ERROR_76000); }
	 * 
	 * } this.getManagedBean().setBasicCategoryList(removedCategoryList); }
	 * 
	 * }
	 */

	public void getCheckCategoryRestrList(AjaxBehaviorEvent event) {
		if (this.getManagedBean().getPmCategoryL3().isBrandNew()) {
			if (this.getManagedBean().getPmCategoryL3().isByCompType()
					|| this.getManagedBean().isCategoryRestrRequired()) {
				if (CollectionUtils.isNotEmpty(this.getManagedBean()
						.getCategoryRestrList())) {
					this.getManagedBean().getCategoryRestrList().clear();

				}
			}
		}
	}
	
	
	/**
	 * Pre render for public search.
	 */
	public void preRenderPublicSearch() {
		log.info("preRenderPublicSearch");
		resetPublicSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/pm/category/publicSearch.xhtml");
	}
	
	public void resetPublicSearchBean(){
		log.info("resetPublicSearchBean");
		getManagedBean().setCategoryCodePublicSearchBean(null);
		retrieveSupplierType();
		publicSearchCategoryPagingDataModel.resetData();
	}
	
	public void publicSearchCategory(){
		log.info("publicSearchCategory");
		CategoryCodePublicSearchBean ccPulicSearchBean = getManagedBean().getCategoryCodePublicSearchBean();
		if(StringUtils.isBlank(ccPulicSearchBean.getCatDesc()) 
				&& ccPulicSearchBean.getPaidUpCapital() == null 
				&& ccPulicSearchBean.getSelectedSupplierType() == null 
				&& ccPulicSearchBean.getSelectedCompanyType() == null
				&& ccPulicSearchBean.getSelectedBumiPercentage() == null
				&& ccPulicSearchBean.getHomeOperated()== null){
			throw new EpException(MessageConst.ERROR_775017);
		}
		
		publicSearchCategoryPagingDataModel.reloadData();
	}
	
	
	/**
	 * The Class PublicSearchCategoryPagingDataModel.
	 */
	private class PublicSearchCategoryPagingDataModel extends
			PagingDataModel<PmCategoryL3> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search category paging data model.
		 */
		public PublicSearchCategoryPagingDataModel() {
			super("publicSearchCategoryTable",false);

		}
		
		public ISearchResultBean<PmCategoryL3> pageChangeListener(
				ISearchBean<PmCategoryL3> searchBean) {
			log.info("PublicSearchCategoryPagingDataModel - pageChangeListener");
			ICategoryCodeSO categoryCodeSO;
			try {
				ISearchBean<PmCategoryL3> searchBeanData = getSearchBeanData(searchBean);
				categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setFetchEntities(true);
				reqDTO.setSearchBean(searchBeanData);
				CategoryCodeResDTO resDTO = categoryCodeSO
						.searchAllCategoryL3(reqDTO);
				if(resDTO.isSuccessful() && resDTO.getCategoryL3ResultBean() !=null){				
					return resDTO.getCategoryL3ResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				categoryCodeSO = null;
			}

			return null;

		}
	}
			
	private ISearchBean<PmCategoryL3> getSearchBeanData(
			ISearchBean<PmCategoryL3> searchBean) {
		CategoryCodePublicSearchBean ccPulicSearchBean = getManagedBean().getCategoryCodePublicSearchBean();
		if(StringUtils.isNotBlank(ccPulicSearchBean.getCatDesc())){
			BaseSearchFilterBean l1SearchFilterBean = new SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryName",
					SearchOperator.OPERATOR_LIKE.value(),
					ccPulicSearchBean.getCatDesc(),
					true);
			l1SearchFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
			BaseSearchFilterBean l2SearchFilterBean = new SearchFilterBean("pmCategoryL2.categoryName",
					SearchOperator.OPERATOR_LIKE.value(),
					ccPulicSearchBean.getCatDesc(),
					true);
			l2SearchFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
			BaseSearchFilterBean l3SearchFilterBean = new SearchFilterBean("categoryName",
					SearchOperator.OPERATOR_LIKE.value(),
					ccPulicSearchBean.getCatDesc(),
					true);
			l3SearchFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
			
			searchBean.getDataFilterBeans().add(
					new SearchMultiFilterBean(
							new SearchMultiFilterBean(
									l1SearchFilterBean,
									SearchCondition.CONDITION_OR.value(),
									l2SearchFilterBean),
							SearchCondition.CONDITION_OR.value(),
							l3SearchFilterBean));
		}
		
		BaseSearchFilterBean l1StatusSearchFilterBean = new SearchFilterBean("pmCategoryL2.pmCategoryL1.recordStatusFlg",
				SearchOperator.OPERATOR_EQUALS.value(),
				DbValueConst.RECORD_STATUS_ACTIVE);
		
		BaseSearchFilterBean l2StatusSearchFilterBean = new SearchFilterBean("pmCategoryL2.recordStatusFlg",
				SearchOperator.OPERATOR_EQUALS.value(),
				DbValueConst.RECORD_STATUS_ACTIVE);
		
		BaseSearchFilterBean l3StatusSearchFilterBean = new SearchFilterBean("recordStatusFlg",
				SearchOperator.OPERATOR_EQUALS.value(),
				DbValueConst.RECORD_STATUS_ACTIVE);
		
		
		searchBean.getDataFilterBeans().add(
				new SearchMultiFilterBean(
						new SearchMultiFilterBean(
								l1StatusSearchFilterBean,
								SearchCondition.CONDITION_AND.value(),
								l2StatusSearchFilterBean),
						SearchCondition.CONDITION_AND.value(),
						l3StatusSearchFilterBean));
		
		if(ccPulicSearchBean.getPaidUpCapital() !=null){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("paidUpCapital",
							SearchOperator.OPERATOR_GREATER_EQUAL.value(),
							ccPulicSearchBean.getPaidUpCapital()));
		}
		
		if(ccPulicSearchBean.getSelectedSupplierType() !=null){
			searchBean.getDataFilterBeans().add(
					new SearchMultiFilterBean(
					new SearchFilterBean("supplierTypeCode.parameterId",
							SearchOperator.OPERATOR_EQUALS.value(),
							ccPulicSearchBean.getSelectedSupplierType()),
							new SearchFilterBean("supplierTypeCode.recordStatusFlg",
									SearchOperator.OPERATOR_EQUALS.value(),
									DbValueConst.RECORD_STATUS_ACTIVE)));
		}
		
		if(ccPulicSearchBean.getSelectedCompanyType() !=null){
			searchBean.getDataFilterBeans().add(
					new SearchMultiFilterBean(
							new SearchMultiFilterBean(
									new SearchMultiFilterBean(
										new SearchFilterBean("byCompType",
												SearchOperator.OPERATOR_EQUALS.value(),
												true),
										SearchCondition.CONDITION_AND.value(),
										new SearchFilterBean("pmCategoryRestrs.companyTypeId.parameterId",
												SearchOperator.OPERATOR_EQUALS.value(),
												ccPulicSearchBean.getSelectedCompanyType())),
												new SearchFilterBean("pmCategoryRestrs.recordStatusFlg",
														SearchOperator.OPERATOR_EQUALS.value(),
														DbValueConst.RECORD_STATUS_ACTIVE)),
														new SearchFilterBean("pmCategoryRestrs.companyTypeId.recordStatusFlg",
																SearchOperator.OPERATOR_EQUALS.value(),
																DbValueConst.RECORD_STATUS_ACTIVE)));
		}
		
		if(ccPulicSearchBean.getSelectedBumiPercentage() !=null){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("bumiPercentage",
							SearchOperator.OPERATOR_EQUALS.value(),
							ccPulicSearchBean.getSelectedBumiPercentage()));			
		}
		
		if(ccPulicSearchBean.getHomeOperated()!=null){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("homeOperated",
							SearchOperator.OPERATOR_EQUALS.value(),
							ccPulicSearchBean.getHomeOperated()));			
		}
		
		searchBean.getSortColumns().add(new SearchOrderBean("pmCategoryL2.pmCategoryL1.categoryL1Code",true));
		searchBean.getSortColumns().add(new SearchOrderBean("pmCategoryL2.categoryL2Code",true));
		searchBean.getSortColumns().add(new SearchOrderBean("categoryL3Code",true));

		return searchBean;
	}
			
	public SelectItem[] getOperateFromHomeStatus() {
		return PmCommonUtil.getYesNoOptions();
	}		
	
	public void retrieveSupplierType() {
		String supplierParamType = "ST";
		String languageCode = new FacesScopes().getUserLocale()
				.getLanguage();
		StringBuilder sb = new StringBuilder();
		sb.append(supplierParamType);
		//not equal O
		sb.append("_NE_O_");
		sb.append(languageCode);
		if(parameterDescMap.get(sb.toString()) !=null){
			this.getManagedBean().setSupplierType(parameterDescMap.get(sb.toString()));
		}else{
			IParameterSO parameterSO;
			try {

				ParameterReqDTO reqDTO = new ParameterReqDTO();
				parameterSO = (IParameterSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IParameterSO.class);
				reqDTO.setLanguageCode(languageCode);

				ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_NOT_EQUALS.value(), "O"));
				reqDTO.setParameterDescSearchBean(searchBean);
				reqDTO.setParameterType(supplierParamType);
				ParameterResDTO resDTO = parameterSO
						.retrieveParameterDescListByType(reqDTO);
				parameterDescMap.put(sb.toString(), resDTO.getParameterDescList());
				this.getManagedBean()
						.setSupplierType(resDTO.getParameterDescList());				

			} catch (Exception e) {
				throw new EpException(e.getMessage());
			} finally {
				parameterSO = null;
			}
		} 
	}
	
	public ISearchBean<PmParameterDesc> getFacilityTypeSearchBean() {
		return facilityTypeSearchBean;
	}

	/**
	 * @return the publicSearchCategoryPagingDataModel
	 */
	public IPagingDataModel<PmCategoryL3> getPublicSearchCategoryPagingDataModel() {
		return publicSearchCategoryPagingDataModel;
	}

	/**
	 * @param publicSearchCategoryPagingDataModel the publicSearchCategoryPagingDataModel to set
	 */
	public void setPublicSearchCategoryPagingDataModel(
			IPagingDataModel<PmCategoryL3> publicSearchCategoryPagingDataModel) {
		this.publicSearchCategoryPagingDataModel = publicSearchCategoryPagingDataModel;
	}
	
	public String constructCategoryDetails(PmCategoryL3 categoryL3){
		StringBuilder sb = new StringBuilder();
		sb.append(ResourceBundleUtils
				.getMessage(
						"lbl.pm.supplier.type",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes()
								.getUserLocale()));
		sb.append(" : ");
		sb.append(getParameterDesc(categoryL3.getSupplierTypeCode()));
		sb.append("<br/>");
		sb.append(ResourceBundleUtils
				.getMessage(
						"lbl.pm.by.company.type",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes()
								.getUserLocale()));
		sb.append(" : ");
		
		int count = 0;
		String paramDesc = null;
		for(PmCategoryRestr restr : categoryL3.getPmCategoryRestrs()){
			if(DbValueConst.RECORD_STATUS_ACTIVE == restr.getRecordStatusFlg()){
				paramDesc = getParameterDesc(restr.getCompanyTypeId());
				if(StringUtils.isNotBlank(paramDesc)){
					if(count != 0){
						sb.append(",");
					}
					sb.append(paramDesc);
					count++;
				}
			}			
		}
		
		// no Business/Company Type
		if(count == 0){
			sb.append("-");
		}
		
		sb.append("<br/>");
		sb.append(ResourceBundleUtils
				.getMessage(
						"lbl.pm.bumi.percentage",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes()
								.getUserLocale()));
		sb.append(" : ");
		sb.append(categoryL3.getBumiPercentage());
		sb.append("%");
		sb.append("<br/>");
		sb.append(ResourceBundleUtils
				.getMessage(
						"lbl.pm.operate.from.home",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes()
								.getUserLocale()));
		sb.append(" : ");
		sb.append(categoryL3.getHomeOperated() == 1 ? ResourceBundleUtils
				.getMessage(
						"lbl.btn.yes",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes()
								.getUserLocale()) : ResourceBundleUtils
								.getMessage(
										"lbl.btn.no",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes()
												.getUserLocale()));
		sb.append("<br/>");
		sb.append(ResourceBundleUtils
				.getMessage(
						"lbl.pm.paid.capital",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes()
								.getUserLocale()));
		sb.append(" : ");		
		sb.append(getAmountDecimalFormat().format(categoryL3.getPaidUpCapital()));
		sb.append("<br/>");
		return sb.toString();
	}
	
	public String getBoldWithSearchDesc(String desc){
		StringBuilder sb = new StringBuilder();
		if(getManagedBean().getCategoryCodePublicSearchBean() != null 
				&& StringUtils.isNotBlank(getManagedBean().getCategoryCodePublicSearchBean().getCatDesc())){
			String searchDesc = getManagedBean().getCategoryCodePublicSearchBean().getCatDesc();
			int startIndex = 0;
			int endIndex = 0;
			if(StringUtils.containsIgnoreCase(desc,searchDesc)){
				if(desc.toLowerCase().startsWith(searchDesc.toLowerCase())){
					startIndex = desc.toLowerCase().indexOf(searchDesc.toLowerCase());
					endIndex = startIndex + searchDesc.length();					
					sb.append("<b>");
					sb.append(desc.substring(0, endIndex));
					sb.append("</b>");
					sb.append(desc.substring(endIndex, desc.length()));
				}else if(desc.toLowerCase().endsWith(searchDesc.toLowerCase())){
					startIndex = desc.toLowerCase().indexOf(searchDesc.toLowerCase());				
					sb.append(desc.substring(0, startIndex));
					sb.append("<b>");
					sb.append(desc.substring(startIndex, desc.length()));
					sb.append("</b>");
				}else {
					startIndex = desc.toLowerCase().indexOf(searchDesc.toLowerCase());
					endIndex = startIndex + searchDesc.length();	
					sb.append(desc.substring(0, startIndex));
					sb.append("<b>");
					sb.append(desc.substring(startIndex,endIndex));
					sb.append("</b>");
					sb.append(desc.substring(endIndex, desc.length()));
				}
			}else{
				sb.append(desc);
			}
		}else{
			sb.append(desc);
		}
		
		return sb.toString();
	}
	
	public void constructMachineDlg(PmCategoryL3 categoryL3){
		getManagedBean().getMachineList().clear();
		getManagedBean().setPmCategoryL3(categoryL3);
		Set<String> machineSet = null;
		if(ParameterTypeConst.CATEGORY_TYPE.equals(categoryL3.getCategoryTypeCode().getParameterType()) 
				&& !ParameterConst.CATEGORY_TYPE_PRINTING.equals(categoryL3.getCategoryTypeCode().getParameterCode())){
			machineSet = new HashSet<String>();
			for(PmMachineSection section:categoryL3.getMachineSectionList()){
				machineSet.add(section.getPmMachine().getMachineDesc());
			}
			getManagedBean().getMachineList().addAll(machineSet);
			  
		}else if(ParameterTypeConst.CATEGORY_TYPE.equals(categoryL3.getCategoryTypeCode().getParameterType()) 
				&& ParameterConst.CATEGORY_TYPE_PRINTING.equals(categoryL3.getCategoryTypeCode().getParameterCode())){
			machineSet = new HashSet<String>();
			for(PmMachineCategory category:categoryL3.getPmMachineCategory()){
				machineSet.add(category.getPmMachine().getMachineDesc());
			}
			getManagedBean().getMachineList().addAll(machineSet);
		}
		Collections.sort(getManagedBean().getMachineList());
		
		RequestContext.getCurrentInstance().execute("dialogMachine.show();");
		RequestContext.getCurrentInstance().execute("dataTableMachine.getPaginator().setPage(0);");
	}
	
	private DecimalFormat getAmountDecimalFormat(){		
		if(amountDF == null){
		   CommonUtils commonUtils = new CommonUtils();
		   amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		}
		return amountDF;
	}
}
