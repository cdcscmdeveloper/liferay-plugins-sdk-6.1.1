package my.ep.web.constant.sc;

/**
 * QT LOG CONSTANT -> TO BE USED FOR SLAUTILS.LOGERRORSLA Method
 * @author shafful
 *
 */
public class SLALogConstant {
	public static final String QT_SLA_DOT_SEPARATOR = ".";

	/*
	 * MODULE
	 */
	public static final String QT_MODULE = "QT"; // #25426
	public static final String COMMON_MODULE = "COMMON"; //#28895#35492
	
	/*
	 * CLASS_NAME
	 */
	// 25037 - start
	public static final String QT_EVALUATION_MEETING_BACKINGBEAN = "EvaluationMeetingBackingBean";
	public static final String QT_APPROVAL_BACKINGBEAN = "ApprovalBackingBean";
	public static final String QT_DETAIL_BACKINGBEAN = "QTDetailBackingBean";
	// 25037 - end

	// #25426 - start
	public static final String QT_SUPPLIER_FINALIZATION_BACKINGBEAN = "SupplierFinalizationBackingBean";
	// #25426 - end
	
	public static final String QT_LOA_DETAIL_BACKINGBEAN = "LOADetailBackingBean"; // #29007
	public static final String QT_DIRECT_LOA_DETAIL_BACKINGBEAN = "DirectLOABackingBean"; // #29007

	/*
	 * METHOD_NAME
	 */
	// 25037 - start
	public static final String QT_PRERENDER_GENERIC_APPROVAL = "preRenderGenericApproval";
	public static final String QT_UPDATE_GENERIC_APPROVAL_TASK = "updateGenericApprovalTask";
	public static final String QT_PRESUBMIT_GENERIC_APPROVAL_TASK = "preSubmitGenericApprovalTask";
	public static final String QT_PREPARE_COMMITTEE_HISTORY = "prepareCommitteeHistory";
	public static final String QT_PREPARE_QT_INFO = "prepareQTInfo";

	// 25037 - end
	
	// #29007 -start-
	public static final String QT_GENERATE_LAMPIRANA3PDF_FROM_VELOCITY = "generateLampiranA3PdfFromVelocity";
	public static final String QT_CREATE_LAMPIRANA3FORQT = "createLampiranA3forQT";
	public static final String QT_CREATE_LAMPIRANA3PDF_FOR_QT_VERIFICATION = "createLampiranA3forQTVerification";

	// #29007 -end-
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

	private SLALogConstant() {

	}
}
