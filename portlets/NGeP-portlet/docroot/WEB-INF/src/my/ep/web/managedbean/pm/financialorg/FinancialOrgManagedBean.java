/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.financialorg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.bean.pm.FinancialOrgSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
/**
 * Managed Bean for Financial Organisation
 * 
 * @author venugopal
 * @since 30/7/2014
 *
 */
@ManagedBean(name = "financialOrgManagedBean")
@SessionScoped
public class FinancialOrgManagedBean extends BaseManagedBean {


	private static final long serialVersionUID = 1L;
	// Financial Search
	private FinancialOrgSearchBean financialOrgSearchBean;
	
	// Financial Org
	private PmFinancialOrg financialOrg = new PmFinancialOrg();

	// Financial Org address
	private PmAddress pmAddress;
	private PmContact contactPerson;
	private PmAddressType addressType;
	
	// General
	private boolean showParentMsg;
	private List<String> serviceTypes;
	private boolean enableUpdateStatus;
	private int selectedTabIndex;
	private Date currentDate = new Date();
	private boolean showFactoringBank=true;
	
	private boolean isLazy = false; 
	
	private Integer tabActiveIndex = 0;
	private PmFinancialSvc selectedDeleteSvc;
	private PmFinancialAcc selectedDeleteFactoringBank;
	
	private boolean addressAndContactPersonMandatory;
	private List<PmParameterDesc> bankServicesList;
	
	// #29487 - View bank details by factoring user_endorsed - Start
	public Long financialProfId;
	public boolean facCompanyInformation = false;
	// #29487 - View bank details by factoring user_endorsed - End

	/************ AUDIT #28895-35509 *************/
	private PmFinancialOrg oldFinancialOrg;
	private PmFinancialOrg savedFinancialOrg;
	private PmAddress oldPmAddress;
	private PmContact oldContactPerson;
	private Long pmAddressTypeId;
	private List<PmParameterDesc> oldBankServicesList;
	private List<PmFinancialAcc> oldFinancialAccList;
	private List<PmFinancialAcc> newFinancialAccList;
	/************ AUDIT #28895-35509 *************/
	public FinancialOrgManagedBean(){}

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

	/**
	 * @return the tabActiveIndex
	 */
	public Integer getTabActiveIndex() {
		return tabActiveIndex;
	}

	/**
	 * @param tabActiveIndex the tabActiveIndex to set
	 */
	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * @return the selectedDeleteFactoringBank
	 */
	public PmFinancialAcc getSelectedDeleteFactoringBank() {
		return selectedDeleteFactoringBank;
	}

	/**
	 * @param selectedDeleteFactoringBank the selectedDeleteFactoringBank to set
	 */
	public void setSelectedDeleteFactoringBank(
			PmFinancialAcc selectedDeleteFactoringBank) {
		this.selectedDeleteFactoringBank = selectedDeleteFactoringBank;
	}

	/**
	 * @return the selectedDeleteSvc
	 */
	public PmFinancialSvc getSelectedDeleteSvc() {
		return selectedDeleteSvc;
	}

	/**
	 * @param selectedDeleteSvc the selectedDeleteSvc to set
	 */
	public void setSelectedDeleteSvc(PmFinancialSvc selectedDeleteSvc) {
		this.selectedDeleteSvc = selectedDeleteSvc;
	}

	/**
	 * @return the addressAndContactPersonMandatory
	 */
	public boolean isAddressAndContactPersonMandatory() {
		return addressAndContactPersonMandatory;
	}

	/**
	 * @param addressAndContactPersonMandatory the addressAndContactPersonMandatory to set
	 */
	public void setAddressAndContactPersonMandatory(
			boolean addressAndContactPersonMandatory) {
		this.addressAndContactPersonMandatory = addressAndContactPersonMandatory;
	}

	/**
	 * @return the addressType
	 */
	public PmAddressType getAddressType() {
		return addressType;
	}

	/**
	 * @param addressType the addressType to set
	 */
	public void setAddressType(PmAddressType addressType) {
		this.addressType = addressType;
	}

