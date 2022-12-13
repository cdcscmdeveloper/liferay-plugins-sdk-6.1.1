package my.ep.web.managedbean.report.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name="colPaymentRptMB")
@SessionScoped
public class ColPaymentRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> paymentTypeMethodLs;
	private String paymentMethod;

	public List<SelectItem> getPaymentTypeMethodLs() {
		return paymentTypeMethodLs;
	}

	public void setPaymentTypeMethodLs(List<SelectItem> paymentTypeMethodLs) {
		this.paymentTypeMethodLs = paymentTypeMethodLs;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}
