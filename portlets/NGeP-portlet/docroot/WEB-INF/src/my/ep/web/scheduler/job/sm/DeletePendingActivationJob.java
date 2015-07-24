package my.ep.web.scheduler.job.sm;

import java.util.Date;

import my.ep.factory.EpLogger;
import my.ep.web.util.DateUtils;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DeletePendingActivationJob implements Job {
	public static Logger log = EpLogger
			.getLogger(DeletePendingActivationJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info(">>>>>>>>>>>>start executing delete pending activation job with at:"
				+ DateUtils.dateToString("dd/MM/yyyy HH:mm", new Date())
				+ "<<<<<<<<<<<<<<");
		SmServiceLookup.getSmSchedulerSO().resetExpiredPendingActivationPersonnel();
		log.info(">>>>>>>>>>>>finish executing delete pending activation job with at:"
				+ DateUtils.dateToString("dd/MM/yyyy HH:mm", new Date())
				+ "<<<<<<<<<<<<<<");
	}

}
