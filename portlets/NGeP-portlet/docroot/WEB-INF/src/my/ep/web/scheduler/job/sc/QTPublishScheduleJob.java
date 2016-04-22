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

import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
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
public class QTPublishScheduleJob implements Job {

	private static Logger log = EpLogger.getLogger(QTPublishScheduleJob.class);

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

		IQTDetailSO qtDetailSO;
		try {
			// Publish QT's scheduled for today and by this hour
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			Long qtId = dataMap.getLong("qtId");;
			if(qtId != null){
				reqDTO.setQtId(qtId);
				QuationTenderResDTO resDTO = qtDetailSO
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
			qtDetailSO = null;
		}

	}
}
