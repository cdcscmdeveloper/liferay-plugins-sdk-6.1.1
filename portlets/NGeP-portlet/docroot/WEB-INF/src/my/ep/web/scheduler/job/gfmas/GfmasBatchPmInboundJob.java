package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/*
 * @author P Ganga prasad
 * @author Venkat Chanumolu
 */
//Review this based on the requirement
@DisallowConcurrentExecution
public class GfmasBatchPmInboundJob implements Job {

	public static Logger log = EpLogger.getLogger(GfmasBatchPmInboundJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GfmasBatchPmInboundJob  : Start");
		
		//TODO If each service call is dependent on each other please revert back and leave a note
		IGfmasServiceSO gfmasServiceSO = null;
		gfmasServiceSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.INTEGRATION, IGfmasServiceSO.class);
		try {
			gfmasServiceSO.pejabatPerakaunanGfmasInbound();
		} catch (Exception e) {
			log.info("GfmasBatchPmInboundJob :" + e.getMessage());
		}

		try {
			gfmasServiceSO.pegawaiPengawalGfmasInbound();
		} catch (Exception e) {
			log.info("GfmasBatchPmInboundJob :" + e.getMessage());
		}
		try {
			gfmasServiceSO.kodAkaunGfmasInbound();
		} catch (Exception e) {
			log.info("GfmasBatchPmInboundJob :" + e.getMessage());
		}
		try {
			gfmasServiceSO.kodBankGfmasInbound();
		} catch (Exception e) {
			log.info("GfmasBatchPmInboundJob :" + e.getMessage());
		}
		try {
			gfmasServiceSO.kumpulanPtjGfmasInbound();
		} catch (Exception e) {
			log.info("GfmasBatchPmInboundJob :" + e.getMessage());
		}
		try {
			gfmasServiceSO.votGfmasInbound();
		} catch (Exception e) {
			log.info("GfmasBatchPmInboundJob :" + e.getMessage());
		}
		try {
			gfmasServiceSO.votDanaGfmasInbound();
		} catch (Exception e) {
			log.info("GfmasBatchPmInboundJob :" + e.getMessage());
		}
		try {
			gfmasServiceSO.projectGfmasInbound();
		} catch (Exception e) {
			log.info("GfmasBatchPmInboundJob :" + e.getMessage());
		}
		try {
			gfmasServiceSO.programAktivitiGfmasInbound();
		} catch (Exception e) {
			log.info("GfmasBatchPmInboundJob :" + e.getMessage());
		}
		try {
			gfmasServiceSO.masterDataPtjGfmasInbound();
		} catch (Exception e) {
			log.info("GfmasBatchPmInboundJob :" + e.getMessage());
		} finally {
			gfmasServiceSO = null;
		}

		log.info("GfmasBatchPmInboundJob  : End");

	}

}