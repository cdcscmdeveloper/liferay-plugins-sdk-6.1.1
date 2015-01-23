package my.ep.web.scheduler.servlet.cm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.cm.CmSchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.cm.CmBatchIntegrationJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class CmSchedulerServlet extends HttpServlet {
	private static final long serialVersionUID = 8691110272078406918L;
	public static Logger log = EpLogger
			.getLogger(CmSchedulerServlet.class);
	
	public void init(ServletConfig config) throws ServletException {
				
				// CM Integration Continuous job
				EpSchedulerHelper.scheduleJob(new JobKey(
						CmSchedulerConstants.CM_INTEGRATION_CONTINUOUS_JOB,
						CmSchedulerConstants.CM_INTEGRATION_JOB_GROUP),
						CmBatchIntegrationJob.class, "Job to run every two hours",
						"0 0 8,20 * * ?");
	}
}
