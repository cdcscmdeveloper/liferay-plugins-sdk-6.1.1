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
	public static final String PROCEED_UPDATE_APPLN_SIDEMENU = "smUpdateMofSideMenu_BasePage";
	// public static final String PROCEED_UPDATE_MOF_APPLN =
	// "updateMofSmUser_BaseWizardPage";
	public static final String PROCEED_ADDTNLCAT_MOF_APPLN = "addAdditionalCategoryBasePage";
	public static final String PROCEED_ADDTNLCAT_APPLN_SIDEMENU = "smAddtnlCatApplnSideMenu_BasePage";
	// public static final String PROCEED_ADDTNLCAT_MOF_APPLN =
	// "addAdditionalCategorySmUser_BaseWizardPage";
	public static final String PROCEED_RENEW_MOF_APPLN = "renewMofBasePage";
	public static final String PROCEED_RENEW_APPLN_SIDEMENU = "smRenewRegSideMenu_BasePage";
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
	public static final int NEW_APPL_NORMCAT_REVIEW_MAXCOUNT = 14;
	public static final int NEW_APPL_SPLCAT_REVIEW_MAXCOUNT = 18;
	public static final int BUMI_APPL_NORMCAT_REVIEW_MAXCOUNT = 8;
	public static final int BUMI_APPL_SPLCAT_REVIEW_MAXCOUNT = 12;
	public static final int UPDATE_MOF_APPL_REVIEW_MAXCOUNT = 13;
	public static final int RENEW_APPL_NORMCAT_REVIEW_MAXCOUNT = 14;
	public static final int RENEW_APPL_SPLCAT_REVIEW_MAXCOUNT = 18;
	public static final int ADDLN_CATCODES_APPL_NORMCAT_REVIEW_MAXCOUNT = 13;
	public static final int ADDLN_CATCODES_APPL_SPLCAT_REVIEW_MAXCOUNT = 17;

	// Panel Names
	public static final String SM_BASIC_INFO_PANEL = "BasicInfoPanel";
	public static final String SM_OFFICE_FACILITY_INFO_PANEL = "OfficeFacilityInfoPanel";
	public static final String SM_BRANCH_PANEL = "BranchCompanyPanel";
	public static final String SM_ASSOC_COMPANY_PANEL = "AssocCompanyPanel";
	public static final String SM_EQUI_COMPANY_PANEL = "EquiCompanyPanel";
	public static final String SM_FACTORY_FACILITY_PANEL = "FactoryPanel";
	public static final String SM_DOCKYARD_FACILITY_PANEL = "DockyardPanel";
	public static final String SM_WORKSHOP_FACILITY_PANEL = "WorkshopPanel";
	public static final String SM_STORE_FACILITY_PANEL = "StorePanel";
	public static final String SM_SHRHLDR_PERM_ADDR_PANEL = "ShareHldrPermAddr";
	public static final String SM_SHRHLDR_OFF_ADDR_PANEL = "ShareHldrOffAddr";
	public static final String SM_SHRHLDR_MAIL_ADDR_PANEL = "ShareHldrMailAddr";
	public static final String SM_SHRHLDR_RESD_ADDR_PANEL = "ShareHldrResdAddr";
	public static final String SM_PERSNL_PERM_ADDR_PANEL = "PersonnelPermAddr";
	public static final String SM_PERSNL_OFF_ADDR_PANEL = "PersonnelOffAddr";
	public static final String SM_PERSNL_MAIL_ADDR_PANEL = "PersonnelMailAddr";
	public static final String SM_PERSNL_RESD_ADDR_PANEL = "PersonnelResdAddr";
	public static final String SM_BANK_INFO_PANEL = "BankInfoPanel";

	public static final String SM_PERSONNEL_ADDR_TYPE_OFFICE = "O";
	public static final String SM_PERSONNEL_ADDR_TYPE_RES = "R";
	public static final String SM_PERSONNEL_ADDR_TYPE_PERM = "I";
	public static final String SM_PERSONNEL_ADDR_TYPE_MAIL = "M";

	// FacilityTypes
	public static final String SM_FACILITY_TYPE_FACTORY = "F";
	public static final String SM_FACILITY_TYPE_DOCKYARD = "D";
	public static final String SM_FACILITY_TYPE_WORKSHOP = "W";
	public static final String SM_FACILITY_TYPE_STORE = "S";
	public static final String SM_FACILITY_TYPE_HOMEOFFICE = "H";
	public static final String SM_FACILITY_TYPE_BUSSINESSOFFICE = "B";

	public static final String PERSONNEL_TYPE_DIRECTOR = "DIR";
	public static final String PERSONNEL_TYPE_STAFF = "STF";

	// Basic Registration && Account Maintenance
	public static final String INDIVIDUAL_FLAG = "13";
	public static final String STYPE_CONSULTANT = "J";
	public static final String STYPE_CONTRACTOR = "K";
	public static final String STYPE_BASIC = "B";
	public static final String ACC_BASIC = "B";
	public static final String ACC_MOF = "M";
	public static final String PERM_ADD = "P";
	public static final String BUS_ADD = "C";
	public static final String RES_ADD = "R";
	public static final int DEFAULT_REV_NO = 1;
	public static final String PROF_SERVICE = "P";
	public static final String SPECIAL_SERVICE = "S";
	public static final int FIRST_USER_AUTHO = 1;
	public static final int FIRST_USER_CONT_SIGNER = 1;
	public static final boolean FIRST_USER_CONT_PERSON = true;
	public static final int REG_SOFTCERT = 0;
	public static final int REG_BUMI = 0;
	public static final String BASIC_IND_IDENT_TYPE = "I";

	public static final String SECTION_REV_ROLE_PO = "P";
	public static final String SECTION_REV_ROLE_APP = "A";
	public static final String ACTIVATION_PATH = "/web/guest/supplier-user-activation?";
	public static final String ORG_TYPE_SUPPLIER = "SUP";

	// Site Visit Assignment
	public static final String SVO_HQ = "UPS_SVO_HQ";
	public static final String SVO_SA = "UPS_SVO_SA";
	public static final String SVO_SK = "UPS_SVO_SK";
	public static final String SVO = "UPS_SVO";
	public static final String SM_RMK_DOC_TYPE_SVO = "SVO";
	public static final String SM_RMK_DOC_TYPE_SVC = "SVC";

	// Site Visit
	public static final String SVO_FIN_CHECKLIST = "FCL";

	// Getters for panel info
	public String getSM_BASIC_INFO_PANEL() {
		return SM_BASIC_INFO_PANEL;
	}

	public String getSM_OFFICE_FACILITY_INFO_PANEL() {
		return SM_OFFICE_FACILITY_INFO_PANEL;
	}

	public String getSM_BRANCH_PANEL() {
		return SM_BRANCH_PANEL;
	}

	public String getSM_ASSOC_COMPANY_PANEL() {
		return SM_ASSOC_COMPANY_PANEL;
	}

	public String getSM_EQUI_COMPANY_PANEL() {
		return SM_EQUI_COMPANY_PANEL;
	}

	// Getter for facility type info
	public String getSM_FACILITY_TYPE_FACTORY() {
		return SM_FACILITY_TYPE_FACTORY;
	}

	public String getSM_FACILITY_TYPE_DOCKYARD() {
		return SM_FACILITY_TYPE_DOCKYARD;
	}

	public String getSM_FACILITY_TYPE_WORKSHOP() {
		return SM_FACILITY_TYPE_WORKSHOP;
	}

	public String getSM_FACILITY_TYPE_STORE() {
		return SM_FACILITY_TYPE_STORE;
	}

	public String getSM_FACILITY_TYPE_HOMEOFFICE() {
		return SM_FACILITY_TYPE_HOMEOFFICE;
	}

	public String getSM_FACILITY_TYPE_BUSSINESSOFFICE() {
		return SM_FACILITY_TYPE_BUSSINESSOFFICE;
	}

	public String getSM_FACTORY_FACILITY_PANEL() {
		return SM_FACTORY_FACILITY_PANEL;
	}

	public String getSM_DOCKYARD_FACILITY_PANEL() {
		return SM_DOCKYARD_FACILITY_PANEL;
	}

	public String getSM_WORKSHOP_FACILITY_PANEL() {
		return SM_WORKSHOP_FACILITY_PANEL;
	}

	public String getSM_STORE_FACILITY_PANEL() {
		return SM_STORE_FACILITY_PANEL;
	}

	public String getSM_SHRHLDR_PERM_ADDR_PANEL() {
		return SM_SHRHLDR_PERM_ADDR_PANEL;
	}

	public String getSM_SHRHLDR_OFF_ADDR_PANEL() {
		return SM_SHRHLDR_OFF_ADDR_PANEL;
	}

	public String getSM_SHRHLDR_MAIL_ADDR_PANEL() {
		return SM_SHRHLDR_MAIL_ADDR_PANEL;
	}

	public String getSM_SHRHLDR_RESD_ADDR_PANEL() {
		return SM_SHRHLDR_RESD_ADDR_PANEL;
	}

	public String getSM_BANK_INFO_PANEL() {
		return SM_BANK_INFO_PANEL;
	}

	public String getSM_PERSNL_PERM_ADDR_PANEL() {
		return SM_PERSNL_PERM_ADDR_PANEL;
	}

	public String getSM_PERSNL_OFF_ADDR_PANEL() {
		return SM_PERSNL_OFF_ADDR_PANEL;
	}

	public String getSM_PERSNL_MAIL_ADDR_PANEL() {
		return SM_PERSNL_MAIL_ADDR_PANEL;
	}

	public String getSM_PERSNL_RESD_ADDR_PANEL() {
		return SM_PERSNL_RESD_ADDR_PANEL;
	}

	public String getPERSONNEL_TYPE_DIRECTOR() {
		return PERSONNEL_TYPE_DIRECTOR;
	}

	public String getPERSONNEL_TYPE_STAFF() {
		return PERSONNEL_TYPE_STAFF;
	}

	public static String getProfService() {
		return PROF_SERVICE;
	}

	public static String getSpecialService() {
		return SPECIAL_SERVICE;
	}

}
