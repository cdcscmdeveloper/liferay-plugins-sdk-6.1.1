package my.ep.web.scheduler.job.phis;

import my.ep.biz.service.intg.phis.IPHISServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author P Ganga prasad
 * @author Venkat Chanumolu
 */
//Review this based on the requirement
@DisallowConcurrentExecution
public class MasterDataVotJob implements Job {

	public static Logger log = EpLogger.getLogger(MasterDataVotJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("MasterDataVotJob start");
		IPHISServiceSO phisServiceSO = null;
		try {
			phisServiceSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.INTEGRATION,
							IPHISServiceSO.class);
			phisServiceSO.votFund();
			// please add resDTO to log better

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			// error handling follows here
		} finally {
			phisServiceSO = null;
		}
		log.info("MasterDataVotJob end");

	}

}
