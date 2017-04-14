package my.ep.web.managedbean.report.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="revenueCollectionRptMB")
@SessionScoped
public class RevenueCollectionRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	private List<String> selectedPaymentTypes;

	public List<String> getSelectedPaymentTypes() {
		return selectedPaymentTypes;
	}

	public void setSelectedPaymentTypes(List<String> selectedPaymentTypes) {
		this.selectedPaymentTypes = selectedPaymentTypes;
	}


}
