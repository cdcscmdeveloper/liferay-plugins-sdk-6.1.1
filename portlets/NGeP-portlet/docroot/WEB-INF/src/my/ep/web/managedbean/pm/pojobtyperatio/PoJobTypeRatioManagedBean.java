/*
 * Copyright © 2017 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.pojobtyperatio;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.pojobtyperatio.IPoJobTypeRatioSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.pojobtyperatio.PoJobTypeRatioReqDTO;
import my.ep.dto.pm.common.pojobtyperatio.PoJobTypeRatioResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmPoJobTypeRatio;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
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
	private boolean applDisabled;
	private boolean categoryDisabled;
	
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
}