	/**
	 * @return the bankServicesList
	 */
	public List<PmParameterDesc> getBankServicesList() {
		if(bankServicesList == null){
			bankServicesList = new ArrayList<PmParameterDesc>();
		}
		return bankServicesList;
	}

	/**
	 * @param bankServicesList the bankServicesList to set
	 */
	public void setBankServicesList(List<PmParameterDesc> bankServicesList) {
		this.bankServicesList = bankServicesList;
	}

	// #29487 - View bank details by factoring user_endorsed - Start
	/**
	 * @return the financialProfileId
	 */	
	public Long getFinancialId() {
		return financialProfId;
	}

	/**
	 * @param financialProfileId the financialProfileId to set
	 */	
	public void setFinancialId(Long financialProfId) {
		this.financialProfId = financialProfId;
	}
	
	/**
	 * @return the FacCompanyInformation
	 */	
	public boolean getFacCompanyInformation() {
		return facCompanyInformation;
	}

	/**
	 * @param FacCompanyInformation the FacCompanyInformation to set
	 */	
	public void setFacCompanyInformation(boolean facCompanyInformation) {
		this.facCompanyInformation = facCompanyInformation;
	}
	// #29487 - View bank details by factoring user_endorsed - End

	/************ AUDIT START #28895-35509 *************/
	public PmFinancialOrg getOldFinancialOrg() {
		return oldFinancialOrg;
	}

	public void setOldFinancialOrg(PmFinancialOrg oldFinancialOrg) {
		this.oldFinancialOrg = oldFinancialOrg;
	}
	
	public PmFinancialOrg getSavedFinancialOrg() {
		return savedFinancialOrg;
	}

	public void setSavedFinancialOrg(PmFinancialOrg savedFinancialOrg) {
		this.savedFinancialOrg = savedFinancialOrg;
	}
	
	public PmAddress getOldPmAddress() {
		return oldPmAddress;
	}
	
	public void setOldPmAddress(PmAddress oldPmAddress) {
		this.oldPmAddress = oldPmAddress;
	}

	/**
	 * @return the pmAddressTypeId
	 */
	public Long getPmAddressTypeId() {
		return pmAddressTypeId;
	}

	/**
	 * @param pmAddressTypeId the pmAddressTypeId to set
	 */
	public void setPmAddressTypeId(Long pmAddressTypeId) {
		this.pmAddressTypeId = pmAddressTypeId;
	}

	/**
	 * @return the oldContactPerson
	 */
	public PmContact getOldContactPerson() {
		return oldContactPerson;
	}

	/**
	 * @param oldContactPerson the oldContactPerson to set
	 */
	public void setOldContactPerson(PmContact oldContactPerson) {
		this.oldContactPerson = oldContactPerson;
	}

	/**
	 * @return the oldBankServicesList
	 */
	public List<PmParameterDesc> getOldBankServicesList() {
		return oldBankServicesList;
	}

	/**
	 * @param oldBankServicesList the oldBankServicesList to set
	 */
	public void setOldBankServicesList(List<PmParameterDesc> oldBankServicesList) {
		this.oldBankServicesList = oldBankServicesList;
	}

	/**
	 * @return the oldFinancialAccList
	 */
	public List<PmFinancialAcc> getOldFinancialAccList() {
		return oldFinancialAccList;
	}

	/**
	 * @param oldFinancialAccList the oldFinancialAccList to set
	 */
	public void setOldFinancialAccList(List<PmFinancialAcc> oldFinancialAccList) {
		this.oldFinancialAccList = oldFinancialAccList;
	}

	/**
	 * @return the newFinancialAccList
	 */
	public List<PmFinancialAcc> getNewFinancialAccList() {
		return newFinancialAccList;
	}

	/**
	 * @param newFinancialAccList the newFinancialAccList to set
	 */
	public void setNewFinancialAccList(List<PmFinancialAcc> newFinancialAccList) {
		this.newFinancialAccList = newFinancialAccList;
	}
	/************ AUDIT END #28895-35509 *************/

}
