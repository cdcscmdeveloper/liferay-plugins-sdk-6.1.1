/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.directpurchase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.directpurchase.PrInquiryReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryResDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPIDRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPRIRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefaultAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScRNWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSQRemark;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.purchaseindefault.PurchaseInDefaultBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.managedbean.sc.directpurchase.RequestNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
/**
 * 
 * @author sri
 * @since 11.02.2014
 * 
 */
@ManagedBean(name = "requestNoteBackingBean")
@RequestScoped
public class RequestNoteBackingBean extends BaseBackingBean<RequestNoteManagedBean> {
	private static Logger log = Logger.getLogger(RequestNoteBackingBean.class);
	 
	public RequestNoteBackingBean() {
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
	 * saveAction method to save request note details
	 * return String
	 */
	public String saveAction() {
	    IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
    try {
		if (this.getManagedBean().getRequestNoteDtls() != null && getManagedBean().getRequestNoteDtls().size() > 0) {
			requestNote.setRequestNoteDtls(this.getManagedBean().getRequestNoteDtls());
		}
		// Target Approver
		if (this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0) {
			ScRequestNoteApprovalUser approvalUser = new ScRequestNoteApprovalUser();
		    requestNote.setApprovalUsers(createTargetApprover(approvalUser));
		}
		// Remarks
		requestNote.setRemarks(initializeRemarks(requestNote));
		requestNote.setPmOrgProfile(ptjNameEvent());
		requestNoteReqDTO.setRequestNote(requestNote);
		
		requestNote = removeEmptyRemarkBeforeSave(requestNote);
		requestNoteReqDTO.setRequestNote(requestNote);
		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		requestNoteReqDTO.setToken(token);
		RequestNoteResDTO requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
		if(requestNoteResDTO.isSuccessful()){
		  FacesContext fc = FacesContext.getCurrentInstance();
		  fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "acknowledge");
		}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		} finally {
			directPurchaseSO = null;
		}
		return null;
	}
	
	/**
	 * submitAction method to submit request note details
	 * return String
	 */
	public String submitAction() {
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
    try {
		if (this.getManagedBean().getRequestNoteDtls() != null && getManagedBean().getRequestNoteDtls().size() > 0) {
			requestNote.setRequestNoteDtls(this.getManagedBean().getRequestNoteDtls());
		}
		// Target Approver
		if (this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0) {
			ScRequestNoteApprovalUser approvalUser = new ScRequestNoteApprovalUser();
		    requestNote.setApprovalUsers(createTargetApprover(approvalUser));
		}
		 List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
		 ScRNWorkFlowStatus workFlowStatus = new ScRNWorkFlowStatus();
    	 PmStatus status = new PmStatus();
    	 status.setStatusId(SourcingConstants.RN_PENDING_SUBMISSION);
    	 StatusReqDTO statusReqDTO = new StatusReqDTO();
    	 if (status != null) {
    	     statusReqDTO.setStatusId(status.getStatusId());
    		 StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
    		 workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
    	  }
    	 workFlowStatus.setRequestNote(requestNote);
	     workFlowStatus.setCurrent(true);
	     workFlowStatuses.add(workFlowStatus);
	     requestNote.setWorkFlowStatuses(workFlowStatuses);
	     
		// Remarks
		requestNote.setRemarks(initializeRemarks(requestNote));
		requestNoteReqDTO.setRequestNote(requestNote);
		if (getManagedBean().getSourceMethod().equalsIgnoreCase(ParameterConst.METHOD_SC)) {
			   requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
		} else if (getManagedBean().getSourceMethod().equalsIgnoreCase(ParameterConst.METHOD_PRI)) {
			  requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_PRI);
	    } else if (getManagedBean().getSourceMethod().equalsIgnoreCase(ParameterConst.METHOD_PID)) {
			  requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_PID);
		}
	
