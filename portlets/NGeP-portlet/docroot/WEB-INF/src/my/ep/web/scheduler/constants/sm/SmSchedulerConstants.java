package my.ep.web.scheduler.constants.sm;

public class SmSchedulerConstants {

	// Sm Job Group ID
	public static final String SM_JOB_GROUP = "SM JOB GROUP";

	// Sm Job Key ID
	public static final String SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB = "Delete or deactivate pending activation personnel";
	public static final String SM_APP_SOFTCERT_RENEW_REMINDER_JOB = "Soft Cert Renewal Reminder";
	public static final String SM_APP_PAYMENT_REMINDER = "Payment Reminder";
	
	// Sm Job Description
	public static final String SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB_DESC = "Job run every day at 00:05 AM to delete pending activation personnel";
	public static final String SM_APP_SOFTCERT_RENEW_REMINDER_JOB_DESC = "Job run every day at 02:30 AM to send the soft cert renew reminder.";
	public static final String SM_PAYMENT_REMINDER_JOB_DESC = "Job run every day at 03:00 AM to send the payment reminder.";
	public static final String SM_APP_OFFLINE_SUPPORTING_DOC_REMINDER_DESC = "Job run every day at 03:30 AM to send the payment reminder.";
	
	//Payment Reminder
	public static final int PAYMENT_REMINDER_DAY = 3;
	public static final Long STATUS_PROCESSING_PAYMENT = 20114L;
	public static final Long STATUS_APPLICATION_PAYMENT = 20251L;
	
	//Soft Cert Renewal Reminder
	public static final String CERT_MODE_NEW = "N";
	public static final String CERT_MODE_RENEW = "R";
	public static final int SOFTCERT_RENEWAL_DAY = 7;
	
	//Timer Setting
	public static final String SM_DAILY_SOFTCERT_RENEWAL_TIMER = "O0 30 2 * * ?";
	public static final String SM_DAILY_DEL_PEND_ACTV_TIMER = "0 55 23 * * ?";
	public static final String SM_DAILY_PAYMENT_REMINDER_TIMER = "0 00 3 * * ?";
	public static final String SM_DAILY_SUPPORTING_DOC_REMINDER_TIMER = "0 30 3 * * ?";
	
}
