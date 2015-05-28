package my.ep.web.backingbean.trackingdiary;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.managedbean.trackingdiary.PpTrackingDiaryManagedBean;
import my.ep.web.managedbean.trackingdiary.ScTrackingDiaryManagedBean;
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
		try{
			items = new SelectItem[8];
//			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
			
				int i = 1;
//				items = new SelectItem[planYears.size()+1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				for(i=0;i<7;i++){
					items[i+1] = new SelectItem(new Long(2013-i), new Long(2013-i).toString());
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return items;
	}

	public void search() {
		System.out.println("ScTrackingDiaryBackingBean search");
//		if ((null == getManagedBean().getSearchDocumentType()) 
//				&& (getManagedBean().getSearchUNSPSCExtensionCodeValue() != null && getManagedBean().getSearchUNSPSCExtensionCodeValue().isEmpty()) 
//				&& (getManagedBean().getSearchSupplierValue() != null && getManagedBean().getSearchSupplierValue().isEmpty())
//				) {
//			throw new EpException(MessageConst.ERROR_776101);
//		}
		getManagedBean().getPpDocumentDataModel().reloadData();
	}

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

	public String goToTrackingDiarySearch(Object[] scObj) {
		searchTrackingDiaryDataModel.reloadData();
		return NAV_TO_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public void reset(){
	}

	public String backToPrev() {

		return NAV_TO_SC_SEARCH + "?faces-redirect=true";
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

}

