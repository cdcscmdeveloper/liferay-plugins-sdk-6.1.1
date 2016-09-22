package my.ep.web.scheduler.servlet.crm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.SchedulerPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.crm.UpdateGovernmentInfoJob;
import my.ep.web.scheduler.job.crm.UpdateGovernmentUserInfoJob;
import my.ep.web.scheduler.job.crm.UpdateSupplierInfoJob;
import my.ep.web.scheduler.job.crm.UpdateSupplierUserInfoJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class CRMSchedulerServlet extends HttpServlet {

	public static Logger log = EpLogger.getLogger(CRMSchedulerServlet.class);

	private static final long serialVersionUID = 2591757747850240619L;

	public void init(ServletConfig config) throws ServletException {

		// CRM update govt.. user info job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.UPDATE_GOVERNMENT_USER_INFO_JOB,
								SchedulerConstants.CRM_INTEGRATION_JOB_GROUP),
						UpdateGovernmentUserInfoJob.class,
						SchedulerConstants.UPDATE_GOVERNMENT_USER_INFO_JOB_DESCRIPTION,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_UPDATE_GOVERNMENT_USER_INFO_JOB));

		// CRM update govt.. info job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.UPDATE_GOVERNMENT_INFO_JOB,
								SchedulerConstants.CRM_INTEGRATION_JOB_GROUP),
								UpdateGovernmentInfoJob.class,
						SchedulerConstants.UPDATE_GOVERNMENT_INFO_JOB_DESCRIPTION,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_UPDATE_GOVERNMENT_INFO_JOB));

		// CRM update supplier user info job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.UPDATE_SUPPLIER_USER_INFO_JOB,
								SchedulerConstants.CRM_INTEGRATION_JOB_GROUP),
								UpdateSupplierUserInfoJob.class,
						SchedulerConstants.UPDATE_SUPPLIER_USER_INFO_JOB_DESCRIPTION,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_UPDATE_SUPPLIER_USER_INFO_JOB));

		// CRM update supplier info job
//		EpSchedulerHelper
//				.scheduleJob(
//						new JobKey(
//								SchedulerConstants.UPDATE_SUPPLIER_INFO_JOB,
//								SchedulerConstants.CRM_INTEGRATION_JOB_GROUP),
//								UpdateSupplierInfoJob.class,
//						SchedulerConstants.UPDATE_SUPPLIER_INFO_JOB_DESCRIPTION,
//						SchedulerPropertiesUtils
//								.getInstance()
//								.getProperty(
//										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_UPDATE_SUPPLIER_INFO_JOB));

	}

}
