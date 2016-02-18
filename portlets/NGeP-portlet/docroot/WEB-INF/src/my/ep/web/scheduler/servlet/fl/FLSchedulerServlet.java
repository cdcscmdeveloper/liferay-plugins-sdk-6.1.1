package my.ep.web.scheduler.servlet.fl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.gfmas.PaymentInformationJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class FLSchedulerServlet extends HttpServlet {
	private static final long serialVersionUID = 5961523576286300095L;
	public static Logger log = EpLogger
			.getLogger(FLSchedulerServlet.class);
	
	public void init(ServletConfig config) throws ServletException {
		
		// Expire the contract that no more effective. 
	/*	EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.GFMAS_PAYMENT_INFORMATION_INBOUND,
				SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
				PaymentInformationJob.class,
				SchedulerConstants.GFMAS_FL_CONTINUOUS_JOB_DESC,
				EpPropertiesUtils.getInstance().getProperty("phis.fl.outbound"));*/
	}


}
