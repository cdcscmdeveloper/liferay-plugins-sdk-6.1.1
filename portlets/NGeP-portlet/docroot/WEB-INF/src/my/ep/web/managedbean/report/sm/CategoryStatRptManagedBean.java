package my.ep.web.managedbean.report.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name="categoryStatRptMB")
@SessionScoped
public class CategoryStatRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	//#7754_#35984 - Start
	private String stateByZone;
	private List<SelectItem> stateLstByZone;
	//#7754_#35984 - End
	
	public String getStateByZone() {
		return stateByZone;
	}
	public void setStateByZone(String stateByZone) {
		this.stateByZone = stateByZone;
	}
	public List<SelectItem> getStateLstByZone() {
		return stateLstByZone;
	}
	public void setStateLstByZone(List<SelectItem> stateLstByZone) {
		this.stateLstByZone = stateLstByZone;
	}

}
