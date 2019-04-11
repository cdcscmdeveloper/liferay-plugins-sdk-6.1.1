package my.ep.web.managedbean.report.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean (name="reqDelApplWithStatMB")
@SessionScoped
public class ReqDelApplWithStatRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;

	public List<SelectItem> processingOfficerLs;
	public String selectedPO;


	public String getSelectedPO() {
		return selectedPO;
	}

	public void setSelectedPO(String selectedPO) {
		this.selectedPO = selectedPO;
	}

	public List<SelectItem> getProcessingOfficerLs() {
		return processingOfficerLs;
	}

	public void setProcessingOfficerLs(List<SelectItem> processingOfficerLs) {
		this.processingOfficerLs = processingOfficerLs;
	}
}