		requestNoteReqDTO.setRequestNote(requestNote);
		//Hoa: when submit need to set submit in the reqDTO to true
		requestNoteReqDTO.setSubmit(Boolean.TRUE);
		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		requestNoteReqDTO.setToken(token);
		requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
		RequestNoteResDTO requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
		if (requestNoteResDTO.isSuccessful()) {
			 FacesContext fc = FacesContext.getCurrentInstance();
			 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "acknowledge");
		}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		} finally {
			directPurchaseSO = null;
		}
		return null;
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
			String	dpMode	=	directPurchaseDataType.getDpMode();
			
			if(ParameterConst.REQUEST_NOTE_RN.equalsIgnoreCase(dpMode)){
				//RequestNote Details
				IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
				RequestNoteReqDTO	requestDTO	=	new RequestNoteReqDTO();
				requestDTO.setRequestNoteId(Long.valueOf(directPurchaseDataType.getDpId()));
				RequestNoteResDTO	resDTO	=	directPurchaseSO.getRequestNote(requestDTO);
				ScRequestNote	requestNote	=	resDTO.getRequestNoteDetail();
				getManagedBean().setRequestNoteDtls(requestNote.getRequestNoteDtls());
				getManagedBean().setDirectPurchaseDataType(directPurchaseDataType);
				List<ScRequestNoteRemark> results= new ArrayList<ScRequestNoteRemark>();
				for (ScRequestNoteRemark remark : requestNote.getRemarks()) {
					remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
					remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					remark.setChangedBy(remark.getCreatedBy());
					remark.setChangedDate(remark.getCreatedDate());
					remark.setUser(facesScopes.getUserSessionBean());
				    results.add(remark);
		       }
		       requestNote.setRemarks(results);
			   getManagedBean().setRequestNote(requestNote);
			   getManagedBean().setOutComes(res.getAction());
			   getManagedBean().setRequestNoteDtl(requestNote.getRequestNoteDtls().get(0));
			   initialInformation(requestNote);
			   fulfilmentTypeEvent(requestNote.getRequestNoteDtls().get(0));
			   FacesContext fc = FacesContext.getCurrentInstance();
			   fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "commonRequestNote");
			
			} else if(ParameterConst.REQUEST_NOTE_PRI.equalsIgnoreCase(dpMode)){ 
				//Purchase Inquiry
				IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
				PrInquiryReqDTO prInquiryReqDTO = new PrInquiryReqDTO();
				Long prInquiryID = Long.valueOf(directPurchaseDataType.getDpId());
				prInquiryReqDTO.setPrInquiryId(prInquiryID);
				prInquiryReqDTO.setFetchSubEntity(true);
				PrInquiryResDTO prInquiryResDTO = prInquirySO.getPrInquiry(prInquiryReqDTO);
				PurchaseInquiryBackingBean inquiryBackingBean = new PurchaseInquiryBackingBean();
		        ScPrInquiry  prInquiry = prInquiryResDTO.getPrInquiryDetail();
		    	List<ScPRIRemark> results= new ArrayList<ScPRIRemark>();
				for (ScPRIRemark remark : prInquiry.getRemarks()) {
					remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
					remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					remark.setChangedBy(remark.getCreatedBy());
					remark.setChangedDate(remark.getCreatedDate());
					remark.setUser(facesScopes.getUserSessionBean());
				    results.add(remark);
		       }
				 prInquiry.setRemarks(results);
				 
				if(prInquiry.getScRequestSupplierItems() != null && prInquiry.getScRequestSupplierItems().size()>0 ){
					ScRequestSupplierItem requestSupplierItem = prInquiry.getScRequestSupplierItems().get(0);
					ScSupplierItemPrice itemPrice =requestSupplierItem.getSupplierItemPrices().get(0);
					ScRequestNoteDtl requestNoteDtl = requestSupplierItem.getScRequestNoteDtl().get(0);
					requestSupplierItem.setSupplierItemPrice(itemPrice);
					inquiryBackingBean.getManagedBean().setPmOrgProfile(requestSupplierItem.getRequestNoteItem().getRequestNote().getPmOrgProfile());
					inquiryBackingBean.getManagedBean().setRequestNoteDtl(requestNoteDtl);
					prInquiry.setRequestSupplierItem(requestSupplierItem);
					prInquiry.setDayToResponse(ParameterConst.PIQ_RESP_DAYS); // by default days
					
					List<String> priceTypes= new ArrayList<String>();
					priceTypes.add(itemPrice.getPricing().getPricingType());
					inquiryBackingBean.getManagedBean().setPriceType(itemPrice.getPricing().getPricingType());
					inquiryBackingBean.getManagedBean().setPriceTypes(inquiryBackingBean.getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, priceTypes));
				}
				inquiryBackingBean.getManagedBean().setTaskId(taskId);
				inquiryBackingBean.getManagedBean().setProcessName(res.getProcessName());
				inquiryBackingBean.getManagedBean().setOutComes(res.getAction());
				inquiryBackingBean.getManagedBean().setScPrInquiry(prInquiry);
				inquiryBackingBean.initializeRemarks(prInquiry);
				//initialInformation(new ScRequestNote());
				inquiryBackingBean.getCurrentStatus(res.getDocStatus().longValue()); 
			    FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "prInquiryMain");
			
			} else if(ParameterConst.REQUEST_NOTE_PID.equalsIgnoreCase(dpMode)){ 
				// Purchase In Default	
				IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			    PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
		 	    Long pidID = Long.valueOf(directPurchaseDataType.getDpId());
		 	    purchaseInDefaultReqDTO.setPurchaseInDefaultId(pidID);
		 	    purchaseInDefaultReqDTO.setFetchSubEntity(true);
		 	    PurchaseInDefaultResDTO purchaseInDefaultResDTO = purchaseInDefaultSO.getPurchaseInDefault(purchaseInDefaultReqDTO);
		 	    ScPurchaseInDefault purchaseInDefault = purchaseInDefaultResDTO.getPurchaseInDefaultDtl();
		 	    CtContractItem contractItem = purchaseInDefault.getContractedItem();
		 	    IContractItemSO contractItemSO	=	(IContractItemSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
				ContractReqDTO ConIreqDTO = new ContractReqDTO();
				ConIreqDTO.setContractItemId(contractItem.getContractItemId());
				ContractResDTO ConIresDTO = contractItemSO.getContractItem(ConIreqDTO);
				contractItem	=	ConIresDTO.getCtContractItem();
				purchaseInDefault.setContractedItem(contractItem);
				List<ScPIDRemark> results= new ArrayList<ScPIDRemark>();
				for (ScPIDRemark remark : purchaseInDefault.getRemarks()) {
					remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
					remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					remark.setChangedBy(remark.getCreatedBy());
					remark.setChangedDate(remark.getCreatedDate());
					remark.setUser(facesScopes.getUserSessionBean());
				    results.add(remark);
		       }
				purchaseInDefault.setRemarks(results);
				List<ScPurchaseInDefaultAttachment> attachments = new ArrayList<ScPurchaseInDefaultAttachment>();
				ScPurchaseInDefaultAttachment attachment =purchaseInDefault.getScPurchaseInDefaultAttachment();
				attachment.setUser(facesScopes.getUserSessionBean());
				attachment.setChangedDate(new Timestamp(System.currentTimeMillis()));
				attachments.add(attachment);
		 	    
		 	    //get RequestNote Backing bean and manage bean and set Request Note to RequestNote manage bean
				PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				PurchaseInDefaultBackingBean purchaseInDefaultBackingBean = (PurchaseInDefaultBackingBean) portletSession.getAttribute("purchaseInDefaultBackingBean");
				if (purchaseInDefaultBackingBean == null) {
					purchaseInDefaultBackingBean = new PurchaseInDefaultBackingBean();
				}
		 	    
				purchaseInDefaultBackingBean.getManagedBean().setTaskId(taskId);
				purchaseInDefaultBackingBean.getManagedBean().setProcessName(res.getProcessName());
				purchaseInDefaultBackingBean.getManagedBean().setOutComes(res.getAction());
				purchaseInDefaultBackingBean.getManagedBean().setPurchaseInDefault(purchaseInDefault);
				purchaseInDefaultBackingBean.getManagedBean().setAttachments(attachments);
				purchaseInDefaultBackingBean.getManagedBean().setJustificationType(ParameterConst.PID_CP);
				purchaseInDefaultBackingBean.getCurrentStatus(res.getDocStatus().longValue()); 
				purchaseInDefaultBackingBean.initialInformation(purchaseInDefault);
		 	    FacesContext fc = FacesContext.getCurrentInstance();
			    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "purchaseInDefaultMain");
		    } else if(ParameterConst.REQUEST_NOTE_SQ.equalsIgnoreCase(dpMode)){ 
				// Simple Quote
		    	IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
		    	QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
		    	Long quoteId = Long.valueOf(directPurchaseDataType.getDpId());
		    	quoteReqDTO.setQuoteId(quoteId);
		    	quoteReqDTO.setFetchSubEntity(true);
		    	QuoteResDTO  quoteResDTO = quoteSO.getQuoteDtl((quoteReqDTO));
		    	
		    	ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
		    	scQuote.setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
				List<ScSQRemark> results= new ArrayList<ScSQRemark>();
				for (ScSQRemark remark : scQuote.getRemarks()) {
					remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
					remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					remark.setChangedBy(remark.getCreatedBy());
					remark.setChangedDate(remark.getCreatedDate());
					remark.setUser(facesScopes.getUserSessionBean());
				    results.add(remark);
		       }
		 	    
		 	    //get SimpleQuoteBackingBean and manage bean and set simple quote  to simple quote manage bean
				PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				SimpleQuoteBackingBean simpleQuoteBackingBean = (SimpleQuoteBackingBean) portletSession.getAttribute("simpleQuoteBackingBean");
				if (simpleQuoteBackingBean == null) {
					simpleQuoteBackingBean = new SimpleQuoteBackingBean();
				}
				simpleQuoteBackingBean.getManagedBean().setTaskId(taskId);
				simpleQuoteBackingBean.getManagedBean().setProcessName(res.getProcessName());
				simpleQuoteBackingBean.getManagedBean().setOutComes(res.getAction());
				simpleQuoteBackingBean.getManagedBean().setScQuote(scQuote);
				simpleQuoteBackingBean.getManagedBean().setDurationDropdown(ParameterConst.SQ_DURATION_DAY);
				simpleQuoteBackingBean.getManagedBean().setDuration(0);
				simpleQuoteBackingBean.durationEvent();
				simpleQuoteBackingBean.getCurrentStatus(res.getDocStatus().longValue()); 
				simpleQuoteBackingBean.getManagedBean().setPmOrgProfile(scQuote.getRequestNotes().get(0).getPmOrgProfile());
		 	    FacesContext fc = FacesContext.getCurrentInstance();
			    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "simpleQuote");
		    }
			
	}
  }	
	
	public void updateBpmTask(String outcome) {
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		BpmRequestDTO reqBpm = new BpmRequestDTO();
		//Hoa: call approvalRequestNote method to group all the request note dtl. with the same fulfilment type, assigned to, supplier in the same purchase request
		if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)){
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			RequestNoteReqDTO	reqDTO	=	new RequestNoteReqDTO();
			reqDTO.setRequestNoteId(getManagedBean().getRequestNote().getRequestNoteId());
			directPurchaseSO.approvalRequestNote(reqDTO);
		}
		FacesScopes facesScopes = new FacesScopes();
		
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		try {
			reqBpm.setTaskId(getManagedBean().getTaskId());
			reqBpm.setOutcome(outcome);
			if (null != ctxToken) {
				reqBpm.setCtxToken(ctxToken);
				BpmResponseDTO resBpm = bpmSO.updateTask(reqBpm);
				boolean result = resBpm != null ? resBpm.isSuccessful() : false;
				if (resBpm.isSuccessful()) {
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, "acknowledge");
				}
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		} finally {
			bpmSO = null;
		}
	}
	public void applyForRequestNote() {
	   log.info("applyForRequestNote");
	   List<ScRequestNoteDtl>	requestNoteDtls	=	getManagedBean().getRequestNoteDtls();
	   if(getManagedBean().getSelectedRequestNoteDtls() != null) {
		  for(ScRequestNoteDtl selectedReqDtl : getManagedBean().getSelectedRequestNoteDtls()) {
			 if(getManagedBean().getRequestNoteDtls().contains(selectedReqDtl)){
				selectedReqDtl.setFulfillmentType(ParameterConst.FULFILLMENT_ONE_OFF);
				PmUser pmUser = new PmUser();
				pmUser.setUserId(getManagedBean().getAssignedToUserId());
			    selectedReqDtl.setAssignedUser(pmUser);
				selectedReqDtl.setDeliverPeriodType(getManagedBean().getPeriodType());
				selectedReqDtl.setDeliverPeriod(getManagedBean().getPeriodValue());
				requestNoteDtls.add(selectedReqDtl);
				getManagedBean().getRequestNoteDtls().remove(selectedReqDtl);
				getManagedBean().setRequestNoteDtls(requestNoteDtls);
			  }
			}
		}
	}
	
	private void populateUserDivisionList() {
		log.info("populateUserMinistryList");
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(user.getUserId());
			reqDTO.setOrgType(OrganizationType.DIVISION.value());

			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			List<PmOrgProfile> userDivisionList = resDTO.getOrgProfileList();
			getManagedBean().setUserDivisionList(userDivisionList);
		}
	}
	
	private	ScRequestNote	removeEmptyRemarkBeforeSave(ScRequestNote requestList){
		ScRequestNote result	=	requestList;
		List<ScRequestNoteRemark> removeList = new ArrayList<ScRequestNoteRemark>();
		if(requestList.getRemarks() != null){
			for(ScRequestNoteRemark remark: requestList.getRemarks()){
				if(StringUtils.isBlank(remark.getRemark())){
					removeList.add(remark);
				}
			}
			result.getRemarks().removeAll(removeList);
		}
		return result;
	}
	
	/**
	  * initializing remarks in Request Note
	 */
	public List<ScRequestNoteRemark> initializeRemarks(ScRequestNote scRequestNote) {
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		List<ScRequestNoteRemark> results = scRequestNote.getRemarks();
		if(results == null){
			results =  new ArrayList<ScRequestNoteRemark>();
		}
		
		ScRequestNoteRemark remark = new ScRequestNoteRemark();
		FacesScopes facesScopes = new FacesScopes();
		if (results.size() == 0
				|| !compareUserName(results.get(0).getUser().getLoginId())) {
			 remark = new ScRequestNoteRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setUser(facesScopes.getUserSessionBean());
			if (getManagedBean().getRequestNote().getRemarks() != null && getManagedBean().getRequestNote().getRemarks().size()>0) {
			   remark.setRemark(getManagedBean().getRequestNote().getRemarks().get(0).getRemark());
			} else {
				 remark.setRemark("");
			}
			PmStatus status = new PmStatus();
			status.setStatusId(SourcingConstants.RN_PENDING_SUBMISSION);
		    StatusReqDTO statusReqDTO = new StatusReqDTO();
		    if (status != null) {
		    statusReqDTO.setStatusId(status.getStatusId());
		    StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
		    remark.setPmStatus(statusResDTO.getPmStatus());
		   }
		   remark.setPmStatus(status);
		   remark.setRequestNote(scRequestNote);
		   results.add(0, remark);
		} 
		return results;
	}
	
	/**
	 * get AddtoCart details in Add To Cart PopUp
	 */
	public void addToCart(VCmSupplierItem vCmSupplierItem) {
		log.info("addToCart");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
		CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
		catReqDTO.setDocId(vCmSupplierItem.getDocId());
		CatalogueResDTO catResDTO = catalogueSO.searchSupplierItemByStatus(catReqDTO);
		if (catResDTO.getSupplierItemList() != null) {
			ScRequestItem requestItem = new ScRequestItem();
			ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			CmSupplierItem cmSupplierItem = catResDTO.getSupplierItemList().get(0);
		    ScSupplierItemPrice supplierItemPrice = new ScSupplierItemPrice();
			supplierItemPrice.setPricing(cmSupplierItem.getCmPricings().get(0));
			requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
		    requestSupplierItem.setSupplierItemPrice(supplierItemPrice);
			requestSupplierItem.setRequestNoteItem(requestItem);
			getManagedBean().setRequestSupplierItem(requestSupplierItem);
			// TODO check supplier is government seller or not 
			if(cmSupplierItem.getSmSupplier().getPmOrgProfile().isGovtSeller()){
				 getManagedBean().getRequestNoteDtl().setProcurementType(ParameterConst.PROC_TYPE_G2G);
			} else {
				 getManagedBean().getRequestNoteDtl().setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
			}
			if (cmSupplierItem.getCmPricings() != null
					&& cmSupplierItem.getCmPricings().size() > 0) {
				List<String> priceTypes = new ArrayList<String>();
				for (CmPricing princing : cmSupplierItem.getCmPricings()) {
					if (!priceTypes.contains(princing.getPricingType())) {
						priceTypes.add(princing.getPricingType());
					}
				}
				// TODO PriceType drop down list in AddToCart
				getManagedBean().setPriceTypes(
						getPriceTypeDropdownValues(
								ParameterTypeConst.PRICE_TYPE, priceTypes));
			}
		}
		//TODO default value to price type drop down listing
	    getManagedBean().setPriceTypeCode(ParameterConst.PRICE_TYPE_STANDARD);
	    getCurrentStatus(SourcingConstants.RN_PENDING_SUBMISSION);
	}
	
	/**
	 * get AddtoCart details in Add To Cart PopUp in compare catalogue
	 */
	public void compareAddToCart(CmSupplierItem cmSupplierItem) {
		log.info("addToCart");
		 if(cmSupplierItem != null) {
		    ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			ScRequestItem requestItem = new ScRequestItem();
			ScSupplierItemPrice supplierItemPrice = new ScSupplierItemPrice();
			supplierItemPrice.setPricing(cmSupplierItem.getCmPricings().get(0));
			requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
			requestSupplierItem.setSupplierItemPrice(supplierItemPrice);
			requestSupplierItem.setRequestNoteItem(requestItem);
			getManagedBean().setRequestSupplierItem(requestSupplierItem);
			
			if (cmSupplierItem.getCmPricings() != null
					&& cmSupplierItem.getCmPricings().size() > 0) {
				List<String> priceTypes = new ArrayList<String>();
				for (CmPricing princing : cmSupplierItem.getCmPricings()) {
					if (!priceTypes.contains(princing.getPricingType())) {
						priceTypes.add(princing.getPricingType());
					}
				}
				// TODO PriceType drop down list in AddToCart
				getManagedBean().setPriceTypes(getPriceTypeDropdownValues(
						ParameterTypeConst.PRICE_TYPE, priceTypes));
			}
		 }
		  //TODO default value to Procurement type and price type drop down listing
		  getManagedBean().setPriceTypeCode(ParameterConst.PRICE_TYPE_STANDARD);
		  getManagedBean().getRequestNoteDtl().setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
	 }
	
	 public SelectItem[] getPriceTypeDropdownValues(String parameterType,
			List<String> priceTypes) {
		SelectItem[] items = null;
		try {
			items = new SelectItem[priceTypes.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for (String priceTypeCode : priceTypes) {
				items[i++] = new SelectItem(priceTypeCode,getParameterDesc(parameterType,
						priceTypeCode));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}
	
	/**
	 * Page navigation to catalogue search page in Request Note
	 */
	public String backToPervious() {
		log.info("backToPervious");
		return "catalogueSearch";
	}

	public String addToRequestNote(String sourceMethod) {
		log.info("addToRequestNote");
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		if (sourceMethod
				.equalsIgnoreCase(ParameterConst.METHOD_PRI)) {
			PurchaseInquiryBackingBean inquiryBackingBean = new PurchaseInquiryBackingBean();
			CmSupplierItem cmSupplierItem = inquiryBackingBean.getManagedBean()
					.getSupplierItem();
			if (cmSupplierItem != null) {
				ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
				ScSupplierItemPrice supplierItemPrice = new ScSupplierItemPrice();
				supplierItemPrice.setPricing(cmSupplierItem.getCmPricings().get(0));
				requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
				supplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
				requestSupplierItem.setSupplierItemPrice(supplierItemPrice);
				requestSupplierItem.setSmSupplier(cmSupplierItem
						.getSmSupplier());
				getManagedBean().setRequestSupplierItem(requestSupplierItem);
				getManagedBean().setRequestNoteDtl(inquiryBackingBean.getManagedBean().getRequestNoteDtl());
				getManagedBean().setOrgProfileId(inquiryBackingBean.getManagedBean().getOrgProfileId());
			}
		}
		FacesScopes facesScopes = new FacesScopes();
		ScRequestNoteDtl scRequestNoteDtl = new ScRequestNoteDtl();
		scRequestNoteDtl.setFulfillmentType(ParameterConst.FULFILLMENT_ONE_OFF);
		getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
		ScRequestNote requestNote =  getManagedBean().getRequestNote();
		requestNote	=	new ScRequestNote();
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		requestNote.setPmOrgProfile(new PmOrgProfile());
		//Hoa: use getRequestNoteNo() to get Request Note Number
		requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
		if (sourceMethod.equalsIgnoreCase(ParameterConst.METHOD_SC)) {
		    requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
		    getManagedBean().setSourceMethod(sourceMethod);
		    scRequestNoteDtl.setScRequestNote(requestNote);
		    fulfilmentTypeEvent(scRequestNoteDtl);
		} else if (sourceMethod.equalsIgnoreCase(ParameterConst.METHOD_PRI)) {
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_PRI);
			getManagedBean().setSourceMethod(sourceMethod);
		    fulfilmentTypeEvent(scRequestNoteDtl);
			
		}	
		//Hoa: TODO WorkFlow Status should be set In the save, submit method
		 List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
		 ScRNWorkFlowStatus workFlowStatus = new ScRNWorkFlowStatus();
    	 requestNote.setWorkFlowStatuses(workFlowStatuses);
    	 PmStatus status = new PmStatus();
    	 status.setStatusId(SourcingConstants.RN_PENDING_SUBMISSION);
    	 StatusReqDTO statusReqDTO = new StatusReqDTO();
    	 if (status != null) {
    	     statusReqDTO.setStatusId(status.getStatusId());
    		 StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
    		 workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
    	  }
    	 workFlowStatuses.add(workFlowStatus);
		
	    if(requestNote.getWorkFlowStatuses() != null){
	    	requestNote.getWorkFlowStatuses().addAll(workFlowStatuses);
	    }else{
	    	requestNote.setWorkFlowStatuses(workFlowStatuses);
	    }
		ScRequestSupplierItem requestSupplierItem = getManagedBean().getRequestSupplierItem();
		scRequestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
		scRequestNoteDtl.setAssignedUser(facesScopes.getUserSessionBean());
		scRequestNoteDtl.setProcurementType(getManagedBean().getRequestNoteDtl().getProcurementType());
		scRequestNoteDtl.setJustificationType(getManagedBean().getRequestNoteDtl().getJustificationType());
		scRequestNoteDtl.setJustification(getManagedBean().getRequestNoteDtl().getJustification());
		scRequestNoteDtl.setScRequestNote(requestNote);
		scRequestNoteDtl.setDeliverPeriodType(ParameterConst.SQ_DURATION_DAY);
		
		//TODO have to compare duplicate items in request note
	   if (getManagedBean().getRequestNoteDtls().isEmpty()){
		    getManagedBean().getRequestNoteDtls().add(scRequestNoteDtl);
		} else {
			    List<Long> itemIds = new ArrayList<Long>();
			    for (ScRequestNoteDtl noteDtl : getManagedBean().getRequestNoteDtls()) {
				 itemIds.add(noteDtl.getScRequestSupplierItem().getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getItemId());
			   }
			   if (itemIds.size() > 0) {
			   if(itemIds.contains(getManagedBean().getRequestNoteDtl().getScRequestSupplierItem().getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getItemId())){
				  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,
									"Item already selected.", ""));
				  FacesContext fc = FacesContext.getCurrentInstance();
				  fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "commonRequestNote");
					 
				} else {
				   List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
				   requestNoteDtls.add(getManagedBean().getRequestNoteDtl());
				   getManagedBean().getRequestNoteDtls().addAll(requestNoteDtls);
				}
			}
		}
		 populateUserDivisionList();
		 getManagedBean().setRequestNote(requestNote);
		 getCurrentStatus(status.getStatusId());
		 
		 initialInformation(requestNote);
		 initializeRemarks(requestNote);
		 return "commonRequestNote";
	 }
	
	
	/**
	 * Event for procurement type  drop down in Add to Cart
	 */
	public void procurementTypeEvent() {
		log.info("procurementTypeEvent"); 
		if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
			getManagedBean().setProcTypeOption(false);
			getManagedBean().setJustTypeOption(false);
			
		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(false);
			getManagedBean().setJustTypeOption(false);
		
		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(true);
		    getManagedBean().setJustDropdown(getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));
			
		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
            getManagedBean().setProcTypeOption(true);
            getManagedBean().setJustDropdown(getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY));
		}
	}
	
	public String showJustDescription(String justficationCode){
		String justDesc = "";
		if(getManagedBean().getJustDropdown() !=null){
		  for (SelectItem selectItem : getManagedBean().getJustDropdown()) {
			  if(selectItem.getValue() != null){
			   if(selectItem.getValue().equals(justficationCode)){
				  justDesc = selectItem.getLabel();
			    }
			  }
			}
		}
		return justDesc;
	}
	
	/**
	 * Event for justification type drop down in Add to Cart
	 */
	public void justificationTypeEvent() {
		log.info("justificationTypeEvent"); 
		if (getManagedBean().getRequestNoteDtl().getJustificationType()
				.equalsIgnoreCase(ParameterTypeConst.JUSTIFICATION_TYPE_OTHER)) {
			getManagedBean().setJustTypeOption(true);
		} else {
			getManagedBean().setJustTypeOption(false);
		}
	}
	
	/**
	 * show Item Details, Justification, Pricing details in Request Note
	 */
	public void showReqNotePopUps(ScRequestNoteDtl scRequestNoteDtl){
		log.info("showReqNotePopUps");
		if(scRequestNoteDtl != null){
		  ScRequestSupplierItem supplierItem = 	getManagedBean().getRequestSupplierItem();
		  supplierItem.setSupplierItemPrice(scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemPrice());
		  String ItemCode = supplierItem.getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getExtensionCode();
		  CharSequence charSeq = ItemCode.subSequence(0, 2);
		  Integer icode = Integer.valueOf(charSeq.toString());
		  // TODO checking first two digits in itemCode  whether service or product
		  if (icode > ParameterConst.PRODUCT_OR_SERVICE) { // for service
			  getManagedBean().setItemCheck(true);
		  } else if (icode < ParameterConst.PRODUCT_OR_SERVICE) { // product
			 getManagedBean().setItemCheck(false);
		  }
		 getManagedBean().setRequestSupplierItem(supplierItem);
		}
	}
	
	/**
	 * Event for fulfilment type drop down in request note
	 */
	public void fulfilmentTypeEvent(ScRequestNoteDtl requestNoteDtl) {
		log.info("fulfilmentTypeEvent"); 
		if(getManagedBean().getFulfillmentType() != null){
			requestNoteDtl.setFulfillmentType(getManagedBean().getFulfillmentType());
		}
		
		List<PmUser> userlist = new ArrayList<PmUser>();
		if(requestNoteDtl.getFulfillmentType() != null ){
		if (requestNoteDtl.getFulfillmentType().equalsIgnoreCase(ParameterConst.FULFILLMENT_ONE_OFF)) {
			userlist = userListByRole(RoleCode.REQUISITIONER.value());
			getManagedBean().setUserList(userlist);
			
		 } else if (requestNoteDtl.getFulfillmentType().equalsIgnoreCase(ParameterConst.FULFILLMENT_PERIODIC)) {
			List<String> roleList= new ArrayList<String>();
			roleList.add(RoleCode.DESK_OFFICER.value());
			roleList.add(RoleCode.REQUISITIONER.value());
			for (String role : roleList) {
			    userlist.addAll(userListByRole(role));
			}
		    getManagedBean().setUserList(userlist);
		   }
		}
	}
	
	public List<PmUser> userListByRole(String roleCode){
		log.info("userListByRole - Start");
		List<PmUser> options = new ArrayList<PmUser>();
		try{
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setRoleCode(roleCode);
			IUserSO reqSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserResDTO resDTO = reqSO.retrieveUserListByRole(reqDTO);
				options = resDTO.getUserList();
			log.info("result size: " +options.size());
		}catch (Exception e){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.toString(),"Role "+RoleCode.PROGRAM_SECTOR.value()));
			throw e;
		}
		log.info("userListByRole - End");
		return options;
	}
	
	public void deleteRequestNoteDtl() {
		log.info("deleteRequestNoteDtl");
		if (getManagedBean().getSelectedRequestNoteDtls() != null) {
			for (ScRequestNoteDtl requestNoteDtl : getManagedBean()
					.getSelectedRequestNoteDtls()) {
				if (getManagedBean().getRequestNoteDtls().contains(
						requestNoteDtl)) {
					List<ScRequestNoteDtl> reqNoteDtl = getManagedBean()
							.getRequestNoteDtls();
					reqNoteDtl.remove(requestNoteDtl);
					getManagedBean().setRequestNoteDtls(reqNoteDtl);
				}
			}
		}
	}
	
	/**
	  * To get Approver Pick List  in Request Note
	 */
	private	List<ScRequestNoteApprovalUser> createTargetApprover(ScRequestNoteApprovalUser approver){
		List<ScRequestNoteApprovalUser> results = new ArrayList<ScRequestNoteApprovalUser>();
		 for (PmUser pmUser :  getManagedBean().getApprovalUsers().getTarget()) {
			 approver.setPmUser(pmUser);
		      results.add(approver);
		}
		return results;
	}
    
	public String getCurrentStatus(){
		String result = "";
		if (getManagedBean().getRequestNote().getWorkFlowStatuses() != null) {
		for(ScRNWorkFlowStatus status : getManagedBean().getRequestNote().getWorkFlowStatuses()){
			if(status.isCurrent()){
				result = getStatusDescById(status.getPmStatus().getStatusId());
				break;
			}
		}
		}
		return result;
	}
	
	 public void  getCurrentStatus(Long statusId) {
			if (statusId != null) {
				 getManagedBean().setRequestNoteStatus(getStatusDescById(statusId));
			}
		  }
	
	
	 public void initialInformation(ScRequestNote requestNote){
		    DualListModel<PmUser>	approvalUsers;
			IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDto = new UserReqDTO();
			userReqDto.setRoleCode(RoleCode.BPK_APPROVER.value());
			UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
			List<PmUser> availableApprover	=	userResDTO.getUserList();
			List<PmUser> selectedApprover	=	new ArrayList<PmUser>();
			approvalUsers =	new DualListModel<PmUser>(availableApprover, selectedApprover);
			
		    if (requestNote.getApprovalUsers().size() > 0
				&& requestNote != null) {
			   for (int i = 0; i < requestNote.getApprovalUsers().size(); i++) {
				    selectedApprover.add(requestNote.getApprovalUsers().get(i).getPmUser());
				    for (int j = 0; j < availableApprover.size(); j++) {
					    if (requestNote.getApprovalUsers().get(i).getPmUser().getUserId()
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
	
	
	public boolean isSupplierUser(){
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_USER");
	}


	public boolean isRequisitioner(){
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("REQUISITIONER");
	}

	
	public PmOrgProfile ptjNameEvent() {
		if (getManagedBean().getOrgProfileId() != null) {
			for (PmOrgProfile orgProfile : getManagedBean()
					.getUserDivisionList()) {
				if (orgProfile.getOrgProfileId().equals(
						getManagedBean().getOrgProfileId())) {
					getManagedBean().getRequestNote().setPmOrgProfile(orgProfile);
				}
			}
		}
		return getManagedBean().getOrgProfile();
	}
	
}