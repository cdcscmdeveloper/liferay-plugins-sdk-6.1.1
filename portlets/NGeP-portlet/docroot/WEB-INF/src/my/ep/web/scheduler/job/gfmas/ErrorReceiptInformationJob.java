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
public class ErrorReceiptInformationJob implements Job {

	public static Logger log = EpLogger
			.getLogger(ErrorReceiptInformationJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.info("ErrorReceiptInformationJob start");
		IGfmasServiceSO gfmasServiceSO = null;
		try {
			gfmasServiceSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.INTEGRATION,
							IGfmasServiceSO.class);
			gfmasServiceSO.errorReceiptInformationGfmasInbound();
		} catch (Exception e) {
			log.info("ReceiptInformationErrorJob :" + e.getMessage());
		} finally {
			gfmasServiceSO = null;
		}
		log.info("ErrorReceiptInformationJob end");

	}

}
