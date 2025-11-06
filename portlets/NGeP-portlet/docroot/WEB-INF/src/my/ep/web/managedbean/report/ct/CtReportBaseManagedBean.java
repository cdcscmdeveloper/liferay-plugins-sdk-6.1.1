/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.managedbean.report.ct;

import java.util.ArrayList; //#29416
import java.util.Date;
import java.util.List; //#29416

import my.ep.persistence.entity.ct.dtl.item.CtContractItem; //#29416
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.managedbean.report.ReportBaseManagedBean;

/**
 * The Class CtReportBaseManagedBean.
 * 
 * @author Nancy Ang
 */
public abstract class CtReportBaseManagedBean extends ReportBaseManagedBean {

	private static final long serialVersionUID = 1L;	

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
	private Date fromDate;
	private Date toDate;
	private PmOrgProfile selectedSupplier;
	//#30918 -start
	private Long fulfilmentType;
	private boolean supplierVisible;
	//#30918 - end
	// Start - #29416
	private CtContractItem selectedItem;
	private List<CtContractItem> lovSelectedItemList;
	private String formattedSelectedItems; // Store the formatted string
	private boolean isLovItemReportNameTriggered = false;
	private String contractItemId;
	// End - #29416
	
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
	}/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}
	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	/**
	 * @return the selectedSupplier
	 */
	public PmOrgProfile getSelectedSupplier() {
		return selectedSupplier;
	}
	/**
	 * @param selectedSupplier the selectedSupplier to set
	 */
	public void setSelectedSupplier(PmOrgProfile selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}
	//#30918 -start
	/**
	 * @return the fulfilmentType
	 */
	public Long getFulfilmentType() {
		return fulfilmentType;
	}
	/**
	 * @param fulfilmentType the fulfilmentType to set
	 */
	public void setFulfilmentType(Long fulfilmentType) {
		this.fulfilmentType = fulfilmentType;
	}
	/**
	 * @return the isSupplierVisible
	 */
	public boolean isSupplierVisible() {
		return supplierVisible;
	}
	/**
	 * @param isSupplierVisible the isSupplierVisible to set
	 */
	public void setSupplierVisible(boolean isSupplierVisible) {
		this.supplierVisible = isSupplierVisible;
	}
	//#30918 -end
	// Start - #29416
	public CtContractItem getSelectedItem() {
		if (selectedItem == null) {
			selectedItem = new CtContractItem();
		}
		return selectedItem;
	}

	public void setSelectedItem(CtContractItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public List<CtContractItem> getLovSelectedItemList() {
		if (lovSelectedItemList == null) {
			this.lovSelectedItemList = new ArrayList<>();
		}
		return lovSelectedItemList;
	}

	public void setLovSelectedItemList(List<CtContractItem> lovSelectedItemList) {
		this.lovSelectedItemList = lovSelectedItemList;
		updateFormattedSelectedItems();
	}

	public String getFormattedSelectedItems() {
		if (lovSelectedItemList == null || lovSelectedItemList.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (CtContractItem item : lovSelectedItemList) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(item.getScRequestItem().getItemName());
		}

		return sb.toString();
	}

	public void setFormattedSelectedItems(String formattedSelectedItems) {
		this.formattedSelectedItems = formattedSelectedItems;
	}

	public void updateFormattedSelectedItems() {
		System.out.println("updateFormattedSelectedItems ::: ");
		setFormattedSelectedItems(getFormattedSelectedItems());
	}

	public boolean isLovItemReportNameTriggered() {
		return isLovItemReportNameTriggered;
	}

	public void setLovItemReportNameTriggered(boolean isLovItemReportNameTriggered) {
		this.isLovItemReportNameTriggered = isLovItemReportNameTriggered;
	}

	public String getContractItemId() {
		return contractItemId;
	}

	public void setContractItemId(String contractItemId) {
		this.contractItemId = contractItemId;
	}
	// End - #29416
}
