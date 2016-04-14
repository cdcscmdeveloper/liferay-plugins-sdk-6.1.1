/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.scheduler.job.ntf;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.biz.service.ntf.scheduler.INtfMessageBulkSchedulerSO;
import my.ep.biz.service.ntf.scheduler.INtfMessageDirectEmailSchedulerSO;
import my.ep.biz.service.pm.scheduler.IPmSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class NtfMessageBulkJob implements Job {
	
	public static Logger log = EpLogger
			.getLogger(NtfMessageBulkJob.class);
	
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		INtfMessageBulkSchedulerSO iNtfMessageBulkSchedulerSO
		= EpEjbBusinessDelegate.getInstance()
		.getServiceObject(ModuleCodeConst.NTF_SCH,
				INtfMessageBulkSchedulerSO.class);
		
		iNtfMessageBulkSchedulerSO.retrieveDb();
		iNtfMessageBulkSchedulerSO.sendNotification();
		iNtfMessageBulkSchedulerSO.updateDb();
	}
}