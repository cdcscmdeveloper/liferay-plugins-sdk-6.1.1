package my.ep.web.managedbean.bpm.tasklist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.bean.application.NgepApplicationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.access.application.IAccessApplSO;
import my.ep.biz.service.pm.common.kpi.IPmKpiSO;
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO; //#9133
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.bpm.vo.WorklistVO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.KPICodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.constants.BpmConstants;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.FLProcessNameConst;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.dto.pm.access.application.AccessApplResDTO;
import my.ep.dto.pm.common.kpi.KpiReqDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmKPIValue;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault; //#9133
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.sm.SmUserUtil;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "bpmTasklistMB")
@SessionScoped
public class BPMTaskListManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BPMTaskListManagedBean.class);
	// Refactor: #12177 - Start
	/*private static final Integer SQ_TIME_VALIDITY = Integer.valueOf(EpPropertiesUtils.getInstance()
			.getProperty("sqTimeValidity"));*/
	// Refactor: #12177 - End
	
	private List<WorklistItemVO> taskList;
	private List<FlFulfilmentOrder> flFulfilmentOrderList;
	private static Map<?, ?> bpmMap = null;
	private String processName;
	private String[] action;
	private String taskId;
//	private String taskIdPair;/*#31422-basri 190821*/
	private Integer tabActiveIndex = 0;
	private String taskName;
	private String tempLoginId; 
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
	
	private List<SelectItem> docStatusList;
	private int docStatus;
	
	private List<SelectItem> docStatusListGroup;
	private int docStatusGroup;
	Integer loiPreparationDays = null;
	private boolean sdoWithMultipleRoles;
	private String loginIc;
	
	//start added for #24968
	private FlFulfilmentRequest fulfilmentRequest;
	private FlFulfilmentNote fulfilmentNote = new FlFulfilmentNote();
	//end added for #24968
	
	// 12177 - Start
	private Calendar expirationDateSq;
	// 12177 - End
	
	//Task Sorting
	private String sort_by;
	private boolean ascending = true;
	private String sort_byGroup;
	private boolean ascendingGroup = true;
	private boolean fromSearchBttn = false;
	private Date  myTaskCreatedDateFrom;
	private Date  myTaskCreatedDateTo;
	private Date groupTaskCreatedDateFrom;
	private Date  groupTaskCreatedDateTo;
	private String roleType;
	
	//#32304 - start
	private List<SmAppl> cancelledBumiApplLs;
	private PagingDataModel<SmAppl> bumiListDataModel = new BumiPagingDataModel();
	private Boolean showCancelledBumiApplList;
	//#32304 - end

	public List<WorklistItemVO> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<WorklistItemVO> taskList) {
		this.taskList = taskList;
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
	
	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public String getLoginIc() {
		return loginIc;
	}
	
	public void setLoginIc(String loginIc) {
		this.loginIc = loginIc;
	}

	private PagingDataModel<WorklistItemVO> taskListDataModel = new taskPagingDataModel();
	
	private PagingDataModel<WorklistItemVO> taskListMyTaskDataModel = new taskPagingMyTaskDataModel();
	
	private PagingDataModel<WorklistItemVO> taskListGroupDataModel = new taskPagingGroupDataModel();
	
	// #24714
	private PagingDataModel<WorklistItemVO> taskListBothDataModel = new taskPagingBothDataModel();

	public PagingDataModel<WorklistItemVO> getTaskListDataModel() {
		return taskListDataModel;
	}

	public void setTaskListDataModel(PagingDataModel<WorklistItemVO> taskListDataModel) {
		this.taskListDataModel = taskListDataModel;
	}

	private class taskPagingDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;
		private final Logger log = EpLogger.getLogger(taskPagingDataModel.class);
		
		public taskPagingDataModel() {
			super("taskListId");
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			log.info(">>>>>>>>>>>>>>>>>>>pageChangeListener<<<<<<<<<<<<<<<<<<<");
			IBpmSO bpmSO = null;
			try {

				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);

				// String token = bpmSO.authenticate("roslan", "welcome1");
				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();
				
				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED"); 
//				if(!getDocType().isEmpty()){
				  reqDTO.setDocType(getDocType());
//				}
												
				reqDTO.setViewBy(getViewBy());
				reqDTO.setAssignment(getAssignment());
				reqDTO.setCtxToken(token);
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				
				if (responseDTO.getWorklistVO() != null) {
					// setTaskList(responseDTO.getWorklistVO().getWorklistItem());

					if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
						for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							if (worklistItemVO.getTitle().matches("<")) {
								worklistItemVO.setTitle(worklistItemVO.getTitle().replaceAll("<", "&#60;"));
							}
							// s1 module
							if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE_DRAFT)) {
								ScPrInquiry scPrInquiry = retrivePrInquiryByPrInquiryNo(worklistItemVO.getDocNumber());
								if (scPrInquiry != null) {
									worklistItemVO.setTitle(scPrInquiry.getTitle());
								}
							} else if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							} else if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							} else if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							} else if(worklistItemVO.getTitle().startsWith("lbl.sm")) {
								// sm module
								// worklistItemVO.setTitle(SmCommonUtil.getSmBpmTaskTitle(worklistItemVO.getTitle(),worklistItemVO.getProcess()));
								// sm module
								MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
								mofResDto = retriveCompanyNameByApplicationId(worklistItemVO.getDocId());
								StringBuilder sb = new StringBuilder();
								sb.append(ResourceBundleUtils.getMessage(
										worklistItemVO.getTitle(),
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										face.getUserLocale()));
								if (mofResDto.getCompanyName() != null) {
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


	private Integer retrieveKpiValueByModuleCodeAndKpiCode(String moduleCode, String kpiCode) {
		IPmKpiSO pmKpiSO = null;

		try {
			pmKpiSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IPmKpiSO.class);
			KpiReqDTO reqDTO = new KpiReqDTO();
			reqDTO.setModuleCode(moduleCode);
			reqDTO.setKpiCode(kpiCode);
			PmKPIValue pmKPIValue = pmKpiSO.retrieveKpiValueByModuleCodeAndKpiCode(reqDTO).getKpiValue();
			if (pmKPIValue != null) {
				return pmKPIValue.getKpiValue();
			}

		} finally {
			pmKpiSO = null;
		}

		return null;
	}
	
	private MofRegistrationResDTO retriveCompanyNameByApplicationId(long applId) {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setApplId(applId);
			MofRegistrationResDTO resDTO = mofRegistrationSO.retrieveCompanyNameByApplId(reqDto);
			return resDTO;

		} catch (Exception e) {
			//System.out.println("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}
	}
	
	private ScPrInquiry retrivePrInquiryByPrInquiryNo(String prInquiryNo) {
		//CR 28766: Static Code Analysis Start
		//ScPrInquiry scPrInquiry = getPrInquirySO().retrivePrInquiryByPrInquiryNo(prInquiryNo);
		//return scPrInquiry;
		return getPrInquirySO().retrivePrInquiryByPrInquiryNo(prInquiryNo);
		//CR 28766: Static Code Analysis End
	}

	private IPrInquirySO getPrInquirySO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
	}
	
	//#9133 - start
	private ScPurchaseInDefault retrivePurchaseInDefaultByPdNo(String pdNo) {
		return getPurchaseInDefaultSO().retrivePurchaseInDefaultByPdNo(pdNo);
	}
	
	private IPurchaseInDefaultSO getPurchaseInDefaultSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
	}
	//#9133 - end

	private int calculateFinalOffSet(int currentPage) {
		//CR 28766: Static Code Analysis Start
		//int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;
		//return offSet;
		return (currentPage - 1) * PagingConst.ROWS_PER_PAGE;
		//CR 28766: Static Code Analysis End
	}

	public Integer getTabActiveIndex() {
		return tabActiveIndex;
	}

	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}
	
	private class taskPagingMyTaskDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;
		private final Logger log = EpLogger.getLogger(taskPagingMyTaskDataModel.class);

		public taskPagingMyTaskDataModel() {
			super("taskListMyTaskId");
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(ISearchBean<WorklistItemVO> searchBean) {
		
			IBpmSO bpmSO = null;
			try {
				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();
				
				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED"); 
				reqDTO.setDocType(getDocType());
	
				reqDTO.setViewBy(getViewBy());

				if(getDocNo()!=null && getDocNo().trim().length()>0){
					reqDTO.setDocNumber(getDocNo());
				}
				
				if(getTaskDesc()!=null && getTaskDesc().trim().length()>0){
					reqDTO.setTaskTitle(getTaskDesc().replaceAll("<", "&#60;"));
				}
				
				if(getDocStatus()>0){
					//33849 - Start - Need to set to PA_PENDING_PAYMENT_MATCH because status in SOA for POCO Pending Payment Match is 46000
					if(getDocStatus()== FulfilmentStatusConst.CO_PENDING_PAYMENT_MATCH.intValue() 
							|| getDocStatus()== FulfilmentStatusConst.PO_PENDING_PAYMENT_MATCH.intValue()){
						reqDTO.setStatus(FulfilmentStatusConst.PA_PENDING_PAYMENT_MATCH.intValue());
					}else{
						reqDTO.setStatus(getDocStatus());
					}
					//33849 - End
				}
				
				//Start - Task list sorting n search criteria date from and date to by Najmi 
				//29810 - START
				log.info("getSort_by() = "+getSort_by());
				
				if (isFromSearchBttn()) {
					if (getSort_by() != null && !getSort_by().equals("")) {
						
						if (getSort_by().equalsIgnoreCase(BpmConstants.SORT_BY_DOCNUMBER)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_DOCNUMBER);
						} else if (getSort_by().equalsIgnoreCase(BpmConstants.SORT_BY_DOCTYPE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_DOCTYPE);
						} else if (getSort_by().equalsIgnoreCase(BpmConstants.SORT_BY_TITLE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_TITLE);
						} else if (getSort_by().equalsIgnoreCase(BpmConstants.SORT_BY_CREATEDATE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_CREATEDATE);
						} else if (getSort_by().equalsIgnoreCase(BpmConstants.SORT_BY_STATUS)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_STATUS);
						} else if (getSort_by().equalsIgnoreCase(BpmConstants.SORT_BY_RECEIVEDDATE)){
							reqDTO.setSortBy(BpmConstants.SORT_BY_RECEIVEDDATE);
						} else if (getSort_by().equalsIgnoreCase(BpmConstants.SORT_BY_DOCSTATUS)){
							reqDTO.setSortBy(BpmConstants.SORT_BY_STATUS);
						}					
					}
				} else {
					if (this.getSortField() != null) {
						if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_DOCNUMBER)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_DOCNUMBER);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_DOCTYPE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_DOCTYPE);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_TITLE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_TITLE);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_CREATEDATE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_CREATEDATE);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_STATUS)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_STATUS);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_RECEIVEDDATE)){
							reqDTO.setSortBy(BpmConstants.SORT_BY_RECEIVEDDATE);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_DOCSTATUS)){
							reqDTO.setSortBy(BpmConstants.SORT_BY_STATUS);
						}
					}
				}				 			
				
				reqDTO.setIsAsc(isAscending());
				
				if (getMyTaskCreatedDateTo() != null) {
					getMyTaskCreatedDateTo().setHours(23);
					getMyTaskCreatedDateTo().setMinutes(59);
				}
				
				log.info("date from = "+getMyTaskCreatedDateFrom() + " date to = "+getMyTaskCreatedDateTo());
				
				reqDTO.setCreatedDateFrom(getMyTaskCreatedDateFrom());
				reqDTO.setCreatedDateTo(getMyTaskCreatedDateTo());
				
				//End - Task list sorting n search criteria date from and date to by Najmi 
				//29810 - END
				
				reqDTO.setAssignment(BpmConstants.ASSIGNMENT_MY);
				reqDTO.setCtxToken(token);
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				
				if (responseDTO.getWorklistVO() != null) {
					
					if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
						//	Enhancement #28766 - Start
						//	boolean viewGlpFlag = viewGlpFlag();
						//	Enhancement #28766 - End
						for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							try{
								processWorklistItemByTitle(worklistItemVO, face);
								
								processWorklistItemByTaskName(worklistItemVO, face);
								
								log.info("receive date " +worklistItemVO.getReceivedDate());
								
								//CR 28766: Static Code Analysis Start
								/*
						       if(worklistItemVO.getTaskName().equals(CTProcessNameConst.REVIEW_CIRCULAR_CANCELLATION_RQUEST_HT_NAME)){
						    	   if (worklistItemVO.getDocStatus() != null && (ContractStatusConst.CR_PENDING_APPROVAL.intValue() == worklistItemVO.getDocStatus().intValue() ||  ContractStatusConst.CR_PENDING_REVISION_APPROVAL.intValue() == worklistItemVO.getDocStatus().intValue() )) {
						    		   worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.ct.review.circular.cancellation", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   }
						       }
						       if(worklistItemVO.getTaskName().equals(CTProcessNameConst.REVISE_CIRCULAR_CANCELLATION_RQUEST_HT_NAME)){
						    	   if (worklistItemVO.getDocStatus() != null && ContractStatusConst.CR_PENDING_REVISION.intValue() == worklistItemVO.getDocStatus().intValue()) {
						    		   worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.ct.revise.circular.cancellation", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   }
						       }
						       */
								if (worklistItemVO.getTaskName().equals(CTProcessNameConst.REVIEW_CIRCULAR_CANCELLATION_RQUEST_HT_NAME) && (worklistItemVO.getDocStatus() != null && (ContractStatusConst.CR_PENDING_APPROVAL.intValue() == worklistItemVO .getDocStatus().intValue() || ContractStatusConst.CR_PENDING_REVISION_APPROVAL.intValue() == worklistItemVO.getDocStatus().intValue())) ) {
									worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.ct.review.circular.cancellation",ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
								}
								if (worklistItemVO.getTaskName().equals(CTProcessNameConst.REVISE_CIRCULAR_CANCELLATION_RQUEST_HT_NAME) && (worklistItemVO.getDocStatus() != null && ContractStatusConst.CR_PENDING_REVISION.intValue() == worklistItemVO.getDocStatus().intValue())) {
									worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.ct.revise.circular.cancellation",ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));									
								}
								//CR 28766: Static Code Analysis End
								//#21692 - start
								int docStatus = worklistItemVO.getDocStatus().intValue();
								String noteEPV = ResourceBundleUtils.getMessage("lbl.sc.qt.proposal.extension.tasklist.note",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale());
								
								if (QTStatusConst.EPV_PENDING_SUPPLIER_ACCEPTANCE.intValue() == docStatus) {
									worklistItemVO.setTitle(worklistItemVO.getTitle()
											+ "<br/>"
											+ "<span style=\"color:red;font-weight:bold\">" + noteEPV + "<span>");
								}
								//#21692 - end
	
						       processWorklistItemByDocType(worklistItemVO);
							}catch(Exception ex){
								worklistItemVO.setTitle("Please check");
								log.error("worklistItemVO.setTitle error:" + ex.getMessage());
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

	private void processTitleWithDocStatus(WorklistItemVO worklistItemVO, FacesScopes face) {
		if (worklistItemVO.getDocStatus() != null) {
			int docStatus = worklistItemVO.getDocStatus().intValue();

			if (QTStatusConst.SF_PENDING_LOI_PREPARATION.intValue() == docStatus) {
				if (loiPreparationDays == null) {
					loiPreparationDays = retrieveKpiValueByModuleCodeAndKpiCode(
							ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), KPICodeConst.SC_LOI_PREPARATION);
				}
				worklistItemVO.setTitle(worklistItemVO.getTitle()
						+ "<br/> - "
						+ ResourceBundleUtils.getMessage("lbl.sc.loiloa.loi.preparation.due.date.message",
								new String[] { loiPreparationDays != null ? loiPreparationDays.toString() : "" },
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			} else if (QTStatusConst.EPV_PENDING_SUPPLIER_ACCEPTANCE.intValue() == docStatus) {
				worklistItemVO.setTitle(worklistItemVO.getTitle()
						+ "<br/> - "
						+ ResourceBundleUtils.getMessage("lbl.sc.proposal.validity.period.extension.response.supplier",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			}
		}
	}

	public PagingDataModel<WorklistItemVO> getTaskListMyTaskDataModel() {
		return taskListMyTaskDataModel;
	}

	public void setTaskListMyTaskDataModel(PagingDataModel<WorklistItemVO> taskListMyTaskDataModel) {
		this.taskListMyTaskDataModel = taskListMyTaskDataModel;
	}

	private class taskPagingGroupDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;
		//private final Logger log = EpLogger.getLogger(taskPagingGroupDataModel.class);

		public taskPagingGroupDataModel() {
			super("taskListGroupId");
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(ISearchBean<WorklistItemVO> searchBean) {
		
			IBpmSO bpmSO = null;
			try {

				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

				// String token = bpmSO.authenticate("roslan", "welcome1");
				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();

				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED"); 

				reqDTO.setDocType(getDocTypeGroup());

				if(getTaskDescGroup()!=null && getTaskDescGroup().trim().length()>0){
					reqDTO.setTaskTitle(getTaskDescGroup());
				}
				
				if(getDocNoGroup()!=null && getDocNoGroup().trim().length()>0){
					reqDTO.setDocNumber(getDocNoGroup());
				}
			
				/*if(getSelectedOrgProfileGroup()!=null){
					System.out.println("getOrgProfileId"+getSelectedOrgProfileGroup().getOrgProfileId());
				}*/
				
				if(getDocStatusGroup()>0){
					//33849 - Start - Need to set to PA_PENDING_PAYMENT_MATCH because status in SOA for POCO Pending Payment Match is 46000
					if(getDocStatusGroup()== FulfilmentStatusConst.CO_PENDING_PAYMENT_MATCH.intValue() 
							|| getDocStatusGroup()== FulfilmentStatusConst.PO_PENDING_PAYMENT_MATCH.intValue()){
						reqDTO.setStatus(FulfilmentStatusConst.PA_PENDING_PAYMENT_MATCH.intValue());
					}else{
						reqDTO.setStatus(getDocStatusGroup());
					}
					//33849 - End
				}
				
				reqDTO.setViewBy(getViewByGroup());
							
				//reqDTO.setStatusList(statusList);
				reqDTO.setAssignment(BpmConstants.ASSIGNMENT_GROUP);
				reqDTO.setIsClaimed(false);
				reqDTO.setCtxToken(token);
				
				//Start - Task list sorting n search criteria date from and date to by Najmi 
				//29810 - START
				log.info("getSort_by() = "+getSort_byGroup());
				
				if (isFromSearchBttn()) {
					if (getSort_byGroup() != null && !getSort_byGroup().equals("")) {
						
						if (getSort_byGroup().equalsIgnoreCase(BpmConstants.SORT_BY_DOCNUMBER)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_DOCNUMBER);
						} else if (getSort_byGroup().equalsIgnoreCase(BpmConstants.SORT_BY_DOCTYPE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_DOCTYPE);
						} else if (getSort_byGroup().equalsIgnoreCase(BpmConstants.SORT_BY_TITLE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_TITLE);
						} else if (getSort_byGroup().equalsIgnoreCase(BpmConstants.SORT_BY_CREATEDATE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_CREATEDATE);
						} else if (getSort_byGroup().equalsIgnoreCase(BpmConstants.SORT_BY_STATUS)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_STATUS);
						} else if (getSort_byGroup().equalsIgnoreCase(BpmConstants.SORT_BY_DOCSTATUS)){
							reqDTO.setSortBy(BpmConstants.SORT_BY_STATUS);
						} else if (getSort_byGroup().equalsIgnoreCase(BpmConstants.SORT_BY_RECEIVEDDATE)){
							reqDTO.setSortBy(BpmConstants.SORT_BY_RECEIVEDDATE);
						} else {
							//reqDTO.setSortBy(getSort_by());
						}					
					}
				} else {
					if (this.getSortField() != null) {
						if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_DOCNUMBER)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_DOCNUMBER);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_DOCTYPE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_DOCTYPE);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_TITLE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_TITLE);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_CREATEDATE)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_CREATEDATE);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_STATUS)) {
							reqDTO.setSortBy(BpmConstants.SORT_BY_STATUS);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_DOCSTATUS)){
							reqDTO.setSortBy(BpmConstants.SORT_BY_STATUS);
						} else if (this.getSortField().equalsIgnoreCase(BpmConstants.SORT_BY_RECEIVEDDATE)){
							reqDTO.setSortBy(BpmConstants.SORT_BY_RECEIVEDDATE);
						} else{
							//reqDTO.setSortBy(getSort_by());
						}
					}
				}				 			
				
				reqDTO.setIsAsc(isAscendingGroup());
				
				if (getGroupTaskCreatedDateTo() != null) {
					getGroupTaskCreatedDateTo().setHours(23);
					getGroupTaskCreatedDateTo().setMinutes(59);
				}
				
				log.info("date from = "+getGroupTaskCreatedDateFrom() + " date to = "+getGroupTaskCreatedDateTo());
				
				
				
				reqDTO.setCreatedDateFrom(getGroupTaskCreatedDateFrom());
				reqDTO.setCreatedDateTo(getGroupTaskCreatedDateTo());
				
				//End - Task list sorting n search criteria date from and date to by Najmi 
				//29810 - END
				
				//SDO User - filer Search
				sdoSearchFilter(reqDTO);
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				
				if (responseDTO.getWorklistVO() != null) {

					if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
						//	Enhancement #28766 - Start
						//	boolean viewGlpFlag = viewGlpFlag();
						//	Enhancement #28766 - End
						for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							try {
								if (worklistItemVO.getTitle().matches("<")) {
									worklistItemVO.setTitle(worklistItemVO.getTitle().replaceAll("<", "&#60;"));
								}

								if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE_DRAFT)) {
									ScPrInquiry scPrInquiry = retrivePrInquiryByPrInquiryNo(worklistItemVO.getDocNumber());
									if (scPrInquiry != null) {
										worklistItemVO.setTitle(scPrInquiry.getTitle());
									}
								}
								// #9133 - start
								if (worklistItemVO.getDocType().equals(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT)) {
									ScPurchaseInDefault scPurchaseInDefault = retrivePurchaseInDefaultByPdNo(worklistItemVO
											.getDocNumber());
									if (scPurchaseInDefault != null) {
										worklistItemVO.setTitle(scPurchaseInDefault.getTitle());
									}
								}
								// #9133 - end
								// s1 module
								if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
									worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
								}
								if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
									worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
								}
								if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
									worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
								}
								processTitleWithDocStatus(worklistItemVO, face);

						       if(worklistItemVO.getTitle().startsWith("lbl.sm")) {
						    		MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
									mofResDto = retriveCompanyNameByApplicationId(worklistItemVO.getDocId());
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   if(mofResDto.getCompanyName() != null){
						    		   	   sb.append(" ( ");
								    	   sb.append(mofResDto.getCompanyName());
								    	   sb.append(" ) ");
								    	   //	Enhancement #28766 - Start
								    	   //	if(viewGlpFlag && mofResDto.isGlp()) {
								    	   //	sb.append("<font color=\"red\">&#x26A0;GLP </font>");
								    	   //	}
								    	//	Enhancement #28766 - End
						    	   }
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }
						       
						       
						       if(worklistItemVO.getTitle().startsWith("lbl.cd")) {
						    	
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }
						       
						       if(worklistItemVO.getTitle().startsWith("lbl.pm")) {
						    	   AccessApplResDTO accessApplResDTO = retrieveAccessApplByDocNo(worklistItemVO.getDocNumber());
						    	   if(accessApplResDTO.isSuccessful() && accessApplResDTO.getAccessAppl() != null){
						    		  
							    	   StringBuilder sb = new StringBuilder();
							    	   if(ParameterConst.DOCTYPE_MARKET_RESEARCH.equalsIgnoreCase(worklistItemVO.getDocType())){
							    		   sb.append(ResourceBundleUtils.getMessage("lbl.pm.access.app.tasklist.mr.title", 
								    			   new String[]{ accessApplResDTO.getAccessAppl().getApplicantName(),
							    				   		accessApplResDTO.getAccessAppl().getOrgProfile().getPmOrgValidity().getOrgName()},
								    			   ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							    		   worklistItemVO.setTitle(sb.toString());
							    	   }else if(ParameterConst.DOCTYPE_COMPANY_PROFILE.equalsIgnoreCase(worklistItemVO.getDocType())){
							    		   sb.append(ResourceBundleUtils.getMessage("lbl.pm.access.app.tasklist.cp.title", 
								    			   new String[]{accessApplResDTO.getAccessAppl().getApplicantName(),
							    				   		accessApplResDTO.getAccessAppl().getOrgProfile().getPmOrgValidity().getOrgName()},
								    			   ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							    		   worklistItemVO.setTitle(sb.toString());
							    	   }
						    	   }
						       }
						     //25884 - start
								 if (worklistItemVO.getDocStatus() != null && worklistItemVO.getDocStatus().intValue() == DPStatusConstants.RN_PENDING_ITEM_CODIFICATION.intValue()){
									 IRequestNoteSO rnSO = BusinessDelegate.getInstance().getServiceObject(
												ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
									//[Start] - Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
									RequestNoteReqDTO noteReqDTO = new RequestNoteReqDTO();
									if (worklistItemVO.getDocNumber() != null && !worklistItemVO.getDocNumber().isEmpty()) {
										noteReqDTO.setRequestNoteNo(worklistItemVO.getDocNumber());
									}
									RequestNoteResDTO resDTO = rnSO.getRequestNote(noteReqDTO);
									//[End] - Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
									if (resDTO.getRequestNoteDetail() != null) {
										if (resDTO.getRequestNoteDetail().getRequestNoteDtls() != null
												&& !resDTO.getRequestNoteDetail().getRequestNoteDtls().isEmpty()) {
											StringBuffer sb = new StringBuffer();
											String spacer = " ";
											List<String> listProcurementTitle = new ArrayList<>();
											for (ScRequestNoteDtl noteDtl : resDTO.getRequestNoteDetail().getRequestNoteDtls()) {
												if (noteDtl.getScRequestSupplierItem() != null
														&& noteDtl.getScRequestSupplierItem().getSmSupplier() != null) {
													SupplierSearchResDTO searchResDTO = SmUserUtil.getCurrentSupplier(face.getUserSessionBean()
															.getUserId());
													if (searchResDTO != null && searchResDTO.getSmSupplier() != null){
														if (searchResDTO.getSmSupplier().getSupplierId().intValue() == noteDtl
																.getScRequestSupplierItem().getSmSupplier().getSupplierId().intValue()) {
															if (!listProcurementTitle.contains(noteDtl.getProcurementTitle())){
																listProcurementTitle.add(noteDtl.getProcurementTitle());
																sb.append(noteDtl.getProcurementTitle()).append(spacer).append("/").append(spacer);
															}
															
															if (sb.toString().length() > SourcingConstants.PROCUREMENT_TITLE_LIMIT){
																sb.substring(0, SourcingConstants.PROCUREMENT_TITLE_LIMIT);
															}
														}
													}
												}
											}
											sb.deleteCharAt(sb.toString().lastIndexOf("/"));
											worklistItemVO.setTitle(sb.toString());
										}
									}
								}
								//25884 - end
						       processWorklistItemByTaskName(worklistItemVO, face); //#24968  - performance
							} catch (Exception ex) {
								worklistItemVO.setTitle("please check");
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

	public void setTaskListGroupDataModel(PagingDataModel<WorklistItemVO> taskListGroupDataModel) {
		this.taskListGroupDataModel = taskListGroupDataModel;
	}
	
	// #24714
	private class taskPagingBothDataModel extends PagingDataModel<WorklistItemVO> {
		private static final long serialVersionUID = 1L;
		private final Logger log = EpLogger.getLogger(taskPagingBothDataModel.class);

		public taskPagingBothDataModel() {
			super("taskListBothTaskId"); // Datatables ID
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(ISearchBean<WorklistItemVO> searchBean) {
			IBpmSO bpmSO = null;
			try {
				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

				// String token = bpmSO.authenticate("roslan", "welcome1");
				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();

				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED"); 
				
				reqDTO.setDocType(getDocType());

				if(getTaskDesc()!=null && getTaskDesc().trim().length()>0){
					reqDTO.setTaskTitle(getTaskDesc());
				}
				
				if(getDocNo()!= null && getDocNo().trim().length() > 0){
					reqDTO.setDocNumber(getDocNo());
				}
			
				if(getDocStatus() > 0){
					reqDTO.setStatus(getDocStatus());
				}
				
				reqDTO.setViewBy(getViewBy());
					
				reqDTO.setAssignment(BpmConstants.ASSIGNMENT_MY_AND_GROUP);
				//reqDTO.setIsClaimed(false);
				reqDTO.setCtxToken(token);
				
				//SDO User - filer Search
				sdoSearchFilter(reqDTO);
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				
				if (responseDTO.getWorklistVO() != null) {

					if(responseDTO.getWorklistVO().getWorklistItem()!= null) {
				    	   //	Enhancement #28766 - Start
							//	boolean viewGlpFlag = viewGlpFlag();
				    	   //	Enhancement #28766 - End
						
						java.util.Collections.sort(responseDTO.getWorklistVO().getWorklistItem(), new java.util.Comparator<WorklistItemVO>() {
						  @Override
						  public int compare(WorklistItemVO w1, WorklistItemVO w2) {
							  return w1.getTaskType().compareTo(w2.getTaskType());
						  }
						});
						
						for( WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							try {
								processWorklistItemByTitle(worklistItemVO, face);
								
								processWorklistItemByTaskName(worklistItemVO, face);
						       
								processWorklistItemByDocType(worklistItemVO);
						     
								// Identify if it is a group or individual task
								worklistItemVO.getTaskType();
								
							} catch(Exception ex) {
								worklistItemVO.setTitle("please check");
								
								log.error("worklistItemVO.setTitle error:" + ex.getMessage());
							}
						}
						
						resultBean.setTotalRecord(responseDTO.getWorklistVO()
								.getTotal());
						resultBean.setResultList(responseDTO.getWorklistVO()
								.getWorklistItem());
					}
					
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

	public PagingDataModel<WorklistItemVO> getTaskListBothDataModel() {
		return taskListBothDataModel;
	}
	
	public void setTaskListBothDataModel(
			PagingDataModel<WorklistItemVO> taskListBothDataModel) {
		this.taskListBothDataModel = taskListBothDataModel;
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

	public Map<?, ?> getBpmMap() {
		return bpmMap;
	}

	public void setBpmMap(Map<?, ?> bpmMap) {
		BPMTaskListManagedBean.bpmMap = bpmMap;
	}

	private AccessApplResDTO retrieveAccessApplByDocNo(String docNo){
		IAccessApplSO accessApplSO = null;
		try{
			accessApplSO = (IAccessApplSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAccessApplSO.class);
			
			AccessApplReqDTO reqDTO = new AccessApplReqDTO();
			reqDTO.setDocNo(docNo);
			AccessApplResDTO resDTO = accessApplSO.retrieveAccessApplByApplNo(reqDTO);
			if(resDTO.isSuccessful()){
				return resDTO;
				
			}
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}finally{
			accessApplSO = null;
		}
		return null;
	}

	/**
	 * @return the docStatusList
	 */
	public List<SelectItem> getDocStatusList() {
		if(docStatusList==null)
			docStatusList = new ArrayList<SelectItem>();
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
	 * @return the docStatusListGroup
	 */
	public List<SelectItem> getDocStatusListGroup() {
		return docStatusListGroup;
	}

	/**
	 * @param docStatusListGroup the docStatusListGroup to set
	 */
	public void setDocStatusListGroup(List<SelectItem> docStatusListGroup) {
		this.docStatusListGroup = docStatusListGroup;
	}

	/**
	 * @return the docStatusGroup
	 */
	public int getDocStatusGroup() {
		return docStatusGroup;
	}

	/**
	 * @param docStatusGroup the docStatusGroup to set
	 */
	public void setDocStatusGroup(int docStatusGroup) {
		this.docStatusGroup = docStatusGroup;
	}
	
	/**
	 * @return the loiPreparationDays
	 */
	public Integer getLoiPreparationDays() {
		return loiPreparationDays;
	}

	/**
	 * @param loiPreparationDays the loiPreparationDays to set
	 */
	public void setLoiPreparationDays(Integer loiPreparationDays) {
		this.loiPreparationDays = loiPreparationDays;
	}
	
	public boolean isSDORole(){
		FacesScopes fs = new FacesScopes();
		if (fs.hasRoleUser(RoleCode.UPS_SDO.value())){
			return true;
		}
		return false;
	}

	//	Enhancement #28766 - Start
	//	private boolean viewGlpFlag() {
	//		FacesScopes fs = new FacesScopes();
	//		if (fs.hasRoleUser(RoleCode.UPS_APPROVER.value())
	//				|| fs.hasRoleUser(RoleCode.UPS_PO.value())
	//				|| fs.hasRoleUser(RoleCode.UPS_SDO.value())) {
	//			return true;
	//		}
	//		return false;
	//	}
	//	Enhancement #28766 - End

	public boolean isSdoWithMultipleRoles() {
		return sdoWithMultipleRoles;
	}

	public void setSdoWithMultipleRoles(boolean sdoWithMultipleRoles) {
		this.sdoWithMultipleRoles = sdoWithMultipleRoles;
	}
	
	public void sdoSearchFilter(BpmRequestDTO reqDTO){
		if(isSDORole()){
			StringBuilder taskTitle = new StringBuilder("verifySupportingDocuments");

			if(StringUtils.isNotBlank(getTaskDescGroup())){
				taskTitle.append(".");
				taskTitle.append(getTaskDescGroup());
				
				if(!isSdoWithMultipleRoles()){
					reqDTO.setTaskTitle(taskTitle.toString());
				}else if(isSDORole() && isSdoWithMultipleRoles()){
					if(getDocStatusGroup() == SupplierManagementConst.PM_STATUS_PENDING_SUPP_DOC_VERIFICATION){
						reqDTO.setTaskTitle(taskTitle.toString());
					}
				}
			}
		}
	}
	
	private String retreiveSupplierInvoiceNo(int docId){
		IInvoiceSO invoiceSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IInvoiceSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setDocId(new Long(docId));
		return invoiceSO.retreiveSupplierInvoiceNo(reqDTO).getSupplierInvoiceNo();
	}
	
	private String retreiveSupplierDoRefNo(int docId,boolean isAckFRN){
		IDeliveryOrderSO deliveryOrderSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IDeliveryOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setDocId(new Long(docId));
		reqDTO.setAckFRN(isAckFRN);
		return deliveryOrderSO.retreiveSupplierDoRefNo(reqDTO).getSupplierDoRefNo();
	}
	
	//start added for #24968
	public FlFulfilmentRequest getFulfilmentRequest() {
		return fulfilmentRequest;
	}

	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
	}

	public FlFulfilmentNote getFulfilmentNote() {
		return fulfilmentNote;
	}

	public void setFulfilmentNote(FlFulfilmentNote fulfilmentNote) {
		this.fulfilmentNote = fulfilmentNote;
	}
	
	private void retrievePoCoNoForCreateFRN(WorklistItemVO worklistItemVO,String taskId) {
		IDeliveryOrderSO deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		//#24968-start-bazlin
		reqDTO.setDeliveryOrderId(worklistItemVO.getDocId());
		resDTO = deliveryOrderSO.retrievePocoDOById(reqDTO);
		String poCoNumber = resDTO.getDocNo();
		//#24968-end-bazlin
		
		if(poCoNumber != null){
			worklistItemVO.setPocoNo(poCoNumber);
		}
	}
	
	private void retrievePoCoNoForAckModifyDo(WorklistItemVO worklistItemVO,String taskId) {
		
		IDeliveryOrderSO deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		//#24968-start-bazlin
		reqDTO.setDeliveryOrderId(worklistItemVO.getDocId());
		resDTO = deliveryOrderSO.retrievePocoDOById(reqDTO);
		String poCoNumber = resDTO.getDocNo();		
		//#24968-end-bazlin
		
		if(poCoNumber != null){
			worklistItemVO.setPocoNo(poCoNumber);
		}
	}
	
	private void retrievePoCoNoForReviewCancelDo(WorklistItemVO worklistItemVO,String taskId) {
		
		IDeliveryOrderSO deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		//#24968-start-bazlin
		reqDTO.setDeliveryOrderId(worklistItemVO.getDocId());
		resDTO = deliveryOrderSO.retrievePocoDOById(reqDTO);
		String poCoNumber = resDTO.getDocNo();	
		//#24968-end-bazlin
		
		if(poCoNumber != null){
			worklistItemVO.setPocoNo(poCoNumber);
		}
	}
	
	private void retrievePoCoNoForApproveFRN(WorklistItemVO worklistItemVO,String taskId) {
		
		IReceivedNoteSO receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		//#24968-start-bazlin
		reqDTO.setFulfilmentNoteId(worklistItemVO.getDocId());
		resDTO = receivedNoteSO.retrievePocoRNById(reqDTO);
		String poCoNumber = resDTO.getDocNo();	
		//#24968-end-bazlin

		if(poCoNumber != null){
			worklistItemVO.setPocoNo(poCoNumber);
		}
	}
	
	private void retrievePoCoNoForReviewDan(WorklistItemVO worklistItemVO,String taskId) {
		
		IAdjustmentSO adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IAdjustmentSO.class);
		FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		//#24968-start-bazlin
		flReqDTO.setDanId(worklistItemVO.getDocId());
		resDTO = adjustmentSO.retrievePoCoAdjNoById(flReqDTO);
		String poCoNumber = resDTO.getDocNo();	
		//#24968-end-bazlin
		
		if(poCoNumber != null){
			worklistItemVO.setPocoNo(poCoNumber);
		}
	}
	
	private void retrievePoCoNoForStopFulFilment(WorklistItemVO worklistItemVO,String taskId) {
		
		IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
		FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		//#24968-start-bazlin
		flReqDTO.setDocId(Long.valueOf(worklistItemVO.getDocId()));
		resDTO = orderSO.retrievePoCoStopInstruction(flReqDTO);
		String poCoNumber = resDTO.getDocNo();
		//#24968-end-bazlin
		
		
		if(poCoNumber != null){
			worklistItemVO.setPocoNo(poCoNumber);
		}
	}
	
	private void retrievePoCoNoForSubmitFailedCancelFRN(WorklistItemVO worklistItemVO,String taskId) {
		
		IReceivedNoteSO receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		//#24968-start-bazlin
		reqDTO.setDeliveryOrderId(worklistItemVO.getDocId());
		resDTO = receivedNoteSO.retrievePocoRNByDOId(reqDTO);
		String poCoNumber = resDTO.getDocNo();
		//#24968-end-bazlin
		
		if(poCoNumber != null){
			worklistItemVO.setPocoNo(poCoNumber);
		}
	}
	
	private void retrievePoCoNoForAckModifiedInvoice(WorklistItemVO worklistItemVO,String taskId) {
		IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		//#24968-start-bazlin
		reqDTO.setInvoiceId(Long.valueOf(worklistItemVO.getDocId()));
		resDTO = invoiceSO.retrievePocoInvoiceByInvoiceId(reqDTO);
		String poCoNumber = resDTO.getDocNo();
		//#24968-end-bazlin
		
		if(poCoNumber != null){
			worklistItemVO.setPocoNo(poCoNumber);
		}		
	}
	
	private void retrievePoCoNoForCancelInvoice(WorklistItemVO worklistItemVO,String taskId) {		
		IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		//#24968-start-bazlin
		reqDTO.setInvoiceId(Long.valueOf(worklistItemVO.getDocId()));
		resDTO = invoiceSO.retrievePocoInvoiceByInvoiceId(reqDTO);
		String poCoNumber = resDTO.getDocNo();
		//#24968-end-bazlin
		
		if(poCoNumber != null){
			worklistItemVO.setPocoNo(poCoNumber);
		}		
	}
	
	public Object getBpmTaskDetails(String taskId) {
		FacesScopes facesScopes = new FacesScopes();

		IBpmSO bpmSO;

		bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(facesScopes.getBPMAuthenticationCtxToken());
		reqDTO.setTaskId(taskId);

		BpmResponseDTO resDTO = bpmSO.getTaskDetails(reqDTO);
		Object payLoad = resDTO.getPojo();
		return payLoad;
	}
	//end added for #24968
	
	// 12177 - Start
	private boolean isSqStillValid(Date sqEndDate){		
		
		Boolean validDate = true;	
		
		// Refactor: To make sqTimeValidity to read only once - Start.
		Integer sqTimeValidity = NgepApplicationBean.getSqTimeValidity();
		logger.info("Refactor 12177 (BPMTaskListManagedBean) : getSqTimeValidity - " + sqTimeValidity);
		// Refactor: To make sqTimeValidity to read only once - End.
		
		if(sqEndDate != null){
				Date currentDate = new Date();
				Date closingDate = sqEndDate;
					
				Calendar cal = Calendar.getInstance();
				cal.clear(); 
				cal.setTime(closingDate);
				// Refactor: #12177 - Start
				cal.add(Calendar.DATE, sqTimeValidity); //add 60days to closing/end date
				// Refactor: #12177 - End
				setExpirationDate(dateToCalendar(cal.getTime()));
					
				if(currentDate.compareTo(cal.getTime()) >= 0){
						validDate = false;					
				}							
			}						
			return validDate;	
	}
	
	public Calendar getExpirationDate() {
		return expirationDateSq;
	}

	public void setExpirationDate(Calendar expirationDateSq) {
		this.expirationDateSq = expirationDateSq;
	}
	
	//Convert Date to Calendar
    private Calendar dateToCalendar(Date date) {
    	Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	// 12177 - End
    
    /**
     * This method was refactored for code readibility and maintenance. To filter worklistItemVO
     * by title.
     * 
     * Performance risk: call service to retrieve from DB.
     * 
     * Mitigation: service only select selected field.
     * 
     * @param worklistItemVO
     * @param face
     */
    private void processWorklistItemByTitle(WorklistItemVO worklistItemVO, FacesScopes face) {
		String title = worklistItemVO.getTitle();
		
		if (title.contains("<")) {
			worklistItemVO.setTitle(worklistItemVO.getTitle().replaceAll("<", "&#60;"));
		}

		// s1 module
		if (title.equalsIgnoreCase(SourcingConstants.PI_TITLE_DRAFT)) {
			ScPrInquiry scPrInquiry = retrivePrInquiryByPrInquiryNo(worklistItemVO.getDocNumber());
			if (scPrInquiry != null) {
				worklistItemVO.setTitle(scPrInquiry.getTitle());
			}
		} else if (title.equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
			worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
		} else if (title.equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
			worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
		} else if (title.equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
			worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
		} else if (title.startsWith("lbl.sm")) {
			// sm module
			// worklistItemVO.setTitle(SmCommonUtil.getSmBpmTaskTitle(worklistItemVO.getTitle(),worklistItemVO.getProcess()));
			// sm module
			MofRegistrationResDTO mofResDto = retriveCompanyNameByApplicationId(worklistItemVO.getDocId());
			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(),
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			if (mofResDto.getCompanyName() != null) {
				sb.append(" ( ");
				sb.append(mofResDto.getCompanyName());
				sb.append(" ) ");
				// Enhancement #28766 - Remove GLP flag - Start
				// if (viewGlpFlag() && mofResDto.isGlp()) {
				// sb.append("<font color=\"red\">&#x26A0;GLP </font>");
				// }
				// Enhancement #28766 - Remove GLP flag - End
			}

			worklistItemVO.setTitle(sb.toString());
		} else if (title.startsWith("lbl.cd")) {

			StringBuilder sb = new StringBuilder();
			sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(),
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));

			worklistItemVO.setTitle(sb.toString());
		} else if (title.startsWith("lbl.pm")) {
			AccessApplResDTO accessApplResDTO = retrieveAccessApplByDocNo(worklistItemVO.getDocNumber());
			if (accessApplResDTO.isSuccessful() && accessApplResDTO.getAccessAppl() != null) {

				StringBuilder sb = new StringBuilder();
				if (ParameterConst.DOCTYPE_MARKET_RESEARCH.equalsIgnoreCase(worklistItemVO.getDocType())) {
					sb.append(ResourceBundleUtils.getMessage("lbl.pm.access.app.tasklist.mr.title", new String[] {
							accessApplResDTO.getAccessAppl().getApplicantName(),
							accessApplResDTO.getAccessAppl().getOrgProfile().getPmOrgValidity().getOrgName() },
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
					worklistItemVO.setTitle(sb.toString());
				} else if (ParameterConst.DOCTYPE_COMPANY_PROFILE.equalsIgnoreCase(worklistItemVO.getDocType())) {
					sb.append(ResourceBundleUtils.getMessage("lbl.pm.access.app.tasklist.cp.title", new String[] {
							accessApplResDTO.getAccessAppl().getApplicantName(),
							accessApplResDTO.getAccessAppl().getOrgProfile().getPmOrgValidity().getOrgName() },
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
					worklistItemVO.setTitle(sb.toString());
				}
			}
		}  else if (worklistItemVO.getDocStatus() != null
				&& QTStatusConst.SF_PENDING_LOI_PREPARATION.intValue() == worklistItemVO.getDocStatus().intValue()) {
			if (loiPreparationDays == null) {
				loiPreparationDays = retrieveKpiValueByModuleCodeAndKpiCode(
						ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), KPICodeConst.SC_LOI_PREPARATION);
			}
			
			worklistItemVO.setTitle(worklistItemVO.getTitle() + "<br/> - "
							+ ResourceBundleUtils.getMessage(
									"lbl.sc.loiloa.loi.preparation.due.date.message",
									new String[] { loiPreparationDays != null ? loiPreparationDays.toString() : "" },
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									face.getUserLocale()));
		}
		//25884 - start
		else if (worklistItemVO.getDocStatus() != null && worklistItemVO.getDocStatus().intValue() == DPStatusConstants.RN_PENDING_ITEM_CODIFICATION.intValue()){
			 IRequestNoteSO rnSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
			 RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
			if (worklistItemVO.getDocNumber() != null && !worklistItemVO.getDocNumber().isEmpty()) {
				reqDTO.setRequestNoteNo(worklistItemVO.getDocNumber());
			}
			RequestNoteResDTO resDTO = rnSO.getRequestNote(reqDTO);

			if (resDTO.getRequestNoteDetail() != null) {
				if (resDTO.getRequestNoteDetail().getRequestNoteDtls() != null
						&& !resDTO.getRequestNoteDetail().getRequestNoteDtls().isEmpty()) {
					StringBuffer sb = new StringBuffer();
					String spacer = " ";
					List<String> listProcurementTitle = new ArrayList<>();
					for (ScRequestNoteDtl noteDtl : resDTO.getRequestNoteDetail().getRequestNoteDtls()) {
						if (noteDtl.getScRequestSupplierItem() != null
								&& noteDtl.getScRequestSupplierItem().getSmSupplier() != null) {
							SupplierSearchResDTO searchResDTO = SmUserUtil.getCurrentSupplier(face.getUserSessionBean()
									.getUserId());
							if (searchResDTO != null && searchResDTO.getSmSupplier() != null){
								if (searchResDTO.getSmSupplier().getSupplierId().intValue() == noteDtl
										.getScRequestSupplierItem().getSmSupplier().getSupplierId().intValue()) {
									if (!listProcurementTitle.contains(noteDtl.getProcurementTitle())){
										listProcurementTitle.add(noteDtl.getProcurementTitle());
										sb.append(noteDtl.getProcurementTitle()).append(spacer).append("/").append(spacer);
									}
									
									if (sb.toString().length() > SourcingConstants.PROCUREMENT_TITLE_LIMIT){
										sb.substring(0, SourcingConstants.PROCUREMENT_TITLE_LIMIT);
									}
								}
							}
						}
					}
					sb.deleteCharAt(sb.toString().lastIndexOf("/"));
					worklistItemVO.setTitle(sb.toString());
				}
			}
		}
		//25884 - end
	}
    
    /**
     * This method was refactored for code readibility and maintenance. To filter worklistItemVO
     * by document type.
     * 
     * Performance risk: call service to retrieve from DB.
     * 
     * Mitigation: service only select selected field.
     * 
     * @param worklistItemVO
     */
	private void processWorklistItemByDocType(WorklistItemVO worklistItemVO) {
		  // 12177 - Start			       
 	   QuoteResDTO quoteResDTO = null;
 	   Long docId = null;
 	   Long currentStatusId = null;
 	   
 	   if(worklistItemVO.getDocId() != null){
 		   docId = new Long(worklistItemVO.getDocId());
 	   }
 	   if(worklistItemVO.getDocStatus() != null){
 		   currentStatusId = Long.valueOf(worklistItemVO.getDocStatus()); 
 	   }
	       
	   if(worklistItemVO.getDocType() != null && currentStatusId != null){
		   
	       if(worklistItemVO.getDocType().equals(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
			   if(currentStatusId.equals(DPStatusConstants.SQ_PENDING_RN_CREATION)){
				   if((worklistItemVO.getDocId() != null && worklistItemVO.getDocId() != 0) || worklistItemVO.getDocNumber() != null){  
					   IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
					   // call specific column, native query
					   quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, worklistItemVO.getDocNumber(), 
							    currentStatusId, worklistItemVO.getDocType(), true);	
		    		   if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
		    			   if(!isSqStillValid(quoteResDTO.getEndDate())){
					    	   worklistItemVO.setDocStatus(DPStatusConstants.SQ_EXPIRED.intValue());
					       }
		    			   if(getExpirationDate() != null){
				    		   worklistItemVO.setExpirationDate(getExpirationDate());
				    	   }
		    		   }else if (quoteResDTO.getPid() != null){
		    			   
		    			   QuoteReqDTO quotereqDTO = new QuoteReqDTO();
						   quotereqDTO.setQuoteNo(worklistItemVO.getDocNumber());
						   // call specific column, native query
						   quoteResDTO = quoteSO.getPidInfoBySqDocNo(quotereqDTO);
						   if(null!=quoteResDTO.getChangeddate() && null!=quoteResDTO.getPidValiddays()){
							   Calendar cal = Calendar.getInstance();
								cal.clear();
								cal.setTime(quoteResDTO.getChangeddate());
								cal.add(Calendar.DATE, quoteResDTO.getPidValiddays());
								worklistItemVO.setExpirationDate(cal);
						   }  
		    		   }
				   } else {		   
					   logger.info("SQ_PENDING_RN_CREATION not meet condition worklistItemVO.getTaskId() = " + worklistItemVO.getTaskId() + " | worklistItemVO.getDocStatus() = " + worklistItemVO.getDocStatus());
				   }
			   }else if(currentStatusId.equals(DPStatusConstants.SQ_PENDING_SUBMISSION)){
				   if((worklistItemVO.getDocId() != null && worklistItemVO.getDocId() != 0) || worklistItemVO.getDocNumber() != null){  
					   IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
					   QuoteReqDTO quotereqDTO = new QuoteReqDTO();
					   quotereqDTO.setQuoteNo(worklistItemVO.getDocNumber());
					   // call specific column, native query
					   quoteResDTO = quoteSO.getPidInfoBySqDocNo(quotereqDTO);
					   if(null!=quoteResDTO.getChangeddate() && null!=quoteResDTO.getPidValiddays()){
						   Calendar cal = Calendar.getInstance();
							cal.clear();
							cal.setTime(quoteResDTO.getChangeddate());
							cal.add(Calendar.DATE, quoteResDTO.getPidValiddays());
							worklistItemVO.setExpirationDate(cal);
					   }
				   } else {		   
					   logger.info("SQ_PENDING_SUBMISSION not meet condition worklistItemVO.getTaskId() = " + worklistItemVO.getTaskId() + " | worklistItemVO.getDocStatus() = " + worklistItemVO.getDocStatus());
				   }
			   }else if(currentStatusId.equals(DPStatusConstants.PID_EXPIRED)){
				   if((worklistItemVO.getDocId() != null && worklistItemVO.getDocId() != 0) || worklistItemVO.getDocNumber() != null){  
					   IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
					   QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
					   quoteReqDTO.setQuoteNo(worklistItemVO.getDocNumber());
					   // call specific column, native query
					   quoteResDTO = quoteSO.getPidInfoBySqDocNo(quoteReqDTO);
					   if(null!=quoteResDTO.getChangeddate() && null!=quoteResDTO.getPidValiddays()){
						   Calendar cal = Calendar.getInstance();
							cal.clear();
							cal.setTime(quoteResDTO.getChangeddate());
							cal.add(Calendar.DATE, quoteResDTO.getPidValiddays());
							worklistItemVO.setExpirationDate(cal);
					   }	
				   } else {		   
					   logger.info("PID_EXPIRED not meet condition worklistItemVO.getTaskId() = " + worklistItemVO.getTaskId() + " | worklistItemVO.getDocStatus() = " + worklistItemVO.getDocStatus());
				   }
			   }
	       }else if(worklistItemVO.getDocType().equals(ParameterConst.DOCTYPE_REQUEST_NOTE)){ 	 
	    	   if((worklistItemVO.getDocId() != null && worklistItemVO.getDocId() != 0) || worklistItemVO.getDocNumber() != null){  
	    	   
		    	   if(currentStatusId.equals(DPStatusConstants.RN_PENDING_ITEM_CODIFICATION)
		    		  || currentStatusId.equals(DPStatusConstants.RN_PENDING_SUBMISSION)
		    		  || currentStatusId.equals(DPStatusConstants.RN_PENDING_APPROVAL)
				      || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION) 
				      || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION_APPROVAL)
				      || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)){
		    		   
			    	  if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)){
			    	      docId = null;
			    	  }
			    		   
			    	  IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
			    	  // call specific column, native query
			    	  quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, worklistItemVO.getDocNumber(), 
										currentStatusId, worklistItemVO.getDocType(), true);	
			    	  if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
			    		  if(!isSqStillValid(quoteResDTO.getEndDate())){
			    			  worklistItemVO.setDocStatus(DPStatusConstants.SQ_EXPIRED.intValue());
			    		  }
			    		  if(getExpirationDate() != null){
			    			  worklistItemVO.setExpirationDate(getExpirationDate());
						  }
			    	  }
					  
		    	    }else{
		    	    
		    	    	IRequestNoteSO requestNoteSO = (IRequestNoteSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);			    	
					    RequestNoteReqDTO reqRNDTO = new RequestNoteReqDTO();
					    RequestNoteResDTO resRNDTO = new RequestNoteResDTO();
					    if(worklistItemVO.getDocId() != null){
					    	reqRNDTO.setRequestNoteId(worklistItemVO.getDocId().longValue());
					    }
					    reqRNDTO.setRequestNoteNo(worklistItemVO.getDocNumber());
					    // call specific column, native query
					    resRNDTO = requestNoteSO.getPidDtlsByRnIdAndRnNo(reqRNDTO);
		
					    if(resRNDTO.getPidChangedDate() != null && resRNDTO.getPidValidDays() != null) {
		
					    	int pidValidDays = resRNDTO.getPidValidDays();
							Date changedDate = resRNDTO.getPidChangedDate();
							Date pidExpiredDate = new Date(); 
									
							pidExpiredDate = DateUtils.addDays(changedDate, pidValidDays);
										
							Calendar pidExpiredDateCL = Calendar.getInstance();
							pidExpiredDateCL.setTime(pidExpiredDate);
							worklistItemVO.setExpirationDate(pidExpiredDateCL);
									
							if(pidExpiredDate.compareTo(DateUtils.getCurrentDate())<0) {
								worklistItemVO.setDocStatus(DPStatusConstants.PID_EXPIRED.intValue());
							}
					    }
				   }  
	    	   } else {		   
				   logger.info("DOCTYPE_REQUEST_NOTE not meet condition worklistItemVO.getTaskId() = " + worklistItemVO.getTaskId() + " | worklistItemVO.getDocStatus() = " + worklistItemVO.getDocStatus());
			   }
	       }else if(worklistItemVO.getDocType().equals(ParameterConst.DOCTYPE_PURCHASE_REQUEST)){
	    	   if((worklistItemVO.getDocId() != null && worklistItemVO.getDocId() != 0) || worklistItemVO.getDocNumber() != null){  
		    	   if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL)
		    		  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION)
					  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
					  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)){
	
		    		  IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
		    		  // call specific column, native query
					  quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, worklistItemVO.getDocNumber(), 
								 currentStatusId, worklistItemVO.getDocType(), true);	
					  if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
						  if(!isSqStillValid(quoteResDTO.getEndDate())){
							  worklistItemVO.setDocStatus(DPStatusConstants.SQ_EXPIRED.intValue());
						  }
						  if(getExpirationDate() != null){
				    		  worklistItemVO.setExpirationDate(getExpirationDate());
				    	  }
					  }
		    	   }else{
				      IFulfilmentRequestSO fulfilmentRequestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
				      FulfilmentReqDTO reqFLDTO = new FulfilmentReqDTO();
				      FulfilmentResDTO resFLDTO = new FulfilmentResDTO();
				      
				      if(worklistItemVO.getDocId() != null){
				    	  reqFLDTO.setFlDocId(worklistItemVO.getDocId().toString());
				      }
				      reqFLDTO.setFlDocNo(worklistItemVO.getDocNumber());
				      // call specific column, native query
				      resFLDTO = fulfilmentRequestSO.retrievePIDbyFLDocId(reqFLDTO);
				    	   
				      if(resFLDTO.getFlFulfilmentRequest().getChangedDate()!=null && resFLDTO.getFlFulfilmentRequest().getPidValidDays()!=null) {
				    	  int pidValidDays = resFLDTO.getFlFulfilmentRequest().getPidValidDays();
				    	  Date pidExpiredDate = new Date(); 
								
				    	  if(resFLDTO.getFlFulfilmentRequest().getChangedDate()!=null) {
				    		  pidExpiredDate = DateUtils.addDays(resFLDTO.getFlFulfilmentRequest().getChangedDate(), pidValidDays);
				    	  }
								
				    	  Calendar pidExpiredDateCL = Calendar.getInstance();
				    	  pidExpiredDateCL.setTime(pidExpiredDate);
				    	  worklistItemVO.setExpirationDate(pidExpiredDateCL);
									   
				    	  if(pidExpiredDate.compareTo(DateUtils.getCurrentDate())<0) {
				    		  worklistItemVO.setDocStatus(DPStatusConstants.PID_EXPIRED.intValue());
				    	  }
				      }
		    	   }
		    	} else {		   
					   logger.info("DOCTYPE_PURCHASE_REQUEST not meet condition worklistItemVO.getTaskId() = " + worklistItemVO.getTaskId() + " | worklistItemVO.getDocStatus() = " + worklistItemVO.getDocStatus());
				}
			 			   
	       }else if(worklistItemVO.getDocType().equals(ParameterConst.DOCTYPE_LETTER_OF_AWARD)){
	    	   if((worklistItemVO.getDocId() != null && worklistItemVO.getDocId() != 0) || worklistItemVO.getDocNumber() != null){  
		    	   if(currentStatusId.equals(QTStatusConst.LOA_PENDING_SUBMISSION)
					  || currentStatusId.equals(QTStatusConst.LOA_PENDING_APPROVAL)
				      || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION)
				      || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION_APPROVAL)){
	
		    		  IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
		    		  // call specific column, native query
				      quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, worklistItemVO.getDocNumber(), 
									currentStatusId, worklistItemVO.getDocType(), true);	
					  if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
						  if(!isSqStillValid(quoteResDTO.getEndDate())){
							  worklistItemVO.setDocStatus(DPStatusConstants.SQ_EXPIRED.intValue());
						  }
						  if(getExpirationDate() != null){
				    		  worklistItemVO.setExpirationDate(getExpirationDate());
				    	  }
					  }  			   
				   }
	    	   } else {		   
				   logger.info("DOCTYPE_LETTER_OF_AWARD not meet condition worklistItemVO.getTaskId() = " + worklistItemVO.getTaskId() + " | worklistItemVO.getDocStatus() = " + worklistItemVO.getDocStatus());
			   }
	       }
	       // 12177 - End
	       //#9133 - start
	       else if (worklistItemVO.getDocType().equals(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT)) {
				ScPurchaseInDefault scPurchaseInDefault = retrivePurchaseInDefaultByPdNo(worklistItemVO.getDocNumber());
				if (scPurchaseInDefault != null) {
					worklistItemVO.setTitle(scPurchaseInDefault.getTitle());
				}
	       }
	       //#9133 - end
	   	} else {
	   		logger.info("DOCTYPE AND DOCSTATUSID NULL not meet condition = " + worklistItemVO.getTaskId() 
	   				+ "| worklistItemVO.getDocType() = " + worklistItemVO.getDocType()
	   				+ "| worklistItemVO.getDocStatus() = " + worklistItemVO.getDocStatus());
	   	}
	}
	  //#24968-start - performance
	/**
     * This method was refactored for code readibility and maintenance. To filter worklistItemVO
     * by Task Name.
     * 
     * Performance risk: call service to retrieve from DB.
     * 
     * Mitigation: service only select selected field.
     * 
     * @param worklistItemVO
     * @param face
     */
    private void processWorklistItemByTaskName(WorklistItemVO worklistItemVO, FacesScopes face) {
    	if(worklistItemVO.getTaskName().equals(FLProcessNameConst.PERFORM_PAYMNET_MATCH_HT)){
	    	   worklistItemVO.setTitle(retreiveSupplierInvoiceNo(worklistItemVO.getDocId()) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
	    }     
    	else if(worklistItemVO.getTaskName().equals(FLProcessNameConst.CREATE_FRN_HT) 
	    	 || worklistItemVO.getTaskName().equals(FLProcessNameConst.REVISE_FRN_HT)){
	    	   worklistItemVO.setTitle(retreiveSupplierDoRefNo(worklistItemVO.getDocId(),false) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
	    	   retrievePoCoNoForCreateFRN(worklistItemVO,worklistItemVO.getTaskId());
	    }
    	else if(worklistItemVO.getTaskName().equals(FLProcessNameConst.ACK_MODIFIED_DO_HT)){
	    	retrievePoCoNoForAckModifyDo(worklistItemVO,worklistItemVO.getTaskId());
		}	       
    	else if(worklistItemVO.getTaskName().equals(FLProcessNameConst.REVIEW_CANCEL_DO_HT)){
	    	retrievePoCoNoForReviewCancelDo(worklistItemVO,worklistItemVO.getTaskId());
	    }	       
    	else if(worklistItemVO.getTaskName().equals(FLProcessNameConst.REVIEW_DAN_HT)){
	    	retrievePoCoNoForReviewDan(worklistItemVO,worklistItemVO.getTaskId());
	    }	       
    	else if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_STOP_FULFILMENT_REQ_HT)
	    	|| worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_STOP_FULFILMENT_REQ_HT)){
	    	retrievePoCoNoForStopFulFilment(worklistItemVO,worklistItemVO.getTaskId());
	    }	       
    	else if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_FAILED_CANCEL_FRN_HT)){
	    	retrievePoCoNoForSubmitFailedCancelFRN(worklistItemVO,worklistItemVO.getTaskId());
	    }	       
    	else if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.ACK_MODIFIED_INVOICE_HT)){
	    	retrievePoCoNoForAckModifiedInvoice(worklistItemVO,worklistItemVO.getTaskId());
	    }	       
    	else if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_CANCEL_INVOICE_HT)){
	    	 retrievePoCoNoForCancelInvoice(worklistItemVO,worklistItemVO.getTaskId());
	    }     
    	else if(worklistItemVO.getTaskName().equals(FLProcessNameConst.APPROVE_FRN_HT)){
	    	worklistItemVO.setTitle(retreiveSupplierDoRefNo(worklistItemVO.getDocId(),true) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
	    	retrievePoCoNoForApproveFRN(worklistItemVO,worklistItemVO.getTaskId());
	    }
    	//# start 25627  - 
		else if (worklistItemVO.getTaskName().equalsIgnoreCase(SCProcessNameConst.APPROVE_DO_REASSIGNMENT)) {
			worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.quotation.tender.deskofficermaintenance.approveTaskListTitle", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
		}
	     //# end 25627
    	//#25037-start
		else if (worklistItemVO.getTaskName().equalsIgnoreCase(SCProcessNameConst.GENERIC_APPROVAL_APPROVE) || worklistItemVO.getTaskName().equalsIgnoreCase(SCProcessNameConst.GENERIC_APPROVAL_REVISE)) {
		//Generic approval workflow, define your title template, lbl.sc.taskname.title.[requestType][action(revise or approve)]
			worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.taskname.title.".concat(worklistItemVO.getDocType()).concat(worklistItemVO.getTaskName().split(" ")[0]), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
		}
    	//#25037-end
		
		  
	}
 	// end #24968 

	/**
	 * @return the sort_by
	 */
	public String getSort_by() {
		return sort_by;
	}

	/**
	 * @param sort_by the sort_by to set
	 */
	public void setSort_by(String sort_by) {
		this.sort_by = sort_by;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public String getSort_byGroup() {
		return sort_byGroup;
	}

	public void setSort_byGroup(String sort_byGroup) {
		this.sort_byGroup = sort_byGroup;
	}

	public boolean isAscendingGroup() {
		return ascendingGroup;
	}

	public void setAscendingGroup(boolean ascendingGroup) {
		this.ascendingGroup = ascendingGroup;
	}

	public boolean isFromSearchBttn() {
		return fromSearchBttn;
	}

	public void setFromSearchBttn(boolean fromSearchBttn) {
		this.fromSearchBttn = fromSearchBttn;
	}

	public Date getMyTaskCreatedDateFrom() {
		return myTaskCreatedDateFrom;
	}

	public void setMyTaskCreatedDateFrom(Date myTaskCreatedDateFrom) {
		this.myTaskCreatedDateFrom = myTaskCreatedDateFrom;
	}

	public Date getMyTaskCreatedDateTo() {
		return myTaskCreatedDateTo;
	}

	public void setMyTaskCreatedDateTo(Date myTaskCreatedDateTo) {
		this.myTaskCreatedDateTo = myTaskCreatedDateTo;
	}

	public Date getGroupTaskCreatedDateFrom() {
		return groupTaskCreatedDateFrom;
	}

	public void setGroupTaskCreatedDateFrom(Date groupTaskCreatedDateFrom) {
		this.groupTaskCreatedDateFrom = groupTaskCreatedDateFrom;
	}

	public Date getGroupTaskCreatedDateTo() {
		return groupTaskCreatedDateTo;
	}

	public void setGroupTaskCreatedDateTo(Date groupTaskCreatedDateTo) {
		this.groupTaskCreatedDateTo = groupTaskCreatedDateTo;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	/*#31422-basri 190821 (remove Un-used code Parameter)
	public void setTaskIdPair(String taskIdPair) {
		this.taskIdPair = taskIdPair;
		
	}
	
	public String getTaskIdPair() {
		return taskIdPair;
	}
	#31422-basri 190821*/

	//#32304 - start
	public List<SmAppl> getCancelledBumiApplLs() {
		return cancelledBumiApplLs;
	}

	public void setCancelledBumiApplLs(List<SmAppl> cancelledBumiApplLs) {
		this.cancelledBumiApplLs = cancelledBumiApplLs;
	}
	
	public PagingDataModel<SmAppl> getBumiListDataModel() {
		return bumiListDataModel;
	}

	public void setBumiListDataModel(PagingDataModel<SmAppl> bumiListDataModel) {
		this.bumiListDataModel = bumiListDataModel;
	}

	public Boolean getShowCancelledBumiApplList() {
		return showCancelledBumiApplList;
	}

	public void setShowCancelledBumiApplList(Boolean showCancelledBumiApplList) {
		this.showCancelledBumiApplList = showCancelledBumiApplList;
	}

	private class BumiPagingDataModel extends PagingDataModel<SmAppl> {
		private static final long serialVersionUID = 1L;
		
		public BumiPagingDataModel() {
			super("bumiPagingDataModelId");
		}

		@Override
		protected ISearchResultBean<SmAppl> pageChangeListener(ISearchBean<SmAppl> searchBean) {
			SearchResultBean<SmAppl> resultBean = new SearchResultBean<>();
			resultBean.setResultList(null);
			resultBean.setTotalRecord(0);
			List<SmAppl> bumi = BPMTaskListManagedBean.this.getCancelledBumiApplLs();
			if(bumi != null){
				resultBean.setResultList(new ArrayList<SmAppl>());
				for(int i=calculateFinalOffSet(getPageNo()); i<bumi.size(); i++){
					resultBean.getResultList().add(bumi.get(i));
				}
				resultBean.setTotalRecord(resultBean.getResultList().size());
			}
			return resultBean;
		}
	}
	//#32304 - end
}
