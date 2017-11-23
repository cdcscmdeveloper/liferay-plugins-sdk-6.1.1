package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "discActionExecSummaryRptMB")
@SessionScoped
public class DiscActionExecSummaryRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;

	private Long discActionStatus;


	public Long getDiscActionStatus() {
		return discActionStatus;
	}

	public void setDiscActionStatus(Long discActionStatus) {
		this.discActionStatus = discActionStatus;
	}

	
}
