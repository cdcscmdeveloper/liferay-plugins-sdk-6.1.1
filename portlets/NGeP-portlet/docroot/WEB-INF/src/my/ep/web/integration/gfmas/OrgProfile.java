package my.ep.web.integration.gfmas;

import java.util.Date;

/*
 * @author P Ganga prasad
 */
public class OrgProfile {

	private String year;
	private String code;
	private String ptjCode;
	private String description;
	private String ctrOfficerCode;
	private String agOfficeCode;
	private String status;

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

	public String getCtrOfficerCode() {
		return ctrOfficerCode;
	}

	public void setCtrOfficerCode(String ctrOfficerCode) {
		this.ctrOfficerCode = ctrOfficerCode;
	}

	public String getAgOfficeCode() {
		return agOfficeCode;
	}

	public void setAgOfficeCode(String agOfficeCode) {
		this.agOfficeCode = agOfficeCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
