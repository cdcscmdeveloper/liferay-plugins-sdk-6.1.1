package my.ep.web.backingbean.sm.applicationView;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmCompanyExperienceCat;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
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
import my.ep.persistence.entity.sm.SmSupplierBranchBank;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.BaseReviewApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.eclipse.persistence.exceptions.ValidationException;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.liferay.compat.portal.util.PortalUtil;

public abstract class BaseReviewApplicationBackingBean<T extends BaseReviewApplicationManagedBean>
		extends BaseBackingBean<T> {

	public static Logger log = Logger
			.getLogger(BaseReviewApplicationBackingBean.class);
	private StreamedContent file;
	private static final String SER_FILE_TYPE = ".ser";
	private static final String FOLDER_NAME_SM = "SM";
	private static final String FOLDER_NAME_REVISION = "Revision";
	public static final int SEARCH_BY_COMPANY_NAME = 1;
	public static final int SEARCH_BY_BUSINESS_REG_NO = 2;
	public static final int SEARCH_BY_EP_NO = 3;

	/**
	 * Initialized and construct BaseReviewApplicationBackingBean
	 */
	public BaseReviewApplicationBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

	/**
	 * Initialized all related steps in View Application
	 */
	public void initMain() {
		if (reloadApplication()) {
			initApplication(null);
			setMaxTab();
		}
		setTabParam();
	}

	/**
	 * Initialized all related smAppl from SER file
	 * 
	 * @param SmAppl
	 *            smAppl
	 */
	public void initApplication(SmAppl smAppl) {
		ISmApplRevisionSO applRevisionSO = null;

		try {
			// set revision application type
			setRevisionApplType();

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
				setApplication(resDTO.getSmAppl());
				getManagedBean().setSmSupplier(resDTO.getSmSupplier());
			}

		} catch (NullPointerException ne) {
			log.error("***************Error init revision appl:"
					+ ne.getMessage());
			getManagedBean().setReviewApplicationExist(false);
		} finally {
			applRevisionSO = null;
		}

		// show message if supp profile not ready
		if (!getManagedBean().isReviewApplicationExist()) {
			// if failed to load ser file, then load using db (previous and
			// original application)
			if (getManagedBean().isOriginal()
					|| getManagedBean().isPreviousApproved()) {
				MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
				mofRegistrationReqDTO.setApplNo(getManagedBean()
						.getApplicationNoParam());
				getManagedBean().setSmAppl(
						SmServiceLookup
								.getMofRegistrationSO()
								.retrieveSmApplWithChildByApplNo(
										mofRegistrationReqDTO).getSmAppl());
			} else {
				SmCommonUtil.redirectToPage(
						SmApplicationUtil.getApplicationErrorPage(),
						MessageConst.INFO_721212, null);
			}
		}

	}

	/**
	 * Check if need to reload application
	 * 
	 * @return boolean
	 */
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

	/**
	 * Download supporting document
	 * 
	 * @param String
	 *            fileName
	 * @param String
	 *            filePath
	 */
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

	/**
	 * View check signatory by SmSupplierBank
	 * 
	 * @param SmSupplierBank
	 *            signAcctBean
	 */
	public void viewSignatories(SmSupplierBank signAcctBean) {
		getManagedBean().setSelectedSignatoryBankAcct(signAcctBean);
	}

	/**
	 * Get PmParameterDesc by language
	 * 
	 * @param PmParameter
	 *            param
	 * @return String
	 */
	public String getParameterDescByPmParameter(PmParameter param) {
		return getParameterDesc(param, new FacesScopes().getUserLocale());
	}

	/**
	 * Get authorized personnel account status label
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @return String
	 */
	public String getAuthorizedPersonnelAccountStatus(SmPersonnel smPersonnel) {
		String accStatus = "";
		if (smPersonnel.getEpRole() != null
				&& StringUtils.isNotBlank(smPersonnel.getEpRole())) {
			if (smPersonnel.getPmUser() == null) {
				accStatus = SmCommonUtil.getMessage(
						"lbl.sm.activation.status.pending", null);
			} else {
				if (SmCommonUtil.isPersonnelAlreadyDeactivate(smPersonnel)) {
					accStatus = SmCommonUtil.getMessage(
							"lbl.sm.activation.status.pending", null);
				} else {
					accStatus = SmCommonUtil.getMessage(
							"lbl.sm.activation.status.activated", null);
				}
			}
		}
		return accStatus;
	}

	/**
	 * Get SER file name and path
	 * 
	 * @param String
	 *            applNoParam
	 * @return String
	 */
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

	/**
	 * Get drop down value for display purpose by id
	 * 
	 * @param String
	 *            parameterType
	 * @param String
	 *            parameterId
	 * @return String
	 */
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

	/**
	 * Get active MofAccount by supplier
	 * 
	 * @return SmMofAccount
	 */
	public SmMofAccount getSingleSmMofAccount() {
		SmMofAccount smMofAccount = new SmMofAccount();
		try {
			smMofAccount = SmCommonUtil.getMofAccountBySuppIdAndSuppType(
					getManagedBean().getSmSupplier().getSupplierId(),
					getManagedBean().getSmAppl().getSupplierType());
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return smMofAccount;
	}

	/**
	 * Get MofCert by applId
	 * 
	 * @return SmMofCert
	 */
	public SmMofCert getMofCert() {
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
		return SmServiceLookup.getVirtualCertSO()
				.retrieveMofCertByApplId(reqDTO).getSmMofCert();
	}

	/**
	 * Set tab value for first load
	 */
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

	/**
	 * Set tab value
	 * 
	 * @param int outputTab
	 * @param String
	 *            outputPageType
	 */
	public void setTabValue(int outputTab, String outputPageType) {
		getManagedBean().setTabActiveIndex(outputTab);
		getManagedBean().setRedirectToPersonPage(false);
		getManagedBean().setCurrentPageType(outputPageType);
		getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
	}

	/**
	 * Set default address to Malaysia
	 * 
	 * @return SmAddress
	 */
	private SmAddress setDefaultAddr() {
		SmAddress addr = new SmAddress();
		PmCountry cntry = new PmCountry();
		if (getManagedBean().getDefPmCountry() != null
				&& getManagedBean().getDefPmCountry().getCountryId() != null) {
			cntry = getManagedBean().getDefPmCountry();
		} else {
			cntry = SmCommonUtil
					.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		}
		addr.setPmCity(new PmCity());
		addr.setPmCountry(cntry);
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());

		return addr;
	}

	/**
	 * Set revision application steps
	 * 
	 * @param SmApplRevisionResDTO
	 *            resDTO
	 */
	public void setApplication(SmAppl smAppl) {
		initStep0BaseInfo(smAppl);
		initStep1CompanyInfo();
		initStep2BranchCompanyInfo();
		initStep3PersonnelInfo();
		initStep4CaptialAndEquityInfo();
		initStep5BankInfo();
		initStep6RegisterCategoryCode();
		initStep6_1ViewFactoryInfo();
		initStep6_2EquipmentInfo();
		initStep7SupportingDocumentsInfo();
		initStep8_9PoApproverDefaultDecision();
		initBumiRecommendNote();
	}

	/**
	 * Initialized base information
	 * 
	 * @param SmApplRevisionResDTO
	 *            resDTO
	 */
	private void initStep0BaseInfo(SmAppl smAppl) {
		getManagedBean().setSmAppl(smAppl);
		
		if(getManagedBean().getSmSupplier()==null){
			try{
				getManagedBean().setSmSupplier(getManagedBean().getSmAppl().getSmSupplier());
			}catch(ValidationException ex){
				log.debug(ex.getMessage());
			}
		}

		getManagedBean().setBusinessTypeVal(
				SmApplicationUtil.assignSupplierBusinessType(getManagedBean()
						.getSmSupplier()));
		getManagedBean()
				.setSupplierBusinessTypeVal(
						SmCommonUtil
								.getDropDownViewOnlyValueByCode(
										getManagedBean().getSmAppl()
												.getSupplierType().equals("K") ? ParameterTypeConst.SUPPLIER_BUSINESS_TYPE
												: ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT,
										getManagedBean().getSmSupplier()
												.getBusinessType()));
		populateLocalAuthority(getManagedBean().getSmSupplier(), smAppl
				.getSmCompanyBasic().getSmCompanyAddresses().get(0)
				.getSmAddress());
		SelectItem item = (SelectItem) EntityHelper
				.getEntityBySinglePropertyFromList(getManagedBean()
						.getSmSupplier().getLocalAuthorityId(),
						getManagedBean().getLocalAuthorityItems(), "value");
		if (item != null) {
			getManagedBean().setLocalAuthority(item.getLabel());
		}
		if (getManagedBean().isViewSupplierProfile()) {
			getManagedBean().setSmMofCert(getMofCert());
			if (getManagedBean().getSmMofCert() == null) {
				getManagedBean().setSmMofAccount(getSingleSmMofAccount());
			}
		}
		// init current regsitration status description
		getManagedBean().setCurrRegStatus(
				SmApplicationUtil.getCurrRegStatusDescBySmAppl(getManagedBean()
						.getSmAppl()));
		getManagedBean().setReviewApplicationExist(true);
	}

	/**
	 * Initialized revision application company information step
	 */
	private void initStep1CompanyInfo() {
		// companyBasicInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmCompanyBasic() == null) {
			getManagedBean().getSmAppl()
					.setSmCompanyBasic(new SmCompanyBasic());
		} else {
			SmCompanyBasic activeCompBasic = getCompanyBasicByRevNo();
			getManagedBean().getSmAppl().setSmCompanyBasic(activeCompBasic);
		}

		// staffInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmStaffTotals() == null) {
			getManagedBean().getSmAppl().setSmStaffTotals(
					new ArrayList<SmStaffTotal>());
		} else {
			getManagedBean().getSmAppl().setSmStaffTotals(
					getStaffTotalByRevNo());
		}

		// officeFacilityTypeFormViewOnly
		getManagedBean().setBasicCompanyFacility(new SmFacility());
		for (SmFacility smFac : getManagedBean().getSmAppl().getSmFacilities()) {
			if ((smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE) || smFac
					.getFacilityType()
					.equalsIgnoreCase(
							SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE))
					&& smFac.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
				this.getManagedBean().setBasicCompanyFacility(smFac);
			}
		}

		// certificateInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmSupplierCerts() == null) {
			getManagedBean().getSmAppl().setSmSupplierCerts(
					new ArrayList<SmSupplierCert>());
		} else {
			List<SmSupplierCert> activeCertList = new ArrayList<SmSupplierCert>();
			for (SmSupplierCert cert : getManagedBean().getSmAppl()
					.getSmSupplierCerts()) {
				if (cert.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					activeCertList.add(cert);
				}
			}
			getManagedBean().getSmAppl().setSmSupplierCerts(activeCertList);
		}

		// professionalBodyInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmAssociationBodies() == null) {
			getManagedBean().getSmAppl().setSmAssociationBodies(
					new ArrayList<SmAssociationBody>());
		} else {
			List<SmAssociationBody> activeAssocBodies = new ArrayList<SmAssociationBody>();
			for (SmAssociationBody assocBody : getManagedBean().getSmAppl()
					.getSmAssociationBodies()) {
				if (assocBody.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					activeAssocBodies.add(assocBody);
				}
			}
			getManagedBean().getSmAppl().setSmAssociationBodies(
					activeAssocBodies);
		}

		// init company basic gst
		getManagedBean().setHqGst(getSupplierGst(null));

	}

	/**
	 * Initialized branch company information step
	 */
	private void initStep2BranchCompanyInfo() {
		// branchCompanyInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmSupplierBranches() == null) {
			getManagedBean().getSmAppl().setSmSupplierBranches(
					new ArrayList<SmSupplierBranch>());
		} else {
			getManagedBean().getSmAppl().setSmSupplierBranches(
					getSmSupplierBranchByRevNo());
			for (SmSupplierBranch smSupplierBranch : getManagedBean()
					.getSmAppl().getSmSupplierBranches()) {
				if (smSupplierBranch.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					smSupplierBranch.setGst(getSupplierGst(smSupplierBranch
							.getBranchCode() != null ? smSupplierBranch
							.getBranchCode() : smSupplierBranch
							.getSupplierBranchId()));
					if (smSupplierBranch.getGst() == null) {
						smSupplierBranch.setGst(new SmGstSupplier());
					}
					if (smSupplierBranch.getSmPersonnel() == null) {
						smSupplierBranch.setSmPersonnel(new SmPersonnel());
					}
					SmApplicationUtil
							.initGstSuppDocs(smSupplierBranch.getGst());
				}
			}
		}

		// associatedCompanyInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmAssociateCompanies() == null) {
			getManagedBean().getSmAppl().setSmAssociateCompanies(
					new ArrayList<SmAssociateCompany>());
		} else {
			getManagedBean().getSmAppl().setSmAssociateCompanies(
					getAssocCompanyByRevNo());
		}

		// companyProjectsExperienceInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmCompanyExperiences() == null) {
			getManagedBean().getSmAppl().setSmCompanyExperiences(
					new ArrayList<SmCompanyExperience>());
		} else {
			List<SmCompanyExperience> activeCompanyExperiences = new ArrayList<SmCompanyExperience>();
			for (SmCompanyExperience exp : getManagedBean().getSmAppl()
					.getSmCompanyExperiences()) {
				if (exp.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					activeCompanyExperiences.add(exp);
				}
			}
			getManagedBean().getSmAppl().setSmCompanyExperiences(
					activeCompanyExperiences);
		}

	}

	/**
	 * Initialized personnel information step
	 */
	private void initStep3PersonnelInfo() {
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
				if (getManagedBean().getSmSupplier() != null
						&& SmCommonUtil.isSoftcertReadyForRenewal(
								getManagedBean().getSmSupplier()
										.getSupplierId(), smPersonnel)) {
					smPersonnel
							.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_READY_FOR_RENEWAL);
				}
				if (getManagedBean().getSmSupplier() != null) {
					SmCommonUtil.setRejectedPersonnelSoftcert(smPersonnel,
							getManagedBean().getSmSupplier().getSupplierId());
				}
			}
			getManagedBean().getSmAppl().setSmPersonnels(getPersonnelByRevNo());
			setPersonnelInfoCalculationSummary();
		}
		SmApplicationUtil.sortPersonnelByRoleInCompany(getManagedBean()
				.getSmAppl());
	}

	/**
	 * Initialized capital and equity information step
	 */
	private void initStep4CaptialAndEquityInfo() {
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
		} else {
			List<SmEquityCompany> activeEquityCompanies = new ArrayList<SmEquityCompany>();
			for (SmEquityCompany eqComp : getManagedBean().getSmAppl()
					.getSmEquityCompanies()) {
				if (eqComp.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					activeEquityCompanies.add(eqComp);
				}
			}
			getManagedBean().getSmAppl().setSmEquityCompanies(
					activeEquityCompanies);
		}
	}

	/**
	 * Initialized Bank Information step
	 */
	private void initStep5BankInfo() {
		getManagedBean().setSmSupplierBankList(getSupplierBankByRevNo());
		getManagedBean().setSelectedSignatoryBankAcct(new SmSupplierBank());
	}

	/**
	 * Initialized register category code step
	 */
	private void initStep6RegisterCategoryCode() {
		if (getManagedBean().getSmAppl().getSmSupplierCategories() == null) {
			getManagedBean().getSmAppl().setSmSupplierCategories(
					new ArrayList<SmSupplierCategory>());
		} else {
			setActiveCategoryList();
		}
		setCategoryTypeConfirm();
	}

	/**
	 * Initialized Factory Information step
	 */
	private void initStep6_1ViewFactoryInfo() {
		// factoryOrWorkshopInfoFormViewOnly/dockyardInfoFormViewOnly/workshopInfoFormViewOnly/storeInfoFormViewOnly
		setApplicationFacilities();
	}

	/**
	 * Initialized Equipment Information step
	 */
	private void initStep6_2EquipmentInfo() {
		// machineryInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmMachineDtls() == null) {
			getManagedBean().getSmAppl().setSmMachineDtls(
					new ArrayList<SmMachineDtl>());
		} else {
			List<SmMachineDtl> activeMachineDtls = new ArrayList<SmMachineDtl>();
			for (SmMachineDtl machineDtl : getManagedBean().getSmAppl()
					.getSmMachineDtls()) {
				if (machineDtl.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					activeMachineDtls.add(machineDtl);
				}
			}
			getManagedBean().getSmAppl().setSmMachineDtls(activeMachineDtls);
		}
		// manufacturingOrAssmblyInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmManufacturingDtls() == null) {
			getManagedBean().getSmAppl().setSmManufacturingDtls(
					new ArrayList<SmManufacturingDtl>());
		} else {
			List<SmManufacturingDtl> activeManufacturingDtls = new ArrayList<SmManufacturingDtl>();
			for (SmManufacturingDtl manuFacDtl : getManagedBean().getSmAppl()
					.getSmManufacturingDtls()) {
				if (manuFacDtl.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					activeManufacturingDtls.add(manuFacDtl);
				}
			}
			getManagedBean().getSmAppl().setSmManufacturingDtls(
					activeManufacturingDtls);
		}
	}

	/**
	 * Initialized Suppoerting Document Information step
	 */
	private void initStep7SupportingDocumentsInfo() {
		// offlineSupportingDocsFormViewOnly/onlineSupportingDocsFormViewOnly
		if (getManagedBean().getSmAppl().getSmApplSupportDocs() == null) {
			getManagedBean().getSmAppl().setSmApplSupportDocs(
					new ArrayList<SmApplSupportDoc>());
		} else {
			List<SmApplSupportDoc> activeSuppDocs = new ArrayList<SmApplSupportDoc>();
			for (SmApplSupportDoc suppDoc : getManagedBean().getSmAppl()
					.getSmApplSupportDocs()) {
				if (suppDoc.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					activeSuppDocs.add(suppDoc);
				}
			}
			getManagedBean().getSmAppl().setSmApplSupportDocs(activeSuppDocs);
		}
	}

	/**
	 * Initialized PO or Approver Default Decision page
	 */
	private void initStep8_9PoApproverDefaultDecision() {
		// poDefault_DescisionPageViewOnly/approverDefault_DescisionPageViewOnly
		setReviewSectionRemarks();
		setSupplierCancellationRemarks(getManagedBean().getSmAppl()
				.getApplType(), getManagedBean().getSmAppl().getApplId());
		setPmStatusDesc(getManagedBean().getSmAppl().getPmStatus() == null ? ""
				: getManagedBean().getSmAppl().getPmStatus().getStatusId()
						.toString());
	}

	/**
	 * Set supplier cancellation remarks
	 * 
	 * @param String
	 *            docType
	 * @param String
	 *            docId
	 */
	private void setSupplierCancellationRemarks(String docType, Long docId) {
		SmRemaksResDTO resDTO = SmApplicationUtil.getRemarksByDoc(docType,
				docId);
		StringBuilder sb = new StringBuilder();
		if (resDTO != null && resDTO.getRemarksList() != null
				&& !resDTO.getRemarksList().isEmpty()) {

			for (SmRemark rem : resDTO.getRemarksList()) {
				sb.append(rem.getRemark()).append("\n");
			}
			getManagedBean().setSupplierRemarks(sb.toString());
		}
	}

	/**
	 * Set personnel information calculation summary
	 */
	private void setPersonnelInfoCalculationSummary() {
		getManagedBean().setPersonCalc(
				SmCommonUtil.calcPersonnelListCount(getManagedBean()
						.getSmAppl().getSmPersonnels(), getManagedBean()
						.getSmAppl().getSmStaffTotals(), getManagedBean()
						.getSmAppl().getSupplierType()));
	}

	/**
	 * Set status
	 * 
	 * @param String
	 *            statusId
	 */
	private void setPmStatusDesc(String statusId) {
		if (StringUtils.isBlank(statusId)) {
			getManagedBean().setCurrentStatus(StringUtils.EMPTY);
		} else {
			PmStatusDesc sd = SmCommonUtil.getStatusDescByStatusId(Long
					.parseLong(statusId));
			if (sd != null) {
				getManagedBean().setCurrentStatus(sd.getStatusName());
			}
		}
	}

	/**
	 * Assign selected associate company and close block UI
	 * 
	 * @param SmAssociateCompany
	 *            assocBean
	 */
	public void assignSelAssoc(SmAssociateCompany assocBean) {
		getManagedBean().setSelectedAssocCompany(assocBean);
		closeBlockUI();
	}

	/**
	 * Assign selected branch and close block UI
	 * 
	 * @param SmSupplierBranch
	 *            branchBean
	 */
	public void assignSelBranch(SmSupplierBranch branchBean) {
		getManagedBean().setSelectedBranch(branchBean);
		closeBlockUI();
	}

	/**
	 * Assign selected certificate
	 * 
	 * @param SmSupplierCert
	 *            certBean
	 */
	public void assignSelCert(SmSupplierCert certBean) {
		getManagedBean().setSelectedCert(certBean);
		getManagedBean().setSelectedCertType(
				this.getDropDownViewOnlyValueById(
						"CRT",
						Long.parseLong(getManagedBean().getSelectedCert()
								.getCertTypeId().toString())));
		closeBlockUI();
	}

	/**
	 * Assign selected bank and close block UI
	 * 
	 * @param SmSupplierBank
	 *            shareBankBean
	 */
	public void assignSelBank(SmSupplierBank shareBankBean) {
		getManagedBean().setSelBankBranchName("");

		StringBuilder sb = new StringBuilder();
		int i = 0;

		if (!SmCommonUtil.isCollectionEmpty(shareBankBean
				.getSmChequeSignatories())) {
			if (shareBankBean.getSmChequeSignatories().size() == 1) {
				sb.append(shareBankBean.getSmChequeSignatories().get(0)
						.getSmPersonnel().getName());
			} else {
				for (SmChequeSignatory smChequeSignatoryTmp : shareBankBean
						.getSmChequeSignatories()) {
					i++;
					sb.append(i + ") ");
					sb.append(smChequeSignatoryTmp.getSmPersonnel().getName());
					sb.append("<br />");
				}
			}
		}

		getManagedBean().setChequeSignPersonnelName(sb.toString());

		sb = new StringBuilder();
		i = 0;
		if (!SmCommonUtil.isCollectionEmpty(shareBankBean
				.getSmSupplierBranchBanks())) {
			if (shareBankBean.getSmSupplierBranchBanks().size() == 1) {
				sb.append(shareBankBean.getSmSupplierBranchBanks().get(0)
						.getSmSupplierBranch().getBranchName());
			} else {
				for (SmSupplierBranchBank brTmp : shareBankBean
						.getSmSupplierBranchBanks()) {
					i++;
					sb.append(i + ") ");
					sb.append(brTmp.getSmSupplierBranch().getBranchName());
					sb.append("<br />");
				}
			}
		}

		getManagedBean().setBankBranchName(sb.toString());

		// if (shareBankBean.getSmSupplierBranch() != null
		// && shareBankBean.getSmSupplierBranch().getSupplierBranchId() != null)
		// {
		// getManagedBean().setSelBankBranchName(
		// shareBankBean.getSmSupplierBranch().getBranchName());
		// }
		getManagedBean().setSelectedBank(shareBankBean);
		if (shareBankBean.getSmAddress() == null) {
			shareBankBean.setSmAddress(setDefaultAddr());
		}
		setSelectedBankAccountPurpose();
		closeBlockUI();
	}

	/**
	 * Assign selected equity company
	 * 
	 * @param SmEquityCompany
	 *            equiCompBean
	 */
	public void assignSelEquiCompany(SmEquityCompany equiCompBean) {
		getManagedBean().setSelectedEquityCompany(equiCompBean);
	}

	/**
	 * Assign selected factory and close block UI
	 * 
	 * @param SmFacility
	 *            facilityBean
	 */
	public void assignSelFactory(SmFacility facilityBean) {
		getManagedBean().setSelectedFactoryFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.factory", null)));
		closeBlockUI();
	}

	/**
	 * Assign selected workshop and close block UI
	 * 
	 * @param SmFacility
	 *            facilityBean
	 */
	public void assignSelWorkshop(SmFacility facilityBean) {
		getManagedBean().setSelectedWorkShopFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.workshop", null)));
		closeBlockUI();
	}

	/**
	 * Assign selected dockyard and close block UI
	 * 
	 * @param SmFacility
	 *            facilityBean
	 */
	public void assignSelDockyard(SmFacility facilityBean) {
		getManagedBean().setSelectedDockyardFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.dockyard", null)));
		closeBlockUI();
	}

	/**
	 * Assign selected store and close block UI
	 * 
	 * @param SmFacility
	 *            facilityBean
	 */
	public void assignSelStore(SmFacility facilityBean) {
		getManagedBean().setSelectedStoreFacility(facilityBean);
		getManagedBean().setFacilityType(
				StringUtils.upperCase(SmCommonUtil.getMessage(
						"lbl.sm.facility.type.store", null)));
		closeBlockUI();
	}

	/**
	 * Assign selected manufacturer and close block UI
	 * 
	 * @param SmManufacturingDtl
	 *            manBean
	 */
	public void assignSelManufacturer(SmManufacturingDtl manBean) {
		getManagedBean().setSelectedManufacturer(manBean);
		closeBlockUI();
	}

	/**
	 * Assign selected machinery and close block UI
	 * 
	 * @param SmMachineDtl
	 *            macBean
	 */
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
		closeBlockUI();
	}

	/**
	 * Assign selected company experience
	 * 
	 * @param SmCompanyExperience
	 *            consultantBean
	 */
	public void assignSelCompanyExperience(SmCompanyExperience consultantBean) {
		getManagedBean().setSelectedCompanyExperience(consultantBean);

		StringBuilder catL1 = new StringBuilder();
		StringBuilder catL2 = new StringBuilder();
		StringBuilder catL3 = new StringBuilder();
		for (SmCompanyExperienceCat cat : consultantBean
				.getSmCompanyExperienceCats()) {
			if (cat.getPmCategoryL1() != null) {
				catL1.append(cat.getPmCategoryL1().getCategoryName()).append(
						"<br/>");
			} else if (cat.getPmCategoryL2() != null) {
				catL2.append(cat.getPmCategoryL1().getCategoryName()).append(
						"<br/>");
			} else if (cat.getPmCategoryL3() != null) {
				catL3.append(cat.getPmCategoryL1().getCategoryName()).append(
						"<br/>");
			}
		}
		getManagedBean().setSelectedCompanyExperienceCatL1(catL1.toString());
		getManagedBean().setSelectedCompanyExperienceCatL2(catL2.toString());
		getManagedBean().setSelectedCompanyExperienceCatL3(catL3.toString());

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
		closeBlockUI();
	}

	/**
	 * Assign selected association body
	 * 
	 * @param SmAssociationBody
	 *            assocBean
	 */
	public void assignSelAssocBody(SmAssociationBody assocBean) {
		getManagedBean().setSelectedAssocBody(assocBean);
	}

	/**
	 * Redirect back to personnel section
	 */
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

	/**
	 * Set selected bank account purpose
	 */
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

	/**
	 * Set category type confirmation
	 */
	private void setCategoryTypeConfirm() {
		Locale locale = new FacesScopes().getUserLocale();
		String[] params = { "" };
		if (StringUtils.isNotBlank(getManagedBean().getSmAppl()
				.getApplCategoryType())
				&& getManagedBean().getSmAppl().getApplCategoryType()
						.equals("N")) {
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

	/**
	 * Set application facility type
	 */
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
				if (smFac.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
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
	}

	/**
	 * Set review section remarks
	 */
	private void setReviewSectionRemarks() {
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

	/**
	 * Assign selected share holder
	 * 
	 * @param SmPersonnel
	 *            shareHolderBean
	 */
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
						RoleCode.FL_USER.value())
						|| shareHolderBean.getEpRole().equalsIgnoreCase(
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

	/**
	 * Assign selected share holder addresses
	 * 
	 * @param SmPersonnel
	 *            shareHolderBean
	 */
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

	/**
	 * Set personnel company role
	 */
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

	/**
	 * Assign selected share holder job function and close block UI
	 * 
	 * @param SmStaffExperience
	 *            exp
	 */
	public void assignSelShrHldrJobFunc(SmStaffExperience exp) {
		getManagedBean().setSelectedJobFunction(exp);
		closeBlockUI();
	}

	/**
	 * Assign selected share holder related company and close block UI
	 * 
	 * @param SmRelatedCompany
	 *            com
	 */
	public void assignSelShrHldrRelatedCompany(SmRelatedCompany com) {
		getManagedBean().setSelectedRelatedCompany(com);
		closeBlockUI();
	}

	/**
	 * Assign selected share holder professional body and close block UI
	 * 
	 * @param SmAssociationBody
	 *            asb
	 */
	public void assignSelShrHldrProfBody(SmAssociationBody asb) {
		getManagedBean().setSelectedStfPrfBdy(asb);
		closeBlockUI();
	}

	/**
	 * Assign selected share holder academics and close block UI
	 * 
	 * @param SmStaffAcademic
	 *            aca
	 */
	public void assignSelShrHldrAcademics(SmStaffAcademic aca) {
		if (aca.getPmEduField() == null) {
			aca.setPmEduField(new PmEduField());
		}
		if (aca.getQualificationId() == null) {
			aca.setQualificationId(new PmParameter());
		}
		getManagedBean().setSelectedStaffAcademic(aca);
		closeBlockUI();
	}

	/**
	 * Get authorized personnel account status
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @param SmSupplier
	 *            smSupplier
	 * @return String
	 */
	public String getPersonnelActivationStatus(SmPersonnel smPersonnel,
			SmSupplier smSupplier) {
		String accStatus = "";
		if (smSupplier != null
				&& smSupplier.getSupplierId() != null
				&& smPersonnel.getEpRole() != null
				&& StringUtils.isNotBlank(smPersonnel.getEpRole())
				&& smPersonnel.getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {

			if (SmApplicationUtil.isLatestApprovedPersonnelActivated(
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

	/**
	 * Get description of the parameter by parameter id
	 * 
	 * @param Long
	 *            parameterId
	 * @return String
	 */
	public String getParamDescByParameterId(Long parameterId) {
		PmParameter pmParameter = null;
		if (parameterId != null) {
			pmParameter = SmApplicationUtil
					.retrievePmParameterById(parameterId);
			return pmParameter == null ? "" : SmCommonUtil
					.getParamDescriptionByLanguageAndPmParameter(pmParameter);
		} else {
			return "";
		}
	}

	/**
	 * Set application share holders
	 * 
	 * @param List
	 *            <SmPersonnel> perList
	 */
	private void setApplicationShareHolders(List<SmPersonnel> perList) {
		getManagedBean().setSmapplShareHolders(new ArrayList<SmPersonnel>());

		for (SmPersonnel per : perList) {

			if (per.isEquityOwner()) {
				if (per.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					getManagedBean().getSmapplShareHolders().add(per);
				}
			}
		}

	}

	/**
	 * Set list of active category
	 */
	private void setActiveCategoryList() {
		getManagedBean().getSmAppl().setSmSupplierCategories(
				getSmSupplierCategoryByRevNo());
		getManagedBean().setSuppProfileOrHistoryCategoryList(
				new ArrayList<SmSupplierCategory>());
		List<SmSupplierCategory> catList = new ArrayList<>();
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.APPROVED_RECORD_STATUS) == 0
					|| cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
				catList.add(cat);
			}
			if (getManagedBean().isViewApplicationHistory()) {
				// for history load all category except status deleted
				getManagedBean().getSuppProfileOrHistoryCategoryList().add(cat);
			} else {
				// for supp profile load all active category only
				if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					getManagedBean().getSuppProfileOrHistoryCategoryList().add(
							cat);
				}
			}
		}
		getManagedBean().setActiveCategoryList(catList);
	}

	/**
	 * Initialized and construct bumi recommendation message
	 */
	public void initBumiRecommendNote() {
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

	/**
	 * Create if SER file not exist
	 * 
	 * @param SmAppl
	 *            smApplFromSuppProfile
	 */
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

			getManagedBean().setSerializedFileExist(
					SmCommonUtil.isSerFileExist(applNo, serType));

			if (!getManagedBean().isSerializedFileExist()) {
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

	/**
	 * Set revision application view type
	 */
	private void setRevisionApplType() {
		String serType = getManagedBean()
				.getApplicationNoParam()
				.substring(
						getManagedBean().getApplicationNoParam().length() - 3,
						getManagedBean().getApplicationNoParam().length())
				.equals("APP") ? "APP" : "SUP";
		if (serType.equals("APP")) {
			if (getManagedBean().isViewSupplierProfile()) {
				getManagedBean().setPreviousApproved(false);
				getManagedBean().setOriginal(false);
			} else {
				getManagedBean().setPreviousApproved(true);
				getManagedBean().setOriginal(false);
			}
		} else {
			getManagedBean().setPreviousApproved(false);
			getManagedBean().setOriginal(true);
		}
	}

	/**
	 * Get max tab by application type
	 * 
	 * @param String
	 *            applType
	 * @param String
	 *            applCategoryType
	 * @return int
	 */
	private int getMaxTabByApplTypeAndCatType(String applType,
			String applCategoryType) {
		int maxTab = 0;
		switch (applType) {
		case ParameterConst.NEW_SM_APPLN_FOR_MOF:
			if (applCategoryType != null
					&& applCategoryType.equalsIgnoreCase("S")) {
				if (getManagedBean().isPreviousApproved()) {
					maxTab = 10;
				} else {
					maxTab = 8;
				}
			} else {
				if (getManagedBean().isPreviousApproved()) {
					maxTab = 8;
				} else {
					maxTab = 6;
				}
			}
			break;
		case ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES:
			if (applCategoryType != null
					&& applCategoryType.equalsIgnoreCase("S")) {
				if (getManagedBean().isPreviousApproved()) {
					maxTab = 9;
				} else {
					maxTab = 7;
				}
			} else {
				if (getManagedBean().isPreviousApproved()) {
					maxTab = 7;
				} else {
					maxTab = 5;
				}
			}
			break;
		case ParameterConst.NEW_SM_APPLN_FOR_BUMI:
			if (getManagedBean().isPreviousApproved()) {
				maxTab = 6;
			} else {
				maxTab = 4;
			}
			break;
		case ParameterConst.NEW_SM_APPLN_FOR_UPDATE:
			if (getManagedBean().isPreviousApproved()) {
				maxTab = 7;
			} else {
				maxTab = 5;
			}
			break;
		case ParameterConst.NEW_SM_APPLN_FOR_RENEW:
			if (applCategoryType != null
					&& applCategoryType.equalsIgnoreCase("S")) {
				if (getManagedBean().isPreviousApproved()) {
					maxTab = 10;
				} else {
					maxTab = 8;
				}
			} else {
				if (getManagedBean().isPreviousApproved()) {
					maxTab = 8;
				} else {
					maxTab = 6;
				}
			}
			break;
		default:
			break;
		}
		return maxTab;
	}

	/**
	 * Close block UI
	 */
	private void closeBlockUI() {
		SmCommonUtil.closeBlockUI();
	}

	/**
	 * Set maximum no of tab by application type
	 */
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

	/**
	 * Search By on change and reset value
	 * 
	 * @param e
	 */
	public void onChangeSearchBy(AjaxBehaviorEvent e) {
		if (getManagedBean().getSearchBy() == SEARCH_BY_COMPANY_NAME) {
			getManagedBean().getSearchSupplier().setEpNo("");
			getManagedBean().getSearchSupplier().setRegNo("");
		} else if (getManagedBean().getSearchBy() == SEARCH_BY_BUSINESS_REG_NO) {
			getManagedBean().getSearchSupplier().setEpNo("");
			getManagedBean().getSearchSupplier().setCompanyName("");
		} else if (getManagedBean().getSearchBy() == SEARCH_BY_EP_NO) {
			getManagedBean().getSearchSupplier().setCompanyName("");
			getManagedBean().getSearchSupplier().setRegNo("");
		}
	}

	/**
	 * Reset search value
	 */
	public void resetSearch() {
		getManagedBean().getSearchSupplier().setCompanyName(StringUtils.EMPTY);
		getManagedBean().getSearchSupplier().setRegNo(StringUtils.EMPTY);
		getManagedBean().setSearchMofNo(StringUtils.EMPTY);
		getManagedBean().setSearchSuppType(StringUtils.EMPTY);
		getManagedBean().setSearchPmRegStatusId(null);
		getManagedBean().setSearchPmZoneId(null);
		getManagedBean().setSearchApplType(StringUtils.EMPTY);
		getManagedBean().setSearchApplNo(StringUtils.EMPTY);
		getManagedBean().setSearchApplStatusId(null);
	}

	/**
	 * Set login role
	 */
	private void setUserLoginRole() {
		FacesScopes sc = new FacesScopes();
		if (sc.hasRoleUser(SupplierManagementConst.PO_HQ)
				|| sc.hasRoleUser(SupplierManagementConst.PO_HQ_TEMP)) {
			getManagedBean().setPoUser(true);
			getManagedBean().setRoleCode(RoleCode.UPS_PO.value());
		} else if (sc.hasRoleUser(SupplierManagementConst.APPROVER_HQ)
				|| sc.hasRoleUser(SupplierManagementConst.APPROVER_HQ_TEMP)
				|| sc.hasRoleUser(SupplierManagementConst.APPROVER)) {
			getManagedBean().setApproverUser(true);
			getManagedBean().setRoleCode(RoleCode.UPS_APPROVER.value());
		} else if (sc.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_USER)
				|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_USER)
				|| sc.hasRoleUser(RoleCode.FL_USER.value())
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_USER)) {
			getManagedBean().setSupplierUser(true);
			if (sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)
					|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_USER)) {
				getManagedBean().setMofSupplier(true);
			}
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setUserId((MDC.get("USER_ID") == null) ? 1 : Long
					.valueOf(String.valueOf(MDC.get("USER_ID"))));
			SmPersonnel loginPersonnel = SmServiceLookup.getPersonnelSO()
					.retrievePersonnelByUserId(reqDTO).getSmPersonnel();
			getManagedBean().setRoleCode(
					loginPersonnel == null ? "" : loginPersonnel.getEpRole());
		}
	}

	/**
	 * Initialized supplier by login role
	 */
	public void initLogin() {
		SmCommonUtil.setMDCUserId();
		setUserLoginRole();
		if (getManagedBean().isSupplierUser()) {
			getManagedBean().setSearchSupplier(
					SmApplicationUtil.getCurrentSupplier());
		} else {
			getManagedBean().setSearchSupplier(new SmSupplier());
			getManagedBean().getSearchSupplier().setCompanyName("");
			getManagedBean().getSearchSupplier().setEpNo("");
			getManagedBean().getSearchSupplier().setRegNo("");
		}
	}

	/**
	 * Set url for Supplier Profile or Supplier Application History.
	 */
	private void setSupplierProfileOrHistoryUrl(String baseUrl,
			String baseUrlLabel, String supplierId) {
		String url = SmCommonUtil.getEpBaseUrl() + baseUrl;
		getManagedBean().setPortletUrl(url);
		getManagedBean().setSupplierIdParam(supplierId);
		getManagedBean().setPortletUrlLabel(baseUrlLabel);
	}

	/**
	 * Redirect back to Search Supplier Profile page
	 * 
	 * @return String
	 */
	public String backToSearch() {
		if (getManagedBean().isViewApplicationHistory()) {
			return "searchSupplierApplicationHistoryPage.xhtml";
		} else {
			return "searchSupplierProfilePage.xhtml";
		}
	}

	/**
	 * View Supplier Profile from Application List
	 */
	public void viewApplication(SmAppl selectedSmAppl) {
		String baseUrl = "";
		String baseUrlLabel = "";
		String basePage = "";

		if (getManagedBean().isViewApplicationHistory()) {
			basePage = "/xhtml/sm/revisionSmApplView/viewApplicationHistory/smViewApplHistorySideMenu_BasePage.xhtml";
			baseUrl = "/supplier-profile";
			baseUrlLabel = SmCommonUtil.getMessage("lbl.sm.supprofile", null);
		} else {
			basePage = "/xhtml/sm/revisionSmApplView/viewSupplierProfile/smViewProfileSideMenu_BasePage.xhtml";
			baseUrl = "/application-history";
			baseUrlLabel = SmCommonUtil.getMessage(
					"lbl.sm.supplier.application.history", null);
		}

		SmAppl smAppl = getApplByLoginRole(selectedSmAppl);
		setApplication(smAppl);

		setSupplierProfileOrHistoryUrl(baseUrl, baseUrlLabel, smAppl
				.getSmSupplier().getSupplierId().toString());
		SmCommonUtil.redirectToPage(basePage, "", null);
	}

	/**
	 * Get Supplier Id from url parameter
	 * 
	 * @return String supplierId
	 */
	public String getSupplierIdParam() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String supplierId = originalServletRequest.getParameter("supplierId");
		return supplierId;
	}

	/**
	 * Get application rejection reasons
	 * 
	 * @return String
	 */
	public String getApplicationRejectionReason() {
		String langCode = new FacesScopes().getUserLocale().getLanguage();
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (SmApplRejectReason rej : getManagedBean().getSmAppl()
				.getSmApplRejectReasons()) {
			if (rej.getPmReason() != null
					&& rej.getPmReason().getPmReasonDescs() != null) {
				for (PmReasonDesc desc : rej.getPmReason().getPmReasonDescs()) {
					if (desc.getPmLanguage() != null
							&& desc.getPmLanguage().getLanguageCode()
									.equalsIgnoreCase(langCode)) {
						sb.append(i + ") " + desc.getReasonDesc()).append(
								"<br />");
						i++;
					}
				}

			}
		}
		return sb.toString();
	}

	/**
	 * Print Supplier Profile
	 * 
	 * @throws IOException
	 */
	public void printSupplierProfile() throws IOException {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("PAPPLNO", getManagedBean().getSmAppl().getApplNo());
		generateReport(ReportConst.SM_SUPPLIER_PROFILE_REPORT, paramMap);
	}

	/**
	 * Redirect to Supplier Profile or Supplier History Page
	 */
	public void redirectToSupplierProfileOrHistoryPage() {
		SmCommonUtil.redirectToExternalUrl(getManagedBean().getPortletUrl()
				+ "?supplierId=" + getManagedBean().getSupplierIdParam());
	}

	/**
	 * Retrieve all registration status list
	 * 
	 * @return List<PmStatusDesc>
	 */
	public List<PmStatusDesc> retrieveRegistrationStatusList() {
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		StatusResDTO resDTO = null;
		resDTO = SmServiceLookup.getStatusSO().retrieveRegStatusList(reqDTO);
		if (resDTO != null) {
			return resDTO.getStatusDescList();
		} else {
			return null;
		}
	}

	/**
	 * Retrieve application by login role and view type either application
	 * history or supplier profile
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return SmAppl
	 */
	private SmAppl getApplByLoginRole(SmAppl smAppl) {
		if (getManagedBean().isViewApplicationHistory()) {
			// role supplier user need to load supplier ser file
			if (getManagedBean().isSupplierUser()) {
				if (StringUtils.isNotBlank(smAppl.getSupplierSerFile())) {
					SmAppl smApplSer = null;
					try {
						if (SmCommonUtil.isSerFileExist(smAppl.getApplNo(),
								"SER")) {
							SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
							reqDTO.setDeSerFileName(getDeSerFileNameAndPath(smAppl
									.getApplNo() + "_SER"));
							smApplSer = SmServiceLookup.getSmApplRevisionSO()
									.readApplicationHist(reqDTO).getSmAppl();
							if (smApplSer == null) {
								smAppl = retrieveApplByApplIdWithChild(smAppl
										.getApplId());
								log.info(">>>>>>>>>>>>>>>>fail to load from ser file for applNo:"
										+ smAppl.getApplNo()
										+ "<<<<<<<<<<<<<<<<");
							} else {
								log.info(">>>>>>>>>>>>>>>>loaded from ser file for applNo:"
										+ smAppl.getApplNo()
										+ "<<<<<<<<<<<<<<<<");
								smAppl = smApplSer;
							}
						} else {
							smAppl = retrieveApplByApplIdWithChild(smAppl
									.getApplId());
							log.info(">>>>>>>>>>>>ser file not exist for applNo:"
									+ smAppl.getApplNo() + "<<<<<<<<<<<<<<");
						}
					} catch (Exception ex) {
						smAppl = retrieveApplByApplIdWithChild(smAppl
								.getApplId());
						log.info(">>>>>>>>>>>>>>>>exception when load from ser file for applNo:"
								+ smAppl.getApplNo() + "<<<<<<<<<<<<<<<<");
						log.info(">>>>>>>>>>>>>>>>exception load ser msg:"
								+ ex.getMessage() + "<<<<<<<<<<<<<<<<");
					} finally {
						smApplSer = null;
					}
				}
				// migrated data dont have serialized file, so directly load
				// from DB
				else {
					smAppl = retrieveApplByApplIdWithChild(smAppl.getApplId());
					log.info(">>>>>>>>>>>dont have ser file so directly load from DB for applNo:"
							+ smAppl.getApplNo());
				}
			}
			// role po/approver can load directly from DB
			else {
				smAppl = retrieveApplByApplIdWithChild(smAppl.getApplId());
				log.info(">>>>>>>>>>>po/approver directly load from DB for applNo:"
						+ smAppl.getApplNo());
			}
		} else {
			// if not for view application history means for view supplier
			// profile just directly load from DB
			smAppl = retrieveApplByApplIdWithChild(smAppl.getApplId());
			log.info(">>>>>>>>>>>view latest profile directly load from DB for applNo:"
					+ smAppl.getApplNo());
		}
		return smAppl;
	}

	private SmAppl retrieveApplByApplIdWithChild(Long applId) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplId(applId);
		return SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplicationDetailsById(reqDTO).getSmAppl();
	}

	private SmCompanyBasic getCompanyBasicByRevNo() {
		SmCompanyBasic activeCompanyBasic = new SmCompanyBasic();

		// if from serialized file, directly take the company basic
		if (getManagedBean().getSmAppl() != null
				&& getManagedBean().getSmAppl().getSmCompanyBasic() != null
				&& getManagedBean().isSerializedFileExist()) {
			activeCompanyBasic = getManagedBean().getSmAppl()
					.getSmCompanyBasic();
		} else {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			List<SmCompanyBasic> compBasicList = SmServiceLookup
					.getMofRegistrationSO()
					.retrieveSmCompanyBasicByApplId(reqDTO)
					.getCompanyBasicList();

			if (compBasicList != null && !compBasicList.isEmpty()) {
				if (getManagedBean().isViewApplicationHistory()) {
					// role supplier user load from rev 1
					if (getManagedBean().isSupplierUser()) {
						activeCompanyBasic = SmCommonUtil
								.getCompanyBasicByRevNo(compBasicList, 1);
					}
					// role approver load from rev 2
					else {
						activeCompanyBasic = SmCommonUtil
								.getCompanyBasicByRevNo(compBasicList, 2);
					}
				} else {
					// for supplier profile load from rev 2
					activeCompanyBasic = SmCommonUtil.getCompanyBasicByRevNo(
							compBasicList, 2);
				}

			}

			if (activeCompanyBasic.getTotalMember() != null
					&& activeCompanyBasic.getTotalMember().compareTo(
							BigDecimal.ZERO) != 0) {
				activeCompanyBasic.setTotalMember(activeCompanyBasic
						.getTotalMember().setScale(0, BigDecimal.ROUND_DOWN));
			}
		}

		return activeCompanyBasic;
	}

	private List<SmStaffTotal> getStaffTotalByRevNo() {
		List<SmStaffTotal> mainList = getManagedBean().getSmAppl()
				.getSmStaffTotals();
		List<SmStaffTotal> objList = new ArrayList<SmStaffTotal>();
		SmStaffTotal obj = null;

		if (mainList != null && !mainList.isEmpty()) {
			if (getManagedBean().isViewApplicationHistory()) {
				// appl history load by supplier role for revNo=1
				if (getManagedBean().isSupplierUser()) {
					for (SmStaffTotal param : mainList) {
						obj = SmCommonUtil.getStaffTotalByRevNoAndCategory(
								mainList, param, 1);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
				// appl history load by po/approver role for revNo=2
				else {
					for (SmStaffTotal param : mainList) {
						obj = SmCommonUtil.getStaffTotalByRevNoAndCategory(
								mainList, param, 2);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
			} else {
				// supplier profile direct load to revNo=2
				for (SmStaffTotal param : mainList) {
					obj = SmCommonUtil.getStaffTotalByRevNoAndCategory(
							mainList, param, 2);
					if (obj != null && !objList.contains(obj)) {
						objList.add(obj);
					}
				}
			}
		}
		return objList;
	}

	private List<SmSupplierBank> getSupplierBankByRevNo() {
		List<SmSupplierBank> mainList = getManagedBean().getSmAppl()
				.getSmSupplierBanks();
		List<SmSupplierBank> objList = new ArrayList<SmSupplierBank>();
		SmSupplierBank obj = null;

		if (mainList != null && !mainList.isEmpty()) {
			if (getManagedBean().isViewApplicationHistory()) {
				// appl history load by supplier role for revNo=1
				if (getManagedBean().isSupplierUser()) {
					for (SmSupplierBank param : mainList) {
						obj = SmCommonUtil.getSupplierBankByRevNoAndAccNo(
								mainList, param, 1);
						if (obj != null && !objList.contains(obj)) {
							obj.setSmChequeSignatories(getCheckSignatories(obj));
							objList.add(obj);
						}
					}
				}
				// appl history load by po/approver role for revNo=2
				else {
					for (SmSupplierBank param : mainList) {
						obj = SmCommonUtil.getSupplierBankByRevNoAndAccNo(
								mainList, param, 2);
						if (obj != null && !objList.contains(obj)) {
							obj.setSmChequeSignatories(getCheckSignatories(obj));
							objList.add(obj);
						}
					}
				}
			} else {
				// supplier profile direct load to revNo=2
				for (SmSupplierBank param : mainList) {
					obj = SmCommonUtil.getSupplierBankByRevNoAndAccNo(mainList,
							param, 2);
					if (obj != null && !objList.contains(obj)) {
						obj.setSmChequeSignatories(getCheckSignatories(obj));
						objList.add(obj);
					}
				}
			}
		}

		return objList;
	}

	private List<SmPersonnel> getPersonnelByRevNo() {
		List<SmPersonnel> mainList = getManagedBean().getSmAppl()
				.getSmPersonnels();
		List<SmPersonnel> objList = new ArrayList<SmPersonnel>();
		SmPersonnel obj = null;

		if (mainList != null && !mainList.isEmpty()) {
			if (getManagedBean().isViewApplicationHistory()) {
				// appl history load by supplier role for revNo=1
				if (getManagedBean().isSupplierUser()) {
					for (SmPersonnel param : mainList) {
						obj = SmCommonUtil
								.getSmPersonnelByRevNoAndIdentificationNo(
										mainList, param, 1);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
				// appl history load by po/approver role for revNo=2
				else {
					for (SmPersonnel param : mainList) {
						obj = SmCommonUtil
								.getSmPersonnelByRevNoAndIdentificationNo(
										mainList, param, 2);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
			} else {
				// supplier profile direct load to revNo=2
				for (SmPersonnel param : mainList) {
					obj = SmCommonUtil
							.getSmPersonnelByRevNoAndIdentificationNo(mainList,
									param, 2);
					if (obj != null && !objList.contains(obj)) {
						objList.add(obj);
					}
				}
			}
		}
		return objList;
	}

	private List<SmAssociateCompany> getAssocCompanyByRevNo() {
		List<SmAssociateCompany> mainList = getManagedBean().getSmAppl()
				.getSmAssociateCompanies();
		List<SmAssociateCompany> objList = new ArrayList<SmAssociateCompany>();
		SmAssociateCompany obj = null;

		if (mainList != null && !mainList.isEmpty()) {
			if (getManagedBean().isViewApplicationHistory()) {
				// appl history load by supplier role for revNo=1
				if (getManagedBean().isSupplierUser()) {
					for (SmAssociateCompany param : mainList) {
						obj = SmCommonUtil
								.getSmAssociateCompanyByRevNoAndRegNo(mainList,
										param, 1);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
				// appl history load by po/approver role for revNo=2
				else {
					for (SmAssociateCompany param : mainList) {
						obj = SmCommonUtil
								.getSmAssociateCompanyByRevNoAndRegNo(mainList,
										param, 2);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
			} else {
				// supplier profile direct load to revNo=2
				for (SmAssociateCompany param : mainList) {
					obj = SmCommonUtil.getSmAssociateCompanyByRevNoAndRegNo(
							mainList, param, 2);
					if (obj != null && !objList.contains(obj)) {
						objList.add(obj);
					}
				}
			}
		}
		return objList;
	}

	private List<SmSupplierCategory> getSmSupplierCategoryByRevNo() {
		List<SmSupplierCategory> mainList = getManagedBean().getSmAppl()
				.getSmSupplierCategories();
		List<SmSupplierCategory> objList = new ArrayList<SmSupplierCategory>();
		SmSupplierCategory obj = null;

		if (mainList != null && !mainList.isEmpty()) {
			if (getManagedBean().isViewApplicationHistory()) {
				// appl history load by supplier role for revNo=1
				if (getManagedBean().isSupplierUser()) {
					for (SmSupplierCategory param : mainList) {
						obj = SmCommonUtil
								.getSmSupplierCategoryByRevNoAndCodeLvl(
										mainList, param, 1);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
				// appl history load by po/approver role for revNo=2
				else {
					for (SmSupplierCategory param : mainList) {
						obj = SmCommonUtil
								.getSmSupplierCategoryByRevNoAndCodeLvl(
										mainList, param, 2);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
			} else {
				// supplier profile direct load to revNo=2
				for (SmSupplierCategory param : mainList) {
					obj = SmCommonUtil.getSmSupplierCategoryByRevNoAndCodeLvl(
							mainList, param, 2);
					if (obj != null && !objList.contains(obj)) {
						objList.add(obj);
					}
				}
			}
		}
		return objList;
	}

	private List<SmSupplierBranch> getSmSupplierBranchByRevNo() {
		List<SmSupplierBranch> mainList = getManagedBean().getSmAppl()
				.getSmSupplierBranches();
		List<SmSupplierBranch> objList = new ArrayList<SmSupplierBranch>();
		SmSupplierBranch obj = null;

		if (mainList != null && !mainList.isEmpty()) {
			if (getManagedBean().isViewApplicationHistory()) {

				// appl history load by supplier role for revNo=1
				if (getManagedBean().isSupplierUser()) {
					for (SmSupplierBranch param : mainList) {
						obj = SmCommonUtil.getSmSupplierBranchByRevNoAndName(
								mainList, param, 1);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}

				// appl history load by po/approver role for revNo=2
				else {
					for (SmSupplierBranch param : mainList) {
						obj = SmCommonUtil.getSmSupplierBranchByRevNoAndName(
								mainList, param, 2);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
			} else {
				// supplier profile direct load to revNo=2
				for (SmSupplierBranch param : mainList) {
					obj = SmCommonUtil.getSmSupplierBranchByRevNoAndName(
							mainList, param, 2);
					if (obj != null && !objList.contains(obj)) {
						objList.add(obj);
					}
				}
			}
		}
		return objList;
	}

	private List<SmChequeSignatory> getCheckSignatories(SmSupplierBank bank) {
		List<SmChequeSignatory> mainList = bank.getSmChequeSignatories();
		List<SmChequeSignatory> objList = new ArrayList<SmChequeSignatory>();
		SmChequeSignatory obj = null;

		if (mainList != null && !mainList.isEmpty()) {
			if (getManagedBean().isViewApplicationHistory()) {
				// appl history load by supplier role for revNo=1
				if (getManagedBean().isSupplierUser()) {
					for (SmChequeSignatory param : mainList) {
						obj = SmCommonUtil
								.getSmChequeSignatoryByRevNoAndIdentificationNo(
										mainList, param, 1);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
				// appl history load by po/approver role for revNo=2
				else {
					for (SmChequeSignatory param : mainList) {
						obj = SmCommonUtil
								.getSmChequeSignatoryByRevNoAndIdentificationNo(
										mainList, param, 2);
						if (obj != null && !objList.contains(obj)) {
							objList.add(obj);
						}
					}
				}
			} else {
				// supplier profile direct load to revNo=2
				for (SmChequeSignatory param : mainList) {
					obj = SmCommonUtil
							.getSmChequeSignatoryByRevNoAndIdentificationNo(
									mainList, param, 2);
					if (obj != null && !objList.contains(obj)) {
						objList.add(obj);
					}
				}
			}
		}
		return objList;
	}

	private SmGstSupplier getSupplierGst(Long branchCode) {
		SmGstSupplier gst = null;
		SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
		gstReqDTO.setBranchCode(branchCode);
		if (getManagedBean().isViewSupplierProfile()) {
			if (getManagedBean().isOriginal()
					|| getManagedBean().isPreviousApproved()) {
				gstReqDTO.setApplNo(getManagedBean().getSmAppl().getApplNo());
				gst = SmServiceLookup.getSupplierGstSO()
						.retrieveGstInfoByApplNoAndBranchCode(gstReqDTO)
						.getSmGstSupplier();
			} else {
				gstReqDTO.setSupplierId(getManagedBean().getSmAppl()
						.getSmSupplier().getSupplierId());
				gst = SmServiceLookup.getSupplierGstSO()
						.retrieveGstInfoBySupplierIdAndBranchCode(gstReqDTO)
						.getSmGstSupplier();
			}
		} else {
			gstReqDTO.setApplNo(getManagedBean().getSmAppl().getApplNo());
			gst = SmServiceLookup.getSupplierGstSO()
					.retrieveGstInfoByApplNoAndBranchCode(gstReqDTO)
					.getSmGstSupplier();
		}
		if (gst == null) {
			gst = new SmGstSupplier();
		}
		SmApplicationUtil.initGstSuppDocs(gst);
		return gst;
	}
}
