package my.ep.web.scheduler.job.eperunding;

import my.ep.biz.service.intg.eperunding.IEperundingServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ManagementPersonnelJob implements Job {

	public static Logger log = EpLogger.getLogger(ManagementPersonnelJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("ManagementPersonnelJob start");
		IEperundingServiceSO  eperundingServiceSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.INTEGRATION,
						IEperundingServiceSO.class);
		eperundingServiceSO.createManagementPersonnel();
		log.info("ManagementPersonnelJob end");

	}

}
