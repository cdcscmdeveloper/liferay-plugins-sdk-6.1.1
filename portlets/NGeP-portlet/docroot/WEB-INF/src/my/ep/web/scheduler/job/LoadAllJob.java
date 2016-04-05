package my.ep.web.scheduler.job;

import my.ep.factory.EpLogger;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LoadAllJob implements Job {
	public static Logger log = EpLogger.getLogger(LoadAllJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("Do nothing...");

	}
}
