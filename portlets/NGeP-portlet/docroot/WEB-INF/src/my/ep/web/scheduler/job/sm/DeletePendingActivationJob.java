package my.ep.web.scheduler.job.sm;

import my.ep.factory.EpLogger;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DeletePendingActivationJob implements Job {
	public static Logger log = EpLogger
			.getLogger(DeletePendingActivationJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("start executing delete pending job...");
		//TODO include business logic here
		log.info("finish executing delete pending job...");
	}

}
