package my.ep.web.managedbean.report.sm;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUser;

@ManagedBean(name = "smRevisitRptMB")
@SessionScoped
public class SmRevisitRptManagedBean extends SmReportManagedBean {
	private static final long serialVersionUID = 1L;

	private String svoRoleCode;
	private PmUser revisitOfficer;
	private Date visitDateFrom;
	private Date visitDateTo;

	public String getSvoRoleCode() {
		return svoRoleCode;
	}

	public void setSvoRoleCode(String svoRoleCode) {
		this.svoRoleCode = svoRoleCode;
	}

	public PmUser getRevisitOfficer() {
		return revisitOfficer;
	}

	public void setRevisitOfficer(PmUser revisitOfficer) {
		this.revisitOfficer = revisitOfficer;
	}

	public Date getVisitDateFrom() {
		return visitDateFrom;
	}

	public void setVisitDateFrom(Date visitDateFrom) {
		this.visitDateFrom = visitDateFrom;
	}

	public Date getVisitDateTo() {
		return visitDateTo;
	}

	public void setVisitDateTo(Date visitDateTo) {
		this.visitDateTo = visitDateTo;
	}
}
