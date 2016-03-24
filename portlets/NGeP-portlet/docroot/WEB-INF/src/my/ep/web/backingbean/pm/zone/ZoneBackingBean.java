/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.backingbean.pm.zone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.zone.ZoneReqDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.pm.common.PmZoneState;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ZoneSearchBean;
import my.ep.web.managedbean.pm.zone.ZoneManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;


/**
 * The Class ZoneBackingBean.
 */
@ManagedBean(name = "zoneBackingBean")
@ViewScoped
public class ZoneBackingBean extends BaseBackingBean<ZoneManagedBean> {
	
	/** The log. */
	private static Logger log = Logger.getLogger(ZoneBackingBean.class);
	
	/** The Constant NAV_TO_MAINTAIN. */
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/zonesearch/zoneMaintenance";
	
	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/pm/zonesearch/zoneSearchMain";

	/** The search zone. */
	private IPagingDataModel<PmZone> searchZone = new SearchZonePagingDataModel();

	/** The result. */
	boolean result = false;
	
	/** The result. *//*
	boolean statusField = false;*/
	// Default value
	/** The Constant SEARCH_DEFAULT. */
	public static final boolean SEARCH_DEFAULT = false;

	/**
	 * Instantiates a new zone backing bean.
	 */
	public ZoneBackingBean() {
         
	}

	/**
	 * Pre loading zone.
	 */
	public void preLoadingZone() {	
		log.info("preLoading Zone method");		
		this.resetZoneSearchBean();		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * reset search bean fields.
	 */
	public void resetZoneSearchBean() {
		log.info("ZoneSearchBeanLoading ----");
		this.getManagedBean().setLazy(false);
		this.getManagedBean().setZoneSearchBean(new ZoneSearchBean());
		searchZone = new SearchZonePagingDataModel();
	}

	/**
	 * Search zone.
	 */
	public void searchZone() {
		searchZone.reloadData();
	}

	/**
	 * Back to search.
	 * 
	 * @return the string
	 */
	public String backToSearch() {
		this.getManagedBean().setLazy(true);		
		return NAV_TO_MAIN;
	}

	/**
	 * Initial zone.
	 */
	public void initialZone() {		
		getManagedBean().setPmZone(new PmZone());
		getManagedBean().setZoneStateList(new ArrayList<PmZoneState>());
		
	}
	
	/**
	 * Redirect to zone.
	 * 
	 * @param id
	 *            (Long id(mandatory))
	 * @return NAV_TO_MAINTAIN
	 * @throws Exception
	 *             the exception
	 */
	public String redirectToZone(Long id)
			throws Exception {

		if (id != null && id > 0) {
			maintainZone(id);
			
		} else {
			initialZone();
			
		}
		getManagedBean().setShowParentMsg(true);
		return NAV_TO_MAINTAIN;
	}

	/**
	 * Save zone.
	 * 
	 * @return the string
	 * @throws Exception 
	 */
	public String saveZone() throws Exception {
		IZoneSO zoneSO = null;
		String returnUrl = null;
		try{
			List<PmZoneState> zones = new ArrayList<PmZoneState>();
			PmZone zone = this.getManagedBean().getPmZone();
			zoneValidation(zone);
			
			if (zone.isBrandNew()) {
				zone.setZoneCode(zone.getZoneCode().toUpperCase());
				List<PmZoneState> stateZones = getManagedBean().getZoneStateList();
				for (PmZoneState zoneSta : stateZones) {
					zoneSta.setPmZone(zone);
			    }				
				zones.addAll(stateZones);
				zone.setPmZoneStates(zones);
			}
			
			zoneSO = (IZoneSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IZoneSO.class);
			ZoneReqDTO reqDTO = new ZoneReqDTO();
			reqDTO.setPmZone(zone);
			ZoneResDTO resDTO = zoneSO.saveOrUpdateZone(reqDTO);	
			if (resDTO.isSuccessful()) {
				result = resDTO.isSuccessful();
				if (this.getManagedBean().getPmZone().isBrandNew()) {
					returnUrl = NAV_TO_MAIN;
					PmCommonUtil.showFacesMsg(
							FacesContext.getCurrentInstance(),
							FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_780098,
							new String[] {
									resDTO.getPmZone().getZoneCode(),
									resDTO.getPmZone().getZoneName()});					
				} else {
					redirectToZone(resDTO.getPmZone().getZoneId());
					PmCommonUtil.showFacesMsg(
							FacesContext.getCurrentInstance(),
							FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_780097,
							new String[] {
									resDTO.getPmZone().getZoneCode(),
									resDTO.getPmZone().getZoneName()});
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			zoneSO = null;
		}
		
		return returnUrl;
	}

	/**
	 * Maintain zone.
	 * 
	 * @param zoneId
	 *            (Long zone id(mandatory))
	 */
	public void maintainZone(Long zoneId) {
		zoneList();
		ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
		zoneReqDTO.setZoneId(zoneId);
		IZoneSO zoneSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
		ZoneResDTO resDTO = zoneSO.retrieveZoneStateByZoneId(zoneReqDTO);
		getManagedBean().setPmZone(resDTO.getPmZone());
		getManagedBean().setZoneStateList(resDTO.getPmZone().getPmZoneStates());
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO stateResDTO = stateSO.retrieveStateList();
		List<PmState> stList = stateResDTO.getStateList();
		if (resDTO.getPmZone().getPmZoneStates() != null
				&& !resDTO.getPmZone().getPmZoneStates().isEmpty()) {
			for (PmZone pmzone : getManagedBean().getPmZonesList()) {
				if (pmzone.getPmZoneStates() != null
						&& !pmzone.getPmZoneStates().isEmpty()) {
					for (PmZoneState state : pmzone.getPmZoneStates()) {
						for (PmState pmState : stList) {
							if (state.getPmState().getStateId().equals(pmState
									.getStateId())) {
								pmState.setExistingState(true);
							} else {
								pmState.setExistingState(false);
							}
						}
					}
				}
			}
		}
		this.getManagedBean().setStates(stList);
	}

	/**
	 * Gets the search bean data.
	 * 
	 * @param searchBean
	 *            (ISearchBean<PmZone> searchBean)
	 * @return ISearchBean<PmZone> searchBean
	 */
	private ISearchBean<PmZone> getSearchBeanData(ISearchBean<PmZone> searchBean) {
		ZoneSearchBean zSearchBean = this.getManagedBean().getZoneSearchBean();
		if (StringUtils.isNotBlank(zSearchBean.getZoneCode())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("zoneCode",
							SearchOperator.OPERATOR_LIKE.value(), zSearchBean
									.getZoneCode(), true));
		}
		
		if (StringUtils.isNotBlank(zSearchBean.getZoneName())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("zoneName",
							SearchOperator.OPERATOR_LIKE.value(), zSearchBean
									.getZoneName(), true));
		}
		
		if (StringUtils.isNotBlank(zSearchBean.getStateName())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmZoneStates.pmState.stateName",
							SearchOperator.OPERATOR_LIKE.value(), zSearchBean
									.getStateName(), true));
		}
		
