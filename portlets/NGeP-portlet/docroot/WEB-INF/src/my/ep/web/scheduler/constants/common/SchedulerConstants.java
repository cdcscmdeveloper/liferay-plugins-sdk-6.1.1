package my.ep.web.scheduler.constants.common;

/**
 * @author Venkat Chanumolu
 */
public class SchedulerConstants {

	// status constants for Scheduler Manager
	public static final String JOB_RUN = "RUN";
	public static final String JOB_WAIT = "WAIT";
	public static final String JOB_RESUME = "RESUME";
	public static final String JOB_PAUSE = "PAUSE";
	public static final String JOB_NOT_SCHEDULED = "NOT SCHEDULED";

	// Sourcing Module Constants
	// Sourcing Job Group ID
	public static final String SC_JOB_GROUP = "SOURCING JOB GROUP";
	
	//Bidding jobDataMap Constant
	public static final String JOB_BID_ID_LIST = "jobBidIdList";
	// Sourcing Job Key Id
	public static final String SC_CONTINOUS_JOB = "CONTINUOUS BID SCHEDULE STATUS CHECK";
	public static final String SC_DAILY_JOB = "BIDS FETCH DAILY JOB";
	public static final String SC_UPDATE_SUPPLIERS_JOB = "UPDATE SUPPLIERS ";
	public static final String SC_QT_PROPOSAL_VALIDITY_EXPIRY = "QT PROPOSAL VALIDITY EXPIRY";
	public static final String SC_FAILOVER_HANDLER_JOB = "SOURCING FAILOVER HANDLER";
	public static final String SC_QT_PUBLISH_JOB = "PUBLISH APPROVED QT AS SCHEDULED";
	public static final String SC_QT_PROPOSAL_CLOSE_JOB = "CLOSE PROPOSAL AS SCHEDULED";
	public static final String QT_PROPOSAL_CLOSE_SIMPLE_JOB = "QT PROPSAL CLOSE SIMPLE JOB FOR QT:";
	public static final String QT_PROPOSAL_CLOSE_SIMPLE_JOB_GROUP = "QT PROPOSAL CLOSE SIMPLE JOB GROUP";
	public static final String QT_PUBLISH_SIMPLE_JOB = "QT PUBLISH SIMPLE JOB FOR QT:";
	public static final String QT_PUBLISH_SIMPLE_JOB_GROUP = "QT PUBLISH SIMPLE JOB GROUP";
	public static final String QT_PUBLISH_CLOSE_JOB_DATA = "qtId";
	public static final String SC_QUOTATION_PROPOSAL_VALIDITY_REMINDER_JOB = "QUOTATION PROPOSAL VALIDITY REMINDER";
	public static final String SC_TENDER_PROPOSAL_VALIDITY_REMINDER_JOB = "TENDER PROPOSAL VALIDITY REMINDER";
	public static final String SC_UPCOMING_BIDSCHEDULE_REMINDER_JOB = "UPCOMING BIDSESSIONS REMINDER";
	
	// Sourcing Job description
	public static final String SC_DAILY_JOB_DESCRIPTION = "Job run daily at 23:55 to fetch data for eBidding that has been scheduled on the following day";
	public static final String SC_CONTINUOUS_JOB_DESCRIPTION = "Job run every minute to update ebidding status for start, close and discruption bidding";
	public static final String SC_UPDATE_SUPPLIERS_JOB_DESCRIPTION = "Job run 3 times daily at 10:00, 17:00 and 23:55, to check and update qualified supplier data for quotation / tender after published.";
	public static final String SC_PROPOSAL_EXPIRY_JOB_DESCRIPTION = "Job run daily at 00:05 to send reminder when proposal validity period expired";
	public static final String SC_FAILOVER_HANDLER_JOB_DESCRIPTION = "Job run upon server started to handle quotation / tender disruption scenarios";
	public static final String SC_QT_PUBLISH_JOB_DESCRIPTION = "Job run daily at 00:00 to pre-create scheduler for QT data that has been created and scheduled for publishing date at 12:00 BRLN For QT created after 00:00 until 12:00 on the day, system will create a real time scheduler in order to run at 12:00.";
	public static final String SC_QT_PROPOSAL_CLOSE_JOB_DESCRIPTION = "Job run daily at 00:00 to pre-create scheduler for quotation / tender data that has been created on the day and scheduled for closing date at 12:00";
	public static final String SC_QUOTATION_PROPOSAL_VALIDITY_REMINDER_JOB_DESCRIPTION  = "Job run daily 00:05 to send reminder to Desk Officer on quotation proposal validity remaining period (30, 14, 7, 3, 2, 1 days)";
	public static final String SC_TENDER_PROPOSAL_VALIDITY_REMINDER_JOB_DESCRIPTION  = "Job run daily 00:05  to send reminder to Desk Officer on tender proposal validity remaining period (30, 14, 7, 3, 2, 1 days)";
	public static final String SC_UPCOMING_BIDSCHEDULE_REMINDER_JOB_DESCRIPTION = "Job run daily 00:05 to send reminder to supplier who participated in upcoming bid sessions for the day";
	
