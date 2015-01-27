package my.ep.web.scheduler.job.cm;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.GfmasIntegration;
import my.ep.web.integration.common.PhisIntegration;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Venkat Chanumolu
 */

@DisallowConcurrentExecution
public class CmBatchIntegrationJob implements Job {
	
	public static Logger log = EpLogger.getLogger(CmBatchIntegrationJob.class);
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
	   log.info("CmBatchIntegrationJob ");
       PhisIntegration cmIntegration = new PhisIntegration();	
       GfmasIntegration gfmasIntegration = new GfmasIntegration();
       gfmasIntegration.sstInformationGfmas();
       //gfmasIntegration.kodAkaunInPutStream();
       cmIntegration.createFilesforPHIS();
       
       cmIntegration.votFund();
       cmIntegration.votFundDana();
     //  cmIntegration.glAccount();
       cmIntegration.programActivity();
       cmIntegration.project();
		
	}

}