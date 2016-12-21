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
	private String jobClassSimpleName;
	private String jobName;
	private String jobGroup;
	private String cronExpression;
	private String cronExpressionDescription;

	public EpJobVO() {

	}

	public EpJobVO(JobKey jobKey, Trigger trigger, String status,
			String action, String state, String description,
			Class<? extends Job> jobClass, String cronExpression) {

		this.jobKey = jobKey;
		this.trigger = trigger;
		this.status = status;
		this.action = action;
		this.state = state;
		this.description = description;
		this.jobClass = jobClass;
		this.cronExpression = cronExpression;
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

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getJobClassSimpleName() {
		return jobClassSimpleName;
	}

	public void setJobClassSimpleName(String jobClassSimpleName) {
		this.jobClassSimpleName = jobClassSimpleName;
	}

	public String getCronExpressionDescription() {
		return cronExpressionDescription;
	}

	public void setCronExpressionDescription(String cronExpressionDescription) {
		this.cronExpressionDescription = cronExpressionDescription;
	}

}
