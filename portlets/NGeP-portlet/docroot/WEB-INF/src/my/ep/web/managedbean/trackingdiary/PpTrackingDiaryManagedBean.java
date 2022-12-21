/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
//import my.ep.web.backingbean.trackingdiary.PpPlanDataModel;

/**
 * Managed Bean for Fulfillment document
 * 
 * @author Alvin
 * @since 2/11/2013
 */
@ManagedBean(name = "ppTrackingDiaryManagedBean")
@SessionScoped
public class PpTrackingDiaryManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(PpTrackingDiaryManagedBean.class);

	private static final long serialVersionUID = 1L;
	
	private ProcurementPlanSearchBean planSearchBean = new ProcurementPlanSearchBean();
	private IPagingDataModel<PpPlan> ppDocumentDataModel = new PpDocumentPagingDataModel();
	
	private PpPlan ppPlan;
	
	/**
	 * Get search bean, filtered based on login org.
	 * @return ISearchBean<PpPlan>
	 */
	private ISearchBean<PpPlan> getSearchBeanData(ISearchBean<PpPlan> searchBean){
		log.info("getSearchBeanData - Start");

//		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		ProcurementPlanSearchBean planSearchBean = getPlanSearchBean();
		
		// plan type
		if(planSearchBean.getPlanType()!=null && planSearchBean.getPlanType().getParameterId()!=null){
			log.info("+ getPlanType "  +planSearchBean.getPlanType().getParameterId());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("planType.parameterId");
			bean.setSearchValue(planSearchBean.getPlanType().getParameterId());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		
		// plan number
		if(planSearchBean.getPlanNumber()!=null && !StringUtils.isBlank(planSearchBean.getPlanNumber())){
			log.info("+ getPlanNumber "  +planSearchBean.getPlanNumber());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setCaseInsensitive(true);
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			bean.setSearchField("planNo");
			bean.setSearchValue(planSearchBean.getPlanNumber());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		
		// year
		if(planSearchBean.getYear()!=null && !"".equalsIgnoreCase(planSearchBean.getYear())){
			log.info("+ getYear "  +planSearchBean.getYear());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("planYear");
			bean.setSearchValue(planSearchBean.getYear());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		
		//base on the flag, default list is either select all or empty
		if(isEmpty){
			log.info("+ status "  + null);
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(null);
			searchBean.getDataFilterBeans().add(bean);
		}
		log.info("getSearchBeanData - End");
		return searchBean;
	}
	
	/**
	 * paginate for plan list
	 * @author 
	 *
	 */
	private class PpDocumentPagingDataModel extends PagingDataModel<PpPlan> {
		private static final long serialVersionUID = 1L;

		public PpDocumentPagingDataModel() {
			super("pPlanResult");
		}
		@Override
		public ISearchResultBean<PpPlan> pageChangeListener(ISearchBean<PpPlan> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			
			try {	
					ISearchBean<PpPlan> searchBeanData = getSearchBeanData(searchBean);
					ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
					reqDTO.setSearchBean(searchBeanData);
					IProcurementPlanSO reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
					ProcurementPlanResDTO resDTO = reqSO.retrievePlanBySearchBean(reqDTO);
					if (resDTO.isSuccessful()) {
						return resDTO.getPlanResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}
	/* --------------------------------------------------------- Search & Result - END -----------------------------------------------------------------*/
	public ProcurementPlanSearchBean getPlanSearchBean() {
		return planSearchBean;
	}
	public void setPlanSearchBean(ProcurementPlanSearchBean planSearchBean) {
		this.planSearchBean = planSearchBean;
	}
	public IPagingDataModel<PpPlan> getPpDocumentDataModel() {
		return ppDocumentDataModel;
	}
	public void setPpDocumentDataModel(IPagingDataModel<PpPlan> ppDocumentDataModel) {
		this.ppDocumentDataModel = ppDocumentDataModel;
	}
	
	/**
	 * @return the ppPlan
	 */
	public PpPlan getPpPlan() {
		return ppPlan;
	}
	/**
	 * @param ppPlan the ppPlan to set
	 */
	public void setPpPlan(PpPlan ppPlan) {
		this.ppPlan = ppPlan;
	}
	
}
