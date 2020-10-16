package my.ep.web.backingbean.sm.sdoreview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.zone.ZoneReqDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.dto.sm.sdoreview.SdoReviewResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPendingPoWeightage;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sdoreview.SdoReviewManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "sdoReviewBackingBean")
@RequestScoped
public class SdoReviewBackingBean extends BaseBackingBean<SdoReviewManagedBean> {
	private static final long serialVersionUID = 1L;

	private final static Logger log = Logger.getLogger(SdoReviewBackingBean.class);
	private StreamedContent file;
	private static final int inquiryTextLength = 2500;
	
	public SdoReviewBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	private FacesScopes getFacesScopes() {
		//#29116 CR 3460: Static Code Analysis
		//FacesScopes facesScopes = new FacesScopes();
		return new FacesScopes();
	}

	/**
	 * Retrieve the application by appl ID
	 * set the value and populate info on screen
	 * 
	 */
	public void viewSupportingDocument() {
		SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISdoReviewSO.class);
		reqDTO.setApplId(this.getManagedBean().getAppId());
		SdoReviewResDTO resDTO = reqSO.retrieveApplById(reqDTO);
		this.getManagedBean().setSmAppl(resDTO.getSmAppl());
		
		// show rejection reason for resubmit application
		if (getManagedBean().getSmAppl().isResubmit()
				&& getManagedBean().getSmAppl().getOriginalAppl() != null) {
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			mofReqDTO.setSmAppl(getManagedBean().getSmAppl());
			SmAppl latestRejectedAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveLatestRejectedApplication(mofReqDTO).getSmAppl();
			getManagedBean().setRejectReasonsMsg(
					SmCommonUtil
							.getHtmlApplicationRejectReasons(latestRejectedAppl
									.getSmApplRejectReasons()));
		}

