package my.ep.web.scheduler.sc.ebidding.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.sc.ebidding.EPSchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.sc.ebidding.JobTasks.BidSchedulesPoolingCronJob;
import my.ep.web.scheduler.sc.ebidding.JobTasks.ContinuousBidSchedulerJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class EpJobServletInitializer extends HttpServlet {

	public static Logger log = EpLogger
			.getLogger(EpJobServletInitializer.class);

	private static final long serialVersionUID = 2591757747850240619L;

	public void init(ServletConfig config) throws ServletException {
		
		// Sc Daily job
				EpSchedulerHelper.scheduleJob(config.getServletContext(), new JobKey(
						EPSchedulerConstants.EP_DAILY_JOB,
						EPSchedulerConstants.EP_JOB_GROUP),
						BidSchedulesPoolingCronJob.class,
						"Job run every day at 11:55 PM", "0 55 23 * * ?");

		// daily Job to be run at server startup
		
		EpSchedulerHelper.scheduleStartupJob(config.getServletContext(), new JobKey(
				EPSchedulerConstants.EP_DAILY_JOB,
				EPSchedulerConstants.EP_JOB_GROUP),
				BidSchedulesPoolingCronJob.class, "Job to run at server start-up");

		// Sc Continous job
		EpSchedulerHelper.scheduleJob(config.getServletContext(), new JobKey(
				EPSchedulerConstants.EP_CONTINOUS_JOB,
				EPSchedulerConstants.EP_JOB_GROUP),
				ContinuousBidSchedulerJob.class, "Job to run every 60 Seconds",
				"0/60 * * * * ?");

		
		
	}

}
