package my.ep.web.scheduler.job.phis;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.PhisIntegration;
import my.ep.web.integration.common.PhisTemplate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author P Ganga prasad
 */
public class MasterDataMaterialInformationJob implements Job {

	public static Logger log = EpLogger
			.getLogger(MasterDataMaterialInformationJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("MasterDataMaterialInformationJob start");
		PhisIntegration phisTemplate = new PhisIntegration();
		phisTemplate.masterDataMaterialInfoPHIS();
		log.info("MasterDataMaterialInformationJob end");

	}

}
