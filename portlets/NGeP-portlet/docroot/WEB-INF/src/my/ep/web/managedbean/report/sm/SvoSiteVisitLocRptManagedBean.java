package my.ep.web.managedbean.report.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name = "svoSiteVisitLocRptMB")
@SessionScoped
public class SvoSiteVisitLocRptManagedBean extends SmReportManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8216956527603600956L;
	private List<SelectItem> svoUserList;
	private String svoUser;
	//12625 start
	private String svoApplicationStatus;
	private String svoPerformSiteVisit;
	//12625 end

	public List<SelectItem> getSvoUserList() {
		return svoUserList;
	}

	public void setSvoUserList(List<SelectItem> svoUserList) {
		this.svoUserList = svoUserList;
	}

	public String getSvoUser() {
		return svoUser;
	}

	public void setSvoUser(String svoUser) {
		this.svoUser = svoUser;
	}
	
	//12625 start
	public String getSvoApplicationStatus() {
		return svoApplicationStatus;
	}

	public void setSvoApplicationStatus(String svoApplicationStatus) {
		this.svoApplicationStatus = svoApplicationStatus;
	}

	public String getSvoPerformSiteVisit() {
		return svoPerformSiteVisit;
	}

	public void setSvoPerformSiteVisit(String svoPerformSiteVisit) {
		this.svoPerformSiteVisit = svoPerformSiteVisit;
	}
	//12625 end
	

}
