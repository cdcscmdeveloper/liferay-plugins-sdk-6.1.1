package my.ep.web.scheduler.job.gfmas;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.BatchTemplate;
import my.ep.web.integration.common.GfmasIntegration;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author P Ganga prasad
 */
public class MasterDataVendorOutboundJob implements Job {

	public static Logger log = EpLogger
			.getLogger(MasterDataVendorOutboundJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("MasterDataVendorOutboundJob start");
		BatchTemplate batchTemplate = new GfmasIntegration();
		batchTemplate.masterDataVendorGfmasOutbound();
		log.info("MasterDataVendorOutboundJob end");

	}

}
