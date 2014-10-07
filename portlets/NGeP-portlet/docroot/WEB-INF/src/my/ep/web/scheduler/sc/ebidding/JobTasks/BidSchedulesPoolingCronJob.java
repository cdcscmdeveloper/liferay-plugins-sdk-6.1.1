package my.ep.web.scheduler.sc.ebidding.JobTasks;

import java.util.ArrayList;
import java.util.List;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.SchedulerHelper;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Venkat Chanumolu
 */

/*
 * This is a cronjob and is responsible for populating scheduled bids of today, if run before 11:55PM ,otherwise  next day  into the Static List variable
 * This can have only one instance and executes only once.
 */
@DisallowConcurrentExecution
public class BidSchedulesPoolingCronJob implements Job {
	
	public static Logger log = EpLogger.getLogger(BidSchedulesPoolingCronJob.class);
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDTO = new BiddingReqDTO();
			BiddingResDTO resDTO = biddingSO.retriveSchedulerJobBidSchedulesList(reqDTO);
			if(resDTO.isSuccessful()){
				if(resDTO.getActiveBidSchedules()!= null){
					log.info("jobBidSchedulesList size is"+resDTO.getActiveBidSchedules().size());
					SchedulerHelper schedulerHelper = SchedulerHelper.getInstance();
					schedulerHelper.setJobBidScheduleList(resDTO.getActiveBidSchedules());
					List<Long> jobBidScheduleIdList = new ArrayList<Long>();
					for(ScBidSchedule ScBidSchedule : resDTO.getActiveBidSchedules()){
						jobBidScheduleIdList.add(ScBidSchedule.getBidScheduleId());
					}
					schedulerHelper.setJobBidScheduleIdList(jobBidScheduleIdList);
					jobBidScheduleIdList = null;
				}else {
					SchedulerHelper schedulerHelper = SchedulerHelper.getInstance();
					schedulerHelper.getJobBidScheduleList().clear();
					schedulerHelper.getJobBidScheduleIdList().clear();
				}
			}

		} finally {
			biddingSO = null;
		}
	}

}