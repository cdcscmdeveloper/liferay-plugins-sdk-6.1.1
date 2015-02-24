package my.ep.web.integration.eperunding;

import java.util.Date;

public class DeletedCompany {

	private String mOFNo;
	private String reason;
	private Date dateDeleted;

	public String getmOFNo() {
		return mOFNo;
	}

	public void setmOFNo(String mOFNo) {
		this.mOFNo = mOFNo;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}

}
