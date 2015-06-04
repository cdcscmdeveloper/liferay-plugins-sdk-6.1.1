package my.ep.web.managedbean.pm.signatory;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCertSignatory;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author DK
 * 
 */
@ManagedBean
@SessionScoped
public class AuthorizedSignatoryManagedBean extends BaseManagedBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PmZone zone;
	
	private PmCertSignatory pmCertSignatory;
	
	private List<PmUser> signatoryUsers;
	private PmUser signatoryUser;
	private List<PmCertSignatory> certSignatories;
	private List<PmCertSignatory> deletedCertSignatories;
	private int status;
	private boolean validationError = false;
	

	/**
	 * @return the deletedCertSignatories
	 */
	public List<PmCertSignatory> getDeletedCertSignatories() {
		return deletedCertSignatories;
	}

	/**
	 * @param deletedCertSignatories the deletedCertSignatories to set
	 */
	public void setDeletedCertSignatories(
			List<PmCertSignatory> deletedCertSignatories) {
		this.deletedCertSignatories = deletedCertSignatories;
	}

	/**
	 * @return the zone
	 */	
	public PmZone getZone() {
		return zone;
	}

	/**
	 * @param zone the zone to set
	 */
	public void setZone(PmZone zone) {
		this.zone = zone;
	}



	/**
	 * @return the signatoryUsers
	 */
	public List<PmUser> getSignatoryUsers() {
		return signatoryUsers;
	}

	/**
	 * @param signatoryUsers the signatoryUsers to set
	 */
	public void setSignatoryUsers(List<PmUser> signatoryUsers) {
		this.signatoryUsers = signatoryUsers;
	}

	/**
	 * @return the signatoryUser
	 */
	public PmUser getSignatoryUser() {
		return signatoryUser;
	}

	/**
	 * @param signatoryUser the signatoryUser to set
	 */
	public void setSignatoryUser(PmUser signatoryUser) {
		this.signatoryUser = signatoryUser;
	}

	/**
	 * @return the pmCertSignatory
	 */	
	public PmCertSignatory getPmCertSignatory() {
		return pmCertSignatory;
	}

	/**
	 * @param pmCertSignatory the pmCertSignatory to set
	 */
	public void setPmCertSignatory(PmCertSignatory pmCertSignatory) {
		this.pmCertSignatory = pmCertSignatory;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the certSignatories
	 */
	public List<PmCertSignatory> getCertSignatories() {
		return certSignatories;
	}

	/**
	 * @param certSignatories the certSignatories to set
	 */
	public void setCertSignatories(List<PmCertSignatory> certSignatories) {
		this.certSignatories = certSignatories;
	}

	/**
	 * @return the validationError
	 */
	public boolean isValidationError() {
		return validationError;
	}

	/**
	 * @param validationError the validationError to set
	 */
	public void setValidationError(boolean validationError) {
		this.validationError = validationError;
	}	
		
}
