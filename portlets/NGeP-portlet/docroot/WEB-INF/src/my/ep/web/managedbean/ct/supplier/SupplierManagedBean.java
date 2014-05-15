/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.supplier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Supplier
 * 
 * @author Alam
 * @since 7/11/2013	 
 */
@ManagedBean (name ="supplierManagedBean")
@SessionScoped
public class SupplierManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String[] action;
	private String taskName;
    private String chkMenuOrTxtBox;;
	private Date endDate;
	private String loaNo;
	private Date startDate;
	private Long contractNo;
	private String contractName;
	private String loginUser;
	private int selectedIndex;
	private CtFactoring factoring;
	private CtFactoring uploadFiles;
	private SearchFilterBean searchFilterBean = new SearchFilterBean();
	private List<CtContract> contractList = new ArrayList<CtContract>();
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	
	private CtContractVer contractVer;
	private CtContract ctContract;
	private List<PmFinancialAcc>  financialAccList;
	private String accountNo="";
	private boolean removeFactoring;
	
	public SupplierManagedBean(){
		
	}
	
	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}
	
	public Long getContractNo() {
		return contractNo;
	}
	
	public void setContractNo(Long contractNo) {
		this.contractNo = contractNo;
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	public CtFactoring getFactoring() {
		return factoring;
	}
	
	public void setFactoring(CtFactoring factoring) {
		this.factoring = factoring;
	}
		
	public SearchFilterBean getSearchFilterBean() {
		return searchFilterBean;
	}
	
	public void setSearchFilterBean(SearchFilterBean searchFilterBean) {
		this.searchFilterBean = searchFilterBean;
	}
	
	public List<CtContract> getContractList() {
		return contractList;
	}
	
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
	
	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
			remarkList = new ArrayList<CtRemark>();
		return remarkList;
	}

	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	public List<CtAttachment> getAttachmentList() {
		if(attachmentList == null)
		   attachmentList = new ArrayList<CtAttachment>();
		return attachmentList;
	}

	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public CtContractVer getContractVer() {
		return contractVer;
	}

	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getLoaNo() {
		return loaNo;
	}

	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public CtContract getCtContract() {
		return ctContract;
	}

	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	public String getChkMenuOrTxtBox() {
		return chkMenuOrTxtBox;
	}

	public void setChkMenuOrTxtBox(String chkMenuOrTxtBox) {
		this.chkMenuOrTxtBox = chkMenuOrTxtBox;
	}

	public List<PmFinancialAcc> getFinancialAccList() {
		if(financialAccList == null)
			financialAccList = new ArrayList<PmFinancialAcc>();
		 return financialAccList;
	}

	public void setFinancialAccList(List<PmFinancialAcc> financialAccList) {
		this.financialAccList = financialAccList;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public boolean isRemoveFactoring() {
		return removeFactoring;
	}

	public void setRemoveFactoring(boolean removeFactoring) {
		this.removeFactoring = removeFactoring;
	}

	public CtFactoring getUploadFiles() {
		return uploadFiles;
	}

	public void setUploadFiles(CtFactoring uploadFiles) {
		this.uploadFiles = uploadFiles;
	}	
}
