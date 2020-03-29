package my.ep.web.managedbean.bpm.tasklist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

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
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.biz.service.sc.requestitem.IRequestItemSO;
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
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
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.dto.pm.access.application.AccessApplResDTO;
import my.ep.dto.pm.common.kpi.KpiReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sc.requestitem.RequestItemResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.fl.dandata.FLDanDataType;
import my.ep.jaxb.fl.frndata.FLFulFillmentReceivedNoteDataType;
import my.ep.jaxb.fl.maintenace.cancel.dofulfillment.FLDOFulfillmentCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.invoice.FLInvoiceCancelReqType;
import my.ep.jaxb.fl.maintenace.modification.dofulfillment.FLDOFulfillmentModificationReqType;
import my.ep.jaxb.fl.maintenace.modification.invoice.FLInvoiceModificationReqType;
import my.ep.jaxb.fl.stopfullfillmentdata.FLStopFullfillmentDataType;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmKPIValue;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "bpmTasklistMB")
@SessionScoped
public class BPMTaskListManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private List<WorklistItemVO> taskList;
	private List<FlFulfilmentOrder> flFulfilmentOrderList;
	private static Map bpmMap = null;
	private String processName;
	private String[] action;
	private String taskId;
	private Integer tabActiveIndex = 0;
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

	public void setTaskListDataModel(
			PagingDataModel<WorklistItemVO> taskListDataModel) {
		this.taskListDataModel = taskListDataModel;
	}

	private class taskPagingDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;
		private final Logger log = EpLogger.getLogger(taskPagingDataModel.class);
		
		public taskPagingDataModel() {
			super("taskListId");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			log.info(">>>>>>>>>>>>>>>>>>>pageChangeListener<<<<<<<<<<<<<<<<<<<");
			IBpmSO bpmSO = null;
			try {
				log.info(">>>>>>>>>>>>>>>>>>getAssignment()=" + getAssignment());
				log.info(">>>>>>>.pageNo()=" + getPageNo());
				log.info(">>>>>>>.pageSize()=" + getPageSize());
				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);

				// String token = bpmSO.authenticate("roslan", "welcome1");
				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();

				log.info("testing BPM auth******" + token);
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
					log.info("testing BPM******size=" + responseDTO.getWorklistVO().getWorklistItem().size());
					log.info("testing BPM******total=" + responseDTO.getWorklistVO().getTotal());

					// setTaskList(responseDTO.getWorklistVO().getWorklistItem());

					if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
						for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							if (worklistItemVO.getTitle().matches("<")) {
								worklistItemVO.setTitle(worklistItemVO.getTitle().replaceAll("<", "&#60;"));
							}
							// s1 module
							if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE_DRAFT)) {
								log.info("Doc number = " + worklistItemVO.getDocNumber());
								ScPrInquiry scPrInquiry = retrivePrInquiryByPrInquiryNo(worklistItemVO.getDocNumber());
								if (scPrInquiry != null) {
									worklistItemVO.setTitle(scPrInquiry.getTitle());
									log.info("prno = " + scPrInquiry.getPrInquiryNo() + "title = "
											+ scPrInquiry.getTitle());
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
								log.info("***** The document Id for the workflow is ****** "
										+ worklistItemVO.getDocId());
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
			pmKpiSO = BusinessDelegate
					.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IPmKpiSO.class);
			KpiReqDTO reqDTO = new KpiReqDTO();
			reqDTO.setModuleCode(moduleCode);
			reqDTO.setKpiCode(kpiCode);
			PmKPIValue pmKPIValue = pmKpiSO
					.retrieveKpiValueByModuleCodeAndKpiCode(reqDTO)
					.getKpiValue();
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
	
	private ScPrInquiry retrivePrInquiryByPrInquiryNo(String prInquiryNo) {
		ScPrInquiry scPrInquiry = getPrInquirySO().retrivePrInquiryByPrInquiryNo(prInquiryNo);
		return scPrInquiry;
	}

	private IPrInquirySO getPrInquirySO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
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
		private final Logger log = EpLogger.getLogger(taskPagingMyTaskDataModel.class);

		public taskPagingMyTaskDataModel() {
			super("taskListMyTaskId");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
		
			IBpmSO bpmSO = null;
			try {
				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

				// String token = bpmSO.authenticate("roslan", "welcome1");
				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();

				log.info("testing BPM auth******" + token);
				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED"); // TODO : ADD filter for state and set do variable
				log.info("getDocType()"+getDocType());
				reqDTO.setDocType(getDocType());
	
				reqDTO.setViewBy(getViewBy());

				if(getDocNo()!=null && getDocNo().trim().length()>0){
					reqDTO.setDocNumber(getDocNo());
				}
				
				if(getTaskDesc()!=null && getTaskDesc().trim().length()>0){
					reqDTO.setTaskTitle(getTaskDesc().replaceAll("<", "&#60;"));
				}
				
				/*if(getSelectedOrgProfile()!=null){
					System.out.println("getSelectedOrgProfile()"+getSelectedOrgProfile().getOrgProfileId());
				}*/
				log.info("getDocStatus()" + getDocStatus());
				if(getDocStatus()>0){
					reqDTO.setStatus(getDocStatus());
				}

				reqDTO.setAssignment(BpmConstants.ASSIGNMENT_MY);
				reqDTO.setCtxToken(token);
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				
				if (responseDTO.getWorklistVO() != null) {
					log.info("testing BPM******size=" + responseDTO.getWorklistVO().getWorklistItem().size());
					log.info("testing BPM******total=" + responseDTO.getWorklistVO().getTotal());

					if(responseDTO.getWorklistVO().getWorklistItem()!=null) {

						boolean viewGlpFlag = viewGlpFlag();
						for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							try{
								if (worklistItemVO.getTitle().contains("<")) {
									worklistItemVO.setTitle(worklistItemVO.getTitle().replaceAll("<", "&#60;"));
								}

							// s1 module
							if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE_DRAFT)) {
								log.info("Doc number = " + worklistItemVO.getDocNumber());
								ScPrInquiry scPrInquiry = retrivePrInquiryByPrInquiryNo(worklistItemVO
										.getDocNumber());
								if (scPrInquiry != null) {
									worklistItemVO.setTitle(scPrInquiry.getTitle());
									log.info("prno = " + scPrInquiry.getPrInquiryNo() + "title = "
											+ scPrInquiry.getTitle());
								}
							} else if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							} else if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							} else if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							} else if(worklistItemVO.getTitle().startsWith("lbl.sm")) {
								//sm module
								//worklistItemVO.setTitle(SmCommonUtil.getSmBpmTaskTitle(worklistItemVO.getTitle(),worklistItemVO.getProcess()));
								//sm module
								MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
								log.info("***** The document Id for the workflow is ****** "+ worklistItemVO.getDocId());
								mofResDto = retriveCompanyNameByApplicationId(worklistItemVO.getDocId());
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   if(mofResDto.getCompanyName() != null){
						    		   	   sb.append(" ( ");
								    	   sb.append(mofResDto.getCompanyName());
								    	   sb.append(" ) ");
								    	   if(viewGlpFlag && mofResDto.isGlp()) {
								    		   sb.append("<font color=\"red\">&#x26A0;GLP </font>");
								    	   }
						    	   }
						    	   
								worklistItemVO.setTitle(sb.toString());
							} else if (worklistItemVO.getTitle().startsWith("lbl.cd")) {

								StringBuilder sb = new StringBuilder();
								sb.append(ResourceBundleUtils.getMessage(
										worklistItemVO.getTitle(),
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										face.getUserLocale()));

								worklistItemVO.setTitle(sb.toString());
							} else if (worklistItemVO.getTitle().startsWith("lbl.pm")) {
								AccessApplResDTO accessApplResDTO = retrieveAccessApplByDocNo(worklistItemVO.getDocNumber());
								if (accessApplResDTO.isSuccessful() && 
										accessApplResDTO.getAccessAppl() != null) {

									StringBuilder sb = new StringBuilder();
									if (ParameterConst.DOCTYPE_MARKET_RESEARCH.equalsIgnoreCase(worklistItemVO.getDocType())) {
										sb.append(ResourceBundleUtils.getMessage(
														"lbl.pm.access.app.tasklist.mr.title",
														new String[] {
																accessApplResDTO.getAccessAppl().getApplicantName(),
																accessApplResDTO.getAccessAppl().getOrgProfile().getPmOrgValidity().getOrgName() },
														ResourceBundleUtils.BUNDLE_COMMON_LABELS,
														face.getUserLocale()));
										worklistItemVO.setTitle(sb.toString());
									} else if (ParameterConst.DOCTYPE_COMPANY_PROFILE.equalsIgnoreCase(worklistItemVO
													.getDocType())) {
										sb.append(ResourceBundleUtils.getMessage(
														"lbl.pm.access.app.tasklist.cp.title",
														new String[] {
																accessApplResDTO.getAccessAppl().getApplicantName(),
																accessApplResDTO.getAccessAppl().getOrgProfile().getPmOrgValidity().getOrgName() },
														ResourceBundleUtils.BUNDLE_COMMON_LABELS,
														face.getUserLocale()));
										worklistItemVO.setTitle(sb.toString());
									}
								}
								} else {
									processTitleWithDocStatus(worklistItemVO, face);
								}
							 if(worklistItemVO.getTaskName().equals(FLProcessNameConst.PERFORM_PAYMNET_MATCH_HT)){
						    	   worklistItemVO.setTitle(retreiveSupplierInvoiceNo(worklistItemVO.getDocId()) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
						       }
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.CREATE_FRN_HT) 
						    		   || worklistItemVO.getTaskName().equals(FLProcessNameConst.REVISE_FRN_HT)){
						    	   worklistItemVO.setTitle(retreiveSupplierDoRefNo(worklistItemVO.getDocId(),false) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
							       //start added for #24968
						    	   retrievePoCoNoForCreateFRN(worklistItemVO,worklistItemVO.getTaskId());
						    	   //end added for #24968
						       }
						       //start added for #24968
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.ACK_MODIFIED_DO_HT)){
							    	   retrievePoCoNoForAckModifyDo(worklistItemVO,worklistItemVO.getTaskId());
							       }
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.REVIEW_CANCEL_DO_HT)){
						    	   retrievePoCoNoForReviewCancelDo(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.REVIEW_DAN_HT)){
						    	   retrievePoCoNoForReviewDan(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_STOP_FULFILMENT_REQ_HT)
						    		   || worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_STOP_FULFILMENT_REQ_HT)){
						    	   retrievePoCoNoForStopFulFilment(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_FAILED_CANCEL_FRN_HT)){
						    	   retrievePoCoNoForSubmitFailedCancelFRN(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.ACK_MODIFIED_INVOICE_HT)){
						    	   retrievePoCoNoForAckModifiedInvoice(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_CANCEL_INVOICE_HT)){
						    	   retrievePoCoNoForCancelInvoice(worklistItemVO,worklistItemVO.getTaskId());
						       }

						       //end added for #24968
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.APPROVE_FRN_HT)){
						    	   worklistItemVO.setTitle(retreiveSupplierDoRefNo(worklistItemVO.getDocId(),true) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
							       //start added for #24968
						    	   retrievePoCoNoForApproveFRN(worklistItemVO,worklistItemVO.getTaskId());
						    	   //end added for #24968
						       }
						       if(worklistItemVO.getTaskName().equals(CTProcessNameConst.REVIEW_CIRCULAR_CANCELLATION_RQUEST_HT_NAME)){
						    	   if (worklistItemVO.getDocStatus() != null
											&& (ContractStatusConst.CR_PENDING_APPROVAL.intValue() == worklistItemVO.getDocStatus().intValue() || 
												ContractStatusConst.CR_PENDING_REVISION_APPROVAL.intValue() == worklistItemVO.getDocStatus().intValue() )) {
						    		   worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.ct.review.circular.cancellation", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   }
						       }
						       if(worklistItemVO.getTaskName().equals(CTProcessNameConst.REVISE_CIRCULAR_CANCELLATION_RQUEST_HT_NAME)){
						    	   if (worklistItemVO.getDocStatus() != null
											&& ContractStatusConst.CR_PENDING_REVISION.intValue() == worklistItemVO.getDocStatus().intValue()) {
						    		   worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.ct.revise.circular.cancellation", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   }
						       }
						       
						    // 12177 - Start
						       IQuoteSO quoteSO;
					    	   quoteSO = BusinessDelegate.getInstance().getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
					    	   QuoteResDTO quoteResDTO = null;
						       Long currentStatusId = Long.valueOf(worklistItemVO.getDocStatus());
						       
						       if(worklistItemVO.getDocType().equals("SQ")){
								   if(currentStatusId.equals(DPStatusConstants.SQ_PENDING_RN_CREATION)){
									   quoteResDTO = quoteSO.getQuoteInfoFromOtherDocNo(worklistItemVO.getDocNumber(), 
											    currentStatusId, worklistItemVO.getDocType());	
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
										   quoteResDTO = quoteSO.getQuoteDtlByQuoteNo(quotereqDTO);
										   if(quoteResDTO.getQuoteBidDtl().getPurchaseInDefault() != null){
											   Calendar cal = Calendar.getInstance();
												cal.clear(); 
												cal.setTime(quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getChangedDate());
												cal.add(Calendar.DATE, quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getPidValidDays());
											   worklistItemVO.setExpirationDate(cal);
										   }

						    			   
						    		   }
								   }
								   
								   if(currentStatusId.equals(DPStatusConstants.SQ_PENDING_SUBMISSION)){
									   QuoteReqDTO quotereqDTO = new QuoteReqDTO();
									   quotereqDTO.setQuoteNo(worklistItemVO.getDocNumber());
									   quoteResDTO = quoteSO.getQuoteDtlByQuoteNo(quotereqDTO);
									   if(quoteResDTO.getQuoteBidDtl().getPurchaseInDefault() != null){
										   Calendar cal = Calendar.getInstance();
											cal.clear(); 
											cal.setTime(quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getChangedDate());
											cal.add(Calendar.DATE, quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getPidValidDays());
										   worklistItemVO.setExpirationDate(cal);
									   }
								   }
								   
								   if(currentStatusId.equals(DPStatusConstants.PID_EXPIRED)){
									   QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
									   quoteReqDTO.setQuoteNo(worklistItemVO.getDocNumber());
									   quoteResDTO = quoteSO.getQuoteDtlByQuoteNo(quoteReqDTO);	
									   Date pidExpiredDate = new Date(); 
									   pidExpiredDate = DateUtils.addDays(quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getChangedDate(), quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getPidValidDays());
									   
									   Calendar pidExpiredDateCL = Calendar.getInstance();
									   pidExpiredDateCL.setTime(pidExpiredDate);
									   worklistItemVO.setExpirationDate(pidExpiredDateCL);
									   
								   }
						       }
						       // 12177 - End
						       
						       //pid validity
						       
						       if(worklistItemVO.getDocType().equals("RN")){
						    	   System.out.println("Begin PID validity process");
						    	   IRequestNoteSO requestSO = (IRequestNoteSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
						    	   IRequestItemSO requestItemSO = (IRequestItemSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestItemSO.class);
						    	   RequestNoteReqDTO reqRNDTO = new RequestNoteReqDTO();
						    	   RequestNoteResDTO resRNDTO = new RequestNoteResDTO();
						    	   
						    	   reqRNDTO.setRequestNoteNo(worklistItemVO.getDocNumber());
						    	   resRNDTO = requestSO.getRequestNote(reqRNDTO);
						    	   
						    	   if(resRNDTO.getRequestNoteDetail().getRequestNoteDtls().get(0).getRequestItem().getPurchaseInDefault()!=null) {
										int pidValidDays = resRNDTO.getRequestNoteDetail().getRequestNoteDtls().get(0).getRequestItem().getPurchaseInDefault().getPidValidDays();
										Date changedDate = resRNDTO.getRequestNoteDetail().getRequestNoteDtls().get(0).getRequestItem().getPurchaseInDefault().getChangedDate();
										Date pidExpiredDate = new Date(); 
										
										
											pidExpiredDate = DateUtils.addDays(changedDate, pidValidDays);
										
											Calendar pidExpiredDateCL = Calendar.getInstance();
											   pidExpiredDateCL.setTime(pidExpiredDate);
											   worklistItemVO.setExpirationDate(pidExpiredDateCL);
										
										if(DateUtils.getCurrentDate().after(pidExpiredDate))  {
											//set overide status here 
											//taskListMyTaskDataModel.getRowKey(worklistItemVO).
											worklistItemVO.setDocStatus(DPStatusConstants.PID_EXPIRED.intValue());
											
											
											//System.out.println("Lets overide the status! here");
										}
									}else{ // 12177 - Start
										if(currentStatusId.equals(DPStatusConstants.RN_PENDING_ITEM_CODIFICATION)
										   || currentStatusId.equals(DPStatusConstants.RN_PENDING_APPROVAL)
								    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION) 
								    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION_APPROVAL)
								    	   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)){
									       quoteResDTO = quoteSO.getQuoteInfoFromOtherDocNo(worklistItemVO.getDocNumber(), 
														 currentStatusId, worklistItemVO.getDocType());	
									       if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
									    	   if(!isSqStillValid(quoteResDTO.getEndDate())){
									    		   worklistItemVO.setDocStatus(DPStatusConstants.SQ_EXPIRED.intValue());							    		   
											   }
									    	   if(getExpirationDate() != null){
									    		   worklistItemVO.setExpirationDate(getExpirationDate());
									    	   }							    	   
									       }   	   
								        }
									}
						    	   // 12177 - End
						       }
						       else  if(worklistItemVO.getDocType().equals("PR")){
						    	   System.out.println("Begin PID validity process");
						    	  // IPurchaseRequestSO requestSO = (IPurchaseRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseRequestSO.class);
						    	   IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
						    	   //IRequestItemSO requestItemSO = (IRequestItemSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestItemSO.class);
						    	   FulfilmentReqDTO reqFLDTO = new FulfilmentReqDTO();
						    	   FulfilmentResDTO resFLDTO = new FulfilmentResDTO();
						    	   
						    	   reqFLDTO.setFlDocNo(worklistItemVO.getDocNumber());
						    	   resFLDTO = fulfilmentSO.retrieveFulfilmentRequestByDocNo(reqFLDTO);
						    	   
						    	   if(resFLDTO.getFlFulfilmentRequest().getScPurchaseInDefault()!=null) {
										int pidValidDays = resFLDTO.getFlFulfilmentRequest().getScPurchaseInDefault().getPidValidDays();
										Date pidExpiredDate = new Date(); 
										
										if(resFLDTO.getFlFulfilmentRequest().getScPurchaseInDefault().getChangedDate()!=null) {
											pidExpiredDate = DateUtils.addDays(resFLDTO.getFlFulfilmentRequest().getScPurchaseInDefault().getChangedDate(), pidValidDays);
										}
										
										Calendar pidExpiredDateCL = Calendar.getInstance();
										pidExpiredDateCL.setTime(pidExpiredDate);
										worklistItemVO.setExpirationDate(pidExpiredDateCL);
										
										if(DateUtils.getCurrentDate().after(pidExpiredDate)) {
											//set overide status here 
											//taskListMyTaskDataModel.getRowKey(worklistItemVO).
											worklistItemVO.setDocStatus(DPStatusConstants.PID_EXPIRED.intValue());						   
											//System.out.println("Lets overide the status! here");
										}
										
								   }else{ // 12177 - Start
									   if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL)
										  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION)
										  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
										  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)){
										   quoteResDTO = quoteSO.getQuoteInfoFromOtherDocNo(worklistItemVO.getDocNumber(), 
													 currentStatusId, worklistItemVO.getDocType());	
										   if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
									    	   if(!isSqStillValid(quoteResDTO.getEndDate())){
									    		   worklistItemVO.setDocStatus(DPStatusConstants.SQ_EXPIRED.intValue());
											   }
									    	   if(getExpirationDate() != null){
									    		   worklistItemVO.setExpirationDate(getExpirationDate());
									    	   }
										   }
									   }
								   }
						    	   // 12177 - End
						       }
						       // 12177 - Start
						       else if(worklistItemVO.getDocType().equals("LA")){
							    	  if(currentStatusId.equals(QTStatusConst.LOA_PENDING_SUBMISSION)
										  || currentStatusId.equals(QTStatusConst.LOA_PENDING_APPROVAL)
							    		  || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION)
							    		  || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION_APPROVAL)){
							    		  quoteResDTO = quoteSO.getQuoteInfoFromOtherDocNo(worklistItemVO.getDocNumber(), 
													 currentStatusId, worklistItemVO.getDocType());	
										   if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
									    	   if(!isSqStillValid(quoteResDTO.getEndDate())){
									    		   worklistItemVO.setDocStatus(DPStatusConstants.SQ_EXPIRED.intValue());
											   }
									    	   if(getExpirationDate() != null){
									    		   worklistItemVO.setExpirationDate(getExpirationDate());
									    	   }
										   }  			   
							    	   }
							    }
						       // 12177 - End
							}catch(Exception ex){
								worklistItemVO.setTitle("Please check");
								log.info("worklistItemVO.setTitle error:" + ex.getMessage());
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

	public void setTaskListMyTaskDataModel(
			PagingDataModel<WorklistItemVO> taskListMyTaskDataModel) {
		this.taskListMyTaskDataModel = taskListMyTaskDataModel;
	}

	private class taskPagingGroupDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;
		private final Logger log = EpLogger.getLogger(taskPagingGroupDataModel.class);

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

				log.info("testing BPM auth******" + token);
				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED"); // TODO : ADD filter for state and set do variable

				reqDTO.setDocType(getDocTypeGroup());

				if(getTaskDescGroup()!=null && getTaskDescGroup().trim().length()>0){
					reqDTO.setTaskTitle(getTaskDescGroup());
				}
				
				log.info("getDocNoGroup():" + getDocNoGroup());
				if(getDocNoGroup()!=null && getDocNoGroup().trim().length()>0){
					reqDTO.setDocNumber(getDocNoGroup());
				}
			
				/*if(getSelectedOrgProfileGroup()!=null){
					System.out.println("getOrgProfileId"+getSelectedOrgProfileGroup().getOrgProfileId());
				}*/
				
				if(getDocStatusGroup()>0){
					log.info("getDocStatusGroup" + getDocStatusGroup());
					reqDTO.setStatus(getDocStatusGroup());
				}
				
				reqDTO.setViewBy(getViewByGroup());
				
				//System.out.println("***status**"+getSelectedStatusDescAdvance().getStatusName());
				
				
				//reqDTO.setStatusList(statusList);
				reqDTO.setAssignment(BpmConstants.ASSIGNMENT_GROUP);
				reqDTO.setIsClaimed(false);
				reqDTO.setCtxToken(token);
				
				//SDO User - filer Search
				sdoSearchFilter(reqDTO);
				BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
				SearchResultBean<WorklistItemVO> resultBean = new SearchResultBean<WorklistItemVO>();
				
				if (responseDTO.getWorklistVO() != null) {
					log.info("testing BPM******size=" + responseDTO.getWorklistVO().getWorklistItem().size());
					log.info("testing BPM******total=" + responseDTO.getWorklistVO().getTotal());

					if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
						boolean viewGlpFlag = viewGlpFlag();
						for(WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							try {
								if (worklistItemVO.getTitle().matches("<")) {
									worklistItemVO.setTitle(worklistItemVO.getTitle().replaceAll("<", "&#60;"));
								}

								if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE_DRAFT)) {
									log.info("Doc number = " + worklistItemVO.getDocNumber());
									ScPrInquiry scPrInquiry = retrivePrInquiryByPrInquiryNo(worklistItemVO
											.getDocNumber());
									if (scPrInquiry != null) {
										worklistItemVO.setTitle(scPrInquiry.getTitle());
										log.info("prno = " + scPrInquiry.getPrInquiryNo() + "title = "
												+ scPrInquiry.getTitle());
									}
								}
								// s1 module
								if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
									worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
								}
								if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
									worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
								}
								if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
									worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
											"lbl.sc.purchase.in.default", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
											face.getUserLocale()));
								}
								processTitleWithDocStatus(worklistItemVO, face);

						       if(worklistItemVO.getTitle().startsWith("lbl.sm")) {
						    		MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
									log.info("***** The document Id for the workflow is ****** "
											+ worklistItemVO.getDocId());
									mofResDto = retriveCompanyNameByApplicationId(worklistItemVO.getDocId());
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   if(mofResDto.getCompanyName() != null){
						    		   	   sb.append(" ( ");
								    	   sb.append(mofResDto.getCompanyName());
								    	   sb.append(" ) ");
								    	   if(viewGlpFlag && mofResDto.isGlp()) {
								    		   sb.append("<font color=\"red\">&#x26A0;GLP </font>");
								    	   }
						    	   }
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }
						       
						       
						       if(worklistItemVO.getTitle().startsWith("lbl.cd")) {
						    	
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }
						       
						       if(worklistItemVO.getTitle().startsWith("lbl.pm")) {
						    	   AccessApplResDTO accessApplResDTO = 
						    			   retrieveAccessApplByDocNo(worklistItemVO.getDocNumber());
						    	   if(accessApplResDTO.isSuccessful() && 
						    			   accessApplResDTO.getAccessAppl() != null){
						    		  
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
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.PERFORM_PAYMNET_MATCH_HT)){
						    	   worklistItemVO.setTitle(retreiveSupplierInvoiceNo(worklistItemVO.getDocId()) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
						       }
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.CREATE_FRN_HT) 
						    		   || worklistItemVO.getTaskName().equals(FLProcessNameConst.REVISE_FRN_HT)){
						    	   worklistItemVO.setTitle(retreiveSupplierDoRefNo(worklistItemVO.getDocId(),false) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
							       //start added for #24968
						    	   retrievePoCoNoForCreateFRN(worklistItemVO,worklistItemVO.getTaskId());
						    	   //end added for #24968
						       }
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.APPROVE_FRN_HT)){
						    	   worklistItemVO.setTitle(retreiveSupplierDoRefNo(worklistItemVO.getDocId(),true) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
							       //start added for #24968
						    	   retrievePoCoNoForApproveFRN(worklistItemVO,worklistItemVO.getTaskId());
						    	   //end added for #24968
						       }
							   //start added for #24968
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.ACK_MODIFIED_DO_HT)){
							    	   retrievePoCoNoForAckModifyDo(worklistItemVO,worklistItemVO.getTaskId());
							       }
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.REVIEW_CANCEL_DO_HT)){
						    	   retrievePoCoNoForReviewCancelDo(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.REVIEW_DAN_HT)){
						    	   retrievePoCoNoForReviewDan(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_STOP_FULFILMENT_REQ_HT)
						    		   || worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_STOP_FULFILMENT_REQ_HT)){
						    	   retrievePoCoNoForStopFulFilment(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_FAILED_CANCEL_FRN_HT)){
						    	   retrievePoCoNoForSubmitFailedCancelFRN(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.ACK_MODIFIED_INVOICE_HT)){
						    	   retrievePoCoNoForAckModifiedInvoice(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_CANCEL_INVOICE_HT)){
						    	   retrievePoCoNoForCancelInvoice(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       //end added for #24968
							} catch (Exception ex) {
								worklistItemVO.setTitle("please check");
								log.info("worklistItemVO.setTitle error:" + ex.getMessage());
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
	
	// #24714
	private class taskPagingBothDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;
		private final Logger log = EpLogger.getLogger(taskPagingBothDataModel.class);

		public taskPagingBothDataModel() {
			super("taskListBothTaskId"); // Datatables ID
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			IBpmSO bpmSO = null;
			try {
				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

				// String token = bpmSO.authenticate("roslan", "welcome1");
				FacesScopes face = new FacesScopes();
				Object token = face.getBPMAuthenticationCtxToken();

				log.info("testing BPM auth******" + token);
				BpmRequestDTO reqDTO = new BpmRequestDTO();
				reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
				reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				reqDTO.setState("ASSIGNED"); // TODO : ADD filter for state and set do variable
				
				reqDTO.setDocType(getDocType());

				if(getTaskDesc()!=null && getTaskDesc().trim().length()>0){
					reqDTO.setTaskTitle(getTaskDesc());
				}
				
				log.info("getDocNoGroup():"+getDocNo());
				
				if(getDocNo()!= null && getDocNo().trim().length() > 0){
					reqDTO.setDocNumber(getDocNo());
				}
			
				if(getDocStatus() > 0){
					log.info("getDocStatus"+getDocStatus());
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
						boolean viewGlpFlag = viewGlpFlag();
						
						for( WorklistItemVO worklistItemVO : responseDTO.getWorklistVO().getWorklistItem()) {
							try {
								if (worklistItemVO.getTitle().matches("<")) {
									worklistItemVO.setTitle(worklistItemVO.getTitle().replaceAll("<", "&#60;"));
								}
								if (worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE_DRAFT)) {
									log.info("Doc number = " + worklistItemVO.getDocNumber());
									ScPrInquiry scPrInquiry = retrivePrInquiryByPrInquiryNo(worklistItemVO
											.getDocNumber());
									if (scPrInquiry != null) {
										worklistItemVO.setTitle(scPrInquiry.getTitle());
										log.info("prno = " + scPrInquiry.getPrInquiryNo() + "title = "
												+ scPrInquiry.getTitle());
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
								worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
							}
							
							//sm module
							//worklistItemVO.setTitle(SmCommonUtil.getSmBpmTaskTitle(worklistItemVO.getTitle(),worklistItemVO.getProcess()));
							//sm module
						       if(worklistItemVO.getTitle().startsWith("lbl.sm")) {
						    		MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
						    		log.info("***** The document Id for the workflow is ****** "+worklistItemVO.getDocId());
									mofResDto = retriveCompanyNameByApplicationId(worklistItemVO.getDocId());
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   if(mofResDto.getCompanyName() != null){
						    		   	   sb.append(" ( ");
								    	   sb.append(mofResDto.getCompanyName());
								    	   sb.append(" ) ");
								    	   if(viewGlpFlag && mofResDto.isGlp()) {
								    		   sb.append("<font color=\"red\">&#x26A0;GLP </font>");
								    	   }
						    	   }
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }

						       if(worklistItemVO.getTitle().startsWith("lbl.cd")) {
						    	
						    	   StringBuilder sb = new StringBuilder();
						    	   sb.append(ResourceBundleUtils.getMessage(worklistItemVO.getTitle(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
						    	   
						    	   
						        worklistItemVO.setTitle(sb.toString());
						       }
						       
						       if(worklistItemVO.getTitle().startsWith("lbl.pm")) {
						    	   AccessApplResDTO accessApplResDTO = 
						    			   retrieveAccessApplByDocNo(worklistItemVO.getDocNumber());
						    	   if(accessApplResDTO.isSuccessful() && 
						    			   accessApplResDTO.getAccessAppl() != null){
						    		  
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
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.PERFORM_PAYMNET_MATCH_HT)){
						    	   worklistItemVO.setTitle(retreiveSupplierInvoiceNo(worklistItemVO.getDocId()) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
						       }
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.CREATE_FRN_HT) 
						    		   || worklistItemVO.getTaskName().equals(FLProcessNameConst.REVISE_FRN_HT)){
						    	   worklistItemVO.setTitle(retreiveSupplierDoRefNo(worklistItemVO.getDocId(),false) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
							       //start added for #24968
						    	   retrievePoCoNoForCreateFRN(worklistItemVO,worklistItemVO.getTaskId());
						    	   //end added for #24968
						       }
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.APPROVE_FRN_HT)){
						    	   worklistItemVO.setTitle(retreiveSupplierDoRefNo(worklistItemVO.getDocId(),true) + " <br/> " + worklistItemVO.getTitle().replaceAll("<", "&#60;"));
							       //start added for #24968
						    	   retrievePoCoNoForApproveFRN(worklistItemVO,worklistItemVO.getTaskId());
						    	   //end added for #24968
						       }
							   //start added for #24968
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.ACK_MODIFIED_DO_HT)){
							    	   retrievePoCoNoForAckModifyDo(worklistItemVO,worklistItemVO.getTaskId());
							       }
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.REVIEW_CANCEL_DO_HT)){
						    	   retrievePoCoNoForReviewCancelDo(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equals(FLProcessNameConst.REVIEW_DAN_HT)){
						    	   retrievePoCoNoForReviewDan(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_STOP_FULFILMENT_REQ_HT)
						    		   || worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_STOP_FULFILMENT_REQ_HT)){
						    	   retrievePoCoNoForStopFulFilment(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_FAILED_CANCEL_FRN_HT)){
						    	   retrievePoCoNoForSubmitFailedCancelFRN(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.ACK_MODIFIED_INVOICE_HT)){
						    	   retrievePoCoNoForAckModifiedInvoice(worklistItemVO,worklistItemVO.getTaskId());
						       }
						       
						       if(worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_CANCEL_INVOICE_HT)){
						    	   retrievePoCoNoForCancelInvoice(worklistItemVO,worklistItemVO.getTaskId());
						       }
								// end added for #24968
						       
						       // 12177 - Start
						       IQuoteSO quoteSO;
					    	   quoteSO = BusinessDelegate.getInstance().getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
					    	   QuoteResDTO quoteResDTO = null;
						       Long currentStatusId = Long.valueOf(worklistItemVO.getDocStatus());
						       
						       if(worklistItemVO.getDocType().equals("SQ")){
								   if(currentStatusId.equals(DPStatusConstants.SQ_PENDING_RN_CREATION)){
									   quoteResDTO = quoteSO.getQuoteInfoFromOtherDocNo(worklistItemVO.getDocNumber(), 
											    currentStatusId, worklistItemVO.getDocType());	
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
										   quoteResDTO = quoteSO.getQuoteDtlByQuoteNo(quotereqDTO);
										   if(quoteResDTO.getQuoteBidDtl().getPurchaseInDefault() != null){
											   Calendar cal = Calendar.getInstance();
												cal.clear(); 
												cal.setTime(quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getChangedDate());
												cal.add(Calendar.DATE, quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getPidValidDays());
											   worklistItemVO.setExpirationDate(cal);
										   }

						    			   
						    		   }
								   }
								   
								   if(currentStatusId.equals(DPStatusConstants.SQ_PENDING_SUBMISSION)){
									   QuoteReqDTO quotereqDTO = new QuoteReqDTO();
									   quotereqDTO.setQuoteNo(worklistItemVO.getDocNumber());
									   quoteResDTO = quoteSO.getQuoteDtlByQuoteNo(quotereqDTO);
									   if(quoteResDTO.getQuoteBidDtl().getPurchaseInDefault() != null){
										   Calendar cal = Calendar.getInstance();
											cal.clear(); 
											cal.setTime(quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getChangedDate());
											cal.add(Calendar.DATE, quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getPidValidDays());
										   worklistItemVO.setExpirationDate(cal);
									   }
								   }
								   
								   if(currentStatusId.equals(DPStatusConstants.PID_EXPIRED)){
									   QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
									   quoteReqDTO.setQuoteNo(worklistItemVO.getDocNumber());
									   quoteResDTO = quoteSO.getQuoteDtlByQuoteNo(quoteReqDTO);	
									   Date pidExpiredDate = new Date(); 
									   pidExpiredDate = DateUtils.addDays(quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getChangedDate(), quoteResDTO.getQuoteBidDtl().getPurchaseInDefault().getPidValidDays());
									   
									   Calendar pidExpiredDateCL = Calendar.getInstance();
									   pidExpiredDateCL.setTime(pidExpiredDate);
									   worklistItemVO.setExpirationDate(pidExpiredDateCL);
									   
								   }
						       }
						       
						       if(worklistItemVO.getDocType().equals("RN")){ 
						    	   if(currentStatusId.equals(DPStatusConstants.RN_PENDING_ITEM_CODIFICATION)
						    		  || currentStatusId.equals(DPStatusConstants.RN_PENDING_APPROVAL)
								      || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION) 
								      || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION_APPROVAL)
								      || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)){
									  quoteResDTO = quoteSO.getQuoteInfoFromOtherDocNo(worklistItemVO.getDocNumber(), 
												currentStatusId, worklistItemVO.getDocType());	
									  if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
										  if(!isSqStillValid(quoteResDTO.getEndDate())){
											  worklistItemVO.setDocStatus(DPStatusConstants.SQ_EXPIRED.intValue());
										  }
										  if(getExpirationDate() != null){
								    		  worklistItemVO.setExpirationDate(getExpirationDate());
								    	  }
									  }else{
										  System.out.println("Begin PID validity process");
								    	   IRequestNoteSO requestSO = (IRequestNoteSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
								    	   IRequestItemSO requestItemSO = (IRequestItemSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestItemSO.class);
								    	   RequestNoteReqDTO reqRNDTO = new RequestNoteReqDTO();
								    	   RequestNoteResDTO resRNDTO = new RequestNoteResDTO();
								    	   
								    	   reqRNDTO.setRequestNoteNo(worklistItemVO.getDocNumber());
								    	   resRNDTO = requestSO.getRequestNote(reqRNDTO);
								    	   
								    	   if(resRNDTO.getRequestNoteDetail().getRequestNoteDtls().get(0).getRequestItem().getPurchaseInDefault()!=null) {
								    		   int pidValidDays = resRNDTO.getRequestNoteDetail().getRequestNoteDtls().get(0).getRequestItem().getPurchaseInDefault().getPidValidDays();
												Date changedDate = resRNDTO.getRequestNoteDetail().getRequestNoteDtls().get(0).getRequestItem().getPurchaseInDefault().getChangedDate();
												Date pidExpiredDate = new Date(); 
												
												
													pidExpiredDate = DateUtils.addDays(changedDate, pidValidDays);
													
													Calendar pidExpiredDateCL = Calendar.getInstance();
													   pidExpiredDateCL.setTime(pidExpiredDate);
													   worklistItemVO.setExpirationDate(pidExpiredDateCL);
												
												if(pidExpiredDate.compareTo(DateUtils.getCurrentDate())<0) {
													//set overide status here 
													//taskListMyTaskDataModel.getRowKey(worklistItemVO).
													worklistItemVO.setDocStatus(DPStatusConstants.PID_EXPIRED.intValue());
													
													//System.out.println("Lets overide the status! here");
												}
									  }
								   }
						    	   }
						       }
						       
						       if(worklistItemVO.getDocType().equals("PR")){
						    	   if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL)
						    		  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION)
									  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
									  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)){
									  quoteResDTO = quoteSO.getQuoteInfoFromOtherDocNo(worklistItemVO.getDocNumber(), 
												 currentStatusId, worklistItemVO.getDocType());	
									  if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
										  if(!isSqStillValid(quoteResDTO.getEndDate())){
											  worklistItemVO.setDocStatus(DPStatusConstants.SQ_EXPIRED.intValue());
										  }
										  if(getExpirationDate() != null){
								    		  worklistItemVO.setExpirationDate(getExpirationDate());
								    	  }
									  }else if(quoteResDTO.getPid() != null){
										  System.out.println("Begin PID validity process");
								    	  // IPurchaseRequestSO requestSO = (IPurchaseRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseRequestSO.class);
								    	   IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
								    	   //IRequestItemSO requestItemSO = (IRequestItemSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestItemSO.class);
								    	   FulfilmentReqDTO reqFLDTO = new FulfilmentReqDTO();
								    	   FulfilmentResDTO resFLDTO = new FulfilmentResDTO();
								    	   
								    	   reqFLDTO.setFlDocNo(worklistItemVO.getDocNumber());
								    	   resFLDTO = fulfilmentSO.retrieveFulfilmentRequestByDocNo(reqFLDTO);
								    	   
								    	   if(resFLDTO.getFlFulfilmentRequest().getScPurchaseInDefault()!=null) {
												int pidValidDays = resFLDTO.getFlFulfilmentRequest().getScPurchaseInDefault().getPidValidDays();
												Date pidExpiredDate = new Date(); 
												
												if(resFLDTO.getFlFulfilmentRequest().getScPurchaseInDefault().getChangedDate()!=null) {
													pidExpiredDate = DateUtils.addDays(resFLDTO.getFlFulfilmentRequest().getScPurchaseInDefault().getChangedDate(), pidValidDays);
												}
												
												Calendar pidExpiredDateCL = Calendar.getInstance();
												   pidExpiredDateCL.setTime(pidExpiredDate);
												   worklistItemVO.setExpirationDate(pidExpiredDateCL);
												   
												if(pidExpiredDate.compareTo(DateUtils.getCurrentDate())<0) {
													//set overide status here 
													//taskListMyTaskDataModel.getRowKey(worklistItemVO).
													worklistItemVO.setDocStatus(DPStatusConstants.PID_EXPIRED.intValue());
													
													//System.out.println("Lets overide the status! here");
												}
										   }
									  }
								   }
						       }
						       
						       if(worklistItemVO.getDocType().equals("LA")){
						    	   if(currentStatusId.equals(QTStatusConst.LOA_PENDING_SUBMISSION)
									  || currentStatusId.equals(QTStatusConst.LOA_PENDING_APPROVAL)
								      || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION)
								      || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION_APPROVAL)){
								      quoteResDTO = quoteSO.getQuoteInfoFromOtherDocNo(worklistItemVO.getDocNumber(), 
													currentStatusId, worklistItemVO.getDocType());	
									  if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
										  if(!isSqStillValid(quoteResDTO.getEndDate())){
											  worklistItemVO.setDocStatus(DPStatusConstants.SQ_EXPIRED.intValue());
										  }
										  if(getExpirationDate() != null){
								    		  worklistItemVO.setExpirationDate(getExpirationDate());
								    	  }
									  }  			   
								   }
						       }
						       // 12177 - End
						       
						       // Identify if it is a group or individual task
						       worklistItemVO.getTaskType();
						       
							} catch(Exception ex) {
								worklistItemVO.setTitle("please check");
								//log.info("worklistItemVO.setTitle error:"+ex.getMessage());
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

	public static Map getBpmMap() {

		return bpmMap;
	}

	public static void setBpmMap(Map bpmMap) {
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

	private boolean viewGlpFlag() {
		FacesScopes fs = new FacesScopes();
		if (fs.hasRoleUser(RoleCode.UPS_APPROVER.value())
				|| fs.hasRoleUser(RoleCode.UPS_PO.value())
				|| fs.hasRoleUser(RoleCode.UPS_SDO.value())) {
			return true;
		}
		return false;
	}

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
	
	public void retrievePoCoNoForCreateFRN(WorklistItemVO worklistItemVO,String taskId) {
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
	
	public void retrievePoCoNoForAckModifyDo(WorklistItemVO worklistItemVO,String taskId) {
		
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
	
	public void retrievePoCoNoForReviewCancelDo(WorklistItemVO worklistItemVO,String taskId) {
		
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
	
	public void retrievePoCoNoForApproveFRN(WorklistItemVO worklistItemVO,String taskId) {
		
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
	
	public void retrievePoCoNoForReviewDan(WorklistItemVO worklistItemVO,String taskId) {
		
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
	
	public void retrievePoCoNoForStopFulFilment(WorklistItemVO worklistItemVO,String taskId) {
		
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
	
	public void retrievePoCoNoForSubmitFailedCancelFRN(WorklistItemVO worklistItemVO,String taskId) {
		
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
	
	public void retrievePoCoNoForAckModifiedInvoice(WorklistItemVO worklistItemVO,String taskId) {
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
	
	public void retrievePoCoNoForCancelInvoice(WorklistItemVO worklistItemVO,String taskId) {		
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
	public boolean isSqStillValid(Date sqEndDate){		
		
		Boolean validDate = true;	
		Integer sqTimeValidity = null;
			
		// by default days
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);	
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(ParameterConst.SIMPLE_QUOTE);
		List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList();
		if (ruleValueList != null && ruleValueList.size() > 0) {
			PmConfigRuleValue configRuleValue = ruleValueList.get(0);
			sqTimeValidity = Integer.valueOf(configRuleValue.getRuleValue());
		}	
			
		if(sqEndDate != null){
				Date currentDate = new Date();
				Date closingDate = sqEndDate;
					
				Calendar cal = Calendar.getInstance();
				cal.clear(); 
				cal.setTime(closingDate);
				cal.add(Calendar.DATE, sqTimeValidity); //add 60days to closing/end date
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
	
}
