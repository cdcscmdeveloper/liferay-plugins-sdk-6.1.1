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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.DualListModel;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Supplier
 * 
 * @author Alam
 * @since 7/11/2013
 */
@ManagedBean(name = "supplierManagedBean")
@SessionScoped
public class SupplierManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private String contractNo;
	private String contractName;
	private String originalAccountNo;
	private String curentAccNoBankName;
	private String accBankName;
	private String factoringBankAddress;
	private String textBeforeSign;
	private String buttonAction;
	private String fileDirectory;
	private String accountNoAndName;
	private Date effDate;
	private Date expDate;
	private Date loaDate;
	private int index;
	private boolean removeFactoring;
	private boolean agreeFactoringClause;
	private boolean viewOnly = false;
	private boolean autoUpdate;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<PmUser> contractAdminlist;
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUser> userMap;
	private Map<Long, String> factoringCompMap;
	private Map<String, SmSupplierBank> supplierBankAccountMap;
	private SelectItem[] listOfFinancialOrg;
	private SelectItem[] populateBankAccountList;
	private CtContract selectedContract;
	private CtContract ctContract;
	private CtContractVer contractVer;
	private CtFactoring factoring;
	private PmUser supplierPmUser;
	private PmDigiSign pmDigiSign;
	
    @ManagedProperty(value="#{spkiBackingBean}")
    private SpkiBackingBean spkiBackingBean;
	
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
	 * @return the contractName
	 */
	public String getContractName() {
		return contractName;
	}
	
	/**
	 * @param contractName the contractName to set
	 */
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	
	/**
	 * @return the originalAccountNo
	 */
	public String getOriginalAccountNo() {
		return originalAccountNo;
	}
	
	/**
	 * @param originalAccountNo the originalAccountNo to set
	 */
	public void setOriginalAccountNo(String originalAccountNo) {
		this.originalAccountNo = originalAccountNo;
	}
	
	/**
	 * @return the accBankName
	 */
	public String getAccBankName() {
		return accBankName;
	}
	
	/**
	 * @param accBankName the accBankName to set
	 */
	public void setAccBankName(String accBankName) {
		this.accBankName = accBankName;
	}
	
	/**
	 * @return the factoringBankAddress
	 */
	public String getFactoringBankAddress() {
		return factoringBankAddress;
	}
	
	/**
	 * @param factoringBankAddress the factoringBankAddress to set
	 */
	public void setFactoringBankAddress(String factoringBankAddress) {
		this.factoringBankAddress = factoringBankAddress;
	}
	
	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}
	
	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
	/**
	 * @return the buttonAction
	 */
	public String getButtonAction() {
		return buttonAction;
	}
	
	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}
	
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
	 * @return the accountNoAndName
	 */
	public String getAccountNoAndName() {
		return accountNoAndName;
	}
	
	/**
	 * @param accountNoAndName the accountNoAndName to set
	 */
	public void setAccountNoAndName(String accountNoAndName) {
		this.accountNoAndName = accountNoAndName;
	}
	
	/**
	 * @return the effDate
	 */
	public Date getEffDate() {
		return effDate;
	}
	
	/**
	 * @param effDate the effDate to set
	 */
	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}
	
	/**
	 * @return the expDate
	 */
	public Date getExpDate() {
		return expDate;
	}
	
	/**
	 * @param expDate the expDate to set
	 */
	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}
	
	/**
	 * @return the loaDate
	 */
	public Date getLoaDate() {
		return loaDate;
	}
	
	/**
	 * @param loaDate the loaDate to set
	 */
	public void setLoaDate(Date loaDate) {
		this.loaDate = loaDate;
	}	
	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the removeFactoring
	 */
	public boolean isRemoveFactoring() {
		return removeFactoring;
	}
	
	/**
	 * @param removeFactoring the removeFactoring to set
	 */
	public void setRemoveFactoring(boolean removeFactoring) {
		this.removeFactoring = removeFactoring;
	}
	
	/**
	 * @return the agreeFactoringClause
	 */
	public boolean isAgreeFactoringClause() {
		return agreeFactoringClause;
	}
	
	/**
	 * @param agreeFactoringClause the agreeFactoringClause to set
	 */
	public void setAgreeFactoringClause(boolean agreeFactoringClause) {
		this.agreeFactoringClause = agreeFactoringClause;
	}
	
	/**
	 * @return the viewOnly
	 */
	public boolean isViewOnly() {
		return viewOnly;
	}
	
	/**
	 * @param viewOnly the viewOnly to set
	 */
	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}
	
	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}
	
	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
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
	
	/**
	 * @return the contractAdminlist
	 */
	public List<PmUser> getContractAdminlist() {
		return contractAdminlist;
	}
	
	/**
	 * @param contractAdminlist the contractAdminlist to set
	 */
	public void setContractAdminlist(List<PmUser> contractAdminlist) {
		this.contractAdminlist = contractAdminlist;
	}
	
	/**
	 * @return the approverModel
	 */
	public DualListModel<PmUser> getApproverModel() {
		return approverModel;
	}
	
	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<PmUser> approverModel) {
		this.approverModel = approverModel;
	}
	
	/**
	 * @return the userMap
	 */
	public Map<Long, PmUser> getUserMap() {
		if (userMap == null) {
			userMap = new HashMap<Long, PmUser>();
		}
		return userMap;
	}
	
	/**
	 * @param userMap the userMap to set
	 */
	public void setUserMap(Map<Long, PmUser> userMap) {
		this.userMap = userMap;
	}
	
	/**
	 * @return the factoringCompMap
	 */
	public Map<Long, String> getFactoringCompMap() {
		if (factoringCompMap == null) {
			factoringCompMap = new HashMap<Long, String>();
		}
		return factoringCompMap;
	}
	
	/**
	 * @param factoringCompMap the factoringCompMap to set
	 */
	public void setFactoringCompMap(Map<Long, String> factoringCompMap) {
		this.factoringCompMap = factoringCompMap;
	}
	
	/**
	 * @return the supplierBankAccountMap
	 */
	public Map<String, SmSupplierBank> getSupplierBankAccountMap() {
		if (supplierBankAccountMap == null) {
			supplierBankAccountMap = new HashMap<String, SmSupplierBank>();
		}
		return supplierBankAccountMap;
	}
	
	/**
	 * @param supplierBankAccountMap the supplierBankAccountMap to set
	 */
	public void setSupplierBankAccountMap(Map<String, SmSupplierBank> supplierBankAccountMap) {
		this.supplierBankAccountMap = supplierBankAccountMap;
	}
	
	/**
	 * @return the listOfFinancialOrg
	 */
	public SelectItem[] getListOfFinancialOrg() {
		return listOfFinancialOrg;
	}
	
	/**
	 * @param listOfFinancialOrg the listOfFinancialOrg to set
	 */
	public void setListOfFinancialOrg(SelectItem[] listOfFinancialOrg) {
		this.listOfFinancialOrg = listOfFinancialOrg;
	}
	
	/**
	 * @return the populateBankAccountList
	 */
	public SelectItem[] getPopulateBankAccountList() {
		return populateBankAccountList;
	}

	/**
	 * @param populateBankAccountList the populateBankAccountList to set
	 */
	public void setPopulateBankAccountList(SelectItem[] populateBankAccountList) {
		this.populateBankAccountList = populateBankAccountList;
	}
	
	/**
	 * @return the selectedContract
	 */
	public CtContract getSelectedContract() {
		return selectedContract;
	}
	
	/**
	 * @param selectedContract the selectedContract to set
	 */
	public void setSelectedContract(CtContract selectedContract) {
		this.selectedContract = selectedContract;
	}
	
	/**
	 * @return the ctContract
	 */
	public CtContract getCtContract() {
		return ctContract;
	}
	
	/**
	 * @param ctContract the ctContract to set
	 */
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}
	
	/**
	 * @return the contractVer
	 */
	public CtContractVer getContractVer() {
		return contractVer;
	}
	
	/**
	 * @param contractVer the contractVer to set
	 */
	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}
	
	/**
	 * @return the factoring
	 */
	public CtFactoring getFactoring() {
		return factoring;
	}
	
	/**
	 * @param factoring the factoring to set
	 */
	public void setFactoring(CtFactoring factoring) {
		this.factoring = factoring;
	}
	
	/**
	 * @return the supplierPmUser
	 */
	public PmUser getSupplierPmUser() {
		return supplierPmUser;
	}
	
	/**
	 * @param supplierPmUser the supplierPmUser to set
	 */
	public void setSupplierPmUser(PmUser supplierPmUser) {
		this.supplierPmUser = supplierPmUser;
	}
	
	/**
	 * @return the pmDigiSign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}
	
	/**
	 * @param pmDigiSign the pmDigiSign to set
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * @return the curentAccNoBankName
	 */
	public String getCurentAccNoBankName() {
		return curentAccNoBankName;
	}

	/**
	 * @param pmDigiSign the curentAccNoBankName to set
	 */
	public void setCurentAccNoBankName(String curentAccNoBankName) {
		this.curentAccNoBankName = curentAccNoBankName;
	}

	/**
	 * @return the spkiBackingBean
	 */
	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	/**
	 * @param spkiBackingBean the spkiBackingBean to set
	 */
	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}
}
