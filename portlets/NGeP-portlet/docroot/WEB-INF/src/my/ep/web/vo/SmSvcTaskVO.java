package my.ep.web.vo;

import java.io.Serializable;

public class SmSvcTaskVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long stateId;
	private String stateName;
	private int totalTaskBefore;
	private int totalTaskAfter;
	
	public String getStateName() {
		return stateName;
	}
	
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public int getTotalTaskBefore() {
		return totalTaskBefore;
	}

	public void setTotalTaskBefore(int totalTaskBefore) {
		this.totalTaskBefore = totalTaskBefore;
	}

	public int getTotalTaskAfter() {
		return totalTaskAfter;
	}

	public void setTotalTaskAfter(int totalTaskAfter) {
		this.totalTaskAfter = totalTaskAfter;
	}
}
