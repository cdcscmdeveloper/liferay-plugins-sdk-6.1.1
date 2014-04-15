package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
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
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
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

	// private static final String NAV_TO_BSV_ATTENDANCE_PAGE =
	// "/xhtml/sc/quatationtender/briefingsitevisit/briefingAttendance.xhtml";

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
		/*
		 * ScQtBriefing briefing = new ScQtBriefing();
		 * 
		 * briefing.setPmAddress(new PmAddress());
		 * briefing.getPmAddress().setPmCountry(new PmCountry());
		 * briefing.getPmAddress().setPmState(new PmState());
		 * briefing.getPmAddress().setPmDistrict(new PmDistrict());
		 * briefing.getPmAddress().setPmCity(new PmCity()); //
		 * briefing.setScQtZoneCriteria(new ScQtZoneCriteria());
		 * this.getManagedBean().setQtBriefing(briefing);
		 * 
		 * ScQtBriefing siteBriefing = new ScQtBriefing();
		 * siteBriefing.setPmAddress(new PmAddress());
		 * siteBriefing.getPmAddress().setPmCountry(new PmCountry());
		 * siteBriefing.getPmAddress().setPmState(new PmState());
		 * siteBriefing.getPmAddress().setPmDistrict(new PmDistrict());
		 * siteBriefing.getPmAddress().setPmCity(new PmCity()); //
		 * siteBriefing.setScQtZoneCriteria(new ScQtZoneCriteria());
		 * this.getManagedBean().setQtSiteBriefing(siteBriefing);
		 */

		/*
		 * List<TreeNode> list = getManagedBean().getZoneTrees(); for (TreeNode
		 * treeNode : list) { System.out.println("Tree Node : " +
		 * treeNode.getData()); if (treeNode.getChildCount() != 0) {
		 * treeNode.getChildren().clear(); } }
		 * this.getManagedBean().setTreeNodesList(list);
		 */

		// getManagedBean().getQt().setZonal(false);

		// getManagedBean().getQtCriteria().setMultiLocality(false);

		// Briefing Zone Bean
		/*
		 * List<BriefingZoneBean> briefingZoneBeans = getManagedBean()
		 * .getBriefingZoneBean();
		 * 
		 * if (null == briefingZoneBeans) { briefingZoneBeans = new
		 * ArrayList<BriefingZoneBean>(); }
		 */

		/*
		 * List<ScQtBriefing> test1 = getManagedBean().getTest(); if(null ==
		 * test1) { test1 = new ArrayList<ScQtBriefing>(); } test1.add(new
		 * ScQtBriefing()); System.out.println("Size : " + test1.size());
		 * getManagedBean().setTest(test1);
		 */

		/*
		 * if (this.getManagedBean().getQt() == null) {
		 * this.getManagedBean().setQt(new ScQt()); }
		 */

		if (this.getManagedBean().getScQtSampleSubmission().getAddress() == null) {
			this.getManagedBean().getScQtSampleSubmission()
					.setAddress(new PmAddress());
		}

		if (this.getManagedBean().getScQtSampleSubmission().getAddress()
				.getPmState() == null) {
			this.getManagedBean().getScQtSampleSubmission().getAddress()
					.setPmState(new PmState());
		}

		if (this.getManagedBean().getScQtSampleSubmission().getAddress()
				.getPmDistrict() == null) {
			this.getManagedBean().getScQtSampleSubmission().getAddress()
					.setPmDistrict(new PmDistrict());
		}

		if (this.getManagedBean().getScQtSampleSubmission().getAddress()
				.getPmCity() == null) {
			this.getManagedBean().getScQtSampleSubmission().getAddress()
					.setPmCity(new PmCity());
		}

		retrievePmStateList();
		getDeskOfficerAdminAssistant();
	}

	/**
	 * 
	 */
	public void retrievePmStateList() {
		debugLogger("retrievePmStateList");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(1l);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		List<PmState> list = resDTO.getStateList();
		System.out.println("list: " + list.size());
		for (PmState pmState : list) {
			System.out.println(pmState.getStateId() + "|"
					+ pmState.getStateCode() + "|" + pmState.getStateName());
		}
		this.getManagedBean().setStates(list);
		System.out.println("State Id : "
				+ this.getManagedBean().getStates().get(0).getStateId());
	}

	public void retrievePmDistrictList(AjaxBehaviorEvent event) {
		debugLogger("retrievePmDistrictList");
		Long stateId = (Long) event.getComponent().getAttributes().get("value");
		// PmState state = (PmState) event.getBehavior();
		System.out.println("State Id : " + stateId);
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
		System.out.println("District Id : " + districtId);
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

	/*
	 * public void addZoneContactPerson(String briefingZoneName) {
	 * debugLogger("addZoneContactPerson");
	 * getManagedBean().setBriefingZoneName(briefingZoneName); ScQtContactPerson
	 * contactperson = new ScQtContactPerson(); List<BriefingZoneBean> beans =
	 * getManagedBean().getBriefingZoneBean(); for (BriefingZoneBean
	 * briefingZoneBean : beans) { if
	 * (briefingZoneBean.getZoneName().equalsIgnoreCase(
	 * this.getManagedBean().getBriefingZoneName())) { List<ScQtContactPerson>
	 * contactList = briefingZoneBean .getContactPersonsList(); if (null ==
	 * contactList) { contactList = new ArrayList<ScQtContactPerson>(); } for
	 * (ScQtContactPerson scQtContactPerson : contactList) {
	 * scQtContactPerson.setBrandNew(false); } System.out.println("list size : "
	 * + contactList.size()); contactList.add(contactperson);
	 * briefingZoneBean.setContactPersonsList(contactList); } } }
	 */

	public void addContactPerson() {
		debugLogger("addContactPerson");
		ScQtContactPerson contactperson = new ScQtContactPerson();
		try {
			List<ScQtContactPerson> contactList = this.getManagedBean()
					.getScQtContactPersons();
			if (null == contactList) {
				contactList = new ArrayList<ScQtContactPerson>();
			}
			for (ScQtContactPerson scQtContactPerson : contactList) {
				scQtContactPerson.setOfficerType("C");
				scQtContactPerson.setScQt(getManagedBean().getQt());
				scQtContactPerson.setBrandNew(false);
			}
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
		contactPerson.setUser(new PmUser());
		contactPerson.setOfficerType("A");
		contactPerson.setScQt(getManagedBean().getQt());
		contactPerson.setBrandNew(false);
		// getManagedBean().setQtContactPerson(contactPerson);
		/*
		 * PmUser pmUser = new PmUser(); try { List<PmUser> usersList =
		 * this.getManagedBean().getPmUsersList(); if (null == usersList) {
		 * usersList = new ArrayList<PmUser>(); } for (PmUser user : usersList)
		 * { user.setBrandNew(false); usersList.add(pmUser);
		 * this.getManagedBean().setPmUsersList(usersList); }
		 */

		try {
			if (this.getManagedBean().getPmuserContactpersonList() == null) {
				this.getManagedBean().setPmuserContactpersonList(
						new ArrayList());
			}
			this.getManagedBean().getPmuserContactpersonList()
					.add(contactPerson);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteDeskOfficerAdminAssistant() {
		debugLogger("deleteDeskOfficerAdminAssistant");
		List<PmUser> usersList = this.getManagedBean().getPmUsersList();
		List<PmUser> selectedUsersList = this.getManagedBean()
				.getSelectedPmUsersList();
		if (usersList.size() == selectedUsersList.size()) {
			usersList.clear();
		} else {
			for (PmUser pmUser : selectedUsersList) {
				usersList.remove(pmUser);
			}
		}
	}

	public void applySampleSubmission() {
		debugLogger("applySampleSubmission");
		List<ScQtSampleSubmission> sampleSubmissionsList = getManagedBean()
				.getScQtSampleSubmissionList();
		log.info("List : " + sampleSubmissionsList);
	}

	public void okSubmission() {
		debugLogger("okSubmission");
		List<ScQtSampleSubmission> list = getManagedBean()
				.getScQtSampleSubmissionList();
		ScQtSampleSubmission selectedSubmission = getManagedBean()
				.getScQtSampleSubmission();
		if (null == list) {
			list = new ArrayList<ScQtSampleSubmission>();
		}

		for (ScQtSampleSubmission scQtSampleSubmission : list) {
			scQtSampleSubmission.setAddress(selectedSubmission.getAddress());
			int index = list.indexOf(scQtSampleSubmission);
			list.set(index, scQtSampleSubmission);
		}
		// if (null != selectedSubmission) {
		// list.add(selectedSubmission);
		// }
		this.getManagedBean().setScQtSampleSubmissionList(list);
	}

}
