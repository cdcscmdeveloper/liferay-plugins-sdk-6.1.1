/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.scheduler.job.pm;

import my.ep.biz.service.pm.scheduler.IPmSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * AccessApplSO is a class that provides service object for Access Application.
 * 
 * @author Ken
 * @since 02.02.2016
 * 
 */
/*
 * This is a cronjob and is responsible for send reminder (7 days before the access application expired) 
 * This can have only one instance and executes only once.
 */
@DisallowConcurrentExecution
public class PmAccessApplReminderJob implements Job {
	
	public static Logger log = EpLogger.getLogger(PmAccessApplReminderJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		IPmSchedulerSO schedulerSO = null;
		try {
			schedulerSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IPmSchedulerSO.class);			
			schedulerSO.sendAccessApplicationReminder();
		} finally {
			schedulerSO = null;
		}
	}

}