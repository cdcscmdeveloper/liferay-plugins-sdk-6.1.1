package my.ep.web.constant.sc;

/**
 * QT LOG CONSTANT -> TO BE USED FOR SLAUTILS.LOGERRORSLA Method
 * @author shafful
 *
 */
public class SLALogConstant {
	/*
	 * MODULE
	 */
	public static final String QT_MODULE = "QT";	
	public static final String COMMON_MODULE = "COMMON";
	/*
	 * CLASS_NAME
	 */
	public static final String QT_CLONE_BACKINGBEAN = "QTCloneBackingBean";
	public static final String QT_MULTIPLE_CLONE_BACKINGBEAN = "QTMultipleCloneBackingBean";
	public static final String QT_SUPPLIER_FINALIZATION_BACKINGBEAN = "SupplierFinalizationBackingBean";
	public static final String COMMON_BASE_BACKINGBEAN = "BaseBackingBean";
	public static final String QT_LOA_DETAIL_BACKINGBEAN = "LOADetailBackingBean";
	public static final String QT_DIRECT_LOA_DETAIL_BACKINGBEAN = "DirectLOABackingBean";
	public static final String QT_NOTICE_BOARD_BACKINGBEAN = "NoticeBoardBackingBean"; //Development #35054
	//12471 - start
	public static final String QT_COMMITTEE_BACKINGBEAN = "QTCommitteBackingBean";
	public static final String QT_EVALUATION_MEETING_BACKINGBEAN = "EvaluationMeetingBackingBean";
	public static final String QT_SUPPLIER_FINALIZATION_MEETING_BACKINGBEAN = "SupplierFinalizationMeetingBackingBean";
	public static final String QT_SOURCING_BACKINGBEAN = "SourcingBaseBackingBean";
	public static final String QT_USER_COMMITTEE_PAGING_DATA_MODEL = "UserCommitteePagingDataModel";
	public static final String QT_USER_QC_PAGING_DATA_MODEL = "UserQCPagingDataModel";
	public static final String QT_SLA_DOT_SEPARATOR = ".";
	//12471 - end

	public static final String QT_BASEBACKINGBEAN = "BaseBackingBean";
	
	// 25037 - start
	public static final String QT_APPROVAL_BACKINGBEAN = "ApprovalBackingBean";
	public static final String QT_DETAIL_BACKINGBEAN = "QTDetailBackingBean";
	// 25037 - end
	
	/*
	 * METHOD_NAME
	 */
	public static final String QT_RETRIEVE_PUBLICATION_APPROVAL_TAB_DTL = "retrievePublicationApprovalTabDtl";
	public static final String QT_RETRIEVE_APPROVAL = "retrieveApproval";
	public static final String QT_RETRIEVE_COMMITTEE_MEMBERS = "retrieveCommitteeMembers";
	public static final String QT_CLONE_COMMITTEE = "cloneComitteee";
	public static final String QT_POPULATE_APPROVALS = "populateApprovals";
	public static final String QT_POPULATE_COMMITTEE = "populateCommittee";
	public static final String QT_UPDATE_BPM_TASKBRIEF = "updateBPMTaskBrief";
	public static final String COMMON_GET_STATUSDROPDOWNVALUES_SEARCHBEAN = "getStatusDropdownValuesBySearchBean";
	public static final String QT_GENERATE_LAMPIRANA3PDF_FROM_VELOCITY = "generateLampiranA3PdfFromVelocity";
	public static final String  QT_NOTICEBOARDGFMAS= "noticeBoardGfmas"; //Development #35054
	public static final String QT_CREATE_LAMPIRANA3FORQT = "createLampiranA3forQT";
	//#29187 Start Vinod
	public static final String QT_GET_COMPLIANCE_DROPDOWN_VALUES = "getComplianceDropdownValues";
	public static final String QT_CHECK_SAMPLE_COMPLIANCE = "checkSampleCompliance";
	public static final String QT_CHECK_SPEC_COMPLIANCE = "checkTechnicalSpecCompliance";
	public static final String QT_GET_MANDATORY_SPEC_DROPDOWN_VALUES = "getMandatorySpecDropdownValues";
	public static final String QT_GET_MANDATORY_SPEC_AND_COMPLIANCE_DROPDOWN_VALUES = "getMandatorySpecAndComplianceDropdownValues";
	public static final String QT_EVALUATION_BACKINGBEAN = "EvaluationBackingBean";
	public static final String QT_SPECIFICATION_BACKINGBEAN = "SpecificationBackingBean";
	public static final String QT_TEMPLATESPEC_BACKINGBEAN = "TemplateSpecBackingBean";
	//#29187 End Vinod
	//#10141 Start Vinod
	public static final String QT_INITIATE_INACTIVE_CHECKLIST_PROCESS_FOR_DOC = "initiateInactiveChecklistProcessForDoc";
	public static final String QT_SPEC_PRE_RENDER_VIEW = "preRenderView";
	public static final String QT_DOWNLOAD_SCORE_REPORT = "downloadScoreReport";
	public static final String QT_DOWNLOAD_REPORT_SPEC_PREPARE = "downloadReportSpecPrepare";
	public static final String QT_PREVIEW_FILE = "previewFile";
	public static final String QT_PREVIEW_FILE_BY_INPUT_STREAM = "previewFileByInputStream";
	public static final String QT_DOWNLOAD_TECHNICAL_CHECKLIST_PRIVIEW = "downloadTechnicalChecklistPreview";
	public static final String QT_DOWNLOAD_FINANCIAL_CHECKLIST_PRIVIEW = "downloadFinancialChecklistPreview";
	public static final String QT_DOWNLOAD_SELECTED_TECHNICAL_CHECKLIST_PRIVIEW = "downloadSelectedTechnicalChecklistPreview";
	public static final String QT_DOWNLOAD_SELECTED_FINANCIAL_CHECKLIST_PRIVIEW = "downloadSelectedFinancialChecklistPreview";
	public static final String QT_SPEC_SCORE_PREVIEW_BACKINGBEAN = "SpecificationBackingBean";
	public static final String QT_GET_SCORE_REPORT = "getScoreReport";
	//#10141 End Vinod
	//#29468 Start Vinod
	public static final String QT_CREATE_TEMPLATE_SPEC_PAGING_DATA_MODEL = "CreateTemplateSpecPagingDataModel";
	public static final String QT_GET_CATEGORYCODES_FOR_CLONE = "getCategoryCodesForClone";
	public static final String QT_PROC_CATEGORY_CHANGED = "procCategoryChanged";
	//#29468 End Vinod
	public static final String QT_GET_RADIOBUTTONCODEVALUES_SEARCHBEAN = "getRadioButtonCodeValuesBySearchBean";
	public static final String QT_GET_DROPDOWNVALUES_SEARCHBEAN_FOR_PARTIAL= "getDropdownValuesBySearchBeanForPartial";
	public static final String QT_CREATE_LAMPIRANA3PDF_FOR_QT_VERIFICATION= "createLampiranA3forQTVerification";
	//12471 - start
	public static final String QT_BUILD_ORG_NAME_DD = "buildOrgNameDD";
	public static final String QT_BUILD_PTJ_DD = "buildPtjDD";
	public static final String QT_PAGE_CHANGE_LISTENER = "pageChangeListener";
	//12471 - end
	//06022024@Sonarqube QT#58 - haziq
	public static final String QT_CALC_TOTAL_AMOUNT = "calcTotalAmount";
	// 25037 - start
	public static final String QT_PRERENDER_GENERIC_APPROVAL = "preRenderGenericApproval";
	public static final String QT_UPDATE_GENERIC_APPROVAL_TASK = "updateGenericApprovalTask";
	public static final String QT_PRESUBMIT_GENERIC_APPROVAL_TASK = "preSubmitGenericApprovalTask";
	public static final String QT_PREPARE_COMMITTEE_HISTORY = "prepareCommitteeHistory";
	public static final String QT_PREPARE_QT_INFO = "prepareQTInfo";

