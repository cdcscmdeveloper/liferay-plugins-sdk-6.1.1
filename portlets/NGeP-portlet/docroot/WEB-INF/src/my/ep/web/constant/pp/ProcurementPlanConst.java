package my.ep.web.constant.pp;

public class ProcurementPlanConst {

	//Navigation
	public static final String NAV_TO_LIST_BUDGET_ALLOCATION = "listBudget";
	public static final String NAV_TO_BUDGET_ALLOCATION = "budgetAllocation";
	
	public static final String NAV_TO_LIST_PLAN = "listPlan";
	public static final String NAV_TO_PLAN_DET = "procurementPlan";
	
	public static final String NAV_TO_REVIEW_PLAN = "reviewPlan";
	public static final String NAV_TO_VERIFY_PLAN = "verifyPlan";
	public static final String NAV_TO_VIEW_PLAN = "viewPlan";
	public static final String NAV_TO_CREATE_MTO_PLAN = "createPlan";
	
	//View Mode
	public static final String MODE_NEW = "N";
	public static final String MODE_NEW_MTO = "T";
	public static final String MODE_REVISION = "S";
	public static final String MODE_VERIFICATION = "V";
	public static final String MODE_APPROVAL = "A";
	public static final String MODE_MAINTAIN = "M";
	public static final String MODE_VIEW = "R";
	public static final String MODE_LIST = "L";
	
	//Current Mode
	public static final String CURRENT_NEW = "tabN";
	public static final String CURRENT_MTO = "tabM";
	
	//Plan Type
	public static final String TYPE_PROCUREMENT = "P";
	public static final String TYPE_EXPENDITURE = "E";
	
	//Favourite Type
	public static final String FAVOURITE_TYPE_NONE = "N";
	public static final String FAVOURITE_TYPE_PLAN = "P";
	public static final String FAVOURITE_TYPE_ITEM = "I";
	
	//Procurement Mode
	public static final String PROCUREMENT_QUOTATION = "QT";
	public static final String PROCUREMENT_DIRECT_NEGO = "DN";
	public static final String PROCUREMENT_TENDER = "TD";
	public static final String PROCUREMENT_DIRECT_PURCHASE = "DP";
	public static final String PROCUREMENT_EXISTING_CONTRACT = "EC";
	
	//Fulfillment Type
	public static final String FULFILLMENT_ONE_OFF = "OF";
	public static final String FULFILLMENT_AS_AND_WHEN = "PW";
	public static final String FULFILLMENT_SCHEDULE = "PS";
	
	//QT Type
	public static final String QT_TYPE_BUMIPUTERA = "BP";
	public static final String QT_TYPE_OPEN = "OP";
	public static final String QT_TYPE_PANEL = "PN";
	public static final String QT_TYPE_RESTRICTED = "RT";

	//Goods or Services Type
	public static final String GOOD_SERVICE_TYPE_IMPORTED = "I";
	public static final String GOOD_SERVICE_TYPE_LOCAL = "L";
	public static final String GOOD_SERVICE_TYPE_BOTH = "B";
	
	//BPM
	public static final String BPM_REVISE = "REVISE";
	public static final String BPM_APPROVE = "APPROVE";
	public static final String BPM_SUBMIT = "SUBMIT";
	public static final String BPM_SAVE = "SAVE";

	
	//General
	public static final String YES_INT = "1";
	public static final String NO_INT = "0";
	public static final String SO_PP = "pp";
	
	//Contract Ind
	public static final String CONTRACT_IND_PKP = "P";
	public static final String CONTRACT_IND_LOA = "S";
	public static final String CONTRACT_IND_CONTRACT = "C";
	
	//Product Level
	public static final Integer UNSPSC_SEGMENT = 0;
	public static final Integer UNSPSC_FAMILY = 1;
	public static final Integer UNSPSC_CLASS = 2;
	public static final Integer UNSPSC_COMMODITY = 3;
	
	//PM Status
	public static final Long STATUS_PBA_NEW = 10001L;
	public static final Long STATUS_PBA_PENDING_APPROVAL = 10002L;
	public static final Long STATUS_PBD_NEW = 10005L;
	
	//PM Profile Types
	public static final Long TYPE_MINISTRY = 38L;
	public static final Long TYPE_JABATAN = 37L;
	public static final Long TYPE_PTJ = 1L;
	
	
	public static final Long STATUS_PENDING_APPROVAL = 2L;
	public static final Long STATUS_PENDING_REVISION = 3L;
	
	//Doc Type
	public static final String DOCTYPE_PLAN = "PPL";
	public static final String DOCTYPE_PLAN_ITEM = "PPI";
	public static final String DOCTYPE_BUDGET_ALLOC = "PBA";
	public static final String DOCTYPE_BUDGET_ALLOC_ITEM = "PBD";

	
	//PP Programs
	public static final String PROG_PLAN = "plan";
	public static final String PROG_BUDGET = "budget";
	
