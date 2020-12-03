package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="applRevisedRptMB")
@SessionScoped
public class ApplRevisedRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	private boolean flag;

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}
