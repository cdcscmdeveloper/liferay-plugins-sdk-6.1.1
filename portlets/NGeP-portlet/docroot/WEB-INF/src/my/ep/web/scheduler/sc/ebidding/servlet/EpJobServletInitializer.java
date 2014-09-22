package my.ep.web.scheduler.sc.ebidding.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.sc.ebidding.constants.EPSchedulerConstants;
import my.ep.web.scheduler.sc.ebidding.triggers.EPContinuousSimpleJobTrigger;
import my.ep.web.scheduler.sc.ebidding.triggers.EPDailyCronJobTrigger;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

public class EpJobServletInitializer extends HttpServlet{
	
	public static Logger log = EpLogger
			.getLogger(EpJobServletInitializer.class);
	
	private static final long serialVersionUID = 2591757747850240619L;
	private Scheduler scheduler;
	private JobKey ePContinousJobKey = new JobKey(EPSchedulerConstants.EP_CONTINOUS_JOB,EPSchedulerConstants.EP_JOB_GROUP);
    private JobKey ePDailyJobKey = new JobKey(EPSchedulerConstants.EP_DAILY_JOB,EPSchedulerConstants.EP_JOB_GROUP);
	
	public void init(ServletConfig config) throws ServletException{
		try {
	    	//initialize
	        scheduler = ((StdSchedulerFactory) config.getServletContext().getAttribute(
	                QuartzInitializerListener.QUARTZ_FACTORY_KEY)).getScheduler();
	        

	        //start executing jobs
	        EPContinuousSimpleJobTrigger.run(scheduler,ePContinousJobKey);
            EPDailyCronJobTrigger.run(scheduler,ePDailyJobKey);
	        
	    } catch (final Exception e) {
	    	log.error(e.getMessage());
	    }
	}

}
