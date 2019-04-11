/*
 * Copyright © 2016 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.custservice;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.web.bean.pm.ProfileManagementSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Pm Customer Service
 * 
 * @author ken
 * @since 2/12/2016
 * 
 */
@ManagedBean(name = "custServiceManagedBean")
@SessionScoped
public class CustServiceManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private ProfileManagementSearchBean searchBean = new ProfileManagementSearchBean();
	private PmParameter selectedOrgTypeParam;
	private boolean isLazy;
	private List<PmUserRole> selectedRoleList;
	private PmUserOrg selectedUserOrg;
	private List<PmUserGroup> flUserGroups;
	private List<PmUserGroup> rnUserGroups;
	private Integer tabActiveIndex;
	

	public CustServiceManagedBean() {
	}

	/**
	 * @return the searchBean
	 */
	public ProfileManagementSearchBean getSearchBean() {
		if (searchBean == null) {
			searchBean = new ProfileManagementSearchBean();
		}
		return searchBean;
	}

	/**
	 * @param searchBean
	 *            the searchBean to set
	 */
	public void setSearchBean(ProfileManagementSearchBean searchBean) {
		this.searchBean = searchBean;
	}

	/**
	 * @return the selectedOrgTypeParam
	 */
	public PmParameter getSelectedOrgTypeParam() {
		if (selectedOrgTypeParam == null) {
			selectedOrgTypeParam = new PmParameter();
		}
		return selectedOrgTypeParam;
	}

	/**
	 * @param selectedOrgTypeParam
	 *            the selectedOrgTypeParam to set
	 */
	public void setSelectedOrgTypeParam(PmParameter selectedOrgTypeParam) {
		this.selectedOrgTypeParam = selectedOrgTypeParam;
	}

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * @return the selectedRoleList
	 */
	public List<PmUserRole> getSelectedRoleList() {
		if(selectedRoleList == null){
			selectedRoleList = new ArrayList<PmUserRole>();
		}
		return selectedRoleList;
	}

	/**
	 * @param selectedRoleList the selectedRoleList to set
	 */
	public void setSelectedRoleList(List<PmUserRole> selectedRoleList) {
		this.selectedRoleList = selectedRoleList;
	}

	/**
	 * @return the selectedUserOrg
	 */
	public PmUserOrg getSelectedUserOrg() {
		return selectedUserOrg;
	}

	/**
	 * @param selectedUserOrg the selectedUserOrg to set
	 */
	public void setSelectedUserOrg(PmUserOrg selectedUserOrg) {
		this.selectedUserOrg = selectedUserOrg;
	}

	/**
	 * @return the tabActiveIndex
	 */
	public Integer getTabActiveIndex() {
		if(tabActiveIndex == null){
			tabActiveIndex = 0;
		}
		return tabActiveIndex;
	}

	/**
	 * @param tabActiveIndex the tabActiveIndex to set
	 */
	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * @return the flUserGroups
	 */
	public List<PmUserGroup> getFlUserGroups() {
		return flUserGroups;
	}

	/**
	 * @param flUserGroups the flUserGroups to set
	 */
	public void setFlUserGroups(List<PmUserGroup> flUserGroups) {
		this.flUserGroups = flUserGroups;
	}

	/**
	 * @return the rnUserGroups
	 */
	public List<PmUserGroup> getRnUserGroups() {
		return rnUserGroups;
	}

	/**
	 * @param rnUserGroups the rnUserGroups to set
	 */
	public void setRnUserGroups(List<PmUserGroup> rnUserGroups) {
		this.rnUserGroups = rnUserGroups;
	}

	

	

}
