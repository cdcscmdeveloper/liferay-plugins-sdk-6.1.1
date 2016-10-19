package my.ep.web.scheduler.job.spa;

import my.ep.biz.service.intg.spa.ISPAServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author hktan
 * @author hktan
 */
//Review this based on the requirement
@DisallowConcurrentExecution
public class ColorSpaJob implements Job {

	public static Logger log = EpLogger.getLogger(ColorSpaJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("ColorSpaJob start");
		ISPAServiceSO spaServiceSO = null;
		try {
			spaServiceSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.INTEGRATION,
							ISPAServiceSO.class);
			spaServiceSO.masterDataMaterialColorSpaOutbound();
			// please add resDTO to log better

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			// error handling follows here
		} finally {
			spaServiceSO = null;
		}
		log.info("ColorSpaJob end");

	}

}
