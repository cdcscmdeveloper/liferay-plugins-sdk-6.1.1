package my.ep.web.backingbean.trackingdiary;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.managedbean.trackingdiary.PpTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.PpCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "ppTrackingDiaryBackingBean")
@RequestScoped
public class PpTrackingDiaryBackingBean extends
		BaseBackingBean<PpTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(PpTrackingDiaryBackingBean.class);
	public static final String NAV_TO_PP_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_DETAIL_SEARCH = "ppTrackingDiaryDetailSearch.xhtml";
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	
	public PpTrackingDiaryBackingBean() {
	}
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	//Authorization 
	private ISearchBean<PmParameterDesc> planTypeSearchBean;
	
	public ISearchBean<PmParameterDesc> getPlanTypeSearchBean() {
		log.info("getPlanTypeSearchBean");
		if (this.planTypeSearchBean == null) {
			this.planTypeSearchBean = super.createSearchBean(PmParameterDesc.class, "somPlanType");
		}

		return this.planTypeSearchBean;
	}
	/**
	 * get Existing Plan Year options
	 * @return SelectItem[]
	 */
	public SelectItem[] getExistingPlanYearOptions() {
		log.info("getExistingPlanYearOptions");
		SelectItem[] items = null;
		List <Long> planYears = null; 
		Long orgProfileId = null;
		try{
			
			//default
			items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
			planYears = PpCommonUtil.getExistingPlanYear(getFacesScopes().getUserSessionBean().getUserId(), orgProfileId,null);
			
			if (planYears!=null && !planYears.isEmpty()) {
				int i = 1;
				items = new SelectItem[planYears.size()+1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				for(Long year:planYears){
					items[i] = new SelectItem(year, year.toString());
					i++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return items;
	}
	

	/**
	 * get Existing Plan Year options
	 * @return SelectItem[]
	 *//*
	public SelectItem[] getExistingPlanYearOptions() {
		log.info("getExistingPlanYearOptions");
		SelectItem[] items = null;
		try{
				items = new SelectItem[8];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				for(int i=0;i<7;i++){
					items[i+1] = new SelectItem(new Long(2016-i), new Long(2016-i).toString());
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return items;
	}*/

	public void search() {
		System.out.println("ppTrackingDiaryBackingBean search");
		boolean isEmpty = true;
		ProcurementPlanSearchBean planSearchBean = getManagedBean().getPlanSearchBean();
		
		if(!StringUtils.isBlank(planSearchBean.getPlanNumber())){
			 isEmpty = false;
		 }else if(planSearchBean.getYear()!=null){
			 isEmpty = false;
		 }else if(planSearchBean.getPlanType().getParameterId()!=null){
			 isEmpty = false;
		 }
		 if(isEmpty){
			 throw new EpException(MessageConst.ERROR_730027);
		 }
		getManagedBean().getPpDocumentDataModel().reloadData();
	}
	
	public void reset(){
		log.info("resetPlanSearchBean");
		getManagedBean().setPlanSearchBean(new ProcurementPlanSearchBean());
		//getManagedBean().setPpDocumentDataModel(n).searchTrackingDiaryDataModel = new SearchTrackingDiaryPagingDataModel();
		renewSesionObject();
	}
	
	/**
	 * Load and generate default buttons
	 *//*
	private void loadDefaultButtons(){
		log.info("loadDefaultButtons");
		//if status is approved
		Long statusId = this.getManagedBean().getSelectedPlan().getStatus().getStatusId();
		if(isRolePlanner() && 
				(ProcurementPlanConst.PPP_APPROVED.equals(statusId) 
				|| ProcurementPlanConst.PPE_APPROVED.equals(statusId) 
				|| this.getManagedBean().getSelectedPlan().isBrandNew())){
			//BPM buttons
			String[] outcome = new String[2];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			outcome[1] = BpmOutcomeConst.BPM_SUBMIT;
			this.getManagedBean().setOutComes(outcome);
		}else{
			this.getManagedBean().setOutComes(null);
		}
	}*/
	

//	/**
//	 * get VOT Type options
//	 * @return SelectItem[]
//	 */
//	public SelectItem[] getVotTypeOptions() {
//		log.info("getVotTypeOptions");
//		return getDropdownValues(ParameterTypeConst.VOT_TYPE);
//	}
	
//	/**
//	 * reset search bean fields
//	 */
//	public void resetPlanSearchBean(){
//		log.info("resetPlanSearchBean");
//		this.getManagedBean().setSearch(false);
//		this.getManagedBean().setPlanSearchBean(new ProcurementPlanSearchBean());
////		generateOrgFilter();
//		ppPlanDataModel = new PpPlanDataModel();
//	}
	
	/**
	 * search plan
	 */
	public void searchPlan(){
		log.info("searchPlan");
//		this.getManagedBean().setSearch(true);
		boolean isEmpty = true;
		  if(!StringUtils.isBlank(this.getManagedBean().getPlanSearchBean().getPlanNumber())){
			  isEmpty = false;
		  }else if(this.getManagedBean().getPlanSearchBean().getYear()!=null){
			  isEmpty = false;
		  }else if(this.getManagedBean().getPlanSearchBean().getPlanType().getParameterId()!=null){
			  isEmpty = false;
		  }
		  if(isEmpty){
			  throw new EpException(MessageConst.ERROR_730027);
		  }
		getManagedBean().getPpDocumentDataModel().reloadData();
	}
	
	/**
	 * @param ppPlan
	 * @return navigate to detail page
	 */
	public String goToTrackingDiarySearch(PpPlan ppPlan) {
		getManagedBean().setPpPlan(ppPlan);
		searchTrackingDiaryDataModel.reloadData();
		return NAV_TO_DETAIL_SEARCH + "?faces-redirect=true";
	}
	

//	public void search() {
//		System.out.println("ScTrackingDiaryBackingBean search");
//		if ((null == getManagedBean().getSearchDocumentType()) 
////				&& (getManagedBean().getSearchUNSPSCExtensionCodeValue() != null && getManagedBean().getSearchUNSPSCExtensionCodeValue().isEmpty()) 
////				&& (getManagedBean().getSearchSupplierValue() != null && getManagedBean().getSearchSupplierValue().isEmpty())
//				) {
//			throw new EpException(MessageConst.ERROR_776101);
//		}
//		getManagedBean().getScRNDocumentDataModel().reloadData();
//	}

	private class SearchTrackingDiaryPagingDataModel extends PagingDataModel<PmTrackingDiary> {

		private static final long serialVersionUID = 1L;

		public SearchTrackingDiaryPagingDataModel() {
			super("trackingDiaryTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(
				ISearchBean<PmTrackingDiary> searchBean) {
//			userSO.retrieveUserByUserId;
//			Object[] scObj = getManagedBean().getScObj();

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
//			searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),scObj[3]));
//			searchBean.getDataFilterBeans().add(new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),scObj[0]));

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("resDTO.getResultBean()====================================: "+ resDTO.getResultBean().getTotalRecord());
			return resDTO.getResultBean();
			
		}
	}


	public String backToPrev() {

		return NAV_TO_PP_SEARCH + "?faces-redirect=true";
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

}

