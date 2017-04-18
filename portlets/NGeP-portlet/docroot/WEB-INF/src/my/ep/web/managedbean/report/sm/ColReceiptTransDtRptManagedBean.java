package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "colReceiptTransDtRptMB")
@SessionScoped
public class ColReceiptTransDtRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;

	private String rptType;

	public String getRptType() {
		return rptType;
	}

	public void setRptType(String rptType) {
		this.rptType = rptType;
	}

}
