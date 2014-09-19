/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.taskassign;

import java.util.ArrayList;
import java.util.Arrays;
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
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpBpmException;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.logging.LogKey;
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
import org.apache.log4j.MDC;
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
	
	//BPM default filter values
	public static final String VIEW_BY = "ALL";
	public static final String ASSIGNMENT = "ALL";
	public static final String DOC_TYPE = "";
	public static final String STATE = "ASSIGNED";
	
	//Mapper for DDL object
	private static Map<Long, Object> orgTypeMapper;
	
	//List for Paging
	private PagingDataModel<WorklistItemVO> taskListDataModel = new taskPagingDataModel();

	public TaskAssignmentBackingBean(){
		//TODO
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, getFacesScopes().getUserSessionBean().getUserId());
		}
	}
		
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
			this.getManagedBean().setSelectedUser(null);
			this.getManagedBean().setSelectedTask(item);
			
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
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setLanguageCode(languageCode);
		reqDTO.setStatusId(docInt.longValue());
		StatusResDTO resDTO = statusSO.retrieveStatusDescById(reqDTO);

		log.info("docInt:" + docInt);

		return resDTO.getPmStatusDesc() != null ? resDTO.getPmStatusDesc().getStatusName() : "Review";
	}
	
	public void retrieveAvailableUsers(){
		log.info("retrieveAvailableUsers");
		if(this.getManagedBean().getAvailableUsers()==null){
			this.getManagedBean().setAvailableUsers(new ArrayList<PmUser>());	
		}
		
	}
	/**
	 * Reassign task to selected user
	 */
	public void confirmReassignTask(){
		log.info("confirmReassignTask");
		PmUser pmUser = this.getManagedBean().getSelectedUser();
		if(pmUser==null){
			throw new EpException("Please select an user to be re-assigned.");//TODO
		}
		reassignTask(pmUser);
	}
	
	public List<PmUser> retrieveAvaiableUsers(){
		
		List <PmUser> pmUsers = new ArrayList<PmUser>();
//		try {
//			this.getManagedBean().getTaskAssignmentSearchBean().getUser().getUserId();
//			this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().getOrgProfileId();
//			ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();
//			
//			SearchFilterBean bean = new SearchFilterBean();
//			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
//			bean.setSearchField("orgTypeCode.parameterCode");
//			bean.setSearchValue(paramCode);
//			searchBean.getDataFilterBeans().add(bean);
//			
//			UserReqDTO reqDTO = new UserReqDTO();
//			UserResDTO resDTO = null;
//			reqDTO.setUserSearchBean(userSearchBean);
//			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
//			resDTO = userSO.retrieveUserProfileByUserId(reqDTO);
//			
//			if(resDTO.isSuccessful()){
////				resDTO.setUserResultBean(searchResultBean);
//				pmUsers = resDTO.getUserList();
//			}
//				
//		} catch (Exception e) {
////			throw new EpException(MessageConst.ERROR_770053, new String[]{id.toString(), e.getMessage()});
////			TODO
//		}		
		return pmUsers;
	}


	
	/**
	 * Reassign task to selected user
	 * @param users
	 */
	private void reassignTask(PmUser user)throws EpException{
		log.info("reassignTask");
		IBpmSO bpmSO = null;
		BpmRequestDTO reqDTO = null;
		BpmResponseDTO responseDTO = null;
		List<String>users = null;
		String token = null;
		
		try {
			reqDTO = new BpmRequestDTO();
			WorklistItemVO workListItem = this.getManagedBean().getSelectedTask();
			token = getFacesScopes().getBPMAuthenticationCtxToken();
			users = new ArrayList<String>();
			if(token==null){
				throw new EpException("BPMAuthenticationCtxToken is null. Please login.");//TODO 
			}

			users.add(user.getLoginId());
			
			// set useful info
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(workListItem.getTaskId());
//			reqDTO.setAssignee(users);

			//invoke BPM
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			responseDTO = bpmSO.reassignTask(reqDTO);
			
			if(responseDTO.isSuccessful()){
				//TODO
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
			    FacesMessage.SEVERITY_INFO,
			    null,
			    MessageUtils.getMessage(MessageConst.INFO_770090, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}else{
				throw new EpException("BPM submission is failed. Unknown problem occurred. Please contact administrator.");//TODO
			}
		}catch(EpBpmException be){
			throw new EpException("Task Re-assignment failed: {0}.", new String[]{be.getMessage()});//TODO
		}catch(EpException e){
			throw new EpException("Task Re-assignment failed: {0}.", new String[]{e.getMessage()});//TODO
		} finally {
			bpmSO = null;
		}
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
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;
			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
//				loginId = taskSearchBean.user.getLoginId();
				reqDTO = new BpmRequestDTO();
				token = getFacesScopes().getBPMAuthenticationCtxToken();

				if(token==null){
					throw new EpException("BPMAuthenticationCtxToken is null. Please login.");//TODO 
				}
					log.info("BPM auth******" + token);
					log.info("BPM assignment******"+ ASSIGNMENT);
					log.info("BPM pageNo******"+getPageNo());
					log.info("BPM pageSize*****"+getPageSize());
					
				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
				
				// set useful info
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState(STATE); 
//				reqDTO.setUserName(userName);
//				if(!getDocType().isEmpty()){
				  reqDTO.setDocType(DOC_TYPE);
//				}
				reqDTO.setViewBy(VIEW_BY);
				reqDTO.setAssignment(ASSIGNMENT);
				reqDTO.setCtxToken(token);
				
				// Invoke BPM TODO
				responseDTO = bpmSO.getWorklistItems(reqDTO);
				
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