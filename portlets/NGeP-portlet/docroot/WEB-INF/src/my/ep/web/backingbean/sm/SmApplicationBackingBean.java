package my.ep.web.backingbean.sm;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.attachment.ISmAttachmentSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.reviewApplication.IReviewApplicationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.zone.ZoneReqDTO;
import my.ep.dto.sm.attachment.SmAttachmentReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.revertApplication.RevertApplicationReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmEduLevel;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmRace;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmRegStatus;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmCompanyExperienceCat;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBranchBank;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierSystem;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.backingbean.sm.questionnaire.QuestionnaireBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationNotificationUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmFieldChangeUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmAuthorizedPersonnelCountVO;
import my.ep.web.vo.SmPersonnleCalcVO;
import my.ep.web.vo.SmSiteVisitFacilityVO;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import com.liferay.portal.kernel.util.StringUtil;

// TODO: Auto-generated Javadoc
@ManagedBean(name = "backingBean")
@RequestScoped
public class SmApplicationBackingBean extends
		BaseBackingBean<SmApplicationManagedBean> {

	public static Logger log = Logger.getLogger(SmApplicationBackingBean.class);

	private int currentTab;
	private int outputTab;
	private String buttonType = "";
	private StreamedContent file;
	NumberFormat formatter = new DecimalFormat("#0.00");
	SmRuleHelper ruleHelper = new SmRuleHelper();

	/**
	 * Instantiates a new sm application backing bean.
	 */
	public SmApplicationBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	/**
	 * Renew session object by application type.
	 * 
	 * @param applicationType
	 *            - the application type
	 */
	public void renewSesionObjectByApplicationType(String applicationType) {
		renewSesionObject();
		log.info("*** renewSesionObjectByApplicationType *** "
				+ applicationType);
		SmApplicationUtil.setNavigationForApplTnCPage(applicationType);
	}

	/**
	 * Pre-render re-submission questionnaire page.
	 */
	public void prerenderResubmissionQuestionaire() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			log.info("**** Prerender Questions *************");
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			HttpServletRequest originalServletRequest = new FacesScopes()
					.getOriginalServletRequest();
			String applId = originalServletRequest.getParameter("apId");

			log.info("application id : " + applId);
			SmAppl rejAppl = null;
			SmSupplier supplierProfile = null;
			String xhtml = "/xhtml/sm/resubmitApplication/prerenderResubmitApplication.xhtml";
			if (applId != null) {
				MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
				mofResDto = SmApplicationUtil.retriveApplicationById(Integer
						.parseInt(applId));
				rejAppl = mofResDto.getSmAppl();
				supplierProfile = mofResDto.getSmSupplier();
			}

			this.getManagedBean().setValidResubmit(true);
			if (rejAppl != null && supplierProfile != null) {
				boolean activeExist = false;
				activeExist = SmApplicationUtil
						.checkActiveSmApplForSupp(supplierProfile);
				if (rejAppl.getApplSubmitDate() != null) {
					Timestamp currentDate = new Timestamp(
							System.currentTimeMillis());
					long diff = currentDate.getTime()
							- rejAppl.getApplSubmitDate().getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);
					if (diffDays > ruleHelper.getApplicationResubmissionDays()) {
						getManagedBean().setResubmitInValidMsg(
								SmCommonUtil.getMessage(
										"lbl.sm.resubmitExpired", null));
						this.getManagedBean().setValidResubmit(false);
					}
				}
				if (!activeExist && this.getManagedBean().isValidResubmit()) {
					MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
					reqDTO.setCreatedDate(rejAppl.getCreatedDate());
					reqDTO.setSupplierId(supplierProfile.getSupplierId());
					reqDTO.setAppType(rejAppl.getApplType());

					MofRegistrationResDTO resDto = null;
					// 1.1. Check for resubmitted and rejected
					StringBuilder sb = new StringBuilder();
					sb.append("(")
							.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER)
							.append(",")
							.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER)
							.append(",")
							.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM)
							.append(")");
					reqDTO.setWorkflowStatus(sb.toString());
					resDto = mofRegistrationSO
							.checkForApplicationDuplicateResubmission(reqDTO);
					if (resDto != null && resDto.getResubmitApplnCount() > 0) {
						getManagedBean()
								.setResubmitInValidMsg(
										SmCommonUtil
												.getMessage(
														"lbl.sm.resubmitAndRejectApplicationCantProceed",
														null));
						this.getManagedBean().setValidResubmit(false);
					}
					// 1.2 Check for resubmitted and approved
					if (this.getManagedBean().isValidResubmit()) {
						sb = new StringBuilder();
						sb.append("(")
								.append(SupplierManagementConst.PM_STATUS_MOF_ACCT_REG_APPROVED)
								.append(",")
								.append(SupplierManagementConst.PM_STATUS_MOF_ACCT_MAINTAIN_APPROVED)
								.append(")");
						reqDTO.setWorkflowStatus(sb.toString());
						resDto = mofRegistrationSO
								.checkForApplicationDuplicateResubmission(reqDTO);
						if (resDto != null
								&& resDto.getResubmitApplnCount() > 0) {
							getManagedBean()
									.setResubmitInValidMsg(
											SmCommonUtil
													.getMessage(
															"lbl.sm.approvedApplicationCantProceed",
															null));
							this.getManagedBean().setValidResubmit(false);
						}
					}
					// 1.3 Check for resubmitted and pending final payment
					if (this.getManagedBean().isValidResubmit()) {
						sb = new StringBuilder();
						sb.append("(")
								.append(SupplierManagementConst.PM_STATUS_PENDING_PAYMENT)
								.append(")");
						reqDTO.setWorkflowStatus(sb.toString());
						resDto = mofRegistrationSO
								.checkForApplicationDuplicateResubmission(reqDTO);
						if (resDto != null
								&& resDto.getResubmitApplnCount() > 0) {
							getManagedBean()
									.setResubmitInValidMsg(
											SmCommonUtil
													.getMessage(
															"lbl.sm.approvedApplicationPendingFinalPay",
															null));
							this.getManagedBean().setValidResubmit(false);
						}
					}
					if (this.getManagedBean().isValidResubmit()
							&& rejAppl.getCreatedDate().compareTo(
									supplierProfile.getSmAppl()
											.getCreatedDate()) < 0) {
						getManagedBean().setResubmitInValidMsg(
								SmCommonUtil.getMessage(
										"lbl.sm.anotherApplApproved", null));
						this.getManagedBean().setValidResubmit(false);
					}
					if (this.getManagedBean().isValidResubmit()) {
						SmAppl rejectedAppl = SmApplicationUtil
								.retriveApplicationById(
										Integer.parseInt(applId)).getSmAppl();
						SmAppl latestResubmitAppl = null;
						if (rejectedAppl.isQuestionnaire()) {
							latestResubmitAppl = SmApplicationUtil
									.getLatesResubmitApplicationByRejectedAppl(rejectedAppl);
						}
						if (latestResubmitAppl == null) {
							FacesContext fc = FacesContext.getCurrentInstance();
							MofRegistrationReqDTO mofReqDto = new MofRegistrationReqDTO();
							mofReqDto.setApplId(Long.parseLong(applId));
							mofReqDto.setSmSupplier(supplierProfile);
							MofRegistrationResDTO mofRespDto = mofRegistrationSO
									.cloneSmApplicationForResubmissionDraft(mofReqDto);
							SmAppl clnAppl = mofRespDto.getSmAppl();
							SmSupplier supp = mofRespDto.getSmSupplier();

							if (!clnAppl.getApplType().equalsIgnoreCase(
									ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
								QuestionnaireBackingBean quesBackingBean = new QuestionnaireBackingBean();
								quesBackingBean.getManagedBean().setTargetUrl(
										xhtml);
								quesBackingBean.getManagedBean().setSmSupp(
										supplierProfile);
								quesBackingBean.getManagedBean().setSmAppl(
										clnAppl);
								quesBackingBean.getManagedBean()
										.setRejectedAppl(rejectedAppl);
								quesBackingBean.retrieveQuestionnaire();
								fc.getApplication()
										.getNavigationHandler()
										.handleNavigation(fc, null,
												"/xhtml/sm/questionnaire/questionnaireView.xhtml");
								getManagedBean().setSmAppl(clnAppl);
								getManagedBean().setSmSupplier(supp);
							} else {
								getManagedBean().setSmAppl(clnAppl);
								getManagedBean().setSmSupplier(supp);
								prerenderApplicationForResubmission();
							}
						} else {
							getManagedBean().setSmAppl(latestResubmitAppl);
							getManagedBean().setSmSupplier(supplierProfile);
							prerenderApplicationForResubmission();
						}
					}
				} else {
					getManagedBean().setResubmitInValidMsg(
							SmCommonUtil.getMessage("lbl.sm.activeDrftExist",
									null));
					this.getManagedBean().setValidResubmit(false);

				}

			} else {
				getManagedBean().setResubmitInValidMsg(
						SmCommonUtil.getMessage("lbl.sm.resubmission.invalid",
								null));
				// redirect & display resubmit expired
				this.getManagedBean().setValidResubmit(false);
			}
		} finally {
			mofRegistrationSO = null;
		}

	}

	/**
	 * Pre-render application for re-submission after finishing the answer
	 * questionnaire
	 */
	public void prerenderApplicationForResubmission() {
		log.info("**** Prerender Application *************");
		SmAppl appl = getManagedBean().getSmAppl();
		SmSupplier supplierProfile = getManagedBean().getSmSupplier();

		try {
			if (appl != null && appl.getApplId() != null) {
				MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
				mofResDto = SmApplicationUtil.retriveApplicationById(appl
						.getApplId().intValue());
				appl = mofResDto.getSmAppl();
				supplierProfile = mofResDto.getSmSupplier();
			}
		} finally {
		}

		if (appl != null && supplierProfile != null) {
			if (appl.getSmApplSupportDocs() != null
					&& !appl.getSmApplSupportDocs().isEmpty()) {
				appl.getSmApplSupportDocs().clear();
				appl.setSmApplSupportDocs(new ArrayList<SmApplSupportDoc>());
			}

			appl.setSdoReceivedDate(null);
			proceedToApplication(appl.getApplType(), Boolean.TRUE, appl,
					supplierProfile);
			getManagedBean().getSmAppl().setResubmit(true);
			Calendar c = new GregorianCalendar();
			c.add(Calendar.DATE, ruleHelper.getApplicationSubmissionDays());
			Date d = c.getTime();
			getManagedBean().getSmAppl().setDraftExpiryDate(d);
			if (getManagedBean().getSmAppl().getSmApplSectionReviews() != null
					&& !getManagedBean().getSmAppl().getSmApplSectionReviews()
							.isEmpty()) {
				getManagedBean().getSmAppl().getSmApplSectionReviews().clear();

			}
			if (getManagedBean().getSmAppl().getRecordStatusFlg()
					.compareTo(DbValueConst.RECORD_STATUS_DELETED) == 0) {
				getManagedBean().getSmAppl().setRecordStatusFlg(
						DbValueConst.RECORD_STATUS_ACTIVE);
			}
			getManagedBean().getSmAppl().setQuestionnaire(false);
			if (getManagedBean().getSmAppl().getSmSvTasks() != null
					&& !getManagedBean().getSmAppl().getSmSvTasks().isEmpty()) {
				for (SmSvTask task : getManagedBean().getSmAppl()
						.getSmSvTasks()) {
					task.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
				}
			}
			if (getManagedBean().getSmAppl().getSmSupplierCategories() != null
					&& !getManagedBean().getSmAppl().getSmSupplierCategories()
							.isEmpty()) {
				for (SmSupplierCategory cat : getManagedBean().getSmAppl()
						.getSmSupplierCategories()) {
					if (cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
						if (cat.getPoDecFlg() != null) {
							cat.setPrevApprovedByPo(cat.isApprovedByPo());
							cat.setPrevPoDecFlg(cat.getPoDecFlg());
							cat.setPoDecFlg(null);
						}
						if (!StringUtils.isEmpty(cat.getPoRemarks())) {
							cat.setPrevPoRemarks(cat.getPoRemarks());
							cat.setPoRemarks(null);
						}
						if (cat.getApprvrDecFlg() != null) {
							cat.setPrevApprovedByAp(cat.isApprovedByAp());
							cat.setPrevApprDecFlg(cat.getApprvrDecFlg());
							cat.setApprvrDecFlg(null);
						}
						if (!StringUtils.isEmpty(cat.getApproverRemarks())) {
							cat.setPrevApproverRemarks(cat.getApproverRemarks());
							cat.setApproverRemarks(null);
						}

					}

				}
			}

			setNavigationForResubmitAppln(appl.getApplType());
		} else {
			// redirect & display resubmit expired
			this.getManagedBean().setValidResubmit(false);
		}

	}

	/**
	 * Process draft application from BPM.
	 * 
	 * @param applicationType
	 *            - the application type
	 * @param fromBpm
	 *            - boolean true ,if from task list and false , if application
	 *            from screen
	 * @param appl
	 *            - SmAppl object
	 * @param supp
	 *            - SmSupplier object
	 * 
	 */
	public void processDraftApplication(String applicationType,
			boolean fromBpm, SmAppl appl, SmSupplier supp) {
		proceedToApplication(applicationType, fromBpm, appl, supp);

	}

	/**
	 * Proceed to application UI , click on proceed.
	 * 
	 * @param applicationType
	 *            - the application type
	 * @return the the redirect page
	 */
	public String proceedToApplication(String applicationType) {
		return proceedToApplication(applicationType, Boolean.FALSE, null, null);
	}

	/**
	 * Gets the max date.
	 * 
	 * @return the max date
	 */
	public String getMaxDate() {
		DateFormat df = new SimpleDateFormat();
		log.info("*** Date String *** " + df.format(new Date()));
		return df.format(new Date());
	}

	/**
	 * Proceed to application from UI and also BPM task list.
	 * 
	 * @param applicationType
	 *            - the application type
	 * @param fromBpm
	 *            - boolean true ,if from task list and false , if application
	 *            from screen
	 * @param appl
	 *            - SmAppl object
	 * @param supp
	 *            - SmSupplier object
	 * @return the redirect page
	 */
	public String proceedToApplication(String applicationType, boolean fromBpm,
			SmAppl appl, SmSupplier supp) {
		getManagedBean().setRedirectToPersonPage(false);
		assignValReqdToFalse();
		if (!applicationType
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			log.info("*** Today ****" + getManagedBean().getToday());
			getManagedBean().setCountryLs(
					SmApplicationUtil.retrievePmCountryList());
			getManagedBean().setCountryItems(SmCommonUtil.getCountryList());
			getManagedBean().setProfBodyLs(
					SmApplicationUtil.retrievePmProfBodyList());

			getManagedBean().setUomLs(SmApplicationUtil.retrieveUomList());
			getManagedBean()
					.setReasonDescList(
							SmApplicationUtil
									.retrieveRejectReasonListByParameterCode(ReasonCategory.SUPPLIER_REJECTION_REASON
											.value()));
			getManagedBean()
					.setRejectionOtherReasonId(
							SmApplicationUtil
									.retrieveOtherRejectReasonIdByParamCodeAndType(
											ReasonCategory.SUPPLIER_REJECTION_REASON
													.value(),
											SupplierManagementConst.SM_REASON_CODE_OTHERS));
			getManagedBean().setEduFieldList(
					SmApplicationUtil.retrieveEduFieldList());
			getManagedBean().setRacesList(
					SmApplicationUtil.retrievePmRaceList());

			getManagedBean().setDiplomaEduLevel(
					SmApplicationUtil.retrievePmEduLevelByCode("80"));
			getManagedBean().setCertEduLevel(
					SmApplicationUtil.retrievePmEduLevelByCode("60"));
			getManagedBean().setGlpCert(
					SmApplicationUtil.retrievePmParameterByTypeAndCode(
							SupplierManagementConst.SM_SUPPLIER_PARAM_TYPE_CRT,
							SupplierManagementConst.SM_SUPPLIER_CERT_TYPE_GLP));
			getManagedBean().setAcadQualifications(
					SmApplicationUtil
							.getAcademicQualifications(getManagedBean()));
		}

		getManagedBean()
				.setDefPmCountry(
						SmCommonUtil
								.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE));
		SmAppl smAppl = new SmAppl();
		SmSupplier smApplSupplier = new SmSupplier();

		MofRegistrationResDTO resDto = new MofRegistrationResDTO();

		if (!fromBpm) {
			SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();

			boolean cannotProceedAlertMsg = false;

			if (applicationType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				boolean hasActiveTransactions = SmApplicationUtil
						.hasActiveTransactions(currSupp);
				if (hasActiveTransactions) {
					throw new EpException(MessageConst.ERROR_722565);
				}
			} else {

			}
			cannotProceedAlertMsg = SmApplicationUtil
					.checkActiveSmApplForSupp(currSupp);
			if (cannotProceedAlertMsg) {
				throw new EpException(MessageConst.ERROR_722542);
			}

			if (applicationType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {

				// individual registration type cant proceed to upgrade to
				// MOF
				if (currSupp != null
						&& currSupp
								.getBusinessType()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL)) {
					throw new EpException(MessageConst.ERROR_722607);
				}
				// government seller type cant proceed to upgrade to
				// MOF
				if (currSupp != null
						&& currSupp
								.getBusinessType()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER)) {
					throw new EpException(MessageConst.ERROR_722628);
				}
				if (getManagedBean().getNewSupplierType() == null
						|| StringUtils.isBlank(getManagedBean()
								.getNewSupplierType())) {
					getManagedBean().setNewSupplierType("K");
				}
				smAppl.setSupplierType(getManagedBean().getNewSupplierType());
			}
			// else if (applicationType.equalsIgnoreCase("B")) {}
			else if (applicationType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
					|| applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
					|| applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
					|| applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)) {
				cannotProceedAlertMsg = SmApplicationUtil
						.validateBeforeProceed(currSupp.getSupplierId(),
								applicationType);
			}
			if (cannotProceedAlertMsg) {
				return SmApplicationUtil
						.setExceptionNavigationForAppl(applicationType);
			} else {
				log.info("****** Before Clone supp id *****"
						+ currSupp.getSupplierId());
				resDto = cloneSmApplicationDraft(currSupp.getSupplierId(),
						applicationType, smAppl.getSupplierType());
				if (resDto.isErrorCaued()) {
					return SmApplicationUtil
							.setExceptionNavigationForAppl(applicationType);

				}
				smApplSupplier = resDto.getSmSupplier();
				log.info(" *** After clone Supplier Id ******* "
						+ smApplSupplier.getSupplierId());
				log.info("**** After clone appl id *** "
						+ resDto.getSmAppl().getApplId() + " -- "
						+ resDto.getSmAppl().getApplNo());
				if (resDto.isErrorCaued()) {
					return SmApplicationUtil
							.setExceptionNavigationForAppl(applicationType);
				}
				smAppl = resDto.getSmAppl();
				smAppl.setResubmit(false);
				smAppl.setSupportingDocMode(SupplierManagementConst.SUPPORTING_DOC_ONLINE);
				if (smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
					if (smAppl.getSmCompanyBasic() != null) {
						smAppl.getSmCompanyBasic().setAnnualRevenue(null);
					}
				}
				log.info("****** After retrieve the appl is ********"
						+ smAppl.getApplId() + " *** Num ***"
						+ smAppl.getApplNo() + "*** Bank Size *** "
						+ smAppl.getSmSupplierBanks().size());
			}
		} else {
			log.info("******* This is PO or Approver from BPM ************");
			smApplSupplier = supp;
			smAppl = appl;
		}
		getManagedBean().setSmSupplier(smApplSupplier);
		if (applicationType
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			getManagedBean()
					.setDisableWithFedralTransaction(
							SmApplicationUtil
									.isTransactWithBasicAccountApprovedSoftCert(getManagedBean()
											.getSmSupplier().getSmAppl()));
		}
		if (!applicationType
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				&& !applicationType
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)
				&& !applicationType
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			SmApplicationUtil.checkEquityOwnerUpdateAllowance(getManagedBean());
		}
		retrieveBusinessTypeList();
		assignSupplierBusinessType(getManagedBean().getSmSupplier());

		if (getManagedBean().isApproverUser() || getManagedBean().isPoUser()) {
			if (smAppl.getSmCompanyBasic() == null) {
				smAppl.setSmCompanyBasic(new SmCompanyBasic());
			}
		}

		if (smAppl.getSmCompanyBasic() != null) {
			if (smAppl.getSmCompanyBasic().getSmCompanyAddresses() != null
					&& !smAppl.getSmCompanyBasic().getSmCompanyAddresses()
							.isEmpty()) {
				SmCompanyAddress comAdd = smAppl.getSmCompanyBasic()
						.getSmCompanyAddresses().get(0);
				SmAddress addr = comAdd.getSmAddress();
				SmApplicationUtil.setPmStateDropdownValuesForBasicInfo(this
						.getManagedBean(), addr.getPmCountry().getCountryId());
				this.getManagedBean().setBasicCompanyDistricts(
						SmCommonUtil.getDistrictList(addr.getPmState()
								.getStateId()));
				getManagedBean().setPrevBasicStateId(
						addr.getPmState().getStateId());
				this.getManagedBean().setBasicCompanyCities(
						SmCommonUtil.getCityList(addr.getPmDistrict()
								.getDistrictId()));
			} else {
				SmCompanyAddress comAdd = new SmCompanyAddress();
				SmAddress addr = SmApplicationUtil
						.setDefaultAddr(getManagedBean().getDefPmCountry());
				comAdd.setSmAddress(addr);
				List<SmCompanyAddress> addrs = new ArrayList<SmCompanyAddress>();
				addrs.add(comAdd);
				smAppl.getSmCompanyBasic().setSmCompanyAddresses(addrs);
			}
		} else {
			log.info("******** My Company Basic is null ********");
		}

		getManagedBean().setSmAppl(smAppl);

		if (fromBpm) {
			SmApplicationUtil.getInstance().setApplAddressTransientValue(
					getManagedBean().getSmAppl(), false);
			SmApplicationUtil.checkOverLappingCategories(getManagedBean());
			if (!(getManagedBean().isApproverUser() || getManagedBean()
					.isPoUser())) {
				boolean updateNeeded = SmApplicationUtil
						.resetDeactivatedPersonnel(getManagedBean());
				if (updateNeeded)
					saveOrUpdateAppl();
			}
		}

		if (getManagedBean().getSmAppl().getSmStaffTotals().isEmpty()) {
			log.info("*** Empty list of staff *****" + smAppl.getSupplierType());
			getManagedBean().getSmAppl().setSmStaffTotals(
					SmApplicationUtil.getStaffList(smAppl));
		}

		getManagedBean().setTabActiveIndex(0);
		if (!fromBpm) {
			setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_DECLARATION);

			if (getManagedBean().getSmAppl().getSupplierType()
					.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setWithFederal(true);
			}

			// SmApplicationUtil.getInstance().setApplAddressTransientValue(
			// getManagedBean().getSmAppl(), false);

		} else {
			if (!StringUtils.isEmpty(getManagedBean().getCurrentStatus())) {
				log.info("*** Current Status Value Retrieved from BPM (set in common appl backing bean)****** "
						+ getManagedBean().getCurrentStatus());
			} else {
				SmWorkflowStatus sts = SmCommonUtil
						.getCurrentActiveWokFlowStatus(smAppl.getApplId());
				if (sts != null && sts.getPmStatus() != null
						&& sts.getPmStatus().getStatusId() != null) {
					log.info("** PM Status retrived from work flow *******");
					setPmStatusDesc(sts.getPmStatus().getStatusId().toString());
					log.info("*** Status ******* "
							+ getManagedBean().getCurrentStatus());
				} else {
					log.info("** PM Status retrived from SM appl only *******");
					setPmStatusDesc(smAppl.getPmStatus().getStatusId()
							.toString());
					log.info("*** SM appl  Status ******* "
							+ getManagedBean().getCurrentStatus());
				}
			}
		}
		if (fromBpm
				&& (getManagedBean().isPoUser() || getManagedBean()
						.isApproverUser())) {
			setRevUrl();
			getManagedBean().setMaxReviewCount(
					assignMaxReviewCount(applicationType, getManagedBean()
							.getSmAppl().getApplCategoryType(),
							getManagedBean().getSmAppl().getSupplierType(),
							getManagedBean().getBusinessTypeVal()));
			if (getManagedBean().getSmAppl().getSmApplSectionReviews() == null) {
				smAppl.setSmApplSectionReviews(new ArrayList<SmApplSectionReview>());
			}

			if (getManagedBean().isPoUser()) {
				setDefaultPoReviewSection();
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_VERIFICATION);
			}

			if (getManagedBean().isApproverUser()) {
				setDefaultApproverReviewSection();
			}

			if (applicationType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				getManagedBean().setSupplierRemarks(
						SmApplicationUtil.getSmRemarksByDocTypeAndId(
								applicationType, getManagedBean().getSmAppl()
										.getApplId()));
			}

			// currently is display for ApplType C/DPA
			getManagedBean().setBasicInfo(
					SmApplicationUtil.getSmSupplierBasicInfoVO(getManagedBean()
							.getBasicInfo(), getManagedBean().getSmSupplier(),
							getManagedBean().getSmAppl()));
		}

		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			getManagedBean().getSmAppl().setApplCategoryType("N");
		} else if (StringUtils.isEmpty(getManagedBean().getSmAppl()
				.getApplCategoryType())) {
			getManagedBean().getSmAppl().setApplCategoryType("N");
		}
		getManagedBean().setCurrentPageType(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC);
		if (SmCommonUtil.isCollectionEmpty(smAppl.getSmFacilities())
				&& !smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
			SmFacility fac = new SmFacility();
			if (smAppl.getApplType().equalsIgnoreCase(
					ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				fac.setFacilityType("H");
			}
			if (fac.getSmAddress() == null
					|| StringUtils.isBlank(fac.getSmAddress().getAddress1())) {
				fac.setSmAddress(new SmAddress());
				SmApplicationUtil.getInstance().cloneAddress(
						fac.getSmAddress(),
						getManagedBean().getSmAppl().getSmCompanyBasic()
								.getSmCompanyAddresses().get(0).getSmAddress());
			}
			fac.setFacilityName(getManagedBean().getSmAppl()
					.getSmCompanyBasic().getCompanyName());
			fac.setSmAppl(smAppl);
			fac.setPurchasePrice(new BigDecimal(0.00));
			fac.setRentalRate(new BigDecimal(0.00));
			fac.setRevNo(1);
			smAppl.getSmFacilities().add(fac);
			this.getManagedBean().setBasicCompanyFacility(fac);
			SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
					getManagedBean(), fac.getSmAddress(),
					SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL);

		} else {
			setApplicationFailities(smAppl.getSmFacilities());
		}
		if (smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			SmApplicationUtil.setCancellationSmAttachment(getManagedBean());
			// set basic info for cancel mof
		}

		// set previous approved personnels
		getManagedBean()
				.setPrevApprovedPersonnels(
						SmApplicationUtil
								.getPrevApprovedPersonnelDirectorOwner(smAppl));

		// set application header info
		getManagedBean().setApplStatusInfo(
				SmApplicationUtil.getSmApplStatusInfoVO(smAppl));

		// set whether allowed to edit company name
		getManagedBean().setDisabledCompanyName(
				SmApplicationUtil.isDisableCompanyNameInAppl(getManagedBean()
						.getSmSupplier(), getManagedBean().getSmAppl()));

		// set whether allowed to edit business type
		getManagedBean().setDisabledBusinessType(
				SmApplicationUtil.isDisableBusinessTypeInAppl(getManagedBean()
						.getSmSupplier(), getManagedBean().getSmAppl()));

		// set whether allowed to change business type from office to
		// business
		setBusinessAndHadApprovedSpecialCatcode();

		// set travelling software select items
		getManagedBean().setTravellingSoftwareItems(
				SmCommonUtil.getInstance().getTravellingSoftwareItems());

		getManagedBean().setRegStatusDescList(
				SmApplicationUtil.getInstance().retrieveRegistrationStatusList(
						getManagedBean().getSmAppl().getSupplierType(),
						applicationType));

		getManagedBean()
				.setSupplierParentCatList(
						SmApplicationUtil
								.retrieveParentCategoryListForExperiencePage(getManagedBean()));
		if (getManagedBean().getSmAppl().getSmAssociationBodies() != null) {
			SmApplicationUtil.assignNonPersonnelProfBodyList(getManagedBean());
		}
		List<SmMofAccount> accounts = getManagedBean().getSmSupplier()
				.getSmMofAccounts();
		if (accounts != null && !accounts.isEmpty()) {
			for (SmMofAccount account : accounts) {
				if (account.getSupplierType().equalsIgnoreCase(
						getManagedBean().getSmSupplier().getSmAppl()
								.getSupplierType())
						&& account.getRecordStatusFlg().compareTo(
								DbValueConst.RECORD_STATUS_ACTIVE) == 0) {
					getManagedBean().setMofNumber(account.getMofNo());
				}
			}
		}

		// init GST information
		initGst();

		// init to show change value compare to previous application
		SmFieldChangeUtil.initLatestAppl(getManagedBean());

		// set softcert reject reason
		setPersonnelSoftcertRejectReason();

		return SmApplicationUtil.setNavigationForAppl(applicationType);

	}

	/**
	 * Delete pending mof application.
	 * 
	 * @return String - output page to redirect
	 */
	public String deletePendingMofApplication() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			SmSupplier smSupplier = SmApplicationUtil.getCurrentSupplier();
			log.info("****** Testing the delete pending *********"
					+ smSupplier.getSupplierId());
			getManagedBean().setSmSupplier(smSupplier);
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSmSupplier(smSupplier);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrievePendingApplicationDetails(reqDto);
			if (resDTO.getSmAppl() == null) {
				throw new EpException(MessageConst.ERROR_722528);
			} else {
				SmAppl smAppl = SmApplicationUtil.defaultSmAppl(resDTO
						.getSmAppl());
				if (smAppl.getApplType() != null
						&& smAppl.getApplType().equalsIgnoreCase(
								ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
					log.info("*** This is cancellation application *****");
					throw new EpException(MessageConst.ERROR_722581);
				}
				getManagedBean().setSmAppl(smAppl);
				SmWorkflowStatus sts = SmCommonUtil
						.getCurrentActiveWokFlowStatus(getManagedBean()
								.getSmAppl().getApplId());
				if (sts != null && sts.getPmStatus() != null) {
					if (sts.getPmStatus()
							.getStatusId()
							.compareTo(
									SupplierManagementConst.PM_STATUS_PENDING_APPL_DECLARATION) == 0) {
						log.info("*** This is draft application *****");
						throw new EpException(MessageConst.ERROR_722569);
					} else if (sts
							.getPmStatus()
							.getStatusId()
							.compareTo(
									SupplierManagementConst.PM_STATUS_PENDING_PAYMENT) == 0) {
						log.info("*** This is application with pending payment *****");
						throw new EpException(MessageConst.ERROR_722577);
					} else if (sts
							.getPmStatus()
							.getStatusId()
							.compareTo(
									SupplierManagementConst.PM_STATUS_PENDING_REVIEW_CANCELLATION) == 0) {
						log.info("*** This is duplicate request for the delete pending *****");
						throw new EpException(MessageConst.ERROR_722582,
								new String[] { smAppl.getApplNo() });
					}
					setPmStatusDesc(sts.getPmStatus().getStatusId().toString());
				}
			}
			getManagedBean()
					.setReasonDescList(
							SmApplicationUtil
									.retrieveRejectReasonListByParameterCode(ReasonCategory.DELETE_PENDING_APPLICATION_REASON
											.value()));
			getManagedBean()
					.setRejectionOtherReasonId(
							SmApplicationUtil
									.retrieveOtherRejectReasonIdByParamCodeAndType(
											ReasonCategory.DELETE_PENDING_APPLICATION_REASON
													.value(),
											SupplierManagementConst.SM_REASON_CODE_OTHER));
			return SupplierManagementConst.PROCEED_DEL_PENDING_MOF_APPLN;

		} finally {
			mofRegistrationSO = null;
		}
	}

	/**
	 * To open the confirmation dialog box for cancel registration with MOF
	 */
	public String onDisplayConfirmCanceMofRegistration() {
		String page = null;
		Locale locale = new FacesScopes().getUserLocale();
		StringBuffer sb = new StringBuffer();
		boolean valid = true;
		if (getManagedBean().getSupplierRemarks() == null
				|| getManagedBean().getSupplierRemarks().trim() == "") {
			sb.append(ResourceBundleUtils.getMessage(
					"lbl.sm.mof.reg.reason.delete",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			sb.append(" is mandatory </br>");
			valid = false;
		}
		if (getManagedBean().getCancellationAttachment().getFileName() == null
				|| getManagedBean().getCancellationAttachment().getFileName()
						.trim() == "") {
			sb.append(ResourceBundleUtils.getMessage("lbl.sm.suppdoc.attach",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			sb.append(" is mandatory ");
			valid = false;
		}
		if (!valid) {
			throw new EpException(sb.toString());
		} else {
			page = submitCancelMofApplToBpm();
		}
		return page;
	}

	public void onDisplaySpkiPopForSubmitAppl() {
		boolean valid = false;
		if (getManagedBean().getSmAppl().getSupportingDocMode() != null
				&& getManagedBean().getSmAppl().getSupportingDocMode()
						.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			valid = validateSupportingDocs();
		} else {
			valid = true;
		}
		if (valid) {
			getManagedBean().getSpkiBackingBean().getChallengeQuestions();
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
		} else {
			log.info("*** Invalid Online supporting docs validataion **** ");
		}
	}

	/**
	 * To open the confirmation dialog box for deleting pending application
	 */
	public void onDisplayConfirmDelPending() {
		RequestContext.getCurrentInstance().execute("delPenRegConfirm.show()");
	}

	/**
	 * Update sm appl.To set the navigation tab counts and values.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void updateSmAppl() throws Exception {
		try {
			log.info("**** ENTER ACTION METHOD BEAN *************" + currentTab
					+ "  " + outputTab);

			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				log.info(" ****** REMAIN PREV VALUE ********");
			}
			getManagedBean().setTabActiveIndex(outputTab);
			log.info("Value ----"
					+ getManagedBean().getSmAppl().getSupportingDocMode());

		} catch (Exception e) {
			getManagedBean().setTabActiveIndex(currentTab);
			throw new EpException(MessageConst.ERROR_722505);
		}

	}

	/**
	 * Validate and Update sm application.Mainly called when navigate from one
	 * page to other or save as draft i.e. triggred from task list
	 * 
	 * @param applType
	 *            - the appl type
	 * @param currentPageType
	 *            - the current page type
	 * @param outputPageType
	 *            - the output page type
	 * @param isBpmSubmit
	 *            - is from task list
	 * @param isBpmSubmit
	 *            the is bpm submit
	 */
	public void updateSmApplBpmSubmit(String applType, String currentPageType,
			String outputPageType, boolean isBpmSubmit) {
		validateUpdateSmAppl(applType, currentPageType, outputPageType,
				isBpmSubmit);
	}

	/**
	 * Validate and Update sm application.Mainly called when navigate from one
	 * page to other or save as draft
	 * 
	 * @param applType
	 *            - the appl type
	 * @param currentPageType
	 *            - the current page type
	 * @param outputPageType
	 *            - the output page type
	 * 
	 */
	public void updateSmAppl(String applType, String currentPageType,
			String outputPageType) {
		if ((getManagedBean().isApproverUser() || getManagedBean().isPoUser())
				&& currentPageType
						.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
			for (SmSupplierCategory cat : getManagedBean()
					.getActiveCategoryList()) {
				log.info(" ***** Cat code is ****** " + cat.getPoDecFlg());
				for (SmSupplierCategory parentCat : getManagedBean()
						.getSmAppl().getSmSupplierCategories()) {
					if (parentCat.getSupplierCategoryId().equals(
							cat.getSupplierCategoryId())) {
						getManagedBean()
								.getSmAppl()
								.getSmSupplierCategories()
								.set(getManagedBean().getSmAppl()
										.getSmSupplierCategories()
										.indexOf(parentCat), cat);
						break;
					}
				}

			}
		} else if (currentPageType
				.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
			SmApplicationUtil.validateCategoryOtherDesc(true, getManagedBean(),
					currentTab);
		}

		if (currentPageType
				.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_PODEC)
				|| currentPageType
						.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_APPRDEC)) {
			retriveCurrentAppl();
		} else if (getManagedBean().getSmAppl().getSmApplRejectReasons() != null
				&& !getManagedBean().getSmAppl().getSmApplRejectReasons()
						.isEmpty()) {
			log.info("*** Clear the rejection list ******* ");
			getManagedBean().getSmAppl().getSmApplRejectReasons().clear();
		} else if (currentPageType
				.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS)
				&& getManagedBean().isSupplierUser()) {
			if (getManagedBean().getSmAppl().getSupportingDocMode() != null
					&& getManagedBean()
							.getSmAppl()
							.getSupportingDocMode()
							.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)
					&& (currentTab < outputTab)) {
				validateSupportingDocs();
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses() != null
					&& !getManagedBean().getSmAppl().getSmCompanyBasic()
							.getSmCompanyAddresses().isEmpty()) {
				SmCompanyAddress comAdd = getManagedBean().getSmAppl()
						.getSmCompanyBasic().getSmCompanyAddresses().get(0);
				getManagedBean().setPrevBasicStateId(
						comAdd.getSmAddress().getPmState().getStateId());
			}
		} else if (currentPageType
				.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)
				&& getManagedBean().isSupplierUser()) {
			SmApplicationUtil.validateGSt(getManagedBean(), currentTab,
					ruleHelper);
		}
		validateUpdateSmAppl(applType, currentPageType, outputPageType, false);
		saveSupplierBusinesType(currentPageType);
		log.info("******* Current page AFTER in managed bean ********"
				+ getManagedBean().getCurrentPageType());
	}

	/**
	 * Update application and set spki value
	 * 
	 * @param PmDigiSign
	 *            pmDigiSign
	 * @param String
	 *            applType
	 * @param String
	 *            currentPageType
	 * @param String
	 *            outputPageType
	 */
	public void updateSmAppl(PmDigiSign pmDigiSign, String applType,
			String currentPageType, String outputPageType) {

		if (pmDigiSign != null) {
			updateSpkiDigiSignData(pmDigiSign, "SR", this.getManagedBean()
					.getSmAppl().getApplId(), this.getManagedBean().getSmAppl()
					.getApplNo(), prepareOriginalText());
			SpkiBackingBean spkiBackingBean = new SpkiBackingBean();
			byte[] signedData = spkiBackingBean
					.spkiSignTransaction(prepareOriginalText());
			log.info("signedData: " + signedData);

			pmDigiSign.setSpkiSignedData(signedData);
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		updateSmAppl(applType, currentPageType, outputPageType);
	}

	/**
	 * Get cert type name
	 * 
	 * @param Long
	 *            certTypeId
	 * @return String
	 */
	public String getCertTypeLabelById(Long certTypeId) {
		String retVal = "";
		if (certTypeId != null) {
			for (SelectItem item : Arrays.asList(getDropdownValues("CRT"))) {
				if (item.getValue() != null
						&& item.getValue().toString()
								.equals(certTypeId.toString())) {
					retVal = item.getLabel();
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * Get country label by country Id
	 * 
	 * @param Long
	 *            countryId
	 * @return String
	 */
	public String getCountryLabelById(Long countryId) {
		return SmCommonUtil.getCountryLabelById(countryId);
	}

	/**
	 * Get state label by state Id
	 * 
	 * @param Long
	 *            stateId
	 * @return String
	 */
	public String getStateLabelById(Long stateId) {
		return SmCommonUtil.getStateLabelById(stateId);
	}

	/**
	 * Get district label by district Id
	 * 
	 * @param Long
	 *            districtId
	 * @return String
	 */
	public String getDistrictLabelById(Long districtId) {
		return SmCommonUtil.getDistrictLabelById(districtId);
	}

	/**
	 * Get city label by city Id
	 * 
	 * @param Long
	 *            cityId
	 * @return String
	 */
	public String getCityLabelById(Long cityId) {
		return SmCommonUtil.getCityLabelById(cityId);
	}

	/**
	 * Get annual revenue desciption by id
	 * 
	 * @param Long
	 *            annualRevenueId
	 * @return String
	 */
	public String getAnnualRevById(Long annualRevenueId) {
		String name = "";
		if (annualRevenueId != null) {
			for (SelectItem item : Arrays.asList(getDropdownValues("SAR"))) {
				if (item.getValue() != null
						&& item.getValue().toString()
								.equals(annualRevenueId.toString())) {
					name = item.getLabel();
					break;
				}
			}
		}
		return name;
	}

	/**
	 * Get staff total by category
	 * 
	 * @param String
	 *            staffCategory
	 * @return SmStaffTotal
	 */
	public SmStaffTotal getStaffTotalByCategory(String staffCategory) {
		return SmFieldChangeUtil.getStaffTotalByCategoryFromList(
				getManagedBean().getLatestAppl(), staffCategory);
	}

	/**
	 * Get ownership type label by type
	 * 
	 * @param String
	 *            ownershipType
	 * @return String
	 */
	public String getOwnershipTypeLabel(String ownershipType) {
		return SmCommonUtil.getOwnershipTypeLabel(ownershipType);
	}

	/**
	 * Get factory type label by type
	 * 
	 * @param String
	 *            facType
	 * @return String
	 */
	public String getFacTypeLabel(String facType) {
		return SmCommonUtil.getFacTypeLabel(facType);
	}

	/**
	 * Get race by Id
	 * 
	 * @param Long
	 *            raceId
	 * @return String
	 */
	public String getRaceLabel(Long raceId) {
		return SmCommonUtil.getRaceByIdFromSelectItems(raceId);
	}

	/**
	 * Get professional body from session list by profBodyId
	 * 
	 * @param Long
	 *            profBodyId
	 * @return String
	 */
	public String getProfBodyByIdFromList(Long profBodyId) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getProfBodyLs())
				&& profBodyId != null) {
			for (PmCertBody body : getManagedBean().getProfBodyLs()) {
				if (body.getCertBodyId().compareTo(profBodyId) == 0) {
					retVal = body.getBodyName();
				}
			}
		}
		return retVal;
	}

	/**
	 * Get education level from session list by eduLvlId
	 * 
	 * @param Long
	 *            eduLvlId
	 * @return String
	 */
	public String getEduLvlByIdFromList(Long eduLvlId) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getEduLevelList())
				&& eduLvlId != null) {
			for (PmEduLevel eduLvl : getManagedBean().getEduLevelList()) {
				if (eduLvl.getEduLevelId().compareTo(eduLvlId) == 0) {
					retVal = eduLvl.getLevelDesc();
				}
			}
		}
		return retVal;
	}

	/**
	 * Get education field from session list by eduFieldId
	 * 
	 * @param Long
	 *            eduFieldId
	 * @return String
	 */
	public String getEduFieldByIdFromList(Long eduFieldId) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getEduFieldList())
				&& eduFieldId != null) {
			for (PmEduField eduField : getManagedBean().getEduFieldList()) {
				if (eduField.getEduFieldId().compareTo(eduFieldId) == 0) {
					retVal = eduField.getFieldName();
				}
			}
		}
		return retVal;
	}

	/**
	 * Get financial organization from session list by finOrgId
	 * 
	 * @param Long
	 *            finOrgId
	 * @return String
	 */
	public String getFinOrgByIdFromList(Long finOrgId) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getFinOrgLs())
				&& finOrgId != null) {
			for (PmFinancialOrg finOrg : getManagedBean().getFinOrgLs()) {
				if (finOrg.getFinancialOrgId().compareTo(finOrgId) == 0) {
					retVal = finOrg.getFinOrgName();
				}
			}
		}
		return retVal;
	}

	/**
	 * Get bank account purpose label by accPurpose
	 * 
	 * @param String
	 *            accPurpose
	 * @return String
	 */
	public String getBankAccPurposeLabel(String accPurpose) {
		String retVal = "";
		if (accPurpose.equals("R")) {
			retVal = SmCommonUtil.getMessage("lbl.sm.bankDtls.registration",
					null);
		} else if (accPurpose.equals("P")) {
			retVal = SmCommonUtil.getMessage("lbl.sm.paymentLbl", null);
		} else {
			retVal = SmCommonUtil.getMessage("lbl.sm.bankDtls.both.regandpay",
					null);
		}
		return retVal;
	}

	/**
	 * Get price amount format to display from BigDecimal object type
	 * 
	 * @param BigDecimal
	 *            priceAmount
	 * @return BigDecimal
	 */
	public String getPriceAmountFormat(BigDecimal priceAmount) {
		return SmCommonUtil.getPriceAmountFormat(priceAmount);
	}

	/**
	 * Get date format to display from Date object type
	 * 
	 * @param Date
	 *            date
	 * @return String
	 */
	public String getDateFormat(Date date) {
		return SmCommonUtil.getDisplayDateFormat(date);
	}

	/**
	 * Get select item label from select items by select items value
	 * 
	 * @param Object
	 *            objId
	 * @param String
	 *            paramType
	 * @return String
	 */
	public String getLabelFromSelectItems(Object objId, String paramType) {
		String retVal = "";
		List<SelectItem> items = Arrays.asList(getDropdownValues(paramType));
		if (!SmCommonUtil.isCollectionEmpty(items) && objId != null) {
			for (SelectItem item : items) {
				if (item.getValue() != null
						&& item.getValue().toString().equals(objId.toString())) {
					retVal = item.getLabel();
				}
			}
		}
		return retVal;
	}

	/**
	 * Get select item label from select items by value
	 * 
	 * @param Object
	 *            objId
	 * @return String
	 */
	public String getLabelFromSelectItems(Object objId, List<SelectItem> items) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(items) && objId != null) {
			for (SelectItem item : items) {
				if (item.getValue() != null
						&& item.getValue().toString().equals(objId.toString())) {
					retVal = item.getLabel();
				}
			}
		}
		return retVal;
	}

	/**
	 * Update spki related data
	 * 
	 * @param PmDigiSign
	 *            pmDigiSign
	 * @param String
	 *            docType
	 * @param Long
	 *            docId
	 * @param String
	 *            docNumber
	 * @param String
	 *            textBeforeSign
	 */
	private void updateSpkiDigiSignData(PmDigiSign pmDigiSign, String docType,
			Long docId, String docNumber, String textBeforeSign) {
		FacesScopes facesScope = new FacesScopes();
		Long userId = facesScope.getUserSessionBean().getUserId();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		pmDigiSign.setDocId(new BigDecimal(docId));
		pmDigiSign.setDocNo(docNumber);
		pmDigiSign.setDocType(docType);
		pmDigiSign.setCreatedBy(userId);
		pmDigiSign.setCreatedDate(date);
		pmDigiSign.setOriginalData(textBeforeSign);
	}

	/**
	 * text to be signed for SM: applNo + userId (IC or password) + epNo
	 * 
	 * @return
	 */
	private String prepareOriginalText() {

		String applNo = this.getManagedBean().getSmAppl().getApplNo();
		String userIdentificationNo = new FacesScopes().getUserSessionBean()
				.getIdentificationNo();
		String epNo = this.getManagedBean().getSmSupplier().getEpNo();

		String originalText = applNo + "|" + userIdentificationNo + "|" + epNo;
		log.info("originalText: " + originalText);
		return originalText;
	}

	/**
	 * Validate and Update sm application.Mainly called when navigate from one
	 * page to other or save as draft
	 * 
	 * @param applType
	 *            - the appl type
	 * @param currentPageType
	 *            - the current page type
	 * @param outputPageType
	 *            - the output page type
	 * @param isBpmSubmit
	 *            - is from task list
	 * @return true, if successful
	 */
	public boolean validateUpdateSmAppl(String applType,
			String currentPageType, String outputPageType, Boolean isBpmSubmit) {
		boolean valid = false;
		log.info(" ***** Business  Type After hard code *********"
				+ getManagedBean().getSmAppl().getSmSupplier()
						.getBusinessType());
		getManagedBean().setTabActiveIndex(outputTab);
		try {

			log.info("******* IS PO *******" + getManagedBean().isPoUser());
			if (getManagedBean().isPoUser()
					|| getManagedBean().isApproverUser()) {
				setPoOrApprvrSectionReview();
			}

			log.info("****** Button type value ******** " + buttonType);
			if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)
					&& (buttonType
							.equalsIgnoreCase(SupplierManagementConst.SM_APPL_BUTTON_TYPE_PREV)
							|| buttonType
									.equalsIgnoreCase(SupplierManagementConst.SM_APPL_BUTTON_TYPE_SAME)
							|| currentTab > outputTab || currentTab == outputTab)) {
				valid = SmApplicationUtil.validateCategoryOtherDesc(true,
						getManagedBean(), currentTab);
			} else {
				valid = validateBeforeSave(applType, currentPageType);
			}

			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				log.info(" ****** REMAIN PREV VALUE ********");
			}
			if (valid) {
				saveOrUpdateAppl();
				getManagedBean().setCurrentPageType(outputPageType);

			} else {
				getManagedBean().setTabActiveIndex(currentTab);
				getManagedBean().getSmAppl().setCurrentActiveTab(currentTab);
				getManagedBean().setCurrentPageType(currentPageType);
			}
		}

		finally {
			if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)
					&& !valid) {
				saveOrUpdateAppl();

			} else if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)
					&& !valid) {
				saveOrUpdateAppl();
				calculateCapital();
				SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
						getManagedBean().getSmAppl(), getManagedBean()
								.getPrevApprovedPersonnels(), getManagedBean()
								.isDisAllowOwnerEdit());
				SmApplicationUtil.setPersonnelSoftcertRenewalStatus(this
						.getManagedBean().getSmSupplier(), this
						.getManagedBean().getSmAppl().getSmPersonnels());
			}
			if (getManagedBean().getCurrentPageType() != null) {
				switch (getManagedBean().getCurrentPageType()) {
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC:
					if (getManagedBean().getSmAppl().getSmAssociationBodies() != null) {
						SmApplicationUtil
								.assignNonPersonnelProfBodyList(getManagedBean());
					}
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS:
					if (getManagedBean().isSupplierUser()) {
						assignApplicationSupportingDocs();
					}
					if (getManagedBean().isPoUser()
							|| getManagedBean().isApproverUser() && !valid) {
						saveOrUpdateAppl();
					}
					displayBumiRecommendNote();
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL:
					calcPersonnelListCount();
					SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
							getManagedBean().getSmAppl(), getManagedBean()
									.getPrevApprovedPersonnels(),
							getManagedBean().isDisAllowOwnerEdit());
					SmApplicationUtil.setPersonnelSoftcertRenewalStatus(this
							.getManagedBean().getSmSupplier(), this
							.getManagedBean().getSmAppl().getSmPersonnels());
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY:
					if (valid) {
						SmApplicationUtil
								.assignActiveAndApprovedCategoryList(getManagedBean());
					}
					if (getManagedBean().getSmAppl().getApplCategoryType()
							.equalsIgnoreCase("N")) {
						getManagedBean()
								.setCatTypeConfirm(
										SmCommonUtil
												.getMessage(
														"lbl.sm.categorycode.note.normal",
														null));
					} else {
						getManagedBean().setCatTypeConfirm(
								SmCommonUtil.getMessage(
										"lbl.sm.categorycode.note.special",
										null));
					}
					SmCommonUtil
							.calculateSmApplicationPaidUpCapitalPercent(getManagedBean()
									.getSmAppl().getSmCompanyBasic());
					// API FOR LIST OF OVERLAPPING CATEGORIES
					SmApplicationUtil
							.checkOverLappingCategories(getManagedBean());
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY:
					if (valid) {
						log.info("*********OUPUT IS CAPITAL PAGE Values is capital *******");
						calculateCapital();
						SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
								getManagedBean().getSmAppl(), getManagedBean()
										.getPrevApprovedPersonnels(),
								getManagedBean().isDisAllowOwnerEdit());
						break;
					}
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_PODEC:
					SmApplicationUtil.checkAllowedToApprove(getManagedBean());
					displayBumiRecommendNote();
					assignApplicationRegistrationStatus(getManagedBean()
							.getSmAppl().getPmRegStatus());
					getHqFacility();
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_APPRDEC:
					SmApplicationUtil.checkAllowedToApprove(getManagedBean());
					displayBumiRecommendNote();
					assignApplicationRegistrationStatus(getManagedBean()
							.getSmAppl().getPmRegStatus());
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_SITEVISITREV:
					log.info("******* This is site visit review page ********");
					assignViewSiteVisitReport();
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY:
					log.info("******* This is facility for special category page ********");
					SmApplicationUtil
							.handleShowOrHideFacilityPanels(getManagedBean());
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP:
					SmApplicationUtil
							.showOrHideTravelSoftwarePanel(getManagedBean());
					break;
				default:
					break;
				}

				// init business premise whether show or hide home office
				initBusinessPremise();

				// to init page whether normal or special category type is
				// allowed
				initApplCategoryType();

				// init business premise whether show or hide home office
				initBusinessPremise();

			}
			assignValReqdToFalse();
		}
		return valid;
	}

	/**
	 * Retrieve latest application after update.
	 * 
	 * @param currentPageType
	 *            - the current page type
	 * @param outputPageType
	 *            - the output page type
	 * @return true, if successful
	 */
	private boolean retriveLatestApplAfterUpdate(String currentPageType,
			String outputPageType) {

		IMofRegistrationSO mofRegistrationSO = null;
		mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IMofRegistrationSO.class);
		try {

			MofRegistrationReqDTO finReqDto = new MofRegistrationReqDTO();
			finReqDto.setApplId(getManagedBean().getSmAppl().getApplId());
			log.info("********* Final Block Id *****" + finReqDto.getApplId());
			log.info("***** Its a valid ************");
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveCurrentDraftApplication(finReqDto);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			setApplicationFailities(getManagedBean().getSmAppl()
					.getSmFacilities());

			if (getManagedBean().getSmAppl().getSmPersonnels() != null
					&& !getManagedBean().getSmAppl().getSmPersonnels()
							.isEmpty()) {
				setApplicationShareHolders(getManagedBean().getSmAppl()
						.getSmPersonnels());
			}
			retrieveBusinessTypeList();
			assignSupplierBusinessType(getManagedBean().getSmAppl()
					.getSmSupplier());
			if (getManagedBean().isPoUser()) {
				setDefaultPoReviewSection();
			}
			if (getManagedBean().isApproverUser()) {
				setDefaultApproverReviewSection();
			}
			return true;
		} finally {
			mofRegistrationSO = null;
			// SmApplicationUtil.getInstance().setApplAddressTransientValue(
			// getManagedBean().getSmAppl(), false);

			// set application table field change status
			SmFieldChangeUtil.setApplTableListChangeStatus(getManagedBean()
					.getLatestAppl(), getManagedBean().getSmAppl());

			// init GST information
			initGst();
		}

	}

	/**
	 * Retrieve current application.
	 */
	private void retriveCurrentAppl() {
		IMofRegistrationSO mofRegistrationSO = null;
		mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IMofRegistrationSO.class);
		try {

			MofRegistrationReqDTO finReqDto = new MofRegistrationReqDTO();
			finReqDto.setApplId(getManagedBean().getSmAppl().getApplId());
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveCurrentDraftApplication(finReqDto);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
		} finally {
			mofRegistrationSO = null;
		}
	}

	/**
	 * Gets the personnel info by id.
	 * 
	 * @param personnelId
	 *            - Long ( personnel id)
	 * @return SmPersonnel
	 */
	public SmPersonnel getPersonnelInfoById(Long personnelId) {
		IPersonnelSO smPersonnelSO = null;
		smPersonnelSO = (IPersonnelSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IPersonnelSO.class);
		try {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setPersonnelId(Long.valueOf(personnelId));
			PersonnelResDTO resDTO = smPersonnelSO
					.retrievePersonnelById(reqDTO);
			return resDTO.getSmPersonnel();
		} finally {
			smPersonnelSO = null;
		}

	}

	/**
	 * Verify selected rejection reason
	 */
	public void verifySelectedRejectReasons() {
		getManagedBean().setRejectReasonReqd(false);
		for (Long resId : getManagedBean().getReasonId()) {
			if (resId.compareTo(getManagedBean().getRejectionOtherReasonId()) == 0) {
				getManagedBean().setRejectReasonReqd(true);
				break;
			}
		}
		if (!getManagedBean().isRejectReasonReqd()) {
			getManagedBean().setApplnRejectionRemarks("");
		}
	}

	/**
	 * Sets the SM application rejection reasons selected.
	 */
	private void setSmApplRejectionReasons() {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		List<SmApplRejectReason> resList = new ArrayList<SmApplRejectReason>();
		for (Long resId : getManagedBean().getReasonId()) {
			for (PmReasonDesc desc : getManagedBean().getReasonDescList()) {
				if (desc.getPmReason().getReasonId().equals(resId)) {
					SmApplRejectReason rej = new SmApplRejectReason();
					rej.setPmReason(desc.getPmReason());
					rej.setSmAppl(getManagedBean().getSmAppl());
					resList.add(rej);
					log.info("Reason Id === " + resId);
					sb.append(++count).append(".)");
					sb.append(desc.getReasonDesc());
					sb.append("\n");
				}
			}

		}
		if (getManagedBean().getSmAppl().getSmApplRejectReasons() != null
				&& !getManagedBean().getSmAppl().getSmApplRejectReasons()
						.isEmpty()) {
			log.info(" ***** Already exisiting records ******* "
					+ getManagedBean().getSmAppl().getSmApplRejectReasons()
							.size());
			getManagedBean().getSmAppl().getSmApplRejectReasons().clear();
			log.info(" *****Size after reset******* "
					+ getManagedBean().getSmAppl().getSmApplRejectReasons()
							.size());
		}
		getManagedBean().getSmAppl().getSmApplRejectReasons().addAll(resList);
		getManagedBean().setReasonDesc(sb.toString());
	}

	/**
	 * Sets the PO or Approver section review.
	 */
	private void setPoOrApprvrSectionReview() {
		if (getManagedBean().isPoUser()
				&& getManagedBean().getPoSecReview().getApplSectionReviewId() != null) {
			for (SmApplSectionReview rev : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (rev.getApplSectionReviewId().equals(
						getManagedBean().getPoSecReview()
								.getApplSectionReviewId())) {
					getManagedBean()
							.getSmAppl()
							.getSmApplSectionReviews()
							.set(getManagedBean().getSmAppl()
									.getSmApplSectionReviews().indexOf(rev),
									getManagedBean().getPoSecReview());
				}
			}

		}
		if (getManagedBean().isApproverUser()
				&& getManagedBean().getApprvrSecReview()
						.getApplSectionReviewId() != null) {
			log.info("**** Existing Apprvr remarks *******");
			for (SmApplSectionReview revs : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (revs.getApplSectionReviewId().equals(
						getManagedBean().getApprvrSecReview()
								.getApplSectionReviewId())) {
					getManagedBean()
							.getSmAppl()
							.getSmApplSectionReviews()
							.set(getManagedBean().getSmAppl()
									.getSmApplSectionReviews().indexOf(revs),
									getManagedBean().getApprvrSecReview());
				}
			}
		}
	}

	/**
	 * Validate before save.
	 * 
	 * @param applType
	 *            - the appl type
	 * @param currentPageType
	 *            - the current page type
	 * @return true, if valid
	 */
	private Boolean validateBeforeSave(String applType, String currentPageType) {
		log.info("******* Page Type ********" + currentPageType);
		boolean validSave = true;
		switch (currentPageType) {
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC:
			validSave = SmApplicationUtil.validateBasicInfoBeforeSave(
					validSave, getManagedBean(), currentTab);
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY:
			validSave = SmApplicationUtil.validateEquityOwnerCount(
					getManagedBean(), currentTab, currentPageType);
			validSave = SmApplicationUtil.validateEquityOwnerAmount(
					getManagedBean(), currentTab, ruleHelper);
			validSave = SmApplicationUtil.validateEquityCompanyAmount(
					getManagedBean(), currentTab);
			if (getManagedBean().getSmAppl().getSupplierType()
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				validSave = SmApplicationUtil.validatePersonnelAcademics(
						validSave, getManagedBean(), currentTab);
			}
			validatePaidCaptialAmt(true);
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL:

			validSave = SmApplicationUtil.validateEquityOwnerCount(
					getManagedBean(), currentTab, currentPageType);
			if (getManagedBean().getSmAppl().getSupplierType()
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				validSave = SmApplicationUtil.validatePersonnelAcademics(
						validSave, getManagedBean(), currentTab);
			}
			validSave = SmApplicationUtil.validatePersonnelBeforeSave(
					validSave, currentTab, getManagedBean());
			calculateBumiPercentageTotals();
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK:
			validSave = SmApplicationUtil.validateBankBeforeSave(validSave,
					getManagedBean(), currentTab);
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY:
			validSave = SmApplicationUtil.validateCategoriesBeforeSave(
					validSave, getManagedBean(), currentTab);
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY:
			validSave = SmApplicationUtil.validateFacilityPanel(
					getManagedBean(), currentTab, validSave);
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS:
			if ((getManagedBean().isPoUser() || getManagedBean()
					.isApproverUser()) && currentTab < outputTab) {
				validSave = SmApplicationUtil.checkSupportDocsValidity(
						validSave, getManagedBean(), currentTab);
			}

		default:
			log.info("****** Not a defined page ***********");

		}
		return validSave;
	}

	/**
	 * Retrieve and Assign supporting documents list required for application.
	 */
	private void assignApplicationSupportingDocs() {
		log.info(" ** For Suporting Docs ** ");

		// default to false has suppdoc to download
		getManagedBean().setHasSuppDocToDownload(false);

		if (getManagedBean().getSmAppl().getSmApplSupportDocs() == null) {
			getManagedBean().getSmAppl().setSmApplSupportDocs(
					new ArrayList<SmApplSupportDoc>());
		}
		DocumentReqDTO rqDto = new DocumentReqDTO();
		rqDto.setStateCode(getManagedBean().getSmAppl().getSmCompanyBasic()
				.getSmCompanyAddresses().get(0).getSmAddress().getPmState()
				.getStateCode());
		rqDto.setAppTypeCode(getManagedBean().getSmAppl().getApplType());
		if (!getManagedBean().getSmAppl().getSmSupplierCategories().isEmpty()) {
			List<Long> catL3Ids = new ArrayList<Long>();
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catL3Ids.add(cat.getPmCategoryL3().getCategoryL3Id());
				}
			}

			// temp fix to reduce the number of supporting documents retrieve
			// for update & bumi.
			if (catL3Ids.size() == 0) {
				catL3Ids.add(0L);
			}

			rqDto.setCategoryL3Ids(catL3Ids);
			log.info(" **** Total categories sent to Doc So ***"
					+ rqDto.getCategoryL3Ids().size());
		}
		if (!getManagedBean().getSmAppl().getSmSupplierCerts().isEmpty()) {
			List<Long> certIds = new ArrayList<>();
			for (SmSupplierCert cert : getManagedBean().getSmAppl()
					.getSmSupplierCerts()) {
				if (getManagedBean().getGlpCert() != null
						&& cert.getCertTypeId() != null
						&& cert.getCertTypeId().compareTo(
								new BigDecimal(getManagedBean().getGlpCert()
										.getParameterId())) == 0) {
					if (cert.getPmCertBody() != null
							&& cert.getPmCertBody().getCertBodyId() != null)
						certIds.add(cert.getPmCertBody().getCertBodyId());
				}
			}
			rqDto.setCertBodyIds(certIds);
		}

		rqDto.setSupplierTypeCode(getManagedBean().getSmAppl()
				.getSupplierType());
		rqDto.setCompanyTypeCode(getManagedBean().getSmAppl().getSmSupplier()
				.getBusinessType());
		DocumentResDTO rsDTO = null;
		rsDTO = retrieveSupportingDocumentList(rqDto);

		if (rsDTO != null && rsDTO.getDocumentList() != null
				&& !rsDTO.getDocumentList().isEmpty()) {
			log.info("*** Not Empty list ****" + rsDTO.getDocumentList().size());
			List<SmApplSupportDoc> docs = new ArrayList<SmApplSupportDoc>();

			if (getManagedBean().getSmAppl().getSmApplSupportDocs().isEmpty()) {
				log.info(" ******* Empty Doc List *****");
				for (PmDocument doc : rsDTO.getDocumentList()) {
					docs.add(SmApplicationUtil.getDefaultSmApplSupportingDoc(
							doc, getManagedBean().getSmAppl()));

					// to display or hide download column
					if (doc.isDownload()) {
						getManagedBean().setHasSuppDocToDownload(true);
					}
				}
			} else {
				if (getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().get(0).getSmAddress()
						.getPmState().getStateId()
						.equals(getManagedBean().getPrevBasicStateId())) {
					log.info(" ******* Existing doc list without change in state id  *****");
					docs.addAll(getManagedBean().getSmAppl()
							.getSmApplSupportDocs());
					for (PmDocument doc : rsDTO.getDocumentList()) {
						boolean recordExists = false;
						for (SmApplSupportDoc smDocu : getManagedBean()
								.getSmAppl().getSmApplSupportDocs()) {
							if (smDocu.getPmDocument().getDocumentId()
									.equals(doc.getDocumentId())) {
								// docs.add(smDocu);
								recordExists = true;
							}
						}
						if (!recordExists) {
							docs.add(SmApplicationUtil
									.getDefaultSmApplSupportingDoc(doc,
											getManagedBean().getSmAppl()));
						}

						// to display or hide download column
						if (doc.isDownload()) {
							getManagedBean().setHasSuppDocToDownload(true);
						}
					}
				} else {
					log.info(" ******* Existing doc list without CHANGE in state id  *****");
					for (PmDocument doc : rsDTO.getDocumentList()) {
						getManagedBean().getSmAppl().getSmApplSupportDocs()
								.clear();
						docs.add(SmApplicationUtil
								.getDefaultSmApplSupportingDoc(doc,
										getManagedBean().getSmAppl()));
						if (doc.isDownload()) {
							getManagedBean().setHasSuppDocToDownload(true);
						}
					}
				}
			}

			getManagedBean().getSmAppl().setSmApplSupportDocs(docs);
		} else {
			log.info("*** Empty Supporting doc list **** ");
		}
		log.info(" ** For Suporting Docs END ** ");
	}

	/**
	 * Sets the tab value for the navigation i.e. count of the tab to navigate.
	 * 
	 * @param currentTab
	 *            - int current tab value
	 * @param outputTab
	 *            - int output tab value
	 * @param buttonType
	 *            - String button type prev,next or same
	 * 
	 */
	public void setTabValue(int currentTab, int outputTab, String buttonType) {
		log.info(" ***** Button type ***** " + buttonType);
		this.buttonType = buttonType;
		setTabValue(currentTab, outputTab);
	}

	/**
	 * Sets the tab value for the navigation i.e. count of the tab to navigate.
	 * 
	 * @param currentTab
	 *            - int current tab value
	 * @param outputTab
	 *            - int output tab value
	 */
	public void setTabValue(int currentTab, int outputTab) {
		log.info("**** SET TAB CALLED *******");
		this.currentTab = currentTab;
		this.outputTab = outputTab;
	}

	/**
	 * Assign review application page navigation counts for PO and Approver
	 * final steps.
	 * 
	 * @param navType
	 *            - the nav type i.e. next or prev
	 */
	public void assignReviewSupportingNav(String navType) {
		int currentPage = getManagedBean().getTabActiveIndex();
		try {
			this.currentTab = currentPage;
			// getManagedBean().getSmAppl().setSupportingDocMode(SupplierManagementConst.SUPPORTING_DOC_ONLINE);
			if (navType.equalsIgnoreCase("next")) {
				this.outputTab = currentPage + 1;
			} else if (navType.equalsIgnoreCase("prev")) {
				this.outputTab = currentPage - 1;
			} else {
				this.outputTab = currentPage;
			}
		} catch (Exception e) {
			this.currentTab = currentPage;
			this.outputTab = currentPage;
		}
	}

	/**
	 * Ajax call to set the base or office facility selected i.e. owned or
	 * rented.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent object
	 */
	public void officeFacilitySelected(AjaxBehaviorEvent e) {
		if (getManagedBean().getBasicCompanyFacility().getSmAddress() == null
				|| StringUtils
						.isBlank(getManagedBean().getBasicCompanyFacility()
								.getSmAddress().getAddress1())) {
			getManagedBean().getBasicCompanyFacility().setSmAddress(
					new SmAddress());
			SmApplicationUtil.getInstance().cloneAddress(
					getManagedBean().getBasicCompanyFacility().getSmAddress(),
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getSmCompanyAddresses().get(0).getSmAddress());
			SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
					getManagedBean(),
					getManagedBean().getBasicCompanyFacility().getSmAddress(),
					SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL);
		}
		setFacilityDefaultPrices(getManagedBean().getBasicCompanyFacility());
		initBusinessPremise();
	}

	/**
	 * Sets the facility default prices.
	 * 
	 * @param fac
	 *            - SmFacility object
	 */
	private void setFacilityDefaultPrices(SmFacility fac) {
		if (fac.getRentalRate() == null) {
			fac.setRentalRate(new BigDecimal(0.00));
		}
		if (fac.getPurchasePrice() == null) {
			fac.setPurchasePrice(new BigDecimal(0.00));
		}
	}

	/**
	 * Ajax call to assign selected personnel privilege
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void assignShareHolderPrivilege(AjaxBehaviorEvent e) {
		if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE
				&& StringUtils.isNotBlank(getManagedBean()
						.getSelectedShareHolder().getEpRole())
				&& getManagedBean().getSelectedShareHolder().getEpRole()
						.equals(RoleCode.FL_USER.value())) {
			getManagedBean().getSelectedShareHolder().setEpRole(
					RoleCode.MOF_SUPPLIER_USER.value());
		}
		if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() != DbValueConst.RECORD_STATUS_ACTIVE) {
			getManagedBean().setValidSmUserLimit(false);
		}
		if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(
					DbValueConst.RECORD_STATUS_SUSPENDED);
			getManagedBean().getShareHolderMailAddr().setSameAsPerm(true);
		} else {
			getManagedBean().getShareHolderMailAddr().setSameAsPerm(false);
			assignSelShareHolderAddress(getManagedBean()
					.getSelectedShareHolder());
		}
		buildRoleList(getManagedBean().getSelectedShareHolder());
	}

	/**
	 * Ajax call to assign selected personnel privilege by ep role.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void assignShareHolderPrivilegeByEpRole(AjaxBehaviorEvent e) {
		SmApplicationUtil.setPersonnelRoleChange(getManagedBean().getSmAppl(),
				getPrevApprovedPersonnel(getManagedBean()
						.getSelectedShareHolder()), getManagedBean()
						.getSelectedShareHolder());
	}

	/**
	 * Ajax call to set approve or reject date for the selected category.
	 * 
	 * @param category
	 *            - SmSupplierCategory
	 */
	public void assignCategoryApproveOrRejectDate(SmSupplierCategory category) {
		if (getManagedBean().isPoUser()) {
			log.info("***** PO DECISION SELECTED **** "
					+ category.getPoDecFlg());
		} else if (getManagedBean().isApproverUser()) {
			log.info("***** APPROVER DECISION SELECTED **** "
					+ category.getApprvrDecFlg());
		}
		category.setApprovedDate(new Date());
	}

	public void assignCategoryReApprovalOnOtherDescChange(SmSupplierCategory cat) {
		if (getManagedBean().isSupplierUser()) {
			if (StringUtils.isNotEmpty(cat.getOtherDesc())
					&& cat.getApprovedDate() != null) {
				if (!cat.getOtherDesc().trim()
						.equalsIgnoreCase(cat.getPrevOtherDesc().trim())) {
					cat.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
					cat.setPoDecFlg(null);
					cat.setApprvrDecFlg(null);
				} else {
					cat.setRecordStatusFlg(SupplierManagementConst.APPROVED_RECORD_STATUS);
					cat.setPoDecFlg(cat.getPrevPoDecFlg());
					cat.setApprvrDecFlg(cat.getPrevApprDecFlg());
				}

			}
		}
	}

	/**
	 * Ajax call to set the application category selected.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void applicationCategorySelected(AjaxBehaviorEvent e) {
		SmApplicationUtil.setApplicationCategorySelected(getManagedBean());
	}

	/**
	 * Ajax call to Assign documents verified value.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void assignDocVerified(AjaxBehaviorEvent e) {
		log.info(" *******Docs verified******88");
	}

	/**
	 * Assign application category type based on selection on screen.
	 */
	public void assignApplnCategoryType() {
		List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
		// Remove from active list
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			for (SmSupplierCategory cat : getManagedBean()
					.getActiveCategoryList()) {
				if (cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getActiveCategoryList().removeAll(catList);
		} else {
			for (SmSupplierCategory cat : getManagedBean()
					.getActiveCategoryList()) {
				if (!cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getActiveCategoryList().removeAll(catList);
		}
		// Remove from main list
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.removeAll(catList);
		} else {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (!cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.removeAll(catList);
		}
		StringBuffer sb = new StringBuffer();
		String desc = null;
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			desc = SmCommonUtil.getMessage("lbl.sm.categorycode.note.normal",
					null);
		} else {
			desc = SmCommonUtil.getMessage("lbl.sm.categorycode.note.special",
					null);
		}
		sb.append(desc);
		getManagedBean().setCatTypeConfirm(sb.toString());
		log.info("***** After confirm +++++++"
				+ getManagedBean().getSmAppl().getSupportingDocMode());
		if (getManagedBean().getSmAppl().getSupportingDocMode() == null
				|| getManagedBean().getSmAppl().getSupportingDocMode()
						.equals("")) {
			getManagedBean().getSmAppl().setSupportingDocMode(
					SupplierManagementConst.SUPPORTING_DOC_ONLINE);
		}
		saveOrUpdateAppl();
	}

	/**
	 * Reset application category type based on the selection to Normal or
	 * Special.
	 */
	public void resetApplnCategoryType() {
		log.info("********* Reset the Values ********");
		if (getManagedBean().getSmAppl().getApplCategoryType() != null
				&& getManagedBean().getSmAppl().getApplCategoryType()
						.equals("S")) {
			getManagedBean().getSmAppl().setApplCategoryType("N");
		} else {
			getManagedBean().getSmAppl().setApplCategoryType("S");
		}
		// getManagedBean().setCatTypeConfirm(null);
		if (getManagedBean().getSmAppl().getSupportingDocMode() == null
				|| getManagedBean().getSmAppl().getSupportingDocMode()
						.equals("")) {
			getManagedBean().getSmAppl().setSupportingDocMode(
					SupplierManagementConst.SUPPORTING_DOC_ONLINE);
		}
		RequestContext.getCurrentInstance().execute("typeConfirmation.hide()");
	}

	// -- Process Branches Start

	/**
	 * Copy hq gst info to branch
	 * 
	 * @param SmSupplierBranch
	 *            branch
	 */
	public void copyHqGstToBranch(SmSupplierBranch branch) {
		if (branch.getGst() == null) {
			branch.setGst(new SmGstSupplier());
			SmApplicationUtil.initGstSuppDocs(branch.getGst());
		}
		SmCommonUtil.copyHqGstToBranch(getManagedBean().getCompanyBasicGst(),
				branch.getGst(), branch.isSameAsHq(), branch.getBranchCode());
	}

	/**
	 * Delete GST
	 * 
	 * @param SmGstSupplier
	 *            gst
	 */
	public void deleteGst(SmGstSupplier gst) {
		gst.setGstRegNo(null);
		gst.setGstEffDate(null);
		gst.setGstEndDate(null);
		gst.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
		SmApplicationUtil.initGstSuppDocs(gst);
	}

	/**
	 * Add or update selected branch info to SmSupplierBranch list of the
	 * application.
	 */
	public void addOrUpdateBranchToSmAppl() {
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedBranch(), getManagedBean().getSmAppl()
				.getSmSupplierBranches());
		if (getManagedBean().getSelectedBranch().getSupplierBranchId() == null) {
			RequestContext.getCurrentInstance().execute(
					"suppBrnachDtWidVar.paginator.endLink.click()");
		}
		saveOrUpdateAppl();
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("banchDialog.hide()");
		calcPersonnelListCount();
	}

	/**
	 * Assign selected branch object to managed bean for editing the
	 * information.
	 * 
	 * @param branchBean
	 *            - SmSupplierBranch object
	 * @param index
	 *            - int
	 */
	public void assignSelBranch(SmSupplierBranch branchBean, int index) {

		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), branchBean.getSmAddress(),
				SupplierManagementConst.SM_BRANCH_PANEL);
		if (branchBean.getSmPersonnel() == null) {
			branchBean.setSmPersonnel(new SmPersonnel());
		}
		if (branchBean.getGst() == null) {
			branchBean.setGst(new SmGstSupplier());
			branchBean.getGst().setSmGstSupplierDocs(
					new ArrayList<SmGstSupplierDoc>());
		}
		SmApplicationUtil.initGstSuppDocs(branchBean.getGst());
		getManagedBean().setSelectedBranch(
				(SmSupplierBranch) SerializationUtils.clone(branchBean));
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestBranch(
					(SmSupplierBranch) EntityHelper
							.getEntityByMultiplePropertiesFromList(branchBean,
									getManagedBean().getLatestAppl()
											.getSmSupplierBranches(),
									new String[] { "branchCode" }, null, null));
		}
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("banchDialog.show()");

	}

	/**
	 * Add or create a new SmSupplierBranch object
	 */
	public void assignNewBranchBean() {
		SmSupplierBranch br = new SmSupplierBranch();
		br.setRevNo(1);
		br.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		br.setSmAppl(getManagedBean().getSmAppl());
		br.setSmPersonnel(new SmPersonnel());
		getManagedBean().setBranchCompanyStates(
				SmCommonUtil.getStateList(getManagedBean().getSelectedBranch()
						.getSmAddress().getPmCountry().getCountryId()));
		getManagedBean().setBranchCompanyDistricts(new ArrayList<SelectItem>());
		getManagedBean().setBranchCompanyCities(new ArrayList<SelectItem>());
		getManagedBean().setSelectedBranch(br);
		SmApplicationUtil.addNewBranchGst(
				getManagedBean().getCompanyBasicGst(), getManagedBean()
						.getSelectedBranch());
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("banchDialog.show()");
	}

	/**
	 * Cancel add or update functionality and close the supplier branch
	 * information dialog
	 */
	public void assignCancelBranchBean() {
		if (getManagedBean().getSelectedBranch() != null
				&& getManagedBean().getSelectedBranch().getGst() != null) {
			getManagedBean().getSelectedBranch().setGst(null);
		}
		assignValReqdToFalse();
		initGst();
		RequestContext.getCurrentInstance().reset(":form:tt:banchDialog");
		RequestContext.getCurrentInstance().execute("banchDialog.hide()");
	}

	/**
	 * Delete or remove selected branches from the SmSupplierBranch list of the
	 * application.
	 */
	public void deleteSelectedBranches() {
		// validate if deleted branch been used in bank section
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean()
				.getSelectedBranches())) {
			for (SmSupplierBranch brTmp : getManagedBean()
					.getSelectedBranches()) {
				if (SmApplicationUtil.isBranchBankAssoc(brTmp, getManagedBean()
						.getSmAppl().getSmSupplierBanks())) {
					throw new EpException(MessageConst.ERROR_722617);
				}
			}
		}
		if (getManagedBean().getSelectedBranches().size() > 0) {
			for (SmSupplierBranch brn : getManagedBean().getSelectedBranches()) {
				getManagedBean().getSmAppl().getSmSupplierBranches()
						.remove(brn);
			}
			getManagedBean().getSelectedBranches().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Company Branch" });
		}
		saveOrUpdateAppl();
	}

	// -- Process Branches End

	// -- Process Associated Companies Start
	/**
	 * Add or update selected associated company info to SmAssociateCompany list
	 * of the application.
	 */
	public void addOrUpdateAssocToSmAppl() {
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedAssocCompany(), getManagedBean().getSmAppl()
				.getSmAssociateCompanies());
		if (getManagedBean().getSelectedAssocCompany().getAssociateCompanyId() == null) {
			RequestContext.getCurrentInstance().execute(
					"assocDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("assocDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the associated company
	 * information dialog
	 */
	public void cancelAssocCompanyFuntion() {
		assignValReqdToFalse();
		if (getManagedBean().getSelectedAssocCompany() != null
				&& getManagedBean().getSelectedAssocCompany().getSmAddress() != null
				&& getManagedBean().getSelectedAssocCompany().getSmAddress()
						.getAddressId() != null) {
			getManagedBean().getSelectedAssocCompany().setSmAddress(
					SmCommonUtil.getInstance().getSmAddressById(
							getManagedBean().getSelectedAssocCompany()
									.getSmAddress().getAddressId()));
		}
		RequestContext.getCurrentInstance().reset(":form:tt:asDisp");
		RequestContext.getCurrentInstance().execute("assocDialog.hide()");
	}

	/**
	 * Assign selected associated company object to managed bean for editing the
	 * information.
	 * 
	 * @param assocBean
	 *            -SmAssociateCompany object
	 */
	public void assignSelAssoc(SmAssociateCompany assocBean) {
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), assocBean.getSmAddress(),
				SupplierManagementConst.SM_ASSOC_COMPANY_PANEL);
		getManagedBean().setSelectedAssocCompany(
				(SmAssociateCompany) SerializationUtils.clone(assocBean));
		getManagedBean().getSelectedAssocCompany().setIsShareholding(
				new BigDecimal(0));
		assignValReqdToTrue();
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestAssocCompany(
					(SmAssociateCompany) EntityHelper
							.getEntityByMultiplePropertiesFromList(assocBean,
									getManagedBean().getLatestAppl()
											.getSmAssociateCompanies(),
									new String[] { "regNo" }, null, null));
		}
		RequestContext.getCurrentInstance().execute("assocDialog.show()");
	}

	/**
	 * Add or create a new SmAssociateCompany object
	 */
	public void assignNewAssocBean() {
		getManagedBean().setSelectedAssocCompany(new SmAssociateCompany());
		getManagedBean().getSelectedAssocCompany().setRevNo(1);
		getManagedBean().getSelectedAssocCompany().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(null)));
		getManagedBean().getSelectedAssocCompany().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setAssocCompanyStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedAssocCompany().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setAssocCompanyDistricts(new ArrayList<SelectItem>());
		getManagedBean().setAssocCompanyCities(new ArrayList<SelectItem>());
		getManagedBean().getSelectedAssocCompany().setIsShareholding(
				new BigDecimal(0));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("assocDialog.show()");
	}

	/**
	 * Delete or remove selected assocaiated companies from the
	 * SmAssociateCompany list of the application.
	 */
	public void deleteSelectedAssocCompanies() {
		log.info("*** Delete Assoc Companies ******* "
				+ getManagedBean().getSelectedAssocCompanies().size());
		if (getManagedBean().getSelectedAssocCompanies().size() > 0) {
			for (SmAssociateCompany assoc : getManagedBean()
					.getSelectedAssocCompanies()) {
				getManagedBean().getSmAppl().getSmAssociateCompanies()
						.remove(assoc);
			}
			getManagedBean().getSelectedAssocCompanies().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Assocatied Company" });
		}
	}

	// -- Process Associated Companies End

	// -- Process Certs Start
	/**
	 * Add or update selected certificate info to SmSupplierCert list of the
	 * application.
	 * 
	 * @throws ParseException
	 *             the parse exception
	 */
	public void addOrUpdateCertsToSmAppl() throws ParseException {
		getManagedBean().getSelectedCert().setSmAppl(
				getManagedBean().getSmAppl());
		if (getManagedBean().getSelectedCert().getCertEndDate() != null
				&& getManagedBean()
						.getSelectedCert()
						.getCertStartDate()
						.compareTo(
								getManagedBean().getSelectedCert()
										.getCertEndDate()) >= 0) {
			throw new EpException(MessageConst.ERROR_722530);
		}
		EntityHelper.addOrUpdateEntityToList(
				getManagedBean().getSelectedCert(), getManagedBean()
						.getSmAppl().getSmSupplierCerts());
		if (getManagedBean().getSelectedCert().getSupplierCertId() == null) {
			RequestContext.getCurrentInstance().execute(
					"suppCertDtWidVar.paginator.endLink.click()");
		}
		if (getHq() != null) {
			saveOrUpdateAppl();
		}
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("certDialog.hide()");
	}

	/**
	 * Assign selected supplier cert object to managed bean for editing the
	 * information.
	 * 
	 * @param certBean
	 *            - SmSupplierCert object
	 */
	public void assignSelCert(SmSupplierCert certBean) {
		if (getHq() != null) {
			getManagedBean().setSelectedCert(
					(SmSupplierCert) SerializationUtils.clone(certBean));
		} else {
			getManagedBean().setSelectedCert(certBean);
		}
		assignCertBodyList();
		assignValReqdToTrue();
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestCert(
					(SmSupplierCert) EntityHelper
							.getEntityByMultiplePropertiesFromList(certBean,
									getManagedBean().getLatestAppl()
											.getSmSupplierCerts(),
									new String[] { "certTypeId", "certName" },
									new String[] { "pmCertBody" }, null));
		}
		RequestContext.getCurrentInstance().execute("certDialog.show()");
	}

	/**
	 * Cancel add or update functionality and close the supplier cert
	 * information dialog
	 */
	public void cancelSupplierCertAction() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:certPop");
		RequestContext.getCurrentInstance().execute("certDialog.hide()");
	}

	/**
	 * Add or create a new SmSupplierCert object
	 */
	public void assignNewCert() {
		SmSupplierCert cert = new SmSupplierCert();
		cert.setPmCertBody(new PmCertBody());
		cert.setRevNo(1);
		getManagedBean().setSelectedCert(cert);
		cert.setSmAppl(getManagedBean().getSmAppl());
		getManagedBean().setCertBodyLs(new ArrayList<PmCertBody>());
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("certDialog.show()");
	}

	/**
	 * Assign cert body list.
	 */
	public void assignCertBodyList() {
		if (getManagedBean().getSelectedCert() != null
				&& getManagedBean().getSelectedCert().getCertTypeId() != null) {
			PmParameter param = SmApplicationUtil
					.retrievePmParameterById(getManagedBean().getSelectedCert()
							.getCertTypeId().longValue());
			if (param != null) {
				getManagedBean().setCertBodyLs(
						SmApplicationUtil.retrievePmCertBodyList(param
								.getParameterCode()));

			}

		}
	}

	/**
	 * Delete or remove selected supplier certificates from the SmSupplierCert
	 * list of the application.
	 */
	public void deleteSelectedCerts() {
		if (getManagedBean().getSelectedCertList().size() > 0) {
			for (SmSupplierCert cert : getManagedBean().getSelectedCertList()) {
				getManagedBean().getSmAppl().getSmSupplierCerts().remove(cert);
			}
			getManagedBean().getSelectedCertList().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Supplier Certificate" });
		}
	}

	// -- Process Certss End
	// -- Process Assoc Bodies Start
	/**
	 * Add or update selected professional body info to SmAssociationBody List
	 * of the application.
	 * 
	 * @throws ParseException
	 *             the parse exception
	 */
	public void addOrUpdateAssocBodiesToSmAppl() throws ParseException {
		getManagedBean().getSelectedAssocBody().setSmAppl(
				getManagedBean().getSmAppl());
		if (getManagedBean().getSelectedAssocBody().getPmCertBody()
				.getCertBodyId() != null) {
			for (PmCertBody cert : getManagedBean().getProfBodyLs()) {
				if (getManagedBean().getSelectedAssocBody().getPmCertBody()
						.getCertBodyId().equals(cert.getCertBodyId())) {
					getManagedBean().getSelectedAssocBody().setPmCertBody(cert);
					break;
				}
			}

		}
		if (getManagedBean().getSmAppl().getSmAssociationBodies()
				.contains(getManagedBean().getSelectedAssocBody())) {

			// TODO if any validation reqd for edit
		} else {
			getManagedBean().getSmAppl().getSmAssociationBodies()
					.add(getManagedBean().getSelectedAssocBody());
			RequestContext.getCurrentInstance().execute(
					"suppProfBodyDtWidVar.paginator.endLink.click()");
		}
		SmApplicationUtil.assignNonPersonnelProfBodyList(getManagedBean());
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("profDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the professional body
	 * information dialog
	 */
	public void cancelSelProfBody() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:profPop");
		RequestContext.getCurrentInstance().execute("profDialog.hide()");
	}

	/**
	 * Assign selected professional body object to managed bean for editing the
	 * information.
	 * 
	 * @param assocBean
	 *            - SmAssociationBody object
	 */
	public void assignSelAssocBody(SmAssociationBody assocBean) {
		getManagedBean().setSelectedAssocBody(assocBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("profDialog.show()");
	}

	/**
	 * Add or create a new SmAssociationBody object
	 */
	public void assignNewAssocBody() {
		SmAssociationBody assocBean = new SmAssociationBody();
		assocBean.setPmCertBody(new PmCertBody());
		assocBean.setRevNo(1);
		getManagedBean().setSelectedAssocBody(assocBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("profDialog.show()");
	}

	/**
	 * Delete or remove selected professional bodies from the SmAssociationBody
	 * list of the application.
	 */
	public void deleteSelectedAssocBodies() {

		log.info("*** Delete AssocBodies ******* "
				+ getManagedBean().getSelectedAssocBodyList().size());
		if (getManagedBean().getSelectedAssocBodyList().size() > 0) {
			if (getManagedBean().getSelectedAssocBodyList().size() == getManagedBean()
					.getSmAppl().getSmAssociationBodies().size()) {
				throw new EpException(MessageConst.ERROR_722597);
			}
			for (SmAssociationBody assoc : getManagedBean()
					.getSelectedAssocBodyList()) {
				getManagedBean().getSmAppl().getSmAssociationBodies()
						.remove(assoc);
			}
			getManagedBean().getSelectedAssocBodyList().clear();
			SmApplicationUtil.assignNonPersonnelProfBodyList(getManagedBean());
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Professional Body" });
		}

	}

	// -- Process Assoc Bodies End

	// -- Process Consultant or Contractor Company Experience Start
	/**
	 * Add or update selected project info to SmCompanyExperience list of the
	 * application.
	 */
	public void addOrUpdateCompanyExperienceToSmAppl() {
		if (getManagedBean().getSelectedCompanyExperience()
				.getSmCompanyExperienceCats() == null
				|| (getManagedBean().getSelectedCompanyExperience()
						.getSmCompanyExperienceCats() != null && getManagedBean()
						.getSelectedCompanyExperience()
						.getSmCompanyExperienceCats().isEmpty())) {
			throw new EpException(MessageConst.ERROR_722550);
		}
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedCompanyExperience(), getManagedBean().getSmAppl()
				.getSmCompanyExperiences());
		if (getManagedBean().getSelectedCompanyExperience()
				.getCompanyExperienceId() == null) {
			if (getManagedBean().getSmAppl().getSupplierType()
					.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
				RequestContext.getCurrentInstance().execute(
						"suppCnstlExpDtWidVar.paginator.endLink.click()");
			} else {
				RequestContext.getCurrentInstance().execute(
						"suppContrExpDtWidVar.paginator.endLink.click()");
			}
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			RequestContext.getCurrentInstance().execute("consDialog.hide()");
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			RequestContext.getCurrentInstance().execute("contDialog.hide()");
		}
	}

	/**
	 * Cancel add or update functionality and close the company experience
	 * information dialog
	 */
	public void cancelCompExpFunctions() {
		assignValReqdToFalse();
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			RequestContext.getCurrentInstance().reset(":form:tt:consDialog");
			RequestContext.getCurrentInstance().execute("consDialog.hide()");
		} else {

			RequestContext.getCurrentInstance().reset(":form:tt:contDialog");
			RequestContext.getCurrentInstance().execute("contDialog.hide()");

		}
	}

	/**
	 * Assign selected company experience or project object to managed bean for
	 * editing the information.
	 * 
	 * @param consultantBean
	 *            - SmCompanyExperience object
	 */
	public void assignSelCompanyExperience(SmCompanyExperience consultantBean) {
		assignValReqdToTrue();
		if (consultantBean.getConsultantFee() == null) {
			consultantBean.setConsultantFee(new BigDecimal(0));
		}
		if (consultantBean.getContractValue() == null) {
			consultantBean.setContractValue(new BigDecimal(0));
		}
		getManagedBean()
				.setSupplierParentCatList(
						SmApplicationUtil
								.retrieveParentCategoryListForExperiencePage(getManagedBean()));
		if (consultantBean.getSmCompanyExperienceCats() != null
				&& !consultantBean.getSmCompanyExperienceCats().isEmpty()) {
			for (SmCompanyExperienceCat cat : consultantBean
					.getSmCompanyExperienceCats()) {
				populateExperienceChildCategoryByParent(cat.getPmCategoryL1()
						.getCategoryL1Id(), cat.getPmCategoryL2(),
						cat.getPmCategoryL3());
				populateExperienceSubChildCategoryByChild(cat.getPmCategoryL2()
						.getCategoryL2Id(), cat.getPmCategoryL3());
			}

		}
		getManagedBean().setSelectedCompanyExperience(
				(SmCompanyExperience) SerializationUtils.clone(consultantBean));
		SmFieldChangeUtil.initCompanyExperienceChangeField(getManagedBean());
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			RequestContext.getCurrentInstance().execute("consDialog.show()");
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			RequestContext.getCurrentInstance().execute("contDialog.show()");
		}
	}

	/**
	 * Assign new category object for select company experience or project.
	 * 
	 * @param exp
	 *            - SmCompanyExperience object
	 */
	public void assignCategoryForCompExperience(SmCompanyExperience exp) {
		getManagedBean().setSelectedCompanyExperience(exp);
		SmCompanyExperienceCat cat = new SmCompanyExperienceCat();
		cat.setPmCategoryL1(new PmCategoryL1());
		PmCategoryL2 catL2 = new PmCategoryL2();
		catL2.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		cat.setPmCategoryL2(catL2);
		PmCategoryL3 catL3 = new PmCategoryL3();
		catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		cat.setPmCategoryL3(catL3);
		cat.setSmCompanyExperience(getManagedBean()
				.getSelectedCompanyExperience());
		getManagedBean().getSelectedCompanyExperience()
				.getSmCompanyExperienceCats().add(cat);
		getManagedBean().setSelected(false);
		getManagedBean().setSelectedAll(false);

	}

	/**
	 * Check for duplicate category in the same project.
	 * 
	 * @param expCat
	 *            - SmCompanyExperienceCat object
	 * @param exp
	 *            - SmCompanyExperience object
	 */
	public void checkDuplicateCategoryForProject(SmCompanyExperienceCat expCat,
			SmCompanyExperience exp) {
		log.info("**** Validate the category Codes ********** ");
		for (SmCompanyExperienceCat cat : exp.getSmCompanyExperienceCats()) {
			if (expCat.equals(cat)) {
				log.info(" *** Same Obj ****"
						+ cat.getPmCategoryL1().getCategoryL1Code() + " -- --"
						+ expCat.getPmCategoryL1().getCategoryL1Code());

			} else if (cat.getPmCategoryL1().getCategoryL1Id()
					.equals(expCat.getPmCategoryL1().getCategoryL1Id())
					&& cat.getPmCategoryL2().getCategoryL2Id()
							.equals(expCat.getPmCategoryL2().getCategoryL2Id())
					&& cat.getPmCategoryL3().getCategoryL3Id()
							.equals(expCat.getPmCategoryL3().getCategoryL3Id())) {
				expCat.getPmCategoryL3().setCategoryL3Id(null);
				throw new EpException(MessageConst.ERROR_722533);
			}
		}

	}

	/**
	 * Delete selected category from company experience object.
	 * 
	 * @param exp
	 *            -SmCompanyExperience object
	 */
	public void deleteSelectedCategoryFromCompanyExperience(
			SmCompanyExperience exp) {
		if (getManagedBean().getSelectedCompExpCats().size() == exp
				.getSmCompanyExperienceCats().size()) {
			throw new EpException(MessageConst.ERROR_722549);
		}
		if (getManagedBean().getSelectedCompExpCats().size() > 0) {
			for (SmCompanyExperienceCat cat : getManagedBean()
					.getSelectedCompExpCats()) {
				exp.getSmCompanyExperienceCats().remove(cat);
			}
			getManagedBean().getSelectedCompExpCats().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Category" });
		}
		getManagedBean().setSelected(false);
		getManagedBean().setSelectedAll(false);
	}

	/**
	 * Add or create a new SmCompanyExperience object
	 */
	public void assignNewCompanyExperience() {
		log.info("***** Assign New Company Exp ***********");
		SmCompanyExperience exp = new SmCompanyExperience();
		exp.setRevNo(1);
		exp.setSupplierType(getManagedBean().getSmAppl().getSupplierType());
		exp.setSmAppl(getManagedBean().getSmAppl());
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			exp.setProcurementModeId(new Integer(0));
		}
		exp.setConsultantFee(new BigDecimal(0));
		exp.setContractValue(new BigDecimal(0));
		List<SmCompanyExperienceCat> expCatList = new ArrayList<SmCompanyExperienceCat>();
		SmCompanyExperienceCat cat = new SmCompanyExperienceCat();
		cat.setPmCategoryL1(new PmCategoryL1());
		PmCategoryL2 catL2 = new PmCategoryL2();
		catL2.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		cat.setPmCategoryL2(catL2);
		PmCategoryL3 catL3 = new PmCategoryL3();
		catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		cat.setPmCategoryL3(catL3);
		cat.setSmCompanyExperience(exp);
		expCatList.add(cat);
		exp.setSmCompanyExperienceCats(expCatList);

		getManagedBean().setSelectedCompanyExperience(exp);
		getManagedBean()
				.setSupplierParentCatList(
						SmApplicationUtil
								.retrieveParentCategoryListForExperiencePage(getManagedBean()));
		resetChildCatList();
		assignValReqdToTrue();
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			RequestContext.getCurrentInstance().execute("consDialog.show()");
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			RequestContext.getCurrentInstance().execute("contDialog.show()");
		}

	}

	/**
	 * Delete or remove selected company projects from the SmCompanyExperience
	 * list of the application.
	 */
	public void deleteSelectedCompanyExperiences() {
		if (getManagedBean().getSelectedCompanyExperiences().size() > 0) {
			for (SmCompanyExperience exp : getManagedBean()
					.getSelectedCompanyExperiences()) {
				getManagedBean().getSmAppl().getSmCompanyExperiences()
						.remove(exp);
			}
			getManagedBean().getSelectedCompanyExperiences().clear();
		} else {
			log.info(" ******** NO Record selected please select atleast one ");
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Company Project" });
		}

	}

	// ---- Process Consultant or Contractor Company Experience End

	// -- Process Equity Companies Start
	/**
	 * Add or update selected share holding company info to SmEquityCompany list
	 * of the application.
	 */
	public void addOrUpdateEquityCompanyToSmAppl() {
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedEquityCompany(), getManagedBean().getSmAppl()
				.getSmEquityCompanies());
		if (getManagedBean().getSelectedEquityCompany().getEquityCompanyId() == null) {
			RequestContext.getCurrentInstance().execute(
					"suppEquiCompDtWidVar.paginator.endLink.click()");
		}
		saveOrUpdateAppl();
		calculateCapital();
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("eqiCompDialog.hide()");
	}

	/**
	 * Assign selected smEquityCompany object to managed bean for editing the
	 * information.
	 * 
	 * @param equiCompBean
	 *            -SmEquityCompany object
	 */
	public void assignSelEquiCompany(SmEquityCompany equiCompBean) {
		if (equiCompBean.getShareAmt() == null) {
			equiCompBean.setShareAmt(new BigDecimal(0));
		}
		validatePaidCaptialAmt(false);
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), equiCompBean.getSmAddress(),
				SupplierManagementConst.SM_EQUI_COMPANY_PANEL);
		getManagedBean().setSelectedEquityCompany(
				(SmEquityCompany) SerializationUtils.clone(equiCompBean));
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestEquityCompany(
					(SmEquityCompany) EntityHelper
							.getEntityByMultiplePropertiesFromList(
									equiCompBean, getManagedBean()
											.getLatestAppl()
											.getSmEquityCompanies(),
									new String[] { "regNo" }, null, null));
		}
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("eqiCompDialog.show()");
	}

	/**
	 * Add or create a new SmEquityCompany object
	 */
	public void assignNewEquiCompBean() {
		validatePaidCaptialAmt(false);
		getManagedBean().setSelectedEquityCompany(new SmEquityCompany());
		getManagedBean().getSelectedEquityCompany().setRevNo(1);
		getManagedBean().getSelectedEquityCompany().setShareAmt(
				new BigDecimal(0));
		getManagedBean().getSelectedEquityCompany().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry())));
		getManagedBean().getSelectedEquityCompany().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setEquiCompanyStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedEquityCompany().getSmAddress()
						.getPmCountry().getCountryId()));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("eqiCompDialog.show()");
	}

	/**
	 * Delete or remove selected share holding or equity companies from the
	 * SmEquityCompany list of the application.
	 */
	public void deleteSelectedEquiCompanies() {

		if (getManagedBean().getSelectedEquityCompanies().size() > 0) {
			validatePaidCaptialAmt(false);
			for (SmEquityCompany equi : getManagedBean()
					.getSelectedEquityCompanies()) {
				getManagedBean().getSmAppl().getSmEquityCompanies()
						.remove(equi);
			}
			getManagedBean().getSelectedEquityCompanies().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Share Holding Company" });
		}

	}

	/**
	 * Cancel add or update functionality and close the share holding company
	 * information dialog
	 */
	public void cancelEquiCompFuntionc() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:eqiCompDialog");
		RequestContext.getCurrentInstance().execute("eqiCompDialog.hide()");
	}

	// -- Process Equity Companies End

	// -- Process Category Codes Start

	/**
	 * Ajax action performed when parent is changed on the screen i.e. reset
	 * child and sub child category values
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent object
	 */
	public void changeParentCategory(AjaxBehaviorEvent e) {
		String selPmCatL1 = getManagedBean().getSelectedParentCat();
		if (selPmCatL1 == null) {
			PmCategoryL1 p = new PmCategoryL1();
			p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
			getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		} else {
			for (PmCategoryL1 p : getManagedBean().getDefaultParentCatList()) {
				if (p.getCategoryL1Code().equalsIgnoreCase(selPmCatL1)) {
					if (p.getPmCategoryL2s() == null) {
						p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
					}
					getManagedBean().getSelectedCategory().setPmCategoryL1(p);

				}
			}
		}
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(
				new PmCategoryL2());
		getManagedBean().getSelectedCategory().setPmCategoryL3(
				new PmCategoryL3());
		getManagedBean().setSelectedChildCat(null);
		getManagedBean().setSelectedSubChildCat(null);

	}

	/**
	 * Ajax action performed when child category is changed on the screen i.e.
	 * reset sub child category values
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent object
	 */
	public void changeChildCategory(AjaxBehaviorEvent e) {
		String selPmCatL2 = getManagedBean().getSelectedChildCat();
		if (selPmCatL2 == null) {
			PmCategoryL2 p2 = new PmCategoryL2();
			p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
			getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		} else {
			for (PmCategoryL2 p2 : getManagedBean().getSelectedCategory()
					.getPmCategoryL1().getPmCategoryL2s()) {
				if (p2.getCategoryL2Code().equalsIgnoreCase(selPmCatL2)) {
					if (p2.getPmCategoryL3s() == null) {
						p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
					}
					getManagedBean().getSelectedCategory().setPmCategoryL2(p2);

				}
			}
		}
		getManagedBean().getSelectedCategory().setPmCategoryL3(
				new PmCategoryL3());
		getManagedBean().setSelectedSubChildCat(null);

	}

	/**
	 * Resetting all the existing values.
	 */
	private void resetSelectedCategoryParams() {
		PmCategoryL1 p = new PmCategoryL1();
		p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		PmCategoryL3 p3 = new PmCategoryL3();
		getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
	}

	/**
	 * Add or create a new SmSupplierCategory object.
	 */
	public void assignNewCategoryCode() {
		boolean validCatCode = true;
		if (getManagedBean().getSmAppl().getSmSupplierCategories().size() == ruleHelper
				.getMaxCategoryAllowed()) {
			validCatCode = false;
			throw new EpException(MessageConst.ERROR_722517);
		}

		if (validCatCode) {
			SmSupplierCategory categ = new SmSupplierCategory();
			categ.setRevNo(1);
			categ.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
			if (getManagedBean().getSmAppl().getApplCategoryType()
					.equalsIgnoreCase("S")) {
				categ.setSpecialCategory(true);
			}
			categ.setSmAppl(getManagedBean().getSmAppl());
			categ.setSupplierType(getManagedBean().getSmAppl()
					.getSupplierType());
			getManagedBean().setSelectedCategory(categ);
			getManagedBean()
					.setSupplierParentCatList(
							SmApplicationUtil
									.retrieveParentCategoryListForCategoryCodesPage(getManagedBean()));
			getManagedBean().setSupplierChildCatList(
					new ArrayList<PmCategoryL2>());
			this.getManagedBean().setSupplierSubChildCatList(
					new ArrayList<PmCategoryL3>());
			resetSelectedCategoryParams();
			assignValReqdToTrue();
			RequestContext.getCurrentInstance().execute("regCatDialog.show()");
		}
	}

	/**
	 * Cancel save or update functionality and close the category codes
	 * information dialog
	 */
	public void cancelCategoryCodesSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:regCatDialog");
		RequestContext.getCurrentInstance().execute("regCatDialog.hide()");
	}

	/**
	 * Action to Approve all category codes by PO or Approver.
	 */
	public void approveAllCategoryCodesByPOorApprover() {
		for (SmSupplierCategory cat : getManagedBean().getActiveCategoryList()) {
			if (getManagedBean().isPoUser()) {
				cat.setPoDecFlg(1);
			} else if (getManagedBean().isApproverUser()) {
				cat.setApprvrDecFlg(1);
			}
		}
	}

	/**
	 * Action to Approve all supporting document by PO or Approver.
	 */
	public void approveAllSuppDocByPOorApprover() {
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmApplSupportDocs())) {
			for (SmApplSupportDoc doc : getManagedBean().getSmAppl()
					.getSmApplSupportDocs()) {
				doc.setVerifiedFlg(1);
			}
		}
	}

	/**
	 * Delete selected category codes from the SmSupplierCategory list of the
	 * application.
	 */
	public void deleteSelectedCategoryCodes() {
		SmApplicationUtil.checkCategoryPendingTransaction(getManagedBean()
				.getDeleteCategoryList(), getManagedBean().getSmAppl()
				.getSmSupplier().getSupplierId());
		if (getManagedBean().getDeleteCategoryList() != null
				&& getManagedBean().getDeleteCategoryList().size() > 0) {
			List<SmSupplierCategory> categories = getManagedBean()
					.getDeleteCategoryList();
			deleteCatCodes(categories);
			// API FOR LIST OF OVERLAPPING CATEGORIES
			SmApplicationUtil.checkOverLappingCategories(getManagedBean());
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Category Code" });
		}
	}

	/**
	 * Delete category codes.
	 * 
	 * @param categories
	 *            - List of categories
	 */
	private void deleteCatCodes(List<SmSupplierCategory> categories) {
		for (SmSupplierCategory category : categories) {
			if (category.getRecordStatusFlg().compareTo(
					SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
				getManagedBean().getSmAppl().getSmSupplierCategories()
						.remove(category);
			} else if (category.getRecordStatusFlg().compareTo(
					SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
				category.setRecordStatusFlg(SupplierManagementConst.RECORD_STATUS_DELETED);
			}
		}
		saveOrUpdateAppl();
		SmApplicationUtil.assignActiveAndApprovedCategoryList(getManagedBean());
	}

	/**
	 * Delete selected category code(s) that are still pending approval from the
	 * SmSupplierCategory list of the application.
	 */
	public void deleteSelectedPendingApprovalCategoryCodes() {
		if (getManagedBean().getDeleteNewApplCategoryList() != null
				&& getManagedBean().getDeleteNewApplCategoryList().size() > 0) {
			List<SmSupplierCategory> categories = getManagedBean()
					.getDeleteNewApplCategoryList();
			deleteCatCodes(categories);
			// API FOR LIST OF OVERLAPPING CATEGORIES
			SmApplicationUtil.checkOverLappingCategories(getManagedBean());
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Category Code" });
		}
	}

	/**
	 * Save selected category information into SmSupplierCategory list of the
	 * application.
	 */
	public void saveSelectedCategory() {
		boolean validCat = true;
		if (getManagedBean().getSelectedCategory().getPmCategoryL1() != null
				&& getManagedBean().getSelectedCategory().getPmCategoryL1()
						.getCategoryL1Id() != null) {
			if (getManagedBean().getSelectedCategory().getPmCategoryL1()
					.getCategoryL1Code() != null
					&& getManagedBean()
							.getSelectedCategory()
							.getPmCategoryL1()
							.getCategoryL1Code()
							.equalsIgnoreCase(
									SupplierManagementConst.ICT_CATEGORY_L1_CODE)) {
				validCat = SmApplicationUtil.validateIctCatCode(
						getManagedBean().getBusinessTypeVal(), getManagedBean()
								.getSmAppl(), getManagedBean()
								.getSelectedCategory());
			}
		}
		boolean overLapCategory = false;
		overLapCategory = SmApplicationUtil.validL3CategoryCodeForOverLapping(
				getManagedBean().getSelectedCategory().getPmCategoryL3()
						.getCategoryL3Id(), getManagedBean().getSmSupplier()
						.getSupplierId(), getManagedBean().getSmAppl()
						.getApplId());
		getManagedBean().getSelectedCategory().setOverLapCategory(
				overLapCategory);
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getPmCategoryL1()
					.getCategoryL1Id()
					.equals(getManagedBean().getSelectedCategory()
							.getPmCategoryL1().getCategoryL1Id())
					&& cat.getPmCategoryL2()
							.getCategoryL2Id()
							.equals(getManagedBean().getSelectedCategory()
									.getPmCategoryL2().getCategoryL2Id())
					&& cat.getPmCategoryL3()
							.getCategoryL3Id()
							.equals(getManagedBean().getSelectedCategory()
									.getPmCategoryL3().getCategoryL3Id())) {
				validCat = false;
				if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.DELETED_RECORD_STATUS) == 0) {
					if (cat.getCancelledDate() == null) {
						throw new EpException(MessageConst.ERROR_722563);
					} else if (!isValidToAddAfterDeleted(cat.getCancelledDate())) {
						throw new EpException(MessageConst.ERROR_722563);
					}
				} else {
					throw new EpException(MessageConst.ERROR_722533);
				}
			}
		}

		if (validCat) {
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.add(getManagedBean().getSelectedCategory());

			if (!getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {

			} else {

			}
			saveOrUpdateAppl();
			SmApplicationUtil
					.assignActiveAndApprovedCategoryList(getManagedBean());
			assignValReqdToFalse();
			// API FOR LIST OF OVERLAPPING CATEGORIES
			SmApplicationUtil.checkOverLappingCategories(getManagedBean());
			RequestContext.getCurrentInstance().execute("regCatDialog.hide()");

		}
	}

	/**
	 * Check whether is valid to add again previously deleted category
	 * 
	 * @param Date
	 *            cancelDate
	 * @return boolean
	 */
	private boolean isValidToAddAfterDeleted(Date cancelDate) {
		boolean retVal = false;
		Calendar cancelCalDate = Calendar.getInstance();
		cancelCalDate.setTime(cancelDate);
		cancelCalDate.add(Calendar.YEAR, SmRuleHelper.getInstance()
				.getYearsAllowedToAddDeletedCatCode());
		if (new Date().after(cancelCalDate.getTime())) {
			retVal = true;
		}
		return retVal;
	}

	// -- Process Category Codes End
	// factory start
	/**
	 * Add or update selected factory info to SmFacility list of the
	 * application.
	 */
	public void addOrUpdateFactoryToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedFactoryFacility();
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getSmAppl().getSmFacilities());
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getApplFactoryFacilities());
		if (smFac.getFacilityId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smFacilityFactorDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("factoryDialog.hide()");

	}

	/**
	 * Cancel add or update functionality and close the factory information
	 * dialog
	 */
	public void cancelFactorySaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:factoryDialog");
		RequestContext.getCurrentInstance().execute("factoryDialog.hide()");
	}

	/**
	 * Assign selected factory - facility object to managed bean for editing the
	 * information.
	 * 
	 * @param facilityBean
	 *            - SmFacility object
	 */
	public void assignSelFactory(SmFacility facilityBean) {
		log.info("******** Selected value ******"
				+ facilityBean.getSmAddress().getAddress1());
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), facilityBean.getSmAddress(),
				SupplierManagementConst.SM_FACTORY_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		getManagedBean().setSelectedFactoryFacility(
				(SmFacility) SerializationUtils.clone(facilityBean));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.factory", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("factoryDialog.show()");
	}

	/**
	 * Add or create a new SmFacility object of type Factory
	 */
	public void assignNewFactoryBean() {
		getManagedBean().setSelectedFactoryFacility(new SmFacility());
		setFacilityDefaultPrices(getManagedBean().getSelectedFactoryFacility());
		getManagedBean().getSelectedFactoryFacility().setRevNo(1);
		getManagedBean().getSelectedFactoryFacility().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry())));
		getManagedBean().getSelectedFactoryFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedFactoryFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_FACTORY);
		getManagedBean().setFactoryFacilityStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.factory", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("factoryDialog.show()");
	}

	/**
	 * Factory owner type selected on screen.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void factoryOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedFactoryFacility()
						.getOwnershipType());
	}

	/**
	 * Delete or remove selected factories from the SmFacility list of the
	 * application.
	 */
	public void deleteSelectedFactories() {
		if (getManagedBean().getSelectedFactoryFacilities().size() > 0) {
			for (SmFacility sm : getManagedBean()
					.getSelectedFactoryFacilities()) {
				getManagedBean().getSmAppl().getSmFacilities().remove(sm);
				getManagedBean().getApplFactoryFacilities().remove(sm);
			}
			getManagedBean().getSelectedFactoryFacilities().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Factory" });
		}

	}

	// factory end

	// Dockyard start
	/**
	 * Add or update selected dockyard info to SmFacility list of the
	 * application.
	 */
	public void addOrUpdateDockYardToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedDockyardFacility();
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getSmAppl().getSmFacilities());
		if (smFac.getFacilityId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smFacilityDockDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("dockYardDialog.hide()");

	}

	/**
	 * Cancel add or update functionality and close the dockyard information
	 * dialog
	 */
	public void cancelDockyardSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:dockYardDialog");
		RequestContext.getCurrentInstance().execute("dockYardDialog.hide()");
	}

	/**
	 * Assign selected dockyard - facility object to managed bean for editing
	 * the information.
	 * 
	 * @param facilityBean
	 *            - SmFacility object
	 */
	public void assignSelDockyard(SmFacility facilityBean) {
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), facilityBean.getSmAddress(),
				SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		getManagedBean().setSelectedDockyardFacility(
				(SmFacility) SerializationUtils.clone(facilityBean));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.dockyard", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("dockYardDialog.show()");
	}

	/**
	 * Add or create a new SmFacility object of type Dockyard
	 */
	public void assignNewDockyardBean() {
		getManagedBean().setSelectedDockyardFacility(new SmFacility());
		setFacilityDefaultPrices(getManagedBean().getSelectedDockyardFacility());
		getManagedBean().getSelectedDockyardFacility().setRevNo(1);
		getManagedBean().getSelectedDockyardFacility().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry())));
		getManagedBean().getSelectedDockyardFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedDockyardFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD);
		getManagedBean().setDockFacilityStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.dockyard", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("dockYardDialog.show()");
	}

	/**
	 * Dockyard owner type selected on screen.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void dockOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedDockyardFacility()
						.getOwnershipType());
	}

	/**
	 * Delete or remove selected dockyards from the SmFacility list of the
	 * application.
	 */
	public void deleteSelectedDocks() {

		if (getManagedBean().getSelectedDockyardFacilities().size() > 0) {
			for (SmFacility sm : getManagedBean()
					.getSelectedDockyardFacilities()) {
				getManagedBean().getSmAppl().getSmFacilities().remove(sm);
				getManagedBean().getApplDockyardFacilities().remove(sm);
			}
			getManagedBean().getSelectedDockyardFacilities().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Dockyard" });
		}

	}

	// Dockyard end
	// Workshop start
	/**
	 * Add or update selected workshop info to SmFacility list of the
	 * application.
	 */
	public void addOrUpdateWorkshopToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedWorkShopFacility();
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getSmAppl().getSmFacilities());
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getApplWorkShopFacilities());
		if (smFac.getFacilityId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smWorkShopDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("workShopDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the workshop information
	 * dialog
	 */
	public void cancelWorkShopSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:workShopDialog");
		RequestContext.getCurrentInstance().execute("workShopDialog.hide()");
	}

	/**
	 * Assign selected workshop - facility object to managed bean for editing
	 * the information.
	 * 
	 * @param facilityBean
	 *            - SmFacility object
	 */
	public void assignSelWorkshop(SmFacility facilityBean) {
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), facilityBean.getSmAddress(),
				SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		getManagedBean().setSelectedWorkShopFacility(
				(SmFacility) SerializationUtils.clone(facilityBean));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.workshop", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("workShopDialog.show()");
	}

	/**
	 * Add or create a new SmFacility object of type Workshop
	 */
	public void assignNewWorkshopBean() {
		getManagedBean().setSelectedWorkShopFacility(new SmFacility());
		setFacilityDefaultPrices(getManagedBean().getSelectedWorkShopFacility());
		getManagedBean().getSelectedWorkShopFacility().setRevNo(1);
		getManagedBean().getSelectedWorkShopFacility().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry())));
		getManagedBean().getSelectedWorkShopFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedWorkShopFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP);
		getManagedBean().setWorkShopFacilityStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.workshop", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("workShopDialog.show()");
	}

	/**
	 * work shop owner type selected on screen.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void workShopOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedWorkShopFacility()
						.getOwnershipType());
	}

	/**
	 * Delete or remove selected work shops from the SmFacility list of the
	 * application.
	 */
	public void deleteSelectedWorkShops() {
		if (getManagedBean().getSelectedWorkShopFacilities().size() > 0) {
			for (SmFacility sm : getManagedBean()
					.getSelectedWorkShopFacilities()) {
				getManagedBean().getSmAppl().getSmFacilities().remove(sm);
				getManagedBean().getApplWorkShopFacilities().remove(sm);
			}
			getManagedBean().getSelectedWorkShopFacilities().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Workshop" });
		}

	}

	// Workshop end
	// Store start
	/**
	 * Add or update selected store info to SmFacility list of the application.
	 */
	public void addOrUpdateStoreToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedStoreFacility();
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getSmAppl().getSmFacilities());
		EntityHelper.addOrUpdateEntityToList(smFac, getManagedBean()
				.getApplStoreFacilities());
		if (smFac.getFacilityId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smFacStoreDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("storeDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the store information dialog
	 */
	public void cancelStoreSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:storeDialog");
		RequestContext.getCurrentInstance().execute("storeDialog.hide()");
	}

	/**
	 * Assign selected store - facility object to managed bean for editing the
	 * information.
	 * 
	 * @param facilityBean
	 *            the facility bean
	 */
	public void assignSelStore(SmFacility facilityBean) {
		SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
				getManagedBean(), facilityBean.getSmAddress(),
				SupplierManagementConst.SM_STORE_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		getManagedBean().setSelectedStoreFacility(
				(SmFacility) SerializationUtils.clone(facilityBean));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.store", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("storeDialog.show()");
	}

	/**
	 * Add or create a new SmFacility object of type Store
	 */
	public void assignNewStoreBean() {
		getManagedBean().setSelectedStoreFacility(new SmFacility());
		setFacilityDefaultPrices(getManagedBean().getSelectedStoreFacility());
		getManagedBean().getSelectedStoreFacility().setRevNo(1);
		getManagedBean().getSelectedStoreFacility().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry())));
		getManagedBean().getSelectedStoreFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedStoreFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_STORE);
		getManagedBean().setStoreFacilityStates(
				SmCommonUtil.getStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.store", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("storeDialog.show()");
	}

	/**
	 * Store owner type selected on screen.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void storeOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedStoreFacility()
						.getOwnershipType());
	}

	/**
	 * Delete or remove selected stores from the SmFacility list of the
	 * application.
	 */
	public void deleteSelectedStores() {

		if (getManagedBean().getSelectedStoreFacilities().size() > 0) {
			for (SmFacility sm : getManagedBean().getSelectedStoreFacilities()) {
				getManagedBean().getSmAppl().getSmFacilities().remove(sm);
				getManagedBean().getApplStoreFacilities().remove(sm);
			}
			getManagedBean().getSelectedStoreFacilities().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Store" });
		}

	}

	// Store end
	// manufacturer start
	/**
	 * Add or update selected bank info to SmManufacturingDtl list of the
	 * application.
	 */
	public void addOrUpdateManufacturerToSmAppl() {
		if (getManagedBean().getSelectedManufacturer().getPmUom().getUomId() != null) {
			for (PmUom uom : getManagedBean().getUomLs()) {
				if (uom.getUomId().equals(
						getManagedBean().getSelectedManufacturer().getPmUom()
								.getUomId())) {
					getManagedBean().getSelectedManufacturer().setPmUom(uom);
					break;
				}
			}
		}
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedManufacturer(), getManagedBean().getSmAppl()
				.getSmManufacturingDtls());
		if (getManagedBean().getSelectedManufacturer().getManufacturingDtlId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smManuDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("manufactorDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the manufacturer information
	 * dialog
	 */
	public void cancelManuSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:manufactorDialog");
		RequestContext.getCurrentInstance().execute("manufactorDialog.hide()");
	}

	/**
	 * Assign selected manufacturer object to managed bean for editing the
	 * information.
	 * 
	 * @param manBean
	 *            - SmManufacturingDtl object
	 */
	public void assignSelManufacturer(SmManufacturingDtl manBean) {
		getManagedBean().setSelectedManufacturer(manBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("manufactorDialog.show()");
	}

	/**
	 * Add or create a new SmManufacturingDtl object
	 */
	public void assignNewManufacturerBean() {
		log.info("**** Add new Manufacturer *********");
		SmManufacturingDtl manu = new SmManufacturingDtl();
		manu.setPmUom(new PmUom());
		manu.setRevNo(1);
		manu.setSmAppl(getManagedBean().getSmAppl());
		getManagedBean().setSelectedManufacturer(manu);
		getManagedBean().setUomSelectItems(
				SmApplicationUtil.getUomSelectItems());
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("manufactorDialog.show()");
		log.info("****** After **********"
				+ getManagedBean().getSelectedManufacturer().getPmUom()
						.getUomName());

	}

	/**
	 * Delete or remove selected manufactures from the SmManufacturingDtl list
	 * of the application.
	 */
	public void deleteSelectedManufacturers() {

		if (getManagedBean().getSelectedManufacturingList().size() > 0) {
			for (SmManufacturingDtl man : getManagedBean()
					.getSelectedManufacturingList()) {
				getManagedBean().getSmAppl().getSmManufacturingDtls()
						.remove(man);
			}
			getManagedBean().getSelectedManufacturingList().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Manufacturer" });
		}
	}

	// manufacturer end

	// machinery start
	/**
	 * Add or update selected machinery info to SmMachineDtls list of the
	 * application.
	 */
	public void addOrUpdateMachineToSmAppl() {
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedMachine(), getManagedBean().getSmAppl()
				.getSmMachineDtls());
		if (getManagedBean().getSelectedMachine().getMachineDtlId() == null) {
			RequestContext.getCurrentInstance().execute(
					"smMachDtWidVar.paginator.endLink.click()");
		}
		saveOrUpdateAppl();
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("equipmentDialog.hide()");
	}

	/**
	 * Cancel add or update functionality and close the machine dtl information
	 * dialog
	 */
	public void cancelMachineSaveorUpdate() {
		assignValReqdToFalse();
		if (getManagedBean().getSelectedMachine().getMachineDtlId() == null) {
			getManagedBean().getSelectedMachine().setValue(
					new BigDecimal(SupplierManagementConst.MIN_AMT_RANGE));
		} else {
			getManagedBean().getSelectedMachine().setValue(
					getManagedBean().getRentPrevVal());
		}

		RequestContext.getCurrentInstance().reset(":form:tt:equipmentDialog");
		RequestContext.getCurrentInstance().execute("equipmentDialog.hide()");
	}

	/**
	 * Assign selected machine object to managed bean for editing the
	 * information.
	 * 
	 * @param macBean
	 *            - SmMachineDtl object
	 */
	public void assignSelMachinery(SmMachineDtl macBean) {
		getManagedBean().setSelectedMachine(macBean);
		getManagedBean().setRentPrevVal(macBean.getValue());
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equipmentDialog.show()");
	}

	/**
	 * Add or create a new SmMachineDtl object
	 */
	public void assignNewMachineryBean() {
		getManagedBean().setSelectedMachine(new SmMachineDtl());
		getManagedBean().getSelectedMachine().setRevNo(1);
		getManagedBean().getSelectedMachine().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedMachine().setValue(new BigDecimal(0.00));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equipmentDialog.show()");
	}

	/**
	 * Delete or remove selected machinery from the SmMachineDtl list of the
	 * application.
	 */
	public void deleteSelectedMachinery() {

		if (getManagedBean().getSelectedMachineList().size() > 0) {
			for (SmMachineDtl mac : getManagedBean().getSelectedMachineList()) {
				getManagedBean().getSmAppl().getSmMachineDtls().remove(mac);
			}
			getManagedBean().getSelectedMachineList().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.machine.or.equipment", null) });
		}
	}

	// manufacturer end

	// START:Supplier System

	/**
	 * Add or create a new SmSupplierSystem object
	 */
	public void assignNewSystemBean() {
		getManagedBean().setOthersSystem(false);
		getManagedBean().setSelectedSystem(new SmSupplierSystem());
		getManagedBean().getSelectedSystem().setSystem(new PmMachine());
		getManagedBean().getSelectedSystem().setRevNo(1);
		getManagedBean().getSelectedSystem().setSmAppl(
				getManagedBean().getSmAppl());
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("systemDialog.show()");
	}

	/**
	 * Assign selected system object to managed bean for editing the
	 * information.
	 * 
	 * @param systemBean
	 *            - SmSupplierSystem object
	 */
	public void assignSelSystem(SmSupplierSystem systemBean) {
		if (StringUtils.isBlank(systemBean.getSystemName())) {
			getManagedBean().setOthersSystem(false);
		} else {
			getManagedBean().setOthersSystem(true);
		}
		getManagedBean().setSelectedSystem(
				(SmSupplierSystem) SerializationUtils.clone(systemBean));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("systemDialog.show()");
	}

	/**
	 * Cancel add or update functionality and close the system information
	 * dialog
	 */
	public void cancelSystemSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:systemDialog");
		RequestContext.getCurrentInstance().execute("systemDialog.hide()");
	}

	/**
	 * Add or update selected travelling system info to SmSupplierSystem list of
	 * the application.
	 */
	public void addOrUpdateSystemToSmAppl() {
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedSystem(), getManagedBean().getSmAppl()
				.getSmSupplierSystems());
		if (getManagedBean().getSelectedSystem().getSupplierSystemId() == null) {
			RequestContext.getCurrentInstance().execute(
					"systemDtWidVar.paginator.endLink.click()");
		}
		saveOrUpdateAppl();
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("systemDialog.hide()");
	}

	/**
	 * Delete or remove selected travelling system from the SmSupplierSystem
	 * list of the application.
	 */
	public void deleteSelectedSystem() {

		if (getManagedBean().getSelectedSupplierSystemList().size() > 0) {
			for (SmSupplierSystem system : getManagedBean()
					.getSelectedSupplierSystemList()) {
				getManagedBean().getSmAppl().getSmSupplierSystems()
						.remove(system);
			}
			getManagedBean().getSelectedSupplierSystemList().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.travelling.system", null) });
		}
	}

	/**
	 * Get Machine description by id
	 * 
	 * @param Long
	 *            machineId
	 * @return String
	 */
	public String getMachineDescById(Long machineId) {
		PmMachine machine = SmCommonUtil.getInstance()
				.getMachineById(machineId);
		if (machine != null) {
			return machine.getMachineDesc();
		} else {
			return "";
		}
	}

	/**
	 * Reset selected software name when click other software checkbox
	 */
	public void onTickOtherSystem() {
		SmSupplierSystem system = (SmSupplierSystem) EntityHelper
				.getEntityByMultiplePropertiesFromList(getManagedBean()
						.getSelectedSystem().getSupplierSystemId(),
						getManagedBean().getSmAppl().getSmSupplierSystems(),
						new String[] { "supplierSystemId" }, null, null);
		if (system != null) {
			if (getManagedBean().isOthersSystem()) {
				getManagedBean().getSelectedSystem().setSystem(null);
			} else {
				getManagedBean().getSelectedSystem().setSystemName("");
				if (system.getSystem() != null) {
					getManagedBean().getSelectedSystem().setSystem(
							system.getSystem());
				} else {
					getManagedBean().getSelectedSystem().setSystem(
							new PmMachine());
				}
			}
		} else {
			if (getManagedBean().isOthersSystem()) {
				getManagedBean().getSelectedSystem().setSystem(null);
			} else {
				getManagedBean().getSelectedSystem().setSystemName("");
				getManagedBean().getSelectedSystem().setSystem(new PmMachine());
			}
		}
	}

	// END:Supplier Sysem

	// -- Process Share Holder Personnel Start
	/**
	 * Add or update selected personnel info to SmPersonnel of the application.
	 * 
	 * @throws ParseException
	 *             the parse exception
	 */
	public void addOrUpdateShareHolderToSmAppl() throws ParseException {
		boolean validShrHldr = true;
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			calculateShareHolderPercent();
		}

		getManagedBean().setIdentificationTypes(getPmIdTypeDropdownValues());
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& getManagedBean()
						.getSelectedShareHolder()
						.getWorkExperienceYear()
						.compareTo(
								new BigDecimal(ruleHelper
										.getConsutantPersonnelMinExp())) < 0
				&& getManagedBean().getSelectedShareHolder().isEquityOwner()) {

			validShrHldr = false;
			throw new EpException(MessageConst.ERROR_722518);
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& getManagedBean().getSelectedShareHolder().isEquityOwner()
				&& (getManagedBean().getSelectedShareHolder()
						.getSmStaffExperiences() == null || getManagedBean()
						.getSelectedShareHolder().getSmStaffExperiences()
						.isEmpty())) {

			validShrHldr = false;
			throw new EpException(SmCommonUtil.getMessage(
					"lbl.sm.jobexperience.required.atleast.one", null));
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& (getManagedBean().getSelectedShareHolder()
						.getSmStaffAcademics() == null || getManagedBean()
						.getSelectedShareHolder().getSmStaffAcademics()
						.isEmpty())) {

			validShrHldr = false;
			throw new EpException(MessageConst.ERROR_722575);
		}
		addOrUpdateShareHolderAddress(getManagedBean().getSelectedShareHolder());
		validShrHldr = SmApplicationUtil.checkValidUser(getManagedBean()
				.getSelectedShareHolder(), getManagedBean().getSmAppl());

		// validate email domain
		if (getManagedBean().getSelectedShareHolder() != null) {
			String email = getManagedBean().getSelectedShareHolder()
					.getPmUser() != null
					&& StringUtils.isNotBlank(getManagedBean()
							.getSelectedShareHolder().getPmUser().getEmail()) ? getManagedBean()
					.getSelectedShareHolder().getPmUser().getEmail()
					: getManagedBean().getSelectedShareHolder().getEmail();
			SmCommonUtil.checkEmailDomainWithExceptionMsg(getManagedBean()
					.getSelectedShareHolder().getName(), email);
		}

		getManagedBean().getSelectedShareHolder().setSmAppl(
				getManagedBean().getSmAppl());
		if (validShrHldr) {
			if (getManagedBean().getSelectedShareHolder().getPmRace() != null
					&& getManagedBean().getSelectedShareHolder().getPmRace()
							.getRaceId() == null) {
				getManagedBean().getSelectedShareHolder().setPmRace(null);
			}
			if (getManagedBean().getSelectedShareHolder().getTitle() != null
					&& getManagedBean().getSelectedShareHolder().getTitle()
							.getSalutationId() == null) {
				getManagedBean().getSelectedShareHolder().setTitle(null);
			}
			EntityHelper.addOrUpdateEntityToList(getManagedBean()
					.getSelectedShareHolder(), getManagedBean().getSmAppl()
					.getSmPersonnels());
			if (getManagedBean().getSelectedShareHolder().getPersonnelId() == null) {
				if (getManagedBean().getSelectedShareHolder().isEquityOwner()
						&& getManagedBean()
								.getCurrentPageType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
					RequestContext.getCurrentInstance().execute(
							"suppCapShrHldrDtWidVar.paginator.endLink.click()");
				} else {
					RequestContext
							.getCurrentInstance()
							.execute(
									"suppPersonnelInfoDtWidVar.paginator.endLink.click()");
				}
			}
			saveOrUpdateAppl();
			SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
					getManagedBean().getSmAppl(), getManagedBean()
							.getPrevApprovedPersonnels(), getManagedBean()
							.isDisAllowOwnerEdit());
			if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
					SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
				calculateCapital();
				getManagedBean().setRedirectToPersonPage(false);
			}
			if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
					SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
				log.info("****** Calc countf ******* ");
				calcPersonnelListCount();
				getManagedBean().setRedirectToPersonPage(false);

			}

		}

	}

	/**
	 * Save or update the latest information of the application i.e. SmAppl .
	 */
	private void saveOrUpdateAppl() {
		// SmApplicationUtil.getInstance().setApplAddressTransientValue(
		// getManagedBean().getSmAppl(), true);
		updatePersonnelRecordStatus(getManagedBean().getSmAppl()
				.getSmPersonnels(), getManagedBean().getSmSupplier());
		SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
				.getCompanyBasicGst());
		SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
				.getSelectedBranch().getGst());
		SmApplicationUtil.getInstance()
				.removeNotApplicableInformationBeforeSave(getManagedBean());
		try {
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSmAppl(this.getManagedBean().getSmAppl());
			reqDto.setCompanyBasicGst(getManagedBean().getCompanyBasicGst());
			reqDto.setBranchGst(getManagedBean().getSelectedBranch().getGst());
			reqDto.setSameAsHq(getManagedBean().getSelectedBranch()
					.isSameAsHq());
			SmServiceLookup.getMofRegistrationSO().updateApplication(reqDto);
		} finally {
			retriveLatestApplAfterUpdate("", "");
		}
	}

	/**
	 * Update SmPersonnel record status just in case after change epRole not
	 * reflect.
	 * 
	 * @param smPersonnels
	 *            - List of sm personnels
	 */
	private void updatePersonnelRecordStatus(List<SmPersonnel> smPersonnels,
			SmSupplier smSupplier) {
		if (smPersonnels != null && !smPersonnels.isEmpty()) {
			for (SmPersonnel smPersonnel : smPersonnels) {
				if (smPersonnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_SUSPENDED
						&& smPersonnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					smPersonnel
							.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					if (StringUtils.isNotBlank(smPersonnel.getEpRole())
							&& (smPersonnel.getPmUser() == null || smPersonnel
									.getPmUser().getUserId() == null)) {
						smPersonnel
								.setRecordStatusFlg(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
					}
				}
			}
		}
	}

	/**
	 * To check whether need to disable ep role drop down
	 * 
	 * @param smPersonnel
	 * @return boolean
	 */
	public boolean disableEpRoleDropDown(SmPersonnel smPersonnel) {
		boolean retVal = false;
		SmPersonnel prevPersonnel = getPrevApprovedPersonnel(smPersonnel);
		if (StringUtils.isNotBlank(smPersonnel.getEpRole())
				&& (smPersonnel.getEpRole().equals(
						RoleCode.MOF_SUPPLIER_ADMIN.value())
						|| smPersonnel.getEpRole().equals(
								RoleCode.SUPPLIER_TEMP.value()) || smPersonnel
						.getEpRole().equals(
								RoleCode.BASIC_SUPPLIER_ADMIN.value()))) {
			retVal = true;
		} else if (prevPersonnel != null
				&& StringUtils.isNotBlank(prevPersonnel.getEpRole())
				&& prevPersonnel.getEpRole().equals(
						RoleCode.MOF_SUPPLIER_USER.value())
				&& prevPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
				&& (prevPersonnel.getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ || smPersonnel
						.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING)) {
			retVal = true;
		} else if (!retVal && prevPersonnel != null
				&& !prevPersonnel.getEpRole().equals(RoleCode.CM_USER.value())) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * To redirect from personnel information page the main screen when click
	 * Back.
	 */
	public void backFromPersonnelPage() {
		log.info("*** Back to perssonel page ******");
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			calculateCapital();
		}
		getManagedBean().setRedirectToPersonPage(false);
		RequestContext.getCurrentInstance().reset(":form:tt:shareHolderDialog");
	}

	/**
	 * Assign selected SmPersonnel object to managed bean for editing the
	 * information.
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public void assignSelShareHolder(SmPersonnel shareHolderBean) {
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			validatePaidCaptialAmt(false);
			getManagedBean().setPersonnelInfoHeader(
					SmCommonUtil.getMessage("lbl.sm.equity.owner.information",
							null));
		} else if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
			getManagedBean().setPersonnelInfoHeader(
					SmCommonUtil.getMessage("lbl.sm.person.panHdr", null));
		}

		if (shareHolderBean.getPmUser() != null
				&& shareHolderBean.getPmUser().getUserId() != null
				&& shareHolderBean.getPmUser().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE)
			getManagedBean().setDisableIcEmail(true);
		else
			getManagedBean().setDisableIcEmail(false);

		getManagedBean().setIdentificationTypes(getPmIdTypeDropdownValues());
		if (shareHolderBean.getEpRole() != null
				&& (shareHolderBean.getEpRole().equalsIgnoreCase(
						RoleCode.SUPPLIER_TEMP.value())
						|| shareHolderBean.getEpRole().equalsIgnoreCase(
								RoleCode.MOF_SUPPLIER_USER.value())
						|| shareHolderBean.getEpRole().equalsIgnoreCase(
								RoleCode.MOF_SUPPLIER_ADMIN.value()) || shareHolderBean
						.getEpRole().equalsIgnoreCase(RoleCode.CM_USER.value()))) {
			getManagedBean().setUserWithRole(true);
		} else {
			getManagedBean().setUserWithRole(false);
		}
		getManagedBean().setSelectedShareHolder(
				(SmPersonnel) SerializationUtils.clone(shareHolderBean));
		if (getManagedBean().getSelectedShareHolder().getPmCountry() == null) {
			getManagedBean().getSelectedShareHolder().setPmCountry(
					new PmCountry());
		}
		if (getManagedBean().getSelectedShareHolder().getPmRace() == null) {
			getManagedBean().getSelectedShareHolder().setPmRace(new PmRace());
		}
		if (getManagedBean().getSelectedShareHolder().getTitle() == null) {
			getManagedBean().getSelectedShareHolder().setTitle(
					new PmSalutation());
		}
		if (getManagedBean().getSelectedShareHolder().getEpRole() == null) {
			getManagedBean().getSelectedShareHolder().setEpRole("");
		}
		assignSelShareHolderAddress(getManagedBean().getSelectedShareHolder());
		managePersonnelRoles("Y");
		manageEquityLimit(getManagedBean().getSelectedShareHolder());
		if (getManagedBean().getSelectedShareHolder().getIdentificationTypeId() != null) {
			getManagedBean().getSelectedShareHolder().setIdentificationType(
					SmApplicationUtil
							.retrievePmParameterById(getManagedBean()
									.getSelectedShareHolder()
									.getIdentificationTypeId()));
		}
		if (getManagedBean().getSelectedShareHolder().getIdentificationType() == null) {
			getManagedBean().getSelectedShareHolder().setIdentificationType(
					new PmParameter());
		}
		getManagedBean().setRedirectToPersonPage(true);
		getManagedBean().setRemoveEpRole(false);
		buildRoleList(getManagedBean().getSelectedShareHolder());
		if (getManagedBean().getSelectedShareHolder().getEpRole() != null
				&& (getManagedBean().getSelectedShareHolder().getEpRole()
						.equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value()) || getManagedBean()
						.getSelectedShareHolder().getEpRole()
						.equalsIgnoreCase(RoleCode.SUPPLIER_TEMP.value()))) {
			SelectItem role = new SelectItem();
			role.setLabel(SmCommonUtil.getMessage(
					"lbl.sm.person.composition.suppAdmin", null));
			role.setValue(getManagedBean().getSelectedShareHolder().getEpRole());
			getManagedBean().getePRoleList().add(role);
		}

		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& (getManagedBean().getSelectedShareHolder().getEpRole() == null
						|| getManagedBean().getSelectedShareHolder()
								.getEpRole().isEmpty() || getManagedBean()
						.getSelectedShareHolder().getEpRole()
						.equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value()))) {
			getManagedBean().getSelectedShareHolder().setEpRole(
					RoleCode.MOF_SUPPLIER_USER.value());
		}

		// to determine whether shareholder for viewonly or edit
		setShareHolderForViewOnly(shareHolderBean);

		// disable shareholder/personnel name and nationality if already
		// activate (have login id) or created in basic
		setDisabledShareholderNameAndNationality(shareHolderBean);

		// set personnel field change value
		SmFieldChangeUtil.initPersonnelChangeField(getManagedBean());

	}

	/**
	 * Getys Authorized Personnel Account Status
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public String getPersonnelActivationStatus(SmPersonnel smPersonnel,
			SmSupplier smSupplier) {
		String accStatus = "";
		if (smSupplier != null && smSupplier.getSupplierId() != null
				&& smPersonnel.getEpRole() != null
				&& StringUtils.isNotBlank(smPersonnel.getEpRole())) {

			if (smPersonnel.getPersonnelId() != null
					&& SmApplicationUtil.isLatestApprovedPersonnelActivated(
							smPersonnel.getIdentificationNo(),
							smSupplier.getSupplierId())) {
				accStatus = SmCommonUtil.getMessage(
						"lbl.sm.activation.status.activated", null);
			} else {
				accStatus = SmCommonUtil.getMessage(
						"lbl.sm.activation.status.pending", null);
			}
		}
		return accStatus;
	}

	@SuppressWarnings("unused")
	private void disableJustification(SmPersonnel shareHolderBean) {
		getManagedBean().setDisabledJustification(false);
		if (getManagedBean().getSmAppl() != null && shareHolderBean != null
				&& shareHolderBean.getSmAppl() != null) {
			if (getManagedBean().getSmAppl().getApplId()
					.compareTo(shareHolderBean.getSmAppl().getApplId()) == 0) {
				getManagedBean().setDisabledJustification(true);
			}
		}
	}

	/**
	 * Adds or update addresses information to the selected SmPersonnel
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 * @throws ParseException
	 *             the parse exception
	 */
	public void addOrUpdateShareHolderAddress(SmPersonnel shareHolderBean)
			throws ParseException {

		// validate address if role is staff
		validateSaveAddressForRoleStaff();

		if (shareHolderBean.getAuthorizedFlg() == 0) {
			getManagedBean().getShareHolderMailAddr().setSameAsPerm(true);
		}

		if (!shareHolderBean.isDirector()
				&& !shareHolderBean.isHigherManagement()) {
			getManagedBean().getShareHolderOffAddr().setSameAsPerm(true);
			getManagedBean().getShareHolderResdAddr().setSameAsPerm(true);
		}

		// clone the addrs start
		SmApplicationUtil.cloneShareHolderAddress(getManagedBean());
		// clone the addrs end

		if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {
			processAddNewAddress(shareHolderBean, getManagedBean()
					.getShareHolderPermAddr());
			processAddNewAddress(shareHolderBean, getManagedBean()
					.getShareHolderMailAddr());
			processAddNewAddress(shareHolderBean, getManagedBean()
					.getShareHolderOffAddr());
			processAddNewAddress(shareHolderBean, getManagedBean()
					.getShareHolderResdAddr());
		} else {

			for (SmPersonnelAddress addr : shareHolderBean
					.getSmPersonnelAddresses()) {
				processUpdatedAddressList(addr, shareHolderBean);
			}
			processUpdatedAddress(shareHolderBean, getManagedBean()
					.getShareHolderPermAddr());
			processUpdatedAddress(shareHolderBean, getManagedBean()
					.getShareHolderMailAddr());
			processUpdatedAddress(shareHolderBean, getManagedBean()
					.getShareHolderOffAddr());
			processUpdatedAddress(shareHolderBean, getManagedBean()
					.getShareHolderResdAddr());
		}
		getManagedBean().setSelectedShareHolder(shareHolderBean);

	}

	/**
	 * Builds the role list.
	 */
	public void buildRoleList(SmPersonnel personnel) {
		String epRole = StringUtils.isNotBlank(personnel.getEpRole()) ? personnel
				.getEpRole() : "";
		boolean showFlUserRole = false;
		boolean showSuppUserRole = false;
		List<SelectItem> roles = new ArrayList<SelectItem>();
		SelectItem role = new SelectItem();

		// if already reach max authorized personnel
		if (getManagedBean().isValidSmUserLimit()) {
			// Show FL_USER if not authorized
			if (personnel.getAuthorizedFlg() != DbValueConst.RECORD_STATUS_ACTIVE) {
				showFlUserRole = true;
			}
			// if role is MOF_SUPPLIER_USER, show MOF_SUPPLIER_USER
			if (epRole.equals(RoleCode.MOF_SUPPLIER_USER.value())) {
				showSuppUserRole = true;
			}
		}
		// if not reach max authorized limit (MOF_SUPPLIER_USER will always
		// show)
		else {
			showSuppUserRole = true;
			// if role is FL_USER, show FL_USER
			if (epRole.equals(RoleCode.FL_USER.value())) {
				showFlUserRole = true;
			}
		}

		if (showSuppUserRole) {
			role = new SelectItem();
			role.setLabel(SmCommonUtil.getMessage(
					"lbl.sm.person.composition.suppUser", null));
			role.setValue(RoleCode.MOF_SUPPLIER_USER.value());
			roles.add(role);
		}

		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)
				&& getManagedBean().getSmAppl().getSmCompanyBasic() != null
				&& getManagedBean().getSmAppl().getSmCompanyBasic()
						.isWithFederal()) {
			role = new SelectItem();
			role.setLabel(SmCommonUtil.getMessage(
					"lbl.sm.person.composition.catUser", null));
			role.setValue(RoleCode.CM_USER.value());
			roles.add(role);
		}

		if (showFlUserRole) {
			role = new SelectItem();
			role.setLabel(SmCommonUtil.getMessage(
					"lbl.sm.person.composition.fluser", null));
			role.setValue(RoleCode.FL_USER.value());
			roles.add(role);
		}

		if (personnel.getEpRole() != null
				&& (personnel.getEpRole().equalsIgnoreCase(
						RoleCode.MOF_SUPPLIER_ADMIN.value()) || personnel
						.getEpRole().equalsIgnoreCase(
								RoleCode.SUPPLIER_TEMP.value()))) {
			role = new SelectItem();
			role.setLabel(SmCommonUtil.getMessage(
					"lbl.sm.person.composition.suppAdmin", null));
			role.setValue(personnel.getEpRole());
			roles.add(role);
		}
		getManagedBean().setePRoleList(roles);
	}

	/**
	 * Add or create a new SmPersonnel bank object
	 * 
	 * @param personelType
	 *            - the personel type to define equity or not
	 */
	public void assignNewShareHolder(String personelType) {
		log.info("********* assignNewShareHolder *****");
		SmPersonnel shr = new SmPersonnel();
		shr.setEpRole("");
		shr.setShareAmt(new BigDecimal(0));
		shr.setSharePercentage(new BigDecimal(0));
		setDefaultPersonnelValues(shr);
		getManagedBean().setDisableIcEmail(false);
		getManagedBean().setIdentificationTypes(getPmIdTypeDropdownValues());

		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			validatePaidCaptialAmt(false);
			manageEquityLimit(shr);
			if (!getManagedBean().isValidEquityLimit()) {
				if (getManagedBean()
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT)) {
					throw new EpException(
							MessageConst.ERROR_722547,
							new String[] { String.valueOf(ruleHelper
									.getMaxEquityOwnerCountForROBIndividual()) });
				} else {
					throw new EpException(
							MessageConst.ERROR_722547,
							new String[] { String.valueOf(ruleHelper
									.getMaxEquityOwnerCountForOtherThanROBIndividaul()) });
				}

			}
			shr.setEquityOwner(true);
			getManagedBean().setPersonnelInfoHeader(
					SmCommonUtil.getMessage("lbl.sm.equity.owner.information",
							null));
		} else if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
			manageEquityLimit(shr);
			getManagedBean().setPersonnelInfoHeader(
					SmCommonUtil.getMessage("lbl.sm.person.panHdr", null));
		}
		assignSelShareHolderAddress(shr);

		// display justification
		shr.setJustificationReqd(SmApplicationUtil
				.getSmAuthorizedPersonnelCountVO(
						getManagedBean().getSmSupplier().getSupplierId(),
						getManagedBean().getSmAppl().getApplType(),
						getManagedBean().getSmAppl().getApplId(), true)
				.isJustificationRequired());

		getManagedBean().setSelectedShareHolder(shr);
		if (getManagedBean().isValidSmUserLimit()) {
			getManagedBean().setUserWithRole(false);
		} else {
			getManagedBean().setUserWithRole(true);
		}
		buildRoleList(getManagedBean().getSelectedShareHolder());
		getManagedBean().setRedirectToPersonPage(true);

		// set default nationality to MALAYSIA
		getManagedBean()
				.getSelectedShareHolder()
				.setPmCountry(
						SmCommonUtil
								.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE));

		// reset personnel view only to false for add new
		getManagedBean().setShowPersonnelViewOnly(false);

		// reset personnel name and nationality to false for add new
		getManagedBean().setDisabledShareholderPersonnelNameAndNationality(
				false);

		// reset previous approved personnel because not exist for new personnel
		getManagedBean().setLatestPersonnel(null);

	}

	/**
	 * Delete or remove selected personnel from the SmPeronnel list of the
	 * application.
	 * 
	 * @param personType
	 *            - the person type
	 */
	public void deleteSelectedShareHolders(String personType) {
		boolean existAsBankSignatory = false;
		boolean existAsBranchPoc = false;
		for (SmPersonnel shr : getManagedBean().getSelectedShareHolders()) {
			if (shr.getEpRole() != null
					&& (shr.getEpRole().equalsIgnoreCase(
							RoleCode.SUPPLIER_TEMP.value())
							|| shr.getEpRole().equalsIgnoreCase(
									RoleCode.MOF_SUPPLIER_ADMIN.value()) || shr
							.getEpRole().equalsIgnoreCase(
									RoleCode.BASIC_SUPPLIER_ADMIN.value()))) {
				throw new EpException(MessageConst.ERROR_722553);
			}
			if (SmApplicationUtil.isPersonnelBankSignatory(shr,
					getManagedBean().getSmAppl().getSmSupplierBanks())) {
				existAsBankSignatory = true;
			}
			if (SmApplicationUtil.isPersonnelBranchPointOfContact(shr,
					getManagedBean().getSmAppl().getSmSupplierBranches())) {
				existAsBranchPoc = true;
			}

			if (existAsBankSignatory && existAsBranchPoc) {
				throw new EpException(MessageConst.ERROR_722588);
			} else {
				if (existAsBankSignatory) {
					throw new EpException(MessageConst.ERROR_722585);
				} else if (existAsBranchPoc) {
					throw new EpException(MessageConst.ERROR_722586);
				}
			}
		}
		log.info("*** Delete Share Holders ******* "
				+ getManagedBean().getSelectedShareHolders().size());
		if (getManagedBean().getSelectedShareHolders().size() > 0) {
			if (personType
					.equalsIgnoreCase(SupplierManagementConst.PERSONNEL_TYPE_EQUITY)) {
				validatePaidCaptialAmt(false);
			}
			for (SmPersonnel shr : getManagedBean().getSelectedShareHolders()) {
				getManagedBean().getSmAppl().getSmPersonnels().remove(shr);
				getManagedBean().getSmapplShareHolders().remove(shr);
			}
			saveOrUpdateAppl();
			SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
					getManagedBean().getSmAppl(), getManagedBean()
							.getPrevApprovedPersonnels(), getManagedBean()
							.isDisAllowOwnerEdit());
			getManagedBean().getSelectedShareHolders().clear();
			if (!personType
					.equalsIgnoreCase(SupplierManagementConst.PERSONNEL_TYPE_EQUITY)) {
				calcPersonnelListCount();
			} else {
				calculateCapital();
			}
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage("lbl.sm.personnel",
							null) });
		}

	}

	// -- Process Share Holder Personnel End
	// -- Process Common for both Personnel and Share Holder Start --
	/**
	 * Add or create a new sm staff experience object
	 */
	public void addShrHldrJobFunction() {
		log.info("******* getManagedBean().getSelectedShareHolder() **********"
				+ getManagedBean().getSelectedShareHolder().getName());
		SmStaffExperience exp = new SmStaffExperience();
		exp.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		getManagedBean().setSelectedJobFunction(exp);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.show()");
	}

	/**
	 * Assign selected job function - SmStaffExperience object to managed bean
	 * for editing the information.
	 * 
	 * @param exp
	 *            - SmStaffExperience object
	 */
	public void assignSelShrHldrJobFunc(SmStaffExperience exp) {
		getManagedBean().setSelectedJobFunction(exp);
		if (getManagedBean().getLatestPersonnel() != null) {
			getManagedBean().setLatestJobFunction(
					(SmStaffExperience) EntityHelper
							.getEntityByMultiplePropertiesFromList(exp,
									getManagedBean().getLatestPersonnel()
											.getSmStaffExperiences(),
									new String[] { "companyName", "toYear" },
									null, null));
		}
		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.show()");
		if (getManagedBean().isShowPersonnelViewOnly()) {
			SmCommonUtil.closeBlockUI();
		} else {
			assignValReqdToTrue();
		}
	}

	/**
	 * Add or update selected job function info to SmStaffExperience list of the
	 * selected SmPersonnel.
	 */
	public void addOrUpdateJobFunctions() {
		if (getManagedBean()
				.getSelectedJobFunction()
				.getFromYear()
				.compareTo(
						getManagedBean().getSelectedJobFunction().getToYear()) > 0) {
			throw new EpException(MessageConst.ERROR_722531);

		}
		EntityHelper.addOrUpdateEntityToList(getManagedBean()
				.getSelectedJobFunction(), getManagedBean()
				.getSelectedShareHolder().getSmStaffExperiences());
		if (getManagedBean().getSelectedJobFunction().getStaffExperienceId() == null) {
			RequestContext.getCurrentInstance().execute(
					"suppPrsnJobFunDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		SmFieldChangeUtil.setPersonnelTableListChangeStatus(getManagedBean());

		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.hide()");

	}

	/**
	 * Cancel add or update functionality and close the job function information
	 * dialog
	 */
	public void cancelJobFunctions() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(
				":form:tt:equiJobFunctionInfo");
		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.hide()");
	}

	/**
	 * Delete or remove selected job functions from the SmStaffExperience list
	 * of the selected SmPersonnel
	 */
	public void deleteSelectedShrHldrJobFunction() {
		log.info(" ******* The is size of job holders is ********* "
				+ getManagedBean().getSelectedShrHldrJobFunctions().size());
		if (getManagedBean().getSelectedShrHldrJobFunctions().size() > 0) {
			for (SmStaffExperience exp : getManagedBean()
					.getSelectedShrHldrJobFunctions()) {
				getManagedBean().getSelectedShareHolder()
						.getSmStaffExperiences().remove(exp);

			}
			getManagedBean().getSelectedShrHldrJobFunctions().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.personnel.dash.jobfunction", null) });
		}

	}

	/**
	 * Add or create a new related company object
	 */
	public void addShrHldrRelatedCompany() {
		SmRelatedCompany com = new SmRelatedCompany();
		com.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		getManagedBean().setSelectedRelatedCompany(com);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.show()");
	}

	/**
	 * Assign selected related company - SmRelatedCompany object to managed bean
	 * for editing the information.
	 * 
	 * @param com
	 *            -SmRelatedCompany object
	 */
	public void assignSelShrHldrRelatedCompany(SmRelatedCompany com) {
		getManagedBean().setSelectedRelatedCompany(com);
		if (getManagedBean().getLatestPersonnel() != null) {
			getManagedBean().setLatestRelatedCompany(
					(SmRelatedCompany) EntityHelper
							.getEntityByMultiplePropertiesFromList(com,
									getManagedBean().getLatestPersonnel()
											.getSmRelatedCompanies(),
									new String[] { "regNo" }, null, null));
		}
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.show()");
		if (getManagedBean().isShowPersonnelViewOnly()) {
			SmCommonUtil.closeBlockUI();
		} else {
			assignValReqdToTrue();
		}
	}

	/**
	 * Add or update selected related company info to SmRelatedCompany list of
	 * the selected SmPersonnel.
	 */
	public void addOrUpdateRelatedCompanies() {
		if (getManagedBean().getSelectedShareHolder().getSmRelatedCompanies()
				.contains(getManagedBean().getSelectedRelatedCompany())) {

			// TODO if any validation reqd for edit
		} else {
			getManagedBean().getSelectedShareHolder().getSmRelatedCompanies()
					.add(getManagedBean().getSelectedRelatedCompany());
			RequestContext.getCurrentInstance().execute(
					"suppPrsnRelComDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		SmFieldChangeUtil.setPersonnelTableListChangeStatus(getManagedBean());
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.hide()");

	}

	/**
	 * Delete or remove selected related companies from the SmRelatedCompany
	 * list of the selected SmPersonnel
	 */
	public void deleteSelectedShrHldrRelatedCompanies() {

		if (getManagedBean().getSelectedShrHldrRelatedCompanies().size() > 0) {
			for (SmRelatedCompany rel : getManagedBean()
					.getSelectedShrHldrRelatedCompanies()) {
				getManagedBean().getSelectedShareHolder()
						.getSmRelatedCompanies().remove(rel);
			}
			getManagedBean().getSelectedShrHldrRelatedCompanies().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.personnel.dash.relatedcompany", null) });
		}

	}

	/**
	 * Cancel add or update functionality and close the related company
	 * information dialog
	 */
	public void cancelShrHldrRelatedCompanies() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:equiRelCompInfo");
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.hide()");
	}

	/**
	 * Add or create a new staff academics object for selected Smpersonnel
	 */
	public void addShrHldrAcademicQualification() {

		SmStaffAcademic aca = new SmStaffAcademic();
		aca.setPmEduField(new PmEduField());
		aca.setPmEduLevel(new PmEduLevel());

		aca.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		getManagedBean().setSelectedStaffAcademic(aca);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equiAcadInfo.show()");
	}

	/**
	 * Assign selected staff academics - SmStaffAcademic object to managed bean
	 * for editing the information.
	 * 
	 * @param aca
	 *            -SmStaffAcademic
	 */
	public void assignSelShrHldrAcademics(SmStaffAcademic aca) {
		if (aca.getPmEduLevel() == null) {
			aca.setPmEduLevel(new PmEduLevel());
		}
		getManagedBean().setSelectedStaffAcademic(aca);
		if (getManagedBean().getLatestPersonnel() != null) {
			getManagedBean()
					.setLatestStaffAcademic(
							(SmStaffAcademic) EntityHelper
									.getEntityByMultiplePropertiesFromList(aca,
											getManagedBean()
													.getLatestPersonnel()
													.getSmStaffAcademics(),
											null, new String[] { "pmEduField",
													"pmEduLevel" }, null));
		}
		RequestContext.getCurrentInstance().execute("equiAcadInfo.show()");
		if (getManagedBean().isShowPersonnelViewOnly()) {
			SmCommonUtil.closeBlockUI();
		} else {
			assignValReqdToTrue();
		}

	}

	/**
	 * Delete or remove selected records from the SmStaffAcademics list of the
	 * selected SmPersonnel.
	 */
	public void deleteSelectedShrHldrAcademics() {
		if (getManagedBean().getSelectedShrHldrAcademics().size() > 0) {
			for (SmStaffAcademic aca : getManagedBean()
					.getSelectedShrHldrAcademics()) {
				getManagedBean().getSelectedShareHolder().getSmStaffAcademics()
						.remove(aca);
			}
			getManagedBean().getSelectedShrHldrAcademics().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.personnel.dash.academics", null) });
		}

	}

	/**
	 * Cancel add or update functionality and close the staff academics
	 * information dialog
	 */
	public void cancelShrHldrAcademic() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:equiAcadInfo");
		RequestContext.getCurrentInstance().execute("equiAcadInfo.hide()");
	}

	/**
	 * Add or update selected staff academics info to SmStaffAcademic list of
	 * the selected SmPersonnel.
	 */
	public void addOrUpdateShrHldrAcademic() {
		for (PmEduField edu : getManagedBean().getEduFieldList()) {
			if (getManagedBean().getSelectedStaffAcademic().getPmEduField()
					.getEduFieldId().equals(edu.getEduFieldId())) {
				getManagedBean().getSelectedStaffAcademic().setPmEduField(edu);
				break;
			}
		}
		for (PmEduLevel lvl : getManagedBean().getEduLevelList()) {
			if (getManagedBean().getSelectedStaffAcademic().getPmEduLevel()
					.getEduLevelId().equals(lvl.getEduLevelId())) {
				getManagedBean().getSelectedStaffAcademic().setPmEduLevel(lvl);
				break;
			}
		}

		if (getManagedBean().getSelectedStaffAcademic().getPmEduLevel() != null) {

			String qual = "";
			if (getManagedBean().getSelectedStaffAcademic().getPmEduLevel()
					.getLevelCode() != null) {
				qual = getManagedBean().getSelectedStaffAcademic()
						.getPmEduLevel().getLevelCode();
				if (getManagedBean().getSelectedShareHolder().isProf()) {
					if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_NOTDEFINED)
							|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_LEVEL_CERT)
							|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_LEVEL_DIPLOMA)) {
						throw new EpException(MessageConst.ERROR_722574,
								new String[] { "Professional Staff",
										"BACHELOR'S DEGREE" });
					}
				} else if (getManagedBean().getSelectedShareHolder()
						.isSemiProf()) {
					if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_NOTDEFINED)
							|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_LEVEL_CERT)) {
						throw new EpException(MessageConst.ERROR_722574,
								new String[] { "Semi-Professional Staff",
										"DIPLOMA" });
					}
				} else if (getManagedBean().getSelectedShareHolder()
						.isClerical()) {
					if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_NOTDEFINED)) {
						throw new EpException(
								MessageConst.ERROR_722574,
								new String[] { "Clerical Staff", "CERTIFICATE" });
					}
				}

			}
		}
		if (getManagedBean().getSelectedShareHolder().getSmStaffAcademics()
				.contains(getManagedBean().getSelectedStaffAcademic())) {
			// TODO if any validation reqd for edit
		} else {

			getManagedBean().getSelectedShareHolder().getSmStaffAcademics()
					.add(getManagedBean().getSelectedStaffAcademic());
			RequestContext.getCurrentInstance().execute(
					"suppPrsnAcadDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		SmFieldChangeUtil.setPersonnelTableListChangeStatus(getManagedBean());
		RequestContext.getCurrentInstance().execute("equiAcadInfo.hide()");

	}

	/**
	 * Add or create a new shr hldr prof body object
	 */
	public void addShrHldrProfBody() {

		SmAssociationBody assBody = new SmAssociationBody();
		assBody.setPmCertBody(new PmCertBody());
		assBody.setRevNo(1);
		assBody.setSmAppl(getManagedBean().getSmAppl());
		assBody.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		getManagedBean().setSelectedStfPrfBdy(assBody);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.show()");
	}

	/**
	 * Assign selected professional body - SmAssocication object to managed bean
	 * for editing the information.
	 * 
	 * @param asb
	 *            -SmAssociationBody object
	 */
	public void assignSelShrHldrProfBody(SmAssociationBody asb) {
		getManagedBean().setSelectedStfPrfBdy(asb);
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestStfPrfBdy(
					(SmAssociationBody) EntityHelper
							.getEntityByMultiplePropertiesFromList(asb,
									getManagedBean().getLatestAppl()
											.getSmAssociationBodies(),
									new String[] { "regNo" }, null, null));
		}
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.show()");
		if (getManagedBean().isShowPersonnelViewOnly()) {
			SmCommonUtil.closeBlockUI();
		} else {
			assignValReqdToTrue();
		}

	}

	/**
	 * Cancel add or update functionality and close the professionl body
	 * information dialog
	 */
	public void cancelShrHldrProfBody() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(
				":form:tt:equiShrProfBeanInfo");
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.hide()");
	}

	/**
	 * Add or update selected prof body info to SmAssocationBody list of the
	 * selected SmPersonnel.
	 */
	public void addOrUpdateShrHldrProfBody() {
		for (PmCertBody cert : getManagedBean().getProfBodyLs()) {
			if (getManagedBean().getSelectedStfPrfBdy().getPmCertBody()
					.getCertBodyId().equals(cert.getCertBodyId())) {
				getManagedBean().getSelectedStfPrfBdy().setPmCertBody(cert);
				break;
			}
		}
		if (getManagedBean().getSelectedShareHolder().getSmAssociationBodies()
				.contains(getManagedBean().getSelectedStfPrfBdy())) {

			// TODO if any validation reqd for edit
		} else {
			getManagedBean().getSelectedShareHolder().getSmAssociationBodies()
					.add(getManagedBean().getSelectedStfPrfBdy());
			RequestContext.getCurrentInstance().execute(
					"suppPrsnProfBdyDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		SmFieldChangeUtil.setPersonnelTableListChangeStatus(getManagedBean());
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.hide()");

	}

	/**
	 * Delete or remove selected prof bodies from the SmAssocationBody list of
	 * the selected SmPersonnel.
	 */
	public void deleteSelectedShrHldrProfBodies() {
		if (getManagedBean().getSelectedShrHldrProfBodies().size() > 0) {
			for (SmAssociationBody prof : getManagedBean()
					.getSelectedShrHldrProfBodies()) {
				getManagedBean().getSelectedShareHolder()
						.getSmAssociationBodies().remove(prof);
			}
			getManagedBean().getSelectedShrHldrProfBodies().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.personnel.dash.profbody", null) });
		}
	}

	/**
	 * Assign personnel for sm supplier branch.
	 * 
	 * @param brn
	 *            - SmSupplierBranch object
	 */
	public void assignPersonForBranch(SmSupplierBranch brn) {
		SmSupplierBranch br = new SmSupplierBranch();
		for (int i = 0; i < getManagedBean().getSmAppl()
				.getSmSupplierBranches().size(); i++) {
			br = getManagedBean().getSmAppl().getSmSupplierBranches().get(i);
			if (br.getSupplierBranchId().equals(brn.getSupplierBranchId())) {
				getManagedBean().getSmAppl().getSmSupplierBranches()
						.set(i, brn);
				break;
			}
		}
		saveOrUpdateAppl();
		calcPersonnelListCount();

	}

	/**
	 * Assign selected personnel identity type.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 */
	public void assignPersonIdentType(AjaxBehaviorEvent e) {
		if (getManagedBean().getSelectedShareHolder().getIdentificationTypeId() != null) {
			getManagedBean().getSelectedShareHolder().setIdentificationType(
					SmApplicationUtil
							.retrievePmParameterById(getManagedBean()
									.getSelectedShareHolder()
									.getIdentificationTypeId()));
			if (getManagedBean().getSelectedShareHolder()
					.getIdentificationType() == null) {
				getManagedBean().getSelectedShareHolder()
						.setIdentificationType(new PmParameter());
			}
		} else {
			getManagedBean().getSelectedShareHolder().setIdentificationType(
					new PmParameter());
		}
	}

	/**
	 * Assign selected personnel nationality.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 */
	public void assignPersonNationality(AjaxBehaviorEvent e) {
		if (getManagedBean().getSelectedShareHolder().getPmCountry()
				.getCountryId() != null) {
			log.info(" **** Selected Country Id ***** "
					+ getManagedBean().getSelectedShareHolder().getPmCountry()
							.getCountryId());
			PmCountry cntry = new PmCountry();

			cntry = SmApplicationUtil.retrievePmCountryById(getManagedBean()
					.getSelectedShareHolder().getPmCountry().getCountryId());
			if (cntry != null) {
				getManagedBean().getSelectedShareHolder().setPmCountry(cntry);
			}

			log.info(" **** Selected Country Id ***** "
					+ getManagedBean().getSelectedShareHolder().getPmCountry()
							.getCountryCode());
		}
	}

	/**
	 * Assign selected personnel object to managed bean for editing the
	 * information.
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public void assignSelShareHolderAddress(SmPersonnel shareHolderBean) {
		if (isPersonnelViewOnly(shareHolderBean)
				&& shareHolderBean.getPersonnelId() != null) {
			if (shareHolderBean.getSmPersonnelAddresses() != null
					&& shareHolderBean.getSmPersonnelAddresses().size() > 0) {
				for (SmPersonnelAddress personnelAddress : shareHolderBean
						.getSmPersonnelAddresses()) {
					if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
						if (personnelAddress.getSmAddress() != null
								&& personnelAddress.getSmAddress()
										.getAddressId() != null) {
							getManagedBean().setShareHolderPermAddr(
									personnelAddress);
						}
					} else if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
						if (personnelAddress.getSmAddress() != null
								&& personnelAddress.getSmAddress()
										.getAddressId() != null) {
							getManagedBean().setShareHolderMailAddr(
									personnelAddress);
						}
					} else if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
						if (personnelAddress.getSmAddress() != null
								&& personnelAddress.getSmAddress()
										.getAddressId() != null) {
							getManagedBean().setShareHolderResdAddr(
									personnelAddress);
						}
					} else if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
						if (personnelAddress.getSmAddress() != null
								&& personnelAddress.getSmAddress()
										.getAddressId() != null) {
							getManagedBean().setShareHolderOffAddr(
									personnelAddress);
						}
					}
				}
			}
		} else {
			SmPersonnelAddress perm = new SmPersonnelAddress();
			perm.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM);
			perm.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
			perm.setSmPersonnel(shareHolderBean);
			perm.setSameAsPerm(false);

			SmPersonnelAddress mail = new SmPersonnelAddress();
			mail.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL);
			mail.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
			mail.setSmPersonnel(shareHolderBean);
			mail.setSameAsPerm(false);

			SmPersonnelAddress off = new SmPersonnelAddress();
			off.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE);
			off.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
			off.setSmPersonnel(shareHolderBean);
			off.setSameAsPerm(false);

			SmPersonnelAddress res = new SmPersonnelAddress();
			res.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES);
			res.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
			res.setSmPersonnel(shareHolderBean);
			res.setSameAsPerm(false);

			getManagedBean().setShareHolderPermAddr(perm);
			getManagedBean().setShareHolderMailAddr(mail);
			getManagedBean().setShareHolderOffAddr(off);
			getManagedBean().setShareHolderResdAddr(res);
			List<SelectItem> defStateItems = SmCommonUtil.getStateList(perm
					.getSmAddress().getPmCountry().getCountryId());
			if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {

				// reset to default values for a new record
				getManagedBean().setShrHldrPermStates(defStateItems);
				getManagedBean().setShrHldrPermDistricts(
						new ArrayList<SelectItem>());
				getManagedBean().setShrHldrPermCities(
						new ArrayList<SelectItem>());
				getManagedBean().setShrHldrMailStates(defStateItems);
				getManagedBean().setShrHldrMailDistricts(
						new ArrayList<SelectItem>());
				getManagedBean().setShrHldrMailCities(
						new ArrayList<SelectItem>());
				getManagedBean().setShrHldrResdStates(defStateItems);
				getManagedBean().setShrHldrResdDistricts(
						new ArrayList<SelectItem>());
				getManagedBean().setShrHldrResdCities(
						new ArrayList<SelectItem>());
				getManagedBean().setShrHldrOffStates(defStateItems);
				getManagedBean().setShrHldrOffDistricts(
						new ArrayList<SelectItem>());
				getManagedBean().setShrHldrOffCities(
						new ArrayList<SelectItem>());

			} else {
				for (SmPersonnelAddress addr : shareHolderBean
						.getSmPersonnelAddresses()) {
					SmAddress smAddress = new SmAddress();
					smAddress.setAddress1(addr.getSmAddress().getAddress1());
					smAddress.setAddress2(addr.getSmAddress().getAddress2());
					smAddress.setAddress3(addr.getSmAddress().getAddress3());
					smAddress.setAddress4(addr.getSmAddress().getAddress4());
					PmCountry country = new PmCountry();
					country.setCountryId(addr.getSmAddress().getPmCountry()
							.getCountryId());
					smAddress.setPmCountry(country);
					smAddress.setCountryId(country.getCountryId());
					PmState state = new PmState();
					state.setStateId(addr.getSmAddress().getPmState()
							.getStateId());
					smAddress.setPmState(state);
					smAddress.setStateId(state.getStateId());
					PmDistrict district = new PmDistrict();
					district.setDistrictId(addr.getSmAddress().getPmDistrict()
							.getDistrictId());
					smAddress.setPmDistrict(district);
					smAddress.setDistrictId(district.getDistrictId());
					PmCity city = new PmCity();
					city.setCityId(addr.getSmAddress().getPmCity().getCityId());
					smAddress.setPmCity(city);
					smAddress.setCityId(city.getCityId());
					smAddress.setPostcode(addr.getSmAddress().getPostcode());
					addr.setSmAddress(smAddress);

					if (addr.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
						getManagedBean().setShareHolderPermAddr(addr);
						SmApplicationUtil
								.getInstance()
								.assignStateDistAndCityForSelVal(
										getManagedBean(),
										getManagedBean()
												.getShareHolderPermAddr()
												.getSmAddress(),
										SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL);

					}
					if (addr.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
						getManagedBean().setShareHolderMailAddr(addr);
						SmApplicationUtil
								.getInstance()
								.assignStateDistAndCityForSelVal(
										getManagedBean(),
										getManagedBean()
												.getShareHolderMailAddr()
												.getSmAddress(),
										SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL);

					}
					if (addr.getAddressType().equalsIgnoreCase(
							SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
						getManagedBean().setShareHolderResdAddr(addr);
						SmApplicationUtil
								.getInstance()
								.assignStateDistAndCityForSelVal(
										getManagedBean(),
										getManagedBean()
												.getShareHolderResdAddr()
												.getSmAddress(),
										SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL);

					}
					if (addr.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
						getManagedBean().setShareHolderOffAddr(addr);
						SmApplicationUtil
								.getInstance()
								.assignStateDistAndCityForSelVal(
										getManagedBean(),
										getManagedBean()
												.getShareHolderOffAddr()
												.getSmAddress(),
										SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL);
					}
				}

				if ((getManagedBean().getShrHldrPermStates() == null)
						|| (getManagedBean().getShrHldrPermStates() != null && getManagedBean()
								.getShrHldrPermStates().isEmpty())) {
					getManagedBean().setShrHldrPermStates(defStateItems);
					getManagedBean().setShrHldrPermDistricts(
							new ArrayList<SelectItem>());
					getManagedBean().setShrHldrPermCities(
							new ArrayList<SelectItem>());
				}
				if ((getManagedBean().getShrHldrMailStates() == null)
						|| (getManagedBean().getShrHldrMailStates() != null && getManagedBean()
								.getShrHldrMailStates().isEmpty())) {
					getManagedBean().setShrHldrMailStates(defStateItems);
					getManagedBean().setShrHldrMailDistricts(
							new ArrayList<SelectItem>());
					getManagedBean().setShrHldrMailCities(
							new ArrayList<SelectItem>());
				}
				if ((getManagedBean().getShrHldrResdStates() == null)
						|| (getManagedBean().getShrHldrResdStates() != null && getManagedBean()
								.getShrHldrResdStates().isEmpty())) {
					getManagedBean().setShrHldrResdStates(defStateItems);
					getManagedBean().setShrHldrResdDistricts(
							new ArrayList<SelectItem>());
					getManagedBean().setShrHldrResdCities(
							new ArrayList<SelectItem>());
				}
				if ((getManagedBean().getShrHldrOffStates() == null)
						|| (getManagedBean().getShrHldrOffStates() != null && getManagedBean()
								.getShrHldrOffStates().isEmpty())) {
					getManagedBean().setShrHldrOffStates(defStateItems);
					getManagedBean().setShrHldrOffDistricts(
							new ArrayList<SelectItem>());
					getManagedBean().setShrHldrOffCities(
							new ArrayList<SelectItem>());
				}

			}
		}
		getManagedBean().setSelectedShareHolder(shareHolderBean);
	}

	// -- Process Common for both Personnel and Share Holder Start --
	// -- Process Personnel Start
	// -- Process Personnel End

	// Process Bank Info Start
	/**
	 * Add or update selected bank info to SmSupplierBanks of the application.
	 */
	public void addOrUpdateBankInfoToSmAppl() {
		getManagedBean().setSelectedSignatoryBankAcct(new SmSupplierBank());
		if (!StringUtils.isNumeric(getManagedBean().getSelectedBank()
				.getAccountNo())) {
			throw new EpException(MessageConst.ERROR_722616);
		}

		if (SmApplicationUtil.checkValidBank(
				getManagedBean().getSelectedBank(), getManagedBean()
						.getSmAppl())) {
			// set cheque signatories
			getManagedBean().getSelectedBank().getSmChequeSignatories().clear();
			for (Long perId : getManagedBean().getChequeSingPerId()) {
				for (SmPersonnel sm : getManagedBean().getSmAppl()
						.getSmPersonnels()) {
					if (sm.getPersonnelId().equals(perId)) {
						SmChequeSignatory ch = new SmChequeSignatory();
						ch.setSmPersonnel(sm);
						ch.setSmSupplierBank(getManagedBean().getSelectedBank());
						getManagedBean().getSelectedBank()
								.getSmChequeSignatories().add(ch);
					}
				}
			}

			// set pmFinancialOrg
			for (PmFinancialOrg org : getManagedBean().getFinOrgLs()) {
				if (getManagedBean().getSelectedBank().getPmFinancialOrg()
						.getFinancialOrgId().equals(org.getFinancialOrgId())) {
					getManagedBean().getSelectedBank().setPmFinancialOrg(org);

				}
			}

			// set associatedBranches
			if (getManagedBean().getSelectedBankBranchIds() != null
					&& getManagedBean().getSelectedBankBranchIds().length > 0) {
				for (Long brId : getManagedBean().getSelectedBankBranchIds()) {
					for (SmSupplierBranch br : getManagedBean().getSmAppl()
							.getSmSupplierBranches()) {
						if (br.getSupplierBranchId().equals(brId)) {
							SmApplicationUtil.validateBankBranchLimit(br);
							SmSupplierBranchBank brBank = SmApplicationUtil
									.getBranchBankFromListByBranchIdAndBankId(
											br.getSupplierBranchId(),
											getManagedBean().getSelectedBank()
													.getSupplierBankId(),
											getManagedBean().getSelectedBank()
													.getSmSupplierBranchBanks());
							// newly added
							if (brBank == null) {
								brBank = new SmSupplierBranchBank();
								brBank.setSmSupplierBranch(br);
								brBank.setSmSupplierBank(getManagedBean()
										.getSelectedBank());
								getManagedBean().getSelectedBank()
										.getSmSupplierBranchBanks().add(brBank);
							}
						}
					}
				}

				// remove if not selected
				if (!SmCommonUtil.isCollectionEmpty(getManagedBean()
						.getSelectedBank().getSmSupplierBranchBanks())) {
					Iterator<SmSupplierBranchBank> latestBrBankIt = getManagedBean()
							.getSelectedBank().getSmSupplierBranchBanks()
							.iterator();
					while (latestBrBankIt.hasNext()) {
						SmSupplierBranchBank latestBrBank = latestBrBankIt
								.next();

						if (latestBrBank.getSmSupplierBranch() != null
								&& !SmApplicationUtil.isBranchIdExistInList(
										latestBrBank.getSmSupplierBranch()
												.getSupplierBranchId(),
										getManagedBean()
												.getSelectedBankBranchIds())) {
							latestBrBankIt.remove();
						}
					}
				}

			} else {
				if (!getManagedBean().getSelectedBank().getAccountPurpose()
						.equals("R")
						&& getManagedBean().getSelectedBank().getForHqFlg() == Integer
								.parseInt(ParameterConst.FLAG_NO)) {
					// At least one branch or HQ is required for bank account
					// with account purpose payment or both (payment and
					// registration)
					throw new EpException(MessageConst.ERROR_722619);
				} else {
					getManagedBean().getSelectedBank()
							.getSmSupplierBranchBanks().clear();
				}
			}

			EntityHelper.addOrUpdateEntityToList(getManagedBean()
					.getSelectedBank(), getManagedBean().getSmAppl()
					.getSmSupplierBanks());

			assignValReqdToFalse();
			saveOrUpdateAppl();
			RequestContext.getCurrentInstance().execute("bankDialog.hide()");
		}
	}

	/**
	 * Cancel add or update functionality and close the bank information dialog
	 */
	public void cancelSmBankFunctions() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:bankDialog");
		RequestContext.getCurrentInstance().execute("bankDialog.hide()");
	}

	/**
	 * Assign selected bank object to managed bean for editing the information.
	 * 
	 * @param shareBankBean
	 *            - SmSupplierBank object
	 */
	public void assignSelBank(SmSupplierBank shareBankBean) {
		getManagedBean().setChequeSingPerId(null);
		getManagedBean().setSelectedBankBranchIds(null);

		// assign cheque signatories
		Long[] chequSigns = new Long[shareBankBean.getSmChequeSignatories()
				.size()];
		for (int i = 0; i < chequSigns.length; i++) {
			chequSigns[i] = shareBankBean.getSmChequeSignatories().get(i)
					.getSmPersonnel().getPersonnelId();

		}
		getManagedBean().setChequeSingPerId(chequSigns);

		// assign associated branches
		Long[] bankBranchesIds = null;
		if (SmCommonUtil.isCollectionEmpty(shareBankBean
				.getSmSupplierBranchBanks())) {
			bankBranchesIds = new Long[0];
		} else {
			bankBranchesIds = new Long[shareBankBean.getSmSupplierBranchBanks()
					.size()];
			for (int i = 0; i < bankBranchesIds.length; i++) {
				bankBranchesIds[i] = shareBankBean.getSmSupplierBranchBanks()
						.get(i).getSmSupplierBranch().getSupplierBranchId();

			}
		}
		getManagedBean().setSelectedBankBranchIds(bankBranchesIds);

		if (shareBankBean.getSmAddress() != null
				&& shareBankBean.getSmAddress().getPmCountry().getCountryId() != null
				&& shareBankBean.getSmAddress().getPmState().getStateId() != null
				&& shareBankBean.getSmAddress().getPmDistrict().getDistrictId() != null) {
			SmApplicationUtil.getInstance().assignStateDistAndCityForSelVal(
					getManagedBean(), shareBankBean.getSmAddress(),
					SupplierManagementConst.SM_BANK_INFO_PANEL);
		} else {
			shareBankBean.setSmAddress(SmApplicationUtil
					.setDefaultAddr(getManagedBean().getDefPmCountry()));
			SmApplicationUtil.getInstance().populateStateByCountry(
					shareBankBean.getSmAddress(),
					SupplierManagementConst.SM_BANK_INFO_PANEL,
					getManagedBean());
		}
		getManagedBean().setSelectedBank(
				(SmSupplierBank) SerializationUtils.clone(shareBankBean));
		if (getManagedBean().getLatestAppl() != null) {
			getManagedBean().setLatestBank(
					(SmSupplierBank) EntityHelper
							.getEntityByMultiplePropertiesFromList(
									shareBankBean, getManagedBean()
											.getLatestAppl()
											.getSmSupplierBanks(),
									new String[] { "accountNo",
											"accountPurpose" }, null, null));
		}
		retrieveBankFinOrgListByType();
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("bankDialog.show()");
	}

	/**
	 * Delete or remove selected banks from the SmSupplierBank list of the
	 * application.
	 */
	public void deleteSelectedBanks() {
		if (getManagedBean().getSelectedBanks().size() > 0) {
			for (SmSupplierBank bank : getManagedBean().getSelectedBanks()) {
				// remove cheque signatories
				bank.getSmChequeSignatories().clear();

				// remove associate branches
				if (!SmCommonUtil.isCollectionEmpty(bank
						.getSmSupplierBranchBanks())) {
					bank.getSmSupplierBranchBanks().clear();
				}

				getManagedBean().getSmAppl().getSmSupplierBanks().remove(bank);
			}
			saveOrUpdateAppl();
			getManagedBean().getSelectedBanks().clear();
			getManagedBean().setSelectedSignatoryBankAcct(new SmSupplierBank());
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.supplier.bank", null) });
		}
	}

	/**
	 * View signatories for selected SmSupplierBank.
	 * 
	 * @param signAcctBean
	 *            -SmSupplierBank object
	 */
	public void viewSignatories(SmSupplierBank signAcctBean) {
		getManagedBean().setSelectedSignatoryBankAcct(signAcctBean);
	}

	/**
	 * Add or create a new SmSupplier bank object
	 */
	public void assignNewBank() {
		getManagedBean().setChequeSingPerId(null);
		getManagedBean().setSelBankBranchId(null);
		getManagedBean().setSelectedBankBranchIds(null);
		SmSupplierBank bank = new SmSupplierBank();
		bank.setSmAddress(SmApplicationUtil.setDefaultAddr(getManagedBean()
				.getDefPmCountry()));
		bank.setSmAppl(getManagedBean().getSmAppl());
		bank.setPmFinancialOrg(new PmFinancialOrg());
		bank.setSmChequeSignatories(new ArrayList<SmChequeSignatory>());
		bank.setSmSupplierBranchBanks(new ArrayList<SmSupplierBranchBank>());
		getManagedBean().setSelectedBank(bank);
		getManagedBean().setBankStates(
				SmCommonUtil.getStateList(getManagedBean().getSelectedBank()
						.getSmAddress().getPmCountry().getCountryId()));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("bankDialog.show()");
	}

	/**
	 * Make a call to the BPM for supplier submitting the application.
	 */
	public void initiateSmApplicationTask() {
		IMofRegistrationSO regSo = null;
		try {
			SmAppl bpmAppl = getManagedBean().getSmAppl();
			if (getManagedBean().getSmAppl().isResubmit()) {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_RE_REVIEW);

			} else {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_VERIFICATION);
			}
			getManagedBean().getSmAppl().setApplSubmitDate(new Date());
			log.info(" **** SUPPORTING DOC MODE ****"
					+ bpmAppl.getSupportingDocMode());

			if (bpmAppl.getSupportingDocMode() == null) {
				bpmAppl.setSupportingDocMode(SupplierManagementConst.SUPPORTING_DOC_ONLINE);
			}
			if (!bpmAppl.isActiveAppl()) {
				bpmAppl.setActiveAppl(true);
			}
			String s = "";
			if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				s = SupplierManagementConst.SUPP_SUBMIT_CANCREG_APPL;
			} else {
				s = SupplierManagementConst.SUPP_SUBMIT_NORM_APPL;
			}
			for (SmSupplierBranch br : getManagedBean().getSmAppl()
					.getSmSupplierBranches()) {
				if (br.getBranchCode() == null) {
					br.setBranchCode(br.getSupplierBranchId());
				}
			}

			MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(bpmAppl,
					SupplierManagementConst.SM_APPL_BPM_OUTCOME_SUBMIT, s);
			log.info(" ****** The task list id ss *********"
					+ getManagedBean().getTaskId());
			if (getManagedBean().getTaskId() != null
					&& !getManagedBean().getTaskId().equalsIgnoreCase("")) {
				log.info("***** Update the task *******");
				reqDTO.setUpdateTask(true);
				reqDTO.setTaskId(getManagedBean().getTaskId());
			} else {
				reqDTO.setUpdateTask(false);
			}

			if (getManagedBean().getSmAppl().isResubmit()) {
				reqDTO.setPoLoginId(SmApplicationUtil.getPOLoginId(
						getManagedBean().getSmSupplier(), getManagedBean()
								.getSmAppl()));
			}

			// Set list of users to send to BPM
			if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase("N")
					|| getManagedBean().getSmAppl().getApplType()
							.equalsIgnoreCase("A")
					|| getManagedBean().getSmAppl().getApplType()
							.equalsIgnoreCase("B")
					|| getManagedBean().getSmAppl().getApplType()
							.equalsIgnoreCase("U")
					|| getManagedBean().getSmAppl().getApplType()
							.equalsIgnoreCase("R")
					|| getManagedBean().getSmAppl().getApplType()
							.equalsIgnoreCase("C")) {
				reqDTO.setUserList(new ArrayList<String>());
				log.info("User List Size for pass to BPM:"
						+ reqDTO.getUserList().size());
				PmUser pmUser = new FacesScopes().getUserSessionBean();
				reqDTO.getUserList().add(pmUser.getLoginId());
				log.info("Login user added to the user list to BPM");
			}

			if (this.getManagedBean().getPmDigiSign() != null) {
				reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
			}

			@SuppressWarnings("unused")
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
					.getCompanyBasicGst());
			reqDTO.setCompanyBasicGst(getManagedBean().getCompanyBasicGst());
			resDTO = regSo.initiateSmApplicationTask(reqDTO);
		} finally {
			regSo = null;
			retriveLatestApplAfterUpdate("", "");
		}

	}

	/**
	 * Resubmit sm application inquiry to bpm.
	 * 
	 * @param applType
	 *            - the appl type
	 * @return the redirect page
	 */
	public String resubmitSmApplInquiryToBpm(String applType) {
		log.info("*** Resubmit for send inquiry **** ");
		if (getManagedBean().getSmAppl().getSupportingDocMode() != null
				&& getManagedBean().getSmAppl().getSupportingDocMode()
						.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			validateSupportingDocs();
		}
		getManagedBean().getSmAppl().setResubmit(true);
		return submitSmApplToBpm(applType);

	}

	/**
	 * Submit sm application to bpm and save the latest information to DB.
	 * 
	 * @param applType
	 *            - the appl type
	 * @return the redirect page
	 */
	public String submitSmApplToBpm(String applType) {

		// try {
		if (getManagedBean().getSmAppl().getSupportingDocMode() != null
				&& getManagedBean().getSmAppl().getSupportingDocMode()
						.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			validateSupportingDocs();
		} else {
			log.info("*** Remove unnecessary files for the sm supporting docs **** ");
			for (SmApplSupportDoc doc : getManagedBean().getSmAppl()
					.getSmApplSupportDocs()) {
				if (doc.getSmApplDocFiles() != null
						&& !doc.getSmApplDocFiles().isEmpty()) {
					doc.getSmApplDocFiles().clear();
				}
			}
		}

		// validate personnel email domain
		SmApplicationUtil
				.checkPersonnelsEmailDomainWithExceptionMsg(getManagedBean()
						.getSmAppl().getSmPersonnels());

		// validate branch
		SmApplicationUtil.validateBranchWithoutBankAcc(getManagedBean(),
				currentTab, true);

		// validate facility
		SmApplicationUtil.validateFacilityPanel(getManagedBean(), -1, true);

		if (getManagedBean().getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
				|| getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				|| getManagedBean()
						.getSmAppl()
						.getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			boolean validNewCatAdded = SmApplicationUtil
					.valiateNewCategoryAdded(getManagedBean());
			if (!validNewCatAdded) {
				throw new EpException(MessageConst.ERROR_722570);
			}
		}
		initiateSmApplicationTask();
		String msgConst = "";
		if ((applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF) || applType
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW))
				&& !getManagedBean().getSmAppl().isResubmit()) {
			msgConst = MessageConst.INFO_700053;
		} else {
			msgConst = MessageConst.INFO_700005;
		}
		log.info("Message value === " + msgConst);
		FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								MessageUtils
										.getMessage(
												msgConst,
												new String[] {
														getParameterDesc(
																ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																getManagedBean()
																		.getSmAppl()
																		.getApplType()),
														getManagedBean()
																.getSmAppl()
																.getApplNo(),
														SmRuleHelper
																.getInstance()
																.getProcessingFee()
																.setScale(
																		2,
																		BigDecimal.ROUND_UP)
																.toString(),
														SmRuleHelper
																.getInstance()
																.getApplicationSubmissionDays()
																+ "" },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes()
														.getUserLocale())));
		renewSesionObject();
		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}

	public String submitSmApplToBpm(PmDigiSign pmDigiSign, String applType) {

		if (pmDigiSign != null) {
			updateSpkiDigiSignData(pmDigiSign, "SR", this.getManagedBean()
					.getSmAppl().getApplId(), this.getManagedBean().getSmAppl()
					.getApplNo(), prepareOriginalText());
			byte[] signedData = getManagedBean().getSpkiBackingBean()
					.spkiSignTransaction(prepareOriginalText());

			log.info("signedData: " + signedData);

			pmDigiSign.setSpkiSignedData(signedData);
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		if (applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			log.info(" *** This is cancellation application **** ");
			return submitCancelMofApplToBpm();
		} else {
			return submitSmApplToBpm(applType);
		}
	}

	/**
	 * Submit cancel mof application to bpm and update the DB.
	 * 
	 * @return the redirect page
	 */
	public String submitCancelMofApplToBpm() {
		log.info(" Call Cancel ---------------->>>>>>>>>>");
		try {
			SmRemark remark = new SmRemark();
			remark.setDocId(getManagedBean().getSmAppl().getApplId());
			remark.setDocType(ParameterConst.NEW_SM_APPLN_FOR_CANCEL);
			remark.setRemark(getManagedBean().getSupplierRemarks());
			saveSmRemarks(remark);
			saveSmCancellationAttachment(getManagedBean()
					.getCancellationAttachment());
			initiateSmApplicationTask();
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
															getParameterDesc(
																	ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																	ParameterConst.NEW_SM_APPLN_FOR_CANCEL),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			renewSesionObject();
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * Submit delete pending mof appl to bpm.
	 * 
	 * @return the string
	 */
	public String submitDeletePendingMofApplToBpm() {
		IMofRegistrationSO regSo = null;
		try {
			StringBuffer sb = new StringBuffer();
			for (Long resId : getManagedBean().getReasonId()) {
				for (PmReasonDesc desc : getManagedBean().getReasonDescList()) {
					if (desc.getPmReason().getReasonId().equals(resId)) {
						if (!desc
								.getPmReason()
								.getReasonCode()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_REASON_CODE_OTHER)) {
							sb.append(desc.getReasonDesc());
							sb.append(",");
						} else {
							sb.append(desc.getReasonDesc()).append(" (");
							sb.append(getManagedBean().getSupplierRemarks());
							sb.append("),");
						}
					}
				}
			}

			if (sb.toString() == null || sb.toString().equalsIgnoreCase("")) {
				throw new EpException(MessageConst.ERROR_722529);
			}
			SmRemark remark = new SmRemark();
			remark.setDocId(getManagedBean().getSmAppl().getApplId());
			remark.setDocType(ParameterConst.NEW_SM_APPLN_FOR_DELETE_PENDING_APPL);
			remark.setRemark(sb.toString());
			if (remark.getRemark().endsWith(",")) {
				remark.setRemark(remark.getRemark().substring(0,
						remark.getRemark().length() - 1));
			}
			if (remark.getRemark().length() > 2500) {
				remark.setRemark(remark.getRemark().substring(0, 2500));
			}

			saveSmRemarks(remark);
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
			reqDTO.setOutcome(SupplierManagementConst.SM_APPL_BPM_OUTCOME_SUBMIT);
			reqDTO.setSupplierId(this.getManagedBean().getSmAppl()
					.getSmSupplier().getSupplierId());
			reqDTO.setUserList(new ArrayList<String>());
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			reqDTO.getUserList().add(pmUser.getLoginId());
			reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			if (this.getManagedBean()
					.getSmAppl()
					.getSupportingDocMode()
					.equalsIgnoreCase(
							SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
				reqDTO.setOnlineDoc(true);
			} else {
				reqDTO.setOnlineDoc(false);
			}

			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			regSo.initiateSmDelPendingApplicationTask(reqDTO);
			String msg = "";
			SmWorkflowStatus sts = SmCommonUtil
					.getCurrentActiveWokFlowStatus(getManagedBean().getSmAppl()
							.getApplId());
			if (sts != null && sts.getPmStatus() != null) {
				if (sts.getPmStatus()
						.getStatusId()
						.compareTo(
								SupplierManagementConst.PM_STATUS_PENDING_PROCESS_FEE) == 0
						|| sts.getPmStatus()
								.getStatusId()
								.compareTo(
										SupplierManagementConst.PM_STATUS_PENDING_VERIFY_SUPP_DOCS) == 0) {
					msg = MessageConst.INFO_700056;
				} else {
					msg = MessageConst.INFO_700057;
				}
			} else {
				msg = SmCommonUtil.getMessage(
						"lbl.sm.appl.no.status.submitted.msg", null);
			}
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(msg, new String[] {

							getManagedBean().getSmAppl().getApplNo() },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			renewSesionObject();
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} finally {
			regSo = null;
		}
	}

	/**
	 * Delete draft applciation from Db and clear task from BPM if required.
	 * 
	 * @return the redirect page
	 */
	public String deleteDraftApplication() {

		boolean valid = false;
		try {
			valid = deleteDraftApplciationTask();
			if (valid) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_721205,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																		getManagedBean()
																				.getSmAppl()
																				.getApplType()),
																getManagedBean()
																		.getSmAppl()
																		.getApplNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				return null;
			}
		} finally {

		}
	}

	/**
	 * Save the application as draft in DB and Intialize bpm if required.
	 * 
	 * @param applType
	 *            - the appl type
	 * @param currentPageType
	 *            - the current page type
	 * @return the redirect page
	 */
	public String saveApplicationAsDraft(String applType, String currentPageType) {
		log.info("****** Save As Draft **********" + currentPageType
				+ currentTab);
		boolean valid = false;
		try {
			if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
				valid = SmApplicationUtil.validateCategoryOtherDesc(true,
						getManagedBean(), currentTab);
			} else {
				valid = validateBeforeSave(applType, currentPageType);
			}

			if (valid) {
				valid = intializeSaveDraftBpm(currentPageType);
			}
			if (valid) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_721204,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																		getManagedBean()
																				.getSmAppl()
																				.getApplType()),
																getManagedBean()
																		.getSmAppl()
																		.getApplNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				return null;
			}
		} finally {

		}
	}

	/**
	 * Save the application as draft in DB and Intialize bpm if required.
	 * 
	 * @param currentPageType
	 *            - the current page type
	 * @return true, if successful
	 */
	private boolean intializeSaveDraftBpm(String currentPageType) {
		log.info("*********  intializeSaveDraftBpm ************* ");
		IMofRegistrationSO regSo = null;
		boolean valid = false;
		try {
			// SmApplicationUtil.getInstance().setApplAddressTransientValue(
			// getManagedBean().getSmAppl(), true);
			SmAppl bpmAppl = getManagedBean().getSmAppl();
			bpmAppl.setApplSaveDate(new Timestamp(System.currentTimeMillis()));

			if (bpmAppl.getSupportingDocMode() == null) {
				bpmAppl.setSupportingDocMode(SupplierManagementConst.SUPPORTING_DOC_ONLINE);
			}
			String outcome = "";
			if (!getManagedBean().isDraftedAppl()) {
				outcome = SupplierManagementConst.SM_APPL_BPM_OUTCOME_DRAFT;
			} else {
				outcome = "";
			}
			MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(bpmAppl,
					outcome, SupplierManagementConst.SUPP_SAVE_APPL_DRAFT);
			List<String> useList = new ArrayList<String>();
			PmUser loginUser = new FacesScopes().getUserSessionBean();
			useList.add(loginUser.getLoginId());
			reqDTO.setUserList(useList);
			log.info("******* Save as draft User List to BPM Size *******"
					+ reqDTO.getUserList());
			if (reqDTO.getOutcome().equals("")) {
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
			}
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
					.getCompanyBasicGst());
			SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
					.getSelectedBranch().getGst());
			SmApplicationUtil.getInstance()
					.removeNotApplicableInformationBeforeSave(getManagedBean());

			reqDTO.setCompanyBasicGst(getManagedBean().getCompanyBasicGst());
			reqDTO.setBranchGst(getManagedBean().getSelectedBranch().getGst());
			reqDTO.setSameAsHq(getManagedBean().getSelectedBranch()
					.isSameAsHq());
			resDTO = regSo.initiateSmApplSaveAsDraftTask(reqDTO);
			if (resDTO != null) {
				log.info("******* From SO ******** " + resDTO.isBpmSuccess());
				valid = resDTO.isBpmSuccess();
				if (!valid) {
					log.info("******* Process No Valid ******");
					throw new EpException(MessageConst.ERROR_700020);
				}
			} else {
				log.info("*** The Save as draft returned null **** ");
				valid = false;
				throw new EpException(MessageConst.ERROR_722519);
			}
			return valid;
		} finally {
			regSo = null;
			retriveLatestApplAfterUpdate(currentPageType, "");
		}
	}

	/**
	 * Delete draft applciation from Db and clear task from BPM if required.
	 * 
	 * @return true, if successful
	 */
	private boolean deleteDraftApplciationTask() {
		log.info("*********  Delete draft application task ************* ");
		IMofRegistrationSO regSo = null;
		boolean valid = true;
		try {
			SmAppl bpmAppl = getManagedBean().getSmAppl();
			String outcome = "CANCEL";
			MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(bpmAppl,
					outcome, SupplierManagementConst.SUPP_SUBMIT_DELDRFT_APPL);
			List<String> useList = new ArrayList<String>();
			PmUser loginUser = new FacesScopes().getUserSessionBean();
			useList.add(loginUser.getLoginId());
			reqDTO.setUserList(useList);
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			resDTO = regSo.initiateSmApplDeleteDraftTask(reqDTO);
			if (resDTO != null) {
				valid = resDTO.isBpmSuccess();
				if (!valid) {
					log.info("******* Process No Valid ******");
					throw new EpException(MessageConst.ERROR_700020);
				}
			} else {
				log.info("*** The Save as draft returned null **** ");
				valid = false;
				throw new EpException(MessageConst.ERROR_722519);
			}
			return valid;
		} catch (Exception e) {
			log.info("***Error in Delete Draft  **** ");
			retriveLatestApplAfterUpdate(getManagedBean().getCurrentPageType(),
					"");
			throw new EpException(e.getMessage());
		}

		finally {
			regSo = null;
		}

	}

	/**
	 * Sets default values for the mof registration req dto.
	 * 
	 * @param bpmAppl
	 *            - SmAppl object
	 * 
	 * @param outcome
	 *            - the outcome value
	 * @param actionDesc
	 *            - the action desc
	 * @return the mof registration req dto object
	 */
	private MofRegistrationReqDTO setMofRegistrationReqDTO(SmAppl bpmAppl,
			String outcome, String actionDesc) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setOutcome(outcome);
		reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
		reqDTO.setSupplierId(bpmAppl.getSmSupplier().getSupplierId());
		reqDTO.setAppType(bpmAppl.getApplType());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setSmAppl(bpmAppl);
		reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
		reqDTO.setSupplierType(this.getManagedBean().getSmAppl()
				.getSupplierType().toUpperCase());
		reqDTO.setTrackingDairyVO(SmApplicationUtil.assignTrackingDairyValues(
				getManagedBean(), actionDesc, getManagedBean().getSmAppl()
						.getPmStatus()));
		PmState state = getManagedBean().getSmAppl().getSmCompanyBasic()
				.getSmCompanyAddresses().get(0).getSmAddress().getPmState();
		if (state != null && state.getStateId() != null) {
			PmZone zone = SmApplicationUtil.retrieveZoneByStateId(state
					.getStateId());
			log.info("*** ZONE SENT TO BPM ***** " + zone.getZoneName());
			if (zone != null && zone.getZoneName() != null) {
				reqDTO.setBaseOfficeState(zone.getZoneName().toUpperCase()
						.trim());
			}
		} else {
			throw new EpException(MessageConst.ERROR_722568);
		}

		if (this.getManagedBean()
				.getSmAppl()
				.getSupportingDocMode()
				.equalsIgnoreCase(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			reqDTO.setOnlineDoc(true);
		} else {
			reqDTO.setOnlineDoc(false);
		}

		reqDTO.setResubmit(bpmAppl.isResubmit());
		reqDTO.setLangCode(new FacesScopes().getUserLocale().getLanguage());
		return reqDTO;
	}

	/**
	 * Assign missing or invalid supporting docs list.
	 */
	public void assignMissingDocsList() {
		log.info("*** Send Inquiry **** ");
		SmApplicationUtil.validateSupportingDocsDecision(getManagedBean());
		getManagedBean().setMissinDocs(new ArrayList<SmApplSupportDoc>());
		for (SmApplSupportDoc doc : getManagedBean().getSmAppl()
				.getSmApplSupportDocs()) {
			if (doc.getVerifiedFlg() == null || doc.getVerifiedFlg() != 1) {
				getManagedBean().getMissinDocs().add(doc);
			}
		}
		log.info("**** Send Inquiry List Size ***** "
				+ getManagedBean().getMissinDocs().size());
		RequestContext.getCurrentInstance().execute("sendInqDialog.show()");
	}

	/**
	 * Submit po escalation to approver.
	 * 
	 * @param recommendation
	 *            - the recommendation
	 */
	public void submitPOEscalationToApprover(String recommendation) {
		SmApplicationUtil.validateCategoryAndDocsDecision(getManagedBean());
		getManagedBean().getPoSecReview().setRecommendation(recommendation);
		RequestContext.getCurrentInstance()
				.execute("submitApprvrDialog.show()");
	}

	/**
	 * Cancel po escalation to approver.
	 */
	public void cancelPOEscalationToApprover() {
		getManagedBean().getPoSecReview().setRecommendation("");
		RequestContext.getCurrentInstance()
				.execute("submitApprvrDialog.hide()");
	}

	/**
	 * PO select to assign final approve or reject to apparover.
	 * 
	 * @param dialog
	 *            - the dialog id to be opened
	 */
	public void assignFinalApproveOrReject(String dialog) {
		log.info("**** assignFinalApproveOrReject *** ");
		if (!dialog.equalsIgnoreCase("rejectDialog")
				&& !dialog.equalsIgnoreCase("approverRejDialog")) {
			SmApplicationUtil.validateCategoryAndDocsDecision(getManagedBean());
		}
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute(dialog + ".show()");
	}

	/**
	 * Approver select to Approve or reject mof cancellation.
	 * 
	 * @param dialog
	 *            - the dialog id to be opened
	 */
	public void approveOrRejectMofCancellation(String dialog) {
		log.info("**** assignFinalApproveOrReject *** ");
		assignValReqdToTrue();
		getManagedBean().setRejectReasonReqd(true);
		RequestContext.getCurrentInstance().execute(dialog + ".show()");
	}

	/**
	 * PO select to Assign site visit and pop up opens.
	 * 
	 * @param dialog
	 *            - the dialog id to be opened
	 */
	public void assignSiteVisitPopUp(String dialog) {
		log.info("**** assignSiteVisitPopUp *** ");
		SmApplicationUtil.validateCategoryAndDocsDecision(getManagedBean());
		getManagedBean()
				.setSplCategoryList(new ArrayList<SmSupplierCategory>());
		for (SmSupplierCategory category : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (category.isSpecialCategory()
					&& category.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
				getManagedBean().getSplCategoryList().add(category);
			}
		}
		getManagedBean().setShowParentMsg(false);
		SmApplicationUtil.setAssignSiteVisitVO(getManagedBean());
		RequestContext.getCurrentInstance().execute(dialog + ".show()");
	}

	/**
	 * Assign view site visit report.
	 */
	public void assignViewSiteVisitReport() {
		log.info("**** assignViewSiteVisitReport *** ");
		SvoSiteVisitBackingBean svo = new SvoSiteVisitBackingBean();
		svo.getManagedBean().setAppId(getManagedBean().getSmAppl().getApplId());
		svo.reviewSiteVisitReport();
	}

	/**
	 * Submit po descision for site visit.
	 * 
	 * @param dialog
	 *            - the dialog id be closed
	 * @param decision
	 *            - the decision
	 * @return the redirect page.
	 */
	public String submitPODescisionForSiteVisit(String dialog, String decision) {
		log.info("**** assignSiteVisitPopUp *** ");
		List<String> updateIds = new ArrayList<String>();
		updateIds.add(":form");
		Boolean valid = SmApplicationUtil
				.validateSiteVisitSelections(getManagedBean());
		if (valid) {
			getManagedBean().setShowParentMsg(true);
			RequestContext.getCurrentInstance().update(updateIds);
			RequestContext.getCurrentInstance().execute(dialog + ".hide()");
			return submitPOorApproverReviewToBPM(decision);
		} else {
			return null;
		}
	}

	/**
	 * Call alert for HQ GST Effective Date not first day of month
	 */
	public void onCheckHqEffectiveDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getManagedBean().getCompanyBasicGst().getGstEffDate());
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if (day != 1) {
			RequestContext.getCurrentInstance().execute("alertHq.show();");
		}
	}

	/**
	 * Reset HQ Effective Date to first day of month
	 */
	public void resetHqEffectiveDate() {
		SmCommonUtil.resetGstEffectiveDate(getManagedBean()
				.getCompanyBasicGst());
	}

	/**
	 * Cancel button action for site visit action.
	 * 
	 * @param dialog
	 *            - the dialog id to be close
	 */
	public void cancelSiteVisitAction(String dialog) {
		getManagedBean().setShowParentMsg(true);
		RequestContext.getCurrentInstance().reset(":form:tt:" + dialog);
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
	}

	/**
	 * Cancel button action for approve or reject.Close the pop up without any
	 * action.
	 * 
	 * @param dialog
	 *            - the dialog id to be close
	 */
	public void cancelFinalApproveOrReject(String dialog) {
		assignValReqdToFalse();
		getManagedBean().setReasonId(null);
		getManagedBean().setRejectReasonReqd(false);
		RequestContext.getCurrentInstance().reset(":form:tt:" + dialog);
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
	}

	/**
	 * Reset mof cancellation final approve or reject.
	 * 
	 * @param dialog
	 *            - the dialog id to be closed
	 */
	public void resetMofCancellationFinalApproveOrReject(String dialog) {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:" + dialog);
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
	}

	/**
	 * Final decision approve or reject application.
	 * 
	 * @param dialog
	 *            - the dialog id be closed
	 * @param decision
	 *            - the decision
	 * @return the redirect page.
	 */
	public String finalDecisionApproveOrRejectAppln(String dialog,
			String decision) {
		List<String> updateIds = new ArrayList<String>();
		updateIds.add(":form");
		log.info("*** Submit Approve *** " + decision);
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
		RequestContext.getCurrentInstance().update(updateIds);
		return submitPOorApproverReviewToBPM(decision);

	}

	/**
	 * Final decision of PO on delete pending appln
	 * 
	 * @param dialog
	 *            - the dialog id to be closed
	 * @param decision
	 *            - the decision
	 * @return the redirect page.
	 */
	public String finalDecisionDeletePendingAppln(String dialog, String decision) {
		List<String> updateIds = new ArrayList<String>();
		updateIds.add(":form");
		log.info("*** Submit Approve *** " + decision);
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
		RequestContext.getCurrentInstance().update(updateIds);
		return approveOrRejectPendingApplicationCancellation(decision);

	}

	/**
	 * Approve or reject pending application cancellation.
	 * 
	 * @param res
	 *            - the PO or Approver resposne
	 * @return the redirect page.
	 */
	public String approveOrRejectPendingApplicationCancellation(String res) {
		IReviewApplicationSO reqSO = null;

		ReviewApplicationReqDTO reqDTO = new ReviewApplicationReqDTO();
		reqSO = (IReviewApplicationSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IReviewApplicationSO.class);
		reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
		reqDTO.setApplNo(this.getManagedBean().getSmAppl().getApplNo());
		reqDTO.setOutcome(res);
		reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setLocation(getManagedBean().getSmAppl().getSmCompanyBasic()
				.getSmCompanyAddresses().get(0).getSmAddress().getPmState()
				.getStateName().toUpperCase());
		reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
		if (getManagedBean().getSupplierToPOList() != null) {
			log.info("**** LIST of supp from BPM earlier task *******"
					+ getManagedBean().getSupplierToPOList().size());
			reqDTO.setUserList(getManagedBean().getSupplierToPOList());
		} else {
			log.info("***** NO SUPPLIER LIST FROM BPM TO DECISION ***** ");
			reqDTO.setUserList(new ArrayList<String>());
		}
		reqSO.approveOrRejectPendingApplicationCancellation(reqDTO);

		res = res.equals("REJECT") ? SmCommonUtil.getMessage(
				"lbl.sm.cancelpending.reject.uppercase", null) : SmCommonUtil
				.getMessage("lbl.sm.cancelpending.approve.uppercase", null);
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_700055,
								new String[] {
										getManagedBean().getSmAppl()
												.getApplNo(), res },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
		renewSesionObject();
		return "cancelPendingAcknowledgePage";
	}

	/**
	 * Submit p oor approver review to bpm.
	 * 
	 * @param res
	 *            - the PO or Approver resposne
	 * @return the redirect page.
	 */
	public String submitPOorApproverReviewToBPM(String res) {
		IReviewApplicationSO reqSO = null;
		String retMsg = null;
		try {
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			Long pmStatId = getManagedBean().getSmAppl().getPmStatus()
					.getStatusId();
			retriveCurrentAppl();
			// validate personnels email domain
			SmApplicationUtil
					.checkPersonnelsEmailDomainWithExceptionMsg(getManagedBean()
							.getSmAppl().getSmPersonnels());
			log.info(" **** After retrieve ***"
					+ getManagedBean().getSmAppl().getPmStatus().getStatusId());
			log.info("----------------------Submit PO / Approver Decision For BPM-------------------------"
					+ res);
			log.info(" **** The Value from screen of approval ***" + pmStatId);
			ReviewApplicationReqDTO reqDTO = new ReviewApplicationReqDTO();
			reqSO = (IReviewApplicationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IReviewApplicationSO.class);
			reqDTO.setPoLoginId(pmUser.getLoginId());
			reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
			reqDTO.setApplNo(this.getManagedBean().getSmAppl().getApplNo());
			reqDTO.setOutcome(res);
			reqDTO.setSuppId(this.getManagedBean().getSmAppl().getSmSupplier()
					.getSupplierId());
			reqDTO.setBpmTaskName(getManagedBean().getBpmTaskName());
			reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			PmState state = getManagedBean().getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress().getPmState();
			if (state != null && state.getStateId() != null) {
				PmZone zone = SmApplicationUtil.retrieveZoneByStateId(state
						.getStateId());
				log.info("*** ZONE SENT TO BPM ***** " + zone.getZoneName());
				if (zone != null && zone.getZoneName() != null) {
					reqDTO.setLocation(zone.getZoneName().toUpperCase());
				}
			} else {
				throw new EpException(MessageConst.ERROR_722568);
			}
			reqDTO.setSupplierType(this.getManagedBean().getSmAppl()
					.getSupplierType().toUpperCase());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setLangCode(new FacesScopes().getUserLocale().getLanguage());
			if (getManagedBean().getSupplierToPOList() != null) {
				log.info("**** LIST of supp from BPM earlier task *******"
						+ getManagedBean().getSupplierToPOList().size());
				reqDTO.setUserList(getManagedBean().getSupplierToPOList());
			} else {
				log.info("***** NO SUPPLIER LIST FROM BPM TO DECISION ***** ");
				reqDTO.setUserList(new ArrayList<String>());
			}

			if (this.getManagedBean()
					.getSmAppl()
					.getSupportingDocMode()
					.equalsIgnoreCase(
							SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
				reqDTO.setOnlineDoc(true);
			} else {
				reqDTO.setOnlineDoc(false);
			}

			boolean validSiteVisit = true;
			String actionDesc = "";
			if (res.equalsIgnoreCase("SITE_VISIT")
					|| res.equalsIgnoreCase("SITE_VISIT_AFTER_APPROVE")) {
				// SmApplicationUtil.siteVisitDataUpdate(getManagedBean());
				SmApplicationUtil.setSiteVisitData(getManagedBean());
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_ASSIGNMENT);
				reqDTO.setSvTask(getManagedBean().getSvTask());
				reqDTO.setSvTaskList(getManagedBean().getSvTaskList());
				actionDesc = SupplierManagementConst.PO_RECOM_SITE_VISIT;

			}
			if (res.equalsIgnoreCase("INQUIRY")) {
				for (SmApplSupportDoc doc : getManagedBean().getSmAppl()
						.getSmApplSupportDocs()) {
					for (SmApplSupportDoc misDoc : getManagedBean()
							.getMissinDocs()) {
						if (misDoc.getApplSupportDocId().equals(
								doc.getApplSupportDocId())) {
							getManagedBean()
									.getSmAppl()
									.getSmApplSupportDocs()
									.set(getManagedBean().getSmAppl()
											.getSmApplSupportDocs()
											.indexOf(doc), misDoc);
						}
					}
				}
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_REPLY_INQUIRY_BY_PROCESSING_OFFICER);
				actionDesc = SupplierManagementConst.PO_SEND_INQUIRY_SUPP;
			}
			if (res.equalsIgnoreCase("Reject")) {

				if (getManagedBean().isPoUser()) {
					setApplPmStatus(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER);
					actionDesc = SupplierManagementConst.PO_REJECT_APPL;
				} else if (getManagedBean().isApproverUser()) {
					setApplPmStatus(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER);
					actionDesc = SupplierManagementConst.APPROVER_REJECT_APPL;
				}
				if (!getManagedBean()
						.getSmAppl()
						.getApplType()
						.equalsIgnoreCase(
								ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
					setSmApplRejectionReasons();
					reqDTO.setRejectionReasons(getManagedBean().getReasonDesc());
				}
				if (StringUtils.isNotBlank(getManagedBean()
						.getApplnRejectionRemarks())) {
					SmApplicationUtil.manageAndSaveNewSmRemarks(
							getManagedBean().getSmAppl().getApplId(),
							ParameterConst.SM_APPLN_RMRK_DECISION_REJECT,
							getManagedBean().getApplnRejectionRemarks());
				}
				SmApplicationNotificationUtil
						.setRejectionNotificationParameters(getManagedBean(),
								reqDTO);

			} else if (res.equalsIgnoreCase("Approve")) {
				if (getManagedBean().isPoUser()) {
					actionDesc = SupplierManagementConst.PO_APPROVE_APPL;
				} else if (getManagedBean().isApproverUser()) {
					actionDesc = SupplierManagementConst.APPROVER_APPROVE_APPL;
				}
				if (getManagedBean()
						.getSmAppl()
						.getApplType()
						.equalsIgnoreCase(
								ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
					setApplPmStatus(SupplierManagementConst.PM_STATUS_APPROVED_BY_APPROVER);
				} else {
					// set SME Status
					SmApplicationUtil
							.setSmeStatus(getManagedBean().getSmAppl());

					if (pmStatId != null) {
						for (PmStatusDesc desc : getManagedBean()
								.getRegStatusDescList()) {
							if (pmStatId.equals(desc.getPmStatus()
									.getStatusId())) {
								getManagedBean().getSmAppl().setPmStatus(
										desc.getPmStatus());
								if (desc.getPmStatus().getPmRegStatus() != null) {
									getManagedBean().getSmAppl()
											.setPmRegStatus(
													desc.getPmStatus()
															.getPmRegStatus());
									getManagedBean().getSmSupplier().setBumi(
											desc.getPmStatus().getPmRegStatus()
													.isBumiAppl());
									break;
								}
							}
						}
					}
					reqDTO.setPaymentReqd(setPaymentMode());
				}
				for (SmSupplierCategory cat : getManagedBean().getSmAppl()
						.getSmSupplierCategories()) {
					if (cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0
							|| cat.getRecordStatusFlg()
									.compareTo(
											SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
						if ((getManagedBean().isPoUser()
								&& cat.getPoDecFlg() != null && cat
								.getPoDecFlg().compareTo(new Integer(1)) == 0)
								|| (getManagedBean().isApproverUser()
										&& cat.getApprvrDecFlg() != null && cat
										.getApprvrDecFlg().compareTo(
												new Integer(1)) == 0)) {
							cat.setRecordStatusFlg(SupplierManagementConst.APPROVED_RECORD_STATUS);
							setSupplierCategoryEffectiveDate(cat,
									getManagedBean().getSmAppl().getApplType());
						} else {
							cat.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						}

					}
				}

				SmApplicationNotificationUtil
						.setApprovalNotificationParameters(getManagedBean(),
								reqDTO);
				if (StringUtils.isNotBlank(getManagedBean()
						.getApplnApprovalRemarks())) {
					SmApplicationUtil.manageAndSaveNewSmRemarks(
							getManagedBean().getSmAppl().getApplId(),
							ParameterConst.SM_APPLN_RMRK_DECISION_APPROVE,
							getManagedBean().getApplnApprovalRemarks());
				}
				if (getManagedBean().isOverLappedCategoryExists()
						&& StringUtils.isNotBlank(getManagedBean()
								.getApplnOverLapRemarks())) {
					SmApplicationUtil.manageAndSaveNewSmRemarks(
							getManagedBean().getSmAppl().getApplId(),
							ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY,
							getManagedBean().getOverLappedCategoryMsg());
				}

			}

			log.info("Before final decision ---"
					+ getManagedBean().getSmAppl().getPmStatus().getStatusId());
			validSiteVisit = testSmAppl(getManagedBean().getSmAppl()
					.getApplType(),
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC, "", true);
			if (res.equalsIgnoreCase("Approve")) {
			}
			if (validSiteVisit) {
				String successMsg = "";
				SmMofAccount mofAcc = getManagedBean().getSmSupplier() != null
						&& getManagedBean().getSmAppl() != null ? SmCommonUtil
						.getMofAccountBySuppIdAndSuppType(getManagedBean()
								.getSmSupplier().getSupplierId(),
								getManagedBean().getSmAppl().getSupplierType())
						: null;
				String mofNo = mofAcc != null
						&& StringUtils.isNotBlank(mofAcc.getMofNo()) ? mofAcc
						.getMofNo() : "";
				String applNo = getManagedBean().getSmAppl().getApplNo();
				String applTypeDesc = getParameterDesc(
						ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
						SmApplicationUtil.setParameterConst(getManagedBean()
								.getSmAppl().getApplType()));
				if (getManagedBean().isPoUser()
						|| getManagedBean().isApproverUser()) {
					log.info(" **** Final Decision from PO ****");
					if (res.equalsIgnoreCase("Approve")) {
						if (StringUtils.isNotBlank(mofNo)) {
							successMsg = SmCommonUtil.getOutputMsgBundle(
									MessageConst.INFO_700027, new String[] {
											applNo, mofNo });
						} else {
							successMsg = SmCommonUtil.getOutputMsgBundle(
									MessageConst.INFO_700030,
									new String[] { applNo });
						}
					} else if (res.equalsIgnoreCase("Reject")) {
						if (getManagedBean().getBpmTaskName() != null
								&& getManagedBean()
										.getBpmTaskName()
										.equalsIgnoreCase(
												SupplierManagementConst.BPM_TASK_REVIEW_SITE_VISIT_AFTER_APPROVE)) {
							reqDTO.setLastApprovedAppl(SmApplicationUtil
									.getLastApprovedApplictionForSupplier(getManagedBean()));
						}
						if (StringUtils.isNotBlank(mofNo)) {
							successMsg = SmCommonUtil.getOutputMsgBundle(
									MessageConst.INFO_700028, new String[] {
											applNo, mofNo });
						} else {
							successMsg = SmCommonUtil.getOutputMsgBundle(
									MessageConst.INFO_700029,
									new String[] { applNo });
						}
					} else if (res.equalsIgnoreCase("Escalate")) {
						String statusDesc = "";
						if (getManagedBean().getPoSecReview() != null
								&& getManagedBean().getPoSecReview()
										.getRecommendation().equals("APPROVE")) {
							statusDesc = SmCommonUtil.getMessage(
									"lbl.sm.approval.lowercaps", null);
						} else {
							statusDesc = SmCommonUtil.getMessage(
									"lbl.sm.rejected.lowercaps", null);
						}
						actionDesc = SupplierManagementConst.PO_ESCALATE_APPROVER;
						successMsg = SmCommonUtil.getOutputMsgBundle(
								MessageConst.INFO_700014, new String[] {
										applNo, statusDesc,
										getApproverListForSuccessMsg() });
					} else if (res.equalsIgnoreCase("Inquiry")) {
						successMsg = SmCommonUtil.getOutputMsgBundle(
								MessageConst.INFO_700016, new String[] {
										applTypeDesc, applNo });
					} else if (res.equalsIgnoreCase("SITE_VISIT")) {
						successMsg = SmCommonUtil.getOutputMsgBundle(
								MessageConst.INFO_700015,
								new String[] { applNo });
					} else if (res.equalsIgnoreCase("SITE_VISIT_AFTER_APPROVE")) {
						successMsg = SmCommonUtil.getOutputMsgBundle(
								MessageConst.INFO_700017,
								new String[] { applNo });
					}
					reqDTO.setPoOrApproverDecision(res);
					reqDTO.setSmAppl(this.getManagedBean().getSmAppl());
					reqDTO.setTrackingDairyVO(SmApplicationUtil
							.assignTrackingDairyValues(getManagedBean(),
									actionDesc, getManagedBean().getSmAppl()
											.getPmStatus()));
					FacesScopes fs = new FacesScopes();
					reqDTO.setServerName(fs.getRequest().getServerName());
					reqDTO.setServerPort(fs.getRequest().getServerPort());
					reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());

					reqSO.submitPOorApproverReviewToBPM(reqDTO);

					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									successMsg));
					renewSesionObject();
					retMsg = CommonUIConst.ACKNOWLEDGE_PAGE;

				} else {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Sample error message",
											"Un Authorized user to perform this action"));
					retMsg = null;
				}
			} else {
				retMsg = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			retMsg = null;
			retriveLatestApplAfterUpdate("", "");
			throw new EpException(e.getMessage());
		} finally {
			reqSO = null;
		}
		return retMsg;
	}

	private String getApproverListForSuccessMsg() {
		StringBuffer bf = new StringBuffer();
		String retVal = "";
		SmAddress hqAddr = getHqAddress();
		if (hqAddr != null) {
			ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
			zoneReqDTO.setStateId(hqAddr.getPmState().getStateId());
			PmZone zone = SmServiceLookup.getZoneSO()
					.retrieveZoneByStateId(zoneReqDTO).getPmZone();
			ReviewApplicationReqDTO reviewApplicationReqDTO = new ReviewApplicationReqDTO();
			reviewApplicationReqDTO.setZoneCode(zone.getZoneCode());
			List<PmUser> approverList = SmServiceLookup
					.getReviewApplicationSO()
					.getApproversByZoneAfterEscalate(reviewApplicationReqDTO)
					.getUserAssignList();
			if (!SmCommonUtil.isCollectionEmpty(approverList)) {
				int i = 0;
				for (PmUser user : approverList) {
					i++;
					bf.append("<tr><td>").append(i).append(")</td><td>")
							.append(user.getUserName()).append("</td></tr>");
				}
			}
		}
		if (bf.length() > 0) {
			retVal = bf.toString();
		}
		return retVal;
	}

	/**
	 * Test sm application validations before save or update to DB.
	 * 
	 * @param applType
	 *            - the application type
	 * @param currentPageType
	 *            -the current page type
	 * @param outputPageType
	 *            - the output page type
	 * @param isBpmSubmit
	 *            - boolean
	 * @return true, if successful
	 */
	public boolean testSmAppl(String applType, String currentPageType,
			String outputPageType, Boolean isBpmSubmit) {
		boolean valid = false;
		boolean isError = false;
		try {

			if (getManagedBean().isPoUser()
					|| getManagedBean().isApproverUser()) {
				setPoOrApprvrSectionReview();
			}

			valid = validateBeforeSave(applType, currentPageType);
			// valid = true;
			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				log.info(" ****** REMAIN PREV VALUE ********");
			}
			if (valid) {
				// Save or update to DB
				getManagedBean().setCurrentPageType(outputPageType);

			} else {
				getManagedBean().setTabActiveIndex(currentTab);
				getManagedBean().getSmAppl().setCurrentActiveTab(currentTab);
				getManagedBean().setCurrentPageType(currentPageType);
			}
			isError = valid;
		}

		finally {
		}
		return isError;
	}

	/**
	 * Sets the payment mode i.e. to determine if payment required.
	 * 
	 * @return true, if successful
	 */
	private boolean setPaymentMode() {
		boolean payReq = false;
		if (getManagedBean().getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				|| getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			payReq = true;
		}
		return payReq;
	}

	/**
	 * Change all sections verification value based on selection on screen.
	 * 
	 * @param isVerifiedAll
	 *            - boolean
	 */
	public void changeVerifyAll(Boolean isVerifiedAll) {
		log.info(" **** Enter to  verified all ***** " + isVerifiedAll);
		SmApplSectionReview sec = new SmApplSectionReview();

		if (getManagedBean().isPoUser()) {
			sec = getManagedBean().getPoSecReview();
			getManagedBean().setPoSecReview(
					SmApplicationUtil.setVerificationValues(sec, isVerifiedAll,
							getManagedBean().getMaxReviewCount(),
							getManagedBean()));
		} else if (getManagedBean().isApproverUser()) {
			sec = getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					SmApplicationUtil.setVerificationValues(sec, isVerifiedAll,
							getManagedBean().getMaxReviewCount(),
							getManagedBean()));
		}

	}

	/**
	 * Change section verification value based on selection on screen.
	 * 
	 * @param isSectionVerified
	 *            - boolean
	 */
	public void changeVerifySection(Boolean isSectionVerified) {
		if (getManagedBean().isPoUser()) {
			getManagedBean().getPoSecReview();
			getManagedBean().setPoSecReview(
					assignVerificationCount(getManagedBean().getPoSecReview(),
							isSectionVerified));
		} else if (getManagedBean().isApproverUser()) {
			getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					assignVerificationCount(getManagedBean()
							.getApprvrSecReview(), isSectionVerified));
		}
	}

	/**
	 * Set mof no by business registration no entered
	 * 
	 * @param String
	 *            regNo
	 * @return
	 */
	public void onBlurSetMofNo(Object obj) {
		String mofNo = "";
		try {
			Object objRegNoVal = PropertyUtils.getProperty(obj, "regNo");
			if (objRegNoVal != null) {
				MofAccountReqDTO reqDTO = new MofAccountReqDTO();
				reqDTO.setRegNo(objRegNoVal.toString());
				SmMofAccount acc = SmServiceLookup.getMofAccountSO()
						.retriveMofAccountBySupplierRegNo(reqDTO)
						.getMofAccount();
				if (acc != null) {
					mofNo = acc.getMofNo();
				}
			}
			PropertyUtils.setProperty(obj, "mofNo", mofNo);
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Get whether supplier already is business office and had approved special
	 * category
	 */
	private void setBusinessAndHadApprovedSpecialCatcode() {
		getManagedBean().setAlreadyBusinessSpecialCatcode(false);
		StringBuffer bf = new StringBuffer();
		if (!getManagedBean().getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				&& !SmCommonUtil.isCollectionEmpty(getManagedBean().getSmAppl()
						.getSmSupplierCategories())) {
			SmFacility hq = getHq();
			if (hq != null && hq.getFacilityType().equals("B"))
				for (SmSupplierCategory cat : getManagedBean().getSmAppl()
						.getSmSupplierCategories()) {
					if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
							&& cat.getPmCategoryL3().getHomeOperated() != DbValueConst.RECORD_STATUS_ACTIVE) {
						getManagedBean().setAlreadyBusinessSpecialCatcode(true);
						bf.append(" "
								+ cat.getPmCategoryL1().getCategoryL1Code()
								+ cat.getPmCategoryL2().getCategoryL2Code()
								+ cat.getPmCategoryL3().getCategoryL3Code()
								+ ",");
					}
				}
		}
		getManagedBean().setBusinessOfficeCatCodeMsg(
				SmCommonUtil.getMessage(
						"lbl.sm.not.allowed.change.factype.home.office", null)
						+ "<br /> - " + bf.toString());
		getManagedBean().setBusinessOfficeCatCodeMsg(
				StringUtils.removeEnd(getManagedBean()
						.getBusinessOfficeCatCodeMsg(), ","));
	}

	/**
	 * Get HQ
	 * 
	 * @return SmFacility
	 */
	private SmFacility getHq() {
		SmFacility hq = null;
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmFacilities())) {
			for (SmFacility fac : getManagedBean().getSmAppl()
					.getSmFacilities()) {
				if (StringUtils.isNotBlank(fac.getFacilityType())
						&& fac.getFacilityType().equals("H")
						|| fac.getFacilityType().equals("B")) {
					hq = fac;
					break;
				}
			}
		}
		return hq;
	}

	/**
	 * Add or remove verification count for sections.
	 * 
	 * @param sec
	 *            -SmApplSectionReview object
	 * @param isSectionVerified
	 *            - boolean
	 * @return the sm appl section review object
	 */
	private SmApplSectionReview assignVerificationCount(
			SmApplSectionReview sec, Boolean isSectionVerified) {
		if (isSectionVerified) {
			sec.setCheckCount(sec.getCheckCount() + 1);

		} else {
			sec.setCheckCount(sec.getCheckCount() - 1);

		}
		if (sec.getCheckCount() >= getManagedBean().getMaxReviewCount()) {
			sec.setVerifiedAll(Boolean.TRUE);
		} else {
			sec.setVerifiedAll(Boolean.FALSE);
		}

		return sec;
	}

	/**
	 * Save sm remarks.
	 * 
	 * @param remarks
	 *            - SmRemark object
	 */
	private void saveSmRemarks(SmRemark remarks) {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setSmRemark(remarks);
		ISmRemarksSO reqSO = (ISmRemarksSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISmRemarksSO.class);
		reqSO.saveSmRemarks(reqDTO);

	}

	/**
	 * Save sm cancellation attachment.
	 * 
	 * @param attach
	 *            -SmAttachment
	 */
	private void saveSmCancellationAttachment(SmAttachment attach) {
		SmAttachmentReqDTO reqDTO = new SmAttachmentReqDTO();
		reqDTO.setAttachment(attach);
		ISmAttachmentSO reqSO = (ISmAttachmentSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISmAttachmentSO.class);
		reqSO.updateSmAttachment(reqDTO);

	}

	/**
	 * Retrieve supporting document list.
	 * 
	 * @param reqDTO
	 *            -DocumentReqDTO
	 * @return the document res dto object
	 */
	private DocumentResDTO retrieveSupportingDocumentList(DocumentReqDTO reqDTO) {
		DocumentResDTO resDto = new DocumentResDTO();
		IDocumentSO reqSO = (IDocumentSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDocumentSO.class);
		resDto = reqSO.retrieveSupportingDocuments(reqDTO);
		return resDto;

	}

	/**
	 * Set financial organization list and hq flag when on change account
	 * purpose
	 */
	public void onChangeBankAccPurpose() {
		retrieveBankFinOrgListByType();
		if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("R")) {
			getManagedBean().getSelectedBank().setForHqFlg(
					Integer.parseInt(ParameterConst.FLAG_YES));
		}
	}

	/**
	 * Retrieve bank fin org list by type.
	 */
	public void retrieveBankFinOrgListByType() {
		boolean runQuery = true;
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		log.info("******* Selected Value **********"
				+ getManagedBean().getSelectedBank().getAccountPurpose());
		if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("P")) {
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
		} else if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("R")) {
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_REGISTRATION);
		} else if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("B")) {
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
		} else {
			this.getManagedBean().setFinOrgLs(new ArrayList<PmFinancialOrg>());
			runQuery = false;
		}
		if (runQuery) {
			IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
					.getInstance()
					.getServiceObject("pm", IFinancialOrgSO.class);
			log.info("******* Service Type ********" + reqDTO.getServiceType());
			FinancialOrgResDTO resDTO = reqSO
					.retrieveFinancialOrgListByServiceType(reqDTO);

			this.getManagedBean().setFinOrgLs(resDTO.getFinancialOrgList());
			if ((getManagedBean().getSelectedBank().getAccountPurpose()
					.equalsIgnoreCase("P") || getManagedBean()
					.getSelectedBank().getAccountPurpose()
					.equalsIgnoreCase("B"))
					&& getManagedBean().getSelectedBank().getSmSupplierBranch() == null) {
			}
		}
		log.info("****** After getting the fin org list *******"
				+ getManagedBean().getFinOrgLs().size());
	}

	/**
	 * Assign selected support doc.
	 * 
	 * @param doc
	 *            the doc
	 */
	public void assignSelectedSupportDoc(SmApplSupportDoc doc) {
		log.info("****** Assign Supporting Doc *******");
		getManagedBean().setSupportDoc(doc);
	}

	/**
	 * Handle and save file attachment for resubmit supproting doc file(s).
	 * 
	 * @param event
	 *            - the FileUploadEvent
	 */
	public void handleResubmitSupDocFileAttachment(FileUploadEvent event) {
		log.info("********handleFileAttachment********");
		if (event.getFile() != null) {
			log.info("***** File attached *******"
					+ event.getFile().getFileName());
			for (SmApplSupportDoc doc : getManagedBean().getSmAppl()
					.getSmApplSupportDocs()) {
				if (doc.getApplSupportDocId().compareTo(
						getManagedBean().getSupportDoc().getApplSupportDocId()) == 0) {
					log.info("**** This is the selected object ***** ");
					doc.getSmApplDocFiles()
							.get(0)
							.setFileName(
									StringUtils.replace(event.getFile()
											.getFileName(), " ", "-"));
					String filePath = Calendar.getInstance().get(Calendar.YEAR)
							+ "/SM/" + getManagedBean().getSmAppl().getApplNo()
							+ "/SD/";
					doc.getSmApplDocFiles().get(0).setFilePath(filePath);
					commonUploadFile(event, filePath);
				}
			}
		}
	}

	/**
	 * Handle and save file attachment for supporting documents
	 * 
	 * @param event
	 *            - the FileUploadEvent
	 */
	public void handleSupDocFileAttachment(FileUploadEvent event) {
		log.info("********handleFileAttachment********");
		if (event.getFile() != null) {
			log.info("***** File attached *******"
					+ event.getFile().getFileName());
			if (getManagedBean().getSmAppl().getSmApplSupportDocs()
					.contains(getManagedBean().getSupportDoc())) {
				log.info("******* Contains selected file ****");
				SmApplSupportDoc doc = getManagedBean().getSupportDoc();
				doc.getSmApplDocFiles()
						.get(0)
						.setFileName(
								StringUtils.replace(event.getFile()
										.getFileName(), " ", "-"));
				getManagedBean()
						.getSmAppl()
						.getSmApplSupportDocs()
						.set(getManagedBean().getSmAppl()
								.getSmApplSupportDocs()
								.indexOf(getManagedBean().getSupportDoc()),
								getManagedBean().getSupportDoc());
				String filePath = Calendar.getInstance().get(Calendar.YEAR)
						+ "/SM/APPLICATION/"
						+ getManagedBean().getSmAppl().getApplNo() + "/SD/";
				for (SmApplSupportDoc docs : getManagedBean().getSmAppl()
						.getSmApplSupportDocs()) {
					log.info(" *** The name is ****"
							+ docs.getSmApplDocFiles().get(0).getFileName());
					docs.getSmApplDocFiles().get(0).setFilePath(filePath);
				}

				commonUploadFile(event, filePath);
			}
		}

	}

	/**
	 * Handle and save file attachment for cancel mof registration.
	 * 
	 * @param event
	 *            - the FileUploadEvent
	 */
	public void handleCancelMofRegFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			log.info("***** File attached *******"
					+ event.getFile().getFileName());
			StringUtils.replace(event.getFile().getFileName(), " ", "-");
			getManagedBean().getCancellationAttachment().setFileName(
					StringUtils
							.replace(event.getFile().getFileName(), " ", "-"));
			log.info("***** File attached name to DB *******"
					+ getManagedBean().getCancellationAttachment()
							.getFileName());

			String filePath = Calendar.getInstance().get(Calendar.YEAR)
					+ "/SM/" + getManagedBean().getSmAppl().getApplNo()
					+ "/SD/";
			getManagedBean().getCancellationAttachment().setFilePath(filePath);
			commonUploadFile(event, filePath);

		}
	}

	/**
	 * Clone sm application draft from the stable or prev approved application.
	 * 
	 * @param id
	 *            - supplier Id
	 * @param applicationType
	 *            - the application type
	 * @param supplierType
	 *            - the supplier type
	 * @return the mof registration res dto object
	 */
	private MofRegistrationResDTO cloneSmApplicationDraft(long id,
			String applicationType, String supplierType) {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSupplierId(id);
			reqDto.setAppType(applicationType);
			reqDto.setSupplierType(supplierType);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.cloneSmApplicationDraft(reqDto);
			return resDTO;

		} catch (Exception e) {
			log.info("**** THIS IS THE EXCEPTION IN CLONE APPL ****");
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			mofRegistrationSO = null;
		}
	}

	/**
	 * Assign max count of sections to be review based on application.
	 * 
	 * @param appType
	 *            - the application type
	 * @param applCatType
	 *            - the applcation category type
	 * @param supplierType
	 *            - the supplier type
	 * @param businessType
	 *            - the business type
	 * @return the int - count of sections
	 */
	private int assignMaxReviewCount(String appType, String applCatType,
			String supplierType, String businessType) {
		return SmApplicationUtil.assignMaxReviewCount(appType, applCatType,
				supplierType, businessType, getManagedBean()
						.isReviewSiteVisit());
	}

	/**
	 * Ajax call when select country to populate states
	 * 
	 * @param SmAddress
	 *            addr
	 * @param String
	 *            panelName
	 */
	public void onChangeCountry(SmAddress addr, String panelName) {
		SmApplicationUtil.getInstance().populateStateByCountry(addr, panelName,
				getManagedBean());
		setRoleStaffAddrReqd();
	}

	/**
	 * Ajax call when select state to populate district
	 * 
	 * @param SmAddress
	 *            addr
	 * @param String
	 *            panelName
	 */
	public void onChangeState(SmAddress addr, String panelName) {
		SmApplicationUtil.getInstance().populateDistrictsByState(addr,
				panelName, getManagedBean());
		setRoleStaffAddrReqd();
	}

	/**
	 * Ajax call when select district to populate cities
	 * 
	 * @param SmAddress
	 *            addr
	 * @param String
	 *            panelName
	 */
	public void onChangeDistrict(SmAddress addr, String panelName) {
		SmApplicationUtil.getInstance().populateCitiesByDistrict(addr,
				panelName, getManagedBean());
		setRoleStaffAddrReqd();
	}

	/**
	 * Ajax call when select city to reload and set city by city id transient
	 * value
	 * 
	 * @param SmAddress
	 *            addr
	 */
	public void onChangeCity(SmAddress addr, String panelName) {
		addr.setPmCity(SmCommonUtil.getInstance().getCityById(addr.getCityId()));
		setRoleStaffAddrReqd();
	}

	/**
	 * Populate experience page child category list by parent category id.
	 * 
	 * @param parentId
	 *            - Long parent id
	 * @param catL2
	 *            - PmCategoryL2 object
	 * @param catL3
	 *            - PmCategoryL3 object
	 * 
	 */
	public void populateExperienceChildCategoryByParent(long parentId,
			PmCategoryL2 catL2, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL2> categoryL2s = SmApplicationUtil
					.retrieveExperienceChildCategoryList(parentId,
							getManagedBean());
			catL2.setPmCategoryL2s(categoryL2s);
			catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populate experience page sub child category by child id.
	 * 
	 * @param childId
	 *            - Long the child category id
	 * @param catL3
	 *            - PmCategoryL3 object
	 */
	public void populateExperienceSubChildCategoryByChild(long childId,
			PmCategoryL3 catL3) {
		try {
			List<PmCategoryL3> categoryL3s = SmApplicationUtil
					.retrieveExperienceSubChildCategoryList(childId,
							getManagedBean());
			catL3.setPmCategoryL3s(categoryL3s);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Populate and update child category list by parent category Id.
	 * 
	 * @param parentId
	 *            - Long parent id
	 * @param catL2
	 *            - PmCategoryL2 object
	 * @param catL3
	 *            - PmCategoryL3 object
	 */
	public void populateChildCategoryByParent(long parentId,
			PmCategoryL2 catL2, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL2> categoryL2s = SmApplicationUtil
					.retrieveChildCategoryList(parentId, getManagedBean());

			this.getManagedBean().setSupplierChildCatList(categoryL2s);
			catL2.setPmCategoryL2s(categoryL2s);

			this.getManagedBean().setSupplierSubChildCatList(
					new ArrayList<PmCategoryL3>());
			catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());

			resetChildCategory(catL2);
			resetSubChildCategory(catL3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Populate and update sub child category list by child category Id.
	 * 
	 * @param childId
	 *            - Long the child category id
	 * @param catL3
	 *            - PmCategoryL3 object
	 */
	public void populateSubChildCategoryByChild(long childId, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL3> categoryL3s = SmApplicationUtil
					.retrieveSubChildCategoryList(childId, getManagedBean());

			this.getManagedBean().setSupplierSubChildCatList(categoryL3s);
			catL3.setPmCategoryL3s(categoryL3s);

			resetSubChildCategory(catL3);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reset selected child category id to null.
	 * 
	 * @param catL2
	 *            -PmCategoryL2 object
	 */
	private void resetChildCategory(PmCategoryL2 catL2) {
		catL2.setCategoryL2Id(null);

	}

	/**
	 * Reset selected sub child category id to null.
	 * 
	 * @param catL3
	 *            - PmCategoryL3 object
	 */
	private void resetSubChildCategory(PmCategoryL3 catL3) {
		catL3.setCategoryL3Id(null);

	}

	/**
	 * Reset child categroy list in SmSupplier Category.
	 */
	private void resetChildCatList() {
		this.getManagedBean().setSupplierChildCatList(
				new ArrayList<PmCategoryL2>());
		this.getManagedBean().setSupplierSubChildCatList(
				new ArrayList<PmCategoryL3>());
	}

	/**
	 * Sets or filter the failities based on the facility type from all
	 * smFacilities.
	 * 
	 * @param smFacilities
	 *            - List of SmFacility in the application
	 */
	private void setApplicationFailities(List<SmFacility> smFacilities) {
		log.info("*** Existing Facility******");
		getManagedBean().setApplFactoryFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplDockyardFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplWorkShopFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplStoreFacilities(new ArrayList<SmFacility>());
		for (SmFacility smFac : smFacilities) {
			setFacilityDefaultPrices(smFac);
			if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
					|| smFac.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
				if (smFac.getSmAddress() == null
						|| smFac.getSmAddress().getAddressId() == null) {
					smFac.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
					SmApplicationUtil.getInstance().cloneAddress(
							smFac.getSmAddress(), getHqAddress());
				}
				if (!smFac.getOwnershipType().equalsIgnoreCase("O")) {
					getManagedBean().setBasicOffFacStates(
							SmCommonUtil.getStateList(smFac.getSmAddress()
									.getPmCountry().getCountryId()));
				}
				log.info("*********Setting Basic company value **********");
				smFac.setFacilityName(getManagedBean().getSmAppl()
						.getSmCompanyBasic().getCompanyName());
				this.getManagedBean().setBasicCompanyFacility(smFac);
				if (smFac.getSmAddress() != null
						&& smFac.getSmAddress().getPmCountry() != null
						&& smFac.getSmAddress().getPmState() != null
						&& smFac.getSmAddress().getPmCity() != null) {
					SmApplicationUtil
							.getInstance()
							.assignStateDistAndCityForSelVal(
									getManagedBean(),
									smFac.getSmAddress(),
									SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL);
				}
			}
			if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_FACTORY)) {
				getManagedBean().getApplFactoryFacilities().add(smFac);
			} else if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD)) {
				getManagedBean().getApplDockyardFacilities().add(smFac);
			} else if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP)) {
				getManagedBean().getApplWorkShopFacilities().add(smFac);
			} else if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_STORE)) {
				getManagedBean().getApplStoreFacilities().add(smFac);
			}
		}

	}

	/**
	 * Sets or filter the list the application share holders from SmPersonnels.
	 * 
	 * @param perList
	 *            - List of all SmPersonnels in the application
	 */
	private void setApplicationShareHolders(List<SmPersonnel> perList) {
		getManagedBean().setSmapplShareHolders(new ArrayList<SmPersonnel>());

		for (SmPersonnel per : perList) {

			if (per.isEquityOwner()) {
				getManagedBean().getSmapplShareHolders().add(per);
			}
		}

	}

	/**
	 * Sets the default values for new added personnel.
	 * 
	 * @param shr
	 *            - SmPersonnel object
	 */
	private void setDefaultPersonnelValues(SmPersonnel shr) {
		shr.setTitle(new PmSalutation());
		shr.setRevNo(1);
		shr.setSmAppl(getManagedBean().getSmAppl());
		shr.setBumiFlg(1);
		shr.setMuslimFlg(1);
		shr.setPmCountry(new PmCountry());
		shr.setPmRace(new PmRace());
		shr.setIdentificationType(new PmParameter());
		shr.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		shr.setSmStaffAcademics(new ArrayList<SmStaffAcademic>());
		shr.setSmStaffExperiences(new ArrayList<SmStaffExperience>());
		shr.setSmRelatedCompanies(new ArrayList<SmRelatedCompany>());
		shr.setSmAssociationBodies(new ArrayList<SmAssociationBody>());
		shr.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
	}

	/**
	 * Sets the default review section for approver in the SmAppSectionReview.
	 */
	private void setDefaultApproverReviewSection() {
		boolean containsAppRev = false;

		if (!getManagedBean().getSmAppl().getSmApplSectionReviews().isEmpty()) {
			for (SmApplSectionReview rev : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (rev.getRoleType().equalsIgnoreCase(
						SupplierManagementConst.SECTION_REV_ROLE_APP)) {
					getManagedBean().setApprvrSecReview(rev);
					containsAppRev = true;
				}
				if (rev.getRoleType().equalsIgnoreCase(
						SupplierManagementConst.SECTION_REV_ROLE_PO)) {
					getManagedBean().setPoSecReview(rev);
				}
			}
			if (getManagedBean().isReviewSiteVisit()
					&& !getManagedBean().getApprvrSecReview()
							.isSitevisitVerified()) {
				getManagedBean().getApprvrSecReview().setVerifiedAll(false);
			}
		}
		if (!containsAppRev) {
			SmApplSectionReview appRev = new SmApplSectionReview();
			appRev.setSmAppl(getManagedBean().getSmAppl());
			appRev.setRoleType(SupplierManagementConst.SECTION_REV_ROLE_APP);
			getManagedBean().setApprvrSecReview(appRev);
			getManagedBean().getSmAppl().getSmApplSectionReviews()
					.add(getManagedBean().getApprvrSecReview());
		}

	}

	/**
	 * Sets the default review section for PO in the SmAppSectionReview.
	 */
	private void setDefaultPoReviewSection() {

		boolean containsPORev = false;

		if (!getManagedBean().getSmAppl().getSmApplSectionReviews().isEmpty()) {
			for (SmApplSectionReview rev : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (rev.getRoleType().equalsIgnoreCase(
						SupplierManagementConst.SECTION_REV_ROLE_PO)) {
					getManagedBean().setPoSecReview(rev);
					containsPORev = true;
				}
			}
			if (getManagedBean().isReviewSiteVisit()
					&& !getManagedBean().getPoSecReview().isSitevisitVerified()) {
				getManagedBean().getPoSecReview().setVerifiedAll(false);
			}
		}
		if (!containsPORev) {
			SmApplSectionReview poRev = new SmApplSectionReview();
			poRev.setSmAppl(getManagedBean().getSmAppl());
			poRev.setRoleType(SupplierManagementConst.SECTION_REV_ROLE_PO);
			getManagedBean().setPoSecReview(poRev);
			getManagedBean().getSmAppl().getSmApplSectionReviews()
					.add(getManagedBean().getPoSecReview());
		}

	}

	/**
	 * Test printer.
	 */
	public void testPrinter() {
		log.info("********* Test printer actions ********  ");
	}

	/**
	 * Calculate share holder capital percentage for the supplier.
	 */
	public void calculateShareHolderPercent() {
		log.info("********* calculateShareHolderPercent ********  ");
		SmApplicationUtil.calculateShareHolderPercent(getManagedBean());
	}

	/**
	 * Calculate equity or share holding company's percentage for the supplier.
	 */
	public void calculateEquityCompanyPercent() {
		try {
			if (getManagedBean().getSelectedEquityCompany().getShareAmt() != null) {
				getManagedBean().getSelectedEquityCompany().setShareAmt(
						getManagedBean().getSelectedEquityCompany()
								.getShareAmt()
								.setScale(2, RoundingMode.HALF_UP));
			}
			getManagedBean().setEquiCompCapTotal(new BigDecimal(0));
			for (SmEquityCompany comp : getManagedBean().getSmAppl()
					.getSmEquityCompanies()) {
				if (comp.getShareAmt() == null) {
					comp.setShareAmt(new BigDecimal(0));
				}
				if (!(comp.getEquityCompanyId() == null && comp
						.equals(getManagedBean().getSelectedEquityCompany()))
						&& !(comp.getEquityCompanyId() != null && comp
								.getEquityCompanyId().equals(
										getManagedBean()
												.getSelectedEquityCompany()
												.getEquityCompanyId()))) {

					getManagedBean().setEquiCompCapTotal(
							getManagedBean().getEquiCompCapTotal()
									.add(comp.getShareAmt())
									.setScale(2, RoundingMode.HALF_UP));
				}
			}
			getManagedBean().setEquiCompCapTotal(
					getManagedBean()
							.getEquiCompCapTotal()
							.add(getManagedBean().getSelectedEquityCompany()
									.getShareAmt())
							.setScale(2, RoundingMode.HALF_UP));
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getCompaniesEquityTotal() != null
					&& getManagedBean().getEquiCompCapTotal().compareTo(
							getManagedBean().getSmAppl().getSmCompanyBasic()
									.getCompaniesEquityTotal()) <= 0) {
				BigDecimal mul = new BigDecimal(100);
				if (getManagedBean().getSmAppl().getSmCompanyBasic()
						.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
					getManagedBean().getSelectedEquityCompany()
							.setSharePercentage(
									getManagedBean()
											.getSelectedEquityCompany()
											.getShareAmt()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
				}

			} else {
				getManagedBean().getSelectedEquityCompany().setSharePercentage(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				getManagedBean().getSelectedEquityCompany().setShareAmt(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				throw new EpException(MessageConst.ERROR_722521);
			}
		} catch (NumberFormatException ne) {
			throw new EpException(MessageConst.ERROR_722522);
		}
	}

	/**
	 * Calculate capital amounts and percentage for the supplier.
	 */
	public void calculateCapital() {
		try {
			SmCommonUtil.calculateSmApplicationPaidUpCapitalSum(
					getManagedBean().getSmAppl().getSmCompanyBasic(),
					getManagedBean().getBusinessTypeVal());
			validatePaidCaptialAmt(false);
			SmCommonUtil
					.calculateSmApplicationPaidUpCapitalPercent(getManagedBean()
							.getSmAppl().getSmCompanyBasic());
			SmCommonUtil.calcAllEquityOwnerPercent(getManagedBean());
			getManagedBean().setBumiEquityPercentage(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getBumiPercent());

		} catch (NumberFormatException ne) {
			throw new EpException(MessageConst.ERROR_722522);
		}
	}

	public void handleGstSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (getManagedBean().getSelectedGstSuppDoc() != null) {

				// construct file path
				String filePath = SmCommonUtil
						.getUploadFilePath(SupplierManagementConst.GST_FILE_PATH)
						+ getManagedBean().getSmSupplier().getRegNo()
						+ "/"
						+ System.currentTimeMillis() + "/";

				// init suppDocFile (currently only default one file)
				SmGstSupplierDoc doc = getManagedBean().getSelectedGstSuppDoc();
				doc.setFileName(StringUtils.replace(event.getFile()
						.getFileName(), " ", "-"));
				doc.setFilePath(filePath);

				// set selected doc to main doc list
				setSuppDocFileAttachment(doc);

				// upload file
				commonUploadFile(event, filePath);
			}
		}

	}

	public void assignSelectedGstDoc(SmGstSupplierDoc doc) {
		getManagedBean().setSelectedGstSuppDoc(doc);
	}

	private void setSuppDocFileAttachment(SmGstSupplierDoc doc) {
		List<SmGstSupplierDoc> gstSuppDocList = null;
		if (getManagedBean().getCurrentPageType().equals(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)) {
			gstSuppDocList = getManagedBean().getCompanyBasicGst()
					.getSmGstSupplierDocs();
		} else if (getManagedBean().getCurrentPageType().equals(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH)) {
			gstSuppDocList = getManagedBean().getSelectedGst()
					.getSmGstSupplierDocs();
		}

		if (gstSuppDocList != null && !gstSuppDocList.isEmpty()) {
			Iterator<SmGstSupplierDoc> docVOIter = gstSuppDocList.iterator();
			while (docVOIter.hasNext()) {
				SmGstSupplierDoc docVO = docVOIter.next();
				if (docVO.getPmDocument().getDocumentId()
						.compareTo(doc.getPmDocument().getDocumentId()) == 0) {
					docVO = doc;
				}
			}
		}
	}

	/**
	 * Calc personnel list count for supplier.
	 */
	private void calcPersonnelListCount() {
		log.info("*** Start calcPersonnelListCount**** ");
		SmPersonnleCalcVO calcVO = SmCommonUtil.calcPersonnelListCount(
				getManagedBean().getSmAppl().getSmPersonnels(),
				getManagedBean().getSmAppl().getSmStaffTotals(),
				getManagedBean().getSmAppl().getSupplierType());
		getManagedBean().setPersonCalc(calcVO);

		// set already reach valid max authorized personnel and justification
		SmAuthorizedPersonnelCountVO countVO = SmApplicationUtil
				.getSmAuthorizedPersonnelCountVO(getManagedBean()
						.getSmSupplier().getSupplierId(), getManagedBean()
						.getSmAppl().getApplType(), getManagedBean()
						.getSmAppl().getApplId(), false);
		getManagedBean().setValidSmUserLimit(
				countVO.isReachAuthorizedPersonnelLimit());
		SmApplicationUtil.setPendingActivationPersonnelRoleBasedOnAuthLimit(
				getManagedBean().getSmAppl(), getManagedBean()
						.isValidSmUserLimit());

		log.info("*** END calcPersonnelListCount**** ");
	}

	/**
	 * Calculate bumi percentage totals for supplier.
	 */
	private void calculateBumiPercentageTotals() {
		SmPersonnleCalcVO calcVO = SmCommonUtil.calculateBumiPercentageTotals(
				getManagedBean().getPersonCalc(), getManagedBean().getSmAppl()
						.getSmPersonnels());
		getManagedBean().setPersonCalc(calcVO);
	}

	/**
	 * Sets the navigation for resubmit application.
	 * 
	 * @param applType
	 *            - type of application submitted
	 * 
	 */
	private void setNavigationForResubmitAppln(String applType) {
		SmApplicationUtil.setNavigationForResubmitAppln(applType);
	}

	/**
	 * Manages min and max equity holder limit for supplier.
	 * 
	 * @param personnel
	 *            - SmPersonnel object
	 */
	private void manageEquityLimit(SmPersonnel personnel) {
		getManagedBean().setValidEquityLimit(
				SmApplicationUtil.manageEquityLimit(personnel, getManagedBean()
						.getBusinessTypeVal(), getManagedBean()
						.getSmapplShareHolders()));
	}

	/**
	 * Assign supplier business type for screen validation purpose.
	 * 
	 * @param sup
	 *            - SmSupplier object
	 */
	private void assignSupplierBusinessType(SmSupplier sup) {
		getManagedBean().setBusinessTypeVal(
				SmApplicationUtil.assignSupplierBusinessType(sup));

	}

	/**
	 * Sets the application current pm status.
	 * 
	 * @param statusIdpm
	 *            - pm status id passed as String
	 */
	private void setApplPmStatus(String statusId) {
		StatusResDTO resDTO = SmApplicationUtil.setApplPmStatus(statusId);
		if (resDTO != null && resDTO.getPmStatus() != null) {
			getManagedBean().getSmAppl().setPmStatus(resDTO.getPmStatus());
		}
	}

	/**
	 * Sets the pm status desc.
	 * 
	 * @param statusId
	 *            - pm status id passed as String
	 */
	private void setPmStatusDesc(String statusId) {
		log.info("****** Status Id ******" + statusId);
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(Long.parseLong(statusId));
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusResDTO resDTO = reqSO.retrieveStatusDescById(reqDTO);
		if (resDTO != null && resDTO.getPmStatusDesc() != null) {
			getManagedBean().setCurrentStatus(
					resDTO.getPmStatusDesc().getStatusName());
		}
	}

	/**
	 * Assign Application Registration Status
	 * 
	 * @param regStatus
	 *            - PmRegStatus object
	 */
	private void assignApplicationRegistrationStatus(PmRegStatus regStatus) {
		if (getManagedBean().getSmAppl().getPmRegStatus() != null
				&& getManagedBean().getSmAppl().getPmRegStatus().getPmStatus() != null) {
			log.info("****** reg Status Id ******"
					+ regStatus.getPmStatus().getStatusId());
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(regStatus.getPmStatus().getStatusId());
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale()
					.getLanguage());
			IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IStatusSO.class);
			StatusResDTO resDTO = reqSO.retrieveStatusDescById(reqDTO);
			if (resDTO != null && resDTO.getPmStatusDesc() != null) {
				getManagedBean().setApplRegStatus(
						resDTO.getPmStatusDesc().getStatusName());
			} else {
				getManagedBean().setApplRegStatus(" ");
			}
		} else {
			getManagedBean().setApplRegStatus(" ");
		}
	}

	/**
	 * Assign value reqd to false when anyof the pop up is opened in the
	 * screen.Used for enable and disable screen validation dynamically.
	 */
	private void assignValReqdToTrue() {
		getManagedBean().setShowParentMsg(false);
		getManagedBean().setValueReqd(true);
	}

	/**
	 * Assign value reqd to false when anyof the pop up is closed in the
	 * screen.Used for enable and disable screen validation dynamically.
	 */
	private void assignValReqdToFalse() {
		getManagedBean().setShowParentMsg(true);
		getManagedBean().setValueReqd(false);
	}

	/**
	 * Validate supporting docs.
	 * 
	 * @return true, if successful
	 */
	private boolean validateSupportingDocs() {
		return SmApplicationUtil.validateSupportingDocs(getManagedBean()
				.getSmAppl().getSmApplSupportDocs());
	}

	/**
	 * Retrieve business type list.
	 */
	public void retrieveBusinessTypeList() {
		SelectItem[] selectItems = getDropdownCodeValues(SupplierManagementConst.SUPPLIER_BUSINESS_TYPE);
		List<SelectItem> businessTypeSelectItems = new ArrayList<SelectItem>();
		if (selectItems != null && selectItems.length > 0) {
			String bizType = getManagedBean().getSmSupplier().getBusinessType();
			for (SelectItem item : selectItems) {
				if (item.getValue() != null) {
					if (bizType
							.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
							|| bizType
									.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)) {

						if (item.getValue()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
								|| item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)) {
							businessTypeSelectItems.add(item);
						}
					} else if (bizType
							.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
							|| bizType
									.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)) {
						if (item.getValue()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
								|| item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)) {
							businessTypeSelectItems.add(item);
						}
					} else if (bizType
							.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
							|| bizType
									.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)) {
						if (item.getValue()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
								|| item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)) {
							businessTypeSelectItems.add(item);
						}
					}
				}
			}
		}

		// sort
		if (CollectionUtils.isNotEmpty(businessTypeSelectItems)) {
			Collections.sort(businessTypeSelectItems,
					new Comparator<SelectItem>() {
						@Override
						public int compare(final SelectItem s1,
								final SelectItem s2) {
							return StringUtil
									.upperCase(s1.getLabel())
									.compareTo(
											StringUtil.upperCase(s2.getLabel()));
						}
					});
		}
		this.getManagedBean()
				.setBusinessTypeLs(
						businessTypeSelectItems
								.toArray(new SelectItem[businessTypeSelectItems
										.size()]));
	}

	/**
	 * Validate paid captial amt.
	 * 
	 * @param setNavigationCurrentPage
	 *            -boolean value determining the navigation to current page
	 */
	private void validatePaidCaptialAmt(boolean setNavigationCurrentPage) {
		SmApplicationUtil.validatePaidCaptialAmt(setNavigationCurrentPage,
				getManagedBean(), currentTab);
	}

	/**
	 * Manage personnel roles for the selected SmPersonnel in the application.
	 */
	public void managePersonnelRoles(String updateAddressDetails) {
		log.info("**** Enter Manage Roles  ******");
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
			log.info(" **** Role management for contractor ***********");
			if (getManagedBean().getSelectedShareHolder().isEquityOwner()
					|| getManagedBean().getSelectedShareHolder().isDirector()
					|| getManagedBean().getSelectedShareHolder()
							.isHigherManagement()
					|| getManagedBean().getSelectedShareHolder().isManagement()) {
				getManagedBean().getSelectedShareHolder().setClerical(false);
				getManagedBean().getSelectedShareHolder().setDisableStaff(true);
			} else {
				getManagedBean().getSelectedShareHolder()
						.setDisableStaff(false);
			}

		} else if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			log.info(" **** Role management for consultant ***********");
			if (getManagedBean().getSelectedShareHolder().isEquityOwner()
					|| getManagedBean().getSelectedShareHolder().isDirector()
					|| getManagedBean().getSelectedShareHolder()
							.isHigherManagement()
					|| getManagedBean().getSelectedShareHolder().isProf()
					|| getManagedBean().getSelectedShareHolder().isSemiProf()) {
				getManagedBean().getSelectedShareHolder().setClerical(false);
				getManagedBean().getSelectedShareHolder().setDisableStaff(true);
			} else if (getManagedBean().getSelectedShareHolder().isManagement()
					&& (getManagedBean().getSelectedShareHolder()
							.isEquityOwner() || getManagedBean()
							.getSelectedShareHolder().isDirector())) {
				getManagedBean().getSelectedShareHolder().setClerical(false);
				getManagedBean().getSelectedShareHolder().setDisableStaff(true);
			} else {
				getManagedBean().getSelectedShareHolder()
						.setDisableStaff(false);
			}

			if (getManagedBean().getSelectedShareHolder().isEquityOwner()
					|| getManagedBean().getSelectedShareHolder().isDirector()
					|| getManagedBean().getSelectedShareHolder()
							.isHigherManagement()
					|| getManagedBean().getSelectedShareHolder().isProf()
					|| getManagedBean().getSelectedShareHolder().isManagement()) {
				getManagedBean().getSelectedShareHolder().setSemiProf(false);
				getManagedBean().getSelectedShareHolder().setDisableSemiProf(
						true);
			} else {
				getManagedBean().getSelectedShareHolder().setDisableSemiProf(
						false);
			}
		}

		if (getManagedBean().getSelectedShareHolder().getPersonnelId() != null
				&& updateAddressDetails.equalsIgnoreCase("Y")) {
			if (getManagedBean().getSelectedShareHolder().isEquityOwner()
					|| getManagedBean().getSelectedShareHolder().isDirector()
					|| getManagedBean().getSelectedShareHolder()
							.isHigherManagement()) {
				getManagedBean().getShareHolderResdAddr().setSameAsPerm(false);
				getManagedBean().getShareHolderOffAddr().setSameAsPerm(false);
				assignSelShareHolderAddress(getManagedBean()
						.getSelectedShareHolder());
			} else {
				getManagedBean().getShareHolderResdAddr().setSameAsPerm(true);
				getManagedBean().getShareHolderOffAddr().setSameAsPerm(true);
			}
		}
	}

	/**
	 * Download selected supporting document.
	 * 
	 * @param fileName
	 *            -the file name
	 * @param filePath
	 *            - the file path
	 */
	public void downloadSelectedSuppDoc(String fileName, String filePath) {
		String finalFilePath = filePath + fileName;
		try {
			commonDownload(finalFilePath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finalFilePath = null;
	}

	/**
	 * Download signature supporting document template.
	 * 
	 * @param fileName
	 *            - the file name
	 * @param filePath
	 *            - the file path
	 */
	public void downloadSignatureSuppDoc(String fileName, String filePath) {
		String finalFilePath = filePath;
		try {
			commonDownload(finalFilePath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finalFilePath = null;
	}

	/**
	 * Print Supplier Profile
	 * 
	 * @throws IOException
	 */
	public void printSupplierProfile() throws IOException {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("PAPPLNO", getManagedBean().getSmAppl().getApplNo());
		generateReport(ReportConst.SM_SUPPLIER_UNREGIS_REPORT, paramMap);
	}

	/**
	 * Gets the file.
	 * 
	 * @return file -StreamedContent
	 */
	public StreamedContent getFile() {
		return file;
	}

	/**
	 * Sets the file.
	 * 
	 * @param file
	 *            - StreamedContent
	 * 
	 */
	public void setFile(StreamedContent file) {
		this.file = file;
	}

	/**
	 * Sets the url for revision application.
	 */
	public void setRevUrl() {
		String url = SmCommonUtil.getEpBaseUrl() + "/view-revision-application";

		String revApprvrAppNo = "";
		String revSupplAppNo = "";
		if (getManagedBean().getSmAppl() != null) {
			if (getManagedBean().getSmAppl().getApprovedSerFile() != null
					&& StringUtils.isNotBlank(getManagedBean().getSmAppl()
							.getApprovedSerFile())) {
				revApprvrAppNo = getManagedBean()
						.getSmAppl()
						.getApprovedSerFile()
						.substring(
								getManagedBean().getSmAppl()
										.getApprovedSerFile().lastIndexOf('/') + 1,
								getManagedBean().getSmAppl()
										.getApprovedSerFile().indexOf("_APP"))
						+ "_APP";
			}

			revSupplAppNo = getManagedBean().getSmAppl().getApplNo() == null ? ""
					: getManagedBean().getSmAppl().getApplNo() + "_SUP";
		}

		getManagedBean().setRevisionApproverAppNoParam(revApprvrAppNo);
		getManagedBean().setRevisionSupplierAppNoParam(revSupplAppNo);
		getManagedBean().setRevisionBaseUrl(url);
		setRevisionApplicationType();
	}

	/**
	 * Sets the revision application type.
	 */
	private void setRevisionApplicationType() {
		String applType = "";

		if (getManagedBean().getRevisionApproverAppNoParam() != null
				&& StringUtils.isNotBlank(getManagedBean()
						.getRevisionApproverAppNoParam())
				&& getManagedBean().getRevisionApproverAppNoParam().length() > 2) {
			applType = Character.toString(getManagedBean()
					.getRevisionApproverAppNoParam().charAt(1));
		}
		getManagedBean().setRevisionApplType(applType);
	}

	/**
	 * Remove un selected admin from the selected personnel list.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 * 
	 */
	public void unSelectAdmin(AjaxBehaviorEvent e) {
		if (getManagedBean().getSelectedShareHolders() != null) {
			Iterator<SmPersonnel> iterator = getManagedBean()
					.getSelectedShareHolders().iterator();
			while (iterator.hasNext()) {
				SmPersonnel personnel = iterator.next();
				if ((personnel.getEpRole() != null && (personnel.getEpRole()
						.equals(RoleCode.SUPPLIER_TEMP.value())
						|| personnel.getEpRole().equalsIgnoreCase(
								RoleCode.MOF_SUPPLIER_ADMIN.value()) || personnel
						.getEpRole().equalsIgnoreCase(
								RoleCode.BASIC_SUPPLIER_ADMIN.value())))
						|| (personnel.isPrevApprovedDirector() || personnel
								.isPrevApprovedEquityOwner())) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * Auto ticked and disabled hq facility
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 * 
	 */
	public void autoTickedHq(AjaxBehaviorEvent e) {
		setHqFacilityInSelectedFacility();
	}

	/**
	 * On click check or uncheck check box for site visit associate company
	 * assignment
	 */
	public void onCheckedSiteVisitAssocCompanyAddress() {
		getManagedBean().setSelectedAssocCompanies(
				new ArrayList<SmAssociateCompany>());
		for (SmSiteVisitFacilityVO siteVisit : getManagedBean()
				.getSiteVisitFacVOList()) {
			if (!SmCommonUtil.isCollectionEmpty(siteVisit.getAssocCompanies())) {
				for (SmAssociateCompany assoc : siteVisit.getAssocCompanies()) {
					if (assoc.isSelected()) {
						getManagedBean().getSelectedAssocCompanies().add(assoc);
					}
				}
			}
		}
	}

	/**
	 * On click check or uncheck check box for site visit facility address
	 * assignment
	 */
	public void onCheckedSiteVisitFacilityAddress() {
		getManagedBean().setSelectedFactoryFacilities(
				new ArrayList<SmFacility>());
		for (SmSiteVisitFacilityVO siteVisit : getManagedBean()
				.getSiteVisitFacVOList()) {
			if (!SmCommonUtil.isCollectionEmpty(siteVisit.getFacilities())) {
				for (SmFacility fac : siteVisit.getFacilities()) {
					if (fac.isSelected()) {
						getManagedBean().getSelectedFactoryFacilities()
								.add(fac);
					}
				}
			}
		}
		setSelectedSpecialCategoriesForSiteVisit();
	}

	/**
	 * On click check or uncheck check box for site visit branches assignment
	 */
	public void onCheckedSiteVisitBranchAddress() {
		getManagedBean().setSelectedBranches(new ArrayList<SmSupplierBranch>());
		for (SmSiteVisitFacilityVO siteVisit : getManagedBean()
				.getSiteVisitFacVOList()) {
			if (!SmCommonUtil.isCollectionEmpty(siteVisit.getSuppBranches())) {
				for (SmSupplierBranch br : siteVisit.getSuppBranches()) {
					if (br.isSelected()) {
						getManagedBean().getSelectedBranches().add(br);
					}
				}
			}
		}
	}

	/**
	 * Removed un select apporved categories from the list.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void unSelectApporvedCategories(AjaxBehaviorEvent e) {
		if (getManagedBean().getDeleteNewApplCategoryList() != null
				&& getManagedBean().isSupplierUser()) {
			Iterator<SmSupplierCategory> iterator = getManagedBean()
					.getDeleteNewApplCategoryList().iterator();
			while (iterator.hasNext()) {
				SmSupplierCategory cat = iterator.next();
				if ((cat.getRecordStatusFlg() != null && cat
						.getRecordStatusFlg().compareTo(
								SupplierManagementConst.APPROVED_RECORD_STATUS) == 0)
						|| (cat.getApprovedDate() != null)) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * Retrieve identity type from PM.
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getPmIdTypeDropdownValues() {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);
		Map<Long, Object> identityType = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityType.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}

		List<SelectItem> itemList = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);

		for (SelectItem selectItem : items) {

			if (selectItem.getValue() == null) {
				itemList.add(selectItem);
			} else {
				PmParameter pmParameter = (PmParameter) identityType
						.get(selectItem.getValue());
				if (!pmParameter.getParameterCode().equals("A")) {
					itemList.add(selectItem);
				}
			}

		}

		return itemList.toArray(new SelectItem[itemList.size()]);

	}

	/**
	 * Initiate the application category type i.e. normal or special.
	 */
	private void initApplCategoryType() {
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
			if (getManagedBean().getBasicCompanyFacility() != null
					&& getManagedBean()
							.getBasicCompanyFacility()
							.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)) {
				if (getManagedBean().getSmAppl() != null) {
					getManagedBean()
							.getSmAppl()
							.setApplCategoryType(
									SupplierManagementConst.SM_APPL_CATEGORY_TYPE_NORMAL);
					SmApplicationUtil
							.setApplicationCategorySelected(getManagedBean());
					assignApplnCategoryType();
				}
			}
		}
	}

	/**
	 * Display bumi recommend note for the application by supplier in managed
	 * bean.
	 */
	private void displayBumiRecommendNote() {
		log.info("*** Start displayBumiRecommendNote *******");
		getManagedBean().setBumiRecommendationNote("");
		if (getManagedBean().getSmAppl() != null) {
			String applType = getManagedBean().getSmAppl().getApplType() == null ? ""
					: getManagedBean().getSmAppl().getApplType();
			String suppType = "";

			if (getManagedBean().isSupplierUser()) {
				if (applType.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
					suppType = StringUtils.isNotBlank(getManagedBean()
							.getSmAppl().getSupplierType()) ? getManagedBean()
							.getSmAppl().getSupplierType() : getManagedBean()
							.getNewSupplierType();
				} else {
					suppType = getManagedBean().getSmAppl().getSupplierType() == null ? ""
							: getManagedBean().getSmAppl().getSupplierType();
				}
			} else {
				suppType = getManagedBean().getSmAppl().getSupplierType() == null ? ""
						: getManagedBean().getSmAppl().getSupplierType();
			}

			if (getManagedBean().getBumiEquityPercentage() == null) {
				calculateCapital();
			}
			getManagedBean().setBumiRecommendationNote(
					SmCommonUtil.displayBumiRecommendNote(getManagedBean()
							.getSmAppl().getSmSupplierBanks(), getManagedBean()
							.getPersonCalc(), getManagedBean()
							.getCurrentPageType(), applType, suppType,
							getManagedBean().getBumiEquityPercentage(),
							getManagedBean().isSupplierUser()));
		}
		log.info("*** End displayBumiRecommendNote ********");
	}

	/**
	 * Initiate the business premise of the supplier in the managed bean.
	 */
	private void initBusinessPremise() {
		getManagedBean().setDisabledHomeOffice(false);
		String suppType = "";
		if (getManagedBean().getSmAppl() != null) {
			suppType = StringUtils.isNotBlank(getManagedBean()
					.getNewSupplierType()) ? getManagedBean()
					.getNewSupplierType() : getManagedBean().getSmAppl()
					.getSupplierType();
		}
		// hide home office for consultant
		if (suppType.equalsIgnoreCase("J")
				&& getManagedBean().getBasicCompanyFacility() != null) {
			getManagedBean().setDisabledHomeOffice(true);
			getManagedBean().getBasicCompanyFacility().setFacilityType(
					SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE);

		}
	}

	/**
	 * Update and set all categories, added in the company experience section to
	 * the managed bean mapping
	 */
	public void selectAllCompanyExperinceCategories() {

		if (getManagedBean().isSelectedAll()) {
			getManagedBean().setSelected(true);
			getManagedBean().setSelectedCompExpCats(
					getManagedBean().getSelectedCompanyExperience()
							.getSmCompanyExperienceCats());
		} else {
			getManagedBean().setSelected(false);
			getManagedBean().setSelectedCompExpCats(null);

		}

	}

	/**
	 * Update and set selected category, added in the company experience section
	 * to the managed bean mapping
	 * 
	 * @param index
	 *            - int value of the selected index
	 **/
	public void selectedCompanyExperinceCategory(int index) {

		List<SmCompanyExperienceCat> selectedCompExpCatsList = getManagedBean()
				.getSelectedCompExpCats();

		if (getManagedBean().isSelected()) {
			if (selectedCompExpCatsList == null) {
				selectedCompExpCatsList = new ArrayList<SmCompanyExperienceCat>();
			}
			selectedCompExpCatsList.add(getManagedBean()
					.getSelectedCompanyExperience()
					.getSmCompanyExperienceCats().get(index));

		} else {

			if (selectedCompExpCatsList.contains(getManagedBean()
					.getSelectedCompanyExperience()
					.getSmCompanyExperienceCats().get(index))) {
				selectedCompExpCatsList.remove(getManagedBean()
						.getSelectedCompanyExperience()
						.getSmCompanyExperienceCats().get(index));
			}
		}
		getManagedBean().setSelectedCompExpCats(selectedCompExpCatsList);
	}

	/**
	 * Trim personnel passport value of the selected shareholder in the managed
	 * bean.
	 */
	public void trimPersonnelPassportValue() {
		String newPassportVal = "";
		if (StringUtils.isNotBlank(this.getManagedBean()
				.getSelectedShareHolder().getIdentificationNo())) {
			newPassportVal = this.getManagedBean().getSelectedShareHolder()
					.getIdentificationNo().replaceAll("\\s", "");
			this.getManagedBean().getSelectedShareHolder()
					.setIdentificationNo(newPassportVal);
		}
	}

	/**
	 * Gets the my identity status description based on the responsecode.
	 * 
	 * @param identityResponseCode
	 *            - the identity response code
	 * @return String - MY Identity status description value
	 */
	public String getMyIdentityStatusDescription(String identityResponseCode) {
		StringBuffer sb = new StringBuffer();
		sb.append("lbl.sm.myIdentityCode.");
		if (StringUtils.isNotBlank(identityResponseCode)) {
			sb.append(identityResponseCode);
		} else {
			sb.append("emptyVal");
		}
		log.debug("*** Value from getMyIdentityStatusDescription output is ****** "
				+ sb.toString());
		return SmCommonUtil.getMessage(sb.toString(), null);

	}

	/**
	 * Sets the share holder for view only.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object
	 */
	private void setShareHolderForViewOnly(SmPersonnel smPersonnel) {
		getManagedBean().setShowPersonnelViewOnly(
				isPersonnelViewOnly(smPersonnel));
		if (getManagedBean().isShowPersonnelViewOnly()) {
			getManagedBean()
					.setSelectedShareholderIdentificationType(
							SmCommonUtil
									.getParamDescriptionByLanguageAndPmParameter(getManagedBean()
											.getSelectedShareHolder()
											.getIdentificationType()));
		}
	}

	/**
	 * Checks if is personnel view only.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object to check
	 * @return true, if is personnel view only
	 */
	public boolean isPersonnelViewOnly(SmPersonnel smPersonnel) {
		boolean retVal = false;
		if (smPersonnel.isPrevApprovedDirector()
				|| smPersonnel.isPrevApprovedEquityOwner()) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Checks the record status and Sets the disabled shareholder name and
	 * nationality.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object to be checked or validated
	 */
	private void setDisabledShareholderNameAndNationality(
			SmPersonnel smPersonnel) {
		boolean retVal = false;
		try {
			if (smPersonnel.getPmUser() != null
					&& smPersonnel.getPmUser().getRecordStatusFlg() == 1) {
				retVal = true;
			} else if (SmApplicationUtil.isPersonnelCreatedInBasic(smPersonnel,
					getManagedBean().getSmAppl().getSmSupplier()
							.getSupplierId())) {
				retVal = true;
			}
		} catch (Exception ex) {
			log.info(ex.getMessage());
		}

		getManagedBean().setDisabledShareholderPersonnelNameAndNationality(
				retVal);
	}

	/**
	 * Check if pending category status.
	 * 
	 * @param recordStatusFlg
	 *            - the record status integer value
	 * @return true, if successful
	 */
	public boolean checkIfPendingCategoryStatus(Integer recordStatusFlg) {
		log.info("****** checkIfPendingCategoryStatus *********"
				+ recordStatusFlg);
		if (recordStatusFlg != null && recordStatusFlg.compareTo(8) == 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Get valid epRole list for personnel
	 * 
	 * @return List<SelectItem> epRoles
	 */
	public List<SelectItem> getPersonnelValidEpRoles() {
		List<SelectItem> ePRoleList = new ArrayList<SelectItem>();
		SelectItem roleToRemove = null;
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getePRoleList())) {
			ePRoleList = getManagedBean().getePRoleList();
			if (getManagedBean().getSmAppl().getSmCompanyBasic() != null
					&& !getManagedBean().getSmAppl().getSmCompanyBasic()
							.isWithFederal()) {
				for (SelectItem role : getManagedBean().getePRoleList()) {
					if (role.getValue().equals(RoleCode.CM_USER.value())) {
						roleToRemove = role;
						break;
					}
				}
			}
		}
		if (roleToRemove != null) {
			ePRoleList.remove(roleToRemove);
		}
		return ePRoleList;
	}

	/**
	 * Get epRole from select items list
	 * 
	 * @param String
	 *            roleCode
	 * @return String
	 */
	public String getEpRoleLabelByRole(String roleCode) {
		String retVal = "";
		for (SelectItem item : getManagedBean().getePRoleList()) {
			if (item.getValue().equals(roleCode)) {
				retVal = item.getLabel();
				break;
			}
		}
		return retVal;
	}

	/**
	 * Get list of academic qualification
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getAcademicQualifications() {
		String suppType = StringUtils.isNotBlank(getManagedBean()
				.getNewSupplierType()) ? getManagedBean().getNewSupplierType()
				: getManagedBean().getSmAppl().getSupplierType();
		List<SelectItem> finalList = getManagedBean().getAcadQualifications();

		if (getManagedBean().getSelectedShareHolder() != null
				&& StringUtils.isNotBlank(suppType) && suppType.equals("J")) {
			if (getManagedBean().getCertEduLevel() != null
					&& getManagedBean().getDiplomaEduLevel() != null
					&& (getManagedBean().getSelectedShareHolder().isProf()
							|| getManagedBean().getSelectedShareHolder()
									.isSemiProf() || getManagedBean()
							.getSelectedShareHolder().isClerical())) {
				finalList = new ArrayList<SelectItem>();
				for (SelectItem tempItem : getManagedBean()
						.getAcadQualifications()) {
					if (tempItem.getValue() != null) {
						if (getManagedBean()
								.getCertEduLevel()
								.getEduLevelId()
								.compareTo(
										Long.parseLong(tempItem.getValue()
												.toString())) == 0) {
							if (getManagedBean().getSelectedShareHolder()
									.isClerical()) {
								finalList.add(tempItem);
							}
						} else if (getManagedBean()
								.getDiplomaEduLevel()
								.getEduLevelId()
								.compareTo(
										Long.parseLong(tempItem.getValue()
												.toString())) == 0) {
							if (getManagedBean().getSelectedShareHolder()
									.isClerical()
									|| getManagedBean()
											.getSelectedShareHolder()
											.isSemiProf()) {
								finalList.add(tempItem);
							}
						} else {
							finalList.add(tempItem);
						}
					} else {
						finalList.add(tempItem);
					}
				}
			}
		}
		return finalList;
	}

	/**
	 * Save supplier business type if got changes
	 * 
	 * @param currPage
	 */
	private void saveSupplierBusinesType(String currPage) {
		if (currPage
				.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)
				&& !getManagedBean().isDisabledBusinessType()) {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSmSupplier(getManagedBean().getSmSupplier());
			SmServiceLookup.getMofRegistrationSO()
					.updateSupplierProfile(reqDTO);
		}
	}

	public void addNewCategoryAction(PmCategoryL3 pmCategoryL3) {

		log.info("pmCategoryL3.getCategoryName(): "
				+ pmCategoryL3.getCategoryName());

		SmSupplierCategory categ = new SmSupplierCategory();
		categ.setRevNo(1);
		categ.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
		if (getManagedBean().getSmAppl().getApplCategoryType()
				.equalsIgnoreCase("S")) {
			categ.setSpecialCategory(true);
		}
		categ.setSmAppl(getManagedBean().getSmAppl());
		categ.setSupplierType(getManagedBean().getSmAppl().getSupplierType());
		getManagedBean().setSelectedCategory(categ);

		categ.setPmCategoryL3(pmCategoryL3);
		categ.setPmCategoryL2(pmCategoryL3.getPmCategoryL2());
		categ.setPmCategoryL1(pmCategoryL3.getPmCategoryL2().getPmCategoryL1());
		getManagedBean().setSelectedCategory(categ);
		saveSelectedCategory();

		log.info("getManagedBean().getActiveCategoryList(): "
				+ getManagedBean().getActiveCategoryList());

	}

	public void prerenderDirectMofRegistration() {

		ISupplierSearchSO supplierSearchSO = null;
		SmSupplier smSupplier = null;
		try {
			supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
			supplierReqDTO.setAllStatuses(true);
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);
			smSupplier = smResDTO.getResultBean().getResultList().get(0);
		} finally {
			supplierSearchSO = null;
		}

		getManagedBean().setNewSupplierType(
				smSupplier.getSmAppl().getSupplierType());
		String prerenderUrl = proceedToApplication("N");
		log.info("prerenderUrl: " + prerenderUrl);

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"/xhtml/sm/NewMofApplication/mofRegSmUser_BaseWizardPage.xhtml");
	}

	/**
	 * Redirect to Previous or Original Application Details
	 * 
	 * @param String
	 *            linkType
	 */
	public void redirectToRevisionSupplierProfilePage(String linkType) {
		String applNo = linkType.equals("O") ? getManagedBean()
				.getRevisionSupplierAppNoParam() : getManagedBean()
				.getRevisionApproverAppNoParam();
		String applType = linkType.equals("O") ? getManagedBean().getSmAppl()
				.getApplType() : getManagedBean().getRevisionApplType();
		String currTab = Integer.toString(getManagedBean().getTabActiveIndex());
		SmCommonUtil.redirectToExternalUrl(getManagedBean()
				.getRevisionBaseUrl()
				+ "?applicationNo="
				+ applNo
				+ "&applicationType=" + applType + "&currentTab=" + currTab);
	}

	/**
	 * Revert po application to supplier application data
	 */
	public void revertToOriginalApplication() {
		RevertApplicationReqDTO reqDTO = new RevertApplicationReqDTO();
		reqDTO.setSmAppl(getManagedBean().getSmAppl());
		getManagedBean().setSmAppl(
				SmServiceLookup.getRevertApplicationSO()
						.revertToSupplierOriginalApplication(reqDTO)
						.getSmAppl());
		initSmCompanyBasic(getManagedBean().getSmAppl());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_721033,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
	}

	/**
	 * Initialized company basic to ManagedBean
	 * 
	 * @param SmAppl
	 *            smAppl
	 */
	private void initSmCompanyBasic(SmAppl smAppl) {
		if (smAppl.getSmCompanyBasic() != null) {
			if (smAppl.getSmCompanyBasic().getSmCompanyAddresses() != null
					&& !smAppl.getSmCompanyBasic().getSmCompanyAddresses()
							.isEmpty()) {
				SmCompanyAddress comAdd = smAppl.getSmCompanyBasic()
						.getSmCompanyAddresses().get(0);
				SmAddress addr = comAdd.getSmAddress();
				SmApplicationUtil.setPmStateDropdownValuesForBasicInfo(this
						.getManagedBean(), addr.getPmCountry().getCountryId());
				this.getManagedBean().setBasicCompanyDistricts(
						SmCommonUtil.getDistrictList(addr.getPmState()
								.getStateId()));
				getManagedBean().setPrevBasicStateId(
						addr.getPmState().getStateId());
				this.getManagedBean().setBasicCompanyCities(
						SmCommonUtil.getCityList(addr.getPmDistrict()
								.getDistrictId()));
			} else {
				SmCompanyAddress comAdd = new SmCompanyAddress();
				SmAddress addr = SmApplicationUtil
						.setDefaultAddr(getManagedBean().getDefPmCountry());
				comAdd.setSmAddress(addr);
				List<SmCompanyAddress> addrs = new ArrayList<SmCompanyAddress>();
				addrs.add(comAdd);
				smAppl.getSmCompanyBasic().setSmCompanyAddresses(addrs);
			}
		}
	}

	/**
	 * Get previous approved personnel
	 * 
	 * @param currPersonnel
	 * @return SmPersonnel
	 */
	private SmPersonnel getPrevApprovedPersonnel(SmPersonnel currPersonnel) {
		List<SmPersonnel> prevPersonnels = getManagedBean()
				.getPrevApprovedPersonnels() == null
				|| getManagedBean().getPrevApprovedPersonnels().isEmpty() ? SmCommonUtil
				.getBasicPersonnelsBySupplierId(getManagedBean().getSmAppl()
						.getSmSupplier().getSupplierId()) : getManagedBean()
				.getPrevApprovedPersonnels();

		SmPersonnel retVal = null;

		if (currPersonnel != null
				&& StringUtils.isNotBlank(currPersonnel.getIdentificationNo())
				&& prevPersonnels != null && !prevPersonnels.isEmpty()) {
			for (SmPersonnel prevPersonnel : prevPersonnels) {
				if (prevPersonnel.getIdentificationNo().equals(
						currPersonnel.getIdentificationNo())) {
					retVal = prevPersonnel;
				}
			}
		}

		return retVal;
	}

	/**
	 * Set address fields whether need to be mandatory
	 */
	public void setRoleStaffAddrReqd() {
		getManagedBean().setRoleStaffAndPermAddrReqd(false);
		getManagedBean().setRoleStaffAndMailAddrReqd(false);
		if (getManagedBean().getShareHolderPermAddr() != null
				&& getManagedBean().getShareHolderPermAddr().getSmAddress() != null) {
			SmAddress permAddress = getManagedBean().getShareHolderPermAddr()
					.getSmAddress();
			getManagedBean().setRoleStaffAndPermAddrReqd(
					isAddressReqdForRoleStaff(permAddress));
		}
		if (getManagedBean().getShareHolderMailAddr() != null
				&& getManagedBean().getShareHolderMailAddr().getSmAddress() != null) {
			SmAddress mailAddress = getManagedBean().getShareHolderMailAddr()
					.getSmAddress();
			getManagedBean().setRoleStaffAndMailAddrReqd(
					isAddressReqdForRoleStaff(mailAddress));
		}
	}

	/**
	 * Get whether address fields is mandatory for role staff
	 * 
	 * @param smAddress
	 * @return boolean
	 */
	private boolean isAddressReqdForRoleStaff(SmAddress smAddress) {
		boolean retVal = false;
		if (getManagedBean().getSelectedShareHolder().isClerical()) {
			if (StringUtils.isNotBlank(smAddress.getAddress1())) {
				retVal = true;
			} else if (smAddress.getPmState() != null
					&& smAddress.getPmState().getStateId() != null) {
				retVal = true;
			} else if (smAddress.getPmDistrict() != null
					&& smAddress.getPmDistrict().getDistrictId() != null) {
				retVal = true;
			} else if (smAddress.getPmCity() != null
					&& smAddress.getPmCity().getCityId() != null) {
				retVal = true;
			} else if (StringUtils.isNotBlank(smAddress.getPostcode())) {
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Validation for saving address for role staff
	 */
	private void validateSaveAddressForRoleStaff() {
		StringBuffer sb = new StringBuffer();

		if (getManagedBean().getShareHolderPermAddr().getSmAddress() != null) {
			SmAddress permAddr = getManagedBean().getShareHolderPermAddr()
					.getSmAddress();
			if (isAddressReqdForRoleStaff(permAddr)) {
				constructSaveStaffRoleAddressErrMsg(permAddr,
						"lbl.sm.person.permAddr", sb);
			}
		}

		if (getManagedBean().getShareHolderMailAddr() != null
				&& getManagedBean().getShareHolderMailAddr().getSmAddress() != null) {
			SmAddress mailAddr = getManagedBean().getShareHolderMailAddr()
					.getSmAddress();
			if (isAddressReqdForRoleStaff(mailAddr)) {
				constructSaveStaffRoleAddressErrMsg(mailAddr,
						"lbl.sm.person.mailAddr", sb);
			}
		}

		if (sb.length() > 0) {
			throw new EpException(sb.toString());
		}
	}

	/**
	 * Construct validation message for saving address for role staff
	 * 
	 * @param smAddress
	 * @param addressTypeKey
	 * @param sb
	 */
	private void constructSaveStaffRoleAddressErrMsg(SmAddress smAddress,
			String addressTypeKey, StringBuffer sb) {

		if (StringUtils.isBlank(smAddress.getAddress1())) {
			sb.append(
					getAddressFieldRequiredMsg(addressTypeKey, "lbl.sm.address"))
					.append("<br />");
		}
		if (smAddress.getPmState() == null
				|| smAddress.getPmState().getStateId() == null) {
			sb.append(
					getAddressFieldRequiredMsg(addressTypeKey, "lbl.sm.state"))
					.append("<br />");
		}
		if (smAddress.getPmDistrict() == null
				|| smAddress.getPmDistrict().getDistrictId() == null) {
			sb.append(
					getAddressFieldRequiredMsg(addressTypeKey,
							"lbl.sm.district")).append("<br />");
		}
		if (smAddress.getPmCity() == null
				|| smAddress.getPmCity().getCityId() == null) {
			sb.append(
					getAddressFieldRequiredMsg(addressTypeKey,
							"lbl.sm.towncity")).append("<br />");
		}
		if (StringUtils.isBlank(smAddress.getPostcode())) {
			sb.append(
					getAddressFieldRequiredMsg(addressTypeKey,
							"lbl.sm.postcode")).append("<br />");
		}
	}

	/**
	 * Check whether address need to be saved
	 * 
	 * @param smAddress
	 * @return
	 */
	private boolean isNoNeedToSaveAddress(SmAddress smAddress) {
		boolean retVal = false;
		if (StringUtils.isBlank(smAddress.getAddress1())
				&& (smAddress.getPmState() == null || smAddress.getPmState()
						.getStateId() == null)
				&& (smAddress.getPmDistrict() == null || smAddress
						.getPmDistrict().getDistrictId() == null)
				&& (smAddress.getPmCity() == null || smAddress.getPmCity()
						.getCityId() == null)
				&& StringUtils.isBlank(smAddress.getPostcode())) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Add new address for personnel
	 * 
	 * @param SmPersonnel
	 *            shareHolderBean
	 * @param SmPersonnelAddress
	 *            smPersonnelAddress
	 */
	private void processAddNewAddress(SmPersonnel shareHolderBean,
			SmPersonnelAddress smPersonnelAddress) {
		if (!isNoNeedToSaveAddress(smPersonnelAddress.getSmAddress())) {
			shareHolderBean.getSmPersonnelAddresses().add(smPersonnelAddress);
		}
	}

	/**
	 * Update edited address or remove if is not needed for personnel
	 * 
	 * @param SmPersonnel
	 *            shareHolderBean
	 * @param SmPersonnelAddress
	 *            addr
	 */
	private void processUpdatedAddress(SmPersonnel shareHolderBean,
			SmPersonnelAddress addr) {
		if (!shareHolderBean.getSmPersonnelAddresses().contains(addr)
				&& !isNoNeedToSaveAddress(addr.getSmAddress())) {
			shareHolderBean.getSmPersonnelAddresses().add(addr);
		} else {
			if (shareHolderBean.getSmPersonnelAddresses().contains(addr)
					&& isNoNeedToSaveAddress(addr.getSmAddress())) {
				shareHolderBean.getSmPersonnelAddresses().remove(addr);
			}
		}
	}

	/**
	 * Update personnel address list and if not exist add to the list, if not
	 * needed remove from list
	 * 
	 * @param SmPersonnelAddress
	 *            addr
	 * @param SmPersonnel
	 *            shareHolderBean
	 */
	private void processUpdatedAddressList(SmPersonnelAddress addr,
			SmPersonnel shareHolderBean) {
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				getManagedBean().setShareHolderMailAddr(addr);
			}
		}
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				getManagedBean().setShareHolderOffAddr(addr);
			}
		}
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				getManagedBean().setShareHolderResdAddr(addr);
			}
		}
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)
				&& !isNoNeedToSaveAddress(addr.getSmAddress())) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				getManagedBean().setShareHolderPermAddr(addr);
			}
		}
	}

	/**
	 * Get address required msg by address type and address field
	 * 
	 * @param String
	 *            addrTypeKey
	 * @param String
	 *            addrFieldKey
	 * @return String
	 */
	private String getAddressFieldRequiredMsg(String addrTypeKey,
			String addrFieldKey) {
		return SmCommonUtil
				.getMessage(
						"lbl.sm.required.msg",
						new String[] { SmCommonUtil.getMessage(addrTypeKey,
								null)
								+ " - "
								+ SmCommonUtil.getMessage(addrFieldKey, null) });
	}

	/**
	 * Get head quarters facility and set to disabled from ticked
	 * 
	 * @return SmFacility
	 */
	private SmFacility getHqFacility() {
		SmAddress hqAddress = getHqAddress();
		SmFacility hqFacility = null;
		if (hqAddress != null
				&& getManagedBean().getSmAppl().getSmFacilities() != null
				&& !getManagedBean().getSmAppl().getSmFacilities().isEmpty()) {
			for (SmFacility fac : getManagedBean().getSmAppl()
					.getSmFacilities()) {
				if (fac.getSmAddress() != null
						&& fac.getSmAddress().getAddressId()
								.compareTo(hqAddress.getAddressId()) == 0) {
					fac.setBooleanValue(true);
					hqFacility = fac;
					break;
				}
			}
		}
		return hqFacility;
	}

	/**
	 * Get head quarters address
	 * 
	 * @return SmAddress
	 */
	private SmAddress getHqAddress() {
		SmAddress hqAddress = null;
		if (getManagedBean().getSmAppl().getSmCompanyBasic() != null
				&& getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses() != null
				&& !getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().isEmpty()) {
			hqAddress = getManagedBean().getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress();
		}
		return hqAddress;
	}

	/**
	 * Set hq facility in selected facility
	 */
	private void setHqFacilityInSelectedFacility() {
		SmFacility hq = getHq();
		if (!isFacilityExistInList(hq, getManagedBean()
				.getSelectedFactoryFacilities())) {
			if (getManagedBean().getSelectedFactoryFacilities() == null
					|| getManagedBean().getSelectedFactoryFacilities()
							.isEmpty()) {
				getManagedBean().setSelectedFactoryFacilities(
						new ArrayList<SmFacility>());

			}
			getManagedBean().getSelectedFactoryFacilities().add(hq);
		}
	}

	/**
	 * Set selected categories for site visit based on selected facility
	 */
	private void setSelectedSpecialCategoriesForSiteVisit() {
		getManagedBean().setSelectedCategories(
				new ArrayList<SmSupplierCategory>());
		for (SmFacility fac : getManagedBean().getSelectedFactoryFacilities()) {
			if (SmCommonUtil.isCollectionEmpty(SmApplicationUtil
					.getCategoryByFacType(fac.getFacilityType(),
							getManagedBean().getSelectedCategories()))) {
				getManagedBean().getSelectedCategories().addAll(
						SmApplicationUtil.getCategoryByFacType(fac
								.getFacilityType(), getManagedBean()
								.getSplCategoryList()));
			}
		}
	}

	/**
	 * Check if selected facility exist in selected facility list
	 * 
	 * @param SmFacility
	 *            facParam
	 * @param List
	 *            <SmFacility> facParamList
	 * @return boolean
	 */
	private boolean isFacilityExistInList(SmFacility facParam,
			List<SmFacility> facParamList) {
		boolean retVal = false;
		if (facParamList != null && !facParamList.isEmpty()) {
			for (SmFacility facTmp : facParamList) {
				if (facParam.getFacilityId() != null
						&& facTmp.getFacilityId() != null
						&& facParam.getFacilityId().compareTo(
								facTmp.getFacilityId()) == 0) {
					retVal = true;
				}
			}
		}
		return retVal;
	}

	/**
	 * Initialized latest gst information for HQ and branch
	 */
	private void initGst() {
		// init GST information
		getManagedBean().setCompanyBasicGst(
				SmApplicationUtil.initHqGstInformation(getManagedBean()
						.getSmAppl(), getManagedBean().getCompanyBasicGst()));
		SmApplicationUtil.initBranchGstInformation(getManagedBean()
				.getCompanyBasicGst(), getManagedBean().getSmAppl());
		if (getManagedBean().getSelectedBranch() != null) {
			getManagedBean().getSelectedBranch().setGst(null);
		}
	}

	/**
	 * Set softcert rejection reason for each personnels
	 */
	private void setPersonnelSoftcertRejectReason() {
		if (getManagedBean().getSmAppl() != null
				&& getManagedBean().getSmAppl().getSmSupplier() != null
				&& getManagedBean().getSmAppl().getSmPersonnels() != null
				&& !getManagedBean().getSmAppl().getSmPersonnels().isEmpty()) {
			for (SmPersonnel personnel : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				SmCommonUtil.setRejectedPersonnelSoftcert(personnel,
						getManagedBean().getSmAppl().getSmSupplier()
								.getSupplierId());
			}
		}
	}

	private void setSupplierCategoryEffectiveDate(SmSupplierCategory cat,
			String applType) {
		Calendar c = Calendar.getInstance();
		if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
				&& cat.getCategoryEffDate() == null) {
			if (StringUtils.isNotBlank(applType)
					&& !applType.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
					&& !applType.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
					&& !applType.equals(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)
					&& !applType
							.equals(ParameterConst.NEW_SM_APPLN_FOR_DELETE_PENDING_APPL)) {
				c.add(Calendar.MONTH, SmServiceLookup.getSmRuleSO()
						.retrieveNewCatEffectiveMonthAfterAppoved()
						.getNewCatEffectiveMonth());
			}
			cat.setCategoryEffDate(c.getTime());
		}
	}

}
