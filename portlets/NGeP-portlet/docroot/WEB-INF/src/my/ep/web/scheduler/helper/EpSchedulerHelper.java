package my.ep.web.scheduler.helper;

import java.text.ParseException;

import my.ep.factory.EpLogger;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
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
			log.error(e.getMessage());
		}
	}
	
	public static boolean isJobRunningNow(final JobKey jobKey) {
        boolean retVal = false;

        try {
            for (final JobExecutionContext jobExecute : EpSchedulerHelper.getScheduler().getCurrentlyExecutingJobs()) {
                if (jobExecute.getJobDetail().getKey().equals(jobKey)) {
                    retVal = true;
                    break;
                }
            }
        } catch (final SchedulerException e) {
        	log.error(e.getMessage());
        }

        return retVal;
    }

    public static Trigger getTriggerByJobKey(final JobKey jobKey) throws SchedulerException {
        return EpSchedulerHelper.getScheduler().getTriggersOfJob(jobKey).get(0);
    }

    public static String getTriggerState(final Trigger trigger) {
        String state = TriggerState.NONE.toString();
        try {
            state = EpSchedulerHelper.getScheduler().getTriggerState(trigger.getKey()).toString();
        } catch (final SchedulerException e) {
        	log.error(e.getMessage());
        }

        return state;
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
