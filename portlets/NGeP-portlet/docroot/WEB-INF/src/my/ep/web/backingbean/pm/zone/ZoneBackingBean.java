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

import org.apache.log4j.Logger;

@ManagedBean(name = "zoneBackingBean")
@ViewScoped
public class ZoneBackingBean extends BaseBackingBean<ZoneManagedBean> {
	private static Logger log = Logger.getLogger(ZoneBackingBean.class);
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/zonesearch/zoneMaintenance";
	public static final String NAV_TO_MAIN = "/xhtml/pm/zonesearch/zoneSearchMain";

	private IPagingDataModel<PmZone> searchZone = new SearchZonePagingDataModel();

	boolean result = false;
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public ZoneBackingBean() {

	}

	public void preLoadingZone() {		
		this.resetZoneSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * reset search bean fields
	 */
	public void resetZoneSearchBean() {
		log.info("ZoneSearchBean");
		this.getManagedBean().setZoneSearchBean(new ZoneSearchBean());
		searchZone = new SearchZonePagingDataModel();
	}

	public void searchZone() {
		searchZone.reloadData();
	}

	public String backToSearch() {

		return NAV_TO_MAIN;
	}

	public void initialZone() {
		getManagedBean().setPmZone(new PmZone());
		getManagedBean().setZoneStateList(new ArrayList<PmZoneState>());
		
	}
	
	public String redirectToZone(Long id)
			throws Exception {

		if (id != null && id > 0) {
			maintainZone(id);
			
		} else {
			initialZone();
			
		}
		return NAV_TO_MAINTAIN;
	}

	public String saveZone() {
		ZoneReqDTO reqDTO = new ZoneReqDTO();
		IZoneSO zoneSO = (IZoneSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IZoneSO.class);
		if (getManagedBean().getZoneStateList().isEmpty()) {
			throw new EpException(MessageConst.ERROR_770051);
		}
		List<PmZoneState> zones = new ArrayList<PmZoneState>();
		PmZone zone = this.getManagedBean().getPmZone();
		
		if (zone.isBrandNew()) {			
			if (isExistZoneCode(getManagedBean().getPmZone().getZoneCode())) {
				throw new EpException(MessageConst.ERROR_770134);

			}

			List<PmZoneState> stateZones = getManagedBean().getZoneStateList();

			for (PmZoneState zoneSta : stateZones) {

				zoneSta.setPmZone(zone);

			}
			zones.addAll(stateZones);
			zone.setPmZoneStates(zones);
		}

		reqDTO.setPmZone(zone);

		ZoneResDTO resDTO = zoneSO.saveOrUpdateZone(reqDTO);
		if (resDTO.isSuccessful()) {
			result = resDTO.isSuccessful();

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Zone", this
							.getManagedBean().getPmZone().getZoneName()
							+ " is Successfully saved"));

		}
		return NAV_TO_MAIN;
	}

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
							if (state.getPmState().getStateId() == pmState
									.getStateId()) {
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

	private ISearchBean<PmZone> getSearchBeanData(ISearchBean<PmZone> searchBean) {

		ZoneSearchBean zSearchBean = this.getManagedBean().getZoneSearchBean();
		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		if (zSearchBean.getZoneCode() != null
				&& zSearchBean.getZoneCode().trim().length() > 0) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("zoneCode",
							SearchOperator.OPERATOR_LIKE.value(), zSearchBean
									.getZoneCode(), true));
			isEmpty = false;
		}
		if (zSearchBean.getZoneCode() != null
				&& zSearchBean.getZoneName().trim().length() > 0) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("zoneName",
							SearchOperator.OPERATOR_LIKE.value(), zSearchBean
									.getZoneName(), true));
			isEmpty = false;
		}
		if (zSearchBean.getStateName() != null
				&& zSearchBean.getStateName().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmZoneStates.pmState.stateName",
							SearchOperator.OPERATOR_LIKE.value(), zSearchBean
									.getStateName(), true));
			isEmpty = false;
		}

		if (isEmpty && !isSearchable) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getZoneSearchBean().getStatus()));
		}
		return searchBean;
	}

	private class SearchZonePagingDataModel extends PagingDataModel<PmZone> {
		private static final long serialVersionUID = 1L;

		public SearchZonePagingDataModel() {
			super("searchZone");
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

	}

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

	public void okZoneAction() {
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateReqDTO reqStateDTO = new StateReqDTO();
		List<PmZoneState> selectedStateList = getManagedBean()
				.getSelectedZones();
		for (PmState state : getManagedBean().getSelectedStates()) {
			PmZoneState zoneState = new PmZoneState();
			reqStateDTO.setStateId(state.getStateId());
			StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
			PmState newState = resStateDTO.getPmState();
			zoneState.setPmState(newState);
			zoneState.setPmZone(getManagedBean().getPmZone());
			this.getManagedBean().getZoneStateList().add(zoneState);
		}
		for (PmZoneState zstate : selectedStateList) {
			if (getManagedBean().getZoneStateList().size() > 0) {
				if (getManagedBean().getZoneStateList().contains(zstate.getPmState())) {
					throw new EpException(MessageConst.ERROR_770135);
				} 
			}
		}

	}

	private void retrievePmStateList() {
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = stateSO.retrieveStateList();

		List<PmState> stList = resDTO.getStateList();
		if (getManagedBean().getPmZonesList() != null
				&& !getManagedBean().getPmZonesList().isEmpty())
			for (PmZone zone : getManagedBean().getPmZonesList()) {
				if (zone.getPmZoneStates() != null
						&& !zone.getPmZoneStates().isEmpty()) {
					for (PmZoneState state : zone.getPmZoneStates()) {
						stList.remove(state.getPmState());
						/*for (PmState pmState : stList) {
							if (state.getPmState().getStateId() == pmState
									.getStateId()) {
								pmState.setExistingState(true);
							}

						}*/
					}
				}
			}
		List<PmZoneState> selectStates = getManagedBean().getZoneStateList();
		for(PmZoneState zoneState : selectStates){
			stList.remove(zoneState.getPmState());
			/*for(PmState pmState : stList){
				if(zoneState.getPmState().getStateId().equals(pmState.getStateId())){
					pmState.setExistingState(true);
				}
			}*/
		}
		this.getManagedBean().setStates(stList);
	}

	

	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getStatusOptions();
	}

	public void zoneList() {
		IZoneSO zoneSO = (IZoneSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IZoneSO.class);
		ZoneResDTO zoneResDTO = zoneSO.retrieveZoneList();
		zoneResDTO.setSuccessful(true);
		getManagedBean().setPmZonesList(zoneResDTO.getZoneList());

	}

	public SelectItem[] getRecordStatusOptions() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(1, "Active");
		items[1] = new SelectItem(0, "Inactive");
		return items;
	}

	public IPagingDataModel<PmZone> getSearchZone() {
		return searchZone;
	}

	public void setSearchZone(IPagingDataModel<PmZone> searchZone) {
		this.searchZone = searchZone;
	}

	public boolean isExistZoneCode(String zoneCode) {
		IZoneSO zoneSO = (IZoneSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IZoneSO.class);
		ZoneReqDTO reqDTO = new ZoneReqDTO();
		reqDTO.setZoneCode(zoneCode);
		ZoneResDTO resDTO = zoneSO.retrieveZoneByZoneCode(reqDTO);

		if (resDTO.getPmZone() != null) {
			return true;
		} else {
			return false;
		}
	}

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
}
