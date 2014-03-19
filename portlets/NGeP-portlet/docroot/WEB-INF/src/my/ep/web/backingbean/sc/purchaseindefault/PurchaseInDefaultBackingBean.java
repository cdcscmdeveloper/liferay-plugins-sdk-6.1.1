/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.purchaseindefault;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPIDApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScPIDRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPIDWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefaultAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.ReasonCategory;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.directpurchase.PurchaseInDefaultManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.FileUploadEvent;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
/**
 * 
 * @author sri
 * 
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
		retrieveReasonDescListByCategory();
	}
	
	 /**
	  * pricingDetails for getting pricing details in PID
	  * @return 
	  */
		public String pidAction(VCmSupplierItem vCmSupplierItem){
			debugLogger("pidAction");
			IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			IContractItemSO contractItemSO	=	(IContractItemSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
			ContractReqDTO ConIreqDTO = new ContractReqDTO();
			ConIreqDTO.setContractItemId(vCmSupplierItem.getDocId());
			ContractResDTO ConIresDTO = contractItemSO.getContractItem(ConIreqDTO);
		    ScPurchaseInDefault scPurchaseInDefault = new ScPurchaseInDefault(); 
			scPurchaseInDefault.setContractedItem(ConIresDTO.getCtContractItem());
		    
			/*IContractSO contractSO	=	(IContractSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractId(1l);
			ContractResDTO resDTO = contractSO.retrieveContract(reqDTO);
			this.getManagedBean().setContract(resDTO.getContract());*/
			
			/*ICatalogueSO catalogueSO	=	(ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
			CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
			catReqDTO.setDocId(vCmSupplierItem.getDocId());
			CatalogueResDTO catResDTO = catalogueSO.searchSupplierItemByStatus(catReqDTO);
			
			if (catResDTO.getSupplierItemList() !=null) {
			   ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			   CmPricing cmPricing = new CmPricing();
			   cmPricing.setCmSupplierItem(catResDTO.getSupplierItemList().get(0));
			   requestSupplierItem.setPricing(cmPricing);
			   requestSupplierItem.setSmSupplier(cmPricing.getCmSupplierItem().getSmSupplier());
			   getManagedBean().setScRequestSupplierItem(requestSupplierItem);*/
			   
			   // initializing remarks
			   scPurchaseInDefault.setRemarks(initializeRemarks(scPurchaseInDefault));
			   scPurchaseInDefault.setPurchaseInDefaultNo(purchaseInDefaultSO.generatePIDNo());
			   // WorkFlow Status
			   List<ScPIDWorkFlowStatus> wflowStatus =  new  ArrayList<ScPIDWorkFlowStatus>();
			   ScPIDWorkFlowStatus workFlowStatus = new ScPIDWorkFlowStatus(SourcingConstants.PRI_PENDING_SUBMISSION);
			   workFlowStatus.setCurrent(true);
			   workFlowStatus.setPurchaseInDefault(scPurchaseInDefault);
			   wflowStatus.add(workFlowStatus);
			   scPurchaseInDefault.setWorkFlowStatuses(wflowStatus);
		       getManagedBean().setPurchaseInDefault(scPurchaseInDefault);
		   //}
			//TODO default value to justification drop down listing
		  	getManagedBean().setJustificationType(ParameterConst.PID_CP);
			return "purchaseInDefaultMain";
		}	
		 
    /**
	 *  submitPurchaseInDefault  method to submit PID details 
     *  in Purchase In Default
	*/	
	 public void submitPurchaseInDefault() {
		  debugLogger("submitPurchaseInDefault");
		  IPurchaseInDefaultSO purchaseInDefaultSO = null;
		  try {
			    purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			    PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
				ScPurchaseInDefault scPurchaseInDefault = getManagedBean().getPurchaseInDefault();
				List<ScPIDWorkFlowStatus> wflowStatuses =  new  ArrayList<ScPIDWorkFlowStatus>();
		 		scPurchaseInDefault.setReasonType("R");
		 		scPurchaseInDefault.setRecordStatus(true);
		 		scPurchaseInDefault.setCreatedDate(DateUtils.getCurrentDate());
				// Target Approver
				ScPIDApprovalUser approvalUser = new ScPIDApprovalUser();
				scPurchaseInDefault.setApprovalUsers(createTargetApprover(approvalUser));
				// Contract Item
				//scPurchaseInDefault.setContractedItem(getManagedBean().getCtContractItem());
				// WorkFlow Status 
				ScPIDWorkFlowStatus workFlowStatus = new ScPIDWorkFlowStatus(SourcingConstants.PID_PENDING_SUBMISSION);
				workFlowStatus.setPurchaseInDefault(scPurchaseInDefault);
				wflowStatuses.add(workFlowStatus);
				scPurchaseInDefault.setWorkFlowStatuses(wflowStatuses);
				// remarks
				scPurchaseInDefault.setRemarks(initializeRemarks(scPurchaseInDefault));
				scPurchaseInDefault.setScPurchaseInDefaultAttachment(getManagedBean().getAttachments().get(0));
				reqDTO.setPurchaseInDefaultDtl(scPurchaseInDefault);
				
				//Hoa: add token authenticate to support BPM
				FacesScopes facesScopes = new FacesScopes();
				String token = facesScopes.getBPMAuthenticationCtxToken();
				reqDTO.setToken(token);
				
				PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO.submitPurchaseInDefault(reqDTO);
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.", ""));
			} catch (Exception ex) {
			    FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"Sorry We are unable to process.", ""));
					ex.printStackTrace();
			} finally {
					purchaseInDefaultSO = null;
			}
			}
			
		/**
		 * initializing remarks in Purchase In Default
		 */
		private List<ScPIDRemark> initializeRemarks(ScPurchaseInDefault scPurchaseInDefault) {
			List<ScPIDRemark> results = new ArrayList<ScPIDRemark>();
			FacesScopes facesScopes = new FacesScopes();
			if (results.size() == 0
					|| !compareUserName(results.get(0).getUser().getLoginId())) {
				ScPIDRemark remark = new ScPIDRemark();
				remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
				remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				remark.setChangedBy(remark.getCreatedBy());
				remark.setChangedDate(remark.getCreatedDate());
				remark.setUser(facesScopes.getUserSessionBean());
				if (getManagedBean().getPurchaseInDefault().getRemarks() != null && getManagedBean().getPurchaseInDefault().getRemarks().size()>0) {
				   remark.setRemark(getManagedBean().getPurchaseInDefault().getRemarks().get(0).getRemark());
				} 
				PmStatus status = new PmStatus();
				status.setStatusId(SourcingConstants.RN_PENDING_SUBMISSION);
				remark.setPmStatus(status);
				remark.setPurchaseInDefault(scPurchaseInDefault);
				results.add(0, remark);
			}
			return results;
		}
		
		/**
		 * To get Approver Pick List  in Purchase In Default
		 */
		private	List<ScPIDApprovalUser> createTargetApprover(ScPIDApprovalUser scPIDApprovalUser){
			List<ScPIDApprovalUser> results = new ArrayList<ScPIDApprovalUser>();
			 scPIDApprovalUser = new ScPIDApprovalUser();
			 for (PmUser pmUser :  getManagedBean().getApprovalUsers().getTarget()) {
				  scPIDApprovalUser.setPmUser(pmUser);
			      results.add(scPIDApprovalUser);
			}
			return results;
		}
		
		/**
		  * To get status for approval in Purchase In Default
		 */
	    public String  getCurrentStatus() {
			String result = "";
			if (getManagedBean().getPurchaseInDefault().getWorkFlowStatuses() != null) {
				for (ScPIDWorkFlowStatus status : getManagedBean()
						.getPurchaseInDefault().getWorkFlowStatuses()) {
					if (status.isCurrent()) {
						result = getStatusDescById(status.getPmStatus()
								.getStatusId());
					    if(result.equalsIgnoreCase("Pending Submission")){
							  getManagedBean().setEditFlag(false);
					    } 
						break;
					}
				}
			}
			return result;
		}
	    
	    /**
		 * Event for justification type drop down in pid
		 */
		public void justificationTypeEvent() {
			log.info("justificationTypeEvent"); 
			
			// TODO  have to change justification type values 
			if (getManagedBean().getJustificationType().equalsIgnoreCase(ParameterConst.PID_CP) || getManagedBean().getJustificationType().equalsIgnoreCase(ParameterConst.PID_SS)) {
				getManagedBean().setVisibleOrderQty(false);
				getManagedBean().setVisibleRemarkAttach(true);
				
			} else if (getManagedBean().getJustificationType().equalsIgnoreCase(ParameterConst.PID_RI) || getManagedBean().getJustificationType().equalsIgnoreCase(ParameterConst.PID_EQ) ) {
				getManagedBean().setVisibleOrderQty(true);
				getManagedBean().setVisibleRemarkAttach(true);
				
			} else {
				getManagedBean().setVisibleOrderQty(false);
				getManagedBean().setVisibleRemarkAttach(true);
			}
		}
		
	   /**
		*  handleFileUpload event for file upload in pid
	   */
		public void handleFileUpload(FileUploadEvent event) {
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
			attachment.setUser(facesScopes.getUserSessionBean());
			attachment.setChangedDate(new Timestamp(System.currentTimeMillis()));
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
			reqDTO.setLanguage("en");
			IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
			ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);
			getManagedBean().setJustifications(getDropdownCodeValues(resDTO.getReasonDescList()));
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
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null, 
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								e.toString(), 
								e.getMessage()));
			}
		    return items;
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
		public String backToPervious() {
			log.info("backToPervious");
			return "catalogueSearch";
		}		
		private void debugLogger(Object message) {
			if (log.isDebugEnabled()) {
				log.debug(message);
			}
			log.info(message);
		}
}