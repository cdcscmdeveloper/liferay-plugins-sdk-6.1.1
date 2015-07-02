/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.scheduler.job.sc;

import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Class to close all the published qt for testing purpose
 * 
 * @author Kenny
 * @since 01.07.2015
 */
public class CloseQTSchedulerJob implements Job {

	private static Logger log = EpLogger
			.getLogger(CloseQTSchedulerJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		debugLogger("Start execute ");
		
		IQTProposalSO qtProposalSO;
		
		try{
			qtProposalSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
		
			qtProposalSO.closeProposalPublished();
			
		} finally {
			qtProposalSO = null;
		}
		debugLogger("End execute ");

	}

}
