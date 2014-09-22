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

// this is the job that gets executed for every 5(interval not final) seconds
// and iterates through the records of temporary table and changes the status by
// comparing the start and end times
public class ContinuousBidSchedulerJob implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println("********** inside execute method of ContinuousBidSchedulerJob **********");
		System.out.println("***********job  to schedule bids and this job runs forever***********");
		List<ScBidSchedule> nextDayScheduledBids = null;
		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDTO = new BiddingReqDTO();

			// TODO need API to fetch all the records from the temporary table

			
			BiddingResDTO resDTO = new BiddingResDTO();
			if (resDTO.isSuccessful()) {
				// nextDayScheduledBids = resDTO.getActiveBidSchedules() ;
			}
			

		} finally {
			biddingSO = null;
		}

	}

}