package my.ep.web.bean.ct;

import java.io.Serializable;
import java.util.Date;

public class FactoringCompany implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String factoringName;
	private String authRefNo;
	private Date effDate;
	private Date expDate;
	public String getFactoringName() {
		return factoringName;
	}
	public void setFactoringName(String factoringName) {
		this.factoringName = factoringName;
	}
	public String getAuthRefNo() {
		return authRefNo;
	}
	public void setAuthRefNo(String authRefNo) {
		this.authRefNo = authRefNo;
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
	public Date getAppDate() {
		return appDate;
	}
	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}
	private Date appDate;

}
