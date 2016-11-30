package my.ep.web.scheduler.job.sm;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import my.ep.factory.EpLogger;
import my.ep.web.util.sm.helper.SmServiceLookup;

public class MOFApplicationPaymentReminderJob implements Job{
	public static Logger log = EpLogger.getLogger(MOFApplicationPaymentReminderJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info(">>>>>>>>>>>MOFApplicationPaymentReminderJob - Start<<<<<<<<<<<<<<<<");
		try{
			SmServiceLookup.getSmSchedulerSO().mofApplicationPaymentReminder();
		}catch(Exception ex){
			log.info("********MOFApplicationPaymentReminderJob Exception**********" + ex.getMessage());
		}
		
		log.info(">>>>>>>>>>>MOFApplicationPaymentReminderJob - End<<<<<<<<<<<<<<<<");
	}

}
