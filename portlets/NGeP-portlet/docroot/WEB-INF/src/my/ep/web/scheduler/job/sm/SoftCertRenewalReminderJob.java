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

public class SoftCertRenewalReminderJob implements Job {
	public static Logger log = EpLogger.getLogger(SoftCertRenewalReminderJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		List<String> statusCodeLs = new ArrayList<String>();
		statusCodeLs.add(SchedulerConstants.CERT_MODE_NEW);
		statusCodeLs.add(SchedulerConstants.CERT_MODE_RENEW);
		
		Date d1 = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d1);
		cal.add(Calendar.DATE, SchedulerConstants.SOFTCERT_RENEWAL_DAY);
		d1 = cal.getTime();
		List<Date> dateLs = new ArrayList<Date>();
		dateLs.add(d1);
		
		log.info("date1>>>>" + d1);
		
		SmSchedulerReqDTO reqDTO = new SmSchedulerReqDTO();
		reqDTO.setStrStatusCodeLs(statusCodeLs);
		reqDTO.setDateLs(dateLs);
		
		try{
			SmServiceLookup.getSmSchedulerSO().softcertRenewReminder(reqDTO);
		}catch(Exception ex){
			log.info("********SoftCertRenewalReminder Exception**********" + ex.getMessage());
		}finally{
			d1 = null;
		}
		
	}

}
