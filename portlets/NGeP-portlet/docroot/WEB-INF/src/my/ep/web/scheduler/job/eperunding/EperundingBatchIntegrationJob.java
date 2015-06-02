package my.ep.web.scheduler.job.eperunding;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.EperundingIntegration;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;



@DisallowConcurrentExecution
public class EperundingBatchIntegrationJob implements Job {
	
	public static Logger log = EpLogger.getLogger(EperundingBatchIntegrationJob.class);
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
	  log.info("Eperunding Servrice");
//	  EperundingIntegration eperundingIntegration = new EperundingIntegration();	
//	  eperundingIntegration.createFilesforEperunding();
       
		
	}

}