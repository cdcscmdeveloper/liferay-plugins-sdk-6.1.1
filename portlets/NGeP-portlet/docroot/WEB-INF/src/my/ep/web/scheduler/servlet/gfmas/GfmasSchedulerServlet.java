package my.ep.web.scheduler.servlet.gfmas;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.SchedulerPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.gfmas.GLAgOffJob;
import my.ep.web.scheduler.job.gfmas.GLBankjob;
import my.ep.web.scheduler.job.gfmas.GLDANAJob;
import my.ep.web.scheduler.job.gfmas.GLKPTJJob;
import my.ep.web.scheduler.job.gfmas.GLPTJJob;
import my.ep.web.scheduler.job.gfmas.GLPrgActJob;
import my.ep.web.scheduler.job.gfmas.GLProjectJob;
import my.ep.web.scheduler.job.gfmas.GLSegJob;
import my.ep.web.scheduler.job.gfmas.GLVOTJob;
import my.ep.web.scheduler.job.gfmas.GlAccjob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class GfmasSchedulerServlet extends HttpServlet {

	private static final long serialVersionUID = -4832945404532466168L;
	public static Logger log = EpLogger.getLogger(GfmasSchedulerServlet.class);

	public void init(ServletConfig config) throws ServletException {

		// gfmas auto Integration Jobs
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_KPTJ_JOB,
						SchedulerConstants.GFMAS_AUTO_INTEGRATION_JOB_GROUP),
						GLKPTJJob.class,
						SchedulerConstants.GFMAS_KPTJ_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_GFMAS_KPTJ));
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_PP_JOB,
						SchedulerConstants.GFMAS_AUTO_INTEGRATION_JOB_GROUP),
						GLSegJob.class,
						SchedulerConstants.GFMAS_KPTJ_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_GFMAS_PP));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_BANK_JOB,
						SchedulerConstants.GFMAS_AUTO_INTEGRATION_JOB_GROUP),
						GLBankjob.class,
						SchedulerConstants.GFMAS_KPTJ_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_GFMAS_BANK));
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_AGOFF_JOB,
						SchedulerConstants.GFMAS_AUTO_INTEGRATION_JOB_GROUP),
						GLAgOffJob.class,
						SchedulerConstants.GFMAS_KPTJ_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_GFMAS_AGOFF));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_GLACC_JOB,
						SchedulerConstants.GFMAS_AUTO_INTEGRATION_JOB_GROUP),
						GlAccjob.class,
						SchedulerConstants.GFMAS_KPTJ_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_GFMAS_GLACC));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_PTJ_JOB,
						SchedulerConstants.GFMAS_AUTO_INTEGRATION_JOB_GROUP),
						GLPTJJob.class,
						SchedulerConstants.GFMAS_KPTJ_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_GFMAS_GLPTJ));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_VOT_JOB,
						SchedulerConstants.GFMAS_AUTO_INTEGRATION_JOB_GROUP),
						GLVOTJob.class,
						SchedulerConstants.GFMAS_KPTJ_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_GFMAS_GLVOT));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_DANA_JOB,
						SchedulerConstants.GFMAS_AUTO_INTEGRATION_JOB_GROUP),
						GLDANAJob.class,
						SchedulerConstants.GFMAS_KPTJ_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_GFMAS_GLDANA));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_PROG_ACT_JOB,
						SchedulerConstants.GFMAS_AUTO_INTEGRATION_JOB_GROUP),
						GLPrgActJob.class,
						SchedulerConstants.GFMAS_KPTJ_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_GFMAS_PROG_ACT));
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_PROJECT_JOB,
						SchedulerConstants.GFMAS_AUTO_INTEGRATION_JOB_GROUP),
						GLProjectJob.class,
						SchedulerConstants.GFMAS_KPTJ_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_GFMAS_PROJECT));
		
	}
}
