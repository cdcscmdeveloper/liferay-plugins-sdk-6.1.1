package my.ep.web.scheduler.job.epol;

import my.ep.biz.service.intg.epol.IEpolServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author Mohd Yusri
 */
public class GovernmentUserInfoJob implements Job {

	public static Logger log = EpLogger
			.getLogger(GovernmentUserInfoJob.class);

	public static IEpolServiceSO epolServiceSO = EpEjbBusinessDelegate
			.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION,
					IEpolServiceSO.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("EPOL GovernmentUserInfoJob start");
		epolServiceSO.createGovernmentEntityInfo();

		log.info("EPOL GovernmentUserInfoJob End");

	}

}
