package my.ep.web.backingbean.sc.schedulermanager;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.schedulermanager.SchedulerManagerManagedBean;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.util.sc.helper.SchedulerHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.EpJobVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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

	public void preRenderSchedulerManager() {
		renewSesionObject();
		SmCommonUtil.setMDCUserId();
		initAllJobs();
		SmCommonUtil.redirectToPage("ePSchedulerManager.xhtml", "", null);
	}

	public void initAllJobs() {

		// loop jobs by group
		try {
			String jobAction = "";
			String jobState = "";
			Trigger trigger = null;

			getManagedBean().setEpSchedulerJobList(new ArrayList<EpJobVO>());
			getManagedBean().setJobGroups(
					EpSchedulerHelper.getScheduler().getJobGroupNames());

			for (String groupName : EpSchedulerHelper.getScheduler()
					.getJobGroupNames()) {

				// set search by group
				if (!StringUtils.isBlank(getManagedBean().getJobGroup())) {
					groupName = getManagedBean().getJobGroup();
				}

				// get jobkey
				for (JobKey jobKey : EpSchedulerHelper.getScheduler()
						.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

					// get job details
					JobDetail job = EpSchedulerHelper.getScheduler()
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

						getManagedBean().getEpSchedulerJobList().add(
								new EpJobVO(jobKey, trigger,
										getJobStatus(jobKey), jobAction,
										jobState, job.getDescription(), job
												.getJobClass()));

					}

				}

				// break loop if search by group
				if (!StringUtils.isBlank(getManagedBean().getJobGroup())) {
					break;
				}

			}

			// set bidding list
			getManagedBean().setCurrentDayBids(
					SchedulerHelper.getInstance().getJobBidScheduleList());

		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}

	public JobKey getJobKey(String jobName, String searchGroupName) {
		JobKey returnJobKey = null;
		try {
			for (String groupName : EpSchedulerHelper.getScheduler()
					.getJobGroupNames()) {

				if (searchGroupName.equalsIgnoreCase(groupName)) {
					for (JobKey jobKey : EpSchedulerHelper.getScheduler()
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
					EpSchedulerHelper.getScheduler().pauseJob(
							epJobVO.getJobKey());
				} else {
					EpSchedulerHelper.getScheduler().resumeJob(
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
			boolean success = EpSchedulerHelper.runBuildJobNow(epJobVO
					.getJobKey());
			if (!success) {
				throw new EpException(MessageConst.ERRQRTZ_00003);
			} else {
				initAllJobs();
			}
		} else {
			throw new EpException(MessageConst.ERRQRTZ_00001);
		}
	}

	public void deleteJob() {
		boolean success = EpSchedulerHelper
				.deleteJobAndAssociatedTriggers(getManagedBean()
						.getSelectedJobVO().getJobKey());
		if (!success) {
			throw new EpException(MessageConst.ERRQRTZ_00002);
		} else {
			initAllJobs();
		}
	}

	public void recoverJob(EpJobVO epJobVO) {
		try {
			if (EpSchedulerHelper.getScheduler()
					.getTriggerState(epJobVO.getTrigger().getKey()).toString()
					.equals(TriggerState.BLOCKED.toString())) {
				JobDetail jobDetail = EpSchedulerHelper.getScheduler()
						.getJobDetail(epJobVO.getJobKey());
				if (jobDetail != null && jobDetail.getJobBuilder() != null) {
					if (jobDetail.requestsRecovery()) {
						initAllJobs();
					} else {
						rescheduledJob(epJobVO);
					}
				}
			}

		} catch (SchedulerException e) {
			throw new EpException(MessageConst.ERRQRTZ_00001);
		}
	}

	public void rescheduledJob(EpJobVO epJobVO) {
		try {
			if (EpSchedulerHelper.getScheduler()
					.getTriggerState(epJobVO.getTrigger().getKey()).toString()
					.equals(TriggerState.COMPLETE.toString())
					|| EpSchedulerHelper.getScheduler()
							.getTriggerState(epJobVO.getTrigger().getKey())
							.toString().equals(TriggerState.BLOCKED.toString())) {
				JobDetail jobDetail = EpSchedulerHelper.getScheduler()
						.getJobDetail(epJobVO.getJobKey());
				if (jobDetail != null && jobDetail.getJobBuilder() != null) {
					EpSchedulerHelper.getScheduler()
							.rescheduleJob(epJobVO.getTrigger().getKey(),
									epJobVO.getTrigger());
					initAllJobs();
				}
			}

		} catch (SchedulerException e) {
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
