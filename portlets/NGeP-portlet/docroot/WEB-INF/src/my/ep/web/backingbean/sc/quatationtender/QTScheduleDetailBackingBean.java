package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * @author SudheerKumar
 * 
 */
@ManagedBean(name = "qTScheduleDetailBackingBean")
@RequestScoped
public class QTScheduleDetailBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	private static final Logger log = EpLogger
			.getLogger(QTScheduleDetailBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	private Boolean checked;

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public QTScheduleDetailBackingBean() {

	}

	@PostConstruct
	public void initDetails() {

	}

	public void retrievePmStateList() {
		debugLogger("retrievePmStateList");
		if (null == this.getManagedBean().getStates()) {
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(1l);
			this.getManagedBean().setDistricts(null);
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IStateSO.class);
			StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
			List<PmState> list = resDTO.getStateList();
			this.getManagedBean().setStates(list);

		}
		retrievePmDistrictList();
	}

	public void retrievePmDistrictList() {
		Long stateId = this.getManagedBean().getScQtSampleSubmission()
				.getAddress().getPmState().getStateId();
		this.getManagedBean().setDistricts(null);
		if (stateId != null) {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(stateId);
			IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDistrictSO.class);
			DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
			this.getManagedBean().setDistricts(resDTO.getDistrictList());
			retrievePmCityList();
		}

	}

	public void retrievePmCityList() {
		Long districtId = this.getManagedBean().getScQtSampleSubmission()
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

	public void addContactPerson() {
		debugLogger("addContactPerson");
		ScQtContactPerson contactperson = new ScQtContactPerson();

		List<ScQtContactPerson> contactList = this.getManagedBean()
				.getScQtContactPersons();
		if (null == contactList) {
			contactList = new ArrayList<ScQtContactPerson>();
		}
		contactperson.setOfficerType(SourcingConstants.CONTACT_PERSON_TYPE);
		contactperson.setScQt(getManagedBean().getQt());
		contactList.add(contactperson);
		this.getManagedBean().setScQtContactPersons(contactList);

	}

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

	public void addDeskOfficerAdminAssistant() {
		debugLogger("addDeskOfficerAdminAssistant");
		ScQtContactPerson contactPerson = new ScQtContactPerson();
		try {
			List<ScQtContactPerson> contactList = this.getManagedBean()
					.getPmuserContactpersonList();
			if (null == contactList) {
				contactList = new ArrayList<ScQtContactPerson>();
			}
			contactPerson.setUser(new PmUser());
			contactPerson
					.setOfficerType(SourcingConstants.CONTACT_PERSON_TYPE_DOAA);
			contactPerson.setScQt(getManagedBean().getQt());
			contactList.add(contactPerson);
			this.getManagedBean().setPmuserContactpersonList(contactList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

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

	public void retrievePmuserName(ScQtContactPerson contactPerson) {
		debugLogger("retrievePmuserName");
		List<PmUser> list = this.getManagedBean().getDeskOfficerAdmin();
		if (list != null) {
			for (PmUser user : list) {
				if (user.getUserId().equals(contactPerson.getUser().getUserId())) {
					contactPerson.setName(user.getUserName());
					contactPerson.setEmail(user.getEmail());
					contactPerson.setPhoneNo(user.getPhoneNo());
					contactPerson.setFaxNo(user.getFaxNo());
					contactPerson.setUser(user);
					break;
				}
			}
		}
	}
	
	/**
	 * Will be call when we click apply button for address.
	 */
	public void applyAddessToSelectedZonesAction() {

		debugLogger("applySampleSubmission");
		ScQtSampleSubmission sampleSubmission = new ScQtSampleSubmission();
		PmAddress address = new PmAddress();
		address.setPmCity(new PmCity());
		address.setPmState(new PmState());
		address.setPmDistrict(new PmDistrict());
		PmCountry country = new PmCountry();
		country.setCountryId(1l);
		address.setPmCountry(country);
		sampleSubmission.setAddress(address);
		this.getManagedBean().setScQtSampleSubmission(sampleSubmission);
		retrievePmStateList();
	}

	/**
	 * Will be call if clicks OK action on sample dailog screen..
	 */
	public void okSubmission() {
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
	}

	/**
	 * To update closing date and proposal validity days..
	 */
	public void handleClosingNProposalDate() {
		ScQt scQt = this.getManagedBean().getQt();
		Date clsDate = null;
		if (scQt.isBsvRequired()) {
			/*
			 * List<ScQtBsv> bsvDetails = scQt.getScQtBriefings(); Date
			 * briefingDate=null; if(null == bsvDetails){ //TODO Need to write
			 * api to get higest date. briefingDate= new Date(); }else{
			 * for(ScQtBsv bsvs : bsvDetails){ List<ScQtBsvDtl> dtls =
			 * bsvs.getScQtBsvDtls(); for(int i=0;i<dtls.size();i++){
			 * 
			 * } } }
			 */
			clsDate = DateUtils.addDays(scQt.getPublishDate(),
					scQt.getPublishPeriod());
		} else {
			clsDate = DateUtils.addDays(scQt.getPublishDate(),
					scQt.getPublishPeriod());
		}
		scQt.setClosingDate(clsDate);

		getManagedBean().setProposalValidityUntil(
				DateUtils.addDays(clsDate, scQt.getQtValidityPeriod()));
	}

	/**
	 * Will be called if its zone based and clicks edit icon..
	 * 
	 * @param sampleSubmission
	 */
	public void editAddress(ScQtSampleSubmission sampleSubmission, int index) {
		if (sampleSubmission.getAddress() == null) {
			PmAddress address = new PmAddress();
			address.setPmCity(new PmCity());
			address.setPmState(new PmState());
			address.setPmDistrict(new PmDistrict());
			PmCountry country = new PmCountry();
			country.setCountryId(1l);
			address.setPmCountry(country);
			sampleSubmission.setAddress(address);
		}
		this.getManagedBean().setSampleAddressIndex(index);
		this.getManagedBean().setActionType(SourcingConstants.ACTION_EDIT);
		this.getManagedBean().setScQtSampleSubmission(sampleSubmission);
		retrievePmStateList();
		RequestContext.getCurrentInstance().execute("sampledlg.show();");
	}

	/**
	 * Will be call when we click cancel action of open dailog.
	 */
	public void closeSampleAddressPopUp() {
		this.getManagedBean().setScQtSampleSubmission(null);
		RequestContext.getCurrentInstance().execute("sampledlg.hide();");
	}
	
	

}