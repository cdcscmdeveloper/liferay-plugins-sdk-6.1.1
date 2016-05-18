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
import my.ep.constant.sc.QTDisruptionConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.BpmEndpointConst;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.exception.ExceptionUtils;
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

	private IQTProposalSO proposalSO;
	private IQTNoticeBoardSO noticeBoardSO;

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

		try {

			if (proposalSO == null) {
				proposalSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTProposalSO.class);
			}
			ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
			proposalReqDTO.setQtId(qtId);
			ProposalResDTO proposalResDTO = proposalSO
					.updateProposalWorkFlowStatus(proposalReqDTO);
			if (proposalResDTO.isSuccessful()) {
				log.info("$$$$$ qt Work Flow status updated suceessfull for Publish");
				insertSuppliersAndSendNotification(qtId);
			} else {
				log.info("$$$$$ qt Work Flow status updated unsuceessfull for Publish");
				updateQtAsDisrupted(qtId,
						QTDisruptionConst.QT_PUBLISH_WORK_FLOW_DISRUPTION,
						proposalResDTO.getErrorMessage());
			}

		} catch (Exception e) {
			log.info("$$$$$ qt Work Flow status updated failed for Publish");
			log.error(e.getLocalizedMessage());
			reSchedulePublicationForPublishFailQt(qtId);
		}

		finally {
			proposalSO = null;
			noticeBoardSO = null;
		}

	}

	private void insertSuppliersAndSendNotification(Long qtId) {
		// Insert suppliers and send notification after work flow status update successful
		try {
			if (noticeBoardSO == null) {
				noticeBoardSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTNoticeBoardSO.class);
			}

			NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
			if (qtId != null) {
				reqDTO.setQtId(qtId);
				NoticeBoardResDTO resDTO = noticeBoardSO
						.publishQtAndInsertSuppliers(reqDTO);

				if (resDTO.isSuccessful()) {
					log.info("$$$$$ Insert Supplier and send notification successful for the QT Id :"
							+ qtId);
					updateQtPublishBpmTask(qtId);
				} else {
					log.info("$$$$$ Insert Supplier and send notification unsuccessful for the QT Id :"
							+ qtId);
					updateQtAsDisrupted(
							qtId,
							QTDisruptionConst.QT_PUBLISH_SUPPLIER_INSERT_DISRUPTION,
							resDTO.getErrorMessage());
				}
			}
		} catch (Exception e) {
			log.info("$$$$$ Insert Supplier and send notification failed at"
					+ DateUtils.getCurrentDate());
			log.error(e.getLocalizedMessage());
			String errorMessage = null;
			Throwable rootCause = ExceptionUtils.getRootCause(e);
    		if (rootCause != null) {
    			errorMessage = ExceptionUtils.getStackTrace(rootCause);	
    		} else {
    			errorMessage = ExceptionUtils.getStackTrace(e);
    			
    		}
			updateQtAsDisrupted(qtId,
					QTDisruptionConst.QT_PUBLISH_SUPPLIER_INSERT_DISRUPTION,
					errorMessage);
		}
	}

	private void updateQtPublishBpmTask(Long qtId) {
		// Update BPM after work flow status and supplier insertion successful
		try {
			if (noticeBoardSO == null) {
				noticeBoardSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTNoticeBoardSO.class);
			}
			NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setBpmEndpoint(BpmEndpointConst.SC_PUBLISH_QT_MESSAGEPOINT);
			NoticeBoardResDTO resDTO = noticeBoardSO
					.updateBpmForPublishAndProposalClose(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("$$$$$ BPM Update after Publish successful for the QT Id :"
						+ qtId);
			} else {
				log.info("$$$$$ BPM Update after Publish unsuccessful for the QT Id :"
						+ qtId);
				updateQtAsDisrupted(qtId,
						QTDisruptionConst.QT_PUBLISH_BPM_DISRUPTION,
						resDTO.getErrorMessage());
			}
		} catch (Exception e) {
			log.info("$$$$$ BPM Update after Publish failed for the QT Id :"
					+ qtId);
			String errorMessage = null;
			Throwable rootCause = ExceptionUtils.getRootCause(e);
    		if (rootCause != null) {
    			errorMessage = ExceptionUtils.getStackTrace(rootCause);	
    		} else {
    			errorMessage = ExceptionUtils.getStackTrace(e);
    			
    		}
			updateQtAsDisrupted(qtId,
					QTDisruptionConst.QT_PUBLISH_BPM_DISRUPTION,
					errorMessage);
		}
	}

	private void updateQtAsDisrupted(Long qtId, int disruptionStage, String errorMessage) {
		try {
			if(proposalSO == null){
				proposalSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			}
			ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
			proposalReqDTO.setQtId(qtId);
			proposalReqDTO.setDisruptionStage(disruptionStage);
			proposalReqDTO.setErrorMessage(errorMessage);
			proposalSO.updateDisruptedQtAndSendNotification(proposalReqDTO);
		} finally {
			proposalSO = null;
		}
	}
	
	private void reSchedulePublicationForPublishFailQt(Long qtId){
		if(proposalSO == null){
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
		}
		ProposalReqDTO reqDTO = new ProposalReqDTO();
		reqDTO.setQtId(qtId);
		reqDTO.setWorkFlowStatusConstant(QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION);
		ProposalResDTO resDTO = proposalSO.updateRescheduleWorkFlowStatus(reqDTO);
		if(resDTO.isSuccessful()){
			log.info("Work flow status updated for publish failed QT with id"+qtId);
		}
	}
}
