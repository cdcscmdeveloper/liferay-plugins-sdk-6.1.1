/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.pm.cc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.sm.search.ISupplierCategorySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.search.SupplierCategoryReqDTO;
import my.ep.dto.sm.search.SupplierCategoryResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.cc.CategoryCodeSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * Common Category Code Search Backing Bean
 * 
 * @author Kenny
 * @since 11.03.2015
 */
@ManagedBean
@RequestScoped
public class CategoryCodeSearchBackingBean extends BaseBackingBean<CategoryCodeSearchManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Logger log = EpLogger.getLogger(CategoryCodeSearchBackingBean.class);
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }

	public CategoryCodeSearchBackingBean() {
	}
	
	public void searchAction() {
		debugLogger("searchAction");
		
		getCategoryDescrDataModel().reloadData();
	}
	
	public void resetAction() {
		debugLogger("resetAction");
		
		getManagedBean().setCategoryDesciption(null);
		getCategoryDescrDataModel().resetData();
	}
	
	public void categorySearchAction(String supplierType, String facilityType, String paidUpCapital, String bumiPercentage, String categoryType, String businessTypeCode,String catcodeL1, Long supplierId) {
		debugLogger("searchAction");
		
		log.info("supplierType: " + supplierType);
		log.info("facilityType: " + facilityType);
		log.info("paidUpCapital: " + paidUpCapital);
		log.info("bumiPercentage: " + bumiPercentage);
		log.info("categoryType: " + categoryType);
		log.info("supplierType: " + businessTypeCode);
		
		this.getManagedBean().setSupplierType(supplierType);
		this.getManagedBean().setFacilityType(facilityType);
		this.getManagedBean().setPaidUpCapital(paidUpCapital);
		this.getManagedBean().setBumiPercentage(bumiPercentage);
		this.getManagedBean().setCategoryType(categoryType);
		this.getManagedBean().setBusinessTypeCode(businessTypeCode);
		this.getManagedBean().setCatcodeL1(catcodeL1);
		
		//32629 - Start
		this.getManagedBean().setSupplierId(supplierId);
		//32629 - End
		
		getCategoryDataModel().reloadData();
	}
	
	public void categoryResetAction() {
		debugLogger("resetAction");
		
		getManagedBean().setCategoryDesciption(null);
		getManagedBean().setCategoryCode(null);
		getCategoryDataModel().resetData();
	}
	
	public void okAction(String methodActionStr) {
		debugLogger("okAction");
		log.info("methodActionStr: " + methodActionStr);
		
		if (StringUtils.isNotBlank(methodActionStr)) {
			MethodExpression methodExp = FacesScopes
	                .createMethodExpression(FacesContext.getCurrentInstance(),
	                		methodActionStr, null,
	                        new Class[] { PmCategoryL3.class });
			
			String searchBy = getManagedBean().getSearchBy();
			PmCategoryL3 selectedCategoryL3 = null;
			
			if (ParameterConst.CATEGORY_CODE_SEARCH_BY_CODE.equals(searchBy)) {
				if (getManagedBean().getCategoryCodeIdLvl1() == null) {
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(
									"lbl.sc.main.category", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale())});
				} else if (getManagedBean().getCategoryCodeIdLvl2() == null) {
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(
									"lbl.sc.sub.category", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale())});
				} else if (getManagedBean().getCategoryCodeIdLvl3() == null) {
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(
									"lbl.sc.sub.category.division", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale())});
				} else {
					ICategoryCodeSO categoryCodeSO = null;
					try {
						categoryCodeSO = BusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
										ICategoryCodeSO.class);

						CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
						reqDTO.setCategoryL3Id(getManagedBean().getCategoryCodeIdLvl3());
						CategoryCodeResDTO resDTO = categoryCodeSO
								.retrieveCategoryL3DtlById(reqDTO);

						selectedCategoryL3 = resDTO.getCategoryL3();
					} finally {
						categoryCodeSO = null;
					}
				}
			} else {
				selectedCategoryL3 = getManagedBean().getSelectedCategoryL3();
				
				if (selectedCategoryL3 == null) {
					throw new EpException(MessageConst.ERROR_760249);
				}
			}
			
			methodExp.invoke(
					FacesContext.getCurrentInstance().getELContext(), 
					new Object[]{selectedCategoryL3});
			
			RequestContext.getCurrentInstance().execute("catCodePopup.hide()");
		}
	}
	
	public void cancelAction() {
		debugLogger("cancelAction");
	}
	
	public SelectItem[] getLevel1CategoryCodes() {
		debugLogger("getLevel1CategoryCodes");
		
		SelectItem[] items = null;

		ICategoryCodeSO categoryCodeSO = null;
		try {
			categoryCodeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);

			List<PmCategoryL1> pmCategoryL1s = categoryCodeSO
					.retrieveCategoryL1List()
					.getCategoryL1List();

			items = new SelectItem[pmCategoryL1s.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			
			int i = 1;
			for (PmCategoryL1 pmCategoryL1 : pmCategoryL1s) {
				items[i++] = new SelectItem(pmCategoryL1.getCategoryL1Id(),
						pmCategoryL1.getCategoryL1Code() + " - " + pmCategoryL1.getCategoryName());
			}
		} finally {
			categoryCodeSO = null;
		}
		
		return items;
	}
	
	public SelectItem[] getLevel2CategoryCodes() {
		debugLogger("getLevel2CategoryCodes");
		
		Long categoryCodeIdLvl1 = getManagedBean().getCategoryCodeIdLvl1();
		SelectItem[] items = null;
		
		if (categoryCodeIdLvl1 != null) {
			ICategoryCodeSO categoryCodeSO = null;
			try {
				categoryCodeSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICategoryCodeSO.class);

				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setCategoryL1Id(categoryCodeIdLvl1);
				List<PmCategoryL2> pmCategoryL2s = categoryCodeSO
						.retrieveCategoryL2ListByL1Id(reqDTO)
						.getCategoryL2List();

				items = new SelectItem[pmCategoryL2s.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
				
				int i = 1;
				for (PmCategoryL2 pmCategoryL2 : pmCategoryL2s) {
					items[i++] = new SelectItem(pmCategoryL2.getCategoryL2Id(),
							pmCategoryL2.getCategoryL2Code() + " - " + pmCategoryL2.getCategoryName());
				}
			} finally {
				categoryCodeSO = null;
			}	
		}
		
		return items;
	}
	
	public SelectItem[] getLevel3CategoryCodes() {
		debugLogger("getLevel3CategoryCodes");
		
		Long categoryCodeIdLvl2 = getManagedBean().getCategoryCodeIdLvl2();
		SelectItem[] items = null;

		if (categoryCodeIdLvl2 != null) {
			ICategoryCodeSO categoryCodeSO = null;
			try {
				categoryCodeSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICategoryCodeSO.class);

				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setCategoryL2Id(categoryCodeIdLvl2);
				List<PmCategoryL3> pmCategoryL3s = categoryCodeSO
						.retrieveCategoryL3ListByL2Id(reqDTO)
						.getCategoryL3List();

				items = new SelectItem[pmCategoryL3s.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
				
				int i = 1;
				for (PmCategoryL3 pmCategoryL3 : pmCategoryL3s) {
					items[i++] = new SelectItem(pmCategoryL3.getCategoryL3Id(),
							pmCategoryL3.getCategoryL3Code() + " - " + pmCategoryL3.getCategoryName());
				}
			} finally {
				categoryCodeSO = null;
			}
		}
		
		return items;
	}
	
	public IPagingDataModel<PmCategoryL3> getCategoryDescrDataModel() {
		IPagingDataModel<PmCategoryL3> categoryDescrDataModel = getManagedBean().getCategoryDescrDataModel();
		
		if (categoryDescrDataModel == null) {
			categoryDescrDataModel = new CategoryDescrPagingDataModel();
			getManagedBean().setCategoryDescrDataModel(categoryDescrDataModel);
		}
		
		return categoryDescrDataModel;
	}
	
	private class CategoryDescrPagingDataModel extends PagingDataModel<PmCategoryL3> {
		private static final long serialVersionUID = 1L;

		public CategoryDescrPagingDataModel() {
			super("catDescrTbl", false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmCategoryL3> pageChangeListener(
				ISearchBean<PmCategoryL3> searchBean) {
			debugLogger("pageChangeListener");

			ICategoryCodeSO categoryCodeSO;
			try {
				categoryCodeSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
								ICategoryCodeSO.class);
				
				String categoryDesciption = getManagedBean().getCategoryDesciption();
				BaseSearchFilterBean l1SearchFilterBean = new SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryName",
						SearchOperator.OPERATOR_LIKE.value(),
						categoryDesciption,
						true);
				//l1SearchFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
				BaseSearchFilterBean l2SearchFilterBean = new SearchFilterBean("pmCategoryL2.categoryName",
						SearchOperator.OPERATOR_LIKE.value(),
						categoryDesciption,
						true);
				l2SearchFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
				BaseSearchFilterBean l3SearchFilterBean = new SearchFilterBean("categoryName",
						SearchOperator.OPERATOR_LIKE.value(),
						categoryDesciption,
						true);
				l3SearchFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
				
				// Filter By - category level 3 record status
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				// Filter By - category level 2 record status
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmCategoryL2.recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				// Filter By - category level 1 record status
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmCategoryL2.pmCategoryL1.recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				searchBean.getDataFilterBeans().add(
						new SearchMultiFilterBean(
								new SearchMultiFilterBean(
										l1SearchFilterBean,
										l2SearchFilterBean),
								l3SearchFilterBean));

				// Order by categoryName
				searchBean.getSortColumns().add(
						new SearchOrderBean("categoryName"));

				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setSearchBean(searchBean);
				CategoryCodeResDTO resDTO = categoryCodeSO.searchAllCategoryL3(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getCategoryL3ResultBean();
				}
			} finally {
				categoryCodeSO = null;
			}

			return null;
		}
	}
	
	public IPagingDataModel<PmCategoryL3> getCategoryDataModel() {
		IPagingDataModel<PmCategoryL3> categoryDataModel = getManagedBean().getCategoryDataModel();
		
		if (categoryDataModel == null) {
			categoryDataModel = new CategoryPagingDataModel();
			getManagedBean().setCategoryDataModel(categoryDataModel);
		}
		
		return categoryDataModel;
	}
	
	private class CategoryPagingDataModel extends PagingDataModel<PmCategoryL3> {
		private static final long serialVersionUID = 1L;

		public CategoryPagingDataModel() {
			super("catDescrTbl", false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmCategoryL3> pageChangeListener(
				ISearchBean<PmCategoryL3> searchBean) {
			debugLogger("pageChangeListener");

			ICategoryCodeSO categoryCodeSO;
			try {
				categoryCodeSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
								ICategoryCodeSO.class);
				
				String categoryDesciption = getManagedBean().getCategoryDesciption();
				if(!categoryDesciption.isEmpty()) {
					BaseSearchFilterBean l1SearchFilterBean = new SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryName",
							SearchOperator.OPERATOR_LIKE.value(),
							categoryDesciption,
							true);
					l1SearchFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
					BaseSearchFilterBean l2SearchFilterBean = new SearchFilterBean("pmCategoryL2.categoryName",
							SearchOperator.OPERATOR_LIKE.value(),
							categoryDesciption,
							true);
					l2SearchFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
					BaseSearchFilterBean l3SearchFilterBean = new SearchFilterBean("categoryName",
							SearchOperator.OPERATOR_LIKE.value(),
							categoryDesciption,
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
				
				String categoryCode = getManagedBean().getCategoryCode();
				if(!categoryCode.isEmpty()) {
					String l1Code = categoryCode.substring(0, 2);
					String l2Code = categoryCode.substring(2, 4);
					String l3Code = categoryCode.substring(4, 6);
					
					
					BaseSearchFilterBean l1CodeSearchFilterBean = new SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryL1Code",
							SearchOperator.OPERATOR_EQUALS.value(),
							l1Code,
							true);
					BaseSearchFilterBean l2CodeSearchFilterBean = new SearchFilterBean("pmCategoryL2.categoryL2Code",
							SearchOperator.OPERATOR_EQUALS.value(),
							l2Code,
							true);
					BaseSearchFilterBean l3CodeSearchFilterBean = new SearchFilterBean("categoryL3Code",
							SearchOperator.OPERATOR_EQUALS.value(),
							l3Code,
							true);
					
					searchBean.getDataFilterBeans().add(l1CodeSearchFilterBean);
					searchBean.getDataFilterBeans().add(l2CodeSearchFilterBean);
					searchBean.getDataFilterBeans().add(l3CodeSearchFilterBean);					
				}
				
				// Order by categoryName
//				searchBean.getSortColumns().add(
//						new SearchOrderBean("categoryName"));

				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setSearchBean(searchBean);

				if(getManagedBean().getSupplierType() != null && getManagedBean().getSupplierType().length() > 0) {
					reqDTO.setSupplierTypeCode(getManagedBean().getSupplierType());
				}
				if(getManagedBean().getFacilityType() != null && getManagedBean().getFacilityType().length() > 0) {
					if (getManagedBean().getFacilityType().equalsIgnoreCase("H")) {
						reqDTO.setHomeOperated(1);
					} else {
						reqDTO.setHomeOperated(0);
					}
				}
				if(getManagedBean().getPaidUpCapital() != null && getManagedBean().getPaidUpCapital().length() > 0) {
					reqDTO.setPaidCapital(new BigDecimal(getManagedBean().getPaidUpCapital()));
				}
//				if(getBumiPercentage() != null && getBumiPercentage().length() > 0) {
//					Integer bumi = Integer.parseInt(getBumiPercentage());
//					reqDTO.setBumiPercentage(bumi);
//				}
				if(getManagedBean().getCategoryType() != null && getManagedBean().getCategoryType().length() > 0) {
					if (getManagedBean().getCategoryType().equals("N")) {
						reqDTO.setIsSpecialCategory(false);
					} else {
						reqDTO.setIsSpecialCategory(true);
					}
				}
				if(getManagedBean().getBusinessTypeCode() != null && getManagedBean().getBusinessTypeCode().length() > 0) {
					reqDTO.setBusinessTypeCode(getManagedBean().getBusinessTypeCode());
				}
				reqDTO.setMainCatCode(getManagedBean().getCatcodeL1());
				
				log.info("supplierType: " + getManagedBean().getSupplierType());
				log.info("facilityType: " + getManagedBean().getFacilityType());
				log.info("paidUpCapital: " + getManagedBean().getPaidUpCapital());
				log.info("bumiPercentage: " + getManagedBean().getBumiPercentage());
				log.info("categoryType: " + getManagedBean().getCategoryType());
				log.info("supplierType: " + getManagedBean().getBusinessTypeCode());
				
				CategoryCodeResDTO resDTO = categoryCodeSO.searchCategoryL3(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getCategoryL3ResultBean();
				}
			} finally {
				categoryCodeSO = null;
			}

			return null;
		}
	}
	/**
	 * to retrieve L1 Category Codes in dropdown by the following parameters
	 * @param supplierType
	 * @param facilityType
	 * @param paidUpCapital
	 * @param bumiPercentage
	 * @param categoryType
	 * @param businessTypeCode
	 * @return
	 */
	public List<SelectItem> retrieveCategoryL1List(
			String supplierType, String facilityType, BigDecimal paidUpCapital, 
			Integer bumiPercentage, String categoryType, String businessTypeCode,String catcodeL1) {
		
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		log.info("** getSupplierType type ***"
				+ supplierType);
		log.info("** getApplCategoryType type ***" + categoryType);
		reqDTO.setSupplierTypeCode(supplierType);
		reqDTO.setBusinessTypeCode(businessTypeCode);
		if (facilityType.equalsIgnoreCase("H")) {
			reqDTO.setHomeOperated(1);
		} else {
			reqDTO.setHomeOperated(0);
		}
		if (paidUpCapital != null) {
			reqDTO.setPaidCapital(paidUpCapital);
		}
		Integer bumiPerc = new Integer(0);
		if (bumiPercentage != null) {
			//SONARQUBE PM BUG MINOR AZZA G51
			bumiPerc = bumiPercentage;
			if (bumiPerc.compareTo(51) >= 0 && bumiPerc.compareTo(100) < 0) {
				bumiPerc = 99;
			}
		}
		if (categoryType.equals("N")) {
			reqDTO.setIsSpecialCategory(false);
		} else {
			reqDTO.setIsSpecialCategory(true);
		}
		log.info("*** Bumi Percent Sent for Category L1 ***** " + bumiPerc);
		reqDTO.setBumiPercentage(bumiPerc);
		reqDTO.setMainCatCode(catcodeL1);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL1s(reqDTO);
//		System.out.println("resDTO.getCategoryL1List(): " + resDTO.getCategoryL1List());		
		
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		for(PmCategoryL1 l1 : resDTO.getCategoryL1List()) {
			log.info("l1.getCategoryName(): " + l1.getCategoryName());
			selectItems.add(new SelectItem(l1.getCategoryL1Id(), l1.getCategoryL1Code() + " - " + l1.getCategoryName()));
		}		
		return selectItems;
	}
	
	/**
	 * to retrieve L2 Category Codes in dropdown by the following parameters
	 * @param categoryL1Id
	 * @param supplierType
	 * @param facilityType
	 * @param paidUpCapital
	 * @param bumiPercentage
	 * @param categoryType
	 * @param businessTypeCode
	 * @return
	 */
	public List<SelectItem> retrieveCategoryL2List(
			long categoryL1Id, 
			String supplierType, String facilityType, BigDecimal paidUpCapital, 
			Integer bumiPercentage, String categoryType, String businessTypeCode) {
		
		if(categoryL1Id == 0) {
			return new ArrayList<SelectItem>();
		}
		
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		reqDTO.setSupplierTypeCode(supplierType);
		if (facilityType.equalsIgnoreCase("H")) {
			reqDTO.setHomeOperated(1);
		} else {
			reqDTO.setHomeOperated(0);
		}
		if (paidUpCapital != null) {
			reqDTO.setPaidCapital(paidUpCapital);
		}
		if (categoryType.equals("N")) {
			reqDTO.setIsSpecialCategory(false);
		} else {
			reqDTO.setIsSpecialCategory(true);
		}
		Integer bumiPerc = new Integer(0);
		if (bumiPercentage != null) {
			//SONARQUBE PM BUG MINOR AZZA G51
			bumiPerc = bumiPercentage;
			if (bumiPerc.compareTo(51) >= 0 && bumiPerc.compareTo(100) < 0) {
				bumiPerc = 99;
			}
		}
		reqDTO.setBusinessTypeCode(businessTypeCode);
		log.info("*** Bumi Percent Sent for Category L2 ***** " + bumiPerc);
		reqDTO.setBumiPercentage(bumiPerc);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2s(reqDTO);

		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		for(PmCategoryL2 l2 : resDTO.getCategoryL2List()) {
			selectItems.add(new SelectItem(l2.getCategoryL2Id(), l2.getCategoryL2Code() + " - " + l2.getCategoryName()));
		}
		
		return selectItems;
	}
	
	/**
	 * to retrieve L3 Category Codes in dropdown by the following parameters
	 * @param categoryL2Id
	 * @param supplierType
	 * @param facilityType
	 * @param paidUpCapital
	 * @param bumiPercentage
	 * @param categoryType
	 * @param businessTypeCode
	 * @return
	 */
	//26238 - approver cannot add imposed category code - start
	//#33387 - START
	public List<SelectItem> retrieveCategoryL3List(
			long categoryL2Id, 
			String supplierType, String facilityType, BigDecimal paidUpCapital, 
			Integer bumiPercentage, String categoryType, Long supplierId, String businessTypeCode, List<SmSupplierCategory> activeCategoryList) {
		//#33387 - END
		if(categoryL2Id == 0) {
			return new ArrayList<SelectItem>();
		}
		
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(categoryL2Id);
		log.info("** getSupplierType type ***"
				+ supplierType);
		log.info("** getApplCategoryType type ***"
				+ categoryType);
		reqDTO.setSupplierTypeCode(supplierType);
		reqDTO.setBusinessTypeCode(businessTypeCode);
		if (categoryType.equals("N")) {
			reqDTO.setIsSpecialCategory(false);
		} else {
			reqDTO.setIsSpecialCategory(true);
		}
		if (facilityType.equalsIgnoreCase("H")) {
			reqDTO.setHomeOperated(1);
		} else {
			reqDTO.setHomeOperated(0);
		}
		if (paidUpCapital != null) {
			reqDTO.setPaidCapital(paidUpCapital);
		}
		Integer bumiPerc = new Integer(0);
		if (bumiPercentage != null) {
			//SONARQUBE PM BUG MINOR AZZA G51
			bumiPerc = bumiPercentage;
			if (bumiPerc.compareTo(51) >= 0 && bumiPerc.compareTo(100) < 0) {
				bumiPerc = 99;
			}
		}
		log.info("*** Bumi Percent Sent for Category L3 ***** " + bumiPerc);
		reqDTO.setBumiPercentage(bumiPerc);
		CategoryCodeResDTO resDTO = SmServiceLookup.getCategoryCodeSO().retrieveCategoryL3ListByL2Id(reqDTO);

		List<SelectItem> selectItems;
		//27438, 27613, 27673: Disciplinary Action - start
		SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
		
		if(currSupp != null && currSupp.getSmAppl() != null){
			
			//27738 Start - System allow to add 221709 without basic category
			MofRegistrationReqDTO reqDTO1 = new MofRegistrationReqDTO();
			SmAppl activeAppl = SmCommonUtil.getActiveApplBySupplier(currSupp);
			if (activeAppl != null){
				currSupp.setSmAppl(activeAppl);
			}
			reqDTO1.setApplNo(currSupp.getSmAppl().getApplNo());
			SmAppl srcAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmApplWithChildByApplNo(reqDTO1).getSmAppl();
			currSupp.setSmAppl(srcAppl);
			
			List<SmDisciplinaryAction> activeDAs = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(currSupp);
			List<SmSupplierCategory> apprvList = getApprovedCategories(currSupp, activeDAs);
			getManagedBean().setApprovedCategoryList(apprvList);
			
			selectItems = getSelectItems(resDTO.getCategoryL3List(), activeDAs, currSupp.getSupplierId());
			//27738 end 
		} else { 
			//27859,30720 Start - 3rd level category didnot appear
				// #33387 - START
				List<SmSupplierCategory> apprvList = new ArrayList<>();

				getManagedBean().setSmAppl(SmApplicationUtil.getLatestApplBySupplierId(supplierId));
				ISupplierCategorySO supplierCategorySO = (ISupplierCategorySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierCategorySO.class);

				SupplierCategoryReqDTO suppCatReqDTO = new SupplierCategoryReqDTO();
				SupplierCategoryResDTO suppCatResDTO = new SupplierCategoryResDTO();
				suppCatReqDTO.setSupplierId(supplierId);
				suppCatResDTO = supplierCategorySO.retrieveCategoryBySupplierId(suppCatReqDTO);

				List<Object[]> catList = null;

				if (null != suppCatResDTO) {
					catList = suppCatResDTO.getResultList();
				}

				for (Object[] arr : catList) {
					SmSupplierCategory cat = new SmSupplierCategory();

					PmCategoryL1 pmCategoryL1 = new PmCategoryL1();
					PmCategoryL2 pmCategoryL2 = new PmCategoryL2();
					PmCategoryL3 pmCategoryL3 = new PmCategoryL3();

					pmCategoryL1.setCategoryL1Code((String) arr[3]);
					pmCategoryL2.setCategoryL2Code((String) arr[4]);
					pmCategoryL3.setCategoryL3Code((String) arr[5]);

					cat.setPmCategoryL1(pmCategoryL1);
					cat.setPmCategoryL2(pmCategoryL2);
					cat.setPmCategoryL3(pmCategoryL3);

					BigDecimal recordStatus = (BigDecimal) arr[7];
					if (recordStatus.compareTo(BigDecimal.ONE) == 0) {
						cat.setRecordStatus(true);
					} else if (recordStatus.compareTo(BigDecimal.ZERO) == 0) {
						cat.setRecordStatus(false);
					}

					if (cat.getRecordStatusFlg().compareTo(SupplierManagementConst.APPROVED_RECORD_STATUS) == 0 || cat.getRecordStatusFlg().compareTo(SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
						// 28327: Disciplinary Action - start - <28327>
						apprvList.add(cat);
						// 28327: Disciplinary Action - start - <28327>
					}
				}

				// #33387 - Add null checking
				if (activeCategoryList != null) {
					apprvList.addAll(activeCategoryList);
					SmApplicationUtil.sortSupplierCategory(apprvList);
					getManagedBean().setApprovedCategoryList(apprvList);
					
					// #33387 - Add null checking
				}
				List<SmDisciplinaryAction> activeDAs = SmServiceLookup.getSupplierDataMaintenanceSO()
						.retrieveActiveDisciplinaryActionBySupplierId(supplierId).getSmDisciplinaryActions();
				
				selectItems = getSelectItems(resDTO.getCategoryL3List(), activeDAs, null);
			//27859,30720 end - 3rd level category didnot appear
			//#33387 - END
		}
		//27438, 27613, 27673: Disciplinary Action - end
		
		return selectItems;
	}
	
	//#26238 - start
	private List<SmSupplierCategory> getApprovedCategories(SmSupplier currSupp, List<SmDisciplinaryAction> activeDAs){
		if (currSupp == null || currSupp.getSmAppl() == null){
			return new ArrayList<>();
		}
		
		List<SmSupplierCategory> apprvList = new ArrayList<>();
		for (SmSupplierCategory cat : currSupp.getSmAppl().getSmSupplierCategories()) {
			 if (cat.getRecordStatusFlg().compareTo(SupplierManagementConst.APPROVED_RECORD_STATUS) == 0 
					 || cat.getRecordStatusFlg().compareTo(SupplierManagementConst.PENDING_ACT_STATUS) == 0){
				cat.setEntryMode(cat.getOtherDesc()); 
				
				if(!hasActiveCategoryDisciplinaryAction(cat, activeDAs)){
					apprvList.add(cat);
				}
			}
		}  
		SmApplicationUtil.sortSupplierCategory(apprvList);
		return apprvList;
	}
	
	private boolean hasActiveCategoryDisciplinaryAction(SmSupplierCategory cat, List<SmDisciplinaryAction> activeDAs){
		if (cat == null || EntityHelper.isCollectionEmpty(activeDAs)){
			return false;
		}
		for (SmDisciplinaryAction da : activeDAs){
			boolean isCategoryDA = da.isCancelCategory() || da.isBumiputeraCancel() || da.isManufacturerCancel();
			if (isCategoryDA && da.getDisciplinaryActionId().equals(cat.getDisciplinaryActionId())){
				return true;
			}
		}
		return false;
	}
	
	private List<SelectItem> getSelectItems(List<PmCategoryL3> catL3s, List<SmDisciplinaryAction> activeDAs, 
			Long supplierId){
		
		List<Long> activeDAL3 = getActiveDACategoryL3Ids(activeDAs, supplierId);
		
		boolean hasBumiputeraDA = hasActiveBumiputeraDisciplinaryAction(activeDAs);
		boolean hasAllBasicMTO = hasAllBasicMTOCodes(getManagedBean().getApprovedCategoryList());
		
		List<SelectItem> selectItems = new ArrayList<>();
		for (PmCategoryL3 catL3 : catL3s) {
			SelectItem item = new SelectItem(catL3.getCategoryL3Id(), 
					catL3.getCategoryL3Code() + " - " + catL3.getCategoryName());
			
			boolean isActiveDAL3 = activeDAL3.contains(catL3.getCategoryL3Id());
			boolean isBumiputeraEligible = catL3.getBumiPercentage() > 50 && hasBumiputeraDA;
			// check approved basic category for MTO code
			boolean isMTO = !hasAllBasicMTO && catL3.getCategoryL3Code() != null && catL3.getCategoryL3Id() == 347;

			if (isActiveDAL3 || isBumiputeraEligible || isMTO) {
			    item.setDisabled(true);
			}
			selectItems.add(item);
		}
		return selectItems;
	}
	
	private List<Long> getActiveDACategoryL3Ids(List<SmDisciplinaryAction> activeDAs, Long supplierId){
		List<Long> activeDAL3 = new ArrayList<>();
		if (EntityHelper.isCollectionEmpty(activeDAs)){
			return activeDAL3;
		}
		SmSupplier supplier = new SmSupplier();
		supplier.setSupplierId(supplierId);
		List<Object[]> cancelDA = SmApplicationUtil.retrieveCancelCategoryBySupplier(supplier);
		if (EntityHelper.isCollectionEmpty(cancelDA)){
			return activeDAL3;
		}
		for (Object[] arr : cancelDA){
			for (SmDisciplinaryAction da : activeDAs){
				if (arr[7].toString().equals(da.getDisciplinaryActionId().toString())){
					activeDAL3.add(Long.valueOf(arr[2].toString()));
				}
			}
		}
		return activeDAL3;
	}
	
	private boolean hasActiveBumiputeraDisciplinaryAction(List<SmDisciplinaryAction> activeDAs){
		if (!EntityHelper.isCollectionEmpty(activeDAs)){
			for (SmDisciplinaryAction da : activeDAs){
				if (da.isBumiputeraCancel()){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean hasAllBasicMTOCodes(List<SmSupplierCategory> catList){
		if (EntityHelper.isCollectionEmpty(catList)){
			return false;
		}
		int checkBasic = 0;
		List<String> basicMTO = Arrays.asList("04", "05", "06");
		
		for(SmSupplierCategory cat : catList){
			boolean isSubCategoryMTO = cat.getPmCategoryL1() != null && ("22").equals(cat.getPmCategoryL1().getCategoryL1Code())
					&& cat.getPmCategoryL2() != null && ("17").equals(cat.getPmCategoryL2().getCategoryL2Code());
			
			if(isSubCategoryMTO && cat.getPmCategoryL3() != null && basicMTO.contains(cat.getPmCategoryL3().getCategoryL3Code())){
				checkBasic++;
			}
		}
		return checkBasic == basicMTO.size();
	}
	//26238 - approver cannot add imposed category code - end
 
	public void resetL2L3CategoryCode() {
		this.getManagedBean().setCategoryCodeIdLvl2(null);
		this.getManagedBean().setCategoryCodeIdLvl3(null);
	}

	public void resetAllOnShow() {
		this.getManagedBean().setSearchBy(ParameterConst.CATEGORY_CODE_SEARCH_BY_CODE);
		this.getManagedBean().setCategoryCodeIdLvl1(null);
		resetL2L3CategoryCode();
		categoryResetAction();
	}
	
	//32629 - Start
		/**
		 * Removed DA category code from the list.
		 * 
		 * @param e
		 *            -AjaxBehaviorEvent
		 */
	public void unSelectDACategories(AjaxBehaviorEvent e) {
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(getManagedBean().getSelectedCategoryL3().getPmCategoryL2().getCategoryL2Id());
		reqDTO.setSupplierTypeCode(getManagedBean().getSupplierType());
		reqDTO.setBusinessTypeCode(getManagedBean().getBusinessTypeCode());
		if (("N").equals(getManagedBean().getCategoryType())) {
			reqDTO.setIsSpecialCategory(false);
		} else {
			reqDTO.setIsSpecialCategory(true);
		}
		if (("H").equalsIgnoreCase(getManagedBean().getFacilityType())) {
			reqDTO.setHomeOperated(1);
		} else {
			reqDTO.setHomeOperated(0);
		}
		if (getManagedBean().getPaidUpCapital() != null) {
			reqDTO.setPaidCapital(new BigDecimal(getManagedBean().getPaidUpCapital()));
		}
		
		//27438, 27613, 27673: Disciplinary Action - start
		SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
		if(currSupp != null && currSupp.getSmAppl() != null){
			
			//27738 Start - System allow to add 221709 without basic category
			MofRegistrationReqDTO reqDTO1 = new MofRegistrationReqDTO();
			SmAppl activeAppl = SmCommonUtil.getActiveApplBySupplier(currSupp);
			reqDTO1.setApplNo(activeAppl.getApplNo());
			SmAppl srcAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmApplWithChildByApplNo(reqDTO1).getSmAppl();
			currSupp.setSmAppl(srcAppl);
		
			List<SmDisciplinaryAction> activeDAs = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(currSupp);
			List<SmSupplierCategory> apprvList = getApprovedCategories(currSupp, activeDAs);
			getManagedBean().setApprovedCategoryList(apprvList);
		
			checkSelectedL3(activeDAs, srcAppl);
			//27738 end 
		}
		//27438, 27613, 27673: Disciplinary Action - end
	}
	//32629 - End
	//#26238
	private void checkSelectedL3(List<SmDisciplinaryAction> activeDAs, SmAppl srcAppl){
		if (getManagedBean().getSelectedCategoryL3() == null){
			return;
		}
		PmCategoryL3 selectedL3 = getManagedBean().getSelectedCategoryL3();
		Long selectedL3Id = selectedL3.getCategoryL3Id();
		
		List<Long> activeDAL3 = getActiveDACategoryL3Ids(activeDAs, srcAppl.getSmSupplier().getSupplierId());
		if (activeDAL3.contains(selectedL3Id)){
			getManagedBean().setSelectedCategoryL3(null);
		}
		boolean hasBumiputeraDA = hasActiveBumiputeraDisciplinaryAction(activeDAs);
		
		if (selectedL3.getBumiPercentage() > 50 && (hasBumiputeraDA ||  srcAppl.getSmSupplier().isBumi())){
			getManagedBean().setSelectedCategoryL3(null);
		}
		boolean hasAllBasicMTO = hasAllBasicMTOCodes(getManagedBean().getApprovedCategoryList());
		if (!hasAllBasicMTO && selectedL3Id == 347){
			getManagedBean().setSelectedCategoryL3(null);
		}
	}
	
}
