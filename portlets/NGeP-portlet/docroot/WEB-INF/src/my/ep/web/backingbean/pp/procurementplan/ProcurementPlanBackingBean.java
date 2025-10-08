/*
 * Copyright 2013 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pp.procurementplan;
 
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
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
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmProcurementCatType;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.VPmGo;
import my.ep.persistence.entity.pp.procurementplan.PpExpenditureItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanActor;
import my.ep.persistence.entity.pp.procurementplan.PpProcurementItem;
import my.ep.persistence.entity.pp.procurementplan.PpRejectReason;
import my.ep.persistence.entity.pp.procurementplan.PpRemark;
import my.ep.persistence.entity.pp.procurementplan.PpUpdateReason;
import my.ep.persistence.entity.pp.procurementplan.PpWorkflowStatus;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.glaccount.GlAccountBackingBean;
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
import my.ep.web.util.pm.helper.RoleHelper;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.pp.common.PpCommonUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
//#24975

//#35502 - Start
import my.ep.biz.service.IAuditTrailSO;
import my.ep.constant.pp.AuditPPConst;
import my.ep.web.backingbean.pp.audit.AuditProcurementPlanBackingBeanTemplate;
import my.ep.biz.service.pp.audit.IAuditProcurementPlanSO;
import my.ep.dto.pp.audit.AuditProcurementPlanReqDTO;
import my.ep.dto.pp.audit.AuditProcurementPlanResDTO;
import my.ep.dto.pp.audit.AuditProcurementPlanSPDTO;
//#35502 - End

/**
 * Backing Bean for Procurement Plan 
 * 
 * @author RAIN POH
 * @since 20/10/2013
 *
 */
@ManagedBean(name = "pPlanBackingBean")
@ViewScoped
public class ProcurementPlanBackingBean extends BaseBackingBean<ProcurementPlanManagedBean> implements Serializable {
	/**
	 * addUpdateInfo
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ProcurementPlanBackingBean.class);
	//Navigation Details
	public static final String NAV_TO_MAINTAIN = "procurementPlanDet";
	public static final String NAV_TO_SEARCH = "procurementPlanMain";
	private static final String NAV_TO_ANNUAL_PPLAN = "/xhtml/pp/procurementplan/search/annualplan/apProcurementMain.xhtml";
	
	//List for Paging
	private IPagingDataModel<PpPlan> ppPlanDataModel = new PpPlanDataModel();
	private IPagingDataModel<PmOrgProfile> ppOrgProfileDataModel = new PpOrgProfileDataModel();
	private IPagingDataModel<PpProcurementItem> ppAnnualPlanDataModel = new PpAPlanDataModel();	//[#25550]
	private IPagingDataModel<PpProcurementItem> ppCloneProcurementDataModel = new PpCloneProcurementDataModel();//[#25549]
	private IPagingDataModel<PpExpenditureItem> ppEpItemsDataModel = new PpEpItemsDataModel();	//[#25549]

	
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
	private static final String CAP_CHAR = "^"; //[Bug#27032]
	
	//Mandatory check
	//200911@akmal - Enhancement27624 - Start
	private static final Integer startyear = 2018;
	private static boolean isMandatory = false;
	private static String pageType = "";
	
	private GlAccountBackingBean glAccBackingBean = new GlAccountBackingBean();
	//200911@akmal - Enhancement27624 - End
	private static Map<List<PpProcurementItem>,List<PpProcurementItem>> ppItemMap = 
    		new HashMap<List<PpProcurementItem>,List<PpProcurementItem>>();
	
	/**
	 * 
	 * 
	 * @return planTypeSearchBean
	 */
	public ISearchBean<PmParameterDesc> getPlanTypeSearchBean() {
		log.info("getPlanTypeSearchBean");
		if (this.planTypeSearchBean == null) {
			this.planTypeSearchBean = super.createSearchBean(PmParameterDesc.class, "somPlanType");
		}

		return this.planTypeSearchBean;
	}
		
	/**
	 * Debug logger
	 * 
	 * @param message
	 */
	/*private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.info(message);
		}
		log.info(message);
	}*/
	
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
		this.getManagedBean().setBackAllow(true);
		this.resetPageModeFlags();
		this.resetManagedBean();
		this.getManagedBean().setTrackingDiary(isTrackingDiary);
		this.retrievePlanTypeMapper();
		initialSearchPlan();
		this.resetPlanSearchBean();
		generateSearchOptions();
		setupOrgProfile();
		//210108@akmal - Enhancement27624 - Start
		this.getManagedBean().getPlanSearchBean().setPlanDetstatus(99L);
//		if(this.getManagedBean().getvPmGolist()!=null)
			this.getManagedBean().getvPmGolist().clear();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		 try{
			 PortletRequest pr = (javax.portlet.PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.portlet.request");
			 Method method = pr.getClass().getMethod("getOriginalHttpServletRequest");
			 HttpServletRequest httpServletRequest = (HttpServletRequest)method.invoke(pr, new Object[] {});
			 //friendlyURL
			 String plid = httpServletRequest.getParameter("p_l_id");
			 ThemeDisplay theme = (ThemeDisplay) pr.getAttribute(WebKeys.THEME_DISPLAY);
			 Layout selectedLayout = LayoutLocalServiceUtil.getLayout(Long.valueOf(plid));
			 String urlliferay = PortalUtil.getLayoutFriendlyURL(selectedLayout, theme);
			 if(urlliferay.contains("plan-maintenance")){//09022021@24975 - start
				 this.getManagedBean().setAudit(false);
				//#32730 - start
				 this.getManagedBean().setPreYear(false);
				//#32730 - end
		       }else if(urlliferay.contains("procurement-plan-search")){
				 this.getManagedBean().setAudit(true);
			 //if procurement-plan-search = audit /web/dashboard/procurement-plan-search 
			 //if plan-maintenance = plan maintenance /web/dashboard/plan-maintenance
		   }
		 }//09022021@24975 - End
		 catch(Exception e){
			 log.error(e.getStackTrace());
		 }
		setScreenVariables();
		//210108@akmal - Enhancement27624 - End
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}
	
