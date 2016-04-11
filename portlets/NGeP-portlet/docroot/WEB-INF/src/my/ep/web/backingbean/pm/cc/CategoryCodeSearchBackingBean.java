/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.pm.cc;

import java.util.ArrayList;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.cc.CategoryCodeSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
	
	public void categorySearchAction(String supplierType, String facilityType, String paidUpCapital, String bumiPercentage, String categoryType, String businessTypeCode) {
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
				searchBean.getSortColumns().add(
						new SearchOrderBean("categoryName"));

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
					Integer capital = Integer.parseInt(getManagedBean().getPaidUpCapital());
					reqDTO.setPaidUpCapital(capital);
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
			String supplierType, String facilityType, Integer paidUpCapital, 
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
			reqDTO.setPaidUpCapital(paidUpCapital.intValue());
		}
		Integer bumiPerc = new Integer(0);
		if (bumiPercentage != null) {
			bumiPerc = bumiPercentage.intValue();
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
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL1s(reqDTO);
		System.out.println("resDTO.getCategoryL1List(): " + resDTO.getCategoryL1List());		
		
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		List<SelectItem> consultantSelectItems = new ArrayList<SelectItem>();
		for(PmCategoryL1 l1 : resDTO.getCategoryL1List()) {
			System.out.println("l1.getCategoryName(): " + l1.getCategoryName());
			//filter for consultant
			if(!StringUtils.isBlank(supplierType) && supplierType.equals("J")){
				if(!StringUtils.isBlank(catcodeL1) && catcodeL1.equals(l1.getCategoryL1Code())){
					consultantSelectItems.add(new SelectItem(l1.getCategoryL1Id(), l1.getCategoryL1Code() + " - " + l1.getCategoryName()));
				}else{
					selectItems.add(new SelectItem(l1.getCategoryL1Id(), l1.getCategoryL1Code() + " - " + l1.getCategoryName()));
				}
			}else{
				selectItems.add(new SelectItem(l1.getCategoryL1Id(), l1.getCategoryL1Code() + " - " + l1.getCategoryName()));
			}
		}
		
		if(!EntityHelper.isCollectionEmpty(consultantSelectItems)){
			selectItems = consultantSelectItems;
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
			String supplierType, String facilityType, Integer paidUpCapital, 
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
			reqDTO.setPaidUpCapital(paidUpCapital.intValue());
		}
		if (categoryType.equals("N")) {
			reqDTO.setIsSpecialCategory(false);
		} else {
			reqDTO.setIsSpecialCategory(true);
		}
		Integer bumiPerc = new Integer(0);
		if (bumiPercentage != null) {
			bumiPerc = bumiPercentage.intValue();
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
	public List<SelectItem> retrieveCategoryL3List(
			long categoryL2Id, 
			String supplierType, String facilityType, Integer paidUpCapital, 
			Integer bumiPercentage, String categoryType, String businessTypeCode) {

		if(categoryL2Id == 0) {
			return new ArrayList<SelectItem>();
		}
		
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
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
			reqDTO.setPaidUpCapital(paidUpCapital.intValue());
		}
		Integer bumiPerc = new Integer(0);
		if (bumiPercentage != null) {
			bumiPerc = bumiPercentage.intValue();
			if (bumiPerc.compareTo(51) >= 0 && bumiPerc.compareTo(100) < 0) {
				bumiPerc = 99;
			}
		}
		log.info("*** Bumi Percent Sent for Category L3 ***** " + bumiPerc);
		reqDTO.setBumiPercentage(bumiPerc);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);

		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		for(PmCategoryL3 l3 : resDTO.getCategoryL3List()) {
			selectItems.add(new SelectItem(l3.getCategoryL3Id(), l3.getCategoryL3Code() + " - " + l3.getCategoryName()));
		}
		
		return selectItems;
	}
	
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
	
}
