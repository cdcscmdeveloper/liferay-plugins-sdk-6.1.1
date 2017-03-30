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
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.BpmConstants;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.PagingConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmOverlapCategoryVO;

import org.apache.log4j.Logger;

@ManagedBean(name = "bulkApplicationMB")
@SessionScoped
public class SmBulkApplicationManagedBean extends BaseManagedBean {

	private static Logger log = Logger
			.getLogger(SmBulkApplicationManagedBean.class);
	private static final long serialVersionUID = -7139722023908927672L;
	private List<WorklistItemVO> taskList;
	private String viewBy = "ALL";
	private String assignment = "ALL";
	private String docType = "";
	private PagingDataModel<SmBulkApplicationVO> claimTaskListDataModel = new ClaimTaskPagingDataModel();
	private PagingDataModel<SmBulkApplicationVO> unClaimTaskListDataModel = new UnClaimTaskPagingDataModel();
	private List<SmBulkApplicationVO> selectedTasks;
	private List<SmBulkApplicationVO> selectedUnclaimTasks;
	private List<SmBulkApplicationVO> initSelectedTasks;
	private List<PmStatusDesc> regStatusDescList;
	private List<SmAppl> failedSubmittedList;
	private List<SmBulkApplOverlapCatVO> selectedOverlapCategoryVOList;
	private List<PmReasonDesc> reasonDescList;
	private String maxBulkApprovalMsg;
	private int maxBulkApprovalCount;
	private String page;

	private class ClaimTaskPagingDataModel extends
			PagingDataModel<SmBulkApplicationVO> {

		private static final long serialVersionUID = 1L;

		public ClaimTaskPagingDataModel() {
			super("claimTaskListId");
		}

		@Override
		protected ISearchResultBean<SmBulkApplicationVO> pageChangeListener(
				ISearchBean<SmBulkApplicationVO> searchBean) {
			List<SmBulkApplicationVO> smApplicationTaskList = new ArrayList<SmBulkApplicationVO>();
			try {

				BpmRequestDTO reqDTO = getBpmReqDTO();
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setAssignment(BpmConstants.ASSIGNMENT_MY);
				BpmResponseDTO responseDTO = SmServiceLookup.getBpmSO()
						.getWorklistItems(reqDTO);
				SearchResultBean<SmBulkApplicationVO> resultBean = new SearchResultBean<SmBulkApplicationVO>();

				if (responseDTO.getWorklistVO() != null) {
					if (responseDTO.getWorklistVO().getWorklistItem() != null) {
						SmBulkApplicationVO bulkAppl = null;
						for (WorklistItemVO worklistItemVO : responseDTO
								.getWorklistVO().getWorklistItem()) {
							if (worklistItemVO.isClaimed()) {
								bulkAppl = getBulkApplicationVO(worklistItemVO);
								if (bulkAppl != null) {
									smApplicationTaskList
											.add(getBulkApplicationVO(worklistItemVO));
								}
							}
						}
					}

					resultBean.setTotalRecord(responseDTO.getWorklistVO()
							.getTotal());
					resultBean.setResultList(smApplicationTaskList);
				} else {
					resultBean.setTotalRecord(0);
					resultBean.setResultList(null);
				}
				return resultBean;
			} finally {

			}
		}

	}

