package my.ep.web.managedbean.sm.supplieruseractivation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "supplierUserActivationManagedBean")
@SessionScoped
public class SupplierUserActivationManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4916539661013709750L;
	private boolean validActivation;
	private long supplierId;
	private long personnelId;
	private String encrpKeyStr;
	private SmPersonnel smPersonnel;
	
	public boolean isValidActivation() {
		return validActivation;
	}

	public void setValidActivation(boolean validActivation) {
		this.validActivation = validActivation;
	}

	public long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(long supplierId) {
		this.supplierId = supplierId;
	}

	public long getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(long personnelId) {
		this.personnelId = personnelId;
	}

	public String getEncrpKeyStr() {
		return encrpKeyStr;
	}

	public void setEncrpKeyStr(String encrpKeyStr) {
		this.encrpKeyStr = encrpKeyStr;
	}

	public SmPersonnel getSmPersonnel() {
		return smPersonnel;
	}

	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	
}
