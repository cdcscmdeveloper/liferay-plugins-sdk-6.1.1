package my.ep.web.backingbean.pp.procurementplan;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
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
import my.ep.persistence.entity.pp.procurementplan.PpWorkflowStatus;
import my.ep.type.aut.SearchCondition;
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
	public static Map<Long, Object> votTypeMapper;
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.info(message);
		}
		log.info(message);
	}
	
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	

	/**
	 * Pre-loading from Search Plan
	 */
	public void preLoading(){
		log.info("preLoading");
		this.getManagedBean().setBackAllow(true);
		this.resetPageModeFlags();
		this.resetManagedBean();
		initialSearchPlan();
		this.resetPlanSearchBean();
		
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}
	
	/**
	 * Pre-loading from Create Plan
	 */
	public void preLoadingCreate(){
		log.info("preLoadingCreate");
		this.getManagedBean().setBackAllow(false);
		this.resetManagedBean();
		this.retrievePlanTypeMapper();
		this.retrieveVotTypeMapper();
		initialPlanDetails();
		loadDefaultButtons();
		
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAINTAIN);
	}



	/**
	 * Pre-loading from Task List
	 */
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

		//only for PLAN OFFICER
		if(isRolePlanner()){
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
		this.getManagedBean().setBackAllow(false);
		generatePageModeForProcess(res.getTaskName());
		
		
		// Procurement Plan
		PPPlanDataType planObj = (PPPlanDataType) obj;
		this.loadPlanDetails(planObj.getPlanId().longValue(), false);

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAINTAIN);
	}
	
	
	/**
	 * Redirect to Plan Search
	 * @return xhtml
	 */
	public String redirectToSearchPlan(){
		log.info("redirectToSearchPlan");
		try{
			initialSearchPlan();	
		}catch (Exception e){
			log.error(e);
		}
		return NAV_TO_SEARCH;
	
	}
	
	/**
	 * Redirect to plan details from search screen
	 * @param id
	 * @return xhtml
	 */
	public String redirectToMaintainPlan(Long id) throws Exception{
		log.info("redirectToMaintainPlan");
		this.resetManagedBean();
		this.retrievePlanTypeMapper();
		this.retrieveVotTypeMapper();
		if(id!=null && id>0){
			loadPlanDetails(id, true);
			loadDefaultButtons();
		}
//		else{
//			initialPlanDetails();
//			loadDefaultButtons();
//		}
	
		return NAV_TO_MAINTAIN;
	}
	
	
	
	
	
	/* PAGE MODE/VIEW CONTROL - BEGIN */
	
	/**
	 * Load and generate default buttons
	 */
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
	}
	
	/**
	 * Generate page mode based on status and role (from search plan)
	 * UI is populated based on the following flags
	 * 1. modeReview: Rejected Flag & Reasons are viewable
	 * 2. modeEditable: all fields are editable
	 * 3. modeReviewEditable: Rejected Flag & Reasons are editable
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
				
			// APPROVAL & VERIFICATION	
			}else if(ProcurementPlanConst.PPP_PENDING_VERIFICATION.equals(status) 
					|| ProcurementPlanConst.PPP_PENDING_APPROVAL.equals(status)
					|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(status)){
				this.getManagedBean().setModeEditable(false);
				this.getManagedBean().setModeReview(true);
			
			// APPROVED
			}else if(ProcurementPlanConst.PPP_APPROVED.equals(status) 
					|| ProcurementPlanConst.PPE_APPROVED.equals(status) ){
				this.getManagedBean().setModeReview(false);
				this.getManagedBean().setModeReviewEditable(false);
				if(isRolePlanner()){
					this.getManagedBean().getSelectedPlan().setUpdatable(true);
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
	
	/**
	 * Generate page mode based on process name
	 * UI is populated based on the following flags
	 * 1. modeReview: Rejected Flag & Reasons are viewable
	 * 2. modeEditable: all fields are editable
	 * 3. modeReviewEditable: Rejected Flag & Reasons are editable
	 * @param taskName
	 */
	private void generatePageModeForProcess(String taskName){
		log.info("generatePageModeForProcess");
		log.info("taskName: " + taskName);
		this.getManagedBean().setModeReview(false);
		this.getManagedBean().setModeReviewEditable(false);
		this.getManagedBean().setModeEditable(false);
	
		
		if(!StringUtils.isBlank(taskName)){
			// DRAFT & REVISION & UPDATE
			if(PPProcessNameConst.REVISE_PLAN.equalsIgnoreCase(taskName)
					|| PPProcessNameConst.SUBMIT_PLAN.equalsIgnoreCase(taskName)){
				if(PPProcessNameConst.REVISE_PLAN.equalsIgnoreCase(taskName)){
					this.getManagedBean().setModeReview(true);
				}
				this.getManagedBean().setModeReviewEditable(false);
				this.getManagedBean().setModeEditable(true);
				
			// APPROVAL & VERIFICATION	
			}else if(PPProcessNameConst.REVIEW_PLAN.equalsIgnoreCase(taskName)
					|| PPProcessNameConst.VERIFY_PLAN.equalsIgnoreCase(taskName)){
				this.getManagedBean().setModeEditable(false);
				this.getManagedBean().setModeReview(true);
				this.getManagedBean().setModeReviewEditable(true);
			}
		}
		log.info("MODE REVIEW: " + this.getManagedBean().isModeReview());
		log.info("MODE REVIEW EDITABLE: " + this.getManagedBean().isModeReviewEditable());
		log.info("MODE EDITABLE: " + this.getManagedBean().isModeEditable());
	}

	/* PAGE MODE - END */

	
	
	
	
	/**
	 * Prepare and initial search view (filtered by login user)
	 */
	private void initialSearchPlan(){
		log.info("initialSearchPlan");
	
		PmOrgProfile pmOrgProfile = PpCommonUtil.retrieveUserOrgListByUserId(getFacesScopes().getUserSessionBean().getUserId(), true);
		if(pmOrgProfile==null || pmOrgProfile.getOrgProfileId()==null){
			throw new EpException("Unable to retrieve Org. Profile.");//TODO 
		}
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
	}
	

	
	/**
	 * Reset all the critical page mode flags
	 */
	private void resetPageModeFlags(){
		log.info("resetPageModeFlags");
		this.getManagedBean().setModeReview(false);
		this.getManagedBean().setModeEditable(false);
		this.getManagedBean().setModeReviewEditable(false);
	}
	
	/**
	 * Reset managed bean values
	 */
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
		this.getManagedBean().setSelectedRemarks(null);
		this.getManagedBean().setCurrentActiveOrg(null);
		this.getManagedBean().setFilterOrgCondition(null);
	
	
	}
	

	/**
	 * Load plan details by id
	 * @param id
	 * @param fromList (from search screen or task list)
	 */
	private void loadPlanDetails(Long id, boolean fromList){
		log.info("loadPlanDetails - Start");
		PpPlan ppPlan = null;
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setPlanId(id);
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.retrievePlanDetById(reqDTO);
			if (resDTO.isSuccessful()) {
				ppPlan = resDTO.getPlan();
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
					if(ppPlan.getCurrentStatus()==null || (ppPlan.getCurrentStatus()!=null && ppPlan.getCurrentStatus().getPmStatus()==null)){
						ppPlan.setCurrentStatus(new PpWorkflowStatus());
						ppPlan.getCurrentStatus().setPmStatus(new PmStatus());
					}
					this.getManagedBean().setSelectedPlan(ppPlan);
					if(fromList){
						generatePageMode();
					}
					
					loadPlanActors(ppPlan.getOrgProfile(), ppPlan.getPpPlanActors()); 
					loadPlanRemark(resDTO.getRemarkList());

					//load plan det
					if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
						calculateEpGrandTotal();
					}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
						calculatePpGrandTotal();
					}
					
		
					// define item editable mode
					itemEditableMode(ppPlan);
					
					
				}else{
					throw new EpException("Unable to retrieve plan."); //TODO
				}
			}
			
		}catch(Exception e){
			log.error(e);
			throw new EpException(e.toString()); //TODO
		}finally{
			reqSO = null;
		}

		
		
		log.info("loadPlanDetails - End");
	}

	/**
	 * generate item editable mode
	 * @param ppPlan
	 */
	private void itemEditableMode(PpPlan ppPlan){
		log.info("itemEditableMode");
		if(ProcurementPlanConst.PPE_PENDING_REVISION.equals(ppPlan.getStatus().getStatusId())
				|| ProcurementPlanConst.PPP_PENDING_REVISION.equals(ppPlan.getStatus().getStatusId())){
			this.getManagedBean().getSelectedPlan().setRejected(true);
			if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
				for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
					if(DbValueConst.RECORD_STATUS_ACTIVE == epItem.getRecordStatusFlg() && epItem.isRejected()){
						epItem.setEditable(true);
						this.getManagedBean().getSelectedPlan().setRejected(false);
					}
				}
				if(this.getManagedBean().getSelectedPlan().isRejected()){
					for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
						if(DbValueConst.RECORD_STATUS_ACTIVE == epItem.getRecordStatusFlg()){
							epItem.setEditable(true);
						}
					}
				}
			}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
				for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
					if(DbValueConst.RECORD_STATUS_ACTIVE == ppItem.getRecordStatusFlg() && ppItem.isRejected()){
						ppItem.setEditable(true);
						this.getManagedBean().getSelectedPlan().setRejected(false);
					}
				}
				if(this.getManagedBean().getSelectedPlan().isRejected()){
					for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
						if(DbValueConst.RECORD_STATUS_ACTIVE == ppItem.getRecordStatusFlg()){
							ppItem.setEditable(true);
						}
					}
				}
			}
		}else{
			if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
				for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
					if(DbValueConst.RECORD_STATUS_ACTIVE == epItem.getRecordStatusFlg()){
						epItem.setEditable(true);
					}
				}
			}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
				for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
					if(DbValueConst.RECORD_STATUS_ACTIVE == ppItem.getRecordStatusFlg()){
						ppItem.setEditable(true);
					}
				}
			}
		}
	}
	
	/**
	 * Prepare and initial new plan
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
		ppPlan.setCurrentStatus(new PpWorkflowStatus());
		ppPlan.getCurrentStatus().setPmStatus(new PmStatus());
		this.getManagedBean().setSelectedPlan(ppPlan);
		
		generatePageMode();
		loadPlanRemark(new ArrayList<PpRemark>());
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
        if(this.getManagedBean().getSelectedPlan().getPlanType()!=null 
        		&& this.getManagedBean().getSelectedPlan().getPlanType().getParameterCode()!=null
        		&& ParameterConst.PLAN_TYPE_PROCUREMENT.equals(this.getManagedBean().getSelectedPlan().getPlanType().getParameterCode())){
        	generateSourceVerifiers(orgProfile.getOrgProfileId(), RoleCode.PLAN_VERFICATION_OFFICER.value(), ppPlanActors);
        }
		List<PmUser> sourceApprover = new ArrayList<PmUser>(); 
        List<PmUser> targetApprover = new ArrayList<PmUser>();  
        this.getManagedBean().setApproverModel(new DualListModel<PmUser>(sourceApprover, targetApprover));
        generateSourceApprovers(orgProfile.getParentOrgProfile().getOrgProfileId(), RoleCode.PLAN_APPROVER.value(), ppPlanActors); //getParentOrgProfile().TODO

		log.info("loadPlanActors - End");
	}

	/**
	 * Initial & load remarks
	 * @param remarkList
	 */
	public void loadPlanRemark(List<PpRemark> remarkList) {
		log.info("loadPlanRemark - Start");
		this.getManagedBean().setSelectedRemarks(remarkList);
		
		boolean newRemark = false;
		//if first remark is not new
		if (!this.getManagedBean().getSelectedRemarks().isEmpty()){
			if(!this.getManagedBean().getSelectedRemarks().get(0).isBrandNew()){
				newRemark = true;
			}
		}

		// if remarks is empty
		if (this.getManagedBean().getSelectedRemarks().isEmpty() || this.getManagedBean().getSelectedRemarks().size() == 0) {//|| !compareUserName(this.getManagedBean().getSelectedRemarks().get(0).getUserId())
			newRemark = true;
		}
		
		// filter by current view mode
		if(!this.getManagedBean().isModeEditable() 
				&& !this.getManagedBean().isModeReviewEditable()){
			newRemark = false;
		}
		
		if(newRemark){
			PpRemark remark = new PpRemark();
			remark.setRemarkType(ProcurementPlanConst.REMARK_TYPE_REMARK);
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			remark.setUserId(getFacesScopes().getUserSessionBean().getLoginId());
			if(!this.getManagedBean().getSelectedPlan().isBrandNew()){
				PmRole roleCode = new PmRole();
				roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
				remark.setRoleCode(roleCode);
			}
			this.getManagedBean().getSelectedRemarks().add(0, remark);
		} 
		log.info("loadPlanRemark - End");
		
	}
	
	public void retrieveEpItemRemark(PpExpenditureItem epItem){
		log.info("retrieveEpItemRemark");
		if(!epItem.isBrandNew()){
			if(epItem.getPpRemarks()==null || epItem.getPpRemarks().isEmpty()){
				epItem.setPpRemarks(PpCommonUtil.retrieveRemark(epItem.getExpenditureItemId(), ParameterConst.DOCTYPE_PLAN_EXPENDITURE));
			}
		}
		loadEpItemRemarks(epItem);
	}
	
	public void retrievePpItemRemark(PpProcurementItem ppItem){
		log.info("retrievePpItemRemark");
		if(!ppItem.isBrandNew()){
			if(ppItem.getPpRemarks()==null || ppItem.getPpRemarks().isEmpty()){
				ppItem.setPpRemarks(PpCommonUtil.retrieveRemark(ppItem.getProcurementItemId(), ParameterConst.DOCTYPE_PLAN_PROCUREMENT));
			}
		}
		if(ppItem.getPpRemarks()!=null){
			System.out.println("ppItem.getPpRemarks().size() " + ppItem.getPpRemarks().size());
		}
		loadPpItemRemarks(ppItem);
	}
	
	/**
	 * Initial or load item remarks
	 * @param ppItem
	 */
	public void loadPpItemRemarks(PpProcurementItem ppItem) {
		log.info("loadPpItemRemarks - Start");
		this.getManagedBean().setConfirmRemark(false);
		this.getManagedBean().setSelectedItemRemarkDesc("");
		boolean newRemark = false;
		
		
		if(ppItem.getPpRemarks()==null){
			System.out.println(1);
			ppItem.setPpRemarks(new ArrayList<PpRemark>());
		}
		/* enable for phase 2
		//if first remark is not new
		if (!ppItem.getPpRemarks().isEmpty()){
			if(!ppItem.getPpRemarks().get(0).isBrandNew()){
				newReason = true;
			}
		}*/

		// if reason is empty
		if (ppItem.getPpRemarks().isEmpty() || ppItem.getPpRemarks().size() == 0) {//|| !compareUserName(remarkList.get(0).getUserId())
			System.out.println(2);
			newRemark = true;
		}
		
		// filter by current view mode	
		if(!this.getManagedBean().isModeEditable() 
				&& !this.getManagedBean().getSelectedPlan().isUpdatable()){
			newRemark = false;
		}
	
		if(newRemark){
			log.info("new remark added");
			PpRemark remark = new PpRemark();
			remark.setRemarkType(ProcurementPlanConst.REMARK_TYPE_REMARK);
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			remark.setUserId(getFacesScopes().getUserSessionBean().getLoginId());
			PmRole roleCode = new PmRole();
			roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
			remark.setRoleCode(roleCode);
			ppItem.getPpRemarks().add(0, remark);
			System.out.println("ppRemarks " + ppItem.getPpRemarks().size());

		}
	
		this.getManagedBean().setSelectedItemRemarks(ppItem.getPpRemarks());
		this.getManagedBean().setSelectedItemRemarkDesc(ppItem.getPpRemarks().get(0).getRemark());
		
		
		log.info("loadPpItemRemarks - End");
		
	}
	/**
	 * Initial or load item remarks
	 * @param epItem
	 */
	public void loadEpItemRemarks(PpExpenditureItem epItem) {
		log.info("loadEpItemRemarks - Start");
		this.getManagedBean().setConfirmRemark(false);
		this.getManagedBean().setSelectedItemRemarkDesc("");
		boolean newRemark = false;
		
		
		if(epItem.getPpRemarks()==null){
			System.out.println(1);
			epItem.setPpRemarks(new ArrayList<PpRemark>());
		}
		/* enable for phase 2
		//if first remark is not new
		if (!ppItem.getPpRemarks().isEmpty()){
			if(!ppItem.getPpRemarks().get(0).isBrandNew()){
				newReason = true;
			}
		}*/

		// if reason is empty
		if (epItem.getPpRemarks().isEmpty() || epItem.getPpRemarks().size() == 0) {//|| !compareUserName(remarkList.get(0).getUserId())
			System.out.println(2);
			newRemark = true;
		}
		
		// filter by current view mode	
		if(!this.getManagedBean().isModeEditable() 
				&& !this.getManagedBean().getSelectedPlan().isUpdatable()){
			newRemark = false;
		}
	
		if(newRemark){
			log.info("new remark added");
			PpRemark remark = new PpRemark();
			remark.setRemarkType(ProcurementPlanConst.REMARK_TYPE_REMARK);
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			remark.setUserId(getFacesScopes().getUserSessionBean().getLoginId());
			PmRole roleCode = new PmRole();
			roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
			remark.setRoleCode(roleCode);
			epItem.getPpRemarks().add(0, remark);
			System.out.println("ppRemarks " + epItem.getPpRemarks().size());

		}
	
		this.getManagedBean().setSelectedItemRemarks(epItem.getPpRemarks());
		this.getManagedBean().setSelectedItemRemarkDesc(epItem.getPpRemarks().get(0).getRemark());
		
		
		log.info("loadEpItemRemarks - End");
		
	}
	
	/**
	 * Handle item remark close popup
	 */
	public void closeItemRemark(){
		log.info("closeItemRemark");
		if(!this.getManagedBean().isConfirmRemark()){
			if(this.getManagedBean().getSelectedItemRemarks()!=null && !this.getManagedBean().getSelectedItemRemarks().isEmpty()){
			
				this.getManagedBean().getSelectedItemRemarks().get(0).setRemark(this.getManagedBean().getSelectedItemRemarkDesc());
				this.getManagedBean().setSelectedItemRemarkDesc("");
			
			}
		}
	}
	
	public void retrieveJustification(PpExpenditureItem epItem){
		log.info("retrieveJustification");
		if(!epItem.isBrandNew()){
			if(epItem.getJustifications()==null || epItem.getJustifications().isEmpty()){
				epItem.setJustifications(PpCommonUtil.retrieveJustification(epItem.getExpenditureItemId(), ParameterConst.DOCTYPE_PLAN_EXPENDITURE));
			}
		}
		loadJustification(epItem);
	}
	
	
	/**
	 * Initial or load item remarks
	 * @param epItem
	 */
	public void loadJustification(PpExpenditureItem epItem) {
		log.info("loadJustification - Start");
		this.getManagedBean().setConfirmJustification(false);
		this.getManagedBean().setSelectedJustificationDesc("");
		boolean newRemark = false;
		
		
		if(epItem.getJustifications()==null){
			epItem.setJustifications(new ArrayList<PpRemark>());
		}
		/* enable for phase 2
		//if first remark is not new
		if (!epItem.getJustifications().isEmpty()){
			if(!epItem.getJustifications().get(0).isBrandNew()){
				newReason = true;
			}
		}*/

		// if reason is empty
		if (epItem.getJustifications().isEmpty() || epItem.getJustifications().size() == 0) {//|| !compareUserName(epItem.getJustifications().get(0).getUserId())
			newRemark = true;
		}
		
		// filter by current view mode	
		if(!this.getManagedBean().isModeEditable()){
			newRemark = false;
		}
	
		if(newRemark){
			log.info("new justification added");
			PpRemark remark = new PpRemark();
			remark.setRemarkType(ProcurementPlanConst.REMARK_TYPE_JUSTIFICATION);
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			remark.setUserId(getFacesScopes().getUserSessionBean().getLoginId());
			PmRole roleCode = new PmRole();
			roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
			remark.setRoleCode(roleCode);
			epItem.getJustifications().add(0, remark);

		}
	
		this.getManagedBean().setSelectedJustifications(epItem.getJustifications());
		this.getManagedBean().setSelectedJustificationDesc(epItem.getJustifications().get(0).getRemark());
		
		
		log.info("loadJustification - End");
		
	}
	
	/**
	 * Handle item remark close popup
	 */
	public void closeJustification(){
		log.info("closeJustification");
		if(!this.getManagedBean().isConfirmJustification()){
			if(this.getManagedBean().getSelectedJustifications()!=null && !this.getManagedBean().getSelectedJustifications().isEmpty()){
			
				this.getManagedBean().getSelectedJustifications().get(0).setRemark(this.getManagedBean().getSelectedJustificationDesc());
				this.getManagedBean().setSelectedJustificationDesc("");
			
			}
		}
	}
	
	public void retrieveEpRejectReasons(PpExpenditureItem epItem){
		log.info("retrieveEpRejectReasons");
		if(epItem.getPpRejectReasons()==null || epItem.getPpRejectReasons().isEmpty()){
			epItem.setPpRejectReasons(PpCommonUtil.retrieveRejectReason(epItem.getExpenditureItemId(), ParameterConst.DOCTYPE_PLAN_EXPENDITURE));
		}
		loadRejectReasons(epItem.getPpRejectReasons());
	}
	
	public void retrievePpRejectReasons(PpProcurementItem ppItem){
		log.info("retrievePpRejectReasons");
		if(ppItem.getPpRejectReasons()==null || ppItem.getPpRejectReasons().isEmpty()){
			ppItem.setPpRejectReasons(PpCommonUtil.retrieveRejectReason(ppItem.getProcurementItemId(), ParameterConst.DOCTYPE_PLAN_PROCUREMENT));
		}
		if(ppItem.getPpRejectReasons()!=null){
			System.out.println("ppItem.getPpRejectReasons().size() " + ppItem.getPpRejectReasons().size());
		}
		loadRejectReasons(ppItem.getPpRejectReasons());
	}
	
	
	/**
	 * Initial or load reject reasons
	 * @param ppRejectReasons
	 */
	public void loadRejectReasons(List<PpRejectReason> ppRejectReasons) {
		log.info("loadRejectReasons - Start");
		this.getManagedBean().setConfirmReason(false);
		this.getManagedBean().setSelectedReasonDesc("");
		boolean newReason = false;
		if(ppRejectReasons==null){
			ppRejectReasons = new ArrayList<PpRejectReason>();
		}
		/* enable for phase 2
		//if first reason is not new
		if (!ppRejectReasons.isEmpty()){
			if(!ppRejectReasons.get(0).isBrandNew()){
				newReason = true;
			}
		}*/

		// if reason is empty
		if (ppRejectReasons.isEmpty() || ppRejectReasons.size() == 0) {//|| !compareUserName(remarkList.get(0).getUserId())
			newReason = true;
		}
		
		if(newReason && this.getManagedBean().isModeReviewEditable()){
			log.info("new reason added");
			PpRejectReason reason = new PpRejectReason();
			reason.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			reason.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			reason.setChangedBy(reason.getCreatedBy());
			reason.setChangedDate(reason.getCreatedDate());
			reason.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			PmRole roleCode = new PmRole();
			roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
			reason.setRoleCode(roleCode);
			ppRejectReasons.add(0, reason);
		}
	
		this.getManagedBean().setSelectedRejectReasons(ppRejectReasons);
		this.getManagedBean().setSelectedReasonDesc(ppRejectReasons.get(0).getReasonDesc());
		
		
		log.info("loadRejectReasons - End");
		
	}
	
	/**
	 * Handle reject reason close popup
	 */
	public void closeRejectReason(){
		log.info("closeRejectReason");
		if(!this.getManagedBean().isConfirmReason()){
			if(this.getManagedBean().getSelectedRejectReasons()!=null && !this.getManagedBean().getSelectedRejectReasons().isEmpty()){
			
				this.getManagedBean().getSelectedRejectReasons().get(0).setReasonDesc(this.getManagedBean().getSelectedReasonDesc());
				this.getManagedBean().setSelectedReasonDesc("");
			
			}
		}
	}
	
	/**
	 * Handle reason confirm button for dialog
	 */
	public void confirmReasonCloseDialog(){
		log.info("confirmReasonCloseDialog");
		this.getManagedBean().setConfirmReason(true);
	}
	
	/**
	 * Handle item remark confirm button for dialog
	 */
	public void confirmRemarkCloseDialog(){
		log.info("confirmRemarkCloseDialog");
		this.getManagedBean().setConfirmRemark(true);
	}
	
	/**
	 * Handle justification confirm button for dialog
	 */
	public void confirmJustificationCloseDialog(){
		log.info("confirmJustificationCloseDialog");
		this.getManagedBean().setConfirmJustification(true);
	}
	
	/**
	 * retrieve user portrait, return default portrait if liferay return null
	 * @param loginId
	 * @return String
	 */
	public String userImagePathWithDefaultImage(String loginId){
		log.info("userImagePathWithDefaultImage");
		String imagePath = "";
	
		imagePath = userImagePath(loginId);	
		if(StringUtils.isBlank(imagePath)){
			imagePath = ProfileManagementConst.DEFAULT_PORTRAIT;
		}
		return imagePath;
	}


	/**
	 * Retrieve and set plan type into mapper
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
	 * Retrieve and set vot type into mapper
	 */
	private void retrieveVotTypeMapper(){
		log.info("retrieveVotTypeMapper");
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.VOT_TYPE, null);
		votTypeMapper = new HashMap<Long,Object>();
		for(PmParameterDesc pmDesc: parameterDescs){
			votTypeMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}
	

	/**
	 * Generate/change plan view based selected plan type
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
	 * Generate/change plan view based selected vot type
	 */
	public void changeVotType(){
		log.info("changeVotType");
		Long votTypeId = this.getManagedBean().getSelectedPlan().getVotFundType().getParameterId();
		if(votTypeId != null){
			PmParameter votType = (PmParameter)votTypeMapper.get(votTypeId);
			this.getManagedBean().getSelectedPlan().getVotFundType().setParameterCode(votType!=null? votType.getParameterCode() : null);
		}else{
			this.getManagedBean().getSelectedPlan().setVotFundType(new PmParameter());
		}
	}
	
	/**
	 * Generate procurement items view
	 * @param plan
	 */
	private void generatePpPlan(PpPlan plan){
		log.info("generatePpPlan");
		if(plan!=null && !plan.isBrandNew()){
			//plan type is not editable after submission
		}else{
			this.getManagedBean().getSelectedPlan().setPpProcurementItems(new ArrayList<PpProcurementItem>());
			this.getManagedBean().getSelectedPlan().setPpExpenditureItems(null);
			this.getManagedBean().getSelectedPlan().setStatus(PpCommonUtil.getDefaultStatus(ParameterConst.DOCTYPE_PLAN_PROCUREMENT));
			loadPlanActors(this.getManagedBean().getSelectedPlan().getOrgProfile(), null); //plan type is not editable after submission
		}
		
	}
	
	
	/**
	 * Generate expenditure items view
	 * @param plan
	 */
	private void generateEpPlan(PpPlan plan){
		log.info("generateEpPlan");
		if(plan!=null && !plan.isBrandNew()){
			//plan type is not editable after submission
		}else{
			this.getManagedBean().getSelectedPlan().setPpExpenditureItems(new ArrayList<PpExpenditureItem>());
			this.getManagedBean().getSelectedPlan().setPpProcurementItems(null);
			this.getManagedBean().getSelectedPlan().setStatus(PpCommonUtil.getDefaultStatus(ParameterConst.DOCTYPE_PLAN_EXPENDITURE));
		}
	}
	
	/**
	 * Add new item to expenditure list
	 */
	public void addEpItemDet(){
		log.info("addEpItemDet");
		if(this.getManagedBean().getSelectedPlan().getPpExpenditureItems()==null){
			this.getManagedBean().getSelectedPlan().setPpExpenditureItems(new ArrayList<PpExpenditureItem>());
		}
		
		if(!this.getManagedBean().getSelectedPlan().getPpExpenditureItems().isEmpty()){
			List <Long> soIds = new ArrayList<Long>();
//			int size = this.getManagedBean().getSelectedPlan().getPpExpenditureItems().size();
//			PpExpenditureItem epItem = this.getManagedBean().getSelectedPlan().getPpExpenditureItems().get(size-1);
			for(PpExpenditureItem epItem: this.getManagedBean().getSelectedPlan().getPpExpenditureItems()){
				if(epItem.getPmSo().getSoId()==null){
					throw new EpException("Object is mandatory."); //TODO
				}
				if(epItem.getBudgetAmt()==null || epItem.getBudgetAmt().intValue()<=0){
					throw new EpException("Total Approved Budget must be greater than 0."); //TODO
				}
				if(epItem.getPlanAmt()==null || epItem.getPlanAmt().intValue()<=0){
					throw new EpException("Budget to spend through eP must be greater than 0."); //TODO
				}
				if(epItem.getNonEpAmt()==null || epItem.getNonEpAmt().intValue()<0){
					throw new EpException("Non eP Allocation must not be lesser than 0."); //TODO
				}
				if(epItem.getNonEpAmt().intValue()>0){
					if(epItem.getJustifications()==null 
							|| epItem.getJustifications().get(0)==null 
							|| StringUtils.isBlank(epItem.getJustifications().get(0).getRemark())){
						throw new EpException("Justification for not using eP is mandatory."); //TODO
					}
				}
				soIds.add(epItem.getPmSo().getSoId());
			}
			if(!soIds.isEmpty()){
				Set<Long> soIdSet = new HashSet<Long>(soIds);
				if(soIdSet.size()!=soIds.size()){
					throw new EpException("Object must be unique."); //TODO
				}
			}
			
		}

		PpExpenditureItem epItem = new PpExpenditureItem();
		epItem.setPmSo(new PmSo());
		epItem.setEditable(true);
		this.getManagedBean().getSelectedPlan().addPpExpenditureItems(epItem);

	}
	
	/**
	 * Delete selected expenditure items
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
	 * Calculate non allocation budget & update grand total
	 */
	public void calculateNonAllocationBudget(PpExpenditureItem item){
		log.info("calculateNonAllocationBudget");
		item.setNonEpAmt(PpCommonUtil.substractBigDecimalNullable(item.getBudgetAmt(), item.getPlanAmt()));
		calculateEpGrandTotal();
		
	}
	
	/**
	 * Calculate expenditure items grand total
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
	 * Calculate procurement items grand total
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
	 * Add new item procurement item list
	 */
	public void addPpItemDet(){
		log.info("planPpItemDet");
		if(this.getManagedBean().getSelectedPlan().getPpProcurementItems()==null){
			this.getManagedBean().getSelectedPlan().setPpProcurementItems(new ArrayList<PpProcurementItem>());
		}
		if(!this.getManagedBean().getSelectedPlan().getPpProcurementItems().isEmpty()){
			int size = this.getManagedBean().getSelectedPlan().getPpProcurementItems().size();
			PpProcurementItem ppItem = this.getManagedBean().getSelectedPlan().getPpProcurementItems().get(size-1);
//			for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
				if(StringUtils.isBlank(ppItem.getProcurementTitle())){
					throw new EpException("Procurement Title is mandatory."); //TODO
				}
				if(ppItem.getProcurementCat().getParameterId()==null){
					throw new EpException("Procurement Category is mandatory."); //TODO
				}
				if(ppItem.getProcurementType().getParameterId()==null){
					throw new EpException("Procurement Method is mandatory."); //TODO
				}
				if(ppItem.getInvitationDate()==null){
					throw new EpException("Invitation Date is mandatory."); //TODO
				}
				if(ppItem.getEstimatedAmt()==null || ppItem.getEstimatedAmt().intValue()<=0){
					throw new EpException("Est. Amt. must be greater than 0."); //TODO
				}
//			}
		}
		PpProcurementItem ppItem = new PpProcurementItem();
		ppItem.setProcurementCat(new PmParameter());
		ppItem.setProcurementType(new PmParameter());
		ppItem.setEditable(true);
		this.getManagedBean().getSelectedPlan().addPpProcurementItems(ppItem);
		
	}
	
	/**
	 * Delete selected procurement items
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
	 * get Status options
	 * @return SelectItem[]
	 */
	public SelectItem[] getStatusOptions() {
		log.info("getStatusOptions");
		return PpCommonUtil.getStatusOptionsDefault();
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
	 * get available Plan Year options for plan details
	 * @return SelectItem[]
	 */
	public SelectItem[] getPlanYearOptions() {
		log.info("getPlanYearOptions");
		//TODO REMOVE RAIN
		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(this.getManagedBean().getSelectedPlan().getPlanType().getParameterCode())){
			return PpCommonUtil.getPlanYearOptions(getFacesScopes().getUserSessionBean().getUserId(), this.getCurrentActiveOrg().getOrgProfileId());
		}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(this.getManagedBean().getSelectedPlan().getPlanType().getParameterCode())){
			return PpCommonUtil.getEpPlanYearOptions(getFacesScopes().getUserSessionBean().getUserId(), this.getCurrentActiveOrg().getOrgProfileId());
		}else{
			SelectItem[] items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			return items;
		}
