package my.ep.web.scheduler.servlet.sm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.scheduler.constants.cm.CmSchedulerConstants;
import my.ep.web.scheduler.constants.sm.SmSchedulerConstants;
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
import my.ep.web.scheduler.job.gfmas.GfmasBatchSmOutboundJob;
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
				SmSchedulerConstants.SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB,
				SmSchedulerConstants.SM_JOB_GROUP),
				DeletePendingActivationJob.class,
				SmSchedulerConstants.SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB_DESC,
				SmSchedulerConstants.SM_DAILY_DEL_PEND_ACTV_TIMER);

		// Sm SoftCert Renewal Reminder Job
		EpSchedulerHelper.scheduleJob(new JobKey(
				SmSchedulerConstants.SM_APP_SOFTCERT_RENEW_REMINDER_JOB,
				SmSchedulerConstants.SM_JOB_GROUP),
				SoftCertRenewalReminderJob.class,
				SmSchedulerConstants.SM_APP_SOFTCERT_RENEW_REMINDER_JOB_DESC,
				SmSchedulerConstants.SM_DAILY_SOFTCERT_RENEWAL_TIMER);
				
		// Sm Payment Reminder Job
		EpSchedulerHelper.scheduleJob(new JobKey(
				SmSchedulerConstants.SM_DAILY_PAYMENT_REMINDER_TIMER,
				SmSchedulerConstants.SM_JOB_GROUP),
				MOFApplicationPaymentReminderJob.class,
				SmSchedulerConstants.SM_PAYMENT_REMINDER_JOB_DESC,
				SmSchedulerConstants.SM_DAILY_PAYMENT_REMINDER_TIMER);
		
		// Sm App Offline Supporting Documents Reminder
		EpSchedulerHelper.scheduleJob(new JobKey(
				SmSchedulerConstants.SM_DAILY_SUPPORTING_DOC_REMINDER_TIMER,
				SmSchedulerConstants.SM_JOB_GROUP),
				SupportingDocumentReminderJob.class,
				SmSchedulerConstants.SM_APP_OFFLINE_SUPPORTING_DOC_REMINDER_DESC,
				SmSchedulerConstants.SM_DAILY_SUPPORTING_DOC_REMINDER_TIMER);
		
		//GFMAS SM OUT bound Continuous job				
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.GFMAS_SM_OUT_CONTINUOUS_JOB,
				CmSchedulerConstants.GFMAS_SM_OUT_JOB_GROUP),
				GfmasBatchSmOutboundJob.class, CmSchedulerConstants.GFMAS_SM_OUT_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.sm.outbound"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_CONSULTANT_PROFILE_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				ConsultantProfileJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound1"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_ASSOCIATED_COMPANY_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				AssociatedCompanyJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound2"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_COMPANY_OWNER_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				CompanyOwnerJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound3"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_EQUITY_OWNER_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				EquityOwnerJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound4"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_BOARD_DIRECTOR_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				BoardOfDirectorJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound5"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_MANAGEMENT_PERSONNEL_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				ManagementPersonnelJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound6"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_AUTHORIZED_SIGNATORY_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				SignatoryJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound7"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_BRANCH_COMPANY_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				BranchCompanyJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound8"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_REGISTERED_CATEGORY_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				RegisteredCategoryJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound9"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_GOVERNMENT_PROJECT_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				GovernmentProjectJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound10"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				CmSchedulerConstants.EPERUNDING_DELETED_COMAPNY_JOB,
				CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
				DeletedCompanyJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("eperunding.sm.outbound11"));
		
		log.info("end init sm scheduler servlet....");

	}
}
