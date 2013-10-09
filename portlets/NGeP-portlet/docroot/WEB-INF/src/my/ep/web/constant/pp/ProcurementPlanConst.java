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
	
	
	//Goods or Services Type
	public static final String GOOD_SERVICE_TYPE_IMPORTED = "I";
	public static final String GOOD_SERVICE_TYPE_LOCAL = "L";
	public static final String GOOD_SERVICE_TYPE_BOTH = "B";
	
	//BPM
	public static final String BPM_REVISE = "REVISE";
	public static final String BPM_APPROVE = "APPROVE";
	public static final String BPM_SUBMIT = "SUBMIT";

	//General
	public static final String YES_INT = "1";
	public static final String NO_INT = "0";
	
	//Contract Ind
	public static final String CONTRACT_IND_PKP = "P";
	public static final String CONTRACT_IND_LOA = "S";
	public static final String CONTRACT_IND_CONTRACT = "C";
	
	
	
	
	
	
	
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
	public static String getProcurementQuotation() {
		return PROCUREMENT_QUOTATION;
	}
	/**
	 * @return the procurementDirectNego
	 */
	public static String getProcurementDirectNego() {
		return PROCUREMENT_DIRECT_NEGO;
	}
	/**
	 * @return the procurementTender
	 */
	public static String getProcurementTender() {
		return PROCUREMENT_TENDER;
	}
	/**
	 * @return the procurementDirectPurchase
	 */
	public static String getProcurementDirectPurchase() {
		return PROCUREMENT_DIRECT_PURCHASE;
	}
	/**
	 * @return the procurementExistingContract
	 */
	public static String getProcurementExistingContract() {
		return PROCUREMENT_EXISTING_CONTRACT;
	}
	/**
	 * @return the bpmRevise
	 */
	public static String getBpmRevise() {
		return BPM_REVISE;
	}
	/**
	 * @return the bpmApprove
	 */
	public static String getBpmApprove() {
		return BPM_APPROVE;
	}
	/**
	 * @return the bpmSubmit
	 */
	public static String getBpmSubmit() {
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
	public static String getFulfillmentOneOff() {
		return FULFILLMENT_ONE_OFF;
	}
	/**
	 * @return the fulfillmentAsAndWhen
	 */
	public static String getFulfillmentAsAndWhen() {
		return FULFILLMENT_AS_AND_WHEN;
	}
	/**
	 * @return the fulfillmentSchedule
	 */
	public static String getFulfillmentSchedule() {
		return FULFILLMENT_SCHEDULE;
	}
	/**
	 * @return the yesInt
	 */
	public static String getYesInt() {
		return YES_INT;
	}
	/**
	 * @return the noInt
	 */
	public static String getNoInt() {
		return NO_INT;
	}
	
	/**
	 * @return the contractIndPkp
	 */
	public static String getContractIndPkp() {
		return CONTRACT_IND_PKP;
	}
	/**
	 * @return the contractIndLoa
	 */
	public static String getContractIndLoa() {
		return CONTRACT_IND_LOA;
	}
	/**
	 * @return the contractIndContract
	 */
	public static String getContractIndContract() {
		return CONTRACT_IND_CONTRACT;
	}
}
