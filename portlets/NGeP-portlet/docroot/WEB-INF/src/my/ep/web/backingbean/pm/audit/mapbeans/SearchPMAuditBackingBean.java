package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.VAuditPMDTO;
import my.ep.exception.EpException;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.AuditPMSearchBean;
import my.ep.web.managedbean.pm.audit.AuditPMManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
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
	private IPagingDataModel<VAuditPMDTO> searchMaster = new SearchMasterPagingDataModel();
	private IPagingDataModel<VAuditPMDTO> searchDetail = new SearchDetailPagingDataModel();
	// Navigation Details
	public static final String NAV_TO_SEARCH = "auditMainPM";
	public static final String NAV_TO_MAINTAIN = "auditDetailMainPM";
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public SearchPMAuditBackingBean() {

	}

	public String getSelectionRoleCode() {
		return getManagedBean().getSearchBean().getRoleCode();
	}

	public String roleCode;

	public void preLoading() {
		this.resetSearchBean();
		log.info("preLoading Location");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	public void searchMaster() {
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getSearchBean().setSearching(true);
		searchMaster.reloadData();
	}

	public void searchDetail() {
		this.getManagedBean().setLazy(true);
		searchDetail.reloadData();
	}

	public String backToSearch() {
		getManagedBean().setSelectedType(null);
		getManagedBean().getSearchBean().setSearching(false);
		return NAV_TO_SEARCH;
	}

	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();

	public String redirectToDetails(String locationType, BigDecimal pmParameterLvlId, BigDecimal createBy, String roleCode)
			throws Exception {
		getManagedBean().setLocationDetailType(locationType);
		getManagedBean().getSearchBean().setPmParameterLvlId(pmParameterLvlId);
		log.info("pmParameterLvlId.intValue() :::::  " + pmParameterLvlId.intValue());
		getManagedBean().getSearchBean().setRoleCode(roleCode);
		getManagedBean().getSearchBean().setCreatedBy(createBy.toString());
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
					auditPMReqDTO.setpItemCode(lSearchBean.getItemCode());
					auditPMReqDTO.setpItemName(lSearchBean.getItemName());
					auditPMReqDTO.setpRoleCode(lSearchBean.getRoleCodeOldVal());
					auditPMReqDTO.setPdateFrom(lSearchBean.getDateFromOldVal());
					auditPMReqDTO.setPdateTo(lSearchBean.getDateToOldVal());
				}
				if (lSearchBean.getUserId() != null && lSearchBean.isSearching()) {
					auditPMReqDTO.setCreatedBy(lSearchBean.getUserId().toString());
					lSearchBean.setUserIdOldVal(lSearchBean.getUserId());
				} else if (lSearchBean.getUserId() != null && !lSearchBean.isSearching()) {
					auditPMReqDTO.setCreatedBy(lSearchBean.getUserIdOldVal().toString());
				}

				auditPMReqDTO.setCategoryConfigParamLevel2(getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
				// lSearchBean.setPmParameterLvlIdOldval(lSearchBean.getPmParameterLvlId());
				auditPMReqDTO.setPmParameterLvlId(lSearchBean.getPmParameterLvlId());
				auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);

				ISearchResultBean<VAuditPMDTO> resDTO = null;
				System.out.println("LEVEL2 MASTER ::::::: " + getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
				resDTO = auditPMSO.searchMasterResultList(auditPMReqDTO, searchBean);
				return resDTO;

			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
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
				AuditPMSearchBean lSearchBean = getManagedBean().getSearchBean();
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

				auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);

				ISearchResultBean<VAuditPMDTO> resDTO;

				auditPMReqDTO.setCategoryConfigParamLevel2(getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
				System.out.println("LEVEL2 DETAIL ::::::: " + getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
				resDTO = auditPMSO.searchDetailResultList(auditPMReqDTO, searchBean);
				return resDTO;

			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
		}
	}

	/**
	 * Retrieve Dropdown Level1
	 * 
	 */
	public List<VAuditPMDTO> getDropdownLevel1() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
		List<VAuditPMDTO> paramLvl1List = auditPMSO.getDropdownLvl1(languageCode);
		return paramLvl1List;
	}

	/**
	 * Retrieve Dropdown Level2
	 * 
	 */
	public List<VAuditPMDTO> getDropdownLevel2() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = (IAuditPMSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);
		List<VAuditPMDTO> paramLvl2List = auditPMSO.getDropdownLvl2(languageCode, getManagedBean().getSearchBean()
				.getSystemConfigParamLevel1());
		return paramLvl2List;
	}

	/**
	 * Retrieve Dropdown Level3
	 * 
	 */
	public List<VAuditPMDTO> getDropdownLevel3() {
		IAuditPMSO auditPMSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditPMSO = (IAuditPMSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);
		List<VAuditPMDTO> paramLvl3List = auditPMSO.getDropdownLvl3(languageCode, getManagedBean().getSearchBean()
				.getSystemConfigParamLevel1(), getManagedBean().getSearchBean().getCategoryConfigParamLevel2());
		return paramLvl3List;
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

	public void changeType() {
		log.info("changeLocationType!!!");
		getManagedBean().getSearchBean().setDateTo(null);
		getManagedBean().getSearchBean().setDateFrom(null);
	}

	public void resetGetActionBy() {
		Long userId = getManagedBean().getSearchBean().getPmUser().getUserId();
		getManagedBean().getSearchBean().setUserId(new BigDecimal(userId));
	}

	/**
	 * reset search bean fields
	 */
	public void resetSearchBean() {
		log.info("resetLocationSearchBean");
		getManagedBean().setLazy(false);
		AuditPMSearchBean searchBean = new AuditPMSearchBean();
		this.getManagedBean().setSearchBean(searchBean);
		searchMaster = new SearchMasterPagingDataModel();
		getManagedBean().setSelectedType(null);
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
		AuditPMSearchBean searchBean = new AuditPMSearchBean();
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
}
