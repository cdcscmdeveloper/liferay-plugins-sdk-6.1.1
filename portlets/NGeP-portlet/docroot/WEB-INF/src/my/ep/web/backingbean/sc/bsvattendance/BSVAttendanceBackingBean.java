package my.ep.web.backingbean.sc.bsvattendance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.sitevisitproposaldata.SCSiteVisitProposalDataType;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBSVRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.managedbean.sc.bsvattendance.BSVAttendanceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

import com.liferay.portal.util.PortalUtil;

/**
 * @author sri
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
			if (scQt.isZonal()) {
				List<TplSpecZoneDtl> zoneDtls = resDTO.getZonesList();
				// Need to construct tab bean for zone
				List<BriefingZoneBean> bsvZoneBeans = new ArrayList<BriefingZoneBean>();

				if (null != zoneDtls) {
					for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
						reqDTO.setSpecZoneId(specZoneDtl.getSpecZoneId());
						BriefingResDTO briefingResDTO = qTBriefingSO
								.retrieveBriefingSiteVisitDtls(reqDTO);
						initializeRemarks(briefingResDTO.getScQtBsv());
						BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
								specZoneDtl, briefingResDTO.getBriefingList(),
								briefingResDTO.getSiteVisitList(),
								briefingResDTO.getScQtBsv());
						bsvZoneBeans.add(briefingZoneBean);
					}
				}

				this.getManagedBean().setZoneBean(bsvZoneBeans);
			} else {
				ScQtBsv scQtBsv = resDTO.getScQtBsv();
				initializeRemarks(scQtBsv);
				BriefingZoneBean briefingZoneBean = new BriefingZoneBean(null,
						resDTO.getBriefingList(), resDTO.getSiteVisitList(),
						scQtBsv);
				this.getManagedBean().setNormalBsvBean(briefingZoneBean);

			}
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

				this.getManagedBean().setScQtBriefing(briefing);
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

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qTBriefingSO = null;
		}
		return NAV_TO_BSV_REG_PAGE + "?faces-redirect=true";
	}

	/**
	 * Method To call to save Briefing Site Visit Details
	 */
	public void submitBsvDetails() {
		List<ScQtBsv> bsvs = new ArrayList<ScQtBsv>();
		if (this.getManagedBean().getScQt().isZonal()) {
			List<BriefingZoneBean> zoneBean = this.getManagedBean()
					.getZoneBean();
			for (BriefingZoneBean briefingZoneBean : zoneBean) {
				if (!isAllBsvsComplete(briefingZoneBean)) {
					throw new EpException("");
				}
			}

			for (BriefingZoneBean briefingZoneBean : zoneBean) {

				ScQtBsv scQtBsv = briefingZoneBean.getScQtBsv();
				scQtBsv.setTplSpecZoneDtl(briefingZoneBean.getTplSpecZoneDtl());
				if (scQtBsv.isSameBsv()) {
					scQtBsv.setScQtBsvDtls(briefingZoneBean.getBriefingList());
				} else {
					briefingZoneBean.getBriefingList().addAll(
							briefingZoneBean.getSitevisitList());
					scQtBsv.setScQtBsvDtls(briefingZoneBean.getBriefingList());
				}
				removeEmptyRemarks(scQtBsv);
				bsvs.add(scQtBsv);
			}
		} else {
			BriefingZoneBean normalBsv = this.getManagedBean()
					.getNormalBsvBean();
			boolean flag = isAllBsvsComplete(normalBsv);
			if (!flag) {
				throw new EpException("");
			}
			ScQtBsv scQtBsv = normalBsv.getScQtBsv();
			scQtBsv.setTplSpecZoneDtl(normalBsv.getTplSpecZoneDtl());
			if (scQtBsv.isSameBsv()) {
				scQtBsv.setScQtBsvDtls(normalBsv.getBriefingList());
			} else {
				normalBsv.getBriefingList()
						.addAll(normalBsv.getSitevisitList());
				scQtBsv.setScQtBsvDtls(normalBsv.getBriefingList());
			}
			removeEmptyRemarks(scQtBsv);
			bsvs.add(scQtBsv);
		}
		// Code to check the whether need to submit or not..
	}

	/**
	 * Method To call to save Briefing Registration and attandance details
	 */
	public String saveBriefingRegAttendance(String status) {
		IQTBriefingSO qTBriefingSO = null;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			List<ScQtBsvDtl> briefingDetails = new ArrayList<ScQtBsvDtl>();
			ScQtBsvDtl scQtBrief = getManagedBean().getScQtBriefing();
			for (ScQtBsvRegistration registration : this.getManagedBean()
					.getBriefingRegistrationList()) {				
				List<ScQtBsvAttendance> bsvAttandance = registration
						.getBsvAttendance();
			boolean flag =	isOneAuthorizedPersonal(bsvAttandance);
			
					if (!flag) {
						throw new EpException(MessageConst.ERROR_760043,
								new String[] { "1" });
					}
				
			}
			scQtBrief.setQtBsvRegistrations(this.getManagedBean()
					.getBriefingRegistrationList());
			if (null != status) {
				PmStatus bsvStatus = new PmStatus();
				bsvStatus.setStatusId(QTStatusConst.BSV_COMPLETE);
				scQtBrief.setBsvStatus(bsvStatus);
			}
			briefingDetails.add(scQtBrief);
			reqDTO.setQtBriefingList(briefingDetails);

			BriefingResDTO resDTO = qTBriefingSO.saveQTDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				ScQtBsvDtl bsv = resDTO.getBriefingList().get(0);
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700004,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_QUOTATION),
																bsv.getQtBsvDtlId()
																		.toString() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

			}
		} finally {
			qTBriefingSO = null;
		}

		retrieveBriefingSiteVisitList(146l);
		return this.getManagedBean().getPreviousPage()
				+ ".xhtml?faces-redirect=true";

	}

	/**
	 * @param bsvAttandance
	 */
	private boolean isOneAuthorizedPersonal(List<ScQtBsvAttendance> bsvAttandance) {
		
		for(ScQtBsvAttendance attendance : bsvAttandance){
			if(attendance.isAuthorized() && attendance.isAttended()){
				return true;
			}
		}
		return false;
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
		BriefingReqDTO briefingReqDTO = null;
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

	public String backBtn() {
		log.info("** Back Button **");

		return this.getManagedBean().getPreviousPage()
				+ ".xhtml?faces-redirect=true";

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

	public void showLocationDtls(ScQtBsvDtl briefing) {
		this.getManagedBean().setLocation(briefing.getLocation());
		this.getManagedBean().setAddress(briefing.getAddress().getAddress1());
	}


	public void initializeRemarks(ScQtBsv bsv) {

		List<ScQtBSVRemark> bsvRemarks = new ArrayList<ScQtBSVRemark>();
		if (bsv.getBsvRemarks() == null || bsv.getBsvRemarks().isEmpty()) {
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
			bsvRemarks.add(remark);
			bsv.setBsvRemarks(bsvRemarks);
		}
	}

	public void bsvRegistationListner(List<SmMofAccount> mofAccounts) {
		log.debug("businessNatureListener");
		List<ScQtBsvRegistration> bsvRegistrations = new ArrayList<ScQtBsvRegistration>();
		if (mofAccounts != null) {
			for (SmMofAccount mofAccount : mofAccounts) {
				ScQtBsvRegistration bsvRegistration = new ScQtBsvRegistration();
				bsvRegistration.setSupplier(mofAccount.getSmSupplier());
				bsvRegistration.setBsvDtl(this.getManagedBean()
						.getScQtBriefing());
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

	private void removeEmptyRemarks(ScQtBsv bsv) {
		List<ScQtBSVRemark> bsvRemarks = bsv.getBsvRemarks();
		for (int i = 0; i < bsvRemarks.size(); i++) {
			ScQtBSVRemark bsvRemark = bsvRemarks.get(i);
			if (null == bsvRemark.getRemark()
					|| "".equals(bsvRemark.getRemark())) {
				bsvRemarks.remove(i);
			}
		}
		bsv.setBsvRemarks(bsvRemarks);
	}

	public boolean isAllBsvsComplete(BriefingZoneBean zoneBean) {
		ScQtBsv bsv = zoneBean.getScQtBsv();
		if (bsv.isSameBsv()) {
			List<ScQtBsvDtl> bsvDtls = zoneBean.getBriefingList();
			return isBsvStatusComplete(bsvDtls);
		} else {
			zoneBean.getBriefingList().addAll(zoneBean.getSitevisitList());
			return isBsvStatusComplete(zoneBean.getBriefingList());
		}
	}

	private boolean isBsvStatusComplete(List<ScQtBsvDtl> bsvDetails) {
		for (ScQtBsvDtl bsvDtl : bsvDetails) {
			if (!QTStatusConst.BSV_COMPLETE.equals(bsvDtl.getBsvStatus()
					.getStatusId())) {
				return false;
			}

		}
		return true;
	}
}
