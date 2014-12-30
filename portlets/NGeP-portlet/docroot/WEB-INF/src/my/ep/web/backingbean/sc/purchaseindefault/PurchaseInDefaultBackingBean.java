/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.purchaseindefault;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.dtl.IContractItemSO;
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
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPIDApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScPIDRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPIDWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefaultAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpNtfBusinessDelegate;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sc.directpurchase.PurchaseInDefaultManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
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
	private PmDigiSign pmDigiSign;
	private String textBeforeSign;
	
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
			ISearchBean<VCmSupplierItem> viewSearchBean = null;
			IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			IContractItemSO contractItemSO	=	(IContractItemSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
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
				 retrieveReasonDescListByCategory(true);
			}else if(vCmSupplierItem.getCircularNo() != null){
				// panel item 
				PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
				FacesScopes facesScope = new FacesScopes();
				Long userId = facesScope.getUserSessionBean().getUserId();
				purchaseInDefaultReqDTO.setPanelItemId(vCmSupplierItem.getDocId());
				purchaseInDefaultReqDTO.setFetchSubEntity(true);
				PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO.getPanelSupplierByPanelItem(purchaseInDefaultReqDTO);
				CtPanelItem ctPanelItem = resDTO.getCtPanelItem();
				if(ctPanelItem != null) {
					scPurchaseInDefault.setPanelItem(ctPanelItem);
					getManagedBean().setCheckContractItem(Boolean.FALSE);
					purchaseInDefaultReqDTO.setPanelItem(ctPanelItem);
					purchaseInDefaultReqDTO.setUserId(userId);
					PurchaseInDefaultResDTO defaultResDTO = purchaseInDefaultSO.getIsExceedMaxProcurementAmount(purchaseInDefaultReqDTO);
				    retrieveReasonDescListByCategory(defaultResDTO.isExceedProcurementAmt());
				}
			}
			scPurchaseInDefault.setPurchaseInDefaultNo(purchaseInDefaultSO.generatePIDNo());
			
			// WorkFlow Status
			ScPIDWorkFlowStatus workFlowStatus = new ScPIDWorkFlowStatus();
			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(DPStatusConstants.PID_PENDING_SUBMISSION);
			StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
			workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
			workFlowStatus.setPurchaseInDefault(scPurchaseInDefault);
			//scPurchaseInDefault.setCurrentStatus(workFlowStatus);
			getCurrentStatus(workFlowStatus.getPmStatus());  

			// initializing remarks
			scPurchaseInDefault.setRemarks(initializeRemarks(
					scPurchaseInDefault,
					 null,
					scPurchaseInDefault.getRemarks(),
					ScPIDRemark.class));

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
			String extensionCode = vCmSupplierItem.getItemCode();
			Long uomId = vCmSupplierItem.getUomId();
			StringBuilder sb = new StringBuilder();
			sb.append("itemCode^").append(extensionCode).append("|")//item Code
			.append("catType^").append("XC").append("|")
			.append("uomId^").append(uomId);
			viewSearchBean = new CatalogueBackingBean().getManagedBean().getSearchDataModel().getSearchBean();
			List<BaseSearchFilterBean> filterBeans = viewSearchBean.getDataFilterBeans();

			for (BaseSearchFilterBean baseFilterBean : filterBeans) {
		    	if (baseFilterBean instanceof SearchFilterBean) {
		    		SearchFilterBean filterBean = (SearchFilterBean) baseFilterBean;
		    		sb.append("|")
		    		.append(filterBean.getSearchField()).append("^");			    		
		    		if(filterBean.getSearchOperator() == SearchOperator.OPERATOR_IN.value()) {
		    			sb.append(filterBean.getSearchValue().toString().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\s","")).append("^");
		    		} else {
		    			sb.append(filterBean.getSearchValue()).append("^");
		    		}
		    		
		    		sb.append(filterBean.getSearchOperator());
		    	}
		    }
		    getManagedBean().setConditionField(sb.toString());
			getManagedBean().setvCmSupplierItem(vCmSupplierItem);
			
			initialInformation(scPurchaseInDefault);
			
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
		    	if(scPurchaseInDefault.getApprovalUsers() == null || scPurchaseInDefault.getApprovalUsers().isEmpty()){
		    		scPurchaseInDefault.setApprovalUsers(createTargetApprover(scPurchaseInDefault));
		    	} else{
		    		scPurchaseInDefault.getApprovalUsers().addAll(createTargetApprover(scPurchaseInDefault));
		    	}
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
				
				FacesScopes facesScopes = new FacesScopes();
				// validate  quantity
		    	if(scPurchaseInDefault.getQuantity() != null && scPurchaseInDefault.getQuantity().intValue() < 0){
					throw new EpException(MessageConst.ERROR_100033, new String[] {ResourceBundleUtils.getMessage("lbl.sc.order.qty",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()), "0"});
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
					
					/*if(scPurchaseInDefault.getReasonType().getReasonCode().equalsIgnoreCase(ParameterConst.PID_EQ)){
						if(scPurchaseInDefault.getContractedItem().getMinimumOrderQty() > scPurchaseInDefault.getQuantity().doubleValue()){
							throw new EpException(MessageConst.ERROR_100033, new String[] {ResourceBundleUtils.getMessage("lbl.sc.order.qty",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()), ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default.min.qty",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
						}
					}*/
				}
				else if(scPurchaseInDefault.getPanelItem() != null) {
					reqDTO.setContractType(SourcingConstants.PID_TYPE_PANEL);
					int checkQty = 0;
					
					if(scPurchaseInDefault.getQuantity() != null){
						checkQty = Double.compare(scPurchaseInDefault.getQuantity().doubleValue() , scPurchaseInDefault.getPanelItem().getMinimumOrderQty());
					}
                  /*
					if(scPurchaseInDefault.getReasonType().getReasonCode().equalsIgnoreCase(ParameterConst.PID_EQ)){
						if(checkQty < 0) {
							throw new EpException(MessageConst.ERROR_100033, new String[] {ResourceBundleUtils.getMessage("lbl.sc.order.qty",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()), ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default.min.qty",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
						}
					}*/
					if(scPurchaseInDefault.getReasonType().getReasonCode().equalsIgnoreCase(ParameterConst.PID_RI)){
						if(checkQty > 0) {
							throw new EpException(MessageConst.ERROR_760203, new String[] {ResourceBundleUtils.getMessage("lbl.sc.order.qty",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()), ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default.min.qty",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
						}
					}
				}
				reqDTO.setPurchaseInDefaultDtl(scPurchaseInDefault);
				//Hoa: add token authenticate to support BPM
				String token = facesScopes.getBPMAuthenticationCtxToken();
				reqDTO.setToken(token);
				PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO.submitPurchaseInDefault(reqDTO);
				if (resDTO.isSuccessful()) {
					ScPurchaseInDefault purchaseInDefault = resDTO.getPurchaseInDefaultDtl();
					 successMessage(MessageConst.INFO_700006, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, purchaseInDefault.getPurchaseInDefaultNo());
					// clear pid data
					super.renewSesionObject();
					
				} else {
					throw new EpException(MessageConst.ERROR_700020);
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
			UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);

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
   
   
	/**
	 * @param action
	 * @param object
	 * @return PurchaseInDefaultResDTO
	 */
	private PurchaseInDefaultResDTO updateBPMTask(String outcome, PmDigiSign digiSign) {
		 PurchaseInDefaultResDTO resDTO = null;
		 IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
		   try {
			   PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
			   ScPurchaseInDefault scPurchaseInDefault = getManagedBean().getPurchaseInDefault();
			   // remarks
			   scPurchaseInDefault.setRemarks(initializeRemarks(
						scPurchaseInDefault,
						scPurchaseInDefault.getCurrentStatus(),
						scPurchaseInDefault.getRemarks(),
						ScPIDRemark.class));
			   
			   if(!BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
				   finalizeRemarks(scPurchaseInDefault.getRemarks());
			   }
			   
			   if (null != digiSign) {
					updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, 
							scPurchaseInDefault.getPurchaseInDefaultId(), scPurchaseInDefault.getPurchaseInDefaultNo());
				}
			   reqDTO.setPurchaseInDefaultDtl(scPurchaseInDefault);
			   FacesScopes facesScopes = new FacesScopes();
			   String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
			   reqDTO.setToken(ctxToken);
			   reqDTO.setTaskId(getManagedBean().getTaskId());
			   reqDTO.setOutcome(outcome);
			   reqDTO.setDigiSignData(digiSign);
			   resDTO = purchaseInDefaultSO.updatePurchaseInDefault(reqDTO);
				 
			   if(resDTO.isSuccessful()){
				   if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)){
					   Long statusId = getManagedBean().getPidStatus().getStatusId();
					   
					   if(DPStatusConstants.PID_PENDING_VERIFICATION.equals(statusId)){
						   successMessage(MessageConst.INFO_700005, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault().getPurchaseInDefaultNo()); 
					   } else  if(DPStatusConstants.PID_PENDING_APPROVAL.equals(statusId)){
						   successMessage(MessageConst.INFO_700007, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault().getPurchaseInDefaultNo());
					   }
				   } else if(BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
					   successMessage(MessageConst.INFO_700008, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault().getPurchaseInDefaultNo());
				   }

			   } else {
				   throw new EpException(MessageConst.ERROR_700020);
			   }
			   
		   } finally {
			   purchaseInDefaultSO = null;
		   }
			
		return resDTO;
	}

	public void approverAction(String outcome) {
		debugLogger("approverAction");
		this.getManagedBean().setAction(outcome);
		if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)){
			 getManagedBean().setAutoUpdate(Boolean.FALSE);
			if(isDigiSigningRequired(RoleCode.RN_APPROVER.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

			} else if(isDigiSigningRequired(RoleCode.BPK_APPROVER.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
				
			} else if(isDigiSigningRequired(RoleCode.CT_APPROVER.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}
		} else {
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			updateBPMTask(outcome, null);
		}	
	}
   
   
   /**
    * To get Approver Pick List  in Purchase In Default
    */
   private	List<ScPIDApprovalUser> createTargetApprover(ScPurchaseInDefault purchaseInDefault){
	   List<ScPIDApprovalUser> results = new ArrayList<ScPIDApprovalUser>();
	   List<Long> userIds = new ArrayList<Long>();
	   if(purchaseInDefault.getApprovalUsers() != null && purchaseInDefault.getApprovalUsers().size()>0){
		   for (ScPIDApprovalUser scRequestNoteApprovalUser : purchaseInDefault.getApprovalUsers() ) {
			   userIds.add(scRequestNoteApprovalUser.getPmUser().getUserId());
		   }
	   }

	   if (this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0) {
		   for (PmUser pmUser :  getManagedBean().getApprovalUsers().getTarget()) {
			   if(userIds.isEmpty()){
				   ScPIDApprovalUser approver = new ScPIDApprovalUser();
				   approver.setPmUser(pmUser);
				   approver.setPurchaseInDefault(purchaseInDefault);
				   results.add(approver);
			   } else {
				   if(!userIds.contains(pmUser.getUserId())){
					   ScPIDApprovalUser approver = new ScPIDApprovalUser();
					   approver.setPmUser(pmUser);
					   approver.setPurchaseInDefault(purchaseInDefault);
					   purchaseInDefault.getApprovalUsers().add(approver);
					   results.add(approver);
				   } 
			   }
		   }
	   }
	   return results;
   }

   /**
    * To get status for pid in Purchase In Default
    */
   public void  getCurrentStatus(PmStatus pmStatus) {
	   getManagedBean().setPidStatus(pmStatus);
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

   public void retrieveReasonDescListByCategory (boolean isExceedProcAmt) {
	   ReasonReqDTO reqDTO = new ReasonReqDTO();
	   reqDTO.setReasonCategory(ReasonCategory.PID_JUSTIFICATION.value());
	   reqDTO.setLanguage("BM");
	   IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
	   ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);

	   List<PmReasonDesc>  pmReasonDescs = new ArrayList<PmReasonDesc>();
	   if(resDTO.getReasonDescList() != null && resDTO.getReasonDescList().size()>0){

		   if(isExceedProcAmt){
			   getManagedBean().setJustifications(getDropdownCodeValues(resDTO.getReasonDescList()));
		   } else {
			   for (PmReasonDesc pmReasonDesc : resDTO.getReasonDescList()) {
					   if(pmReasonDesc.getPmReason().getReasonCode().equals(ParameterConst.PID_OT)){
						   pmReasonDescs.add(pmReasonDesc);   
					   }

					   if(pmReasonDesc.getPmReason().getReasonCode().equals(ParameterConst.PID_QR)){
						   pmReasonDescs.add(pmReasonDesc);   
					   }
			   }
		   }

		   if(pmReasonDescs.size()>0){
			   getManagedBean().setJustifications(getDropdownCodeValues(pmReasonDescs));  
		   }
	   }
	   getManagedBean().setPmReason(resDTO.getReasonDescList().get(0).getPmReason());
   }

   public SelectItem[] getDropdownCodeValues(List<PmReasonDesc> list) {
	   SelectItem[] items = null;
	   FacesScopes face = new FacesScopes();
	   try {
		   items = new SelectItem[list.size()+1];
		   items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
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
		   
		   List<ScPurchaseInDefaultAttachment> attachments = new ArrayList<ScPurchaseInDefaultAttachment>();
		   ScPurchaseInDefaultAttachment attachment = purchaseInDefault.getScPurchaseInDefaultAttachment();
		   attachments.add(attachment);
		   getManagedBean().setTaskId(taskId);
		   getManagedBean().setProcessName(res.getProcessName());
		   getManagedBean().setOutComes(res.getAction());
		   getManagedBean().setPurchaseInDefault(purchaseInDefault);
		   getManagedBean().setAttachments(attachments);
		   
		   IStatusSO statusSO = null;
			try {
				
				statusSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				
				StatusReqDTO statusReqDTO = new StatusReqDTO();
				statusReqDTO.setStatusId(res.getDocStatus().longValue());
				
				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				
				getCurrentStatus(statusResDTO.getPmStatus());
				
			} finally {
				statusSO = null;
			}

			// remarks
			purchaseInDefault.setRemarks(initializeRemarks(
					   purchaseInDefault,
					   purchaseInDefault.getCurrentStatus(),
						purchaseInDefault.getRemarks(),
						ScPIDRemark.class));

		   initialInformation(purchaseInDefault);
		   retrieveReasonDescListByCategory(true);
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
		
	 public void gpkiSignAction(PmDigiSign digiSign) {
		 updateBPMTask(this.getManagedBean().getAction(), digiSign);
	 }

	 public PmDigiSign getPmDigiSign() {
		 return pmDigiSign;
	 }

	 public void setPmDigiSign(PmDigiSign pmDigiSign) {
		 this.pmDigiSign = pmDigiSign;
	 }

	 public String getTextBeforeSign() {
		 StringBuilder sb = new StringBuilder();
		 sb.append(getManagedBean().getPurchaseInDefault().getPurchaseInDefaultNo());
		 sb.append(new FacesScopes().getUserSessionBean().getUserId());
		 textBeforeSign = sb.toString();
		 return textBeforeSign;
	 }

	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
	
	public void viewMarketResearch(ScPurchaseInDefault scPurchaseInDefault) throws IOException {
		debugLogger("viewMarketResearch");
		ScRequestItem requestItem = null;

		if (scPurchaseInDefault != null) {
			FacesScopes facesScopes = new FacesScopes();	
			Map<String, String> paramMap = new HashMap<String, String>();
			String reportName = ReportConst.SC_MARKET_RESEARCH_RPT_JSP;

			paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");

			if(scPurchaseInDefault.isBrandNew()) {
			
				 VCmSupplierItem vCmSupplierItem = getManagedBean().getvCmSupplierItem();
				 long  uomId = vCmSupplierItem.getUomId();
				 String  itemCode   = vCmSupplierItem.getItemCode();
				 String conditionField = getManagedBean().getConditionField();

				for (String filter: conditionField.split("\\|")){
					System.out.println(filter);
					String searchField = null;
					String searchValue = null;
					int searchCondition = SearchOperator.OPERATOR_EQUALS.value();
					String[] conditions = filter.split("\\^");
					searchField = conditions[0];
					searchValue = conditions[1];
					if(conditions.length > 2) {
						searchCondition = Integer.parseInt(conditions[2]);
					}
					if ("itemName".equals(searchField)) {
						paramMap.put(ReportConst.PARAM_ITEM_NAME, searchValue);	
					} else if ("companyName".equals(searchField)) {
						paramMap.put(ReportConst.PARAM_SUPPLIER_NAME, searchValue);	
					} else if ("epNo".equals(searchField)) {
						paramMap.put(ReportConst.PARAM_EP_NO, searchValue);	
					} else if ("bizRegNo".equals(searchField)) {
						paramMap.put(ReportConst.PARAM_BIZ_REG_NO, searchValue);	
					} else if ("stateId".equals(searchField)) {
						paramMap.put(ReportConst.PARAM_STATE_ID, searchValue);	

					} else if ("districtId".equals(searchField)) {
						paramMap.put(ReportConst.PARAM_DISTRICT_ID, searchValue);	
					} else if ("mofNo".equals(searchField)) {
						paramMap.put(ReportConst.PARAM_MOF_NO, searchValue);	
					} 
					paramMap.put(ReportConst.PARAM_ITEM_CODE, itemCode);	
					paramMap.put(ReportConst.PARAM_UOM_ID, uomId + "");	
					System.out.println(searchField + "|" + searchValue + "|" + searchCondition);
				}
				paramMap.put(ReportConst.PARAM_IS_SAVED_FLAG, 0 + "");

			}
			else {
				if(scPurchaseInDefault.getPanelItem() != null){
				  requestItem =	scPurchaseInDefault.getPanelItem().getScRequestItem();
				} else if(scPurchaseInDefault.getContractedItem() != null){
				  requestItem = scPurchaseInDefault.getContractedItem().getScRequestItem();
				}

				paramMap.put(ReportConst.PARAM_REQUEST_ITEM_ID, requestItem.getRequestNoteItemId() + "");
				paramMap.put(ReportConst.PARAM_IS_SAVED_FLAG, 1 + "");
			}

			generateReport(reportName, paramMap);
		}
	} 
	
}