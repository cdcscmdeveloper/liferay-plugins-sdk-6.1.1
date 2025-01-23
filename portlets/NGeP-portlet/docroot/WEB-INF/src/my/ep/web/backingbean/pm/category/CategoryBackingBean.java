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
import java.util.Comparator;
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
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
//start #29665
import my.ep.persistence.entity.pm.common.PmRemarkCategory;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.util.pm.helper.ParameterHelper;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;
import my.ep.util.SLAUtils;
//end #29665

/**
 * Backing Bean for Category
 * 
 * @author azizi
 * @since 30/9/2014
 */
@ManagedBean(name = "categoryBackingBean")
@ViewScoped
// @RequestScoped
public class CategoryBackingBean extends BaseBackingBean<CategoryManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	//Authorization 
	private ISearchBean<PmParameterDesc> facilityTypeSearchBean;
	
	//Param Type as key
	private Map<String, List<PmParameterDesc>> parameterDescMap = 
			new HashMap<String, List<PmParameterDesc>>();
	
	//Start #29665
	private boolean remarkDisable = true;
	private boolean descriptionEditDisable = true;
	private Integer tabIndexActive; // tab 0,1,2
	//End #29665
	
	
	/**
	 * Instantiates a new category backing bean.
	 */
	public CategoryBackingBean() {
		// start #29665 load tab index for level 3 category
		if (this.getManagedBean().getCatLevel() != null
				&& ParameterConst.CATEGORY_LEVEL_3.equals(this.getManagedBean().getCatLevel())) {
			updateTabIndex();
		}
		// end #29665 load tab index for level 3 category
	}
	
	/**
	 * To set a flag to indicate whether the category name has been changed
	 * 
	 * @param event
	 */
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
					 {
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

				/*searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));*/
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmMachineSection.categoryType.parameterType",
								SearchOperator.OPERATOR_EQUALS.value(), "CAT"));
				log.info("getManagedBean().isNewCategory()" + getManagedBean().isNewCategory());
				if(getManagedBean().isNewCategory()){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"pmMachineSection.categoryType.parameterId",
									SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getPmCategoryL3().getCategoryTypeCodeVal()));
					reqDTO.setSearchMachineBean(searchBean);
				}else{
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"pmMachineSection.categoryType.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getPmCategoryL3().getCategoryTypeCode().getParameterCode()));
					reqDTO.setSearchMachineBean(searchBean);
				}
				
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
		this.tabIndexActive=2; //#29665
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
				pmCategoryL3.setCategoryTypeCode(new PmParameter());
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
		this.searchLOVMachinePagingDataModel.reloadData();
	}

	/**
	 * Edits the category.
	 * 
	 * @param id
	 *            (Long id(mandatory))
	 * @return string(/xhtml/pm/category/categoryl1.xhtml?faces-redirect=true)
	 */
	public String editCategory(Long id) {
		ICategoryCodeSO categoryCodeSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

			log.info("editCategory id = " + id);

			reqDTO.setCategoryL1Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL1DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("resDTO.getCategoryL1().getPmCategoryL2s().size() = "
						+ resDTO.getCategoryL1().getPmCategoryL2s().size());
				this.sortPmCategoryL2List(resDTO.getCategoryL1().getPmCategoryL2s());
				this.getManagedBean().setCatLevel(ParameterConst.CATEGORY_LEVEL_1);
				PmCategoryL1 oldCategoryL1 = (PmCategoryL1) BeanUtils.cloneBean(resDTO.getCategoryL1());
				this.getManagedBean().setOldCategoryL1(oldCategoryL1);
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL1());
				this.getManagedBean().setNewCategory(false);

				// start #29665
				this.getManagedBean().setUpdatedCategoryDesc(resDTO.getCategoryL1().getCategoryName());
				this.getManagedBean().setRemarkCategory(null);
				this.getManagedBean().setRemarkHistoryList(updateCreatedUserName(resDTO.getRemarkList()));
				// end #29665
			}
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError("PM", "editCategory", null, "CategoryBackingBean", null, ex);
		}
		return "/xhtml/pm/category/categoryl1.xhtml?faces-redirect=true"; // #29665
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
	 * @param level
	 * @param triggerFromBack
	 *            (Long id(mandatory))
	 * @return string(/xhtml/pm/category/categoryl2.xhtml?faces-redirect=true)
	 */
	public String editSubCategory(Long id, Integer level, boolean triggerFromBack) {// #29665 add new param
		ICategoryCodeSO categoryCodeSO;
		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					ICategoryCodeSO.class);

			reqDTO.setCategoryL2Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL2DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCatLevel(ParameterConst.CATEGORY_LEVEL_2);
				this.getManagedBean().setPmCategoryL2(resDTO.getCategoryL2());
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL2().getPmCategoryL1());

				this.sortPmCategoryL3List(resDTO.getCategoryL2().getPmCategoryL3s());
				PmCategoryL2 oldCategoryL2 = (PmCategoryL2) BeanUtils.cloneBean(resDTO.getCategoryL2());
				this.getManagedBean().setOldCategoryL2(oldCategoryL2);
				this.getManagedBean().setNewCategory(false);

				// start #29665
				this.getManagedBean().setUpdatedCategoryDesc(resDTO.getCategoryL2().getCategoryName());
				this.getManagedBean().setRemarkCategory(null);
				this.getManagedBean().setRemarkHistoryList(updateCreatedUserName(resDTO.getRemarkList()));
				// end #29665
			}
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError("PM", "editSubCategory", null, "CategoryBackingBean", null, ex);
		}

		// start #29665 navigation purpose
		if (!triggerFromBack) {
			if (level == 0) {
				this.getManagedBean().setFromLevel(0);
			} else {
				this.getManagedBean().setFromLevel(1);
			}
		}
		this.remarkDisable = true;
		// end #29665 navigation purpose

		return "/xhtml/pm/category/categoryl2.xhtml?faces-redirect=true";// #29665
	}

	/**
	 * Edits the sub division.
	 * 
	 * @param id
	 * @param level
	 *            Long id(mandatory))
	 * @return string(/xhtml/pm/category/categoryl3.xhtml?faces-redirect=true)
	 */
	public String editSubDivision(Long id, Integer level) { //#29665 add new param
		log.info("***editSubDivision id***" + id);
		
		loadPmCategoryL3Data(id);
		
		//start #29665 navigation purpose
		if (level == 0) {
		    this.getManagedBean().setFromLevel(0);
		} else {
		    this.getManagedBean().setFromLevel(2);
		}
		this.remarkDisable = true;
		//end #29665 navigation purpose

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
			
			for (PmMachineCategory cat : resDTO.getCategoryL3().getPmMachineCategory()) {
				// start #29665
				List<String> siteVisit = new ArrayList<>();
				if (cat.getDockYardSiteVisit() != null) {
					siteVisit.add(cat.getDockYardSiteVisit().toString());
				}

				if (cat.getWorkshopSiteVisit() != null) {
					siteVisit.add(cat.getWorkshopSiteVisit().toString());
				}

				if (cat.getPrintingSiteVisit() != null) {
					siteVisit.add(cat.getPrintingSiteVisit().toString());
				}

				cat.setPmMachineCategorySectionList(siteVisit);
				// end #29665
			}
			
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
				
				//start #29665
				this.getManagedBean().setUpdatedCategoryDesc(resDTO.getCategoryL3().getCategoryName());
				this.getManagedBean().setRemarkCategory(null);
				this.getManagedBean().setRemarkHistoryList(updateCreatedUserName(resDTO.getRemarkList()));
				 //end #29665

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
				
				//start #29665 - comment out unused function, it can cause initialized error
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL3().getPmCategoryL2().getPmCategoryL1());
				//end #29665 - comment out unused function, it can cause initialized error

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

			//start #29665
			PmRemarkCategory prc = new PmRemarkCategory();
			if (!this.remarkDisable && !this.getManagedBean().isNewCategory()) {
				prc.setCategoryId(this.getManagedBean().getPmCategoryL1().getCategoryL1Id());
				prc.setCategoryLevel(1);
				prc.setCreatedDate(new Timestamp(new Date().getTime()));
				prc.setCreatedBy(getUserIdSessionFromLoginId());
				if (!this.descriptionEditDisable
						&& this.getManagedBean().getUpdatedCategoryDesc()
								.compareTo(this.getManagedBean().getOldCategoryL1().getCategoryName()) != 0) {
					prc.setOldDescription(this.getManagedBean().getOldCategoryL1().getCategoryName());
					this.getManagedBean().getPmCategoryL1()
							.setCategoryName(this.getManagedBean().getUpdatedCategoryDesc().toUpperCase());
					this.getManagedBean().getPmCategoryL1().setDescChangeDate(prc.getCreatedDate());
				} else {
					prc.setOldDescription(null);
				}
				prc.setRemark(this.getManagedBean().getRemarkCategory());
				reqDTO.setRemarkCat(prc);
			} else {
				reqDTO.setRemarkCat(null);
			}
			//end #29665

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
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
									MessageConst.INFO_100200,
									new String[] {
											ResourceBundleUtils.getMessage("lbl.pm.main.category",
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes().getUserLocale()),
											resDTO.getCategoryL1().getCategoryL1Code() },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				} else {
					// start #29665
					this.descriptionEditDisable = true;
					this.remarkDisable = true;
					this.getManagedBean().setRemarkCategory(null);
					this.getManagedBean().setRemarkHistoryList(updateCreatedUserName(resDTO.getRemarkList()));
					// end #29665

					returnUrl = "/xhtml/pm/category/categoryl1.xhtml";
					result = resDTO.isSuccessful();
					// resetCategory();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
									MessageConst.INFO_100201,
									new String[] {
											ResourceBundleUtils.getMessage("lbl.pm.main.category",
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes().getUserLocale()),
											resDTO.getCategoryL1().getCategoryL1Code() },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
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
								this.getManagedBean().getPmCategoryL1()
								.getRecordStatusFlg().longValue(),
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
			
			// start #29665
			PmRemarkCategory prc = new PmRemarkCategory();
			if (!this.remarkDisable && !this.getManagedBean().isNewCategory()) {
				prc.setCategoryId(this.getManagedBean().getPmCategoryL2().getCategoryL2Id());
				prc.setCategoryLevel(2);
				prc.setCreatedDate(new Timestamp(new Date().getTime()));
				prc.setCreatedBy(getUserIdSessionFromLoginId());
				if (!this.descriptionEditDisable
						&& this.getManagedBean().getUpdatedCategoryDesc()
								.compareTo(this.getManagedBean().getOldCategoryL2().getCategoryName()) != 0) {
					prc.setOldDescription(this.getManagedBean().getOldCategoryL2().getCategoryName());
					this.getManagedBean().getPmCategoryL2()
							.setCategoryName(this.getManagedBean().getUpdatedCategoryDesc().toUpperCase());
					this.getManagedBean().getPmCategoryL2().setDescChangeDate(prc.getCreatedDate());
				} else {
					prc.setOldDescription(null);
				}
				prc.setRemark(this.getManagedBean().getRemarkCategory());
				reqDTO.setRemarkCat(prc);
			} else {
				reqDTO.setRemarkCat(null);
			}
			// end #29665

			reqDTO.setPmCategoryL2(this.getManagedBean().getPmCategoryL2());
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
			reqDTO.setCategoryLevel(this.getManagedBean().getCatLevel());
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());

			trackingDiaryForCreateAndUpdateOfSubCategory(reqDTO);

			CategoryCodeResDTO resDTO = categoryCodeSO.saveSubCategory(reqDTO);

			// start @29665 add if to avoid NPE when create new sub categ
			if (!this.getManagedBean().isNewCategory()) {
				preLoadPmCategoryl2(this.getManagedBean().getPmCategoryL2().getCategoryL2Id());
			}
			//end #29665 add if to avoid NPE when create new sub categ
			
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
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
									MessageConst.INFO_100200,
									new String[] {
											ResourceBundleUtils.getMessage("lbl.pm.sub.category",
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes().getUserLocale()),
											resDTO.getCategoryL2().getCategoryL2Code() },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				} else {
					// start #29665
					this.descriptionEditDisable = true;
					this.remarkDisable = true;
					this.getManagedBean().setRemarkCategory(null);
					this.getManagedBean().setRemarkHistoryList(updateCreatedUserName(resDTO.getRemarkList()));
					// end #29665
					returnUrl = "/xhtml/pm/category/categoryl2.xhtml";
					result = resDTO.isSuccessful();
					// resetCategory();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
									MessageConst.INFO_100201,
									new String[] {
											ResourceBundleUtils.getMessage("lbl.pm.sub.category",
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes().getUserLocale()),
											resDTO.getCategoryL2().getCategoryL2Code() },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
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
								this.getManagedBean().getPmCategoryL2()
								.getRecordStatusFlg().longValue(),
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
	//Bug #37757 - Start
	public String saveSubDivision() {

		ICategoryCodeSO categoryCodeSO;
		String returnUrl = null;
		
		validateNewAndDuplicateCategory();
		// validation for fields
		validatePmCategoryL3();

		try {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);
			
			prepareCategoryData();
			// Basic category and Category Restr data information
			getBasicCategoryData();
			getCategoryRestrData();
			getMachineCategoryData(); //#29665
			
			// start #29665
			PmRemarkCategory prc = new PmRemarkCategory();
			if (!this.remarkDisable && !this.getManagedBean().isNewCategory()) {
				prc.setCategoryId(this.getManagedBean().getPmCategoryL3().getCategoryL3Id());
				prc.setCategoryLevel(3);
				prc.setCreatedDate(new Timestamp(new Date().getTime()));
				prc.setCreatedBy(getUserIdSessionFromLoginId());
				if (validateCategoryDescAndName()) {
					prc.setOldDescription(this.getManagedBean().getOldCategoryL3().getCategoryName());
					this.getManagedBean().getPmCategoryL3()
							.setCategoryName(this.getManagedBean().getUpdatedCategoryDesc().toUpperCase());
					this.getManagedBean().getPmCategoryL3().setDescChangeDate(prc.getCreatedDate());
				} else {
					prc.setOldDescription(null);
				}
				prc.setRemark(this.getManagedBean().getRemarkCategory());
				reqDTO.setRemarkCat(prc);
			} else {
				reqDTO.setRemarkCat(null);
			}
			// end #29665

			reqDTO.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
			reqDTO.setCategoryLevel(this.getManagedBean().getCatLevel());
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
			//Bug #37757 - Start
			if (validateSsmExclusive()) {
				reqDTO.getPmCategoryL3().setSsmExclusive(0);
			}
			//Bug #37757 - End

			PmCategoryL3 pmCategoryL3 = this.getManagedBean().getPmCategoryL3();
			IStatusSO statusSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			

			trackingDiaryForCreateAndUpdateOfSubDivision(
					reqDTO, pmCategoryL3, statusSO);

			getAllCategoryDetailsForTracking(reqDTO, this.getManagedBean().getPmCategoryL3()
					.getRecordStatusFlg().longValue());
			
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

				if (this.getManagedBean().isNewCategory()) {
					this.getManagedBean().setNewCategory(false);
					returnUrl = "/xhtml/pm/category/search.xhtml";
					result = resDTO.isSuccessful();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
									MessageConst.INFO_100200,
									new String[] {
											ResourceBundleUtils.getMessage("lbl.pm.sub.div",
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes().getUserLocale()),
											this.getManagedBean().getPmCategoryL3().getCategoryL3Code() },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				} else {
					loadPmCategoryL3Data(this.getManagedBean().getPmCategoryL3().getCategoryL3Id());
					// start #29665
					this.descriptionEditDisable = true;
					this.remarkDisable = true;
					updateTabIndex();
					// end #29665
					returnUrl = "/xhtml/pm/category/categoryl3.xhtml";
					result = resDTO.isSuccessful();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
									MessageConst.INFO_100201,
									new String[] {
											ResourceBundleUtils.getMessage("lbl.pm.sub.div",
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes().getUserLocale()),
											this.getManagedBean().getPmCategoryL3().getCategoryL3Code() },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}
			}

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, null, null,
					"CategoryBackingBean.saveSubDivision", null, e);
		} 
		return returnUrl;
	}
	//Bug #37757 - End

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
	private void trackingDiaryForCreateAndUpdateOfSubDivision(
			CategoryCodeReqDTO reqDTO, PmCategoryL3 pmCategoryL3,
			IStatusSO statusSO) {
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


		reqDTO.setTrackingStatus(true);

		reqDTO.getTrackDairyVOs().add(
				PmCommonUtil.assignTrackingDairyForPmCategoryL3(
						this.getManagedBean().getPmCategoryL3()
								.getCategoryL3Id(),
						this.getManagedBean().getPmCategoryL2()
								.getCategoryL2Id(),
						this.getManagedBean().getPmCategoryL3()
								.getCategoryL3Code(),
								this.getManagedBean().getPmCategoryL3()
								.getRecordStatusFlg().longValue(),
						getPmActionDesc(messageId, msgParam),
						ParameterConst.SUB_DIVISION_CATEGORY_PARAM_CODE,
						ParameterConst.SUB_DIVISION_CATEGORY_PARAM_CODE));
		
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

		// #29665 remove unused variable
		List<PmMachineCategory> tobeAddedPmMachineCategory = this.getManagedBean().getTobeAddedPmMachineCategoryList();

		// #29665 start
		if (CollectionUtils.isNotEmpty(tobeAddedPmMachineCategory)) {
			tobeAddedPmMachineCategory.clear();
		// #29665 end
		} else if (CollectionUtils.isEmpty(this.getManagedBean().getPmMachineCategoryList())
				&& CollectionUtils.isEmpty(tobeAddedPmMachineCategory)
				&& this.getManagedBean().isCheckSpecialCategory()) {

			throw new EpException(MessageConst.ERROR_775008, new String[] { ResourceBundleUtils.getMessage(
					"lbl.pm.special.equipment", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()) });
		}

		// start #29665
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getPmMachineCategoryList())) {
			for (PmMachineCategory cat : this.getManagedBean().getPmMachineCategoryList()) {
				if (cat.getPmMachineCategorySectionList().contains(String.valueOf(ParameterConst.DOCKYARD_TYPEID))) {
					cat.setDockYardSiteVisit(ParameterConst.DOCKYARD_TYPEID);
				} else {
					cat.setDockYardSiteVisit(null);
				}

				if (cat.getPmMachineCategorySectionList().contains(String.valueOf(ParameterConst.WORKSHOP_TYPEID))) {
					cat.setWorkshopSiteVisit(ParameterConst.WORKSHOP_TYPEID);
				} else {
					cat.setWorkshopSiteVisit(null);
				}

				if (cat.getPmMachineCategorySectionList().contains(String.valueOf(ParameterConst.PRINTING_TYPEID))) {
					String languageCode = new FacesScopes().getUserLocale().getLanguage();
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					
					reqDTO.setLanguageCode(languageCode);
					reqDTO.setCodeName(ParameterConst.ACCESSORIES_TYPE);
					ParameterResDTO resDTO = parameterSO.retrieveParameterDescByCodeNameAndLanguage(reqDTO);
					cat.setPrintingSiteVisit(resDTO.getPmParameterDesc().getPmParameter().getParameterId());
				} else {
					cat.setPrintingSiteVisit(null);
				}
			}
			this.getManagedBean().getPmCategoryL3()
					.setPmMachineCategory(this.getManagedBean().getPmMachineCategoryList());
		}
		// end #29665
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
			Long statusId) {

		String[] commonMsgParam = new String[] {
				this.getManagedBean().getPmCategoryL1().getCategoryL1Code(),
				this.getManagedBean().getPmCategoryL2().getCategoryL2Code(),
				this.getManagedBean().getPmCategoryL3().getCategoryL3Code() };

		getBasicCategoryForTrackingDairy(reqDTO, statusId, commonMsgParam);
		getMachineCategoryForTrackingDairy(reqDTO, statusId, commonMsgParam);
		getCategoryRestrForTrackingDiary(reqDTO, statusId, commonMsgParam);

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
			Long statusId, String[] commonMsgParam) {
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
						trackingDairyData(reqDTO, statusId,
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
							trackingDairyData(reqDTO, statusId,
									statusMessageId, commonMsgParam);
						}
					}
				} else {
					for (PmCategoryRestr pmCategoryRestr : this
							.getManagedBean().getCategoryRestrList()) {
						if (pmCategoryRestr.isBrandNew()) {
							statusMessageId = MessageConst.TD_80712;
							reqDTO.setTrackingStatus(true);
							trackingDairyData(reqDTO, statusId,
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
			Long statusId, String[] commonMsgParam) {
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
						trackingDairyData(reqDTO, statusId,
								statusMessageId, commonMsgParam);
					}
				}
				this.getManagedBean().getDeletedPmMachineCategoryList().clear();
			}
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getPmMachineCategoryList())) {
				if (!this.getManagedBean().isNewCategory()) {
					for (PmMachineCategory deleMachine : this.getManagedBean()
							.getPmMachineCategoryList()){
						if (deleMachine.isBrandNew()) {
							statusMessageId = MessageConst.TD_80714;
							reqDTO.setTrackingStatus(true);
							trackingDairyData(reqDTO, statusId,
									statusMessageId, commonMsgParam);
						}
					}
				} else {
					for (PmMachineCategory deleMachine : this.getManagedBean()
							.getPmMachineCategoryList()) {
						if (deleMachine.isBrandNew()) {
							statusMessageId = MessageConst.TD_80714;
							reqDTO.setTrackingStatus(true);
							trackingDairyData(reqDTO, statusId,
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
			Long statusId, String[] commonMsgParam) {
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
						trackingDairyData(reqDTO, statusId,
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
							trackingDairyData(reqDTO, statusId,
									statusMessageId, commonMsgParam);
						}
					}
				} else {
					for (PmBasicCategory pmBasicCategory : this
							.getManagedBean().getBasicCategoryList()) {
						if (pmBasicCategory.isBrandNew()) {
							statusMessageId = MessageConst.TD_80716;
							reqDTO.setTrackingStatus(true);
							trackingDairyData(reqDTO, statusId,
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
			Long statusId, String statusMessageId,
			String[] commonMsgParam) {
		reqDTO.getTrackDairyVOs().add(
				PmCommonUtil.assignTrackingDairyForPmBasicCategory(
						this.getManagedBean().getPmCategoryL3()
								.getCategoryL3Id(),
						this.getManagedBean().getPmCategoryL2()
								.getCategoryL2Id(),
						this.getManagedBean().getPmCategoryL3()
								.getCategoryL3Code(),
								statusId,
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
		pmBasicCategory.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
		if (CollectionUtils.isEmpty(this.getManagedBean().getDeletedpmBasicCategory())) {
			this.getManagedBean().setDeletedpmBasicCategory(new ArrayList<PmBasicCategory>());
		}
		this.getManagedBean().getDeletedpmBasicCategory().add(pmBasicCategory);

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getBasicCategoryList())) {
			this.getManagedBean().getBasicCategoryList().remove(pmBasicCategory);
		}
		this.getManagedBean().getDeletedPmBasicCategoryList().add(pmBasicCategory);

		// start #29665 remove parameterId null in to be added list
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getTobeAddedPmBasicCategoryList())) {
			this.getManagedBean().getTobeAddedPmBasicCategoryList().remove(pmBasicCategory);
		}
		// end #29665 remove parameterId null in to be added list

		if (StringUtils.isBlank(this.getManagedBean().getRemarkCategory()) && !this.getManagedBean().isNewCategory()) {
			this.remarkDisable = false;
		} // #29665
	}

	/**
	 * Delete pm machine.
	 * 
	 * @param pmMachineCategory
	 *            the pm machine category
	 */
	public void deletePmMachine(PmMachineCategory pmMachineCategory) {
		log.info("In deletePmMachine");
		pmMachineCategory.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);

		if (CollectionUtils.isEmpty(this.getManagedBean().getDeletedpmMachineCategory())) {
			this.getManagedBean().setDeletedpmMachineCategory(new ArrayList<PmMachineCategory>());
			this.getManagedBean().getDeletedpmMachineCategory().add(pmMachineCategory);
		}

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getPmMachineCategoryList())) {
			this.getManagedBean().getPmMachineCategoryList().remove(pmMachineCategory);

		}

		// start #29665 remove parameterId null in to be added list
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getTobeAddedPmMachineCategoryList())) {
			this.getManagedBean().getTobeAddedPmMachineCategoryList().remove(pmMachineCategory);
		}
		// end #29665 remove parameterId null in to be added list

		this.getManagedBean().getDeletedPmMachineCategoryList().add(pmMachineCategory);

		if (StringUtils.isBlank(this.getManagedBean().getRemarkCategory()) && !this.getManagedBean().isNewCategory()) {
			this.remarkDisable = false; // #29665
		}
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
			if(DbValueConst.RECORD_STATUS_ACTIVE == this.getManagedBean().getPmCategoryL1()
					.getRecordStatusFlg().intValue()){
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setMainCatCode(this.getManagedBean().getPmCategoryL1()
						.getCategoryL1Code());
				CategoryCodeResDTO resDTO = categoryCodeSO
						.retrieveCategoryL1ByCode(reqDTO);
				if (resDTO.getCategoryL1() != null) {
					if (this.getManagedBean().isNewCategory()) {
						throw new EpException(MessageConst.ERROR_770218,new String[] {
								PmCommonUtil.getResourceBundleLabels("lbl.pm.main.category"),
								PmCommonUtil.getResourceBundleLabels("lbl.pm.category.code.only"),
								PmCommonUtil.getResourceBundleLabels("lbl.pm.category.code.only")
						});
					}else{
						if(!resDTO.getCategoryL1().getCategoryL1Id()
								.equals(getManagedBean().getPmCategoryL1().getCategoryL1Id())){
							throw new EpException(MessageConst.ERROR_770220, new String[] {
								PmCommonUtil.getResourceBundleLabels("lbl.pm.category.code.only")
							});
						}
					
					}
				}

				CategoryCodeResDTO resDTODesc = retrieveCategoryL1ListByCatCodeDesc(categoryCodeSO);
				if (resDTODesc.getCategoryL1List() != null
						&& resDTODesc.getCategoryL1List().size() > 0) {						
							if (this.getManagedBean().isNewCategory()) {
								throw new EpException(MessageConst.ERROR_770218,new String[] {
										PmCommonUtil.getResourceBundleLabels("lbl.pm.main.category"),
										PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only"),
										PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only")
								});
							}else{
								throw new EpException(MessageConst.ERROR_770220, new String[] {
										PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only")
								});	
							}
						}
				
					
			}
			
		}finally {
			categoryCodeSO = null;
		}
	}

	/**
	 * To retrieve categoryL1 List By Category Code Description
	 * 
	 * @param categoryCodeSO
	 * @return CategoryCodeResDTO
	 */
	private CategoryCodeResDTO retrieveCategoryL1ListByCatCodeDesc(
			ICategoryCodeSO categoryCodeSO) {
		CategoryCodeReqDTO reqDTOUpdate = new CategoryCodeReqDTO();
		reqDTOUpdate.setMainCatDesc(this.getManagedBean().getPmCategoryL1()
				.getCategoryName());
		if(!getManagedBean().getPmCategoryL1().isBrandNew()){
			reqDTOUpdate.setCategoryL1Id(this.getManagedBean().getPmCategoryL1()
					.getCategoryL1Id());
		}
		
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
			if (getManagedBean().getPmCategoryL2().getRecordStatusFlg() 
					== DbValueConst.RECORD_STATUS_ACTIVE) {
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setCategoryL1Id(this.getManagedBean().getPmCategoryL1()
						.getCategoryL1Id());
				if(!this.getManagedBean().getPmCategoryL2().isBrandNew()){
					reqDTO.setCategoryL2Id(this.getManagedBean().getPmCategoryL2()
						.getCategoryL2Id());
				}
				
				reqDTO.setSubCatCode(this.getManagedBean().getPmCategoryL2()
						.getCategoryL2Code());
				CategoryCodeResDTO resDTO = categoryCodeSO
						.retrieveCategoryL2ListByCode(reqDTO);
				if (resDTO.getCategoryL2List() != null
						&& resDTO.getCategoryL2List().size() > 0) {
					if (this.getManagedBean().isNewCategory()) {
						throw new EpException(MessageConst.ERROR_770219,new String[] { 
								PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.category"),
								PmCommonUtil.getResourceBundleLabels("lbl.pm.category.code.only"),
								PmCommonUtil.getResourceBundleLabels("lbl.pm.main.category.desc"),
								this.getManagedBean().getPmCategoryL1().getCategoryName(),
								PmCommonUtil.getResourceBundleLabels("lbl.pm.category.code.only")
						});
					}else{
						throw new EpException(MessageConst.ERROR_770220, new String[] {
								PmCommonUtil.getResourceBundleLabels("lbl.pm.category.code.only")
						});	
					}
				}

				CategoryCodeResDTO resDTODesc = retrieveCategoryL2ListByCatCodeDesc(categoryCodeSO);
				if (resDTODesc.getCategoryL2List() != null
						&& resDTODesc.getCategoryL2List().size() > 0) {
					if (this.getManagedBean().isNewCategory()) {
						throw new EpException(MessageConst.ERROR_770219,new String[] { 
								PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.category"),
								PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only"),
								PmCommonUtil.getResourceBundleLabels("lbl.pm.main.category.desc"),
								this.getManagedBean().getPmCategoryL1().getCategoryName(),
								PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only")
						});
					}else{
						 throw new EpException(MessageConst.ERROR_770220, new String[] {
								PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only")
						});	
					}
				}
			}
		} finally {
			categoryCodeSO = null;
		}
	}

	/**
	 * To Retrieve CategoryL2 List By Category Code Description
	 * 
	 * @param categoryCodeSO
	 * @return CategoryCodeResDTO
	 */
	private CategoryCodeResDTO retrieveCategoryL2ListByCatCodeDesc(
			ICategoryCodeSO categoryCodeSO) {
		CategoryCodeReqDTO reqDTOUpdate = new CategoryCodeReqDTO();
		reqDTOUpdate.setCategoryL1Id(this.getManagedBean().getPmCategoryL1()
				.getCategoryL1Id());
		if(!getManagedBean().getPmCategoryL2().isBrandNew()){
			reqDTOUpdate.setCategoryL2Id(this.getManagedBean().getPmCategoryL2()
				.getCategoryL2Id());
		}		
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

		//#29665 remove load getmachine

		if (this.getManagedBean().getPmCategoryL3().isByCompType()
				&& (this.getManagedBean().getCategoryRestrList() == null || this
						.getManagedBean().getCategoryRestrList().size() == 0)) {
			throw new EpException(MessageConst.ERROR_775015,new String[]{ResourceBundleUtils.getMessage("lbl.pm.company.type.tab",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}

		if (DbValueConst.RECORD_STATUS_ACTIVE == 
				this.getManagedBean().getPmCategoryL3().getRecordStatusFlg().intValue()) {
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL2Id(this.getManagedBean().getPmCategoryL3()
						.getPmCategoryL2().getCategoryL2Id());
			if(!getManagedBean().getPmCategoryL3().isBrandNew()){
				reqDTO.setCategoryL3Id(this.getManagedBean().getPmCategoryL3()
					.getCategoryL3Id());
			}
			
			reqDTO.setSubDivCode(this.getManagedBean().getPmCategoryL3()
						.getCategoryL3Code());
			CategoryCodeResDTO resDTO = categoryCodeSO
						.retrieveCategoryL3ListByCode(reqDTO);
			if (resDTO.getCategoryL3List() != null
						&& resDTO.getCategoryL3List().size() > 0) {
				if (this.getManagedBean().isNewCategory()) {
					throw new EpException(MessageConst.ERROR_770219,new String[] { 
							PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.div"),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.category.code.only"),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.category.desc"),
							this.getManagedBean().getPmCategoryL2().getCategoryName(),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.category.code.only")
					});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.category.code.only")
					});	
				}
			}
	
			CategoryCodeResDTO resDTODesc = retrieveCategoryL3ListByCatCodeDesc(categoryCodeSO);
			if (resDTODesc.getCategoryL3List() != null
						&& resDTODesc.getCategoryL3List().size() > 0) {						
				if (this.getManagedBean().isNewCategory()) {
					throw new EpException(MessageConst.ERROR_770219,new String[] { 
							PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.div"),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only"),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.sub.category.desc"),
							this.getManagedBean().getPmCategoryL2().getCategoryName(),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only")
					});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.category.desc.only")
					});	
				}
			}
		}
			
			
		} finally {
			categoryCodeSO = null;
		}
	}

	/**
	 * To Retrieve CategoryL3 List By Category Code Description
	 * 
	 * @param categoryCodeSO
	 * @return CategoryCodeResDTO
	 */
	private CategoryCodeResDTO retrieveCategoryL3ListByCatCodeDesc(
			ICategoryCodeSO categoryCodeSO) {
		CategoryCodeReqDTO reqDTODesc = new CategoryCodeReqDTO();
		reqDTODesc.setCategoryL2Id(this.getManagedBean().getPmCategoryL3()
				.getPmCategoryL2().getCategoryL2Id());
		reqDTODesc.setCategoryL1Id(this.getManagedBean().getPmCategoryL3()
				.getPmCategoryL2().getPmCategoryL1().getCategoryL1Id());
		reqDTODesc.setSubCatDesc(this.getManagedBean().getPmCategoryL3()
				.getCategoryName());
		if(!getManagedBean().getPmCategoryL3().isBrandNew()){
			reqDTODesc.setCategoryL3Id(this.getManagedBean().getPmCategoryL3()
				.getCategoryL3Id());
		}
		
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
		// Start #29665
		if (StringUtils.isBlank(this.getManagedBean().getRemarkCategory()) && !this.getManagedBean().isNewCategory()) {
			this.remarkDisable = false;
		}
		// End #29665
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
		//Start #29665
		if (StringUtils.isBlank(this.getManagedBean().getRemarkCategory()) && !this.getManagedBean().isNewCategory()) {
			this.remarkDisable = false;
		}
		//End #29665
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
		//reset the Category data based on selection categroy level		
		searchCategory.updatePagingResults(null);
		
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
			if (this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue()
					|| this.getManagedBean().isBasicCategoryRequired()) {
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getBasicCategoryList())) {
					this.getManagedBean().getBasicCategoryList().clear();

				}
			}
		}
		// start #29665
		else if (StringUtils.isBlank(this.getManagedBean().getRemarkCategory())
				&& !this.getManagedBean().isNewCategory()) {
			this.remarkDisable = false;
		}
		updateTabIndex();
		// end #29665
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

						// start #29665
						if (parameterCode.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_PRINTING)) {
							this.getManagedBean().setCheckSpecialCategory(true);
							this.getManagedBean().setDisableSection(1);
						} else if (parameterCode.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_NORMAL)) {
							this.getManagedBean().setFacilityTypeChoosable(false);
						} else if (parameterCode.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_DOCKYARD)
								|| parameterCode.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_WORKSHOP)) {
							this.getManagedBean().setDisableSection(2);
						} else {
							this.getManagedBean().setFacilityTypeChoosable(true);
							this.getManagedBean().setDisableSection(0);
						}
						updateTabIndex();
						if (CollectionUtils.isNotEmpty(this.getManagedBean().getPmMachineCategoryList())) {
							this.getManagedBean().getPmMachineCategoryList().clear();
						}
						// end #29665
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
	 * Navigation to back to previous screen or search screen
	 * 
	 * @return the string
	 */
	public String backToSearch() {
		// start #29665 back navigation based on previous screen
		if (this.getManagedBean().getFromLevel() == 1) {
			this.getManagedBean().setFromLevel(0);
			return editCategory(this.getManagedBean().getPmCategoryL2().getPmCategoryL1().getCategoryL1Id());
		} else if (this.getManagedBean().getFromLevel() == 2) {
			this.getManagedBean().setFromLevel(0);
			return editSubCategory(this.getManagedBean().getPmCategoryL3().getPmCategoryL2().getCategoryL2Id(), 2, true);
		} else {
			return "/xhtml/pm/category/search.xhtml?faces-redirect=true";
		}
		// end #29665 back navigation based on previous screen
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

			this.getManagedBean().getPmCategoryL3().setPmCategoryRestrs(new ArrayList<PmCategoryRestr>());
		}
		try {
			PmCategoryRestr restr = new PmCategoryRestr();
			restr.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
			restr.setCompanyTypeId(new PmParameter());
			restr.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);

			log.info("company--->" + restr.getCompanyTypeId().getParameterId());

			if (CollectionUtils.isNotEmpty(this.getManagedBean().getCategoryRestrList())) {
				this.getManagedBean().getCategoryRestrList().add(restr);
			} else {
				this.getManagedBean().setCategoryRestrList((new ArrayList<PmCategoryRestr>()));
				this.getManagedBean().getCategoryRestrList().add(restr);
			}
			if (CollectionUtils.isEmpty(this.getManagedBean().getTobeAddedCategoryRestrList())) {
				this.getManagedBean().setTobeAddedCategoryRestrList(new ArrayList<PmCategoryRestr>());
				this.getManagedBean().getTobeAddedCategoryRestrList().add(restr);
			} else {
				this.getManagedBean().getTobeAddedCategoryRestrList().add(restr);
			}
			this.getManagedBean().setCategoryRestrRequired(true);
			// Start #29665
			if (StringUtils.isBlank(this.getManagedBean().getRemarkCategory())
					&& !this.getManagedBean().isNewCategory()) {
				this.remarkDisable = false;
			}
			// End #29665
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

		pmCategoryRestr.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
		if (this.getManagedBean().getDeletedpmRestrCategory() == null) {
			this.getManagedBean().setDeletedpmRestrCategory(new ArrayList<PmCategoryRestr>());
		}
		this.getManagedBean().getDeletedpmRestrCategory().add(pmCategoryRestr);
		this.getManagedBean().getCategoryRestrList().remove(pmCategoryRestr);
		this.getManagedBean().getDeletedPmPmCategoryRestrList().add(pmCategoryRestr);

		// start #29665 remove parameterId null in to be added list
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getTobeAddedCategoryRestrList())) {
			this.getManagedBean().getTobeAddedCategoryRestrList().remove(pmCategoryRestr);
		}

		if (StringUtils.isBlank(this.getManagedBean().getRemarkCategory()) && !this.getManagedBean().isNewCategory()) {
			this.remarkDisable = false;
		}
		// end #29665 remove parameterId null in to be added list
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

	/**
	 * Get Check Category Restriction List
	 * 
	 * @param event
	 */
	public void getCheckCategoryRestrList(AjaxBehaviorEvent event) {
		if (this.getManagedBean().getPmCategoryL3().isBrandNew()) {
			if (this.getManagedBean().getPmCategoryL3().isByCompType()
					|| this.getManagedBean().isCategoryRestrRequired()) {
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getCategoryRestrList())) {
					this.getManagedBean().getCategoryRestrList().clear();

				}
			}
		}
		// start #29665
		else if (StringUtils.isBlank(this.getManagedBean().getRemarkCategory())
				&& !this.getManagedBean().isNewCategory()) {
			this.remarkDisable = false;
		}
		updateTabIndex();
		// end #29665
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
	
	/**
	 * Reset Public Search Bean
	 */
	public void resetPublicSearchBean(){
		log.info("resetPublicSearchBean");
		getManagedBean().setCategoryCodePublicSearchBean(null);
		retrieveSupplierType();
		publicSearchCategoryPagingDataModel.resetData();
	}
	
	/**
	 * Public Search Category
	 */
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
				CategoryCodePublicSearchBean ccPulicSearchBean = getManagedBean().getCategoryCodePublicSearchBean();
				//ISearchBean<PmCategoryL3> searchBeanData = getSearchBeanData(searchBean);
				categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setFetchEntities(true);
				reqDTO.setCatDesc(ccPulicSearchBean.getCatDesc());
				reqDTO.setSelectedSupplierType(ccPulicSearchBean.getSelectedSupplierType());
				reqDTO.setSelectedBumiPercentage(ccPulicSearchBean.getSelectedBumiPercentage());
				reqDTO.setSelectedCompanyType(ccPulicSearchBean.getSelectedCompanyType());
				reqDTO.setHomeOperated(ccPulicSearchBean.getHomeOperated());
				reqDTO.setPaidUpCapitalSearch(ccPulicSearchBean.getPaidUpCapital());
				reqDTO.setSearchBean(searchBean);
				CategoryCodeResDTO resDTO = categoryCodeSO
						.searchPublicAllCategoryL3(reqDTO);
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
			
	/**
	 * To Generate Search Bean Data 
	 * @param searchBean
	 * @return ISearchBean<PmCategoryL3>
	 */
	@Deprecated
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
							SearchOperator.OPERATOR_LESS_EQUAL.value(),
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
						
			
//			searchBean.getDataFilterBeans().add(
		SearchMultiFilterBean ctMulti1SearchBean =	new SearchMultiFilterBean(
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
																DbValueConst.RECORD_STATUS_ACTIVE));
		
