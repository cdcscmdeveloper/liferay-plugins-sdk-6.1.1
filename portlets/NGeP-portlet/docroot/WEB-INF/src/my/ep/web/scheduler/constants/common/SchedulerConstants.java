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

	// Sourcing Job description
	public static final String SC_DAILY_JOB_DESCRIPTION = "Job to run daily at 11:55 PM";
	public static final String SC_CONTINUOUS_JOB_DESCRIPTION = "Job to run every 60 Seconds";
	public static final String SC_UPDATE_SUPPLIERS_JOB_DESCRIPTION = "Job to run daily at 11:55 PM to update qualified/disqualified suppliers";
	public static final String SC_PROPOSAL_EXPIRY_JOB_DESCRIPTION = "Job to run daily at 12:05 AM";
	public static final String SC_QT_PUBLISH_JOB_DESCRIPTION = "Job to run daily at 12:00 am to publish Qt's";
	public static final String SC_QT_PROPOSAL_CLOSE_JOB_DESCRIPTION = "Job to run daily at 12:00 am to close proposal";

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
	public static final String UPDATE_EXPIRED_CONTRACT_JOB_DESC = "Job run every day at 00:01 AM to expire the contract that no more effective.";
	public static final String UPDATE_EXPIRED_FACTORING_JOB_DESC = "Job run every day at 00:01 AM to expire the factoring that no more effective.";
	public static final String UPDATE_EXPIRED_CIRCULAR_JOB_DESC = "Job run every day at 00:01 AM to expire the circular that no more effective.";
	public static final String UPDATE_SUSPENDED_AGENCY_ITEM_JOB_DESC = "Job run every day at 00:01 AM to activate the suspended agency and item.";
	public static final String CONTRACT_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 AM to send Contract Notification to contract admin and selected approvers.";
	public static final String AGREEMENT_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 AM to send Agreement Completion Notification to contract admin and selected approvers.";
	public static final String PERFORMANCE_BOND_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 AM to send Performance Bond Notification to contract admin and selected approvers.";
	public static final String REMEDY_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 AM to send Remedy Notification to contract admin and selected approvers.";
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

	// Sm Job Description
	public static final String SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB_DESC = "Job run every day at 11:55 PM to delete pending activation personnel";
	public static final String SM_APP_SOFTCERT_RENEW_REMINDER_JOB_DESC = "Job run every day at 02:30 AM to send the soft cert renew reminder.";
	public static final String SM_PAYMENT_REMINDER_JOB_DESC = "Job run every day at 03:00 AM to send the payment reminder.";
	public static final String SM_APP_OFFLINE_SUPPORTING_DOC_REMINDER_DESC = "Job run every day at 03:30 AM to send offline supporting document reminder.";
	public static final String SM_CHECK_BASIC_ACCOUNT_EXPIRATION_JOB_DESC = "Job run every day at 1 AM to check or deactivate Basic Account";
	public static final String SM_CHANGE_EXPIRED_MOF_PERSONNEL_ROLE_JOB_DESC = "Job run every day at 03:00 AM to Change MOF Expired Personnel Role";
	
	// Payment Reminder
	public static final int PAYMENT_REMINDER_DAY = 3;
	public static final Long STATUS_PROCESSING_PAYMENT = 20114L;
	public static final Long STATUS_APPLICATION_PAYMENT = 20251L;

	// Soft Cert Renewal Reminder
	public static final String CERT_MODE_NEW = "N";
	public static final String CERT_MODE_RENEW = "R";
	public static final int SOFTCERT_RENEWAL_DAY = 7;

	// Integration
	public static final String PHIS_INTEGRATION_JOB_GROUP = "PHIS INTEGRATION JOB GROUP";
	public static final String EPERUNDING_INTEGRATION_JOB_GROUP = "EPERUNDING INTEGRATION JOB GROUP";
	public static final String GFMAS_INTEGRATION_JOB_GROUP = "1GFMAS INTEGRATION JOB GROUP";

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
	public static final String EPERUNDING_DELETED_COMAPNY_JOB = "DELETED COMAPNY JOB";

	// PHIS OUT BOUND
	public static final String PHIS_VOT_FUND_JOB = "MASTER DATA VOT JOB";
	public static final String PHIS_VOT_FUND_DANA_JOB = "MASTER DATA VOT DANA JOB";
	public static final String PHIS_PROGRAM_JOB = "MASTER DATA PROGRAM JOB";
	public static final String PHIS_PROJECT_JOB = "MASTER DATA PROJECT JOB";
	public static final String PHIS_KOD_JOB = "MASTER DATA KOD AKAUN JOB";
	public static final String PHIS_DELIVERY_ADDRESS_JOB = "MASTER DATA DELIVERY ADDRESS JOB";
	public static final String PHIS_PAY_EFF_NOTIFY_JOB = "PAYMENT EFFECT NOTIFICATION JOB";
	public static final String PHIS_MASTER_DATA_MATERIAL = "MATERIAL MASTER DATA JOB";
	public static final String PHIS_MASTER_DATA = "MATERIAL MASTER DATA JOB";

	// 1GFMAS OUT BOUND

	public static final String GFMAS_SST_INFORMATION = "SST INFORMATION JOB";
	public static final String GFMAS_VENDOR_OUTBOUND = "MASTER DATA VENDOR JOB(Outbound)";
	public static final String GFMAS_VENDOR_INBOUND = "MASTER DATA VENDOR JOB(Inbound)";
	public static final String GFMAS_MASTER_DATA_MATERIAL = "MATERIAL MASTER DATA JOB";
	public static final String GFMAS_RECEIPT_INFORMATION = "RECEIPT INFORMATION JOB";
	public static final String GFMAS_ERR_RECEIPT_INFORMATION = "ERROR RECEIPT INFORMATION JOB";

	// 1GFMAS IN BOUND

	public static final String GFMAS_MASTER_DATA_INBOUND = "MASTER GL DATA JOB(Inbound)";
	public static final String GFMAS_INBOUND_RETRY = "RETRY MASTER DATA JOB(Inbound)";
	public static final String GFMAS_MASTER_DATA_ERROR = "MASTER DATA ERROR FILE JOB(Inbound)";
	public static final String GFMAS_PAYMENT_INFORMATION_INBOUND = "PAYMENT INFORMATION JOB(Inbound)";

	// PHIS OUT BOUND

	public static final String EPERUNDING_INTEGRATION_JOB_DESC = "Job run once daily after 1 AM";
	public static final String PHIS_PM_JOB_DESC = "Job run every 5 minutes";
	public static final String PHIS_DELIVERY_ADDRESS_JOB_DESC = "Job runs every 1 hour";
	public static final String PHIS_CM_JOB_DESC = "Job run twice daily 9 AM and 9 PM";
	public static final String PHIS_FL_JOB_DESC = "Job runs every 1 hour";
	public static final String PHIS_MASTERDATA_MATERIAL_JOB_DESC = "Job run twice daily 8 AM and 8 PM";
	public static final String GFMAS_RETRY_IN_JOB_DESC = "Job run once daily 2 AM";
	public static final String GFMAS_PM_IN_JOB_DESC = "Job run every 5 minutes";
	public static final String GFMAS_SM_IN_JOB_DESC = "Job run every 5 minute";
	public static final String GFMAS_SC_OUT_JOB_DESC = "Job run once daily 8 AM";
	public static final String GFMAS_FL_IN_JOB_DESC = "Job runs every 1 hour";
	public static final String GFMAS_SM_OUT_JOB_DESC = "Job run every 5 minute";
	public static final String GFMAS_SM_RECEIPT_OUT_JOB_DESC = "Job run once daily 6 AM";
	public static final String GFMAS_SM_RECEIPT_IN_JOB_DESC = "Job run once daily 7 AM";
	public static final String GFMAS_MASTERDATA_MATERIAL_JOB_DESC = "Job run twice daily 9 AM and 9 PM";

	// CRM Job Group ID
	public static final String CRM_INTEGRATION_JOB_GROUP = "CRM INTEGRATION JOB GROUP";

	// CRM Job Key Id
	public static final String UPDATE_GOVERNMENT_USER_INFO_JOB = "UPDATE GOVERNMENT USER INFO CRM OUTBOUND JOB";
	public static final String UPDATE_SUPPLIER_USER_INFO_JOB = "UPDATE SUPPLIER USER INFO CRM OUTBOUND JOB";
	public static final String UPDATE_SUPPLIER_INFO_JOB = "UPDATE SUPPLIER INFO CRM OUTBOUND JOB";
	public static final String UPDATE_GOVERNMENT_INFO_JOB = "UPDATE GOVERNMENT INFO CRM OUTBOUND JOB";

	// Sourcing Job description
	public static final String UPDATE_GOVERNMENT_USER_INFO_JOB_DESCRIPTION = "Job to run every 5 minutes";
	public static final String UPDATE_SUPPLIER_USER_INFO_JOB_DESCRIPTION = "Job to run every 5 minutes";
	public static final String UPDATE_SUPPLIER_INFO_JOB_DESCRIPTION = "Job to run every 5 minutes";
	public static final String UPDATE_GOVERNMENT_INFO_JOB_DESCRIPTION = "Job to run every 5 minutes";

	// NOTIFICATIONS
	public static final String NOTIFICATION_INTEGRATION_CONTINUOUS_JOB = "NOTIFICATION JOB GROUP";
	public static final String NOTIFICATION_INTEGRATION_JOB_GROUP = "NOTIFICATION INTEGRATION JOB";
	public static final String NOTIFICATION_INTEGRATION_CONTINUOUS_JOB_DESC = "Job run 10 AM and 10 PM";
	public static final String CM_EXPIRED_ITEM_TIMER_JOB = "ITEM EXPIRED JOB";
	public static final String CM_EXPIRED_ITEM_TIMER_JOB_GROUP = "CM JOB GROUP";
	public static final String CM_EXPIRED_ITEM_TIMER_CONTINUOUS_JOB_DESC = "Job run 12 AM";
	public static final String CM_DROP_DOC_SEQ_JOB_GROUP = "DROP DOC SEQUENCE JOB";

	public static final String NTF_MESSAGE_BULK_INTERNAL_JOB = "NTF BULK MESSAGE JOB";
	public static final String NTF_MESSAGE_BULK_JOB_GROUP = "NTF BULK MESSAGE JOB GROUP";
	public static final String NTF_MESSAGE_BULK_JOB_DESC = "Ntf Bulk Job run every 3 minutes";

	public static final String NTF_MESSAGE_REAL_TIME_INTERNAL_JOB = "NTF REAL TIME MESSAGE JOB";
	public static final String NTF_MESSAGE_REAL_TIME_JOB_GROUP = "NTF REAL TIME MESSAGE JOB GROUP";
	public static final String NTF_MESSAGE_REAL_TIME_JOB_DESC = "Ntf Real Time Message Job run every 2 minutes";

	public static final String NTF_MESSAGE_DIRECT_EMAIL_INTERNAL_JOB = "NTF DIRECT EMAIL MESSAGE JOB";
	public static final String NTF_MESSAGE_DIRECT_EMAIL_JOB_GROUP = "NTF DIRECT EMAIL JOB GROUP";
	public static final String NTF_MESSAGE_DIRECT_EMAIL_JOB_DESC = "Ntf Direct Email Job run every 4 minute";

	public static final String NTF_EMAIL_BULK_INTERNAL_JOB = "NTF BULK EMAIL JOB";
	public static final String NTF_EMAIL_BULK_JOB_GROUP = "NTF BULK EMAIL JOB GROUP";
	public static final String NTF_EMAIL_BULK_JOB_DESC = "Ntf Bulk Email Job run every 20 minutes";

	public static final String NTF_EMAIL_REAL_TIME_INTERNAL_JOB = "NTF REAL TIME  EMAIL JOB";
	public static final String NTF_EMAIL_REAL_TIME_JOB_GROUP = "NTF REAL TIME EMAIL JOB GROUP";
	public static final String NTF_EMAIL_REAL_TIME_JOB_DESC = "Real Time Email Job run every 5 minutes";
}
