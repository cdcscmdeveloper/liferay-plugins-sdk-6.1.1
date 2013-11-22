/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.constant.pp;

public class ProcurementPlanConst {

	//Navigation
	public static final String NAV_TO_LIST_BUDGET_ALLOCATION = "listBudget";
	public static final String NAV_TO_BUDGET_ALLOCATION = "budgetAllocation";
	
	public static final String NAV_TO_LIST_PLAN = "listPlan";
	public static final String NAV_TO_PLAN_DET = "procurementPlan";
	
	public static final String NAV_TO_TASK_LIST = "/xhtml/shared/acknowledge";
	
	
	public static final String PP_DET = "details/ppDetails.xhtml";
	public static final String EP_EXISTING_CONTRACT_DET = "details/epDetailsExistingContract.xhtml";
	public static final String EP_DIRECT_PURCHASE_DET = "details/epDetailsDirectPurchase.xhtml";
	public static final String EP_FAVOURITE_PLAN_DET = "details/epDetailsFavourPlan.xhtml";
	public static final String EP_FAVOURITE_ITEM_DET = "details/epDetailsFavourItem.xhtml";
	

	//Submission Type
	public static final String SUBMIT_APPROVAL = "A";
	public static final String SUBMIT_VERIFICATION = "V";
	
	//View Mode
	public static final String MODE_DRAFT= "D";
	public static final String MODE_NEW = "N";
	public static final String MODE_NEW_MTO = "T";
	public static final String MODE_REVISION = "S";
	public static final String MODE_VERIFICATION = "V";
	public static final String MODE_APPROVAL = "A";
	public static final String MODE_MAINTAIN = "M";
	public static final String MODE_VIEW = "R";
	public static final String MODE_LIST = "L";
	public static final String MODE_APPROVAL_LIST = "P";
	
	//Current Mode
	public static final String CURRENT_NEW = "tabN";
	public static final String CURRENT_MTO = "tabM";
	public static final String CURRENT_ALLOC_BUDGET = "tabB";
	public static final String CURRENT_PLAN = "tabL";

	
	
	//Plan Type
	public static final String PLAN_TYPE_PROCUREMENT = "P";
	public static final String PLAN_TYPE_EXPENDITURE = "E";
	
	//Favourite Type
	public static final String FAVOURITE_TYPE_NONE = "N";
	public static final String FAVOURITE_TYPE_PLAN = "P";
	public static final String FAVOURITE_TYPE_ITEM = "I";
	
	//Procurement Mode
	public static final String PROCUREMENT_QUOTATION = "Q";
	public static final String PROCUREMENT_DIRECT_NEGO = "N";
	public static final String PROCUREMENT_TENDER = "T";
	public static final String PROCUREMENT_DIRECT_PURCHASE = "D";
	public static final String PROCUREMENT_EXISTING_CONTRACT = "E";
	
	//Fulfillment Type
	public static final String FULFILLMENT_ONE_OFF = "O";
	public static final String FULFILLMENT_AS_AND_WHEN = "P";
	public static final String FULFILLMENT_SCHEDULE = "S";
	
	//QT Type
	public static final String QT_TYPE_BUMIPUTERA = "B";
	public static final String QT_TYPE_OPEN = "O";
	public static final String QT_TYPE_PANEL = "P";
	public static final String QT_TYPE_RESTRICTED = "R";
	public static final String QT_TYPE_INTERNATIONAL = "I";

	//Item Origin Type
	public static final String ITEM_ORIGIN_TYPE_IMPORTED = "I";
	public static final String ITEM_ORIGIN_TYPE_LOCAL = "L";
	public static final String ITEM_ORIGIN_TYPE_BOTH = "B";
	
	//Verification Level
	public static final boolean LEVEL_SINGLE = false;
	public static final boolean LEVEL_MULTIPLE = true;
	
	
	//BPM
	public static final String BPM_REVISE = "REVISE";
	public static final String BPM_APPROVE = "APPROVE";
	public static final String BPM_SUBMIT = "SUBMIT";
	public static final String BPM_SAVE = "SAVE";

	//Default BPM Actions
	public static final String[] BPM_DEFAULT_ACTIONS = new String[]{BPM_SAVE, BPM_SUBMIT};
	
	//General
	public static final String YES_INT = "1";
	public static final String NO_INT = "0";
	public static final String SO_PP = "pp";
	public static final Integer PVO_LEVEL = 3;
	public static final String DATE_DISPLAY_PATTERN = "MM/dd/yyyy";
	
	//Contract Ind
	public static final String CONTRACT_IND_PKP = "P";
	public static final String CONTRACT_IND_LOA = "S";
	public static final String CONTRACT_IND_CONTRACT = "C";
	
	
	//Product Level
	public static final Integer UNSPSC_SEGMENT = 0;
	public static final Integer UNSPSC_FAMILY = 1;
	public static final Integer UNSPSC_CLASS = 2;
	public static final Integer UNSPSC_COMMODITY = 3;

