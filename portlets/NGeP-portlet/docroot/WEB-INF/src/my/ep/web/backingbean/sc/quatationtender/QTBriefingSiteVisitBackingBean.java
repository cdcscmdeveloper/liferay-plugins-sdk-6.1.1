package my.ep.web.backingbean.sc.quatationtender;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBreifingZoneRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefingRegistration;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefingRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.AddressType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "qTBriefingSiteVisitBackingBean")
@RequestScoped
public class QTBriefingSiteVisitBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	private static final String NAV_TO_BSV_ATTENDANCE_PAGE = "/xhtml/sc/quatationtender/briefingsitevisit/briefingAttendance.xhtml";

	private static final Logger log = EpLogger
			.getLogger(QTBriefingSiteVisitBackingBean.class);

	IAddressSO addressSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public QTBriefingSiteVisitBackingBean() {
		retrievePmStateList();
	}

	/**
	 * 
	 */
	public void retrievePmStateList() {
		debugLogger("retrievePmStateList");
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(1L);

		StateResDTO resDTO = stateSO.retriveStatesListByCountryID(reqDTO);

		List<PmState> list = resDTO.getStateList();
		this.getManagedBean().setStates(list);
		// retrievePmDistrictList();
	}
	
	public void retrievePmDistrictList(AjaxBehaviorEvent event) {
		debugLogger("retrievePmDistrictList");
		Long stateId = (Long) event.getComponent().getAttributes().get("value");
		log.info("State Id : " + stateId);
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(stateId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
		this.getManagedBean().setDistricts(resDTO.getDistrictList());
	}
	
	public void retrievePmCityList(AjaxBehaviorEvent event) {
		Long districtId = (Long) event.getComponent().getAttributes()
				.get("value");
		log.info("District Id : " + districtId);
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		this.getManagedBean().setCitys(resDTO.getCityList());
	}

	public void bsvZoneTabChange(TabChangeEvent evnt) {
		debugLogger("bsvZoneTabChange");
		ScQt scQt = getManagedBean().getQt();

		BriefingZoneBean briefingZoneBean = (BriefingZoneBean) evnt.getData();

		TplSpecZoneDtl tplSpecZoneDtl = briefingZoneBean.getTplSpecZoneDtl();
		getManagedBean().setSelectedZone(tplSpecZoneDtl);
		List<ScQtBsvDtl> briefings = null;
		List<ScQtBsvDtl> siteVisit = null;
		briefings = retrieveBriefingSiteVisitDtlsByZoneNQtId(scQt.getQtId(),
				tplSpecZoneDtl.getSpecZoneId(), "B");
		this.getManagedBean().setBriefings(briefings);
		siteVisit = retrieveBriefingSiteVisitDtlsByZoneNQtId(scQt.getQtId(),
				tplSpecZoneDtl.getSpecZoneId(), "S");

		this.getManagedBean().setSiteBriefings(siteVisit);

		List<BriefingZoneBean> zoneBean = getManagedBean()
				.getBriefingZoneBean();
		for (int i = 0; i < zoneBean.size(); i++) {
			if (zoneBean
					.get(i)
					.getTplSpecZoneDtl()
					.getZoneName()
					.equalsIgnoreCase(
							briefingZoneBean.getTplSpecZoneDtl().getZoneName())) {
				BriefingZoneBean bean = new BriefingZoneBean(tplSpecZoneDtl,
						briefings, siteVisit);
				getManagedBean().getBriefingZoneBean().remove(i);
				getManagedBean().getBriefingZoneBean().add(bean);
				break;
			}
		}
		initializeRemarks(scQt.isZonal());
	}

	/**
	 * @param qtId
	 * @param specZoneId
	 * @param attendenceType
	 * @return
	 */
	private List<ScQtBsvDtl> retrieveBriefingSiteVisitDtlsByZoneNQtId(
			Long qtId, Long zoneId, String bsvType) {
		debugLogger("retrieveBriefingSiteVisitDtlsByZoneNQtId");
		IQTBriefingSO qTBriefingSO;

		qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
		BriefingReqDTO reqDTO = new BriefingReqDTO();
		reqDTO.setQtId(qtId);
		reqDTO.setSpecZoneId(zoneId);
		reqDTO.setBriefingType(bsvType);
		BriefingResDTO briefingResDTO = qTBriefingSO
				.retrieveBriefingSiteVisitList(reqDTO);
		this.getManagedBean().setRemarks(briefingResDTO.getBriefingRemarks());
		return briefingResDTO.getBriefingResultBean().getResultList();
	}

	public void initializeRemarks(boolean isZonal) {
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		ScRemark remark = null;
		if (isZonal) {
			remark = new ScQtBreifingZoneRemark();
		} else {
			remark = new ScQtBriefingRemark();
		}
		remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setCreatedUser(facesScopes.getUserSessionBean());
		PmStatus status = new PmStatus();
		status.setStatusId(QTStatusConst.QT_BRIEFING_SITE_VISIT);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO
					.retrieveStatusByStatusId(statusReqDTO);
			remark.setPmStatus(statusResDTO.getPmStatus());
			getManagedBean().getRemarks().add(remark);
		}
	}

	public void processChecked(AjaxBehaviorEvent e) {
		System.out.println("====enter process checked===="
				+ getManagedBean().getSelectedBoolean1());
		Boolean id = (Boolean) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
	}

	public void addZoneBriefingDetails(String briefingZoneName) {
		debugLogger("addBriefingDetails");
		ScQtBsvDtl bsvDtl = new ScQtBsvDtl();
		bsvDtl.setBsvType("B");
		bsvDtl.setAddress(new PmAddress());
		bsvDtl.getAddress().setPmState(new PmState());
		bsvDtl.getAddress().setPmCity(new PmCity());
		bsvDtl.getAddress().setPmDistrict(new PmDistrict());
		bsvDtl.getAddress().setPmCountry(new PmCountry());

		getManagedBean().setQtBriefing(bsvDtl);
		getManagedBean().setBriefingZoneName(briefingZoneName);
	}

	public void addZoneSiteDetails(String sitevisitZoneName) {
		debugLogger("addZoneSiteDetails");
		ScQtBsvDtl bsvDtl = new ScQtBsvDtl();
		bsvDtl.setBsvType("S");
		bsvDtl.setAddress(new PmAddress());
		bsvDtl.getAddress().setPmState(new PmState());
		bsvDtl.getAddress().setPmCity(new PmCity());
		bsvDtl.getAddress().setPmDistrict(new PmDistrict());
		bsvDtl.getAddress().setPmCountry(new PmCountry());

		getManagedBean().setQtSiteBriefing(bsvDtl);
		getManagedBean().setBriefingZoneName(sitevisitZoneName);
	}

	public void addToBriefingListByZone() {
		debugLogger("addToBriefingListByZone");

		ScQtBsvDtl selectedBriefing = this.getManagedBean().getQtBriefing();

		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setUserId(2L);
		reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
		List<PmAddressType> list = resDTO.getAddressTypeList();

		selectedBriefing.setAddress(list.get(0).getPmAddress());

		// Setting the pm status for qt details
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(1l);

		IStatusSO statusSO;
		PmStatus status = null;
		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusResDTO pmStatus = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			status = pmStatus.getPmStatus();
		} catch (Exception e) {

		} finally {
			statusSO = null;
		}
		selectedBriefing.setBsvStatus(status);

		List<BriefingZoneBean> beans = getManagedBean().getBriefingZoneBean();
		for (BriefingZoneBean briefingZoneBean : beans) {
			if (briefingZoneBean
					.getTplSpecZoneDtl()
					.getZoneName()
					.equalsIgnoreCase(
							this.getManagedBean().getBriefingZoneName())) {
				List<ScQtBsvDtl> briefings = briefingZoneBean.getBriefingList();
				if (null == briefings) {
					briefings = new ArrayList<ScQtBsvDtl>();
				}
				briefings.add(selectedBriefing);
				briefingZoneBean.setBriefingList(briefings);
				break;
			}
		}
	}

	public void addToSitevisitListByZone() {
		debugLogger("addToSitevisitListByZone");
		ScQtBsvDtl selectedSiteBriefing = this.getManagedBean()
				.getQtSiteBriefing();

		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setUserId(2L);
		reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
		List<PmAddressType> list = resDTO.getAddressTypeList();

		selectedSiteBriefing.setAddress(list.get(0).getPmAddress());

		// Setting the pm status for qt details
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(1l);

		IStatusSO statusSO;
		PmStatus status = null;
		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusResDTO pmStatus = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			status = pmStatus.getPmStatus();
		} catch (Exception e) {

		} finally {
			statusSO = null;
		}
		selectedSiteBriefing.setBsvStatus(status);

		List<BriefingZoneBean> beans = getManagedBean().getBriefingZoneBean();
		for (BriefingZoneBean briefingZoneBean : beans) {
			if (briefingZoneBean
					.getTplSpecZoneDtl()
					.getZoneName()
					.equalsIgnoreCase(
							this.getManagedBean().getBriefingZoneName())) {
				List<ScQtBsvDtl> sitevisit = briefingZoneBean
						.getSitevisitList();
				if (null == sitevisit) {
					sitevisit = new ArrayList<ScQtBsvDtl>();
				}
				sitevisit.add(selectedSiteBriefing);
				briefingZoneBean.setSitevisitList(sitevisit);
				break;
			}
		}
	}

	public void addToSiteVisitZoneDtl() {
		debugLogger("addToSiteVisitZoneDtl");
	}

	public void addBriefingDetails() {
		debugLogger("addBriefingDetails");
		ScQtBsvDtl bsvDtl = new ScQtBsvDtl();
		bsvDtl.setBsvType("B");
		bsvDtl.setAddress(new PmAddress());
		bsvDtl.getAddress().setPmState(new PmState());
		bsvDtl.getAddress().setPmCity(new PmCity());
		bsvDtl.getAddress().setPmDistrict(new PmDistrict());
		bsvDtl.getAddress().setPmCountry(new PmCountry());

		getManagedBean().setQtBriefing(bsvDtl);

	}

	public void addSiteDetails() {
		debugLogger("addSiteDetails");
		ScQtBsvDtl bsvDtl = new ScQtBsvDtl();
		bsvDtl.setBsvType("S");
		bsvDtl.setAddress(new PmAddress());
		bsvDtl.getAddress().setPmState(new PmState());
		bsvDtl.getAddress().setPmCity(new PmCity());
		bsvDtl.getAddress().setPmDistrict(new PmDistrict());
		bsvDtl.getAddress().setPmCountry(new PmCountry());

		getManagedBean().setQtSiteBriefing(bsvDtl);
	}

	public void addToSiteVisitDtl() {
		debugLogger("addToSiteVisitDtl");

		List<ScQtBsvDtl> list = getManagedBean().getSiteBriefings();
		if (list == null) {
			list = new ArrayList<ScQtBsvDtl>();
		}
		for (ScQtBsvDtl scQtBsvDtl : getManagedBean()
				.getSelectedBriefingDetails()) {
			scQtBsvDtl.setBsvType("A");
			list.add(scQtBsvDtl);
		}
		getManagedBean().setSiteBriefings(list);
	}

	public void addToBriefingList() {
		debugLogger("addToBriefingList");
		List<ScQtBsvDtl> brefings = this.getManagedBean().getBriefings();
		ScQtBsvDtl selectedBrefing = this.getManagedBean().getQtBriefing();

		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setUserId(2L);
		reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
		List<PmAddressType> list = resDTO.getAddressTypeList();

		selectedBrefing.setAddress(list.get(0).getPmAddress());

		// Setting the pm status for qt details
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(1l);

		IStatusSO statusSO;
		PmStatus status = null;
		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusResDTO pmStatus = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			status = pmStatus.getPmStatus();
		} catch (Exception e) {

		} finally {
			statusSO = null;
		}
		selectedBrefing.setBsvStatus(status);
		if (null == brefings) {
			brefings = new ArrayList<ScQtBsvDtl>();
		}
		if (null != selectedBrefing) {
			brefings.add(selectedBrefing);
		}
		this.getManagedBean().setBriefings(brefings);
	}

	public void addToSiteBriefingList() {
		debugLogger("addToSiteBriefingList");
		List<ScQtBsvDtl> siteBriefings = this.getManagedBean()
				.getSiteBriefings();
		ScQtBsvDtl selectedSiteBriefing = this.getManagedBean()
				.getQtSiteBriefing();

		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setUserId(2L);
		reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
		List<PmAddressType> list = resDTO.getAddressTypeList();
		selectedSiteBriefing.setAddress(list.get(0).getPmAddress());

		// Setting the pm status for qt details
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(1l);

		IStatusSO statusSO;
		PmStatus status = null;
		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusResDTO pmStatus = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			status = pmStatus.getPmStatus();
		} catch (Exception e) {

		} finally {
			statusSO = null;
		}

		selectedSiteBriefing.setBsvStatus(status);
		if (null == siteBriefings) {
			siteBriefings = new ArrayList<ScQtBsvDtl>();
		}
		if (null != selectedSiteBriefing) {
			siteBriefings.add(selectedSiteBriefing);
		}
		this.getManagedBean().setSiteBriefings(siteBriefings);
	}

	public void deleteZoneBriefingDetails() {
		debugLogger("deleteZoneBriefingDetails");
		List<ScQtBsvDtl> beans = getManagedBean().getBriefingZoneBean().get(0)
				.getBriefingList();
		List<ScQtBsvDtl> selectList = getManagedBean()
				.getSelectedBriefingDetails();
		if (beans.size() == selectList.size()) {
			beans.clear();
		} else {
			for (ScQtBsvDtl scQtBriefing : selectList) {
				beans.remove(scQtBriefing);
			}
		}
	}

	public void deleteBriefingDetails() {
		debugLogger("deleteBriefingDetails");
		List<ScQtBsvDtl> beans = getManagedBean().getBriefings();
		List<ScQtBsvDtl> selectList = getManagedBean()
				.getSelectedBriefingDetails();
		if (beans.size() == selectList.size()) {
			beans.clear();
		} else {
			for (ScQtBsvDtl scQtBriefing : selectList) {
				beans.remove(scQtBriefing);
			}
		}
	}

	public String editAction(ScQtBsvDtl scQtBriefing) {
		debugLogger("editAction");
		IQTBriefingSO qTBriefingSO = null;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			if (scQtBriefing != null) {
				BriefingReqDTO reqDTO = new BriefingReqDTO();

				reqDTO.setQtBriefingId(scQtBriefing.getQtBsvDtlId());

				BriefingResDTO resDTO = qTBriefingSO
						.retrieveBriefingRegistrationList(reqDTO);

				List<ScQtBsvRegistration> briefRegList = resDTO
						.getBriefingRegResultBean().getResultList();

				List<ScQtBsvRegistration> registrationList = new ArrayList<ScQtBsvRegistration>();
				for (ScQtBsvRegistration scqtbriefReg : briefRegList) {
					registrationList.add(scqtbriefReg);
				}
				this.getManagedBean().setBriefingRegistrationList(
						registrationList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qTBriefingSO = null;
		}
		return NAV_TO_BSV_ATTENDANCE_PAGE + "?faces-redirect=true";
	}

	public void onRowSelect(SelectEvent event) {
		ScQtBriefingRegistration briefingRegistration = (ScQtBriefingRegistration) event
				.getObject();
		this.getManagedBean().setBriefingAttendanceList(
				briefingRegistration.getScQtBriefingAttendances());
	}

	public void addSupplierDetails() {
		debugLogger("addSupplierDetails");
		try {
			ScQtBsvDtl scQtBsvDtl = getManagedBean().getQtBriefing();
			// ScQtBriefing scQtBriefing = getManagedBean().getQtBriefing();
			ScQtBsvRegistration briefReg = new ScQtBsvRegistration();
			// ScQtBriefingRegistration briefReg = new
			// ScQtBriefingRegistration();
			SmSupplier smSupplier = new SmSupplier();
			SmMofAccount account = new SmMofAccount();
			List<SmMofAccount> accounts = new ArrayList<SmMofAccount>();
			accounts.add(account);
			smSupplier.setSmMofAccounts(accounts);
			briefReg.setSupplier(smSupplier);
			briefReg.setBsvDtl(scQtBsvDtl);

			List<ScQtBsvRegistration> attendaceforComp = getManagedBean()
					.getBriefingRegistrationList();

			if (null == attendaceforComp) {
				attendaceforComp = new ArrayList<ScQtBsvRegistration>();
			}

			attendaceforComp.add(briefReg);

			this.getManagedBean().setBriefingRegistrationList(attendaceforComp);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteSupplierDetails() {
		this.getManagedBean().getSelectedbriefRegistration().setDeleted(true);
	}

	public String briefingBackAction() {
		debugLogger("briefingBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}
}
