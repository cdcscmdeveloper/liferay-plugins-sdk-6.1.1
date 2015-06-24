package my.ep.web.integration.gfmas;

import java.util.Date;

/*
 * @author P Ganga prasad
 */
public class Project {

	private String year;
	private String code;
	private String votCode;
	private String setiaCode;
	private String subSetiaCode;
	private String description;
	private String status;
	private String pembiayaan;

	// version 2.7
	private Date effectiveDate;
	private Date expiryDate;

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVotCode() {
		return votCode;
	}

	public void setVotCode(String votCode) {
		this.votCode = votCode;
	}

	public String getSetiaCode() {
		return setiaCode;
	}

	public void setSetiaCode(String setiaCode) {
		this.setiaCode = setiaCode;
	}

	public String getSubSetiaCode() {
		return subSetiaCode;
	}

	public void setSubSetiaCode(String subSetiaCode) {
		this.subSetiaCode = subSetiaCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPembiayaan() {
		return pembiayaan;
	}

	public void setPembiayaan(String pembiayaan) {
		this.pembiayaan = pembiayaan;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

}
