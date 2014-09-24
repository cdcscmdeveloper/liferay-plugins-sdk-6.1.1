package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.sc.bidding.ScBidSchedule;

/**
 * Singleton Helper For Scheduler.
 * 
 * @author Venkat Chanumolu
 * @since 24.09.2014
 * 
 */

public class SchedulerHelper {
	
	private static SchedulerHelper instance;
	
	private static List<ScBidSchedule> jobBidScheduleList = new ArrayList<ScBidSchedule>();
	
	
	/**
	 * Get singleton of SchedulerHelper
	 * 
	 * @return SchedulerHelper singleton
	 */
	public static SchedulerHelper getInstance(){
		if (instance == null){
			instance = new SchedulerHelper();
		}
		return instance;
	}


	/**
	 * @return the jobBidScheduleList
	 */
	public List<ScBidSchedule> getJobBidScheduleList() {
		return jobBidScheduleList;
	}


	/**
	 * @param jobBidScheduleList the jobBidScheduleList to set
	 */
	public void setJobBidScheduleList(List<ScBidSchedule> jobBidScheduleList) {
		this.jobBidScheduleList = jobBidScheduleList;
	}
	
	

}
