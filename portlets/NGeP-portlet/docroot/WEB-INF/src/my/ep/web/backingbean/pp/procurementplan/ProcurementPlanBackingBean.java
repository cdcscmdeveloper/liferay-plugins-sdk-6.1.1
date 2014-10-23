package my.ep.web.backingbean.pp.procurementplan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pp.procurementplan.PpExpenditureItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanActor;
import my.ep.persistence.entity.pp.procurementplan.PpProcurementItem;
import my.ep.persistence.entity.pp.procurementplan.PpRejectReason;
import my.ep.persistence.entity.pp.procurementplan.PpRemark;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.managedbean.pp.procurementplan.ProcurementPlanManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;


/**
 * Backing Bean for Procurement Plan 
 * 
 * @author RAIN POH
 * @since 20/10/2013
 *
 */
@ManagedBean(name = "pPlanBackingBean")
@ViewScoped
public class ProcurementPlanBackingBean extends BaseBackingBean<ProcurementPlanManagedBean> {
	private static Logger log = Logger.getLogger(ProcurementPlanBackingBean.class);
	//Navigation Details
	public static final String NAV_TO_MAINTAIN = "procurementPlanDet";
	public static final String NAV_TO_SEARCH = "procurementPlanMain";
	
	//List for Paging
	private IPagingDataModel<PpPlan> ppPlanDataModel = new PpPlanDataModel();
	
	//Default value
	public static final boolean SEARCH_DEFAULT = false;
	
