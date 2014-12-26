package my.ep.web.backingbean.sm;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.attachment.ISmAttachmentSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.reviewApplication.IReviewApplicationSO;
import my.ep.biz.service.sm.workFlowStatus.ISmWorkFlowStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.attachment.SmAttachmentReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmRace;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
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
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.questionnaire.QuestionnaireBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.SmAuthorizedPersonnelCountVO;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;

@ManagedBean(name = "backingBean")
@RequestScoped
public class SmApplicationBackingBean extends
		BaseBackingBean<SmApplicationManagedBean> {

	public static Logger log = Logger.getLogger(SmApplicationBackingBean.class);

	private int currentTab;
	private int outputTab;
	private StreamedContent file;
	NumberFormat formatter = new DecimalFormat("#0.00");

	public SmApplicationBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

	public void renewSesionObjectByApplicationType(String applicationType) {
		renewSesionObject();
		log.info("*** renewSesionObjectByApplicationType *** "
				+ applicationType);
		SmApplicationUtil.setNavigationForApplTnCPage(applicationType);
	}

	public void prerenderResubmissionQuestionaire() {
		log.info("**** Prerender Questions *************");

		HttpServletRequest originalServletRequest = new FacesScopes()
				.getOriginalServletRequest();
		String applId = originalServletRequest.getParameter("apId");

		log.info("application id : " + applId);
		SmAppl appl = null;
		SmSupplier supplierProfile = null;
		String xhtml = "/xhtml/sm/resubmitApplication/prerenderResubmitApplication.xhtml";
		try {
			if (applId != null) {
				MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
				mofResDto = SmApplicationUtil.retriveApplicationById(Integer
						.parseInt(applId));
				appl = mofResDto.getSmAppl();
				supplierProfile = mofResDto.getSmSupplier();
			}
		} finally {
		}
		this.getManagedBean().setValidResubmit(true);
		if (appl != null && supplierProfile != null) {
			boolean activeExist = false;
			activeExist = SmApplicationUtil
					.checkActiveSmApplForSupp(supplierProfile);
			SmWorkflowStatus sts = getCurrentActiveWokFlowStatus(appl
					.getApplId());
			if (sts != null && sts.getPmStatus() != null
					&& sts.getPmStatus().getStatusId() != null) {
				if (sts.getPmStatus()
						.getStatusId()
						.compareTo(
								SupplierManagementConst.PM_STATUS_MOF_ACCT_MAINTAIN_APPROVED) == 0
						|| sts.getPmStatus()
								.getStatusId()
								.compareTo(
										SupplierManagementConst.PM_STATUS_MOF_ACCT_REG_APPROVED) == 0) {
					getManagedBean().setResubmitInValidMsg(
							SmCommonUtil.getMessage(
									"lbl.sm.approvedApplicationCantProceed",
									null));
					this.getManagedBean().setValidResubmit(false);
				} else if (sts
						.getPmStatus()
						.getStatusId()
						.compareTo(
								SupplierManagementConst.PM_STATUS_PENDING_PAYMENT) == 0) {
					getManagedBean()
							.setResubmitInValidMsg(
									SmCommonUtil
											.getMessage(
													"lbl.sm.approvedApplicationPendingFinalPay",
													null));
					this.getManagedBean().setValidResubmit(false);
				}

			}
			if (this.getManagedBean().isValidResubmit()) {
				if (!activeExist) {
					FacesContext fc = FacesContext.getCurrentInstance();
					if (!appl.getApplType().equalsIgnoreCase(
							ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
						QuestionnaireBackingBean quesBackingBean = new QuestionnaireBackingBean();
						quesBackingBean.getManagedBean().setTargetUrl(xhtml);
						quesBackingBean.getManagedBean().setSmSupp(
								supplierProfile);
						quesBackingBean.getManagedBean().setSmAppl(appl);
						quesBackingBean.retrieveQuestionnaire();
						fc.getApplication()
								.getNavigationHandler()
								.handleNavigation(fc, null,
										"/xhtml/sm/questionnaire/questionnaireView.xhtml");
						getManagedBean().setSmAppl(appl);
						getManagedBean().setSmSupplier(supplierProfile);
					} else {
						getManagedBean().setSmAppl(appl);
						getManagedBean().setSmSupplier(supplierProfile);
						prerenderApplicationForResubmission();
					}
				} else {
					getManagedBean().setResubmitInValidMsg(
							SmCommonUtil.getMessage("lbl.sm.activeDrftExist",
									null));
					this.getManagedBean().setValidResubmit(false);

				}
			}
		} else {
			getManagedBean().setResubmitInValidMsg(
					SmCommonUtil
							.getMessage("lbl.sm.resubmission.invalid", null));
			// redirect & display resubmit expired
			this.getManagedBean().setValidResubmit(false);
		}

	}

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
			c.add(Calendar.DATE, 60);
			Date d = c.getTime();
			getManagedBean().getSmAppl().setDraftExpiryDate(d);
			String newApplNum = SmApplicationUtil.generateApplicationNum(
					getManagedBean().getSmAppl().getApplType(),
					getManagedBean().getSmAppl().getSupplierType());
			if (getManagedBean().getSmAppl().getSmApplSectionReviews() != null
					&& !getManagedBean().getSmAppl().getSmApplSectionReviews()
							.isEmpty()) {
				getManagedBean().getSmAppl().getSmApplSectionReviews().clear();

			}
			if (newApplNum != null) {
				getManagedBean().getSmAppl().setApplNo(newApplNum);
			}

			setNavigationForResubmitAppln(appl.getApplType());
		} else {
			// redirect & display resubmit expired
			this.getManagedBean().setValidResubmit(false);
		}

	}

	public void processDraftApplication(String applicationType,
			boolean fromBpm, SmAppl appl, SmSupplier supp) {
		proceedToApplication(applicationType, fromBpm, appl, supp);
		// updateSmAppl(applicationType, "");

	}

	public String proceedToApplication(String applicationType) {
		return proceedToApplication(applicationType, Boolean.FALSE, null, null);
	}

	public String getMaxDate() {
		DateFormat df = new SimpleDateFormat();
		log.info("*** Date String *** " + df.format(new Date()));
		return df.format(new Date());
	}

	public String proceedToApplication(String applicationType, boolean fromBpm,
			SmAppl appl, SmSupplier supp) {
		getManagedBean().setRedirectToPersonPage(false);
		assignValReqdToFalse();
		if (!applicationType
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			log.info("*** Today ****" + getManagedBean().getToday());
			getManagedBean().setCountryLs(
					SmApplicationUtil.retrievePmCountryList());
			// getManagedBean().setCertBodyLs(
			// SmApplicationUtil.retrievePmCertBodyList());
			getManagedBean().setProfBodyLs(
					SmApplicationUtil.retrievePmProfBodyList());

			getManagedBean().setUomLs(SmApplicationUtil.retrieveUomList());
			getManagedBean().setReasonDescList(
					SmApplicationUtil.retrieveRejectReasonList());
			getManagedBean().setRegStatusDescList(
					SmApplicationUtil.retrieveRegistrationStatusList());
			getManagedBean().setEduFieldList(
					SmApplicationUtil.retrieveEduFieldList());
			getManagedBean().setRacesList(
					SmApplicationUtil.retrievePmRaceList());

		}
		getManagedBean()
				.setDefPmCountry(
						SmCommonUtil
								.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE));
		SmAppl smAppl = new SmAppl();
		SmSupplier smApplSupplier = new SmSupplier();
		try {
			// MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
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
					// cannotProceedAlertMsg = SmApplicationUtil
					// .checkPendingActivationPersonnelList(currSupp
					// .getSupplierId());
				}
				cannotProceedAlertMsg = SmApplicationUtil
						.checkActiveSmApplForSupp(currSupp);
				if (cannotProceedAlertMsg) {
					throw new EpException(MessageConst.ERROR_722542);
				}

				if (applicationType
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
					smAppl.setSupplierType(getManagedBean()
							.getNewSupplierType());
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
						// smAppl.setSmSupplierBanks(new
						// ArrayList<SmSupplierBank>());
						if (smAppl.getSmCompanyBasic() != null) {
							smAppl.getSmCompanyBasic().setAnnualRevenue(0);
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
			if (!applicationType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
					&& !applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				SmApplicationUtil
						.checkEquityOwnerUpdateAllowance(getManagedBean());
			}
			retrieveBusinessTypeList();
			assignSupplierBusinessType(getManagedBean().getSmSupplier());

			if (getManagedBean().isApproverUser()
					|| getManagedBean().isPoUser()) {
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
					this.getManagedBean().setBasicCompanyStates(
							retrievePmStateList(addr.getPmCountry()
									.getCountryId()));
					this.getManagedBean().setBasicCompanyDistricts(
							retrievePmDistrictList(addr.getPmState()
									.getStateId()));
					this.getManagedBean().setBasicCompanyCities(
							retrievePmCityList(addr.getPmDistrict()
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

			if (fromBpm
					&& !(getManagedBean().isApproverUser() || getManagedBean()
							.isPoUser())) {
				boolean updateNeeded = resetDeactivatedPersonnel();
				if (updateNeeded)
					saveOrUpdateAppl();
			}

			if (getManagedBean().getSmAppl().getSmStaffTotals().isEmpty()) {
				log.info("*** Empty list of staff *****"
						+ smAppl.getSupplierType());
				getManagedBean().getSmAppl().setSmStaffTotals(
						SmApplicationUtil.getStaffList(smAppl));
			}
			// getManagedBean().setSmAppl(smAppl);

			getManagedBean().setTabActiveIndex(0);
			if (!fromBpm) {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_DECLARATION);
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setWithFederal(true);
				// }
			} else {
				SmWorkflowStatus sts = getCurrentActiveWokFlowStatus(smAppl
						.getApplId());
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
					setSupplierCancellationRemarks(applicationType,
							getManagedBean().getSmAppl().getApplId());
				}

				// currently is display for ApplType C/DPA
				getManagedBean().setBasicInfo(
						SmApplicationUtil
								.getSmSupplierBasicInfoVO(getManagedBean()));
			}

			// calcPersonnelListCount();
			// calculateCapital();
			// calculateBumiPercentageTotals();
			if (getManagedBean().getSmAppl().getSupplierType()
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				getManagedBean().getSmAppl().setApplCategoryType("N");
			}
			updateSmAppl(applicationType, "", "");
			getManagedBean().setCurrentPageType(
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC);
			if (smAppl.getSmFacilities() != null
					&& smAppl.getSmFacilities().isEmpty()
					&& !smAppl.getApplType().equalsIgnoreCase(
							ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
				log.info("*** Empty Facility******");
				SmFacility fac = new SmFacility();
				if (smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
					// fac.setOwnerName("Sample");
					fac.setFacilityType("H");
				}
				SmAddress addr = new SmAddress();
				addr = setDefaultBaseOfficeAddress(addr);
				fac.setFacilityName(getManagedBean().getSmAppl()
						.getSmCompanyBasic().getCompanyName());
				fac.setSmAppl(smAppl);
				fac.setSmAddress(addr);
				fac.setPurchasePrice(new BigDecimal(0.00));
				fac.setRentalRate(new BigDecimal(0.00));
				fac.setRevNo(1);
				smAppl.getSmFacilities().add(fac);
				this.getManagedBean().setBasicCompanyFacility(fac);
				getManagedBean()
						.setBasicOffFacStates(
								retrievePmStateList(addr.getPmCountry()
										.getCountryId()));

			} else {
				setApplicationFailities(smAppl.getSmFacilities());
			}
			if (smAppl.getApplType().equalsIgnoreCase(
					ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				SmApplicationUtil.setCancellationSmAttachment(getManagedBean());
				// set basic info for cancel mof
			}
			getManagedBean()
					.setSupplierParentCatList(
							SmApplicationUtil
									.retrieveParentCategoryListForExperiencePage(getManagedBean()));
			if (getManagedBean().getSmAppl().getSmAssociationBodies() != null) {
				SmApplicationUtil
						.assignNonPersonnelProfBodyList(getManagedBean());
			}

			return SmApplicationUtil.setNavigationForAppl(applicationType);
		} finally {
			// mofRegistrationSO = null;
		}
	}

	private boolean resetDeactivatedPersonnel() {
		boolean updateNecessary = false;
		if (getManagedBean().getSmAppl().getSmPersonnels() != null
				&& getManagedBean().getSmAppl().getSmPersonnels().size() > 0) {
			for (SmPersonnel personnel : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (personnel.getRecordStatusFlg().equals(
						SupplierManagementConst.DELETED_RECORD_STATUS)) {
					personnel
							.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
					personnel.setEpRole(null);
					personnel.setAuthorizedFlg(0);
					updateNecessary = true;
				}
			}
		}
		return updateNecessary;
	}

	private SmAddress setDefaultBaseOfficeAddress(SmAddress addr) {
		if (getManagedBean().getSmAppl().getSmCompanyBasic()
				.getSmCompanyAddresses() != null
				&& !getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().isEmpty()) {
			SmAddress baseAddr = getManagedBean().getSmAppl()
					.getSmCompanyBasic().getSmCompanyAddresses().get(0)
					.getSmAddress();
			if (baseAddr != null) {
				addr = SmApplicationUtil.setDefaultBaseOfficeAddr(baseAddr,
						addr);
			} else {
				addr = SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry());
			}
		} else {
			addr = SmApplicationUtil.setDefaultAddr(getManagedBean()
					.getDefPmCountry());
		}
		return addr;
	}

	private void setSupplierCancellationRemarks(String docType, Long docId) {
		SmRemaksResDTO resDTO = getRemarksByDoc(docType, docId);
		StringBuffer sb = new StringBuffer();
		if (resDTO != null && resDTO.getRemarksList() != null
				&& !resDTO.getRemarksList().isEmpty()) {

			sb.append(resDTO.getRemarksList().get(0).getRemark());
			// for (SmRemark rem : resDTO.getRemarksList()) {
			// sb.append(rem.getRemark()).append("\n");
			// }
		}
		getManagedBean().setSupplierRemarks(sb.toString());

	}

	private SmRemaksResDTO getRemarksByDoc(String docType, Long docId) {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setDocId(docId);
		reqDTO.setDocType(docType);
		ISmRemarksSO reqSO = (ISmRemarksSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISmRemarksSO.class);
		SmRemaksResDTO resDTO = reqSO.retrieveSmRemarksByDocument(reqDTO);
		return resDTO;
	}

	private SmWorkflowStatus getCurrentActiveWokFlowStatus(Long applId) {
		log.info("*** This is resubmit to get po ****** ");
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		reqDTO.setApplId(applId);
		ISmWorkFlowStatusSO reqSO = (ISmWorkFlowStatusSO) BusinessDelegate
				.getInstance()
				.getServiceObject("sm", ISmWorkFlowStatusSO.class);
		SmWorkFlowStatusResDTO resDTO = reqSO
				.retrieveCurrentWorkFlowStatusByDocId(reqDTO);
		if (resDTO != null && resDTO.getWorkflowStatus() != null) {
			return resDTO.getWorkflowStatus();
		} else {
			return null;
		}
	}

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
				SmWorkflowStatus sts = getCurrentActiveWokFlowStatus(getManagedBean()
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

			return SupplierManagementConst.PROCEED_DEL_PENDING_MOF_APPLN;

		}
		// catch (Exception e) {
		// e.printStackTrace();
		// throw new EpException(MessageConst.ERROR_722505);
		// // return SupplierManagementConst.DEFAULT_DEL_PENDING_MOF_APPLN;
		// }
		finally {
			mofRegistrationSO = null;
		}
	}

	public void onDisplayConfirm() {
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
			RequestContext.getCurrentInstance().execute("canRegConfirm.show()");
		}
	}

	public void onDisplayConfirmDelPending() {
		RequestContext.getCurrentInstance().execute("delPenRegConfirm.show()");
	}

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
			// return SupplierManagementConst.PROCEED_MOF_REG;

		} catch (Exception e) {
			getManagedBean().setTabActiveIndex(currentTab);
			throw new EpException(MessageConst.ERROR_722505);
		}

	}

	public void updateSmApplBpmSubmit(String applType, String currentPageType,
			String outputPageType, boolean isBpmSubmit) {
		validateUpdateSmAppl(applType, currentPageType, outputPageType,
				isBpmSubmit);
	}

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
				&& getManagedBean().isSupplierUser()
				&& getManagedBean().getSmAppl().getSupportingDocMode() != null
				&& getManagedBean().getSmAppl().getSupportingDocMode()
						.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			validateSupportingDocs();
		}
		validateUpdateSmAppl(applType, currentPageType, outputPageType, false);
		log.info("******* Current page AFTER in managed bean ********"
				+ getManagedBean().getCurrentPageType());
	}

	public boolean validateUpdateSmAppl(String applType,
			String currentPageType, String outputPageType, Boolean isBpmSubmit) {
		boolean valid = false;
		// boolean isError = false;
		IMofRegistrationSO mofRegistrationSO = null;
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

			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			valid = validateBeforeSave(applType, currentPageType);
			// valid = true;
			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				log.info(" ****** REMAIN PREV VALUE ********");
			}
			if (valid) {
				// Save or update to DB
				MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
				reqDto.setSmAppl(this.getManagedBean().getSmAppl());
				mofRegistrationSO.updateApplication(reqDto);
				getManagedBean().setCurrentPageType(outputPageType);

			} else {
				getManagedBean().setTabActiveIndex(currentTab);
				getManagedBean().getSmAppl().setCurrentActiveTab(currentTab);
				getManagedBean().setCurrentPageType(currentPageType);
			}
		}

		finally {
			mofRegistrationSO = null;
			if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)
					&& !valid) {
				saveOrUpdateAppl();
			} else if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)
					&& !valid) {
				saveOrUpdateAppl();
				calculateCapital();
			} else {
				valid = retriveLatestApplAfterUpdate(currentPageType,
						outputPageType);
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
					// calcPersonnelListCount();
					displayBumiRecommendNote();
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL:
					calcPersonnelListCount();
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY:
					SmApplicationUtil
							.assignActiveCategoryList(getManagedBean());
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
						break;
					}
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_PODEC:
					SmApplicationUtil.checkAllowedToApprove(getManagedBean());
					// calcPersonnelListCount();
					displayBumiRecommendNote();
					break;
				case SupplierManagementConst.SM_APPL_PAGE_TYPE_APPRDEC:
					SmApplicationUtil.checkAllowedToApprove(getManagedBean());
					// calcPersonnelListCount();
					displayBumiRecommendNote();
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
				default:
					break;
				}

				// to init page whether normal or special category type is
				// allowed
				initApplCategoryType();

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
			// if (getManagedBean().getSmAppl().getPmStatus() != null) {
			// log.info("***** After final Retrieve ****"
			// + getManagedBean().getSmAppl().getPmStatus()
			// .getStatusId());
			// }
			return true;
		}
		// catch (Exception e) {
		// getManagedBean().setCurrentPageType(currentPageType);
		// e.printStackTrace();
		// throw new EpException(MessageConst.ERROR_722519);
		// }
		finally {
			mofRegistrationSO = null;
		}

	}

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

		// for (SmApplRejectReason res : getManagedBean().getSmAppl()
		// .getSmApplRejectReasons()) {
		// log.info("Reason Id === "
		// + res.getPmReason().getReasonId());
		// PmReasonDesc desc = retrieveReasonDescriptionByReasonId(res
		// .getPmReason().getReasonId());
		// if (desc != null) {
		// sb.append(++count).append(".)");
		// sb.append(desc.getReasonDesc());
		// sb.append("\n");
		// }
		// }
		getManagedBean().setReasonDesc(sb.toString());
	}

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
					getManagedBean(), currentTab);
			validSave = SmApplicationUtil.validateEquityOwnerAmount(
					getManagedBean(), currentTab);
			validSave = SmApplicationUtil.validateEquityCompanyAmount(
					getManagedBean(), currentTab);
			validatePaidCaptialAmt(true);
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL:
			validSave = SmApplicationUtil.validateEquityOwnerCount(
					getManagedBean(), currentTab);
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
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS:
			if (getManagedBean().isPoUser()
					|| getManagedBean().isApproverUser()) {
				validSave = SmApplicationUtil.checkSupportDocsValidity(
						validSave, getManagedBean(), currentTab);
			}

		default:
			log.info("****** Not a defined page ***********");

		}
		return validSave;
	}

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
				catL3Ids.add(cat.getPmCategoryL3().getCategoryL3Id());
			}
			rqDto.setCategoryL3Ids(catL3Ids);
			log.info(" **** Total categories sent to Doc So ***"
					+ rqDto.getCategoryL3Ids().size());
		}

		rqDto.setSupplierTypeCode(getManagedBean().getSmAppl()
				.getSupplierType());
		rqDto.setCompanyTypeCode(getManagedBean().getSmAppl().getSmSupplier()
				.getBusinessType());
		DocumentResDTO rsDTO = null;
		if (rqDto.getCategoryL3Ids() != null
				&& !rqDto.getCategoryL3Ids().isEmpty()) {
			rsDTO = retrieveSupportingDocumentList(rqDto);
		} else {
			log.info(" **** Not calling supporting doc API as there are not categories for this supplier **** ");
		}

		if (rsDTO != null && rsDTO.getDocumentList() != null
				&& !rsDTO.getDocumentList().isEmpty()) {
			log.info("*** Not Empty list ****" + rsDTO.getDocumentList().size());
			List<SmApplSupportDoc> docs = new ArrayList<>();

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
				docs.addAll(getManagedBean().getSmAppl().getSmApplSupportDocs());
				for (PmDocument doc : rsDTO.getDocumentList()) {
					boolean recordExists = false;
					for (SmApplSupportDoc smDocu : getManagedBean().getSmAppl()
							.getSmApplSupportDocs()) {

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
			}

			getManagedBean().getSmAppl().setSmApplSupportDocs(docs);
		} else {
			log.info("*** Empty Supporting doc list **** ");
		}
		log.info(" ** For Suporting Docs END ** ");
	}

	public void setTabValue(int currentTab, int outputTab) {
		this.currentTab = currentTab;
		this.outputTab = outputTab;

		// getManagedBean().setTabActiveIndex(outputTab);
	}

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

	public void officeFacilitySelected(AjaxBehaviorEvent e) {
		if (getManagedBean().getBasicCompanyFacility().getOwnershipType()
				.equalsIgnoreCase("O")) {
			getManagedBean().getBasicCompanyFacility().setSmAddress(
					setDefaultBaseOfficeAddress(getManagedBean()
							.getBasicCompanyFacility().getSmAddress()));
		} else {
			SmAddress addr = SmApplicationUtil.setDefaultAddr(getManagedBean()
					.getDefPmCountry());
			if (getManagedBean().getBasicCompanyFacility().getSmAddress() == null) {
				getManagedBean().getBasicCompanyFacility().setSmAddress(addr);
			} else if (getManagedBean().getBasicCompanyFacility()
					.getSmAddress() != null
					&& getManagedBean().getBasicCompanyFacility()
							.getSmAddress().getAddressId() != null) {
				getManagedBean().getBasicCompanyFacility().setSmAddress(
						SmApplicationUtil.setDefaultAddrWithSameId(
								getManagedBean().getDefPmCountry(),
								getManagedBean().getBasicCompanyFacility()
										.getSmAddress()));
			}
			if (getManagedBean().getBasicCompanyFacility().getSmAddress()
					.getPmCountry() != null
					&& getManagedBean().getBasicCompanyFacility()
							.getSmAddress().getPmCountry().getCountryId() != null) {
				populateStatesByCountry(getManagedBean()
						.getBasicCompanyFacility().getSmAddress()
						.getPmCountry().getCountryId(),
						SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL,
						new PmState(), new PmDistrict(), new PmCity());
			} else {
				getManagedBean().getBasicCompanyFacility().setSmAddress(addr);
				populateStatesByCountry(getManagedBean()
						.getBasicCompanyFacility().getSmAddress()
						.getPmCountry().getCountryId(),
						SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL,
						new PmState(), new PmDistrict(), new PmCity());
			}
			if (getManagedBean().getBasicCompanyFacility().getSmAddress()
					.getPmState() != null
					&& getManagedBean().getBasicCompanyFacility()
							.getSmAddress().getPmState().getStateId() != null) {
				populateDistrictsByState(getManagedBean()
						.getBasicCompanyFacility().getSmAddress().getPmState()
						.getStateId(),
						SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL,
						new PmDistrict(), new PmCity());
			}
			if (getManagedBean().getBasicCompanyFacility().getSmAddress()
					.getPmDistrict() != null
					&& getManagedBean().getBasicCompanyFacility()
							.getSmAddress().getPmDistrict().getDistrictId() != null) {
				populateCitiesByDistrict(getManagedBean()
						.getBasicCompanyFacility().getSmAddress()
						.getPmDistrict().getDistrictId(),
						SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL,
						new PmCity());
			}
		}
		setFacilityDefaultPrices(getManagedBean().getBasicCompanyFacility());
		initBusinessPremise();
	}

	private void setFacilityDefaultPrices(SmFacility fac) {
		if (fac.getRentalRate() == null) {
			fac.setRentalRate(new BigDecimal(0.00));
		}
		if (fac.getPurchasePrice() == null) {
			fac.setPurchasePrice(new BigDecimal(0.00));
		}
	}

	private void setPreviousFacilityValues(SmFacility facilityBean) {
		getManagedBean().setRentPrevVal(facilityBean.getRentalRate());
		getManagedBean().setPurPricePrevVal(facilityBean.getPurchasePrice());
		getManagedBean().setOwnerTypePrevVal(facilityBean.getOwnershipType());
	}

	private void resetFacilityValues(SmFacility fac) {
		if (fac.getFacilityId() == null) {
			fac.getSmAddress().setPostcode("");
			fac.setOwnershipType("");
		} else {
			fac.setRentalRate(getManagedBean().getRentPrevVal());
			fac.setPurchasePrice(getManagedBean().getPurPricePrevVal());
			fac.setOwnershipType(getManagedBean().getOwnerTypePrevVal());
		}

	}

	public void assignShareHolderPrivilege(AjaxBehaviorEvent e) {
		log.info("**** Enter Share Holder privilege ******");
		if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() == 0) {
			getManagedBean().getSelectedShareHolder().setSoftcertFlg(0);
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(0);
			if (getManagedBean().getSelectedShareHolder().getEpRole() != null
					&& getManagedBean()
							.getSelectedShareHolder()
							.getEpRole()
							.equalsIgnoreCase(
									RoleCode.MOF_SUPPLIER_USER.value())) {
				getManagedBean().getSelectedShareHolder().setEpRole("");
			}
			getManagedBean().getShareHolderMailAddr().setSameAsPerm(true);
			getManagedBean().setRemoveEpRole(true);
		} else {
			getManagedBean().getShareHolderMailAddr().setSameAsPerm(false);
			assignSelShareHolderAddress(getManagedBean()
					.getSelectedShareHolder());
			getManagedBean().setRemoveEpRole(false);
		}
		buildRoleList();
		if (!getManagedBean().isRemoveEpRole()
				&& (getManagedBean().getSelectedShareHolder().getEpRole() != null && getManagedBean()
						.getSelectedShareHolder().getEpRole()
						.equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value()))) {
			SelectItem role = new SelectItem();
			role.setLabel("Supplier Admin");
			role.setValue(RoleCode.MOF_SUPPLIER_ADMIN.value());
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
		if (getManagedBean().getSelectedShareHolder().getSoftcertFlg() == 0) {
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(0);
		}
	}

	public void assignShareHolderPrivilegeByEpRole(AjaxBehaviorEvent e) {
		if (!getManagedBean().getSelectedShareHolder().getEpRole()
				.equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value())) {
			getManagedBean().getSelectedShareHolder().setSoftcertFlg(0);
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(0);
		} else if (getManagedBean().getSelectedShareHolder().getSoftcertFlg() != 1) {
			getManagedBean().getSelectedShareHolder().setSoftcertFlg(2);
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(0);
		}

	}

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

	public void applicationCategorySelected(AjaxBehaviorEvent e) {
		setApplicationCategorySelected();
	}

	private void setApplicationCategorySelected() {
		getManagedBean().getSmAppl().setCurrentActiveTab(
				getManagedBean().getTabActiveIndex());
		if (getManagedBean().isPoUser() || getManagedBean().isApproverUser()) {
			assignMaxReviewCount(getManagedBean().getSmAppl().getApplType(),
					getManagedBean().getSmAppl().getApplCategoryType(),
					getManagedBean().getSmAppl().getSupplierType(),
					getManagedBean().getBusinessTypeVal());
		}

		// added to custom confirm dialog title and msg
		// based on category type Special or Normal
		setRegCatCodeConfirmDialog();
	}

	private void setRegCatCodeConfirmDialog() {
		getManagedBean().setRegCatCodeConfirmHeader(
				SmCommonUtil.getMessage(
						"lbl.sm.regCatCode.confirm.header.normal", null));
		getManagedBean().setRegCatCodeConfirmMsg(
				SmCommonUtil.getMessage("lbl.sm.regCatCode.confirm.msg.normal",
						null));
		if (getManagedBean().getSmAppl() != null) {
			if (StringUtils.isNotBlank(getManagedBean().getSmAppl()
					.getApplCategoryType())
					&& getManagedBean().getSmAppl().getApplCategoryType()
							.equals("S")) {
				getManagedBean().setRegCatCodeConfirmHeader(
						SmCommonUtil.getMessage(
								"lbl.sm.regCatCode.confirm.header.special",
								null));
				getManagedBean().setRegCatCodeConfirmMsg(
						SmCommonUtil.getMessage(
								"lbl.sm.regCatCode.confirm.msg.special", null));
			}
		}
	}

	public void assignDocVerified(AjaxBehaviorEvent e) {
		log.info(" *******Docs verified******88");
	}

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

	// public void svcSelected(AjaxBehaviorEvent e) {
	// log.info(" ******* On Change Office Facility ***** "
	// + getManagedBean().getPoSiteVisitType());
	//
	// }

	// -- Process Branches Start
	public void addOrUpdateBranchToSmAppl() {
		if (getManagedBean().getSmAppl().getSmSupplierBranches()
				.contains(getManagedBean().getSelectedBranch())) {
			retriveLatestApplAfterUpdate("", "");
			for (int i = 0; i < getManagedBean().getSmAppl()
					.getSmSupplierBranches().size(); i++) {
				if (getManagedBean()
						.getSmAppl()
						.getSmSupplierBranches()
						.get(i)
						.getSupplierBranchId()
						.equals(getManagedBean().getSelectedBranch()
								.getSupplierBranchId())) {
					getManagedBean().getSmAppl().getSmSupplierBranches()
							.set(i, getManagedBean().getSelectedBranch());
				}
			}
			// for (SmPersonnel per : getManagedBean().getSmAppl()
			// .getSmPersonnels()) {
			// if (per.getPersonnelId().equals(
			// getManagedBean().getSelectedBranch().getSmPersonnel()
			// .getPersonnelId())) {
			// getManagedBean().getSelectedBranch().setSmPersonnel(per);
			// }
			// }
			// TODO if any validation reqd for branch info
		} else {
			getManagedBean().getSmAppl().getSmSupplierBranches()
					.add(getManagedBean().getSelectedBranch());
			RequestContext.getCurrentInstance().execute(
					"suppBrnachDtWidVar.paginator.endLink.click()");
		}
		saveOrUpdateAppl();
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("banchDialog.hide()");
		calcPersonnelListCount();
	}

	public void assignSelBranch(SmSupplierBranch branchBean, int index) {

		assignStateDistAndCityForSelVal(branchBean.getSmAddress()
				.getPmCountry().getCountryId(), branchBean.getSmAddress()
				.getPmState().getStateId(), branchBean.getSmAddress()
				.getPmDistrict().getDistrictId(),
				SupplierManagementConst.SM_BRANCH_PANEL);
		if (branchBean.getSmPersonnel() == null) {
			branchBean.setSmPersonnel(new SmPersonnel());
		}
		if (!branchBean.getBranchName().equalsIgnoreCase(
				(getManagedBean().getSmAppl().getSmCompanyBasic()
						.getCompanyName()))) {
			branchBean.setBranchName(getManagedBean().getSmAppl()
					.getSmCompanyBasic().getCompanyName());
		}
		getManagedBean()
				.setSelectedBranchPrev(
						getManagedBean().getSmAppl().getSmSupplierBranches()
								.get(index));
		getManagedBean().setSelectedBranchIndex(index);

		getManagedBean().setSelectedBranch(branchBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("banchDialog.show()");

	}

	public void assignNewBranchBean() {
		SmSupplierBranch br = new SmSupplierBranch();
		br.setBranchName(getManagedBean().getSmAppl().getSmCompanyBasic()
				.getCompanyName());
		br.setRevNo(1);
		br.setSmAddress(SmApplicationUtil.setDefaultAddr(getManagedBean()
				.getDefPmCountry()));
		br.setSmAppl(getManagedBean().getSmAppl());
		br.setSmPersonnel(new SmPersonnel());
		getManagedBean().setBranchCompanyStates(
				retrievePmStateList(getManagedBean().getSelectedBranch()
						.getSmAddress().getPmCountry().getCountryId()));
		getManagedBean().setBranchCompanyDistricts(new ArrayList<PmDistrict>());
		getManagedBean().setBranchCompanyCities(new ArrayList<PmCity>());
		getManagedBean().setSelectedBranch(br);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("banchDialog.show()");
	}

	public void assignCancelBranchBean() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:banchDialog");
		RequestContext.getCurrentInstance().execute("banchDialog.hide()");
	}

	public void deleteSelectedBranches() {
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

	}

	// -- Process Branches End

	// -- Process Associated Companies Start
	public void addOrUpdateAssocToSmAppl() {
		if (getManagedBean().getSmAppl().getSmAssociateCompanies()
				.contains(getManagedBean().getSelectedAssocCompany())) {
			// TODO if any validation reqd for assoc companies
		} else {
			getManagedBean().getSmAppl().getSmAssociateCompanies()
					.add(getManagedBean().getSelectedAssocCompany());
			RequestContext.getCurrentInstance().execute(
					"assocDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("assocDialog.hide()");
	}

	public void cancelAssocCompanyFuntion() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:asDisp");
		RequestContext.getCurrentInstance().execute("assocDialog.hide()");
	}

	public void assignSelAssoc(SmAssociateCompany assocBean) {
		assignStateDistAndCityForSelVal(assocBean.getSmAddress().getPmCountry()
				.getCountryId(), assocBean.getSmAddress().getPmState()
				.getStateId(), assocBean.getSmAddress().getPmDistrict()
				.getDistrictId(),
				SupplierManagementConst.SM_ASSOC_COMPANY_PANEL);
		getManagedBean().setSelectedAssocCompany(assocBean);
		getManagedBean().getSelectedAssocCompany().setIsShareholding(
				new BigDecimal(0));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("assocDialog.show()");
	}

	public void assignNewAssocBean() {
		getManagedBean().setSelectedAssocCompany(new SmAssociateCompany());
		getManagedBean().getSelectedAssocCompany().setRevNo(1);
		getManagedBean().getSelectedAssocCompany().setSmAddress(
				(SmApplicationUtil.setDefaultAddr(getManagedBean()
						.getDefPmCountry())));
		getManagedBean().getSelectedAssocCompany().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setAssocCompanyStates(
				retrievePmStateList(getManagedBean().getSelectedAssocCompany()
						.getSmAddress().getPmCountry().getCountryId()));
		getManagedBean().setAssocCompanyDistricts(new ArrayList<PmDistrict>());
		getManagedBean().setAssocCompanyCities(new ArrayList<PmCity>());
		getManagedBean().getSelectedAssocCompany().setIsShareholding(
				new BigDecimal(0));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("assocDialog.show()");
	}

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
		if (getManagedBean().getSmAppl().getSmSupplierCerts()
				.contains(getManagedBean().getSelectedCert())) {
			// TODO if any validations for edit reqd
		} else {
			getManagedBean().getSmAppl().getSmSupplierCerts()
					.add(getManagedBean().getSelectedCert());
			RequestContext.getCurrentInstance().execute(
					"suppCertDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("certDialog.hide()");
	}

	public void assignSelCert(SmSupplierCert certBean) {
		getManagedBean().setSelectedCert(certBean);
		assignCertBodyList();
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("certDialog.show()");
	}

	public void cancelSupplierCertAction() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:certPop");
		RequestContext.getCurrentInstance().execute("certDialog.hide()");
	}

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

	public void cancelSelProfBody() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:profPop");
		RequestContext.getCurrentInstance().execute("profDialog.hide()");
	}

	public void assignSelAssocBody(SmAssociationBody assocBean) {
		getManagedBean().setSelectedAssocBody(assocBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("profDialog.show()");
	}

	public void assignNewAssocBody() {
		SmAssociationBody assocBean = new SmAssociationBody();
		assocBean.setPmCertBody(new PmCertBody());
		assocBean.setRevNo(1);
		getManagedBean().setSelectedAssocBody(assocBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("profDialog.show()");
	}

	public void deleteSelectedAssocBodies() {

		log.info("*** Delete AssocBodies ******* "
				+ getManagedBean().getSelectedAssocBodyList().size());
		if (getManagedBean().getSelectedAssocBodyList().size() > 0) {
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
	public void addOrUpdateCompanyExperienceToSmAppl() {
		if (getManagedBean().getSelectedCompanyExperience()
				.getSmCompanyExperienceCats() == null
				|| (getManagedBean().getSelectedCompanyExperience()
						.getSmCompanyExperienceCats() != null && getManagedBean()
						.getSelectedCompanyExperience()
						.getSmCompanyExperienceCats().isEmpty())) {
			throw new EpException(MessageConst.ERROR_722550);
		}
		if (getManagedBean().getSelectedCompanyExperience()
				.getPaymentMethodId() == null) {
			getManagedBean().getSelectedCompanyExperience().setPaymentMethodId(
					new BigDecimal(0));
		}
		if (getManagedBean().getSmAppl().getSmCompanyExperiences()
				.contains(getManagedBean().getSelectedCompanyExperience())) {
			// TODO if any validation reqd for branch info
		} else {
			log.info(" **** THIS IS AN ADD FOR Consultant Prjs******");
			getManagedBean().getSmAppl().getSmCompanyExperiences()
					.add(getManagedBean().getSelectedCompanyExperience());
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
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {

			RequestContext.getCurrentInstance().execute("consDialog.hide()");
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			RequestContext.getCurrentInstance().execute("contDialog.hide()");
		}

	}

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

	public void assignSelCompanyExperience(SmCompanyExperience consultantBean) {
		getManagedBean().setSelectedCompanyExperience(consultantBean);
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
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			RequestContext.getCurrentInstance().execute("consDialog.show()");
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			RequestContext.getCurrentInstance().execute("contDialog.show()");
		}
	}

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
	public void addOrUpdateEquityCompanyToSmAppl() {

		if (getManagedBean().getSmAppl().getSmEquityCompanies()
				.contains(getManagedBean().getSelectedEquityCompany())) {
			// TODO if any validation reqd for edit
		} else {
			getManagedBean().getSmAppl().getSmEquityCompanies()
					.add(getManagedBean().getSelectedEquityCompany());
			RequestContext.getCurrentInstance().execute(
					"suppEquiCompDtWidVar.paginator.endLink.click()");
		}
		saveOrUpdateAppl();
		calculateCapital();
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("eqiCompDialog.hide()");
	}

	public void assignSelEquiCompany(SmEquityCompany equiCompBean) {
		if (equiCompBean.getShareAmt() == null) {
			equiCompBean.setShareAmt(new BigDecimal(0));
		}
		validatePaidCaptialAmt(false);
		populateStatesByCountry(equiCompBean.getSmAddress().getPmCountry()
				.getCountryId(), SupplierManagementConst.SM_EQUI_COMPANY_PANEL,
				new PmState(), new PmDistrict(), new PmCity());
		populateDistrictsByState(equiCompBean.getSmAddress().getPmState()
				.getStateId(), SupplierManagementConst.SM_EQUI_COMPANY_PANEL,
				new PmDistrict(), new PmCity());
		populateCitiesByDistrict(equiCompBean.getSmAddress().getPmDistrict()
				.getDistrictId(),
				SupplierManagementConst.SM_EQUI_COMPANY_PANEL, new PmCity());
		getManagedBean().setSelectedEquityCompany(equiCompBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("eqiCompDialog.show()");
	}

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
				retrievePmStateList(getManagedBean().getSelectedEquityCompany()
						.getSmAddress().getPmCountry().getCountryId()));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("eqiCompDialog.show()");
	}

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

	public void cancelEquiCompFuntionc() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:eqiCompDialog");
		RequestContext.getCurrentInstance().execute("eqiCompDialog.hide()");
	}

	// -- Process Equity Companies End

	// -- Process Category Codes Start

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

	public void changeSubChildCategory(AjaxBehaviorEvent e) {
		String selPmCatL3 = getManagedBean().getSelectedSubChildCat();
		if (selPmCatL3 == null) {
			log.info(" --- Selected is a null --- ");
			PmCategoryL3 p3 = new PmCategoryL3();
			getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
		} else {
			for (PmCategoryL3 p3 : getManagedBean().getSelectedCategory()
					.getPmCategoryL2().getPmCategoryL3s()) {
				if (p3.getCategoryL3Code().equalsIgnoreCase(selPmCatL3)) {
					log.info("***** Selected Child is ********"
							+ p3.getCategoryL3Code() + " --- Name ----"
							+ p3.getCategoryName());
					if (p3.getCategoryL3Code().equals("1")) {
					} else if (p3.getCategoryL3Code().equals("2")) {
						getManagedBean().getSelectedManufacturer().setPmUom(
								new PmUom());
					} else if (p3.getCategoryL3Code().equals("3")) {
					} else {
					}
					getManagedBean().getSelectedCategory().setPmCategoryL3(p3);

				}
			}
		}

	}

	public void changeCatEntryModeValues(AjaxBehaviorEvent e) {
		// log.info("**** ENTER THE CHANGE OF CATEGORY TYPE **** "
		// + getManagedBean().getSelectedCategory().getEntryMode());
		resetSelectedCategoryParams();

	}

	/**
	 * resetting all the values
	 * 
	 * @param e
	 */
	private void resetSelectedCategoryParams() {
		// getManagedBean().setSelectedSubChildCat(null);
		// getManagedBean().setSelectedChildCat(null);
		// getManagedBean().setSelectedParentCat(null);
		PmCategoryL1 p = new PmCategoryL1();
		p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		PmCategoryL3 p3 = new PmCategoryL3();
		getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
	}

	public void assignNewCategoryCode() {
		boolean validCatCode = true;
		if (getManagedBean().getSmAppl().getSmSupplierCategories().size() == 30) {
			validCatCode = false;
			throw new EpException(MessageConst.ERROR_722517);
		}

		if (validCatCode) {
			SmSupplierCategory categ = new SmSupplierCategory();
			categ.setRevNo(1);
			// categ.setApprvrDecFlg(1);
			// categ.setPoDecFlg(1);
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

	public void cancelCategoryCodesSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:regCatDialog");
		RequestContext.getCurrentInstance().execute("regCatDialog.hide()");
	}

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
		SmApplicationUtil.assignActiveCategoryList(getManagedBean());
	}

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

	public void saveSelectedCategory() {
		boolean validCat = true;
		if (getManagedBean().getSelectedCategory().getPmCategoryL1() != null
				&& getManagedBean().getSelectedCategory().getPmCategoryL1()
						.getCategoryL1Id() != null) {
			for (PmCategoryL1 catL1 : getManagedBean()
					.getSupplierParentCatList()) {
				if (catL1.getCategoryL1Id().equals(
						getManagedBean().getSelectedCategory()
								.getPmCategoryL1().getCategoryL1Id())) {
					getManagedBean().getSelectedCategory().setPmCategoryL1(
							catL1);
					break;
				}
			}
			if (getManagedBean().getSelectedCategory().getPmCategoryL1()
					.getCategoryL1Code() != null
					&& getManagedBean()
							.getSelectedCategory()
							.getPmCategoryL1()
							.getCategoryL1Code()
							.equalsIgnoreCase(
									SupplierManagementConst.ICT_CATEGORY_L1_CODE)) {
				validCat = SmApplicationUtil.validateIctCatCode(
						getManagedBean().getSmAppl(), getManagedBean()
								.getSelectedCategory());
			}
		}
		boolean overLapCategory = false;
		overLapCategory = SmApplicationUtil.validL3CategoryCodeForOverLapping(
				getManagedBean().getSelectedCategory().getPmCategoryL3()
						.getCategoryL3Id(), getManagedBean().getSmSupplier()
						.getSupplierId());
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
					throw new EpException(MessageConst.ERROR_722563);
				}
				throw new EpException(MessageConst.ERROR_722533);
			}
		}

		if (validCat) {
			for (PmCategoryL1 catL1 : getManagedBean()
					.getSupplierParentCatList()) {
				if (catL1.getCategoryL1Id().equals(
						getManagedBean().getSelectedCategory()
								.getPmCategoryL1().getCategoryL1Id())) {
					getManagedBean().getSelectedCategory().setPmCategoryL1(
							catL1);
					break;
				}
			}
			for (PmCategoryL2 catL2 : getManagedBean()
					.getSupplierChildCatList()) {
				if (catL2.getCategoryL2Id().equals(
						getManagedBean().getSelectedCategory()
								.getPmCategoryL2().getCategoryL2Id())) {
					getManagedBean().getSelectedCategory().setPmCategoryL2(
							catL2);
					break;
				}
			}
			for (PmCategoryL3 catL3 : getManagedBean()
					.getSupplierSubChildCatList()) {
				if (catL3.getCategoryL3Id().equals(
						getManagedBean().getSelectedCategory()
								.getPmCategoryL3().getCategoryL3Id())) {
					getManagedBean().getSelectedCategory().setPmCategoryL3(
							catL3);
					break;
				}
			}
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.add(getManagedBean().getSelectedCategory());

			if (!getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
				RequestContext
						.getCurrentInstance()
						.execute(
								"smCatCodesNonRenewApplDtWidVar.paginator.endLink.click()");
			} else {
				RequestContext
						.getCurrentInstance()
						.execute(
								"smCatCodesRenewApplDtWidVar.paginator.endLink.click()");
			}
			SmApplicationUtil.assignActiveCategoryList(getManagedBean());
			assignValReqdToFalse();
			// API FOR LIST OF OVERLAPPING CATEGORIES
			SmApplicationUtil.checkOverLappingCategories(getManagedBean());
			RequestContext.getCurrentInstance().execute("regCatDialog.hide()");

		}
	}

	// -- Process Category Codes End
	// factory start
	public void addOrUpdateFactoryToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedFactoryFacility();
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplFactoryFacilities().contains(smFac)) {
				log.info(" ******* THIS IS EDIT FOR EXISITING FACTORY **********");
			}

		} else {
			getManagedBean().getApplFactoryFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
			RequestContext.getCurrentInstance().execute(
					"smFacilityFactorDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("factoryDialog.hide()");

	}

	public void cancelFactorySaveorUpdate() {
		assignValReqdToFalse();
		resetFacilityValues(getManagedBean().getSelectedFactoryFacility());
		RequestContext.getCurrentInstance().reset(":form:tt:factoryDialog");
		RequestContext.getCurrentInstance().execute("factoryDialog.hide()");
	}

	public void assignSelFactory(SmFacility facilityBean) {
		log.info("******** Selected value ******"
				+ facilityBean.getSmAddress().getAddress1());
		assignStateDistAndCityForSelVal(facilityBean.getSmAddress()
				.getPmCountry().getCountryId(), facilityBean.getSmAddress()
				.getPmState().getStateId(), facilityBean.getSmAddress()
				.getPmDistrict().getDistrictId(),
				SupplierManagementConst.SM_FACTORY_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		setPreviousFacilityValues(facilityBean);
		getManagedBean().setSelectedFactoryFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.factory", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("factoryDialog.show()");
	}

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
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.factory", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("factoryDialog.show()");
	}

	public void factoryOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedFactoryFacility()
						.getOwnershipType());
	}

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
	public void addOrUpdateDockYardToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedDockyardFacility();
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplDockyardFacilities().contains(smFac)) {
				// TODO any validation for edit
			}

		} else {
			getManagedBean().getApplDockyardFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
			RequestContext.getCurrentInstance().execute(
					"smFacilityDockDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("dockYardDialog.hide()");

	}

	public void cancelDockyardSaveorUpdate() {
		assignValReqdToFalse();
		resetFacilityValues(getManagedBean().getSelectedDockyardFacility());
		RequestContext.getCurrentInstance().reset(":form:tt:dockYardDialog");
		RequestContext.getCurrentInstance().execute("dockYardDialog.hide()");
	}

	public void assignSelDockyard(SmFacility facilityBean) {
		assignStateDistAndCityForSelVal(facilityBean.getSmAddress()
				.getPmCountry().getCountryId(), facilityBean.getSmAddress()
				.getPmState().getStateId(), facilityBean.getSmAddress()
				.getPmDistrict().getDistrictId(),
				SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		setPreviousFacilityValues(facilityBean);
		getManagedBean().setSelectedDockyardFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.dockyard", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("dockYardDialog.show()");
	}

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
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.dockyard", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("dockYardDialog.show()");
	}

	public void dockOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedDockyardFacility()
						.getOwnershipType());
	}

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
	public void addOrUpdateWorkshopToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedWorkShopFacility();
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplWorkShopFacilities().contains(smFac)) {
				log.info(" ******* THIS IS EDIT FOR EXISITING workshop **********");
			}

		} else {
			getManagedBean().getApplWorkShopFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
			RequestContext.getCurrentInstance().execute(
					"smWorkShopDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("workShopDialog.hide()");
	}

	public void cancelWorkShopSaveorUpdate() {
		assignValReqdToFalse();
		resetFacilityValues(getManagedBean().getSelectedWorkShopFacility());
		RequestContext.getCurrentInstance().reset(":form:tt:workShopDialog");
		RequestContext.getCurrentInstance().execute("workShopDialog.hide()");
	}

	public void assignSelWorkshop(SmFacility facilityBean) {
		assignStateDistAndCityForSelVal(facilityBean.getSmAddress()
				.getPmCountry().getCountryId(), facilityBean.getSmAddress()
				.getPmState().getStateId(), facilityBean.getSmAddress()
				.getPmDistrict().getDistrictId(),
				SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		setPreviousFacilityValues(facilityBean);
		getManagedBean().setSelectedWorkShopFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.workshop", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("workShopDialog.show()");
	}

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
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.workshop", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("workShopDialog.show()");
	}

	public void workShopOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedWorkShopFacility()
						.getOwnershipType());
	}

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
	public void addOrUpdateStoreToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedStoreFacility();
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplStoreFacilities().contains(smFac)) {
				log.info(" ******* THIS IS EDIT FOR EXISITING workshop **********");
			}

		} else {
			getManagedBean().getApplStoreFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
			RequestContext.getCurrentInstance().execute(
					"smFacStoreDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		saveOrUpdateAppl();
		RequestContext.getCurrentInstance().execute("storeDialog.hide()");
	}

	public void cancelStoreSaveorUpdate() {
		assignValReqdToFalse();
		resetFacilityValues(getManagedBean().getSelectedStoreFacility());
		RequestContext.getCurrentInstance().reset(":form:tt:storeDialog");
		RequestContext.getCurrentInstance().execute("storeDialog.hide()");
	}

	public void assignSelStore(SmFacility facilityBean) {
		assignStateDistAndCityForSelVal(facilityBean.getSmAddress()
				.getPmCountry().getCountryId(), facilityBean.getSmAddress()
				.getPmState().getStateId(), facilityBean.getSmAddress()
				.getPmDistrict().getDistrictId(),
				SupplierManagementConst.SM_STORE_FACILITY_PANEL);
		setFacilityDefaultPrices(facilityBean);
		setPreviousFacilityValues(facilityBean);
		getManagedBean().setSelectedStoreFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.store", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("storeDialog.show()");
	}

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
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.store", null)));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("storeDialog.show()");
	}

	public void storeOwnerTypeSel(AjaxBehaviorEvent e) {
		log.info("Selected Owner type === "
				+ getManagedBean().getSelectedStoreFacility()
						.getOwnershipType());
	}

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
		if (getManagedBean().getSmAppl().getSmManufacturingDtls()
				.contains(getManagedBean().getSelectedManufacturer())) {

			log.info(" **** THIS IS AN UPDATE For manufacturer******"
					+ getManagedBean().getSelectedManufacturer().getBrand());
		} else {

			getManagedBean().getSmAppl().getSmManufacturingDtls()
					.add(getManagedBean().getSelectedManufacturer());
			RequestContext.getCurrentInstance().execute(
					"smManuDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("manufactorDialog.hide()");
	}

	public void cancelManuSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:manufactorDialog");
		RequestContext.getCurrentInstance().execute("manufactorDialog.hide()");
	}

	public void assignSelManufacturer(SmManufacturingDtl manBean) {
		getManagedBean().setSelectedManufacturer(manBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("manufactorDialog.show()");
	}

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
	public void addOrUpdateMachineToSmAppl() {

		if (getManagedBean().getSmAppl().getSmMachineDtls()
				.contains(getManagedBean().getSelectedMachine())) {

			log.info(" **** THIS IS AN UPDATE For machinery ******"
					+ getManagedBean().getSelectedMachine().getBrandName());
		} else {

			getManagedBean().getSmAppl().getSmMachineDtls()
					.add(getManagedBean().getSelectedMachine());
			RequestContext.getCurrentInstance().execute(
					"smMachDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("equipmentDialog.hide()");
	}

	public void cancelMachineSaveorUpdate() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:equipmentDialog");
		RequestContext.getCurrentInstance().execute("equipmentDialog.hide()");
	}

	public void assignSelMachinery(SmMachineDtl macBean) {
		getManagedBean().setSelectedMachine(macBean);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equipmentDialog.show()");
	}

	public void assignNewMachineryBean() {
		getManagedBean().setSelectedMachine(new SmMachineDtl());
		getManagedBean().getSelectedMachine().setRevNo(1);
		getManagedBean().getSelectedMachine().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedMachine().setValue(new BigDecimal(0.00));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equipmentDialog.show()");
	}

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

	// -- Process Share Holder Personnel Start
	public void addOrUpdateShareHolderToSmAppl() throws ParseException {
		boolean validShrHldr = true;
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			calculateShareHolderPercent();
		}

		getManagedBean().setIdentificationTypes(getPmIdTypeDropdownValues());
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& getManagedBean().getSelectedShareHolder()
						.getWorkExperienceYear() < 5
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
				&& getManagedBean().getSelectedShareHolder().isEquityOwner()
				&& (getManagedBean().getSelectedShareHolder()
						.getSmStaffAcademics() == null || getManagedBean()
						.getSelectedShareHolder().getSmStaffAcademics()
						.isEmpty())) {

			validShrHldr = false;
			throw new EpException(SmCommonUtil.getMessage(
					"lbl.sm.academicqualification.required.atleast.one", null));
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& (getManagedBean().getSelectedShareHolder().isProf()
						|| getManagedBean().getSelectedShareHolder()
								.isSemiProf() || getManagedBean()
						.getSelectedShareHolder().isClerical())
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
		getManagedBean().getSelectedShareHolder().setSmAppl(
				getManagedBean().getSmAppl());
		if (validShrHldr) {
			if (getManagedBean().getSmAppl().getSmPersonnels()
					.contains(getManagedBean().getSelectedShareHolder())) {
				getManagedBean()
						.getSmAppl()
						.getSmPersonnels()
						.set(getManagedBean()
								.getSmAppl()
								.getSmPersonnels()
								.indexOf(
										getManagedBean()
												.getSelectedShareHolder()),
								getManagedBean().getSelectedShareHolder());

				log.info(" **** THIS IS AN UPDATE Personnel ******");
			} else {
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
				getManagedBean().getSmAppl().getSmPersonnels()
						.add(getManagedBean().getSelectedShareHolder());

			}
			if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() == 0) {
				getManagedBean().getSelectedShareHolder().setEpRole(null);
				getManagedBean().getSelectedShareHolder().setSoftcertFlg(0);
				getManagedBean().getSelectedShareHolder().setContractSignerFlg(
						0);
			}
			saveOrUpdateAppl();
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

	private void saveOrUpdateAppl() {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);

			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSmAppl(this.getManagedBean().getSmAppl());
			mofRegistrationSO.updateApplication(reqDto);
		} finally {
			mofRegistrationSO = null;
			retriveLatestApplAfterUpdate("", "");
		}
	}

	public void backFromPersonnelPage() {
		log.info("*** Back to perssonel page ******");
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			calculateCapital();
		}
		if (getManagedBean().getSmAppl().getSmPersonnels()
				.contains(getManagedBean().getSelectedShareHolder())) {

			SmPersonnel per = getManagedBean().getSelectedPersonnel();
			getManagedBean().getSelectedShareHolder().setIdentificationTypeId(
					per.getIdentificationTypeId());
			getManagedBean().getSelectedShareHolder().setIdentificationType(
					per.getIdentificationType());
			getManagedBean().getSelectedShareHolder().setIdentificationNo(
					per.getIdentificationNo());
			getManagedBean().getSelectedShareHolder().setSmPersonnelAddresses(
					per.getSmPersonnelAddresses());
			if (per.getEpRole() != null) {
				getManagedBean().getSelectedShareHolder().setEpRole(
						per.getEpRole());
			}
			if (per.getPmRace() != null && per.getPmRace().getRaceId() != null) {
				log.info(" *** Not null race ******");
				getManagedBean().getSelectedShareHolder().setPmRace(
						per.getPmRace());
			} else {
				log.info(" ***Empty race value  ******");
				getManagedBean().getSelectedShareHolder().setPmRace(null);

			}
			getManagedBean().getSelectedShareHolder().setAuthorizedFlg(
					per.getAuthorizedFlg());
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(
					per.getContractSignerFlg());
			getManagedBean().getSelectedShareHolder().setSoftcertFlg(
					per.getSoftcertFlg());
			getManagedBean().getSelectedShareHolder().setDirector(
					per.isDirector());
			getManagedBean().getSelectedShareHolder().setManagement(
					per.isManagement());
			getManagedBean().getSelectedShareHolder().setHigherManagement(
					per.isHigherManagement());
			getManagedBean().getSelectedShareHolder().setProf(per.isProf());
			getManagedBean().getSelectedShareHolder().setSemiProf(
					per.isSemiProf());
			getManagedBean().getSelectedShareHolder().setClerical(
					per.isClerical());

		}
		getManagedBean().setRedirectToPersonPage(false);
		RequestContext.getCurrentInstance().reset(":form:tt:shareHolderDialog");
	}

	public void assignSelShareHolder(SmPersonnel shareHolderBean) {
		// if (!getManagedBean().getSmAppl().getApplType()
		// .equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
		// && (shareHolderBean.isEquityOwner() || shareHolderBean
		// .isDirector())
		// && getManagedBean().isDisAllowOwnerEdit()) {
		// throw new EpException(MessageConst.ERROR_722573);
		// }
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
				&& shareHolderBean.getPmUser().getUserId() != null)
			getManagedBean().setDisableIcEmail(true);
		else
			getManagedBean().setDisableIcEmail(false);

		getManagedBean().setIdentificationTypes(getPmIdTypeDropdownValues());
		if (shareHolderBean.getEpRole() != null
				&& (shareHolderBean.getEpRole().equalsIgnoreCase(
						RoleCode.MOF_SUPPLIER_USER.value())
						|| shareHolderBean.getEpRole().equalsIgnoreCase(
								RoleCode.MOF_SUPPLIER_ADMIN.value()) || shareHolderBean
						.getEpRole().equalsIgnoreCase(RoleCode.CM_USER.value()))) {
			getManagedBean().setUserWithRole(true);
		} else {
			getManagedBean().setUserWithRole(false);
		}
		if (shareHolderBean.getPmCountry() == null) {
			shareHolderBean.setPmCountry(new PmCountry());
		}
		if (shareHolderBean.getPmRace() == null) {
			shareHolderBean.setPmRace(new PmRace());
		}
		if (shareHolderBean.getEpRole() == null) {
			shareHolderBean.setEpRole("");
		}
		assignSelShareHolderAddress(shareHolderBean);
		getManagedBean().setSelectedShareHolder(shareHolderBean);
		managePersonnelRoles();
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
		// use this object to reset some values on back button click
		SmPersonnel per = new SmPersonnel();
		per.setIdentificationTypeId(shareHolderBean.getIdentificationTypeId());
		per.setIdentificationType(shareHolderBean.getIdentificationType());
		per.setIdentificationNo(shareHolderBean.getIdentificationNo());
		per.setSmPersonnelAddresses(shareHolderBean.getSmPersonnelAddresses());
		if (shareHolderBean.getEpRole() != null) {
			per.setEpRole(shareHolderBean.getEpRole());
		}
		if (shareHolderBean.getPmRace() != null) {
			per.setPmRace(shareHolderBean.getPmRace());
		}
		per.setAuthorizedFlg(shareHolderBean.getAuthorizedFlg());
		per.setContractSignerFlg(shareHolderBean.getContractSignerFlg());
		per.setSoftcertFlg(shareHolderBean.getSoftcertFlg());
		per.setDirector(shareHolderBean.isDirector());
		per.setManagement(shareHolderBean.isManagement());
		per.setHigherManagement(shareHolderBean.isHigherManagement());
		per.setProf(shareHolderBean.isProf());
		per.setSemiProf(shareHolderBean.isSemiProf());
		per.setClerical(shareHolderBean.isClerical());
		getManagedBean().setSelectedPersonnel(per);
		if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() != 1)
			getManagedBean().setRemoveEpRole(true);
		else
			getManagedBean().setRemoveEpRole(false);
		buildRoleList();
		if (!getManagedBean().isRemoveEpRole()
				&& (getManagedBean().getSelectedShareHolder().getEpRole() != null && getManagedBean()
						.getSelectedShareHolder().getEpRole()
						.equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value()))) {
			SelectItem role = new SelectItem();
			role.setLabel(SmCommonUtil.getMessage(
					"lbl.sm.person.composition.suppAdmin", null));
			role.setValue(RoleCode.MOF_SUPPLIER_ADMIN.value());
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

		// disabled justification
		// disableJustification(shareHolderBean);
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

	public void addOrUpdateShareHolderAddress(SmPersonnel shareHolderBean)
			throws ParseException {
		if (shareHolderBean.getAuthorizedFlg() == 0) {
			getManagedBean().getShareHolderMailAddr().setSameAsPerm(true);
		}

		if (shareHolderBean.isDirector() || shareHolderBean.isDirector()
				|| shareHolderBean.isHigherManagement()) {
			// do nothing
		} else {
			getManagedBean().getShareHolderOffAddr().setSameAsPerm(true);
			getManagedBean().getShareHolderResdAddr().setSameAsPerm(true);
		}
		// clone the addrs start
		SmApplicationUtil.cloneShareHolderAddress(getManagedBean());
		// clone the addrs end
		if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {
			shareHolderBean.getSmPersonnelAddresses().add(
					getManagedBean().getShareHolderResdAddr());
			shareHolderBean.getSmPersonnelAddresses().add(
					getManagedBean().getShareHolderPermAddr());
			shareHolderBean.getSmPersonnelAddresses().add(
					getManagedBean().getShareHolderOffAddr());
			shareHolderBean.getSmPersonnelAddresses().add(
					getManagedBean().getShareHolderMailAddr());
			getManagedBean().setSelectedShareHolder(shareHolderBean);
		} else {

			for (SmPersonnelAddress addr : shareHolderBean
					.getSmPersonnelAddresses()) {
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
					getManagedBean().setShareHolderMailAddr(addr);
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderMailAddr());
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
					getManagedBean().setShareHolderOffAddr(addr);
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderOffAddr());
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
					getManagedBean().setShareHolderResdAddr(addr);
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderResdAddr());
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
					getManagedBean().setShareHolderPermAddr(addr);
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderPermAddr());
				}
			}

			if (!shareHolderBean.getSmPersonnelAddresses().contains(
					getManagedBean().getShareHolderPermAddr())) {
				shareHolderBean.getSmPersonnelAddresses().add(
						getManagedBean().getShareHolderPermAddr());
			}
			if (!shareHolderBean.getSmPersonnelAddresses().contains(
					getManagedBean().getShareHolderMailAddr())) {
				shareHolderBean.getSmPersonnelAddresses().add(
						getManagedBean().getShareHolderMailAddr());
			}
			if (!shareHolderBean.getSmPersonnelAddresses().contains(
					getManagedBean().getShareHolderResdAddr())) {
				shareHolderBean.getSmPersonnelAddresses().add(
						getManagedBean().getShareHolderResdAddr());
			}
			if (!shareHolderBean.getSmPersonnelAddresses().contains(
					getManagedBean().getShareHolderOffAddr())) {
				shareHolderBean.getSmPersonnelAddresses().add(
						getManagedBean().getShareHolderOffAddr());
			}

			getManagedBean().setSelectedShareHolder(shareHolderBean);

		}

	}

	public void buildRoleList() {
		List<SelectItem> roles = new ArrayList<SelectItem>();
		SelectItem role = new SelectItem();
		role = new SelectItem();
		role.setLabel("Supplier User");
		role.setValue(RoleCode.MOF_SUPPLIER_USER.value());
		if (!getManagedBean().isRemoveEpRole())
			roles.add(role);
		role = new SelectItem();
		role.setLabel("Catalogue User");
		role.setValue(RoleCode.CM_USER.value());
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR))
			roles.add(role);
		getManagedBean().setePRoleList(roles);
	}

	public void assignNewShareHolder(String personelType) {
		log.info("********* assignNewShareHolder *****");
		// getManagedBean().setAuthPerCount(getAuthUserCount());
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
					throw new EpException(MessageConst.ERROR_722547,
							new String[] { "1" });
				} else {
					throw new EpException(MessageConst.ERROR_722547,
							new String[] { "20" });
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
		// getManagedBean().setSelectedPersonnel(null);

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
		buildRoleList();
		getManagedBean().setRedirectToPersonPage(true);

		// set default nationality to MALAYSIA
		getManagedBean()
				.getSelectedShareHolder()
				.setPmCountry(
						SmCommonUtil
								.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE));

		// enabled justification if disabled
		// disableJustification(null);

		// reset personnel view only to false for add new
		getManagedBean().setShowPersonnelViewOnly(false);

		// reset personnel name and nationality to false for add new
		getManagedBean().setDisabledShareholderPersonnelNameAndNationality(
				false);

	}

	public void deleteSelectedShareHolders(String personType) {
		// if (!personType
		// .equalsIgnoreCase(SupplierManagementConst.PERSONNEL_TYPE_EQUITY)) {
		// for (SmPersonnel shr : getManagedBean().getSelectedShareHolders()) {
		// if (shr.isEquityOwner()) {
		// throw new EpException(MessageConst.ERROR_722536);
		// }
		// if(shr.getEpRole().equalsIgnoreCase("MOF_SUPPLIER_ADMIN")
		// ||shr.getEpRole().equalsIgnoreCase("BASIC_SUPPLIER_ADMIN")){
		// throw new EpException(MessageConst.ERROR_722553);
		// }
		// }
		// }
		for (SmPersonnel shr : getManagedBean().getSelectedShareHolders()) {
			if (shr.getEpRole() != null
					&& (shr.getEpRole().equalsIgnoreCase("MOF_SUPPLIER_ADMIN") || shr
							.getEpRole().equalsIgnoreCase(
									"BASIC_SUPPLIER_ADMIN"))) {
				throw new EpException(MessageConst.ERROR_722553);
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

	public void assignSelShrHldrJobFunc(SmStaffExperience exp) {
		log.info("*********** Selected value *********" + exp.getDesignation());
		getManagedBean().setSelectedJobFunction(exp);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.show()");

	}

	public void addOrUpdateJobFunctions() {

		if (getManagedBean()
				.getSelectedJobFunction()
				.getFromYear()
				.compareTo(
						getManagedBean().getSelectedJobFunction().getToYear()) > 0) {
			throw new EpException(MessageConst.ERROR_722531);

		}
		if (getManagedBean().getSelectedShareHolder().getSmStaffExperiences()
				.contains(getManagedBean().getSelectedJobFunction())) {
			// TODO if any validation reqd for edit
		} else {
			RequestContext.getCurrentInstance().execute(
					"suppPrsnJobFunDtWidVar.paginator.endLink.click()");
			getManagedBean().getSelectedShareHolder().getSmStaffExperiences()
					.add(getManagedBean().getSelectedJobFunction());
		}
		assignValReqdToFalse();

		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.hide()");

	}

	public void cancelJobFunctions() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(
				":form:tt:equiJobFunctionInfo");
		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.hide()");
	}

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

	public void addShrHldrRelatedCompany() {
		SmRelatedCompany com = new SmRelatedCompany();
		com.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		getManagedBean().setSelectedRelatedCompany(com);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.show()");
	}

	public void assignSelShrHldrRelatedCompany(SmRelatedCompany com) {
		getManagedBean().setSelectedRelatedCompany(com);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.show()");

	}

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
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.hide()");

	}

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

	public void cancelShrHldrRelatedCompanies() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:equiRelCompInfo");
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.hide()");
	}

	public void addShrHldrAcademicQualification() {

		SmStaffAcademic aca = new SmStaffAcademic();
		aca.setPmEduField(new PmEduField());

		aca.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		getManagedBean().setSelectedStaffAcademic(aca);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equiAcadInfo.show()");
	}

	public void assignSelShrHldrAcademics(SmStaffAcademic aca) {
		getManagedBean().setSelectedStaffAcademic(aca);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("equiAcadInfo.show()");

	}

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

	public void cancelShrHldrAcademic() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:equiAcadInfo");
		RequestContext.getCurrentInstance().execute("equiAcadInfo.hide()");
	}

	public void addOrUpdateShrHldrAcademic() {
		for (PmEduField edu : getManagedBean().getEduFieldList()) {
			if (getManagedBean().getSelectedStaffAcademic().getPmEduField()
					.getEduFieldId().equals(edu.getEduFieldId())) {
				getManagedBean().getSelectedStaffAcademic().setPmEduField(edu);
				break;
			}
		}

		if (getManagedBean().getSelectedStaffAcademic()
				.getQualificationLevelId() != null) {
			PmParameter param = SmApplicationUtil
					.retrievePmParameterById(getManagedBean()
							.getSelectedStaffAcademic()
							.getQualificationLevelId().longValue());
			String qual = "";
			if (param != null && param.getParameterCode() != null) {
				qual = param.getParameterCode();
				if (getManagedBean().getSelectedShareHolder().isProf()) {
					if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_NOTDEFINED)
							|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_CERTIFIED)
							|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_DIPLOMA)) {
						throw new EpException(MessageConst.ERROR_722574,
								new String[] { "Professional Staff",
										"BACHELOR'S DEGREE" });
					}
				} else if (getManagedBean().getSelectedShareHolder()
						.isSemiProf()) {
					if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_NOTDEFINED)
							|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_CERTIFIED)) {
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
		RequestContext.getCurrentInstance().execute("equiAcadInfo.hide()");

	}

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

	public void assignSelShrHldrProfBody(SmAssociationBody asb) {
		getManagedBean().setSelectedStfPrfBdy(asb);
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.show()");

	}

	public void cancelShrHldrProfBody() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(
				":form:tt:equiShrProfBeanInfo");
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.hide()");
	}

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
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.hide()");

	}

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

	public void assignSelShareHolderAddress(SmPersonnel shareHolderBean) {

		SmPersonnelAddress perm = new SmPersonnelAddress();
		perm.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM);
		perm.setSmAddress(SmApplicationUtil.setDefaultAddr(getManagedBean()
				.getDefPmCountry()));
		perm.setSmPersonnel(shareHolderBean);
		perm.setSameAsPerm(false);

		SmPersonnelAddress mail = new SmPersonnelAddress();
		mail.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL);
		mail.setSmAddress(SmApplicationUtil.setDefaultAddr(getManagedBean()
				.getDefPmCountry()));
		mail.setSmPersonnel(shareHolderBean);
		mail.setSameAsPerm(false);

		SmPersonnelAddress off = new SmPersonnelAddress();
		off.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE);
		off.setSmAddress(SmApplicationUtil.setDefaultAddr(getManagedBean()
				.getDefPmCountry()));
		off.setSmPersonnel(shareHolderBean);
		off.setSameAsPerm(false);

		SmPersonnelAddress res = new SmPersonnelAddress();
		res.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES);
		res.setSmAddress(SmApplicationUtil.setDefaultAddr(getManagedBean()
				.getDefPmCountry()));
		res.setSmPersonnel(shareHolderBean);
		res.setSameAsPerm(false);

		getManagedBean().setShareHolderPermAddr(perm);
		getManagedBean().setShareHolderMailAddr(mail);
		getManagedBean().setShareHolderOffAddr(off);
		getManagedBean().setShareHolderResdAddr(res);
		List<PmState> defStates = new ArrayList<PmState>();
		defStates = retrievePmStateList(perm.getSmAddress().getPmCountry()
				.getCountryId());
		if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {

			// reset to defualt values for a new record
			getManagedBean().setShrHldrPermStates(defStates);
			getManagedBean().setShrHldrPermDistricts(
					new ArrayList<PmDistrict>());
			getManagedBean().setShrHldrPermCities(new ArrayList<PmCity>());
			getManagedBean().setShrHldrMailStates(defStates);
			getManagedBean().setShrHldrMailDistricts(
					new ArrayList<PmDistrict>());
			getManagedBean().setShrHldrMailCities(new ArrayList<PmCity>());
			getManagedBean().setShrHldrResdStates(defStates);
			getManagedBean().setShrHldrResdDistricts(
					new ArrayList<PmDistrict>());
			getManagedBean().setShrHldrResdCities(new ArrayList<PmCity>());
			getManagedBean().setShrHldrOffStates(defStates);
			getManagedBean()
					.setShrHldrOffDistricts(new ArrayList<PmDistrict>());
			getManagedBean().setShrHldrOffCities(new ArrayList<PmCity>());

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
				PmState state = new PmState();
				state.setStateId(addr.getSmAddress().getPmState().getStateId());
				smAddress.setPmState(state);
				PmDistrict district = new PmDistrict();
				district.setDistrictId(addr.getSmAddress().getPmDistrict()
						.getDistrictId());
				smAddress.setPmDistrict(district);
				PmCity city = new PmCity();
				city.setCityId(addr.getSmAddress().getPmCity().getCityId());
				smAddress.setPmCity(city);
				smAddress.setPostcode(addr.getSmAddress().getPostcode());
				addr.setSmAddress(smAddress);

				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
					getManagedBean().setShareHolderPermAddr(addr);
					assignStateDistAndCityForSelVal(getManagedBean()
							.getShareHolderPermAddr().getSmAddress()
							.getPmCountry().getCountryId(), getManagedBean()
							.getShareHolderPermAddr().getSmAddress()
							.getPmState().getStateId(), getManagedBean()
							.getShareHolderPermAddr().getSmAddress()
							.getPmDistrict().getDistrictId(),
							SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL);

				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
					getManagedBean().setShareHolderMailAddr(addr);
					assignStateDistAndCityForSelVal(getManagedBean()
							.getShareHolderMailAddr().getSmAddress()
							.getPmCountry().getCountryId(), getManagedBean()
							.getShareHolderMailAddr().getSmAddress()
							.getPmState().getStateId(), getManagedBean()
							.getShareHolderMailAddr().getSmAddress()
							.getPmDistrict().getDistrictId(),
							SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL);

				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
					getManagedBean().setShareHolderResdAddr(addr);
					assignStateDistAndCityForSelVal(getManagedBean()
							.getShareHolderResdAddr().getSmAddress()
							.getPmCountry().getCountryId(), getManagedBean()
							.getShareHolderResdAddr().getSmAddress()
							.getPmState().getStateId(), getManagedBean()
							.getShareHolderResdAddr().getSmAddress()
							.getPmDistrict().getDistrictId(),
							SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL);

				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
					getManagedBean().setShareHolderOffAddr(addr);
					assignStateDistAndCityForSelVal(getManagedBean()
							.getShareHolderOffAddr().getSmAddress()
							.getPmCountry().getCountryId(), getManagedBean()
							.getShareHolderOffAddr().getSmAddress()
							.getPmState().getStateId(), getManagedBean()
							.getShareHolderOffAddr().getSmAddress()
							.getPmDistrict().getDistrictId(),
							SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL);
				}
			}

			if ((getManagedBean().getShrHldrPermStates() == null)
					|| (getManagedBean().getShrHldrPermStates() != null && getManagedBean()
							.getShrHldrPermStates().isEmpty())) {
				getManagedBean().setShrHldrPermStates(defStates);
				getManagedBean().setShrHldrPermDistricts(
						new ArrayList<PmDistrict>());
				getManagedBean().setShrHldrPermCities(new ArrayList<PmCity>());
			}
			if ((getManagedBean().getShrHldrMailStates() == null)
					|| (getManagedBean().getShrHldrMailStates() != null && getManagedBean()
							.getShrHldrMailStates().isEmpty())) {
				getManagedBean().setShrHldrMailStates(defStates);
				getManagedBean().setShrHldrMailDistricts(
						new ArrayList<PmDistrict>());
				getManagedBean().setShrHldrMailCities(new ArrayList<PmCity>());
			}
			if ((getManagedBean().getShrHldrResdStates() == null)
					|| (getManagedBean().getShrHldrResdStates() != null && getManagedBean()
							.getShrHldrResdStates().isEmpty())) {
				getManagedBean().setShrHldrResdStates(defStates);
				getManagedBean().setShrHldrResdDistricts(
						new ArrayList<PmDistrict>());
				getManagedBean().setShrHldrResdCities(new ArrayList<PmCity>());
			}
			if ((getManagedBean().getShrHldrOffStates() == null)
					|| (getManagedBean().getShrHldrOffStates() != null && getManagedBean()
							.getShrHldrOffStates().isEmpty())) {
				getManagedBean().setShrHldrOffStates(defStates);
				getManagedBean().setShrHldrOffDistricts(
						new ArrayList<PmDistrict>());
				getManagedBean().setShrHldrOffCities(new ArrayList<PmCity>());
			}

		}

		getManagedBean().setSelectedShareHolder(shareHolderBean);

	}

	// -- Process Common for both Personnel and Share Holder Start --
	// -- Process Personnel Start
	// -- Process Personnel End

	// Process Bank Info Start
	public void addOrUpdateBankInfoToSmAppl() {
		getManagedBean().getSelectedBank().getSmChequeSignatories().clear();
		getManagedBean().setSelectedSignatoryBankAcct(new SmSupplierBank());
		for (Long perId : getManagedBean().getChequeSingPerId()) {
			for (SmPersonnel sm : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (sm.getPersonnelId().equals(perId)) {
					SmChequeSignatory ch = new SmChequeSignatory();
					ch.setSmPersonnel(sm);
					ch.setSmSupplierBank(getManagedBean().getSelectedBank());
					getManagedBean().getSelectedBank().getSmChequeSignatories()
							.add(ch);
				}
			}
		}
		for (PmFinancialOrg org : getManagedBean().getFinOrgLs()) {
			if (getManagedBean().getSelectedBank().getPmFinancialOrg()
					.getFinancialOrgId().equals(org.getFinancialOrgId())) {
				getManagedBean().getSelectedBank().setPmFinancialOrg(org);

			}
		}

		if (getManagedBean().getSelBankBranchId() == null) {
			getManagedBean().getSelectedBank().setSmSupplierBranch(null);
		} else {
			for (SmSupplierBranch brn : getManagedBean().getSmAppl()
					.getSmSupplierBranches()) {
				if (brn.getSupplierBranchId().equals(
						getManagedBean().getSelBankBranchId())) {
					getManagedBean().getSelectedBank().setSmSupplierBranch(brn);
					break;
				}
			}
		}
		if (SmApplicationUtil.checkValidBank(
				getManagedBean().getSelectedBank(), getManagedBean()
						.getSmAppl())) {
			if (getManagedBean().getSmAppl().getSmSupplierBanks()
					.contains(getManagedBean().getSelectedBank())) {
				// TODO if any validations for edit
			} else {
				getManagedBean().getSmAppl().getSmSupplierBanks()
						.add(getManagedBean().getSelectedBank());
			}
			assignValReqdToFalse();
			saveOrUpdateAppl();
			RequestContext.getCurrentInstance().execute("bankDialog.hide()");
		}
	}

	public void cancelSmBankFunctions() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:bankDialog");
		RequestContext.getCurrentInstance().execute("bankDialog.hide()");
	}

	public void assignSelBank(SmSupplierBank shareBankBean) {
		getManagedBean().setChequeSingPerId(null);
		getManagedBean().setSelBankBranchId(null);
		Long[] chequSigns = new Long[shareBankBean.getSmChequeSignatories()
				.size()];
		for (int i = 0; i < chequSigns.length; i++) {
			chequSigns[i] = shareBankBean.getSmChequeSignatories().get(i)
					.getSmPersonnel().getPersonnelId();

		}

		if (shareBankBean.getSmSupplierBranch() != null
				&& shareBankBean.getSmSupplierBranch().getSupplierBranchId() != null) {
			getManagedBean().setSelBankBranchId(
					shareBankBean.getSmSupplierBranch().getSupplierBranchId());
		}
		getManagedBean().setChequeSingPerId(chequSigns);
		// shareBankBean.getSmChequeSignatories().clear();
		if (shareBankBean.getSmAddress() != null
				&& shareBankBean.getSmAddress().getPmCountry().getCountryId() != null
				&& shareBankBean.getSmAddress().getPmState().getStateId() != null
				&& shareBankBean.getSmAddress().getPmDistrict().getDistrictId() != null) {
			assignStateDistAndCityForSelVal(shareBankBean.getSmAddress()
					.getPmCountry().getCountryId(), shareBankBean
					.getSmAddress().getPmState().getStateId(), shareBankBean
					.getSmAddress().getPmDistrict().getDistrictId(),
					SupplierManagementConst.SM_BANK_INFO_PANEL);
		} else {
			shareBankBean.setSmAddress(SmApplicationUtil
					.setDefaultAddr(getManagedBean().getDefPmCountry()));
			populateStatesByCountry(shareBankBean.getSmAddress().getPmCountry()
					.getCountryId(),
					SupplierManagementConst.SM_BANK_INFO_PANEL, new PmState(),
					new PmDistrict(), new PmCity());
		}
		getManagedBean().setSelectedBank(shareBankBean);
		retrieveBankFinOrgListByType();
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("bankDialog.show()");
	}

	public void deleteSelectedBanks() {
		if (getManagedBean().getSelectedBanks().size() > 0) {
			for (SmSupplierBank bank : getManagedBean().getSelectedBanks()) {
				bank.getSmChequeSignatories().clear();
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

	public void viewSignatories(SmSupplierBank signAcctBean) {
		getManagedBean().setSelectedSignatoryBankAcct(signAcctBean);
	}

	public void assignNewBank() {
		getManagedBean().setChequeSingPerId(null);
		getManagedBean().setSelBankBranchId(null);
		SmSupplierBank bank = new SmSupplierBank();
		bank.setSmAddress(SmApplicationUtil.setDefaultAddr(getManagedBean()
				.getDefPmCountry()));
		bank.setSmAppl(getManagedBean().getSmAppl());
		bank.setPmFinancialOrg(new PmFinancialOrg());
		bank.setSmChequeSignatories(new ArrayList<SmChequeSignatory>());
		getManagedBean().setSelectedBank(bank);
		getManagedBean().setBankStates(
				retrievePmStateList(getManagedBean().getSelectedBank()
						.getSmAddress().getPmCountry().getCountryId()));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("bankDialog.show()");
	}

	/**
	 * Make a call to the BPM for MofRegistration
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
			MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(bpmAppl,
					SupplierManagementConst.SM_APPL_BPM_OUTCOME_SUBMIT);
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
				reqDTO.setPoLoginId(SmApplicationUtil
						.getPOLoginId(
								SupplierManagementConst.PM_STATUS_PO_ESCALTED_TO_APPROVER,
								SupplierManagementConst.PM_STATUS_PO_REJECTED_APPLICATION,
								getManagedBean().getSmAppl().getApplId()));
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
				// if (reqDTO.getUserList().size() == 0) {
				PmUser pmUser = new FacesScopes().getUserSessionBean();
				reqDTO.getUserList().add(pmUser.getLoginId());
				log.info("Login user added to the user list to BPM");
				// throw new EpException(MessageConst.ERROR_722546);
				// }

				// PmUser pmUser = new FacesScopes().getUserSessionBean();
				// if (!reqDTO.getUserList().contains(pmUser.getLoginId())) {
				// reqDTO.getUserList().add(pmUser.getLoginId());
				// }

			}

			@SuppressWarnings("unused")
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			resDTO = regSo.initiateSmApplicationTask(reqDTO);
			// if(resDTO!=null && !resDTO.isBpmSuccess()){
			// throw new EpException(MessageConst.ERROR_700020);
			// }
		} finally {
			regSo = null;
			retriveLatestApplAfterUpdate("", "");
		}

	}

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
		if (getManagedBean().getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)) {
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
																.getApplNo() },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes()
														.getUserLocale())));
		renewSesionObject();
		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}

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
			// return SupplierManagementConst.PROCEED_CANCEL_MOF_APPLN;
			throw new EpException(MessageConst.ERROR_722519);
		}
	}

	public String submitDeletePendingMofApplToBpm() {
		IMofRegistrationSO regSo = null;
		try {
			if (getManagedBean().getSupplierRemarks().equalsIgnoreCase("")
					|| getManagedBean().getSupplierRemarks() == null) {
				throw new EpException(MessageConst.ERROR_722529);
			}
			SmRemark remark = new SmRemark();
			remark.setDocId(getManagedBean().getSmAppl().getApplId());
			remark.setDocType(ParameterConst.NEW_SM_APPLN_FOR_DELETE_PENDING_APPL);
			remark.setRemark(getManagedBean().getSupplierRemarks());
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
			SmWorkflowStatus sts = getCurrentActiveWokFlowStatus(getManagedBean()
					.getSmAppl().getApplId());
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
		}
		// catch (Exception e) {
		// return SupplierManagementConst.PROCEED_DEL_PENDING_MOF_APPLN;
		// }
		finally {
			regSo = null;
		}
	}

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

	public String saveApplicationAsDraft(String applType, String currentPageType) {
		log.info("****** Save As Draft **********" + currentPageType
				+ currentTab);
		boolean valid = false;
		try {
			valid = validateBeforeSave(applType, currentPageType);
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

	private boolean intializeSaveDraftBpm(String currentPageType) {
		log.info("*********  intializeSaveDraftBpm ************* ");
		IMofRegistrationSO regSo = null;
		boolean valid = false;
		try {
			SmAppl bpmAppl = getManagedBean().getSmAppl();

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
					outcome);
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
		}
		// catch (Exception e) {
		//
		// throw new EpException(MessageConst.ERROR_722519);
		// }

		finally {
			regSo = null;
			retriveLatestApplAfterUpdate(currentPageType, "");

		}

	}

	private boolean deleteDraftApplciationTask() {
		log.info("*********  Delete draft applicaiton task ************* ");
		IMofRegistrationSO regSo = null;
		boolean valid = true;
		try {
			SmAppl bpmAppl = getManagedBean().getSmAppl();
			String outcome = "CANCEL";
			MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(bpmAppl,
					outcome);
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
			throw new EpException(MessageConst.ERROR_722519);
		}

		finally {
			regSo = null;
		}

	}

	private MofRegistrationReqDTO setMofRegistrationReqDTO(SmAppl bpmAppl,
			String outcome) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setOutcome(outcome);
		reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
		reqDTO.setSupplierId(bpmAppl.getSmSupplier().getSupplierId());
		reqDTO.setAppType(bpmAppl.getApplType());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setSmAppl(bpmAppl);
		// reqDTO.setUserName(new FacesScopes().getUserScreenName());
		reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
		reqDTO.setSupplierType(this.getManagedBean().getSmAppl()
				.getSupplierType().toUpperCase());
		PmState state = getManagedBean().getSmAppl().getSmCompanyBasic()
				.getSmCompanyAddresses().get(0).getSmAddress().getPmState();
		if (state != null && state.getStateId() != null) {
			PmZone zone = SmApplicationUtil.retrieveZoneByStateId(state
					.getStateId());
			log.info("*** ZONE SENT TO BPM ***** " + zone.getZoneName());
			reqDTO.setBaseOfficeState(zone.getZoneName().toUpperCase());
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

	public void assignMissingDocsList() {
		log.info("*** Send Inquiry **** ");
		SmApplicationUtil.validateCategoryAndDocsDecision(getManagedBean());
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

	// public String submitPOSendInquiryToBPM(String res) {
	// String output = submitPOorApproverReviewToBPM(res);
	// RequestContext.getCurrentInstance().execute("sendInqDialog.hide()");
	// return output;
	//
	// }
	public void submitPOEscalationToApprover(String recommendation) {
		SmApplicationUtil.validateCategoryAndDocsDecision(getManagedBean());
		getManagedBean().getPoSecReview().setRecommendation(recommendation);
		RequestContext.getCurrentInstance()
				.execute("submitApprvrDialog.show()");
	}

	public void cancelPOEscalationToApprover() {
		getManagedBean().getPoSecReview().setRecommendation("");
		RequestContext.getCurrentInstance()
				.execute("submitApprvrDialog.hide()");
	}

	public void assignFinalApproveOrReject(String dialog) {
		log.info("**** assignFinalApproveOrReject *** ");
		SmApplicationUtil.validateCategoryAndDocsDecision(getManagedBean());
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute(dialog + ".show()");
	}

	public void approveOrRejectMofCancellation(String dialog) {
		log.info("**** assignFinalApproveOrReject *** ");
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute(dialog + ".show()");
	}

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
		RequestContext.getCurrentInstance().execute(dialog + ".show()");
	}

	public void assignViewSiteVisitReport() {
		log.info("**** assignViewSiteVisitReport *** ");
		SvoSiteVisitBackingBean svo = new SvoSiteVisitBackingBean();
		svo.getManagedBean().setAppId(getManagedBean().getSmAppl().getApplId());
		svo.retrieveSiteVisitInfo("V");
		// RequestContext.getCurrentInstance().execute(dialog + ".show()");
	}

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

	public void cancelSiteVisitAction(String dialog) {
		getManagedBean().setShowParentMsg(true);
		RequestContext.getCurrentInstance().reset(":form:tt:" + dialog);
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
	}

	public void cancelFinalApproveOrReject(String dialog) {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:" + dialog);
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
	}

	public void resetMofCancellationFinalApproveOrReject(String dialog) {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:" + dialog);
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
	}

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

	public String finalDecisionDeletePendingAppln(String dialog, String decision) {
		List<String> updateIds = new ArrayList<String>();
		updateIds.add(":form");
		log.info("*** Submit Approve *** " + decision);
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
		RequestContext.getCurrentInstance().update(updateIds);
		return approveOrRejectPendingApplicationCancellation(decision);

	}

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

	public String submitPOorApproverReviewToBPM(String res) {
		IReviewApplicationSO reqSO = null;
		String retMsg = null;
		try {

			Long pmStatId = getManagedBean().getSmAppl().getPmStatus()
					.getStatusId();
			retriveCurrentAppl();
			log.info(" **** After retrieve ***"
					+ getManagedBean().getSmAppl().getPmStatus().getStatusId());
			log.info("----------------------Submit PO / Approver Decision For BPM-------------------------"
					+ res);
			log.info(" **** The Value from screen of approval ***" + pmStatId);
			ReviewApplicationReqDTO reqDTO = new ReviewApplicationReqDTO();
			reqSO = (IReviewApplicationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IReviewApplicationSO.class);
			reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
			reqDTO.setApplNo(this.getManagedBean().getSmAppl().getApplNo());
			reqDTO.setOutcome(res);
			reqDTO.setSuppId(this.getManagedBean().getSmAppl().getSmSupplier()
					.getSupplierId());
			reqDTO.setBpmTaskName(getManagedBean().getBpmTaskName());
			reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setLocation(getManagedBean().getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress().getPmState()
					.getStateName().toUpperCase());
			reqDTO.setSupplierType(this.getManagedBean().getSmAppl()
					.getSupplierType().toUpperCase());
			// reqDTO.setUserName(new FacesScopes().getUserScreenName());
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

			if (res.equalsIgnoreCase("SITE_VISIT")
					|| res.equalsIgnoreCase("SITE_VISIT_AFTER_APPROVE")) {
				SmApplicationUtil.siteVisitDataUpdate(getManagedBean());
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_ASSIGNMENT);
				reqDTO.setSvTask(getManagedBean().getSvTask());

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
			}
			if (res.equalsIgnoreCase("Reject")) {

				if (getManagedBean().isPoUser()) {
					setApplPmStatus(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER);
				} else if (getManagedBean().isApproverUser()) {
					setApplPmStatus(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER);
				}
				if (!getManagedBean()
						.getSmAppl()
						.getApplType()
						.equalsIgnoreCase(
								ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
					setSmApplRejectionReasons();
					reqDTO.setRejectionReasons(getManagedBean().getReasonDesc());
				}
			}
			if (res.equalsIgnoreCase("Approve")) {
				if (getManagedBean()
						.getSmAppl()
						.getApplType()
						.equalsIgnoreCase(
								ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
					setApplPmStatus(SupplierManagementConst.PM_STATUS_APPROVED_BY_APPROVER);
				} else {
					if (pmStatId != null
							&& !getManagedBean()
									.getSmAppl()
									.getApplType()
									.equalsIgnoreCase(
											ParameterConst.NEW_SM_APPLN_FOR_UPDATE)
							&& !getManagedBean()
									.getSmAppl()
									.getApplType()
									.equalsIgnoreCase(
											ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)) {
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
											getManagedBean().getSmAppl()
													.getPmRegStatus()
													.isBumiAppl());
								}
							}
						}
					}
					reqDTO.setPaymentReqd(setPaymentMode());
				}
				for (SmSupplierCategory cat : getManagedBean().getSmAppl()
						.getSmSupplierCategories()) {
					if (cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
						if ((getManagedBean().isPoUser()
								&& cat.getPoDecFlg() != null && cat
								.getPoDecFlg().compareTo(new Integer(1)) == 0)
								|| (getManagedBean().isApproverUser()
										&& cat.getApprvrDecFlg() != null && cat
										.getApprvrDecFlg().compareTo(
												new Integer(1)) == 0)) {
							cat.setRecordStatusFlg(SupplierManagementConst.APPROVED_RECORD_STATUS);
						} else {
							cat.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						}

					}
				}
				if (getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
						|| getManagedBean()
								.getSmAppl()
								.getApplType()
								.equalsIgnoreCase(
										ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
					reqDTO.setBusinessRegNo(getManagedBean().getSmSupplier()
							.getRegNo());
					reqDTO.setCompanyName(getManagedBean().getSmSupplier()
							.getCompanyName());
					reqDTO.setPaymentMethods(" --- TO BE CONFIRMED ON THE PAYMENT LIST DETAILS ---");
					reqDTO.setCategoryDescription(SmApplicationUtil
							.retriveCategoryMessageForNotification(getManagedBean()));
					reqDTO.setNotifLink("<a href=https://www.google.com.my/>"
							+ " Click Here " + " </a>");

				}

			}

			log.info("Before final decision ---"
					+ getManagedBean().getSmAppl().getPmStatus().getStatusId());
			validSiteVisit = testSmAppl(getManagedBean().getSmAppl()
					.getApplType(),
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC, "", true);
			if (res.equalsIgnoreCase("Approve")) {
				// validSiteVisit = updateSmSupplierInfo(getManagedBean()
				// .getSmAppl().getApplType());
				// sendPersonnelActivationEmail();
			}
			if (validSiteVisit) {
				String msgConst = "";
				if (getManagedBean().isPoUser()
						|| getManagedBean().isApproverUser()) {
					log.info(" **** Final Decision from PO ****");
					if (res.equalsIgnoreCase("Approve")) {
						msgConst = MessageConst.INFO_700007;
					} else if (res.equalsIgnoreCase("Reject")) {
						// SmEmailNotificationHelper
						// .sendApplicaitonRejectNotifMail(
						// getManagedBean().getSmAppl(),
						// new FacesScopes().getUserSessionBean()
						// .getUserId(), getManagedBean()
						// .getReasonDesc());
						if (getManagedBean().getBpmTaskName() != null
								&& getManagedBean()
										.getBpmTaskName()
										.equalsIgnoreCase(
												SupplierManagementConst.BPM_TASK_REVIEW_SITE_VISIT_AFTER_APPROVE)) {
							reqDTO.setLastApprovedAppl(SmApplicationUtil
									.getLastApprovedApplictionForSupplier(getManagedBean()));
						}
						msgConst = MessageConst.INFO_700008;
					} else if (res.equalsIgnoreCase("Escalate")) {
						msgConst = MessageConst.INFO_700014;
					} else if (res.equalsIgnoreCase("Inquiry")) {
						msgConst = MessageConst.INFO_700016;
					} else if (res.equalsIgnoreCase("SITE_VISIT")) {
						msgConst = MessageConst.INFO_700015;
					} else if (res.equalsIgnoreCase("SITE_VISIT_AFTER_APPROVE")) {
						msgConst = MessageConst.INFO_700017;
					}
					reqDTO.setPoOrApproverDecision(res);
					reqDTO.setSmAppl(this.getManagedBean().getSmAppl());

					FacesScopes fs = new FacesScopes();
					reqDTO.setServerName(fs.getRequest().getServerName());
					reqDTO.setServerPort(fs.getRequest().getServerPort());

					reqSO.submitPOorApproverReviewToBPM(reqDTO);
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
																			SmApplicationUtil
																					.setParameterConst(getManagedBean()
																							.getSmAppl()
																							.getApplType())),
																	getManagedBean()
																			.getSmAppl()
																			.getApplNo() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
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
			throw new EpException(MessageConst.ERROR_722519);
		} finally {
			reqSO = null;
		}
		return retMsg;
	}

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

	private boolean setPaymentMode() {
		boolean payReq = false;
		if (getManagedBean().getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				|| getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			payReq = true;
		} else {
			for (SmPersonnel per : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (per.getAuthorizedFlg() == 1 && per.getSoftcertFlg() == 2) {
					payReq = true;
					break;
				}

			}
		}
		return payReq;
	}

	public void changeVerifyAll(Boolean isVerifiedAll) {
		log.info(" **** Enter to  verified all ***** " + isVerifiedAll);
		SmApplSectionReview sec = new SmApplSectionReview();

		if (getManagedBean().isPoUser()) {
			sec = getManagedBean().getPoSecReview();
			getManagedBean().setPoSecReview(
					SmApplicationUtil.setVerificationValues(sec, isVerifiedAll,
							getManagedBean().getMaxReviewCount()));
		} else if (getManagedBean().isApproverUser()) {
			sec = getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					SmApplicationUtil.setVerificationValues(sec, isVerifiedAll,
							getManagedBean().getMaxReviewCount()));
		}

	}

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

	private void saveSmRemarks(SmRemark remarks) {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setSmRemark(remarks);
		ISmRemarksSO reqSO = (ISmRemarksSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISmRemarksSO.class);
		reqSO.saveSmRemarks(reqDTO);

	}

	private void saveSmCancellationAttachment(SmAttachment attach) {
		SmAttachmentReqDTO reqDTO = new SmAttachmentReqDTO();
		reqDTO.setAttachment(attach);
		ISmAttachmentSO reqSO = (ISmAttachmentSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISmAttachmentSO.class);
		reqSO.updateSmAttachment(reqDTO);

	}

	private DocumentResDTO retrieveSupportingDocumentList(DocumentReqDTO reqDTO) {
		DocumentResDTO resDto = new DocumentResDTO();
		IDocumentSO reqSO = (IDocumentSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDocumentSO.class);
		resDto = reqSO.retrieveSupportingDocumentList(reqDTO);
		return resDto;

	}

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
				// getManagedBean().getSelectedBank().setSmSupplierBranch(
				// new SmSupplierBranch());
			}
		}
		log.info("****** After getting the fin org list *******"
				+ getManagedBean().getFinOrgLs().size());
	}

	public void assignSelectedSupportDoc(SmApplSupportDoc doc) {
		log.info("****** Assign Supporting Doc *******");
		getManagedBean().setSupportDoc(doc);
	}

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
					doc.getSmApplDocFiles().get(0)
							.setFileName(event.getFile().getFileName());
					String filePath = Calendar.getInstance().get(Calendar.YEAR)
							+ "/SM/" + getManagedBean().getSmAppl().getApplNo()
							+ "/SD/";
					doc.getSmApplDocFiles().get(0).setFilePath(filePath);
					commonUploadFile(event, filePath);
				}
			}
		}
	}

	public void handleSupDocFileAttachment(FileUploadEvent event) {
		log.info("********handleFileAttachment********");
		if (event.getFile() != null) {
			log.info("***** File attached *******"
					+ event.getFile().getFileName());
			if (getManagedBean().getSmAppl().getSmApplSupportDocs()
					.contains(getManagedBean().getSupportDoc())) {
				log.info("******* Contains selected file ****");
				SmApplSupportDoc doc = getManagedBean().getSupportDoc();
				doc.getSmApplDocFiles().get(0)
						.setFileName(event.getFile().getFileName());
				getManagedBean()
						.getSmAppl()
						.getSmApplSupportDocs()
						.set(getManagedBean().getSmAppl()
								.getSmApplSupportDocs()
								.indexOf(getManagedBean().getSupportDoc()),
								getManagedBean().getSupportDoc());
				String filePath = Calendar.getInstance().get(Calendar.YEAR)
						+ "/SM/" + getManagedBean().getSmAppl().getApplNo()
						+ "/SD/";
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

	public void handleCancelMofRegFileAttachment(FileUploadEvent event) {
		log.info("********handleFileAttachment********");
		if (event.getFile() != null) {
			log.info("***** File attached *******"
					+ event.getFile().getFileName());
			getManagedBean().getCancellationAttachment().setFileName(
					event.getFile().getFileName());

			String filePath = Calendar.getInstance().get(Calendar.YEAR)
					+ "/SM/" + getManagedBean().getSmAppl().getApplNo()
					+ "/SD/";
			getManagedBean().getCancellationAttachment().setFilePath(filePath);
			commonUploadFile(event, filePath);

		}
	}

	// private PmReasonDesc retrieveReasonDescriptionByReasonId(Long reasonId) {
	// IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance()
	// .getServiceObject("pm", IReasonSO.class);
	// ReasonReqDTO reqDTO = new ReasonReqDTO();
	// reqDTO.setReasonId(reasonId);
	// reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
	// ReasonResDTO resDTO = reqSO.retrieveReasonDescById(reqDTO);
	// if (resDTO.getReasonDesc() != null) {
	// log.info(" *** Reject Description ****** "
	// + resDTO.getReasonDesc().getReasonDesc());
	// }
	// return resDTO.getReasonDesc();
	// }

	private List<PmState> retrievePmStateList(long countryId) {
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		return resDTO.getStateList();
	}

	private List<PmDistrict> retrievePmDistrictList(long stateId) {
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(stateId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
		return resDTO.getDistrictList();
	}

	private List<PmCity> retrievePmCityList(long districtId) {
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		return resDTO.getCityList();
	}

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
			// MofRegistrationResDTO resDTO = mofRegistrationSO
			// .retrieveSupplierProfile(reqDto);
			return resDTO;

		} catch (Exception e) {
			log.info("**** THIS IS THE EXCEPTION IN CLONE APPL ****");
			e.printStackTrace();
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}
	}

	private int assignMaxReviewCount(String appType, String applCatType,
			String supplierType, String businessType) {
		return SmApplicationUtil.assignMaxReviewCount(appType, applCatType,
				supplierType, businessType);
	}

	public void populateStatesByCountry(long coutryId, String panelName,
			PmState state, PmDistrict district, PmCity city) {
		try {
			List<PmState> stateLs = this.retrievePmStateList(coutryId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				this.getManagedBean().setBasicCompanyStates(stateLs);
				this.getManagedBean().setBasicCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBasicCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				this.getManagedBean().setBasicOffFacStates(stateLs);
				this.getManagedBean().setBasicOffFacDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBasicOffFacCities(
						new ArrayList<PmCity>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				this.getManagedBean().setBranchCompanyStates(stateLs);
				this.getManagedBean().setBranchCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBranchCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				this.getManagedBean().setAssocCompanyStates(stateLs);
				this.getManagedBean().setAssocCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setAssocCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				this.getManagedBean().setEquiCompanyStates(stateLs);
				this.getManagedBean().setEquiCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setEquiCompanyCities(
						new ArrayList<PmCity>());
			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				this.getManagedBean().setFactoryFacilityStates(stateLs);
				this.getManagedBean().setFactoryFacilityDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setFactoryFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				this.getManagedBean().setDockFacilityStates(stateLs);
				this.getManagedBean().setDockFacilityDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setDockFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				this.getManagedBean().setWorkShopFacilityStates(stateLs);
				this.getManagedBean().setWorkShopFacilityDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setWorkShopFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				this.getManagedBean().setStoreFacilityStates(stateLs);
				this.getManagedBean().setStoreFacilityDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setStoreFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				this.getManagedBean().setShrHldrPermStates(stateLs);
				this.getManagedBean().setShrHldrPermDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setShrHldrPermCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				this.getManagedBean().setShrHldrResdStates(stateLs);
				this.getManagedBean().setShrHldrResdDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setShrHldrResdCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				this.getManagedBean().setShrHldrOffStates(stateLs);
				this.getManagedBean().setShrHldrOffDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setShrHldrOffCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				this.getManagedBean().setShrHldrMailStates(stateLs);
				this.getManagedBean().setShrHldrMailDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setShrHldrMailCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				this.getManagedBean().setBankStates(stateLs);
				this.getManagedBean().setBankDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBankCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				this.getManagedBean().setPersnlPermStates(stateLs);
				this.getManagedBean().setPersnlPermDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setPersnlPermCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				this.getManagedBean().setPersnlResdStates(stateLs);
				this.getManagedBean().setPersnlResdDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setPersnlResdCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				this.getManagedBean().setPersnlOffStates(stateLs);
				this.getManagedBean().setPersnlOffDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setPersnlOffCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				this.getManagedBean().setPersnlMailStates(stateLs);
				this.getManagedBean().setPersnlMailDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setPersnlMailCities(
						new ArrayList<PmCity>());
				break;
			default:
				log.info("**** This is an unknown panel ***** ");
				break;
			}
			resetState(state);
			resetDistrict(district);
			resetCity(city);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateDistrictsByState(long stateId, String panelName,
			PmDistrict district, PmCity city) {
		try {
			List<PmDistrict> distLs = this.retrievePmDistrictList(stateId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				this.getManagedBean().setBasicCompanyDistricts(distLs);
				this.getManagedBean().setBasicCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				this.getManagedBean().setBasicOffFacDistricts(distLs);
				this.getManagedBean().setBasicOffFacCities(
						new ArrayList<PmCity>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				this.getManagedBean().setBranchCompanyDistricts(distLs);
				this.getManagedBean().setBranchCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				this.getManagedBean().setAssocCompanyDistricts(distLs);
				this.getManagedBean().setAssocCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				this.getManagedBean().setEquiCompanyDistricts(distLs);
				this.getManagedBean().setEquiCompanyCities(
						new ArrayList<PmCity>());
				break;

			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				this.getManagedBean().setFactoryFacilityDistricts(distLs);
				this.getManagedBean().setFactoryFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				this.getManagedBean().setDockFacilityDistricts(distLs);
				this.getManagedBean().setDockFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				this.getManagedBean().setWorkShopFacilityDistricts(distLs);
				this.getManagedBean().setWorkShopFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				this.getManagedBean().setStoreFacilityDistricts(distLs);
				this.getManagedBean().setStoreFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				this.getManagedBean().setShrHldrPermDistricts(distLs);
				this.getManagedBean().setShrHldrPermCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				this.getManagedBean().setShrHldrResdDistricts(distLs);
				this.getManagedBean().setShrHldrResdCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				this.getManagedBean().setShrHldrOffDistricts(distLs);
				this.getManagedBean().setShrHldrOffCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				this.getManagedBean().setShrHldrMailDistricts(distLs);
				this.getManagedBean().setShrHldrMailCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				this.getManagedBean().setPersnlPermDistricts(distLs);
				this.getManagedBean().setPersnlPermCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				this.getManagedBean().setPersnlResdDistricts(distLs);
				this.getManagedBean().setPersnlResdCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				this.getManagedBean().setPersnlOffDistricts(distLs);
				this.getManagedBean().setPersnlOffCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				this.getManagedBean().setPersnlMailDistricts(distLs);
				this.getManagedBean().setPersnlMailCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				this.getManagedBean().setBankDistricts(distLs);
				this.getManagedBean().setBankCities(new ArrayList<PmCity>());
				break;
			default:
				log.info("**** This is an unknown panel ***** ");
				break;
			}
			resetDistrict(district);
			resetCity(city);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateCitiesByDistrict(long districtId, String panelName,
			PmCity city) {
		try {
			List<PmCity> cityLs = this.retrievePmCityList(districtId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				this.getManagedBean().setBasicCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				this.getManagedBean().setBasicOffFacCities(cityLs);
				break;
			case SupplierManagementConst.SM_BRANCH_PANEL:
				this.getManagedBean().setBranchCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				this.getManagedBean().setAssocCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				this.getManagedBean().setEquiCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				this.getManagedBean().setFactoryFacilityCities(cityLs);
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				this.getManagedBean().setDockFacilityCities(cityLs);
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				this.getManagedBean().setWorkShopFacilityCities(cityLs);
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				this.getManagedBean().setStoreFacilityCities(cityLs);
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				this.getManagedBean().setShrHldrPermCities(cityLs);
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				this.getManagedBean().setShrHldrResdCities(cityLs);
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				this.getManagedBean().setShrHldrOffCities(cityLs);
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				this.getManagedBean().setShrHldrMailCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				this.getManagedBean().setPersnlPermCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				this.getManagedBean().setPersnlResdCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				this.getManagedBean().setPersnlOffCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				this.getManagedBean().setPersnlMailCities(cityLs);
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				this.getManagedBean().setBankCities(cityLs);
				break;
			default:
				log.info("**** This is an unknown panel ***** ");
				break;
			}
			resetCity(city);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	private void resetDistrict(PmDistrict dist) {
		dist.setDistrictId(null);
	}

	private void resetCity(PmCity city) {
		city.setCityId(null);
	}

	private void resetState(PmState state) {
		state.setStateId(null);
	}

	private void resetChildCategory(PmCategoryL2 catL2) {
		catL2.setCategoryL2Id(null);

	}

	private void resetSubChildCategory(PmCategoryL3 catL3) {
		catL3.setCategoryL3Id(null);

	}

	private void resetChildCatList() {
		this.getManagedBean().setSupplierChildCatList(
				new ArrayList<PmCategoryL2>());
		this.getManagedBean().setSupplierSubChildCatList(
				new ArrayList<PmCategoryL3>());
	}

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
				if (!smFac.getOwnershipType().equalsIgnoreCase("O")) {
					getManagedBean().setBasicOffFacStates(
							retrievePmStateList(smFac.getSmAddress()
									.getPmCountry().getCountryId()));
				} else {
					if (smFac.getSmAddress() != null
							&& smFac.getSmAddress().getAddressId() != null) {
						smFac.setSmAddress(setDefaultBaseOfficeAddress(smFac
								.getSmAddress()));
					} else {
						smFac.setSmAddress(setDefaultBaseOfficeAddress(new SmAddress()));
					}
				}
				log.info("*********Setting Basic company value **********");
				smFac.setFacilityName(getManagedBean().getSmAppl()
						.getSmCompanyBasic().getCompanyName());
				this.getManagedBean().setBasicCompanyFacility(smFac);
				if (smFac.getSmAddress() != null
						&& smFac.getSmAddress().getPmCountry() != null
						&& smFac.getSmAddress().getPmState() != null
						&& smFac.getSmAddress().getPmCity() != null) {
					assignStateDistAndCityForSelVal(
							smFac.getSmAddress().getPmCountry().getCountryId(),
							smFac.getSmAddress().getPmState().getStateId(),
							smFac.getSmAddress().getPmDistrict()
									.getDistrictId(),
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

	private void setApplicationShareHolders(List<SmPersonnel> perList) {
		getManagedBean().setSmapplShareHolders(new ArrayList<SmPersonnel>());

		for (SmPersonnel per : perList) {

			if (per.isEquityOwner()) {
				getManagedBean().getSmapplShareHolders().add(per);
			}
		}

	}

	private void setDefaultPersonnelValues(SmPersonnel shr) {
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

	public void testPrinter() {
		log.info("********* Test printer actions ********  ");
	}

	public void calculateShareHolderPercent() {
		log.info("********* calculateShareHolderPercent ********  ");
		SmApplicationUtil.calculateShareHolderPercent(getManagedBean());
	}

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
			// ne.printStackTrace();
			throw new EpException(MessageConst.ERROR_722522);
		}
	}

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

	private void calcPersonnelListCount() {
		log.info("*** Start calcPersonnelListCount**** ");
		SmPersonnleCalcVO calcVO = SmCommonUtil.calcPersonnelListCount(
				getManagedBean().getSmAppl().getSmPersonnels(),
				getManagedBean().getSmAppl().getSmStaffTotals(),
				getManagedBean().getSmAppl().getSupplierType());
		getManagedBean().setPersonCalc(calcVO);

		// not sure for what.disabled temporary
		// int countCheck =
		// SmApplicationUtil.validatePersonLimit(getManagedBean()
		// .getSmAppl().getSmSupplier().getSupplierId(), getManagedBean()
		// .getSmAppl().getApplType(), getManagedBean().getSmAppl()
		// .getApplId());
		//
		// getManagedBean().setPersonnelCountCheck(countCheck);

		// set already reach valid max authorized personnel and justification
		SmAuthorizedPersonnelCountVO countVO = SmApplicationUtil
				.getSmAuthorizedPersonnelCountVO(getManagedBean()
						.getSmSupplier().getSupplierId(), getManagedBean()
						.getSmAppl().getApplType(), getManagedBean()
						.getSmAppl().getApplId(), false);
		getManagedBean().setValidSmUserLimit(
				countVO.isReachAuthorizedPersonnelLimit());

		log.info("*** END calcPersonnelListCount**** ");
	}

	private void calculateBumiPercentageTotals() {
		SmPersonnleCalcVO calcVO = SmCommonUtil.calculateBumiPercentageTotals(
				getManagedBean().getPersonCalc(), getManagedBean().getSmAppl()
						.getSmPersonnels());
		getManagedBean().setPersonCalc(calcVO);
	}

	private void setNavigationForResubmitAppln(String applType) {
		SmApplicationUtil.setNavigationForResubmitAppln(applType);
	}

	private void manageEquityLimit(SmPersonnel personnel) {
		getManagedBean().setValidEquityLimit(
				SmApplicationUtil.manageEquityLimit(personnel, getManagedBean()
						.getBusinessTypeVal(), getManagedBean()
						.getSmapplShareHolders()));
	}

	private void assignSupplierBusinessType(SmSupplier sup) {
		getManagedBean().setBusinessTypeVal(
				SmApplicationUtil.assignSupplierBusinessType(sup));

	}

	private void setApplPmStatus(String statusId) {
		StatusResDTO resDTO = SmApplicationUtil.setApplPmStatus(statusId);
		if (resDTO != null && resDTO.getPmStatus() != null) {
			getManagedBean().getSmAppl().setPmStatus(resDTO.getPmStatus());
		}
	}

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

	private void assignValReqdToTrue() {
		getManagedBean().setShowParentMsg(false);
		getManagedBean().setValueReqd(true);
	}

	private void assignValReqdToFalse() {
		getManagedBean().setShowParentMsg(true);
		getManagedBean().setValueReqd(false);
	}

	private void assignStateDistAndCityForSelVal(Long countryId, Long stateID,
			Long districtId, String panelName) {
		populateStatesByCountry(countryId, panelName, new PmState(),
				new PmDistrict(), new PmCity());
		populateDistrictsByState(stateID, panelName, new PmDistrict(),
				new PmCity());
		populateCitiesByDistrict(districtId, panelName, new PmCity());
	}

	// SM Address Clone code start

	// SM Address Clone code end
	private boolean validateSupportingDocs() {
		return SmApplicationUtil.validateSupportingDocs(getManagedBean()
				.getSmAppl().getSmApplSupportDocs());
	}

	public void retrieveBusinessTypeList() {
		SelectItem[] selectItems = getDropdownCodeValues(SupplierManagementConst.SUPPLIER_BUSINESS_TYPE);
		this.getManagedBean().setBusinessTypeLs(selectItems);
		initTypeOfBusiness();
	}

	private void validatePaidCaptialAmt(boolean setNavigationCurrentPage) {
		SmApplicationUtil.validatePaidCaptialAmt(setNavigationCurrentPage,
				getManagedBean(), currentTab);
	}

	public void managePersonnelRoles() {
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

	public void downloadSelectedSuppDoc(String fileName, String filePath) {
		String finalFilePath = filePath + fileName;
		try {
			commonDownload(finalFilePath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finalFilePath = null;
	}

	public void downloadSignatureSuppDoc(String fileName, String filePath) {
		String finalFilePath = filePath;
		try {
			commonDownload(finalFilePath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finalFilePath = null;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public void setRevUrl() {
		FacesContext fCtx = FacesContext.getCurrentInstance();
		PortletRequest req = (PortletRequest) fCtx.getExternalContext()
				.getRequest();
		ThemeDisplay themeDisplay = (ThemeDisplay) req
				.getAttribute(WebKeys.THEME_DISPLAY);
		Layout layout = themeDisplay.getLayout();
		log.info("layout.getFriendlyURL():" + layout.getFriendlyURL());

		String portalUrl = themeDisplay.getURLPortal();
		String currentUrl = themeDisplay.getURLCurrent();
		currentUrl = currentUrl.substring(0,
				currentUrl.indexOf(layout.getFriendlyURL()));

		String url = portalUrl + currentUrl + "/view-revision-application";

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
	}

	public void unSelectAdmin(AjaxBehaviorEvent e) {
		if (getManagedBean().getSelectedShareHolders() != null) {
			Iterator<SmPersonnel> iterator = getManagedBean()
					.getSelectedShareHolders().iterator();
			while (iterator.hasNext()) {
				SmPersonnel personnel = iterator.next();
				if ((personnel.getEpRole() != null && (personnel.getEpRole()
						.equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value()) || personnel
						.getEpRole().equalsIgnoreCase(
								RoleCode.BASIC_SUPPLIER_ADMIN.value())))
						|| (personnel.isEquityOwner() && getManagedBean()
								.isDisAllowOwnerEdit())) {
					iterator.remove();
				}
			}
		}
	}

	public void unSelectApporvedCategories(AjaxBehaviorEvent e) {
		if (getManagedBean().getDeleteNewApplCategoryList() != null) {
			Iterator<SmSupplierCategory> iterator = getManagedBean()
					.getDeleteNewApplCategoryList().iterator();
			while (iterator.hasNext()) {
				SmSupplierCategory cat = iterator.next();
				if (cat.getRecordStatusFlg() != null
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * retrieve identity type from PM
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
					setApplicationCategorySelected();
					assignApplnCategoryType();
				}
			}
		}
	}

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

	private void initBusinessPremise() {
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

		} else {
			getManagedBean().setDisabledHomeOffice(false);
		}
	}

	private void initTypeOfBusiness() {
		List<SelectItem> businessTypeSelectItems = new ArrayList<SelectItem>();
		if (this.getManagedBean().getBusinessTypeLs() != null) {
			businessTypeSelectItems = Arrays.asList(this.getManagedBean()
					.getBusinessTypeLs());
			if (getManagedBean().getSmSupplier() != null) {
				for (SelectItem selectItem : businessTypeSelectItems) {
					if (selectItem.getValue() != null
							&& selectItem.getValue() != null) {
						if (selectItem.getValue().equals(
								getManagedBean().getSmSupplier()
										.getBusinessType())) {
							getManagedBean().setBusinessTypeVal(
									selectItem.getLabel().toString());
							break;
						}
					}
				}
			}
		}
	}

	public void selectAll() {

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

	public void selected(int index) {

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

	public String getBusinessType(String businessType) {
		String retVal = "";
		List<SelectItem> businessTypeItems = new ArrayList<SelectItem>();
		if (StringUtils.isNotBlank(businessType)) {
			if (getManagedBean().getBusinessTypeLs() != null
					&& getManagedBean().getBusinessTypeLs().length > 0) {
				businessTypeItems = Arrays.asList(getManagedBean()
						.getBusinessTypeLs());
				for (SelectItem item : businessTypeItems) {
					if (businessType.equals(item.getValue())) {
						retVal = item.getLabel();
						break;
					}
				}
			}
		}
		return retVal;
	}

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

	public boolean isPersonnelViewOnly(SmPersonnel smPersonnel) {
		boolean retVal = false;
		if ((smPersonnel.isEquityOwner() || smPersonnel.isDirector())
				&& getManagedBean().isDisAllowOwnerEdit()) {
			retVal = true;
		}
		return retVal;
	}

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
	// private void generateSuppDocPdfFile(VelocityContext context)
	// throws Exception {
	// String appNo = getManagedBean().getSmAppl().getApplNo();
	// String baseDir = File.separator + "docuRepo" + File.separator + "dev"
	// + File.separator + "ngep" + File.separator;
	// String tempDir = baseDir + File.separator
	// + Calendar.getInstance().get(Calendar.YEAR) + File.separator
	// + SupplierManagementConst.FOLDER_NAME_SM + File.separator
	// + appNo + File.separator
	// + SupplierManagementConst.FOLDER_NAME_SUPPDOC;
	//
	// File theDir = new File(tempDir);
	// if (!theDir.exists()) {
	// theDir.mkdir();
	// }
	//
	// File newFile = new File(tempDir+ File.separator
	// + SupplierManagementConst.OFFLINE_SUPP_DOC_PRINT_LIST_FILENAME);
	// if(!newFile.exists()){
	// newFile.createNewFile();
	// }
	//
	// // DocumentGenerator.getInstance().generatePdfFromVelocity(
	// // SupplierManagementConst.DOCPATH_OFFLINE_SUPP_DOC_PRINT_LIST,
	// // context, new FileOutputStream(tempDir));
	// }
}
