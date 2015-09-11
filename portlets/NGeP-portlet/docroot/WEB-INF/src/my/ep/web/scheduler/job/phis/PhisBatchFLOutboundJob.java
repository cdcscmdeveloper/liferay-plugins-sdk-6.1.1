
package my.ep.web.scheduler.job.phis;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.PhisIntegration;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class PhisBatchFLOutboundJob implements Job {

	private static Logger log = EpLogger.getLogger(PhisBatchFLOutboundJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("PHIS : PhisBatchFlOutboundJob start ");
		PhisIntegration phisIntegration = new PhisIntegration();
		phisIntegration.paymentEffectNotification();
		log.info("PHIS : PhisBatchFlOutboundJob end ");

	}

}