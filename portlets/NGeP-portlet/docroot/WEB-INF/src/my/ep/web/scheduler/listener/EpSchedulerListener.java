package my.ep.web.scheduler.listener;

import my.ep.biz.service.quartz.IQuartzSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.quartz.QuartzReqDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.quartz.QuartzAuditLog;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;

/**
 * Listener to log every quartz job during started execution and after execution
 * 
 * @author Syazwan
 * 
 */
public class EpSchedulerListener implements JobListener {
	public static Logger log = EpLogger.getLogger(EpSchedulerHelper.class);
	public static final String LISTENER_NAME = "EP_JOB_LISTENER";

	@Override
	public String getName() {
		return LISTENER_NAME;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		try {
			String jobName = context.getJobDetail().getKey().toString();
			log.info("Job to be executed: " + jobName);
		} catch (Exception e) {
			log.error("ERROR JOB LISTENER JOBTOBEEXECUTED:" + e.getMessage());
		}
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		try {
			String jobName = context.getJobDetail().getKey().toString();
			log.info("Job execution vetoed:" + jobName);
		} catch (Exception e) {
			log.error("ERROR JOB LISTENER JOBEXECUTIONVETOED:" + e.getMessage());
		}
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		try {
			String errMsg = StringUtils.EMPTY;
			int seconds = (int) (context.getJobRunTime() / 1000) % 60;
			int minutes = (int) ((context.getJobRunTime() / (1000 * 60)) % 60);
			int hours = (int) ((context.getJobRunTime() / (1000 * 60 * 60)) % 24);
			String dateFormatted = hours + "hh :" + minutes + "mm:" + seconds
					+ "ss";
			if (seconds == 0 && minutes == 0 && hours == 0) {
				dateFormatted = context.getJobRunTime() + "ms";
			}
			String jobName = context.getJobDetail().getKey().toString();
			log.info("Job finish executed:" + jobName + " (" + dateFormatted
					+ ")");

			if (jobException != null
					&& !StringUtils.isBlank(jobException.getMessage())) {
				errMsg = jobException.getMessage();
				log.info("Exception thrown by job: " + jobName
						+ " with message: " + jobException.getMessage());
			}

			insertAuditLog(context.getJobDetail().getKey(), errMsg,
					context.getJobRunTime());

		} catch (Exception e) {
			log.error("ERROR JOB LISTENER JOBWASEXECUTED:" + e.getMessage());
		}
	}

	private IQuartzSO getQuartzSO() {
		return (IQuartzSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.QUARTZ, IQuartzSO.class);
	}

	private void insertAuditLog(JobKey jobKey, String errMsg,
			Long executionDuration) {
		QuartzAuditLog log = new QuartzAuditLog();
		log.setJobGroup(jobKey.getGroup());
		log.setJobName(jobKey.getName());
		log.setExecutionDuration(executionDuration);
		log.setErrMsg(errMsg);
		QuartzReqDTO reqDTO = new QuartzReqDTO();
		reqDTO.setQuartzAuditLog(log);
		getQuartzSO().saveAuditLog(reqDTO);
	}
}
