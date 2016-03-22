package my.ep.web.scheduler.helper;

import java.util.HashSet;
import java.util.Set;

import my.ep.factory.EpLogger;

import org.apache.commons.lang3.StringUtils;
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
import org.quartz.impl.matchers.GroupMatcher;

/**
 * Helper class for manipulating quartz scheduler
 * 
 * @author Syazwan
 * 
 */
public class EpSchedulerHelper {
	public static Logger log = EpLogger.getLogger(EpSchedulerHelper.class);

	private static Scheduler instance;

	/**
	 * Schedule new job or update existing scheduled job
	 * 
	 * @param JobKey
	 *            jobKey (mandatory)
	 * @param Class
	 *            <? extends Job> jobClass (mandatory)
	 * @param String
	 *            jobDetailDesc (mandatory)
	 * @param String
	 *            cronExpression (mandatory)
	 */
	public static void scheduleJob(JobKey jobKey,
			Class<? extends Job> jobClass, String jobDetailDesc,
			String cronExpression) {

		if (jobClass != null && jobKey != null
				&& !StringUtils.isBlank(cronExpression)
				&& !StringUtils.isBlank(jobDetailDesc)) {
			try {

				// start scheduler if not started yet
				if (!EpSchedulerHelper.getScheduler().isStarted()) {
					EpSchedulerHelper.getScheduler().start();
				}

				// check if job already exist
				if (!EpSchedulerHelper.getScheduler().checkExists(jobKey)) {
					// specify the job' s details..
					final JobDetail job = JobBuilder.newJob(jobClass)
							.withIdentity(jobKey)
							.withDescription(jobDetailDesc).build();

					// specify trigger
					final CronTrigger cronTrigger = TriggerBuilder
							.newTrigger()
							.withSchedule(
									CronScheduleBuilder
											.cronSchedule(cronExpression))
							.build();

					// schedule new job
					EpSchedulerHelper.getScheduler().scheduleJob(job,
							cronTrigger);
				} else {
					// Get existing job
					JobDetail oldJob = EpSchedulerHelper.getScheduler()
							.getJobDetail(jobKey);

					// update job desc if got change
					if (oldJob != null) {

						// retrieve the trigger
						CronTrigger oldTrigger = (CronTrigger) getTriggerByJobKey(jobKey);
						CronTrigger newTrigger = oldTrigger;

						// obtain a builder that would produce the trigger
						TriggerBuilder tb = oldTrigger.getTriggerBuilder();

						// update trigger cron expression
						if (oldTrigger != null
								&& !oldTrigger.getCronExpression().equals(
										cronExpression)) {
							newTrigger = (CronTrigger) tb.withSchedule(
									CronScheduleBuilder
											.cronSchedule(cronExpression))
									.build();
							EpSchedulerHelper.getScheduler().rescheduleJob(
									oldTrigger.getKey(), newTrigger);
						}

						// update job description
						if (!oldJob.getDescription().equals(jobDetailDesc)) {

							JobDetail newJob = JobBuilder.newJob(jobClass)
									.withIdentity(jobKey)
									.withDescription(jobDetailDesc).build();

							Set<CronTrigger> newTriggerSet = new HashSet<CronTrigger>();
							newTriggerSet.add(newTrigger);

							EpSchedulerHelper.getScheduler().scheduleJob(
									newJob, newTriggerSet, true);
						}

					}

				}

			} catch (final SchedulerException e) {
				log.error(e.getMessage());
			}
		}
	}

	/**
	 * Instantly run job for once only
	 * 
	 * @param JobKey
	 *            jobKey (mandatory)
	 * @param Class
	 *            <? extends Job> jobClass (mandatory)
	 */
	public static void runJobOnce(JobKey jobKey, Class<? extends Job> jobClass) {
		// start scheduler if not started yet
		try {

			if (jobKey != null && jobClass != null) {
				JobDetail jobDetail = null;
				if (!EpSchedulerHelper.getScheduler().isStarted()) {
					EpSchedulerHelper.getScheduler().start();
				}

				// check if job not exist, add to scheduler context
				if (!EpSchedulerHelper.getScheduler().checkExists(jobKey)) {
					jobDetail = JobBuilder.newJob(jobClass)
							.withIdentity(jobKey).storeDurably().build();

					// add job
					EpSchedulerHelper.getScheduler().addJob(jobDetail, true);
				}

				// trigger job
				EpSchedulerHelper.getScheduler().triggerJob(jobKey);
			}

		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Instantly run existing job
	 * 
	 * @param JobKey
	 *            jobKey (mandatory)
	 */
	public static void runBuildJobNow(JobKey jobKey) {
		// specify trigger
		Trigger trigger;
		try {
			if (jobKey != null) {
				JobDetail job = EpSchedulerHelper.getScheduler().getJobDetail(
						jobKey);

				if (job != null) {
					trigger = TriggerBuilder.newTrigger().startNow()
							.forJob(job).build();

					// schedule the job
					EpSchedulerHelper.getScheduler().scheduleJob(trigger);
				}
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	/**
	 * Check if the job is already running
	 * 
	 * @param JobKey
	 *            jobKey (mandatory)
	 * @return boolean
	 */
	public static boolean isJobRunningNow(final JobKey jobKey) {
		boolean retVal = false;

		try {
			if (jobKey != null) {
				for (final JobExecutionContext jobExecute : EpSchedulerHelper
						.getScheduler().getCurrentlyExecutingJobs()) {
					if (jobExecute.getJobDetail().getKey().equals(jobKey)) {
						retVal = true;
						break;
					}
				}
			}
		} catch (final SchedulerException e) {
			log.error(e.getMessage());
		}

		return retVal;
	}

	/**
	 * Get trigger by job key
	 * 
	 * @param JobKey
	 *            jobKey (mandatory)
	 * @return Trigger
	 * @throws SchedulerException
	 */
	public static Trigger getTriggerByJobKey(final JobKey jobKey)
			throws SchedulerException {
		return EpSchedulerHelper.getScheduler().getTriggersOfJob(jobKey).get(0);
	}

	/**
	 * Get trigger state
	 * 
	 * @param Trigger
	 *            trigger (mandatory)
	 * @return String
	 */
	public static String getTriggerState(final Trigger trigger) {
		String state = TriggerState.NONE.toString();
		try {
			if (trigger != null) {
				state = EpSchedulerHelper.getScheduler()
						.getTriggerState(trigger.getKey()).toString();
			}
		} catch (final SchedulerException e) {
			log.error(e.getMessage());
		}

		return state;
	}

	/**
	 * Get existing job key by job name and job group
	 * 
	 * @param String
	 *            jobName
	 * @param String
	 *            jobGroup
	 * @return JobKey
	 */
	public static JobKey getJobKey(String jobName, String jobGroup) {
		JobKey returnJobKey = null;
		try {
			for (String groupName : getScheduler().getJobGroupNames()) {

				if (jobGroup.equalsIgnoreCase(groupName)) {
					for (JobKey jobKey : getScheduler().getJobKeys(
							GroupMatcher.jobGroupEquals(groupName))) {

						if (jobKey.getName().equals(jobName)) {
							returnJobKey = jobKey;
							break;
						}
					}

					break;
				}

			}
		} catch (Exception e) {
			log.error(e);
		}

		return returnJobKey;

	}

	/**
	 * Get scheduler instance
	 * 
	 * @return Scheduler
	 */
	public static Scheduler getScheduler() {
		if (instance == null) {
			try {
				instance = new StdSchedulerFactory().getScheduler();
			} catch (SchedulerException e) {
				log.error(e.getMessage());
			}
		}
		return instance;

	}

}
