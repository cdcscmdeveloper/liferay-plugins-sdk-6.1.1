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
	//12471 //28895-35500 - start
	public static final String QT_SUPPLIER_FINALIZATION_MEETING_BACKINGBEAN = "SupplierFinalizationMeetingBackingBean";
	//12471 - end	
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
	public static final String QT_CREATE_LAMPIRANA3FORQT = "createLampiranA3forQT";
	// #29187 Start Vinod
	public static final String QT_GET_COMPLIANCE_DROPDOWN_VALUES = "getComplianceDropdownValues";
	public static final String QT_CHECK_SAMPLE_COMPLIANCE = "checkSampleCompliance";
	public static final String QT_CHECK_SPEC_COMPLIANCE = "checkTechnicalSpecCompliance";
	public static final String QT_GET_MANDATORY_SPEC_DROPDOWN_VALUES = "getMandatorySpecDropdownValues";
	public static final String QT_GET_MANDATORY_SPEC_AND_COMPLIANCE_DROPDOWN_VALUES = "getMandatorySpecAndComplianceDropdownValues";
	public static final String QT_EVALUATION_BACKINGBEAN = "EvaluationBackingBean";
	public static final String QT_SPECIFICATION_BACKINGBEAN = "SpecificationBackingBean";
	public static final String QT_TEMPLATESPEC_BACKINGBEAN = "SpecificationBackingBean";
	// #29187 End Vinod
	//#29468 Start Vinod
	public static final String QT_CREATE_TEMPLATE_SPEC_PAGING_DATA_MODEL = "CreateTemplateSpecPagingDataModel";
	public static final String QT_GET_CATEGORYCODES_FOR_CLONE = "getCategoryCodesForClone";
	public static final String QT_PROC_CATEGORY_CHANGED = "procCategoryChanged";
	//#29468 End Vinod
	public static final String QT_BASEBACKINGBEAN = "BaseBackingBean";
	public static final String QT_GET_RADIOBUTTONCODEVALUES_SEARCHBEAN = "getRadioButtonCodeValuesBySearchBean";
	public static final String QT_GET_DROPDOWNVALUES_SEARCHBEAN_FOR_PARTIAL= "getDropdownValuesBySearchBeanForPartial";
	public static final String QT_CREATE_LAMPIRANA3PDF_FOR_QT_VERIFICATION= "createLampiranA3forQTVerification";
	// 25037 - start
	public static final String QT_PRERENDER_GENERIC_APPROVAL = "preRenderGenericApproval";
	public static final String QT_UPDATE_GENERIC_APPROVAL_TASK = "updateGenericApprovalTask";
	public static final String QT_PRESUBMIT_GENERIC_APPROVAL_TASK = "preSubmitGenericApprovalTask";
	public static final String QT_PREPARE_COMMITTEE_HISTORY = "prepareCommitteeHistory";
	public static final String QT_PREPARE_QT_INFO = "prepareQTInfo";
	public static final String QT_SLA_DOT_SEPARATOR = ".";
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
	// 25791-end	
	
	private SLALogConstant(){
		
	}	
}
