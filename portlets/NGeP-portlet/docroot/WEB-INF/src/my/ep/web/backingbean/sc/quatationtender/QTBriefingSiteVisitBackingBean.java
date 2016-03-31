/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.backingbean.sc.quatationtender;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.sc.bsvattendance.BSVAttendanceBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 * The Class QTBriefingSiteVisitBackingBean.
 *
 * @author Viswanath
 */
@ManagedBean(name = "qTBriefingSiteVisitBackingBean")
@RequestScoped
public class QTBriefingSiteVisitBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	/** The Constant NAV_TO_BSV_ATTENDANCE_PAGE. */
	private static final String NAV_TO_BSV_ATTENDANCE_PAGE = "/xhtml/sc/quatationtender/briefingsitevisit/briefingAttendance.xhtml";

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTBriefingSiteVisitBackingBean.class);

	/** The index. */
	private int index;

	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * Instantiates a new QT briefing site visit backing bean.
	 */
	public QTBriefingSiteVisitBackingBean() {

	}

	/**
	 * Retrieve state list by country id
	 * 
	 */
	public void retrievePmStateList() {
		debugLogger("retrievePmStateList");
		ICountrySO countrySo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
		if (null == this.getManagedBean().getStates()) {
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);

			List<PmState> stateList = PmCommonUtil
					.retrievePmStateList(countryResDTO.getPmCountry()
							.getCountryId());
			this.getManagedBean().setStates(stateList);
		}
		retrievePmDivisions();
	}

	/**
	 * Retrieve district list by state id.
	 */
	public void retrievePmDistrictList() {
		debugLogger("retrievePmDistrictList");
		if (null != this.getManagedBean().getQtBriefing()) {
			this.getManagedBean().setDistricts(null);

			Long divisionId = this.getManagedBean().getQtBriefing()
					.getAddress().getPmDivision().getDivisionId();

			Long stateId = this.getManagedBean().getQtBriefing().getAddress()
					.getPmState().getStateId();
			// PmState pmState = PmCommonUtil.retrievePmStateByStateId(stateId);
			if (!this.getManagedBean().isState() && divisionId == null) {
				DistrictReqDTO reqDTO = new DistrictReqDTO();
				reqDTO.setStateId(stateId);
				IDistrictSO reqSO = (IDistrictSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);
				DistrictResDTO resDTO = reqSO
						.retriveDistrictListByStateID(reqDTO);
				this.getManagedBean().setDistricts(resDTO.getDistrictList());

			} else if (divisionId != null) {
				DistrictReqDTO reqDTO = new DistrictReqDTO();
				reqDTO.setDivisionId(divisionId);
				IDistrictSO reqSO = (IDistrictSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);
				DistrictResDTO resDTO = reqSO
						.retriveDistrictListByDivision(reqDTO);
				this.getManagedBean().setDistricts(resDTO.getDistrictList());
			}

		}
		retrievePmCityList();
	}

	/**
	 * Retrieve city list by district id.
	 */
	public void retrievePmCityList() {
		this.getManagedBean().setCitys(null);
		if (null != this.getManagedBean().getQtBriefing()) {
			Long districtId = this.getManagedBean().getQtBriefing()
					.getAddress().getPmDistrict().getDistrictId();

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

	/**
	 * Retrieve division list by state id (only applicable for sarawak).
	 */
	public void retrievePmDivisions() {
		debugLogger("retrievePmDistrictList");
		this.getManagedBean().setState(false);
		if (null != this.getManagedBean().getQtBriefing()) {
			/*
			 * this.getManagedBean().getQtBriefing().getAddress()
			 * .setPostcode(null);
			 */
			Long stateId = this.getManagedBean().getQtBriefing().getAddress()
					.getPmState().getStateId();
			this.getManagedBean().setDivisionList(null);
			this.getManagedBean().setDistrictList(null);
			this.getManagedBean().setCitys(null);
			if (stateId != null) {
				PmState pmState = PmCommonUtil
						.retrievePmStateByStateId(stateId);
				if (ParameterConst.SARAK_SK_CODE.equalsIgnoreCase(pmState
						.getStateCode())) {
					this.getManagedBean().setState(true);
					this.getManagedBean().setDivisionList(
							PmCommonUtil.retrievePmDivisionList(stateId));
				} else {
					PmDivision division = this.getManagedBean().getQtBriefing()
							.getAddress().getPmDivision();
					if (null != division) {
						this.getManagedBean().getQtBriefing().getAddress()
								.getPmDivision().setDivisionId(null);
					} else {
						this.getManagedBean().getQtBriefing().getAddress()
								.setPmDivision(new PmDivision());
					}

				}
			}

		}

		retrievePmDistrictList();
	}

	/**
	 * Process checked.
	 *
	 * @param e the e
	 */
	public void processChecked(AjaxBehaviorEvent e) {
		Boolean selectedValue = (Boolean) e.getComponent().getAttributes()
				.get("value");
		BriefingZoneBean zoneBean = this.getManagedBean().getBsvDtlBean();
		if (selectedValue) {
			zoneBean.getScQtBsv().setAllSiteVisitReq(true);
			zoneBean.getScQtBsv().setMinSiteVisitAttend(0);
		}
		this.getManagedBean().setBsvDtlBean(zoneBean);

	}

	/**
	 * Add new bsv details to list.
	 *
	 * @param bsvType the bsv type
	 */
	public void addBsvDetails(String bsvType) {
		debugLogger("addBriefingDetails");
		this.getManagedBean().setRequired(Boolean.TRUE);
		this.getManagedBean().setBsvType(bsvType);
		ScQtBsvDtl bsvDtl = new ScQtBsvDtl();
		bsvDtl.setBsvType(bsvType);
		PmAddress address = new PmAddress();
		PmCity pmCity = new PmCity();
		address.setPmCity(pmCity);
		PmState pmState = new PmState();
		address.setPmState(pmState);
		address.setPmDivision(new PmDivision());
		address.setPmDistrict(new PmDistrict());
		bsvDtl.setAddress(address);
		PmStatus pmStatus = new PmStatus();
		pmStatus.setStatusId(QTStatusConst.BSV_PENDING);
		bsvDtl.setBsvStatus(pmStatus);
		this.getManagedBean().setQtBriefing(bsvDtl);
		retrievePmStateList();
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * Add details to briefing list.
	 */
	public void addToBriefingList() {
		debugLogger("addToBriefingList");
		this.getManagedBean().setRequired(false);
		String bsvType = this.getManagedBean().getBsvType();
		BriefingZoneBean briefingBean = this.getManagedBean().getBsvDtlBean();
		ScQtBsvDtl bsvDetail = this.getManagedBean().getQtBriefing();
		// Checking Max attendance value is greater than zero.
		if (bsvDetail.getMaxNoOfAttendance() < 1) {
			throw new EpException("Max Attendance is greater than 0");
		}
		validatePostcode();
		
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
		/*
		 * if (null != bsvDetail) { Date bsvDate = bsvDetail.getBsvDate(); if
		 * (null != bsvDate) { Date publishDate =
		 * this.getManagedBean().getQt().getPublishDate(); if (null !=
		 * publishDate) { if (bsvDate.compareTo(publishDate) < 0 ) { throw new
		 * EpException("Publish Date is more Than Briefing/Site Visit date."); }
		 * } } }
		 */
		if (SourcingConstants.BSV_TYPE_BRIEFING.equalsIgnoreCase(bsvType)) {
			List<ScQtBsvDtl> briefingDetails = briefingBean.getBriefingList();
			if (null == briefingDetails) {
				briefingDetails = new ArrayList<ScQtBsvDtl>();
			}
			if (!briefingDetails.contains(bsvDetail)) {
				briefingDetails.add(bsvDetail);
			}
			briefingBean.setBriefingList(briefingDetails);
		} else if (SourcingConstants.BSV_TYPE_SITEVISIT
				.equalsIgnoreCase(bsvType)) {
			List<ScQtBsvDtl> siteVisitDetails = briefingBean.getSitevisitList();
			if (null == siteVisitDetails) {
				siteVisitDetails = new ArrayList<ScQtBsvDtl>();
			}
			if (!siteVisitDetails.contains(bsvDetail)) {
				siteVisitDetails.add(bsvDetail);
			}
			if (siteVisitDetails.size() == 1) {
				briefingBean.getScQtBsv().setAllSiteVisitReq(true);
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

		}
		this.getManagedBean().setBsvDtlBean(briefingBean);
		this.getManagedBean().setQtBriefing(null);
		// facesmgs error hide..
		RequestContext.getCurrentInstance().execute("addbsvdtls.hide()");
		this.getManagedBean().setRequired(Boolean.FALSE);
		
		ScQtBsvDtl tempScQtBsvDtl = this.getManagedBean().getTempScQtBsvDtl();
		if (null != tempScQtBsvDtl) {
			updateQtAmmendMentDtls(bsvType, tempScQtBsvDtl,bsvDetail);
		}
		
	}

	/**
	 * Delete bsv details from list.
	 *
	 * @param bsvType the bsv type
	 */
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
				throw new EpException(MessageConst.ERROR_760141);
			}
			briefingDetails.removeAll(selectedBsvDetails);
			briefingDtlBean.setSelectedBriefingDtls(null);
		} else if (SourcingConstants.BSV_TYPE_SITEVISIT
				.equalsIgnoreCase(bsvType)) {
			List<ScQtBsvDtl> siteVisitDetails = briefingDtlBean
					.getSitevisitList();
			List<ScQtBsvDtl> selectedBsvDetails = briefingDtlBean
					.getSelectedSiteVisitDtls();
			if (null == selectedBsvDetails || selectedBsvDetails.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760141);
			}
			siteVisitDetails.removeAll(selectedBsvDetails);
			briefingDtlBean.setSelectedSiteVisitDtls(null);
		}
		this.getManagedBean().setBsvDtlBean(briefingDtlBean);
	}

	/**
	 * Goto attandance dtl.
	 *
	 * @param scQtBriefing the sc qt briefing
	 * @return the string
	 */
	public String gotoAttandanceDtl(ScQtBsvDtl scQtBriefing) {
		
		BSVAttendanceBackingBean attendanceBackingBean= new BSVAttendanceBackingBean();
		attendanceBackingBean.registerSupplier(scQtBriefing);
		attendanceBackingBean.getManagedBean().setScQt(this.getManagedBean().getQt());
		attendanceBackingBean.getManagedBean().setMaintanceFlow(true);
		return NAV_TO_BSV_ATTENDANCE_PAGE + "?faces-redirect=true";
	}

	/**
	 * On row select.
	 *
	 * @param event the event
	 */
	public void onRowSelect(SelectEvent event) {
		debugLogger("onRowSelect");
		this.getManagedBean().setBriefingAttendanceList(null);
		ScQtBsvRegistration briefingRegistration = (ScQtBsvRegistration) event
				.getObject();

		getAttandanceDtlsBySelectedRegistartion(briefingRegistration);
	}
	
	/**
	 * Gets the attandance dtls by selected registartion.
	 *
	 * @param briefingRegistration the briefing registration
	 * @return the attandance dtls by selected registartion
	 */
	private void getAttandanceDtlsBySelectedRegistartion(
			ScQtBsvRegistration briefingRegistration) {
		debugLogger("getAttandanceDtlsBySelectedRegistartion");
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
			briefingReqDTO.setSupplierId(briefingRegistration.getSupplier()
					.getSupplierId());
			BriefingResDTO resDto = briefingSO
					.retrieveBriefingAttendanceList(briefingReqDTO);
			attanedList = resDto.getBriefingAttResultBean().getResultList();
		}
		
		if (null == attanedList || attanedList.isEmpty()) {
			attanedList = new ArrayList<ScQtBsvAttendance>();
			IQTBriefingSO bsvSo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setSupplierId(briefingRegistration.getSupplier()
					.getSupplierId());
			BriefingResDTO resDTO = bsvSo
					.retrieveAuthriozesPersonalBySupplierId(reqDTO);
			List<SmPersonnel> personals = resDTO.getPersonals();
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
		
		this.getManagedBean().getSelectedbriefRegistration()
			.setBsvAttendance(attanedList);
	}

	/**
	 * To add the attandance user click add button.
	 */
	public void addAttendanceForBSV() {
		debugLogger("addAttendanceForBSV");
		if (null != this.getManagedBean().getSelectedbriefRegistration()) {
			List<ScQtBsvAttendance> attandances = this.getManagedBean()
					.getSelectedbriefRegistration().getBsvAttendance();
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
			// this.getManagedBean().setBriefingAttendanceList(attandances);
			this.getManagedBean().getSelectedbriefRegistration()
					.setBsvAttendance(attandances);
		} else {
			throw new EpException(MessageConst.ERROR_760185);
		}
	}

	/**
	 * Bsv registation listner.
	 *
	 * @param mofAccounts the mof accounts
	 */
	public void bsvRegistationListner(List<SmMofAccount> mofAccounts) {
		debugLogger("bsvRegistationListner");
		if (getManagedBean().getBriefingRegistrationList() == null
				|| getManagedBean().getBriefingRegistrationList().isEmpty()) {
			getManagedBean().setBriefingRegistrationList(
					new ArrayList<ScQtBsvRegistration>());
		}
		
		if (mofAccounts != null) {
			for (int i = 0; i < mofAccounts.size(); i++) {
				SmMofAccount mofAccount = mofAccounts.get(i);
				ScQtBsvRegistration bsvRegistration = new ScQtBsvRegistration();
				bsvRegistration.setSupplier(mofAccount.getSmSupplier());
				bsvRegistration.getSupplier().setSmMofAccounts(mofAccounts);
				bsvRegistration.setBsvDtl(this.getManagedBean().getQtBriefing());
				if (getManagedBean().getBriefingRegistrationList().contains(
						bsvRegistration)) {
					throw new EpException(MessageConst.ERROR_760186);
				}
				getManagedBean().getBriefingRegistrationList().add(
						bsvRegistration);
				if (i == mofAccounts.size() - 1) {
					this.getManagedBean().setSelectedbriefRegistration(
							bsvRegistration);
					getAttandanceDtlsBySelectedRegistartion(bsvRegistration);
				}
			}
		}
	}

	/**
	 * Method to delete the registrations.
	 */
	public void deleteBsvAttandances() {
		debugLogger("deleteBsvAttandances");
		List<ScQtBsvAttendance> attandances = null;
		if (null != this.getManagedBean().getSelectedbriefRegistration()) {
			attandances = this.getManagedBean().getSelectedbriefRegistration()
					.getBsvAttendance();
		}
		ScQtBsvAttendance selectedBsvAttandance = this.getManagedBean()
				.getScQtBriefingAttendance();
		if (null == selectedBsvAttandance) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		if (null != attandances && !selectedBsvAttandance.isAuthorized()) {
			for (int i = 0; i < attandances.size(); i++) {

				if (attandances
						.get(i)
						.getIcPassPort()
						.equalsIgnoreCase(selectedBsvAttandance.getIcPassPort())) {
					attandances.remove(i);
					break;
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_760126);
		}
	}

	/**
	 * Adds the supplier details.
	 */
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
	 * Method to delete the registrations.
	 */
	public void deleteSupplierDetails() {
		debugLogger("deleteSupplierDetails");
		List<ScQtBsvRegistration> registrations = this.getManagedBean()
				.getBriefingRegistrationList();
		if (null == registrations || registrations.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		ScQtBsvRegistration selectedReqistration = this.getManagedBean()
				.getSelectedbriefRegistration();
		if (null == selectedReqistration) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		for (int i = 0; i < registrations.size(); i++) {
			if (registrations
					.get(i)
					.getSupplier()
					.getCompanyName()
					.equalsIgnoreCase(
							this.getManagedBean()
									.getSelectedbriefRegistration()
									.getSupplier().getCompanyName())) {

				registrations.remove(i);
				break;
			}
		}
		
		this.getManagedBean().setBriefingRegistrationList(registrations);
		this.getManagedBean().setBriefingAttendanceList(null);
	}

	/**
	 * Briefing back action.
	 *
	 * @return the string
	 */
	public String briefingBackAction() {
		debugLogger("briefingBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}

	/**
	 * Method to add briefing details to site visit details.
	 */
	public void addToSiteVisitDtl() {
		debugLogger("addToSiteVisitDtl");
		BriefingZoneBean briefingZoneBean = this.getManagedBean()
				.getBsvDtlBean();
		briefingZoneBean.getScQtBsv().setAllSiteVisitReq(true);
		if (briefingZoneBean.getBriefingList() != null
				&& !briefingZoneBean.getBriefingList().isEmpty()) {
			List<ScQtBsvDtl> selectedBsvDtls = briefingZoneBean
					.getSelectedBriefingDtls();
			List<ScQtBsvDtl> siteVisitDetails = briefingZoneBean
					.getSitevisitList();
			if (null == siteVisitDetails) {
				siteVisitDetails = new ArrayList<ScQtBsvDtl>();
			}
			if (selectedBsvDtls != null && !selectedBsvDtls.isEmpty()) {
				/*
				 * for (ScQtBsvDtl bsvDtls : selectedBsvDtls) { if
				 * (siteVisitDetails.contains(bsvDtls)) { //
				 * briefingZoneBean.setSitevisitList(null); throw new
				 * EpException(MessageConst.ERROR_760099); } }
				 */

				for (ScQtBsvDtl bsvDtl : selectedBsvDtls) {
					ScQtBsvDtl siteVisitDtl = null;
					try {

						siteVisitDtl = (ScQtBsvDtl) BeanUtils.cloneBean(bsvDtl);
						siteVisitDtl.setChangedDate(null);
						siteVisitDtl.setQtBsvDtlId(null);
						siteVisitDtl
								.setBsvType(SourcingConstants.BSV_TYPE_SITEVISIT);
						siteVisitDtl.setQtBsvRegistrations(null);

					} catch (Exception e) {
					}
					if (siteVisitDetails.contains(siteVisitDtl)) {
						// briefingZoneBean.setSitevisitList(null);
						throw new EpException(MessageConst.ERROR_760099);
					} else {
						siteVisitDetails.add(siteVisitDtl);
					}
				}
				briefingZoneBean.setSitevisitList(siteVisitDetails);
			} else {
				throw new EpException(MessageConst.ERROR_760121);
			}
			briefingZoneBean.setSelectedBriefingDtls(null);
			briefingZoneBean.setSelectedSiteVisitDtls(null);
			this.getManagedBean().setBsvDtlBean(briefingZoneBean);
		} else {
			throw new EpException(MessageConst.ERROR_760199);
		}
	}

	/**
	 * Cancel briefing list.
	 */
	public void cancelBriefingList() {
		debugLogger("cancelBriefingList");
		this.getManagedBean().setQtBriefing(null);
		RequestContext.getCurrentInstance().execute("addbsvdtls.hide();");
		getManagedBean().setRequired(Boolean.FALSE);
	}

	/**
	 * Briefing click event.
	 *
	 * @param event the event
	 */
	public void briefingClickEvent(AjaxBehaviorEvent event) {
		debugLogger("briefingClickEvent");
		Boolean selectedValue = (Boolean) event.getComponent().getAttributes()
				.get("value");
		BriefingZoneBean zoneBean = this.getManagedBean().getBsvDtlBean();
		if (selectedValue) {
			
		} else {
			zoneBean.getScQtBsv().setMinBriefingAttend(0);
		}
		this.getManagedBean().setBsvDtlBean(zoneBean);

	}

	/**
	 * Site visit click event.
	 *
	 * @param event the event
	 *//*
	public void siteVisitClickEvent(AjaxBehaviorEvent event) {
		debugLogger("siteVisitClickEvent");
		Boolean selectedValue = (Boolean) event.getComponent().getAttributes()
				.get("value");
		BriefingZoneBean zoneBean = this.getManagedBean().getBsvDtlBean();
		if (selectedValue) {
			zoneBean.getScQtBsv().setMinSiteVisitAttend(0);
		} else {
			zoneBean.getScQtBsv().setMinSiteVisitAttend(0);
		}
		this.getManagedBean().setBsvDtlBean(zoneBean);

	}*/

	/**
	 * Edits the action.
	 *
	 * @param bsvDtl the bsv dtl
	 * @param index the index
	 * @param bsvType the bsv type
	 */
	public void editAction(ScQtBsvDtl bsvDtl, int index, String bsvType) {
		debugLogger("editAction");
		bsvDtl.setSelected(true);
		this.getManagedBean().setRequired(true);
		this.getManagedBean().setBsvIndex(index);
		this.getManagedBean().setQtBriefing(bsvDtl);
		this.getManagedBean().setBsvType(bsvType);
		RequestContext.getCurrentInstance().execute("addbsvdtls.show();");
		retrievePmStateList();
		
		if (QTStatusConst.QT_BRIEFING_SITE_VISIT.equals(this
				.getManagedBean().getCurrentStatusId())){
			ScQtBsvDtl tempScQtBsvDtl = new ScQtBsvDtl();
			try {
				ConvertUtilsBean convertUtilsBean = BeanUtilsBean
						.getInstance().getConvertUtils();
				convertUtilsBean.register(false, true, -1);
				BeanUtils.copyProperties(tempScQtBsvDtl, bsvDtl);
			} catch (IllegalAccessException e) {
				log.error("Exception occured while copying properties"
						+ e.getMessage());
			} catch (InvocationTargetException e) {
				log.error("Exception occured while copying properties"
						+ e.getMessage());
			}
			this.getManagedBean().setTempScQtBsvDtl(tempScQtBsvDtl);
		}
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index.
	 *
	 * @param index the new index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Will be called on change of accordian panel..
	 *
	 * @param event the event
	 */
	public void onZoneTabChange(TabChangeEvent event) {
		debugLogger("onZoneTabChange");
		BriefingZoneBean zoneBean = (BriefingZoneBean) event.getData();

		// this.getManagedBean().setBsvZoneindx(1);
		this.getManagedBean().setBsvDtlBean(zoneBean);
	}
	
	/**
	 * Validate postcode.
	 */
	public void validatePostcode() {
		debugLogger("validatePostcode");
		String postcode = getManagedBean().getQtBriefing().getAddress().getPostcode();
		Pattern pattern = Pattern.compile("\\d{5}");
		Matcher matcher = pattern.matcher(postcode);
		if (!matcher.matches()) {
			throw new EpException("Postcode is not valid, Postcode Should be limited to 5 digits.");
		}

	}
	
	/** 
	 * Update time stamp value into SCQTAMEND table if any changes occurs 
	 * in Briefing/Visit.
	 * 
	 * @param bsvType
	 * @param bsvDetail
	 * @param tempScQtBsvDtl
	 */
	private void updateQtAmmendMentDtls(String bsvType, ScQtBsvDtl tempScQtBsvDtl,
			ScQtBsvDtl bsvDetail ) {

		if (!(compareScQTBsvDtlObjs(tempScQtBsvDtl,bsvDetail))) {
			QtAmendMentBean amendMentBean = this.getManagedBean()
					.getAmendMentBean();
			if (null == amendMentBean) {
				amendMentBean = new QtAmendMentBean();
			}
			java.util.Date today = new java.util.Date();
			if (SourcingConstants.BSV_TYPE_BRIEFING.equalsIgnoreCase(bsvType)) {
				amendMentBean.setBriefingChangedDate(new Timestamp(today
						.getTime()));
			} else {
				amendMentBean.setSiteVisitChangedDate(new Timestamp(today
						.getTime()));
			}
			this.getManagedBean().setAmendMentBean(amendMentBean);
		}
		 this.getManagedBean().setTempScQtBsvDtl(null);

	}
	
	private boolean compareScQTBsvDtlObjs(ScQtBsvDtl bsvDtl1,ScQtBsvDtl bsvDtl2){
		
		PmAddress addr = bsvDtl2.getAddress();
		PmAddress thisAddr = bsvDtl1.getAddress();
		if (StringUtils.equalsIgnoreCase(bsvDtl1.getBsvDesc(),bsvDtl2.getBsvDesc())
				&& StringUtils.equalsIgnoreCase(bsvDtl1.getBsvType(),bsvDtl2.getBsvType())
				&& bsvDtl1.getBsvDate().equals(bsvDtl2.getBsvDate())
				&& StringUtils.equalsIgnoreCase(bsvDtl1.getLocation(),bsvDtl2.getLocation())
				&& StringUtils.equalsIgnoreCase(thisAddr.getAddress1(), addr.getAddress1())
				&& StringUtils.equalsIgnoreCase(thisAddr.getAddress2(),addr.getAddress2())
				&& StringUtils.equalsIgnoreCase(thisAddr.getAddress3(),addr.getAddress3())
				&& StringUtils.equalsIgnoreCase(thisAddr.getAddress4(),addr.getAddress4())
				&& StringUtils.equalsIgnoreCase(thisAddr.getPostcode(),addr.getPostcode())
				&& equalForLong(thisAddr.getPmState().getStateId(),addr.getPmState().getStateId())
				&& equalForLong(thisAddr.getPmCity().getCityId(),addr.getPmCity().getCityId())
				&& equalForLong(thisAddr.getPmDistrict().getDistrictId(),addr.getPmDistrict().getDistrictId())
				&& equalForLong(thisAddr.getPmDivision().getDivisionId(),addr.getPmDivision().getDivisionId())
				&& StringUtils.equalsIgnoreCase(bsvDtl1.getCoordinatingOfficer(),bsvDtl2.getCoordinatingOfficer())
				&& (bsvDtl1.getMaxNoOfAttendance()==bsvDtl2.getMaxNoOfAttendance())){
			
			return true;
			
		}
		return false;
	}
	
	private boolean equalForLong(Long l1,Long l2){
		
		boolean isEqual = false;
		if(l1== null && l2 == null){
			isEqual = true;
		} else if(l1 != null){
			isEqual = l1.equals(l2);
		}
		return isEqual;
	}
	
}
