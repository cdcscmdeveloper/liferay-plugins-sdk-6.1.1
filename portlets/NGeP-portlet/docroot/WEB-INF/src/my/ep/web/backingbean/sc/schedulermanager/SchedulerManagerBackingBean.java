package my.ep.web.backingbean.sc.schedulermanager;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.schedulermanager.SchedulerManagerManagedBean;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.util.sc.helper.SchedulerHelper;
import my.ep.web.vo.EpJobVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
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
		getManagedBean().setCurrentDayBids(
				SchedulerHelper.getInstance().getJobBidScheduleList());
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
			getManagedBean().setJobGroups(new ArrayList<String>());

			for (String groupName : getManagedBean().getScheduler()
					.getJobGroupNames()) {

				// get jobkey
				for (JobKey jobKey : getManagedBean().getScheduler()
						.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

					// get job details
					JobDetail job = getManagedBean().getScheduler()
							.getJobDetail(jobKey);

					// get job's trigger
					trigger = EpSchedulerHelper.getTriggerByJobKey(jobKey);

					if (job != null && trigger != null) {
						// get trigger state
						jobState = EpSchedulerHelper.getTriggerState(trigger);

						if (jobState.equals(TriggerState.PAUSED.toString())) {
							jobAction = SchedulerConstants.JOB_RESUME;
						} else {
							jobAction = SchedulerConstants.JOB_PAUSE;
						}

						// add group for filtering purposes
						if (!getManagedBean().getJobGroups().contains(
								jobKey.getGroup())) {
							getManagedBean().getJobGroups().add(
									jobKey.getGroup());
						}

						if (StringUtils.isBlank(getManagedBean().getJobGroup())) {
							getManagedBean().getEpSchedulerJobList().add(
									new EpJobVO(jobKey, trigger,
											getJobStatus(jobKey), jobAction,
											jobState, job.getDescription(), job
													.getJobClass()));
						} else {
							if (getManagedBean().getJobGroup().equals(
									jobKey.getGroup())) {
								getManagedBean().getEpSchedulerJobList().add(
										new EpJobVO(jobKey, trigger,
												getJobStatus(jobKey),
												jobAction, jobState, job
														.getDescription(), job
														.getJobClass()));
							}
						}

					}

				}

			}
			getManagedBean().setCurrentDayBids(
					SchedulerHelper.getInstance().getJobBidScheduleList());

		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}

	public JobKey getJobKey(String jobName, String searchGroupName) {
		JobKey returnJobKey = null;
		try {
			for (String groupName : getManagedBean().getScheduler()
					.getJobGroupNames()) {

				if (searchGroupName.equalsIgnoreCase(groupName)) {
					for (JobKey jobKey : getManagedBean().getScheduler()
							.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

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

	public void pauseOrResumeJob(EpJobVO epJobVO) {
		try {
			if (!EpSchedulerHelper.isJobRunningNow(epJobVO.getJobKey())) {
				if (epJobVO.getAction().equals(SchedulerConstants.JOB_PAUSE)) {
					getManagedBean().getScheduler().pauseJob(
							epJobVO.getJobKey());
				} else {
					getManagedBean().getScheduler().resumeJob(
							epJobVO.getJobKey());
				}
				initAllJobs();
			} else {
				throw new EpException(MessageConst.ERRQRTZ_00001);
			}
		} catch (SchedulerException e) {
			throw new EpException(MessageConst.ERRQRTZ_00003);
		}
	}

	public void runNowJob(EpJobVO epJobVO) {
		if (!EpSchedulerHelper.isJobRunningNow(epJobVO.getJobKey())) {
			boolean success = EpSchedulerHelper.runJobOnce(epJobVO.getJobKey(),
					epJobVO.getJobClass());
			if (success) {
				initAllJobs();
			} else {
				throw new EpException(MessageConst.ERRQRTZ_00003);
			}
		} else {
			throw new EpException(MessageConst.ERRQRTZ_00001);
		}
	}

	public void deleteJob() {
		if (!EpSchedulerHelper.isJobRunningNow(getManagedBean()
				.getSelectedJobVO().getJobKey())) {
			boolean success = EpSchedulerHelper
					.deleteJobAndAssociatedTriggers(getManagedBean()
							.getSelectedJobVO().getJobKey());
			if (success) {
				initAllJobs();
			} else {
				throw new EpException(MessageConst.ERRQRTZ_00002);
			}
		} else {
			throw new EpException(MessageConst.ERRQRTZ_00001);
		}
	}

	public void onDeleteJob(EpJobVO epJobVO) {
		getManagedBean().setSelectedJobVO(epJobVO);
		RequestContext.getCurrentInstance().execute(
				"confirmDeleteDialog.show();");
	}

	private String getJobStatus(JobKey jobKey) {
		String retVal = SchedulerConstants.JOB_WAIT;
		if (EpSchedulerHelper.isJobRunningNow(jobKey)) {
			retVal = SchedulerConstants.JOB_RUN;
		}
		return retVal;
	}

}
