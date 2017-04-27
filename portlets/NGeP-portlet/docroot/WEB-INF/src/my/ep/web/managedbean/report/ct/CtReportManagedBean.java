/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.managedbean.report.ct;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.managedbean.report.ReportBaseManagedBean;

/**
 * The Class CtReportManagedBean.
 * 
 * @author Nancy Ang
 */
public abstract class CtReportManagedBean extends ReportBaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private PmOrgProfile selectedMinistry;
	private PmOrgProfile selectedPegawaiPengawal;
	private PmOrgProfile selectedKumpulanPTJ;
	private PmOrgProfile selectedPTJ;
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
}
