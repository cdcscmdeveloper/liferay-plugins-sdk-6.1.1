package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
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
public class MasterDataVendorOutboundJob implements Job {

	public static Logger log = EpLogger
			.getLogger(MasterDataVendorOutboundJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("MasterDataVendorOutboundJob start");
		IGfmasServiceSO  gfmasServiceSO;
		try {
			gfmasServiceSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.INTEGRATION,
							IGfmasServiceSO.class);
			
			gfmasServiceSO.masterDataVendorGfmasOutbound();
		} finally{
			gfmasServiceSO = null;
		}
		log.info("MasterDataVendorOutboundJob end");

	}

}
