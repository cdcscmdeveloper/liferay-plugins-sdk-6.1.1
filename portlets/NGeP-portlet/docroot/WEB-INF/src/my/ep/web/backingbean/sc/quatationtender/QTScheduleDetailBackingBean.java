package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

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
		retrievePmStateList();
		getDeskOfficerAdminAssistant();
	}

	public void retrievePmStateList() {
		debugLogger("retrievePmStateList");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(1l);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		List<PmState> list = resDTO.getStateList();
		this.getManagedBean().setStates(list);
	}

	public void retrievePmDistrictList(AjaxBehaviorEvent event) {
		debugLogger("retrievePmDistrictList");
		Long stateId = (Long) event.getComponent().getAttributes().get("value");
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
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		this.getManagedBean().setCitys(resDTO.getCityList());
	}

	private void getDeskOfficerAdminAssistant() {
		debugLogger("getDeskOfficerAdminAssistant");
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(RoleCode.DESK_ASSISTANT.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		List<PmUser> deskOfficer = userResDTO.getUserList();
		getManagedBean().setDeskOfficerAdmin(deskOfficer);

	}

	public void addContactPerson() {
		debugLogger("addContactPerson");
		ScQtContactPerson contactperson = new ScQtContactPerson();
		try {
			List<ScQtContactPerson> contactList = this.getManagedBean()
					.getScQtContactPersons();
			if (null == contactList) {
				contactList = new ArrayList<ScQtContactPerson>();
			}
			contactperson.setOfficerType("C");
			contactperson.setScQt(getManagedBean().getQt());
			/*for (ScQtContactPerson scQtContactPerson : contactList) {
				scQtContactPerson.setBrandNew(false);
			}*/
			contactList.add(contactperson);
			this.getManagedBean().setScQtContactPersons(contactList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteContactPerson() {
		debugLogger("deleteContactPerson");
		List<ScQtContactPerson> contactPersonsList = getManagedBean()
				.getScQtContactPersons();
		List<ScQtContactPerson> selectcontactPersonsList = getManagedBean()
				.getSelectedContactPersons();
		if (contactPersonsList.size() == selectcontactPersonsList.size()) {
			contactPersonsList.clear();
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
			contactPerson.setOfficerType("A");
			contactPerson.setScQt(getManagedBean().getQt());
			/*for (ScQtContactPerson scQtContactPerson : contactList) {
				scQtContactPerson.setBrandNew(false);
			}*/
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
		if (contactList.size() == selectedContactList.size()) {
			contactList.clear();
		} else {
			for (ScQtContactPerson contactPerson : selectedContactList) {
				contactList.remove(contactPerson);
			}
		}
	}

	public void applySampleSubmission() {
		debugLogger("applySampleSubmission");
		List<ScQtSampleSubmission> sampleSubmissionsList = getManagedBean().getScQtSampleSubmissionList();
		log.info("List : " + sampleSubmissionsList);
	}

	public void okSubmission() {
		debugLogger("okSubmission");
		List<ScQtSampleSubmission> submisssionList = getManagedBean().getScQtSampleSubmissionList();
		PmAddress pmAddress = getManagedBean().getPmAddress();
		PmCountry pmCountry = new PmCountry();
		pmCountry.setCountryId(1L);
		pmAddress.setPmCountry(pmCountry);
		/*String address1 = pmAddress.getAddress1();
		String address2 = pmAddress.getAddress2();
		String address3 = pmAddress.getAddress3();
		String address4 = pmAddress.getAddress4();
		String address = address1 + "," + address2 + "," + address3 + ","
				+ address4;
		log.info("PmAddress : " + address);*/
		if (null == submisssionList) {
			submisssionList = new ArrayList<ScQtSampleSubmission>();
		}
		for (ScQtSampleSubmission scQtSampleSubmission : submisssionList) {
			scQtSampleSubmission.setAddress(pmAddress);
			int index = submisssionList.indexOf(scQtSampleSubmission);
			submisssionList.set(index, scQtSampleSubmission);
		}
		this.getManagedBean().setScQtSampleSubmissionList(submisssionList);
	}
	
	public void handleClosingNProposalDate() {
		Calendar cal = Calendar.getInstance();
		if (null == getManagedBean().getQt().getClosingDate()) {
			cal.setTime(getManagedBean().getQt().getPublishDate());
			cal.add(Calendar.DATE, getManagedBean().getQt().getPublishPeriod());
			getManagedBean().getQt().setClosingDate(cal.getTime());
			handleProposalValidityUtil();
		}
	}

	/**
	 * 
	 */
	public void handleProposalValidityUtil() {
		Calendar cal = Calendar.getInstance();
		if (null == getManagedBean().getProposalValidityUntil()) {
			cal.setTime(getManagedBean().getQt().getClosingDate());
			cal.add(Calendar.DATE, getManagedBean().getQt().getQtValidityPeriod());
			getManagedBean().setProposalValidityUntil(cal.getTime());
		}
		
	}
}