package my.ep.web.scheduler.sc.ebidding.JobTasks;

import java.util.List;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Venkat Chanumolu
 */

/*
 * This is a cron job and is responsible to delete the records in the temporary
 * table , and populates the next day scheduled bids into the temporary table
 * This can have only one instance and executes only once.
 */
@DisallowConcurrentExecution
public class BidSchedulesPoolingCronJob implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		System.out.println("********** inside execute method of BidSchedulesPoolingCronJob **********");

		System.out
				.println("job to pool next day scheduled  bids list  and this job runs only once at everyday 11:55PM");
		
		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDTO = new BiddingReqDTO();
			
			//TODO need API to delete all the records in the temporary table and populates and saves the records into the temporary table.
			
			//TODO we need one method from Bidding SO to retrieve all next day bids  
			BiddingResDTO resDTO = new BiddingResDTO();// = biddingSO.retrieveBidScheduleDtl(reqDTO);
			List<ScBidSchedule> nextDayScheduledBids = null ;
			
			if(resDTO.isSuccessful()){
				nextDayScheduledBids = resDTO.getActiveBidSchedules() ;
			}
			// check status is Pending Bidding or not
			//TODO change hard coded statusID
			if (nextDayScheduledBids != null ){
				//TODO need API to save all the next day schedules to the temporary table
				
			}

		} finally {
			biddingSO = null;
		}
		

	}

}