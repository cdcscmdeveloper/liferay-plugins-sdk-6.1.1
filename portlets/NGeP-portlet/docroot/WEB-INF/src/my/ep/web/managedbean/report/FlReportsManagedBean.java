package my.ep.web.managedbean.report;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

@ManagedBean(name = "flReportsManagedBean")
@SessionScoped
public class FlReportsManagedBean extends ReportBaseManagedBean{

	private static final long serialVersionUID = 1L;
	private PmOrgProfile currentActiveOrg;
	private PmOrgProfile selectedMinistry;
	private PmOrgProfile selectedPegawaiPengawal;
	private PmOrgProfile selectedKumpulanPTJ;
	private PmOrgProfile selectedPTJ;
	private boolean disableOrgFields;
	private String selectedYear;
	
	public FlReportsManagedBean(){}

	/**
	 * @return the selectedMinistry
	 */
	public PmOrgProfile getSelectedMinistry() {
		return selectedMinistry;
	}

	/**
	 * @param selectedMinistry the selectedMinistry to set
	 */
	public void setSelectedMinistry(PmOrgProfile selectedMinistry) {
		this.selectedMinistry = selectedMinistry;
	}

	/**
	 * @return the selectedPegawaiPengawal
	 */
	public PmOrgProfile getSelectedPegawaiPengawal() {
		return selectedPegawaiPengawal;
	}

	/**
	 * @param selectedPegawaiPengawal the selectedPegawaiPengawal to set
	 */
	public void setSelectedPegawaiPengawal(PmOrgProfile selectedPegawaiPengawal) {
		this.selectedPegawaiPengawal = selectedPegawaiPengawal;
	}

	/**
	 * @return the selectedKumpulanPTJ
	 */
	public PmOrgProfile getSelectedKumpulanPTJ() {
		return selectedKumpulanPTJ;
	}

	/**
	 * @param selectedKumpulanPTJ the selectedKumpulanPTJ to set
	 */
	public void setSelectedKumpulanPTJ(PmOrgProfile selectedKumpulanPTJ) {
		this.selectedKumpulanPTJ = selectedKumpulanPTJ;
	}

	/**
	 * @return the selectedPTJ
	 */
	public PmOrgProfile getSelectedPTJ() {
		return selectedPTJ;
	}

	/**
	 * @param selectedPTJ the selectedPTJ to set
	 */
	public void setSelectedPTJ(PmOrgProfile selectedPTJ) {
		this.selectedPTJ = selectedPTJ;
	}

	/**
	 * @return the currentActiveOrg
	 */
	public PmOrgProfile getCurrentActiveOrg() {
		return currentActiveOrg;
	}

	/**
	 * @param currentActiveOrg the currentActiveOrg to set
	 */
	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
	}

	/**
	 * @return the disableOrgFields
	 */
	public boolean isDisableOrgFields() {
		return disableOrgFields;
	}

	/**
	 * @param disableOrgFields the disableOrgFields to set
	 */
	public void setDisableOrgFields(boolean disableOrgFields) {
		this.disableOrgFields = disableOrgFields;
	}

	/**
	 * @return the selectedYear
	 */
	public String getSelectedYear() {
		return selectedYear;
	}

	/**
	 * @param selectedYear the selectedYear to set
	 */
	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
	}
}
