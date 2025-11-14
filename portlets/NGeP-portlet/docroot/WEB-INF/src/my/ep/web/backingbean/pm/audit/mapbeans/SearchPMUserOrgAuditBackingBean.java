package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.VAuditPMDTO;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.profilemanagementsearch.ProfileManagementSearchBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.AuditPMUserOrgSearchBean;
import my.ep.web.managedbean.pm.audit.AuditPMUserOrgManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
	private transient IPagingDataModel<VAuditPMDTO> searchMaster = new SearchMasterPagingDataModel();
	private transient IPagingDataModel<VAuditPMDTO> searchDetail = new SearchDetailPagingDataModel();
	// Navigation Details
	public static final String NAV_TO_SEARCH = "userOrgAuditMain";
	public static final String NAV_TO_MAINTAIN = "userOrgAuditDetailMain";
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	// Org Category values
	public static final int FEDERAL_GOVT = 0;
	public static final int OTHERS = 1;

	private String roleCode;

	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();

	private ProfileManagementSearchBackingBean profileManagementSearchBackingBean;

	public SearchPMUserOrgAuditBackingBean() {
		/*
		 * This constructor is intentionally empty. Initialization is handled
		 * via dependency injection or setter methods.
		 */
	}

	/**
	 * init()
	 */
	@PostConstruct
	public void init() {
		if (getManagedBean().getSearchBean().getMasterLabel2() == null) {
			getManagedBean().getSearchBean().setMasterLabel2("OPM");
			/* or leave null if you want no default */
		}
	}

	public String getSelectionRoleCode() {
		return getManagedBean().getSearchBean().getRoleCode();
	}

	/**
	 * preLoading()
	 */
	public void preLoading() {
		this.getManagedBean().setSearchOrgCategory(FEDERAL_GOVT);
		log.info("preLoading User Org");
		getManagedBean().setLazy(false);
		this.resetSearchBean();
		getManagedBean().setResetMasterResult(false);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	/* -- ORG PROFILE SERACH-- */

	/**
	 * This method used for cater search action.
	 * 
	 **/
	public void searchMaster() {
		log.info("getDropdownOrgUserVal :::: >>> : " + getManagedBean().getSearchBean().getMasterLabel2());
		if (getManagedBean().getSearchBean().getMasterLabel2() != null) {
			getManagedBean().setLazy(true);
			getManagedBean().setNotFirstRun(true);
			this.getManagedBean().getSearchBean().setSearching(true);
			getManagedBean().getSearchBean().setMasterLabel1(AuditConst.PM_AUD_ORGUAD);

			searchMaster.reloadData();
		}
		getManagedBean().setResetMasterResult(false);
	}

	/**
	 * searchDetail()
	 */
	public void searchDetail() {
		this.getManagedBean().setLazy(true);
		getManagedBean().getSearchBean().setMasterLabel1(AuditConst.PM_AUD_ORGUAD);
		searchDetail.reloadData();
	}

	/**
	 * @return
	 */
	public String backToSearch() {
		getManagedBean().setSelectedType(null);
		getManagedBean().getSearchBean().setSearching(false);
		return NAV_TO_SEARCH;
	}

	/**
	 * @param paramLevel3
	 * @param pmParameterLvlId
	 * @param createBy
	 * @param roleCode
	 * @param docGroupId
	 * @param paramLevel6
	 * @return
	 * @throws AuditPMException
	 */
	public String redirectToDetails(String paramLevel3, BigDecimal pmParameterLvlId, BigDecimal createBy, String roleCode,
			BigDecimal docGroupId, String paramLevel6, VAuditPMDTO dto) throws AuditPMException {
		if (paramLevel3 == null || pmParameterLvlId == null || roleCode == null) {
			throw new AuditPMException("Missing required parameters for redirectToDetails SecondUI Audit PM. : "
					+ " <paramLevel3>:" + paramLevel3 + " <pmParameterLvlId>:" + pmParameterLvlId + "<roleCode>:" + roleCode);
		}
		getManagedBean().setParamLevel3(paramLevel3);
		getManagedBean().getSearchBean().setPmParameterLvlId(pmParameterLvlId);
		log.info("pmParameterLvlId.intValue() :::::  " + pmParameterLvlId.intValue());
		getManagedBean().getSearchBean().setCreatedBy(createBy.toString());
		getManagedBean().getSearchBean().setMasterLabel1(AuditConst.PM_AUD_ORGUAD);
		getManagedBean().getSearchBean().setpDocGroupId(docGroupId);
		if (getManagedBean().getSearchBean().getMasterLabel2().equalsIgnoreCase(AuditConst.PM_LVL_CODE_2_OUSR)) {
			if (null == paramLevel6 || paramLevel6.isEmpty() || paramLevel6 == "".trim()) {
				log.info("paramLevel6 :::::  " + paramLevel6);
				getManagedBean().getSearchBean().setpUserOrgProfileId(null);
			} else {
				log.info("paramLevel6 value :::::  " + paramLevel6);
				getManagedBean().getSearchBean().setpUserOrgProfileId(new BigDecimal(paramLevel6));
			}
		}

		getManagedBean().getSearchBean().setRoleCode(dto.getRoleCode());
		getManagedBean().getSearchBean().setCreatedBy(dto.getCreateBy().toString());

		return NAV_TO_MAINTAIN;
	}

	/**
	 * search Master UserOrg
	 * 
	 */
	private class SearchMasterPagingDataModel extends PagingDataModel<VAuditPMDTO> {

		private static final long serialVersionUID = 1L;

		public SearchMasterPagingDataModel() {

			super("searchMasters", getManagedBean().isLazy());

		}

		/*
		 * 
		 * (non-Javadoc) ISearchResultBean<VAuditPMDTO>
		 * pageChangeListener(ISearchBean<VAuditPMDTO> searchBean)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<VAuditPMDTO> pageChangeListener(ISearchBean<VAuditPMDTO> searchBean) {
			IAuditPMSO auditPMSO;
			try {

				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				auditPMReqDTO.setpLanguage(languageCode);
				AuditPMUserOrgSearchBean lSearchBean = getManagedBean().getSearchBean();
				lSearchBean.setMasterLabel1(AuditConst.PM_AUD_ORGUAD);
				if (lSearchBean.isSearching()) {
					auditPMReqDTO.setpLvl1Code(lSearchBean.getMasterLabel1());
					auditPMReqDTO.setpLvl2Code(lSearchBean.getMasterLabel2());
					auditPMReqDTO.setpLvl3Code(lSearchBean.getMasterLabel3());
					auditPMReqDTO.setpLvl4(lSearchBean.getMasterLabel4());
					auditPMReqDTO.setpLvl5(lSearchBean.getMasterLabel5());
					auditPMReqDTO.setpLvl6(lSearchBean.getMasterLabel6());
					auditPMReqDTO.setpLvl7(lSearchBean.getMasterLabel7());
					auditPMReqDTO.setpLvl8(lSearchBean.getMasterLabel8());
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
					lSearchBean.setpLvl8OldVal(lSearchBean.getMasterLabel8());

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
					auditPMReqDTO.setpLvl8(lSearchBean.getpLvl8OldVal());

					auditPMReqDTO.setpRoleCode(lSearchBean.getRoleCodeOldVal());
					auditPMReqDTO.setPdateFrom(lSearchBean.getDateFromOldVal());
					auditPMReqDTO.setPdateTo(lSearchBean.getDateToOldVal());
				}
				if (lSearchBean.getUserId() != null && lSearchBean.isSearching()) {
					auditPMReqDTO.setCreatedBy(lSearchBean.getUserId().toString());
					lSearchBean.setUserIdOldVal(lSearchBean.getUserId());
				} else if (lSearchBean.getUserId() != null && !lSearchBean.isSearching()) {
					auditPMReqDTO.setCreatedBy(lSearchBean.getCreatedByOldVal());
				}

				auditPMReqDTO.setCategoryConfigParamLevel2(getManagedBean().getSearchBean().getMasterLabel2());
				auditPMReqDTO.setPmParameterLvlId(lSearchBean.getPmParameterLvlId());
				auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);

				log.info("LEVEL2 MASTER ::::::: " + getManagedBean().getSearchBean().getMasterLabel2());

				if (getManagedBean().isResetMasterResult()) {
					getManagedBean().setResetMasterResult(false);
					return null;
				} else {
					getManagedBean().setResetMasterResult(false);
					return auditPMSO.userOrgSearchPMMasterResultList(auditPMReqDTO, searchBean);
				}
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, "SearchMasterPagingDataModel", "resDTO",
						"searchResult", Long.parseLong(getManagedBean().getSearchBean().getCreatedBy()), e);
			}
			getManagedBean().setResetMasterResult(false);
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

		/*
		 * ISearchResultBean<VAuditPMDTO>
		 * pageChangeListener(ISearchBean<VAuditPMDTO> searchBean) (non-Javadoc)
		 * pageChangeListener
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
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
				if (null != lSearchBean.getpUserOrgProfileId()) {
					auditPMReqDTO.setpUserOrgProfileId(lSearchBean.getpUserOrgProfileId());
				}

				auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);

				ISearchResultBean<VAuditPMDTO> resDTO;

				auditPMReqDTO.setCategoryConfigParamLevel2(getManagedBean().getSearchBean().getMasterLabel2());
				log.info("LEVEL2 DETAIL ::::::: " + getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
				resDTO = auditPMSO.searchDetailResultList(auditPMReqDTO, searchBean);
				return resDTO;

			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, "SearchDetailPagingDataModel", "resDTO",
						"searchResult", Long.parseLong(getManagedBean().getSearchBean().getCreatedBy()), e);
			}
			return null;
		}
	}

	/**
	 * autoSelectOrgType()
	 */
	public void autoSelectOrgType() {
		log.info("autoSelectOrgType triggered");
		log.info("masterLabel2 in AutoUpdate :::" + getManagedBean().getSearchBean().getMasterLabel2());
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
		return auditPMSO.getDropdownMainOrgUser(languageCode);
	}

	/**
	 * Retrieve Dropdown getOrgTypeFederalGovernment
	 * 
	 */
	public List<VAuditPMDTO> getOrgTypeFederalGovernment() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
		return auditPMSO.getOrgTypeFederalGovernment(languageCode, getManagedBean().getSearchBean().getMasterLabel2());
	}

	/**
	 * Retrieve Dropdown getOrgTypeFederalGovernment
	 * 
	 */
	public List<VAuditPMDTO> getGroupRoleDropdownValues() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
		return auditPMSO.getGroupRoleDropdownValues(languageCode);
	}

	/**
	 * Retrieve Dropdown Level3
	 * 
	 */
	public List<VAuditPMDTO> getOrgTypeOther() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
		log.info("getMasterLabel2() for orgTypeOther :::::: " + getManagedBean().getSearchBean().getMasterLabel2());
		return auditPMSO.getOrgTypeOther(languageCode, getManagedBean().getSearchBean().getMasterLabel2());
	}

	/**
	 * Retrieve Dropdown Role List
	 * 
	 */
	public List<VAuditPMDTO> getDropdownRoleList() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
		return auditPMSO.getDropdownRoleList(languageCode, getManagedBean().getSearchBean().getMasterLabel2());
	}

	public List<VAuditPMDTO> getOrgTypeDropdownValues() {
		List<VAuditPMDTO> dropdownRoleList = null;
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);

		if (Integer.parseInt(getManagedBean().getSearchBean().getOrgCategory()) == FEDERAL_GOVT) {
			log.info("FEDERAL BackingBean getMasterLabel2 ::: " + getManagedBean().getSearchBean().getMasterLabel2());
			dropdownRoleList = auditPMSO.getOrgTypeFederalGovernment(languageCode, getManagedBean().getSearchBean()
					.getMasterLabel2());
		} else if (Integer.parseInt(getManagedBean().getSearchBean().getOrgCategory()) == OTHERS) {
			log.info("OTHER BackingBean getMasterLabel2 ::: " + getManagedBean().getSearchBean().getMasterLabel2());
			dropdownRoleList = auditPMSO.getOrgTypeOther(languageCode, getManagedBean().getSearchBean().getMasterLabel2());
		}

		return dropdownRoleList;
	}

	public List<VAuditPMDTO> getServiceTypeDropdownValues() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);

		return auditPMSO.getServiceTypeDropdownValues(languageCode);
	}

	/**
	 * changeType()
	 */
	public void changeType() {
		log.info("changeLocationType!!!");
		getManagedBean().getSearchBean().setDateTo(null);
		getManagedBean().getSearchBean().setDateFrom(null);
		getManagedBean().getSearchBean().setRoleCode(null);
		getManagedBean().getSearchBean().setMasterLabel3(null);
		getManagedBean().getSearchBean().setMasterLabel4(null);
		getManagedBean().getSearchBean().setMasterLabel5(null);
		getManagedBean().getSearchBean().setMasterLabel6(null);
		getManagedBean().getSearchBean().setOrgTypeParamId(null);
		getManagedBean().getSearchBean().setPmUser(null);
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().getSearchBean().setPmOrgValidity(null);
	}

	/**
	 * changeServiceType()
	 */
	public void changeServiceType() {
		log.info("changeServiceType!!!");
		getManagedBean().getSearchBean().setDateTo(null);
		getManagedBean().getSearchBean().setDateFrom(null);
		getManagedBean().getSearchBean().setRoleCode(null);
		getManagedBean().getSearchBean().setMasterLabel4(null);
		getManagedBean().getSearchBean().setMasterLabel5(null);
		getManagedBean().getSearchBean().setOrgTypeParamId(null);
		getManagedBean().getSearchBean().setPmUser(null);
	}

	/**
	 * changeGroupRole()
	 */
	public void changeGroupRole() {
		log.info("changeGroupRole!!!");
		getManagedBean().getSearchBean().setDateTo(null);
		getManagedBean().getSearchBean().setDateFrom(null);
		getManagedBean().getSearchBean().setRoleCode(null);
		getManagedBean().getSearchBean().setMasterLabel4(null);
		getManagedBean().getSearchBean().setMasterLabel5(null);
		getManagedBean().getSearchBean().setMasterLabel6(null);
		getManagedBean().getSearchBean().setMasterLabel7(null);
		getManagedBean().getSearchBean().setMasterLabel8(null);
		getManagedBean().getSearchBean().setOrgTypeParamId(null);
		getManagedBean().getSearchBean().setPmUser(null);
	}

	/**
	 * resetGetActionBy()
	 */
	public void resetGetActionBy() {
		Long userId = getManagedBean().getSearchBean().getPmUser().getUserId();
		getManagedBean().getSearchBean().setUserId(new BigDecimal(userId));
	}

	/**
	 * resetGetOrgName()
	 */
	public void resetGetOrgName() {
		log.info("getOrgValidityId() :::: " + getManagedBean().getSearchBean().getPmOrgValidity().getOrgValidityId());
		getManagedBean().getSearchBean().setMasterLabel4(
				getManagedBean().getSearchBean().getPmOrgValidity().getPmOrgProfile().getOrgProfileId().toString());
		/**
		 * getManagedBean().getSearchBean().getPmOrgValidity().setOrgProfileId(
		 * new Long(orgValidityId));
		 */
	}

	/**
	 * resetGetOrgType()
	 */
	public void resetGetOrgType() {
		Long orgTypeId = getManagedBean().getSearchBean().getPmOrgProfile().getOrgProfileId();
		getManagedBean().getSearchBean().setMasterLabel4(orgTypeId.toString());
		getManagedBean().getSearchBean().setOrgValidityId(getManagedBean().getSearchBean().getPmOrgValidity().getOrgValidityId());
	}

	/**
	 * @param orgProfile
	 */
	public void selectPtjAction(PmOrgProfile orgProfile) {
		this.getManagedBean().getSearchBean().setPmOrgValidity(orgProfile.getPmOrgValidity());
		this.getManagedBean().getUserProfileSearchBean().setSelectedOrgProfileId(orgProfile.getOrgProfileId());
		this.getManagedBean().getSearchBean().setMasterLabel4(orgProfile.getOrgProfileId().toString());
		log.info("getMasterLabel4 PTJ/KUMPTJ ::: " + this.getManagedBean().getSearchBean().getMasterLabel4());
	}

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/**
	 * changeOrgType()
	 */
	public void changeOrgType() {
		log.info("changeResOrgType");

		this.getManagedBean().setOrgTypeParamId(getManagedBean().getSearchBean().getOrgTypeParamId());

		List<VAuditPMDTO> orgTypes = getOrgTypeDropdownValues();
		if (orgTypes != null && !orgTypes.isEmpty()) {
			for (VAuditPMDTO orgTypeObj : orgTypes) {
				if (orgTypeObj.getLevelId().toString().equalsIgnoreCase(getManagedBean().getSearchBean().getOrgTypeParamId())) {
					getManagedBean().getSearchBean().setMasterLabel3(orgTypeObj.getMasterLabel3());
					getManagedBean().getSelectedOrgType().setParameterCode(orgTypeObj.getMasterLabel3());
					getManagedBean().getSelectedOrgType().setParameterId(orgTypeObj.getLevelId().longValue());
					getManagedBean().getCurrentActiveOrg().setOrgProfileId(orgTypeObj.getLevelId().longValue());
					isDisableFields(orgTypeObj);
				}
			}
		}

		if (this.getManagedBean().getSelectedOrgProfile() != null) {
			getManagedBean().setSelectedOrgProfile(null);
		}
		this.getManagedBean().getSearchBean().setMasterLabel4(null);
		getManagedBean().getSearchBean().setMasterLabel5(null);
		getManagedBean().getSearchBean().setMasterLabel6(null);
		this.getManagedBean().getSearchBean().setRoleCode(null);
		this.getManagedBean().getSearchBean().setPmUser(null);
	}

	void isDisableFields(VAuditPMDTO orgTypeObj) {
		if (ParameterConst.ORG_TYPE_PTJ == orgTypeObj.getMasterLabel3()
				|| ParameterConst.ORG_TYPE_PTJ_GROUP == orgTypeObj.getMasterLabel3()) {
			getManagedBean().setDisableFields(true);
		}
	}

	/**
	 * @param entity
	 */
	public void updateSearchCriteriaForOrgName(BaseEntity entity) {
		log.info("updateSearchCriteriaForOrgName");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		log.info("OrgProfileId from Entity ::::  " + orgProfile.getOrgProfileId());
		this.getManagedBean().getUserProfileSearchBean().setSelectedOrgProfileId(orgProfile.getOrgProfileId());

		log.info("getFinOrgName() :::: " + this.getManagedBean().getSelectedOrgProfile());

		this.getManagedBean().getSearchBean().setMasterLabel4(orgProfile.getOrgProfileId().toString());

		log.info("getSearchBean().getMasterLabel4( ::::  " + this.getManagedBean().getSearchBean().getMasterLabel4());

		this.getManagedBean().getSearchBean().setRoleCode(null);

		this.getManagedBean().getSearchBean().setPmUser(null);

		this.getManagedBean().getSearchBean().setMasterLabel5(null);

		this.getManagedBean().getSearchBean().setMasterLabel6(null);
	}

	/**
	 * @return
	 */
	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getManagedBean().getSelectedOrgType().getParameterId());
		sb.append("|recordStatus^1");
		log.info("....getManagedBean().getSelectedOrgType().getParameterCode()="
				+ getManagedBean().getSelectedOrgType().getParameterCode());
		log.info("....this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()="
				+ this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		log.info("....getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()="
				+ getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()));

		sb.append(OrgProfileUtil.retrieveAuthorizationLovSb(getManagedBean().getSelectedOrgType().getParameterCode(),
				getManagedBean().getCurrentActiveOrg().getOrgProfileId(),
				getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())));

		return sb.toString();
	}

	/**
	 * reset search bean fields
	 */
	public void resetSearchBean() {
		log.info("resetOrgSearchBean");
		String masterLabel2 = this.getManagedBean().getSearchBean().getMasterLabel2();
		String orgCategory = String.valueOf(this.getManagedBean().getSearchOrgCategory());
		AuditPMUserOrgSearchBean searchBean = new AuditPMUserOrgSearchBean();
		getManagedBean().setSearchBean(searchBean);

		this.getManagedBean().setSelectedOrgProfile(null);
		this.getManagedBean().setSelectedOrgType(null);
		getManagedBean().setLazy(false);

		getSearchMaster().resetData();
		this.getManagedBean().setSearchOrgCategory(FEDERAL_GOVT);
		getManagedBean().getSearchBean().setOrgCategory(orgCategory);
		searchBean.setMasterLabel2(masterLabel2);

		log.info("Selected masterLabel2: " + getManagedBean().getSearchBean().getMasterLabel2());

		getManagedBean().setResetMasterResult(true);

		autoSelectOrgType();
	}

	/**
	 * reset search bean fields
	 */
	public void resetOrganisationSearchBean() {
		log.info("resetOrgSearchBean");
		String masterLabel2 = this.getManagedBean().getSearchBean().getMasterLabel2();
		String orgCategory = String.valueOf(this.getManagedBean().getSearchOrgCategory());
		AuditPMUserOrgSearchBean searchBean = new AuditPMUserOrgSearchBean();
		getManagedBean().setSearchBean(searchBean);

		this.getManagedBean().setSelectedOrgProfile(null);
		this.getManagedBean().setSelectedOrgType(null);
		getManagedBean().setLazy(false);

		getSearchMaster().resetData();
		this.getManagedBean().setSearchOrgCategory(FEDERAL_GOVT);
		getManagedBean().getSearchBean().setOrgCategory(orgCategory);
		searchBean.setMasterLabel2(masterLabel2);

		log.info("Selected masterLabel2: " + getManagedBean().getSearchBean().getMasterLabel2());

		getManagedBean().setResetMasterResult(true);

		autoSelectOrgType();

		preLoading();
	}

	public void onFromDateSelect() {
		// Clear the toDate
		getManagedBean().getSearchBean().setDateTo(null);
	}

	public IPagingDataModel<VAuditPMDTO> getSearchMaster() {
		if (searchMaster == null) {
			searchMaster = new SearchMasterPagingDataModel();
		}
		return searchMaster;
	}

	public void setSearchMaster(IPagingDataModel<VAuditPMDTO> searchMaster) {
		this.searchMaster = searchMaster;
	}

	public IPagingDataModel<VAuditPMDTO> getSearchDetail() {
		return searchDetail;
	}

	public void setSearchDetail(IPagingDataModel<VAuditPMDTO> searchDetail) {
		this.searchDetail = searchDetail;
	}

	public String getRoleCode() {
		if (getManagedBean().getSearchBean().getRoleCode() != null) {
			roleCode = getManagedBean().getSearchBean().getRoleCode();
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

	public AuditPMReqDTO getReqDTO() {
		return auditPMReqDTO;
	}
}
