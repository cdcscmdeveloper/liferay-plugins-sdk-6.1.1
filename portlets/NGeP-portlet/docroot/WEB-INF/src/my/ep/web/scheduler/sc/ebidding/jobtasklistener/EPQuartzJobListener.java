/*
 * 
 */

package my.ep.web.scheduler.sc.ebidding.jobtasklistener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * @author Venkat Chanumolu
 */
public class EPQuartzJobListener implements JobListener {

	public static final String LISTENER_NAME = "ePQuartzJobListener";

	// must return a name
	@Override
	public String getName() {
		return LISTENER_NAME;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		final String jobName = context.getJobDetail().getKey().toString();
		System.out.println("*****From EPQuartzJobListener jobToBeExecuted is*****"+jobName);
	}

	// No idea what this method does
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		System.out.println("Job is vetoed by trigger: "
				+ context.getJobDetail().getKey().toString());
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			 JobExecutionException jobException) {
		final String jobName = context.getJobDetail().getKey().toString();
		System.out.println("*****From EPQuartzJobListener jobExecutedWas******"+jobName);
		if (!jobException.getMessage().equals("")) {
			System.out.println("Exception thrown by: " + jobName
					+ " Exception: " + jobException.getMessage());
		}
	}

}
