package my.ep.web.scheduler.servlet.ct;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.ct.CtSchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.ct.CtActivateAgencyItemJob;
import my.ep.web.scheduler.job.ct.CtAgreementNotificationJob;
import my.ep.web.scheduler.job.ct.CtContractNotificationJob;
import my.ep.web.scheduler.job.ct.CtExpireCircularJob;
import my.ep.web.scheduler.job.ct.CtExpireContractJob;
import my.ep.web.scheduler.job.ct.CtExpireFactoringJob;
import my.ep.web.scheduler.job.ct.CtPBNotificationJob;
import my.ep.web.scheduler.job.ct.CtRemedyNotificationJob;


import org.apache.log4j.Logger;
import org.quartz.JobKey;

/**
 * Servlet implementation class CtSchedulerServlet
 * 
 * @author NancyAng
 * @since 24/6/2015
 */
public class CtSchedulerServlet extends HttpServlet {
	private static final long serialVersionUID = 5961523576286300095L;
	public static Logger log = EpLogger
			.getLogger(CtSchedulerServlet.class);
	
	public void init(ServletConfig config) throws ServletException {
		
		// Expire the contract that no more effective. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				CtSchedulerConstants.UPDATE_EXPIRED_CONTRACT,
				CtSchedulerConstants.CT_JOB_GROUP),
				CtExpireContractJob.class,
				CtSchedulerConstants.UPDATE_EXPIRED_CONTRACT_JOB_DESC,
				CtSchedulerConstants.TRIGGER_TIME);
		
		// Expire the factoring that no more effective. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				CtSchedulerConstants.UPDATE_EXPIRED_FACTORING,
				CtSchedulerConstants.CT_JOB_GROUP),
				CtExpireFactoringJob.class,
				CtSchedulerConstants.UPDATE_EXPIRED_FACTORING_JOB_DESC,
				CtSchedulerConstants.TRIGGER_TIME);
		
		// Expire the circular that no more effective. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				CtSchedulerConstants.UPDATE_EXPIRED_CIRCULAR,
				CtSchedulerConstants.CT_JOB_GROUP),
				CtExpireCircularJob.class,
				CtSchedulerConstants.UPDATE_EXPIRED_CIRCULAR_JOB_DESC,
				CtSchedulerConstants.TRIGGER_TIME);
		
		// Activate the suspended agency and item.
		EpSchedulerHelper.scheduleJob(new JobKey(
				CtSchedulerConstants.UPDATE_SUSPENDED_AGENCY_ITEM,
				CtSchedulerConstants.CT_JOB_GROUP),
				CtActivateAgencyItemJob.class,
				CtSchedulerConstants.UPDATE_SUSPENDED_AGENCY_ITEM_JOB_DESC,
				CtSchedulerConstants.TRIGGER_TIME);
		
		// Send Contract Notification to contract admin and selected approvers. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				CtSchedulerConstants.CONTRACT_EXPIRATION_REMINDER,
				CtSchedulerConstants.CT_JOB_GROUP),
				CtContractNotificationJob.class,
				CtSchedulerConstants.CONTRACT_EXPIRATION_REMINDER_JOB_DESC,
				CtSchedulerConstants.TRIGGER_TIME);
		
		// Send Agreement Completion Notification to contract admin and selected approvers. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				CtSchedulerConstants.AGREEMENT_EXPIRATION_REMINDER,
				CtSchedulerConstants.CT_JOB_GROUP),
				CtAgreementNotificationJob.class,
				CtSchedulerConstants.AGREEMENT_EXPIRATION_REMINDER_JOB_DESC,
				CtSchedulerConstants.TRIGGER_TIME);
		
		// Send Performance Bond Notification to contract admin and selected approvers. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				CtSchedulerConstants.PERFORMANCE_BOND_EXPIRATION_REMINDER,
				CtSchedulerConstants.CT_JOB_GROUP),
				CtPBNotificationJob.class,
				CtSchedulerConstants.PERFORMANCE_BOND_EXPIRATION_REMINDER_JOB_DESC,
				CtSchedulerConstants.TRIGGER_TIME);
		
		// Send Remedy Notification to contract admin and selected approvers. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				CtSchedulerConstants.REMEDY_EXPIRATION_REMINDER,
				CtSchedulerConstants.CT_JOB_GROUP),
				CtRemedyNotificationJob.class,
				CtSchedulerConstants.REMEDY_EXPIRATION_REMINDER_JOB_DESC,
						CtSchedulerConstants.TRIGGER_TIME);			
	}
}
