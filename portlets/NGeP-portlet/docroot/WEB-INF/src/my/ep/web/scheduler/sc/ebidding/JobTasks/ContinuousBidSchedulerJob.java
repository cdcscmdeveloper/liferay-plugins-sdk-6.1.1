package my.ep.web.scheduler.sc.ebidding.JobTasks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.SchedulerHelper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Venkat Chanumolu
 */

// this job gets executed for every 60 seconds
public class ContinuousBidSchedulerJob implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		SchedulerHelper schedulerHelper = SchedulerHelper.getInstance();

		List<ScBidSchedule> localBidScheduleList = schedulerHelper
				.getJobBidScheduleList();

		if (localBidScheduleList != null && localBidScheduleList.size() > 0) {

			for (int i = 0; i < localBidScheduleList.size(); i++) {
				ScBidSchedule scBidSchedule = localBidScheduleList.get(i);

				if (scBidSchedule.getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_PENDING_BIDDING)) {

					if (scBidSchedule.getStartDate().equals(getCompareTime())) {

						IBiddingSO biddingSO = null;
						try {
							biddingSO = BusinessDelegate.getInstance()
									.getServiceObject(
											ModuleCodeConst.SUPPLIER_SELECTION,
											IBiddingSO.class);
							BiddingReqDTO reqDTO = new BiddingReqDTO();
							reqDTO.setBidScheduleId(scBidSchedule
									.getBidScheduleId());
							BiddingResDTO resDTO = biddingSO
									.retrieveBidScheduleById(reqDTO);
							ScBidSchedule currentSchedule = resDTO
									.getScBidSchedule();

							if (currentSchedule
									.getPmStatus()
									.getStatusId()
									.equals(scBidSchedule.getPmStatus()
											.getStatusId())) {
								currentSchedule.getPmStatus().setStatusId(
										BidStatusConst.BID_IN_PROGRESS);
								reqDTO.setScBidSchedule(currentSchedule);
								resDTO = biddingSO
										.updateSchedulerJobBidSchedule(reqDTO);
								if (resDTO.isSuccessful()) {
									schedulerHelper.getJobBidScheduleList()
											.set(i, resDTO.getScBidSchedule());
								}
							} else {

								schedulerHelper.getJobBidScheduleList().set(i,
										currentSchedule);
							}
						} finally {
							biddingSO = null;
						}
					}
				}
				if (scBidSchedule.getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_IN_PROGRESS)
						&& scBidSchedule.getEndDate().equals(getCompareTime())) {

					IBiddingSO biddingSO = null;

					try {
						biddingSO = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION,
										IBiddingSO.class);
						BiddingReqDTO reqDTO = new BiddingReqDTO();
						reqDTO.setBidScheduleId(scBidSchedule
								.getBidScheduleId());
						BiddingResDTO resDTO = biddingSO
								.retrieveBidScheduleById(reqDTO);
						ScBidSchedule currentSchedule = resDTO
								.getScBidSchedule();
						if (currentSchedule
								.getPmStatus()
								.getStatusId()
								.equals(scBidSchedule.getPmStatus()
										.getStatusId())) {
							currentSchedule.getPmStatus().setStatusId(
									BidStatusConst.BID_COMPLETED);
							reqDTO.setScBidSchedule(currentSchedule);
							resDTO = biddingSO
									.updateSchedulerJobBidSchedule(reqDTO);
							if (resDTO.isSuccessful()) {
								schedulerHelper.getJobBidScheduleList().set(i,
										currentSchedule);
							}
						} else {
							schedulerHelper.getJobBidScheduleList().set(i,
									currentSchedule);
						}

					} finally {
						biddingSO = null;
					}

				}
			}
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