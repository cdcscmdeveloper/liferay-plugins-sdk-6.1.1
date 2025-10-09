package my.ep.web.managedbean.report.dp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

/**
 * @author zaim
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "itemExceedThresholdRptManagedBean")
@SessionScoped
public class ItemExceedThresholdRptManagedBean extends DpReportManagedBean {

	private boolean disableMinistryField;
	private boolean disableOrgField;
	private PmOrgProfile selectedMinistry;
	private PmOrgProfile selectedKumpulanPTJ;
	private PmOrgProfile selectedPTJ;
	private String centralPanelContract;
	private String circularNo;
	private String itemCode;
	private Long procurementType;
	private String itemName;
	private String rnNo;
	private String financialYear;
	private boolean centralPanelContractSelected;
	private boolean disableReason;
	private boolean disablePTJ;
	private boolean disableRole;
	private String reason;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 // intentionally left blank
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
	 * @return the centralPanelContract
	 */
	public String getCentralPanelContract() {
		return centralPanelContract;
	}
	/**
	 * @param centralPanelContract the centralPanelContract to set
	 */
	public void setCentralPanelContract(String centralPanelContract) {
		this.centralPanelContract = centralPanelContract;
	}
	/**
	 * @return the circularNo
	 */
	public String getCircularNo() {
		return circularNo;
	}
	/**
	 * @param circularNo the circularNo to set
	 */
	public void setCircularNo(String circularNo) {
		this.circularNo = circularNo;
	}
	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}
	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	/**
	 * @return the procurementType
	 */
	public Long getProcurementType() {
		return procurementType;
	}
	/**
	 * @param procurementType the procurementType to set
	 */
	public void setProcurementType(Long procurementType) {
		this.procurementType = procurementType;
	}
	/**
	 * @return the rnNo
	 */
	public String getRnNo() {
		return rnNo;
	}
	/**
	 * @param rnNo the rnNo to set
	 */
	public void setRnNo(String rnNo) {
		this.rnNo = rnNo;
	}
	/**
	 * @return the financialYear
	 */
	public String getFinancialYear() {
		return financialYear;
	}
	/**
	 * @param financialYear the financialYear to set
	 */
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}
	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	/**
	 * @return the centralPanelContractSelected
	 */
	public boolean isCentralPanelContractSelected() {
		return centralPanelContractSelected;
	}
	/**
	 * @param centralPanelContractSelected the centralPanelContractSelected to set
	 */
	public void setCentralPanelContractSelected(boolean centralPanelContractSelected) {
		this.centralPanelContractSelected = centralPanelContractSelected;
	}
	/**
	 * @return the disableReason
	 */
	public boolean isDisableReason() {
		return disableReason;
	}
	/**
	 * @param disableReason the disableReason to set
	 */
	public void setDisableReason(boolean disableReason) {
		this.disableReason = disableReason;
	}
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	/**
	 * @return the disablePTJ
	 */
	public boolean isDisablePTJ() {
		return disablePTJ;
	}
	/**
	 * @param disablePTJ the disablePTJ to set
	 */
	public void setDisablePTJ(boolean disablePTJ) {
		this.disablePTJ = disablePTJ;
	}
	/**
	 * @return the disableRole
	 */
	public boolean isDisableRole() {
		return disableRole;
	}
	/**
	 * @param disableRole the disableRole to set
	 */
	public void setDisableRole(boolean disableRole) {
		this.disableRole = disableRole;
	}


}
