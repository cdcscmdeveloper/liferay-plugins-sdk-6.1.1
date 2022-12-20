package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "gstRegSupplierRptMB")
@SessionScoped
public class GstRegSupplierRptManagedBean extends SmReportManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4523272259830233188L;
	private String gstRegNo;
	private String gstRegStatus;

	public String getGstRegNo() {
		return gstRegNo;
	}

	public void setGstRegNo(String gstRegNo) {
		this.gstRegNo = gstRegNo;
	}

	public String getGstRegStatus() {
		return gstRegStatus;
	}

	public void setGstRegStatus(String gstRegStatus) {
		this.gstRegStatus = gstRegStatus;
	}

}
