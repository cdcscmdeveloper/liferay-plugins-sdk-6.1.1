/*
 * Copyright © 2016 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

/**
 * Search Bean for Profile Management
 * 
 * @author Zeinab
 * @since 25/2/2016
 *
 */
public class ProfileManagementSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long selectedOrgType;
	private PmOrgProfile selectedOrgProfile;
	private Boolean companyProfile;
	private Boolean marketResearch;
	private Long appStatusId;
	private String identificationNo;
	private String userName;
	//shafie - #28833 -start
	private PmOrgProfile ptj;
	private PmOrgProfile ministry;
	private PmOrgProfile kptj;
	//shafie - #28833 -start
	
	public ProfileManagementSearchBean(){}

	/**
	 * @return the selectedOrgType
	 */
	public Long getSelectedOrgType() {
		return selectedOrgType;
	}

	/**
	 * @param selectedOrgType the selectedOrgType to set
	 */
	public void setSelectedOrgType(Long selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	/**
	 * @return the selectedOrgProfile
	 */
	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	/**
	 * @param selectedOrgProfile the selectedOrgProfile to set
	 */
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	/**
	 * @return the companyProfile
	 */
	public Boolean getCompanyProfile() {
		return companyProfile;
	}

	/**
	 * @param companyProfile the companyProfile to set
	 */
	public void setCompanyProfile(Boolean companyProfile) {
		this.companyProfile = companyProfile;
	}

	/**
	 * @return the marketResearch
	 */
	public Boolean getMarketResearch() {
		return marketResearch;
	}

	/**
	 * @param marketResearch the marketResearch to set
	 */
	public void setMarketResearch(Boolean marketResearch) {
		this.marketResearch = marketResearch;
	}

	/**
	 * @return the appStatusId
	 */
	public Long getAppStatusId() {
		return appStatusId;
	}

	/**
	 * @param appStatusId the appStatusId to set
	 */
	public void setAppStatusId(Long appStatusId) {
		this.appStatusId = appStatusId;
	}

	/**
	 * @return the identificationNo
	 */
	public String getIdentificationNo() {
		return identificationNo;
	}

	/**
	 * @param identificationNo the identificationNo to set
	 */
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	//shafie - #28833- start
	
	/**
	 * @return the ministry
	 */
	public PmOrgProfile getMinistry() {
		if (ministry == null)
			ministry = new PmOrgProfile();
		return ministry;
	}
		
	
	/**
	 * @param ministry
	 *            the ministry to set
	 */
	public void setMinistry(PmOrgProfile ministry) {
		this.ministry = ministry;	
	}
	
	/**
	 * @return the kptj
	 */
	public PmOrgProfile getKptj() {
		return kptj;
	}

	/**
	 * @param kptj
	 *            the kptj to set
	 */
	public void setKptj(PmOrgProfile kptj) {
		this.kptj = kptj;
	}
	
	/**
	 * @return the ptj
	 */
	public PmOrgProfile getPtj() {
		if (ptj == null)
			ptj = new PmOrgProfile();
		return ptj;
	}

	/**
	 * @param ptj
	 *            the ptj to set
	 */
	public void setPtj(PmOrgProfile ptj) {
		this.ptj = ptj;
	}
	
	//shafie - #28833- end
}
