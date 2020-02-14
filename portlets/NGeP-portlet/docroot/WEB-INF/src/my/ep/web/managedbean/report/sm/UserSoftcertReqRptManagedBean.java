package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean (name="usersSoftcertReqRptMB")
@SessionScoped
public class UserSoftcertReqRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	private String softcertDateType;
	private String userName;
	private String companyName;
	private boolean supplierUser;
	private String certApplicationType;
	private String dateRangeType;
	private String certProvider;
	private String certStatus;
	private boolean showCertProvider;
	private boolean showRevoke;

	public String getSoftcertDateType() {
		return softcertDateType;
	}

	public void setSoftcertDateType(String softcertDateType) {
		
		this.softcertDateType = softcertDateType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isSupplierUser() {
		return supplierUser;
	}

	public void setSupplierUser(boolean supplierUser) {
		this.supplierUser = supplierUser;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCertApplicationType() {
		return certApplicationType;
	}

	public void setCertApplicationType(String certApplicationType) {
		this.certApplicationType = certApplicationType;
	}

	public String getDateRangeType() {
		return dateRangeType;
	}

	public void setDateRangeType(String dateRangeType) {
		this.dateRangeType = dateRangeType;
	}

	public String getCertProvider() {
		return certProvider;
	}

	public void setCertProvider(String certProvider) {
		this.certProvider = certProvider;
	}

	public String getCertStatus() {
		return certStatus;
	}

	public void setCertStatus(String certStatus) {
		this.certStatus = certStatus;
	}

	public boolean isShowCertProvider() {
		return showCertProvider;
	}

	public void setShowCertProvider(boolean showCertProvider) {
		this.showCertProvider = showCertProvider;
	}

	public boolean isShowRevoke() {
		return showRevoke;
	}

	public void setShowRevoke(boolean showRevoke) {
		this.showRevoke = showRevoke;
	}
}
