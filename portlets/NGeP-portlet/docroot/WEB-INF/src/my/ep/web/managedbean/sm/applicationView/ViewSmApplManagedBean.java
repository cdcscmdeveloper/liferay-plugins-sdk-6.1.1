package my.ep.web.managedbean.sm.applicationView;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "viewSmApplMB")
@SessionScoped
public class ViewSmApplManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private SmAppl smAppl;
	private SmSupplier smSupplier;
	private int tabActiveIndex;

	private SmSupplierCert selectedCert;
	private List<PmCertBody> certBodyLs;

	public ViewSmApplManagedBean() {
		System.out.println(" ********* END MANAGED BEAN CREATION ****** ");
	}

	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public SmSupplierCert getSelectedCert() {
		return selectedCert;
	}

	public void setSelectedCert(SmSupplierCert selectedCert) {
		this.selectedCert = selectedCert;
	}

	public List<PmCertBody> getCertBodyLs() {
		return certBodyLs;
	}

	public void setCertBodyLs(List<PmCertBody> certBodyLs) {
		this.certBodyLs = certBodyLs;
	}


}