	/**
	 * Pre-loading from Create Plan
	 */
	public void preLoadingCreate(){
		log.info("preLoadingCreate");
		this.getManagedBean().setBackAllow(false);
		//201228@akmal - Enhancement27624 - Start
		this.getManagedBean().setDisablePlanSearchDet(true);
		this.getManagedBean().setPageStyle("create");
		this.getManagedBean().getPlanSearchBean().setPlanDetstatus(99L);
		//201228@akmal - Enhancement27624 - End
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
		Object obj = super.getBpmTaskDetails(Object.class, new String[]{BpmOutcomeConst.BPM_SAVE});
		
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setBackAllow(false);
		generatePageModeForProcess(this.getManagedBean().getTaskName());

		// Procurement Plan
		PPPlanDataType planObj = (PPPlanDataType) obj;
		//27624 - start
		this.getManagedBean().setPlanDetailSearchReset(true);
		//27624 - end
		this.loadPlanDetails(planObj.getPlanId().longValue(), false);
		setScreenVariables();
		this.getManagedBean().setPlanId(planObj.getPlanId().longValue());
		this.getManagedBean().setFromTaskList(true);

		//201228@akmal - Enhancement27624 - Start
		this.getManagedBean().setMaxYear("31/12/"+Calendar.getInstance().get(Calendar.YEAR));
//		if("approved".equalsIgnoreCase(getStatusDescById(this.getManagedBean().getPpWorkflowStatus().getPmStatus().getStatusId())))
//			this.getManagedBean().setDisablePlanSearchDet(true);
//		else
		this.getManagedBean().setDisablePlanSearchDet(false);
		this.getManagedBean().getPlanSearchBean().setPlanDetstatus(99L);
		this.getManagedBean().getvPmGolist().clear();
		//201228@akmal - Enhancement27624 - End

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAINTAIN);
	}
	
	
	/**
	 * Redirect to Plan Search
	 * @return xhtml
	 */
	public String redirectToSearchPlan(){
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
		//#32730 - start
		this.getManagedBean().setPreYear(true);
		//#32730 - end
		this.resetManagedBean();
		this.getManagedBean().setBtnValue(action);
		this.getManagedBean().setTrackingDiary(isTrackingDiary);
		this.retrievePlanTypeMapper();
		this.retrieveVotTypeMapper();
		//27624 - start
		this.getManagedBean().setPlanDetailSearchReset(true);
		//27624 - end
		if(id!=null && id>0){
			//201229@akmal - Enhancement27624 - Start
			if(this.getManagedBean().isFromTaskList()){ //24975@06112020@azza - Start
				loadPlanDetails(id, false);
			}else{
				loadPlanDetails(id, true);
			} //24975@06112020@azza - End
			if("approved".equalsIgnoreCase(getStatusDescById(this.getManagedBean().getPpWorkflowStatus().getPmStatus().getStatusId()))){
//				this.getManagedBean().setDisablePlanSearchDet(true);
				this.getManagedBean().setDisablePlanSearchDet(false);
				this.getManagedBean().setPageStyle("m_approved");
			}else{//24975@19012021 - azza - start
				this.getManagedBean().setDisablePlanSearchDet(false);
			}//24975@19012021 - azza - end
			loadDefaultButtons();
			this.getManagedBean().setPlanId(id);
			this.getManagedBean().getPlanSearchBean().setPlanDetstatus(99L);
			this.getManagedBean().getvPmGolist().clear();
			//201229@akmal - Enhancement27624 - End
		}
		
		
		RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
		return NAV_TO_MAINTAIN;
	}

	/**
	 * compare username case insensitive
	 * @param username
	 * @return boolean
	 */
	public boolean compareUserNameInsensitive(String username) {
		boolean result = false;
		FacesScopes facesScopes = new FacesScopes();
		if (username != null
				&& username.equalsIgnoreCase(facesScopes.getUserScreenName())) {
			result = true;
		} else {
			result =  false;
		}
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
		
		if (ProcurementPlanConst.ACTION_VIEW_BTN.equals(getManagedBean().getBtnValue())) {
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(this.getManagedBean().getSelectedPlan().getPlanType().getParameterCode())) {
				if (this.getManagedBean().getSelectedPlan().getPpProcurementItems() != null &&
						this.getManagedBean().getSelectedPlan().getPpProcurementItems().size() > 0) {
					for (PpProcurementItem pp : this.getManagedBean().getSelectedPlan().getPpProcurementItems()) {
						pp.setViewMode(true);
					}
				}
			}
		}
		
		log.info("STATUS: " + status);
		
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
						&& (getManagedBean().getBtnValue() != null 
						&& ProcurementPlanConst.ACTION_UPDATE_BTN.equals(getManagedBean().getBtnValue()))){//#27624@322021
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
		this.getManagedBean().setSelectedUpdateReasons(null);
		
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
	
	private void resetPpProcurementItems(List<PpProcurementItem> ppItems) {
		log.info("Resetting PpItems...");
		
		List<Long> fromPpItemList = new ArrayList<Long>();
		List<Long> procurementItemIdList = new ArrayList<Long>();
		
		for (PpProcurementItem ppItem : ppItems) {
			//log.info("ppItem.title= "+ppItem.getProcurementTitle());
			if (!ppItem.isOriginal() && ppItem.getFromPpItem() != null) {
				fromPpItemList.add(ppItem.getFromPpItem());
			}
			
			// #34390 - Start
			if (ppItem.isOriginal() && ppItem.getFromPpItem() != null && this.getManagedBean().isSearch()
					&& this.getManagedBean().getPlanSearchBean().getPlanDetstatus() != null) {
				fromPpItemList.add(ppItem.getFromPpItem());
			}

			if (this.getManagedBean().isSearch()
					&& this.getManagedBean().getPlanSearchBean().getPlanDetstatus() != null) {
				procurementItemIdList.add(ppItem.getFromPpItem());
			}
			// #34390 - End
						
			procurementItemIdList.add(ppItem.getProcurementItemId());
		}
		
		//log.info("fromPpItemList= "+fromPpItemList.size());
		
		if (fromPpItemList.size() > 0) {
			
			List<Long> commonProcurementItemIdList = new ArrayList<Long>(fromPpItemList);
			commonProcurementItemIdList.retainAll(procurementItemIdList);
			
			//log.info("commonProcurementItemIdList.size= "+commonProcurementItemIdList.size());
		    
			if (commonProcurementItemIdList != null && !commonProcurementItemIdList.isEmpty()) {
				for (Long commonProcurementItemId : commonProcurementItemIdList) {
					//log.info("commonProcurementItemId= "+commonProcurementItemId);
					for (PpProcurementItem ppItem : ppItems) {
						
						/* By default, all items are neutral (isOriginal or not).
						   isClonedItem will be true if only it's cloned during 
						   that particular round of approval. */
						ppItem.setClonedItem(false);
						
						/* Always disable Pindaan for cloned ppItem
						   Always disable Reason For Update for original ppItem*/
						/*log.info("procurementItemId= "+ppItem.getProcurementItemId()+", isOriginal= "
						   +ppItem.isOriginal()+", fromPpItem= "+ppItem.getFromPpItem()
						   +", commonProcurementItemId= "+commonProcurementItemId);*/
						if (!ppItem.isOriginal()) {
							if (ppItem.getFromPpItem().equals(commonProcurementItemId)) {
								
								/*log.info("allowAnotherPindaan for "+ ppItem.getProcurementTitle()+
										" ("+ppItem.getProcurementItemId()+") is "+
										ppItem.isAllowAnotherPindaan());*/
								//24975@04112020@azza - start
								if (!ppItem.isAllowAnotherPindaan()) {
									ppItem.setCloneEnabled(false);
									ppItem.setDisableForPindaan(false);
									ppItem.setReasonForUpdateEnabled(true);
									ppItem.setBrandNew(false);
									//#34156 10/11/2022- Start
									ppItem.setNewLabcatMethodType(true);
									
									if (!ppItem.isEditable()){
											if (ppItem.getProcurementType().getParameterCode()
													.equals(ProcurementPlanConst.LIMITED_TENDER)
											|| ppItem.getProcurementType().getParameterCode()
													.equals(ProcurementPlanConst.SELECTIVE_TENDER)) {
										ppItem.setNewLabcatMethodType(false);
										}
									}
																		
									if (ProcurementPlanConst.ACTION_VIEW_BTN.equals(getManagedBean().getBtnValue())) {
										if (ppItem.getProcurementType().getParameterCode()
												.equals(ProcurementPlanConst.LIMITED_TENDER)
												|| ppItem.getProcurementType().getParameterCode()
														.equals(ProcurementPlanConst.SELECTIVE_TENDER)) {
											ppItem.setNewLabcatMethodType(false);
										}
									}
									//#34156 10/11/2022- End
								} else { 
									ppItem.setCloneEnabled(false);
									ppItem.setDisableForPindaan(false);
									ppItem.setReasonForUpdateEnabled(true);
									ppItem.setBrandNew(false);									
									//#34156 10/11/2022- Start
									//#34422 09/11/2023- Start
									if (ppItem.getProcurementType().getParameterCode().equals(ProcurementPlanConst.SELECTIVE_TENDERING)
											|| ppItem.getProcurementType().getParameterCode().equals(ProcurementPlanConst.DIRECT_NEGOTIATION)){
										ppItem.setNewLabcatMethodType(true);
									}
									//#34422 09/11/2023- End
									//#34156 10/11/2022- End
									//#27624@322021 - start
									if (!ProcurementPlanConst.ACTION_VIEW_BTN.equals(getManagedBean().getBtnValue())){
									this.getManagedBean().setModeEditable(true);
									}//#27624@322021 - end
								}
								//24975@04112020@azza - end
							}
						} else {
							/* Always disable Pindaan for original ppItem
							   Always enable Reason For Update for original ppItem*/
							if (ppItem.isBrandNew()) {
								ppItem.setBrandNew(false);
							}
							
							if (ppItem.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED) 
									&& !ppItem.isDisableRow()) { //28012021@27624 
								if (!ppItem.isCloneEnabled()) {
									 ppItem.setCloneEnabled(true);
								}
								if (!ppItem.isDisableForPindaan()) {
									ppItem.setDisableForPindaan(true);
								}
								/*log.info("isDisableForPindaan= "+ppItem.isDisableForPindaan()
										+", isCloneEnabled= "+ppItem.isCloneEnabled());*/
							}
							
							if (ppItem.getProcurementItemId().equals(commonProcurementItemId)) {
								/*log.info("allowAnotherPindaan for "+ ppItem.getProcurementTitle()+
										" ("+ppItem.getProcurementItemId()+") is "+
										ppItem.isAllowAnotherPindaan());*/
								ppItem.setCloneEnabled(true);
								ppItem.setDisableForPindaan(true);
								ppItem.setReasonForUpdateEnabled(true);

								//#34156 10/11/2022- Start
								// #34422 09/11/2023- Start
								if (ppItem
										.getProcurementType()
										.getParameterCode()
										.equals(ProcurementPlanConst.SELECTIVE_TENDERING)
										|| ppItem
												.getProcurementType()
												.getParameterCode()
												.equals(ProcurementPlanConst.DIRECT_NEGOTIATION)) {
									ppItem.setNewLabcatMethodType(true);
								}
								// #34422 09/11/2023- End
								//#34156 10/11/2022- End
								
							} else {
								// The rest should retain the existing field logics
								ppItem.setDisableForPindaan(false);
								ppItem.setReasonForUpdateEnabled(true);
   
								//#34156 10/11/2022- Start
								// #34422 09/11/2023- Start
								if (ppItem
										.getProcurementType()
										.getParameterCode()
										.equals(ProcurementPlanConst.SELECTIVE_TENDERING)
										|| ppItem
												.getProcurementType()
												.getParameterCode()
												.equals(ProcurementPlanConst.DIRECT_NEGOTIATION)) {
									ppItem.setNewLabcatMethodType(true);
								}
								// #34422 09/11/2023- End
								//#34156 10/11/2022- End
							}
						}
					}
				}
		    }
		}
		// Attempting to reorder the PpItems only if clones exist
        List<PpProcurementItem> tempOriginalPpItemList = new ArrayList<PpProcurementItem>();
		List<PpProcurementItem> tempClonedPpItemList = new ArrayList<PpProcurementItem>();
				
        for (PpProcurementItem ppItem : ppItems) {
        	
        	//#34156, #34390 13/01/2023- Start
			if (ppItem.isSelectable() && !ProcurementPlanConst.ACTION_VIEW_BTN.equals(getManagedBean().getBtnValue())
					&& ppItem.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
				if (ppItem.getProcurementType().getParameterCode().equals(ProcurementPlanConst.LIMITED_TENDER)
						|| ppItem.getProcurementType().getParameterCode().equals(ProcurementPlanConst.SELECTIVE_TENDER)) {
					ppItem.setProcurementType(new PmParameter());
				}
			}
			//#34156, #34390 13/01/2023- End
			
			if (!this.getManagedBean().isPlanDetailSearch() && ppItem.getFromPpItem() != null) {//01022021@27624 - start
				tempClonedPpItemList.add(ppItem);
			} else if (this.getManagedBean().isPlanDetailSearch() && ppItem.getFromPpItem() != null
					&& this.getManagedBean().getPlanSearchBean().getTitleOrDesc().isEmpty()) {
				tempClonedPpItemList.add(ppItem);
			} else if (this.getManagedBean().isPlanDetailSearch() && ppItem.getFromPpItem() != null
					&& !this.getManagedBean().getPlanSearchBean().getTitleOrDesc().isEmpty()) {
				tempOriginalPpItemList.add(ppItem);
			} else {
				tempOriginalPpItemList.add(ppItem);
        }
       }//01022021@27624 - end
        
        Collections.sort(tempOriginalPpItemList, new Comparator<PpProcurementItem>() {
 		    @Override
 		    public int compare(PpProcurementItem o1, PpProcurementItem o2) {
 		    	/*log.info("Compare ppItem by createdDate.. ");
 		    	log.info(o1.getProcurementItemId()+
 		    			"= "+o1.getCreatedDate()+
 		    			", "+o2.getProcurementItemId()+"= "+o2.getProcurementItemId()+
 		    			" => "+o1.getCreatedDate().compareTo(o2.getCreatedDate()));*/
 		        return o1.getCreatedDate().compareTo(o2.getCreatedDate());
 		    }
 		});
        
        // Debugging..
        //log.info("tempOriginalPpItemList (reorderedList)..");
        for (PpProcurementItem ppit : tempOriginalPpItemList) {
        	/*log.info("tempOriginalPpItemList.title= "+
        			ppit.getProcurementTitle());*/

        	//#34156 10/11/2022- Start
        	//#34390- Start
        	//#34422 09/11/2023- Start
        	if (ppit.isOriginal() && ppit.getFromPpItem() == null && ppit.isSelectable()
        			&& ppit.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
				ppit.setNewLabcatMethodType(true);
			} else if (ppit.isOriginal() && ppit.getFromPpItem() == null 
					&& ppit.getProcurementType().getParameterCode().equals(ProcurementPlanConst.SELECTIVE_TENDERING)
					|| ppit.getProcurementType().getParameterCode().equals(ProcurementPlanConst.DIRECT_NEGOTIATION)){
				ppit.setNewLabcatMethodType(true);
			}
            //#34422 09/11/2023- End
        	//#34390- End
        	
        	if (ProcurementPlanConst.ACTION_VIEW_BTN.equals(getManagedBean().getBtnValue())) {
				if (ppit.getProcurementType().getParameterCode()
						.equals(ProcurementPlanConst.LIMITED_TENDER)
						|| ppit.getProcurementType().getParameterCode()
								.equals(ProcurementPlanConst.SELECTIVE_TENDER)) {
					ppit.setNewLabcatMethodType(false);
				}
			}
        	//#34156 10/11/2022- End
        }
        
        if (!ppItemMap.isEmpty() && ppItemMap.size() > 0) {
    		log.info("ppItemMap size before= "+ppItemMap.size());
    		ppItemMap.clear();
        }
        
        //log.info("tempClonedPpItemList.size= "+tempClonedPpItemList.size());
        
		if (tempClonedPpItemList.size() > 0) {
			ppItemMap.put(tempClonedPpItemList, tempOriginalPpItemList);
		}
        
        List<PpProcurementItem> clones = new ArrayList<PpProcurementItem>();
    	List<PpProcurementItem> reorderedList = new LinkedList<PpProcurementItem>();
    	List<PpProcurementItem> newPpItems = new ArrayList<PpProcurementItem>(reorderedList);
    	
    	if (!reorderedList.isEmpty() && reorderedList.size() > 0) {
    		//log.info("reorderedList size before this= "+reorderedList.size());
    		reorderedList.clear();
        }
    	    	
    	//log.info("Size ppItemMap= "+ppItemMap.size());
    	
    	if (ppItemMap.size() > 0) {
    		
	        for (Map.Entry<List<PpProcurementItem>, List<PpProcurementItem>> ppItemList : 
	        	ppItemMap.entrySet()) {
	        	
	        	clones = ppItemList.getKey();
	        	reorderedList = ppItemList.getValue();
	        	
	        	/*log.info("Initially, size of reorderedList is "
	        			+reorderedList.size());*/
	        	
	        	for (int i = 0; i < reorderedList.size(); i++) {
		        	for (PpProcurementItem clone : clones) {
	        			if (clone.getFromPpItem().equals(reorderedList.get(i).getProcurementItemId())) {
	        				//log.info("cloned is= "+clone.getProcurementTitle());
	        				//27624 - start
	        				this.getManagedBean().setGetProcurementItemList(true);   
	        				//27624 - end
	        				reorderedList.add(i+1,clone);
	        			}
	        		}
		        }
	        }
	       /* log.info("Size getPpProcurementItems= "+ppItems.size()+
	        		", size reorderedList= "+reorderedList.size());*/
	        
	       /* for (PpProcurementItem reorder : reorderedList) {
	        	log.info("reorderedList.title= "+reorder.getProcurementTitle());
	        }*/
	        
	        newPpItems = new ArrayList<PpProcurementItem>(reorderedList);
	      //27624 - start
	      if(this.getManagedBean().isGetProcurementItemList() == true){
	        if (!ppItems.isEmpty() && ppItems.size() > 0) {
	        	ppItems.clear();
	        	this.getManagedBean().getSelectedPlan().setPpProcurementItems(null);
	        }	        	
	         //log.info("isGetProcurementItemList " + this.getManagedBean().isGetProcurementItemList());
	         this.getManagedBean().getSelectedPlan().setPpProcurementItems(newPpItems);
	        }else{
	        	//log.info("isGetProcurementItemList false " + ppItems.size());
	          this.getManagedBean().getSelectedPlan().setPpProcurementItems(ppItems);
	        }
	        //27624 - end
	        /*log.info("Displaying ppItems for Plan No=  "+
	        		this.getManagedBean().getSelectedPlan().getPlanNo());*/
	               
	       
	        for (PpProcurementItem pItem : this.getManagedBean().getSelectedPlan().getPpProcurementItems()) {
	        	
	        	 /* Check predecessors of each node, change Pencil to Note icon for update reason field
		          * and make its textarea disabled
		         * */
	        	//27624 - start
	        	this.getManagedBean().setGetProcurementItemList(false);
	        	//27624 - end
        		this.checkItemPredecessor(pItem);
	        }
	        
        } else {
        	
        	//log.info("The ProcurementItem list currently has no clones");
        	
        	if (tempOriginalPpItemList != null && tempOriginalPpItemList.size() > 0) {
	        	for (int i = 0; i < tempOriginalPpItemList.size(); i++) {
					reorderedList.add(tempOriginalPpItemList.get(i));
		        }
        	}
        	
        	newPpItems = new ArrayList<PpProcurementItem>(reorderedList);
        	
        	if (!ppItems.isEmpty() && ppItems.size() > 0) {
 	        	ppItems.clear();
 	        	this.getManagedBean().getSelectedPlan().setPpProcurementItems(null);
 	        }
        	
 	        this.getManagedBean().getSelectedPlan().setPpProcurementItems(newPpItems);
 	        /*log.info("Displaying ppItems for Plan No=  "+
 	        		this.getManagedBean().getSelectedPlan().getPlanNo());*/
        	
        }

    }
	
	private void checkItemPredecessor(PpProcurementItem ppItem) {
		
		//log.info("checkItemPredecessor");
		
		if (ppItem.isCloneEnabled() && !ppItem.isAllowAnotherPindaan() &&
				!ppItem.isReviseReject() && !ppItem.isRejected() && !ppItem.isSearchReset()) { //28012021@27624
			//log.info("curItem=  "+ppItem.getProcurementTitle());
			ppItem.setChangePencilToNote(true);
			
			//24975@19022021 -  [delete previous code cause bug task list blank]
			}
		}
			
	private void resetPpProcurementItemFields() {
		
		
		List<Long> getFromItems = new ArrayList<Long>();
		
		for (PpProcurementItem pp: this.getManagedBean().getSelectedPlan().getPpProcurementItems()) {
			if (pp.getFromPpItem() != null) {
				getFromItems.add(pp.getFromPpItem());
			}
		}
		
		for (PpProcurementItem pp: this.getManagedBean().getSelectedPlan().getPpProcurementItems()) {	
			if (pp.isOriginal()) {
				// if there are any clones under it
				if (getFromItems.size() > 0) {
					// If a clone is found, the original status will be 9
					if (getFromItems.contains(pp.getProcurementItemId())) {
						pp.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						pp.setCloneEnabled(true);
						pp.setDisableForPindaan(true);
						pp.setStatusEnabled(true);
						
						if (pp.isEnableRevise()) {
							pp.setEnableRevise(false);
						}
					} else {
						pp.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
						pp.setCloneEnabled(false);
						pp.setDisableForPindaan(false);
						pp.setStatusEnabled(false);
					}
				} else {
					pp.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					pp.setCloneEnabled(false);
					pp.setDisableForPindaan(false);
					pp.setStatusEnabled(false);
				}
				
			} else {
				pp.setRecordStatusFlg(Integer.valueOf(DbValueConst.RECORD_STATUS_ACTIVE));				
				pp.setCloneEnabled(false);
				pp.setDisableForPindaan(false);
				pp.setStatusEnabled(false);
			}
			
		}
		
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

//		//25549 - start
//		this.getManagedBean().setDisable(false);
//		//25549 - start
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setPlanId(id);
			//200911@akmal - Enhancement27624 - Start
			reqDTO.setPlanDetSearch(false);
			//200911@akmal - Enhancement27624 - End
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);
			//200911@akmal - Enhancement27624 - Start
			if(this.getManagedBean().isPlanDetailSearch()){
				reqDTO.setPlanDetSearch(true);
				reqDTO = parseSearchBean(this.getManagedBean().getPlanSearchBean(), reqDTO);
			}
			else if(this.getManagedBean().isPlanDetailReset()){
				reqDTO.setPlanDetReset(true);
				reqDTO = parseSearchBean(this.getManagedBean().getPlanSearchBean(), reqDTO);
			}
			//200911@akmal - Enhancement27624 - End
			//27624 - start
			else if(this.getManagedBean().isPlanDetailSearchReset()){
				reqDTO.setPlanDetSearchReset(true);
				reqDTO.setTitleOrDesc(null);
				reqDTO.setCategoryType(null);
				reqDTO.setProcurementType(null);
				reqDTO.setInvitationDateFrom(null);
				reqDTO.setInvitationDateTo(null);
				reqDTO.setUpdatedDateFrom(null);
				reqDTO.setUpdatedDateTo(null);
				reqDTO.setStatus(null);
				reqDTO.getvPmGolist().clear();
			}
			//27624 - end
			resDTO = reqSO.retrievePlanDetById(reqDTO);
			
			if (resDTO.isSuccessful()) {
				ppPlan = resDTO.getPlan();
				if(ppPlan!=null){
					PmOrgProfile pmOrgProfile = PpCommonUtil.retrievePmOrgProfileDtl(ppPlan.getOrgProfile().getOrgProfileId());
					if(pmOrgProfile==null || pmOrgProfile.getOrgProfileId()==null){
						throw new EpException(MessageConst.ERROR_730000);
					}
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
					} else {
						this.getManagedBean().setPpWorkflowStatus(ppPlan.getCurrentStatus());						
						//check ppWorkflowStatus contains approved status
						List <PpWorkflowStatus> ppWorkflowStatuses = retrievePpApprovedStatus(ppPlan);
						if(ppWorkflowStatuses!=null && !ppWorkflowStatuses.isEmpty()){
							this.getManagedBean().setApprovedPlan(true);
							
							log.info("[#12802] Retrieve plan status history for approval - Start");
							//log.info("[#12802] workflowStatusList size: " + ppWorkflowStatuses.size());
							
							List<Timestamp> approvedDtLst = null;
							
							if(ppWorkflowStatuses.size() == 1) {
								ppPlan.setLastApprovedDt(ppWorkflowStatuses.get(0).getCreatedDate());
							} else {
								approvedDtLst = new ArrayList<Timestamp>();
								for(PpWorkflowStatus ppworkflowstatus : ppWorkflowStatuses) {
									//log.info("WorkflowstatusId: " + ppworkflowstatus.getWorkflowStatusId() + ", created on: " + ppworkflowstatus.getCreatedDate());
									approvedDtLst.add(ppworkflowstatus.getCreatedDate());
								}
								//get the last approved date
								ppPlan.setLastApprovedDt(Collections.max(approvedDtLst));
							}
							//log.info("[#12802] last approved date was: " + ppPlan.getLastApprovedDt());
							log.info("[#12802] Retrieve plan status history for approval - End");
						}
					}
					
					//[SIR#12802] - start
					//Remove item from list for status: 2 - Cancelled
					if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
						//log.info("[SIR#12802] - Plan 'E': item list size BEFORE: " + ppPlan.getPpExpenditureItems().size());
						Iterator<PpExpenditureItem> itr = ppPlan.getPpExpenditureItems().iterator(); 
				        while (itr.hasNext()) { 
				        	PpExpenditureItem expenditureItem = (PpExpenditureItem)itr.next(); 
				            if (ParameterConst.RECORD_STATUS_CANCLED.equals(expenditureItem.getRecordStatusFlg().toString())) { 
				            	//log.info("[SIR#12802] - item status is 2, remove from list!");
				                itr.remove(); 
				            }
				        }
						//log.info("[SIR#12802] - Plan 'E': item list size AFTER: " + ppPlan.getPpExpenditureItems().size());
					}
					else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
						//log.info("[SIR#12802] - Plan 'B': item list size BEFORE: " + ppPlan.getPpProcurementItems().size());
						Iterator<PpProcurementItem> itr = ppPlan.getPpProcurementItems().iterator(); 
				        while (itr.hasNext()) { 
				        	PpProcurementItem procurementItem = (PpProcurementItem)itr.next(); 
				            if (ParameterConst.RECORD_STATUS_CANCLED.equals(procurementItem.getRecordStatusFlg().toString())) { 
				            	//log.info("[SIR#12802] - item status is 2, remove from list!");
				                itr.remove(); 
				            }
				        }
						//log.info("[SIR#12802] - Plan 'B': item list size AFTER: " + ppPlan.getPpProcurementItems().size());						
					}
					//[SIR#12802] - end

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
						//24975@04112020@azza - Start
						this.resetPpExpenditureItems(ppPlan.getPpExpenditureItems());
						//24975@04112020@azza - End
						loadEpItems(ppPlan);
						
						//210108@akmal - Enhancement27624 - Start
						
						//210108@akmal - Enhancement27624 - End
						// set searchbean			    					
					}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
						calculatePpGrandTotal();
					}
					updatePlanYearControl();
					
					if (ppPlan.getPpProcurementItems() != null && !ppPlan.getPpProcurementItems().isEmpty()) {
						this.resetPpProcurementItems(ppPlan.getPpProcurementItems());
					}
					
					//201228@akmal - Enhancement27624 - Start
					this.getManagedBean().setMinYear(resDTO.getLastUpdatedDate());
					this.getManagedBean().setMinInvitationYear("01/01/"+ppPlan.getPlanYear().toString());
					this.getManagedBean().setMaxInvitationYear("31/12/"+ppPlan.getPlanYear().toString());
					//201228@akmal - Enhancement27624 - End
				}else{
					throw new EpException(MessageConst.ERROR_730001);
				}
			}
			
		}catch(Exception e){
			log.error(e);
			throw e;
		}finally{
			reqSO = null;
			//27624 - start
			this.getManagedBean().setPlanDetailSearch(false);
			this.getManagedBean().setPlanDetailReset(false);
			this.getManagedBean().setPlanDetailSearchReset(false);
			//27624 - end
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
			    Collections.sort(this.getManagedBean().getEpItemsB(), new Comparator<PpExpenditureItem>() {  
					@Override
						public int compare(PpExpenditureItem o1, PpExpenditureItem o2) {
												
							return (o1.getPmGlAccount().getGlAccCode() + o1.getCreatedDate()).compareTo(o2.getPmGlAccount().getGlAccCode() +  o2.getCreatedDate());							
						}

				    });
				    
				}
			   
			//T
			if(ParameterConst.VOT_TANGGUNGAN.equals(ppExpenditureItem.getVotFundType().getParameterCode())){
				this.getManagedBean().getEpItemsT().add(ppExpenditureItem);
			    Collections.sort(this.getManagedBean().getEpItemsT(), new Comparator<PpExpenditureItem>() {  
					@Override
						public int compare(PpExpenditureItem o1, PpExpenditureItem o2) {
											
						return (o1.getPmGlAccount().getGlAccCode() + o1.getCreatedDate()).compareTo(o2.getPmGlAccount().getGlAccCode() +  o2.getCreatedDate());							
														
					    }

				    });
				    
				}
			//P
			if(ParameterConst.VOT_PEMBANGUNAN.equals(ppExpenditureItem.getVotFundType().getParameterCode())){
				this.getManagedBean().getEpItemsP().add(ppExpenditureItem);
	            Collections.sort(this.getManagedBean().getEpItemsP(), new Comparator<PpExpenditureItem>() {  
					@Override
						public int compare(PpExpenditureItem o1, PpExpenditureItem o2) {
												
						return (o1.getPmGlAccount().getGlAccCode() + o1.getCreatedDate()).compareTo(o2.getPmGlAccount().getGlAccCode() +  o2.getCreatedDate());							
								
						}

				    });
				    
				}
			//S
			if(ParameterConst.VOT_SETIA.equals(ppExpenditureItem.getVotFundType().getParameterCode())){
				this.getManagedBean().getEpItemsS().add(ppExpenditureItem);
			     Collections.sort(this.getManagedBean().getEpItemsS(), new Comparator<PpExpenditureItem>() {  
						@Override
							public int compare(PpExpenditureItem o1, PpExpenditureItem o2) {
													
							return (o1.getPmGlAccount().getGlAccCode() + o1.getCreatedDate()).compareTo(o2.getPmGlAccount().getGlAccCode() +  o2.getCreatedDate());							
						  }

					    });
					    
					}
			//H
			if(ParameterConst.VOT_AMANAH.equals(ppExpenditureItem.getVotFundType().getParameterCode())){
				this.getManagedBean().getEpItemsH().add(ppExpenditureItem);
				 Collections.sort(this.getManagedBean().getEpItemsH(), new Comparator<PpExpenditureItem>() {  
						@Override
							public int compare(PpExpenditureItem o1, PpExpenditureItem o2) {
													
							return (o1.getPmGlAccount().getGlAccCode() + o1.getCreatedDate()).compareTo(o2.getPmGlAccount().getGlAccCode() +  o2.getCreatedDate());							
								
							}

					    });
					    
					}
		     }
		
		
		calculateEpGrandTotal(ParameterConst.VOT_MENGURUS);
		calculateEpGrandTotal(ParameterConst.VOT_TANGGUNGAN);
		calculateEpGrandTotal(ParameterConst.VOT_PEMBANGUNAN);
		calculateEpGrandTotal(ParameterConst.VOT_SETIA);
		calculateEpGrandTotal(ParameterConst.VOT_AMANAH);
		
		log.info("loadEpItems - End");
	}
	
	// #35502 Start - For Audit Trail required
	/**
	 * To check button type for doing audit for plan expenditure
	 * 
	 * @param action
	 */
	private boolean isPEAudit(String action) {
		return BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)
				|| BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action)
				|| BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action);
	}

	/**
	 * To check button type for doing audit for plan procurement
	 * 
	 * @param action
	 */
	private boolean isPPAudit(String action) {
		return BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)
				|| BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action)
				|| BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)
				|| BpmOutcomeConst.BPM_VERIFY.equalsIgnoreCase(action);
	}

	/**
	 * To obtain data from PE if there is an approved status and pass the data
	 * to the next step auditSaveData process
	 * 
	 * @param action
	 * @param reqSPDTO
	 */
	private void processAudit(String action, AuditProcurementPlanSPDTO reqSPDTO) {
		IAuditTrailSO auditProcurementPlanSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT,
				IAuditTrailSO.class);
		try {
			PortletRequest pr = (javax.portlet.PortletRequest) FacesContext.getCurrentInstance().getExternalContext()
					.getRequestMap().get(AuditPPConst.JAVAX_PORTLET_REQ);
			Method method = pr.getClass().getMethod(AuditPPConst.GET_ORIGINAL_HTTP_SERVLET_REQ);
			HttpServletRequest httpServletRequest = (HttpServletRequest) method.invoke(pr, new Object[] {});
			String plid = httpServletRequest.getParameter(AuditPPConst.P_L_ID);
			ThemeDisplay theme = (ThemeDisplay) pr.getAttribute(WebKeys.THEME_DISPLAY);
			Layout selectedLayout = LayoutLocalServiceUtil.getLayout(Long.valueOf(plid));
			String urlliferay = PortalUtil.getLayoutFriendlyURL(selectedLayout, theme);

			if (urlliferay.contains(AuditPPConst.PLAN_MAINTENANCE) && isAuditFlagPlan(auditProcurementPlanSo)) {
				saveAuditData(action, Long.valueOf(1));
			}

			if (urlliferay.contains(AuditPPConst.PLAN_DETAIL) && isAuditFlagPlan(auditProcurementPlanSo)) {
				IAuditProcurementPlanSO reqSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROCUREMENT_PLAN, IAuditProcurementPlanSO.class);
				AuditProcurementPlanReqDTO reqDTO = new AuditProcurementPlanReqDTO();

				reqDTO.setApproveCode(getPendingApprovalCode());
				reqDTO.setPlanId(this.getManagedBean().getPlanId());

				AuditProcurementPlanResDTO resDTO = reqSO.retrieveFirstTimeApprove(reqDTO);

				handleAuditParameterToSP(resDTO, action, reqSPDTO);
			}

		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(AuditPPConst.AUDIT_PROCUREMENT_PLAN, "processAudit", null,
					"ProcurementPlanBackingBean", null, ex);
		}
	}

	/**
	 * To check whether that plan having created code or not
	 * 
	 * @param resDTO
	 * @param action
	 * @param reqSPDTO
	 */
	private void handleAuditParameterToSP(AuditProcurementPlanResDTO resDTO, String action,
			AuditProcurementPlanSPDTO reqSPDTO) {

		if (!resDTO.isSuccessful()) { // success means ada 1 je create code
			recordAudit(action, reqSPDTO);
			return;
		}

		if (isFirstTimeApprove(action)) {
			saveAuditData(action, Long.valueOf(0));
			return;
		}

		if (isSubmitButton(action)) {
			IAuditProcurementPlanSO reqSubmitSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROCUREMENT_PLAN, IAuditProcurementPlanSO.class);
			AuditProcurementPlanReqDTO reqSubmitDTO = new AuditProcurementPlanReqDTO();

			reqSubmitDTO.setApproveCode(getApprovalCode());
			reqSubmitDTO.setPlanId(this.getManagedBean().getPlanId());

			AuditProcurementPlanResDTO resSubmitDTO = reqSubmitSO.retrieveApproveStatus(reqSubmitDTO);

			if (resSubmitDTO.isSuccessful()) {
				saveAuditData(action, Long.valueOf(1));
			}
		}
	}

	/**
	 * To doing audit record
	 * 
	 * @param action
	 * @param reqSPDTO
	 */
	private void recordAudit(String action, AuditProcurementPlanSPDTO reqSPDTO) {
		IAuditProcurementPlanSO reqSubmitSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROCUREMENT_PLAN, IAuditProcurementPlanSO.class);
		AuditProcurementPlanReqDTO reqSubmitDTO = new AuditProcurementPlanReqDTO();

		reqSubmitDTO.setApproveCode(getApprovalCode());
		reqSubmitDTO.setPlanId(this.getManagedBean().getPlanId());

		AuditProcurementPlanResDTO resApproveDTO = reqSubmitSO.retrieveApproveStatus(reqSubmitDTO);
		
		if (reqSPDTO.getCurrentStatusID() != null || resApproveDTO.isSuccessful()) {
			saveAuditData(action, Long.valueOf(1));
		}
	}

	/**
	 * To get only created code
	 */
	private Long getPendingApprovalCode() {
		return ParameterConst.PLAN_TYPE_EXPENDITURE.equals(this.getManagedBean().getSelectedPlan().getPlanType()
				.getParameterCode()) ? AuditPPConst.PE_PENDING_APPROVAL : AuditPPConst.PP_PENDING_VERIFICATION;
	}

	/**
	 * To get only save code
	 */
	private Long getPendingSubmissionCode() {
		return ParameterConst.PLAN_TYPE_EXPENDITURE.equals(this.getManagedBean().getSelectedPlan().getPlanType()
				.getParameterCode()) ? AuditPPConst.PE_PENDING_SUBMISSION : AuditPPConst.PP_PENDING_SUBMISSION;
	}

	/**
	 * To get only approve code
	 */
	private Long getApprovalCode() {
		return ParameterConst.PLAN_TYPE_EXPENDITURE.equals(this.getManagedBean().getSelectedPlan().getPlanType()
				.getParameterCode()) ? AuditPPConst.PE_APPROVAL : AuditPPConst.PP_APPROVAL;
	}

	/**
	 * To check is approve button
	 * 
	 * @param action
	 */
	private boolean isFirstTimeApprove(String action) {
		return BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action);
	}

	/**
	 * To check is submit maintenance button
	 * 
	 * @param action
	 */
	private boolean isSubmitButton(String action) {
		return BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action);
	}

	/**
	 * To check whether the audit configuration is ON and has an approved status
	 * for both plan
	 * 
	 * @param ppWorkflowStatuses
	 * @param auditProcurementPlanSo
	 */
	private boolean isAuditFlagPlan(IAuditTrailSO auditProcurementPlanSo) {
		return auditProcurementPlanSo.isAuditConfig(AuditPPConst.PP_AU_CONF);
	}

	/**
	 * For audit trail save process
	 * 
	 * @param resDTO
	 * @param action
	 */
	private void saveAuditData(String action, Long isNeedAudit) {
		AuditProcurementPlanBackingBeanTemplate auTemplate = new AuditProcurementPlanBackingBeanTemplate();
		auTemplate.saveAuditData(action, isNeedAudit);
	}

	// #35502 End - For Audit Trail required

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
		
		    //28760 - start
		if (ProcurementPlanConst.ACTION_VIEW_BTN.equals(getManagedBean().getBtnValue())) {
		    }else{
			  generateCancelButton(ppPlan);  
   		    }
		   //28760 - end	
		
			this.getManagedBean().getSelectedPlan().setRejected(true);
			if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
						
				for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
					if(DbValueConst.RECORD_STATUS_ACTIVE == epItem.getRecordStatusFlg() && epItem.isRejected()){
						
					   epItem.setEditable(true);
					   //25549@12102020 - start
					   epItem.setReviseReject(true);
					   //25549@12102020 - end
					   epItem.setDisableReviseItem(true);
					
					   this.getManagedBean().getSelectedPlan().setRejected(false);			
					   
					}else 
						if (DbValueConst.RECORD_STATUS_DELETED == epItem.getRecordStatusFlg() && epItem.isRejected()){
						
						epItem.setEditable(false);
						//25549@12102020 - start
						epItem.setReviseReject(true);
						//25549@12102020 - end
						epItem.setDisableReviseItem(true);
						
						this.getManagedBean().getSelectedPlan().setRejected(false);			  
					}else{
						//24975@04112020@azza - start
						epItem.setEditable(false);
						//24975@04112020@azza - end
						//25549@12102020 - start
						epItem.setReviseReject(false);
						//25549@12102020 - end
					}
	         	}
	
				
				if(this.getManagedBean().getSelectedPlan().isRejected()){
					for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
						if(DbValueConst.RECORD_STATUS_ACTIVE == epItem.getRecordStatusFlg()){
							epItem.setEditable(true);
							//25549@12102020 - start
							epItem.setReviseReject(true);
							//25549@12102020 - end
							epItem.setDisableReviseItem(true);
						}else{
							epItem.setEditable(false);
							//25549@12102020 - start
							epItem.setReviseReject(false);
							//25549@12102020 - end
						}
					}
				}
				
				//[SIR#12802] - disabled item to be deleted if the item has an approved history				
				if(ppPlan.getLastApprovedDt() != null) {
					log.info("[SIR#12802] - Check item for approved status - Start");
					//log.info("[SIR#12802] - plan last approved date: " + ppPlan.getLastApprovedDt());
					
					//28760@11122020@azza - Start
					this.getManagedBean().setFlagCancelButton(false);
					//28760@11122020@azza - End
					
					//log.info("[SIR#28760] - CancelFlag " + this.getManagedBean().isFlagCancelButton());

					
					for(PpExpenditureItem epItem: ppPlan.getPpExpenditureItems()){
						/*log.info("[SIR#12802] - item ID: " + epItem.getExpenditureItemId());
						log.info("[SIR#12802] - item created date: " + epItem.getCreatedDate());*/
						if(ppPlan.getLastApprovedDt().equals(epItem.getCreatedDate()) 
								|| ppPlan.getLastApprovedDt().after(epItem.getCreatedDate())){
							//log.info("[SIR#12802] - item has an approval history");
							epItem.setSelectable(false); //disabled checkbox	
							//#25975
							epItem.setEditable(false); 					 
						} else {
							//log.info("[SIR#12802] - item pending for approval");
							epItem.setSelectable(true); //enabled checkbox
					   }
					}
					log.info("[SIR#12802] - Check item for approved status - End");
				}
				
			} else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){

				for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
					/*log.info("ppItem= "+ppItem.getProcurementTitle()+" berstatus "+ppItem.getRecordStatusFlg()+
							". Rejected? "+ppItem.isRejected());*/
					if(DbValueConst.RECORD_STATUS_ACTIVE == ppItem.getRecordStatusFlg() && ppItem.isRejected()){						
						ppItem.setEditable(true);
						//25549@12102020 - start
						ppItem.setReviseReject(true);
						//25549@12102020 - end
						//24975@04112020@azza - start
						ppItem.setEnableRevise(true);
						//24975@04112020@azza - end
						
						this.getManagedBean().getSelectedPlan().setRejected(false);
						//24975@04112020@azza - start
					} else 
						if (DbValueConst.RECORD_STATUS_DELETED == ppItem.getRecordStatusFlg() && ppItem.isRejected()){
						
							ppItem.setEditable(false);
						
							ppItem.setReviseReject(true);
					
							ppItem.setEnableRevise(true);
						
						    this.getManagedBean().getSelectedPlan().setRejected(false);			  
					}else{
							ppItem.setEditable(false);
							//25549@12102020 - start
							ppItem.setReviseReject(true);
							//25549@12102020 - end
							
							
							//log.info("originalfromppItem " + ppItem.isOriginal() + ppItem.getFromPpItem());
						
							if (!ppItem.isOriginal() && ppItem.getFromPpItem() != null) {
								
								ppItem.setCloneEnabled(false);
								ppItem.setDisableForPindaan(true);
								ppItem.setReasonForUpdateEnabled(false);
								ppItem.setBrandNew(true);
								
								if(ppPlan != null && ppPlan.getStatus() != null && 
										(ProcurementPlanConst.PPP_APPROVED.equals(ppPlan.getStatus().getStatusId()))) {
									ppItem.setAllowAnotherPindaan(true);
								}
								//log.info("allowAnotherPindaan for "+ ppItem.getProcurementTitle()+" is "+ppItem.isAllowAnotherPindaan());
							    }							
					       }
				       }
				//24975@04112020@azza - end
				if(this.getManagedBean().getSelectedPlan().isRejected()){
					for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
						if(DbValueConst.RECORD_STATUS_ACTIVE == ppItem.getRecordStatusFlg()){							
							ppItem.setEditable(true);
							//25549@12102020 - start
							ppItem.setReviseReject(true);
							//25549@12102020 - end
							//24975@04112020@azza - start
							ppItem.setEnableRevise(true);
							//24975@04112020@azza - end
						}else{
							ppItem.setEditable(false);
							//25549@12102020 - start
							ppItem.setReviseReject(false);
							//25549@12102020 - end
						}
					}
				}
				
				//[SIR#12802] - disabled item to be deleted if the item has an approved history
				if(ppPlan.getLastApprovedDt() != null) {
					log.info("[SIR#12802] - Check item for approved status - Start");
					//log.info("[SIR#12802] - plan last approved date: " + ppPlan.getLastApprovedDt());
					
					//28760@11122020@azza - Start
					this.getManagedBean().setFlagCancelButton(false);
					//28760@11122020@azza - End
					
					//log.info("[SIR#28760] - CancelFlag " + this.getManagedBean().isFlagCancelButton());

					
					
					for(PpProcurementItem ppItem: ppPlan.getPpProcurementItems()){
						/*log.info("[SIR#12802] - item ID: " + ppItem.getProcurementItemId());
						log.info("[SIR#12802] - item created date: " + ppItem.getCreatedDate());*/
						if(ppPlan.getLastApprovedDt().equals(ppItem.getCreatedDate()) 
								|| ppPlan.getLastApprovedDt().after(ppItem.getCreatedDate())){
							//log.info("[SIR#12802] - item has an approval history");
							ppItem.setSelectable(false); //disabled checkbox
							//#25975
							ppItem.setEditable(false); 
						} else {
							//log.info("[SIR#12802] - item pending for approval");
							ppItem.setSelectable(true); //enabled checkbox
							//25549@09102020 - start
							
							ppItem.setAllowAnotherPindaan(false); // 31012021@27624
							/*log.info("item yg menanti: "+ppItem.getProcurementTitle()+
									",isEditable= " +ppItem.isEditable());*/
							//25549@09102020 - end
						}
						/*log.info("item= "+ppItem.getProcurementTitle()+
								"enableRevise= "+ppItem.isEnableRevise());*/
					}
					log.info("[SIR#12802] - Check item for approved status - End");
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
	@SuppressWarnings("unchecked")
	public void loadPlanRemark(List<PpRemark> remarkList) {
 		log.info("loadPlanRemark - Start");
 		
		this.getManagedBean().setSelectedRemarks(remarkList);
		
		//24975@04112020@azza - start
		@SuppressWarnings("rawtypes")
		Comparator compare = Collections.reverseOrder(new sortRemark()); 
        Collections.sort(this.getManagedBean().getSelectedRemarks(), compare); 
    	//24975@04112020@azza - End 
        
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
				}else {
					//24975@24102020@azza - start
				  if(this.getManagedBean().getSelectedRemarks().get(0).getPpWorkflowStatus()!=null &&
					  this.getManagedBean().getSelectedPlan().getCurrentStatus().getWorkflowStatusId()!=(
					  this.getManagedBean().getSelectedRemarks().get(0).getPpWorkflowStatus().getWorkflowStatusId()) &&
					  this.getManagedBean().getPpWorkflowStatus().getPmStatus().getStatusId().equals(ProcurementPlanConst.PPE_PENDING_SUBMISSION) ||
					  this.getManagedBean().getPpWorkflowStatus().getPmStatus().getStatusId().equals(ProcurementPlanConst.PPP_PENDING_SUBMISSION)){
					  this.getManagedBean().getSelectedRemarks().get(0).setEditable(true);
					  newRemark = false;
						//24975@24102020@azza - end
				   }	
			   }
			}
		}

		if(newRemark){
			PpRemark remark = new PpRemark();
			remark.setEditable(true);
			remark.setRemarkType(ProcurementPlanConst.REMARK_TYPE_REMARK);
			remark.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
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
			//210108@akmal - Enhancement27624 - Start
			SelectItem[] items;
			Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
			Integer nextYear = currentYear + 1;
			//#32730 - add isPreYear
			if(this.getManagedBean().isAudit() || this.getManagedBean().isPreYear()){
				currentYear = startyear;
				int diff = nextYear - currentYear + 1;
				items = new SelectItem[diff+1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				
				for(int i=1; i <= diff; i++){
					items[i] = new SelectItem(currentYear.longValue(), currentYear.toString());
					currentYear = currentYear+1;
				}
				
				return items;
			} else {
				currentYear = Calendar.getInstance().get(Calendar.YEAR);
				nextYear = currentYear + 1;
				items = new SelectItem[3];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				items[1] = new SelectItem(currentYear.longValue(), currentYear.toString());
				items[2] = new SelectItem(nextYear.longValue(), nextYear.toString());
				return items;
			}
			
			//210108@akmal - Enhancement27624 - End
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
		// #36107 - Start
		if (ppPlanActors != null) {
			List<PmUser> target = new ArrayList<>();
			for (PpPlanActor ppPlanActor : ppPlanActors) {
				List<PmUser> userCheck = PpCommonUtil
						.getUserRolesByUserIdAndOrg(ppPlanActor.getPmUser()
								.getUserId(), orgProfileId, roleCode);
				if (DbValueConst.RECORD_STATUS_ACTIVE == ppPlanActor.getRecordStatusFlg()
						&& roleCode.equals(ppPlanActor.getRoleCode().getRoleCode()) && !userCheck.isEmpty()) {
					target.add(ppPlanActor.getPmUser());
				}
			}
			this.getManagedBean().getVerifierModel().setTarget(target);
		}
		// #36107 - End
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
		// #36107 - Start
		if (ppPlanActors != null) {
			List<PmUser> target = new ArrayList<>();
			for (PpPlanActor ppPlanActor : ppPlanActors) {
				List<PmUser> userCheck = PpCommonUtil
						.getUserRolesByUserIdAndOrg(ppPlanActor.getPmUser()
								.getUserId(), ptjOrgProfileId, roleCode);
				if (DbValueConst.RECORD_STATUS_ACTIVE == ppPlanActor.getRecordStatusFlg()
						&& roleCode.equals(ppPlanActor.getRoleCode().getRoleCode()) && !userCheck.isEmpty()) {
					target.add(ppPlanActor.getPmUser());
				}
			}
			this.getManagedBean().getApproverModel().setTarget(target);
		}
		// #36107 - End
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
			//24975@24102020@azza - start
			int itemIndex = 0;
			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())){
				for(PpProcurementItem ppProcurementItem:ppPlan.getPpProcurementItems()){
					if(ppProcurementItem.getPpRejectReasons()!=null && !ppProcurementItem.getPpRejectReasons().isEmpty()){
						PpRejectReason rejectReason = ppProcurementItem.getPpRejectReasons().get(0);
						if(rejectReason!=null && !StringUtils.isBlank(rejectReason.getReasonDesc())){
							rejectReason.setItemSeq(itemIndex);//&& rejectReason.isBrandNew() add this for phase 2
							rejectReason.setDocId(ppProcurementItem.getProcurementItemId());
							rejectReason.setDocType(ParameterConst.DOCTYPE_PLAN_PROCUREMENT);
							rejectReasons.add(rejectReason);
						}else{	
							
							List<PpRejectReason> PpRejectReasonDel = PpCommonUtil.retrieveRejectReason(ppProcurementItem.getProcurementItemId(), ParameterConst.DOCTYPE_PLAN_PROCUREMENT);
						    
							for(PpRejectReason rejectReasonDel : PpRejectReasonDel){
								 if(rejectReasonDel.getDocId().equals(ppProcurementItem.getProcurementItemId())){
									if(rejectReasonDel.getReasonDesc()!=null && rejectReason.getReasonDesc().isEmpty()){			
										rejectReasonDel.setItemSeq(itemIndex);//&& rejectReason.isBrandNew() add this for phase 2
										rejectReasonDel.setDocId(ppProcurementItem.getProcurementItemId());
										rejectReasonDel.setDocType(ParameterConst.DOCTYPE_PLAN_PROCUREMENT);
										rejectReasonDel.setReasonDesc(rejectReasonDel.getReasonDesc());
										rejectReasonDel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
										rejectReasons.add(rejectReasonDel);
									 }
								 }
							 }
						}
					}
					itemIndex++;
				}
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())){
				for(PpExpenditureItem ppExpenditureItem:ppPlan.getPpExpenditureItems()){
					if(ppExpenditureItem.getPpRejectReasons()!=null && !ppExpenditureItem.getPpRejectReasons().isEmpty()){
						PpRejectReason rejectReason = ppExpenditureItem.getPpRejectReasons().get(0);
						if(rejectReason!=null &&!StringUtils.isBlank(rejectReason.getReasonDesc())){
							rejectReason.setItemSeq(itemIndex);////&& rejectReason.isBrandNew() add this for phase 2
							rejectReason.setDocId(ppExpenditureItem.getExpenditureItemId());
							rejectReason.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
							rejectReasons.add(rejectReason);
						}else{	
							
							List<PpRejectReason> PpRejectReasonDel = PpCommonUtil.retrieveRejectReason(ppExpenditureItem.getExpenditureItemId(), ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
										
							for(PpRejectReason rejectReasonDel : PpRejectReasonDel){
								 if(rejectReasonDel.getDocId().equals(ppExpenditureItem.getExpenditureItemId())){
									 if(rejectReasonDel.getReasonDesc()!=null && rejectReason.getReasonDesc().isEmpty()){			
										rejectReasonDel.setItemSeq(itemIndex);//&& rejectReason.isBrandNew() add this for phase 2
										rejectReasonDel.setDocId(ppExpenditureItem.getExpenditureItemId());
										rejectReasonDel.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
										rejectReasonDel.setReasonDesc(rejectReasonDel.getReasonDesc());
										rejectReasonDel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
										rejectReasons.add(rejectReasonDel);
									 }
								 }
							 }
						}
					}
					itemIndex++;
				}
			}
		}
		return rejectReasons;
	}
	//24975@24102020@azza - end

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
							if(StringUtils.isBlank(justification.getRemark())){
								justification.setRemark(" ");
							}
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
							
							justifications.add(justification);
						}
					}
					itemIndex++;
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
					if(ppExpenditureItem.getPpRemarks()!=null && !ppExpenditureItem.getPpRemarks().isEmpty() 
							&& !this.getManagedBean().isPlanDetailSearch() && !this.getManagedBean().isPlanDetailReset()){//28012021@27624 - start
						PpRemark itemRemark = ppExpenditureItem.getPpRemarks().get(0);
						if(itemRemark!=null && !StringUtils.isBlank(itemRemark.getRemark())){ //&& itemRemark.isBrandNew() add this for phase 2
							itemRemark.setItemSeq(itemIndex);
							itemRemark.setDocId(ppExpenditureItem.getExpenditureItemId());
							itemRemark.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
							ppExpenditureItem.setSearchReset(false); //28012021@27624 - end
							if(itemRemark.getRoleCode() ==null || StringUtils.isBlank(itemRemark.getRoleCode().getRoleCode())){
								log.info("set setRemarkRole");
								PmRole roleCode = new PmRole();
								//24975@041112020@azza - Start
								roleCode.setRoleCode(getOneRoleByStatus(ppPlan));
								//24975@041112020@azza - End
								//roleCode.setRoleCode(RoleCode.PLAN_OFFICER.value());
								//roleCode.setRoleCode(RoleCode.PLAN_EXPEN_OFFICER.value());
								itemRemark.setRoleCode(roleCode);
								itemRemark.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
							}
							itemRemarks.add(itemRemark);
						   }//28012021@27624 
						}else{	
							List<PpRemark> PpItemRemarkDel = PpCommonUtil.retrieveRemark(ppExpenditureItem.getExpenditureItemId(), ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
										
							for(PpRemark itemRemarkDel : PpItemRemarkDel){
								PpRemark itemRemark = ppExpenditureItem.getPpRemarks().get(0);//28012021@27624 
								 if(itemRemarkDel.getDocId().equals(ppExpenditureItem.getExpenditureItemId())){
									 /*log.info("first itemRemarkDel1" + itemRemark.getRemark() + itemRemarkDel.getRemark());*/
									 if(itemRemarkDel.getRemark()!=null 
											 && itemRemark.getRemark()== null
											 || itemRemark.getRemark().isEmpty()){
										/* log.info("first itemRemarkDel" + itemRemarkDel.getRemark());*/
										itemRemarkDel.setItemSeq(itemIndex);
										itemRemarkDel.setDocId(ppExpenditureItem.getExpenditureItemId());
										itemRemarkDel.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
										itemRemarkDel.setRemark(itemRemarkDel.getRemark());
										itemRemarkDel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);

									 }
									 itemRemarks.add(itemRemarkDel);
								 }
							 }
						}
					itemIndex++;
					}
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
		// #35310 - Start
		if (ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan
				.getPlanType().getParameterCode())) {
			validatePpItem();
			// PP20150108-0005
			if (!ppPlan.isBrandNew()
					&& !(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(action) && ProcurementPlanConst.PPP_PENDING_SUBMISSION
							.equals(this.getManagedBean().getPpWorkflowStatus()
									.getPmStatus().getStatusId()))
					&& !(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(action))) {
				if (this.getManagedBean().getVerifierModel().getTarget() == null
						|| this.getManagedBean().getVerifierModel().getTarget()
								.isEmpty()) {
					throw new EpException(MessageConst.ERROR_730017);
				}
				if (this.getManagedBean().getApproverModel().getTarget() == null
						|| this.getManagedBean().getApproverModel().getTarget()
								.isEmpty()) {
					throw new EpException(MessageConst.ERROR_730018);
				}
			}

		} else if (ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan
				.getPlanType().getParameterCode())) {
			// if(ppPlan.getVotFundType().getParameterId()==null){
			// throw new EpException(MessageConst.ERROR_730019);
			// }
			validateAllEpItems();
			// PP20150108-0005
			if (!ppPlan.isBrandNew()
					&& !(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(action) && ProcurementPlanConst.PPE_PENDING_SUBMISSION
							.equals(this.getManagedBean().getPpWorkflowStatus()
									.getPmStatus().getStatusId()))
					&& !(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(action))) {
				if (this.getManagedBean().getApproverModel().getTarget() == null
						|| this.getManagedBean().getApproverModel().getTarget()
								.isEmpty()) {
					throw new EpException(MessageConst.ERROR_730018);
				}
			}
		}
		// #35310 - End

		
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
				//24975@04112020@azza - Start
				for(PpExpenditureItem epItem: this.getManagedBean().getEpItemsB()){
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
				for(PpExpenditureItem epItem: this.getManagedBean().getEpItemsT()){
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
				for(PpExpenditureItem epItem: this.getManagedBean().getEpItemsP()){
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
				for(PpExpenditureItem epItem: this.getManagedBean().getEpItemsS()){
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
				for(PpExpenditureItem epItem: this.getManagedBean().getEpItemsH()){
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
				//24975@04112020@azza - End
			}
			
			
			if(!isItemRejected){
				//overall remark is required
				remarkIsRequired();
			}
			
		}else if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(action)){ //28760 - start
			/*log.info("remark cancel button " + this.getManagedBean().getSelectedRemarks().get(0).getRemark());*/

			if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				if(this.getManagedBean().getSelectedRemarks()!=null && !this.getManagedBean().getSelectedRemarks().isEmpty()){
					if(StringUtils.isBlank(this.getManagedBean().getSelectedRemarks().get(0).getRemark())){
						throw new EpException(MessageConst.ERROR_775158);
					}
				}
			}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				if(this.getManagedBean().getSelectedRemarks()!=null && !this.getManagedBean().getSelectedRemarks().isEmpty()){
					if(StringUtils.isBlank(this.getManagedBean().getSelectedRemarks().get(0).getRemark())){
						throw new EpException(MessageConst.ERROR_775158);
					}
				}
			}
		} //28760 - end
		
		//24975@19112020 - start
		checkUpdateForReasonIndicator(ppPlan);
		//24975@19112020 - end
		
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
		
