package my.ep.web.integration.phis;

import java.util.Date;

/*
 * @author P Ganga prasad
 */

public class PhisProject {

	private int year;
	private String code;
	private String votCode;
	private String setiaCode;
	private String subSetiaCode;
	private String description;
	private String caraPembiayaan; // Added newly
	private String status;

	// version 1.28
	private Date effDate;
	private Date expDate;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
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

	public String getCaraPembiayaan() {
		return caraPembiayaan;
	}

	public void setCaraPembiayaan(String caraPembiayaan) {
		this.caraPembiayaan = caraPembiayaan;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEffDate() {
		return effDate;
	}

	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}

	public Date getExpDate() {
		return expDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}

}
