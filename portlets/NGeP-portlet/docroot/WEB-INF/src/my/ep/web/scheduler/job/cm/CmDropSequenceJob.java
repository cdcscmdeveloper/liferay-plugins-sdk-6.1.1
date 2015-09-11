package my.ep.web.scheduler.job.cm;

import my.ep.biz.service.cm.codification.ICodificationSO;
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
public class CmDropSequenceJob implements Job {
	
	public static Logger log = EpLogger.getLogger(CmDropSequenceJob.class);
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
	  
		ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
						ICodificationSO.class);
		
		
		reqSO.dropSequenceNo();
		
		
	}

}