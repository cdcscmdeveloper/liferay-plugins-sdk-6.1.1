package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.integration.common.BatchTemplate;
import my.ep.web.integration.common.GfmasIntegration;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author P Ganga prasad
 */

@DisallowConcurrentExecution
public class MasterDataVendorOutboundJob implements Job {

	public static Logger log = EpLogger
			.getLogger(MasterDataVendorOutboundJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

//		log.info("MasterDataVendorOutboundJob start");
//		IGfmasServiceSO  gfmasServiceSO;
//		try {
//			  gfmasServiceSO = EpEjbBusinessDelegate.getInstance()
//					.getServiceObject(ModuleCodeConst.INTEGRATION,
//							IGfmasServiceSO.class);
//			
//			gfmasServiceSO.masterDataVendorGfmasOutbound();
//		} finally{
//			gfmasServiceSO = null;
//		}
		
		BatchTemplate integration = new GfmasIntegration();
		integration.masterDataVendorGfmasOutbound();
		log.info("MasterDataVendorOutboundJob end");

	}

}
