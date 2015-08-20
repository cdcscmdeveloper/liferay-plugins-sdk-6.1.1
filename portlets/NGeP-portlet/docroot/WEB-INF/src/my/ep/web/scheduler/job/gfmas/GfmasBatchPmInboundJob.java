package my.ep.web.scheduler.job.gfmas;

import java.io.IOException;

import my.ep.factory.EpLogger;
import my.ep.web.integration.common.GfmasIntegration;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;



@DisallowConcurrentExecution
public class GfmasBatchPmInboundJob implements Job {

	public static Logger log = EpLogger
			.getLogger(GfmasBatchPmInboundJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	public static void main(String[] args) throws IOException {
		GfmasIntegration gfmasIntegration = new GfmasIntegration();
		gfmasIntegration.pejabatPerakaunanGfmasInbound();
	}


	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GfmasBatchPmInboundJob  : Start");

		GfmasIntegration gfmasIntegration = new GfmasIntegration();
		
		gfmasIntegration.pejabatPerakaunanGfmasInbound();
		
		gfmasIntegration.pegawaiPengawalGfmasInbound();
		
        gfmasIntegration.votGfmasInbound();
		
		gfmasIntegration.votDanaGfmasInbound();
		
		gfmasIntegration.programAktivitiGfmasInbound();	
		
        gfmasIntegration.projectGfmasInbound();
		
		gfmasIntegration.kodAkaunGfmasInbound();
		
		gfmasIntegration.kumpulanPtjGfmasInbound();
		
		gfmasIntegration.masterDataPtjGfmasInbound();
		
		gfmasIntegration.kodBankGfmasInbound();	
		
		log.info("GfmasBatchPmInboundJob  : End");
		
	}

}