	//Mapper for DDL object
	public static Map<Long, Object> planTypeMapper;
		
		
	public Long TEMP_ORG_ID = 32659L;
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.info(message);
		}
		log.info(message);
	}
	
	public void preLoading(){
		log.info("preLoading");
		this.resetPlanSearchBean();
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
	 * redirect to plan details
	 * @param id
	 * @return
	 */
	public String redirectToMaintainPlan(Long id) throws Exception{
		log.info("redirectToMaintainPlan");
		this.retrievePlanTypeMapper();
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
	 * initial new plan object
	 */
	private void initialPlanDetails(){
		log.info("initialPlanDetails - Start");
		if(getFacesScopes().getUserSessionBean().getUserId()==null){
			//TODO 
			throw new EpException("Unable to get UserId from session. Please login.");
		}
		PmOrgProfile pmOrgProfile =PmCommonUtil.retrieveUserOrgListByUserId(getFacesScopes().getUserSessionBean().getUserId());
		if(pmOrgProfile==null || pmOrgProfile.getOrgProfileId()==null){
			//TODO 
			throw new EpException("Unable to retrieve Org. Profile.");
		}
		PpPlan ppPlan = new PpPlan();
		ppPlan.setPlanType(new PmParameter());
		ppPlan.setVotFundType(new PmParameter());
		ppPlan.setOrgProfile(pmOrgProfile); //TODO get from login profile
		ppPlan.setPpPlanActors(new ArrayList<PpPlanActor>());
		ppPlan.setStatus(new PmStatus());
		initialPlanRemark(ppPlan);
		loadPlanActors(TEMP_ORG_ID); //ppPlan.getOrgProfile().getOrgProfileId() TODO
		this.getManagedBean().setSelectedPlan(ppPlan);
		retrieveParentOrg();
		log.info("initialPlanDetails - End");
	}
	
	public String getStatusDescById(Long id){
		log.info("getStatusDescById");
		PmStatusDesc statusDesc = StatusHelper.getInstance().getStatusDescById(id);
		return statusDesc!=null ? statusDesc.getStatusName() : "";
	}
	
	private void retrieveParentOrg(){
		log.info("retrieveParentOrg");
		if(this.getManagedBean().getSelectedPlan().getOrgProfile().getParentOrgProfile()==null){
			this.getManagedBean().getSelectedPlan().getOrgProfile().setParentOrgProfile(new PmOrgProfile());
		}
		
	}
	
	/**
	 * generate actor source
	 * @param orgProfileId
	 */
	private void loadPlanActors(Long orgProfileId){
		if(this.getManagedBean().getVerifierModel()==null) {
			List<String> source = new ArrayList<String>(); 
	        List<String> target = new ArrayList<String>();  
	        this.getManagedBean().setVerifierModel(new DualListModel<String>(source, target));
	        generateSourceVerifiers(orgProfileId, RoleCode.PLAN_VERFICATION_OFFICER.toString());
		}
		if(this.getManagedBean().getApproverModel()==null) {
			List<String> source = new ArrayList<String>(); 
	        List<String> target = new ArrayList<String>();  
	        this.getManagedBean().setApproverModel(new DualListModel<String>(source, target));
	        generateSourceApprovers(orgProfileId, RoleCode.PLAN_APPROVER.toString());
		}	
	}
	
	public void initialPlanRemark(PpPlan plan) {
		log.info("initialPlanRemark - Start");
		if (plan.getPpRemarks() == null) {
			plan.setPpRemarks(new ArrayList<PpRemark>());
		}
		if(plan.getPpRemarks().size()==0 || !compareUserName(plan.getPpRemarks().get(0).getCreatedUser().getLoginId())) {
			PpRemark remark = new PpRemark();
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedUser(getFacesScopes().getUserSessionBean());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			plan.addPpRemarks(0, remark);
		}
		log.info("initialPlanRemark - End");
		
	}
	
	public void initialRejectReasons() {
		log.info("initialRejectReasons - Start");
		if (this.getManagedBean().getSelectedRejectReasons() == null) {
			this.getManagedBean().setSelectedRejectReasons(new ArrayList<PpRejectReason>());
		}
		if(this.getManagedBean().getSelectedRejectReasons()==null || this.getManagedBean().getSelectedRejectReasons().isEmpty()){
			PpRejectReason reason = new PpRejectReason();
			reason.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
//			reason.setCreatedUser(getFacesScopes().getUserSessionBean());
			reason.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			reason.setChangedBy(reason.getCreatedBy());
			reason.setChangedDate(reason.getCreatedDate());
			this.getManagedBean().getSelectedRejectReasons().add(reason);
		}
		log.info("initialRejectReasons - End");
		
	}
	
//	public void initializeRemarkAndAttachement(List<CtRemark> remarkList, List<CtAttachment> attachmentList) {
//		FacesScopes facesScopes = new FacesScopes();
//		
//		if(remarkList.size()==0 || !compareUserName(remarkList.get(0).getCreatedUser().getLoginId())) {
//			CtRemark remark = new CtRemark();
//			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
//			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
//			remark.setChangedBy(remark.getCreatedBy());
//			remark.setChangedDate(remark.getCreatedDate());
//			remark.setCreatedUser(facesScopes.getUserSessionBean());
//			getManagedBean().setRemarkList(remarkList);
//			getManagedBean().getRemarkList().add(0, remark);
//		} else {
//			getManagedBean().setRemarkList(remarkList);
//		}
//		getManagedBean().setAttachmentList(attachmentList);
//	}
//	
	
	/**
	 * retrieve plan type into mapper
	 */
	private void retrievePlanTypeMapper(){
		log.info("retrievePlanTypeMapper");
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.PLAN_TYPE, null);
		planTypeMapper = new HashMap<Long,Object>();
		for(PmParameterDesc pmDesc: parameterDescs){
			planTypeMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}
	

	/**
	 * generate view for plan item based on selected plan type
	 */
	public void changePlanType(){
		log.info("changePlanType");
		Long planTypeId = this.getManagedBean().getSelectedPlan().getPlanType().getParameterId();
		if(planTypeId != null){
			PmParameter planType = (PmParameter)planTypeMapper.get(planTypeId);
			this.getManagedBean().getSelectedPlan().getPlanType().setParameterCode(planType!=null? planType.getParameterCode() : null);
			if(planType!=null){
				if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(planType.getParameterCode())){
					generateEpPlan();
				}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(planType.getParameterCode())){
					generatePpPlan();
				}
			}
		}else{
			this.getManagedBean().getSelectedPlan().setPlanType(new PmParameter());
		}
	}
	
	/**
	 * generate procurement items
	 */
	private void generatePpPlan(){
		log.info("generatePpPlan");
		this.getManagedBean().getSelectedPlan().setPpProcurementItems(new ArrayList<PpProcurementItem>());
		this.getManagedBean().getSelectedPlan().setPpExpenditureItems(null);
		
	}
	
	/**
	 * generate expenditure items
	 */
	private void generateEpPlan(){
		log.info("generateEpPlan");
		this.getManagedBean().getSelectedPlan().setPpExpenditureItems(new ArrayList<PpExpenditureItem>());
		this.getManagedBean().getSelectedPlan().setPpProcurementItems(null);
	}
	
	/**
	 * add new ep item to list
	 */
	public void addEpItemDet(){
		log.info("addEpItemDet");
		if(this.getManagedBean().getSelectedPlan().getPpExpenditureItems()==null){
			this.getManagedBean().getSelectedPlan().setPpExpenditureItems(new ArrayList<PpExpenditureItem>());
		}
		PpExpenditureItem epItem = new PpExpenditureItem();
		epItem.setPmSo(new PmSo());
		this.getManagedBean().getSelectedPlan().addPpExpenditureItems(epItem);

	}
	
	/**
	 * add calculate non allocation budget 
	 */
	public void calculateNonAllocationBudget(PpExpenditureItem item){
		log.info("calculateNonAllocationBudget");
		if(item.getBudgetAmt()!=null && item.getPlanAmt()!=null){
			item.setNonAllocatedAmt(item.getBudgetAmt().subtract(item.getPlanAmt()));
		}
	}
	
	/**
	 * add new pp item to list
	 */
	public void addPpItemDet(){
		log.info("planPpItemDet");
		if(this.getManagedBean().getSelectedPlan().getPpProcurementItems()==null){
			this.getManagedBean().getSelectedPlan().setPpProcurementItems(new ArrayList<PpProcurementItem>());
		}
		PpProcurementItem ppItem = new PpProcurementItem();
		ppItem.setProcurementCat(new PmParameter());
		ppItem.setProcurementType(new PmParameter());
		this.getManagedBean().getSelectedPlan().addPpProcurementItems(ppItem);
	}
	
	/**
	 * get VOT Type options
	 * @return SelectItem[]
	 */
	public SelectItem[] getVotTypeOptions() {
		log.info("getVotTypeOptions");
		return getDropdownValues(ParameterTypeConst.VOT_TYPE);
	}
	
	/**
	 * get Plan Type options
	 * @return SelectItem[]
	 */
	public SelectItem[] getPlanTypeOptions() {
		log.info("getPlanTypeOptions");
		return getDropdownValues(ParameterTypeConst.PLAN_TYPE);
	}
	
	/**
	 * get Category options
	 * @return SelectItem[]
	 */
	public SelectItem[] getCategoryOptions() {
		log.info("getCategoryOptions");
		return getDropdownValues(ParameterTypeConst.PROCUREMENT_CATEGORY); //TODO
	}
	
	/**
	 * get Procurement Type options
	 * @return SelectItem[]
	 */
	public SelectItem[] getProcurementTypeOptions() {
		log.info("getProcurementTypeOptions");
		return getDropdownValues(ParameterTypeConst.SC_PROCUREMENT_MODE); //TODO
	}
	
	/**
	 * get Existing Plan Year options
	 * @return SelectItem[]
	 */
	public SelectItem[] getExistingPlanYearOptions() {
		log.info("getExistingPlanYearOptions");
		SelectItem[] items = null;
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setOrgProfileId(TEMP_ORG_ID);//TODO this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId()
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.retrieveExistingPlanYears(reqDTO);
			if (resDTO.isSuccessful() && resDTO.getPlanYears()!=null) {
				int i = 1;
				items = new SelectItem[resDTO.getPlanYears().size()];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				for(Long year:resDTO.getPlanYears()){
					items[i] = new SelectItem(year, year.toString());
					i++;
				}
			}else{
				items = new SelectItem[1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
			}
		}catch(Exception e){
			throw new EpException(e.toString()); //TODO
		}finally{
			reqSO = null;
		}
		return items;
	}
	
	/**
	 * get Plan Year options
	 * @return SelectItem[]
	 */
	public SelectItem[] getPlanYearOptions() {
		log.info("getPlanYearOptions");
		Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
		Integer nextYear = currentYear + 1;
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
		items[1] = new SelectItem(currentYear.longValue(), currentYear.toString());
		items[2] = new SelectItem(nextYear.longValue(), nextYear.toString());
		return items;
	}
	
