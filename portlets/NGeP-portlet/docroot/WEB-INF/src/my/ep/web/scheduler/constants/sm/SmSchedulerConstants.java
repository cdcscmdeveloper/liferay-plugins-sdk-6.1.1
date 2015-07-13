package my.ep.web.scheduler.constants.sm;

public class SmSchedulerConstants {

	// Sm Job Group ID
	public static final String SM_JOB_GROUP = "SM JOB GROUP";
	public static final String SM_INTEGRATION_JOB_GROUP = "SM INTEGRATION JOB GROUP";

	// Sm Job Key ID
	public static final String SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB = "DELETE PENDING ACTIVATION PERSONNEL";
	public static final String SM_INTEGRATION_JOB = "SM INTEGRATION JOB";
	public static final String SM_APP_SOFTCERT_RENEW_REMINDER_JOB = "Soft Cert Renewal Reminder";
	
	// Sm Job Description
	public static final String SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB_DESC = "Job run every day at 11:59 PM to delete pending activation personnel";
	public static final String SM_INTEGRATION_JOB_DESC = "";
	public static final String SM_APP_SOFTCERT_RENEW_REMINDER_JOB_DESC = "Job run every day at 02:30 AM to send the soft cert renew reminder.";
	
	//Payment Reminder
	public static final int PAYMENT_REMINDER_DAY = 3;
	
	//Soft Cert Renewal Reminder
	public static final String CERT_MODE_NEW = "N";
	public static final String CERT_MODE_RENEW = "R";
	public static final int SOFTCERT_RENEWAL_DAY = 7;
	
	//Timer Setting
	public static final String SOFTCERT_RENEWAL_TIMER = "O0 30 2 * * ?";
	
}
