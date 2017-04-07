package my.ep.web.managedbean.report.sm;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile_;
import my.ep.web.managedbean.report.ReportBaseManagedBean;

@ManagedBean(name = "smReportMB")
@SessionScoped
public class SmReportManagedBean extends ReportBaseManagedBean {
	private static final long serialVersionUID = 1L;

	private Date dateFrom;
	private Date dateTo;
	private String zone;
	private PmOrgProfile selectedOrgProfile;
	private String mofNo;
	private String supplierType;
	private String regType;

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

	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	public String getMofNo() {
		return mofNo;
	}

	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

	public String getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}

	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}
}
