/* Copyright © 2014 Archisoft. All rights reserved. */
package my.ep.web.backingbean.sc.bsvattendance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.jaxb.sc.sitevisitproposaldata.SCSiteVisitProposalDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtBSVRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.bsvattendance.BSVAttendanceManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 * BSV Attendance BackingBean class is used to display Brifing, Sitevisit
 * registration & attendance details in BSV Attendance screen.
 * 
 * @author sri
 */
@ManagedBean(name = "bsvAttendanceBackingBean")
@ViewScoped
public class BSVAttendanceBackingBean extends
		SourcingBaseBackingBean<BSVAttendanceManagedBean> implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_BSV_REG_PAGE = "/xhtml/sc/bsvattendance/addattendance/bsvAttendanceRegisterPopup.xhtml";
    private static final String BSV_MAIN_PAGE = "/xhtml/sc/bsvattendance/attendancedetails/bsvAttendanceRegisterMain.xhtml";
    private static final String SAVE_SUCCESS_MSG_KEY = "lbl.sc.bsv.save.message";
    
	public static Logger log = Logger.getLogger(BSVAttendanceBackingBean.class);
	private String slectedHeaderName;	
	
	/** The text before sign. */
	private String textBeforeSign;

	public BSVAttendanceBackingBean() {

	}

	/**
	 * retrieving Briefing SiteVisit List
	 * 
	 * @param qtId
	 *            the Long variable
	 */
	public void retrieveBriefingSiteVisitList(Long qtId) {

		log.debug("** retrieveBriefingSiteVisitList **");
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
						
						//#25095 - start -
						if (briefingResDTO.getBriefingList() != null) {
							List<ScQtBsvDtl> newBriefingList = new ArrayList<ScQtBsvDtl>();
							
							for (ScQtBsvDtl scqtdtl : briefingResDTO.getBriefingList()) {
								scqtdtl = getregistrationDetailsWithAttandance(scqtdtl, false);
								
								newBriefingList.add(scqtdtl);
								
								briefingZoneBean.setBriefingList(newBriefingList);								
							}
						}
						
						if (briefingResDTO.getSiteVisitList() != null) {
							List<ScQtBsvDtl> newSiteVisitList = new ArrayList<ScQtBsvDtl>();
							
							for (ScQtBsvDtl scqtdtl : briefingResDTO.getSiteVisitList()) {
								scqtdtl = getregistrationDetailsWithAttandance(scqtdtl, false);
								
								newSiteVisitList.add(scqtdtl);
								
								briefingZoneBean.setSitevisitList(newSiteVisitList);								
							}
						}
						//#25095 - end -						
						
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
					scQtBsv.setBsvRemarks(initializeRemarks(scQtBsv, 
							this.getManagedBean().getScQt().getCurrentWorkFlowStatus(),
							scQtBsv.getBsvRemarks(), ScQtBSVRemark.class));

					briefingZoneBean = new BriefingZoneBean(null,
							resDTO.getBriefingList(),
							resDTO.getSiteVisitList(), scQtBsv);
					
					//#25095 - start -
					if(resDTO.getBriefingList() != null) {
						List<ScQtBsvDtl> newBriefingList = new ArrayList<ScQtBsvDtl>();
						
						for (ScQtBsvDtl scqtdtl : resDTO.getBriefingList()) {
							scqtdtl = getregistrationDetailsWithAttandance(scqtdtl, false);
							
							newBriefingList.add(scqtdtl);
							
							briefingZoneBean.setBriefingList(newBriefingList);							
						}					
					}
					
					if(resDTO.getSiteVisitList() != null) {
						List<ScQtBsvDtl> newSiteVisitList = new ArrayList<ScQtBsvDtl>();
						
						for (ScQtBsvDtl scqtdtl : resDTO.getSiteVisitList()) {
							scqtdtl = getregistrationDetailsWithAttandance(scqtdtl, false);
							
							newSiteVisitList.add(scqtdtl);
							
							briefingZoneBean.setSitevisitList(newSiteVisitList);						
						}
					}
					//#25095 - end -					
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

		log.debug("** attendanceRegistration **");
		this.getManagedBean().setPreviousPage(BSV_MAIN_PAGE);
		getregistrationDetails(briefing, false);

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
				/*Date todayDate = DateUtils.setDefaultDateTime(DateUtils.getCurrentDateWithoutTime());
				if (!DateUtils.isDateGreater(scQt.getClosingDate(), todayDate)) {
					try {
						log.info("call simple scheduler if QT is closing today.");
						ScSchedulerHelper.getInstance().scheduleSimpleQTClosingJob(scQt);
						EpSchedulerHelper.shutdownScheduler();
					} catch (SchedulerException e) {
						e.printStackTrace();
						log.error(e.getMessage());
					}
				}*/
				
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
																 scQt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} finally {
			qTBriefingSO = null;
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
				boolean flag = isAttendedOrPreRegistered(registration, bsvAttandance);
				if (!flag) {
					throw new EpException(MessageConst.ERROR_760043);
				}

			}

			Long bsvStatusId = null;
			if (SourcingConstants.COMPLETE.equalsIgnoreCase(flowType)) {
				bsvStatusId = QTStatusConst.BSV_COMPLETED;
			} else {
				bsvStatusId = QTStatusConst.BSV_PENDING;
			}

			// reqDTO.setQtBriefingList(briefingDetails);
			reqDTO.setBriefingDetail(scQtBrief);
			reqDTO.setBsvStatusId(bsvStatusId);
			reqDTO.setBriefingReqDtls(this.getManagedBean()
					.getBriefingRegistrationList());
			reqDTO.setScQt(this.getManagedBean().getScQt());
			reqDTO.setToBeDeletedRegistations(this.getManagedBean()
					.getToBeDeletedRegistartions());
			BriefingResDTO resDTO = qTBriefingSO.updateBSVAttendanceRegistration(reqDTO);
			this.getManagedBean().setSelectedbriefRegistrationEnableDisable(null);
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

				getregistrationDetails(bsv, false);
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

	private boolean isAttendedOrPreRegistered(ScQtBsvRegistration bsvRegistration, List<ScQtBsvAttendance> bsvAttandance) {
		boolean isAttendedOrPreRegistered = false;
		
		bsvRegistration.setAttended(false);
		
		if (null == bsvAttandance || bsvAttandance.isEmpty()) {
			return isAttendedOrPreRegistered;
		}
		for (ScQtBsvAttendance attendance : bsvAttandance) {
			if (null != bsvAttandance) {
				boolean isAuthorizedAndAttended = attendance.isAuthorized() && attendance.isAttended();
				
				if (isAuthorizedAndAttended || attendance.isPreRegisterd()) {
					isAttendedOrPreRegistered = true;
				}
				
				if (isAuthorizedAndAttended) {
					bsvRegistration.setAttended(true);
					break;
				}
			}
		}
		return isAttendedOrPreRegistered;
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
		}finally{
			briefingSO=null;
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

		log.debug("** Back Button **");
		this.getManagedBean().setSelectedbriefRegistrationEnableDisable(null);
		this.getManagedBean()
		.setToBeDeletedRegistartions(null);
		retrieveBriefingSiteVisitList(this.getManagedBean().getScQt().getQtId());
		return this.getManagedBean().getPreviousPage()
				+ "?faces-redirect=true";

	}

	

	/**
	 * Method will execute on row select in data table.
	 * 
	 *@param event
	 *            the instance of SelectEvent
	 *            
	 */
	public void onRowSelect(SelectEvent event) {

		this.getManagedBean().setScQtBriefingAttendance(null);
		ScQtBsvRegistration briefingRegistration = (ScQtBsvRegistration) event
				.getObject();
		this.getManagedBean().setSelectedbriefRegistrationEnableDisable(briefingRegistration);
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
		this.getManagedBean().setSelectedbriefRegistrationEnableDisable(null);

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
			// #29770 - #26247 - Start.
			IQTBriefingSO bsvSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			
			try {
				for (int i = 0; i < mofAccounts.size(); i++) {
					SmMofAccount mofAccount = mofAccounts.get(i);
					ScQtBsvRegistration bsvRegistration = new ScQtBsvRegistration();
					SmSupplier smSupplier = bsvSo.retrieveActiveDisciplinaryActionBySupplier(mofAccount.getSmSupplier());
					
					bsvRegistration.setSupplier(smSupplier);
					bsvRegistration.setBsvDtl(this.getManagedBean().getScQtBriefing());
					
					if (getManagedBean().getBriefingRegistrationList().contains(bsvRegistration)) {
						throw new EpException(MessageConst.ERROR_760186);
					}
					
					getManagedBean().getBriefingRegistrationList().add(bsvRegistration);
					
					if (i == mofAccounts.size() - 1) {
						this.getManagedBean().setSelectedbriefRegistration(bsvRegistration);
						getAttandanceDtlsBySelectedRegistartion(bsvRegistration);
					}
					
					setSlectedHeaderName(bsvRegistration.getSupplier().getCompanyName().concat("  ").concat(">"));
				}
			} finally {
				bsvSo = null;
			}
			// #29770 - #26247 - End.
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
	 * Get BSV Registration Details.
	 * 
	 * @param briefing
	 * 		the ScQtBsvDtl instance
	 * @param maintanceFlow
	 * 		the maintenance flow flag.
	 */
	public void getregistrationDetails(ScQtBsvDtl briefing, boolean maintanceFlow) {

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
				reqDTO.setMaintanceFlow(maintanceFlow);

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
				
				//#25095 - start -
				/*calculate attendance*/
				int count = 0;
				
				for (ScQtBsvRegistration registration : briefRegList) {
					List<ScQtBsvAttendance> bsvAttandance = registration.getBsvAttendance();	
					
					count = count + getAttandanceCount(bsvAttandance);
					
					// 27479, 26247 - Start
					SmSupplier smSupplier = qTBriefingSO.retrieveActiveDisciplinaryActionBySupplier(registration.getSupplier());
					registration.setSupplier(smSupplier);
					// 27479, 26247 - End
				}
				
				this.getManagedBean().setAttandance_count(count);
				
				log.info("attandanceCount = "+count);
				//#25095 - end -
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
	
	//#25095 - start -
	/**
	 * Get BSV Registration Details.
	 * 
	 * @param briefing
	 * 		the ScQtBsvDtl instance
	 * @param maintanceFlow
	 * 		the maintenance flow flag.
	 */
	public ScQtBsvDtl getregistrationDetailsWithAttandance(ScQtBsvDtl briefing, boolean maintanceFlow) {			
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
				reqDTO.setMaintanceFlow(maintanceFlow);

				BriefingResDTO resDTO = qTBriefingSO.retrieveBriefingRegistrationList(reqDTO);

				List<ScQtBsvRegistration> briefRegList = resDTO.getBriefingRegResultBean().getResultList();
				
				if(CollectionUtils.isNotEmpty(briefRegList)) {
					for (ScQtBsvRegistration briefReg :briefRegList) {
						if(briefReg.isRegisteredBySupplier()) {
							retrieveAuthriozesPersonalBySupplierId(briefReg);
						}						
					}
				}				
				
				/*calculate attendance*/
				int count = 0;
				
				for (ScQtBsvRegistration registration : briefRegList) {

					List<ScQtBsvAttendance> bsvAttandance = registration.getBsvAttendance();	
					
					count = count + getAttandanceCount(bsvAttandance);
					
					// 27479, 26247 - Start
					SmSupplier smSupplier = qTBriefingSO.retrieveActiveDisciplinaryActionBySupplier(registration.getSupplier());
					registration.setSupplier(smSupplier);
					// 27479, 26247 - End
				}
				
				briefing.setToBeattand(count);
				/*
				 * List<ScQtBsvRegistration> registrationList = new
				 * ArrayList<ScQtBsvRegistration>(); for (ScQtBsvRegistration
				 * scqtbriefReg : briefRegList) { //
				 * scqtbriefReg.setSmMofAccount(new SmMofAccount());
				 * registrationList.add(scqtbriefReg); }
				 */
				this.getManagedBean().setAttandance_count(count);
				this.getManagedBean().setBriefingRegistrationList(briefRegList);
				
				log.info("attandanceCount = "+count);
			}

		} finally {
			qTBriefingSO = null;			
		}
		
		return briefing;
	}
	//#25095 - end - 

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
			briefingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setSupplierId(briefingRegistration.getSupplier()
					.getSupplierId());
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
				//Start Bug #26525
				briefingAttendance.setContactNo(personnel.getPhoneFull());
				//End Bug #26525
				briefingAttendance.setEmail(personnel.getEmail());
				briefingAttendance.setBsvRegistration(briefingRegistration);
				briefingAttendance.setAuthorized(true);
				attanedList.add(briefingAttendance);
			}
		}
		}finally{
			briefingSO=null;	
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

		if (null != bsvDtl && null != bsvDtl.getBsvStatus() && 
				!QTStatusConst.BSV_CANCELLED.equals(bsvDtl.getBsvStatus().getStatusId())) {
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
				boolean flag = isAttendedOrPreRegistered(registration, bsvAttandance);

				if (flag) {
					if (registration.isAttended()) {
						if (null == registration.getBsvRegistrationId() || registration.isModified()) {
							invokeBPM = true;
							registration.setPostRegistered(true);
							registration.setRecordStatusFlg(DbValueConst.RECORD_STATUS_IN_PROGRESS);
						}
					}
					
				} else {
					throw new EpException(MessageConst.ERROR_760043);
				}

			}
			
			if (!invokeBPM) {
				throw new EpException(MessageConst.ERROR_760206);
			}

			// reqDTO.setQtBriefingList(briefingDetails);
			// reqDTO.setBriefingDetail(scQtBrief);
			reqDTO.setBriefingReqDtls(this.getManagedBean()
					.getBriefingRegistrationList());
			reqDTO.setScQt(this.getManagedBean().getScQt());
			
			ScQtApproval scQtApproval = this.getManagedBean().getScQtApproval();
			List<ScQtApprovalRemark> scQtApprovalRemarks = super.finalizeRemarks(scQtApproval.getScQtApprovalRemarks());
			List<ScQtApprovalRemark> newApprovalRemarks = super.finalizeRemarks(getManagedBean().getScQtApprovalRemarks());
			if (CollectionUtils.isNotEmpty(newApprovalRemarks)) {
				if (scQtApprovalRemarks == null) {
					scQtApprovalRemarks = new ArrayList<ScQtApprovalRemark>();
				}
				scQtApprovalRemarks.addAll(0, newApprovalRemarks);
			}
			scQtApproval.setScQtApprovalRemarks(scQtApprovalRemarks);
			List<ScQtApprovalAttachment> scQtApprovalAttachments = scQtApproval.getScQtApprovalAttachments();
			if (CollectionUtils.isNotEmpty(getManagedBean().getScQtApprovalAttachments())) {
				if (scQtApprovalAttachments == null) {
					scQtApprovalAttachments = new ArrayList<ScQtApprovalAttachment>();
				}
				scQtApprovalAttachments.addAll(getManagedBean().getScQtApprovalAttachments());
			}
			scQtApproval.setScQtApprovalAttachments(scQtApprovalAttachments);
			reqDTO.setApproval(scQtApproval);
			
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

				getregistrationDetails(bsv, true);
				
				Locale locate = new FacesScopes().getUserLocale();
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
																ResourceBundleUtils.getMessage(
																		"lbl.sc.bsv.additional.attendance", 
																		ResourceBundleUtils.BUNDLE_COMMON_LABELS,
																		locate),
																scQt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														locate)));

				return CommonUIConst.ACKNOWLEDGE_PAGE;

			}
		} finally {
			qTBriefingSO = null;
		}

		return "";

	}

	/**
	 * return SlectedHeadername.
	 * @return the slectedHeaderName
	 */
	public String getSlectedHeaderName() {
		return slectedHeaderName;
	}

	/**
	 * set slectedHeaderName.
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
				//Start Bug #26525
				briefingAttendance.setContactNo(personnel.getPhoneFull());
				//end Bug #26525				
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

		// #26247 -start-
		SmSupplier smSupplier = bsvSo.retrieveActiveDisciplinaryActionBySupplier(briefingRegistration.getSupplier());
		briefingRegistration.setSupplier(smSupplier);
		// #26247 -end-
	}
	
	/**
	 * update briefing attendance details info.
	 * 
	 * @param bsvAttendance
	 * @param listAttendance
	 * @param attendance
	 *
	 */
	public void updateBriefingAttendance(ScQtBsvAttendance bsvAttendance, ScQtBsvRegistration scQtBsvRegistration,boolean attendance){
		List<ScQtBsvAttendance> listAttendance = scQtBsvRegistration.getBsvAttendance();
		int i=listAttendance.indexOf(bsvAttendance);
		bsvAttendance.setAttended(attendance);
		listAttendance.set(i, bsvAttendance);
		scQtBsvRegistration.setModified(true);
	}
	
	
	
	/**
	 * Return queryString to fetch Panel Or Restricted Supplier's MoF number
	 * 
	 * @return queryString
	 */
	public String constuctConditionFields() {
		
		ScQt scqt = this.getManagedBean().getScQt();
		String queryString = null;
		if(scqt.isPanel()){
			ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
			TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
			specReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			TemplateSpecResDTO templateSpecResDTO = templateSpecSO.retrievePanelItemsByQtId(specReqDTO);
			List<CtPanelItem> panelItems = templateSpecResDTO.getCtPanelItems();
			if (CollectionUtils.isNotEmpty(panelItems)) {
				queryString = "ctPanelItem.panelItemId^"+panelItems.get(0).getPanelItemId();
				if (scqt.isZonal()) {
					TplSpecZoneDtl tplspecZoneDtl = this.getManagedBean()
							.getScQtBriefing().getScQtBsv().getTplSpecZoneDtl();
					if (tplspecZoneDtl != null) {
						List<CtPanelZone> panelZones = panelItems.get(0).getCtPanelZones();
						if (panelZones != null && !panelZones.isEmpty()) {
							for (CtPanelZone panelZone : panelZones) {
								if (tplspecZoneDtl.getZoneName().equals(panelZone.getName())) {
									queryString = queryString
											+ "|ctPanelZone.panelZoneId^"+ panelZone.getPanelZoneId();
									break;
								}
							}
						}
					}
				}
			}
			
		} else if (ParameterTypeConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
				.equalsIgnoreCase(scqt.getQtType())) {
			queryString = "scQt.qtId^" + scqt.getQtId();
		}
		return queryString;
	}
	
	/**
	 * Register the Panel Supplier for BSV
	 * 
	 * @param panelSuppliers
	 */
	public void bsvPanelSupplierRegistationListner(List<CtPanelSupplier> panelSuppliers) {

		log.debug("bsvPanelSupplierRegistationListner");
		if (getManagedBean().getBriefingRegistrationList() == null
				|| getManagedBean().getBriefingRegistrationList().isEmpty()) {
			getManagedBean().setBriefingRegistrationList(
					new ArrayList<ScQtBsvRegistration>());
		}
		List<SmMofAccount> mofAccounts = null;
		if (panelSuppliers != null) {
			mofAccounts = new ArrayList<SmMofAccount>();
			for (CtPanelSupplier panelSupplier : panelSuppliers) {
				mofAccounts.add(panelSupplier.getSupplier().getSmMofAccounts()
						.get(0));
			}
		}
		if (mofAccounts != null&& !mofAccounts.isEmpty()) {
			registerMofAccount(mofAccounts);
		}
	}
	
	/**
	 * Register Restricted Tender suppliers for BSV
	 * 
	 * @param restrictedSuppliers
	 */
	public void bsvRestrictedSuppRegistationListner(List<ScQtSupplier> restrictedSuppliers) {

		log.debug("bsvRestrictedSuppRegistationListner");
		if (getManagedBean().getBriefingRegistrationList() == null
				|| getManagedBean().getBriefingRegistrationList().isEmpty()) {
			getManagedBean().setBriefingRegistrationList(
					new ArrayList<ScQtBsvRegistration>());
		}
		List<SmMofAccount> mofAccounts = null;
		if (restrictedSuppliers != null) {
			mofAccounts = new ArrayList<SmMofAccount>();
			for (ScQtSupplier restrictedSupplier : restrictedSuppliers) {
				mofAccounts.add(restrictedSupplier.getSmSupplier().getSmMofAccounts()
						.get(0));
			}
		}
		
		if (mofAccounts != null && !mofAccounts.isEmpty()) {
			registerMofAccount(mofAccounts);
		}
	}
	
	private void registerMofAccount(List<SmMofAccount> mofAccounts){
		
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
	
	/**
	 *To Get MofAccountNum by supplier.
	 *@param supplier 
	 * 
	 * @return supplier mofAccountNum.
	 */
	public String mofAccountNumber(SmSupplier supplier){
		return retriveMofAccountBySupplier(supplier).getMofNo();
	}
	
	/**
	 * Add attachment to list.
	 * 
	 * @param event
	 *            File upload event instance
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		log.debug("handleFileUpload - START");
		if (event.getFile() != null) {
			ScQtApprovalAttachment attachment = new ScQtApprovalAttachment();
			ScQtApproval scQtApproval = getManagedBean().getScQtApproval();
			List<ScQtApprovalAttachment> attachments = getManagedBean().getScQtApprovalAttachments();
			if (null == attachments) {
				attachments = new ArrayList<ScQtApprovalAttachment>();
			}
			attachment.setScQtApproval(scQtApproval);
			attachment.setDocNo(scQtApproval.getScQt().getQtNo());
			attachments.add(attachment);
			getManagedBean().setScQtApprovalAttachments(attachments);
			uploadAttachment(event.getFile(), attachment);
		}
		log.debug("handleFileUpload - END");
	}
	
	/**
	 *Navigate to additional Attendance Approval Details Page.
	 * 
	 *
	 */
	public void preRenderViewAdditionalAttendance() {

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
			ScQt scQt = briefingResDTO.getScQt();
			List<ScQtApprovalRequest> approvalRequests = briefingResDTO
					.getApprovalRequests();
			ScQtApprovalRequest approvalRequest = approvalRequests.get(0);
			ScQtApproval approval = approvalRequest.getScQtApproval();
			List<ScQtApprovalRemark> scQtApprovalRemarks = new ArrayList<ScQtApprovalRemark>();
			List<ScQtApprovalAttachment> scQtApprovalAttachments = new ArrayList<ScQtApprovalAttachment>();
			this.getManagedBean().setScQtApproval(approval);
			if (approvalRequest.getSubmissionDate() != null) {
				Date submissionDate = approvalRequest.getSubmissionDate();
				for (ScQtApprovalRemark scQtApprovalRemark : approval.getScQtApprovalRemarks()) {
					// To show remark matched with the submission date
					if (scQtApprovalRemark.getCreatedDate().compareTo(submissionDate) == 0) {
						scQtApprovalRemarks.add(scQtApprovalRemark);
					}
				}
				for (ScQtApprovalAttachment scQtApprovalAttachment : approval.getScQtApprovalAttachments()) {
					// To show attachment matched with the submission date
					if (scQtApprovalAttachment.getCreatedDate().compareTo(submissionDate) == 0) {
						scQtApprovalAttachments.add(scQtApprovalAttachment);
					}
				}
			}
			this.getManagedBean().setScQtApprovalRemarks(initializeRemarks(approval, 
					scQt.getCurrentWorkFlowStatus(),
					scQtApprovalRemarks, 
					ScQtApprovalRemark.class));
			this.getManagedBean().setScQtApprovalAttachments(scQtApprovalAttachments);
			this.getManagedBean().setScQtApprovalRequests(approvalRequests);
			this.getManagedBean().setScQt(scQt);
			this.getManagedBean().setBriefingRegistrationList(
					briefingResDTO.getBriefingRegResultBean().getResultList());
			this.getManagedBean().setScQtBriefing(
					briefingResDTO.getScQtBriefing());

		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"/xhtml/sc/bsvattendance/approval/additionalAttandanceApproval.xhtml");
	}
	
	/**
	 * Gpki sign action.Event handle when Approver submit.
	 * 
	 * @param digiSign
	 *            (Mandatory)
	 * @return the string(ACKNOWLEDGE_PAGE)
	 */
	public String gpkiSignAction(PmDigiSign digiSign) {
		return approveAdditionalAttendance(this.getManagedBean()
				.getAction(), digiSign);
	}
	
	/**
	 * Gets the text before sign.
	 * 
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.
	 * 
	 * @param textBeforeSign
	 *            the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		StringBuilder sb = new StringBuilder();
		sb.append(getManagedBean().getScQt().getQtNo());
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		this.textBeforeSign = sb.toString();
	}
	
	/**
	 * Submit Approval Details. Event handle to submit data for bpm.
	 * 
	 * @param action
	 * 		the action out come
	 */
	public String updateAdditionalAttendanceBpmTask(String action) {
		if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(action)) {
			ScQtApproval approval = this.getManagedBean().getScQtApproval();
			if (null != approval) {
				List<ScQtApprovalRemark> remarks = getManagedBean().getScQtApprovalRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { 
										ResourceBundleUtils.getMessage(
											"lbl.sc.remark", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS,
											new FacesScopes().getUserLocale()) });
						}
					}
				}	
			}
			
			return approveAdditionalAttendance(action, null);
		} else {
			this.getManagedBean().setAction(action);
			this.getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			
			return null;
		}
	}
	
	/**
	 * Submit registarion details.
	 * 
	 * @param outcome
	 *            the outcome
	 * @param digiSign
	 * 			the PmDigiSign instance
	 * @return the string
	 */
	private String approveAdditionalAttendance(String outcome, PmDigiSign digiSign) {

		BriefingReqDTO briefingReqDTO = new BriefingReqDTO();

		FacesScopes facesScopes = new FacesScopes();
		Object ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		
		IQTBriefingSO briefingSO;
		try {
			ScQt scQt = this.getManagedBean().getScQt();
			List<ScQtBsvRegistration> registrations = this.getManagedBean()
					.getBriefingRegistrationList();
			
			briefingReqDTO.setBriefingReqDtls(registrations);
			briefingReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			briefingReqDTO.setScQt(scQt);
			briefingReqDTO.setToken(ctxToken);
			briefingReqDTO.setTaskId(this.getManagedBean().getTaskId());
			briefingReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			briefingReqDTO.setOutcome(outcome);
			briefingReqDTO.setTplSpecZoneDtl(this.getManagedBean()
					.getScQtBriefing().getScQtBsv().getTplSpecZoneDtl());
			ScQtApproval approval = this.getManagedBean().getScQtApproval();
			if (null != approval) {
				List<ScQtApprovalRemark> newApprovalRemarks = finalizeRemarks(getManagedBean().getScQtApprovalRemarks());
				if (CollectionUtils.isNotEmpty(newApprovalRemarks)) {
					ScQtApprovalRemark newApprovalRemark = newApprovalRemarks.get(0);
					if (newApprovalRemark.isBrandNew()) {
						List<ScQtApprovalRemark> scQtApprovalRemarks = approval.getScQtApprovalRemarks();
						if (scQtApprovalRemarks == null) {
							scQtApprovalRemarks = new ArrayList<ScQtApprovalRemark>();
						}
						scQtApprovalRemarks.add(0, newApprovalRemark);
						approval.setScQtApprovalRemarks(scQtApprovalRemarks);	
					}
				}
				
				List<ScQtApprovalAttachment> newApprovalAttachments = getManagedBean().getScQtApprovalAttachments();
				if (CollectionUtils.isNotEmpty(newApprovalAttachments)) {
					List<ScQtApprovalAttachment> scQtApprovalAttachments = approval.getScQtApprovalAttachments();
					if (scQtApprovalAttachments == null) {
						scQtApprovalAttachments = new ArrayList<ScQtApprovalAttachment>();
					}
					for (ScQtApprovalAttachment newApprovalAttachment : newApprovalAttachments) {
						if (newApprovalAttachment.isBrandNew()) {
							scQtApprovalAttachments.add(newApprovalAttachment);
						}
					}
					approval.setScQtApprovalAttachments(scQtApprovalAttachments);
				}
 				
				updateApprovalRequest(this.getManagedBean()
						.getScQtApprovalRequests(), outcome);
				approval.setScQtApprovalDtls(this.getManagedBean()
						.getScQtApprovalRequests());
				briefingReqDTO.setApproval(approval);

			}
			
			if (null != digiSign) {
				updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_BSV_ATTENDANCE,
						scQt.getQtId(), scQt.getQtNo());
			}
			briefingReqDTO.setDigiSignData(digiSign);
			
			briefingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);

			BriefingResDTO resDTO = briefingSO
					.updateSupplierRegistionsNSendNotifications(briefingReqDTO);

			if (resDTO.isSuccessful()) {

				renewSesionObject();

				Locale locate = new FacesScopes().getUserLocale();
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
															ResourceBundleUtils.getMessage(
																	"lbl.sc.bsv.additional.attendance", 
																	ResourceBundleUtils.BUNDLE_COMMON_LABELS,
																	locate),
																scQt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														locate)));
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
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);		
	}
}
