/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.constant.sm;

public class SupplierManagementConst {

	// Navigation
	public static final String PROCEED_MOF_REG = "mofRegSmUser_BaseWizardPage";
	// public static final String PROCEED_MOF_REG = "mofRegBasePage";
	public static final String MOF_REG_REDIRECT = "mofRegSmUser_BaseWizardPage?faces-redirect=true";
	public static final String PROCEED_BUMI_APPLN = "applyBumiStatusBasePage";
	public static final String PROCEED_BUMI_APPLN_SIDEMENU = "smBumiSideMenu_BasePage";
	// public static final String PROCEED_BUMI_APPLN =
	// "applyBumiSmUser_BaseWizardPage";
	public static final String PROCEED_UPDATE_MOF_APPLN = "updateMofDetailsBasePage";
	// public static final String PROCEED_UPDATE_MOF_APPLN =
	// "updateMofSmUser_BaseWizardPage";
	public static final String PROCEED_ADDTNLCAT_MOF_APPLN = "addAdditionalCategoryBasePage";
	// public static final String PROCEED_ADDTNLCAT_MOF_APPLN =
	// "addAdditionalCategorySmUser_BaseWizardPage";
	public static final String PROCEED_RENEW_MOF_APPLN = "renewMofBasePage";
	// public static final String PROCEED_RENEW_MOF_APPLN =
	// "renewMofSmUser_BaseWizardPage";
	public static final String PROCEED_CANCEL_MOF_APPLN = "cancelRegistrationWithMof";
	public static final String PROCEED_DEL_PENDING_MOF_APPLN = "deletePendingMofApplication";
	public static final String PROCEED_DELETE_CATCODES_APPLN = "delRegCatCodes";
	public static final String DEFAULT_DELETE_CATCODES_APPLN = "defaultDeleteCategoryCodePage";
	public static final String DEFAULT_MOF_REG = "defaultNewMofApplPage";
	public static final String DEFAULT_BUMI_APPLN = "defaultBumiApplicationPage";
	public static final String DEFAULT_UPDATE_MOF_APPLN = "defaultUpdateMofDetailsPage";
	public static final String DEFAULT_ADDTNLCAT_MOF_APPLN = "defaultAdditionalCategoryCodePage";
	public static final String DEFAULT_RENEW_MOF_APPLN = "defaultRenewMofApplPage";
	public static final String DEFAULT_CANCEL_MOF_APPLN = "defaultCancelMofApplicationPage";
	public static final String DEFAULT_DEL_PENDING_MOF_APPLN = "defaultDeletePedingMofApplnPage";
	public static final String BUMI_APPL_SUCCESS = "bumiApplicationAckPage";
	public static final String BUMI_APPL_FAIL = "step5BumiAppln_SupportingDocumentsInfo";
	public static final String MOF_APPL_SUCCESS = "mofRegApplnAckPage";
	public static final String MOF_APPL_FAIL = "step8ProcessingFeePaymentInfo";
	public static final String UPDATE_MOF_APPL_SUCCESS = "updateMofDetailsApplAckPage";
	public static final String UPDATE_MOF_APPL_FAIL = "step6UpdateMof_SupportingDocumentsInfo";
	public static final String ADDNLCAT_APPL_SUCCESS = "addAdditionalCategoryAckPage";
	public static final String ADDNLCAT_APPL_FAIL = "step6AddnlCatCode_SupportingDocumentsInfo";
	public static final String RENEW_MOF_APPL_SUCCESS = "renewMofApplnAckPage";
	public static final String RENEW_MOF_APPL_FAIL = "step8RenewAppln_ProcessingFeePaymentInfo";
	public static final String CANCEL_MOF_APPL_SUCCESS = "cancelMofApplicationAckPage";
	public static final String DELETE_PENDING_MOF_APPL_SUCCESS = "deletePendingMofApplnAckPage";
	public static final String APPROVER_DECISION_ACK = "/xhtml/sm/approverDecisionPages/approverDecisionAckPage.xhtml";
	public static final String PO_DECISION_ACK = "/xhtml/sm/poDecisionPages/poDecisionAckPage.xhtml";
	public static final String DEFAULT_ACK_PAGE = "/xhtml/shared/acknowledge.xhtml";
	public static final int NEW_APPL_REVIEW_MAXCOUNT = 14;
	public static final int BUMI_APPL_REVIEW_MAXCOUNT = 8;
	public static final int UPDATE_MOF_APPL_REVIEW_MAXCOUNT = 13;
	public static final int RENEW_APPL_REVIEW_MAXCOUNT = 14;
	public static final int ADDLN_CATCODES_APPL_REVIEW_MAXCOUNT = 13;

	// Panel Names
	public static final String SM_BASIC_INFO_PANEL = "BasicInfoPanel";
	public static final String SM_OFFICE_FACILITY_INFO_PANEL = "OfficeFacilityInfoPanel";
	public static final String SM_BRANCH_PANEL = "BranchCompanyPanel";
	public static final String SM_ASSOC_COMPANY_PANEL = "AssocCompanyPanel";
	public static final String SM_EQUI_COMPANY_PANEL = "EquiCompanyPanel";

	// Basic Registration && Account Maintenance
	public static final String INDIVIDUAL_FLAG = "13";
	public static final String STYPE_CONSULTANT = "J";
	public static final String STYPE_CONTRACTOR = "K";
	public static final String STYPE_BASIC = "B";
	public static final String PERM_ADD = "P";
	public static final String BUS_ADD = "C";
	public static final String RES_ADD = "R";
	public static final int DEFAULT_REV_NO = 1;
	public static final String PROF_SERVICE = "P";
	public static final int FIRST_USER_AUTHO = 1;
	public static final int FIRST_USER_CONT_SIGNER = 1;
	public static final boolean FIRST_USER_CONT_PERSON = true;
	public static final int REG_SOFTCERT = 0;
	public static final String BASIC_IND_IDENT_TYPE = "I";

	// Site Visit Assignment
	public static final String SVO_HQ = "UPS_SVO_HQ";
	public static final String SVO_SA = "UPS_SVO_SA";
	public static final String SVO_SK = "UPS_SVO_SK";
	public static final String SM_RMK_DOC_TYPE_SVO = "SVO";
	public static final String SM_RMK_DOC_TYPE_SVC = "SVC";
	
}
