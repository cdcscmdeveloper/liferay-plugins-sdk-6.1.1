package my.ep.web.scheduler.job.eperunding;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.EperundingIntegration;
import my.ep.web.integration.common.EperundingTemplate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class EperundingBatchIntegrationJob implements Job {

	public static Logger log = EpLogger
			.getLogger(EperundingBatchIntegrationJob.class);

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
		Thread currentThread = Thread.currentThread();
		try {
			eperundingTemplate.createConsultantProfile();
			Thread.sleep(5000L);
			eperundingTemplate.createDeletedCompany();
			Thread.sleep(5000L);
			eperundingTemplate.createRegisteredCategory();
			Thread.sleep(5000L);
			eperundingTemplate.createGovernmentProject();
			Thread.sleep(5000L);
			eperundingTemplate.createAuthorizedSignatory();
			Thread.sleep(5000L);
			eperundingTemplate.createManagementPersonnel();
			Thread.sleep(5000L);
			eperundingTemplate.createBoardOfDirector();
			Thread.sleep(5000L);
			eperundingTemplate.createEquiytyOwner();
			Thread.sleep(5000L);
			eperundingTemplate.createBranchCompany();
			Thread.sleep(5000L);
			eperundingTemplate.createCompanyOwner();
			Thread.sleep(5000L);
			eperundingTemplate.createAssociatedCompany();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// INtfSO ntfSO = (INtfSO) EpNtfBusinessDelegate.getInstance()
		// .getOSBServiceObject(INtfSO.class);
		//
		// TestNtfViaEmail testNtfViaEmail = new TestNtfViaEmail();
		// testNtfViaEmail.sendFilestoMail(ntfSO);
		log.info("EperundingBatchIntegrationJob end");

	}

}