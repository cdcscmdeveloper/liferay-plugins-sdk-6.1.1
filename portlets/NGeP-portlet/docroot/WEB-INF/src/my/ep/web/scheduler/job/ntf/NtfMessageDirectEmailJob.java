package my.ep.web.scheduler.job.ntf;

import my.ep.biz.service.ntf.scheduler.INtfMessageBulkSchedulerSO;
import my.ep.biz.service.ntf.scheduler.INtfMessageDirectEmailSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class NtfMessageDirectEmailJob implements Job {

	public static Logger log = EpLogger
			.getLogger(NtfMessageDirectEmailJob.class);
	
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		try{
			
			INtfMessageDirectEmailSchedulerSO iNtfMessageDirectEmailSchedulerSO
			= EpEjbBusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.NTF_SCH,
					INtfMessageDirectEmailSchedulerSO.class);
			
			iNtfMessageDirectEmailSchedulerSO.retrieveDb();
			iNtfMessageDirectEmailSchedulerSO.sendNotification();
			iNtfMessageDirectEmailSchedulerSO.updateDb();
		
		} catch (Exception ex){
			log.error(ex);
		}
	}
}