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
	
	private static Scheduler instance;

	public static void scheduleJob(JobKey jobKey,
			Class<? extends Job> jobClass, String jobDetailDesc,
			String cronExpression) {
		
		try {

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
			if (!EpSchedulerHelper.getScheduler().isStarted()) {
				EpSchedulerHelper.getScheduler().start();
			}

			// schedule the job
			EpSchedulerHelper.getScheduler().scheduleJob(job, cronTrigger);

		} catch (final SchedulerException | ParseException e) {
			log.error(e.getMessage());
		}

	}
	
	public static void runBuildJobNow(JobKey jobKey){
		// specify trigger
		Trigger trigger;
		try {
			trigger = TriggerBuilder.newTrigger().startNow()
					.forJob(EpSchedulerHelper.getScheduler().getJobDetail(jobKey))
					.build();
			
			// schedule the job
			EpSchedulerHelper.getScheduler().scheduleJob(trigger);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Scheduler getScheduler(){
		if (instance == null){
			try {
				instance = new StdSchedulerFactory().getScheduler();
			} catch (SchedulerException e) {
				log.error(e.getMessage());
			}
		}
		return instance;
		
	}

}
