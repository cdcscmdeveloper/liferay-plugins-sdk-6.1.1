package my.ep.web.backingbean.sm.applicationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
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
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.ViewSmApplManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.liferay.compat.portal.util.PortalUtil;

@ManagedBean(name = "viewSmApplBackingBean")
@RequestScoped
public class ViewSmApplBackingBean extends
		BaseBackingBean<ViewSmApplManagedBean> {

	public static Logger log = Logger.getLogger(ViewSmApplBackingBean.class);

	private StreamedContent file;
	private String redirect = "REVIEW APPLICATION TESTING PAGE";
	private static final String SER_FILE_DIR = "/TestHistRev/";
	private static final String SER_FILE_TYPE = ".ser";

	public ViewSmApplBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

//		if (reloadPreviousApplication()) {
//			initPreviousApplication();
//		}
//		constructMessage();
	}

	private void initPreviousApplication() {
		ISmApplRevisionSO applRevisionSO = null;

		try {
			applRevisionSO = (ISmApplRevisionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISmApplRevisionSO.class);

			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setDeSerFileName(SER_FILE_DIR
					+ getManagedBean().getApplicationNoParam() + SER_FILE_TYPE);
			SmApplRevisionResDTO resDTO = applRevisionSO
					.readApplicationHist(reqDTO);

			setApplication(resDTO);
		} catch (NullPointerException ne) {
			getManagedBean().setReviewApplicationExist(false);
		} finally {
			applRevisionSO = null;
		}
	}

	public void navigateToRevAppl() {

		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String applNo = originalServletRequest.getParameter("applicationNo");

		getManagedBean().setApplicationNoParam(applNo);
		initApplication();
		String applType = getManagedBean().getSmAppl().getApplType();
		String redirectToAppl = SmApplicationUtil
				.getReviewApplicationBasePage(applType);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, redirectToAppl);
		// return redirectToAppl;
	}

	public void navigateToViewSuppProfile() {
		SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();

		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setSupplierId(supplier.getSupplierId());
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierSearchSO.class);
		SupplierSearchResDTO smResDTO = supplierSearchSO
				.retriveLatestApplBySupplierId(supplierSearchReqDTO);

		if (smResDTO.getSmAppl() != null) {

			String applNo = smResDTO.getSmAppl().getApplNo();
			getManagedBean().setApplicationNoParam(applNo + "_APP");
			initApplication();
			String applType = getManagedBean().getSmAppl().getApplType();
			String redirectToAppl = SmApplicationUtil
					.getReviewApplicationBasePage(applType);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, redirectToAppl);
		}
	}

	private void initApplication() {
		ISmApplRevisionSO applRevisionSO = null;

		try {
			applRevisionSO = (ISmApplRevisionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISmApplRevisionSO.class);

			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();

			String appNo = getManagedBean().getApplicationNoParam().substring(
					0, getManagedBean().getApplicationNoParam().length() - 4);

			String filePath = Calendar.getInstance().get(Calendar.YEAR)
					+ "/SM/" + appNo + "/Revision/";
			String tempDir = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
			tempDir += "/" + filePath;

			reqDTO.setDeSerFileName(tempDir
					+ getManagedBean().getApplicationNoParam() + SER_FILE_TYPE);
			SmApplRevisionResDTO resDTO = applRevisionSO
					.readApplicationHist(reqDTO);

			setApplication(resDTO);
		} catch (NullPointerException ne) {
			getManagedBean().setReviewApplicationExist(false);
		} finally {
			constructMessage();
			applRevisionSO = null;
		}
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

	private boolean reloadPreviousApplication() {
		boolean reload = false;
		String applicationNo = new FacesScopes().getParameter("applicationNo");

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
			setTabParam();
		}

		return reload;
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

	private void setTabParam() {
		String tabParam = new FacesScopes().getParameter("tab");

		try {
			if (tabParam != null) {
				getManagedBean().setTabActiveIndex(Integer.parseInt(tabParam));
			}
		} catch (NumberFormatException ne) {
			getManagedBean().setTabActiveIndex(0);
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
		//initTesting();
	}
	
	private void initTesting(){
		//testing user
		getManagedBean().setApproverUser(true);
		
		SmApplSectionReview apprSec = new SmApplSectionReview();
		apprSec.setBasicRemark("approver basic remark");
		apprSec.setStaffRemark("approver staff remark");
		apprSec.setOfficeFacilityRemark("approver office remark");
		apprSec.setCertRemark("approver cert remark");
		apprSec.setAssocBodyRemark("approver assoc body remark");
		apprSec.setTrnsNetworkRemark("approver trnsnetwork remark");
		apprSec.setBranchRemark("approver branch remark");
		apprSec.setAssociateRemark("approver associate remark");
		apprSec.setExperienceRemark("approver experience remark");
		apprSec.setCapitalRemark("approver capital remark");
		apprSec.setShareholderRemark("approver shareholder remark");
		apprSec.setEquityCompRemark("approver equitycomp remark");
		apprSec.setPersonnelRemark("approver personnel remark");
		apprSec.setCategoryRemark("approver category remark");
		getManagedBean().setApprvrSecReview(apprSec);
		
		SmApplSectionReview poSec = new SmApplSectionReview();
		poSec.setBasicRemark("po basic remark");
		poSec.setStaffRemark("po staff remark");
		poSec.setOfficeFacilityRemark("po office remark");
		poSec.setCertRemark("po cert remark");
		poSec.setAssocBodyRemark("po assoc body remark");
		poSec.setTrnsNetworkRemark("po trnsnetwork remark");
		poSec.setBranchRemark("po branch remark");
		poSec.setAssociateRemark("po associate remark");
		poSec.setExperienceRemark("po experience remark");
		poSec.setCapitalRemark("po capital remark");
		poSec.setShareholderRemark("po shareholder remark");
		poSec.setEquityCompRemark("po equitycomp remark");
		poSec.setPersonnelRemark("po personnel remark");
		poSec.setCategoryRemark("po category remark");
		getManagedBean().setPoSecReview(poSec);
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

		setDefaultApproverReviewSection();
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
			if(i<=3){
				sb.append(smChequeSignatoryTmp.getSmPersonnel().getName()).append(
						",");
			}else{
				sb.append("<br />");
				i = 0;
			}
		}
		if(sb.length()>0){
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
		getManagedBean().setSelectedFactoryFacility(facilityBean);
		getManagedBean().setFacilityType("FACTORY");
	}

	public void assignSelWorkshop(SmFacility facilityBean) {
		getManagedBean().setSelectedWorkShopFacility(facilityBean);
		getManagedBean().setFacilityType("WORKSHOP");
	}

	public void assignSelDockyard(SmFacility facilityBean) {
		getManagedBean().setSelectedDockyardFacility(facilityBean);
		getManagedBean().setFacilityType("DOCKYARD");
	}

	public void assignSelStore(SmFacility facilityBean) {
		getManagedBean().setSelectedStoreFacility(facilityBean);
		getManagedBean().setFacilityType("STORE");
	}

	public void assignSelManufacturer(SmManufacturingDtl manBean) {
		getManagedBean().setSelectedManufacturer(manBean);
	}

	public void assignSelMachinery(SmMachineDtl macBean) {
		getManagedBean().setSelectedMachine(macBean);

		Locale locale = new FacesScopes().getUserLocale();

		switch (getManagedBean().getSelectedMachine().getPurchaseMethod()) {
		case "A":
			getManagedBean().setSelectedMachineryPurchaseMethod(
					ResourceBundleUtils.getMessage("lbl.sm.machine.cash",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			break;
		case "B":
			getManagedBean().setSelectedMachineryPurchaseMethod(
					ResourceBundleUtils.getMessage(
							"lbl.sm.machine.credit.card",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			break;
		case "C":
			getManagedBean().setSelectedMachineryPurchaseMethod(
					ResourceBundleUtils.getMessage("lbl.sm.machine.debit.card",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			break;
		case "D":
			getManagedBean().setSelectedMachineryPurchaseMethod(
					ResourceBundleUtils.getMessage(
							"lbl.sm.machine.online.banking",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			break;
		default:
			break;

		}
	}

	public void assignSelCompanyExperience(SmCompanyExperience consultantBean) {
		getManagedBean().setSelectedCompanyExperience(consultantBean);
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

	private void constructMessage() {
		if (!getManagedBean().isReviewApplicationExist()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_721211,
									new String[] { getManagedBean()
											.getApplicationNoParam() },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

}
