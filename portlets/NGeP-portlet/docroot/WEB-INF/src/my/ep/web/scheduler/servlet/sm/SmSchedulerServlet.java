package my.ep.web.scheduler.servlet.sm;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.sm.SmSchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.sm.DeletePendingActivationJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class SmSchedulerServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2265394558051568423L;
	public static Logger log = EpLogger
			.getLogger(SmSchedulerServlet.class);


	public void init(ServletConfig config) throws ServletException {
		log.info("start init sm servlet....");
		
		//Sm Delete Pending User Activation Job
		EpSchedulerHelper.scheduleJob(new JobKey(
				SmSchedulerConstants.SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB,
				SmSchedulerConstants.SM_JOB_GROUP),
				DeletePendingActivationJob.class,
				SmSchedulerConstants.SM_DAILY_DEL_PEND_ACTV_PERSONEL_JOB_DESC, "0 11 16 * * ?");
		
		log.info("end init sm servlet....");
	}
}
