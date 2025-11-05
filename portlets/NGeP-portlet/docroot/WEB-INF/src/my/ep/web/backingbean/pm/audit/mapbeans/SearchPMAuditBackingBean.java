package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.VAuditPMDTO;
import my.ep.exception.EpException;
import my.ep.persistence.logging.LogKey;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.AuditPMSearchBean;
import my.ep.web.managedbean.pm.audit.AuditPMManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

/**
 * #35509
 * 
 * @author normuliana
 * 
 */
@ManagedBean(name = "searchPMAuditBackingBean")
@ViewScoped
public class SearchPMAuditBackingBean extends BaseBackingBean<AuditPMManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SearchPMAuditBackingBean.class);
	private transient IPagingDataModel<VAuditPMDTO> searchMaster = new SearchMasterPagingDataModel();
	private transient IPagingDataModel<VAuditPMDTO> searchDetail = new SearchDetailPagingDataModel();
	/* Navigation Details */
	public static final String NAV_TO_SEARCH = "auditMainPM";
	public static final String NAV_TO_MAINTAIN = "auditDetailMainPM";
	/* Default value */
	public static final boolean SEARCH_DEFAULT = false;

	private String roleCode;

	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();

	public SearchPMAuditBackingBean() {
		/*
		 * This constructor is intentionally empty. Initialization is handled
		 * via dependency injection or setter methods.
		 */
	}

	public String getSelectionRoleCode() {
		return getManagedBean().getSearchBean().getRoleCode();
	}

	/**
	 * preLoading()
	 */
	public void preLoading() {
		this.resetSearchBean();
		log.info("preLoading Location");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	/**
	 * searchMaster()
	 */
	public void searchMaster() {
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getSearchBean().setSearching(true);
		validateAtLeastOneSearchCriteria();
		searchMaster.reloadData();
	}

	protected void validateAtLeastOneSearchCriteria() {
		AuditPMSearchBean search = getManagedBean().getSearchBean();

		boolean hasPrimaryCriteria = !StringUtils.isBlank(search.getSystemConfigParamLevel1())
				&& !StringUtils.isBlank(search.getCategoryConfigParamLevel2());

		boolean hasNoSecondaryCriteria = hasNoSecondaryCriteria(search);

		if (hasPrimaryCriteria && hasNoSecondaryCriteria) {
			throw new EpException(SmCommonUtil.getMessage("lbl.pm.audit.search.instruction1", null));
		}
	}

	boolean hasNoSecondaryCriteria(AuditPMSearchBean search) {
		return isItemInfoBlank(search) && isUserInfoBlank(search);
	}

	private boolean isItemInfoBlank(AuditPMSearchBean search) {
		return StringUtils.isBlank(search.getItemConfigParamLevel3()) && StringUtils.isBlank(search.getItemCode())
				&& StringUtils.isBlank(search.getItemName());
	}

	private boolean isUserInfoBlank(AuditPMSearchBean search) {
		return StringUtils.isBlank(search.getRoleCode()) && search.getPmUser() == null && search.getDateFrom() == null
				&& search.getDateTo() == null;
	}

	/**
	 * searchDetail()
	 */
	public void searchDetail() {
		this.getManagedBean().setLazy(true);
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
	 * @return
	 * @throws Exception
	 */
	public String redirectToDetails(String paramLevel3, BigDecimal pmParameterLvlId, BigDecimal createBy, String roleCode,
			String paramLevelCode3, VAuditPMDTO dto)
			throws AuditPMException {
		if (paramLevel3 == null || pmParameterLvlId == null || roleCode == null) {
			throw new AuditPMException("Missing required parameters for redirectToDetails FirstUI PM Audit. "
 + " <paramLevel3>:"
					+ paramLevel3 + " <pmParameterLvlId>:" + pmParameterLvlId + "<roleCode>:" + roleCode);
		}

		AuditPMSearchBean lSearchBean = getManagedBean().getSearchBean();
		getManagedBean().getSearchBean().setPmParameterLvlId(pmParameterLvlId);
		log.info("pmParameterLvlId.intValue() :::::  " + pmParameterLvlId.intValue());
		getManagedBean().getSearchBean().setCreatedBy(createBy.toString());

		getManagedBean().getSearchBean().setPmParameterLvlId(dto.getPmParameterLvlId());
		getManagedBean().getSearchBean().setItemConfigParamLevel3(dto.getParamLevelCode3());
		getManagedBean().getSearchBean().setRoleCode(dto.getRoleCode());
		getManagedBean().getSearchBean().setCreatedBy(createBy.toString());

		return NAV_TO_MAINTAIN;
	}

	/**
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public boolean searching(boolean search) throws AuditPMException {
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
		@Override
		public ISearchResultBean<VAuditPMDTO> pageChangeListener(ISearchBean<VAuditPMDTO> searchBean) {
			IAuditPMSO auditPMSO;

			try {

				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				auditPMReqDTO.setpLanguage(languageCode);
				AuditPMSearchBean lSearchBean = getManagedBean().getSearchBean();
				if (lSearchBean.isSearching()) {
					auditPMReqDTO.setpLvl1Code(lSearchBean.getSystemConfigParamLevel1());
					auditPMReqDTO.setpLvl2Code(lSearchBean.getCategoryConfigParamLevel2());
					auditPMReqDTO.setpLvl3Code(lSearchBean.getItemConfigParamLevel3());
					auditPMReqDTO.setpItemCode(lSearchBean.getItemCode());
					auditPMReqDTO.setpItemName(lSearchBean.getItemName());
					auditPMReqDTO.setpRoleCode(lSearchBean.getRoleCode());
					auditPMReqDTO.setPdateFrom(lSearchBean.getDateFrom());
					auditPMReqDTO.setPdateTo(lSearchBean.getDateTo());

					lSearchBean.setSystemConfigParamLevel1OldVal(lSearchBean.getSystemConfigParamLevel1());
					lSearchBean.setCategoryConfigParamLevel2OldVal(lSearchBean.getCategoryConfigParamLevel2());
					lSearchBean.setItemConfigParamLevel3OldVal(lSearchBean.getItemConfigParamLevel3());
					lSearchBean.setItemNameOldVal(lSearchBean.getItemName());
					lSearchBean.setItemCodeOldVal(lSearchBean.getItemCode());
					lSearchBean.setRoleCodeOldVal(lSearchBean.getRoleCode());
					lSearchBean.setDateFromOldVal(lSearchBean.getDateFrom());
					lSearchBean.setDateToOldVal(lSearchBean.getDateTo());
				} else if (!lSearchBean.isSearching()) {
					auditPMReqDTO.setpLvl1Code(lSearchBean.getSystemConfigParamLevel1OldVal());
					auditPMReqDTO.setpLvl2Code(lSearchBean.getCategoryConfigParamLevel2OldVal());
					auditPMReqDTO.setpLvl3Code(lSearchBean.getItemConfigParamLevel3OldVal());
					auditPMReqDTO.setpItemCode(lSearchBean.getItemCodeOldVal());
					auditPMReqDTO.setpItemName(lSearchBean.getItemNameOldVal());
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

				auditPMReqDTO.setCategoryConfigParamLevel2(getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
				auditPMReqDTO.setPmParameterLvlId(lSearchBean.getPmParameterLvlId());
				auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);

				log.info("LEVEL2 MASTER ::::::: " + getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
				return auditPMSO.searchMasterResultList(auditPMReqDTO, searchBean);

			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, "FirstUIViewMaster Pm Audit", "",
						"auditPMSO", new LogKey(getUserIdSessionFromLoginId()).getUserId(), e);
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

		@Override
		public ISearchResultBean<VAuditPMDTO> pageChangeListener(ISearchBean<VAuditPMDTO> searchBean) {
			IAuditPMSO auditPMSO;

			try {
				AuditPMSearchBean lSearchBean = getManagedBean().getSearchBean();
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				auditPMReqDTO.setpLanguage(languageCode);
				if (lSearchBean.getPmParameterLvlId() != null) {
					auditPMReqDTO.setPmParameterLvlId(lSearchBean.getPmParameterLvlId());
					lSearchBean.setPmParameterLvlIdOldval(lSearchBean.getPmParameterLvlId());
				}
				if (lSearchBean.getItemConfigParamLevel3() != null) {
					auditPMReqDTO.setpLvl3Code(lSearchBean.getItemConfigParamLevel3());
					lSearchBean.setpLvl3CodeOld(lSearchBean.getItemConfigParamLevel3());
				}
				if (lSearchBean.getRoleCode() != null) {
					auditPMReqDTO.setpRoleCode(lSearchBean.getRoleCode());
					lSearchBean.setRoleCodeOldVal(lSearchBean.getRoleCode());
				}
				if (lSearchBean.getCreatedBy() != null) {
					auditPMReqDTO.setCreatedBy(lSearchBean.getCreatedBy());
					lSearchBean.setCreatedByOldVal(lSearchBean.getCreatedBy());
				}
				if (lSearchBean.getItemCode() != null) {
					auditPMReqDTO.setpItemCode(lSearchBean.getItemCode());
					lSearchBean.setItemCodeOldVal(lSearchBean.getItemCode());
				}
				if (lSearchBean.getItemName() != null) {
					auditPMReqDTO.setpItemName(lSearchBean.getItemName());
					lSearchBean.setItemNameOldVal(lSearchBean.getItemName());
				}

				searching(lSearchBean);

				auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);

				ISearchResultBean<VAuditPMDTO> resDTO;

				auditPMReqDTO.setCategoryConfigParamLevel2(getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
				log.info("LEVEL2 DETAIL ::::::: " + getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
				resDTO = auditPMSO.searchDetailResultList(auditPMReqDTO, searchBean);
				return resDTO;

			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, "FirstUIViewDetail PM Audit", "",
						"auditPMSO", new LogKey(getUserIdSessionFromLoginId()).getUserId(), e);
			}
			return null;
		}
	}

	AuditPMSearchBean searching(AuditPMSearchBean lSearchBean) {
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
		return lSearchBean;
	}

	/**
	 * Retrieve Dropdown Level1
	 * 
	 */
	public List<VAuditPMDTO> getDropdownLevel1() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
		return auditPMSO.getDropdownLvl1(languageCode);
	}

	/**
	 * Retrieve Dropdown Level2
	 * 
	 */
	public List<VAuditPMDTO> getDropdownLevel2() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
		return auditPMSO.getDropdownLvl2(languageCode, getManagedBean().getSearchBean().getSystemConfigParamLevel1());
	}

	/**
	 * Retrieve Dropdown Level3
	 * 
	 */
	public List<VAuditPMDTO> getDropdownLevel3() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
		return auditPMSO.getDropdownLvl3(languageCode, getManagedBean().getSearchBean().getSystemConfigParamLevel1(),
				getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
	}

	/**
	 * Retrieve Dropdown Role List
	 * 
	 */
	public List<VAuditPMDTO> getDropdownRoleList() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
		return auditPMSO.getDropdownRoleList(languageCode, "null");
	}

	/**
	 * changeType()
	 */
	public void changeType() {
		log.info("changeLocationType!!!");
		this.getManagedBean().getSearchBean().setCategoryConfigParamLevel2(null);
		this.getManagedBean().getSearchBean().setItemConfigParamLevel3(null);
		this.getManagedBean().getSearchBean().setItemCode(null);
		this.getManagedBean().getSearchBean().setItemName(null);
		this.getManagedBean().getSearchBean().setDateTo(null);
		this.getManagedBean().getSearchBean().setDateFrom(null);
		this.getManagedBean().getSearchBean().setRoleCode(null);
		this.getManagedBean().getSearchBean().setPmUser(null);
	}

	/**
	 * resetGetActionBy(
	 */
	public void resetGetActionBy() {
		Long userId = getManagedBean().getSearchBean().getPmUser().getUserId();
		getManagedBean().getSearchBean().setUserId(new BigDecimal(userId));
	}

	/**
	 * reset search bean fields
	 */
	public void resetSearchBean() {
		log.info("resetLocationSearchBean");
		changeType();
		AuditPMSearchBean searchBean = new AuditPMSearchBean();
		this.getManagedBean().setSearchBean(searchBean);
		getManagedBean().setSelectedType(null);
		getManagedBean().setLazy(false);
		getSearchMaster().resetData();
		searchMaster = new SearchMasterPagingDataModel();

		log.info("ItemCode after reset: " + getManagedBean().getSearchBean().getItemCode());
		log.info("ItemName after reset: " + getManagedBean().getSearchBean().getItemName());
		log.info("RoleCode after reset: " + getManagedBean().getSearchBean().getRoleCode());

	}

	public void onFromDateSelect() {
		// Clear the toDate
		getManagedBean().getSearchBean().setDateTo(null);
	}

	public IPagingDataModel<VAuditPMDTO> getSearchMaster() {
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

	public AuditPMReqDTO getReqDTO() {
		return auditPMReqDTO;
	}
}
