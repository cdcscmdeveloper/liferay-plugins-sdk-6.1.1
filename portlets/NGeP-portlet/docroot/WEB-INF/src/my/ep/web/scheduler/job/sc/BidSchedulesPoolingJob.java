package my.ep.web.scheduler.job.sc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
		log.info("!@#$% BidSchedulesPoolingJob is run @"+DateUtils.getCurrentDate());

		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDTO = new BiddingReqDTO();
			List<Long> cachedBiddingList = biddingSO.getBiddingListFromCache();
			if (cachedBiddingList != null && !cachedBiddingList.isEmpty()) {
				log.debug("^^^^^ printing cached bid Id list from job1");
				for (Long cachedBiddingid : cachedBiddingList) {
					log.debug("^^^^^ cached bid Id from BidSchedulesPoolingJob : "
							+ cachedBiddingid);
				}
			}

			BiddingResDTO resDTO = biddingSO
					.retriveSchedulerJobBidSchedulesList(reqDTO);
			if (resDTO.isSuccessful()) {
				if (resDTO.getActiveBidSchedules() != null) {
					log.debug("jobBidSchedulesList size is"
							+ resDTO.getActiveBidSchedules().size());
					List<Long> jobBidScheduleIdList = new ArrayList<Long>();
					if (resDTO.getActiveBidSchedules() != null) {
						for (ScBidSchedule ScBidSchedule : resDTO
								.getActiveBidSchedules()) {
							jobBidScheduleIdList.add(ScBidSchedule
									.getBidScheduleId());
						}
						// to add active bidSchedule id list to cache
						biddingSO.updateBiddingListToCache(jobBidScheduleIdList);
					}else{
						log.debug("%$%$%$ Active bid Schedules for next day not found");
					}
				}

				log.info("%$%$%$ BidSchedulesPoolingJob job executed successfully @"
						+ DateUtils.getCurrentDate());
			} else {
				log.info("%$%$%$ BidSchedulesPoolingJob job execution failed @"
						+ DateUtils.getCurrentDate());
			}

		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
		} finally {
			biddingSO = null;
		}
	}

}