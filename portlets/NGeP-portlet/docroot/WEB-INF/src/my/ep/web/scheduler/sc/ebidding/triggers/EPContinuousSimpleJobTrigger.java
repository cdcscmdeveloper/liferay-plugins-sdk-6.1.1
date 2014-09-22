package my.ep.web.scheduler.sc.ebidding.triggers;

import my.ep.web.scheduler.sc.ebidding.JobTasks.ContinuousBidSchedulerJob;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class EPContinuousSimpleJobTrigger {

	public static Scheduler run(Scheduler scheduler, JobKey jobKey) {

		try {
			JobDetail job = JobBuilder.newJob(ContinuousBidSchedulerJob.class)
					.withIdentity(jobKey).withDescription("Job to run every 60 Seconds").build();
			
			
			// Trigger the job to run now, and then every 60 seconds
			// TODO decision upon interval is pending
			Trigger trigger = TriggerBuilder
					.newTrigger()
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule()
									.withIntervalInSeconds(60).repeatForever())
					.build();

			// this job is responsible to check for the most recent bid to be
			// scheduled
			scheduler.scheduleJob(job, trigger);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return scheduler;

	}

}
