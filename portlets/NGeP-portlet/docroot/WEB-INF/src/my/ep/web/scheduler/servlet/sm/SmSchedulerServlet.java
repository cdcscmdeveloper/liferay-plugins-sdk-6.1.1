package my.ep.web.scheduler.servlet.sm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.SchedulerPropertiesUtils;
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
import my.ep.web.scheduler.job.gfmas.ErrorReceiptInformationJob;
import my.ep.web.scheduler.job.gfmas.MasterDataVendorInboundJob;
import my.ep.web.scheduler.job.gfmas.MasterDataVendorOutboundJob;
import my.ep.web.scheduler.job.gfmas.ReceiptInformationJob;
import my.ep.web.scheduler.job.sm.ChangeExpiredMofPersonnelToBasicJob;
import my.ep.web.scheduler.job.sm.CheckBasicAccountExpirationJob;
import my.ep.web.scheduler.job.sm.ComplaintAccusationLetterReminderJob;
import my.ep.web.scheduler.job.sm.ComplaintShowCauseLetterReminderJob;
import my.ep.web.scheduler.job.sm.DeletePendingActivationJob;
import my.ep.web.scheduler.job.sm.MOFApplicationPaymentReminderJob;
import my.ep.web.scheduler.job.sm.MofRenewReminderJob;
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
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB,
								SchedulerConstants.SM_JOB_GROUP),
						DeletePendingActivationJob.class,
						SchedulerConstants.SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_DAILY_DEL_PEND_ACTV_PERSONNEL));

		// Sm SoftCert Renewal Reminder Job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.SM_APP_SOFTCERT_RENEW_REMINDER_JOB,
								SchedulerConstants.SM_JOB_GROUP),
						SoftCertRenewalReminderJob.class,
						SchedulerConstants.SM_APP_SOFTCERT_RENEW_REMINDER_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_APP_SOFTCERT_RENEW_REMINDER));
		
		// Sm MOF Expired - Personnel Change Role (Temporary disabled)
//		EpSchedulerHelper
//				.scheduleJob(
//						new JobKey(
//								SchedulerConstants.SM_CHANGE_EXPIRED_MOF_PERSONNEL_ROLE_JOB,
//								SchedulerConstants.SM_JOB_GROUP),
//								ChangeExpiredMofPersonnelToBasicJob.class,
//						SchedulerConstants.SM_CHANGE_EXPIRED_MOF_PERSONNEL_ROLE_JOB_DESC,
//						SchedulerPropertiesUtils
//								.getInstance()
//								.getProperty(
//										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_CHANGE_EXPIRED_MOF_PERSONNEL_ROLE));

		// Sm Payment Reminder Job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.SM_APP_PAYMENT_REMINDER,
								SchedulerConstants.SM_JOB_GROUP),
						MOFApplicationPaymentReminderJob.class,
						SchedulerConstants.SM_PAYMENT_REMINDER_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_APP_PAYMENT_REMINDER));

		// Sm App Offline Supporting Documents Reminder
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.SM_APP_OFFLINE_SUPPORTING_DOC_REMINDER,
								SchedulerConstants.SM_JOB_GROUP),
						SupportingDocumentReminderJob.class,
						SchedulerConstants.SM_APP_OFFLINE_SUPPORTING_DOC_REMINDER_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_APP_OFFLINE_SUPPORTING_DOC_REMINDER));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.SM_CHECK_BASIC_ACCOUNT_EXPIRATION_JOB,
								SchedulerConstants.SM_JOB_GROUP),
						CheckBasicAccountExpirationJob.class,
						SchedulerConstants.SM_CHECK_BASIC_ACCOUNT_EXPIRATION_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_CHECK_BASIC_ACCOUNT_EXPIRATION));

		//show cause letter notification - 
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(
						SchedulerConstants.SM_COMPLAINT_SHOWCAUSE_NOTIFICATION,
						SchedulerConstants.SM_JOB_GROUP),
				ComplaintShowCauseLetterReminderJob.class,
				SchedulerConstants.SM_COMPLAINT_SHOWCAUSE_NOTIFICATION_JOB_DESC,
				SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_COMPLAINT_SHOW_CAUSE_NOTIFICATION));
		
		//Accusation letter notification - 
				EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.SM_COMPLAINT_ACCUSATION_LETTER_NOTIFICATION,
								SchedulerConstants.SM_JOB_GROUP),
						ComplaintAccusationLetterReminderJob.class,
						SchedulerConstants.SM_COMPLAINT_ACCUSATION_LETTER_NOTIFICATION_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_COMPLAINT_ACCUSATION_LETTER_NOTIFICATION));

		//MOF Renewal Reminder - 
		EpSchedulerHelper
			.scheduleJob(
					new JobKey(
							SchedulerConstants.SM_MOF_RENEWAL_REMINDER,
							SchedulerConstants.SM_JOB_GROUP),
					MofRenewReminderJob.class,
					SchedulerConstants.SM_MOF_RENEWAL_REMINDER_JOB_DESC,
					SchedulerPropertiesUtils
							.getInstance()
							.getProperty(
									EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_MOF_RENEWAL_REMINDER));
				
		// 1GFMAS jobs
//		EpSchedulerHelper
//				.scheduleJob(
//						new JobKey(SchedulerConstants.GFMAS_VENDOR_OUTBOUND,
//								SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
//						MasterDataVendorOutboundJob.class,
//						SchedulerConstants.GFMAS_SM_OUT_JOB_DESC,
//						SchedulerPropertiesUtils
//								.getInstance()
//								.getProperty(
//										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_1GFMAS_VENDOR_OUTBOUND));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.GFMAS_ERR_RECEIPT_INFORMATION,
								SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
						ErrorReceiptInformationJob.class,
						SchedulerConstants.GFMAS_SM_RECEIPT_IN_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_1GFMAS_ERR_RECEIPT_INFORMATION));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.GFMAS_RECEIPT_INFORMATION,
								SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
						ReceiptInformationJob.class,
						SchedulerConstants.GFMAS_SM_RECEIPT_OUT_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_1GFMAS_RECEIPT_INFORMATION));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.GFMAS_VENDOR_INBOUND,
								SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
						MasterDataVendorInboundJob.class,
						SchedulerConstants.GFMAS_SM_IN_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_1GFMAS_VENDOR_INBOUND));

		// Eperunding Jobs
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.EPERUNDING_CONSULTANT_PROFILE_JOB,
								SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
						ConsultantProfileJob.class,
						SchedulerConstants.EPERUNDING_INTEGRATION_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_EPERUNDING_CONSULTANT_PROFILE));

		
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.EPERUNDING_DELETED_COMPANY_JOB,
								SchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
						DeletedCompanyJob.class,
						SchedulerConstants.EPERUNDING_DELETE_COMP_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SM_EPERUNDING_DELETED_COMAPNY_JOB));

		log.info("end init sm scheduler servlet....");

	}
}
