package my.ep.web.managedbean.bpm.errorlist;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constants.BpmConstants;
import my.ep.jaxb.error.data.ErrorDataType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "bpmErrorlistMB")
@SessionScoped
public class BPMErrorListManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<WorklistItemVO> errorList;
	private static Map bpmMap = null;
	private String errorMessageTitle;
	private String processName;
	private String[] action;
	private String errorId;
	private Integer tabActiveIndex = 0;
	private String errorName;
	private String tempLoginId;
	private String viewBy = "ALL";
	private String assignment = "ALL";
	private String docType = "";
	private String docNo;
	private String errorDesc;
	private String docNoGroup;
	private String errorDescGroup;
	private String payloadContent;
	private String bodyPayload;
	private int showIndex = 0;
	private Boolean claim = true;
	private List<SelectItem> docStatusList;
	private int docStatus;
	private long docId;
	private ErrorDataType errorDetails = new ErrorDataType();
	
	private PagingDataModel<WorklistItemVO> errorListDataModel = new errorPagingDataModel();



	/**
	 * @return the errorList
	 */
	public List<WorklistItemVO> getErrorList() {
		return errorList;
	}

	/**
	 * @param errorList the errorList to set
	 */
	public void setErrorList(List<WorklistItemVO> errorList) {
		this.errorList = errorList;
	}

	/**
	 * @return the bpmMap
	 */
	public static Map getBpmMap() {
		return bpmMap;
	}

	/**
	 * @param bpmMap the bpmMap to set
	 */
	public static void setBpmMap(Map bpmMap) {
		BPMErrorListManagedBean.bpmMap = bpmMap;
	}

	/**
	 * @return the errorMessageTitle
	 */
	public String getErrorMessageTitle() {
		return errorMessageTitle;
	}

	/**
	 * @param errorMessageTitle the errorMessageTitle to set
	 */
	public void setErrorMessageTitle(String errorMessageTitle) {
		this.errorMessageTitle = errorMessageTitle;
	}

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * @return the action
	 */
	public String[] getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String[] action) {
		this.action = action;
	}

	/**
	 * @return the errorId
	 */
	public String getErrorId() {
		return errorId;
	}

	/**
	 * @param errorId the errorId to set
	 */
	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

	/**
	 * @return the tabActiveIndex
	 */
	public Integer getTabActiveIndex() {
		return tabActiveIndex;
	}

	/**
	 * @param tabActiveIndex the tabActiveIndex to set
	 */
	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * @return the errorName
	 */
	public String getErrorName() {
		return errorName;
	}

	/**
	 * @param errorName the errorName to set
	 */
	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}

	/**
	 * @return the tempLoginId
	 */
	public String getTempLoginId() {
		return tempLoginId;
	}

	/**
	 * @param tempLoginId the tempLoginId to set
	 */
	public void setTempLoginId(String tempLoginId) {
		this.tempLoginId = tempLoginId;
	}

	/**
	 * @return the viewBy
	 */
	public String getViewBy() {
		return viewBy;
	}

	/**
	 * @param viewBy the viewBy to set
	 */
	public void setViewBy(String viewBy) {
		this.viewBy = viewBy;
	}

	/**
	 * @return the assignment
	 */
	public String getAssignment() {
		return assignment;
	}

	/**
	 * @param assignment the assignment to set
	 */
	public void setAssignment(String assignment) {
		this.assignment = assignment;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * @param docNo the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * @return the errorDesc
	 */
	public String getErrorDesc() {
		return errorDesc;
	}

	/**
	 * @param errorDesc the errorDesc to set
	 */
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	/**
	 * @return the docNoGroup
	 */
	public String getDocNoGroup() {
		return docNoGroup;
	}

	/**
	 * @param docNoGroup the docNoGroup to set
	 */
	public void setDocNoGroup(String docNoGroup) {
		this.docNoGroup = docNoGroup;
	}

	/**
	 * @return the errorDescGroup
	 */
	public String getErrorDescGroup() {
		return errorDescGroup;
	}

	/**
	 * @param errorDescGroup the errorDescGroup to set
	 */
	public void setErrorDescGroup(String errorDescGroup) {
		this.errorDescGroup = errorDescGroup;
	}

	/**
	 * @return the payload
	 */
	public String getPayloadContent() {
		return payloadContent;
	}

	/**
	 * @param payload the payload to set
	 */
	public void setPayloadContent(String payloadContent) {
		this.payloadContent = payloadContent;
	}
	
	/**
	 * @return the bodyPayload
	 */
	public String getBodyPayload() {
		return bodyPayload;
	}

	/**
	 * @param bodyPayload the bodyPayload to set
	 */
	public void setBodyPayload(String bodyPayload) {
		this.bodyPayload = bodyPayload;
	}

	/**
	 * @return the showIndex
	 */
	public int getShowIndex() {
		return showIndex;
	}

	/**
	 * @param showIndex the showIndex to set
	 */
	public void setShowIndex(int showIndex) {
		this.showIndex = showIndex;
	}

	/**
	 * @return the claim
	 */
	public Boolean getClaim() {
		return claim;
	}

	/**
	 * @param claim the claim to set
	 */
	public void setClaim(Boolean claim) {
		this.claim = claim;
	}

	/**
	 * @return the docStatusList
	 */
	public List<SelectItem> getDocStatusList() {
		return docStatusList;
	}

	/**
	 * @param docStatusList the docStatusList to set
	 */
	public void setDocStatusList(List<SelectItem> docStatusList) {
		this.docStatusList = docStatusList;
	}

	/**
	 * @return the docStatus
	 */
	public int getDocStatus() {
		return docStatus;
	}

	/**
	 * @param docStatus the docStatus to set
	 */
	public void setDocStatus(int docStatus) {
		this.docStatus = docStatus;
	}
	
	/**
	 * @return the docId
	 */
	public long getDocId() {
		return docId;
	}

	/**
	 * @param docId the docId to set
	 */
	public void setDocId(long docId) {
		this.docId = docId;
	}

	/**
	 * @return the errorDetails
	 */
	public ErrorDataType getErrorDetails() {
		return errorDetails;
	}

	/**
	 * @param errorDetails the errorDetails to set
	 */
	public void setErrorDetails(ErrorDataType errorDetails) {
		this.errorDetails = errorDetails;
	}

	/**
	 * @return the errorListDataModel
	 */
	public PagingDataModel<WorklistItemVO> getErrorListDataModel() {
		return errorListDataModel;
	}

	/**
	 * @param errorListDataModel the errorListDataModel to set
	 */
	public void setErrorListDataModel(
			PagingDataModel<WorklistItemVO> errorListDataModel) {
		this.errorListDataModel = errorListDataModel;
	}

	private class errorPagingDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public errorPagingDataModel() {
			super("errorListId");
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			System.out.println(">>>>>>>>>>>>>>>>>>>pageChangeListener<<<<<<<<<<<<<<<<<<<");

			IBpmSO bpmSO = null;
			
			

			try {
//				System.out.println(">>>>>>>>>>>>>>>>>>getAssignment()="
//						+ getAssignment());
//				System.out.println(">>>>>>>.pageNo()="+getPageNo());
//				System.out.println(">>>>>>>.pageSize()="+getPageSize());

				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);
				
				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState(BpmConstants.STATE_ASSIGNED);
				reqDTO.setAssignment(BpmConstants.ASSIGNMENT_ALL);
				reqDTO.setCtxToken(getToken());

				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				if(responseDTO.getWorklistVO().getWorklistItem() != null){
					for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()){
						setTitle(worklistItemVO.getTitle());
					}
				}
				
				if (responseDTO.getWorklistVO() != null) {
					System.out.println("testing BPM******size="
							+ responseDTO.getWorklistVO().getWorklistItem()
									.size());
					System.out.println("testing BPM******total="
							+ responseDTO.getWorklistVO().getTotal());
					

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
