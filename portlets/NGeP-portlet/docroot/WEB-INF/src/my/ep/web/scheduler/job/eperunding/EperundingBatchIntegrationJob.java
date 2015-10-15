package my.ep.web.scheduler.job.eperunding;

import my.ep.factory.EpLogger;
import my.ep.ntf.service.INtfSO;
import my.ep.util.lookupservice.EpNtfBusinessDelegate;
import my.ep.web.integration.common.EperundingIntegration;
import my.ep.web.integration.common.EperundingTemplate;
import my.ep.web.integration.common.TestNtfViaEmail;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;



@DisallowConcurrentExecution
public class EperundingBatchIntegrationJob implements Job {
	
	public static Logger log = EpLogger.getLogger(EperundingBatchIntegrationJob.class);
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
	  log.info("EperundingBatchIntegrationJob start");
	  EperundingTemplate eperundingTemplate = new EperundingIntegration();
	  eperundingTemplate.createConsultantProfile();
	  eperundingTemplate.createDeletedCompany();
	  eperundingTemplate.createRegisteredCategory();
	  eperundingTemplate.createGovernmentProject();
	  
	  eperundingTemplate.createFilesAuthMgtEqtBODforEperunding();
	  
	  eperundingTemplate.createBranchCompany();
	  eperundingTemplate.createCompanyOwner();
	  eperundingTemplate.createAssociatedCompany();
	  
	  INtfSO ntfSO = (INtfSO) EpNtfBusinessDelegate.getInstance()
				.getOSBServiceObject(INtfSO.class);
		
		TestNtfViaEmail testNtfViaEmail = new TestNtfViaEmail();
		testNtfViaEmail.sendFilestoMail(ntfSO);
	  log.info("EperundingBatchIntegrationJob end");
       
		
	}

}