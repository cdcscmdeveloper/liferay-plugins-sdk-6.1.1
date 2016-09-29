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

	/**
	 * @return the lessThan30Days
	 */
	public int getLessThan30Days() {
		return lessThan30Days;
	}

	/**
	 * @param lessThan30Days the lessThan30Days to set
	 */
	public void setLessThan30Days(int lessThan30Days) {
		this.lessThan30Days = lessThan30Days;
	}

	/**
	 * @return the lessThan120Days
	 */
	public int getLessThan120Days() {
		return lessThan120Days;
	}

	/**
	 * @param lessThan120Days the lessThan120Days to set
	 */
	public void setLessThan120Days(int lessThan120Days) {
		this.lessThan120Days = lessThan120Days;
	}

	/**
	 * @return the searchMinistryProfile
	 */
	public PmOrgProfile getSearchMinistryProfile() {
		return searchMinistryProfile;
	}

	/**
	 * @param searchMinistryProfile the searchMinistryProfile to set
	 */
	public void setSearchMinistryProfile(PmOrgProfile searchMinistryProfile) {
		this.searchMinistryProfile = searchMinistryProfile;
	}

	/**
	 * @return the searchPtjGroupProfile
	 */
	public PmOrgProfile getSearchPtjGroupProfile() {
		return searchPtjGroupProfile;
	}

	/**
	 * @param searchPtjGroupProfile the searchPtjGroupProfile to set
	 */
	public void setSearchPtjGroupProfile(PmOrgProfile searchPtjGroupProfile) {
		this.searchPtjGroupProfile = searchPtjGroupProfile;
	}

	/**
	 * @return the searchPtjProfile
	 */
	public PmOrgProfile getSearchPtjProfile() {
		return searchPtjProfile;
	}

	/**
	 * @param searchPtjProfile the searchPtjProfile to set
	 */
	public void setSearchPtjProfile(PmOrgProfile searchPtjProfile) {
		this.searchPtjProfile = searchPtjProfile;
	}
}
