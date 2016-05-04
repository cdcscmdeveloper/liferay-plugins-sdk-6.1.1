/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.scheduler.job.ntf;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.biz.service.ntf.scheduler.INtfEmailBulkSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class NtfEmailBulkJob implements Job {
	
	public static Logger log = EpLogger.getLogger(NtfEmailBulkJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		INtfEmailBulkSchedulerSO iNtfEmailBulkSchedulerSO
		= EpEjbBusinessDelegate.getInstance()
		.getServiceObject(ModuleCodeConst.NTF_SCH,
				INtfEmailBulkSchedulerSO.class);
		
		iNtfEmailBulkSchedulerSO.retrieveDb();
		iNtfEmailBulkSchedulerSO.sendEmail();
		iNtfEmailBulkSchedulerSO.updateDb();
		
	}
}