	// Contracts Module Constants
	// CT Job Group ID
	public static final String CT_JOB_GROUP = "CONTRACT JOB GROUP";
	// CT Job Key Id
	public static final String UPDATE_EXPIRED_CONTRACT = "UPDATE EXPIRED CONTRACT";
	public static final String UPDATE_EXPIRED_FACTORING = "UPDATE EXPIRED FACTORING";
	public static final String UPDATE_EXPIRED_CIRCULAR = "UPDATE EXPIRED CIRCULAR";
	public static final String UPDATE_SUSPENDED_AGENCY_ITEM = "UPDATE SUSPENDED AGENCY AND ITEM";
	public static final String CONTRACT_EXPIRATION_REMINDER = "CONTRACT EXPIRATION REMINDER";
	public static final String AGREEMENT_EXPIRATION_REMINDER = "AGREEMENT COMPLETION EXPIRATION REMINDER";
	public static final String PERFORMANCE_BOND_EXPIRATION_REMINDER = "PERFORMANCE BOND EXPIRATION REMINDER";
	public static final String REMEDY_EXPIRATION_REMINDER = "REMEDY EXPIRATION REMINDER";
	public static final String IMPOSED_SERVICE_TAX_REMINDER = "IMPOSED SERVICE TAX REMINDER";

	// CT Job Description
	public static final String UPDATE_EXPIRED_CONTRACT_JOB_DESC = "Job run every day at 00:01 to inactive the contracts which are expired.";
	public static final String UPDATE_EXPIRED_FACTORING_JOB_DESC = "Job run every day at 00:01 to remove the factorings which are expired.";
	public static final String UPDATE_EXPIRED_CIRCULAR_JOB_DESC = "Job run every day at 00:01 to inactive the circulars which are expired.";
	public static final String UPDATE_SUSPENDED_AGENCY_ITEM_JOB_DESC = "Job run every day at 00:01 to activate the agencies and items which are suspended.";
	public static final String CONTRACT_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 to send the contract reminder before expiry base on contract setting.";
	public static final String AGREEMENT_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 to send the agreement reminder before completion base on contract setting.";
	public static final String PERFORMANCE_BOND_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 to send the performance bond reminder before expiry base on contract setting.";
	public static final String REMEDY_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 to send the remedy reminder before expiry base on contract setting.";
	public static final String IMPOSED_SERVICE_TAX_REMINDER_JOB_DESC = "Job run every day at 00:01 AM to send Imposed Service Tax Notification to contract admin.";

	// PM Module Constants
	// PM Job Group ID
	public static final String PM_INTEGRATION_JOB_GROUP = "PM INTEGRATION JOB GROUP";
	public static final String PM_JOB_GROUP = "PM JOB GROUP";
	// PM Job Key Id
	public static final String PM_INTEGRATION_DAILY_JOB = "PM INTEGRATION JOB";
	public static final String UPDATE_EXPIRED_ACCESS_APPLICATION = "UPDATE EXPIRED ACCESS APPLICATION";
	public static final String SEND_REMINDER_ACCESS_APPLICATION = "SEND ACCESS APPLICATION REMINDER";
	public static final String RESET_CONFIG_RULES_MAP = "RESET CONFIG RULES MAP";
	// PM Integration Job Description
	public static final String PM_INTEGRATION_DAILY_JOB_DESC = "";
	public static final String UPDATE_EXPIRED_ACCESS_APPLICATION_JOB_DESC = "Job run every day at 00:01 AM to expire the access application that no more effective.";
	public static final String SEND_REMINDER_ACCESS_APPLICATION_JOB_DESC = "Job run every day at 00:01 AM to send reminder.";
	public static final String RESET_CONFIG_RULES_JOB_DESC = "Job run every day at 00:01 AM to reset config rules.";