	/**
	 * @return the bpmSave
	 */
	public static String getBpmSave() {
		return BPM_SAVE;
	}
	/**
	 * @return the soPp
	 */
	public static String getSoPp() {
		return SO_PP;
	}
	
	/**
	 * @return the statusPbaNew
	 */
	public static Long getStatusPbaNew() {
		return STATUS_PBA_NEW;
	}
	/**
	 * @return the statusPbaPendingApproval
	 */
	public static Long getStatusPbaPendingApproval() {
		return STATUS_PBA_PENDING_APPROVAL;
	}
	/**
	 * @return the statusPbdNew
	 */
	public static Long getStatusPbdNew() {
		return STATUS_PBD_NEW;
	}
	/**
	 * @return the progPlan
	 */
	public static String getProgPlan() {
		return PROG_PLAN;
	}
	/**
	 * @return the progBudget
	 */
	public static String getProgBudget() {
		return PROG_BUDGET;
	}
	/**
	 * @return the statusPendingApproval
	 */
	public static Long getStatusPendingApproval() {
		return STATUS_PENDING_APPROVAL;
	}
	/**
	 * @return the statusPendingRevision
	 */
	public static Long getStatusPendingRevision() {
		return STATUS_PENDING_REVISION;
	}
	/**
	 * @return the doctypePlan
	 */
	public static String getDoctypePlan() {
		return DOCTYPE_PLAN;
	}
	/**
	 * @return the doctypePlanItem
	 */
	public static String getDoctypePlanItem() {
		return DOCTYPE_PLAN_ITEM;
	}
	/**
	 * @return the doctypeBudgetAlloc
	 */
	public static String getDoctypeBudgetAlloc() {
		return DOCTYPE_BUDGET_ALLOC;
	}
	/**
	 * @return the doctypeBudgetAllocItem
	 */
	public static String getDoctypeBudgetAllocItem() {
		return DOCTYPE_BUDGET_ALLOC_ITEM;
	}
	
	/**
	 * @return the unspscSegment
	 */
	public static Integer getUnspscSegment() {
		return UNSPSC_SEGMENT;
	}
	/**
	 * @return the unspscFamily
	 */
	public static Integer getUnspscFamily() {
		return UNSPSC_FAMILY;
	}
	/**
	 * @return the unspscClass
	 */
	public static Integer getUnspscClass() {
		return UNSPSC_CLASS;
	}
	/**
	 * @return the unspscCommodity
	 */
	public static Integer getUnspscCommodity() {
		return UNSPSC_COMMODITY;
	}
	/**
	 * @return the navToListBudgetAllocation
	 */
	public String getNavToListBudgetAllocation() {
		return NAV_TO_LIST_BUDGET_ALLOCATION;
	}
	/**
	 * @return the navToBudgetAllocation
	 */
	public String getNavToBudgetAllocation() {
		return NAV_TO_BUDGET_ALLOCATION;
	}
	/**
	 * @return the navToListPlan
	 */
	public String getNavToListPlan() {
		return NAV_TO_LIST_PLAN;
	}
	/**
	 * @return the navToCreatePlan
	 */
	public String getNavToPlanDet() {
		return NAV_TO_PLAN_DET;
	}
	/**
	 * @return the navToReviewPlan
	 */
	public String getNavToReviewPlan() {
		return NAV_TO_REVIEW_PLAN;
	}
	/**
	 * @return the navToVerifyPlan
	 */
	public String getNavToVerifyPlan() {
		return NAV_TO_VERIFY_PLAN;
	}
	/**
	 * @return the navToViewPlan
	 */
	public String getNavToViewPlan() {
		return NAV_TO_VIEW_PLAN;
	}
	/**
	 * @return the navToCreateMtoPlan
	 */
	public String getNavToCreateMtoPlan() {
		return NAV_TO_CREATE_MTO_PLAN;
	}
	/**
	 * @return the modeNew
	 */
	public String getModeNew() {
		return MODE_NEW;
	}
	/**
	 * @return the modeNewMto
	 */
	public String getModeNewMto() {
		return MODE_NEW_MTO;
	}
	/**
	 * @return the modeRevision
	 */
	public String getModeRevision() {
		return MODE_REVISION;
	}
	/**
	 * @return the modeVerification
	 */
	public String getModeVerification() {
		return MODE_VERIFICATION;
	}
	/**
	 * @return the modeApproval
	 */
	public String getModeApproval() {
		return MODE_APPROVAL;
	}
	/**
	 * @return the modeMaintain
	 */
	public String getModeMaintain() {
		return MODE_MAINTAIN;
	}
	/**
	 * @return the modeView
	 */
	public String getModeView() {
		return MODE_VIEW;
	}
	/**
	 * @return the modeList
	 */
	public String getModeList() {
		return MODE_LIST;
	}
	/**
	 * @return the currentNew
	 */
	public String getCurrentNew() {
		return CURRENT_NEW;
	}
	/**
	 * @return the currentMto
	 */
	public String getCurrentMto() {
		return CURRENT_MTO;
	}
	/**
	 * @return the typeProcurement
	 */
	public String getTypeProcurement() {
		return TYPE_PROCUREMENT;
	}
	/**
	 * @return the typeExpenditure
	 */
	public String getTypeExpenditure() {
		return TYPE_EXPENDITURE;
	}
	/**
	 * @return the favouriteTypeNone
	 */
	public String getFavouriteTypeNone() {
		return FAVOURITE_TYPE_NONE;
	}
	/**
	 * @return the favouriteTypePlan
	 */
	public String getFavouriteTypePlan() {
		return FAVOURITE_TYPE_PLAN;
	}
	/**
	 * @return the favouriteTypeItem
	 */
	public String getFavouriteTypeItem() {
		return FAVOURITE_TYPE_ITEM;
	}

	
	
