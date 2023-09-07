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
	
	private SLALogConstant(){
		
	}	
}