	private class UnClaimTaskPagingDataModel extends
			PagingDataModel<SmBulkApplicationVO> {

		private static final long serialVersionUID = 1L;

		public UnClaimTaskPagingDataModel() {
			super("unClaimTaskListId");
		}

		@Override
		protected ISearchResultBean<SmBulkApplicationVO> pageChangeListener(
				ISearchBean<SmBulkApplicationVO> searchBean) {
			List<SmBulkApplicationVO> smApplicationTaskList = new ArrayList<SmBulkApplicationVO>();
			try {

				BpmRequestDTO reqDTO = getBpmReqDTO();
				reqDTO.setAssignment(BpmConstants.ASSIGNMENT_GROUP);
				reqDTO.setIsClaimed(false);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				BpmResponseDTO responseDTO = SmServiceLookup.getBpmSO()
						.getWorklistItems(reqDTO);
				SearchResultBean<SmBulkApplicationVO> resultBean = new SearchResultBean<SmBulkApplicationVO>();

				if (responseDTO.getWorklistVO() != null) {
					if (responseDTO.getWorklistVO().getWorklistItem() != null) {
						SmBulkApplicationVO bulkAppl = null;
						for (WorklistItemVO worklistItemVO : responseDTO
								.getWorklistVO().getWorklistItem()) {
							if (!worklistItemVO.isClaimed()) {
								bulkAppl = getBulkApplicationVO(worklistItemVO);
								if (bulkAppl != null) {
									smApplicationTaskList
											.add(getBulkApplicationVO(worklistItemVO));
								}
							}
						}
					}

					resultBean.setTotalRecord(responseDTO.getWorklistVO()
							.getTotal());
					resultBean.setResultList(smApplicationTaskList);
				} else {
					resultBean.setTotalRecord(0);
					resultBean.setResultList(null);
				}
				return resultBean;
			} finally {

			}
		}

	}

	private BpmRequestDTO getBpmReqDTO() {
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setLimit(5);
		reqDTO.setState("ASSIGNED");
		reqDTO.setDocType(getDocType());
		reqDTO.setViewBy(getViewBy());
		reqDTO.setAssignment(getAssignment());
		reqDTO.setCtxToken(token);
		reqDTO.setDocType("SR");
		return reqDTO;
	}