	// SM Module Scheduler COnstants

	// Sm Job Group ID
	public static final String SM_JOB_GROUP = "SM JOB GROUP";

	// Sm Job Key ID
	public static final String SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB = "DELETE PENDING ACTIVATION";
	public static final String SM_APP_SOFTCERT_RENEW_REMINDER_JOB = "Soft Cert Renewal Reminder";
	public static final String SM_APP_PAYMENT_REMINDER = "Payment Reminder";
	public static final String SM_APP_OFFLINE_SUPPORTING_DOC_REMINDER = "Offline Supporting Document Reminder";
	public static final String SM_CHECK_BASIC_ACCOUNT_EXPIRATION_JOB = "Check or Terminate Basic Account Expiration";
	public static final String SM_CHANGE_EXPIRED_MOF_PERSONNEL_ROLE_JOB = "Change MOF Expired Personnel Role";
	public static final String SM_COMPLAINT_SHOWCAUSE_NOTIFICATION = "Complaint ShowCause Notification";
	public static final String SM_COMPLAINT_ACCUSATION_LETTER_NOTIFICATION = "Complaint Accusation Letter Notification";
	public static final String SM_MOF_RENEWAL_REMINDER = "MOF RENEWAL REMINDER";

	// Sm Job Description
	public static final String SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB_DESC = "Job run every day at 23:55 to delete pending activation personnel which expire after 7 days";
	public static final String SM_PAYMENT_REMINDER_JOB_DESC = "Job run every day at 03:00 to send the payment reminder through email notification on 14 and 7 days before expiry since payment notification is sent 60 days before expiry";
	public static final String SM_COMPLAINT_SHOWCAUSE_NOTIFICATION_JOB_DESC = "Job run every day at 02:00 to send the show cause notification which has not been submitted after 21 days";
	public static final String SM_CHANGE_EXPIRED_MOF_PERSONNEL_ROLE_JOB_DESC = "Job run daily at 00:00 to downgrade MOF Account to Basic Account after expired";
	public static final String SM_CHECK_BASIC_ACCOUNT_EXPIRATION_JOB_DESC = "Job run every day at 01:00 to terminate MOF Accounts and deactivate Basic Accounts which are not logged in after 1 year";
	public static final String SM_APP_SOFTCERT_RENEW_REMINDER_JOB_DESC = "Job run every day at 02:30 to send the soft cert renewal reminder 3 months before expiry.";
	public static final String SM_APP_OFFLINE_SUPPORTING_DOC_REMINDER_DESC = "Job run every day at 03:30 to send offline supporting document reminder 120 days before expiry.";
	public static final String SM_COMPLAINT_ACCUSATION_LETTER_NOTIFICATION_JOB_DESC = "Job run every day at 02:00 to send the accusation notification which has not been submitted after 21 days";	
		
	public static final String SM_MOF_RENEWAL_REMINDER_JOB_DESC = "Job run every day at 4 AM to send the remind MOF supplier to renew MOF status";
	
	// Integration
	public static final String PHIS_INTEGRATION_JOB_GROUP = "PHIS INTEGRATION JOB GROUP";
	public static final String EPERUNDING_INTEGRATION_JOB_GROUP = "EPERUNDING INTEGRATION JOB GROUP";
	public static final String GFMAS_INTEGRATION_JOB_GROUP = "1GFMAS INTEGRATION JOB GROUP";
	public static final String SPA_INTEGRATION_JOB_GROUP = "SPA INTEGRATION JOB GROUP";
	public static final String GFMAS_AUTO_INTEGRATION_JOB_GROUP = "GFMAS AUTOMIC INTEGRATION JOB GROUP";

