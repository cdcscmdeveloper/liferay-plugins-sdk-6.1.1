package my.ep.web.scheduler.helper;

import java.text.ParseException;

import javax.servlet.ServletContext;

import my.ep.factory.EpLogger;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author Syazwan
 */
public class EpSchedulerHelper {
	public static Logger log = EpLogger.getLogger(EpSchedulerHelper.class);

	public static void scheduleJob(ServletContext ctx, JobKey jobKey,
			Class<? extends Job> jobClass, String jobDetailDesc,
			String cronExpression) {

		Scheduler scheduler = null;
		try {

			// get scheduler
			scheduler = ((StdSchedulerFactory) ctx
					.getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY))
					.getScheduler();

			// specify the job' s details..
			final JobDetail job = JobBuilder.newJob(jobClass)
					.withIdentity(jobKey).withDescription(jobDetailDesc)
					.build();

			// specify trigger
			final CronTrigger cronTrigger = TriggerBuilder
					.newTrigger()
					.withSchedule(
							CronScheduleBuilder.cronSchedule(cronExpression))
					.build();

			// start scheduler if not started yet
			if (!scheduler.isStarted()) {
				scheduler.start();
			}

			// schedule the job
			scheduler.scheduleJob(job, cronTrigger);

		} catch (final SchedulerException | ParseException e) {
			log.error(e.getMessage());
		}

	}

	public static void scheduleStartupJob(ServletContext context,
			JobKey jobKey, Class<? extends Job> jobClass, String jobDetailDesc) {
		Scheduler scheduler = null;
		try {

			// get scheduler
			scheduler = ((StdSchedulerFactory) context
					.getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY))
					.getScheduler();

			// specify the job' s details..
			JobDetail job = scheduler.getJobDetail(jobKey);
			// specify trigger
			Trigger trigger = TriggerBuilder.newTrigger().startNow()
					.forJob(job).build();
			// start scheduler if not started yet
			if (!scheduler.isStarted()) {
				scheduler.start();
			}

			// schedule the job
			scheduler.scheduleJob(trigger);

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			scheduler = null;
		}

	}

}
