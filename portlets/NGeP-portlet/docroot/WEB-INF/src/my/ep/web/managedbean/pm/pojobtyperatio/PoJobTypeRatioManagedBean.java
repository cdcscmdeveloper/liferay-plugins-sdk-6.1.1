/*
 * Copyright © 2017 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.pojobtyperatio;

import java.util.Map;
import java.util.TreeMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.pojobtyperatio.IPoJobTypeRatioSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.pojobtyperatio.PoJobTypeRatioReqDTO;
import my.ep.dto.pm.common.pojobtyperatio.PoJobTypeRatioResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmPoJobTypeRatio;
import my.ep.persistence.entity.pm.common.PmPoJobWeight;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Po Type Ratio
 * 
 * @author Ken
 * @since 13/03/2017
 */
@ManagedBean(name = "poJobTypeRatioManagedBean")
@SessionScoped
public class PoJobTypeRatioManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	// Paging for listing
	private IPagingDataModel<PmPoJobTypeRatio> jobTypeRatioDataModel = new JobTypeRatioPagingDataModel();
	
	private PmPoJobTypeRatio poJobTypeRatio;
	private PmPoJobTypeRatio oriPoJobTypeRatio;
	private boolean applDisabled;
	private boolean categoryDisabled;
	private Map<String, String> appTypeMap;
	private Map<String, String> suppTypeMap;
	private Map<String, String> categoryTypeMap;
	private Map<Long,PmPoJobWeight> jobWeightMap;
	
	/************ AUDIT START #28895-35509 *************/
	private Boolean brandNew = false;
	private PmPoJobTypeRatio savedJobTypeRatio;
	/************ AUDIT END #28895-35509 *************/
	
	public PoJobTypeRatioManagedBean() {
	}

	/**
	 * @return the jobTypeRatioDataModel
	 */
	public IPagingDataModel<PmPoJobTypeRatio> getJobTypeRatioDataModel() {
		return jobTypeRatioDataModel;
	}

	/**
	 * @param jobTypeRatioDataModel the jobTypeRatioDataModel to set
	 */
	public void setJobTypeRatioDataModel(IPagingDataModel<PmPoJobTypeRatio> jobTypeRatioDataModel) {
		this.jobTypeRatioDataModel = jobTypeRatioDataModel;
	}
	

	/**
	 * @return the poJobTypeRatio
	 */
	public PmPoJobTypeRatio getPoJobTypeRatio() {
		return poJobTypeRatio;
	}

	/**
	 * @param poJobTypeRatio the poJobTypeRatio to set
	 */
	public void setPoJobTypeRatio(PmPoJobTypeRatio poJobTypeRatio) {
		this.poJobTypeRatio = poJobTypeRatio;
	}

	/**
	 * @return the applDisabled
	 */
	public boolean isApplDisabled() {
		return applDisabled;
	}

	/**
	 * @param applDisabled the applDisabled to set
	 */
	public void setApplDisabled(boolean applDisabled) {
		this.applDisabled = applDisabled;
	}

	/**
	 * @return the categoryDisabled
	 */
	public boolean isCategoryDisabled() {
		return categoryDisabled;
	}

	/**
	 * @param categoryDisabled the categoryDisabled to set
	 */
	public void setCategoryDisabled(boolean categoryDisabled) {
		this.categoryDisabled = categoryDisabled;
	}
	
	/**
	 * @return the appTypeMap
	 */
	public Map<String, String> getAppTypeMap() {
		if(appTypeMap == null){
			appTypeMap = new TreeMap<String, String>(){	
				private static final long serialVersionUID = 1L;{
				     put("lbl.pm.po.jobtyperatio.new", ParameterConst.NEW_SM_APPLN_FOR_MOF);
				     put("lbl.pm.po.jobtyperatio.renew", ParameterConst.NEW_SM_APPLN_FOR_RENEW);
				     put("lbl.pm.po.jobtyperatio.update", ParameterConst.NEW_SM_APPLN_FOR_UPDATE);
				     put("lbl.pm.po.jobtyperatio.cat", ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES);
				     put("lbl.pm.po.jobtyperatio.bumi", ParameterConst.NEW_SM_APPLN_FOR_BUMI);
			}};
		}
		return appTypeMap;
	}

	/**
	 * @param appTypeMap the appTypeMap to set
	 */
	public void setAppTypeMap(Map<String, String> appTypeMap) {
		this.appTypeMap = appTypeMap;
	}

	/**
	 * @return the suppTypeMap
	 */
	public Map<String, String> getSuppTypeMap() {
		return suppTypeMap;
	}

	/**
	 * @param suppTypeMap the suppTypeMap to set
	 */
	public void setSuppTypeMap(Map<String, String> suppTypeMap) {
		this.suppTypeMap = suppTypeMap;
	}

	/**
	 * @return the categoryTypeMap
	 */
	public Map<String, String> getCategoryTypeMap() {
		if(categoryTypeMap == null){
			categoryTypeMap = new TreeMap<String, String>(){	
				private static final long serialVersionUID = 1L;{
				     put("lbl.pm.po.task.category.normal", SupplierManagementConst.SM_APPL_CATEGORY_TYPE_NORMAL);
				     put("lbl.pm.po.task.category.special", SupplierManagementConst.SM_APPL_CATEGORY_TYPE_SPECIAL);
				}};
		}
		return categoryTypeMap;
	}

	/**
	 * @param categoryTypeMap the categoryTypeMap to set
	 */
	public void setCategoryTypeMap(Map<String, String> categoryTypeMap) {
		this.categoryTypeMap = categoryTypeMap;
	}

	/**
	 * @return the oriPoJobTypeRatio
	 */
	public PmPoJobTypeRatio getOriPoJobTypeRatio() {
		return oriPoJobTypeRatio;
	}

	/**
	 * @param oriPoJobTypeRatio the oriPoJobTypeRatio to set
	 */
	public void setOriPoJobTypeRatio(PmPoJobTypeRatio oriPoJobTypeRatio) {
		this.oriPoJobTypeRatio = oriPoJobTypeRatio;
	}



	/**
	 * @return the jobWeightMap
	 */
	public Map<Long,PmPoJobWeight> getJobWeightMap() {
		return jobWeightMap;
	}

	/**
	 * @param jobWeightMap the jobWeightMap to set
	 */
	public void setJobWeightMap(Map<Long,PmPoJobWeight> jobWeightMap) {
		this.jobWeightMap = jobWeightMap;
	}



	// pagination for PO Job Type Ratio List
	private class JobTypeRatioPagingDataModel extends
			PagingDataModel<PmPoJobTypeRatio> {
		private static final long serialVersionUID = 1L;

		public JobTypeRatioPagingDataModel() {
			super("dtPoJobTypeRatio");
		}

		@Override
		protected ISearchResultBean<PmPoJobTypeRatio> pageChangeListener(
				ISearchBean<PmPoJobTypeRatio> searchBean) {
			IPoJobTypeRatioSO jobTypeRatioSO = null;
			try {
				jobTypeRatioSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IPoJobTypeRatioSO.class);
				PoJobTypeRatioReqDTO reqDTO = new PoJobTypeRatioReqDTO();
				reqDTO.setSearchBean(searchBean);
				PoJobTypeRatioResDTO resDTO = jobTypeRatioSO.retrieveJobTypeRatioList(reqDTO);
				if(resDTO != null && resDTO.isSuccessful() && resDTO.getResultBean() != null){
					return resDTO.getResultBean();
				}
			} catch (Exception e) {
				throw new EpException(e.getMessage());
			} finally {
				jobTypeRatioSO = null;
			}
			return null;
		}

	}
	/************ AUDIT START #28895-35509 *************/
	/**
	 * @return the brandNew
	 */
	public Boolean isBrandNew() {
		return brandNew;
	}

	/**
	 * @param brandNew the brandNew to set
	 */
	public void setBrandNew(Boolean brandNew) {
		this.brandNew = brandNew;
	}

	public PmPoJobTypeRatio getSavedJobTypeRatio() {
		return savedJobTypeRatio;
	}

	public void setSavedJobTypeRatio(PmPoJobTypeRatio savedJobTypeRatio) {
		this.savedJobTypeRatio = savedJobTypeRatio;
	}
	/************ AUDIT END #28895-35509 *************/

}
