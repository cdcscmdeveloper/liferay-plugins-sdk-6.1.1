package my.ep.web.managedbean.report.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmStatusDesc;

@ManagedBean(name = "discActionExecSummaryRptMB")
@SessionScoped
public class DiscActionExecSummaryRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;

	private List<PmStatusDesc> discActionStatusLs;
	private Long discActionStatus;

	public List<PmStatusDesc> getDiscActionStatusLs() {
		return discActionStatusLs;
	}

	public void setDiscActionStatusLs(List<PmStatusDesc> discActionStatusLs) {
		this.discActionStatusLs = discActionStatusLs;
	}

	public Long getDiscActionStatus() {
		return discActionStatus;
	}

	public void setDiscActionStatus(Long discActionStatus) {
		this.discActionStatus = discActionStatus;
	}

	
}
