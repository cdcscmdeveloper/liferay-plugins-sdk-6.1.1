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
 * Job Class to Publish all the pending publication qt's by building one job for QT
 * 
 * @author Venkat
 * @since 23.03.2016
 */
public class QTPublishSchedulingJob implements Job {

	private static Logger log = EpLogger.getLogger(QTPublishSchedulingJob.class);

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
			// Publish QT's scheduled for today and by this hour
			noticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			List<ScQt> publisingQts = noticeBoardSO
					.retriveQuotationTendersScheduledToPublish().getQtlist();
			if (!publisingQts.isEmpty()) {
				for (ScQt qtToPublish : publisingQts) {
						EpSchedulerHelper.scheduleSimplePublishingJob(qtToPublish);
				}
			}
		} catch (Exception e) {
			log.info("$$$$$ QT Publish Job run failed at " + new Date());
			log.error(e.getLocalizedMessage());
		} finally {
			noticeBoardSO = null;
		}

	}
}
