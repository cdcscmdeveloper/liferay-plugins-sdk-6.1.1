package my.ep.web.scheduler.job.sc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.SchedulerHelper;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job Class to start and close bid sessions
 * 
 * @author Venkat Chanumolu
 */

// this job gets executed for every 60 seconds
public class ContinuousBidSchedulerJob implements Job {

	private static Logger log = EpLogger
			.getLogger(ContinuousBidSchedulerJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.info("%%%%% ContinuousBidSchedulerJob start...... :) :) :)");
		List<Long> localBidScheduleIdList = null;

		Map<String, Object> jobMap = context.getJobDetail().getJobDataMap()
				.getWrappedMap();
		if (jobMap.containsKey(SchedulerConstants.JOB_BID_ID_LIST)) {
			System.out.println("MMMMM Job2 JobDataMap Contains the Key");
			List<Long> mapBidScheduleIdList = (List<Long>) jobMap
					.get(SchedulerConstants.JOB_BID_ID_LIST);
			if (mapBidScheduleIdList != null && !mapBidScheduleIdList.isEmpty()) {
				System.out
						.println("MMMMM BidId List in the Map found in Job2 and size is "
								+ mapBidScheduleIdList.size());
				for (Long mapBidId : mapBidScheduleIdList) {
					System.out.println("MMMMM mapBid It in Job2" + mapBidId);
				}
			}
		}

		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			List<Long> toDeleteIdList = null;
			int failedBidSchedulesCount = 0;
			BiddingReqDTO reqDTO = new BiddingReqDTO();
			List<Long> cachedBidIdList = biddingSO.getBiddingListFromCache();
			localBidScheduleIdList = cachedBidIdList;
			if (cachedBidIdList != null && !cachedBidIdList.isEmpty()) {
				System.out.println("Printing bidIdList from cache in job2");
				for (Long cachedBidId : cachedBidIdList) {
					System.out
							.println("cached bid Id from Job 2" + cachedBidId);
				}
			}

			if (localBidScheduleIdList != null
					&& !localBidScheduleIdList.isEmpty()) {
				log.info("$$$$$ active bid Schedules size at this run is:"
						+ localBidScheduleIdList.size());
				for (int i = 0; i < localBidScheduleIdList.size(); i++) {
					ScBidSchedule currentSchedule = null;
					try {
						reqDTO.setBidScheduleId(localBidScheduleIdList.get(i));
						BiddingResDTO resDTO = biddingSO
								.retrieveBidScheduleById(reqDTO);
						currentSchedule = resDTO.getScBidSchedule();
						if (currentSchedule.getStartDate().equals(
								getCompareTime())) {
							System.out
							.println("Start time compare successfull for bidSchedule ="
									+ currentSchedule.getBidNo());
							if (currentSchedule.getPmStatus().getStatusId()
									.equals(BidStatusConst.BID_PENDING_BIDDING)) {
								System.out
								.println("status check after Start time successfull for bidSchedule ="
										+ currentSchedule.getBidNo());
								currentSchedule
										.setPmStatus(getPmStatusByStatusId(BidStatusConst.BID_IN_PROGRESS));
								reqDTO.setScBidSchedule(currentSchedule);
								log.info("##### bid schedule is going to be updated to in progress for bid no: "
										+ currentSchedule.getBidNo());
								resDTO = biddingSO
										.updateSchedulerJobBidSchedule(reqDTO);
								if (resDTO.isSuccessful()) {
									log.info("$%$%$% Bidding is in Progress for bid no"
											+ currentSchedule.getBidNo());
								}

							} else if (currentSchedule.getPmStatus()
									.getStatusId()
									.equals(BidStatusConst.BID_CANCELLED)) {
								log.info("##### bid schedule is cancelled : "
										+ currentSchedule.getBidNo());
								if (toDeleteIdList == null) {
									toDeleteIdList = new ArrayList<Long>();
									toDeleteIdList.add(currentSchedule
											.getBidScheduleId());
								} else {
									toDeleteIdList.add(currentSchedule
											.getBidScheduleId());
								}
							}else{
								System.out
								.println("status check after Start time not successfull for bidSchedule ="
										+ currentSchedule.getBidNo());
							}

						} else if (currentSchedule.getEndDate().equals(
								getCompareTime())) {
							System.out
							.println("End time compare successfull for bidSchedule ="
									+ currentSchedule.getBidNo());
							if (currentSchedule.getPmStatus().getStatusId()
									.equals(BidStatusConst.BID_IN_PROGRESS)) {
								System.out
								.println("Status check after End time successfull for bidSchedule ="
										+ currentSchedule.getBidNo());
								currentSchedule
										.setPmStatus(getPmStatusByStatusId(BidStatusConst.BID_COMPLETED));
								reqDTO.setScBidSchedule(currentSchedule);
								log.info("##### bid schedule is going to be updated to completed for bid no: "
										+ currentSchedule.getBidNo());
								resDTO = biddingSO
										.updateSchedulerJobBidSchedule(reqDTO);

								if (resDTO.isSuccessful()) {
									log.info("$%$%$% Bidding Completed for bid no"
											+ currentSchedule.getBidNo());
									if (toDeleteIdList == null) {
										toDeleteIdList = new ArrayList<Long>();
										toDeleteIdList.add(currentSchedule
												.getBidScheduleId());
									} else {
										toDeleteIdList.add(currentSchedule
												.getBidScheduleId());
									}
								}

							}else{
								System.out
								.println("Status check after End time failed for bidSchedule ="
										+ currentSchedule.getBidNo());
							}

						} else if ((currentSchedule.getStartDate().before(
								getCompareTime()) && currentSchedule
								.getPmStatus().getStatusId()
								.equals(BidStatusConst.BID_PENDING_BIDDING))
								|| (currentSchedule.getEndDate().before(
										getCompareTime()) && currentSchedule
										.getPmStatus().getStatusId()
										.equals(BidStatusConst.BID_IN_PROGRESS))) {
							log.info("$%$%$% bid seesion creation/closing failed for bid no"
									+ currentSchedule.getBidNo());
							failedBidSchedulesCount++;
							if (toDeleteIdList == null) {
								toDeleteIdList = new ArrayList<Long>();
								toDeleteIdList.add(currentSchedule
										.getBidScheduleId());
							} else {
								toDeleteIdList.add(currentSchedule
										.getBidScheduleId());
							}
						}

					} catch (Exception e) {
						log.info("$%$%$% Exception while maintaining bid seesion with bid no"
								+ currentSchedule.getBidNo());
						log.error(e.getLocalizedMessage());
					}

				}
				if (toDeleteIdList != null && toDeleteIdList.size() > 0) {

					SchedulerHelper.getJobBidScheduleIdList().removeAll(
							toDeleteIdList);
					localBidScheduleIdList.removeAll(toDeleteIdList);
					System.out
							.println("printing removed bid id list size before update to cache"
									+ localBidScheduleIdList.size());
					biddingSO.updateBiddingListToCache(localBidScheduleIdList);
					context.getJobDetail()
							.getJobDataMap()
							.put(SchedulerConstants.JOB_BID_ID_LIST,
									localBidScheduleIdList);

				}

				if (failedBidSchedulesCount > 0) {
					try {
						log.info("#$#$#$#$ bid session fail over handling initiated");
						biddingSO.retrieveAndUpdateFailoverBidSchedules(reqDTO);
					} catch (Exception e) {
						log.info("$%$%$% Exception while handling failed bidSchedules");
						log.error(e.getLocalizedMessage());
					}

				}

			} else {
				log.info("$$$$$ no active bid Schedules in this run");
			}

		} catch (Exception e) {
			log.error(e);
		}

		finally {
			biddingSO = null;
		}

		if (localBidScheduleIdList != null) {
			log.info("$$$$$ active bid Schedules size at the end of this run is:"
					+ localBidScheduleIdList.size());
		}

		log.info("%%%%% ContinuousBidSchedulerJob end...... :) :) :)");
	}

	private Date getCompareTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date compareTime = cal.getTime();
		return compareTime;
	}

	/**
	 * Get PmStatus by status id
	 * 
	 * @param statusId
	 *            the status id
	 * @return the PmStatus instance
	 */
	private PmStatus getPmStatusByStatusId(Long statusId) {
		if (statusId != null) {
			IStatusSO statusSO = null;
			try {
				statusSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setStatusId(statusId);
				return statusSO.retrieveStatusByStatusId(reqDTO).getPmStatus();

			} finally {
				statusSO = null;
			}
		}

		return null;
	}
}