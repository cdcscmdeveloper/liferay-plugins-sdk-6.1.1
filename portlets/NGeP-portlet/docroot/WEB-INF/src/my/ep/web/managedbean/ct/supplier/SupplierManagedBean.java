/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.supplier;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.sm.SmSupplierBank;
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
	private Date effDate;
	private String loaNo;
	private Date expDate;
	private Date loaDate;
	private String contractNo;
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
	private Long loaId;
	private Long contractVerId;
	private SmSupplierBank supplierBank;
	private String supplierAddress;
	private String supplierBankAddress;
	
	private Map<Long, String> factoringCompMap;
	private String factoringBankName;
	private String factoringBankAddress;
	
	private boolean agreeFactoringClause;
	
	public SupplierManagedBean(){
		
	}
	
	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}
	
	public String getContractNo() {
		return contractNo;
	}
	
	public void setContractNo(String contractNo) {
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

	public String getLoaNo() {
		return loaNo;
	}

	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}

	public Date getEffDate() {
		return effDate;
	}

	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}

	public Date getExpDate() {
		return expDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}

	public Date getLoaDate() {
		return loaDate;
	}

	public void setLoaDate(Date loaDate) {
		this.loaDate = loaDate;
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

	public Long getLoaId() {
		return loaId;
	}

	public void setLoaId(Long loaId) {
		this.loaId = loaId;
	}

	public SmSupplierBank getSupplierBank() {
		return supplierBank;
	}

	public void setSupplierBank(SmSupplierBank supplierBank) {
		this.supplierBank = supplierBank;
	}

	public String getSupplierAddress() {
		return supplierAddress;
	}

	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	public String getSupplierBankAddress() {
		return supplierBankAddress;
	}

	public void setSupplierBankAddress(String supplierBankAddress) {
		this.supplierBankAddress = supplierBankAddress;
	}

	public Long getContractVerId() {
		return contractVerId;
	}

	public void setContractVerId(Long contractVerId) {
		this.contractVerId = contractVerId;
	}

	public Map<Long, String> getFactoringCompMap() {
		if(factoringCompMap==null) {
			factoringCompMap = new HashMap<Long, String>();
		}
		return factoringCompMap;
	}

	public void setFactoringCompMap(Map<Long, String> factoringCompMap) {
		this.factoringCompMap = factoringCompMap;
	}

	public String getFactoringBankName() {
		return factoringBankName;
	}

	public void setFactoringBankName(String factoringBankName) {
		this.factoringBankName = factoringBankName;
	}

	public String getFactoringBankAddress() {
		return factoringBankAddress;
	}

	public void setFactoringBankAddress(String factoringBankAddress) {
		this.factoringBankAddress = factoringBankAddress;
	}

	public boolean isAgreeFactoringClause() {
		return agreeFactoringClause;
	}

	public void setAgreeFactoringClause(boolean agreeFactoringClause) {
		this.agreeFactoringClause = agreeFactoringClause;
	}	
	
	
}
