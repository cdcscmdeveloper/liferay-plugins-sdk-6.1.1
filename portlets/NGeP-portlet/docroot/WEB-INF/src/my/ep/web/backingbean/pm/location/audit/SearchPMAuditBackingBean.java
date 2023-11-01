package my.ep.web.backingbean.pm.location.audit;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.audittrail.IAuditLocationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.auditlocation.AuditLocationReqDTO;
import my.ep.dto.pm.common.auditlocation.VPmAuditLocationDTO;
import my.ep.exception.EpException;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.AuditLocationSearchBean;
import my.ep.web.managedbean.pm.audittrail.AuditLocationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "locationAuditBackingBean")
@ViewScoped
public class SearchPMAuditBackingBean extends BaseBackingBean<AuditLocationManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SearchPMAuditBackingBean.class);
	private IPagingDataModel<VPmAuditLocationDTO> searchLocation = new SearchLocatonPagingDataModel();
	private IPagingDataModel<VPmAuditLocationDTO> searchLocationDetail = new SearchDetailLocatonPagingDataModel();
	// Navigation Details
	public static final String NAV_TO_SEARCH = "auditLocationMain";
	public static final String NAV_TO_MAINTAIN = "auditDetailLocationMain";
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public SearchPMAuditBackingBean() {

	}

	public String getSelectionRoleCode() {
		System.out.println("roleCode frm UI :::>>>: " + getManagedBean().getLocationSearchBean().getRoleCode());
		return getManagedBean().getLocationSearchBean().getRoleCode();
	}

	public String roleCode;

	public void preLoadingLocation() {
		this.resetLocationSearchBean();
		log.info("preLoading Location");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	public void searchLocation() {
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getLocationSearchBean().setSearching(true);
		searchLocation.reloadData();	}

	public void searchLocationDetail() {
		this.getManagedBean().setLazy(true);
		searchLocationDetail.reloadData();
	}

	public String backToSearch() {
		getManagedBean().setSelectedType(null);
		getManagedBean().getLocationSearchBean().setSearching(false);
		return NAV_TO_SEARCH;
	}

	AuditLocationReqDTO locationReqDTO = new AuditLocationReqDTO();

	public String redirectToDetailsLocation(String locationType, BigDecimal pmParameterLvlId, BigDecimal createBy,
			String roleCode) throws Exception {
			getManagedBean().setLocationDetailType(locationType);
			getManagedBean().getLocationSearchBean().setPmParameterLvlId(pmParameterLvlId);
			getManagedBean().getLocationSearchBean().setRoleCode(roleCode);
			getManagedBean().getLocationSearchBean().setCreatedBy(createBy.toString());
		return NAV_TO_MAINTAIN;
	}

	public boolean searching(boolean search) throws Exception {
		getManagedBean().getLocationSearchBean().setSearching(search);
		return getManagedBean().getLocationSearchBean().isSearching();
	}

	/**
	 * search Master Location
	 * 
	 */
	private class SearchLocatonPagingDataModel extends PagingDataModel<VPmAuditLocationDTO> {

		private static final long serialVersionUID = 1L;

		public SearchLocatonPagingDataModel() {

			super("searchLocations", getManagedBean().isLazy());

		}

		public ISearchResultBean<VPmAuditLocationDTO> pageChangeListener(ISearchBean<VPmAuditLocationDTO> searchBean) {
			IAuditLocationSO locationSO;
			try {
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				locationReqDTO.setpLanguage(languageCode);
				AuditLocationSearchBean lSearchBean = getManagedBean().getLocationSearchBean();
				if (lSearchBean.isSearching()) {
				locationReqDTO.setpLvl1Code(lSearchBean.getSystemConfigParamLevel1());
				locationReqDTO.setpLvl2Code(lSearchBean.getCategoryConfigParamLevel2());
				locationReqDTO.setpLvl3Code(lSearchBean.getItemConfigParamLevel3());
				locationReqDTO.setpRoleCode(lSearchBean.getRoleCode());
				locationReqDTO.setPdateFrom(lSearchBean.getDateFrom());
				locationReqDTO.setPdateTo(lSearchBean.getDateTo());
				
				lSearchBean.setSystemConfigParamLevel1OldVal(lSearchBean.getSystemConfigParamLevel1());
				lSearchBean.setCategoryConfigParamLevel2OldVal(lSearchBean.getCategoryConfigParamLevel2());
				lSearchBean.setItemConfigParamLevel3OldVal(lSearchBean.getItemConfigParamLevel3());
				lSearchBean.setRoleCodeOldVal(lSearchBean.getRoleCode());
				lSearchBean.setDateFromOldVal(lSearchBean.getDateFrom());
				lSearchBean.setDateToOldVal(lSearchBean.getDateTo());
				}else if(!lSearchBean.isSearching()){
					locationReqDTO.setpLvl1Code(lSearchBean.getSystemConfigParamLevel1OldVal());
					locationReqDTO.setpLvl2Code(lSearchBean.getCategoryConfigParamLevel2OldVal());
					locationReqDTO.setpLvl3Code(lSearchBean.getItemConfigParamLevel3OldVal());
					locationReqDTO.setpRoleCode(lSearchBean.getRoleCodeOldVal());
					locationReqDTO.setPdateFrom(lSearchBean.getDateFromOldVal());
					locationReqDTO.setPdateTo(lSearchBean.getDateToOldVal());
				}
				if (lSearchBean.getUserId() != null && lSearchBean.isSearching()){
					locationReqDTO.setCreatedBy(lSearchBean.getUserId().toString());
					lSearchBean.setUserIdOldVal(lSearchBean.getUserId());
				}else if (lSearchBean.getUserId() != null && !lSearchBean.isSearching()){
					locationReqDTO.setCreatedBy(lSearchBean.getUserIdOldVal().toString());
				}

				//locationReqDTO.setLocationSearchBean(searchBean);
				locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);

				ISearchResultBean<VPmAuditLocationDTO> resDTO ;
				resDTO = locationSO.searchLocationMasterResultList(locationReqDTO,searchBean);

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
	private class SearchDetailLocatonPagingDataModel extends PagingDataModel<VPmAuditLocationDTO> {

		private static final long serialVersionUID = 1L;

		public SearchDetailLocatonPagingDataModel() {

			super("searchDetailLocations", getManagedBean().isLazy());

		}

		public ISearchResultBean<VPmAuditLocationDTO> pageChangeListener(ISearchBean<VPmAuditLocationDTO> searchBean) {
			IAuditLocationSO locationSO;

			try {
				AuditLocationSearchBean lSearchBean = getManagedBean().getLocationSearchBean();
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				locationReqDTO.setpLanguage(languageCode);
				System.out.println("getPmParameterLvlId#### :::" + getManagedBean().getLocationSearchBean().getPmParameterLvlId());
				System.out.println("getPmParameterLvlId ::: " + lSearchBean.getPmParameterLvlId());
				System.out.println("getRoleCode ::: " + lSearchBean.getRoleCode());
				System.out.println("getCreatedBy ::: " + lSearchBean.getCreatedBy());
				if (lSearchBean.getPmParameterLvlId() != null ) {
					locationReqDTO.setPmParameterLvlId(lSearchBean.getPmParameterLvlId());
					lSearchBean.setPmParameterLvlIdOldval(lSearchBean.getPmParameterLvlId());
				}
				if (lSearchBean.getRoleCode() != null) {
					locationReqDTO.setpRoleCode(lSearchBean.getRoleCode());
					lSearchBean.setRoleCodeOldVal(lSearchBean.getRoleCode());
				}
				if (lSearchBean.getCreatedBy() != null ) {
					locationReqDTO.setCreatedBy(lSearchBean.getCreatedBy());
					lSearchBean.setCreatedByOldVal(lSearchBean.getCreatedBy());
				}
				if(lSearchBean.isSearching()){
				locationReqDTO.setPdateFrom(lSearchBean.getDateFrom());				
				locationReqDTO.setPdateTo(lSearchBean.getDateTo());
				
				lSearchBean.setPdateFromOldVal(lSearchBean.getDateFrom());
				lSearchBean.setPdateToOldVal(lSearchBean.getDateTo());
				}
				if(!lSearchBean.isSearching()){
					locationReqDTO.setPdateFrom(lSearchBean.getPdateFromOldVal());
					locationReqDTO.setPdateTo(lSearchBean.getPdateToOldVal());
				}
				
				//locationReqDTO.setLocationSearchBean(searchBean);
				locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);

				ISearchResultBean<VPmAuditLocationDTO> resDTO ;
				resDTO = locationSO.searchLocationDetailResultList(locationReqDTO,searchBean);

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
	public List<VPmAuditLocationDTO> getDropdownLevel1() {
		IAuditLocationSO locationSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);
		List<VPmAuditLocationDTO> paramLvl1List = locationSO.getdropdownLvl1(languageCode);
		System.out.println("paramLvl2List.size() ::::>>> ::: " + paramLvl1List.size());
		for (VPmAuditLocationDTO d2 : paramLvl1List) {
			System.out.println("d2 getLevelCode >>> ::: " + d2.getLevelCode());
			System.out.println("d2 getCodeName >>> ::: " + d2.getCodeName());
			System.out.println("d2 getLevelId >>> ::: " + d2.getLevelId());
		}
		return paramLvl1List;
	}

	/**
	 * Retrieve Dropdown Level2
	 * 
	 */
	public List<VPmAuditLocationDTO> getDropdownLevel2() {
		IAuditLocationSO locationSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		System.out.println("getManagedBean().getLevel1Selected()333@#!!! ::::>>> ::: "
				+ getManagedBean().getLocationSearchBean().getSystemConfigParamLevel1());
		locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);
		List<VPmAuditLocationDTO> paramLvl2List = locationSO.getdropdownLvl2(languageCode, getManagedBean()
				.getLocationSearchBean().getSystemConfigParamLevel1());
		System.out.println("paramLvl2List.size() ::::>>> ::: " + paramLvl2List.size());
		return paramLvl2List;
	}

	/**
	 * Retrieve Dropdown Level3
	 * 
	 */
	public List<VPmAuditLocationDTO> getDropdownLevel3() {
		IAuditLocationSO locationSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		System.out.println("getManagedBean().getLevel2Selected()333@#!!! ::::>>> ::: "
				+ getManagedBean().getLocationSearchBean().getCategoryConfigParamLevel2());
		System.out.println("getManagedBean().getSystemConfigParamLevel1()111@#!!! ::::>>> ::: "
				+ getManagedBean().getLocationSearchBean().getSystemConfigParamLevel1());
		locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);
		List<VPmAuditLocationDTO> paramLvl3List = locationSO.getdropdownLvl3(languageCode, getManagedBean()
				.getLocationSearchBean().getSystemConfigParamLevel1(), getManagedBean().getLocationSearchBean()
				.getCategoryConfigParamLevel2());
		System.out.println("paramLvl3List.size() ::::>>> ::: " + paramLvl3List.size());
		System.out.println("getManagedBean().getLevel3Selected()555@#!!! ::::>>> ::: "
				+ getManagedBean().getLocationSearchBean().getItemConfigParamLevel3());
		return paramLvl3List;
	}

	/**
	 * Retrieve Dropdown Role List
	 * 
	 */
	public List<VPmAuditLocationDTO> getDropdownRoleList() {
		IAuditLocationSO locationSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);
		List<VPmAuditLocationDTO> dropdownRoleList = locationSO.getDropdownRoleList(languageCode);

		return dropdownRoleList;	}

	public void changeLocationType() {
		log.info("changeLocationType!!!");
	}

	public void resetGetActionBy() {
		System.out.println("USERID ::: >>> ::: " + getManagedBean().getLocationSearchBean().getPmUser().getUserId());
		Long userId = getManagedBean().getLocationSearchBean().getPmUser().getUserId();
		getManagedBean().getLocationSearchBean().setUserId(new BigDecimal(userId));
	}

	/**
	 * reset search bean fields
	 */
	public void resetLocationSearchBean() {
		log.info("resetLocationSearchBean");
		getManagedBean().setLazy(false);
		AuditLocationSearchBean searchBean = new AuditLocationSearchBean();
		this.getManagedBean().setLocationSearchBean(searchBean);
		searchLocation = new SearchLocatonPagingDataModel();
		getManagedBean().setSelectedType(null);
	}

	public IPagingDataModel<VPmAuditLocationDTO> getSearchLocation() {
		return searchLocation;
	}

	public void setSearchLocation(IPagingDataModel<VPmAuditLocationDTO> searchLocation) {
		this.searchLocation = searchLocation;
	}

	public void resetLocationDetailSearchBean() {
		log.info("resetLocationDetailSearchBean");
		getManagedBean().setLazy(false);
		AuditLocationSearchBean searchBean = new AuditLocationSearchBean();
		this.getManagedBean().setLocationSearchBean(searchBean);
		searchLocationDetail = new SearchDetailLocatonPagingDataModel();
		getManagedBean().setSelectedType(null);
	}

	public IPagingDataModel<VPmAuditLocationDTO> getSearchLocationDetail() {
		return searchLocationDetail;
	}

	public void setSearchLocationDetail(IPagingDataModel<VPmAuditLocationDTO> searchLocationDetail) {
		this.searchLocationDetail = searchLocationDetail;
	}

	public String getRoleCode() {
		if (getManagedBean().getLocationSearchBean().getRoleCode() != null) {
			return roleCode = getManagedBean().getLocationSearchBean().getRoleCode();
		}
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
}
