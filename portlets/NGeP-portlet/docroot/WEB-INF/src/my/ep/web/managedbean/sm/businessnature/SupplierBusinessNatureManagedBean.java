package my.ep.web.managedbean.sm.businessnature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private SmSupplierBusiness[] selectedBusinessNatureLs;
	private SmSupplier smSupplier;
	private List<PmBusinessNature> bizNatures;
	private Map<Long, SmSupplierBusiness> supplierBusinessMap = new HashMap<Long, SmSupplierBusiness>();

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

	public Map<Long, SmSupplierBusiness> getSupplierBusinessMap() {
		return supplierBusinessMap;
	}

	public void setSupplierBusinessMap(Map<Long, SmSupplierBusiness> supplierBusinessMap) {
		this.supplierBusinessMap = supplierBusinessMap;
	}
}
