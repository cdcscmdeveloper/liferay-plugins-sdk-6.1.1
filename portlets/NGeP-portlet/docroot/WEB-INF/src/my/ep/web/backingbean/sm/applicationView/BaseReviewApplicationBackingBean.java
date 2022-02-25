package my.ep.web.backingbean.sm.applicationView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.biz.service.pm.authorization.role.IRoleSO;
import my.ep.biz.service.sm.businessnature.ISupplierBusinessNatureSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
// #29617
import my.ep.biz.service.sm.sme.ISupplierSmeSO;
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
import my.ep.biz.service.sm.vendorcode.IVendorCodeSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.CmConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
import my.ep.dto.pm.authorization.role.RoleResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.zone.ZoneReqDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorReqDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorResDTO;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.dto.sm.businessnature.SupplierBusinessNatureReqDTO;
import my.ep.dto.sm.businessnature.SupplierBusinessNatureResDTO;
import my.ep.dto.sm.common.ssm.SsmReqDTO;
import my.ep.dto.sm.common.ssm.SsmResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
// #29617
import my.ep.dto.sm.sme.SupplierSmeReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmDigiCert;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplAuditors;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplRevise;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmCompanyExperienceCat;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmDisciplinaryActionRemarks;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMachineCategory;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmPersonnelSecretaries;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSapVendorCode;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
// #29617
import my.ep.persistence.entity.sm.SmSmeSupplier;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBranchBank;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCategoryFacility;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.BaseReviewApplicationManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.BlacklistOwnerDirectorVO;
import my.ep.web.vo.DisciplinaryActionCategoryVO;
import my.ep.web.vo.DisciplinaryActionVO;
import my.ep.web.vo.SmOverlapCategoryVO;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.liferay.compat.portal.util.PortalUtil;

