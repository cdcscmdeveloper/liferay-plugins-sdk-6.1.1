package my.ep.web.managedbean.pm.salutation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "salutationManagedBean")
@SessionScoped
public class SalutationManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PmSalutation pmSalutation;
	
	private String salutationCodes;
	
	private String salutationNames;

	public String getSalutationCodes() {
		return salutationCodes;
	}

	public void setSalutationCodes(String salutationCodes) {
		this.salutationCodes = salutationCodes;
	}

	public String getSalutationNames() {
		return salutationNames;
	}

	public void setSalutationNames(String salutationNames) {
		this.salutationNames = salutationNames;
	}

	public PmSalutation getPmSalutation() {
		return pmSalutation;
	}

	public void setPmSalutation(PmSalutation pmSalutation) {
		this.pmSalutation = pmSalutation;
	}
		
			
}
