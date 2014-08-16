/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.directpurchase;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
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
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.directpurchase.PrInquiryReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryResDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmSupplierZoneDTL;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDistrictCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDivisionCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationStateCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScPIDRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefaultAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteDoc;
import my.ep.persistence.entity.sc.directpurchase.ScRNWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRNZone;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.sc.purchaseindefault.PurchaseInDefaultBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.sc.directpurchase.RequestNoteManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
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

	public void searchAction(){
		getManagedBean().getReqNoteDataModel().reloadData();
	}
	
	public void resetAction() {
		getManagedBean().setSearchReqNoteNo(null);
		getManagedBean().setSearchSrcMethod(null);
		getManagedBean().setSearchCreatedDateFrom(null);
		getManagedBean().setSearchCreatedDateTo(null);
		getManagedBean().setSearchStatuses(null);
		getManagedBean().getReqNoteDataModel().resetData();
	}
	
	public String editRequestNote(ScRequestNote scRequestNote) {
		retrieveRequestNoteDetails(scRequestNote.getRequestNoteId() ,null , Boolean.FALSE);
		
		scRequestNote = getManagedBean().getRequestNote();
		
		if (scRequestNote.getWorkFlowStatuses() != null && scRequestNote.getWorkFlowStatuses().size() > 0) {
			getCurrentStatus(scRequestNote.getWorkFlowStatuses().iterator().next().getPmStatus().getStatusId());	
		}
		
		return "commonRequestNote";
	}
	
	/**
	 * saveAction method to save request note details
	 * return String
	 */
	public String saveAction(String outcome) {
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		RequestNoteResDTO requestNoteResDTO = null;
    try {
    	 requestNote.setRequestNoteDtls(this.getManagedBean().getRequestNoteDtls());

		// Target Approver
		if (this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0) {
		    requestNote.setApprovalUsers(createTargetApprover(requestNote));
		}
		
		requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
		// Remarks
		requestNote.setRemarks(initializeRemarks(requestNote));
	    if(requestNote.getRemarks() != null ){
	    	for (int i = 0; i < requestNote.getRemarks().size(); i++) {
	    		ScRequestNoteRemark remark = requestNote.getRemarks().get(i);
	    		if(StringUtils.isBlank(remark.getRemark())){
	    			requestNote.getRemarks().remove(remark);
	    		}	
	    	} 
	    }
	    
	    //Hoa: TODO WorkFlow Status should be set In the save, submit method
	    List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
	    ScRNWorkFlowStatus workFlowStatus = new ScRNWorkFlowStatus();
	    requestNote.setWorkFlowStatuses(workFlowStatuses);
	    workFlowStatus.setRequestNote(requestNote);
	    PmStatus status = new PmStatus();
	    status.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
	    StatusReqDTO statusReqDTO = new StatusReqDTO();
	    if (status != null) {
	    	statusReqDTO.setStatusId(status.getStatusId());
	    	StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
	    	workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
	    	workFlowStatus.setCurrent(Boolean.TRUE);
	    }
	    workFlowStatuses.add(workFlowStatus);

	    requestNote.setWorkFlowStatuses(workFlowStatuses);
	    requestNoteReqDTO.setRequestNote(requestNote);
	    FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
	    if(outcome.isEmpty()){
	    	requestNoteReqDTO.setSubmit(Boolean.FALSE);
			requestNoteReqDTO.setToken(token);
			requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
		    requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
		    
	     } else if(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)){
	    	 requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
	    	 requestNoteReqDTO.setToken(token);
	    	 requestNoteReqDTO.setOutcome("");
	    	 requestNoteResDTO = directPurchaseSO.updateRequestNote(requestNoteReqDTO);
	     }
		if (requestNoteResDTO.isSuccessful()) {
			ScRequestNote scRequestNote = requestNoteResDTO.getRequestNoteDetail();
			successMessage(MessageConst.INFO_700025,ParameterTypeConst.DOCUMENT_TYPES,
					ParameterConst.DOCTYPE_REQUEST_NOTE,scRequestNote.getRequestNoteNo());
			// clear data
			super.renewSesionObject();
		}else {
			throw new EpException("Failed.");
		}
    }    
	    finally {
			directPurchaseSO = null;
		}
		return null;
	}
	
	/**
	 * submitAction method to submit request note details
	 * return String
	 */
	public String submitAction(String outcome) {
		debugLogger("submitAction");
		RequestNoteResDTO requestNoteResDTO = null;
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		try {
			List<ScRequestNoteDtl>	requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
			if (this.getManagedBean().getRequestNoteDtls() != null && getManagedBean().getRequestNoteDtls().size() > 0) {
				for (ScRequestNoteDtl requestNoteDtl : getManagedBean().getRequestNoteDtls()) {
					for (ScRequestSupplierItem  supplierItem : requestNoteDtl.getRequestItem().getSupplierItems()) {
						if(supplierItem.getSmSupplier().getSupplierId().equals(requestNoteDtl.getScRequestSupplierItem().getSmSupplier().getSupplierId())){
							requestNoteDtl.setScRequestSupplierItem(supplierItem);
						}
					}
					requestNoteDtls.add(requestNoteDtl);
				}	
			}
			requestNote.setRequestNoteDtls(requestNoteDtls);
			// Target Approver
			if (this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0) {
				requestNote.setApprovalUsers(createTargetApprover(requestNote));
			}

			requestNote.setWorkFlowStatuses(null);
			// Remarks
			requestNote.setRemarks(initializeRemarks(requestNote));
			if (getManagedBean().getSourceMethod().equalsIgnoreCase(ParameterConst.METHOD_SQ)) {
				requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
			} else {
				requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
			}
			
			if(!(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome))){
				if(requestNote.getRemarks() != null ){
					for (int i = 0; i < requestNote.getRemarks().size(); i++) {
						ScRequestNoteRemark remark = requestNote.getRemarks().get(i);
						if(StringUtils.isBlank(remark.getRemark())){
							requestNote.getRemarks().remove(remark);
						}	
					} 
				}
			}
			
			if(getManagedBean().getPrInquiryId() != null){
				requestNoteReqDTO.setPurchaseInquiryId(getManagedBean().getPrInquiryId());
			}
			FacesScopes facesScopes = new FacesScopes();
			String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
			requestNoteReqDTO.setRequestNote(requestNote);
			if(outcome.isEmpty()) {	
				requestNoteReqDTO.setToken(ctxToken);
				requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
				requestNoteReqDTO.setSubmit(Boolean.TRUE);
				requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
			} else {
				requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
				requestNoteReqDTO.setToken(ctxToken);
				requestNoteReqDTO.setOutcome(outcome);
				requestNoteResDTO = directPurchaseSO.updateRequestNote(requestNoteReqDTO);
			}
			
			if (requestNoteResDTO.isSuccessful()) {
				if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700010,ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE,getManagedBean().getRequestNote().getRequestNoteNo());

				} else if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome) || outcome.isEmpty()){
					successMessage(MessageConst.INFO_700005,ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE,getManagedBean().getRequestNote().getRequestNoteNo());
				}	
				// clear data
				super.renewSesionObject();
			} else{
				throw new EpException("Failed.");
			}
		}    
		finally {
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
		IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
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
			Long dpId = null;
			boolean zoneFlag = Boolean.FALSE;

			if(directPurchaseDataType.getRnId() == null){
				dpId = Long.valueOf(directPurchaseDataType.getDpId());
			} else{
				dpId = Long.valueOf(directPurchaseDataType.getRnId());
			}
			
			if(ParameterConst.REQUEST_NOTE_RN.equalsIgnoreCase(dpMode)){ 
				dpId = Long.valueOf(dpId);

			} else if(ParameterConst.REQUEST_NOTE_SQ.equalsIgnoreCase(dpMode)){ 
				// Simple Quote
				QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
				Long quoteId = Long.valueOf(directPurchaseDataType.getDpId());
				quoteReqDTO.setQuoteId(quoteId);
				ScQuote quote = quoteSO.getQuoteDtl(quoteReqDTO).getQuoteBidDtl();
				if(quote != null){
                    getManagedBean().setScQuote(quote); // copy scQuote to sq view in request note
					if(quote.isZonal()){
						zoneFlag = Boolean.TRUE;
					}
					requestNoteReqDTO.setRequestNoteId(quote.getRequestNote().getRequestNoteId());
					RequestNoteResDTO requestNoteResDTO = directPurchaseSO.getRequestNote(requestNoteReqDTO);
					if(requestNoteResDTO.getRequestNoteDetail() != null){
						dpId = requestNoteResDTO.getRequestNoteDetail().getRequestNoteId();
					}
				}
			}

			retrieveRequestNoteDetails(dpId,res.getDocStatus().longValue() , zoneFlag);
			// title checker
			getManagedBean().setTitleChecker(Boolean.FALSE);
			FacesContext fc = FacesContext.getCurrentInstance();

			if(zoneFlag){
				// page nav. to requte note by zone 
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "requestNoteByZone");
			} else {
				// page nav. to common request note
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "commonRequestNote");
			}
		}
	}

	private void retrieveRequestNoteDetails(Long requestNoteId , Long docStatusId , boolean zoneFlag) {
		IDirectPurchaseSO directPurchaseSO;
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		ParameterResDTO parameterResDTO = null;
		try {
			//RequestNote Details
			directPurchaseSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			
			RequestNoteReqDTO	requestDTO	=	new RequestNoteReqDTO();
			requestDTO.setRequestNoteId(requestNoteId);
			RequestNoteResDTO	resDTO	=	directPurchaseSO.getRequestNote(requestDTO);
			ScRequestNote	requestNote	=	resDTO.getRequestNoteDetail();
			List<ScRequestNoteDtl>  requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
			
			HashMap<String,Long> ZoneNames = new HashMap<String,Long>();
			if(requestNote.getRequestNoteDtls()  != null && requestNote.getRequestNoteDtls().size() >0){
				for (ScRequestNoteDtl scRequestNoteDtl : requestNote.getRequestNoteDtls()) {
					ScRequestSupplierItem scRequestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();
					if(scRequestSupplierItem != null){
						if(!scRequestSupplierItem.getSupplierItemPrices().isEmpty() && scRequestSupplierItem.getSupplierItemPrices() != null ){
							scRequestSupplierItem.setSupplierItemPrice(scRequestSupplierItem.getSupplierItemPrices().get(0));
							scRequestSupplierItem.setSupplierItemId(scRequestSupplierItem.getSmSupplier().getSupplierId());
							scRequestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);
						}
						
						// get PI id to Request note
						if(scRequestSupplierItem.getScPrInquiy() != null){
							getManagedBean().setPrInquiryId(scRequestSupplierItem.getScPrInquiy().getPrInquiryId());
						}
					}
					
					if(scRequestNoteDtl.getFulfilmentType() == null){
						parameterReqDTO = new ParameterReqDTO();
						parameterReqDTO.setParameterType(ParameterTypeConst.FULFILMENT_TYPE);
						parameterReqDTO.setParameterCode(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN);
						parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
						scRequestNoteDtl.setFulfilmentType(parameterResDTO.getPmParameter());
					}
					
					if(scRequestNoteDtl.getDeliverTermType() == null){
						parameterReqDTO = new ParameterReqDTO();
						parameterReqDTO.setParameterType(ParameterTypeConst.DELIVERY_TERM);
						parameterReqDTO.setParameterCode(ParameterConst.SQ_DURATION_DAY);
						parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
						scRequestNoteDtl.setDeliverTermType(parameterResDTO.getPmParameter());
					}
					if(scRequestNoteDtl.getPriceType() == null){
						parameterReqDTO = new ParameterReqDTO();
						parameterReqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
						parameterReqDTO.setParameterCode(ParameterConst.PRICE_TYPE_STANDARD);
					    parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
						scRequestNoteDtl.setDeliverTermType(parameterResDTO.getPmParameter());
					}
					
					if(scRequestNoteDtl.getJustificationType() != null){
						getManagedBean().setJustificationType(scRequestNoteDtl.getJustificationType().getParameterCode());
					}
					if(scRequestNoteDtl.getProcurementType().getParameterCode() != null){
						getManagedBean().setProcurementType(scRequestNoteDtl.getProcurementType().getParameterCode());
					}
					
					if(scRequestNoteDtl.getJustification() != null){
						getManagedBean().setJustification(scRequestNoteDtl.getJustification());
					}
					
					if(scRequestNoteDtl.getAssignedUser() == null){
						scRequestNoteDtl.setAssignedUser(new PmUser());	
					}
					
					if(scRequestNoteDtl.getTplSpecZoneDtl() !=null){
						ZoneNames.put(scRequestNoteDtl.getTplSpecZoneDtl().getZoneName(), scRequestNoteDtl.getTplSpecZoneDtl().getSpecZoneId());
					}
					
					if(scRequestNoteDtl.getScRequestSupplierItem() == null){
						ScRequestSupplierItem  requestSupplierItem = new ScRequestSupplierItem();
						SmSupplier  smSupplier = new SmSupplier();
						requestSupplierItem.setSmSupplier(smSupplier);
						scRequestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
					}
					supplierLister(scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemId() ,scRequestNoteDtl.getPriceType().getParameterId() ,
							scRequestNoteDtl,scRequestNoteDtl.getRequestItem().getSupplierItems() ,scRequestNoteDtl.getAssignedUser().getUserId() , scRequestNoteDtl.getDeliverTermType().getParameterId());
					requestNoteDtls.add(scRequestNoteDtl);
				}
				getManagedBean().setRequestNote(requestNote);
				fulfilmentTypeEvent(requestNote.getRequestNoteDtls().get(0));
				getManagedBean().setRequestNoteDtl(requestNote.getRequestNoteDtls().get(0));
			}
			
			if(zoneFlag){
				// get grouped request note dtl objs for Item list
				List<ScRequestNoteDtl> itemList = groupRequestNoteDtls(requestNote.getRequestNoteDtls());
				getManagedBean().setRequestNoteDtls(itemList);
				
				// supplier rec list and sorting
				Map<String, Long> sortZoneNames = new TreeMap<String, Long>(ZoneNames);
				getManagedBean().setSupplierRecList(sortSupplierRecs(requestNoteDtls ,sortZoneNames));
				
			} else{
				getManagedBean().setRequestNoteDtls(requestNoteDtls);
			}
			
			// created by 
			getManagedBean().setCreatedBy(getCreatedBy(requestNote.getCreatedBy()).getUserName());
			// remarks
			requestNote.setRemarks(initializeRemarks(requestNote));

			if (requestNote.getSourceMethod().equalsIgnoreCase(ParameterConst.REQUEST_NOTE_SQ)) {
				getManagedBean().setSourceMethod(ParameterConst.METHOD_SQ);

			} else {
				getManagedBean().setSourceMethod(ParameterConst.METHOD_SC);
				getManagedBean().setFulfillmentType(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
			}
			// For status 
			if(getStatusDescById(docStatusId).equalsIgnoreCase("Pending RN Creation")){
				getCurrentStatus(DPStatusConstants.RN_PENDING_SUBMISSION);
			} else {
				// For status 
				if(docStatusId != null){
					getCurrentStatus(docStatusId);
				}
			}
			getManagedBean().setRequestNote(requestNote);
			initialInformation(requestNote);

		} finally {
			directPurchaseSO = null;
		}
	}


	//Group all the request note dtls by request item 
	public	List<ScRequestNoteDtl>	groupRequestNoteDtls(List<ScRequestNoteDtl>	requestNoteDtls){
		List<ScRequestNoteDtl>	results	=	new ArrayList<ScRequestNoteDtl>();
		
		Map<Long, ScRequestNoteDtl >	uniqueRequestNoteDtls	=	new HashMap<Long,  ScRequestNoteDtl>();
		for(ScRequestNoteDtl requestNoteDtl:requestNoteDtls){
			uniqueRequestNoteDtls.put(requestNoteDtl.getRequestItem().getRequestNoteItemId(), requestNoteDtl);
		}
		 		
		List<String>	sortItemList	=	new ArrayList<String>();
		Set<Long> itemIds	=	uniqueRequestNoteDtls.keySet();
		if(itemIds != null){
			for(Long requestItemId: itemIds){
				ScRequestNoteDtl	requestNoteDtl	=	uniqueRequestNoteDtls.get(requestItemId);
				results.add(requestNoteDtl);
				sortItemList.add(requestNoteDtl.getRequestItem().getItemName());
			}
			
			Collections.sort(sortItemList);
			results = sortItemList(results ,sortItemList);
		}
		return results;
	}

	private	List<ScRequestNoteDtl>	sortItemList(List<ScRequestNoteDtl>	requestNoteDtls , List<String>	sortItemList){
		List<ScRequestNoteDtl>	results	=	new ArrayList<ScRequestNoteDtl>();
		for(String itemName:sortItemList){

			for(ScRequestNoteDtl requestNoteDtl:requestNoteDtls){

				if(itemName.equalsIgnoreCase(requestNoteDtl.getRequestItem().getItemName())){
					results.add(requestNoteDtl);
				}
			}
		}
		return results;
	}

	private	List<ScRequestNoteDtl>	sortSupplierRecs(List<ScRequestNoteDtl>	requestNoteDtls, Map<String, Long>	sortSupplierRecs){
		List<ScRequestNoteDtl>	results	=	new ArrayList<ScRequestNoteDtl>();
		for(String zoneName:sortSupplierRecs.keySet()){

			for(ScRequestNoteDtl requestNoteDtl:requestNoteDtls){

				if(zoneName.equalsIgnoreCase(requestNoteDtl.getTplSpecZoneDtl().getZoneName())){
					results.add(requestNoteDtl);
				}
			}
		}
		return results;
	}

	 public void approverAction(String outcome) {
		debugLogger("approverAction");
		RequestNoteResDTO requestNoteResDTO = null;
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		try {
			requestNote.setRemarks(initializeRemarks(requestNote));
			if(!(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome) || BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome))){
				if(requestNote.getRemarks() != null ){
					for (int i = 0; i < requestNote.getRemarks().size(); i++) {
						ScRequestNoteRemark remark = requestNote.getRemarks().get(i);
						if(StringUtils.isBlank(remark.getRemark())){
							requestNote.getRemarks().remove(remark);
						}	
					} 
				}
			}
			
			if(getManagedBean().getPrInquiryId() != null){
				requestNoteReqDTO.setPurchaseInquiryId(getManagedBean().getPrInquiryId());
			}
			
			FacesScopes facesScopes = new FacesScopes();
			String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
			requestNoteReqDTO.setRequestNote(requestNote);
			requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
			requestNoteReqDTO.setToken(ctxToken);
			requestNoteReqDTO.setOutcome(outcome);
			requestNoteResDTO = directPurchaseSO.updateRequestNote(requestNoteReqDTO);
			
			if (requestNoteResDTO.isSuccessful()) {
				ScRequestNote scRequestNote = requestNoteResDTO.getRequestNoteDetail();
				if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)){
//					IPurchaseRequestOsbSO purchaseReqSO = (IPurchaseRequestOsbSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseRequestOsbSO.class);
//					PurchaseRequestOSBReqDTO reqDTO = new PurchaseRequestOSBReqDTO();
//					reqDTO.setRequestNoteId(getManagedBean().getRequestNote().getRequestNoteId());
//					reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
//					purchaseReqSO.splitPurchaseRequest(reqDTO);
					successMessage(MessageConst.INFO_700007, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE,scRequestNote.getRequestNoteNo());

				} else if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700026, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo());

				} else if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700009, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo());

				} else if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700011, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo());

				} else if(BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700008, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo());

					
				} else if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700010, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo());
				}	  
			} else{
				throw new EpException("Failed.");
			}
		}    
		finally {
			directPurchaseSO = null;
		}
	}
	 
	public List<SmSupplier> getSuppliersBySelectedItems() {
		List<SmSupplier> suppliers = new ArrayList<SmSupplier>();
		List<ScRequestNoteDtl>	selectedRequestNoteDtls	= getManagedBean().getSelectedRequestNoteDtls();
		
		if(selectedRequestNoteDtls != null) {
			int itemSize = selectedRequestNoteDtls.size();
			Map<SupplierPK, Integer> supplierMap = new HashMap<SupplierPK, Integer>();
			
			for (ScRequestNoteDtl requestNoteDtl : selectedRequestNoteDtls) {
				if (requestNoteDtl.getRequestItem().getSupplierItems() != null) {
					for (ScRequestSupplierItem supplierItem : requestNoteDtl.getRequestItem().getSupplierItems()) {
						SupplierPK supplierPK = new SupplierPK(supplierItem.getSmSupplier());
						
						Integer itemCount = supplierMap.get(supplierPK);
						if (itemCount != null) {
							itemCount += 1;
						} else {
							itemCount = 1;
						}
						supplierMap.put(supplierPK, itemCount);
					}
				}
			}
			
			for (Entry<SupplierPK, Integer> supplierEntry : supplierMap.entrySet()) {
				if (supplierEntry.getValue().intValue() == itemSize) {
					suppliers.add(supplierEntry.getKey().getSmSupplier());
				}
			}
		}
		
		return suppliers;
	}
	
	public void applyForRequestNote() {
	   log.info("applyForRequestNote");
	   IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	   ParameterReqDTO reqDTO = new ParameterReqDTO();
	   List<ScRequestNoteDtl>	requestNoteDtls	=	getManagedBean().getRequestNoteDtls();
	   if(getManagedBean().getSelectedRequestNoteDtls() != null && getManagedBean().getSelectedRequestNoteDtls().size() >0) {
		  for(ScRequestNoteDtl selectedReqDtl : getManagedBean().getSelectedRequestNoteDtls()) {
			 if(getManagedBean().getRequestNoteDtls().contains(selectedReqDtl)){
			    reqDTO.setParameterType(ParameterTypeConst.FULFILMENT_TYPE);
			    reqDTO.setParameterCode(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
				ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				selectedReqDtl.setFulfilmentType(parameterResDTO.getPmParameter());
				PmUser pmUser = new PmUser();
				pmUser.setUserId(getManagedBean().getAssignedToUserId());
			    selectedReqDtl.setAssignedUser(pmUser);
			    
			    reqDTO = new ParameterReqDTO();
			    reqDTO.setParameterType(ParameterTypeConst.DELIVERY_TERM);
			    reqDTO.setParameterCode(getManagedBean().getPeriodType());
			    parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			    selectedReqDtl.setDeliverTermType(parameterResDTO.getPmParameter());
				selectedReqDtl.setDeliverTerm(getManagedBean().getPeriodValue());
				requestNoteDtls.add(selectedReqDtl);
				getManagedBean().getRequestNoteDtls().remove(selectedReqDtl);
				getManagedBean().setRequestNoteDtls(requestNoteDtls);
			  }
			}
		} else {
			throw new EpException("Please select At least one Item");
		}
	}

	/**
	 * page navigation to  Request Note screen
	 */
	public void requestNoteAction() {
		debugLogger("requestNoteAction");

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "commonRequestNote");
	}

	/**
	  * initializing remarks in Request Note
	 */
	public List<ScRequestNoteRemark> initializeRemarks(ScRequestNote scRequestNote) {
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		FacesScopes facesScopes = new FacesScopes();
		List<ScRequestNoteRemark> results =   scRequestNote.getRemarks();
		if(results == null){
			results = new ArrayList<ScRequestNoteRemark>();
		} 
		if (results.size() == 0
				|| !compareUserName(results.get(0).getCreatedUser().getLoginId())) {
			ScRequestNoteRemark  remark = new ScRequestNoteRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setCreatedUser(facesScopes.getUserSessionBean());
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
	public void addToCart(Long cmSupplierItemId , boolean cart , List<CmSupplierItem> compareSupplierItems) {
		log.info("addToCart");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
		CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
		System.out.println("cmSupplierItemId:" + cmSupplierItemId);
		catReqDTO.setSupplierItemId(cmSupplierItemId);
		CatalogueResDTO catResDTO = catalogueSO.findActivePricing(catReqDTO);
		ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
		
		List<CmPricing> cmPricings = catResDTO.getPriceLs();
		if(cmPricings != null && cmPricings.size() > 0){
			CmSupplierItem cmSupplierItem = cmPricings.get(0).getCmSupplierItem();
			cmSupplierItem.setCmPricings(cmPricings);
			ScRequestItem requestItem = createRequestItem(cmSupplierItem);

			requestSupplierItem = createScRequestSupplierItem(requestItem, cmSupplierItem.getSmSupplier(), cmPricings);
			//ScSupplierItemPrice supplierItemPrice = createSupplierItemPrice(requestSupplierItem, cmPricings.get(0));
			if(requestSupplierItem.getSupplierItemPrices() != null && requestSupplierItem.getSupplierItemPrices().size()>0){
				requestSupplierItem.setSupplierItemPrice(requestSupplierItem.getSupplierItemPrices().get(0));
			}
			requestSupplierItem.setSupplierItemId(cmSupplierItem.getSmSupplier().getSupplierId());

			// TODO check supplier is government seller or not 
			if(cmSupplierItem.getSmSupplier().getPmOrgProfile() != null &&
					cmSupplierItem.getSmSupplier().getPmOrgProfile().isGovtSeller()){
				getManagedBean().setProcurementType(ParameterConst.PROC_TYPE_G2G);
				getManagedBean().setProcurementTypes(getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE , Boolean.TRUE));

			} else {
				getManagedBean().setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
				getManagedBean().setProcurementTypes(getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE , Boolean.FALSE));
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
				getManagedBean().setPriceType(ParameterConst.PRICE_TYPE_STANDARD);
				getManagedBean().setPriceTypes(getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, priceTypes));
				// required validation
				getManagedBean().setRequired(Boolean.TRUE);
				// TODO PriceType drop down list in request note
			}
		} else {
			// TODO - remove after catalogue team had add the filtering in view.
			throw new EpException("Price not found");
		}
		
		// copy suppliers from compare screen
		if(compareSupplierItems !=null){
			getManagedBean().setCompareSupplierItems(compareSupplierItems);
		}
		
		// set request supplier item.
		getManagedBean().setRequestSupplierItem(requestSupplierItem);
		
		getManagedBean().setJustRequired(Boolean.FALSE);
		getCurrentStatus(DPStatusConstants.RN_PENDING_SUBMISSION);
		
		// clear the justification type and justification remarks
		getManagedBean().setJustificationType("");
		getManagedBean().setJustification("");
		getManagedBean().setProcTypeOption(Boolean.FALSE);
		
		getManagedBean().setAddToCartFlag(cart);
		// update item details data table
		RequestContext.getCurrentInstance().execute("addToCartPopup.show();");
	}
	
	private ScRequestItem createRequestItem(CmSupplierItem cmSupplierItem){
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		ScRequestItem scRequestItem	= new ScRequestItem();
		CmItem cmItem = cmSupplierItem.getCmItem();
		String ItemCode = cmItem.getExtensionCode();
		CharSequence charSeq = ItemCode.subSequence(0, 2);
		Integer itemCode = Integer.valueOf(charSeq.toString());
		reqDTO.setParameterType(ParameterTypeConst.SC_ITEM_TYPE);
		// TODO checking first two digits in itemCode  whether service or product
		if (itemCode >= 70 && itemCode <= 94) { // for service  between  70 and 94  
			reqDTO.setParameterCode(ParameterConst.ITEM_TYPE_SERVICE);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			scRequestItem.setItemType(resDTO.getPmParameter());
		} else  {
			reqDTO.setParameterCode(ParameterConst.ITEM_TYPE_PRODUCT);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			scRequestItem.setItemType(resDTO.getPmParameter());
		}
		scRequestItem.setCmItem(cmItem);
		scRequestItem.setItemName(cmItem.getItemName());
		scRequestItem.setItemDesc(cmSupplierItem.getItemDesc());
		scRequestItem.setMinqty(cmSupplierItem.getMinOrder());
		
		if(cmSupplierItem.getPmUom()!=null){
			scRequestItem.setUom(cmSupplierItem.getPmUom());
		} else{
			throw new EpException("Uom is mandatory.");
		}
		return scRequestItem;
	}
	
	private ScRequestSupplierItem createScRequestSupplierItem(ScRequestItem requestItem, SmSupplier smSupplier, List<CmPricing> cmPricings) {
		ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
		requestSupplierItem.setSupplierItemId(smSupplier.getSupplierId());
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
		
		// zone dtl
		if(pricing.getCmSupplierZone() != null){
			result.setTplSpecZone(createSpecZone(pricing.getCmSupplierZone()));
		}
		
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
	
	private TplSpecZoneDtl createSpecZone(CmSupplierZone supplierZone) {
		TplSpecZoneDtl specZoneDtl = null;
		if(supplierZone != null) {
			if(supplierZone.getCmSupplierZoneDtls() != null) {
				specZoneDtl = new TplSpecZoneDtl();
				specZoneDtl.setZoneName(supplierZone.getSuppZoneName());
				ScRNZone zone = getManagedBean().getScRNZone(); 
				List<TplSpecZoneLoc> zoneLocs = new ArrayList<TplSpecZoneLoc>();
				for(CmSupplierZoneDTL zoneDtl : supplierZone.getCmSupplierZoneDtls()) {
					TplSpecZoneLoc specZoneLoc = new TplSpecZoneLoc();
					specZoneLoc.setPmDistrict(zoneDtl.getPmDistrict());
					specZoneLoc.setPmState(zoneDtl.getPmState());
					specZoneLoc.setPmDivision(zoneDtl.getPmDivision());
					specZoneLoc.setTplSpecZoneDtl(specZoneDtl);
					zoneLocs.add(specZoneLoc);
				}
				zone.setScRequestNote(getManagedBean().getRequestNote());
				getManagedBean().getRequestNote().setScRNZone(zone);
				specZoneDtl.setTplSpecZone(zone);
				specZoneDtl.setTplSpecZoneLocs(zoneLocs);
			}
		}
		return specZoneDtl;
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
	} finally {
			
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
	  } finally {
			
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
		log.info("add item");
		getManagedBean().setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
		ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
		ScRequestItem requestItem = new ScRequestItem();
    	requestSupplierItem.setRequestNoteItem(requestItem);
		getManagedBean().setRequestSupplierItem(requestSupplierItem);
		getManagedBean().setProcurementType(null);
		getManagedBean().setJustification(null);
		getManagedBean().setJustificationType(null);
        getManagedBean().setProcTypeOption(Boolean.FALSE);
		
		return "catalogueSearch";
	}

	public String addToRequestNote(boolean cartFlag) {
		log.info("addToRequestNote");
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				ParameterReqDTO reqDTO = new ParameterReqDTO();
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		List<ScRequestNoteDtl>	requestNoteDtls = getManagedBean().getRequestNoteDtls();
		ScRequestSupplierItem requestSupplierItem = getManagedBean().getRequestSupplierItem();
		ScRequestNoteDtl scRequestNoteDtl = new ScRequestNoteDtl();
		ScRequestItem scRequestItem = requestSupplierItem.getRequestNoteItem();
		// add to cart validation
		getManagedBean().setRequired(Boolean.FALSE);
		getManagedBean().setJustRequired(Boolean.FALSE);
		
		if(scRequestItem.getQuantity() !=null && scRequestItem.getQuantity().intValue() < 0){
			throw new EpException(MessageConst.INFO_100033, new String[]{" Order Quatity" ,"0"});
		}
		
		ScSupplierItemPrice scSupplierItemPrice = requestSupplierItem.getSupplierItemPrice();
		CmSupplierItem selectedSupplierItem = scSupplierItemPrice.getPricing().getCmSupplierItem();
		ScRequestNote  requestNote = getManagedBean().getRequestNote();
		
		if(getManagedBean().getSourceMethod().equalsIgnoreCase(ParameterConst.METHOD_SQ)){
			// Simple Quote
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
			
		} else if(getManagedBean().getSourceMethod().equalsIgnoreCase(ParameterConst.METHOD_PRI)){
			// purchase inquiry
			if(getManagedBean().getRequestNoteDtls() != null){
				requestSupplierItem.setScPrInquiy(getManagedBean().getRequestNoteDtls().get(0).getScRequestSupplierItem().getScPrInquiy());		
			}
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);

		} else {
			// purchase in default & add to cart
		    requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
		    List<ScRequestSupplierItem> supplierItems = new ArrayList<ScRequestSupplierItem>();
		    if(cartFlag){
		    	// adding suppliers from the compare screen 
		    	if(getManagedBean().getCompareSupplierItems() != null){

		    		for (CmSupplierItem supplierItem : getManagedBean().getCompareSupplierItems() ) {
		    			if(supplierItem.getCmItem().getItemId().equals(selectedSupplierItem.getCmItem().getItemId())){
		    				ScRequestSupplierItem scRequestSupplierItem = createScRequestSupplierItem(
		    						scRequestItem, supplierItem.getSmSupplier(), supplierItem.getCmPricings());
		    				supplierItems.add(scRequestSupplierItem);	
		    			}
		    		}
		    	}
		    } else{
		    	// adding suppliers from search catalogue 
		    	IViewSupplierItemSO viewSupplierItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, IViewSupplierItemSO.class);
		    	CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();
		    	catalogueReqDTO.setViewSearchBean(new CatalogueBackingBean().getManagedBean().getSearchDataModel().getSearchBean());
		    	catalogueReqDTO.setSupplierItemId(selectedSupplierItem.getSupplierItemId());
		    	catalogueReqDTO.setExtensionCode(scRequestItem.getCmItem().getExtensionCode());
		    	CatalogueResDTO catalogueResDTO = viewSupplierItemSO.getSuppliersItemPricingByExtensionCode(catalogueReqDTO);

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
		    }
		   
		  
		    scRequestItem.setSupplierItems(supplierItems);
		    scRequestItem.setRequestNote(requestNote);
		}
		
		requestSupplierItem.setRequestNoteItem(scRequestItem);
		requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
		scRequestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
		scRequestNoteDtl.setRequestItem(scRequestItem);

		FacesScopes facesScopes = new FacesScopes();
		scRequestNoteDtl.setAssignedUser(facesScopes.getUserSessionBean());
		reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
		reqDTO.setParameterCode(getManagedBean().getProcurementType());
		ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
		scRequestNoteDtl.setProcurementType(resDTO.getPmParameter());
		
		if (scRequestNoteDtl.getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC);
			reqDTO.setParameterCode(getManagedBean().getJustificationType());
			resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			scRequestNoteDtl.setJustificationType(resDTO.getPmParameter());
		} else if (scRequestNoteDtl.getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY);
			reqDTO.setParameterCode(getManagedBean().getJustificationType());
		    resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			scRequestNoteDtl.setJustificationType(resDTO.getPmParameter());
			
		} else{
			scRequestNoteDtl.setJustificationType(null);
		}
		
		scRequestNoteDtl.setJustification(getManagedBean().getJustification());
		scRequestNoteDtl.setScRequestNote(requestNote);
		
		reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(ParameterTypeConst.DELIVERY_TERM);
		reqDTO.setParameterCode(ParameterConst.SQ_DURATION_DAY);
	    resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
		scRequestNoteDtl.setDeliverTermType(resDTO.getPmParameter());
		
	    reqDTO = new ParameterReqDTO();
	    reqDTO.setParameterType(ParameterTypeConst.FULFILMENT_TYPE);
	    reqDTO.setParameterCode(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
	    resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
	    scRequestNoteDtl.setFulfilmentType(resDTO.getPmParameter());
	    
	    reqDTO = new ParameterReqDTO();
	    reqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
	    reqDTO.setParameterCode(getManagedBean().getPriceType());
	    resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
	    scRequestNoteDtl.setPriceType(resDTO.getPmParameter());
	    
	    if(selectedSupplierItem.getLeadTime() != null){
	    	 scRequestNoteDtl.setDeliverTerm(selectedSupplierItem.getLeadTime().intValue());
	    }
        
	    // validating suppliers
	    RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
	    requestNoteReqDTO.setRequestNoteDtl(scRequestNoteDtl);
	    directPurchaseSO.validateAddToCart(requestNoteReqDTO);
	    
		//TODO have to compare duplicate items in request note
		if (getManagedBean().getRequestNoteDtls().isEmpty()){
			getManagedBean().getRequestNoteDtls().add(scRequestNoteDtl);
			
			// set by default selected price type , rec.supplier and offer price
			supplierLister(scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemId() ,scRequestNoteDtl.getPriceType().getParameterId() ,
					scRequestNoteDtl,scRequestNoteDtl.getRequestItem().getSupplierItems() ,scRequestNoteDtl.getAssignedUser().getUserId() ,scRequestNoteDtl.getDeliverTermType().getParameterId());
		} else {
			List<Long> itemIds = new ArrayList<Long>();
			for (ScRequestNoteDtl noteDtl : getManagedBean().getRequestNoteDtls()) {
				itemIds.add(noteDtl.getScRequestSupplierItem().getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getItemId());
			}
			if (itemIds.size() > 0) {
				if(itemIds.contains(scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getItemId())){
					throw new EpException(MessageConst.INFO_700027, new String[]{scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem().getItemName(),""});
				} else {
					requestNoteDtls.add(scRequestNoteDtl);
					// set by default selected price type , rec.supplier and offer price
					supplierLister(scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemId() ,scRequestNoteDtl.getPriceType().getParameterId() ,
							scRequestNoteDtl,scRequestNoteDtl.getRequestItem().getSupplierItems() , scRequestNoteDtl.getAssignedUser().getUserId() ,scRequestNoteDtl.getDeliverTermType().getParameterId());
					getManagedBean().setRequestNoteDtls(requestNoteDtls);
				}
			}
		}
		requestNote.setRequestNoteDtls(requestNoteDtls);

		// to get ptj
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
			profileReqDTO.setUserId(user.getUserId());
			profileReqDTO.setOrgType(OrganizationType.DIVISION.value());
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(profileReqDTO);
			if(orgProfileResDTO.getOrgProfileList() != null && orgProfileResDTO.getOrgProfileList().size()>0){
				requestNote.setPmOrgProfile(orgProfileResDTO.getOrgProfileList().get(0));
				ptjNameEvent();
			}
		}
		
		if(requestNote.getCreatedUser() == null){
			getManagedBean().setCreatedBy(getCreatedBy(facesScopes.getUserSessionBean().getUserId()).getUserName());
		}
		requestNote.setRemarks(initializeRemarks(requestNote));
		initialInformation(requestNote);
		getManagedBean().setRequestNote(requestNote);
	
		getCurrentStatus(DPStatusConstants.RN_PENDING_SUBMISSION);
		getManagedBean().setFulfillmentType(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
		fulfilmentTypeEvent(scRequestNoteDtl);
		getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
		
		// request note counter
		RequestContext.getCurrentInstance().execute("addToCartPopup.hide()");
		RequestContext.getCurrentInstance().update("btnId");
		
		// compare screen add to cart - page navigation to request note screen
		if(cartFlag){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "commonRequestNote");
		} 
		
		return null;
	}

	public List<PmParameter>  getPriceTypeByPrice(List<ScSupplierItemPrice> supplierItemPrices) {
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		List<Long> checkIds = new ArrayList<Long>();
		List<PmParameter> params = new ArrayList<PmParameter>();
		for (ScSupplierItemPrice scSupplierItemPrice : supplierItemPrices) {
			if(scSupplierItemPrice.getPriceType() != null){
				if(scSupplierItemPrice.getPriceType().getParameterId() != null){
					reqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
					reqDTO.setParameterCode(scSupplierItemPrice.getPriceType().getParameterCode());
					ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
					scSupplierItemPrice.setPriceType(resDTO.getPmParameter());
				}
				
				if(!checkIds.contains(scSupplierItemPrice.getPriceType().getParameterId())){
					checkIds.add(scSupplierItemPrice.getPriceType().getParameterId());
					params.add(scSupplierItemPrice.getPriceType());
				}
			}
		}
		return params;
	}

	/**
	 * Event for procurement type  drop down in Add to Cart
	 */
	public void procurementTypeEvent() {
		log.info("procurementTypeEvent"); 
		if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL) || getManagedBean().getProcurementType().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);
			   getManagedBean().setJustRequired(Boolean.FALSE);
			
		} else if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
		    getManagedBean().setJustDropdown(getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));
		    getManagedBean().setJustRequired(Boolean.TRUE);
			
		} else if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
            getManagedBean().setProcTypeOption(Boolean.TRUE);
            getManagedBean().setJustDropdown(getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY));
            getManagedBean().setJustRequired(Boolean.TRUE);
		}
	}
	
	/**
	 * Event for justification type drop down in Add to Cart
	 */
	public void justificationTypeEvent() {
		log.info("justificationTypeEvent");
		/*if (getManagedBean().getJustificationType()
				.equalsIgnoreCase(ParameterTypeConst.JUSTIFICATION_TYPE_OTHER)) {
			getManagedBean().setJustTypeOption(true);
		} else {
			getManagedBean().setJustTypeOption(false);
		}*/
	}
	
	/**
	 * show Item Details, Justification, Pricing details in Request Note
	 */
	public void showReqNotePopUps(ScRequestNoteDtl scRequestNoteDtl){
		log.info("showReqNotePopUps");
		
		if(scRequestNoteDtl.getScRequestSupplierItem() != null){
			ScRequestSupplierItem  scRequestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();

			String ItemCode = scRequestNoteDtl.getRequestItem().getCmItem().getExtensionCode();
			CharSequence charSeq = ItemCode.subSequence(0, 2); 
			Integer itemCode = Integer.valueOf(charSeq.toString()); 

			// TODO checking first two digits in itemCode whether service or product 
			if (itemCode >= 70 && itemCode <= 94) { // for service  between  70 and 94  
				getManagedBean().setItemCheck(Boolean.TRUE); 

			} else if (itemCode < 70 || itemCode > 94) { // for product  < 70 and  > 94  
				getManagedBean().setItemCheck(Boolean.FALSE); 
			}

			if(scRequestSupplierItem.getRequestNoteItem() == null){
				scRequestSupplierItem.setRequestNoteItem(scRequestNoteDtl.getRequestItem());
			}

			if(scRequestSupplierItem.getSupplierItemPrices() != null && scRequestSupplierItem.getSupplierItemPrices().size() >0){
				for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {
					if(scSupplierItemPrice.getPriceType().getParameterId() == scRequestNoteDtl.getPriceType().getParameterId()){
						scRequestSupplierItem.getSupplierItemPrice().setPriceType(scSupplierItemPrice.getPriceType());
					}
				}
			}
			getManagedBean().setRequestSupplierItem(scRequestSupplierItem);
			getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
		}
	}

	/**
	 * show pricing details in Request Note
	 */
	public void showPricing(ScRequestNoteDtl scRequestNoteDtl){
		log.info("showPricing");
		List<ScSupplierItemPrice>	supplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		if(scRequestNoteDtl.getScRequestSupplierItem() != null){
			ScRequestSupplierItem  scRequestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();

			if(scRequestSupplierItem.getSupplierItemPrices() != null && scRequestSupplierItem.getSupplierItemPrices().size() >0){
				for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {

					if(scSupplierItemPrice.getPriceType().getParameterId() == scRequestNoteDtl.getPriceType().getParameterId()){
						supplierItemPrices.add(scSupplierItemPrice);
					}
				}
			}
			getManagedBean().setRequestSupplierItem(scRequestSupplierItem);
			getManagedBean().setPricingList(supplierItemPrices);
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
		if(requestNoteDtl.getFulfilmentType() != null ){
			if (requestNoteDtl.getFulfilmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
				userlist = userListByRole(RoleCode.REQUISITIONER.value());
				getManagedBean().setUserList(userlist);

			} else {
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
			if(getManagedBean().getRequestNote().getPmOrgProfile()!= null){
			  reqDTO.setOrgProfileId(getManagedBean().getRequestNote().getPmOrgProfile().getOrgProfileId());
			}
			IUserSO reqSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserResDTO resDTO = reqSO.retrieveUserListByRole(reqDTO);
				options = resDTO.getUserList();
			log.info("result size: " +options.size());
		} finally{
			
		}
		log.info("userListByRole - End");
		return options;
	}
	
	public void deleteRequestNoteDtl() {
		log.info("deleteRequestNoteDtl");
		if (getManagedBean().getSelectedRequestNoteDtls() != null && !getManagedBean().getSelectedRequestNoteDtls().isEmpty()) {
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
			
		} else {
			throw new EpException("Please select At least one Item");
		}
		
		// update item details data table
		RequestContext.getCurrentInstance().update("requestListDataTable");
	}
	
	/**
	  * To get Approver Pick List  in Request Note
	 */
	private	List<ScRequestNoteApprovalUser> createTargetApprover(ScRequestNote requestNote){
		List<ScRequestNoteApprovalUser> results = new ArrayList<ScRequestNoteApprovalUser>();
		for (PmUser pmUser :  getManagedBean().getApprovalUsers().getTarget()) {
			ScRequestNoteApprovalUser approver = new ScRequestNoteApprovalUser();
			approver.setPmUser(pmUser);
			approver.setRequestNote(requestNote);
			results.add(approver);
		}
		return results;
	}
	
	public void  getCurrentStatus(Long statusId) {
		if (statusId != null) {
			getManagedBean().setRequestNoteStatus(getStatusDescById(statusId));
		}
	}
	
	public PmUser  getCreatedBy(Long createdBy) {
		PmUser user = null;
		IUserSO userSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		if(createdBy != null){
			reqDTO.setUserId(createdBy);
			UserResDTO resDTO = userSO.retrieveUserDetails(reqDTO);

			if(resDTO.getPmUser() != null){
				 user = resDTO.getPmUser();
			}
		}
		return user;
	}
	
	 public void initialInformation(ScRequestNote requestNote){
		    DualListModel<PmUser>	approvalUsers;
			IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDto = new UserReqDTO();
			userReqDto.setRoleCode(RoleCode.RN_APPROVER.value());
			
			if(requestNote.getPmOrgProfile() != null){
			   userReqDto.setOrgProfileId(requestNote.getPmOrgProfile().getOrgProfileId());
			}
			
			UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
			List<PmUser> availableApprover	=	userResDTO.getUserList();
			List<PmUser> selectedApprover	=	new ArrayList<PmUser>();
			approvalUsers =	new DualListModel<PmUser>(availableApprover, selectedApprover);
			
		    if ( requestNote != null && requestNote.getApprovalUsers().size() > 0) {
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

	
	public void ptjNameEvent() {
		IPurchaseInDefaultSO purchaseInDefaultSO  = null;
		try {
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
					requestItem.setCmItem(scRequestNoteDtl.getScRequestSupplierItem().getSupplierItemPrice().getPricing().getCmSupplierItem().getCmItem());
					
					// TODO selected ptj id for non contract item for pid
					purchaseInDefaultReqDTO.setOrgProfileId(scRequestNoteDtl.getScRequestNote().getPmOrgProfile().getOrgProfileId());
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
					requestSupplierItem.setRequestNoteItem(requestItem);

					// request Note Items
					requestNoteItems.add(requestItem);
					//TODO call initialInformation &  fulfilmentTypeEvent methods based on ptj
					initialInformation(scRequestNoteDtl.getScRequestNote());
					fulfilmentTypeEvent(scRequestNoteDtl);
				}
			}
			this.getManagedBean().setRequestNoteDtls(requestNoteDtls);
			getManagedBean().getRequestNote().setRequestNoteDtls(requestNoteDtls);
			getManagedBean().getRequestNote().setRequestNoteItems(requestNoteItems);
			
		} finally {
			purchaseInDefaultSO = null;
		}
	}
	
	/**
	 * showPurchaseInDefault method to show purchase in default  in Request Note
	 */
	public void showPurchaseInDefault(ScRequestNoteDtl requestNoteDtl){
		debugLogger("showPurchaseInDefault");
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
				purchaseInDefaultBackingBean.getCurrentStatus(DPStatusConstants.PID_COMPLETED);
				List<ScPurchaseInDefaultAttachment> attachments = new ArrayList<ScPurchaseInDefaultAttachment>();
				ScPurchaseInDefaultAttachment attachment =purchaseInDefault.getScPurchaseInDefaultAttachment();
				attachment.setUser(facesScopes.getUserSessionBean());
				attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				attachments.add(attachment);
				purchaseInDefaultBackingBean.getManagedBean().setAttachments(attachments);
				
				// disable all for pid view
				purchaseInDefaultBackingBean.getManagedBean().setPidBtnFlag(Boolean.FALSE);
				
				// TODO current page url for page navigation from pid to request note
				purchaseInDefaultBackingBean.getManagedBean().setPageNavToRequestNote(fc.getViewRoot().getViewId());
			} 
		}

		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "purchaseInDefaultMain");
	 } finally {
		 purchaseInDefaultSO = null;
	 }
	}


	/**
	 * showPurchaseInquiry method to  show Purchase Inquiry  in Request Note
	 */
	public void showPurchaseInquiry(ScRequestNoteDtl scRequestNoteDtl){
		debugLogger("showPurchaseInquiry");
		IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
		PurchaseInquiryBackingBean purchaseInquiryBackingBean = new PurchaseInquiryBackingBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		try{
			if(scRequestNoteDtl.getScRequestSupplierItem().getScPrInquiy() != null){
				//Purchase Inquiry
				PrInquiryReqDTO prInquiryReqDTO = new PrInquiryReqDTO();
				ScPrInquiry scPrInquiry = scRequestNoteDtl.getScRequestSupplierItem().getScPrInquiy();
				prInquiryReqDTO.setPrInquiryId(scPrInquiry.getPrInquiryId());
				prInquiryReqDTO.setFetchSubEntity(true);
				PrInquiryResDTO prInquiryResDTO = prInquirySO.getPrInquiry(prInquiryReqDTO);
				ScPrInquiry  prInquiry  = prInquiryResDTO.getPrInquiryDetail();
				// status
				if(prInquiry.getWorkflowStatuses() != null && prInquiry.getWorkflowStatuses().size() >0){
					purchaseInquiryBackingBean.getCurrentStatus(prInquiry.getWorkflowStatuses().get(0).getPmStatus().getStatusId());
				}
				// remarks
				prInquiry.setRemarks(purchaseInquiryBackingBean.initializeRemarks(prInquiry));
				if(prInquiry.getScRequestSupplierItems() != null && prInquiry.getScRequestSupplierItems().size()>0 ){

					// get selected supplier 
					ScRequestSupplierItem requestSupplierItem = prInquiry.getScRequestSupplierItems().get(0);
					ScRequestNoteDtl requestNoteDtl = requestSupplierItem.getScRequestNoteDtl().get(0);

					// lead time 
					if(requestSupplierItem.getSupplierItemPrices() != null && requestSupplierItem.getSupplierItemPrices().size() >0){
						requestSupplierItem.getRequestNoteItem().setLeadTime( requestSupplierItem.getSupplierItemPrices().get(0).getPricing().getCmSupplierItem().getLeadTime());
					}

					// price type drop down
					List<String> priceTypes= new ArrayList<String>();
					priceTypes.add(requestNoteDtl.getPriceType().getParameterCode());
					purchaseInquiryBackingBean.getManagedBean().setPriceType(requestNoteDtl.getPriceType().getParameterCode());
					purchaseInquiryBackingBean.getManagedBean().setPriceTypes(getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, priceTypes));

					purchaseInquiryBackingBean.getManagedBean().setPmOrgProfile(requestNoteDtl.getScRequestNote().getPmOrgProfile());
					purchaseInquiryBackingBean.getManagedBean().setRequestNoteDtl(requestNoteDtl);
					purchaseInquiryBackingBean. getManagedBean().setScRequestSupplierItem(requestSupplierItem);

					// no of response days
					 prInquiry.setDayToResponse((int)DateUtils.getDaysDifference(prInquiry.getCreatedDate(),prInquiry.getDateToResponse()));
					 
					purchaseInquiryBackingBean.getManagedBean().setStockAvailable(prInquiry.getIsStockAvailable());
					purchaseInquiryBackingBean.getManagedBean().setScPrInquiry(prInquiry);
					purchaseInquiryBackingBean.initializeRemarks(prInquiry);
				}
			}

			// title checker
			purchaseInquiryBackingBean.getManagedBean().setTitleChecker(Boolean.FALSE);
			// TODO current page url for page navigation from pid to request note
			purchaseInquiryBackingBean.getManagedBean().setRnToPIFlag(Boolean.FALSE);
			purchaseInquiryBackingBean.getManagedBean().setPageNavToRequestNote(fc.getViewRoot().getViewId());
			fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "prInquiryMain");

		} finally {
			prInquirySO = null;
		}
	}

	/**
	 * showSimpleQuote method to  show Simple Quote  in Request Note Header Icon
	 */
	public void showSimpleQuote(){
		debugLogger("showSimpleQuote");
		SimpleQuoteBackingBean simpleQuoteBackingBean = new SimpleQuoteBackingBean();
		TemplateSpecBean productTplSpecBean = simpleQuoteBackingBean.getManagedBean().getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = simpleQuoteBackingBean.getManagedBean().getServiceTemplateSpecBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		try{
			if(getManagedBean().getScQuote() != null){
				
				ScQuote scQuote = getManagedBean().getScQuote();
				
				if(scQuote.getJustificationType() == null){
					scQuote.setJustificationType(new PmParameter());
				}
				
				// service and product
				if(scQuote.getQuoteDocs() != null){
					for (ScQuoteDoc scQuoteDoc : scQuote.getQuoteDocs()) {
						TplSpecMaster specMaster = scQuoteDoc.getSpecMaster();
						if(ParameterConst.SQ_ITEM_LIST_PRODUCT.equalsIgnoreCase(specMaster.getItemTypeCode().getParameterCode())){
							productTplSpecBean.setSpecMaster(specMaster);
							simpleQuoteBackingBean.getManagedBean().setProductTemplateSpecBean(productTplSpecBean);
							// service Tpl Spe
						} else  if(ParameterConst.SQ_ITEM_LIST_SERVICE.equalsIgnoreCase(specMaster.getItemTypeCode().getParameterCode())){
							serviceTplSpecBean.setSpecMaster(specMaster);
							simpleQuoteBackingBean.getManagedBean().setServiceTemplateSpecBean(serviceTplSpecBean);
						}
					}
				}
				// zone details
				if(scQuote.getQuoteZone() != null){
					if(scQuote.getQuoteZone().getTplSpecZoneDtls() != null && scQuote.getQuoteZone().getTplSpecZoneDtls().size() >0){
						for (TplSpecZoneDtl tplSpecZoneDtl : scQuote.getQuoteZone().getTplSpecZoneDtls() ) {
							if(tplSpecZoneDtl.getTplSpecZoneLocs() != null && tplSpecZoneDtl.getTplSpecZoneLocs().size()>0){
								simpleQuoteBackingBean.getManagedBean().getZoneTrees().add(LocationSingleton.getInstance().constuctTreeNode(tplSpecZoneDtl, tplSpecZoneDtl.getTplSpecZoneLocs()));
							}
						}
					}
				}

				// by locality
				List<PmState> states = new ArrayList<PmState>();
				if(scQuote.getLocationStateCriterias() != null) {
					if(scQuote.getLocationStateCriterias().size() >0){
						for (ScLocationStateCriteria locationStateCriteria : scQuote.getLocationStateCriterias() ) {
							//states.add(locationStateCriteria.getPmState());
						}
					}
				}
				List<PmDivision> divisions = new ArrayList<PmDivision>();
				if(scQuote.getLocationDivisionCriterias() != null) {
					if(scQuote.getLocationDivisionCriterias().size() >0){
						for (ScLocationDivisionCriteria locationDivisionCriteria : scQuote.getLocationDivisionCriterias() ) {
							divisions.add(locationDivisionCriteria.getPmDivision());
						}
					}
				}	
				List<PmDistrict> districts = new ArrayList<PmDistrict>();
				if(scQuote.getLocationDistrictCriterias() != null) {
					if(scQuote.getLocationDistrictCriterias().size() >0){
						for (ScLocationDistrictCriteria locationDistrictCriteria : scQuote.getLocationDistrictCriterias() ) {
							districts.add(locationDistrictCriteria.getPmDistrict());
						}
					}
				}	
				if(states.size() > 0 || divisions.size() > 0 || districts.size() > 0){
					if(simpleQuoteBackingBean.getManagedBean().getLocationTrees() == null || simpleQuoteBackingBean.getManagedBean().getLocationTrees().isEmpty()){
					simpleQuoteBackingBean.getManagedBean().getLocationTrees().add(
							LocationSingleton.getInstance().buildLocationTree(null, states,
									districts, divisions, null,TplSpecZoneLoc.class));
					}
				}
				getManagedBean().getRequestNote().setPmOrgProfile(scQuote.getRequestNote().getPmOrgProfile());
				// location level by default - district 
				simpleQuoteBackingBean.getManagedBean().setLocationLevel(ParameterConst.LOCATION_LEVEL_DISTRICT);
				// business nature details
				if(scQuote.getBusinessNatureCriterias() !=null && scQuote.getBusinessNatureCriterias().size() > 0){
					simpleQuoteBackingBean.getManagedBean().setByBusinessNature(Boolean.TRUE);
					simpleQuoteBackingBean.getManagedBean().setBusinessNatureCriterias(scQuote.getBusinessNatureCriterias());
				}
				
				// supplier list
				if(scQuote.getScQuoteSuppliers() != null && scQuote.getScQuoteSuppliers().size() > 0){
					simpleQuoteBackingBean.getManagedBean().setSupplierList(scQuote.getScQuoteSuppliers());
				}
				
				// remarks
				scQuote.setRemarks(simpleQuoteBackingBean.initializeRemarks(scQuote));
				
				// status
				if(scQuote.getWorkflowStatuses() != null && scQuote.getWorkflowStatuses().size() >0){
					simpleQuoteBackingBean.getCurrentStatus(scQuote.getWorkflowStatuses().get(0).getPmStatus().getStatusId());
				}
				
				// procurement type drop down.
				if(scQuote.getProcurementType() != null){
					if(scQuote.getProcurementType().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)){
						simpleQuoteBackingBean.getManagedBean().setProcurementTypes(getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE , Boolean.TRUE));
					} else {
						simpleQuoteBackingBean.getManagedBean().setProcurementTypes(getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE , Boolean.FALSE));
					} 
				}
				simpleQuoteBackingBean.getManagedBean().setScQuote(scQuote);
				// country id
				CountryReqDTO countryReqDTO = new CountryReqDTO();
				countryReqDTO.setCountryCode("MY");
				ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
				CountryResDTO resDTO = reqSO.retrieveCountryByCode(countryReqDTO);
				simpleQuoteBackingBean.getManagedBean().setDefaultCountryId(resDTO.getPmCountry().getCountryId());
				simpleQuoteBackingBean.getManagedBean().setRequestNote(getManagedBean().getRequestNote());
				
				
				// duration calc
				try {
					simpleQuoteBackingBean.dateListener();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				procurementTypeEvent();
				// title checker
				simpleQuoteBackingBean.getManagedBean().setTitleChecker(Boolean.FALSE);
				
				// TODO current page url for page navigation from pid to request note
				simpleQuoteBackingBean.getManagedBean().setRnToSQFlag(Boolean.FALSE);
				simpleQuoteBackingBean.getManagedBean().setPageNavToRequestNote(fc.getViewRoot().getViewId());
				
				// page navigation to simple quote
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "simpleQuote");
			}
		} finally {
		}
	}
	
	public void closeListener() {
		log.info("closeListener");
		// add to cart validation
		RequestContext.getCurrentInstance().execute("addToCartPopup.hide()");
		getManagedBean().setRequired(Boolean.FALSE);
		getManagedBean().setJustRequired(Boolean.FALSE);
	}
	
	public void supplierLister(Long supplierId ,Long priceTypeId ,ScRequestNoteDtl requestNoteDtl , List<ScRequestSupplierItem> supplierItems , Long assignedUserId , Long deliveryTermId) {
		debugLogger("supplierLister");
		for (ScRequestSupplierItem scRequestSupplierItem : supplierItems) {
			if(scRequestSupplierItem.getSmSupplier().getSupplierId().equals(supplierId)){
				if(priceTypeId != null && priceTypeId > 0){

					if(scRequestSupplierItem.getSupplierItemPrices()!= null && scRequestSupplierItem.getSupplierItemPrices().size()>0){

						for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {
							
							// zonal item price  
							if(requestNoteDtl.getTplSpecZoneDtl() !=null){
								if(requestNoteDtl.getTplSpecZoneDtl().getSpecZoneId().equals(scSupplierItemPrice.getTplSpecZone().getSpecZoneId())){
									scRequestSupplierItem.setSupplierItemPrice(scSupplierItemPrice);
									requestNoteDtl.setPriceType(scSupplierItemPrice.getPriceType());
									break;
								}
							}else {
								 // non zonal item price 
								if(scSupplierItemPrice.getPriceType() != null){
									if(scSupplierItemPrice.getPriceType().getParameterId().equals(priceTypeId)){
										scRequestSupplierItem.setSupplierItemPrice(scSupplierItemPrice);
										requestNoteDtl.setPriceType(scSupplierItemPrice.getPriceType());
										break;
									}
								}
							}
						}
					}
				} else{
					//requestNoteDtl.getPriceType().setParameterId(null);
				}
				// selected rec.supplier
				scRequestSupplierItem.setSupplierItemId(supplierId);
				requestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);
				// prepared by
				if(getManagedBean().getUserList() != null && getManagedBean().getUserList().size()>0){
					for (PmUser user  : getManagedBean().getUserList()) {
						if(user.getUserId().equals(assignedUserId)){
							requestNoteDtl.setAssignedUser(user);	
						}
					}
				}
				
				// delivery term
				if(deliveryTermId != null){
					IParameterSO  parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterId(deliveryTermId);
					ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
					requestNoteDtl.setDeliverTermType(resDTO.getPmParameter());
				}
			}
		}
	}

 /**
  * selected assgned user to apply supplier recomendation list.
  */
	public void applyForSupplierRecs(ScRequestNoteDtl selectedReqDtl  , Long assignedUserId){ 
		log.info("applyForSupplierRecs");
		List<ScRequestNoteDtl>	requestNoteDtls	= new ArrayList<ScRequestNoteDtl>();
		if(getManagedBean().getSupplierRecList() != null && getManagedBean().getSupplierRecList().size() >0) {
			for(ScRequestNoteDtl requestNoteDtl : getManagedBean().getSupplierRecList()) {

				if(requestNoteDtl.getRequestItem().getRequestNoteItemId().equals(selectedReqDtl.getRequestItem().getRequestNoteItemId())){
					requestNoteDtl.setAssignedUser(getCreatedBy(assignedUserId));
				}
				requestNoteDtls.add(requestNoteDtl);
			}
			getManagedBean().setSupplierRecList(requestNoteDtls);
		} 
	}

	/**
	 * Get parameter code select items by parameter type for radio buttons.
	 * 
	 * @param parameterType
	 * 			the parameter type
	 * @return array of SelectItem
	 */
	public SelectItem[] getRadioButtonCodeValues(String parameterType , boolean g2gSeller) {
		return getRadioButtonCodeValuesBySearchBean(parameterType, null , g2gSeller);
	}
	
	public SelectItem[] getRadioButtonCodeValuesBySearchBean(String parameterType, ISearchBean<PmParameterDesc> searchBean , boolean g2gSeller) {
		SelectItem[] items = null;
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(parameterType, searchBean);

		items = new SelectItem[parameterDescs.size()];
		int i = 0;
		for(PmParameterDesc parameterDesc : parameterDescs) {
			if(g2gSeller){
				if(parameterDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)){
					items[i] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(), parameterDesc.getCodeName() , null , !g2gSeller );
				} else {
					items[i] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(), parameterDesc.getCodeName() , null , g2gSeller);
				}
			} else{

				if(parameterDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)){
					items[i] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(), parameterDesc.getCodeName() , null , !g2gSeller);
				} else{
					items[i] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(), parameterDesc.getCodeName() , null , g2gSeller);
				}
			}
			
			i++;
		}
		return items;
	}
	
	public boolean  checkPriceOffered(String priceTypeCode) {
		log.info("checkPriceOffered");
		if(priceTypeCode != null){
			if(priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER) ||
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_TARIFF) ||
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER) || 
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION) || 
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER) ||
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD) ||
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF) || 
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)){

				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	
	public String cancel() {
		log.info("cancel");
		// clear request note data
		 super.renewSesionObject();
		return "catalogueSearch";
	}
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	public List<PmStatusDesc> getStatusDescList() {
		List<PmStatusDesc> statusDescs = getManagedBean().getStatusDescs();
		
		if (statusDescs == null || statusDescs.size() == 0) {
			IStatusSO statusSO = null;
			
			try {
				statusSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setModuleCode(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase());
				reqDTO.setDocType(ParameterConst.DOCTYPE_REQUEST_NOTE);
				reqDTO.setLanguageCode(languageCode);

				StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
				if(resDTO.isSuccessful()) {
					statusDescs = resDTO.getStatusDescList();
					
					getManagedBean().setStatusDescs(statusDescs);
				}
			} finally {
				statusSO = null;
			}	
		}
		return statusDescs;
	}
	
	private class SupplierPK {
		private SmSupplier smSupplier;
		
		public SupplierPK(SmSupplier smSupplier) {
			this.smSupplier = smSupplier;
		}
		
		/**
		 * @return the smSupplier
		 */
		public SmSupplier getSmSupplier() {
			return smSupplier;
		}

		/**
		 * @param smSupplier the smSupplier to set
		 */
		public void setSmSupplier(SmSupplier smSupplier) {
			this.smSupplier = smSupplier;
		}

		/**
		 * @param other the another SmSupplier instance
		 */
		public boolean equals(Object other) {
			if (other == null) {
				return false;
			}
			if (this == other) {
				return true;
			}
			if (!(other instanceof SupplierPK)) {
				return false;
			}
			
			final SupplierPK otherObject = (SupplierPK)other;

			return this.getSmSupplier().getSupplierId().equals(otherObject.getSmSupplier().getSupplierId());
		}

		/**
		 * @return the hashcode
		 */
		public int hashCode() {
			int result = 3;
			
			if (this.getSmSupplier() != null && this.getSmSupplier().getSupplierId() != null) {
				result = 31 * result + this.getSmSupplier().getSupplierId().hashCode();
			}
			
			return result;
		}
	}
}