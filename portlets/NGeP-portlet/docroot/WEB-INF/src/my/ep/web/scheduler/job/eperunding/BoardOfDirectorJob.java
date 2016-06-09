package my.ep.web.scheduler.job.eperunding;

import my.ep.biz.service.intg.eperunding.IEperundingServiceSO;
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
public class BoardOfDirectorJob implements Job {

	public static Logger log = EpLogger.getLogger(BoardOfDirectorJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("BoardOfDirectorJob start");
		IEperundingServiceSO eperundingServiceSO = null;
		try {
			eperundingServiceSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.INTEGRATION,
							IEperundingServiceSO.class);
			eperundingServiceSO.createBoardOfDirector();
			// please add resDTO to log better

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			// error handling follows here
		} finally {
			eperundingServiceSO = null;
		}
		log.info("BoardOfDirectorJob end");

	}

}
