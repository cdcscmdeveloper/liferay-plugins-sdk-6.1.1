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

import java.util.Calendar;
import java.util.Date;

import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.QTDisruptionConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.BpmEndpointConst;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.exception.ExceptionUtils;
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
		debugLogger("Start execute in QTProposalCloseJob @"
				+ DateUtils.getCurrentDate());
		log.debug("##### Start execute in QTProposalCloseJob @"
				+ DateUtils.getCurrentDate());
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		Long qtId = dataMap
				.getLong(SchedulerConstants.QT_PUBLISH_CLOSE_JOB_DATA);

		log.debug("#$#$#$#$ Proposal being closed for QT with id:: " + qtId);

		try {
			if (proposalSO == null) {
				proposalSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTProposalSO.class);
			}
			ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
			proposalReqDTO.setQtId(qtId);
			ScQt qt = proposalSO.getQtByQtId(proposalReqDTO).getScQt();
			if (qt.getClosingDate().equals(getCompareTime())) {
				ProposalResDTO proposalResDTO = proposalSO
						.updateProposalCloseWorkFlowStatus(proposalReqDTO);

				if (proposalResDTO.isSuccessful()) {
					log.debug("$$$$$ qt Work Flow status update suceessful from QTProposalCloseJob job");
					insertProposalAmount(qtId);
				}
			} else if (qt.getClosingDate().before(getCompareTime())) {
				log.debug("%%%%% QT not Closed ontime and updated to closing reschedule");
				reSchedulePublicationForPublishFailQt(qtId);
			}

		} catch (Exception e) {
			log.debug("$$$$$ qt Work Flow status updated failed from QTProposalCloseJob job");
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
			reSchedulePublicationForPublishFailQt(qtId);
		} finally {
			noticeBoardSO = null;
			proposalSO = null;
		}
		debugLogger("End execute in QTProposalCloseJob @"
				+ DateUtils.getCurrentDate());
		log.debug("##### Finish execute in QTProposalCloseJob @"
				+ DateUtils.getCurrentDate());
	}

	private void insertProposalAmount(Long qtId) {
		try {

			noticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);

			NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
			// Close QT's Which are scheduled today for Proposal Close
			reqDTO.setQtId(qtId);
			NoticeBoardResDTO resDTO = noticeBoardSO
					.closeQtAndSaveProposalAmount(reqDTO);

			if (resDTO.isSuccessful()) {
				log.debug("$$$$$ Insert Proposal amount Successful for the QT No :"
						+ qtId);
				updateBPMPostProposalClose(reqDTO);
			} else {
				log.debug("$$$$$ Insert Proposal amount unSuccessful for the QT No :"
						+ qtId);
				updateQtAsDisrupted(
						qtId,
						QTDisruptionConst.QT_CLOSE_PROPOSAL_AMOUNT_INSERT_DISRUPTION,
						resDTO.getErrorMessage());
			}

		} catch (Exception e) {
			log.debug("$$$$$ Insert Proposal amount unSuccessful for the QT No :"
					+ qtId);
			log.error(e.getLocalizedMessage());
			String errorMessage = null;
			e.printStackTrace();
			Throwable rootCause = ExceptionUtils.getRootCause(e);
			if (rootCause != null) {
				errorMessage = ExceptionUtils.getStackTrace(rootCause);
			} else {
				errorMessage = ExceptionUtils.getStackTrace(e);

			}
			updateQtAsDisrupted(
					qtId,
					QTDisruptionConst.QT_CLOSE_PROPOSAL_AMOUNT_INSERT_DISRUPTION,
					errorMessage);
		}
	}

	private void updateBPMPostProposalClose(NoticeBoardReqDTO reqDTO) {
		Long qtId = reqDTO.getQtId();

		try {
			noticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);

			reqDTO.setBpmEndpoint(BpmEndpointConst.SC_CLOSE_PROPOSAL_MESSAGEPOINT);
			NoticeBoardResDTO resDTO = noticeBoardSO
					.updateBpmForPublishAndProposalClose(reqDTO);
			if (resDTO.isSuccessful()) {
				log.debug("$$$$$ BPM Update after Proposal Close successful for the QT Id :"
						+ qtId);
			} else {
				log.debug("$$$$$ BPM Update after Proposal Close unsuccessful for the QT Id :"
						+ qtId);
				updateQtAsDisrupted(qtId,
						QTDisruptionConst.QT_CLOSE_BPM_DISRUPTION,
						resDTO.getErrorMessage());
			}
		} catch (Exception e) {
			log.debug("$$$$$ BPM Update after Proposal Close failed for the QT Id :"
					+ qtId);
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
			String errorMessage = null;
			Throwable rootCause = ExceptionUtils.getRootCause(e);
			if (rootCause != null) {
				errorMessage = ExceptionUtils.getStackTrace(rootCause);
			} else {
				errorMessage = ExceptionUtils.getStackTrace(e);

			}
			updateQtAsDisrupted(qtId,
					QTDisruptionConst.QT_CLOSE_BPM_DISRUPTION, errorMessage);
		}
	}

	private void updateQtAsDisrupted(Long qtId, int disruptionStage,
			String errorMessage) {
		try {
			if (proposalSO == null) {
				proposalSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTProposalSO.class);
			}
			ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
			proposalReqDTO.setQtId(qtId);
			proposalReqDTO.setDisruptionStage(disruptionStage);
			proposalReqDTO.setErrorMessage(errorMessage);
			proposalSO.updateDisruptedQtAndSendNotification(proposalReqDTO);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
	}

	private void reSchedulePublicationForPublishFailQt(Long qtId) {
		if (proposalSO == null) {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
		}
		ProposalReqDTO reqDTO = new ProposalReqDTO();
		reqDTO.setQtId(qtId);
		reqDTO.setJobType(SourcingConstants.JOB_TYPE_CLOSE);
		reqDTO.setWorkFlowStatusConstant(QTStatusConst.QT_PENDING_RESCEDULE_PROPOSAL_CLOSING_DATE);
		ProposalResDTO resDTO = proposalSO
				.updateRescheduleWorkFlowStatus(reqDTO);
		if (resDTO.isSuccessful()) {
			log.debug("Work flow status updated for proposal close failed QT with id"
					+ qtId);
		}
	}

	private Date getCompareTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date compareTime = cal.getTime();
		return compareTime;
	}
}
