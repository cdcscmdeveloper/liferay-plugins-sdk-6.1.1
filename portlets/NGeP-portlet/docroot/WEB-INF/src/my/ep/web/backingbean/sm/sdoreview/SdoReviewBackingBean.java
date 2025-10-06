package my.ep.web.backingbean.sm.sdoreview;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.biz.service.sm.supportingdocinquiry.ISupportingDocInquirySO;
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
import my.ep.dto.sm.supportingdocinquiry.SupportingDocInquiryResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPendingPoWeightage;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.persistence.entity.sm.SmApplInquiryDocFile;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmApplSupportDocInquiry;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.type.pm.RoleCode;
import my.ep.util.BpmCommonUtils;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.ai.SmAIDocBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sdoreview.SdoReviewManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "sdoReviewBackingBean")
@RequestScoped
public class SdoReviewBackingBean extends BaseBackingBean<SdoReviewManagedBean> {
	private static final long serialVersionUID = 1L;

	private final static Logger log = Logger.getLogger(SdoReviewBackingBean.class);
	private StreamedContent file;
	private static final int inquiryTextLength = 2500;
	//#11641 - Start
	private int mandatoryCounter = 1;
	private int nonMandatoryCounter = 1;
	private boolean mandatoryEmpty = false;
	private boolean nonMandatoryEmpty = false;
	private boolean mandatoryAllReceived = false;
	private boolean nonMandatoryAllReceived = false;
	//#11641 - End
	
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
		
		//#28807 - START
		log.info("#28807 supp doc total after sort -> " + this.getManagedBean().getSmAppl().getApplNo() + " -> " + this.getManagedBean().getSmAppl().getSmApplSupportDocs().size());
		int filesCount = 0;
		for(SmApplSupportDoc smApplSupportDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
			for(SmApplDocFile smApplDocFile : smApplSupportDoc.getSmApplDocFiles()){
				if(!smApplDocFile.getFileName().isEmpty() || 
					smApplDocFile.getFileName() != null || 
					!smApplDocFile.getFileName().equalsIgnoreCase("")){
					filesCount+=1;
				}
			}
		}
		log.info("#28807 total sup doc files -> " + this.getManagedBean().getSmAppl().getApplNo() + " -> " + filesCount);
		//#28807 - END
		
