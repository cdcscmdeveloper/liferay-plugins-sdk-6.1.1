package my.ep.web.scheduler.servlet.cm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.cm.CmDropSequenceJob;
import my.ep.web.scheduler.job.cm.CmUpdateExpiredItemJob;
import my.ep.web.scheduler.job.gfmas.MaterialMasterDataInformationJob;
import my.ep.web.scheduler.job.phis.MasterDataMaterialInformationJob;
import my.ep.web.scheduler.job.phis.MasterDataMaterialJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class CmSchedulerServlet extends HttpServlet {
	private static final long serialVersionUID = 8691110272078406918L;
	public static Logger log = EpLogger
			.getLogger(CmSchedulerServlet.class);

	public void init(ServletConfig config) throws ServletException {

		// PHIS CM Out bound Continuous job
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.PHIS_MASTER_DATA,
				SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
				MasterDataMaterialJob.class, SchedulerConstants.PHIS_CM_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("phis.cm.outbound"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.PHIS_MASTER_DATA_MATERIAL,
				SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
				MasterDataMaterialInformationJob.class, SchedulerConstants.PHIS_MASTERDATA_MATERIAL_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("phis.cm.outbound"));
		
		// 1GFMAS Master Data Material Information job
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.GFMAS_MASTER_DATA_MATERIAL,
				SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
				MaterialMasterDataInformationJob.class, SchedulerConstants.GFMAS_MASTERDATA_MATERIAL_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.sm.outbound1"));

		///CM Expired Item
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.CM_EXPIRED_ITEM_TIMER_JOB,
				SchedulerConstants.CM_EXPIRED_ITEM_TIMER_JOB_GROUP),
				CmUpdateExpiredItemJob.class, SchedulerConstants.CM_EXPIRED_ITEM_TIMER_CONTINUOUS_JOB_DESC,
				SchedulerConstants.CM_EXPIRED_ITEM_TIMER);

		
		///CM Drop Doc Sequence
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.CM_EXPIRED_ITEM_TIMER_JOB,
				SchedulerConstants.CM_DROP_DOC_SEQ_JOB_GROUP),
				CmDropSequenceJob.class, SchedulerConstants.CM_EXPIRED_ITEM_TIMER_CONTINUOUS_JOB_DESC,
				SchedulerConstants.CM_EXPIRED_ITEM_TIMER);
		
	}
}
