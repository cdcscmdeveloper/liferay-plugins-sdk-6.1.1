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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.bean.sc.QtAmendMentValueBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.sc.bsvattendance.BSVAttendanceBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.collections.CollectionUtils;
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
		
		ScQt scQt = this.getManagedBean().getQt();
		if(scQt != null){
			message += " @qtNo = "+scQt.getQtNo();
		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		/*else{
		log.info(message);
		}*/
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
		ICountrySO countrySo = null;
		try{
			countrySo = BusinessDelegate.getInstance().getServiceObject(
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
		}finally{
			countrySo = null;	
		}
	}

	/**
	 * Retrieve district list by state id.
	 */
	public void retrievePmDistrictList() {
		debugLogger("retrievePmDistrictList");
		IDistrictSO reqSO = null;
		try{
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
				 reqSO = (IDistrictSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);
				DistrictResDTO resDTO = reqSO
						.retriveDistrictListByStateID(reqDTO);
				this.getManagedBean().setDistricts(resDTO.getDistrictList());

			} else if (divisionId != null) {
				DistrictReqDTO reqDTO = new DistrictReqDTO();
				reqDTO.setDivisionId(divisionId);
				 reqSO = (IDistrictSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);
				DistrictResDTO resDTO = reqSO
						.retriveDistrictListByDivision(reqDTO);
				this.getManagedBean().setDistricts(resDTO.getDistrictList());
			}

		}
		retrievePmCityList();
		}finally{
			reqSO = null;	
		}
	}

	/**
	 * Retrieve city list by district id.
	 */
	public void retrievePmCityList() {
		this.getManagedBean().setCitys(null);
		ICitySO reqSO =null;
		try{
		if (null != this.getManagedBean().getQtBriefing()) {
			Long districtId = this.getManagedBean().getQtBriefing()
					.getAddress().getPmDistrict().getDistrictId();

			if (null != districtId) {
				CityReqDTO reqDTO = new CityReqDTO();
				reqDTO.setDistrictId(districtId);
				 reqSO = (ICitySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICitySO.class);
				CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
				this.getManagedBean().setCitys(resDTO.getCityList());
			}
		}
		}finally{
			reqSO =null;	
		}
	}

	/**
	 * Retrieve division list by state id (only applicable for sarawak).
	 */
	public void retrievePmDivisions() {
		debugLogger("retrievePmDistrictList");
		this.getManagedBean().setState(false);
		this.getManagedBean().setDivisionList(null);
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
				Long dbStateId = this.getManagedBean().getBsvDBStateId();
				if(dbStateId != null && !stateId.equals(dbStateId)){
					ScAddress address = this.getManagedBean().getQtBriefing().getAddress();
					if(address.getPmDistrict()!= null){
						address.setPmDistrict(new PmDistrict());
					}
					if(address.getPmCity()!= null){
						address.setPmCity(new PmCity());
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
		ScAddress address = new ScAddress();
		PmCity pmCity = new PmCity();
		address.setPmCity(pmCity);
		PmState pmState = new PmState();
		address.setPmState(pmState);
		address.setPmDivision(new PmDivision());
		address.setPmDistrict(new PmDistrict());
		bsvDtl.setAddress(address);
		bsvDtl.setBsvStatus(getPmStatusByStatusId(QTStatusConst.BSV_PENDING));
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
			throw new EpException(MessageConst.ERROR_760401);
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
		
		this.getManagedBean().setRequired(Boolean.FALSE);
		
		ScQtBsvDtl tempScQtBsvDtl = this.getManagedBean().getTempScQtBsvDtl();
		if (null != tempScQtBsvDtl) {
			updateQtAmmendMentDtls(bsvType, tempScQtBsvDtl,bsvDetail);
		} else {
			updateLatestBsvDate();
			if(this.getManagedBean().getAmendMentBean() == null){
				this.getManagedBean().setAmendMentBean(new QtAmendMentBean());
			}
			
		}
		
		RequestContext.getCurrentInstance().execute("addbsvdtls.hide();");
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
		
		// Set QtAmendMentBean to invoke BPM from Maintenance.
		if(this.getManagedBean().getAmendMentBean() == null){
			this.getManagedBean().setAmendMentBean(new QtAmendMentBean());
		}
		updateLatestBsvDate();
	}

	/**
	 * Goto attandance dtl.
	 *
	 * @param scQtBriefing the sc qt briefing
	 * @return the string
	 */
	public String gotoAttandanceDtl(ScQtBsvDtl scQtBriefing) {
		
		BSVAttendanceBackingBean attendanceBackingBean= new BSVAttendanceBackingBean();
		attendanceBackingBean.getregistrationDetails(scQtBriefing, true);
		attendanceBackingBean.getManagedBean().setScQt(this.getManagedBean().getQt());
		
		new QTDetailBackingBean().retrieveApprovalTabDtl();
		List<TabBean> tabList = this.getManagedBean().getTabList();
		for (TabBean tabBean : tabList) {
			if (ParameterConst.APPROVAL_CATEGORY_PTJ.equals(tabBean.getTabType())) {
				ScQt scQt = this.getManagedBean().getQt();
				ScQtApproval qtApproval = tabBean.getScQtApproval();
				List<ScQtApprovalRemark> scQtApprovalRemarks = initializeRemarks(
						qtApproval, scQt.getCurrentWorkFlowStatus(),
						null,
						ScQtApprovalRemark.class);
				attendanceBackingBean.getManagedBean().setScQtApproval(qtApproval);
				attendanceBackingBean.getManagedBean().setScQtApprovalRemarks(scQtApprovalRemarks);
				attendanceBackingBean.getManagedBean().setScQtApprovalAttachments(new ArrayList<ScQtApprovalAttachment>());
				break;
			}
		}
		
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
		IQTBriefingSO briefingSO =null;
		try{
		if (null == attanedList && null != briefingRegistration
				&& null != briefingRegistration.getBsvRegistrationId()) {
			 briefingSO = BusinessDelegate.getInstance()
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
			briefingSO  = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setSupplierId(briefingRegistration != null ?briefingRegistration.getSupplier()
					.getSupplierId():null); //20230712@asyrafhaziq- SQUBE Bug NullPointer#3
			BriefingResDTO resDTO = briefingSO
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
		}finally{
			briefingSO=null;	
		}
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
		
		updateLatestBsvDate();
	}

	/**
	 * Cancel briefing list.
	 */
	public void cancelBriefingList() {
		debugLogger("cancelBriefingList");
		this.getManagedBean().setQtBriefing(null);
		getManagedBean().setRequired(Boolean.FALSE);
		RequestContext.getCurrentInstance().execute("addbsvdtls.hide();");
		
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
		this.getManagedBean().setBsvDBStateId(null);
		RequestContext.getCurrentInstance().execute("addbsvdtls.show();");
		retrievePmStateList();
		ScAddress currentAddress = bsvDtl.getAddress();
		this.getManagedBean().setBsvDBStateId(currentAddress.getPmState().getStateId());
		if (super.isQTStatusInBetween(getManagedBean().getCurrentStatusId(), 
				QTStatusConst.QT_PENDING_PUBLICATION, 
				QTStatusConst.QT_BRIEFING_SITE_VISIT)){
			
			ScQtBsvDtl tempScQtBsvDtl = new ScQtBsvDtl();
			
			ScAddress tempPmAddress = new ScAddress();
			PmStatus currentBsvStatus = bsvDtl.getBsvStatus();
			try {				
				ConvertUtilsBean convertUtilsBean = BeanUtilsBean
						.getInstance().getConvertUtils();
				convertUtilsBean.register(false, true, -1);
				
				// tempScQtBsvDtl
				BeanUtils.copyProperties(tempScQtBsvDtl, bsvDtl);
				
				// PmAddress
				tempScQtBsvDtl.setAddress(tempPmAddress);
				BeanUtils.copyProperties(tempPmAddress, currentAddress);

				// PmState
				tempPmAddress.setPmState(new PmState());
				tempPmAddress.getPmState().setStateId(currentAddress.getPmState().getStateId());
                 
				// PmDivision
				tempPmAddress.setPmDivision(new PmDivision());
				tempPmAddress.getPmDivision().setDivisionId(currentAddress.getPmDivision().getDivisionId());

				// PmDistrict
				tempPmAddress.setPmDistrict(new PmDistrict());
				tempPmAddress.getPmDistrict().setDistrictId(currentAddress.getPmDistrict().getDistrictId());

				// PmCity
				tempPmAddress.setPmCity(new PmCity());
				tempPmAddress.getPmCity().setCityId(currentAddress.getPmCity().getCityId());
				
				if (currentBsvStatus != null) {
					// update transient for bsv status id
					bsvDtl.setBsvStatusId(currentBsvStatus.getStatusId());
					
					tempScQtBsvDtl.setBsvStatus(new PmStatus());
					tempScQtBsvDtl.getBsvStatus().setStatusId(currentBsvStatus.getStatusId());	
				}
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
	private void updateQtAmmendMentDtls(String bsvType,
			ScQtBsvDtl oldBsvDtl, ScQtBsvDtl newBsvDtl) {

		String fieldPrefix = oldBsvDtl.getQtBsvDtlId() + QTFieldConst.FIELD_QT_BSV_SEPARATOR;
		QtAmendMentBean amendMentBean = this.getManagedBean().getAmendMentBean();
		
		if (null == amendMentBean) {
			amendMentBean = new QtAmendMentBean();
		}
		
		// bsvDesc
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_DESC, 
				oldBsvDtl.getBsvDesc(), newBsvDtl.getBsvDesc(),
				true);
		
		// bsvDate
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_DATE, 
				oldBsvDtl.getBsvDate(), newBsvDtl.getBsvDate(),
				true);
		
		// Proposal Start Date
		ScQt scQt = getManagedBean().getQt();
		Date oldProposalStartDate = scQt.getProposalStartDate();
		updateLatestBsvDate();
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				QTFieldConst.FIELD_QT_PROPOSAL_START_DATE, 
				oldProposalStartDate, scQt.getProposalStartDate(),
				true);
		
		// location
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_LOCATION, 
				oldBsvDtl.getLocation(), newBsvDtl.getLocation(),
				true);

		// address
		ScAddress oldAddress = oldBsvDtl.getAddress();
		ScAddress newAddress = newBsvDtl.getAddress();
		if (newAddress.getAddress2().equals("")) {
			newAddress.setAddress2(null);
		}
		if (newAddress.getAddress3().equals("")) {
			newAddress.setAddress3(null);
		}
		/*if (newAddress.getAddress4().equals("")) {
			newAddress.setAddress4(null);
		}*/
		// address1
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_1, 
				oldAddress.getAddress1(), newAddress.getAddress1(),
				true);
		// address2
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_2, 
				oldAddress.getAddress2(), newAddress.getAddress2(),
				true);
		// address3
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_3, 
				oldAddress.getAddress3(), newAddress.getAddress3(),
				true);
		// address4
	/*	putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_4, 
				oldAddress.getAddress4(), newAddress.getAddress4());*/
		// postcode
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_POSTCODE, 
				oldAddress.getPostcode(), newAddress.getPostcode(),
				true);
		// cityId
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_CITY, 
				oldAddress.getPmCity().getCityId(), newAddress.getPmCity().getCityId(),
				true);

		// coordinatingOfficer
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_COORDINATING_OFFICER, 
				oldBsvDtl.getCoordinatingOfficer(), newBsvDtl.getCoordinatingOfficer(),
				true);

		// maxNoOfAttendance
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_MAX_NO_OF_ATTENDANCE, 
				oldBsvDtl.getMaxNoOfAttendance(), newBsvDtl.getMaxNoOfAttendance(),
				true);

		// bsvStatus
		Long oldBsvStatusId = oldBsvDtl.getBsvStatus() != null ? oldBsvDtl.getBsvStatus().getStatusId() : null;
		Long newBsvStatusId = newBsvDtl.getBsvStatus() != null ? newBsvDtl.getBsvStatus().getStatusId() : null;
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_STATUS, 
				oldBsvStatusId, newBsvStatusId,
				true);

		this.getManagedBean().setAmendMentBean(amendMentBean);
		this.getManagedBean().setTempScQtBsvDtl(null);

	}
	
	/**
	 * Put into AmendmentBean if any changes.
	 * 
	 * @param amendMentBean
	 * 		the QtAmendMentBean instance
	 * @param key
	 * 		the field key
	 * @param oldValue
	 * 		the old value
	 * @param newValue
	 * 		the new value
	 * @return	the indicator field is changed.
	 */
	public boolean putIntoAmendMentBeanIfChanged(QtAmendMentBean amendMentBean, String key, Object oldValue, Object newValue, boolean ptjApprovalReqFlg) {
		boolean isChanged = false;
		QtAmendMentValueBean oldValueBean = amendMentBean.getValueMap().get(key);
		
		// Always get the original old value. 
		if (oldValueBean != null) {
			oldValue = oldValueBean.getOldValue();
		}
		
		if (oldValue instanceof String || newValue instanceof String) {
			if (!StringUtils.equals((String)oldValue, (String)newValue)) {
				isChanged = true;
			}
		} else {
			if (oldValue != null && newValue != null &&
					!oldValue.equals(newValue)) {
				isChanged = true;
			}	
		}
		
		if (isChanged) {
			QtAmendMentValueBean valueBean = new QtAmendMentValueBean();
			valueBean.setOldValue(oldValue);
			valueBean.setNewValue(newValue);
			amendMentBean.getValueMap().put(key, valueBean);
			
			if (ptjApprovalReqFlg) {
				amendMentBean.setPTJApprovalReq(true);	
			} else {
				valueBean.setApprovalDate(new Date());
				valueBean.setIsApproved(true);
			}
		} else {
			// remove if had been revert to old value.
			if (oldValueBean != null) {
				amendMentBean.getValueMap().remove(key);
			}
		}
		
		return isChanged;
	}

	private void updateLatestBsvDate() {
		ScQt scQt = getManagedBean().getQt();
		Date latestBsvDate = null;
		
		scQt.setBsvRequired(false);
		
		if (scQt.isZonal()) {
			List<BriefingZoneBean> briefingBeans = getManagedBean()
					.getBriefingZoneBean();
			if (null != briefingBeans && !briefingBeans.isEmpty()) {
				for (BriefingZoneBean bsvDtlBean : briefingBeans) {
					latestBsvDate = updateProposalStartDate(latestBsvDate, bsvDtlBean.getBriefingList());
					latestBsvDate = updateProposalStartDate(latestBsvDate, bsvDtlBean.getSitevisitList());
				}
			} 
		} else {
			BriefingZoneBean bsvDtlBean = getManagedBean().getBsvDtlBean();
			if (bsvDtlBean != null) {
				latestBsvDate = updateProposalStartDate(latestBsvDate, bsvDtlBean.getBriefingList());
				latestBsvDate = updateProposalStartDate(latestBsvDate, bsvDtlBean.getSitevisitList());
			} 
		}
		
		// if no briefing / site visit.
		if (!scQt.isBsvRequired()) {
			latestBsvDate = scQt.getPublishDate();
		} 
		
		scQt.setProposalStartDate(latestBsvDate);
		updatePublishClosingDate(scQt);
	}
	
	private Date updateProposalStartDate(Date latestBsvDate, List<ScQtBsvDtl> scQtBsvDtls) {
		ScQt scQt = getManagedBean().getQt();
		
		if (CollectionUtils.isNotEmpty(scQtBsvDtls)) {
			for (ScQtBsvDtl scQtBsvDtl : scQtBsvDtls) {
				Date bsvDate = scQtBsvDtl.getBsvDate();
				
				if (bsvDate != null &&
						(scQtBsvDtl.getBsvStatus() == null || !QTStatusConst.BSV_CANCELLED.equals(scQtBsvDtl.getBsvStatus().getStatusId())) && 
						(latestBsvDate == null || (latestBsvDate != null && bsvDate.after(latestBsvDate)))) {
					latestBsvDate = bsvDate;
					//20190718@asyrafhaziq - Bug #26137 - Start
					scQt.setBsvRequired(true);
					//20190718@asyrafhaziq - Bug #26137 - End
				}
			}
		}
		
		return latestBsvDate;
	}
	
	/**
	 * Get the BsvMinDate.
	 * @return minDate
	 */
	public Date handleBSVMinDate() {
		
		Date minDate = DateUtils.addDays(handlePublishDate(),1);
		ScQt scQt = this.getManagedBean().getQt();
		Long statusId = scQt.getQtStatus().getStatusId();
		if (isQTStatusPublished(statusId)) {
			minDate = DateUtils.addDays(scQt.getPublishDate(),1);
		}
		return minDate;

	}
	
	/**
	 * Get BSV Status Dropdown
	 * 
	 * @return	the array of SelectItem instance
	 */
	public SelectItem[] getBsvStatusDropdownValues() {
		SearchBean<PmStatusDesc> searchBean = new SearchBean<PmStatusDesc>();
		// Filter By - statusId
		List<Long> statusIds = new ArrayList<Long>();
		statusIds.add(QTStatusConst.BSV_COMPLETED);
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmStatus.statusId",
				SearchOperator.OPERATOR_NOT_IN.value(),
				statusIds));

		return super.getStatusDropdownValuesBySearchBean(
				ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
				ParameterConst.DOCTYPE_BSV_REGISTRATION, 
				searchBean);
	}
	
	/**
	 * BSV Status dropdown change action
	 */
	public void bsvStatusChanged() {
		IStatusSO statusSO = null;
		try {
			ScQtBsvDtl scQtBsvDtl = getManagedBean().getQtBriefing();
			Long statusId = scQtBsvDtl.getBsvStatusId();

			if (statusId != null) {
				statusSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setStatusId(statusId);
				StatusResDTO resDTO = statusSO.retrieveStatusByStatusId(reqDTO);
				scQtBsvDtl.setBsvStatus(resDTO.getPmStatus());	
			}
		} finally {
			statusSO = null;
		}
	}
	
	//#34217 - Start
	public void closeBriefingList() {
		log.info("closeBriefingList()");
		this.getManagedBean().setQtBriefing(null);
		getManagedBean().setRequired(Boolean.FALSE);
	}
	//#34217 - End
}
