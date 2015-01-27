package my.ep.web.scheduler.servlet.cm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.cm.CmSchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.eperunding.EperundingBatchIntegrationJob;
import my.ep.web.scheduler.job.gfmas.GfmasBatchIntegrationJob;
import my.ep.web.scheduler.job.phis.PhisBatchIntegrationJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class CmSchedulerServlet extends HttpServlet {
	private static final long serialVersionUID = 8691110272078406918L;
	public static Logger log = EpLogger
			.getLogger(CmSchedulerServlet.class);
	
	public void init(ServletConfig config) throws ServletException {
				
				// CM Integration Continuous job
				/*EpSchedulerHelper.scheduleJob(new JobKey(
						CmSchedulerConstants.CM_INTEGRATION_CONTINUOUS_JOB,
						CmSchedulerConstants.CM_INTEGRATION_JOB_GROUP),
						CmBatchIntegrationJob.class, "Job to run every two hours",
						"0 0 8,20 * * ?");*/
				//PHIS Integration Continuous job
				EpSchedulerHelper.scheduleJob(new JobKey(
						CmSchedulerConstants.PHIS_INTEGRATION_CONTINUOUS_JOB,
						CmSchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						PhisBatchIntegrationJob.class, CmSchedulerConstants.PHIS_INTEGRATION_CONTINUOUS_JOB_DESC,
						"0 0 8,20 * * ?");
				//GFMAS Integration Continuous job
				EpSchedulerHelper.scheduleJob(new JobKey(
						CmSchedulerConstants.GFMAS_INTEGRATION_CONTINUOUS_JOB,
						CmSchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
						GfmasBatchIntegrationJob.class, CmSchedulerConstants.GFMAS_INTEGRATION_CONTINUOUS_JOB_DESC,
						"0 0 9,21 * * ?");
				//PHIS Integration Continuous job
				EpSchedulerHelper.scheduleJob(new JobKey(
						CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB,
						CmSchedulerConstants.EPERUNDING_INTEGRATION_JOB_GROUP),
						EperundingBatchIntegrationJob.class, CmSchedulerConstants.EPERUNDING_INTEGRATION_CONTINUOUS_JOB_DESC,
						"0 0 7,19 * * ?");
	}
}
