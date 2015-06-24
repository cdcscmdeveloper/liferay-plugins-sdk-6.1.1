package my.ep.web.scheduler.constants.ct;

/**
 * Constant For CT Scheduler
 * 
 * @author NancyAng
 * @since 24/6/2015
 */
public class CtSchedulerConstants {
	
	public static final String TRIGGER_TIME = "0 01 00 * * ?";

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
	
	// CT　Job Description
	public static final String UPDATE_EXPIRED_CONTRACT_JOB_DESC = "Job run every day at 00:01 AM to expire the contract that no more effective.";
	public static final String UPDATE_EXPIRED_FACTORING_JOB_DESC = "Job run every day at 00:01 AM to expire the factoring that no more effective.";
	public static final String UPDATE_EXPIRED_CIRCULAR_JOB_DESC = "Job run every day at 00:01 AM to expire the circular that no more effective.";
	public static final String UPDATE_SUSPENDED_AGENCY_ITEM_JOB_DESC = "Job run every day at 00:01 AM to activate the suspended agency and item.";
	public static final String CONTRACT_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 AM to send Contract Notification to contract admin and selected approvers.";
	public static final String AGREEMENT_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 AM to send Agreement Completion Notification to contract admin and selected approvers.";
	public static final String PERFORMANCE_BOND_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 AM to send Performance Bond Notification to contract admin and selected approvers.";
	public static final String REMEDY_EXPIRATION_REMINDER_JOB_DESC = "Job run every day at 00:01 AM to expire the send Remedy Notification to contract admin and selected approvers.";

}
