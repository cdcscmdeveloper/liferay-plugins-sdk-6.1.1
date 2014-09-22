package my.ep.web.scheduler.sc.ebidding.triggers;

import my.ep.web.scheduler.sc.ebidding.JobTasks.BidSchedulesPoolingCronJob;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/*
 * This is the class that is instantiated and executes on server startup and this should be configured in web.xml
 */

public class EPDailyCronJobTrigger {

	
	public static Scheduler run(Scheduler scheduler,JobKey jobKey)throws Exception{

		
		// now we need one cron trigger for each possible bid to be scheduled and checks status of bid before proceeding
		JobDetail cronjob = JobBuilder.newJob(BidSchedulesPoolingCronJob.class)
				.withIdentity(jobKey).withDescription("Job to run every day at 11:55 PM").build();
		
		// cron expression is dynamic and will take start time and end time of the bid Schedule
		Trigger bidSchedulesPoolingCronJobTigger = TriggerBuilder
				.newTrigger()
				.withSchedule(
					CronScheduleBuilder.cronSchedule("0 55 23 * * ?").withMisfireHandlingInstructionFireAndProceed())
				.build();
		
    	scheduler.scheduleJob(cronjob, bidSchedulesPoolingCronJobTigger);
		return scheduler;
		
	}

}
