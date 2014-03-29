package my.ep.web.backingbean.sc.quatationtender;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBreifingZoneRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefing;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefingRegistration;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefingRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 * @author SudheerKumar
 * 
 */
@ManagedBean(name = "qTScheduleDetailBackingBean")
@RequestScoped
public class QTScheduleDetailBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	private static final String NAV_TO_BSV_ATTENDANCE_PAGE = "/xhtml/sc/quatationtender/briefingsitevisit/briefingAttendance.xhtml";

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
		ScQtBriefing briefing = new ScQtBriefing();

		briefing.setPmAddress(new PmAddress());
		briefing.getPmAddress().setPmCountry(new PmCountry());
		briefing.getPmAddress().setPmState(new PmState());
		briefing.getPmAddress().setPmDistrict(new PmDistrict());
		briefing.getPmAddress().setPmCity(new PmCity());
		// briefing.setScQtZoneCriteria(new ScQtZoneCriteria());
		this.getManagedBean().setQtBriefing(briefing);

		ScQtBriefing siteBriefing = new ScQtBriefing();
		siteBriefing.setPmAddress(new PmAddress());
		siteBriefing.getPmAddress().setPmCountry(new PmCountry());
		siteBriefing.getPmAddress().setPmState(new PmState());
		siteBriefing.getPmAddress().setPmDistrict(new PmDistrict());
		siteBriefing.getPmAddress().setPmCity(new PmCity());
		// siteBriefing.setScQtZoneCriteria(new ScQtZoneCriteria());
		this.getManagedBean().setQtSiteBriefing(siteBriefing);

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
		List<BriefingZoneBean> briefingZoneBeans = getManagedBean()
				.getBriefingZoneBean();

		if (null == briefingZoneBeans) {
			briefingZoneBeans = new ArrayList<BriefingZoneBean>();
		}

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

		getDeskOfficerAdminAssistant();
	}

	private void getDeskOfficerAdminAssistant() {
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(RoleCode.DESK_ASSISTANT.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		List<PmUser> deskOfficer = userResDTO.getUserList();
		getManagedBean().setDeskOfficerAdmin(deskOfficer);

	}

	public void bsvZoneTabChange(TabChangeEvent evnt) {
		debugLogger("bsvZoneTabChange");
		ScQt scQt = getManagedBean().getQt();

		BriefingZoneBean briefingZoneBean = (BriefingZoneBean) evnt.getData();

		TplSpecZoneDtl tplSpecZoneDtl = briefingZoneBean.getTplSpecZoneDtl();
		getManagedBean().setSelectedZone(tplSpecZoneDtl);
		List<ScQtBriefing> briefings=null;
		List<ScQtBriefing> siteVisit=null;
		if (scQt.isSameBsv()) {
			briefings = retrieveBriefingSiteVisitDtlsByZoneNQtId(
					scQt.getQtId(), tplSpecZoneDtl.getSpecZoneId(),
					scQt.getAttendenceType());
			this.getManagedBean().setBriefings(briefings);
		} else {
			 briefings = retrieveBriefingSiteVisitDtlsByZoneNQtId(
					scQt.getQtId(), tplSpecZoneDtl.getSpecZoneId(), "B");
			this.getManagedBean().setBriefings(briefings);
			 siteVisit = retrieveBriefingSiteVisitDtlsByZoneNQtId(
					scQt.getQtId(), tplSpecZoneDtl.getSpecZoneId(), "S");

			this.getManagedBean().setSiteBriefings(siteVisit);

		}

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
				//getManagedBean().getBriefingZoneBean().remove(i);
				getManagedBean().getBriefingZoneBean().add(i,bean);
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
	private List<ScQtBriefing> retrieveBriefingSiteVisitDtlsByZoneNQtId(
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
		getManagedBean().setBriefingZoneName(briefingZoneName);
	}

	public void addZoneSiteDetails(String briefingZoneName) {
		debugLogger("addZoneSiteDetails");
		getManagedBean().setBriefingZoneName(briefingZoneName);
	}

	public void addToBriefingListByZone() {
		debugLogger("addToBriefingListByZone");
		ScQtBriefing selectedBirefing = this.getManagedBean().getQtBriefing();
		List<BriefingZoneBean> beans = getManagedBean().getBriefingZoneBean();
		for (BriefingZoneBean briefingZoneBean : beans) {
			if (briefingZoneBean
					.getTplSpecZoneDtl()
					.getZoneName()
					.equalsIgnoreCase(
							this.getManagedBean().getBriefingZoneName())) {
				List<ScQtBriefing> briefings = briefingZoneBean
						.getBriefingList();
				if (null == briefings) {
					briefings = new ArrayList<ScQtBriefing>();
				}
				briefings.add(selectedBirefing);
				briefingZoneBean.setBriefingList(briefings);
				break;
			}
		}
	}

	public void addToSitevisitListByZone() {
		debugLogger("addToSitevisitListByZone");
		ScQtBriefing selectedSiteBriefing = this.getManagedBean()
				.getQtSiteBriefing();
		List<BriefingZoneBean> beans = getManagedBean().getBriefingZoneBean();
		for (BriefingZoneBean briefingZoneBean : beans) {
			if (briefingZoneBean
					.getTplSpecZoneDtl()
					.getZoneName()
					.equalsIgnoreCase(
							this.getManagedBean().getBriefingZoneName())) {
				List<ScQtBriefing> sitevisit = briefingZoneBean
						.getSitevisitList();
				if (null == sitevisit) {
					sitevisit = new ArrayList<ScQtBriefing>();
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
				scQtContactPerson.setBrandNew(false);
			}
			contactList.add(contactperson);
			this.getManagedBean().setScQtContactPersons(contactList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void addBriefingDetails() {
		debugLogger("addBriefingDetails");
		List<ScQtBriefing> briefingdetails = this.getManagedBean()
				.getBriefings();
		if (briefingdetails == null) {
			briefingdetails = new ArrayList<ScQtBriefing>();
		}
		this.getManagedBean().setBriefings(briefingdetails);
	}

	public void addSiteDetails() {
		List<ScQtBriefing> sitedetails = this.getManagedBean()
				.getSiteBriefings();
		if (sitedetails == null) {
			sitedetails = new ArrayList<ScQtBriefing>();
		}
		this.getManagedBean().setSiteBriefings(sitedetails);
	}

	public void addToBriefingList() {
		List<ScQtBriefing> brefings = this.getManagedBean().getBriefings();
		ScQtBriefing selectedBrefing = this.getManagedBean().getQtBriefing();
		if (null == brefings) {
			brefings = new ArrayList<ScQtBriefing>();
		}
		if (null != selectedBrefing) {
			brefings.add(selectedBrefing);
		}
	}

	public void addToSiteBriefingList() {
		List<ScQtBriefing> siteBriefings = this.getManagedBean()
				.getSiteBriefings();
		ScQtBriefing selectedSiteBriefing = this.getManagedBean()
				.getQtSiteBriefing();
		if (null == siteBriefings) {
			siteBriefings = new ArrayList<ScQtBriefing>();
		}
		if (null != selectedSiteBriefing) {
			siteBriefings.add(selectedSiteBriefing);
		}
	}

	public void addToSiteVisitDtl() {
		List<ScQtBriefing> list = getManagedBean().getSiteBriefings();
		if (list == null) {
			list = new ArrayList<ScQtBriefing>();
		}
		System.out.println("size : "
				+ getManagedBean().getSelectedBriefingDetails());
		for (ScQtBriefing scQtBriefing : getManagedBean()
				.getSelectedBriefingDetails()) {
			list.add(scQtBriefing);
		}
		getManagedBean().setSiteBriefings(list);
		System.out.println("Breifieng Dtl : "
				+ getManagedBean().getSiteBriefings().size());
	}

	public void deleteContactPerson() {
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

	public void deleteZoneBriefingDetails() {
		debugLogger("deleteZoneBriefingDetails");
		List<ScQtBriefing> beans = getManagedBean().getBriefingZoneBean()
				.get(0).getBriefingList();
		List<ScQtBriefing> selectList = getManagedBean()
				.getSelectedBriefingDetails();
		if (beans.size() == selectList.size()) {
			beans.clear();
		} else {
			for (ScQtBriefing scQtBriefing : selectList) {
				beans.remove(scQtBriefing);
			}
		}
	}

	public void deleteBriefingDetails() {
		debugLogger("deleteBriefingDetails");
		List<ScQtBriefing> beans = getManagedBean().getBriefings();
		List<ScQtBriefing> selectList = getManagedBean()
				.getSelectedBriefingDetails();
		if (beans.size() == selectList.size()) {
			beans.clear();
		} else {
			for (ScQtBriefing scQtBriefing : selectList) {
				beans.remove(scQtBriefing);
			}
		}
	}

	public String editAction(ScQtBriefing scQtBriefing) {
		debugLogger("editAction");
		System.out.println("BriefingDesc : " + scQtBriefing.getBriefingDesc());
		IQTBriefingSO qTBriefingSO = null;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			System.out.println("ScQtBriefingId : "
					+ scQtBriefing.getQtBriefingId());
			if (scQtBriefing != null) {
				BriefingReqDTO reqDTO = new BriefingReqDTO();

				reqDTO.setQtBriefingId(scQtBriefing.getQtBriefingId());

				BriefingResDTO resDTO = qTBriefingSO
						.retrieveBriefingRegistrationList(reqDTO);

				List<ScQtBriefingRegistration> briefRegList = resDTO
						.getBriefingRegResultBean().getResultList();

				List<ScQtBriefingRegistration> registrationList = new ArrayList<ScQtBriefingRegistration>();
				for (ScQtBriefingRegistration scqtbriefReg : briefRegList) {
					scqtbriefReg.setSmMofAccount(new SmMofAccount());
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
			ScQtBriefing scQtBriefing = getManagedBean().getQtBriefing();
			ScQtBriefingRegistration briefReg = new ScQtBriefingRegistration();
			SmSupplier smSupplier = new SmSupplier();
			SmMofAccount account = new SmMofAccount();
			List<SmMofAccount> accounts = new ArrayList<SmMofAccount>();
			accounts.add(account);
			smSupplier.setSmMofAccounts(accounts);
			briefReg.setSmSupplier(smSupplier);
			briefReg.setScQtBriefing(scQtBriefing);

			List<ScQtBriefingRegistration> attendaceforComp = getManagedBean()
					.getBriefingRegistrationList();

			if (null == attendaceforComp) {
				attendaceforComp = new ArrayList<ScQtBriefingRegistration>();
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

	public void handleDeskOfficerChanged(PmUser user) {
		List<PmUser> list = getManagedBean().getDeskOfficerAdmin();
		for (PmUser pmUser : list) {
			if (pmUser.getUserId() == user.getUserId()) {
				getManagedBean().setPmUser(pmUser);
			}
		}
	}

	public void addDeskOfficerAdminAssistant() {
		debugLogger("addDeskOfficerAdminAssistant");
		PmUser pmUser = new PmUser();
		try {
			List<PmUser> usersList = this.getManagedBean().getPmUsersList();
			if (null == usersList) {
				usersList = new ArrayList<PmUser>();
			}
			for (PmUser user : usersList) {
				user.setBrandNew(false);
			}
			usersList.add(pmUser);
			this.getManagedBean().setPmUsersList(usersList);
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

}
