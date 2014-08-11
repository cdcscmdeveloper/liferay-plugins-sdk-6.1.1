package my.ep.web.managedbean.bpm.tasklist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.constants.SCProcessNameConst;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

@ManagedBean(name = "bpmTasklistMB")
@SessionScoped
public class BPMTaskListManagedBean extends BaseManagedBean {

	private List<WorklistItemVO> taskList;
	private List<FlFulfilmentOrder> flFulfilmentOrderList;
	private Map bpmMap = new HashMap();
	private String processName;
	private String[] action;

	private String taskId;

	// private String status="ASSIGNED";

	private String taskName;

	private String tempLoginId; // TODO: temp and to be removed

	private String viewBy = "ALL";

	private String assignment = "ALL";
	
	private String docType = "";

	private int showIndex = 0;
	
	public List<WorklistItemVO> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<WorklistItemVO> taskList) {
		this.taskList = taskList;
	}

	public Map getBpmMap() {
		return bpmMap;
	}

	public void setBpmMap(Map bpmMap) {
		this.bpmMap = bpmMap;
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
				String token = face.getBPMAuthenticationCtxToken();

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

	private int calculateFinalOffSet(int currentPage) {

		int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;

		return offSet;
	}

}
