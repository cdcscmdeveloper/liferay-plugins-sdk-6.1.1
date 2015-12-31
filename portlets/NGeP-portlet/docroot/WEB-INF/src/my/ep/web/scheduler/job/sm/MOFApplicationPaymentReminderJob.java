package my.ep.web.scheduler.job.sm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MOFApplicationPaymentReminderJob implements Job{
	public static Logger log = EpLogger.getLogger(MOFApplicationPaymentReminderJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Date d1 = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d1);
		cal.add(Calendar.DATE, SchedulerConstants.PAYMENT_REMINDER_DAY);
		d1 = cal.getTime();
		
		SmSchedulerReqDTO reqDTO = new SmSchedulerReqDTO();
		List<Long> statusCodeLs = new ArrayList<Long>();
		statusCodeLs.add(SchedulerConstants.STATUS_PROCESSING_PAYMENT); //Processing Fee
		statusCodeLs.add(SchedulerConstants.STATUS_APPLICATION_PAYMENT); //Application Payment
		reqDTO.setStatusCodeLs(statusCodeLs);
		reqDTO.setParamDate(d1);
		
		
		try{
			SmServiceLookup.getSmSchedulerSO().mofApplicationPaymentReminder(reqDTO);
		}catch(Exception ex){
			log.info("********SoftCertRenewalReminder Exception**********" + ex.getMessage());
		}finally{
			d1 = null;
		}
	}

}
