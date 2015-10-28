package my.ep.web.scheduler.job.gfmas;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.BatchTemplate;
import my.ep.web.integration.common.GfmasIntegration;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class GfmasBatchSmOutboundJob implements Job {

	public static Logger log = EpLogger
			.getLogger(GfmasBatchSmOutboundJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GfmasBatchSmOutboundJob APIVE :start");

		BatchTemplate batchTemplate = new GfmasIntegration();
		batchTemplate.masterDataVendorGfmasOutbound();
		
		log.info("GfmasBatchSmOutboundJob APIVE :end");

	}

}