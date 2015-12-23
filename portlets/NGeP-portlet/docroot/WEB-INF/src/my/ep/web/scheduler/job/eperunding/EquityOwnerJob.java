package my.ep.web.scheduler.job.eperunding;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.EperundingIntegration;
import my.ep.web.integration.common.EperundingTemplate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class EquityOwnerJob implements Job {

	public static Logger log = EpLogger.getLogger(EquityOwnerJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("EquityOwnerJob start");
		EperundingTemplate eperundingTemplate = new EperundingIntegration();
		eperundingTemplate.createEquiytyOwner();
		log.info("EquityOwnerJob end");

	}

}
