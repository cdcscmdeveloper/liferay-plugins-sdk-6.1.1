package my.ep.web.scheduler.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;

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
		addAndScheduleNewJob(jobKey, jobClass, jobDetailDesc, cronExpression);
		updateScheduledJob(jobKey, jobDetailDesc, cronExpression);
	}

	/**
	 * Instantly run job for once only
	 * 
	 * @param JobKey
	 *            jobKey (mandatory)
	 * @param Class
	 *            <? extends Job> jobClass (mandatory)
	 */
	public static boolean runJobOnce(JobKey jobKey,
			Class<? extends Job> jobClass) {
		boolean success = true;
		try {

			if (jobKey != null && jobClass != null) {
				JobDetail jobDetail = null;

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
			success = false;
			log.error(e.getMessage());
		}
		return success;
	}

	/**
	 * Instantly run existing job
	 * 
	 * @param JobKey
	 *            jobKey (mandatory)
	 */
	public static boolean runBuildJobNow(JobKey jobKey) {
		boolean success = true;

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
			success = false;
			log.error(e.getMessage());
		}
		return success;
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
	 * Delete job and all associated triggers by job key
	 * 
	 * @param JobKey
	 *            jobKey (mandatory)
	 */
	public static boolean deleteJobAndAssociatedTriggers(JobKey jobKey) {
		boolean success = true;
		try {
			EpSchedulerHelper.getScheduler().deleteJob(jobKey);
		} catch (SchedulerException e) {
			log.error(e.getMessage());
			success = false;
		}
		return success;
	}

	/**
	 * Add and schedule new job
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
	public static void addAndScheduleNewJob(JobKey jobKey,
			Class<? extends Job> jobClass, String jobDetailDesc,
			String cronExpression) {
		if (jobKey != null && jobClass != null
				&& !StringUtils.isBlank(jobDetailDesc)
				&& !StringUtils.isBlank(cronExpression)) {
			try {
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
											.cronSchedule(cronExpression)
											.withMisfireHandlingInstructionIgnoreMisfires())
							.build();

					// schedule new job
					EpSchedulerHelper.getScheduler().scheduleJob(job,
							cronTrigger);
				}
			} catch (SchedulerException e) {
				log.error(e.getMessage());
			}
		}
	}

	/**
	 * Update existing scheduled job
	 * 
	 * @param JobKey
	 *            jobKey (mandatory)
	 * @param String
	 *            jobDetailDesc (mandatory)
	 * @param String
	 *            cronExpression (mandatory)
	 */
	public static void updateScheduledJob(JobKey jobKey, String jobDetailDesc,
			String cronExpression) {
		if (jobKey != null && !StringUtils.isBlank(jobDetailDesc)
				&& !StringUtils.isBlank(cronExpression)) {
			try {
				if (EpSchedulerHelper.getScheduler().checkExists(jobKey)) {
					// Get existing job
					JobDetail oldJob = EpSchedulerHelper.getScheduler()
							.getJobDetail(jobKey);

					// update job desc if got change
					if (oldJob != null) {

						// retrieve the trigger
						CronTrigger oldTrigger = (CronTrigger) getTriggerByJobKey(jobKey);

						// if trigger is null, create new trigger and scheduled
						// with existing job
						if (oldTrigger == null) {
							oldTrigger = TriggerBuilder
									.newTrigger()
									.withSchedule(
											CronScheduleBuilder
													.cronSchedule(
															cronExpression)
													.withMisfireHandlingInstructionIgnoreMisfires())
									.build();

						}

						CronTrigger newTrigger = oldTrigger;

						// obtain a builder that would produce the trigger
						TriggerBuilder tb = newTrigger.getTriggerBuilder();

						// update trigger cron expression and miss fire policy
						// do nothing
						if ((!newTrigger.getCronExpression().equals(
								cronExpression) && !isJobRunningNow(jobKey))
								|| newTrigger.getMisfireInstruction() != newTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
							newTrigger = (CronTrigger) tb
									.withSchedule(
											CronScheduleBuilder
													.cronSchedule(
															cronExpression)
													.withMisfireHandlingInstructionIgnoreMisfires())
									.build();
							EpSchedulerHelper.getScheduler().rescheduleJob(
									newTrigger.getKey(), newTrigger);
						}

						// update job description
						if (!oldJob.getDescription().equals(jobDetailDesc)
								&& !isJobRunningNow(jobKey)) {

							JobDetail newJob = JobBuilder
									.newJob(oldJob.getJobClass())
									.withIdentity(jobKey)
									.withDescription(jobDetailDesc).build();

							Set<CronTrigger> newTriggerSet = new HashSet<CronTrigger>();
							newTriggerSet.add(newTrigger);

							EpSchedulerHelper.getScheduler().scheduleJob(
									newJob, newTriggerSet, true);
						}

					}
				}
			} catch (SchedulerException e) {
				log.error(e.getMessage());
			}
		}
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
		Trigger trigger = null;
		List<? extends Trigger> triggerList = EpSchedulerHelper.getScheduler()
				.getTriggersOfJob(jobKey);
		if (!EntityHelper.isCollectionEmpty(triggerList)) {
			trigger = triggerList.get(0);
		}
		return trigger;
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
				if (!instance.isStarted()) {
					instance.start();
				}
			} catch (SchedulerException e) {
				log.error(e.getMessage());
			}
		}
		return instance;

	}

}
