package my.ep.web.scheduler.job.ct;

import java.util.Date;

import my.ep.biz.service.ct.ICtSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * This is scheduled job to send Imposed Service Tax Notification 
 * to contract admin. 
 * 
 * @author NancyAng
 * @since 29/2/2016
 */

@DisallowConcurrentExecution
public class CtImposedServiceTaxNotificationJob implements Job {
	
	public static Logger log = EpLogger.getLogger(CtImposedServiceTaxNotificationJob.class);
	
	@SuppressWarnings("unused")
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		ICtSchedulerSO schedulerSO = null;
		try {
			
			Date currentDate = DateUtils.getCurrentDateWithoutTime();
			Date gstReliefDueDate = DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, ContractConst.GST_RELIEF_DUE_DATE);
			Date sevenDaysPrior = DateUtils.minusDays(gstReliefDueDate, 7);
			Date fourteenDaysPrior = DateUtils.minusDays(gstReliefDueDate, 14);
			Date thirtyDaysPrior = DateUtils.minusDays(gstReliefDueDate, 30);
			
			// Only execute 30 days, 14 days and 7 days prior to 31 March 2017 and on 31 March 2017
			if ((currentDate.getTime() == gstReliefDueDate.getTime())
					|| (currentDate.getTime() == sevenDaysPrior.getTime())
					|| (currentDate.getTime() == fourteenDaysPrior.getTime())
					|| (currentDate.getTime() == thirtyDaysPrior.getTime())) {
				schedulerSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, ICtSchedulerSO.class);			
				schedulerSO.sendImposedServiceTaxNotification();
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			schedulerSO = null;
		}
	}
}