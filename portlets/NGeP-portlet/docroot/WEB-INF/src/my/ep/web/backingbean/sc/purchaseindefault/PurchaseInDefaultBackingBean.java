/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.purchaseindefault;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.ct.supplier.PanelSupplierReqDTO;
import my.ep.dto.ct.supplier.PanelSupplierResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.ntf.dto.NtfRequestDTO;
import my.ep.ntf.dto.NtfResponseDTO;
import my.ep.ntf.service.INtfSO;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPIDApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScPIDRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPIDWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefaultAttachment;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpNtfBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.directpurchase.PurchaseInDefaultManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
/**
 * 
 * @author sri
 * since 01.15.2014
 */
@ManagedBean(name = "purchaseInDefaultBackingBean")
@RequestScoped
public class PurchaseInDefaultBackingBean extends SourcingBaseBackingBean<PurchaseInDefaultManagedBean> {
	private static Logger log = Logger.getLogger(PurchaseInDefaultBackingBean.class);
	public PurchaseInDefaultBackingBean() {
		super();
		if (MDC.get(LogKey.USER_ID) == null) {
			PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			User user = (User) request.getAttribute(WebKeys.USER);
			if (user != null) {
				Long userName = user.getUserId();
				new LogKey(userName);
				if (log.isDebugEnabled()) {
					log.debug("userName: " + userName);
				}
			}
		}
	}
	
