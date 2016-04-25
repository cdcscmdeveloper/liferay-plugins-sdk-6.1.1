package my.ep.web.scheduler.job.sc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.SchedulerHelper;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Venkat Chanumolu
 */

// this job gets executed for every 60 seconds
public class ContinuousBidSchedulerJob implements Job {

	private static Logger log = EpLogger
			.getLogger(ContinuousBidSchedulerJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		IBiddingSO biddingSO = null;
		try {
			// SchedulerHelper schedulerHelper = SchedulerHelper.getInstance();
			List<Long> localBidScheduleIdList = SchedulerHelper
					.getJobBidScheduleIdList();

			List<Long> toDeleteIdList = null;

			for (int i = 0; i < localBidScheduleIdList.size(); i++) {

				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

				BiddingReqDTO reqDTO = new BiddingReqDTO();
				reqDTO.setBidScheduleId(localBidScheduleIdList.get(i));
				BiddingResDTO resDTO = biddingSO
						.retrieveBidScheduleById(reqDTO);
				ScBidSchedule currentSchedule = resDTO.getScBidSchedule();
				if (currentSchedule.getStartDate().equals(getCompareTime())) {

					if (currentSchedule.getPmStatus().getStatusId()
							.equals(BidStatusConst.BID_PENDING_BIDDING)) {

						currentSchedule.getPmStatus().setStatusId(
								BidStatusConst.BID_IN_PROGRESS);
						reqDTO.setScBidSchedule(currentSchedule);
						resDTO = biddingSO
								.updateSchedulerJobBidSchedule(reqDTO);
						if (resDTO.isSuccessful()) {
							debugLogger("*****Bidding is in Progress for BidSchedule with id"
									+ resDTO.getScBidSchedule()
											.getBidScheduleId());
						}

					} else if (currentSchedule.getPmStatus().getStatusId()
							.equals(BidStatusConst.BID_CANCELLED)) {
						if (toDeleteIdList == null) {
							toDeleteIdList = new ArrayList<Long>();
							toDeleteIdList.add(currentSchedule
									.getBidScheduleId());
						} else {
							toDeleteIdList.add(currentSchedule
									.getBidScheduleId());
						}
					}

				} else if (currentSchedule.getEndDate()
						.equals(getCompareTime())) {
					// TODO check what if paused bid is not resumed
					debugLogger("currentScheduleId : "
							+ currentSchedule.getBidScheduleId());
					debugLogger("currentScheduleEndDate : "
							+ currentSchedule.getEndDate());
					debugLogger("comparecurrentTime : " + getCompareTime());
					debugLogger("Is schedule end : "
							+ currentSchedule.getEndDate().equals(
									getCompareTime()));
					if (currentSchedule.getPmStatus().getStatusId()
							.equals(BidStatusConst.BID_IN_PROGRESS)
							|| currentSchedule.getPmStatus().getStatusId()
									.equals(BidStatusConst.BID_PAUSED)) {
						currentSchedule.getPmStatus().setStatusId(
								BidStatusConst.BID_COMPLETED);
						reqDTO.setScBidSchedule(currentSchedule);
						resDTO = biddingSO
								.updateSchedulerJobBidSchedule(reqDTO);

						if (resDTO.isSuccessful()) {
							debugLogger("*****Bidding Completed for bidSchedule with id*****"
									+ resDTO.getScBidSchedule()
											.getBidScheduleId());
							if (toDeleteIdList == null) {
								toDeleteIdList = new ArrayList<Long>();
								toDeleteIdList.add(currentSchedule
										.getBidScheduleId());
							} else {
								toDeleteIdList.add(currentSchedule
										.getBidScheduleId());
							}
						}

					}

				}

			}
			if (toDeleteIdList != null && toDeleteIdList.size() > 0) {
				for (Long deletedId : toDeleteIdList) {
					SchedulerHelper.getJobBidScheduleIdList().remove(deletedId);
				}
				toDeleteIdList = null;
			}

		}catch(Exception e){
			log.error(e);
		}

		finally {
			biddingSO = null;
		}

	}

	private Date getCompareTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date compareTime = cal.getTime();
		return compareTime;
	}

}