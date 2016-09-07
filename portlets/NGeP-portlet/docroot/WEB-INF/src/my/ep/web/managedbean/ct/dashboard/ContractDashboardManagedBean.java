package my.ep.web.managedbean.ct.dashboard;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="contractDashboardManagedBean")
@SessionScoped
public class ContractDashboardManagedBean extends BaseManagedBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int lessThan30Days;
	private int lessThan120Days;
	private PmOrgProfile searchMinistryProfile;
	private PmOrgProfile searchPtjGroupProfile;
	private PmOrgProfile searchPtjProfile;

	public int getLessThan30Days() {
		return lessThan30Days;
	}

	public void setLessThan30Days(int lessThan30Days) {
		this.lessThan30Days = lessThan30Days;
	}

	public int getLessThan120Days() {
		return lessThan120Days;
	}

	public void setLessThan120Days(int lessThan120Days) {
		this.lessThan120Days = lessThan120Days;
	}

	public PmOrgProfile getSearchMinistryProfile() {
		return searchMinistryProfile;
	}

	public void setSearchMinistryProfile(PmOrgProfile searchMinistryProfile) {
		this.searchMinistryProfile = searchMinistryProfile;
	}

	public PmOrgProfile getSearchPtjGroupProfile() {
		return searchPtjGroupProfile;
	}

	public void setSearchPtjGroupProfile(PmOrgProfile searchPtjGroupProfile) {
		this.searchPtjGroupProfile = searchPtjGroupProfile;
	}

	public PmOrgProfile getSearchPtjProfile() {
		return searchPtjProfile;
	}

	public void setSearchPtjProfile(PmOrgProfile searchPtjProfile) {
		this.searchPtjProfile = searchPtjProfile;
	}
	
	
	
}
