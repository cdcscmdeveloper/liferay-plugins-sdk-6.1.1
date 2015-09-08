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
		// TODO Auto-generated method stub
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		
		//reminder for 90 days have passed
		Date date1 = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		cal.add(Calendar.DATE, -90);
		date1 = cal.getTime();
		System.out.println("date1>>>>" + date1);
		
		/*Date date2 = new Date();
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		cal2.add(Calendar.DATE, -54);
		date2 = cal2.getTime();
		System.out.println("date2>>>>" + date2);*/
		
		List<Date> dateLs = new ArrayList<Date>(); 
		dateLs.add(date1);
		//dateLs.add(date2);
		
		List<Long> statusCodeLs = new ArrayList<Long>();
		statusCodeLs.add(20200L);
		statusCodeLs.add(20101L);
		
		SmSchedulerReqDTO reqDTO = new SmSchedulerReqDTO();
		reqDTO.setStatusCodeLs(statusCodeLs);
		reqDTO.setDateLs(dateLs);
		
		try{
			reqSO.offlineSupportingDocumentReminder(reqDTO);
		}catch(Exception ex){
			log.info("********SupportingDocumentReminder Exception**********" + ex.getMessage());
		}finally{
			date1 = null;
		}
	}

}
