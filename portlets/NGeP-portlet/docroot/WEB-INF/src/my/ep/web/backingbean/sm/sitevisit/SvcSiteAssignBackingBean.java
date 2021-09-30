package my.ep.web.backingbean.sm.sitevisit;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "svcSiteAssignBackingBean")
@RequestScoped
public class SvcSiteAssignBackingBean extends
		BaseBackingBean<SvcSiteAssignManagedBean> {
	private static final long serialVersionUID = 1L;

	private final static Logger log = Logger.getLogger(SvcSiteAssignBackingBean.class);
	
	private static final Long SV_BEFORE_APPROVE = 20110L;
	private static final Long SV_AFTER_APPROVE = 20117L;
	private static final String SVC_TASK_LIST = "/xhtml/bpmtasklist/svcTasklist.xhtml";
	private static final String SVC_ACK_PAGE = "/xhtml/sm/svcassignment/svcAcknowledgement.xhtml";
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
		SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
		ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance().getServiceObject("sm", ISiteAssignSO.class);
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
		
		List<String> svoNames = new ArrayList<String>();
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
			bpmSupplierList.add(user.getLoginId());
			
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setBpmPojo(getManagedBean().getBpmPojo());
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			reqDTO.setToken(token);
			reqDTO.setBpmSupplierList(bpmSupplierList);
			reqDTO.setSvoNames(svoNames);
			
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			
			//set the username to msg
			StringBuilder userNameStr = new StringBuilder();
			for(String svoName : svoNames){
				userNameStr.append("<li>" + svoName + "</li>");
			}
		
			reqDTO.setLogKey(new LogKey(pmUser.getUserId()));
			reqDTO.setChangedBy(pmUser.getUserId());
			SiteAssignResDTO resDTO = reqSO.submitBPMTaskAssignment(reqDTO);
			
			if (resDTO.isSuccessful()) {
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
				MessageUtils.getMessage(MessageConst.INFO_721201,new String[] {getManagedBean().getSmAppl().getApplNo(),userNameStr.toString()}, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				renewSesionObject();
				return SVC_ACK_PAGE;
			} else {
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
	 * Validation - to ensure only 1 SVO being selected
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
	 * Set site visit address list facility name in data table
	 */
	private void setSiteVisitAddressesFacilityName() {
		//28775 - Site Visit Grouping Enhancement - 28775 
		//name in sm_facility
		//unique name too?
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
		if(workFlowStatus != null){
			if(workFlowStatus.getPmStatus().getStatusId().equals(SV_BEFORE_APPROVE)){
				getManagedBean().setSiteVisitStatus(SmCommonUtil.getMessage("lbl.sm.sitevist.to.be.performed.before.approval", null));
			}else if(workFlowStatus.getPmStatus().getStatusId().equals(SV_AFTER_APPROVE)){
				getManagedBean().setSiteVisitStatus(SmCommonUtil.getMessage("lbl.sm.sitevist.to.be.performed.after.approval", null));
			}
			
			UserReqDTO userReqDTO = new UserReqDTO();
			//#29448
			userReqDTO.setUserId(workFlowStatus.getSmAppl().getPoUserId());
			PmUser processingOfficer = SmServiceLookup.getUserSO().retrieveUserByUserId(userReqDTO).getPmUser();
			
			if(processingOfficer != null){
				getManagedBean().setProcessingOfficerName(processingOfficer.getUserName());
			}
		}
	}
	
	private void getAllSiteVisitCategoryCode(){
		SmAppl smAppl = getManagedBean().getSmAppl();
		
		getManagedBean().setCategoryLs(new ArrayList<SmSupplierCategory>());
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		List<SmSupplierCategory> tempL = new ArrayList<SmSupplierCategory>();
		for(SmSvTask svTask : smAppl.getSmSvTasks()){
			if(svTask.getPmCategoryL3() == null) continue;
			tempL.addAll(SmCommonUtil.getInstance().retrieveSupplierCategoryByApplIdAndCategoryId
					(smAppl.getApplId(), svTask.getPmCategoryL3().getCategoryL3Id()));
			if(!smAppl.isSiteVisitGroup() || svTask.getSmSvTaskGroups() == null)
				continue;
			for(SmSvTaskGroup svTGroup : svTask.getSmSvTaskGroups()){
				tempL.addAll(SmCommonUtil.getInstance().retrieveSupplierCategoryByApplIdAndCategoryId
						(smAppl.getApplId(), svTGroup.getPmCategoryL3().getCategoryL3Id()));
			}
		}
		for(SmSupplierCategory catTemp : tempL){
			boolean dupe = false;
			for(SmSupplierCategory catBean : getManagedBean().getCategoryLs()){
				if(catTemp.getPmCategoryL3().getCategoryL3Id().equals(
						catBean.getPmCategoryL3().getCategoryL3Id())){
					dupe = true;
					break;
				}
			}
			if(!dupe)	
				getManagedBean().getCategoryLs().add(catTemp);
		}
		SmApplicationUtil.sortSupplierCategory(getManagedBean().getCategoryLs());
		/*
		List<String> tempCatLs = new ArrayList<String>();
		for(SmSvTask svTask : smAppl.getSmSvTasks()){
			if(svTask.getPmCategoryL3() != null){
				List<SmSupplierCategory> suppCatLs = SmCommonUtil.getInstance().
						retrieveSupplierCategoryByApplIdAndCategoryId(smAppl.getApplId(), svTask.getPmCategoryL3().getCategoryL3Id());
				//CR 28774: Static Code Analysis
				if(!suppCatLs.isEmpty()){
					for(SmSupplierCategory suppCat : suppCatLs){
						StringBuilder catrStr = new StringBuilder();
						catrStr.append(suppCat.getPmCategoryL1().getCategoryL1Code());
						catrStr.append(suppCat.getPmCategoryL2().getCategoryL2Code());
						catrStr.append(suppCat.getPmCategoryL3().getCategoryL3Code());
						
						if(!tempCatLs.contains(catrStr.toString())){
							if(suppCat != null){
								getManagedBean().getCategoryLs().add(suppCat);
							}
							tempCatLs.add(catrStr.toString());
						}
					}
				}
			}
		}
		*/
		//28775 - Site Visit Grouping Enhancement - 28775 - end
	}
	
	private PmUser getSupplierMOFAdmin(){
		PmUser pmUser = null;
		//CR 28774: Static Code Analysis
		if(!getManagedBean().getSmAppl().getSmPersonnels().isEmpty()){
			for(SmPersonnel personnel : getManagedBean().getSmAppl().getSmPersonnels()){
				if(personnel.getEpRole() != null){
					if(personnel.getEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())){
						if(personnel.getPmUser() != null){
							if(personnel.getPmUser().getUserId() != CommonConstants.SYSTEM_USER_ID && personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
								pmUser = personnel.getPmUser();
								break;
							}
						}
					}
				}
			}
		}
		
		if(pmUser == null){
			throw new EpException("MOF supplier admin not found");
		}
		
		return pmUser;
	} 
	
	//28775 - Site Visit Grouping Enhancement - 28775 - start
	public String getFacilityType(SmSvTask svTask){
		String s = svTask.getSmSvAddress().getFacilityType();
		if(svTask.getPmCategoryL3() != null){
			s = s.concat("<br/>"
					+ svTask.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()
					+ svTask.getPmCategoryL3().getPmCategoryL2().getCategoryL2Code()
					+ svTask.getPmCategoryL3().getCategoryL3Code());
		}		
		if(!getManagedBean().getSmAppl().isSiteVisitGroup()) 
			return s;
		
		for(SmSvTaskGroup svTGroup : svTask.getSmSvTaskGroups()){
			String catCode = svTGroup.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()
					+ svTGroup.getPmCategoryL3().getPmCategoryL2().getCategoryL2Code()
					+ svTGroup.getPmCategoryL3().getCategoryL3Code();
			if(!s.contains(catCode))
				s = s.concat("<br/>"+catCode);
		}
		return s;
	}
	//28775 - Site Visit Grouping Enhancement - 28775 - end
}
