package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;

@ManagedBean(name="profileOfRegCompanyRptMB")
@SessionScoped
public class ProfileOfRegCompanyRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;

	private SmSupplier smSupplier;
	
	private SmMofAccount smMofAccount; 

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}
	//#30824 START
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}
	//#30824 END
}
