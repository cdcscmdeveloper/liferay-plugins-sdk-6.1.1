package my.ep.web.scheduler.job.eperunding;

import my.ep.biz.service.intg.eperunding.IEperundingServiceSO;
import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.integration.common.EperundingIntegration;
import my.ep.web.integration.common.EperundingTemplate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ConsultantProfileJob implements Job {

	public static Logger log = EpLogger.getLogger(ConsultantProfileJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("ConsultantProfileJob start");
		IEperundingServiceSO  eperundingServiceSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.INTEGRATION,
						IEperundingServiceSO.class);
		eperundingServiceSO.createConsultantProfile();
		log.info("ConsultantProfileJob end");

	}

}
