package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "suppPerformRptMB")
@SessionScoped
public class SuppPerformRptManagedBean extends SmReportManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7910328254638800865L;
	private String regDateLabel;

	public String getRegDateLabel() {
		return regDateLabel;
	}

	public void setRegDateLabel(String regDateLabel) {
		this.regDateLabel = regDateLabel;
	}

}
