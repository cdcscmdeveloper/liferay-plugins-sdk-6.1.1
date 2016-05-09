/* Copyright © 2014 Archisoft. All rights reserved. */
package my.ep.web.backingbean.sc.bsvattendance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
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
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.bsvattendance.BSVAttendanceManagedBean;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.quartz.SchedulerException;

/**
 * BSV Attendance BackingBean class is used to display Brifing, Sitevisit
 * registration & attendance details in BSV Attendance screen.
 * 
 * @author sri
 */
@ManagedBean(name = "bsvAttendanceBackingBean")
@ViewScoped
public class BSVAttendanceBackingBean extends
		SourcingBaseBackingBean<BSVAttendanceManagedBean> {

	private static final String NAV_TO_BSV_REG_PAGE = "/xhtml/sc/bsvattendance/addattendance/bsvAttendanceRegisterPopup.xhtml";
    private static final String BSV_MAIN_PAGE = "/xhtml/sc/bsvattendance/attendancedetails/bsvAttendanceRegisterMain.xhtml";
    private static final String SAVE_SUCCESS_MSG_KEY = "lbl.sc.bsv.save.message";
    
	public static Logger log = Logger.getLogger(BSVAttendanceBackingBean.class);
	private String slectedHeaderName;

	public BSVAttendanceBackingBean() {

	}

	/**
	 * retrieving Briefing SiteVisit List
	 * 
	 * @param qtId
	 *            the Long variable
	 */
	public void retrieveBriefingSiteVisitList(Long qtId) {

		log.info("** retrieveBriefingSiteVisitList **");
		IQTBriefingSO qTBriefingSO = null;

		try {
			/*
			 * if (this.getManagedBean().getNormalBsvBean() == null || null ==
			 * this.getManagedBean().getZoneBean() ||
			 * this.getManagedBean().getZoneBean().isEmpty()) {
			 */
			qTBriefingSO = (IQTBriefingSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			String languageCode = new FacesScopes().getUserLocale()
					.getLanguage();

			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setQtId(qtId);
			// reqDTO.setScQt(this.getManagedBean().getScQt());
			reqDTO.setLanguageCode(languageCode);

			BriefingResDTO resDTO = qTBriefingSO
					.retrieveBriefingSiteVisitDtl(reqDTO);

			// getting Briefing Same As Site Visit value
			ScQt scQt = resDTO.getScQt();
			this.getManagedBean().setScQt(scQt);
			if (scQt.isZonal()) {
				List<TplSpecZoneDtl> zoneDtls = resDTO.getZonesList();
				// Need to construct tab bean for zone
				List<BriefingZoneBean> bsvZoneBeans = new ArrayList<BriefingZoneBean>();

				if (null != zoneDtls) {
					for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
						reqDTO.setSpecZoneId(specZoneDtl.getSpecZoneId());
						BriefingResDTO briefingResDTO = qTBriefingSO
								.retrieveBriefingSiteVisitDtls(reqDTO);
						/*
						 * if (null != briefingResDTO.getScQtBsv() &&
						 * briefingResDTO.getScQtBsv().getQtBsvId() != null) {
						 */
						ScQtBsv scQtBsv = briefingResDTO.getScQtBsv();

						if (null != scQtBsv) {
							scQtBsv.setBsvRemarks(initializeRemarks(scQtBsv,
									this.getManagedBean().getScQt()
											.getCurrentWorkFlowStatus(),
									scQtBsv.getBsvRemarks(),
									ScQtBSVRemark.class));

						}
						BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
								specZoneDtl, briefingResDTO.getBriefingList(),
								briefingResDTO.getSiteVisitList(),
								briefingResDTO.getScQtBsv());
						bsvZoneBeans.add(briefingZoneBean);
					}
					// }
					if (bsvZoneBeans.isEmpty()) {
						throw new EpException(
								"No Bsv Details Available For this");
					}
				}

				this.getManagedBean().setZoneBean(bsvZoneBeans);
				this.getManagedBean().setNormalBsvBean(
						bsvZoneBeans.get(Integer.valueOf(this.getManagedBean().getBsvZoneindx())));
								
			} else {
				ScQtBsv scQtBsv = resDTO.getScQtBsv();
				BriefingZoneBean briefingZoneBean = null;
				if (null != resDTO.getScQtBsv()
						&& resDTO.getScQtBsv().getQtBsvId() != null) {
					scQtBsv.setBsvRemarks(initializeRemarks(scQtBsv, null,
							scQtBsv.getBsvRemarks(), ScQtBSVRemark.class));

					briefingZoneBean = new BriefingZoneBean(null,
							resDTO.getBriefingList(),
							resDTO.getSiteVisitList(), scQtBsv);
				} else {
					throw new EpException("No Bsv Details Avalible For this");
				}
				this.getManagedBean().setNormalBsvBean(briefingZoneBean);

				// }
			}
		} finally {
			qTBriefingSO = null;
		}

	}

	/**
	 * Add the attendance on click of 'Add' button.
	 *  method commented - defect fix-5975
	 * 
	 */
	/*public void addAttendanceForBSV() {

		if (null != this.getManagedBean().getSelectedbriefRegistration()) {
			List<ScQtBsvAttendance> attandances = this.getManagedBean()
					.getSelectedbriefRegistration().getBsvAttendance();
			ScQtBsvAttendance briefingAttendance = new ScQtBsvAttendance();
			briefingAttendance.setBsvRegistration(this.getManagedBean()
					.getSelectedbriefRegistration());
			briefingAttendance.setAttended(true);
			briefingAttendance.setBrandNew(false);
			briefingAttendance.setName(StringUtils.EMPTY);
			briefingAttendance.setIcPassPort(StringUtils.EMPTY);

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

	}*/

	/**
	 * Show registration details when user clicks register button
	 * 
	 * @param briefing
	 *            the instance of ScQtBsvDtl
	 * @return navigationPath the String variable.
	 */
	public String registerSupplier(ScQtBsvDtl briefing) {

		log.info("** attendanceRegistration **");
		this.getManagedBean().setPreviousPage(BSV_MAIN_PAGE);
		getregistrationDetails(briefing);

		return NAV_TO_BSV_REG_PAGE;
	}

	/**
	 * Save Briefing Site Visit Details on click of 'Save' button.
	 * 
	 * @param outCome
	 *            the String variable
	 * @return navigationPath the String variable
	 */
	public String submitBsvDetails(String outCome) {

		List<ScQtBsv> bsvs = new ArrayList<ScQtBsv>();
		Date closeDate = this.getManagedBean().getScQt().getClosingDate();
		if (DateUtils.getDaysDifference(closeDate, new Date()) > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat(
					new CommonUtils()
							.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATETIME));
			throw new EpException(MessageConst.ERROR_760209,
					new String[] { sdf.format(closeDate) });
		}
		if (this.getManagedBean().getScQt().isZonal()) {
			List<BriefingZoneBean> zoneBean = this.getManagedBean()
					.getZoneBean();
			for (BriefingZoneBean briefingZoneBean : zoneBean) {
				if (!isAllBsvsComplete(briefingZoneBean)) {
					throw new EpException(MessageConst.ERROR_760210);
				}
			}

			for (BriefingZoneBean briefingZoneBean : zoneBean) {
				List<ScQtBsvDtl> bsvDetails = new ArrayList<ScQtBsvDtl>();
				ScQtBsv scQtBsv = briefingZoneBean.getScQtBsv();
				if (null != scQtBsv && null != scQtBsv.getQtBsvId()) {
					scQtBsv.setTplSpecZoneDtl(briefingZoneBean
							.getTplSpecZoneDtl());
					if (scQtBsv.isSameBsv()) {
						bsvDetails.addAll(briefingZoneBean.getBriefingList());

					} else {
						bsvDetails.addAll(briefingZoneBean.getBriefingList());
						bsvDetails.addAll(briefingZoneBean.getSitevisitList());

					}
					scQtBsv.setScQtBsvDtls(bsvDetails);
					finalizeRemarks(scQtBsv.getBsvRemarks());
					// removeEmptyRemarks(scQtBsv);
					bsvs.add(scQtBsv);
				}
			}
		} else {
			BriefingZoneBean normalBsv = this.getManagedBean()
					.getNormalBsvBean();
			List<ScQtBsvDtl> bsvDetails = new ArrayList<ScQtBsvDtl>();
			boolean flag = isAllBsvsComplete(normalBsv);
			if (!flag) {
				throw new EpException(MessageConst.ERROR_760210);
			}
			ScQtBsv scQtBsv = normalBsv.getScQtBsv();
			if (null != scQtBsv && null != scQtBsv.getQtBsvId()) {
				scQtBsv.setTplSpecZoneDtl(normalBsv.getTplSpecZoneDtl());
				if (scQtBsv.isSameBsv()) {
					bsvDetails.addAll(normalBsv.getBriefingList());
				} else {
					bsvDetails.addAll(normalBsv.getBriefingList());
					bsvDetails.addAll(normalBsv.getSitevisitList());

				}
				scQtBsv.setScQtBsvDtls(bsvDetails);
				finalizeRemarks(scQtBsv.getBsvRemarks());
				bsvs.add(scQtBsv);
			}
		}
		// Code to check the whether need to submit or not..
		IQTBriefingSO qTBriefingSO = null;
		BriefingResDTO resDTO = null;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			FacesScopes facesScopes = new FacesScopes();
			ScQt scQt = this.getManagedBean().getScQt();
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setBsvDetails(bsvs);
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setOutcome(outCome);
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setRoleCode(getUserRoleCode());
			resDTO = qTBriefingSO.updateQtSuppliersNStatus(reqDTO);
			if (resDTO.isSuccessful()) {
				Date todayDate = DateUtils.setDefaultDateTime(DateUtils.getCurrentDateWithoutTime());
				if (!DateUtils.isDateGreater(scQt.getClosingDate(), todayDate)) {
					try {
						log.info("call simple scheduler if QT is closing today.");
						EpSchedulerHelper.scheduleSimpleQTClosingJob(scQt);
					} catch (SchedulerException e) {
						e.printStackTrace();
						log.error(e.getMessage());
					}
				}
				
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700026,
														new String[] {
																 getMessageCommon("lbl.sc.notice.board.briefing.and.sitevisit.dtls"),
																"" },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} finally {

		}
		return null;

	}

	/**
	 * Save Briefing Registration and attendance details
	 * 
	 * @param flowType
	 *            the String variable
	 * @return navigationUrl the String Variable
	 */
	public String saveBriefingRegAttendance(String flowType) {

		IQTBriefingSO qTBriefingSO = null;
		String url = null;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			String languageCode = new FacesScopes().getUserLocale()
					.getLanguage();
			reqDTO.setLanguageCode(languageCode);

			ScQtBsvDtl scQtBrief = this.getManagedBean().getScQtBriefing();

			for (ScQtBsvRegistration registration : this.getManagedBean()
					.getBriefingRegistrationList()) {

				List<ScQtBsvAttendance> bsvAttandance = registration
						.getBsvAttendance();

				if (getAttandanceCount(bsvAttandance) > scQtBrief
						.getMaxNoOfAttendance()) {
					throw new EpException(MessageConst.ERROR_760230,
							new String[] { Integer.valueOf(
									scQtBrief.getMaxNoOfAttendance())
									.toString() });
				}
				boolean flag = isOneAuthorizedPersonal(bsvAttandance);

				if (flag) {
					registration.setAttended(true);

				} else {
					throw new EpException(MessageConst.ERROR_760043,
							new String[] { "1" });
				}

			}

			if (SourcingConstants.COMPLETE.equalsIgnoreCase(flowType)) {
				PmStatus bsvStatus = new PmStatus();
				bsvStatus.setStatusId(QTStatusConst.BSV_COMPLETED);
				scQtBrief.setBsvStatus(bsvStatus);
			} else {
				PmStatus bsvStatus = new PmStatus();
				bsvStatus.setStatusId(QTStatusConst.BSV_PENDING);
				scQtBrief.setBsvStatus(bsvStatus);
			}

			// reqDTO.setQtBriefingList(briefingDetails);
			reqDTO.setBriefingDetail(scQtBrief);
			reqDTO.setBriefingReqDtls(this.getManagedBean()
					.getBriefingRegistrationList());
			reqDTO.setScQt(this.getManagedBean().getScQt());
			reqDTO.setToBeDeletedRegistations(this.getManagedBean()
					.getToBeDeletedRegistartions());
			BriefingResDTO resDTO = qTBriefingSO.saveQTDtl(reqDTO);
			if (SourcingConstants.COMPLETE.equalsIgnoreCase(flowType)) {
				String previousPage = this.getManagedBean().getPreviousPage();
				Long qtId = this.getManagedBean().getScQt().getQtId();
				String bsvZoneindx = this.getManagedBean().getBsvZoneindx();
				renewSessionWithPreviousValues();
				this.getManagedBean().setBsvZoneindx(bsvZoneindx);	
				retrieveBriefingSiteVisitList(qtId);
				url = previousPage + "?faces-redirect=true";
				return url;
			}
			if (resDTO.isSuccessful()
					&& SourcingConstants.SAVE.equalsIgnoreCase(flowType)) {
				ScQtBsvDtl bsv = resDTO.getScQtBriefing();
				String previousPage = this.getManagedBean().getPreviousPage();
				ScQt scQt = this.getManagedBean().getScQt();
				String bsvZoneindx = this.getManagedBean().getBsvZoneindx();
				renewSessionWithPreviousValues();
				// this.getManagedBean().setScQtBriefing(bsv);
				this.getManagedBean().setPreviousPage(previousPage);
				this.getManagedBean().setScQt(scQt);
				this.getManagedBean().setBsvZoneindx(bsvZoneindx);

				getregistrationDetails(bsv);
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_INFO,
										null, ResourceBundleUtils.getMessage(
												SAVE_SUCCESS_MSG_KEY, 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes().getUserLocale())));

			}

		} finally {
			qTBriefingSO = null;
		}

		return "";

	}

	private void renewSessionWithPreviousValues() {

		String taskId = getManagedBean().getTaskId();
		String[] outcomes = getManagedBean().getOutComes();
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		String taskName = getManagedBean().getTaskName();
		Object siteVisitDatatype = getManagedBean().getPayLoad();
		this.renewSesionObject();
		getManagedBean().setTaskId(taskId);
		getManagedBean().setOutComes(outcomes);
		getManagedBean().setCurrentStatusId(currentStatusId);
		getManagedBean().setTaskName(taskName);
		getManagedBean().setPayLoad(siteVisitDatatype);
	}

	/**
	 * @param bsvAttandance
	 *            the instance of ScQtBsvAttendance
	 * @return true/false
	 */
	private boolean isOneAuthorizedPersonal(
			List<ScQtBsvAttendance> bsvAttandance) {

		if (null == bsvAttandance || bsvAttandance.isEmpty()) {
			return false;
		}
		for (ScQtBsvAttendance attendance : bsvAttandance) {
			if (null != bsvAttandance) {
				if (attendance.isAuthorized() && attendance.isAttended()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Override from SourcingBaseBackingBean#preRenderView()
	 */
	public void preRenderView() {

		SCQuotationTenderDataType siteVisitDatatype = getBpmTaskDetails(SCQuotationTenderDataType.class, null);

		retrieveBriefingSiteVisitList(siteVisitDatatype.getQtId().longValue());

		FacesContext fc = FacesContext.getCurrentInstance();

		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,BSV_MAIN_PAGE);
	}

	/**
	 * Update info into BPM
	 * 
	 * @param action
	 *            the String variable
	 * @return navigateUrl if success else will throw exception by BPM
	 */
	public String updateBpmTask(String action) {

		BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
		briefingReqDTO.setOutcome(action);
		briefingReqDTO.setTaskId(this.getManagedBean().getTaskId());
		FacesScopes facesScopes = new FacesScopes();
		briefingReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		briefingReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
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

	/**
	 * Display previous screen on click of 'Back' button.
	 * 
	 * @return navigateUrl
	 */
	public String backBtn() {

		log.info("** Back Button **");
		retrieveBriefingSiteVisitList(this.getManagedBean().getScQt().getQtId());
		return this.getManagedBean().getPreviousPage()
				+ "?faces-redirect=true";

	}

	/**
	 * Method will execute on row select in data table.
	 * 
	 * @param event
	 *            the instance of SelectEvent
	 */
	public void onRowSelect(SelectEvent event) {

		this.getManagedBean().setScQtBriefingAttendance(null);
		ScQtBsvRegistration briefingRegistration = (ScQtBsvRegistration) event
				.getObject();

		getAttandanceDtlsBySelectedRegistartion(briefingRegistration);
		setSlectedHeaderName(briefingRegistration.getSupplier()
				.getCompanyName().concat("  ").concat(">"));
	}

	/**
	 * Method to delete the registrations
	 */
	public void deleteSupplierDetails() {

		List<ScQtBsvRegistration> registrations = this.getManagedBean()
				.getBriefingRegistrationList();
		// List<ScQtBsvRegistration> toBeDeletedRegistration = new
		// ArrayList<ScQtBsvRegistration>();
		if (null == registrations || registrations.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		ScQtBsvRegistration selectedReqistration = this.getManagedBean()
				.getSelectedbriefRegistration();

		if (null == selectedReqistration) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		if (null != selectedReqistration.getBsvRegistrationId()) {
			selectedReqistration.setDeleted(true);
			List<ScQtBsvRegistration> toBeDeltedList = this.getManagedBean()
					.getToBeDeletedRegistartions();
			if (null == toBeDeltedList) {
				toBeDeltedList = new ArrayList<ScQtBsvRegistration>();
			}
			toBeDeltedList.add(selectedReqistration);
			this.getManagedBean().setToBeDeletedRegistartions(toBeDeltedList);
		}
		registrations.remove(selectedReqistration);
		/*
		 * for (int i = 0; i < registrations.size(); i++) {
		 * 
		 * 
		 * if (registrations .get(i) .getSupplier() .getCompanyName()
		 * .equalsIgnoreCase( this.getManagedBean()
		 * .getSelectedbriefRegistration() .getSupplier().getCompanyName())) {
		 * registrations.get(i).setBsvAttendance(null); registrations.remove(i);
		 * break; } }
		 */

		this.getManagedBean().setBriefingRegistrationList(registrations);
		this.getManagedBean().setBriefingAttendanceList(null);
		this.getManagedBean().getSelectedbriefRegistration()
				.setBsvAttendance(null);

	}

	/**
	 * Set location, address info into Managed bean
	 * 
	 * @param briefing
	 *            the instance of ScQtBsvDtl
	 */
	public void showLocationDtls(ScQtBsvDtl briefing) {
		this.getManagedBean().setLocation(briefing.getLocation());
		/*
		 * StringBuffer address = new StringBuffer(briefing.getAddress()
		 * .getAddress1());
		 * 
		 * if (!StringUtils.isBlank(briefing.getAddress().getAddress2())) {
		 * address.append(",");
		 * address.append(briefing.getAddress().getAddress2());
		 * 
		 * } if (!StringUtils.isBlank(briefing.getAddress().getAddress3())) {
		 * address.append(",");
		 * address.append(briefing.getAddress().getAddress3());
		 * 
		 * } if (!StringUtils.isBlank(briefing.getAddress().getAddress4())) {
		 * address.append(",");
		 * address.append(briefing.getAddress().getAddress4());
		 * 
		 * }
		 */
		this.getManagedBean().setBsvDetails(briefing);
	}

	/**
	 * Get attandanceDtls by SelectedRegistartion.
	 * 
	 * @param mofAccounts
	 *            the instance of SmMofAccount list
	 */
	public void bsvRegistationListner(List<SmMofAccount> mofAccounts) {

		log.debug("businessNatureListener");
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
				bsvRegistration.setBsvDtl(this.getManagedBean()
						.getScQtBriefing());
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
				setSlectedHeaderName(bsvRegistration.getSupplier()
						.getCompanyName().concat("  ").concat(">"));
			}

		}
	}

	/**
	 * Method to delete the registrations- 
	 * Commented method - defect fix-5975
	 */
	/*public void deleteBsvAttandances() {

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
	}*/

	/**
	 * Method will notify whether BSvs completed or not.
	 * 
	 * @param zoneBean
	 *            the instance of BriefingZoneBean
	 * @return true if completes
	 */
	public boolean isAllBsvsComplete(BriefingZoneBean zoneBean) {

		ScQtBsv bsv = zoneBean.getScQtBsv();
		if (null != bsv && null != bsv.getQtBsvId()) {
			if (bsv.isSameBsv()) {
				List<ScQtBsvDtl> bsvDtls = zoneBean.getBriefingList();
				return isBsvStatusComplete(bsvDtls);
			} else {
				List<ScQtBsvDtl> bsvDetails = new ArrayList<ScQtBsvDtl>(
						zoneBean.getBriefingList());
				bsvDetails.addAll(zoneBean.getSitevisitList());
				return isBsvStatusComplete(bsvDetails);
			}
		}
		return true;
	}

	private boolean isBsvStatusComplete(List<ScQtBsvDtl> bsvDetails) {

		for (ScQtBsvDtl bsvDtl : bsvDetails) {
			if (!QTStatusConst.BSV_COMPLETED.equals(bsvDtl.getBsvStatus()
					.getStatusId())) {
				return false;
			}

		}
		return true;
	}

	private void getregistrationDetails(ScQtBsvDtl briefing) {

		this.getManagedBean().setBriefingAttendanceList(null);
		IQTBriefingSO qTBriefingSO = null;
		this.getManagedBean().setSelectedbriefRegistration(null);
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			this.getManagedBean().setBriefingRegistrationList(null);

			if (briefing != null) {
				this.getManagedBean().setScQtBriefing(briefing);
				BriefingReqDTO reqDTO = new BriefingReqDTO();
				reqDTO.setQtBriefingId(briefing.getQtBsvDtlId());

				BriefingResDTO resDTO = qTBriefingSO
						.retrieveBriefingRegistrationList(reqDTO);

				List<ScQtBsvRegistration> briefRegList = resDTO
						.getBriefingRegResultBean().getResultList();
				
				if(CollectionUtils.isNotEmpty(briefRegList)){
					for (ScQtBsvRegistration briefReg :briefRegList){
						if(briefReg.isRegisteredBySupplier()){
							retrieveAuthriozesPersonalBySupplierId(briefReg);
						}
						
					}
				}

				/*
				 * List<ScQtBsvRegistration> registrationList = new
				 * ArrayList<ScQtBsvRegistration>(); for (ScQtBsvRegistration
				 * scqtbriefReg : briefRegList) { //
				 * scqtbriefReg.setSmMofAccount(new SmMofAccount());
				 * registrationList.add(scqtbriefReg); }
				 */
				this.getManagedBean().setBriefingRegistrationList(briefRegList);

			}

		} finally {
			qTBriefingSO = null;
		}
	}

	/**
	 * Method will be executed by Ajax call.
	 * 
	 * @param event
	 *            the instance of AjaxBehaviorEvent
	 */
	public void updateAttandanceToSelectedBsvReg(AjaxBehaviorEvent event) {

		List<ScQtBsvAttendance> attandances = this.getManagedBean()
				.getBriefingAttendanceList();
		this.getManagedBean().getSelectedbriefRegistration()
				.setBsvAttendance(attandances);

	}

	/**
	 * Will be called on change of accordian panel..
	 * 
	 * @param event
	 *            the instance of TabChangeEvent
	 */
	public void onZoneTabChange(TabChangeEvent event) {

		BriefingZoneBean zoneBean = (BriefingZoneBean) event.getData();
		List<BriefingZoneBean> ZoneBeanLst = this.getManagedBean()
				.getZoneBean();
		if (ZoneBeanLst != null && ZoneBeanLst.contains(zoneBean)) {
			int indx = ZoneBeanLst.indexOf(zoneBean);
			this.getManagedBean().setBsvZoneindx(Integer.toString(indx));
		}
		this.getManagedBean().setNormalBsvBean(zoneBean);
	}

	/**
	 * Update the attendance flag based on Icpassport number
	 * 
	 * @param attandance
	 *            the instance of ScQtBsvAttendance
	 */
	public void updateIsAttandedFlag(ScQtBsvAttendance attandance) {

		List<ScQtBsvAttendance> attandanceList = this.getManagedBean()
				.getBriefingAttendanceList();
		for (int i = 0; i < attandanceList.size(); i++) {
			if (attandance.getIcPassPort().equalsIgnoreCase(
					attandanceList.get(i).getIcPassPort())) {
				attandanceList.remove(i);
				attandanceList.add(attandance);
				break;
			}
		}
	}

	private void getAttandanceDtlsBySelectedRegistartion(
			ScQtBsvRegistration briefingRegistration) {
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

		// this.getManagedBean().setBriefingAttendanceList(attanedList);

	}

	/**
	 * Return true if bsv date is greater or equal to Today date
	 * 
	 * @param bsvDtl
	 *            the instance of ScQtBsvDtl
	 * @return true/false
	 */
	public boolean showRegistrationIcon(ScQtBsvDtl bsvDtl) {

		if (null != bsvDtl) {
			return DateUtils.isDateGreaterOrEqual(new Date(),
					bsvDtl.getBsvDate());
		}
		return false;
	}

	/**
	 * @param attandance
	 */
	private int getAttandanceCount(List<ScQtBsvAttendance> attandance) {
		int count = 0;
		if (CollectionUtils.isNotEmpty(attandance)) {
			for (ScQtBsvAttendance attendance : attandance) {
				if (attendance.isAttended()) {
					count++;
				}
			}
		}
		return count;

	}

	/**
	 * Method is used to save Bsv registration details from maintenance flow.
	 */
	public String saveBsvRegistarionFromMaintanceFlow() {

		IQTBriefingSO qTBriefingSO = null;

		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			String languageCode = new FacesScopes().getUserLocale()
					.getLanguage();
			reqDTO.setLanguageCode(languageCode);

			ScQtBsvDtl scQtBrief = this.getManagedBean().getScQtBriefing();
			boolean invokeBPM = false;
			for (ScQtBsvRegistration registration : this.getManagedBean()
					.getBriefingRegistrationList()) {

				List<ScQtBsvAttendance> bsvAttandance = registration
						.getBsvAttendance();

				if (getAttandanceCount(bsvAttandance) > scQtBrief
						.getMaxNoOfAttendance()) {
					throw new EpException(MessageConst.ERROR_760230,
							new String[] { Integer.valueOf(
									scQtBrief.getMaxNoOfAttendance())
									.toString() });
				}
				boolean flag = isOneAuthorizedPersonal(bsvAttandance);

				if (flag) {
					registration.setAttended(true);
					if (null == registration.getBsvRegistrationId()) {
						invokeBPM = true;
						registration.setPostRegistered(true);
					}
				} else {
					throw new EpException(MessageConst.ERROR_760043,
							new String[] { "1" });
				}

			}

			// reqDTO.setQtBriefingList(briefingDetails);
			// reqDTO.setBriefingDetail(scQtBrief);
			reqDTO.setBriefingReqDtls(this.getManagedBean()
					.getBriefingRegistrationList());
			reqDTO.setScQt(this.getManagedBean().getScQt());
			reqDTO.setToBeDeletedRegistations(this.getManagedBean()
					.getToBeDeletedRegistartions());
			reqDTO.setInvokeBpm(invokeBPM);
			BriefingResDTO resDTO = qTBriefingSO.saveQTDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				ScQtBsvDtl bsv = resDTO.getScQtBriefing();
				String previousPage = this.getManagedBean().getPreviousPage();
				ScQt scQt = this.getManagedBean().getScQt();

				renewSessionWithPreviousValues();
				// this.getManagedBean().setScQtBriefing(bsv);
				this.getManagedBean().setPreviousPage(previousPage);
				this.getManagedBean().setScQt(scQt);

				getregistrationDetails(bsv);
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700005,
														new String[] {
																"New Registations for Briefing/SiteVisit",
																"" },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;

			}
		} finally {
			qTBriefingSO = null;
		}

		return "";

	}

	/**
	 * @return the slectedHeaderName
	 */
	public String getSlectedHeaderName() {
		return slectedHeaderName;
	}

	/**
	 * @param slectedHeaderName
	 *            the slectedHeaderName to set
	 */
	public void setSlectedHeaderName(String slectedHeaderName) {
		this.slectedHeaderName = slectedHeaderName;
	}

	
	private void retrieveAuthriozesPersonalBySupplierId(ScQtBsvRegistration briefingRegistration){
		
		    List<ScQtBsvAttendance> attanedList = new ArrayList<ScQtBsvAttendance>();
			IQTBriefingSO bsvSo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setSupplierId(briefingRegistration.getSupplier().getSupplierId());
			BriefingResDTO resDTO = bsvSo
					.retrieveAuthriozesPersonalBySupplierId(reqDTO);
			List<SmPersonnel> personals = resDTO.getPersonals();

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
			
			List<ScQtBsvAttendance>  existingAttandances = briefingRegistration.getBsvAttendance();
			if (CollectionUtils.isNotEmpty(briefingRegistration.getBsvAttendance())) {
				for (ScQtBsvAttendance extAttandance : existingAttandances) {
					if (attanedList.contains(extAttandance)) {
						attanedList.remove(extAttandance);
					}
					attanedList.add(extAttandance);
				}
			}
			briefingRegistration.setBsvAttendance(attanedList);
		}
	
	public void updateBriefingAttendance(ScQtBsvAttendance bsvAttendance,List<ScQtBsvAttendance> listAttendance,boolean attendance){
		int i=listAttendance.indexOf(bsvAttendance);
		bsvAttendance.setAttended(attendance);
		listAttendance.set(i, bsvAttendance);
	}
}
