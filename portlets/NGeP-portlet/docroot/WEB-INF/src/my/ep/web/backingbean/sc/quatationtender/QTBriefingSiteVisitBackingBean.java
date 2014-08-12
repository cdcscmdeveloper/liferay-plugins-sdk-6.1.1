package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

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

	private int index;

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public QTBriefingSiteVisitBackingBean() {

	}

	/**
	 * 
	 */
	public void retrievePmStateList() {
		debugLogger("retrievePmStateList");
		if (null == this.getManagedBean().getStates()) {
			IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IStateSO.class);
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(1L);

			StateResDTO resDTO = stateSO.retriveStatesListByCountryID(reqDTO);

			List<PmState> list = resDTO.getStateList();
			this.getManagedBean().setStates(list);

		}
		retrievePmDistrictList();
	}

	public void retrievePmDistrictList() {
		debugLogger("retrievePmDistrictList");
		if (null != this.getManagedBean().getQtBriefing()) {
			Long stateId = this.getManagedBean().getQtBriefing().getAddress()
					.getPmState().getStateId();
			this.getManagedBean().setDistricts(null);
			if (null != stateId) {
				DistrictReqDTO reqDTO = new DistrictReqDTO();
				reqDTO.setStateId(stateId);
				IDistrictSO reqSO = (IDistrictSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);
				DistrictResDTO resDTO = reqSO
						.retriveDistrictListByStateID(reqDTO);
				this.getManagedBean().setDistricts(resDTO.getDistrictList());
				
			}
		}
		retrievePmCityList();
	}

	public void retrievePmCityList() {
		if (null != this.getManagedBean().getQtBriefing()) {
			Long districtId = this.getManagedBean().getQtBriefing()
					.getAddress().getPmDistrict().getDistrictId();
			this.getManagedBean().setCitys(null);
			if (null != districtId) {
				CityReqDTO reqDTO = new CityReqDTO();
				reqDTO.setDistrictId(districtId);
				ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICitySO.class);
				CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
				this.getManagedBean().setCitys(resDTO.getCityList());
			}
		}
	}

	public void processChecked(AjaxBehaviorEvent e) {
		Boolean selectedValue = (Boolean) e.getComponent().getAttributes()
				.get("value");
		BriefingZoneBean zoneBean = this.getManagedBean().getBsvDtlBean();
		if (selectedValue) {
			zoneBean.getScQtBsv().setAllSiteVisitReq(false);
			zoneBean.getScQtBsv().setMinSiteVisitAttend(0);
		}
		this.getManagedBean().setBsvDtlBean(zoneBean);

	}

	public void addBsvDetails(String bsvType) {
		debugLogger("addBriefingDetails");
		this.getManagedBean().setRequired(true);
		this.getManagedBean().setBsvType(bsvType);
		ScQtBsvDtl bsvDtl = new ScQtBsvDtl();
		bsvDtl.setBsvType(bsvType);
		PmAddress address = new PmAddress();
		PmCity pmCity = new PmCity();
		address.setPmCity(pmCity);
		PmState pmState = new PmState();
		address.setPmState(pmState);
		address.setPmDistrict(new PmDistrict());
		bsvDtl.setAddress(address);
		PmStatus pmStatus = new PmStatus();
		pmStatus.setStatusId(QTStatusConst.BSV_PENDING);
		bsvDtl.setBsvStatus(pmStatus);
		this.getManagedBean().setQtBriefing(bsvDtl);

	}

	public void addToBriefingList() {
		debugLogger("addToBriefingList");
		this.getManagedBean().setRequired(false);
		String bsvType = this.getManagedBean().getBsvType();
		BriefingZoneBean briefingBean = this.getManagedBean().getBsvDtlBean();
		ScQtBsvDtl bsvDetail = this.getManagedBean().getQtBriefing();
		// Checking for duplication.. if it is add flow..
		if (!bsvDetail.isSelected()) {

			if (null != briefingBean.getBriefingList()
					&& briefingBean.getBriefingList().contains(bsvDetail)) {
				throw new EpException(MessageConst.ERROR_760099);
			} else if (null != briefingBean.getSitevisitList()
					&& briefingBean.getSitevisitList().contains(bsvDetail)) {
				throw new EpException(MessageConst.ERROR_760099);
			}
			bsvDetail.setScQtBsv(briefingBean.getScQtBsv());

		}
		if (SourcingConstants.BSV_TYPE_BRIEFING.equalsIgnoreCase(bsvType)) {
			List<ScQtBsvDtl> briefingDetails = briefingBean.getBriefingList();
			if (null == briefingDetails) {
				briefingDetails = new ArrayList<>();
			}
			if (!briefingDetails.contains(bsvDetail)) {
				briefingDetails.add(bsvDetail);
			} else {
				briefingDetails.remove(this.getManagedBean().getBsvIndex());
				briefingDetails.add(this.getManagedBean().getBsvIndex(),
						bsvDetail);
			}
			briefingBean.setBriefingList(briefingDetails);
		} else if (SourcingConstants.BSV_TYPE_SITEVISIT
				.equalsIgnoreCase(bsvType)) {
			List<ScQtBsvDtl> siteVisitDetails = briefingBean.getSitevisitList();
			if (null == siteVisitDetails) {
				siteVisitDetails = new ArrayList<>();
			}
			if (!siteVisitDetails.contains(bsvDetail)) {
				siteVisitDetails.add(bsvDetail);
			} else {
				siteVisitDetails.remove(this.getManagedBean().getBsvIndex());
				siteVisitDetails.add(this.getManagedBean().getBsvIndex(),
						bsvDetail);
			}

			briefingBean.setSitevisitList(siteVisitDetails);
		}

		if (this.getManagedBean().getQt().isZonal()) {
			List<BriefingZoneBean> zoneBeans = this.getManagedBean()
					.getBriefingZoneBean();
			for (int i = 0; i < zoneBeans.size(); i++) {
				if (zoneBeans
						.get(i)
						.getTplSpecZoneDtl()
						.getZoneName()
						.equalsIgnoreCase(
								briefingBean.getTplSpecZoneDtl().getZoneName())) {
					zoneBeans.remove(i);
					zoneBeans.add(i, briefingBean);
					break;
				}
			}
			this.getManagedBean().setBriefingZoneBean(zoneBeans);
		} else {
			this.getManagedBean().setBsvDtlBean(briefingBean);
		}

		this.getManagedBean().setQtBriefing(null);
		RequestContext.getCurrentInstance().execute("addbsvdtls.hide();");
	}

	public void deleteBsvDetails(String bsvType) {
		debugLogger("deleteBriefingDetails");
		BriefingZoneBean briefingDtlBean = this.getManagedBean()
				.getBsvDtlBean();
		if (SourcingConstants.BSV_TYPE_BRIEFING.equalsIgnoreCase(bsvType)) {
			List<ScQtBsvDtl> briefingDetails = briefingDtlBean
					.getBriefingList();
			List<ScQtBsvDtl> selectedBsvDetails = briefingDtlBean
					.getSelectedBriefingDtls();
			if (null == selectedBsvDetails || selectedBsvDetails.isEmpty()) {
				throw new EpException("Please select details to remove");
			}
			for (int i = 0; i < briefingDetails.size(); i++) {
				for (int j = 0; j < selectedBsvDetails.size(); j++) {
					if (selectedBsvDetails
							.get(j)
							.getBsvDesc()
							.equalsIgnoreCase(
									briefingDetails.get(i).getBsvDesc())) {
						briefingDetails.remove(i);
					}
				}
			}

		} else if (SourcingConstants.BSV_TYPE_SITEVISIT
				.equalsIgnoreCase(bsvType)) {
			List<ScQtBsvDtl> siteVisitDetails = briefingDtlBean
					.getSitevisitList();
			List<ScQtBsvDtl> selectedBsvDetails = briefingDtlBean
					.getSelectedSiteVisitDtls();
			if (null == selectedBsvDetails || selectedBsvDetails.isEmpty()) {
				throw new EpException("Please select details to remove");
			}
			for (int i = 0; i < siteVisitDetails.size(); i++) {
				for (int j = 0; j < selectedBsvDetails.size(); j++) {
					if (selectedBsvDetails
							.get(j)
							.getBsvDesc()
							.equalsIgnoreCase(
									siteVisitDetails.get(i).getBsvDesc())) {
						siteVisitDetails.remove(i);
					}
				}
			}
		}
		this.getManagedBean().setBsvDtlBean(briefingDtlBean);
	}

	public String gotoAttandanceDtl(ScQtBsvDtl scQtBriefing) {
		debugLogger("editAction");
		IQTBriefingSO qTBriefingSO = null;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			if (scQtBriefing != null) {
				this.getManagedBean().setQtBriefing(scQtBriefing);
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
		this.getManagedBean().setBriefingAttendanceList(null);
		ScQtBsvRegistration briefingRegistration = (ScQtBsvRegistration) event
				.getObject();
		this.getManagedBean()
				.setSelectedbriefRegistration(briefingRegistration);

		List<ScQtBsvAttendance> attanedList = briefingRegistration
				.getBsvAttendance();

		if (null == attanedList && null != briefingRegistration
				&& null != briefingRegistration.getBsvRegistrationId()) {
			IQTBriefingSO briefingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
			briefingReqDTO.setQtBriefingId(briefingRegistration
					.getBsvRegistrationId());
			BriefingResDTO resDto = briefingSO
					.retrieveBriefingAttendanceList(briefingReqDTO);
			attanedList = resDto.getBriefingAttResultBean().getResultList();
		}

		if (null == attanedList || attanedList.isEmpty()) {
			attanedList = new ArrayList<ScQtBsvAttendance>();
			ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISupplierSearchSO.class);
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setMofAccountNo(briefingRegistration
					.getSupplier().getSmMofAccounts().get(0).getMofNo());
			SupplierSearchResDTO resDTO = supplierSearchSO
					.retrieveAuthorizedPersonnelsByMofNo(supplierSearchReqDTO);
			List<SmPersonnel> personals = resDTO.getPersonnels();
			log.debug("Personal list size**************" + personals.size());

			/**
			 * Below code to add the smpersonal to attandance list if alredy
			 * attanded persons mark it.
			 */

			for (SmPersonnel personnel : personals) {
				ScQtBsvAttendance briefingAttendance = new ScQtBsvAttendance();
				briefingAttendance.setName(personnel.getName());
				briefingAttendance.setIcPassPort(personnel
						.getIdentificationNo());
				briefingAttendance.setContactNo(personnel.getMobileNo());
				briefingAttendance.setEmail(personnel.getEmail());
				briefingAttendance.setBsvRegistration(briefingRegistration);
				briefingAttendance.setAuthorized(true);

				attanedList.add(briefingAttendance);
			}
		}

		this.getManagedBean().setBriefingAttendanceList(attanedList);
	}

	/**
	 * To add the attandance user click add button
	 */
	public void addAttendanceForBSV() {
		List<ScQtBsvAttendance> attandances = this.getManagedBean()
				.getBriefingAttendanceList();
		ScQtBsvAttendance briefingAttendance = new ScQtBsvAttendance();
		briefingAttendance.setBsvRegistration(this.getManagedBean()
				.getSelectedbriefRegistration());
		briefingAttendance.setAttended(true);
		briefingAttendance.setBrandNew(false);
		if (null == attandances || attandances.isEmpty()) {
			attandances = new ArrayList<ScQtBsvAttendance>();
			attandances.add(briefingAttendance);
		} else {
			attandances.add(briefingAttendance);
		}
		this.getManagedBean().setBriefingAttendanceList(attandances);
		this.getManagedBean().getSelectedbriefRegistration()
				.setBsvAttendance(attandances);

	}

	public void bsvRegistationListner(List<SmMofAccount> mofAccounts) {
		log.debug("businessNatureListener");
		List<ScQtBsvRegistration> bsvRegistrations = new ArrayList<ScQtBsvRegistration>();
		if (mofAccounts != null) {
			for (SmMofAccount mofAccount : mofAccounts) {
				ScQtBsvRegistration bsvRegistration = new ScQtBsvRegistration();
				bsvRegistration.setSupplier(mofAccount.getSmSupplier());
				bsvRegistration
						.setBsvDtl(this.getManagedBean().getQtBriefing());
				bsvRegistrations.add(bsvRegistration);
			}
			if (getManagedBean().getBriefingRegistrationList() == null
					|| getManagedBean().getBriefingRegistrationList().isEmpty()) {
				getManagedBean().setBriefingRegistrationList(bsvRegistrations);
			} else {
				getManagedBean().getBriefingRegistrationList().addAll(
						bsvRegistrations);
			}

		}
	}

	/**
	 * Method to delete the registrations
	 */
	public void deleteBsvAttandances() {
		List<ScQtBsvAttendance> attandances = this.getManagedBean()
				.getBriefingAttendanceList();
		ScQtBsvAttendance selectedBsvAttandance = this.getManagedBean()
				.getScQtBriefingAttendance();
		if (null != attandances) {
			for (int i = 0; i < attandances.size(); i++) {
				if (attandances
						.get(i)
						.getIcPassPort()
						.equalsIgnoreCase(selectedBsvAttandance.getIcPassPort())) {
					attandances.remove(i);

				}
			}
		}
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

	/**
	 * Method to delete the registrations
	 */
	public void deleteSupplierDetails() {
		List<ScQtBsvRegistration> registrations = this.getManagedBean()
				.getBriefingRegistrationList();
		for (ScQtBsvRegistration registration : registrations) {
			if (registration
					.getSupplier()
					.getSmMofAccounts()
					.get(0)
					.getMofNo()

					.equalsIgnoreCase(
							this.getManagedBean()
									.getSelectedbriefRegistration()
									.getSupplier().getSmMofAccounts().get(0)
									.getMofNo())) {
				registrations.remove(registration);
				break;
			}
		}
		this.getManagedBean().setBriefingRegistrationList(registrations);
	}

	public String briefingBackAction() {
		debugLogger("briefingBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}

	/**
	 * Method to add briefing details to site visit details.
	 */
	public void addToSiteVisitDtl() {
		BriefingZoneBean briefingZoneBean = this.getManagedBean()
				.getBsvDtlBean();
		List<ScQtBsvDtl> selectedBsvDtls = briefingZoneBean
				.getSelectedBriefingDtls();
		List<ScQtBsvDtl> siteVisitDetails = briefingZoneBean.getSitevisitList();
		if (null == siteVisitDetails) {
			siteVisitDetails = new ArrayList<ScQtBsvDtl>();
		}
		if (siteVisitDetails.containsAll(selectedBsvDtls)) {
			throw new EpException(MessageConst.ERROR_760099);
		}
		if (selectedBsvDtls != null) {
			for (ScQtBsvDtl bsvDtl : selectedBsvDtls) {
				try {
					ScQtBsvDtl siteVisitDtl = (ScQtBsvDtl) BeanUtils
							.cloneBean(bsvDtl);
					siteVisitDtl
							.setBsvType(SourcingConstants.BSV_TYPE_SITEVISIT);
					siteVisitDetails.add(siteVisitDtl);
				} catch (Exception e) {

				}

			}
			briefingZoneBean.setSitevisitList(siteVisitDetails);
		} else {
			throw new EpException(MessageConst.ERROR_760098);
		}
		briefingZoneBean.setSelectedBriefingDtls(null);
		briefingZoneBean.setSelectedSiteVisitDtls(null);
		this.getManagedBean().setBsvDtlBean(briefingZoneBean);
	}

	public void cancelBriefingList() {
		this.getManagedBean().setQtBriefing(null);
		RequestContext.getCurrentInstance().execute("addbsvdtls.hide();");
	}

	public void briefingClickEvent(AjaxBehaviorEvent event) {
		Boolean selectedValue = (Boolean) event.getComponent().getAttributes()
				.get("value");
		BriefingZoneBean zoneBean = this.getManagedBean().getBsvDtlBean();
		if (selectedValue) {
			zoneBean.getScQtBsv().setMinBriefingAttend(0);
		}
		this.getManagedBean().setBsvDtlBean(zoneBean);

	}

	public void siteVisitClickEvent(AjaxBehaviorEvent event) {
		Boolean selectedValue = (Boolean) event.getComponent().getAttributes()
				.get("value");
		BriefingZoneBean zoneBean = this.getManagedBean().getBsvDtlBean();
		if (selectedValue) {
			zoneBean.getScQtBsv().setMinSiteVisitAttend(0);
		}
		this.getManagedBean().setBsvDtlBean(zoneBean);

	}

	public void editAction(ScQtBsvDtl bsvDtl, int index) {
		bsvDtl.setSelected(true);
		this.getManagedBean().setBsvIndex(index);
		this.getManagedBean().setQtBriefing(bsvDtl);
		RequestContext.getCurrentInstance().execute("addbsvdtls.show();");
		retrievePmStateList();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
