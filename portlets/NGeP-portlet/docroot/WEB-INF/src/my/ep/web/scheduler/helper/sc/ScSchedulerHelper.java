package my.ep.web.scheduler.helper.sc;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.sc.QTProposalCloseJob;
import my.ep.web.scheduler.job.sc.QTPublishJob;

import org.apache.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

/**
 * Singleton Helper For Sc Scheduler.
 * 
 * @author kenny
 * @since 05.07.2016
 * 
 */
public class ScSchedulerHelper {
	public static Logger log = EpLogger.getLogger(ScSchedulerHelper.class);
	private static ScSchedulerHelper instance;
	
	/**
	 * Get singleton of ScSchedulerHelper.
	 * 
	 * @return ScSchedulerHelper singleton
	 */
	public static ScSchedulerHelper getInstance(){
		if (instance == null){
			instance = new ScSchedulerHelper();
		}
		return instance;
	}

	/**
	 * sSchedule QT Publishing Job
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * @throws SchedulerException
	 */
	public void scheduleSimpleQTPublishingJob(ScQt scQt)
			throws SchedulerException {
		JobKey jobKey = getQTPublishingJobKey(scQt);

		// define the job and tie it to our QTPublishJob class
		JobDetail job = JobBuilder
				.newJob(QTPublishJob.class)
				.withIdentity(jobKey.getName(), jobKey.getGroup())
				.usingJobData(SchedulerConstants.QT_PUBLISH_CLOSE_JOB_DATA, scQt.getQtId()).build();
		// define a simple trigger that runs at specific time (publish time)
		SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder
				.newTrigger()
				.withIdentity(jobKey.getName(), jobKey.getGroup())
				.startAt(scQt.getPublishDate())
				.forJob(jobKey)
				.build();

		// Tell quartz to schedule the job using our trigger
		EpSchedulerHelper.getScheduler().scheduleJob(job, trigger);
	}

	/**
	 * Schedule QT Closing Job
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * @throws SchedulerException
	 */
	public void scheduleSimpleQTClosingJob(ScQt scQt)
			throws SchedulerException {
		JobKey jobKey = getQTClosingJobKey(scQt);

		// define the job and tie it to our QTPublishJob class
		JobDetail job = JobBuilder
				.newJob(QTProposalCloseJob.class)
				.withIdentity(jobKey.getName(), jobKey.getGroup())
				.usingJobData(SchedulerConstants.QT_PUBLISH_CLOSE_JOB_DATA, scQt.getQtId()).build();
		// define a simple trigger that runs at specific time (closing time)
		SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder
				.newTrigger()
				.withIdentity(jobKey.getName(), jobKey.getGroup())
				.startAt(scQt.getClosingDate())
				.forJob(jobKey).build();

		// Tell quartz to schedule the job using our trigger
		EpSchedulerHelper.getScheduler().scheduleJob(job, trigger);
	}
	
	/**
	 * Get QT Publishing JobKey by QT No.
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * @return	the JobKey
	 */
	public JobKey getQTPublishingJobKey(ScQt scQt) {
		return EpSchedulerHelper.getJobKey(
				SchedulerConstants.QT_PUBLISH_SIMPLE_JOB + scQt.getQtNo(), 
				SchedulerConstants.QT_PUBLISH_SIMPLE_JOB_GROUP);
	}
	
	/**
	 * Get QT Closing JobKey by QT No.
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * @return	the JobKey
	 */
	public JobKey getQTClosingJobKey(ScQt scQt) {
		return EpSchedulerHelper.getJobKey(
				SchedulerConstants.QT_PROPOSAL_CLOSE_SIMPLE_JOB + scQt.getQtNo(), 
				SchedulerConstants.QT_PROPOSAL_CLOSE_SIMPLE_JOB_GROUP);
	}
}