	// EPERUNDING OUT BOUND
	public static final String EPERUNDING_CONSULTANT_PROFILE_JOB = "CONSULTANT PROFILE JOB";
	public static final String EPERUNDING_ASSOCIATED_COMPANY_JOB = "ASSOCIATED COMPANY JOB";
	public static final String EPERUNDING_COMPANY_OWNER_JOB = "COMPANY OWNER JOB";
	public static final String EPERUNDING_EQUITY_OWNER_JOB = "EQUITY OWNER JOB";
	public static final String EPERUNDING_BOARD_DIRECTOR_JOB = "BOARD OF DIRECTOR JOB";
	public static final String EPERUNDING_MANAGEMENT_PERSONNEL_JOB = "MANAGEMENT PERSONNEL JOB";
	public static final String EPERUNDING_AUTHORIZED_SIGNATORY_JOB = "AUTHORIZED SIGNATORY JOB";
	public static final String EPERUNDING_BRANCH_COMPANY_JOB = "BRANCH_COMPANY JOB";
	public static final String EPERUNDING_REGISTERED_CATEGORY_JOB = "REGISTERED CATEGORY JOB";
	public static final String EPERUNDING_GOVERNMENT_PROJECT_JOB = "GOVERNMENT PROJECT JOB";
	public static final String EPERUNDING_DELETED_COMPANY_JOB = "DELETED COMPANY JOB";

	// PHIS OUT BOUND
	public static final String PHIS_VOT_FUND_JOB = "MASTER DATA VOT JOB";
	public static final String PHIS_VOT_FUND_DANA_JOB = "MASTER DATA DANA JOB";
	public static final String PHIS_PROGRAM_JOB = "MASTER DATA PROGRAM / AKTIVITI JOB";
	public static final String PHIS_PROJECT_JOB = "MASTER DATA – PROJEK / SETIA / SUB SETIA / CP JOB";
	public static final String PHIS_KOD_JOB = "MASTER DATA KOD AKAUN JOB";
	public static final String PHIS_DELIVERY_ADDRESS_JOB = "MASTER DATA DELIVERY ADDRESS JOB";
	public static final String PHIS_PAY_EFF_NOTIFY_JOB = "PAYMENT EFFECT NOTIFICATION JOB";
	public static final String PHIS_MASTER_DATA_MATERIAL = "MASTER DATA – MATERIAL INFORMATION JOB";
	public static final String PHIS_MASTER_DATA = "MATERIAL MASTER DATA JOB";
	
	//SPA OUT BOUND
	
	public static final String SPA_MASTER_DATA_MATERIAL_JOB = "MATERIAL MASTER DATA JOB";
	public static final String SPA_MASTER_DATA_SEGMENT_JOB = "MATERIAL MASTER DATA SEGMENT JOB";
	public static final String SPA_MASTER_DATA_FAMILY_JOB = "MATERIAL MASTER DATA FAMILY JOB";
	public static final String SPA_MASTER_DATA_CLASS_JOB = "MATERIAL MASTER DATA CLASS JOB";
	public static final String SPA_MASTER_DATA_COMMODITY_JOB = "MATERIAL MASTER DATA COMMODITY JOB";
	public static final String SPA_MASTER_DATA_COLOR_JOB = "MATERIAL MASTER DATA COLOR JOB";
	public static final String SPA_MASTER_DATA_BRAND_JOB = "MATERIAL MASTER DATA BRAND JOB";
	public static final String SPA_MASTER_DATA_MEASUREMENT_JOB = "MATERIAL MASTER DATA MEASUREMENT JOB";
	public static final String SPA_MASTER_DATA_TYPE_JOB = "MATERIAL MASTER DATA TYPE JOB";
	
	///GFMAS AUTOMIC JON
	
	public static final String GFMAS_KPTJ_JOB = "GFMAS KPTJ JOB";
	public static final String GFMAS_PP_JOB = "GFMAS PP JOB";
	public static final String GFMAS_BANK_JOB = "GFMAS BANK JOB";
	public static final String GFMAS_AGOFF_JOB = "GFMAS AG OFFICE JOB";
	public static final String GFMAS_GLACC_JOB = "GFMAS GL ACCOUNT JOB";
	public static final String GFMAS_PTJ_JOB = "GFMAS PTJ JOB";
	public static final String GFMAS_VOT_JOB = "GFMAS VOT JOB";
	public static final String GFMAS_DANA_JOB = "GFMAS DANA JOB";
	public static final String GFMAS_PROG_ACT_JOB = "GFMAS PROGRAM ACT JOB";
	public static final String GFMAS_PROJECT_JOB = "GFMAS PROJECT JOB";

	// 1GFMAS OUT BOUND