//		Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
//		Integer nextYear = currentYear + 1;
//		SelectItem[] items = new SelectItem[3];
//		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
//		items[1] = new SelectItem(currentYear.longValue(), currentYear.toString());
//		items[2] = new SelectItem(nextYear.longValue(), nextYear.toString());
//		return items;
	}
	
	/**
	 * get available VOT Type options for plan details
	 * @return SelectItem[]
	 */
	public SelectItem[] getPlanVotTypeOptions() {
		log.info("getPlanVotTypeOptions");
		List<Long> votTypeIds = null;
		List<PmParameterDesc> parameterDescs = new ArrayList<PmParameterDesc>();
		
		if(this.getManagedBean().getSelectedPlan().getPlanType().getParameterId()!=null 
				&& this.getManagedBean().getSelectedPlan().getPlanYear()!=null){
			votTypeIds = PpCommonUtil.existingPlannedVotTypes(
					this.getCurrentActiveOrg().getOrgProfileId(), 
					this.getManagedBean().getSelectedPlan().getPlanYear());

			SearchBean<PmParameterDesc> searchBean = null;
			if(votTypeIds!=null && !votTypeIds.isEmpty()){
				log.info("filter VOT: ");
				searchBean = new SearchBean<PmParameterDesc>();
				//not included parameterId
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
				bean.setSearchField("pmParameter.parameterId");
				bean.setSearchValue(votTypeIds);
				searchBean.getDataFilterBeans().add(bean);
			}
			parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.VOT_TYPE, searchBean);
		}
	
			
		SelectItem[] items = null;
		items = new SelectItem[parameterDescs.size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale()));
		int i = 1;
		for (PmParameterDesc parameterDesc : parameterDescs) {
			items[i++] = new SelectItem(parameterDesc.getPmParameter()
					.getParameterId(), parameterDesc.getCodeName());
		}
		return items;
		// TODO REMOVE RAIN
