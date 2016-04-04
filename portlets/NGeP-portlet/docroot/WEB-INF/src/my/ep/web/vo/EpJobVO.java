package my.ep.web.vo;

import java.io.Serializable;

import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.Trigger;

public class EpJobVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4778444005489515499L;

	private JobKey jobKey;
	private Class<? extends Job> jobClass;
	private Trigger trigger;
	private String status;
	private String action;
	private String state;
	private String description;
	private String jobClassName;
	private String prevJobName;
	private String cronSec;
	private String cronMin;
	private String cronHr;
	private String cronDay;
	private String cronMonth;
	private String cronYear;
	private String jobName;
	private String jobGroup;

	public EpJobVO() {

	}

	public EpJobVO(JobKey jobKey, Trigger trigger, String status,
			String action, String state, String description,
			Class<? extends Job> jobClass) {

		this.jobKey = jobKey;
		this.trigger = trigger;
		this.status = status;
		this.action = action;
		this.state = state;
		this.description = description;
		this.jobClass = jobClass;
	}

	public JobKey getJobKey() {
		return jobKey;
	}

	public void setJobKey(JobKey jobKey) {
		this.jobKey = jobKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Class<? extends Job> getJobClass() {
		return jobClass;
	}

	public void setJobClass(Class<? extends Job> jobClass) {
		this.jobClass = jobClass;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}

	public String getPrevJobName() {
		return prevJobName;
	}

	public void setPrevJobName(String prevJobName) {
		this.prevJobName = prevJobName;
	}

	public String getCronSec() {
		return cronSec;
	}

	public void setCronSec(String cronSec) {
		this.cronSec = cronSec;
	}

	public String getCronMin() {
		return cronMin;
	}

	public void setCronMin(String cronMin) {
		this.cronMin = cronMin;
	}

	public String getCronHr() {
		return cronHr;
	}

	public void setCronHr(String cronHr) {
		this.cronHr = cronHr;
	}

	public String getCronDay() {
		return cronDay;
	}

	public void setCronDay(String cronDay) {
		this.cronDay = cronDay;
	}

	public String getCronMonth() {
		return cronMonth;
	}

	public void setCronMonth(String cronMonth) {
		this.cronMonth = cronMonth;
	}

	public String getCronYear() {
		return cronYear;
	}

	public void setCronYear(String cronYear) {
		this.cronYear = cronYear;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

}
