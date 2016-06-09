package my.ep.web.scheduler.job.cm;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author hktan
 */

@DisallowConcurrentExecution
public class CmUpdateExpiredItemJob implements Job {

	public static Logger log = EpLogger.getLogger(CmUpdateExpiredItemJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		ICatalogueSO reqSO = null;
		try {
			reqSO = (ICatalogueSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
							ICatalogueSO.class);
			reqSO.updateExpiredItem();
			// please add resDTO to log better

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			// error handling follows here
		} finally {
			reqSO = null;
		}

	}

}