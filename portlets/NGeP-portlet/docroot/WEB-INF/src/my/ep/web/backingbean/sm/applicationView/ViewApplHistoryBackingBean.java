package my.ep.web.backingbean.sm.applicationView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
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
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmApplSupportDocInquiry;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.backingbean.sm.commonappl.CommonApplBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.backingbean.sm.virtualcert.VirtualCertBackingBean;
import my.ep.web.backingbean.trackingdiary.PmTrackingDiaryBackingBean;
import my.ep.web.managedbean.sm.applicationView.ViewApplHistoryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.SmVirtualCertHelper;
import my.ep.web.vo.DisciplinaryActionVO;
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
				//log.info("2nd tab");
				initSecondTabInfo();
			} else if (outputTab == 2) {
				//log.info("3rd tab");
				initThirdTabInfo();
			} else if (outputTab == 3) {
				//log.info("4th tab");
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
				//log.info("5th tab");
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
	@SuppressWarnings("deprecation")
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
		//26238 - start
		else {			
			SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
			List<SmDisciplinaryAction> resultList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(supplier);
			if(!EntityHelper.isCollectionEmpty(resultList) && resultList.size() > 0){
				getManagedBean().setDisciplinaryAction(true);
			} else {
				getManagedBean().setDisciplinaryAction(false);
			}			
		} //26238 - end

		SmCommonUtil.redirectToPage("/xhtml/sm/revisionSmApplView/viewApplicationHistory/searchSupplierApplicationHistoryPage.xhtml","", null);
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
		if (getManagedBean().getSmAppl().getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_IN_PROGRESS) {
			if (SmApplicationUtil
					.isRejectedApplValidForResubmission(getManagedBean()
							.getSmAppl())) {
				SmCommonUtil.redirectToExternalUrl(getManagedBean()
						.getResubmitUrl());
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
					redirectToReapplyNewAppl();
				}
				//END 27951
			}
		} else {			
			//#27232 - END SSM CHANGES FOR RESUBMIT
			if (SmApplicationUtil
					.isRejectedApplValidForResubmission(getManagedBean()
							.getSmAppl())) {
				SmCommonUtil.redirectToExternalUrl(getManagedBean()
						.getResubmitUrl());
			} else {
				throw new EpException(SmCommonUtil.getMessage(
						"lbl.sm.resubmitExpired", null));
			}
		}
	}

	/**
	 * Cloning new application and redirect to New MOF application page
	 */
	public void redirectToReapplyNewAppl() {
		SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
		//START 27951
		//ANY CHANGES HERE PLEASE CHANGES IN ViewApplHistoryBackingBean.redirectToReapplyNewAppl & viewOnlineAccount.RegistrationBackingBean & SmApplicationBackingBean.proceedToApplication
		boolean proceed = true;
		smApplBB.getManagedBean().setPreRegistration(this.getManagedBean().isPreRegistration());
		smApplBB.getManagedBean().setPersonnelsRole(this.getManagedBean().getPersonnelsRole());
		this.getManagedBean().setPreRegistration(true);
		//log.info("-------------------------------------------------------------------"+SmCommonUtil.getInstance().ssmActive());
		if(SmCommonUtil.getInstance().ssmActive() && businessTypeTovalidateWithSSM(getManagedBean().getSmAppl().getSmSupplier().getRegNo(),getManagedBean().getSmAppl().getSmSupplier().getBusinessType()) && this.getManagedBean().getSsmResDTO() != null && !this.getManagedBean().isCheckRoleSSM()){			
			List<SmPersonnel> personnelsRole = this.getManagedBean().getPersonnelsRole();
			personnelsRole.clear();
			for(SmPersonnel personnel :  this.getManagedBean().getSmAppl().getSmPersonnels()){
				if (personnel.isEquityOwner() || personnel.isDirector()) {
					if(personnel.getEpRole() != null){
						SmPersonnel personnelSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(),this.getManagedBean().getSsmResDTO().getSmPersonnelOwners(),"identificationNo");
						SmPersonnel personnelDirectorSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(),this.getManagedBean().getSsmResDTO().getSmPersonnelDirector(),"identificationNo");
						if(personnelDirectorSSM == null && personnelSSM == null){
							personnelsRole.add(personnel);
						}
					}
				}
			}
			//log.info("--------------------------------personnelsRole.size()-----------------------------------"+personnelsRole.size());
			if(personnelsRole != null && personnelsRole.size() > 0) {
					proceed = false;
					getManagedBean().setPreRegistration(true);
					getManagedBean().setSmSupplierSsm(this.getManagedBean().getSmSupplier());	
					this.getManagedBean().setPersonnelsRole(personnelsRole);
					RequestContext.getCurrentInstance().execute("blockWidget.hide()");
					RequestContext.getCurrentInstance().execute("ownerCheckingWidgetVar.show()");
			}
			
		}
		if(proceed){
		// #24923
		smApplBB.getManagedBean().setNewSupplierType(
				getManagedBean().getSmAppl().getSupplierType());
		smApplBB.proceedToApplication(ParameterConst.NEW_SM_APPLN_FOR_MOF);

		// Enhancement #20467 - point to new appl page with bullet
		String page = my.ep.web.constant.sm.SupplierManagementConst.APPLICATION_BASE_PAGE_BY_TYPE
				.get("RE");
		SmCommonUtil.redirectToPage(page, null, null, null);
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
		//26238 - start
		SmSupplier supplier = smAppl.getSmSupplier();
		List<SmDisciplinaryAction> resultList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(supplier);
		if(!EntityHelper.isCollectionEmpty(resultList)
				&& resultList.size() > 0){
			getManagedBean().setDisciplinaryAction(true);
		} else {
			getManagedBean().setDisciplinaryAction(false);
		}
		//26238 - end
		//28237
		retrieveActiveDisciplinaryAction(supplier);
		
		// 25792 - start
		//log.info("Init Sm Appl");
		//log.info("Appl Id: " + smAppl.getApplId());		
		//log.info("Supplier type: " + smAppl.getSupplierType());
		getManagedBean().setTabActiveIndex(0);
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

		//log.info("initPOApproverDecisionInfo");
		SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO()
				.retrievePOApproverFAandOLASmRemarks(
						getManagedBean().getSmAppl().getApplNo(),
						getManagedBean().getSmAppl().getApplId());
		resetPOApproverDecisionInfo();
		if (resDTO.getPoApproverRemarksList() != null) {
			//log.info("PoApproverRemarksList() size:"
					//+ resDTO.getPoApproverRemarksList().size());
			for (Object[] obj : resDTO.getPoApproverRemarksList()) {
				boolean isPoUser = false;
				boolean isApproverUser = false;

				String userName = (String) obj[0];
				Date remarkCreatedDate = (Date) obj[1];
				String roleType = (String) obj[2];
				String docType = (String) obj[3];
				String remark = (String) obj[4];

				//log.info("userName:" + userName + ",remarkCreatedDate:"
						//+ remarkCreatedDate + ",roleType:" + roleType
						//+ ",docType:" + docType + ",remark:" + remark);
				
				if (roleType != null){
					if (roleType.equals(my.ep.web.constant.sm.SupplierManagementConst.SECTION_REV_ROLE_PO)) {
						isPoUser = true;
					} 
					
					if (roleType.equals(my.ep.web.constant.sm.SupplierManagementConst.SECTION_REV_ROLE_APP)){
						isApproverUser = true;
					}
				}

				if (isPoUser) {
					getManagedBean().setPoUserName(userName);
					setPORemarksInfo(docType, remark, remarkCreatedDate);
				} 
				//25930
				if(isApproverUser) {
					getManagedBean().setApproverUserName(userName);
					setApproverRemarksInfo(docType, remark, remarkCreatedDate);
				}
			}
		}
		
		// #29664 - start
		SmRemarksReqDTO remarksReqDTO = new SmRemarksReqDTO();
		remarksReqDTO.setDocType(ParameterConst.SM_APPLN_RMRK_DECISION_APPROVE);
		remarksReqDTO.setDocId(getManagedBean().getSmAppl().getApplId());

		SmRemaksResDTO remaksResDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(remarksReqDTO);

		if (remaksResDTO.getRemarksList().size() > 0) {
			for (SmRemark poRemark : remaksResDTO.getRemarksList()) {
				if (poRemark.getRoleCode() != null && poRemark.getRoleCode().equals(RoleCode.UPS_PO.value())) {
					getManagedBean().setPoRemarkAfterSiteVisit(poRemark.getRemark());
					getManagedBean().setPoRemarkAfterSiteVisitCreatedDate(poRemark.getCreatedDate());

					if (!(poRemark.getCreatedBy().equals(getManagedBean().getSmAppl().getPoUserId()))) {
						PmUser user = SmCommonUtil.getUserByUserId(poRemark.getCreatedBy());
						getManagedBean().setSecondPoUserName(user.getUserName());
					}
				} else {
					List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(poRemark.getCreatedBy());
					String roleCode = StringUtils.EMPTY;
					if (!EntityHelper.isCollectionEmpty(userRoles)) {
						for (PmUserRole pmRole : userRoles) {
							if (pmRole.getPmRole().getRoleCode().equals(RoleCode.UPS_PO.value()) && pmRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								roleCode = pmRole.getPmRole().getRoleCode();
								break;
							}
						}
					}
					if ((roleCode != null && roleCode.equals(RoleCode.UPS_PO.value()))) {
						//System.out.println("getCreatedBy: " + poRemark.getCreatedBy() + " || getRoleCode = " + poRemark.getRoleCode() + " || getRemarkId = "
								//+ poRemark.getRemarkId() + " || getRemark = " + poRemark.getRemark());
						getManagedBean().setPoRemarkAfterSiteVisit(poRemark.getRemark());
						getManagedBean().setPoRemarkAfterSiteVisitCreatedDate(poRemark.getCreatedDate());
						if (!(poRemark.getCreatedBy().equals(getManagedBean().getSmAppl().getPoUserId()))) {
							PmUser user = SmCommonUtil.getUserByUserId(poRemark.getCreatedBy());
							getManagedBean().setSecondPoUserName(user.getUserName());
						}
					}
				}
			}
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
			
			//log.info("Error found while downloading file "+filePath+" : "+e);
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
	//26238 - start
	public void onViewDisciplinaryAction() {
		//28237 - start
		SmSupplier currSupp = getManagedBean().getSmSupplier();
		
		if(currSupp != null){
			
			//27876: Disciplinary Action - start - <27876>
			SmSupplier newCurrSupp =  new SmSupplier();
			newCurrSupp = currSupp;
			newCurrSupp.setFlagSupplier(true);
			//27876: Disciplinary Action - end - <27876>
			
			//30872: Disciplinary Action - start - <30872>
			newCurrSupp.setFlagDASuppProfile(false);
			//30872: Disciplinary Action - end - <30872>
			
			List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(newCurrSupp);
			if(!EntityHelper.isCollectionEmpty(voList)
					&& voList.size() > 0){
				getManagedBean().setDisciplineVOs(voList);
			} else {
				getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
			}
		} else {
			getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		}
		//28237 - end
		/*
		SmSupplier currSupp = null;
		if(getManagedBean().isSupplierUser()){
			currSupp = SmApplicationUtil.getCurrentSupplier();
		} else {
			currSupp = getManagedBean().getSearchSupplier();
		}
		
		List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(currSupp);
		if(!EntityHelper.isCollectionEmpty(voList)
				&& voList.size() > 0){
			getManagedBean().setDisciplineVOs(voList);
		} else {
			getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		}
		*/
		RequestContext.getCurrentInstance().execute(
				"disciplinaryActionHistoryDlg.show();");
	} 
	
	//28281
	/*
	private static List<DisciplinaryActionVO> getDisciplinaryActionVO(SmSupplier currSupp) {
		List<DisciplinaryActionVO> voList = new ArrayList<DisciplinaryActionVO>();
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		perReqDTO.setSmSupplierId(currSupp.getSupplierId());
		
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelBySupplierId(perReqDTO);
		
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
					if(disciplinaryAction.getRemark() != null)
						retVal.setRemarks(disciplinaryAction.getRemark());
					
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
					
					if(disciplinaryAction.isCancelCategory()){
						List<DisciplinaryActionCategoryVO> catVoList = new ArrayList<DisciplinaryActionCategoryVO>();
						List<Object[]> catList = SmApplicationUtil.retrieveCancelCategory();
						
						if(!EntityHelper.isCollectionEmpty(catList)
								&& catList.size() > 0){
							for(Object[] arr : catList){
								if(disciplinaryAction.getDisciplinaryActionId().toString().equalsIgnoreCase(arr[7].toString())){
									DisciplinaryActionCategoryVO catVo = new DisciplinaryActionCategoryVO();
									String catCode = StringUtils.EMPTY;
									catCode = arr[3].toString() + arr[4].toString() + arr[5].toString() ;
									catVo.setCategoryCode(catCode);	
									catVoList.add(catVo);
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
								if(personnel.isSuspendRegistration()){
									blacklistOwner.setIcNo(personnel.getIdentificationNo());
									blacklistownerDirectorList.add(blacklistOwner);
								}
							}
							retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
						}						
					}
					
					if(disciplinaryAction.isCancelRegistration()){
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
						if(personnelResDTO != null 
								&& personnelResDTO.getSmPersonnelList().size() > 0){
							for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
								BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
								if(personnel.isCancelRegistration()){
									blacklistOwner.setIcNo(personnel.getIdentificationNo());
									blacklistownerDirectorList.add(blacklistOwner);
								}
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
								if(personnel.isBlacklisted()){
									blacklistOwner.setIcNo(personnel.getIdentificationNo());
									blacklistownerDirectorList.add(blacklistOwner);
								}
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
	*/
	
	public boolean mofView(){
		boolean mofView = false;
		SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
		if(supplier != null && supplier.getMofNo() != null){
			mofView = true;
		}
		return mofView;
	}
	//26238 - end
	
	public void resetInputOnChangeAccountType() {
		
		//log.info("OnChangeAccountType");
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
		}else if(this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.APPL_TYPE_CATEGORY) && isRejected){
			return false;
		}
		
		return true;
	}
	//#29823 - END
}
