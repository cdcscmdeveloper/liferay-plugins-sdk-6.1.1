package my.ep.web.integration.phis;

import java.util.Date;

/*
 * @author P Ganga prasad
 */
public class PhisProgramActivity {

	private int year;
	private String code;
	private String votCode;
	private String description;
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
