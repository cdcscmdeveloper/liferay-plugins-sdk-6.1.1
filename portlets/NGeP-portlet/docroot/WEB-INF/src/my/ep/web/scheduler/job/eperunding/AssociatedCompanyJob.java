package my.ep.web.scheduler.job.eperunding;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.EperundingIntegration;
import my.ep.web.integration.common.EperundingTemplate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AssociatedCompanyJob implements Job {

	public static Logger log = EpLogger
			.getLogger(AssociatedCompanyJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("AssociatedCompanyJob start");
		EperundingTemplate eperundingTemplate = new EperundingIntegration();
			eperundingTemplate.createAssociatedCompany();
		log.info("AssociatedCompanyJob end");

	}

}
