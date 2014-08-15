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
	
	private PmZone zones;
	
	private PmCertSignatory pmCertSignatory;
	
	private List<PmUser> signatoryUser;
	
	private int status;
	

	/**
	 * @return the zones
	 */	
	public PmZone getZones() {
		return zones;
	}

	/**
	 * @param zones the zones to set
	 */
	public void setZones(PmZone zones) {
		this.zones = zones;
	}

	/**
	 * @return the signatoryUser
	 */	
	public List<PmUser> getSignatoryUser() {
		return signatoryUser;
	}

	/**
	 * @param signatoryUser the signatoryUser to set
	 */
	public void setSignatoryUser(List<PmUser> signatoryUser) {
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
		
}
