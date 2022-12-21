package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean (name="applRequireSiteVisitRptMB")
@SessionScoped
public class ApplRequireSiteVisitRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;

	private String siteVisitStatus;

	public String getSiteVisitStatus() {
		return siteVisitStatus;
	}

	public void setSiteVisitStatus(String siteVisitStatus) {
		this.siteVisitStatus = siteVisitStatus;
	}
}