//		return getVotTypeOptions();
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
			}else if(ProcurementPlanConst.PPE_PENDING_REVISION.equals(statusId)){
				roleCode = RoleCode.PLAN_EXPEN_OFFICER.value();
			}else if(ProcurementPlanConst.PPP_PENDING_REVISION.equals(statusId)){
				roleCode = RoleCode.PLAN_PROCU_OFFICER.value();
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
	
	
	/* BUTTON SUBMIT - BEGIN */
	
	/**
	 * Get & sync the selected plan actors from UI
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
	
	/**
	 * Get new rejection reason from UI
	 * @param ppPlan
	 * @return List <PpRejectReason>
	 */
	private List <PpRejectReason> getAllRejectionReasons(PpPlan ppPlan){
		log.info("getAllRejectionReasons");
		List <PpRejectReason> rejectReasons = null;
		if(ppPlan.getStatus()!=null 
				&& (ProcurementPlanConst.PPP_PENDING_VERIFICATION.equals(ppPlan.getStatus().getStatusId())
						|| ProcurementPlanConst.PPP_PENDING_APPROVAL.equals(ppPlan.getStatus().getStatusId())
						|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(ppPlan.getStatus().getStatusId()))){
			
			rejectReasons = new ArrayList<PpRejectReason>();
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
				for(PpProcurementItem ppProcurementItem:ppPlan.getPpProcurementItems()){
					if(ppProcurementItem.getPpRejectReasons()!=null && !ppProcurementItem.getPpRejectReasons().isEmpty()){
						PpRejectReason rejectReason = ppProcurementItem.getPpRejectReasons().get(0);
						if(rejectReason!=null && !StringUtils.isBlank(rejectReason.getReasonDesc())){ //&& rejectReason.isBrandNew() add this for phase 2
							rejectReason.setDocId(ppProcurementItem.getProcurementItemId());
							rejectReason.setDocType(ParameterConst.DOCTYPE_PLAN_PROCUREMENT);
							rejectReasons.add(rejectReason);
						}
					}
				}
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
				for(PpExpenditureItem ppExpenditureItem:ppPlan.getPpExpenditureItems()){
					if(ppExpenditureItem.getPpRejectReasons()!=null && !ppExpenditureItem.getPpRejectReasons().isEmpty()){
						PpRejectReason rejectReason = ppExpenditureItem.getPpRejectReasons().get(0);
						if(rejectReason!=null &&!StringUtils.isBlank(rejectReason.getReasonDesc())){ //&& rejectReason.isBrandNew() add this for phase 2
							rejectReason.setDocId(ppExpenditureItem.getExpenditureItemId());
							rejectReason.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
							rejectReasons.add(rejectReason);
						}
					}
				}
			}
		}
		return rejectReasons;
	}
	

	private PpRemark getRemark(PpPlan ppPlan){
		log.info("getRemark");
		PpRemark remark = null;
		if(this.getManagedBean().getSelectedRemarks()!=null && !this.getManagedBean().getSelectedRemarks().isEmpty()){
			PpRemark ppRemark = this.getManagedBean().getSelectedRemarks().get(0);
			if(ppRemark.isBrandNew() && !StringUtils.isBlank(ppRemark.getRemark())){
				ppRemark.setDocId(ppPlan.getPlanId());
				ppRemark.setDocType(ParameterConst.DOCTYPE_PLAN_PLANLEVEL);
				if(ppRemark.getRoleCode() ==null || StringUtils.isBlank(ppRemark.getRoleCode().getRoleCode())){
					log.info("set setRemarkRole");
					PmRole roleCode = new PmRole();
					roleCode.setRoleCode(getOneRoleByStatus(ppPlan));
					ppRemark.setRoleCode(roleCode);
					ppRemark.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
				}
				remark = ppRemark;
			}
			
		}
		return remark;
	}
	
	private List <PpRemark> getAllJustifications(PpPlan ppPlan){
		log.info("getAllJustifications");
		List <PpRemark> justifications = null;
		if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
			if(ppPlan.getStatus()==null
					|| (ProcurementPlanConst.PPE_PENDING_REVISION.equals(ppPlan.getStatus().getStatusId())
							|| ProcurementPlanConst.PPE_APPROVED.equals(ppPlan.getStatus().getStatusId())
							|| ProcurementPlanConst.PPE_PENDING_SUBMISSION.equals(ppPlan.getStatus().getStatusId()))){
				justifications = new ArrayList<PpRemark>();
				int itemIndex = 0;
				for(PpExpenditureItem ppExpenditureItem:ppPlan.getPpExpenditureItems()){
					if(ppExpenditureItem.getJustifications()!=null && !ppExpenditureItem.getJustifications().isEmpty()){
						PpRemark justification = ppExpenditureItem.getJustifications().get(0);
						if(justification!=null && !StringUtils.isBlank(justification.getRemark())){ //&& itemRemark.isBrandNew() add this for phase 2
							justification.setItemSeq(itemIndex);
							justification.setDocId(ppExpenditureItem.getExpenditureItemId());
							justification.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
							if(justification.getRoleCode() ==null || StringUtils.isBlank(justification.getRoleCode().getRoleCode())){
								log.info("set setRemarkRole");
								PmRole roleCode = new PmRole();
								roleCode.setRoleCode(RoleCode.PLAN_EXPEN_OFFICER.value());
								justification.setRoleCode(roleCode);
								justification.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
							}
							justifications.add(justification);
						}
					}
					itemIndex++;
				}
				
			}
		}
		return justifications;
	}
	
	private List <PpRemark> getAllItemRemarks(PpPlan ppPlan){
		log.info("getAllItemRemarks");

		List <PpRemark> itemRemarks =  new ArrayList<PpRemark>();
		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
			int itemIndex = 0;
			for(PpProcurementItem ppProcurementItem:ppPlan.getPpProcurementItems()){
				System.out.println("loop pp item1 ");
				if(ppProcurementItem.getPpRemarks()!=null && !ppProcurementItem.getPpRemarks().isEmpty()){
					System.out.println("loop pp item 2");
					PpRemark itemRemark = ppProcurementItem.getPpRemarks().get(0);
					if(itemRemark!=null && !StringUtils.isBlank(itemRemark.getRemark())){ //&& itemRemark.isBrandNew() add this for phase 2
						itemRemark.setItemSeq(itemIndex);
						itemRemark.setDocId(ppProcurementItem.getProcurementItemId());
						itemRemark.setDocType(ParameterConst.DOCTYPE_PLAN_PROCUREMENT);
						if(itemRemark.getRoleCode() ==null || StringUtils.isBlank(itemRemark.getRoleCode().getRoleCode())){
							log.info("set setRemarkRole");
							PmRole roleCode = new PmRole();
							roleCode.setRoleCode(getOneRoleByStatus(ppPlan));
							itemRemark.setRoleCode(roleCode);
							itemRemark.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
						}
						itemRemarks.add(itemRemark);
					}
				}
				itemIndex++;
			}
		}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
			int itemIndex = 0;
			// Reason for update
