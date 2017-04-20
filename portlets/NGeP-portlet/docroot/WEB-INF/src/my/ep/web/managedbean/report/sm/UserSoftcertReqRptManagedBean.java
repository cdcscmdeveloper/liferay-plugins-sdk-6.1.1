package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean (name="usersSoftcertReqRptMB")
@SessionScoped
public class UserSoftcertReqRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	private String softcertDateType;
	private String userName;

	public String getSoftcertDateType() {
		return softcertDateType;
	}

	public void setSoftcertDateType(String softcertDateType) {
		this.softcertDateType = softcertDateType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
