package my.ep.web.managedbean.report.sm;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile_;
import my.ep.web.managedbean.report.ReportBaseManagedBean;

@ManagedBean(name = "smReportMB")
@SessionScoped
public class SmReportManagedBean extends ReportBaseManagedBean{
	private static final long serialVersionUID = 1L;

	private Date dateFrom;
	private Date dateTo;
	private String zone;
	private String companyName;
	private PmOrgProfile selectedOrgProfile;
	
	public Date getDateFrom() {
		return dateFrom;
	}
	
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	
	public Date getDateTo() {
		return dateTo;
	}
	
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}
}
