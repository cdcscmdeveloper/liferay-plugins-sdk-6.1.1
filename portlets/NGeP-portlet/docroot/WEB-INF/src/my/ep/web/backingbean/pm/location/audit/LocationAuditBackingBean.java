package my.ep.web.backingbean.pm.location.audit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.audittrail.IAudittrailSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.audittrail.AuditLocationReqDTO;
import my.ep.dto.pm.common.audittrail.AuditLocationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.VPmAuditLocation;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.AuditLocationSearchBean;
import my.ep.web.managedbean.pm.audit.AuditLocationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "locationAuditBackingBean")
@ViewScoped
public class LocationAuditBackingBean extends BaseBackingBean<AuditLocationManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(LocationAuditBackingBean.class);
	private IPagingDataModel<VPmAuditLocation> searchLocation = new SearchLocatonPagingDataModel();

	// Navigation Details
	public static final String NAV_TO_SEARCH = "auditLocationMain";
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public LocationAuditBackingBean() {

	}
	// #32753 - START
	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

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
		if (this.getManagedBean().getLocationSearchBean().getLocationType() == null) {
			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage("lbl.pm.location.type",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });

		}
		// Enhancement29642 - end
		searchLocation.reloadData();
	}

	public String backToSearch() {
		getManagedBean().setSelectedType(null);
		// getManagedBean().setLazy(true);

		return NAV_TO_SEARCH;
	}

	private ISearchBean<VPmAuditLocation> getSearchBeanData(ISearchBean<VPmAuditLocation> searchBean) {
		AuditLocationSearchBean lSearchBean = this.getManagedBean().getLocationSearchBean();

		String locTypeSearch = lSearchBean.getLocationType().equalsIgnoreCase("ST") ? "STATE" : 
			lSearchBean.getLocationType().equalsIgnoreCase("DV") ? "ADOPT DIVISION" : 
				lSearchBean.getLocationType().equalsIgnoreCase("DS") ? "DISTRICT" :
					lSearchBean.getLocationType().equalsIgnoreCase("CT") ? "CITY" : null ;
		if (lSearchBean.getLocationType() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("locationType", SearchOperator.OPERATOR_EQUALS.value(), locTypeSearch));
		}
		if (lSearchBean.getActionType() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("actionType", SearchOperator.OPERATOR_EQUALS.value(), lSearchBean.getActionType()));
		}
		return searchBean;

	}

	/**
	 * search Location details
	 * 
	 */
	private class SearchLocatonPagingDataModel extends PagingDataModel<VPmAuditLocation> {

		private static final long serialVersionUID = 1L;

		public SearchLocatonPagingDataModel() {

			super("searchLocations", getManagedBean().isLazy());

		}
		public ISearchResultBean<VPmAuditLocation> pageChangeListener(ISearchBean<VPmAuditLocation> searchBean) {
			IAudittrailSO locationSO;

			try {
				AuditLocationReqDTO locationReqDTO = new AuditLocationReqDTO();

				ISearchBean<VPmAuditLocation> searchBeanData = getSearchBeanData(searchBean);				
				
				AuditLocationSearchBean lSearchBean = getManagedBean().getLocationSearchBean();
				String locTypeSearch = lSearchBean.getLocationType().equalsIgnoreCase("ST") ? "STATE" : 
					lSearchBean.getLocationType().equalsIgnoreCase("DV") ? "ADOPT DIVISION" : 
						lSearchBean.getLocationType().equalsIgnoreCase("DS") ? "DISTRICT" :
							lSearchBean.getLocationType().equalsIgnoreCase("CT") ? "CITY" : null ;
				if (lSearchBean.getLocationType() != null) {
					locationReqDTO.setLocationType(locTypeSearch);
					}
				if (lSearchBean.getActionType() != null) {
					locationReqDTO.setActionType(lSearchBean.getActionType());
					}				
				locationReqDTO.setLocationSearchBean(searchBeanData);
				locationSO = (IAudittrailSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAudittrailSO.class);
				AuditLocationResDTO resDTO = locationSO.searchPMData(locationReqDTO);
				
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

//	public void retrieveStateList(AjaxBehaviorEvent e) {
//		Long countryId = (Long) e.getComponent().getAttributes().get("value");
//		StateReqDTO reqDTO = new StateReqDTO();
//		reqDTO.setCountryId(countryId);
//		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
//				IStateSO.class);
//		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
//		getManagedBean().setStates(resDTO.getStateList());
//	}

//	public void retrieveDivisionList(AjaxBehaviorEvent e) {
//		Long stateId = (Long) e.getComponent().getAttributes().get("value");
//
//		StateReqDTO stateReqDTO = new StateReqDTO();
//		stateReqDTO.setStateId(stateId);
//		IStateSO stateReqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(
//				ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
//		StateResDTO stateResDTO = stateReqSO.retriveStateByID(stateReqDTO);
//		PmState pmState = null;
//		if (null != stateResDTO.getPmState()) {
//			pmState = stateResDTO.getPmState();
//		}
//
//		DivisionReqDTO reqDTO = new DivisionReqDTO();
//
//		reqDTO.setStateId(stateId);
//		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance().getServiceObject(
//				ModuleCodeConst.PROFILE_MANAGEMENT, IDivisionSO.class);
//		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
//		getManagedBean().setDivisions(resDTO.getDivisionList());
//
//		if (pmState != null) {
//			if (pmState.getAdoptDivision()) {
//				getManagedBean().setDivisionBox(false);
//			} else {
//				getManagedBean().getDistrict().getPmDivision().setDivisionId(null);
//				getManagedBean().setDivisionBox(true);
//			}
//		}
//
//	}

//	public void retrieveDistrictList(AjaxBehaviorEvent e) {
//		Long stateId = (Long) e.getComponent().getAttributes().get("value");
//		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
//		districtReqDTO.setStateId(stateId);
//		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(
//				ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
//		DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);
//		getManagedBean().setDistricts(districtResDTO.getDistrictList());
//
//	}

//	public List<PmDivision> getDivisionList(Long stateId) {
//
//		return PmCommonUtil.retrievePmDivisionList(stateId);
//	}

//	public SelectItem[] getStatusOptions() {
//
//		return PmCommonUtil.getCommonStatusOptions();
//	}

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

	public IPagingDataModel<VPmAuditLocation> getSearchLocation() {
		return searchLocation;
	}

	public void setSearchLocation(IPagingDataModel<VPmAuditLocation> searchLocation) {
		this.searchLocation = searchLocation;
	}

	/**
	 * get all country list
	 * 
	 * @return List<PmCountry>
	 */
//	public List<PmCountry> getCountryList() {
//		log.info("getCountryList");
//		return PmCommonUtil.retrievePmCountryList();
//	}

}