	//Module
	public static final String MODULE = "PP";
	
	//Doc Type
	public static final String DOCTYPE_PLAN = "PPL";
	public static final String DOCTYPE_PLAN_ITEM = "PPI";
	public static final String DOCTYPE_BUDGET_ALLOC = "PBA";
	public static final String DOCTYPE_BUDGET_ALLOC_ITEM = "PBD";

	
	//PP Programs
	public static final String PROG_PLAN = "plan";
	public static final String PROG_BUDGET = "budget";
	
	//VOT type
	public static final String VOT_PS = "PS";
	public static final String VOT_BT = "BT";
	public static final String VOT_AMANAH = "AV";
	public static final String VOT_WITHOUT = "AW";
	
//	
//	//Reason Code
//	public static final String REASON_REJECT = "PPR";
//	public static final String REASON_CANCEL = "PPC";
	
	
	/** refer to PM_CODE **/
	//PM Status Budget Allocation
	public static final Long STATUS_PBA_PENDING_SUBMIT = 30020L;

	//PM Status Budget Allocation Dtl
	public static final Long STATUS_PBD_PENDING_SUBMIT = 30025L; 
	
	//PM Status Plan
	public static final Long STATUS_PPL_PENDING_SUBMIT = 30001L;
	
	//PM Status Plan Item
	public static final Long STATUS_PPI_PENDING_SUBMIT = 30009L;

	
	//Contract Type
	public static final String CONTRACT_TYPE_MINISTRY = "168"; //refer to PM_CODE
	public static final String CONTRACT_IND_CENTRAL = "169"; //refer to PM_CODE
	
	/**
	 * @return the votPs
	 */
	public String getVotPs() {
		return VOT_PS;
	}
	/**
	 * @return the votBt
	 */
	public String getVotBt() {
		return VOT_BT;
	}
	/**
	 * @return the votAmanah
	 */
	public String getVotAmanah() {
		return VOT_AMANAH;
	}
	/**
	 * @return the votWithout
	 */
	public String getVotWithout() {
		return VOT_WITHOUT;
	}
	/**
	 * @return the contractTypeMinistry
	 */
	public String getContractTypeMinistry() {
		return CONTRACT_TYPE_MINISTRY;
	}
	/**
	 * @return the contractIndCentral
	 */
	public String getContractIndCentral() {
		return CONTRACT_IND_CENTRAL;
	}
	
	
	
	/**
	 * @return the bpmSave
	 */
	public String getBpmSave() {
		return BPM_SAVE;
	}
	/**
	 * @return the soPp
	 */
	public String getSoPp() {
		return SO_PP;
	}
	
	
	/**
	 * @return the progPlan
	 */
	public String getProgPlan() {
		return PROG_PLAN;
	}
	/**
	 * @return the progBudget
	 */
	public String getProgBudget() {
		return PROG_BUDGET;
	}
	
	/**
	 * @return the doctypePlan
	 */
	public String getDoctypePlan() {
		return DOCTYPE_PLAN;
	}
	/**
	 * @return the doctypePlanItem
	 */
	public String getDoctypePlanItem() {
		return DOCTYPE_PLAN_ITEM;
	}
	/**
	 * @return the doctypeBudgetAlloc
	 */
	public String getDoctypeBudgetAlloc() {
		return DOCTYPE_BUDGET_ALLOC;
	}
	/**
	 * @return the doctypeBudgetAllocItem
	 */
	public String getDoctypeBudgetAllocItem() {
		return DOCTYPE_BUDGET_ALLOC_ITEM;
	}
	
