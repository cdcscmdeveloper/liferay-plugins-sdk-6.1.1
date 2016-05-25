package my.ep.web.managedbean.pm.signatory;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
	private Map<Long , Date> idToCertSignMapForChangedRecs = new HashMap<Long , Date>();
	private Date  tomorrow = tomorrowDate();
	private PmCertSignatory deleteSignatory;
	
	public AuthorizedSignatoryManagedBean(){}

	public Date getTomorrow() {
		return tomorrow;
	}
	public void setTomorrow(Date tomorrow) {
		this.tomorrow = tomorrow;
	}
	public Date tomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, 1);
		
        return c1.getTime();
    } 
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

	public Map<Long, Date> getIdToCertSignMapForChangedRecs() {
		return idToCertSignMapForChangedRecs;
	}

	public void setIdToCertSignMapForChangedRecs(
			Map<Long, Date> idToCertSignMapForChangedRecs) {
		this.idToCertSignMapForChangedRecs = idToCertSignMapForChangedRecs;
	}
	/**
	 * @return the deleteSignatory
	 */
	public PmCertSignatory getDeleteSignatory() {
		return deleteSignatory;
	}
	/**
	 * @param deleteSignatory the deleteSignatory to set
	 */
	public void setDeleteSignatory(PmCertSignatory deleteSignatory) {
		this.deleteSignatory = deleteSignatory;
	}

		
}
