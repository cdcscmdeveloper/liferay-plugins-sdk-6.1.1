package my.ep.web.scheduler.servlet.cm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.util.EpPropertiesUtils;
import my.ep.web.integration.common.BatchLogger;
import my.ep.web.scheduler.constants.cm.CmSchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.cm.CmUpdateExpiredItemJob;
import my.ep.web.scheduler.job.eperunding.EperundingBatchIntegrationJob;
import my.ep.web.scheduler.job.gfmas.GfmasBatchFlInboundJob;
import my.ep.web.scheduler.job.gfmas.GfmasBatchPmInboundJob;
import my.ep.web.scheduler.job.gfmas.GfmasBatchScInboundJob;
import my.ep.web.scheduler.job.gfmas.GfmasBatchSmInboundJob;
import my.ep.web.scheduler.job.gfmas.GfmasBatchSmOutboundJob;
import my.ep.web.scheduler.job.phis.PhisBatchCmOutboundJob;
import my.ep.web.scheduler.job.phis.PhisBatchPmOutboundJob;
import my.ep.web.scheduler.job.phis.PhisBatchSmOutboundJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class CmSchedulerServlet extends HttpServlet {
	private static final long serialVersionUID = 8691110272078406918L;
	public static Logger log = BatchLogger
			.getLogger(CmSchedulerServlet.class);
	
	public void init(ServletConfig config) throws ServletException {
				
			// PHIS PM Out bound Continuous job
				EpSchedulerHelper.scheduleJob(new JobKey(
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
						GfmasBatchScInboundJob.class, CmSchedulerConstants.GFMAS_SC_CONTINUOUS_JOB_DESC,
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
						EpPropertiesUtils.getInstance().getProperty("1gfmas.sm.outbound"));
				//Eperunding SM Integration Continuous job
					EpSchedulerHelper.scheduleJob(new JobKey(
						CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB,
						CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
						EperundingBatchIntegrationJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
						EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound"));
				//Notification Integration Continuous job
				/*EpSchedulerHelper.scheduleJob(new JobKey(
						CmSchedulerConstants.NOTIFICATION_INTEGRATION_CONTINUOUS_JOB,
						CmSchedulerConstants.NOTIFICATION_INTEGRATION_JOB_GROUP),
						SmNotificationJob.class, CmSchedulerConstants.NOTIFICATION_INTEGRATION_CONTINUOUS_JOB_DESC,
						EpPropertiesUtils.getInstance().getProperty("notification.sm.outbound"));*/
					
					///CM Expired Item
						EpSchedulerHelper.scheduleJob(new JobKey(
								CmSchedulerConstants.CM_EXPIRED_ITEM_TIMER_JOB,
								CmSchedulerConstants.CM_EXPIRED_ITEM_TIMER_JOB_GROUP),
								CmUpdateExpiredItemJob.class, CmSchedulerConstants.CM_EXPIRED_ITEM_TIMER_CONTINUOUS_JOB_DESC,
								CmSchedulerConstants.CM_EXPIRED_ITEM_TIMER);
	}
}
