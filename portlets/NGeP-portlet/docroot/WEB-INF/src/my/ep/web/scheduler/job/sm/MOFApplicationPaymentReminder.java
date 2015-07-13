package my.ep.web.scheduler.job.sm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.sm.ISmSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.sm.SmSchedulerConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MOFApplicationPaymentReminder implements Job{
	public static Logger log = EpLogger.getLogger(MOFApplicationPaymentReminder.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		ISmSchedulerSO reqSO = (ISmSchedulerSO) BusinessDelegate.getInstance().
				getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISmSchedulerSO.class);
		
		Date d1 = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d1);
		cal.add(Calendar.DATE, SmSchedulerConstants.PAYMENT_REMINDER_DAY);
		d1 = cal.getTime();
		
		SmSchedulerReqDTO reqDTO = new SmSchedulerReqDTO();
		List<Long> statusCodeLs = new ArrayList<Long>();
		statusCodeLs.add(SmSchedulerConstants.STATUS_PROCESSING_PAYMENT); //Processing Fee
		statusCodeLs.add(SmSchedulerConstants.STATUS_APPLICATION_PAYMENT); //Application Payment
		reqDTO.setStatusCodeLs(statusCodeLs);
		reqDTO.setParamDate(d1);
		
		
		try{
			reqSO.mofApplicationPaymentReminder(reqDTO);
		}catch(Exception ex){
			log.info("********SoftCertRenewalReminder Exception**********" + ex.getMessage());
		}finally{
			reqSO = null;
		}
	}

}
