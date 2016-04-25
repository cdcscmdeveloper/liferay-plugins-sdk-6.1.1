package my.ep.web.managedbean.bpm.tasklist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.BpmConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "bpmTasklistMB")
@SessionScoped
public class BPMTaskListManagedBean extends BaseManagedBean {
	private List<WorklistItemVO> taskList;
	private List<FlFulfilmentOrder> flFulfilmentOrderList;
	private static Map bpmMap = null;
	private String processName;
	private String[] action;

	private String taskId;
	
	private Integer tabActiveIndex = 0;

	// private String status="ASSIGNED";

	private String taskName;

	private String tempLoginId; // TODO: temp and to be removed

	private String viewBy = "ALL";

	private String assignment = "ALL";
	
	private String docType = "";
	
	
	private String viewByGroup = "ALL";
	
	private String docTypeGroup = "";

	private int showIndex = 0;
	
	private PmOrgProfile selectedOrgProfile;
	
	private PmOrgProfile selectedOrgProfileGroup;
	
	private String docNo;
	
	private String taskDesc;
	
	
	private String docNoGroup;
	
	private String taskDescGroup;
	
	private Boolean claim = true;
	
	private PmStatusDesc selectedStatusDesc;
	private PmStatusDesc selectedStatusDescAdvance;
	
