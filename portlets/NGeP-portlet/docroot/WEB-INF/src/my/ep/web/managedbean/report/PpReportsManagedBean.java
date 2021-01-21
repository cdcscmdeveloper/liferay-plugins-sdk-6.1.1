package my.ep.web.managedbean.report;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

@ManagedBean(name = "ppReportsManagedBean")
@SessionScoped
public class PpReportsManagedBean extends ReportBaseManagedBean {

	private static final long serialVersionUID = 1L;
	private PmOrgProfile currentActiveOrg;
	private PmOrgProfile selectedMinistry;
	private PmOrgProfile selectedPegawaiPengawal;
	private PmOrgProfile selectedKumpulanPTJ;
	private PmOrgProfile selectedPTJ;
	private boolean disabledMinistryLov = false;
	private boolean disabledPegawaiLov = false;
	private boolean disabledKumpulanPTJLov = false;
	private boolean disabledPTJLov = false;
	private String selectedYear;
	private Long procurementCatId;
	private Long procurementTypeId;
	private Integer status;
	private boolean ministryMandatory;	//[#27197]
	
	private String documentType; //#27472

	public PpReportsManagedBean() {
	}

	/**
	 * @return the selectedMinistry
	 */
	public PmOrgProfile getSelectedMinistry() {
		return selectedMinistry;
	}

	/**
	 * @param selectedMinistry
	 *            the selectedMinistry to set
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
	 * @param selectedPegawaiPengawal
	 *            the selectedPegawaiPengawal to set
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
	 * @param selectedKumpulanPTJ
	 *            the selectedKumpulanPTJ to set
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
	 * @param selectedPTJ
	 *            the selectedPTJ to set
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
	 * @param currentActiveOrg
	 *            the currentActiveOrg to set
	 */
	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
	}

	/**
	 * @return the selectedYear
	 */
	public String getSelectedYear() {
		return selectedYear;
	}

	/**
	 * @param selectedYear
	 *            the selectedYear to set
	 */
	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
	}

	/**
	 * @return the procurementCatId
	 */
	public Long getProcurementCatId() {
		return procurementCatId;
	}

	/**
	 * @param procurementCatId
	 *            the procurementCatId to set
	 */
	public void setProcurementCatId(Long procurementCatId) {
		this.procurementCatId = procurementCatId;
	}

	/**
	 * @return the procurementTypeId
	 */
	public Long getProcurementTypeId() {
		return procurementTypeId;
	}

	/**
	 * @param procurementTypeId
	 *            the procurementTypeId to set
	 */
	public void setProcurementTypeId(Long procurementTypeId) {
		this.procurementTypeId = procurementTypeId;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	public boolean isDisabledMinistryLov() {
		return disabledMinistryLov;
	}

	public void setDisabledMinistryLov(boolean disabledMinistryLov) {
		this.disabledMinistryLov = disabledMinistryLov;
	}

	public boolean isDisabledPegawaiLov() {
		return disabledPegawaiLov;
	}

	public void setDisabledPegawaiLov(boolean disabledPegawaiLov) {
		this.disabledPegawaiLov = disabledPegawaiLov;
	}

	public boolean isDisabledKumpulanPTJLov() {
		return disabledKumpulanPTJLov;
	}

	public void setDisabledKumpulanPTJLov(boolean disabledKumpulanPTJLov) {
		this.disabledKumpulanPTJLov = disabledKumpulanPTJLov;
	}

	public boolean isDisabledPTJLov() {
		return disabledPTJLov;
	}

	public void setDisabledPTJLov(boolean disabledPTJLov) {
		this.disabledPTJLov = disabledPTJLov;
	}

	public boolean isMinistryMandatory() {
		return ministryMandatory;
	}

	public void setMinistryMandatory(boolean ministryMandatory) {
		this.ministryMandatory = ministryMandatory;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	//START #27472
	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	//END #27472
	
	
}