//		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(planType)){
//			/* additional rules for PP if any*/
//			
//		}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(planType)){
//			/* additional rules for EP if any*/
//
//		}
		
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
		//#24975 azza - start
		ChekDuplicatePpItemClone(epItems);
		//#24975 azza - end
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
	//210108@akmal - Enhancement27624 - Start
	public void onSelectCategorySearch(Long id){
		log.info("The category search id " + id);
		List<PmProcurementCatType> pmProcCats= PpCommonUtil.retrieveProcurementCatTypeList(getFacesScopes().getUserLocale().getLanguage());
		if(id.equals(Long.valueOf(0L))){ //09022021@24975 - start
			this.getManagedBean().setSelectedCatSearch(null);
		}else{
			for(PmProcurementCatType pmProcCat : pmProcCats ){
				if(pmProcCat.getProcurementCat().getParameterId().equals(id)){
					this.getManagedBean().setSelectedCatSearch(pmProcCat.getProcurementCat());
					break;
				}
			}
		}//09022021@24975 - end
	}
	//210108@akmal - Enhancement27624 - End
	
	public void onSelectCategory(Long id, int index){
		log.info("The category id " + id);
		List<PmProcurementCatType> pmProcCats= PpCommonUtil.retrieveProcurementCatTypeList(getFacesScopes().getUserLocale().getLanguage());
		for(PmProcurementCatType pmProcCat : pmProcCats ){
			if(pmProcCat.getProcurementCat().getParameterId().equals(id)){
				this.getManagedBean().getSelectedPlan().getPpProcurementItems().get(index).setProcurementCat(pmProcCat.getProcurementCat());
				break;
			}
		}
	}
	public void onSelectCategoryType(Long id, int index){
		//log.info("The category type id " + id);
		Long procurementCatId = this.getManagedBean().getSelectedPlan().getPpProcurementItems().get(index).getProcurementCat().getParameterId();
		List<PmProcurementCatType> pmProcCats= 	PpCommonUtil.retrieveProcurementTypeListByCatId(getFacesScopes().getUserLocale().getLanguage(), procurementCatId);
		for(PmProcurementCatType pmProcCat : pmProcCats ){
			if(pmProcCat.getProcurementType().getParameterId().equals(id)){
				this.getManagedBean().getSelectedPlan().getPpProcurementItems().get(index).setProcurementType(pmProcCat.getProcurementType());
				break;
			}
		}
	}
	
	//#34156 - Start
	public void onSelectCategoryTypeNewLabel(Long id, int index, boolean NewLabcatMethodType){		
		//log.info("The category type id " + id);
		//#34422 09/11/2023 - Start
		this.getManagedBean().getSelectedPlan().getPpProcurementItems().get(index).setProcurementType(new PmParameter());
		//#34422 09/11/2023 - End
		Long procurementCatId = this.getManagedBean().getSelectedPlan().getPpProcurementItems().get(index).getProcurementCat().getParameterId();
		List<PmProcurementCatType> pmProcCats= 	PpCommonUtil.retrieveProcurementTypeListByCatIdNewLabel(getFacesScopes().getUserLocale().getLanguage(), procurementCatId, NewLabcatMethodType);
		for(PmProcurementCatType pmProcCat : pmProcCats ){
			if(pmProcCat.getProcurementType().getParameterId().equals(id)){
				this.getManagedBean().getSelectedPlan().getPpProcurementItems().get(index).setProcurementType(pmProcCat.getProcurementType());
				break;
			}
		}
	}
	//#34156 - End
	
	public String savePlan(String action) throws Exception{	
		//LOG : 26549 : SLA Monitoring
		long startTime = System.currentTimeMillis();
		log.info("savePlan - START");
		log.info("the action: " + action);
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
		//#24975
		List <PpUpdateReason> updateReasons = null;
		AuditProcurementPlanSPDTO reqSPDTO = new AuditProcurementPlanSPDTO(); //#35502
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			ppPlan = this.getManagedBean().getSelectedPlan();
			brandNew = ppPlan.isBrandNew();
					
			//Validation
			validationPlanSubmit(action, ppPlan);
			if(brandNew){
				//comment this for testing purpose only
				validationPlanAvailability(ppPlan);  
			}
			
			//Get All EP if PlanType == E 
			if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				getAllEpItem(ppPlan);
			}
			
			//[SIR#12802]- Get final item list
			log.info("Call method getFinalItemList...");
			getFinalItemList(ppPlan);			
			
			//Get All Values
			updatePlanActors(ppPlan);
			
			//Get Remark at Plan Level
			remark = getRemark(ppPlan);
			
			//Get Remark at Item Level
			itemRemarks = getAllItemRemarks(ppPlan); 
			
			updateReasons = deleteUpdateReasons(ppPlan);
			
			//Get Justification 
			if (ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())) {
				justifications = getAllJustifications(ppPlan);
			}
			
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
			
			//28760 -start
			if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(action)) {
				ppPlan.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
				ppPlan.setCancel(DbValueConst.RECORD_STATUS_ACTIVE );
			}else{
				ppPlan.setCancel(DbValueConst.RECORD_STATUS_SUSPENDED );
			}
			//28760 -end

			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setPpRemark(remark);
			reqDTO.setPpRemarks(itemRemarks);
			//24975 - start
			if (ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())) {
				reqDTO.setUpdateReasonList(updateReasons);
			}
			//24975 - end
			if (ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())) {
				reqDTO.setJustifications(justifications);
			}
			reqDTO.setPpPlan(ppPlan);
			reqDTO.setOutcome(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setUserRole(getUserRoleValue());
			if(!ppPlan.isBrandNew()){
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
			}	
			reqDTO.setDocTitle(PpCommonUtil.getDocTitle(ppPlan));
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);
			
			if (this.getManagedBean().getDeletePpItems() == null) {
				this.getManagedBean().setDeletePpItems(new ArrayList<PpProcurementItem>());
			}
			log.info("Before save, getDeletePpItems= "+this.getManagedBean().getDeletePpItems().size()); 
			reqDTO.setDeletedPpItemList(this.getManagedBean().getDeletePpItems());
			
			//#35502 Start
			AuditProcurementPlanReqDTO reqAuDTO = new AuditProcurementPlanReqDTO();
			reqAuDTO.setApproveCode(getPendingSubmissionCode());
			reqAuDTO.setPlanId(this.getManagedBean().getPlanId());
			
			IAuditProcurementPlanSO reqAuSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROCUREMENT_PLAN, IAuditProcurementPlanSO.class);
			AuditProcurementPlanResDTO resAuDTO = reqAuSO.retrieveFirstTimeApprove(reqAuDTO); //sebab untuk selepas save

			if (!brandNew) {
				Long statusId = ppPlan.getStatus().getStatusId();
				boolean isPendingSubmission = statusId.equals(AuditPPConst.PE_PENDING_SUBMISSION)
						|| statusId.equals(AuditPPConst.PP_PENDING_SUBMISSION);
				this.getManagedBean().setAuditReviseStatusID(statusId);
				
				if (!isPendingSubmission || resAuDTO == null) {
			        reqSPDTO.setCurrentStatusID(statusId);
			    }
			}
			//#35502 End
				
			//SAVE Plan to DB
			resDTO = reqSO.savePlan(reqDTO);
			//resDTO.setSuccessful(false); // for testing only...	
			docType = retrievePlanDocType(ppPlan);
			
			this.getManagedBean().getDeletePpItems().clear();
			//log.info("After save, getDeletePpItems= "+this.getManagedBean().getDeletePpItems().size()); 
				
			if (resDTO.isSuccessful()) {
				//LOG : 26549 : SLA Monitoring
				SLAUtils.getInstance().logSLAInfo("PM","PS",
						"ProcurementPlanBackingBean.savePlan", startTime);
				log.info("Record saved successfully");
				
				//#35502 Start - For Audit Trail to SP
				/**
				 * For Audit Trail Procurement Plan process
				 */
				boolean isPE = ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode());
				boolean isPEAudit = isPEAudit(action);
				boolean isPP = ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode());
				boolean isPPAudit = isPPAudit(action);

				if ((isPE && isPEAudit) || (isPP && isPPAudit)) {
					processAudit(action, reqSPDTO);
				}
				//#35502 End - For Audit Trail to SP
				
				//210111@akmal - Enhancement27624 - Start
				boolean proceed = true;
				if(this.getManagedBean().isPlanDetailReset()) proceed=false;
				if(this.getManagedBean().isPlanDetailSearch()) proceed=false;
				if(proceed){
					if(this.getManagedBean().getTaskId()!=null){
						loadPlanDetails(resDTO.getPlan().getPlanId(), false);
					}else{
						loadPlanDetails(resDTO.getPlan().getPlanId(), true);
					}
					
					String nextRole = null;
					String messageId = MessageConst.INFO_700005;
					if(BpmOutcomeConst.BPM_SUBMIT.equals(action)){
						messageId = MessageConst.INFO_730031;
						if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
							nextRole = RoleCode.PLAN_APPROVER.value();
						}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
							nextRole = RoleCode.PLAN_VERF_OFFICER.value();
	//						if(ppPlan.getCurrentStatus()!= null 
	//								&& ppPlan.getCurrentStatus().getPmStatus() != null 
	//								&& ProcurementPlanConst.PPP_PENDING_REVISION
	//								.equals(ppPlan.getCurrentStatus().getPmStatus().getStatusId())){
	//							nextRole = resDTO.getPreviousActionedRoleCode();						
	//						}
						}
					}else if(BpmOutcomeConst.BPM_REVISE.equals(action)){
						messageId = MessageConst.INFO_730032;
						nextRole = RoleCode.PLAN_OFFICER.value();
					}else if(BpmOutcomeConst.BPM_VERIFY.equals(action)){
						messageId = MessageConst.INFO_730031;
						nextRole = RoleCode.PLAN_APPROVER.value();
					}else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
						messageId = MessageConst.INFO_700004;
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						messageId = MessageConst.INFO_700007;
					} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) { //28760 - Start
						messageId = MessageConst.INFO_723005;
					}//28760 - End
					
	//				else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
	//					messageId = MessageConst.INFO_700008;
	//				}
					
					if(StringUtils.isNotBlank(nextRole)){
						PmRoleDesc roleDesc = RoleHelper.getInstance().getRoleDescByCode(nextRole);
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(messageId, 
										new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType), resDTO.getPlan().getPlanNo(),roleDesc.getRoleName()}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					}else{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(messageId, 
										new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType), resDTO.getPlan().getPlanNo()}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					}
				
					if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) 
							|| (StringUtils.isBlank(this.getManagedBean().getTaskId()) 
									&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE))) { //PP20150108-0007
						renewSesionObject();
						return CommonUIConst.ACKNOWLEDGE_PAGE;
					}
				}
				//210111@akmal - Enhancement27624 - End
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
	
	//210121@akmal - Enhancement27624 - Start
	public void saveSubPlan(String action) throws Exception{
		long startTime = System.currentTimeMillis();
		log.info("saveSubPlan - START");
//		log.info("the action: " + action);
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
		List <PpUpdateReason> updateReasons = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			ppPlan = this.getManagedBean().getSelectedPlan();
			
//			validationPlanSubmit(action, ppPlan);
			
			if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				validateAllEpItems();
				getAllEpItem(ppPlan);
			}
			else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(ppPlan.getPlanType().getParameterCode())){
				validatePpItem();
			}
			
			log.info("Call method getFinalItemList...");
			getFinalItemList(ppPlan);
			itemRemarks = getAllItemRemarks(ppPlan); 
			
			updateReasons = deleteUpdateReasons(ppPlan);
			
			//Get Justification 
			if (ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())) {
				justifications = getAllJustifications(ppPlan);
			}
						
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setPpRemark(remark);
			reqDTO.setPpRemarks(itemRemarks);
			
			if (ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())) {
				reqDTO.setUpdateReasonList(updateReasons);
			}
			if (ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())) {
				reqDTO.setJustifications(justifications);
			}			
						
						
			reqDTO.setPpPlan(ppPlan);
			reqDTO.setOutcome(action);
			reqDTO.setUserRole(getUserRoleValue());
			reqDTO.setDocTitle(PpCommonUtil.getDocTitle(ppPlan));
			reqDTO.setSaveSubPlan(true);
			if(!ppPlan.isBrandNew()){
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
			}
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);			
	
			if (this.getManagedBean().getDeletePpItems() == null) {
				this.getManagedBean().setDeletePpItems(new ArrayList<PpProcurementItem>());
			}
			log.info("Before save, getDeletePpItems= "+this.getManagedBean().getDeletePpItems().size()); 
			reqDTO.setDeletedPpItemList(this.getManagedBean().getDeletePpItems());

//			reqSO.
			
			resDTO = reqSO.savePlan(reqDTO);
	
			this.getManagedBean().getDeletePpItems().clear();
			log.info("After save, getDeletePpItems= "+this.getManagedBean().getDeletePpItems().size());
			if (resDTO.isSuccessful()) {
			}
		}
		catch(Exception e){
			log.error(e.getStackTrace());
		}
		finally{
			log.info("saveSubPlan - End");
		}
	}
	//210121@akmal - Enhancement27624 - End
	
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
			reason.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
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
		RequestContext.getCurrentInstance().execute("hideLovMsg();");
		validateEpItem(paramCode); // disabled this if no validation required at this stage
		PpExpenditureItem epItem = new PpExpenditureItem();
		//24975@22012021 - start
		epItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		epItem.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
		log.info("setCreatedDate " + new Timestamp(System.currentTimeMillis()) + " setCreatedBy " + getFacesScopes().getUserSessionBean().getUserId());
		//24975@22012021 - end
		epItem.setPmGlAccount(new PmGlAccount());
		epItem.setVotFundType(new PmParameter());
		epItem.setEditable(true);
		epItem.setPpPlan(this.getManagedBean().getSelectedPlan());
		 //25549@09102020 - start
		epItem.setReviseReject(true);
		epItem.setOriginal(true);
		epItem.setDisableReviseItem(true);
	    //25549@09102020 - end
			
		
		
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
			//03122020@24975 - Start
			List<PpExpenditureItem> ppItemNotUsingEp = new ArrayList<PpExpenditureItem>();
			int indicatorIndex = 0;
			//03122020@24975 - End
			for(PpExpenditureItem epItem: epItems){
				if(epItem.getRecordStatusFlg() != null){
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
						//01032021@Bug #30476 - start
						if(epItem.getNonEpAmt()==null || epItem.getNonEpAmt().intValue()<0
								|| epItem.getNonEpAmt().doubleValue()<-epItem.getNonEpAmt().doubleValue()){
							throw new EpException(MessageConst.ERROR_730006, new String[]{epItem.getPmGlAccount().getGlAccCode()});
						}	//01032021@Bug #30476 - end
						
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
								//03122020@24975 - Start
								epItem.setNotUsingEp(true);
								ppItemNotUsingEp.add(epItem);
								indicatorIndex++;
								//03122020@24975 - End
							}
						}
						//#25549@24022021 - remove 
						soIds.add(epItem.getPmGlAccount().getGlAccountId());
					}
				}
			}
			//03122020@24975 - Start
			if (indicatorIndex > 0) {
				throw new EpException(MessageConst.ERROR_730007);
			}//03122020@24975 - End
			
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
		//List<PpExpenditureItem> deletedItems = new ArrayList<PpExpenditureItem>();
		List <PpExpenditureItem> epItems = new ArrayList<PpExpenditureItem>();
		
		//VOT-B
		if(ParameterConst.VOT_MENGURUS.equals(paramCode)){
			epItems = this.getManagedBean().getEpItemsB();
			
			if(this.getManagedBean().getDeleteEpItemsB()==null){
				this.getManagedBean().setDeleteEpItemsB(new ArrayList<PpExpenditureItem>());
			}
			
			//add deleted item to list
			for(PpExpenditureItem expenditureItem: epItems){
				//log.info("[SIR#12802] - VOT-B: Expenditure item delete status:" + expenditureItem.isDeleted());
				//if(expenditureItem.isBrandNew() && expenditureItem.isDeleted()){
				//[SIR#12802] - start
				if(expenditureItem.isDeleted()){
					
					//[#24975] - azza - start
					for(PpExpenditureItem expItem: epItems){
					if (expItem.getExpenditureItemId() != null){
						if (expItem.getExpenditureItemId().equals(expenditureItem.getFromPpItem())){
							//For cloning
							expItem.setCloneEnabled(false);
							expItem.setDisableForPindaan(false);
							//For update reason
							expItem.setReasonForUpdateEnabled(true);
							expItem.setBrandNew(false);
							//For status
							expItem.setStatusEnabled(false);
							expItem.setEditable(false);
							expItem.setJustificationEnabled(false);
							expItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							expItem.setDisableReviseItem(true);
							//24975@06112020@azza - Start
							expItem.setChangePencilToNote(false);
							//24975@06112020@azza - End
							// 24975@20112020 - Start
							if(expItem.isIndicator()== true){
								expItem.setIndicator(false);
							}							
//							// 24975@20112020 - End
						    //24975 - start
						  for (PpRemark setEditRemark : expItem.getPpRemarks()) {
							  if (expItem.getExpenditureItemId().equals(expenditureItem.getFromPpItem())) {
								 setEditRemark.setRemark(null);
								}
							 }//24975 - End
						  
						    //03122020@24975 - Start
							if (expItem.isNotUsingEp() == true) {
								expItem.setNotUsingEp(false);
							 }
							//03122020@24975 - End
							if (expItem.isSearchReset() == true) {//28012021@27624 - start
								expItem.setSearchReset(false);
							 }//28012021@27624 - end
					      }
				       }	
					}
				//[#24975] - azza - end
					
				//[SIR#12802] - end	
					//Set to status 2 - Cancelled
					//log.info("[SIR#12802] - Set item status to 2");
					expenditureItem.setRecordStatusFlg(Integer.valueOf(DbValueConst.RECORD_STATUS_CANCELLED));
					expenditureItem.setBrandNew(false);
					this.getManagedBean().getDeleteEpItemsB().add(expenditureItem);
					count++;
			}
		}
			
			//remove deleted item from list
			if(count > 0) {
				//log.info("[SIR#12802] - Remove deleted item from expenditureItem");
				for(PpExpenditureItem expenditureItem: this.getManagedBean().getDeleteEpItemsB()){
					epItems.remove(expenditureItem);
				}
			}
		}
		//VOT-T
		else if(ParameterConst.VOT_TANGGUNGAN.equals(paramCode)){
			epItems = this.getManagedBean().getEpItemsT();
			
			if(this.getManagedBean().getDeleteEpItemsT()==null){
				this.getManagedBean().setDeleteEpItemsT(new ArrayList<PpExpenditureItem>());
			}
			
			//add deleted item to list
			for(PpExpenditureItem expenditureItem: epItems){
				//log.info("[SIR#12802] - VOT-T: Expenditure item delete status:" + expenditureItem.isDeleted());
				//if(expenditureItem.isBrandNew() && expenditureItem.isDeleted()){
				//[SIR#12802] - start
				if(expenditureItem.isDeleted()){
					
					//[#24975] - azza - start
					for(PpExpenditureItem expItem: epItems){
						if (expItem.getExpenditureItemId() != null){
						if (expItem.getExpenditureItemId().equals(expenditureItem.getFromPpItem())){
							//For cloning
							expItem.setCloneEnabled(false);
							expItem.setDisableForPindaan(false);
							//For update reason
							expItem.setReasonForUpdateEnabled(true);
							expItem.setBrandNew(false);
							//For status
							expItem.setStatusEnabled(false);
							expItem.setEditable(false);
							expItem.setJustificationEnabled(false);
							expItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							expItem.setDisableReviseItem(true);
							//24975@06112020@azza - Start
							expItem.setChangePencilToNote(false);
							//24975@06112020@azza - End
							// 24975@20112020 - Start
							if(expItem.isIndicator()== true){
							   expItem.setIndicator(false);
							}
							// 24975@20112020 - End	
							  //24975 - start
							  for (PpRemark setEditRemark : expItem.getPpRemarks()) {
								  if (expItem.getExpenditureItemId().equals(expenditureItem.getFromPpItem())) {
									 setEditRemark.setRemark(null);
									}
							   }//24975 - End
							  
							//03122020@24975 - Start
							if (expItem.isNotUsingEp() == true) {
								expItem.setNotUsingEp(false);
							  }
							//03122020@24975 - End
							if (expItem.isSearchReset() == true) {//28012021@27624 - start
								expItem.setSearchReset(false);
							 }//28012021@27624 - end
						  }
				      }
				  }
				//[#24975] - azza - end
					
				//[SIR#12802] - end	
					//Set to status 2 - Cancelled
					//log.info("[SIR#12802] - Set item status to 2");
					expenditureItem.setRecordStatusFlg(Integer.valueOf(DbValueConst.RECORD_STATUS_CANCELLED));		
					expenditureItem.setBrandNew(false);
					this.getManagedBean().getDeleteEpItemsT().add(expenditureItem);
					count++;
				}
			}
			
			//remove deleted item from list
			if(count > 0) {
				//log.info("[SIR#12802] - Remove deleted item from expenditureItem");
				for(PpExpenditureItem expenditureItem: this.getManagedBean().getDeleteEpItemsT()){
					epItems.remove(expenditureItem);
				}
			}
		}
		//VOT-P
		else if(ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)){
			epItems = this.getManagedBean().getEpItemsP();
			
			if(this.getManagedBean().getDeleteEpItemsP()==null){
				this.getManagedBean().setDeleteEpItemsP(new ArrayList<PpExpenditureItem>());
			}
			
			//add deleted item to list
			for(PpExpenditureItem expenditureItem: epItems){
				//log.info("[SIR#12802] - VOT-P: Expenditure item delete status:" + expenditureItem.isDeleted());
				//if(expenditureItem.isBrandNew() && expenditureItem.isDeleted()){
				//[SIR#12802] - start
				if(expenditureItem.isDeleted()){
					
					//[#24975] - azza - start
					for(PpExpenditureItem expItem: epItems){
						if (expItem.getExpenditureItemId() != null){
						if (expItem.getExpenditureItemId().equals(expenditureItem.getFromPpItem())){
							//For cloning
							expItem.setCloneEnabled(false);
							expItem.setDisableForPindaan(false);
							//For update reason
							expItem.setReasonForUpdateEnabled(true);
							expItem.setBrandNew(false);
							//For status
							expItem.setStatusEnabled(false);
							expItem.setEditable(false);
							expItem.setJustificationEnabled(false);
							expItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							expItem.setDisableReviseItem(true);
							//24975@06112020@azza - Start
							expItem.setChangePencilToNote(false);
							//24975@06112020@azza - End
							// 24975@20112020 - Start
							if(expItem.isIndicator()== true){
							   expItem.setIndicator(false);
                             }
							// 24975@20112020 - End
							//24975 - start
						 for (PpRemark setEditRemark : expItem.getPpRemarks()) {
							if (expItem.getExpenditureItemId().equals(expenditureItem.getFromPpItem())) {
								setEditRemark.setRemark(null);
						      }
					      }//24975 - End
						 
						//03122020@24975 - Start
						if (expItem.isNotUsingEp() == true) {
							expItem.setNotUsingEp(false);
						}
						//03122020@24975 - End
						if (expItem.isSearchReset() == true) {//28012021@27624 - start
							expItem.setSearchReset(false);
						 }//28012021@27624 - end
					 }
				  }
				}
				//[#24975] - azza - end
					
				//[SIR#12802] - end	
					//Set to status 2 - Cancelled
					//log.info("[SIR#12802] - Set item status to 2");
					expenditureItem.setRecordStatusFlg(Integer.valueOf(DbValueConst.RECORD_STATUS_CANCELLED));		
					expenditureItem.setBrandNew(false);
					this.getManagedBean().getDeleteEpItemsP().add(expenditureItem);	
					count++;
				}
			}
			
			//remove deleted item from list
			if(count > 0) {
				//log.info("[SIR#12802] - Remove deleted item from expenditureItem");
				for(PpExpenditureItem expenditureItem: this.getManagedBean().getDeleteEpItemsP()){
					epItems.remove(expenditureItem);
				}
			}
		}
		//VOT-S
		else if(ParameterConst.VOT_SETIA.equals(paramCode)){
			epItems = this.getManagedBean().getEpItemsS();
			
			if(this.getManagedBean().getDeleteEpItemsS()==null){
				this.getManagedBean().setDeleteEpItemsS(new ArrayList<PpExpenditureItem>());
			}
			
		
			//add deleted item to list
			for(PpExpenditureItem expenditureItem: epItems){
				//log.info("[SIR#12802] - VOT-S: Expenditure item delete status:" + expenditureItem.isDeleted());
				//if(expenditureItem.isBrandNew() && expenditureItem.isDeleted()){
				//[SIR#12802] - start
				if(expenditureItem.isDeleted()){
					
					//[#24975] - azza - start
					for(PpExpenditureItem expItem: epItems){
						if (expItem.getExpenditureItemId() != null){
						if (expItem.getExpenditureItemId().equals(expenditureItem.getFromPpItem())){
							//For cloning
							expItem.setCloneEnabled(false);
							expItem.setDisableForPindaan(false);
							//For update reason
							expItem.setReasonForUpdateEnabled(true);
							expItem.setBrandNew(false);
							//For status
							expItem.setStatusEnabled(false);
							expItem.setEditable(false);
							expItem.setJustificationEnabled(false);
							expItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							expItem.setDisableReviseItem(true);
							//24975@06112020@azza - Start
							expItem.setChangePencilToNote(false);
							//24975@06112020@azza - End
							// 24975@20112020 - Start
							if(expItem.isIndicator()== true){
							   expItem.setIndicator(false);
						   }
							// 24975@20112020 - End
							//24975 - start
							 for (PpRemark setEditRemark : expItem.getPpRemarks()) {
								if (expItem.getExpenditureItemId().equals(expenditureItem.getFromPpItem())) {
									setEditRemark.setRemark(null);
							      }
						    }//24975 - End
							 
							//03122020@24975 - Start
							if (expItem.isNotUsingEp() == true) {
								expItem.setNotUsingEp(false);
							}
						   //03122020@24975 - End
						    if (expItem.isSearchReset() == true) {//28012021@27624 - start
								expItem.setSearchReset(false);
						   }//28012021@27624 - end
					   }
				    }
				}
				//[#24975] - azza - end
					
				//[SIR#12802] - end	
					//Set to status 2 - Cancelled
					//log.info("[SIR#12802] - Set item status to 2");
					expenditureItem.setRecordStatusFlg(Integer.valueOf(DbValueConst.RECORD_STATUS_CANCELLED));		
					expenditureItem.setBrandNew(false);
					this.getManagedBean().getDeleteEpItemsS().add(expenditureItem);
					count++;
				}
			}
			
			//remove deleted item from list
			if(count > 0) {
				//log.info("[SIR#12802] - Remove deleted item from expenditureItem");
				for(PpExpenditureItem expenditureItem: this.getManagedBean().getDeleteEpItemsS()){
					epItems.remove(expenditureItem);
				}
			}
		}
		//VOT-H
		else if(ParameterConst.VOT_AMANAH.equals(paramCode)){
			epItems = this.getManagedBean().getEpItemsH();
			
			if(this.getManagedBean().getDeleteEpItemsH()==null){
				this.getManagedBean().setDeleteEpItemsH(new ArrayList<PpExpenditureItem>());
			}
			
			//add deleted item to list
			for(PpExpenditureItem expenditureItem: epItems){
				//log.info("[SIR#12802] - VOT-H: Expenditure item delete status:" + expenditureItem.isDeleted());
				//if(expenditureItem.isBrandNew() && expenditureItem.isDeleted()){
				//[SIR#12802] - start
				if(expenditureItem.isDeleted()){
					
					//[#24975] - azza - start
					for(PpExpenditureItem expItem: epItems){
						if (expItem.getExpenditureItemId() != null){
						if (expItem.getExpenditureItemId().equals(expenditureItem.getFromPpItem())){
							//For cloning
							expItem.setCloneEnabled(false);
							expItem.setDisableForPindaan(false);
							//For update reason
							expItem.setReasonForUpdateEnabled(true);
							expItem.setBrandNew(false);
							//For status
							expItem.setStatusEnabled(false);
							expItem.setEditable(false);
							expItem.setJustificationEnabled(false);
							expItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							expItem.setDisableReviseItem(true);
							//24975@06112020@azza - Start
							expItem.setChangePencilToNote(false);
							//24975@06112020@azza - End
							// 24975@20112020 - Start
							if(expItem.isIndicator()== true){
							   expItem.setIndicator(false);
							}
							// 24975@20112020 - End
							//24975 - start
							 for (PpRemark setEditRemark : expItem.getPpRemarks()) {
								if (expItem.getExpenditureItemId().equals(expenditureItem.getFromPpItem())) {
									setEditRemark.setRemark(null);
							      }
						     }//24975 - End
							 
							//03122020@24975 - Start
							 if (expItem.isNotUsingEp() == true) {
								 expItem.setNotUsingEp(false);
							  }
							//03122020@24975 - End
							 if (expItem.isSearchReset() == true) {//28012021@27624 - start
									expItem.setSearchReset(false);
							}//28012021@27624 - end
					    }
				    }
				}
				//[#24975] - azza - end
					
				//[SIR#12802] - end	
					//Set to status 2 - Cancelled
					//log.info("[SIR#12802] - Set item status to 2");
					expenditureItem.setRecordStatusFlg(Integer.valueOf(DbValueConst.RECORD_STATUS_CANCELLED));
					expenditureItem.setBrandNew(false);
					this.getManagedBean().getDeleteEpItemsH().add(expenditureItem);	
					count++;
				}
			}
			
			//remove deleted item from list
			if(count > 0) {
				//log.info("[SIR#12802] - Remove deleted item from expenditureItem");
				for(PpExpenditureItem expenditureItem: this.getManagedBean().getDeleteEpItemsH()){
					epItems.remove(expenditureItem);
				}
			}
		}

		log.info("Number of item(s) to be deleted:" + count);
		if(count==0){
			throw new EpException(MessageConst.ERROR_730009);
		}

		//[SIR#12802] - start
		//update new item list after deletion
		log.info("Set new item list...");
		//VOT-B
		if(ParameterConst.VOT_MENGURUS.equals(paramCode)){
			this.getManagedBean().setEpItemsB(epItems);
		}else if(ParameterConst.VOT_TANGGUNGAN.equals(paramCode)){
		//VOT-T
			this.getManagedBean().setEpItemsT(epItems);
		}else if(ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)){
		//VOT-P
			this.getManagedBean().setEpItemsP(epItems);
		}else if(ParameterConst.VOT_SETIA.equals(paramCode)){
		//VOT-S
			this.getManagedBean().setEpItemsS(epItems);
		}else if(ParameterConst.VOT_AMANAH.equals(paramCode)){
		//VOT-H
			this.getManagedBean().setEpItemsH(epItems);
		}
		//[SIR#12802] - end		
		calculateEpGrandTotal(paramCode);
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
			//24975@04112020 - start
		if (this.getManagedBean().isApprovedPlan()== false){
			epTotalBudget = PpCommonUtil.addBigDecimalNullable(epTotalBudget, epItem.getBudgetAmt());
			epTotalNonEpAlloc = PpCommonUtil.addBigDecimalNullable(epTotalNonEpAlloc, epItem.getNonEpAmt());
			epTotalPlan = PpCommonUtil.addBigDecimalNullable(epTotalPlan, epItem.getPlanAmt());
		}else{
		 if(this.getManagedBean().isApprovedPlan()== true){
			 if(epItem.getRecordStatusFlg()!= 9 || epItem.isDisableRow() == true){ //24022021@25549 
			epTotalBudget = PpCommonUtil.addBigDecimalNullable(epTotalBudget, epItem.getBudgetAmt());
			epTotalNonEpAlloc = PpCommonUtil.addBigDecimalNullable(epTotalNonEpAlloc, epItem.getNonEpAmt());
			epTotalPlan = PpCommonUtil.addBigDecimalNullable(epTotalPlan, epItem.getPlanAmt());
		  }
	    }
	  }
	}
		//24975@04112020 - End
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
		log.info("retrieveEpItemRemark" + epItem.isBrandNew() );
		if(!epItem.isBrandNew()){
			if(epItem.getPpRemarks()==null){
				epItem.setPpRemarks(new ArrayList<PpRemark>());
//				epItem.setPpRemarks(PpCommonUtil.retrieveRemark(epItem.getExpenditureItemId(), ParameterConst.DOCTYPE_PLAN_EXPENDITURE));
			}
		}
		loadEpItemRemarks(epItem);
		//24975@20112020 - start
		if (epItem.isIndicator() == true){
			epItem.setIndicator(false);
		}
		//24975@20112020 - End
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
		
