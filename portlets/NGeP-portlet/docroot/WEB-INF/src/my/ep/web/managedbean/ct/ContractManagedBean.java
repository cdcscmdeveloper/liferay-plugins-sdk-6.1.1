/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract
 * 
 * @author kieyun
 * @since 13/11/2013	 
 */
@ManagedBean(name="ctManagedBean")
@SessionScoped
public class ContractManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private String fileDirectory;
	private int attachFileIndex;
	private boolean viewContract = false;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private CtContractVer ctContractVer;
	private CtContractVer agreementContractVer;
	private double performanceBondRuleAmt;
	private String supplierCompanyName;
	private boolean updateCompanyName;
	private String supplierBranchName;
	private String supplierAddress;
	private boolean updateSupplierAddress;
	private SmAddress smAddress;
	private boolean directPurchase;
	private boolean gstCheckingGUI;
	/**
	 * @return the fileDirectory
	 */
	public String getFileDirectory() {
		return fileDirectory;
	}
	
	/**
	 * @param fileDirectory the fileDirectory to set
	 */
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}	
	
	/**
	 * @return the attachFileIndex
	 */
	public int getAttachFileIndex() {
		return attachFileIndex;
	}
	
	/**
	 * @param attachFileIndex the attachFileIndex to set
	 */
	public void setAttachFileIndex(int attachFileIndex) {
		this.attachFileIndex = attachFileIndex;
	}
	
	/**
	 * @return the viewContract
	 */
	public boolean isViewContract() {
		return viewContract;
	}
	
	/**
	 * @param viewContract the viewContract to set
	 */
	public void setViewContract(boolean viewContract) {
		this.viewContract = viewContract;
	}
	
	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if (remarkList == null) {
			remarkList = new ArrayList<CtRemark>();
		}
		return remarkList;
	}
	
	/**
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}
	
	/**
	 * @return the attachmentList
	 */
	public List<CtAttachment> getAttachmentList() {
		if (attachmentList == null) {
			attachmentList = new ArrayList<CtAttachment>();
		}
		return attachmentList;
	}
	
	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public CtContractVer getCtContractVer() {
		return ctContractVer;
	}

	public void setCtContractVer(CtContractVer ctContractVer) {
		this.ctContractVer = ctContractVer;
	}

	public CtContractVer getAgreementContractVer() {
		return agreementContractVer;
	}

	public void setAgreementContractVer(CtContractVer agreementContractVer) {
		this.agreementContractVer = agreementContractVer;
	}

	public double getPerformanceBondRuleAmt() {
		return performanceBondRuleAmt;
	}

	public void setPerformanceBondRuleAmt(double performanceBondRuleAmt) {
		this.performanceBondRuleAmt = performanceBondRuleAmt;
	}

	public String getSupplierCompanyName() {
		return supplierCompanyName;
	}

	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}

	public boolean isUpdateCompanyName() {
		return updateCompanyName;
	}

	public void setUpdateCompanyName(boolean updateCompanyName) {
		this.updateCompanyName = updateCompanyName;
	}

	public String getSupplierBranchName() {
		return supplierBranchName;
	}

	public void setSupplierBranchName(String supplierBranchName) {
		this.supplierBranchName = supplierBranchName;
	}

	public String getSupplierAddress() {
		return supplierAddress;
	}

	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	public boolean isUpdateSupplierAddress() {
		return updateSupplierAddress;
	}

	public void setUpdateSupplierAddress(boolean updateSupplierAddress) {
		this.updateSupplierAddress = updateSupplierAddress;
	}

	public SmAddress getSmAddress() {
		return smAddress;
	}

	public void setSmAddress(SmAddress smAddress) {
		this.smAddress = smAddress;
	}

	public boolean isDirectPurchase() {
		return directPurchase;
	}

	public void setDirectPurchase(boolean directPurchase) {
		this.directPurchase = directPurchase;
	}

	public boolean isGstCheckingGUI() {
		return gstCheckingGUI;
	}

	public void setGstCheckingGUI(boolean gstCheckingGUI) {
		this.gstCheckingGUI = gstCheckingGUI;
	}
	
	
}
