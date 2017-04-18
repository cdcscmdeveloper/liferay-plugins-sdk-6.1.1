package my.ep.web.managedbean.report.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name="categoryStatYearRptMB")
@SessionScoped
public class CategoryStatRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;

	private Long regStatus;
	private List<SelectItem> regStatusLs;
	
	public Long getRegStatus() {
		return regStatus;
	}
	
	public void setRegStatus(Long regStatus) {
		this.regStatus = regStatus;
	}
	
	public List<SelectItem> getRegStatusLs() {
		return regStatusLs;
	}
	
	public void setRegStatusLs(List<SelectItem> regStatusLs) {
		this.regStatusLs = regStatusLs;
	}
	
}
