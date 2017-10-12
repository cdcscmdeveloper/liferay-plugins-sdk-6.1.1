/*
 * Copyright © 2017 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.pojobtyperatio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import my.ep.persistence.constant.DbValueConst;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
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
public class PoJobTypeRatioBackingBean extends BaseBackingBean<PoJobTypeRatioManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_MAIN = "poJobTypeRatioMain.xhtml";
	private static final String NAV_TO_DETAILS = "poJobTypeRatioDetails.xhtml";

	private static final Logger log = EpLogger.getLogger(PoJobTypeRatioBackingBean.class);

	public void preRenderView() {
		log.info("preRenderView");
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAIN);
	}

	public void redirectToMaintainPoJobTypeRatio(Long poJobTypeRatioId) {
		log.info("redirectToMaintainPoJobTypeRatio");
		initJobWeightMap();
		if (poJobTypeRatioId != null && poJobTypeRatioId > 0) {
			loadPoJobTypeRatio(poJobTypeRatioId);
		} else {
			initialPoJobTypeRatio();
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_DETAILS);
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
		List<PmPoJobTypeRatio> jobTypeRatioList = retrievePoJobTypeRatios(poJobTypeRatioId, null);
		PmPoJobTypeRatio oriPoJobTypeRatio = null;
		if (jobTypeRatioList != null && !jobTypeRatioList.isEmpty()) {
			getManagedBean().setPoJobTypeRatio(jobTypeRatioList.get(0));
			oriPoJobTypeRatio = (PmPoJobTypeRatio) (SerializationUtils.clone(jobTypeRatioList.get(0)));
			getManagedBean().setOriPoJobTypeRatio(oriPoJobTypeRatio);
			getManagedBean().setApplDisabled(false);
			getManagedBean().setCategoryDisabled(false);
			disableCategoryType();
		} else {
			throw new EpException("not able to retrive po job type ratio");
		}
	}

	private void initJobWeightMap() {
		List<PmPoJobWeight> jobWeightList = PmCommonUtil.retrievePmPoJobWeightList();
		if (CollectionUtils.isNotEmpty(jobWeightList)) {
			Map<Long, PmPoJobWeight> map = new HashMap<Long, PmPoJobWeight>();
			for (PmPoJobWeight jobWeight : jobWeightList) {
				map.put(jobWeight.getPoJobWeightId(), jobWeight);
			}
			getManagedBean().setJobWeightMap(map);
		}
	}

	public void changeSuppType() {
		getManagedBean().getPoJobTypeRatio().setApplType(null);
		getManagedBean().getPoJobTypeRatio().setCategoryType(null);
		getManagedBean().getPoJobTypeRatio().getPoJobWeight().setPoJobWeightId(null);
		if (StringUtils.isNotBlank(getManagedBean().getPoJobTypeRatio().getSupplierType())) {
			getManagedBean().setApplDisabled(false);
		}
	}

	public void changeAppType() {
		getManagedBean().getPoJobTypeRatio().setCategoryType(null);
		getManagedBean().getPoJobTypeRatio().getPoJobWeight().setPoJobWeightId(null);
		disableCategoryType();
	}

	private void disableCategoryType() {
		if (StringUtils.isNotBlank(getManagedBean().getPoJobTypeRatio().getApplType())
				&& ProfileManagementConst.CATEGORY_ENABLE_APPL_LIST
						.contains(getManagedBean().getPoJobTypeRatio().getApplType())) {
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
		if (StringUtils.isNotBlank(getManagedBean().getPoJobTypeRatio().getSupplierType())
				&& !SupplierManagementConst.SUPP_TYPE_CONTRACTOR
						.equals(getManagedBean().getPoJobTypeRatio().getSupplierType())) {
			map.remove("lbl.pm.po.jobtyperatio.bumi");
		}
		SelectItem[] items = null;
		items = new SelectItem[map.size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
		int i = 1;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			items[i++] = new SelectItem(entry.getValue(), PmCommonUtil.getResourceBundleLabels(entry.getKey()));
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
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
		int i = 1;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			items[i++] = new SelectItem(entry.getValue(), PmCommonUtil.getResourceBundleLabels(entry.getKey()));
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
		for (PmPoJobWeight weight : getManagedBean().getJobWeightMap().values()) {
			map.put(getParameterDesc(weight.getWeightType()), weight.getPoJobWeightId());
		}

		SelectItem[] items = null;
		items = new SelectItem[map.size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
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
			jobRatioSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IPoJobTypeRatioSO.class);
			boolean isBrandNew = getManagedBean().getPoJobTypeRatio().isBrandNew();
			PoJobTypeRatioReqDTO reqDTO = new PoJobTypeRatioReqDTO();
			reqDTO.setJobTypeRatio(getManagedBean().getPoJobTypeRatio());
			setupTrackingDiaryVal(reqDTO);
			PoJobTypeRatioResDTO resDTO = jobRatioSO.saveJobTypeRatio(reqDTO);
			if (resDTO.isSuccessful() && resDTO.getPoJobTypeRatio() != null) {
				if (isBrandNew) {
					PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_779300, new String[] {});
					getManagedBean().setAppTypeMap(null);
					getManagedBean().setCategoryTypeMap(null);
					getManagedBean().getJobTypeRatioDataModel().reloadData();
					return NAV_TO_MAIN;
				} else {
					PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_779301, new String[] {});
					initJobWeightMap();
					getManagedBean().setPoJobTypeRatio(resDTO.getPoJobTypeRatio());
					PmPoJobTypeRatio oriPoJobTypeRatio = (PmPoJobTypeRatio) (SerializationUtils
							.clone(resDTO.getPoJobTypeRatio()));
					getManagedBean().setOriPoJobTypeRatio(oriPoJobTypeRatio);
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
		StringBuilder sb = new StringBuilder();
		// supp type
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("supplierType", SearchOperator.OPERATOR_EQUALS.value(), ratio.getSupplierType()));
		sb.append(getSuppTypeLabel(ratio.getSupplierType()));
		sb.append(StringUtils.isNotBlank(ratio.getCategoryType()) ? ", ": " "+PmCommonUtil.getMessage("lbl.sm.activation.and", null)+" ");

		// appl type
		searchBean.getDataFilterBeans()
				.add(new SearchFilterBean("applType", SearchOperator.OPERATOR_EQUALS.value(), ratio.getApplType()));
		sb.append(getMapKeyByValue(ratio.getApplType(), getManagedBean().getAppTypeMap()));

		// category type
		if (StringUtils.isNotBlank(ratio.getCategoryType())) {
			sb.append(" ");
			sb.append(PmCommonUtil.getMessage("lbl.sm.activation.and", null));
			sb.append(" ");
			sb.append(getMapKeyByValue(ratio.getCategoryType(), getManagedBean().getCategoryTypeMap()));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("categoryType",
					SearchOperator.OPERATOR_EQUALS.value(), ratio.getCategoryType()));
		} else {
			searchBean.getDataFilterBeans()
					.add(new SearchFilterBean("categoryType", SearchOperator.OPERATOR_IS_NULL.value(), null));
		}

		if (!ratio.isBrandNew()) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("poJobTypeRatioId",
					SearchOperator.OPERATOR_NOT_EQUALS.value(), ratio.getPoJobTypeRatioId()));
		}

		List<PmPoJobTypeRatio> list = retrievePoJobTypeRatios(null, searchBean);
		if (list != null && !list.isEmpty()) {
			throw new EpException(MessageConst.ERROR_779302, new String[] { sb.toString() });
		}

	}

	private List<PmPoJobTypeRatio> retrievePoJobTypeRatios(Long poJobTypeRatioId,
			ISearchBean<PmPoJobTypeRatio> searchBean) {
		IPoJobTypeRatioSO jobTypeRatioSO = null;
		try {
			jobTypeRatioSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IPoJobTypeRatioSO.class);
			PoJobTypeRatioReqDTO reqDTO = new PoJobTypeRatioReqDTO();
			if (searchBean == null) {
				searchBean = new SearchBean<PmPoJobTypeRatio>();
			}

			if (poJobTypeRatioId != null && poJobTypeRatioId > 0) {
				// po job type ratio id
				searchBean.getDataFilterBeans().add(new SearchFilterBean("poJobTypeRatioId",
						SearchOperator.OPERATOR_EQUALS.value(), poJobTypeRatioId));
			}
			reqDTO.setSearchBean(searchBean);
			PoJobTypeRatioResDTO resDTO = jobTypeRatioSO.retrieveJobTypeRatioList(reqDTO);
			if (resDTO != null && resDTO.isSuccessful() && resDTO.getResultBean() != null
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
		getManagedBean().getJobTypeRatioDataModel().reloadData();
		return NAV_TO_MAIN;
	}

	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getCommonStatusOptions();
	}

	public String getAppTypeString(String appType) {
		return getStringMapValByKey(appType, getManagedBean().getAppTypeMap(), null);
	}

	public String getCateogryTypeString(String categoryType) {
		return getStringMapValByKey(categoryType, getManagedBean().getCategoryTypeMap(), null);
	}

	private String getStringMapValByKey(String key, Map<String, String> map, Locale locale) {
		String value = null;
		for (Entry<String, String> entry : map.entrySet()) {
			if (Objects.equals(key, entry.getValue())) {
				value = PmCommonUtil.getResourceBundleLabels(entry.getKey(),
						locale != null ? locale : getFacesScopes().getUserLocale());
				break;
			}
		}
		return value;

	}

	private void setupTrackingDiaryVal(PoJobTypeRatioReqDTO reqDTO) {
		PmPoJobTypeRatio jobTypeRatio = getManagedBean().getPoJobTypeRatio();
		PmPoJobTypeRatio oriJobTypeRatio = getManagedBean().getOriPoJobTypeRatio();
		getManagedBean().setAppTypeMap(null);
		getManagedBean().setCategoryTypeMap(null);
		String messageId = null;
		String[] params = null;
		Locale myLocale = PmCommonUtil.getMyLocale();
		if (jobTypeRatio.isBrandNew()) {
			messageId = MessageConst.TD_81520;
			params = initMessageParam(jobTypeRatio, getParameterDesc(getManagedBean().getJobWeightMap()
					.get(jobTypeRatio.getPoJobWeight().getPoJobWeightId()).getWeightType(), myLocale));
			addTrackingDiaryIntoList(messageId, params, jobTypeRatio, reqDTO);
		} else {
			if (oriJobTypeRatio != null && jobTypeRatio != null) {
				if (!oriJobTypeRatio.getPoJobWeight().getPoJobWeightId()
						.equals(jobTypeRatio.getPoJobWeight().getPoJobWeightId())) {
					messageId = MessageConst.TD_81521;
					params = initMessageParam(jobTypeRatio,
							getManagedBean().getJobWeightMap().get(oriJobTypeRatio.getPoJobWeight().getPoJobWeightId())
									.getWeightValue().toString(),
							getManagedBean().getJobWeightMap().get(jobTypeRatio.getPoJobWeight().getPoJobWeightId())
									.getWeightValue().toString());
					addTrackingDiaryIntoList(messageId, params, jobTypeRatio, reqDTO);

				}

				if (!oriJobTypeRatio.getRecordStatusFlg().equals(jobTypeRatio.getRecordStatusFlg())) {
					if (DbValueConst.RECORD_STATUS_SUSPENDED == jobTypeRatio.getRecordStatusFlg()) {
						messageId = MessageConst.TD_81522;
					} else if (DbValueConst.RECORD_STATUS_ACTIVE == jobTypeRatio.getRecordStatusFlg()) {
						messageId = MessageConst.TD_81523;
					}
					params = initMessageParam(jobTypeRatio);
					addTrackingDiaryIntoList(messageId, params, jobTypeRatio, reqDTO);

				}
			}
		}
	}

	private String[] initMessageParam(PmPoJobTypeRatio jobTypeRatio, String... moreParam) {
		Locale myLocale = PmCommonUtil.getMyLocale();
		String[] returnParams;
		if (moreParam.length > 0) {
			List<String> arrayList = new ArrayList<String>();
			arrayList.add(jobTypeRatio.getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
					? PmCommonUtil.getResourceBundleLabels("lbl.sm.contractor", myLocale)
					: PmCommonUtil.getResourceBundleLabels("lbl.sm.consultant", myLocale));
			arrayList.add(getStringMapValByKey(jobTypeRatio.getApplType(), getManagedBean().getAppTypeMap(), myLocale));
			arrayList.add(jobTypeRatio.getCategoryType() != null ? getStringMapValByKey(jobTypeRatio.getCategoryType(),
					getManagedBean().getCategoryTypeMap(), myLocale) : "-");
			for (String string : moreParam) {
				arrayList.add(string);
			}
			returnParams = new String[arrayList.size()];
			arrayList.toArray(returnParams);
		} else {
			returnParams = new String[] {
					jobTypeRatio.getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
							? PmCommonUtil.getResourceBundleLabels("lbl.sm.contractor", myLocale)
							: PmCommonUtil.getResourceBundleLabels("lbl.sm.consultant", myLocale),
					getStringMapValByKey(jobTypeRatio.getApplType(), getManagedBean().getAppTypeMap(), myLocale),
					jobTypeRatio.getCategoryType() != null ? getStringMapValByKey(jobTypeRatio.getCategoryType(),
							getManagedBean().getCategoryTypeMap(), myLocale) : "-" };
		}

		return returnParams;
	}

	private void addTrackingDiaryIntoList(String messageId, String[] params, PmPoJobTypeRatio jobTypeRatio,
			PoJobTypeRatioReqDTO reqDTO) {
		if (messageId != null && params != null && params.length > 0) {
			reqDTO.getTrackingDiaryList().add(PmCommonUtil.assignTrackingDairyForJobTypeRatio(jobTypeRatio,
					PmCommonUtil.getActionDesc(messageId, params), jobTypeRatio.getRecordStatusFlg().longValue()));
		}
	}

	private String getMapKeyByValue(String val, Map<String, String> map) {
		String keyLabel = "";
		if (StringUtils.isNotBlank(val)) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (entry.getValue().equals(val)) {
					keyLabel = PmCommonUtil.getMessage(entry.getKey(), null);
					break;
				}
			}
		}
		return keyLabel;
	}

	private String getSuppTypeLabel(String code) {
		if (StringUtils.isNotBlank(code)) {
			return code.equals(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
					? PmCommonUtil.getMessage("lbl.sm.contractor", null)
					: PmCommonUtil.getMessage("lbl.sm.consultant", null);
		} else {
			return "";
		}
	}

}
