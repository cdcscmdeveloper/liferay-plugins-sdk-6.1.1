/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.pm.cc;

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
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.cc.CategoryCodeSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
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
}
