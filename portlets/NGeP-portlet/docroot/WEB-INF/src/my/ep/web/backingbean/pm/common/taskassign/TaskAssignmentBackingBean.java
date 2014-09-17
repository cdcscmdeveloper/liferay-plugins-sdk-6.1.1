/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.common.taskassign;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.SupportDocumentSearchBean;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.pm.taskassign.TaskAssignmentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
	
	//BPM default filter values
	public static final String VIEW_BY = "ALL";
	public static final String ASSIGNMENT = "ALL";
	public static final String DOC_TYPE = "";
	public static final String STATE = "ASSIGNED";
	
	
	private PagingDataModel<WorklistItemVO> taskListDataModel = new taskPagingDataModel();

		
	public void preLoading(){
		log.info("preLoading");
		this.resetTaskAssignSearchBean();
//		this.getManagedBean().setSelectedTabIndex(0);
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
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
		this.taskListDataModel.reloadData();
		log.info("searchDocument - End");
		
	}
	
	/**
	 * redirect to Task Re-assignment Details
	 * @param id
	 * @return xhtml
	 */
	public void redirectToTaskReassign(WorklistItemVO item){
		log.info("redirectToTaskReassign - Start");
		try{
			this.getManagedBean().setSelectedTask(item);
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("redirectToTaskReassign - End");
		RequestContext.getCurrentInstance().execute("dlTaskReassignment.show()");
	}
	
	

	public String applicationStatus(Integer docInt) {
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		StatusReqDTO reqDTO = new StatusReqDTO();

		reqDTO.setLanguageCode(languageCode);
		reqDTO.setStatusId(docInt.longValue());
		StatusResDTO resDTO = statusSO.retrieveStatusDescById(reqDTO);

		System.out.println("docInt:" + docInt);

		return resDTO.getPmStatusDesc() != null ? resDTO.getPmStatusDesc().getStatusName() : "Review";
	}
	
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

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			log.info(">>>>>>>>>>>>>>>>>>>pageChangeListener<<<<<<<<<<<<<<<<<<<");
			IBpmSO bpmSO = null;
			try {
				TaskAssignmentSearchBean taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				String userName = taskSearchBean.getUserId();
				
				log.info(">>>>>>>>>>>>>>>>>>getAssignment()="+ ASSIGNMENT);
				log.info(">>>>>>>.pageNo()="+getPageNo());
				log.info(">>>>>>>.pageSize()="+getPageSize());
				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

				FacesScopes face = new FacesScopes();
				String token = face.getBPMAuthenticationCtxToken();

			    log.info("testing BPM auth******" + token);
				BpmRequestDTO reqDTO = new BpmRequestDTO();
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
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				resultBean.setTotalRecord(0);
				resultBean.setResultList(null);
				
				if (responseDTO.getWorklistVO() != null) {
					log.info("testing BPM******size=" + responseDTO.getWorklistVO().getWorklistItem().size());
					log.info("testing BPM******total="+ responseDTO.getWorklistVO().getTotal());
					if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
						for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							// fl module Purchase Request Initiation Submit PR
							if(worklistItemVO.getProcess().equalsIgnoreCase(FLProcessNameConst.PURCHASE_REQ_INITIATION_PROCESS_NAME) && 
									worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_PR_HT)) {
								if(!worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
									worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())+" - "+worklistItemVO.getTitle());									
								}
							}
							// s1 module
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pid", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
						}
					}
					
					resultBean.setTotalRecord(responseDTO.getWorklistVO().getTotal());
					resultBean.setResultList(responseDTO.getWorklistVO().getWorklistItem());
				}
				return resultBean;
			} finally {
				bpmSO = null;
			}
		}
		
		private int calculateFinalOffSet(int currentPage) {
			int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;
			return offSet;
		}
	}
	
}