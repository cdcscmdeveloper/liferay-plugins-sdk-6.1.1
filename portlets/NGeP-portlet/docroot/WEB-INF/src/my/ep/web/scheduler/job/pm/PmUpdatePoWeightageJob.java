package my.ep.web.scheduler.job.pm;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import my.ep.biz.service.pm.common.poroletask.IPoRoleTaskSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

@DisallowConcurrentExecution
public class PmUpdatePoWeightageJob implements Job {
	public static Logger log = EpLogger.getLogger(PmUpdatePoWeightageJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		IPoRoleTaskSO pmRoleTaskSO = null;
		try {
			pmRoleTaskSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IPoRoleTaskSO.class);
			pmRoleTaskSO.updatePoWeightage();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		} finally {
			pmRoleTaskSO = null;
		}
	}
}