//		if(!epItem.isEditable()){
//			newRemark = false;
//		}
		
	
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
			remark.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
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
		
		  //24975@06112020@azza - Start
		  for(PpRemark setEditRemark : epItem.getPpRemarks()){
			  if (epItem.isChangePencilToNote()== true){
   				  this.getManagedBean().setDisable(true);
     		    }else{
     			  this.getManagedBean().setDisable(false);
			}
		}
		  //24975@06112020@azza - End

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
		//03122020@24975 - Start
		if (epItem.isNotUsingEp() == true) {
			epItem.setNotUsingEp(false);
		}
		//03122020@24975 - End
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
	    this.getManagedBean().setJustificationUsingDisable(false); //#35502, Fix as-is bug: #38652

		if(epItem.getJustifications()==null){
			epItem.setJustifications(new ArrayList<PpRemark>());
		}

		// if reason is empty
		if (epItem.getJustifications().isEmpty() || epItem.getJustifications().size() == 0) {
			newRemark = true;
		}
		
		// filter by current view mode	
		if(!this.getManagedBean().isModeEditable() || !epItem.isEditable()){
			newRemark = false;
		}
		
		if(newRemark){
			log.info("new justification added");
			PpRemark remark = new PpRemark();
			remark.setEditable(true);
			remark.setRemarkType(ProcurementPlanConst.REMARK_TYPE_JUSTIFICATION);
			remark.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
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

		//#35502, Fix as-is bug: #38652 Start
		if (epItem.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED) {
			this.getManagedBean().setJustificationUsingDisable(true);
		}
		//#35502, Fix as-is bug: #38652 End

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
		/*BigDecimal ppTotalEst = new BigDecimal(0);
		
		for(PpProcurementItem ppItem: this.getManagedBean().getSelectedPlan().getPpProcurementItems()){
			if (ppItem.getFromPpItem() == null) {
				ppTotalEst = PpCommonUtil.addBigDecimalNullable(ppTotalEst, ppItem.getEstimatedAmt());
			}
		}*/
		this.getManagedBean().setPpTotalEst(
				defineCalculatedPpItems(this.getManagedBean().getSelectedPlan().getPpProcurementItems()));
	}
	
	public BigDecimal defineCalculatedPpItems(List<PpProcurementItem> ppItems) {
		
		log.info("defineCalculatedPpItems..");
		
		BigDecimal ppTotalEst = new BigDecimal(0);
		List<Long> ppItemIdList = new ArrayList<Long>();
		List<Long> excludedPpItemIdList = new ArrayList<Long>();
		List<PpProcurementItem> ppItemList = new ArrayList<PpProcurementItem>();
		
		for (PpProcurementItem ppItem : ppItems) {
			ppItemIdList.add(ppItem.getProcurementItemId());
		}
		
		for (PpProcurementItem ppItem : ppItems) {
			if (ppItem.getFromPpItem() != null) {
				if (ppItemIdList.contains(ppItem.getFromPpItem())) {
					/*log.info("("+ppItem.getProcurementItemId()+
							" cloned from "+ppItem.getFromPpItem()+")");*/
					excludedPpItemIdList.add(ppItem.getFromPpItem()); // Won't be calculated
				}
			}
			ppItemList.add(ppItem);
		}
		
		/*log.info("Size ppItemIdList= "+ppItemIdList.size()
				+", Size ppItemList= "+ppItemList.size()
				+", Size excludedPpItemIdList= "+excludedPpItemIdList.size());*/
		
		// Second time filter
		for (int i = 0; i < ppItemList.size(); i++) {
			if (!excludedPpItemIdList.contains(ppItemList.get(i).getProcurementItemId())) {
				/*log.info("To be calculated= "+ppItemList.get(i).getProcurementItemId()+" - "
						+ppItemList.get(i).getEstimatedAmt());*/
				ppTotalEst = PpCommonUtil.addBigDecimalNullable(ppTotalEst, ppItemList.get(i).getEstimatedAmt());
			}
		}
		//log.info("TotalEst= "+ppTotalEst);
		return ppTotalEst;
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
		//24975@22012021 - start
		ppItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		ppItem.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
		log.info("setCreatedDate " + new Timestamp(System.currentTimeMillis()) + " setCreatedBy " + getFacesScopes().getUserSessionBean().getUserId());
		//24975@22012021 - end
		ppItem.setProcurementCat(new PmParameter());
		ppItem.setProcurementType(new PmParameter());
		ppItem.setEditable(true);
		//25549@09102020 - start
		ppItem.setReviseReject(true);
		ppItem.setOriginal(true);
		//25549@09102020 - end
		ppItem.setEnableRevise(true);
		//#34156 - Start
		ppItem.setNewLabcatMethodType(true);
		//#34156 - End

		//#32730 - Start
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);				
		int nextYear = currentYear + 1;
				
		if(Calendar.getInstance().get(Calendar.YEAR) == this.getManagedBean().getSelectedPlan().getPlanYear()){ 
			//current year
			ppItem.setInvitationDate(new Date());
		}else if(nextYear == this.getManagedBean().getSelectedPlan().getPlanYear()){ 
			System.out.println("next year ");
			//next year
			Calendar date = new GregorianCalendar((nextYear), Calendar.JANUARY, 1);
			
			ppItem.setInvitationDate(date.getTime());
		}else{
			//previous year
			Long preYear = this.getManagedBean().getSelectedPlan().getPlanYear();
			Calendar datePreYear = new GregorianCalendar((preYear.intValue()), Calendar.DECEMBER, 31);
			
			ppItem.setInvitationDate(datePreYear.getTime());
		}
		//#32730 - End
		this.getManagedBean().getSelectedPlan().addPpProcurementItems(ppItem);
		
	}
	/**
	 * generate min date 
	 * @return String (date in string)
	 */
	public String getMinDate(){
		log.info("getMinDate");		
		Date today = new Date();
		String dateStr = "";
		Calendar c1 = Calendar.getInstance();
		String	year	=	String.valueOf(c1.get(Calendar.YEAR));
		if(getManagedBean().getSelectedPlan().getPlanYear() != null){
			if(year.equals(getManagedBean().getSelectedPlan().getPlanYear().toString())){
				dateStr = DateUtils.dateToString("dd/MM/yyyy", today);	
			}else{
				Calendar c2 = new GregorianCalendar(
						getManagedBean().getSelectedPlan().getPlanYear().intValue(),Calendar.JANUARY,1);
				dateStr = DateUtils.dateToString("dd/MM/yyyy", c2.getTime());	
			}
		}else{
			dateStr = DateUtils.dateToString("dd/MM/yyyy", today);	
		}
		
			
		return dateStr;
	}
	
	/**
	 * generate max date 
	 * @return String (date in string)
	 */
	public String getMaxDate(){
		log.info("getMaxDate");
		String dateStr = "";
		if(getManagedBean().getSelectedPlan().getPlanYear() != null){		
			Calendar c2 = new GregorianCalendar(
					getManagedBean().getSelectedPlan().getPlanYear().intValue(),Calendar.DECEMBER,31);
			dateStr = DateUtils.dateToString("dd/MM/yyyy", c2.getTime());
		}
		
			
		return dateStr;
	}
	
	/**
	 * validation of pp item
	 */
	private void validatePpItem(){
		log.info("validatePpItem");
		if(!this.getManagedBean().getSelectedPlan().getPpProcurementItems().isEmpty()){
			//boolean isReasonEmpty;
			//List<Long> totalClones = new ArrayList<Long>();
			//List<Long> notEmptyReasons = new ArrayList<Long>();
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
					// Set Reason For Update to be mandatory
					
					log.info("statusId= "+this.getManagedBean().getSelectedPlan().getStatus().getStatusId());
					
					/*if (ProcurementPlanConst.PPP_APPROVED.equals(this.getManagedBean().getSelectedPlan().getStatus().getStatusId())) {
						if(ppItem.getPpUpdateReasons() != null && 
								!ppItem.getPpUpdateReasons().isEmpty()) {
							String reasonDesc = ppItem.getPpUpdateReasons().get(0).getReasonDesc();
							log.info("original reasonDesc for ppItemId "
									+ppItem.getProcurementItemId()+" (isCloned="
									+ppItem.isCloned()+") = "+reasonDesc);
							
							isReasonEmpty = reasonDesc == null || reasonDesc.trim().length() == 0;
							log.info("isReasonEmpty= "+isReasonEmpty);
							
							if (ppItem.isCloned() && !isReasonEmpty) {
								notEmptyReasons.add(ppItem.getProcurementItemId());
							}
							if (ppItem.isCloned()) {
								totalClones.add(ppItem.getProcurementItemId());
							}
						}
					}*/
					
				}
			}
			/*if (ProcurementPlanConst.PPP_APPROVED.equals(this.getManagedBean().getSelectedPlan().getStatus().getStatusId())) {
				log.info("notEmptyReasons.size= "+notEmptyReasons.size());
				log.info("totalClones.size= "+totalClones.size());
				if ((notEmptyReasons.size() == 0 && totalClones.size() > 0) || 
						((totalClones.size() > 0) && 
								(totalClones.size() != notEmptyReasons.size()))) {
					throw new EpException(MessageConst.ERROR_730035);
				}
			}*/
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

		// #35502 Fix as-is bug Start
		if (this.getManagedBean().getDeletePpItems() == null) {
			this.getManagedBean().setDeletePpItems(new ArrayList<PpProcurementItem>());
		}
		// #35502 Fix as-is bug End

		//add deleted item to list
		for(PpProcurementItem procurementItem: this.getManagedBean().getSelectedPlan().getPpProcurementItems()){
			//log.info("[SIR#12802] - Procurement item delete status:" + procurementItem.isDeleted());
			//if(procurementItem.isBrandNew() && procurementItem.isDeleted()){
			//[SIR#12802] - start
			if(procurementItem.isDeleted()){
			//[SIR#12802] - end
				//Set to status 2 - Cancelled
				//log.info("[SIR#12802] - Set item status to 2");
				/*log.info("To be deleted= "+procurementItem.getProcurementItemId()+" -> "+procurementItem.getProcurementTitle());*/
				
				if ( (procurementItem.getFromPpItem() != null && 
						procurementItem.getProcurementItemId() != null) ||
						(procurementItem.getProcurementItemId() != null && 
						procurementItem.getFromPpItem() == null) ) {
					//24975@04112020@azza - Start
					procurementItem.setBrandNew(false);
					//24975@04112020@azza - End
					procurementItem.setRecordStatusFlg(Integer.valueOf(DbValueConst.RECORD_STATUS_CANCELLED));				
				}
				
				/*log.info("Now, status of "+procurementItem.getProcurementTitle()+
						" is "+procurementItem.getRecordStatusFlg());*/
				
				//24975@04112020@azza - Start
				for(PpProcurementItem PpItem:  this.getManagedBean().getSelectedPlan().getPpProcurementItems()){
				if (PpItem.getProcurementItemId() != null){
					if (PpItem.getProcurementItemId() .equals(procurementItem.getFromPpItem())){
						//For cloning
						PpItem.setCloneEnabled(false);
						PpItem.setDisableForPindaan(false);
						//For update reason
						PpItem.setReasonForUpdateEnabled(true);
						PpItem.setBrandNew(false);
						//For status
						PpItem.setStatusEnabled(false);
						PpItem.setEditable(false);
						PpItem.setJustificationEnabled(false);
						PpItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
						PpItem.setAllowAnotherPindaan(true);
						PpItem.setEnableRevise(true);
						
						// For update reason, change the icon back to Pencil if any 
						if (PpItem.isChangePencilToNote()) {
							PpItem.setChangePencilToNote(false);
						}
						if (PpItem.getPpUpdateReasons() != null || 
								!PpItem.getPpUpdateReasons().isEmpty()) {
							 //24975 - start
							 for (PpUpdateReason updateReasons : PpItem.getPpUpdateReasons()){
									if (PpItem.getProcurementItemId().equals(procurementItem.getFromPpItem())) {
										updateReasons.setReasonDesc(null);
										if (updateReasons.isDisableReasonField()) {
											updateReasons.setDisableReasonField(false);
										}
									}
								}
							}// 24975 - End	
							// 24975@20112020 - Start
							if (PpItem.isIndicator() == true) {
								PpItem.setIndicator(false);
							}
							// 24975@20112020 - End
							if (PpItem.isSearchReset() == true) {//28012021@27624 - start
								PpItem.setSearchReset(false);
						  }//28012021@27624 - end
				      }
			       }
				
				}
				//24975@04112020@azza - End
				//Add deleted item
				this.getManagedBean().getDeletePpItems().add(procurementItem);
				count++;
			}
		}
		/*log.info("Number of item(s) to be deleted:" + count);*/
		if(count==0){
			throw new EpException(MessageConst.ERROR_730015);
		}
		
		//remove deleted item from list
		for(PpProcurementItem procurementItem: this.getManagedBean().getDeletePpItems()){
			this.getManagedBean().getSelectedPlan().getPpProcurementItems().remove(procurementItem);
		}
		//24975@04112020@azza - Start
