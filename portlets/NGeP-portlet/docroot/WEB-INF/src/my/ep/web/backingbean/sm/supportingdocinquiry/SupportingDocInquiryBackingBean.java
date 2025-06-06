package my.ep.web.backingbean.sm.supportingdocinquiry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.supportingdocinquiry.ISupportingDocInquirySO;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.supportingdocinquiry.SupportingDocInquiryReqDTO;
import my.ep.dto.sm.supportingdocinquiry.SupportingDocInquiryResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmApplSupportDocInquiry;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.supportingdocinquiry.SupportingDocInquiryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "supportingDocInquiryBackingBean")
@RequestScoped
public class SupportingDocInquiryBackingBean extends BaseBackingBean<SupportingDocInquiryManagedBean> {

	private static Logger log = Logger.getLogger(SupportingDocInquiryBackingBean.class);
	//#11641 - Start
	private int mandatoryCounter = 1;
	private int nonMandatoryCounter = 1;
	private boolean mandatoryEmpty = false;
	private boolean nonMandatoryEmpty = false;
	//#11641 - End

	public SupportingDocInquiryBackingBean(){
		SmCommonUtil.setMDCUserId();
	}
	
	/**
	 * Pre - render Expired SDO Inquiry List Page
	 */
	@SuppressWarnings("deprecation")
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
	@SuppressWarnings("deprecation")
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
	@SuppressWarnings("deprecation")
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
		//log.info("**********viewDocInquiry*********");
		ISupportingDocInquirySO reqSO = (ISupportingDocInquirySO) BusinessDelegate.getInstance().getServiceObject("sm", ISupportingDocInquirySO.class);
		SupportingDocInquiryReqDTO reqDTO = new SupportingDocInquiryReqDTO();
		//this.getManagedBean().setAppId(1258837L);
		reqDTO.setAppId(this.getManagedBean().getAppId());
		// #37775 - Start
		reqSO.recheckSupportDocFileAndDocFileInquiry(reqDTO.getAppId());
		// #37775 - End
		SupportingDocInquiryResDTO resDTO = reqSO.retrieveInquiryByAppId(reqDTO);
		this.getManagedBean().setSmApplInquiry(resDTO.getSmApplInquiry());
		//#11641 - START
		Collections.sort(this.getManagedBean().getSmApplInquiry().getSmAppl().getSmApplSupportDocs(), new Comparator<SmApplSupportDoc>(){
			@Override
			public int compare(SmApplSupportDoc doc1, SmApplSupportDoc doc2) {
				if(doc1.getPmDocument().getDisplaySeq() == null){
					return (doc2.getPmDocument().getDisplaySeq() == null) ?  0 : 1;
				}
				if(doc2.getPmDocument().getDisplaySeq() == null){
					return -1;
				}
				return Integer.valueOf(doc1.getPmDocument().getDisplaySeq().compareTo(doc2.getPmDocument().getDisplaySeq()));
			}
		});
		//#11641 - END
	}
	
	public String submitInquiryResponse(){
		//log.info("**********submitInquiryResponse*********");
		try{
			PmUser user = new FacesScopes().getUserSessionBean();
			//log.info("**********submitInquiryResponse*********");
			ISupportingDocInquirySO reqSO = (ISupportingDocInquirySO) BusinessDelegate.getInstance().getServiceObject("sm", ISupportingDocInquirySO.class);
			SupportingDocInquiryReqDTO reqDTO = new SupportingDocInquiryReqDTO();
			Object token = new FacesScopes().getBPMAuthenticationCtxToken();
			
			Date currDate = new Date();
			
			//log.info("**********Current Date*********" + currDate);
			this.getManagedBean().getSmApplInquiry().setResponseDate(currDate);
			reqDTO.setToken(token);
			reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
			reqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
			reqDTO.setSmApplInquiry(this.getManagedBean().getSmApplInquiry());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setChangedBy(user.getUserId());
			
			//#11641 - START
			if(!this.getManagedBean().getSmApplInquiry().getSmAppl().getSmApplSupportDocs().isEmpty()){
				for(SmApplSupportDoc smApplSupportDoc : this.getManagedBean().getSmApplInquiry().getSmAppl().getSmApplSupportDocs()){
					if(!smApplSupportDoc.getPmDocument().isMandatory() && !smApplSupportDoc.isReceived()){
						boolean nonMandatoryDocHasNew = false;
						if(this.getManagedBean().getAddedDocFile()!=null){
							for(SmApplDocFile addedDoc : this.getManagedBean().getAddedDocFile()){
								if(addedDoc.getSmApplSupportDoc().getApplSupportDocId() == smApplSupportDoc.getApplSupportDocId()){
									nonMandatoryDocHasNew = true;
								}
							}
						}
						if(this.getManagedBean().getAddedDocFile()!=null && !nonMandatoryDocHasNew){
							this.getManagedBean().getAddedDocFile().addAll(smApplSupportDoc.getSmApplDocFiles());
						}
					}
				}
			}
			reqDTO.setAddedSmApplDocFiles(this.getManagedBean().getAddedDocFile());
			reqDTO.setUpdatedSupportDocs(this.getManagedBean().getUpdatedSupportDocs());
			reqDTO.setEmptyToRemove(this.getManagedBean().getEmptyToRemove());
			//#11641 - END

			//#28807 - START
			//log.info("#28807 supp doc after supplier response -> " + this.getManagedBean().getSmApplInquiry().getSmAppl().getApplNo() + " -> " + this.getManagedBean().getSmApplInquiry().getSmAppl().getSmApplSupportDocs().size());
			//int filesCount = 0;
			for(SmApplSupportDoc smApplSupportDoc : this.getManagedBean().getSmApplInquiry().getSmAppl().getSmApplSupportDocs()){
				for(SmApplDocFile smApplDocFile : smApplSupportDoc.getSmApplDocFiles()){
					if(!smApplDocFile.getFileName().isEmpty() || 
						smApplDocFile.getFileName() != null || 
						!smApplDocFile.getFileName().equalsIgnoreCase("")){
						//filesCount+=1;
					}
				}
			}
			//log.info("#28807 total sup doc files -> " + this.getManagedBean().getSmApplInquiry().getSmAppl().getApplNo()+ " -> " + filesCount);
			//#28807 - END
			
			SupportingDocInquiryResDTO resDTO = reqSO.updateInquiry(reqDTO);
			//SupportingDocInquiryResDTO resDTO = new SupportingDocInquiryResDTO();
			//resDTO.setSuccessful(true);
			if(resDTO.isSuccessful()){
				//log.info("*****is Success******");
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
		//log.info("**********preRenderView*********");
		if(this.getManagedBean().getSmApplInquiry() == null){
			//this.getManagedBean().setAppId(1259567L); //
			viewDocInquiry();
		}
	}
	
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
	
	/*
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
		} else if(facesScopes.hasRoleUser(RoleCode.SUPPLIER_TEMP.value())){
			return RoleCode.SUPPLIER_TEMP.value();
		}else{
			return StringUtils.EMPTY;
		}
	}
	*/
	
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
	//#11641 - Start
	public int getMandatoryCounter() {
		return mandatoryCounter++;
	}

	public int getNonMandatoryCounter() {
		return nonMandatoryCounter++;
	}

	public boolean isMandatoryEmpty() {
		int mandatoryCount = 0;
		for(SmApplSupportDocInquiry smApplSupportDocInquiry : this.getManagedBean().getSmApplInquiry().getSmApplSupportDocInquiries()){
			if(smApplSupportDocInquiry.getPmDocument().isMandatory()){
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
		for(SmApplSupportDocInquiry smApplSupportDocInquiry : this.getManagedBean().getSmApplInquiry().getSmApplSupportDocInquiries()){
			if(!smApplSupportDocInquiry.getPmDocument().isMandatory()){
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
	
	public void handleSupDocFileAttachment(FileUploadEvent event) {
		log.info("********handleFileAttachment********");
		if (event.getFile() != null) {
			log.info("***** File attached *******"+ event.getFile().getFileName());
			
			if (getManagedBean().getSmApplInquiry().getSmAppl().getSmApplSupportDocs().contains(this.getManagedBean().getSupportDoc())) {
				log.info("******* Contains selected file ****");
				String filePath = Calendar.getInstance().get(Calendar.YEAR)
						+ "/SM/APPLICATION/"
						+ this.getManagedBean().getSmApplInquiry().getSmAppl().getApplNo() + "/SD/";
				SmApplSupportDoc doc = getManagedBean().getSupportDoc();

				if (EntityHelper.isCollectionEmpty(doc.getSmApplDocFiles())) {
					SmApplicationUtil.setDefaultSmApplDocFiles(doc);
				}
				
				SmApplDocFile smApplDocFile = new SmApplDocFile();
				smApplDocFile.setFileName(StringUtils.replace(event.getFile().getFileName(), " ", "-"));
				smApplDocFile.setSmApplSupportDoc(doc);
				smApplDocFile.setFilePath(filePath);
				
				for(int i=0; i<doc.getSmApplDocFiles().size(); i++){
					if(doc.getSmApplDocFiles().get(i).getFileName()==null || doc.getSmApplDocFiles().get(i).getFileName().equalsIgnoreCase(" ")){
						if(this.getManagedBean().getEmptyToRemove() == null){
							this.getManagedBean().setEmptyToRemove(new ArrayList<SmApplDocFile>());
							this.getManagedBean().getEmptyToRemove().add(doc.getSmApplDocFiles().get(i));
						}else{
							this.getManagedBean().getEmptyToRemove().add(doc.getSmApplDocFiles().get(i));
						}
					}
				}
				getManagedBean()
						.getSmApplInquiry()
						.getSmAppl()
						.getSmApplSupportDocs()
						.set(getManagedBean().getSmApplInquiry().getSmAppl()
								.getSmApplSupportDocs()
								.indexOf(getManagedBean().getSupportDoc()),
								getManagedBean().getSupportDoc());

				commonUploadFile(event, filePath);
				
				List<SmApplDocFile> smApplDocFiles = new ArrayList<SmApplDocFile>();
				for(SmApplSupportDoc smApplSupportDoc : this.getManagedBean().getSmApplInquiry().getSmAppl().getSmApplSupportDocs()){
					if(smApplSupportDoc.equals(this.getManagedBean().getSupportDoc())){
						if(smApplSupportDoc.getNewAddedFiles() != null){
							smApplDocFiles = smApplSupportDoc.getNewAddedFiles();
						}
						
						smApplDocFiles.add(smApplDocFile);
						if(this.getManagedBean().getAddedDocFile() == null){
							List<SmApplDocFile> newFile = new ArrayList<SmApplDocFile>();
							newFile.add(smApplDocFile);
							this.getManagedBean().setAddedDocFile(newFile);
						}else{
							this.getManagedBean().getAddedDocFile().add(smApplDocFile);
						}
						smApplSupportDoc.setNewAddedFiles(smApplDocFiles);
					}
				}
				if(this.getManagedBean().getUpdatedSupportDocs() != null){
					this.getManagedBean().getUpdatedSupportDocs().add(this.getManagedBean().getSupportDoc());
				}else{
					this.getManagedBean().setUpdatedSupportDocs(new ArrayList<SmApplSupportDoc>());
					this.getManagedBean().getUpdatedSupportDocs().add(this.getManagedBean().getSupportDoc());
				}
			}
		}
	}

	public void onDeleteSupDocFileAttachment(SmApplSupportDoc doc, int index, SmApplDocFile file) {
		this.getManagedBean().setSupportDoc(doc);
		this.getManagedBean().setAttachedFileIndex(index);
		this.getManagedBean().setSmApplDocFile(file);
		
		RequestContext.getCurrentInstance().execute(
				"confirmDeleteSupDocDialog.show();");
	}
	
	public void deleteSupDocFileAttachment() {
		SmApplSupportDoc smApplSupportDoc = getManagedBean().getSupportDoc();
		if(this.getManagedBean().getSmApplDocFile() != null){
			smApplSupportDoc.getSmApplDocFiles().remove(this.getManagedBean().getSmApplDocFile());
			smApplSupportDoc.getNewAddedFiles().remove(this.getManagedBean().getSmApplDocFile());
			this.getManagedBean().getAddedDocFile().remove(this.getManagedBean().getSmApplDocFile());
		}
		this.getManagedBean().setSmApplDocFile(null);
	}
	
	//#34392 - start
	public void deleteInquirySupDocFileAttachment() {
		SmApplSupportDoc smApplSupportDoc = getManagedBean().getSupportDoc();
		if(this.getManagedBean().getSmApplDocFile() != null){
			smApplSupportDoc.getSmApplDocFiles().remove(this.getManagedBean().getSmApplDocFile());
			smApplSupportDoc.getNewAddedFiles().remove(this.getManagedBean().getSmApplDocFile());
		}
		this.getManagedBean().setSmApplDocFile(null);
	}
	//#34392 - end
	
	public void assignSelectedSupportDocIndex(SmApplSupportDoc doc, int index) {
		this.getManagedBean().setAttachedFileIndex(index);
		this.getManagedBean().setSupportDoc(doc);
	}
	
	public String retrieveRemarkByDocId(PmDocument pmDocument){
		String remark = null;
		for(SmApplSupportDocInquiry smApplSupportDocInquiry : this.getManagedBean().getSmApplInquiry().getSmApplSupportDocInquiries()){
			if(smApplSupportDocInquiry.getPmDocument().equals(pmDocument)){
				remark = smApplSupportDocInquiry.getSdoRemarks();
			}	
		}
		return remark;
	}
	
	public void validateSupportDocs(){
		if(getManagedBean().getSmApplInquiry().getSmAppl().getSupportingDocMode() != null && getManagedBean().getSmApplInquiry().getSmAppl().getSupportingDocMode().equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)){
			log.info("=====Start Validate Supporting Doc=====");
			boolean validSupportDocs = true;
			int i = 0;
			StringBuilder sb = new StringBuilder();
			sb.append("<br/>");
			for (SmApplSupportDoc doc : getManagedBean().getSmApplInquiry().getSmAppl().getSmApplSupportDocs()) {
				if (doc.getPmDocument() != null && !doc.isReceived()
						&& doc.getPmDocument().isMandatory()
						&& !doc.getPmDocument().isHardCopyReq()
						&& (EntityHelper.isCollectionEmpty(doc.getNewAddedFiles())
								|| doc.getNewAddedFiles().get(0).getFileName()
										.equalsIgnoreCase(" ") || doc
								.getNewAddedFiles().get(0).getFilePath()
								.equalsIgnoreCase(" "))
								&& !sb.toString().contains(doc.getPmDocument().getDocName())) {
					sb.append(++i + ")");
					sb.append(doc.getPmDocument().getDocName());
					sb.append("<br/>");
					validSupportDocs = false;
				}
			}
			if (!validSupportDocs) {
				throw new EpException(MessageConst.ERROR_722686, new String[] { sb.toString() });
			} else {
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show();");
			}
		}else if(getManagedBean().getSmApplInquiry().getSmAppl().getSupportingDocMode() != null && getManagedBean().getSmApplInquiry().getSmAppl().getSupportingDocMode().equals(SupplierManagementConst.SUPPORTING_DOC_OFFLINE)){
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show();");
		}
	}
	//#11641 - END
	
	// #34392 - Start
	public void downloadSignatureSuppDoc(String docDefinitionKey)
			throws Exception {
		if (org.apache.commons.lang.StringUtils.isNotBlank(docDefinitionKey)) {
			SmApplicationBackingBean SmApplicationBackingBean = new SmApplicationBackingBean();
			if (docDefinitionKey.equals("SBP")) {
				SmApplicationBackingBean.getManagedBean().setSmAppl(
						getManagedBean().getSmApplInquiry().getSmAppl());
				SmApplicationBackingBean.printDeclarationForm();
			} else if (docDefinitionKey.equals("SBV")) {
				SmApplicationBackingBean.printBankVerificationForm();
			}
		}
	}
	// #34392 - END
}

