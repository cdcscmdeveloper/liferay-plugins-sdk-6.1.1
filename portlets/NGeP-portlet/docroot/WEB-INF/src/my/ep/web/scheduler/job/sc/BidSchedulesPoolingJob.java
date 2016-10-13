package my.ep.web.scheduler.job.sc;

import java.util.ArrayList;
import java.util.List;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
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
	
	/* (non-Javadoc)
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
					schedulerHelper
							.setJobBidScheduleIdList(jobBidScheduleIdList);
					jobBidScheduleIdList = null;
				} else {
					SchedulerHelper schedulerHelper = SchedulerHelper
							.getInstance();
					schedulerHelper.getJobBidScheduleList().clear();
					schedulerHelper.getJobBidScheduleIdList().clear();
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