	public static final String GFMAS_SST_INFORMATION = "SST INFORMATION JOB";
	public static final String GFMAS_VENDOR_OUTBOUND = "MASTER DATA VENDOR JOB(Outbound)";
	public static final String GFMAS_VENDOR_INBOUND = "MASTER DATA VENDOR JOB(Inbound)";
	public static final String GFMAS_MASTER_DATA_MATERIAL = "MASTER DATA MATERIAL INFORMATION JOB";
	public static final String GFMAS_RECEIPT_INFORMATION = "RECEIPT INFORMATION JOB";
	public static final String GFMAS_ERR_RECEIPT_INFORMATION = "RECEIPT INFORMATION ERROR JOB";

	// 1GFMAS IN BOUND

	public static final String GFMAS_MASTER_DATA_INBOUND = "MASTER DATA VOT, DANA, PROGRAM AKTIVITI, PROJEK, KOD AKAUN, KPTJ, PTJ JOB (Inbound)";
	public static final String GFMAS_INBOUND_RETRY = "RETRY MASTER DATA JOB(Inbound)";
	public static final String GFMAS_MASTER_DATA_ERROR = "MASTER DATA ERROR FILE JOB(Inbound)";
	public static final String GFMAS_PAYMENT_INFORMATION_INBOUND = "PAYMENT INFORMATION JOB(Inbound)";

	// PHIS OUT BOUND
	
	public static final String EPERUNDING_DELETE_COMP_JOB_DESC = "Job run every day at 01:00 to list of deleted consultants to ePerunding";
	public static final String EPERUNDING_INTEGRATION_JOB_DESC = "Job run every day at 01:00 to send eperunding data as below: BRLN 1) Consultant's profile BRLN 2) Associated Company BRLN 3) Company Owner BRLN 4) Equity Owner BRLN 5) Board of Directors BRLN 6) Management Personnel BRLN 7) Signatory BRLN 8) Branch Company BRLN 9) Registered Category BRLN 10) Government Project";
	public static final String PHIS_PM_VOT_FUND_JOB_DESC = "Job run every 5 minutes to send Master Data VOT to PHIS.";
	public static final String PHIS_PM_DANA_JOB_DESC = "Job run every 5 minutes to send Master Data Dana to PHIS.";
	public static final String PHIS_PM_PROGRAM_JOB_DESC = "Job run every 5 minutes to send Master Data Program / Aktiviti to PHIS.";
	public static final String PHIS_PM_KOD_JOB_DESC = "Job run every 5 minutes to send Master Data Kod Akaun to PHIS.";
	public static final String PHIS_DELIVERY_ADDRESS_JOB_DESC = "Job run every 5 minutes to send Master Data Delivery Address to PHIS.";
	public static final String PHIS_CM_JOB_DESC = "Job run twice daily 9 AM and 9 PM";
	public static final String PHIS_FL_JOB_DESC = "Job runs every 1 hour";
	public static final String PHIS_MASTERDATA_MATERIAL_JOB_DESC = "Job runs every 1 hour";
	public static final String GFMAS_RETRY_IN_JOB_DESC = "Job run once daily 2 AM";
	public static final String GFMAS_PM_IN_JOB_DESC = "Job run every 5 minutes to send Master Data Material Information to PHIS.";
	public static final String GFMAS_MASTER_DATA_JOB_DESC = "Job run every 5 minutes to retrieve 1GFMAS Master Data from 1GFMAS as below: BRLN 1) GLVOT BRLN 2) GLDNA BRLN 3) GLPRG BRLN 4) GLPRJ BRLN 5) GLGLC BRLN 6) GLPCG BRLN 7) GLSEG BRLN 8) GLPTJ BRLN 9) GLBAC BRLN 10) CMBNK";
	public static final String GFMAS_PM_ERROR_JOB_DESC = "Job run every 5 minutes to send Master Data Error File.";
	public static final String GFMAS_SM_IN_JOB_DESC = "Job run every 3 minutes to retrieve 1GFMAS Master Data Vendor APOVE  file  from 1GFMAS";
	public static final String GFMAS_SC_OUT_JOB_DESC = "Job run every day at 09:00 and 21:00 to send Master Data Surat Setuju Terima AP516 to 1GFMAS";
	public static final String GFMAS_FL_IN_JOB_DESC = "Job runs every 1 hour";
	public static final String GFMAS_SM_OUT_JOB_DESC = "Job run every 3 minute";
	public static final String GFMAS_SM_RECEIPT_OUT_JOB_DESC = "Job run once daily 12:15 AM";
	public static final String GFMAS_SM_RECEIPT_IN_JOB_DESC = "Job run once daily 1 AM";
	public static final String GFMAS_MASTERDATA_MATERIAL_JOB_DESC = "Job run every 3 minutes to send Master Data Material Information MMINF to 1GFMAS";
	public static final String SPA_JOB_DESC = "Job run once daily at 00:00 to send Master Data Masterial Information file";
	public static final String SPA_JOB_DAILY_DESC = "Job run once daily after 12 AM";
	public static final String SPA_JOB_MEASUREMENT_DESC = "Job run once daily after 00:10 to send Master Data Measurement file";
	public static final String SPA_JOB_COMMODITY_DESC = "Job run once daily at 00:05 to send Master Data Commodity file";
	public static final String SPA_JOB_FAMILY_DESC = "Job run once daily at 00:00 to send Master Data Family file";
	public static final String SPA_JOB_TYPE_DESC = "Job run once daily at 00:10 to send Master Data Type file";
	public static final String SPA_JOB_COLOR_DESC = "Job run once daily at 00:05 to send Master Data Color file";
	public static final String SPA_JOB_BRAND_DESC = "Job run once daily at 00:10 to send Master Data Brand file";
	public static final String SPA_JOB_CLASS_DESC = "Job run once daily at 00:05 to send Master Data Class file";
	public static final String SPA_JOB_SEGMENT_DESC = "Job run once daily after 00:00 to send Master Data Segment file";
	
	
	///GFMAS AUTO
	public static final String GFMAS_KPTJ_INTEGRATION_JOB_DESC = "Job run every 5 minutes to process 1GFMAS GLPCG Files";
	public static final String GFMAS_VOT_INTEGRATION_JOB_DESC = "Job run every 5 minutes to process 1GFMAS GLVOT Files";
	public static final String GFMAS_BANK_INTEGRATION_JOB_DESC = "Job run every 5 minutes to process 1GFMAS CMBNK Files";
	public static final String GFMAS_AGOFF_INTEGRATION_JOB_DESC = "Job run every 5 minutes to process 1GFMAS GLBAC Files";
	public static final String GFMAS_PROJECT_INTEGRATION_JOB_DESC = "Job run every 5 minutes to process 1GFMAS GLPRJ Files";
	public static final String GFMAS_DANA_INTEGRATION_JOB_DESC = "Job run every 5 minutes to process 1GFMAS GLGLC Files";
	public static final String GFMAS_GLACC_INTEGRATION_JOB_DESC = "Job run every 5 minutes to process 1GFMAS GLGLC Files";
	public static final String GFMAS_PRGACT_INTEGRATION_JOB_DESC = "Job run every 5 minutes to process 1GFMAS GLPRG Files";
	public static final String GFMAS_PTJ_INTEGRATION_JOB_DESC = "Job run every 5 minutes to process 1GFMAS GLPTJ Files";
	public static final String GFMAS_PP_INTEGRATION_JOB_DESC = "Job run every 5 minutes to process 1GFMAS GLSEG Files";

