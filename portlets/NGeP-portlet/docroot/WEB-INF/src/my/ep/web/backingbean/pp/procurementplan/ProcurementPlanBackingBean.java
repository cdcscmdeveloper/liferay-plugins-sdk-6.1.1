package my.ep.web.backingbean.pp.procurementplan;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pp.ProcurementPlanConst;
import my.ep.constants.PPProcessNameConst;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.pp.plandata.PPPlanDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmProcurementCatType;
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
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.pp.procurementplan.ProcurementPlanManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.pp.common.PpCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import com.liferay.portal.util.PortalUtil;


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
	
		
		
//	public Long TEMP_ORG_ID = 32659L;
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
	
	// From Search Plan
	public void preLoading(){
		log.info("preLoading");
		this.resetPlanSearchBean();
		this.resetPageModeFlags();
		this.resetManagedBean();
		initialSearchPlan();
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	// From Task List
	public void preRenderView() {
		log.info("preRenderView");
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		if (taskId == null) {
			return;
		}
		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);

		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();

		//only for DRAFT (check status)
		if(PPProcessNameConst.SUBMIT_PLAN.equalsIgnoreCase(res.getTaskName())){
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
			this.getManagedBean().setOutComes(outcome);
		}else{
			this.getManagedBean().setOutComes(res.getAction());
		}
		
		this.getManagedBean().setTaskName(res.getTaskName());
		this.getManagedBean().setTaskId(taskId);
		generatePageModeForProcess(res.getTaskName());
		
		// Procurement Plan
		PPPlanDataType planObj = (PPPlanDataType) obj;
		this.loadPlanDetails(planObj.getPlanId().longValue());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAINTAIN);
	}
	

	/**
	 * Generate page mode based on process name
	 * @param processName
	 */
	private void generatePageModeForProcess(String processName){
		log.info("generatePageModeForProcess");
		this.getManagedBean().setModeReview(false);
		this.getManagedBean().setModeReviewEditable(false);
		this.getManagedBean().setModeEditable(false);
		if(!StringUtils.isBlank(processName)){
			// DRAFT & REVISION & UPDATE
			if(PPProcessNameConst.REVISE_PLAN.equalsIgnoreCase(processName)
					|| PPProcessNameConst.SUBMIT_PLAN.equalsIgnoreCase(processName)){
				this.getManagedBean().setModeReview(false);
				this.getManagedBean().setModeReviewEditable(false);
				this.getManagedBean().setModeEditable(true);
				
			// APPROVAL & VERIFICATION	
			}else if(PPProcessNameConst.REVIEW_PLAN.equalsIgnoreCase(processName)
					|| PPProcessNameConst.VERIFY_PLAN.equalsIgnoreCase(processName)){
				this.getManagedBean().setModeEditable(false);
				this.getManagedBean().setModeReview(true);
				this.getManagedBean().setModeReviewEditable(true);
			}
		}
		log.info("MODE REVIEW: " + this.getManagedBean().isModeReview());
		log.info("MODE REVIEW EDITABLE: " + this.getManagedBean().isModeReviewEditable());
		log.info("MODE EDITABLE: " + this.getManagedBean().isModeEditable());
	}
	 
	
	
	private void initialSearchPlan(){
		log.info("initialSearchPlan");
	
		PmOrgProfile pmOrgProfile = PpCommonUtil.retrieveUserOrgListByUserId(getFacesScopes().getUserSessionBean().getUserId(), true);
		if(pmOrgProfile==null || pmOrgProfile.getOrgProfileId()==null){
			throw new EpException("Unable to retrieve Org. Profile.");//TODO 
		}
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
		generateOrgFilter();
	}
	
	public void generateOrgFilter(){
		log.info("generateOrgFilter");
		//check role and populate drop down value accordingly
		StringBuilder conditionSB = new StringBuilder();
		conditionSB.append("orgTypeCode.parameterCode^");
		conditionSB.append(ParameterConst.ORG_TYPE_MINISTRY);
		conditionSB.append("|");
		conditionSB.append("recordStatus^1^");
		
		if(!isRoleBPK()){
			if(isRoleApprover()){
				conditionSB.append("|");
				conditionSB.append("orgProfileId^");
				conditionSB.append(this.getCurrentActiveOrg().getOrgProfileId());
				
			}else{
				conditionSB.append("|");
				conditionSB.append("orgProfileId^");
				conditionSB.append(this.getCurrentActiveOrg().getParentOrgProfile().getOrgProfileId());
			}
		}
			
		this.getManagedBean().setFilterOrgCondition(conditionSB.toString());

	}
	
	/**
	 * Reset all the critical page mode flags
	 */
	private void resetPageModeFlags(){
		log.info("resetPageModeFlags");
		this.getManagedBean().setModeReview(false);
		this.getManagedBean().setModeEditable(false);
		this.getManagedBean().setModeReviewEditable(false);

//		this.getManagedBean().setCurrentRole(null); //TODO
//		this.getManagedBean().setCurrentStatus(null); //TODO
	}
	
	private void resetManagedBean(){
		log.info("resetManagedBean");
		this.getManagedBean().setEpTotalBudget(null);
		this.getManagedBean().setEpTotalPlan(null);
		this.getManagedBean().setEpTotalNonEpAlloc(null);
		this.getManagedBean().setPpTotalEst(null);
		this.getManagedBean().setDeleteEpItems(null);
		this.getManagedBean().setDeletePpItems(null);
		this.getManagedBean().setApproverModel(null);
		this.getManagedBean().setVerifierModel(null);
		this.getManagedBean().setSelectedRejectReasons(null);
		this.getManagedBean().setCurrentActiveOrg(null);
		this.getManagedBean().setFilterOrgCondition(null);
	
	
	}
	
	/**
	 * redirect to Plan Search
	 * @return xhtml
	 */
	public String redirectToSearchPlan(){
		log.info("redirectToSearchPlan - Start");
		try{
			initialSearchPlan();
			
			
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
		this.resetManagedBean();
		this.retrievePlanTypeMapper();
		
		if(id!=null && id>0){
			loadPlanDetails(id);
			loadDefaultButtons();
		}else{
			initialPlanDetails();
			loadDefaultButtons();
		}
	
		return NAV_TO_MAINTAIN;
	}
	
	/**
	 * load and generate default buttons
	 */
	private void loadDefaultButtons(){
		log.info("loadDefaultButtons");
		//if status is approved
		Long statusId = this.getManagedBean().getSelectedPlan().getStatus().getStatusId();
		if(isRolePlanner() && 
				(ProcurementPlanConst.PPP_APPROVED.equals(statusId) 
				|| ProcurementPlanConst.PPE_APPROVED.equals(statusId) 
				|| this.getManagedBean().getSelectedPlan().isBrandNew())){
			log.info("loadDefaultButtons2");
			//BPM buttons
			String[] outcome = new String[2];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			outcome[1] = BpmOutcomeConst.BPM_SUBMIT;
			this.getManagedBean().setOutComes(outcome);
		}else{
			this.getManagedBean().setOutComes(null);
		}
	}
	/**
	 * load plan object by id
	 * @param id
	 */
	private void loadPlanDetails(Long id){
		log.info("loadPlanDetails - Start");
		PpPlan ppPlan = retrievePlanById(id);
		if(ppPlan!=null){
			PmOrgProfile pmOrgProfile = PpCommonUtil.retrievePmOrgProfileDtl(ppPlan.getOrgProfile().getOrgProfileId());
			if(pmOrgProfile==null || pmOrgProfile.getOrgProfileId()==null){
				throw new EpException("Unable to retrieve Org. Profile.");//TODO 
			}
			if(ppPlan.getVotFundType()==null){
				ppPlan.setVotFundType(new PmParameter());
			}
			
			ppPlan.setOrgProfile(pmOrgProfile); 
			ppPlan.setStatus(ppPlan.getCurrentStatus()!=null?ppPlan.getCurrentStatus().getPmStatus():new PmStatus());
			this.getManagedBean().setSelectedPlan(ppPlan);
			
			generatePageMode();
			loadPlanActors(ppPlan.getOrgProfile(), ppPlan.getPpPlanActors()); // TODOTEMP_ORG_ID
			loadPlanRemark(ppPlan.getPpRemarks());

			//load plan det
			if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
				calculateEpGrandTotal();
			}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
				calculatePpGrandTotal();
			}

			
		}else{
			throw new EpException("Unable to retrieve plan."); //TODO
		}
		
		
		log.info("loadPlanDetails - End");
	}
	
	
	
	/**
	 * Retrieve plan by plan id
	 * @param planId
	 * @return PpPlan
	 */
	private PpPlan retrievePlanById(Long planId){
		log.info("retrievePlanById - Start");
		PpPlan ppPlan = null;
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setPlanId(planId);
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.retrievePlanDetById(reqDTO);
			if (resDTO.isSuccessful()) {
				ppPlan = resDTO.getPlan();
			}
		}catch(Exception e){
			throw new EpException(e.toString()); //TODO
		}finally{
			reqSO = null;
		}
		log.info("retrievePlanById - End");
		return ppPlan;
	}
	
	/**
	 * initial new plan object
	 */
	private void initialPlanDetails(){
		log.info("initialPlanDetails - Start");
		if(getFacesScopes().getUserSessionBean().getUserId()==null){
			throw new EpException("Unable to get UserId from session. Please login.");//TODO 
		}
		PmOrgProfile pmOrgProfile = PpCommonUtil.retrieveUserOrgListByUserId(getFacesScopes().getUserSessionBean().getUserId(), true);
		if(pmOrgProfile==null || pmOrgProfile.getOrgProfileId()==null){
			throw new EpException("Unable to retrieve Org. Profile.");//TODO 
		}
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
		PpPlan ppPlan = new PpPlan();
		ppPlan.setPlanType(new PmParameter());
		ppPlan.setVotFundType(new PmParameter());
		ppPlan.setOrgProfile(pmOrgProfile);
		ppPlan.setPpPlanActors(new ArrayList<PpPlanActor>());
		ppPlan.setStatus(new PmStatus());
		ppPlan.setPpRemarks(new ArrayList<PpRemark>());
		this.getManagedBean().setSelectedPlan(ppPlan);
		
		generatePageMode();
		loadPlanRemark(ppPlan.getPpRemarks());
		loadPlanActors(ppPlan.getOrgProfile() , null);
		
	

		log.info("initialPlanDetails - End");
	}
	
	/**
	 * Get status description by status id
	 */
	public String getStatusDescById(Long id){
		log.info("getStatusDescById");
		PmStatusDesc statusDesc = StatusHelper.getInstance().getStatusDescById(id);
		return statusDesc!=null ? statusDesc.getStatusName() : "";
	}
	
	/**
	 * generate actor source
	 * @param orgProfileId
	 */
	private void loadPlanActors(PmOrgProfile orgProfile, List <PpPlanActor> ppPlanActors){
		log.info("loadPlanActors - Start");
		List<PmUser> sourceVerifier = new ArrayList<PmUser>(); 
        List<PmUser> targetVerifier = new ArrayList<PmUser>();  
        this.getManagedBean().setVerifierModel(new DualListModel<PmUser>(sourceVerifier, targetVerifier));
        generateSourceVerifiers(orgProfile.getOrgProfileId(), RoleCode.PLAN_VERFICATION_OFFICER.value(), ppPlanActors);
		List<PmUser> sourceApprover = new ArrayList<PmUser>(); 
        List<PmUser> targetApprover = new ArrayList<PmUser>();  
        this.getManagedBean().setApproverModel(new DualListModel<PmUser>(sourceApprover, targetApprover));
        generateSourceApprovers(orgProfile.getParentOrgProfile().getOrgProfileId(), RoleCode.PLAN_APPROVER.value(), ppPlanActors); //getParentOrgProfile().TODO

		log.info("loadPlanActors - End");
	}
	
	/**
	 * Initial or load remarks
	 */
	public void loadPlanRemark(List<PpRemark> remarkList) {
		log.info("loadPlanRemark - Start");
		boolean newRemark = false;
		//if first remark is not new
		if (!remarkList.isEmpty()){
			if(!remarkList.get(0).isBrandNew()){
				newRemark = true;
			}
		}

		// if remarks is empty
		if (remarkList.isEmpty() || remarkList.size() == 0) {//|| !compareUserName(remarkList.get(0).getUserId())
			newRemark = true;
		}
		
		// filter by current view mode
		if(!this.getManagedBean().isModeEditable() 
				&& !this.getManagedBean().isModeReviewEditable()){
			newRemark = false;
		}
		
		if(newRemark){
			PpRemark remark = new PpRemark();
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			remark.setUserId(getFacesScopes().getUserSessionBean().getLoginId());
			remarkList.add(0, remark);
		} 
		log.info("loadPlanRemark - End");
		
	}

	
	/**
	 * Initial or load reject reasons
	 */
	public void loadRejectReasons(List<PpRejectReason> ppRejectReasons) {
		log.info("initialRejectReasons - Start");
//		confirmRejectReason();
		boolean newReason = false;
		//if first reason is not new
		if (!ppRejectReasons.isEmpty()){
			if(!ppRejectReasons.get(0).isBrandNew()){
				newReason = true;
			}
		}

		// if reason is empty
		if (ppRejectReasons.isEmpty() || ppRejectReasons.size() == 0) {//|| !compareUserName(remarkList.get(0).getUserId())
			newReason = true;
		}
		
		if(newReason){
			PpRejectReason reason = new PpRejectReason();
			reason.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			reason.setCreatedUser(getFacesScopes().getUserSessionBean());
			reason.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			reason.setChangedBy(reason.getCreatedBy());
			reason.setChangedDate(reason.getCreatedDate());
			PmRole roleCode = new PmRole();
			roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
			reason.setRoleCode(roleCode);
			ppRejectReasons.add(0, reason);
		
		} 
		
		this.getManagedBean().setSelectedRejectReasons(ppRejectReasons);
		
		log.info("initialRejectReasons - End");
		
	}
	
	public void confirmRejectReason(){
		log.info("confirmRejectReason");
		if(this.getManagedBean().getSelectedRejectReasons()!=null && !this.getManagedBean().getSelectedRejectReasons().isEmpty()){
			System.out.println(this.getManagedBean().getSelectedRejectReasons().get(0).getReasonDesc());
		}
	}
	
	
	/**
	 * Generate page mode based on status and role (from search plan)
	 */
	private void generatePageMode(){
		log.info("generatePageMode");
		// Current Workflow Status
		Long status =  this.getManagedBean().getSelectedPlan().getStatus().getStatusId();
		this.getManagedBean().setModeReview(false);
		this.getManagedBean().setModeReviewEditable(false);
		this.getManagedBean().setModeEditable(false);
		if(!isRoleBPK()){
	
			
			// NEW
			if(this.getManagedBean().getSelectedPlan().isBrandNew()){
				if(isRolePlanner()){
					this.getManagedBean().setModeEditable(true);
				}
			// PENDING REVISION
			}else if(ProcurementPlanConst.PPP_PENDING_REVISION.equals(status)
					|| ProcurementPlanConst.PPE_PENDING_REVISION.equals(status)){
				this.getManagedBean().setModeReview(true);
				if(isRolePlanner()){
					this.getManagedBean().setModeEditable(true);
				}
			// APPROVAL & VERIFICATION	
			}else if(ProcurementPlanConst.PPP_PENDING_VERIFICATION.equals(status) 
					|| ProcurementPlanConst.PPP_PENDING_APPROVAL.equals(status)
					|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(status)){
				this.getManagedBean().setModeEditable(false);
				this.getManagedBean().setModeReview(true);
				if(isRoleApprover() || isRoleVerifier()){
					this.getManagedBean().setModeReviewEditable(true);
				}
			// APPROVED
			}else if(ProcurementPlanConst.PPP_APPROVED.equals(status) 
					|| ProcurementPlanConst.PPE_APPROVED.equals(status) ){
				this.getManagedBean().setModeReview(false);
				this.getManagedBean().setModeReviewEditable(false);
				if(isRolePlanner()){
					this.getManagedBean().setModeEditable(true);
				}
			}
		}else{
			// PENDING REVISION, APPROVAL & VERIFICATION (show reasons)
			if(ProcurementPlanConst.PPP_PENDING_REVISION.equals(status)
					|| ProcurementPlanConst.PPE_PENDING_REVISION.equals(status)
					|| ProcurementPlanConst.PPP_PENDING_VERIFICATION.equals(status) 
					|| ProcurementPlanConst.PPP_PENDING_APPROVAL.equals(status)
					|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(status)){
				this.getManagedBean().setModeEditable(false);
				this.getManagedBean().setModeReview(true);
				this.getManagedBean().setModeReviewEditable(false);
			}
				
		}
		log.info("MODE REVIEW: " + this.getManagedBean().isModeReview());
		log.info("MODE REVIEW EDITABLE: " + this.getManagedBean().isModeReviewEditable());
		log.info("MODE EDITABLE: " + this.getManagedBean().isModeEditable());
	}
	