	/**
	 * @return the procurementQuotation
	 */
	public String getProcurementQuotation() {
		return PROCUREMENT_QUOTATION;
	}
	/**
	 * @return the procurementDirectNego
	 */
	public String getProcurementDirectNego() {
		return PROCUREMENT_DIRECT_NEGO;
	}
	/**
	 * @return the procurementTender
	 */
	public String getProcurementTender() {
		return PROCUREMENT_TENDER;
	}
	/**
	 * @return the procurementDirectPurchase
	 */
	public String getProcurementDirectPurchase() {
		return PROCUREMENT_DIRECT_PURCHASE;
	}
	/**
	 * @return the procurementExistingContract
	 */
	public String getProcurementExistingContract() {
		return PROCUREMENT_EXISTING_CONTRACT;
	}
	/**
	 * @return the qtTypeBumiputera
	 */
	public String getQtTypeBumiputera() {
		return QT_TYPE_BUMIPUTERA;
	}
	/**
	 * @return the qtTypeOpen
	 */
	public String getQtTypeOpen() {
		return QT_TYPE_OPEN;
	}
	/**
	 * @return the qtTypePanel
	 */
	public String getQtTypePanel() {
		return QT_TYPE_PANEL;
	}
	/**
	 * @return the qtTypeRestricted
	 */
	public String getQtTypeRestricted() {
		return QT_TYPE_RESTRICTED;
	}
	/**
	 * @return the bpmRevise
	 */
	public String getBpmRevise() {
		return BPM_REVISE;
	}
	/**
	 * @return the bpmApprove
	 */
	public String getBpmApprove() {
		return BPM_APPROVE;
	}
	/**
	 * @return the bpmSubmit
	 */
	public String getBpmSubmit() {
		return BPM_SUBMIT;
	}
	/**
	 * @return the goodServiceTypeImported
	 */
	public String getGoodServiceTypeImported() {
		return GOOD_SERVICE_TYPE_IMPORTED;
	}
	/**
	 * @return the goodServiceTypeLocal
	 */
	public String getGoodServiceTypeLocal() {
		return GOOD_SERVICE_TYPE_LOCAL;
	}
	/**
	 * @return the goodServiceTypeBoth
	 */
	public String getGoodServiceTypeBoth() {
		return GOOD_SERVICE_TYPE_BOTH;
	}
	/**
	 * @return the fulfillmentOneOff
	 */
	public String getFulfillmentOneOff() {
		return FULFILLMENT_ONE_OFF;
	}
	/**
	 * @return the fulfillmentAsAndWhen
	 */
	public String getFulfillmentAsAndWhen() {
		return FULFILLMENT_AS_AND_WHEN;
	}
	/**
	 * @return the fulfillmentSchedule
	 */
	public String getFulfillmentSchedule() {
		return FULFILLMENT_SCHEDULE;
	}
	/**
	 * @return the yesInt
	 */
	public String getYesInt() {
		return YES_INT;
	}
	/**
	 * @return the noInt
	 */
	public String getNoInt() {
		return NO_INT;
	}
	
	/**
	 * @return the contractIndPkp
	 */
	public String getContractIndPkp() {
		return CONTRACT_IND_PKP;
	}
	/**
	 * @return the contractIndLoa
	 */
	public String getContractIndLoa() {
		return CONTRACT_IND_LOA;
	}
	/**
	 * @return the contractIndContract
	 */
	public String getContractIndContract() {
		return CONTRACT_IND_CONTRACT;
	}
}
