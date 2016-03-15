/*
 * Copyright 2013 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pp.procurementplan;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
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
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.pp.plandata.PPPlanDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmProcurementCatType;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pp.procurementplan.PpExpenditureItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanActor;
import my.ep.persistence.entity.pp.procurementplan.PpProcurementItem;
import my.ep.persistence.entity.pp.procurementplan.PpRejectReason;
import my.ep.persistence.entity.pp.procurementplan.PpRemark;
import my.ep.persistence.entity.pp.procurementplan.PpWorkflowStatus;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.pp.procurementplan.ProcurementPlanManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.pp.common.PpCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
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
	private IPagingDataModel<PmOrgProfile> ppOrgProfileDataModel = new PpOrgProfileDataModel();
	
	//Default value
	public static final boolean SEARCH_DEFAULT = false;
	
	//Mapper for DDL object
	public static Map<Long, Object> planTypeMapper;
	public static Map<Long, Object> votTypeMapper;
	
	//Authorization 
	private ISearchBean<PmParameterDesc> planTypeSearchBean;
	
	//Search Org Profie
	private static final int SEARCH_MINISTRY_CODE = 0;
	private static final int SEARCH_MINISTRY_NAME = 1;
	private static final int SEARCH_PTJ_CODE = 2;
	private static final int SEARCH_PTJ_NAME = 3;
	
	
	public ISearchBean<PmParameterDesc> getPlanTypeSearchBean() {
		log.info("getPlanTypeSearchBean");
		if (this.planTypeSearchBean == null) {
			this.planTypeSearchBean = super.createSearchBean(PmParameterDesc.class, "somPlanType");
		}

		return this.planTypeSearchBean;
	}
		
	
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
	public void preLoading(boolean isTrackingDiary){
		log.info("preLoading");
		this.getManagedBean().setBackAllow(true);
		this.resetPageModeFlags();
		this.resetManagedBean();
		this.getManagedBean().setTrackingDiary(isTrackingDiary);
		this.retrievePlanTypeMapper();
		initialSearchPlan();
		this.resetPlanSearchBean();
		generateSearchOptions();
		setupOrgProfile();	
		
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
		this.resetManagedBean();
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		if (taskId == null) {
			return;
		}
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);

		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();

		//only for PLAN OFFICER/ PLAN APPROVER/ PLAN VERIFIER (from tasklist)
		if(isRolePlanner() || isRoleApprover() || isRoleVerifier()){
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
			this.getManagedBean().setOutComes(outcome);
		}
		
		this.getManagedBean().setTaskName(res.getTaskName());
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setBackAllow(false);
		generatePageModeForProcess(res.getTaskName());
		
		this.getManagedBean().setBpmStatusCreator(res.getCreator());
		this.getManagedBean().setBpmStatusDate(new Timestamp(res.getCreateDate().getTimeInMillis()));
		this.getManagedBean().setBpmStatusId(res.getDocStatus().longValue());

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
	public String redirectToMaintainPlan(Long id,String action,boolean isTrackingDiary) throws Exception{
		log.info("redirectToMaintainPlan");
		this.resetManagedBean();
		this.getManagedBean().setBtnValue(action);
		this.getManagedBean().setTrackingDiary(isTrackingDiary);
		this.retrievePlanTypeMapper();
		this.retrieveVotTypeMapper();
		if(id!=null && id>0){
			loadPlanDetails(id, true);
			loadDefaultButtons();
		}
		RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
		return NAV_TO_MAINTAIN;
	}
	
	

	/**
	 * compare username case insensitive
	 * @param username
	 * @return
	 */
	public boolean compareUserNameInsensitive(String username) {
		log.info("compareUserNameInsensitive");
		boolean result = false;
		FacesScopes facesScopes = new FacesScopes();
		if (username != null
				&& username.equalsIgnoreCase(facesScopes.getUserScreenName())) {
			result = true;
		} else {
			result =  false;
		}
		log.info("compareUserNameInsensitive: " + result);	
		return result;
	}

	
	/* PAGE MODE/VIEW CONTROL - BEGIN */

	/**
	 * Load and generate default buttons
	 */
	private void loadDefaultButtons(){
		log.info("loadDefaultButtons");
		//if status is approved
		Long statusId = this.getManagedBean().getSelectedPlan().getStatus().getStatusId();
		if(isRolePlanner()){
			if(this.getManagedBean().getSelectedPlan().isBrandNew()){
				//BPM buttons
				String[] outcome = new String[2];
				outcome[0] = BpmOutcomeConst.BPM_SAVE;
				outcome[1] = BpmOutcomeConst.BPM_SUBMIT;
				this.getManagedBean().setOutComes(outcome);
			}else if(StringUtils.isNotBlank(getManagedBean().getBtnValue()) 
					&& (ProcurementPlanConst.PPP_APPROVED.equals(statusId) 
					|| ProcurementPlanConst.PPE_APPROVED.equals(statusId))){
				if(ProcurementPlanConst.ACTION_UPDATE_BTN.equals(getManagedBean().getBtnValue())){
					String[] outcome = new String[2];
					outcome[0] = BpmOutcomeConst.BPM_SAVE;
					outcome[1] = BpmOutcomeConst.BPM_SUBMIT;
					this.getManagedBean().setOutComes(outcome);
				}else{
					this.getManagedBean().setOutComes(null);
				}
			}	
		}  
		else{
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
					|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(status)
					|| ProcurementPlanConst.PPP_PENDING_REVISION_APPROVAL.equals(status)
					|| ProcurementPlanConst.PPP_PENDING_REVISION_VERIFICATION.equals(status)
					|| ProcurementPlanConst.PPE_PENDING_REVISION_APPROVAL.equals(status)){
				this.getManagedBean().setModeEditable(false);
				this.getManagedBean().setModeReview(true);
			
			// APPROVED
			}else if(ProcurementPlanConst.PPP_APPROVED.equals(status) 
					|| ProcurementPlanConst.PPE_APPROVED.equals(status) ){
				this.getManagedBean().setModeReview(false);
				this.getManagedBean().setModeReviewEditable(false);
				if(isRolePlanner() 
						&& !(getManagedBean().getBtnValue() != null 
						&& ProcurementPlanConst.ACTION_VIEW_BTN.equals(getManagedBean().getBtnValue()))){
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
					|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(status)
					|| ProcurementPlanConst.PPP_PENDING_REVISION_APPROVAL.equals(status)
					|| ProcurementPlanConst.PPP_PENDING_REVISION_VERIFICATION.equals(status)
					|| ProcurementPlanConst.PPE_PENDING_REVISION_APPROVAL.equals(status)){
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
				this.getManagedBean().setModeEditable(true);
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

		this.getManagedBean().setPpTotalEst(null);
		this.getManagedBean().setDeletePpItems(null);
		this.getManagedBean().setApproverModel(null);
		this.getManagedBean().setVerifierModel(null);
		this.getManagedBean().setSelectedRejectReasons(null);
		this.getManagedBean().setSelectedJustifications(null);
		this.getManagedBean().setSelectedRemarks(null);
		this.getManagedBean().setCurrentActiveOrg(null);
		this.getManagedBean().setFilterOrgCondition(null);
		this.getManagedBean().setPpWorkflowStatus(null);
		this.getManagedBean().setBpmStatusCreator(null);
		this.getManagedBean().setBpmStatusDate(null);
		this.getManagedBean().setBpmStatusId(null);
		this.getManagedBean().setInvitationYear("");
		this.getManagedBean().setApprovedPlan(false);
		this.getManagedBean().setBtnValue(null);
		this.getManagedBean().setTrackingDiary(false);
		
		//EP
		this.getManagedBean().setEpItemsB(null);
		this.getManagedBean().setEpTotalBudgetB(null);
		this.getManagedBean().setEpTotalPlanB(null);
		this.getManagedBean().setEpTotalNonEpAllocB(null);
		this.getManagedBean().setEpItemsT(null);
		this.getManagedBean().setEpTotalBudgetT(null);
		this.getManagedBean().setEpTotalPlanT(null);
		this.getManagedBean().setEpTotalNonEpAllocT(null);
		this.getManagedBean().setEpItemsP(null);
		this.getManagedBean().setEpTotalBudgetP(null);
		this.getManagedBean().setEpTotalPlanP(null);
		this.getManagedBean().setEpTotalNonEpAllocP(null);
		this.getManagedBean().setEpItemsS(null);
		this.getManagedBean().setEpTotalBudgetS(null);
		this.getManagedBean().setEpTotalPlanS(null);
		this.getManagedBean().setEpTotalNonEpAllocS(null);
		this.getManagedBean().setEpItemsH(null);
		this.getManagedBean().setEpTotalBudgetH(null);
		this.getManagedBean().setEpTotalPlanH(null);
		this.getManagedBean().setEpTotalNonEpAllocH(null);
		

	}
	

	/**
	 * Load plan details by id
	 * @param id
	 * @param fromList (from search screen or task list)
	 * @param statusLoginId
	 * @param statusDate
	 * @param statusId
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
						throw new EpException(MessageConst.ERROR_730000);
					}
//					if(ppPlan.getVotFundType()==null){
//						ppPlan.setVotFundType(new PmParameter());
//					}
					
					ppPlan.setOrgProfile(pmOrgProfile); 
					ppPlan.setStatus(ppPlan.getCurrentStatus()!=null?ppPlan.getCurrentStatus().getPmStatus():new PmStatus());
					if(ppPlan.getCurrentStatus()==null || (ppPlan.getCurrentStatus()!=null && ppPlan.getCurrentStatus().getPmStatus()==null)){
						log.info("no workflow status");
						ppPlan.setCurrentStatus(new PpWorkflowStatus());
						ppPlan.getCurrentStatus().setPmStatus(new PmStatus());
						/*
						no longer required as BPM will always insert status 23 Jan 2015
						if(!fromList){
							log.info("load BPM status");
							//get BPM status instead
							PmStatus bpmStatus = new PmStatus();
							bpmStatus.setStatusId(this.getManagedBean().getBpmStatusId());
							PpWorkflowStatus ppWorkflowStatus = new PpWorkflowStatus();
							ppWorkflowStatus.setPmStatus(bpmStatus);
							ppWorkflowStatus.setCreatedDate(this.getManagedBean().getBpmStatusDate());
							ppWorkflowStatus.setCreatedUsername(PpCommonUtil.retrieveUserByLoginIdOrUserId(this.getManagedBean().getBpmStatusCreator(), null).getUserName());
							this.getManagedBean().setPpWorkflowStatus(ppWorkflowStatus);
						}else{
							// PP20150108-0012
							log.info("load default status");
							PmStatus bpmStatus = new PmStatus();
							PpWorkflowStatus ppWorkflowStatus = new PpWorkflowStatus();
							if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
								bpmStatus.setStatusId(ProcurementPlanConst.PPP_PENDING_SUBMISSION);
								ppWorkflowStatus.setPmStatus(bpmStatus);
							}else{
								bpmStatus.setStatusId(ProcurementPlanConst.PPE_PENDING_SUBMISSION);
								ppWorkflowStatus.setPmStatus(bpmStatus);
							}
							ppWorkflowStatus.setCreatedDate(ppPlan.getCreatedDate());
							ppWorkflowStatus.setCreatedUsername(PpCommonUtil.retrieveUserByLoginIdOrUserId(null, ppPlan.getCreatedBy()).getUserName());
							this.getManagedBean().setPpWorkflowStatus(ppWorkflowStatus);
						}
						*/
					}else{
						this.getManagedBean().setPpWorkflowStatus(ppPlan.getCurrentStatus());
						//check ppWorkflowStatus contains approved
						List <PpWorkflowStatus> ppWorkflowStatuses = retrievePpApprovedStatus(ppPlan);
						if(ppWorkflowStatuses!=null && !ppWorkflowStatuses.isEmpty()){
							this.getManagedBean().setApprovedPlan(true);
						}
					}
					
					this.getManagedBean().setSelectedPlan(ppPlan);
					if(fromList){
						generatePageMode();
					}
					
					loadPlanActors(ppPlan.getOrgProfile(), ppPlan.getPpPlanActors()); 
					loadPlanRemark(resDTO.getRemarkList());
					
					// define item editable mode
					itemEditableMode(ppPlan);
					
					//load plan det
					if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
						loadEpItems(ppPlan);
					}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
						calculatePpGrandTotal();
					}
					
				}else{
					throw new EpException(MessageConst.ERROR_730001);
				}
			}
			
		}catch(Exception e){
			log.error(e);
			throw e;
		}finally{
			reqSO = null;
		}
		
		log.info("loadPlanDetails - End");
	}

	/**
	 * load expenditure items
	 */
	private void loadEpItems(PpPlan ppPlan){
		log.info("loadEpItems - Start");
		if(ppPlan.getPpExpenditureItems()==null){
			ppPlan.setPpExpenditureItems(new ArrayList<PpExpenditureItem>());
		}
		this.getManagedBean().setEpItemsB(new ArrayList<PpExpenditureItem>());
		this.getManagedBean().setEpItemsT(new ArrayList<PpExpenditureItem>());
		this.getManagedBean().setEpItemsP(new ArrayList<PpExpenditureItem>());
		this.getManagedBean().setEpItemsS(new ArrayList<PpExpenditureItem>());
		this.getManagedBean().setEpItemsH(new ArrayList<PpExpenditureItem>());
		
		for(PpExpenditureItem ppExpenditureItem:ppPlan.getPpExpenditureItems()){
			//B
			if(ParameterConst.VOT_MENGURUS.equals(ppExpenditureItem.getVotFundType().getParameterCode())){
				this.getManagedBean().getEpItemsB().add(ppExpenditureItem);
			}
			//T
			if(ParameterConst.VOT_TANGGUNGAN.equals(ppExpenditureItem.getVotFundType().getParameterCode())){
				this.getManagedBean().getEpItemsT().add(ppExpenditureItem);
			}
			//P
			if(ParameterConst.VOT_PEMBANGUNAN.equals(ppExpenditureItem.getVotFundType().getParameterCode())){
				this.getManagedBean().getEpItemsP().add(ppExpenditureItem);
			}
			//S
			if(ParameterConst.VOT_SETIA.equals(ppExpenditureItem.getVotFundType().getParameterCode())){
				this.getManagedBean().getEpItemsS().add(ppExpenditureItem);
			}
			//H
			if(ParameterConst.VOT_AMANAH.equals(ppExpenditureItem.getVotFundType().getParameterCode())){
				this.getManagedBean().getEpItemsH().add(ppExpenditureItem);
			}
		}
		calculateEpGrandTotal(ParameterConst.VOT_MENGURUS);
		calculateEpGrandTotal(ParameterConst.VOT_TANGGUNGAN);
		calculateEpGrandTotal(ParameterConst.VOT_PEMBANGUNAN);
		calculateEpGrandTotal(ParameterConst.VOT_SETIA);
		calculateEpGrandTotal(ParameterConst.VOT_AMANAH);
		
		log.info("loadEpItems - End");
	}
	
	/**
	 * retrieve pp approved status
	 * @param planType
	 * @return List<PpWorkflowStatus>
	 */
	private List<PpWorkflowStatus> retrievePpApprovedStatus(PpPlan ppPlan){
		log.info("retrievePpApprovedStatus");
		Long docId = null;
		String docType = null;
		Long statusId = null;
		
		List<PpWorkflowStatus> ppWorkflowStatus = new ArrayList<PpWorkflowStatus>();
		docId = ppPlan.getPlanId();
		ppPlan.getPlanType();
		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
			docType = ParameterConst.DOCTYPE_PLAN_PROCUREMENT;
			statusId = ProcurementPlanConst.PPP_APPROVED;
		}else{
			docType = ParameterConst.DOCTYPE_PLAN_EXPENDITURE;
			statusId = ProcurementPlanConst.PPE_APPROVED;
		}
		
		ppWorkflowStatus = PpCommonUtil.retrievePpApprovedStatus(docId, docType, statusId);
		
		return ppWorkflowStatus;
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
					}else{
						epItem.setEditable(false);
					}
				}
				if(this.getManagedBean().getSelectedPlan().isRejected()){
					for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
						if(DbValueConst.RECORD_STATUS_ACTIVE == epItem.getRecordStatusFlg()){
							epItem.setEditable(true);
						}else{
							epItem.setEditable(false);
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
						}else{
							ppItem.setEditable(false);
						}
					}
				}
			}
		}else{
			if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
				for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
					if(DbValueConst.RECORD_STATUS_ACTIVE == epItem.getRecordStatusFlg()){
						epItem.setEditable(true);
					}else{
						epItem.setEditable(false);
					}
				}
			}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
				for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
					if(DbValueConst.RECORD_STATUS_ACTIVE == ppItem.getRecordStatusFlg()){
						ppItem.setEditable(true);
					}else{
						ppItem.setEditable(false);
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
			throw new EpException(MessageConst.ERROR_730002);
		}
		PmOrgProfile pmOrgProfile = PpCommonUtil.retrieveUserOrgListByUserId(getFacesScopes().getUserSessionBean().getUserId(), true);
		if(pmOrgProfile==null || pmOrgProfile.getOrgProfileId()==null){
			throw new EpException(MessageConst.ERROR_730000);
		}
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
		PpPlan ppPlan = new PpPlan();
		ppPlan.setPlanType(new PmParameter());
