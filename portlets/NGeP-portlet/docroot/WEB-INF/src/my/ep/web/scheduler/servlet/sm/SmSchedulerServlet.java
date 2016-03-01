package my.ep.web.scheduler.servlet.sm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.eperunding.AssociatedCompanyJob;
import my.ep.web.scheduler.job.eperunding.BoardOfDirectorJob;
import my.ep.web.scheduler.job.eperunding.BranchCompanyJob;
import my.ep.web.scheduler.job.eperunding.CompanyOwnerJob;
import my.ep.web.scheduler.job.eperunding.ConsultantProfileJob;
import my.ep.web.scheduler.job.eperunding.DeletedCompanyJob;
import my.ep.web.scheduler.job.eperunding.EquityOwnerJob;
import my.ep.web.scheduler.job.eperunding.GovernmentProjectJob;
import my.ep.web.scheduler.job.eperunding.ManagementPersonnelJob;
import my.ep.web.scheduler.job.eperunding.RegisteredCategoryJob;
import my.ep.web.scheduler.job.eperunding.SignatoryJob;
import my.ep.web.scheduler.job.gfmas.MasterDataVendorInboundJob;
import my.ep.web.scheduler.job.gfmas.MasterDataVendorOutboundJob;
import my.ep.web.scheduler.job.gfmas.ReceiptInformationJob;
import my.ep.web.scheduler.job.sm.DeletePendingActivationJob;
import my.ep.web.scheduler.job.sm.MOFApplicationPaymentReminderJob;
import my.ep.web.scheduler.job.sm.SoftCertRenewalReminderJob;
import my.ep.web.scheduler.job.sm.SupportingDocumentReminderJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class SmSchedulerServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2265394558051568423L;
	public static Logger log = EpLogger.getLogger(SmSchedulerServlet.class);

	public void init(ServletConfig config) throws ServletException {
		log.info("start init sm scheduler servlet....");

		// Sm Delete Pending User Activation Job
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB,
				SchedulerConstants.SM_JOB_GROUP),
				DeletePendingActivationJob.class,
				SchedulerConstants.SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB_DESC,
				SchedulerConstants.SM_DAILY_DEL_PEND_ACTV_TIMER);

		// Sm SoftCert Renewal Reminder Job
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.SM_APP_SOFTCERT_RENEW_REMINDER_JOB,
				SchedulerConstants.SM_JOB_GROUP),
				SoftCertRenewalReminderJob.class,
				SchedulerConstants.SM_APP_SOFTCERT_RENEW_REMINDER_JOB_DESC,
				SchedulerConstants.SM_DAILY_SOFTCERT_RENEWAL_TIMER);
				
		// Sm Payment Reminder Job
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.SM_DAILY_PAYMENT_REMINDER_TIMER,
				SchedulerConstants.SM_JOB_GROUP),
				MOFApplicationPaymentReminderJob.class,
				SchedulerConstants.SM_PAYMENT_REMINDER_JOB_DESC,
				SchedulerConstants.SM_DAILY_PAYMENT_REMINDER_TIMER);
		
		// Sm App Offline Supporting Documents Reminder
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.SM_DAILY_SUPPORTING_DOC_REMINDER_TIMER,
				SchedulerConstants.SM_JOB_GROUP),
				SupportingDocumentReminderJob.class,
				SchedulerConstants.SM_APP_OFFLINE_SUPPORTING_DOC_REMINDER_DESC,
				SchedulerConstants.SM_DAILY_SUPPORTING_DOC_REMINDER_TIMER);
		
		//1GFMAS jobs				
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.GFMAS_VENDOR_OUTBOUND,
				SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
				MasterDataVendorOutboundJob.class, SchedulerConstants.GFMAS_SM_OUT_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.sm.outbound"));
				
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.GFMAS_RECEIPT_INFORMATION,
				SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
				ReceiptInformationJob.class, SchedulerConstants.GFMAS_SM_RECEIPT_OUT_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.smp.Outbound"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.GFMAS_VENDOR_INBOUND,
				SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
				MasterDataVendorInboundJob.class, SchedulerConstants.GFMAS_SM_IN_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.smp.Outbound"));	
		
		//Eperunding Jobs
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_CONSULTANT_PROFILE_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				ConsultantProfileJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound1"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_ASSOCIATED_COMPANY_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				AssociatedCompanyJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound2"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_COMPANY_OWNER_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				CompanyOwnerJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound3"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_EQUITY_OWNER_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				EquityOwnerJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound4"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_BOARD_DIRECTOR_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				BoardOfDirectorJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound5"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_MANAGEMENT_PERSONNEL_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				ManagementPersonnelJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound6"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_AUTHORIZED_SIGNATORY_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				SignatoryJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound7"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_BRANCH_COMPANY_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				BranchCompanyJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound8"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_REGISTERED_CATEGORY_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				RegisteredCategoryJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound9"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_GOVERNMENT_PROJECT_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				GovernmentProjectJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound10"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.EPERUNDING_DELETED_COMAPNY_JOB,
				SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				DeletedCompanyJob.class, SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound11"));
		
		log.info("end init sm scheduler servlet....");

	}
}