	private SmBulkApplicationVO getBulkApplicationVO(
			WorklistItemVO worklistItemVO) {
		SmBulkApplicationVO bulkApplicationVO = null;
		if (worklistItemVO.getDocType() != null
				&& worklistItemVO.getDocType().equalsIgnoreCase("SR")) {
			FacesScopes face = new FacesScopes();
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			mofReqDTO.setApplId(worklistItemVO.getDocId().longValue());
			SmAppl appl = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmApplicationDetailsById(mofReqDTO).getSmAppl();
			SmApplSectionReview poRev = (SmApplSectionReview) EntityHelper
					.getEntityBySinglePropertyFromList(
							SupplierManagementConst.SECTION_REV_ROLE_PO,
							appl.getSmApplSectionReviews(), "roleType");

			if (appl != null) {
				String companyName = appl.getSmSupplier().getCompanyName();
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
				bulkApplicationVO.setTaskId(worklistItemVO.getTaskId());
				bulkApplicationVO.setTaskName(worklistItemVO.getTaskName());
				bulkApplicationVO.setCreateDate(worklistItemVO.getCreateDate());
				bulkApplicationVO.setExpirationDate(worklistItemVO
						.getExpirationDate());
				bulkApplicationVO.setReceivedDate(poRev.getChangedDate());
				bulkApplicationVO.setTitle(worklistItemVO.getTitle());
				bulkApplicationVO.setAppl(appl);
				if (poRev != null) {
					bulkApplicationVO.setPo(SmCommonUtil.getUserByUserId(poRev
							.getChangedBy()));
				}
				bulkApplicationVO.setRegStatusDescList(SmApplicationUtil
						.getInstance().retrieveRegistrationStatusList(
								appl.getSupplierType(), appl.getApplType()));
				bulkApplicationVO.setHqFacility(SmApplicationUtil
						.getHqFacilityByAppl(appl));
				PmStatusDesc currentStatusDesc = SmApplicationUtil
						.getCurrRegStatusDesc(appl);
				if (currentStatusDesc == null) {
					currentStatusDesc = SmApplicationUtil
							.getDefaultRegStatusDesc(bulkApplicationVO
									.getHqFacility(), appl.getSmSupplier()
									.isBumi());
				}
				bulkApplicationVO.setApprovedStatusId(currentStatusDesc
						.getPmStatus().getStatusId());
				bulkApplicationVO.setCurrRegStatus(currentStatusDesc
						.getStatusName());

				// set overlap category alert
				if (appl != null
						&& appl.getApplType().equals(
								ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)) {
					for (SmSupplierCategory cat : appl
							.getSmSupplierCategories()) {
						if (cat.isOverLapCategory()
								&& (cat.getRecordStatusFlg()
										.compareTo(
												SupplierManagementConst.APPROVED_RECORD_STATUS) == 0 || cat
										.getRecordStatusFlg()
										.compareTo(
												SupplierManagementConst.PENDING_ACT_STATUS) == 0)) {
							bulkApplicationVO.setOverlappedCategory(true);
							break;
						}
					}
				}
				
				PmState state = appl.getSmCompanyBasic()
						.getSmCompanyAddresses().get(0).getSmAddress()
						.getPmState();
				bulkApplicationVO.setPmZone(SmApplicationUtil
						.retrieveZoneByStateId(state.getStateId()));
				if (appl.getSupportingDocMode().equalsIgnoreCase(
						SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
					bulkApplicationVO.setOnlineDoc(true);
				} else {
					bulkApplicationVO.setOnlineDoc(false);
				}
				bulkApplicationVO.setPaymentReqd(getPaymentMode(appl));
				bulkApplicationVO
						.setPendingApproveCategories(new ArrayList<SmSupplierCategory>());
				for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
					if (cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0
							|| cat.getRecordStatusFlg()
									.compareTo(
											SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
						bulkApplicationVO.getPendingApproveCategories()
								.add(cat);
					}
				}
			}
		}
		return bulkApplicationVO;
	}

	private int calculateFinalOffSet(int currentPage) {
		int offSet = (currentPage - 1) * 5;
		return offSet;
	}

	private boolean getPaymentMode(SmAppl smAppl) {
		boolean payReq = false;
		if (smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				|| smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			payReq = true;
		}
		return payReq;
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

	public PagingDataModel<SmBulkApplicationVO> getClaimTaskListDataModel() {
		return claimTaskListDataModel;
	}

	public void setClaimTaskListDataModel(
			PagingDataModel<SmBulkApplicationVO> claimTaskListDataModel) {
		this.claimTaskListDataModel = claimTaskListDataModel;
	}

	public PagingDataModel<SmBulkApplicationVO> getUnClaimTaskListDataModel() {
		return unClaimTaskListDataModel;
	}

	public void setUnClaimTaskListDataModel(
			PagingDataModel<SmBulkApplicationVO> unClaimTaskListDataModel) {
		this.unClaimTaskListDataModel = unClaimTaskListDataModel;
	}

	public List<SmAppl> getFailedSubmittedList() {
		return failedSubmittedList;
	}

	public void setFailedSubmittedList(List<SmAppl> failedSubmittedList) {
		this.failedSubmittedList = failedSubmittedList;
	}

	public int getMaxBulkApprovalCount() {
		return maxBulkApprovalCount;
	}

	public void setMaxBulkApprovalCount(int maxBulkApprovalCount) {
		this.maxBulkApprovalCount = maxBulkApprovalCount;
	}

	public List<SmBulkApplicationVO> getInitSelectedTasks() {
		return initSelectedTasks;
	}

	public void setInitSelectedTasks(List<SmBulkApplicationVO> initSelectedTasks) {
		this.initSelectedTasks = initSelectedTasks;
	}

	public List<SmBulkApplicationVO> getSelectedUnclaimTasks() {
		return selectedUnclaimTasks;
	}

	public void setSelectedUnclaimTasks(
			List<SmBulkApplicationVO> selectedUnclaimTasks) {
		this.selectedUnclaimTasks = selectedUnclaimTasks;
	}

	public String getMaxBulkApprovalMsg() {
		return maxBulkApprovalMsg;
	}

	public void setMaxBulkApprovalMsg(String maxBulkApprovalMsg) {
		this.maxBulkApprovalMsg = maxBulkApprovalMsg;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public List<SmBulkApplOverlapCatVO> getSelectedOverlapCategoryVOList() {
		return selectedOverlapCategoryVOList;
	}

	public void setSelectedOverlapCategoryVOList(
			List<SmBulkApplOverlapCatVO> selectedOverlapCategoryVOList) {
		this.selectedOverlapCategoryVOList = selectedOverlapCategoryVOList;
	}

	public List<PmReasonDesc> getReasonDescList() {
		return reasonDescList;
	}

	public void setReasonDescList(List<PmReasonDesc> reasonDescList) {
		this.reasonDescList = reasonDescList;
	}
}
