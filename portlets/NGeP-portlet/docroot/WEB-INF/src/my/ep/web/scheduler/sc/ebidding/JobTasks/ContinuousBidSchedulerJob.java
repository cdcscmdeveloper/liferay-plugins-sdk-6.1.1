package my.ep.web.scheduler.sc.ebidding.JobTasks;

import java.util.ArrayList;
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

		List<Long> localBidScheduleIdList = schedulerHelper
				.getJobBidScheduleIdList();
		IBiddingSO biddingSO = null;
		List<Long> toDeleteIdList = null;

		for (int i = 0; i < localBidScheduleIdList.size(); i++) {

			try {
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
				
				BiddingReqDTO reqDTO = new BiddingReqDTO();
				reqDTO.setBidScheduleId(localBidScheduleIdList.get(i));
				BiddingResDTO resDTO = biddingSO
						.retrieveBidScheduleById(reqDTO);
				ScBidSchedule currentSchedule = resDTO
						.getScBidSchedule();
				if(currentSchedule.getStartDate().equals(getCompareTime())){
					
					if (currentSchedule.getPmStatus().getStatusId()
							.equals(BidStatusConst.BID_PENDING_BIDDING)) {
						
						currentSchedule.getPmStatus().setStatusId(
								BidStatusConst.BID_IN_PROGRESS);
						reqDTO.setScBidSchedule(currentSchedule);
						resDTO = biddingSO
								.updateSchedulerJobBidSchedule(reqDTO);
					}else if(currentSchedule.getPmStatus().getStatusId()
							.equals(BidStatusConst.BID_CANCELLED)){
						if(toDeleteIdList == null){
							toDeleteIdList = new ArrayList<Long>();
							toDeleteIdList.add(currentSchedule.getBidScheduleId());
						}else{toDeleteIdList.add(currentSchedule.getBidScheduleId());}
					}
						
				}else if (currentSchedule.getEndDate().equals(getCompareTime())){
					//TODO check what if paused bid is not resumed
					if (currentSchedule.getPmStatus().getStatusId()
							.equals(BidStatusConst.BID_IN_PROGRESS)|| currentSchedule.getPmStatus().getStatusId()
							.equals(BidStatusConst.BID_PAUSED) ){
						currentSchedule.getPmStatus().setStatusId(
								BidStatusConst.BID_COMPLETED);
						reqDTO.setScBidSchedule(currentSchedule);
						resDTO = biddingSO
								.updateSchedulerJobBidSchedule(reqDTO);
						if(resDTO.isSuccessful()){
							if(toDeleteIdList == null){
								toDeleteIdList = new ArrayList<Long>();
								toDeleteIdList.add(currentSchedule.getBidScheduleId());
							}else{toDeleteIdList.add(currentSchedule.getBidScheduleId());}
						}
						
					}
					
				}
				
			} finally {
				biddingSO = null;
			}
		}
		
		if (toDeleteIdList != null && toDeleteIdList.size()>0){
			for(Long deletedId : toDeleteIdList){
				schedulerHelper
				.getJobBidScheduleIdList().remove(deletedId);
			}
			toDeleteIdList = null;
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