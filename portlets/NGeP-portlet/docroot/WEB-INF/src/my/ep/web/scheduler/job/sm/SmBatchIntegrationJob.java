package my.ep.web.scheduler.job.sm;

import my.ep.factory.EpLogger;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Venkat Chanumolu
 */


@DisallowConcurrentExecution
public class SmBatchIntegrationJob implements Job {
	
	public static Logger log = EpLogger.getLogger(SmBatchIntegrationJob.class);
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		
	}

}