	 /**
	  * pricingDetails for getting pricing details in PID
	  * @return 
	  */
		public String pidAction(VCmSupplierItem vCmSupplierItem){
			debugLogger("pidAction");
			IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			IContractItemSO contractItemSO	=	(IContractItemSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
			IPanelSupplierSO panelSupplierSO	=	(IPanelSupplierSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IPanelSupplierSO.class);
			ScPurchaseInDefault scPurchaseInDefault = new ScPurchaseInDefault(); 
			//TODO check contract item or panel item
			if(vCmSupplierItem.getContractNo() !=null){
				// contract item
				ContractItemReqDTO contractItemReqDTO = new ContractItemReqDTO();
				contractItemReqDTO.setContractItemId(vCmSupplierItem.getDocId());
				contractItemReqDTO.setFetchSubEntity(Boolean.TRUE);
				ContractItemResDTO contractItemResDTO = contractItemSO.getContractItem(contractItemReqDTO);
				if(contractItemResDTO.getCtContractItem()!=null) {
					scPurchaseInDefault.setContractedItem(contractItemResDTO.getCtContractItem());
					getManagedBean().setCheckContractItem(Boolean.TRUE);
				}
			}else if(vCmSupplierItem.getCircularNo() !=null){
				// panel item 
				PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
				reqDTO.setPanelItemId(vCmSupplierItem.getDocId());
				PanelSupplierResDTO resDTO= panelSupplierSO.retrievePanelItem(reqDTO);
				if(resDTO.getCtPanelItem()!=null) {
					scPurchaseInDefault.setPanelItem(resDTO.getCtPanelItem());
					getManagedBean().setCheckContractItem(Boolean.FALSE);
				}
			}
			// initializing remarks
			scPurchaseInDefault.setRemarks(initializeRemarks(scPurchaseInDefault));
			scPurchaseInDefault.setPurchaseInDefaultNo(purchaseInDefaultSO.generatePIDNo());
			// WorkFlow Status
			List<ScPIDWorkFlowStatus> wflowStatus =  new  ArrayList<ScPIDWorkFlowStatus>();
			ScPIDWorkFlowStatus workFlowStatus = new ScPIDWorkFlowStatus();
			PmStatus status = new PmStatus();
			status.setStatusId(DPStatusConstants.PID_PENDING_SUBMISSION);
			StatusReqDTO statusReqDTO = new StatusReqDTO();
			if (status != null) {
				statusReqDTO.setStatusId(status.getStatusId());
				StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
				workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
			}
			workFlowStatus.setCurrent(true);
			workFlowStatus.setPurchaseInDefault(scPurchaseInDefault);
			//wflowStatus.add(workFlowStatus);
			scPurchaseInDefault.setWorkFlowStatuses(wflowStatus);
			getCurrentStatus(status.getStatusId());  

			// to get ptj
			PmUser user = new FacesScopes().getUserSessionBean();
			if (user != null) {
				IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(user.getUserId());
				UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
				if(resDTO.getUserOrgList() != null && resDTO.getUserOrgList().size()>0){
					scPurchaseInDefault.setPmOrgProfile(resDTO.getUserOrgList() .get(0).getPmOrgProfile());
				}
			}

			initialInformation(scPurchaseInDefault);
			retrieveReasonDescListByCategory();
			
			getManagedBean().setPurchaseInDefault(scPurchaseInDefault);
			// by default value as cheaper price
			PmReason reason = new PmReason();
			reason.setReasonCode(ParameterConst.PID_CP);
			getManagedBean().getPurchaseInDefault().setReasonType(reason);
			
			return "purchaseInDefaultMain";
		}	
		 
    /**
	 *  submitPurchaseInDefault  method to submit PID details 
     *  in Purchase In Default
	*/	
		public String submitPurchaseInDefault() {
			debugLogger("submitPurchaseInDefault");
			IPurchaseInDefaultSO purchaseInDefaultSO = null;
			try {
				purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
				PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
				 IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
				  ReasonReqDTO reasonReqDTO = new ReasonReqDTO();
				ScPurchaseInDefault scPurchaseInDefault = getManagedBean().getPurchaseInDefault();
				scPurchaseInDefault.setRecordStatus(Boolean.TRUE);
				scPurchaseInDefault.setCreatedDate(DateUtils.getCurrentDate());
				
				// Target Approver
				scPurchaseInDefault.setApprovalUsers(createTargetApprover(scPurchaseInDefault));
				// WorkFlow Status 
				scPurchaseInDefault.setWorkFlowStatuses(null);
				
				if(getManagedBean().getAttachments() != null){
					scPurchaseInDefault.setScPurchaseInDefaultAttachment(getManagedBean().getAttachments().get(0));
				}
				
				// justification
				if(scPurchaseInDefault.getReasonType().getReasonCode() != null){
					reasonReqDTO.setReasonCategory(ReasonCategory.PID_JUSTIFICATION.value());
					reasonReqDTO.setLanguage("BM");
					ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reasonReqDTO);
					for (PmReasonDesc reasonDesc : resDTO.getReasonDescList()) {
						if(scPurchaseInDefault.getReasonType().getReasonCode().equalsIgnoreCase(reasonDesc.getPmReason().getReasonCode())){
							scPurchaseInDefault.setReasonType(reasonDesc.getPmReason());
						}
					}
				}
				
				// validate  quantity
				
		    	if(scPurchaseInDefault.getQuantity() != null && scPurchaseInDefault.getQuantity().intValue() < 0){
		    		throw new EpException(MessageConst.INFO_100033, new String[]{"Quatity" ,"0"});
		    	}
				
				if(scPurchaseInDefault.getContractedItem() != null) {
					if(scPurchaseInDefault.getContractedItem().getCtContractVer().getCtContract().getContractType() != null){
						if(scPurchaseInDefault.getContractedItem().getCtContractVer().getCtContract().
								getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
							reqDTO.setContractType(SourcingConstants.PID_TYPE_MINISTRY);
						}
						else if(scPurchaseInDefault.getContractedItem().getCtContractVer().getCtContract().
								getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_CENTRAL)){
							reqDTO.setContractType(SourcingConstants.PID_TYPE_CENTRAL);
						}
					}
				}
				else if(scPurchaseInDefault.getPanelItem() != null) {
					reqDTO.setContractType(SourcingConstants.PID_TYPE_PANEL);
				}
				reqDTO.setPurchaseInDefaultDtl(scPurchaseInDefault);
				//Hoa: add token authenticate to support BPM
				FacesScopes facesScopes = new FacesScopes();
				String token = facesScopes.getBPMAuthenticationCtxToken();
				reqDTO.setToken(token);
				PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO.submitPurchaseInDefault(reqDTO);
				if (resDTO.isSuccessful()) {
					ScPurchaseInDefault purchaseInDefault = resDTO.getPurchaseInDefaultDtl();
					 successMessage(MessageConst.INFO_700006, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, purchaseInDefault.getPurchaseInDefaultNo());
					// clear pid data
					super.renewSesionObject();
					
				} else {
					throw new EpException("Failed.");
				}
			} finally {
				purchaseInDefaultSO = null;
			}
			return null;
		}
		
		public void sendNotificationEmail(){
			debugLogger("sendNotificationEmail");
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getManagedBean().getPurchaseInDefault().getCreatedBy());
			UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);

			INtfSO ntfSO = (INtfSO) EpNtfBusinessDelegate.getInstance().getOSBServiceObject(INtfSO.class);
			FacesScopes facesScopes = new FacesScopes();
			NtfRequestDTO reqDTO = new NtfRequestDTO();
			reqDTO.setTemplateId("400000");
			reqDTO.setSenderId(userResDTO.getPmUser().getUserId().toString());

			List<String> userIdLst = new ArrayList<String>();
			userIdLst.add(getManagedBean().getPurchaseInDefault().getCreatedBy().toString());
			//userIdLst.add("1");
			reqDTO.setEmailToArr(userIdLst.toArray(new String[userIdLst.size()]));
			List<String> subParaLst = new ArrayList<String>();
			subParaLst.add(getManagedBean().getPurchaseInDefault().getPurchaseInDefaultNo());
			reqDTO.setSubParaArr(subParaLst.toArray(new String[subParaLst.size()]));
			// Template variables
			List<String> cntParaLst = new ArrayList<String>();
			cntParaLst.add(userResDTO.getPmUser().getUserName());
			cntParaLst.add(getManagedBean().getPurchaseInDefault().getPurchaseInDefaultNo());
			cntParaLst.add(facesScopes.getUserSessionBean().getUserName());
			reqDTO.setCntParaArr(cntParaLst.toArray(new String[cntParaLst.size()]));
			try {
				NtfResponseDTO resDTO = ntfSO.processAndPublishViaEmail(reqDTO);
				debugLogger(resDTO.getStatusCode());
				debugLogger(resDTO.getSeverity());
				debugLogger(resDTO.getStatusDesc());
			} finally{
			}
			debugLogger("END");
		}


   public void approverAction(String outcome) {
	   IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
	   try {
		   PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
		   ScPurchaseInDefault scPurchaseInDefault = getManagedBean().getPurchaseInDefault();
		   // remarks
		   scPurchaseInDefault.setRemarks(initializeRemarks(scPurchaseInDefault));
		   if(!BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
			   if(scPurchaseInDefault.getRemarks() != null ){
				   for (int i = 0; i < scPurchaseInDefault.getRemarks().size(); i++) {
					   ScPIDRemark remark = scPurchaseInDefault.getRemarks().get(i);
					   if(StringUtils.isBlank(remark.getRemark())){
						   scPurchaseInDefault.getRemarks().remove(remark);
					   }	
				   } 
			   }
		   }
		   reqDTO.setPurchaseInDefaultDtl(scPurchaseInDefault);
		   FacesScopes facesScopes = new FacesScopes();
		   String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		   reqDTO.setToken(ctxToken);
		   reqDTO.setTaskId(getManagedBean().getTaskId());
		   reqDTO.setOutcome(outcome);
		   PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO.updatePurchaseInDefault(reqDTO);
			 
		   if(resDTO.isSuccessful()){
			   if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)){
				   if(getManagedBean().getPidStatus().equalsIgnoreCase(ParameterConst.PID_PENDING_VERIFICATION)){
					   successMessage(MessageConst.INFO_700005, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault().getPurchaseInDefaultNo()); 
				   } else  if(getManagedBean().getPidStatus().equalsIgnoreCase(ParameterConst.PID_PENDING_APPROVAL)){
					   successMessage(MessageConst.INFO_700007, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault().getPurchaseInDefaultNo());
				   }
			   } else if(BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
				   successMessage(MessageConst.INFO_700008, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault().getPurchaseInDefaultNo());
			   }

		   } else {
			   throw new EpException("Failed.");
		   }
		   
	   } finally {
		   purchaseInDefaultSO = null;
	   }
   } 

   /**
    * initializing remarks in Purchase In Default
    */
   private List<ScPIDRemark> initializeRemarks(ScPurchaseInDefault scPurchaseInDefault) {
	   IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
	   FacesScopes facesScopes = new FacesScopes();
	   List<ScPIDRemark> results =   scPurchaseInDefault.getRemarks();
		if(results == null){
			results = new ArrayList<ScPIDRemark>();
		} 
	   
	   if (results.size() == 0
			   || !compareUserName( results.get(0).getCreatedUser().getLoginId())) {
		   ScPIDRemark remark = new ScPIDRemark();
		   remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		   remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		   remark.setChangedBy(remark.getCreatedBy());
		   remark.setCreatedUser(facesScopes.getUserSessionBean());
		   if (getManagedBean().getPurchaseInDefault().getRemarks() != null && getManagedBean().getPurchaseInDefault().getRemarks().size()>0) {
			   remark.setRemark(getManagedBean().getPurchaseInDefault().getRemarks().get(0).getRemark());
		   } 
		   PmStatus status = new PmStatus();
		   status.setStatusId(DPStatusConstants.PID_PENDING_SUBMISSION);
		   StatusReqDTO statusReqDTO = new StatusReqDTO();
		   if (status != null) {
			   statusReqDTO.setStatusId(status.getStatusId());
			   StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
			   remark.setPmStatus(statusResDTO.getPmStatus());
		   }
		   remark.setPmStatus(status);
		   remark.setPurchaseInDefault(scPurchaseInDefault);
		   results.add(0, remark);
	   }
	   return results;
   }

   /**
    * To get Approver Pick List  in Purchase In Default
    */
   private	List<ScPIDApprovalUser> createTargetApprover(ScPurchaseInDefault purchaseInDefault){
	   List<ScPIDApprovalUser> results = new ArrayList<ScPIDApprovalUser>();
	   for (PmUser pmUser :  getManagedBean().getApprovalUsers().getTarget()) {
		   ScPIDApprovalUser scPIDApprovalUser = new ScPIDApprovalUser();
		   scPIDApprovalUser.setPmUser(pmUser);
		   scPIDApprovalUser.setPurchaseInDefault(purchaseInDefault);
		   results.add(scPIDApprovalUser);
	   }
	   return results;
   }

   /**
    * To get status for pid in Purchase In Default
    */
   public void  getCurrentStatus(Long statusId) {
	   if (statusId != null) {
		   getManagedBean().setPidStatus(getStatusDescById(statusId));
	   }
   }   
   /**
    * Event for justification type drop down in pid
    */
   public void justificationTypeEvent() {
	   log.info("justificationTypeEvent : "+getManagedBean().getPurchaseInDefault().getReasonType().getReasonId()); 
	   if(getManagedBean().getPurchaseInDefault().getReasonType().getReasonCode() !=null){
		   if (getManagedBean().getPurchaseInDefault().getReasonType().getReasonCode().equalsIgnoreCase(ParameterConst.PID_CP) || getManagedBean().getPurchaseInDefault().getReasonType().getReasonCode().equalsIgnoreCase(ParameterConst.PID_SS)) {
			   getManagedBean().setVisibleOrderQty(Boolean.FALSE);

		   } else if (getManagedBean().getPurchaseInDefault().getReasonType().getReasonCode().equalsIgnoreCase(ParameterConst.PID_RI) || getManagedBean().getPurchaseInDefault().getReasonType().getReasonCode().equalsIgnoreCase(ParameterConst.PID_EQ) ) {
			   getManagedBean().setVisibleOrderQty(Boolean.TRUE);

		   } else {
			   getManagedBean().setVisibleOrderQty(Boolean.FALSE);
		   }
	   }
	   // update order qty
	   RequestContext.getCurrentInstance().update("reqDtlId");
   }

   /**
    *  handleFileUpload event for file upload in pid
    */
   public void handleFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
	   debugLogger("handleFileUpload");
	   FacesScopes facesScopes = new FacesScopes();
	   List<ScPurchaseInDefaultAttachment> attachments = new ArrayList<ScPurchaseInDefaultAttachment>();
	   if (event.getFile() != null){
		   ScPurchaseInDefault purchaseInDefault = getManagedBean().getPurchaseInDefault();
		   ScPurchaseInDefaultAttachment attachment = getManagedBean().getPurchaseInDefault().getScPurchaseInDefaultAttachment();
		   if (null == attachment) {
			   attachment = new ScPurchaseInDefaultAttachment();
			   attachment.setScPurchaseInDefault(purchaseInDefault);
		   }
		   
		   attachment.setDocNo(purchaseInDefault.getPurchaseInDefaultNo());
		   attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		   attachment.setCreatedUser(facesScopes.getUserSessionBean());
		  
		   uploadAttachment(event.getFile(), attachment);
		   attachments.add(attachment);
		   getManagedBean().setAttachments(attachments);

	   }
   }

   /**
    *  determineIcon for displaying image icon in  pid
    */	
   public String determineIcon(ScPurchaseInDefaultAttachment attachment) {
	   debugLogger("determineIcon");
	   String imageFileName = "icon_file_default.png";
	   String ext = "";

	   if(attachment != null){
		   String fileName = attachment.getFileName();

		   if(fileName != null){
			   int mid= fileName.lastIndexOf(".");
			   ext = fileName.substring(mid+1,fileName.length());  
		   }

		   if ("doc".equals(ext) || "docx".equals(ext)){
			   imageFileName ="icon_doc.png";
		   }
		   else if ("pdf".equals(ext)){
			   imageFileName ="icon_pdf.png";
		   }
		   else if("xls".equals(ext) || "xlsx".equals(ext) || "csv".equals(ext)){
			   imageFileName ="icon_exel.png";
		   }
		   else if("jpg".equals(ext) || "bmp".equals(ext) || "png".equals(ext) || "gif".equals(ext)){
			   imageFileName ="icon_image.png";
		   }
		   else if("txt".equals(ext)){
			   imageFileName ="icon_txt.png";
		   }
	   }
	   return imageFileName;
   }

   public void retrieveReasonDescListByCategory () {
	   ReasonReqDTO reqDTO = new ReasonReqDTO();
	   reqDTO.setReasonCategory(ReasonCategory.PID_JUSTIFICATION.value());
	   reqDTO.setLanguage("BM");
	   IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
	   ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);
	   getManagedBean().setJustifications(getDropdownCodeValues(resDTO.getReasonDescList()));
	   getManagedBean().setPmReason(resDTO.getReasonDescList().get(0).getPmReason());
   }

   public SelectItem[] getDropdownCodeValues(List<PmReasonDesc> list) {
	   SelectItem[] items = null;
	   try {
		   items = new SelectItem[list.size()+1];
		   items[0] = new SelectItem(null, "- Select One -");
		   int i = 1;
		   for(PmReasonDesc reasonDesc : list) {
			   items[i++] = new SelectItem(
					   reasonDesc.getPmReason().getReasonCode(),
					   reasonDesc.getReasonDesc());
		   }
	   } finally{
	   }
	   return items;
   }

   public void initialInformation(ScPurchaseInDefault purchaseInDefault){
	   DualListModel<PmUser>	approvalUsers;
	   IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	   UserReqDTO userReqDto = new UserReqDTO();
	   userReqDto.setRoleCode(RoleCode.RN_APPROVER.value());
	   if(purchaseInDefault.getPmOrgProfile() != null){
		   userReqDto.setOrgProfileId(purchaseInDefault.getPmOrgProfile().getOrgProfileId());
	   }
	   UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
	   List<PmUser> availableApprover	=	userResDTO.getUserList();
	   List<PmUser> selectedApprover	=	new ArrayList<PmUser>();
	   approvalUsers =	new DualListModel<PmUser>(availableApprover, selectedApprover);

	   if (purchaseInDefault.getApprovalUsers().size() > 0
			   && purchaseInDefault != null) {
		   for (int i = 0; i < purchaseInDefault.getApprovalUsers().size(); i++) {
			   selectedApprover.add(purchaseInDefault.getApprovalUsers().get(i).getPmUser());
			   for (int j = 0; j < availableApprover.size(); j++) {
				   if (purchaseInDefault.getApprovalUsers().get(i).getPmUser().getUserId()
						   .equals(availableApprover.get(j).getUserId())) {
					   availableApprover.remove(availableApprover.get(j));
				   }
			   }
		   }
		   approvalUsers = new DualListModel<PmUser>(availableApprover, selectedApprover);
		   getManagedBean().setApprovalUsers(approvalUsers);
	   } else {
		   approvalUsers = new DualListModel<PmUser>(availableApprover, selectedApprover);
		   getManagedBean().setApprovalUsers(approvalUsers);
	   }
   }

   /**
    * to get Ptj drop down Listing in purchase in default
    */
   private void populatePtjDropDownList() {
	   debugLogger("populatePtjDropDownList");
	   PmUser user = new FacesScopes().getUserSessionBean();
	   if (user != null) {
		   OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		   reqDTO.setUserId(user.getUserId());
		   reqDTO.setOrgType(OrganizationType.DIVISION.value());

		   IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				   .getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						   IOrgProfileSO.class);
		   OrgProfileResDTO resDTO = orgProfileSO
				   .retrieveOrgProfileListByOrgType(reqDTO);

		   List<PmOrgProfile> ptjList = resDTO.getOrgProfileList();
		   System.out.println("userDivisionList" + ptjList.size());
		   //getManagedBean().setPtjDropdown(ptjList);
	   }
   }

   /**
    * render the view base on the task id
    */
   public void preRenderView() {
	   HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
	   HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
	   String taskId = originalServletRequest.getParameter("taskId");
	   if (StringUtils.isNotBlank(taskId)) {
		   this.getManagedBean().setTaskId(taskId);
		   log.info("taskId : " + taskId);
		   IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		   FacesScopes facesScopes = new FacesScopes();
		   String token = facesScopes.getBPMAuthenticationCtxToken();
		   BpmRequestDTO reqDTO = new BpmRequestDTO();
		   reqDTO.setCtxToken(token);
		   reqDTO.setTaskId(taskId);
		   BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		   this.getManagedBean().setOutComes(res.getAction());
		   this.getManagedBean().setProcessName(res.getProcessName());
		   Object obj = res.getPojo();
		   SCDirectPurchaseDataType directPurchaseDataType = (SCDirectPurchaseDataType) obj;
		   // Purchase In Default	
		   IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
		   PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
		   Long pidID = Long.valueOf(directPurchaseDataType.getDpId());
		   purchaseInDefaultReqDTO.setPurchaseInDefaultId(pidID);
		   purchaseInDefaultReqDTO.setFetchSubEntity(true);
		   PurchaseInDefaultResDTO purchaseInDefaultResDTO = purchaseInDefaultSO.getPurchaseInDefault(purchaseInDefaultReqDTO);
		   ScPurchaseInDefault purchaseInDefault = purchaseInDefaultResDTO.getPurchaseInDefaultDtl();
		   // contract item
		   if(purchaseInDefault.getContractedItem() != null){
			/*  CtContractItem contractItem = purchaseInDefault.getContractedItem();
			   IContractItemSO contractItemSO	=	(IContractItemSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
			   ContractItemReqDTO contractItemReqDTO = new ContractItemReqDTO();
			   contractItemReqDTO.setContractItemId(contractItem.getContractItemId());
			   ContractItemResDTO ConIresDTO = contractItemSO.getContractItem(contractItemReqDTO);
			   contractItem	=	ConIresDTO.getCtContractItem();
			   purchaseInDefault.setContractedItem(contractItem);*/
			   getManagedBean().setCheckContractItem(Boolean.TRUE);
		   }
		   // panel item
		   if(purchaseInDefault.getPanelItem() !=null){
			   getManagedBean().setCheckContractItem(Boolean.FALSE);
		   }
		   
		   // remarks
		   purchaseInDefault.setRemarks(initializeRemarks(purchaseInDefault));
		   List<ScPurchaseInDefaultAttachment> attachments = new ArrayList<ScPurchaseInDefaultAttachment>();
		   ScPurchaseInDefaultAttachment attachment = purchaseInDefault.getScPurchaseInDefaultAttachment();
		   attachments.add(attachment);
		   getManagedBean().setTaskId(taskId);
		   getManagedBean().setProcessName(res.getProcessName());
		   getManagedBean().setOutComes(res.getAction());
		   getManagedBean().setPurchaseInDefault(purchaseInDefault);
		   getManagedBean().setAttachments(attachments);
		   getCurrentStatus(res.getDocStatus().longValue()); 

		   initialInformation(purchaseInDefault);
		   retrieveReasonDescListByCategory();
		   justificationTypeEvent();
		   // title checker
		   getManagedBean().setTitleChecker(Boolean.FALSE);
		   FacesContext fc = FacesContext.getCurrentInstance();
		   fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "purchaseInDefaultMain");
	   }
   }

   /**
    *  deleteUploadFile method for deleting upload fine in pid
    */
   public void deleteUploadFile() {
	   debugLogger("deleteUploadFile");
	   getManagedBean().getAttachments().clear();
   }

	 /**
	  * Page navigation to catalogue search page in Purchase in default
	  */
	 public void backToPervious() {
		 log.info("backToPervious");
		 FacesContext fc = FacesContext.getCurrentInstance();
		 if(getManagedBean().getPageNavToRequestNote() != null){
			 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getPageNavToRequestNote());  
		 } else {
			   // clear data
				super.renewSesionObject();
			 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "catalogueSearch");  
		 }
	 }		
	 private void debugLogger(Object message) {
		 if (log.isDebugEnabled()) {
			 log.debug(message);
		 }
		 log.info(message);
	 }
}