		// show rejection reason for resubmit application
		if (getManagedBean().getSmAppl().isResubmit()
				&& getManagedBean().getSmAppl().getOriginalAppl() != null) {
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			mofReqDTO.setSmAppl(getManagedBean().getSmAppl());
			SmAppl latestRejectedAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveLatestRejectedApplication(mofReqDTO).getSmAppl();
			
			//	Refs #15629 - Start - 5/2/2021
			//start 30974
			if(!EntityHelper.isCollectionEmpty(latestRejectedAppl
					.getSmApplRejectReasons()) && latestRejectedAppl.getPmStatus() != null && SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER.equalsIgnoreCase(latestRejectedAppl.getPmStatus().getStatusId().toString())){

				Iterator<SmApplRejectReason> smApplRejectReasonIter = latestRejectedAppl
						.getSmApplRejectReasons().iterator();
			    while(smApplRejectReasonIter.hasNext()){
			    	SmApplRejectReason smApplRejectReason = smApplRejectReasonIter.next();
			        if(smApplRejectReason.isReasonByPo()){
			        	smApplRejectReasonIter.remove();
			          }
			      }
			}
			//End 30974
			// Refs #15629 - End - 5/2/2021
			
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
				//#11641 - START
				if(!applSuppDocSbhSrk.isReceived()){
					this.getManagedBean().getSupportingDocMap().put(applSuppDocSbhSrk.getApplSupportDocId(), applSuppDocSbhSrk);
				}
				//#11641 - END
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
				//#11641 - START
				if(!applSuppDoc.isReceived()){
					this.getManagedBean().getSupportingDocMap().put(applSuppDoc.getApplSupportDocId(), applSuppDoc);
				}
				//#11641 - END
			}
		}
		
		//retrieve Supplier GST Info
		SmGstSupplier gstSupp = SmCommonUtil.getSmGstSupplierBySupplierIdAndBranchCode(getManagedBean().getSmAppl().getSmSupplier().getSupplierId(), null, null,true);
		getManagedBean().setSmGstSupplier(gstSupp);
		
		
		//initialized mandatory and non mandatory supporting documents
		initNonMandatoryAndMandatoryDocs();
		SmAIDocBackingBean.validateBank(getManagedBean().getSmAppl()); //#38890
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
			
			//#11641 - Start
			this.getManagedBean().getSmApplInquiry().setApplInquiryId(reqSO.retrieveSeqNextVal("SM_APPL_INQUIRY_SEQ"));
			List<SmApplSupportDocInquiry> smApplSupportDocInquiries = new ArrayList<SmApplSupportDocInquiry>();
			List<SmApplInquiryDocFile> smApplInquiryDocFiles = new ArrayList<SmApplInquiryDocFile>();
			
			for(SmApplSupportDoc smApplSupportDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
				if(!smApplSupportDoc.isReceived()){
					
					SmApplSupportDocInquiry smApplSupportDocInquiry = new SmApplSupportDocInquiry();
					smApplSupportDocInquiry.setPmDocument(smApplSupportDoc.getPmDocument());
					smApplSupportDocInquiry.setSdoRemarks(smApplSupportDoc.getInquiryRemark());
					smApplSupportDocInquiry.setReceived(smApplSupportDoc.isInquiryReceived());
					smApplSupportDocInquiry.setSmApplInquiry(this.getManagedBean().getSmApplInquiry());
					smApplSupportDocInquiry.setApplSupportDocInquiryId(reqSO.retrieveSeqNextVal("SM_APPL_SUPPDOC_INQUIRY_SEQ"));
					
					smApplSupportDoc.setInquiryRemark("");
					
					for(SmApplDocFile smApplDocFile : smApplSupportDoc.getSmApplDocFiles()){
						
						SmApplInquiryDocFile smApplInquiryDocFile = new SmApplInquiryDocFile();
						smApplInquiryDocFile.setApplInquiryDocFileId(reqSO.retrieveSeqNextVal("SM_APPL_INQ_FILE_SEQ"));
						smApplInquiryDocFile.setFileName(smApplDocFile.getFileName());
						smApplInquiryDocFile.setFilePath(smApplDocFile.getFilePath());
						smApplInquiryDocFile.setSmApplSupportDocInquiry(smApplSupportDocInquiry);
						
						smApplInquiryDocFiles.add(smApplInquiryDocFile);
					}
					
					smApplSupportDocInquiry.setSmApplInquiryDocFiles(smApplInquiryDocFiles);
					smApplSupportDocInquiries.add(smApplSupportDocInquiry);
				}
				
			}
			this.getManagedBean().getSmApplInquiry().setSmApplSupportDocInquiries(smApplSupportDocInquiries);
			//#11641 - End
			
			String assignedPOName = "";
			reqDTO.setSmAppl(this.getManagedBean().getSmAppl());
			if(resAction.equalsIgnoreCase(SupplierManagementConst.BPM_VERIFY)){
				assignedPOName = setPOJobRoleWeigtage(reqDTO);
			}
			
			// 29810 - start - add new data type in SMMOFRegDataType - saufi
			SMMOFRegDataType bpmObj = (SMMOFRegDataType) this.getManagedBean().getBpmPojo();
			if(this.getManagedBean().getSmAppl().getSdoReceivedDate() != null){
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
				String dateStr = df.format(this.getManagedBean().getSmAppl().getSdoReceivedDate());	
				bpmObj.setReceivedDate(BpmCommonUtils.getCalendarValue(this.getManagedBean().getSmAppl().getSdoReceivedDate()));
			}
			
			reqDTO.setBpmPojo(bpmObj);
			//29810 - end
			
			//29810 - to check receive date value is there
			log.info("29810 ====== SdoReviewBackingBean ========= getReceivedDate " + ((SMMOFRegDataType) reqDTO.getBpmPojo()).getReceivedDate());
			
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
			reqDTO.setChangedBy(getUserIdSessionFromLoginId()); // #31849 add log key

			SdoReviewResDTO resDTO = reqSO.submitBPMSdoReview(reqDTO);

			if (resDTO.isSuccessful()) {			
				RequestContext.getCurrentInstance().execute("inquiryDlg.hide()");
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				if(resAction.equalsIgnoreCase(SupplierManagementConst.BPM_INQUIRY)){
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(MessageConst.INFO_721105,
							new String[] {getManagedBean().getSmAppl().getApplNo(),getManagedBean().getSmAppl().getSmSupplier().getCompanyName()},MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
					//14530
					sendEmailtoSupplier(reqDTO);
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
	
	//3460 - Start
	public void changePlainTxt(String txt) throws Exception {
		SmRemark smRmk = new SmRemark();
		txt = txt.trim();
		byte[] bytes = txt.getBytes(StandardCharsets.US_ASCII);
		String asciiEncodedString = new String(bytes, StandardCharsets.US_ASCII);
		int length = asciiEncodedString.length();
		if (length > 4000) {
			asciiEncodedString = asciiEncodedString.substring(0, 4000);
			log.info("THIS IS THE LENGTH OF THE AFTER CUT TEXT>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
					+ asciiEncodedString.length());
			smRmk.setRemark(asciiEncodedString);
			this.getManagedBean().setSmRemark(smRmk);
		} else {
			log.info("THIS IS THE LENGTH OF ACTUAL TEXT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + length);
			smRmk.setRemark(asciiEncodedString);
			this.getManagedBean().setSmRemark(smRmk);
		}
	}
	//3460 - End

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
		//#11641 - START
		this.getManagedBean().setEmptyMandatoryDocHistory(true);
		this.getManagedBean().setEmptyNonMandatoryDocHistory(true);
		//#34392 - start
		resetSmApplSupportDocInquiries();
		//#34392 - end
		for(SmApplSupportDocInquiry historySupportDoc: this.getManagedBean().getSelectedInquiryResponse().getSmApplSupportDocInquiries()){
			if(historySupportDoc.getPmDocument().isMandatory()){
				this.getManagedBean().setEmptyMandatoryDocHistory(false);
			}
			else if(!historySupportDoc.getPmDocument().isMandatory()){
				this.getManagedBean().setEmptyNonMandatoryDocHistory(false);
			}
		}
		//#11641 - END
		RequestContext.getCurrentInstance().update("resOutPanelId");

	}
	
	//#34392 - start
	/**
	 * re-select inquiry before opening popup
	 */
	public void resetSmApplSupportDocInquiries() {
		ISupportingDocInquirySO reqSO = (ISupportingDocInquirySO) BusinessDelegate.getInstance().getServiceObject("sm",
				ISupportingDocInquirySO.class);
		SupportingDocInquiryResDTO resDTO = reqSO.retrieveInquiryByApplInquiryId(this.getManagedBean()
				.getSelectedInquiryResponse().getApplInquiryId());
		this.getManagedBean().getSelectedInquiryResponse()
				.setSmApplSupportDocInquiries(resDTO.getSmApplInquiry().getSmApplSupportDocInquiries());
	}
	//#34392 - end
	
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
			
			//#30294 - START
			Collections.sort(this.getManagedBean().getSmAppl().getSmApplSupportDocs(), new Comparator<SmApplSupportDoc>(){
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
			//#30294 - END
			//#28807 - START
			log.info("#28807 supp doc total after sort -> " + this.getManagedBean().getSmAppl().getApplNo() + " -> " + this.getManagedBean().getSmAppl().getSmApplSupportDocs().size());
			int filesCount = 0;
			for(SmApplSupportDoc smApplSupportDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
				for(SmApplDocFile smApplDocFile : smApplSupportDoc.getSmApplDocFiles()){
					if(!smApplDocFile.getFileName().isEmpty() || 
						smApplDocFile.getFileName() != null || 
						!smApplDocFile.getFileName().equalsIgnoreCase("")){
						filesCount+=1;
					}
				}
			}
			log.info("#28807 total sup doc files -> " + this.getManagedBean().getSmAppl().getApplNo() + " -> " + filesCount);
			//#28807 - END
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
	
	public boolean isMandatoryAllReceived() {
		int mandatoryCount = 0;
		int mandatoryReceived = 0;
		
		for(SmApplSupportDoc suppDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
			if(suppDoc.getPmDocument().isMandatory()){
				mandatoryCount++;
			}
		}
		for(SmApplSupportDoc suppDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
			if(suppDoc.getPmDocument().isMandatory() && suppDoc.isReceived()){
				mandatoryReceived++;
			}
		}
		if(mandatoryReceived == mandatoryCount){
			this.mandatoryAllReceived = true;
		}else{
			this.mandatoryAllReceived = false;
		}
		return mandatoryAllReceived;
	}

	public boolean isNonMandatoryAllReceived() {
		int nonMandatoryCount = 0;
		int nonMandatoryReceived = 0;
		
		for(SmApplSupportDoc suppDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
			if(!suppDoc.getPmDocument().isMandatory()){
				nonMandatoryCount++;
			}
		}
		for(SmApplSupportDoc suppDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
			if(!suppDoc.getPmDocument().isMandatory() && suppDoc.isReceived()){
				nonMandatoryReceived++;
			}
		}
		if(nonMandatoryReceived == nonMandatoryCount){
			this.nonMandatoryAllReceived = true;
		}else{
			this.nonMandatoryAllReceived = false;
		}
		return nonMandatoryAllReceived;
	}
	
	public void receivedStatus(SmApplSupportDoc smApplSupportDoc){
		if(smApplSupportDoc.isInquiryReceived()){
			for(SmApplSupportDoc smApplSupportDocs : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
				if(smApplSupportDocs.equals(smApplSupportDoc)){
					smApplSupportDocs.setInquiryReceived(false);
					log.info("false");
				}
			}
		}else{
			for(SmApplSupportDoc smApplSupportDocs : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
				if(smApplSupportDocs.equals(smApplSupportDoc)){
					smApplSupportDocs.setInquiryReceived(true);
					log.info("TRUE");
				}
			}
		}
	}
	//#11641 - End
	
	//14530 - Start
		private void sendEmailtoSupplier(SdoReviewReqDTO sdoReviewReqDTO){
			ISdoReviewSO sdoreqSO = SmServiceLookup.getSdoReviewSO();
			SdoReviewResDTO sdoReviewResDTO = sdoreqSO.retrieveApplById(sdoReviewReqDTO);
			SmAppl smappl = sdoReviewResDTO.getSmAppl();
			
			if (smappl.getApplId() == sdoReviewReqDTO.getApplId()){
				try {
					log.info("***sendEmailSDOInquirytoSupplpier - Start*****");
					sdoreqSO.sendEmailSDOInquirytoSupplier(sdoReviewReqDTO);
					log.info("***sendEmailSDOInquirytoSupplpier - Start*****" + sdoReviewReqDTO);
				} catch (Exception ex){
					log.info("sendEmailSDOEnquirytoSupplier ERROR :: " +ex.getMessage());
				}
			}
		}
	//14530 - End
}