public abstract class BaseReviewApplicationBackingBean<T extends BaseReviewApplicationManagedBean>
		extends BaseBackingBean<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4811194310782224027L;
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
		log.info("TROUBLESHOOT REVISION : initMain");
		if (reloadApplication()) {
			initApplication(null);
			setMaxTab();
		}else{
			log.info("TROUBLESHOOT REVISION : Else");
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

		try {
			// set revision application type
			setRevisionApplType();

			// checking if SER file not exist then recreate
			createSerFileIfNotExist(smAppl);

			if (getManagedBean().isReviewApplicationExist()) {
				SmApplRevisionResDTO resDTO = SmCommonUtil
						.getSerializedApplicationByFilePathName(getDeSerFileNameAndPath(getManagedBean()
								.getApplicationNoParam()));
				getManagedBean().setSmSupplier(resDTO.getSmSupplier());
				if (resDTO != null) {
					setApplication(resDTO.getSmAppl());
				}
			}

		} catch (NullPointerException ne) {
			log.error("***************Error init revision appl:"
					+ ne.getMessage());
			getManagedBean().setReviewApplicationExist(false);
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
		log.info("TROUBLESHOOT REVISION : applicationNo="+applicationNo);
		if (applicationNo != null) {
			if (getManagedBean().getApplicationNoParam() == null) {
				log.info("TROUBLESHOOT REVISION : getManagedBean().getApplicationNoParam()="+getManagedBean().getApplicationNoParam());
				reload = true;
				getManagedBean().setApplicationNoParam(applicationNo);
			} else {
				log.info(applicationNo+"====TROUBLESHOOT REVISION ELSE : getManagedBean().getApplicationNoParam()="+getManagedBean().getApplicationNoParam());
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
	 * Dummy submit
	 */
	public void submit() {

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
		if (getManagedBean().getSmAppl() != null) {
			getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
		}
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
	public void initStep0BaseInfo(SmAppl smAppl) {
		getManagedBean().setSmAppl(smAppl);
		
		// 28155 - start
		smAppl.getSmSupplier().setSmDisciplinaryActions(
				SmApplicationUtil.retrieveSmDisciplinaryActionBySupplier(smAppl.getSmSupplier()));
		getManagedBean().setSmSupplier(smAppl.getSmSupplier());
		// 28155 - end
		
		getManagedBean().setBusinessTypeVal(
				SmApplicationUtil.assignSupplierBusinessType(getManagedBean()
						.getSmSupplier()));
		// 25792 - start
		getManagedBean()
				.setSupplierBusinessTypeVal(
						SmCommonUtil
								.getDropDownViewOnlyValueByCode(
										getManagedBean().getSmAppl()
												.getSupplierType().equals("K")
												|| getManagedBean().getSmAppl()
														.getSupplierType()
														.equals("B") ? ParameterTypeConst.SUPPLIER_BUSINESS_TYPE
												: ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT,
										getManagedBean().getSmSupplier()
												.getBusinessType()));

		if (smAppl.getSmCompanyBasic() != null) {
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
		}
		// 25792 - end

		if (!EntityHelper.isCollectionEmpty(smAppl.getSmSupplier()
				.getSmMofAccounts())) {

			for (SmMofAccount acc : smAppl.getSmSupplier().getSmMofAccounts()) {
				if (acc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					getManagedBean().setSmMofAccount(acc);
					break;
				}
			}

			VirtualCertReqDTO certReqDTO = new VirtualCertReqDTO();
			certReqDTO.setApplId(smAppl.getApplId());

			List<SmMofCert> certs = SmServiceLookup.getVirtualCertSO()
					.retrieveMofCertsByApplId(certReqDTO).getSmMofCerts();

			if (!EntityHelper.isCollectionEmpty(certs)) {
				for (SmMofCert cert : certs) {
					if (!cert.isBumiCert()) {
						getManagedBean().setSmMofCert(cert);
						break;
					}
				}
			}
		}

		// set reason to cancel MOF (#24811)
		if (getManagedBean().getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			getManagedBean().setCancelRemarkVO(
					SmCommonUtil.getInstance().getCancelMofRemarksVO(
							getManagedBean().getSmAppl()));
		}

		// init current regsitration status description
		PmStatusDesc statusDesc = SmApplicationUtil
				.getCurrRegStatusDesc(getManagedBean().getSmAppl());
		if (statusDesc != null) {
			getManagedBean().setCurrRegStatus(statusDesc.getStatusName());
		}
		
		//28767
		this.getManagedBean().getPersonnelDirectorManager().clear();
		this.getManagedBean().getPersonnelMembers().clear();
		BigDecimal sum = new BigDecimal(0);
		for(SmPersonnel personnel:this.getManagedBean().getSmAppl().getSmPersonnels()){
			BigDecimal shareAmt = personnel.getShareAmt();
				if(personnel.getDesignation() != null){
					String designation = populateDesignationSSM(personnel.getDesignation());
					personnel.setDesignation(designation);
				}
				if(personnel.isDirector() || personnel.isManagement()){
				this.getManagedBean().getPersonnelDirectorManager().add(personnel);	
				}
				if(personnel.isEquityOwner()){
					if(shareAmt != null && !(personnel.getShareAmt() == null)){
						sum =  sum.add(shareAmt);
						getManagedBean().setTotalShare(sum);
						
					}
					this.getManagedBean().getPersonnelMembers().add(personnel);
				}
		
		}
		
		getManagedBean().setReviewApplicationExist(true);
	}

	/**
	 * Initialized revision application company information step
	 */
	public void initStep1CompanyInfo() {
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
				if (assocBody.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
						&& assocBody.getSmPersonnel() == null) {
					activeAssocBodies.add(assocBody);
				}
			}
			getManagedBean().getSmAppl().setSmAssociationBodies(
					activeAssocBodies);
		}

		// init company basic gst
		getManagedBean().setHqGst(getSupplierGst(null));
		// #29617 start
		if(getManagedBean().getSmSupplier().getSmeRegisteredFlag() == 1){
			setSmeSupplierInfo(getManagedBean().getSmSupplier());
		}
		// #29617 end

	}

	/**
	 * Initialized branch company information step
	 */
	public void initStep2BranchCompanyInfo() {
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
			removeDeletedFromList(getManagedBean().getSmAppl()
					.getSmSupplierBranches());
		}

		// associatedCompanyInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmAssociateCompanies() == null) {
			getManagedBean().getSmAppl().setSmAssociateCompanies(
					new ArrayList<SmAssociateCompany>());
		} else {
			getManagedBean().getSmAppl().setSmAssociateCompanies(
					getAssocCompanyByRevNo());
			removeDeletedFromList(getManagedBean().getSmAppl()
					.getSmAssociateCompanies());
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
	public void initStep3PersonnelInfo() {
		List<SmPersonnel> deletedPersonnels = new ArrayList<SmPersonnel>();

		if (getManagedBean().getSmAppl().getSmPersonnels() == null) {
			getManagedBean().getSmAppl().setSmPersonnels(
					new ArrayList<SmPersonnel>());
		} else {
			for (SmPersonnel smPersonnel : getManagedBean().getSmAppl()
					.getSmPersonnels()) {

				if (smPersonnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE
						&& smPersonnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_PENDING_ACTIVATION) {
					deletedPersonnels.add(smPersonnel);
				}

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
		}
		SmApplicationUtil.sortPersonnelByRoleInCompany(getManagedBean()
				.getSmAppl());
		getManagedBean().getSmAppl().getSmPersonnels()
				.removeAll(deletedPersonnels);
		setPersonnelInfoCalculationSummary();
	}

	/**
	 * Initialized capital and equity information step
	 */
	public void initStep4CaptialAndEquityInfo() {
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
	public void initStep5BankInfo() {
		getManagedBean().setSmSupplierBankList(getSupplierBankByRevNo());
		getManagedBean().setSelectedSignatoryBankAcct(new SmSupplierBank());
		removeDeletedFromList(getManagedBean().getSmSupplierBankList());
	}

	/**
	 * Initialized register category code step
	 */
	public void initStep6RegisterCategoryCode() {
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
	public void initStep6_1ViewFactoryInfo() {
		// factoryOrWorkshopInfoFormViewOnly/dockyardInfoFormViewOnly/workshopInfoFormViewOnly/storeInfoFormViewOnly
		setApplicationFacilities();
	}

	/**
	 * Initialized Equipment Information step
	 */
	public void initStep6_2EquipmentInfo() {
		// machineryInfoFormViewOnly
		if (getManagedBean().getSmAppl().getSmMachineDtls() == null) {
			getManagedBean().getSmAppl().setSmMachineDtls(
					new ArrayList<SmMachineDtl>());
		} else {
			List<SmMachineDtl> activeMachineDtls = new ArrayList<SmMachineDtl>();
			SmMachineDtl machDtlTmp = null;

			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				// #30021 - start
//				if (cat.getRecordStatusFlg().compareTo(
//						SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
				// #30021 - end
					for (SmMachineDtl machineDtl : getManagedBean().getSmAppl()
							.getSmMachineDtls()) {
						machDtlTmp = (SmMachineDtl) EntityHelper
								.getEntityBySinglePropertyFromList(
										machineDtl.getMachineDtlId(),
										activeMachineDtls, "machineDtlId");
						if (machineDtl.getSmMachineCategories() != null
								&& machineDtl.getSmMachineCategories().size() > 0) {
							for (SmMachineCategory machCat : machineDtl
									.getSmMachineCategories()) {
								String macCatCode = machCat
										.getPmCategoryL3()
										.getPmCategoryL2()
										.getPmCategoryL1()
										.getCategoryL1Code()
										.concat(machCat.getPmCategoryL3()
												.getPmCategoryL2()
												.getCategoryL2Code())
										.concat(machCat.getPmCategoryL3()
												.getCategoryL3Code());
								String suppCatCode = cat
										.getPmCategoryL3()
										.getPmCategoryL2()
										.getPmCategoryL1()
										.getCategoryL1Code()
										.concat(cat.getPmCategoryL3()
												.getPmCategoryL2()
												.getCategoryL2Code())
										.concat(cat.getPmCategoryL3()
												.getCategoryL3Code());
								if (machineDtl.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
										&& macCatCode
												.equalsIgnoreCase(suppCatCode)
										&& machDtlTmp == null) {
									activeMachineDtls.add(machineDtl);
									break;
								}
							}
						}
					}
				// #30021 - start
//				}
				// #30021 - end
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

		getManagedBean().setDockYardFacilitiesVOList(
				getCategoryFacilityVO(getManagedBean().getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD));
		getManagedBean().setWorkshopFacilitiesVOList(
				getCategoryFacilityVO(getManagedBean().getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP));
		getManagedBean().setStoreFacilitiesVOList(
				getCategoryFacilityVO(getManagedBean().getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_STORE));
		getManagedBean().setFactoryFacilitiesVOList(
				getCategoryFacilityVO(getManagedBean().getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_FACTORY));
	}

	/**
	 * Initialized Suppoerting Document Information step
	 */
	public void initStep7SupportingDocumentsInfo() {
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
			
			//#30294 - START
			if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl().getSmApplSupportDocs())) {
				Collections.sort(getManagedBean().getSmAppl().getSmApplSupportDocs(), new Comparator<SmApplSupportDoc>() {
							@Override
							public int compare(final SmApplSupportDoc s1, final SmApplSupportDoc s2) {
								if(s1.getPmDocument().getDisplaySeq() == null){
									return (s2.getPmDocument().getDisplaySeq() == null) ?  0 : 1;
								}
								if(s2.getPmDocument().getDisplaySeq() == null){
									return -1;
								}
								return Integer.valueOf(s1.getPmDocument().getDisplaySeq().compareTo(s2.getPmDocument().getDisplaySeq()));
							}
						});
			}
			//#30294 - END
		}
		
		//#3460 - Start
		//To get sdo remarks by application
		getManagedBean().setSdoRemark("");
		initSDORemarkInfo();
		//#3460 - End
	}

	/**
	 * Initialized PO or Approver Default Decision page
	 */
	public void initStep8_9PoApproverDefaultDecision() {
		// poDefault_DescisionPageViewOnly/approverDefault_DescisionPageViewOnly
		setReviewSectionRemarks();
		setSupplierCancellationRemarks(getManagedBean().getSmAppl()
				.getApplType(), getManagedBean().getSmAppl().getApplId());
		setPmStatusDesc(getManagedBean().getSmAppl().getPmStatus() == null ? ""
				: getManagedBean().getSmAppl().getPmStatus().getStatusId()
						.toString());
		//#30730 - START
		if(this.getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR) ||
		   this.getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
			//#12483 - START
			if(getManagedBean().getSmAppl().getSmApplRevises() != null){
				sortApplicationRevises();
			}
			//#12483 - END
		}
		//#30730 - END
	}

	/**
	 * Set supplier cancellation remarks
	 * 
	 * @param String
	 *            docType
	 * @param String
	 *            docId
	 */
	public void setSupplierCancellationRemarks(String docType, Long docId) {
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
	public void setPmStatusDesc(String statusId) {
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
			}
			if (cat.getPmCategoryL2() != null) {
				catL2.append(cat.getPmCategoryL2().getCategoryName()).append(
						"<br/>");
			}
			if (cat.getPmCategoryL3() != null) {
				catL3.append(cat.getPmCategoryL3().getCategoryName()).append(
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

		SmPersonnelAddress perm = new SmPersonnelAddress();
		perm.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM);
		perm.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		perm.getSmAddress().setPmCountry(new PmCountry());
		perm.setSmPersonnel(shareHolderBean);
		perm.setSameAsPerm(false);
		getManagedBean().setShareHolderPermAddr(perm);

		SmPersonnelAddress mail = new SmPersonnelAddress();
		mail.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL);
		mail.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		mail.getSmAddress().setPmCountry(new PmCountry());
		mail.setSmPersonnel(shareHolderBean);
		mail.setSameAsPerm(false);
		getManagedBean().setShareHolderMailAddr(mail);

		SmPersonnelAddress off = new SmPersonnelAddress();
		off.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE);
		off.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		off.getSmAddress().setPmCountry(new PmCountry());
		off.setSmPersonnel(shareHolderBean);
		off.setSameAsPerm(false);
		getManagedBean().setShareHolderResdAddr(off);

		SmPersonnelAddress res = new SmPersonnelAddress();
		res.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES);
		res.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		res.getSmAddress().setPmCountry(new PmCountry());
		res.setSmPersonnel(shareHolderBean);
		res.setSameAsPerm(false);
		getManagedBean().setShareHolderOffAddr(res);

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
			/*#28256 - Start*/
			if (getManagedBean().getSelectedShareHolder().isDirector()
					|| getManagedBean().getSelectedShareHolder()
							.isHigherManagement()) {
				getManagedBean().getSelectedShareHolder().setClerical(false);
				getManagedBean().getSelectedShareHolder().setDisableStaff(true);
			} else {
				getManagedBean().getSelectedShareHolder()
						.setDisableStaff(false);
			}
			/*#28256 - End*/

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
	public void setApplicationShareHolders(List<SmPersonnel> perList) {
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
		//31480 start
		List<SmDisciplinaryAction> resultList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(getManagedBean().getSmAppl().getSmSupplier());
		//31480 end
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.APPROVED_RECORD_STATUS) == 0
					|| cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
				catList.add(cat);
			}
			if (getManagedBean().isViewApplicationHistory()) {
				// for history load all category
				getManagedBean().getSuppProfileOrHistoryCategoryList().add(cat);
			} else {
				//31480 - start
				// for supp profile load all active category only
				if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					//29949:Disciplinary Action - 26238 - start, filter out category code that active and imposed with DA
					if(getManagedBean().getSmAppl().getSmSupplier() != null){
						if (resultList != null && resultList.size() > 0) {
							for(SmDisciplinaryAction disciplinaryAction: resultList){
								if((disciplinaryAction.isCancelCategory()|| disciplinaryAction.isManufacturerCancel() || disciplinaryAction.isBumiputeraCancel()) && cat.getDisciplinaryActionId() == null){
									getManagedBean().getSuppProfileOrHistoryCategoryList().add(
											cat);
								}
								
							}
						} else {
							getManagedBean().getSuppProfileOrHistoryCategoryList().add(cat);//29949:Disciplinary Action - 26238 - bug fix
						}
					}
					//29949:Disciplinary Action - 26238 - end
				}
				//to show category for bumiputera that imposed canceled bumiputera DA but still have pending
				else if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_CANCELLED) {
					if (getManagedBean().getSmAppl().getSmSupplier() != null) {
						if (resultList != null && resultList.size() > 0) {
							for (SmDisciplinaryAction disciplinaryAction : resultList) {
								if (cat.getDisciplinaryActionId().equals(disciplinaryAction.getDisciplinaryActionId())) {
									if (disciplinaryAction.isBumiputeraCancel() && disciplinaryAction.isBumiputeraPending()) {
										getManagedBean().getSuppProfileOrHistoryCategoryList().add(cat);
									}
									break;
								}
							}
						}
					}
				}
				//31480 - end
			}
		}
		getManagedBean().setActiveCategoryList(catList);

		// sort by record status
		Collections.sort(getManagedBean().getActiveCategoryList(),
				new Comparator<SmSupplierCategory>() {
					@Override
					public int compare(final SmSupplierCategory s1,
							final SmSupplierCategory s2) {
						return s1.getRecordStatusFlg()
								- s2.getRecordStatusFlg();
					}
				});

		//14150
		SmApplicationUtil.sortSupplierCategory(getManagedBean().getSuppProfileOrHistoryCategoryList());
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
	 * On change supplier type selection
	 */
	public void onChangeSupplierType() {
		getManagedBean().setRegStatusDescList(
				SmApplicationUtil.getInstance().retrieveRegistrationStatusList(
						getManagedBean().getSearchSuppType(), null));
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
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_USER)
				|| sc.hasRoleUser(RoleCode.SUPPLIER_TEMP.value())
				|| sc.hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())
				|| sc.hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())) {
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
		} else if (sc.hasRoleUser(SupplierManagementConst.BPK_AUDITOR)) {
			getManagedBean().setBpkUser(true);
			getManagedBean().setRoleCode(RoleCode.BPK_AUDITOR.value());
		} else if (sc.hasRoleUser(SupplierManagementConst.BPK_OFFICER)) {
			getManagedBean().setBpkUser(true);
			getManagedBean().setRoleCode(RoleCode.BPK_OFFICER.value());
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
			// 25792 - start
			FacesScopes sc = new FacesScopes();
			if (sc.hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) {

				String agencyTypeName = getAgencyTypeName(getManagedBean()
						.getSearchSupplier());
				getManagedBean().setAgencyTypeName(agencyTypeName);
				getManagedBean().setG2GSupplierAdmin(true);

			}
			// 25792 - end

		} else {
			getManagedBean().setSearchSupplier(new SmSupplier());
			getManagedBean().getSearchSupplier().setCompanyName("");
			getManagedBean().getSearchSupplier().setEpNo("");
			getManagedBean().getSearchSupplier().setRegNo("");
		}
	}

	/**
	 * Initialized login user zone
	 */
	public void initLoginUserZone() {
		FacesScopes fs = new FacesScopes();
		PmUser user = fs.getUserSessionBean();
		List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user
				.getUserId());
		String ePRole = StringUtils.EMPTY;
		if (!EntityHelper.isCollectionEmpty(userRoles)) {
			for (PmUserRole userRole : userRoles) {
				if (userRole.getPmRole().getRoleCode()
						.equals(RoleCode.UPS_APPROVER.value())
						|| userRole.getPmRole().getRoleCode()
								.equals(RoleCode.UPS_PO.value())) {
					ePRole = userRole.getPmRole().getRoleCode();
					break;
				}
			}
		}
		getManagedBean().setSearchPmZoneId(
				SmCommonUtil.getUserZoneIdByLoginIdAndEpRole(user.getLoginId(),
						ePRole));
		ZoneReqDTO reqDTO = new ZoneReqDTO();
		reqDTO.setZoneId(getManagedBean().getSearchPmZoneId());

		if (getManagedBean().getSearchPmZoneId() != null) {
			getManagedBean().setLoginUserZone(
					SmServiceLookup.getZoneSO().retriveZoneByID(reqDTO)
							.getPmZone());
		}

	}

	/**
	 * Set url for Supplier Profile or Supplier Application History.
	 */
	public void setSupplierProfileOrHistoryUrl(String baseUrl,
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
		//30974 - start
		String poRemarks = "";
		//30974 - end
		log.info("View Application");
		if (getManagedBean().isViewApplicationHistory()) {
			basePage = "/xhtml/sm/revisionSmApplView/viewApplicationHistory/smViewApplHistorySideMenu_BasePage.xhtml";
			if (this.getManagedBean().isBpkUser()) {
				baseUrl = "/supplier-profile-search";
			} else {
				baseUrl = "/supplier-profile";
			}
			baseUrlLabel = SmCommonUtil.getMessage("lbl.sm.supprofile", null);
		} else {
			basePage = "/xhtml/sm/revisionSmApplView/viewSupplierProfile/smViewProfileSideMenu_BasePage.xhtml";
			if (this.getManagedBean().isBpkUser()) {
				baseUrl = "/supplier-application-search";
			} else {
				baseUrl = "/application-history";
			}
			baseUrlLabel = SmCommonUtil.getMessage(
					"lbl.sm.supplier.application.history", null);
		}

		// reset previous session redirect to personnel private page
		getManagedBean().setRedirectToPersonPage(false);

		SmAppl smAppl = getApplByLoginRole(selectedSmAppl);
		// 25792 - start
		if(SupplierManagementConst.SUPP_TYPE_BASIC
				.equalsIgnoreCase(smAppl.getSupplierType())) {
			getManagedBean().setAccountType(SupplierManagementConst.SUPP_TYPE_BASIC);
		} else if(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
				.equalsIgnoreCase(smAppl.getSupplierType())) {
			getManagedBean().setAccountType(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT);
		} else {
			getManagedBean().setAccountType("M");
		}
		if (SupplierManagementConst.SUPP_TYPE_BASIC
				.equalsIgnoreCase(getManagedBean().getAccountType())
				|| SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
						.equalsIgnoreCase(getManagedBean().getAccountType())) {
			log.info("Non MOF");
			initStep0BaseInfo(smAppl);
			initFirstTabInfo();
		} else {
			log.info("MOF");
			setApplication(smAppl);
		}
		// 25792 - end
		//30974
		List<SmApplRejectReason> ReasonsByPO = new ArrayList<SmApplRejectReason>();
		if(this.getManagedBean().getSmAppl()!= null && this.getManagedBean().getSmAppl().getSmApplRejectReasons() != null){
			for(SmApplRejectReason reasonPO : this.getManagedBean().getSmAppl().getSmApplRejectReasons()){
				if(reasonPO.isReasonByPo()){
					ReasonsByPO.add(reasonPO);
				}
			}
		
			//30974 - start
			if (this.getManagedBean().getSmAppl().getSmApplSectionReviews() != null
					&& this.getManagedBean().getSmAppl().getSmApplSectionReviews().size() > 0) {
				for (SmApplSectionReview sectionreview : this.getManagedBean().getSmAppl().getSmApplSectionReviews()) {
					if (sectionreview.getRemarkToApprover() != null
							&& !sectionreview.getRemarkToApprover().equalsIgnoreCase("")) {
						poRemarks = sectionreview.getRemarkToApprover();
						getManagedBean().setRejectReasonByPO(
								SmCommonUtil.getHtmlApplicationRejectReasonsByPo(ReasonsByPO, poRemarks));
					}else{
						getManagedBean().setRejectReasonByPO(SmCommonUtil.getHtmlApplicationRejectReasons(ReasonsByPO));
					}
				}
			} else {
				getManagedBean().setRejectReasonByPO(SmCommonUtil.getHtmlApplicationRejectReasons(ReasonsByPO));
			}
			//30974 - end
		}
		//30974
		getManagedBean().setRejectReason(getApplicationRejectionReason());
		
		setSupplierProfileOrHistoryUrl(baseUrl, baseUrlLabel, smAppl
				.getSmSupplier().getSupplierId().toString());
		SmCommonUtil.redirectToPage(basePage, "", null);
	}

	/**
	 * Validate prefix 3 characters for searching by company name
	 */
	public void validateCompanySearch() {
		if (getManagedBean().getSearchSupplier() != null) {
			if (!StringUtils.isBlank(getManagedBean().getSearchSupplier()
					.getCompanyName())
					&& getManagedBean().getSearchSupplier().getCompanyName()
							.length() < 3) {
				throw new EpException(SmCommonUtil.getMessage(
						"lbl.sm.tt.search.prefix.three.char", null));
			}
			// 25792 - start
			else {
				if (SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
						.equalsIgnoreCase(getManagedBean().getAccountType())) {
					if (!StringUtils.isBlank(getManagedBean().getAgencyName())
							&& getManagedBean().getAgencyName().length() < 3) {
						throw new EpException(SmCommonUtil.getMessage(
								"lbl.sm.tt.agency.search.prefix.three.char", null));
					}
				}

			}
			// 25792 - end
		}

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
		String rejectReasons = StringUtils.EMPTY;
		//30974 - Start (16/7/2021)
		String rejectReasonsByPO = StringUtils.EMPTY;
		//30974 - End (16/7/2021)
		if (getManagedBean().getSmAppl().isActiveAppl()
				&& getManagedBean().getSmAppl().isResubmit()
				&& getManagedBean().getSmAppl().getOriginalAppl() != null) {

			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			mofReqDTO.setSmAppl(getManagedBean().getSmAppl());
			List<SmApplRejectReason> rejectReasonList = SmServiceLookup
					.getMofRegistrationSO()
					.retrieveLatestRejectedApplicationRejectReasons(mofReqDTO)
					.getRejectReasons();
			rejectReasons = SmCommonUtil
					.getHtmlApplicationRejectReasons(rejectReasonList);

			//			Refs #15629 - Start - 5/2/2021
			//			if (StringUtils.isBlank(rejectReasons)) {
			//				rejectReasons = SmApplicationUtil.getSmRemarksByDocTypeAndId(
			//						ParameterConst.SM_APPLN_RMRK_DECISION_REJECT,
			//						getManagedBean().getSmAppl().getOriginalAppl()
			//								.getApplId());
			//			}
			//			Refs #15629 - End - 5/2/2021
			//30974 - start - 13/10/2021
			List<SmApplRejectReason> ReasonsByPO = new ArrayList<SmApplRejectReason>();
			for (SmApplRejectReason reason : getManagedBean().getSmAppl().getSmApplRejectReasons()) {
				if (reason.isReasonByPo()) {
					ReasonsByPO.add(reason);
				}
			}
			if(ReasonsByPO != null && !ReasonsByPO.isEmpty()){
				getManagedBean().setSmApplRejectReasonsByPO(ReasonsByPO);
				rejectReasonsByPO = SmCommonUtil
						.getHtmlApplicationRejectReasons(getManagedBean().getSmApplRejectReasonsByPO());
			}
			//30974 - end - 13/10/2021
			
			//30974 - Start - 21/10/2021
			if (this.getManagedBean().getSmAppl().getPmStatus() != null
					&& this.getManagedBean().getSmAppl().getPmStatus().getStatusId()
							.equals(SupplierManagementConst.PM_STATUS_PENDING_PAYMENT)
					&& this.getManagedBean().getSmAppl().getApplType()
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
				rejectReasons = StringUtils.EMPTY;
			}
			//30974 - end - 21/10/2021
		} else {
			if (getManagedBean().getSmAppl().getApplType()
					.equals(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				SmRemarksReqDTO remarkReqDTO = new SmRemarksReqDTO();
				remarkReqDTO.setDocId(getManagedBean().getSmAppl().getApplId());
				remarkReqDTO
						.setDocType(ParameterConst.SM_APPLN_RMRK_DECISION_REJECT);
				SmRemaksResDTO remarkResDTO = SmServiceLookup.getSmRemarksSO()
						.retrieveSmRemarksByDocument(remarkReqDTO);
				if (!EntityHelper.isCollectionEmpty(remarkResDTO
						.getRemarksList())) {
					rejectReasons = remarkResDTO.getRemarksList().get(0)
							.getRemark();
				}
			} else {
				
				//	Refs #15629 - Start - 21/1/2021
				if(!EntityHelper.isCollectionEmpty(getManagedBean()
						.getSmAppl().getSmApplRejectReasons())){
					//#30974 - Start - 2/7/2021
					if (this.getManagedBean().getSmAppl().getPmStatus().getStatusId() != null) {
						if (this.getManagedBean().getSmAppl().getPmStatus().getStatusId().toString()
								.equalsIgnoreCase(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER)) {
							Iterator<SmApplRejectReason> smApplRejectReasonIter = getManagedBean().getSmAppl()
									.getSmApplRejectReasons().iterator();
							while (smApplRejectReasonIter.hasNext()) {
								SmApplRejectReason smApplRejectReason = smApplRejectReasonIter.next();
								if (!smApplRejectReason.isReasonByPo()) {
									smApplRejectReasonIter.remove();
								}
							}
							// To display Reject Reasons Only By PO - Start
							List<SmApplRejectReason> ReasonsByPO = new ArrayList<SmApplRejectReason>(getManagedBean()
									.getSmAppl().getSmApplRejectReasons());
							getManagedBean().setSmApplRejectReasonsByPO(ReasonsByPO);
							// To display Reject Reasons Only By PO - End
						} else if (this.getManagedBean().getSmAppl().getPmStatus().getStatusId().toString()
								.equalsIgnoreCase(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER)) {

							// To display Reject Reasons Only By PO - Start
							List<SmApplRejectReason> ReasonsByPO = new ArrayList<SmApplRejectReason>();
							for (SmApplRejectReason reason : getManagedBean().getSmAppl().getSmApplRejectReasons()) {
								if (reason.isReasonByPo()) {
									ReasonsByPO.add(reason);
								}
							}
							if(ReasonsByPO != null && !ReasonsByPO.isEmpty()){
								getManagedBean().setSmApplRejectReasonsByPO(ReasonsByPO);	
							}
							// To display Reject Reasons Only By PO - End

							Iterator<SmApplRejectReason> smApplRejectReasonIter = getManagedBean().getSmAppl()
									.getSmApplRejectReasons().iterator();
							while (smApplRejectReasonIter.hasNext()) {
								SmApplRejectReason smApplRejectReason = smApplRejectReasonIter.next();
								if (smApplRejectReason.isReasonByPo()) {
									smApplRejectReasonIter.remove();
								}
							}
						} else {
							//If there is status id but not rejected
							// To display Reject Reasons Only By PO - Start
							List<SmApplRejectReason> ReasonsByPO = new ArrayList<SmApplRejectReason>();
							for (SmApplRejectReason reason : getManagedBean().getSmAppl().getSmApplRejectReasons()) {
								if (reason.isReasonByPo()) {
									ReasonsByPO.add(reason);
								}
							}
							if(ReasonsByPO != null && !ReasonsByPO.isEmpty()){
								getManagedBean().setSmApplRejectReasonsByPO(ReasonsByPO);			
							}	
							// To display Reject Reasons Only By PO - End
						}
					} else {
						//If there is no status id null, it goes into this block
						// To display Reject Reasons Only By PO - Start
						List<SmApplRejectReason> ReasonsByPO = new ArrayList<SmApplRejectReason>();
						for (SmApplRejectReason reason : getManagedBean().getSmAppl().getSmApplRejectReasons()) {
							if (reason.isReasonByPo()) {
								ReasonsByPO.add(reason);
							}
						}
						if(ReasonsByPO != null && !ReasonsByPO.isEmpty()){
							getManagedBean().setSmApplRejectReasonsByPO(ReasonsByPO);
						}
						// To display Reject Reasons Only By PO - End
					}
					
					//since there is duplicate problem occurs, If there is duplicate ID checking - Start
					//this is for all reasons
					if(getManagedBean().getSmAppl().getSmApplRejectReasons() != null && getManagedBean().getSmAppl().getSmApplRejectReasons().size() > 0){
						Map<String, SmApplRejectReason> removedDuplicateID = new HashMap<String, SmApplRejectReason>();
						List<SmApplRejectReason> newListOfRejectID = new ArrayList<>();
						for(SmApplRejectReason reason : this.getManagedBean().getSmAppl().getSmApplRejectReasons()){
							removedDuplicateID.put(reason.getPmReason().getReasonId().toString(), reason);
						}
						Iterator itr = removedDuplicateID.keySet().iterator();
						while (itr.hasNext()){
							String id = itr.next().toString();
							newListOfRejectID.add((SmApplRejectReason)removedDuplicateID.get(id));
						}
						getManagedBean().getSmAppl().setSmApplRejectReasons(newListOfRejectID);	
					}
					
					//this is for PO reject reasons only
					if(getManagedBean().getSmApplRejectReasonsByPO() != null && getManagedBean().getSmApplRejectReasonsByPO().size() > 0){
						Map<String, SmApplRejectReason> removedDuplicateID = new HashMap<String, SmApplRejectReason>();
						List<SmApplRejectReason> newListOfRejectID = new ArrayList<>();
						for(SmApplRejectReason reason : this.getManagedBean().getSmApplRejectReasonsByPO()){
							removedDuplicateID.put(reason.getPmReason().getReasonId().toString(), reason);
						}
						Iterator itr = removedDuplicateID.keySet().iterator();
						while (itr.hasNext()){
							String id = itr.next().toString();
							newListOfRejectID.add((SmApplRejectReason)removedDuplicateID.get(id));
						}
						getManagedBean().setSmApplRejectReasonsByPO(newListOfRejectID);	
					}
					// If there is duplicate ID checking - End
					//#30974 - End - 2/7/2021
				}
				// Refs #15629 - End - 21/1/2021
				
				//30974 - Start - 13/10/2021
				if (this.getManagedBean().getSmAppl().getPmStatus() != null && !this.getManagedBean().getSmAppl().isActiveAppl()
						&& !this.getManagedBean().getSmAppl().getPmStatus().getStatusId().toString()
								.equalsIgnoreCase(SupplierManagementConst.PM_STATUS_REGISTERED)){
					rejectReasons = SmCommonUtil.getHtmlApplicationRejectReasons(getManagedBean().getSmAppl()
							.getSmApplRejectReasons());
				}
				//30974 - end - 13/10/2021
				if (StringUtils.isBlank(rejectReasons)) {
					rejectReasons = SmApplicationUtil
							.getSmRemarksByDocTypeAndId(
									ParameterConst.SM_APPLN_RMRK_DECISION_REJECT,
									getManagedBean().getSmAppl().getApplId());
				}
				//30974 - Start - 16/7/2021
				if(!EntityHelper.isCollectionEmpty(getManagedBean().getSmApplRejectReasonsByPO())){
					rejectReasonsByPO = SmCommonUtil
							.getHtmlApplicationRejectReasons(getManagedBean().getSmApplRejectReasonsByPO());

					if (StringUtils.isBlank(rejectReasonsByPO)) {
						rejectReasons = SmApplicationUtil
								.getSmRemarksByDocTypeAndId(
										ParameterConst.SM_APPLN_RMRK_DECISION_REJECT,
										getManagedBean().getSmAppl().getApplId());
					}
				}
				
				//30974 - End - 16/7/2021
			}
		}
		log.info("HTML REJECT REASON:" + rejectReasons);
		log.info("REJECT REASON BY PO ONLY: " + rejectReasons);
		//30974 - Start - 16/7/2021
		/*if(!StringUtils.isBlank(rejectReasonsByPO)){
			getManagedBean().setRejectReasonByPO(rejectReasonsByPO);
		}*/
		//30974 - End - 16/7/2021
		return rejectReasons;
	}

	/**
	 * Print Supplier Profile
	 * 
	 * @throws IOException
	 */
	public void printSupplierProfile() throws IOException {
		Map<String, String> paramMap = new HashMap<String, String>();
		//25792 - start
		String supplierType = getManagedBean().getSmAppl().getSupplierType();
		if(my.ep.constant.sm.SupplierManagementConst.SUPP_TYPE_BASIC
				.equalsIgnoreCase(supplierType)) {
			//currently PAPPLID and PLANGUAGE use for Basic and G2G
			paramMap.put("PAPPLID", String.valueOf(getManagedBean().getSmAppl().getApplId()));
			paramMap.put("PLANGUAGE", new FacesScopes().getUserLocale().getLanguage());
			generateReport(ReportConst.SM_SUPPLIER_PROFILE_BASIC_ACCOUNT_REPORT, paramMap);
		} else if(my.ep.constant.sm.SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
				.equalsIgnoreCase(supplierType)) {
			//currently PAPPLID and PLANGUAGE use for Basic and G2G
			paramMap.put("PAPPLID", String.valueOf(getManagedBean().getSmAppl().getApplId()));
			paramMap.put("PLANGUAGE", new FacesScopes().getUserLocale().getLanguage());
			paramMap.put("PAGENCYTYPE", getManagedBean().getAgencyTypeName());
			generateReport(ReportConst.SM_SUPPLIER_PROFILE_G2G_REPORT, paramMap);
		} else {
			paramMap.put("PAPPLNO", getManagedBean().getSmAppl().getApplNo());
			// #29367 & #30574 - start
			System.out.println("applStatusId ::: " + getManagedBean().getSmAppl().getPmStatus().getStatusId());
			if(SupplierManagementConst.LIST_APPLICATION_DETAILS_REPORT.contains(getManagedBean().getSmAppl().getPmStatus().getStatusId().longValue())){
				log.info("1 - SM_APPLICATION_DETAILS");
				generateReport(ReportConst.SM_SUPPLIER_PROFILE_REPORT, paramMap);
			} else {
				log.info("2 - SM_SUPPLIER_UNREGIS_REPORT");
				generateReport(ReportConst.SM_SUPPLIER_UNREGIS_REPORT, paramMap);
			}
			// #29367 & #30574 - end
		}
		//25792 - end
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

			if (activeCompanyBasic != null
					&& activeCompanyBasic.getTotalMember() != null
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
						if (param.getRevNo() == 1) {
							param.setSmChequeSignatories(getCheckSignatories(param));
							objList.add(param);
						}
					}
				}
				// appl history load by po/approver role for revNo=2
				else {

					// Check if no record for revision no 2, then just take
					// without filter
					obj = (SmSupplierBank) EntityHelper
							.getEntityBySinglePropertyFromList(2, mainList,
									"revNo");
					if (obj == null) {
						for (SmSupplierBank param : mainList) {
							param.setSmChequeSignatories(getCheckSignatories(param));
							objList.add(param);
						}
					} else {
						for (SmSupplierBank param : mainList) {
							if (param.getRevNo() == 2) {
								param.setSmChequeSignatories(getCheckSignatories(param));
								objList.add(param);
							}
						}
					}

				}
			} else {
				// supplier profile direct load to revNo=2

				// Check if no record for revision no 2, then just take
				// without filter
				obj = (SmSupplierBank) EntityHelper
						.getEntityBySinglePropertyFromList(2, mainList, "revNo");
				if (obj == null) {
					for (SmSupplierBank param : mainList) {
						param.setSmChequeSignatories(getCheckSignatories(param));
						objList.add(param);
					}
				} else {
					for (SmSupplierBank param : mainList) {
						if (param.getRevNo() == 2) {
							param.setSmChequeSignatories(getCheckSignatories(param));
							objList.add(param);
						}
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

	public SmGstSupplier getSupplierGst(Long branchCode) {
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

	public void onClickClone() {
		RequestContext.getCurrentInstance().execute("cloneDialog.show()");
	}

	public void cloneApplication() {
		if (!StringUtils.isBlank(getManagedBean().getSrcApplNo())
				&& !StringUtils.isBlank(getManagedBean().getTargetApplNo())) {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplNo(getManagedBean().getSrcApplNo());
			SmAppl srcAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl();
			reqDTO.setApplNo(getManagedBean().getTargetApplNo());
			SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl();
			reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSmAppl(smAppl);
			reqDTO.setSrcAppl(srcAppl);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
				"Clone Success!!!"));
		RequestContext.getCurrentInstance().execute("cloneDialog.hide()");
	}

	/**
	 * Construct and get category and facility list to display in facility
	 * section
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return List<SmCategoryFacilityVO>
	 */
	public static List<PmCategoryL3> getCategoryFacilityVO(SmAppl smAppl,
			String facType) {
		return getCategoryFacilityVO(facType, smAppl.getSmSupplierCategories(),
				smAppl.getSmFacilities());
	}

	public static List<PmCategoryL3> getCategoryFacilityVO(String facType,
			List<SmSupplierCategory> categories, List<SmFacility> facilities) {
		List<PmCategoryL3> categoryFacilityVOList = new ArrayList<PmCategoryL3>();
		if (!EntityHelper.isCollectionEmpty(categories)) {
			for (SmSupplierCategory cat : categories) {
				if (cat.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
						&& cat.getPmCategoryL3() != null
						&& !StringUtils.isBlank(cat.getPmCategoryL3()
								.getFacilityType())
						&& cat.getPmCategoryL3().getFacilityType()
								.equalsIgnoreCase(facType)) {

					cat.getPmCategoryL3().setFacilityList(
							new ArrayList<SmFacility>());
					cat.getPmCategoryL3().setSelectedFacilityList(
							new ArrayList<SmFacility>());

					for (SmFacility fac : facilities) {
						if (!EntityHelper.isCollectionEmpty(fac
								.getSmSupplierCategoryFacilities())) {
							SmSupplierCategoryFacility catTmp = (SmSupplierCategoryFacility) EntityHelper
									.getEntityBySinglePropertyFromList(cat
											.getPmCategoryL3()
											.getCategoryL3Id(), fac
											.getSmSupplierCategoryFacilities(),
											"pmCategoryL3");

							if (catTmp != null) {
								SmFacility facTmp = (SmFacility) EntityHelper
										.getEntityBySinglePropertyFromList(fac
												.getFacilityId(), cat
												.getPmCategoryL3()
												.getFacilityList(),
												"facilityId");
								if (facTmp == null) {
									cat.getPmCategoryL3().getFacilityList()
											.add(fac);
								}
							}
						}
					}

					// only add category that have facilities
					if (cat.getPmCategoryL3() != null
							&& !EntityHelper.isCollectionEmpty(cat
									.getPmCategoryL3().getFacilityList())) {
						categoryFacilityVOList.add(cat.getPmCategoryL3());
					}
				}
			}
		}
		return categoryFacilityVOList;
	}

	/**
	 * Generate Offline Supporting Document Stream Content
	 * 
	 * @return StreamedContent
	 */
	public StreamedContent getOfflineSuppDocCheckList() {
		StreamedContent retVal = null;
		try {
			retVal = SmCommonUtil
					.getPdfFileStreamContentByActualPath(SmCommonUtil
							.getUploadFilePath(SupplierManagementConst.OFFLINE_DOC_CHECKLIST_PATH)
							+ getManagedBean().getSmAppl().getApplNo()
							+ SupplierManagementConst.OFFLINE_SUPP_DOC_CHECKLIST_PREFIX);
		} catch (IOException e) {
			throw new EpException(e.getMessage());
		}
		return retVal;
	}

	public void printDocCheckList() {
		SmApplicationUtil.generateOfflineSuppDocChecklistPdf(getManagedBean()
				.getSmAppl().getSmApplSupportDocs(), getManagedBean()
				.getSmAppl(), "SUPPLIER_DECLARE");
		RequestContext.getCurrentInstance().execute("docCheckListDlg.show()");
	}

	public void removeDeletedFromList(List<? extends Object> mainList) {
		Iterator<? extends Object> removeIter = mainList.iterator();
		Object removeVal = null;
		while (removeIter.hasNext()) {
			Object removeObj = removeIter.next();
			try {
				removeVal = PropertyUtils.getProperty(removeObj,
						"recordStatusFlg");
				if (removeVal != null
						&& Integer.parseInt(removeVal.toString()) == DbValueConst.RECORD_STATUS_DELETED) {
					removeIter.remove();
				}
			} catch (IllegalAccessException | InvocationTargetException
					| NoSuchMethodException | NullPointerException e) {
				throw new EpException(e.getMessage());
			}
		}
	}

	// 25792 - start
	private IPersonnelSO getPersonnelSO() {
		IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);

		return personnelSO;
	}

	private ISupplierSearchSO getSupplierSearchSO() {
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		
		return supplierSearchSO;
	}
	
	private ISupplierBusinessNatureSO getSupplierBusinessSO() {
		ISupplierBusinessNatureSO supplierBusinessNatureSO = (ISupplierBusinessNatureSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierBusinessNatureSO.class);
		
		return supplierBusinessNatureSO;
	}

	// only applicable for Basic Account and G2G
	public void initFirstTabInfo() {
		log.info("Init First Tab Info");
		if (SupplierManagementConst.SUPP_TYPE_BASIC
				.equalsIgnoreCase(getManagedBean().getAccountType())) {
			log.info("Basic Account");
			if (SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL
					.equalsIgnoreCase(getManagedBean().getSmAppl()
							.getSmSupplier().getBusinessType())) {
				log.info("Individual");
				if (getManagedBean().getSmAppl().getSmCompanyBasic() == null) {
					getManagedBean().getSmAppl().setSmCompanyBasic(
							new SmCompanyBasic());
				} else {
					SmCompanyBasic activeCompBasic = getCompanyBasicByRevNo();
					getManagedBean().getSmAppl().setSmCompanyBasic(
							activeCompBasic);
				}
				ISupplierDataMaintenanceSO smDataMaintenanceSo = (ISupplierDataMaintenanceSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ISupplierDataMaintenanceSO.class);
				SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
				reqDTO.setSupplier(getManagedBean().getSmAppl().getSmSupplier());
				reqDTO.setUserLocale(new FacesScopes().getUserLocale());
				SupplierDataMaintenanceResDTO resDTO = smDataMaintenanceSo
						.retrieveSmSupplierOwners(reqDTO);
				SmAppl smAppl = resDTO.getSmAppl();
				List<SmSupplierOwner> smSupplierOwnerList = smAppl
						.getSmSupplier().getSmSupplierOwners();
				getManagedBean().getSmAppl().getSmSupplier()
						.setSmSupplierOwners(smSupplierOwnerList);

				IVendorCodeSO vendorCodeSO = (IVendorCodeSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT,
								IVendorCodeSO.class);

				SmSapVendorCode smSapVendorCode = vendorCodeSO
						.retrieveSapVendorCodeByEpNoAndBranchCode(
								getManagedBean().getSmAppl().getSmSupplier()
										.getEpNo(), null);

				if (smSapVendorCode != null) {
					getManagedBean().setSapVendorCode(
							smSapVendorCode.getSapVendorCode());
				}

				SupplierSearchReqDTO searchReqDTO = new SupplierSearchReqDTO();
				searchReqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
				SupplierSearchResDTO searchResDTO = getSupplierSearchSO().retrieveSmSupplierBankByAppl(searchReqDTO);
				List<SmSupplierBank> smSupplierBankList = searchResDTO.getSmSupplierBankList();
				getManagedBean().setSmSupplierBankList(smSupplierBankList);
				getManagedBean().setSelectedSignatoryBankAcct(
						new SmSupplierBank());
				removeDeletedFromList(getManagedBean().getSmSupplierBankList());
				setPersonnelInfo();
				if(getManagedBean().getSmAppl().getSmPersonnels() != null && !getManagedBean().getSmAppl().getSmPersonnels().isEmpty()) {
					getManagedBean().setSelectedPersonnel(getManagedBean().getSmAppl().getSmPersonnels().get(0));
				}
			} else {
				if (getManagedBean().getSmAppl().getSmCompanyBasic() == null) {
					getManagedBean().getSmAppl().setSmCompanyBasic(
							new SmCompanyBasic());
				} else {
					SmCompanyBasic activeCompBasic = getCompanyBasicByRevNo();
					getManagedBean().getSmAppl().setSmCompanyBasic(
							activeCompBasic);
				}
				ISupplierDataMaintenanceSO smDataMaintenanceSo = (ISupplierDataMaintenanceSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ISupplierDataMaintenanceSO.class);
				SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
				reqDTO.setSupplier(getManagedBean().getSmAppl().getSmSupplier());
				reqDTO.setUserLocale(new FacesScopes().getUserLocale());
				SupplierDataMaintenanceResDTO resDTO = smDataMaintenanceSo
						.retrieveSmSupplierOwners(reqDTO);
				SmAppl smAppl = resDTO.getSmAppl();
				List<SmSupplierOwner> smSupplierOwnerList = smAppl
						.getSmSupplier().getSmSupplierOwners();
				getManagedBean().getSmAppl().getSmSupplier()
						.setSmSupplierOwners(smSupplierOwnerList);

				IVendorCodeSO vendorCodeSO = (IVendorCodeSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT,
								IVendorCodeSO.class);

				SmSapVendorCode smSapVendorCode = vendorCodeSO
						.retrieveSapVendorCodeByEpNoAndBranchCode(
								getManagedBean().getSmAppl().getSmSupplier()
										.getEpNo(), null);

				if (smSapVendorCode != null) {
					getManagedBean().setSapVendorCode(
							smSapVendorCode.getSapVendorCode());
				}
			}

		} else if (SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
				.equalsIgnoreCase(getManagedBean().getAccountType())) {

			if (getManagedBean().getSmAppl().getSmCompanyBasic() == null) {
				getManagedBean().getSmAppl().setSmCompanyBasic(
						new SmCompanyBasic());
			} else {
				SmCompanyBasic activeCompBasic = getCompanyBasicByRevNo();
				getManagedBean().getSmAppl().setSmCompanyBasic(activeCompBasic);
			}

			IVendorCodeSO vendorCodeSO = (IVendorCodeSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IVendorCodeSO.class);

			SmSapVendorCode smSapVendorCode = vendorCodeSO
					.retrieveSapVendorCodeByEpNoAndBranchCode(getManagedBean()
							.getSmAppl().getSmSupplier().getEpNo(), null);

			if (smSapVendorCode != null) {
				getManagedBean().setSapVendorCode(
						smSapVendorCode.getSapVendorCode());
			}


			String agencyTypeName = getAgencyTypeName(getManagedBean()
					.getSmAppl().getSmSupplier());
			getManagedBean().setAgencyTypeName(agencyTypeName);

		}

	}

	private void setAddresses(SmPersonnel smPersonnel) {
		List<SmPersonnelAddress> smPersonnelAddressList = smPersonnel
				.getSmPersonnelAddresses();
		if (smPersonnelAddressList != null && !smPersonnelAddressList.isEmpty()) {
			log.info("Find address");
			for (SmPersonnelAddress smPersonnelAddress : smPersonnelAddressList) {
				log.info("Filter address");
				if (SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM
						.equalsIgnoreCase(smPersonnelAddress.getAddressType())) {
					getManagedBean().setAddressAsInIc(smPersonnelAddress);
				} else if (SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL
						.equalsIgnoreCase(smPersonnelAddress.getAddressType())) {
					getManagedBean().setCorrespondenceAddress(
							smPersonnelAddress);
				}
			}
		}
	}
	
	private String epRoleName(String roleCode) {

		IRoleSO roleSO = (IRoleSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IRoleSO.class);

		RoleReqDTO reqDTO = new RoleReqDTO();
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		reqDTO.setRoleCode(roleCode);
		RoleResDTO resDTO = roleSO.retrieveRoleDescByRoleCode(reqDTO);
		PmRoleDesc pmRoleDesc = resDTO.getPmRoleDesc();
		if (pmRoleDesc != null) {
			return pmRoleDesc.getRoleName();
		}

		return null;
	}

	private String getAgencyTypeName(SmSupplier smSupplier) {
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setSmSupplier(smSupplier);
		supplierSearchReqDTO.setLangCode(new FacesScopes().getUserLocale()
				.getLanguage());
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		SupplierSearchResDTO smResDTO = supplierSearchSO
				.getG2GSupplierAgencyType(supplierSearchReqDTO);

		return smResDTO.getAgencyType();
	}

	public void initSecondTabInfo() {
		if (SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
				.equalsIgnoreCase(getManagedBean().getSmAppl()
						.getSupplierType())
				|| SupplierManagementConst.SUPP_TYPE_BASIC
						.equalsIgnoreCase(getManagedBean().getSmAppl()
								.getSupplierType())) {
			if (SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL
					.equalsIgnoreCase(getManagedBean().getSmAppl()
							.getSmSupplier().getBusinessType())) {				
				getManagedBean().setCatalogueInfoItemStatus(CmConst.ITEM_STATUS_PENDING_APPROVAL);
				getManagedBean().getSupplierItemSearchDataModel().reloadData();
				setTotalSupplierItemForEachStatus();
				getManagedBean().setCatalogueInfoTabActiveIndex(0);
				getManagedBean().setRedirectToSupplierItemPage(false);
				retrieveSupplierCatalogueRequestBySupplierId(getManagedBean().getSmSupplier().getSupplierId());
			} else {
				setPersonnelInfo();				
			}

		}
	}

	/**
	 * Assign selected share holder
	 * 
	 * @param SmPersonnel
	 *            shareHolderBean
	 */
	public void assignSelectedPersonnel(SmPersonnel selectedPersonnel) {

		//25792 - start - reopen
		setAddresses(selectedPersonnel);
		//25792 - end - reopen
		getManagedBean().setSelectedPersonnel(selectedPersonnel);
		getManagedBean().setRedirectToPersonPage(true);
	}

	public void assignSelectedSupplierItem(CmSupplierItem selectedSupplierItem) {

		getManagedBean().setSelectedSupplierItem(selectedSupplierItem);
		getManagedBean().setRedirectToSupplierItemPage(true);
		getManagedBean().setSupplierItemDetailTabActiveIndex(0);
		getManagedBean().setPricingTypeGroupTabActiveIndex(0);
		getManagedBean().setPricingTypeTabActiveIndex(0);
		setSupplierZoneList();
	}
	
	private SmSoftcertRequest getSoftcertRequest(Long userId) {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		reqDTO.setUserId(userId);
		PersonnelResDTO resDTO = getPersonnelSO()
				.retrievePersonnelSoftcertRequestBySupplierIdAndUserId(reqDTO);
		return resDTO.getSoftcertRequest();
	}

	public void initThirdTabInfo() {
		
		if((SupplierManagementConst.SUPP_TYPE_BASIC
				.equalsIgnoreCase(getManagedBean().getSmAppl()
						.getSupplierType()) && !SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL
						.equalsIgnoreCase(getManagedBean().getSmSupplier()
								.getBusinessType())) || SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
								.equalsIgnoreCase(getManagedBean().getSmAppl()
										.getSupplierType())) {
			
			SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			SupplierSearchResDTO resDTO = getSupplierSearchSO().retrieveSmSupplierBankByAppl(reqDTO);
			List<SmSupplierBank> smSupplierBankList = resDTO.getSmSupplierBankList();
			getManagedBean().setSmSupplierBankList(smSupplierBankList);
			removeDeletedFromList(getManagedBean().getSmSupplierBankList());
		}	
	}
	
	public void initFourthTabInfo() {
		log.info("Fourth Tab Info");
		if(SupplierManagementConst.SUPP_TYPE_BASIC
				.equalsIgnoreCase(getManagedBean().getSmAppl()
						.getSupplierType()) && !SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL
						.equalsIgnoreCase(getManagedBean().getSmSupplier()
								.getBusinessType())) {
			log.info("Nature of Business");
			SupplierBusinessNatureReqDTO reqDTO = new SupplierBusinessNatureReqDTO();
			reqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());		
			SupplierBusinessNatureResDTO resDTO = getSupplierBusinessSO().retrieveAllBusinessNatureBySupplierId(reqDTO);
			List<SmSupplierBusiness> smSupplierBusinessList = resDTO.getBusinessNatureLs();
			getManagedBean().getSmSupplier().setSmSupplierBusinesses(smSupplierBusinessList);
		} else if(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
				.equalsIgnoreCase(getManagedBean().getSmAppl()
						.getSupplierType())) {
			
			log.info("Catalogue Information");
			getManagedBean().setCatalogueInfoItemStatus(CmConst.ITEM_STATUS_PENDING_APPROVAL);
			getManagedBean().getSupplierItemSearchDataModel().reloadData();
			setTotalSupplierItemForEachStatus();
			getManagedBean().setCatalogueInfoTabActiveIndex(0);
			getManagedBean().setRedirectToSupplierItemPage(false);
			retrieveSupplierCatalogueRequestBySupplierId(getManagedBean().getSmSupplier().getSupplierId());
		}
		
	}
	
	public void initFifthTabInfo() {
		log.info("Fifth Tab Info");
		log.info("Catalogue Information");
		getManagedBean().setCatalogueInfoItemStatus(CmConst.ITEM_STATUS_PENDING_APPROVAL);
		getManagedBean().getSupplierItemSearchDataModel().reloadData();
		setTotalSupplierItemForEachStatus();
		getManagedBean().setCatalogueInfoTabActiveIndex(0);
		getManagedBean().setRedirectToSupplierItemPage(false);
		retrieveSupplierCatalogueRequestBySupplierId(getManagedBean().getSmSupplier().getSupplierId());
	}
	
	private void retrieveSupplierCatalogueRequestBySupplierId(Long supplierId) {
		RequestCodeReqDTO reqDTO = new RequestCodeReqDTO();
		reqDTO.setSupplierId(supplierId);

		IRequestCodeSO reqSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
						IRequestCodeSO.class);
		RequestCodeResDTO resDTO = reqSO
				.retrieveRequestCodeBySupplierId(reqDTO);

		List<CmRequestCode> requestCodeList = resDTO.getRequestLs();
		if(requestCodeList != null) {
			String languageCode = new FacesScopes().getUserLocale().getLanguage();
			for(CmRequestCode cmRequestCode:requestCodeList) {
				if(cmRequestCode.getCmWorkflowStatus() != null) {
					
					List<PmStatusDesc> pmStatusDescList = cmRequestCode.getCmWorkflowStatus().getPmStatus().getPmStatusDescs();

					for(PmStatusDesc pmStatusDesc: pmStatusDescList) {
						if(languageCode.equalsIgnoreCase(pmStatusDesc.getLanguageCode())) {
							cmRequestCode.getCmWorkflowStatus().getPmStatus().getPmStatusDescs().clear();
							cmRequestCode.getCmWorkflowStatus().getPmStatus().getPmStatusDescs().add(pmStatusDesc);
							break;
						}
					}
				}
			}
		}
		getManagedBean().setRequestCodeList(requestCodeList);
	}
	
	public void redirectToCatalogueInfoPage() {
		getManagedBean().setRedirectToSupplierItemPage(false);
		RequestContext.getCurrentInstance().reset(":form:tt:itemDetailPanelId");
	}
	
	private void setSupplierZoneList() {
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;
		
		reqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());

		resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);
		
		getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());
	}
	
	private void setTotalSupplierItemForEachStatus() {
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;
		
		int size = 0;
		
		reqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		reqDTO.setItemStatus(CmConst.ITEM_STATUS_PENDING_APPROVAL);
		resDTO = catalogueSO.getNumOfSuppItemByStatusAndSuppId(reqDTO);
		size = Integer.parseInt(String.valueOf(resDTO.getNumberOfSupplierItems()));
		getManagedBean().setPendingApprovalSupplierItemSize(size);
		
		reqDTO.setItemStatus(CmConst.ITEM_STATUS_APPROVED);
		resDTO = catalogueSO.getNumOfSuppItemByStatusAndSuppId(reqDTO);
		size = Integer.parseInt(String.valueOf(resDTO.getNumberOfSupplierItems()));
		getManagedBean().setApprovedSupplierItemSize(size);
		
		reqDTO.setItemStatus(CmConst.ITEM_STATUS_REJECTED);
		resDTO = catalogueSO.getNumOfSuppItemByStatusAndSuppId(reqDTO);
		size = Integer.parseInt(String.valueOf(resDTO.getNumberOfSupplierItems()));
		getManagedBean().setRejectedSupplierItemSize(size);
		
		reqDTO.setItemStatus(CmConst.ITEM_STATUS_PUBLISHED);
		resDTO = catalogueSO.getNumOfSuppItemByStatusAndSuppId(reqDTO);
		size = Integer.parseInt(String.valueOf(resDTO.getNumberOfSupplierItems()));
		getManagedBean().setPublishedSupplierItemSize(size);
		
		reqDTO.setItemStatus(CmConst.ITEM_STATUS_EXPIRED);
		resDTO = catalogueSO.getNumOfSuppItemByStatusAndSuppId(reqDTO);
		size = Integer.parseInt(String.valueOf(resDTO.getNumberOfSupplierItems()));
		getManagedBean().setExpiredSupplierItemSize(size);
	}
	
	private void setPersonnelInfo() {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
		PersonnelResDTO resDTO = getPersonnelSO()
				.retrievePersonnelListByApplId(reqDTO);
		getManagedBean().getSmAppl().setSmPersonnels(
				resDTO.getSmPersonnelList());
		List<SmPersonnel> deletedPersonnels = new ArrayList<>();//25792 - reopen

		if (getManagedBean().getSmAppl().getSmPersonnels() == null) {
			getManagedBean().getSmAppl().setSmPersonnels(
					new ArrayList<SmPersonnel>());
		} else {
			for (SmPersonnel smPersonnel : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (smPersonnel.getPmUser() != null) {
					SmSoftcertRequest smSoftcertReq = getSoftcertRequest(smPersonnel
							.getPmUser().getUserId());
					List<SmSoftcertRequest> smSoftcerRequests = new ArrayList<>();
					if (smSoftcertReq != null) {						
						smSoftcerRequests.add(smSoftcertReq);						
					}
					smPersonnel.getPmUser().setSmSoftcertRequests(
							smSoftcerRequests);
				}
				List<PmParameterDesc> pmParameterDescList = smPersonnel
						.getIdentificationType().getPmParameterDescs();

				for (PmParameterDesc pmParameterDesc : pmParameterDescList) {
					FacesScopes facesScopes = new FacesScopes();

					if (!facesScopes
							.getUserLocale()
							.getLanguage()
							.equalsIgnoreCase(
									pmParameterDesc.getLanguageCode())) {
						smPersonnel.getIdentificationType()
								.getPmParameterDescs()
								.remove(pmParameterDesc);
						break;
					}
				}
				//25792 - reopen (modified if)
				if (smPersonnel.getRecordStatusFlg().intValue() != DbValueConst.RECORD_STATUS_ACTIVE
						&& smPersonnel.getRecordStatusFlg().intValue() != DbValueConst.RECORD_STATUS_PENDING_ACTIVATION) {
					deletedPersonnels.add(smPersonnel);
				}

				if (smPersonnel.getRoleName() != null
						&& StringUtils.isNotBlank(smPersonnel
								.getRoleName())) {
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
					smPersonnel
							.setRoleName(smPersonnel
									.getRoleName()
									.replaceAll(
											"Staff",
											SmCommonUtil
													.getMessage(
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
					smPersonnel
							.setRoleName(smPersonnel
									.getRoleName()
									.replaceAll(
											"Management",
											SmCommonUtil
													.getMessage(
															"lbl.sm.personnel.comprole.mngmnt",
															null)));
				}
				if (getManagedBean().getSmSupplier() != null
						&& SmCommonUtil.isSoftcertReadyForRenewal(
								getManagedBean().getSmSupplier()
										.getSupplierId(), smPersonnel)) {
					log.info("Ready for renewal");
					smPersonnel
							.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_READY_FOR_RENEWAL);
				}
				if (getManagedBean().getSmSupplier() != null) {
					SmCommonUtil.setRejectedPersonnelSoftcert(
							smPersonnel, getManagedBean()
									.getSmSupplier().getSupplierId());
				}
			}
			getManagedBean().getSmAppl().setSmPersonnels(
					getPersonnelByRevNo());
			//25792 - start - reopen
			if(SupplierManagementConst.SUPP_TYPE_BASIC.equalsIgnoreCase(getManagedBean().getSmAppl().getSupplierType()) 
					&& !SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL
					.equalsIgnoreCase(getManagedBean().getSmAppl().getSmSupplier().getBusinessType())) {
				SmApplicationUtil.sortPersonnelByRoleInCompany(getManagedBean()
						.getSmAppl());
			}
			List<SmPersonnel> smPersonnelList = getManagedBean().getSmAppl().getSmPersonnels();
			if((SupplierManagementConst.SUPP_TYPE_BASIC
					.equalsIgnoreCase(getManagedBean().getSmAppl()
							.getSupplierType()) && !SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL
							.equalsIgnoreCase(getManagedBean().getSmSupplier()
									.getBusinessType())) || SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
									.equalsIgnoreCase(getManagedBean().getSmAppl()
											.getSupplierType())) {			
				if(smPersonnelList != null && !smPersonnelList.isEmpty()) {
					List<SmPersonnel> newSmPersonnelList = new ArrayList<>();
					List<SmPersonnel> userSmPersonnelList = new ArrayList<>();
					List<SmPersonnel> catalogueUserList = new ArrayList<>();
					if(SupplierManagementConst.SUPP_TYPE_BASIC
							.equalsIgnoreCase(getManagedBean().getSmAppl()
									.getSupplierType()) && !SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL
									.equalsIgnoreCase(getManagedBean().getSmSupplier()
											.getBusinessType())) {
						for(SmPersonnel smPersonnel: smPersonnelList) {
							String epRoleName = epRoleName(smPersonnel.getEpRole());
							if("BASIC_SUPPLIER_ADMIN".equalsIgnoreCase(smPersonnel.getEpRole())) {
								smPersonnel.setEpRole(epRoleName);
								newSmPersonnelList.add(smPersonnel);
							} else if("BASIC_SUPPLIER_USER".equalsIgnoreCase(smPersonnel.getEpRole())) {
								smPersonnel.setEpRole(epRoleName);
								userSmPersonnelList.add(smPersonnel);
							} else if("CM_USER".equalsIgnoreCase(smPersonnel.getEpRole())) {
								smPersonnel.setEpRole(epRoleName);
								catalogueUserList.add(smPersonnel);
							}
						}
					} else if(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
							.equalsIgnoreCase(getManagedBean().getSmAppl()
									.getSupplierType())) {
						for(SmPersonnel smPersonnel: smPersonnelList) {
							String epRoleName = epRoleName(smPersonnel.getEpRole());
							if("G2G_ADMIN".equalsIgnoreCase(smPersonnel.getEpRole())) {
								smPersonnel.setEpRole(epRoleName);
								newSmPersonnelList.add(smPersonnel);
							} else if("G2G_USER".equalsIgnoreCase(smPersonnel.getEpRole())) {
								smPersonnel.setEpRole(epRoleName);
								userSmPersonnelList.add(smPersonnel);
							} else if("CM_USER".equalsIgnoreCase(smPersonnel.getEpRole())) {
								smPersonnel.setEpRole(epRoleName);
								catalogueUserList.add(smPersonnel);
							}
						}
					}			
					if(!userSmPersonnelList.isEmpty()) {
						newSmPersonnelList.addAll(userSmPersonnelList);
					}		
					if(!catalogueUserList.isEmpty()) {
						newSmPersonnelList.addAll(catalogueUserList);
					}
					getManagedBean().getSmAppl().setSmPersonnels(
							newSmPersonnelList);
				}
			} else if(SupplierManagementConst.SUPP_TYPE_BASIC.equalsIgnoreCase(getManagedBean().getSmAppl().getSupplierType()) 
					&& SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL
					.equalsIgnoreCase(getManagedBean().getSmAppl().getSmSupplier().getBusinessType())) {
				if(smPersonnelList != null && !smPersonnelList.isEmpty()) {
					String epRoleName = epRoleName(smPersonnelList.get(0).getEpRole());
					smPersonnelList.get(0).setEpRole(epRoleName);
					setAddresses(smPersonnelList.get(0));
				}
				
			}
			//25792 - end - reopen
		}		
		getManagedBean().getSmAppl().getSmPersonnels()
				.removeAll(deletedPersonnels);
	}
	// 25792 - end
	
	//28281 - start
	public static List<DisciplinaryActionVO> getDisciplinaryActionVO(SmSupplier currSupp) {
		List<DisciplinaryActionVO> voList = new ArrayList<DisciplinaryActionVO>();
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		//26238 -  Disciplonary action - 28146 - start
		BlacklistDirectorReqDTO reqDTO = new BlacklistDirectorReqDTO();
		BlacklistDirectorResDTO resDTO = new BlacklistDirectorResDTO();
		
		perReqDTO.setSmSupplierId(currSupp.getSupplierId());
		perReqDTO.setRecordStatus(null);
		
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelBySupplierIdAndRecordStatus(perReqDTO);
		//26238 -  Disciplonary action - 28146 - end
		
		List<SmDisciplinaryAction> resultList = SmApplicationUtil.retrieveSmDisciplinaryActionBySupplier(currSupp);
		if (resultList != null
				&& resultList.size() > 0) {
			for(SmDisciplinaryAction disciplinaryAction: resultList){
				if(disciplinaryAction.isWarning() || disciplinaryAction.isCancelCategory() 
						|| disciplinaryAction.isSuspendRegistration() || disciplinaryAction.isCancelRegistration() 
						|| disciplinaryAction.isBlacklistOwner() || disciplinaryAction.isManufacturerCancel()
						|| disciplinaryAction.isBumiputeraCancel()){
					DisciplinaryActionVO retVal = new DisciplinaryActionVO();
					if(disciplinaryAction.getStartDate() != null)
						retVal.setStartDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getStartDate()));
					if(disciplinaryAction.getEndDate() != null)
						retVal.setEndDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getEndDate()));
					
					//28198: Disciplinary Action - start - 26238
					List<DisciplinaryActionVO> remList = new ArrayList<DisciplinaryActionVO>();
					
					List<SmDisciplinaryActionRemarks> remarksList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionRemarksByDA(disciplinaryAction);
					
					if(remarksList != null && remarksList.size() > 0){
						for (SmDisciplinaryActionRemarks remarks : remarksList) {
							DisciplinaryActionVO rem1 = new DisciplinaryActionVO();
							rem1.setRemarks(remarks.getRemark());
							remList.add(rem1);
						}
					}
					DisciplinaryActionVO rem = new DisciplinaryActionVO();
					rem.setRemarks(disciplinaryAction.getRemark());
					remList.add(rem);
					retVal.setRemarksList(remList);
					//28198: Disciplinary Action - end - 26238
					
					if(disciplinaryAction.isWarning())
						retVal.setWarning(true);
					if(disciplinaryAction.isCancelCategory())
						retVal.setCancelCategory(true);
					if(disciplinaryAction.isSuspendRegistration())
						retVal.setSuspendRegistration(true);
					if(disciplinaryAction.isCancelRegistration())
						retVal.setCancelRegistration(true);
					if(disciplinaryAction.isBumiputeraCancel())
						retVal.setBumiputeraCancel(true);
					if(disciplinaryAction.isManufacturerCancel())
						retVal.setManufacturerCancel(true);
					if(disciplinaryAction.isBlacklistOwner())
						retVal.setBlacklistOwnerDirector(true);
					
					/*
					 * Bug #32560 ADDED INDICATOR RECORD STATUS, TO SHOW WHICH DA IS ACTIVE 
					 */
					retVal.setActiveRecordStatus(disciplinaryAction.isRecordStatus());
					
					//28281 (bumi cat code) start
					if(disciplinaryAction.isCancelCategory() || disciplinaryAction.isManufacturerCancel()
							|| disciplinaryAction.isBumiputeraCancel()){
						List<DisciplinaryActionCategoryVO> catVoList = new ArrayList<DisciplinaryActionCategoryVO>();
						//28281
						List<Object[]> catList = SmApplicationUtil.retrieveCancelCategoryByDisciplinaryActionId(disciplinaryAction.getDisciplinaryActionId());
						
						if(!EntityHelper.isCollectionEmpty(catList)
								&& catList.size() > 0){
							List<String> catCodeList = new ArrayList<String>();
							for(Object[] arr : catList){
								if(disciplinaryAction.getDisciplinaryActionId().toString().equalsIgnoreCase(arr[7].toString())){
									DisciplinaryActionCategoryVO catVo = new DisciplinaryActionCategoryVO();
									String catCode = StringUtils.EMPTY;
									catCode = arr[3].toString() + arr[4].toString() + arr[5].toString() ;
									catVo.setCategoryCode(catCode);
									if(!catCodeList.contains(catCode)){
										catVoList.add(catVo);
										catCodeList.add(catCode);
									}
									//28281 (bumi cat code) end
								}
							}						
							retVal.setCategoryCodeList(catVoList);
						}
					}
					
					if(disciplinaryAction.isSuspendRegistration()){
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();					
						if(personnelResDTO != null 
								&& personnelResDTO.getSmPersonnelList().size() > 0){
							for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
								BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
								
								//29078 : Disciplinary Action - 26238 - start
								reqDTO.setDirectorIdNo(personnel.getIdentificationNo());
								resDTO = SmServiceLookup.getBlacklistDirectorSO().retrieveAllPersonBlacklistByIdentificationNO(reqDTO);//29078 : Disciplinary Action - <26238>
								if(resDTO != null && resDTO.getPersonBlacklist() != null){
									if (personnel.isDirector() || personnel.isEquityOwner()){
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
									}
									blacklistownerDirectorList.add(blacklistOwner);
								}
								//29078 : Disciplinary Action - 26238 - end
							}
							retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
						}						
					}
					
					if(disciplinaryAction.isCancelRegistration() 
							&& !disciplinaryAction.isBlacklistOwner()){
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
						if(personnelResDTO != null 
								&& personnelResDTO.getSmPersonnelList().size() > 0){
							for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
								BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
								
								//29078 : Disciplinary Action - 26238 - start
								reqDTO.setDirectorIdNo(personnel.getIdentificationNo());
								resDTO = SmServiceLookup.getBlacklistDirectorSO().retrieveAllPersonBlacklistByIdentificationNO(reqDTO);//29078 : Disciplinary Action - <26238>
								if(resDTO != null && resDTO.getPersonBlacklist() != null){
									if (personnel.isDirector() || personnel.isEquityOwner()){
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
									}
									blacklistownerDirectorList.add(blacklistOwner);
								}
								//29078 : Disciplinary Action - 26238 - end
							}
							retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
						}						
					}
					
					if(disciplinaryAction.isBlacklistOwner()){
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
						if(personnelResDTO != null 
								&& personnelResDTO.getSmPersonnelList().size() > 0){
							for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
								BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
								//26238 -  Disciplonary action - 28146 - start
								reqDTO.setDirectorIdNo(personnel.getIdentificationNo());
								// Get blacklist by identification no
								resDTO = SmServiceLookup.getBlacklistDirectorSO()
										.retrieveAllPersonBlacklistByIdentificationNO(reqDTO);//29078 : Disciplinary Action - <26238>
								if(resDTO != null && resDTO.getPersonBlacklist() != null){
									if (personnel.isDirector() || personnel.isEquityOwner()){
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
									}
									blacklistownerDirectorList.add(blacklistOwner);
								}
								//26238 -  Disciplonary action - 28146 - end
							}
							retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
						}
					}
					
					if(disciplinaryAction.getMofNo() != null){
						retVal.setMofNoExist(true);
					}
					voList.add(retVal);
				}
				log.info(voList);					
				}				
		}
		return voList;
	}
	
	
	public void retrieveActiveDisciplinaryAction(SmSupplier supplier){
		List<SmDisciplinaryAction> discList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(supplier);
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		perReqDTO.setSmSupplierId(supplier.getSupplierId());
		
		//27915: Disciplinary Action - start - 26238
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelDABySupplierId(perReqDTO);
		//27915: Disciplinary Action - end - 26238
		
		if(discList != null && discList.size() > 0){
			for(SmDisciplinaryAction disc : discList){
				if(disc.isWarning() || disc.isCancelCategory() || disc.isSuspendRegistration() 
						|| disc.isCancelRegistration() || disc.isBlacklistOwner()
						|| disc.isManufacturerCancel() || disc.isBumiputeraCancel()){
					if(disc.isWarning())
						this.getManagedBean().setWarning(true);	
					if(disc.isCancelCategory())
						this.getManagedBean().setCancelCategoryCode(true);
					if(disc.isSuspendRegistration())
						this.getManagedBean().setSuspendRegistration(true);
					if(disc.isCancelRegistration())
						this.getManagedBean().setCancelRegistration(true);
					if(disc.isBlacklistOwner())
						this.getManagedBean().setBlacklistOwnerOrDirector(true);
					if(disc.isBumiputeraCancel())
						this.getManagedBean().setBumiputeraStatusCancel(true);
					if(disc.isManufacturerCancel())
						this.getManagedBean().setManufacturerStatusCancel(true);
					
					if(!disc.isWarning()){
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
						if(personnelResDTO != null 
								&& personnelResDTO.getSmPersonnelList().size() > 0){
							for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
								BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
								if(personnel.isCancelRegistration()
										|| personnel.isSuspendRegistration()
											|| personnel.isBlacklisted()){
									if (personnel.isDirector() || personnel.isEquityOwner()){
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
									}
									blacklistownerDirectorList.add(blacklistOwner);
								}
							}
							this.getManagedBean().setBlacklistownerDirectorList(blacklistownerDirectorList);
						}
					}
					
					if(disc.isCancelCategory() 
						|| disc.isManufacturerCancel() 
							|| disc.isBumiputeraCancel()){
						List<DisciplinaryActionCategoryVO> catVoList = new ArrayList<DisciplinaryActionCategoryVO>();
						//32660 - start
						List<Object[]> catList = SmApplicationUtil.retrieveCancelCategoryByDisciplinaryActionId(disc.getDisciplinaryActionId());
						//32660 - end
						
						if(catList != null
								&& catList.size() > 0){
							for(Object[] arr : catList){
								DisciplinaryActionCategoryVO catVo = new DisciplinaryActionCategoryVO();
								String catCode = StringUtils.EMPTY;
								catCode = arr[3].toString() + arr[4].toString() + arr[5].toString() ;
								catVo.setCategoryCode(catCode);	
								catVoList.add(catVo);
							}	
							
							this.getManagedBean().setCategoryCodeList(catVoList);
						}
					}
					this.getManagedBean().setStartDate(DateUtils.dateToString("dd/MM/yyyy",disc.getStartDate()));
					this.getManagedBean().setEndDate(DateUtils.dateToString("dd/MM/yyyy",disc.getEndDate()));
					//28198: Disciplinary Action - start - 26238
					List<SmDisciplinaryActionRemarks> remarksList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionRemarksByDA(disc);
					
					if(remarksList != null && remarksList.size() > 0){
						for (SmDisciplinaryActionRemarks remarks : remarksList) {
							this.getManagedBean().setRemarks(remarks.getRemark());
							break;
						}
					}else{
						this.getManagedBean().setRemarks(disc.getRemark());
					}
					//28198: Disciplinary Action - end - 26238
				}
			}
		} else {
			this.getManagedBean().setNoDisciplinary(true);
		}
	}
	//28281 - end
	// #29617 start
	public void setSmeSupplierInfo(SmSupplier supplier) {
		SupplierSmeReqDTO reqDTO = new SupplierSmeReqDTO();
		SmSmeSupplier smeSupplier = new SmSmeSupplier();
		reqDTO.setSmSupplier(supplier);
		ISupplierSmeSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
				ISupplierSmeSO.class);
		smeSupplier = reqSO.retrieveCurrentSmeInfo(reqDTO);
		getManagedBean().setSmeSupplier(smeSupplier);
	}
	// #29617 end
	//#12483 - START
	public void sortApplicationRevises(){
		Collections.sort(this.getManagedBean().getSmAppl().getSmApplRevises(), new Comparator<SmApplRevise>(){
			@Override
			public int compare(SmApplRevise revise1, SmApplRevise revise2) {
				return Integer.valueOf(revise1.getCreatedDate().compareTo(revise2.getCreatedDate()));
			}
		});
	}
	//#12483 - END
	
	//START 31111
			public void onViewOverlapCategory(SmAppl smAppl) {
				SmBulkApplicationVO vo = new SmBulkApplicationVO();
				SmOverlapCategoryVO overlapCategoryVO = getOverlapCategoryVO(smAppl);
				if (overlapCategoryVO != null) {
					vo.setOverlappedCategory(overlapCategoryVO
							.isOverLappedCategoryExist());
					vo.setOverlapMsg(overlapCategoryVO.getOverLappedCategoryMsg());
					vo.setOverlapCategoryVOList(overlapCategoryVO
							.getOverlapCategoryVOList());
				}
				getManagedBean()
						.setOverlapCategoryVOList(vo.getOverlapCategoryVOList());
				RequestContext.getCurrentInstance().execute(
						"overlapCategoryDialog.show();");
			}
			private SmOverlapCategoryVO getOverlapCategoryVO(SmAppl smAppl) {
				SmOverlapCategoryVO retVal = null;
				MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
				reqDTO.setSupplierId(smAppl.getSmSupplier().getSupplierId());
				reqDTO.setApplId(smAppl.getApplId());
				reqDTO.setLangCode(new FacesScopes().getUserLocale().getLanguage());
				List<Object[]> resultList = SmServiceLookup.getMofRegistrationSO()
						.retrieveOverLappedCategoryDetails(reqDTO).getResultList();
				if (!EntityHelper.isCollectionEmpty(resultList)
						&& resultList.size() > 0) {
					//#29659 - START
					List<Object[]> resultListNew = new ArrayList<>();
					for (Object[] arr : resultList) {
						
						Object[] object = new Object[arr.length + 1];
						for(int i=0; i<arr.length;i++){
							object[i]=arr[i];
						}
						String title = SmServiceLookup.getPersonnelSO().getSalutationNameByPersonnelId(Long.parseLong(arr[23] + ""));
						object[object.length - 1] = title;
						resultListNew.add(object);
					}
					
					retVal = new SmOverlapCategoryVO();
					retVal.setOverLappedCategoryExist(true);
					retVal.setOverLappedCategoryMsg(SmCommonUtil.getMessage(
							"lbl.sm.category.overlappingMsg", null) + "<br />");
					Locale loc = new Locale("ms", "MY");
					ResourceBundleUtils.getMessage("lbl.sm.category.overlappingMsg",
							null, ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc);
					retVal.setOverLappedCategoryRemarks(ResourceBundleUtils.getMessage(
							"lbl.sm.category.overlappingMsg", null,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc)
							+ System.lineSeparator() + System.lineSeparator());
					retVal.setOverlapCategoryVOList(getOverlapCategoryVOList(
							resultListNew, smAppl.getSupplierType()));
					//#29659 - END
				}
				return retVal;
			}
			
			private static List<SmBulkApplOverlapCatVO> getOverlapCategoryVOList(
					List<Object[]> resultList, String suppType) {
				List<SmBulkApplOverlapCatVO> retVal = new ArrayList<SmBulkApplOverlapCatVO>();

				if (!EntityHelper.isCollectionEmpty(resultList)) {
					SmBulkApplOverlapCatVO voTmp = null;
					SmBulkApplOverlapCatVO voSubTmp = null;
					SmBulkApplOverlapCatVO voSubRemove = null;
					String catCode = StringUtils.EMPTY;
					String isActiveAppl = StringUtils.EMPTY;
					String applStatus = StringUtils.EMPTY;
					String language = new FacesScopes().getUserLocale().getLanguage();
					boolean isAdd = false;
					boolean isSubAdd = false;
					for (Object[] arr : resultList) {
						catCode = arr[0] + "" + arr[1] + arr[2] + "";
						isActiveAppl = arr[20] + "";
						voTmp = (SmBulkApplOverlapCatVO) EntityHelper
								.getEntityBySinglePropertyFromList(catCode, retVal,
										"categoryCode");
						if (voTmp == null) {
							voTmp = new SmBulkApplOverlapCatVO();
							voTmp.setCategoryCode(catCode);
							voTmp.setCategoryName("(" + arr[17] + "/" + arr[18] + "/"
									+ arr[19] + ")");
							isAdd = true;
						}

						if (isActiveAppl.equals("1")) {
							SmWorkflowStatus workflow = SmCommonUtil
									.getCurrentActiveWokFlowStatus(Long
											.parseLong(arr[22] + ""));
							if (workflow != null
									&& workflow.getPmStatus() != null
									&& !EntityHelper.isCollectionEmpty(workflow
											.getPmStatus().getPmStatusDescs())) {
								for (PmStatusDesc desc : workflow.getPmStatus()
										.getPmStatusDescs()) {
									if (!StringUtils.isBlank(desc.getLanguageCode())
											&& desc.getLanguageCode().equals(language)) {
										applStatus = desc.getStatusName();
									}
								}
							}
						}

						if (EntityHelper.isCollectionEmpty(voTmp.getOverlapInfoList())) {
							voTmp.setOverlapInfoList(new ArrayList<SmBulkApplOverlapCatVO>());
						}

						voSubTmp = getOverlapVOByRegNoAndIdNo(arr[7] + "",
								arr[16] + "", voTmp.getOverlapInfoList());

						if (voSubTmp == null) {
							isSubAdd = true;
						} else {
							if (isActiveAppl.equals("1") && !voSubTmp.isActiveAppl()) {
								voSubRemove = voSubTmp;
								isSubAdd = true;
							}
						}

						if (isSubAdd) {
							voSubTmp = new SmBulkApplOverlapCatVO();
							voSubTmp.setActiveAppl(isActiveAppl.equals("1") ? true
									: false);
							voSubTmp.setPersonnelName(arr[3] + "");
							voSubTmp.setIdentificationNo(arr[16] + "");
							voSubTmp.setRole(getRoleName(arr[9] + "", arr[10] + "",
									arr[11] + "", arr[15] + "", arr[12] + "", arr[13]
											+ "", arr[14] + "", suppType));
							voSubTmp.setCompanyName(arr[4] + "");
							voSubTmp.setMofNo(arr[5] + "");
							if (StringUtils.isBlank(voSubTmp.getMofNo())) {
								voSubTmp.setMofNo(SmCommonUtil.getMessage("lbl.txt.na",
										null));
							}
							voSubTmp.setExpDate(arr[6] + "");
							if (StringUtils.isBlank(voSubTmp.getExpDate())) {
								voSubTmp.setExpDate(SmCommonUtil.getMessage(
										"lbl.txt.na", null));
							}
							voSubTmp.setRegNo(arr[7] + "");
							voSubTmp.setApplNo(arr[8] + "");
							voSubTmp.setApplStatus(StringUtils.isBlank(applStatus) ? arr[21]
									+ ""
									: applStatus);
							//#29659 - START 
							voSubTmp.setTitle(arr[24] + "");
							//#29659 - END
							voTmp.getOverlapInfoList().add(voSubTmp);
							isSubAdd = false;
						}

						if (voSubRemove != null) {
							voTmp.getOverlapInfoList().remove(voSubRemove);
						}

						if (isAdd) {
							retVal.add(voTmp);
							isAdd = false;
						}

					}
				}
				return retVal;
			}
			
			private static SmBulkApplOverlapCatVO getOverlapVOByRegNoAndIdNo(
					String regNo, String idNo, List<SmBulkApplOverlapCatVO> overlapLists) {
				SmBulkApplOverlapCatVO retVal = null;
				if (!EntityHelper.isCollectionEmpty(overlapLists)) {
					for (SmBulkApplOverlapCatVO vo : overlapLists) {
						if (vo.getRegNo() != null && regNo != null
								&& vo.getRegNo().equals(regNo)
								&& vo.getIdentificationNo() != null && idNo != null
								&& vo.getIdentificationNo().equals(idNo)) {
							retVal = vo;
							break;
						}
					}
				}
				return retVal;
			}
			
			private static String getRoleName(String equityOwner, String director,
					String higherManagement, String clerical, String management,
					String prof, String semiProf, String suppType) {
				StringBuilder sb = new StringBuilder();
				if (equityOwner.equals("1")) {
					sb.append(
							SmCommonUtil.getMessage(
									SupplierManagementConst.PERSONNEL_EQUI_OWNER, null))
							.append(",");
				}
				if (director.equals("1")) {
					sb.append(
							SmCommonUtil.getMessage(
									SupplierManagementConst.PERSONNEL_DIR_ROLENAME,
									null)).append(",");
				}
				if (higherManagement.equals("1")) {
					sb.append(
							SmCommonUtil.getMessage(
									SupplierManagementConst.PERSONNEL_HGHR_ROLENAME,
									null)).append(",");
				}
				if (management.equals("1")) {
					sb.append(
							SmCommonUtil
									.getMessage(
											SupplierManagementConst.PERSONNEL_MANAGEMENT_ROLENAME,
											null)).append(",");
				}
				if (clerical.equals("1")) {
					if (suppType.equals(SupplierManagementConst.SUPP_TYPE_CONSULTANT)) {
						sb.append(
								SmCommonUtil
										.getMessage(
												SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_ROLENAME,
												null)).append(",");
					} else {
						sb.append(
								SmCommonUtil
										.getMessage(
												SupplierManagementConst.PERSONNEL_STAFF_ROLENAME,
												null)).append(",");
					}
				}
				if (prof.equals("1")) {
					sb.append(
							SmCommonUtil
									.getMessage(
											SupplierManagementConst.PERSONNEL_PROF_STAFF_ROLENAME,
											null)).append(",");
				}
				if (semiProf.equals("1")) {
					sb.append(
							SmCommonUtil
									.getMessage(
											SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_ROLENAME,
											null)).append(",");
				}
				return sb.length() > 0 ? StringUtils.removeEnd(sb.toString(), ",")
						: StringUtils.EMPTY;
			}
	  //END 31111
			
	//31033 start
	public static List<DisciplinaryActionVO> getActiveDisciplinaryActionVO(SmSupplier currSupp) {
		List<DisciplinaryActionVO> voList = new ArrayList<DisciplinaryActionVO>();
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		// 26238 - Disciplonary action - 28146 - start
		BlacklistDirectorReqDTO reqDTO = new BlacklistDirectorReqDTO();
		BlacklistDirectorResDTO resDTO = new BlacklistDirectorResDTO();

		perReqDTO.setSmSupplierId(currSupp.getSupplierId());
		perReqDTO.setRecordStatus(null);

		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelBySupplierIdAndRecordStatus(perReqDTO);
		// 26238 - Disciplonary action - 28146 - end

		List<SmDisciplinaryAction> resultList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(currSupp);
		if (resultList != null && resultList.size() > 0) {
			for (SmDisciplinaryAction disciplinaryAction : resultList) {
				if (disciplinaryAction.isWarning() || disciplinaryAction.isCancelCategory()
						|| disciplinaryAction.isSuspendRegistration() || disciplinaryAction.isCancelRegistration()
						|| disciplinaryAction.isBlacklistOwner() || disciplinaryAction.isManufacturerCancel()
						|| disciplinaryAction.isBumiputeraCancel()) {
					DisciplinaryActionVO retVal = new DisciplinaryActionVO();
					if (disciplinaryAction.getStartDate() != null)
						retVal.setStartDate(DateUtils.dateToString("dd/MM/yyyy", disciplinaryAction.getStartDate()));
					if (disciplinaryAction.getEndDate() != null)
						retVal.setEndDate(DateUtils.dateToString("dd/MM/yyyy", disciplinaryAction.getEndDate()));

					// 28198: Disciplinary Action - start - 26238
					List<DisciplinaryActionVO> remList = new ArrayList<DisciplinaryActionVO>();

					List<SmDisciplinaryActionRemarks> remarksList = SmApplicationUtil
							.retrieveActiveSmDisciplinaryActionRemarksByDA(disciplinaryAction);

					if (remarksList != null && remarksList.size() > 0) {
						for (SmDisciplinaryActionRemarks remarks : remarksList) {
							DisciplinaryActionVO rem1 = new DisciplinaryActionVO();
							rem1.setRemarks(remarks.getRemark());
							remList.add(rem1);
						}
					}
					DisciplinaryActionVO rem = new DisciplinaryActionVO();
					rem.setRemarks(disciplinaryAction.getRemark());
					remList.add(rem);
					retVal.setRemarksList(remList);
					// 28198: Disciplinary Action - end - 26238

					if (disciplinaryAction.isWarning())
						retVal.setWarning(true);
					if (disciplinaryAction.isCancelCategory())
						retVal.setCancelCategory(true);
					if (disciplinaryAction.isSuspendRegistration())
						retVal.setSuspendRegistration(true);
					if (disciplinaryAction.isCancelRegistration())
						retVal.setCancelRegistration(true);
					if (disciplinaryAction.isBumiputeraCancel())
						retVal.setBumiputeraCancel(true);
					if (disciplinaryAction.isManufacturerCancel())
						retVal.setManufacturerCancel(true);
					if (disciplinaryAction.isBlacklistOwner())
						retVal.setBlacklistOwnerDirector(true);
					
					/*
					 * Bug #32560 ADDED INDICATOR RECORD STATUS, TO SHOW WHICH DA IS ACTIVE 
					 */
					retVal.setActiveRecordStatus(disciplinaryAction.isRecordStatus());

					// 28281 (bumi cat code) start
					if (disciplinaryAction.isCancelCategory() || disciplinaryAction.isManufacturerCancel()
							|| disciplinaryAction.isBumiputeraCancel()) {
						List<DisciplinaryActionCategoryVO> catVoList = new ArrayList<DisciplinaryActionCategoryVO>();
						// 28281
						List<Object[]> catList = SmApplicationUtil
								.retrieveCancelCategoryByDisciplinaryActionId(disciplinaryAction
										.getDisciplinaryActionId());

						if (!EntityHelper.isCollectionEmpty(catList) && catList.size() > 0) {
							List<String> catCodeList = new ArrayList<String>();
							for (Object[] arr : catList) {
								if (disciplinaryAction.getDisciplinaryActionId().toString()
										.equalsIgnoreCase(arr[7].toString())) {
									DisciplinaryActionCategoryVO catVo = new DisciplinaryActionCategoryVO();
									String catCode = StringUtils.EMPTY;
									catCode = arr[3].toString() + arr[4].toString() + arr[5].toString();
									catVo.setCategoryCode(catCode);
									if (!catCodeList.contains(catCode)) {
										catVoList.add(catVo);
										catCodeList.add(catCode);
									}
									// 28281 (bumi cat code) end
								}
							}
							retVal.setCategoryCodeList(catVoList);
						}
					}

					if (disciplinaryAction.isSuspendRegistration()) {
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
						if (personnelResDTO != null && personnelResDTO.getSmPersonnelList().size() > 0) {
							for (SmPersonnel personnel : personnelResDTO.getSmPersonnelList()) {
								BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();

								// 29078 : Disciplinary Action - 26238 - start
								reqDTO.setDirectorIdNo(personnel.getIdentificationNo());
								resDTO = SmServiceLookup.getBlacklistDirectorSO()
										.retrieveAllPersonBlacklistByIdentificationNO(reqDTO);// 29078
																								// :
																								// Disciplinary
																								// Action
																								// -
																								// <26238>
								if (resDTO != null && resDTO.getPersonBlacklist() != null) {
									if (personnel.isDirector() || personnel.isEquityOwner()) {
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
									}
									blacklistownerDirectorList.add(blacklistOwner);
								}
								// 29078 : Disciplinary Action - 26238 - end
							}
							retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
						}
					}

					if (disciplinaryAction.isCancelRegistration() && !disciplinaryAction.isBlacklistOwner()) {
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
						if (personnelResDTO != null && personnelResDTO.getSmPersonnelList().size() > 0) {
							for (SmPersonnel personnel : personnelResDTO.getSmPersonnelList()) {
								BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();

								// 29078 : Disciplinary Action - 26238 - start
								reqDTO.setDirectorIdNo(personnel.getIdentificationNo());
								resDTO = SmServiceLookup.getBlacklistDirectorSO()
										.retrieveAllPersonBlacklistByIdentificationNO(reqDTO);// 29078
																								// :
																								// Disciplinary
																								// Action
																								// -
																								// <26238>
								if (resDTO != null && resDTO.getPersonBlacklist() != null) {
									if (personnel.isDirector() || personnel.isEquityOwner()) {
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
									}
									blacklistownerDirectorList.add(blacklistOwner);
								}
								// 29078 : Disciplinary Action - 26238 - end
							}
							retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
						}
					}

					if (disciplinaryAction.isBlacklistOwner()) {
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
						if (personnelResDTO != null && personnelResDTO.getSmPersonnelList().size() > 0) {
							for (SmPersonnel personnel : personnelResDTO.getSmPersonnelList()) {
								BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
								// 26238 - Disciplonary action - 28146 - start
								reqDTO.setDirectorIdNo(personnel.getIdentificationNo());
								// Get blacklist by identification no
								resDTO = SmServiceLookup.getBlacklistDirectorSO()
										.retrieveAllPersonBlacklistByIdentificationNO(reqDTO);// 29078
																								// :
																								// Disciplinary
																								// Action
																								// -
																								// <26238>
								if (resDTO != null && resDTO.getPersonBlacklist() != null) {
									if (personnel.isDirector() || personnel.isEquityOwner()) {
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
									}
									blacklistownerDirectorList.add(blacklistOwner);
								}
								// 26238 - Disciplonary action - 28146 - end
							}
							retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
						}
					}

					if (disciplinaryAction.getMofNo() != null) {
						retVal.setMofNoExist(true);
					}
					voList.add(retVal);
				}
				log.info(voList);
			}
		}
		return voList;
	}
	//31033 end
	
	//3460 - Start
		/**
		 * To displaying the SDO remarks when viewing the applications
		 */
	private void initSDORemarkInfo() {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SDOR);
		reqDTO.setDocId(getManagedBean().getSmAppl().getApplId());

		SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(reqDTO);

		if (resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
			SmRemark remark = resDTO.getRemarksList().get(0);
			getManagedBean().setSdoRemark(remark.getRemark());
		}
	}
		//3460 - End
		
}
