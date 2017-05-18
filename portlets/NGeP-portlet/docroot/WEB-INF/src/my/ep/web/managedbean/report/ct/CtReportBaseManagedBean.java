/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.managedbean.report.ct;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.managedbean.report.ReportBaseManagedBean;

/**
 * The Class CtReportBaseManagedBean.
 * 
 * @author Nancy Ang
 */
public abstract class CtReportBaseManagedBean extends ReportBaseManagedBean {

	private static final long serialVersionUID = 1L;	

	private PmOrgProfile currentActiveOrg;
	private boolean disableMinistryField;
	private boolean disableOrgField;
	private PmOrgProfile selectedMinistry;
	private PmOrgProfile selectedKumpulanPTJ;
	private PmOrgProfile selectedPTJ;
	private String contractNo;
	private String contractPhysicalNo;
	private String loaNo;
	private String loaPhysicalNo;
	private String agreement;
	private Long procurementMode;
	private String status;
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
	 * @return the disableMinistryField
	 */
	public boolean isDisableMinistryField() {
		return disableMinistryField;
	}
	/**
	 * @param disableMinistryField the disableMinistryField to set
	 */
	public void setDisableMinistryField(boolean disableMinistryField) {
		this.disableMinistryField = disableMinistryField;
	}
	/**
	 * @return the disableOrgField
	 */
	public boolean isDisableOrgField() {
		return disableOrgField;
	}
	/**
	 * @param disableOrgField the disableOrgField to set
	 */
	public void setDisableOrgField(boolean disableOrgField) {
		this.disableOrgField = disableOrgField;
	}
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
	 * @return the contractNo
	 */
	public String getContractNo() {
		return contractNo;
	}
	/**
	 * @param contractNo the contractNo to set
	 */
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	/**
	 * @return the contractPhysicalNo
	 */
	public String getContractPhysicalNo() {
		return contractPhysicalNo;
	}
	/**
	 * @param contractPhysicalNo the contractPhysicalNo to set
	 */
	public void setContractPhysicalNo(String contractPhysicalNo) {
		this.contractPhysicalNo = contractPhysicalNo;
	}
	/**
	 * @return the loaNo
	 */
	public String getLoaNo() {
		return loaNo;
	}
	/**
	 * @param loaNo the loaNo to set
	 */
	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}
	/**
	 * @return the loaPhysicalNo
	 */
	public String getLoaPhysicalNo() {
		return loaPhysicalNo;
	}
	/**
	 * @param loaPhysicalNo the loaPhysicalNo to set
	 */
	public void setLoaPhysicalNo(String loaPhysicalNo) {
		this.loaPhysicalNo = loaPhysicalNo;
	}
	/**
	 * @return the agreement
	 */
	public String getAgreement() {
		return agreement;
	}
	/**
	 * @param agreement the agreement to set
	 */
	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}
	/**
	 * @return the procurementMode
	 */
	public Long getProcurementMode() {
		return procurementMode;
	}
	/**
	 * @param procurementMode the procurementMode to set
	 */
	public void setProcurementMode(Long procurementMode) {
		this.procurementMode = procurementMode;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}
