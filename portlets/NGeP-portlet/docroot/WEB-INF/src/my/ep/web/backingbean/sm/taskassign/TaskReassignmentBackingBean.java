/*

 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.sm.taskassign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.pm.access.application.IAccessApplSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sm.common.taskassginment.ISmTaskAssignmentSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constants.BpmConstants;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.dto.pm.access.application.AccessApplResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.dto.sm.common.taskassignment.TaskAssignmentReqDTO;
import my.ep.dto.sm.common.taskassignment.TaskAssignmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmStatusRole;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sm.TaskAssignmentSearchBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.sm.taskassign.TaskReassignmentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.SmTaskReassignmentVO;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * Backing Bean for Task Reasssingment (new)
 * 
 * @author KK LIM
 * @since 15.11.2016
 * 
 */
@ManagedBean(name = "supplierTaskReassignmentBackingBean")
@ViewScoped
public class TaskReassignmentBackingBean extends BaseBackingBean<TaskReassignmentManagedBean> {
	private static Logger log = Logger
			.getLogger(TaskReassignmentBackingBean.class);

	// Navigation Details
	public static final String NAV_TO_SEARCH = "taskReassignment";


	// List for Paging
	private PagingDataModel<WorklistItemVO> userTaskListDataModel = new userTaskListDataModel();	
	private PagingDataModel<WorklistItemVO> groupTaskListDataModel = new groupTaskListDataModel();
	private PagingDataModel<WorklistItemVO> othersTaskListDataModel = new othersTaskListDataModel();
	private PagingDataModel<SmPersonnel> userListDataModel = new userPagingDataModel();

	public void preLoading() throws EpException {
		log.info("preLoading");
		resetTaskAssignSearchBean();
		SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
		getManagedBean().setSmSupplier(supplier);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	/**
	 * reset search bean fields
	 */
	public void resetTaskAssignSearchBean() {
		log.info("resetTaskAssignSearchBean");
		this.getManagedBean().setTaskAssignmentSearchBean(
				new TaskAssignmentSearchBean());
		this.getManagedBean().getTaskAssignmentSearchBean()
				.setExcludeUser(getFacesScopes().getUserSessionBean());
		this.getManagedBean().setTaskLazy(false);
		resetAllTaskList();
	}
	
	/**
	 * To Reset All Task List (BPM)
	 */
	private void resetAllTaskList(){
		this.userTaskListDataModel.resetData();
		this.groupTaskListDataModel.resetData();
		this.othersTaskListDataModel.resetData();
	}
	
	/**
	 * To Reload All Task List (BPM)
	 */
	private void reloadAllTaskList(){
		this.userTaskListDataModel.reloadData();
		this.groupTaskListDataModel.reloadData();
		this.othersTaskListDataModel.reloadData();
	}


	/**
	 * Reset Fields
	 * **/
	public void onChangeUser(BaseEntity entity) {
		this.getManagedBean().getTaskAssignmentSearchBean().setSearched(false);
		this.getManagedBean().getTaskAssignmentSearchBean().setDocType(null);
		this.getManagedBean().setTaskLazy(false);
		resetAllTaskList();
	}

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}