		for(SmPersonnel sp : resDTO.getSmAppl().getSmPersonnels()){
			if(sp.isContactPerson()){
				this.getManagedBean().setSmPersonnel(sp);
				break;
			}
		}
		this.getManagedBean().setCompanyAdd(this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
		this.getManagedBean().setSmRemark(new SmRemark());
		this.getManagedBean().setSmApplInquiry(new SmApplInquiry());
		this.getManagedBean().setSelectedInquiryResponse(new SmApplInquiry());
		this.getManagedBean().setUnselectedSmApplSuppDocs(new ArrayList<SmApplSupportDoc>());
		this.getManagedBean().setSupportingDocMap(new HashMap<Long,SmApplSupportDoc>());

		String zoneCode = retrieveZoneByStateId(this.getManagedBean().getCompanyAdd().getPmState().getStateId());
		this.getManagedBean().setZoneCode(zoneCode);
		
		if(zoneCode.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.PM_ZONE_SABAH) 
				|| zoneCode.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.PM_ZONE_SARAWAK)){
			for(SmApplSupportDoc applSuppDocSbhSrk : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
				//#29116 CR 3460: Static Code Analysis Start
				//if(!applSuppDocSbhSrk.isReceived()){
					if(!applSuppDocSbhSrk.isReceived() && applSuppDocSbhSrk.getPmDocument().isMandatory()){
						this.getManagedBean().getUnselectedSmApplSuppDocs().add(applSuppDocSbhSrk);
					}
				//}
				//#29116 CR 3460: Static Code Analysis End

				this.getManagedBean().getSupportingDocMap().put(applSuppDocSbhSrk.getApplSupportDocId(), applSuppDocSbhSrk);
			}
		}else{
			for(SmApplSupportDoc applSuppDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
				//#29116 CR 3460: Static Code Analysis Start
				//if(!applSuppDoc.isReceived()){
					if(!applSuppDoc.isReceived() && applSuppDoc.getPmDocument().isMandatory()){
						this.getManagedBean().getUnselectedSmApplSuppDocs().add(applSuppDoc);
					}
				//}
				//#29116 CR 3460: Static Code Analysis End
				
				this.getManagedBean().getSupportingDocMap().put(applSuppDoc.getApplSupportDocId(), applSuppDoc);
			}
		}
		
		//retrieve Supplier GST Info
		SmGstSupplier gstSupp = SmCommonUtil.getSmGstSupplierBySupplierIdAndBranchCode(getManagedBean().getSmAppl().getSmSupplier().getSupplierId(), null, null,true);
		getManagedBean().setSmGstSupplier(gstSupp);
		
		
		//initialized mandatory and non mandatory supporting documents
		initNonMandatoryAndMandatoryDocs();
	}

	/**
	 * @deprecated
	 * Get Business Type Description
	 * @param String - businessType
	 * @return String
	 */
	public String getBusinessType(String businessType){
		String retVal = "";
		if(getManagedBean().getBusinessTypeLs()!=null && !getManagedBean().getBusinessTypeLs().isEmpty()){
			for(PmParameterDesc desc:getManagedBean().getBusinessTypeLs()){
				if(desc.getPmParameter().getParameterCode().equals(businessType)){
					retVal = desc.getCodeName();
					break;
				}
			}
		}
		return retVal;
	}
	
	public void onClickClaimButton(){
		claimBpmTask();
		getManagedBean().setClaimTask(true);
	}
	
	/**
	 * submit SDO Review Action
	 * @param String resAction
	 * @return String
	 */
	public String submitSDOReviewAction(String resAction) {
		if(resAction != null && StringUtils.isNotBlank(resAction)){
			if(resAction.equalsIgnoreCase(SupplierManagementConst.BPM_INQUIRY)){
				if(this.getManagedBean().getSmApplInquiry().getInquirySubject() == null 
						|| this.getManagedBean().getSmApplInquiry().getInquirySubject().equals("")){ 
					RequestContext.getCurrentInstance().update("inquiryMessageId");
					throw new EpException(MessageConst.ERROR_721104);
				}

				if(this.getManagedBean().getSmApplInquiry().getInquiryText() == null 
						|| this.getManagedBean().getSmApplInquiry().getInquiryText().equals("")){
					RequestContext.getCurrentInstance().update("inquiryMessageId");
					throw new EpException(MessageConst.ERROR_721105);

				}

				//check the string length of get Inquiry Text
				if(this.getManagedBean().getSmApplInquiry().getInquiryText().length() > inquiryTextLength){
					String label = SmCommonUtil.getMessage("lbl.sm.supportingdoc.inquiry",null);
					throw new EpException(MessageConst.ERROR_721114, new String[] {label, String.valueOf(inquiryTextLength)});
				}
			}


			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
			ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISdoReviewSO.class);
			reqDTO.setApplId(this.getManagedBean().getAppId());
			reqDTO.setOutcome(resAction);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			
			String assignedPOName = "";
			reqDTO.setSmAppl(this.getManagedBean().getSmAppl());
			if(resAction.equalsIgnoreCase(SupplierManagementConst.BPM_VERIFY)){
				assignedPOName = setPOJobRoleWeigtage(reqDTO);
			}
			reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
			updateMandatoryAndNonMandatorySuppDocToApplDoc();
			
			reqDTO.setToken(token);
			reqDTO.setOutcome(resAction);
			
			//3460 - Start
			if ((!getManagedBean().getSmRemark().getRemark().equalsIgnoreCase(""))) {
				SmRemark smRmk = new SmRemark();
				smRmk.setDocId(this.getManagedBean().getAppId());
				smRmk.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SDOR);
				PmRole pmRole = new PmRole();
				pmRole.setRoleCode(RoleCode.UPS_SDO.value());
				smRmk.setPmRole(pmRole);
				smRmk.setRemark(this.getManagedBean().getSmRemark().getRemark());
				reqDTO.setSmRemark(smRmk);
			}
			//3460 - End 
			
			this.getManagedBean().getSmApplInquiry().setSmAppl(this.getManagedBean().getSmAppl());
			reqDTO.setSmApplInquiry(this.getManagedBean().getSmApplInquiry());
			reqDTO.setChangedBy(new FacesScopes().getUserSessionBean().getUserId());

			SdoReviewResDTO resDTO = reqSO.submitBPMSdoReview(reqDTO);

			if (resDTO.isSuccessful()) {			
				RequestContext.getCurrentInstance().execute("inquiryDlg.hide()");
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				if(resAction.equalsIgnoreCase(SupplierManagementConst.BPM_INQUIRY)){
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(MessageConst.INFO_721105,
							new String[] {getManagedBean().getSmAppl().getApplNo(),getManagedBean().getSmAppl().getSmSupplier().getCompanyName()},MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
				}else{
					
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(MessageConst.INFO_721104,
							new String[] {getManagedBean().getSmAppl().getApplNo(), assignedPOName},MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
				}
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				RequestContext.getCurrentInstance().execute("inquiryDlg.hide()");
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
			}
		}
		return null;
	}

	public String getAssignedPOName(String docNo) {
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setDocNumber(docNo);
		reqDTO.setStatus(20104);
		
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		BpmResponseDTO resDTO = bpmSO.getTasks(reqDTO);		
		
		if(resDTO != null && resDTO.getWorklistVO() != null 
				&& resDTO.getWorklistVO().getWorklistItem() != null 
				&& !resDTO.getWorklistVO().getWorklistItem().isEmpty()) {
			
			if(resDTO.getWorklistVO().getWorklistItem().get(0).getAssignees() != null
					&& !resDTO.getWorklistVO().getWorklistItem().get(0).getAssignees().isEmpty()) {
				
				String assignedPOLoginId = resDTO.getWorklistVO().getWorklistItem().get(0).getAssignees().get(0);
				PmUser assignedPO = PmCommonUtil.retrieveUserByLoginId(assignedPOLoginId);
				if(assignedPO != null) {
					return  " - " + assignedPO.getUserName();
				}
			}
		}
		return "";
	}
	
	/**
	 * Show the dialog based on action
	 * -if action is "VERIFY", show submission confirmation dialog
	 * -if action is "INQUIRY", populate the invalid/ unsubmitted doc and show the inquiry dialog
	 * @param action
	 * 
	 */
	public void showSdoActionDialog(String action){
		if(action.equalsIgnoreCase(SupplierManagementConst.BPM_VERIFY)){
			RequestContext.getCurrentInstance().execute("submissionDlg.show()");
			
		}else if(action.equalsIgnoreCase(SupplierManagementConst.BPM_INQUIRY)){
			Map<Long, SmApplSupportDoc> supportingDocMap = this.getManagedBean().getSupportingDocMap();
			if(!supportingDocMap.isEmpty()){
				StringBuilder enquiry = new StringBuilder("");
				int count = 1;
				for(Map.Entry<Long, SmApplSupportDoc> suppDoc : supportingDocMap.entrySet()){
					enquiry.append(count + ". " + supportingDocMap.get(suppDoc.getKey()).getPmDocument().getDocName());
					enquiry.append(System.getProperty("line.separator"));
					count++;
				}
				this.getManagedBean().getSmApplInquiry().setInquiryText(enquiry.toString());
			}
			RequestContext.getCurrentInstance().execute("inquiryDlg.show()");
		}
	}
	
	/**
	 * inquiry row select
	 */
	public void inquiryRowSelect(){
		RequestContext.getCurrentInstance().update("resOutPanelId");

	}
	
	/**
	 * update unselectedSuppLs when user check/ uncheck the receive status
	 * @param SmApplSupportDoc applSuppDoc
	 */
	public void receiveStatusChange(SmApplSupportDoc applSuppDoc){
		List<SmApplSupportDoc> unselectedSuppDocLs = this.getManagedBean().getUnselectedSmApplSuppDocs();
		
		if(applSuppDoc.isReceived()){
			unselectedSuppDocLs.remove(applSuppDoc);
			this.getManagedBean().getSupportingDocMap().remove(applSuppDoc.getApplSupportDocId());
		}else{
			String zoneCode = this.getManagedBean().getZoneCode();
			if(zoneCode.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.PM_ZONE_SABAH) 
					|| zoneCode.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.PM_ZONE_SARAWAK)){
				if(!applSuppDoc.getSmApplDocFiles().isEmpty()){
					unselectedSuppDocLs.add(applSuppDoc);
				}else{
					if(applSuppDoc.getPmDocument().isMandatory()){
						this.getManagedBean().getUnselectedSmApplSuppDocs().add(applSuppDoc);
					}
				}
			}else{
				if(applSuppDoc.getPmDocument().isMandatory()){
					unselectedSuppDocLs.add(applSuppDoc);
				}
			}
			
			this.getManagedBean().getSupportingDocMap().put(applSuppDoc.getApplSupportDocId(), applSuppDoc);
		}
		
	}
	
	/**
	 * get Supplier Application Type description
	 * @param applType
	 * @return String
	 */
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
	
	/**
	 * show Verify Confirmation dialog box
	 */
	public void showVerifyConfirmation(){
		RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show()");
	}
	
	/**
	 * download the uploaded supporting doc
	 * @param fileName
	 * @param filePath
	 */
	public void downloadSuppDoc(String fileName, String filePath) {
		file = null;
		String finalFilePath = filePath + fileName;
		try {
			file = commonDownload(finalFilePath, fileName);
		} catch (IOException e) {
			log.error("File:SdoReviewBackingBean, Method:downloadSuppDoc, Message: ", e);
		}
		finalFilePath = null;
	}
	
	/**
	 * method to get the streamed content
	 * @return StreamedContent
	 */
	public StreamedContent getFile(){
		return file;
	}
	
	/**
	 * retrieve zone by state id
	 * @param stateId
	 * @return String
	 */
	private String retrieveZoneByStateId(Long stateId){
		IZoneSO reqSO = (IZoneSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
		ZoneReqDTO reqDTO = new ZoneReqDTO();
		reqDTO.setStateId(stateId);
		
		ZoneResDTO resDTO = reqSO.retrieveZoneByStateId(reqDTO);
		
		return resDTO.getPmZone().getZoneCode();
	}
	
	/**
	 * Initialize latest supporting docs to mandatory and non mandatory document
	 * list
	 */
	private void initNonMandatoryAndMandatoryDocs() {
		if (getManagedBean().getSmAppl() != null
				&& !EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
						.getSmApplSupportDocs())) {
			getManagedBean()
					.setMandatoryDocs(new ArrayList<SmApplSupportDoc>());
			getManagedBean().setNonMandatoryDocs(
					new ArrayList<SmApplSupportDoc>());
			for (SmApplSupportDoc doc : getManagedBean().getSmAppl()
					.getSmApplSupportDocs()) {
				if (doc.getPmDocument() != null) {
					if (doc.getPmDocument().isMandatory()) {
						getManagedBean().getMandatoryDocs().add(doc);
					} else {
						getManagedBean().getNonMandatoryDocs().add(doc);
					}
				}
			}
		}
	}

	/**
	 * Update mandatory and non mandatory documents back to application main
	 * list
	 */
	private void updateMandatoryAndNonMandatorySuppDocToApplDoc() {
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getNonMandatoryDocs())) {
			for (SmApplSupportDoc doc : getManagedBean().getNonMandatoryDocs()) {
				EntityHelper.addOrUpdateEntityToList(doc, getManagedBean()
						.getSmAppl().getSmApplSupportDocs());
			}
		}
		if (!EntityHelper
				.isCollectionEmpty(getManagedBean().getMandatoryDocs())) {
			for (SmApplSupportDoc doc : getManagedBean().getMandatoryDocs()) {
				EntityHelper.addOrUpdateEntityToList(doc, getManagedBean()
						.getSmAppl().getSmApplSupportDocs());
			}
		}
	}
	
	/**
	 * Action to Receive all supporting document.
	 */
	public void receiveAllSuppDoc() {
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getMandatoryDocs())) {
			for (SmApplSupportDoc doc : getManagedBean().getMandatoryDocs()) {
				doc.setReceived(true);
				this.receiveStatusChange(doc);
			}
		}
	}

	private String setPOJobRoleWeigtage(SdoReviewReqDTO reqDTO){
		String processingOfficer = null;
		if(!getManagedBean().getSmAppl().isResubmit()) {
			PmUser poUser = SmCommonUtil.getInstance().retrievePOWithLowestWeightage(getManagedBean()
					.getSmAppl().getSupplierType(), getManagedBean()
					.getSmAppl().getApplType(), getManagedBean()
					.getSmAppl().getApplCategoryType(), getManagedBean().getZoneCode());
			reqDTO.setPmUser(poUser);
			((SMMOFRegDataType)this.getManagedBean().getBpmPojo()).setPoUsers(poUser.getLoginId());	
			
			processingOfficer = poUser.getUserName();
			reqDTO.setPmUser(poUser);
			reqDTO.getSmAppl().setPoUserId(poUser.getUserId());
			setPendingWeightage(reqDTO);
		}else{
			PmUser poUser = null;
			String payloadPO = ((SMMOFRegDataType)this.getManagedBean().getBpmPojo()).getPoUsers();
			String loginId = StringUtils.isNotBlank(payloadPO) ? payloadPO.trim() : null;
			if(loginId == null){
				poUser = SmCommonUtil.getInstance().retrievePOWithLowestWeightage(getManagedBean()
						.getSmAppl().getSupplierType(), getManagedBean()
						.getSmAppl().getApplType(), getManagedBean()
						.getSmAppl().getApplCategoryType(),getManagedBean().getZoneCode());
			}else{
				poUser = SmCommonUtil.getInstance().retrievePOByUserIdAndJobTask(loginId, 
						getManagedBean().getSmAppl().getSupplierType(), getManagedBean()
						.getSmAppl().getApplType(), getManagedBean()
						.getSmAppl().getApplCategoryType(), getManagedBean().getZoneCode());
			}

			processingOfficer = poUser.getUserName();
			((SMMOFRegDataType)this.getManagedBean().getBpmPojo()).setPoUsers(poUser.getLoginId());	
			
			reqDTO.setPmUser(poUser);
			reqDTO.getSmAppl().setPoUserId(poUser.getUserId());
		}
		
		
		return processingOfficer;
	}

	
	private void setPendingWeightage(SdoReviewReqDTO reqDTO){
		Integer jobRatioWeight = SmCommonUtil.getInstance().retrievePoJobTypeWeight(getManagedBean()
				.getSmAppl().getSupplierType(), getManagedBean()
				.getSmAppl().getApplType(), getManagedBean()
				.getSmAppl().getApplCategoryType());
		PmPendingPoWeightage pendingWeightage = new PmPendingPoWeightage();
		pendingWeightage.setPmUser(reqDTO.getPmUser());
		pendingWeightage.setWeightage(jobRatioWeight);
		pendingWeightage.setApplId(reqDTO.getApplId());
		pendingWeightage.setRecordStatusFlg(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
		reqDTO.setPendingPoWeightage(pendingWeightage);
	}

}
