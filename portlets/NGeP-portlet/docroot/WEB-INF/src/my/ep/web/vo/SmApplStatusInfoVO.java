package my.ep.web.vo;

import java.io.Serializable;

public class SmApplStatusInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4853256486683754992L;

	private String submissionDate;
	private String daysLeft;
	private String daysLeftOriginal;
	private String createdByAndDate;
	private String modifiedByAndDate;

	public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getDaysLeft() {
		return daysLeft;
	}

	public void setDaysLeft(String daysLeft) {
		this.daysLeft = daysLeft;
	}

	public String getCreatedByAndDate() {
		return createdByAndDate;
	}

	public void setCreatedByAndDate(String createdByAndDate) {
		this.createdByAndDate = createdByAndDate;
	}

	public String getModifiedByAndDate() {
		return modifiedByAndDate;
	}

	public void setModifiedByAndDate(String modifiedByAndDate) {
		this.modifiedByAndDate = modifiedByAndDate;
	}

	public String getDaysLeftOriginal() {
		return daysLeftOriginal;
	}

	public void setDaysLeftOriginal(String daysLeftOriginal) {
		this.daysLeftOriginal = daysLeftOriginal;
	}

}
