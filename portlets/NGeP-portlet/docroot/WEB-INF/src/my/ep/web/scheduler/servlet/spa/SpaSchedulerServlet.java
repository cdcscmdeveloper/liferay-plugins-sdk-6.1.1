package my.ep.web.scheduler.servlet.spa;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.SchedulerPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.spa.BrandSpaJob;
import my.ep.web.scheduler.job.spa.ClassSpaJob;
import my.ep.web.scheduler.job.spa.ColorSpaJob;
import my.ep.web.scheduler.job.spa.CommoditySpaJob;
import my.ep.web.scheduler.job.spa.FamilySpaJob;
import my.ep.web.scheduler.job.spa.MasterDataSpaJob;
import my.ep.web.scheduler.job.spa.MeasurementSpaJob;
import my.ep.web.scheduler.job.spa.SegmentSpaJob;
import my.ep.web.scheduler.job.spa.TypeSpaJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class SpaSchedulerServlet extends HttpServlet {

	private static final long serialVersionUID = -4832945404532466168L;
	public static Logger log = EpLogger.getLogger(SpaSchedulerServlet.class);

	public void init(ServletConfig config) throws ServletException {

		// SPA Integration Jobs
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.SPA_MASTER_DATA_MATERIAL_JOB,
						SchedulerConstants.SPA_INTEGRATION_JOB_GROUP),
						MasterDataSpaJob.class,
						SchedulerConstants.SPA_JOB_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SPA_MASTER_DATA_MATERIAL));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.SPA_MASTER_DATA_SEGMENT_JOB,
						SchedulerConstants.SPA_INTEGRATION_JOB_GROUP),
						SegmentSpaJob.class,
						SchedulerConstants.SPA_JOB_DAILY_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SPA_MASTER_DATA_SEGMENT));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.SPA_MASTER_DATA_FAMILY_JOB,
						SchedulerConstants.SPA_INTEGRATION_JOB_GROUP),
						FamilySpaJob.class,
						SchedulerConstants.SPA_JOB_DAILY_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SPA_MASTER_DATA_FAMILY));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.SPA_MASTER_DATA_CLASS_JOB,
						SchedulerConstants.SPA_INTEGRATION_JOB_GROUP),
						ClassSpaJob.class,
						SchedulerConstants.SPA_JOB_DAILY_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SPA_MASTER_DATA_CLASS));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.SPA_MASTER_DATA_COMMODITY_JOB,
						SchedulerConstants.SPA_INTEGRATION_JOB_GROUP),
						CommoditySpaJob.class,
						SchedulerConstants.SPA_JOB_DAILY_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SPA_MASTER_DATA_Commodity));
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.SPA_MASTER_DATA_COLOR_JOB,
						SchedulerConstants.SPA_INTEGRATION_JOB_GROUP),
						ColorSpaJob.class,
						SchedulerConstants.SPA_JOB_DAILY_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SPA_MASTER_DATA_COLOR));
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.SPA_MASTER_DATA_BRAND_JOB,
						SchedulerConstants.SPA_INTEGRATION_JOB_GROUP),
						BrandSpaJob.class,
						SchedulerConstants.SPA_JOB_DAILY_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SPA_MASTER_DATA_BRAND));
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.SPA_MASTER_DATA_MEASUREMENT_JOB,
						SchedulerConstants.SPA_INTEGRATION_JOB_GROUP),
						MeasurementSpaJob.class,
						SchedulerConstants.SPA_JOB_DAILY_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SPA_MASTER_DATA_MEASUREMENT));
		
		
		EpSchedulerHelper
		.scheduleJob(
				new JobKey(SchedulerConstants.SPA_MASTER_DATA_TYPE_JOB,
						SchedulerConstants.SPA_INTEGRATION_JOB_GROUP),
						TypeSpaJob.class,
						SchedulerConstants.SPA_JOB_DAILY_DESC,
						SchedulerPropertiesUtils
						.getInstance()
						.getProperty(
								EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SPA_MASTER_DATA_TYPE));

	}
}
