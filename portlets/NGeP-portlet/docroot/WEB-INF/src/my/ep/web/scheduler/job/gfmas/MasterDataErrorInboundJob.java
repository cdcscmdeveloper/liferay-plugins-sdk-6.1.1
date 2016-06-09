package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
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
public class MasterDataErrorInboundJob implements Job {
	public static Logger log = EpLogger
			.getLogger(MasterDataErrorInboundJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("MasterDataErrorInboundJob start");
		IGfmasServiceSO gfmasServiceSO = null;
		try {
			gfmasServiceSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.INTEGRATION,
							IGfmasServiceSO.class);
			gfmasServiceSO.masterDataErrorInbound();
		} catch (Exception e) {
			log.info("MasterDataErrorInboundJob :" + e.getMessage());
		} finally {
			gfmasServiceSO = null;
		}

		log.info("MasterDataErrorInboundJob end");

	}
}
