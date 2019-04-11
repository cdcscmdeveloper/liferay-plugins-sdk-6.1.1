package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="dailyReceiptRptMB")
@SessionScoped
public class DailyReceiptRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;

	private String receiptNo;

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	
	
}
