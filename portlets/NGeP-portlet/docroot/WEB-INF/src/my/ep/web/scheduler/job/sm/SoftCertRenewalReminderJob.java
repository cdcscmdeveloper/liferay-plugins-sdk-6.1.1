package my.ep.web.scheduler.job.sm;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import my.ep.factory.EpLogger;
import my.ep.web.util.sm.helper.SmServiceLookup;

public class SoftCertRenewalReminderJob implements Job {
	public static Logger log = EpLogger.getLogger(SoftCertRenewalReminderJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info(">>>>>>>>>>>SoftCertRenewalReminderJob - Start<<<<<<<<<<<<<<<<");
		try{
			SmServiceLookup.getSmSchedulerSO().softcertRenewReminder();
		}catch(Exception ex){
			log.info("********SoftCertRenewalReminder Exception**********" + ex.getMessage());
		}
		log.info(">>>>>>>>>>>SoftCertRenewalReminderJob - End<<<<<<<<<<<<<<<<");
	}

}
