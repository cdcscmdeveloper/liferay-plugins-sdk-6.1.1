package my.ep.web.scheduler.job.sm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.sm.ISmSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.sm.SmSchedulerConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SoftCertRenewalReminder implements Job {
	public static Logger log = EpLogger.getLogger(SoftCertRenewalReminder.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		ISmSchedulerSO reqSO = (ISmSchedulerSO) BusinessDelegate.getInstance().
				getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISmSchedulerSO.class);
		
		List<String> statusCodeLs = new ArrayList<String>();
		statusCodeLs.add(SmSchedulerConstants.CERT_MODE_NEW);
		statusCodeLs.add(SmSchedulerConstants.CERT_MODE_RENEW);
		
		Date d1 = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d1);
		cal.add(Calendar.DATE, SmSchedulerConstants.SOFTCERT_RENEWAL_DAY);
		d1 = cal.getTime();
		List<Date> dateLs = new ArrayList<Date>();
		dateLs.add(d1);
		
		log.info("date1>>>>" + d1);
		
		SmSchedulerReqDTO reqDTO = new SmSchedulerReqDTO();
		reqDTO.setStrStatusCodeLs(statusCodeLs);
		reqDTO.setDateLs(dateLs);
		
		try{
			reqSO.softcertRenewReminder(reqDTO);
		}catch(Exception ex){
			log.info("********SoftCertRenewalReminder Exception**********" + ex.getMessage());
		}finally{
			reqSO = null;
		}
		
	}

}
