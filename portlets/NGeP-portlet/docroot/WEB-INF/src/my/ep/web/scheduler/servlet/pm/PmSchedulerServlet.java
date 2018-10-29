package my.ep.web.scheduler.servlet.pm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.SchedulerPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.gfmas.GfmasBatchPmInboundJob;
import my.ep.web.scheduler.job.gfmas.MasterDataErrorInboundJob;
import my.ep.web.scheduler.job.pm.PmAccessApplJob;
import my.ep.web.scheduler.job.pm.PmAccessApplReminderJob;
import my.ep.web.scheduler.job.pm.PmConfigRulesJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class PmSchedulerServlet extends HttpServlet {

	private static final long serialVersionUID = -4832945404532466168L;
	public static Logger log = EpLogger.getLogger(PmSchedulerServlet.class);

	public void init(ServletConfig config) throws ServletException {

		// 1GFMAS integration Jobs
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.GFMAS_MASTER_DATA_INBOUND,
								SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
						GfmasBatchPmInboundJob.class,
						SchedulerConstants.GFMAS_MASTER_DATA_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_1GFMAS_MASTER_DATA_INBOUND));

		// 1GFMAS integration Jobs
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.GFMAS_MASTER_DATA_ERROR,
								SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
						MasterDataErrorInboundJob.class,
						SchedulerConstants.GFMAS_PM_ERROR_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_1GFMAS_MASTER_DATA_ERROR));

		// Update Expired Access Application Workflow Status
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.UPDATE_EXPIRED_ACCESS_APPLICATION,
								SchedulerConstants.PM_JOB_GROUP),
						PmAccessApplJob.class,
						SchedulerConstants.UPDATE_EXPIRED_ACCESS_APPLICATION_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_UPDATE_EXPIRED_ACCESS));

		// Send Access Application Reminder
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.SEND_REMINDER_ACCESS_APPLICATION,
								SchedulerConstants.PM_JOB_GROUP),
						PmAccessApplReminderJob.class,
						SchedulerConstants.SEND_REMINDER_ACCESS_APPLICATION_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_SEND_REMINDER_ACCESS));

		// Reset Pm RuleConfig Map
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.RESET_CONFIG_RULES_MAP,
								SchedulerConstants.PM_JOB_GROUP),
						PmConfigRulesJob.class,
						SchedulerConstants.RESET_CONFIG_RULES_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_RESET_CONFIG_RULES_MAP));
		
		// Update PO Weightage
				EpSchedulerHelper
						.scheduleJob(
								new JobKey(SchedulerConstants.UPDATE_PO_WEIGHTAGE,
										SchedulerConstants.PM_JOB_GROUP),
								PmConfigRulesJob.class,
								SchedulerConstants.UPDATE_PO_WEIGHTAGE_JOB_DESC,
								SchedulerPropertiesUtils
										.getInstance()
										.getProperty(
												EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_RESET_CONFIG_RULES_MAP));

	}
}
