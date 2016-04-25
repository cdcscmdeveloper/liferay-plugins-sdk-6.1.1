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

import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job Class to Publish QT
 * 
 * @author Venkat
 * @since 23.03.2016
 */
public class QTPublishJob implements Job {

	private static Logger log = EpLogger.getLogger(QTPublishJob.class);

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
	public void execute(JobExecutionContext context) throws JobExecutionException {
		debugLogger("Start execute ");

		IQTNoticeBoardSO noticeBoardSO;
		try {
			// Publish QT's scheduled for today and by this hour
			noticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			Long qtId = dataMap.getLong("qtId");
			
			if(qtId != null){
				reqDTO.setQtId(qtId);
				NoticeBoardResDTO resDTO = noticeBoardSO
						.publishQtAndInsertSuppliers(reqDTO);

				if (resDTO.isSuccessful()) {
					log.info("##### Publishing Successful for the QT Id :"
							+ qtId);
				} else {
					log.info("##### Publishing Failed for the QT Id :"
							+ qtId);
				}
			}else{
				log.info("$$$$$ QT Publish Job run failed at " + new Date());
			}
			
		} catch (Exception e) {
			log.info("$$$$$ QT Publish Job run failed at " + new Date());
			log.error(e.getLocalizedMessage());
		} finally {
			noticeBoardSO = null;
		}

	}
}
