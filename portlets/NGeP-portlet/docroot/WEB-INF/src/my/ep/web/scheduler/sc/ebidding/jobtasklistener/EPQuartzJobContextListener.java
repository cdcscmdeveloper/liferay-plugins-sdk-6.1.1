/*
 * 
 */

package my.ep.web.scheduler.sc.ebidding.jobtasklistener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.sc.ebidding.EPSchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.sc.ebidding.JobTasks.BidSchedulesPoolingCronJob;
import my.ep.web.scheduler.sc.ebidding.JobTasks.ContinuousBidSchedulerJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

/**
 * @author Venkat Chanumolu
 */
public class EPQuartzJobContextListener implements ServletContextListener {

	public static Logger log = EpLogger
			.getLogger(EPQuartzJobContextListener.class);

	@Override
	public void contextDestroyed(final ServletContextEvent arg0) {
		log.debug("Context Destroy...");
	}

	@Override
	public void contextInitialized(final ServletContextEvent ctx) {
		// Sc Continous job
		EpSchedulerHelper.scheduleJob(ctx.getServletContext(), new JobKey(
				EPSchedulerConstants.EP_CONTINOUS_JOB,
				EPSchedulerConstants.EP_JOB_GROUP),
				ContinuousBidSchedulerJob.class, "Job to run every 60 Seconds",
				"0/60 * * * * ?");

		// Sc Daily job
		EpSchedulerHelper.scheduleJob(ctx.getServletContext(), new JobKey(
				EPSchedulerConstants.EP_DAILY_JOB,
				EPSchedulerConstants.EP_JOB_GROUP),
				BidSchedulesPoolingCronJob.class,
				"Job run every day at 11:55 PM", "0 55 23 * * ?");

	}
}
