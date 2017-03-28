/*
 * Copyright © 2017 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.pojobtyperatio;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.pojobtyperatio.IPoJobTypeRatioSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.common.pojobtyperatio.PoJobTypeRatioReqDTO;
import my.ep.dto.pm.common.pojobtyperatio.PoJobTypeRatioResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmPoJobTypeRatio;
import my.ep.persistence.entity.pm.common.PmPoJobWeight;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.pojobtyperatio.PoJobTypeRatioManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Backing Bean for Po Job Type Ratio
 * 
 * @author Ken
 * @since 13/3/2017
 */
@ManagedBean(name = "poJobTypeRatioBackingBean")
@ViewScoped
public class PoJobTypeRatioBackingBean extends
		BaseBackingBean<PoJobTypeRatioManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_MAIN = "poJobTypeRatioMain.xhtml";
	private static final String NAV_TO_DETAILS = "poJobTypeRatioDetails.xhtml";

	private static final Logger log = EpLogger
			.getLogger(PoJobTypeRatioBackingBean.class);

	public void preRenderView() {
		log.info("preRenderView");
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	public void redirectToMaintainPoJobTypeRatio(Long poJobTypeRatioId) {
		log.info("redirectToMaintainPoJobTypeRatio");
		if (poJobTypeRatioId != null && poJobTypeRatioId > 0) {
			loadPoJobTypeRatio(poJobTypeRatioId);
		} else {
			initialPoJobTypeRatio();
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_DETAILS);
	}

	private PmPoJobTypeRatio initNewJobTypeRatio() {
		log.info("initNewJobTypeRatio");
		PmPoJobTypeRatio ratio = new PmPoJobTypeRatio();
		PmPoJobWeight weight = new PmPoJobWeight();
		ratio.setPoJobWeight(weight);
		return ratio;
	}

	private void initialPoJobTypeRatio() {
		log.info("initialPoJobTypeRatio");
		PmPoJobTypeRatio ratio = initNewJobTypeRatio();
		getManagedBean().setPoJobTypeRatio(ratio);
		getManagedBean().setApplDisabled(true);
		getManagedBean().setCategoryDisabled(true);

	}

	private void loadPoJobTypeRatio(Long poJobTypeRatioId) {
		log.info("loadPoJobTypeRaio");
		getManagedBean().setPoJobTypeRatio(initNewJobTypeRatio());
		List<PmPoJobTypeRatio> jobTypeRatioList = retrievePoJobTypeRatios(
				poJobTypeRatioId, null);
		if (jobTypeRatioList != null && !jobTypeRatioList.isEmpty()) {
			getManagedBean().setPoJobTypeRatio(jobTypeRatioList.get(0));
			getManagedBean().setApplDisabled(false);
			getManagedBean().setCategoryDisabled(false);
			disableCategoryType();
		} else {
			throw new EpException("not able to retrive po job type ratio");
		}
	}

	public void changeSuppType() {
		getManagedBean().getPoJobTypeRatio().setApplType(null);
		getManagedBean().getPoJobTypeRatio().setCategoryType(null);
		getManagedBean().getPoJobTypeRatio().getPoJobWeight()
				.setPoJobWeightId(null);
		if (StringUtils.isNotBlank(getManagedBean().getPoJobTypeRatio()
				.getSupplierType())) {
			getManagedBean().setApplDisabled(false);
		}
	}

	public void changeAppType() {
		getManagedBean().getPoJobTypeRatio().setCategoryType(null);
		getManagedBean().getPoJobTypeRatio().getPoJobWeight()
				.setPoJobWeightId(null);
		disableCategoryType();
	}

	private void disableCategoryType() {
		if (StringUtils.isNotBlank(getManagedBean().getPoJobTypeRatio()
				.getApplType())
				&& ProfileManagementConst.CATEGORY_ENABLE_APPL_LIST
						.contains(getManagedBean().getPoJobTypeRatio()
								.getApplType())) {
			getManagedBean().setCategoryDisabled(false);
		} else {
			getManagedBean().setCategoryDisabled(true);
		}
	}

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * retrieve application type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] applicationTypeOptions() {
		log.info("applicationTypeOptions");
		getManagedBean().setAppTypeMap(null);
		Map<String, String> map = getManagedBean().getAppTypeMap();
		if (StringUtils.isNotBlank(getManagedBean().getPoJobTypeRatio()
				.getSupplierType())
				&& !SupplierManagementConst.SUPP_TYPE_CONTRACTOR
						.equals(getManagedBean().getPoJobTypeRatio()
								.getSupplierType())) {
			map.remove(PmCommonUtil
					.getResourceBundleLabels("lbl.pm.po.jobtyperatio.bumi"));
		}
		SelectItem[] items = null;
		items = new SelectItem[map.size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale()));
		int i = 1;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			items[i++] = new SelectItem(entry.getValue(), entry.getKey());
		}

		return items;
	}

	/**
	 * retrieve category type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] catTypeOptions() {
		log.info("catTypeOptions");
		getManagedBean().setCategoryTypeMap(null);
		Map<String, String> map = getManagedBean().getCategoryTypeMap();
		SelectItem[] items = null;
		items = new SelectItem[map.size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale()));
		int i = 1;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			items[i++] = new SelectItem(entry.getValue(), entry.getKey());
		}

		return items;
	}

	/**
	 * retrieve job weight type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] jobWeightTypeOptions() {
		log.info("jobWeightTypeOptions");
		Map<String, Long> map = new TreeMap<String, Long>();
		for (PmPoJobWeight weight : PmCommonUtil.retrievePmPoJobWeightList()) {
			map.put(getParameterDesc(weight.getWeightType()),
					weight.getPoJobWeightId());
		}

		SelectItem[] items = null;
		items = new SelectItem[map.size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale()));
		int i = 1;
		for (Map.Entry<String, Long> entry : map.entrySet()) {
			items[i++] = new SelectItem(entry.getValue(), entry.getKey());
		}

		return items;
	}

	public String savePoJobRatio() {
		IPoJobTypeRatioSO jobRatioSO = null;
		poJobRatioTypeValidation();
		try {
			jobRatioSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IPoJobTypeRatioSO.class);
			boolean isBrandNew = getManagedBean().getPoJobTypeRatio()
					.isBrandNew();
			PoJobTypeRatioReqDTO reqDTO = new PoJobTypeRatioReqDTO();
			reqDTO.setJobTypeRatio(getManagedBean().getPoJobTypeRatio());
			PoJobTypeRatioResDTO resDTO = jobRatioSO.saveJobTypeRatio(reqDTO);
			if (resDTO.isSuccessful()) {
				if (isBrandNew) {
					PmCommonUtil.showFacesMsg(
							FacesContext.getCurrentInstance(),
							FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_779300, new String[] {});
					getManagedBean().setAppTypeMap(null);
					getManagedBean().setCategoryTypeMap(null);
					getManagedBean().getJobTypeRatioDataModel().reloadData();
					return NAV_TO_MAIN;
				} else {
					PmCommonUtil.showFacesMsg(
							FacesContext.getCurrentInstance(),
							FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_779301, new String[] {});
					return NAV_TO_DETAILS;
				}
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		} finally {
			jobRatioSO = null;
		}
		return null;
	}

	private void poJobRatioTypeValidation() {
		ISearchBean<PmPoJobTypeRatio> searchBean = new SearchBean<PmPoJobTypeRatio>();
		PmPoJobTypeRatio ratio = getManagedBean().getPoJobTypeRatio();
		// supp type
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("supplierType",
						SearchOperator.OPERATOR_EQUALS.value(), ratio
								.getSupplierType()));

		// appl type
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("applType", SearchOperator.OPERATOR_EQUALS
						.value(), ratio.getApplType()));

		// category type
		if (StringUtils.isNotBlank(ratio.getCategoryType())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("categoryType",
							SearchOperator.OPERATOR_EQUALS.value(), ratio
									.getCategoryType()));
		} else {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("categoryType",
							SearchOperator.OPERATOR_IS_NULL.value(), null));
		}

		if (!ratio.isBrandNew()) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("poJobTypeRatioId",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), ratio
									.getPoJobTypeRatioId()));
		}

		List<PmPoJobTypeRatio> list = retrievePoJobTypeRatios(null, searchBean);
		if (list != null && !list.isEmpty()) {
			throw new EpException(
					"This combination of Supplier Type, Application Type and Category type is existed. Please select other combination.");
		}

	}

	private List<PmPoJobTypeRatio> retrievePoJobTypeRatios(
			Long poJobTypeRatioId, ISearchBean<PmPoJobTypeRatio> searchBean) {
		IPoJobTypeRatioSO jobTypeRatioSO = null;
		try {
			jobTypeRatioSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IPoJobTypeRatioSO.class);
			PoJobTypeRatioReqDTO reqDTO = new PoJobTypeRatioReqDTO();
			if (searchBean == null) {
				searchBean = new SearchBean<PmPoJobTypeRatio>();
			}

			if (poJobTypeRatioId != null && poJobTypeRatioId > 0) {
				// po job type ratio id
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("poJobTypeRatioId",
								SearchOperator.OPERATOR_EQUALS.value(),
								poJobTypeRatioId));
			}
			reqDTO.setSearchBean(searchBean);
			PoJobTypeRatioResDTO resDTO = jobTypeRatioSO
					.retrieveJobTypeRatioList(reqDTO);
			if (resDTO != null && resDTO.isSuccessful()
					&& resDTO.getResultBean() != null
					&& !resDTO.getResultBean().getResultList().isEmpty()) {
				return resDTO.getResultBean().getResultList();
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		} finally {
			jobTypeRatioSO = null;
		}
		return null;
	}

	public String backToMain() {
		getManagedBean().setAppTypeMap(null);
		getManagedBean().setCategoryTypeMap(null);
		return NAV_TO_MAIN;
	}

	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getStatusOptions();
	}

	public String getAppTypeString(String appType) {
		String appTypeString = null;
		for (Entry<String, String> entry : getManagedBean().getAppTypeMap()
				.entrySet()) {
			if (Objects.equals(appType, entry.getValue())) {
				appTypeString = entry.getKey();
				break;
			}
		}
		return appTypeString;

	}

	public String getCateogryTypeString(String categoryType) {
		String cateogryTypeString = null;
		for (Entry<String, String> entry : getManagedBean().getCategoryTypeMap()
				.entrySet()) {
			if (Objects.equals(categoryType, entry.getValue())) {
				cateogryTypeString = entry.getKey();
				break;
			}
		}
		return cateogryTypeString;
	}

}