//		ppPlan.setVotFundType(new PmParameter());
		ppPlan.setOrgProfile(pmOrgProfile);
		ppPlan.setPpPlanActors(new ArrayList<PpPlanActor>());
		ppPlan.setStatus(new PmStatus());
		ppPlan.setCurrentStatus(new PpWorkflowStatus());
		ppPlan.getCurrentStatus().setPmStatus(new PmStatus());
		this.getManagedBean().setSelectedPlan(ppPlan);
		
		generatePageMode();
		loadPlanRemark(new ArrayList<PpRemark>());
		loadPlanActors(ppPlan.getOrgProfile() , null);
		
		//default status for view
		PpWorkflowStatus ppWorkflowStatus = new PpWorkflowStatus();
		ppWorkflowStatus.setPmStatus( new PmStatus());
		ppWorkflowStatus.setCreatedDate(ppPlan.getCreatedDate());
		ppWorkflowStatus.setCreatedUsername(PpCommonUtil.retrieveUserByLoginIdOrUserId(null, ppPlan.getCreatedBy()).getUserName());
		this.getManagedBean().setPpWorkflowStatus(ppWorkflowStatus);
		
		log.info("initialPlanDetails - End");
	}
	
	/**
	 * Get status description by status id
	 */
	public String getStatusDescById(Long id){
		log.info("getStatusDescById");
		
		PmStatusDesc statusDesc = id == 0 ? null:StatusHelper.getInstance().getStatusDescById(id);
		return statusDesc!=null ? statusDesc.getStatusName() : "";
	}
	
	/**
	 * show default status description when statusId is null
	 */
	public String getDefaultStatusDesc(String planType){
		log.info("getDefaultStatusDesc");
		Long statusId = 0L;
		PmStatusDesc statusDesc = null;
		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(planType)){
			statusId = ProcurementPlanConst.PPP_PENDING_SUBMISSION;
			statusDesc = StatusHelper.getInstance().getStatusDescById(statusId);
		}else{
			statusId = ProcurementPlanConst.PPE_PENDING_SUBMISSION;
			statusDesc = StatusHelper.getInstance().getStatusDescById(statusId);
		}
		return statusDesc!=null ? statusDesc.getStatusName() : "";
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
	 * Generate procurement items view
	 * @param plan
	 */
	private void generatePpPlan(PpPlan plan){
		log.info("generatePpPlan");
		if(plan!=null && !plan.isBrandNew()){
			//plan type is not editable after submission
		}else{
			this.getManagedBean().getSelectedPlan().setPpProcurementItems(new ArrayList<PpProcurementItem>());
//			this.getManagedBean().getSelectedPlan().setVotFundType(new PmParameter());
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
			this.getManagedBean().setEpItemsB(new ArrayList<PpExpenditureItem>());
			this.getManagedBean().setEpItemsT(new ArrayList<PpExpenditureItem>());
			this.getManagedBean().setEpItemsP(new ArrayList<PpExpenditureItem>());
			this.getManagedBean().setEpItemsS(new ArrayList<PpExpenditureItem>());
			this.getManagedBean().setEpItemsH(new ArrayList<PpExpenditureItem>());
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
	 * get Plan Type options
	 * @return SelectItem[]
	 */
	public SelectItem[] getPlanTypeOptions() {
		log.info("getPlanTypeOptions");
		return getDropdownValues(ParameterTypeConst.PLAN_TYPE);
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
				throw new EpException(MessageConst.ERROR_730000);
			}
			this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
		}
		return this.getManagedBean().getCurrentActiveOrg();
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
		return getFacesScopes().hasRoleUser(RoleCode.BPK_OFFICER.value());
	}
	
	private boolean isAuditor(){
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value());
	}
	
	/**
	 * Login user has planner role
	 * @return boolean
	 */
	public boolean isRolePlanner(){
		boolean isPlanner = false;
		if(getFacesScopes().hasRoleUser(RoleCode.PLAN_OFFICER.value())){
			isPlanner = true;
		}
		
		/* disable above code and enable following code if roles merge to PLAN_OFFICER  PP20150108-0002
			if(getFacesScopes().hasRoleUser(RoleCode.PLAN_OFFICER.value())){
				isPlanner = true;
			}
		*/
		
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
				roleCode = RoleCode.PLAN_OFFICER.value();
				//roleCode = RoleCode.PLAN_PROCU_OFFICER.value();
			}else if(ProcurementPlanConst.PPE_PENDING_SUBMISSION.equals(statusId)){
				roleCode = RoleCode.PLAN_OFFICER.value();
				//roleCode = RoleCode.PLAN_EXPEN_OFFICER.value();
			}else if(ProcurementPlanConst.PPE_PENDING_REVISION.equals(statusId)){
				roleCode = RoleCode.PLAN_OFFICER.value();
				//roleCode = RoleCode.PLAN_EXPEN_OFFICER.value();
			}else if(ProcurementPlanConst.PPP_PENDING_REVISION.equals(statusId)){
				roleCode = RoleCode.PLAN_OFFICER.value();
				//roleCode = RoleCode.PLAN_PROCU_OFFICER.value();
			}else if(ProcurementPlanConst.PPP_PENDING_VERIFICATION.equals(statusId)
					|| ProcurementPlanConst.PPP_PENDING_REVISION_VERIFICATION.equals(statusId)){
				roleCode = RoleCode.PLAN_VERFICATION_OFFICER.value();
			}else if(ProcurementPlanConst.PPP_PENDING_APPROVAL.equals(statusId)
					|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(statusId)
					|| ProcurementPlanConst.PPP_PENDING_REVISION_APPROVAL.equals(statusId)
					|| ProcurementPlanConst.PPE_PENDING_REVISION_APPROVAL.equals(statusId)){
				roleCode = RoleCode.PLAN_APPROVER.value();
			}else if(ProcurementPlanConst.PPP_APPROVED.equals(statusId) 
						|| ProcurementPlanConst.PPE_APPROVED.equals(statusId)){
				roleCode = RoleCode.PLAN_OFFICER.value();
				//roleCode = RoleCode.PLAN_PROCU_OFFICER.value();
			}
		}else{
			//pending submission or new
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(plan.getPlanType().getParameterCode())){
				roleCode = RoleCode.PLAN_OFFICER.value();
				//roleCode = RoleCode.PLAN_PROCU_OFFICER.value();
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(plan.getPlanType().getParameterCode())){
				roleCode = RoleCode.PLAN_OFFICER.value();
				//roleCode = RoleCode.PLAN_EXPEN_OFFICER.value();
			}
		}
		return roleCode;
	}
	
	
	
	
	
	
	
	
	
	
	
	/* --------------------------------------------------------- Plan General Related - START -----------------------------------------------------------------*/

