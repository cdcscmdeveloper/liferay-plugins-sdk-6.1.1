package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean (name="deleteMOFAccRptMB")
@SessionScoped
public class DeleteMOFAccRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;
	private String cancelBy;

	public String getCancelBy() {
		return cancelBy;
	}

	public void setCancelBy(String cancelBy) {
		this.cancelBy = cancelBy;
	}
	
	
}