//		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
//				.getInstance().getServiceObject(
//						ModuleCodeConst.PROFILE_MANAGEMENT,
//						ICategoryCodeSO.class);
//		SearchMultiFilterBean ctMulti2SearchBean = 
//				new SearchMultiFilterBean(ctMulti1SearchBean, 
//						new SearchFilterBean("categoryL3Id",
//				SearchOperator.OPERATOR_IN.value(),
//				categoryCodeSO.getCateogryL3WithoutCompTypeAndRestrictionSQ(), 
//				SearchCondition.CONDITION_OR.value()));
//		
//		searchBean.getDataFilterBeans().add(ctMulti2SearchBean);
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
	
	/**
	 * To get Operate From Home Status Drop Down(Yes / No)
	 * @return
	 */
	public SelectItem[] getOperateFromHomeStatus() {
		return PmCommonUtil.getYesNoOptions();
	}		
	
	/**
	 * To Retrieve Supplier Type
	 */
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
	
	/**
	 * To bold the wording if the wording contains search description.
	 * 
	 * @param desc
	 * @return String
	 */
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
	
	/**
	 * To Construct Machine Dialog
	 * 
	 * @param categoryL3
	 */
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
	
	/**
	 * To get Amount Decimal Format
	 * 
	 * @return DecimalFormat
	 */
	private DecimalFormat getAmountDecimalFormat(){		
		if(amountDF == null){
		   CommonUtils commonUtils = new CommonUtils();
		   amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		}
		return amountDF;
	}
	
	/**
	 * Sort PmCategoryL2 List
	 *
	 * @param pmCategoryL2s the pm category l2 list
	 */
	private void sortPmCategoryL2List(List<PmCategoryL2> pmCategoryL2s) {
		Comparator<PmCategoryL2> comparator = new Comparator<PmCategoryL2>() {
			@Override
			public int compare(PmCategoryL2 o1, PmCategoryL2 o2) {
				return new CompareToBuilder().append(o1.getCategoryL2Code(),
						o2.getCategoryL2Code()).toComparison();
			}
		};
		Collections.sort(pmCategoryL2s, comparator);
	}
	
	/**
	 * Sort PmCategoryL3 List
	 *
	 * @param pmCategoryL3s the pm category l3 list
	 */
	private void sortPmCategoryL3List(List<PmCategoryL3> pmCategoryL3s) {
		Comparator<PmCategoryL3> comparator = new Comparator<PmCategoryL3>() {
			@Override
			public int compare(PmCategoryL3 o1, PmCategoryL3 o2) {
				return new CompareToBuilder().append(o1.getCategoryL3Code(),
						o2.getCategoryL3Code()).toComparison();
			}
		};
		Collections.sort(pmCategoryL3s, comparator);
	}
	
	//start #29665
	/**
	 * String getUpdateContent
	 * 
	 * @param level the level of the category to update
	 * @return the update content string for the specified category level, or an
	 *         empty string if the level is not recognized
	 */
	public String getUpdateContent(Integer level) {
		if (level == Integer.parseInt(ParameterConst.CATEGORY_LEVEL_1)) {
			return PmCommonUtil.getMessage("lbl.pm.categ.update.main", new String[] { this.getManagedBean()
					.getPmCategoryL1().getCategoryL1Code() });
		} else if (level == Integer.parseInt(ParameterConst.CATEGORY_LEVEL_2)) {
			return PmCommonUtil.getMessage("lbl.pm.categ.update.sub", new String[] { this.getManagedBean()
					.getPmCategoryL2().getCategoryL2Code() });
		} else if (level == Integer.parseInt(ParameterConst.CATEGORY_LEVEL_3)) {
			return PmCommonUtil.getMessage("lbl.pm.categ.update.division", new String[] { this.getManagedBean()
					.getPmCategoryL3().getCategoryL3Code() });
		} else {
			return ""; // suppose not happened here
		}
	}
	
	/**
	 * Enables the editing of the description and remarks fields.
	 * <p>
	 * This method sets the flags that control the editability of the 
	 * description and remark fields to false, allowing them to be edited.
	 */
	public void enableDiscription(){
		this.descriptionEditDisable = false;
		this.remarkDisable = false;
	}
	
	/**
	 * To reset description and remarks text area to be disable when user cancel
	 * the popup update.
	 * 
	 * @param level
	 *            the level of the category to cancel (1 for main category, 2
	 *            for subcategory, 3 for subdivision)
	 */
	public void cancelCategory(Integer level) {
		this.descriptionEditDisable = true;
		this.remarkDisable = true;
		this.getManagedBean().setRemarkCategory(null);
		if (level == Integer.parseInt(ParameterConst.CATEGORY_LEVEL_1)) {
			editCategory(this.getManagedBean().getPmCategoryL1().getCategoryL1Id());
		} else if (level == Integer.parseInt(ParameterConst.CATEGORY_LEVEL_2)) {
			editSubCategory(this.getManagedBean().getPmCategoryL2().getCategoryL2Id(), level, false);
		} else if (level == Integer.parseInt(ParameterConst.CATEGORY_LEVEL_3)) {
			editSubDivision(this.getManagedBean().getPmCategoryL3().getCategoryL3Id(), level);
		}
	}
	
	/**
	 * The void onChangeDescription
	 * <p>
	 * This method checks if the category is not new and if the remark field is
	 * currently disabled. If both conditions are met, it enables the remark
	 * field by setting `remarkDisable` to false.
	 */
	public void onChangeDescription() {
		if (!this.getManagedBean().isNewCategory() && this.remarkDisable) {
			this.remarkDisable = false;
		}
	}
	
	private List<PmRemarkCategory> updateCreatedUserName(List<PmRemarkCategory> remarkList) {
		for(PmRemarkCategory rc: remarkList){
			PmUser user = PmCommonUtil.retrieveUserById(rc.getCreatedBy(),true);
			rc.setCreatedUsername(user.getUserName());
		}
		return remarkList;
	}
	
	/**
	 * String getParamCategRestr
	 * 
	 * @param parameterCode
	 * @return parameter discription
	 */
	public String getParamCategRestr(String parameterCode) {
		return ParameterHelper.getInstance().getParameterDesc(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE, parameterCode);
	}
	
	/**
	 * To enable/disable tab based on when tick box at level 3 screen
	 */
	private void updateTabIndex() {
		if ((this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue() && this.getManagedBean()
				.getPmCategoryL3().isByCompType())
				|| (this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue())) {
			this.tabIndexActive = 0;
		} else if (!this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue()
				&& this.getManagedBean().getPmCategoryL3().isByCompType()) {
			this.tabIndexActive = 1;
		} else {
			this.tabIndexActive = 2;
		}
	}
	//end 29665

	/**
	 * @return the result
	 */
	public boolean isResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	/**
	 * @return the originalCategoryName
	 */
	public String getOriginalCategoryName() {
		return originalCategoryName;
	}

	/**
	 * @param originalCategoryName the originalCategoryName to set
	 */
	public void setOriginalCategoryName(String originalCategoryName) {
		this.originalCategoryName = originalCategoryName;
	}

	/**
	 * @return the categoryNameChanged
	 */
	public boolean isCategoryNameChanged() {
		return categoryNameChanged;
	}

	/**
	 * @param categoryNameChanged the categoryNameChanged to set
	 */
	public void setCategoryNameChanged(boolean categoryNameChanged) {
		this.categoryNameChanged = categoryNameChanged;
	}

	/**
	 * @return the amountDF
	 */
	public DecimalFormat getAmountDF() {
		return amountDF;
	}

	/**
	 * @param amountDF the amountDF to set
	 */
	public void setAmountDF(DecimalFormat amountDF) {
		this.amountDF = amountDF;
	}

	/**
	 * @return the facilityTypeSearchBean
	 */
	public ISearchBean<PmParameterDesc> getFacilityTypeSearchBean() {
		return facilityTypeSearchBean;
	}

	/**
	 * @param facilityTypeSearchBean the facilityTypeSearchBean to set
	 */
	public void setFacilityTypeSearchBean(
			ISearchBean<PmParameterDesc> facilityTypeSearchBean) {
		this.facilityTypeSearchBean = facilityTypeSearchBean;
	}

	/**
	 * @return the parameterDescMap
	 */
	public Map<String, List<PmParameterDesc>> getParameterDescMap() {
		return parameterDescMap;
	}

	/**
	 * @param parameterDescMap the parameterDescMap to set
	 */
	public void setParameterDescMap(
			Map<String, List<PmParameterDesc>> parameterDescMap) {
		this.parameterDescMap = parameterDescMap;
	}

	/**
	 * @param searchCategory the searchCategory to set
	 */
	public void setSearchCategory(IPagingDataModel<Object[]> searchCategory) {
		this.searchCategory = searchCategory;
	}
