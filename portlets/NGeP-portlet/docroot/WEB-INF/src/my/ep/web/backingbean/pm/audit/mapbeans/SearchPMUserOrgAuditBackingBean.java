package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.VAuditPMDTO;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.profilemanagementsearch.ProfileManagementSearchBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.AuditPMUserOrgSearchBean;
import my.ep.web.managedbean.pm.audit.AuditPMUserOrgManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * @author normuliana
 * 
 */
@ManagedBean(name = "searchPMUserOrgAuditBackingBean")
@ViewScoped
public class SearchPMUserOrgAuditBackingBean extends BaseBackingBean<AuditPMUserOrgManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SearchPMUserOrgAuditBackingBean.class);
	private IPagingDataModel<VAuditPMDTO> searchMaster = new SearchMasterPagingDataModel();
	private IPagingDataModel<VAuditPMDTO> searchDetail = new SearchDetailPagingDataModel();
	// Navigation Details
	public static final String NAV_TO_SEARCH = "userOrgAuditMain";
	public static final String NAV_TO_MAINTAIN = "userOrgAuditDetailMain";
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	// Mapper for DDL object
	private static Map<String, Object> identityTypeMapper;
	private static Map<Long, Object> orgTypeMapper;

	// Org Category values
	public static final int FEDERAL_GOVT = 0;
	public static final int OTHERS = 1;

	public ProfileManagementSearchBackingBean profileManagementSearchBackingBean;

	public SearchPMUserOrgAuditBackingBean() {

	}

	public String getSelectionRoleCode() {
		return getManagedBean().getSearchBean().getRoleCode();
	}

	public String roleCode;

	public void preLoading() {
		this.resetOrganisationSearchBean();
		log.info("preLoading User Org");
		getManagedBean().setLazy(false);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	/* -- ORG PROFILE SERACH-- */

	/**
	 * This method used for cater search action.
	 * 
	 **/
	public void searchMaster() {
		System.out.println("getDropdownOrgUserVal :::: >>> : " + getManagedBean().getSearchBean().getMasterLabel2());
		if (getManagedBean().getSearchBean().getMasterLabel2() != null) {
			getManagedBean().setLazy(true);
			getManagedBean().setNotFirstRun(true);
			this.getManagedBean().getSearchBean().setSearching(true);
			getManagedBean().getSearchBean().setMasterLabel1("ORGUAD");
			searchMaster.reloadData();
		}
	}

	public void searchDetail() {
		this.getManagedBean().setLazy(true);
		getManagedBean().getSearchBean().setMasterLabel1("ORGUAD");
		searchDetail.reloadData();
	}

	public String backToSearch() {
		getManagedBean().setSelectedType(null);
		getManagedBean().getSearchBean().setSearching(false);
		return NAV_TO_SEARCH;
	}

	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();

	public String redirectToDetails(String paramLevel3, BigDecimal pmParameterLvlId, BigDecimal createBy, String roleCode,
			BigDecimal docGroupId) throws Exception {
		getManagedBean().setParamLevel3(paramLevel3);
		getManagedBean().getSearchBean().setPmParameterLvlId(pmParameterLvlId);
		log.info("pmParameterLvlId.intValue() :::::  " + pmParameterLvlId.intValue());
		getManagedBean().getSearchBean().setRoleCode(roleCode);
		getManagedBean().getSearchBean().setCreatedBy(createBy.toString());
		getManagedBean().getSearchBean().setMasterLabel1("ORGUAD");
		getManagedBean().getSearchBean().setpDocGroupId(docGroupId);

		return NAV_TO_MAINTAIN;
	}

	public boolean searching(boolean search) throws Exception {
		getManagedBean().getSearchBean().setSearching(search);
		return getManagedBean().getSearchBean().isSearching();
	}

	/**
	 * search Master Location
	 * 
	 */
	private class SearchMasterPagingDataModel extends PagingDataModel<VAuditPMDTO> {

		private static final long serialVersionUID = 1L;

		public SearchMasterPagingDataModel() {

			super("searchMasters", getManagedBean().isLazy());

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<VAuditPMDTO> pageChangeListener(ISearchBean<VAuditPMDTO> searchBean) {
			IAuditPMSO auditPMSO;
			try {

				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				auditPMReqDTO.setpLanguage(languageCode);
				AuditPMUserOrgSearchBean lSearchBean = getManagedBean().getSearchBean();
				lSearchBean.setMasterLabel1("ORGUAD");
				if (lSearchBean.isSearching()) {
					auditPMReqDTO.setpLvl1Code(lSearchBean.getMasterLabel1());
					auditPMReqDTO.setpLvl2Code(lSearchBean.getMasterLabel2());
					auditPMReqDTO.setpLvl3Code(lSearchBean.getMasterLabel3());
					auditPMReqDTO.setpLvl4(lSearchBean.getMasterLabel4()); // setpItemCode
					auditPMReqDTO.setpLvl5(lSearchBean.getMasterLabel5()); // setpItemName
					auditPMReqDTO.setpLvl6(lSearchBean.getMasterLabel6());
					auditPMReqDTO.setpLvl7(lSearchBean.getMasterLabel7());
					auditPMReqDTO.setpRoleCode(lSearchBean.getRoleCode());
					auditPMReqDTO.setPdateFrom(lSearchBean.getDateFrom());
					auditPMReqDTO.setPdateTo(lSearchBean.getDateTo());

					lSearchBean.setSystemConfigParamLevel1OldVal(lSearchBean.getMasterLabel1());
					lSearchBean.setCategoryConfigParamLevel2OldVal(lSearchBean.getMasterLabel2());
					lSearchBean.setItemConfigParamLevel3OldVal(lSearchBean.getMasterLabel3());
					lSearchBean.setpLvl1OldVal(lSearchBean.getMasterLabel1());
					lSearchBean.setpLvl2OldVal(lSearchBean.getMasterLabel2());
					lSearchBean.setpLvl3OldVal(lSearchBean.getMasterLabel3());
					lSearchBean.setpLvl4OldVal(lSearchBean.getMasterLabel4());
					lSearchBean.setpLvl5OldVal(lSearchBean.getMasterLabel5());
					lSearchBean.setpLvl6OldVal(lSearchBean.getMasterLabel6());
					lSearchBean.setpLvl7OldVal(lSearchBean.getMasterLabel7());

					lSearchBean.setRoleCodeOldVal(lSearchBean.getRoleCode());
					lSearchBean.setDateFromOldVal(lSearchBean.getDateFrom());
					lSearchBean.setDateToOldVal(lSearchBean.getDateTo());
				} else if (!lSearchBean.isSearching()) {
					auditPMReqDTO.setpLvl1Code(lSearchBean.getSystemConfigParamLevel1OldVal());
					auditPMReqDTO.setpLvl2Code(lSearchBean.getCategoryConfigParamLevel2OldVal());
					auditPMReqDTO.setpLvl3Code(lSearchBean.getItemConfigParamLevel3OldVal());
					auditPMReqDTO.setpItemCode(lSearchBean.getMasterLabel4());
					auditPMReqDTO.setpItemName(lSearchBean.getMasterLabel5());

					auditPMReqDTO.setpLvl1Code(lSearchBean.getpLvl1OldVal());
					auditPMReqDTO.setpLvl2Code(lSearchBean.getpLvl2OldVal());
					auditPMReqDTO.setpLvl3Code(lSearchBean.getpLvl3OldVal());
					auditPMReqDTO.setpLvl4(lSearchBean.getpLvl4OldVal());
					auditPMReqDTO.setpLvl5(lSearchBean.getpLvl5OldVal());
					auditPMReqDTO.setpLvl6(lSearchBean.getpLvl6OldVal());
					auditPMReqDTO.setpLvl7(lSearchBean.getpLvl7OldVal());

					auditPMReqDTO.setPdateFrom(lSearchBean.getDateFromOldVal());
					auditPMReqDTO.setPdateTo(lSearchBean.getDateToOldVal());
				}
				if (lSearchBean.getUserId() != null && lSearchBean.isSearching()) {
					auditPMReqDTO.setCreatedBy(lSearchBean.getUserId().toString());
					lSearchBean.setUserIdOldVal(lSearchBean.getUserId());
				} else if (lSearchBean.getUserId() != null && !lSearchBean.isSearching()) {
					auditPMReqDTO.setCreatedBy(lSearchBean.getUserIdOldVal().toString());
				}

				auditPMReqDTO.setCategoryConfigParamLevel2(getManagedBean().getSearchBean().getMasterLabel2());
				auditPMReqDTO.setPmParameterLvlId(lSearchBean.getPmParameterLvlId());
				auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);

				ISearchResultBean<VAuditPMDTO> resDTO = null;
				System.out.println("LEVEL2 MASTER ::::::: " + getManagedBean().getSearchBean().getMasterLabel2());
				resDTO = auditPMSO.userOrgSearchPMMasterResultList(auditPMReqDTO, searchBean);
				return resDTO;

			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, "SearchMasterPagingDataModel", "resDTO",
						"searchResult", Long.parseLong(getManagedBean().getSearchBean().getCreatedBy()), e);
			}
			return null;
		}
	}

	/**
	 * search Details Location
	 * 
	 */
	private class SearchDetailPagingDataModel extends PagingDataModel<VAuditPMDTO> {

		private static final long serialVersionUID = 1L;

		public SearchDetailPagingDataModel() {

			super("searchDetails", getManagedBean().isLazy());

		}

		public ISearchResultBean<VAuditPMDTO> pageChangeListener(ISearchBean<VAuditPMDTO> searchBean) {
			IAuditPMSO auditPMSO;

			try {
				AuditPMUserOrgSearchBean lSearchBean = getManagedBean().getSearchBean();
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				auditPMReqDTO.setpLanguage(languageCode);
				if (lSearchBean.getPmParameterLvlId() != null) {
					auditPMReqDTO.setPmParameterLvlId(lSearchBean.getPmParameterLvlId());
					lSearchBean.setPmParameterLvlIdOldval(lSearchBean.getPmParameterLvlId());
				}
				if (lSearchBean.getRoleCode() != null) {
					auditPMReqDTO.setpRoleCode(lSearchBean.getRoleCode());
					lSearchBean.setRoleCodeOldVal(lSearchBean.getRoleCode());
				}
				if (lSearchBean.getCreatedBy() != null) {
					auditPMReqDTO.setCreatedBy(lSearchBean.getCreatedBy());
					lSearchBean.setCreatedByOldVal(lSearchBean.getCreatedBy());
				}
				if (lSearchBean.isSearching()) {
					auditPMReqDTO.setPdateFrom(lSearchBean.getDateFrom());
					auditPMReqDTO.setPdateTo(lSearchBean.getDateTo());

					lSearchBean.setPdateFromOldVal(lSearchBean.getDateFrom());
					lSearchBean.setPdateToOldVal(lSearchBean.getDateTo());
				}
				if (!lSearchBean.isSearching()) {
					auditPMReqDTO.setPdateFrom(lSearchBean.getPdateFromOldVal());
					auditPMReqDTO.setPdateTo(lSearchBean.getPdateToOldVal());
				}
				auditPMReqDTO.setpDocGroupId(lSearchBean.getpDocGroupId());
				auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);

				ISearchResultBean<VAuditPMDTO> resDTO;

				auditPMReqDTO.setCategoryConfigParamLevel2(getManagedBean().getSearchBean().getMasterLabel2());
				System.out.println("LEVEL2 DETAIL ::::::: " + getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
				resDTO = auditPMSO.searchDetailResultList(auditPMReqDTO, searchBean);
				return resDTO;

			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, "SearchDetailPagingDataModel", "resDTO",
						"searchResult", Long.parseLong(getManagedBean().getSearchBean().getCreatedBy()), e);
			}
			return null;
		}
	}

	public void autoSelectOrgType() {
		if (!getManagedBean().isNotFirstRun()) {
			log.info("updateAuto");
			RequestContext.getCurrentInstance().execute("autoSelectOrgType();");
			getManagedBean().setNotFirstRun(true);
		}
	}

	public void updateSearchCriteriaForOrgName(BaseEntity entity) {
		log.info("updateSearchCriteriaForOrgName");
		PmOrgValidity orgProfile = (PmOrgValidity) entity;
		this.getManagedBean().getPmOrgValidity().setOrgValidityId(orgProfile.getOrgValidityId());
	}

	/**
	 * Retrieve Dropdown MainOrgUser : Penyelenggaraan Profil
	 * 1.Organisasi/2.Penyelenggaraan Organisasi Kewangan/3.Pengguna/4.Kumpulan
	 * Pengguna
	 * 
	 */
	public List<VAuditPMDTO> getDropdownMainOrgUser() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
		List<VAuditPMDTO> getDropdownMainOrgUserList = auditPMSO.getDropdownMainOrgUser(languageCode);
		return getDropdownMainOrgUserList;
	}

	/**
	 * Retrieve Dropdown getOrgTypeFederalGovernment
	 * 
	 */
	public List<VAuditPMDTO> getOrgTypeFederalGovernment() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = (IAuditPMSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);
		List<VAuditPMDTO> getOrgTypeFederalGovernmentList = auditPMSO.getOrgTypeFederalGovernment(languageCode, getManagedBean()
				.getSearchBean().getMasterLabel2());
		return getOrgTypeFederalGovernmentList;
	}

	/**
	 * Retrieve Dropdown getOrgTypeFederalGovernment
	 * 
	 */
	public List<VAuditPMDTO> getGroupRoleDropdownValues() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = (IAuditPMSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);
		List<VAuditPMDTO> getOrgTypeFederalGovernmentList = auditPMSO.getGroupRoleDropdownValues(languageCode);
		return getOrgTypeFederalGovernmentList;
	}

	/**
	 * Retrieve Dropdown Level3
	 * 
	 */
	public List<VAuditPMDTO> getOrgTypeOther() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = (IAuditPMSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);
		log.info("getMasterLabel2() for orgTypeOther :::::: " + getManagedBean().getSearchBean().getMasterLabel2());
		List<VAuditPMDTO> getOrgTypeOtherList = auditPMSO.getOrgTypeOther(languageCode, getManagedBean().getSearchBean()
				.getMasterLabel2());
		return getOrgTypeOtherList;
	}

	public List<VAuditPMDTO> getDropdownRoleOrgUser() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = (IAuditPMSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);
		List<VAuditPMDTO> getDropdownRoleOrgUserList = auditPMSO.getDropdownRoleOrgUser(languageCode);

		return getDropdownRoleOrgUserList;
	}

	/**
	 * Retrieve Dropdown Role List
	 * 
	 */
	public List<VAuditPMDTO> getDropdownRoleList() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = (IAuditPMSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);
		List<VAuditPMDTO> dropdownRoleList = auditPMSO.getDropdownRoleList(languageCode);

		return dropdownRoleList;
	}

	public List<VAuditPMDTO> getOrgTypeDropdownValues() {
		List<VAuditPMDTO> dropdownRoleList = null;
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = (IAuditPMSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);

		if (Integer.parseInt(getManagedBean().getSearchBean().getOrgCategory()) == FEDERAL_GOVT) {
			dropdownRoleList = auditPMSO.getOrgTypeFederalGovernment(languageCode, getManagedBean().getSearchBean()
					.getMasterLabel2());
		} else if (Integer.parseInt(getManagedBean().getSearchBean().getOrgCategory()) == OTHERS) {
			dropdownRoleList = auditPMSO.getOrgTypeOther(languageCode, getManagedBean().getSearchBean().getMasterLabel2());
		}
		log.info("Parameter ID" + dropdownRoleList.get(0).getLevelId());
		return dropdownRoleList;
	}

	public List<VAuditPMDTO> getServiceTypeDropdownValues() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = (IAuditPMSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);

		List<VAuditPMDTO> dropdownServiceTypeList = auditPMSO.getServiceTypeDropdownValues(languageCode);
		return dropdownServiceTypeList;
	}

	public void changeType() {
		log.info("changeLocationType!!!");
		getManagedBean().getSearchBean().setDateTo(null);
		getManagedBean().getSearchBean().setDateFrom(null);
		getManagedBean().getSearchBean().setRoleCode(null);
	}

	public void search() {
		log.info("changeLocationType!!!");
		searchMaster = new SearchMasterPagingDataModel();
	}

	public void resetGetActionBy() {
		Long userId = getManagedBean().getSearchBean().getPmUser().getUserId();
		getManagedBean().getSearchBean().setUserId(new BigDecimal(userId));
	}

	public void resetGetOrgType() {
		Long orgTypeId = getManagedBean().getSearchBean().getPmOrgProfile().getOrgProfileId();
		getManagedBean().getSearchBean().setMasterLabel4(orgTypeId.toString());
	}

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void changeOrgType() {
		log.info("changeResOrgType");

		this.getManagedBean().setOrgTypeParamId(getManagedBean().getSearchBean().getOrgTypeParamId().toString());

		List<VAuditPMDTO> orgTypes = getOrgTypeDropdownValues();
		if (orgTypes != null && !orgTypes.isEmpty()) {
			for (VAuditPMDTO orgTypeObj : orgTypes) {
				if (orgTypeObj.getLevelId().toString().equalsIgnoreCase(getManagedBean().getSearchBean().getOrgTypeParamId())) {
					getManagedBean().getSearchBean().setMasterLabel3(orgTypeObj.getMasterLabel3());
				}
			}

		}

		if (this.getManagedBean().getSelectedOrgProfile() != null) {
			getManagedBean().setSelectedOrgProfile(null);
		}
	}

	public void changeOrgUnit(BaseEntity entity) {
		log.info("changeOrgUnit");
	}

	/**
	 * The method used for generating a list of available display type of status
	 * as selection.
	 * 
	 * @return the selection list of display type
	 */
	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getRecordStatusOptions();
	}

	/**
	 * reset search bean fields
	 */
	public void resetOrganisationSearchBean() {
		log.info("resetOrgSearchBean");
		getManagedBean().setLazy(false);
		AuditPMUserOrgSearchBean searchBean = new AuditPMUserOrgSearchBean();
		getManagedBean().setSearchBean(searchBean);
		getManagedBean().setNotFirstRun(false);
		getManagedBean().setSearchOrgCategory(FEDERAL_GOVT);
		searchMaster = new SearchMasterPagingDataModel();
	}

	public IPagingDataModel<VAuditPMDTO> getSearchMaster() {
		return searchMaster;
	}

	public void setSearchMaster(IPagingDataModel<VAuditPMDTO> searchMaster) {
		this.searchMaster = searchMaster;
	}

	public void resetLocationDetailSearchBean() {
		log.info("resetLocationDetailSearchBean");
		getManagedBean().setLazy(false);
		AuditPMUserOrgSearchBean searchBean = new AuditPMUserOrgSearchBean();
		this.getManagedBean().setSearchBean(searchBean);
		searchDetail = new SearchDetailPagingDataModel();
		getManagedBean().setSelectedType(null);
	}

	public IPagingDataModel<VAuditPMDTO> getSearchDetail() {
		return searchDetail;
	}

	public void setSearchDetail(IPagingDataModel<VAuditPMDTO> searchDetail) {
		this.searchDetail = searchDetail;
	}

	public String getRoleCode() {
		if (getManagedBean().getSearchBean().getRoleCode() != null) {
			return roleCode = getManagedBean().getSearchBean().getRoleCode();
		}
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	/**
	 * @return the federalGovt
	 */
	public int getFederalGovt() {
		return FEDERAL_GOVT;
	}

	/**
	 * @return the others
	 */
	public int getOthers() {
		return OTHERS;
	}

	public ProfileManagementSearchBackingBean getProfileManagementSearchBackingBean() {
		return profileManagementSearchBackingBean;
	}

	public void setProfileManagementSearchBackingBean(ProfileManagementSearchBackingBean profileManagementSearchBackingBean) {
		this.profileManagementSearchBackingBean = profileManagementSearchBackingBean;
	}
}
