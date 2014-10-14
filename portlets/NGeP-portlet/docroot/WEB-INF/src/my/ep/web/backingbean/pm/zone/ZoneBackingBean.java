package my.ep.web.backingbean.pm.zone;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
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
import my.ep.web.managedbean.pm.zone.ZoneManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;

@ManagedBean(name = "zoneBackingBean")
@ViewScoped
public class ZoneBackingBean extends BaseBackingBean<ZoneManagedBean> {
	private static Logger log = Logger.getLogger(ZoneBackingBean.class);
	private IPagingDataModel<PmZone> searchZone = new SearchZonePagingDataModel();
	boolean result = false;
	private boolean flag;

	public ZoneBackingBean() {
		retrievePmStateList();

	}

	public void searchZone() {
		searchZone.reloadData();
	}

	public String backToSearch() {

		return "/xhtml/pm/zonesearch/zoneSearchMain.xhtml";
	}

	public String addZone() {

		getManagedBean().setPmZone(new PmZone());
		getManagedBean().setZoneStateList(new ArrayList<PmZoneState>());
		getManagedBean().getPmZone().setRecordStatusFlg(null);

		getSelectionTypes();
		return "/xhtml/pm/zonesearch/zoneMaintenance.xhtml";
	}

	public void reset() {
		this.getManagedBean().setZoneCode(null);
		this.getManagedBean().setZoneName(null);
		this.getManagedBean().setStateName(null);
		this.getManagedBean().setStatus(null);
		this.getManagedBean().setPmZone(new PmZone());

	}

	public void saveZone() {
		ZoneReqDTO reqDTO = new ZoneReqDTO();
		IZoneSO zoneSO = (IZoneSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IZoneSO.class);
		if (getManagedBean().getZoneStateList().isEmpty()) {
			throw new EpException(MessageConst.ERROR_770051);
		}
		List<PmZoneState> zones = getManagedBean().getZoneStateList();

		PmZone zone = this.getManagedBean().getPmZone();
		if (zone.isBrandNew()) {

			List<PmZoneState> stateZones = getManagedBean().getZoneStateList();

			for (PmZoneState zoneSta : stateZones) {
				getManagedBean().setZoneStateList(stateZones);

				zoneSta.setPmZone(zone);

			}
			zones.addAll(stateZones);
		}

		zone.setPmZoneStates(zones);
		reqDTO.setPmZone(zone);

		ZoneResDTO resDTO = zoneSO.saveOrUpdateZone(reqDTO);
		if (resDTO.isSuccessful()) {
			result = resDTO.isSuccessful();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Zone submit successfully.", "Zone Name : "
									+ this.getManagedBean().getPmZone()
											.getZoneName()));

		}
	}

	public String editZone(PmZone zone) {
		ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
		zoneReqDTO.setZoneId(zone.getZoneId());
		IZoneSO zoneSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
		ZoneResDTO resDTO = zoneSO.retrieveZoneStateByZoneId(zoneReqDTO);
		getManagedBean().setPmZone(resDTO.getPmZone());
		getManagedBean().setZoneStateList(resDTO.getPmZone().getPmZoneStates());

		this.getManagedBean().setZoneStateList(
				resDTO.getPmZone().getPmZoneStates());

		return "/xhtml/pm/zonesearch/zoneMaintenance.xhtml";

	}

	private class SearchZonePagingDataModel extends PagingDataModel<PmZone> {
		private static final long serialVersionUID = 1L;

		public SearchZonePagingDataModel() {
			super("searchZone");
		}

		public ISearchResultBean<PmZone> pageChangeListener(
				ISearchBean<PmZone> searchBean) {
			IZoneSO zoneSO;
			try {
				ZoneReqDTO zoneReqDTO = new ZoneReqDTO();

				if (getManagedBean().getZoneCode() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("zoneCode",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getZoneCode(), true));
				}
				if (getManagedBean().getZoneName() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("zoneName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getZoneName(), true));
				}
				if (getManagedBean().getStateName() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"pmZoneStates.pmState.stateName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getStateName(), true));
				}
				if (getManagedBean().getStatus() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("recordStatusFlg",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getStatus()));
				}
				zoneReqDTO.setZoneSearchBean(searchBean);
				zoneSO = (IZoneSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IZoneSO.class);
				ZoneResDTO resDto = zoneSO.searchZoneData(zoneReqDTO);
				if (resDto.isSuccessful()) {
					return resDto.getZoneResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private void retrievePmStateList() {
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = stateSO.retrieveZoneStateList();
		
		this.getManagedBean().setStates(resDTO.getStateList());
	}

	public void addState() {

		if (getManagedBean().getPmState() == null) {
			this.getManagedBean().setPmState(new PmState());
		}
		getManagedBean().setSelectedStates(null);

		getSelectionTypes();
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

	public void selectAll(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<PmZoneState> pmZoneList = getManagedBean().getZoneStateList();
		if (pmZoneList != null) {
			for (PmZoneState zoneState : pmZoneList) {
				if (flag) {
					zoneState.setSelected(true);
				} else {
					zoneState.setSelected(false);
				}
			}
		}
	}

	public void okZoneAction() {
		List<PmZoneState> zoneList = getManagedBean().getZoneStateList();
		List<String> itemIds = new ArrayList<String>();
		for (PmState state : getManagedBean().getSelectedStates()) {
			PmZoneState zoneState = new PmZoneState();
			zoneState.setPmState(state);

			if (this.getManagedBean().getZoneStateList().isEmpty()) {
				this.getManagedBean().getZoneStateList().add(zoneState);
			} else {
				for (PmZoneState zone : this.getManagedBean()
						.getZoneStateList()) {
					itemIds.add(zone.getPmState().getStateCode());
				}
				if (itemIds.size() > 0) {
					if (itemIds.contains(state.getStateId())) {

					} else {
						zoneList.add(zoneState);
						this.getManagedBean().setZoneStateList(zoneList);
					}
				}
			}
		}

	}

	private void getSelectionTypes() {
		SelectionType[] type = SelectionType.values();
		List<SelectItem> selectionTypes = new ArrayList<SelectItem>();
		for (SelectionType selectionType : type) {
			selectionTypes.add(new SelectItem(selectionType.value(),
					selectionType.name()));
		}
		this.getManagedBean().setSelections(selectionTypes);
	}
	public SelectItem[] getStatusOptions() {
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

	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