	// 25037 - end
	
	// 25791-start
	public static final String QT_DETAIL_BACKING_BEAN = "QTDetailBackingBean";
	public static final String QT_RETRIVE_CONTRACT_DETAILS_LOA = "retrieveContractDtlsByLoa";
	public static final String QT_VIEW_LOA_CONTRACT_PAGING_MODEL = "ViewLoaContractPagingDataModel";
	public static final String QT_VIEW_CONTRACT_PAGING_MODEL = "ViewContractPagingDataModel";
	public static final String QT_VIEW_NORMAL_LOA_TRACKING_DTLS_PAGE = "viewNormalLoaTrackingDetailsPage";
	public static final String VIEW_LOA_CONTRACT_DETAILS_PAGE = "viewLoaContractDetailsPage";
	public static final String VIEW_DL_LOA_CONTRACT_TRACKING_DETAILS = "viewLoaContractTrackingDtls";
	public static final String VIEW_QT_LOA_CONTRACT_DTLS = "viewQtLoaContractDtls";
	public static final String VIEW_QT_BID_SCHEDULES_DATA_MODEL = "ScheduleBiddingPagingDataModel";
	public static final String COMP_EXP_LOA_DETAILS_PAGE = "viewLOADetailsPage";
	
	
	// 25791-end
	
	// 29661 -start-
	public static final String FETCH_CHECKLIST_VALUE_SCORE = "fetchCheckListValueScore";
	public static final String FETCH_COMPANY_EXP_PROPOSAL_CHECKLIST_SCORE = "fetchProposalAndCheckListScores";
	public static final String RETRIEVE_COMPANY_EXP_EVALUATION_DETAILS = "retrieveCompanyExpDetails";
	public static final String QT_SP_DETAIL_BACKINGBEAN = "SPDetailBackingBean";
	public static final String RETRIEVE_PROPOSAL_COMPANY_EXP_DETAILS = "retrieveProposalCompanyExpdtls";
	public static final String DOWNLOAD_SUPPLIER_LOA_DETAILS_REPORTS = "downloadCompExpLOAReports";
	public static final String RETRIEVE_SUPP_MAP_REPORT = "retrieveSuppLoiLoaMapForReport";
	public static final String COMP_EXP_SUPP_PAGING_DATA_MODEL = "ViewSupplierCompExpDtlsPagingDataModel";
	public static final String RETRIEVE_CONTRACT_STATUS_COUNT = "retrieveContractStatusValueCount";

	// 29661 -end-
	
	// 26247 - start
	public static final String QT_NOTICEBOARD_BACKING_BEAN = "NoticeBoardBackingBean";
	public static final String QT_NAVIGATE_TO_CHECK_QUALIFICATION = "navigateToCheckQualification";
	// 26247 - end
	
	private SLALogConstant(){
		
	}	
}