//	public void initialCategoryCode(){
//		getManagedBean().setPmMachineSection(new PmMachineSection());
//		getManagedBean().getPmMachineSection().setCategoryType(new PmParameter());
//	}
	
	private List<String> getMachineSection(List<PmMachineSection> list){
		List<String> strList = new ArrayList<String>();
		if(list.size()>0 && list!= null){
			for(PmMachineSection sc: list){
				if(sc.getSection()!=null){
					strList.add(sc.getSection().getParameterId().toString());		
				}
			}
		}
		return strList;
	}

	//start #29665
	public boolean isRemarkDisable() {
		return remarkDisable;
	}

	public void setRemarkDisable(boolean remarkDisable) {
		this.remarkDisable = remarkDisable;
	}

	public boolean isDescriptionEditDisable() {
		return descriptionEditDisable;
	}

	public void setDescriptionEditDisable(boolean descriptionEditDisable) {
		this.descriptionEditDisable = descriptionEditDisable;
	}
	
	public Integer getTabIndexActive() {
		return tabIndexActive;
	}

	public void setTabIndexActive(Integer tabIndexActive) {
		this.tabIndexActive = tabIndexActive;
	}

	//end #29665
	
	//#29665 - Start
	/**
	 * void validateRequiredField
	 * @param uiComponent
	 * @param value
	 * @param facesContext
	 * @throws ValidatorException if the input value is blank
	 */
	public void validateRequiredField(FacesContext facesContext, UIComponent uiComponent, Object value) {

		String remarkValue = value.toString();

		String labelNameVal = (String) uiComponent.getAttributes().get("labelName");

		if (StringUtils.isBlank(remarkValue)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Required field error",
					PmCommonUtil.getMessage("lbl.pm.categ.remark.required", new String[] { labelNameVal })));
		}
	}
	//#29665 - End
	
	//Bug #37757 - Start
	/**
	 * prepare category code data
	 */
	private void prepareCategoryData() {
		IParameterSO parameterSO;
		parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IParameterSO.class);

		this.getManagedBean().getPmCategoryL3()
				.setCategoryName(this.getManagedBean().getPmCategoryL3().getCategoryName().toUpperCase());
		this.getManagedBean().getPmCategoryL3()
				.setBasicCategoryReq(this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue() ? 1 : 0);
		this.getManagedBean().getPmCategoryL3()
				.setHomeOperated(this.getManagedBean().getPmCategoryL3().isHomeOperatedValue() ? 1 : 0);
		this.getManagedBean().getPmCategoryL3()
				.setRecordStatusFlg(this.getManagedBean().getPmCategoryL3().getRecordStatusFlg());

		if (this.getManagedBean().isNewCategory()) {
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			// supplier type
			parameterReqDTO.setParameterId(this.getManagedBean().getPmCategoryL3().getSupplierTypeCodeVal());
			ParameterResDTO parameterResDTO = parameterSO.retrieveParameterById(parameterReqDTO);
			this.getManagedBean().getPmCategoryL3().setSupplierTypeCode(parameterResDTO.getPmParameter());

			// Special Category
			parameterReqDTO.setParameterId(this.getManagedBean().getPmCategoryL3().getCategoryTypeCodeVal());
			parameterResDTO = parameterSO.retrieveParameterById(parameterReqDTO);
			this.getManagedBean().getPmCategoryL3().setCategoryTypeCode(parameterResDTO.getPmParameter());
		}
	}
    
	/**
	 * validate new and duplicate category code
	 */
	private void validateNewAndDuplicateCategory() {
		// start #29665 check is it new category
		if (!this.getManagedBean().isNewCategory()) {
			this.getManagedBean().setPmCategoryL2(this.getManagedBean().getPmCategoryL3().getPmCategoryL2());
		}
		// end #29665 check is it new category

		// check the duplicate restriction
		if (!checkCategoryRestrDuplicateCompanyType()) {
			throw new EpException(MessageConst.ERROR_775006);
		}
	}

	/**
	 * validate SsmExclusive if ssmExclusive is null
	 */
	private boolean validateSsmExclusive() {
		return this.getManagedBean().getPmCategoryL3() != null
				&& this.getManagedBean().getPmCategoryL3().getSsmExclusive() == null;
	}

	/**
	 * validate Category Desc and Name
	 */
	private boolean validateCategoryDescAndName() {
		return !this.descriptionEditDisable
				&& this.getManagedBean().getUpdatedCategoryDesc()
						.compareTo(this.getManagedBean().getOldCategoryL1().getCategoryName()) != 0;
	}
	//Bug #37757 - End
}