	// CRM Job Group ID
	public static final String CRM_INTEGRATION_JOB_GROUP = "CRM INTEGRATION JOB GROUP";

	// CRM Job Key ID
	public static final String UPDATE_GOVERNMENT_USER_INFO_JOB = "UPDATE GOVERNMENT USER INFO CRM OUTBOUND JOB";
	public static final String UPDATE_SUPPLIER_USER_INFO_JOB = "UPDATE SUPPLIER USER INFO CRM OUTBOUND JOB";
	public static final String UPDATE_SUPPLIER_INFO_JOB = "UPDATE SUPPLIER INFO CRM OUTBOUND JOB";
	public static final String UPDATE_GOVERNMENT_INFO_JOB = "UPDATE GOVERNMENT INFO CRM OUTBOUND JOB";

	// CRM Job Description
	public static final String UPDATE_GOVERNMENT_USER_INFO_JOB_DESCRIPTION = "Job to run every 5 minutes";
	public static final String UPDATE_SUPPLIER_USER_INFO_JOB_DESCRIPTION = "Job to run every 5 minutes";
	public static final String UPDATE_SUPPLIER_INFO_JOB_DESCRIPTION = "Job to run every 5 minutes";
	public static final String UPDATE_GOVERNMENT_INFO_JOB_DESCRIPTION = "Job to run every 5 minutes";

