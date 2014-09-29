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
	public static final int NEW_APPL_NORMCAT_REVIEW_MAXCOUNT = 13;
	public static final int NEW_APPL_SPLCAT_REVIEW_MAXCOUNT = 17;
	public static final int BUMI_APPL_NORMCAT_REVIEW_MAXCOUNT = 7;
	public static final int BUMI_APPL_SPLCAT_REVIEW_MAXCOUNT = 11;
	public static final int UPDATE_MOF_APPL_REVIEW_MAXCOUNT = 13;
	public static final int RENEW_APPL_NORMCAT_REVIEW_MAXCOUNT = 13;
	public static final int RENEW_APPL_SPLCAT_REVIEW_MAXCOUNT = 17;
	public static final int ADDLN_CATCODES_APPL_NORMCAT_REVIEW_MAXCOUNT = 12;
	public static final int ADDLN_CATCODES_APPL_SPLCAT_REVIEW_MAXCOUNT = 16;
	public static final int RECORD_DELETED = 1;
	public static final String COUNTRY_MALAYSIA_CODE = "MY";
	public static final String ICT_CATEGORY_L1_CODE = "21";

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
	public static final String PERSONNEL_TYPE_EQUITY = "EQUI";

	// Basic Registration && Account Maintenance
	public static final String INDIVIDUAL_FLAG = "A";
	public static final String STYPE_CONSULTANT = "J";
	public static final String STYPE_CONTRACTOR = "K";
	public static final String STYPE_BASIC = "B";
	public static final String ACC_BASIC = "O";
	public static final String ACC_MOF = "N";
	public static final String PERM_ADD = "I";
	public static final String BUS_ADD = "C";
	public static final String RES_ADD = "R";
	public static final String MAIL_ADD = "M";
	public static final int DEFAULT_REV_NO = 1;
	public static final String PROF_SERVICE = "P";
	public static final String SPECIAL_SERVICE = "S";
	public static final int FIRST_USER_AUTHO = 1;
	public static final int FIRST_USER_CONT_SIGNER = 1;
	public static final boolean FIRST_USER_CONT_PERSON = true;
	public static final int REG_SOFTCERT = 0;
	public static final int REG_BUMI = 0;
	public static final String BASIC_IND_IDENT_TYPE = "I";
	public static final Long BASIC_IND_IDENT_TYPE_ID = 88L;
	public static final String SPECIAL_SERVICE_CODE = "ISS";
	public static final String PROF_SERVICE_CODE = "IPS";
	public static final String NEW_IC = "I";
	public static final int PENDING_ACT_STATUS = 8;
	public static final int APPROVED_RECORD_STATUS = 1;

	public static final String SECTION_REV_ROLE_PO = "P";
	public static final String SECTION_REV_ROLE_APP = "A";
	public static final String LOGIN_URL = "";
	public static final String ORG_TYPE_SUPPLIER = "SUP";
	public static final String SUPPLIER_BUSINESS_TYPE = "SBT";

	// SDO BPM Action
	public static final String BPM_INQUIRY = "INQUIRY";
	public static final String BPM_SUBMIT = "SUBMIT";
	public static final String BPM_VERIFY = "VERIFY";

	// Site Visit Assignment
	public static final String SVO_HQ = "UPS_SVO_HQ";
	public static final String SVO_SA = "UPS_SVO_SA";
	public static final String SVO_SK = "UPS_SVO_SK";
	public static final String SVO = "UPS_SVO";
	public static final String SM_RMK_DOC_TYPE_SVO = "SVO";
	public static final String SM_RMK_DOC_TYPE_SVC = "SVC";

	// Site Visit
	public static final String CAT_TYPE_TRANSPORTATION = "A";
	public static final String CAT_TYPE_WORKSHOP = "B";
	public static final String CAT_TYPE_INSURANCE = "I";
	public static final String CAT_TYPE_DOCKYARD = "L";
	public static final String CAT_TYPE_MANUFACTURING = "M";
	public static final String CAT_TYPE_PRINTING = "PC";
	public static final String CAT_TYPE_TRAVEL = "PG";
	public static final String CAT_TYPE_BUMIPUTERA = "BP";
	public static final String CAT_TYPE_READING_MATERIAL_FROM_OVERSEA = "RM";

	public static final String P221601 = "221601";
	public static final String P221602 = "221602";
	public static final String P221603 = "221603";
	public static final String P221604 = "221604";
	public static final String P221605 = "221605";
	public static final String P221607 = "221607";
	public static final String P221609 = "221609";
	public static final String P221610 = "221610";
	public static final String P221611 = "221611";
	public static final String P221612 = "221612";
	public static final String P221613 = "221613";
	public static final String P221614 = "221614";
	public static final String P222003 = "222003";

	// Payment
	public static final String SOFTCERT_PAYMENT = "SF";
	public static final String MOF_PAYMENT = "MF";
	public static final String RENEWAL_PAYMENT = "RF";
	public static final String PROCESSING_PAYMENT = "PF";
	public static final String ONLINE_PAYMENT = "OL";
	public static final String OFFLINE_PAYMENT = "OF";

	public static final String MOL_SUCCESS = "00";
	public static final String MOL_FAIL = "11";
	public static final String MOL_PENDING = "22";
	public static final String PAYMENT_INVALID_RES = "55";

	public static final String PM_STATUS_PAYMENT = "SPY";
	public static final String PM_PAYMENT_MODE = "SPM";
	public static final String PM_PAYMENT_ITEM = "SPI";
	public static final int IS_SOFTCERT_REQ = 2;

	public static final long PM_STAT_PENDING_PAYMENT = 20401;
	public static final long PM_STAT_FAILED_PAYMENT = 20402;
	public static final long PM_STAT_SUCCESS_PAYMENT = 20405;

	// PO and Approver
	public static final String PO_HQ = "UPS_PO";
	public static final String PO_SA = "UPS_PO_SA";
	public static final String PO_SK = "UPS_PO_SK";
	public static final String PO_HQ_TEMP = "UPS_PO_HQ";

	public static final String APPROVER_HQ = "UPS_APPROVER";
	public static final String APPROVER = "APPROVER";
	public static final String APPROVER_HQ_TEMP = "UPS_APPROVER_HQ";
	public static final String APPROVER_SA = "UPS_APPROVER_SA";
	public static final String APPROVER_SK = "UPS_APPROVER_SK";

	public static final String SUPPLIER_ADMIN = "SUPPLIER_ADMIN";
	public static final String SUPPLIER_USER = "SUPPLIER_USER";

	public static final String BASIC_SUPPLIER_ADMIN = "BASIC_SUPPLIER_ADMIN";
	public static final String MOF_SUPPLIER_ADMIN = "MOF_SUPPLIER_ADMIN";
	public static final String BASIC_SUPPLIER_USER = "BASIC_SUPPLIER_USER";
	public static final String MOF_SUPPLIER_USER = "MOF_SUPPLIER_USER";

	// SmPersonnel Parameter for staff role

	public static final String SM_PERSONNEL_STAFF_PARAMTYPE_CONTRACTOR = "KPT";
	public static final String SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT = "JPT";

	public static final String PERSONNEL_MANAGEMENT_STAFF_CONSULTANT_PARAMCODE = "1";
	public static final String PERSONNEL_PROF_STAFF_CONSULTANT_PARAMCODE = "2";
	public static final String PERSONNEL_SEMIPROF_STAFF_CONSULTANT_PARAMCODE = "3";
	public static final String PERSONNEL_CLERICAL_STAFF_CONSULTANT_PARAMCODE = "4";

	public static final String PERSONNEL_MANAGEMENT_ROLENAME = "Management";
	public static final String PERSONNEL_PROF_STAFF_ROLENAME = "Professionl Staff";
	public static final String PERSONNEL_SEMIPROF_STAFF_ROLENAME = "Semi-Professional Staff";
	public static final String PERSONNEL_CLERICAL_STAFF_ROLENAME = "Clerical Staff";
	public static final String PERSONNEL_STAFF_ROLENAME = "Staff";
	public static final String PERSONNEL_HGHR_ROLENAME = "Higher Management";
	public static final String PERSONNEL_DIR_ROLENAME = "Director";
	public static final String PERSONNEL_EQUI_OWNER = "Share Holder";
	public static final String PERSONNEL_TYPE_FRGNR = "NON-MALAYSIAN";
	public static final String PERSONNEL_TYPE_BUMI = "BUMI";
	public static final String PERSONNEL_TYPE_NON_BUMI = "NON-BUMI";

	public static final String PERSONNEL_MANAGEMENT_STAFF_CONTRACTOR_PARAMCODE = "1";
	public static final String PERSONNEL_NORMAL_STAFF_CONTRACTOR_PARAMCODE = "2";

	public static final String SM_APPL_BPM_OUTCOME_SUBMIT = "SUBMIT";
	public static final String SM_APPL_BPM_OUTCOME_DRAFT = "DRAFT";
	public static final String SM_APPL_BPM_OUTCOME_SAVE = "SAVE";

	public static final String STAFF_TYPE_CONTRACTOR = "K";
	public static final String STAFF_TYPE_PROFESSIONAL = "P";
	public static final String STAFF_TYPE_SEMIPROF = "S";
	public static final String STAFF_TYPE_CLERICAL = "C";

	public static final String SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL = "A";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_LLP = "B";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG = "C";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH = "D";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK = "E";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG = "F";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH = "G";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK = "H";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD = "I";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD = "J";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN = "K";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI = "L";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN = "M";

	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT = "ROS";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT = "ROB_I";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT = "ROB_P";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT = "ROC";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT = "LLP";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_UNKNOWN = "UNKNOW";

	public static final String RESUBMIT_APPL_PATH = "/web/dashboard/resubmit-application?";

	public static final String APPL_PM_STATUS_REJECT = "20109";
	// public static final String PM_STATUS_PENDING_APPLICATION_DECLARATION =
	// "20100";
	public static final String PM_STATUS_PENDING_APPLICATION_DECLARATION = "20700";
	public static final String PM_STATUS_PENDING_SUPPORTING_DOCUMENT_VERIFICATION = "20101";
	public static final String PM_STATUS_PENDING_APPLICATION_VERIFICATION = "20102";
	public static final String PM_STATUS_PENDING_REVIEW_SITE_VISIT_REPORT = "20103";
	public static final String PM_STATUS_PENDING_RE_REVIEW = "20104";
	public static final String PM_STATUS_PENDING_SITE_VISIT = "20105";
	public static final String PM_STATUS_PENDING_RE_SITE_VISIT = "20106";
	public static final String PM_STATUS_PENDING_REPLY_INQUIRY_BY_PROCESSING_OFFICER = "20107";
	public static final String PM_STATUS_PENDING_APPLICATION_APPROVAL = "20108";
	public static final String PM_STATUS_PENDING_APPLICATION_RE_APPROVAL = "20109";
	public static final String PM_STATUS_PENDING_SITE_VISIT_ASSIGNMENT = "20110";
	public static final String PM_STATUS_PENDING_RE_SITE_VISIT_ASSIGNMENT = "20111";
	public static final String PM_STATUS_REJECTED_BY_PROCESSING_OFFICER = "20112";
	public static final String PM_STATUS_REJECTED_BY_APPROVER = "20113";
	public static final String PM_STATUS_APPROVED_BY_APPROVER = "20113";
	public static final String PM_STATUS_PENDING_PAYMENT_PROCESSING_FEE = "20114";
	public static final String PM_STATUS_PENDING_CERTIFCATE_GENERATION = "20198";
	public static final String PM_STATUS_REGISTERED = "20199";
	public static final String PM_STATUS_PENDING_APPLICATION_REJECTION = "20115";
	public static final String PM_STATUS_DELETE_COMPANY = "20188";
	public static final long PM_STATUS_PO_REVIEW = 20104;

	public static final String SM_APPL_PAGE_TYPE_BASIC = "BASICINFO";
	public static final String SM_APPL_PAGE_TYPE_BRANCH = "BRANCHINFO";
	public static final String SM_APPL_PAGE_TYPE_EQUITY = "EQUITYINFO";
	public static final String SM_APPL_PAGE_TYPE_PERSONNEL = "PERSONNELINFO";
	public static final String SM_APPL_PAGE_TYPE_BANK = "BANKINFO";
	public static final String SM_APPL_PAGE_TYPE_CATEGORY = "CATEGORYINFO";
	public static final String SM_APPL_PAGE_TYPE_FACTORY = "FACTORYINFO";
	public static final String SM_APPL_PAGE_TYPE_EQUIP = "EQUIPINFO";
	public static final String SM_APPL_PAGE_TYPE_SUPPORTDOCS = "SUPPORTDOCSINFO";
	public static final String SM_APPL_PAGE_TYPE_PAYMENT = "PAYMENTINFO";
	public static final String SM_APPL_PAGE_TYPE_SUPPORTDOCSREV = "SUPPORTDOCSREVIEW";
	public static final String SM_APPL_PAGE_TYPE_SITEVISITREV = "SITEVISITREVSION";
	public static final String SM_APPL_PAGE_TYPE_PODEC = "PODECISION";
	public static final String SM_APPL_PAGE_TYPE_APPRDEC = "APPROVERDECISION";

	public static final String SM_MOF_CERT_SERIAL_SEQ = "SM_MOF_CERT_SERIAL_SEQ";
	public static final String SM_MOF_465_SEQ = "SM_MOF_465_SEQ";
	public static final String SM_MOF_357_0_SEQ = "SM_MOF_357_0_SEQ";
	public static final String SM_MOF_357_2_SEQ = "SM_MOF_357_2_SEQ";
	public static final String SM_MOF_357_3_SEQ = "SM_MOF_357_3_SEQ";
	public static final String SM_MYIDENTITY_STATUS_CODE_VALID = "70018";
	public static final String SM_MYIDENTITY_SEVERITY_SUCC = "SUCC";

	public static final int RECORD_STATUS_DELETED = 9;
	public static final int RECORD_STATUS_SUSPENDED = 0;

	public static final String REV_APPL_PATH = "/web/dashboard/view-revision-application?";

	// Site Visit
	public static final String SVO_FIN_CHECKLIST = "FCL";
	public static final String MALAYSIA_CODE = "MY";
	public static final String SABAH_CODE = "SA";
	public static final String SARAWAK_CODE = "13";
	public static final String KUCHING_DISTRICT_CODE = "1301";

	// Pm Edu codes
	public static final String SUPPLIER_EDU_PHD = "1";
	public static final String SUPPLIER_EDU_MASTERS = "2";
	public static final String SUPPLIER_EDU_BACHELORS = "3";
	public static final String SUPPLIER_EDU_DIPLOMA = "4";
	public static final String SUPPLIER_EDU_CERTIFIED = "5";
	public static final String SUPPLIER_EDU_NOTDEFINED = "6";

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

	public String getPROF_SERVICE() {
		return PROF_SERVICE;
	}

	public String getSPECIAL_SERVICE() {
		return SPECIAL_SERVICE;
	}

	public int getRECORD_DELETED() {
		return RECORD_DELETED;
	}

	public String getINDIVIDUAL_FLAG() {
		return INDIVIDUAL_FLAG;
	}

	public String getSTYPE_CONSULTANT() {
		return STYPE_CONSULTANT;
	}

	public String getSTYPE_CONTRACTOR() {
		return STYPE_CONTRACTOR;
	}

	public String getCOUNTRY_MALAYSIA_CODE() {
		return COUNTRY_MALAYSIA_CODE;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROC_BHD() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT;
	}

	public String getBPM_SUBMIT() {
		return BPM_SUBMIT;
	}

	public String getBPM_INQUIRY() {
		return BPM_INQUIRY;
	}

	public String getBPM_VERIFY() {
		return BPM_VERIFY;
	}

	public String getCAT_TYPE_TRANSPORTATION() {
		return CAT_TYPE_TRANSPORTATION;
	}

	public String getCAT_TYPE_WORKSHOP() {
		return CAT_TYPE_WORKSHOP;
	}

	public String getCAT_TYPE_INSURANCE() {
		return CAT_TYPE_INSURANCE;
	}

	public String getCAT_TYPE_DOCKYARD() {
		return CAT_TYPE_DOCKYARD;
	}

	public String getCAT_TYPE_MANUFACTURING() {
		return CAT_TYPE_MANUFACTURING;
	}

	public String getCAT_TYPE_PRINTING() {
		return CAT_TYPE_PRINTING;
	}

	public String getCAT_TYPE_TRAVEL() {
		return CAT_TYPE_TRAVEL;
	}

	public String getCAT_TYPE_BUMIPUTERA() {
		return CAT_TYPE_BUMIPUTERA;
	}

	public String getCAT_TYPE_READING_MATERIAL_FROM_OVERSEA() {
		return CAT_TYPE_READING_MATERIAL_FROM_OVERSEA;
	}

	public String getACC_BASIC() {
		return ACC_BASIC;
	}

	public String getACC_MOF() {
		return ACC_MOF;
	}

	public String getSM_APPL_PAGE_TYPE_BASIC() {
		return SM_APPL_PAGE_TYPE_BASIC;
	}

	public String getSM_APPL_PAGE_TYPE_BRANCH() {
		return SM_APPL_PAGE_TYPE_BRANCH;
	}

	public String getSM_APPL_PAGE_TYPE_EQUITY() {
		return SM_APPL_PAGE_TYPE_EQUITY;
	}

	public String getSM_APPL_PAGE_TYPE_PERSONNEL() {
		return SM_APPL_PAGE_TYPE_PERSONNEL;
	}

	public String getSM_APPL_PAGE_TYPE_BANK() {
		return SM_APPL_PAGE_TYPE_BANK;
	}

	public String getSM_APPL_PAGE_TYPE_CATEGORY() {
		return SM_APPL_PAGE_TYPE_CATEGORY;
	}

	public String getSM_APPL_PAGE_TYPE_FACTORY() {
		return SM_APPL_PAGE_TYPE_FACTORY;
	}

	public String getSM_APPL_PAGE_TYPE_EQUIP() {
		return SM_APPL_PAGE_TYPE_EQUIP;
	}

	public String getSM_APPL_PAGE_TYPE_SUPPORTDOCS() {
		return SM_APPL_PAGE_TYPE_SUPPORTDOCS;
	}

	public String getSM_APPL_PAGE_TYPE_PAYMENT() {
		return SM_APPL_PAGE_TYPE_PAYMENT;
	}

	public String getSM_APPL_PAGE_TYPE_SUPPORTDOCSREV() {
		return SM_APPL_PAGE_TYPE_SUPPORTDOCSREV;
	}

	public String getSM_APPL_PAGE_TYPE_SITEVISITREV() {
		return SM_APPL_PAGE_TYPE_SITEVISITREV;
	}

	public String getSM_APPL_PAGE_TYPE_PODEC() {
		return SM_APPL_PAGE_TYPE_PODEC;
	}

	public String getSM_APPL_PAGE_TYPE_APPRDEC() {
		return SM_APPL_PAGE_TYPE_APPRDEC;
	}

	public String getPERSONNEL_TYPE_EQUITY() {
		return PERSONNEL_TYPE_EQUITY;
	}

	public String getSTAFF_TYPE_CONTRACTOR() {
		return STAFF_TYPE_CONTRACTOR;
	}

	public String getSTAFF_TYPE_PROFESSIONAL() {
		return STAFF_TYPE_PROFESSIONAL;
	}

	public String getSTAFF_TYPE_SEMIPROF() {
		return STAFF_TYPE_SEMIPROF;
	}

	public String getSTAFF_TYPE_CLERICAL() {
		return STAFF_TYPE_CLERICAL;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT() {
		return SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT;
	}

	public String getNEW_IC() {
		return NEW_IC;
	}

	public int getPENDING_ACT_STATUS() {
		return PENDING_ACT_STATUS;
	}

	public int getAPPROVED_RECORD_STATUS() {
		return APPROVED_RECORD_STATUS;
	}

	public String getSM_SUPPLIER_BUSINESS_TYPE_LLP() {
		return SM_SUPPLIER_BUSINESS_TYPE_LLP;
	}

}
