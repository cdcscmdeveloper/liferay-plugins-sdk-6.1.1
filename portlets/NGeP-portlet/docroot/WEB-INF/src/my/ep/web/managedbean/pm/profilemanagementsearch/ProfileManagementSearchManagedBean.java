/*
 * Copyright © 2016 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.profilemanagementsearch;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.pm.ProfileManagementSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.profilemanagement.ProfileManagementManagedBean;

/**
 * The managed bean for profile Management Search
 * 
 * @author Zeinab
 * @since 24.02.2016
 */
@ManagedBean(name = "profileManagSearchManagedBean")
@SessionScoped
public class ProfileManagementSearchManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(ProfileManagementManagedBean.class);
	private Map<String, SelectItem[]> orgTypeMap = new HashMap<String, SelectItem[]>();
	private String selectedAppTypeParamCode;
	private Long selectedOrgType;

	private PmOrgProfile selectedOrgProfile;
	private ProfileManagementSearchBean profileManagSearchBean;

	public ProfileManagementSearchManagedBean() {
	}

	/**
	 * get search bean
	 * 
	 * @return ISearchBean<PmAccessAppl>
	 */
	public void fillRequestDTO(AccessApplReqDTO applReqDTO) {
		log.info("fillRequestDTO - Start");
		ProfileManagementSearchBean pmSearchBean = this
				.getProfileManagSearchBean();

		if (getSelectedOrgProfile() != null) {
			log.info("..getSelectedOrgProfile().getOrgProfileId()="
					+ getSelectedOrgProfile().getOrgProfileId());
			applReqDTO.setOrgProfileId(getSelectedOrgProfile()
					.getOrgProfileId());
		}

		if (this.getSelectedOrgType() != null) {
			log.info("+ getSelectedOrgType " + getSelectedOrgType());
			applReqDTO.setOrgTypeId(this.getSelectedOrgType());
		}

		// IdentificationNO
		if (pmSearchBean.getIdentificationNo() != null
				&& !"".equalsIgnoreCase(pmSearchBean.getIdentificationNo())) {
			log.info("+ identificationNo " + pmSearchBean.getIdentificationNo());
			applReqDTO.setIdentificationNo(pmSearchBean.getIdentificationNo());
		}
		// User Name
		if (pmSearchBean.getUserName() != null
				&& !"".equalsIgnoreCase(pmSearchBean.getUserName())) {
			log.info("+ userName " + pmSearchBean.getUserName());
			applReqDTO.setUserName(pmSearchBean.getUserName());
		}

		if (pmSearchBean.getCompanyProfile() != null
				&& pmSearchBean.getCompanyProfile()) {
			applReqDTO.setCompanyProfile(1);
		} else if (pmSearchBean.getMarketResearch() != null
				&& pmSearchBean.getMarketResearch()) {
			applReqDTO.setMarketResearch(1);
		}

	}

	/**
	 * get search bean
	 * 
	 * @return ISearchBean<PmAccessAppl>
	 */
	private ISearchBean<Object[]> getSearchBeanData(
			ISearchBean<Object[]> searchBean) {
		log.info("getSearchBeanData - Start");
		boolean isEmpty = true;
		ProfileManagementSearchBean pmSearchBean = this
				.getProfileManagSearchBean();

		if (pmSearchBean.getSelectedOrgProfile() != null) {
			log.info("..pmSearchBean.getSelectedOrgProfile().getOrgProfileId()="
					+ pmSearchBean.getSelectedOrgProfile().getOrgProfileId());
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("orgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),
							pmSearchBean.getSelectedOrgProfile()
									.getOrgProfileId()));
			isEmpty = false;
		}

		if (this.getSelectedOrgType() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("orgTypeCode.parameterId",
							SearchOperator.OPERATOR_EQUALS.value(), this
									.getSelectedOrgType()));
			isEmpty = false;
		}

		// IdentificationNO
		if (pmSearchBean.getIdentificationNo() != null
				&& !"".equalsIgnoreCase(pmSearchBean.getIdentificationNo())) {
			log.info("+ identificationNo " + pmSearchBean.getIdentificationNo());
			SearchFilterBean userSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			userSearchBean.setCaseInsensitive(true);
			userSearchBean.setSearchField("identificationNo");
			userSearchBean.setSearchValue(pmSearchBean.getIdentificationNo());
			searchBean.getDataFilterBeans().add(userSearchBean);
			isEmpty = false;
		}
		// User Name
		if (pmSearchBean.getUserName() != null
				&& !"".equalsIgnoreCase(pmSearchBean.getUserName())) {
			log.info("+ userName " + pmSearchBean.getUserName());
			SearchFilterBean userSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			userSearchBean.setCaseInsensitive(true);
			userSearchBean.setSearchField("applicantName");
			userSearchBean.setSearchValue(pmSearchBean.getUserName());
			searchBean.getDataFilterBeans().add(userSearchBean);
			isEmpty = false;
		}

		if (pmSearchBean.getCompanyProfile() != null
				&& pmSearchBean.getCompanyProfile()) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("companyProfile",
							SearchOperator.OPERATOR_EQUALS.value(), 1));
			isEmpty = false;
		}
		if (pmSearchBean.getMarketResearch() != null
				&& pmSearchBean.getMarketResearch()) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("marketResearch",
							SearchOperator.OPERATOR_EQUALS.value(), 1));
			isEmpty = false;
		}

		log.info("getSearchBeanData - End");
		return searchBean;
	}

	/**
	 * @return the orgTypeMap
	 */
	public Map<String, SelectItem[]> getOrgTypeMap() {
		return orgTypeMap;
	}

	/**
	 * @param orgTypeMap
	 *            the orgTypeMap to set
	 */
	public void setOrgTypeMap(Map<String, SelectItem[]> orgTypeMap) {
		this.orgTypeMap = orgTypeMap;
	}

	/**
	 * @return the selectedAppTypeParamCode
	 */
	public String getSelectedAppTypeParamCode() {
		return selectedAppTypeParamCode;
	}

	/**
	 * @param selectedAppTypeParamCode
	 *            the selectedAppTypeParamCode to set
	 */
	public void setSelectedAppTypeParamCode(String selectedAppTypeParamCode) {
		this.selectedAppTypeParamCode = selectedAppTypeParamCode;
	}

	/**
	 * @return the selectedOrgType
	 */
	public Long getSelectedOrgType() {
		return selectedOrgType;
	}

	/**
	 * @param selectedOrgType
	 *            the selectedOrgType to set
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
	 * @param selectedOrgProfile
	 *            the selectedOrgProfile to set
	 */
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	/**
	 * @return the profileManagSearchBean
	 */
	public ProfileManagementSearchBean getProfileManagSearchBean() {
		if (profileManagSearchBean == null) {
			profileManagSearchBean = new ProfileManagementSearchBean();
		}
		return profileManagSearchBean;
	}

	/**
	 * @param profileManagSearchBean
	 *            the profileManagSearchBean to set
	 */
	public void setProfileManagSearchBean(
			ProfileManagementSearchBean profileManagSearchBean) {
		this.profileManagSearchBean = profileManagSearchBean;
	}

}
