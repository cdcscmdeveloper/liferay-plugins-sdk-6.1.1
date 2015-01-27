package my.ep.web.scheduler.job.gfmas;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.GfmasIntegration;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Venkat Chanumolu
 */

@DisallowConcurrentExecution
public class GfmasBatchIntegrationJob implements Job {

	public static Logger log = EpLogger
			.getLogger(GfmasBatchIntegrationJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GfmasBatchIntegrationJob ");

		GfmasIntegration gfmasIntegration = new GfmasIntegration();
		 gfmasIntegration.sstInformationGfmas();
		// gfmasIntegration.kodAkaunInPutStream();

	}

}