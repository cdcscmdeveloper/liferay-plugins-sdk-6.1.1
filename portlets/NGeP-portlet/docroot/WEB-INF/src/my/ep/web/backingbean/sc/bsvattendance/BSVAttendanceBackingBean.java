package my.ep.web.backingbean.sc.bsvattendance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.sitevisitproposaldata.SCSiteVisitProposalDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBSVRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.bsvattendance.BSVAttendanceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import com.liferay.portal.util.PortalUtil;

/**
 * @author sri Briefing Attendance Backing Bean
 */
@ManagedBean(name = "bsvAttendanceBackingBean")
@RequestScoped
public class BSVAttendanceBackingBean extends
		SourcingBaseBackingBean<BSVAttendanceManagedBean> {

	private static final String NAV_TO_BSV_REG_PAGE = "bsvAttendanceRegisterPopup.xhtml";

	public static Logger log = Logger.getLogger(BSVAttendanceBackingBean.class);

	public BSVAttendanceBackingBean() {
		if (getManagedBean().getScQt() == null) {
			retrieveBriefingSiteVisitList(146L);
		}
	}

	/**
	 * retrieving Briefing SiteVisit List
	 */
	public void retrieveBriefingSiteVisitList(Long qtId) {
		log.info("** retrieveBriefingSiteVisitList **");
		IQTBriefingSO qTBriefingSO = null;

		try {
			qTBriefingSO = (IQTBriefingSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			String languageCode = new FacesScopes().getUserLocale()
					.getLanguage();

			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setLanguageCode(languageCode);

			BriefingResDTO resDTO = qTBriefingSO
					.retrieveBriefingSiteVisitDtl(reqDTO);

			// getting Briefing Same As Site Visit value
			ScQt scQt = resDTO.getScQt();
			getManagedBean().setScQt(scQt);

			this.getManagedBean().setBriefingDetailsList(
					resDTO.getBriefingList());
			this.getManagedBean().setSiteVisitDetailsList(
					resDTO.getSiteVisitList());
			this.getManagedBean().setScQtBsv(resDTO.getScQtBsv());
			getManagedBean().setRemarks(
					getManagedBean().getScQtBsv().getBsvRemarks());
			if (null == getManagedBean().getRemarks()
					|| getManagedBean().getRemarks().isEmpty())
				initializeRemarks();

			ScQtBsvRegistration scQtBriefingRegistration = new ScQtBsvRegistration();
			scQtBriefingRegistration.setSupplier(new SmSupplier());
			// scQtBriefingRegistration.setSmMofAccount(new SmMofAccount());
			getManagedBean().setScQtBriefingRegistration(
					scQtBriefingRegistration);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		} finally {
			qTBriefingSO = null;
		}

	}

	/**
	 * To add the attandance user click add button
	 */
	public void addAttendanceForBSV() {
		List<ScQtBsvAttendance> briefingAttendances = this.getManagedBean()
				.getBriefingAttendanceList();
		ScQtBsvAttendance briefingAttendance = new ScQtBsvAttendance();
		briefingAttendance.setBsvRegistration(this.getManagedBean()
				.getSelectedbriefRegistration());

	}

	/**
	 * To show registration details when user clicks register button
	 */
	public String registerSupplier(ScQtBsvDtl briefing) {
		log.info("** attendanceRegistration **");
		this.getManagedBean().setPreviousPage("bsvAttendanceRegisterMain");

		IQTBriefingSO qTBriefingSO = null;

		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);

			if (briefing != null) {

				BriefingReqDTO reqDTO = new BriefingReqDTO();

				reqDTO.setQtBriefingId(briefing.getQtBsvDtlId());

				BriefingResDTO resDTO = qTBriefingSO
						.retrieveBriefingRegistrationList(reqDTO);

				List<ScQtBsvRegistration> briefRegList = resDTO
						.getBriefingRegResultBean().getResultList();

				List<ScQtBsvRegistration> registrationList = new ArrayList<ScQtBsvRegistration>();
				for (ScQtBsvRegistration scqtbriefReg : briefRegList) {
					// scqtbriefReg.setSmMofAccount(new SmMofAccount());
					registrationList.add(scqtbriefReg);
				}
				this.getManagedBean().setBriefingRegistrationList(
						registrationList);

				retriveSupplierList();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qTBriefingSO = null;
		}
		return NAV_TO_BSV_REG_PAGE + "?faces-redirect=true";
	}

	/**
	 * Adding new row for Company List in Briefing Attendance Registration
	 */
	public void addSupplierDetails() {
		log.info("** add Company **");
		try {
			ScQtBsvDtl scQtBriefing = getManagedBean().getScQtBriefing();
			ScQtBsvRegistration briefReg = new ScQtBsvRegistration();
			SmSupplier smSupplier = new SmSupplier();
			SmMofAccount account = new SmMofAccount();
			List<SmMofAccount> accounts = new ArrayList<SmMofAccount>();
			accounts.add(account);
			smSupplier.setSmMofAccounts(accounts);
			briefReg.setSupplier(smSupplier);
			briefReg.setBsvDtl(scQtBriefing);

			List<ScQtBsvRegistration> attendaceforComp = getManagedBean()
					.getBriefingRegistrationList();

			if (null == attendaceforComp) {
				attendaceforComp = new ArrayList<ScQtBsvRegistration>();
			}

			attendaceforComp.add(briefReg);

			this.getManagedBean().setBriefingRegistrationList(attendaceforComp);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	/**
	 * getting Approver drop down listing
	 */
	public List<PmUser> getApproverNames() {
		List<PmUser> approverUsers = this.getManagedBean().getApproverNames();

		if (approverUsers == null) {
			IUserSO userSO = null;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

				// Get Approver Users
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.APPROVER.value());

				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				approverUsers = resDTO.getUserList();
				System.out.println("Approver List: " + approverUsers.size());
				this.getManagedBean().setApproverNames(approverUsers);

			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, ex
								.toString(), ex.getMessage()));
			} finally {
				userSO = null;
			}
		}

		return approverUsers;
	}

	/**
	 * Method To call to save Briefing Site Visit Details
	 */
	public void saveBriefSiteVisit() {
		IQTBriefingSO qTBriefingSO = null;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			List<ScQtBsvDtl> briefingDetails = new ArrayList<ScQtBsvDtl>();
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			// Save Briefing Details Status
			List<ScQtBsvDtl> list = getManagedBean().getBriefingDetailsList();
			if (null != list && !list.isEmpty()) {
				briefingDetails.addAll(list);
			}

			// Save Site visit Details Status
			List<ScQtBsvDtl> siteVisitlist = getManagedBean()
					.getSiteVisitDetailsList();
			if (null != siteVisitlist && !siteVisitlist.isEmpty()) {
				briefingDetails.addAll(list);
			}

			for (ScQtBsvDtl scQtBriefing : briefingDetails) {
				scQtBriefing.setQtBsvRegistrations(null);
			}
			BriefingResDTO resDTO = null;
			if (briefingDetails.size() > 0) {

				reqDTO.setQtBriefingList(briefingDetails);
				resDTO = qTBriefingSO.saveQTDtl(reqDTO);
			}
			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_700004,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}

		} finally {
			qTBriefingSO = null;
		}

	}

	/**
	 * Method To call to save Briefing Registration details
	 */
	public void saveBriefingAttendance() {
		IQTBriefingSO qTBriefingSO=null;
		try {
			 qTBriefingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			List<ScQtBsvDtl> briefingDetails = new ArrayList<ScQtBsvDtl>();

			ScQtBsvDtl scQtBrief = getManagedBean().getScQtBriefing();
			ScQtBsvRegistration qtBriefingRegistration = this.getManagedBean()
					.getSelectedbriefRegistration();
			List<ScQtBsvRegistration> registrationList = new ArrayList<ScQtBsvRegistration>();
			registrationList.add(qtBriefingRegistration);
			scQtBrief.setQtBsvRegistrations(registrationList);

			reqDTO.setQtBriefingList(briefingDetails);

			BriefingResDTO resDTO = qTBriefingSO.saveQTDtl(reqDTO);

			// ScQt scQt = getManagedBean().getScQt();

			// renewSesionObject();

			// retrieveBriefingSiteVisitList(scQt.getQtId());
			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_700004,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		} finally {
			qTBriefingSO = null;
		}

	}

	/**
	 * Method To call to save Briefing Registration by company
	 */
	public void saveBriefingAttendanceByCompany() {
		IQTBriefingSO qTBriefingSO;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			List<ScQtBsvDtl> briefingDetails = new ArrayList<ScQtBsvDtl>();
			List<ScQtBsvDtl> selectedBriefingDetails = getManagedBean()
					.getSelectedBriefingDetails();
			List<ScQtBsvDtl> selectedSiteVisitDetails = getManagedBean()
					.getSelectedSiteVisitDetails();

			if (selectedBriefingDetails != null) {
				briefingDetails.addAll(selectedBriefingDetails);
			}

			if (selectedSiteVisitDetails != null) {
				briefingDetails.addAll(selectedSiteVisitDetails);
			}

			if (briefingDetails.size() > 0) {
				ScQtBsvRegistration briefingRegistration = getManagedBean()
						.getScQtBriefingRegistration();
				List<ScQtBsvAttendance> briefingAttendanceList = getManagedBean()
						.getBriefingAttendanceList();
				List<ScQtBsvRegistration> scQtBriefingRegistrations = null;
				List<ScQtBsvAttendance> scQtBriefingAttendances = null;

				for (ScQtBsvDtl scQtBriefing : briefingDetails) {
					ScQtBsvRegistration scQtBriefingRegistration = (ScQtBsvRegistration) BeanUtils
							.cloneBean(briefingRegistration);
					scQtBriefingRegistration.setBsvDtl(scQtBriefing);

					scQtBriefingRegistrations = new ArrayList<ScQtBsvRegistration>();
					scQtBriefingRegistrations.add(scQtBriefingRegistration);
					scQtBriefing
							.setQtBsvRegistrations(scQtBriefingRegistrations);

					scQtBriefingAttendances = new ArrayList<ScQtBsvAttendance>();
					for (ScQtBsvAttendance briefingAttendance : briefingAttendanceList) {
						ScQtBsvAttendance scQtBriefingAttendance = (ScQtBsvAttendance) BeanUtils
								.cloneBean(briefingAttendance);

						scQtBriefingAttendance
								.setBsvRegistration(scQtBriefingRegistration);
						scQtBriefingAttendances.add(scQtBriefingAttendance);
					}
					scQtBriefingRegistration
							.setBsvAttendance(scQtBriefingAttendances);
				}

				reqDTO.setQtBriefingList(briefingDetails);

				qTBriefingSO.saveQTDtl(reqDTO);

				ScQt scQt = getManagedBean().getScQt();

				renewSesionObject();

				retrieveBriefingSiteVisitList(scQt.getQtId());

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.",
								"Briefing Registration. "));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			qTBriefingSO = null;
		}

	}

	private void retriveSupplierList() {
		System.out.println("retriveSupplierList");
		// drop down listing for mof nor and sm supplier
		List<SmMofAccount> mofNors = new ArrayList<SmMofAccount>();
		SmMofAccount SmMofAccount1 = new SmMofAccount();
		SmMofAccount1.setMofNo("MOF000002");
		SmMofAccount1.setMofAccountId(2);
		mofNors.add(SmMofAccount1);

		List<SmSupplier> smSuppliers = new ArrayList<SmSupplier>();
		SmSupplier SmSupplier = new SmSupplier();
		SmSupplier.setSupplierId(27l);
		// SmSupplier.setSupplierName("Redah Jaya Sdn Bhd");
		List<SmMofAccount> smMofAccounts1 = new ArrayList<SmMofAccount>();
		smMofAccounts1.add(SmMofAccount1);
		SmMofAccount1.setSmSupplier(SmSupplier);
		SmSupplier.setSmMofAccounts(smMofAccounts1);
		smSuppliers.add(SmSupplier);

		SmMofAccount SmMofAccount2 = new SmMofAccount();
		SmMofAccount2.setMofNo("MOF000004");
		SmMofAccount2.setMofAccountId(4);
		mofNors.add(SmMofAccount2);
		this.getManagedBean().setSmMofAccounts(mofNors);

		SmSupplier SmSupplier1 = new SmSupplier();
		SmSupplier1.setSupplierId(99l);
		// SmSupplier1.setSupplierName("MDEC malaysia");
		smSuppliers.add(SmSupplier1);
		List<SmMofAccount> smMofAccounts2 = new ArrayList<SmMofAccount>();
		SmMofAccount2.setSmSupplier(SmSupplier1);
		smMofAccounts2.add(SmMofAccount2);
		SmSupplier.setSmMofAccounts(smMofAccounts2);
		this.getManagedBean().setSmSuppliers(smSuppliers);
	}

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);

		log.info("taskId : " + taskId);
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		// TODO
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		// String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		this.getManagedBean().setOutComes(res.getAction());
		SCSiteVisitProposalDataType siteVisitDatatype = (SCSiteVisitProposalDataType) obj;
		this.getManagedBean().setSiteVisitPayLoad(siteVisitDatatype);
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtId(new Long(siteVisitDatatype.getQtId()).longValue());
		QuationTenderResDTO tenderResDTO = detailSO
				.retriveQTDetails(tenderReqDTO);
		QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();
		detailBackingBean.getManagedBean().setQt(tenderResDTO.getQtDetail());
		populateParentOrgProfile(tenderResDTO.getQtDetail());
		retrieveBriefingSiteVisitList(siteVisitDatatype.getQtId().longValue());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "");
	}

	public String updateBpmTask(String action) {
		System.out.println("Succeesfully update the task");
		BriefingReqDTO briefingReqDTO = prepareBriefingDtls();
		briefingReqDTO.setOutcome(action);
		briefingReqDTO.setTaskId(this.getManagedBean().getTaskId());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		briefingReqDTO.setToken(ctx);
		briefingReqDTO.setPayLoad(this.getManagedBean().getSiteVisitPayLoad());
		IQTBriefingSO briefingSO = getBriefingSO();
		try {
			BriefingResDTO resDTO = briefingSO.updateBPMAction(briefingReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", " Type. : "

						));

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");

			} else {
				throw new EpException("");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to process now", ""

					));
		}
		return null;
	}

	private IQTBriefingSO getBriefingSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);

	}

	private BriefingReqDTO prepareBriefingDtls() {
		List<ScQtBsvDtl> briefingDetails = new ArrayList<ScQtBsvDtl>();

		// Save Briefing Details Status
		List<ScQtBsvDtl> list = getManagedBean().getBriefingDetailsList();
		if (list != null) {
			System.out.println("briefing list size" + list.size());
			briefingDetails.addAll(list);
		}

		// Save Site visit Details Status
		list = getManagedBean().getSiteVisitDetailsList();
		if (list != null) {
			System.out.println("site visit list size" + list.size());
			briefingDetails.addAll(list);
		}

		for (ScQtBsvDtl scQtBriefing : briefingDetails) {
			scQtBriefing.setQtBsvRegistrations(null);
		}

		BriefingReqDTO reqDTO = new BriefingReqDTO();
		reqDTO.setQtBriefingList(briefingDetails);

		return reqDTO;
	}

	public String backBtn() {
		log.info("** Back Button **");

		return this.getManagedBean().getPreviousPage()
				+ ".xhtml?faces-redirect=true";

	}

	public void onRowSelect(SelectEvent event) {
		ScQtBsvRegistration briefingRegistration = (ScQtBsvRegistration) event
				.getObject();
		this.getManagedBean()
				.setSelectedbriefRegistration(briefingRegistration);
		/*
		 * // TODO Need to add a call to SM to get the authorised persons list.
		 * List<SmPersonnel> authorisedPersons = null;
		 *//**
		 * Below code to retrieve the attandancelist if already save..
		 */
		/*
		 * List<ScQtBriefingAttendance> attanedList = null; if (null !=
		 * briefingRegistration && null !=
		 * briefingRegistration.getQtBriefingRegistrationId()) { IQTBriefingSO
		 * briefingSO = BusinessDelegate.getInstance()
		 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
		 * IQTBriefingSO.class); BriefingReqDTO briefingReqDTO = new
		 * BriefingReqDTO(); briefingReqDTO.setQtBriefingId(briefingRegistration
		 * .getQtBriefingRegistrationId()); BriefingResDTO resDto = briefingSO
		 * .retrieveBriefingAttendanceList(briefingReqDTO); attanedList =
		 * resDto.getBriefingAttResultBean().getResultList(); }
		 *//**
		 * Below code to add the smpersonal to attandance list if alredy
		 * attanded persons mark it.
		 */
		/*
		 * List<ScQtBriefingAttendance> attandanceList = new
		 * ArrayList<ScQtBriefingAttendance>();
		 * 
		 * for (SmPersonnel personnel : authorisedPersons) {
		 * ScQtBriefingAttendance briefingAttendance = new
		 * ScQtBriefingAttendance();
		 * briefingAttendance.setName(personnel.getName());
		 * briefingAttendance.setIcPassport(personnel.getIdentificationNo());
		 * briefingAttendance.setContactNo(personnel.getMobileNo());
		 * briefingAttendance.setEmail(personnel.getEmail()); briefingAttendance
		 * .setScQtBriefingRegistration(briefingRegistration);
		 * briefingAttendance.setAttended(isAttandedForBSV(briefingAttendance,
		 * attanedList)); attandanceList.add(briefingAttendance); }
		 */

		this.getManagedBean().setBriefingAttendanceList(
				briefingRegistration.getBsvAttendance());
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
					.getCompanyName()
					.equalsIgnoreCase(
							this.getManagedBean()
									.getSelectedbriefRegistration()
									.getSupplier().getCompanyName())) {
				registrations.remove(registration);
				break;
			}
		}
	}

	public void showLocationDtls(ScQtBsvDtl briefing) {
		this.getManagedBean().setLocation(briefing.getLocation());
		this.getManagedBean().setAddress(briefing.getAddress().getAddress1());
	}

	/**
	 * Method to check attanded or not
	 * 
	 * @param attandance
	 * @param attandedList
	 * @return
	 */
	private boolean isAttandedForBSV(ScQtBsvAttendance attandance,
			List<ScQtBsvAttendance> attandedList) {
		boolean flag = false;
		if (null != attandedList && !attandedList.isEmpty()) {
			for (ScQtBsvAttendance scQtBriefingAttendance : attandedList) {
				if (scQtBriefingAttendance.getIcPassPort().equalsIgnoreCase(
						attandance.getIcPassPort())) {
					flag = true;
					break;
				}

			}
		}
		return flag;
	}

	private BriefingResDTO retrieveBriefingSiteVisitDtlsByZoneNQtId(Long qtId,
			Long zoneId, String bsvType) {
		IQTBriefingSO qTBriefingSO;

		qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
		BriefingReqDTO reqDTO = new BriefingReqDTO();
		reqDTO.setQtId(qtId);
		reqDTO.setSpecZoneId(zoneId);
		reqDTO.setBriefingType(bsvType);
		BriefingResDTO briefingResDTO = qTBriefingSO
				.retrieveBriefingSiteVisitDtls(reqDTO);
		return briefingResDTO;
	}

	public void zoneTabChange(TabChangeEvent evnt) {
		ScQt scQt = getManagedBean().getScQt();
		TplSpecZoneDtl specZoneDtl = (TplSpecZoneDtl) evnt.getData();
		getManagedBean().setSelectedZone(specZoneDtl);

		Long zoneId = specZoneDtl.getSpecZoneId();

		IQTBriefingSO qTBriefingSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTBriefingSO.class);
		BriefingReqDTO reqDTO = new BriefingReqDTO();
		reqDTO.setQtId(scQt.getQtId());
		reqDTO.setSpecZoneId(zoneId);
		BriefingResDTO briefingResDTO = qTBriefingSO
				.retrieveBriefingSiteVisitDtls(reqDTO);
		this.getManagedBean().setScQtBsv(briefingResDTO.getScQtBsv());
		this.getManagedBean().setBriefingDetailsList(
				briefingResDTO.getBriefingList());
		this.getManagedBean().setSiteVisitDetailsList(
				briefingResDTO.getSiteVisitList());
		this.getManagedBean().setRemarks(
				briefingResDTO.getScQtBsv().getBsvRemarks());

		initializeRemarks();
	}

	public void initializeRemarks() {
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		ScQtBSVRemark remark = new ScQtBSVRemark();

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

		}
		if (null == getManagedBean().getRemarks()) {
			getManagedBean().setRemarks(new ArrayList<ScQtBSVRemark>());
		}
		getManagedBean().getRemarks().add(remark);
	}

}
