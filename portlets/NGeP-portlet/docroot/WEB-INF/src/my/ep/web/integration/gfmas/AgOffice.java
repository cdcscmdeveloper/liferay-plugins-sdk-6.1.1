package my.ep.web.integration.gfmas;

import java.util.Date;

public class AgOffice {

	private String year;
	private String code;
	private String description;
	private String status;
	private Date effDate;

	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param string
	 *            the year to set
	 */
	public void setYear(String string) {
		this.year = string;
	}

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
	 * @return the effDate
	 */
	public Date getEffDate() {
		return effDate;
	}

	/**
	 * @param effDate the effDate to set
	 */
	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}

}
