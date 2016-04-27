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

import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job Class to Close QT for proposal
 * 
 * @author Venkat
 * @since 23.03.2016
 */
public class QTProposalCloseJob implements Job {

	private static Logger log = EpLogger.getLogger(QTProposalCloseJob.class);

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
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		debugLogger("Start execute ");

		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		Long qtId = dataMap.getLong("qtId");

		IQTProposalSO proposalSO;

		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
			proposalReqDTO.setQtId(qtId);
			ProposalResDTO proposalResDTO = proposalSO
					.updateProposalWorkFlowStatus(proposalReqDTO);
			if (proposalResDTO.isSuccessful()) {
				log.info("$$$$$ qt Work Flow status updated suceessfully from QTProposalCloseJob job");
			} else {
				log.info("$$$$$ qt Work Flow status updated failed from QTProposalCloseJob job");
			}

		} catch (Exception e) {
			log.info("$$$$$ qt Work Flow status updated failed from QTProposalCloseJob job");
			log.error(e.getLocalizedMessage());
		} finally {
			proposalSO = null;
		}

		IQTNoticeBoardSO noticeBoardSO;

		try {
			// Close QT's Which are scheduled today for Proposal Close
			noticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);

			NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
			reqDTO.setQtId(qtId);
			NoticeBoardResDTO resDTO = noticeBoardSO
					.closeQtAndSaveProposalAmount(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("$$$$$ Closing Successful for the QT No :" + qtId);
			} else {
				log.info("$$$$$ Closing Failed for the QT No :" + qtId);
			}

			log.info("$$$$$ QT Close Job run sucessfully at "
					+ DateUtils.getCurrentDate());
		} catch (Exception closeJobException) {
			log.info("$$$$$ QT Closure Job run failed at "
					+ DateUtils.getCurrentDate());
			log.error(closeJobException.getLocalizedMessage());
		} finally {
			noticeBoardSO = null;
		}
		debugLogger("End execute ");

	}
}
