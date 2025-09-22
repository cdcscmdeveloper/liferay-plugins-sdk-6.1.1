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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtEpvApprovalReqAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.type.pm.RoleCode;
import my.ep.util.CustomUtils;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.collections.CollectionUtils;
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
public class QTScheduleDetailBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final long serialVersionUID = 1L;

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
		
		ScQt scQt = this.getManagedBean().getQt();
		if(scQt != null){
			message += " @qtNo = "+scQt.getQtNo();
		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		/*else {
			log.info(message);
		}*/
		
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
		ICountrySO countrySo;
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
		// retrievePmDistrictList();
		retrievePmDivisions();
		}finally{
			countrySo=null;	
		}
	}

	/**
	 * Retrieve division list by state id (only applicable for sarawak).
	 */
	public void retrievePmDivisions() {
		debugLogger("retrievePmDistrictList");
		this.getManagedBean().setState(false);
		if (null != this.getManagedBean().getScQtSampleSubmission()) {
			Long stateId = this.getManagedBean().getScQtSampleSubmission()
					.getScAddress().getPmState().getStateId();
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
							.getScQtSampleSubmission().getScAddress()
							.getPmDivision();
					if (null != division) {
						this.getManagedBean().getScQtSampleSubmission()
								.getScAddress().getPmDivision()
								.setDivisionId(null);
					} else {
						this.getManagedBean().getScQtSampleSubmission()
								.getScAddress().setPmDivision(new PmDivision());
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
		IDistrictSO reqSO =null;
		try{
		if (null != this.getManagedBean().getScQtSampleSubmission()) {
			this.getManagedBean().setDistricts(null);
			Long divisionId = null;
			if (null != this.getManagedBean().getScQtSampleSubmission()
					.getScAddress().getPmDivision()) {
				divisionId = this.getManagedBean().getScQtSampleSubmission()
						.getScAddress().getPmDivision().getDivisionId();
			}
			Long stateId = this.getManagedBean().getScQtSampleSubmission()
					.getScAddress().getPmState().getStateId();
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
			reqSO =null;
		}
	}

	/**
	 * Retrieve city list by district id.
	 */
	public void retrievePmCityList() {
		debugLogger("retrievePmCityList");
		this.getManagedBean().setCitys(null);
		ICitySO reqSO =null;
		try{
		if (null != this.getManagedBean().getScQtSampleSubmission()) {
			Long districtId = this.getManagedBean().getScQtSampleSubmission()
					.getScAddress().getPmDistrict().getDistrictId();
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
	 * Gets the desk officer admin assistant.
	 * 
	 * @return the desk officer admin assistant
	 */
	public void getDeskOfficerAdminAssistant() {
		debugLogger("getDeskOfficerAdminAssistant");
		IUserSO iUserSO =null;
		try{
		if (null == getManagedBean().getDeskOfficerAdmin()) {
			 iUserSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserSO.class);
			UserReqDTO userReqDto = new UserReqDTO();
			userReqDto.setRoleCode(RoleCode.DESK_ASSISTANT.value());
			PmOrgProfile pmOrgProfile = getManagedBean().getQt().getPmOrgProfile();
			
			//Enhancement #21877
			List<Long> orgProfileIds=new ArrayList<Long>();
			if (pmOrgProfile != null) {	
				orgProfileIds.add(pmOrgProfile.getOrgProfileId());
			}
			
			PmOrgProfile OwnerPmOrgProfile = getManagedBean().getQt().getOwnerOrgProfile();	
			//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			if (OwnerPmOrgProfile != null && pmOrgProfile != null &&pmOrgProfile.getOrgProfileId() != OwnerPmOrgProfile.getOrgProfileId()) {
				orgProfileIds.add(OwnerPmOrgProfile.getOrgProfileId());	
			}
			
			userReqDto.setOrgProfileIds(orgProfileIds);
			UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
			List<PmUser> deskOfficer = userResDTO.getUserList();
			getManagedBean().setDeskOfficerAdmin(deskOfficer);
		}
		}finally{
			 iUserSO =null;
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
		List<ScQtContactPerson> contactPersonList=	this.getManagedBean().getToBeRemovalContactPersonList();
		if(null== contactPersonList){
			contactPersonList=new ArrayList<ScQtContactPerson>();
		}
		if (null == selectcontactPersonsList
				|| selectcontactPersonsList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760098);
		} else {
			contactPersonList.addAll(selectcontactPersonsList);
			for (ScQtContactPerson scQtContactPerson : selectcontactPersonsList) {
				contactPersonsList.remove(scQtContactPerson);
			}
		}
		this.getManagedBean().setToBeRemovalContactPersonList(contactPersonList);
		this.getManagedBean().setSelectedContactPersons(null);
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
		//#28895 - 35500 start
		List<ScQtContactPerson> deletedContactList = new ArrayList<>();
		List<ScQtContactPerson> newDoaList = this.getManagedBean().getNewDoaList();
		//#28895 - 35500 end
		
		if (null == selectedContactList || selectedContactList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760098);
		} else {
			for (ScQtContactPerson contactPerson : selectedContactList) {
				contactList.remove(contactPerson);
				deletedContactList.add(contactPerson);
				//#28895-35500 start
				if (newDoaList != null && newDoaList.contains(contactPerson)){
					newDoaList.remove(contactPerson);
				}
			}
			this.getManagedBean().setPmUserDeletedContactPersonList(deletedContactList);
			//#28895 - 35500 end
		}
		this.getManagedBean().setSelectedpmuserContactpersonList(null);
	}

	/**
	 * Retrieve pmuser name.
	 * 
	 * @param contactPerson
	 *            the contact person
	 */
	public void retrievePmuserName(ScQtContactPerson contactPerson, int index) {
		debugLogger("retrievePmuserName");
		/*
		 * List<Long> doaaIdList = new ArrayList<Long>();
		 * 
		 * if (doaaList != null && doaaList.size() > 0) { for (ScQtContactPerson
		 * doa : doaaList) { //String officeType =
		 * SourcingConstants.CONTACT_PERSON_TYPE_DOAA; if (null !=
		 * doa.getUser()&& null != doa.getUser().getUserId()) {
		 * doaaIdList.add(doa.getUser().getUserId()); } } }
		 */
		Long previousId = null;
		if(contactPerson.getUserId()==null){
			previousId = contactPerson.getUser().getUserId();
		}
		List<PmUser> list = this.getManagedBean().getDeskOfficerAdmin();
		list = list == null ? new ArrayList<PmUser>(): list;
		
		List<ScQtContactPerson> list2 = this.getManagedBean().getPmuserContactpersonList();
		list2 = list2 == null ? new ArrayList<ScQtContactPerson>(): list2;
		
		Map<Object, Object> map = CustomUtils.listToMap(list, "getUserId");
		Map<Object, Object> map2 = CustomUtils.listToMap(list2, "getUser.getUserId");
		
		if(previousId!=null){
			ScQtContactPerson person = (ScQtContactPerson)map2.get(previousId);
			person.setUser(new PmUser());
		}
		
		if(map2.get(contactPerson.getUserId()) != null){
			contactPerson.setUserId(null);
			throw new EpException(getMessage("lbl.sc.alreadyExisted", new String[]{}));
		}
		PmUser newUser = (PmUser)map.get(contactPerson.getUserId());
		if(newUser!=null){
			contactPerson.setName(newUser.getUserName());
			contactPerson.setEmail(newUser.getEmail());
			contactPerson.setPhoneNo(newUser.getPhoneNo());
			contactPerson.setFaxNo(newUser.getFaxNo());
			contactPerson.setUser(newUser);
		}
		
		//28895 - 35500 start
		List<ScQtContactPerson> newDoaList = this.getManagedBean().getNewDoaList();
		newDoaList = newDoaList == null ? new ArrayList<ScQtContactPerson>() : newDoaList;
		newDoaList.add(contactPerson);
		this.getManagedBean().setNewDoaList(newDoaList);
		//#28895-35500 end
		
//		if (list != null) {
//			for (PmUser user : list) {
//				if (user.getUserId().equals(contactPerson.getUserId())) {
//					contactPerson.setName(user.getUserName());
//					contactPerson.setEmail(user.getEmail());
//					contactPerson.setPhoneNo(user.getPhoneNo());
//					contactPerson.setFaxNo(user.getFaxNo());
//					contactPerson.setUser(user);
//					break;
//				}
//
//			}
//		}
	}

	/**
	 * Will be call when we click apply button for address.
	 */
	public void applyAddessToSelectedZonesAction() {
		debugLogger("applySampleSubmission");
		ICountrySO countrySo =null;
		try{
		if (!this.getManagedBean().getSelectedSubmissionList().isEmpty()) {
			ScQtSampleSubmission sampleSubmission = new ScQtSampleSubmission();
			ScAddress address = new ScAddress();
			address.setPmCity(new PmCity());
			address.setPmState(new PmState());
			address.setPmDistrict(new PmDistrict());
			address.setPmDivision(new PmDivision());
			PmCountry country = new PmCountry();
			 countrySo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICountrySO.class);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			country.setCountryId(countryResDTO.getPmCountry().getCountryId());
			address.setPmCountry(country);
			sampleSubmission.setScAddress(address);
			this.getManagedBean().setScQtSampleSubmission(sampleSubmission);
			retrievePmStateList();
			this.getManagedBean().setActionType(null);
			RequestContext.getCurrentInstance().execute("sampledlg.show();");
			this.getManagedBean().setRequired(Boolean.TRUE);
		} else {
			throw new EpException(MessageConst.ERROR_760232);
		}
		}finally{
		getManagedBean().setAutoUpdate(false);
		 countrySo =null;
		}
	}

	/**
	 * Will be call if clicks OK action on sample dailog screen..
	 */
	public void okSubmission() {
		debugLogger("okSubmission");
		List<BriefingZoneBean> zoneBean=this.getManagedBean().getSampleAddressBean();//11104-start
		if(null==zoneBean){
			zoneBean=new ArrayList<BriefingZoneBean>();
			this.getManagedBean().setSampleAddressBean(zoneBean);
		}//11104-End
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
		ScAddress address=	this.getManagedBean().getScQtSampleSubmission().getScAddress();//11104-start
			if(CollectionUtils.isEmpty(zoneBean)){
				BriefingZoneBean briefBean=new BriefingZoneBean(this.getManagedBean().getScQtSampleSubmission().getTplSpecZoneDtl(),address.getPmState().getStateId(),address.getPmDivision().getDivisionId(),address.getPmDistrict().getDistrictId(),address.getPmCity().getCityId());
				zoneBean.add(briefBean);
				}else{
					int index=0;
					int equalindex=0;
					BriefingZoneBean bean=null;
					for(BriefingZoneBean mainBean: zoneBean){
						if(mainBean.getTplSpecZoneDtl().getSpecZoneId().equals(this.getManagedBean().getScQtSampleSubmission().getTplSpecZoneDtl().getSpecZoneId())|| mainBean.getTplSpecZoneDtl().getSpecZoneId()==this.getManagedBean().getScQtSampleSubmission().getTplSpecZoneDtl().getSpecZoneId()){
							equalindex=index;
							bean=new BriefingZoneBean(this.getManagedBean().getScQtSampleSubmission().getTplSpecZoneDtl(),address.getPmState().getStateId(),address.getPmDivision().getDivisionId(),address.getPmDistrict().getDistrictId(),address.getPmCity().getCityId());
						}
						index++;
					}
					if(null!=bean){
						zoneBean.remove(equalindex);
						zoneBean.add(equalindex,bean);
						
					}else{
						bean=new BriefingZoneBean(this.getManagedBean().getScQtSampleSubmission().getTplSpecZoneDtl(),address.getPmState().getStateId(),address.getPmDivision().getDivisionId(),address.getPmDistrict().getDistrictId(),address.getPmCity().getCityId());
						zoneBean.add(bean);
					}
				}
			 this.getManagedBean().setSampleAddressBean(zoneBean);//11104-End
		} else {
			submissionList = this.getManagedBean().getSelectedSubmissionList();
			if (null == submissionList || submissionList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760100);
			}
			for (ScQtSampleSubmission sampleSubmission : submissionList) {
				sampleSubmission.setScAddress(this.getManagedBean()
						.getScQtSampleSubmission().getScAddress());
			ScAddress address=	this.getManagedBean()
				.getScQtSampleSubmission().getScAddress();//11104-start
			if(CollectionUtils.isEmpty(zoneBean)){
			BriefingZoneBean briefBean=new BriefingZoneBean(sampleSubmission.getTplSpecZoneDtl(),address.getPmState().getStateId(),address.getPmDivision().getDivisionId(),address.getPmDistrict().getDistrictId(),address.getPmCity().getCityId());
			zoneBean.add(briefBean);
			}else{
				int index=0;
				int equalindex=0;
				BriefingZoneBean bean=null;
				for(BriefingZoneBean mainBean: zoneBean){
					if(mainBean.getTplSpecZoneDtl().getSpecZoneId().equals(sampleSubmission.getTplSpecZoneDtl().getSpecZoneId())|| mainBean.getTplSpecZoneDtl().getSpecZoneId()==sampleSubmission.getTplSpecZoneDtl().getSpecZoneId()){
						equalindex=index;
						bean=new BriefingZoneBean(sampleSubmission.getTplSpecZoneDtl(),address.getPmState().getStateId(),address.getPmDivision().getDivisionId(),address.getPmDistrict().getDistrictId(),address.getPmCity().getCityId());
					}
					index++;
				}
				if(null!=bean){
					zoneBean.remove(equalindex);
					zoneBean.add(equalindex,bean);
					
				}else{
					bean=new BriefingZoneBean(sampleSubmission.getTplSpecZoneDtl(),address.getPmState().getStateId(),address.getPmDivision().getDivisionId(),address.getPmDistrict().getDistrictId(),address.getPmCity().getCityId());
					zoneBean.add(bean);
				}
			}
			}
			
			 this.getManagedBean().setSampleAddressBean(zoneBean);//11104-End
			this.getManagedBean().setSelectedSubmissionList(null);
		}

		// this.getManagedBean().setScQtSampleSubmission(null);
		RequestContext.getCurrentInstance().execute("sampledlg.hide();");
		this.getManagedBean().setRequired(Boolean.FALSE);
	}

	/**
	 * To update publish date
	 */
	public void handlePublishedDate() {
		debugLogger("handlePublishedDate");
		ScQt scQt = this.getManagedBean().getQt();
		Date newPublishDate = scQt.getNewPublishDate();
		if (newPublishDate != null) {
			scQt.setPublishDate(DateUtils.setDefaultDateTime(newPublishDate));
			
			if (!scQt.isBsvRequired()) {
				scQt.setProposalStartDate(scQt.getPublishDate());
			}
			
			updatePublishClosingDate(scQt);
		}
	}
	
	public String getlanguageKey(){
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getExternalContext().getRequestLocale().toString();
	}

	public void handleChmnFinalizationDate(boolean validityDaysExpiredQt) {
		ScQt scQt = this.getManagedBean().getQt();
		Date chmnFinalizationDate = scQt.getChmnFinalizationDate();
		if (chmnFinalizationDate != null) {
			scQt.setChmnFinalizationDate(DateUtils.setDefaultDateTime(chmnFinalizationDate));
		}
		//#21692 attachment - Start
		Boolean oldApprovalPBM = scQt.isApprovalPBM();
		Boolean enableDateMOF = enableApprovalDateMOF(validityDaysExpiredQt);
		if (!enableDateMOF) {
			scQt.setMofDate(null);
		}
		scQt.setApprovalPBM(enableDateMOF);
		resetDecisionCertPBM(enableDateMOF);
		if ((oldApprovalPBM && !enableDateMOF)
				|| (!oldApprovalPBM && enableDateMOF)) {
			deleteEpvAttachments();
		}
		//#21692 attachment - End
	}
	//#21692 attachment - Start
	/**
	 * delete existing attachment and reset decision
	 */
	public void deleteEpvAttachments() {
		ScQtApprovalRequest apprvRequest = this.getManagedBean().getScQtApprovalRequestList().get(0);
		//#21692 only remove decision and attachment, do not save data - Start
		apprvRequest.setEpvApprovalReqAttachmentR1(null);
		apprvRequest.setEpvApprovalReqAttachmentR2(null);
		apprvRequest.setEpvApprovalReqAttachmentR3(null);
		if (CollectionUtils.isEmpty(this.getManagedBean().getToBeRemoveEPVAttachments())) {
			this.getManagedBean().setToBeRemoveEPVAttachments(new ArrayList<String>());
		}
		this.getManagedBean().getToBeRemoveEPVAttachments().add(ParameterConst.EPV_TYPE_REQUEST_1);
		this.getManagedBean().getToBeRemoveEPVAttachments().add(ParameterConst.EPV_TYPE_REQUEST_2);
		this.getManagedBean().getToBeRemoveEPVAttachments().add(ParameterConst.EPV_TYPE_REQUEST_3);
		apprvRequest.setDecisionCertPBM(null);
		apprvRequest.setDecisionType(null);
		
		List<ScQtApprovalRequest> newApprovalReqList = new ArrayList<>();
		if (this.getManagedBean().getQt().isApprovalPBM()) {
			apprvRequest.setDecisionCertPBM(new PmParameter());
		}
		apprvRequest.setDecisionType(new PmParameter());
		newApprovalReqList.add(apprvRequest);
		//#21692 only remove decision and attachment, do not save data - End
		this.getManagedBean().setScQtApprovalRequestList(newApprovalReqList);
	}
	//#21692 attachment - End

	private void resetDecisionCertPBM(Boolean enableDateMOF) {
		List<ScQtApprovalRequest> approvalRequests = getManagedBean().getScQtApprovalRequestList();
		if (CollectionUtils.isNotEmpty(approvalRequests)) {
			for (ScQtApprovalRequest approvalRequest : approvalRequests) {
				PmParameter pmParameter = new PmParameter();
				if (enableDateMOF) {
					if (approvalRequest.getDecisionCertPBM() == null) {
						approvalRequest.setDecisionCertPBM(pmParameter);
					}
				} else {
					approvalRequest.setDecisionCertPBM(null);
				}
			}
		}
	}

	public boolean enableApprovalDateMOF(boolean validityDaysExpiredQt) {
		ScQt scQt = this.getManagedBean().getQt();
		boolean enableApprovalDateMOF = Boolean.FALSE;
		if (!validityDaysExpiredQt) {
			log.info("Invalid");
			Date proposalValidityEndDate = scQt.getProposalValidityEndDate();
			Date chmnFinalizationDate = scQt.getChmnFinalizationDate();
			if ((null != proposalValidityEndDate) && (null != chmnFinalizationDate)) {
				log.info("proposalValidityEndDate: " + proposalValidityEndDate);
				log.info("chmnFinalizationDate: " + chmnFinalizationDate);

				long days = DateUtils.getDaysDifference(proposalValidityEndDate, chmnFinalizationDate);
				int day = (int) days;
				if (day > 0) {//#21692
					return Boolean.TRUE;
				}
			}
		}

		return enableApprovalDateMOF;
	}

	public void handleMofDate() {
		ScQt scQt = this.getManagedBean().getQt();
		Date mofDate = scQt.getMofDate();
		if (mofDate != null) {
			scQt.setMofDate(DateUtils.setDefaultDateTime(mofDate));
		}
	}

	/**
	 * To update publish date for reschedule
	 */
	public void handleReschedulePublishedDate() {
		debugLogger("handleReschedulePublishedDate");
		
		ScQt scQt = this.getManagedBean().getQt();
		Date oldPublishDate = scQt.getPublishDate();
		Date newPublishDate = scQt.getNewPublishDate();
		if (newPublishDate != null) {
			newPublishDate = DateUtils.setDefaultDateTime(newPublishDate);
			scQt.setPublishDate(newPublishDate);
			
			if (!scQt.isBsvRequired()) {
				scQt.setProposalStartDate(scQt.getPublishDate());
			}
			
			updatePublishClosingDate(scQt);
			
			QtAmendMentBean amendMentBean = this.getManagedBean()
					.getAmendMentBean();
			if (null == amendMentBean) {
				amendMentBean = new QtAmendMentBean();	
				this.getManagedBean().setAmendMentBean(amendMentBean);
			}
			new QTBriefingSiteVisitBackingBean().putIntoAmendMentBeanIfChanged(
					amendMentBean, 
					QTFieldConst.FIELD_QT_PUBLISH_DATE, 
					oldPublishDate, newPublishDate,
					false);
		}
	}
	
	/**
	 * To update closing date and proposal validity days for reschedule
	 */
	public void handleReschedulePublishClosingDate() {
		debugLogger("handleReschedulePublishClosingDate");
		ScQt scQt = this.getManagedBean().getQt();
		Date oldClosingDate = scQt.getClosingDate();
		Date newClosingDate = scQt.getNewClosingDate();
		if (newClosingDate != null) {
			newClosingDate = DateUtils.setDefaultDateTime(newClosingDate);
			scQt.setClosingDate(newClosingDate);
			if(scQt.isSampleReq()){
				scQt.setSampleClosingDate(scQt.getClosingDate());
			}
			
			updateProposalValidityDate(scQt);
			
			QtAmendMentBean amendMentBean = this.getManagedBean()
					.getAmendMentBean();
			
			if (null == amendMentBean) {
				amendMentBean = new QtAmendMentBean();
				
				this.getManagedBean().setAmendMentBean(amendMentBean);
			}
			
			new QTBriefingSiteVisitBackingBean().putIntoAmendMentBeanIfChanged(
					amendMentBean, 
					QTFieldConst.FIELD_QT_CLOSING_DATE, 
					oldClosingDate, newClosingDate,
					false);
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
		ICountrySO countrySo =null;
		try{
		if (sampleSubmission.getScAddress() == null) {
			ScAddress address = new ScAddress();
			address.setPmCity(new PmCity());
			address.setPmState(new PmState());
			address.setPmDistrict(new PmDistrict());
			address.setPmDivision(new PmDivision());
			PmCountry country = new PmCountry();
			 countrySo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICountrySO.class);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			country.setCountryId(countryResDTO.getPmCountry().getCountryId());
			address.setPmCountry(country);
			sampleSubmission.setScAddress(address);
		}
		}finally{
			countrySo =null;	
		}
		this.getManagedBean().setSampleAddressIndex(index);
		this.getManagedBean().setActionType(SourcingConstants.ACTION_EDIT);
		this.getManagedBean().setScQtSampleSubmission(sampleSubmission);
		this.getManagedBean().getScQtSampleSubmissionList()
				.set(index, this.getManagedBean().getScQtSampleSubmission());
		retrievePmStateList();
		RequestContext.getCurrentInstance().execute("sampledlg.show();");
		this.getManagedBean().setRequired(Boolean.TRUE);
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * Will be call when we click cancel action of open dailog.
	 */
	public void closeSampleAddressPopUp() {
		debugLogger("closeSampleAddressPopUp");
		
		ScQtSampleSubmission sampleSubmission = this.getManagedBean()
				.getScQtSampleSubmission();
		if (SourcingConstants.ACTION_EDIT.equalsIgnoreCase(this
				.getManagedBean().getActionType())
				&& sampleSubmission != null
				&& sampleSubmission.getQtSampleSubmissionId() == null) {
			this.getManagedBean().setScQtSampleSubmission(null);
			int index = this.getManagedBean().getSampleAddressIndex();
			ScQtSampleSubmission sampleSubmissionFrmList  = this.getManagedBean()
					.getScQtSampleSubmissionList().get(index);
			sampleSubmissionFrmList.setScAddress(null);
			this.getManagedBean()
			.getScQtSampleSubmissionList().set(index,sampleSubmissionFrmList);
					
							
		}
		
		RequestContext.getCurrentInstance().execute("sampledlg.hide();");
		this.getManagedBean().setRequired(Boolean.FALSE);
		this.getManagedBean().setAutoUpdate(Boolean.TRUE);
	}

	/**
	 * Handle maximum date.
	 * 
	 * @return the date
	 */
	public Date handleMaximumDate() {
		debugLogger("handleMaximumDate");
		ScQt scQt = this.getManagedBean().getQt();
		Date proposalStartDate = scQt.getProposalStartDate();

		if (scQt.isBsvRequired()
				&& proposalStartDate != null
				&& DateUtils.isDateGreater(proposalStartDate, new Date())) {

			return DateUtils.minusDays(proposalStartDate, 1);

		} else {
			Calendar c1 = Calendar.getInstance();
			c1.add(Calendar.YEAR, 50);
			return c1.getTime();
		}

	}

	/**
	 * Validate postcode.
	 */
	public void validatePostcode() {
		debugLogger("validatePostcode");
		String postcode = getManagedBean().getScQtSampleSubmission()
				.getScAddress().getPostcode();
		Pattern pattern = Pattern.compile("\\d{5}");
		Matcher matcher = pattern.matcher(postcode);
		if (matcher.matches()) {
			// throw new EpException("Postcode is Valid");
		} else {
			throw new EpException(
					"Postcode is not valid, Postcode Should be limited to 5 digits.");
		}

	}
	
	/**
	 * Close listener.close popup window.
	 */
	public void closeListener() {
		this.getManagedBean().setRequired(Boolean.FALSE);
		//this.getManagedBean().setZoneLocation(Boolean.FALSE);
		this.getManagedBean().setAutoUpdate(true);
	}

}