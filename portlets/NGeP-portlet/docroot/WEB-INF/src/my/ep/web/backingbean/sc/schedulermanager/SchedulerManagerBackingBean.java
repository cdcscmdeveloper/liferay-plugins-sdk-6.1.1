package my.ep.web.backingbean.sc.schedulermanager;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.schedulermanager.SchedulerManagerManagedBean;
import my.ep.web.scheduler.constants.sc.ebidding.EPSchedulerConstants;
import my.ep.web.scheduler.sc.ebidding.util.SchedulerFactoryUtil;
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
		BaseBackingBean<SchedulerManagerManagedBean > {

	public static Logger log = EpLogger
			.getLogger(SchedulerManagerBackingBean.class);

	public SchedulerManagerBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
		initAllJobs();
	}

	public void initAllJobs() {

		// loop jobs by group
		try {
			String jobAction = "";
			String jobState = "";
			Trigger trigger = null;
			if (getManagedBean().getScheduler() == null) {
				getManagedBean().setScheduler( SchedulerFactoryUtil.getFacesContextSchedulerFactory());
			}
			getManagedBean().setEpSchedulerJobList(new ArrayList<EpJobVO>());

			for (String groupName : getManagedBean().getScheduler().getJobGroupNames()) {

				// get jobkey
				for (JobKey jobKey : getManagedBean().getScheduler().getJobKeys(GroupMatcher
						.jobGroupEquals(groupName))) {

					// get job's trigger
					trigger = getTrigger(jobKey);
					
					//get trigger state
					jobState = getTriggerState(trigger);

					if (jobState.equals(TriggerState.PAUSED.toString())) {
						jobAction = EPSchedulerConstants.JOB_RESUME;
					} else {
						jobAction = EPSchedulerConstants.JOB_PAUSE;
					}

					getManagedBean().getEpSchedulerJobList().add(
							new EpJobVO(jobKey, trigger, getJobStatus(jobKey), jobAction,
									jobState,getManagedBean().getScheduler().getJobDetail(jobKey).getDescription()));

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
		try {
			if (epJobVO.getStatus().equals(EPSchedulerConstants.JOB_WAIT)) {

				// specify trigger
				Trigger trigger = TriggerBuilder.newTrigger().startNow()
						.forJob(getManagedBean().getScheduler().getJobDetail(epJobVO.getJobKey()))
						.build();

				// schedule the job
				getManagedBean().getScheduler().scheduleJob(trigger);
			}
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}

	private boolean jobRun(JobKey jobKey) {
		boolean retVal = false;

		try {
			for (JobExecutionContext jobExecute : getManagedBean().getScheduler()
					.getCurrentlyExecutingJobs()) {
				if (jobExecute.getJobDetail().getKey().equals(jobKey)) {
					retVal = true;
					break;
				}
			}
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}

		return retVal;
	}

	private String getJobStatus(JobKey jobKey) {
		String retVal = EPSchedulerConstants.JOB_WAIT;
		if (jobRun(jobKey)) {
			retVal = EPSchedulerConstants.JOB_RUN;
		}
		return retVal;
	}

	private Trigger getTrigger(JobKey jobKey) throws SchedulerException {
		return (Trigger) getManagedBean().getScheduler().getTriggersOfJob(jobKey).get(0);
	}

	private String getTriggerState(Trigger trigger) {
		String state = TriggerState.NONE.toString();
		try {
			state = getManagedBean().getScheduler().getTriggerState(trigger.getKey()).toString();
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}

		return state;
	}

}