//	/**
//	 * Generate/change plan view based selected vot type
//	 */
//	public void changeVotType(){
//		log.info("changeVotType");
//		Long votTypeId = this.getManagedBean().getSelectedPlan().getVotFundType().getParameterId();
//		if(votTypeId != null){
//			PmParameter votType = (PmParameter)votTypeMapper.get(votTypeId);
//			this.getManagedBean().getSelectedPlan().getVotFundType().setParameterCode(votType!=null? votType.getParameterCode() : null);
//		}else{
//			this.getManagedBean().getSelectedPlan().setVotFundType(new PmParameter());
//		}
//	}
//	
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
        generateSourceApprovers(getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId(), RoleCode.PLAN_APPROVER.value(), ppPlanActors); 

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
		if (this.getManagedBean().getSelectedRemarks().isEmpty() || this.getManagedBean().getSelectedRemarks().size() == 0) {
			newRemark = true;
		}
		
		// filter by current view mode
		if(!this.getManagedBean().isModeEditable() 
				&& !this.getManagedBean().isModeReviewEditable()){
			newRemark = false;
		}
	
		
		// for user to edit draft remark
		if(newRemark){
			// remark list is not null 
			if(!this.getManagedBean().getSelectedRemarks().isEmpty() && 
					compareUserNameInsensitive(this.getManagedBean().getSelectedRemarks().get(0).getUserId())){
				//if both statuses are null
				if(this.getManagedBean().getSelectedRemarks().get(0).getPpWorkflowStatus()==null 
						&& this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId()==null){
					this.getManagedBean().getSelectedRemarks().get(0).setEditable(true);
					newRemark = false;
				//if both plan status!=null && remark status == null
				}else if(this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId()!=null && 
						(this.getManagedBean().getSelectedRemarks().get(0).getPpWorkflowStatus()==null 
							|| (this.getManagedBean().getSelectedRemarks().get(0).getPpWorkflowStatus()!=null &&
							this.getManagedBean().getSelectedRemarks().get(0).getPpWorkflowStatus().getWorkflowStatusId()==null))){
						this.getManagedBean().getSelectedRemarks().get(0).setEditable(true);
						newRemark = false;
				// if remark status == plan status
				}else if(this.getManagedBean().getSelectedRemarks().get(0).getPpWorkflowStatus()!=null &&
						this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId().equals(
								this.getManagedBean().getSelectedRemarks().get(0).getPpWorkflowStatus().getWorkflowStatusId())){
					this.getManagedBean().getSelectedRemarks().get(0).setEditable(true);
					newRemark = false;
				}
			}
		}

		if(newRemark){
			PpRemark remark = new PpRemark();
			remark.setEditable(true);
			remark.setRemarkType(ProcurementPlanConst.REMARK_TYPE_REMARK);
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			remark.setUserId(getFacesScopes().getUserSessionBean().getLoginId());
			remark.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
			if(!this.getManagedBean().getSelectedPlan().isBrandNew()){
				PmRole roleCode = new PmRole();
				roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
				remark.setRoleCode(roleCode);
			}
			this.getManagedBean().getSelectedRemarks().add(0, remark);
		} 
		log.info("loadPlanRemark - End");
		
	}
	
	/**
	 * get available Plan Year options for plan details
	 * @param brandNew
	 * @return SelectItem[]
	 */
	public SelectItem[] getPlanYearOptions(boolean brandNew) {
		log.info("getPlanYearOptions");
		//TODO comment this for testing 
		if(brandNew){			
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(this.getManagedBean().getSelectedPlan().getPlanType().getParameterCode())){
				PmParameter parameter = ParameterHelper.getInstance().getParameterByTypeAndCode(ParameterConst.PLAN_TYPE_PT, ParameterConst.PLAN_TYPE_PROCUREMENT);
				return PpCommonUtil.getPlanYearOptions(getFacesScopes().getUserSessionBean().getUserId(), this.getCurrentActiveOrg().getOrgProfileId(),parameter.getParameterId());
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(this.getManagedBean().getSelectedPlan().getPlanType().getParameterCode())){
				PmParameter parameter = ParameterHelper.getInstance().getParameterByTypeAndCode(ParameterConst.PLAN_TYPE_PT, ParameterConst.PLAN_TYPE_EXPENDITURE);
//				return PpCommonUtil.getEpPlanYearOptions(getFacesScopes().getUserSessionBean().getUserId(), this.getCurrentActiveOrg().getOrgProfileId());
				return PpCommonUtil.getPlanYearOptions(getFacesScopes().getUserSessionBean().getUserId(), this.getCurrentActiveOrg().getOrgProfileId(),parameter.getParameterId());
			}else{
				SelectItem[] items = new SelectItem[1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				return items;
			}
		}else{
			
			Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
			Integer nextYear = currentYear + 1;
			SelectItem[] items = new SelectItem[3];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(currentYear.longValue(), currentYear.toString());
			items[2] = new SelectItem(nextYear.longValue(), nextYear.toString());
			return items;
		}
	}
	
	/**
	 * get available VOT Type options for plan details
	 * @param brandNew
	 * @return SelectItem[]
	 */
	public SelectItem[] getPlanVotTypeOptions(boolean brandNew) {
		log.info("getPlanVotTypeOptions");
		return getVotTypeOptions();
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
		List<PmUser> sourceList = PpCommonUtil.retrieveUserListByRole(orgProfileId, roleCode, this.getManagedBean().getVerifierModel().getTarget(),false);
		this.getManagedBean().getVerifierModel().setSource(sourceList);
	
	}
	
	/**
	 * retrieve approver source and target
	 * @param orgProfileId
	 * @param roleCode
	 * @param ppPlanActors
	 */
	private void generateSourceApprovers(Long ptjOrgProfileId, String roleCode, List<PpPlanActor>ppPlanActors){
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
		List<PmUser> sourceList = PpCommonUtil.retrieveUserListByRole(ptjOrgProfileId, roleCode, this.getManagedBean().getApproverModel().getTarget(),true);
		this.getManagedBean().getApproverModel().setSource(sourceList);
	}
	
	public String retrieveMinistry(){
		return retrieveMinistryByOrgProfileId(getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
	}
	
	public String retrievePegawaiPengawal(){
		return retrievePegawaiPengawalByOrgProfileId(getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
	}
	
	public String retrieveKumpulanPtj(){
		return retrieveJabatanByOrgProfileId(getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
	}
	/* --------------------------------------------------------- Plan General Related - END -----------------------------------------------------------------*/	
	
	
	
	
	
	
	
	/* --------------------------------------------------------- Button Submit - START -----------------------------------------------------------------*/
	
	/**
	 * Get & sync the selected plan actors from UI
	 * @param ppPlan
	 */
	private void updatePlanActors(PpPlan ppPlan){
		log.info("updatePlanActors");
		if(ppPlan.getPpPlanActors()!=null && !ppPlan.getPpPlanActors().isEmpty()){ //!ppPlan.isBrandNew() && 
			log.info("syncPlanActors");
			//Existing Plan Actors
			List <Long> existingVerifiers = new ArrayList<Long>();
			List <Long> existingApprovers = new ArrayList<Long>();
			for(PpPlanActor actor:ppPlan.getPpPlanActors()){
				if(RoleCode.PLAN_VERFICATION_OFFICER.value().equals(actor.getRoleCode().getRoleCode())){
					existingVerifiers.add(actor.getPmUser().getUserId());
				}
				if(RoleCode.PLAN_APPROVER.value().equals(actor.getRoleCode().getRoleCode())){
					existingApprovers.add(actor.getPmUser().getUserId());
				}
			}
			
			//New Plan Actors
			List <Long> newVerifiers = new ArrayList<Long>();
			List <Long> newApprovers = new ArrayList<Long>();
			
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
//							for(PmUser user:this.getManagedBean().getVerifierModel().getTarget()){ PP20150410-0045
								planActor = new PpPlanActor();
								planActor.setUserSeq(seq);
								planActor.setPmUser(newUser);
								planActor.setRoleCode(pmRole);
								ppPlan.addPpPlanActors(planActor);
								seq++;
//							}
						}
					}
					//Remove Unselected Actor
					for(PpPlanActor actor:ppPlan.getPpPlanActors()){
						if(RoleCode.PLAN_VERFICATION_OFFICER.value().equals(actor.getRoleCode().getRoleCode())){
							if(!newVerifiers.contains(actor.getPmUser().getUserId())){
								//Remove
								actor.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
							}else{
								actor.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
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
						log.info("add new approver");
						//Add
						PpPlanActor planActor = null;
						PmRole pmRole = new PmRole();
						pmRole.setRoleCode(RoleCode.PLAN_APPROVER.value());
						int seq = 1;
//						for(PmUser user:this.getManagedBean().getApproverModel().getTarget()){PP20150410-0045
							planActor = new PpPlanActor();
							planActor.setUserSeq(seq);
							planActor.setPmUser(newUser);
							planActor.setRoleCode(pmRole);
							ppPlan.addPpPlanActors(planActor);
							seq++;
//						}
					}
				}
				//Remove Unselected Actor
				for(PpPlanActor actor:ppPlan.getPpPlanActors()){
					if(RoleCode.PLAN_APPROVER.value().equals(actor.getRoleCode().getRoleCode())){
						if(!newApprovers.contains(actor.getPmUser().getUserId())){
							//Remove
							log.info("remove existing approver");
							actor.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						}else{
							actor.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
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
						|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(ppPlan.getStatus().getStatusId()))
						|| ProcurementPlanConst.PPP_PENDING_REVISION_APPROVAL.equals(ppPlan.getStatus().getStatusId())
						|| ProcurementPlanConst.PPP_PENDING_REVISION_VERIFICATION.equals(ppPlan.getStatus().getStatusId())
						|| ProcurementPlanConst.PPE_PENDING_REVISION_APPROVAL.equals(ppPlan.getStatus().getStatusId())
				){
			
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
	

	/**
	 * get remark from managedbean
	 * @param ppPlan
	 * @return PpRemark
	 */
	private PpRemark getRemark(PpPlan ppPlan){
		log.info("getRemark");
		PpRemark remark = null;
		if(this.getManagedBean().getSelectedRemarks()!=null && !this.getManagedBean().getSelectedRemarks().isEmpty()){
			PpRemark ppRemark = this.getManagedBean().getSelectedRemarks().get(0);
			if((ppRemark.isBrandNew() && !StringUtils.isBlank(ppRemark.getRemark())) 
					|| !ppRemark.isBrandNew() && ppRemark.isEditable()){
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
	
	/**
	 * get justification from managedbean
	 * @param ppPlan
	 * @return List <PpRemark>
	 */
	private List <PpRemark> getAllJustifications(PpPlan ppPlan){
		log.info("getAllJustifications");
		List <PpRemark> justifications = null;
		if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
	
			if(ppPlan.getStatus()==null || ppPlan.getStatus().getStatusId()==null
					|| (ProcurementPlanConst.PPE_PENDING_REVISION_APPROVAL.equals(ppPlan.getStatus().getStatusId())
					|| (ProcurementPlanConst.PPE_PENDING_REVISION.equals(ppPlan.getStatus().getStatusId())
					|| ProcurementPlanConst.PPE_APPROVED.equals(ppPlan.getStatus().getStatusId())
					|| ProcurementPlanConst.PPE_PENDING_SUBMISSION.equals(ppPlan.getStatus().getStatusId())
					|| ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(ppPlan.getStatus().getStatusId())))){
				log.info("getting justification");
				justifications = new ArrayList<PpRemark>();
				int itemIndex = 0;
				for(PpExpenditureItem ppExpenditureItem:ppPlan.getPpExpenditureItems()){
					if(ppExpenditureItem.getJustifications()!=null && !ppExpenditureItem.getJustifications().isEmpty()){
						log.info("justification not null");
						PpRemark justification = ppExpenditureItem.getJustifications().get(0);
						// add this for phase 2 and remove the old one
//						if(justification!=null && (justification.isBrandNew() && !StringUtils.isBlank(justification.getRemark())) 
//								|| !justification.isBrandNew() && justification.isEditable()){
//							
						if(justification!=null ){
							if(!StringUtils.isBlank(justification.getRemark())){
								justification.setItemSeq(itemIndex);
								justification.setDocId(ppExpenditureItem.getExpenditureItemId());
								justification.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
								if(justification.getRoleCode() ==null || StringUtils.isBlank(justification.getRoleCode().getRoleCode())){
									log.info("set setRemarkRole");
									PmRole roleCode = new PmRole();
									roleCode.setRoleCode(RoleCode.PLAN_OFFICER.value());
								//	roleCode.setRoleCode(RoleCode.PLAN_EXPEN_OFFICER.value());
									justification.setRoleCode(roleCode);
									justification.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
								}
							}else{
								justification.setRemark(" ");
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
	
	/**
	 * get item remark from managedbean
	 * @param ppPlan
	 * @return  List <PpRemark>
	 */
	private List <PpRemark> getAllItemRemarks(PpPlan ppPlan){
		log.info("getAllItemRemarks");

		List <PpRemark> itemRemarks =  new ArrayList<PpRemark>();
		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
			int itemIndex = 0;
			for(PpProcurementItem ppProcurementItem:ppPlan.getPpProcurementItems()){
				if(ppProcurementItem.getPpRemarks()!=null && !ppProcurementItem.getPpRemarks().isEmpty()){
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
					if(ppExpenditureItem.getPpRemarks()!=null && !ppExpenditureItem.getPpRemarks().isEmpty()){
						PpRemark itemRemark = ppExpenditureItem.getPpRemarks().get(0);
						if(itemRemark!=null && !StringUtils.isBlank(itemRemark.getRemark())){ //&& itemRemark.isBrandNew() add this for phase 2
							itemRemark.setItemSeq(itemIndex);
							itemRemark.setDocId(ppExpenditureItem.getExpenditureItemId());
							itemRemark.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
							if(itemRemark.getRoleCode() ==null || StringUtils.isBlank(itemRemark.getRoleCode().getRoleCode())){
								log.info("set setRemarkRole");
								PmRole roleCode = new PmRole();
								roleCode.setRoleCode(RoleCode.PLAN_OFFICER.value());
								//roleCode.setRoleCode(RoleCode.PLAN_EXPEN_OFFICER.value());
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

	/**
	 * plan general info validation
	 * @param action
	 * @param ppPlan
	 * @return boolean
	 */
	private boolean validationPlanSubmit(String action, PpPlan ppPlan){
		log.info("validationPlanSubmit - START");
		// basic validation 
		if(ppPlan.getPlanType().getParameterId()==null){
			throw new EpException(MessageConst.ERROR_730016);
		}
		
		if(ppPlan.getPlanYear()==null){
			throw new EpException(MessageConst.ERROR_730028);
		}
		
		if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
			boolean valid = false;
			  if(getManagedBean().getEpItemsB() != null && !getManagedBean().getEpItemsB().isEmpty()){
			   valid = true;
			  }else if(getManagedBean().getEpItemsH() != null && !getManagedBean().getEpItemsH().isEmpty()){
			   valid = true;
			  }else if(getManagedBean().getEpItemsT() != null && !getManagedBean().getEpItemsT().isEmpty()){
				   valid = true;
			  }else if(getManagedBean().getEpItemsP() != null && !getManagedBean().getEpItemsP().isEmpty()){
				   valid = true;
			  }else if(getManagedBean().getEpItemsS() != null && !getManagedBean().getEpItemsS().isEmpty()){
				   valid = true;
			  }
			  
			  if(!valid){
			   throw new EpException(MessageConst.ERROR_730029);
			  }
    }else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
		boolean valid = false;
		  if(getManagedBean().getSelectedPlan().getPpProcurementItems() != null && !getManagedBean().getSelectedPlan().getPpProcurementItems().isEmpty()){
		   valid = true;
		  }
		  
		  if(!valid){
		   throw new EpException(MessageConst.ERROR_730029);
		  }
}
		
		if(ppPlan.isBrandNew() && BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)){ 
            if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
                    if(this.getManagedBean().getVerifierModel().getTarget()==null || this.getManagedBean().getVerifierModel().getTarget().isEmpty()){
                            throw new EpException(MessageConst.ERROR_730017);
                    }
                    if(this.getManagedBean().getApproverModel().getTarget()==null || this.getManagedBean().getApproverModel().getTarget().isEmpty()){
                            throw new EpException(MessageConst.ERROR_730018);
                    }
            }else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
                    if(this.getManagedBean().getApproverModel().getTarget()==null || this.getManagedBean().getApproverModel().getTarget().isEmpty()){
                            throw new EpException(MessageConst.ERROR_730018);
                    }
            }
		}
		
		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
			validatePpItem();
			// PP20150108-0005
			if(!ppPlan.isBrandNew() 
					&& !(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(action) && ProcurementPlanConst.PPP_PENDING_SUBMISSION.equals(this.getManagedBean().getPpWorkflowStatus().getPmStatus().getStatusId()))
					&& !(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(action) && ProcurementPlanConst.PPE_PENDING_SUBMISSION.equals(this.getManagedBean().getPpWorkflowStatus().getPmStatus().getStatusId()))){
				if(this.getManagedBean().getVerifierModel().getTarget()==null || this.getManagedBean().getVerifierModel().getTarget().isEmpty()){
					throw new EpException(MessageConst.ERROR_730017);
				}
				if(this.getManagedBean().getApproverModel().getTarget()==null || this.getManagedBean().getApproverModel().getTarget().isEmpty()){
					throw new EpException(MessageConst.ERROR_730018);
				}
			}
		
		}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
//			if(ppPlan.getVotFundType().getParameterId()==null){
//				throw new EpException(MessageConst.ERROR_730019);
//			}
			validateAllEpItems();
			// PP20150108-0005
			if(!ppPlan.isBrandNew() 
					&& !(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(action) && ProcurementPlanConst.PPP_PENDING_SUBMISSION.equals(this.getManagedBean().getPpWorkflowStatus().getPmStatus().getStatusId()))
					&& !(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(action) && ProcurementPlanConst.PPE_PENDING_SUBMISSION.equals(this.getManagedBean().getPpWorkflowStatus().getPmStatus().getStatusId()))){
				if(this.getManagedBean().getApproverModel().getTarget()==null || this.getManagedBean().getApproverModel().getTarget().isEmpty()){
					throw new EpException(MessageConst.ERROR_730018);
				}
			}
		}

		
		if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action) 
				||BpmOutcomeConst.BPM_VERIFY.equalsIgnoreCase(action) ){ 
			// no rejected item checked
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
					if(ppItem.isRejected()){
						throw new EpException(MessageConst.ERROR_730020);
					}
				}
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
					if(epItem.isRejected()){
						throw new EpException(MessageConst.ERROR_730021);
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
									throw new EpException(MessageConst.ERROR_730022);
								}
							}
						}else{
							throw new EpException(MessageConst.ERROR_730022);
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
									throw new EpException(MessageConst.ERROR_730022);
								}
							}
						}else{
							throw new EpException(MessageConst.ERROR_730022);
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
	
	/**
	 * validate if remark is required
	 */
	private void remarkIsRequired(){
		log.info("remarkIsRequired");
		if(this.getManagedBean().getSelectedRemarks()!=null && !this.getManagedBean().getSelectedRemarks().isEmpty()){
			if(StringUtils.isBlank(this.getManagedBean().getSelectedRemarks().get(0).getRemark())){
				throw new EpException(MessageConst.ERROR_730023);
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

		}
		
		existingPlans = PpCommonUtil.retrievePlanBySearchBean(searchBean);
		if(existingPlans!=null && !existingPlans.isEmpty()){
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(planType)){
				throw new EpException(MessageConst.ERROR_730024);
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(planType)){
				throw new EpException(MessageConst.ERROR_730025);
			}
			
		}
	}
	
	private void getAllEpItem(PpPlan plan){
		log.info("getAllEpItem");
		addEpItemToPlan(plan, this.getManagedBean().getEpItemsB());
		addEpItemToPlan(plan, this.getManagedBean().getEpItemsT());
		addEpItemToPlan(plan, this.getManagedBean().getEpItemsP());
		addEpItemToPlan(plan, this.getManagedBean().getEpItemsS());
		addEpItemToPlan(plan, this.getManagedBean().getEpItemsH());

	}
	
	private void addEpItemToPlan(PpPlan plan, List<PpExpenditureItem> epItems){
		log.info("addEpItemToPlan");
		if(plan.getPpExpenditureItems()==null){
			plan.setPpExpenditureItems(new ArrayList<PpExpenditureItem>());
		}
		if(epItems!=null && !epItems.isEmpty()){
			for(PpExpenditureItem epItem:epItems){
				if(epItem.isBrandNew() && !epItem.isSelected()){
					System.out.println("add ep item");
					plan.getPpExpenditureItems().add(epItem);
					epItem.setSelected(true);
				}
//				else{
//					for (int i=0; i <plan.getPpExpenditureItems().size(); i++){
//						if(plan.getPpExpenditureItems().get(i).getExpenditureItemId()
//								.equals(epItem.getExpenditureItemId())){
//							plan.getPpExpenditureItems().set(i, epItem);
//						}
//					}
//				}
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
		boolean brandNew = false;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			ppPlan = this.getManagedBean().getSelectedPlan();
			brandNew = ppPlan.isBrandNew();
			
			//Validation
			validationPlanSubmit(action, ppPlan);
			if(ppPlan.isBrandNew()){
				//comment this for testing purpose only
				validationPlanAvailability(ppPlan);  
			}
			
			//Get All EP if PlanType == E 
			if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				getAllEpItem(ppPlan);
			}
			
			
			//Get All Values
			updatePlanActors(ppPlan);
			
			//Get Remark at Plan Level
			remark = getRemark(ppPlan);
			
			//Get Remark at Item Level
			itemRemarks = getAllItemRemarks(ppPlan); 
			
			//Get Justification 
			justifications = getAllJustifications(ppPlan);
			
			//Current not Pending Submission/Null and it is SAVE/REVISE
			if(!brandNew 
				&& !(ppPlan.getCurrentStatus().getWorkflowStatusId()!=null 
					&& ProcurementPlanConst.PPE_PENDING_SUBMISSION.equals(ppPlan.getCurrentStatus().getWorkflowStatusId()))
				&& !(ppPlan.getCurrentStatus().getWorkflowStatusId()!=null 
					&& ProcurementPlanConst.PPP_PENDING_SUBMISSION.equals(ppPlan.getCurrentStatus().getWorkflowStatusId()))){
				
				if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action) 
						|| BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(action)){
					rejectReasons = getAllRejectionReasons(ppPlan);
					if(rejectReasons!=null && !rejectReasons.isEmpty()){
						reqDTO.setRejectReasonList(rejectReasons);
					}
				}
			}
			
//			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
//            reqDTO.setUserRole(getOneRoleByStatus(ppPlan));
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setPpRemark(remark);
			reqDTO.setPpRemarks(itemRemarks);
			reqDTO.setJustifications(justifications);
			reqDTO.setPpPlan(ppPlan);
			reqDTO.setOutcome(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setUserRole(getUserRoleValue());
			if(!ppPlan.isBrandNew()){
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
			}	
			reqDTO.setDocTitle(PpCommonUtil.getDocTitle(ppPlan));
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.savePlan(reqDTO);
			docType = retrievePlanDocType(ppPlan);
					
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully"); 
				if(this.getManagedBean().getTaskId()!=null){
					loadPlanDetails(resDTO.getPlan().getPlanId(), false);
				}else{
					loadPlanDetails(resDTO.getPlan().getPlanId(), true);
				}
				
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
				
				
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) 
						|| (StringUtils.isBlank(this.getManagedBean().getTaskId()) 
								&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE))) { //PP20150108-0007
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			reqSO = null;
		}
		log.info("savePlan - END");
		return null;
	}
	
	/* --------------------------------------------------------- Button Submit - END -----------------------------------------------------------------*/
	

	
	
	
	
	
	/* --------------------------------------------------------- Item Level - START -----------------------------------------------------------------*/
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
		this.getManagedBean().setSelectedItemRemarks(null);
	}
	
	/**
	 * Initial or load reject reasons
	 * @param ppRejectReasons
	 */
	public void loadRejectReasons(List<PpRejectReason> ppRejectReasons) {
		log.info("loadRejectReasons - Start");
		this.getManagedBean().setConfirmReason(false);
		this.getManagedBean().setSelectedReasonDesc("");
		this.getManagedBean().setSelectedRejectReasons(null);
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
		if (ppRejectReasons.isEmpty() || ppRejectReasons.size() == 0) {
			newReason = true;
		}
		
		/* enable for phase 2
		// for user to edit draft reject reason
		if(newReason){
			if(!ppRejectReasons.isEmpty() && compareUserNameInsensitive(ppRejectReasons.get(0).getUserId())){

				//if both statuses are null
				if(ppRejectReasons.get(0).getPpWorkflowStatus()==null 
						&& this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId()==null){
					ppRejectReasons.get(0).setEditable(true);
					newReason = false;
				// if remark status == plan status
				}else if(ppRejectReasons.get(0).getPpWorkflowStatus()!=null &&
						this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId().equals(
								ppRejectReasons.get(0).getPpWorkflowStatus().getWorkflowStatusId())){
					ppRejectReasons.get(0).setEditable(true);
					newReason = false;
				}
			}
		} */
		
		
		if(newReason && this.getManagedBean().isModeReviewEditable()){
			log.info("new reason added");
			PpRejectReason reason = new PpRejectReason();
			reason.setEditable(true);
			reason.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			reason.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			reason.setChangedBy(reason.getCreatedBy());
			reason.setChangedDate(reason.getCreatedDate());
			reason.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			reason.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
			PmRole roleCode = new PmRole();
			roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
			reason.setRoleCode(roleCode);
			ppRejectReasons.add(0, reason);
		}
	
		this.getManagedBean().setSelectedRejectReasons(ppRejectReasons);
		this.getManagedBean().setSelectedReasonDesc(ppRejectReasons.isEmpty() ? "": ppRejectReasons.get(0).getReasonDesc());
		
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
		this.getManagedBean().setSelectedRejectReasons(null);
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
		RequestContext.getCurrentInstance().execute("dlItemRemarkWv.hide()");
	}
	

	/*  Expenditure Related ------------------- START ---------------------------*/


	/**
	 * Add new item to expenditure list
	 * @param paramCode
	 */
	public void addEpItemDet(String paramCode){
		log.info("addEpItemDet");

		validateEpItem(paramCode); // disabled this if no validation required at this stage
		PpExpenditureItem epItem = new PpExpenditureItem();
		epItem.setPmGlAccount(new PmGlAccount());
		epItem.setVotFundType(new PmParameter());
		epItem.setEditable(true);
		epItem.setPpPlan(this.getManagedBean().getSelectedPlan());
		
		//B
		if(ParameterConst.VOT_MENGURUS.equals(paramCode)){
			epItem.getVotFundType().setParameterCode(ParameterConst.VOT_MENGURUS);
			this.getManagedBean().getEpItemsB().add(epItem);
		}else if(ParameterConst.VOT_TANGGUNGAN.equals(paramCode)){
		//T
			epItem.getVotFundType().setParameterCode(ParameterConst.VOT_TANGGUNGAN);
			this.getManagedBean().getEpItemsT().add(epItem);
		}else if(ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)){
		//P
			epItem.getVotFundType().setParameterCode(ParameterConst.VOT_PEMBANGUNAN);
			this.getManagedBean().getEpItemsP().add(epItem);
		}else if(ParameterConst.VOT_SETIA.equals(paramCode)){
		//S
			epItem.getVotFundType().setParameterCode(ParameterConst.VOT_SETIA);
			this.getManagedBean().getEpItemsS().add(epItem);
		}else if(ParameterConst.VOT_AMANAH.equals(paramCode)){
		//H
			epItem.getVotFundType().setParameterCode(ParameterConst.VOT_AMANAH);
			this.getManagedBean().getEpItemsH().add(epItem);
		}
	}
	
	/**
	 * validate all EP Items
	 */
	private void validateAllEpItems(){
		log.info("validateAllEpItems");
		boolean isEmpty = true;
		//B
		if(this.getManagedBean().getEpItemsB()!=null && this.getManagedBean().getEpItemsB().isEmpty()){
			isEmpty = false;
		}
		//T
		if(this.getManagedBean().getEpItemsT()!=null && this.getManagedBean().getEpItemsT().isEmpty()){
			isEmpty = false;
		}
		//P
		if(this.getManagedBean().getEpItemsP()!=null && this.getManagedBean().getEpItemsP().isEmpty()){
			isEmpty = false;
		}
		//S
		if(this.getManagedBean().getEpItemsS()!=null && this.getManagedBean().getEpItemsS().isEmpty()){
			isEmpty = false;
		}
		//H
		if(this.getManagedBean().getEpItemsH()!=null && this.getManagedBean().getEpItemsH().isEmpty()){
			isEmpty = false;
		}
		
		if(isEmpty){
			//TODO SAM add error msg exception not null
		}
		
		validateEpItem(ParameterConst.VOT_MENGURUS);
		validateEpItem(ParameterConst.VOT_TANGGUNGAN);
		validateEpItem(ParameterConst.VOT_PEMBANGUNAN);
		validateEpItem(ParameterConst.VOT_SETIA);
		validateEpItem(ParameterConst.VOT_AMANAH);
	}
	
	/**
	 * validate Ep Item
	 * @param paramCode
	 */
	private void validateEpItem(String paramCode){
		log.info("validateEpItem");
		
		List<PpExpenditureItem> epItems =  new ArrayList<PpExpenditureItem>();
		//B
		if(ParameterConst.VOT_MENGURUS.equals(paramCode)){
			epItems = this.getManagedBean().getEpItemsB();
		}else if(ParameterConst.VOT_TANGGUNGAN.equals(paramCode)){
		//T
			epItems = this.getManagedBean().getEpItemsT();
		}else if(ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)){
		//P
			epItems = this.getManagedBean().getEpItemsP();
		}else if(ParameterConst.VOT_SETIA.equals(paramCode)){
		//S
			epItems = this.getManagedBean().getEpItemsS();
		}else if(ParameterConst.VOT_AMANAH.equals(paramCode)){
		//H
			epItems = this.getManagedBean().getEpItemsH();
		}	
		if(!epItems.isEmpty()){
			List <Long> soIds = new ArrayList<Long>();
			for(PpExpenditureItem epItem: epItems){
				if(DbValueConst.RECORD_STATUS_ACTIVE ==epItem.getRecordStatusFlg()){
					if(epItem.getPmGlAccount().getGlAccountId()==null){
						throw new EpException(MessageConst.ERROR_730003);
					}
					if(epItem.getBudgetAmt()==null || epItem.getBudgetAmt().intValue()<=0){
						throw new EpException(MessageConst.ERROR_730004);
					}
					if(epItem.getPlanAmt()==null || epItem.getPlanAmt().intValue()<=0){
						throw new EpException(MessageConst.ERROR_730005);
					}
					if(epItem.getNonEpAmt()==null || epItem.getNonEpAmt().intValue()<0){
						throw new EpException(MessageConst.ERROR_730006, new String[]{epItem.getPmGlAccount().getGlAccCode()});
					}
					if(epItem.getNonEpAmt().intValue()>0){
						if(epItem.getJustifications()==null 
								|| (epItem.getJustifications()!=null && epItem.getJustifications().isEmpty())
								|| (epItem.getJustifications()!=null 
									&& epItem.getJustifications().isEmpty() 
									&& epItem.getJustifications().get(0)==null)  
								|| (epItem.getJustifications()!=null &&(
									 epItem.getJustifications().isEmpty() 
									|| epItem.getJustifications().get(0)==null
									|| StringUtils.isBlank(epItem.getJustifications().get(0).getRemark())))){
							throw new EpException(MessageConst.ERROR_730007);
						}
					}
					soIds.add(epItem.getPmGlAccount().getGlAccountId());
				}
			}
			if(!soIds.isEmpty()){
				if(!validationUniqueIds(soIds)){
					throw new EpException(MessageConst.ERROR_730008);
				}
			}
			
		}
	}
	
	/**
	 * validate unique account code
	 * @param ids
	 * @return boolean
	 */
	private boolean validationUniqueIds(List<Long> ids){
		log.info("validationUniqueIds");
		boolean result = true;
		if(!ids.isEmpty()){
			Set<Long> idSet = new HashSet<Long>(ids);
			if(idSet.size()!=ids.size()){
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * change account code
	 * @param paramCode
	 */
	public void changeAccountCode(String paramCode){
		log.info("changeAccountCode");
		List <PpExpenditureItem> epItems = new ArrayList<PpExpenditureItem>();
		//B
		if(ParameterConst.VOT_MENGURUS.equals(paramCode)){
			epItems = this.getManagedBean().getEpItemsB();
		}else if(ParameterConst.VOT_TANGGUNGAN.equals(paramCode)){
		//T
			epItems = this.getManagedBean().getEpItemsT();
		}else if(ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)){
		//P
			epItems = this.getManagedBean().getEpItemsP();
		}else if(ParameterConst.VOT_SETIA.equals(paramCode)){
		//S
			epItems = this.getManagedBean().getEpItemsS();
		}else if(ParameterConst.VOT_AMANAH.equals(paramCode)){
		//H
			epItems = this.getManagedBean().getEpItemsH();
		}
				
		if(!epItems.isEmpty()){
			List <Long> soIds = new ArrayList<Long>();
			for(PpExpenditureItem epItem: epItems){
				soIds.add(epItem.getPmGlAccount().getGlAccountId());
			}
			if(!soIds.isEmpty()){
				if(!validationUniqueIds(soIds)){
					throw new EpException(MessageConst.ERROR_730008);
				}
			}
		}
	}

	/**
	 * Delete selected expenditure items
	 * @param paramCode
	 */
	public void deleteEpItemDet(String paramCode){
		log.info("deleteEpItemDet");
		int count = 0;
		List<PpExpenditureItem> deletedItems = new ArrayList<PpExpenditureItem>();
		List <PpExpenditureItem> epItems = new ArrayList<PpExpenditureItem>();
		//B
		if(ParameterConst.VOT_MENGURUS.equals(paramCode)){
			epItems = this.getManagedBean().getEpItemsB();
		}else if(ParameterConst.VOT_TANGGUNGAN.equals(paramCode)){
		//T
			epItems = this.getManagedBean().getEpItemsT();
		}else if(ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)){
		//P
			epItems = this.getManagedBean().getEpItemsP();
		}else if(ParameterConst.VOT_SETIA.equals(paramCode)){
		//S
			epItems = this.getManagedBean().getEpItemsS();
		}else if(ParameterConst.VOT_AMANAH.equals(paramCode)){
		//H
			epItems = this.getManagedBean().getEpItemsH();
		}
		//add deleted item to list
		for(PpExpenditureItem expenditureItem: epItems){
			if(expenditureItem.isBrandNew() && expenditureItem.isDeleted()){
				deletedItems.add(expenditureItem);
				count++;
			}
		}
		if(count==0){
			throw new EpException(MessageConst.ERROR_730009);
		}
		
		//remove deleted item from list
		for(PpExpenditureItem expenditureItem: deletedItems){
			epItems.remove(expenditureItem);
		}
		
	}
	
	/**
	 * Calculate non allocation budget & update grand total
	 * @param item
	 * @param paramCode
	 */
	public void calculateNonAllocationBudget(PpExpenditureItem item, String paramCode){
		log.info("calculateNonAllocationBudget");
		item.setNonEpAmt(PpCommonUtil.substractBigDecimalNullable(item.getBudgetAmt(), item.getPlanAmt()));
		calculateEpGrandTotal(paramCode);
		
	}
	

	/**
	 * Calculate expenditure items grand total
	 * @param paramCode
	 * @epItems 
	 */
	private void calculateEpGrandTotal(String paramCode){
		log.info("calculateEpGrandTotal");
		BigDecimal epTotalBudget = new BigDecimal(0);
		BigDecimal epTotalNonEpAlloc = new BigDecimal(0);
		BigDecimal epTotalPlan = new BigDecimal(0);
		List<PpExpenditureItem> epItems =  new ArrayList<PpExpenditureItem>();
		//B
		if(ParameterConst.VOT_MENGURUS.equals(paramCode)){
			epItems = this.getManagedBean().getEpItemsB();
		}else if(ParameterConst.VOT_TANGGUNGAN.equals(paramCode)){
		//T
			epItems = this.getManagedBean().getEpItemsT();
		}else if(ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)){
		//P
			epItems = this.getManagedBean().getEpItemsP();
		}else if(ParameterConst.VOT_SETIA.equals(paramCode)){
		//S
			epItems = this.getManagedBean().getEpItemsS();
		}else if(ParameterConst.VOT_AMANAH.equals(paramCode)){
		//H
			epItems = this.getManagedBean().getEpItemsH();
		}
		for(PpExpenditureItem epItem: epItems){
			epTotalBudget = PpCommonUtil.addBigDecimalNullable(epTotalBudget, epItem.getBudgetAmt());
			epTotalNonEpAlloc = PpCommonUtil.addBigDecimalNullable(epTotalNonEpAlloc, epItem.getNonEpAmt());
			epTotalPlan = PpCommonUtil.addBigDecimalNullable(epTotalPlan, epItem.getPlanAmt());
		}
		//B
		if(ParameterConst.VOT_MENGURUS.equals(paramCode)){
			this.getManagedBean().setEpTotalBudgetB(epTotalBudget);
			this.getManagedBean().setEpTotalNonEpAllocB(epTotalNonEpAlloc);
			this.getManagedBean().setEpTotalPlanB(epTotalPlan);
		}else if(ParameterConst.VOT_TANGGUNGAN.equals(paramCode)){
		//T
			this.getManagedBean().setEpTotalBudgetT(epTotalBudget);
			this.getManagedBean().setEpTotalNonEpAllocT(epTotalNonEpAlloc);
			this.getManagedBean().setEpTotalPlanT(epTotalPlan);
		}else if(ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)){
		//P
			this.getManagedBean().setEpTotalBudgetP(epTotalBudget);
			this.getManagedBean().setEpTotalNonEpAllocP(epTotalNonEpAlloc);
			this.getManagedBean().setEpTotalPlanP(epTotalPlan);
		}else if(ParameterConst.VOT_SETIA.equals(paramCode)){
		//S
			this.getManagedBean().setEpTotalBudgetS(epTotalBudget);
			this.getManagedBean().setEpTotalNonEpAllocS(epTotalNonEpAlloc);
			this.getManagedBean().setEpTotalPlanS(epTotalPlan);
		}else if(ParameterConst.VOT_AMANAH.equals(paramCode)){
		//H
			this.getManagedBean().setEpTotalBudgetH(epTotalBudget);
			this.getManagedBean().setEpTotalNonEpAllocH(epTotalNonEpAlloc);
			this.getManagedBean().setEpTotalPlanH(epTotalPlan);
		}

		
	}
	

	/**
	 * retrieve ep item remark
	 * @param epItem
	 */
	public void retrieveEpItemRemark(PpExpenditureItem epItem){
		log.info("retrieveEpItemRemark");
		if(!epItem.isBrandNew()){
			if(epItem.getPpRemarks()==null){
				epItem.setPpRemarks(new ArrayList<PpRemark>());
//				epItem.setPpRemarks(PpCommonUtil.retrieveRemark(epItem.getExpenditureItemId(), ParameterConst.DOCTYPE_PLAN_EXPENDITURE));
			}
		}
		loadEpItemRemarks(epItem);
	}
	/**
	 * Initial or load item remarks
	 * @param epItem
	 */
	public void loadEpItemRemarks(PpExpenditureItem epItem) {
		log.info("loadEpItemRemarks - Start");
		this.getManagedBean().setConfirmRemark(false);
		this.getManagedBean().setSelectedItemRemarkDesc("");
		this.getManagedBean().setSelectedItemRemarks(null);
		boolean newRemark = false;
		
		
		if(epItem.getPpRemarks()==null){
			epItem.setPpRemarks(new ArrayList<PpRemark>());
		}
		/* enable for phase 2
		//if first remark is not new
		if (!epItem.getPpRemarks().isEmpty()){
			if(!epItem.getPpRemarks().get(0).isBrandNew()){
				newRemark = true;
			}
		}*/

		// if reason is empty
		if (epItem.getPpRemarks().isEmpty() || epItem.getPpRemarks().size() == 0) {
			newRemark = true;
		}
		
		// filter by current view mode	
		if((!this.getManagedBean().isModeEditable() 
				&& !this.getManagedBean().getSelectedPlan().isUpdatable())){
			newRemark = false;
		}
		
		if(!epItem.isEditable()){
			newRemark = false;
		}
		
	
		/* enable for phase 2
		// for user to edit draft remark
		if(newRemark){
			if(!epItem.getPpRemarks().isEmpty() && compareUserNameInsensitive(epItem.getPpRemarks().get(0).getUserId())){
				//if both statuses are null
				if(epItem.getPpRemarks().get(0).getPpWorkflowStatus()==null 
						&& this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId()==null){
					epItem.getPpRemarks().get(0).setEditable(true);
					newRemark = false;
				//if both plan status!=null && remark status == null
				}else if(this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId()!=null && 
						(epItem.getPpRemarks().get(0).getPpWorkflowStatus()==null 
							|| (epItem.getPpRemarks().get(0).getPpWorkflowStatus()!=null &&
									epItem.getPpRemarks().get(0).getPpWorkflowStatus().getWorkflowStatusId()==null))){
				// if remark status == plan status
				}else if(epItem.getPpRemarks().get(0).getPpWorkflowStatus()!=null &&
						this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId().equals(
								epItem.getPpRemarks().get(0).getPpWorkflowStatus().getWorkflowStatusId())){
					epItem.getPpRemarks().get(0).setEditable(true);
					newRemark = false;
				}
			}
		} */
		
		if(newRemark){
			log.info("new remark added");
			PpRemark remark = new PpRemark();
			remark.setEditable(true);
			remark.setRemarkType(ProcurementPlanConst.REMARK_TYPE_REMARK);
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			remark.setUserId(getFacesScopes().getUserSessionBean().getLoginId());
			remark.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
			PmRole roleCode = new PmRole();
			roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
			remark.setRoleCode(roleCode);
			epItem.getPpRemarks().add(0, remark);

		}
	
		this.getManagedBean().setSelectedItemRemarks(epItem.getPpRemarks());
		this.getManagedBean().setSelectedItemRemarkDesc(epItem.getPpRemarks().isEmpty() ? "": epItem.getPpRemarks().get(0).getRemark());
		
		
		log.info("loadEpItemRemarks - End");
		
	}
	
	/**
	 * retrieve ep's justification
	 * @param epItem
	 */
	public void retrieveJustification(PpExpenditureItem epItem){
		log.info("retrieveJustification");
		if(!epItem.isBrandNew()){
			if(epItem.getJustifications()==null || epItem.getJustifications().isEmpty()){
				epItem.setJustifications(new ArrayList<PpRemark>());
//				epItem.setJustifications(PpCommonUtil.retrieveJustification(epItem.getExpenditureItemId(), ParameterConst.DOCTYPE_PLAN_EXPENDITURE));
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
		this.getManagedBean().setSelectedJustifications(null);
		this.getManagedBean().setSelectedJustificationDesc("");
		boolean newRemark = false;
		
		
		if(epItem.getJustifications()==null){
			epItem.setJustifications(new ArrayList<PpRemark>());
		}
		/* enable for phase 2
		//if first remark is not new
		if (!epItem.getJustifications().isEmpty()){
			if(!epItem.getJustifications().get(0).isBrandNew()){
				newRemark = true;
			}
		}*/

		// if reason is empty
		if (epItem.getJustifications().isEmpty() || epItem.getJustifications().size() == 0) {
			newRemark = true;
		}
		
		// filter by current view mode	
		if(!this.getManagedBean().isModeEditable() || !epItem.isEditable()){
			newRemark = false;
		}
	
		
		/* enable for phase 2
		// for user to edit draft remark
		if(newRemark){
			if(!epItem.getJustifications().isEmpty() && compareUserNameInsensitive(epItem.getJustifications().get(0).getUserId())){
				//if both statuses are null
				if(epItem.getJustifications().get(0).getPpWorkflowStatus()==null 
						&& this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId()==null){
					epItem.getJustifications().get(0).setEditable(true);
					newRemark = false;
				//if both plan status!=null && justification status == null
				}else if(this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId()!=null && 
						(epItem.getJustifications().get(0).getPpWorkflowStatus()==null 
							|| (epItem.getJustifications().get(0).getPpWorkflowStatus()!=null &&
									epItem.getJustifications().get(0).getPpWorkflowStatus().getWorkflowStatusId()==null))){
				// if remark status == plan status
				}else if(epItem.getJustifications().get(0).getPpWorkflowStatus()!=null &&
						this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId().equals(
								epItem.getJustifications().get(0).getPpWorkflowStatus().getWorkflowStatusId())){
					epItem.getJustifications().get(0).setEditable(true);
					newRemark = false;
				}
			}
		} */
		
		if(newRemark){
			log.info("new justification added");
			PpRemark remark = new PpRemark();
			remark.setEditable(true);
			remark.setRemarkType(ProcurementPlanConst.REMARK_TYPE_JUSTIFICATION);
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			remark.setUserId(getFacesScopes().getUserSessionBean().getLoginId());
			remark.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
			PmRole roleCode = new PmRole();
			roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
			remark.setRoleCode(roleCode);
			epItem.getJustifications().add(0, remark);

		}
	
		this.getManagedBean().setSelectedJustifications(epItem.getJustifications());
		this.getManagedBean().setSelectedJustificationDesc(epItem.getJustifications().isEmpty() ? "": epItem.getJustifications().get(0).getRemark());
		
		
		
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
		this.getManagedBean().setSelectedJustifications(null);
	}
	
	/**
	 * Handle justification confirm button for dialog
	 */
	public void confirmJustificationCloseDialog(){
		log.info("confirmJustificationCloseDialog");
//		PpRemark ppRemark = getManagedBean().getSelectedJustifications().get(0);
//		if(getManagedBean().getSelectedPlan() != null 
//				&& getManagedBean().getSelectedPlan().getPpExpenditureItems() != null
//				&& ParameterConst.DOCTYPE_PLAN_EXPENDITURE.equals(ppRemark.getDocType()) 
//				&& StringUtils.isBlank(ppRemark.getRemark())){
//			for(PpExpenditureItem epItem:getManagedBean().getSelectedPlan().getPpExpenditureItems()){
//				if(epItem.getExpenditureItemId().equals(ppRemark.getDocId())){
//					epItem.getJustifications().clear();
//				
//			}
//			
//			}
//		}
		this.getManagedBean().setConfirmJustification(true);
		RequestContext.getCurrentInstance().execute("dlItemJustificationWv.hide()");
	}
	
	public void retrieveEpRejectReasons(PpExpenditureItem epItem){
		log.info("retrieveEpRejectReasons");
		if(epItem.getPpRejectReasons()==null || epItem.getPpRejectReasons().isEmpty()){
			epItem.setPpRejectReasons(new ArrayList<PpRejectReason>());
//			epItem.setPpRejectReasons(PpCommonUtil.retrieveRejectReason(epItem.getExpenditureItemId(), ParameterConst.DOCTYPE_PLAN_EXPENDITURE));
		}
		loadRejectReasons(epItem.getPpRejectReasons());
	}
	
	/* Expenditure Related ------------------- END ---------------------------*/
	
	
	
	
	
	/* Procurement Related ------------------- START ---------------------------*/
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
		log.info("addPpItemDet");
		if(this.getManagedBean().getSelectedPlan().getPlanYear()==null){
			throw new EpException(MessageConst.ERROR_730028);
		}
		if(this.getManagedBean().getSelectedPlan().getPpProcurementItems()==null){
			this.getManagedBean().getSelectedPlan().setPpProcurementItems(new ArrayList<PpProcurementItem>());
		}
		validatePpItem(); // disabled this if no validation required at this stage
		PpProcurementItem ppItem = new PpProcurementItem();
		ppItem.setProcurementCat(new PmParameter());
		ppItem.setProcurementType(new PmParameter());
		ppItem.setEditable(true);
		if(Calendar.getInstance().get(Calendar.YEAR) == this.getManagedBean().getSelectedPlan().getPlanYear()){ 
			//current year
			ppItem.setInvitationDate(new Date());
		}else{ 
			System.out.println("next year ");
			//next year
			int nextYear = Calendar.getInstance().get(Calendar.YEAR);
			Calendar date = new GregorianCalendar((nextYear+1), Calendar.JANUARY, 1);
			
			ppItem.setInvitationDate(date.getTime());
		}
		this.getManagedBean().getSelectedPlan().addPpProcurementItems(ppItem);
		
	}
	/**
	 * generate min date 
	 * @return String (date in string)
	 */
	public String getMinDate(){
		log.info("getMinDate");
		String dateStr = "";
		Date today = new Date();
		dateStr = DateUtils.dateToString("dd/MM/yyyy", today);
		return dateStr;
	}
	
	/**
	 * validation of pp item
	 */
	private void validatePpItem(){
		log.info("validatePpItem");
		if(!this.getManagedBean().getSelectedPlan().getPpProcurementItems().isEmpty()){
//			int size = this.getManagedBean().getSelectedPlan().getPpProcurementItems().size();
//			PpProcurementItem ppItem = this.getManagedBean().getSelectedPlan().getPpProcurementItems().get(size-1);
			for(PpProcurementItem ppItem: this.getManagedBean().getSelectedPlan().getPpProcurementItems()){
				if(DbValueConst.RECORD_STATUS_ACTIVE ==ppItem.getRecordStatusFlg()){
					if(StringUtils.isBlank(ppItem.getProcurementTitle())){
						throw new EpException(MessageConst.ERROR_730010);
					}
					if(StringUtils.isBlank(ppItem.getProcurementDesc())){
						throw new EpException(MessageConst.ERROR_730030);
					}
					if(ppItem.getProcurementCat().getParameterId()==null){
						throw new EpException(MessageConst.ERROR_730011);
					}
					if(ppItem.getProcurementType().getParameterId()==null){
						throw new EpException(MessageConst.ERROR_730013);
					}
					if(ppItem.getInvitationDate()==null){
						throw new EpException(MessageConst.ERROR_730012);
					}
					if(ppItem.getEstimatedAmt()==null || ppItem.getEstimatedAmt().intValue()<=0){
						throw new EpException(MessageConst.ERROR_730014);
					}
				}
			}
		}else{
			// SAM TODO error msg at least one item is required
		}
	}
	/**
	 * Delete selected procurement items
	 */
	public void deletePpItemDet(){
		log.info("deletePpItemDet");
		int count = 0;
		if(this.getManagedBean().getSelectedPlan().getPpProcurementItems()==null){
			this.getManagedBean().getSelectedPlan().setPpProcurementItems(new ArrayList<PpProcurementItem>());
		}
		if(this.getManagedBean().getDeletePpItems()==null){
			this.getManagedBean().setDeletePpItems(new ArrayList<PpProcurementItem>());
		}
		//add deleted item to list
		for(PpProcurementItem procurementItem: this.getManagedBean().getSelectedPlan().getPpProcurementItems()){
			if(procurementItem.isBrandNew() && procurementItem.isDeleted()){
				this.getManagedBean().getDeletePpItems().add(procurementItem);
				count++;
			}
		}
		
		if(count==0){
			throw new EpException(MessageConst.ERROR_730015);
		}
		
		//remove deleted item from list
		for(PpProcurementItem procurementItem: this.getManagedBean().getDeletePpItems()){
			this.getManagedBean().getSelectedPlan().getPpProcurementItems().remove(procurementItem);
		}
		
		
//		if(this.getManagedBean().getDeletePpItems()!=null && !this.getManagedBean().getDeletePpItems().isEmpty()){
//			if(this.getManagedBean().getSelectedPlan().getPpProcurementItems()==null){
//				this.getManagedBean().getSelectedPlan().setPpProcurementItems(new ArrayList<PpProcurementItem>());
//			}
//			for(PpProcurementItem procurementItem: this.getManagedBean().getDeletePpItems()){
//				if(procurementItem.isBrandNew()){
//					this.getManagedBean().getSelectedPlan().getPpProcurementItems().remove(procurementItem);
//				}
//			}
//			this.getManagedBean().getDeletePpItems().clear();
//			calculatePpGrandTotal();
//		}else{
//			throw new EpException(MessageConst.ERROR_730015);
//		}
	}
	
	
	
	/**
	 * retrieve pp reject reason
	 * @param ppItem
	 */
	public void retrievePpRejectReasons(PpProcurementItem ppItem){
		log.info("retrievePpRejectReasons");
		if(ppItem.getPpRejectReasons()==null || ppItem.getPpRejectReasons().isEmpty()){
			ppItem.setPpRejectReasons(new ArrayList<PpRejectReason>());
//			ppItem.setPpRejectReasons(PpCommonUtil.retrieveRejectReason(ppItem.getProcurementItemId(), ParameterConst.DOCTYPE_PLAN_PROCUREMENT));
		}
		loadRejectReasons(ppItem.getPpRejectReasons());
	}
	
	/**
	 * retrieve pp item remark
	 * @param ppItem
	 */
	public void retrievePpItemRemark(PpProcurementItem ppItem){
		log.info("retrievePpItemRemark");
		if(!ppItem.isBrandNew()){
			if(ppItem.getPpRemarks()==null || ppItem.getPpRemarks().isEmpty()){
				ppItem.setPpRemarks(new ArrayList<PpRemark>());
//				ppItem.setPpRemarks(PpCommonUtil.retrieveRemark(ppItem.getProcurementItemId(), ParameterConst.DOCTYPE_PLAN_PROCUREMENT));
			}
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
		this.getManagedBean().setSelectedItemRemarks(null);
		boolean newRemark = false;
		
		
		if(ppItem.getPpRemarks()==null){
			ppItem.setPpRemarks(new ArrayList<PpRemark>());
		}
		/* enable for phase 2
		//if first remark is not new
		if (!ppItem.getPpRemarks().isEmpty()){
			if(!ppItem.getPpRemarks().get(0).isBrandNew()){
				newRemark = true;
			}
		}*/

		// if remark is empty
		if (ppItem.getPpRemarks().isEmpty() || ppItem.getPpRemarks().size() == 0) {
			newRemark = true;
		}
		
		// filter by current view mode	
		if(!this.getManagedBean().isModeEditable() 
				&& !this.getManagedBean().getSelectedPlan().isUpdatable()){
			newRemark = false;
		}
		
		if(!ppItem.isEditable()){
			newRemark = false;
		}
		
		
		/* enable for phase 2
		// for user to edit draft remark
		if(newRemark){
			// remark list is not null & remark status == plan status
			if(!ppItem.getPpRemarks().isEmpty() && compareUserNameInsensitive(ppItem.getPpRemarks().get(0).getUserId())){
				//if both statuses are null
				if(ppItem.getPpRemarks().get(0).getPpWorkflowStatus()==null 
						&& this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId()==null){
					ppItem.getPpRemarks().get(0).setEditable(true);
					newRemark = false;
				//if both plan status!=null && justification status == null
				}else if(this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId()!=null && 
						(ppItem.getPpRemarks().get(0).getPpWorkflowStatus()==null 
							|| (ppItem.getPpRemarks().get(0).getPpWorkflowStatus()!=null &&
									ppItem.getPpRemarks().get(0).getPpWorkflowStatus().getWorkflowStatusId()==null))){
				// if remark status == plan status
				}else if(ppItem.getPpRemarks().get(0).getPpWorkflowStatus()!=null &&
						this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId().equals(
								ppItem.getPpRemarks().get(0).getPpWorkflowStatus().getWorkflowStatusId())){
					ppItem.getPpRemarks().get(0).setEditable(true);
					newRemark = false;
				}
				
			}
			
			
		} */
		
		if(newRemark){
			log.info("new remark added");
			PpRemark remark = new PpRemark();
			remark.setEditable(true);
			remark.setRemarkType(ProcurementPlanConst.REMARK_TYPE_REMARK);
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			remark.setUserId(getFacesScopes().getUserSessionBean().getLoginId());
			remark.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
			PmRole roleCode = new PmRole();
			roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
			remark.setRoleCode(roleCode);
			ppItem.getPpRemarks().add(0, remark);


		}
	
		this.getManagedBean().setSelectedItemRemarks(ppItem.getPpRemarks());
		this.getManagedBean().setSelectedItemRemarkDesc(ppItem.getPpRemarks().isEmpty() ? "": ppItem.getPpRemarks().get(0).getRemark());
		
		
		log.info("loadPpItemRemarks - End");
		
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
	 * to default the invitation date options for procurement item
	 * @param planYear
	 */
	public void updatePlanYearControl(Long planYear){
		log.info("updatePlanYearControl");
		if(planYear!=null){
			if(planYear == Calendar.getInstance().get(Calendar.YEAR)){ 
				//this year
				this.getManagedBean().setInvitationYear("");
			}else{ 
				//next year
				this.getManagedBean().setInvitationYear("c+0:c+0");
				//c+1:c+0 2017
				//c+0:c+0
				//c+1:c+1 2016, 2017
				//"" 2015(current)
			}
		}else{
			this.getManagedBean().setInvitationYear("");
		}
		
		log.info("updatePlanYearControl " + this.getManagedBean().getInvitationYear());
		
	}
	/* Procurement Related ------------------- END ---------------------------*/
	/* --------------------------------------------------------- Item Level - START -----------------------------------------------------------------*/
	
	
	
	/* --------------------------------------------------------- Search & Result - START -----------------------------------------------------------------*/
		
	/**
	 * Prepare and initial search view (filtered by login user)
	 */
	private void initialSearchPlan(){
		log.info("initialSearchPlan");
	
		PmOrgProfile pmOrgProfile = PpCommonUtil.retrieveUserOrgListByUserId(getFacesScopes().getUserSessionBean().getUserId(), true);
		if(pmOrgProfile==null || pmOrgProfile.getOrgProfileId()==null){
			throw new EpException(MessageConst.ERROR_730000);
		}
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
	}
	

	/**
	 * Generate/change plan search view based on selected plan type
	 * @param planTypeId
	 */
	public void changePlanTypeSearch(Long planTypeId){
		log.info("changePlanTypeSearch");
		//PP20150108-0023
		if(planTypeId != null){
			PmParameter planType = (PmParameter)planTypeMapper.get(planTypeId);
			this.getManagedBean().getPlanSearchBean().getPlanType().setParameterCode(planType!=null? planType.getParameterCode() : null);
			if(planType!=null){
				if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(planType.getParameterCode())){
					
				}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(planType.getParameterCode())){
					this.getManagedBean().getPlanSearchBean().setVotType(new PmParameter());
				}
			}
		}else{
			this.getManagedBean().getPlanSearchBean().setPlanType(new PmParameter());
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
	 * Generate org filter based on login
	 */
	public void generateOrgFilter(){
		log.info("generateOrgFilter");
		//check role and populate drop down value accordingly
		StringBuilder conditionSB = new StringBuilder();

		
		if(!isRoleBPK() && !isAuditor()){
			if(isRoleApprover()){
				conditionSB.append("|");
				conditionSB.append("orgProfileId^");
				conditionSB.append(this.getCurrentActiveOrg().getOrgProfileId());
				conditionSB.append("*");
				conditionSB.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()));
				conditionSB.append("^");
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
	 * Get search bean, filtered based on login org.
	 * @return ISearchBean<PpPlan>
	 */
	private ISearchBean<PpPlan> getSearchBeanData(ISearchBean<PpPlan> searchBean){
		log.info("getSearchBeanData - Start");

		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		ProcurementPlanSearchBean planSearchBean = this.getManagedBean().getPlanSearchBean();
			
		// plan number
		if(StringUtils.isNotBlank(planSearchBean.getPlanNumber())){
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
		if(planSearchBean.getPlanType()!=null && planSearchBean.getPlanType().getParameterCode()!=null){
			log.info("+ getPlanType "  +planSearchBean.getPlanType().getParameterCode());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("planType.parameterCode");
			bean.setSearchValue(planSearchBean.getPlanType().getParameterCode());
			searchBean.getDataFilterBeans().add(bean);
			
			// plan title
			if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(planSearchBean.getPlanType().getParameterCode()) 
					&& StringUtils.isNotBlank(planSearchBean.getPlanTitle())){
				log.info("+ getPlanTitle "  +planSearchBean.getPlanTitle());
				bean = new SearchFilterBean();
				bean.setCaseInsensitive(true);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchField("ppProcurementItems.procurementTitle");
				bean.setSearchValue(planSearchBean.getPlanTitle());
				bean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(bean);				
			}
			
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
		}else if(planSearchBean.getKptj()!=null  && planSearchBean.getKptj().getOrgProfileId()!=null){
			log.info("+ getKptj "  +planSearchBean.getKptj().getOrgProfileId());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("orgProfile."+
					OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
			bean.setSearchValue(planSearchBean.getKptj().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}else if(planSearchBean.getMinistry()!=null  && planSearchBean.getMinistry().getOrgProfileId()!=null){
			log.info("+ getMinistry "  +planSearchBean.getMinistry().getOrgProfileId());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("orgProfile."+
					OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()));
			bean.setSearchValue(planSearchBean.getMinistry().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
//		}else{
//			//filter plan by org profile
//			if(!isRoleBPK()){
//				if(isRoleApprover()){
//					log.info("isRoleApprover");
//					searchBean.getDataFilterBeans().add(
//							new SearchMultiFilterBean(
//									new SearchFilterBean(
//											"orgProfile.orgProfileId", 
//											SearchOperator.OPERATOR_EQUALS.value(), 
//											this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()),
//									new SearchFilterBean("orgProfile."+
//											OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()), 
//											SearchOperator.OPERATOR_EQUALS.value(), 
//											this.getManagedBean().getCurrentActiveOrg().getOrgProfileId(), 
//											SearchCondition.CONDITION_OR.value())));
//				}else if(isRoleVerifier() || isRolePlanner()){
//					log.info("isRoleVerifier or isRolePlanner");
//					SearchFilterBean profile = new SearchFilterBean();
//					profile.setSearchField("orgProfile.orgProfileId");
//					profile.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
//					profile.setSearchValue(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()); // user login active org
//					searchBean.getDataFilterBeans().add(profile);
//				}
//			}
//		}
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
			bean.setSearchField("ppExpenditureItems.votFundType.parameterId");
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
		getManagedBean().setSelectedMinistry(null);
		getManagedBean().setSelectedKumpulanPTJ(null);
		getManagedBean().setSelectedPTJ(null);
		setupOrgProfile();
		generateOrgFilter();
		ppPlanDataModel = new PpPlanDataModel();
	}
	
	/**
	 * search plan
	 */
	public void searchPlan(){
		log.info("searchPlan");
		this.getManagedBean().setSearch(true);
		boolean isEmpty = true;
		  if(!StringUtils.isBlank(this.getManagedBean().getPlanSearchBean().getPlanNumber())){
			  isEmpty = false;
		  }else if(this.getManagedBean().getPlanSearchBean().getYear()!=null){
			  isEmpty = false;
		  }else if(this.getManagedBean().getPlanSearchBean().getPlanType().getParameterId()!=null){
			  isEmpty = false;
		  }else if(this.getManagedBean().getPlanSearchBean().getVotType().getParameterId()!=null){
			  isEmpty = false;
		  }else if(this.getManagedBean().getPlanSearchBean().getPtj()!=null 
				  || this.getManagedBean().getPlanSearchBean().getKptj()!=null
				  || this.getManagedBean().getPlanSearchBean().getMinistry()!=null){
			  isEmpty = false;
		  }
		  if(isEmpty){
			  throw new EpException(MessageConst.ERROR_730027);
		  }
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
					
					// status
					if(getManagedBean().getPlanSearchBean().getStatus()!=null 
							&& getManagedBean().getPlanSearchBean().getPlanType()!=null 
							&& getManagedBean().getPlanSearchBean().getPlanType().getParameterCode()!=null){
						log.info("+ getStatus "  + getManagedBean().getPlanSearchBean().getStatus());
						reqDTO.setStatusId(getManagedBean().getPlanSearchBean().getStatus());
						if(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals( getManagedBean().getPlanSearchBean().getPlanType().getParameterCode())){
							reqDTO.setDocType(ParameterConst.DOCTYPE_PLAN_PROCUREMENT);
						}else if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals( getManagedBean().getPlanSearchBean().getPlanType().getParameterCode())){
							reqDTO.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
						}
					}
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
								
								if(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
									if(ppPlan.getPpProcurementItems() != null && !ppPlan.getPpProcurementItems().isEmpty()){
										ppPlan.setTitle(ppPlan.getPpProcurementItems().get(0).getProcurementTitle());
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
	/* --------------------------------------------------------- Search & Result - END -----------------------------------------------------------------*/
	
	
	private static String getUserRoleValue() {		
		FacesScopes sc = new FacesScopes();
		if (sc.hasRoleUser(RoleCode.PLAN_APPROVER.value())) {
			return RoleCode.PLAN_APPROVER.value();
		} else if (sc.hasRoleUser(RoleCode.PLAN_OFFICER.value())) {
			return RoleCode.PLAN_OFFICER.value();
//		} else if (sc.hasRoleUser(RoleCode.PLAN_EXPEN_OFFICER.value())) {
//			return RoleCode.PLAN_EXPEN_OFFICER.value();
//		} else if (sc.hasRoleUser(RoleCode.PLAN_PROCU_OFFICER.value())) {
//			return RoleCode.PLAN_PROCU_OFFICER.value();
		} else if (sc.hasRoleUser(RoleCode.PLAN_VERF_OFFICER.value())) {
			return RoleCode.PLAN_VERF_OFFICER.value();
		} 
	return null;	
	}
	
	private void generateSearchOptions(){
		List<SelectItem> items = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem(SEARCH_MINISTRY_CODE, 
				ResourceBundleUtils.getMessage("lbl.pm.org.ministryCode",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items.add(item);
		item = new SelectItem(SEARCH_MINISTRY_NAME, 
				ResourceBundleUtils.getMessage("lbl.pm.org.ministryName",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items.add(item);
		item = new SelectItem(SEARCH_PTJ_CODE, 
				ResourceBundleUtils.getMessage("lbl.pp.ptj.code",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items.add(item);
		item = new SelectItem(SEARCH_PTJ_NAME, 
				ResourceBundleUtils.getMessage("lbl.pp.ptj.name",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items.add(item);
		getManagedBean().setSearchOptions(items);
		this.getManagedBean().setSelectedSearchType(SEARCH_MINISTRY_CODE);
		getManagedBean().setSearchValue(null);
	}
	
	
	/**
	 * paginate for org profile list
	 * @author Ken
	 *
	 */
	private class PpOrgProfileDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public PpOrgProfileDataModel() {
			super("opresultTb");
		}
		@Override
		public ISearchResultBean<PmOrgProfile> pageChangeListener(ISearchBean<PmOrgProfile> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			
			try {				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("orgTypeCode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								ParameterConst.ORG_TYPE_PTJ));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmOrgValidity.recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				if(StringUtils.isNotBlank(getManagedBean().getSearchValue())){
					if(getManagedBean().getSelectedSearchType() == SEARCH_MINISTRY_CODE){
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(OrgProfileUtil.getSearchBeanParentWithoutParentIdString(OrganizationType.MINISTRY.value()) + 
										"pmOrgValidity.orgCode",
										SearchOperator.OPERATOR_LIKE.value(),
										getManagedBean().getSearchValue(), true));
					}else if(getManagedBean().getSelectedSearchType() == SEARCH_MINISTRY_NAME){
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(OrgProfileUtil.getSearchBeanParentWithoutParentIdString(OrganizationType.MINISTRY.value()) +
										"pmOrgValidity.orgName",
										SearchOperator.OPERATOR_LIKE.value(),
										getManagedBean().getSearchValue(), true));
					}else if(getManagedBean().getSelectedSearchType() == SEARCH_PTJ_CODE){
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("pmOrgValidity.orgCode",
										SearchOperator.OPERATOR_LIKE.value(),
										getManagedBean().getSearchValue(), true));
					}else if(getManagedBean().getSelectedSearchType() == SEARCH_PTJ_NAME){
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("pmOrgValidity.orgName",
										SearchOperator.OPERATOR_LIKE.value(),
										getManagedBean().getSearchValue(), true));
					}  
					
				}
				
				//user authorisation
				PmUser user = getFacesScopes().getUserSessionBean();
				List<PmUserOrg> userOrgList = PmCommonUtil.retrieveActiveOrg(user.getUserId());
				PmOrgProfile orgProfile = null;
				if(userOrgList != null && !userOrgList.isEmpty()){
					orgProfile = userOrgList.get(0).getPmOrgProfile();
				}
				//PTJ & MINISTRY
				if(orgProfile != null){
					if(OrganizationType.DIVISION.value().equals(orgProfile.getOrgTypeCode().getParameterCode())){
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("orgProfileId",
										SearchOperator.OPERATOR_EQUALS.value(),
										orgProfile.getOrgProfileId()));
					}else if(OrganizationType.MINISTRY.value().equals(orgProfile.getOrgTypeCode().getParameterCode())){
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
										SearchOperator.OPERATOR_EQUALS.value(),
										orgProfile.getOrgProfileId()));
					}
				}
				
				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();

				orgProfileReqDTO.setOrgProfileSearchBean(searchBean);
				OrgProfileResDTO resDto = getOrgProfileSO()
						.searchOrgProfile(orgProfileReqDTO);

				if (resDto.isSuccessful()) {
					return resDto.getOrgProfileResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}
	
	/**
	 * Gets the org profile so.
	 * 
	 * @return the org profile so
	 */
	private IOrgProfileSO getOrgProfileSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
	}


	/**
	 * @return the ppOrgProfileDataModel
	 */
	public IPagingDataModel<PmOrgProfile> getPpOrgProfileDataModel() {
		return ppOrgProfileDataModel;
	}


	/**
	 * @param ppOrgProfileDataModel the ppOrgProfileDataModel to set
	 */
	public void setPpOrgProfileDataModel(IPagingDataModel<PmOrgProfile> ppOrgProfileDataModel) {
		this.ppOrgProfileDataModel = ppOrgProfileDataModel;
	}	
	
	/**
	 * This method used for cater org profile search action.
	 * 
	 **/
	public void searchAction() {
		ppOrgProfileDataModel.reloadData();
	}
	
	/**
	 * This method used for cater org profile reset search action.
	 * 
	 **/
	public void resetAction() {
		getManagedBean().setSelectedSearchType(SEARCH_MINISTRY_CODE);
		getManagedBean().setSearchValue(null);
		getManagedBean().setSelectedPTJ(null);
		ppOrgProfileDataModel.reloadData();
	}
	
	public void populateSaveObject(){
		if(getManagedBean().getSelectedPTJ() != null){
			getManagedBean().getPlanSearchBean().setPtj(getManagedBean().getSelectedPTJ());
		}
	}
	
	public String retrievePpBillingAddr() {
		if(getManagedBean().getSelectedPlan().getOrgProfileAddress() != null){
			return getPpFullAddress(getManagedBean().getSelectedPlan().getOrgProfileAddress());
		}else{
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setOrgProfileId(getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(resDTO.getAddressTypeList() !=null && !resDTO.getAddressTypeList().isEmpty()){
				return getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress());
			}else{
				return "";
			}
		}
	}
	
	public String constructMinistryConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.MINISTRY.value());
		sb.append("|recordStatus^1");
		return sb.toString();
	}
	
	public String constructKumpulanPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DEPARTMENT.value());		
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(),OrganizationType.MINISTRY.value()));
		sb.append("^");
		sb.append(getManagedBean().getSelectedMinistry().getOrgProfileId());
		return sb.toString();
	}
	
	public String constructPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DIVISION.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
		sb.append("^");
		sb.append(getManagedBean().getSelectedKumpulanPTJ().getOrgProfileId());		
		return sb.toString();
	}
	
	public void updateSearchCriteria(BaseEntity entity) {
		log.info("updateSearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPlanSearchBean()
				.setPtj(orgProfile);
	}
	
	public void updateKPTJSearchCriteria(BaseEntity entity) {
		log.info("updateKPTJSearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPlanSearchBean()
				.setKptj(orgProfile);
	}
	
	public void updateMinistrySearchCriteria(BaseEntity entity) {
		log.info("updateMinistrySearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPlanSearchBean()
				.setMinistry(orgProfile);
	}
	
	//ministry, kumpulan ptj and ptj
	private void setupOrgProfile(){
		if(getManagedBean().getCurrentActiveOrg() !=null){
			Long orgProfileId = getManagedBean().getCurrentActiveOrg().getOrgProfileId();
			if(getManagedBean().getCurrentActiveOrg().getGroupOrgType() !=null 
					&&(ParameterConst.ORG_AUTH_TYPE_KUMPULAN_PTJ.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType())
							|| ParameterConst.ORG_AUTH_TYPE_MINISTRY.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType()))){
				if(isRolePlanner() || isRoleVerifier()){
					getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));
					getManagedBean().setDisabledMinistryLov(true);
					getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(orgProfileId));
					getManagedBean().setSelectedPTJ(getManagedBean().getCurrentActiveOrg());
					getManagedBean().getPlanSearchBean().setPtj(getManagedBean().getCurrentActiveOrg());
					getManagedBean().setDisabledKumpulanPTJLov(true);
					getManagedBean().setDisabledPTJLov(true);
					
				}else{
					 // group org hierarchy
					if(ParameterConst.ORG_AUTH_TYPE_MINISTRY.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType())){
						getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));
						getManagedBean().setDisabledMinistryLov(true);
						getManagedBean().getPlanSearchBean().setMinistry(retrieveMinistryOrgProfile(orgProfileId));
					}else if(ParameterConst.ORG_AUTH_TYPE_KUMPULAN_PTJ.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType())){
//						List<PmOrgProfile> orgProfileList = PpCommonUtil.retrieveOrgProfileListFromGroupOrgType(orgProfileId,
//								OrganizationType.MINISTRY.value(), ParameterConst.ORG_AUTH_TYPE_MINISTRY);
//						if(orgProfileList != null && !orgProfileList.isEmpty()){
//							getManagedBean().setSelectedMinistry(orgProfileList.get(0));
//							getManagedBean().getPlanSearchBean().setMinistry(orgProfileList.get(0));
//						}		
						getManagedBean().getPlanSearchBean().setMinistry(retrieveMinistryOrgProfile(orgProfileId));
						getManagedBean().getPlanSearchBean().setKptj(retrieveJabatanOrgProfile(orgProfileId));
						getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));
						getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(orgProfileId));
						getManagedBean().setDisabledMinistryLov(true);
						getManagedBean().setDisabledKumpulanPTJLov(true);
					}
				}
				
			}else if(getManagedBean().getCurrentActiveOrg().getGroupOrgType() ==null 
					|| (getManagedBean().getCurrentActiveOrg().getGroupOrgType() !=null 
					&& ParameterConst.ORG_AUTH_TYPE_ORG_GROUP.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType()))){
				// usual org hierarchy 
				if(isRolePlanner() || isRoleVerifier()){
					getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));
					getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(orgProfileId));
					getManagedBean().setSelectedPTJ(getManagedBean().getCurrentActiveOrg());
					getManagedBean().getPlanSearchBean().setPtj(getManagedBean().getCurrentActiveOrg());
					getManagedBean().setDisabledMinistryLov(true);
					getManagedBean().setDisabledKumpulanPTJLov(true);
					getManagedBean().setDisabledPTJLov(true);
				}else if(isRoleApprover()){
						getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));
						getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(orgProfileId));
						getManagedBean().setSelectedPTJ(getManagedBean().getCurrentActiveOrg());
						getManagedBean().getPlanSearchBean().setPtj(getManagedBean().getCurrentActiveOrg());
						getManagedBean().setDisabledMinistryLov(true);
						getManagedBean().setDisabledKumpulanPTJLov(true);
						getManagedBean().setDisabledPTJLov(true);
				}
			}
			
			
		}
	}
	
	public List<PmParameterDesc> retrievePplanType(){
		log.info("retrievePplanType");		
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.PLAN_TYPE, null);
	}
	
	public void changeSearchPlanType(String paramCode){
		if(paramCode != null){
			this.getManagedBean().getPlanSearchBean().setStatus(null);
			this.getManagedBean().getPlanSearchBean().setPlanTitle(null);
		}
	}

	
	public SelectItem[] retrievePlanStatus(){
		log.info("retrievePlanStatus");
		SelectItem[] items = null;
		if(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals(getManagedBean().getPlanSearchBean().getPlanType().getParameterCode())){
			items =  getStatusDropdownValues(ModuleCodeConst.PROCUREMENT_PLAN, ParameterConst.DOCTYPE_PLAN_PROCUREMENT);
		}else if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(getManagedBean().getPlanSearchBean().getPlanType().getParameterCode())){
			items = getStatusDropdownValues(ModuleCodeConst.PROCUREMENT_PLAN, ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
		}else{
			items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale()));
		}
		
		return items;
	}
	
	/**
	 * Generate report based on doc type.
	 *
	 * @param docType the doc type
	 * @param docNo the doc no
	 */
	public void generateReport(PpPlan plan) {
		
		Map<String, String> paramMap = new HashMap<String, String>();
		//http://ngepdevrps01.ngep.com:5901/reports/rwservlet?ngepdev&report=PP_DETAIL_PROCUREMENT.jsp&PUSER=244074&PPTJ=263725&PYEAR=2016&PMINIS=my.ep.persistence.entity.pm.authorization.PmOrgProfile@282f131b&PREPORTID=PP_002&destype=cache&desformat=pdf
		//PPTJ=263725&PMINIS=231618&PYEAR=2016&PREPORTID=PP_002
//		paramMap.put("PREPORTID", "PP_002");
		paramMap.put("PYEAR", plan.getPlanYear()+"");
		paramMap.put("PPTJ", plan.getOrgProfile().getOrgProfileId()+"");
//		paramMap.put("PMINIS", retrieveMinistryOrgProfile(plan.getOrgProfile().getOrgProfileId()).getOrgProfileId()+"");
//		paramMap.put(ReportConst.PARAM_DOC_NO, docNo);
		paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
		String reportName = null;
		if(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals(plan.getPlanType().getParameterCode())) {
			reportName = ReportConst.PP_NGEP_PP_DETAIL_PROCUREMENT;
		} else if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(plan.getPlanType().getParameterCode())) {
			reportName = ReportConst.PP_NGEP_PP_DETAIL_EXPENDITURE;
		} 
		if(reportName!=null) {
			try {
				generateReport(reportName, paramMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new EpException("Report is not available for this document.");
		}
	}
	
	public String retrievePlanDocType(PpPlan plan){
		return ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(plan.getPlanType().getParameterCode()) 
		? ParameterConst.DOCTYPE_PLAN_PROCUREMENT
		: ParameterConst.DOCTYPE_PLAN_EXPENDITURE;
	}
	
	public String getProcurementCategoryLabels(PmParameter parameter) {
		log.info("getProcurementCategoryLabels");
		String label = "";
		String languageCode = getFacesScopes().getUserLocale().getLanguage();
		for(PmParameterDesc desc: parameter.getPmParameterDescs()){
			if(languageCode.equals(desc.getLanguageCode())){
				label = desc.getCodeName();
				break;
			}
		}
		return label;
	}

}
