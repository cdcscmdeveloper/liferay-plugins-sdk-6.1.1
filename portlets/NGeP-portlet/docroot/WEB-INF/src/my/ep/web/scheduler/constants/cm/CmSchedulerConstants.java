package my.ep.web.scheduler.constants.cm;

/**
 * @author Venkat Chanumolu
 */
public class CmSchedulerConstants {

	
	//EPERUNDING OUT BOUND
	public static final String EPERUNDING_INTEGRATION_JOB_GROUP = "EPERUNDING JOB GROUP";	
	public static final String EPERUNDING_INTEGRATION_CONTINUOUS_JOB = "EPERUNDING INTEGRATION JOB";	
	public static final String EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC = "Job run once daily 1 AM";
	
	//PHIS OUT BOUND
	//PM
	public static final String PHIS_PM_OUTBOUND_JOB_GROUP="PHIS JOB GROUP";
	public static final String PHIS_PM_OUTBOUND_CONTINUOUS_JOB ="PHIS OUTBOUND PM JOB";
	public static final String PHIS_PM_CONTINUOUS_JOB_DESC ="Job run every 5 minutes";
	//SM
	public static final String PHIS_SM_OUTBOUND_JOB_GROUP ="PHIS JOB GROUP";
	public static final String PHIS_SM_OUTBOUND_CONTINUOUS_JOB ="PHIS OUTBOUND SM JOB";
	public static final String PHIS_SM_CONTINUOUS_JOB_DESC ="Job run twice daily 9 AM and 9 PM";
	//CM
	public static final String PHIS_CM_OUTBOUND_JOB_GROUP ="PHIS JOB GROUP";
	public static final String PHIS_CM_OUTBOUND_CONTINUOUS_JOB="PHIS OUTBOUND CM JOB";
	public static final String PHIS_CM_CONTINUOUS_JOB_DESC ="Job run twice daily 9 AM and 9 PM";
	
	//GFMS IN BOUND
	//PM
	public static final String GFMAS_PM_IN_JOB_GROUP ="GFMAS JOB GROUP";
	public static final String GFMAS_PM_IN_CONTINUOUS_JOB="GFMAS IN PM JOB";
	public static final String GFMAS_PM_CONTINUOUS_JOB_DESC ="Job run every 5 minutes";
	//SM
	public static final String GFMAS_SM_IN_JOB_GROUP ="GFMAS JOB GROUP";
	public static final String GFMAS_SM_IN_CONTINUOUS_JOB ="GFMAS IN SM JOB";
	public static final String GFMAS_SM_CONTINUOUS_JOB_DESC ="Job run every 5 minute";
	//SC
	public static final String GFMAS_SC_OUT_JOB_GROUP ="GFMAS JOB GROUP";
	public static final String GFMAS_SC_OUT_CONTINUOUS_JOB ="GFMAS OUT SC JOB";
	public static final String GFMAS_SC_CONTINUOUS_JOB_DESC ="Job run once daily 8 AM";
	//FL
	public static final String GFMAS_FL_IN_JOB_GROUP ="GFMAS JOB GROUP";
	public static final String GFMAS_FL_IN_CONTINUOUS_JOB ="GFMAS IN FL JOB";
	public static final String GFMAS_FL_CONTINUOUS_JOB_DESC ="Job run every 1 hour";
	
	//GFMS OUT BOUND
	//SM
	public static final String GFMAS_SM_OUT_JOB_GROUP="GFMAS JOB GROUP";
	public static final String GFMAS_SM_OUT_CONTINUOUS_JOB ="GFMAS OUT SM JOB";
	public static final String GFMAS_SM_OUT_CONTINUOUS_JOB_DESC ="Job run every 5 minute";
	
	// NOTIFICATIONS
	public static final String NOTIFICATION_INTEGRATION_CONTINUOUS_JOB= "NOTIFICATION JOB GROUP";	
	public static final String NOTIFICATION_INTEGRATION_JOB_GROUP  = "NOTIFICATION INTEGRATION JOB";	
	public static final String NOTIFICATION_INTEGRATION_CONTINUOUS_JOB_DESC = "Job run 10 AM and 10 PM";
	
	public static final String CM_EXPIRED_ITEM_TIMER_JOB= "CM JOB GROUP";	
	public static final String CM_EXPIRED_ITEM_TIMER_JOB_GROUP  = "ITEM EXPIRED JOB";	
	public static final String CM_EXPIRED_ITEM_TIMER_CONTINUOUS_JOB_DESC = "Job run 12 AM";
	public static final String CM_EXPIRED_ITEM_TIMER = "0 01 00 * * ?";
	
	public static final String CM_DROP_DOC_SEQ_JOB_GROUP  = "DROP DOC SEQUENCE JOB";	

}