//			if(ppPlan.getStatus()==null && ProcurementPlanConst.PPE_APPROVED.equals(ppPlan.getStatus().getStatusId())){
				for(PpExpenditureItem ppExpenditureItem:ppPlan.getPpExpenditureItems()){
					System.out.println("loop ep item1 ");
					if(ppExpenditureItem.getPpRemarks()!=null && !ppExpenditureItem.getPpRemarks().isEmpty()){
						PpRemark itemRemark = ppExpenditureItem.getPpRemarks().get(0);
						if(itemRemark!=null && !StringUtils.isBlank(itemRemark.getRemark())){ //&& itemRemark.isBrandNew() add this for phase 2
							System.out.println("loop ep item2 ");
							itemRemark.setItemSeq(itemIndex);
							itemRemark.setDocId(ppExpenditureItem.getExpenditureItemId());
							itemRemark.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
							if(itemRemark.getRoleCode() ==null || StringUtils.isBlank(itemRemark.getRoleCode().getRoleCode())){
								log.info("set setRemarkRole");
								PmRole roleCode = new PmRole();
								roleCode.setRoleCode(RoleCode.PLAN_EXPEN_OFFICER.value());
								itemRemark.setRoleCode(roleCode);
								itemRemark.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
							}
							itemRemarks.add(itemRemark);
						}
					}
					itemIndex++;
				}
