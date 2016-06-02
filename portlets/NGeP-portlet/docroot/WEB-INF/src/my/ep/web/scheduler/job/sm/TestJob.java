package my.ep.web.scheduler.job.sm;

import java.util.Date;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.util.DateUtils;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

public class TestJob implements Job {
	public static Logger log = EpLogger.getLogger(TestJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info(">>>>>>>>>>>>start executing test job with at:"
				+ DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date())
				+ "<<<<<<<<<<<<<<");

		try {
			log.info(">>>>>>>SCHEDULER INSTANCE:"
					+ EpSchedulerHelper.getScheduler().getSchedulerInstanceId());
		} catch (SchedulerException e) {
			log.debug(">>>>>>>ERROR MESSAGE RUN TEST JOB:" + e.getMessage()
					+ "<<<<<<<<<<<<");
		}
		log.info(">>>>>>>>>>>>finish executing test job with at:"
				+ DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date())
				+ "<<<<<<<<<<<<<<");

	}
}
