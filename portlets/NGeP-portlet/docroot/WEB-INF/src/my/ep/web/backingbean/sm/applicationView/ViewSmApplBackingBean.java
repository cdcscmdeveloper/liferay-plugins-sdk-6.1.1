package my.ep.web.backingbean.sm.applicationView;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.certbody.CertBodyResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
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
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.ViewSmApplManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
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
					.getServiceObject("sm", ISmApplRevisionSO.class);

			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setDeSerFileName(SER_FILE_DIR
					+ getManagedBean().getApplicationNoParam() + SER_FILE_TYPE);
			SmApplRevisionResDTO resDTO = applRevisionSO
					.readApplicationHist(reqDTO);

			getManagedBean().setSmAppl(resDTO.getSmAppl());
			getManagedBean().setSmSupplier(resDTO.getSmAppl().getSmSupplier());
			setSupplierBusinessType(getManagedBean().getSmSupplier());
			getManagedBean()
					.setSupplierBusinessTypeVal(
							getDropDownViewOnlyValueByCode(
									SupplierManagementConst.SUPPLIER_BUSINESS_TYPE,
									getManagedBean().getSmSupplier()
											.getBusinessType()));
			setSmCompanyBasicCalculation();
			setApplicationShareHolders(getManagedBean().getSmAppl()
					.getSmPersonnels());
			setCategoryTypeConfirm();
			setApplicationFacilities(getManagedBean().getSmAppl()
					.getSmFacilities());
			setPersonnelInfoCalculationSummary();
			setDefaultApproverReviewSection();
			retrievePmCertBodyList();
			setSupplierCancellationRemarks(getManagedBean().getSmAppl()
					.getApplType(), getManagedBean().getSmAppl().getApplId());
			setPmStatusDesc(getManagedBean().getSmAppl().getPmStatus()
					.getStatusId().toString());
			getManagedBean().setReviewApplicationExist(true);
		} catch (NullPointerException ne) {
			getManagedBean().setReviewApplicationExist(false);
		} finally {
			applRevisionSO = null;
		}
	}

	public void navigateToRevAppl(){
		
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String applNo = originalServletRequest.getParameter("applicationNo");
		
		String redirectToAppl = "";
		getManagedBean().setApplicationNoParam(applNo);
		initApplication();
		String applType = getManagedBean().getSmAppl().getApplType();
		if(applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)){
			redirectToAppl = "/xhtml/sm/revisionSmApplView/newMofApplicationView/smViewMofRegSideMenu_BasePage.xhtml";
		} else if(applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)){
			redirectToAppl = "/xhtml/sm/revisionSmApplView/bumiputeraApplicationView/smViewBumiSideMenu_BasePage.xhtml";
		} else if(applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)){
			redirectToAppl = "/xhtml/sm/revisionSmApplView/addAdditionalCategoryCodesView/smViewAddtnlCatApplnSideMenu_BasePage.xhtml";
		} else if(applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)){
			redirectToAppl = "/xhtml/sm/revisionSmApplView/renewMofApplicationView/smViewRenewRegSideMenu_BasePage.xhtml";
		} else if(applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)){
			redirectToAppl = "/xhtml/sm/revisionSmApplView/updateMofDetailsView/smViewUpdateMofSideMenu_BasePage.xhtml";
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, redirectToAppl);
//		return redirectToAppl;
	}
	
	public void navigateToViewSuppProfile(){
		
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		
		SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
		
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setSupplierId(supplier.getSupplierId());
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm",
						ISupplierSearchSO.class);
		SupplierSearchResDTO smResDTO = supplierSearchSO
				.retriveLatestApplBySupplierId(supplierSearchReqDTO);
		
		if(smResDTO.getSmAppl() != null){
		
			String applNo = smResDTO.getSmAppl().getApplNo();
			
			String redirectToAppl = "";
			getManagedBean().setApplicationNoParam(applNo + "_APP");
			initApplication();
			String applType = getManagedBean().getSmAppl().getApplType();
			if(applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)){
				redirectToAppl = "/xhtml/sm/revisionSmApplView/newMofApplicationView/smViewMofRegSideMenu_BasePage.xhtml";
			} else if(applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)){
				redirectToAppl = "/xhtml/sm/revisionSmApplView/bumiputeraApplicationView/smViewBumiSideMenu_BasePage.xhtml";
			} else if(applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)){
				redirectToAppl = "/xhtml/sm/revisionSmApplView/addAdditionalCategoryCodesView/smViewAddtnlCatApplnSideMenu_BasePage.xhtml";
			} else if(applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)){
				redirectToAppl = "/xhtml/sm/revisionSmApplView/renewMofApplicationView/smViewRenewRegSideMenu_BasePage.xhtml";
			} else if(applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)){
				redirectToAppl = "/xhtml/sm/revisionSmApplView/updateMofDetailsView/smViewUpdateMofSideMenu_BasePage.xhtml";
			}
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, redirectToAppl);
		}
	}

	private void initApplication() {
		ISmApplRevisionSO applRevisionSO = null;

		try {
			applRevisionSO = (ISmApplRevisionSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", ISmApplRevisionSO.class);

			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			
			String appNo = getManagedBean().getApplicationNoParam().substring(0, getManagedBean().getApplicationNoParam().length() - 4);
			
			String filePath = Calendar.getInstance().get(Calendar.YEAR)
					+ "/SM/" + appNo
					+ "/Revision/";
			String tempDir = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
			tempDir += "/" + filePath;
			
			reqDTO.setDeSerFileName(tempDir + getManagedBean().getApplicationNoParam() + SER_FILE_TYPE);
			SmApplRevisionResDTO resDTO = applRevisionSO
					.readApplicationHist(reqDTO);

			getManagedBean().setSmAppl(resDTO.getSmAppl());
			getManagedBean().setSmSupplier(resDTO.getSmAppl().getSmSupplier());
			setSupplierBusinessType(getManagedBean().getSmSupplier());
			getManagedBean()
					.setSupplierBusinessTypeVal(
							getDropDownViewOnlyValueByCode(
									SupplierManagementConst.SUPPLIER_BUSINESS_TYPE,
									getManagedBean().getSmSupplier()
											.getBusinessType()));
			setSmCompanyBasicCalculation();
			setApplicationShareHolders(getManagedBean().getSmAppl()
					.getSmPersonnels());
			setCategoryTypeConfirm();
			setApplicationFacilities(getManagedBean().getSmAppl()
					.getSmFacilities());
			setPersonnelInfoCalculationSummary();
			setDefaultApproverReviewSection();
			retrievePmCertBodyList();
			setSupplierCancellationRemarks(getManagedBean().getSmAppl()
					.getApplType(), getManagedBean().getSmAppl().getApplId());
			setPmStatusDesc(getManagedBean().getSmAppl().getPmStatus()
					.getStatusId().toString());
			getManagedBean().setReviewApplicationExist(true);
		} catch (NullPointerException ne) {
			getManagedBean().setReviewApplicationExist(false);
		} finally {
			constructMessage();
			applRevisionSO = null;
		}
	}
	
	
	// temp added for testing
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

	public void viewApplication() {
		retrievePmCertBodyList();
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

	private SmRemaksResDTO getRemarksByDoc(String docType, Long docId) {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setDocId(docId);
		reqDTO.setDocType(docType);
		ISmRemarksSO reqSO = (ISmRemarksSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISmRemarksSO.class);
		SmRemaksResDTO resDTO = reqSO.retrieveSmRemarksByDocument(reqDTO);
		return resDTO;
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

	private void retrievePmCertBodyList() {
		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveCertifiedBodyList();
		this.getManagedBean().setCertBodyLs(resDTO.getCertBodyList());
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
				.getServiceObject("pm", IStatusSO.class);
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
		for (SmChequeSignatory smChequeSignatoryTmp : shareBankBean
				.getSmChequeSignatories()) {
			sb.append(smChequeSignatoryTmp.getSmPersonnel().getName()).append(
					"\n");
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
		setSmEquityCompanyPercentageCalculation();
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
		if (consultantBean.getSmCompanyExperienceCats() != null
				&& !consultantBean.getSmCompanyExperienceCats().isEmpty()) {
			populateChildCategoryByParent(consultantBean
					.getSmCompanyExperienceCats().get(0).getPmCategoryL1()
					.getCategoryL1Id(), new PmCategoryL2(), new PmCategoryL3());
			populateSubChildCategoryByChild(consultantBean
					.getSmCompanyExperienceCats().get(0).getPmCategoryL2()
					.getCategoryL2Id(), new PmCategoryL3());
		}
	}

	public void assignSelAssocBody(SmAssociationBody assocBean) {
		getManagedBean().setSelectedAssocBody(assocBean);
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

	private PmParameter retrievePmParameterById(Long paramId) {
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(paramId);
		ParameterResDTO resDTO = reqSO.retrieveParameterById(reqDTO);

		return resDTO.getPmParameter();
	}

	private List<PmState> retrievePmStateList(long countryId) {
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		return resDTO.getStateList();
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
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);

		return resDTO.getCategoryL3List();
	}

	private void resetChildCategory(PmCategoryL2 catL2) {
		catL2.setCategoryL2Id(null);

	}

	private void resetSubChildCategory(PmCategoryL3 catL3) {
		catL3.setCategoryL3Id(null);

	}

	public void onTabChange(TabChangeEvent event) {
		FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab"
				+ event.getTab().getId());

		FacesContext.getCurrentInstance().addMessage(null, msg);
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

	private void setSmCompanyBasicCalculation() {
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
			} else {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setBumiEquity(new BigDecimal(0));
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
			} else {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setNonBumiEquity(new BigDecimal(0));
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
			} else {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setGovtEquity(new BigDecimal(0));
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
			} else {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setGovtUnitrustEquity(new BigDecimal(0));
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
			} else {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setForeignCompanyEquity(new BigDecimal(0));
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
			} else {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setForeignerEquity(new BigDecimal(0));
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
			} else {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setLocalCompanyEquity(new BigDecimal(0));
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
			} else {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setAuthorizedCapital(new BigDecimal(0));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getTotalShare() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setTotalShare(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic().getTotalShare()
										.setScale(2, RoundingMode.HALF_UP));
			} else {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setTotalShare(new BigDecimal(0));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getTotalMember() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setTotalMember(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic().getTotalMember());
			} else {
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.setTotalMember(new BigDecimal(0));
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
										.divide(getManagedBean().getSmAppl()
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
										.divide(getManagedBean().getSmAppl()
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
										.divide(getManagedBean().getSmAppl()
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
										.divide(getManagedBean().getSmAppl()
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
										.divide(getManagedBean().getSmAppl()
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
										.divide(getManagedBean().getSmAppl()
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
										.divide(getManagedBean().getSmAppl()
												.getSmCompanyBasic()
												.getPaidUpCapital(), 4,
												RoundingMode.HALF_UP)
										.multiply(mul)
										.setScale(2, RoundingMode.HALF_UP));
			}

		} catch (NumberFormatException ne) {
			throw new EpException(MessageConst.ERROR_722522);
		}
	}

	private void setSmEquityCompanyPercentageCalculation() {
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

	private void setApplicationFacilities(List<SmFacility> smFacilities) {
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
				if (!smFac.getOwnershipType().equalsIgnoreCase("O")) {
					getManagedBean().setBasicOffFacStates(
							retrievePmStateList(smFac.getSmAddress()
									.getPmCountry().getCountryId()));
				}
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

	private void setDefaultApproverReviewSection() {
		if (!getManagedBean().getSmAppl().getSmApplSectionReviews().isEmpty()) {
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
		manageEquityLimit(shareHolderBean);
		if (getManagedBean().getSelectedShareHolder().getIdentificationTypeId() != null) {
			getManagedBean().getSelectedShareHolder()
					.setIdentificationType(
							retrievePmParameterById(getManagedBean()
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

	private void manageEquityLimit(SmPersonnel personnel) {
		if (personnel.isEquityOwner()
				&& !getManagedBean()
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)) {
			getManagedBean().setValidEquityLimit(true);
		} else if (getManagedBean().getBusinessTypeVal().equalsIgnoreCase(
				SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)
				|| (getManagedBean()
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT) && getManagedBean()
						.getSmapplShareHolders().size() >= 1)
				|| (getManagedBean()
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT) && getManagedBean()
						.getSmapplShareHolders().size() >= 20)) {
			getManagedBean().setValidEquityLimit(false);

		} else {
			getManagedBean().setValidEquityLimit(true);
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

	private void setSupplierBusinessType(SmSupplier sup) {
		if (sup.getBusinessType()
				.equalsIgnoreCase(
						SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)) {
			getManagedBean()
					.setBusinessTypeVal(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT);
		} else if (sup
				.getBusinessType()
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
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT);

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
		} else if (sup.getBusinessType().equalsIgnoreCase(
				SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP)) {
			getManagedBean()
					.setBusinessTypeVal(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT);
		}
	}

	public void retrieveBusinessTypeList() {
		SelectItem[] selectItems = getDropdownCodeValues(SupplierManagementConst.SUPPLIER_BUSINESS_TYPE);
		this.getManagedBean().setBusinessTypeLs(selectItems);
	}

	private void constructMessage() {
		if (!getManagedBean().isReviewApplicationExist()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700054,
									new String[] { getManagedBean()
											.getApplicationNoParam() },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}
	}

	private void redirectToViewBasePage() {
		String pageType = new FacesScopes().getParameter("page");
		
		if(pageType!=null){
			FacesContext fc = FacesContext.getCurrentInstance();
			switch (pageType) {
			case "N":
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(
								fc,
								null,
								"/xhtml/sm/revisionSmApplView/newMofApplicationView/smViewMofRegSideMenu_BasePage.xhtml");
				break;
			case "A":
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(
								fc,
								null,
								"/xhtml/sm/revisionSmApplView/addAdditionalCategoryCodesView/smViewAddtnlCatApplnSideMenu_BasePage.xhtml");
				break;
			case "B":
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(
								fc,
								null,
								"/xhtml/sm/revisionSmApplView/bumiputeraApplicationView/smViewBumiSideMenu_BasePage.xhtml");
				break;
			case "U":
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(
								fc,
								null,
								"/xhtml/sm/revisionSmApplView/updateMofDetailsView/smViewUpdateMofSideMenu_BasePage.xhtml");
				break;
			case "R":
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(
								fc,
								null,
								"/xhtml/sm/revisionSmApplView/renewMofApplicationView/smViewRenewRegSideMenu_BasePage.xhtml");
				break;
			default:
				break;
			}
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
