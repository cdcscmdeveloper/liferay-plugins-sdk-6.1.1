package my.ep.web.scheduler.servlet.pm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.phis.MasterDataKodAkaunJob;
import my.ep.web.scheduler.job.phis.MasterDataProgramJob;
import my.ep.web.scheduler.job.phis.MasterDataProjectJob;
import my.ep.web.scheduler.job.phis.MasterDataVotDanaJob;
import my.ep.web.scheduler.job.phis.MasterDataVotJob;
import my.ep.web.scheduler.job.phis.PaymentEffectNotificationJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class PmSchedulerServlet extends HttpServlet {
	
	private static final long serialVersionUID = -4832945404532466168L;
	public static Logger log = EpLogger
			.getLogger(PmSchedulerServlet.class);
	
	public void init(ServletConfig config) throws ServletException {
		
		// Sc Daily job
		/*EpSchedulerHelper.scheduleJob(new JobKey(
		ScSchedulerConstants.EP_DAILY_JOB,
		ScSchedulerConstants.EP_JOB_GROUP),
		BidSchedulesPoolingJob.class,
		"Job run every day at 11:55 PM", "0 55 23 * * ?");*/
		
		EpSchedulerHelper.scheduleJob(new JobKey(
		SchedulerConstants.PHIS_VOT_FUND_JOB,
		SchedulerConstants.PHIS_PM_OUTBOUND_JOB_GROUP),
		MasterDataVotJob.class, SchedulerConstants.PHIS_PM_CONTINUOUS_JOB_DESC,
		EpPropertiesUtils.getInstance().getProperty("phis.pm.outbound"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
		SchedulerConstants.PHIS_VOT_FUND_DANA_JOB,
		SchedulerConstants.PHIS_PM_OUTBOUND_JOB_GROUP),
		MasterDataVotDanaJob.class, SchedulerConstants.PHIS_PM_CONTINUOUS_JOB_DESC,
		EpPropertiesUtils.getInstance().getProperty("phis.pm.outbound"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
		SchedulerConstants.PHIS_PROGRAM_JOB,
		SchedulerConstants.PHIS_PM_OUTBOUND_JOB_GROUP),
		MasterDataProgramJob.class, SchedulerConstants.PHIS_PM_CONTINUOUS_JOB_DESC,
		EpPropertiesUtils.getInstance().getProperty("phis.pm.outbound"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
		SchedulerConstants.PHIS_PROJECT_JOB,
		SchedulerConstants.PHIS_PM_OUTBOUND_JOB_GROUP),
		MasterDataProjectJob.class, SchedulerConstants.PHIS_PM_CONTINUOUS_JOB_DESC,
		EpPropertiesUtils.getInstance().getProperty("phis.pm.outbound"));
		
		EpSchedulerHelper.scheduleJob(new JobKey(
		SchedulerConstants.PHIS_KOD_JOB,
		SchedulerConstants.PHIS_PM_OUTBOUND_JOB_GROUP),
		MasterDataKodAkaunJob.class, SchedulerConstants.PHIS_PM_CONTINUOUS_JOB_DESC,
		EpPropertiesUtils.getInstance().getProperty("phis.pm.outbound"));
		
		/*EpSchedulerHelper.scheduleJob(new JobKey(
		SchedulerConstants.PHIS_DELIVERY_ADDRESS_JOB,
		SchedulerConstants.PHIS_PM_OUTBOUND_JOB_GROUP),
		MasterDataDeliveryAddressJob.class, SchedulerConstants.PHIS_PM_CONTINUOUS_JOB_DESC,
		EpPropertiesUtils.getInstance().getProperty("phis.pm.outbound"));*/
		
		EpSchedulerHelper.scheduleJob(new JobKey(
		SchedulerConstants.PHIS_PAY_EFF_NOTIFY_JOB,
		SchedulerConstants.PHIS_PM_OUTBOUND_JOB_GROUP),
		PaymentEffectNotificationJob.class, SchedulerConstants.PHIS_PM_CONTINUOUS_JOB_DESC,
		EpPropertiesUtils.getInstance().getProperty("phis.pm.outbound"));
		
	}
}