//	/**
//	 * Generate page mode based on status and role (from search plan)
//	 */
//	private void generatePageModeTest(){
//		log.info("generatePageModeTest");
//		// Current Workflow Status
//		Long status = this.getManagedBean().getCurrentStatus();// TODO this.getManagedBean().getSelectedPlan().getStatus().getStatusId();
//		this.getManagedBean().setModeReview(false);
//		this.getManagedBean().setModeReviewEditable(false);
//		this.getManagedBean().setModeEditable(false);
//		if(!isRoleBPK()){
//			/*
//			// Only approved plan is editable from search plan TODO
//			if(ProcurementPlanConst.PPP_APPROVED.equals(status) 
//					|| ProcurementPlanConst.PPE_APPROVED.equals(status) ){
//				this.getManagedBean().setModeReview(false);
//				this.getManagedBean().setModeReviewEditable(false);
//				if(isRolePlanner()){
//					this.getManagedBean().setModeEditable(true);
//				}
//			}*/
//			
//			// NEW & DRAFT & REVISION
//			if(ProcurementPlanConst.PPP_PENDING_SUBMISSION.equals(status) 
//					|| ProcurementPlanConst.PPE_PENDING_SUBMISSION.equals(status)
//					|| ProcurementPlanConst.PPP_PENDING_REVISION.equals(status)
//					|| ProcurementPlanConst.PPE_PENDING_REVISION.equals(status)){
//				this.getManagedBean().setModeReview(false);
//				this.getManagedBean().setModeReviewEditable(false);
//				if(isRolePlanner()){
//					this.getManagedBean().setModeEditable(true);
//				}else{
//					this.getManagedBean().setModeEditable(false);
//				}
//			// APPROVAL & VERIFICATION	
//			}else if(ProcurementPlanConst.PPP_PENDING_VERIFICATION.equals(status) 
//					|| ProcurementPlanConst.PPP_PENDING_APPROVAL.equals(status)
//					|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(status)){
//				this.getManagedBean().setModeEditable(false);
//				this.getManagedBean().setModeReview(true);
//				if(isRoleApprover() || isRoleVerifier()){
//					this.getManagedBean().setModeReviewEditable(true);
//				}else{
//					this.getManagedBean().setModeReviewEditable(false);
//				}
//			// APPROVED
//			}else if(ProcurementPlanConst.PPP_APPROVED.equals(status) 
//					|| ProcurementPlanConst.PPE_APPROVED.equals(status) ){
//				this.getManagedBean().setModeReview(false);
//				this.getManagedBean().setModeReviewEditable(false);
//				if(isRolePlanner()){
//					this.getManagedBean().setModeEditable(true);
//				}
//			}
//		}else{
//			if(ProcurementPlanConst.PPP_PENDING_VERIFICATION.equals(status) 
//					|| ProcurementPlanConst.PPP_PENDING_APPROVAL.equals(status)
//					|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(status)){
//				this.getManagedBean().setModeEditable(false);
//				this.getManagedBean().setModeReview(true);
//				this.getManagedBean().setModeReviewEditable(false);
//			}
//				
//		}
//		log.info("MODE REVIEW: " + this.getManagedBean().isModeReview());
//		log.info("MODE REVIEW EDITABLE: " + this.getManagedBean().isModeReviewEditable());
//		log.info("MODE EDITABLE: " + this.getManagedBean().isModeEditable());
//	}
//	
	/**
	 * get role applicable to current status
	 * @param plan
	 * @return String
	 */
	private String getOneRoleByStatus(PpPlan plan){
		log.info("getOneRoleByStatus");
		String roleCode = "";
		Long statusId = plan.getStatus().getStatusId();
		if(statusId!=null){
			if(ProcurementPlanConst.PPP_PENDING_SUBMISSION.equals(statusId)){
				roleCode = RoleCode.PLAN_PROCU_OFFICER.value();
			}else if(ProcurementPlanConst.PPE_PENDING_SUBMISSION.equals(statusId)){
				roleCode = RoleCode.PLAN_EXPEN_OFFICER.value();
			}else if(ProcurementPlanConst.PPP_PENDING_VERIFICATION.equals(statusId)){
				roleCode = RoleCode.PLAN_VERFICATION_OFFICER.value();
			}else if(ProcurementPlanConst.PPP_PENDING_APPROVAL.equals(statusId)
					|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(statusId)){
				roleCode = RoleCode.PLAN_APPROVER.value();
			}else if(ProcurementPlanConst.PPP_APPROVED.equals(statusId) 
						|| ProcurementPlanConst.PPE_APPROVED.equals(statusId)){
				roleCode = RoleCode.PLAN_PROCU_OFFICER.value();
			}
		}
		return roleCode;
	}
	/**
	 * Login user has approver role
	 * @return boolean
	 */
	public boolean isRoleApprover(){
		return getFacesScopes().hasRoleUser(RoleCode.PLAN_APPROVER.value());
	}
	
	/**
	 * Login user has verifier role
	 * @return boolean
	 */
	public boolean isRoleVerifier(){
		return getFacesScopes().hasRoleUser(RoleCode.PLAN_VERFICATION_OFFICER.value());
	}
	
	/**
	 * Login user has BPK role
	 * @return boolean
	 */
	public boolean isRoleBPK(){
		return getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value());
	}
	
	/**
	 * Login user has planner role
	 * @return boolean
	 */
	public boolean isRolePlanner(){
		boolean isPlanner = false;
		if(getFacesScopes().hasRoleUser(RoleCode.PLAN_PROCU_OFFICER.value()) 
				||getFacesScopes().hasRoleUser(RoleCode.PLAN_EXPEN_OFFICER.value())){
			isPlanner = true;
		}
		return isPlanner;
		
	}
	
	/* LOCAL*/
