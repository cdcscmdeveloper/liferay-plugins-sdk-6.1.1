package my.ep.web.backingbean.sm.applicationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
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
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.BaseReviewApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.liferay.compat.portal.util.PortalUtil;
import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.mgbean.BeanBuilder;
import com.sun.faces.mgbean.BeanManager;

public abstract class BaseReviewApplicationBackingBean<T extends BaseReviewApplicationManagedBean>
		extends BaseBackingBean<T> {

	public static Logger log = Logger
			.getLogger(BaseReviewApplicationBackingBean.class);
	private StreamedContent file;
	private static final String SER_FILE_TYPE = ".ser";
	private static final String FOLDER_NAME_SM = "SM";
	private static final String FOLDER_NAME_REVISION = "Revision";

	public BaseReviewApplicationBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

	public void initMain() {
		if (reloadApplication()) {
			initApplication(null);
			setMaxTab();
		}
		setTabParam();
	}

	public void initApplication(SmAppl smAppl) {
		ISmApplRevisionSO applRevisionSO = null;

		try {
			// checking if SER file not exist then recreate
			createSerFileIfNotExist(smAppl);

			if (getManagedBean().isReviewApplicationExist()) {
				applRevisionSO = (ISmApplRevisionSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ISmApplRevisionSO.class);

				SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();

				reqDTO.setDeSerFileName(getDeSerFileNameAndPath(getManagedBean()
						.getApplicationNoParam()));
				SmApplRevisionResDTO resDTO = applRevisionSO
						.readApplicationHist(reqDTO);
				setApplication(resDTO);
			}

		} catch (NullPointerException ne) {
			getManagedBean().setReviewApplicationExist(false);
		} finally {
			applRevisionSO = null;
		}

		// show message if supp profile not ready
		if (!getManagedBean().isReviewApplicationExist()) {
			SmApplicationUtil.showSuppProfileNotReadyMsg(FacesContext
					.getCurrentInstance());
		}

	}

	public boolean reloadApplication() {
		boolean reload = false;
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String applicationNo = originalServletRequest
				.getParameter("applicationNo");

		if (applicationNo != null) {
			if (getManagedBean().getApplicationNoParam() == null) {
				reload = true;
				getManagedBean().setApplicationNoParam(applicationNo);
			} else {
				if (!applicationNo.equals(getManagedBean()
						.getApplicationNoParam())) {
					reload = true;
					getManagedBean().setApplicationNoParam(applicationNo);
				}
			}
		}
		return reload;
	}

	public void downloadSelectedSuppDoc(String fileName, String filePath) {
		String finalFilePath = filePath + fileName;
		try {
			commonDownload(finalFilePath, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finalFilePath = null;
	}

	public void viewSignatories(SmSupplierBank signAcctBean) {
		getManagedBean().setSelectedSignatoryBankAcct(signAcctBean);
	}

	private void printJsfBeanList() {
		ApplicationAssociate application = ApplicationAssociate
				.getInstance(FacesContext.getCurrentInstance()
						.getExternalContext());
		BeanManager beanManager = application.getBeanManager();
		Map<String, BeanBuilder> beanMap = beanManager.getRegisteredBeans();
		Set<Entry<String, BeanBuilder>> beanEntries = beanMap.entrySet();

		for (Entry<String, BeanBuilder> bean : beanEntries) {
			String beanName = bean.getKey();
			if (beanManager.isManaged(beanName)) {
				BeanBuilder builder = bean.getValue();
				log.info("Bean name: " + beanName);
				log.info("Bean class: " + builder.getBeanClass());
				log.info("Bean scope: " + builder.getScope());
			}
		}
	}

	private String getDeSerFileNameAndPath(String applNoParam) {
		String appNo = applNoParam.substring(0, applNoParam.length() - 4);

		String filePath = Calendar.getInstance().get(Calendar.YEAR)
				+ File.separator + FOLDER_NAME_SM + File.separator + appNo
				+ File.separator + FOLDER_NAME_REVISION + File.separator;
		String tempDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		tempDir += File.separator + filePath + applNoParam + SER_FILE_TYPE;

		return tempDir;
	}

	private String getDropDownViewOnlyValueByCode(String parameterType,
			String parameterCode) {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(parameterType, null);
		String retVal = "";
		for (PmParameterDesc parameterDesc : parameterDescs) {

			if (parameterCode.equals(parameterDesc.getPmParameter()
					.getParameterCode())) {
				retVal = parameterDesc.getCodeName();
			}
		}

		return retVal;
	}

	private String getDropDownViewOnlyValueById(String parameterType,
			Long parameterId) {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(parameterType, null);
		String retVal = "";
		for (PmParameterDesc parameterDesc : parameterDescs) {

			if (parameterId.compareTo(parameterDesc.getPmParameter()
					.getParameterId()) == 0) {
				retVal = parameterDesc.getCodeName();
			}
		}

		return retVal;
	}

	private SmMofAccount getSingleSmMofAccount() {
		SmMofAccount smMofAccount = new SmMofAccount();
		try {
			List<SmMofAccount> accounts = getManagedBean().getSmAppl()
					.getSmSupplier().getSmMofAccounts();
			for (SmMofAccount account : accounts) {
				if (account.getSupplierType().equalsIgnoreCase(
						getManagedBean().getSmAppl().getSupplierType())
						&& account.getRecordStatusFlg().compareTo(
								DbValueConst.RECORD_STATUS_ACTIVE) == 0) {
					smMofAccount = account;
					break;
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return smMofAccount;
	}

	public void setTabParam() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String tabParam = originalServletRequest.getParameter("currentTab");
		try {
			if (tabParam != null) {
				if (Integer.parseInt(tabParam) <= getManagedBean().getMaxTab()) {
					setTabValue(Integer.parseInt(tabParam), "");
				} else {
					setTabValue(0, "");
				}
			}
		} catch (NumberFormatException ne) {
			setTabValue(0, "");
		}
	}

	public void setTabValue(int outputTab, String outputPageType) {
		getManagedBean().setTabActiveIndex(outputTab);
		getManagedBean().setRedirectToPersonPage(false);
		getManagedBean().setCurrentPageType(outputPageType);
		getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
	}

	private SmAddress setDefaultAddr() {
		SmAddress addr = new SmAddress();
		PmCountry cntry = new PmCountry();
		if (getManagedBean().getDefPmCountry() != null
				&& getManagedBean().getDefPmCountry().getCountryId() != null) {
			cntry = getManagedBean().getDefPmCountry();
		} else {
			cntry = SmCommonUtil
					.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			// cntry.setCountryId(1l);
		}
		addr.setPmCity(new PmCity());
		addr.setPmCountry(cntry);
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());

		return addr;
	}

	private void setApplication(SmApplRevisionResDTO resDTO) {
		initStep0BaseInfo(resDTO);
		initStep1CompanyInfo();
		initStep2BranchCompanyInfo();
		initStep3CaptialAndEquityInfo();
		initStep4PersonnelInfo();
		initStep5BankInfo();
		initStep6RegisterCategoryCode();
		initStep6_1ViewFactoryInfo();
		initStep6_2EquipmentInfo();
		initStep7SupportingDocumentsInfo();
		initStep8_9PoApproverDefaultDecision();
		initBumiRecommendNote();

		// testing by user
		// getManagedBean().setApproverUser(false);
		// getManagedBean().setSupplierUser(false);
		// getManagedBean().setPoUser(true);
		// getManagedBean().setViewSupplierProfile(true);
	}

	private void initStep0BaseInfo(SmApplRevisionResDTO resDTO) {
		getManagedBean().setSmAppl(resDTO.getSmAppl());
		getManagedBean().setSmSupplier(resDTO.getSmAppl().getSmSupplier());
		getManagedBean().setBusinessTypeVal(
				SmApplicationUtil.assignSupplierBusinessType(getManagedBean()
						.getSmSupplier()));
		getManagedBean().setSupplierBusinessTypeVal(
				getDropDownViewOnlyValueByCode(
						SupplierManagementConst.SUPPLIER_BUSINESS_TYPE,
						getManagedBean().getSmSupplier().getBusinessType()));
		if (getManagedBean().isViewSupplierProfile()) {
			getManagedBean().setSmMofAccount(getSingleSmMofAccount());
		}
		getManagedBean().setReviewApplicationExist(true);
	}

	private void initStep1CompanyInfo() {
		// companyBasicInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmCompanyBasic() == null) {
			getManagedBean().getSmAppl()
					.setSmCompanyBasic(new SmCompanyBasic());
		}

		// staffInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmStaffTotals() == null) {
			getManagedBean().getSmAppl().setSmStaffTotals(
					new ArrayList<SmStaffTotal>());
		}

		// officeFacilityTypeFormViewOnly
		getManagedBean().setBasicCompanyFacility(new SmFacility());
		for (SmFacility smFac : getManagedBean().getSmAppl().getSmFacilities()) {
			if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
					|| smFac.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
				this.getManagedBean().setBasicCompanyFacility(smFac);
			}
		}

		// certificateInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmSupplierCerts() == null) {
			getManagedBean().getSmAppl().setSmSupplierCerts(
					new ArrayList<SmSupplierCert>());
		}

		// professionalBodyInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmAssociationBodies() == null) {
			getManagedBean().getSmAppl().setSmAssociationBodies(
					new ArrayList<SmAssociationBody>());
		}
	}

	private void initStep2BranchCompanyInfo() {
		// branchCompanyInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmSupplierBranches() == null) {
			getManagedBean().getSmAppl().setSmSupplierBranches(
					new ArrayList<SmSupplierBranch>());
		} else {
			for (SmSupplierBranch smSupplierBranch : getManagedBean()
					.getSmAppl().getSmSupplierBranches()) {
				if (smSupplierBranch.getSmPersonnel() == null) {
					smSupplierBranch.setSmPersonnel(new SmPersonnel());
				}
			}
		}

		// associatedCompanyInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmAssociateCompanies() == null) {
			getManagedBean().getSmAppl().setSmAssociateCompanies(
					new ArrayList<SmAssociateCompany>());
		}

		// companyProjectsExperienceInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmCompanyExperiences() == null) {
			getManagedBean().getSmAppl().setSmCompanyExperiences(
					new ArrayList<SmCompanyExperience>());
		}

	}

	private void initStep4PersonnelInfo() {
		// personnelInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmPersonnels() == null) {
			getManagedBean().getSmAppl().setSmPersonnels(
					new ArrayList<SmPersonnel>());
		} else {
			for (SmPersonnel smPersonnel : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (smPersonnel.getRoleName() != null
						&& StringUtils.isNotBlank(smPersonnel.getRoleName())) {
					// read value rolename from properties
					smPersonnel
							.setRoleName(smPersonnel
									.getRoleName()
									.replaceAll(
											"Director",
											SmCommonUtil
													.getMessage(
															"lbl.sm.personnel.comprole.director",
															null)));
					smPersonnel.setRoleName(smPersonnel.getRoleName()
							.replaceAll(
									"Staff",
									SmCommonUtil.getMessage(
											"lbl.sm.personnel.comprole.staff",
											null)));
					smPersonnel
							.setRoleName(smPersonnel
									.getRoleName()
									.replaceAll(
											"Equity Owner",
											SmCommonUtil
													.getMessage(
															"lbl.sm.personnel.comprole.shareholder",
															null)));
					smPersonnel
							.setRoleName(smPersonnel
									.getRoleName()
									.replaceAll(
											"Higher Management",
											SmCommonUtil
													.getMessage(
															"lbl.sm.personnel.comprole.higher.mngmnt",
															null)));
					smPersonnel.setRoleName(smPersonnel.getRoleName()
							.replaceAll(
									"Management",
									SmCommonUtil.getMessage(
											"lbl.sm.personnel.comprole.mngmnt",
											null)));
				}
			}
		}
		setPersonnelInfoCalculationSummary();
	}

	private void initStep3CaptialAndEquityInfo() {
		// captialOrEquityStatusInfoFormViewOnly
		SmCommonUtil
				.calculateSmApplicationPaidUpCapitalPercent(getManagedBean()
						.getSmAppl().getSmCompanyBasic());

		// captialOrEquityShareHolderInfoFormViewOnly
		setApplicationShareHolders(getManagedBean().getSmAppl()
				.getSmPersonnels());

		// captialOrEquityShareHoldingCompanyInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmEquityCompanies() == null) {
			getManagedBean().getSmAppl().setSmEquityCompanies(
					new ArrayList<SmEquityCompany>());
		}
	}

	private void initStep5BankInfo() {
		if (getManagedBean().getSmAppl().getSmSupplierBanks() == null) {
			getManagedBean().getSmAppl().setSmSupplierBanks(
					new ArrayList<SmSupplierBank>());
		}
	}

	private void initStep6RegisterCategoryCode() {
		if (getManagedBean().getSmAppl().getSmSupplierCategories() == null) {
			getManagedBean().getSmAppl().setSmSupplierCategories(
					new ArrayList<SmSupplierCategory>());
		} else {
			setActiveCategoryList();
		}
		setCategoryTypeConfirm();
	}

	private void initStep6_1ViewFactoryInfo() {
		// factoryOrWorkshopInfoFormViewOnly/dockyardInfoFormViewOnly/workshopInfoFormViewOnly/storeInfoFormViewOnly
		setApplicationFacilities();
	}

	private void initStep6_2EquipmentInfo() {
		// machineryInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmMachineDtls() == null) {
			getManagedBean().getSmAppl().setSmMachineDtls(
					new ArrayList<SmMachineDtl>());
		}
		// manufacturingOrAssmblyInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmManufacturingDtls() == null) {
			getManagedBean().getSmAppl().setSmManufacturingDtls(
					new ArrayList<SmManufacturingDtl>());
		}
	}

	private void initStep7SupportingDocumentsInfo() {
		// offlineSupportingDocsFormViewOnly/onlineSupportingDocsFormViewOnly
		if (getManagedBean().getSmAppl().getSmApplSupportDocs() == null) {
			getManagedBean().getSmAppl().setSmApplSupportDocs(
					new ArrayList<SmApplSupportDoc>());
		}
	}

	private void initStep8_9PoApproverDefaultDecision() {
		// poDefault_DescisionPageViewOnly/approverDefault_DescisionPageViewOnly

		if (getManagedBean().isPoUser()) {
			setDefaultPoReviewSection();
		}

		if (getManagedBean().isApproverUser()) {
			setDefaultApproverReviewSection();
		}
		setSupplierCancellationRemarks(getManagedBean().getSmAppl()
				.getApplType(), getManagedBean().getSmAppl().getApplId());
		setPmStatusDesc(getManagedBean().getSmAppl().getPmStatus() == null ? ""
				: getManagedBean().getSmAppl().getPmStatus().getStatusId()
						.toString());
	}

	private void setSupplierCancellationRemarks(String docType, Long docId) {
		SmRemaksResDTO resDTO = SmApplicationUtil.getRemarksByDoc(docType,
				docId);
		StringBuffer sb = new StringBuffer();
		if (resDTO != null && resDTO.getRemarksList() != null
				&& !resDTO.getRemarksList().isEmpty()) {

			for (SmRemark rem : resDTO.getRemarksList()) {
				sb.append(rem.getRemark()).append("\n");
			}
			getManagedBean().setSupplierRemarks(sb.toString());
		}
	}

	private void setPersonnelInfoCalculationSummary() {
		getManagedBean().setPersonCalc(
				SmCommonUtil.calcPersonnelListCount(getManagedBean()
						.getSmAppl().getSmPersonnels(), getManagedBean()
						.getSmAppl().getSmStaffTotals(), getManagedBean()
						.getSmAppl().getSupplierType()));
	}

	private void setPmStatusDesc(String statusId) {
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(Long.parseLong(statusId));
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		StatusResDTO resDTO = reqSO.retrieveStatusDescById(reqDTO);
		if (resDTO != null && resDTO.getPmStatusDesc() != null) {
			getManagedBean().setCurrentStatus(
					resDTO.getPmStatusDesc().getStatusName());
		}
	}

	public void assignSelAssoc(SmAssociateCompany assocBean) {
		getManagedBean().setSelectedAssocCompany(assocBean);
	}

	public void assignSelBranch(SmSupplierBranch branchBean) {
		getManagedBean().setSelectedBranch(branchBean);
	}

	public void assignSelCert(SmSupplierCert certBean) {
		getManagedBean().setSelectedCert(certBean);
		getManagedBean().setSelectedCertType(
				this.getDropDownViewOnlyValueById(
						"CRT",
						Long.parseLong(getManagedBean().getSelectedCert()
								.getCertTypeId().toString())));
	}

	public void assignSelBank(SmSupplierBank shareBankBean) {
		getManagedBean().setSelBankBranchName("");

		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (SmChequeSignatory smChequeSignatoryTmp : shareBankBean
				.getSmChequeSignatories()) {
			i++;
			if (i <= 2) {
				sb.append(smChequeSignatoryTmp.getSmPersonnel().getName())
						.append(",");
			} else {
				sb.append("<br />");
				i = 0;
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.lastIndexOf(","));
		}
		getManagedBean().setChequeSignPersonnelName(sb.toString());

		if (shareBankBean.getSmSupplierBranch() != null
				&& shareBankBean.getSmSupplierBranch().getSupplierBranchId() != null) {
			getManagedBean().setSelBankBranchName(
					shareBankBean.getSmSupplierBranch().getBranchName());
		}
		getManagedBean().setSelectedBank(shareBankBean);
		if (shareBankBean.getSmAddress() == null) {
			shareBankBean.setSmAddress(setDefaultAddr());
		}
		setSelectedBankAccountPurpose();
	}

	public void assignSelEquiCompany(SmEquityCompany equiCompBean) {
		getManagedBean().setSelectedEquityCompany(equiCompBean);
	}

	public void assignSelFactory(SmFacility facilityBean) {
		Locale locale = new FacesScopes().getUserLocale();
		getManagedBean().setSelectedFactoryFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(ResourceBundleUtils.getMessage(
						"lbl.sm.facility.type.factory",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)));
	}

	public void assignSelWorkshop(SmFacility facilityBean) {
		Locale locale = new FacesScopes().getUserLocale();
		getManagedBean().setSelectedWorkShopFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(ResourceBundleUtils.getMessage(
						"lbl.sm.facility.type.workshop",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)));
	}

	public void assignSelDockyard(SmFacility facilityBean) {
		Locale locale = new FacesScopes().getUserLocale();
		getManagedBean().setSelectedDockyardFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(ResourceBundleUtils.getMessage(
						"lbl.sm.facility.type.dockyard",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)));
	}

	public void assignSelStore(SmFacility facilityBean) {
		Locale locale = new FacesScopes().getUserLocale();
		getManagedBean().setSelectedStoreFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(ResourceBundleUtils.getMessage(
						"lbl.sm.facility.type.store",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)));
	}

	public void assignSelManufacturer(SmManufacturingDtl manBean) {
		getManagedBean().setSelectedManufacturer(manBean);
	}

	public void assignSelMachinery(SmMachineDtl macBean) {
		getManagedBean().setSelectedMachine(macBean);

		if (macBean.getPurchaseMethod() != null
				&& StringUtils.isNotBlank(macBean.getPurchaseMethod())) {
			Locale locale = new FacesScopes().getUserLocale();
			PmParameter paymentMethod = SmCommonUtil
					.retrieveParameterByTypeAndCode("BM",
							macBean.getPurchaseMethod(), locale.getLanguage());
			getManagedBean()
					.setSelectedMachineryPurchaseMethod(
							SmCommonUtil
									.getParamDescriptionByLanguageAndPmParameter(paymentMethod));
		}

	}

	public void assignSelCompanyExperience(SmCompanyExperience consultantBean) {
		getManagedBean().setSelectedCompanyExperience(consultantBean);
		if (getManagedBean().getSelectedCompanyExperience()
				.getProcurementModeId() != null) {
			PmParameter procurementMode = SmApplicationUtil
					.retrievePmParameterById(getManagedBean()
							.getSelectedCompanyExperience()
							.getProcurementModeId().longValue());
			getManagedBean()
					.setSelectedCompanyExperienceProcurementMode(
							SmCommonUtil
									.getParamDescriptionByLanguageAndPmParameter(procurementMode));
		}
	}

	public void assignSelAssocBody(SmAssociationBody assocBean) {
		getManagedBean().setSelectedAssocBody(assocBean);
	}

	public void backFromPersonnelPage() {
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
		}
		if (getManagedBean().getSmAppl().getSmPersonnels()
				.contains(getManagedBean().getSelectedShareHolder())) {

			// TODO reset few values that could have been already submit by
			// partial submit on ajax functions
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

	private void setSelectedBankAccountPurpose() {
		Locale locale = new FacesScopes().getUserLocale();
		switch (getManagedBean().getSelectedBank().getAccountPurpose()) {
		case "P":
			getManagedBean().setSelectedBankAccountPurpose(
					ResourceBundleUtils.getMessage("lbl.sm.paymentLbl",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			break;
		case "R":
			getManagedBean().setSelectedBankAccountPurpose(
					ResourceBundleUtils.getMessage(
							"lbl.sm.bankDtls.registration",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			break;
		case "B":
			getManagedBean().setSelectedBankAccountPurpose(
					ResourceBundleUtils.getMessage(
							"lbl.sm.bankDtls.both.regandpay",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			break;
		default:
			break;

		}
	}

	private void setCategoryTypeConfirm() {
		Locale locale = new FacesScopes().getUserLocale();
		String[] params = { "" };
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			params[0] = ResourceBundleUtils.getMessage("lbl.sm.catCodes.norm",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		} else {
			params[0] = ResourceBundleUtils.getMessage("lbl.sm.catCodes.spl",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		}
		getManagedBean().setCatTypeConfirm(
				ResourceBundleUtils.getMessage("lbl.sm.catCodeNote", params,
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
	}

	private void setApplicationFacilities() {
		getManagedBean().setApplFactoryFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplDockyardFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplWorkShopFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplStoreFacilities(new ArrayList<SmFacility>());

		if (getManagedBean().getSmAppl().getSmFacilities() != null
				&& CollectionUtils.isNotEmpty(getManagedBean().getSmAppl()
						.getSmFacilities())) {
			for (SmFacility smFac : getManagedBean().getSmAppl()
					.getSmFacilities()) {
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
	}

	private void setDefaultApproverReviewSection() {
		boolean containsAppRev = false;
		if (getManagedBean().getSmAppl().getSmApplSectionReviews() != null) {
			for (SmApplSectionReview rev : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (rev.getRoleType().equalsIgnoreCase(
						SupplierManagementConst.SECTION_REV_ROLE_APP)) {
					getManagedBean().setApprvrSecReview(rev);
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

	public void assignSelShareHolder(SmPersonnel shareHolderBean) {
		Locale locale = new FacesScopes().getUserLocale();
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			getManagedBean().setPersonnelInfoHeader(
					ResourceBundleUtils.getMessage(
							"lbl.sm.equity.owner.information",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		} else if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
			getManagedBean().setPersonnelInfoHeader(
					ResourceBundleUtils.getMessage(
							"lbl.sm.personnel.information",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		}

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
		if (shareHolderBean.getEpRole() == null) {
			shareHolderBean.setEpRole("");
		}
		assignSelShareHolderAddress(shareHolderBean);
		getManagedBean().setSelectedShareHolder(shareHolderBean);
		managePersonnelRoles();
		if (getManagedBean().getSelectedShareHolder().getIdentificationTypeId() != null) {
			getManagedBean().getSelectedShareHolder().setIdentificationType(
					SmApplicationUtil
							.retrievePmParameterById(getManagedBean()
									.getSelectedShareHolder()
									.getIdentificationTypeId()));
			getManagedBean()
					.setSelectedShareholderIdentificationType(
							SmCommonUtil
									.getParamDescriptionByLanguageAndPmParameter(getManagedBean()
											.getSelectedShareHolder()
											.getIdentificationType()));

		}
		if (getManagedBean().getSelectedShareHolder().getIdentificationType() == null) {
			getManagedBean().getSelectedShareHolder().setIdentificationType(
					new PmParameter());
		}
		getManagedBean().setRedirectToPersonPage(true);
		// TODO use this object to reset some values on back button click
		SmPersonnel per = new SmPersonnel();
		per.setIdentificationTypeId(shareHolderBean.getIdentificationTypeId());
		per.setIdentificationType(shareHolderBean.getIdentificationType());
		per.setIdentificationNo(shareHolderBean.getIdentificationNo());
		per.setSmPersonnelAddresses(shareHolderBean.getSmPersonnelAddresses());
		if (shareHolderBean.getEpRole() != null) {
			per.setEpRole(shareHolderBean.getEpRole());
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
	}

	public void assignSelShareHolderAddress(SmPersonnel shareHolderBean) {

		if (shareHolderBean.getSmPersonnelAddresses() != null
				&& shareHolderBean.getSmPersonnelAddresses().size() > 0) {
			for (SmPersonnelAddress personnelAddress : shareHolderBean
					.getSmPersonnelAddresses()) {
				if (personnelAddress.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
					if (personnelAddress.getSmAddress() != null
							&& personnelAddress.getSmAddress().getAddressId() != null) {
						getManagedBean().setShareHolderPermAddr(
								personnelAddress);
					}
				} else if (personnelAddress.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
					if (personnelAddress.getSmAddress() != null
							&& personnelAddress.getSmAddress().getAddressId() != null) {
						getManagedBean().setShareHolderMailAddr(
								personnelAddress);
					}
				} else if (personnelAddress.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
					if (personnelAddress.getSmAddress() != null
							&& personnelAddress.getSmAddress().getAddressId() != null) {
						getManagedBean().setShareHolderResdAddr(
								personnelAddress);
					}
				} else if (personnelAddress.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
					if (personnelAddress.getSmAddress() != null
							&& personnelAddress.getSmAddress().getAddressId() != null) {
						getManagedBean()
								.setShareHolderOffAddr(personnelAddress);
					}
				}
			}
		}
		getManagedBean().setSelectedShareHolder(shareHolderBean);
	}

	public void managePersonnelRoles() {
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
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
	}

	public void assignSelShrHldrJobFunc(SmStaffExperience exp) {
		getManagedBean().setSelectedJobFunction(exp);
	}

	public void assignSelShrHldrRelatedCompany(SmRelatedCompany com) {
		getManagedBean().setSelectedRelatedCompany(com);
	}

	public void assignSelShrHldrProfBody(SmAssociationBody asb) {
		getManagedBean().setSelectedStfPrfBdy(asb);
	}

	public void assignSelShrHldrAcademics(SmStaffAcademic aca) {
		getManagedBean().setSelectedStaffAcademic(aca);
	}

	private void setApplicationShareHolders(List<SmPersonnel> perList) {
		getManagedBean().setSmapplShareHolders(new ArrayList<SmPersonnel>());

		for (SmPersonnel per : perList) {

			if (per.isEquityOwner()) {
				getManagedBean().getSmapplShareHolders().add(per);
			}
		}

	}

	private void setActiveCategoryList() {
		List<SmSupplierCategory> catList = new ArrayList<>();
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.APPROVED_RECORD_STATUS) == 0
					|| cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
				catList.add(cat);
			}
		}
		getManagedBean().setActiveCategoryList(catList);
	}

	private void initBumiRecommendNote() {
		getManagedBean().setBumiRecommendationNote("");
		if (getManagedBean().getSmAppl() != null) {
			String applType = getManagedBean().getSmAppl().getApplType() == null ? ""
					: getManagedBean().getSmAppl().getApplType();
			String suppType = getManagedBean().getSmAppl().getSupplierType() == null ? ""
					: getManagedBean().getSmAppl().getSupplierType();

			getManagedBean().setBumiRecommendationNote(
					SmCommonUtil.displayBumiRecommendNote(getManagedBean()
							.getSmAppl().getSmSupplierBanks(), getManagedBean()
							.getPersonCalc(), getManagedBean()
							.getCurrentPageType(), applType, suppType,
							getManagedBean().getSmAppl().getSmCompanyBasic()
									.getBumiPercent(), getManagedBean()
									.isSupplierUser()));
		}

	}

	private void createSerFileIfNotExist(SmAppl smApplFromSuppProfile) {
		log.info("inside createSerFileIfNotExist....");
		getManagedBean().setReviewApplicationExist(true);

		if (getManagedBean().getApplicationNoParam() != null
				&& StringUtils.isNotBlank(getManagedBean()
						.getApplicationNoParam())) {
			String applNo = getManagedBean().getApplicationNoParam().substring(
					0, getManagedBean().getApplicationNoParam().length() - 4);
			String serType = getManagedBean()
					.getApplicationNoParam()
					.substring(
							getManagedBean().getApplicationNoParam().length() - 3,
							getManagedBean().getApplicationNoParam().length())
					.equals("APP") ? "APP" : "SUP";

			if (!SmCommonUtil.isSerFileExist(applNo, serType)) {
				if (getManagedBean().isViewSupplierProfile()) {
					if (smApplFromSuppProfile != null) {
						SmCommonUtil.createSerFile(smApplFromSuppProfile,
								serType);
					} else {
						log.info("smApplFromSuppProfile is null when attempting to create SER file....");
						getManagedBean().setReviewApplicationExist(false);
					}
				} else {
					MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
					reqDTO.setApplNo(applNo);
					IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_MANAGEMENT,
									IMofRegistrationSO.class);
					MofRegistrationResDTO resDTO = mofRegistrationSO
							.retrieveSmApplWithChildByApplNo(reqDTO);
					log.info("after retrieveSmApplWithChildByApplNo....");

					if (resDTO.getSmAppl() != null) {
						SmCommonUtil.createSerFile(resDTO.getSmAppl(), serType);
					} else {
						log.info("smAppl is null when attempting to create SER file....");
						getManagedBean().setReviewApplicationExist(false);
					}
				}
			}
		} else {
			getManagedBean().setReviewApplicationExist(false);
		}
	}

	private int getMaxTabByApplTypeAndCatType(String applType,
			String applCategoryType) {
		int maxTab = 0;
		switch (applType) {
		case ParameterConst.NEW_SM_APPLN_FOR_MOF:
			if (applCategoryType != null
					&& applCategoryType.equalsIgnoreCase("S")) {
				maxTab = 8;
			} else {
				maxTab = 6;
			}
			break;
		case ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES:
			if (applCategoryType != null
					&& applCategoryType.equalsIgnoreCase("S")) {
				maxTab = 7;
			} else {
				maxTab = 5;
			}
			break;
		case ParameterConst.NEW_SM_APPLN_FOR_BUMI:
			maxTab = 4;
			break;
		case ParameterConst.NEW_SM_APPLN_FOR_UPDATE:
			maxTab = 5;
			break;
		case ParameterConst.NEW_SM_APPLN_FOR_RENEW:
			if (applCategoryType != null
					&& applCategoryType.equalsIgnoreCase("S")) {
				maxTab = 8;
			} else {
				maxTab = 6;
			}
			break;
		default:
			break;
		}
		return maxTab;
	}

	private void setMaxTab() {
		int maxTab = 0;
		if (getManagedBean().getSmAppl() != null) {
			maxTab = getMaxTabByApplTypeAndCatType(getManagedBean().getSmAppl()
					.getApplType(), getManagedBean().getSmAppl()
					.getApplCategoryType());
		}
		getManagedBean().setMaxTab(maxTab);
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public void assignSiteVisitPopUp() {
		log.info("**** assignSiteVisitPopUp *** ");

	}
}
