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
public class MasterDataMaterialInformationJob implements Job {

	public static Logger log = EpLogger
			.getLogger(MasterDataMaterialInformationJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("MasterDataMaterialInformationJob start");
		IPHISServiceSO  phisServiceSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.INTEGRATION,
						IPHISServiceSO.class);
		phisServiceSO.masterDataMaterialInfoPHIS();
		log.info("MasterDataMaterialInformationJob end");

	}

}
