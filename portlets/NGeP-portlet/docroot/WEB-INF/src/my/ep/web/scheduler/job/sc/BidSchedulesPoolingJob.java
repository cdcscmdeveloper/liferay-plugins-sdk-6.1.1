package my.ep.web.scheduler.job.sc;

import java.util.ArrayList;
import java.util.List;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.SchedulerHelper;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job Class to pool current day bid schedules
 * 
 * @author Venkat Chanumolu
 */

@DisallowConcurrentExecution
public class BidSchedulesPoolingJob implements Job {

	public static Logger log = EpLogger.getLogger(BidSchedulesPoolingJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDTO = new BiddingReqDTO();
			List<Long> cachedBiddingList = biddingSO.getBiddingListFromCache();
			if(cachedBiddingList != null && !cachedBiddingList.isEmpty()){
				System.out.println("^^^^^ printing cached bid Id list from job1");
				for(Long cachedBiddingid : cachedBiddingList){
					System.out.println("^^^^^ cached bid Id from job1"+cachedBiddingid);
				}
			}
			
			BiddingResDTO resDTO = biddingSO
					.retriveSchedulerJobBidSchedulesList(reqDTO);
			if (resDTO.isSuccessful()) {
				if (resDTO.getActiveBidSchedules() != null) {
					
					
					log.info("jobBidSchedulesList size is"
							+ resDTO.getActiveBidSchedules().size());
					SchedulerHelper schedulerHelper = SchedulerHelper
							.getInstance();
					schedulerHelper.setJobBidScheduleList(resDTO
							.getActiveBidSchedules());
					List<Long> jobBidScheduleIdList = new ArrayList<Long>();
					for (ScBidSchedule ScBidSchedule : resDTO
							.getActiveBidSchedules()) {
						jobBidScheduleIdList.add(ScBidSchedule
								.getBidScheduleId());
					}
					// to add active bidSchedule id list to cache
					biddingSO.updateBiddingListToCache(jobBidScheduleIdList);
					
					schedulerHelper
							.setJobBidScheduleIdList(jobBidScheduleIdList);
					EpSchedulerHelper.updateJobDetailData(
							SchedulerConstants.SC_CONTINOUS_JOB,
							SchedulerConstants.SC_JOB_GROUP,
							SchedulerConstants.JOB_BID_ID_LIST,
							jobBidScheduleIdList);
					System.out.println("JobDataMap is set with the active bid Id list in Job1");
				}else{
					EpSchedulerHelper.updateJobDetailData(
							SchedulerConstants.SC_CONTINOUS_JOB,
							SchedulerConstants.SC_JOB_GROUP,
							SchedulerConstants.JOB_BID_ID_LIST,
							new ArrayList<Long>());
				}

				log.info("%$%$%$ BidSchedulesPoolingJob job executed successfully @"
						+ DateUtils.getCurrentDate());
			} else {
				log.info("%$%$%$ BidSchedulesPoolingJob job execution failed @"
						+ DateUtils.getCurrentDate());
			}

		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			// TODO need to define error handling scenarios
		} finally {
			biddingSO = null;
		}
	}

}