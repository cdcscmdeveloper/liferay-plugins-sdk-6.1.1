package my.ep.web.scheduler.servlet.epol;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.SchedulerPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.epol.GovernmentEntityInfoJob;
import my.ep.web.scheduler.job.epol.GovernmentUserInfoJob;
import my.ep.web.scheduler.job.epol.CompanyInfoJob;
import my.ep.web.scheduler.job.epol.ContactInfoJob;
import my.ep.web.scheduler.job.epol.SupplierUserInfoJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class EPOLSchedulerServlet extends HttpServlet {

	public static Logger log = EpLogger.getLogger(EPOLSchedulerServlet.class);

	private static final long serialVersionUID = 2591757747850240619L;

	public void init(ServletConfig config) throws ServletException {

		// EPOL government entity info job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.EPOL_GOVERNMENT_ENTITY_INFO_JOB,
								SchedulerConstants.EPOL_INTEGRATION_JOB_GROUP),
								GovernmentEntityInfoJob.class,
						SchedulerConstants.EPOL_GOVERNMENT_ENTITY_INFO_JOB_DESCRIPTION,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_EPOL_GOVERNMENT_ENTITY_INFO_JOB));

		// EPOL government user info job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.EPOL_GOVERNMENT_USER_INFO_JOB,
								SchedulerConstants.EPOL_INTEGRATION_JOB_GROUP),
								GovernmentUserInfoJob.class,
						SchedulerConstants.EPOL_GOVERNMENT_USER_INFO_JOB_DESCRIPTION,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_EPOL_GOVERNMENT_USER_INFO_JOB));

		// EPOL company info job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.EPOL_COMPANY_INFO_JOB,
								SchedulerConstants.EPOL_INTEGRATION_JOB_GROUP),
								CompanyInfoJob.class,
						SchedulerConstants.EPOL_COMPANY_INFO_JOB_DESCRIPTION,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_EPOL_COMPANY_INFO_JOB));

		// EPOL contact info job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.EPOL_CONTACT_INFO_JOB,
								SchedulerConstants.EPOL_INTEGRATION_JOB_GROUP),
								ContactInfoJob.class,
						SchedulerConstants.EPOL_CONTACT_INFO_JOB_DESCRIPTION,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_EPOL_CONTACT_INFO_JOB));

		// EPOL supplier user info job
				EpSchedulerHelper
						.scheduleJob(
								new JobKey(
										SchedulerConstants.EPOL_SUPPLIER_USER_INFO_JOB,
										SchedulerConstants.EPOL_INTEGRATION_JOB_GROUP),
										SupplierUserInfoJob.class,
								SchedulerConstants.EPOL_SUPPLIER_USER_INFO_JOB_DESCRIPTION,
								SchedulerPropertiesUtils
										.getInstance()
										.getProperty(
												EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_EPOL_SUPPLIER_USER_INFO_JOB));

	}

}
