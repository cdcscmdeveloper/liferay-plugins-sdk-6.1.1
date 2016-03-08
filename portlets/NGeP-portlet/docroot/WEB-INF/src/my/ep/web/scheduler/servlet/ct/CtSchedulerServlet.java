package my.ep.web.scheduler.servlet.ct;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.ct.CtActivateAgencyItemJob;
import my.ep.web.scheduler.job.ct.CtAgreementNotificationJob;
import my.ep.web.scheduler.job.ct.CtContractNotificationJob;
import my.ep.web.scheduler.job.ct.CtExpireCircularJob;
import my.ep.web.scheduler.job.ct.CtExpireContractJob;
import my.ep.web.scheduler.job.ct.CtExpireFactoringJob;
import my.ep.web.scheduler.job.ct.CtImposedServiceTaxNotificationJob;
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
				SchedulerConstants.UPDATE_EXPIRED_CONTRACT,
				SchedulerConstants.CT_JOB_GROUP),
				CtExpireContractJob.class,
				SchedulerConstants.UPDATE_EXPIRED_CONTRACT_JOB_DESC,
				SchedulerConstants.TRIGGER_TIME);
		
		// Expire the factoring that no more effective. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.UPDATE_EXPIRED_FACTORING,
				SchedulerConstants.CT_JOB_GROUP),
				CtExpireFactoringJob.class,
				SchedulerConstants.UPDATE_EXPIRED_FACTORING_JOB_DESC,
				SchedulerConstants.TRIGGER_TIME);
		
		// Expire the circular that no more effective. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.UPDATE_EXPIRED_CIRCULAR,
				SchedulerConstants.CT_JOB_GROUP),
				CtExpireCircularJob.class,
				SchedulerConstants.UPDATE_EXPIRED_CIRCULAR_JOB_DESC,
				SchedulerConstants.TRIGGER_TIME);
		
		// Activate the suspended agency and item.
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.UPDATE_SUSPENDED_AGENCY_ITEM,
				SchedulerConstants.CT_JOB_GROUP),
				CtActivateAgencyItemJob.class,
				SchedulerConstants.UPDATE_SUSPENDED_AGENCY_ITEM_JOB_DESC,
				SchedulerConstants.TRIGGER_TIME);
		
		// Send Contract Notification to contract admin and selected approvers. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.CONTRACT_EXPIRATION_REMINDER,
				SchedulerConstants.CT_JOB_GROUP),
				CtContractNotificationJob.class,
				SchedulerConstants.CONTRACT_EXPIRATION_REMINDER_JOB_DESC,
				SchedulerConstants.TRIGGER_TIME);
		
		// Send Agreement Completion Notification to contract admin and selected approvers. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.AGREEMENT_EXPIRATION_REMINDER,
				SchedulerConstants.CT_JOB_GROUP),
				CtAgreementNotificationJob.class,
				SchedulerConstants.AGREEMENT_EXPIRATION_REMINDER_JOB_DESC,
				SchedulerConstants.TRIGGER_TIME);
		
		// Send Performance Bond Notification to contract admin and selected approvers. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.PERFORMANCE_BOND_EXPIRATION_REMINDER,
				SchedulerConstants.CT_JOB_GROUP),
				CtPBNotificationJob.class,
				SchedulerConstants.PERFORMANCE_BOND_EXPIRATION_REMINDER_JOB_DESC,
				SchedulerConstants.TRIGGER_TIME);
		
		// Send Remedy Notification to contract admin and selected approvers. 
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.REMEDY_EXPIRATION_REMINDER,
				SchedulerConstants.CT_JOB_GROUP),
				CtRemedyNotificationJob.class,
				SchedulerConstants.REMEDY_EXPIRATION_REMINDER_JOB_DESC,
				SchedulerConstants.TRIGGER_TIME);
		
		// Send Imposed Service Tax Notification to contract admin.
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.IMPOSED_SERVICE_TAX_REMINDER,
				SchedulerConstants.CT_JOB_GROUP),
				CtImposedServiceTaxNotificationJob.class,
				SchedulerConstants.IMPOSED_SERVICE_TAX_REMINDER_JOB_DESC,
				SchedulerConstants.TRIGGER_TIME);
	}
}