	public List<WorklistItemVO> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<WorklistItemVO> taskList) {
		this.taskList = taskList;
	}

	
	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/*
	 * public String getStatus() { return status; }
	 * 
	 * public void setStatus(String status) { this.status = status; }
	 */

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTempLoginId() {
		return tempLoginId;
	}

	public void setTempLoginId(String tempLoginId) {
		this.tempLoginId = tempLoginId;
	}

	public String getViewBy() {
		return viewBy;
	}

	public void setViewBy(String viewBy) {
		this.viewBy = viewBy;
	}

	public String getAssignment() {
		return assignment;
	}

	public void setAssignment(String assignment) {
		this.assignment = assignment;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public int getShowIndex() {
		return showIndex;
	}

	public void setShowIndex(int showIndex) {
		this.showIndex = showIndex;
	}

	public List<FlFulfilmentOrder> getFlFulfilmentOrderList() {
		return flFulfilmentOrderList;
	}

	public void setFlFulfilmentOrderList(
			List<FlFulfilmentOrder> flFulfilmentOrderList) {
		this.flFulfilmentOrderList = flFulfilmentOrderList;
	}

	private PagingDataModel<WorklistItemVO> taskListDataModel = new taskPagingDataModel();
	
	private PagingDataModel<WorklistItemVO> taskListMyTaskDataModel = new taskPagingMyTaskDataModel();
	
	private PagingDataModel<WorklistItemVO> taskListGroupDataModel = new taskPagingGroupDataModel();
	
	
	
	
	

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
			super("taskListId");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			System.out
					.println(">>>>>>>>>>>>>>>>>>>pageChangeListener<<<<<<<<<<<<<<<<<<<");
			IBpmSO bpmSO = null;
			try {
				System.out.println(">>>>>>>>>>>>>>>>>>getAssignment()="
						+ getAssignment());
				System.out.println(">>>>>>>.pageNo()="+getPageNo());
				System.out.println(">>>>>>>.pageSize()="+getPageSize());
				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);

				// String token = bpmSO.authenticate("roslan", "welcome1");
				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();

			   System.out.println("testing BPM auth******" + token);
				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED"); // TODO : ADD filter for state and set do variable
//				if(!getDocType().isEmpty()){
				  reqDTO.setDocType(getDocType());
//				}
												
				reqDTO.setViewBy(getViewBy());
				reqDTO.setAssignment(getAssignment());
				reqDTO.setCtxToken(token);
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				
				if (responseDTO.getWorklistVO() != null) {
					System.out.println("testing BPM******size="
							+ responseDTO.getWorklistVO().getWorklistItem()
									.size());
					System.out.println("testing BPM******total="
							+ responseDTO.getWorklistVO().getTotal());

					// setTaskList(responseDTO.getWorklistVO().getWorklistItem());

					if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
						for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							// s1 module
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							
							//sm module
							//worklistItemVO.setTitle(SmCommonUtil.getSmBpmTaskTitle(worklistItemVO.getTitle(),worklistItemVO.getProcess()));
							//sm module
						       if(worklistItemVO.getTitle().startsWith("lbl.sm")) {
						    		MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
						    		System.out.println("***** The document Id for the workflow is ****** "+worklistItemVO.getDocId());
									mofResDto = retriveCompanyNameByApplicationId(worklistItemVO.getDocId());
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   if(mofResDto.getCompanyName() != null){
						    		   	   sb.append(" ( ");
								    	   sb.append(mofResDto.getCompanyName());
								    	   sb.append(" ) ");					    	   
						    	   }
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }
						}
					}
					
					resultBean.setTotalRecord(responseDTO.getWorklistVO()
							.getTotal());
					resultBean.setResultList(responseDTO.getWorklistVO()
							.getWorklistItem());
				} else {
					resultBean.setTotalRecord(0);
					resultBean.setResultList(null);
				}
				return resultBean;
			} finally {
				bpmSO = null;
			}
		}

	}

	private MofRegistrationResDTO retriveCompanyNameByApplicationId(long applId) {

		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setApplId(applId);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveCompanyNameByApplId(reqDto);
			return resDTO;

		} catch (Exception e) {
			System.out.println("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}

	}
	private int calculateFinalOffSet(int currentPage) {

		int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;

		return offSet;
	}

	public Integer getTabActiveIndex() {
		return tabActiveIndex;
	}

	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}
	
	private class taskPagingMyTaskDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public taskPagingMyTaskDataModel() {
			super("taskListMyTaskId");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
		
			IBpmSO bpmSO = null;
			try {

				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);

				// String token = bpmSO.authenticate("roslan", "welcome1");
				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();

			   System.out.println("testing BPM auth******" + token);
				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED"); // TODO : ADD filter for state and set do variable

				reqDTO.setDocType(getDocType());

												
				reqDTO.setViewBy(getViewBy());
				
				if(getDocNo()!=null && getDocNo().trim().length()>0){
					reqDTO.setDocNumber(getDocNo());
				}
				
				if(getTaskDesc()!=null && getTaskDesc().trim().length()>0){
					reqDTO.setTaskTitle(getTaskDesc());
				}
				
				if(getSelectedOrgProfile()!=null){
					System.out.println("getSelectedOrgProfile()"+getSelectedOrgProfile().getOrgProfileId());
				}
				
				if(getSelectedStatusDesc()!=null && getSelectedStatusDesc().getPmStatus()!=null && getSelectedStatusDesc().getPmStatus().getStatusId()>0){
					System.out.println("getSelectedStatusDesc()"+getSelectedStatusDesc().getPmStatus().getStatusId().intValue());
					reqDTO.setStatus(getSelectedStatusDesc().getPmStatus().getStatusId().intValue());
				}
				
				
				reqDTO.setAssignment("MY");
				reqDTO.setCtxToken(token);
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				
				if (responseDTO.getWorklistVO() != null) {
					System.out.println("testing BPM******size="
							+ responseDTO.getWorklistVO().getWorklistItem()
									.size());
					System.out.println("testing BPM******total="
							+ responseDTO.getWorklistVO().getTotal());

					
					if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
						for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							// s1 module
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							
							//sm module
							//worklistItemVO.setTitle(SmCommonUtil.getSmBpmTaskTitle(worklistItemVO.getTitle(),worklistItemVO.getProcess()));
							//sm module
						       if(worklistItemVO.getTitle().startsWith("lbl.sm")) {
						    		MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
						    		System.out.println("***** The document Id for the workflow is ****** "+worklistItemVO.getDocId());
									mofResDto = retriveCompanyNameByApplicationId(worklistItemVO.getDocId());
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   if(mofResDto.getCompanyName() != null){
						    		   	   sb.append(" ( ");
								    	   sb.append(mofResDto.getCompanyName());
								    	   sb.append(" ) ");					    	   
						    	   }
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }
						       
						       
						       
						       if(worklistItemVO.getTitle().startsWith("lbl.cd")) {
						    	
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }
						}
					}
					
					resultBean.setTotalRecord(responseDTO.getWorklistVO()
							.getTotal());
					resultBean.setResultList(responseDTO.getWorklistVO()
							.getWorklistItem());
				} else {
					resultBean.setTotalRecord(0);
					resultBean.setResultList(null);
				}
				return resultBean;
			} finally {
				bpmSO = null;
			}
		}

	}

	public PagingDataModel<WorklistItemVO> getTaskListMyTaskDataModel() {
		return taskListMyTaskDataModel;
	}

	public void setTaskListMyTaskDataModel(
			PagingDataModel<WorklistItemVO> taskListMyTaskDataModel) {
		this.taskListMyTaskDataModel = taskListMyTaskDataModel;
	}
	
	
	
	private class taskPagingGroupDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public taskPagingGroupDataModel() {
			super("taskListGroupId");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
		
			IBpmSO bpmSO = null;
			try {

				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);

				// String token = bpmSO.authenticate("roslan", "welcome1");
				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();

			   System.out.println("testing BPM auth******" + token);
				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED"); // TODO : ADD filter for state and set do variable

				reqDTO.setDocType(getDocTypeGroup());

				if(getTaskDescGroup()!=null && getTaskDescGroup().trim().length()>0){
					reqDTO.setTaskTitle(getTaskDescGroup());
				}
				
				System.out.println("getDocNoGroup():"+getDocNoGroup());
				if(getDocNoGroup()!=null && getDocNoGroup().trim().length()>0){
					reqDTO.setDocNumber(getDocNoGroup());
				}
			
				if(getSelectedOrgProfileGroup()!=null){
					System.out.println("getOrgProfileId"+getSelectedOrgProfileGroup().getOrgProfileId());
				}
				
				if(getSelectedStatusDescAdvance()!=null && getSelectedStatusDescAdvance().getPmStatus()!=null && getSelectedStatusDescAdvance().getPmStatus().getStatusId()>0){
					System.out.println("getSelectedStatusDescAdvance"+getSelectedStatusDescAdvance().getPmStatus().getStatusId().intValue());
					reqDTO.setStatus(getSelectedStatusDescAdvance().getPmStatus().getStatusId().intValue());
				}
				
				reqDTO.setViewBy(getViewByGroup());
				
				//System.out.println("***status**"+getSelectedStatusDescAdvance().getStatusName());
				
				
				//reqDTO.setStatusList(statusList);
				reqDTO.setAssignment(BpmConstants.ASSIGNMENT_GROUP);
				reqDTO.setIsClaimed(false);
				reqDTO.setCtxToken(token);
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				
				if (responseDTO.getWorklistVO() != null) {
					System.out.println("testing BPM******size="
							+ responseDTO.getWorklistVO().getWorklistItem()
									.size());
					System.out.println("testing BPM******total="
							+ responseDTO.getWorklistVO().getTotal());

					
					if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
						for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							// s1 module
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							
							//sm module
							//worklistItemVO.setTitle(SmCommonUtil.getSmBpmTaskTitle(worklistItemVO.getTitle(),worklistItemVO.getProcess()));
							//sm module
						       if(worklistItemVO.getTitle().startsWith("lbl.sm")) {
						    		MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
						    		System.out.println("***** The document Id for the workflow is ****** "+worklistItemVO.getDocId());
									mofResDto = retriveCompanyNameByApplicationId(worklistItemVO.getDocId());
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   if(mofResDto.getCompanyName() != null){
						    		   	   sb.append(" ( ");
								    	   sb.append(mofResDto.getCompanyName());
								    	   sb.append(" ) ");					    	   
						    	   }
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }
						       
						       
						       if(worklistItemVO.getTitle().startsWith("lbl.cd")) {
						    	
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }
						       
						       
						       
						}
					}
					
					resultBean.setTotalRecord(responseDTO.getWorklistVO()
							.getTotal());
					resultBean.setResultList(responseDTO.getWorklistVO()
							.getWorklistItem());
				} else {
					resultBean.setTotalRecord(0);
					resultBean.setResultList(null);
				}
				return resultBean;
			} finally {
				bpmSO = null;
			}
		}

	}

	public PagingDataModel<WorklistItemVO> getTaskListGroupDataModel() {
		return taskListGroupDataModel;
	}

	public void setTaskListGroupDataModel(
			PagingDataModel<WorklistItemVO> taskListGroupDataModel) {
		this.taskListGroupDataModel = taskListGroupDataModel;
	}

	public String getViewByGroup() {
		return viewByGroup;
	}

	public void setViewByGroup(String viewByGroup) {
		this.viewByGroup = viewByGroup;
	}

	public String getDocTypeGroup() {
		return docTypeGroup;
	}

	public void setDocTypeGroup(String docTypeGroup) {
		this.docTypeGroup = docTypeGroup;
	}

	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public PmOrgProfile getSelectedOrgProfileGroup() {
		return selectedOrgProfileGroup;
	}

	public void setSelectedOrgProfileGroup(PmOrgProfile selectedOrgProfileGroup) {
		this.selectedOrgProfileGroup = selectedOrgProfileGroup;
	}

	public String getTaskDescGroup() {
		return taskDescGroup;
	}

	public void setTaskDescGroup(String taskDescGroup) {
		this.taskDescGroup = taskDescGroup;
	}

	public String getDocNoGroup() {
		return docNoGroup;
	}

	public void setDocNoGroup(String docNoGroup) {
		this.docNoGroup = docNoGroup;
	}

	public Boolean getClaim() {
		return claim;
	}

	public void setClaim(Boolean claim) {
		this.claim = claim;
	}

	public PmStatusDesc getSelectedStatusDesc() {
		return selectedStatusDesc;
	}

	public void setSelectedStatusDesc(PmStatusDesc selectedStatusDesc) {
		this.selectedStatusDesc = selectedStatusDesc;
	}

	public PmStatusDesc getSelectedStatusDescAdvance() {
		return selectedStatusDescAdvance;
	}

	public void setSelectedStatusDescAdvance(PmStatusDesc selectedStatusDescAdvance) {
		this.selectedStatusDescAdvance = selectedStatusDescAdvance;
	}

	public static Map getBpmMap() {

		return bpmMap;
	}

	public static void setBpmMap(Map bpmMap) {
		BPMTaskListManagedBean.bpmMap = bpmMap;
	}
	
	
	
	
	

}
