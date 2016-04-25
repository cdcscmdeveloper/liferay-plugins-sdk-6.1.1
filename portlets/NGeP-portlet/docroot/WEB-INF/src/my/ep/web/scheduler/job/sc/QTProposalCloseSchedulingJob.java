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

import java.util.Date;
import java.util.List;

import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job Class to Publish all the pending publication qt's and relative activities
 * 
 * @author Venkat
 * @since 23.03.2016
 */
public class QTProposalCloseSchedulingJob implements Job {

	private static Logger log = EpLogger.getLogger(QTProposalCloseSchedulingJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		debugLogger("Start execute ");

		IQTNoticeBoardSO noticeBoardSO;

		try {
			// Close QT's Which are scheduled today for Proposal Close
			noticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			List<ScQt> closedQts = noticeBoardSO
					.retriveQuationTendersToCloseProposal().getQtlist();

			if (!closedQts.isEmpty()) {
				for (ScQt closedQt : closedQts) {
					EpSchedulerHelper.scheduleSimpleQTClosingJob(closedQt);
				}
			}
			log.info("$$$$$ QT Close Job run sucessfully at " + new Date());
		} catch (Exception closeJobException) {
			log.info("$$$$$ QT Closure Job run failed at " + new Date());
			log.error(closeJobException.getLocalizedMessage());
		} finally {
			noticeBoardSO = null;
		}
		debugLogger("End execute ");

	}
}
