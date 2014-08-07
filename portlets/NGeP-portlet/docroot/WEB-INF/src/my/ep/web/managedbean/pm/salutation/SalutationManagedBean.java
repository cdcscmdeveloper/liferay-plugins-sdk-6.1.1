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
	
	private Long status;	
	
	/**
	 * @return the salutationCodes
	 */
	public String getSalutationCodes() {
		return salutationCodes;
	}

	/**
	 * @param salutationCodes the salutationCodes to set
	 */
	public void setSalutationCodes(String salutationCodes) {
		this.salutationCodes = salutationCodes;
	}

	/**
	 * @return the salutationNames
	 */
	public String getSalutationNames() {
		return salutationNames;
	}

	/**
	 * @param salutationNames the salutationNames to set
	 */
	public void setSalutationNames(String salutationNames) {
		this.salutationNames = salutationNames;
	}

	/**
	 * @return the pmSalutation
	 */
	public PmSalutation getPmSalutation() {
		return pmSalutation;
	}

	/**
	 * @param pmSalutation the pmSalutation to set
	 */
	public void setPmSalutation(PmSalutation pmSalutation) {
		this.pmSalutation = pmSalutation;
	}

	/**
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}
	
		
			
}
