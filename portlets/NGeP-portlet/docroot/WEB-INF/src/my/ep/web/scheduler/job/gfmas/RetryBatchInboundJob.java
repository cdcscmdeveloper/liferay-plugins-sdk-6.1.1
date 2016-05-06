package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RetryBatchInboundJob implements Job {

	public static Logger log = EpLogger
			.getLogger(RetryBatchInboundJob.class);

	public static IGfmasServiceSO gfmasServiceSO = EpEjbBusinessDelegate
			.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION,
					IGfmasServiceSO.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("RetryBatchInboundJob start");
		try {

			gfmasServiceSO.retryGfmasBatchInboundFiles();
		} catch (Exception e) {
			log.info("RetryBatchInboundJob :" + e.getMessage());
		}
		log.info("RetryBatchInboundJob end");

	}

}
