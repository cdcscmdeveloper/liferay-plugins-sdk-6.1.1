package my.ep.web.managedbean.pm.accessappl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmAccessAppl;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "accessApplActivationManagedBean")
@SessionScoped
public class AccessApplActivationManagedBean extends BaseManagedBean {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PmAccessAppl accessAppl;
	private String encryptActivationKey;
	private String createdTimeParam;
	
	
	public AccessApplActivationManagedBean(){}

	/**
	 * @return the accessAppl
	 */
	public PmAccessAppl getAccessAppl() {
		return accessAppl;
	}

	/**
	 * @param accessAppl the accessAppl to set
	 */
	public void setAccessAppl(PmAccessAppl accessAppl) {
		this.accessAppl = accessAppl;
	}

	/**
	 * @return the encryptActivationKey
	 */
	public String getEncryptActivationKey() {
		return encryptActivationKey;
	}

	/**
	 * @param encryptActivationKey the encryptActivationKey to set
	 */
	public void setEncryptActivationKey(String encryptActivationKey) {
		this.encryptActivationKey = encryptActivationKey;
	}

	/**
	 * @return the createdTimeParam
	 */
	public String getCreatedTimeParam() {
		return createdTimeParam;
	}

	/**
	 * @param createdTimeParam the createdTimeParam to set
	 */
	public void setCreatedTimeParam(String createdTimeParam) {
		this.createdTimeParam = createdTimeParam;
	}


}