//		resetPpProcurementItemFields();
		//24975@04112020@azza - End
		calculatePpGrandTotal();
		
		
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
			//#35671 - Start
			ppItem.setPpRejectReasons(PpCommonUtil.retrieveRejectReason(ppItem.getProcurementItemId(), ParameterConst.DOCTYPE_PLAN_PROCUREMENT));
			//#35671 - End
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
			remark.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
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
	
	//#34156 - Start
	public List<PmProcurementCatType> getProcurementTypeOptionsNewLabel(Long procurementCatId, boolean NewLabcatMethodType) {
		log.info("getProcurementTypeOptionsNewLabel");
		List<PmProcurementCatType> procurementCatTypeList = new ArrayList<PmProcurementCatType>();
		if(procurementCatId!=null && procurementCatId>0L){
			String languageCode = getFacesScopes().getUserLocale().getLanguage();
			procurementCatTypeList = PpCommonUtil.retrieveProcurementTypeListByCatIdNewLabel(languageCode, procurementCatId, NewLabcatMethodType);
		}
		return procurementCatTypeList;
	}
	
	public List<PmProcurementCatType> getProcurementTypeOptionsClone(Long procurementCatId) {
		log.info("getProcurementTypeOptionsClone");
		List<PmProcurementCatType> procurementCatTypeList = new ArrayList<PmProcurementCatType>();
		if(procurementCatId!=null && procurementCatId>0L){
			String languageCode = getFacesScopes().getUserLocale().getLanguage();
			procurementCatTypeList = PpCommonUtil.retrieveProcurementTypeListByCatIdClone(languageCode, procurementCatId);
		}
		return procurementCatTypeList;
	}
	//#34156 - End	
	
	public void verifyPlanYearControl(ValueChangeEvent event){		
		if(getManagedBean().getSelectedPlan().isBrandNew() 
				&& ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals(getManagedBean().getSelectedPlan().getPlanType().getParameterCode()) &&
				CollectionUtils.isNotEmpty(getManagedBean().getSelectedPlan().getPpProcurementItems())) {
			//log.info("New: "+event.getNewValue()+", Old: "+event.getOldValue());
			getManagedBean().setOldYearValue(event.getOldValue() != null ? Long.valueOf(event.getOldValue().toString()) : null);
			
			if(event.getOldValue() != null && event.getNewValue() != null){
				//Change from Old Value To New Value (eg year 2017 to year 2018)
				//Confirmation Popup
				RequestContext.getCurrentInstance().execute("planYearChangeId.show();");
			}else if(event.getOldValue() == null && event.getNewValue() != null){
				//  Change from NULL to New Value (eg Null to year 2017
				this.getManagedBean().getSelectedPlan().setPlanYear(Long.valueOf(event.getNewValue().toString()));
				updatePlanYearControl();
			}
		}
	}
	
	/**
	 * to default the invitation date options for procurement item
	 * @param planYear
	 */
	public void updatePlanYearControl(){
		log.info("updatePlanYearControl");
		if(getManagedBean().getSelectedPlan().isBrandNew() 
				&& ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals(getManagedBean().getSelectedPlan().getPlanType().getParameterCode())) {
//			if(getManagedBean().getSelectedPlan().getPlanYear() == null){
//				cancelPlanYearUpdate();
//				throw new EpException(MessageConst.ERROR_100022,
//						new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pp.plan.year")});
//			}
			
			if(this.getManagedBean().getSelectedPlan().getPlanYear() != null 
					&& CollectionUtils.isNotEmpty(getManagedBean().getSelectedPlan().getPpProcurementItems())){
				for(PpProcurementItem ppItem : getManagedBean().getSelectedPlan().getPpProcurementItems()){
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
					getManagedBean().setOldYearValue(null);
				}
				
			}
		}
		
//		if(planYear!=null){
//			if(planYear == Calendar.getInstance().get(Calendar.YEAR)){ 
//				//this year
//				this.getManagedBean().setInvitationYear("");
//			}else{ 
//				//next year
//				this.getManagedBean().setInvitationYear("c+0:c+0");
//				//c+1:c+0 2017
//				//c+0:c+0
//				//c+1:c+1 2016, 2017
//				//"" 2015(current)
//			}
//		}else{
//			this.getManagedBean().setInvitationYear("");
//		}
//		
//		log.info("updatePlanYearControl " + this.getManagedBean().getInvitationYear());
		
	}
	
	public void cancelPlanYearUpdate(){	
		getManagedBean().getSelectedPlan().setPlanYear(getManagedBean().getOldYearValue());	
	}
	
	/* Procurement Related ------------------- END ---------------------------*/
	/* --------------------------------------------------------- Item Level - START -----------------------------------------------------------------*/
	
	
	
	/* --------------------------------------------------------- Search & Result - START -----------------------------------------------------------------*/
		
	/**
	 * Prepare and initial search view (filtered by login user)
	 */
	private void initialSearchPlan(){
		log.info("initialSearchPlan");
		//#32730 - start
		this.getManagedBean().setPreYear(false);
		//#32730 - end
		//200911@akmal - Enhancement27624 - Start
		Long userId = getFacesScopes().getUserSessionBean().getUserId() != null ? getFacesScopes().getUserSessionBean().getUserId() : new LogKey().getUserId();
		
		PmOrgProfile pmOrgProfile = PpCommonUtil.retrieveUserOrgListByUserId(userId, true);
		//200911@akmal - Enhancement27624 - End
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
			//log.info("+ getPlanNumber "  +planSearchBean.getPlanNumber());
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
			//log.info("+ getPlanType "  +planSearchBean.getPlanType().getParameterCode());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("planType.parameterCode");
			bean.setSearchValue(planSearchBean.getPlanType().getParameterCode());
			searchBean.getDataFilterBeans().add(bean);
			
			// plan title
			if(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals(planSearchBean.getPlanType().getParameterCode()) 
					&& StringUtils.isNotBlank(planSearchBean.getPlanTitle())){
				//log.info("+ getPlanTitle "  +planSearchBean.getPlanTitle());
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
			//log.info("+ getPtj "  +planSearchBean.getPtj().getOrgProfileId());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("orgProfile.orgProfileId");
			bean.setSearchValue(planSearchBean.getPtj().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}else if(planSearchBean.getKptj()!=null  && planSearchBean.getKptj().getOrgProfileId()!=null){
			//log.info("+ getKptj "  +planSearchBean.getKptj().getOrgProfileId());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("orgProfile."+
					OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
			bean.setSearchValue(planSearchBean.getKptj().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}else if(planSearchBean.getMinistry()!=null  && planSearchBean.getMinistry().getOrgProfileId()!=null){
			//log.info("+ getMinistry "  +planSearchBean.getMinistry().getOrgProfileId());
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
			//log.info("+ getYear "  +planSearchBean.getYear());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("planYear");
			bean.setSearchValue(planSearchBean.getYear());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		//Remove code #32730
		/*else{
			//27012021@27624 - Start
			log.info("+ getYeardefault "  + Calendar.getInstance().get(Calendar.YEAR));
			
			Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
			Integer NextYear = currentYear + 1;
					
			SearchFilterBean sfbean1 = new SearchFilterBean();
			sfbean1.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			sfbean1.setSearchField("planYear");
			sfbean1.setSearchValue(currentYear);
			sfbean1.setAndOr(SearchCondition.CONDITION_OR.value());
			
			SearchFilterBean sfbean2 = new SearchFilterBean();
			sfbean2.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			sfbean2.setSearchField("planYear");
			sfbean2.setSearchValue(NextYear);
			sfbean2.setAndOr(SearchCondition.CONDITION_OR.value());
			
			SearchMultiFilterBean beans = new SearchMultiFilterBean(sfbean1,sfbean2);			
			searchBean.getDataFilterBeans().add(beans);		
			isEmpty = false;
			//27012021@27624 - End
		}*/
		// vot type
		if(planSearchBean.getVotType()!=null && planSearchBean.getVotType().getParameterId()!=null){
			//log.info("+ getVotType "  +planSearchBean.getVotType().getParameterId());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppExpenditureItems.votFundType.parameterId");
			bean.setSearchValue(planSearchBean.getVotType().getParameterId());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		
		//base on the flag, default list is either select all or empty
		if(isEmpty && !isSearchable){
			//log.info("+ status "  + null);
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
		this.getManagedBean().setSearch(true);
		boolean isEmpty = true;
		//200911@akmal - Enhancement27624 - Start
		boolean isPlanYearEmpty = true;
		boolean isPlanTypeEmpty = true;
		//200911@akmal - Enhancement27624 - End
		  if(!StringUtils.isBlank(this.getManagedBean().getPlanSearchBean().getPlanNumber())){
			  isEmpty = false;
		//200911@akmal - Enhancement27624 - Start
		  } 
		  if(this.getManagedBean().getPlanSearchBean().getYear()!=null){
			  isPlanYearEmpty = false;
			//201228@akmal - 27624 - Start
			  isEmpty = false;
			  this.getManagedBean().setMaxYear("31/12/"+Calendar.getInstance().get(Calendar.YEAR));
			//201228@akmal - 27624 - End
		  }
		  if(this.getManagedBean().getPlanSearchBean().getPlanType().getParameterCode()!=null){
			  isPlanTypeEmpty = false;
			//201105@akmal - 27624 - Start
			  isEmpty = false;
			//201105@akmal - 27624 - End
		//200911@akmal - Enhancement27624 - End
		  }
		  if(this.getManagedBean().getPlanSearchBean().getVotType().getParameterId()!=null){
			  isEmpty = false;
		  }else if((this.getManagedBean().getPlanSearchBean().getPtj()!=null && this.getManagedBean().getPlanSearchBean().getPtj().getOrgProfileId() != null)
				  || this.getManagedBean().getPlanSearchBean().getKptj()!=null && this.getManagedBean().getPlanSearchBean().getKptj().getOrgProfileId() != null
				  || this.getManagedBean().getPlanSearchBean().getMinistry()!=null && this.getManagedBean().getPlanSearchBean().getMinistry().getOrgProfileId() != null){
			  isEmpty = false;
		  }
		//210108@akmal - Enhancement27624 - Start
		  if(!this.getManagedBean().isAudit()){
			  isPlanTypeEmpty = false;
			  isPlanYearEmpty = false;
		  }
		  
		  if(isPlanTypeEmpty){
			  FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_730016, 
					new String[]{ResourceBundleUtils.getMessage("lbl.pp.plan.year",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())+"("+MessageConst.ERROR_730016+")"));
		  } 
		  if(isPlanYearEmpty){
			  FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_730028, 
					new String[]{ResourceBundleUtils.getMessage("lbl.pp.plan.type",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())+"("+MessageConst.ERROR_730028+")"));
		  } 
		  if(!isPlanTypeEmpty && !isPlanYearEmpty){
			  ppPlanDataModel.reloadData();
		  }
		//210108@akmal - Enhancement27624 - End
		
	}
	
	//210111@akmal - Enhancement27624 - Start
	/**
	 * search plan details
	 */
	public void searchPlanDetails() throws Exception{
		log.info("searchPlanDetails");
		this.getManagedBean().setSearch(true);
		boolean isEmpty = true;
		
		if(!StringUtils.isBlank(this.getManagedBean().getPlanSearchBean().getTitleOrDesc()) ||
			this.getManagedBean().getPlanSearchBean().getCategoryType() !=null ||
			this.getManagedBean().getPlanSearchBean().getProcurementType() !=null ||
			this.getManagedBean().getPlanSearchBean().getInvitationDateFrom() !=null ||
			this.getManagedBean().getPlanSearchBean().getInvitationDateTo() !=null ||
			this.getManagedBean().getPlanSearchBean().getUpdatedDateFrom() !=null ||
			this.getManagedBean().getPlanSearchBean().getUpdatedDateTo() !=null ||
			this.getManagedBean().getPlanSearchBean().getStatus() !=null
			){
			isEmpty = false;
		}
		  
		if(isEmpty){
//			throw new EpException(MessageConst.ERROR_730027);
		}
		this.getManagedBean().setPlanDetailSearch(true);
		//210120@akmal - Enhancement27624 - Start
		if((this.getManagedBean().getSelectedPlan().isUpdatable() && !this.getManagedBean().isTrackingDiary())
				|| this.getManagedBean().isFromTaskList()){
			/*log.info("getPpProcurementItems-"+this.getManagedBean().getSelectedPlan().getPpProcurementItems().size());
			log.info("getPpExpenditureItems-"+this.getManagedBean().getSelectedPlan().getPpExpenditureItems().size());*/
			if(!this.getManagedBean().getSelectedPlan().getPpProcurementItems().isEmpty() || !this.getManagedBean().getSelectedPlan().getPpExpenditureItems().isEmpty()){
//				savePlan(BpmOutcomeConst.BPM_SAVE);
				saveSubPlan(BpmOutcomeConst.BPM_SAVE);
			}
		}
		if(this.getManagedBean().isFromTaskList()){
			loadPlanDetails(this.getManagedBean().getPlanId(), false);
		}else{//210116@azza - Enhancement24975- Start
			loadPlanDetails(this.getManagedBean().getPlanId(), true);
		}//210116@azza - Enhancement24975- End
		this.getManagedBean().setPrevAction("search");
		//210120@akmal - Enhancement27624 - End
	}
	//210111@akmal - Enhancement27624 - End
	
	public ProcurementPlanReqDTO parseSearchBean(ProcurementPlanSearchBean searchbean, ProcurementPlanReqDTO reqDTO){
		
		if(!this.getManagedBean().isPlanDetailReset()){
			reqDTO.setTitleOrDesc(searchbean.getTitleOrDesc());
			reqDTO.setCategoryType(searchbean.getCategoryType());
			reqDTO.setProcurementType(searchbean.getProcurementType());
			reqDTO.setInvitationDateFrom(searchbean.getInvitationDateFrom());
			reqDTO.setInvitationDateTo(searchbean.getInvitationDateTo());
			reqDTO.setUpdatedDateFrom(searchbean.getUpdatedDateFrom());
			reqDTO.setUpdatedDateTo(searchbean.getUpdatedDateTo());
			reqDTO.setStatus(searchbean.getPlanDetstatus());
			reqDTO.getvPmGolist().addAll(this.getManagedBean().getvPmGolist());
		}
		reqDTO.setPlanType(this.getManagedBean().getSelectedPlan().getPlanType().getParameterCode());
		
		return reqDTO;
	}
	
	//210801@akmal - Enhancement27624 - Start
	public void resetPlanDetailSearch() throws Exception{
		log.info("resetPlanDetailSearch");
		this.getManagedBean().setPlanDetailReset(true);
		this.getManagedBean().getvPmGolist().clear();
		this.getManagedBean().getvPmGomap().clear();
		this.getManagedBean().getPlanSearchBean().setProcurementType(null);
		this.getManagedBean().getPlanSearchBean().setCategoryType(null);
		this.getManagedBean().getPlanSearchBean().setTitleOrDesc(null);
		this.getManagedBean().getPlanSearchBean().setInvitationDateFrom(null);
		this.getManagedBean().getPlanSearchBean().setInvitationDateTo(null);
		this.getManagedBean().getPlanSearchBean().setUpdatedDateFrom(null);
		this.getManagedBean().getPlanSearchBean().setUpdatedDateTo(null);
		this.getManagedBean().getPlanSearchBean().setStatus(null);
		this.getManagedBean().getPlanSearchBean().setPlanDetstatus(99L);
		this.getManagedBean().setSelectedCatSearch(null);
		
		//210120@akmal - Enhancement27624 - Start
		if((this.getManagedBean().getSelectedPlan().isUpdatable() && !this.getManagedBean().isTrackingDiary())
				|| this.getManagedBean().isFromTaskList()){
			/*log.info("getPpProcurementItems-"+this.getManagedBean().getSelectedPlan().getPpProcurementItems().size());
			log.info("getPpExpenditureItems-"+this.getManagedBean().getSelectedPlan().getPpExpenditureItems().size());*/
			if(!this.getManagedBean().getSelectedPlan().getPpProcurementItems().isEmpty() || !this.getManagedBean().getSelectedPlan().getPpExpenditureItems().isEmpty()){
//				savePlan(BpmOutcomeConst.BPM_SAVE);
				saveSubPlan(BpmOutcomeConst.BPM_SAVE);
			}
		}
		//210120@akmal - Enhancement27624 - End
		if(this.getManagedBean().isFromTaskList()){
			generatePageModeForProcess(this.getManagedBean().getTaskName());
			this.loadPlanDetails(this.getManagedBean().getPlanId(), false);
			this.getManagedBean().setMaxYear("31/12/"+Calendar.getInstance().get(Calendar.YEAR));
			if("approved".equalsIgnoreCase(getStatusDescById(this.getManagedBean().getPpWorkflowStatus().getPmStatus().getStatusId())))
				this.getManagedBean().setDisablePlanSearchDet(true);
//			if(this.getManagedBean().getPrevAction().equalsIgnoreCase("search"))
			this.getManagedBean().setPrevAction("reset");
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAINTAIN);
		}
		else
			redirectToMaintainPlan(this.getManagedBean().getPlanId(), 
				this.getManagedBean().getBtnValue(), this.getManagedBean().isTrackingDiary());
	}
	//210801@akmal - Enhancement27624 - End

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
						//log.info("+ getStatus "  + getManagedBean().getPlanSearchBean().getStatus());
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
	
	/**
	 * Get the user role value
	 * 
	 * @return String
	 */
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
	
	/**
	 * To generate search options in ppOrgProfilePopUp.xhtml
	 */
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
	 * Pagination for org profile list
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
	
	/**
	 * To populate save object after select PTJ in LOV
	 */	
	public void populateSaveObject(){
		if(getManagedBean().getSelectedPTJ() != null){
			getManagedBean().getPlanSearchBean().setPtj(getManagedBean().getSelectedPTJ());
		}
	}
	
	/**
	 * To Retrieve Procurement Plan Billing Address
	 * 
	 * @return String
	 */
	public String retrievePpBillingAddr() {
		if(getManagedBean().getSelectedPlan().getOrgProfileAddress() != null){
			//log.info("[#20191212] - OrgProfileAddress NOT NULL");
			return getPpFullAddress(getManagedBean().getSelectedPlan().getOrgProfileAddress());
		}else{
			//log.info("[#20191212] - OrgProfileAddress IS NULL");
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setOrgProfileId(getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(resDTO.getAddressTypeList() !=null && !resDTO.getAddressTypeList().isEmpty()){
				PmAddress address = null;
				for(PmAddressType addressType : resDTO.getAddressTypeList() ){
					if(addressType.getRecordStatusFlg()==1){
						address = addressType.getPmAddress();
						break;
					}
				}
				
				if(address != null){
					return getFullAddress(address);
				}
				return "";
			}else{
				return "";
			}
		}
	}
	
	/**
	 * To construct Ministry condition fields for LOV
	 * 
	 * @return String
	 */
	public String constructMinistryConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.MINISTRY.value());
		sb.append("|recordStatus^1");
		return sb.toString();
	}
	
	/**
	 * To construct Kumpulan PTJ condition fields for LOV
	 * 
	 * @return String
	 */
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
	
	/**
	 * To construct PTJ condition fields for LOV
	 * 
	 * @return String
	 */
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
	
	/**
	 * Post Action Listener : to set PTJ in PlanSearchBean after PTJ has been selected in LOV
	 */	
	public void updateSearchCriteria(BaseEntity entity) {
		log.info("updateSearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPlanSearchBean()
				.setPtj(orgProfile);
	}
	
	/**
	 * Post Action Listener : to set Kumpulan PTJ in PlanSearchBean after Kumpulan PTJ has been selected in LOV
	 */	
	public void updateKPTJSearchCriteria(BaseEntity entity) {
		log.info("updateKPTJSearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPlanSearchBean()
				.setKptj(orgProfile);
		
		resetPtjOrgProfile(); //[Bug#27032]
	}
	
	/**
	 * Post Action Listener : to set Ministry in PlanSearchBean after Ministry has been selected in LOV
	 */	
	public void updateMinistrySearchCriteria(BaseEntity entity) {
		log.info("updateMinistrySearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPlanSearchBean()
				.setMinistry(orgProfile);
		
		resetPtjGroupOrgProfile(); //[Bug#27032]
	}
	
	/**
	 * To setup org profile details (ministry, kumpulan ptj and ptj)
	 */
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
						//Bug#29887@07012021 - Start 
						getManagedBean().setSelectedMinistry(getManagedBean().getCurrentActiveOrg()); 
						getManagedBean().setDisabledMinistryLov(true);
						getManagedBean().getPlanSearchBean().setMinistry(getManagedBean().getCurrentActiveOrg());
						//Bug#29887@07012021 - End
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
	
	/**
	 * To Retrieve Plan Type List
	 * 
	 * @return List<PmParameterDesc> 
	 */
	public List<PmParameterDesc> retrievePplanType(){
		log.info("retrievePplanType");		
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.PLAN_TYPE, null);
	}
	
	/**
	 * reset action after change search plan type
	 * 
	 * @param paramCode
	 */
	public void changeSearchPlanType(String paramCode){
		if(paramCode != null){
			this.getManagedBean().getPlanSearchBean().setStatus(null);
			this.getManagedBean().getPlanSearchBean().setPlanTitle(null);
		}
	}
	
	//200911@akmal - Enhancement27624 - Start
	/**
	 * To Retrieve Plan Type category List
	 * 
	 * @return List<PmParameterDesc> 
	 */
	public List<PmParameterDesc> retrievePplanTypeCategory(){
		
		log.info("retrievePplanTypeCategory");		
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.PROCUREMENT_TYPE_CATEGORY_PP, null);
	}
	
	/**
	 * To Retrieve Plan procurement method
	 * 
	 * @return List<PmParameterDesc> 
	 */
	//201228@akmal - Enhancement27624 - Start
	public List<PmParameterDesc> retrievePplanProcurementMethod(){
		log.info("retrievePplanProcurementMethod - Start");
		List<PmProcurementCatType> procurementCatTypeList = new ArrayList<PmProcurementCatType>();

		String languageCode = getFacesScopes().getUserLocale().getLanguage();
		if(this.getManagedBean().getSelectedCatSearch()!=null)
			procurementCatTypeList = PpCommonUtil.retrieveProcurementTypeListByCatId(languageCode, this.getManagedBean().getSelectedCatSearch().getParameterId());
			
		List<PmParameterDesc> arrlist = ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.PROCUREMENT_METHOD, null);
		List<PmParameterDesc> filteredlist = new ArrayList<PmParameterDesc>();
		
//		log.info("procurementCatTypeList size - "+procurementCatTypeList.size());
//		for(int i=0; i<procurementCatTypeList.size(); i++){
//			log.info("procurementCatTypeList getProcurementType().getParameterCode - "+procurementCatTypeList.get(i).getProcurementType().getParameterCode());
//			log.info("procurementCatTypeList getProcurementType().getParameterType - "+procurementCatTypeList.get(i).getProcurementType().getParameterType());
//			log.info("procurementCatTypeList getProcurementType().getParameterId - "+procurementCatTypeList.get(i).getProcurementType().getParameterId());
//			log.info("==================================================== ");
//		}
		
//		log.info("arrlist size - "+arrlist.size());
//		for(int i=0; i<arrlist.size(); i++){
//			log.info("arrlist getPmParameter().getParameterCode - "+arrlist.get(i).getPmParameter().getParameterCode());
//			log.info("arrlist getPmParameter().getParameterType - "+arrlist.get(i).getPmParameter().getParameterType());
//			log.info("arrlist getPmParameter().getParameterId - "+arrlist.get(i).getPmParameter().getParameterId());
//			log.info("==================================================== ");
//		}
		if(!procurementCatTypeList.isEmpty()){
			for(PmProcurementCatType cattype : procurementCatTypeList){
				for(PmParameterDesc paramdesc : arrlist){
					if(cattype.getProcurementType().getParameterId().equals(paramdesc.getPmParameter().getParameterId())){
						filteredlist.add(paramdesc);
						break;
					}
				}
			}
		}
		if(filteredlist.isEmpty()){ 
			//log.info("filteredlist.isEmpty size - "+filteredlist.size());
			//34422 - start - adding new status and hide old status
			//#34156 10/11/2022- Start
			for(PmParameterDesc paramdesc : arrlist){
				if((!paramdesc.getPmParameter().getParameterCode().equals(ProcurementPlanConst.LIMITED_TENDER)) 
						&& (!paramdesc.getPmParameter().getParameterCode().equals(ProcurementPlanConst.SELECTIVE_TENDER))  ){
					log.info("arrlist - "+paramdesc.getPmParameter().getParameterId()+ " - " + paramdesc.getCodeName());
					filteredlist.add(paramdesc);
				}
			}
			//34422 end - new status
		}
		//#34156 10/11/2022- End
		log.info("retrievePplanProcurementMethod - End");
		return filteredlist;
	}
	//201228@akmal - Enhancement27624 - Start
	
	/**
	 * Gets the Status DropDown Values.
	 *
	 * @return SelectItem[]
	 */
	public SelectItem[] getRecordStatusDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recordStatusList = new ArrayList<String>();
		recordStatusList.add(ParameterConst.RECORD_STATUS_ACTIVE);
		recordStatusList.add(ParameterConst.RECORD_STATUS_INACTIVE);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode",SearchOperator.OPERATOR_IN.value(), recordStatusList));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.RECORD_STATUS, searchBean);
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.pp.all", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale()));
		
		return items;
	}
	
	//201228@akmal - Enhancement27624 - End

	/**
	 * To Retrieve Plan Status List
	 * 
	 * @return SelectItem[]
	 */
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
	
	/**
	 * To Retrieve Plan Doc Type
	 * @param plan
	 * @return String
	 */
	public String retrievePlanDocType(PpPlan plan){
		return ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(plan.getPlanType().getParameterCode()) 
		? ParameterConst.DOCTYPE_PLAN_PROCUREMENT
		: ParameterConst.DOCTYPE_PLAN_EXPENDITURE;
	}
	
	/**
	 * To get Procurement Category Lables 
	 * 
	 * @param parameter
	 * @return String
	 */
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
	
	/**
	 * Post Action Listener : To Validate Ep Item B in LOV
	 * 
	 * @param entity
	 * @param index
	 */
	public void validateEpItemBLov(BaseEntity entity,int index){
		PmGlAccount glAcc = (PmGlAccount) entity;
		List<PpExpenditureItem> epItems = this.getManagedBean().getEpItemsB();
		validateEpItems(epItems, glAcc,index,"b");
	}
	
	/**
	 * Post Action Listener : To Validate Ep Item H in LOV
	 * 
	 * @param entity
	 * @param index
	 */
	public void validateEpItemHLov(BaseEntity entity,int index){
		PmGlAccount glAcc = (PmGlAccount) entity;
		List<PpExpenditureItem> epItems = this.getManagedBean().getEpItemsH();
		validateEpItems(epItems, glAcc,index,"h");
	}
	
	/**
	 * Post Action Listener : To Validate Ep Item P in LOV
	 * 
	 * @param entity
	 * @param index
	 */
	public void validateEpItemPLov(BaseEntity entity,int index){
		PmGlAccount glAcc = (PmGlAccount) entity;
		List<PpExpenditureItem> epItems = this.getManagedBean().getEpItemsP();
		validateEpItems(epItems, glAcc,index,"p");
	}
	
	/**
	 * Post Action Listener : To Validate Ep Item S in LOV
	 * 
	 * @param entity
	 * @param index
	 */
	public void validateEpItemSLov(BaseEntity entity,int index){
		PmGlAccount glAcc = (PmGlAccount) entity;
		List<PpExpenditureItem> epItems = this.getManagedBean().getEpItemsS();
		validateEpItems(epItems, glAcc,index,"s");
	}
	
	/**
	 * Post Action Listener : To Validate Ep Item T in LOV
	 * 
	 * @param entity
	 * @param index
	 */
	public void validateEpItemTLov(BaseEntity entity,int index){
		PmGlAccount glAcc = (PmGlAccount) entity;
		List<PpExpenditureItem> epItems = this.getManagedBean().getEpItemsT();
		validateEpItems(epItems, glAcc,index,"t");
	}
	
	/**
	 * To Validate Expenditure Plan Items
	 * 
	 * @param epItems
	 * @param glAccount
	 * @param index
	 * @param type
	 */
	private void validateEpItems(List<PpExpenditureItem> epItems,PmGlAccount glAccount,int index,String type){
		if(!epItems.isEmpty()){
			List <Long> soIds = new ArrayList<Long>();
			for(PpExpenditureItem epItem: epItems){
				if(DbValueConst.RECORD_STATUS_ACTIVE ==epItem.getRecordStatusFlg()){					
					soIds.add(epItem.getPmGlAccount().getGlAccountId());
				}
			}
			if(!soIds.isEmpty()){
				if(!validationUniqueIds(soIds)){	
					RequestContext.getCurrentInstance().execute("show"+type+"LovMsg();");
					String duplicateCode = glAccount.getGlAccCode() + " " +glAccount.getDescription();
					epItems.get(index).setPmGlAccount(new PmGlAccount());
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									MessageUtils.getMessage(MessageConst.INFO_721223,
											new String[] { duplicateCode }, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));				
				}else{
					RequestContext.getCurrentInstance().execute("hide"+type+"LovMsg();");
				}
			}
		}
	}
	
	/**
	 * Action after select GL Account In LOV
	 * 
	 * @param glAccount
	 * @param index
	 * @param type
	 */
	public void selectGlAccountAction(PmGlAccount glAccount,Integer index,String type){
		if(StringUtils.isNotBlank(type)){
			List<PpExpenditureItem> epItems = null;
			switch (type) {
			case "b" :
				epItems = getManagedBean().getEpItemsB();
				break;
			case "h":
				epItems = getManagedBean().getEpItemsH();
				break;
			case "p":
				epItems = getManagedBean().getEpItemsP();
				break;
			case "s":
				epItems = getManagedBean().getEpItemsS();
				break;
			case "t":
				epItems = getManagedBean().getEpItemsT();
				break;
			default:
				epItems = new ArrayList<PpExpenditureItem>();
				break;
			}
			
			if(CollectionUtils.isNotEmpty(epItems)){
				epItems.get(index).setPmGlAccount(glAccount);
				validateEpItems(epItems, glAccount,index,type);
			}
		}
	}
	
	//210801@akmal - Enhancement27624 - Start
	/**
	 * Action after select list of GL Account In LOV
	 * 
	 * @param vPmGoList
	 */
	public void selectGlAccountAction(List<VPmGo> vPmGoList){
		Iterator<VPmGo> iterator = vPmGoList.iterator();
		while (iterator.hasNext()) {
			VPmGo obj = iterator.next();
		    if (!this.getManagedBean().getvPmGomap().containsKey(obj.getGlAccCode())) {
		    	this.getManagedBean().getvPmGomap().put(obj.getGlAccCode(),obj);
		    }
		}
		this.getManagedBean().getvPmGolist().clear();
		this.getManagedBean().getvPmGolist().addAll(this.getManagedBean().getvPmGomap().values());
	}
	//210801@akmal - Enhancement27624 - End

	/**
	 * To Retrieve Kumpulan PTJ Id
	 * 
	 * @return Long
	 */
	public Long retrieveKptjId(){		
		PmOrgProfile orgProfile = retrieveJabatanOrgProfile(getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
		return orgProfile != null && orgProfile.getOrgProfileId() != null ? orgProfile.getOrgProfileId() : null;
		
	}
	
	public boolean isApprovedMode(PpPlan plan){
		boolean isApproved = false;
		if(plan != null && plan.getStatus()!=null && (ProcurementPlanConst.PPP_APPROVED.equals(plan.getStatus().getStatusId()) 
				|| ProcurementPlanConst.PPE_APPROVED.equals(plan.getStatus().getStatusId()))){
			isApproved = true;
		}
		return isApproved;
	}
	
	private void getFinalItemList(PpPlan pplan){
		log.info("inside getFinalItemList");
		
		if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(pplan.getPlanType().getParameterCode())){			

			//Add existing item to list for status: 2 - Cancelled
			//log.info("[SIR#12802] - item list is for plan type 'E'");
			
			//create new list for all deleted item
			if(this.getManagedBean().getDeleteEpItemsAll()==null){
				this.getManagedBean().setDeleteEpItemsAll(new ArrayList<PpExpenditureItem>());
			}
			
			//create new list for expenditure item
			if(pplan.getPpExpenditureItems()==null){
				pplan.setPpExpenditureItems(new ArrayList<PpExpenditureItem>());
			}
			
			//get deleted item for VOT-B (for existing record only)
			if(this.getManagedBean().getDeleteEpItemsB()!=null 
					&& !this.getManagedBean().getDeleteEpItemsB().isEmpty()){
				//log.info("[SIR#12802] - VOT B: list size for all deleted item BEFORE: " + this.getManagedBean().getDeleteEpItemsAll().size());
				List <PpExpenditureItem> delExpenditureItem = this.getManagedBean().getDeleteEpItemsB();				
				
					Iterator<PpExpenditureItem> itr = delExpenditureItem.iterator(); 
			        while(itr.hasNext()) { 
			        	PpExpenditureItem expenditureItem = (PpExpenditureItem)itr.next(); 
			            if(!expenditureItem.isBrandNew()) { 
			            	if(expenditureItem.getExpenditureItemId()!= null){
			            	//log.info("[SIR#12802] - item is not new but status is 2, add to all deleted list!");
			            	this.getManagedBean().getDeleteEpItemsAll().add(expenditureItem);
			            	}
			            }
			        }				

				//log.info("[SIR#12802] - VOT B: list size for all deleted item AFTER: " + this.getManagedBean().getDeleteEpItemsAll().size());
			}
			
			//get deleted item for VOT-T (for existing record only)
			if(this.getManagedBean().getDeleteEpItemsT()!=null 
					&& !this.getManagedBean().getDeleteEpItemsT().isEmpty()){
				//log.info("[SIR#12802] - VOT T: list size for all deleted item BEFORE: " + this.getManagedBean().getDeleteEpItemsAll().size());
				List <PpExpenditureItem> delExpenditureItem = this.getManagedBean().getDeleteEpItemsT();				
				
					Iterator<PpExpenditureItem> itr = delExpenditureItem.iterator(); 
			        while(itr.hasNext()) { 
			        	PpExpenditureItem expenditureItem = (PpExpenditureItem)itr.next(); 
			            if(!expenditureItem.isBrandNew()) { 
			            	if(expenditureItem.getExpenditureItemId()!= null){
			            	//log.info("[SIR#12802] - item is not new but status is 2, add to all deleted list!");
			            	this.getManagedBean().getDeleteEpItemsAll().add(expenditureItem);
			            }
			           }
			        }				

				//log.info("[SIR#12802] - VOT T: list size for all deleted item AFTER: " + this.getManagedBean().getDeleteEpItemsAll().size());
			}
			
			//get deleted item for VOT-P (for existing record only)
			if(this.getManagedBean().getDeleteEpItemsP()!=null 
					&& !this.getManagedBean().getDeleteEpItemsP().isEmpty()){
				//log.info("[SIR#12802] - VOT P: list size for all deleted item BEFORE: " + this.getManagedBean().getDeleteEpItemsAll().size());
				List <PpExpenditureItem> delExpenditureItem = this.getManagedBean().getDeleteEpItemsP();				
				
					Iterator<PpExpenditureItem> itr = delExpenditureItem.iterator(); 
			        while(itr.hasNext()) { 
			        	PpExpenditureItem expenditureItem = (PpExpenditureItem)itr.next(); 
			            if(!expenditureItem.isBrandNew()) { 
			            	if(expenditureItem.getExpenditureItemId()!= null){
			            	//log.info("[SIR#12802] - item is not new but status is 2, add to all deleted list!");
			            	this.getManagedBean().getDeleteEpItemsAll().add(expenditureItem);
			            }
			        }				
			     }

				//log.info("[SIR#12802] - VOT P: list size for all deleted item AFTER: " + this.getManagedBean().getDeleteEpItemsAll().size());
			}
			
			//get deleted item for VOT-S (for existing record only)
			if(this.getManagedBean().getDeleteEpItemsS()!=null 
					&& !this.getManagedBean().getDeleteEpItemsS().isEmpty()){
				//log.info("[SIR#12802] - VOT S: list size for all deleted item BEFORE: " + this.getManagedBean().getDeleteEpItemsAll().size());
				List <PpExpenditureItem> delExpenditureItem = this.getManagedBean().getDeleteEpItemsS();				
				
					Iterator<PpExpenditureItem> itr = delExpenditureItem.iterator(); 
			        while(itr.hasNext()) { 
			        	PpExpenditureItem expenditureItem = (PpExpenditureItem)itr.next(); 
			            if(!expenditureItem.isBrandNew()) { 
			            	if(expenditureItem.getExpenditureItemId()!= null){
			            	//log.info("[SIR#12802] - item is not new but status is 2, add to all deleted list!");
			            	this.getManagedBean().getDeleteEpItemsAll().add(expenditureItem);
			            }
			        }				
			    }
				//log.info("[SIR#12802] - VOT S: list size for all deleted item AFTER: " + this.getManagedBean().getDeleteEpItemsAll().size());
			}
			
			//get deleted item for VOT-H (for existing record only)
			if(this.getManagedBean().getDeleteEpItemsH()!=null 
					&& !this.getManagedBean().getDeleteEpItemsH().isEmpty()){
				//log.info("[SIR#12802] - VOT H: list size for all deleted item BEFORE: " + this.getManagedBean().getDeleteEpItemsAll().size());
				List <PpExpenditureItem> delExpenditureItem = this.getManagedBean().getDeleteEpItemsH();				
				
					Iterator<PpExpenditureItem> itr = delExpenditureItem.iterator(); 
			        while(itr.hasNext()) { 
			        	PpExpenditureItem expenditureItem = (PpExpenditureItem)itr.next(); 
			            if(!expenditureItem.isBrandNew()) { 
			            	if(expenditureItem.getExpenditureItemId()!= null){
			            	//log.info("[SIR#12802] - item is not new but status is 2, add to all deleted list!");
			            	this.getManagedBean().getDeleteEpItemsAll().add(expenditureItem);
			            }
			        }		
			     }

				//log.info("[SIR#12802] - VOT H: list size for all deleted item AFTER: " + this.getManagedBean().getDeleteEpItemsAll().size());
			}
			
			//update list in expenditure item (for existing record only)
			//log.info("[SIR#12802] - Updating list in expenditure item");
			if(this.getManagedBean().getDeleteEpItemsAll()!=null 
					&& !this.getManagedBean().getDeleteEpItemsAll().isEmpty()){
				//log.info("[SIR#12802] - Comparing record between deleted item and expenditure item");
				List <PpExpenditureItem> delExpenditureItemAll = this.getManagedBean().getDeleteEpItemsAll();
				long expItemId,votTypeId = 0L;				
				Iterator<PpExpenditureItem> itr = delExpenditureItemAll.iterator();					
				
				//iterate through all deleted item list.
		        while(itr.hasNext()) { 
		        	PpExpenditureItem delExpenditureItem = (PpExpenditureItem)itr.next(); 
		        	
		        
		        	expItemId = delExpenditureItem.getExpenditureItemId().longValue();
		        	votTypeId = delExpenditureItem.getVotFundType().getParameterId().longValue();
		        	
		        	for(PpExpenditureItem ppExpenditureItem : pplan.getPpExpenditureItems()) {   		
			        	if((ppExpenditureItem.getExpenditureItemId() != null) && 
			        			(ppExpenditureItem.getExpenditureItemId().longValue() == expItemId && 
			        			ppExpenditureItem.getVotFundType().getParameterId().longValue() == votTypeId)) {
			        		//log.info("[SIR#12802] - Found match!!");
			        		//log.info("[SIR#12802] - Update status for matched expenditure item");
			        		ppExpenditureItem.setRecordStatusFlg(delExpenditureItem.getRecordStatusFlg());
			        		break;
			        	}
			        }
		       }
		        //reset or clear list in all deleted item list after finished.
		        this.getManagedBean().getDeleteEpItemsAll().clear();
		        //log.info("[SIR#12802] - All Expenditure Deleted item list size after compare:" + this.getManagedBean().getDeleteEpItemsAll().size());
		        //log.info("[SIR#12802] - Expenditure item list size after compare:" + pplan.getPpExpenditureItems().size());		        
			}			
		}
		
		else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(pplan.getPlanType().getParameterCode())){			

			//Add existing item to list for status: 2 - Cancelled
			//log.info("[SIR#12802] - item list is for plan type 'P'");
			if(pplan.getPpProcurementItems()!=null && !pplan.getPpProcurementItems().isEmpty()){
				//log.info("[SIR#12802] - item list size BEFORE: " + pplan.getPpProcurementItems().size());
				List <PpProcurementItem> delProcurementItem = this.getManagedBean().getDeletePpItems();
				
				if(delProcurementItem!=null && !delProcurementItem.isEmpty()) {
					Iterator<PpProcurementItem> itr = delProcurementItem.iterator(); 
			        while(itr.hasNext()) { 
			        	PpProcurementItem procurementItem = (PpProcurementItem)itr.next(); 
			            if(!procurementItem.isBrandNew()) { 
			            	//log.info("[SIR#12802] - item is not new but status is 2, add to list!");
			            	pplan.getPpProcurementItems().add(procurementItem);
			            }
			        }
				}
				//log.info("[SIR#12802] - item list size AFTER: " + pplan.getPpProcurementItems().size());
			}
		}
	}
	
	//#24975
	/**
	 * This method used to invoke 
	 * and initiate the item specification dialog.
	 */
	public void viewreasonUpdateDlg() {
		//getManagedBean().setSelectedItemForSpec(ctContractItem);
		log.info("view dialog");
		RequestContext.getCurrentInstance().execute("reasonUpdateDlg.show()");
	}
	
	/**
	 * The method used for rendering the item price
	 * of selected service item that to be displayed on screen.
	 * @param list 
	 * @param 
	 * @return 
	 */
	
	public void addUpdateInfo(PpPlan ppPlan, Integer index) {
		log.info("addUpdateInfo");
		log.info("Original index= " + index);

		List<PpProcurementItem> procurementList = null;
		List<PpProcurementItem> procurementLinkedList = new LinkedList<PpProcurementItem>();
		List<PpProcurementItem> newProcurementList = new ArrayList<PpProcurementItem>();
		PpProcurementItem newPpItem = new PpProcurementItem();
		// boolean isReasonEmpty;

		PmRole roleCode = new PmRole();

		if (this.getManagedBean().getSelectedPlan().getPlanYear() == null) {
			throw new EpException(MessageConst.ERROR_730028);
		}

		if (ppPlan.getPpProcurementItems() != null && !ppPlan.getPpProcurementItems().isEmpty()) {

			procurementList = this.getManagedBean().getSelectedPlan().getPpProcurementItems();

			for (PpProcurementItem procItem : procurementList) {
				procurementLinkedList.add(procItem);
			}

			// PpProcurementItem clonedPpItem = procurementList.get(index);
			PpProcurementItem originalPpItem = procurementLinkedList.get(index);

			/*
			 * newPpItem = (PpProcurementItem) procurementList. get(index).cloneEntity();
			 */
			newPpItem.setCloned(true);

			// #32730 - Start
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			int nextYear = currentYear + 1;

			if (Calendar.getInstance().get(Calendar.YEAR) == this.getManagedBean().getSelectedPlan().getPlanYear()) {
				newPpItem.setInvitationDate(new Date());
			} else if (nextYear == this.getManagedBean().getSelectedPlan().getPlanYear()) {
				Calendar date = new GregorianCalendar((nextYear), Calendar.JANUARY, 1);
				newPpItem.setInvitationDate(date.getTime());
			} else {
				// previous year
				Long preYear = this.getManagedBean().getSelectedPlan().getPlanYear();
				Calendar datePreYear = new GregorianCalendar((preYear.intValue()), Calendar.DECEMBER, 31);
				newPpItem.setInvitationDate(datePreYear.getTime());
			}
			// #32730 - End
			// Always allow user to clone without key in reason
			/*
			 * if (originalPpItem.getPpUpdateReasons().isEmpty()) { PpUpdateReason clonedUpdateReason = new PpUpdateReason(); clonedUpdateReason.setReasonDesc("\u200B"); isReasonEmpty =
			 * clonedUpdateReason.getReasonDesc() == null || clonedUpdateReason.getReasonDesc().trim().length() == 0; if (!isReasonEmpty) { originalPpItem.getPpUpdateReasons().add(clonedUpdateReason);
			 * } }
			 */

			newPpItem.setCreatedBy(getUserIdSessionFromLoginId()); // #31849 add log key
			newPpItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			newPpItem.setChangedDate(new Timestamp(new Date().getTime()));
			// 24975@06112020@azza - Start
			newPpItem.setProcurementCat(originalPpItem.getProcurementCat());
			// #34156 10/11/2022- Start
			// #34422 09/11/2023- Start
			if (!originalPpItem.getProcurementType().getParameterCode().equals(ProcurementPlanConst.SELECTIVE_TENDER)
					&& !originalPpItem.getProcurementType().getParameterCode().equals(ProcurementPlanConst.LIMITED_TENDER)) {
				newPpItem.setProcurementType(originalPpItem.getProcurementType());
			} else {
				newPpItem.setProcurementType(new PmParameter());
			}
			// #34422 09/11/2023- End
			// #34156 10/11/2022- End
			// 24975@06112020@azza - End
			newPpItem.setProcurementTitle(originalPpItem.getProcurementTitle());
			newPpItem.setProcurementDesc(originalPpItem.getProcurementDesc());
			newPpItem.setEstimatedAmt(originalPpItem.getEstimatedAmt());
			newPpItem.setEditable(true);
			newPpItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			newPpItem.setBrandNew(true);
			newPpItem.setCloneEnabled(true);
			newPpItem.setDisableForPindaan(true);
			newPpItem.setFromPpItem(originalPpItem.getProcurementItemId());
			newPpItem.setReasonForUpdateEnabled(false);
			newPpItem.setPpUpdateReasons(originalPpItem.getPpUpdateReasons());
			newPpItem.setStatusEnabled(true);
			newPpItem.setOriginal(false);
			newPpItem.setClonedItem(true);
			// 25549@09102020 - start
			newPpItem.setReviseReject(true);
			// 25549@09102020 - end
			if (newPpItem.getPpPlan() == null) {
				newPpItem.setPpPlan(ppPlan);
			}
			// #34156 - Start
			newPpItem.setNewLabcatMethodType(true);
			// #34156 - End

			// log.info("PpUpdateReasons size= "+newPpItem.getPpUpdateReasons().size());

			if (newPpItem.getPpUpdateReasons() != null && !newPpItem.getPpUpdateReasons().isEmpty()) {
				log.info("newPpItem reasonDesc= " + newPpItem.getPpUpdateReasons().get(0).getReasonDesc());
				for (PpUpdateReason newUpdateReason : newPpItem.getPpUpdateReasons()) {
					if (newUpdateReason.getRoleCode() == null) {
						roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
						newUpdateReason.setRoleCode(roleCode);
					}
					if (newUpdateReason.getDocId() == null) {
						newUpdateReason.setDocId(originalPpItem.getProcurementItemId());
					}
					if (newUpdateReason.getDocType() == null) {
						newUpdateReason.setDocType(ParameterConst.DOCTYPE_PLAN_PROCUREMENT);
					}
				}
				/*
				 * log.info("newPpItem PpUpdateReasons(): " + "reasonDesc= "+newPpItem.getPpUpdateReasons().get(0).getReasonDesc()+ ", getRole= "+newPpItem.getPpUpdateReasons().get(0).getRoleCode()+
				 * ", getDocType= "+newPpItem.getPpUpdateReasons().get(0).getDocType());
				 */
			}

			procurementLinkedList.listIterator(index + 1).add(newPpItem);

			this.getManagedBean().getSelectedPlan().setPpProcurementItems(null);

			for (PpProcurementItem linkedPpItem : procurementLinkedList) {
				newProcurementList.add(linkedPpItem);
			}

			this.getManagedBean().getSelectedPlan().setPpProcurementItems(newProcurementList);

			// log.info("Size of procurementList= "+this.getManagedBean().getSelectedPlan().getPpProcurementItems().size());

			disableEnablePpItemFields(this.getManagedBean().getSelectedPlan().getPpProcurementItems(), index + 1);

		} else {
			throw new EpException(MessageConst.ERROR_730015);
		}

	}
	 
	public void disableEnablePpItemFields(List<PpProcurementItem> items, Integer idx) {

		log.info("disableEnablePpItemFields");

		/*
		 * for (int i = 0; i < items.size(); i++) { log.info("index= "+i+" (ppItemId= "+items.get(i).getProcurementItemId()+ ") - "+items.get(i).getProcurementTitle()); }
		 */

		PpProcurementItem cloned = items.get(idx);

		if ((cloned.isCloned() && cloned.isBrandNew())) {

			log.info("...Trace log for Bug #31872 START 001....");

			cloned.setAllowAnotherPindaan(false);
			cloned.setCloneEnabled(false);
			cloned.setReasonForUpdateEnabled(false);
			cloned.setStatusEnabled(false);
			cloned.setEnableRevise(true);

			log.info("...Trace log for Bug #31872 END 001....");
		}

		/*
		 * log.info("cloned= "+cloned.getProcurementTitle()+" ("+cloned.getProcurementItemId() +"), isCloned= "+cloned.isCloned()+", isClonedItem= "
		 * +cloned.isClonedItem()+", isBrandNew= "+cloned.isBrandNew() +", allowAnotherPindaan= "+cloned.isAllowAnotherPindaan());
		 */

		idx -= 1;
		PpProcurementItem original = items.get(idx);

		// For originals that were previously clones and this time they are used to clone new ones
		if (!original.isOriginal() && original.getFromPpItem() != null) {

			log.info("...Trace log for Bug #31872 START 002....");

			original.setBrandNew(false);
			original.setOriginal(true);
			original.setCloned(false);
			original.setCloneEnabled(true);
			// #27624@322021 - start
			if (!ProcurementPlanConst.ACTION_VIEW_BTN.equals(getManagedBean().getBtnValue())) {
				this.getManagedBean().setModeEditable(true);
			}
			// #27624@322021 - end
			original.setDisableForPindaan(false);
			// this.getManagedBean().setDisable(false);
			original.setEnableRevise(false);

			log.info("...Trace log for Bug #31872 END 002....");
		} else {

			log.info("...Trace log for Bug #31872 START 003....");

			original.setEnableRevise(true);

			log.info("...Trace log for Bug #31872 END 003....");
		}

		/*
		 * log.info("original= "+original.getProcurementTitle()+" ("+original.getProcurementItemId() +"), isCloned= "+original.isCloned()+", isBrandNew= "+original.isBrandNew()
		 * +", isOriginal= "+original.isOriginal()+", fromPpItem= "+original.getFromPpItem() +", allowAnotherPindaan= "+original.isAllowAnotherPindaan());
		 */

		if (!(original.isCloned() && original.isBrandNew())) {

			log.info("...Trace log for Bug #31872 START 003...." + original.getRecordStatusFlg());

			if (!original.isRejected() && original.isAllowAnotherPindaan()) {
				original.setAllowAnotherPindaan(false);
			}
			original.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
			original.setCloneEnabled(true);
			original.setReasonForUpdateEnabled(true);
			original.setStatusEnabled(true);

			if (original.isEnableRevise()) {
				original.setEnableRevise(false);
			}

			log.info("...Trace log for Bug #31872 END 003...." + original.getRecordStatusFlg());
		}

	}
	 
    /**
	 * retrieve pp reject reason
	 * @param ppItem
	 */
	public void retrievePpUpdateReasons(PpProcurementItem ppItem) {
		log.info("retrievePpUpdateReasons");
		// log.info("pp item " + ppItem.getProcurementDesc());
		// log.info("reasons= "+ppItem.getPpUpdateReasons().size());

		if (!ppItem.isBrandNew()) {
			if (ppItem.getPpUpdateReasons() == null || ppItem.getPpUpdateReasons().isEmpty()) {
				ppItem.setPpUpdateReasons(new ArrayList<PpUpdateReason>());
				// ppItem.setPpRejectReasons(PpCommonUtil.retrieveRejectReason(ppItem.getProcurementItemId(), ParameterConst.DOCTYPE_PLAN_PROCUREMENT));
			}
		}
		// loadUpdateReasons(ppItem.getPpUpdateReasons());
		// 24975 - Start
		loadUpdateReasons(ppItem);
		// 24975 - End
		// 24975@20112020 - start
		if (ppItem.isIndicator() == true) {
			ppItem.setIndicator(false);
		}
		// 24975@20112020 - End
	}

	/**
	 * Initial or load reject reasons
	 * @param ppUpdateReasons
	 */
	 //24975@06112020@azza - Start
	public void loadUpdateReasons(PpProcurementItem ppItem) {
		log.info("loadUpdateReasons - Start");
		this.getManagedBean().setConfirmReason(false);
		this.getManagedBean().setSelectedReasonDesc("");
		this.getManagedBean().setSelectedUpdateReasons(null);
		boolean newReason = false;

		if (ppItem.getPpUpdateReasons() == null) {
			ppItem.setPpUpdateReasons(new ArrayList<PpUpdateReason>());
			// ppUpdateReasons = new ArrayList<PpUpdateReason>();
		}

		if (!ppItem.getPpUpdateReasons().isEmpty()) {
			if (!ppItem.getPpUpdateReasons().get(0).isBrandNew()) {
				// newReason = true;
			}
		}

		// if reason is empty
		if (ppItem.getPpUpdateReasons().isEmpty() || ppItem.getPpUpdateReasons().size() == 0) {
			newReason = true;
		}
		// log.info("newReason: "+newReason);
		// if(newReason && this.getManagedBean().isModeReviewEditable()){
		if (newReason) {
			log.info("new reason added");
			PpUpdateReason reason = new PpUpdateReason();
			reason.setEditable(true);
			reason.setCreatedBy(getUserIdSessionFromLoginId()); // #31849 add log key
			reason.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			reason.setChangedBy(reason.getCreatedBy());
			reason.setChangedDate(reason.getCreatedDate());
			reason.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
			reason.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
			PmRole roleCode = new PmRole();
			roleCode.setRoleCode(getOneRoleByStatus(this.getManagedBean().getSelectedPlan()));
			reason.setRoleCode(roleCode);
			// reason.setDocId(ppUpdateReasons.getProcurementItemId());
			// ppUpdateReasons.add(0, reason);
			ppItem.getPpUpdateReasons().add(0, reason);
			// log.info("doc id : " + reason.getDocId());
		}

		for (PpUpdateReason setEditRemark : ppItem.getPpUpdateReasons()) {
			if (ppItem.isChangePencilToNote() == true) {
				log.info("PpUpdateReason true");
				this.getManagedBean().setDisable(true);
			} else {
				log.info("PpUpdateReason false");
				this.getManagedBean().setDisable(false);
			}
		}

		this.getManagedBean().setSelectedUpdateReasons(ppItem.getPpUpdateReasons());
		this.getManagedBean().setSelectedReasonDesc(ppItem.getPpUpdateReasons().isEmpty() ? "" : ppItem.getPpUpdateReasons().get(0).getReasonDesc());
		// 24975@06112020@azza - End
		log.info("loadUpdateReasons - End");
	}
	
	/**
	 * Get new rejection reason from UI
	 * @param ppPlan
	 * @return List <PpUpdateReason>
	 */
	// 28012021@27624 - start
	private List<PpUpdateReason> deleteUpdateReasons(PpPlan ppPlan) {
		log.info("getAllUpdateReasons");
		List<PpUpdateReason> updateReasons = new ArrayList<PpUpdateReason>();
		if (ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())) {
			log.info("----------Before PpPlan save----------");
			int itemIndex = 0;
			for (PpProcurementItem ppProcurementItem : ppPlan.getPpProcurementItems()) {
				if (ppProcurementItem.getPpUpdateReasons() != null && !ppProcurementItem.getPpUpdateReasons().isEmpty()) {
					if (ppProcurementItem.isSearchReset() && !this.getManagedBean().isPlanDetailSearch() && !this.getManagedBean().isPlanDetailReset()) {
						PpUpdateReason updateReason = ppProcurementItem.getPpUpdateReasons().get(0);
						if (updateReason != null && !StringUtils.isBlank(updateReason.getReasonDesc())) {
							updateReason.setItemSeq(itemIndex);
							updateReason.setDocId(ppProcurementItem.getProcurementItemId());
							updateReason.setDocType(ParameterConst.DOCTYPE_PLAN_PROCUREMENT);
							ppProcurementItem.setSearchReset(false);
							if (updateReason.getRoleCode() == null || StringUtils.isBlank(updateReason.getRoleCode().getRoleCode())) {
								log.info("set setRemarkRole");
								PmRole roleCode = new PmRole();
								roleCode.setRoleCode(getOneRoleByStatus(ppPlan));
								updateReason.setRoleCode(roleCode);
								updateReason.setPpWorkflowStatus(this.getManagedBean().getSelectedPlan().getCurrentStatus());
							}
							updateReasons.add(updateReason);
						}
					} else {
						List<PpUpdateReason> PpUpdateReasonDel = PpCommonUtil.retrieveUpdateReason(ppProcurementItem.getProcurementItemId(), ParameterConst.DOCTYPE_PLAN_PROCUREMENT);

						for (PpUpdateReason updateReasonDel : PpUpdateReasonDel) {
							PpUpdateReason updateReason = ppProcurementItem.getPpUpdateReasons().get(0);
							if (updateReasonDel.getDocId().equals(ppProcurementItem.getProcurementItemId())) {
								if (updateReasonDel.getReasonDesc() != null && updateReason.getReasonDesc() == null || updateReason.getReasonDesc().isEmpty()) {
									// log.info("first getReasonDesc" + updateReasonDel.getReasonDesc());
									updateReasonDel.setItemSeq(itemIndex);
									updateReasonDel.setDocId(ppProcurementItem.getProcurementItemId());
									updateReasonDel.setDocType(ParameterConst.DOCTYPE_PLAN_PROCUREMENT);
									updateReasonDel.setReasonDesc(updateReasonDel.getReasonDesc());
									updateReasonDel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
									updateReasons.add(updateReasonDel);
								}
							}

						}
					}

					itemIndex++;
				}
			}
		}// 04122020@24975 - end
			// log.info("updateReasons:" + updateReasons);
		return updateReasons;
	}
	// 28012021@27624 - End
	
	/**
	 * validate if Update is required
	 */
	private void UpdateReasonRequired(){
		log.info("remarkIsRequired");
		if(this.getManagedBean().getSelectedRemarks()!=null && !this.getManagedBean().getSelectedRemarks().isEmpty()){
			if(StringUtils.isBlank(this.getManagedBean().getSelectedRemarks().get(0).getRemark())){
				throw new EpException(MessageConst.ERROR_730023);
			}
		}
	}
	
	/**
	 * Handle update reason close popup
	 */
	public void closeUpdateReason() {
		log.info("closeUpdateReason");
		if (!this.getManagedBean().isConfirmReason()) {
			if (this.getManagedBean().getSelectedUpdateReasons() != null && !this.getManagedBean().getSelectedUpdateReasons().isEmpty()) {

				this.getManagedBean().getSelectedUpdateReasons().get(0).setReasonDesc(this.getManagedBean().getSelectedReasonDesc());
				this.getManagedBean().setSelectedReasonDesc("");
			}
		}
		this.getManagedBean().setSelectedUpdateReasons(null);
	}
	
	//[#25550]
	/**
	 * Pre-loading from Annual Ministry Procurement Plan
	 */
	public void preRenderAnnualPlanView() {
		
		super.renewSesionObject();
		searchProcurementAnnualPlan();
		setMinDateMaxDate();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_ANNUAL_PPLAN + "?faces-redirect=true");
	}
	
	/**
	 * reset search bean fields
	 */
	public void setMinDateMaxDate() {
		log.info("setMinDateMaxDate");

		if (this.getManagedBean().getPlanSearchBean().getInvitationDateFrom() != null) {
			// log.info("[#25550] Selected date From: " + this.getManagedBean().getPlanSearchBean().getInvitationDateFrom().toString());
			this.getManagedBean().setInvitationMinDateTo(this.getManagedBean().getPlanSearchBean().getInvitationDateFrom());
		} else if (this.getManagedBean().getPlanSearchBean().getInvitationDateFrom() == null && this.getManagedBean().getPlanSearchBean().getInvitationDateTo() == null) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_YEAR, 1); // set 1st day of year
			this.getManagedBean().setInvitationMinDateFrom(cal.getTime());
			this.getManagedBean().setInvitationMinDateTo(cal.getTime());

			// set date to last day of current year
			cal.set(Calendar.MONTH, 11); // 11 = december
			cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve
			this.getManagedBean().setInvitationMaxDate(cal.getTime());
		}
	}
	
	/**
	 * Main Filtration For Procurement Annual Plan
	 */
	public void searchProcurementAnnualPlan() {
		
		//log.debug("*** searchProcurementPlan **** ");
		log.info("[#25550] searchProcurementAnnualPlan");			
		
		if(this.getManagedBean().getSelectedMinistry() == null) {
			this.getManagedBean().setSelectedMinistry(new PmOrgProfile());
		}
		if(this.getManagedBean().getSelectedKumpulanPTJ() == null) {
			this.getManagedBean().setSelectedKumpulanPTJ(new PmOrgProfile());
		}
		if(this.getManagedBean().getSelectedPTJ() == null) {
			this.getManagedBean().setSelectedPTJ(new PmOrgProfile());
		}
		
		log.info("[#25550] invoke ppAnnualPlanDataModel...");
		ppAnnualPlanDataModel.reloadData();
	}
	
	/**
	 * reset search bean fields
	 */
	public void resetProcurementAnnualPlanSearchBean(){
		log.info("resetProcurementAnnualPlanSearchBean");		
		this.getManagedBean().setPlanSearchBean(new ProcurementPlanSearchBean());
		getManagedBean().setSelectedMinistry(new PmOrgProfile());
		getManagedBean().setSelectedKumpulanPTJ(new PmOrgProfile());
		getManagedBean().setSelectedPTJ(new PmOrgProfile());
		getManagedBean().setInvitationMinDateFrom(null);
		getManagedBean().setInvitationMinDateTo(null);
		getManagedBean().setInvitationMaxDate(null);
		setMinDateMaxDate();
		
		ppAnnualPlanDataModel = new PpAPlanDataModel();
	}
	
	//[#25550]
	/**
	 * @return the ppAnnualPlanDataModel
	 */
	public IPagingDataModel<PpProcurementItem> getPpAnnualPlanDataModel() {
		return ppAnnualPlanDataModel;
	}

	//[#25550]
	/**
	 * @param ppAnnualPlanDataModel the ppAnnualPlanDataModel to set
	 */
	public void setPpAnnualPlanDataModel(IPagingDataModel<PpProcurementItem> ppAnnualPlanDataModel) {
		this.ppAnnualPlanDataModel = ppAnnualPlanDataModel;
	}
	
	/**
	 * notice board quotation/tender list paging class
	 */
	private class PpAPlanDataModel extends PagingDataModel<PpProcurementItem> {

		private static final long serialVersionUID = 1L;

		public PpAPlanDataModel() {
			super("dtapsearchresults");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PpProcurementItem> pageChangeListener(ISearchBean<PpProcurementItem> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			
			try {
				log.info("[#25550] pageChangeListener...");						
				log.info("[#25550] call getProcItemSearchBeanData");
				//construct search bean criteria
				ISearchBean<PpProcurementItem> searchBeanData = getProcItemSearchBeanData(searchBean);
				
				ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
				//set search criteria
				reqDTO.setProcItemSearchBean(searchBeanData);					
				//set status [30005] - Approved						
				reqDTO.setStatusId(ProcurementPlanConst.PPP_APPROVED);
				//set document type - PP	
				reqDTO.setDocType(ParameterConst.DOCTYPE_PLAN_PROCUREMENT);						
				
				IProcurementPlanSO reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);
				
				//retrieve records for view
				log.info("[#25550] call retrieveAnnualPlanBySearchBean");
				ProcurementPlanResDTO resDTO = reqSO.retrieveAnnualPlanBySearchBean(reqDTO);
				
				if (resDTO.isSuccessful()) {						
					return resDTO.getPlanItemResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * Get search bean, filtered based on default value or by search criteria.
	 * @return ISearchBean<PpProcurementItem>
	 */
	private ISearchBean<PpProcurementItem> getProcItemSearchBeanData(ISearchBean<PpProcurementItem> searchBean){
		log.info("[#25550] getProcItemSearchBeanData - Start");
		
		//get search bean properties
		ProcurementPlanSearchBean planSearchBean = this.getManagedBean().getPlanSearchBean();
			
		//set plan year
		Calendar calNow = Calendar.getInstance();		
		Long currentYr = new Long(calNow.get(Calendar.YEAR));
		
		log.info("[#25550] Current Year is : " + currentYr);
		SearchFilterBean bean = new SearchFilterBean();		
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("ppPlan.planYear");
		bean.setSearchValue(currentYr);
		searchBean.getDataFilterBeans().add(bean);
			
		//set plan type		
		log.info("[#25550] Plan type is: " + ProcurementPlanConst.PLAN_TYPE_PROCUREMENT);
		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("ppPlan.planType.parameterCode");
		bean.setSearchValue(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT);	// P - Procurement
		searchBean.getDataFilterBeans().add(bean);
		
		//set procurement item type		
		log.info("[#25550] Procurement item type is: " + ProcurementPlanConst.PROCUREMENT_ITEM_TYPE_TD);
		//34233 - Start
		List<String> procurementTypeList = new ArrayList<String>();
		procurementTypeList.add(ProcurementPlanConst.PROCUREMENT_ITEM_TYPE_TD); // TD - Tender
		procurementTypeList.add(ProcurementPlanConst.SELECTIVE_TENDERING); // STR - Selective Tendering
		procurementTypeList.add(ProcurementPlanConst.REQUEST_FOR_PROPOSAL); // RP - Tender Terbuka Request For Proposal
		searchBean.getDataFilterBeans().add(new SearchFilterBean("procurementType.parameterCode",SearchOperator.OPERATOR_IN.value(), procurementTypeList));
		//34233 - End
			
		//set procurement item title or description
		if(StringUtils.isNotBlank(planSearchBean.getTitleOrDesc())){
			log.info("[#25550] Procurement title/description like: " + planSearchBean.getTitleOrDesc());
			SearchFilterBean sfbean1 = new SearchFilterBean();
			sfbean1.setCaseInsensitive(true);
			sfbean1.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			sfbean1.setSearchField("procurementTitle");
			sfbean1.setSearchValue(planSearchBean.getTitleOrDesc());
			sfbean1.setAndOr(SearchCondition.CONDITION_OR.value());
			
			SearchFilterBean sfbean2 = new SearchFilterBean();
			sfbean2.setCaseInsensitive(true);
			sfbean2.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			sfbean2.setSearchField("procurementDesc");
			sfbean2.setSearchValue(planSearchBean.getTitleOrDesc());
			sfbean2.setAndOr(SearchCondition.CONDITION_OR.value());
			
			SearchMultiFilterBean beans = new SearchMultiFilterBean(sfbean1,sfbean2);			
			searchBean.getDataFilterBeans().add(beans);				
		}
		
		//set procurement item category type
		if(planSearchBean.getCategoryType()!=null && planSearchBean.getCategoryType().getParameterId()!=null){
			//log.info("[#25550] Procurement item category type is: " + planSearchBean.getCategoryType().getParameterId());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("procurementCat.parameterId");
			bean.setSearchValue(planSearchBean.getCategoryType().getParameterId());
			searchBean.getDataFilterBeans().add(bean);
		}
		
		if(planSearchBean.getMinistry()!=null  && planSearchBean.getMinistry().getOrgProfileId()!=null) {
			//log.info("[Bug#27032] Ministry Id: " + planSearchBean.getMinistry().getOrgProfileId());
		}
		if(planSearchBean.getKptj()!=null  && planSearchBean.getKptj().getOrgProfileId()!=null) {
			//log.info("[Bug#27032] KPtj Id: " + planSearchBean.getKptj().getOrgProfileId());
		}
		if(planSearchBean.getPtj()!=null && planSearchBean.getPtj().getOrgProfileId()!=null) {
			//log.info("[Bug#27032] Ptj Id: " + planSearchBean.getPtj().getOrgProfileId());
		}
				
		//set ptj
		if(planSearchBean.getPtj()!=null && planSearchBean.getPtj().getOrgProfileId()!=null){
			//log.info("[#25550] Ptj is: " + planSearchBean.getPtj().getOrgProfileId());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.orgProfile.orgProfileId");
			bean.setSearchValue(planSearchBean.getPtj().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);
						
		//set kumpulan ptj
		} else if(planSearchBean.getKptj()!=null  && planSearchBean.getKptj().getOrgProfileId()!=null){
			//log.info("[#25550] KPtj is: " + planSearchBean.getKptj().getOrgProfileId());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.orgProfile." + OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
			bean.setSearchValue(planSearchBean.getKptj().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);
						
		//set ministry
		}else if(planSearchBean.getMinistry()!=null  && planSearchBean.getMinistry().getOrgProfileId()!=null){
			//log.info("[#25550] Ministry is: "  +planSearchBean.getMinistry().getOrgProfileId());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.orgProfile." + OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()));
			bean.setSearchValue(planSearchBean.getMinistry().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);			
		}

		//set expected invitation date
		if(planSearchBean.getInvitationDateFrom() != null && planSearchBean.getInvitationDateTo() != null) {
			/*log.info("[#25550] Expected invitation date from is: " + planSearchBean.getInvitationDateFrom());
			log.info("[#25550] Expected invitation date to is: " + planSearchBean.getInvitationDateTo());*/
			bean = new SearchFilterBean();
			bean.setSearchField("invitationDate");
			bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
			SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
					planSearchBean.getInvitationDateFrom(), planSearchBean.getInvitationDateTo());
			bean.setSearchValue(betweenVal);
			searchBean.getDataFilterBeans().add(bean);
		}
		
		//set record status flag		
		bean = new SearchFilterBean();
		bean.setSearchField("recordStatusFlg");
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
		searchBean.getDataFilterBeans().add(bean);		
	
		log.info("getProcItemSearchBeanData - End");
		return searchBean;
	}

	//[Bug#27032]
	public String constuctConditionFieldsForPTJGroup() {

		StringBuffer parentOrgProfile = new StringBuffer();
		PmOrgProfile ministryOrgProfile = this.getManagedBean().getSelectedMinistry();
		Long MinistryOrgProfileId = ministryOrgProfile != null ? ministryOrgProfile.getOrgProfileId() : null;
		if (null != MinistryOrgProfileId) {
			String minLevel = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.LVL_ORG_PROFILE_MINISTRY);
			String ptjGrpLevel = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.LVL_ORG_PROFILE_PTJGROUP);
			if (minLevel != null && ptjGrpLevel != null) {
				int ministryLevel = Integer.valueOf(minLevel);
				int ptjGroupLevel = Integer.valueOf(ptjGrpLevel);
				int orgLevel = ministryLevel - ptjGroupLevel;

				for (int i = 0; i < orgLevel; i++) {
					parentOrgProfile.append("parentOrgProfile.");
				}
				parentOrgProfile.append("orgProfileId");
				parentOrgProfile.append(CAP_CHAR);
				parentOrgProfile.append(MinistryOrgProfileId);
			}
		} else {
			parentOrgProfile.append("  ");
		}
		return parentOrgProfile.toString();
	}
	
	//[Bug#27032]
	public String constuctConditionFieldsForPTJ() {

		String parentOrgProfile = null;
		
		PmOrgProfile ministryOrgProfile = this.getManagedBean().getSelectedMinistry();
		Long MinistryOrgProfileId = ministryOrgProfile != null ? ministryOrgProfile.getOrgProfileId() : null;
		PmOrgProfile groupOrgProfile = this.getManagedBean().getSelectedKumpulanPTJ();
		Long PTJGroupOrgProfileId = groupOrgProfile != null ? groupOrgProfile.getOrgProfileId() : null;
		if (null != PTJGroupOrgProfileId) {
			parentOrgProfile = OrgProfileUtil.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
					+ CAP_CHAR + PTJGroupOrgProfileId;
		} else if (null != MinistryOrgProfileId) {
			parentOrgProfile = OrgProfileUtil.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ CAP_CHAR + MinistryOrgProfileId;
		} else {
			parentOrgProfile = "";
		}
		return parentOrgProfile;
	}
	
	//[Bug#27032]
	public void resetPtjGroupOrgProfile() {
		this.getManagedBean().setSelectedKumpulanPTJ(new PmOrgProfile());		
		this.getManagedBean().getPlanSearchBean().setKptj(null); //new
		resetPtjOrgProfile();
	}
	
	//[Bug#27032]
	public void resetPtjOrgProfile() {
		this.getManagedBean().setSelectedPTJ(new PmOrgProfile());		
		this.getManagedBean().getPlanSearchBean().setPtj(null); //new
	}
	

	//25549 - start
	public SelectItem[] getPlanPreviousYearOptions() {
		log.info("getPlanPreviousYearOptions");

		// log.info("selected year " +
		// this.getManagedBean().getPlanSearchBean().getYear());
		// 25549@05102020 - start
		Long currentYear = this.getManagedBean().getSelectedPlan().getPlanYear();

		if (currentYear != null) {
			Long preYear1 = currentYear - 1;
			Long preYear2 = preYear1 - 1;
			SelectItem[] items = new SelectItem[2];
			items[0] = new SelectItem(preYear1.longValue(), preYear1.toString());
			items[1] = new SelectItem(preYear2.longValue(), preYear2.toString());

			return items;
		}
		return null;
		// 25549@05102020 - end
	}
	
	/**
	 * Main Filtration For Procurement Plan
	 */
	public void searchCloneProcurementPlan() {
		log.info("[#25549] searchCloneProcurementPlan");
		ppCloneProcurementDataModel.reloadData();
	}

	/**
	 * Main Filtration For Procurement Plan
	 */
	public void searchCloneExpenditureBPlan() {
		log.info("[#25549] searchCloneEpBPlan");
		ppEpItemsDataModel.reloadData();
	}

			
	/**
	 * @return the ppCloneProcurementDataModel
	 */
	public IPagingDataModel<PpProcurementItem> getPpCloneProcurementDataModel() {
		return ppCloneProcurementDataModel;
	}

	// 25549 - start
	/**
	 * @param ppCloneProcurementDataModel
	 *            the ppCloneProcurementDataModel to set
	 */
	public void setPpCloneProcurementDataModel(IPagingDataModel<PpProcurementItem> ppCloneProcurementDataModel) {
		this.ppCloneProcurementDataModel = ppCloneProcurementDataModel;
	}
	// 25549 - end
			
			
	/**
	 * notice board quotation/tender list paging class
	 */
	private class PpCloneProcurementDataModel extends PagingDataModel<PpProcurementItem> {

		private static final long serialVersionUID = 1L;

		public PpCloneProcurementDataModel() {
			super("dtcloneprosearchresults");

		}

		/*
		 * (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PpProcurementItem> pageChangeListener(ISearchBean<PpProcurementItem> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			try {
				log.info("[#25549] pageChangeListener...");
				log.info("[#25549] call getProcItemSearchBeanData");
				// construct search bean criteria
				ISearchBean<PpProcurementItem> searchBeanData = getCloneProcItemSearchBeanData(searchBean);

				ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
				// set search criteria
				reqDTO.setProcItemSearchBean(searchBeanData);
				// set status [30005] - Approved
				reqDTO.setStatusId(ProcurementPlanConst.PPP_APPROVED);
				// set document type - PP
				reqDTO.setDocType(ParameterConst.DOCTYPE_PLAN_PROCUREMENT);

				IProcurementPlanSO reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);

				// retrieve records for view
				log.info("[#25549] call retrieveAnnualPlanBySearchBean");
				ProcurementPlanResDTO resDTO = reqSO.retrievePpItemClonePlanBySearchBean(reqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getPlanItemResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
			
	/**
	 * Get search bean, filtered based on default value or by search criteria.
	 * @return ISearchBean<PpProcurementItem>
	 */
	private ISearchBean<PpProcurementItem> getCloneProcItemSearchBeanData(ISearchBean<PpProcurementItem> searchBean) {
		log.info("[#25549] getCloneProcItemSearchBeanData - Start");

		log.info("[#25549] Selected Year : " + this.getManagedBean().getPlanSearchBean().getYear());
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("ppPlan.planYear");
		bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getYear());
		searchBean.getDataFilterBeans().add(bean);

		// set plan type
		log.info("[#25549] Plan type is: " + ProcurementPlanConst.PLAN_TYPE_PROCUREMENT);
		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("ppPlan.planType.parameterCode");
		bean.setSearchValue(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT); // P -
																			// Procurement
		searchBean.getDataFilterBeans().add(bean);

		// set procurement item title or description
		if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanTitle()) || StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanDesc())) {

			// 25549@azza20200819 - start
			String Title = "";
			String Desc = "";

			if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanTitle())) {
				Title = this.getManagedBean().getPlanSearchBean().getPlanTitle();
			} else {
				Title = "''";
			}
			if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanDesc())) {
				Desc = this.getManagedBean().getPlanSearchBean().getPlanDesc();
			} else {
				Desc = "''";
			}

			log.info("[#25549] Procurement title like: " + Title);
			log.info("[#25549] Procurement description like: " + Desc);
			SearchFilterBean sfbean1 = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
			sfbean1.setCaseInsensitive(true);
			sfbean1.setSearchField("procurementTitle");
			sfbean1.setSearchValue(Title);
			sfbean1.setAndOr(SearchCondition.CONDITION_OR.value());

			SearchFilterBean sfbean2 = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
			sfbean2.setCaseInsensitive(true);
			sfbean2.setSearchField("procurementDesc");
			sfbean2.setSearchValue(Desc);
			sfbean2.setAndOr(SearchCondition.CONDITION_OR.value());
			// 25549@azza20200819 - end
			SearchMultiFilterBean beans = new SearchMultiFilterBean(sfbean1, sfbean2);
			searchBean.getDataFilterBeans().add(beans);
		}

		if (this.getManagedBean().getPlanSearchBean().getCategoryType() != null && this.getManagedBean().getPlanSearchBean().getCategoryType().getParameterId() != null) {
			log.info("[#25550] Procurement item category type is: " + this.getManagedBean().getPlanSearchBean().getCategoryType().getParameterId());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("procurementCat.parameterId");
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getCategoryType().getParameterId());
			searchBean.getDataFilterBeans().add(bean);
		}

		if (this.getManagedBean().getPlanSearchBean().getProcurementType() != null && this.getManagedBean().getPlanSearchBean().getProcurementType().getParameterId() != null) {
			log.info("[#25550] Procurement item procurement type is: " + this.getManagedBean().getPlanSearchBean().getProcurementType().getParameterId());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("procurementType.parameterId");
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getProcurementType().getParameterId());
			searchBean.getDataFilterBeans().add(bean);
		}

		// set ptj
		if (this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId() != null) {
			log.info("[#25549] Ptj is: " + this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.orgProfile.orgProfileId");
			bean.setSearchValue(this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
			searchBean.getDataFilterBeans().add(bean);
		}

		// set record status flag

		log.info("[#25549] Selected Status : " + this.getManagedBean().getPlanSearchBean().getStatus());

		bean = new SearchFilterBean();
		bean.setSearchField("recordStatusFlg");
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getStatus());
		searchBean.getDataFilterBeans().add(bean);

		log.info("getProcItemSearchBeanData - End");
		return searchBean;
	}
	//EPItemBDataModel
			
	//25549 - start
	/**
	 * @return the ppEpItemsBDataModel
	 */
	public IPagingDataModel<PpExpenditureItem> getPpEpItemsDataModel() {
		return ppEpItemsDataModel;

	}

	// 25549 - start
	/**
	 * @param ppEpItemsBDataModel
	 *            the ppEpItemsBDataModel to set
	 */
	public void ppEpItemsDataModel(IPagingDataModel<PpExpenditureItem> ppEpItemsDataModel) {
		this.ppEpItemsDataModel = ppEpItemsDataModel;
	}
	// [#25549]
					
					
	/**
	 * notice board quotation/tender list paging class
	 */					
	private class PpEpItemsDataModel extends PagingDataModel<PpExpenditureItem> {

		private static final long serialVersionUID = 1L;

		public PpEpItemsDataModel() {
			super("dtclonepitemssearchresults");

		}

		/*
		 * (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PpExpenditureItem> pageChangeListener(ISearchBean<PpExpenditureItem> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			try {
				log.info("[#25549] pageChangeListener...");
				log.info("[#25549] call getPpEpItemsBDataModel");
				// construct search bean criteria

				RequestContext.getCurrentInstance().execute("hideLovMsg();");

				ProcurementPlanBackingBean bb = new ProcurementPlanBackingBean();

				ISearchBean<PpExpenditureItem> searchBeanData = getEPItemBSearchBeanData(searchBean);

				ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
				// set search criteria
				reqDTO.setExpenditureItemSearchBean(searchBeanData);
				// set status [30009] - Approved
				reqDTO.setStatusId(ProcurementPlanConst.PPE_APPROVED);
				// set document type - PE
				reqDTO.setDocType(ParameterConst.DOCTYPE_PLAN_EXPENDITURE);

				IProcurementPlanSO reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);

				// retrieve records for view
				log.info("[#25549] call retrieveEpItemClonePlanBySearchBean");
				ProcurementPlanResDTO resDTO = reqSO.retrieveEpItemClonePlanBySearchBean(reqDTO);

				// goCode

				if (resDTO.isSuccessful()) {

					if (resDTO.getPlanEPItemResultBean().getResultList() != null) {

						for (PpExpenditureItem planItem : resDTO.getPlanEPItemResultBean().getResultList()) {

							// fetch procurement method
							if (planItem.getRecordStatusFlg() != null) {
								planItem.getRecordStatusFlg();

								if (planItem.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
									bb.getManagedBean().setStatus(ResourceBundleUtils.getMessage("lbl.pm.active", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
								} else if (planItem.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED) {
									bb.getManagedBean().setStatus(ResourceBundleUtils.getMessage("lbl.pm.inactive", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
								}
							}

						}
						return resDTO.getPlanEPItemResultBean();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
					
									
	/**
	 * Get search bean, filtered based on default value or by search criteria.
	 * @return ISearchBean<PpExpenditureItem>
	 */
	private ISearchBean<PpExpenditureItem> getEPItemBSearchBeanData(ISearchBean<PpExpenditureItem> searchBean) {
		log.info("[#25549] getCloneProcItemSearchBeanData - Start");

		// 200911@akmal - Enhancement27624 - Start
		if (this.getManagedBean().getParamExpenditure() == null) {
			// 200911@akmal - Enhancement27624 - End
			log.info("[#25549] Selected Year : " + this.getManagedBean().getPlanSearchBean().getYear());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planYear");
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getYearB());
			searchBean.getDataFilterBeans().add(bean);

			// set plan type
			log.info("[#25549] Plan type is: " + ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planType.parameterCode");
			bean.setSearchValue(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			searchBean.getDataFilterBeans().add(bean);

			log.info("[#25549] Vot_Mengurus : " + this.getManagedBean().getPlanSearchBean().getParamforexpenditure());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("votFundType.parameterCode");
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getParamforexpenditure());
			searchBean.getDataFilterBeans().add(bean);

			// set ptj
			if (this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId() != null) {
				log.info("[#25549] Ptj is: " + this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("ppPlan.orgProfile.orgProfileId");
				bean.setSearchValue(this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				searchBean.getDataFilterBeans().add(bean);
			}

			// set record status flag

			log.info("[#25549] Selected Status : " + this.getManagedBean().getPlanSearchBean().getStatus());

			bean = new SearchFilterBean();
			bean.setSearchField("recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getStatusB());
			searchBean.getDataFilterBeans().add(bean);

		}

		if (ParameterConst.VOT_MENGURUS.equals(this.getManagedBean().getParamExpenditure())) {
			log.info("[#25549] Selected Year : " + this.getManagedBean().getPlanSearchBean().getYearB());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planYear");
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getYearB());
			searchBean.getDataFilterBeans().add(bean);

			// set plan type
			log.info("[#25549] Plan type is: " + ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planType.parameterCode");
			bean.setSearchValue(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			searchBean.getDataFilterBeans().add(bean);

			log.info("[#25549] Vot_Mengurus : " + this.getManagedBean().getPlanSearchBean().getParamforexpenditure());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("votFundType.parameterCode");
			bean.setSearchValue(ParameterConst.VOT_MENGURUS);
			searchBean.getDataFilterBeans().add(bean);

			if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExCode()) || StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExDesc())) {
				// 25549@azza27082020 - start
				String Code = "";
				String Desc = "";

				if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExCode())) {
					Code = this.getManagedBean().getPlanSearchBean().getPlanExCode();
				} else {
					Code = "''";
				}
				if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExDesc())) {
					Desc = this.getManagedBean().getPlanSearchBean().getPlanExDesc();
				} else {
					Desc = "''";
				}

				SearchFilterBean sfbean1 = new SearchFilterBean();
				sfbean1.setCaseInsensitive(true);
				sfbean1.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				sfbean1.setSearchField("pmGlAccount.glAccCode");
				sfbean1.setSearchValue(Code);
				sfbean1.setAndOr(SearchCondition.CONDITION_OR.value());

				SearchFilterBean sfbean2 = new SearchFilterBean();
				sfbean2.setCaseInsensitive(true);
				sfbean2.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				sfbean2.setSearchField("pmGlAccount.description");
				sfbean2.setSearchValue(Desc);
				sfbean2.setAndOr(SearchCondition.CONDITION_OR.value());
				// 25549@azza27082020 - end
				SearchMultiFilterBean beans = new SearchMultiFilterBean(sfbean1, sfbean2);
				searchBean.getDataFilterBeans().add(beans);
			}

			// set ptj
			if (this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId() != null) {
				log.info("[#25549] Ptj is: " + this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("ppPlan.orgProfile.orgProfileId");
				bean.setSearchValue(this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				searchBean.getDataFilterBeans().add(bean);
			}

			// set record status flag

			log.info("[#25549] Selected Status : " + this.getManagedBean().getPlanSearchBean().getStatusB());

			bean = new SearchFilterBean();
			bean.setSearchField("recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getStatusB());
			searchBean.getDataFilterBeans().add(bean);

		}

		if (ParameterConst.VOT_TANGGUNGAN.equals(this.getManagedBean().getParamExpenditure())) {
			// T
			log.info("[#25549] Selected Year : " + this.getManagedBean().getPlanSearchBean().getYearT());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planYear");
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getYearT());
			searchBean.getDataFilterBeans().add(bean);

			// set plan type
			log.info("[#25549] Plan type is: " + ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planType.parameterCode");
			bean.setSearchValue(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			searchBean.getDataFilterBeans().add(bean);

			log.info("[#25549] Vot_Mengurus : " + this.getManagedBean().getPlanSearchBean().getParamforexpenditure());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("votFundType.parameterCode");
			bean.setSearchValue(ParameterConst.VOT_TANGGUNGAN);
			searchBean.getDataFilterBeans().add(bean);

			if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExCodeT()) || StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExDescT())) {
				// 25549@azza27082020 - start
				String Code = "";
				String Desc = "";

				if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExCodeT())) {
					Code = this.getManagedBean().getPlanSearchBean().getPlanExCodeT();
				} else {
					Code = "''";
				}
				if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExDescT())) {
					Desc = this.getManagedBean().getPlanSearchBean().getPlanExDescT();
				} else {
					Desc = "''";
				}

				SearchFilterBean sfbean1 = new SearchFilterBean();
				sfbean1.setCaseInsensitive(true);
				sfbean1.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				sfbean1.setSearchField("pmGlAccount.glAccCode");
				sfbean1.setSearchValue(Code);
				sfbean1.setAndOr(SearchCondition.CONDITION_OR.value());

				SearchFilterBean sfbean2 = new SearchFilterBean();
				sfbean2.setCaseInsensitive(true);
				sfbean2.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				sfbean2.setSearchField("pmGlAccount.description");
				sfbean2.setSearchValue(Desc);
				sfbean2.setAndOr(SearchCondition.CONDITION_OR.value());
				// 25549@azza27082020 - end
				SearchMultiFilterBean beans = new SearchMultiFilterBean(sfbean1, sfbean2);
				searchBean.getDataFilterBeans().add(beans);
			}

			// set ptj
			if (this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId() != null) {
				log.info("[#25549] Ptj is: " + this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("ppPlan.orgProfile.orgProfileId");
				bean.setSearchValue(this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				searchBean.getDataFilterBeans().add(bean);
			}

			// set record status flag

			log.info("[#25549] Selected Status : " + this.getManagedBean().getPlanSearchBean().getStatusT());

			bean = new SearchFilterBean();
			bean.setSearchField("recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getStatusT());
			searchBean.getDataFilterBeans().add(bean);

		}

		if (ParameterConst.VOT_PEMBANGUNAN.equals(this.getManagedBean().getParamExpenditure())) {
			// T
			log.info("[#25549] Selected Year : " + this.getManagedBean().getPlanSearchBean().getYearP());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planYear");
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getYearP());
			searchBean.getDataFilterBeans().add(bean);

			// set plan type
			log.info("[#25549] Plan type is: " + ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planType.parameterCode");
			bean.setSearchValue(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			searchBean.getDataFilterBeans().add(bean);

			log.info("[#25549] Vot_Mengurus : " + this.getManagedBean().getPlanSearchBean().getParamforexpenditure());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("votFundType.parameterCode");
			bean.setSearchValue(ParameterConst.VOT_PEMBANGUNAN);
			searchBean.getDataFilterBeans().add(bean);

			if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExCodeP()) || StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExDescP())) {
				// 25549@azza27082020 - start
				String Code = "";
				String Desc = "";

				if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExCodeP())) {
					Code = this.getManagedBean().getPlanSearchBean().getPlanExCodeP();
				} else {
					Code = "''";
				}
				if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExDescP())) {
					Desc = this.getManagedBean().getPlanSearchBean().getPlanExDescP();
				} else {
					Desc = "''";
				}

				SearchFilterBean sfbean1 = new SearchFilterBean();
				sfbean1.setCaseInsensitive(true);
				sfbean1.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				sfbean1.setSearchField("pmGlAccount.glAccCode");
				sfbean1.setSearchValue(Code);
				sfbean1.setAndOr(SearchCondition.CONDITION_OR.value());

				SearchFilterBean sfbean2 = new SearchFilterBean();
				sfbean2.setCaseInsensitive(true);
				sfbean2.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				sfbean2.setSearchField("pmGlAccount.description");
				sfbean2.setSearchValue(Desc);
				sfbean2.setAndOr(SearchCondition.CONDITION_OR.value());
				// 25549@azza27082020 - end
				SearchMultiFilterBean beans = new SearchMultiFilterBean(sfbean1, sfbean2);
				searchBean.getDataFilterBeans().add(beans);
			}

			// set ptj
			if (this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId() != null) {
				log.info("[#25549] Ptj is: " + this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("ppPlan.orgProfile.orgProfileId");
				bean.setSearchValue(this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				searchBean.getDataFilterBeans().add(bean);
			}

			// set record status flag

			log.info("[#25549] Selected Status : " + this.getManagedBean().getPlanSearchBean().getStatusP());

			bean = new SearchFilterBean();
			bean.setSearchField("recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getStatusP());
			searchBean.getDataFilterBeans().add(bean);

		}

		if (ParameterConst.VOT_SETIA.equals(this.getManagedBean().getParamExpenditure())) {
			// T
			log.info("[#25549] Selected Year : " + this.getManagedBean().getPlanSearchBean().getYearS());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planYear");
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getYearS());
			searchBean.getDataFilterBeans().add(bean);

			// set plan type
			log.info("[#25549] Plan type is: " + ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planType.parameterCode");
			bean.setSearchValue(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			searchBean.getDataFilterBeans().add(bean);

			log.info("[#25549] Vot_Mengurus : " + this.getManagedBean().getPlanSearchBean().getParamforexpenditure());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("votFundType.parameterCode");
			bean.setSearchValue(ParameterConst.VOT_SETIA);
			searchBean.getDataFilterBeans().add(bean);

			if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExCodeS()) || StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExDescS())) {
				// 25549@azza27082020 - start
				String Code = "";
				String Desc = "";

				if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExCodeS())) {
					Code = this.getManagedBean().getPlanSearchBean().getPlanExCodeS();
				} else {
					Code = "''";
				}
				if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExDescS())) {
					Desc = this.getManagedBean().getPlanSearchBean().getPlanExDescS();
				} else {
					Desc = "''";
				}

				SearchFilterBean sfbean1 = new SearchFilterBean();
				sfbean1.setCaseInsensitive(true);
				sfbean1.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				sfbean1.setSearchField("pmGlAccount.glAccCode");
				sfbean1.setSearchValue(Code);
				sfbean1.setAndOr(SearchCondition.CONDITION_OR.value());

				SearchFilterBean sfbean2 = new SearchFilterBean();
				sfbean2.setCaseInsensitive(true);
				sfbean2.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				sfbean2.setSearchField("pmGlAccount.description");
				sfbean2.setSearchValue(Desc);
				sfbean2.setAndOr(SearchCondition.CONDITION_OR.value());
				// 25549@azza27082020 - end
				SearchMultiFilterBean beans = new SearchMultiFilterBean(sfbean1, sfbean2);
				searchBean.getDataFilterBeans().add(beans);
			}

			// set ptj
			if (this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId() != null) {
				log.info("[#25549] Ptj is: " + this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("ppPlan.orgProfile.orgProfileId");
				bean.setSearchValue(this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				searchBean.getDataFilterBeans().add(bean);
			}

			// set record status flag

			log.info("[#25549] Selected Status : " + this.getManagedBean().getPlanSearchBean().getStatusS());

			bean = new SearchFilterBean();
			bean.setSearchField("recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getStatusS());
			searchBean.getDataFilterBeans().add(bean);

		}

		if (ParameterConst.VOT_AMANAH.equals(this.getManagedBean().getParamExpenditure())) {
			// T
			log.info("[#25549] Selected Year : " + this.getManagedBean().getPlanSearchBean().getYearH());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planYear");
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getYearH());
			searchBean.getDataFilterBeans().add(bean);

			// set plan type
			log.info("[#25549] Plan type is: " + ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("ppPlan.planType.parameterCode");
			bean.setSearchValue(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
			searchBean.getDataFilterBeans().add(bean);

			log.info("[#25549] Vot_Mengurus : " + this.getManagedBean().getPlanSearchBean().getParamforexpenditure());
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("votFundType.parameterCode");
			bean.setSearchValue(ParameterConst.VOT_AMANAH);
			searchBean.getDataFilterBeans().add(bean);

			if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExCodeH()) || StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExDescH())) {
				// 25549@azza27082020 - start
				String Code = "";
				String Desc = "";

				if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExCodeH())) {
					Code = this.getManagedBean().getPlanSearchBean().getPlanExCodeH();
				} else {
					Code = "''";
				}
				if (StringUtils.isNotBlank(this.getManagedBean().getPlanSearchBean().getPlanExDescH())) {
					Desc = this.getManagedBean().getPlanSearchBean().getPlanExDescH();
				} else {
					Desc = "''";
				}

				SearchFilterBean sfbean1 = new SearchFilterBean();
				sfbean1.setCaseInsensitive(true);
				sfbean1.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				sfbean1.setSearchField("pmGlAccount.glAccCode");
				sfbean1.setSearchValue(Code);
				sfbean1.setAndOr(SearchCondition.CONDITION_OR.value());

				SearchFilterBean sfbean2 = new SearchFilterBean();
				sfbean2.setCaseInsensitive(true);
				sfbean2.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				sfbean2.setSearchField("pmGlAccount.description");
				sfbean2.setSearchValue(Desc);
				sfbean2.setAndOr(SearchCondition.CONDITION_OR.value());
				// 25549@azza27082020 - end
				SearchMultiFilterBean beans = new SearchMultiFilterBean(sfbean1, sfbean2);
				searchBean.getDataFilterBeans().add(beans);
			}

			// set ptj
			if (this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId() != null) {
				log.info("[#25549] Ptj is: " + this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("ppPlan.orgProfile.orgProfileId");
				bean.setSearchValue(this.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
				searchBean.getDataFilterBeans().add(bean);
			}

			// set record status flag

			log.info("[#25549] Selected Status : " + this.getManagedBean().getPlanSearchBean().getStatusH());

			bean = new SearchFilterBean();
			bean.setSearchField("recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(this.getManagedBean().getPlanSearchBean().getStatusH());
			searchBean.getDataFilterBeans().add(bean);
		}

		log.info("getProcItemSearchBeanData - End");
		return searchBean;
	}	
	
	/**
	 * Add new item procurement item list (Clone Previous Year Data)
	 */
	public void addPpItemClone() {
		log.info("addPpItemClone");

		for (int j = 0; j < this.getManagedBean().getClonePpProcurementItemLs().size(); j++) {
			PpProcurementItem PpItemClone = this.getManagedBean().getClonePpProcurementItemLs().get(j);

			PpProcurementItem ppItem = new PpProcurementItem();

			ppItem.setProcurementCat(PpItemClone.getProcurementCat());
			// #34156 10/11/2022- Start
			// #34422 09/11/2023- Start
			if (!PpItemClone.getProcurementType().getParameterCode().equals(ProcurementPlanConst.SELECTIVE_TENDER)
					&& !PpItemClone.getProcurementType().getParameterCode().equals(ProcurementPlanConst.LIMITED_TENDER)) {
				ppItem.setProcurementType(PpItemClone.getProcurementType());
			} else {
				ppItem.setProcurementType(new PmParameter());
			}
			// #34422 09/11/2023- End
			// #34156 10/11/2022- End
			ppItem.setProcurementTitle(PpItemClone.getProcurementTitle());
			ppItem.setProcurementDesc(PpItemClone.getProcurementDesc());
			ppItem.setEstimatedAmt(PpItemClone.getEstimatedAmt());
			// 25549, #32730 - start
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			int nextYear = currentYear + 1;

			if (Calendar.getInstance().get(Calendar.YEAR) == this.getManagedBean().getSelectedPlan().getPlanYear()) {
				// current year
				ppItem.setInvitationDate(new Date());
			} else if (nextYear == this.getManagedBean().getSelectedPlan().getPlanYear()) {
				System.out.println("next year ");
				// next year
				Calendar date = new GregorianCalendar((nextYear), Calendar.JANUARY, 1);

				ppItem.setInvitationDate(date.getTime());
			} else {
				System.out.println("previous year ");
				// previous year
				Long preYear = this.getManagedBean().getSelectedPlan().getPlanYear();
				Calendar datePreYear = new GregorianCalendar((preYear.intValue()), Calendar.DECEMBER, 31);

				ppItem.setInvitationDate(datePreYear.getTime());
			}// 25549 - end
				// #32730 - End
			ppItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			ppItem.setEditable(true);
			// 25549@12102020 - start
			ppItem.setReviseReject(true);
			ppItem.setOriginal(true);
			// 25549@12102020 - end
			// #34156 - Start
			ppItem.setNewLabcatMethodType(true);
			// #34156 - End

			System.out.println("size list " + j);
			this.getManagedBean().getSelectedPlan().addPpProcurementItems(ppItem);
		}
		// #25549@24022021 - calculatePpGrandTotal() remove
		resetProcurementClone();
	}
		
	/**
	 * Add new item procurement item list (Clone Previous Year Data)
	 */
	public void addPpExenditureItemClone() {
		log.info("addPpExenditureItemClone " + this.getManagedBean().getParamExpenditure());

		List<PpExpenditureItem> PpExpenditureItemList = null;

		if (ParameterConst.VOT_MENGURUS.equals(this.getManagedBean().getParamExpenditure())) {
			PpExpenditureItemList = this.getManagedBean().getClonePpExpenditureItemLs();
		} else if (ParameterConst.VOT_TANGGUNGAN.equals(this.getManagedBean().getParamExpenditure())) {
			// T
			PpExpenditureItemList = this.getManagedBean().getClonePpExpenditureTItemLs();
		} else if (ParameterConst.VOT_PEMBANGUNAN.equals(this.getManagedBean().getParamExpenditure())) {
			// P
			PpExpenditureItemList = this.getManagedBean().getClonePpExpenditurePItemLs();
		} else if (ParameterConst.VOT_SETIA.equals(this.getManagedBean().getParamExpenditure())) {
			// S
			PpExpenditureItemList = this.getManagedBean().getClonePpExpenditureSItemLs();
		} else if (ParameterConst.VOT_AMANAH.equals(this.getManagedBean().getParamExpenditure())) {
			// H
			PpExpenditureItemList = this.getManagedBean().getClonePpExpenditureHItemLs();
		}

		log.info("getlist " + PpExpenditureItemList.size());

		for (int j = 0; j < PpExpenditureItemList.size(); j++) {
			PpExpenditureItem PpExpenditureItemClone = PpExpenditureItemList.get(j);

			PpExpenditureItem ppExItem = new PpExpenditureItem();

			RequestContext.getCurrentInstance().execute("hideLovMsg();");

			ppExItem.setPmGlAccount(PpExpenditureItemClone.getPmGlAccount());
			ppExItem.setVotFundType(new PmParameter());
			ppExItem.setPpPlan(this.getManagedBean().getSelectedPlan());
			ppExItem.setJustifications(PpExpenditureItemClone.getJustifications());
			ppExItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			ppExItem.setEditable(true);
			// 25549@12102020 - start
			ppExItem.setReviseReject(true);
			ppExItem.setOriginal(true);
			// 25549@12102020 - end
			// 24975@04112020@azza - start
			ppExItem.setDisableReviseItem(true);
			// 24975@04112020@azza - end

			// B
			if (ParameterConst.VOT_MENGURUS.equals(this.getManagedBean().getParamExpenditure())) {
				ppExItem.getVotFundType().setParameterCode(ParameterConst.VOT_MENGURUS);
				this.getManagedBean().getEpItemsB().add(ppExItem);
			} else if (ParameterConst.VOT_TANGGUNGAN.equals(this.getManagedBean().getParamExpenditure())) {
				// T
				ppExItem.getVotFundType().setParameterCode(ParameterConst.VOT_TANGGUNGAN);
				this.getManagedBean().getEpItemsT().add(ppExItem);
			} else if (ParameterConst.VOT_PEMBANGUNAN.equals(this.getManagedBean().getParamExpenditure())) {
				// P
				ppExItem.getVotFundType().setParameterCode(ParameterConst.VOT_PEMBANGUNAN);
				this.getManagedBean().getEpItemsP().add(ppExItem);
			} else if (ParameterConst.VOT_SETIA.equals(this.getManagedBean().getParamExpenditure())) {
				// S
				ppExItem.getVotFundType().setParameterCode(ParameterConst.VOT_SETIA);
				this.getManagedBean().getEpItemsS().add(ppExItem);
			} else if (ParameterConst.VOT_AMANAH.equals(this.getManagedBean().getParamExpenditure())) {
				// H
				ppExItem.getVotFundType().setParameterCode(ParameterConst.VOT_AMANAH);
				this.getManagedBean().getEpItemsH().add(ppExItem);
			}
		}
		// #25549@24022021 - calculateEpGrandTotal() remove
		resetExpenditureClone();
	}
	
	public void parameterExpenditure(String paramCode) {

		// 25549@azza - start
		calculateEpGrandTotal(paramCode);
		// 25549@azza - end

		log.info("parameterExpenditure " + paramCode);
		// B
		if (ParameterConst.VOT_MENGURUS.equals(paramCode)) {
			this.getManagedBean().setParamExpenditure(ParameterConst.VOT_MENGURUS);
		} else if (ParameterConst.VOT_TANGGUNGAN.equals(paramCode)) {
			// T
			this.getManagedBean().setParamExpenditure(ParameterConst.VOT_TANGGUNGAN);
		} else if (ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)) {
			// P
			this.getManagedBean().setParamExpenditure(ParameterConst.VOT_PEMBANGUNAN);
		} else if (ParameterConst.VOT_SETIA.equals(paramCode)) {
			// S
			this.getManagedBean().setParamExpenditure(ParameterConst.VOT_SETIA);
		} else if (ParameterConst.VOT_AMANAH.equals(paramCode)) {
			// H
			this.getManagedBean().setParamExpenditure(ParameterConst.VOT_AMANAH);
		}

		rowSelectExpenditure();

	}		
		
	public void resetProcurementClone() {

		log.info("resetProcurementClone");
		// #25549@24022021 - start
		calculatePpGrandTotal();
		// #25549@24022021 - end
		this.getManagedBean().setPlanSearchBean(new ProcurementPlanSearchBean());
		ppCloneProcurementDataModel.resetData();
	}
		
		
	public void resetExpenditureClone() {

		log.info("resetExpenditureClone");

		// #25549@24022021 - start
		calculateEpGrandTotal(ParameterConst.VOT_MENGURUS);
		calculateEpGrandTotal(ParameterConst.VOT_TANGGUNGAN);
		calculateEpGrandTotal(ParameterConst.VOT_PEMBANGUNAN);
		calculateEpGrandTotal(ParameterConst.VOT_SETIA);
		calculateEpGrandTotal(ParameterConst.VOT_AMANAH);
		// #25549@24022021 - end
		this.getManagedBean().setPlanSearchBean(new ProcurementPlanSearchBean());
		ppEpItemsDataModel.resetData();

	}			

	public boolean goCode(String pmGlAccount) {

		boolean flag = false;
		String goCode = "";

		if (pmGlAccount != null && pmGlAccount.trim().length() > 0) {
			goCode = pmGlAccount.substring(4, 8);
		}

		log.info("goCode " + goCode);

		if (Integer.parseInt(goCode) == 0000) {
			flag = true;
		}

		log.info("flag " + flag);

		return flag;
	}
		
	public void rowSelectProcurement() {
		// #25549@24022021 - start
		calculatePpGrandTotal();
		// #25549@24022021 - end
		System.out.println(this.getManagedBean().getClonePpProcurementItemLs().size());
		if (this.getManagedBean().getClonePpProcurementItemLs().size() > 0) {
			this.getManagedBean().setDisableSelectBtn(false);
			System.out.println("rowSelect is Enable Button !!:" + this.getManagedBean().isDisableSelectBtn());
		} else if (this.getManagedBean().getClonePpProcurementItemLs().size() == 0 || this.getManagedBean().getClonePpProcurementItemLs().size() < 0) {
			this.getManagedBean().setDisableSelectBtn(true);
			System.out.println("unSelectRow is Disable Button !!:" + this.getManagedBean().isDisableSelectBtn());
		}
	}		
		
	public void rowSelectExpenditure() {

		List<PpExpenditureItem> PpExpenditureItemList = null;

		if (ParameterConst.VOT_MENGURUS.equals(this.getManagedBean().getParamExpenditure())) {
			log.info("B");
			PpExpenditureItemList = this.getManagedBean().getClonePpExpenditureItemLs();
		} else if (ParameterConst.VOT_TANGGUNGAN.equals(this.getManagedBean().getParamExpenditure())) {
			log.info("T");
			PpExpenditureItemList = this.getManagedBean().getClonePpExpenditureTItemLs();
		} else if (ParameterConst.VOT_PEMBANGUNAN.equals(this.getManagedBean().getParamExpenditure())) {
			log.info("P");
			PpExpenditureItemList = this.getManagedBean().getClonePpExpenditurePItemLs();
		} else if (ParameterConst.VOT_SETIA.equals(this.getManagedBean().getParamExpenditure())) {
			log.info("S");
			PpExpenditureItemList = this.getManagedBean().getClonePpExpenditureSItemLs();
		} else if (ParameterConst.VOT_AMANAH.equals(this.getManagedBean().getParamExpenditure())) {
			log.info("H");
			PpExpenditureItemList = this.getManagedBean().getClonePpExpenditureHItemLs();
		}

		System.out.println(PpExpenditureItemList.size());
		if (PpExpenditureItemList.size() > 0) {
			this.getManagedBean().setDisableSelectExpBtn(false);
			System.out.println("rowSelect Expenditure is Enable Button !!:" + this.getManagedBean().isDisableSelectExpBtn());
		} else if (PpExpenditureItemList.size() == 0 || PpExpenditureItemList.size() < 0) {
			this.getManagedBean().setDisableSelectExpBtn(true);
			System.out.println("unSelectRow Expenditure is Disable Button !!:" + this.getManagedBean().isDisableSelectExpBtn());
		}
	}
	//25549 - end

	//24795 - start		
	public void addUpdateEpAllInfo(String paramCode, PpPlan ppPlan, Integer index) {
		log.info("addUpdateExpenditureInfo");
		log.info("Original index= " + index);

		List<PpExpenditureItem> PpExpenditureItemList = null;

		if (ParameterConst.VOT_MENGURUS.equals(paramCode)) {
			PpExpenditureItemList = this.getManagedBean().getEpItemsB();
		} else if (ParameterConst.VOT_TANGGUNGAN.equals(paramCode)) {
			// T
			PpExpenditureItemList = this.getManagedBean().getEpItemsT();
		} else if (ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)) {
			// P
			PpExpenditureItemList = this.getManagedBean().getEpItemsP();
		} else if (ParameterConst.VOT_SETIA.equals(paramCode)) {
			// S
			PpExpenditureItemList = this.getManagedBean().getEpItemsS();
		} else if (ParameterConst.VOT_AMANAH.equals(paramCode)) {
			// H
			PpExpenditureItemList = this.getManagedBean().getEpItemsH();
		}

		log.info("getlist " + PpExpenditureItemList.size());

		PpExpenditureItem newPpExItem = new PpExpenditureItem();

		if (this.getManagedBean().getSelectedPlan().getPlanYear() == null) {
			throw new EpException(MessageConst.ERROR_730028);
		}

		if (ppPlan.getPpExpenditureItems() != null && !ppPlan.getPpExpenditureItems().isEmpty()) {

			PpExpenditureItem clonedPpExpItem = PpExpenditureItemList.get(index);

			newPpExItem.setCloned(true);
			newPpExItem.setCreatedBy(getUserIdSessionFromLoginId()); // #31849 add log key
			newPpExItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			newPpExItem.setChangedDate(new Timestamp(new Date().getTime()));
			newPpExItem.setPmGlAccount(clonedPpExpItem.getPmGlAccount());
			newPpExItem.setVotFundType(new PmParameter());
			newPpExItem.setBudgetAmt(clonedPpExpItem.getBudgetAmt());
			newPpExItem.setPlanAmt(clonedPpExpItem.getPlanAmt());
			newPpExItem.setNonEpAmt(clonedPpExpItem.getNonEpAmt());
			newPpExItem.setPpPlan(this.getManagedBean().getSelectedPlan());
			newPpExItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			newPpExItem.setBrandNew(true);
			newPpExItem.setEditable(true);
			newPpExItem.setCloneEnabled(true);
			newPpExItem.setDisableForPindaan(true);
			newPpExItem.setFromPpItem(clonedPpExpItem.getExpenditureItemId());
			newPpExItem.setReasonForUpdateEnabled(false);
			newPpExItem.setStatusEnabled(true);
			newPpExItem.setJustificationEnabled(false);
			newPpExItem.setDisableEditAccount(true);
			// 25549@05102020 - start
			List<PpRemark> newJustifications = new ArrayList<PpRemark>();
			for (PpRemark oldJustifications : clonedPpExpItem.getJustifications()) {

				PpRemark copyJustifications = new PpRemark();
				copyJustifications.setDocId(oldJustifications.getDocId());
				copyJustifications.setDocType(oldJustifications.getDocType());
				copyJustifications.setRoleCode(oldJustifications.getRoleCode());
				copyJustifications.setRemarkType(oldJustifications.getRemarkType());
				copyJustifications.setRemark(oldJustifications.getRemark());
				//#35502, Fix as-is bug: #38802 Start
		    	copyJustifications.setCreatedBy(getUserIdSessionFromLoginId());
		    	copyJustifications.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
		    	copyJustifications.setChangedDate(new Timestamp(System.currentTimeMillis()));
		    	//#35502, Fix as-is bug: #38802 End

				newJustifications.add(copyJustifications);
			}
			newPpExItem.setJustifications(newJustifications);
			newPpExItem.setOriginal(false);
			// 25549@05102020 - end
			// 25549@09102020 - start
			newPpExItem.setReviseReject(true);
			newPpExItem.setDisableReviseItem(true);
			// 25549@09102020 - end

			log.info("Plan Id " + newPpExItem.getPpPlan().getPlanId());
			log.info("FromPpItem " + newPpExItem.getFromPpItem());

			// B
			if (ParameterConst.VOT_MENGURUS.equals(paramCode)) {
				newPpExItem.getVotFundType().setParameterCode(ParameterConst.VOT_MENGURUS);
				index += 1;
				this.getManagedBean().getEpItemsB().add(index, newPpExItem);
			} else if (ParameterConst.VOT_TANGGUNGAN.equals(paramCode)) {
				// T
				newPpExItem.getVotFundType().setParameterCode(ParameterConst.VOT_TANGGUNGAN);
				index += 1;
				this.getManagedBean().getEpItemsT().add(index, newPpExItem);
			} else if (ParameterConst.VOT_PEMBANGUNAN.equals(paramCode)) {
				// P
				newPpExItem.getVotFundType().setParameterCode(ParameterConst.VOT_PEMBANGUNAN);
				index += 1;
				this.getManagedBean().getEpItemsP().add(index, newPpExItem);
			} else if (ParameterConst.VOT_SETIA.equals(paramCode)) {
				// S
				newPpExItem.getVotFundType().setParameterCode(ParameterConst.VOT_SETIA);
				index += 1;
				this.getManagedBean().getEpItemsS().add(index, newPpExItem);
			} else if (ParameterConst.VOT_AMANAH.equals(paramCode)) {
				// H
				newPpExItem.getVotFundType().setParameterCode(ParameterConst.VOT_AMANAH);
				index += 1;
				this.getManagedBean().getEpItemsH().add(index, newPpExItem);
			}

			disableEnablePpExpItemFields(PpExpenditureItemList, index);

		} else {
			throw new EpException(MessageConst.ERROR_730015);
		}

	}
		 
	public void disableEnablePpExpItemFields(List<PpExpenditureItem> items, Integer idx) {

		log.info("...Trace log for Bug #31887 disableEnablePpExpItemFields....");

		PpExpenditureItem cloned = items.get(idx);

		if ((cloned.isCloned() && cloned.isBrandNew())) {

			log.info("...Trace log for Bug #31887 START 001....");

			cloned.setCloneEnabled(false);
			cloned.setReasonForUpdateEnabled(false);
			cloned.setStatusEnabled(false);
			cloned.setJustificationEnabled(false);
			cloned.setDisableEditAccount(true);

			log.info("...Trace log for Bug #31887 END 001....");

		}

		idx -= 1;
		PpExpenditureItem original = items.get(idx);

		// 24975@26102020 - start
		log.info("ExpCheckOriginalItem " + original.getExpenditureItemId() + original.isCloned() + original.isBrandNew());

		if (!original.isCloned() && !original.isBrandNew()) {
			// 24975@26102020 - end

			log.info("...Trace log for Bug #31887 START 002...." + original.getRecordStatusFlg());

			original.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
			original.setCloneEnabled(true);
			original.setReasonForUpdateEnabled(true);
			original.setStatusEnabled(true);
			original.setDisableEditAccount(true);
			original.setJustificationEnabled(true);

			log.info("...Trace log for Bug #31887 END 002...." + original.getRecordStatusFlg());

			// 24975@04112020@azza - start
		} else if (original.isCloned() && !original.isBrandNew()) {

			log.info("...Trace log for Bug #31887 START 003...." + original.getRecordStatusFlg());

			original.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
			original.setCloneEnabled(true);
			original.setReasonForUpdateEnabled(true);
			original.setStatusEnabled(true);
			original.setDisableEditAccount(true);
			original.setJustificationEnabled(true);

			log.info("...Trace log for Bug #31887 END 003...." + original.getRecordStatusFlg());

		}
	}
	// 24975@04112020@azza - end
		   
	// 27624@28012021 - Start
	public void ChekDuplicatePpItemClone(List<PpExpenditureItem> epItems) {

		CheckDuplicatePpExpenditureItems(epItems);

		List<PpExpenditureItem> expenditureItems = null;
		Map<PpExpenditureItem, PpExpenditureItem> clonedEpItems = new HashMap<PpExpenditureItem, PpExpenditureItem>();

		if (epItems != null && !epItems.isEmpty()) {

			expenditureItems = epItems;

			log.info("Size of expenditureItems= " + epItems.size());

			List<Long> numberOfClones = new ArrayList<Long>();
			List<PpExpenditureItem> ppEpItemIndicator = new ArrayList<PpExpenditureItem>();

			if (expenditureItems.size() > 1) {
				int indicatorIndex = 0;
				for (int i = 0; i < expenditureItems.size(); i++) {

					log.info("expenditureId clone " + expenditureItems.get(i).isCloned());

					if (expenditureItems.get(i).isCloned() == true) {

						if (this.getManagedBean().isPlanDetailSearch() || this.getManagedBean().isPlanDetailReset()) {
							expenditureItems.get(i - 1).setSearchReset(true);
						}

						log.info("Cloned procurementItemId= " + expenditureItems.get(i).getExpenditureItemId() + ", brandNew= " + expenditureItems.get(i).isBrandNew());

						log.info("Original expenditureItems= " + expenditureItems.get(i - 1).getExpenditureItemId() + ", brandNew= " + expenditureItems.get(i - 1).isBrandNew());
						numberOfClones.add(expenditureItems.get(i).getExpenditureItemId());
						clonedEpItems.put(expenditureItems.get(i), expenditureItems.get(i - 1));

						if (!this.getManagedBean().isPlanDetailSearch() && !this.getManagedBean().isPlanDetailReset()) {
							if (expenditureItems.get(i - 1).getPpRemarks() != null && expenditureItems.get(i - 1).getPpRemarks().isEmpty() || expenditureItems.get(i - 1).getPpRemarks().get(0) == null
									|| StringUtils.isBlank(expenditureItems.get(i - 1).getPpRemarks().get(0).getRemark())) {
								PpExpenditureItem setIndicator = expenditureItems.get(i - 1);
								setIndicator.setIndicator(true);
								ppEpItemIndicator.add(setIndicator);
								indicatorIndex++;
							}
						}
					} else if (expenditureItems.get(i).isSearchReset() && !this.getManagedBean().isPlanDetailSearch() && !this.getManagedBean().isPlanDetailReset()) {
						if (expenditureItems.get(i).getPpRemarks() != null && expenditureItems.get(i).getPpRemarks().isEmpty() || expenditureItems.get(i).getPpRemarks().get(0) == null
								|| StringUtils.isBlank(expenditureItems.get(i).getPpRemarks().get(0).getRemark()) || expenditureItems.get(i).getPpRemarks().get(0).getRemark().equals("\u200B")) {
							PpExpenditureItem setIndicator = expenditureItems.get(i);
							setIndicator.setIndicator(true);
							ppEpItemIndicator.add(setIndicator);
							indicatorIndex++;
						}
					}
				}
				if (indicatorIndex > 0) {
					throw new EpException(MessageConst.ERROR_730035);
				}

				CheckDuplicatePpExpenditureItems(epItems);
			}

			PpExpenditureItem clonedEpItem = new PpExpenditureItem();

			// Should not allow multiple updates
			log.info("Saiz numberOfClones= " + numberOfClones.size());
			for (Map.Entry<PpExpenditureItem, PpExpenditureItem> clonedExpItem : clonedEpItems.entrySet()) {

				clonedEpItem = clonedExpItem.getKey();

				if (!expenditureItemValueChanges(clonedEpItem, clonedExpItem.getValue())) {
					throw new EpException(MessageConst.ERROR_730033);
				}
			}
		}
	}
	// 28012021@27624 - end
     
	private boolean expenditureItemValueChanges(PpExpenditureItem clonedItem, PpExpenditureItem originalItem) {

		boolean hasChanges = false;

		int compareBudgetAmt = clonedItem.getBudgetAmt().compareTo(originalItem.getBudgetAmt());
		int comparePlanAmt = clonedItem.getPlanAmt().compareTo(originalItem.getPlanAmt());
		int compareNonEpAmt = clonedItem.getNonEpAmt().compareTo(originalItem.getNonEpAmt());

		log.info("************************Compare PpExpItems Values**************************");
		log.info("PmGlAccount => " + clonedItem.getPmGlAccount() + " : " + originalItem.getPmGlAccount());
		log.info("BudgetAmt => " + clonedItem.getBudgetAmt() + " : " + originalItem.getBudgetAmt());
		log.info("PlanAmt => " + clonedItem.getPlanAmt() + " : " + originalItem.getPlanAmt());
		log.info("NonEpAmt => " + clonedItem.getNonEpAmt() + " : " + originalItem.getNonEpAmt());
		log.info("CreatedBy => " + clonedItem.getCreatedBy() + " : " + originalItem.getCreatedBy());
		log.info("CreatedDate => " + clonedItem.getCreatedDate() + " : " + originalItem.getCreatedDate());
		log.info("ChangedBy => " + clonedItem.getChangedBy() + " : " + originalItem.getChangedBy());
		log.info("ChangedDate => " + clonedItem.getChangedDate() + " : " + originalItem.getChangedDate());
		log.info("Editable => " + clonedItem.isEditable() + " : " + originalItem.isEditable());
		log.info("BrandNew => " + clonedItem.isBrandNew() + " : " + originalItem.isBrandNew());

		log.info("compareBudgetAmt= " + compareBudgetAmt + ", comparePlanAmt= " + comparePlanAmt + ", compareNonEpAmt= " + compareNonEpAmt);
		log.info("*****************************************************************");

		int[] fields = new int[] { compareBudgetAmt, comparePlanAmt, compareNonEpAmt };

		for (int i = 0; i < fields.length; i++) {
			log.info("field= " + fields[i]);
			if (fields[i] != 0) {
				hasChanges = true;
			}
		}
		log.info("hasChanges: " + hasChanges);
		return hasChanges;

	}
	
	private void resetPpExpenditureItems(List<PpExpenditureItem> ppItems) {
		log.info("Resetting PpItems...");

		List<Long> fromPpItemList = new ArrayList<Long>();
		List<Long> expenditureIItemIdList = new ArrayList<Long>();

		for (PpExpenditureItem ppItem : ppItems) {
			fromPpItemList.add(ppItem.getFromPpItem());
			expenditureIItemIdList.add(ppItem.getExpenditureItemId());
		}

		List<Long> commonExpenditureItemIdList = new ArrayList<Long>(fromPpItemList);
		commonExpenditureItemIdList.retainAll(expenditureIItemIdList);

		if (commonExpenditureItemIdList != null && !commonExpenditureItemIdList.isEmpty()) {
			for (Long commonExpenditureItemId : commonExpenditureItemIdList) {

				for (PpExpenditureItem ppItem : ppItems) {
					/*
					 * Always disable Pindaan for cloned ppItem Always disable Reason For Update for original ppItem
					 */
					if (!ppItem.isOriginal()) {
						if (ppItem.getFromPpItem().equals(commonExpenditureItemId)) {
							if (ppItem.getRecordStatusFlg() == 9 && !ppItem.isDisableRow()) { // 28012021@27624
								ppItem.setCloneEnabled(true);
								ppItem.setDisableForPindaan(false);
								ppItem.setReasonForUpdateEnabled(true);
								ppItem.setDisableEditAccount(true);
								ppItem.setJustificationEnabled(true);
								ppItem.setReviseReject(false);
								// 24975@06112020@azza - Start
								ppItem.setChangePencilToNote(true);
								// 24975@06112020@azza - End

							} else if (ppItem.getRecordStatusFlg() == 1) {

								log.info("getFromRes1 " + ppItem.getFromPpItem() + "ppIdRes " + commonExpenditureItemId + "status " + ppItem.getRecordStatusFlg());

								ppItem.setDisableForPindaan(false);
								ppItem.setReasonForUpdateEnabled(true);
								ppItem.setJustificationEnabled(true);
								ppItem.setReviseReject(true);
							} else {
								log.info("getFromRes2 " + ppItem.getFromPpItem() + "ppIdRes " + commonExpenditureItemId + "status " + ppItem.getRecordStatusFlg());

								ppItem.setCloneEnabled(false);
								ppItem.setDisableForPindaan(true);
								ppItem.setReasonForUpdateEnabled(false);
								ppItem.setDisableEditAccount(true);
								ppItem.setJustificationEnabled(false);
								ppItem.setBrandNew(true);
								ppItem.setReviseReject(true);
							}
						}
					} else {
						/*
						 * Always disable Pindaan for original ppItem Always enable Reason For Update for original ppItem
						 */
						if (ppItem.getExpenditureItemId().equals(commonExpenditureItemId)) {
							log.info("original item 1 " + ppItem.getExpenditureItemId());
							ppItem.setCloneEnabled(true);
							ppItem.setDisableForPindaan(false);
							ppItem.setReasonForUpdateEnabled(true);
							ppItem.setDisableEditAccount(true);
							ppItem.setJustificationEnabled(true);
							ppItem.setReviseReject(true);
							if (ppItem.isSearchReset()) { // 27624@28012021@azza - Start
								ppItem.setChangePencilToNote(false);
							} else {
								ppItem.setChangePencilToNote(true);
							}// 27624@28012021@azza - End
						} else {
							log.info("original item 2 " + ppItem.getExpenditureItemId());
							// The rest should retain the existing field logics
							ppItem.setDisableForPindaan(false);
							ppItem.setReasonForUpdateEnabled(true);
							ppItem.setJustificationEnabled(true);
							ppItem.setReviseReject(true);
						}
					}
				}
			}
		}
	} 
	//25479 - end
	
	//25479 - start
	private void CheckDuplicatePpExpenditureItems(List<PpExpenditureItem> ppItems) {
		log.info("Resetting PpItems...");
		
				
		List<Long> fromPpItemList = new ArrayList<Long>();
		List<Long> expenditureIItemIdList = new ArrayList<Long>();
		
		for (PpExpenditureItem ppItem : ppItems) {
			fromPpItemList.add(ppItem.getFromPpItem());
			expenditureIItemIdList.add(ppItem.getExpenditureItemId());
		}
	    
		List<Long> commonExpenditureItemIdList = new ArrayList<Long>(fromPpItemList);
		commonExpenditureItemIdList.retainAll(expenditureIItemIdList);
		
		if (commonExpenditureItemIdList != null && !commonExpenditureItemIdList.isEmpty()) {
			for (Long commonExpenditureItemId : commonExpenditureItemIdList) {
				for (PpExpenditureItem ppItem : ppItems) {
					
					log.info("expenditure id null " + ppItem.getExpenditureItemId());
					
					//25549 - start
				if (ppItem.getExpenditureItemId() != null){
					if (!ppItem.isOriginal()) {
						if (ppItem.getFromPpItem().equals(commonExpenditureItemId)) {
							
							log.info("getFrom " + ppItem.getFromPpItem() + "ppId " + commonExpenditureItemId);
							
							ppItem.setCloneEnabled(true);
							ppItem.setDisableForPindaan(false);
							ppItem.setReasonForUpdateEnabled(false);
							ppItem.setDisableEditAccount(true);
							ppItem.setJustificationEnabled(false);
							ppItem.setBrandNew(false);
						}   
					}
				  }
			   }
		     }
		   }
        }
	//25479 - end
	
	//25549@azza20200827 - Start
	public void onSelectCategoryClone(Long id){
		log.info("The category id " + id);
		List<PmProcurementCatType> pmProcCats= PpCommonUtil.retrieveProcurementCatTypeList(getFacesScopes().getUserLocale().getLanguage());
		for(PmProcurementCatType pmProcCat : pmProcCats ){
			if(pmProcCat.getProcurementCat().getParameterId().equals(id)){
				break;
			}
		}
	}
	//25549@azza20200827 - End

	//200911@akmal - Enhancement27624 - Start
	private void setScreenVariables(){
		// check which screen to initialize for - Audit plan search, Plan Maintenance
		List<RoleCode> roles = Arrays.asList(RoleCode.BPK_AUDITOR, RoleCode.BPK_OFFICER, RoleCode.AUDITOR_GENERAL,
					RoleCode.AUDITOR_INTERNAL, RoleCode.PLAN_OFFICER, RoleCode.PLAN_VERFICATION_OFFICER, RoleCode.PLAN_APPROVER);
		
		setMandatory(PpCommonUtil.isUserHasRole(roles));
		
		if(isMandatory()){
			// check which screen
			//if pplan search audit
			
			//else if pplan maintenance
			
		}
	}
	
	public static boolean isMandatory() {
		return isMandatory;
	}

	public static void setMandatory(boolean isMandatory) {
		ProcurementPlanBackingBean.isMandatory = isMandatory;
	}

	public static String getPageType() {
		return pageType;
	}

	public static void setPageType(String pageType) {
		ProcurementPlanBackingBean.pageType = pageType;
	}

	//200911@akmal - Enhancement27624 - End
	// #25549@24022021 - start 
	/* 
	public void disableDuplicateAccountId(String AccountId) {
			
	    List<PpExpenditureItem> PpExpenditureItemList = null;
			
		   if (ParameterConst.VOT_MENGURUS.equals(this.getManagedBean().getParamExpenditure())) {
				  log.info("B");			
				 PpExpenditureItemList = this.getManagedBean().getEpItemsB();
			   } else if (ParameterConst.VOT_TANGGUNGAN.equals(this.getManagedBean().getParamExpenditure())) {
				  log.info("T");	
				 PpExpenditureItemList = this.getManagedBean().getEpItemsT();
			   } else if (ParameterConst.VOT_PEMBANGUNAN.equals(this.getManagedBean().getParamExpenditure())) {
				  log.info("P");	
				  PpExpenditureItemList = this.getManagedBean().getEpItemsP();
			   } else if (ParameterConst.VOT_SETIA.equals(this.getManagedBean().getParamExpenditure())) {
				  log.info("S");	
				  PpExpenditureItemList = this.getManagedBean().getEpItemsS();
			  } else if (ParameterConst.VOT_AMANAH.equals(this.getManagedBean().getParamExpenditure())) {
				  log.info("H");	
				  PpExpenditureItemList = this.getManagedBean().getEpItemsH();
		      }
			
			for (PpExpenditureItem ppExpItems : PpExpenditureItemList){
				 if (ppExpItems.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){	 
				     if (ppExpItems.getPmGlAccount().getGlAccCode().equals(AccountId)){
				         flag = true;
				      }
			      }
			  }
	    }*/
	// #25549@24022021 - end
	 
	public void selectedSEptatus(String Param, Integer Status, Integer index) {

		List<PpExpenditureItem> PpExpenditureItemList = null;

		if (ParameterConst.VOT_MENGURUS.equals(Param)) {
			log.info("B");
			PpExpenditureItemList = this.getManagedBean().getEpItemsB();
		} else if (ParameterConst.VOT_TANGGUNGAN.equals(Param)) {
			log.info("T");
			PpExpenditureItemList = this.getManagedBean().getEpItemsT();
		} else if (ParameterConst.VOT_PEMBANGUNAN.equals(Param)) {
			log.info("P");
			PpExpenditureItemList = this.getManagedBean().getEpItemsP();
		} else if (ParameterConst.VOT_SETIA.equals(Param)) {
			log.info("S");
			PpExpenditureItemList = this.getManagedBean().getEpItemsS();
		} else if (ParameterConst.VOT_AMANAH.equals(Param)) {
			log.info("H");
			PpExpenditureItemList = this.getManagedBean().getEpItemsH();
		}

		PpExpenditureItem PpExpItem = PpExpenditureItemList.get(index);
		if (Status == 9) {
			log.info("setActionCloneEnabled " + "true");
			PpExpItem.setDisableRow(true);
			PpExpItem.setIsDisableRow(true);// 28012021@27624 - start
		} else {
			log.info("setActionCloneEnabled " + "false");
			PpExpItem.setDisableRow(false);
			PpExpItem.setIsDisableRow(false); // 28012021@27624 - End
		}
	}

	public void selectedPpStatus(Integer Status, Integer index) {

		PpProcurementItem PpItem = this.getManagedBean().getSelectedPlan().getPpProcurementItems().get(index);
		if (Status == 9) {
			log.info("setActionCloneEnabled " + "true");
			PpItem.setDisableRow(true);
			PpItem.setIsDisableRow(true);// 28012021@27624 - start
		} else {
			log.info("setActionCloneEnabled " + "false");
			PpItem.setDisableRow(false);
			PpItem.setIsDisableRow(false);// 28012021@27624 - end
		}
	}

	class sortRemark implements Comparator<PpRemark> {
		public int compare(PpRemark o1, PpRemark o2) {
			return (o1.getCreatedDate()).compareTo(o2.getCreatedDate());
		}
	}
	// 24975@04112020@azza - End
	     
	// 28760 - start
	/**
	 * generate cancel button rendered
	 */
	private void generateCancelButton(PpPlan ppPlan) {
		log.info("generateCancelButton - Start");
		this.getManagedBean().setFlagCancelButton(false);
		Long status = this.getManagedBean().getSelectedPlan().getStatus().getStatusId();

		if (ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppPlan.getPlanType().getParameterCode())) {
			if ((ProcurementPlanConst.PPE_PENDING_REVISION.equals(status)) || (ProcurementPlanConst.PPE_PENDING_SUBMISSION.equals(status))) {
				this.getManagedBean().setFlagCancelButton(true);
			} else if ((ProcurementPlanConst.PPE_PENDING_APPROVAL.equals(status)) || (ProcurementPlanConst.PPE_PENDING_REVISION_APPROVAL.equals(status))) {
				this.getManagedBean().setFlagCancelButton(true);
			}
		} else {
			if ((ProcurementPlanConst.PPP_PENDING_REVISION.equals(status)) || (ProcurementPlanConst.PPP_PENDING_SUBMISSION.equals(status))) {
				this.getManagedBean().setFlagCancelButton(true);
			} else if ((ProcurementPlanConst.PPP_PENDING_VERIFICATION.equals(status)) || (ProcurementPlanConst.PPP_PENDING_REVISION_VERIFICATION.equals(status))) {
				this.getManagedBean().setFlagCancelButton(true);
			} else if ((ProcurementPlanConst.PPP_PENDING_APPROVAL.equals(status)) || (ProcurementPlanConst.PPP_PENDING_REVISION_APPROVAL.equals(status))) {
				this.getManagedBean().setFlagCancelButton(true);
			}
		}
		log.info("generateCancelButton - End");
	}
	// 28760 - end
	
	// 24975@19112020 - start
	public void checkUpdateForReasonIndicator(PpPlan ppPlan) {
		log.info("start checkUpdateForReasonIndicator");

		if (ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppPlan.getPlanType().getParameterCode())) {

			List<PpProcurementItem> procurementItems = null;
			List<PpProcurementItem> ppItemIndicator = new ArrayList<PpProcurementItem>();

			if (ppPlan.getPpProcurementItems() != null && !ppPlan.getPpProcurementItems().isEmpty()) {

				procurementItems = ppPlan.getPpProcurementItems();

				if (procurementItems.size() > 1) {
					int indicatorIndex = 0;
					for (int i = 0; i < procurementItems.size(); i++) {
						if (procurementItems.get(i).isClonedItem()) {
							if (procurementItems.get(i - 1).getPpUpdateReasons() != null && procurementItems.get(i - 1).getPpUpdateReasons().isEmpty()
									|| procurementItems.get(i - 1).getPpUpdateReasons().get(0) == null || StringUtils.isBlank(procurementItems.get(i - 1).getPpUpdateReasons().get(0).getReasonDesc())
									|| procurementItems.get(i - 1).getPpUpdateReasons().get(0).getReasonDesc().equals("\u200B")) {
								PpProcurementItem setIndicator = procurementItems.get(i - 1);
								setIndicator.setIndicator(true);
								ppItemIndicator.add(setIndicator);
								indicatorIndex++;
							}
						} else if (procurementItems.get(i).isSearchReset()) { // 28012021@27624 - start
							if (procurementItems.get(i).getPpUpdateReasons() != null && procurementItems.get(i).getPpUpdateReasons().isEmpty()
									|| procurementItems.get(i).getPpUpdateReasons().get(0) == null || StringUtils.isBlank(procurementItems.get(i).getPpUpdateReasons().get(0).getReasonDesc())
									|| procurementItems.get(i).getPpUpdateReasons().get(0).getReasonDesc().equals("\u200B")) {
								PpProcurementItem setIndicator = procurementItems.get(i);
								setIndicator.setIndicator(true);
								ppItemIndicator.add(setIndicator);
								indicatorIndex++;
							}// 28012021@27624 - end
						}
					}
					if (indicatorIndex > 0) {
						throw new EpException(MessageConst.ERROR_730035);
					}
				}
			}
		}
		log.info("End checkUpdateForReasonIndicator");
		// 24975@19112020 - start
	}
	
	// #25549@24022021 - start
	public void onPageChangeProcurementClone(PageEvent event) {
		// System.out.println(this.getManagedBean().getClonePpProcurementItemLs().size());
		if (this.getManagedBean().getClonePpProcurementItemLs().size() > 0) {
			this.getManagedBean().setDisableSelectBtn(false);
			// System.out.println("rowSelect is Enable Button !!:" + this.getManagedBean().isDisableSelectBtn());
		} else if (this.getManagedBean().getClonePpProcurementItemLs().size() == 0 || this.getManagedBean().getClonePpProcurementItemLs().size() < 0) {
			this.getManagedBean().setDisableSelectBtn(true);
			// System.out.println("unSelectRow is Disable Button !!:" + this.getManagedBean().isDisableSelectBtn());
		}
	}

	public void onPageChangeExpenditureClone(PageEvent event) {
		// System.out.println(this.getManagedBean().getClonePpExpenditureItemLs().size());
		if (this.getManagedBean().getClonePpExpenditureItemLs().size() > 0) {
			this.getManagedBean().setDisableSelectBtn(false);
			// System.out.println("rowSelect is Enable Button !!:" + this.getManagedBean().isDisableSelectBtn());
		} else if (this.getManagedBean().getClonePpExpenditureItemLs().size() == 0 || this.getManagedBean().getClonePpExpenditureItemLs().size() < 0) {
			this.getManagedBean().setDisableSelectBtn(true);
			// System.out.println("unSelectRow is Disable Button !!:" + this.getManagedBean().isDisableSelectBtn());
		}
	}
	// #25549@24022021 - end
}