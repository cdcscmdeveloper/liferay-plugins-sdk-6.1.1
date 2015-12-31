package my.ep.web.scheduler.servlet.cm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.cm.CmDropSequenceJob;
import my.ep.web.scheduler.job.cm.CmUpdateExpiredItemJob;
import my.ep.web.scheduler.job.eperunding.EperundingBatchIntegrationJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class CmSchedulerServlet extends HttpServlet {
	private static final long serialVersionUID = 8691110272078406918L;
	public static Logger log = EpLogger
			.getLogger(CmSchedulerServlet.class);

	public void init(ServletConfig config) throws ServletException {

		// PHIS PM Out bound Continuous job
			/*EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.PHIS_PM_OUTBOUND_CONTINUOUS_JOB,
				CmSchedulerConstants.PHIS_PM_OUTBOUND_JOB_GROUP),
				PhisBatchPmOutboundJob.class, CmSchedulerConstants.PHIS_PM_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("phis.pm.outbound"));
		//PHIS SM Out bound Continuous job
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.PHIS_SM_OUTBOUND_CONTINUOUS_JOB,
				CmSchedulerConstants.PHIS_SM_OUTBOUND_JOB_GROUP),
				PhisBatchSmOutboundJob.class, CmSchedulerConstants.PHIS_SM_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("phis.sm.outbound")); 
		//PHIS CM Out bound Continuous job
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.PHIS_CM_OUTBOUND_CONTINUOUS_JOB,
				CmSchedulerConstants.PHIS_CM_OUTBOUND_JOB_GROUP),
				PhisBatchCmOutboundJob.class, CmSchedulerConstants.PHIS_CM_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("phis.cm.outbound"));
		//GFMAS PM IN bound Continuous job				
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.GFMAS_FL_IN_CONTINUOUS_JOB,
				CmSchedulerConstants.GFMAS_FL_IN_JOB_GROUP),
				GfmasBatchFlInboundJob.class, CmSchedulerConstants.GFMAS_FL_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.fl.inbound"));
		//GFMAS PM IN bound Continuous job				
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.GFMAS_PM_IN_CONTINUOUS_JOB,
				CmSchedulerConstants.GFMAS_PM_IN_JOB_GROUP),
				GfmasBatchPmInboundJob.class, CmSchedulerConstants.GFMAS_PM_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.pm.inbound"));
		//GFMAS SC IN bound Continuous job				
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.GFMAS_SC_OUT_CONTINUOUS_JOB,
				CmSchedulerConstants.GFMAS_SC_OUT_JOB_GROUP),
				GfmasBatchScOutboundJob.class, CmSchedulerConstants.GFMAS_SC_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.sc.outbound"));  
		//GFMAS SM IN bound Continuous job				
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.GFMAS_SM_IN_CONTINUOUS_JOB,
				CmSchedulerConstants.GFMAS_SM_IN_JOB_GROUP),
				GfmasBatchSmInboundJob.class, CmSchedulerConstants.GFMAS_SM_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.sm.inbound"));
		//GFMAS SM OUT bound Continuous job				
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.GFMAS_SM_OUT_CONTINUOUS_JOB,
				CmSchedulerConstants.GFMAS_SM_OUT_JOB_GROUP),
				GfmasBatchSmOutboundJob.class, CmSchedulerConstants.GFMAS_SM_OUT_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.sm.outbound"));*/
		//Eperunding SM Integration Continuous job
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				EperundingBatchIntegrationJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound"));
		//Notification Integration Continuous job
	/*	EpSchedulerHelper.scheduleJob(new JobKey(
						CmSchedulerConstants.NOTIFICATION_INTEGRATION_CONTINUOUS_JOB,
						CmSchedulerConstants.NOTIFICATION_INTEGRATION_JOB_GROUP),
						SmNotificationJob.class, CmSchedulerConstants.NOTIFICATION_INTEGRATION_CONTINUOUS_JOB_DESC,
						EpPropertiesUtils.getInstance().getProperty("notification.sm.outbound"));*/

		///CM Expired Item
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.CM_EXPIRED_ITEM_TIMER_JOB,
				SchedulerConstants.CM_EXPIRED_ITEM_TIMER_JOB_GROUP),
				CmUpdateExpiredItemJob.class, SchedulerConstants.CM_EXPIRED_ITEM_TIMER_CONTINUOUS_JOB_DESC,
				SchedulerConstants.CM_EXPIRED_ITEM_TIMER);


		///CM Drop Doc Sequence
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.CM_EXPIRED_ITEM_TIMER_JOB,
				SchedulerConstants.CM_DROP_DOC_SEQ_JOB_GROUP),
				CmDropSequenceJob.class, SchedulerConstants.CM_EXPIRED_ITEM_TIMER_CONTINUOUS_JOB_DESC,
				SchedulerConstants.CM_EXPIRED_ITEM_TIMER);



		
	}
}
