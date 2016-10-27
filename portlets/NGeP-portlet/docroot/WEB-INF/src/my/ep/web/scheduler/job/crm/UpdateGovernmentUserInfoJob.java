package my.ep.web.scheduler.job.crm;

import my.ep.biz.service.intg.crm.ICRMServiceSO;
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
public class UpdateGovernmentUserInfoJob implements Job {

	public static Logger log = EpLogger
			.getLogger(UpdateGovernmentUserInfoJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("UpdateGovernmentUserInfoJob start");
		ICRMServiceSO crmServiceSO = null;
		try {
			crmServiceSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.INTEGRATION,
							ICRMServiceSO.class);
			crmServiceSO.updateGovernmentUserInfoService();
			// please add resDTO to log better
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			// more error handling code follows here
		} finally {
			crmServiceSO = null;
		}

		log.info("UpdateGovernmentUserInfoJob End");

	}

}
