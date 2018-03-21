package my.ep.web.scheduler.servlet.phis;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.SchedulerPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.phis.MasterDataDeliveryAddressJob;
import my.ep.web.scheduler.job.phis.MasterDataKodAkaunJob;
import my.ep.web.scheduler.job.phis.MasterDataMaterialInformationJob;
import my.ep.web.scheduler.job.phis.MasterDataMaterialJob;
import my.ep.web.scheduler.job.phis.MasterDataProgramJob;
import my.ep.web.scheduler.job.phis.MasterDataProjectJob;
import my.ep.web.scheduler.job.phis.MasterDataVotDanaJob;
import my.ep.web.scheduler.job.phis.MasterDataVotJob;
import my.ep.web.scheduler.job.phis.PaymentEffectNotificationJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class PhisSchedulerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7729696524773396427L;
	public static Logger log = EpLogger.getLogger(PhisSchedulerServlet.class);

	public void init(ServletConfig config) throws ServletException {
		// PHIS CM Out bound Continuous job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.PHIS_MASTER_DATA,
								SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						MasterDataMaterialJob.class,
						SchedulerConstants.PHIS_CM_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_CM_PHIS_CM_OUTBOUND));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.PHIS_MASTER_DATA_MATERIAL,
								SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						MasterDataMaterialInformationJob.class,
						SchedulerConstants.PHIS_MASTERDATA_MATERIAL_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_CM_PHIS_CM_OUTBOUND));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.PHIS_PAY_EFF_NOTIFY_JOB,
								SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						PaymentEffectNotificationJob.class,
						SchedulerConstants.PHIS_FL_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_FL_PHIS_FL_OUTBOUND));

		// PHIS Integration Jobs
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.PHIS_VOT_FUND_JOB,
								SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						MasterDataVotJob.class,
						SchedulerConstants.PHIS_PM_VOT_FUND_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_PHIS_VOT_FUND));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.PHIS_VOT_FUND_DANA_JOB,
								SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						MasterDataVotDanaJob.class,
						SchedulerConstants.PHIS_PM_DANA_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_PHIS_VOT_FUND_DANA));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.PHIS_PROGRAM_JOB,
								SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						MasterDataProgramJob.class,
						SchedulerConstants.PHIS_PM_PROGRAM_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_PHIS_PROGRAM));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.PHIS_PROJECT_JOB,
								SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						MasterDataProjectJob.class,
						SchedulerConstants.PHIS_PROJECT_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_PHIS_PROJECT));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.PHIS_KOD_JOB,
								SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						MasterDataKodAkaunJob.class,
						SchedulerConstants.PHIS_PM_KOD_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_PHIS_KOD));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.PHIS_DELIVERY_ADDRESS_JOB,
								SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						MasterDataDeliveryAddressJob.class,
						SchedulerConstants.PHIS_DELIVERY_ADDRESS_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_PM_PHIS_DELIVERY_ADDRESS));
	}

}
