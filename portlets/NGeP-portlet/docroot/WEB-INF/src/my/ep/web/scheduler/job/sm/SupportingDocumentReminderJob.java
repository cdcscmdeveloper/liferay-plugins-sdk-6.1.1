package my.ep.web.scheduler.job.sm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SupportingDocumentReminderJob implements Job {
	public static Logger log = EpLogger.getLogger(SupportingDocumentReminderJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info(">>>>>>>>>>>SupportingDocumentReminderJob - Start<<<<<<<<<<<<<<<<");
		try{
			SmServiceLookup.getSmSchedulerSO().offlineSupportingDocumentReminder();
		}catch(Exception ex){
			log.info("********SupportingDocumentReminder Exception**********" + ex.getMessage());
		}
		log.info(">>>>>>>>>>>SupportingDocumentReminderJob - End<<<<<<<<<<<<<<<<");
	}

}
