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
import my.ep.dto.pm.common.auditLocation.AuditLocationReqDTO;
import my.ep.dto.pm.common.auditLocation.AuditLocationResDTO;
import my.ep.dto.pm.common.auditLocation.VPmAuditLocationDTO;
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

	// #32753 - START
	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
//	private FacesScopes getFacesScopes() {
//		FacesScopes facesScopes = new FacesScopes();
//		return facesScopes;
//	}

	// #32753 - END

	public void preLoadingLocation() {
		this.resetLocationSearchBean();
		log.info("preLoading Location");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	public void searchLocation() {
		this.getManagedBean().setLazy(true);
		// Enhancement29642 - start
		// if (this.getManagedBean().getLocationSearchBean().getLocationType()
		// == null) {
		// throw new EpException(MessageConst.ERROR_100022,
		// new String[] { ResourceBundleUtils.getMessage("lbl.pm.location.type",
		// ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		// FacesScopes().getUserLocale()) });
		//
		// }
		// Enhancement29642 - end
		searchLocation.reloadData();
	}
	
	public void searchLocationDetail() {
		this.getManagedBean().setLazy(true);

		searchLocationDetail.reloadData();
	}

	public String backToSearch() {
		getManagedBean().setSelectedType(null);
		// getManagedBean().setLazy(true);
		//detailData = false;
		return NAV_TO_SEARCH;
	}

	AuditLocationReqDTO locationReqDTO = new AuditLocationReqDTO();

	public String redirectToLocation(String locationType,BigDecimal pmParameterLvlId,BigDecimal createBy,String roleCode) throws Exception {
		getManagedBean().setLocationDetailType(locationType);
		getManagedBean().getLocationSearchBean().setPmParameterLvlId(pmParameterLvlId);
		getManagedBean().getLocationSearchBean().setRoleCode(roleCode);
		getManagedBean().getLocationSearchBean().setCreatedBy(createBy.toString());
		return NAV_TO_MAINTAIN;
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
				locationReqDTO.setpLvl1Code(lSearchBean.getSystemConfigParamLevel1());
				locationReqDTO.setpLvl2Code(lSearchBean.getCategoryConfigParamLevel2());
				locationReqDTO.setpLvl3Code(lSearchBean.getItemConfigParamLevel3());

				locationReqDTO.setpRoleCode(lSearchBean.getRoleCode());


				locationReqDTO.setCreatedBy(lSearchBean.getCreatedBy());
				locationReqDTO.setPdateFrom(lSearchBean.getDateFrom());

				locationReqDTO.setPdateTo(lSearchBean.getDateTo());

				locationReqDTO.setLocationSearchBean(searchBean);
				locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);
				
				AuditLocationResDTO resDTO = locationSO.searchLocationMasterResultList(locationReqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getLocationResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
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
				System.out.println("getPmParameterLvlId ::: " + lSearchBean.getPmParameterLvlId());
				System.out.println("getRoleCode ::: " + lSearchBean.getRoleCode());
				System.out.println("getCreatedBy ::: " + lSearchBean.getCreatedBy());
				if (lSearchBean.getPmParameterLvlId() != null) {
					locationReqDTO.setPmParameterLvlId(lSearchBean.getPmParameterLvlId());
				}
				if (lSearchBean.getRoleCode() != null) {
					locationReqDTO.setpRoleCode(lSearchBean.getRoleCode());
				}
				if (lSearchBean.getCreatedBy() != null) {
					locationReqDTO.setCreatedBy(lSearchBean.getCreatedBy());
				}
				locationReqDTO.setPdateFrom(lSearchBean.getDateFrom());

				locationReqDTO.setPdateTo(lSearchBean.getDateTo());

				locationReqDTO.setLocationSearchBean(searchBean);
				locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);

				AuditLocationResDTO resDTO = locationSO.searchLocationDetailResultList(locationReqDTO);
				
				if (resDTO.isSuccessful()) {
					return resDTO.getLocationResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}

	}
	
	/**
	 * Retrieve Dropdown Level1
	 * 
	 */
	public List<VPmAuditLocationDTO> getLoadDropdown() {
		IAuditLocationSO locationSO;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		String selectedCode_L1 = getManagedBean().getLocationSearchBean().getSystemConfigParamLevel1();
		String selectedCode_L2 = getManagedBean().getLocationSearchBean().getCategoryConfigParamLevel2();
		locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);
		List<VPmAuditLocationDTO> paramLvl1List = locationSO.getLoadDropdownList(languageCode, selectedCode_L1, selectedCode_L2);
		System.out.println("paramLvl2List.size() ::::>>> ::: " + paramLvl1List.size());
		return paramLvl1List;
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
		for(VPmAuditLocationDTO d2 :paramLvl1List){
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
		System.out.println("getManagedBean().getLevel1Selected()333@#!!! ::::>>> ::: " + getManagedBean().getLocationSearchBean().getSystemConfigParamLevel1());
		locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);
		List<VPmAuditLocationDTO> paramLvl2List = locationSO.getdropdownLvl2(languageCode,getManagedBean().getLocationSearchBean().getSystemConfigParamLevel1());
		System.out.println("paramLvl2List.size() ::::>>> ::: " + paramLvl2List.size());
		return paramLvl2List;
	}
	
	/**
	 * Retrieve Dropdown Level3
	 * 
	 */
	public List<VPmAuditLocationDTO> getDropdownLevel3() {
		IAuditLocationSO locationSO;
		//getManagedBean().setSelectedType(locationType);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		System.out.println("getManagedBean().getLevel2Selected()333@#!!! ::::>>> ::: " + getManagedBean().getLocationSearchBean().getCategoryConfigParamLevel2());
		System.out.println("getManagedBean().getSystemConfigParamLevel1()111@#!!! ::::>>> ::: " + getManagedBean().getLocationSearchBean().getSystemConfigParamLevel1());
		locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);
		List<VPmAuditLocationDTO> paramLvl3List = locationSO.getdropdownLvl3(languageCode, getManagedBean().getLocationSearchBean().getSystemConfigParamLevel1(),getManagedBean().getLocationSearchBean().getCategoryConfigParamLevel2());
		System.out.println("paramLvl3List.size() ::::>>> ::: " + paramLvl3List.size());
		System.out.println("getManagedBean().getLevel3Selected()555@#!!! ::::>>> ::: " + getManagedBean().getLocationSearchBean().getItemConfigParamLevel3());
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
		
		return dropdownRoleList;
	}
	
	/**
	 * Retrieve Dropdown ActionBy List
	 * 
	 */
	public List<VPmAuditLocationDTO> getActionByListbyRoleCode() {
		IAuditLocationSO locationSO;
		locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);
		List<VPmAuditLocationDTO> dropdownRoleList = locationSO.getActionByListbyRoleCode(this.getManagedBean().getLocationSearchBean().getRole());
		this.getManagedBean().setVpmAuditLocationDTOList(dropdownRoleList);
		return dropdownRoleList;
	}
	/**
	 * search Details Location
	 * 
	 */
	private class getActionByPagingDataModel extends PagingDataModel<VPmAuditLocationDTO> {

		private static final long serialVersionUID = 1L;

		public getActionByPagingDataModel() {

			super("searchActionBylist", getManagedBean().isLazy());

		}

		public ISearchResultBean<VPmAuditLocationDTO> pageChangeListener(ISearchBean<VPmAuditLocationDTO> searchBean) {
			IAuditLocationSO locationSO;

			try {
				AuditLocationSearchBean lSearchBean = getManagedBean().getLocationSearchBean();
				locationReqDTO.setLocationType(getManagedBean().getLocationDetailType());
				if (lSearchBean.getRole()!= null) {
					locationReqDTO.setRole(lSearchBean.getRole());
				}
				locationReqDTO.setLocationSearchBean(searchBean);

				locationSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);
				AuditLocationResDTO resDTO = locationSO.getActionByResultlist(locationReqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getLocationResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}

	}
	
	public void changeLocationType(){
		log.info("changeLocationType!!!");
	}
	
	
//	 public void retrieveLevel2List(AjaxBehaviorEvent e) {
//	 String codeLevel2 = (String) e.getComponent().getAttributes().get("value");
//	 System.out.println("codeLevel2 ::::>>> ****## " + codeLevel2);
//	 VPmAuditLocationDTO reqDTO = new VPmAuditLocationDTO();
//	 reqDTO.setParamLevel3(codeLevel2);
//	 getManagedBean().setLevel2Selected(codeLevel2);
//	 System.out.println("codeLevel2 ::::>>> ### " + codeLevel2);
//	 getDropdownLevel3();
//	 }

	// public void retrieveStateList(AjaxBehaviorEvent e) {
	// Long countryId = (Long) e.getComponent().getAttributes().get("value");
	// StateReqDTO reqDTO = new StateReqDTO();
	// reqDTO.setCountryId(countryId);
	// IStateSO reqSO = (IStateSO)
	// BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
	// IStateSO.class);
	// StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
	// getManagedBean().setStates(resDTO.getStateList());
	// }

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
}
