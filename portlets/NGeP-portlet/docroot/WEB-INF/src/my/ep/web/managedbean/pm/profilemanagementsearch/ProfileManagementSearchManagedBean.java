package my.ep.web.managedbean.pm.profilemanagementsearch;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.access.application.IAccessApplSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.dto.pm.access.application.AccessApplResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAccessAppl;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ProfileManagementSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.profilemanagement.ProfileManagementManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
	
	/**
	 * get search bean
	 * 
	 * @return ISearchBean<PmAccessAppl>
	 */
	public void fillRequestDTO(AccessApplReqDTO applReqDTO) {
		log.info("fillRequestDTO - Start");
		ProfileManagementSearchBean pmSearchBean = this.getProfileManagSearchBean();

		if (pmSearchBean.getSelectedOrgProfile() != null) {
			log.info("..pmSearchBean.getSelectedOrgProfile().getOrgProfileId()="
					+ pmSearchBean.getSelectedOrgProfile().getOrgProfileId());
			applReqDTO.setOrgProfileId(pmSearchBean.getSelectedOrgProfile().getOrgProfileId());	
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
		}else if (pmSearchBean.getMarketResearch() != null
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

	private ProfileManagementSearchBean profileManagSearchBean;

	public String getSelectedAppTypeParamCode() {
		return selectedAppTypeParamCode;
	}

	public void setSelectedAppTypeParamCode(String selectedAppTypeParamCode) {
		this.selectedAppTypeParamCode = selectedAppTypeParamCode;
	}

	public ProfileManagementSearchBean getProfileManagSearchBean() {
		if (profileManagSearchBean == null) {
			profileManagSearchBean = new ProfileManagementSearchBean();
		}
		return profileManagSearchBean;
	}

	public void setProfileManagSearchBean(
			ProfileManagementSearchBean profileManagSearchBean) {
		this.profileManagSearchBean = profileManagSearchBean;
	}

	public Long getSelectedOrgType() {
		return selectedOrgType;
	}

	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	public void setSelectedOrgType(Long selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	public Map<String, SelectItem[]> getOrgTypeMap() {
		return orgTypeMap;
	}

	public void setOrgTypeMap(Map<String, SelectItem[]> orgTypeMap) {
		this.orgTypeMap = orgTypeMap;
	}

}
