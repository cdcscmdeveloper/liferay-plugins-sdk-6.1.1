package my.ep.web.scheduler.job.phis;

import my.ep.web.integration.common.BatchLogger;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class PhisBatchSmOutboundJob implements Job {

	private static Logger log = BatchLogger.getLogger(PhisBatchSmOutboundJob.class);

	/*private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}*/

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info(" SM PHIS Servrice");
//		PhisIntegration phisIntegration = new PhisIntegration();
//
//		phisIntegration.masterDataMaterialInfoPHIS();

	}

}