//	private String getUserRole() {		
//		if(getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) 
//			return RoleCode.MIN_CONTRACT_ADMIN.value();
//		else if(getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
//			return RoleCode.CT_APPROVER.value();
//		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()))
//			return RoleCode.BPK_CCA.value();
//		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()))
//			return RoleCode.BPK_APPROVER.value();
//		else 
//			return null;
//	}

	private void generateSourceVerifiers(Long orgProfileId, String roleCode){
		log.info("generateSourceVerifiers");
		List<String> sourceList = new ArrayList<String>();
		List<PmUser> actorList = PmCommonUtil.retrieveUserListByRole(orgProfileId, roleCode);
		for (PmUser user : actorList) {
			sourceList.add(user.getUserName());
//			getManagedBean().getUserMap().put(user.getUserName(), user);
		}
		this.getManagedBean().getVerifierModel().setSource(sourceList);
	}
	
	private void generateSourceApprovers(Long orgProfileId, String roleCode){
		log.info("generateSourceApprovers");
		List<String> sourceList = new ArrayList<String>();
		List<PmUser> actorList = PmCommonUtil.retrieveUserListByRole(orgProfileId, roleCode);
		for (PmUser user : actorList) {
			sourceList.add(user.getUserName());
//			getManagedBean().getUserMap().put(user.getUserName(), user);
		}
		this.getManagedBean().getApproverModel().setSource(sourceList);
	}
	
	
	
	
	
	
	
	
	/**
	 * get search bean 
	 * @return ISearchBean<PpPlan>
	 */
	private ISearchBean<PpPlan> getSearchBeanData(ISearchBean<PpPlan> searchBean){
		log.info("getSearchBeanData - Start");

		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		ProcurementPlanSearchBean planSearchBean = this.getManagedBean().getPlanSearchBean();
			System.out.println(planSearchBean);
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
		// ministry
		if(planSearchBean.getMinistry()!=null && planSearchBean.getMinistry().getOrgProfileId()!=null){
			log.info("+ getMinistry "  +planSearchBean.getMinistry().getOrgProfileId());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("orgProfile.orgProfileId");
			bean.setSearchValue(planSearchBean.getMinistry().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		// ptj
		if(planSearchBean.getPtj()!=null && !"".equalsIgnoreCase(planSearchBean.getPtj())){
			log.info("+ getPtj "  +planSearchBean.getPtj());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			bean.setCaseInsensitive(true);
			bean.setSearchField("orgProfile.pmOrgValidity.orgName");
			bean.setSearchValue(planSearchBean.getPtj());
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
		// vot type
		if(planSearchBean.getVotType()!=null && planSearchBean.getVotType().getParameterId()!=null){
			log.info("+ getVotType "  +planSearchBean.getVotType().getParameterId());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("votFundType.parameterId");
			bean.setSearchValue(planSearchBean.getVotType().getParameterId());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		
		// status
		if(planSearchBean.getStatus()!=null && planSearchBean.getStatus()!=null){
			log.info("+ getStatus "  +planSearchBean.getStatus());
		
		}
		
		//base on the flag, default list is either select all or empty
		if(isEmpty && !isSearchable){
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
	 * reset search bean fields
	 */
	public void resetPlanSearchBean(){
		log.info("resetPlanSearchBean");
		this.getManagedBean().setPlanSearchBean(new ProcurementPlanSearchBean());
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
	public IPagingDataModel<PpPlan> getPpPlanDataModel() {
		return ppPlanDataModel;
	}

	/**
	 * @param ppPlanDataModel the ppPlanDataModel to set
	 */
	public void setPpPlanDataModel(IPagingDataModel<PpPlan> ppPlanDataModel) {
		this.ppPlanDataModel = ppPlanDataModel;
	}
	
	/**
	 * paginate for plan list
	 * @author RainPoh
	 *
	 */
	private class PpPlanDataModel extends PagingDataModel<PpPlan> {
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

	

}
