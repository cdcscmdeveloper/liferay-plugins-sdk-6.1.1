package my.ep.web.managedbean.sm.bulkapplication;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationReqDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmBulkApplicationVO;

@ManagedBean(name = "bulkApplicationMB")
@SessionScoped
public class SmBulkApplicationManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7139722023908927672L;
	private List<WorklistItemVO> taskList;
	private String viewBy = "ALL";
	private String assignment = "ALL";
	private String docType = "";
	private PagingDataModel<SmBulkApplicationVO> taskListDataModel = new taskPagingDataModel();
	private List<SmBulkApplicationVO> selectedTasks;
	private List<PmStatusDesc> regStatusDescList;

	private class taskPagingDataModel extends
			PagingDataModel<SmBulkApplicationVO> {

		private static final long serialVersionUID = 1L;

		public taskPagingDataModel() {
			super("taskListId");
		}

		@Override
		protected ISearchResultBean<SmBulkApplicationVO> pageChangeListener(
				ISearchBean<SmBulkApplicationVO> searchBean) {
			IBpmSO bpmSO = null;
			List<SmBulkApplicationVO> smApplicationTaskList = new ArrayList<SmBulkApplicationVO>();
			SmBulkApplicationVO bulkApplicationVO = null;
			try {
				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);

				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();

				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED");
				reqDTO.setDocType(getDocType());
				reqDTO.setViewBy(getViewBy());
				reqDTO.setAssignment(getAssignment());
				reqDTO.setCtxToken(token);
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<SmBulkApplicationVO> resultBean = new SearchResultBean<SmBulkApplicationVO>();

				if (responseDTO.getWorklistVO() != null) {
					if (responseDTO.getWorklistVO().getWorklistItem() != null) {
						for (WorklistItemVO worklistItemVO : responseDTO
								.getWorklistVO().getWorklistItem()) {
							// sm module
							if (worklistItemVO.getTitle().startsWith("lbl.sm")) {
								SmBulkApplicationReqDTO bulkApplReqDTO = new SmBulkApplicationReqDTO();
								bulkApplReqDTO.setApplId(worklistItemVO
										.getDocId().longValue());
								SmAppl appl = SmServiceLookup
										.getSmBulkApplicationSO()
										.getApplicationByApplId(bulkApplReqDTO)
										.getAppl();
								String companyName = appl.getSmSupplier()
										.getCompanyName();
								StringBuilder sb = new StringBuilder();
								sb.append(ResourceBundleUtils.getMessage(
										worklistItemVO.getTitle(),
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										face.getUserLocale()));
								if (companyName != null) {
									sb.append(" ( ");
									sb.append(companyName);
									sb.append(" ) ");
								}
								worklistItemVO.setTitle(sb.toString());
								bulkApplicationVO = new SmBulkApplicationVO();
								bulkApplicationVO.setItem(worklistItemVO);
								bulkApplicationVO.setAppl(appl);
								bulkApplicationVO
										.setRegStatusDescList(SmApplicationUtil
												.getInstance()
												.retrieveRegistrationStatusList(
														appl.getSupplierType(),
														appl.getApplType()));
								smApplicationTaskList.add(bulkApplicationVO);
							}
						}
					}

					resultBean.setTotalRecord(smApplicationTaskList.size());
					resultBean.setResultList(smApplicationTaskList);
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

	public List<WorklistItemVO> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<WorklistItemVO> taskList) {
		this.taskList = taskList;
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

	public PagingDataModel<SmBulkApplicationVO> getTaskListDataModel() {
		return taskListDataModel;
	}

	public void setTaskListDataModel(
			PagingDataModel<SmBulkApplicationVO> taskListDataModel) {
		this.taskListDataModel = taskListDataModel;
	}

	public List<SmBulkApplicationVO> getSelectedTasks() {
		return selectedTasks;
	}

	public void setSelectedTasks(List<SmBulkApplicationVO> selectedTasks) {
		this.selectedTasks = selectedTasks;
	}

	public List<PmStatusDesc> getRegStatusDescList() {
		return regStatusDescList;
	}

	public void setRegStatusDescList(List<PmStatusDesc> regStatusDescList) {
		this.regStatusDescList = regStatusDescList;
	}
}
