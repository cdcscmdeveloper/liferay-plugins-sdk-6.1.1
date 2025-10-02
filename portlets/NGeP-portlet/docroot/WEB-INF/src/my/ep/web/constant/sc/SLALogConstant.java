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
	
	// 25791-start
	public static final String QT_DETAIL_BACKING_BEAN = "QTDetailBackingBean";
	public static final String QT_RETRIEVE_CONTRACT_DETAILS_LOA = "retrieveContractDtlsByLOA";
	public static final String QT_VIEW_LOA_CONTRACT_PAGING_MODEL = "ViewLOAContractPagingDataModel";
	public static final String QT_VIEW_CONTRACT_PAGING_MODEL = "ViewContractPagingDataModel";
	public static final String QT_VIEW_NORMAL_LOA_TRACKING_DTLS_PAGE = "viewNormalLOATrackingDetailsPage";
	public static final String VIEW_LOA_CONTRACT_DETAILS_PAGE = "viewLOAContractDetailsPage";
	public static final String VIEW_DL_LOA_CONTRACT_TRACKING_DETAILS = "viewLOAContractTrackingDtls";
	public static final String VIEW_QT_LOA_CONTRACT_DTLS = "viewQTLOAContractDtls";
	public static final String VIEW_QT_BID_SCHEDULES_DATA_MODEL = "ScheduleBiddingPagingDataModel";
	// 25791-end
	
	// #29007 -start-
	public static final String QT_GENERATE_LAMPIRANA3PDF_FROM_VELOCITY = "generateLampiranA3PdfFromVelocity";
	public static final String QT_CREATE_LAMPIRANA3FORQT = "createLampiranA3forQT";
	public static final String QT_CREATE_LAMPIRANA3PDF_FOR_QT_VERIFICATION = "createLampiranA3forQTVerification";

	// #29007 -end-

	private SLALogConstant() {

	}
}