	/**
	 * To construct user condisiton fields for opUserLov
	 * 
	 * @return String
	 */
	public String constructUserConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("smAppl.smSupplier.supplierId^");
		sb.append(getManagedBean().getSmSupplier().getSupplierId());
//		sb.append("|recordStatus^1");
		return sb.toString();
	}

	/**
	 * search task assignment
	 */
	public void searchTaskAssign() {
		log.info("searchDocument - Start");
		if (!(this.getManagedBean().getTaskAssignmentSearchBean().getUser() != null
				&& this.getManagedBean().getTaskAssignmentSearchBean()
						.getUser().getPmUser() != null && this.getManagedBean().getTaskAssignmentSearchBean().getUser().getPmUser().getLoginId().length() > 0)) {
			throw new EpException(MessageConst.ERROR_770092);
		}
		this.getManagedBean().getTaskAssignmentSearchBean().setSearched(true);
		this.getManagedBean().setTaskLazy(true);
		reloadAllTaskList();
		
		//#29822 - START
		this.getManagedBean().setCheckAllUserTasksEnabled(false);
		this.getManagedBean().setCheckAllGroupTasksEnabled(false);
		this.getManagedBean().setCheckAllOthersTasksEnabled(false);
		//#29822 - END
		log.info("searchDocument - End");

	}

	/**
	 * User Task List Data Model
	 * 
	 * @author Ken
	 *
	 */
	private class userTaskListDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public userTaskListDataModel() {
			super("dtTaskAssignResult",getManagedBean().isTaskLazy());
		}

		private ISearchResultBean<WorklistItemVO> getWorklistItems() {
			log.info("getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String loginId = null;
			List<Integer> statusIds = null;
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;

			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser().getPmUser() != null ? taskSearchBean.getUser().getPmUser().getLoginId() : null;
				System.out.println("taskSearchBean.getUser().getPmUser(): " + taskSearchBean.getUser().getPmUser());
				System.out.println("loginId: " + loginId);
				if (loginId != null && loginId.length() > 0) {
					statusIds = retrieveAvailableStatusId();
//					if (statusIds != null && !statusIds.isEmpty()) {
						bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
						reqDTO = new BpmRequestDTO();
						Object ctxToken = bpmSO.authenticate(loginId);

						log.info("BPM pageNo******" + getPageNo());
						log.info("BPM pageSize*****" + getPageSize());
						log.info("BPM loginId******" + loginId);
						log.info("BPM statusIds******" + statusIds.size());

						reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
						reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
						reqDTO.setStatusList(statusIds);
						reqDTO.setUserId(loginId);
						reqDTO.setCtxToken(ctxToken);
						List<String> docNumberList = new ArrayList<String>();
						if (StringUtils.isNotBlank(getManagedBean().getTaskAssignmentSearchBean().getDocNo())) {
							docNumberList.add(getManagedBean().getTaskAssignmentSearchBean().getDocNo());
							reqDTO.setDocNumberList(docNumberList);
						}

						if (getManagedBean().getTaskAssignmentSearchBean().getDocType() != null) {
							reqDTO.setDocType(getManagedBean().getTaskAssignmentSearchBean().getDocType());
						}
						
						reqDTO.setState(BpmConstants.STATE_ASSIGNED);
						reqDTO.setAssignment(BpmConstants.ASSIGNMENT_MY);
						responseDTO = bpmSO.getReassignedItems(reqDTO);


						// Set resultBean for UI
						resultBean = new SearchResultBean<WorklistItemVO>();
						resultBean.setTotalRecord(0);
						resultBean.setResultList(null);

						if (responseDTO.getWorklistVO() != null) {
							log.info("BPM size******"+ responseDTO.getWorklistVO().getWorklistItem().size());
							log.info("BPM total******"+ responseDTO.getWorklistVO().getTotal());
							/*
							 * if(responseDTO.getWorklistVO().getWorklistItem()!=
							 * null) { // markup data
							 * markupWorklistItem(responseDTO
							 * .getWorklistVO().getWorklistItem()); }
							 */
							resultBean.setTotalRecord(responseDTO.getWorklistVO().getTotal());
							resultBean.setResultList(responseDTO.getWorklistVO().getWorklistItem());
							
							//#29822 - START
							if(!responseDTO.getWorklistVO().getWorklistItem().isEmpty() && responseDTO.getWorklistVO().getWorklistItem().size() > 0){
								getManagedBean().setAllUserTasks(new ArrayList <WorklistItemVO>());
								getManagedBean().setCheckAllUserTasksEnabled(true);
								for(WorklistItemVO worklist : responseDTO.getWorklistVO().getWorklistItem()){
									getManagedBean().getAllUserTasks().add(worklist);
								}
							}else{
								getManagedBean().setCheckAllUserTasksEnabled(false);
							}
							//29822 - END
						}
//					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bpmSO = null;
			}
			return resultBean;
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(ISearchBean<WorklistItemVO> searchBean) {
			log.info("pageChangeListener");
			return getWorklistItems();
		}

		private int calculateFinalOffSet(int currentPage) {
			int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;
			return offSet;
		}
	}
	
	/**
	 * Group Task List Data Model (available to claim in task list screen)
	 * 
	 * @author Ken
	 *
	 */
	private class groupTaskListDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public groupTaskListDataModel() {
			super("dtGroupTaskAssignResult",getManagedBean().isTaskLazy());
		}

		private ISearchResultBean<WorklistItemVO> getWorklistItems() {
			log.info("groupTaskListDataModel - getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String loginId = null;
			List<Integer> statusIds = null;
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;

			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser().getPmUser() != null ? taskSearchBean.getUser().getPmUser().getLoginId() : null;
				if (loginId != null && loginId.length() > 0) {
					statusIds = retrieveAvailableStatusId();
					if (statusIds != null && !statusIds.isEmpty()) {
						bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
						reqDTO = new BpmRequestDTO();
						Object ctxToken = bpmSO.authenticate(loginId);

						log.info("BPM pageNo******" + getPageNo());
						log.info("BPM pageSize*****" + getPageSize());
						log.info("BPM loginId******" + loginId);
						log.info("BPM statusIds******" + statusIds.size());

						reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
						reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
						reqDTO.setStatusList(statusIds);
						reqDTO.setUserId(loginId);
						reqDTO.setCtxToken(ctxToken);
						List<String> docNumberList = new ArrayList<String>();
						if (StringUtils.isNotBlank(getManagedBean().getTaskAssignmentSearchBean().getDocNo())) {
							docNumberList.add(getManagedBean().getTaskAssignmentSearchBean().getDocNo());
							reqDTO.setDocNumberList(docNumberList);
						}

						if (getManagedBean().getTaskAssignmentSearchBean().getDocType() != null) {
							reqDTO.setDocType(getManagedBean().getTaskAssignmentSearchBean().getDocType());
						}
						
						reqDTO.setState(BpmConstants.STATE_ASSIGNED);
						reqDTO.setAssignment(BpmConstants.ASSIGNMENT_GROUP);
						reqDTO.setIsClaimed(false);
						responseDTO = bpmSO.getReassignedItems(reqDTO);

						// Set resultBean for UI
						resultBean = new SearchResultBean<WorklistItemVO>();
						resultBean.setTotalRecord(0);
						resultBean.setResultList(null);

						if (responseDTO.getWorklistVO() != null) {
							log.info("BPM size******"+ responseDTO.getWorklistVO().getWorklistItem().size());
							log.info("BPM total******"+ responseDTO.getWorklistVO().getTotal());
							/*
							 * if(responseDTO.getWorklistVO().getWorklistItem()!=
							 * null) { // markup data
							 * markupWorklistItem(responseDTO
							 * .getWorklistVO().getWorklistItem()); }
							 */
							resultBean.setTotalRecord(responseDTO.getWorklistVO().getTotal());
							resultBean.setResultList(responseDTO.getWorklistVO().getWorklistItem());
							
							//#29822 - START
							if(!responseDTO.getWorklistVO().getWorklistItem().isEmpty() && responseDTO.getWorklistVO().getWorklistItem().size() > 0){
								getManagedBean().setAllUserTasks(new ArrayList <WorklistItemVO>());
								getManagedBean().setCheckAllGroupTasksEnabled(true);
								for(WorklistItemVO worklist : responseDTO.getWorklistVO().getWorklistItem()){
									getManagedBean().getAllGroupTasks().add(worklist);
								}
							}else{
								getManagedBean().setCheckAllGroupTasksEnabled(false);
							}
							//29822 - END
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bpmSO = null;
			}
			return resultBean;
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
	
	/**
	 * Others Task List Data Model (group task has been claimed by other user)
	 * 
	 * @author Ken
	 *
	 */
	private class othersTaskListDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public othersTaskListDataModel() {
			super("dtOthersTaskAssignResult",getManagedBean().isTaskLazy());
		}

		private ISearchResultBean<WorklistItemVO> getWorklistItems() {
			log.info("othersTaskListDataModel - getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String loginId = null;
			List<Integer> statusIds = null;
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;

			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser().getPmUser() != null ? taskSearchBean
						.getUser().getPmUser().getLoginId() : null;
				if (loginId != null && loginId.length() > 0) {
					statusIds = retrieveAvailableStatusId();
					if (statusIds != null && !statusIds.isEmpty()) {
						bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
								.getServiceObject(IBpmSO.class);
						reqDTO = new BpmRequestDTO();
						Object ctxToken = bpmSO.authenticate(loginId);

						log.info("BPM pageNo******" + getPageNo());
						log.info("BPM pageSize*****" + getPageSize());
						log.info("BPM loginId******" + loginId);
						log.info("BPM statusIds******" + statusIds.size());

						reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
						reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
						reqDTO.setStatusList(statusIds);
						reqDTO.setUserId(loginId);
						reqDTO.setCtxToken(ctxToken);
						List<String> docNumberList = new ArrayList<String>();
						if (StringUtils.isNotBlank(getManagedBean()
								.getTaskAssignmentSearchBean().getDocNo())) {
							docNumberList.add(getManagedBean()
									.getTaskAssignmentSearchBean().getDocNo());
							reqDTO.setDocNumberList(docNumberList);
						}

						if (getManagedBean().getTaskAssignmentSearchBean().getDocType() != null) {
							reqDTO.setDocType(getManagedBean().getTaskAssignmentSearchBean().getDocType());
						}
						
						reqDTO.setState(BpmConstants.STATE_ASSIGNED);
						reqDTO.setAssignment(BpmConstants.ASSIGNMENT_ALL);
						responseDTO = bpmSO.getGroupByOthersItems(reqDTO);
						
						// Set resultBean for UI
						resultBean = new SearchResultBean<WorklistItemVO>();
						resultBean.setTotalRecord(0);
						resultBean.setResultList(null);

						if (responseDTO.getWorklistVO() != null) {
							log.info("BPM size******"
									+ responseDTO.getWorklistVO()
											.getWorklistItem().size());
							log.info("BPM total******"
									+ responseDTO.getWorklistVO().getTotal());
							/*
							 * if(responseDTO.getWorklistVO().getWorklistItem()!=
							 * null) { // markup data
							 * markupWorklistItem(responseDTO
							 * .getWorklistVO().getWorklistItem()); }
							 */
							resultBean.setTotalRecord(responseDTO
									.getWorklistVO().getTotal());
							resultBean.setResultList(responseDTO
									.getWorklistVO().getWorklistItem());
							
							//#29822 - START
							if(!responseDTO.getWorklistVO().getWorklistItem().isEmpty() && responseDTO.getWorklistVO().getWorklistItem().size() > 0){
								getManagedBean().setAllOthersTasks(new ArrayList <WorklistItemVO>());
								getManagedBean().setCheckAllOthersTasksEnabled(true);
								for(WorklistItemVO worklist : responseDTO.getWorklistVO().getWorklistItem()){
									getManagedBean().getAllOthersTasks().add(worklist);
								}
							}else{
								getManagedBean().setCheckAllOthersTasksEnabled(false);
							}
							//#29822 - END
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bpmSO = null;
			}
			return resultBean;
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

	/**
	 * retrieve status id list for BPM
	 * 
	 * @return List<Integer>
	 */
	private List<Integer> retrieveAvailableStatusId() {
		log.info("retrieveAvailableStatusId");
		List<Integer> statusIds = new ArrayList<Integer>();
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();
			if (getManagedBean().getTaskAssignmentSearchBean()
					.getUser() != null
					&& getManagedBean().getTaskAssignmentSearchBean()
							.getUser().getEpRole() != null) {
				reqDTO = new StatusReqDTO();
				reqDTO.setRoleCodeList(new ArrayList<String>());
				reqDTO.getRoleCodeList().add(getManagedBean().getTaskAssignmentSearchBean().getUser().getEpRole());
			}

			if (reqDTO.getRoleCodeList() != null
					&& !reqDTO.getRoleCodeList().isEmpty()) {
				reqDTO.setReassignable(true);
				IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
				StatusResDTO resDTO = statusSO.retrieveStatusRoleListByRoleCode(reqDTO);
				if (resDTO.isSuccessful()) {
					for (int i = 0; i < resDTO.getStatusRoleList().size(); i++) {
						statusIds.add(resDTO.getStatusRoleList().get(i)
								.getPmStatus().getStatusId().intValue());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770094,
					new String[] { e.getMessage() });
		}
		return statusIds;
	}

	/**
	 * Get meaningful application status message
	 * 
	 * @param docInt
	 * @return String
	 */
	public String applicationStatus(Integer docInt) {
		log.info("applicationStatus");
		log.info("docInt:" + docInt);
		if (docInt != null) {
			PmStatusDesc desc = StatusHelper.getInstance().getStatusDescById(docInt.longValue());
			return desc.getStatusName();
		} else {
			return "";
		}
	}

	/**
	 * Redirect to Task Re-assignment Details
	 * 
	 * @param item
	 *            WorklistItemVO
	 * @return xhtml
	 */
	public void redirectToTaskReassign(WorklistItemVO item) {
		log.info("redirectToTaskReassign - Start");
		try {
			this.getManagedBean().setSelectedUser(null);
			this.getManagedBean().setSelectedTask(item);
			resetAvailableUser();
			
			//#29822 - START
			log.info("Selected Task - " + this.getManagedBean().getSelectedTask().getDocNumber());
			this.getManagedBean().setBulkReassign(false);
			this.getManagedBean().setSingleReassign(true);
			//#29822 - END
		} catch (Exception e) {
			throw e;
		}
		log.info("redirectToTaskReassign - End");
		RequestContext.getCurrentInstance().execute("dlSuppTaskReassignment.show()");
	}


	/**
	 * Reassign task to selected user
	 */
	public void confirmReassignTask() {
		log.info("confirmReassignTask");

		//#29822 - START
		log.info("[#29822] isBulkReassign? " + getManagedBean().isBulkReassign());
		
		List<WorklistItemVO> tasks = null;	
		SmPersonnel smUser = this.getManagedBean().getSelectedUser();
		ISmTaskAssignmentSO taskAssignmentSO = null;
		SmTaskReassignmentVO vo = null;
		TaskAssignmentReqDTO reqDTO = null;
		TaskAssignmentResDTO resDTO = null;
		StringBuilder stb = null;
		
		if(getManagedBean().isSingleReassign()){
			tasks = new ArrayList<>();
			tasks.add(getManagedBean().getSelectedTask());
		}
		else if(getManagedBean().isBulkReassign()) {
			tasks = new ArrayList<>();
			tasks.addAll(getManagedBean().getSelectedTasksToBulkReassign());					
		}
		
		if (tasks == null) {
			throw new EpException(MessageConst.ERROR_770097);
		}
		
		List<String> assigneeUsers = new ArrayList<String>();

		if (smUser == null) {
			throw new EpException(MessageConst.ERROR_770096);
		}
		
		// replace with the new assignee.
		assigneeUsers.add(smUser.getPmUser().getLoginId());
		

		try {
			for(WorklistItemVO task : tasks){
				reqDTO = new TaskAssignmentReqDTO();
								
				log.info("[#29822] WorklistItemVO DocNo: " + task.getDocNumber());
				
				if(getManagedBean().isSingleReassign()){					
					vo = getManagedBean().getSmTaskReassignmentVO();
				}				
				else if(getManagedBean().isBulkReassign()) {
					for(SmTaskReassignmentVO smtaskvo : getManagedBean().getTasksReassignmentVO()) {
						log.info("[#29822] SmTaskReassignmentVO DocNo: " + smtaskvo.getDocNo());
						if(task.getDocNumber().equals(smtaskvo.getDocNo())) {
							vo = smtaskvo;
							break;
						}						
					}
				}
				
				reqDTO.setTaskId(task.getTaskId());
				reqDTO.setUser(smUser.getPmUser());
				reqDTO.setUserIdList(assigneeUsers);
				reqDTO.setDocNumber(StringUtils.isNotBlank(vo.getDocNo())? vo.getDocNo() : task.getDocNumber());
				reqDTO.setDocId(vo.getDocId() != null ? vo.getDocId().intValue() : task.getDocId());		
				reqDTO.setDocType(vo.getDocType() != null ? vo.getDocType() : task.getDocType());
				reqDTO.setGroupDocId(vo.getGroupDocId() != null ? vo.getGroupDocId() : null);
				reqDTO.setGroupDocType(StringUtils.isNotBlank(vo.getGroupDocType()) ? vo.getGroupDocType() : null);
				reqDTO.setDocStatus(task.getDocStatus());
				reqDTO.setRoleCode(SmCommonUtil.getUserRoleValue());
				reqDTO.setActionedByUser(getFacesScopes().getUserSessionBean().getUserId());
				reqDTO.setFromUser(getManagedBean().getTaskAssignmentSearchBean().getUser().getPmUser());
				reqDTO.setUserLocale(getFacesScopes().getUserLocale());
				
				//For Tracking Diary List (CT)
				if(vo.getTrackingDiaryList() != null && !vo.getTrackingDiaryList().isEmpty()){
					reqDTO.setTrackingDiaryList(vo.getTrackingDiaryList());
				}
				
				taskAssignmentSO = (ISmTaskAssignmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISmTaskAssignmentSO.class);
				resDTO = taskAssignmentSO.reassignTask(reqDTO);
				
				if (resDTO != null && resDTO.isSuccessful()) {
					if(stb != null) {
						stb.append(", ");
						stb.append(task.getDocNumber());
					} else {
						stb = new StringBuilder(task.getDocNumber());
					}
				}
			}
			log.info("[#29822] Successful reassign task: " + stb.toString());
			
			// isSuccess = reassignTask(pmUser, task);
			if (resDTO != null && resDTO.isSuccessful()) {
				RequestContext.getCurrentInstance().execute("dlSuppTaskReassignment.hide()");
				reloadAllTaskList();
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
						FacesMessage.SEVERITY_INFO, MessageConst.INFO_770104, 
						new String[] { stb.toString() });				
			}
		//#29822 - END
		} finally {
			taskAssignmentSO = null;
		}

	}
	
	/**
	 * Action after docType has been selected
	 * 
	 * @param entity
	 */
	public void onChangeDocType(){
		this.getManagedBean().getTaskAssignmentSearchBean()
			.setSearched(false);
		this.getManagedBean().setTaskLazy(false);
		resetAllTaskList();
	}
	
	
	/**
	 * Remove group task/other task from user.
	 */
	public void removeReassignTask() {
		log.info("removeReassignTask");

		String claimedBy = null;
		WorklistItemVO task = this.getManagedBean().getSelectedTask();
		ISmTaskAssignmentSO taskAssignmentSO = null;
		int reassignWorkListType = getManagedBean().getTaskType();
		
		if (task == null) {
			throw new EpException(MessageConst.ERROR_770097);
		}
		List<String> assigneeUsers = task.getAssignees();
		if (assigneeUsers !=null && !assigneeUsers.isEmpty()) {
			// remove the current user.
			if (assigneeUsers.contains(getManagedBean()
					.getTaskAssignmentSearchBean().getUser().getPmUser().getLoginId())) {
				assigneeUsers.remove(getManagedBean()
							.getTaskAssignmentSearchBean().getUser().getPmUser()
							.getLoginId());
			
			}
			if(assigneeUsers != null && assigneeUsers.size() > 1 && ProfileManagementConst.REASSIGN_WORKLIST_TYPE_OTHERS == reassignWorkListType){
				claimedBy = task.getAcquiredBy();
			}
		}

		try {
			TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
			reqDTO.setTaskId(task.getTaskId());
			reqDTO.setUserIdList(assigneeUsers);
			reqDTO.setClaimedBy(claimedBy);

			taskAssignmentSO = (ISmTaskAssignmentSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISmTaskAssignmentSO.class);
			TaskAssignmentResDTO resDTO = taskAssignmentSO.removeGroupOrOthersTask(reqDTO);

			// isSuccess = reassignTask(pmUser, task);
			if (resDTO != null && resDTO.isSuccessful()) {
				reloadAllTaskList();
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
						FacesMessage.SEVERITY_INFO, MessageConst.INFO_770105, null);
			}
		} finally {
			taskAssignmentSO = null;
		}

	}

	/**
	 * @return the userTaskListDataModel
	 */
	public PagingDataModel<WorklistItemVO> getUserTaskListDataModel() {
		return userTaskListDataModel;
	}

	/**
	 * @param userTaskListDataModel the userTaskListDataModel to set
	 */
	public void setUserTaskListDataModel(
			PagingDataModel<WorklistItemVO> userTaskListDataModel) {
		this.userTaskListDataModel = userTaskListDataModel;
	}

	/**
	 * @return the groupTaskListDataModel
	 */
	public PagingDataModel<WorklistItemVO> getGroupTaskListDataModel() {
		return groupTaskListDataModel;
	}

	/**
	 * @param groupTaskListDataModel the groupTaskListDataModel to set
	 */
	public void setGroupTaskListDataModel(
			PagingDataModel<WorklistItemVO> groupTaskListDataModel) {
		this.groupTaskListDataModel = groupTaskListDataModel;
	}

	/**
	 * @return the othersTaskListDataModel
	 */
	public PagingDataModel<WorklistItemVO> getOthersTaskListDataModel() {
		return othersTaskListDataModel;
	}

	/**
	 * @param othersTaskListDataModel the othersTaskListDataModel to set
	 */
	public void setOthersTaskListDataModel(
			PagingDataModel<WorklistItemVO> othersTaskListDataModel) {
		this.othersTaskListDataModel = othersTaskListDataModel;
	}
	
	public void redirectToTaskReassignment(PmOrgProfile orgProfile, PmUser user) {		
		log.info("redirectToTaskReassignment");
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());		
		resetTaskAssignSearchBean();
		
		this.getManagedBean().getTaskAssignmentSearchBean()
			.setOrgType(orgProfile.getOrgTypeCode());
		this.getManagedBean().getTaskAssignmentSearchBean()
			.getUser().setPmUser(user);
		this.getManagedBean().setOrgProfileRequired(false);
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/taskReassignment/taskReassignment.xhtml");

		
	}
	
	public void redirectToPmUserPage(){
		log.info("this.getManagedBean().getBackURL()"+this.getManagedBean().getBackURL());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, this.getManagedBean().getBackURL()); 

	}
	
	public List<PmParameterDesc> retrieveParameterDesc(){
		if(getManagedBean().getTaskAssignmentSearchBean().getUser() != null && getManagedBean().getTaskAssignmentSearchBean().getUser().getEpRole() == null) {
			return null;
		}
		
		IStatusSO statusSO = null;
		StatusReqDTO reqDTO = null;
		if(getManagedBean().getTaskAssignmentSearchBean().getUser() != null){
		reqDTO = new StatusReqDTO();
		reqDTO.setRoleCodeList(new ArrayList<String>());
		reqDTO.getRoleCodeList().add(getManagedBean().getTaskAssignmentSearchBean().getUser().getEpRole());

		}
		if (reqDTO != null) {
			reqDTO.setReassignable(true);
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusResDTO resDTO = statusSO
					.retrieveStatusRoleListByRoleCode(reqDTO);

			if (resDTO.isSuccessful()) {
				List<String> tempDocTypeList = new ArrayList<String>();
				for (PmStatusRole statusRole : resDTO.getStatusRoleList()) {
					if(!tempDocTypeList.contains(statusRole.getPmStatus().getDocType())){
						tempDocTypeList.add(statusRole.getPmStatus().getDocType());
					}
					
				}
				
				ISearchBean<PmParameterDesc> searchBean = createSearchBean(PmParameterDesc.class, "trDocTypeDD");
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmParameter.parameterCode",
								SearchOperator.OPERATOR_IN.value(),
								tempDocTypeList));
				List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.DOCUMENT_TYPES, searchBean);
				for(PmParameterDesc parameterDesc :  parameterDescs){
					if(parameterDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
						parameterDesc.setCodeName(ResourceBundleUtils.getMessage("lbl.rpt.drpdown.contractOrderlbl",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
					}
				}
				return parameterDescs;
			}
			
		}
		return null;
		
	}
	
	/**
	 * Available user data model (to confirm which user to be assignee)
	 * 
	 * @author KK LIM
	 *
	 */
	private class userPagingDataModel extends PagingDataModel<SmPersonnel> {

		private static final long serialVersionUID = 1L;

		public userPagingDataModel() {
			super("dtTaskReassign");
		}

		@Override
		protected ISearchResultBean<SmPersonnel> pageChangeListener(ISearchBean<SmPersonnel> searchBean) {
			log.info("generateAvailableUsers");
			
			//#29822 - START
			List<WorklistItemVO> tasks = new ArrayList<>();
			ISearchResultBean<SmPersonnel> resultBean = null;
			HashMap<String, SmPersonnel> users = new HashMap<>();
			String capitalizeUserName;
			SmTaskReassignmentVO vo = null;
			List<String> roleList = new ArrayList<String>();
			try {
				log.info("[#29822] isBulkReassign? " + getManagedBean().isBulkReassign());
				if(getManagedBean().isSingleReassign()){
					tasks.add(getManagedBean().getSelectedTask());
					getManagedBean().setTasksReassignmentVO(null);
				}
				else if(getManagedBean().isBulkReassign()){
					tasks.addAll(getManagedBean().getSelectedTasksToBulkReassign());
					getManagedBean().setTasksReassignmentVO(null);
				}
				
				for(WorklistItemVO task : tasks){
					if(getManagedBean().isBulkReassign()){
						vo = null;
					}
					
					PmStatus status = PmCommonUtil.getPmStatusById(task.getDocStatus().longValue());
					
					if(status != null){
						int reassignType = status.getPmStatusTask().getReasssignType() != null 
								&& status.getPmStatusTask().getReasssignType() != null
								? status.getPmStatusTask().getReasssignType() : 0;
						String moduleCode = status.getModuleCode();
						
						log.info(":::pageChangeListener() reassignType: "+ reassignType +", moduleCode: "+ moduleCode +".");
						log.info("[#29822] docType: "+ task.getDocType() +", docNo: "+ task.getDocNumber() + ", docStatus: " + task.getDocStatus().longValue() + ", taskName: " + task.getTaskName());		
						
						if(vo == null){
							if (ModuleCodeConst.CONTRACT_MANAGEMENT.equalsIgnoreCase(moduleCode)) {
								vo = retrieveCtTaskReassignmentDetails(task);
							} else if (ModuleCodeConst.SUPPLIER_SELECTION.equalsIgnoreCase(moduleCode)) {
								vo = retrieveSourcingTaskReassignmentDetail(task);
							} else if (ModuleCodeConst.ORDER_FULFILLMENT.equalsIgnoreCase(moduleCode)) {
								vo = retrieveFLTaskReassignmentDetail(task);
							}else if (ModuleCodeConst.PROCUREMENT_PLAN.equalsIgnoreCase(moduleCode)) {
								vo = retrievePpTaskReassignmentDetail(task);
							}else if(ModuleCodeConst.CATALOGUE_MANAGEMENT.equalsIgnoreCase(moduleCode)) {
								vo = retrieveCMTaskReassignmentDetail(task);
							}else if(ModuleCodeConst.PROFILE_MANAGEMENT.equalsIgnoreCase(moduleCode)) {
								vo = retrievePMTaskReassignmentDetail(task);
							}else if(ModuleCodeConst.SUPPLIER_MANAGEMENT.equalsIgnoreCase(moduleCode)) {
								vo = new SmTaskReassignmentVO();
							}
						}
						
						if(vo != null){
							vo.setStatus(status);
							if(getManagedBean().isSingleReassign()){
								getManagedBean().setSmTaskReassignmentVO(vo);
							}
							else if(getManagedBean().isBulkReassign()){
								vo.setDocNo(task.getDocNumber());
								if(getManagedBean().getTasksReassignmentVO() != null) {
									log.info("[#29822] add vo...");
									getManagedBean().getTasksReassignmentVO().add(vo);
								} else {
									log.info("[#29822] declare new list and add vo...");
									getManagedBean().setTasksReassignmentVO(new ArrayList<SmTaskReassignmentVO>());
									getManagedBean().getTasksReassignmentVO().add(vo);
								}
							}
							log.info("[#29822] Role list [" + StringUtils.join(vo.getRoleList(), "|")+ "]");
							
							// 99 - MIXTURE(Sub Reassign Type)
							if(reassignType == ProfileManagementConst.REASSIGN_TYPE_MIXTURE){
								roleList = vo.getRoleList();
								if(!roleList.isEmpty()){
									PmStatusRole statusRole = PmCommonUtil.retrieveStatusRoleByStatusIdAndRoleCode(task.getDocStatus().longValue(), roleList.get(0));
									if(statusRole != null && statusRole.getReasssignType() !=null){
										reassignType = statusRole.getReasssignType();
										log.info("[#29822] generateUserResultBean for reassignType:" + reassignType);
										resultBean = generateUserResultBean(status , searchBean, task,reassignType,roleList,vo);
									}
								}
							}else{
								log.info("[#29822] generateUserResultBean for reassignType:" + reassignType);
								resultBean = generateUserResultBean(status, searchBean, task,reassignType,roleList,vo);
							}
							
							if(resultBean != null && resultBean.getResultList() != null){
								String currentUser = getManagedBean().getTaskAssignmentSearchBean().getUser() != null ?
										  getManagedBean().getTaskAssignmentSearchBean().getUser().getPmUser().getLoginId() : 
										  getManagedBean().getSelectedReassignee().get(0);
								log.info("[#29822] currentUser is:" + currentUser);	
								
								for (SmPersonnel getResultList : resultBean.getResultList()) {
							    	if (!getResultList.getPmUser().getLoginId().equals(currentUser)) {
							    		if(users != null && users.size() > 0) {
							    			if(!users.containsKey(getResultList.getPmUser().getLoginId()))
							    				log.info("[#29822] add next user: " + getResultList.getPmUser().getLoginId());
							    				users.put(getResultList.getPmUser().getLoginId(), getResultList);
							    		} else {
							    			log.info("[#29822] add 1st user: " + getResultList.getPmUser().getLoginId());
						    				users.put(getResultList.getPmUser().getLoginId(), getResultList);
							    		}
							    	}
							    }
							}
						}
					}
				}
				
			if(resultBean != null && resultBean.getResultList() != null){
				log.info("[#29822] add new list of user...");
			    resultBean.getResultList().clear();
				resultBean.getResultList().addAll(new ArrayList<SmPersonnel>(users.values()));
				
				for(SmPersonnel user : resultBean.getResultList()){
					log.info("[#29822] capitalize user name " + user.getIdentificationNo() + "(" + user.getPmUser().getUserName() + ")");
					capitalizeUserName = WordUtils.capitalizeFully(user.getName());
					user.setName(capitalizeUserName);
				}
				log.info("getTotalRecord:: "+resultBean.getTotalRecord());
				return resultBean;
			}
		//#29822 - END
		}catch(Exception e){
			throw e;
		}
			return null;
		}
	}
	
	/**
	 * To Retrieve Sourcing Module Task Reassignment Details
	 * 
	 * @param task
	 * @return SmTaskReassignmentVO
	 */
	private SmTaskReassignmentVO retrieveSourcingTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrieveSourcingTaskReassignmentDetail");
		ISourcingIntegrationSO scIntegrationSO = null;
		SmTaskReassignmentVO vo = new SmTaskReassignmentVO();
		try {
			SourcingIntegrationReqDTO reqDTO = new SourcingIntegrationReqDTO();
			scIntegrationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					ISourcingIntegrationSO.class);
			reqDTO.setDocType(task.getDocType());
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setStatusId(task.getDocStatus().longValue());
			reqDTO.setTaskName(task.getTaskName());
			SourcingIntegrationResDTO resDTO = scIntegrationSO.retrieveTaskReassignmentDetail(reqDTO);
			if(resDTO != null){
				vo.setGroupDocId(resDTO.getGroupDocId());
				vo.setGroupDocType(resDTO.getGroupDocType());
				vo.setDocId(resDTO.getDocId());
				vo.setDocType(resDTO.getDocType());
//				vo.setOrgProfileId(resDTO.getOrgProfileId());
				vo.setRoleList(new ArrayList<String>(Arrays.asList(resDTO.getRoleCode())));
				vo.setUserGroupId(resDTO.getUserGroupId());
				
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Sourcing Module.");
		} finally {
			scIntegrationSO = null;
		}
		return vo;
	}

	/**
	 * To Retrieve Procurement Plan Task Reassignment Details
	 * 
	 * @param task
	 * @return SmTaskReassignmentVO
	 */
	private SmTaskReassignmentVO retrievePpTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrievePpTaskReassignmentDetail");
		IProcurementPlanSO procurementSO= null;
		SmTaskReassignmentVO vo = new SmTaskReassignmentVO();
		try {
			ProcurementPlanReqDTO reqDTO  = new ProcurementPlanReqDTO();
			procurementSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROCUREMENT_PLAN,
					IProcurementPlanSO.class);
			String docType = ParameterConst.DOCTYPE_PLAN_EXPENDITURE.equals(task.getDocType()) ? ParameterConst.PLAN_TYPE_EXPENDITURE: ParameterConst.PLAN_TYPE_PROCUREMENT;
			reqDTO.setDocType(docType);
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setStatusId(task.getDocStatus().longValue());
			ProcurementPlanResDTO resDTO = procurementSO.retrieveTaskReassignmentDetail(reqDTO);
			if (resDTO != null && resDTO.getOrgProfileId() != null) {
//				vo.setOrgProfileId(resDTO.getOrgProfileId());
				vo.setDocId(resDTO.getDocId());
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Procurement Plan Module.");
		} finally {
			procurementSO = null;
		}
		return vo;
	}
	
	/**
	 * To Retrieve Contract Management Task Reassignment Details
	 * 
	 * @param task
	 * @return SmTaskReassignmentVO
	 */
	private SmTaskReassignmentVO retrieveCtTaskReassignmentDetails(WorklistItemVO task) {
		log.info("retrieveCtTaskReassignmentDetails");
		SmTaskReassignmentVO vo = new SmTaskReassignmentVO();
		IContractIntegrationSO ctIntegrationSO = null;
		try {
			ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();
			ctIntegrationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT,
					IContractIntegrationSO.class);
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setDocType(task.getDocType());
			ContractIntegrationResDTO resDTO = ctIntegrationSO
					.retrieveTaskAssignmentRole(reqDTO);
			if (resDTO.isSuccessful()) {
				vo.setRoleList(resDTO.getRoleCodeList());
				vo.setGroupDocId(resDTO.getGroupDocId());
				vo.setGroupDocType(resDTO.getGroupDocType());
				vo.setDocId(resDTO.getDocId());
				vo.setDocNo(resDTO.getDocNo());
				vo.setDocType(resDTO.getDocType());
				//For Tracking Diary 
				if(resDTO.getCircularList() != null && !resDTO.getCircularList().isEmpty()){
					vo.setTrackingDiaryList(resDTO.getCircularList());
				}
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Contract Management Module.");
		} finally {
			ctIntegrationSO = null;
		}
		return vo;
	}

	/**
	 * To Retrieve Order Fulfilment Task Reassignment Details
	 * 
	 * @param task
	 * @return SmTaskReassignmentVO
	 */
	private SmTaskReassignmentVO retrieveFLTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrieveFLTaskReassignmentDetail");
		IFulfilmentSO fulfilmentSO = null;
		SmTaskReassignmentVO vo = new SmTaskReassignmentVO();
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			fulfilmentSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.ORDER_FULFILLMENT,
					IFulfilmentSO.class);
			reqDTO.setDocType(task.getDocType());
			reqDTO.setFlDocNo(task.getDocNumber());
			reqDTO.setStatusId(task.getDocStatus().longValue());
			reqDTO.setTaskName(task.getTaskName());
			reqDTO.setTaskId(task.getTaskId());
			if(getManagedBean().getTaskAssignmentSearchBean() != null &&
					getManagedBean().getTaskAssignmentSearchBean().getUser().getPmUser() != null &&
					getManagedBean().getTaskAssignmentSearchBean().getUser().getPmUser().getLoginId() != null){
				reqDTO.setUserLoginId(getManagedBean().getTaskAssignmentSearchBean().getUser().getPmUser().getLoginId());
			}
			
			FulfilmentResDTO resDTO = fulfilmentSO.retrieveTaskReassignmentDetail(reqDTO);
			if(resDTO != null){
				vo.setOrderAmount(resDTO.getOrderAmount());
				vo.setApprovalLimit(resDTO.getOrderAmount());
//				vo.setOrgProfileId(resDTO.getOrgProfileId());
				vo.setDocId(resDTO.getDocId());
				vo.setDocNo(resDTO.getDocNo());
				vo.setDocType(resDTO.getDocType());
				vo.setUserGroupId(resDTO.getUserGroupId());
				vo.setAddressId(resDTO.getAddressId());
				vo.setSupplier(resDTO.isSupplier());
				vo.setGroupDocId(resDTO.getGroupDocId());
				vo.setGroupDocType(resDTO.getGroupDocType());
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Fulfilment Module.");
		} finally {
			fulfilmentSO = null;
		}
		return vo;
	}
	
	/**
	 * To Retrieve Catalogue Management Task Reassignment Details
	 * 
	 * @param task
	 * @return SmTaskReassignmentVO
	 */
	private SmTaskReassignmentVO retrieveCMTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrieveCMTaskReassignmentDetail");
		ICodificationSO codificationSO = null;
		SmTaskReassignmentVO vo = new SmTaskReassignmentVO();
		try {
			CodificationReqDTO reqDTO = new CodificationReqDTO();
			codificationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CATALOGUE_MANAGEMENT,
					ICodificationSO.class);
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setDocType(task.getDocType());
			
			CodificationResDTO resDTO = codificationSO.findDocIdByDocNoAndDocType(reqDTO);
			if(resDTO != null){
				vo.setDocId(resDTO.getDocId());
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Catalogue Management Module.");
		} finally {
			codificationSO = null;
		}
		return vo;
	}
	
	/**
	 * To Retrieve Profile Management Task Reassignment Details
	 * 
	 * @param task
	 * @return SmTaskReassignmentVO
	 */
	private SmTaskReassignmentVO retrievePMTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrievePMTaskReassignmentDetail");
		SmTaskReassignmentVO vo = new SmTaskReassignmentVO();
		//it is for access application only
		if(task.getDocType() != null && 
				(ParameterConst.DOCTYPE_COMPANY_PROFILE.equals(task.getDocType()) || ParameterConst.DOCTYPE_MARKET_RESEARCH.equals(task.getDocType()))){
			IAccessApplSO accessApplSO = null;
			try{
				accessApplSO = (IAccessApplSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IAccessApplSO.class);
				
				AccessApplReqDTO reqDTO = new AccessApplReqDTO();
				reqDTO.setDocNo(task.getDocNumber());
				AccessApplResDTO resDTO = accessApplSO.retrieveAccessApplByApplNo(reqDTO);
				if(resDTO.isSuccessful() && resDTO.getAccessAppl() != null){
					vo.setDocId(resDTO.getAccessAppl().getAccessApplId());
					
				}
			}catch(Exception e){
				throw new EpException(e.getMessage());
			}finally{
				accessApplSO = null;
			}
		}
		
		return vo;
	}
	
	/**
	 * To generate user result bean based on reassignType
	 * 
	 * @param status
	 * @param searchBean
	 * @param task
	 * @param reassignType
	 * @param roleList
	 * @param vo
	 * @return ISearchResultBean<PmUser>
	 */
	private ISearchResultBean<SmPersonnel> generateUserResultBean(PmStatus status , ISearchBean<SmPersonnel> searchBean,WorklistItemVO task ,int reassignType,List<String> roleList,SmTaskReassignmentVO vo){
		ISearchResultBean<SmPersonnel> resultBean = null;
		boolean isSearched = false;
		Integer subReassignType = null;
		Long userId = getManagedBean().getTaskAssignmentSearchBean().getUser().getPmUser().getUserId();
		// filter by user name
		if(StringUtils.isNotBlank(getManagedBean().getSearchUserName())){
			searchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"name",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getSearchUserName(),true));
		}
		if(roleList.isEmpty()){
			roleList = vo.getRoleList();
		}	
		
		if(!isSearched){
			// Filter By user status
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));
			
			// Filter By user org status
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("smAppl.smSupplier.supplierId",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getSmSupplier().getSupplierId()));	
			
			// Filter By is softcert
			if(status.getPmStatusTask().isSoftcert()){
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("softcert",
								SearchOperator.OPERATOR_EQUALS.value(),
								status.getPmStatusTask().isSoftcert()));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUser.pmUserDigiCert.pmDigiCert.validFrom",
								SearchOperator.OPERATOR_LESS_EQUAL.value(),
								getCurrentDateWithoutTime()));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUser.pmUserDigiCert.pmDigiCert.validTo",
								SearchOperator.OPERATOR_GREATER.value(),
								getCurrentDateWithoutTime()));
			}

			// Filter By is contract signer
			if(status.getPmStatusTask().getPropertyField() != null){
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(status.getPmStatusTask().getPropertyField(),
								SearchOperator.OPERATOR_EQUALS.value(),
								status.getPmStatusTask().getPropertyValue()));
			}
			
			
			// not current selected user and not current login user
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUser.userId",
							SearchOperator.OPERATOR_NOT_IN.value(),
							new ArrayList<Long>(Arrays.asList(userId))));

			if (roleList == null || roleList.isEmpty() || roleList.get(0) == null) {
				roleList = retrieveAvailableRoles(task.getDocStatus().longValue(),reassignType,subReassignType);
			}

			if (!roleList.isEmpty()) {
				// in applicable to roles
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"epRole",
								SearchOperator.OPERATOR_IN.value(), roleList));
			}
		
			
			// Order by user name
			searchBean.getSortColumns().add(new SearchOrderBean("name"));
			
			resultBean = SmCommonUtil.searchUsersResultBeanBySearchBean(searchBean);
		}
		
		return resultBean;
		
	}
	
	/**
	 * retrieve available roles by status id
	 * 
	 * @param statusId
	 * @return List<String>
	 */
	private List<String> retrieveAvailableRoles(Long statusId,Integer reassignType,Integer subReassignType) {
		log.info("retrieveAvailableRoles");
		IStatusSO statusSO = null;
		List<String> statusRoles = new ArrayList<String>();
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			reqDTO.setStatusId(statusId);
			reqDTO.setReassignType(reassignType);
			reqDTO.setSubReassignType(subReassignType);
			StatusResDTO resDTO = statusSO.retrieveStatusRoleByStatusId(reqDTO);
			if (resDTO.isSuccessful()) {
				for (PmStatusRole statusRole : resDTO.getStatusRoleList()) {
					statusRoles.add(statusRole.getPmRole().getRoleCode());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770093,
					new String[] { e.getMessage() });
		} finally {
			statusSO = null;
		}
		return statusRoles;
	}
	
	/**
	 * To search available user to be assignee
	 */
	public void searchAvailableUser(){
		userListDataModel.reloadData();
	}
	
	/**
	 * To reset user list data model 
	 */
	public void resetAvailableUser(){
		getManagedBean().setSearchUserName(null);
		//#29822 - START
		getManagedBean().setTasksReassignmentVO(null);
		//#29822 - END
		userListDataModel.resetData();
	}
	
	/**
	 * @return the userListDataModel
	 */
	public PagingDataModel<SmPersonnel> getUserListDataModel() {
		return userListDataModel;
	}

	/**
	 * @param userListDataModel the userListDataModel to set
	 */
	public void setUserListDataModel(PagingDataModel<SmPersonnel> userListDataModel) {
		this.userListDataModel = userListDataModel;
	}
	
	/**
	 * Get Role description by role code.
	 * 
	 * @param roleCode
	 *            the role code.
	 * @return role description
	 */
	public String getRoleDescByRoleCode(String roleCode) {
		log.info("getRoleDescByRoleCode");
		return PmCommonUtil.getRoleDescByRoleCode(roleCode);
	}
	
	/**
	 * Retrieve current date without time. 
	 * To compare with effective and expiry date.
	 * 
	 * @return Date object
	 */
	protected Date getCurrentDateWithoutTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	//#29822 - START
	public void checkUserTask(AjaxBehaviorEvent event) {
		log.info("checkUserTask - Start");			
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
			this.getManagedBean().getSelectedTasksToBulkReassign().clear();
		}
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign() == null) {
			this.getManagedBean().setSelectedTasksToBulkReassign(new ArrayList <WorklistItemVO>());
		}
		//checkbox checked
		if ((boolean) event.getComponent().getAttributes().get("value")) {
			for (WorklistItemVO task : this.getManagedBean().getAllUserTasks()) {
		    	if (task.getTaskSelect()) {
		    		log.info("[#29822] CHECK USER task: "+task.getTaskId());
		    		task.setTaskSelect(true);
		    		this.getManagedBean().getSelectedTasksToBulkReassign().add(task);		    		
		    	}
		    }
		} 
		//checkbox unchecked
		else {
			for (WorklistItemVO task : this.getManagedBean().getAllUserTasks()) {
				if (task.getTaskSelect()) {
					log.info("[#29822] UNCHECK USER task: "+task.getTaskId());
		    		task.setTaskSelect(true);
		    		this.getManagedBean().getSelectedTasksToBulkReassign().add(task);
		    	} else {
		    		task.setTaskSelect(false);
		    	}
			}
		}
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
			this.getManagedBean().setShowParentMsg(false);
			this.getManagedBean().setShowChildMsg(true);
			boolean isValid = false;
	    	if(this.getManagedBean().getSelectedTasksToBulkReassign().size() > 1) {
	    		//validate selected task list - same doc type and same doc status
	    		isValid = validateDocTypeAndStatus();
	    	} else {
	    		isValid = true;
	    	}
	    	
    	} else {
    		this.getManagedBean().setShowParentMsg(true);
			this.getManagedBean().setShowChildMsg(false);    		
    	}
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0 &&
				(this.getManagedBean().getAllUserTasks().size() == this.getManagedBean().getSelectedTasksToBulkReassign().size())) {			
			this.getManagedBean().setCheckAllUserTasks(true);
			this.getManagedBean().setCheckAllUserTasksEnabled(true);
		} else {
			this.getManagedBean().setCheckAllUserTasks(false);
			this.getManagedBean().setCheckAllUserTasksEnabled(true);
		}
		log.info("checkUserTask - End");
	}
	
	public void checkAllUserTasks() {
		log.info("checkAllUserTasks - Start");
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
			this.getManagedBean().getSelectedTasksToBulkReassign().clear();
		}
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign() == null) {
			this.getManagedBean().setSelectedTasksToBulkReassign(new ArrayList <WorklistItemVO>());
		}
		
    	for ( WorklistItemVO task : this.getManagedBean().getAllUserTasks()) {
    		if (this.getManagedBean().isCheckAllUserTasks()) {
    			task.setTaskSelect(true);
    			this.getManagedBean().getSelectedTasksToBulkReassign().add(task);
    		} else {
    			task.setTaskSelect(false);

    			if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
    					this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
    				this.getManagedBean().getSelectedTasksToBulkReassign().clear();
    			}
    		}
		}
    	
    	if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
    		this.getManagedBean().setShowParentMsg(false);
			this.getManagedBean().setShowChildMsg(true);
			boolean isValid = false;
	    	//validate selected task list - same doc type and same doc status
			
			isValid = validateDocTypeAndStatus();
	    	
			if(isValid) {
				
			}
    	
    	} else {
    		this.getManagedBean().setShowParentMsg(true);
			this.getManagedBean().setShowChildMsg(false);    		
    	}
    	log.info("checkAllUserTasks - End");
	}
	
	
	
	/**
	 * Validate doc type and doc status 
	 * 
	 * @return void
	 */	
	public boolean validateDocTypeAndStatus() {
		log.info("validateDocTypeAndStatus - Start");
		
		String docType = null;
		long docStat = 0L;
		WorklistItemVO task = null;
		boolean isSameTypeAndStat = false;
		
		try {			
			if(this.getManagedBean().getSelectedTasksToBulkReassign() != null 
					&& this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
				
				//get document type & status from first task from bulk reassign list
				task = this.getManagedBean().getSelectedTasksToBulkReassign().get(0);
				docType = task.getDocType();
				docStat = task.getDocStatus().longValue();
				this.getManagedBean().setDocType(docType);
				
				if(this.getManagedBean().getSelectedTasksToBulkReassign().size() > 1) {
					//check if next task from list have same doc type & status from first task
					for(int i = 1; i < this.getManagedBean().getSelectedTasksToBulkReassign().size(); i++) {
						task = this.getManagedBean().getSelectedTasksToBulkReassign().get(i);
						
						if(docType.equals(task.getDocType()) && (docStat == task.getDocStatus().longValue())) {							
							isSameTypeAndStat = true;					
						} else {							
							isSameTypeAndStat = false;
							break;
						}
					}
				} else {
					isSameTypeAndStat = true;
				}
			}		
			
			if(isSameTypeAndStat) {				
				return true;
			} else {				
				this.getManagedBean().getSelectedTasksToBulkReassign().clear();				
				
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								ResourceBundleUtils.getMessage("lbl.pm.ta.user.task.invalidtypeandstatus", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale())));
				return false;
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
	}
	
	/**
	 * Redirect to Bulk Task Re-assignment Details
	 * 
	 * @return xhtml
	 */	
	public void redirectToBulkTasksReassign() {
		log.info("redirectToBulkTasksReassign - Start");		
		
		try {
			this.getManagedBean().setSelectedUser(null);
			this.getManagedBean().setSelectedTask(null);
			resetAvailableUser();
			this.getManagedBean().setTasksReassignmentVO(null);
			this.getManagedBean().setCommitteeUpdate(false);
			this.getManagedBean().setRemarks(null);		
			this.getManagedBean().setBulkReassign(true);
			this.getManagedBean().setSingleReassign(false);
			
			if(this.getManagedBean().getSelectedTasksToBulkReassign() != null && this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
				WorklistItemVO task = this.getManagedBean().getSelectedTasksToBulkReassign().get(0);
				this.getManagedBean().setDocType(task.getDocType());
			}
			
			//set value for text area document number
			this.getManagedBean().setSelectedTasksTA(getSelectedTasks());
				
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
		log.info("redirectToBulkTasksReassign - End");
		
		RequestContext.getCurrentInstance().execute("dlSuppTaskReassignment.show()");		
	}
	
	public String getSelectedTasks() {		
		log.info("getSelectedTasks - Start");		
		WorklistItemVO task = null;
		StringBuilder documents = new StringBuilder();
		
		if(this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {			 
			for (int i = 0; i < this.getManagedBean().getSelectedTasksToBulkReassign().size(); i++) {
				task = this.getManagedBean().getSelectedTasksToBulkReassign().get(i);				
				documents.append(task.getDocNumber());
				documents.append(", ");				
			}
		}
		log.info("getSelectedTasks - End");
		return documents.toString();
		
	}
	
	//#29822 - start
	public void removeReassignAllGroupTasks() {
		
		IBpmSO bpmSO = null;
		BpmRequestDTO reqDTO = null;
		String loginId = null;
		List<Integer> statusIds = retrieveAvailableStatusId();
		TaskAssignmentSearchBean taskSearchBean = null;

		TaskAssignmentResDTO resDTO = new TaskAssignmentResDTO();
		
		try {
			taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
			loginId = taskSearchBean.getUser() != null ? taskSearchBean
					.getUser().getPmUser().getLoginId() : null;

			if (loginId == null) {
				if (getManagedBean().getResultUser() != null) {
					loginId = getManagedBean().getResultUser().getLoginId();
				}
			} 
			if (loginId != null && loginId.length() > 0) {
				
				if (statusIds != null && !statusIds.isEmpty()) {
					bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
							.getServiceObject(IBpmSO.class);
					reqDTO = new BpmRequestDTO();
					Object ctxToken = bpmSO.authenticate(loginId);

					//set limit to 10
//					reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
					reqDTO.setLimit(20);
					reqDTO.setOffset(0);
					reqDTO.setStatusList(statusIds);
					reqDTO.setUserId(loginId);
					reqDTO.setCtxToken(ctxToken);
					List<String> docNumberList = new ArrayList<String>();
					if (StringUtils.isNotBlank(getManagedBean()
							.getTaskAssignmentSearchBean().getDocNo())) {
						docNumberList.add(getManagedBean()
								.getTaskAssignmentSearchBean().getDocNo());
						reqDTO.setDocNumberList(docNumberList);
					}

					
					if (StringUtils.isNotBlank(getManagedBean().getTaskAssignmentSearchBean().getDocNo())) {
						docNumberList.add(getManagedBean().getTaskAssignmentSearchBean().getDocNo());
						reqDTO.setDocNumberList(docNumberList);
					}

					if (getManagedBean().getTaskAssignmentSearchBean().getDocType() != null
							&& getManagedBean().getTaskAssignmentSearchBean().getDocType() != null) {
						reqDTO.setDocType(getManagedBean()
								.getTaskAssignmentSearchBean().getDocType());
					}
					
					reqDTO.setState(BpmConstants.STATE_ASSIGNED);
					reqDTO.setAssignment(BpmConstants.ASSIGNMENT_GROUP);
					reqDTO.setIsClaimed(false);
					
					//start remove all task
					resDTO = removeReassignAllGroupTasks(reqDTO, this.getManagedBean());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bpmSO = null;
		}
		
		
		if (resDTO != null && resDTO.isSuccessful()) {
			resetCheckAllTasks();
			this.getManagedBean().getAllGroupTasks().clear();
			this.getManagedBean().getSelectedGroupTasks().clear();
			this.getManagedBean().setEnableDeleteGroupTasksButton(false);
			reloadAllTaskList();
			PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
			FacesMessage.SEVERITY_INFO, MessageConst.INFO_770105, null);
		}
	}
	
	//#29822 - end
	
	public void resetCheckAllTasks() {
		log.info("resetCheckAllTasks");
		if (getManagedBean().isCheckAllGroupTasksEnabled()) {
			getManagedBean().setCheckAllGroupTasksEnabled(false);
		}
		if (getManagedBean().isCheckAllGroupTasks()) {
			getManagedBean().setCheckAllGroupTasks(false);
		}
		if (getManagedBean().isCheckAllOthersTasksEnabled()) {
			getManagedBean().setCheckAllOthersTasksEnabled(false);
		}
		if (getManagedBean().isCheckAllOthersTasks()) {
			getManagedBean().setCheckAllOthersTasks(false);
		}
		log.info("isCheckAllGroupTasksEnabled= "+getManagedBean().isCheckAllGroupTasksEnabled());
		log.info("isCheckAllOthersTasksEnabled= "+getManagedBean().isCheckAllOthersTasksEnabled());		
		
		getManagedBean().setCheckAllUserTasks(false);
		getManagedBean().setCheckAllUserTasksEnabled(false);	
	}	
	
	public void removeReassignGroupTasks() {
		
		log.info("removeReassignGroupTasks");

		String claimedBy = null;
		ISmTaskAssignmentSO taskAssignmentSO = null;
		int reassignWorkListType = getManagedBean().getTaskType();
		List<String> assigneeUsers = new ArrayList<>();
		
		TaskAssignmentResDTO resDTO = new TaskAssignmentResDTO();
		
		if (this.getManagedBean().getSelectedGroupTasks() != null && 
				!this.getManagedBean().getSelectedGroupTasks().isEmpty()) {
			
			log.info("Number of task(s) to be deleted= "+this.getManagedBean().getSelectedGroupTasks().size());
			
			for (Entry<String, WorklistItemVO> selectedTask : this.getManagedBean().getSelectedGroupTasks().entrySet()) {
				
				String taskId = selectedTask.getKey();
				WorklistItemVO task = selectedTask.getValue();
				log.info("Task(s) to be deleted: "+task.getDocNumber()+" - "+taskId);
				
				if (!task.getAssignees().isEmpty()) {
					assigneeUsers = task.getAssignees();
				}
				if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
					String currentUser = getManagedBean().getTaskAssignmentSearchBean().getUser() != null ?
							  getManagedBean().getTaskAssignmentSearchBean().getUser().getPmUser().getLoginId() : 
							  this.getManagedBean().getSelectedReassignee().get(0);
					
					if (assigneeUsers.contains(currentUser)) {
						assigneeUsers.remove(currentUser);
					}
					if (assigneeUsers != null && assigneeUsers.size() > 1 && 
							ProfileManagementConst.REASSIGN_WORKLIST_TYPE_OTHERS == reassignWorkListType) {
						claimedBy = task.getAcquiredBy();
					}
				}
				
				try {
					TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
					reqDTO.setTaskId(task.getTaskId());
					reqDTO.setUserIdList(assigneeUsers);
					reqDTO.setClaimedBy(claimedBy);

					taskAssignmentSO = (ISmTaskAssignmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmTaskAssignmentSO.class);
					resDTO = taskAssignmentSO.removeGroupOrOthersTask(reqDTO);

				} finally {
					taskAssignmentSO = null;
				}
			}
		}
		if (resDTO != null && resDTO.isSuccessful()) {
			resetCheckAllTasks();
			this.getManagedBean().getAllGroupTasks().clear();
			this.getManagedBean().getSelectedGroupTasks().clear();
			this.getManagedBean().setEnableDeleteGroupTasksButton(false);
			reloadAllTaskList();
			PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
			FacesMessage.SEVERITY_INFO, MessageConst.INFO_770105, null);
		}
	}
	
	//#29822 - start
	public void removeReassignAllOthersTasks() {
		
		IBpmSO bpmSO = null;
		BpmRequestDTO reqDTO = null;
		String loginId = null;
		List<Integer> statusIds = retrieveAvailableStatusId();
		TaskAssignmentSearchBean taskSearchBean = null;

		TaskAssignmentResDTO resDTO = new TaskAssignmentResDTO();
		
		try {
			taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
			loginId = taskSearchBean.getUser() != null ? taskSearchBean
					.getUser().getPmUser().getLoginId() : null;

			if (loginId == null) {
				if (getManagedBean().getResultUser() != null) {
					loginId = getManagedBean().getResultUser().getLoginId();
				}
			} 
			if (loginId != null && loginId.length() > 0) {
				
				if (statusIds != null && !statusIds.isEmpty()) {
					bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
							.getServiceObject(IBpmSO.class);
					reqDTO = new BpmRequestDTO();
					Object ctxToken = bpmSO.authenticate(loginId);

					//set limit to 10
//					reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
					reqDTO.setLimit(20);
					reqDTO.setOffset(0);
					reqDTO.setStatusList(statusIds);
					reqDTO.setUserId(loginId);
					reqDTO.setCtxToken(ctxToken);
					List<String> docNumberList = new ArrayList<String>();
					if (StringUtils.isNotBlank(getManagedBean()
							.getTaskAssignmentSearchBean().getDocNo())) {
						docNumberList.add(getManagedBean()
								.getTaskAssignmentSearchBean().getDocNo());
						reqDTO.setDocNumberList(docNumberList);
					}

					if (getManagedBean().getTaskAssignmentSearchBean()
							.getDocType() != null
							&& getManagedBean()
									.getTaskAssignmentSearchBean().getDocType() != null) {
						reqDTO.setDocType(getManagedBean()
								.getTaskAssignmentSearchBean().getDocType());
					}
					
					reqDTO.setState(BpmConstants.STATE_ASSIGNED);
					reqDTO.setAssignment(BpmConstants.ASSIGNMENT_ALL);
					
					//start remove all task
					resDTO = removeReassignAllOthersTasks(reqDTO, this.getManagedBean());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bpmSO = null;
		}
		
		
		if (resDTO != null && resDTO.isSuccessful()) {
			resetCheckAllTasks();
			this.getManagedBean().getAllOthersTasks().clear();
			this.getManagedBean().getSelectedOthersTasks().clear();
			this.getManagedBean().setEnableDeleteOthersTasksButton(false);
			reloadAllTaskList();
			PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
			FacesMessage.SEVERITY_INFO, MessageConst.INFO_770105, null);
		}
	
	}
	
	//#29822 - end
	
	public void removeReassignOthersTasks() {
	
		log.info("removeReassignOthersTasks");
	
		String claimedBy = null;
		ISmTaskAssignmentSO taskAssignmentSO = null;
		int reassignWorkListType = getManagedBean().getTaskType();
		List<String> assigneeUsers = new ArrayList<>();
		
		TaskAssignmentResDTO resDTO = new TaskAssignmentResDTO();
		
		if (this.getManagedBean().getSelectedOthersTasks() != null && 
				!this.getManagedBean().getSelectedOthersTasks().isEmpty()) {
			
			log.info("Number of task(s) to be deleted= "+this.getManagedBean().getSelectedOthersTasks().size());
			
			for (Entry<String, WorklistItemVO> selectedTask : this.getManagedBean().getSelectedOthersTasks().entrySet()) {
				
				String taskId = selectedTask.getKey();
				WorklistItemVO task = selectedTask.getValue();
				log.info("Task(s) to be deleted: "+taskId);
				
				if (!task.getAssignees().isEmpty()) {
					assigneeUsers = task.getAssignees();
				}
				
				if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
					String currentUser = getManagedBean().getTaskAssignmentSearchBean().getUser() != null ?
							  getManagedBean().getTaskAssignmentSearchBean().getUser().getPmUser().getLoginId() : 
							  this.getManagedBean().getSelectedReassignee().get(0);
					
					if (assigneeUsers.contains(currentUser)) {
						assigneeUsers.remove(currentUser);
					}
					if (assigneeUsers != null && assigneeUsers.size() > 1 && 
							ProfileManagementConst.REASSIGN_WORKLIST_TYPE_OTHERS == reassignWorkListType) {
						claimedBy = task.getAcquiredBy();
					}
				}
				
				try {
					TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
					reqDTO.setTaskId(task.getTaskId());
					reqDTO.setUserIdList(assigneeUsers);
					reqDTO.setClaimedBy(claimedBy);
	
					taskAssignmentSO = (ISmTaskAssignmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmTaskAssignmentSO.class);
					resDTO = taskAssignmentSO.removeGroupOrOthersTask(reqDTO);
	
				} finally {
					taskAssignmentSO = null;
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_770098);
		}
		if (resDTO != null && resDTO.isSuccessful()) {
			resetCheckAllTasks();
			this.getManagedBean().getAllOthersTasks().clear();
			this.getManagedBean().getSelectedOthersTasks().clear();
			this.getManagedBean().setEnableDeleteOthersTasksButton(false);
			reloadAllTaskList();
			PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
			FacesMessage.SEVERITY_INFO, MessageConst.INFO_770105, null);
		}
	}
	
	public void showDialog(String dialog){
		log.info("Displaying " + dialog);
		RequestContext.getCurrentInstance().execute(dialog+".show()");	
	}
	
	public void checkAllGroupTasks() {
		log.info("Total number of GROUP tasks= " + this.getManagedBean().getAllGroupTasks().size());
		
		if (this.getManagedBean().getSelectedGroupTasks() != null && 
				this.getManagedBean().getSelectedGroupTasks().size() > 0) {
			this.getManagedBean().getSelectedGroupTasks().clear();
		}
    	for ( WorklistItemVO task : this.getManagedBean().getAllGroupTasks()) {
    		if (this.getManagedBean().isCheckAllGroupTasks()) {
    			task.setTaskSelect(true);
    			this.getManagedBean().getSelectedGroupTasks().put(task.getTaskId(),task);
    		} else {
    			task.setTaskSelect(false);
    			if (this.getManagedBean().getSelectedGroupTasks().containsKey(task.getTaskId())) {
	    			this.getManagedBean().getSelectedGroupTasks().remove(task.getTaskId());
	    		}
    		}
		}
    	this.getManagedBean().setEnableDeleteGroupTasksButton(this.getManagedBean().isCheckAllGroupTasks());
    	log.info("getSelectedGroupTasks= " + this.getManagedBean().getSelectedGroupTasks().size());
    	log.info("enableDeleteGroupTasksButton= " + this.getManagedBean().isEnableDeleteGroupTasksButton());
	}
	
	public void checkGroupTask(AjaxBehaviorEvent event) {
		
		if ((boolean) event.getComponent().getAttributes().get("value")) {
			for (WorklistItemVO checkedTask : this.getManagedBean().getAllGroupTasks()) {
		    	if (checkedTask.getTaskSelect()) {
		    		log.info("task= "+checkedTask.getTaskId());
		    		this.getManagedBean().getSelectedGroupTasks().put(checkedTask.getTaskId(),checkedTask);
		    	}
		    }
		} else {
			for (WorklistItemVO uncheckedTask : this.getManagedBean().getAllGroupTasks()) {
				if (!uncheckedTask.getTaskSelect()) {
					uncheckedTask.setTaskSelect(false);
		    		this.getManagedBean().getSelectedGroupTasks().remove(uncheckedTask.getTaskId());
		    	}
			}
		}
		if (this.getManagedBean().getSelectedGroupTasks().size() > 0) {
			this.getManagedBean().setEnableDeleteGroupTasksButton(true);
    	} else {
    		this.getManagedBean().setEnableDeleteGroupTasksButton(false);
    	}
		log.info("getSelectedGroupTasks= " + this.getManagedBean().getSelectedGroupTasks().size());
		log.info("enableDeleteGroupTasksButton= " + this.getManagedBean().isEnableDeleteGroupTasksButton());
	}
	
	public void checkAllOthersTasks() {
		log.info("Total number of OTHERS tasks= " + this.getManagedBean().getAllOthersTasks().size());
		
		if (this.getManagedBean().getSelectedOthersTasks() != null && 
				this.getManagedBean().getSelectedOthersTasks().size() > 0) {
			this.getManagedBean().getSelectedOthersTasks().clear();
		}
    	for ( WorklistItemVO task : this.getManagedBean().getAllOthersTasks()) {
    		if (this.getManagedBean().isCheckAllOthersTasks()) {
    			task.setTaskSelect(true);
    			this.getManagedBean().getSelectedOthersTasks().put(task.getTaskId(),task);
    		} else {
    			task.setTaskSelect(false);
    			if (this.getManagedBean().getSelectedOthersTasks().containsKey(task.getTaskId())) {
	    			this.getManagedBean().getSelectedOthersTasks().remove(task.getTaskId());
	    		}
    		}
		}
    	this.getManagedBean().setEnableDeleteOthersTasksButton(this.getManagedBean().isCheckAllOthersTasks());
    	log.info("getSelectedOthersTasks= " + this.getManagedBean().getSelectedOthersTasks().size());
    	log.info("enableDeleteOthersTasksButton= " + this.getManagedBean().isEnableDeleteOthersTasksButton());
	}
	
	public void checkOthersTask(AjaxBehaviorEvent event) {
		
		if ((boolean) event.getComponent().getAttributes().get("value")) {
			for (WorklistItemVO checkedTask : this.getManagedBean().getAllOthersTasks()) {
		    	if (checkedTask.getTaskSelect()) {
		    		log.info("task= "+checkedTask.getTaskId());
		    		this.getManagedBean().getSelectedOthersTasks().put(checkedTask.getTaskId(),checkedTask);
		    	}
		    }
		} else {
			for (WorklistItemVO uncheckedTask : this.getManagedBean().getAllOthersTasks()) {
				if (!uncheckedTask.getTaskSelect()) {
					uncheckedTask.setTaskSelect(false);
					this.getManagedBean().getSelectedOthersTasks().remove(uncheckedTask.getTaskId());
		    	}
			}
		}
		if (this.getManagedBean().getSelectedOthersTasks().size() > 0) {
			this.getManagedBean().setEnableDeleteOthersTasksButton(true);
    	} else {
    		this.getManagedBean().setEnableDeleteOthersTasksButton(false);
    	}
		log.info("getSelectedOthersTasks= " + this.getManagedBean().getSelectedOthersTasks().size());
		log.info("enableDeleteOthersTasksButton= " + this.getManagedBean().isEnableDeleteOthersTasksButton());
	}
	//#29822 - END
	
	//#29822 - start
	/**
	 * Method to remove all task reassigment PM
	 * 
	 *
	 */
	
	public TaskAssignmentResDTO removeReassignAllGroupTasks(BpmRequestDTO bpmReqDTO, TaskReassignmentManagedBean taskReassignmentManagedBean) {
		
		//init
		ISmTaskAssignmentSO taskAssignmentSO  = null;
		IBpmSO bpmSO = null;
		
		String claimedBy = null;
		
		BpmResponseDTO responseDTO = null;
		TaskAssignmentResDTO resDTO = new TaskAssignmentResDTO();
	
		
		try {
			//SLA Monitoring
			long startTime = System.currentTimeMillis();
			
			//get services
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			
			taskAssignmentSO = (ISmTaskAssignmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
					ISmTaskAssignmentSO.class);
			
			//get all the task based on limit -> 10
			responseDTO = bpmSO.getReassignedItems(bpmReqDTO);
			
			//start do-while
			do{
				if (responseDTO.getWorklistVO() != null) {
					if (!responseDTO.getWorklistVO().getWorklistItem().isEmpty() && 
							responseDTO.getWorklistVO().getWorklistItem().size() > 0) {
						
						//clear selected group task
						if (taskReassignmentManagedBean.getSelectedGroupTasks() != null && 
								taskReassignmentManagedBean.getSelectedGroupTasks().size() > 0) {
							taskReassignmentManagedBean.getSelectedGroupTasks().clear();
						}
						
						//add the task from respondeDTO to bean selected group taks
				    	for (WorklistItemVO task : responseDTO.getWorklistVO().getWorklistItem()) {   		
				  			task.setTaskSelect(true);
							taskReassignmentManagedBean.getSelectedGroupTasks().put(task.getTaskId(),task);
				    	}
				    	
				    	taskReassignmentManagedBean.setEnableDeleteGroupTasksButton(true);
				    	log.info("getSelectedGroupTasks= "+taskReassignmentManagedBean.getSelectedGroupTasks().size());
				    	log.info("enableDeleteGroupTasksButton= "+taskReassignmentManagedBean.isEnableDeleteGroupTasksButton());

						int reassignWorkListType = taskReassignmentManagedBean.getTaskType();
						List<String> assigneeUsers = new ArrayList<String>();
						
						if (taskReassignmentManagedBean.getSelectedGroupTasks() != null && 
								!taskReassignmentManagedBean.getSelectedGroupTasks().isEmpty()) {
							
							log.info("Number of task(s) to be deleted= "+taskReassignmentManagedBean.getSelectedGroupTasks().size());
							
							for (Entry<String, WorklistItemVO> selectedTask : taskReassignmentManagedBean.getSelectedGroupTasks().entrySet()) {			
								String taskId = selectedTask.getKey();
								WorklistItemVO task = selectedTask.getValue();
								log.info("Task(s) to be deleted: "+task.getDocNumber()+" - "+taskId);
								
								if (!task.getAssignees().isEmpty()) {
									assigneeUsers = task.getAssignees();
								}
								if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
									String currentUser = taskReassignmentManagedBean.getTaskAssignmentSearchBean().getUser() != null ?
											taskReassignmentManagedBean.getTaskAssignmentSearchBean().getUser().getPmUser().getLoginId() : 
											  taskReassignmentManagedBean.getSelectedReassignee().get(0);
									
									if (assigneeUsers.contains(currentUser)) {
										assigneeUsers.remove(currentUser);
									}
									if (assigneeUsers != null && assigneeUsers.size() > 1 && 
											ProfileManagementConst.REASSIGN_WORKLIST_TYPE_OTHERS == reassignWorkListType) {
										claimedBy = task.getAcquiredBy();
									}
								}
								
								TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
								reqDTO.setTaskId(task.getTaskId());
								reqDTO.setUserIdList(assigneeUsers);
								reqDTO.setClaimedBy(claimedBy);

								taskAssignmentSO.removeGroupOrOthersTask(reqDTO);
							}
						}
					} 
				}
				
				//get all the task based on limit -> 10
				//end the loop if size is equal to zero
				responseDTO = bpmSO.getReassignedItems(bpmReqDTO);
				
			}while(responseDTO.getWorklistVO().getWorklistItem().size() > 0);
			//end while
			
			log.info("finish delete all task");
			
			SLAUtils.getInstance().logSLAInfo("SM","remove",
					"TaskReassignmentBackingBean.removeReassignAllGroupTasks", startTime);
			
			resDTO.setSuccessful(true);
			return resDTO;
		} catch (Exception e) {
			resDTO.setSuccessful(false);
			return resDTO;
		} finally {
			bpmSO = null;
			taskAssignmentSO = null;
		}
	}
	
	
	public TaskAssignmentResDTO removeReassignAllOthersTasks(BpmRequestDTO bpmReqDTO, TaskReassignmentManagedBean taskReassignmentManagedBean) {
		
		//init
		ISmTaskAssignmentSO taskAssignmentSO  = null;
		IBpmSO bpmSO = null;

		String claimedBy = null;
		
		BpmResponseDTO responseDTO = null;
		TaskAssignmentResDTO resDTO = new TaskAssignmentResDTO();
		
		
		try {
			//SLA Monitoring
			long startTime = System.currentTimeMillis();
			
			//get services
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			
			taskAssignmentSO = (ISmTaskAssignmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
					ISmTaskAssignmentSO.class);
			
			//get all the task based on limit -> 10
			responseDTO = bpmSO.getGroupByOthersItems(bpmReqDTO);
			
			//start do-while
			do{
				if (responseDTO.getWorklistVO() != null) {
					if (!responseDTO.getWorklistVO().getWorklistItem().isEmpty() && 
							responseDTO.getWorklistVO().getWorklistItem().size() > 0) {
						
						//clear selected group task
						if (taskReassignmentManagedBean.getSelectedOthersTasks() != null && 
								taskReassignmentManagedBean.getSelectedOthersTasks().size() > 0) {
							taskReassignmentManagedBean.getSelectedOthersTasks().clear();
						}
						
						//add the task from respondeDTO to bean selected group taks
				    	for (WorklistItemVO task : responseDTO.getWorklistVO().getWorklistItem()) {   		
				  			task.setTaskSelect(true);
							taskReassignmentManagedBean.getSelectedOthersTasks().put(task.getTaskId(),task);
				    	}
				    	
				    	taskReassignmentManagedBean.setEnableDeleteGroupTasksButton(true);
				    	log.info("getSelectedGroupTasks= "+taskReassignmentManagedBean.getSelectedOthersTasks().size());
				    	log.info("enableDeleteGroupTasksButton= "+taskReassignmentManagedBean.isEnableDeleteGroupTasksButton());

						int reassignWorkListType = taskReassignmentManagedBean.getTaskType();
						List<String> assigneeUsers = new ArrayList<String>();
						
						if (taskReassignmentManagedBean.getSelectedOthersTasks() != null && 
								!taskReassignmentManagedBean.getSelectedOthersTasks().isEmpty()) {
							
							log.info("Number of task(s) to be deleted= "+taskReassignmentManagedBean.getSelectedOthersTasks().size());
							
							for (Entry<String, WorklistItemVO> selectedTask : taskReassignmentManagedBean.getSelectedOthersTasks().entrySet()) {			
								String taskId = selectedTask.getKey();
								WorklistItemVO task = selectedTask.getValue();
								log.info("Task(s) to be deleted: "+task.getDocNumber()+" - "+taskId);
								
								if (!task.getAssignees().isEmpty()) {
									assigneeUsers = task.getAssignees();
								}
								if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
									String currentUser = taskReassignmentManagedBean.getTaskAssignmentSearchBean().getUser() != null ?
											taskReassignmentManagedBean.getTaskAssignmentSearchBean().getUser().getPmUser().getLoginId() : 
											  taskReassignmentManagedBean.getSelectedReassignee().get(0);
									
									if (assigneeUsers.contains(currentUser)) {
										assigneeUsers.remove(currentUser);
									}
									if (assigneeUsers != null && assigneeUsers.size() > 1 && 
											ProfileManagementConst.REASSIGN_WORKLIST_TYPE_OTHERS == reassignWorkListType) {
										claimedBy = task.getAcquiredBy();
									}
								}
								
								TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
								reqDTO.setTaskId(task.getTaskId());
								reqDTO.setUserIdList(assigneeUsers);
								reqDTO.setClaimedBy(claimedBy);

								taskAssignmentSO.removeGroupOrOthersTask(reqDTO);

							}
						}
					} 
				}
				
				//get all the task based on limit -> 10
				//end the loop if size is equal to zero
				responseDTO = bpmSO.getGroupByOthersItems(bpmReqDTO);
				
			}while(responseDTO.getWorklistVO().getWorklistItem().size() > 0);
			//end while
			
			log.info("finish delete all task");
			
			SLAUtils.getInstance().logSLAInfo("SM","remove",
					"TaskReassignmentBackingBean.removeReassignAllOthersTasks", startTime);
			
			resDTO.setSuccessful(true);
			return resDTO;
		} catch (Exception e) {
			resDTO.setSuccessful(false);
			return resDTO;
		} finally {
			bpmSO = null;
			taskAssignmentSO = null;
		}
	}
	
	//#29822 - end
	
}