/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.scheduler.job.ntf;

import my.ep.factory.EpLogger;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class NtfEmailRealTimeJob implements Job {
	
	public static Logger log = EpLogger.getLogger(NtfEmailRealTimeJob.class);
	
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.info("1");
	}
}