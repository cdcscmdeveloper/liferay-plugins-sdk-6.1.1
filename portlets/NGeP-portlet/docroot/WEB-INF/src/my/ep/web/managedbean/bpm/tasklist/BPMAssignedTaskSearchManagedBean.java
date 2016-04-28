package my.ep.web.managedbean.bpm.tasklist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.bpm.tasksearch.BPMAssignedTaskSearchBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "bpmAssignedTaskSearchMB")
@SessionScoped
public class BPMAssignedTaskSearchManagedBean extends BaseManagedBean {

	private static Logger log = Logger.getLogger(BPMAssignedTaskSearchBackingBean.class);

	private static final long serialVersionUID = 1L;

	private String docNo;
	private String docType;
	private int docStatus;
	private Date searchFromCreatedDate;
	private Date searchToCreatedDate;
	private List<PmParameterDesc> docTypeList;
	private List<SelectItem> docStatusList;

	private IPagingDataModel<WorklistItemVO> taskListDataModel = new TaskListPagingDataModel();

	private class TaskListPagingDataModel extends	PagingDataModel<WorklistItemVO> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public TaskListPagingDataModel() {
			super("taskListTableId", true);
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<WorklistItemVO> searchBean(mandatory))
		 * @return ISearchResultBean<WorklistItemVO>
		 */
		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {

			log.info("*****************pageChangeListener****************");
			log.info(" *******" + "getDocNo: " + getDocNo() + " *******");
			log.info(" *******" + "getDocType: "+ getDocType() + " *******");
			log.info(" *******" + "getDocStatus: "+ getDocStatus() + " *******");
			log.info(" *******" + "getSearchFromCreatedDate: "+ getSearchFromCreatedDate() + " *******");
			log.info(" *******" + "getSearchToCreatedDate: "+ getSearchToCreatedDate() + " *******");
			log.info("***************************************************");

			if ((getDocNo() == null && getDocType() == null) ||
				(StringUtils.isBlank(getDocNo()) && getDocType() == null) ||
				(getDocNo() == null && getDocType() == null && getSearchFromCreatedDate() == null && getSearchToCreatedDate() == null)) {
				log.info("******EmptyResultBean******");
				return new SearchResultBean<WorklistItemVO>();
			}
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
			reqDTO.setOffset((getPageNo() - 1) * PagingConst.ROWS_PER_PAGE);
			reqDTO.setState("ASSIGNED");
			reqDTO.setAssignment("ALL");

			if (getDocNo() != null && !(getDocNo().trim().isEmpty())) {
				reqDTO.setDocNumber(getDocNo().trim());
				reqDTO.setDocType(null);
				reqDTO.setStatus(0);
				reqDTO.setAfterDate(null);
				reqDTO.setBeforeDate(null);
				
			} else if (getDocType() != null) {
				reqDTO.setDocType(getDocType());
//				reqDTO.setStatus(20100);
				if (getDocStatus() != 0) {
					reqDTO.setStatus(getDocStatus());
				}
				if (getSearchFromCreatedDate() != null) {
					Calendar afterDate = Calendar.getInstance();
				    afterDate.setTime(getSearchFromCreatedDate()); 
					reqDTO.setAfterDate(afterDate);
				}
				if (getSearchToCreatedDate() != null) {
					Calendar beforeDate = Calendar.getInstance();
					beforeDate.setTime(getSearchToCreatedDate());
					beforeDate.add(Calendar.DATE, 1);
					reqDTO.setBeforeDate(beforeDate);
				}
			}

			log.info("*******reqDTO*******");
			log.info("*******" + "reqDTO.getLimit(): " + reqDTO.getLimit() + " *******");
			log.info("*******" + "getPageNo(): " + getPageNo() + " *******");
			log.info("*******" + "reqDTO.getOffset(): " + reqDTO.getOffset() + " *******");
			log.info("*******" + "reqDTO.getState(): " + reqDTO.getState() + " *******");
			log.info("*******" + "reqDTO.getAssignment(): " + reqDTO.getAssignment() + " *******");

			log.info("*******" + "reqDTO.getDocNumber(): " + reqDTO.getDocNumber() + " *******");
			log.info("*******" + "reqDTO.getDocType(): " + reqDTO.getDocType() + " *******");
			log.info("*******" + "reqDTO.getStatus(): " + reqDTO.getStatus() + " *******");
			log.info("*******" + "reqDTO.getAfterDate(): " + reqDTO.getAfterDate() + " *******");
			log.info("*******" + "reqDTO.getBeforeDate(): " + reqDTO.getBeforeDate() + " *******");
			log.info("*********************************************************");

//			BpmResponseDTO resDTO = bpmSO.getWorklistItems(reqDTO);
			BpmResponseDTO resDTO = bpmSO.getTasks(reqDTO);

			log.info("*******resDTO*******");
			log.info("*******" + "resDTO.isSuccessful(): " + resDTO.isSuccessful() + " *******");
			log.info("*******" + "resDTO.getWorklistVO().getTotal(): " + resDTO.getWorklistVO().getTotal() + " *******");
			log.info("*******" + "resDTO.getWorklistVO().getWorklistItem().size(): "+ resDTO.getWorklistVO().getWorklistItem().size() + " *******");
			log.info("*********************************************************");

			SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();

			resultBean.setTotalRecord(resDTO.getWorklistVO().getTotal());
			resultBean.setResultList(resDTO.getWorklistVO().getWorklistItem());

			return resultBean;
		}
	}

	public String getDocNo() {
		return docNo;
	}
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}

	public int getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(int docStatus) {
		this.docStatus = docStatus;
	}

	public Date getSearchFromCreatedDate() {
		return searchFromCreatedDate;
	}
	public void setSearchFromCreatedDate(Date searchFromCreatedDate) {
		this.searchFromCreatedDate = searchFromCreatedDate;
	}

	public Date getSearchToCreatedDate() {
		return searchToCreatedDate;
	}
	public void setSearchToCreatedDate(Date searchToCreatedDate) {
		this.searchToCreatedDate = searchToCreatedDate;
	}

	public List<PmParameterDesc> getDocTypeList() {
		if(docTypeList==null)
			docTypeList = new ArrayList<PmParameterDesc>();
			return docTypeList;
	}
	public void setDocTypeList(List<PmParameterDesc> docTypeList) {
		this.docTypeList = docTypeList;
	}

	public List<SelectItem> getDocStatusList() {
		if(docStatusList==null)
			docStatusList = new ArrayList<SelectItem>();
			return docStatusList;
	}
	public void setDocStatusList(List<SelectItem> docStatusList) {
		this.docStatusList = docStatusList;
	}

	public IPagingDataModel<WorklistItemVO> getTaskListDataModel() {
		return taskListDataModel;
	}
	public void setTaskListDataModel(IPagingDataModel<WorklistItemVO> taskListDataModel) {
		this.taskListDataModel = taskListDataModel;
	}

	public void resetTaskListDataModel() {
		this.taskListDataModel = new TaskListPagingDataModel();
	}
}
