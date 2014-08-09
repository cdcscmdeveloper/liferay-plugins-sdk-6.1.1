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
	
	private List<PmUser> users;


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
	 * @return the users
	 */	
	public List<PmUser> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<PmUser> users) {
		this.users = users;
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
	
	
}
