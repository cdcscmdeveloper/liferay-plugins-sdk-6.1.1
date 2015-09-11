package my.ep.web.integration.gfmas;

import java.util.Date;

/*
 * @author P Ganga prasad
*/

public class VotFund {

	private String year;
	private String votCode;
	private String ptjCode;
	private String description;
	private String status; // values will be X<V
	private String votFundType;

	//version 2.7
	private Date effectiveDate;
	private Date expiryDate;

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getVotCode() {
		return votCode;
	}

	public void setVotCode(String code) {
		this.votCode = code;
	}

	public String getPtjCode() {
		return ptjCode;
	}

	public void setPtjCode(String ptjCode) {
		this.ptjCode = ptjCode;
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

	public String getVotFundType() {
		return votFundType;
	}

	public void setVotFundType(String votFundType) {
		this.votFundType = votFundType;
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
