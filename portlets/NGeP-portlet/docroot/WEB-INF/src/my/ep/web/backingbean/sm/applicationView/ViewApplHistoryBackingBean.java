package my.ep.web.backingbean.sm.applicationView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sm.supportingdocinquiry.ISupportingDocInquirySO;
import my.ep.biz.service.sm.vendorcode.IVendorCodeSO;
import my.ep.biz.service.sm.workFlowStatus.ISmWorkFlowStatusSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sm.SmTrackingDiaryConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.dto.sm.common.ssm.SsmReqDTO;
import my.ep.dto.sm.common.ssm.SsmResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.dto.sm.supportingdocinquiry.SupportingDocInquiryResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.persistence.entity.sm.SmApplInquiryDocFile;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmApplSupportDocInquiry;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSapVendorCode;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.backingbean.sm.commonappl.CommonApplBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.backingbean.sm.virtualcert.VirtualCertBackingBean;
import my.ep.web.backingbean.trackingdiary.PmTrackingDiaryBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.managedbean.sm.applicationView.ViewApplHistoryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.SmVirtualCertHelper;
import my.ep.web.vo.SmOverlapCategoryVO;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "viewApplHistoryBackingBean")
@RequestScoped
public class ViewApplHistoryBackingBean extends
		BaseReviewApplicationBackingBean<ViewApplHistoryManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 103650277810238429L;
	public static VirtualCertBackingBean virtualCertInstance;
	public static CommonApplBackingBean commonApplInstance;
	public static PmTrackingDiaryBackingBean trackinDiaryInstance;
	public static RegistrationBackingBean registrationBackingBean;
	public static SvoSiteVisitBackingBean svoSiteVisitBackingBean;
	//#11641 - Start
	private int mandatoryCounter = 1;
	private int nonMandatoryCounter = 1;
	private boolean mandatoryEmpty = false;
	private boolean nonMandatoryEmpty = false;
	private boolean inquiryResponse = true;
	//#11641 - End
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
		if (!StringUtils.isBlank(outputPageType)
				&& outputPageType
						.equals(my.ep.web.constant.sm.SupplierManagementConst.SM_APPL_PAGE_TYPE_TRACKING_DIARY)) {
			//START - #28870 
			getManagedBean().getTrackingDiary().setTrackingDiaryParam(
					SmTrackingDiaryConst.SM_DEFAULT_DOC_TYPE,
					getManagedBean().getSmAppl().getApplId(), getManagedBean().isSupplierUser() ? true : false);
			//END - #28870
			getManagedBean().getTrackingDiary().getManagedBean()
					.getSearchTrackingDiaryDataModel().reloadData();
		}

		// 25792 - start
		if (SupplierManagementConst.SUPP_TYPE_BASIC
				.equalsIgnoreCase(getManagedBean().getAccountType())
				|| SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
						.equalsIgnoreCase(getManagedBean().getAccountType())) {

			if (outputTab == 1) {
				log.info("2nd tab");
				initSecondTabInfo();
			} else if (outputTab == 2) {
				log.info("3rd tab");
				initThirdTabInfo();
			} else if (outputTab == 3) {
				log.info("4th tab");
				if ((SupplierManagementConst.SUPP_TYPE_BASIC
						.equalsIgnoreCase(getManagedBean().getAccountType())
						&& !SupplierManagementConst.BUSINESS_TYPE_IND
								.equalsIgnoreCase(getManagedBean()
										.getSmSupplier().getBusinessType())) || 
										SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT
										.equalsIgnoreCase(getManagedBean().getAccountType())) {
					initFourthTabInfo();
				}

			} else if (outputTab == 4) {
				log.info("5th tab");
				if (SupplierManagementConst.SUPP_TYPE_BASIC
						.equalsIgnoreCase(getManagedBean().getAccountType())
						&& !SupplierManagementConst.BUSINESS_TYPE_IND
								.equalsIgnoreCase(getManagedBean()
										.getSmSupplier().getBusinessType())) {
					initFifthTabInfo();
				}

			}
			
		}
		// 25792 - end
	}

	/**
	 * Pre-render for View Application History
	 */
	public void preRenderViewApplicationHistory() {
		renewSesionObject();
		initLogin();
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(true);
		getManagedBean().setRenderedBackButton(true);

		if (!getManagedBean().isSupplierUser()) {
			initLoginUserZone();
			String supplierIdParam = getSupplierIdParam();
			getManagedBean().setRegStatusDescList(
					retrieveRegistrationStatusList());
			getManagedBean().setZoneList(
					SmServiceLookup.getZoneSO().retrieveActiveZoneList()
							.getZoneList());
			if (StringUtils.isNotBlank(supplierIdParam)) {
				SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
				reqDTO.setSupplierId(Long.parseLong(supplierIdParam));
				SmSupplier paramSupplier = SmServiceLookup
						.getSupplierSearchSO().retriveSupplierById(reqDTO)
						.getSmSupplier();
				getManagedBean().setSearchSupplier(new SmSupplier());
				getManagedBean().getSearchSupplier().setRegNo(
						paramSupplier.getRegNo());
				if (paramSupplier.getSmAppl() != null) {
					getManagedBean().setSearchSuppType(
							paramSupplier.getSmAppl().getSupplierType());
					if (paramSupplier.getSmAppl().getSmCompanyBasic() != null
							&& !EntityHelper.isCollectionEmpty(paramSupplier
									.getSmAppl().getSmCompanyBasic()
									.getSmCompanyAddresses())) {
						SmAddress address = paramSupplier.getSmAppl()
								.getSmCompanyBasic().getSmCompanyAddresses()
								.get(0).getSmAddress();
						if (address != null && address.getPmState() != null) {
							PmZone zone = SmApplicationUtil
									.retrieveZoneByStateId(address.getPmState()
											.getStateId());
							if (zone != null) {
								getManagedBean().setSearchPmZoneId(
										zone.getZoneId());
							}
						}
					}
				}

				getManagedBean().searchSupplier();
			}
		}

		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/revisionSmApplView/viewApplicationHistory/searchSupplierApplicationHistoryPage.xhtml",
						"", null);
	}

	/**
	 * View revision application by approver for task that is not claimed
	 * 
	 * @param String
	 *            applNo
	 */
	public void viewUnClaimTask(String applNo) {
		getManagedBean().setViewApplicationHistory(true);
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setPoApprUnClaimTask(true); //#30766 & #34209, #32008
		// #28767 - START
		getManagedBean().setTabActiveIndex(0);
		// #28767 - END
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(applNo);
		SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl();
		setApplication(smAppl);
	}

	/**
	 * On click claim task button
	 */
	public void onClickClaimButton() {
		claimBpmTask();
		getCommonApplInstance().preRenderView();
	}
	
	public void onViewOverlapCategory() {
		RequestContext.getCurrentInstance().execute(
				"overlapCategoryDialog.show();");
	}

	/**
	 * Redirect to resubmit page
	 */
	public void redirectToResubmit() {
		
		// ##25969 - Blocked for resubmission if rejected application already
		// deactivated (record status=9)
		if (getManagedBean().getSmAppl().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED) {
			throw new EpException(SmCommonUtil.getMessage(
					"lbl.sm.resubmit.delete", null));
		}
		//#27250 - START SSM CHANGES FOR RESUBMIT
		resubmitSSM();
		//#27250 - END SSM CHANGES FOR RESUBMIT
		
		// #14832 - To cater if resubmit link already expired and supplier still
		// in progress (Supplier Temp), so will call alert popup that will
		// redirect to clone new application and start fresh
		//32627 START
		List<String> rejectedStatus = Arrays
				.asList(SupplierManagementConst.REJECTED_STATUSES);
		//32627 END
		if (getManagedBean().getSmAppl().getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_IN_PROGRESS) {
			if (SmApplicationUtil
					.isRejectedApplValidForResubmission(getManagedBean()
							.getSmAppl())) {
				redirectToReapplyNewAppl(true); //#29956
			} else {
				//START 27951
				if(!this.getManagedBean().isCheckRoleSSM()){
					// #29583 - start
					// checked whether supplier already registered(20199)
					MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
					reqDTO.setSupplierId(getManagedBean().getSmAppl().getSmSupplier().getSupplierId());
					if (SmServiceLookup.getMofRegistrationSO().alreadyMof(reqDTO)
							.isValid()) {
						throw new EpException(MessageConst.ERROR_722674);
					}
					// #29583 - end
					this.getManagedBean().getPersonnelsRole().clear();
					this.getManagedBean().setPreRegistration(true);
					RequestContext.getCurrentInstance().execute(
							"confirmDialog.show()");
				}else{
					redirectToReapplyNewAppl(false); //#29956
				}
				//END 27951
			}
		} else {			
			//#27232 - END SSM CHANGES FOR RESUBMIT
			if (SmApplicationUtil
					.isRejectedApplValidForResubmission(getManagedBean()
							.getSmAppl())) {
				redirectToReapplyNewAppl(true); //29956
			} else {
				//32627 START
				SmAppl currAppl = this.getManagedBean().getSmAppl();
				Boolean resubmit = true;
				if(rejectedStatus.contains(currAppl.getPmStatus().getStatusId().toString())){
					List<SmAppl> applList = this.getManagedBean().getSmSupplier().getSmAppls();
					for(SmAppl appl : applList){
						if(appl.getPmStatus() != null){ //32627
							if(currAppl.getCreatedDate().before(appl.getCreatedDate()) &&
									currAppl.getApplType().equals(appl.getApplType()) && 
									appl.getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STATUS_REGISTERED)){
								resubmit = false;
							}
						}
					}
				}
				if(!resubmit){
					throw new EpException(SmCommonUtil.getMessage(
							"lbl.sm.approvedApplicationCantProceed", null));
				}else{
					throw new EpException(SmCommonUtil.getMessage(
							"lbl.sm.resubmitExpired", null));
				}
				//32627 END
			}
		}
	}
	
	//start #29956
	public static boolean isMofAccEffDateBefore6Months(SmSupplier supplier) {
		boolean retVal = false;
		if (supplier != null && supplier.getSupplierId() != null) {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			reqDTO.setParamMonths(6);
			int count = SmServiceLookup
					.getMofRegistrationSO()
					.countMofAccEffDatePassedCurrDateByMonthsAndSupplierId(
							reqDTO).getCount();
			if (count > 0) {
				retVal = true;
			}
		}
		return retVal;
	}	
	//end #29956

	/**
	 * Cloning new application and redirect to New MOF application page
	 */
	public void redirectToReapplyNewAppl(Boolean resubmission) {
		SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
		//START 27951
		//ANY CHANGES HERE PLEASE CHANGES IN ViewApplHistoryBackingBean.redirectToReapplyNewAppl & viewOnlineAccount.RegistrationBackingBean & SmApplicationBackingBean.proceedToApplication
		boolean proceed = true;
		String baseUrl; //#36474
		smApplBB.getManagedBean().setPreRegistration(this.getManagedBean().isPreRegistration());
		smApplBB.getManagedBean().setPersonnelsRole(this.getManagedBean().getPersonnelsRole());
		this.getManagedBean().setPreRegistration(true);
		log.info("-------------------------------------------------------------------"+SmCommonUtil.getInstance().ssmActive());
		if(SmCommonUtil.getInstance().ssmActive() && businessTypeTovalidateWithSSM(getManagedBean().getSmAppl().getSmSupplier().getRegNo(),getManagedBean().getSmAppl().getSmSupplier().getBusinessType()) && this.getManagedBean().getSsmResDTO() != null && !this.getManagedBean().isCheckRoleSSM()){			
			List<SmPersonnel> personnelsRole = this.getManagedBean().getPersonnelsRole();
			//start #29956
			boolean displayPopupDowngradeSSMOwner = displayPopupDowngradeSSMOwner(this.getManagedBean().getSmAppl(), this.getManagedBean().getSsmResDTO());
			if(displayPopupDowngradeSSMOwner){
				proceed = false;
			}
			//end #29956
		}
		if(proceed){
			if(resubmission){
				SmCommonUtil.redirectToExternalUrl(getManagedBean()
						.getResubmitUrl());
			}else{
				baseUrl = "supptemp-mof-registration"; //#36474
				// #24923
				smApplBB.getManagedBean().setNewSupplierType(
						getManagedBean().getSmAppl().getSupplierType());
				smApplBB.proceedToApplication(ParameterConst.NEW_SM_APPLN_FOR_MOF);
		
				SmCommonUtil.redirectToExternalUrl(baseUrl); //#36474
				// Enhancement #20467 - point to new appl page with bullet
				String page = my.ep.web.constant.sm.SupplierManagementConst.APPLICATION_BASE_PAGE_BY_TYPE
						.get("RE");
				SmCommonUtil.redirectToPage(page, null, null, null);
			}			
		}		
		//END 27951
		
	}

	/**
	 * Get Application Status
	 * 
	 * @param PmStatus
	 *            status
	 * @return String
	 */
	private String getApplicationStatus(PmStatus status, SmAppl smAppl) {
		/*
		 * BigDecimal suppStatDec = appl[12] != null ? (BigDecimal) appl[12]
				: new BigDecimal(0);
		String applType = appl[1] != null ? (String) appl[1] : "";
		String suppType = appl[2] != null ? (String) appl[2] : "";

		Locale locale = new FacesScopes().getUserLocale();
		if (suppStatDec.toString().equals("5") && applType.equals("O")
				&& suppType.equals("B")) {
			return ResourceBundleUtils.getMessage("lbl.sm.in.progress",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		} else if (StringUtils.isNotBlank((String) appl[6])) {
			return (String) appl[6];
		}
		return ResourceBundleUtils.getMessage("lbl.sm.no.available",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		 * 
		 */
		Locale locale = new FacesScopes().getUserLocale();
		if(smAppl.getSmSupplier().getRecordStatusFlg() == 5 
				&& smAppl.getApplType().equalsIgnoreCase("O") 
				&& smAppl.getSupplierType().equalsIgnoreCase("B")) {
			return ResourceBundleUtils.getMessage("lbl.sm.in.progress",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		} else if(status != null) {
			String statusDesc = StringUtils.EMPTY;
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(status.getStatusId());
			reqDTO.setLanguageCode(locale.getLanguage());
			StatusResDTO resDTO = SmServiceLookup.getStatusSO()
					.retrieveStatusDescById(reqDTO);
			if (resDTO != null && resDTO.getPmStatusDesc() != null) {
				statusDesc = resDTO.getPmStatusDesc().getStatusName();
			}
			return statusDesc;
		}
		return ResourceBundleUtils.getMessage("lbl.sm.no.available",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		
	}

	private void initWorkflowStatus(SmAppl smAppl, String status) {
		List<String> rejectedStatus = Arrays
				.asList(SupplierManagementConst.REJECTED_STATUSES);
		SmWorkflowStatus sts = SmCommonUtil
				.getCurrentActiveWokFlowStatus(smAppl.getApplId());
		if (sts != null && sts.getPmStatus() != null) {
			smAppl.setWorkflowStatus(sts.getPmStatus());
			/*smAppl.setWorkflowStatusDesc(StringUtils.isBlank(status) ? getApplicationStatus(sts
					.getPmStatus(), smAppl) : status);*/
			//25792  - start
			smAppl.setWorkflowStatusDesc(StringUtils.isBlank(status) || "N/A".equalsIgnoreCase(status) ? getApplicationStatus(sts
					.getPmStatus(), smAppl) : status);
			//25792 - end

			if (rejectedStatus.contains(sts.getPmStatus().getStatusId()
					.toString())) {
				getManagedBean().setResubmitUrl(
						SmCommonUtil.getEpBaseUrl()
								+ "/resubmit-application?apId="
								+ smAppl.getApplId());
			}
		}
	}

	/**
	 * Perform search supplier application history
	 */
	public void search() {
		//25792 - start
		getManagedBean().setAccountType(getManagedBean().getSearchAccountType());
		//25792 - end
		validateCompanySearch();
		getManagedBean().searchSupplier();
	}

	/**
	 * Initialized application by application no
	 * 
	 * @param String
	 *            applNo
	 */
	public void initSmApplByApplNo(Long applId, String status) {
		getManagedBean().setResubmitUrl(StringUtils.EMPTY);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplId(applId);
		SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplicationDetailsById(reqDTO).getSmAppl();
		// 25792 - start
		log.info("Init Sm Appl");
		log.info("Appl Id: " + smAppl.getApplId());		
		log.info("Supplier type: " + smAppl.getSupplierType());
		getManagedBean().setTabActiveIndex(0);
		//#31248 - START
		getManagedBean().setDisplayResubmitButton(setResubmitButton(smAppl)) ; //#31248  - END
		viewApplication(smAppl);
		initWorkflowStatus(getManagedBean().getSmAppl(), status);	
		if (!SupplierManagementConst.SUPP_TYPE_BASIC.equalsIgnoreCase(smAppl
				.getSupplierType()) && !SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT.equalsIgnoreCase(smAppl
						.getSupplierType())) {
			initBumiRecommendNote();
			initVirtualCert();
			if (!getManagedBean().isSupplierUser()) {
				initPOApproverDecisionInfo();
				initSiteVisit();
			}
		}
		//START 14530
		// sort created date descendent
		Collections.sort(this.getManagedBean().getSmAppl().getSmApplInquiries(),
				new Comparator<SmApplInquiry>() {
						public int compare(SmApplInquiry o1, SmApplInquiry o2) {
							if (o1.getCreatedDate() == null
									|| o2.getCreatedDate() == null)
								return 0;
								return o2.getCreatedDate().compareTo(
											o1.getCreatedDate());
								}
							});
					
		if(this.getManagedBean().getSmAppl().getSmApplInquiries() != null){
			this.getManagedBean().setPoSupplierInquiryHistory(new ArrayList<SmApplInquiry>());
			for (SmApplInquiry inquiry : this.getManagedBean().getSmAppl()
					.getSmApplInquiries()) {
				//#34392 - start
//				if (!StringUtils.isBlank(inquiry.getInquirySubject())
//						&& inquiry
//								.getInquirySubject()
//								.equals(SupplierManagementConst.INQUIRY_PO_SUPPLIER)) {
					inquiry.getSmApplSupportDocInquiries().size();
					this.getManagedBean().getPoSupplierInquiryHistory().add(inquiry);
					POinquiryRowSelect(inquiry);
					
//				}
				//#34392 - end
			}
		}		
		//END 14530
		
		/*
		 * retrieve status from SM_APPL, not workflow status
		 String statusDesc = null; 
		 if(smAppl.getPmStatus() != null) {
			statusDesc = getApplicationStatus(smAppl.getPmStatus(), smAppl);
			getManagedBean().setApplicationStatusDesc(statusDesc);
			List<String> rejectedStatus = Arrays
					.asList(SupplierManagementConst.REJECTED_STATUSES);
			if (rejectedStatus.contains(smAppl.getPmStatus().getStatusId()
					.toString())) {
				getManagedBean().setResubmitUrl(
						SmCommonUtil.getEpBaseUrl()
								+ "/resubmit-application?apId="
								+ smAppl.getApplId());
			}
		}
		else {
			Locale locale = new FacesScopes().getUserLocale();
			statusDesc = ResourceBundleUtils.getMessage("lbl.sm.no.available",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
			getManagedBean().setApplicationStatusDesc(statusDesc);
		}*/
		// 25792 - end
	}

	/**
	 * Download virtual certificate
	 * 
	 * @param SmMofCert
	 *            cert
	 */
	public void printVirtualCert(SmMofCert cert) {
		try {
			getVirtualCertInstance().printVirtualCert(cert);
		} catch (IOException e) {
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * Initialized virtual certificate by application Id
	 */
	private void initVirtualCert() {
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
		try {
			SmVirtualCertHelper.getInstance().autoGenerateVirtualCert(
					getManagedBean().getSmAppl().getApplId(), false);
			getManagedBean().setMofCertLs(
					SmServiceLookup.getVirtualCertSO()
							.retrieveMofCertsByApplId(reqDTO).getSmMofCerts());
		} catch (Exception ex) {
			getManagedBean().setMofCertLs(
					SmServiceLookup.getVirtualCertSO()
							.retrieveMofCertsByApplId(reqDTO).getSmMofCerts());
		}
	}

	/**
	 * Initialized PO or approver decision info
	 */
	private void initPOApproverDecisionInfo() {

		log.info("initPOApproverDecisionInfo");
		SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO()
				.retrievePOApproverFAandOLASmRemarks(
						getManagedBean().getSmAppl().getApplNo(),
						getManagedBean().getSmAppl().getApplId());
		resetPOApproverDecisionInfo();
		if (resDTO.getPoApproverRemarksList() != null) {
			log.info("PoApproverRemarksList() size:"
					+ resDTO.getPoApproverRemarksList().size());
			//#32424 START--03032023--
			boolean isMoreThanOneRemark = false; 	
			
			if(resDTO.getPoApproverRemarksList().size()> 1){
				isMoreThanOneRemark = true; 
			}//#32424 END--03032023--
			
			for (Object[] obj : resDTO.getPoApproverRemarksList()) {
				boolean isPoUser = false;
				boolean isApproverUser = false;
				boolean isBothPoApproverHaveRemark = false; //#32424 --03032023--

				String userName = (String) obj[0];
				Date remarkCreatedDate = (Date) obj[1];
				String roleType = (String) obj[2];
				String docType = (String) obj[3];
				String remark = (String) obj[4];

				log.info("userName:" + userName + ",remarkCreatedDate:"
						+ remarkCreatedDate + ",roleType:" + roleType
						+ ",docType:" + docType + ",remark:" + remark);
				
				//#32424 START--03032023--
				if (roleType.equalsIgnoreCase(my.ep.web.constant.sm.SupplierManagementConst.SECTION_REV_ROLE_PO) && 
						roleType.equalsIgnoreCase(my.ep.web.constant.sm.SupplierManagementConst.SECTION_REV_ROLE_APP)){
					isBothPoApproverHaveRemark = true;
				} //#32424 END--03032023--

				if (roleType != null) {
					if (roleType
							.equals(my.ep.web.constant.sm.SupplierManagementConst.SECTION_REV_ROLE_PO)) {
						isPoUser = true;
					} 
					
					if (roleType.equals(my.ep.web.constant.sm.SupplierManagementConst.SECTION_REV_ROLE_APP)){
						isApproverUser = true;
					}
				}

				if (isPoUser) {
					getManagedBean().setPoUserName(userName);
					 //#32424 START--03032023--
					//To handle a condition for scenario PO1 rejecting the application. 
					if (docType.equals(ParameterConst.SM_APPLN_RMRK_DECISION_REJECT)
							&& !isMoreThanOneRemark
							&& !isBothPoApproverHaveRemark) {
						remark = "";  
					} //#32424 END--03032023--
					setPORemarksInfo(docType, remark, remarkCreatedDate);
				} 
				//25930
				if(isApproverUser) {
					getManagedBean().setApproverUserName(userName);
					//#32424 START--03032023--
					if (docType.equals(ParameterConst.SM_APPLN_RMRK_DECISION_REJECT)) {
						remark = "";
					} //#32424 END--03032023--
					setApproverRemarksInfo(docType, remark, remarkCreatedDate);
				}
			}
		}
		
		// #29664 - start
		SmRemarksReqDTO remarksReqDTO = new SmRemarksReqDTO();
		//remarksReqDTO.setDocType(ParameterConst.SM_APPLN_RMRK_DECISION_APPROVE); //#32424 --03032023--
		remarksReqDTO.setDocId(getManagedBean().getSmAppl().getApplId());
		//#32424 START--03032023--
		SmRemaksResDTO remaksResDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocIdSiteVisitAfterApproval(remarksReqDTO);
		
		if (remaksResDTO.getRemarksList().size() > 0) {
			log.info("getRemarksList().size():: " + remaksResDTO.getRemarksList().size());
			for (SmRemark poRemark : remaksResDTO.getRemarksList()) {
				boolean isDifferentFirstAndSecondPoForApprovedAppl = false; 
				boolean isDifferentFirstAndSecondPoForRejectedAppl = false; 
				
				// #32424 - start
				if (!StringUtils.isEmpty(poRemark.getRoleCode()) && poRemark.getRoleCode().equals(RoleCode.UPS_PO.value())){				
					System.out.println("getDocId:: " + poRemark.getDocId() + " || getRoleCode:: "
							+ poRemark.getRoleCode() + " || getChangedDate:: " + poRemark.getChangedDate()
							+ " || getRemark:: " + poRemark.getRemark());
				// #32424 - end
					getManagedBean().setPoRemarkAfterSiteVisit(poRemark.getRemark());
					getManagedBean().setPoRemarkAfterSiteVisitCreatedDate(poRemark.getChangedDate());
					
					for (SmApplSectionReview smApplSectionreview : getManagedBean().getSmAppl().getSmApplSectionReviews()) {
						if(smApplSectionreview.getRoleType().equals(my.ep.web.constant.sm.SupplierManagementConst.SECTION_REV_ROLE_PO)
								&& (!smApplSectionreview.getChangedBy().equals(poRemark.getChangedBy())
										||!poRemark.getChangedBy().equals(getManagedBean().getSmAppl().getPoUserId()))){
								isDifferentFirstAndSecondPoForApprovedAppl = true;
							}
					}
					if (isDifferentFirstAndSecondPoForApprovedAppl) {  
						PmUser user = SmCommonUtil.getUserByUserId(poRemark.getChangedBy());
						getManagedBean().setSecondPoUserName(user.getUserName());
					} 
					break;
				} else if (StringUtils.isEmpty(poRemark.getRoleCode())
						&& poRemark.getDocType().equals(ParameterConst.SM_APPLN_RMRK_DECISION_APPROVE)) {
					// #32424 - start
					SmWorkflowStatus currWorkflowStatus = SmCommonUtil.getCurrentActiveWokFlowStatus(getManagedBean()
							.getSmAppl().getApplId());					
					SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
					String poChangedDate = sdf.format(poRemark.getChangedDate());
					String wFlowCreatedDate = sdf.format(currWorkflowStatus.getCreatedDate());
					
					System.out.println("getPoUserId== " + getManagedBean().getSmAppl().getPoUserId()
							+ " || poRemark.getChangedBy()==" + poRemark.getChangedBy() 
							+ " || wFlow CreatedBy==" + currWorkflowStatus.getCreatedBy() + " || applStatusId=="
							+ getManagedBean().getSmAppl().getPmStatus().getStatusId() + " || wFlowStatusId=="
							+ currWorkflowStatus.getPmStatus().getStatusId() + " || poChangedDate==" + poChangedDate
							+ " || wFlowCreatedDate==" + wFlowCreatedDate);
					
					if ((getManagedBean().getSmAppl().getPoUserId() != null && ((getManagedBean().getSmAppl()
							.getPoUserId().equals(poRemark.getChangedBy())) || poRemark.getChangedBy().equals(
							currWorkflowStatus.getCreatedBy())))
							&& (getManagedBean().getSmAppl().getPmStatus().getStatusId() != null
									&& getManagedBean().getSmAppl().getPmStatus().getStatusId() == SupplierManagementConst.PM_STATUS_REGISTERED && currWorkflowStatus
									.getPmStatus().getStatusId() == SupplierManagementConst.PM_STATUS_REGISTERED)
							&& poChangedDate.equals(wFlowCreatedDate)) {
						getManagedBean().setPoRemarkAfterSiteVisit(poRemark.getRemark());
						getManagedBean().setPoRemarkAfterSiteVisitCreatedDate(poRemark.getChangedDate());

						if (!(poRemark.getChangedBy().equals(getManagedBean().getSmAppl().getPoUserId()))) {
							PmUser user = SmCommonUtil.getUserByUserId(poRemark.getChangedBy());
							getManagedBean().setSecondPoUserName(user.getUserName());
						}

						if (getManagedBean().getSmAppl().getPoUserId().equals(poRemark.getChangedBy())
								|| poRemark.getChangedBy().equals(currWorkflowStatus.getCreatedBy())) {
							// update roleCode in smRemark
							log.info("getRemarkId:: " + poRemark.getRemarkId());
							updateRoleCode(getManagedBean().getSmAppl().getApplId(), poRemark.getRemarkId(),
									ParameterConst.SM_APPLN_RMRK_DECISION_APPROVE, RoleCode.UPS_PO.value());
						}
					}
				// #32424 - end
				} else if (StringUtils.isEmpty(poRemark.getRoleCode()) && remaksResDTO.getRemarksList().size()>1
								&& poRemark.getDocType().equals(ParameterConst.SM_APPLN_RMRK_DECISION_REJECT)) {
						
						for (SmApplSectionReview smApplSectionreview : getManagedBean().getSmAppl().getSmApplSectionReviews()) {
							if(smApplSectionreview.getRoleType().equals(my.ep.web.constant.sm.SupplierManagementConst.SECTION_REV_ROLE_PO)
									&& (!smApplSectionreview.getChangedBy().equals(poRemark.getChangedBy())
											||!poRemark.getChangedBy().equals(getManagedBean().getSmAppl().getPoUserId()))){
									isDifferentFirstAndSecondPoForRejectedAppl = true;
								}
						}
						if (isDifferentFirstAndSecondPoForRejectedAppl) {  
							PmUser user = SmCommonUtil.getUserByUserId(poRemark.getChangedBy());
							getManagedBean().setSecondPoUserName(user.getUserName());
						}
						
						getManagedBean().setPoRemarkAfterSiteVisit("N/A");
						getManagedBean().setPoRemarkAfterSiteVisitCreatedDate(poRemark.getChangedDate());
					}
			} //#32424 END--03032023--
		}
		// #29664 - end
	}

	/**
	 * Reset PO or approver decision info to null
	 */
	private void resetPOApproverDecisionInfo() {
		getManagedBean().setPoApprOverlappedRemark(null);
		getManagedBean().setPoUserName(null);
		getManagedBean().setPoRemarkCreatedDate(null);
		getManagedBean().setPoRemark(null);
		getManagedBean().setApproverUserName(null);
		getManagedBean().setApproverRemarkCreatedDate(null);
		getManagedBean().setApproverRemark(null);
		// #29664 - start
		getManagedBean().setSecondPoUserName(null);		
		getManagedBean().setPoRemarkAfterSiteVisit(null);
		// #29664 - end
	}

	/**
	 * Set PO remarks info
	 * 
	 * @param docType
	 *            document type
	 * @param remark
	 *            remark
	 * @param remarkCreatedDate
	 *            remark created date
	 */
	private void setPORemarksInfo(String docType, String remark,
			Date remarkCreatedDate) {
		setPOApproverRemarksInfo(true, docType, remark, remarkCreatedDate);
	}

	/**
	 * Set approver remarks info
	 * 
	 * @param docType
	 *            document type
	 * @param remark
	 *            remark
	 * @param remarkCreatedDate
	 *            remark created date
	 */
	private void setApproverRemarksInfo(String docType, String remark,
			Date remarkCreatedDate) {
		setPOApproverRemarksInfo(false, docType, remark, remarkCreatedDate);
	}

	/**
	 * Set PO/approver remarks info
	 * 
	 * @param isPoUser
	 *            po user indicator
	 * @param docType
	 *            document type
	 * @param remark
	 *            remark
	 * @param remarkCreatedDate
	 *            remark created date
	 */
	private void setPOApproverRemarksInfo(boolean isPoUser, String docType,
			String remark, Date remarkCreatedDate) {
		if (docType != null) {
			if (docType.equals(ParameterConst.SM_APPLN_RMRK_DECISION_APPROVE)
					|| docType
							.equals(ParameterConst.SM_APPLN_RMRK_DECISION_REJECT)) {
				if (isPoUser) {
					getManagedBean().setPoRemark(remark);
					getManagedBean().setPoRemarkCreatedDate(remarkCreatedDate);
				} else {
					getManagedBean().setApproverRemark(remark);
					getManagedBean().setApproverRemarkCreatedDate(
							remarkCreatedDate);
				}
			} else {
				getManagedBean().setPoApprOverlappedRemark(remark);
			}
		}
	}

	public void initSiteVisit() {
		SvoSiteVisitBackingBean svo = new SvoSiteVisitBackingBean();
		svo.getManagedBean().setAppId(getManagedBean().getSmAppl().getApplId());
		svo.reviewSiteVisitReport();
	}

	/**
	 * Get VirtualCertBackingBean instance
	 * 
	 * @return VirtualCertBackingBean
	 */
	public static VirtualCertBackingBean getVirtualCertInstance() {
		if (virtualCertInstance == null) {
			virtualCertInstance = new VirtualCertBackingBean();
		}
		return virtualCertInstance;

	}

	/**
	 * Get CommonApplBackingBean instance
	 * 
	 * @return CommonApplBackingBean
	 */
	public static CommonApplBackingBean getCommonApplInstance() {
		if (commonApplInstance == null) {
			commonApplInstance = new CommonApplBackingBean();
		}
		return commonApplInstance;

	}

	/**
	 * Get PmTrackingDiaryBackingBean instance
	 * 
	 * @return PmTrackingDiaryBackingBean
	 */
	public static PmTrackingDiaryBackingBean getTrackinDiaryInstance() {
		if (trackinDiaryInstance == null) {
			trackinDiaryInstance = new PmTrackingDiaryBackingBean();
		}
		return trackinDiaryInstance;

	}

	/**
	 * Get RegistrationBackingBean instance
	 * 
	 * @return RegistrationBackingBean
	 */
	public static RegistrationBackingBean getRegistrationBackingBean() {
		if (registrationBackingBean == null) {
			registrationBackingBean = new RegistrationBackingBean();
		}
		return registrationBackingBean;

	}

	/**
	 * Get SvoSiteVisitBackingBean instance
	 * 
	 * @return SvoSiteVisitBackingBean
	 */
	public static SvoSiteVisitBackingBean getSvoSiteVisitBackingBean() {
		if (svoSiteVisitBackingBean == null) {
			svoSiteVisitBackingBean = new SvoSiteVisitBackingBean();
		}
		return svoSiteVisitBackingBean;
	}
	
	public void selectedOverlapCategory(SmSupplierCategory supplierCategory){
		getManagedBean().getSmAppl().setApplId(supplierCategory.getSmAppl().getApplId());
		
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/*test*/
	
	public StreamedContent commonDownload(String path, String displayFileName)
			throws IOException {
		
		String filePath = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		
		filePath += "/" + path;

		commonDownloadUtil(filePath, displayFileName);
		return null;
	}
	
	public void commonDownloadUtil(String filePath, String displayFileName) throws IOException{
		
		PortletResponse resp = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; filename=\""+ displayFileName + "\"");
		//response.setContentType("application/octet-stream");
		response.setContentType(getMimeType(displayFileName));
		response.flushBuffer();

		InputStream docInputStream = null;
		try {
			URLConnection uc = new URL(filePath).openConnection();
			docInputStream = new BufferedInputStream(uc.getInputStream());
		} catch (Exception e) {
			// Cannot open connection, maybe local file
			docInputStream = new FileInputStream(filePath);
			
			log.info("Error found while downloading file "+filePath+" : "+e);
			//log.error(e);
			//			e.printStackTrace();
			
		}

		OutputStream out = null;
		try {		

			out = response.getOutputStream();
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = docInputStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096];
			}
		} catch(Exception e) {
			log.error(e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (docInputStream != null) {
				docInputStream.close();	
			}
		}

	}
	
	private String getMimeType(String fileName) {
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
		
		String mimeType = (String)CommonConstants.getFileExtMap().get(fileExt);
		
		return (mimeType != null) ? mimeType : "octet-stream";
	}
	
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
		getManagedBean().setOverlapCategoryVOList(
				vo.getOverlapCategoryVOList());
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
			retVal = new SmOverlapCategoryVO();
			retVal.setOverLappedCategoryExist(true);
			retVal.setOverLappedCategoryMsg(SmCommonUtil.getMessage(
					"lbl.sm.category.overlappingMsg", null)
					+ "<br />");
			Locale loc = new Locale("ms", "MY");
			ResourceBundleUtils.getMessage("lbl.sm.category.overlappingMsg",
					null, ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc);
			retVal.setOverLappedCategoryRemarks(ResourceBundleUtils.getMessage(
					"lbl.sm.category.overlappingMsg", null,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc)
					+ System.lineSeparator()
					+ System.lineSeparator());
			retVal.setOverlapCategoryVOList(getOverlapCategoryVOList(
					resultList, smAppl.getSupplierType()));
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
	
	public String onDisplayConfirmCanceMofRegistration() {
		String page = null;
		Locale locale = new FacesScopes().getUserLocale();
		StringBuilder sb = new StringBuilder();
		if (getManagedBean().getSupplierRemarks() == null
				|| getManagedBean().getSupplierRemarks().trim() == "") {
			sb.append(ResourceBundleUtils.getMessage(
					"lbl.sm.mof.reg.reason.delete",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			sb.append(" is mandatory </br>");
			
		}
		if (getManagedBean().getCancellationAttachment().getFileName() == null
				|| getManagedBean().getCancellationAttachment().getFileName()
						.trim() == "") {
			sb.append(ResourceBundleUtils.getMessage("lbl.sm.suppdoc.attach",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			sb.append(" is mandatory ");
			
		}
		return page;
	}
	public String getStatusDescription(Object[] appl) {
		BigDecimal suppStatDec = appl[12] != null ? (BigDecimal) appl[12]
				: new BigDecimal(0);
		String applType = appl[1] != null ? (String) appl[1] : "";
		String suppType = appl[2] != null ? (String) appl[2] : "";

		Locale locale = new FacesScopes().getUserLocale();
		if (suppStatDec.toString().equals("5") && applType.equals("O")
				&& suppType.equals("B")) {
			return ResourceBundleUtils.getMessage("lbl.sm.in.progress",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		} else if (StringUtils.isNotBlank((String) appl[6])) {
			return (String) appl[6];
		}
		return ResourceBundleUtils.getMessage("lbl.sm.no.available",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
	}

	// 25792 - start
	private ISearchBean<PmParameterDesc> getOrgTypeSearchBean(String componentId) {
		ISearchBean<PmParameterDesc> orgTypeSearchBean = super
				.createSearchBean(PmParameterDesc.class, componentId);
		return orgTypeSearchBean;
	}

	/**
	 * The method used for generating a list of available agency type type as
	 * selection.
	 * 
	 */
	public SelectItem[] getAgencyTypeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = null;
		searchBean = getOrgTypeSearchBean("orgTypeFederalList");

		SelectItem[] items = null, filteredItems = null;
		if (searchBean != null) {
			searchBean.getSortColumns();
			items = getDropdownValuesBySearchBean(
					ParameterTypeConst.ORGANISATION_TYPES, searchBean);

			int index = 1;
			filteredItems = new SelectItem[5];
			filteredItems[0] = items[0];
			Locale locale = new FacesScopes().getUserLocale();
			filteredItems[0].setLabel(ResourceBundleUtils.getMessage("lbl.txt.please.select",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			for (int i = 1; i < items.length; i++) {
				IParameterSO parameterSO = (IParameterSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IParameterSO.class);

				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO.setParameterId((Long) items[i].getValue());
				ParameterResDTO parameterResDTO = parameterSO
						.retrieveParameterById(parameterReqDTO);

				PmParameter pmParameter = parameterResDTO.getPmParameter();
				if (pmParameter.getParameterCode().equalsIgnoreCase(
						ParameterConst.ORG_TYPE_STATUTORY_BODY)
						|| pmParameter.getParameterCode().equalsIgnoreCase(
								ParameterConst.ORG_TYPE_STATE_STATUTORY_BODY)
						|| pmParameter.getParameterCode().equalsIgnoreCase(
								ParameterConst.ORG_TYPE_LOCAL_AUTHORITY)
						|| pmParameter.getParameterCode().equalsIgnoreCase(
								ParameterConst.ORG_TYPE_STATE_GOVT)) {
					filteredItems[index++] = items[i];
				}
			}
			return filteredItems;

		}

		return items;
	}

	public void resetSearch() {
		getManagedBean().setAccountType(StringUtils.EMPTY);
		getManagedBean().setAgencyType(null);
		getManagedBean().setAgencyName(StringUtils.EMPTY);
		getManagedBean().setSearchEpNo(StringUtils.EMPTY);
		super.resetSearch();

	}

	public String getBusinessNatureSection(SmSupplierBusiness suppBusinessNat) {
		String sectionLvl1Code = "";
		PmBusinessNature pmBizNat = suppBusinessNat.getPmBusinessNature();
		PmBusinessNature pmBizObjL4 = null;
		if (pmBizNat != null
				&& pmBizNat.getParentBusinessNature() != null
				&& pmBizNat.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL4 = SmCommonUtil.retrieveBusinessNatureById(pmBizNat
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL3 = null;
		if (pmBizObjL4 != null
				&& pmBizObjL4.getParentBusinessNature() != null
				&& pmBizObjL4.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL3 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL4
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL2 = null;
		if (pmBizObjL3 != null
				&& pmBizObjL3.getParentBusinessNature() != null
				&& pmBizObjL3.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL2 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL3
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL1 = null;
		if (pmBizObjL2 != null
				&& pmBizObjL2.getParentBusinessNature() != null
				&& pmBizObjL2.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL1 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL2
					.getParentBusinessNature().getBusinessNatureId());
		}
		sectionLvl1Code = pmBizObjL1 == null ? "" : pmBizObjL1.getLevelCode();
		return sectionLvl1Code;
	}
	
	public void onCatalogueInfoTabChange(int tabIndex, String itemStatus) {
		getManagedBean().setCatalogueInfoItemStatus(itemStatus);
		getManagedBean().getSupplierItemSearchDataModel().reloadData();
		getManagedBean().setCatalogueInfoTabActiveIndex(tabIndex);
	}
	
	public void onChangePricingTypeGroupTab() {
		
		getManagedBean().setPricingTypeTabActiveIndex(0);
	}
	
	
	/**
	 * getUomCode and description.
	 * 
	 * 
	 *@return uomCodeDesc.		
	 */
	public String retriveUomCodeDesc(PmUom pmUom){
		String uomCodeDesc="";
		if(null!=pmUom){
			if(null!=pmUom.getUomName()){
				uomCodeDesc = pmUom.getUomName();//25792
			}
		}
		
	return uomCodeDesc;	
	}
	public void resetInputOnChangeAccountType() {
		
		log.info("OnChangeAccountType");
		getManagedBean().setAgencyType(null);
		getManagedBean().setAgencyName(StringUtils.EMPTY);
		getManagedBean().setSearchEpNo(StringUtils.EMPTY);
		super.resetSearch();
	}
	// 25792 - end
	
	public void resubmitSSM(){
		//#27232 - START SSM CHANGES FOR RESUBMIT
		if(SmCommonUtil.getInstance().ssmActive() && businessTypeTovalidateWithSSM(getManagedBean().getSmAppl().getSmSupplier().getRegNo(),getManagedBean().getSmAppl().getSmSupplier().getBusinessType())){
			//checking with ssm
			SsmReqDTO ssmReqDTO = new SsmReqDTO();
			ssmReqDTO.setRegNo(getManagedBean().getSmAppl().getSmSupplier().getRegNo());
			ssmReqDTO.setBusinessType(getManagedBean().getSmAppl().getSmSupplier().getBusinessType());
			//ssmReqDTO.setCheckOnly(true);
			SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
			//27951
			this.getManagedBean().setSsmResDTO(ssmResDTO);
			if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
				//check the business/company registration status in ssm
				this.getManagedBean().setStatusDescFromSSM(setStatusMessageFromSSM(ssmResDTO.getStatusFromSSM()));
				if(!ssmResDTO.getStatusFromSSM().equalsIgnoreCase("A") && !ssmResDTO.getStatusFromSSM().equalsIgnoreCase("E")){
					//27382 - start
					if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("L")){
						throw new EpException(MessageConst.ERROR_721097,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
					}//27382 - Terminated 
					if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("T")){
						throw new EpException(MessageConst.ERROR_721098,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
					}//27382 - Dissolved Conversion to LLP
					if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("B")){
						if(businessTypeIsROC(this.getManagedBean().getSmSupplier().getBusinessType())){
							throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.b", null) });
						}
						if(businessTypeIsROB(this.getManagedBean().getSmSupplier().getBusinessType())){
							throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.dissolved", null) });
						}
						}else{
					throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});}
					} 
					//27382 - end
			} else{					
				SmCommonUtil.ssmError(ssmResDTO);
			}
		}
		//#27232 - END SSM CHANGES FOR RESUBMIT
	}
	
	//28771 - start
	public void onChangeSupportingDocumentMode(){
		getManagedBean().searchSupplier();
		
	}
	//28771 - end 
	
	//#31248 - START
	public Boolean setResubmitButton(SmAppl smAppl){
		ISmWorkFlowStatusSO workFlowStatusSO = SmServiceLookup.getSmWorkFlowStatusSO();
		SmWorkFlowStatusReqDTO smWorkFlowStatusReqDTO = new SmWorkFlowStatusReqDTO();
		//32627 - START
		//retrieve by latest reject appl id
		MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
		mofReqDTO.setSupplierId(smAppl.getSmSupplier().getSupplierId());
		mofReqDTO.setSmAppl(smAppl);
		SmAppl latestRejectedAppl = SmServiceLookup
				.getMofRegistrationSO()
				.retrieveLatestRejectedApplicationBySupplierId(mofReqDTO)
				.getSmAppl();
		// #35336 START
		SmAppl latestRegisteredAppl = SmServiceLookup.getMofRegistrationSO()
			.retrieveLatestRegisteredApplicationBySupplierId(mofReqDTO).getSmAppl();

		if (isValidDateComparison(latestRegisteredAppl, latestRejectedAppl)) {
			if (latestRegisteredAppl.getCreatedDate().before(latestRejectedAppl.getCreatedDate())
					&& smAppl.getApplId().equals(latestRejectedAppl.getApplId())
					&& !SmApplicationUtil.checkRejectedApplExpired(smAppl) 
					&& smAppl.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)) {
				showErrorAdminHasBeenReplace();
			}
		}
		// #35336 END
		if(latestRejectedAppl != null &&
				smAppl.getApplId().equals(latestRejectedAppl.getApplId())){
		smWorkFlowStatusReqDTO.setApplId(smAppl.getApplId());
		}
		List<String> rejectedStatus = Arrays
				.asList(SupplierManagementConst.REJECTED_STATUSES);
		
		SmWorkFlowStatusResDTO smWorkFlowStatusResDTO = workFlowStatusSO.retrieveCurrentWorkFlowStatusByDocId(smWorkFlowStatusReqDTO);
		Boolean button = false;
		if(smWorkFlowStatusResDTO != null 
				&& smWorkFlowStatusResDTO.getWorkflowStatus() != null 
				&& smWorkFlowStatusResDTO.getWorkflowStatus().getSmAppl() != null
				&& !smWorkFlowStatusResDTO.getWorkflowStatus().getSmAppl().isActiveAppl()){
			button = true;
			if(smWorkFlowStatusResDTO.getWorkflowStatus().getPmStatus() != null){
				if(smWorkFlowStatusResDTO.getWorkflowStatus().getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STATUS_DELETE_PENDING_APPL_CONTRACTOR)){
					button = false;
				} else if(smWorkFlowStatusResDTO.getWorkflowStatus().getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STATUS_DELETE_PENDING_APPL_CONSULTANT)){
					button = false;
				} else if(smWorkFlowStatusResDTO.getWorkflowStatus().getPmStatus().getStatusId().equals(Long.parseLong(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_PAYMENT))){
					button = false;
				} else if(smWorkFlowStatusResDTO.getWorkflowStatus().getPmStatus().getStatusId().equals(Long.parseLong(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_PROCESSING_FEE))){
					button = false;
				}else if(smWorkFlowStatusResDTO.getWorkflowStatus().getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STATUS_REGISTERED)){
					button = false;
				}//#32002 - start
				else if(rejectedStatus.contains(smWorkFlowStatusResDTO.getWorkflowStatus().getPmStatus().getStatusId().toString())
						 && checkStatusMofNoBySupplierId(smAppl)== false){
					button = false;
				}//#32002 - end
			}
		}
		//#35336 START
		if (smAppl.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)) {
			button = false;
		}
		//#35336 END
		return button;
		//32627 - END
	}
	//#31248 - END
	
	//#11641 - Start
	public int getMandatoryCounter() {
		return mandatoryCounter++;
	}

	public int getNonMandatoryCounter() {
		return nonMandatoryCounter++;
	}
	
	public boolean isMandatoryEmpty() {
		int mandatoryCount = 0;
		for(SmApplSupportDoc suppDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
			if(suppDoc.getPmDocument().isMandatory()){
				mandatoryCount++;
			}
		}
		if(mandatoryCount > 0){
			mandatoryEmpty = false;
		}else{
			mandatoryEmpty = true;
		}
		return mandatoryEmpty;
	}
	
	public boolean isNonMandatoryEmpty() {
		int nonMandatoryCount = 0;
		for(SmApplSupportDoc suppDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
			if(!suppDoc.getPmDocument().isMandatory()){
				nonMandatoryCount++;
			}
		}
		if(nonMandatoryCount > 0){
			this.nonMandatoryEmpty = false;
		}else{
			this.nonMandatoryEmpty = true;
		}
		return nonMandatoryEmpty;
	}
	
	/**
	 * inquiry row select
	 */
	public void inquiryRowSelect(String docMode){
		if(docMode.equalsIgnoreCase("online")){
			RequestContext.getCurrentInstance().update("resOutPanelId");
		}
		if(docMode.equalsIgnoreCase("offline")){
			RequestContext.getCurrentInstance().update("resOutPanelIdOffline");
		}
	}

	public boolean isInquiryResponse() {
		for(SmApplInquiry smApplInquiry : this.getManagedBean().getSmAppl().getSmApplInquiries()){
			if(smApplInquiry.getResponseDate() == null){
				inquiryResponse = false;
				return inquiryResponse;
			}
		}
		return inquiryResponse;
	}
	
	public List<SmApplSupportDocInquiry> getNoResponseInquiryForApplHistory(){
		for(SmApplInquiry smApplInquiry : this.getManagedBean().getSmAppl().getSmApplInquiries()){
			if(smApplInquiry.getResponseDate() == null){
				return smApplInquiry.getSmApplSupportDocInquiries();
			}
		}
		return null;
	}
	//11641 - END
	
	//#29482 - Start
	public void assignSelAcadQualify(SmPersonnel smPersonnel) {
		this.getManagedBean().setSmPersonnel(smPersonnel);
		RequestContext.getCurrentInstance().execute("qualifyDialog.show()");
	}
	//#29482 - End
	
	//#29823 - START
	public boolean isApplPORejectedAfterApprovelSV(){
		boolean isRejected = false;
		boolean isHasBasic = false;
		if(this.getManagedBean().getSmAppl().getPmStatus().getStatusId() == 20112){
			for(SmWorkflowStatus smWorkflowStatus : this.getManagedBean().getSmAppl().getSmWorkflowStatuses()){
				if(smWorkflowStatus.getPmStatus().getStatusId() == 20116 || smWorkflowStatus.getPmStatus().getStatusId() == 20117){
					isRejected = true;
					break;
				}
			}
		}
		
		for(SmAppl previousSmAppl : this.getManagedBean().getSmAppl().getSmSupplier().getSmAppls()){
			if(previousSmAppl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_BASIC)){
				isHasBasic = true;
				break;
			}
		}
		
		if(this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.APPL_TYPE_MOF) && isRejected && !isHasBasic){
			return false;
		}else if((this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.APPL_TYPE_CATEGORY)
				|| this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.APPL_TYPE_BUMI)) && isRejected){ //#34483
			return false;
		}
		
		return true;
	}
	//#29823 - END
	//#32002 - start
	public Boolean checkStatusMofNoBySupplierId(SmAppl smAppl){
		
		SmSupplier supplier = smAppl.getSmSupplier();
		if (supplier.getSupplierId() != null
				&& (smAppl.getApplType().equals(SupplierManagementConst.SM_APPLICATION_TYPE_RENEW_MOF)
				|| smAppl.getApplType().equals(SupplierManagementConst.SM_APPLICATION_TYPE_UPDATE_MOF) //32627 START
				|| smAppl.getApplType().equals(SupplierManagementConst.SM_APPLICATION_TYPE_ADD_CATEGORY))) { //32627 END
			SmMofAccount resultList = SmServiceLookup.getMofRegistrationSO().getLatestMofAccountBySupplierId(
					supplier.getSupplierId());
			if (resultList != null) {
				if (resultList.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)
						&& supplier.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)) {
					return false;
				}
				if (resultList.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)
						&& supplier.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
					return false;
				}
			}
		}
		return true;
	}//#32002 - end
	
	//14530
	public boolean checkPoinquiry()
	{
		for(SmApplInquiry smapplinquiry : this.getManagedBean().getSmAppl().getSmApplInquiries()){
			if(smapplinquiry.getInquirySubject().equalsIgnoreCase(SupplierManagementConst.INQUIRY_PO_SUPPLIER)){
				return true;
			}
		}
		return false;
	}
	/**
	 * inquiry row select
	 */
	public void POinquiryRowSelect(SmApplInquiry applInquiry) {
		this.getManagedBean().setPoToSupplierInquiry(applInquiry);
		// #34392 - start
		getManagedBean().setMandatoryDocsInquiry(new ArrayList<SmApplSupportDocInquiry>());
		getManagedBean().setNonMandatoryDocsInquiry(new ArrayList<SmApplSupportDocInquiry>());

		for (SmApplSupportDocInquiry doc : applInquiry.getSmApplSupportDocInquiries()) {
			doc.getSmApplInquiry().getSmAppl().getSupportingDocMode();
			if (doc.getPmDocument().isMandatory()) {
				getManagedBean().getMandatoryDocsInquiry().add(doc);
			} else if (!doc.getPmDocument().isMandatory()) {
				getManagedBean().getNonMandatoryDocsInquiry().add(doc);
			}

		}

		if (!EntityHelper.isCollectionEmpty(getManagedBean().getMandatoryDocsInquiry())) {
			Collections.sort(getManagedBean().getMandatoryDocsInquiry(), new Comparator<SmApplSupportDocInquiry>() {
				@Override
				public int compare(SmApplSupportDocInquiry doc1, SmApplSupportDocInquiry doc2) {
					if (doc1.getPmDocument().getDisplaySeq() == null) {
						return (doc2.getPmDocument().getDisplaySeq() == null) ? 0 : 1;
					}
					if (doc2.getPmDocument().getDisplaySeq() == null) {
						return -1;
					}
					return Integer.valueOf(doc1.getPmDocument().getDisplaySeq()
							.compareTo(doc2.getPmDocument().getDisplaySeq()));
				}
			});
		}

		if (!EntityHelper.isCollectionEmpty(getManagedBean().getNonMandatoryDocsInquiry())) {
			Collections.sort(getManagedBean().getNonMandatoryDocsInquiry(), new Comparator<SmApplSupportDocInquiry>() {
				@Override
				public int compare(SmApplSupportDocInquiry doc1, SmApplSupportDocInquiry doc2) {
					if (doc1.getPmDocument().getDisplaySeq() == null) {
						return (doc2.getPmDocument().getDisplaySeq() == null) ? 0 : 1;
					}
					if (doc2.getPmDocument().getDisplaySeq() == null) {
						return -1;
					}
					return Integer.valueOf(doc1.getPmDocument().getDisplaySeq()
							.compareTo(doc2.getPmDocument().getDisplaySeq()));
				}
			});
		}
		// #34392 - end
		RequestContext.getCurrentInstance().update("inquiryDetails");
	}
	//14530
	
	// #34392 - Start
	/**
	 * re-select doc file before opening popup
	 * 
	 * @param applInquiry
	 */
	public List<SmApplInquiryDocFile> reselectDocFileValue(
			long applSupportDocInquiryId) {
		ISupportingDocInquirySO reqSO = (ISupportingDocInquirySO) BusinessDelegate
				.getInstance().getServiceObject("sm",
						ISupportingDocInquirySO.class);
		
		SupportingDocInquiryResDTO resDTO = reqSO
				.retrieveInquiryFileByApplSupportDocId(applSupportDocInquiryId);

		return resDTO.getSmApplInquiryDocFileList();
	}
	// #34392 - end
	
	// #32424 - start
	public void updateRoleCode(Long applId, Long remarkId, String docType, String poRoleCode) {
		System.out.println("ViewApplHistoryBackingBean.updateRoleCode()");
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setDocId(applId);
		reqDTO.setRemarkId(remarkId);
		reqDTO.setDocType(docType);
		reqDTO.setRoleCode(poRoleCode);

		SmServiceLookup.getSmRemarksSO().updateRoleCode(reqDTO);
	}
	// #32424 - end
	
	//#30766 & #34209 - START
	public void rePopulateApplFacilitiesList()
	{
		getManagedBean().setDockYardFacilitiesVOList(new ArrayList<PmCategoryL3>());
		getManagedBean().setFactoryFacilitiesVOList(new ArrayList<PmCategoryL3>());
		getManagedBean().setStoreFacilitiesVOList(new ArrayList<PmCategoryL3>());
		getManagedBean().setWorkshopFacilitiesVOList(new ArrayList<PmCategoryL3>());
		
		if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl().getSmSupplierCategories())) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl().getSmSupplierCategories()) {
				if(cat.getPmCategoryL3() != null && !StringUtils.isBlank(cat.getPmCategoryL3().getFacilityType())) {
					if(cat.getPmCategoryL3().getFacilityType().equalsIgnoreCase(my.ep.web.constant.sm.SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD))
						getManagedBean().getDockYardFacilitiesVOList().add(cat.getPmCategoryL3());
					if(cat.getPmCategoryL3().getFacilityType().equalsIgnoreCase(my.ep.web.constant.sm.SupplierManagementConst.SM_FACILITY_TYPE_FACTORY))
						getManagedBean().getFactoryFacilitiesVOList().add(cat.getPmCategoryL3());
					if(cat.getPmCategoryL3().getFacilityType().equalsIgnoreCase(my.ep.web.constant.sm.SupplierManagementConst.SM_FACILITY_TYPE_STORE))
						getManagedBean().getStoreFacilitiesVOList().add(cat.getPmCategoryL3());
					if(cat.getPmCategoryL3().getFacilityType().equalsIgnoreCase(my.ep.web.constant.sm.SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP))
						getManagedBean().getWorkshopFacilitiesVOList().add(cat.getPmCategoryL3());
				}
			}
		}
	}
	
	public void rePopulateMachine(){
		getManagedBean().getSmAppl().setSmMachineDtls(getManagedBean().getAllMachineDtls());
	}
	//#30766 & #34209 - END
	// #35336 - START
	private boolean isValidDateComparison(SmAppl latestRegisteredAppl, SmAppl latestRejectedAppl) {
		return latestRegisteredAppl != null && latestRegisteredAppl.getChangedDate() != null
			&& latestRejectedAppl != null && latestRejectedAppl.getCreatedDate() != null;
	}

	private void showErrorAdminHasBeenReplace() {
		FacesContext.getCurrentInstance().addMessage(
			null,
			new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", MessageUtils.getMessage(
				MessageConst.ERROR_721099, MessageUtils.BUNDLE_COMMON_MESSAGES,
				new FacesScopes().getUserLocale())));
	}
	// #35336 - END
}
