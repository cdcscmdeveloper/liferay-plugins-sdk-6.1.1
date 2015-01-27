package my.ep.web.integration.gfmas;

public class OrgProfile {

	private String year;
	private String code;
	private String ptjCode;
	private String description;
	private String ctrOfficerCode;
	private String agOfficeCode;
	/**
	 * @return the ptjCode
	 */
	public String getPtjCode() {
		return ptjCode;
	}

	/**
	 * @param ptjCode the ptjCode to set
	 */
	public void setPtjCode(String ptjCode) {
		this.ptjCode = ptjCode;
	}

	/**
	 * @return the ctrOfficerCode
	 */
	public String getCtrOfficerCode() {
		return ctrOfficerCode;
	}

	/**
	 * @param ctrOfficerCode the ctrOfficerCode to set
	 */
	public void setCtrOfficerCode(String ctrOfficerCode) {
		this.ctrOfficerCode = ctrOfficerCode;
	}

	/**
	 * @return the agOfficeCode
	 */
	public String getAgOfficeCode() {
		return agOfficeCode;
	}

	/**
	 * @param agOfficeCode the agOfficeCode to set
	 */
	public void setAgOfficeCode(String agOfficeCode) {
		this.agOfficeCode = agOfficeCode;
	}

	private String status;

	

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

}
