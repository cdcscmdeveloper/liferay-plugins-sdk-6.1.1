package my.ep.web.managedbean.sm.businessnature;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="supplierBusinessNatureMB")
@SessionScoped
public class SupplierBusinessNatureManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;

	private List<SmSupplierBusiness> businessNatureLs;
	
	private SmSupplierBusiness[] selectedBusinessNatureLs;
	private SmSupplier smSupplier;
	private List<PmBusinessNature> bizNatures;

	public List<SmSupplierBusiness> getBusinessNatureLs() {
		return businessNatureLs;
	}

	public void setBusinessNatureLs(List<SmSupplierBusiness> businessNatureLs) {
		this.businessNatureLs = businessNatureLs;
	}

	public SmSupplierBusiness[] getSelectedBusinessNatureLs() {
		return selectedBusinessNatureLs;
	}

	public void setSelectedBusinessNatureLs(
			SmSupplierBusiness[] selectedBusinessNatureLs) {
		this.selectedBusinessNatureLs = selectedBusinessNatureLs;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public List<PmBusinessNature> getBizNatures() {
		return bizNatures;
	}

	public void setBizNatures(List<PmBusinessNature> bizNatures) {
		this.bizNatures = bizNatures;
	}
}
