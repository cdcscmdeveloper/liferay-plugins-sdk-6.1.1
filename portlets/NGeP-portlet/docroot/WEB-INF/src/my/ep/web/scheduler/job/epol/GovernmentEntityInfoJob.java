package my.ep.web.scheduler.job.epol;

import my.ep.biz.service.intg.epol.IEpolServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author Mohd Yusri
 */
@DisallowConcurrentExecution
public class GovernmentEntityInfoJob implements Job {

	public static Logger log = EpLogger.getLogger(GovernmentEntityInfoJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("EPOL GovernmentEntityInfoJob start");
		IEpolServiceSO epolServiceSO = null;
		try {
			epolServiceSO = EpEjbBusinessDelegate.getInstance().
					getServiceObject(ModuleCodeConst.INTEGRATION,
							IEpolServiceSO.class);
			epolServiceSO.createGovernmentEntityInfo();

			// please add resDTO to log better
			
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			// error handling follows here
		} finally {
			epolServiceSO = null;
		}
		log.info("EPOL GovernmentEntityInfoJob End");

	}

}
