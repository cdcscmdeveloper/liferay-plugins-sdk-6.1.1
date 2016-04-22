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
import my.ep.web.scheduler.job.sc.ContinuousBidSchedulerJob;
import my.ep.web.scheduler.job.sc.InviteSuppliersJob;
import my.ep.web.scheduler.job.sc.ProposalValidityExpiaryJob;
import my.ep.web.scheduler.job.sc.ScFailoverHandlerJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class ScSchedulerServlet extends HttpServlet {

	public static Logger log = EpLogger.getLogger(ScSchedulerServlet.class);

	private static final long serialVersionUID = 2591757747850240619L;

	public void init(ServletConfig config) throws ServletException {

		// Sc Daily job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.SC_DAILY_JOB,
								SchedulerConstants.SC_JOB_GROUP),
						BidSchedulesPoolingJob.class,
						SchedulerConstants.SC_DAILY_JOB_DESCRIPTION,
						EpPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SC_DAILY_JOB));
		
		// daily Job to be run at server startup

		EpSchedulerHelper.runJobOnce(new JobKey(
				SchedulerConstants.SC_DAILY_JOB,
				SchedulerConstants.SC_JOB_GROUP),BidSchedulesPoolingJob.class);
		

		

		// Sc Continous job
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.SC_CONTINOUS_JOB,
								SchedulerConstants.SC_JOB_GROUP),
						ContinuousBidSchedulerJob.class,
						SchedulerConstants.SC_CONTINUOUS_JOB_DESCRIPTION,
						EpPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SC_CONTINUOUS_JOB));
		

		// Sc Daily job for published Quatation Tenders.
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.SC_UPDATE_SUPPLIERS_JOB,
								SchedulerConstants.SC_JOB_GROUP),
						InviteSuppliersJob.class,
						SchedulerConstants.SC_UPDATE_SUPPLIERS_JOB_DESCRIPTION,
						EpPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SC_UPDATE_SUPPLIERS));
	

	
		// Sc Daily job for Proposal Validity Expiary Quatation Tenders.
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.SC_QT_PROPOSAL_VALIDITY_EXPIRY,
								SchedulerConstants.SC_JOB_GROUP),
						ProposalValidityExpiaryJob.class,
						SchedulerConstants.SC_PROPOSAL_EXPIRY_JOB_DESCRIPTION,
						EpPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SC_PROPOSAL_VALIDITY_EXPIRY));
		

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.GFMAS_SST_INFORMATION,
								SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
						SSTInformationJob.class,
						SchedulerConstants.GFMAS_SC_OUT_JOB_DESC,
						EpPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_SC_1GFMAS_SC_OUTBOUND));

		// failover handler Job to be run at server startup
		EpSchedulerHelper.runJobOnce(new JobKey(
				SchedulerConstants.SC_FAILOVER_HANDLER_JOB,
				SchedulerConstants.SC_JOB_GROUP),ScFailoverHandlerJob.class);
		
		

	}

}
