/*
 * 
 */

package my.ep.web.scheduler.sc.ebidding.jobtasklistener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import my.ep.factory.EpLogger;
import my.ep.test.quartz.constant.EpQuartzConstant;
import my.ep.test.quartz.job.sc.ScContinousJob;
import my.ep.test.quartz.job.sc.ScDailyJob;
import my.ep.test.quartz.job.servlet.EpJobServletInitializer;
import my.ep.test.quartz.trigger.sc.EpScheduler;
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
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;

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
