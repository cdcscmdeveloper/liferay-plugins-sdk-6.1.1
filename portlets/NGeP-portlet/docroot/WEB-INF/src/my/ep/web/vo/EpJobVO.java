package my.ep.web.vo;

import java.io.Serializable;

import org.quartz.JobKey;
import org.quartz.Trigger;

public class EpJobVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4778444005489515499L;

	private JobKey jobKey;
	private Trigger trigger;
	private String status;
	private String action;
	private String state;
	private String description;
	
	public EpJobVO(){
		
	}

	public EpJobVO(JobKey jobKey, Trigger trigger, String status,String action,String state,String description) {

		this.jobKey = jobKey;
		this.trigger = trigger;
		this.status = status;
		this.action = action;
		this.state = state;
		this.description = description;
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

}