	// EPOL Job Group ID
	public static final String EPOL_INTEGRATION_JOB_GROUP = "EPOL INTEGRATION JOB GROUP";

	// EPOL Job Key ID
	public static final String EPOL_GOVERNMENT_ENTITY_INFO_JOB = "GOVERNMENT ENTITY INFO JOB (Outbound)";
	public static final String EPOL_GOVERNMENT_USER_INFO_JOB = "GOVERNMENT USER INFO JOB (Outbound)";
	public static final String EPOL_COMPANY_INFO_JOB = "COMPANY INFO JOB (Outbound)";
	public static final String EPOL_CONTACT_INFO_JOB = "CONTACT INFO JOB (Outbound)";
	public static final String EPOL_SUPPLIER_USER_INFO_JOB = "SUPPLIER USER INFO JOB (Outbound)";

	// EPOL Job Description
	public static final String EPOL_GOVERNMENT_ENTITY_INFO_JOB_DESCRIPTION = "Job run once daily after 1 AM";
	public static final String EPOL_GOVERNMENT_USER_INFO_JOB_DESCRIPTION = "Job run once daily after 1 AM";
	public static final String EPOL_COMPANY_INFO_JOB_DESCRIPTION = "Job run once daily after 1 AM";
	public static final String EPOL_CONTACT_INFO_JOB_DESCRIPTION = "Job run once daily after 1 AM";
	public static final String EPOL_SUPPLIER_USER_INFO_JOB_DESCRIPTION = "Job run once daily after 1 AM";
	//TODO need to delete obsolete constants
	public static final String NOTIFICATION_INTEGRATION_CONTINUOUS_JOB = "NOTIFICATION JOB GROUP";
	public static final String NOTIFICATION_INTEGRATION_JOB_GROUP = "NOTIFICATION INTEGRATION JOB";
	public static final String NOTIFICATION_INTEGRATION_CONTINUOUS_JOB_DESC = "Job run 10 AM and 10 PM";
	
	// CM Related jobs
	public static final String CM_EXPIRED_ITEM_TIMER_JOB = "ITEM EXPIRED JOB";
	public static final String CM_EXPIRED_ITEM_TIMER_JOB_GROUP = "CM JOB GROUP";
	public static final String CM_EXPIRED_ITEM_TIMER_CONTINUOUS_JOB_DESC = "Job run at 00:00 daily to remove the supplier catalogue items which are expired on the day before.";
	public static final String CM_DROP_SEQUENCE_JOB_DESC = "Job run at 00:00 daily to generate sequence number for  prefix in codification document number.";
	
	public static final String CM_DROP_DOC_SEQ_JOB_GROUP = "CM JOB GROUP";
	public static final String CM_DROP_SEQUENCE_JOB = "DROP SEQUENCE JOB";


	// NOTIFICATIONS
	public static final String NTF_JOB_GROUP = "NOTIFICATION JOB GROUP";
	public static final String NTF_MESSAGE_BULK_INTERNAL_JOB = "NTF BULK MESSAGE JOB";
	public static final String NTF_MESSAGE_BULK_JOB_DESC = "Job run every 3 minutes to send portal inbox and sms notification for Sourcing module - Supplier invitation";

	public static final String NTF_MESSAGE_REAL_TIME_INTERNAL_JOB = "NTF REAL TIME MESSAGE JOB";
	public static final String NTF_MESSAGE_REAL_TIME_JOB_DESC = "Job run every 2 minutes to send portal inbox and sms notification for user already exist in NextGen system ";

	public static final String NTF_MESSAGE_DIRECT_EMAIL_INTERNAL_JOB = "NTF DIRECT EMAIL MESSAGE JOB";
	public static final String NTF_MESSAGE_DIRECT_EMAIL_JOB_DESC = "Job run every 4 minutes to send email notification for user which not exist in NextGen system";

	public static final String NTF_EMAIL_BULK_INTERNAL_JOB = "NTF BULK EMAIL JOB";
	public static final String NTF_EMAIL_BULK_JOB_DESC = "Job run every 20 minutes to send email notification for Sourcing module - Supplier invitation";

	public static final String NTF_EMAIL_REAL_TIME_INTERNAL_JOB = "NTF REAL TIME  EMAIL JOB";
	public static final String NTF_EMAIL_REAL_TIME_JOB_DESC = "Job run every 5 minutes to send email notification for user already exist in NextGen system ";
}
