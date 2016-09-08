package my.ep.web.scheduler.job.epol;

import my.ep.biz.service.intg.epol.IEpolServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author Mohd Yusri
 */
public class CompanyInfoJob implements Job {
	
	public static Logger log = EpLogger.getLogger(CompanyInfoJob.class);
	
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("EPOL CompanyInfoJob start");
		IEpolServiceSO  epolServiceSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.INTEGRATION,
						IEpolServiceSO.class);
		epolServiceSO.createCompanyInfo();
		log.info("EPOL CompanyInfoJob end");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