		if (StringUtils.isBlank(zSearchBean.getStatus())) {			
			List<Integer> statusList = new ArrayList<Integer>();
			statusList.add(DbValueConst.RECORD_STATUS_ACTIVE);
			statusList.add(DbValueConst.RECORD_STATUS_SUSPENDED);

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_IN.value(), statusList));		

		} else {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getZoneSearchBean().getStatus()));				
		}

		return searchBean;
	}

	/**
	 * The Class SearchZonePagingDataModel.
	 */
	private class SearchZonePagingDataModel extends PagingDataModel<PmZone> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
		
		

		/**
		 * Instantiates a new search zone paging data model.
		 */
		public SearchZonePagingDataModel() {
			
			super("searchZones",getManagedBean().isLazy());
		}

		
		@Override
		public ISearchResultBean<PmZone> pageChangeListener(
				ISearchBean<PmZone> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IZoneSO zoneSO;

			try {
				ISearchBean<PmZone> searchBeanData = getSearchBeanData(searchBean);
				ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
				zoneReqDTO.setZoneSearchBean(searchBeanData);
				zoneSO = (IZoneSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IZoneSO.class);
				ZoneResDTO resDTO = zoneSO.searchZoneData(zoneReqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getZoneResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}
	}

	/**
	 * Adds the state.
	 */
	public void addState() {
		zoneList();
		if (getManagedBean().getPmState() == null) {
			this.getManagedBean().setPmState(new PmState());
		}
		if (this.getManagedBean().getSelectedStates() != null
				&& !this.getManagedBean().getSelectedStates().isEmpty()) {
			this.getManagedBean().setSelectedStates(null);
		}

		retrievePmStateList();
		RequestContext.getCurrentInstance()
		.execute("statelistpopup.show()");
		getManagedBean().setShowParentMsg(false);
		

	}

	/**
	 * Delete state.
	 */
	public void deleteState() {

		List<PmZoneState> zoneList = getManagedBean().getZoneStateList();
		List<PmZoneState> selectedList = getManagedBean().getSelectedZones();
		for (PmZoneState zonelist : zoneList) {
			if (zonelist.isSelected()) {
				selectedList.add(zonelist);
			}
		}
		if (zoneList.size() == selectedList.size()) {
			zoneList.clear();
		} else {
			for (PmZoneState zoneState : selectedList) {
				zoneList.remove(zoneState);
			}
		}
	}

	/**
	 * Ok zone action.
	 */
	public void okZoneAction() {
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateReqDTO reqStateDTO = new StateReqDTO();
		
		for (PmState state : getManagedBean().getSelectedStates()) {
			PmZoneState zoneState = new PmZoneState();
			reqStateDTO.setStateId(state.getStateId());
			StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
			PmState newState = resStateDTO.getPmState();
			zoneState.setPmState(newState);
			zoneState.setPmZone(getManagedBean().getPmZone());
			this.getManagedBean().getZoneStateList().add(zoneState);
		}
		if (getManagedBean().getSelectedStates().isEmpty()&&getManagedBean().getZoneStateList().isEmpty()) {
			throw new EpException(MessageConst.ERROR_770051);
		}
		RequestContext.getCurrentInstance()
		.execute("statelistpopup.hide()");
		getManagedBean().setShowParentMsg(true);
		
	}
	
	/**
	 * Cancle zone.
	 */
	public void cancleZone(){
		RequestContext.getCurrentInstance()
		.execute("statelistpopup.hide()");
		getManagedBean().setShowParentMsg(true);
		
	}

	/**
	 * Retrieve pm state list.
	 */
	private void retrievePmStateList() {
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = stateSO.retrieveStateList();

		List<PmState> stList = resDTO.getStateList();
		if (getManagedBean().getPmZonesList() != null
				&& !getManagedBean().getPmZonesList().isEmpty())
			for (PmZone zone : getManagedBean().getPmZonesList()) {
				if (zone.getRecordStatusFlg() ==1 && zone.getPmZoneStates() != null
						&& !zone.getPmZoneStates().isEmpty()) {
					for (PmZoneState zoneState : zone.getPmZoneStates()) {	
						stList.remove(zoneState.getPmState());
						
					}
				}
			}
		List<PmZoneState> selectStates = getManagedBean().getZoneStateList();
		for(PmZoneState zoneState : selectStates){
			stList.remove(zoneState.getPmState());
			
		}
		this.getManagedBean().setStates(stList);
	}

	

	/**
	 * Gets the status options.
	 * 
	 * @return the status options
	 */
	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getCommonStatusOptions();
	}

	/**
	 * Zone list.
	 */
	public void zoneList() {
		IZoneSO zoneSO = (IZoneSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IZoneSO.class);
		ZoneResDTO zoneResDTO = zoneSO.retrieveZoneList();
		zoneResDTO.setSuccessful(true);
		getManagedBean().setPmZonesList(zoneResDTO.getZoneList());

	}


	/**
	 * Gets the search zone.
	 * 
	 * @return the search zone
	 */
	public IPagingDataModel<PmZone> getSearchZone() {
		return searchZone;
	}

	/**
	 * Sets the search zone.
	 * 
	 * @param searchZone
	 *            the new search zone
	 */
	public void setSearchZone(IPagingDataModel<PmZone> searchZone) {
		this.searchZone = searchZone;
	}

	/**
	 * Checks if is exist zone code.
	 * 
	 * @param zoneCode
	 *            (String zonecode(mandatory))
	 * @return true, if is exist zone code
	 */
	private boolean isExistZoneCode(PmZone zone) {
		IZoneSO zoneSO = null;
		boolean isExist = false;
		try{
			ISearchBean<PmZone> searchBean =  new SearchBean<PmZone>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("zoneCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							zone.getZoneCode().toLowerCase().trim(),
							true));
			
			if(!zone.isBrandNew()){
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("zoneId",
								SearchOperator.OPERATOR_NOT_EQUALS.value(),
								zone.getZoneId()));
			}
			
			ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
			zoneReqDTO.setZoneSearchBean(searchBean);
			zoneSO = (IZoneSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IZoneSO.class);
			ZoneResDTO resDTO = zoneSO.searchZoneData(zoneReqDTO);
			if (resDTO.isSuccessful() 
					&& resDTO.getZoneList() != null 
					&& !resDTO.getZoneList().isEmpty()) {
				isExist = true;
			}
		}finally{
			zoneSO = null;
		}
		return isExist;
	}
	
	private boolean isExistZoneName(PmZone zone) {
		IZoneSO zoneSO = null;
		boolean isExist = false;
		try{
			ISearchBean<PmZone> searchBean =  new SearchBean<PmZone>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("zoneName",
							SearchOperator.OPERATOR_EQUALS.value(),
							zone.getZoneName().toLowerCase().trim(),
							true));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(), 
							DbValueConst.RECORD_STATUS_ACTIVE
			));
			
			if(!zone.isBrandNew()){
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("zoneId",
								SearchOperator.OPERATOR_NOT_EQUALS.value(),
								zone.getZoneId()));
			}
			
			ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
			zoneReqDTO.setZoneSearchBean(searchBean);
			zoneSO = (IZoneSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IZoneSO.class);
			ZoneResDTO resDTO = zoneSO.searchZoneData(zoneReqDTO);
			if (resDTO.isSuccessful() 
					&& resDTO.getZoneList() != null 
					&& !resDTO.getZoneList().isEmpty()) {
				isExist = true;
			}
		}finally{
			zoneSO = null;
		}
		return isExist;
	}
		

	/**
	 * Removes the disabled states.
	 */
	public void removeDisabledStates() {
		Iterator<PmState> stIterator = getManagedBean().getSelectedStates()
				.iterator();
		while (stIterator.hasNext()) {
			PmState state = stIterator.next();
			if (state.isExistingState()) {
				stIterator.remove();
			}
		}
	}
	
	/**
	 * Check zone state availability.
	 * 
	 * @return true, if successful
	 */
	public boolean checkZoneStateAvailability(){
		log.info(".................checkZoneStateAvailability..............");
		zoneList();
		PmZone currentZone = getManagedBean().getPmZone();
		List<PmState> assignedStates = new ArrayList<PmState>(); 
		for (PmZone zone : getManagedBean().getPmZonesList()) {
			if (!zone.getZoneCode().equals(currentZone.getZoneCode()) 
					&& zone.getRecordStatusFlg() ==1 
					&& zone.getPmZoneStates() != null
					&& !zone.getPmZoneStates().isEmpty()) {
				for (PmZoneState zoneState : zone.getPmZoneStates()) {	
					assignedStates.add(zoneState.getPmState());
				}
			}
	     }
//		for(PmState state:assignedStates){
//			log.info(".................state.getStateCode()="+state.getStateCode());
//		}
		for(PmZoneState currentZoneState : getManagedBean().getZoneStateList()){
			log.info(".................currentZoneState.getPmState()="+currentZoneState.getPmState());
			log.info(".................assignedStates.contains(currentZoneState.getPmState()="+assignedStates.contains(currentZoneState.getPmState()));
			if(assignedStates.contains(currentZoneState.getPmState())){
				return false;
			}
		}
		return true;
	}
	
	private void zoneValidation(PmZone zone){
		log.info("zoneValidation");
		if (getManagedBean().getZoneStateList().isEmpty()) {
			throw new EpException(MessageConst.ERROR_770051);
		}
		
		log.info(".................zone.getRecordStatusFlg()="+zone.getRecordStatusFlg());
		if (zone.getRecordStatusFlg() == 1) {//if changed to active				
			if (!checkZoneStateAvailability()){
				this.getManagedBean().getPmZone().setRecordStatusFlg(0);//revert back to inactive
				throw new EpException(MessageConst.ERROR_770151);

			}
	     }	
		
		if(DbValueConst.RECORD_STATUS_ACTIVE == zone.getRecordStatusFlg()){
			if (isExistZoneCode(getManagedBean().getPmZone())) {
				if(getManagedBean().getPmZone().isBrandNew()){
					throw new EpException(MessageConst.ERROR_770221,new String[]{
							PmCommonUtil.getResourceBundleLabels("lbl.pm.zone.code"),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.zone.code")});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.zone.code")
					});	
				}
			}
			
			if (isExistZoneName(getManagedBean().getPmZone())) {
				if(getManagedBean().getPmZone().isBrandNew()){
					throw new EpException(MessageConst.ERROR_770221,new String[]{
							PmCommonUtil.getResourceBundleLabels("lbl.pm.zone.name"),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.zone.name")});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.zone.name")
					});	
				}
			}
		}
	}
		
}
