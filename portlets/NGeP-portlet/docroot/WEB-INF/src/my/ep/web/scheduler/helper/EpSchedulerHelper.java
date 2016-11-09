package my.ep.web.scheduler.helper;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
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
		scheduleJob(jobKey, jobClass, jobDetailDesc, cronExpression, null);
	}
	
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
	 * @param JobDataMap
	 *            job data map (mandatory)
	 */
	public static void scheduleJob(JobKey jobKey,
			Class<? extends Job> jobClass, String jobDetailDesc,
			String cronExpression, JobDataMap jobDataMap) {
		addAndScheduleNewJob(jobKey, jobClass, jobDetailDesc, cronExpression, jobDataMap);
		updateScheduledJob(jobKey, jobDetailDesc, cronExpression, jobClass);
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
			String cronExpression, JobDataMap jobDataMap) {
		if (jobKey != null && jobClass != null
				&& !StringUtils.isBlank(jobDetailDesc)
				&& !StringUtils.isBlank(cronExpression)) {
			try {
				if (!EpSchedulerHelper.getScheduler().checkExists(jobKey)) {
					// specify the job' s details..
					JobBuilder jobBuilder = JobBuilder.newJob(jobClass)
							.withIdentity(jobKey)
							.withDescription(jobDetailDesc);
					if (jobDataMap != null) {
						jobBuilder = jobBuilder.storeDurably().usingJobData(jobDataMap);
					}
					final JobDetail job = jobBuilder.build();

					// specify trigger
					final CronTrigger cronTrigger = TriggerBuilder
							.newTrigger()
							.withSchedule(
									CronScheduleBuilder
											.cronSchedule(cronExpression)
											.withMisfireHandlingInstructionFireAndProceed())
							.build();
					log.info(">>>>>>>>>>>ADD JOB:" + jobKey.getName()
							+ "   MISFIRE POLICY:"
							+ cronTrigger.getMisfireInstruction()
							+ "<<<<<<<<<<<<<<<");

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
			String cronExpression, Class<? extends Job> jobClass) {
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
													.withMisfireHandlingInstructionFireAndProceed())
									.build();
							log.info(">>>>>>>>>>>UPDATE JOB:"
									+ jobKey.getName() + "   MISFIRE POLICY:"
									+ oldTrigger.getMisfireInstruction()
									+ "<<<<<<<<<<<<<<<");

						}

						CronTrigger newTrigger = oldTrigger;

						// obtain a builder that would produce the trigger
						TriggerBuilder tb = newTrigger.getTriggerBuilder();

						// update trigger cron expression and miss fire policy
						// do nothing
						if ((!newTrigger.getCronExpression().equals(
								cronExpression) && !isJobRunningNow(jobKey))
								|| newTrigger.getMisfireInstruction() != newTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
							newTrigger = (CronTrigger) tb
									.withSchedule(
											CronScheduleBuilder
													.cronSchedule(
															cronExpression)
													.withMisfireHandlingInstructionFireAndProceed())
									.build();
							EpSchedulerHelper.getScheduler().rescheduleJob(
									newTrigger.getKey(), newTrigger);
							log.info(">>>>>>>>>>>UPDATE JOB:"
									+ jobKey.getName() + "   MISFIRE POLICY:"
									+ newTrigger.getMisfireInstruction()
									+ "<<<<<<<<<<<<<<<");
						}

						// update job description or job class
						if ((!oldJob.getDescription().equals(jobDetailDesc) || !oldJob
								.getJobClass().equals(jobClass))) {

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
	 * Get cron expression by jobKey
	 * 
	 * @param JobKey
	 *            jobKey
	 * @return String
	 */
	public static String getCronExpressionByJobKey(JobKey jobKey) {
		String retVal = StringUtils.EMPTY;
		try {
			List<? extends Trigger> triggers = getScheduler().getTriggersOfJob(
					jobKey);
			for (Trigger trigger : triggers) {
				if (trigger instanceof CronTrigger) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					retVal = cronTrigger.getCronExpression();
				}
			}
		} catch (SchedulerException e) {
			log.error(e);
		}
		return retVal;
	}

	/**
	 * Get scheduler instance
	 * 
	 * @return Scheduler
	 */
	public static Scheduler getScheduler() {
		if (instance == null) {
			log.info(">>>>>EP SCHEDULER INSTANCE IS NULL<<<<<<");
			try {
				instance = StdSchedulerFactory.getDefaultScheduler();
				if (!instance.isStarted()) {
					log.info(">>>>>EP SCHEDULER INSTANCE IS NOT STARTED<<<<<<");
					instance.start();
					log.info(">>>>>EP SCHEDULER INSTANCE IS FINISH STARTED<<<<<<");
				}
			} catch (SchedulerException e) {
				log.info(">>>>>FAILED TO START EP SCHEDULER DUE TO ERR MSG:"
						+ e.getMessage() + "<<<<<<<<<");
			}
		}
		return instance;

	}

	/**
	 * schedule simple Job
	 * 
	 * @param jobKey
	 *            the JobKey instance,jobClass the jobClass
	 *            instance,newJobDataMap the JobDataMap instance,scheduleDate
	 *            the Date instance
	 * @throws SchedulerException
	 */
	public static void scheduleSimpleJob(JobKey jobKey, Class jobClass,
			JobDataMap newJobDataMap, Date scheduleDate)
			throws SchedulerException {

		// define the job and tie it to our QTPublishJob class
		JobDetail job = JobBuilder.newJob(jobClass)
				.withIdentity(jobKey.getName(), jobKey.getGroup())
				.usingJobData(newJobDataMap).build();

		// define a simple trigger that runs at specific time (publish time)
		SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
				.withIdentity(jobKey.getName(), jobKey.getGroup())
				.startAt(scheduleDate).forJob(jobKey).build();

		// Tell quartz to schedule the job using our trigger
		getScheduler().scheduleJob(job, trigger);
	}

	/**
	 * To update job data in the JobDetail
	 * 
	 * @param jobName
	 * 		the job name
	 * @param jobGroup
	 * 		the job group
	 * @param jobDataKey
	 * 		the job data key
	 * @param jobDataValue
	 * 		the job data value
	 */
	public static void updateJobDetailData(String jobName, String jobGroup, String jobDataKey, Object jobDataValue) {
		try {
			JobDetail jobDetail = EpSchedulerHelper.getScheduler()
					.getJobDetail(getJobKey(jobName, jobGroup));
			Map<String, Object> jobDataMap = jobDetail.getJobDataMap().getWrappedMap();
			jobDataMap.put(jobDataKey, jobDataValue);
			
			EpSchedulerHelper.getScheduler().addJob(jobDetail, true);
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}
}
