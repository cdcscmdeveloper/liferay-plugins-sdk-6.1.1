package my.ep.web.scheduler.sc.ebidding.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.sc.ebidding.JobTasks.BidSchedulesPoolingCronJob;
import my.ep.web.scheduler.sc.ebidding.JobTasks.ContinuousBidSchedulerJob;
import my.ep.web.scheduler.sc.ebidding.constants.EPSchedulerConstants;
import my.ep.web.scheduler.sc.ebidding.triggers.EPContinuousSimpleJobTrigger;
import my.ep.web.scheduler.sc.ebidding.triggers.EPDailyCronJobTrigger;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

public class EpJobServletInitializer extends HttpServlet {

	public static Logger log = EpLogger
			.getLogger(EpJobServletInitializer.class);

	private static final long serialVersionUID = 2591757747850240619L;

	public void init(ServletConfig config) throws ServletException {

		// Sc Continous job
		EpSchedulerHelper.scheduleJob(config.getServletContext(), new JobKey(
				EPSchedulerConstants.EP_CONTINOUS_JOB,
				EPSchedulerConstants.EP_JOB_GROUP),
				ContinuousBidSchedulerJob.class, "Job to run every 60 Seconds",
				"0/60 * * * * ?");

		// Sc Daily job
		EpSchedulerHelper.scheduleJob(config.getServletContext(), new JobKey(
				EPSchedulerConstants.EP_DAILY_JOB,
				EPSchedulerConstants.EP_JOB_GROUP),
				BidSchedulesPoolingCronJob.class,
				"Job run every day at 11:55 PM", "0 55 23 * * ?");
	}

}
