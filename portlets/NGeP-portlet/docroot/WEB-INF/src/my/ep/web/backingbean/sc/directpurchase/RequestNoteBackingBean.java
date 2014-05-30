/**
 * Copyright � 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.directpurchase;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
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
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPIDRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefaultAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScRNWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.sc.purchaseindefault.PurchaseInDefaultBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.directpurchase.RequestNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

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
    	 status.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
    	 StatusReqDTO statusReqDTO = new StatusReqDTO();
    	 if (status != null) {
    	     statusReqDTO.setStatusId(status.getStatusId());
    		 StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
    		 workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
    	  }
    	 workFlowStatus.setRequestNote(requestNote);
	     workFlowStatus.setCurrent(Boolean.TRUE);
	     //workFlowStatuses.add(workFlowStatus);
	     requestNote.setWorkFlowStatuses(workFlowStatuses);
	     
		// Remarks
		requestNote.setRemarks(initializeRemarks(requestNote));
		requestNoteReqDTO.setRequestNote(requestNote);
		requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
	    requestNote.setPmOrgProfile(ptjNameEvent());
		requestNoteReqDTO.setRequestNote(requestNote);
		// when save need to set submit in the reqDTO to false
		requestNoteReqDTO.setSubmit(Boolean.FALSE);
		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		requestNoteReqDTO.setToken(token);
		requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
		RequestNoteResDTO requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
		if (requestNoteResDTO.isSuccessful()) {
			ScRequestNote scRequestNote = requestNoteResDTO.getRequestNoteDetail();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, 
					null,
					MessageUtils.getMessage(MessageConst.INFO_700025, 
							new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_REQUEST_NOTE), scRequestNote.getRequestNoteNo()}, 
							MessageUtils.BUNDLE_COMMON_MESSAGES, 
							new FacesScopes().getUserLocale())));
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	
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
    	 status.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
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
		if (getManagedBean().getSourceMethod().equalsIgnoreCase(ParameterConst.METHOD_SQ)) {
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
		} else {
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
		}
		
	    requestNote.setPmOrgProfile(ptjNameEvent());
		requestNoteReqDTO.setRequestNote(requestNote);
		//Hoa: when submit need to set submit in the reqDTO to true
		requestNoteReqDTO.setSubmit(Boolean.TRUE);
		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		requestNoteReqDTO.setToken(token);
		requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
		RequestNoteResDTO requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
		if (requestNoteResDTO.isSuccessful()) {
			ScRequestNote scRequestNote = requestNoteResDTO.getRequestNoteDetail();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, 
					null,
					MessageUtils.getMessage(MessageConst.INFO_700005, 
							new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_REQUEST_NOTE), scRequestNote.getRequestNoteNo()}, 
							MessageUtils.BUNDLE_COMMON_MESSAGES, 
							new FacesScopes().getUserLocale())));
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	
			
			super.renewSesionObject();
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
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
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
				List<ScRequestNoteDtl>  requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
				if(requestNote.getRequestNoteDtls()  != null){
					for (ScRequestNoteDtl scRequestNoteDtl : requestNote.getRequestNoteDtls()) {
						ScRequestSupplierItem scRequestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();
						if(!scRequestSupplierItem.getSupplierItemPrices().isEmpty() && scRequestSupplierItem.getSupplierItemPrices() != null ){
							scRequestSupplierItem.setSupplierItemPrice(scRequestSupplierItem.getSupplierItemPrices().get(0));
							scRequestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);
						}
						requestNoteDtls.add(scRequestNoteDtl);
					}
				}
				getManagedBean().setRequestNoteDtls(requestNoteDtls);
				List<ScRequestNoteRemark> results= new ArrayList<ScRequestNoteRemark>();
				if( requestNote.getRemarks() !=null){
					for (ScRequestNoteRemark remark : requestNote.getRemarks()) {
						remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
						remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						remark.setChangedBy(remark.getCreatedBy());
						remark.setChangedDate(remark.getCreatedDate());
						remark.setUser(facesScopes.getUserSessionBean());
						results.add(remark);
					}
				}
			   ScRequestNoteDtl scRequestNoteDtl = new ScRequestNoteDtl();
			   parameterReqDTO.setParameterType(ParameterTypeConst.RN_FULFILLMENT_TYPE);
			   parameterReqDTO.setParameterCode(ParameterConst.FULFILLMENT_ONE_OFF);
			   ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
			   scRequestNoteDtl.setFulfillmentType(parameterResDTO.getPmParameter());
		       requestNote.setRemarks(results);
			   getManagedBean().setRequestNote(requestNote);
			   getManagedBean().setOutComes(res.getAction());
			   getManagedBean().setFulfillmentType(ParameterConst.FULFILLMENT_ONE_OFF);
			   getManagedBean().setOrgProfile(requestNote.getPmOrgProfile());
			   initialInformation(requestNote);
			   fulfilmentTypeEvent(requestNote.getRequestNoteDtls().get(0));
			   // For status 
			   getCurrentStatus(res.getDocStatus().longValue());
			   FacesContext fc = FacesContext.getCurrentInstance();
			   fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "commonRequestNote");
			}
	}
  }	
	
	public void updateBpmTask(String outcome) {
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		BpmRequestDTO reqBpm = new BpmRequestDTO();
		/*//Hoa: call approvalRequestNote method to group all the request note dtl. with the same fulfilment type, assigned to, supplier in the same purchase request
		if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)){
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			RequestNoteReqDTO	reqDTO	=	new RequestNoteReqDTO();
			reqDTO.setRequestNoteId(getManagedBean().getRequestNote().getRequestNoteId());
			directPurchaseSO.approvalRequestNote(reqDTO);
		}*/
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		try {
			reqBpm.setTaskId(getManagedBean().getTaskId());
			reqBpm.setOutcome(outcome);
			if (null != ctxToken) {
				reqBpm.setCtxToken(ctxToken);
				BpmResponseDTO resBpm = bpmSO.updateTask(reqBpm);
				boolean result = resBpm != null ? resBpm.isSuccessful() : false;
				if (result) {
					if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, null, 
								MessageUtils.getMessage(MessageConst.INFO_700026, 
										new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_REQUEST_NOTE),getManagedBean().getRequestNote().getRequestNoteNo()}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
						FacesContext fc = FacesContext.getCurrentInstance();
						fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);

					} else if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, null, 
								MessageUtils.getMessage(MessageConst.INFO_700009, 
										new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_REQUEST_NOTE),getManagedBean().getRequestNote().getRequestNoteNo()}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
						FacesContext fc = FacesContext.getCurrentInstance();
						fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);

					} else if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, null, 
								MessageUtils.getMessage(MessageConst.INFO_700011, 
										new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_REQUEST_NOTE),getManagedBean().getRequestNote().getRequestNoteNo()}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
						FacesContext fc = FacesContext.getCurrentInstance();
						fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	   	

					} else if(BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, null, 
								MessageUtils.getMessage(MessageConst.INFO_700008, 
										new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_REQUEST_NOTE),getManagedBean().getRequestNote().getRequestNoteNo()}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
						FacesContext fc = FacesContext.getCurrentInstance();
						fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	   	
					}
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
	   IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	   ParameterReqDTO reqDTO = new ParameterReqDTO();
	   List<ScRequestNoteDtl>	requestNoteDtls	=	getManagedBean().getRequestNoteDtls();
	   if(getManagedBean().getSelectedRequestNoteDtls() != null) {
		  for(ScRequestNoteDtl selectedReqDtl : getManagedBean().getSelectedRequestNoteDtls()) {
			 if(getManagedBean().getRequestNoteDtls().contains(selectedReqDtl)){
			    reqDTO.setParameterType(ParameterTypeConst.RN_FULFILLMENT_TYPE);
			    reqDTO.setParameterCode(ParameterConst.FULFILLMENT_ONE_OFF);
				ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				selectedReqDtl.setFulfillmentType(parameterResDTO.getPmParameter());
				PmUser pmUser = new PmUser();
				pmUser.setUserId(getManagedBean().getAssignedToUserId());
			    selectedReqDtl.setAssignedUser(pmUser);
				selectedReqDtl.setDeliverTermType(getManagedBean().getPeriodType());
				selectedReqDtl.setDeliverTerm(getManagedBean().getPeriodValue());
				requestNoteDtls.add(selectedReqDtl);
				getManagedBean().getRequestNoteDtls().remove(selectedReqDtl);
				getManagedBean().setRequestNoteDtls(requestNoteDtls);
			  }
			}
		}
	}
	
	public void populateUserDivisionList() {
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
			status.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
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
	public void addToCart(Long cmSupplierItemId) {
		log.info("addToCart");
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
		CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
		System.out.println("cmSupplierItemId:" + cmSupplierItemId);
		catReqDTO.setSupplierItemId(cmSupplierItemId);
		//CatalogueResDTO catResDTO = catalogueSO.searchSupplierItemByStatus(catReqDTO);
		CatalogueResDTO catResDTO = catalogueSO.findActivePricing(catReqDTO);
		
		List<CmPricing> cmPricings = catResDTO.getPriceLs();
		if(cmPricings != null && cmPricings.size() > 0){
			CmSupplierItem cmSupplierItem = cmPricings.get(0).getCmSupplierItem();
			cmSupplierItem.setCmPricings(cmPricings);
			
			ScRequestItem requestItem = createRequestItem(cmSupplierItem);

			ScRequestSupplierItem requestSupplierItem = createScRequestSupplierItem(requestItem, cmSupplierItem.getSmSupplier(), cmPricings);
			ScSupplierItemPrice supplierItemPrice = createSupplierItemPrice(requestSupplierItem, cmPricings.get(0));
			
			requestSupplierItem.setSupplierItemPrice(supplierItemPrice);
			getManagedBean().setRequestSupplierItem(requestSupplierItem);
			
			ParameterResDTO resDTO; 
			 // TODO check supplier is government seller or not 
			  ScRequestNoteDtl requestNoteDtl =  getManagedBean().getRequestNoteDtl();
			  if(cmSupplierItem.getSmSupplier().getPmOrgProfile().isGovtSeller()){
				  reqDTO = new ParameterReqDTO();
				  reqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
				  reqDTO.setParameterCode(ParameterConst.PROC_TYPE_G2G);
				   resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				  requestNoteDtl.setProcurementType(resDTO.getPmParameter());
			  } else {
				  reqDTO = new ParameterReqDTO();
				  reqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
				  reqDTO.setParameterCode(ParameterConst.PROC_TYPE_NORMAL);
				   resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				  requestNoteDtl.setProcurementType(resDTO.getPmParameter());
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
				getManagedBean().setPriceTypes(getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, priceTypes));
				
				// TODO PriceType drop down list in request note
				getManagedBean().setRnPriceTypes(getPriceTypes(ParameterTypeConst.PRICE_TYPE, priceTypes));
			}
		}
		getCurrentStatus(DPStatusConstants.RN_PENDING_SUBMISSION);
	}
	
	private ScRequestItem createRequestItem(CmSupplierItem cmSupplierItem){
		CmItem cmItem = cmSupplierItem.getCmItem();
		ScRequestItem scRequestItem	= new ScRequestItem();
		
		scRequestItem.setCmItem(cmItem);
		scRequestItem.setItemName(cmItem.getItemName());
		scRequestItem.setItemDesc(cmSupplierItem.getItemDesc());
		scRequestItem.setMinqty(cmSupplierItem.getMinOrder());
		scRequestItem.setUom(cmSupplierItem.getPmUom());
		
		return scRequestItem;
	}
	
	private ScRequestSupplierItem createScRequestSupplierItem(ScRequestItem requestItem, SmSupplier smSupplier, List<CmPricing> cmPricings) {
		ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
		
		requestSupplierItem.setRequestNoteItem(requestItem);
		requestSupplierItem.setSmSupplier(smSupplier);
		
		requestSupplierItem.setSupplierItemPrices(createScSupplierItemPrices(requestSupplierItem, cmPricings));
		
		return requestSupplierItem;
	}

	private	List<ScSupplierItemPrice>	createScSupplierItemPrices(ScRequestSupplierItem scRequestSupplierItem, List<CmPricing> pricings){
		List<ScSupplierItemPrice> results	=	new ArrayList<ScSupplierItemPrice>();
		for(CmPricing pricing: pricings){
			results.add(createSupplierItemPrice(scRequestSupplierItem, pricing));
		}
		return results;
	}
	
	private ScSupplierItemPrice		createSupplierItemPrice(ScRequestSupplierItem	scRequestSupplierItem, CmPricing pricing){
		ScSupplierItemPrice	result	=	new ScSupplierItemPrice();
		
		result.setScRequestSupplierItem(scRequestSupplierItem);
		result.setPricing(pricing);
		
		result.setTplSpecZone(null);	//TODO - zone in cm.
		
		IParameterSO parameterSO;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
			reqDTO.setParameterCode(pricing.getPricingType());
			
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			
			result.setPriceType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
	
		if (pricing.getPrice() != null) {
			result.setItemPrice(pricing.getPrice());
		} else {
			result.setItemPrice(new BigDecimal(0));
		}
		
		result.setFromQty(pricing.getFromQty());
		result.setToQty(pricing.getToQty());
		
		return result;
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
	
	
	
	public SelectItem[] getPriceTypes(String parameterType,
			List<String> priceTypes) {
		SelectItem[] items = null;
		try {
			items = new SelectItem[priceTypes.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for (String priceTypeCode : priceTypes) {
				items[i++] = new SelectItem(getPmParameterById(parameterType,
						priceTypeCode) ,getParameterDesc(parameterType,
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
	
	public Long getPmParameterById(String parameterType, String parameterCode) {		
		Long parameterId = null;
		
		// Get the parameter description list.
		List<PmParameterDesc> list = ParameterHelper.getInstance()
				.getParameterDescList(parameterType);
		
		for (PmParameterDesc pmParameterDesc : list) {
			// compare matched parameterCode.
			if (pmParameterDesc.getPmParameter().getParameterCode()
					.equals(parameterCode)) {
				parameterId = pmParameterDesc.getPmParameter().getParameterId();
				break;
			}
		}
		
		return parameterId;
	}
	
	/**
	 * Page navigation to catalogue search page in Request Note
	 */
	public String backToPervious() {
		log.info("backToPervious");
		getManagedBean().setOrgProfileId(null);
		return "catalogueSearch";
	}

	public String addToRequestNote() {
		log.info("addToRequestNote");
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		List<ScRequestNoteDtl>	requestNoteDtls = getManagedBean().getRequestNoteDtls();
		ScRequestSupplierItem requestSupplierItem = getManagedBean().getRequestSupplierItem();
		ScRequestNoteDtl scRequestNoteDtl = new ScRequestNoteDtl();
		List<ScRequestItem>	requestNoteItems = getManagedBean().getRequestNoteItems();
		ScRequestItem scRequestItem = requestSupplierItem.getRequestNoteItem();
		ScSupplierItemPrice scSupplierItemPrice = requestSupplierItem.getSupplierItemPrice();
		CmSupplierItem cmSupplierItem = scSupplierItemPrice.getPricing().getCmSupplierItem();
		ScRequestNote  requestNote = getManagedBean().getRequestNote();
		
		if(getManagedBean().getSourceMethod().equalsIgnoreCase(ParameterConst.METHOD_SQ)){
			// Simple Quote
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
			
		} else if(getManagedBean().getSourceMethod().equalsIgnoreCase(ParameterConst.METHOD_PRI)){
			// purchase inquiry
			if(getManagedBean().getRequestNoteDtls() !=null){
				requestSupplierItem.setScPrInquiy(getManagedBean().getRequestNoteDtls().get(0).getScRequestSupplierItem().getScPrInquiy());		
			}
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);

		} else {
			// purchase in default & add to cart
			//IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			//PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
		    //purchaseInDefaultReqDTO.setItemId(requestSupplierItem.getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getItemId());
		    // TODO hard coded ptj id for non contract item for pid
		    //purchaseInDefaultReqDTO.setOrgProfileId(16717L);
		    //PurchaseInDefaultResDTO defaultResDTO = purchaseInDefaultSO.getPurchaseInDefaultByItemId(purchaseInDefaultReqDTO);
		    
		    // pid to request note
		   /* if(defaultResDTO.getPurchaseInDefaultDtl() != null){
		    	ScPurchaseInDefault  purchaseInDefault = defaultResDTO.getPurchaseInDefaultDtl();
		    	scRequestItem.setPurchaseInDefault(purchaseInDefault);
		    }*/
		    requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
		    
		    IViewSupplierItemSO viewSupplierItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, IViewSupplierItemSO.class);
		    
		    CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();

		    catalogueReqDTO.setViewSearchBean(new CatalogueBackingBean().getManagedBean().getSearchDataModel().getSearchBean());
		    catalogueReqDTO.setSupplierItemId(cmSupplierItem.getSupplierItemId());
		    catalogueReqDTO.setExtensionCode(scRequestItem.getCmItem().getExtensionCode());
		    
		    CatalogueResDTO catalogueResDTO = viewSupplierItemSO.getSuppliersItemPricingByExtensionCode(catalogueReqDTO);
		    
		    List<ScRequestSupplierItem> supplierItems = new ArrayList<ScRequestSupplierItem>();
		    for (VCmSupplierItem vCmSupplierItem : catalogueResDTO.getViewSuppItemResultBean().getResultList()) {
		    	if (vCmSupplierItem.getCmPricings() != null && vCmSupplierItem.getCmPricings().size() > 0) {
		    		CmSupplierItem supplierItem = vCmSupplierItem.getCmPricings().get(0).getCmSupplierItem();
	    			ScRequestSupplierItem scRequestSupplierItem = createScRequestSupplierItem(
			    			scRequestItem, supplierItem.getSmSupplier(), vCmSupplierItem.getCmPricings());
	    			
	    			supplierItems.add(scRequestSupplierItem);	
		    	}
		    }
		    // Add selected supplier item.
		    supplierItems.add(requestSupplierItem);
		    
		    scRequestItem.setSupplierItems(supplierItems);
		}
		
		requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
		scRequestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
		//Hoa: TODO WorkFlow Status should be set In the save, submit method
		List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
		ScRNWorkFlowStatus workFlowStatus = new ScRNWorkFlowStatus();
		requestNote.setWorkFlowStatuses(workFlowStatuses);
		PmStatus status = new PmStatus();
		status.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
			workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
		}
		//workFlowStatuses.add(workFlowStatus);

		if(requestNote.getWorkFlowStatuses() != null){
			requestNote.getWorkFlowStatuses().addAll(workFlowStatuses);
		}else{
			requestNote.setWorkFlowStatuses(workFlowStatuses);
		}

		scRequestNoteDtl.setAssignedUser(new PmUser());
		scRequestNoteDtl.setProcurementType(getManagedBean().getRequestNoteDtl().getProcurementType());
		scRequestNoteDtl.setJustificationType(getManagedBean().getRequestNoteDtl().getJustificationType());
		scRequestNoteDtl.setJustification(getManagedBean().getRequestNoteDtl().getJustification());
		scRequestNoteDtl.setScRequestNote(requestNote);
		scRequestNoteDtl.setDeliverTermType(ParameterConst.SQ_DURATION_DAY);
	    reqDTO = new ParameterReqDTO();
	    reqDTO.setParameterType(ParameterTypeConst.RN_FULFILLMENT_TYPE);
	    reqDTO.setParameterCode(ParameterConst.FULFILLMENT_ONE_OFF);
	    ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
	    scRequestNoteDtl.setFulfillmentType(resDTO.getPmParameter());
	    
	    reqDTO = new ParameterReqDTO();
	    reqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
	    reqDTO.setParameterCode(scSupplierItemPrice.getPriceType().getParameterCode());
	    resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
	    scRequestNoteDtl.setPriceType(resDTO.getPmParameter());
	    
	    scRequestNoteDtl.setDeliverTerm(cmSupplierItem.getLeadTime().intValue());

		//TODO have to compare duplicate items in request note
		if (getManagedBean().getRequestNoteDtls().isEmpty()){
			getManagedBean().getRequestNoteDtls().add(scRequestNoteDtl);
		} else {
			List<Long> itemIds = new ArrayList<Long>();
			for (ScRequestNoteDtl noteDtl : getManagedBean().getRequestNoteDtls()) {
				itemIds.add(noteDtl.getScRequestSupplierItem().getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getItemId());
			}
			if (itemIds.size() > 0) {
				if(itemIds.contains(scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getItemId())){
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
							null,
							MessageUtils.getMessage(MessageConst.INFO_700027, 
									new String[]{scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getItemName()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, 
									new FacesScopes().getUserLocale())));
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "commonRequestNote");
				} else {
					requestNoteDtls.add(scRequestNoteDtl);
					getManagedBean().setRequestNoteDtls(requestNoteDtls);
				}
			}
		}
		
		// request items & request supplier items
		//List<ScRequestSupplierItem> requestSupplierItems = new ArrayList<ScRequestSupplierItem>();
		//ScRequestSupplierItem scReqSupplierItem = getManagedBean().getRequestSupplierItem();
		//requestSupplierItem.setRequestNoteItem(scRequestItem);
		//requestSupplierItems.add(scReqSupplierItem);
		
		//scRequestItem.setSupplierItems(requestSupplierItems);
		//requestNoteItems.add(scRequestItem);
		//requestNote.setRequestNoteItems(requestNoteItems);
		scRequestItem.setRequestNote(requestNote);
		requestNote.setRequestNoteDtls(requestNoteDtls);
		//this.getManagedBean().setRequestNoteItems(requestNoteItems);
		
		populateUserDivisionList();
		getManagedBean().setRequestNote(requestNote);
		getManagedBean().setFulfillmentType(ParameterConst.FULFILLMENT_ONE_OFF);
		getCurrentStatus(status.getStatusId());
		initialInformation(requestNote);
		initializeRemarks(requestNote);
		fulfilmentTypeEvent(scRequestNoteDtl);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "commonRequestNote");
		return null;
	}

	/**
	 * Event for procurement type  drop down in Add to Cart
	 */
	public void procurementTypeEvent() {
		log.info("procurementTypeEvent"); 
		if (getManagedBean().getRequestNoteDtl().getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);
			
		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);
		
		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
		    getManagedBean().setJustDropdown(getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));
			
		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
            getManagedBean().setProcTypeOption(Boolean.TRUE);
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
		List<ScRequestNoteDtl> requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
		requestNoteDtls.add(scRequestNoteDtl);
		if(scRequestNoteDtl != null){
		  ScRequestSupplierItem supplierItem = scRequestNoteDtl.getScRequestSupplierItem();
		  supplierItem.setSupplierItemPrice(scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemPrice());
		  String ItemCode = supplierItem.getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getExtensionCode();
		  supplierItem.setScRequestNoteDtl(requestNoteDtls);
		  
		  CharSequence charSeq = ItemCode.subSequence(0, 2);
		  Integer icode = Integer.valueOf(charSeq.toString());
		  // TODO checking first two digits in itemCode  whether service or product
		  if (icode > ParameterConst.PRODUCT_OR_SERVICE) { // for service
			  getManagedBean().setItemCheck(Boolean.TRUE);
		  } else if (icode < ParameterConst.PRODUCT_OR_SERVICE) { // product
			 getManagedBean().setItemCheck(Boolean.FALSE);
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
			//requestNoteDtl.setFulfillmentType(getManagedBean().getFulfillmentType());
		}
		
		List<PmUser> userlist = new ArrayList<PmUser>();
		if(requestNoteDtl.getFulfillmentType() != null ){
		if (requestNoteDtl.getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILLMENT_ONE_OFF)) {
			userlist = userListByRole(RoleCode.REQUISITIONER.value());
			getManagedBean().setUserList(userlist);
			
		 } else if (requestNoteDtl.getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILLMENT_PERIODIC)) {
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
	
	public void  getCurrentStatus(Long statusId) {
		if (statusId != null) {
			getManagedBean().setRequestNoteStatus(getStatusDescById(statusId));
		}
	}
	
	 public void initialInformation(ScRequestNote requestNote){
		    DualListModel<PmUser>	approvalUsers;
			IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDto = new UserReqDTO();
			userReqDto.setRoleCode(RoleCode.RN_APPROVER.value());
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
		IPurchaseInDefaultSO purchaseInDefaultSO  = null;
		try {
			
		if (getManagedBean().getOrgProfileId() != null) {
			for (PmOrgProfile orgProfile : getManagedBean().getUserDivisionList()) {
				if (orgProfile.getOrgProfileId().equals(getManagedBean().getOrgProfileId())) {
					ScRequestNote sRequestNote = getManagedBean().getRequestNote();
					sRequestNote.setPmOrgProfile(orgProfile);
					getManagedBean().setRequestNote(sRequestNote);
				}
			}
			//TODO Based on ptj selection have to get pid for request note 
			 purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
			// fetch pid item for request note item	
			List<ScRequestItem>	requestNoteItems = getManagedBean().getRequestNoteItems();
			List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
			if(getManagedBean().getRequestNoteDtls() != null && getManagedBean().getRequestNoteDtls().size() >0){
				for (ScRequestNoteDtl scRequestNoteDtl : getManagedBean().getRequestNoteDtls()) {
					ScRequestSupplierItem requestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();
					ScRequestItem requestItem = requestSupplierItem.getRequestNoteItem();

					// TODO selected ptj id for non contract item for pid
					purchaseInDefaultReqDTO.setOrgProfileId(getManagedBean().getOrgProfileId());
					purchaseInDefaultReqDTO.setItemId(scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getItemId()) ;
					PurchaseInDefaultResDTO defaultResDTO = purchaseInDefaultSO.getPurchaseInDefaultByItemId(purchaseInDefaultReqDTO);

					// pid to request note
					if(defaultResDTO.getPurchaseInDefaultDtl() != null){
						ScPurchaseInDefault  purchaseInDefault = defaultResDTO.getPurchaseInDefaultDtl();
						requestItem.setPurchaseInDefault(purchaseInDefault);
					} else {
						requestItem.setPurchaseInDefault(null);
					}
					
					requestSupplierItem.setRequestNoteItem(requestItem);
					scRequestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
					requestNoteDtls.add(scRequestNoteDtl);
				   
					// Request Supplier Items
					List<ScRequestSupplierItem> requestSupplierItems = new ArrayList<ScRequestSupplierItem>();
					ScRequestSupplierItem scReqSupplierItem = getManagedBean().getRequestSupplierItem();
					requestSupplierItem.setRequestNoteItem(requestItem);
					requestSupplierItems.add(scReqSupplierItem);
				    
					// request Note Items
					//requestItem.setSupplierItems(requestSupplierItems);
					requestNoteItems.add(requestItem);
				}
			}
			this.getManagedBean().setRequestNoteDtls(requestNoteDtls);
			ScRequestNote requestNote = getManagedBean().getRequestNote();
			requestNote.setRequestNoteDtls(requestNoteDtls);
			requestNote.setRequestNoteItems(requestNoteItems);
			this.getManagedBean().setRequestNote(requestNote);
		}
		return getManagedBean().getRequestNote().getPmOrgProfile();
	} catch (Exception ex) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Sorry We are unable to process.", ""));
		ex.printStackTrace();
	} finally {
		purchaseInDefaultSO = null;
	}
		return null;
  }
	
	/**
	  * To get Approver Pick List  in Request Note
	 */
	public void showPurchaseInDefault(ScRequestNoteDtl requestNoteDtl){
		log.info("showPurchaseInDefault");
		IPurchaseInDefaultSO purchaseInDefaultSO  = null;
		FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();
		PurchaseInDefaultBackingBean purchaseInDefaultBackingBean = new PurchaseInDefaultBackingBean();
	 try{
		if(requestNoteDtl.getScRequestSupplierItem().getRequestNoteItem().getPurchaseInDefault() != null){
			// Purchase In Default	
		     purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
			purchaseInDefaultReqDTO.setPurchaseInDefaultId(requestNoteDtl.getScRequestSupplierItem().getRequestNoteItem().getPurchaseInDefault().getPurchaseInDefaultId());
			purchaseInDefaultReqDTO.setFetchSubEntity(true);
			PurchaseInDefaultResDTO purchaseInDefaultResDTO = purchaseInDefaultSO.getPurchaseInDefault(purchaseInDefaultReqDTO);
			ScPurchaseInDefault purchaseInDefault = purchaseInDefaultResDTO.getPurchaseInDefaultDtl();
			if(purchaseInDefault != null){
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
				purchaseInDefaultBackingBean.getManagedBean().setPurchaseInDefault(purchaseInDefault);
				purchaseInDefaultBackingBean.initialInformation(purchaseInDefault);
				purchaseInDefaultBackingBean.retrieveReasonDescListByCategory();
				purchaseInDefaultBackingBean.justificationTypeEvent();
				purchaseInDefaultBackingBean.getManagedBean().setSelectedPmOrgProfile(getManagedBean().getRequestNote().getPmOrgProfile());
				purchaseInDefaultBackingBean.getCurrentStatus(DPStatusConstants.PID_COMPLETED);
				List<ScPurchaseInDefaultAttachment> attachments = new ArrayList<ScPurchaseInDefaultAttachment>();
				ScPurchaseInDefaultAttachment attachment =purchaseInDefault.getScPurchaseInDefaultAttachment();
				attachment.setUser(facesScopes.getUserSessionBean());
				attachment.setChangedDate(new Timestamp(System.currentTimeMillis()));
				attachments.add(attachment);
				purchaseInDefaultBackingBean.getManagedBean().setAttachments(attachments);
				
				// disable all for pid view
				purchaseInDefaultBackingBean.getManagedBean().setApproverFlag(Boolean.TRUE);
				purchaseInDefaultBackingBean.getManagedBean().setPidBtnFlag(Boolean.FALSE);
				
				// TODO current page url for page navigation from pid to request note
				purchaseInDefaultBackingBean.getManagedBean().setPageNavToRequestNote(fc.getViewRoot().getViewId());
			} 
		}
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "purchaseInDefaultMain");
	} catch (Exception ex) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Sorry We are unable to process.", ""));
		ex.printStackTrace();
	} finally {
		purchaseInDefaultSO = null;
	}
	}
	

	public String cancel() {
		log.info("cancel");
		// clear request note data
		 super.renewSesionObject();
		return "catalogueSearch";
	}


}