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
 * PmConfigRulesJob is a class that provides service object for Config Rules
 * 
 * @author Ken
 * @since 07.04.2016
 * 
 */
/*
 * This is a cronjob and is responsible for clear Config Rules Map
 * This can have only one instance and executes only once.
 */
@DisallowConcurrentExecution
public class PmConfigRulesJob implements Job {
	
	public static Logger log = EpLogger.getLogger(PmConfigRulesJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		IPmSchedulerSO schedulerSO = null;
		try {
			schedulerSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IPmSchedulerSO.class);			
			schedulerSO.resetRulesConfigMap();
		} finally {
			schedulerSO = null;
		}
	}

}