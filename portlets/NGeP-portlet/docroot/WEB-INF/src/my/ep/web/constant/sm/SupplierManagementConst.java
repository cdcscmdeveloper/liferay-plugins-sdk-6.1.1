/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.constant.sm;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.constants.SMProcessNameConst;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;

public class SupplierManagementConst {

	public static final Map<String, String> APPLICATION_BASE_PAGE_BY_TYPE;
	static {
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put("N",
				"/xhtml/sm/NewMofApplication/smMofRegSideMenu_BasePage.xhtml");
		tmp.put("C",
				"/xhtml/sm/NewMofApplication/smMofRegSideMenu_BasePage.xhtml");
		tmp.put("A",
				"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
		tmp.put("B",
				"/xhtml/sm/bumiPuteraApplication/smBumiSideMenu_BasePage.xhtml");
		tmp.put("U",
				"/xhtml/sm/updateMofDetails/smUpdateMofSideMenu_BasePage.xhtml");
		tmp.put("R",
				"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
		APPLICATION_BASE_PAGE_BY_TYPE = Collections.unmodifiableMap(tmp);
	}
	// Navigation
	public static final String PROCEED_MOF_REG = "mofRegSmUser_BaseWizardPage";
	public static final String PROCEED_MOF_REG_TNC_PAGE = "/xhtml/sm/NewMofApplication/newMofApplTermsAndConditionsPage.xhtml";
	// public static final String PROCEED_MOF_REG = "mofRegBasePage";
	public static final String MOF_REG_REDIRECT = "mofRegSmUser_BaseWizardPage?faces-redirect=true";
	public static final String PROCEED_BUMI_APPLN = "applyBumiStatusBasePage";
	public static final String PROCEED_BUMI_APPLN_SIDEMENU = "smBumiSideMenu_BasePage";
	public static final String PROCEED_BUMI_APPLN_TNC_PAGE = "/xhtml/sm/bumiPuteraApplication/bumiApplicationTermsAndConditionPage.xhtml";
	// public static final String PROCEED_BUMI_APPLN =
	// "applyBumiSmUser_BaseWizardPage";
	public static final String PROCEED_UPDATE_MOF_APPLN = "updateMofDetailsBasePage";
	public static final String PROCEED_UPDATE_APPLN_SIDEMENU = "smUpdateMofSideMenu_BasePage";
	public static final String PROCEED_UPDATE_APPLN_TNC_PAGE = "/xhtml/sm/updateMofDetails/updateMofDetailsTermsAndConditionsPage.xhtml";
	// public static final String PROCEED_UPDATE_MOF_APPLN =
	// "updateMofSmUser_BaseWizardPage";
	public static final String PROCEED_ADDTNLCAT_MOF_APPLN = "addAdditionalCategoryBasePage";
	// public static final String PROCEED_ADDTNLCAT_APPLN_SIDEMENU =
	// "smAddtnlCatApplnSideMenu_BasePage";
	public static final String PROCEED_ADDTNLCAT_APPLN_SIDEMENU = "/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml";
	public static final String PROCEED_ADDTNLCAT_APPLN_TNC_PAGE = "/xhtml/sm/addAdditionalCategoryCodes/additionalCategoryCodeTermsAndConditionPage.xhtml";
	// public static final String PROCEED_ADDTNLCAT_MOF_APPLN =
	// "addAdditionalCategorySmUser_BaseWizardPage";
	public static final String PROCEED_RENEW_MOF_APPLN = "renewMofBasePage";
	public static final String PROCEED_RENEW_APPLN_SIDEMENU = "smRenewRegSideMenu_BasePage";
	public static final String PROCEED_RENEW_APPLN_TNC_PAGE = "/xhtml/sm/renewMofApplication/renewMofApplTermsAndConditionsPage.xhtml";
	// public static final String PROCEED_RENEW_MOF_APPLN =
	// "renewMofSmUser_BaseWizardPage";
	public static final String PROCEED_CANCEL_MOF_APPLN = "cancelRegistrationWithMof";
	public static final String PROCEED_CANCEL_MOF_APPLN_TNC_PAGE = "/xhtml/sm/cancelRegWithMof/cancelMofApplicationTermsAndConditionPage.xhtml";
	public static final String PROCEED_DELETE_PENDING_APPLN_TNC_PAGE = "/xhtml/sm/deletePendingMofAppln/deletePendingMofApplnTermsAndConditionsPage.xhtml";
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
	public static final int NEW_APPL_SPLCAT_REVIEW_MAXCOUNT = 18;
	public static final int BUMI_APPL_NORMCAT_REVIEW_MAXCOUNT = 7;
	public static final int BUMI_APPL_SPLCAT_REVIEW_MAXCOUNT = 12;
	public static final int UPDATE_MOF_APPL_REVIEW_MAXCOUNT = 13;
	public static final int RENEW_APPL_NORMCAT_REVIEW_MAXCOUNT = 13;
	public static final int RENEW_APPL_SPLCAT_REVIEW_MAXCOUNT = 18;
	public static final int ADDLN_CATCODES_APPL_NORMCAT_REVIEW_MAXCOUNT = 13;
	public static final int ADDLN_CATCODES_APPL_SPLCAT_REVIEW_MAXCOUNT = 16;
	public static final int RECORD_DELETED = 1;
	public static final String COUNTRY_MALAYSIA_CODE = "MY";
	public static final String ICT_CATEGORY_L1_CODE = "21";
	public static final String ICT_L2_COMMUNICATION_CODE = "02";

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

	// Facility Type Map
	public static final Map<String, String> FACILITY_TYPE_MAP;
	static {
		FACILITY_TYPE_MAP = new HashMap<String, String>();
		FACILITY_TYPE_MAP.put(SM_FACILITY_TYPE_FACTORY,
				SmCommonUtil.getMessage("lbl.sm.facility.type.factory", null));
		FACILITY_TYPE_MAP.put(SM_FACILITY_TYPE_DOCKYARD,
				SmCommonUtil.getMessage("lbl.sm.facility.type.dockyard", null));
		FACILITY_TYPE_MAP.put(SM_FACILITY_TYPE_WORKSHOP,
				SmCommonUtil.getMessage("lbl.sm.facility.type.workshop", null));
		FACILITY_TYPE_MAP.put(SM_FACILITY_TYPE_STORE,
				SmCommonUtil.getMessage("lbl.sm.facility.type.store", null));
		FACILITY_TYPE_MAP.put(SM_FACILITY_TYPE_HOMEOFFICE, SmCommonUtil
				.getMessage("lbl.sm.facility.type.home.office", null));
		FACILITY_TYPE_MAP.put(SM_FACILITY_TYPE_BUSSINESSOFFICE, SmCommonUtil
				.getMessage("lbl.sm.facility.type.business.office", null));
	}

	public static String[] CAT_TYPE_CODES = new String[] {
			SupplierManagementConst.CAT_TYPE_TRANSPORTATION,
			SupplierManagementConst.CAT_TYPE_WORKSHOP,
			SupplierManagementConst.CAT_TYPE_INSURANCE,
			SupplierManagementConst.CAT_TYPE_DOCKYARD,
			SupplierManagementConst.CAT_TYPE_MANUFACTURING,
			SupplierManagementConst.CAT_TYPE_PRINTING,
			SupplierManagementConst.CAT_TYPE_TRAVEL };

	public static List<String> PROF_STAFF_EDU_LEVEL_CODES = Arrays
			.asList(new String[] { "1", "2", "3" });
	public static List<String> SEMI_PROF_STAFF_EDU_LEVEL_CODES = Arrays
			.asList(new String[] { "1", "2", "3", "4" });
	public static List<String> CLERICAL_STAFF_EDU_LEVEL_CODES = Arrays
			.asList(new String[] { "1", "2", "3", "4", "5" });
	public static List<String> EQUI_OWNER_EDU_LEVEL_CODES = Arrays
			.asList(new String[] { "1", "2", "3" });

	// Application Category Type
	public static final String SM_APPL_CATEGORY_TYPE_NORMAL = "N";
	public static final String SM_APPL_CATEGORY_TYPE_SPECIAL = "S";

	public static final String PERSONNEL_TYPE_DIRECTOR = "DIR";
	public static final String PERSONNEL_TYPE_STAFF = "STF";
	public static final String PERSONNEL_TYPE_EQUITY = "EQUI";

	// GOVT SELLER REGISTRATION
	public static final String GOVT_SELLER_TNC_PAGE = "1";
	public static final String GOVT_SELLER_SEARCH_PAGE = "2";
	public static final String GOVT_SELLER_ACCOUNT_INFO_PAGE = "3";
	public static final String GOVT_SELLER_USER_INFO_PAGE = "4";

	// Basic Registration && Account Maintenance
	public static final String INDIVIDUAL_FLAG = "A";
	public static final String STYPE_CONSULTANT = "J";
	public static final String STYPE_CONTRACTOR = "K";
	public static final String STYPE_BASIC = "B";
	public static final String ACC_BASIC = "O";
	public static final String ACC_MOF = "N";
	public static final String ACC_GOVT_SELLER = "G";
	public static final String ACC_BASIC_REACTIVATE = "R";
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
	public static final int REG_SOFTCERT = 2;
	public static final int REG_BUMI = 0;
	public static final String BASIC_IND_IDENT_TYPE = "I";
	public static final Long BASIC_IND_IDENT_TYPE_ID = 88L;
	public static final String SPECIAL_SERVICE_CODE = "ISS";
	public static final String PROF_SERVICE_CODE = "IPS";
	public static final String NEW_IC = "I";
	public static final int PENDING_ACT_STATUS = 8;
	public static final int APPROVED_RECORD_STATUS = 1;
	public static final int DELETED_RECORD_STATUS = 9;
	public static final int PENDING_RE_APPROVAL_STATUS = 6;
	public static final String TEMP_TYPE = "T";

	public static final String SECTION_REV_ROLE_PO = "P";
	public static final String SECTION_REV_ROLE_APP = "A";
	public static final String LOGIN_URL = "";
	public static final String ORG_TYPE_SUPPLIER = "SUP";
	public static final String SUPPLIER_BUSINESS_TYPE = "SBT";

	// SDO BPM Action
	public static final String BPM_INQUIRY = "INQUIRY";
	public static final String BPM_SUBMIT = "SUBMIT";
	public static final String BPM_VERIFY = "VERIFY";

	// Supporting Doc Type
	public static final String SUPPORTING_DOC_ONLINE = "S";
	public static final String SUPPORTING_DOC_OFFLINE = "H";

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

	public static final String MOL_SUCCESS = my.ep.constant.sm.SupplierManagementConst.MOL_SUCCESS;
	public static final String MOL_FAIL = my.ep.constant.sm.SupplierManagementConst.MOL_FAIL;
	public static final String MOL_PENDING = my.ep.constant.sm.SupplierManagementConst.MOL_PENDING;
	public static final String PAYMENT_INVALID_RES = my.ep.constant.sm.SupplierManagementConst.PAYMENT_INVALID_RES;

	public static final String PM_STATUS_PAYMENT = "SPY";
	public static final String PM_PAYMENT_MODE = "SPM";
	public static final String PM_PAYMENT_ITEM = "SPI";

	// soft cert status
	public static final int IS_SOFTCERT_REQ = my.ep.constant.sm.SupplierManagementConst.IS_SOFTCERT_REQ;
	public static final int SOFT_CERT_PROCESSING = my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_PROCESSING;
	public static final int SOFT_CERT_PENDING_REQ = my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_PENDING_REQ;
	public static final int SOFT_CERT_ISSUED = my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_ISSUED;
	public static final int SOFT_CERT_REVOKED = my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_REVOKED;
	public static final int SOFT_CERT_REJECTED = my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_REJECTED;
	public static final int SOFT_CERT_READY_FOR_RENEWAL = my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_READY_FOR_RENEWAL;
	public static final int SOFT_CERT_REQ_FAILED = my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_REQ_FAILED;

	// rejected soft cert reason code
	public static final String REJECTED_WITH_FRAUD = "R";
	public static final String REJECTED_WITH_BLACKLIST = "B";
	public static final String REJECTED_WITH_INCOMPLETE_SUPPDOC = "I";
	public static final String REJECTED_WITH_NO_CONFIG_CHALLENGE_RESPONSE = "N";

	// GST rule value constant
	public static String GST_RULE_VALUE = "GST";

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

	public static final String PERSONNEL_MANAGEMENT_ROLENAME = "lbl.sm.personnel.comprole.mngmnt";
	public static final String PERSONNEL_PROF_STAFF_ROLENAME = "lbl.sm.personnel.comprole.professional.staff";
	public static final String PERSONNEL_SEMIPROF_STAFF_ROLENAME = "lbl.sm.personnel.comprole.semi.professional.staff";
	public static final String PERSONNEL_CLERICAL_STAFF_ROLENAME = "lbl.sm.personnel.comprole.clerical.staff";
	public static final String PERSONNEL_STAFF_ROLENAME = "lbl.sm.personnel.comprole.staff";
	public static final String PERSONNEL_HGHR_ROLENAME = "lbl.sm.personnel.comprole.higher.mngmnt";
	public static final String PERSONNEL_DIR_ROLENAME = "lbl.sm.personnel.comprole.director";
	public static final String PERSONNEL_EQUI_OWNER = "lbl.sm.personnel.comprole.shareholder";
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

	public static final String SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_LLP = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_NGO = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_NGO;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2 = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2 = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER;
	public static final String SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY = my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY;

	public static List<String> LIST_OF_BUSINESS_TYPE_ALLOWED_TO_BE_EDITED = Arrays
			.asList(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH,
					SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK,
					SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH,
					SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK,
					SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG,
					SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG);
	public static List<String> LIST_OF_BUSINESS_TYPE_NOT_ALLOWED_TO_EDIT_COMPANY_NAME = Arrays
			.asList(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG,
					SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG);
	public static List<String> LOCAL_AUTH_BUSINESS_TYPE = Arrays
			.asList(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH,
					SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH,
					SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK,
					SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK);

	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT = "ROS";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT = "ROB_I";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT = "ROB_P";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT = "ROC";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT = "LLP";
	public static final String SM_SUPPLIER_BUSINESS_TYPE_UNKNOWN = "UNKNOW";
	
	public static final String MOF_STATUS_ACTIVE = my.ep.constant.sm.SupplierManagementConst.MOF_STATUS_ACTIVE;
	public static final String MOF_STATUS_EXPIRED = my.ep.constant.sm.SupplierManagementConst.MOF_STATUS_EXPIRED;
	public static final String MOF_STATUS_NOT_AVAILABLE = my.ep.constant.sm.SupplierManagementConst.MOF_STATUS_NOT_AVAILABLE;

	public static final String RESUBMIT_APPL_PATH = "resubmit-application?";

	public static final String APPL_PM_STATUS_REJECT = "20109";
	public static final String PM_STATUS_PENDING_APPLICATION_DECLARATION = "20100";
	// public static final String PM_STATUS_PENDING_APPLICATION_DECLARATION =
	// "20700";
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
	public static final String PM_STATUS_REJECTED_BY_SYSTEM = "20118";
	public static final String PM_STATUS_PENDING_PAYMENT_PROCESSING_FEE = "20114";
	public static final String PM_STATUS_PENDING_CERTIFCATE_GENERATION = "20198";
	public static final String PM_STATUS_REGISTERED = "20199";
	public static final String PM_STATUS_PENDING_APPLICATION_REJECTION = "20115";
	public static final String PM_STATUS_DELETE_COMPANY = "20188";
	public static final long PM_STATUS_PO_REVIEW = 20104;
	public static final long PM_STATUS_PO_ESCALTED_TO_APPROVER = 20108;
	public static final long PM_STATUS_PO_REJECTED_APPLICATION = 20112;
	public static final long PM_STATUS_APPROVER_REJECTED_APPLICATION = 20113;
	public static final long PM_STATUS_PENDING_APPL_DECLARATION = 20100;
	public static final long PM_STATUS_PENDING_PROCESS_FEE = 20114;
	public static final long PM_STATUS_PENDING_PAYMENT = 20251;
	public static final long PM_STATUS_PENDING_VERIFY_SUPP_DOCS = 20101;
	public static final long PM_STATUS_MOF_ACCT_MAINTAIN_APPROVED = 20299;
	public static final long PM_STATUS_MOF_ACCT_REG_APPROVED = 20199;
	public static final long PM_STATUS_PENDING_REVIEW_CANCELLATION = 20301;
	public static final long PM_STATUS_PENDING_SITE_VISIT_AFTER_APPROVED = 20117;
	public static final long PM_STATUS_PENDING_SITE_VISIT_REPORT_AFTER_APPROVED = 20116;
	public static final long PM_STATUS_REJECTED_BY_SYS = 20118L;

	public static final String SM_APPL_PAGE_TYPE_BASIC = "BASICINFO";
	public static final String SM_APPL_PAGE_TYPE_BRANCH = "BRANCHINFO";
	public static final String SM_APPL_PAGE_TYPE_EQUITY = "EQUITYINFO";
	public static final String SM_APPL_PAGE_TYPE_PERSONNEL = "PERSONNELINFO";
	public static final String SM_APPL_PAGE_TYPE_BANK = "BANKINFO";
	public static final String SM_APPL_PAGE_TYPE_CATEGORY = "CATEGORYINFO";
	public static final String SM_APPL_PAGE_TYPE_FACTORY = "FACTORYINFO";
	public static final String SM_APPL_PAGE_TYPE_EQUIP = "EQUIPINFO";
	public static final String SM_APPL_PAGE_TYPE_SUPPORTDOCS = "SUPPORTDOCSINFO";
	public static final String SM_APPL_PAGE_TYPE_VIRTUAL_CERT = "VIRTUALCERTINFO";
	public static final String SM_APPL_PAGE_TYPE_TRACKING_DIARY = "TRACKINGDIARYINFO";
	public static final String SM_APPL_PAGE_TYPE_PAYMENT = "PAYMENTINFO";
	public static final String SM_APPL_PAGE_TYPE_SUPPORTDOCSREV = "SUPPORTDOCSREVIEW";
	public static final String SM_APPL_PAGE_TYPE_SITEVISITREV = "SITEVISITREVSION";
	public static final String SM_APPL_PAGE_TYPE_PODEC = "PODECISION";
	public static final String SM_APPL_PAGE_TYPE_APPRDEC = "APPROVERDECISION";
	public static final String SM_APPL_BUTTON_TYPE_PREV = "PREV";
	public static final String SM_APPL_BUTTON_TYPE_SAME = "SAME";
	public static final String SM_APPL_BUTTON_TYPE_NEXT = "NEXT";

	public static final String SM_MOF_CERT_SERIAL_SEQ = "SM_MOF_CERT_SERIAL_SEQ";
	public static final String SM_MOF_465_SEQ = "SM_MOF_465_SEQ";
	public static final String SM_MOF_357_0_SEQ = "SM_MOF_357_0_SEQ";
	public static final String SM_MOF_357_2_SEQ = "SM_MOF_357_2_SEQ";
	public static final String SM_MOF_357_3_SEQ = "SM_MOF_357_3_SEQ";
	

	public static final int RECORD_STATUS_DELETED = 9;
	public static final int RECORD_STATUS_SUSPENDED = 0;

	public static final String REV_APPL_PATH = "/web/dashboard/view-revision-application?";

	// Revision Application Type
	public static final String ORIGINAL_APPLICATION = "O";
	public static final String PREVIOUS_APPLICATION = "P";

	// Site Visit
	// public static final String SVO_FIN_CHECKLIST = "FCL";
	public static final String MALAYSIA_CODE = "MY";
	public static final String SABAH_CODE = my.ep.constant.sm.SupplierManagementConst.PM_SABAH_STATE_CODE;
	public static final String SARAWAK_CODE = my.ep.constant.sm.SupplierManagementConst.PM_SARAWAK_STATE_CODE;
	public static final String KUCHING_DISTRICT_CODE = my.ep.constant.sm.SupplierManagementConst.KUCHING_DISTRICT_CODE;

	// Pm Edu codes
	public static final String SUPPLIER_EDU_PHD = "1";
	public static final String SUPPLIER_EDU_MASTERS = "2";
	public static final String SUPPLIER_EDU_BACHELORS = "3";
	public static final String SUPPLIER_EDU_DIPLOMA = "4";
	public static final String SUPPLIER_EDU_CERTIFIED = "5";
	public static final String SUPPLIER_EDU_NOTDEFINED = "6";

	// Pm Edu Level Codes
	public static final String SUPPLIER_EDU_LEVEL_CERT = "60";
	public static final String SUPPLIER_EDU_LEVEL_DIPLOMA = "80";
	public static final long SUPPLIER_EDU_LEVEL_MIN_FOR_STF = 30;
	public static final long SUPPLIER_EDU_LEVEL_MIN_FOR_PRFSNL = 91;
	public static final long SUPPLIER_EDU_LEVEL_MIN_FOR_SEMIPRF = 80;

	// PO BPM Task names
	public static final String BPM_TASK_REVIEW_APPL_ESCALATED = SMProcessNameConst.REVIEW_APPL_ESCALATED;
	public static final String BPM_TASK_REVIEW_APPL = SMProcessNameConst.REVIEW_APPL;
	public static final String BPM_TASK_REVIEW_APPL_SPLCAT = SMProcessNameConst.REVIEW_APPL_SPLCAT;
	public static final String BPM_TASK_VERIFY_APPL = SMProcessNameConst.VERIFY_APPL;
	public static final String BPM_TASK_REVIEW_SITE_VISIT_AFTER_APPROVE = SMProcessNameConst.REVIEW_SITE_VISIT_AFTER_APPROVE;

	public static final double MIN_AMT_RANGE = 0.01;
	public static final double MAX_AMT_RANGE = 99999999999999.99;
	public static final double MIN_EXP_RANGE = 0.10;
	public static final double MAX_EXP_RANGE = 99.11;

	// PM Cert body
	public static final String PM_CERT_BODY_OTHERS = "LL";

	// Pm category facility types
	public static final long CATEGORY_TYPE_ID_TRANSPORTATION = 203;
	public static final long CATEGORY_TYPE_ID_WORKSHOP = 204;
	public static final long CATEGORY_TYPE_ID_INSURANCE = 205;
	public static final long CATEGORY_TYPE_ID_DOCKYARD = 206;
	public static final long CATEGORY_TYPE_ID_MANUFACTURING = 207;
	public static final long CATEGORY_TYPE_ID_NORMAL = 208;
	public static final long CATEGORY_TYPE_ID_PRINTING = 209;
	public static final long CATEGORY_TYPE_ID_TRAVEL = 210;
	public static final long CATEGORY_TYPE_ID_READING = 1261;

	// SM Action description for sm tracking dairy
	public static final String SUPP_SAVE_APPL_DRAFT = "Pembekal simpan draf permohonan";
	public static final String SUPP_SUBMIT_NORM_APPL = "Pembekal hantar permohonan untuk diluluskan";
	public static final String SUPP_SUBMIT_CANCREG_APPL = "Pembekal hantar pembatalan pendaftaran dengan MOF untuk diluluskan";
	public static final String SUPP_SUBMIT_DELDRFT_APPL = "Pembekal hantar hapus permohonan belum selesai untuk diluluskan";
	public static final String PO_RECOM_SITE_VISIT = "PO mencadang untuk lawatan tapak";
	public static final String PO_REJECT_APPL = "Permohonan ditolak oleh PO";
	public static final String APPROVER_REJECT_APPL = "Permohonan ditolak oleh Pelulus";
	public static final String PO_APPROVE_APPL = "Permohonan diluluskan oleh PO";
	public static final String APPROVER_APPROVE_APPL = "Permohonan diluluskan oleh Pelulus";
	public static final String PO_SEND_INQUIRY_SUPP = "PO hantar pertanyaan kepada pembekal";
	public static final String PO_ESCALATE_APPROVER = "PO hantar permohonan kepada Pelulus untuk keputusan akhir";
	public static final String SUPP_REGISTRATION = "Pendaftaran Pembekal";
	public static final String SUPP_ACCOUNT_ACTIVATION = "Pengaktifan Akaun Pendaftaran";

	public static final String SDO_SUBMIT_APPL = "SDO hantar senarai dokumen sokongan";
	public static final String SDO_SEND_INQUIRY = "SDO hantar pertanyaan kepada pembekal";
	public static final String SUPP_SEND_INQUIRY_SDO = "Pembekal hantar respon kepada pembekal";
	public static final String SVC_ASSIGN_SITEVISIT = "SVC beri tugasan lawatan tapak kepada SVO";
	public static final String SVO_SUBMIT_REPORT = "SVO hantar laporan lawatan tapak";

	public static final String APPROVER_SUBMIT_SUPPLIER_DATA_MAINTENANCE = "Pelulus hantar data pembekal yang telah diselenggara";
	public static final String APPROVER_SUBMIT_CANCEL_SUPPLIER_MOF_ACCOUNT = "Pelulus hantar untuk batalkan akaun MOF pembekal";
	public static final String APPROVER_SUBMIT_RESEND_EXPIRED_SDO_INQUIRY = "Pelulus hantar permohonan yang telah luput pertanyaan SDO";

	public static final String SM_STATE_NAME_SABAH = "SABAH";
	public static final String SM_STATE_NAME_SARAWAK = "SARAWAK";
	public static final String SM_STATE_NAME_SEMENANJUNG = "SEMENANJUNG";

	public static final String SM_SUPPLIER_CERT_TYPE_GLP = "GLP";
	public static final String SM_SUPPLIER_PARAM_TYPE_CRT = "CRT";

	public static final Integer SM_SUPPORTDOCS_FILE_SIZE_LIMIT = 20000000;
	public static final String GST_ATTACHMENT_DOC_TYPE = "GST";
	public static final String SM_REASON_CODE_OTHERS = "OTHERS";
	public static final String SM_REASON_CODE_OTHER = "OTHER";

	public static final String GST_FILE_PATH = "/SM/GST/";
	public static final String SITE_VISIT_REPORT_PATH = "/SM/SITE_VISIT/";
	public static final String OFFLINE_DOC_CHECKLIST_PATH = "/SM/OFFLINE_DOC/CHECKLIST/";
	public static final String SUPPORTING_DOC_TEMP_PATH = "/SM/SUPPORTING_TEMP_DOWNLOAD/";

	// SME STATUS
	public static final String SME_STATUS_SERVICES = my.ep.constant.sm.SupplierManagementConst.SME_STATUS_SERVICES;
	public static final String SME_STATUS_MANUFACTURING = my.ep.constant.sm.SupplierManagementConst.SME_STATUS_MANUFACTURING;
	public static final String SME_STATUS_NONE_SME = my.ep.constant.sm.SupplierManagementConst.SME_STATUS_NONE_SME;
	public static final String SME_STATUS_BOTH = my.ep.constant.sm.SupplierManagementConst.SME_STATUS_BOTH;

	// SUPPLIER TYPE
	public static final String SUPP_TYPE_CONTRACTOR = my.ep.constant.sm.SupplierManagementConst.SUPP_TYPE_CONTRACTOR;
	public static final String SUPP_TYPE_CONSULTANT = my.ep.constant.sm.SupplierManagementConst.SUPP_TYPE_CONSULTANT;
	public static final String SUPP_TYPE_BASIC = my.ep.constant.sm.SupplierManagementConst.SUPP_TYPE_BASIC;
	public static final String SUPP_TYPE_GOVT_TO_GOVT = my.ep.constant.sm.SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT;

	public static final String SITE_VISIT_ASSIGNMENT_TYPE_ALL = "C";
	public static final String SITE_VISIT_ASSIGNMENT_TYPE_BEFORE = "B";
	public static final String SITE_VISIT_ASSIGNMENT_TYPE_AFTER = "A";

	public static final String OFFLINE_SUPP_DOC_CHECKLIST_PREFIX = "_SUPP_DOC_CHECKLIST.pdf";
	public static final String DECLARE_SUPP_DOC_PREFIX = "_DECLARATION.pdf";
	public static final String BANK_VERIFICATION_FORM_PREFIX = "_PENANDATANGAN_CEK.pdf";

	public static final int MAX_PAYMENT_BANK = 35;

	// Getters for panel info
	public String getSUPP_TYPE_CONTRACTOR() {
		return SUPP_TYPE_CONTRACTOR;
	}

	public String getSUPP_TYPE_CONSULTANT() {
		return SUPP_TYPE_CONSULTANT;
	}

	public String getSUPP_TYPE_BASIC() {
		return SUPP_TYPE_BASIC;
	}

	public String getSUPP_TYPE_GOVT_TO_GOVT() {
		return SUPP_TYPE_GOVT_TO_GOVT;
	}

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

	public String getSM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2() {
		return SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2;
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

	public String getSM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER() {
		return SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER;
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

	public String getACC_GOVT_SELLER() {
		return ACC_GOVT_SELLER;
	}

	public String getGOVT_SELLER_TNC_PAGE() {
		return GOVT_SELLER_TNC_PAGE;
	}

	public String getGOVT_SELLER_ACCOUNT_INFO_PAGE() {
		return GOVT_SELLER_ACCOUNT_INFO_PAGE;
	}

	public String getGOVT_SELLER_SEARCH_PAGE() {
		return GOVT_SELLER_SEARCH_PAGE;
	}

	public String getGOVT_SELLER_USER_INFO_PAGE() {
		return GOVT_SELLER_USER_INFO_PAGE;
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

	public String getSM_APPL_PAGE_TYPE_VIRTUAL_CERT() {
		return SM_APPL_PAGE_TYPE_VIRTUAL_CERT;
	}
	
	public String getSM_APPL_PAGE_TYPE_TRACKING_DIARY() {
		return SM_APPL_PAGE_TYPE_TRACKING_DIARY;
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

	public String getPM_CERT_BODY_OTHERS() {
		return PM_CERT_BODY_OTHERS;
	}

	public String getSUPPORTING_DOC_ONLINE() {
		return SUPPORTING_DOC_ONLINE;
	}

	public String getSUPPORTING_DOC_OFFLINE() {
		return SUPPORTING_DOC_OFFLINE;
	}

	public String getBPM_TASK_REVIEW_APPL_ESCALATED() {
		return BPM_TASK_REVIEW_APPL_ESCALATED;
	}

	public String getBPM_TASK_REVIEW_APPL() {
		return BPM_TASK_REVIEW_APPL;
	}

	public String getBPM_TASK_REVIEW_APPL_SPLCAT() {
		return BPM_TASK_REVIEW_APPL_SPLCAT;
	}

	public String getBPM_TASK_VERIFY_APPL() {
		return BPM_TASK_VERIFY_APPL;
	}

	public String getBPM_TASK_REVIEW_SITE_VISIT_AFTER_APPROVE() {
		return BPM_TASK_REVIEW_SITE_VISIT_AFTER_APPROVE;
	}

	public double getMIN_AMT_RANGE() {
		return MIN_AMT_RANGE;
	}

	public double getMAX_AMT_RANGE() {
		return MAX_AMT_RANGE;
	}

	public String getSM_APPL_BUTTON_TYPE_PREV() {
		return SM_APPL_BUTTON_TYPE_PREV;
	}

	public String getSM_APPL_BUTTON_TYPE_NEXT() {
		return SM_APPL_BUTTON_TYPE_NEXT;
	}

	public String getSM_APPL_BUTTON_TYPE_SAME() {
		return SM_APPL_BUTTON_TYPE_SAME;
	}

	public Integer getSM_SUPPORTDOCS_FILE_SIZE_LIMIT() {
		return SM_SUPPORTDOCS_FILE_SIZE_LIMIT;
	}

	public String getSupplierBusinessType() {
		return SUPPLIER_BUSINESS_TYPE;
	}

	public int getPENDING_RE_APPROVAL_STATUS() {
		return PENDING_RE_APPROVAL_STATUS;
	}

	public double getMIN_EXP_RANGE() {
		return MIN_EXP_RANGE;
	}

	public double getMAX_EXP_RANGE() {
		return MAX_EXP_RANGE;
	}

	public String getSiteVisitAssignmentTypeAll() {
		return SITE_VISIT_ASSIGNMENT_TYPE_ALL;
	}

	public String getSiteVisitAssignmentTypeBefore() {
		return SITE_VISIT_ASSIGNMENT_TYPE_BEFORE;
	}

	public String getSiteVisitAssignmentTypeAfter() {
		return SITE_VISIT_ASSIGNMENT_TYPE_AFTER;
	}
	
	public long getPmStatusRejectedBySys() {
		return PM_STATUS_REJECTED_BY_SYS;
	}

	public int getMAX_PAYMENT_BANK() {
		return SmRuleHelper.getInstance().getMaxPaymentBankForEachBranch();
	}
	
	public String getMOF_STATUS_ACTIVE() {
		return MOF_STATUS_ACTIVE;
	}
	
	public String getMOF_STATUS_EXPIRED() {
		return MOF_STATUS_EXPIRED;
	}
	public String getMOF_STATUS_NOT_AVAILABLE() {
		return MOF_STATUS_NOT_AVAILABLE;
	}

}
