package my.ep.web.scheduler.job.crm;

import my.ep.biz.service.intg.crm.ICRMServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UpdateSupplierUserInfoJob implements Job{

	public static Logger log = EpLogger
			.getLogger(UpdateSupplierUserInfoJob.class);
	
	public static ICRMServiceSO  crmServiceSO = EpEjbBusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.INTEGRATION,
					ICRMServiceSO.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("UpdateSupplierUserInfoJob start");
		
		crmServiceSO.updateSupplierUserInfoService();
		
		log.info("UpdateSupplierUserInfoJob End");
		
	}

}
