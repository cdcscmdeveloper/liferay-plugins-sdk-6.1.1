/**
 * Copyright © 2015 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.pm.businessnature;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.businessnature.BusinessNatureManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Backing Bean for Business Nature
 * 
 * @author DK
 * @since 15/8/2014
 */
@ManagedBean(name = "businessNatureBackingBean")
@ViewScoped
public class BusinessNatureBackingBean extends
		BaseBackingBean<BusinessNatureManagedBean> {

	private static final Logger log = EpLogger
			.getLogger(BusinessNatureManagedBean.class);

	private IPagingDataModel<PmBusinessNature> businessNatureDataModel = new BusinessNaturePagingDataModel();

	public BusinessNatureBackingBean() {
		log.info("inside constructor");
	}

	/**
	 * Debug Logger
	 * 
	 * @param message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * load clean view
	 */
	public void preLoading() {
		debugLogger("preLoading");
		this.resetBusinessNatureSearch();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "businessNatureMain");
	}

	/**
	 * reload to business nature search page
	 * 
	 */
	public String redirectBusinessSearch() {
		debugLogger("redirectBusinessSearch");

		// this.getManagedBean().setLazy(true);

		return "/xhtml/pm/businessnature/businessNatureMain.xhtml?faces-redirect=true";

	}
	
	/**
	 * Update Business Category Level
	 */
	public void updateBusinessCategoryLevel(){
		businessNatureDataModel.updatePagingResults(null);
	}

	/**
	 * reset search for Business Nature
	 * 
	 */
	public void resetBusinessNatureSearch() {
		debugLogger("resetBusinessNatureSeatch");

		this.getManagedBean().setSecLevelCode(new String());
		this.getManagedBean().setSecLevelName(new String());

		this.getManagedBean().setDivLevelCode(new String());
		this.getManagedBean().setDivLevelName(new String());

		this.getManagedBean().setGroupLevelCode(new String());
		this.getManagedBean().setGroupLevelName(new String());

		this.getManagedBean().setClassLevelCode(new String());
		this.getManagedBean().setClassLevelName(new String());

		this.getManagedBean().setItemLevelCode(new String());
		this.getManagedBean().setItemLevelName(new String());

		this.getManagedBean().setCategoryLvl(null);

		this.getManagedBean().setStatus(null);

		this.getManagedBean().setLazy(false);

		// businessNatureDataModel.reloadData();
	}

	/**
	 * reload to business nature maintain page
	 * 
	 */
	public String createBusinessNature() {
		debugLogger("createBusinessNature");
		getManagedBean().setNewcategory(true);
		PmBusinessNature pmBusinessNature = new PmBusinessNature();
		pmBusinessNature.setBusinessLevel("10");

		this.getManagedBean().setPmBusinessNature(pmBusinessNature);
		this.getManagedBean().setBrandNew(true);

		return "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";

	}

	/**
	 * To create Business Nature
	 * 
	 * @param pmBusinessNature
	 * @return String
	 */
	public String createBusinessNature(PmBusinessNature pmBusinessNature) {

		getManagedBean().setNewcategory(true);
		PmBusinessNature pmBusinessNature2 = new PmBusinessNature();

		IBusinessNatureSO businessNatureSO = (IBusinessNatureSO) BusinessDelegate
				.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IBusinessNatureSO.class);
		try {

			if (Integer.valueOf(pmBusinessNature.getBusinessLevel()) > 10) {
				BusinessNatureReqDTO businessNatureReqDTO = new BusinessNatureReqDTO();
				businessNatureReqDTO.setBusinessLevel(pmBusinessNature
						.getBusinessLevel());
				businessNatureReqDTO.setBusinessNatureId(pmBusinessNature
						.getBusinessNatureId());
				BusinessNatureResDTO businessNatureResDTO = businessNatureSO
						.retrieveBusinessNatureFetchAll(businessNatureReqDTO);
				pmBusinessNature = businessNatureResDTO.getBusinessNature();
			}

			pmBusinessNature2.setParentBusinessNature(pmBusinessNature);
			pmBusinessNature2.setBusinessLevel(String.valueOf(Integer
					.valueOf(pmBusinessNature.getBusinessLevel()) + 10));
			this.getManagedBean().setPmBusinessNature(pmBusinessNature2);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			businessNatureSO = null;
		}

		this.getManagedBean().setBrandNew(true);

		return "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";

	}

	/**
	 * get status dropdown list
	 */
	public SelectItem[] getBusinessNatureStatus() {

		return PmCommonUtil.getCommonStatusOptions();
	}

	/**
	 * get business level category list
	 */
	public SelectItem[] getBusinessNatureCategoryLv() {

		SelectItem[] items = new SelectItem[6];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(10, ResourceBundleUtils.getMessage(
				"lbl.sm.businessnature.lvl1.section",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[2] = new SelectItem(20, ResourceBundleUtils.getMessage(
				"lbl.sm.businessnature.lvl2.division",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[3] = new SelectItem(30, ResourceBundleUtils.getMessage(
				"lbl.sm.businessnature.lvl3.group",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[4] = new SelectItem(40, ResourceBundleUtils.getMessage(
				"lbl.sm.businessnature.lvl4.class",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[5] = new SelectItem(50, ResourceBundleUtils.getMessage(
				"lbl.sm.businessnature.lvl5.item",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));

		return items;

	}

	/**
	 * search business nature info based on codes and names search
	 */
	private ISearchBean<PmBusinessNature> getSearchBeanData(
			ISearchBean<PmBusinessNature> searchBean) {

		if (getManagedBean().getCategoryLvl() != null) {
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean.setSearchField("businessLevel");
			codesSearchBean.setSearchValue(getManagedBean().getCategoryLvl());
			searchBean.getDataFilterBeans().add(codesSearchBean);

			// section
			if (getManagedBean().getCategoryLvl() == 10L) {
				if (StringUtils.isNotBlank(getManagedBean().getSecLevelCode())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getSecLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getSecLevelName())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getSecLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getStatus() != null) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					codesSearchBean.setSearchField("recordStatusFlg");
					codesSearchBean
							.setSearchValue(getManagedBean().getStatus());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

			}

			// division
			if (getManagedBean().getCategoryLvl() == 20L) {
				if (StringUtils.isNotBlank(getManagedBean().getDivLevelCode())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getDivLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getDivLevelName())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getDivLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getSecLevelCode())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getSecLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getSecLevelName())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getSecLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getStatus() != null) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					codesSearchBean.setSearchField("recordStatusFlg");
					codesSearchBean
							.setSearchValue(getManagedBean().getStatus());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
			}

			// group
			if (getManagedBean().getCategoryLvl() == 30L) {
				if (StringUtils.isNotBlank(getManagedBean().getGroupLevelCode())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getGroupLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getGroupLevelName())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getGroupLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getDivLevelCode())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getDivLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getDivLevelName())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getDivLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getSecLevelCode())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getSecLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getSecLevelName())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getSecLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				if (getManagedBean().getStatus() != null) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					codesSearchBean.setSearchField("recordStatusFlg");
					codesSearchBean
							.setSearchValue(getManagedBean().getStatus());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
			}

			// class
			if (getManagedBean().getCategoryLvl() == 40L) {
				if (StringUtils.isNotBlank(getManagedBean().getClassLevelCode())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getClassLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getClassLevelName())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getClassLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getGroupLevelCode())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getGroupLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getGroupLevelName())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getGroupLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getDivLevelCode())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getDivLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getDivLevelName())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getDivLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getSecLevelCode())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getSecLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getSecLevelName())){
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getSecLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				if (getManagedBean().getStatus() != null) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					codesSearchBean.setSearchField("recordStatusFlg");
					codesSearchBean
							.setSearchValue(getManagedBean().getStatus());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
			}

			// item
			if (getManagedBean().getCategoryLvl() == 50L) {
				if (StringUtils.isNotBlank(getManagedBean().getItemLevelCode())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getItemLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getItemLevelName())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getItemLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getClassLevelCode())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getClassLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getClassLevelName())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getClassLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getGroupLevelCode())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getGroupLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getGroupLevelName())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getGroupLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getDivLevelCode())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getDivLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getDivLevelName())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getDivLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getSecLevelCode())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getSecLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (StringUtils.isNotBlank(getManagedBean().getSecLevelName())) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean()
							.getSecLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getStatus() != null) {
					codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					codesSearchBean.setSearchField("recordStatusFlg");
					codesSearchBean
							.setSearchValue(getManagedBean().getStatus());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
			}
		}

		return searchBean;
	}

	/**
	 * To Retrieve Business Nature
	 */
	public void retrieveBusinessNature() {
		debugLogger("retrieveBusinessNature");

		boolean hasSearchValue = false;

		if (getManagedBean().getCategoryLvl() != null
				&& getManagedBean().getCategoryLvl().intValue() > 0) {
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
		}

		this.getManagedBean().setLazy(true);
		businessNatureDataModel.reloadData();

	}

	/**
	 * 
	 * @return businessNatureDataModel
	 */
	public IPagingDataModel<PmBusinessNature> getBusinessNatureDataModel() {
		return businessNatureDataModel;
	}

	/**
	 * @param businessNatureDataModel the categoryLvl to businessNatureDataModel
	 */
	public void setBusinessNatureDataModel(
			IPagingDataModel<PmBusinessNature> businessNatureDataModel) {
		this.businessNatureDataModel = businessNatureDataModel;
	}

	/**
	 * Business Nature Paging Data Model for searching
	 * 
	 * @author DK
	 *
	 */
	private class BusinessNaturePagingDataModel extends
			PagingDataModel<PmBusinessNature> {
		private static final long serialVersionUID = 1L;

		public BusinessNaturePagingDataModel() {
			super("businessNatureDataModel", getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<PmBusinessNature> pageChangeListener(
				ISearchBean<PmBusinessNature> searchBean) {

			try {
				if (getManagedBean().getCategoryLvl() != null
						&& getManagedBean().getCategoryLvl() > 0L) {
					ISearchBean<PmBusinessNature> searchBeanData = getSearchBeanData(searchBean);
					BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
					reqDTO.setBusinessNatureSearchBean(searchBeanData);
					reqDTO.setBusinessLevel(getManagedBean().getCategoryLvl()
							.toString());

					IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate
							.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IBusinessNatureSO.class);
					BusinessNatureResDTO resDTO = reqSO
							.retrieveBusinessNatureBySearch(reqDTO);
					if (resDTO.isSuccessful()) {
						return resDTO.getBusinessNatureSearchResultBean();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * get business nature info into maintenance page
	 */
	public String goToBusinessMaintaincePage(PmBusinessNature pmBusinessNature) {
		getManagedBean().setNewcategory(false);
		debugLogger("goToBusinessMaintaincePage");
		IBusinessNatureSO businessNatureSO = (IBusinessNatureSO) BusinessDelegate
				.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IBusinessNatureSO.class);
		try {
			BusinessNatureReqDTO businessNatureReqDTO = new BusinessNatureReqDTO();
			businessNatureReqDTO.setBusinessLevel(pmBusinessNature
					.getBusinessLevel());
			businessNatureReqDTO.setBusinessNatureId(pmBusinessNature
					.getBusinessNatureId());
			BusinessNatureResDTO businessNatureResDTO = businessNatureSO
					.retrieveBusinessNatureFetchAll(businessNatureReqDTO);

			this.getManagedBean().setPmBusinessNature(
					businessNatureResDTO.getBusinessNature());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			businessNatureSO = null;
		}

		this.getManagedBean().setBrandNew(false);

		return "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";
	}

	/**
	 * Validation message for business nature details
	 * 
	 */
	private void validationMessage(PmBusinessNature businessNature) {
		if (StringUtils.isBlank(businessNature.getLevelCode())) {
			throw new EpException(MessageConst.ERROR_738000);
		}

		if (StringUtils.isBlank(businessNature.getLevelName())) {
			throw new EpException(MessageConst.ERROR_738001);
		}
		
		if (DbValueConst.RECORD_STATUS_ACTIVE == businessNature.getRecordStatusFlg().intValue()) {
			validateLevelCode(businessNature);
			validateLevelName(businessNature);
		}
	}
	
	/**
	 * Validate Unique Level Code
	 * 
	 * @param businessNature
	 */
	private void validateLevelCode(PmBusinessNature businessNature){
		log.info("validateLevelCode");
		IBusinessNatureSO businessNatureSO = null;
		try {
			businessNatureSO = (IBusinessNatureSO) BusinessDelegate
					.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IBusinessNatureSO.class);
		BusinessNatureReqDTO businessNatureReqDTO = new BusinessNatureReqDTO();
		if(Integer.valueOf(businessNature
				.getBusinessLevel()) > 10){
			businessNatureReqDTO.setParentBusinessNatureId(businessNature
					.getParentBusinessNature().getBusinessNatureId());
		}
		businessNatureReqDTO.setBusinessLevel(businessNature
				.getBusinessLevel());
		businessNatureReqDTO
				.setLevelCode(businessNature.getLevelCode());
		if(!businessNature.isBrandNew()){
			businessNatureReqDTO
				.setBusinessNatureId(businessNature.getBusinessNatureId());
		}
		
		BusinessNatureResDTO businessNatureResDTO = businessNatureSO
				.retrieveBusinessNatureListByLevel(businessNatureReqDTO);
		if (businessNatureResDTO.getBusinessNatureList().size() > 0) {
			String levelDesc = "";
			if(businessNature.isBrandNew()){
				if(ParameterConst.BUSINESS_NATURE_LVL_SECTION.equals(businessNature.getBusinessLevel())){
					levelDesc = getParameterDesc(ParameterTypeConst.BUSINESS_NATURE, ParameterConst.BUSINESS_NATURE_LVL_SECTION);
				}else if(ParameterConst.BUSINESS_NATURE_LVL_DIVISION.equals(businessNature.getBusinessLevel())){
					levelDesc = getParameterDesc(ParameterTypeConst.BUSINESS_NATURE, ParameterConst.BUSINESS_NATURE_LVL_DIVISION);
				}else if(ParameterConst.BUSINESS_NATURE_LVL_GROUP.equals(businessNature.getBusinessLevel())){
					levelDesc = getParameterDesc(ParameterTypeConst.BUSINESS_NATURE, ParameterConst.BUSINESS_NATURE_LVL_GROUP);
				}else if(ParameterConst.BUSINESS_NATURE_LVL_CLASS.equals(businessNature.getBusinessLevel())){
					levelDesc = getParameterDesc(ParameterTypeConst.BUSINESS_NATURE, ParameterConst.BUSINESS_NATURE_LVL_CLASS);
				}else if(ParameterConst.BUSINESS_NATURE_LVL_ITEM.equals(businessNature.getBusinessLevel())){
					levelDesc =getParameterDesc(ParameterTypeConst.BUSINESS_NATURE, ParameterConst.BUSINESS_NATURE_LVL_ITEM);
				}
				throw new EpException(MessageConst.ERROR_770218, new String[]{
						levelDesc,
						PmCommonUtil.getResourceBundleLabels("lbl.pm.bn.code"),
						PmCommonUtil.getResourceBundleLabels("lbl.pm.bn.code")});
			}else{
				throw new EpException(MessageConst.ERROR_770220, new String[] {
						PmCommonUtil.getResourceBundleLabels("lbl.pm.bn.code")
				});	
			}
		}
		}finally {
			businessNatureSO = null;
		}
	}
	
	/**
	 * Validate Unique Level Name
	 * 
	 * @param businessNature
	 */
	private void validateLevelName(PmBusinessNature businessNature){
		log.info("validateLevelName");
		IBusinessNatureSO businessNatureSO = null;
		try{
			ISearchBean<PmBusinessNature> searchBean = new SearchBean<PmBusinessNature>();
			
			if(Integer.valueOf(businessNature
					.getBusinessLevel()) > 10){
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"parentBusinessNature.businessNatureId", 
						SearchOperator.OPERATOR_EQUALS.value(), 
						businessNature.getParentBusinessNature().getBusinessNatureId()
						));
			}
			
			
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"businessLevel", 
					SearchOperator.OPERATOR_EQUALS.value(), 
					businessNature.getBusinessLevel()
					));
			
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"levelName", 
					SearchOperator.OPERATOR_EQUALS.value(), 
					businessNature.getLevelName().toLowerCase().trim(),
			true));
			
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"recordStatusFlg", 
					SearchOperator.OPERATOR_EQUALS.value(), 
					DbValueConst.RECORD_STATUS_ACTIVE
					));
			
			
			if(!businessNature.isBrandNew()){
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"businessNatureId", 
						SearchOperator.OPERATOR_NOT_EQUALS.value(), 
						businessNature.getBusinessNatureId()
						));
			}
			
			BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
			reqDTO.setBusinessNatureSearchBean(searchBean);
			reqDTO.setBusinessLevel(getManagedBean().getCategoryLvl()
					.toString());
	
			businessNatureSO = (IBusinessNatureSO) BusinessDelegate
					.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IBusinessNatureSO.class);
			BusinessNatureResDTO resDTO = businessNatureSO
					.retrieveBusinessNatureBySearch(reqDTO);
			if (resDTO.isSuccessful() && !resDTO.getBusinessNatureList().isEmpty()) {
				String levelDesc = "";
				if(businessNature.isBrandNew()){
					if(ParameterConst.BUSINESS_NATURE_LVL_SECTION.equals(businessNature.getBusinessLevel())){
						levelDesc = getParameterDesc(ParameterTypeConst.BUSINESS_NATURE, ParameterConst.BUSINESS_NATURE_LVL_SECTION);
					}else if(ParameterConst.BUSINESS_NATURE_LVL_DIVISION.equals(businessNature.getBusinessLevel())){
						levelDesc = getParameterDesc(ParameterTypeConst.BUSINESS_NATURE, ParameterConst.BUSINESS_NATURE_LVL_DIVISION);
					}else if(ParameterConst.BUSINESS_NATURE_LVL_GROUP.equals(businessNature.getBusinessLevel())){
						levelDesc = getParameterDesc(ParameterTypeConst.BUSINESS_NATURE, ParameterConst.BUSINESS_NATURE_LVL_GROUP);
					}else if(ParameterConst.BUSINESS_NATURE_LVL_CLASS.equals(businessNature.getBusinessLevel())){
						levelDesc = getParameterDesc(ParameterTypeConst.BUSINESS_NATURE, ParameterConst.BUSINESS_NATURE_LVL_CLASS);
					}else if(ParameterConst.BUSINESS_NATURE_LVL_ITEM.equals(businessNature.getBusinessLevel())){
						levelDesc =getParameterDesc(ParameterTypeConst.BUSINESS_NATURE, ParameterConst.BUSINESS_NATURE_LVL_ITEM);
					}
					throw new EpException(MessageConst.ERROR_770218, new String[]{
							levelDesc,
							PmCommonUtil.getResourceBundleLabels("lbl.pm.bn.name"),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.bn.name")});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.bn.name")
					});	
				}
			}
		}finally{
			businessNatureSO = null;
		}
	}

	/**
	 * save and update business nature detail
	 * 
	 */
	public String saveBusinessNatureDtls() {
		log.info("saveBusinessNatureDtls");
		IBusinessNatureSO businessNatureSO = null;
		String returnUrl = null;
		try {
			businessNatureSO = EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IBusinessNatureSO.class);
			PmBusinessNature businessnature = getManagedBean()
					.getPmBusinessNature();
			businessnature.setLevelCode(businessnature.getLevelCode()
					.toUpperCase());

			updateCategoryLevel(Integer.valueOf(businessnature
					.getBusinessLevel()));

			validationMessage(businessnature);

			BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
			reqDTO.setBusinessNature(businessnature);

			BusinessNatureResDTO resDTO = businessNatureSO
					.saveBusinessNatureDtls(reqDTO);
			this.getManagedBean().setPmBusinessNature(
					resDTO.getBusinessNature());
			if (resDTO.isSuccessful()) {
				if (this.getManagedBean().isBrandNew()) {
					returnUrl =  "/xhtml/pm/businessnature/businessNatureMain.xhtml";
//					if (Integer.valueOf(this.getManagedBean()
//							.getPmBusinessNature().getBusinessLevel()) > 10) {
//						BusinessNatureReqDTO businessNatureReqDTO = new BusinessNatureReqDTO();
//						businessNatureReqDTO.setBusinessLevel(this
//								.getManagedBean().getPmBusinessNature()
//								.getParentBusinessNature().getBusinessLevel());
//						businessNatureReqDTO.setBusinessNatureId(this
//								.getManagedBean().getPmBusinessNature()
//								.getParentBusinessNature()
//								.getBusinessNatureId());
//						BusinessNatureResDTO businessNatureResDTO = businessNatureSO
//								.retrieveBusinessNatureFetchAll(businessNatureReqDTO);
//
//						businessNatureResDTO
//								.getBusinessNature()
//								.getChildBusinessNatures()
//								.add(this.getManagedBean()
//										.getPmBusinessNature());
//						reqDTO.setBusinessNature(businessNatureResDTO
//								.getBusinessNature());
//						businessNatureSO.saveBusinessNatureDtls(reqDTO);
//					}
					
					FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.ERR_900016,
													new String[] {
															getManagedBean()
																	.getTaskName(),
																	getManagedBean()
																	.getPmBusinessNature()
																	.getLevelCode() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
				}else{
					returnUrl =  "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";
					FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.ERR_900024,
													new String[] {
															getManagedBean()
																	.getTaskName(),
															getManagedBean()
																	.getPmBusinessNature()
																	.getLevelCode() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
				}
			}

		} catch (Exception e) {
			throw(e);
		} finally {
			businessNatureSO = null;
		}
		return returnUrl;
	}

	/**
	 * To update category level
	 * 
	 * @param businessLvlCode
	 */
	public void updateCategoryLevel(int businessLvlCode) {

		// ResourceBundleUtils.getMessage("lbl.sm.businessnature.lvl5.item",
		// ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		// FacesScopes().getUserLocale());
		log.info("businessLvlCode  :" + businessLvlCode);

		if (10 == businessLvlCode) {
			getManagedBean().setTaskName(
					ResourceBundleUtils.getMessage(
							"lbl.sm.businessnature.lvl1.section",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
		} else if (20 == businessLvlCode) {
			getManagedBean().setTaskName(
					ResourceBundleUtils.getMessage(
							"lbl.sm.businessnature.lvl2.division",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
		} else if (30 == businessLvlCode) {
			getManagedBean().setTaskName(
					ResourceBundleUtils.getMessage(
							"lbl.sm.businessnature.lvl3.group",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));

		} else if (40 == businessLvlCode) {
			getManagedBean().setTaskName(
					ResourceBundleUtils.getMessage(
							"lbl.sm.businessnature.lvl4.class",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));

		} else if (50 == businessLvlCode) {
			getManagedBean().setTaskName(
					ResourceBundleUtils.getMessage(
							"lbl.sm.businessnature.lvl5.item",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));

		}

	}

	/**
	 * To Retrieve Action Label
	 * 
	 * @param level
	 * @return String
	 */
	public String retrieveActionLabel(String level){
		String label = "";
		if("10".equals(level)){
			label = ResourceBundleUtils.getMessage(
					"lbl.pm.add.division.category",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		}else if("20".equals(level)){
			label = ResourceBundleUtils.getMessage(
					"lbl.pm.add.group.category",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		}else if("30".equals(level)){
			label = ResourceBundleUtils.getMessage(
					"lbl.pm.add.class.category",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		}else if("40".equals(level)){
			label = ResourceBundleUtils.getMessage(
					"lbl.pm.add.item.category",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		}
		return label;
	}
}
