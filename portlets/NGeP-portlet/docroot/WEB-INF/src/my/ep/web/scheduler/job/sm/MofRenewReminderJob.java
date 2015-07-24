package my.ep.web.scheduler.job.sm;

import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MofRenewReminderJob implements Job { 

	public static Logger log = EpLogger
			.getLogger(MofRenewReminderJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		SmSchedulerReqDTO reqDTO = new SmSchedulerReqDTO();
		
		SmSchedulerResDTO resDTO = SmServiceLookup.getSmSchedulerSO().retriveMOFAccountsForRenewalNotification(reqDTO);
		
		if(resDTO.isSuccessful()) {
			log.info("MOF Renew Reminders successfully sent.");
		} else {
			log.info("MOF Renew Reminders failed to send.");
		}
	}

}