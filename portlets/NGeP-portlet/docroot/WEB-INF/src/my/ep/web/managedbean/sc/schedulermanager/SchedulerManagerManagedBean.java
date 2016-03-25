package my.ep.web.managedbean.sc.schedulermanager;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.EpJobVO;

import org.quartz.Scheduler;

@ManagedBean(name = "schedulerManagerManagedBean")
@SessionScoped
public class SchedulerManagerManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4940842520664848758L;

	private List<EpJobVO> epSchedulerJobList;
	private List<EpJobVO> filteredEpSchedulerJobList;
	private List<String> jobGroups;
	private String jobGroup;
	private EpJobVO selectedJobVO;
	private Scheduler scheduler;
	private List<ScBidSchedule> currentDayBids;

	/**
	 * @return the currentDayBids
	 */
	public List<ScBidSchedule> getCurrentDayBids() {
		return currentDayBids;
	}

	/**
	 * @param currentDayBids
	 *            the currentDayBids to set
	 */
	public void setCurrentDayBids(List<ScBidSchedule> currentDayBids) {
		this.currentDayBids = currentDayBids;
	}

	/**
	 * @return the scheduler
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * @param scheduler
	 *            the scheduler to set
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public SchedulerManagerManagedBean() {
		epSchedulerJobList = new ArrayList<>();
	}

	/**
	 * @return the epSchedulerJobList
	 */
	public List<EpJobVO> getEpSchedulerJobList() {
		return epSchedulerJobList;
	}

	/**
	 * @param epSchedulerJobList
	 *            the epSchedulerJobList to set
	 */
	public void setEpSchedulerJobList(List<EpJobVO> epSchedulerJobList) {
		this.epSchedulerJobList = epSchedulerJobList;
	}

	public EpJobVO getSelectedJobVO() {
		return selectedJobVO;
	}

	public void setSelectedJobVO(EpJobVO selectedJobVO) {
		this.selectedJobVO = selectedJobVO;
	}

	public List<EpJobVO> getFilteredEpSchedulerJobList() {
		return filteredEpSchedulerJobList;
	}

	public void setFilteredEpSchedulerJobList(
			List<EpJobVO> filteredEpSchedulerJobList) {
		this.filteredEpSchedulerJobList = filteredEpSchedulerJobList;
	}

	public List<String> getJobGroups() {
		return jobGroups;
	}

	public void setJobGroups(List<String> jobGroups) {
		this.jobGroups = jobGroups;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

}
