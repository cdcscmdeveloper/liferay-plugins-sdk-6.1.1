package my.ep.web.scheduler.sc.ebidding.JobTasks;

import java.util.List;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Venkat Chanumolu
 */

// this is the job that gets executed for every 60 seconds
// and iterates through the records of ScBidSchedule List and updates the status by
// comparing the start and end times
public class ContinuousBidSchedulerJob implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		List<ScBidSchedule> nextDayScheduledBids = null;
		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDTO = new BiddingReqDTO();
			
			BiddingResDTO resDTO = new BiddingResDTO();
			if (resDTO.isSuccessful()) {
			}

		} finally {
			biddingSO = null;
		}

	}

}