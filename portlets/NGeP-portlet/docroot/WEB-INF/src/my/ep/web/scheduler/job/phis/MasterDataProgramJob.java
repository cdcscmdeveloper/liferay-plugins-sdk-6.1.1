package my.ep.web.scheduler.job.phis;

import my.ep.biz.service.intg.phis.IPHISServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.integration.common.PhisIntegration;
import my.ep.web.integration.common.PhisTemplate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author P Ganga prasad
 */
public class MasterDataProgramJob implements Job {

	public static Logger log = EpLogger.getLogger(MasterDataProgramJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("MasterDataProgramJob start");
		IPHISServiceSO  phisServiceSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.INTEGRATION,
						IPHISServiceSO.class);
		phisServiceSO.programActivity();
		log.info("MasterDataProgramJob end");

	}

}
