/*
 * 
 */

package my.ep.web.scheduler.sc.ebidding.jobtasklistener;

import my.ep.factory.EpLogger;
import my.ep.web.backingbean.sc.schedulermanager.SchedulerManagerBackingBean;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * @author Venkat Chanumolu
 */
public class EPQuartzJobListener implements JobListener {

	public static final String LISTENER_NAME = "ePQuartzJobListener";
	
	public static Logger log = EpLogger
			.getLogger(EPQuartzJobListener.class);

	// must return a name
	@Override
	public String getName() {
		return LISTENER_NAME;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		final String jobName = context.getJobDetail().getKey().toString();
		log.debug("*****From EPQuartzJobListener jobToBeExecuted is*****"+jobName);
	}

	// No idea what this method does
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		log.debug("Job is vetoed by trigger: "
				+ context.getJobDetail().getKey().toString());
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			 JobExecutionException jobException) {
		final String jobName = context.getJobDetail().getKey().toString();
		log.debug("*****From EPQuartzJobListener jobExecutedWas******"+jobName);
		if (!jobException.getMessage().equals("")) {
			log.debug("Exception thrown by: " + jobName
					+ " Exception: " + jobException.getMessage());
		}
	}

}