	/**
	 * @return the unspscSegment
	 */
	public Integer getUnspscSegment() {
		return UNSPSC_SEGMENT;
	}
	/**
	 * @return the unspscFamily
	 */
	public Integer getUnspscFamily() {
		return UNSPSC_FAMILY;
	}
	/**
	 * @return the unspscClass
	 */
	public Integer getUnspscClass() {
		return UNSPSC_CLASS;
	}
	/**
	 * @return the unspscCommodity
	 */
	public Integer getUnspscCommodity() {
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
//	/**
//	 * @return the navToReviewPlan
//	 */
//	public String getNavToReviewPlan() {
//		return NAV_TO_REVIEW_PLAN;
//	}
//	/**
//	 * @return the navToVerifyPlan
//	 */
//	public String getNavToVerifyPlan() {
//		return NAV_TO_VERIFY_PLAN;
//	}
//	/**
//	 * @return the navToViewPlan
//	 */
//	public String getNavToViewPlan() {
//		return NAV_TO_VIEW_PLAN;
//	}
//	/**
//	 * @return the navToCreateMtoPlan
//	 */
//	public String getNavToCreateMtoPlan() {
//		return NAV_TO_CREATE_MTO_PLAN;
//	}
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
	 * @return the currentAllocBudget
	 */
	public String getCurrentAllocBudget() {
		return CURRENT_ALLOC_BUDGET;
	}
	/**
	 * @return the currentPlan
	 */
	public String getCurrentPlan() {
		return CURRENT_PLAN;
	}
	/**
	 * @return the planTypeProcurement
	 */
	public String getPlanTypeProcurement() {
		return PLAN_TYPE_PROCUREMENT;
	}
	/**
	 * @return the planTypeExpenditure
	 */
	public String getPlanTypeExpenditure() {
		return PLAN_TYPE_EXPENDITURE;
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
	 * @return the itemOriginTypeImported
	 */
	public String getItemOriginTypeImported() {
		return ITEM_ORIGIN_TYPE_IMPORTED;
	}
	/**
	 * @return the itemOriginTypeLocal
	 */
	public String getItemOriginTypeLocal() {
		return ITEM_ORIGIN_TYPE_LOCAL;
	}
	/**
	 * @return the itemOriginTypeBoth
	 */
	public String getItemOriginTypeBoth() {
		return ITEM_ORIGIN_TYPE_BOTH;
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
	
	/**
	 * @return the levelSingle
	 */
	public boolean isLevelSingle() {
		return LEVEL_SINGLE;
	}
	/**
	 * @return the levelMultiple
	 */
	public boolean isLevelMultiple() {
		return LEVEL_MULTIPLE;
	}
	/**
	 * @return the qtTypeInternational
	 */
	public String getQtTypeInternational() {
		return QT_TYPE_INTERNATIONAL;
	}
	/**
	 * @return the bpmDefaultActions
	 */
	public String[] getBpmDefaultActions() {
		return BPM_DEFAULT_ACTIONS;
	}
	
	/**
	 * @return the ppDet
	 */
	public String getPpDet() {
		return PP_DET;
	}
	/**
	 * @return the epExistingContractDet
	 */
	public String getEpExistingContractDet() {
		return EP_EXISTING_CONTRACT_DET;
	}
	/**
	 * @return the epDirectPurchaseDet
	 */
	public String getEpDirectPurchaseDet() {
		return EP_DIRECT_PURCHASE_DET;
	}
	/**
	 * @return the navToTaskList
	 */
	public String getNavToTaskList() {
		return NAV_TO_TASK_LIST;
	}
	/**
	 * @return the epFavouritePlanDet
	 */
	public String getEpFavouritePlanDet() {
		return EP_FAVOURITE_PLAN_DET;
	}
	/**
	 * @return the epFavouriteItemDet
	 */
	public String getEpFavouriteItemDet() {
		return EP_FAVOURITE_ITEM_DET;
	}
	/**
	 * @return the submitApproval
	 */
	public String getSubmitApproval() {
		return SUBMIT_APPROVAL;
	}
	/**
	 * @return the submitVerification
	 */
	public String getSubmitVerification() {
		return SUBMIT_VERIFICATION;
	}
	/**
	 * @return the pvoLevel
	 */
	public Integer getPvoLevel() {
		return PVO_LEVEL;
	}
	/**
	 * @return the dateDisplayPattern
	 */
	public String getDateDisplayPattern() {
		return DATE_DISPLAY_PATTERN;
	}
//	/**
//	 * @return the reasonReject
//	 */
//	public String getReasonReject() {
//		return REASON_REJECT;
//	}
//	/**
//	 * @return the reasonCancel
//	 */
//	public String getReasonCancel() {
//		return REASON_CANCEL;
//	}
	/**
	 * @return the modeDraft
	 */
	public String getModeDraft() {
		return MODE_DRAFT;
	}
	/**
	 * @return the modeApprovalList
	 */
	public String getModeApprovalList() {
		return MODE_APPROVAL_LIST;
	}
	/**
	 * @return the module
	 */
	public String getModule() {
		return MODULE;
	}
	/**
	 * @return the statusPbaPendingSubmit
	 */
	public Long getStatusPbaPendingSubmit() {
		return STATUS_PBA_PENDING_SUBMIT;
	}
	/**
	 * @return the statusPbdPendingSubmit
	 */
	public Long getStatusPbdPendingSubmit() {
		return STATUS_PBD_PENDING_SUBMIT;
	}
	/**
	 * @return the statusPplPendingSubmit
	 */
	public Long getStatusPplPendingSubmit() {
		return STATUS_PPL_PENDING_SUBMIT;
	}
	/**
	 * @return the statusPpiPendingSubmit
	 */
	public Long getStatusPpiPendingSubmit() {
		return STATUS_PPI_PENDING_SUBMIT;
	}
	
	
	
}