//	/**
//	 * Login user has approver role
//	 * @return boolean
//	 */
//	public boolean isRoleApprover(){
//		return RoleCode.PLAN_APPROVER.value().equals(this.getManagedBean().getCurrentRole());
//	}
//	
//	/**
//	 * Login user has verifier role
//	 * @return boolean
//	 */
//	public boolean isRoleVerifier(){
//		return RoleCode.PLAN_VERFICATION_OFFICER.value().equals(this.getManagedBean().getCurrentRole());
//	}
//	
//	/**
//	 * Login user has BPK role
//	 * @return boolean
//	 */
//	public boolean isRoleBPK(){
//		return RoleCode.BPK_ADMIN.value().equals(this.getManagedBean().getCurrentRole());
//	}
//	
//	/**
//	 * Login user has planner role
//	 * @return boolean
//	 */
//	public boolean isRolePlanner(){
//		return RoleCode.PLAN_PROCU_OFFICER.value().equals(this.getManagedBean().getCurrentRole());
//		
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
					generateEpPlan(this.getManagedBean().getSelectedPlan());
				}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(planType.getParameterCode())){
					generatePpPlan(this.getManagedBean().getSelectedPlan());
				}
			}
		}else{
			this.getManagedBean().getSelectedPlan().setPlanType(new PmParameter());
		}
	}
	
	/**
	 * generate procurement items
	 */
	private void generatePpPlan(PpPlan plan){
		log.info("generatePpPlan");
		if(plan!=null && !plan.isBrandNew()){
			
		}else{
			this.getManagedBean().getSelectedPlan().setPpProcurementItems(new ArrayList<PpProcurementItem>());
			this.getManagedBean().getSelectedPlan().setPpExpenditureItems(null);
			this.getManagedBean().getSelectedPlan().setStatus(PpCommonUtil.getDefaultStatus(ParameterConst.DOCTYPE_PLAN_PROCUREMENT));
		}
		
	}
	
	/**
	 * generate expenditure items
	 */
	private void generateEpPlan(PpPlan plan){
		log.info("generateEpPlan");
		if(plan!=null && !plan.isBrandNew()){
			
		}else{
			this.getManagedBean().getSelectedPlan().setPpExpenditureItems(new ArrayList<PpExpenditureItem>());
			this.getManagedBean().getSelectedPlan().setPpProcurementItems(null);
			this.getManagedBean().getSelectedPlan().setStatus(PpCommonUtil.getDefaultStatus(ParameterConst.DOCTYPE_PLAN_EXPENDITURE));
		}
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
	 * delete ep item to list
	 */
	public void deleteEpItemDet(){
		log.info("deleteEpItemDet");
		if(this.getManagedBean().getDeleteEpItems()!=null && !this.getManagedBean().getDeleteEpItems().isEmpty()){
			if(this.getManagedBean().getSelectedPlan().getPpExpenditureItems()==null){
				this.getManagedBean().getSelectedPlan().setPpExpenditureItems(new ArrayList<PpExpenditureItem>());
			}
			for(PpExpenditureItem expenditureItem: this.getManagedBean().getDeleteEpItems()){
//				if(expenditureItem.isBrandNew()){
					this.getManagedBean().getSelectedPlan().getPpExpenditureItems().remove(expenditureItem);
//				}
			}
			this.getManagedBean().getDeleteEpItems().clear();
			calculateEpGrandTotal();
		}else{
			throw new EpException("At least one expenditure item must be selected."); //TODO
		}
	}
	
	/**
	 * add calculate non allocation budget 
	 */
	public void calculateNonAllocationBudget(PpExpenditureItem item){
		log.info("calculateNonAllocationBudget");
		item.setNonEpAmt(PpCommonUtil.substractBigDecimalNullable(item.getBudgetAmt(), item.getPlanAmt()));
		calculateEpGrandTotal();
		
	}
	
	/**
	 * calculate expenditure items grand total
	 */
	private void calculateEpGrandTotal(){
		log.info("calculateEpGrandTotal");
		BigDecimal epTotalBudget = new BigDecimal(0);
		BigDecimal epTotalNonEpAlloc = new BigDecimal(0);
		BigDecimal epTotalPlan = new BigDecimal(0);
		
		for(PpExpenditureItem epItem: this.getManagedBean().getSelectedPlan().getPpExpenditureItems()){
			epTotalBudget = PpCommonUtil.addBigDecimalNullable(epTotalBudget, epItem.getBudgetAmt());
			epTotalNonEpAlloc = PpCommonUtil.addBigDecimalNullable(epTotalNonEpAlloc, epItem.getNonEpAmt());
			epTotalPlan = PpCommonUtil.addBigDecimalNullable(epTotalPlan, epItem.getPlanAmt());
		}
		

		this.getManagedBean().setEpTotalBudget(epTotalBudget);
		this.getManagedBean().setEpTotalNonEpAlloc(epTotalNonEpAlloc);
		this.getManagedBean().setEpTotalPlan(epTotalPlan);
	}
	

	/**
	 * calculate procurement items grand total
	 */
	public void calculatePpGrandTotal(){
		log.info("calculatePpGrandTotal");
		BigDecimal ppTotalEst = new BigDecimal(0);
		
		for(PpProcurementItem ppItem: this.getManagedBean().getSelectedPlan().getPpProcurementItems()){
			ppTotalEst = PpCommonUtil.addBigDecimalNullable(ppTotalEst, ppItem.getEstimatedAmt());
		}
		this.getManagedBean().setPpTotalEst(ppTotalEst);
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
		for(PpProcurementItem procurementItem: this.getManagedBean().getSelectedPlan().getPpProcurementItems()){
			System.out.println(procurementItem.getProcurementTitle());

		}
	}
	
	/**
	 * delete pp item to list
	 */
	public void deletePpItemDet(){
		log.info("deletePpItemDet");
		if(this.getManagedBean().getDeletePpItems()!=null && !this.getManagedBean().getDeletePpItems().isEmpty()){
			if(this.getManagedBean().getSelectedPlan().getPpProcurementItems()==null){
				this.getManagedBean().getSelectedPlan().setPpProcurementItems(new ArrayList<PpProcurementItem>());
			}
			for(PpProcurementItem procurementItem: this.getManagedBean().getDeletePpItems()){
				System.out.println(procurementItem.getProcurementTitle());
//				if(procurementItem.isBrandNew()){
					this.getManagedBean().getSelectedPlan().getPpProcurementItems().remove(procurementItem);
//				}
			}
			this.getManagedBean().getDeletePpItems().clear();
			calculatePpGrandTotal();
		}else{
			throw new EpException("At least one procurement item must be selected."); //TODO
		}
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
	 *  get Procurement Cat options 
	 * @return List<PmProcurementCatType>
	 */
	public List<PmProcurementCatType> getProcurementCategoryOptions() {
		log.info("getProcurementCategoryOptions");
		String languageCode = getFacesScopes().getUserLocale().getLanguage();
		return PpCommonUtil.retrieveProcurementCatTypeList(languageCode);
	}
	
	/**
	 * get Procurement Type options by cat id
	 * @return List<PmProcurementCatType>
	 */
	public List<PmProcurementCatType> getProcurementTypeOptions(Long procurementCatId) {
		log.info("getProcurementTypeOptions");
		List<PmProcurementCatType> procurementCatTypeList = new ArrayList<PmProcurementCatType>();
		if(procurementCatId!=null && procurementCatId>0L){
			String languageCode = getFacesScopes().getUserLocale().getLanguage();
			procurementCatTypeList = PpCommonUtil.retrieveProcurementTypeListByCatId(languageCode, procurementCatId);
		}
		return procurementCatTypeList;
	}
	

//	public List<> getProcurementTypeOptions() {
//		log.info("getProcurementTypeOptions");
//		retrieveProcurementCatTypeList
//		return getDropdownValues(ParameterTypeConst.SC_PROCUREMENT_MODE); //TODO
//	}
	
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
			//default
			items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
			
			
			//get from DB
			Long userId = getFacesScopes().getUserSessionBean().getUserId();
			if(userId!=null){
				log.info("getExistingPlanYearOptions" + userId);
				log.info("getExistingPlanYearOptions" + this.getCurrentActiveOrg().getOrgProfileId());
				reqDTO = new ProcurementPlanReqDTO();
				resDTO = new ProcurementPlanResDTO();
				reqDTO.setOrgProfileId(this.getCurrentActiveOrg().getOrgProfileId());
				reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
				resDTO = reqSO.retrieveExistingPlanYears(reqDTO);
				if (resDTO.isSuccessful() && resDTO.getPlanYears()!=null) {
					int i = 1;
					items = new SelectItem[resDTO.getPlanYears().size()+1];
					items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
					for(Long year:resDTO.getPlanYears()){
						items[i] = new SelectItem(year, year.toString());
						i++;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new EpException(e.toString()); //TODO
		}finally{
			reqSO = null;
		}
		return items;
	}
	
	
	/**
	 * to make sure the current active org is not null
	 * @return
	 */
	private PmOrgProfile getCurrentActiveOrg(){
		log.info("getCurrentActiveOrg");
		if(this.getManagedBean().getCurrentActiveOrg()==null){
			PmOrgProfile pmOrgProfile = 
					PpCommonUtil.retrieveUserOrgListByUserId(getFacesScopes().getUserSessionBean().getUserId(), true);
			if(pmOrgProfile==null || pmOrgProfile.getOrgProfileId()==null){
				throw new EpException("Unable to retrieve Org. Profile.");//TODO 
			}
			this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
		}
		return this.getManagedBean().getCurrentActiveOrg();
	}
	/**
	 * get Plan Year options
	 * @return SelectItem[]
	 */
	public SelectItem[] getPlanYearOptions() {
		log.info("getPlanYearOptions");
		return PpCommonUtil.getPlanYearOptions();
	}
	

	/**
	 * retrieve verifier source and target
	 * @param orgProfileId
	 * @param roleCode
	 * @param ppPlanActors
	 */
	private void generateSourceVerifiers(Long orgProfileId, String roleCode, List<PpPlanActor>ppPlanActors){
		log.info("generateSourceVerifiers");	
		if(ppPlanActors!=null){
		   List<PmUser> target = new ArrayList<PmUser>();  
			for(PpPlanActor ppPlanActor : ppPlanActors){
				if(DbValueConst.RECORD_STATUS_ACTIVE == ppPlanActor.getRecordStatusFlg()){
					if(roleCode.equals(ppPlanActor.getRoleCode().getRoleCode())){
						target.add(ppPlanActor.getPmUser());
					}
				}
			}
			this.getManagedBean().getVerifierModel().setTarget(target);
		}
		List<PmUser> sourceList = PpCommonUtil.retrieveUserListByRole(orgProfileId, roleCode, this.getManagedBean().getVerifierModel().getTarget());
		this.getManagedBean().getVerifierModel().setSource(sourceList);
	
	}
	
	/**
	 * retrieve approver source and target
	 * @param orgProfileId
	 * @param roleCode
	 * @param ppPlanActors
	 */
	private void generateSourceApprovers(Long orgProfileId, String roleCode, List<PpPlanActor>ppPlanActors){
		log.info("generateSourceApprovers");
		if(ppPlanActors!=null){
		   List<PmUser> target = new ArrayList<PmUser>();  
			for(PpPlanActor ppPlanActor : ppPlanActors){
				if(DbValueConst.RECORD_STATUS_ACTIVE == ppPlanActor.getRecordStatusFlg()){
					if(roleCode.equals(ppPlanActor.getRoleCode().getRoleCode())){ 
						target.add(ppPlanActor.getPmUser());
					}
				}
			}
			this.getManagedBean().getApproverModel().setTarget(target);
		}
		List<PmUser> sourceList = PpCommonUtil.retrieveUserListByRole(orgProfileId, roleCode, this.getManagedBean().getApproverModel().getTarget());
		this.getManagedBean().getApproverModel().setSource(sourceList);
	}
	
	
	/**
	 * Update selected plan actors
	 * @param ppPlan
	 */
	private void updatePlanActors(PpPlan ppPlan){
		log.info("updatePlanActors");
		if(!ppPlan.isBrandNew() && ppPlan.getPpPlanActors()!=null && !ppPlan.getPpPlanActors().isEmpty()){
			log.info("syncPlanActors");
			//Existing Plan Actors
			List <Long> existingVerifiers = new ArrayList<>();
			List <Long> existingApprovers = new ArrayList<>();
			for(PpPlanActor actor:ppPlan.getPpPlanActors()){
				if(RoleCode.PLAN_VERFICATION_OFFICER.value().equals(actor.getRoleCode().getRoleCode())){
					System.out.println("Existing VERIFIER: " + actor.getPmUser().getUserId());
					existingVerifiers.add(actor.getPmUser().getUserId());
				}
				if(RoleCode.PLAN_APPROVER.value().equals(actor.getRoleCode().getRoleCode())){
					System.out.println("Existing Approver: " + actor.getPmUser().getUserId());
					existingApprovers.add(actor.getPmUser().getUserId());
				}
			}
			
			//New Plan Actors
			List <Long> newVerifiers = new ArrayList<>();
			List <Long> newApprovers = new ArrayList<>();
			
			//Verifier
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
				if(this.getManagedBean().getVerifierModel()!=null 
						&& this.getManagedBean().getVerifierModel().getTarget()!=null 
						&& !this.getManagedBean().getVerifierModel().getTarget().isEmpty()){
					//Add Selected Actor
					for(PmUser newUser:this.getManagedBean().getVerifierModel().getTarget()){
						newVerifiers.add(newUser.getUserId());
						if(!existingVerifiers.contains(newUser.getUserId())){
							//Add
							PpPlanActor planActor = null;
							PmRole pmRole = new PmRole();
							pmRole.setRoleCode(RoleCode.PLAN_VERFICATION_OFFICER.value());
							int seq = 1;
							for(PmUser user:this.getManagedBean().getVerifierModel().getTarget()){
								planActor = new PpPlanActor();
								planActor.setUserSeq(seq);
								planActor.setPmUser(user);
								planActor.setRoleCode(pmRole);
								ppPlan.addPpPlanActors(planActor);
								seq++;
							}
						}
					}
					//Remove Unselected Actor
					for(PpPlanActor actor:ppPlan.getPpPlanActors()){
						if(RoleCode.PLAN_VERFICATION_OFFICER.value().equals(actor.getRoleCode().getRoleCode())){
							if(!newVerifiers.contains(actor.getPmUser().getUserId())){
								//Remove
								actor.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
							}
						}
					}
				}
			}
			
			//Approver
			if(this.getManagedBean().getApproverModel()!=null 
					&& this.getManagedBean().getApproverModel().getTarget()!=null
					&& !this.getManagedBean().getApproverModel().getTarget().isEmpty()){
				//Add Selected Actor
				for(PmUser newUser:this.getManagedBean().getApproverModel().getTarget()){
					newApprovers.add(newUser.getUserId());
					if(!existingApprovers.contains(newUser.getUserId())){
						System.out.println("add new approver");
						//Add
						PpPlanActor planActor = null;
						PmRole pmRole = new PmRole();
						pmRole.setRoleCode(RoleCode.PLAN_APPROVER.value());
						int seq = 1;
						for(PmUser user:this.getManagedBean().getApproverModel().getTarget()){
							planActor = new PpPlanActor();
							planActor.setUserSeq(seq);
							planActor.setPmUser(user);
							planActor.setRoleCode(pmRole);
							ppPlan.addPpPlanActors(planActor);
							seq++;
						}
					}
				}
				//Remove Unselected Actor
				for(PpPlanActor actor:ppPlan.getPpPlanActors()){
					if(RoleCode.PLAN_APPROVER.value().equals(actor.getRoleCode().getRoleCode())){
						if(!newApprovers.contains(actor.getPmUser().getUserId())){
							//Remove
							System.out.println("remove existing approver");
							actor.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						}
					}
				}
			}
		}else{
			log.info("addPlanActors");
			if(ppPlan.getPpPlanActors()==null){
				ppPlan.setPpPlanActors(new ArrayList<PpPlanActor>());
			}
			//Verifier
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
				if(this.getManagedBean().getVerifierModel()!=null 
						&& this.getManagedBean().getVerifierModel().getTarget()!=null 
						&& !this.getManagedBean().getVerifierModel().getTarget().isEmpty()){
					log.info("addVerifier");
					//Add Selected Actor
					PpPlanActor planActor = null;
					PmRole pmRole = new PmRole();
					pmRole.setRoleCode(RoleCode.PLAN_VERFICATION_OFFICER.value());
					int seq = 1;
					for(PmUser user:this.getManagedBean().getVerifierModel().getTarget()){
						planActor = new PpPlanActor();
						planActor.setUserSeq(seq);
						planActor.setPmUser(user);
						planActor.setRoleCode(pmRole);
						ppPlan.addPpPlanActors(planActor);
						seq++;
					}
				}
			}
			
			//Approver
			if(this.getManagedBean().getApproverModel()!=null 
					&& this.getManagedBean().getApproverModel().getTarget()!=null
					&& !this.getManagedBean().getApproverModel().getTarget().isEmpty()){
				log.info("addApprover");
				//Add Selected Actor
				PpPlanActor planActor = null;
				PmRole pmRole = new PmRole();
				pmRole.setRoleCode(RoleCode.PLAN_APPROVER.value());
				int seq = 1;
				for(PmUser user:this.getManagedBean().getApproverModel().getTarget()){
					planActor = new PpPlanActor();
					planActor.setUserSeq(seq);
					planActor.setPmUser(user);
					planActor.setRoleCode(pmRole);
					ppPlan.addPpPlanActors(planActor);
					seq++;
				}
			}
		}
		
	}
	
	private List <PpRejectReason> getAllRejectionReasons(PpPlan ppPlan){
		log.info("getAllRejectionReasons");
		List <PpRejectReason> rejectReasons = null;
		if(ppPlan.getStatus()!=null 
				&& (ProcurementPlanConst.PPP_PENDING_VERIFICATION.equals(ppPlan.getStatus().getStatusId())
						|| ProcurementPlanConst.PPP_PENDING_APPROVAL.equals(ppPlan.getStatus().getStatusId())
						|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(ppPlan.getStatus().getStatusId()))){
			
			rejectReasons = new ArrayList<PpRejectReason>();
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
				int seq = 0;
				for(PpProcurementItem ppProcurementItem:ppPlan.getPpProcurementItems()){
					if(ppProcurementItem.getPpRejectReasons()!=null && !ppProcurementItem.getPpRejectReasons().isEmpty()){
						PpRejectReason rejectReason = ppProcurementItem.getPpRejectReasons().get(0);
						if(rejectReason!=null && rejectReason.isBrandNew() && !StringUtils.isBlank(rejectReason.getReasonDesc())){
							rejectReason.setItemSeq(seq);
							rejectReasons.add(rejectReason);
						}
					}
					seq++;
				}
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
				int seq = 0;
				for(PpExpenditureItem ppExpenditureItem:ppPlan.getPpExpenditureItems()){
					if(ppExpenditureItem.getPpRejectReasons()!=null && !ppExpenditureItem.getPpRejectReasons().isEmpty()){
						PpRejectReason rejectReason = ppExpenditureItem.getPpRejectReasons().get(0);
						if(rejectReason!=null && rejectReason.isBrandNew() && !StringUtils.isBlank(rejectReason.getReasonDesc())){
							rejectReason.setItemSeq(seq);
							rejectReasons.add(rejectReason);
						}
					}
					seq++;
				}
			}
		}
		return rejectReasons;
	}
	

	/**
	 * Method to update the plan
	 */
	public String savePlan(String action) throws Exception{
		log.info("savePlan");
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		PpPlan ppPlan = null; 
		List <PpRejectReason> rejectReasons = null;
		String docType = "";
		try{
			ppPlan = this.getManagedBean().getSelectedPlan();
			
			//Validation
			
			//Get All Values
			updatePlanActors(ppPlan);
			if(ppPlan.getPpRemarks()!=null){
				PpRemark ppRemark= ppPlan.getPpRemarks().get(0);
				if(ppRemark.isBrandNew() && !StringUtils.isBlank(ppRemark.getRemark())){
					if(ppRemark.getRoleCode() ==null || StringUtils.isBlank(ppRemark.getRoleCode().getRoleCode())){
						PmRole roleCode = new PmRole();
						roleCode.setRoleCode(getOneRoleByStatus(ppPlan));
						ppRemark.setRoleCode(roleCode);
						ppRemark.setPpPlan(ppPlan);
					}
				}
			}
			
			if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)){
				rejectReasons = getAllRejectionReasons(ppPlan);
				if(rejectReasons!=null && !rejectReasons.isEmpty()){
					reqDTO.setRejectReasonList(rejectReasons);
				}
			}
			
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setPpPlan(ppPlan);
			reqDTO.setOutcome(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken()); 
			if(!ppPlan.isBrandNew()){
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
			}	
			reqDTO.setDocTitle(PpCommonUtil.getDocTitle(ppPlan.getPlanType().getParameterCode()));
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.savePlan(reqDTO);
			docType = 
					ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode()) 
					? ParameterConst.DOCTYPE_PLAN_PROCUREMENT
					: ParameterConst.DOCTYPE_PLAN_EXPENDITURE;
					
					
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully"); //TODO
				
				String messageId = MessageConst.INFO_700005;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700007;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_700009;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType), resDTO.getPlan().getPlanNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				
			
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new EpException(e.toString()); //TODO
		}finally{
			reqSO = null;
		}
		return null;
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
			bean.setSearchField("orgProfile.parentOrgProfile.orgProfileId");
			bean.setSearchValue(planSearchBean.getMinistry().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}else{
			log.info("default org filter");
			//filter plan by org profile
			if(!isRoleBPK()){
				if(isRoleApprover()){
					log.info("isRoleApprover");
					SearchFilterBean profile = new SearchFilterBean();
					profile.setSearchField("orgProfile.parentOrgProfile.orgProfileId");
					profile.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
					profile.setSearchValue(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()); // user login active org
					searchBean.getDataFilterBeans().add(profile);
				}else if(isRoleVerifier() || isRolePlanner()){
					log.info("isRoleVerifier or isRolePlanner");
					SearchFilterBean profile = new SearchFilterBean();
					profile.setSearchField("orgProfile.orgProfileId");
					profile.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
					profile.setSearchValue(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()); // user login active org
					searchBean.getDataFilterBeans().add(profile);
				}
			}
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
		ppPlanDataModel = new PpPlanDataModel();
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
