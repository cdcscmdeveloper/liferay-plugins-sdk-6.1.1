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
 */
@DisallowConcurrentExecution
public class ReceiptInformationJob implements Job {

	public static Logger log = EpLogger.getLogger(ReceiptInformationJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("ReceiptInformationJob start");
		IGfmasServiceSO  gfmasServiceSO;
		try {
			gfmasServiceSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.INTEGRATION,
							IGfmasServiceSO.class);
			
//		gfmasServiceSO.receiptInformationGfmasOutbound();
		} catch (Exception e) {
			gfmasServiceSO = null;
		}
		log.info("ReceiptInformationJob end");

	}

}
