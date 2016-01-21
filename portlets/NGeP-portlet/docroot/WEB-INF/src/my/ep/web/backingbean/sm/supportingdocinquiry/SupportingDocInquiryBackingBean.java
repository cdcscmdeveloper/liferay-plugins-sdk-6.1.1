package my.ep.web.backingbean.sm.supportingdocinquiry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.supportingdocinquiry.ISupportingDocInquirySO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.supportingdocinquiry.SupportingDocInquiryReqDTO;
import my.ep.dto.sm.supportingdocinquiry.SupportingDocInquiryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.supportingdocinquiry.SupportingDocInquiryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "supportingDocInquiryBackingBean")
@RequestScoped
public class SupportingDocInquiryBackingBean extends BaseBackingBean<SupportingDocInquiryManagedBean> {

	private static Logger log = Logger.getLogger(SupportingDocInquiryBackingBean.class);

	public SupportingDocInquiryBackingBean(){

	}
	
	/**
	 * Pre - render Expired SDO Inquiry List Page
	 */
	public void preRenderExpiredInquiry() {
		renewSesionObject();
		getManagedBean().setExpiredSdoVerifySuppDocApplList(
				SmServiceLookup.getSupportingDocInquirySO()
						.retrieveExpiredSdoSuppDocVerificationAppl()
						.getExpiredSdoInquiryApplList());
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/suppliersupportingdocinquiry/expiredSdoInquiryList.xhtml",
						"",null);
	}

	/**
	 * Navigate to Expired SDO Inquiry Selected Application Details Page
	 * 
	 * @param SmAppl
	 *            selectedSmAppl
	 */
	public void navigateToExpiredInquiryApplication(String applNo) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(applNo);
		getManagedBean().setSmAppl(
				SmServiceLookup.getMofRegistrationSO()
						.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl());
		getManagedBean().setSmSupplier(
				getManagedBean().getSmAppl().getSmSupplier());
		checkIfSupplierHasActiveTransaction();
		if (getManagedBean().isAllowedToSubmit()) {
			getManagedBean()
					.setSupplierBusinessTypeVal(
							SmCommonUtil
									.getDropDownViewOnlyValueByCode(
											SupplierManagementConst.SUPPLIER_BUSINESS_TYPE,
											getManagedBean().getSmSupplier()
													.getBusinessType()));
			setBumiRecommendNote();
		}
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/suppliersupportingdocinquiry/expiredSdoInquiryApplication.xhtml",
						"",null);
	}

	/**
	 * Back to Expired SDO Inquiry List Page
	 */
	public void backToExpiredSdoInquiryListPage() {
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/suppliersupportingdocinquiry/expiredSdoInquiryList.xhtml",
						"",null);
	}

	/**
	 * Submit resend to SDO inquiry
	 */
	public void submitResendToSdoInquiry() {
		try {
			initiateSmApplicationTask();
			String[] params = { "" };
			params[0] = getManagedBean().getSmAppl().getApplNo();
			FacesContext fc = FacesContext.getCurrentInstance();
			SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_INFO, MessageUtils
					.getMessage(MessageConst.INFO_700063, params,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()), null);
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							SmApplicationUtil.getApplicationErrorPage());
		}catch(Exception ex){
			throw new EpException(ex.getMessage());
		}
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
			throw new EpException(e.getMessage());
		}
		finalFilePath = null;
	}
	
	public void viewDocInquiry(){
		log.info("**********viewDocInquiry*********");
		ISupportingDocInquirySO reqSO = (ISupportingDocInquirySO) BusinessDelegate.getInstance().getServiceObject("sm", ISupportingDocInquirySO.class);
		SupportingDocInquiryReqDTO reqDTO = new SupportingDocInquiryReqDTO();
		//this.getManagedBean().setAppId(1258837L);
		reqDTO.setAppId(this.getManagedBean().getAppId());
		
		SupportingDocInquiryResDTO resDTO = reqSO.retrieveInquiryByAppId(reqDTO);
		this.getManagedBean().setSmApplInquiry(resDTO.getSmApplInquiry());
		
	}
	
	public String submitInquiryResponse(){
		log.info("**********submitInquiryResponse*********");
		try{
			log.info("**********submitInquiryResponse*********");
			ISupportingDocInquirySO reqSO = (ISupportingDocInquirySO) BusinessDelegate.getInstance().getServiceObject("sm", ISupportingDocInquirySO.class);
			SupportingDocInquiryReqDTO reqDTO = new SupportingDocInquiryReqDTO();
			Object token = new FacesScopes().getBPMAuthenticationCtxToken();
			
			Date currDate = new Date();
			
			log.info("**********Current Date*********" + currDate);
			this.getManagedBean().getSmApplInquiry().setResponseDate(currDate);
			reqDTO.setToken(token);
			reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
			reqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
			reqDTO.setSmApplInquiry(this.getManagedBean().getSmApplInquiry());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setTrackingDairyVO(SmCommonUtil.assignTrackingDiaryValues(
					SupplierManagementConst.SUPP_SEND_INQUIRY_SDO, this.getManagedBean().getAppId(), this.getManagedBean().getSmApplInquiry().getSmAppl().getApplNo(), getUserRole()));

			SupportingDocInquiryResDTO resDTO = reqSO.updateInquiry(reqDTO);
			//SupportingDocInquiryResDTO resDTO = new SupportingDocInquiryResDTO();
			//resDTO.setSuccessful(true);
			if(resDTO.isSuccessful()){
				log.info("*****is Success******");
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				renewSesionObject();
				return navigateToAck();
			}
		}catch(Exception ex){
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
			throw new EpException(ex.getMessage());
		}
		
		return null;
	}
	
	public String navigateToAck(){
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721102,
		MessageUtils.BUNDLE_COMMON_MESSAGES,
		new FacesScopes().getUserLocale())));
		
		renewSesionObject();
		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}
	
	
	public void preRenderView(){
		log.info("**********preRenderView*********");
		if(this.getManagedBean().getSmApplInquiry() == null){
			//this.getManagedBean().setAppId(1259567L); //TODO
			viewDocInquiry();
		}
	}
	
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
	
	private String getUserRole() {
		FacesScopes facesScopes = new FacesScopes();
		if (facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
			return RoleCode.MOF_SUPPLIER_ADMIN.value();
		} else if (facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
			return RoleCode.MOF_SUPPLIER_USER.value();
		} else if (facesScopes.hasRoleUser(RoleCode.FL_USER.value())) {
			return RoleCode.FL_USER.value();
		} else if (facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN
				.value())) {
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();
		} else if (facesScopes
				.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
			return RoleCode.BASIC_SUPPLIER_USER.value();
		} else {
			return null;
		}
	}
	
	/**
	 * To check whether selected supplier has active transaction
	 * 
	 * @param boolean isCancelMof
	 */
	private void checkIfSupplierHasActiveTransaction() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (SmApplicationUtil.checkActiveSmApplForSupp(getManagedBean()
						.getSmAppl().getSmSupplier())) {
			getManagedBean().setAllowedToSubmit(false);
			SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_ERROR,
					MessageUtils.getMessage(MessageConst.ERROR_722608,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()), null);
		}
	}
	
	/**
	 * Initialized and construct bumi recommendation message
	 */
	private void setBumiRecommendNote() {
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
	 * Make a call to the BPM to resubmit to SDO.
	 */
	private void initiateSmApplicationTask() {
		SmAppl bpmAppl = getManagedBean().getSmAppl();
		if (!bpmAppl.isActiveAppl()) {
			bpmAppl.setActiveAppl(true);
		}

		MofRegistrationReqDTO reqDTO = setMofRegistrationReqDTO(
				bpmAppl,
				SupplierManagementConst.SM_APPL_BPM_OUTCOME_SUBMIT,
				SupplierManagementConst.APPROVER_SUBMIT_RESEND_EXPIRED_SDO_INQUIRY);

		reqDTO.setUpdateTask(false);

		if (getManagedBean().getSmAppl().isResubmit()) {
			reqDTO.setPoLoginId(SmApplicationUtil.getPOLoginId(getManagedBean()
					.getSmSupplier(), getManagedBean().getSmAppl()));
		}

		// Set list of users to send to BPM
		if (getManagedBean().getSmAppl().getApplType().equalsIgnoreCase("N")
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
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			reqDTO.getUserList().add(pmUser.getLoginId());

		}
		SmServiceLookup.getMofRegistrationSO()
				.initiateResendExpiredSdoInquiryTask(reqDTO);

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
		reqDTO.setTrackingDairyVO(assignTrackingDairyValues(getManagedBean().getSmAppl(),actionDesc));
		PmState state = getManagedBean().getSmAppl().getSmCompanyBasic()
				.getSmCompanyAddresses().get(0).getSmAddress().getPmState();
		if (state != null && state.getStateId() != null) {
			PmZone zone = SmApplicationUtil.retrieveZoneByStateId(state
					.getStateId());
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
	 * Assign tracking diary values to VO
	 * 
	 * @param SmAppl
	 *            appl
	 * @param String
	 *            actionDesc
	 * @return SmTrackingDairyVO
	 */
	private SmTrackingDairyVO assignTrackingDairyValues(SmAppl appl,
			String actionDesc) {
		SmTrackingDairyVO trckVO = new SmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getManagedBean().getRoleCode());
		trckVO.setDocId(appl.getApplId());
		trckVO.setDocNo(appl.getApplNo());
		trckVO.setDocType("SR");
		trckVO.setGroupId(null);
		trckVO.setStatus(appl.getPmStatus());
		trckVO.setGroupDocType(null);
		return trckVO;

	}
}
