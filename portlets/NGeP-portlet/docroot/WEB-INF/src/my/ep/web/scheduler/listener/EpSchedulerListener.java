package my.ep.web.scheduler.listener;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.helper.EpSchedulerHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
		String jobName = context.getJobDetail().getKey().toString();
		log.info("**********************************Job to be executed ["
				+ jobName + "]**********************************");

	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		String jobName = context.getJobDetail().getKey().toString();
		log.info("**********************************Job execution vetoed["
				+ jobName + "]**********************************");
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		int seconds = (int) (context.getJobRunTime() / 1000) % 60 ;
		int minutes = (int) ((context.getJobRunTime() / (1000*60)) % 60);
		int hours   = (int) ((context.getJobRunTime() / (1000*60*60)) % 24);
		String dateFormatted = hours+":"+minutes+":"+seconds;
		String jobName = context.getJobDetail().getKey().toString();
		log.info("**********************************Job [" + jobName
				+ "] finish executed with duration of [" + dateFormatted
				+ "]**********************************");

		if (jobException != null
				&& !StringUtils.isBlank(jobException.getMessage())) {
			log.info("**********************************Exception thrown by job: "
					+ jobName
					+ " with message: "
					+ jobException.getMessage()
					+ "!!!!!!!!!!!!!!!!!");
		}

	}
}
