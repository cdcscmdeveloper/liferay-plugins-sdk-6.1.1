package my.ep.web.scheduler.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

public class QuartzAdapter {

	public String getJobName(JobDetail jobDetail) {
		return jobDetail.getKey().getName();
	}

	public String getJobGroup(JobDetail jobDetail) {
		return jobDetail.getKey().getGroup();
	}

	public String getJobFullName(JobDetail jobDetail) {
		return getJobGroup(jobDetail) + '.' + getJobName(jobDetail);
	}

	public String getJobDescription(JobDetail jobDetail) {
		return jobDetail.getDescription();
	}

	public Class<?> getJobClass(JobDetail jobDetail) {
		return jobDetail.getJobClass();
	}

	public Date getTriggerPreviousFireTime(Trigger trigger) {
		return trigger.getPreviousFireTime();
	}

	public Date getTriggerNextFireTime(Trigger trigger) {
		return trigger.getNextFireTime();
	}

	public String getCronTriggerExpression(CronTrigger trigger) {
		return trigger.getCronExpression();
	}

	public long getSimpleTriggerRepeatInterval(SimpleTrigger trigger) {
		return trigger.getRepeatInterval();
	}

	public JobDetail getContextJobDetail(JobExecutionContext context) {
		return context.getJobDetail();
	}

	public Date getContextFireTime(JobExecutionContext context) {
		return context.getFireTime();
	}

	public List<JobDetail> getAllJobsOfScheduler(Scheduler scheduler)
			throws SchedulerException {
		final List<JobDetail> result = new ArrayList<JobDetail>();
		for (final String jobGroupName : scheduler.getJobGroupNames()) {
			final GroupMatcher<JobKey> groupMatcher = GroupMatcher
					.groupEquals(jobGroupName);
			for (final JobKey jobKey : scheduler.getJobKeys(groupMatcher)) {
				final JobDetail jobDetail;
				try {
					jobDetail = scheduler.getJobDetail(jobKey);
					if (jobDetail != null) {
						result.add(jobDetail);
					}
				} catch (final Exception e) {

				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Trigger> getTriggersOfJob(JobDetail jobDetail,
			Scheduler scheduler) throws SchedulerException {
		return (List<Trigger>) scheduler.getTriggersOfJob(jobDetail.getKey());
	}

	public boolean isTriggerPaused(Trigger trigger, Scheduler scheduler)
			throws SchedulerException {
		return scheduler.getTriggerState(trigger.getKey()) == Trigger.TriggerState.PAUSED;
	}

	public void pauseJob(JobDetail jobDetail, Scheduler scheduler)
			throws SchedulerException {
		scheduler.pauseJob(jobDetail.getKey());
	}

	public void resumeJob(JobDetail jobDetail, Scheduler scheduler)
			throws SchedulerException {
		scheduler.resumeJob(jobDetail.getKey());
	}
}
