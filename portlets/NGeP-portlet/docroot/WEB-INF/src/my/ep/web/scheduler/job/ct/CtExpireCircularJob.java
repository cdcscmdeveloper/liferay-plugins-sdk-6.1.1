package my.ep.web.scheduler.job.ct;

import my.ep.biz.service.ct.ICtSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * This is scheduled job to expire the circular that no more effective.
 * 
 * @author NancyAng
 * @since 24/6/2015
 */

@DisallowConcurrentExecution
public class CtExpireCircularJob implements Job {
	
	public static Logger log = EpLogger.getLogger(CtExpireCircularJob.class);
	
	@SuppressWarnings("unused")
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		ICtSchedulerSO schedulerSO = null;
		try {
			schedulerSO = (ICtSchedulerSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, ICtSchedulerSO.class);			
			schedulerSO.updateExpiredCircular();
		} finally {
			schedulerSO = null;
		}
	}
}