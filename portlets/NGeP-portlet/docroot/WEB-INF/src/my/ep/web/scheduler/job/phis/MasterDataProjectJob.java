package my.ep.web.scheduler.job.phis;

import my.ep.biz.service.intg.phis.IPHISServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author P Ganga prasad
 */
public class MasterDataProjectJob implements Job {

	public static Logger log = EpLogger.getLogger(MasterDataProjectJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("MasterDataProjectJob start");
		IPHISServiceSO  phisServiceSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.INTEGRATION,
						IPHISServiceSO.class);
		phisServiceSO.project();
		log.info("MasterDataProjectJob end");

	}

}
