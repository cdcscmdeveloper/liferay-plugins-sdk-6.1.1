/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.taskassign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.AssigneeVO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.dto.fl.FlDocumentReqDTO;
import my.ep.dto.fl.FlDocumentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpBpmException;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusRole;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.pm.taskassign.TaskAssignmentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
/**
 * Backing Bean for Task Assignment
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name="taskAssignBackingBean")
@ViewScoped
public class TaskAssignmentBackingBean extends BaseBackingBean<TaskAssignmentManagedBean>{
	private static Logger log = Logger.getLogger(TaskAssignmentBackingBean.class);
	public static final boolean SEARCH_DEFAULT = false;
	//Navigation Details
	public static final String NAV_TO_SEARCH = "taskAssignment";
	public static final String NAV_TO_MAINTAIN = "taskAssignment";

	//Mapper for DDL object
	private static Map<Long, Object> orgTypeMapper;
	
	//List for Paging
	private PagingDataModel<WorklistItemVO> taskListDataModel = new taskPagingDataModel();
	
	private String currentDate;
	public static final String APPROVER = "APPROVER";
	
	public void preLoading(){
		log.info("preLoading");
		this.retrieveOrgTypeMap();
		this.resetTaskAssignSearchBean();
		this.getManagedBean().setOrgProfileRequired(true);
		this.getManagedBean().setResultUser(null);
		this.getManagedBean().setResultOrgProfile(null);
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
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
	 * reset search bean fields
	 */
	public void resetTaskAssignSearchBean(){
		log.info("resetTaskAssignSearchBean");
		this.getManagedBean().setTaskAssignmentSearchBean(new TaskAssignmentSearchBean());
	}
	
	/**
	 * search task assignment
	 */
	public void searchTaskAssign(){
		log.info("searchDocument - Start");
		if(!(this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile()!=null 
				&& this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().getOrgProfileId()!=null)){
			throw new EpException("Org. Name is required.");//TODO
		}
		if(!(this.getManagedBean().getTaskAssignmentSearchBean().getUser()!=null 
				&& this.getManagedBean().getTaskAssignmentSearchBean().getUser().getUserId()!=null)){
			throw new EpException("User Name is required.");//TODO
		}
		this.getManagedBean().setResultUser(this.getManagedBean().getTaskAssignmentSearchBean().getUser());
		this.getManagedBean().setResultOrgProfile(this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile());
		this.taskListDataModel.reloadData();
		log.info("searchDocument - End");
		
	}

	public String getCurrentDateStr(){
//		conditionField="orgTypeCode.parameterType^#{ParameterTypeConst.organisationTypes}|orgTypeCode.parameterCode^#{taskAssignBackingBean.managedBean.taskAssignmentSearchBean.orgType.parameterCode}|recordStatus^1|pmOrgValidity.effDate^#{taskAssignBackingBean.getCurrentDateStr()}^6|pmOrgValidity.expDate^#{taskAssignBackingBean.getCurrentDateStr()}^4*pmOrgValidity.expDate^null^11"/>
		if(this.currentDate==null){
			currentDate = PmCommonUtil.getCurrentDateStr(new Date());
		}
		return currentDate;
		
	}
	
	/**
	 * retrieve and set org type mapper
	 */
	private void retrieveOrgTypeMap(){
		log.info("retrieveOrgTypeMap");
		List<PmParameterDesc> parameterDescs = getOrgTypeList();
		orgTypeMapper = new HashMap<Long,Object>();
		for(PmParameterDesc pmDesc: parameterDescs){
			orgTypeMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}
	
	/**
	 * Retrieve Org. Type from PM
	 * @return List<PmParameterDesc> 
	 */
	public List<PmParameterDesc> getOrgTypeList() {
		log.info("getOrgTypeList");
		return PmCommonUtil.retrieveFilteredOrgTypes(ParameterTypeConst.ORGANISATION_TYPES
				, null
				, new ArrayList(Arrays.asList(ProfileManagementConst.TASK_ASSIGNMENT_ORG_TYPE_INCLUDE)));
	}
	
	/**
	 * change organization type
	 */
	public void changeOrgType() {
		log.info("changeOrgType");
		if(this.getManagedBean().getTaskAssignmentSearchBean()!=null){
			
			if(this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile()==null){
				this.getManagedBean().getTaskAssignmentSearchBean().setOrgProfile(new PmOrgProfile());
			}
			this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().setOrgName("");
			
			if(this.getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterId()!=null){
				PmParameter pmParameter = (PmParameter) orgTypeMapper.get(this.getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterId());
				this.getManagedBean().getTaskAssignmentSearchBean().getOrgType().setParameterCode(pmParameter.getParameterCode());
				
				
				if(ParameterConst.ORG_TYPE_SM_BPK_HQ.equals(pmParameter.getParameterCode())
					|| ParameterConst.ORG_TYPE_SM_PMS_SABAH.equals(pmParameter.getParameterCode())
					|| ParameterConst.ORG_TYPE_SM_PMS_SARAWAK.equals(pmParameter.getParameterCode())){
					this.getManagedBean().setOrgProfileRequired(false);
					//default org profile
					PmOrgValidity pmOrgValidity = getOrgProfile(pmParameter.getParameterCode());
					this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().setOrgProfileId(pmOrgValidity.getPmOrgProfile().getOrgProfileId());
					this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().setOrgName(pmOrgValidity.getOrgName());
				}else{
					this.getManagedBean().setOrgProfileRequired(true);
				}
			}
		}
		
	}
	
	/**
	 * retrieve valid org profile based on selected org type
	 * @param paramCode
	 * @return PmOrgValidity
	 */
	private PmOrgValidity getOrgProfile(String paramCode){
		ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();
		
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("orgTypeCode.parameterCode");
		bean.setSearchValue(paramCode);
		searchBean.getDataFilterBeans().add(bean);
		
		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("recordStatusFlg");
		bean.setSearchValue(1);
		searchBean.getDataFilterBeans().add(bean);
		
		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("pmOrgValidity.recordStatusFlg");
		bean.setSearchValue(1);
		searchBean.getDataFilterBeans().add(bean);
		
		PmOrgValidity pmOrgValidity = new PmOrgValidity();

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		OrgProfileResDTO resDTO = null;
		reqDTO.setOrgType(paramCode);
//		reqDTO.setOrgProfileSearchBean(searchBean);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		resDTO = orgProfileSO.retrieveValidOrgProfile(reqDTO);
		if(resDTO.isSuccessful()){
			pmOrgValidity = resDTO.getPmOrgValidity();
//			System.out.println("size>>>>" + resDTO.getOrgProfileResultBean().getResultList().size()); searchOrgProfile
//			for(PmOrgProfile temp:resDTO.getOrgProfileResultBean().getResultList()){
//				System.out.println(temp.getPmOrgValidity().getOrgName());
//			}
		}
		return pmOrgValidity;
			
	}
	/**
	 * Redirect to Task Re-assignment Details
	 * @param item WorklistItemVO
	 * @return xhtml
	 */
	public void redirectToTaskReassign(WorklistItemVO item){
		log.info("redirectToTaskReassign - Start");
		try{
			this.getManagedBean().setAvailableUsers(new ArrayList<PmUser>());
			this.getManagedBean().setSelectedUser(null);
			this.getManagedBean().setSelectedTask(item);
			this.getManagedBean().setAvailableUsers(generateAvailableUsers(item));
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("redirectToTaskReassign - End");
		RequestContext.getCurrentInstance().execute("dlTaskReassignment.show()");
	}
	
	
	/**
	 * Get meaningful application status message
	 * @param docInt
	 * @return String
	 */
	public String applicationStatus(Integer docInt) {
		log.info("applicationStatus");
		log.info("docInt:" + docInt);
		if(docInt!=null){
			return getStatusDescById(docInt.longValue());
		}else{
			return "";
		}
	}

	
	
	/**
	 * retrieve available roles by status id
	 * @param statusId
	 * @return List<String>
	 */
	private List<String> retrieveAvailableRoles(Long statusId){
		log.info("retrieveAvailableRoles");
		List<String> statusRoles = new ArrayList<String>();
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();
			IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			reqDTO.setStatusId(statusId);
			StatusResDTO resDTO = statusSO.retrieveStatusRoleByStatusId(reqDTO);
			if(resDTO.isSuccessful()){
				for(PmStatusRole statusRole:resDTO.getStatusRoleList()){
					statusRoles.add(statusRole.getPmRole().getRoleCode());
				}
			}
			
		} catch (Exception e) {
//			throw new EpException(MessageConst.ERROR_770053, new String[]{id.toString(), e.getMessage()});
//			TODO
			e.printStackTrace();
			throw new EpException("Unable to retrieve status role list.");
		}
		return statusRoles;
	}
	
	/**
	 * retrieve status id list for BPM
	 * @return
	 */
	private List<Integer> retrieveAvailableStatusId(){
		log.info("retrieveAvailableRoles");
		List<Integer> statusIds = new ArrayList<Integer>();
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();
			IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusResDTO resDTO = statusSO.retrieveStatusRoleStatusId(reqDTO);
			if(resDTO.isSuccessful()){
				for(int i=0; i<resDTO.getStatusIds().size(); i++){
					statusIds.add((int) (long)resDTO.getStatusIds().get(i));
				}
			}
			
		} catch (Exception e) {
//			throw new EpException(MessageConst.ERROR_770053, new String[]{id.toString(), e.getMessage()});
//			TODO
			e.printStackTrace();
			throw new EpException("Unable to retrieve status id list.");
			
		}
		return statusIds;
	}
	
	/**
	 * retrieve and generate available users
	 * @return List<PmUser>
	 */
	public List<PmUser> generateAvailableUsers(WorklistItemVO task){
		log.info("generateAvailableUsers");

		boolean isApprover = false;
		List <PmUser> pmUsers = new ArrayList<PmUser>();
		try {
			List<String> statusRoles = retrieveAvailableRoles(this.getManagedBean().getSelectedTask().getDocStatus().longValue());
			Long userId = this.getManagedBean().getTaskAssignmentSearchBean().getUser().getUserId();
			Long orgProfileId = this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().getOrgProfileId();
			Long orgTypeId = this.getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterId();
			ISearchBean<PmUser> searchBean = new SearchBean<PmUser>();
		
			/* Search Bean Filter - Start */
			// only active user
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("recordStatusFlg");
			bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(bean);
			
			// only active user role
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmUserOrgs.pmUserRoles.recordStatusFlg");
			bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(bean);
			
			// only active org group
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmUserOrgs.recordStatusFlg");
			bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(bean);
			
			// not current selected user
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_NOT_EQUALS.value());
			bean.setSearchField("userId");
			bean.setSearchValue(userId);
			searchBean.getDataFilterBeans().add(bean);
				
			// same org profile id
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmUserOrgs.pmOrgProfile.orgProfileId");
			bean.setSearchValue(orgProfileId);
			searchBean.getDataFilterBeans().add(bean);
			
			// same org type id
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmUserOrgs.pmOrgProfile.orgTypeCode.parameterId");
			bean.setSearchValue(orgTypeId);
			searchBean.getDataFilterBeans().add(bean);

			// in applicable role code
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
			bean.setSearchField("pmUserOrgs.pmUserRoles.pmRole.roleCode");
			bean.setSearchValue(statusRoles);
			searchBean.getDataFilterBeans().add(bean);
			
			// approver should be returned by BPM not selected from DB
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_NOT_LIKE.value());
			bean.setSearchField("pmUserOrgs.pmUserRoles.pmRole.roleCode");
			bean.setSearchValue(APPROVER);
			searchBean.getDataFilterBeans().add(bean);
			/* Search Bean Filter - End */
			
			//search users
			pmUsers = PmCommonUtil.searchUsersBySearchBean(searchBean);

			//if contains approver role
			for(String roleCode:statusRoles){
				if(roleCode.toLowerCase().contains(APPROVER.toLowerCase())){
					isApprover = true;
					break;
				}
			}
			if(isApprover){
				generateCompleteUsers(pmUsers, task);
			}
			
				
		} catch (Exception e) {
//			throw new EpException(MessageConst.ERROR_770053, new String[]{id.toString(), e.getMessage()});
//			TODO
			e.printStackTrace();
			throw new EpException("Unable to retrieve available users.");
		}		
		return pmUsers;
	}
	
	/**
	 * retrieve selected approvers from modules (SC, FL, CT)
	 * @param pmUsers
	 * @param task
	 */
	private void generateCompleteUsers(List<PmUser> pmUsers, WorklistItemVO task){
		log.info("generateCompleteUsers");
		String moduleCode = PmCommonUtil.getPmStatusById(task.getDocStatus().longValue()).getModuleCode();
		log.info("moduleCode: " + moduleCode);
		List <PmUser> selectedApprovers = new ArrayList<PmUser>();
		//FL
		if(ModuleCodeConst.ORDER_FULFILLMENT.equalsIgnoreCase(moduleCode)){
			selectedApprovers = retrieveFlApprovers(task.getDocNumber(), task.getDocType());
		//SC
		}else if(ModuleCodeConst.SUPPLIER_SELECTION.equalsIgnoreCase(moduleCode)){
			selectedApprovers = retrieveScApprovers(task.getDocId(), task.getDocStatus());
		//CT
		}else if(ModuleCodeConst.CONTRACT_MANAGEMENT.equalsIgnoreCase(moduleCode)){
			selectedApprovers = retrieveCtApprovers(task.getDocNumber(), task.getDocType());
		}
		if(selectedApprovers!=null && !selectedApprovers.isEmpty()){
			log.info("add approvers to list");
			pmUsers.addAll(selectedApprovers);
		}
	}

	/**
	 * retrieve selected approvers from FL
	 * @param docNo
	 * @param docType
	 * @return List<PmUser>
	 */
	private List<PmUser> retrieveFlApprovers(String docNo, String docType){
		log.info("retrieveFlApprovers");
		List <PmUser> selectedApprovers = new ArrayList<PmUser>();
		IFulfilmentDocumentSO reqSO = null;
		FlDocumentReqDTO reqDTO = new FlDocumentReqDTO();
		FlDocumentResDTO resDTO = new FlDocumentResDTO();
		try{
			log.info("docType: " + docType);
			log.info("docNo: " + docNo);
			reqDTO.setDocType(docType);
			reqDTO.setDocNo(docNo);
			reqSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentDocumentSO.class);
			resDTO = reqSO.retrieveApproverListByDocTypeAndNo(reqDTO);
			if(resDTO.isSuccessful()){
				selectedApprovers = PmCommonUtil.retriveUsersByIds(resDTO.getUserIdList());
			}
		}catch(Exception e){
			throw e; 
		}finally{
			reqSO = null;
		}
		
		return selectedApprovers;
	}
	/**
	 * retrieve selected approvers from CT
	 * @param docNo
	 * @param docType
	 * @return List<PmUser> 
	 */
	private List<PmUser> retrieveCtApprovers(String docNo, String docType){
		log.info("retrieveCtApprovers");
		List <PmUser> selectedApprovers = new ArrayList<PmUser>();
		IContractIntegrationSO reqSO = null;
		ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();
		ContractIntegrationResDTO resDTO = new ContractIntegrationResDTO();
		try{
			log.info("docType: " + docType);
			log.info("docNo: " + docNo);
			reqDTO.setDocType(docType);
			reqDTO.setDocNo(docNo);
			reqSO = (IContractIntegrationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractIntegrationSO.class);
			resDTO = reqSO.retrieveApproverListByDocTypeAndNo(reqDTO);
			if(resDTO.isSuccessful()){
				selectedApprovers = PmCommonUtil.retriveUsersByIds(resDTO.getUserIdList());
			}
		}catch(Exception e){
			throw e; 
		}finally{
			reqSO = null;
		}
		return selectedApprovers;
	}
	
	/**
	 * retrieve selected approvers from SC
	 * @param docId
	 * @param statusCode
	 * @return List<PmUser>
	 */
	private List<PmUser> retrieveScApprovers(Integer docId, Integer statusCode){
		log.info("retrieveScApprovers");
		List <PmUser> selectedApprovers = new ArrayList<PmUser>();
		IQTDetailSO reqSO = null;
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		QuationTenderResDTO resDTO = new QuationTenderResDTO();
		try{
			log.info("statusCode: " + statusCode);
			log.info("docId: " + docId);
			
			reqDTO.setStatusId(statusCode.longValue());
			reqDTO.setDocId(docId.longValue());
			reqSO = (IQTDetailSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			resDTO = reqSO.retrieveApprovalsByStatusId(reqDTO);
			
			if(resDTO.isSuccessful()){
				selectedApprovers = resDTO.getApprovers();
			}
		}catch(Exception e){
			throw e; 
		}finally{
			reqSO = null;
		}
		return selectedApprovers;
	}
	
	/**
	 * Reassign task to selected user
	 */
	public void confirmReassignTask(){
		log.info("confirmReassignTask");
		boolean isSuccess = false;
		PmUser pmUser = this.getManagedBean().getSelectedUser();
		WorklistItemVO task = this.getManagedBean().getSelectedTask();
		if(pmUser==null){
			throw new EpException("Please select an user to be re-assigned.");//TODO
		}
		if(task==null){
			throw new EpException("Task is not null.");//TODO
		}
		
		isSuccess = reassignTask(pmUser, task.getTaskId());
		
		if(isSuccess){
			this.taskListDataModel.reloadData();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
		    FacesMessage.SEVERITY_INFO,
		    null,
		    MessageUtils.getMessage(MessageConst.INFO_770090, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()))); //TODO
		}
	}
	
	/**
	 * Invoke BPM & Reassign task to selected user
	 * @param users
	 */
	private boolean reassignTask(PmUser user, String taskId)throws EpException{
		log.info("reassignTask");
		boolean isSuccess = false;
		IBpmSO bpmSO = null;
		BpmRequestDTO reqDTO = null;
		BpmResponseDTO responseDTO = null;
		String token = null;
		
		try {

			if(taskId==null){
				throw new EpException("taskId is null.");//TODO
			}
			log.info("BPM taskId******"+taskId);
			log.info("BPM userId*****"+user.getLoginId());
			
			reqDTO = new BpmRequestDTO();
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			token = bpmSO.authenticate("weblogic","welcome1"); //TODO
			
			if(token==null){
				throw new EpException("BPMAuthenticationCtxToken is null. Please login.");//TODO 
			}
		
			log.info("BPM token******"+token);
			
			// set useful info
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			reqDTO.getAssignee().add(new AssigneeVO(user.getLoginId(), "user"));

			//invoke BPM
			responseDTO = bpmSO.reassignTask(reqDTO);
			
			if(responseDTO.isSuccessful()){
				isSuccess = true;
			}else{
				throw new EpException("BPM submission is failed. Unknown problem occurred. Please contact administrator.");//TODO
			}
		}catch(EpBpmException be){
			throw new EpException("EpBpmException: Task Re-assignment failed: {0}.", new String[]{be.getMessage()});//TODO
		}catch(EpException e){
			throw new EpException("EpException: Task Re-assignment failed: {0}.", new String[]{e.getMessage()});//TODO
		} finally {
			bpmSO = null;
		}
		return isSuccess;
	}
	
	/**
	 * refresh the worklist item
	 */
	public void refreshTaskList() {
		this.taskListDataModel.reloadData();
	}
	
	public PagingDataModel<WorklistItemVO> getTaskListDataModel() {
		return taskListDataModel;
	}

	public void setTaskListDataModel(
			PagingDataModel<WorklistItemVO> taskListDataModel) {
		this.taskListDataModel = taskListDataModel;
	}

	private class taskPagingDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public taskPagingDataModel() {
			super("dtTaskAssignResult");
		}
		
		private ISearchResultBean<WorklistItemVO> getWorklistItems(){
			log.info("getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String token = null;
			String loginId = null;
			List<Integer> statusIds = null;
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;
			
			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser()!=null? taskSearchBean.getUser().getLoginId() : null;
				
				if(loginId!=null){
					statusIds = retrieveAvailableStatusId();
//					token = getFacesScopes().getBPMAuthenticationCtxToken();
					
					bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
					token = bpmSO.authenticate("weblogic","welcome1"); //TODO
					  
					reqDTO = new BpmRequestDTO();

					log.info("BPM pageNo******"+getPageNo());
					log.info("BPM pageSize*****"+getPageSize());
					log.info("BPM loginId******"+loginId);
					log.info("BPM statusIds******"+statusIds.size());
					
					
					if(token==null){
						throw new EpException("BPMAuthenticationCtxToken is null. Please login.");//TODO 
					}
					log.info("BPM auth******" + token);
					
					// set useful info
					reqDTO.setCtxToken(token);
					reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
					reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
			        reqDTO.setStatusList(statusIds);
			        reqDTO.setUserId(loginId);

					// Invoke BPM 
					responseDTO = bpmSO.getReassignedItems(reqDTO);
					
					// Set resultBean for UI
					resultBean = new SearchResultBean<WorklistItemVO>();
					resultBean.setTotalRecord(0);
					resultBean.setResultList(null);
					
					if (responseDTO.getWorklistVO() != null) {
						log.info("BPM size******" + responseDTO.getWorklistVO().getWorklistItem().size());
						log.info("BPM total******"+ responseDTO.getWorklistVO().getTotal());
						/*
						if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
							// markup data
							markupWorklistItem(responseDTO.getWorklistVO().getWorklistItem());
						}*/
						resultBean.setTotalRecord(responseDTO.getWorklistVO().getTotal());
						resultBean.setResultList(responseDTO.getWorklistVO().getWorklistItem());
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				bpmSO = null;
			}
			return resultBean;
		}

		/**
		 * Generate meaningful title
		 * @param worklistItem
		 */
		private void markupWorklistItem(List<WorklistItemVO> worklistItem){
			log.info("markupWorklistItem");
			for(WorklistItemVO worklistItemVO : worklistItem) {
				// fl module Purchase Request Initiation Submit PR
				if(worklistItemVO.getProcess().equalsIgnoreCase(FLProcessNameConst.PURCHASE_REQ_INITIATION_PROCESS_NAME) && 
						worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_PR_HT)) {
					if(!worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
						worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale())+" - "+worklistItemVO.getTitle());									
					}
				}
				// s1 module
				if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				}
				if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				}
				if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pid", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				}
			}
		}
		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			log.info("pageChangeListener");
			return getWorklistItems();
		}
		
		private int calculateFinalOffSet(int currentPage) {
			int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;
			return offSet;
		}
	}
	
}