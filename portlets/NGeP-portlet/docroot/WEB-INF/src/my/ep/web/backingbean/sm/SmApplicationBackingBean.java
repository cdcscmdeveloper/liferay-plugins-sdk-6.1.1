package my.ep.web.backingbean.sm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.reviewApplication.IReviewApplicationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.certbody.CertBodyResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmCompanyExperienceCat;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
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
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvCategory;
import my.ep.persistence.entity.sm.SmSvOffice;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.ReasonCategory;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.email.SmEmailNotificationHelper;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "backingBean")
@RequestScoped
public class SmApplicationBackingBean extends
		BaseBackingBean<SmApplicationManagedBean> {

	public static Logger log = Logger.getLogger(SmApplicationBackingBean.class);

	private int currentTab;
	private int outputTab;

	public SmApplicationBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

	}

	public void prerenderApplicationForResubmission() {
		HttpServletRequest originalServletRequest = new FacesScopes()
				.getOriginalServletRequest();
		String applId = originalServletRequest.getParameter("apId");

		log.info("application id : " + applId);

		SmAppl appl = null;
		SmSupplier supplierProfile = null;

		try {
			if (applId != null) {
				MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
				mofResDto = retriveApplicationById(Integer.parseInt(applId));
				appl = mofResDto.getSmAppl();
				supplierProfile = mofResDto.getSmSupplier();
			}
		} finally {
		}

		if (appl != null && supplierProfile != null) {
			System.out.println(" *** The application type is ******* "
					+ appl.getApplType());
			proceedToApplication(appl.getApplType(), Boolean.TRUE, appl,
					supplierProfile);
			System.out.println(" ****** After proceed *****"
					+ getManagedBean().getSmAppl().getApplType());
			getManagedBean().getSmAppl().setResubmit(true);
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

	public String proceedToApplication(String applicationType, boolean fromBpm,
			SmAppl appl, SmSupplier supp) {
		System.out
				.println("**** ENTER Create Application Type Of  *************"
						+ applicationType + "Application type = >>"
						+ getManagedBean().getSmAppl().getSupplierType());
		if (!applicationType.equalsIgnoreCase("C")) {
			retrievePmCountryList();
			retrievePmCertBodyList();
			retrievePmProfBodyList();
			retrieveParentCategoryList();
			retrieveUomList();
			retrieveRejectReasonList();
			retrieveRegistrationStatusList();
		}
		SmAppl smAppl = new SmAppl();
		SmSupplier smApplSupplier = new SmSupplier();
		try {
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			MofRegistrationResDTO resDto = new MofRegistrationResDTO();

			if (!fromBpm) {
				SmSupplier currSupp = getCurrentSupplier();

				boolean cannotProceedAlertMsg = false;

				if (applicationType.equalsIgnoreCase("N")) {
					smAppl.setSupplierType(getManagedBean()
							.getNewSupplierType());
				} else if (applicationType.equalsIgnoreCase("B")) {

				} else if (applicationType.equalsIgnoreCase("R")
						|| applicationType.equalsIgnoreCase("A")
						|| applicationType.equalsIgnoreCase("B")) {
					cannotProceedAlertMsg = validateBeforeProceed(
							currSupp.getSupplierId(), applicationType);
				}
				if (cannotProceedAlertMsg) {
					return setExceptionNavigationForAppl(applicationType);
				} else {
					System.out.println("****** Before Clone supp id *****"
							+ currSupp.getSupplierId());
					resDto = cloneSmApplicationDraft(currSupp.getSupplierId(),
							applicationType, smAppl.getSupplierType());
					if (resDto.isErrorCaued()) {
						return setExceptionNavigationForAppl(applicationType);

					}
					smApplSupplier = resDto.getSmSupplier();
					System.out.println(" *** After clone Supplier Id ******* "
							+ smApplSupplier.getSupplierId());
					resDto = retriveLatestDraftApplication(smApplSupplier,
							applicationType);
					if (resDto.isErrorCaued()) {
						return setExceptionNavigationForAppl(applicationType);
					}

					smAppl = resDto.getSmAppl();
					System.out
							.println("****** After retrieve the appl is ********"
									+ smAppl.getApplId()
									+ " *** Num ***"
									+ smAppl.getApplNo());
					// TODO Set default not null values -- End insert appl ---
					// TODo Remove hard code later
					smAppl.setApplCategoryType(null);
				}
			} else {
				System.out
						.println("******* This is PO or Approver from BPM ************");
				smApplSupplier = supp;
				smAppl = appl;
			}

			getManagedBean().setSmSupplier(smApplSupplier);

			if (!fromBpm) {
				// TODO remove this hard coded data and make dynamic later
				getManagedBean().getSmAppl().setSupportingDocMode("O");
			}

			assignSupplierBusinessType(getManagedBean().getSmSupplier());

			if (getManagedBean().isApproverUser()
					|| getManagedBean().isPoUser()) {
				System.out.println("**** LOGIN IS PO or APPROVER *******"
						+ smAppl.getApplCategoryType());
				// smAppl.setSupportingDocMode("O");
				// smAppl.setSupplierType(SupplierManagementConst.STYPE_CONSULTANT);

				if (smAppl.getSmCompanyBasic() == null) {
					System.out.println(" **** COMPANY BAsic cannot be null");
					smAppl.setSmCompanyBasic(new SmCompanyBasic());
				}
				// smAppl.setApplCategoryType("S");
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
					SmAddress addr = setDefaultAddr();
					comAdd.setSmAddress(addr);
					List<SmCompanyAddress> addrs = new ArrayList<SmCompanyAddress>();
					addrs.add(comAdd);
					smAppl.getSmCompanyBasic().setSmCompanyAddresses(addrs);
				}
			} else {
				System.out
						.println("******** My Company Basic is null ********");
			}

			getManagedBean().setSmAppl(smAppl);
			if (getManagedBean().getSmAppl().getSmStaffTotals().isEmpty()) {
				System.out.println("*** Empty list of staff *****"
						+ smAppl.getSupplierType());
				getManagedBean().getSmAppl().setSmStaffTotals(
						getStaffList(smAppl.getSupplierType()));
			}
			// getManagedBean().setSmAppl(smAppl);

			getManagedBean().setTabActiveIndex(0);
			if (!fromBpm) {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_DECLARATION);
			} else {
				setPmStatusDesc(smAppl.getPmStatus().getStatusId().toString());
			}
			if (fromBpm
					&& (getManagedBean().isPoUser() || getManagedBean()
							.isApproverUser())) {
				getManagedBean()
						.setMaxReviewCount(
								assignMaxReviewCount(applicationType,
										getManagedBean().getSmAppl()
												.getApplCategoryType(),
										getManagedBean().getSmAppl()
												.getSupplierType()));

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

				if (applicationType.equalsIgnoreCase("C")) {
					setSupplierCancellationRemarks(applicationType,
							getManagedBean().getSmAppl().getApplId());
				}
			}

			calcPersonnelListCount();
			calculateCapital();
			calculateBumiPercentageTotals();
			updateSmAppl(applicationType, "", "");
			if (smAppl.getSmFacilities() != null
					&& smAppl.getSmFacilities().isEmpty()
					&& !smAppl.getApplType().equalsIgnoreCase(
							ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
				System.out.println("*** Empty Facility******");
				SmFacility fac = new SmFacility();
				if (smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
					// fac.setOwnerName("Sample");
					fac.setFacilityType("H");
				}
				SmAddress addr = setDefaultAddr();
				fac.setSmAppl(smAppl);
				fac.setSmAddress(addr);
				fac.setRevNo(1);
				smAppl.getSmFacilities().add(fac);
				this.getManagedBean().setBasicCompanyFacility(fac);
				getManagedBean()
						.setBasicOffFacStates(
								retrievePmStateList(addr.getPmCountry()
										.getCountryId()));

			} else {
				setApplicationFailities(smAppl.getSmFacilities());
				// TODO
			}
			getManagedBean().setCurrentPageType(
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC);
			return setNavigationForAppl(applicationType);

			/*
			 * } catch (Exception e) {
			 * System.out.println(" **** Unknown Expection ******* ");
			 * e.printStackTrace(); return
			 * setExceptionNavigationForAppl(applicationType);
			 */
		} finally {
			// mofRegistrationSO = null;
		}
	}

	private void setSupplierCancellationRemarks(String docType, Long docId) {
		SmRemaksResDTO resDTO = getRemarksByDoc(docType, docId);
		StringBuffer sb = new StringBuffer();
		if (resDTO != null && resDTO.getRemarksList() != null
				&& !resDTO.getRemarksList().isEmpty()) {

			for (SmRemark rem : resDTO.getRemarksList()) {
				sb.append(rem.getRemark()).append("\n");
			}
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

	private boolean validateBeforeProceed(Long supplierId,
			String applicationType) {

		boolean result = false;
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);

		MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
		MofRegistrationResDTO resDto = new MofRegistrationResDTO();

		reqDto.setSupplierId(supplierId);
		resDto = mofRegistrationSO.retrieveSupplierProfile(reqDto);

		if (resDto.getSmSupplier() != null) {

			SmSupplier smSupplier = resDto.getSmSupplier();
			List<SmMofAccount> accounts = smSupplier.getSmMofAccounts();

			for (SmMofAccount account : accounts) {
				if (account.getSupplierType().equalsIgnoreCase(
						smSupplier.getSmAppl().getSupplierType())) {

					long diff = account.getExpDate().getTime()
							- (new Date()).getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);

					if (applicationType.equalsIgnoreCase("R")) {

						if (diffDays > 90) {
							result = true;
							throw new EpException(MessageConst.ERROR_722501);
						} else if (diffDays < -365) {
							result = true;
							throw new EpException(MessageConst.ERROR_722502);
						}
					} else if (applicationType.equalsIgnoreCase("A")
							&& diffDays < 90) {
						result = true;
						throw new EpException(MessageConst.ERROR_722503);
					} else if (applicationType.equalsIgnoreCase("B")
							&& smSupplier.isBumi()) {
						result = true;
						throw new EpException(MessageConst.ERROR_722504);
					}
				}
			}
		}
		return result;
	}

	public String deletePendingMofApplication() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {

			// SmSupplier smSupplier = retriveSupplierById((long) 3)
			// .getSmSupplier();

			SmSupplier smSupplier = getCurrentSupplier();
			System.out.println("****** Testing the delete pending *********"
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

				SmAppl smAppl = defaultSmAppl(resDTO.getSmAppl());
				getManagedBean().setSmAppl(smAppl);
				if (getManagedBean().getSmAppl().getPmStatus() != null) {
					setPmStatusDesc(getManagedBean().getSmAppl().getPmStatus()
							.getStatusId().toString());
				}
			}

			return SupplierManagementConst.PROCEED_DEL_PENDING_MOF_APPLN;

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_722505);
			// return SupplierManagementConst.DEFAULT_DEL_PENDING_MOF_APPLN;
		} finally {
			mofRegistrationSO = null;
		}
	}

	public void updateSmAppl() throws Exception {
		try {
			System.out.println("**** ENTER ACTION METHOD BEAN *************"
					+ currentTab + "  " + outputTab);

			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				System.out.println(" ****** REMAIN PREV VALUE ********");
			}
			getManagedBean().setTabActiveIndex(outputTab);
			System.out.println("Value ----"
					+ getManagedBean().getSmAppl().getSupportingDocMode());
			// return SupplierManagementConst.PROCEED_MOF_REG;

		} catch (Exception e) {
			// System.out.println("this is error");
			// e.printStackTrace();
			getManagedBean().setTabActiveIndex(currentTab);
			throw new EpException(MessageConst.ERROR_722505);
			// return SupplierManagementConst.PROCEED_MOF_REG;
		}

	}

	public void updateSmApplBpmSubmit(String applType, String currentPageType,
			String outputPageType, boolean isBpmSubmit) {
		testUpdateSmAppl(applType, currentPageType, outputPageType, isBpmSubmit);
	}

	public void updateSmAppl(String applType, String currentPageType,
			String outputPageType) {
		System.out.println("******* Current Page Before **** "
				+ currentPageType);
		System.out
				.println("******* Current page before in managed bean ********"
						+ getManagedBean().getCurrentPageType());
		System.out.println("********** Output Page before ***********"
				+ outputPageType);
		testUpdateSmAppl(applType, currentPageType, outputPageType, false);
		System.out
				.println("******* Current page AFTER in managed bean ********"
						+ getManagedBean().getCurrentPageType());
	}

	public boolean testUpdateSmAppl(String applType, String currentPageType,
			String outputPageType, Boolean isBpmSubmit) {
		boolean valid = false;
		boolean isError = false;
		IMofRegistrationSO mofRegistrationSO = null;
		System.out.println(" ***** Business  Type After hard code *********"
				+ getManagedBean().getSmAppl().getSmSupplier()
						.getBusinessType());
		getManagedBean().setTabActiveIndex(outputTab);
		try {

			System.out.println("******* IS PO *******"
					+ getManagedBean().isPoUser());
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
				System.out.println(" ****** REMAIN PREV VALUE ********");
			}
			if (valid) {
				// Save or update to DB
				MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
				reqDto.setSmAppl(this.getManagedBean().getSmAppl());
				MofRegistrationResDTO resDTO = mofRegistrationSO
						.updateApplication(reqDto);
				getManagedBean().setCurrentPageType(outputPageType);
			} else {
				getManagedBean().setTabActiveIndex(currentTab);
				getManagedBean().getSmAppl().setCurrentActiveTab(currentTab);
				getManagedBean().setCurrentPageType(currentPageType);
			}
			// if (valid) {
			// valid = retriveLatestApplAfterUpdate(pageType);
			// }
			isError = valid;
		}
		// catch (Exception e) {
		// // System.out.println("this is error");
		// // e.printStackTrace();
		//
		// getManagedBean().setTabActiveIndex(currentTab);
		// // getManagedBean().getSmAppl().setCurrentActiveTab(currentTab);
		// isError = true;
		// valid = false;
		// retriveLatestApplAfterUpdate(pageType);
		// // return SupplierManagementConst.PROCEED_MOF_REG;
		// isError = false;
		// throw new EpException(MessageConst.ERROR_722505);
		//
		// }
		finally {
			isError = retriveLatestApplAfterUpdate(currentPageType);
			mofRegistrationSO = null;
		}
		return isError;
	}

	private boolean retriveLatestApplAfterUpdate(String currentPageType) {

		IMofRegistrationSO mofRegistrationSO = null;
		mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IMofRegistrationSO.class);
		try {

			MofRegistrationReqDTO finReqDto = new MofRegistrationReqDTO();
			finReqDto.setApplId(getManagedBean().getSmAppl().getApplId());
			System.out.println("********* Final Block Id *****"
					+ finReqDto.getApplId());
			System.out.println("***** Its a valid ************");
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
			System.out
					.println(" ***** Business  Type After in finally block *********"
							+ getManagedBean().getSmAppl().getSmSupplier()
									.getBusinessType());
			// TODO remove the below line if not reqd
			assignSupplierBusinessType(getManagedBean().getSmAppl()
					.getSmSupplier());
			if (getManagedBean().isPoUser()) {
				setDefaultPoReviewSection();
			}
			if (getManagedBean().isApproverUser()) {
				setDefaultApproverReviewSection();
			}

			if (currentPageType
					.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
				System.out.println(" ****8 This is capital ********* ");
				for (int i = 0; i < getManagedBean().getSmAppl()
						.getSmSupplierBranches().size(); i++) {
					SmSupplierBranch br = getManagedBean().getSmAppl()
							.getSmSupplierBranches().get(i);
					if (br.getSmPersonnel() == null) {
						System.out
								.println(" **** This is null personnel ******** ");
						br.setSmPersonnel(new SmPersonnel());
						getManagedBean().getSmAppl().getSmSupplierBranches()
								.set(i, br);

					}
				}
			}
			if (getManagedBean().getSmAppl().getPmStatus() != null) {
				System.out.println("***** After final Retrieve ****"
						+ getManagedBean().getSmAppl().getPmStatus()
								.getStatusId());
			}
			return true;
		} catch (Exception e) {
			getManagedBean().setCurrentPageType(currentPageType);
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_722519);
		} finally {
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

	private void setSmApplRejectionReasons() {
		List<SmApplRejectReason> resList = new ArrayList<SmApplRejectReason>();
		for (Long resId : getManagedBean().getReasonId()) {
			for (PmReasonDesc desc : getManagedBean().getReasonDescList()) {
				if (desc.getPmReason().getReasonId().equals(resId)) {
					SmApplRejectReason rej = new SmApplRejectReason();
					rej.setPmReason(desc.getPmReason());
					rej.setSmAppl(getManagedBean().getSmAppl());
					resList.add(rej);
				}
			}
		}
		getManagedBean().getSmAppl().setSmApplRejectReasons(resList);
	}

	private boolean siteVisitDataUpdate() {
		SmSvTask svTask = new SmSvTask();
		List<SmSvAddress> addrList = new ArrayList<SmSvAddress>();
		List<SmSvCategory> smSvCategories = new ArrayList<SmSvCategory>();
		svTask.setSmAppl(getManagedBean().getSmAppl());
		System.out.println("** Basic Office ***"
				+ getManagedBean().getBasicCompanyFacility().getSmAddress()
						.getAddress1() + " -- Id -- "
				+ getManagedBean().getBasicCompanyFacility().getFacilityId());
		SmSvOffice svOff = new SmSvOffice();
		svOff.setSmSvTask(svTask);
		svOff.setSmFacility(getManagedBean().getBasicCompanyFacility());
		svTask.setSmSvOffice(svOff);
		for (SmFacility fac : getManagedBean().getSelectedFactoryFacilities()) {
			SmSvAddress facAddr = new SmSvAddress();
			facAddr.setSmSvTask(svTask);
			facAddr.setSmAddress(fac.getSmAddress());
			addrList.add(facAddr);
		}
		for (SmSupplierBranch br : getManagedBean().getSelectedBranches()) {

			SmSvAddress brAddr = new SmSvAddress();
			brAddr.setSmSvTask(svTask);
			brAddr.setSmAddress(br.getSmAddress());
			addrList.add(brAddr);
		}
		for (SmAssociateCompany assoc : getManagedBean()
				.getSelectedAssocCompanies()) {
			SmSvAddress asocAddr = new SmSvAddress();
			asocAddr.setSmSvTask(svTask);
			asocAddr.setSmAddress(assoc.getSmAddress());
			addrList.add(asocAddr);
		}
		svTask.setSmSvAddresses(addrList);
		List<PmParameter> catParam = new ArrayList<PmParameter>();
		for (SmSupplierCategory cat : getManagedBean().getSelectedCategories()) {
			if (!catParam.contains(cat.getPmCategoryL3().getCategoryTypeCode())) {
				catParam.add(cat.getPmCategoryL3().getCategoryTypeCode());

			}
			SmSvCategory svCat = new SmSvCategory();
			svCat.setSmSvTask(svTask);
			svCat.setPmCategoryL1(cat.getPmCategoryL1());
			svCat.setPmCategoryL2(cat.getPmCategoryL2());
			svCat.setPmCategoryL3(cat.getPmCategoryL3());
			smSvCategories.add(svCat);

		}
		svTask.setSmSvCategories(smSvCategories);
		List<SmSvTemplate> templates = new ArrayList<SmSvTemplate>();
		for (PmParameter param : catParam) {
			SmSvTemplate temp = new SmSvTemplate();
			temp.setPmParameter(param);
			temp.setSmSvTask(svTask);
			templates.add(temp);
		}
		svTask.setSmSvTemplates(templates);
		return updateSiteVisitTask(svTask);

	}

	private boolean updateSiteVisitTask(SmSvTask svTask) {
		ISiteAssignSO siteSO = null;
		boolean updateSuccess = true;
		try {
			// Save or update to DB
			siteSO = (ISiteAssignSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", ISiteAssignSO.class);
			SiteAssignReqDTO reqDto = new SiteAssignReqDTO();

			reqDto.setSmSvTask(svTask);

			SiteAssignResDTO resDTO = siteSO.updateSiteVisitAssignment(reqDto);
			updateSuccess = resDTO.isSuccessful();
		} catch (Exception e) {
			// System.out.println("this is error");
			// e.printStackTrace();
			updateSuccess = false;
			throw new EpException(MessageConst.ERROR_722506);
		} finally {
			siteSO = null;
		}
		return updateSuccess;
	}

	private void setPoOrApprvrSectionReview() {
		if (getManagedBean().isPoUser()
				&& getManagedBean().getPoSecReview().getApplSectionReviewId() != null) {
			for (SmApplSectionReview rev : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (rev.getApplSectionReviewId().equals(
						getManagedBean().getPoSecReview()
								.getApplSectionReviewId())) {
					System.out.println("**** Existing po remarks *******"
							+ getManagedBean().getPoSecReview()
									.getApplSectionReviewId());
					System.out.println("**** Baisc po remarks *******"
							+ getManagedBean().getPoSecReview()
									.getBasicRemark());
					System.out.println("**** Baisc verified po remarks *******"
							+ getManagedBean().getPoSecReview()
									.isBasicVerified());
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
			System.out.println("**** Existing Apprvr remarks *******");
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
		System.out.println("******* Page Type ********" + currentPageType);
		boolean validSave = true;
		switch (currentPageType) {
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC:
			if (this.getManagedBean().getSmAppl().getSmFacilities().isEmpty()) {
				this.getManagedBean().getSmAppl().getSmFacilities()
						.add(this.getManagedBean().getBasicCompanyFacility());

			} else {
				// TODO
				for (int i = 0; i < this.getManagedBean().getSmAppl()
						.getSmFacilities().size(); i++) {
					if (this.getManagedBean().getSmAppl().getSmFacilities()
							.get(i).getFacilityType().equalsIgnoreCase("B")
							|| this.getManagedBean().getSmAppl()
									.getSmFacilities().get(i).getFacilityType()
									.equalsIgnoreCase("H")) {
						this.getManagedBean()
								.getSmAppl()
								.getSmFacilities()
								.set(i,
										this.getManagedBean()
												.getBasicCompanyFacility());
						break;
					}
				}
			}
			int validStfCont = 0;
			for (SmStaffTotal stfTl : getManagedBean().getSmAppl()
					.getSmStaffTotals()) {
				if (stfTl.getTotalBumi() > 0 || stfTl.getTotalForeigner() > 0
						|| stfTl.getTotalNonBumi() > 0) {
					validStfCont = validStfCont + 1;
					break;
				}
			}
			if (validStfCont == 0) {
				getManagedBean().getSmAppl().setSmStaffTotals(
						getStaffList(getManagedBean().getSmAppl()
								.getSupplierType()));
				setNavigationBackToCurrentPage();
				throw new EpException(MessageConst.ERROR_722526);
			}
			if (!getManagedBean().getSmAppl().getSmAssociationBodies()
					.isEmpty()) {
				System.out
						.println(" ******* ASSOCIATION BODIES ADDED *********");
				for (SmAssociationBody assoc : getManagedBean().getSmAppl()
						.getSmAssociationBodies()) {
					System.out.println(" ** THE CERT VALUE BEFORE SAVE ***"
							+ assoc.getPmCertBody().getCertBodyId());
				}
			}
			System.out.println("******** Before save cert size ******"
					+ getManagedBean().getSmAppl().getSmSupplierCerts().size());
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY:
			// assignEnteredStaffValues();
			if (getManagedBean().getSmapplShareHolders() == null
					|| getManagedBean().getSmapplShareHolders().isEmpty()) {

				validSave = false;
				setNavigationBackToCurrentPage();
				throw new EpException(MessageConst.ERROR_722507);
			} else if (getManagedBean().getBusinessTypeVal() != null
					&& getManagedBean()
							.getBusinessTypeVal()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
					&& getManagedBean().getSmapplShareHolders().size() < 2) {

				validSave = false;
				setNavigationBackToCurrentPage();
				throw new EpException(MessageConst.ERROR_722508);
			}
			if (getManagedBean()
					.getSmAppl()
					.getSmCompanyBasic()
					.getPaidUpCapital()
					.compareTo(
							getManagedBean().getSmAppl().getSmCompanyBasic()
									.getAuthorizedCapital()) > 0) {
				validSave = false;
				setNavigationBackToCurrentPage();
				throw new EpException(MessageConst.ERROR_722509);
			}
			calcPersonnelListCount();
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL:
			int mgmtCount = 0;
			int staffCount = 0;
			int directorCount = 0;
			int profCount = 0;
			int semiProfCount = 0;

			for (SmPersonnel per : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				PmParameter param = null;
				if (per.getPmStaffRole() != null
						&& per.getPmStaffRole().getPmParameterType() != null) {
					param = per.getPmStaffRole();

				} else if (per.getStaffRoleFlg() != null) {
					param = new PmParameter();
					param = retrievePmParameterById(per.getStaffRoleFlg());
				}
				System.out.println("**** EPF Value --- >>>> " + per.getEpfNo());
				if (getManagedBean()
						.getSmAppl()
						.getSupplierType()
						.equalsIgnoreCase(
								SupplierManagementConst.STYPE_CONTRACTOR)) {
					if (per.isDirector() || per.isHigherManagement()) {
						directorCount = directorCount + 1;
					} else if (per.getStaffRoleFlg() != null
							&& param != null
							&& param.getPmParameterType()
									.getParameterType()
									.equalsIgnoreCase(
											SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONTRACTOR)
							&& param.getParameterCode()
									.equals(SupplierManagementConst.PERSONNEL_MANAGEMENT_STAFF_CONTRACTOR_PARAMCODE)) {
						mgmtCount = mgmtCount + 1;
					} else if (per.getStaffRoleFlg() != null
							&& param != null
							&& param.getPmParameterType()
									.getParameterType()
									.equalsIgnoreCase(
											SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONTRACTOR)
							&& param.getParameterCode()
									.equals(SupplierManagementConst.PERSONNEL_NORMAL_STAFF_CONTRACTOR_PARAMCODE)) {
						staffCount = staffCount + 1;
					}
				}
				if (getManagedBean()
						.getSmAppl()
						.getSupplierType()
						.equalsIgnoreCase(
								SupplierManagementConst.STYPE_CONSULTANT)) {
					if (per.getStaffRoleFlg() != null
							&& param != null
							&& param.getPmParameterType()
									.getParameterType()
									.equalsIgnoreCase(
											SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
							&& param.getParameterCode()
									.equals(SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_CONSULTANT_PARAMCODE)
							&& per.getEpfNo() != null && per.getEpfNo() != "") {
						// for (SmStaffAcademic aca : per.getSmStaffAcademics())
						// {
						// if (aca.getQualificationLevelId() == 123) {
						// staffCount = staffCount + 1;
						// break;
						// }
						// }
						System.out.println("*** is staff **** ");
						staffCount = staffCount + 1;

					} else if (per.getStaffRoleFlg() != null
							&& param != null
							&& param.getPmParameterType()
									.getParameterType()
									.equalsIgnoreCase(
											SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
							&& param.getParameterCode()
									.equals(SupplierManagementConst.PERSONNEL_PROF_STAFF_CONSULTANT_PARAMCODE)
							&& per.getEpfNo() != null && per.getEpfNo() != "") {
						for (SmStaffAcademic aca : per.getSmStaffAcademics()) {
							if (aca.getQualificationLevelId() >= 83
									&& aca.getQualificationLevelId() <= 85) {
								profCount = profCount + 1;
								break;
							}
						}

					} else if (per.getStaffRoleFlg() != null
							&& param != null
							&& param.getPmParameterType()
									.getParameterType()
									.equalsIgnoreCase(
											SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
							&& param.getParameterCode()
									.equals(SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_CONSULTANT_PARAMCODE)
							&& per.getEpfNo() != null && per.getEpfNo() != "") {
						for (SmStaffAcademic aca : per.getSmStaffAcademics()) {
							if (aca.getQualificationLevelId() >= 83
									&& aca.getQualificationLevelId() <= 86) {
								semiProfCount = semiProfCount + 1;
								break;
							}
						}
					}
				}

			}
			if (getManagedBean().getSmAppl().getSupplierType()
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				if (semiProfCount < 1) {
					validSave = false;
					setNavigationBackToCurrentPage();
					throw new EpException(MessageConst.ERROR_722510,
							new String[] { "Semi-Professional", "Diploma" });
				}
				if (profCount < 1) {
					validSave = false;
					setNavigationBackToCurrentPage();
					throw new EpException(MessageConst.ERROR_722510,
							new String[] { "Professional", "Bachelor Degree" });
				}
				if (staffCount < 1) {
					validSave = false;
					setNavigationBackToCurrentPage();
					throw new EpException(
							MessageConst.ERROR_722510,
							new String[] { "Clerical Staff",
									"SijilPelajaran Malaysia or equivalent certificate" });
				}
			}
			if (getManagedBean().getSmAppl().getSupplierType()
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
				if (directorCount < 1) {
					validSave = false;
					setNavigationBackToCurrentPage();
					throw new EpException(MessageConst.ERROR_722513,
							new String[] { "Higher Management or Director",
									"Director or Higher Management" });
				}
				if (mgmtCount < 1) {
					validSave = false;
					setNavigationBackToCurrentPage();
					throw new EpException(MessageConst.ERROR_722513,
							new String[] { "Management Staff", "Management" });
				}
				if (staffCount < 1) {
					validSave = false;
					setNavigationBackToCurrentPage();
					throw new EpException(MessageConst.ERROR_722513,
							new String[] { "Staff", "Staff" });
				}
			}
			calculateBumiPercentageTotals();
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK:
			int payAccCnt = 0;

			for (SmSupplierBank bank : getManagedBean().getSmAppl()
					.getSmSupplierBanks()) {
				if (bank.getAccountPurpose().equalsIgnoreCase("P")
						|| bank.getAccountPurpose().equalsIgnoreCase("B")) {
					payAccCnt++;
					break;
				}
			}
			if (payAccCnt == 0) {
				validSave = false;
				setNavigationBackToCurrentPage();
				throw new EpException(MessageConst.ERROR_722515);
			}
			break;
		case SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY:
			if (getManagedBean().getSmAppl().getSmSupplierCategories() != null
					&& getManagedBean().getSmAppl().getSmSupplierCategories()
							.size() > 0) {
				for (SmSupplierCategory category : getManagedBean().getSmAppl()
						.getSmSupplierCategories()) {
					if (category.getPmCategoryL3().getCategoryL3Code()
							.equalsIgnoreCase("99")) {
						if (category.getOtherDesc().isEmpty()) {
							validSave = false;
							setNavigationBackToCurrentPage();
							throw new EpException(MessageConst.ERROR_722514);
						}
					}
				}
			}

			if (getManagedBean().isSupplierUser()) {
				System.out.println(" ** For Suporting Docs ** ");
				if (getManagedBean().getSmAppl().getSmSupplierCategories() == null
						|| getManagedBean().getSmAppl()
								.getSmSupplierCategories().isEmpty()) {
					System.out.println("****** Empty Category List *******");
					validSave = false;
					setNavigationBackToCurrentPage();
					throw new EpException(MessageConst.ERROR_722516);
				}
				if (getManagedBean().getSmAppl().getSmApplSupportDocs() == null) {
					getManagedBean().getSmAppl().setSmApplSupportDocs(
							new ArrayList<SmApplSupportDoc>());
				}
				DocumentReqDTO rqDto = new DocumentReqDTO();
				rqDto.setStateCode(getManagedBean().getSmAppl()
						.getSmCompanyBasic().getSmCompanyAddresses().get(0)
						.getSmAddress().getPmState().getStateCode());
				rqDto.setAppTypeCode(getManagedBean().getSmAppl().getApplType());
				if (!getManagedBean().getSmAppl().getSmSupplierCategories()
						.isEmpty()) {
					List<Long> catL3Ids = new ArrayList<Long>();
					for (SmSupplierCategory cat : getManagedBean().getSmAppl()
							.getSmSupplierCategories()) {
						catL3Ids.add(cat.getPmCategoryL3().getCategoryL3Id());
					}
					rqDto.setCategoryL3Ids(catL3Ids);
					System.out
							.println(" **** Total categories sent to Doc So ***"
									+ rqDto.getCategoryL3Ids().size());
				}
				rqDto.setSupplierTypeCode(getManagedBean().getSmAppl()
						.getSupplierType());
				rqDto.setCompanyTypeCode(getManagedBean().getSmAppl()
						.getSmSupplier().getBusinessType());
				DocumentResDTO rsDTO = retrieveSupportingDocumentList(rqDto);

				if (rsDTO != null && rsDTO.getDocumentList() != null
						&& !rsDTO.getDocumentList().isEmpty()) {
					System.out.println("*** Not Empty list ****"
							+ rsDTO.getDocumentList().size());
					List<SmApplSupportDoc> docs = new ArrayList<>();

					if (getManagedBean().getSmAppl().getSmApplSupportDocs()
							.isEmpty()) {
						System.out.println(" ******* Empty Doc List *****");
						for (PmDocument doc : rsDTO.getDocumentList()) {
							docs.add(getDefaultSmApplSupportingDoc(doc));
						}
					} else {
						docs.addAll(getManagedBean().getSmAppl()
								.getSmApplSupportDocs());
						for (PmDocument doc : rsDTO.getDocumentList()) {
							boolean recordExists = false;
							for (SmApplSupportDoc smDocu : getManagedBean()
									.getSmAppl().getSmApplSupportDocs()) {
								if (smDocu.getPmDocument().getDocumentId()
										.equals(doc.getDocumentId())) {
									// docs.add(smDocu);
									System.out
											.println("******* Document already exists *****");
									recordExists = true;
								}
							}
							if (!recordExists) {
								System.out
										.println("******* This is a new Document ********");
								docs.add(getDefaultSmApplSupportingDoc(doc));
							}
						}
					}

					getManagedBean().getSmAppl().setSmApplSupportDocs(docs);
				}
			}
			break;

		default:
			System.out.println("****** Not a defined page ***********");

		}
		return validSave;
	}

	private void setNavigationBackToCurrentPage() {
		getManagedBean().setTabActiveIndex(currentTab);
		getManagedBean().getSmAppl().setCurrentActiveTab(
				getManagedBean().getSmAppl().getCurrentActiveTab());
	}

	private SmApplSupportDoc getDefaultSmApplSupportingDoc(PmDocument doc) {
		SmApplSupportDoc docu = new SmApplSupportDoc();

		docu.setPmDocument(doc);
		docu.setSmAppl(getManagedBean().getSmAppl());
		docu.setVerified(false);
		docu.setVerifiedFlg(0);
		docu.setReceived(false);

		List<SmApplDocFile> files = new ArrayList<SmApplDocFile>();
		SmApplDocFile docFile = new SmApplDocFile();
		docFile.setSmApplSupportDoc(docu);
		docFile.setFileName(" ");
		docFile.setFilePath(" ");
		files.add(docFile);
		docu.setSmApplDocFiles(files);
		return docu;
	}

	public void createNewApplication() throws Exception {
		try {
			System.out.println("**** New BEAN  *************"
					+ getManagedBean().getSmSupplier().getSmAppls().size());
			System.out.println("**** Latest Appl Id   *************"
					+ getManagedBean().getSmSupplier().getSmAppls().get(0)
							.getApplId());
			SmAppl app = getManagedBean().getSmSupplier().getSmAppls().get(0);
			System.out.println(" ******* New Objectr == >> " + app.getApplId()
					+ " Name == " + app.getApplNo());
			getManagedBean().setSmAppl(app);
			// TODO to be confirmed whether reqd to save the appln as draft
			getManagedBean().setTabActiveIndex(outputTab);

		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
			getManagedBean().setTabActiveIndex(currentTab);
		}

	}

	public void setTabValue(int currentTab, int outputTab) {
		System.out.println(" ***** Tab Values *** " + currentTab + outputTab);
		this.currentTab = currentTab;
		this.outputTab = outputTab;

		// getManagedBean().setTabActiveIndex(outputTab);
	}

	public void assignReviewSupportingNav(String navType) {
		int currentPage = getManagedBean().getTabActiveIndex();
		try {
			this.currentTab = currentPage;
			System.out
					.println("********** ENTER THE PRE RENDER FOR SUPP DOXS ***** "
							+ currentPage + "  , " + navType);
			getManagedBean().getSmAppl().setSupportingDocMode("O");
			if (navType.equalsIgnoreCase("next")) {
				System.out.println("** MY NEXT PAGE ***");
				this.outputTab = currentPage + 1;
			} else if (navType.equalsIgnoreCase("prev")) {
				System.out.println("** MY PREV PAGE ***");
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
			System.out.println("******** SELECTED OWN FACILITY *******");
			SmAddress addr = getManagedBean().getBasicCompanyFacility()
					.getSmAddress();
			if (addr.getPmState().getStateId() == null) {
				getManagedBean().getBasicCompanyFacility().getSmAddress()
						.getPmState().setStateId(1l);
			}
			if (addr.getPmDistrict().getDistrictId() == null) {
				getManagedBean().getBasicCompanyFacility().getSmAddress()
						.getPmDistrict().setDistrictId(1l);
			}
			if (addr.getPmCity().getCityId() == null) {
				getManagedBean().getBasicCompanyFacility().getSmAddress()
						.getPmCity().setCityId(1l);
			}
			if (addr.getAddress1() == null) {
				getManagedBean().getBasicCompanyFacility().getSmAddress()
						.setAddress1(" ");
			}
		} else {

		}
		System.out
				.println(" ******* On Change Office Facility ***** "
						+ getManagedBean().getBasicCompanyFacility()
								.getOwnershipType());
		System.out.println(" **** Values In loop *********"
				+ getManagedBean().getBasicCompanyFacility().getRentalRate());
		System.out.println("********* TEst getPmCountry NULL VALUE **********"
				+ this.getManagedBean().getBasicCompanyFacility()
						.getSmAddress().getPmCountry().getCountryId());
	}

	public void assignPersonnelPrivilege(AjaxBehaviorEvent e) {
		System.out.println("**** Enter peronnel privilege ******");
		if (getManagedBean().getSelectedPersonnel().getAuthorizedFlg() == 0) {
			getManagedBean().getSelectedPersonnel().setSoftcertFlg(0);
			getManagedBean().getSelectedPersonnel().setContractSignerFlg(0);
		}

		if (getManagedBean().getSelectedPersonnel().getSoftcertFlg() == 0) {
			getManagedBean().getSelectedPersonnel().setContractSignerFlg(0);
		}
	}

	public void assignShareHolderPrivilege(AjaxBehaviorEvent e) {
		System.out.println("**** Enter Share Holder privilege ******");
		if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() == 0) {
			getManagedBean().getSelectedShareHolder().setSoftcertFlg(0);
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(0);
		}

		if (getManagedBean().getSelectedShareHolder().getSoftcertFlg() == 0) {
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(0);
		}
	}

	public void applicationCategorySelected(AjaxBehaviorEvent e) {
		System.out
				.println(" ******* On Application Category ***** "
						+ getManagedBean().getSmAppl().getApplCategoryType()
						+ "-- Size --"
						+ getManagedBean().getSmAppl()
								.getSmSupplierCategories().size());
		StringBuffer sb = new StringBuffer();
		String desc = null;
		System.out.println("---- Current Active Tab ---- "
				+ getManagedBean().getSmAppl().getCurrentActiveTab());
		System.out.println("---- Active Tab Index ---- "
				+ getManagedBean().getTabActiveIndex());
		getManagedBean().getSmAppl().setCurrentActiveTab(
				getManagedBean().getTabActiveIndex());
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			desc = "Normal";
		} else {
			desc = "Special";
		}
		sb.append("NOTE : '");
		sb.append(desc);
		sb.append("' categories can only be added in this applicaiton'");
		getManagedBean().setCatTypeConfirm(sb.toString());
		System.out.println("*** Msg Is ****"
				+ getManagedBean().getCatTypeConfirm());
		System.out.println(" -- DOC MODE -- "
				+ getManagedBean().getSmAppl().getSupportingDocMode());
		if (getManagedBean().isPoUser() || getManagedBean().isApproverUser()) {
			assignMaxReviewCount(getManagedBean().getSmAppl().getApplType(),
					getManagedBean().getSmAppl().getApplCategoryType(),
					getManagedBean().getSmAppl().getSupplierType());
		}
	}

	public void assignApplnCategoryType() {
		System.out.println("***** Before confirm +++++++"
				+ getManagedBean().getSmAppl().getSupportingDocMode());
		List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.isSpecialCategory() && !cat.isApprovedByPo()) {
					catList.add(cat);
				}

			}
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.removeAll(catList);
		} else {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (!cat.isSpecialCategory() && !cat.isApprovedByPo()) {
					catList.add(cat);
				}

			}
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.removeAll(catList);
		}
		System.out.println("***** After confirm +++++++"
				+ getManagedBean().getSmAppl().getSupportingDocMode());
		// TODO fix the bug and remove this hard code
		if (getManagedBean().getSmAppl().getSupportingDocMode() == null
				|| getManagedBean().getSmAppl().getSupportingDocMode()
						.equals("")) {
			getManagedBean().getSmAppl().setSupportingDocMode("O");
		}
	}

	public void resetApplnCategoryType() {
		System.out.println("********* Reset the Values ********");
		// getManagedBean().getSmAppl().setApplCategoryType(null);
		getManagedBean().setCatTypeConfirm(null);
		// TODO fix the bug and remove this hard code
		if (getManagedBean().getSmAppl().getSupportingDocMode() == null
				|| getManagedBean().getSmAppl().getSupportingDocMode()
						.equals("")) {
			getManagedBean().getSmAppl().setSupportingDocMode("O");
		}
	}

	public void svcSelected(AjaxBehaviorEvent e) {
		System.out.println(" ******* On Change Office Facility ***** "
				+ getManagedBean().getPoSiteVisitType());

	}

	// -- Process Branches Start
	public void addOrUpdateBranchToSmAppl() {

		if (getManagedBean().getSmAppl().getSmSupplierBranches()
				.contains(getManagedBean().getSelectedBranch())) {

			System.out.println(" **** THIS IS AN UPDATE For Branches******"
					+ getManagedBean().getSelectedBranch().getPhoneNo());
		} else {
			getManagedBean().getSmAppl().getSmSupplierBranches()
					.add(getManagedBean().getSelectedBranch());
		}
	}

	public void assignSelBranch(SmSupplierBranch branchBean, int index) {
		System.out.println("****** Edit Index **********" + index);
		getManagedBean()
				.setSelectedBranchPrev(
						getManagedBean().getSmAppl().getSmSupplierBranches()
								.get(index));
		getManagedBean().setSelectedBranchIndex(index);
		System.out.println("***** Before *****"
				+ getManagedBean().getSelectedBranchPrev().getSmAddress()
						.getAddress1());
		getManagedBean().setSelectedBranch(branchBean);
		System.out.println("***** After *****"
				+ getManagedBean().getSelectedBranchPrev().getSmAddress()
						.getAddress1());

	}

	public void assignNewBranchBean() {
		getManagedBean().setSelectedBranch(new SmSupplierBranch());
		getManagedBean().getSelectedBranch().setRevNo(1);
		getManagedBean().getSelectedBranch().setSmAddress((setDefaultAddr()));
		getManagedBean().getSelectedBranch().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setBranchCompanyStates(
				retrievePmStateList(getManagedBean().getSelectedBranch()
						.getSmAddress().getPmCountry().getCountryId()));
	}

	public void assignCancelBranchBean() {
		if (!getManagedBean().getSmAppl().getSmSupplierBranches().isEmpty()) {
			System.out.println("******** Into cancel *******"
					+ getManagedBean().getSelectedBranchPrev().getSmAddress()
							.getAddress1() + " **** For Index ********"
					+ getManagedBean().getSelectedBranchIndex());
			getManagedBean()
					.getSmAppl()
					.getSmSupplierBranches()
					.set(getManagedBean().getSelectedBranchIndex(),
							getManagedBean().getSelectedBranchPrev());
		}
		assignNewBranchBean();
	}

	public void deleteSelectedBranches() {

		System.out.println("*** Delete Branches Companies ******* "
				+ getManagedBean().getSelectedBranches().size());
		if (getManagedBean().getSelectedBranches().size() > 0) {
			for (SmSupplierBranch brn : getManagedBean().getSelectedBranches()) {
				getManagedBean().getSmAppl().getSmSupplierBranches()
						.remove(brn);
			}
			getManagedBean().getSelectedBranches().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// -- Process Branches End

	// -- Process Associated Companies Start
	public void addOrUpdateAssocToSmAppl() {

		if (getManagedBean().getSmAppl().getSmAssociateCompanies()
				.contains(getManagedBean().getSelectedAssocCompany())) {

			System.out.println(" **** THIS IS AN UPDATE Assoc Companies******");
		} else {
			getManagedBean().getSmAppl().getSmAssociateCompanies()
					.add(getManagedBean().getSelectedAssocCompany());
		}
	}

	public void assignSelAssoc(SmAssociateCompany assocBean) {
		getManagedBean().setSelectedAssocCompany(assocBean);
	}

	public void assignNewAssocBean() {
		getManagedBean().setSelectedAssocCompany(new SmAssociateCompany());
		getManagedBean().getSelectedAssocCompany().setRevNo(1);
		getManagedBean().getSelectedAssocCompany().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedAssocCompany().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setAssocCompanyStates(
				retrievePmStateList(getManagedBean().getSelectedAssocCompany()
						.getSmAddress().getPmCountry().getCountryId()));
	}

	public void deleteSelectedAssocCompanies() {
		System.out.println("*** Delete Assoc Companies ******* "
				+ getManagedBean().getSelectedAssocCompanies().size());
		if (getManagedBean().getSelectedAssocCompanies().size() > 0) {
			for (SmAssociateCompany assoc : getManagedBean()
					.getSelectedAssocCompanies()) {
				getManagedBean().getSmAppl().getSmAssociateCompanies()
						.remove(assoc);
			}
			getManagedBean().getSelectedAssocCompanies().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}
	}

	// -- Process Associated Companies End

	// -- Process Certs Start
	public void addOrUpdateCertsToSmAppl() throws ParseException {
		getManagedBean().getSelectedCert().setSmAppl(
				getManagedBean().getSmAppl());
		if (getManagedBean().getSmAppl().getSmSupplierCerts()
				.contains(getManagedBean().getSelectedCert())) {

			System.out.println(" **** THIS IS AN UPDATE Certs******");
		} else {
			System.out.println(" **** THIS IS AN SAVE NEW Certs******");
			getManagedBean().getSmAppl().getSmSupplierCerts()
					.add(getManagedBean().getSelectedCert());
		}

		RequestContext.getCurrentInstance().execute("certDialog.hide()");
	}

	public void assignSelCert(SmSupplierCert certBean) {
		getManagedBean().setSelectedCert(certBean);
	}

	public void assignNewCert() {
		SmSupplierCert cert = new SmSupplierCert();
		cert.setPmCertBody(new PmCertBody());
		cert.setRevNo(1);
		getManagedBean().setSelectedCert(cert);
		cert.setSmAppl(getManagedBean().getSmAppl());
	}

	public void deleteSelectedCerts() {
		System.out.println("******** Before delete ******"
				+ getManagedBean().getSmAppl().getSmSupplierCerts().size());
		System.out.println("*** Delete Certs ******* "
				+ getManagedBean().getSelectedCertList().size());
		if (getManagedBean().getSelectedCertList().size() > 0) {
			for (SmSupplierCert cert : getManagedBean().getSelectedCertList()) {
				getManagedBean().getSmAppl().getSmSupplierCerts().remove(cert);
			}
			getManagedBean().getSelectedCertList().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}
		System.out.println("******** After delete ******"
				+ getManagedBean().getSmAppl().getSmSupplierCerts().size());
	}

	// -- Process Certss End
	// -- Process Assoc Bodies Start
	public void addOrUpdateAssocBodiesToSmAppl() throws ParseException {
		getManagedBean().getSelectedAssocBody().setSmAppl(
				getManagedBean().getSmAppl());
		if (getManagedBean().getSmAppl().getSmAssociationBodies()
				.contains(getManagedBean().getSelectedAssocBody())) {

			System.out.println(" **** THIS IS AN UPDATE Certs******");
		} else {
			System.out
					.println(" **** THIS IS AN SAVE NEW Association Body******"
							+ getManagedBean().getSelectedAssocBody()
									.getRevNo()
							+ " *** Sample id *** "
							+ getManagedBean().getSelectedAssocBody()
									.getPmCertBody().getCertBodyId());
			getManagedBean().getSmAppl().getSmAssociationBodies()
					.add(getManagedBean().getSelectedAssocBody());
		}
	}

	public void assignSelAssocBody(SmAssociationBody assocBean) {
		getManagedBean().setSelectedAssocBody(assocBean);
	}

	public void assignNewAssocBody() {
		SmAssociationBody assocBean = new SmAssociationBody();
		assocBean.setPmCertBody(new PmCertBody());
		assocBean.setRevNo(1);
		getManagedBean().setSelectedAssocBody(assocBean);
	}

	public void deleteSelectedAssocBodies() {

		System.out.println("*** Delete AssocBodies ******* "
				+ getManagedBean().getSelectedAssocBodyList().size());
		if (getManagedBean().getSelectedAssocBodyList().size() > 0) {
			for (SmAssociationBody assoc : getManagedBean()
					.getSelectedAssocBodyList()) {
				getManagedBean().getSmAppl().getSmAssociationBodies()
						.remove(assoc);
			}
			getManagedBean().getSelectedAssocBodyList().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// -- Process Assoc Bodies End
	// -- Process Contract Projs Start
	public void addOrUpdateContractProjsToSmAppl() {
		// if (getManagedBean().getSmAppl().getSmContractExperiences()
		// .contains(getManagedBean().getSelectedContractProj())) {
		//
		// System.out
		// .println(" **** THIS IS AN UPDATE FOR Contract Prjs******");
		// } else {
		// getManagedBean().getSmAppl().getSmContractExperiences()
		// .add(getManagedBean().getSelectedContractProj());
		// }
	}

	// public void assignSelContractProj(SmContractExperience contractBean) {
	// getManagedBean().setSelectedContractProj(contractBean);
	// }
	//
	// public void assignNewContractProj() {
	// getManagedBean().setSelectedContractProj(new SmContractExperience());
	// }

	// -- Process Contract Projs End

	// -- Process Consultant or Contractor Company Experience Start
	public void addOrUpdateCompanyExperienceToSmAppl() {
		if (getManagedBean().getSelectedCompanyExperience()
				.getPaymentMethodId() == null) {
			getManagedBean().getSelectedCompanyExperience().setPaymentMethodId(
					new BigDecimal(0));
		}
		if (getManagedBean().getSmAppl().getSmCompanyExperiences()
				.contains(getManagedBean().getSelectedCompanyExperience())) {

			System.out
					.println(" **** THIS IS AN UPDATE FOR Consultant Prjs******");
		} else {
			System.out
					.println(" **** THIS IS AN ADD FOR Consultant Prjs******");
			getManagedBean().getSmAppl().getSmCompanyExperiences()
					.add(getManagedBean().getSelectedCompanyExperience());
		}
	}

	public void assignSelCompanyExperience(SmCompanyExperience consultantBean) {
		getManagedBean().setSelectedCompanyExperience(consultantBean);
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			RequestContext.getCurrentInstance().execute("consDialog.show()");
		}
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			RequestContext.getCurrentInstance().execute("contDialog.show()");
		}
	}

	public void assignNewCompanyExperience() {
		System.out.println("***** Assign New Company Exp ***********");
		SmCompanyExperience exp = new SmCompanyExperience();
		exp.setRevNo(1);
		exp.setSupplierType(getManagedBean().getSmAppl().getSupplierType());
		exp.setSmAppl(getManagedBean().getSmAppl());
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			exp.setProcurementModeId(new Integer(0));
		}

		List<SmCompanyExperienceCat> expCatList = new ArrayList<SmCompanyExperienceCat>();
		SmCompanyExperienceCat cat = new SmCompanyExperienceCat();
		cat.setPmCategoryL1(new PmCategoryL1());
		cat.setPmCategoryL2(new PmCategoryL2());
		cat.setPmCategoryL3(new PmCategoryL3());
		cat.setSmCompanyExperience(exp);
		expCatList.add(cat);
		exp.setSmCompanyExperienceCats(expCatList);

		getManagedBean().setSelectedCompanyExperience(exp);

		resetChildCatList();
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

		System.out.println("*** Delete Exps ******* "
				+ getManagedBean().getSelectedCompanyExperiences().size());
		if (getManagedBean().getSelectedCompanyExperiences().size() > 0) {
			for (SmCompanyExperience exp : getManagedBean()
					.getSelectedCompanyExperiences()) {
				getManagedBean().getSmAppl().getSmCompanyExperiences()
						.remove(exp);
			}
			getManagedBean().getSelectedCompanyExperiences().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// ---- Process Consultant or Contractor Company Experience End

	// -- Process Equity Companies Start
	public void addOrUpdateEquityCompanyToSmAppl() {

		if (getManagedBean().getSmAppl().getSmEquityCompanies()
				.contains(getManagedBean().getSelectedEquityCompany())) {

			System.out
					.println(" **** THIS IS AN UPDATE Equity Companies******");
		} else {
			System.out.println("****** ADD EQUIRY ********* ");
			getManagedBean().getSmAppl().getSmEquityCompanies()
					.add(getManagedBean().getSelectedEquityCompany());
		}
	}

	public void assignSelEquiCompany(SmEquityCompany equiCompBean) {
		getManagedBean().setSelectedEquityCompany(equiCompBean);
	}

	public void assignNewEquiCompBean() {

		getManagedBean().setSelectedEquityCompany(new SmEquityCompany());
		getManagedBean().getSelectedEquityCompany().setRevNo(1);
		getManagedBean().getSelectedEquityCompany().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedEquityCompany().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setEquiCompanyStates(
				retrievePmStateList(getManagedBean().getSelectedEquityCompany()
						.getSmAddress().getPmCountry().getCountryId()));

	}

	public void deleteSelectedEquiCompanies() {

		if (getManagedBean().getSelectedEquityCompanies().size() > 0) {
			for (SmEquityCompany equi : getManagedBean()
					.getSelectedEquityCompanies()) {
				getManagedBean().getSmAppl().getSmEquityCompanies()
						.remove(equi);
			}
			getManagedBean().getSelectedEquityCompanies().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

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
			System.out.println(" --- Selected is a null --- ");
			PmCategoryL3 p3 = new PmCategoryL3();
			getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
		} else {
			for (PmCategoryL3 p3 : getManagedBean().getSelectedCategory()
					.getPmCategoryL2().getPmCategoryL3s()) {
				if (p3.getCategoryL3Code().equalsIgnoreCase(selPmCatL3)) {
					System.out.println("***** Selected Child is ********"
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
		// System.out.println("**** ENTER THE CHANGE OF CATEGORY TYPE **** "
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
			if (getManagedBean().getSmAppl().getApplCategoryType()
					.equalsIgnoreCase("S")) {
				categ.setSpecialCategory(true);
			}
			categ.setSmAppl(getManagedBean().getSmAppl());
			categ.setSupplierType(getManagedBean().getSmAppl()
					.getSupplierType());
			getManagedBean().setSelectedCategory(categ);
			resetSelectedCategoryParams();
			RequestContext.getCurrentInstance().execute("regCatDialog.show()");
		}
	}

	public void deleteSelectedCategoryCodes() {
		if (getManagedBean().getDeleteCategoryList().size() > 0) {
			List<SmSupplierCategory> categories = getManagedBean()
					.getDeleteCategoryList();

			for (SmSupplierCategory category : categories) {
				getManagedBean().getSmAppl().getSmSupplierCategories()
						.remove(category);
			}
		}
	}

	public void saveSelectedCategory() {
		// System.out.println(" ******* Save From Selection ****"
		// + getManagedBean().getSelectedCategory().getCategoryType());
		boolean validCat = true;
		System.out.println("***** cat id ********"
				+ getManagedBean().getSelectedCategory().getPmCategoryL1()
						.getCategoryL1Id());
		if (getManagedBean().getSelectedCategory().getPmCategoryL1()
				.getCategoryL1Id() == 21) {
			System.out.println("****** Selected ICT ************");
			validCat = validateIctCatCode();
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

			RequestContext.getCurrentInstance().execute("regCatDialog.hide()");

		}
	}

	// TODO needs many validations and SO retrieve the PMcat1,2,3 details - To
	// be impl in future
	// public void saveEnteredCategory() {
	// // System.out.println(" ******* Save From Entry ****"
	// // + getManagedBean().getSelectedCategory().getCategoryType());
	// PmCategoryL1 p = new PmCategoryL1();
	// p.setCategoryL1Code(getManagedBean().getSelectedParentCat());
	// p.setCategoryName("ParentSample");
	// getManagedBean().getSelectedCategory().setPmCategoryL1(p);
	// PmCategoryL2 p2 = new PmCategoryL2();
	// p2.setCategoryL2Code(getManagedBean().getSelectedChildCat());
	// p2.setCategoryName("ChildSample");
	// getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
	// PmCategoryL3 p3 = new PmCategoryL3();
	// p3.setCategoryL3Code(getManagedBean().getSelectedSubChildCat());
	// p3.setCategoryName("SubChildSample");
	// getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
	// getManagedBean().getSmAppl().getSmSupplierCategories()
	// .add(getManagedBean().getSelectedCategory());
	//
	// }

	// -- Process Category Codes End
	// factory start
	public void addOrUpdateFactoryToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedFactoryFacility();
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplFactoryFacilities().contains(smFac)) {
				System.out
						.println(" ******* THIS IS EDIT FOR EXISITING FACTORY **********");
			}

		} else {
			getManagedBean().getApplFactoryFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
		}

	}

	public void assignSelFactory(SmFacility facilityBean) {
		System.out.println("******** Selected value ******"
				+ facilityBean.getSmAddress().getAddress1());
		getManagedBean().setSelectedFactoryFacility(facilityBean);
	}

	public void assignNewFactoryBean() {
		getManagedBean().setSelectedFactoryFacility(new SmFacility());
		getManagedBean().getSelectedFactoryFacility().setRevNo(1);
		getManagedBean().getSelectedFactoryFacility().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedFactoryFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedFactoryFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_FACTORY);
		getManagedBean().setFactoryFacilityStates(
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
	}

	public void factoryOwnerTypeSel(AjaxBehaviorEvent e) {
		System.out.println("Selected Owner type === "
				+ getManagedBean().getSelectedFactoryFacility()
						.getOwnershipType());
	}

	public void deleteSelectedFactories() {

		System.out.println("*** Delete Branches Companies ******* "
				+ getManagedBean().getSelectedFactoryFacilities().size());
		if (getManagedBean().getSelectedFactoryFacilities().size() > 0) {
			for (SmFacility sm : getManagedBean()
					.getSelectedFactoryFacilities()) {
				getManagedBean().getSmAppl().getSmFacilities().remove(sm);
				getManagedBean().getApplFactoryFacilities().remove(sm);
			}
			getManagedBean().getSelectedFactoryFacilities().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// factory end

	// Dockyard start
	public void addOrUpdateDockYardToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedDockyardFacility();
		System.out.println("**** This is dock yard data ********"
				+ smFac.getSmAddress().getAddress1());
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplDockyardFacilities().contains(smFac)) {
				System.out
						.println(" ******* THIS IS EDIT FOR EXISITING Doc **********");
			}

		} else {
			getManagedBean().getApplDockyardFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
		}

	}

	public void assignSelDockyard(SmFacility facilityBean) {
		System.out.println("******** Selected dock value ******"
				+ facilityBean.getSmAddress().getAddress1());
		getManagedBean().setSelectedDockyardFacility(facilityBean);
	}

	public void assignNewDockyardBean() {
		System.out.println(" ********** New doc yard value *********");
		getManagedBean().setSelectedDockyardFacility(new SmFacility());
		getManagedBean().getSelectedDockyardFacility().setRevNo(1);
		getManagedBean().getSelectedDockyardFacility().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedDockyardFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedDockyardFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD);
		getManagedBean().setDockFacilityStates(
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
	}

	public void dockOwnerTypeSel(AjaxBehaviorEvent e) {
		System.out.println("Selected Owner type === "
				+ getManagedBean().getSelectedDockyardFacility()
						.getOwnershipType());
	}

	public void deleteSelectedDocks() {

		System.out.println("*** Delete Dockyars ******* "
				+ getManagedBean().getSelectedDockyardFacilities().size());
		if (getManagedBean().getSelectedDockyardFacilities().size() > 0) {
			for (SmFacility sm : getManagedBean()
					.getSelectedDockyardFacilities()) {
				getManagedBean().getSmAppl().getSmFacilities().remove(sm);
				getManagedBean().getApplDockyardFacilities().remove(sm);
			}
			getManagedBean().getSelectedDockyardFacilities().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// Dockyard end
	// Workshop start
	public void addOrUpdateWorkshopToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedWorkShopFacility();
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplWorkShopFacilities().contains(smFac)) {
				System.out
						.println(" ******* THIS IS EDIT FOR EXISITING workshop **********");
			}

		} else {
			getManagedBean().getApplWorkShopFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
		}

	}

	public void assignSelWorkshop(SmFacility facilityBean) {
		getManagedBean().setSelectedWorkShopFacility(facilityBean);
	}

	public void assignNewWorkshopBean() {
		getManagedBean().setSelectedWorkShopFacility(new SmFacility());
		getManagedBean().getSelectedWorkShopFacility().setRevNo(1);
		getManagedBean().getSelectedWorkShopFacility().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedWorkShopFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedWorkShopFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP);
		getManagedBean().setWorkShopFacilityStates(
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
	}

	public void workShopOwnerTypeSel(AjaxBehaviorEvent e) {
		System.out.println("Selected Owner type === "
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
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// Workshop end
	// Workshop start
	public void addOrUpdateStoreToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedStoreFacility();
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplStoreFacilities().contains(smFac)) {
				System.out
						.println(" ******* THIS IS EDIT FOR EXISITING workshop **********");
			}

		} else {
			getManagedBean().getApplStoreFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
		}

	}

	public void assignSelStore(SmFacility facilityBean) {
		getManagedBean().setSelectedStoreFacility(facilityBean);
	}

	public void assignNewStoreBean() {
		getManagedBean().setSelectedStoreFacility(new SmFacility());
		getManagedBean().getSelectedStoreFacility().setRevNo(1);
		getManagedBean().getSelectedStoreFacility().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedStoreFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedStoreFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_STORE);
		getManagedBean().setStoreFacilityStates(
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
	}

	public void storeOwnerTypeSel(AjaxBehaviorEvent e) {
		System.out.println("Selected Owner type === "
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
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// Workshop end
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

			System.out.println(" **** THIS IS AN UPDATE For manufacturer******"
					+ getManagedBean().getSelectedManufacturer().getBrand());
		} else {

			getManagedBean().getSmAppl().getSmManufacturingDtls()
					.add(getManagedBean().getSelectedManufacturer());
		}
	}

	public void assignSelManufacturer(SmManufacturingDtl manBean) {
		getManagedBean().setSelectedManufacturer(manBean);
	}

	public void assignNewManufacturerBean() {
		System.out.println("**** Add new Manufacturer *********");
		SmManufacturingDtl manu = new SmManufacturingDtl();
		manu.setPmUom(new PmUom());
		manu.setRevNo(1);
		manu.setSmAppl(getManagedBean().getSmAppl());
		getManagedBean().setSelectedManufacturer(manu);
		System.out.println("****** After **********"
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
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}
	}

	// manufacturer end

	// machinery start
	public void addOrUpdateMachineToSmAppl() {

		if (getManagedBean().getSmAppl().getSmMachineDtls()
				.contains(getManagedBean().getSelectedMachine())) {

			System.out.println(" **** THIS IS AN UPDATE For machinery ******"
					+ getManagedBean().getSelectedMachine().getBrandName());
		} else {

			getManagedBean().getSmAppl().getSmMachineDtls()
					.add(getManagedBean().getSelectedMachine());
		}
	}

	public void assignSelMachinery(SmMachineDtl macBean) {
		getManagedBean().setSelectedMachine(macBean);
	}

	public void assignNewMachineryBean() {
		getManagedBean().setSelectedMachine(new SmMachineDtl());
		getManagedBean().getSelectedMachine().setRevNo(1);
		getManagedBean().getSelectedMachine().setSmAppl(
				getManagedBean().getSmAppl());
	}

	public void deleteSelectedMachinery() {

		if (getManagedBean().getSelectedMachineList().size() > 0) {
			for (SmMachineDtl mac : getManagedBean().getSelectedMachineList()) {
				getManagedBean().getSmAppl().getSmMachineDtls().remove(mac);
			}
			getManagedBean().getSelectedMachineList().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}
	}

	// manufacturer end

	// -- Process Share Holder Personnel Start
	public void addOrUpdateShareHolderToSmAppl(String fromPage)
			throws ParseException {
		boolean validShrHldr = true;
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& getManagedBean().getSelectedShareHolder()
						.getWorkExperienceYear() < 5) {
			System.out
					.println(" ******* This is a failure condition ********* "
							+ getManagedBean().getSelectedShareHolder()
									.getWorkExperienceYear());
			validShrHldr = false;
			throw new EpException(MessageConst.ERROR_722518);
		}
		validShrHldr = checkValidUser(getManagedBean().getSelectedShareHolder());
		// if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() ==
		// 1) {
		// validShrHldr = validateAuthPersonLimit();
		// }
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
				getManagedBean().getSmapplShareHolders().set(
						getManagedBean().getSmapplShareHolders().indexOf(
								getManagedBean().getSelectedShareHolder()),
						getManagedBean().getSelectedShareHolder());
				System.out.println(" **** THIS IS AN UPDATE Personnel ******");
			} else {

				getManagedBean().getSmAppl().getSmPersonnels()
						.add(getManagedBean().getSelectedShareHolder());
				getManagedBean().getSmapplShareHolders().add(
						getManagedBean().getSelectedShareHolder());
			}
			IMofRegistrationSO mofRegistrationSO = null;

			try {
				System.out.println("updating personnel into database");
				mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
						.getInstance().getServiceObject("sm",
								IMofRegistrationSO.class);

				MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();

				reqDto.setSmAppl(this.getManagedBean().getSmAppl());

				MofRegistrationResDTO resDTO = mofRegistrationSO
						.updateApplication(reqDto);
				// getManagedBean().setSmAppl(resDTO.getSmAppl());
			} finally {
				mofRegistrationSO = null;
				retriveLatestApplAfterUpdate("");
			}
			if (fromPage.equalsIgnoreCase("captialInfo")) {
				RequestContext.getCurrentInstance().execute(
						"shareHolderDialog.hide()");
			}
			if (fromPage.equalsIgnoreCase("personnelInfo")) {
				calcPersonnelListCount();
				RequestContext.getCurrentInstance().execute(
						"perShareHolderDialog.hide()");

			}

		}

	}

	public void assignSelShareHolder(SmPersonnel shareHolderBean) {
		if (shareHolderBean.getAuthorizedFlg() == 1) {
			getManagedBean().setAuthPerCount(0);
		} else {
			getManagedBean().setAuthPerCount(getAuthUserCount());
		}
		getManagedBean().setSelectedShareHolder(shareHolderBean);
	}

	public void addOrUpdateShareHolderAddress(SmPersonnel shareHolderBean)
			throws ParseException {
		if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {
			System.out.println("******** Empty list ********"
					+ shareHolderBean.getName() + " *** Id ***"
					+ shareHolderBean.getPersonnelId() + " ** Is Equity ****"
					+ shareHolderBean.isEquityOwner());
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
			System.out
					.println("******** Exisiting Share holders address **********"
							+ shareHolderBean.getName());
			for (SmPersonnelAddress addr : shareHolderBean
					.getSmPersonnelAddresses()) {
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderMailAddr());
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderOffAddr());
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderResdAddr());
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
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
		System.out.println("*** After set addr value ***"
				+ getManagedBean().getSelectedShareHolder().getName()
				+ "** Id ***"
				+ getManagedBean().getSelectedShareHolder().getPersonnelId());
		if (shareHolderBean.isEquityOwner()) {
			addOrUpdateShareHolderToSmAppl("addrPage");
		} else {
			addOrUpdatePersonnelToSmAppl();
		}
	}

	public void assignNewShareHolder() {
		System.out.println("********* assignNewShareHolder *****");
		getManagedBean().setAuthPerCount(getAuthUserCount());
		SmPersonnel shr = new SmPersonnel();
		shr.setEquityOwner(true);
		shr.setShareAmt(new BigDecimal(0));
		shr.setSharePercentage(new BigDecimal(0));
		setDefaultPersonnelValues(shr);
		// getManagedBean().setSelectedPersonnel(null);
		getManagedBean().setSelectedShareHolder(shr);

	}

	public void deleteSelectedShareHolders() {

		System.out.println("*** Delete Share Holders ******* "
				+ getManagedBean().getSelectedShareHolders().size());
		if (getManagedBean().getSelectedShareHolders().size() > 0) {
			for (SmPersonnel shr : getManagedBean().getSelectedShareHolders()) {
				getManagedBean().getSmAppl().getSmPersonnels().remove(shr);
				getManagedBean().getSmapplShareHolders().remove(shr);
			}
			getManagedBean().getSelectedShareHolders().clear();
			calcPersonnelListCount();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// -- Process Share Holder Personnel End
	// -- Process Common for both Personnel and Share Holder Start --
	public void addShrHldrJobFunction(Boolean isShrHldr) {
		System.out.println(">>> Add Personnel Job Function >>>>>>>>>>>>>>>>>>"
				+ isShrHldr);
		SmStaffExperience exp = new SmStaffExperience();
		if (isShrHldr) {
			exp.setSmPersonnel(getManagedBean().getSelectedShareHolder());
			getManagedBean().getSelectedShareHolder().getSmStaffExperiences()
					.add(exp);
		} else {
			exp.setSmPersonnel(getManagedBean().getSelectedPersonnel());
			getManagedBean().getSelectedPersonnel().getSmStaffExperiences()
					.add(exp);
		}
	}

	public void addShrHldrAcademicQualification(Boolean isShrHldr) {
		System.out.println(">>>>>> Add Academic Qualification >>>>>>>>>>>>>>>"
				+ isShrHldr);
		SmStaffAcademic aca = new SmStaffAcademic();
		if (isShrHldr) {
			aca.setSmPersonnel(getManagedBean().getSelectedShareHolder());
			getManagedBean().getSelectedShareHolder().getSmStaffAcademics()
					.add(aca);
		} else {
			aca.setSmPersonnel(getManagedBean().getSelectedPersonnel());
			getManagedBean().getSelectedPersonnel().getSmStaffAcademics()
					.add(aca);
		}
	}

	public void addShrHldrProfBody(Boolean isShrHldr) {
		System.out.println(">>>>>>> Add Professional Body >>>>>>>>>>>>>>"
				+ isShrHldr);
		SmAssociationBody assBody = new SmAssociationBody();
		assBody.setPmCertBody(new PmCertBody());
		assBody.setRevNo(1);
		assBody.setSmAppl(getManagedBean().getSmAppl());
		if (isShrHldr) {
			assBody.setSmPersonnel(getManagedBean().getSelectedShareHolder());
			getManagedBean().getSelectedShareHolder().getSmAssociationBodies()
					.add(assBody);
		} else {
			assBody.setSmPersonnel(getManagedBean().getSelectedPersonnel());
			getManagedBean().getSelectedPersonnel().getSmAssociationBodies()
					.add(assBody);
		}
	}

	public void addShrHldrRelatedCompany(Boolean isShrHldr) {
		System.out.println(">>>>>>>> Add Related Company Body >>>>>>>>>>>>>"
				+ isShrHldr);
		SmRelatedCompany com = new SmRelatedCompany();
		if (isShrHldr) {
			com.setSmPersonnel(getManagedBean().getSelectedShareHolder());
			getManagedBean().getSelectedShareHolder().getSmRelatedCompanies()
					.add(com);
		} else {
			com.setSmPersonnel(getManagedBean().getSelectedPersonnel());
			getManagedBean().getSelectedPersonnel().getSmRelatedCompanies()
					.add(com);
		}
	}

	public void deleteSelectedShrHldrJobFunction(Boolean isShrHldr) {
		System.out.println(" ******* The is size is ********* "
				+ getManagedBean().getSelectedShrHldrJobFunctions().size());
		if (getManagedBean().getSelectedShrHldrJobFunctions().size() > 0) {
			for (SmStaffExperience exp : getManagedBean()
					.getSelectedShrHldrJobFunctions()) {
				if (isShrHldr) {
					getManagedBean().getSelectedShareHolder()
							.getSmStaffExperiences().remove(exp);
				} else {

					getManagedBean().getSelectedPersonnel()
							.getSmStaffExperiences().remove(exp);
				}

			}
			getManagedBean().getSelectedShrHldrJobFunctions().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	public void deleteSelectedShrHldrAcademics(Boolean isShrHldr) {
		System.out.println("****** Delete Academics ******" + isShrHldr);
		if (getManagedBean().getSelectedShrHldrAcademics().size() > 0) {
			for (SmStaffAcademic aca : getManagedBean()
					.getSelectedShrHldrAcademics()) {
				if (isShrHldr) {
					getManagedBean().getSelectedShareHolder()
							.getSmStaffAcademics().remove(aca);
				} else {
					getManagedBean().getSelectedPersonnel()
							.getSmStaffAcademics().remove(aca);
				}
			}
			getManagedBean().getSelectedShrHldrAcademics().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	public void deleteSelectedShrHldrProfBodies(Boolean isShrHldr) {
		System.out.println("****** Delete Prof Bodies ******" + isShrHldr);
		if (getManagedBean().getSelectedShrHldrProfBodies().size() > 0) {
			for (SmAssociationBody prof : getManagedBean()
					.getSelectedShrHldrProfBodies()) {
				if (isShrHldr) {
					getManagedBean().getSelectedShareHolder()
							.getSmAssociationBodies().remove(prof);
				} else {
					getManagedBean().getSelectedPersonnel()
							.getSmAssociationBodies().remove(prof);
				}
			}
			getManagedBean().getSelectedShrHldrProfBodies().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	public void deleteSelectedShrHldrRelatedCompanies(Boolean isShrHldr) {
		System.out
				.println("****** Delete Related Companies ******" + isShrHldr);
		if (getManagedBean().getSelectedShrHldrRelatedCompanies().size() > 0) {
			for (SmRelatedCompany rel : getManagedBean()
					.getSelectedShrHldrRelatedCompanies()) {
				if (isShrHldr) {
					getManagedBean().getSelectedShareHolder()
							.getSmRelatedCompanies().remove(rel);
				} else {
					getManagedBean().getSelectedPersonnel()
							.getSmRelatedCompanies().remove(rel);
				}
			}
			getManagedBean().getSelectedShrHldrRelatedCompanies().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	public void assignSelShareHolderAddress(SmPersonnel shareHolderBean) {

		SmPersonnelAddress perm = new SmPersonnelAddress();
		perm.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM);
		perm.setSmAddress(setDefaultAddr());
		perm.setSmPersonnel(shareHolderBean);

		SmPersonnelAddress mail = new SmPersonnelAddress();
		mail.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL);
		mail.setSmAddress(setDefaultAddr());
		mail.setSmPersonnel(shareHolderBean);

		SmPersonnelAddress off = new SmPersonnelAddress();
		off.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE);
		off.setSmAddress(setDefaultAddr());
		off.setSmPersonnel(shareHolderBean);

		SmPersonnelAddress res = new SmPersonnelAddress();
		res.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES);
		res.setSmAddress(setDefaultAddr());
		res.setSmPersonnel(shareHolderBean);

		getManagedBean().setShareHolderPermAddr(perm);
		getManagedBean().setShareHolderMailAddr(mail);
		getManagedBean().setShareHolderOffAddr(off);
		getManagedBean().setShareHolderResdAddr(res);

		if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {
			System.out.println("******** New Address fields ********"
					+ shareHolderBean.getName());

			// TODO remove if necessary
			List<PmState> defStates = new ArrayList<PmState>();
			defStates = retrievePmStateList(perm.getSmAddress().getPmCountry()
					.getCountryId());

			getManagedBean().setShrHldrPermStates(defStates);
			getManagedBean().setShrHldrMailStates(defStates);
			getManagedBean().setShrHldrResdStates(defStates);
			getManagedBean().setShrHldrOffStates(defStates);

			// getManagedBean().setPersonnelPermAddr(perm);
			// getManagedBean().setPersonnelMailAddr(mail);
			// getManagedBean().setPersonnelOffAddr(off);
			// getManagedBean().setPersonnelResdAddr(res);
			// getManagedBean().setPersnlPermStates(defStates);
			// getManagedBean().setPersnlMailStates(defStates);
			// getManagedBean().setPersnlResdStates(defStates);
			// getManagedBean().setPersnlOffStates(defStates);

		} else {
			System.out.println("******* Already contains address ***********");
			for (SmPersonnelAddress addr : shareHolderBean
					.getSmPersonnelAddresses()) {
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
					getManagedBean().setShareHolderPermAddr(addr);
					// if (isShrHldr) {
					//
					// } else {
					// getManagedBean().setPersonnelPermAddr(addr);
					// }
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
					getManagedBean().setShareHolderMailAddr(addr);
					// if (isShrHldr) {
					//
					// } else {
					// getManagedBean().setPersonnelMailAddr(addr);
					// }
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
					getManagedBean().setShareHolderResdAddr(addr);
					// if (isShrHldr) {
					//
					// } else {
					// getManagedBean().setPersonnelResdAddr(addr);
					// }
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
					getManagedBean().setShareHolderOffAddr(addr);
					// if (isShrHldr) {
					//
					// } else {
					// getManagedBean().setPersonnelOffAddr(addr);
					// }
				}
			}
		}
		if (shareHolderBean.isEquityOwner()) {
			getManagedBean().setSelectedShareHolder(shareHolderBean);
		} else {
			getManagedBean().setSelectedPersonnel(shareHolderBean);
		}
		getManagedBean().setAddrUpdatePersonnel(shareHolderBean);

	}

	// -- Process Common for both Personnel and Share Holder Start --
	// -- Process Personnel Start

	public void addOrUpdatePersonnelToSmAppl() throws ParseException {
		System.out.println("*** The personnel Info ****"
				+ getManagedBean().getSelectedPersonnel().getName());
		boolean validPer = true;
		// if (getManagedBean().getSelectedPersonnel().getAuthorizedFlg() == 1)
		// {
		// validPer = validateAuthPersonLimit();
		// }
		validPer = checkValidUser(getManagedBean().getSelectedPersonnel());
		if (validPer) {
			if (getManagedBean().getSmAppl().getSmPersonnels()
					.contains(getManagedBean().getSelectedPersonnel())) {
				getManagedBean()
						.getSmAppl()
						.getSmPersonnels()
						.set(getManagedBean()
								.getSmAppl()
								.getSmPersonnels()
								.indexOf(
										getManagedBean().getSelectedPersonnel()),
								getManagedBean().getSelectedPersonnel());

				System.out.println(" **** THIS IS AN UPDATE Personnel ******");
			} else {

				getManagedBean().getSmAppl().getSmPersonnels()
						.add(getManagedBean().getSelectedPersonnel());
			}

			IMofRegistrationSO mofRegistrationSO = null;

			try {
				System.out.println("updating personnel into database");
				mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
						.getInstance().getServiceObject("sm",
								IMofRegistrationSO.class);

				MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();

				reqDto.setSmAppl(this.getManagedBean().getSmAppl());

				MofRegistrationResDTO resDTO = mofRegistrationSO
						.updateApplication(reqDto);
				// getManagedBean().setSmAppl(resDTO.getSmAppl());
			} finally {
				mofRegistrationSO = null;
				retriveLatestApplAfterUpdate("");
			}
			// updateSmAppl(getManagedBean().getSmAppl().getApplType(),"Def");
			calcPersonnelListCount();
			RequestContext.getCurrentInstance()
					.execute("directorDialog.hide()");
		}
	}

	public void assignSelPersonnel(SmPersonnel shareHolderBean) {
		if (shareHolderBean.getAuthorizedFlg() == 1) {
			getManagedBean().setAuthPerCount(0);
		} else {
			getManagedBean().setAuthPerCount(getAuthUserCount());
		}
		getManagedBean().setSelectedPersonnel(shareHolderBean);
	}

	public void assignNewPersonnel(String personelType) {
		System.out.println("******* ADD NEW Personnel *************"
				+ personelType);
		getManagedBean().setAuthPerCount(getAuthUserCount());

		SmPersonnel personnel = new SmPersonnel();
		personnel.setEquityOwner(false);
		if (personelType
				.equalsIgnoreCase(SupplierManagementConst.PERSONNEL_TYPE_DIRECTOR)) {
			personnel.setDirOrHghrMngmt(true);
		} else {
			personnel.setDirOrHghrMngmt(false);
		}
		setDefaultPersonnelValues(personnel);
		System.out.println("*** Bumi Flage **** " + personnel.getBumiFlg());
		getManagedBean().setSelectedPersonnel(personnel);

	}

	public void addOrUpdatePeronnelAddress(SmPersonnel persBean) {/*
																 * if (persBean.
																 * getSmPersonnelAddresses
																 * ().isEmpty())
																 * { System.out.
																 * println(
																 * "******** Empty list ********"
																 * +
																 * persBean.getName
																 * ());
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().add(
																 * getManagedBean
																 * ().
																 * getPersonnelResdAddr
																 * ());
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().add(
																 * getManagedBean
																 * ().
																 * getPersonnelPermAddr
																 * ());
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().add(
																 * getManagedBean
																 * ().
																 * getPersonnelOffAddr
																 * ());
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().add(
																 * getManagedBean
																 * ().
																 * getPersonnelMailAddr
																 * ());
																 * getManagedBean
																 * ().
																 * setSelectedPersonnel
																 * (persBean); }
																 * else {
																 * System.out
																 * .println(
																 * "******** Exisiting Share holders address **********"
																 * +
																 * persBean.getName
																 * ()); for (
																 * SmPersonnelAddress
																 * addr :
																 * persBean.
																 * getSmPersonnelAddresses
																 * ()) { if
																 * (addr
																 * .getAddressType
																 * ().
																 * equalsIgnoreCase
																 * (
																 * SupplierManagementConst
																 * .
																 * SM_PERSONNEL_ADDR_TYPE_MAIL
																 * )) {
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().set(
																 * persBean.
																 * getSmPersonnelAddresses
																 * (
																 * ).indexOf(addr
																 * ),
																 * getManagedBean
																 * ().
																 * getPersonnelMailAddr
																 * ()); } if
																 * (addr
																 * .getAddressType
																 * ().
																 * equalsIgnoreCase
																 * (
																 * SupplierManagementConst
																 * .
																 * SM_PERSONNEL_ADDR_TYPE_OFFICE
																 * )) {
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().set(
																 * persBean.
																 * getSmPersonnelAddresses
																 * (
																 * ).indexOf(addr
																 * ),
																 * getManagedBean
																 * ().
																 * getPersonnelOffAddr
																 * ()); } if
																 * (addr
																 * .getAddressType
																 * ().
																 * equalsIgnoreCase
																 * (
																 * SupplierManagementConst
																 * .
																 * SM_PERSONNEL_ADDR_TYPE_RES
																 * )) {
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().set(
																 * persBean.
																 * getSmPersonnelAddresses
																 * (
																 * ).indexOf(addr
																 * ),
																 * getManagedBean
																 * ().
																 * getPersonnelResdAddr
																 * ()); } if
																 * (addr
																 * .getAddressType
																 * ().
																 * equalsIgnoreCase
																 * (
																 * SupplierManagementConst
																 * .
																 * SM_PERSONNEL_ADDR_TYPE_PERM
																 * )) {
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().set(
																 * persBean.
																 * getSmPersonnelAddresses
																 * (
																 * ).indexOf(addr
																 * ),
																 * getManagedBean
																 * ().
																 * getPersonnelPermAddr
																 * ()); } }
																 * getManagedBean
																 * ().
																 * setSelectedPersonnel
																 * (persBean);
																 * System
																 * .out.println(
																 * "*** After set addr value ***"
																 * +
																 * getManagedBean
																 * ().
																 * getSelectedPersonnel
																 * (
																 * ).getName());
																 * } if
																 * (getManagedBean
																 * (
																 * ).getSmAppl()
																 * .
																 * getSmPersonnels
																 * () .contains(
																 * getManagedBean
																 * ().
																 * getSelectedPersonnel
																 * ())) {
																 * 
																 * System.out
																 * .println(
																 * " **** THIS IS AN UPDATE Personnel Address******"
																 * );
																 * getManagedBean
																 * ()
																 * .getSmAppl()
																 * .
																 * getSmPersonnels
																 * () .set(
																 * getManagedBean
																 * (
																 * ).getSmAppl()
																 * .
																 * getSmPersonnels
																 * () .indexOf(
																 * getManagedBean
																 * ().
																 * getSelectedPersonnel
																 * ()),
																 * getManagedBean
																 * ().
																 * getSelectedPersonnel
																 * ()); } else {
																 * 
																 * getManagedBean(
																 * )
																 * .getSmAppl().
																 * getSmPersonnels
																 * () .add(
																 * getManagedBean
																 * ().
																 * getSelectedPersonnel
																 * ()); }
																 */
	}

	public void deleteSelectedPersonnel() {

		System.out.println("*** Delete Personnels ******* "
				+ getManagedBean().getSelectedPersonnels().size());
		if (getManagedBean().getSelectedPersonnels().size() > 0) {
			for (SmPersonnel per : getManagedBean().getSelectedPersonnels()) {
				getManagedBean().getSmAppl().getSmPersonnels().remove(per);
			}
			getManagedBean().getSelectedPersonnels().clear();
			calcPersonnelListCount();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// -- Process Personnel End

	// Process Bank Info Start
	public void addOrUpdateBankInfoToSmAppl() {
		// TODO to be changed later
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
		System.out.println("******* Selected value Branch Before******"
				+ getManagedBean().getSelectedBank().getSmSupplierBranch()
						.getSupplierBranchId());
		if (getManagedBean().getSelectedBank().getSmSupplierBranch()
				.getSupplierBranchId() == null
				|| getManagedBean().getSelectedBank().getSmSupplierBranch()
						.getSupplierBranchId().equals("")) {
			System.out.println("******* Null value Branch ******");
			getManagedBean().getSelectedBank().setSmSupplierBranch(
					new SmSupplierBranch());
		} else {
			System.out.println("******* Selected value Branch ******"
					+ getManagedBean().getSelectedBank().getSmSupplierBranch()
							.getSupplierBranchId());
			for (SmSupplierBranch brn : getManagedBean().getSmAppl()
					.getSmSupplierBranches()) {
				if (brn.getSupplierBranchId().equals(
						getManagedBean().getSelectedBank()
								.getSmSupplierBranch().getSupplierBranchId())) {
					getManagedBean().getSelectedBank().setSmSupplierBranch(brn);
				}
			}
		}

		if (getManagedBean().getSmAppl().getSmSupplierBanks()
				.contains(getManagedBean().getSelectedBank())) {

			System.out.println(" **** THIS IS AN UPDATE Bank******");
			System.out.println("Before Update ===== "
					+ getManagedBean().getSelectedBank()
							.getSmChequeSignatories().size());
		} else {
			System.out.println("Before add ===== "
					+ getManagedBean().getSelectedBank()
							.getSmChequeSignatories().size());
			getManagedBean().getSmAppl().getSmSupplierBanks()
					.add(getManagedBean().getSelectedBank());
		}
	}

	public void assignSelBank(SmSupplierBank shareBankBean) {
		getManagedBean().setChequeSingPerId(null);
		System.out.println("******* SIZE ********** "
				+ shareBankBean.getSmChequeSignatories().size());
		Long[] chequSigns = new Long[shareBankBean.getSmChequeSignatories()
				.size()];
		for (int i = 0; i < chequSigns.length; i++) {
			chequSigns[i] = shareBankBean.getSmChequeSignatories().get(i)
					.getSmPersonnel().getPersonnelId();

		}
		getManagedBean().setChequeSingPerId(chequSigns);
		// shareBankBean.getSmChequeSignatories().clear();
		getManagedBean().setSelectedBank(shareBankBean);
	}

	public void deleteSelectedBanks() {

		System.out.println("*** Delete Banks  ******* "
				+ getManagedBean().getSelectedBanks().size());
		if (getManagedBean().getSelectedBanks().size() > 0) {
			for (SmSupplierBank bank : getManagedBean().getSelectedBanks()) {
				getManagedBean().getSmAppl().getSmSupplierBanks().remove(bank);
			}
			getManagedBean().getSelectedBranches().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	public void viewSignatories(SmSupplierBank signAcctBean) {
		getManagedBean().setSelectedSignatoryBankAcct(signAcctBean);
	}

	public void assignNewBank() {
		System.out.println("******* ADD NEW Bank Info *************");
		getManagedBean().setChequeSingPerId(null);
		SmSupplierBank bank = new SmSupplierBank();
		bank.setSmAddress(setDefaultAddr());
		bank.setSmSupplierBranch(new SmSupplierBranch());
		bank.setSmAppl(getManagedBean().getSmAppl());
		bank.setPmFinancialOrg(new PmFinancialOrg());
		// bank.setPmbank(new PmBank());
		getManagedBean().setBankStates(
				retrievePmStateList(getManagedBean().getSelectedBank()
						.getSmAddress().getPmCountry().getCountryId()));
		bank.setSmChequeSignatories(new ArrayList<SmChequeSignatory>());
		getManagedBean().setSelectedBank(bank);
	}

	// -- Process Bank Info End
	/**
	 * Test BPM Connect
	 */
	public boolean initialBpmTask() {
		IMofRegistrationSO regSo = null;
		try {
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			return regSo.initialBpmTask();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			regSo = null;
		}

	}

	/**
	 * Make a call to the BPM for MofRegistration
	 */
	public void initiateSmApplicationTask() {
		IMofRegistrationSO regSo = null;
		try {
			// TODO to be used while doing the iteration2
			// SmAppl bpmAppl = getManagedBean().getSmSupplier().getSmAppls()
			// .get(0);
			SmAppl bpmAppl = getManagedBean().getSmAppl();
			if (getManagedBean().getSmAppl().isResubmit()) {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_RE_REVIEW);
			} else {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_VERIFICATION);
			}
			getManagedBean().getSmAppl().setApplSubmitDate(new Date());
			updateSmAppl(bpmAppl.getApplType(),
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC, "");
			// }
			System.out.println(" **** SUPPORTING DOC MODE ****"
					+ bpmAppl.getSupportingDocMode());

			if (bpmAppl.getSupportingDocMode() == null) {
				bpmAppl.setSupportingDocMode("O");
			}
			MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(bpmAppl,
					SupplierManagementConst.SM_APPL_BPM_OUTCOME_SUBMIT);
			System.out.println(" ****** The task list id ss *********"
					+ getManagedBean().getTaskId());
			if (getManagedBean().getTaskId() != null
					&& !getManagedBean().getTaskId().equalsIgnoreCase("")) {
				System.out.println("***** Update the task *******");
				reqDTO.setUpdateTask(true);
				reqDTO.setTaskId(getManagedBean().getTaskId());
			} else {
				reqDTO.setUpdateTask(false);
			}
			MofRegistrationResDTO resDTO;
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			resDTO = regSo.initiateSmApplicationTask(reqDTO);
		} finally {
			regSo = null;
		}

	}

	public String submitBumiApplToBpm() {
		try {
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
																	ParameterConst.NEW_SM_APPLN_FOR_BUMI),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.BUMI_APPL_FAIL;
		}
	}

	public String submitUpdateMofApplToBpm() {
		try {
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
																	ParameterConst.NEW_SM_APPLN_FOR_UPDATE),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.UPDATE_MOF_APPL_FAIL;
		}
	}

	public String submitRenewMofApplToBpm() {
		try {
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
																	ParameterConst.NEW_SM_APPLN_FOR_RENEW),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.RENEW_MOF_APPL_FAIL;
		}
	}

	public String submitAddnlCategoryApplToBpm() {
		try {
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
																	ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.ADDNLCAT_APPL_FAIL;
		}
	}

	public String submitMofApplToBpm() {
		try {
			System.out.println("******* BPM START **********");
			initiateSmApplicationTask();
			System.out.println("******* BPM COMPLETED **********");
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
																	ParameterConst.NEW_SM_APPLN_FOR_MOF),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			e.printStackTrace();
			return SupplierManagementConst.MOF_APPL_FAIL;
		}
	}

	public String submitCancelMofApplToBpm() {
		System.out.println(" Call Cancel ---------------->>>>>>>>>>");
		try {
			SmRemark remark = new SmRemark();
			remark.setDocId(getManagedBean().getSmAppl().getApplId());
			remark.setDocType(ParameterConst.NEW_SM_APPLN_FOR_CANCEL);
			// TODO set the role
			remark.setRemark(getManagedBean().getSupplierRemarks());
			saveSmRemarks(remark);
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

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.PROCEED_CANCEL_MOF_APPLN;
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
			reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			if (this.getManagedBean().getSmAppl().getSupportingDocMode()
					.equalsIgnoreCase("O")) {
				reqDTO.setOnlineDoc(true);
			} else {
				reqDTO.setOnlineDoc(false);
			}

			MofRegistrationResDTO resDTO;
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			resDTO = regSo.initiateSmDelPendingApplicationTask(reqDTO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700005,
									new String[] {
											"Delete pending application",
											getManagedBean().getSmAppl()
													.getApplNo() },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.PROCEED_DEL_PENDING_MOF_APPLN;
		} finally {
			regSo = null;
		}
	}

	public String saveApplicationAsDraft(String applType, String currentPageType) {
		System.out.println("****** Save As Draft **********" + currentPageType);
		boolean valid = false;
		try {
			valid = validateBeforeSave(applType, currentPageType);
			if (valid) {
				valid = intializeSaveDraftBpm(currentPageType);
			}
			if (!valid) {
				System.out.println("******* Process No Valid ******");
				throw new EpException(MessageConst.ERROR_722519);
			} else {
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

				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} finally {

		}
	}

	@SuppressWarnings("finally")
	private boolean intializeSaveDraftBpm(String currentPageType) {
		System.out.println("*********  intializeSaveDraftBpm ************* ");
		IMofRegistrationSO regSo = null;
		boolean valid = false;
		try {
			SmAppl bpmAppl = getManagedBean().getSmAppl();

			if (bpmAppl.getSupportingDocMode() == null) {
				bpmAppl.setSupportingDocMode("O");
			}
			String outcome = "";
			if (!getManagedBean().isDraftedAppl()) {
				outcome = SupplierManagementConst.SM_APPL_BPM_OUTCOME_DRAFT;
			} else {
				outcome = "";
			}
			MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(bpmAppl,
					outcome);
			if (reqDTO.getOutcome().equals("")) {
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
			}
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			resDTO = regSo.initiateSmApplSaveAsDraftTask(reqDTO);
			if (resDTO != null) {
				System.out.println("******* From SO ******** "
						+ resDTO.isBpmSuccess());
				valid = resDTO.isBpmSuccess();
			} else {
				valid = false;
			}
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_722519);
		} finally {
			regSo = null;
			if (valid) {
				return retriveLatestApplAfterUpdate(currentPageType);
			} else {
				return valid;
			}

		}

	}

	private MofRegistrationReqDTO setMofRegistrationReqDTO(SmAppl bpmAppl,
			String outcome) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();

		// TODO Remove the hard code later
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
		reqDTO.setBaseOfficeState(getManagedBean().getSmAppl()
				.getSmCompanyBasic().getSmCompanyAddresses().get(0)
				.getSmAddress().getPmState().getStateName().toUpperCase());

		if (this.getManagedBean().getSmAppl().getSupportingDocMode()
				.equalsIgnoreCase("O")) {
			reqDTO.setOnlineDoc(true);
		} else {
			reqDTO.setOnlineDoc(false);
		}

		return reqDTO;
	}

	public String submitPOorApproverReviewToBPM(String res) {
		IReviewApplicationSO reqSO = null;
		String retMsg = null;
		try {

			Long pmStatId = getManagedBean().getSmAppl().getPmStatus()
					.getStatusId();
			retriveCurrentAppl();
			System.out.println(" **** After retrieve ***"
					+ getManagedBean().getSmAppl().getPmStatus().getStatusId());
			log.info("----------------------Submit PO / Approver Decision For BPM-------------------------"
					+ res);
			System.out.println(" **** The Value from screen of approval ***"
					+ pmStatId);
			ReviewApplicationReqDTO reqDTO = new ReviewApplicationReqDTO();
			reqSO = (IReviewApplicationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IReviewApplicationSO.class);
			reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
			reqDTO.setApplNo(this.getManagedBean().getSmAppl().getApplNo());
			reqDTO.setOutcome(res);
			reqDTO.setSuppId(this.getManagedBean().getSmAppl().getSmSupplier()
					.getSupplierId());
			reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			// reqDTO.setUserName(new FacesScopes().getUserScreenName());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());

			if (this.getManagedBean().getSmAppl().getSupportingDocMode()
					.equalsIgnoreCase("O")) {
				reqDTO.setOnlineDoc(true);
			} else {
				reqDTO.setOnlineDoc(false);
			}

			boolean validSiteVisit = true;

			if (res.equalsIgnoreCase("SITE_VISIT")
					|| res.equalsIgnoreCase("SITE_VISIT_AFTER_APPROVE")) {
				validSiteVisit = siteVisitDataUpdate();
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_ASSIGNMENT);
			}
			if (res.equalsIgnoreCase("Inquiry")) {
				setApplPmStatus(SupplierManagementConst.PM_STATUS_PENDING_REPLY_INQUIRY_BY_PROCESSING_OFFICER);
			}
			if (res.equalsIgnoreCase("Reject")) {
				setSmApplRejectionReasons();
				if (getManagedBean().isPoUser()) {
					setApplPmStatus(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER);
				} else if (getManagedBean().isApproverUser()) {
					setApplPmStatus(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER);
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
											getManagedBean().getSmAppl()
													.getPmRegStatus()
													.isBumiAppl());
								}
							}
						}
					}
					reqDTO.setPaymentReqd(setPaymentMode());
				}
			}

			System.out.println("Before final decision ---"
					+ getManagedBean().getSmAppl().getPmStatus().getStatusId());
			validSiteVisit = testUpdateSmAppl(getManagedBean().getSmAppl()
					.getApplType(),
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC, "", true);
			if (res.equalsIgnoreCase("Approve")) {
				validSiteVisit = updateSmSupplierInfo(getManagedBean()
						.getSmAppl().getApplType());
			}
			if (validSiteVisit) {
				ReviewApplicationResDTO resDTO = reqSO
						.submitPOorApproverReviewToBPM(reqDTO);
				String msgConst = "";
				// if (getManagedBean().isApproverUser()) {
				// System.out
				// .println(" **** Final Decision from Approver ****");
				// if (res.equalsIgnoreCase("Approve")) {
				// msgConst = MessageConst.INFO_700007;
				// } else if (res.equalsIgnoreCase("Reject")) {
				// SmEmailNotificationHelper
				// .sendApplicaitonRejectNotifMail(getManagedBean()
				// .getSmAppl());
				// msgConst = MessageConst.INFO_700008;
				// }
				// return SupplierManagementConst.APPROVER_DECISION_ACK;
				// }
				// else
				if (getManagedBean().isPoUser()
						|| getManagedBean().isApproverUser()) {
					System.out.println(" **** Final Decision from PO ****");
					if (res.equalsIgnoreCase("Approve")) {
						msgConst = MessageConst.INFO_700007;
					} else if (res.equalsIgnoreCase("Reject")) {
						SmEmailNotificationHelper
								.sendApplicaitonRejectNotifMail(
										getManagedBean().getSmAppl(),
										new FacesScopes().getUserSessionBean()
												.getUserId());
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
																			setParameterConst(getManagedBean()
																					.getSmAppl()
																					.getApplType())),
																	getManagedBean()
																			.getSmAppl()
																			.getApplNo() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
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
			// e.printStackTrace();
			retMsg = null;
			throw new EpException(MessageConst.ERROR_722519);
		} finally {
			reqSO = null;
		}
		return retMsg;
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
		System.out.println(" **** Enter to  verified all ***** "
				+ isVerifiedAll);
		SmApplSectionReview sec = new SmApplSectionReview();

		if (getManagedBean().isPoUser()) {
			System.out.println(" *** PO VERIFIED ALL *******");
			sec = getManagedBean().getPoSecReview();
			getManagedBean().setPoSecReview(
					setVerificationValues(sec, isVerifiedAll));
		} else if (getManagedBean().isApproverUser()) {
			System.out.println(" *** APPROVER VERIFIED ALL *******");
			sec = getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					setVerificationValues(sec, isVerifiedAll));
		}

	}

	public void changeVerifySection(Boolean isSectionVerified) {
		System.out.println(" **** Enter to  verified Section ***** "
				+ isSectionVerified);
		// SmApplSectionReview sec = new SmApplSectionReview();

		if (getManagedBean().isPoUser()) {
			System.out.println(" *** PO VERIFIED Section *******");
			getManagedBean().getPoSecReview();
			System.out.println(" *** After change ***"
					+ getManagedBean().getPoSecReview().isBasicVerified());
			getManagedBean().setPoSecReview(
					assignVerificationCount(getManagedBean().getPoSecReview(),
							isSectionVerified));
		} else if (getManagedBean().isApproverUser()) {
			System.out.println(" *** APPROVER VERIFIED Section *******");
			getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					assignVerificationCount(getManagedBean()
							.getApprvrSecReview(), isSectionVerified));
		}
	}

	private SmApplSectionReview assignVerificationCount(
			SmApplSectionReview sec, Boolean isSectionVerified) {
		System.out.println("**** Intial Check Count ==== "
				+ sec.getCheckCount());
		if (isSectionVerified) {
			sec.setCheckCount(sec.getCheckCount() + 1);

		} else {
			sec.setCheckCount(sec.getCheckCount() - 1);

		}
		System.out.println("**** Count After checking **** "
				+ sec.getCheckCount());
		if (sec.getCheckCount() >= getManagedBean().getMaxReviewCount()) {
			sec.setVerifiedAll(Boolean.TRUE);
		} else {
			sec.setVerifiedAll(Boolean.FALSE);
		}

		return sec;
	}

	private SmApplSectionReview setVerificationValues(SmApplSectionReview sec,
			Boolean isVerifiedAll) {

		if (isVerifiedAll) {
			sec.setCheckCount(getManagedBean().getMaxReviewCount());

		} else {
			sec.setCheckCount(0);

		}
		sec.setBasicVerified(isVerifiedAll);
		sec.setOfficeFacilityVerfied(isVerifiedAll);
		sec.setStaffVerified(isVerifiedAll);

		sec.setTrnsNetworkVerified(isVerifiedAll);
		sec.setBranchVerified(isVerifiedAll);
		sec.setAssociateVerified(isVerifiedAll);

		sec.setCertVerified(isVerifiedAll);
		sec.setExperienceVerified(isVerifiedAll);
		sec.setCaptialVerified(isVerifiedAll);

		sec.setShareholderVerifed(isVerifiedAll);
		sec.setEquityCompVerifed(isVerifiedAll);
		sec.setPersonnelVerified(isVerifiedAll);

		sec.setBankVerified(isVerifiedAll);
		sec.setCategoryVerified(isVerifiedAll);
		sec.setSitevisitVerified(isVerifiedAll);
		sec.setFactoryVerify(isVerifiedAll);
		sec.setManufacturingVerified(isVerifiedAll);
		sec.setMachineryVerified(isVerifiedAll);
		sec.setLicenseVerified(isVerifiedAll);
		sec.setAssocBodyVerified(isVerifiedAll);

		return sec;
	}

	private SmAddress setDefaultAddr() {
		SmAddress addr = new SmAddress();
		PmCountry cntry = new PmCountry();
		cntry.setCountryId(1l);
		addr.setPmCity(new PmCity());
		addr.setPmCountry(cntry);
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());

		return addr;
	}

	private List<SmStaffTotal> getStaffList(String supplierType) {
		List<SmStaffTotal> staffList = new ArrayList<SmStaffTotal>();
		System.out.println("****  getStaffList *********" + supplierType);
		SmAppl app = getManagedBean().getSmAppl();
		if (supplierType.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			SmStaffTotal stf = new SmStaffTotal();
			stf.setRevNo(new BigDecimal(1));
			stf.setStaffCategory("C");
			stf.setSmAppl(app);
			staffList.add(stf);
		} else if (supplierType
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			SmStaffTotal stf1 = new SmStaffTotal();
			stf1.setStaffCategory("P");
			stf1.setSmAppl(app);
			stf1.setRevNo(new BigDecimal(1));
			staffList.add(stf1);
			SmStaffTotal stf2 = new SmStaffTotal();
			stf2.setStaffCategory("S");
			stf2.setSmAppl(app);
			stf2.setRevNo(new BigDecimal(1));
			staffList.add(stf2);
			SmStaffTotal stf3 = new SmStaffTotal();
			stf3.setStaffCategory("L");
			stf3.setSmAppl(app);
			stf3.setRevNo(new BigDecimal(1));
			staffList.add(stf3);
		}
		return staffList;
	}

	private void retrievePmCountryList() {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();

		this.getManagedBean().setCountryLs(resDTO.getCountryList());
	}

	private void saveSmRemarks(SmRemark remarks) {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setSmRemark(remarks);
		ISmRemarksSO reqSO = (ISmRemarksSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISmRemarksSO.class);
		SmRemaksResDTO resDTO = reqSO.saveSmRemarks(reqDTO);

	}

	private PmCountry retrievePmCountryById(Long countryId) {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryId(countryId);
		CountryResDTO resDTO = reqSO.retrieveCountryById(reqDTO);

		return resDTO.getPmCountry();
	}

	private PmParameter retrievePmParameterById(Long paramId) {
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(paramId);
		ParameterResDTO resDTO = reqSO.retrieveParameterById(reqDTO);

		return resDTO.getPmParameter();
	}

	private void retrieveParentCategoryList() {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL1List();

		this.getManagedBean().setSupplierParentCatList(
				resDTO.getCategoryL1List());
	}

	private void retrievePmCertBodyList() {
		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveCertifiedBodyList();

		this.getManagedBean().setCertBodyLs(resDTO.getCertBodyList());
		System.out.println("****** After getting the cert body list *******"
				+ getManagedBean().getCertBodyLs().size());
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
		System.out.println("******* Selected Value **********"
				+ getManagedBean().getSelectedBank().getAccountPurpose());
		if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("P")) {
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
		} else if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("R")) {
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_REGISTRATION);
		} else if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("B")) {
			// TODO remove this data later to get confirmation
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
		} else {
			this.getManagedBean().setFinOrgLs(new ArrayList<PmFinancialOrg>());
			runQuery = false;
		}
		if (runQuery) {
			IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
					.getInstance()
					.getServiceObject("pm", IFinancialOrgSO.class);
			System.out.println("******* Service Type ********"
					+ reqDTO.getServiceType());
			FinancialOrgResDTO resDTO = reqSO
					.retrieveFinancialOrgListByServiceType(reqDTO);

			this.getManagedBean().setFinOrgLs(resDTO.getFinancialOrgList());
		}
		System.out.println("****** After getting the fin org list *******"
				+ getManagedBean().getFinOrgLs().size());
	}

	public void assignSelectedSupportDoc(SmApplSupportDoc doc) {
		System.out.println("****** Assign Supporting Doc *******");
		getManagedBean().setSupportDoc(doc);
	}

	public void handleSupDocFileAttachment(FileUploadEvent event) {
		System.out.println("********handleFileAttachment********");
		FacesScopes facesScopes = new FacesScopes();
		if (event.getFile() != null) {
			System.out.println("***** File attached *******"
					+ event.getFile().getFileName());
			if (getManagedBean().getSmAppl().getSmApplSupportDocs()
					.contains(getManagedBean().getSupportDoc())) {
				System.out.println("******* Contains selected file ****");
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
				for (SmApplSupportDoc docs : getManagedBean().getSmAppl()
						.getSmApplSupportDocs()) {
					System.out.println(" *** The name is ****"
							+ docs.getSmApplDocFiles().get(0).getFileName());
				}
			}
		}

	}

	private void retrieveUomList() {
		IUomSO reqSO = (IUomSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUomSO.class);
		UomResDTO resDTO = reqSO.retrieveUomList();

		this.getManagedBean().setUomLs(resDTO.getPmUomList());
	}

	private void retrieveRejectReasonList() {
		IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IReasonSO.class);
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonCategory(ReasonCategory.SUPPLIER_REJECTION_REASON
				.value());
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		ReasonResDTO resDTO = reqSO.retrieveReasonDescListByCategory(reqDTO);
		System.out.println(" *** Reject List ****** "
				+ resDTO.getReasonDescList().size());
		this.getManagedBean().setReasonDescList(resDTO.getReasonDescList());
	}

	private void retrieveRegistrationStatusList() {
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		StatusResDTO resDTO = reqSO.retrieveRegStatusList(reqDTO);
		System.out.println(" *** Registration status List ****** "
				+ resDTO.getStatusDescList().size());
		this.getManagedBean().setRegStatusDescList(resDTO.getStatusDescList());
	}

	private void retrievePmProfBodyList() {
		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveProfessionalBodyList();

		this.getManagedBean().setProfBodyLs(resDTO.getCertBodyList());
		System.out.println("****** After getting the prof body list *******"
				+ getManagedBean().getProfBodyLs().size());
	}

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

	private List<PmCategoryL2> retrieveChildCategoryList(long categoryL1Id) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2ListByL1Id(reqDTO);

		return resDTO.getCategoryL2List();
	}

	private List<PmCategoryL3> retrieveSubChildCategoryList(long categoryL2Id) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(categoryL2Id);
		System.out.println("** getSupplierType type ***"
				+ getManagedBean().getSmAppl().getSupplierType());
		System.out.println("** getApplCategoryType type ***"
				+ getManagedBean().getSmAppl().getApplCategoryType());
		// reqDTO.setSupplierTypeCode(getManagedBean().getSmAppl()
		// .getSupplierType());
		// if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
		// reqDTO.setIsSpecialCategory(false);
		// } else {
		// reqDTO.setIsSpecialCategory(true);
		// }
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);

		return resDTO.getCategoryL3List();
	}

	private SmAppl defaultSmAppl(SmAppl appl) {
		// = new SmAppl();
		if (appl.getSmSupplierBranches() == null) {
			appl.setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
		}
		if (appl.getSmAssociateCompanies() == null) {
			appl.setSmAssociateCompanies(new ArrayList<SmAssociateCompany>());
		}
		if (appl.getSmAssociationBodies() == null) {
			appl.setSmAssociationBodies(new ArrayList<SmAssociationBody>());
		}
		if (appl.getSmSupplierCerts() == null) {
			appl.setSmSupplierCerts(new ArrayList<SmSupplierCert>());
		}
		if (appl.getSmEquityCompanies() == null) {
			appl.setSmEquityCompanies(new ArrayList<SmEquityCompany>());
		}
		if (appl.getSmSupplierCategories() == null) {
			appl.setSmSupplierCategories(new ArrayList<SmSupplierCategory>());
		}
		if (appl.getSmFacilities() == null) {
			appl.setSmFacilities(new ArrayList<SmFacility>());
		}
		if (appl.getSmManufacturingDtls() == null) {
			appl.setSmManufacturingDtls(new ArrayList<SmManufacturingDtl>());
		}
		if (appl.getSmMachineDtls() == null) {
			appl.setSmMachineDtls(new ArrayList<SmMachineDtl>());
		}
		if (appl.getSmPersonnels() == null) {
			appl.setSmPersonnels(new ArrayList<SmPersonnel>());
		}
		if (appl.getSmSupplierBanks() == null) {
			appl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}
		if (appl.getSmApplRejectReasons() == null) {
			appl.setSmApplRejectReasons(new ArrayList<SmApplRejectReason>());
		}

		return appl;
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
			// TODO remove the clone later (Problem at company basic clone
			// to be fixed)
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.cloneSmApplicationDraft(reqDto);
			// MofRegistrationResDTO resDTO = mofRegistrationSO
			// .retrieveSupplierProfile(reqDto);
			return resDTO;

		} catch (Exception e) {
			System.out.println("**** THIS IS THE EXCEPTION IN CLONE APPL ****");
			e.printStackTrace();
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}
	}

	private MofRegistrationResDTO retriveSupplierById(long id) {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSupplierId(id);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveSupplierProfile(reqDto);
			return resDTO;

		} catch (Exception e) {
			System.out
					.println("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}
	}

	private MofRegistrationResDTO retriveLatestDraftApplication(
			SmSupplier supplier, String applType) {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSmSupplier(supplier);
			reqDto.setAppType(applType);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveLatestSmApplicationDraft(reqDto);
			return resDTO;

		} catch (Exception e) {
			System.out
					.println("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			e.printStackTrace();
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}
	}

	private String generateApplicationNum(String applType) {
		return null;

	}

	private int assignMaxReviewCount(String appType, String applCatType,
			String supplierType) {
		int i = 0;
		switch (appType) {

		case "N":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.NEW_APPL_SPLCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.NEW_APPL_NORMCAT_REVIEW_MAXCOUNT;
			}
			break;
		case "A":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.ADDLN_CATCODES_APPL_SPLCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.ADDLN_CATCODES_APPL_NORMCAT_REVIEW_MAXCOUNT;
			}
			break;
		case "B":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.BUMI_APPL_SPLCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.BUMI_APPL_NORMCAT_REVIEW_MAXCOUNT;
			}
			break;
		case "U":
			i = SupplierManagementConst.UPDATE_MOF_APPL_REVIEW_MAXCOUNT;
			break;
		case "R":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.RENEW_APPL_NORMCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.RENEW_APPL_SPLCAT_REVIEW_MAXCOUNT;
			}
			break;
		default:
			break;

		}
		if (supplierType
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			i = i + 1;
		}
		return i;
	}

	private String setNavigationForAppl(String appType) {
		String s = null;
		switch (appType) {

		case "N":
			s = SupplierManagementConst.PROCEED_MOF_REG;
			break;
		case "A":
			s = SupplierManagementConst.PROCEED_ADDTNLCAT_APPLN_SIDEMENU;
			// PROCEED_ADDTNLCAT_MOF_APPLN;
			break;
		case "B":
			s = SupplierManagementConst.PROCEED_BUMI_APPLN_SIDEMENU;
			// PROCEED_BUMI_APPLN;
			break;
		case "U":
			s = SupplierManagementConst.PROCEED_UPDATE_APPLN_SIDEMENU;
			// PROCEED_UPDATE_MOF_APPLN;
			break;
		case "R":
			s = SupplierManagementConst.PROCEED_RENEW_APPLN_SIDEMENU;
			// PROCEED_RENEW_MOF_APPLN;
			break;
		case "C":
			s = SupplierManagementConst.PROCEED_CANCEL_MOF_APPLN;
			break;
		default:
			break;

		}
		return s;
	}

	private String setExceptionNavigationForAppl(String appType) {
		String s = null;
		switch (appType) {
		case "N":
			s = SupplierManagementConst.DEFAULT_MOF_REG;
			break;
		case "A":
			s = SupplierManagementConst.DEFAULT_ADDTNLCAT_MOF_APPLN;
			break;
		case "B":
			s = SupplierManagementConst.DEFAULT_BUMI_APPLN;
			break;
		case "U":
			s = SupplierManagementConst.DEFAULT_UPDATE_MOF_APPLN;
			break;
		case "R":
			s = SupplierManagementConst.DEFAULT_RENEW_MOF_APPLN;
			break;
		case "C":
			s = SupplierManagementConst.DEFAULT_CANCEL_MOF_APPLN;
			break;
		default:
			break;

		}
		return s;
	}

	private String setParameterConst(String appType) {
		String s = null;
		switch (appType) {
		case "N":
			s = ParameterConst.NEW_SM_APPLN_FOR_MOF;
			break;
		case "A":
			s = ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES;
			break;
		case "B":
			s = ParameterConst.NEW_SM_APPLN_FOR_BUMI;
			break;
		case "U":
			s = ParameterConst.NEW_SM_APPLN_FOR_UPDATE;
			break;
		case "R":
			s = ParameterConst.NEW_SM_APPLN_FOR_RENEW;
			break;
		case "C":
			s = ParameterConst.NEW_SM_APPLN_FOR_CANCEL;
			break;
		default:
			break;

		}
		return s;
	}

	public void populateStatesByCountry(long coutryId, String panelName,
			PmState state, PmDistrict district, PmCity city) {
		try {
			List<PmState> stateLs = this.retrievePmStateList(coutryId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				System.out.println("**** This is Basic Change Country Panel ");
				this.getManagedBean().setBasicCompanyStates(stateLs);
				this.getManagedBean().setBasicCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBasicCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				System.out
						.println("**** This is my correct off fac Change Country Panel ");
				this.getManagedBean().setBasicOffFacStates(stateLs);
				this.getManagedBean().setBasicOffFacDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBasicOffFacCities(
						new ArrayList<PmCity>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				System.out
						.println("**** This is my branch Change Country Panel ");
				this.getManagedBean().setBranchCompanyStates(stateLs);
				this.getManagedBean().setBranchCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBranchCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				System.out
						.println("**** This is my  assoc Change Country Panel ");
				this.getManagedBean().setAssocCompanyStates(stateLs);
				this.getManagedBean().setAssocCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setAssocCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				System.out
						.println("**** This is my equi Change Country Panel ");
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
				System.out.println("**** This is an unknown panel ***** ");
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
		System.out.println("********** Panel value from page *********"
				+ panelName);
		try {
			List<PmDistrict> distLs = this.retrievePmDistrictList(stateId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				System.out.println("**** This is Basic Change State Panel ");
				this.getManagedBean().setBasicCompanyDistricts(distLs);
				this.getManagedBean().setBasicCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				System.out.println("**** This is Facility Change State Panel ");
				this.getManagedBean().setBasicOffFacDistricts(distLs);
				this.getManagedBean().setBasicOffFacCities(
						new ArrayList<PmCity>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				System.out.println("**** This is Branch Change State Panel ");
				this.getManagedBean().setBranchCompanyDistricts(distLs);
				this.getManagedBean().setBranchCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				System.out.println("**** This is Assoc Change State Panel ");
				this.getManagedBean().setAssocCompanyDistricts(distLs);
				this.getManagedBean().setAssocCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				System.out.println("**** This is Equi Change State Panel ");
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
				System.out.println("**** This is an unknown panel ***** ");
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
				System.out.println("**** This is Basic Change District Panel ");
				this.getManagedBean().setBasicCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				System.out
						.println("**** This is Facility Change District Panel ");
				this.getManagedBean().setBasicOffFacCities(cityLs);
				break;
			case SupplierManagementConst.SM_BRANCH_PANEL:
				System.out
						.println("**** This is Branch Change District Panel ");
				this.getManagedBean().setBranchCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				System.out.println("**** This is Assoc Change District Panel ");
				this.getManagedBean().setAssocCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				System.out
						.println("**** This is Equity Change District Panel ");
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
				System.out.println("**** This is an unknown panel ***** ");
				break;
			}
			resetCity(city);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateChildCategoryByParent(long parentId,
			PmCategoryL2 catL2, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL2> categoryL2s = this
					.retrieveChildCategoryList(parentId);

			this.getManagedBean().setSupplierChildCatList(categoryL2s);
			this.getManagedBean().setSupplierSubChildCatList(
					new ArrayList<PmCategoryL3>());

			resetChildCategory(catL2);
			resetSubChildCategory(catL3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateSubChildCategoryByChild(long childId, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL3> categoryL3s = this
					.retrieveSubChildCategoryList(childId);

			this.getManagedBean().setSupplierSubChildCatList(categoryL3s);

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
		System.out.println("*** Existing Facility******");
		getManagedBean().setApplFactoryFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplDockyardFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplWorkShopFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplStoreFacilities(new ArrayList<SmFacility>());

		for (SmFacility smFac : smFacilities) {
			if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
					|| smFac.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
				getManagedBean().setBasicOffFacStates(
						retrievePmStateList(smFac.getSmAddress().getPmCountry()
								.getCountryId()));
				System.out
						.println("*********Setting Basic company value **********");
				this.getManagedBean().setBasicCompanyFacility(smFac);
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
		System.out.println("*** Existing Personnel******");
		getManagedBean().setSmapplShareHolders(new ArrayList<SmPersonnel>());

		for (SmPersonnel per : perList) {

			if (per.isEquityOwner()) {
				getManagedBean().getSmapplShareHolders().add(per);
			}
		}
		System.out.println("*** After reset *****"
				+ getManagedBean().getSmapplShareHolders().size());

	}

	private void setDefaultPersonnelValues(SmPersonnel shr) {

		shr.setRevNo(1);
		shr.setSmAppl(getManagedBean().getSmAppl());
		shr.setBumiFlg(1);
		shr.setMuslimFlg(1);
		shr.setPmCountry(new PmCountry());
		shr.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		// shr.setSmPersonnelBranches(new ArrayList<SmPersonnelBranch>());
		// SmPersonnelBranch pbr = new SmPersonnelBranch();
		// pbr.setSmPersonnel(shr);
		// pbr.setSmSupplierBranch(new SmSupplierBranch());
		// shr.getSmPersonnelBranches().add(pbr);
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
			System.out.println("** Contains a list of reviews***"
					+ getManagedBean().getSmAppl().getSmApplSectionReviews()
							.size());
			for (SmApplSectionReview rev : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				System.out
						.println("***** Role Type ******" + rev.getRoleType());
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

	private boolean validateAuthPersonLimit() {
		boolean validAuthPerCount = true;
		int authPerCount = 0;
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			if (per.getAuthorizedFlg() == 1) {
				authPerCount++;
			}
		}
		if (authPerCount == 2) {
			validAuthPerCount = false;
			throw new EpException(MessageConst.ERROR_722520);
		}
		return validAuthPerCount;
	}

	private int getAuthUserCount() {
		int authPerCount = 0;
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			if (per.getAuthorizedFlg() == 1) {
				authPerCount = authPerCount + 1;
			}
			// if (per.getSmPersonnelAddresses().isEmpty()) {
			// System.out
			// .println("****** There is record without address ********* ");
			// FacesContext.getCurrentInstance().addMessage(
			// null,
			// new FacesMessage(FacesMessage.SEVERITY_ERROR,
			// "Address Missing : ",
			// "Please provide the address details for "
			// + per.getName()
			// + " before adding a new user"));
			// }
		}
		return authPerCount;
	}

	public void calculateShareHolderPercent() {
		try {
			if (getManagedBean().getSelectedShareHolder().getShareAmt() != null) {
				getManagedBean().getSelectedShareHolder().setShareAmt(
						getManagedBean().getSelectedShareHolder().getShareAmt()
								.setScale(2, RoundingMode.HALF_UP));
			}
			getManagedBean().setEquiPerCapTotal(new BigDecimal(0));
			for (SmPersonnel per : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (per.isEquityOwner()
						&& !per.getPersonnelId().equals(
								getManagedBean().getSelectedShareHolder()
										.getPersonnelId())) {
					getManagedBean().setEquiPerCapTotal(
							getManagedBean().getEquiPerCapTotal()
									.add(per.getShareAmt())
									.setScale(2, RoundingMode.HALF_UP));
				}
			}
			getManagedBean().setEquiPerCapTotal(
					getManagedBean()
							.getEquiPerCapTotal()
							.add(getManagedBean().getSelectedShareHolder()
									.getShareAmt())
							.setScale(2, RoundingMode.HALF_UP));
			if (getManagedBean().getEquiPerCapTotal().compareTo(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getPaidUpCapital()) <= 0) {
				BigDecimal mul = new BigDecimal(100);
				if (getManagedBean().getSmAppl().getSmCompanyBasic()
						.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
					getManagedBean().getSelectedShareHolder()
							.setSharePercentage(
									getManagedBean()
											.getSelectedShareHolder()
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
				getManagedBean().getSelectedShareHolder().setSharePercentage(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				getManagedBean().getSelectedShareHolder().setShareAmt(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				throw new EpException(MessageConst.ERROR_722521, new String[] {
						"Personnel", "personnel" });
			}
		} catch (NumberFormatException ne) {
			// ne.printStackTrace();
			throw new EpException(MessageConst.ERROR_722522);
		}
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
			if (getManagedBean().getEquiCompCapTotal().compareTo(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getPaidUpCapital()) <= 0) {
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
				throw new EpException(MessageConst.ERROR_722521, new String[] {
						"Company", "companies" });
			}
		} catch (NumberFormatException ne) {
			// ne.printStackTrace();
			throw new EpException(MessageConst.ERROR_722522);
		}
	}

	public void calculateCapital() {
		try {
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getBumiEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setBumiEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic().getBumiEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getNonBumiEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setNonBumiEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic().getNonBumiEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getGovtEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setGovtEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic().getGovtEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getGovtUnitrustEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setGovtUnitrustEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic()
										.getGovtUnitrustEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getForeignCompanyEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setForeignCompanyEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic()
										.getForeignCompanyEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getForeignerEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setForeignerEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic()
										.getForeignerEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getLocalCompanyEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setLocalCompanyEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic()
										.getLocalCompanyEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getAuthorizedCapital() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setAuthorizedCapital(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic()
										.getAuthorizedCapital()
										.setScale(2, RoundingMode.HALF_UP));
			}
			getManagedBean()
					.getSmAppl()
					.getSmCompanyBasic()
					.setPaidUpCapital(
							getManagedBean()
									.getSmAppl()
									.getSmCompanyBasic()
									.getBumiEquity()
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getNonBumiEquity())
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getLocalCompanyEquity())
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getForeignCompanyEquity())
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getForeignerEquity())
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getGovtEquity())
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getGovtUnitrustEquity())
									.setScale(2, RoundingMode.HALF_UP));
			System.out.println("**** Paid up capital ****** "
					+ getManagedBean().getSmAppl().getSmCompanyBasic()
							.getPaidUpCapital());
			if (getManagedBean()
					.getSmAppl()
					.getSmCompanyBasic()
					.getPaidUpCapital()
					.compareTo(
							getManagedBean().getSmAppl().getSmCompanyBasic()
									.getAuthorizedCapital()) <= 0) {
				BigDecimal mul = new BigDecimal(100);

				if (getManagedBean().getSmAppl().getSmCompanyBasic()
						.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setBumiPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getBumiEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));

					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setNonBumiPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getNonBumiEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setForiegnerPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getForeignerEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setGovtUTPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getGovtUnitrustEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setGovtEquityPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getGovtEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setForiegnCompPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getForeignCompanyEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setLocalCompPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getLocalCompanyEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
				}

			} else {
				throw new EpException(MessageConst.ERROR_722523);
			}
		} catch (NumberFormatException ne) {
			// ne.printStackTrace();
			throw new EpException(MessageConst.ERROR_722522);
		}
	}

	private boolean checkValidUser(SmPersonnel pers) {
		boolean validUser = true;
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			if (pers.getPersonnelId() == null && pers.equals(per)) {
				System.out.println("****** Same obj before save ");
			} else if (pers.getPersonnelId() != null
					&& pers.getPersonnelId().equals(per.getPersonnelId())) {
				System.out.println("**** Same obj without id ******");
			} else if (per.getIdentificationNo().equalsIgnoreCase(
					pers.getIdentificationNo())) {
				validUser = false;
				throw new EpException(MessageConst.ERROR_722524,
						new String[] { pers.getIdentificationNo() });
			}
		}
		return validUser;
	}

	private boolean validateIctCatCode() {
		boolean validCat = true;
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			int prfCnt = 0;
			int semPrfCnt = 0;
			for (SmPersonnel per : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				PmParameter param = null;
				if (per.getPmStaffRole() != null
						&& per.getPmStaffRole().getPmParameterType() != null) {
					param = per.getPmStaffRole();

				} else if (per.getStaffRoleFlg() != null) {
					param = new PmParameter();
					param = retrievePmParameterById(per.getStaffRoleFlg());
				}

				if (per.getStaffRoleFlg() != null
						&& param != null
						&& param.getPmParameterType()
								.getParameterType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
						&& param.getParameterCode()
								.equals(SupplierManagementConst.PERSONNEL_PROF_STAFF_CONSULTANT_PARAMCODE)
						&& per.getWorkExperienceYear() >= 5) {
					prfCnt = prfCnt + 1;

				} else if (per.getStaffRoleFlg() != null
						&& param != null
						&& param.getPmParameterType()
								.getParameterType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
						&& param.getParameterCode()
								.equals(SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_CONSULTANT_PARAMCODE)
						&& per.getWorkExperienceYear() >= 5) {

					semPrfCnt = semPrfCnt + 1;

				}
				if (prfCnt > 0 && semPrfCnt > 0) {
					break;
				}
			}
			if (semPrfCnt < 1) {
				validCat = false;
				throw new EpException(MessageConst.ERROR_722511,
						new String[] { "semi professional staff" });
			}
			if (prfCnt < 1) {
				validCat = false;
				throw new EpException(MessageConst.ERROR_722511,
						new String[] { "professional staff" });
			}

		}

		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
			System.out.println("***** Validate ICT For contractor ********");

			int ownrCnt = 0;
			int stfCnt = 0;
			int dirCnt = 0;
			for (SmPersonnel per : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				PmParameter param = null;
				if (per.getPmStaffRole() != null
						&& per.getPmStaffRole().getPmParameterType() != null) {
					param = per.getPmStaffRole();

				} else if (per.getStaffRoleFlg() != null) {
					param = new PmParameter();
					param = retrievePmParameterById(per.getStaffRoleFlg());
				}
				if (per.isEquityOwner() && per.getEpfNo() != null
						&& per.getEpfNo() != "") {
					ownrCnt = ownrCnt + 1;

				} else if (per.isDirector() && per.getEpfNo() != null
						&& per.getEpfNo() != "") {
					dirCnt = dirCnt + 1;

				} else if (per.getStaffRoleFlg() != null
						&& param != null
						&& param.getPmParameterType()
								.getParameterType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONTRACTOR)
						&& param.getParameterCode()
								.equals(SupplierManagementConst.PERSONNEL_NORMAL_STAFF_CONTRACTOR_PARAMCODE)
						&& per.getEpfNo() != null && per.getEpfNo() != "") {

					stfCnt = stfCnt + 1;

				}
				if (stfCnt > 0 && dirCnt > 0 && ownrCnt > 0) {
					break;
				}
			}
			if (stfCnt < 1) {
				validCat = false;
				throw new EpException(MessageConst.ERROR_722512,
						new String[] { "staff" });
			}
			if (ownrCnt < 1) {
				System.out.println("****** No OWNER ********** ");
				validCat = false;
				throw new EpException(MessageConst.ERROR_722512,
						new String[] { "equity owner" });
			}
			if (dirCnt < 1) {
				validCat = false;
				throw new EpException(MessageConst.ERROR_722512,
						new String[] { "director" });
			}

		}
		if (validCat) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (getManagedBean().getSelectedCategory().getPmCategoryL3()
						.getCategoryL3Id() != 99
						&& getManagedBean().getSelectedCategory()
								.getPmCategoryL3().getCategoryL3Id() == cat
								.getPmCategoryL3().getCategoryL3Id()
						&& getManagedBean().getSelectedCategory()
								.getPmCategoryL2().getCategoryL2Id() == cat
								.getPmCategoryL2().getCategoryL2Id()
						&& getManagedBean().getSelectedCategory()
								.getPmCategoryL1().getCategoryL1Id() == cat
								.getPmCategoryL1().getCategoryL1Id()) {
					validCat = false;
					throw new EpException(MessageConst.ERROR_722525);
					// break;

				}
			}
		}

		return validCat;

	}

	private void assignEnteredStaffValues() {
		for (SmStaffTotal stf : getManagedBean().getSmAppl().getSmStaffTotals()) {
			if (stf.getStaffCategory().equalsIgnoreCase("C")
					|| stf.getStaffCategory().equalsIgnoreCase("L")) {
				getManagedBean().getPersonCalc().setStfBumiActCnt(
						stf.getTotalBumi());
				getManagedBean().getPersonCalc().setStfFrgActCnt(
						stf.getTotalForeigner());
				getManagedBean().getPersonCalc().setStfNonBumiActCnt(
						stf.getTotalNonBumi());
			} else if (stf.getStaffCategory().equalsIgnoreCase("P")) {
				getManagedBean().getPersonCalc().setProfBumiActCnt(
						stf.getTotalBumi());
				getManagedBean().getPersonCalc().setProfFrgActCnt(
						stf.getTotalForeigner());
				getManagedBean().getPersonCalc().setProfNonBumiActCnt(
						stf.getTotalNonBumi());
			} else if (stf.getStaffCategory().equalsIgnoreCase("S")) {
				getManagedBean().getPersonCalc().setSemiBumiActCnt(
						stf.getTotalBumi());
				getManagedBean().getPersonCalc().setSemiFrgActCnt(
						stf.getTotalForeigner());
				getManagedBean().getPersonCalc().setSemiNonBumiActCnt(
						stf.getTotalNonBumi());
			}
		}
	}

	private void calcPersonnelListCount() {
		resetAllPersonnelCount();
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			calcSumUpByPersonnel(per);
		}

	}

	private void calcSumUpByPersonnel(SmPersonnel per) {

		if (per.getPmCountry() != null
				&& per.getPmCountry().getCountryId() != null) {
			System.out.println("****** per country id ******"
					+ per.getPmCountry().getCountryId());
			if (per.getPmCountry().getCountryCode() == null
					|| per.getPmCountry().getCountryCode().equalsIgnoreCase("")) {
				per.setPmCountry(retrievePmCountryById(per.getPmCountry()
						.getCountryId()));
			}
			System.out.println("******Afte per country code ******"
					+ per.getPmCountry().getCountryCode());
		}

		PmParameter param = null;
		if (per.getPmStaffRole() != null
				&& per.getPmStaffRole().getPmParameterType() != null) {
			param = per.getPmStaffRole();

		} else if (per.getStaffRoleFlg() != null) {
			param = new PmParameter();
			param = retrievePmParameterById(per.getStaffRoleFlg());
		}

		if (per.isEquityOwner() || per.isDirector()) {
			setDirectorCount(per);
		}
		if (per.isHigherManagement()) {
			setHigherMngmtCount(per);
		} else if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
			if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONTRACTOR)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_MANAGEMENT_STAFF_CONTRACTOR_PARAMCODE)) {
				// management
				setManagementCount(per);
			} else if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONTRACTOR)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_NORMAL_STAFF_CONTRACTOR_PARAMCODE)) {
				// staff
				setStaffEnteredCount(per);

			}
		} else if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_MANAGEMENT_STAFF_CONSULTANT_PARAMCODE)) {
				// managment
				setManagementCount(per);

			} else if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_PROF_STAFF_CONSULTANT_PARAMCODE)) {
				// prof
				setProfEnteredCount(per);
			} else if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_CONSULTANT_PARAMCODE)) {
				// semi
				setSemiProfEnteredCount(per);
			} else if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_CONSULTANT_PARAMCODE)) {
				// staff
				setStaffEnteredCount(per);
			}

		}

	}

	private void calculateBumiPercentageTotals() {
		getManagedBean().getPersonCalc().setBodTotalCnt(
				getManagedBean().getPersonCalc().getBodBumiCnt()
						+ getManagedBean().getPersonCalc().getBodNonBumiCnt()
						+ getManagedBean().getPersonCalc().getBodFrgCnt());
		getManagedBean().getPersonCalc().setHghrMngmtTotalCnt(
				getManagedBean().getPersonCalc().getHghMngBumiCnt()
						+ getManagedBean().getPersonCalc()
								.getHghMngNonBumiCnt()
						+ getManagedBean().getPersonCalc().getHghMngFrgCnt());
		getManagedBean().getPersonCalc().setMngmtTotalCnt(
				getManagedBean().getPersonCalc().getMngtBumiCnt()
						+ getManagedBean().getPersonCalc().getMngtNonBumiCnt()
						+ getManagedBean().getPersonCalc().getMngtFrgCnt());
		getManagedBean().getPersonCalc().setStfTotalCnt(
				getManagedBean().getPersonCalc().getStfBumiEnterCnt()
						+ getManagedBean().getPersonCalc()
								.getStfNonBumiEnterCnt()
						+ getManagedBean().getPersonCalc().getStfFrgEnterCnt());
		getManagedBean().getPersonCalc()
				.setSemiTotalCnt(
						getManagedBean().getPersonCalc().getSemiBumiEnterCnt()
								+ getManagedBean().getPersonCalc()
										.getSemiNonBumiEnterCnt()
								+ getManagedBean().getPersonCalc()
										.getSemiFrgEnterCnt());
		getManagedBean().getPersonCalc()
				.setProfTotalCnt(
						getManagedBean().getPersonCalc().getProfBumiEnterCnt()
								+ getManagedBean().getPersonCalc()
										.getProfNonBumiEnterCnt()
								+ getManagedBean().getPersonCalc()
										.getProfFrgEnterCnt());

		getManagedBean().getPersonCalc().setTotalStaffEntered(
				getManagedBean().getPersonCalc().getBodTotalCnt()
						+ getManagedBean().getPersonCalc()
								.getHghrMngmtTotalCnt()
						+ getManagedBean().getPersonCalc().getMngmtTotalCnt()
						+ getManagedBean().getPersonCalc().getStfTotalCnt()
						+ getManagedBean().getPersonCalc().getSemiTotalCnt()
						+ getManagedBean().getPersonCalc().getProfTotalCnt());
		getManagedBean().getPersonCalc().setTotalKeyRolesEntered(
				getManagedBean().getPersonCalc().getBodTotalCnt()
						+ getManagedBean().getPersonCalc()
								.getHghrMngmtTotalCnt()
						+ getManagedBean().getPersonCalc().getMngmtTotalCnt());

		int stfBumiTotal = getManagedBean().getPersonCalc().getBodBumiCnt()
				+ getManagedBean().getPersonCalc().getHghMngBumiCnt()
				+ getManagedBean().getPersonCalc().getMngtBumiCnt()
				+ getManagedBean().getPersonCalc().getStfBumiEnterCnt()
				+ getManagedBean().getPersonCalc().getSemiBumiEnterCnt()
				+ getManagedBean().getPersonCalc().getProfBumiEnterCnt();

		BigDecimal mul = new BigDecimal(100);
		BigDecimal halfBumi = new BigDecimal(51);
		if (getManagedBean().getPersonCalc().getTotalStaffEntered() > 0) {
			BigDecimal stffBumiPer = new BigDecimal(stfBumiTotal
					/ getManagedBean().getPersonCalc().getTotalStaffEntered())
					.multiply(mul).setScale(2, RoundingMode.HALF_UP);
			getManagedBean().getPersonCalc().setTotalStaffBumiPerc(stffBumiPer);
		}
		if (getManagedBean().getPersonCalc().getTotalStaffBumiPerc()
				.compareTo(mul) == 0) {
			getManagedBean().getPersonCalc().setFullBumiStatus(true);
		} else if (getManagedBean().getPersonCalc().getTotalStaffBumiPerc()
				.compareTo(halfBumi) >= 0
				&& getManagedBean().getPersonCalc().getTotalStaffBumiPerc()
						.compareTo(mul) < 0) {
			int keyRoleBumiTotal = getManagedBean().getPersonCalc()
					.getBodBumiCnt()
					+ getManagedBean().getPersonCalc().getHghMngBumiCnt()
					+ getManagedBean().getPersonCalc().getMngtBumiCnt();
			if (getManagedBean().getPersonCalc().getTotalKeyRolesEntered() > 0
					&& getManagedBean().getPersonCalc().getTotalStaffBumiPerc()
							.compareTo(mul) != 0) {
				BigDecimal keyRoleBumiPer = new BigDecimal(keyRoleBumiTotal
						/ getManagedBean().getPersonCalc()
								.getTotalKeyRolesEntered()).multiply(mul)
						.setScale(2, RoundingMode.HALF_UP);
				getManagedBean().getPersonCalc().setTotalKeyRoleBumiPerc(
						keyRoleBumiPer);
				if (getManagedBean().getPersonCalc().getTotalKeyRoleBumiPerc()
						.compareTo(halfBumi) >= 0
						&& getManagedBean().getPersonCalc()
								.getTotalKeyRoleBumiPerc().compareTo(mul) < 0) {
					for (SmPersonnel per : getManagedBean().getSmAppl()
							.getSmPersonnels()) {
						if (per.isEquityOwner() && per.getBumiFlg() == 1) {
							getManagedBean()
									.getPersonCalc()
									.setTotalShrBumiShrHldrPerc(
											getManagedBean()
													.getPersonCalc()
													.getTotalShrBumiShrHldrPerc()
													.add(per.getSharePercentage()));
						}
					}
					if (getManagedBean().getPersonCalc()
							.getTotalShrBumiShrHldrPerc().compareTo(halfBumi) >= 0
							&& getManagedBean().getPersonCalc()
									.getTotalShrBumiShrHldrPerc()
									.compareTo(mul) < 0) {
						getManagedBean().getPersonCalc()
								.setHalfBumiStatus(true);
					}

				}
			}
		}

	}

	private void setManagementCount(SmPersonnel per) {
		// managment
		if (per.getPmCountry() != null
				&& per.getPmCountry().getCountryCode() != null
				&& per.getPmCountry().getCountryCode()
						.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc().setMngtBumiCnt(
						getManagedBean().getPersonCalc().getMngtBumiCnt() + 1);
			} else {
				getManagedBean().getPersonCalc()
						.setMngtNonBumiCnt(
								getManagedBean().getPersonCalc()
										.getMngtNonBumiCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setMngtFrgCnt(
					getManagedBean().getPersonCalc().getMngtFrgCnt() + 1);
		}

	}

	private void setStaffEnteredCount(SmPersonnel per) {
		if (per.getPmCountry() != null
				&& per.getPmCountry().getCountryCode() != null
				&& per.getPmCountry().getCountryCode()
						.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc()
						.setStfBumiEnterCnt(
								getManagedBean().getPersonCalc()
										.getStfBumiEnterCnt() + 1);
			} else {
				getManagedBean().getPersonCalc().setStfNonBumiEnterCnt(
						getManagedBean().getPersonCalc()
								.getStfNonBumiEnterCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setStfFrgEnterCnt(
					getManagedBean().getPersonCalc().getStfFrgEnterCnt() + 1);
		}
	}

	private void setSemiProfEnteredCount(SmPersonnel per) {
		if (per.getPmCountry() != null
				&& per.getPmCountry().getCountryCode() != null
				&& per.getPmCountry().getCountryCode()
						.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc()
						.setSemiBumiEnterCnt(
								getManagedBean().getPersonCalc()
										.getSemiBumiEnterCnt() + 1);
			} else {
				getManagedBean().getPersonCalc().setSemiNonBumiEnterCnt(
						getManagedBean().getPersonCalc()
								.getSemiNonBumiEnterCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setSemiFrgEnterCnt(
					getManagedBean().getPersonCalc().getSemiFrgEnterCnt() + 1);
		}
	}

	private void setProfEnteredCount(SmPersonnel per) {
		if (per.getPmCountry() != null
				&& per.getPmCountry().getCountryCode() != null
				&& per.getPmCountry().getCountryCode()
						.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc()
						.setProfBumiEnterCnt(
								getManagedBean().getPersonCalc()
										.getProfBumiEnterCnt() + 1);
			} else {
				getManagedBean().getPersonCalc().setProfNonBumiEnterCnt(
						getManagedBean().getPersonCalc()
								.getProfNonBumiEnterCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setProfFrgEnterCnt(
					getManagedBean().getPersonCalc().getProfFrgEnterCnt() + 1);
		}
	}

	private void setHigherMngmtCount(SmPersonnel per) {
		if (per.getPmCountry() != null
				&& per.getPmCountry().getCountryCode() != null
				&& per.getPmCountry().getCountryCode()
						.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc()
						.setHghMngBumiCnt(
								getManagedBean().getPersonCalc()
										.getHghMngBumiCnt() + 1);
			} else {
				getManagedBean().getPersonCalc()
						.setHghMngNonBumiCnt(
								getManagedBean().getPersonCalc()
										.getHghMngNonBumiCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setHghMngFrgCnt(
					getManagedBean().getPersonCalc().getHghMngFrgCnt() + 1);
		}
	}

	private void setDirectorCount(SmPersonnel per) {
		if (per.getPmCountry() != null
				&& per.getPmCountry().getCountryCode() != null
				&& per.getPmCountry().getCountryCode()
						.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			System.out.println("**** PM COOOUNTYR CODE *****"
					+ per.getPmCountry().getCountryCode());
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc().setBodBumiCnt(
						getManagedBean().getPersonCalc().getBodBumiCnt() + 1);
			} else {
				getManagedBean().getPersonCalc()
						.setBodNonBumiCnt(
								getManagedBean().getPersonCalc()
										.getBodNonBumiCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setBodFrgCnt(
					getManagedBean().getPersonCalc().getBodFrgCnt() + 1);
		}

	}

	private void resetAllPersonnelCount() {
		getManagedBean().setPersonCalc(new SmPersonnleCalcVO());
		assignEnteredStaffValues();
	}

	private MofRegistrationResDTO retriveApplicationById(int applId) {

		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setApplId(applId);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveSmApplicationDetailsById(reqDto);
			return resDTO;

		} catch (Exception e) {
			System.out
					.println("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}

	}

	private void setNavigationForResubmitAppln(String applType) {
		FacesContext fc = FacesContext.getCurrentInstance();

		switch (applType) {
		case "N":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
					// "/xhtml/sm/NewMofApplication/smMofRegSideMenu_BasePage.xhtml");
							"/xhtml/sm/NewMofApplication/mofRegSmUser_BaseWizardPage.xhtml");
			break;
		case "A":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/addAdditionalCategoryCodes/smAddtnlCatApplnSideMenu_BasePage.xhtml");
			break;
		case "B":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/bumiPuteraApplication/smBumiSideMenu_BasePage.xhtml");
			break;
		case "U":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/updateMofDetails/smUpdateMofSideMenu_BasePage.xhtml");
			break;
		case "R":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
			break;
		default:
			System.out
					.println("***** THIS IS DEFAULT PAGE --- i.e. no matching type ---"
							+ applType);
			break;
		}
	}

	private void assignSupplierBusinessType(SmSupplier sup) {
		if (sup.getBusinessType()
				.equalsIgnoreCase(
						SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)) {
			getManagedBean()
					.setBusinessTypeVal(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_DEFAULT);

		} else if (sup
				.getBusinessType()
				.equalsIgnoreCase(
						SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN)) {
			getManagedBean()
					.setBusinessTypeVal(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT);
		} else if (sup.getBusinessType().equalsIgnoreCase(
				SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)) {
			getManagedBean()
					.setBusinessTypeVal(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT);
		}
	}

	private boolean updateSmSupplierInfo(String applType) {
		try {
			getManagedBean().getSmSupplier().setSmAppl(
					getManagedBean().getSmAppl());
			if (applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
				SmMofAccount acc = new SmMofAccount();
				acc.setSmSupplier(getManagedBean().getSmSupplier());
				acc.setSupplierType(getManagedBean().getSmAppl()
						.getSupplierType());
				StringBuffer sb = new StringBuffer();
				if (getManagedBean().getSmAppl().getSupplierType()
						.equalsIgnoreCase("J")) {
					sb.append("465-");
					sb.append(generateMofNum(sb.toString()));
				} else {
					if (getManagedBean().getSmAppl().getSmCompanyBasic()
							.getSmCompanyAddresses().get(0).getSmAddress()
							.getPmState().getStateName()
							.equalsIgnoreCase("Sabah")) {
						sb.append("357-2");
					} else if (getManagedBean().getSmAppl().getSmCompanyBasic()
							.getSmCompanyAddresses().get(0).getSmAddress()
							.getPmState().getStateName()
							.equalsIgnoreCase("Sarawak")) {
						sb.append("357-3");
					} else {
						sb.append("357-0");
					}
					sb.append(generateMofNum(sb.toString()));
				}
				System.out.println(" *** The final Mof account num **** "
						+ sb.toString());
				acc.setMofNo(sb.toString());
				acc.setEffDate(new Date());
				Calendar c = new GregorianCalendar();
				c.add(Calendar.YEAR, 3);
				Date d = c.getTime();
				acc.setExpDate(c.getTime());
				List<SmMofCert> certList = new ArrayList<SmMofCert>();
				certList.add(assignNewMofCert(acc));
				acc.setSmMofCerts(certList);

				if (getManagedBean().getSmSupplier().getSmMofAccounts() == null) {
					getManagedBean().getSmSupplier().setSmMofAccounts(
							new ArrayList<SmMofAccount>());
				}
				getManagedBean().getSmSupplier().getSmMofAccounts().add(acc);

			} else if (applType
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
					|| applType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)
					|| applType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
					|| applType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)) {
				for (SmMofAccount acc : getManagedBean().getSmSupplier()
						.getSmMofAccounts()) {
					System.out.println("*** Acc Type ********");
					if (acc.getSupplierType().equals(
							getManagedBean().getSmAppl().getSupplierType())) {
						if (applType
								.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
							acc.setEffDate(new Date());
							Calendar c = new GregorianCalendar();
							c.add(Calendar.YEAR, 3);
							Date d = c.getTime();
							acc.setExpDate(c.getTime());
						}
						if (acc.getSmMofCerts() != null
								&& !acc.getSmMofCerts().isEmpty()) {
							for (SmMofCert cer : acc.getSmMofCerts()) {
								cer.setRecordStatusFlg(SupplierManagementConst.RECORD_STATUS_SUSPENDED);
							}
							acc.getSmMofCerts().add(assignNewMofCert(acc));
						} else {
							System.out
									.println("******* Adding new cert for *******"
											+ applType);
							List<SmMofCert> certList = new ArrayList<SmMofCert>();
							certList.add(assignNewMofCert(acc));
							acc.setSmMofCerts(certList);
						}
					}
				}

			} else if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				System.out.println(" **** This is cancelaation **** "
						+ getManagedBean().getSmSupplier().getSmMofAccounts()
								.size());
				for (SmMofAccount acc : getManagedBean().getSmSupplier()
						.getSmMofAccounts()) {
					if (acc.getSupplierType().equals(
							getManagedBean().getSmAppl().getSupplierType())) {
						acc.setRecordStatusFlg(SupplierManagementConst.RECORD_STATUS_DELETED);
						if (acc.getSmMofCerts() != null
								&& !acc.getSmMofCerts().isEmpty()) {
							for (SmMofCert cer : acc.getSmMofCerts()) {
								cer.setRecordStatusFlg(SupplierManagementConst.RECORD_STATUS_DELETED);
							}
						}

					}
				}

			}
			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setSmSupp(this.getManagedBean().getSmSupplier());
			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("sm", IRegistrationSO.class);
			RegistrationResDTO resDTO = reqSO.updateOnlineAcc(reqDTO);
			getManagedBean().setSmSupplier(
					retriveSupplierById(
							getManagedBean().getSmSupplier().getSupplierId())
							.getSmSupplier());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			getManagedBean().setSmSupplier(
					retriveSupplierById(
							getManagedBean().getSmSupplier().getSupplierId())
							.getSmSupplier());
			return false;
		}
	}

	private SmMofCert assignNewMofCert(SmMofAccount acct) {
		SmMofCert cert = new SmMofCert();
		cert.setSmMofAccount(acct);
		cert.setExpDate(acct.getExpDate());
		cert.setEffDate(new Date());
		StringBuffer sb = new StringBuffer();
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase("J")) {
			sb.append("J");
			sb.append(generateMofCertNum(sb.toString()));
		} else {
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress().getPmState()
					.getStateName().equalsIgnoreCase("Sabah")) {
				if (getManagedBean().getSmSupplier().isBumi()) {
					sb.append("BPS");
				} else {
					sb.append("KS");
				}
			} else if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress().getPmState()
					.getStateName().equalsIgnoreCase("Sarawak")) {
				if (getManagedBean().getSmSupplier().isBumi()) {
					sb.append("BPQ");
				} else {
					sb.append("KQ");
				}
			} else {
				if (getManagedBean().getSmSupplier().isBumi()) {
					sb.append("B");
				} else {
					sb.append("K");
				}
			}
			sb.append(generateMofCertNum(sb.toString()));
		}
		cert.setCertSerialNo(sb.toString());
		return cert;

	}

	private String generateMofNum(String mofPrefix) {
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);

		MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
		reqDto.setMofNum(mofPrefix);
		reqDto.setSupplierType(getManagedBean().getSmAppl().getSupplierType());
		reqDto.setSmAppl(this.getManagedBean().getSmAppl());
		MofRegistrationResDTO resDTO = mofRegistrationSO.generateMofNum(reqDto);
		return resDTO.getMofNum();
	}

	private String generateMofCertNum(String mofCertPrefix) {
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);

		MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
		reqDto.setMofCertNum(mofCertPrefix);
		reqDto.setSmAppl(this.getManagedBean().getSmAppl());
		MofRegistrationResDTO resDTO = mofRegistrationSO
				.generateMofCertNum(reqDto);
		return resDTO.getMofCertNum();
	}

	private void setApplPmStatus(String statusId) {
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(Long.parseLong(statusId));
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusResDTO resDTO = reqSO.retrieveStatusByStatusId(reqDTO);
		if (resDTO != null && resDTO.getPmStatus() != null) {
			getManagedBean().getSmAppl().setPmStatus(resDTO.getPmStatus());
		}

		resDTO = reqSO.retrieveStatusDescById(reqDTO);
		if (resDTO != null && resDTO.getPmStatusDesc() != null) {
			getManagedBean().setCurrentStatus(
					resDTO.getPmStatusDesc().getStatusName());
		}
	}

	private void setPmStatusDesc(String statusId) {
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

	private SmSupplier getCurrentSupplier() {
		SmSupplier smSupp = new SmSupplier();
		try {
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierSearchReqDTO);
			smSupp = (SmSupplier) smResDTO.getResultBean().getResultList()
					.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return smSupp;
	}
}
