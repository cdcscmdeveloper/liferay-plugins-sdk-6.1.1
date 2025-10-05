package my.ep.web.backingbean.sm.sitevisit;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSvOfficer;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTaskGroup;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.profilepersonalization.ProfilePersonalizationBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sitevisit.SvcSiteAssignManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmSvcAssignmentVO;

@ManagedBean(name = "svcSiteAssignBackingBean")
@RequestScoped
public class SvcSiteAssignBackingBean extends
		BaseBackingBean<SvcSiteAssignManagedBean> {
	private static final long serialVersionUID = 1L;

	public static Logger log = Logger.getLogger(SvcSiteAssignBackingBean.class);
	
	private final Long SV_BEFORE_APPROVE = 20110L;
	private final Long SV_AFTER_APPROVE = 20117L;
	private final String SVC_TASK_LIST = "/xhtml/bpmtasklist/svcTasklist.xhtml";
	private final String SVC_ACK_PAGE = "/xhtml/sm/svcassignment/svcAcknowledgement.xhtml";
	private static String SVC_PRE_RENDER_XHTML = "/xhtml/sm/svcassignment/prerenderSvcTaskList.xhtml";
	
	public SvcSiteAssignBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	/**
	 * retrieve site visit assignment Info by applId
	 * and populate on screen
	 */
	public void retrieveSiteAssignInfo(Long applId) {
		this.getManagedBean().setAppId(applId);
		//Old Version - Working set
		log.info("----------------------retrieveSiteAssignInfo start----------------");
		SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
		ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance().getServiceObject("sm", ISiteAssignSO.class);
		log.info("----------------------retrieveSiteAssignInfo----------------" + this.getManagedBean().getAppId());
		reqDTO.setAppId(this.getManagedBean().getAppId());
		SiteAssignResDTO resDTO = reqSO.retrieveSiteVisitByApplId(reqDTO);
		//26238 -  Start Disciplinary Action - 28496
		ProfilePersonalizationBackingBean profilepersonalization = new  ProfilePersonalizationBackingBean();
		profilepersonalization.getManagedBean().setSmSupplier(resDTO.getSmAppl().getSmSupplier());
		profilepersonalization.preRenderDisciplinaryAction(); 
		//26238 -  End Disciplinary Action - 28496
		constructSvTaskWithSVOList(resDTO.getSmAppl());	
		//this.getManagedBean().setSmSvTask(resDTO.getSmSvTask());
		getManagedBean().setSmAppl(resDTO.getSmAppl());
		
		setSiteVisitAddressesFacilityName();
		getAllSiteVisitCategoryCode();
		getWorkFlowInfo(getManagedBean().getAppId());
	}
	
	
	/**
	 * Submit Site Visi Assignment
	 * @param String action
	 * return String
	 */
	public String submitSiteVisitAssignment(String action){
		log.info("----------------------submitSiteVisitAssignment start----------------");
		
		List<String> svoNames = new ArrayList<String>();
		//try{
			SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
			ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteAssignSO.class);
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			
			reqDTO.setSvTaskList(new ArrayList<SmSvTask>());
			for(SmSvcAssignmentVO svcTaskVO : getManagedBean().getSvcTaskVOLs()){
				SmSvTask svTask = svcTaskVO.getSmSvTask();
				svTask.setSmSvOfficers(new ArrayList<SmSvOfficer>());
				
				for(PmUser pmUser : svcTaskVO.getSvoUserList().getTarget()){
					SmSvOfficer svOfficer = new SmSvOfficer();
					svOfficer.setSmSvTask(svTask);
					svOfficer.setPmUser(pmUser);
					if(pmUser!=null && !StringUtils.isBlank(pmUser.getUserName()) 
						&& !svoNames.contains(pmUser.getUserName())){
						svoNames.add(pmUser.getUserName());
					}
					svTask.getSmSvOfficers().add(svOfficer);
				}
			}
			
			List<String> bpmSupplierList = new ArrayList<String>();
			PmUser user = SmCommonUtil.getUserByUserId(getManagedBean().getSmAppl().getCreatedBy());
			if(user == null){
				throw new EpException("Supplier List cannot be null");
			}else{
				if(user.getUserId() == CommonConstants.SYSTEM_USER_ID){
					user = getSupplierMOFAdmin();
				}
				
				if(user == null){
					throw new EpException("Supplier List cannot be null.");
				}
			}
			log.info("Supplier Login ID = " + user.getLoginId());
			bpmSupplierList.add(user.getLoginId());
			
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setBpmPojo(getManagedBean().getBpmPojo());
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			reqDTO.setToken(token);
			reqDTO.setBpmSupplierList(bpmSupplierList);
			reqDTO.setSvoNames(svoNames);
			

			System.out.println("<<<<<reqDTO.setSmAppl>>>>" + reqDTO.getSmAppl().getApplNo());
			System.out.println("<<<<<reqDTO.setSmAppl>>>>" + reqDTO.getSmAppl().getApplId());
			
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			log.info("******submitSiteVisitAssignment - getSmSupplier******" + pmUser.getUserId());
			
			//set the username to msg
			StringBuilder userNameStr = new StringBuilder();
			for(String svoName : svoNames){
				userNameStr.append("<li>" + svoName + "</li>");
			}
		
			reqDTO.setLogKey(new LogKey(pmUser.getUserId()));
			reqDTO.setChangedBy(pmUser.getUserId());
			SiteAssignResDTO resDTO = reqSO.submitBPMTaskAssignment(reqDTO);
			
			if (resDTO.isSuccessful()) {
				log.info("Submission is success>>>>>>>>"); 
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
				MessageUtils.getMessage(MessageConst.INFO_721201,new String[] {getManagedBean().getSmAppl().getApplNo(),userNameStr.toString()}, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				renewSesionObject();
				return SVC_ACK_PAGE;
			} else {
				log.info("Submission is failed >>>>>>>>"); 
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				throw new EpException(MessageConst.ERROR_721202);
			}
	}
	
	/**
	 * retrieve the SVO based on supplier type and zone. 
	 * Populate the SVO List on screen for SVC to pick up
	 * @param SmAppl smAppl - SmAppl Object
	 */
	private void constructSvTaskWithSVOList(SmAppl smAppl){
		SiteAssignReqDTO userListReqDTO = new SiteAssignReqDTO();
		ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance().getServiceObject("sm", ISiteAssignSO.class);
		
		userListReqDTO.setSmAppl(smAppl);
		SiteAssignResDTO userListResDTO = reqSO.retrieveSVOListBySupplierTypeAndZone(userListReqDTO);
		
		List<PmUser> sourceLs = new ArrayList<PmUser>();
		List<PmUser> selectedLs = new ArrayList<PmUser>();
		
		log.info("********PM SVO********" + userListResDTO.getPmUsers().size());
		if(userListResDTO.getPmUsers().size() > 0){
			sourceLs = userListResDTO.getPmUsers();
		}
		
		getManagedBean().setSvcTaskVOLs(new ArrayList<SmSvcAssignmentVO>());
		
		for(SmSvTask svTask : smAppl.getSmSvTasks()){
			SmRemark remark = new SmRemark();
			remark.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SVC);
			remark.setDocId(svTask.getSvTaskId());
			svTask.setSmRemark(remark);
			svTask.getSmSvAddress().setAddress(SmCommonUtil.getInstance().getSmAddressById(svTask.getSmSvAddress().getSiteRefId()));
			
			SmSvcAssignmentVO svcAssignmentVO = new SmSvcAssignmentVO();
			svcAssignmentVO.setSmSvTask(svTask);
			svcAssignmentVO.setSvoUserList(new DualListModel<PmUser>(sourceLs, selectedLs));
			getManagedBean().getSvcTaskVOLs().add(svcAssignmentVO);
		}

	}
	
	/**
	 * Validation - to ensure at least 2 SVO being selected
	 */
	public void validateForm(){
		boolean invalid = true;
		
		for(SmSvcAssignmentVO svcAssignmentVO : getManagedBean().getSvcTaskVOLs()){
			//#28774 - Start
			if(svcAssignmentVO.getSvoUserList().getTarget().size() != 1){
			//#28774 - End
			  invalid = false;
			  break;
			}
		}
		
		if(!invalid){
			throw new EpException(MessageConst.ERROR_721201);
		}else{
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show()");
		}
	}
	
	/**
	 * Navigation to Acknowledgement page
	 * @return String
	 */
	public String navigateToAck(){
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721103,
		MessageUtils.BUNDLE_COMMON_MESSAGES,
		new FacesScopes().getUserLocale())));

		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}
	
	private FacesScopes getFacesScopes() {
		//CR 28774: Static Code Analysis
//		FacesScopes facesScopes = new FacesScopes();
//		return facesScopes;
		return new FacesScopes();
	}
	
	/**
	 * get Supplier Application Type Description
	 * @param String applType - application type
	 * @return String
	 */
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}

	//for local testing purpose
	public void preRenderView(){
		if(this.getManagedBean().getSmAppl() == null){
			//retrieveSiteAssignInfo();
		}
	}
	
	/**
	 * return to svcTaskList
	 */
	public String navigateToSvcTaskList(){
		return SVC_TASK_LIST;
	}
	
	/**
	 * redirect to svcTaskList
	 */
	public void redirectToSvcTaskList(){
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,SVC_PRE_RENDER_XHTML);
		//return SVC_TASK_LIST;
	}
	
	/**
	 * Get Label Desc
	 * @param label
	 * @param param
	 * @return
	 */
	public String getLabelWithParam(String label, String param){
		//CR 28774: Static Code Analysis
//		String labelDesc = SmCommonUtil.getMessage(label, new String[]{param});		
		return SmCommonUtil.getMessage(label, new String[]{param});
	}
	
	/**
	 * to get User Role - SVC
	 * @return String
	 */
	private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.UPS_SVC.value()))
			return RoleCode.UPS_SVC.value();		
		else 
			return null;
	}
	
	/**
	 * Set site visit address list facility name in data table
	 */
	private void setSiteVisitAddressesFacilityName() {
		//CR 28774: Static Code Analysis
		if (!getManagedBean().getSmAppl().getSmSvTasks().isEmpty()) {
			for (SmSvTask svTask : getManagedBean().getSmAppl().getSmSvTasks()) {
				
				if(svTask.getSmSvAddress() != null){
					SmCommonUtil.getInstance().setSmSvAddressInfoBySiteTypeAndRefId(svTask.getSmSvAddress().getSiteType(),
							svTask.getSmSvAddress().getSiteRefId(),svTask.getSmSvAddress(), getManagedBean().getSmAppl().getApplId());
					
					
				}
			}
		}
	}
	
	
	private void getWorkFlowInfo(Long applId){
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		reqDTO.setApplId(applId);
		
		SmWorkflowStatus workFlowStatus = SmServiceLookup.getSmWorkFlowStatusSO().retrieveCurrentWorkFlowStatusByDocId(reqDTO).getWorkflowStatus();
//		log.info("workFlowStatus>>>>" + workFlowStatus.getWorkflowStatusId());
//		log.info("workFlow status id >>>>" + workFlowStatus.getPmStatus().getStatusId());
		if(workFlowStatus != null){
			if(workFlowStatus.getPmStatus().getStatusId().equals(SV_BEFORE_APPROVE)){
				log.info("<<<< 20110 >>>>");
				getManagedBean().setSiteVisitStatus(SmCommonUtil.getMessage("lbl.sm.sitevist.to.be.performed.before.approval", null));
			}else if(workFlowStatus.getPmStatus().getStatusId().equals(SV_AFTER_APPROVE)){
				log.info("<<<< 20117 >>>>");
				getManagedBean().setSiteVisitStatus(SmCommonUtil.getMessage("lbl.sm.sitevist.to.be.performed.after.approval", null));
			}
			
			UserReqDTO userReqDTO = new UserReqDTO();
			//Start #29448
			if(workFlowStatus.getSmAppl() != null && workFlowStatus.getSmAppl().getPoUserId() != null){
				userReqDTO.setUserId(workFlowStatus.getSmAppl().getPoUserId());
			}else{
				userReqDTO.setUserId(workFlowStatus.getCreatedBy());
			}
			//End #29448
			PmUser processingOfficer = SmServiceLookup.getUserSO().retrieveUserByUserId(userReqDTO).getPmUser();
			
			if(processingOfficer != null){
				getManagedBean().setProcessingOfficerName(processingOfficer.getUserName());
			}
		}
	}
	
	/**
	 * Get a list of all non-duplicate category codes for site visit
	 */
	private void getAllSiteVisitCategoryCode(){
		SmAppl smAppl = getManagedBean().getSmAppl();
		
		getManagedBean().setCategoryLs(new ArrayList<SmSupplierCategory>());
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		List<SmSupplierCategory> tempL = new ArrayList<>();
		//max loop: 39
		for(SmSvTask svTask : smAppl.getSmSvTasks()){
			tempL.addAll(getSiteVisitCategoryCode(smAppl, svTask.getPmCategoryL3()));
			if(smAppl.isSiteVisitGroup() && svTask.getSmSvTaskGroups() != null){
				for(SmSvTaskGroup svTGroup : svTask.getSmSvTaskGroups()){
					tempL.addAll(getSiteVisitCategoryCode(smAppl, svTGroup.getPmCategoryL3()));
				}
			}
		}
		for(SmSupplierCategory catTemp : tempL){
			boolean duplicate = false;
			for(SmSupplierCategory catBean : getManagedBean().getCategoryLs()){
				if(catTemp.getPmCategoryL3().getCategoryL3Id().equals(
						catBean.getPmCategoryL3().getCategoryL3Id())){
					duplicate = true;
					break;
				}
			}
			if(!duplicate){
				getManagedBean().getCategoryLs().add(catTemp);
			}
		}
		SmApplicationUtil.sortSupplierCategory(getManagedBean().getCategoryLs());
	}
	
	private List<SmSupplierCategory> getSiteVisitCategoryCode(SmAppl appl, PmCategoryL3 l3){
		if(appl == null || l3 == null || appl.getApplId() == null || l3.getCategoryL3Id() == null){
			return new ArrayList<>();
		}
		return SmCommonUtil.getInstance().retrieveSupplierCategoryByApplIdAndCategoryId(
				appl.getApplId(), l3.getCategoryL3Id(), appl.getApplType());
	}
	//28775 - Site Visit Grouping Enhancement - 28775 - end
	
	@SuppressWarnings("unused")
	private PmUser getSupplierMOFAdmin(){
		log.info("<<<<<<<< getSupplierMOFAdmin>>>>>>>>>>>>>>" + getManagedBean().getSmAppl().getApplId());
		PmUser pmUser = null;
		//CR 28774: Static Code Analysis
		if(!getManagedBean().getSmAppl().getSmPersonnels().isEmpty()){
			for(SmPersonnel personnel : getManagedBean().getSmAppl().getSmPersonnels()){
				if(personnel.getEpRole() != null){
					if(personnel.getEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())){
						if(personnel.getPmUser() != null){
							if(personnel.getPmUser().getUserId() != CommonConstants.SYSTEM_USER_ID && 
									personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
								pmUser = personnel.getPmUser();
								break;
							}
						}
					}
				}
			}
		}
		
		if(pmUser != null){
			log.info(">>>>>>>>>pmUser>>>>>>>" + pmUser != null ? pmUser.getUserId() : "No User Found");
		}
		
		log.info("<<<<<<<< getSupplierMOFAdmin End>>>>>>>>>>>>>>");
		
		if(pmUser == null){
			throw new EpException("MOF supplier admin not found");
		}
		
		return pmUser;
	} 
	
	//28775 - Site Visit Grouping Enhancement - 28775 - start
	/**
	 * @param svTask
	 * @return facility type and category code(s)
	 */
	public String getFacilityType(SmSvTask svTask){
		if(svTask == null || svTask.getSmSvAddress() == null){
			return "";
		}
		String facTypes = "";
		if(svTask.getSmSvAddress().getFacilityType() != null){
			facTypes = svTask.getSmSvAddress().getFacilityType();
		}
		if(svTask.getPmCategoryL3() != null){
			facTypes += "<br/>" + svTask.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()
					+ svTask.getPmCategoryL3().getPmCategoryL2().getCategoryL2Code()
					+ svTask.getPmCategoryL3().getCategoryL3Code();
		}		
		if(!getManagedBean().getSmAppl().isSiteVisitGroup()){
			return facTypes;
		}
		
		for(SmSvTaskGroup svTGroup : svTask.getSmSvTaskGroups()){
			String catCode = svTGroup.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()
					+ svTGroup.getPmCategoryL3().getPmCategoryL2().getCategoryL2Code()
					+ svTGroup.getPmCategoryL3().getCategoryL3Code();
			if(!facTypes.contains(catCode)){
				facTypes = facTypes.concat("<br/>"+catCode);
			}
		}
		return facTypes;
	}
	//28775 - Site Visit Grouping Enhancement - 28775 - end
}