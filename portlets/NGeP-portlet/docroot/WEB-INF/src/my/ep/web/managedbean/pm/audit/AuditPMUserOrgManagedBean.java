/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.audit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.web.bean.pm.AuditPMUserOrgSearchBean;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Location
 * 
 * 
 */
@ManagedBean(name = "userOrgMB")
@SessionScoped
public class AuditPMUserOrgManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private OrganisationSearchBean organisationSearchBean;
	private AuditPMUserOrgSearchBean searchBean;
	private Integer searchOrgCategory = 0;
	private int orgCategory = 0;
	private String orgType;
	private Long orgTypeId;
	private boolean notFirstRun;
	private PmOrgProfile selectedOrgProfile;
	private String orgTypeParamId;
	private UserProfilesSearchBean userProfileSearchBean;
	private PmOrgProfile currentActiveOrg;
	private String additionalOrgFilter;
	private PmOrgValidity pmOrgValidity;
	private PmParameter selectedOrgType;
	private boolean isDisabledOrgFields = false;

	private String selectedType;

	private boolean isLazy = false;

	private String paramLevel3;

	public AuditPMUserOrgManagedBean() {
		/* Empty Constructor */
	}

	/**
	 * @return the selectedType
	 */
	public String getSelectedType() {
		return selectedType;
	}

	/**
	 * @param selectedType
	 *            the selectedType to set
	 */
	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	public AuditPMUserOrgSearchBean getSearchBean() {
		if (searchBean == null)
			searchBean = new AuditPMUserOrgSearchBean();
		return searchBean;
	}

	public void setSearchBean(AuditPMUserOrgSearchBean searchBean) {
		this.searchBean = searchBean;
	}

	public Integer getSearchOrgCategory() {
		return searchOrgCategory;
	}

	public void setSearchOrgCategory(Integer searchOrgCategory) {
		this.searchOrgCategory = searchOrgCategory;
	}

	public int getOrgCategory() {
		return orgCategory;
	}

	public void setOrgCategory(int orgCategory) {
		this.orgCategory = orgCategory;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public Long getOrgTypeId() {
		return orgTypeId;
	}

	public void setOrgTypeId(Long orgTypeId) {
		this.orgTypeId = orgTypeId;
	}

	public boolean isNotFirstRun() {
		return notFirstRun;
	}

	public void setNotFirstRun(boolean notFirstRun) {
		this.notFirstRun = notFirstRun;
	}

	public OrganisationSearchBean getOrganisationSearchBean() {
		return organisationSearchBean;
	}

	public void setOrganisationSearchBean(OrganisationSearchBean organisationSearchBean) {
		this.organisationSearchBean = organisationSearchBean;
	}

	/**
	 * @return the selectedOrgProfile
	 */
	public PmOrgProfile getSelectedOrgProfile() {
		if (selectedOrgProfile == null) {
			this.selectedOrgProfile = new PmOrgProfile();
		}
		return selectedOrgProfile;
	}

	/**
	 * @param selectedOrgProfile
	 *            the selectedOrgProfile to set
	 */
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	/**
	 * @return the userProfileSearchBean
	 */
	public UserProfilesSearchBean getUserProfileSearchBean() {
		if (userProfileSearchBean == null)
			userProfileSearchBean = new UserProfilesSearchBean();
		return userProfileSearchBean;
	}

	/**
	 * @param userProfileSearchBean
	 *            the userProfileSearchBean to set
	 */
	public void setUserProfileSearchBean(UserProfilesSearchBean userProfileSearchBean) {
		this.userProfileSearchBean = userProfileSearchBean;
	}

	/**
	 * @return the currentActiveOrg
	 */
	public PmOrgProfile getCurrentActiveOrg() {
		if (currentActiveOrg == null) {
			this.currentActiveOrg = new PmOrgProfile();
		}
		return currentActiveOrg;
	}

	/**
	 * @param currentActiveOrg
	 *            the currentActiveOrg to set
	 */
	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
	}

	/**
	 * @return the additionalOrgFilter
	 */
	public String getAdditionalOrgFilter() {
		return additionalOrgFilter;
	}

	/**
	 * @param additionalOrgFilter
	 *            the additionalOrgFilter to set
	 */
	public void setAdditionalOrgFilter(String additionalOrgFilter) {
		this.additionalOrgFilter = additionalOrgFilter;
	}

	public String getOrgTypeParamId() {
		return orgTypeParamId;
	}

	public void setOrgTypeParamId(String orgTypeParamId) {
		this.orgTypeParamId = orgTypeParamId;
	}

	public PmOrgValidity getPmOrgValidity() {
		return pmOrgValidity;
	}

	public void setPmOrgValidity(PmOrgValidity pmOrgValidity) {
		this.pmOrgValidity = pmOrgValidity;
	}

	public String getParamLevel3() {
		return paramLevel3;
	}

	public void setParamLevel3(String paramLevel3) {
		this.paramLevel3 = paramLevel3;
	}

	/**
	 * @return the selectedOrgType
	 */
	public PmParameter getSelectedOrgType() {
		if (this.selectedOrgType == null) {
			this.selectedOrgType = new PmParameter();
		}
		return selectedOrgType;
	}

	/**
	 * @param selectedOrgType
	 *            the selectedOrgType to set
	 */
	public void setSelectedOrgType(PmParameter selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	/**
	 * @return the isDisabledOrgFields
	 */
	public boolean isDisabledOrgFields() {
		return isDisabledOrgFields;
	}

	/**
	 * @param isDisabledOrgFields
	 *            the isDisabledOrgFields to set
	 */
	public void setDisabledOrgFields(boolean isDisabledOrgFields) {
		this.isDisabledOrgFields = isDisabledOrgFields;
	}

}
