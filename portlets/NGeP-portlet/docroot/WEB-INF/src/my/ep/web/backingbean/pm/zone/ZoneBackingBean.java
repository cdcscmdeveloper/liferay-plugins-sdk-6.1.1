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
import my.ep.web.bean.pm.ZoneSearchBean;
import my.ep.web.managedbean.pm.zone.ZoneManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;

@ManagedBean(name = "zoneBackingBean")
@ViewScoped
public class ZoneBackingBean extends BaseBackingBean<ZoneManagedBean> {
	private static Logger log = Logger.getLogger(ZoneBackingBean.class);
	public static final String NAV_TO_ADD = "/xhtml/pm/zonesearch/zoneMaintenance.xhtml";
	public static final String NAV_TO_MAIN = "/xhtml/pm/zonesearch/zoneSearchMain.xhtml";
	private IPagingDataModel<PmZone> searchZone = new SearchZonePagingDataModel();
	private boolean flag;
	private boolean result;
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public void preLoading() {
		log.info("preLoading");
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
	}

	public ZoneBackingBean() {
		retrievePmStateList();

	}

	public void searchZone() {
		searchZone.reloadData();
	}

	public String backToSearch() {

		return NAV_TO_MAIN;
	}

	public String addZone() {

		getManagedBean().setPmZone(new PmZone());
		getManagedBean().setZoneStateList(new ArrayList<PmZoneState>());
		getManagedBean().getPmZone().setRecordStatusFlg(null);

		getSelectionTypes();
		return NAV_TO_ADD;
	}

	public void reset() {

		this.getManagedBean().setPmZone(new PmZone());

	}

	public void saveZone() {
		log.info("SavingZone");
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

		return NAV_TO_ADD;

	}

	private ISearchBean<PmZone> getSearchBeanData(ISearchBean<PmZone> searchBean) {
		log.info("getSearchBeanData - Start");
		ZoneSearchBean zSearchBean = this.getManagedBean().getZoneSearchBean();

		if (zSearchBean.getZoneCode() != null
				&& zSearchBean.getZoneCode().trim().length() > 0) {
			log.info("+ zoneCode " + zSearchBean.getZoneCode());
			/*SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("zoneCode");
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value(),true);
			bean.setSearchValue(zSearchBean.getZoneCode());
			searchBean.getDataFilterBeans().add(bean);*/
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("zoneCode",
							SearchOperator.OPERATOR_LIKE.value(),
							zSearchBean.getZoneCode(), true));
		}
		if (zSearchBean.getZoneCode() != null
				&& zSearchBean.getZoneName().trim().length() > 0) {
			log.info("+ zoneName " + zSearchBean.getZoneName());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("zoneName");
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			bean.setSearchValue(zSearchBean.getZoneName());
			searchBean.getDataFilterBeans().add(bean);
		}

		if (zSearchBean.getStatus() != null) {
			log.info("+ status " + zSearchBean.getStatus());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(zSearchBean.getStatus());
			searchBean.getDataFilterBeans().add(bean);
		}

		log.info("getSearchBeanData - End");
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

	private void retrievePmStateList() {
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = stateSO.retrieveStateList();

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

	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getStatusOptions();
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
