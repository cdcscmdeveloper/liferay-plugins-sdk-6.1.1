package my.ep.web.scheduler.job.sm;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.factory.EpLogger;
import my.ep.web.util.sm.helper.SmServiceLookup;

public class ComplaintAccusationLetterReminderJob  implements Job{
	public static Logger log = EpLogger.getLogger(ComplaintShowCauseLetterReminderJob.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("*********ComplaintAccusationLetterReminderJob start*******");
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		reqSO.accusationLetterNotification();
		log.info("*********ComplaintAccusationLetterReminderJob end*******");
	}
}
