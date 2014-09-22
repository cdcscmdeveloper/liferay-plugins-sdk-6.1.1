/*
 * 
 */

package my.ep.web.scheduler.sc.ebidding.jobtasklistener;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import my.ep.web.scheduler.sc.ebidding.constants.EPSchedulerConstants;
import my.ep.web.scheduler.sc.ebidding.triggers.EPContinuousSimpleJobTrigger;
import my.ep.web.scheduler.sc.ebidding.triggers.EPDailyCronJobTrigger;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;

/**
 * @author Venkat Chanumolu
 */
public class EPQuartzJobContextListener implements ServletContextListener {

	private Scheduler scheduler;
	private JobKey ePContinousJobKey = new JobKey(EPSchedulerConstants.EP_CONTINOUS_JOB,EPSchedulerConstants.EP_JOB_GROUP);
    private JobKey ePDailyJobKey = new JobKey(EPSchedulerConstants.EP_DAILY_JOB,EPSchedulerConstants.EP_JOB_GROUP);

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		System.out.println("**********Starting shutdown scheduler**********");
		try {
			if(scheduler.isStarted()){
				scheduler.shutdown(false);
			}
		} catch (final Exception e) {
			System.out.println("**********Failed to shutdown scheduler**********");
		}

	}

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		try {
			
			System.out.println("**********Starting initialize scheduler***********");
			
			// initializing scheduler
			
			scheduler = ((StdSchedulerFactory) contextEvent.getServletContext()
					.getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY))
					.getScheduler();
			
			 //add job key to listener
            scheduler.getListenerManager().addJobListener(new EPQuartzJobListener(), KeyMatcher.keyEquals(ePContinousJobKey));
            scheduler.getListenerManager().addJobListener(new EPQuartzJobListener(), KeyMatcher.keyEquals(ePDailyJobKey));
			
            //start scheduler
            if(scheduler.isShutdown()){
            	scheduler.start();
            }
            
            //start executing jobs
            EPContinuousSimpleJobTrigger.run(scheduler,ePContinousJobKey);
            EPDailyCronJobTrigger.run(scheduler,ePDailyJobKey);
            
            // loop jobs by group
         			for (String groupName : scheduler.getJobGroupNames()) {

         				// get jobkey
         				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher
         						.jobGroupEquals(groupName))) {
         					System.out
         							.println("*****Job Group****"+groupName+ " job key is...."+jobKey.getName() );
         				}
         			}
			
         			System.out.println("**********Scheduler status********** "
        					+ (scheduler.isStarted() ? "Started" : "Shutdown"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
