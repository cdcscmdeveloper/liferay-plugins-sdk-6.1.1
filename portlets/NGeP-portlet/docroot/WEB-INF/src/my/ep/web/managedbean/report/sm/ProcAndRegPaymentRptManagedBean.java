package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "procAndRegPaymentRptMB")
@SessionScoped
public class ProcAndRegPaymentRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	private String paymentType;

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

}
