package my.ep.web.managedbean.pm.financialorg;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.web.bean.pm.FinancialOrgSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
/**
 * Managed Bean for Financial Org
 * 
 * @author RAIN POH
 * @since 8/1/2014
 *
 */
@ManagedBean(name = "financialOrgManagedBean")
@SessionScoped
public class FinancialOrgManagedBean extends BaseManagedBean implements Serializable {


	private static final long serialVersionUID = 1L;
	// Financial Search
	private FinancialOrgSearchBean financialOrgSearchBean;
	
	// Financial Org
	private PmFinancialOrg financialOrg = new PmFinancialOrg();

	// Financial Org address
	private PmAddress pmAddress;
	private PmContact contactPerson;

//	// Financial Branch
//	private PmFinancialBranch selectedPmFinancialBranch;
	
//	// Financial Branch address
//	private PmAddress branchAddress;
//	private PmContact branchContactPerson;


//	// Financial Branch Acc
//	private List<PmFinancialAcc> selectedPmFinancialBranchAcc;
	
	// General
	private boolean showParentMsg;
	private List<String> serviceTypes;
	private boolean enableUpdateStatus;
	private int selectedTabIndex;
	private Date currentDate = new Date();
	private boolean showFactoringBank=true;
	
	private boolean isLazy = false; 


	/**
	 * @return the currentDate
	 */
	public Date getCurrentDate() {
		return currentDate;
	}

	/**
	 * @param currentDate the currentDate to set
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

//	/**
//	 * @return the branchAddress
//	 */
//	public PmAddress getBranchAddress() {
//		return branchAddress;
//	}

//	/**
//	 * @param branchAddress
//	 *            the branchAddress to set
//	 */
//	public void setBranchAddress(PmAddress branchAddress) {
//		this.branchAddress = branchAddress;
//	}
//
//	/**
//	 * @return the branchContactPerson
//	 */
//	public PmContact getBranchContactPerson() {
//		return branchContactPerson;
//	}
//
//	/**
//	 * @param branchContactPerson
//	 *            the branchContactPerson to set
//	 */
//	public void setBranchContactPerson(PmContact branchContactPerson) {
//		this.branchContactPerson = branchContactPerson;
//	}

	public PmAddress getPmAddress() {
		return pmAddress;
	}

	public void setPmAddress(PmAddress pmAddress) {
		this.pmAddress = pmAddress;
	}


	/**
	 * @return the financialOrg
	 */
	public PmFinancialOrg getFinancialOrg() {
		return financialOrg;
	}

	/**
	 * @param financialOrg
	 *            the financialOrg to set
	 */
	public void setFinancialOrg(PmFinancialOrg financialOrg) {
		this.financialOrg = financialOrg;
	}

	/**
	 * @return the contactPerson
	 */
	public PmContact getContactPerson() {
		return contactPerson;
	}

	/**
	 * @param contactPerson
	 *            the contactPerson to set
	 */
	public void setContactPerson(PmContact contactPerson) {
		this.contactPerson = contactPerson;
	}

	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * @param selectedTabIndex
	 *            the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}


	/**
	 * @return the financialOrgSearchBean
	 */
	public FinancialOrgSearchBean getFinancialOrgSearchBean() {
		if (financialOrgSearchBean == null)
			financialOrgSearchBean = new FinancialOrgSearchBean();
		return financialOrgSearchBean;
	}

	/**
	 * @param financialOrgSearchBean
	 *            the financialOrgSearchBean to set
	 */
	public void setFinancialOrgSearchBean(
			FinancialOrgSearchBean financialOrgSearchBean) {
		this.financialOrgSearchBean = financialOrgSearchBean;
	}

	/**
	 * @return the showParentMsg
	 */
	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	/**
	 * @param showParentMsg
	 *            the showParentMsg to set
	 */
	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

//	/**
//	 * @return the selectedPmFinancialBranch
//	 */
//	public PmFinancialBranch getSelectedPmFinancialBranch() {
//		return selectedPmFinancialBranch;
//	}
//
//	/**
//	 * @param selectedPmFinancialBranch
//	 *            the selectedPmFinancialBranch to set
//	 */
//	public void setSelectedPmFinancialBranch(
//			PmFinancialBranch selectedPmFinancialBranch) {
//		this.selectedPmFinancialBranch = selectedPmFinancialBranch;
//	}

	/**
	 * @return the serviceTypes
	 */
	public List<String> getServiceTypes() {
		return serviceTypes;
	}

	/**
	 * @param serviceTypes
	 *            the serviceTypes to set
	 */
	public void setServiceTypes(List<String> serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	/**
	 * @return the enableUpdateStatus
	 */
	public boolean isEnableUpdateStatus() {
		return enableUpdateStatus;
	}

	/**
	 * @param enableUpdateStatus the enableUpdateStatus to set
	 */
	public void setEnableUpdateStatus(boolean enableUpdateStatus) {
		this.enableUpdateStatus = enableUpdateStatus;
	}

	/**
	 * @return the showFactoringBank
	 */
	public boolean isShowFactoringBank() {
		return showFactoringBank;
	}

	/**
	 * @param showFactoringBank the showFactoringBank to set
	 */
	public void setShowFactoringBank(boolean showFactoringBank) {
		this.showFactoringBank = showFactoringBank;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

//	/**
//	 * @return the selectedPmFinancialBranchAcc
//	 */
//	public List<PmFinancialAcc> getSelectedPmFinancialBranchAcc() {
//		return selectedPmFinancialBranchAcc;
//	}
//
//	/**
//	 * @param selectedPmFinancialBranchAcc the selectedPmFinancialBranchAcc to set
//	 */
//	public void setSelectedPmFinancialBranchAcc(
//			List<PmFinancialAcc> selectedPmFinancialBranchAcc) {
//		this.selectedPmFinancialBranchAcc = selectedPmFinancialBranchAcc;
//	}

	
}