//			}
		}
		
		return itemRemarks;
	}

	private boolean validationPlanSubmit(String action, PpPlan ppPlan){
		log.info("validationPlanSubmit - START");
		// basic validation TODO
		
		if(ppPlan.getPlanType().getParameterId()==null){
			throw new EpException("Plan Type is mandatory."); //TODO
		}
		
		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
			
			for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
				if(StringUtils.isBlank(ppItem.getProcurementTitle())){
					throw new EpException("Procurement Title is mandatory."); //TODO
				}
				if(ppItem.getProcurementCat().getParameterId()==null){
					throw new EpException("Procurement Category is mandatory."); //TODO
				}
				if(ppItem.getProcurementType().getParameterId()==null){
					throw new EpException("Procurement Method is mandatory."); //TODO
				}
				if(ppItem.getInvitationDate()==null){
					throw new EpException("Invitation Date is mandatory."); //TODO
				}
				if(ppItem.getEstimatedAmt()==null || ppItem.getEstimatedAmt().intValue()<=0){
					throw new EpException("Est. Amt. must be greater than 0."); //TODO
				}
			}
			
			if(this.getManagedBean().getVerifierModel().getTarget()==null || this.getManagedBean().getVerifierModel().getTarget().isEmpty()){
				throw new EpException("At least one Plan Verification Officer is required."); //TODO
			}
			if(this.getManagedBean().getApproverModel().getTarget()==null || this.getManagedBean().getApproverModel().getTarget().isEmpty()){
				throw new EpException("At least one Plan Approver is required."); //TODO
			}
		}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
			if(ppPlan.getVotFundType().getParameterId()==null){
				throw new EpException("Amanah/ VOT Type Type is mandatory."); //TODO
			}
			for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
				if(epItem.getPmSo().getSoId()==null){
					throw new EpException("Object is mandatory."); //TODO
				}
				if(epItem.getBudgetAmt()==null || epItem.getBudgetAmt().intValue()<=0){
					throw new EpException("Total Approved Budget must be greater than 0."); //TODO
				}
				if(epItem.getPlanAmt()==null || epItem.getPlanAmt().intValue()<=0){
					throw new EpException("Budget to spend through eP must be greater than 0."); //TODO
				}
				if(epItem.getNonEpAmt()==null || epItem.getNonEpAmt().intValue()<0){
					throw new EpException("Non eP Allocation must not be lesser than 0."); //TODO
				}
				if(epItem.getNonEpAmt().intValue()>0){
					if(epItem.getJustifications()==null 
							|| epItem.getJustifications().get(0)==null 
							|| StringUtils.isBlank(epItem.getJustifications().get(0).getRemark())){
						throw new EpException("Justification for not using eP is mandatory."); //TODO
					}
				}
			}
			if(this.getManagedBean().getApproverModel().getTarget()==null || this.getManagedBean().getApproverModel().getTarget().isEmpty()){
				throw new EpException("At least one Plan Approver is required."); //TODO
			}
		}

		
		if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action) 
				||BpmOutcomeConst.BPM_VERIFY.equalsIgnoreCase(action) ){ //TODO
			// no rejected item checked
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
					if(ppItem.isRejected()){
						throw new EpException("Procurement plan contains rejected items."); //TODO
					}
				}
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
					if(epItem.isRejected()){
						throw new EpException("Procurement plan contains rejected items."); //TODO
					}
				}
			}
		}else if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)){
			boolean isItemRejected = false;
			//if rejected item checked, must supply with reason
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
					if(ppItem.isRejected()){
						isItemRejected = true;
						if(ppItem.getPpRejectReasons()!=null && !ppItem.getPpRejectReasons().isEmpty()){
							if(ppItem.getPpRejectReasons().get(0).isBrandNew()){
								if(StringUtils.isBlank(ppItem.getPpRejectReasons().get(0).getReasonDesc())){
									throw new EpException("Reject Reason is required for rejected item."); //TODO
								}
							}
						}else{
							throw new EpException("Reject Reason is required for rejected item."); //TODO
						}
					}

				}
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
					if(epItem.isRejected()){
						isItemRejected = true;
						if(epItem.getPpRejectReasons()!=null && !epItem.getPpRejectReasons().isEmpty()){
							if(epItem.getPpRejectReasons().get(0).isBrandNew()){
								if(StringUtils.isBlank(epItem.getPpRejectReasons().get(0).getReasonDesc())){
									throw new EpException("Reject Reason is required for rejected item."); //TODO
								}
							}
						}else{
							throw new EpException("Reject Reason is required for rejected item."); //TODO
						}
					}
				}
			}
			
			if(!isItemRejected){
				//overall remark is required
				remarkIsRequired();
			}

		}
		log.info("validationPlanSubmit - END");
		return false;
		
	}
	
	private void remarkIsRequired(){
		log.info("remarkIsRequired");
		if(this.getManagedBean().getSelectedRemarks()!=null && !this.getManagedBean().getSelectedRemarks().isEmpty()){
			if(StringUtils.isBlank(this.getManagedBean().getSelectedRemarks().get(0).getRemark())){
				throw new EpException("Plan Remark is required"); //TODO
			}
		}
	}
	
	/**
	 * validate if plan with similar plan year, plan type and/or vot type
	 * @param ppPlan
	 */
	private void validationPlanAvailability(PpPlan ppPlan){
		log.info("validationPlanAvailability");
		SearchBean<PpPlan> searchBean = new SearchBean<PpPlan>();
		List <PpPlan> existingPlans = null;
		String planType = ppPlan.getPlanType().getParameterCode();
		
		//orgProfile
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"orgProfile.orgProfileId", 
				SearchOperator.OPERATOR_EQUALS.value(), 
				ppPlan.getOrgProfile().getOrgProfileId()));
		
		//recordStatusFlg
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"recordStatusFlg", 
				SearchOperator.OPERATOR_EQUALS.value(), 
				DbValueConst.RECORD_STATUS_ACTIVE));
		
		//planYear
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"planYear", 
				SearchOperator.OPERATOR_EQUALS.value(), 
				ppPlan.getPlanYear()));

		//planType
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"planType.parameterId", 
				SearchOperator.OPERATOR_EQUALS.value(), 
				ppPlan.getPlanType().getParameterId()));
		
		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(planType)){
			/* additional rules for PP if any*/
			
		}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(planType)){
			/* additional rules for EP if any*/
			
			//vot type
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"ppPlan.votFundType.parameterId", 
					SearchOperator.OPERATOR_EQUALS.value(), 
					ppPlan.getVotFundType().getParameterId()));
			
		}
		
		existingPlans = PpCommonUtil.retrievePlanBySearchBean(searchBean);
		if(existingPlans!=null && !existingPlans.isEmpty()){
			//TODO
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(planType)){
				throw new EpException("Procurement Plan with similar plan year is existed.");
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(planType)){
				throw new EpException("Expenditure Plan with similar plan year and vot type is existed.");
			}
			
		}
	}
	/**
	 * Submit & update plan
	 * @param action (outcome)
	 * @return xhtml
	 * @throws Exception
	 */
	public String savePlan(String action) throws Exception{
		log.info("savePlan - START");
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		PpPlan ppPlan = null; 
		List <PpRejectReason> rejectReasons = null;
		List <PpRemark> itemRemarks = null;
		List <PpRemark> justifications = null;
		String docType = "";
		PpRemark remark = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			ppPlan = this.getManagedBean().getSelectedPlan();
			
			//Validation
			validationPlanSubmit(action, ppPlan);
			if(ppPlan.isBrandNew()){
				validationPlanAvailability(ppPlan); //TODO rain remove
			}
			
			//Get All Values
			updatePlanActors(ppPlan);
			
			//Get Remark at Plan Level
			remark = getRemark(ppPlan);
			
			//Get Remark at Item Level
			itemRemarks = getAllItemRemarks(ppPlan); 
			
			//Get Justification 
			justifications = getAllJustifications(ppPlan);
			
			if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)){
				rejectReasons = getAllRejectionReasons(ppPlan);
				if(rejectReasons!=null && !rejectReasons.isEmpty()){
					reqDTO.setRejectReasonList(rejectReasons);
				}
			}
			
			reqDTO.setPpRemark(remark);
			reqDTO.setPpRemarks(itemRemarks);
			reqDTO.setJustifications(justifications);
			reqDTO.setPpPlan(ppPlan);
			reqDTO.setOutcome(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken()); 
			if(!ppPlan.isBrandNew()){
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
			}	
			reqDTO.setDocTitle(PpCommonUtil.getDocTitle(ppPlan));
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.savePlan(reqDTO);
			docType = 
					ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode()) 
					? ParameterConst.DOCTYPE_PLAN_PROCUREMENT
					: ParameterConst.DOCTYPE_PLAN_EXPENDITURE;
					
					
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully"); 
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
		log.info("savePlan - END");
		return null;
	}
	
	/* BUTTON SUBMIT - END */
	
	
	
	
	
	
	/* FILTER & SEARCH PLAN LIST - BEGIN */
	
	/**
	 * Generate org filter based on login
	 */
	public void generateOrgFilter(){
		log.info("generateOrgFilter");
		//check role and populate drop down value accordingly
		StringBuilder conditionSB = new StringBuilder();

		
		if(!isRoleBPK()){
			if(isRoleApprover()){
				conditionSB.append("|");
				conditionSB.append("orgProfileId^");
				conditionSB.append(this.getCurrentActiveOrg().getOrgProfileId());
				conditionSB.append("*parentOrgProfile.orgProfileId^");
				conditionSB.append(this.getCurrentActiveOrg().getOrgProfileId());
			}else{
				conditionSB.append("|");
				conditionSB.append("orgProfileId^");
				conditionSB.append(this.getCurrentActiveOrg().getOrgProfileId());
			}
		}
			
		this.getManagedBean().setFilterOrgCondition(conditionSB.toString());

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
			if(!isRoleBPK()){
				orgProfileId = this.getCurrentActiveOrg().getOrgProfileId();
			}
			//default
			items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
			planYears = PpCommonUtil.getExistingPlanYear(getFacesScopes().getUserSessionBean().getUserId(), orgProfileId);
			
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
			throw new EpException(e.toString()); //TODO
		}
		return items;
	}
	
	/**
	 * Get search bean, filtered based on login org.
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
//		if(planSearchBean.getMinistry()!=null && planSearchBean.getMinistry().getOrgProfileId()!=null){
//			log.info("+ getMinistry "  +planSearchBean.getMinistry().getOrgProfileId());
//			SearchFilterBean bean = new SearchFilterBean();
//			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
//			bean.setSearchField("orgProfile.parentOrgProfile.orgProfileId");
//			bean.setSearchValue(planSearchBean.getMinistry().getOrgProfileId());
//			searchBean.getDataFilterBeans().add(bean);
//			isEmpty = false;
//		}
		
		// ptj
		if(planSearchBean.getPtj()!=null  && planSearchBean.getPtj().getOrgProfileId()!=null){
			log.info("+ getPtj "  +planSearchBean.getPtj().getOrgProfileId());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("orgProfile.orgProfileId");
			bean.setSearchValue(planSearchBean.getPtj().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}else{
			//filter plan by org profile
			if(!isRoleBPK()){
				if(isRoleApprover()){
					log.info("isRoleApprover");
					searchBean.getDataFilterBeans().add(
							new SearchMultiFilterBean(
									new SearchFilterBean(
											"orgProfile.orgProfileId", 
											SearchOperator.OPERATOR_EQUALS.value(), 
											this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()),
									new SearchFilterBean(
											"orgProfile.parentOrgProfile.orgProfileId", 
											SearchOperator.OPERATOR_EQUALS.value(), 
											this.getManagedBean().getCurrentActiveOrg().getOrgProfileId(), 
											SearchCondition.CONDITION_OR.value())));
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
		this.getManagedBean().setSearch(false);
		this.getManagedBean().setPlanSearchBean(new ProcurementPlanSearchBean());
		generateOrgFilter();
		ppPlanDataModel = new PpPlanDataModel();
	}
	
	/**
	 * search plan
	 */
	public void searchPlan(){
		log.info("searchPlan");
		this.getManagedBean().setSearch(true);
		ppPlanDataModel.reloadData();
		
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
				if(getManagedBean().isSearch()){
					boolean isPlanner = isRolePlanner();
					ISearchBean<PpPlan> searchBeanData = getSearchBeanData(searchBean);
					ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
					reqDTO.setSearchBean(searchBeanData);
					IProcurementPlanSO reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
					ProcurementPlanResDTO resDTO = reqSO.retrievePlanBySearchBean(reqDTO);
					if (resDTO.isSuccessful()) {
						// result list icon
						if(resDTO.getPlanResultBean()!=null && resDTO.getPlanResultBean().getResultList()!=null){
							for(PpPlan ppPlan: resDTO.getPlanResultBean().getResultList()){
								if(ppPlan.getStatus()!=null){
									
									if((ProcurementPlanConst.PPP_APPROVED.equals(ppPlan.getStatus().getStatusId()) 
											|| ProcurementPlanConst.PPE_APPROVED.equals(ppPlan.getStatus().getStatusId()))
											&& isPlanner){
	
										ppPlan.setUpdatable(true);
									}
								}
							}
						}
						
						return resDTO.getPlanResultBean();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}
	/* FILTER & SEARCH PLAN LIST - END */
	

}
