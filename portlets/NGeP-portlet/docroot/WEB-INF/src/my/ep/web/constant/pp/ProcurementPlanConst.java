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
	
	public static final String NAV_TO_TASK_LIST = "/xhtml/tasklist/taskListView";
	
//	public static final String NAV_TO_REVIEW_PLAN = "reviewPlan";
//	public static final String NAV_TO_VERIFY_PLAN = "verifyPlan";
//	public static final String NAV_TO_VIEW_PLAN = "viewPlan";
//	public static final String NAV_TO_CREATE_MTO_PLAN = "createPlan";
	
	public static final String PP_DET = "details/ppDetails.xhtml";
	public static final String EP_EXISTING_CONTRACT_DET = "details/epDetailsExistingContract.xhtml";
	public static final String EP_DIRECT_PURCHASE_DET = "details/epDetailsDirectPurchase.xhtml";
	

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
	public static final String CURRENT_ALLOC_BUDGET = "tabB";
	public static final String CURRENT_PLAN = "tabL";
	public static final String CURRENT_AUDIT = "tabA";
	
	
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
	
	//Contract Ind
	public static final String CONTRACT_IND_PKP = "P";
	public static final String CONTRACT_IND_LOA = "S";
	public static final String CONTRACT_IND_CONTRACT = "C";
	
	//Contract Type
	public static final String CONTRACT_TYPE_MINISTRY = "P";
	public static final String CONTRACT_IND_CENTRAL = "S";
	
	
	//Product Level
	public static final Integer UNSPSC_SEGMENT = 0;
	public static final Integer UNSPSC_FAMILY = 1;
	public static final Integer UNSPSC_CLASS = 2;
	public static final Integer UNSPSC_COMMODITY = 3;
	
	//PM Status Budget Allocation
	public static final Long STATUS_PBA_NEW = 10001L;
	public static final Long STATUS_PBA_PENDING_APPROVAL = 10002L;
	public static final Long STATUS_PBA_SAVED = 10003L;
	
	//PM Status Budget Allocation Dtl
	public static final Long STATUS_PBD_NEW = 10005L;
	
	//PM Status Plan
	public static final Long STATUS_PPL_NEW = 10005L;
	
	//PM Status Plan Item
	public static final Long STATUS_PPI_NEW = 10005L;
	
	
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
	
	//VOT type
	public static final String VOT_PS = "PS";
	public static final String VOT_BT = "BT";
	public static final String VOT_AMANAH = "AV";
	public static final String VOT_WITHOUT = "AW";
	
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
	 * @return the statusPbaNew
	 */
	public Long getStatusPbaNew() {
		return STATUS_PBA_NEW;
	}
	/**
	 * @return the statusPbaPendingApproval
	 */
	public Long getStatusPbaPendingApproval() {
		return STATUS_PBA_PENDING_APPROVAL;
	}
	/**
	 * @return the statusPbdNew
	 */
	public Long getStatusPbdNew() {
		return STATUS_PBD_NEW;
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
	 * @return the statusPendingApproval
	 */
	public Long getStatusPendingApproval() {
		return STATUS_PENDING_APPROVAL;
	}
	/**
	 * @return the statusPendingRevision
	 */
	public Long getStatusPendingRevision() {
		return STATUS_PENDING_REVISION;
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
	 * @return the currentAudit
	 */
	public String getCurrentAudit() {
		return CURRENT_AUDIT;
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
	 * @return the typeMinistry
	 */
	public Long getTypeMinistry() {
		return TYPE_MINISTRY;
	}
	/**
	 * @return the typeJabatan
	 */
	public Long getTypeJabatan() {
		return TYPE_JABATAN;
	}
	/**
	 * @return the typePtj
	 */
	public Long getTypePtj() {
		return TYPE_PTJ;
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
	 * @return the statusPbaSaved
	 */
	public Long getStatusPbaSaved() {
		return STATUS_PBA_SAVED;
	}
	/**
	 * @return the statusPplNew
	 */
	public Long getStatusPplNew() {
		return STATUS_PPL_NEW;
	}
	/**
	 * @return the statusPpiNew
	 */
	public Long getStatusPpiNew() {
		return STATUS_PPI_NEW;
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
}
