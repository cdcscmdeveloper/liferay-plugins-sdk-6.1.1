package my.ep.web.backingbean.sc.schedulermanager;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.schedulermanager.SchedulerManagerManagedBean;
import my.ep.web.scheduler.constants.sc.ebidding.EPSchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.sc.ebidding.util.SchedulerFactoryUtil;
import my.ep.web.util.sc.helper.SchedulerHelper;
import my.ep.web.vo.EpJobVO;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;

@ManagedBean(name = "schedulerManagerBB")
@RequestScoped
public class SchedulerManagerBackingBean extends
		BaseBackingBean<SchedulerManagerManagedBean> {

	public static Logger log = EpLogger
			.getLogger(SchedulerManagerBackingBean.class);

	public SchedulerManagerBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
		initAllJobs();
		getManagedBean().setCurrentDayBids(SchedulerHelper.getInstance().getJobBidScheduleList());
	}

	public void initAllJobs() {

		// loop jobs by group
		try {
			String jobAction = "";
			String jobState = "";
			Trigger trigger = null;
			if (getManagedBean().getScheduler() == null) {
				getManagedBean().setScheduler(EpSchedulerHelper.getScheduler());
			}
			getManagedBean().setEpSchedulerJobList(new ArrayList<EpJobVO>());

			for (String groupName : getManagedBean().getScheduler()
					.getJobGroupNames()) {

				// get jobkey
				for (JobKey jobKey : getManagedBean().getScheduler()
						.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

					// get job's trigger
					trigger = EpSchedulerHelper.getTriggerByJobKey(jobKey);

					// get trigger state
					jobState = EpSchedulerHelper.getTriggerState(trigger);

					if (jobState.equals(TriggerState.PAUSED.toString())) {
						jobAction = EPSchedulerConstants.JOB_RESUME;
					} else {
						jobAction = EPSchedulerConstants.JOB_PAUSE;
					}

					getManagedBean().getEpSchedulerJobList().add(
							new EpJobVO(jobKey, trigger, getJobStatus(jobKey),
									jobAction, jobState, getManagedBean()
											.getScheduler()
											.getJobDetail(jobKey)
											.getDescription()));

				}

			}

		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}

	public void pauseOrResumeJob(EpJobVO epJobVO) {
		try {
			if (epJobVO.getAction().equals(EPSchedulerConstants.JOB_PAUSE)) {
				getManagedBean().getScheduler().pauseJob(epJobVO.getJobKey());
			} else {
				getManagedBean().getScheduler().resumeJob(epJobVO.getJobKey());
			}
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}

	public void runNowJob(EpJobVO epJobVO) {

		if (epJobVO.getStatus().equals(EPSchedulerConstants.JOB_WAIT)) {
			// schedule the job
			EpSchedulerHelper.runBuildJobNow(epJobVO.getJobKey());
		}

	}

	private String getJobStatus(JobKey jobKey) {
		String retVal = EPSchedulerConstants.JOB_WAIT;
		if (EpSchedulerHelper.isJobRunningNow(jobKey)) {
			retVal = EPSchedulerConstants.JOB_RUN;
		}
		return retVal;
	}


}
