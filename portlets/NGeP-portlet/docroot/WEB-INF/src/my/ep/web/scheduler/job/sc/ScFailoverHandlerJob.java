package my.ep.web.scheduler.job.sc;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Venkat Chanumolu
 * @since 08.03.2016
 */

// this job gets executed for every 60 seconds
public class ScFailoverHandlerJob implements Job {

	private static Logger log = EpLogger.getLogger(ScFailoverHandlerJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
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
					.retrieveAndUpdateFailoverBidSchedules(reqDTO);
			
			log.info("Bid Schedules Fail Over Handling Successful"+resDTO.isSuccessful());

		} 
		catch(Exception e){
			log.info("Bid schedules fail over handling not successful",e);
		}
		finally {
			biddingSO = null;
		}

	}

}