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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
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
import my.ep.jaxb.sc.sitevisitproposaldata.SCSiteVisitProposalDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

// TODO: Auto-generated Javadoc
/**
 * The Class QTScheduleDetailBackingBean.
 * 
 * @author Viswanath
 */
@ManagedBean(name = "qTScheduleDetailBackingBean")
@RequestScoped
public class QTScheduleDetailBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTScheduleDetailBackingBean.class);

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/** The checked. */
	private Boolean checked;

	/**
	 * Gets the checked.
	 * 
	 * @return the checked
	 */
	public Boolean getChecked() {
		return checked;
	}

	/**
	 * Sets the checked.
	 * 
	 * @param checked
	 *            the new checked
	 */
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	/**
	 * Instantiates a new QT schedule detail backing bean.
	 */
	public QTScheduleDetailBackingBean() {

	}

	/**
	 * Inits the details.
	 */
	@PostConstruct
	public void initDetails() {

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
		// retrievePmDistrictList();
		retrievePmDivisions();
	}

	/**
	 * Retrieve division list by state id (only applicable for sarawak).
	 */
	public void retrievePmDivisions() {
		debugLogger("retrievePmDistrictList");
		this.getManagedBean().setState(false);
		if (null != this.getManagedBean().getScQtSampleSubmission()) {
			Long stateId = this.getManagedBean().getScQtSampleSubmission()
					.getAddress().getPmState().getStateId();
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
					PmDivision division = this.getManagedBean()
							.getScQtSampleSubmission().getAddress()
							.getPmDivision();
					if (null != division) {
						this.getManagedBean().getScQtSampleSubmission()
								.getAddress().getPmDivision()
								.setDivisionId(null);
					} else {
						this.getManagedBean().getScQtSampleSubmission()
								.getAddress().setPmDivision(new PmDivision());
					}
				}
			}
		}
		retrievePmDistrictList();
	}

	/**
	 * Retrieve district list by state id.
	 */
	public void retrievePmDistrictList() {
		debugLogger("retrievePmDistrictList");
		if (null != this.getManagedBean().getScQtSampleSubmission()) {
			this.getManagedBean().setDistricts(null);
			Long divisionId = null;
			if (null != this.getManagedBean().getScQtSampleSubmission()
					.getAddress().getPmDivision()) {
				divisionId = this.getManagedBean().getScQtSampleSubmission()
						.getAddress().getPmDivision().getDivisionId();
			}
			Long stateId = this.getManagedBean().getScQtSampleSubmission()
					.getAddress().getPmState().getStateId();
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
		debugLogger("retrievePmCityList");
		this.getManagedBean().setCitys(null);
		if (null != this.getManagedBean().getScQtSampleSubmission()) {
			Long districtId = this.getManagedBean().getScQtSampleSubmission()
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
	 * Gets the desk officer admin assistant.
	 * 
	 * @return the desk officer admin assistant
	 */
	public void getDeskOfficerAdminAssistant() {
		debugLogger("getDeskOfficerAdminAssistant");
		if (null == getManagedBean().getDeskOfficerAdmin()) {
			IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserSO.class);
			UserReqDTO userReqDto = new UserReqDTO();
			userReqDto.setRoleCode(RoleCode.DESK_ASSISTANT.value());
			UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
			List<PmUser> deskOfficer = userResDTO.getUserList();
			getManagedBean().setDeskOfficerAdmin(deskOfficer);
		}

	}

	/**
	 * Add new contact person to list.
	 */
	public void addContactPerson() {
		debugLogger("addContactPerson");
		ScQtContactPerson contactperson = new ScQtContactPerson();

		List<ScQtContactPerson> contactList = this.getManagedBean()
				.getScQtContactPersons();
		if (null == contactList) {
			contactList = new ArrayList<ScQtContactPerson>();
		}
		contactperson.setOfficerType(SourcingConstants.OFFICER_TYPE_QT);
		contactperson.setScQt(getManagedBean().getQt());
		contactList.add(contactperson);
		this.getManagedBean().setScQtContactPersons(contactList);

	}

	/**
	 * Delete contact person from list.
	 */
	public void deleteContactPerson() {
		debugLogger("deleteContactPerson");
		List<ScQtContactPerson> contactPersonsList = getManagedBean()
				.getScQtContactPersons();
		List<ScQtContactPerson> selectcontactPersonsList = getManagedBean()
				.getSelectedContactPersons();
		if (null == selectcontactPersonsList
				|| selectcontactPersonsList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760098);
		} else {
			for (ScQtContactPerson scQtContactPerson : selectcontactPersonsList) {
				contactPersonsList.remove(scQtContactPerson);
			}
		}
	}

	/**
	 * Handle desk officer changed.
	 * 
	 * @param user
	 *            the user
	 */
	public void handleDeskOfficerChanged(PmUser user) {
		List<PmUser> list = getManagedBean().getDeskOfficerAdmin();
		for (PmUser pmUser : list) {
			if (pmUser.getUserId() == user.getUserId()) {
				getManagedBean().setPmUser(pmUser);
			}
		}
		getManagedBean().setPmUsersList(list);
		// getManagedBean().getQtContactPerson().setUser(user);
	}

	/**
	 * Adds new desk officer admin assistant to list.
	 */
	public void addDeskOfficerAdminAssistant() {
		debugLogger("addDeskOfficerAdminAssistant");

		List<ScQtContactPerson> doaaList = this.getManagedBean()
				.getPmuserContactpersonList();
		if (null == doaaList) {
			doaaList = new ArrayList<ScQtContactPerson>();

		}
		ScQtContactPerson contactPerson = new ScQtContactPerson();
		contactPerson.setUser(new PmUser());
		contactPerson
				.setOfficerType(SourcingConstants.OFFICER_TYPE_DOAA);
		contactPerson.setScQt(getManagedBean().getQt());
		doaaList.add(contactPerson);
		this.getManagedBean().setPmuserContactpersonList(doaaList);
	}

	/**
	 * Validation desk officer admin assistant.
	 * 
	 * @param doaaList
	 *            the doaa list
	 */
	/*
	 * private void validationDeskOfficerAdminAssistant( List<ScQtContactPerson>
	 * doaaList) { IQTDetailSO detailSO = null; try { detailSO =
	 * EpEjbBusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
	 * 
	 * QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
	 * reqDTO.setDoaaList(doaaList);
	 * detailSO.validateDeskOfficerAdminAssistant(reqDTO); } finally { detailSO
	 * = null; } }
	 */

	/**
	 * Delete desk officer admin assistant from list.
	 */
	public void deleteDeskOfficerAdminAssistant() {
		debugLogger("deleteDeskOfficerAdminAssistant");
		List<ScQtContactPerson> contactList = this.getManagedBean()
				.getPmuserContactpersonList();
		List<ScQtContactPerson> selectedContactList = this.getManagedBean()
				.getSelectedpmuserContactpersonList();
		if (null == selectedContactList || selectedContactList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760098);
		} else {
			for (ScQtContactPerson contactPerson : selectedContactList) {
				contactList.remove(contactPerson);
			}
		}
	}

	/**
	 * Retrieve pmuser name.
	 * 
	 * @param contactPerson
	 *            the contact person
	 */
	public void retrievePmuserName(ScQtContactPerson contactPerson, int index) {
		debugLogger("retrievePmuserName");
		List<PmUser> list = this.getManagedBean().getDeskOfficerAdmin();
		List<ScQtContactPerson> doaaList = this.getManagedBean()
				.getPmuserContactpersonList();
		/*
		 * List<Long> doaaIdList = new ArrayList<Long>();
		 * 
		 * if (doaaList != null && doaaList.size() > 0) { for (ScQtContactPerson
		 * doa : doaaList) { //String officeType =
		 * SourcingConstants.CONTACT_PERSON_TYPE_DOAA; if (null !=
		 * doa.getUser()&& null != doa.getUser().getUserId()) {
		 * doaaIdList.add(doa.getUser().getUserId()); } } }
		 */

		if (list != null) {
			for (PmUser user : list) {
				if (user.getUserId()
						.equals(contactPerson.getUser().getUserId())) {

					contactPerson.setName(user.getUserName());
					contactPerson.setEmail(user.getEmail());
					contactPerson.setPhoneNo(user.getPhoneNo());
					contactPerson.setFaxNo(user.getFaxNo());
					contactPerson.setUser(user);
					break;
				}

			}
		}
		doaaList.remove(index);
		doaaList.add(index, contactPerson);

	}

	/**
	 * Will be call when we click apply button for address.
	 */
	public void applyAddessToSelectedZonesAction() {
		debugLogger("applySampleSubmission");
		if (!this.getManagedBean().getSelectedSubmissionList().isEmpty()) {
			ScQtSampleSubmission sampleSubmission = new ScQtSampleSubmission();
			PmAddress address = new PmAddress();
			address.setPmCity(new PmCity());
			address.setPmState(new PmState());
			address.setPmDistrict(new PmDistrict());
			address.setPmDivision(new PmDivision());
			PmCountry country = new PmCountry();
			ICountrySO countrySo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICountrySO.class);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			country.setCountryId(countryResDTO.getPmCountry().getCountryId());
			address.setPmCountry(country);
			sampleSubmission.setAddress(address);
			this.getManagedBean().setScQtSampleSubmission(sampleSubmission);
			retrievePmStateList();
			RequestContext.getCurrentInstance().execute("sampledlg.show();");
			this.getManagedBean().setRequired(Boolean.TRUE);
		} else {
			throw new EpException(MessageConst.ERROR_760232);
		}
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * Will be call if clicks OK action on sample dailog screen..
	 */
	public void okSubmission() {
		debugLogger("okSubmission");
		List<ScQtSampleSubmission> submissionList = null;
		if (SourcingConstants.ACTION_EDIT.equalsIgnoreCase(this
				.getManagedBean().getActionType())) {
			submissionList = this.getManagedBean()
					.getScQtSampleSubmissionList();
			submissionList
					.remove(this.getManagedBean().getSampleAddressIndex());
			submissionList.add(this.getManagedBean().getSampleAddressIndex(),
					this.getManagedBean().getScQtSampleSubmission());
			this.getManagedBean().setScQtSampleSubmissionList(submissionList);
		} else {
			submissionList = this.getManagedBean().getSelectedSubmissionList();
			if (null == submissionList || submissionList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760100);
			}
			for (ScQtSampleSubmission sampleSubmission : submissionList) {
				sampleSubmission.setAddress(this.getManagedBean()
						.getScQtSampleSubmission().getAddress());
			}
			this.getManagedBean().setSelectedSubmissionList(submissionList);
		}

		// this.getManagedBean().setScQtSampleSubmission(null);
		RequestContext.getCurrentInstance().execute("sampledlg.hide();");
		this.getManagedBean().setRequired(Boolean.FALSE);
	}

	/**
	 * To update closing date and proposal validity days..
	 */
	public void handlePublishClosingDate() {
		debugLogger("handlePublishClosingDate");
		ScQt scQt = this.getManagedBean().getQt();

		Date clsDate = null;
		if (!scQt.isBsvRequired()) {
			if (null != scQt.getPublishDate()) {
				clsDate = DateUtils.addDays(scQt.getPublishDate(),
						scQt.getPublishPeriod());
			}
		} else {
			if (null != scQt.getLatestBsvDate()) {
				clsDate = DateUtils.addDays(scQt.getLatestBsvDate(),
						scQt.getPublishPeriod());
			}
		}
		if (null != clsDate) {
			scQt.setClosingDate(clsDate);
			scQt.setProposalValidityEndDate(DateUtils.addDays(clsDate,
					scQt.getQtValidityPeriod()));
		}if(scQt.isSampleReq()){
			scQt.setSampleClosingDate(clsDate);
		}

	}

	/**
	 * Will be called if its zone based and clicks edit icon..
	 * 
	 * @param sampleSubmission
	 *            the sample submission
	 * @param index
	 *            the index
	 */
	public void editAddress(ScQtSampleSubmission sampleSubmission, int index) {
		debugLogger("editAddress");
		if (sampleSubmission.getAddress() == null) {
			PmAddress address = new PmAddress();
			address.setPmCity(new PmCity());
			address.setPmState(new PmState());
			address.setPmDistrict(new PmDistrict());
			address.setPmDivision(new PmDivision());
			PmCountry country = new PmCountry();
			ICountrySO countrySo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICountrySO.class);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			country.setCountryId(countryResDTO.getPmCountry().getCountryId());
			address.setPmCountry(country);
			sampleSubmission.setAddress(address);
		}
		this.getManagedBean().setSampleAddressIndex(index);
		this.getManagedBean().setActionType(SourcingConstants.ACTION_EDIT);
		this.getManagedBean().setScQtSampleSubmission(sampleSubmission);
		this.getManagedBean().getScQtSampleSubmissionList()
				.set(index, this.getManagedBean().getScQtSampleSubmission());
		retrievePmStateList();
		RequestContext.getCurrentInstance().execute("sampledlg.show();");
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * Will be call when we click cancel action of open dailog.
	 */
	public void closeSampleAddressPopUp() {
		debugLogger("closeSampleAddressPopUp");
		this.getManagedBean().setScQtSampleSubmission(null);
		RequestContext.getCurrentInstance().execute("sampledlg.hide();");
	}

	/**
	 * Handle maximum date.
	 * 
	 * @return the date
	 */
	public Date handleMaximumDate() {
		debugLogger("handleMaximumDate");
		ScQt scQt = this.getManagedBean().getQt();

		if (scQt.isBsvRequired()
				&& DateUtils.isDateGreater(scQt.getLatestBsvDate(), new Date())) {

			Date briefingDate = scQt.getLatestBsvDate();

			return DateUtils.minusDays(briefingDate, 1);

		} else {
			Calendar c1 = Calendar.getInstance();
			c1.add(Calendar.YEAR, 50);
			return c1.getTime();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean#
	 * preRenderView()
	 */
	public void preRenderView() {

		SCSiteVisitProposalDataType payLoad = getBpmTaskDetails(SCSiteVisitProposalDataType.class, null);

		if (SCProcessNameConst.APPROVE_SUPPLIER_ADD.equalsIgnoreCase(this
				.getManagedBean().getTaskName())) {

			IQTBriefingSO briefingSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
			briefingReqDTO.setQtId(payLoad.getQtId());
			briefingReqDTO.setApprovalReqId(Long.valueOf(payLoad
					.getXflagPublication()));

			BriefingResDTO briefingResDTO = briefingSO
					.retrieveRegistartionDtlsByQt(briefingReqDTO);

			List<ScQtApprovalRequest> approvalRequests = briefingResDTO
					.getApprovalRequests();
			ScQtApproval approval = approvalRequests.get(0).getScQtApproval();
			approval.setScQtApprovalRemarks(initializeRemarks(approval, null,
					approval.getScQtApprovalRemarks(), ScQtApprovalRemark.class));
			this.getManagedBean().setScQtApproval(approval);
			this.getManagedBean().setScQtApprovalRequestList(approvalRequests);
			this.getManagedBean().setQt(briefingResDTO.getScQt());
			this.getManagedBean().setBriefingRegistrationList(
					briefingResDTO.getBriefingRegResultBean().getResultList());
			this.getManagedBean().setQtBriefing(
					briefingResDTO.getScQtBriefing());

		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"/xhtml/sc/bsvattendance/approval/additionalAttandanceApproval.xhtml");
	}

	/**
	 * Submit registarion details.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string
	 */
	public String submitRegistarionDetails(String outcome) {

		BriefingReqDTO briefingReqDTO = new BriefingReqDTO();

		FacesScopes facesScopes = new FacesScopes();
		Object ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		
		IQTBriefingSO briefingSO;
		try {
			List<ScQtBsvRegistration> registrations = this.getManagedBean()
					.getBriefingRegistrationList();
			if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
				for (ScQtBsvRegistration registration : registrations) {
					registration
							.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
				}
			}
			briefingReqDTO.setBriefingReqDtls(registrations);
			briefingReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			briefingReqDTO.setScQt(this.getManagedBean().getQt());
			briefingReqDTO.setToken(ctxToken);
			briefingReqDTO.setTaskId(this.getManagedBean().getTaskId());
			briefingReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			briefingReqDTO.setOutcome(outcome);
			briefingReqDTO.setTplSpecZoneDtl(this.getManagedBean()
					.getQtBriefing().getScQtBsv().getTplSpecZoneDtl());
			ScQtApproval approval = this.getManagedBean().getScQtApproval();
			if (null != approval) {
				finalizeRemarks(approval.getScQtApprovalRemarks());
				updateApprovalRequest(this.getManagedBean()
						.getScQtApprovalRequestList(), outcome);
				approval.setScQtApprovalDtls(this.getManagedBean()
						.getScQtApprovalRequestList());
				briefingReqDTO.setApproval(approval);

			}
			briefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);

			BriefingResDTO resDTO = briefingSO
					.updateSupplierRegistionsNSendNotifications(briefingReqDTO);

			if (resDTO.isSuccessful()) {

				renewSesionObject();

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														getMsgCodeByOutCome(outcome),
														new String[] {
																"Additional attandance For Briefing And SiteVisit",
																"" },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} finally {
			briefingSO = null;
		}
		return null;
	}

	/**
	 * Gets the msg code by out come.
	 * 
	 * @param outCome
	 *            the out come
	 * @return the msg code by out come
	 */
	private String getMsgCodeByOutCome(String outCome) {
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outCome)) {
			return MessageConst.INFO_700007;
		}
		return MessageConst.INFO_700008;
	}

	/**
	 * Validate postcode.
	 */
	public void validatePostcode() {
		debugLogger("validatePostcode");
		String postcode = getManagedBean().getScQtSampleSubmission()
				.getAddress().getPostcode();
		Pattern pattern = Pattern.compile("\\d{5}");
		Matcher matcher = pattern.matcher(postcode);
		if (matcher.matches()) {
			// throw new EpException("Postcode is Valid");
		} else {
			throw new EpException(
					"Postcode is not valid, Postcode Should be limited to 5 digits.");
		}

	}
}