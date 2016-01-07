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
public class MasterDataVotJob implements Job {

	public static Logger log = EpLogger.getLogger(MasterDataVotJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("MasterDataVotJob start");
		PhisTemplate phisTemplate = new PhisIntegration();
		phisTemplate.votFund();
		log.info("MasterDataVotJob end");

	}

}
