package my.ep.web.scheduler.servlet.pm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.sc.BidSchedulesPoolingJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class PmSchedulerServlet extends HttpServlet {
	
	private static final long serialVersionUID = -4832945404532466168L;
	public static Logger log = EpLogger
			.getLogger(PmSchedulerServlet.class);
	
	public void init(ServletConfig config) throws ServletException {
		
		// Sc Daily job
				EpSchedulerHelper.scheduleJob(new JobKey(
						SchedulerConstants.SC_DAILY_JOB,
						SchedulerConstants.SC_JOB_GROUP),
						BidSchedulesPoolingJob.class,
						"Job run every day at 11:55 PM", "0 55 23 * * ?");
	
	}
}
