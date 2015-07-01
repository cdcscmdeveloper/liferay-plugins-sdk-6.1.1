package my.ep.web.scheduler.job.phis;

import my.ep.web.integration.common.BatchLogger;
import my.ep.web.integration.common.PhisIntegration;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class PhisBatchPmOutboundJob implements Job {

	private static Logger log = BatchLogger.getLogger(PhisBatchPmOutboundJob.class);

	/*private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}*/

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("PHIS : PhisBatchPmOutboundJob start ");
		PhisIntegration phisIntegration = new PhisIntegration();
		phisIntegration.votFund();
		phisIntegration.votFundDana();
		phisIntegration.glAccount();
		phisIntegration.programActivity();
		phisIntegration.project();
		log.info("PHIS : PhisBatchPmOutboundJob end ");

	}

}