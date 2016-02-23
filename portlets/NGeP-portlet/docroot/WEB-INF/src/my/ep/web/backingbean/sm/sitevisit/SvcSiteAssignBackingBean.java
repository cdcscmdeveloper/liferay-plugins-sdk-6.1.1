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
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.dto.sm.siteVisit.SiteVisitReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSvCategory;
import my.ep.persistence.entity.sm.SmSvOfficer;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sitevisit.SvcSiteAssignManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmSvcAssignmentVO;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "svcSiteAssignBackingBean")
@RequestScoped
public class SvcSiteAssignBackingBean extends
		BaseBackingBean<SvcSiteAssignManagedBean> {
	public static Logger log = Logger.getLogger(SvcSiteAssignBackingBean.class);
	
	private final Long SV_BEFORE_APPROVE = 20110L;
	private final Long SV_AFTER_APPROVE = 20117L;
	
	public SvcSiteAssignBackingBean() {

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
		
		constructSvTaskWithSVOList(resDTO.getSmAppl());	
		/*if(resDTO.getSmAppl().getSmApplSectionReviews() != null){
			setPoRemarkToSVC(resDTO.getSmAppl().getSmApplSectionReviews());
		}*/
		//this.getManagedBean().setSmSvTask(resDTO.getSmSvTask());
		getManagedBean().setSmAppl(resDTO.getSmAppl());
		
		setSiteVisitAddressesFacilityName();
		getAllCategoryCode();
		getWorkFlowInfo(getManagedBean().getAppId());
		
		
	}
	
	
	/**
	 * Submit Site Visi Assignment
	 * @param String action
	 * return String
	 */
	public String submitSiteVisitAssignment(String action){
		log.info("----------------------submitSiteVisitAssignment start----------------");
		
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
					svTask.getSmSvOfficers().add(svOfficer);
				}
			}
			
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setBpmPojo(getManagedBean().getBpmPojo());
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			reqDTO.setToken(token);
			
			System.out.println("<<<<<reqDTO.setSmAppl>>>>" + reqDTO.getSmAppl().getApplNo());
			System.out.println("<<<<<reqDTO.setSmAppl>>>>" + reqDTO.getSmAppl().getApplId());
			reqDTO.setTrackingDairyVO(
					SmCommonUtil.assignTrackingDiaryValues(SupplierManagementConst.SVC_ASSIGN_SITEVISIT, 
							this.getManagedBean().getSmAppl().getApplId(), this.getManagedBean().getSmAppl().getApplNo(), getUserRole()));
			
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			log.info("******submitSiteVisitAssignment - getSmSupplier******" + pmUser.getUserId());
			
			reqDTO.setLogKey(new LogKey(pmUser.getUserId()));
			SiteAssignResDTO resDTO = reqSO.submitBPMTaskAssignment(reqDTO);

			
			if (resDTO.isSuccessful()) {
				log.info("Submission is success>>>>>>>>"); 
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
				MessageUtils.getMessage(MessageConst.INFO_721201,new String[] {getManagedBean().getSmAppl().getApplNo()}, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				log.info("Submission is failed >>>>>>>>"); 
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				throw new EpException(MessageConst.ERROR_721202);
			}
		/*}catch(Exception ex){
			log.info("submitSiteVisitAssignment>>>>>" + ex.getMessage());
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
			throw new EpException(MessageConst.ERROR_721202);
		}*/

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
			if(svcAssignmentVO.getSvoUserList().getTarget().size() < 2){
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
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
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
		SmFacility smFacility = null;
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		if (getManagedBean().getSmAppl().getSmSvTasks().size() > 0) {
			for (SmSvTask svTask : getManagedBean().getSmAppl().getSmSvTasks()) {
				log.info("*********smAppl.getSmSvTasks()**********" + svTask.getSmSvAddress().getSmAddress().getAddressId());
				if (svTask.getSmSvAddress() != null) {
					if(svTask.getSmSvAddress().getSmAddress() != null){
						reqDTO.setSmAddressId(svTask.getSmSvAddress().getSmAddress().getAddressId());
						smFacility = SmServiceLookup.getSiteVisitSO()
								.retrieveSmFacilityByAddressId(reqDTO)
								.getSmFacility();
						svTask.getSmSvAddress().setFacilityName(smFacility == null ? ""
								: smFacility.getFacilityName());
						if(smFacility != null && smFacility.getFacilityType() != null){
							svTask.getSmSvAddress().setFacilityType(smFacility.getFacilityType());
						}
					}
				}
			}
			
			sortSmSvAddressByFacilityType();
		}
	}
	
	/**
	 * Sort smSvAddress by facility type
	 */
	private void sortSmSvAddressByFacilityType(){
		/*if(getManagedBean().getSmSvTask().getSmSvAddresses().size() > 0){
			 Collections.sort(getManagedBean().getSmSvTask().getSmSvAddresses(), new Comparator<SmSvAddress>(){
				@Override
				public int compare(SmSvAddress arg0, SmSvAddress arg1) {
					List<String> orderLs = Arrays.asList("B", "H", "D", "F","S","W");
					
					int result = 0;
					int argIndex0 = orderLs.indexOf(arg0.getFacilityType());
					int argIndex1 = orderLs.indexOf(arg1.getFacilityType());
					
				    //if neither are found in the order list, then do natural sort
				    //if only one is found in the order list, float it above the other
				    //if both are found in the order list, then do the index compare
					
					if (argIndex0 < 0 && argIndex1 < 0){ 
						if(arg0.getFacilityType() != null && arg1.getFacilityType() != null){
							result = arg0.getFacilityType().compareTo(arg1.getFacilityType());
						}
					}else if (argIndex0 < 0){ 
						result = 1;
					}else if (argIndex1 < 0){ 
						result = -1;
					}else{ 
						result = argIndex0 - argIndex1;
					}
				    return result;
				}
			 });
		}*/
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
			userReqDTO.setUserId(workFlowStatus.getCreatedBy());
			PmUser processingOfficer = SmServiceLookup.getUserSO().retrieveUserByUserId(userReqDTO).getPmUser();
			
			if(processingOfficer != null){
				getManagedBean().setProcessingOfficerName(processingOfficer.getUserName());
			}
		}
	}
	
	private void getAllCategoryCode(){
		SmAppl smAppl = getManagedBean().getSmAppl();
		
		getManagedBean().setCategoryLs(new ArrayList<SmSvCategory>());
		for(SmSvTask svTask : smAppl.getSmSvTasks()){
			if(svTask.getSmSvCategories().size() > 0){
				for(SmSvCategory svCategory : svTask.getSmSvCategories()){
					getManagedBean().getCategoryLs().add(svCategory);
				}
			}
		}
	}
}
