package my.ep.web.scheduler.servlet.sc;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.gfmas.SSTInformationJob;
import my.ep.web.scheduler.job.sc.BidSchedulesPoolingJob;
import my.ep.web.scheduler.job.sc.CloseQTSchedulerJob;
import my.ep.web.scheduler.job.sc.ContinuousBidSchedulerJob;
import my.ep.web.scheduler.job.sc.InviteSuppliersJob;
import my.ep.web.scheduler.job.sc.ProposalValidityExpiaryJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class ScSchedulerServlet extends HttpServlet {

	public static Logger log = EpLogger.getLogger(ScSchedulerServlet.class);

	private static final long serialVersionUID = 2591757747850240619L;

	public void init(ServletConfig config) throws ServletException {

		// Sc Daily job
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.SC_DAILY_JOB,
				SchedulerConstants.SC_JOB_GROUP), BidSchedulesPoolingJob.class,
				"Job run every day at 11:55 PM", "0 55 23 * * ?");

		// daily Job to be run at server startup

		EpSchedulerHelper.runBuildJobNow(new JobKey(
				SchedulerConstants.SC_DAILY_JOB,
				SchedulerConstants.SC_JOB_GROUP));

		// Sc Continous job
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.SC_CONTINOUS_JOB,
				SchedulerConstants.SC_JOB_GROUP),
				ContinuousBidSchedulerJob.class, "Job to run every 60 Seconds",
				"0/60 * * * * ?");

		// Sc Daily job for published Quatation Tenders.
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.SC_UPDATE_SUPPLIERS_JOB,
				SchedulerConstants.SC_JOB_GROUP), InviteSuppliersJob.class,
				"Job run every day at 11:55 PM", "0 55 23 * * ?");

		// Sc Daily job for Close Quatation Tenders.
		EpSchedulerHelper.scheduleJob(new JobKey(
						SchedulerConstants.SC_QT_CLOSED_JOB,
						SchedulerConstants.SC_JOB_GROUP),
						CloseQTSchedulerJob.class,"Job run every day by batch",
						EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SC_QTCLOSING));

		// Sc Daily job for Proposal Validity Expiary Quatation Tenders.
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.SC_QT_PROPOSAL_VALIDITY_EXPIARY,
				SchedulerConstants.SC_JOB_GROUP),
				ProposalValidityExpiaryJob.class,"Job run every day at 12:05 AM", "0 05 0 * * ?");

		EpSchedulerHelper.scheduleJob(
				new JobKey(SchedulerConstants.GFMAS_SST_INFORMATION,SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
				SSTInformationJob.class,
				SchedulerConstants.GFMAS_SM_OUT_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("1gfmas.sm.outbound"));
	}

}
