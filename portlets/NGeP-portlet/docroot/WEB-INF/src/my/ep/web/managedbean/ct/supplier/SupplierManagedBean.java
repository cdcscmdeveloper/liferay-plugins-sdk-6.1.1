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
import javax.faces.model.SelectItem;

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
	
	private Date effDate;
	private Date expDate;
	private Date loaDate;
	
	private boolean removeFactoring;
	private boolean agreeFactoringClause;
	
	private Long loaId;
	private int selectedIndex;
	private Long contractVerId;
	
	private String loaNo;
	private String contractNo;
	private String contractName;
	private String loginUser;
	private String accountNo;
	private String originalAccountNo;
	private String factoringBankName;
	private String factoringBankAddress;
	
	private CtContract ctContract;
	private CtContractVer contractVer;
	private CtFactoring factoring;
	private CtFactoring uploadFiles;
	private List<PmFinancialAcc>  financialAccList;
	private SelectItem[] financialBankBranch;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private Map<Long, String> factoringCompMap;
	private Map<String, SmSupplierBank> supplierBankAccountMap;
	private List<CtContract> contractList = new ArrayList<CtContract>();
	private SearchFilterBean searchFilterBean = new SearchFilterBean();
	private String fileDirectory;
	private String accountNoAndName;
	
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

	public List<PmFinancialAcc> getFinancialAccList() {
		if(financialAccList == null)
			financialAccList = new ArrayList<PmFinancialAcc>();
		 return financialAccList;
	}

	public void setFinancialAccList(List<PmFinancialAcc> financialAccList) {
		this.financialAccList = financialAccList;
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

	public Map<String, SmSupplierBank> getSupplierBankAccountMap() {
		if(supplierBankAccountMap==null) {
			supplierBankAccountMap = new HashMap<String, SmSupplierBank>();
		}
		return supplierBankAccountMap;
	}

	public void setSupplierBankAccountMap(Map<String, SmSupplierBank> supplierBankAccountMap) {
		this.supplierBankAccountMap = supplierBankAccountMap;
	}

	public String getOriginalAccountNo() {
		return originalAccountNo;
	}

	public void setOriginalAccountNo(String originalAccountNo) {
		this.originalAccountNo = originalAccountNo;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public SelectItem[] getFinancialBankBranch() {
		return financialBankBranch;
	}

	public void setFinancialBankBranch(SelectItem[] financialBankBranch) {
		this.financialBankBranch = financialBankBranch;
	}
	
	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	public String getAccountNoAndName() {
		return accountNoAndName;
	}

	public void setAccountNoAndName(String accountNoAndName) {
		this.accountNoAndName = accountNoAndName;
	}
}
