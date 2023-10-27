package my.ep.web.managedbean.sm.businessnature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.dto.sm.common.ssm.SsmResDTO;
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
	//16052019 - SSM INTEGRATION CR 
	private SsmResDTO ssmResDTO;
	// 9442 - REOPEN - START
	private List<String> currentBizNatures;
	private List<SmSupplierBusiness> deletedSupplierBusiness;
	// 9442 - REOPEN - END

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

	public SsmResDTO getSsmResDTO() {
		return ssmResDTO;
	}

	public void setSsmResDTO(SsmResDTO ssmResDTO) {
		this.ssmResDTO = ssmResDTO;
	}
	
	// 9442 - REOPEN - START
	public List<String> getCurrentBizNatures() {
		return currentBizNatures;
	}

	public void setCurrentBizNatures(List<String> currentBizNatures) {
		this.currentBizNatures = currentBizNatures;
	}

	public List<SmSupplierBusiness> getDeletedSupplierBusiness() {
		return deletedSupplierBusiness;
	}

	public void setDeletedSupplierBusiness(List<SmSupplierBusiness> deletedSupplierBusiness) {
		this.deletedSupplierBusiness = deletedSupplierBusiness;
	}
	// 9442 - REOPEN - END
}



