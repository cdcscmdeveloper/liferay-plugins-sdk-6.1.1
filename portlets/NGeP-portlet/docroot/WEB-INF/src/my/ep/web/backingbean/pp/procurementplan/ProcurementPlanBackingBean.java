package my.ep.web.backingbean.pp.procurementplan;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pp.procurementplan.ProcurementPlanManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "pPlanBackingBean")
@ViewScoped
public class ProcurementPlanBackingBean extends BaseBackingBean<ProcurementPlanManagedBean> {
	private static Logger log = Logger.getLogger(ProcurementPlanBackingBean.class);
	//Navigation Details
	public static final String NAV_TO_MAINTAIN = "procurementPlanDet";
	public static final String NAV_TO_SEARCH = "procurementPlanMain";
	
	//Plan Type
	public static final int PLAN_PROCUREMENT = 1;
	public static final int PLAN_EXPENDITURE = 2;
	
	//List for Paging
	private IPagingDataModel<Object> ppPlanDataModel = new PpPlanDataModel();
	
	public void preLoading(){
		log.info("preLoading");
//		this.resetSupportDocSearchBean();
//		this.getManagedBean().setSelectedTabIndex(0);
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}
	
	/**
	 * redirect to Plan Search
	 * @return xhtml
	 */
	public String redirectToSearchPlan(){
		log.info("redirectToSearchPlan - Start");
		try{
			
			
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("redirectToSearchPlan - End");
		return NAV_TO_SEARCH;
	
	}
	
	/**
	 * redirect to user details
	 * @param id
	 * @return
	 */
	public String redirectToMaintainPlan(Long id) throws Exception{
		log.info("redirectToMaintainPlan");
//		this.retrieveIdentityTypeMap();
//		this.getManagedBean().setSelectedTabIndex(TAB_USER_PROFILE);
		if(id!=null && id>0){
			loadPlanDetails(id);
		}else{
			initialPlanDetails();
		}
		return NAV_TO_MAINTAIN;
	}
	
	/**
	 * load plan object by id
	 * @param id
	 */
	private void loadPlanDetails(Long id){
		log.info("loadPlanDetails - Start");
		log.info("loadPlanDetails - End");
	}
	
	/**
	 * initial new pmUser object
	 */
	private void initialPlanDetails(){
		log.info("initialPlanDetails - Start");
		log.info("initialPlanDetails - End");
	}
	/**
	 * reset search bean fields
	 */
	public void resetPlanSearchBean(){
		log.info("resetPlanSearchBean");
//		this.getManagedBean().setSupportDocSearchBean(new SupportDocumentSearchBean());
	}
	
	/**
	 * search plan
	 */
	public void searchPlan(){
		log.info("searchPlan - Start");
		ppPlanDataModel.reloadData();
		log.info("searchPlan - End");
		
	}

	/**
	 * @return the ppPlanDataModel
	 */
	public IPagingDataModel<Object> getPpPlanDataModel() {
		return ppPlanDataModel;
	}

	/**
	 * @param ppPlanDataModel the ppPlanDataModel to set
	 */
	public void setPpPlanDataModel(IPagingDataModel<Object> ppPlanDataModel) {
		this.ppPlanDataModel = ppPlanDataModel;
	}
	
	/**
	 * paginate for document list
	 * @author RainPoh
	 *
	 */
	private class PpPlanDataModel extends PagingDataModel<Object> {
		private static final long serialVersionUID = 1L;

		public PpPlanDataModel() {
			super("dtPlanResult");
		}
		/**
		 * @author rainpoh
		 * @since 13.10.2014
		 * 
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<Object> pageChangeListener(ISearchBean<Object> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			
			try {	
//				ISearchBean<PmDocument> searchBeanData = getSearchBeanData(searchBean);
//				DocumentReqDTO reqDTO = new DocumentReqDTO();
//				reqDTO.setDocumentSearchBean(searchBeanData);
//				IDocumentSO reqSO = (IDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);	
//				DocumentResDTO resDTO = reqSO.retrieveDocumentBySearchBean(reqDTO);
//				if (resDTO.isSuccessful()) {
//					return resDTO.getDocumentResultBean();
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * @return the planProcurement
	 */
	public int getPlanProcurement() {
		return PLAN_PROCUREMENT;
	}


	/**
	 * @return the planExpenditure
	 */
	public int getPlanExpenditure() {
		return PLAN_EXPENDITURE;
	}
	
	
	

}
