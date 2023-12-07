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
	//#29187 Start Vinod
	public static final String QT_EVALUATION_BACKINGBEAN = "EvaluationBackingBean";
	public static final String QT_SPECIFICATION_BACKINGBEAN = "SpecificationBackingBean";
	public static final String QT_TEMPLATESPEC_BACKINGBEAN = "SpecificationBackingBean";
	//#29187 End Vinod
	public static final String QT_BASEBACKINGBEAN = "BaseBackingBean";
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
	//#29187 End Vinod
	//#10141 Start Vinod
	public static final String QT_GENERATE_TECHNICAL_CHECKLIST_PRIVIEW_PDF_FROM_VELOCITY = "generateTechnicalChecklistPreviewPdfFromVelocity";
	public static final String QT_GENERATE_FINANCIAL_CHECKLIST_PRIVIEW_PDF_FROM_VELOCITY = "generateFinancialChecklistPreviewPdfFromVelocity";
	public static final String QT_DOWNLOAD_TECHNICAL_CHECKLIST_PRIVIEW = "downloadTechnicalChecklistPreview";
	public static final String QT_DOWNLOAD_FINANCIAL_CHECKLIST_PRIVIEW = "downloadFinancialChecklistPreview";
	//#10141 End Vinod
	public static final String QT_GET_RADIOBUTTONCODEVALUES_SEARCHBEAN = "getRadioButtonCodeValuesBySearchBean";
	public static final String QT_GET_DROPDOWNVALUES_SEARCHBEAN_FOR_PARTIAL= "getDropdownValuesBySearchBeanForPartial";
	public static final String QT_CREATE_LAMPIRANA3PDF_FOR_QT_VERIFICATION= "createLampiranA3forQTVerification";
	private SLALogConstant(){
		
	}	
}
