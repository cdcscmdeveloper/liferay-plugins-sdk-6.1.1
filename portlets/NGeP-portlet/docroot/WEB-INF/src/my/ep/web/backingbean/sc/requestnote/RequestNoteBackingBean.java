/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.requestnote;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.bean.application.NgepApplicationBean; // #12177 Refactor
import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.fl.itemsummary.IFlItemSummarySO;
import my.ep.biz.service.osb.sc.IDPOsbSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sc.audit.IAuditDPRequestNoteSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.biz.service.sc.requestitem.IRequestItemSO;
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.AuditDPRequestNoteConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.fl.itemsummary.FlItemSummaryReqDTO;
import my.ep.dto.osb.sc.DPOsbReqDTO;
import my.ep.dto.osb.sc.DPOsbResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sc.requestitem.RequestItemReqDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorReqDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.common.ScRequestItemAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScPIReportAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScRNWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRNZone;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmDisciplinaryActionRemarks;	 //#32643 & #32642														   
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.cm.codification.CodificationBackingBean;
import my.ep.web.backingbean.ct.circular.CircularBackingBean; //#30866
import my.ep.web.backingbean.sc.audit.AuditDPRequestNoteBackingBean;
import my.ep.web.backingbean.sc.audit.AuditDPRequestNoteBackingBeanTemplate;
import my.ep.web.backingbean.sc.purchaseindefault.PurchaseInDefaultBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;
import my.ep.web.bean.sc.directpurchase.MarketSummaryAndPIBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.DPConstants;
import my.ep.web.managedbean.sc.requestnote.RequestNoteManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.SourcingZoneHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.vo.BlacklistOwnerDirectorVO;
import my.ep.web.vo.DisciplinaryActionCategoryVO;
import my.ep.web.vo.DisciplinaryActionVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;

import com.liferay.portal.util.PortalUtil;

/**
 * The Class RequestNoteBackingBean.
 *
 * @author sri
 * @since 11.02.2014
 */
@ManagedBean(name = "requestNoteBackingBean")
@RequestScoped
public class RequestNoteBackingBean extends SourcingBaseBackingBean<RequestNoteManagedBean> {
	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = Logger.getLogger(RequestNoteBackingBean.class);
	
	/** The flag. */
	private boolean flag;
	private boolean sqExpiredMessageFlag = false;
	private boolean pidExpiredFlag = false;
	
	/** The Constant ZONE_SEPARATOR. */
	public static final String ZONE_SEPARATOR = "_";
	
	/** The request note page. */
	private String REQUEST_NOTE_PAGE = "requestNoteMain";
	
	/** The request note page. */
	private String PURCHASE_REQUEST_PAGE = "purchaseRequestMain";
	
	/** The request note codification page. */
	private String REQUEST_NOTE_CODIFICARION_PAGE = "/xhtml/sc/directpurchase/requestnote/codification/requestNoteCodificationMain.xhtml";
	
	/**
	 * Instantiates a new request note backing bean.
	 */
	public RequestNoteBackingBean() {
		super();
		if (MDC.get(LogKey.USER_ID) == null) {
			PmUser user = new FacesScopes().getUserSessionBean();
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
	 * Search action.
	 * 
	 *  to search request note and for the page nation.
	 */
	public void searchAction(){
		debugLogger("searchAction");
		getManagedBean().getReqNoteDataModel().reloadData();
	}
	
	/**
	 * Reset action.
	 * 
	 * clear the key in data in request note search.
	 */
	public void resetAction() {
		getManagedBean().setSearchReqNoteNo(null);
		getManagedBean().setSearchSrcMethod(null);
		getManagedBean().setSearchCreatedDateFrom(null);
		getManagedBean().setSearchCreatedDateTo(null);
		getManagedBean().setSearchStatus(null);
		getManagedBean().getReqNoteDataModel().resetData();
	}
	
	
	/**
	 * view the request note.
	 *
	 * @param requestNoteId the request note id
	 * @return the string
	 */
	public void viewRequestNote(long requestNoteId) {
		debugLogger("viewRequestNote");
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		requestNoteReqDTO.setRequestNoteId(requestNoteId);
		RequestNoteResDTO requestNoteResDTO = getRequestNoteSO().getRequestNote(requestNoteReqDTO);
		if(requestNoteResDTO.getRequestNoteDetail() != null){
			ScRequestNote requestNote = requestNoteResDTO.getRequestNoteDetail();
			retrieveRequestNoteDetails(requestNote, null, Boolean.FALSE);	
			if(requestNote.getCurrentStatus() != null) {
				getCurrentStatus(requestNote.getCurrentStatus().getPmStatus());	
			}	
			
			//12177,12183 - Start
			if(requestNote != null){
				getDocStatus(requestNote);
			}
			//12177,12183 - End
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
		getManagedBean().setViewOnly(Boolean.TRUE);
		// Start - #30866
		CircularBackingBean circularBB = new CircularBackingBean();
		if (circularBB.getManagedBean().isContinuityCircularAuditSearch()) {
			getManagedBean().setTrackingDairyView(Boolean.TRUE);
		}
		// End - #30866
		
		// page navigation 
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_PAGE);
	}
	
	/**
	 * view the request note.
	 *
	 * @param requestNoteId the request note id
	 * @return the string
	 */
	public void viewRequestNote() {
		debugLogger("viewRequestNote");
		
		
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
		getManagedBean().setViewOnly(Boolean.TRUE);
		
		// page navigation 
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PURCHASE_REQUEST_PAGE);
	}

	/**
	 * save request note details
	 * save - save data into database and initiate task for request note.
	 * @param outcome the outcome
	 * @return the string
	 */
	public String saveAction(String outcome) {
		debugLogger("saveAction");
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		String [] outcomes = getManagedBean().getOutComes();
		String taskId  = getManagedBean().getTaskId();
		Object payLoad  = getManagedBean().getPayLoad();
		RequestNoteResDTO requestNoteResDTO = null;
		Boolean isAssignedState = getManagedBean().isAssignedState();
    try {
    	
    	//26284 - Start
    	if(outcome != null && BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)){
    		if(requestNote != null){
        		checkSupplierDisciplinaryAction(requestNote);
        	}
    	}
    	//26284 - End
    	
    	parseRequestNote(requestNote, outcome);
    	
	    setUserDetails(requestNote);
	    requestNoteReqDTO.setRequestNote(requestNote);
	    FacesScopes face = new FacesScopes();
	    requestNoteReqDTO.setPmUser(face.getUserSessionBean());
	    requestNoteReqDTO.setRoleCode(getUserRoleCode());
	    
	    // update workflow and remarks
	 	updateWorkFlowAndRemarks(requestNote);
	 	
		// finalize remarks
    	finalizeRemarks(requestNote.getRemarks());
	    
	    // reRecommend
	    requestNoteReqDTO.setEndLoaOutcome(getManagedBean().getEndLoaOutcome());
	    requestNoteReqDTO.setEndLoaTaskId(getManagedBean().getEndLoaTaskId());
	    requestNoteReqDTO.setEndLoaToken(getManagedBean().getEndLoaToken());
	    requestNoteReqDTO.setEndLoaRN(getManagedBean().getEndLoaRN());
	    
	    requestNoteReqDTO.setTaskExpiredDate(getManagedBean().getTaskExpiryDate());
	    requestNoteReqDTO.setPidApprovers(getManagedBean().getPidApprovers());
	    requestNoteReqDTO.setPidNo(getManagedBean().getPidNo());
	    requestNoteReqDTO.setPidId(getManagedBean().getPurchaseInDefaultId());
	    requestNoteReqDTO.setBackupApproverList(getManagedBean().getBackupApprovers());
	    requestNoteReqDTO.setDeletedRequestNoteDtls(getManagedBean().getDeleteRequestNoteDtls());

	    Long prInqId = getManagedBean().getPrInquiryId();
	    Long pidId = getManagedBean().getPurchaseInDefaultId();
	    Long statusId = getManagedBean().getCurrentStatusId();
	    Calendar pidExpiryDate = getManagedBean().getTaskExpiryDate();
	    String pidApprovers = getManagedBean().getPidApprovers();
	    String pidNo = getManagedBean().getPidNo();
	    if(getManagedBean().getPrInquiryId() != null){
			requestNoteReqDTO.setPurchaseInquiryId(prInqId);
		}
	    Object token = face.getBPMAuthenticationCtxToken();
	    
	    //#31679 - Start	  		 	  	   	
	  	   	if(requestNoteReqDTO.getRequestNote() != null){
	  	   		
	  	   		log.info("--- Bug 31679 (" + requestNoteReqDTO.getRequestNote().getRequestNoteNo() + ")" + "- Method: saveAction Start ---");
	  	   		
		  	  	if(getManagedBean().getCurrentStatusId() != null && outcome != null  ){
		  	   		log.info("Outcome(" + outcome + ") " + "StatusId (" + getManagedBean().getCurrentStatusId() + ") "
		  	   				+ "RN No.(" + requestNoteReqDTO.getRequestNote().getRequestNoteNo()+ ")");
		  	   	}
		  	  	
	  	   		if(requestNoteReqDTO.getRequestNote().getQuote() != null){
		  	   		log.info("RN No.(" + requestNoteReqDTO.getRequestNote().getRequestNoteNo()+ ") " 
		  	   				+ "SQ No.(" + requestNoteReqDTO.getRequestNote().getQuote().getQuoteNo() + ") "
		  	   				+ "Zonal(" + requestNoteReqDTO.getRequestNote().getQuote().isZonal() + ")");
	  	   		}	  	   		 		
	  	   		
	  	   		if(requestNoteReqDTO.getRequestNote().getRequestNoteDtls() != null){
	  		    		for(ScRequestNoteDtl rnDtl : requestNoteReqDTO.getRequestNote().getRequestNoteDtls()){		    			
	  		    			if(rnDtl.getRequestItem() != null 
	  		    					&& rnDtl.getScRequestSupplierItem() != null){	  		    				
	  		    				log.info("RnDtlId (" + rnDtl.getRequestNoteDtlId() + ") " 
	  		    						+ "ReqItemId (" + rnDtl.getRequestItem().getRequestNoteItemId() + ") " 
	  		    						+ "ReqSupItemId (" + rnDtl.getScRequestSupplierItem().getRequestSupplierItemId() + ") "
	  		    						+ "RnNo (" + requestNoteReqDTO.getRequestNote().getRequestNoteNo() + ")"); 
	  		    			}
	  		    		}
	  		    }
	  	   	log.info("--- Bug 31679 (" + requestNoteReqDTO.getRequestNote().getRequestNoteNo() + ")" + "- Method: saveAction End ---");
	  	   	}  
	  	  //#31679 - End
	    
	    try {
	    	if(outcome.isEmpty()){
		    	requestNoteReqDTO.setSubmit(Boolean.FALSE);
				requestNoteReqDTO.setToken(token);
				requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
			    requestNoteResDTO = getRequestNoteSO().submitRequestNote(requestNoteReqDTO);
			    
		     } else if(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)){
		    	 requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
		    	 requestNoteReqDTO.setToken(token);
		    	 requestNoteReqDTO.setOutcome("");
		    	 requestNoteResDTO = getRequestNoteSO().updateRequestNote(requestNoteReqDTO);
		    	 renewSesionObject();
		    	 getManagedBean().setPrInquiryId(prInqId);
		    	 getManagedBean().setPurchaseInDefaultId(pidId);
		    	 getManagedBean().setCurrentStatusId(statusId);
		    	 getManagedBean().setTaskExpiryDate(pidExpiryDate);
		    	 getManagedBean().setPidApprovers(pidApprovers);
		    	 getManagedBean().setPidNo(pidNo);
		     }
	    } catch(EpException e) {
			requestNote.setRemarks(initializeRemarks(
					requestNote,
					null,
					requestNote.getRemarks(),
					ScRequestNoteRemark.class));
			throw new EpException(e.getMessage(), e.getParameters());
		}
	    
	    if (requestNoteResDTO.isSuccessful()) {
	    	ScRequestNote scRequestNote = requestNoteResDTO.getRequestNoteDetail();
	    	if(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)){
	    		requestNoteReqDTO.setRequestNoteId(scRequestNote.getRequestNoteId());
	    		requestNoteResDTO = getRequestNoteSO().getRequestNote(requestNoteReqDTO);
	    		scRequestNote = requestNoteResDTO.getRequestNoteDetail();
	    		getManagedBean().setOutComes(outcomes);
	    		getManagedBean().setTaskId(taskId);
	    		getManagedBean().setPayLoad(payLoad);
	    		getManagedBean().setAssignedState(isAssignedState);
	    		reloadRequestNote(scRequestNote);
	    		successMessage(MessageConst.INFO_700025,ParameterTypeConst.DOCUMENT_TYPES,
	    				ParameterConst.DOCTYPE_REQUEST_NOTE,scRequestNote.getRequestNoteNo(), Boolean.FALSE);
	    	} else{
	    		successMessage(MessageConst.INFO_700024,ParameterTypeConst.DOCUMENT_TYPES,
	    				ParameterConst.DOCTYPE_REQUEST_NOTE,scRequestNote.getRequestNoteNo(), Boolean.TRUE);
	    		// clear data
	    		super.renewSesionObject();
	    	}
	    }else {
	    	throw new EpException(MessageConst.ERROR_700020);
	    }
    }    
    finally {
    	//directPurchaseSO = null;
    }
    return null;
	}
	
	/**
	 * save request note details for codification
	 * save - save data into database
	 * @param outcome the outcome
	 * @return the string
	 */
	public String saveCodificationAction(String outcome) {
		debugLogger("saveCodificationAction");
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		List<ScRequestNoteDtl> requestNoteDtls = getManagedBean().getRequestNoteDtls();
		String [] outcomes = getManagedBean().getOutComes();
		String taskId  = getManagedBean().getTaskId();
		RequestNoteResDTO requestNoteResDTO = null;
		SmSupplier supplier = getManagedBean().getSmSupplier();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		Object payload = getManagedBean().getPayLoad();
    try {
	    FacesScopes face = new FacesScopes();
	    requestNoteReqDTO.setPmUser(face.getUserSessionBean());
	    requestNoteReqDTO.setRoleCode(getUserRoleCode());
	    requestNoteReqDTO.setSubmit(Boolean.FALSE);
	    
	    Object token = face.getBPMAuthenticationCtxToken();
	    
	    requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
	    requestNoteReqDTO.setToken(token);
	    requestNoteReqDTO.setOutcome("");
	    requestNoteReqDTO.setRequestNoteDtls(requestNoteDtls);
	    
	  //#31679 - Start	  		 	  	   	
  	   	if(requestNote != null){
  	   		
  	   		log.info("--- Bug 31679 (" + requestNote.getRequestNoteNo() + ")" + "- Method: saveCodificationAction Start ---");
  	   		
	  	   	if(getManagedBean().getSmSupplier() != null){
	  	   		log.info("Supplier ID (" + getManagedBean().getSmSupplier().getSupplierId() + ") "
	  	   				+ "Company Name (" + getManagedBean().getSmSupplier().getCompanyName() + ") "
	  	   				+ "RN No.(" + requestNote.getRequestNoteNo()+ ")");
	  	   	}
  	   		
	  	  	if(getManagedBean().getCurrentStatusId() != null && outcome != null  ){
	  	   		log.info("Outcome(" + outcome + ") " + "StatusId (" + getManagedBean().getCurrentStatusId() + ") "
	  	   				+ "RN No.(" + requestNote.getRequestNoteNo()+ ")");
	  	   	}
	  	  	
  	   		if(requestNote.getQuote() != null){
	  	   		log.info("RN No.(" + requestNote.getRequestNoteNo()+ ") " 
	  	   				+ "SQ No.(" + requestNote.getQuote().getQuoteNo() + ") "
	  	   				+ "Zonal(" + requestNote.getQuote().isZonal() + ")");
  	   		}	  	   		 		
  	   		
  	   		if(requestNoteDtls != null){
  		    		for(ScRequestNoteDtl rnDtl : requestNoteDtls){		    			
  		    			if(rnDtl.getRequestItem() != null 
  		    					&& rnDtl.getScRequestSupplierItem() != null){	  		    				
  		    				log.info("RnDtlId (" + rnDtl.getRequestNoteDtlId() + ") " 
  		    						+ "ReqItemId (" + rnDtl.getRequestItem().getRequestNoteItemId() + ") " 
  		    						+ "ReqSupItemId (" + rnDtl.getScRequestSupplierItem().getRequestSupplierItemId() + ") "
  		    						+ "RnNo (" + requestNote.getRequestNoteNo() + ")"); 
  		    			}
  		    		}
  		    }
  	   	log.info("--- Bug 31679 (" + requestNote.getRequestNoteNo() + ")" + "- Method: saveCodificationAction End ---");
  	   	}  
  	   	//#31679 - End
  	   	
	    requestNoteReqDTO.setPayLoad(payload);
	    requestNoteResDTO = getRequestNoteSO().submitRNDtlsAfterCodify(requestNoteReqDTO);
	    renewSesionObject();
	    
	    if (requestNoteResDTO.isSuccessful()) {
	    	if(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)){
	    		requestNoteReqDTO.setRequestNoteId(requestNote.getRequestNoteId());
	    		requestNoteReqDTO.setSupplierId(supplier.getSupplierId());
	    		requestNoteResDTO = getRequestNoteSO().getRequestNoteDtlForCodificationBySupplier(requestNoteReqDTO);
	    		requestNote = requestNoteResDTO.getRequestNoteDetail();
	    		getManagedBean().setOutComes(outcomes);
	    		getManagedBean().setTaskId(taskId);
	    		getManagedBean().setPayLoad(payload);
	    		reloadRequestNoteForCodification(requestNoteResDTO);
	    		successMessage(MessageConst.INFO_700025,ParameterTypeConst.DOCUMENT_TYPES,
	    				ParameterConst.DOCTYPE_REQUEST_NOTE,requestNote.getRequestNoteNo(), Boolean.FALSE);
	    	} else{
	    		successMessage(MessageConst.INFO_700024,ParameterTypeConst.DOCUMENT_TYPES,
	    				ParameterConst.DOCTYPE_REQUEST_NOTE,requestNote.getRequestNoteNo(), Boolean.TRUE);
	    		// clear data
	    		super.renewSesionObject();
	    	}
	    }else {
	    	throw new EpException(MessageConst.ERROR_700020);
	    }
    }    
    finally {
    	//directPurchaseSO = null;
    }
    return null;
	}
	
	/**
	 * Reload request note.
	 *
	 * @param requestNote the request note
	 */
	private void reloadRequestNote(ScRequestNote requestNote){
		debugLogger("reloadRequestNote");
		getManagedBean().setRequestNote(requestNote);
		boolean itemSearchBackBtn = Boolean.TRUE;	
		getManagedBean().setAutoUpdate(true);
		if(requestNote.getCurrentStatus() != null ){
			getCurrentStatus(requestNote.getCurrentStatus().getPmStatus());	
		} else {
			// For status 
			Long docStatusId = getManagedBean().getCurrentStatusId();
			if(docStatusId != null){
				Long statusId = null;
				if (DPStatusConstants.PRI_PENDING_RN_CREATION.equals(docStatusId) || 
						DPStatusConstants.PID_PENDING_RN_CREATION.equals(docStatusId) ||
						DPStatusConstants.SQ_PENDING_RN_CREATION.equals(docStatusId)){
					statusId = DPStatusConstants.RN_PENDING_SUBMISSION;
				} else {
					statusId = docStatusId;
				}
				getCurrentStatus(getPmStatusByStatusId(statusId));
			} 
		}
		
		getManagedBean().setTitleChecker(true);
		List<ScRequestNoteDtl>  requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
		for (ScRequestNoteDtl scRequestNoteDtl : requestNote.getRequestNoteDtls()) {
			ScRequestSupplierItem scRequestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();
			if(scRequestSupplierItem != null){
				scRequestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);
				VCmSupplierItem vCmSupplierItem = new VCmSupplierItem();
				vCmSupplierItem.setCompanyName(scRequestSupplierItem.getSmSupplier().getCompanyName());
				scRequestNoteDtl.setvCmSupplierItem(vCmSupplierItem);

				if(null == scRequestNoteDtl.getPriceType()){
					scRequestNoteDtl.setPriceType(new PmParameter());
				} else{
					scRequestNoteDtl.setPriceTypeId(scRequestNoteDtl.getPriceType().getParameterId());
				}

				if(null == scRequestNoteDtl.getAssignedUser()){
					scRequestNoteDtl.setAssignedUser(new PmUser());	
				}

				if(null == scRequestNoteDtl.getProcurementTypeCategory()){
					scRequestNoteDtl.setProcurementTypeCategory(new PmParameter());
				} else {
					scRequestNoteDtl.setProcTypeCategoryId(scRequestNoteDtl.getProcurementTypeCategory().getParameterId());
				}

				if(scRequestNoteDtl.getAssignedUser() == null){
					FacesScopes facesScopes = new FacesScopes();
					scRequestNoteDtl.setAssignedUser(facesScopes.getUserSessionBean());
				}

				// check purchase inquiry 
				if(scRequestNoteDtl.isShowPurchaseInquiry()){
					// back to search catalouge button hide and show
					itemSearchBackBtn = Boolean.TRUE;
					ScPrInquiry scPrInquiry = null;
					if (scRequestNoteDtl.getPiRequestSupplierItem() != null){
						scPrInquiry = scRequestNoteDtl.getPiRequestSupplierItem().getScPrInquiy();
					}
					if(scRequestSupplierItem.getScPrInquiy() != null){
						scPrInquiry = scRequestSupplierItem.getScPrInquiy();
					}
					if(scPrInquiry != null){
						getManagedBean().setPrInquiryId(scPrInquiry.getPrInquiryId());	
					}
				}

				// check Purchase In Default 
				if(scRequestNoteDtl.getRequestItem() != null && scRequestNoteDtl.getRequestItem().getPurchaseInDefault() != null){
					itemSearchBackBtn = Boolean.FALSE;
				}
			} else {
				if(null == scRequestNoteDtl.getAssignedUser()){
					scRequestNoteDtl.setAssignedUser(new PmUser());	
				}
				if(null == scRequestNoteDtl.getPriceType()){
					scRequestNoteDtl.setPriceType(new PmParameter());
				}
				if(null == scRequestNoteDtl.getProcurementTypeCategory()){
					scRequestNoteDtl.setProcurementTypeCategory(new PmParameter());
				} else {
					scRequestNoteDtl.setProcTypeCategoryId(scRequestNoteDtl.getProcurementTypeCategory().getParameterId());
				}
				ScRequestSupplierItem  requestSupplierItem = new ScRequestSupplierItem();
				SmSupplier  smSupplier = new SmSupplier();
				requestSupplierItem.setSmSupplier(smSupplier);
				scRequestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
			}
			
			// set condition field for saved request note
			// Not used after moved back to dpOsbSO
//			if (!scRequestNoteDtl.isReRecommend()) {
				StringBuilder sb = new StringBuilder();
				sb.append("requestNoteItem.requestNoteItemId^");
				sb.append(scRequestNoteDtl.getRequestItem()
						.getRequestNoteItemId());
				sb.append("|");
				sb.append("loaRejected^0");

				if (scRequestNoteDtl.getPiRequestSupplierItem() != null){
					ScPrInquiry scPrInquiry = scRequestNoteDtl.getPiRequestSupplierItem().getScPrInquiy();
					if(scPrInquiry != null && scPrInquiry.getIsStockAvailable().equals(ParameterConst.PI_NO_STOCK)) {
						sb.append("|");
						sb.append("requestSupplierItemId^");
						sb.append(scRequestNoteDtl.getPiRequestSupplierItem()
								.getRequestSupplierItemId());

						sb.append("^");
						sb.append(SearchOperator.OPERATOR_NOT_IN.value());
					}
				}
				scRequestNoteDtl.setConditionField(sb.toString());
//			} 
			
			if(scRequestNoteDtl.getScRequestSupplierItem() != null && scRequestNoteDtl.getPriceType() != null){
				supplierListener(scRequestNoteDtl.getScRequestSupplierItem().getSupplierId(), scRequestNoteDtl.getPriceType().getParameterId() ,
						scRequestNoteDtl,scRequestNoteDtl.getAssignedUser().getUserId(), scRequestNoteDtl.getProcurementTypeCategory().getParameterId());
			}
			
			//#28865 - start
			if (scRequestNoteDtl.getRequestItem().getQuantity() == null) {
				if (scRequestNoteDtl.getRequestItem().getPurchaseInDefault() != null) {
					if (scRequestNoteDtl.getRequestItem().getPurchaseInDefault().getSuppQuantity() != null) {
							RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
							scRequestNoteDtl.getRequestItem().setQuantity(
									scRequestNoteDtl.getRequestItem().getPurchaseInDefault().getQuantity());
							requestNoteReqDTO.setScRequestItem(scRequestNoteDtl.getRequestItem());
							getRequestNoteSO().updateRequestNoteItem(requestNoteReqDTO);
							getManagedBean().setQtyViewOnly(Boolean.TRUE);
					}
				}
			}
			if (scRequestNoteDtl.getRequestItem().getPurchaseInDefault() != null) {
				if (scRequestNoteDtl.getRequestItem().getPurchaseInDefault().getSuppQuantity() != null) {
					getManagedBean().setQtyViewOnly(Boolean.TRUE);
				}
			}
			//#28865 - end

			requestNoteDtls.add(scRequestNoteDtl);
			
			calculatePricePerItem(scRequestNoteDtl);
		}

		// item search back button 
		getManagedBean().setItemSearchBackBtn(itemSearchBackBtn);
		getManagedBean().setRequestNoteDtls(requestNoteDtls);
		getManagedBean().setRequestNoteDtl(requestNoteDtls.get(0));
		// created by 
		if(requestNote.getCreatedBy() != null) {
			getManagedBean().setCreatedBy(getUserById(requestNote.getCreatedBy()).getUserName());
		}
		// remarks
		requestNote.setRemarks(initializeRemarks(
				requestNote,
				requestNote.getCurrentStatus(),
				requestNote.getRemarks(),
				ScRequestNoteRemark.class));

		initializeApproverGroup();
	}
	
	/**
	 * Reload request note for codification
	 *
	 * @param RequestNoteResDTO requestNoteResDTO
	 */
	private void reloadRequestNoteForCodification(RequestNoteResDTO requestNoteResDTO){
		debugLogger("reloadRequestNoteForCodification");
		ScRequestNote	requestNote = null;
		List<ScRequestNoteDtl> requestNoteDtlList= null;
		
		if(requestNoteResDTO.getRequestNoteDtlList() != null){
			requestNoteDtlList = requestNoteResDTO.getRequestNoteDtlList();
		}
		if(requestNoteResDTO.getRequestNoteDetail() != null){
			requestNote = requestNoteResDTO.getRequestNoteDetail();
		}
		
		if(CollectionUtils.isNotEmpty(requestNoteDtlList)) {
			getManagedBean().setSmSupplier(requestNoteDtlList.get(0).getScRequestSupplierItem().getSmSupplier());
		}
		
		// created by 
		getManagedBean().setCreatedBy(getUserById(requestNote.getCreatedBy()).getUserName());

		getCurrentStatus(getPmStatusByStatusId(getManagedBean().getCurrentStatusId()));
		
		//CHECK VALIDITY PID
		IPurchaseInDefaultSO purchaseInDefaultSO  = null;
		try {
			// Based on ptj selection have to get pid for request note 
			purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
			// fetch pid item for request note item	
			List<ScRequestItem>	requestNoteItems = getManagedBean().getRequestNoteItems();
			List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
			if(requestNoteDtlList != null && requestNoteDtlList.size() >0){
				for (ScRequestNoteDtl scRequestNoteDtl : requestNoteDtlList) {
					//ScRequestSupplierItem requestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();
					ScRequestItem requestItem = scRequestNoteDtl.getRequestItem();
					
					
					if(requestItem.getPurchaseInDefault() != null){
						// selected ptj id for non contract item for pid
						purchaseInDefaultReqDTO.setOrgProfileId(requestNote.getPmOrgProfile().getOrgProfileId());
						purchaseInDefaultReqDTO.setPurchaseInDefaultId(requestItem.getPurchaseInDefault().getPurchaseInDefaultId());
						PurchaseInDefaultResDTO defaultResDTO = purchaseInDefaultSO.getPurchaseInDefaultByPidNo(purchaseInDefaultReqDTO);

						// pid to request note
						if(defaultResDTO.getPurchaseInDefaultDtl() != null){
							ScPurchaseInDefault  purchaseInDefault = defaultResDTO.getPurchaseInDefaultDtl();
							requestItem.setPurchaseInDefault(purchaseInDefault);
							pidExpiredFlag = pidValidityBoolean(purchaseInDefault, requestNote);
						} else {
							requestItem.setPurchaseInDefault(null);
						}
					}
					
				}
			}
			this.getManagedBean().setRequestNoteDtls(requestNoteDtls);
			getManagedBean().getRequestNote().setRequestNoteDtls(requestNoteDtls);
			getManagedBean().getRequestNote().setRequestNoteItems(requestNoteItems);
			
		} finally {
//			purchaseInDefaultSO = null;
//			// title checker
			getManagedBean().setRequestNote(requestNote);
			getManagedBean().setRequestNoteDtls(requestNoteDtlList);
			//getManagedBean().setTitleChecker(Boolean.FALSE);
			
			getManagedBean().setTitleChecker(Boolean.FALSE);
		}
	}
	
	
	/**
	 * submit request note details.
	 * submit - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */	
	public String requestNoteSubmit(String outcome) {
		debugLogger("requestNoteSubmit");
		RequestNoteResDTO requestNoteResDTO = null;
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		try {
			if(getManagedBean().getPrInquiryId() != null){
				requestNoteReqDTO.setPurchaseInquiryId(getManagedBean().getPrInquiryId());
			}
			FacesScopes facesScopes = new FacesScopes();
			requestNoteReqDTO.setPmUser(facesScopes.getUserSessionBean());
			requestNoteReqDTO.setRoleCode(getUserRoleCode());
			
			// reRecommend
			requestNoteReqDTO.setEndLoaOutcome(getManagedBean().getEndLoaOutcome());
			requestNoteReqDTO.setEndLoaTaskId(getManagedBean().getEndLoaTaskId());
			requestNoteReqDTO.setEndLoaToken(getManagedBean().getEndLoaToken());
			requestNoteReqDTO.setEndLoaRN(getManagedBean().getEndLoaRN());
			requestNoteReqDTO.setTaskExpiredDate(getManagedBean().getTaskExpiryDate());
			requestNoteReqDTO.setPidApprovers(getManagedBean().getPidApprovers());
			requestNoteReqDTO.setPidNo(getManagedBean().getPidNo());
			requestNoteReqDTO.setPidId(getManagedBean().getPurchaseInDefaultId());
			requestNoteReqDTO.setBackupApproverList(getManagedBean().getBackupApprovers());
			requestNoteReqDTO.setDeletedRequestNoteDtls(getManagedBean().getDeleteRequestNoteDtls());

			Object ctxToken = facesScopes.getBPMAuthenticationCtxToken();
			setUserDetails(requestNote);
			requestNoteReqDTO.setRequestNote(requestNote);
			requestNoteReqDTO.setUserLocale(new FacesScopes().getUserLocale());
			// 12177 - Start
			requestNoteReqDTO.setSqEndDate(getManagedBean().getSqEndDate());
			// 12177 - End
			
			//#31679 - Start	  		 	  	   	
	  	   	if(requestNoteReqDTO.getRequestNote() != null){	  	   		
	  	   		log.info("--- Bug 31679 (" + requestNoteReqDTO.getRequestNote().getRequestNoteNo() + ")" + "- Method: requestNoteSubmit Start ---");
	  	   		
		  	  	if(getManagedBean().getCurrentStatusId() != null && outcome != null  ){
		  	   		log.info("Outcome(" + outcome + ") " + "StatusId (" + getManagedBean().getCurrentStatusId() + ") "
		  	   				+ "RN No.(" + requestNoteReqDTO.getRequestNote().getRequestNoteNo()+ ")");
		  	   	}
		  	  	
	  	   		if(requestNoteReqDTO.getRequestNote().getQuote() != null){
		  	   		log.info("RN No.(" + requestNoteReqDTO.getRequestNote().getRequestNoteNo()+ ") " 
		  	   				+ "SQ No.(" + requestNoteReqDTO.getRequestNote().getQuote().getQuoteNo() + ") "
		  	   				+ "Zonal(" + requestNoteReqDTO.getRequestNote().getQuote().isZonal() + ")");
	  	   		}	  	   		 		
	  	   		
	  	   		if(requestNoteReqDTO.getRequestNote().getRequestNoteDtls() != null){
	  		    		for(ScRequestNoteDtl rnDtl : requestNoteReqDTO.getRequestNote().getRequestNoteDtls()){		    			
	  		    			if(rnDtl.getRequestItem() != null 
	  		    					&& rnDtl.getScRequestSupplierItem() != null){	  		    				
	  		    				log.info("RnDtlId (" + rnDtl.getRequestNoteDtlId() + ") " 
	  		    						+ "ReqItemId (" + rnDtl.getRequestItem().getRequestNoteItemId() + ") " 
	  		    						+ "ReqSupItemId (" + rnDtl.getScRequestSupplierItem().getRequestSupplierItemId() + ") "
	  		    						+ "RnNo (" + requestNoteReqDTO.getRequestNote().getRequestNoteNo() + ")"); 
	  		    			}
	  		    		}
	  		    }
	  	   	log.info("--- Bug 31679 (" + requestNoteReqDTO.getRequestNote().getRequestNoteNo() + ")" + "- Method: requestNoteSubmit End ---");
	  	   	}  
	  	   	//#31679 - End
			
			//#31689 - start	
	  		if(requestNoteReqDTO.getRequestNote() != null){	
	  	   		if(requestNoteReqDTO.getRequestNote().getRequestNoteDtls() != null){
	  		    		for(ScRequestNoteDtl rnDtl : requestNoteReqDTO.getRequestNote().getRequestNoteDtls()){
	  		    			if(rnDtl.getTotalPricePerItem().compareTo(BigDecimal.ZERO) == 0)
	  		    	  	   		calculatePricePerItem(rnDtl);		    			
	  		    		}
	  		    }
	  	   	}
	  		//#31689 - end

			try {
				if(outcome.isEmpty()) {	
					requestNoteReqDTO.setToken(ctxToken);
					requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
					requestNoteReqDTO.setSubmit(Boolean.TRUE);
					requestNoteResDTO = getRequestNoteSO().submitRequestNote(requestNoteReqDTO);
				} else {
					requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
					requestNoteReqDTO.setToken(ctxToken);
					requestNoteReqDTO.setOutcome(outcome);
					requestNoteResDTO = getRequestNoteSO().updateRequestNote(requestNoteReqDTO);
				}
			} catch(EpException e) {
				requestNote.setRemarks(initializeRemarks(
						requestNote,
						null,
						requestNote.getRemarks(),
						ScRequestNoteRemark.class));
				throw new EpException(e.getMessage(), e.getParameters());
			}

			if (requestNoteResDTO.isSuccessful()) {
				ScRequestNote scRequestNote = requestNoteResDTO.getRequestNoteDetail();
				if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700010,ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo(), Boolean.TRUE);

				} else if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome) || outcome.isEmpty()){
					successMessage(MessageConst.INFO_700005,ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo(), Boolean.TRUE);
				}	
				// clear data
				super.renewSesionObject();
			} else{
				throw new EpException(MessageConst.ERROR_700020);
			}
		}    
		finally {
			//directPurchaseSO = null;
		}
		return null;
	}
	
	/**
	 * submit request note details for codification task
	 * submit - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */	
	public String requestNoteCodificationSubmit(String outcome) {
		debugLogger("requestNoteCodificationSubmit");
		RequestNoteResDTO requestNoteResDTO = null;
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		List<ScRequestNoteDtl>	requestNoteDtls  = this.getManagedBean().getRequestNoteDtls();
		ScRequestNote requestNote = this.getManagedBean().getRequestNote();
		try {
			FacesScopes facesScopes = new FacesScopes();
			requestNoteReqDTO.setPmUser(facesScopes.getUserSessionBean());
			requestNoteReqDTO.setRoleCode(getUserRoleCode());
			
			Object ctxToken = facesScopes.getBPMAuthenticationCtxToken();
			requestNoteReqDTO.setRequestNoteDtls(requestNoteDtls);
	  	   	
			//#31679 - Start	  		 	  	   	
	  	   	if(requestNote != null){
	  	   		
	  	   		log.info("--- Bug 31679 (" + requestNote.getRequestNoteNo() + ")" + "- Method: saveCodificationAction Start ---");
	  	   		
		  	   	if(getManagedBean().getSmSupplier() != null){
		  	   		log.info("Supplier ID (" + getManagedBean().getSmSupplier().getSupplierId() + ") "
		  	   				+ "Company Name (" + getManagedBean().getSmSupplier().getCompanyName() + ") "
		  	   				+ "RN No.(" + requestNote.getRequestNoteNo()+ ")");
		  	   	}
	  	   		
		  	  	if(getManagedBean().getCurrentStatusId() != null && outcome != null  ){
		  	   		log.info("Outcome(" + outcome + ") " + "StatusId (" + getManagedBean().getCurrentStatusId() + ") "
		  	   				+ "RN No.(" + requestNote.getRequestNoteNo()+ ")");
		  	   	}
		  	  	
	  	   		if(requestNote.getQuote() != null){
		  	   		log.info("RN No.(" + requestNote.getRequestNoteNo()+ ") " 
		  	   				+ "SQ No.(" + requestNote.getQuote().getQuoteNo() + ") "
		  	   				+ "Zonal(" + requestNote.getQuote().isZonal() + ")");
	  	   		}	  	   		 		
	  	   		
	  	   		if(requestNoteDtls != null){
	  		    		for(ScRequestNoteDtl rnDtl : requestNoteDtls){		    			
	  		    			if(rnDtl.getRequestItem() != null 
	  		    					&& rnDtl.getScRequestSupplierItem() != null){	  		    				
	  		    				log.info("RnDtlId (" + rnDtl.getRequestNoteDtlId() + ") " 
	  		    						+ "ReqItemId (" + rnDtl.getRequestItem().getRequestNoteItemId() + ") " 
	  		    						+ "ReqSupItemId (" + rnDtl.getScRequestSupplierItem().getRequestSupplierItemId() + ") "
	  		    						+ "RnNo (" + requestNote.getRequestNoteNo() + ")"); 
	  		    			}
	  		    		}
	  		    }
	  	   	log.info("--- Bug 31679 (" + requestNote.getRequestNoteNo() + ")" + "- Method: saveCodificationAction End ---");
	  	   	}  
	  	   	//#31679 - End
			
			requestNoteReqDTO.setUserLocale(new FacesScopes().getUserLocale());
			requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
			requestNoteReqDTO.setToken(ctxToken);
			requestNoteReqDTO.setOutcome(outcome);
			requestNoteReqDTO.setPayLoad(getManagedBean().getPayLoad());
			requestNoteResDTO = getRequestNoteSO().submitRNDtlsAfterCodify(requestNoteReqDTO);

			if (requestNoteResDTO.isSuccessful()) {
				if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome) || outcome.isEmpty()){
					successMessage(MessageConst.INFO_700005,ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, requestNote.getRequestNoteNo(), Boolean.TRUE);
				}	
				// clear data
				super.renewSesionObject();
			} else{
				throw new EpException(MessageConst.ERROR_700020);
			}
		}    
		finally {
			//directPurchaseSO = null;
		}
		return null;
	}
	//20190417@asyrafhaziq_12095 - Start
	/**
	 * cancel or reject Action request note, it shows the prompt(dialogue),
	 * 
	 */
	public void cancelRejectAction(String outcome) {
		debugLogger("cancelRejectAction");

		if (outcome.equals(BpmOutcomeConst.BPM_CANCEL)) {
			RequestContext.getCurrentInstance().execute(
					"cancelRN.show()");
		} else if (outcome.equals(BpmOutcomeConst.BPM_REJECT)) {
			RequestContext.getCurrentInstance().execute(
					"rejectRN.show()");
		} else {
			submitAction(outcome);
		}
	}
   //20190417@asyrafhaziq_12095 - End
	/**
	 * submit Action. before submit / Approve request note, 
	 * it shows the prompt(dialogue) that If system checking the different of delivery period, 
	 * show alert only but allow to proceed with submission of Request note.
	 */
	public void submitAction(String outcome) {
		debugLogger("submitAction");
		getManagedBean().setAction(outcome);
		boolean isDeliveryTermPrompt = Boolean.FALSE;		
		List<ScRequestNoteDtl>	requestNoteDtls  = this.getManagedBean().getRequestNoteDtls();
		ScRequestNote scRequestNote = getManagedBean().getRequestNote();
		
		//26284 - Start
		if(outcome.equals(BpmOutcomeConst.BPM_SUBMIT) ||
			outcome.equals(BpmOutcomeConst.BPM_APPROVE)){  	
			if(scRequestNote != null){
				checkSupplierDisciplinaryAction(scRequestNote);
			}	    	
		}
		//26284 - End
		
		parseRequestNote(scRequestNote, outcome);
		
		scRequestNote.setWorkFlowStatuses(null);
		// To avoid insert workflow status when direct submit without save.
		if (scRequestNote.isBrandNew()) {
			List<ScRequestNoteRemark> remarks = scRequestNote.getRemarks();
			if (CollectionUtils.isNotEmpty(remarks)) {
				for (ScRequestNoteRemark remark : remarks) {
					remark.setScWorkFlowStatus(null);
				}
			}	
		}
		
		if(outcome.equals(BpmOutcomeConst.BPM_SUBMIT) || 
				outcome.isEmpty() || 
				outcome.equals(BpmOutcomeConst.BPM_APPROVE)){

			//#31689 - start
			validateReqNoteIgnoreProcurement(scRequestNote);
			//#31689 - end
			// validate RN
			validateRequestNote(scRequestNote);

			// remarks
			if(!(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome) 
					|| BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome) 
					|| BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome))){
				finalizeRemarks(scRequestNote.getRemarks());
			}

			if(!scRequestNote.getSourceMethod().equals(ParameterConst.REQUEST_NOTE_SQ) && 
					requestNoteDtls != null && requestNoteDtls.size() > 1){
				RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
				scRequestNote.setRequestNoteDtls(requestNoteDtls);
				requestNoteReqDTO.setRequestNote(scRequestNote);
				requestNoteReqDTO.setUserLocale(new FacesScopes().getUserLocale());
				isDeliveryTermPrompt  = getRequestNoteSO().validateDeliveryTerm(requestNoteReqDTO);

				if(isDeliveryTermPrompt){
					RequestContext.getCurrentInstance().execute("deliveryTermConform.show()");
				}
			}
		}
		
		// #27545 - start
		if (outcome.equals(BpmOutcomeConst.BPM_SUBMIT) || outcome.equals(BpmOutcomeConst.BPM_APPROVE)
				|| outcome.equals(BpmOutcomeConst.BPM_REVISE)) {
			validateAssignedUser(requestNoteDtls);
		}
		// #27545 - end
		
		debugLogger("RequestNoteBackingBean >> submitAction ---- isExceededAmountAlerted() = "+ getManagedBean().isExceededAmountAlerted());
		if(getManagedBean().isExceededAmountAlerted()){
			if(!getManagedBean().isExceededMessagePrompt()){
				getManagedBean().setExceededMessagePrompt(Boolean.TRUE);
			}
			else{
				if(!isDeliveryTermPrompt){
					if(outcome.equals(BpmOutcomeConst.BPM_APPROVE) || 
							outcome.equals(BpmOutcomeConst.BPM_REVISE) || 
							outcome.equals(BpmOutcomeConst.BPM_REJECT)){
						approverAction(outcome);
					} else{
						requestNoteSubmit(outcome);
					}
				}
			}
		}else{
			if(!isDeliveryTermPrompt){
				if(outcome.equals(BpmOutcomeConst.BPM_APPROVE) || 
						outcome.equals(BpmOutcomeConst.BPM_REVISE) || 
						outcome.equals(BpmOutcomeConst.BPM_REJECT)){
					approverAction(outcome);
				} else{
					requestNoteSubmit(outcome);
				}
			}
		}
	}
	
	// #27545 - start
	public void validateAssignedUser(List<ScRequestNoteDtl>	requestNoteDtls) {
		//log.info("--validateAssigneduser--");
		Boolean throwError = false;
		PmOrgProfile pmOrgProfile = getManagedBean().getRequestNote().getPmOrgProfile();
		
		if(CollectionUtils.isNotEmpty(requestNoteDtls)) {
			
			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			orgProfileReqDTO.setGroupOrgType(ParameterConst.ORG_AUTH_TYPE_ORG_GROUP);
			OrgProfileResDTO orgProfileResDTO = getOrgProfileSO().retrieveProfileGroupByGroupOrgType(orgProfileReqDTO);
			
			List<Long> orgProfileIdList = new ArrayList<Long>();
			orgProfileIdList.add(pmOrgProfile.getOrgProfileId());
					
			if (CollectionUtils.isNotEmpty(orgProfileResDTO.getProfileGroups())) {
				for(PmProfileGroup group: orgProfileResDTO.getProfileGroups()){
					orgProfileIdList.add(group.getMainOrgProfile().getOrgProfileId());
				}
			}
			
			for (ScRequestNoteDtl requestNoteDtl : requestNoteDtls) {
				PmUser pmUser = requestNoteDtl.getAssignedUser();				
				String fulfilmentType = requestNoteDtl.getFulfilmentType().getParameterCode();
				List<PmUser> userList = null;
	
				UserReqDTO req = new UserReqDTO();
				req.setIncludeUserIds(new ArrayList<Long>());
				req.getIncludeUserIds().add(pmUser.getUserId());
	
				if (fulfilmentType.equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
					req.setRoleCodes(Arrays.asList(RoleCode.REQUISITIONER.value()));				
				} else {
					req.setRoleCodes(Arrays.asList(RoleCode.DESK_OFFICER.value()));
				}
				
				if(orgProfileIdList != null && orgProfileIdList.size()> 1) {
					req.setOrgProfileIds(orgProfileIdList);
				} else {
					req.setOrgProfileId(pmOrgProfile.getOrgProfileId());
				}
	
				UserResDTO res = getUserSO().retrieveUserListByRole(req);
				userList = res.getUserList();
	
				if (CollectionUtils.isEmpty(userList)) {
					throwError = true;
					break;
				}
			}
			if (throwError){
				throw new EpException(MessageConst.ERROR_770242);
			}
		}
	}
	// #27545 - End
	
	/**
	 * submit codification task Action. 
	 */
	public void submitCodificationAction(String outcome) {
		debugLogger("submitCodificationAction");
		getManagedBean().setAction(outcome);
		List<ScRequestNoteDtl>	requestNoteDtls  = this.getManagedBean().getRequestNoteDtls();
		
		if(outcome.equals(BpmOutcomeConst.BPM_SUBMIT)) {
			// check is all items codified
			for(ScRequestNoteDtl rnDtl : requestNoteDtls) {
				ScRequestItem requestItem = rnDtl.getRequestItem();
				if(requestItem.getCmItem() == null) {
					throw new EpException(
							MessageConst.ERROR_760448,
							new String[] {requestItem.getItemName()});
				}
			}
		}
		requestNoteCodificationSubmit(outcome);
	}

	/**
	 * Parses the request note.
	 *
	 * @param requestNote the request note
	 * @param outcome the outcome
	 */
	private void parseRequestNote(ScRequestNote requestNote, String outcome) {
		List<ScRequestNoteDtl>	requestNoteDtls  = this.getManagedBean().getRequestNoteDtls();
		List<ScRequestNoteDtl>	deletedRequestNoteDtls  = this.getManagedBean().getDeleteRequestNoteDtls();
		List<ScRequestNoteDtl>	allRequestNoteDtls  = new ArrayList<ScRequestNoteDtl>();
		
		allRequestNoteDtls.addAll(requestNoteDtls);
		allRequestNoteDtls.addAll(deletedRequestNoteDtls);
		// Target Approver
		formApprover(requestNote);

		// check empty Objects. and concatenation RN title
		// to parse all requestnoteDtl include the deleted one before save
		if (requestNoteDtls != null && allRequestNoteDtls.size() > 0) {
			List<String> results = new ArrayList<String>();
			StringBuilder sb  = new StringBuilder();
			String spacer = " ";
			int counter = 1;
			for (ScRequestNoteDtl requestNoteDtl : allRequestNoteDtls) {
				if (requestNoteDtl.getPriceType() != null && null == requestNoteDtl.getPriceType().getParameterId()) {
					requestNoteDtl.setPriceType(null);
				} else if(requestNoteDtl.getPriceType() != null){
					requestNoteDtl.setPriceTypeId(requestNoteDtl.getPriceType().getParameterId());
				}

				if(requestNoteDtl.getAssignedUser() != null && null == requestNoteDtl.getAssignedUser().getUserId()){
					requestNoteDtl.setAssignedUser(null);
				}
				
				if(requestNoteDtl.getProcurementTypeCategory() != null && null == requestNoteDtl.getProcurementTypeCategory().getParameterId()){
					requestNoteDtl.setProcurementTypeCategory(null);
				}
				
				// #36362 - start
				if (requestNoteDtl.getPriceType() != null && null != requestNoteDtl.getPriceType().getParameterId()) { 
					validateTierQuantityExceedMaxQuantity(requestNoteDtl);
				}
				// #36362 - end
				
				if(!requestNoteDtl.isDeleted() && !results.contains(requestNoteDtl.getProcurementTitle())){
					results.add(requestNoteDtl.getProcurementTitle());
					if(requestNoteDtls.size() != counter){
						results.add(spacer);	
					} 

					sb.append(requestNoteDtl.getProcurementTitle()).append(spacer).append("/").append(spacer);
					++counter;
				} 
				
				if(!BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
					// title validation
					if(!requestNoteDtl.isDeleted() && StringUtils.isBlank(requestNoteDtl.getProcurementTitle())){
						throw new EpException(
								MessageConst.ERROR_760315,
								new String[] {ResourceBundleUtils.getMessage("lbl.sc.direct.purchase.title",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}	
				}
			}	

			//procurement title limit  - 500 
			sb.deleteCharAt(sb.toString().lastIndexOf("/"));
			if(!sb.toString().isEmpty() && sb.toString().length() >= SourcingConstants.PROCUREMENT_TITLE_LIMIT){
				String procTitle = sb.substring(0, SourcingConstants.PROCUREMENT_TITLE_LIMIT);
				requestNote.setTitle(procTitle);
			} else{
				requestNote.setTitle(sb.toString());
			}
		}
		requestNote.setRequestNoteDtls(requestNoteDtls);
	}

	/**
	 * Validate request note.
	 *
	 * @param requestNote the request note
	 * @param nonDeletedDtls the non deleted dtls
	 */
	private void validateRequestNote(ScRequestNote requestNote){
		debugLogger("validateRequestNote");
		// Temp(#34274 - Start)
		boolean isShowMessage = getManagedBean().isExceededAmountAlerted();
		// Temp(#34274 - End)
		// show message for first submission, skip the message for second time submission
		if(requestNote.getRequestNoteDtls() != null && !requestNote.getRequestNoteDtls().isEmpty()){

			for (ScRequestNoteDtl requestNoteDtl : requestNote.getRequestNoteDtls()) {
				if(!requestNoteDtl.isDeleted()) {
					String pType = requestNoteDtl.getProcurementType()
							.getParameterCode();
					if ((!pType.equals(ParameterConst.PROC_TYPE_G2G) && 
							!pType.equals(ParameterConst.PROC_TYPE_EMERGENCY)) && 
							!pType.equals(ParameterConst.PROC_TYPE_PK7)) {
						// start - #31689
						// title validation
						if(StringUtils.isBlank(requestNoteDtl.getProcurementTitle())){
							throw new EpException(
									MessageConst.ERROR_760315,
									new String[] {ResourceBundleUtils.getMessage("lbl.sc.direct.purchase.title",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}

						// pid validity
						//log.info("pID validity begin");
						if(requestNoteDtl.getRequestItem() != null && requestNoteDtl.getRequestItem().getPurchaseInDefault() != null){
							pidValidity(requestNoteDtl.getRequestItem().getPurchaseInDefault(), requestNote);
						}
						//log.info("pID validity end");

						// validate price type
						if(null == requestNoteDtl.getPriceType() || null == requestNoteDtl.getPriceType().getParameterId()){
							throw new EpException(MessageConst.ERROR_760343, new String[]{requestNoteDtl.getRequestItem().getItemName()});
						}

						// validate recommended supplier
						if(null == requestNoteDtl.getScRequestSupplierItem() || null == requestNoteDtl.getScRequestSupplierItem().getSmSupplier() 
								|| null == requestNoteDtl.getScRequestSupplierItem().getSmSupplier().getSupplierId() ){
							throw new EpException(MessageConst.ERROR_760356, new String[]{requestNoteDtl.getRequestItem().getItemName()});
						}

						// quantity
						if( null != requestNoteDtl.getRequestItem() && null == requestNoteDtl.getRequestItem().getQuantity() ){
							throw new EpException(
									MessageConst.ERROR_760315,
									new String[] {ResourceBundleUtils.getMessage("lbl.sc.quantity",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						
						if (requestNoteDtl.getAssignedUser() == null || (requestNoteDtl.getAssignedUser() != null && requestNoteDtl.getAssignedUser().getUserId()==null)) {
							throw new EpException(MessageConst.ERROR_100022, new String[]{
								ResourceBundleUtils.getMessage(
										"lbl.sc.request.note.loa.prepare.by", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale())});
						}
						// end - #31689						
						// Check total procurement amount per ptj per item per year
						FlItemSummaryReqDTO flReqDTO = new FlItemSummaryReqDTO();
						if (requestNoteDtl.getRequestItem().getCmItem() != null) {
							//Long itemId = requestNoteDtl.getRequestItem().getCmItem().getItemId();
							String extensionCode = requestNoteDtl.getRequestItem().getCmItem().getExtensionCode();
							debugLogger("RequestNoteBackingBean >> validateRequestNote : RequestNoteDtl ID = "
									+ requestNoteDtl.getRequestNoteDtlId());
							debugLogger("RequestNoteBackingBean >> validateRequestNote : Extension Code : "
									+ extensionCode);
							
							Calendar cal = Calendar.getInstance();
							if(requestNoteDtl.getCreatedDate()!=null){
								cal.setTimeInMillis(requestNoteDtl.getCreatedDate().getTime());
							}
							else{
								cal.setTimeInMillis(System.currentTimeMillis());
							}
							
							flReqDTO.setOrgProfileId(requestNote.getPmOrgProfile().getOrgProfileId());
							flReqDTO.setExtensionCode(extensionCode);
							flReqDTO.setYear(cal.get(Calendar.YEAR));
							IFlItemSummarySO itemSummarySO = (IFlItemSummarySO) BusinessDelegate.getInstance()
									.getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFlItemSummarySO.class);
							// BigDecimal totalAmt = itemSummarySO
							// .retrieveAccumulatedAmount(flReqDTO);
							// changes to calculate per ptj per item per year
							// using 8 digit
							BigDecimal totalAmt = itemSummarySO.retrieveAccumulatedAmountByItemExtensionCode(flReqDTO);
							
							totalAmt=requestNoteDtl.getTotalPricePerItem().add(totalAmt);
							
							
							debugLogger("RequestNoteBackingBean >> validateRequestNote : totalAmt = " + totalAmt);
							IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
							RuleReqDTO ruleReqDTO = new RuleReqDTO();
							ruleReqDTO.setRuleType(RuleTypeConst.ITEM_THRESHOLD);
							List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList();

							if (ruleValueList != null && ruleValueList.size() > 0) {
								PmConfigRuleValue configRuleValue = ruleValueList.get(0);
								// Temp(#34274 - Start)
								// Rule : ITEM_THRESHOLD For 20k
								if (totalAmt.compareTo(new BigDecimal(configRuleValue.getRuleValue())) > 0) {
									if (!isShowMessage) {
										getManagedBean().setExceededAmountAlerted(Boolean.TRUE);

										FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, 
												MessageUtils.getMessage(MessageConst.ERROR_760036, 
														new String[] {requestNoteDtl.getRequestItem().getItemName(), requestNoteDtl.getRequestItem().getCmItem().getExtensionCode() },
														MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

										// throw new
										// EpException(MessageConst.ERROR_760036,
										// new String[]
										// {requestNoteDtl.getRequestItem().getItemName(),
										// requestNoteDtl.getRequestItem().getCmItem().getExtensionCode()});
									}
								}
								// Temp(#34274 - End)
								
								// 26059 - start
								// ScQuote quote = null;
								// String sourceMethod =
								// requestNote.getSourceMethod();
								// if(sourceMethod.equals(ParameterConst.REQUEST_NOTE_SQ))
								// {
								// quote = requestNote.getQuote();
								// }
								// //Rule : ITEM_THRESHOLD For 20k
								// if(totalAmt.compareTo(new
								// BigDecimal(configRuleValue.getRuleValue())) >
								// 0 && ((quote != null && !quote.isPanel())
								// ||
								// !sourceMethod.equals(ParameterConst.REQUEST_NOTE_SQ)))
								// {
								// String language = new
								// FacesScopes().getUserLocale().getLanguage();
								// String itemTitle;
								// if(CommonConstants.DEFAULT_USER_LANGUAGE_CODE.equalsIgnoreCase(language))
								// {
								// itemTitle =
								// requestNoteDtl.getRequestItem().getCmItem().getCmUnspsc().getUnspscTitleEng();
								// } else {
								// itemTitle =
								// requestNoteDtl.getRequestItem().getCmItem().getCmUnspsc().getUnspscTitle();
								// }
								// throw new
								// EpException(MessageConst.ERROR_760518,
								// new String[] {itemTitle,
								// requestNoteDtl.getRequestItem().getCmItem().getCmUnspsc().getUnspscCode()});
								// }
								// 26059 - end							
							} else {
								throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
							}
						}
					}

					//  #31689 (start - comment out all)
					// title validation
					/*if(StringUtils.isBlank(requestNoteDtl.getProcurementTitle())){
						throw new EpException(
								MessageConst.ERROR_760315,
								new String[] {ResourceBundleUtils.getMessage("lbl.sc.direct.purchase.title",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}

					// pid validity
					if(requestNoteDtl.getRequestItem() != null && requestNoteDtl.getRequestItem().getPurchaseInDefault() != null){
						pidValidity(requestNoteDtl.getRequestItem().getPurchaseInDefault(), requestNote);
					}

					// validate price type
					if(null == requestNoteDtl.getPriceType() || null == requestNoteDtl.getPriceType().getParameterId()){
						throw new EpException(MessageConst.ERROR_760343, new String[]{requestNoteDtl.getRequestItem().getItemName()});
					}

					// validate recommended supplier
					if(null == requestNoteDtl.getScRequestSupplierItem() || null == requestNoteDtl.getScRequestSupplierItem().getSmSupplier() 
							|| null == requestNoteDtl.getScRequestSupplierItem().getSmSupplier().getSupplierId() ){
						throw new EpException(MessageConst.ERROR_760356, new String[]{requestNoteDtl.getRequestItem().getItemName()});
					}

					// quantity
					if( null != requestNoteDtl.getRequestItem() && null == requestNoteDtl.getRequestItem().getQuantity() ){
						throw new EpException(
								MessageConst.ERROR_760315,
								new String[] {ResourceBundleUtils.getMessage("lbl.sc.quantity",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}

					//26221 - Start
					if (requestNoteDtl.getAssignedUser() == null || (requestNoteDtl.getAssignedUser() != null && requestNoteDtl.getAssignedUser().getUserId()==null)) {
						throw new EpException(MessageConst.ERROR_100022, new String[]{
							ResourceBundleUtils.getMessage(
									"lbl.sc.request.note.loa.prepare.by", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale())});
					//26221 - end
					}*/
					// #31689 (end - comment out all)
				}
			}
		}
		
		// approver group name validation
		if(null == requestNote.getPmUserGroup()){
			throw new EpException(
					MessageConst.ERROR_760315,
					new String[] {ResourceBundleUtils.getMessage("lbl.sc.ptj.approver.group",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		// approver name validation
		if(null == requestNote.getApprover()){
			throw new EpException(
					MessageConst.ERROR_760315,
					new String[] {ResourceBundleUtils.getMessage("lbl.sc.approverName",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
	}

	//#31689 - start
	private void validateReqNoteIgnoreProcurement(ScRequestNote requestNote) {
		debugLogger("validateReqNoteIgnoreProcurement");
		if (requestNote.getRequestNoteDtls() != null && !requestNote.getRequestNoteDtls().isEmpty()) {
			for (ScRequestNoteDtl requestNoteDtl : requestNote.getRequestNoteDtls()) {
				if (!requestNoteDtl.isDeleted()) {
					//String pType = requestNoteDtl.getProcurementType().getParameterCode();
					//#32362 - comment out this condition - start
					/*if ((!pType.equals(ParameterConst.PROC_TYPE_G2G) && !pType
							.equals(ParameterConst.PROC_TYPE_EMERGENCY)) && !pType.equals(ParameterConst.PROC_TYPE_PK7)) {*/
					//#32362 - comment out this condition - end
						// title validation
						if (StringUtils.isBlank(requestNoteDtl.getProcurementTitle())) {
							throw new EpException(
									MessageConst.ERROR_760315,
									new String[] { ResourceBundleUtils.getMessage("lbl.sc.direct.purchase.title",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
						}

						// pid validity
						//log.info("pID validity begin");
						if (requestNoteDtl.getRequestItem() != null
								&& requestNoteDtl.getRequestItem().getPurchaseInDefault() != null) {
							pidValidity(requestNoteDtl.getRequestItem().getPurchaseInDefault(), requestNote);
						}
						//log.info("pID validity end");

						// validate price type
						if (null == requestNoteDtl.getPriceType()
								|| null == requestNoteDtl.getPriceType().getParameterId()) {
							throw new EpException(MessageConst.ERROR_760343, new String[] { requestNoteDtl
									.getRequestItem().getItemName() });
						}

						// validate recommended supplier
						if (null == requestNoteDtl.getScRequestSupplierItem()
								|| null == requestNoteDtl.getScRequestSupplierItem().getSmSupplier()
								|| null == requestNoteDtl.getScRequestSupplierItem().getSmSupplier().getSupplierId()) {
							throw new EpException(MessageConst.ERROR_760356, new String[] { requestNoteDtl
									.getRequestItem().getItemName() });
						}

						// quantity
						if (null != requestNoteDtl.getRequestItem()
								&& null == requestNoteDtl.getRequestItem().getQuantity()) {
							throw new EpException(
									MessageConst.ERROR_760315,
									new String[] { ResourceBundleUtils.getMessage("lbl.sc.quantity",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
						}

						if (requestNoteDtl.getAssignedUser() == null
								|| (requestNoteDtl.getAssignedUser() != null && requestNoteDtl.getAssignedUser()
										.getUserId() == null)) {
							throw new EpException(
									MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage("lbl.sc.request.note.loa.prepare.by",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
						}
					//} #32362 - comment out this curly bracket
				}
			}
		}

		// approver group name validation
		if (null == requestNote.getPmUserGroup()) {
			throw new EpException(MessageConst.ERROR_760315, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.ptj.approver.group", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()) });
		}
		// approver name validation
		if (null == requestNote.getApprover()) {
			throw new EpException(MessageConst.ERROR_760315,
					new String[] { ResourceBundleUtils.getMessage("lbl.sc.approverName",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
		}
	}
	//#31689 - end
	
	/**
	 * Pre render page navigation to request note page based on task id
	 * @return void
	 */
	public void preRenderView() {
		debugLogger("start preRenderView");
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		log.info("Temp1: start performance - getBpmTaskDetails");
		SCDirectPurchaseDataType directPurchaseDataType = super.getBpmTaskDetails(SCDirectPurchaseDataType.class, null);
		log.info("Temp1: end performance - getBpmTaskDetails");
		Long dpId = null;
		boolean zoneFlag = Boolean.FALSE;
		ScRequestNote requestNote = null; 

		if(directPurchaseDataType.getRnId() == null){
			dpId = Long.valueOf(directPurchaseDataType.getDpId());
		} else{
			dpId = Long.valueOf(directPurchaseDataType.getRnId());
		}
		
		if(directPurchaseDataType.getPidExpiryDate() != null) {
			getManagedBean().setTaskExpiryDate(directPurchaseDataType.getPidExpiryDate());
		}
		
		getManagedBean().setPidApprovers(directPurchaseDataType.getPidApprover());
		getManagedBean().setPidNo(directPurchaseDataType.getPidDocumentNumber());
		getManagedBean().setPurchaseInDefaultId(directPurchaseDataType.getPidId());
		
		requestNoteReqDTO.setRequestNoteId(dpId);
		
		//28999,30063,30448 - Start
		if(getManagedBean().getCurrentStatusId() != null 
				&& getManagedBean().getCurrentStatusId().equals(DPStatusConstants.SQ_PENDING_RN_CREATION)){
			if(requestNoteReqDTO.getRequestNoteId() != null){
				getRequestNoteSO().checkingPriceTypeAndRequestSupplierItem(requestNoteReqDTO);
			}
		}		
		//28999,30063,30448 - End
		
		log.info("Temp2: start performance - getRequestNoteSO().getRequestNote");
		RequestNoteResDTO requestNoteResDTO = getRequestNoteSO().getRequestNote(requestNoteReqDTO);
		log.info("Temp2: end performance - getRequestNoteSO().getRequestNote");
		if(requestNoteResDTO.getRequestNoteDetail() != null){
			requestNote = requestNoteResDTO.getRequestNoteDetail();
		}

		if(requestNote != null){	
			
			if (requestNote.getScRNZone() != null) {
				getManagedBean().setScRNZone(requestNote.getScRNZone());	
			} else {
				getManagedBean().setScRNZone(new ScRNZone());	
			}
			log.info("Temp3: start performance - retrieveRequestNoteDetails");
			retrieveRequestNoteDetails(requestNote, getManagedBean().getCurrentStatusId(), zoneFlag);
			log.info("Temp3: end performance - retrieveRequestNoteDetails");
			log.info("Temp4: start performance - checkIsSupplierResponse");
			checkIsSupplierResponse(requestNote.getRequestNoteDtls(), getManagedBean().getCurrentStatusId());
			log.info("Temp4: end performance - checkIsSupplierResponse");

			//12177,12183 - Start
			getDocStatus(requestNote);
			//12177,12183 - End
		}
		
		//Redmine Enhancement #12177
		log.info("Temp5: start performance - addButton");
		addButton();
		log.info("Temp5: end performance - addButton");
		if(sqExpiredMessageFlag){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
				MessageUtils.getMessage(MessageConst.ERROR_760481, 
					null,MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		
		// title checker
		getManagedBean().setTitleChecker(Boolean.FALSE);
		FacesContext fc = FacesContext.getCurrentInstance();
		debugLogger("end preRenderView");
		// page nav. to common request note
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_PAGE);
	}
	
	/**
	 * Pre render page navigation to request note page based on task id for codification task
	 * @return void
	 */
	public void preRenderCodificationView() {
		debugLogger("preRenderCodificationView");
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		Long dpId = null;
		Long supplierId = null;
		
		SCAwardSupplierDataType awardSupplierDataType = super.getBpmTaskDetails(SCAwardSupplierDataType.class, null);
		
		if(awardSupplierDataType.getSourceId() != null){
			dpId = Long.valueOf(awardSupplierDataType.getSourceId());
		} 
		
		if(awardSupplierDataType.getSupplierId() != null){
			supplierId = Long.valueOf(awardSupplierDataType.getSupplierId());
		} 
		
		requestNoteReqDTO.setRequestNoteId(dpId);
		requestNoteReqDTO.setSupplierId(supplierId);
		RequestNoteResDTO requestNoteResDTO = getRequestNoteSO().
				getRequestNoteDtlForCodificationBySupplier(requestNoteReqDTO);
		reloadRequestNoteForCodification(requestNoteResDTO);
		
		if(!pidExpiredFlag){
			//getPurchaseInDefaultExpiredMessageCodification(getManagedBean().getRequestNoteStatus().getStatusId());
			
		}
		
		FacesContext fc = FacesContext.getCurrentInstance();
		// page nav. to request note codification page
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_CODIFICARION_PAGE);
	}
	

	/**
	 * Retrieve request note details.
	 *
	 * @param requestNote the request note
	 * @param docStatusId the doc status id
	 * @param zoneFlag the zone flag
	 */
	private void retrieveRequestNoteDetails(ScRequestNote requestNote, Long docStatusId, boolean zoneFlag) {
		//RequestNote Details
		boolean itemSearchBackBtn = Boolean.TRUE;
		getManagedBean().setRequestNote(requestNote);
		List<ScRequestNoteDtl>  requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
		if(requestNote.getRequestNoteDtls()  != null && requestNote.getRequestNoteDtls().size() >0){
			for (ScRequestNoteDtl scRequestNoteDtl : requestNote.getRequestNoteDtls()) {
				ScRequestSupplierItem scRequestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();
				if(scRequestSupplierItem != null){
					if(!scRequestSupplierItem.getSupplierItemPrices().isEmpty() && scRequestSupplierItem.getSupplierItemPrices() != null ){
						scRequestSupplierItem.setSupplierItemPrice(scRequestSupplierItem.getSupplierItemPrices().get(0));
						scRequestSupplierItem.setSupplierId(scRequestSupplierItem.getSmSupplier().getSupplierId());
						scRequestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);
						VCmSupplierItem vCmSupplierItem = new VCmSupplierItem();
						vCmSupplierItem.setCompanyName(scRequestSupplierItem.getSmSupplier().getCompanyName());
						scRequestNoteDtl.setvCmSupplierItem(vCmSupplierItem);
					}

					// check purchase inquiry 
					if(scRequestNoteDtl.isShowPurchaseInquiry()){
						// back to search catalouge button hide and show
						itemSearchBackBtn = Boolean.TRUE;
						ScPrInquiry scPrInquiry = null;
						if (scRequestNoteDtl.getPiRequestSupplierItem() != null){
							scPrInquiry = scRequestNoteDtl.getPiRequestSupplierItem().getScPrInquiy();
						}
						if(scRequestSupplierItem.getScPrInquiy() != null){
							scPrInquiry = scRequestSupplierItem.getScPrInquiy();
						}
						if(scPrInquiry != null){
							getManagedBean().setPrInquiryId(scPrInquiry.getPrInquiryId());	
						}
					}
				}
				
				// Purchase In Default -> Request Note
				if(scRequestNoteDtl.getRequestItem() != null && scRequestNoteDtl.getRequestItem().getPurchaseInDefault() != null){
					itemSearchBackBtn = Boolean.FALSE;
					getPurchaseInDefaultExpiredMessage(docStatusId);
				} else if(scRequestNoteDtl.getRequestItem() != null && scRequestNoteDtl.getRequestItem().getQuote() != null){
					// Purchase In Default ->  Simple Quote  -> Request Note
					getPurchaseInDefaultExpiredMessage(docStatusId);
				}
				
				if(scRequestNoteDtl.getFulfilmentType() == null){
					scRequestNoteDtl.setFulfilmentType(getParameterByTypeAndCode(
							ParameterTypeConst.FULFILMENT_TYPE, ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN));
				}

				if(null == scRequestNoteDtl.getPriceType()){
					scRequestNoteDtl.setPriceType(new PmParameter());
				} else{
					scRequestNoteDtl.setPriceTypeId(scRequestNoteDtl.getPriceType().getParameterId());
				}

				//#26221
				if(scRequestNoteDtl.getAssignedUser() == null){
					if((requestNote.getCreatedBy() != null) && (scRequestNoteDtl.getFulfilmentType().getParameterCode()
							.equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF))) {
						scRequestNoteDtl.setAssignedUser(getUserById(requestNote.getCreatedBy()));
					} else {
						scRequestNoteDtl.setAssignedUser(new PmUser());
					}
				}
				
				//#28865 - start
				if (scRequestNoteDtl.getRequestItem().getQuantity() == null) {
					if (scRequestNoteDtl.getRequestItem().getPurchaseInDefault() != null) {
						if (scRequestNoteDtl.getRequestItem().getPurchaseInDefault().getSuppQuantity() != null) {
								RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
								scRequestNoteDtl.getRequestItem().setQuantity(
										scRequestNoteDtl.getRequestItem().getPurchaseInDefault().getQuantity());
								requestNoteReqDTO.setScRequestItem(scRequestNoteDtl.getRequestItem());
								getRequestNoteSO().updateRequestNoteItem(requestNoteReqDTO);
								getManagedBean().setQtyViewOnly(Boolean.TRUE);
						}
					}
				}
				if (scRequestNoteDtl.getRequestItem().getPurchaseInDefault() != null) {
					if (scRequestNoteDtl.getRequestItem().getPurchaseInDefault().getSuppQuantity() != null) {
						getManagedBean().setQtyViewOnly(Boolean.TRUE);
					}
				}
				//#28865 - end

				if(null == scRequestNoteDtl.getProcurementTypeCategory()){
					scRequestNoteDtl.setProcurementTypeCategory(new PmParameter());
				} else {
					scRequestNoteDtl.setProcTypeCategoryId(scRequestNoteDtl.getProcurementTypeCategory().getParameterId());
				}

				if(scRequestNoteDtl.getJustificationType() != null){
					getManagedBean().setJustificationType(scRequestNoteDtl.getJustificationType().getReasonCode());
				}
				if(scRequestNoteDtl.getProcurementType() != null){
					getManagedBean().setProcurementType(scRequestNoteDtl.getProcurementType().getParameterCode());
				}

				if(scRequestNoteDtl.getJustification() != null){
					getManagedBean().setJustification(scRequestNoteDtl.getJustification());
				}

				if(null == scRequestNoteDtl.getDeliverTermType()){
					scRequestNoteDtl.setDeliverTermType(getDeliveryTermType());
				}

				if(null == scRequestNoteDtl.getScRequestSupplierItem()){
					ScRequestSupplierItem  requestSupplierItem = new ScRequestSupplierItem();
					SmSupplier  smSupplier = new SmSupplier();
					requestSupplierItem.setSmSupplier(smSupplier);
					scRequestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
				}

				// Weather item product or server
				scRequestNoteDtl.setProdAndServiceFlag(checkServiceOrProduct(scRequestNoteDtl));

				// set condition field for saved request note
//				if(!scRequestNoteDtl.isReRecommend()) {
					StringBuilder sb = new StringBuilder();
					sb.append("requestNoteItem.requestNoteItemId^");
					sb.append(scRequestNoteDtl.getRequestItem().getRequestNoteItemId());
					sb.append("|");
					sb.append("loaRejected^0");

					if (scRequestNoteDtl.getPiRequestSupplierItem() != null){
						ScPrInquiry scPrInquiry = scRequestNoteDtl.getPiRequestSupplierItem().getScPrInquiy();
						if(scPrInquiry != null && scPrInquiry.getIsStockAvailable().equals(ParameterConst.PI_NO_STOCK)) {
							sb.append("|");
							sb.append("requestSupplierItemId^");
							sb.append(scRequestNoteDtl.getPiRequestSupplierItem()
									.getRequestSupplierItemId());

							sb.append("^");
							sb.append(SearchOperator.OPERATOR_NOT_IN.value());
						}
					}

					scRequestNoteDtl.setConditionField(sb.toString());
//				}

				supplierListener(scRequestNoteDtl.getScRequestSupplierItem().getSupplierId(), scRequestNoteDtl.getPriceType().getParameterId() ,
						scRequestNoteDtl,scRequestNoteDtl.getAssignedUser().getUserId(), scRequestNoteDtl.getProcurementTypeCategory().getParameterId());
				
				calculatePricePerItem(scRequestNoteDtl);
				
				requestNoteDtls.add(scRequestNoteDtl);
			}
			getManagedBean().setRequestNoteDtl(requestNote.getRequestNoteDtls().get(0));
		}

		getManagedBean().setRequestNoteDtls(requestNoteDtls);

		// created by 
		getManagedBean().setCreatedBy(getUserById(requestNote.getCreatedBy()).getUserName());


		if(requestNote.getSourceMethod().equals(ParameterConst.REQUEST_NOTE_SC)){
			getManagedBean().setFulfillmentType(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
		}

		getManagedBean().setItemSearchBackBtn(itemSearchBackBtn);

		// For status 
		if(docStatusId != null){
			try {
				Long statusId = null;
				if (DPStatusConstants.PRI_PENDING_RN_CREATION.equals(docStatusId) || 
						DPStatusConstants.PID_PENDING_RN_CREATION.equals(docStatusId) ||
						DPStatusConstants.SQ_PENDING_RN_CREATION.equals(docStatusId)){
					statusId = DPStatusConstants.RN_PENDING_SUBMISSION;
				} else {
					statusId = docStatusId;
				}
				getCurrentStatus(getPmStatusByStatusId(statusId));

			} finally {
				//statusSO = null;
			}
		}

		// remarks
		requestNote.setRemarks(initializeRemarks(
				requestNote,
				requestNote.getCurrentStatus(),
				requestNote.getRemarks(),
				ScRequestNoteRemark.class));

		initializeApproverGroup();
	}

	/**
	 * Update bpm task.
	 *
	 * @param outcome the outcome
	 * @return the request note res dto
	 */
	private RequestNoteResDTO updateBPMTask(String outcome) {
		debugLogger("updateBPMTask");
		RequestNoteResDTO requestNoteResDTO = null;
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		try {
			parseRequestNote(requestNote, outcome);
			requestNoteReqDTO.setBackupApproverList(getManagedBean().getBackupApprovers());

			if(getManagedBean().getPrInquiryId() != null){
				requestNoteReqDTO.setPurchaseInquiryId(getManagedBean().getPrInquiryId());
			}
			
			FacesScopes facesScopes = new FacesScopes();
			requestNoteReqDTO.setPmUser(facesScopes.getUserSessionBean());
			requestNoteReqDTO.setRoleCode(getUserRoleCode());
			setUserDetails(requestNote);
			requestNoteReqDTO.setRequestNote(requestNote);
			requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
			requestNoteReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			requestNoteReqDTO.setOutcome(outcome);
			requestNoteReqDTO.setPayLoad(getManagedBean().getPayLoad());
			requestNoteReqDTO.setTaskExpiredDate(getManagedBean().getTaskExpiryDate());
			requestNoteReqDTO.setPidApprovers(getManagedBean().getPidApprovers());
			requestNoteReqDTO.setPidNo(getManagedBean().getPidNo());
			requestNoteReqDTO.setPidId(getManagedBean().getPurchaseInDefaultId());
			requestNoteReqDTO.setDeletedRequestNoteDtls(getManagedBean().getDeleteRequestNoteDtls());
			requestNoteReqDTO.setUserLocale(new FacesScopes().getUserLocale());
			// 12177 - Start
			requestNoteReqDTO.setSqEndDate(getManagedBean().getSqEndDate());
			// 12177 - End
			//12183 - Start
			requestNoteReqDTO.setPidValidityExpired(getManagedBean().isPidValidityExpired());
			//12183 - End
			
			try {
				requestNoteResDTO = getRequestNoteSO().updateRequestNote(requestNoteReqDTO);
			} catch(EpException e) {
				requestNote.setRemarks(initializeRemarks(
						requestNote,
						null,
						requestNote.getRemarks(),
						ScRequestNoteRemark.class));
				throw new EpException(e.getMessage(), e.getParameters());
			}
			
			if (requestNoteResDTO.isSuccessful()) {
				ScRequestNote scRequestNote = requestNoteResDTO.getRequestNoteDetail();
				if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)){
					
					// #33388 - Start
					if (scRequestNote != null) {
						requestNoteReqDTO.setRequestNoteId(scRequestNote.getRequestNoteId());
						getRequestNoteSO().checkingRequestSupplierItemId(requestNoteReqDTO);
					}
					// #33388 - End
					
					successMessage(MessageConst.INFO_700007, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE,scRequestNote.getRequestNoteNo(), Boolean.TRUE);

				} else if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700026, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo(), Boolean.TRUE);

				} else if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700009, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo(), Boolean.TRUE);

				} else if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700011, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo(), Boolean.TRUE);

				} else if(BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700008, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo(), Boolean.TRUE);

					
				} else if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700010, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo(), Boolean.TRUE);
				}	  
			} else{
				throw new EpException(MessageConst.ERROR_700020);
			}
		}    
		finally {
			//directPurchaseSO = null;
		}
		return requestNoteResDTO;
	}

	/**
	 * approve, reject and revise request note details.
	 * save data into database and BPM process will route to the next respective user.
	 * and also authenticating gpki before approve request note details
	 * page navigate to acknowledge page.
	 * @param outcome the outcome
	 */	
	public void approverAction(String outcome) {
		debugLogger("approverAction");
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		this.getManagedBean().setAction(outcome);
		ScRequestNote scRequestNote = getManagedBean().getRequestNote();
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		requestNoteReqDTO.setRequestNote(scRequestNote);
		
		//#31679 - Start	  		 	  	   	
  	   	if(requestNoteReqDTO.getRequestNote() != null){
  	   		
  	   		log.info("--- Bug 31679 (" + requestNoteReqDTO.getRequestNote().getRequestNoteNo() + ")" + "- Method: approverAction Start ---");
  	   		
	  	  	if(getManagedBean().getCurrentStatusId() != null && outcome != null  ){
	  	   		log.info("Outcome(" + outcome + ") " + "StatusId (" + getManagedBean().getCurrentStatusId() + ") "
	  	   				+ "RN No.(" + requestNoteReqDTO.getRequestNote().getRequestNoteNo()+ ")");
	  	   	}
	  	  	
  	   		if(requestNoteReqDTO.getRequestNote().getQuote() != null){
	  	   		log.info("RN No.(" + requestNoteReqDTO.getRequestNote().getRequestNoteNo()+ ") " 
	  	   				+ "SQ No.(" + requestNoteReqDTO.getRequestNote().getQuote().getQuoteNo() + ") "
	  	   				+ "Zonal(" + requestNoteReqDTO.getRequestNote().getQuote().isZonal() + ")");
  	   		}	  	   		 		
  	   		
  	   		if(requestNoteReqDTO.getRequestNote().getRequestNoteDtls() != null){
  		    		for(ScRequestNoteDtl rnDtl : requestNoteReqDTO.getRequestNote().getRequestNoteDtls()){		    			
  		    			if(rnDtl.getRequestItem() != null 
  		    					&& rnDtl.getScRequestSupplierItem() != null){	  		    				
  		    				log.info("RnDtlId (" + rnDtl.getRequestNoteDtlId() + ") " 
  		    						+ "ReqItemId (" + rnDtl.getRequestItem().getRequestNoteItemId() + ") " 
  		    						+ "ReqSupItemId (" + rnDtl.getScRequestSupplierItem().getRequestSupplierItemId() + ") "
  		    						+ "RnNo (" + requestNoteReqDTO.getRequestNote().getRequestNoteNo() + ")"); 
  		    			}
  		    		}
  		    }
  	   	log.info("--- Bug 31679 (" + requestNoteReqDTO.getRequestNote().getRequestNoteNo() + ")" + "- Method: approverAction End ---");
  	   	}  
  	  //#31679 - End

		List<ScRequestNoteDtl> mixedDtls = scRequestNote.getRequestNoteDtls();
		List<ScRequestNoteDtl> nonDeletedDtls = new ArrayList<ScRequestNoteDtl>();
		if(mixedDtls != null) {
			for(ScRequestNoteDtl dtl : mixedDtls) {
				if(!dtl.isDeleted()) {
					nonDeletedDtls.add(dtl);
				}
			}
		}
		
		//set new requestnotedtl to request note (without deleted requestnoteDtl
		scRequestNote.setNewRequestNoteDtls(nonDeletedDtls);
		requestNoteReqDTO.setUserLocale(new FacesScopes().getUserLocale());
		//getRequestNoteSO().validateRNBeforeSubmitting(requestNoteReqDTO);
		
		updateBPMTask(outcome);
	}

	/**
	 * Gets the suppliers by selected request note details items.
	 * 
	 * @return the suppliers by selected items
	 */
	public List<SmSupplier> getSuppliersBySelectedItems() {
		List<SmSupplier> suppliers = new ArrayList<SmSupplier>();
		List<ScRequestNoteDtl>	requestNoteDtls	= getManagedBean().getRequestNoteDtls();
		
		if(requestNoteDtls != null) {
			int itemSize = 0;
			Map<SupplierPK, Integer> supplierMap = new HashMap<SupplierPK, Integer>();
			
			for (ScRequestNoteDtl requestNoteDtl : requestNoteDtls) {
				if(requestNoteDtl.isSelected()){
					++ itemSize;
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
			}
			for (Entry<SupplierPK, Integer> supplierEntry : supplierMap.entrySet()) {
				if (supplierEntry.getValue().intValue() == itemSize) {
					suppliers.add(supplierEntry.getKey().getSmSupplier());
				}
			}
		}
		
		return suppliers;
	}
	
	/**
	 * Apply PR/LOA Prepared By and Delivery/Service Period  for selected request note details 
	 * in Item Details in RN
	 */
	public void applyForRequestNote() {
		debugLogger("applyForRequestNote");
		int counter = 0;
		List<ScRequestNoteDtl>	requestNoteDtls	= new ArrayList<ScRequestNoteDtl>();

		if(getManagedBean().getRequestNoteDtls() != null && getManagedBean().getRequestNoteDtls().size() >0) {
			PmUser assignedUser = null;
			RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
			if(getManagedBean().getAssignedToUserId() != null && getManagedBean().getAssignedToUserId() > 0){
				assignedUser= getUserById(getManagedBean().getAssignedToUserId());
			}
			
			for(ScRequestNoteDtl selectedReqDtl : getManagedBean().getRequestNoteDtls()) {
				if(selectedReqDtl.isSelected()){
					
					// prepare by
					if(assignedUser != null){
						selectedReqDtl.setAssignedUser(assignedUser);
					} 
					
//					if(selectedReqDtl.getScRequestNote().getSourceMethod().equals(ParameterConst.REQUEST_NOTE_SC)){
						// procurement type category
						if(getManagedBean().getProcTypeCategoryId() != null && getManagedBean().getProcTypeCategoryId()>0){
							ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
							parameterReqDTO.setParameterId(getManagedBean().getProcTypeCategoryId());
							PmParameter procTypeCategory = getParameterSO().retrieveParameterById(parameterReqDTO).getPmParameter();
							selectedReqDtl.setProcurementTypeCategory(procTypeCategory);
							selectedReqDtl.setProcTypeCategoryId(procTypeCategory.getParameterId());
						}
						
						// procurement type category
						if(null != getManagedBean().getProcTitle() && !getManagedBean().getProcTitle().isEmpty()){
							selectedReqDtl.setProcurementTitle(getManagedBean().getProcTitle());
						}
						
						if( getManagedBean().getPeriodValue() != null && getManagedBean().getPeriodValue() >0){
							selectedReqDtl.setDeliverTerm(getManagedBean().getPeriodValue());  
						}
//					}
					// rec supplier
//					if(getManagedBean().getSmSupplier() != null && getManagedBean().getSmSupplier().getSupplierId() > 0 ){
					if(getManagedBean().getRecommendSupplierId() != null){
						if(selectedReqDtl.getRequestItem() != null){							
							reqDTO.setRequestItemId(selectedReqDtl.getRequestItem().getRequestNoteItemId());
							reqDTO.setSupplierId(getManagedBean().getRecommendSupplierId() );
							RequestNoteResDTO resDTO = getRequestNoteSO().getSuppItemByReqItemIdAndSupplierId(reqDTO);
							ScRequestSupplierItem supplierItem = resDTO.getSupplierItem();
							
							if (supplierItem != null) {
								selectedReqDtl.setScRequestSupplierItem(supplierItem);
							}
						}
						
						supplierListener(selectedReqDtl.getScRequestSupplierItem().getSupplierId(),
								selectedReqDtl.getPriceType().getParameterId() ,
								selectedReqDtl,
								selectedReqDtl.getAssignedUser().getUserId() , 
								selectedReqDtl.getProcTypeCategoryId());
					}

					requestNoteDtls.add(selectedReqDtl);

					++ counter;
				} else{
					requestNoteDtls.add(selectedReqDtl);
				}

			}
			getManagedBean().setRequestNoteDtls(requestNoteDtls);
		}
		
		//start -- 31689
		for(ScRequestNoteDtl selectedReqDtl : getManagedBean().getRequestNoteDtls()) {
			calculatePricePerItem(selectedReqDtl);
		}
		//end -- 31689


		if(counter==0){
			throw new EpException(MessageConst.ERROR_760158);
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_PAGE);
	}

	/**
	 * page navigation from search catalogue to  Request Note page.
	 */
	public void requestNoteAction() {
		debugLogger("requestNoteAction");
		ScRequestNote scRequestNote = getManagedBean().getRequestNote();
		
		for (ScRequestNoteDtl requestNoteDtl : scRequestNote.getRequestNoteDtls()) {
			if(requestNoteDtl.getPriceType() != null){
				priceTypeChangeListener(requestNoteDtl.getPriceType().getParameterId(), requestNoteDtl);
			}
		}
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_PAGE);
	}
	
	/**
	 * add non contract item to request note to prepare request note
	 * @param cmSupplierItemId the cm supplier item id
	 * @param cart the cart
	 * @param compareSupplierItems the compare supplier items
	 */
	public void addToCart(Long cmSupplierItemId , boolean cart , List<CmSupplierItem> compareSupplierItems) {
		debugLogger("addToCart");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
		CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
		catReqDTO.setSupplierItemId(cmSupplierItemId);
		CatalogueResDTO catResDTO = catalogueSO.findActivePricing(catReqDTO);
		ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
		
		List<CmPricing> cmPricings = catResDTO.getPriceLs();
		if(cmPricings != null && cmPricings.size() > 0){
			CmSupplierItem cmSupplierItem = cmPricings.get(0).getCmSupplierItem();
			cmSupplierItem.setCmPricings(cmPricings);
			ScRequestItem requestItem = createRequestItem(cmSupplierItem);

			SmSupplier smSupplier = cmSupplierItem.getSmSupplier();
			
			RequestNoteReqDTO rnReqDTO = new RequestNoteReqDTO();
			rnReqDTO.setSupplierId(smSupplier.getSupplierId());
			getRequestNoteSO().validateSupplierAddressExist(rnReqDTO);
			
			requestSupplierItem = createScRequestSupplierItem(cmSupplierItem, requestItem, smSupplier, cmPricings);
			//ScSupplierItemPrice supplierItemPrice = createSupplierItemPrice(requestSupplierItem, cmPricings.get(0));
			if(requestSupplierItem.getSupplierItemPrices() != null && requestSupplierItem.getSupplierItemPrices().size()>0){
				requestSupplierItem.setSupplierItemPrice(requestSupplierItem.getSupplierItemPrices().get(0));
			}
			
			requestSupplierItem.setSupplierId(smSupplier.getSupplierId());
			requestSupplierItem.setSupplierName(smSupplier.getCompanyName());
			requestSupplierItem.setMofNumber(getMofNoBySupplierId(smSupplier.getSupplierId()));

			// check supplier is government seller or not 
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
				// PriceType drop down list in AddToCart
				getManagedBean().setPriceTypes(getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, priceTypes));
				// required validation
				getManagedBean().setRequired(Boolean.TRUE);
				// #28789 - start
				getManagedBean().setCompleteCmPricingList(cmSupplierItem.getCmPricings());
				onChangePriceType();
				// #28789 - end
			}
		} else {
			// remove after catalogue team had add the filtering in view.
			throw new EpException(MessageConst.ERROR_760159);
		}
		
		// copy suppliers from compare screen
		if(compareSupplierItems !=null){
			getManagedBean().setCompareSupplierItems(compareSupplierItems);
		}
		
		// set request supplier item.
		getManagedBean().setRequestSupplierItem(requestSupplierItem);
		
		getManagedBean().setJustRequired(Boolean.FALSE);
		
		getCurrentStatus(getPmStatusByStatusId(DPStatusConstants.RN_PENDING_SUBMISSION));	
		
		// clear the justification type, price type and justification remarks
		getManagedBean().setPriceType("");
		getManagedBean().setJustificationType("");
		getManagedBean().setJustification("");
		getManagedBean().setProcTypeOption(Boolean.FALSE);
		
		getManagedBean().setAddToCartFlag(cart);
		// update item details data table
		getManagedBean().setAutoUpdate(Boolean.FALSE);
		// #28789 - start
		getManagedBean().setPriceTypeStringFlag(false);
		getManagedBean().setPriceTypeTableFlag(false);
		// #28789 - start
		RequestContext.getCurrentInstance().execute("addToCartPopup.show();");
	}
	
	/**
	 * Creates the request item.
	 *
	 * @param cmSupplierItem the cm supplier item
	 * @return the sc request item
	 */
	private ScRequestItem createRequestItem(CmSupplierItem cmSupplierItem){
		ScRequestItem scRequestItem	= new ScRequestItem();
		CmItem cmItem = cmSupplierItem.getCmItem();
		boolean prodOrService = false;
		String extensionCode = cmItem.getExtensionCode();
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CATALOGUE_MANAGEMENT,
						ICatalogueSO.class);
		
		if(extensionCode != null) {
			String pOrS = catalogueSO.checkProductOrService(extensionCode);
			if(pOrS.equalsIgnoreCase("P")) {
				prodOrService = true;
			}
		}
		
		if (!prodOrService) { 
			scRequestItem.setItemType(getParameterByTypeAndCode(
					ParameterTypeConst.SC_ITEM_TYPE, ParameterConst.ITEM_TYPE_SERVICE));
		} else  {
			scRequestItem.setItemType(getParameterByTypeAndCode(
					ParameterTypeConst.SC_ITEM_TYPE, ParameterConst.ITEM_TYPE_PRODUCT));
		}
		scRequestItem.setCmItem(cmItem);
		scRequestItem.setItemName(cmItem.getItemName());
		scRequestItem.setItemDesc(cmSupplierItem.getItemDesc());
		scRequestItem.setMinqty(cmSupplierItem.getMinOrder());
		
		if(cmSupplierItem.getPmUom()!=null){
			scRequestItem.setUom(cmSupplierItem.getPmUom());
		} else{
			Locale locale = new FacesScopes().getUserLocale();
			throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.uom",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)});
		}
		return scRequestItem;
	}
	
	/**
	 * Creates the sc request supplier item.
	 *
	 * @param cmSupplierItem the cm supplier item
	 * @param requestItem the request item
	 * @param smSupplier the sm supplier
	 * @param cmPricings the cm pricings
	 * @return the sc request supplier item
	 */
	private ScRequestSupplierItem createScRequestSupplierItem(CmSupplierItem cmSupplierItem, ScRequestItem requestItem, SmSupplier smSupplier, List<CmPricing> cmPricings) {
		ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
		requestSupplierItem.setSupplierId(smSupplier.getSupplierId());
		requestSupplierItem.setRequestNoteItem(requestItem);
		requestSupplierItem.setSmSupplier(smSupplier);
		requestSupplierItem.setCmSupplierItem(cmSupplierItem);
		
		requestSupplierItem.setSupplierItemPrices(createScSupplierItemPrices(requestSupplierItem, cmPricings));
		
		return requestSupplierItem;
	}

	/**
	 * Creates the sc supplier item prices.
	 *
	 * @param scRequestSupplierItem the sc request supplier item
	 * @param pricings the pricings
	 * @return the list
	 */
	private	List<ScSupplierItemPrice>	createScSupplierItemPrices(ScRequestSupplierItem scRequestSupplierItem, List<CmPricing> pricings){
		List<ScSupplierItemPrice> results	=	new ArrayList<ScSupplierItemPrice>();
		for(CmPricing pricing: pricings){
			results.add(createSupplierItemPrice(scRequestSupplierItem, pricing));
		}
		return results;
	}
	
	/**
	 * Creates the supplier item price.
	 *
	 * @param scRequestSupplierItem the sc request supplier item
	 * @param pricing the pricing
	 * @return the sc supplier item price
	 */
	private ScSupplierItemPrice		createSupplierItemPrice(ScRequestSupplierItem	scRequestSupplierItem, CmPricing pricing){
		ScSupplierItemPrice	result	=	new ScSupplierItemPrice();
		result.setScRequestSupplierItem(scRequestSupplierItem);
		result.setPricing(pricing);
		
		ScRequestNote scRequestNote = getManagedBean().getRequestNote();
		ScRNZone zone = getManagedBean().getScRNZone(); 
		
		 Map<String, TplSpecZoneDtl> zoneDtlMap =  getManagedBean().getZoneDtlMap();
    	 if(zoneDtlMap == null){
    	    zoneDtlMap  = new HashMap<String, TplSpecZoneDtl>();
    	 }
		
		// zone dtl
		if(pricing.getCmSupplierZone() != null){
			result.setTplSpecZone(SourcingZoneHelper.getInstance().createSpecZone(pricing.getCmSupplierZone(), 
					zoneDtlMap, zone, scRequestNote.isBrandNew()));
		}
		getManagedBean().setZoneDtlMap(zoneDtlMap);
		
		if (zone.isBrandNew()) {
			zone.setScRequestNote(scRequestNote);
			scRequestNote.setScRNZone(zone);
		}
		result.setPriceType(getParameterByTypeAndCode(
				ParameterTypeConst.PRICE_TYPE, pricing.getPricingType()));

		if (pricing.getPrice() != null) {
			result.setItemPrice(pricing.getPrice());
		} else {
			result.setItemPrice(new BigDecimal(0));
		}
		
		if (pricing.getFromQty() != null) {
			result.setFromQty(new BigDecimal(pricing.getFromQty()));	
		}
		
		if (pricing.getToQty() != null) {
			result.setToQty(new BigDecimal(pricing.getToQty()));	
		}

		return result;
	}

	/**
	 * Gets the price type drop list values.
	 *
	 * @param parameterType the parameter type
	 * @param priceTypes the price types
	 * @return the price type drop down values
	 */
	public SelectItem[] getPriceTypeDropdownValues(String parameterType,
			List<String> priceTypes) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			items = new SelectItem[priceTypes.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			int i = 1;
			for (String priceTypeCode : priceTypes) {
				items[i++] = new SelectItem(priceTypeCode,getParameterDesc(parameterType,
						priceTypeCode));
			}
	} finally {
			
		}
		return items;
	}
	
	/**
	 * Gets the price drop down values .
	 *
	 * @param parameterType the parameter type
	 * @param priceTypes the price types
	 * @return the price types
	 */
	public SelectItem[] getPriceTypes(String parameterType,
			List<String> priceTypes) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			items = new SelectItem[priceTypes.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
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
	
	/**
	 * Gets the pm parameter by id.
	 *
	 * @param parameterType the parameter type
	 * @param parameterCode the parameter code
	 * @return the pm parameter by id
	 */
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
	 * Page navigation to catalogue search page in Request Note.
	 */
	public void backToPrevious() {
		debugLogger("backToPrevious");
		getManagedBean().setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
		ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
		ScRequestItem requestItem = new ScRequestItem();
		requestSupplierItem.setRequestNoteItem(requestItem);
		getManagedBean().setRequestSupplierItem(requestSupplierItem);
		getManagedBean().setProcurementType(null);
		getManagedBean().setJustification(null);
		getManagedBean().setJustificationType(null);
		getManagedBean().setProcTypeOption(Boolean.FALSE);
		FacesContext fc = FacesContext.getCurrentInstance();

		if(getManagedBean().getBackToPreviousPage() != null && !getManagedBean().getBackToPreviousPage().isEmpty()){
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getBackToPreviousPage());
		} else{
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "catalogueSearch");
		}
	}

	/**
	 * add non contract item to request note to prepare request note
	 * page navigate to request note page.
	 * @param cartFlag the cart flag
	 */
	public void addToRequestNote(boolean cartFlag) {
		debugLogger("addToRequestNote");
		List<ScRequestNoteDtl>	requestNoteDtls = getManagedBean().getRequestNoteDtls();
		ScRequestSupplierItem requestSupplierItem = getManagedBean().getRequestSupplierItem();
		ScRequestNoteDtl scRequestNoteDtl = new ScRequestNoteDtl();
		ScRequestItem scRequestItem = requestSupplierItem.getRequestNoteItem();
		// add to cart validation
		getManagedBean().setRequired(Boolean.FALSE);
		getManagedBean().setJustRequired(Boolean.FALSE);
		
		ISearchBean<VCmSupplierItem> viewSearchBean = null;
		List<Long> cmSupplieritemIds = null;
		
		if(scRequestItem.getQuantity() !=null && scRequestItem.getQuantity().intValue() < 0){
			Locale locale = new FacesScopes().getUserLocale();
			throw new EpException(MessageConst.ERROR_100033, new String[] {ResourceBundleUtils.getMessage("lbl.sc.order.qty",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale), "0"});
		}
		
		ScSupplierItemPrice scSupplierItemPrice = requestSupplierItem.getSupplierItemPrice();
		CmSupplierItem selectedSupplierItem = scSupplierItemPrice.getPricing().getCmSupplierItem();
		ScRequestNote  requestNote = getManagedBean().getRequestNote();
		
		if(requestNote.getSourceMethod() == null || requestNote.getSourceMethod().isEmpty()){
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
		}
		
		 if(getManagedBean().getSourceMethod().equalsIgnoreCase(ParameterConst.METHOD_PRI)){
			// purchase inquiry
			if(getManagedBean().getRequestNoteDtls() != null){
				requestSupplierItem.setScPrInquiy(getManagedBean().getRequestNoteDtls().get(0).getScRequestSupplierItem().getScPrInquiy());		
			}

		} else {
			//IViewSupplierItemSO viewSupplierItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, IViewSupplierItemSO.class);
	    	//CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();
			// purchase in default & add to cart
		    
		    String extensionCode = scRequestItem.getCmItem().getExtensionCode();
		    Long uomId = selectedSupplierItem.getPmUom().getUomId();
		    StringBuilder sb = new StringBuilder();
		    sb.append("ITEM_CODE^").append(extensionCode).append("|")//item Code
		    //.append("catType^").append("XC").append("|")
		    .append("UOM_ID^").append(uomId);
		    
		    if(cartFlag){
		    	// adding suppliers from the compare screen 
		    	StringBuilder supplierItemIds = new StringBuilder();
		    	cmSupplieritemIds = new ArrayList<Long>();
		    	if(getManagedBean().getCompareSupplierItems() != null){
		    		for (CmSupplierItem supplierItem : getManagedBean().getCompareSupplierItems()) {
		    			if(supplierItem.getCmItem().getItemId().equals(selectedSupplierItem.getCmItem().getItemId()) 
		    					&& supplierItem.getPmUom().getUomName().equals(selectedSupplierItem.getPmUom().getUomName())){
		    				Long supplierItemId = supplierItem.getSupplierItemId();
		    				
		    				cmSupplieritemIds.add(supplierItemId);	
		    				if (supplierItemIds.length() > 0) {
								supplierItemIds.append(",");
							}
							supplierItemIds.append(supplierItemId);
		    			}
		    		}
		    	}
		    	
		    	sb.append("|")
		    		.append("DOC_ID^")
			    	.append(supplierItemIds)
			    	.append("^")
			    	.append(SearchOperator.OPERATOR_IN.value());
		    	
		    /*	ISearchBean<VCmSupplierItem>  searchBean  = new CatalogueBackingBean().getManagedBean().getSearchDataModel().getSearchBean();
				searchBean.getDataFilterBeans().clear();
				catalogueReqDTO.setViewSearchBean(searchBean);
				catalogueReqDTO.setSupplierItemId(selectedSupplierItem.getSupplierItemId());
				catalogueReqDTO.setExtensionCode(scRequestItem.getCmItem().getExtensionCode());
				catalogueReqDTO.setUomId(selectedSupplierItem.getPmUom().getUomId());
		    	CatalogueResDTO catalogueResDTO = viewSupplierItemSO.getSuppliersItemPricingByExtensionCode(catalogueReqDTO);

		    	if(catalogueResDTO.getSupplierItemList() != null && catalogueResDTO.getSupplierItemList().size()>0){

		    		for (CmSupplierItem supplierItem : catalogueResDTO.getSupplierItemList()) {
		    			if (supplierItem.getItemPricings() != null && supplierItem.getItemPricings().size() > 0) {

		    				if(cmSupplieritemIds.contains(supplierItem.getSupplierItemId())){

		    					if(supplierItem.getCmItem().getItemId().equals(selectedSupplierItem.getCmItem().getItemId())){

		    						ScRequestSupplierItem scRequestSupplierItem = createScRequestSupplierItem(
		    								scRequestItem, supplierItem.getSmSupplier(), supplierItem.getItemPricings());
		    						supplierItems.add(scRequestSupplierItem);	
		    					}
		    				}
		    			}
		    		}
		    	}
		    	// Add selected supplier item from compare 
	    		supplierItems.add(requestSupplierItem);*/
		    } else{
		    	
		    	// adding suppliers from search catalogue 
		    	/*catalogueReqDTO = new CatalogueReqDTO();
		    	catalogueReqDTO.setViewSearchBean(new CatalogueBackingBean().getManagedBean().getSearchDataModel().getSearchBean());
		    	catalogueReqDTO.setSupplierItemId(selectedSupplierItem.getSupplierItemId());
		    	catalogueReqDTO.setExtensionCode(scRequestItem.getCmItem().getExtensionCode());
		    	catalogueReqDTO.setUomId(selectedSupplierItem.getPmUom().getUomId());
		    	CatalogueResDTO  catalogueResDTO = viewSupplierItemSO.getSuppliersItemPricingByExtensionCode(catalogueReqDTO);
		    	if(catalogueResDTO.getSupplierItemList() != null && catalogueResDTO.getSupplierItemList().size()>0){
		    		for (CmSupplierItem supplierItem : catalogueResDTO.getSupplierItemList()) {
		    			if (supplierItem.getItemPricings() != null && supplierItem.getItemPricings().size() > 0) {
		    				ScRequestSupplierItem scRequestSupplierItem = createScRequestSupplierItem(
		    						scRequestItem, supplierItem.getSmSupplier(), supplierItem.getItemPricings());

		    				supplierItems.add(scRequestSupplierItem);	
		    			}
		    		}
		    		
		    	}
		    	// Add selected supplier item from search catalogue
	    		supplierItems.add(requestSupplierItem);*/
		    	
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
		    }
		    
		    scRequestNoteDtl.setConditionField(sb.toString());
		    
		    VCmSupplierItem  vCmSupplierItem = new VCmSupplierItem();
		    vCmSupplierItem.setCompanyName(selectedSupplierItem.getSmSupplier().getCompanyName());
		    scRequestNoteDtl.setvCmSupplierItem(vCmSupplierItem);
		    
		    scRequestItem.setRequestNote(requestNote);
		}
		
		requestSupplierItem.setRequestNoteItem(scRequestItem);
		scRequestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
		scRequestNoteDtl.setRequestItem(scRequestItem);

		FacesScopes facesScopes = new FacesScopes();
		scRequestNoteDtl.setAssignedUser(facesScopes.getUserSessionBean());
		scRequestNoteDtl.setProcurementType(getParameterByTypeAndCode(
				ParameterTypeConst.PROCUREMENT_TYPE, getManagedBean().getProcurementType()));
		
		if (scRequestNoteDtl.getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			scRequestNoteDtl.setJustificationType(getReasonByCategoryAndCode(
					ReasonCategory.JUSTIFICATION_AD_HOC.value(), getManagedBean().getJustificationType()));
		} else if (scRequestNoteDtl.getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)) {
			scRequestNoteDtl.setJustificationType(getReasonByCategoryAndCode(
					ReasonCategory.JUSTIFICATION_PK7.value(), getManagedBean().getJustificationType()));
		} else{
			scRequestNoteDtl.setJustificationType(null);
		}
		
		scRequestNoteDtl.setJustification(getManagedBean().getJustification());
		scRequestNoteDtl.setScRequestNote(requestNote);
		
		// delivery term type as days.
		scRequestNoteDtl.setDeliverTermType(getDeliveryTermType());
		
	    scRequestNoteDtl.setFulfilmentType(getParameterByTypeAndCode(
				ParameterTypeConst.FULFILMENT_TYPE, ParameterConst.FULFILMENT_TYPE_ONE_OFF));
	    
	    if(getManagedBean().getPriceType() != null){
	    	scRequestNoteDtl.setPriceType(getParameterByTypeAndCode(
					ParameterTypeConst.PRICE_TYPE, getManagedBean().getPriceType()));
	    	scRequestNoteDtl.setPriceTypeId(scRequestNoteDtl.getPriceType().getParameterId());
	    }
	    
	    if(selectedSupplierItem.getLeadTime() != null){
	    	 scRequestNoteDtl.setDeliverTerm(selectedSupplierItem.getLeadTime().intValue());
	    }
        
	    // validating suppliers
	    RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
	    requestNoteReqDTO.setRequestNoteDtl(scRequestNoteDtl);
	    requestNoteReqDTO.setExtensionCode(scRequestNoteDtl.getRequestItem().getCmItem().getExtensionCode());
	    requestNoteReqDTO.setUomId(scRequestNoteDtl.getRequestItem().getUom().getUomId());
	    requestNoteReqDTO.setViewSearchBean(viewSearchBean);
	    requestNoteReqDTO.setCmSupplierItemIds(cmSupplieritemIds);
	    requestNoteReqDTO.setUserLocale(new FacesScopes().getUserLocale());
	    getRequestNoteSO().validateAddToCart(requestNoteReqDTO);
	    
	    calculatePricePerItem(scRequestNoteDtl);
	    
		// have to compare duplicate items in request note
		if (getManagedBean().getRequestNoteDtls().isEmpty()){
			getManagedBean().getRequestNoteDtls().add(scRequestNoteDtl);
			
			// set by default selected price type , rec.supplier and offer price
			supplierListener(scRequestNoteDtl.getScRequestSupplierItem().getSupplierId(), scRequestNoteDtl.getPriceType().getParameterId() ,
					scRequestNoteDtl, scRequestNoteDtl.getAssignedUser().getUserId() , scRequestNoteDtl.getProcTypeCategoryId());
		} else {
			List<String> itemUOMIds = new ArrayList<String>();
			for (ScRequestNoteDtl noteDtl : getManagedBean().getRequestNoteDtls()) {
				ScRequestItem requestItem = noteDtl.getRequestItem();
				String itemUOMKey = generateItemUomKey(requestItem);
				
				
				itemUOMIds.add(itemUOMKey);
			}
			if (itemUOMIds.size() > 0) {
				ScRequestItem selectedRequestItem = scRequestNoteDtl.getRequestItem();
				String selectedItemUOMKey = generateItemUomKey(selectedRequestItem);
				if(itemUOMIds.contains(selectedItemUOMKey)){
					throw new EpException(MessageConst.ERROR_700027, 
							new String[]{scRequestNoteDtl.getRequestItem().getCmItem().getItemName() +
							"(" + selectedRequestItem.getUom().getUomName() + ")",""});
				} else {
					requestNoteDtls.add(scRequestNoteDtl);
					// set by default selected price type , rec.supplier and offer price
					supplierListener(scRequestNoteDtl.getScRequestSupplierItem().getSupplierId(), scRequestNoteDtl.getPriceType().getParameterId() ,
							scRequestNoteDtl, scRequestNoteDtl.getAssignedUser().getUserId() ,scRequestNoteDtl.getDeliverTermType().getParameterId());
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
				//ptjNameEvent();
			}
		}
		
		if(requestNote.getCreatedUser() == null){
			getManagedBean().setCreatedBy(getUserById(facesScopes.getUserSessionBean().getUserId()).getUserName());
		}
		initializeApproverGroup();
		
		getManagedBean().setRequestNote(requestNote);

		requestNote.setRemarks(initializeRemarks(
				requestNote,
				null,
				requestNote.getRemarks(),
				ScRequestNoteRemark.class));
		
		// update workflow and remarks
		updateWorkFlowAndRemarks(requestNote);

		getManagedBean().setFulfillmentType(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
		
		getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
		getManagedBean().setRequestNote(requestNote);

		//clear the selected price type
		getManagedBean().setPriceType("");
		// request note counter
		RequestContext.getCurrentInstance().execute("addToCartPopup.hide()");
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_PAGE);
		getManagedBean().setAutoUpdate(Boolean.TRUE);
	}

	/**
	 * Gets the price types by price.
	 *
	 * @param supplierItemPrices the supplier item prices
	 * @return the price type by price
	 */
	public List<PmParameter>  getPriceTypeByPrice(List<ScSupplierItemPrice> supplierItemPrices) {
		List<Long> checkIds = new ArrayList<Long>();
		List<PmParameter> params = new ArrayList<PmParameter>();
		if(CollectionUtils.isNotEmpty(supplierItemPrices)) {
			for (ScSupplierItemPrice scSupplierItemPrice : supplierItemPrices) {
				if(scSupplierItemPrice.getPriceType() != null){
					if(scSupplierItemPrice.getPriceType().getParameterId() != null){
						scSupplierItemPrice.setPriceType(getParameterByTypeAndCode(
								ParameterTypeConst.PRICE_TYPE, scSupplierItemPrice.getPriceType().getParameterCode()));
					}
					
					if(!checkIds.contains(scSupplierItemPrice.getPriceType().getParameterId())){
						checkIds.add(scSupplierItemPrice.getPriceType().getParameterId());
						params.add(scSupplierItemPrice.getPriceType());
					}
				}
			}
		}
		return params;
	}

	/**
	 * Event for procurement type  drop down in Add to Cart.
	 *  to show justification drop down and justification remark based on procurement type.
	 */
	public void procurementTypeEvent() {
		debugLogger("procurementTypeEvent"); 
		String procType = getManagedBean().getProcurementType();
		if (procType.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL) 
				|| procType.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);
			getManagedBean().setJustRequired(Boolean.FALSE);

		} else if (procType.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_AD_HOC.value()));
			getManagedBean().setJustRequired(Boolean.TRUE);
		} else if (procType.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_PK7.value()));
			getManagedBean().setJustRequired(Boolean.TRUE);
		} 

		// when procurement type as pk7 selected 
		if (procType.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)){
			// Temp(#34274 - Start)
			// MaxProcurementAmt
			// DecimalFormat amountDF = new DecimalFormat(new CommonUtils()
			// .getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
			//
			// BigDecimal maxProcurementAmt = getPRThresholdAmount();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
							MessageConst.INFO_760440, null, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			// Temp(#34274 - End)
		}
	}
	
	/**
	 * show Item Details, Justification and Pricing details Popup in Request Note.
	 *
	 * @param scRequestNoteDtl the sc request note dtl
	 */
	public void showReqNotePopUps(ScRequestNoteDtl scRequestNoteDtl){
		debugLogger("showReqNotePopUps");
		if(scRequestNoteDtl.getScRequestSupplierItem() != null){
			ScRequestSupplierItem  scRequestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();

			boolean prodOrService = checkServiceOrProduct(scRequestNoteDtl);
			if(prodOrService){
				getManagedBean().setItemCheck(Boolean.FALSE); 
			} else{
				getManagedBean().setItemCheck(Boolean.TRUE); 
			}

			if(scRequestSupplierItem.getSupplierItemPrices() != null && scRequestSupplierItem.getSupplierItemPrices().size() >0){
				for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {
					if(null != scRequestNoteDtl.getPriceType()){
						if(scSupplierItemPrice.getPriceType().getParameterId() == scRequestNoteDtl.getPriceType().getParameterId()){
							scRequestSupplierItem.getSupplierItemPrice().setPriceType(scSupplierItemPrice.getPriceType());
						}
					}
				}
			}
			getManagedBean().setRequestSupplierItem(scRequestSupplierItem);
			
			if(null != scRequestNoteDtl.getPriceType()){
				scRequestNoteDtl.setPriceTypeId(scRequestNoteDtl.getPriceType().getParameterId());	
			}
			
			ScRequestNoteDtl oriRequestNoteDtl = new ScRequestNoteDtl();
			ScRequestItem oriRequestItem = new ScRequestItem();
			oriRequestItem.setQuantity(scRequestNoteDtl.getRequestItem().getQuantity());
			oriRequestNoteDtl.setRequestItem(oriRequestItem);
			oriRequestNoteDtl.setPriceType(scRequestNoteDtl.getPriceType());
			
			getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
			getManagedBean().setOriSequestNoteDtl(oriRequestNoteDtl);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
		}
	}

	/**
	 * check whether request note details is product or service in Request Note.
	 *
	 * @param scRequestNoteDtl the sc request note dtl
	 * @return true, if successful
	 */
	private boolean checkServiceOrProduct(ScRequestNoteDtl scRequestNoteDtl){
		log.debug("checkServiceOrProduct");
		boolean prodOrService = false;
		String extensionCode = null;
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CATALOGUE_MANAGEMENT,
						ICatalogueSO.class);
		if(scRequestNoteDtl.getRequestItem() != null){
			ScRequestItem requestItem = scRequestNoteDtl.getRequestItem();
			
			if(requestItem.getCmItem() != null){
				extensionCode = requestItem.getCmItem().getExtensionCode();
				String pOrS = catalogueSO.checkProductOrService(extensionCode);
				if(pOrS.equalsIgnoreCase("P")) {
					prodOrService = true;
				}
			} else{
				if(requestItem.getTplSpecQuestion() != null){
					TplSpecQuestion specQuestion =requestItem.getTplSpecQuestion();
					if (ParameterConst.ITEM_TYPE_SERVICE.equals(specQuestion.getTplSpecMaster().getItemTypeCode().getParameterCode())) {	
						prodOrService = false;
					} else if (ParameterConst.ITEM_TYPE_PRODUCT.equals(specQuestion.getTplSpecMaster().getItemTypeCode().getParameterCode())) {	
						prodOrService = true;
					}
				}
			}
		}
		return prodOrService;
	}
	
	/**
	 * once add non contract item to request note
	 * can edit order quantity after added item to request note in item details Popup
	 */
	public void editQuantity(){
		debugLogger("editQuantity");

		if(getManagedBean().getRequestNoteDtl() != null){
			List<ScRequestNoteDtl>	requestNoteDtls	= new ArrayList<ScRequestNoteDtl>();
			BigDecimal quantity  = getManagedBean().getRequestNoteDtl().getRequestItem().getQuantity();
			ScRequestNoteDtl selectedRequestNoteDtl = getManagedBean().getRequestNoteDtl();

			if(getManagedBean().getRequestNoteDtls() != null && getManagedBean().getRequestNoteDtls().size() >0){
				for (ScRequestNoteDtl requestNoteDtl : getManagedBean().getRequestNoteDtls()) {
					if(requestNoteDtl.equals(selectedRequestNoteDtl)){
						requestNoteDtl.getRequestItem().setQuantity(quantity);
						calculatePricePerItem(requestNoteDtl);
						//requestNoteDtl.getScRequestSupplierItem().getRequestNoteItem().setQuantity(quantity);
						requestNoteDtls.add(requestNoteDtl);
					} else{
						requestNoteDtls.add(requestNoteDtl);
					}
				}
				getManagedBean().setRequestNoteDtls(requestNoteDtls);
			}
		}
		RequestContext.getCurrentInstance().execute("itemDtlPopup.hide()");
		getManagedBean().setAutoUpdate(Boolean.TRUE);
	}

	/**
	 * show pricing details in Request Note.
	 * after added item to request note
	 * @param scRequestNoteDtl the sc request note dtl
	 */
	public void showPricing(ScRequestNoteDtl scRequestNoteDtl){
		debugLogger("showPricing");
		List<ScSupplierItemPrice>	supplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		if(scRequestNoteDtl.getScRequestSupplierItem() != null){
			ScRequestSupplierItem  scRequestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();

			if(scRequestSupplierItem.getSupplierItemPrices() != null && scRequestSupplierItem.getSupplierItemPrices().size() >0){
				for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {

					if(scSupplierItemPrice.getPriceType().getParameterId().equals(scRequestNoteDtl.getPriceType().getParameterId())){
						supplierItemPrices.add(scSupplierItemPrice);
					}
				}
			}
			scRequestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);
			getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
			pricingHideAndShow(scRequestNoteDtl);
			getManagedBean().setPricingList(supplierItemPrices);
		}
	}
	
	/**
	 * Pricing hide and show.
	 *
	 * @param requestNoteDtl the request note dtl
	 */
	private void pricingHideAndShow(ScRequestNoteDtl requestNoteDtl) {
		debugLogger("pricingHideAndShow");
		if(requestNoteDtl.getScRequestSupplierItem().getSupplierItemPrice() != null){
			String priceTypeCode = requestNoteDtl.getScRequestSupplierItem().getSupplierItemPrice().getPriceType().getParameterCode();

			if(priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER) ||
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER) ){
				getManagedBean().setTierAndPromoTierFlag(Boolean.FALSE);
				getManagedBean().setZonalStandardAndPromoFlag(Boolean.TRUE);

			}
			if(priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD) || 
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION)){
				getManagedBean().setZonalStandardAndPromoFlag(Boolean.FALSE);
				getManagedBean().setTierAndPromoTierFlag(Boolean.TRUE);
			} 
			
			if(priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER) ||
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER) || priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_TARIFF) || 
					priceTypeCode.equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF) ){
				getManagedBean().setTierAndPromoTierFlag(Boolean.TRUE);
				getManagedBean().setZonalStandardAndPromoFlag(Boolean.TRUE);
			}
		}
	}
	
	
	/**
	 * if item is purchase inquiry then that item shows icon for purchase inquiry to view in  request note. 
	 * @param requestNoteDtl the request note dtl
	 * @return true, if successful
	 */
	public boolean showPurchaseInqIcon(ScRequestNoteDtl requestNoteDtl) {
		debugLogger("showPurchaseInqIcon");
		Boolean isPurchaseInq= Boolean.FALSE;
		if(requestNoteDtl != null && requestNoteDtl.getScRequestSupplierItem().getScPrInquiy() != null ){
			if(requestNoteDtl.getRequestItem() != null){
				if(requestNoteDtl.getRequestItem().getSupplierItems() != null && requestNoteDtl.getRequestItem().getSupplierItems().size()>0){
					for (ScRequestSupplierItem scRequestSupplierItem : requestNoteDtl.getRequestItem().getSupplierItems()) {
						if(scRequestSupplierItem.getScPrInquiy() != null){
							isPurchaseInq = Boolean.TRUE;
							break;
						} else{
							isPurchaseInq = Boolean.FALSE;
						}
					}
				}
			}
		}
		return isPurchaseInq;
	}
	
	/**
	 * get Prepared By method to get drop down in request note.
	 * to get PR/LOA Prepared By users based on Fulfillment type
	 * @param requestNoteDtl the request note dtl
	 */
	public List<PmUser> getPreparedBy(ScRequestNoteDtl requestNoteDtl) {
		debugLogger("getPreparedBy"); 
		UserReqDTO reqDTO = new UserReqDTO();
		List<PmUser> userlist = null;
		if(requestNoteDtl.getFulfilmentType() != null ){
			if (requestNoteDtl.getFulfilmentType().getParameterCode().
					equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
				reqDTO.setRoleCode(RoleCode.REQUISITIONER.value());
				
			} else {
				reqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
			}

			if(getManagedBean().getRequestNote().getPmOrgProfile()!= null){
				reqDTO.setOrgProfileId(getManagedBean().getRequestNote().getPmOrgProfile().getOrgProfileId());
			}

			UserResDTO resDTO = getUserSO().retrieveUserListByRole(reqDTO);
			userlist = resDTO.getUserList();
			if(userlist != null && !userlist.isEmpty()){
				Collections.sort(userlist, new usersComparator());
			}
		}
		getManagedBean().setPreparedBys(userlist);
		return userlist;
	}
	
	/**
	 * get Prepared By method to get drop down in request note.
	 * to get PR/LOA Prepared By users based on Fulfillment type
	 * @param requestNoteDtl the request note dtl
	 */
	public List<Object[]> getRecommendedSupplierList() {
		debugLogger("getRecommendedSupplierList"); 
		
		List<Long>	requestItemIds= new ArrayList<Long>();

		if (CollectionUtils.isNotEmpty(getManagedBean().getRequestNoteDtls())) {
			for(ScRequestNoteDtl selectedReqDtl : getManagedBean().getRequestNoteDtls()) {
				Long requestItemId = selectedReqDtl.getRequestItem().getRequestNoteItemId();
				if(!requestItemIds.contains(requestItemId)) {
					requestItemIds.add(requestItemId);
				}
			}
		}
		
		RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
		reqDTO.setRequestItemIds(requestItemIds);
		RequestNoteResDTO resDTO = getRequestNoteSO().retrieveIntersectSupplierofRequestItems(reqDTO);
		List<Object[]> supplierIdNames = resDTO.getSupplierIdandNameList();
		if(resDTO.isSuccessful()) {
			getManagedBean().setRecommendSupplierList(supplierIdNames);
		}
		
		return supplierIdNames;
	}

	// sorting prepared by users
    /**
	 * The Class usersComparator.
	 */
	class usersComparator implements Comparator<PmUser> {
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(PmUser pmUser1, PmUser pmUser2) {
			int sort;
			// Sort on Name first 
			sort = pmUser1.getUserName().compareTo(pmUser2.getUserName());
			return sort;
		}
	}
	
	/**
	 * Delete request note dtl.
	 * delete - delete selected items in item details in request note.
	 * @return void
	 */
	public void deleteRequestNoteDtl() {
		debugLogger("deleteRequestNoteDtl");
		int counter = 0;
		List<ScRequestNoteDtl>	deleteRequestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
		List<ScRequestNoteDtl>	mainRequestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
		List<ScRequestNoteDtl>	requestNoteDtls	=	getManagedBean().getRequestNoteDtls();
		if (requestNoteDtls != null && !requestNoteDtls.isEmpty()) {
			for (ScRequestNoteDtl requestNoteDtl : requestNoteDtls) {
				if (requestNoteDtl.isSelected()) {
					// delete request note list
					requestNoteDtl.setDeleted(Boolean.TRUE);
					
					if(requestNoteDtl.getRequestNoteDtlId() != null){
						deleteRequestNoteDtls.add(requestNoteDtl);
					}
					
					++ counter;
				} else{
					mainRequestNoteDtls.add(requestNoteDtl);
				}
			}
		
			if(counter == 0){
				throw new EpException(MessageConst.ERROR_760158);
			}
		}
		
		getManagedBean().setDeleteRequestNoteDtls(deleteRequestNoteDtls);
		getManagedBean().setRequestNoteDtls(mainRequestNoteDtls);
		getManagedBean().getRequestNote().setRequestNoteDtls(mainRequestNoteDtls);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_PAGE);
	}

	
	/**
	 * Creates the target approver.
	 *
	 * @param requestNote the request note
	 */
	private void formApprover(ScRequestNote requestNote){
		Long selectedApproverId = getManagedBean().getSelectedApproverId();
		if(selectedApproverId != null) {
			List<PmUser> backupApprovers = new ArrayList<PmUser>();
			if(getManagedBean().getApproverList() != null) {
				List<PmUser> approverList = getManagedBean().getApproverList();

				for (PmUser user : approverList) {
					if(selectedApproverId.equals(user.getUserId())){
						ScRequestNoteApprovalUser approver = new ScRequestNoteApprovalUser();
						approver.setPmUser(user);
						approver.setRequestNote(requestNote);
						requestNote.setApprover(user);
					} else{
						backupApprovers.add(user);
					}
				}
			}
			getManagedBean().setBackupApprovers(backupApprovers);
		}  else{
			requestNote.setApprover(null);
		}
	}

	/**
	 * Gets the current status to show in request note.
	 *
	 * @param pmStatus the pm status
	 * @return the current status
	 */
	public void  getCurrentStatus(PmStatus pmStatus) {
		getManagedBean().setRequestNoteStatus(pmStatus);
	}
	
	/**
	 * Gets the user by user id.
	 *
	 * @param userId the user id
	 * @return the user by id
	 */
	public PmUser  getUserById(Long userId) {
		PmUser user = null;
		UserReqDTO reqDTO = new UserReqDTO();
		if(userId != null){
			reqDTO.setUserId(userId);
			UserResDTO resDTO = getUserSO().retrieveUserByUserId(reqDTO);

			if(resDTO.getPmUser() != null){
				 user = resDTO.getPmUser();
			}
		}
		return user;
	}
	
	/**
	 * Checks if is supplier user.
	 *
	 * @return true, if is supplier user
	 */
	public boolean isSupplierUser(){
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_USER");
	}


	/**
	 * Checks if is requisitioner.
	 *
	 * @return true, if is requisitioner
	 */
	public boolean isRequisitioner(){
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("REQUISITIONER");
	}

	
	/**
	 * gets purchase in default based on ptj.
	 * if item has purchase in default it shows icon to view pid in request note
	 * @return void
	 */
	public void ptjNameEvent() {
		IPurchaseInDefaultSO purchaseInDefaultSO  = null;
		try {
			// Based on ptj selection have to get pid for request note 
			purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
			// fetch pid item for request note item	
			List<ScRequestItem>	requestNoteItems = getManagedBean().getRequestNoteItems();
			List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
			if(getManagedBean().getRequestNoteDtls() != null && getManagedBean().getRequestNoteDtls().size() >0){
				for (ScRequestNoteDtl scRequestNoteDtl : getManagedBean().getRequestNoteDtls()) {
					ScRequestSupplierItem requestSupplierItem = scRequestNoteDtl.getScRequestSupplierItem();
					ScRequestItem requestItem = scRequestNoteDtl.getRequestItem();
					
					// selected ptj id for non contract item for pid
					purchaseInDefaultReqDTO.setOrgProfileId(getManagedBean().getRequestNote().getPmOrgProfile().getOrgProfileId());
					purchaseInDefaultReqDTO.setItemCode(scRequestNoteDtl.getRequestItem().getCmItem().getExtensionCode()) ;
					PurchaseInDefaultResDTO defaultResDTO = purchaseInDefaultSO.getPurchaseInDefaultByItemCode(purchaseInDefaultReqDTO);

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
	 * if item has purchase in default it shows icon to view pid in request note.
	 *
	 * @param requestNoteDtl the request note dtl
	 *  page navigate to purchase in default.
	 */
	public void showPurchaseInDefault(ScRequestNoteDtl requestNoteDtl){
		debugLogger("showPurchaseInDefault");
		PurchaseInDefaultBackingBean backingBean = new PurchaseInDefaultBackingBean();
		if(requestNoteDtl.getRequestItem().getPurchaseInDefault() != null){
			backingBean.viewPurchaseInDefault(requestNoteDtl.getRequestItem().getPurchaseInDefault().getPurchaseInDefaultId());
		}
	}

	/**
	 * when purchase inquiry to request note, request note shows icon for purchase inquiry to view.
	 * if item has purchase inquiry it shows icon to view purchase inquiry in request note.
	 * @param requestNoteDtl the request note dtl
	 *  page navigate to purchase inquiry.
	 */
	public void showPurchaseInquiry(ScRequestNoteDtl scRequestNoteDtl){
		debugLogger("showPurchaseInquiry");
		ScPrInquiry  prInquiry = null;
		PurchaseInquiryBackingBean prInqBackingBean = new PurchaseInquiryBackingBean();
	
		// no stock supplier to view in pi
		if(scRequestNoteDtl.getPiRequestSupplierItem() != null){
			prInquiry = scRequestNoteDtl.getPiRequestSupplierItem().getScPrInquiy();
		} else{
			prInquiry = scRequestNoteDtl.getScRequestSupplierItem().getScPrInquiy();
		}
		
		if(prInquiry != null){
			prInqBackingBean.viewPurchaseInquiry(prInquiry.getPrInquiryId());
		}
	}

	/**
	 * when Simple Quote  to request note, request note shows icon for Simple Quote to view.
	 * @param requestNoteDtl the request note dtl
	 *  page navigate to Simple Quote.
	 */
	public void showSimpleQuote(){
		debugLogger("showSimpleQuote");
		SimpleQuoteBackingBean simpleQuoteBackingBean = new SimpleQuoteBackingBean();
		if(getManagedBean().getRequestNote().getQuote() != null){
			ScQuote scQuote  = getManagedBean().getRequestNote().getQuote();
			simpleQuoteBackingBean.viewQuote(scQuote.getQuoteId());
		}
	}
	
	/**
	 * Close listener for add to cart.
	 * to close add to cart popup window.
	 */
	public void closeListener() {
		debugLogger("closeListener");
		// add to cart validation
		RequestContext.getCurrentInstance().execute("addToCartPopup.hide()");
		getManagedBean().setRequired(Boolean.FALSE);
		getManagedBean().setJustRequired(Boolean.FALSE);
	}
	
	/**
	 * Supplier listener.
	 * to get price type and offered price based on supplier id
	 *
	 * @param supplierId the supplier id
	 * @param priceTypeId the price type id
	 * @param requestNoteDtl the request note dtl
	 * @param assignedUserId the assigned user id
	 * @param deliveryTermId the delivery term id
	 */
	public void supplierListener(Long supplierId, Long priceTypeId, ScRequestNoteDtl requestNoteDtl, Long assignedUserId, Long procTypeCatId) {
		debugLogger("supplierListener");
		// lov changes 
		debugLogger(requestNoteDtl.getRequestNoteDtlId());
		debugLogger(requestNoteDtl.getRequestItem().getRequestNoteItemId());
		
		ScRequestSupplierItem scRequestSupplierItem = requestNoteDtl.getScRequestSupplierItem();
		debugLogger(scRequestSupplierItem.getRequestSupplierItemId());
		boolean isPriceTypeFound = Boolean.TRUE;
		if(priceTypeId != null && priceTypeId > 0){
			if(scRequestSupplierItem !=null 
					&& scRequestSupplierItem.getSupplierItemPrices() != null 
					&& scRequestSupplierItem.getSupplierItemPrices().size()>0){

				for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {
					// non zonal item price 
					if(scSupplierItemPrice.getPriceType() != null){
						if(scSupplierItemPrice.getPriceType().getParameterId().equals(priceTypeId)){
							scRequestSupplierItem.setSupplierItemPrice(scSupplierItemPrice);
							requestNoteDtl.setPriceType(scSupplierItemPrice.getPriceType());
							isPriceTypeFound = Boolean.FALSE;
							break;
						}
					}
				}
			} else{
				isPriceTypeFound = Boolean.FALSE;	
			}
		} else{
			requestNoteDtl.setPriceType(new PmParameter());
			scRequestSupplierItem.setSupplierItemPrice(null);
		}

		if(isPriceTypeFound){
			requestNoteDtl.setPriceType(new PmParameter());
			scRequestSupplierItem.setSupplierItemPrice(null);
		}

		// selected rec.supplier
		requestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);
		// procurement Type Category
		if(procTypeCatId != null && procTypeCatId > 0){
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterId(procTypeCatId);
			PmParameter procTypeCategory = getParameterSO().retrieveParameterById(parameterReqDTO).getPmParameter();
			requestNoteDtl.setProcurementTypeCategory(procTypeCategory);
			requestNoteDtl.setProcTypeCategoryId(procTypeCategory.getParameterId());
		}

		// prepared by
		if(assignedUserId != null && assignedUserId >0){
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(assignedUserId);
			UserResDTO resDTO = getUserSO().retrieveUserByUserId(reqDTO);
			requestNoteDtl.setAssignedUser(resDTO.getPmUser());	
		}
	}
	
	/**
	 * Price type change listener.
	 *
	 * @param priceTypeId the price type id
	 * @param requestNoteDtl the request note dtl
	 */
	public void priceTypeChangeListener(Long priceTypeId, ScRequestNoteDtl requestNoteDtl) {
		debugLogger("priceTypeChangeListener");
		// lov changes 
		ScRequestSupplierItem scRequestSupplierItem = requestNoteDtl.getScRequestSupplierItem();

		if(priceTypeId != null && priceTypeId > 0){
			if(scRequestSupplierItem !=null 
					&& scRequestSupplierItem.getSupplierItemPrices() != null 
					&& scRequestSupplierItem.getSupplierItemPrices().size()>0){

				for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {
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
		} else{
			requestNoteDtl.setPriceType(new PmParameter());
			scRequestSupplierItem.setSupplierItemPrice(null);
		}
		
		calculatePricePerItem(requestNoteDtl);
	}


	/**
	 * Before save post listener.
	 * gets active pricing to get price type and offered price in request note.
	 * before save request note.
	 *
	 * @param requestNoteDtl the request note dtl
	 */
	public void beforeSavePostListener(ScRequestNoteDtl requestNoteDtl) {
		debugLogger("beforeSavePostLister");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
		CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
 		
		//32520 - Start
		if(requestNoteDtl.getRequestItem() != null && requestNoteDtl.getScRequestSupplierItem() != null
				&& requestNoteDtl.getScRequestNote() != null){
				log.info("Bug 32520 : Method beforeSavePostLister ----> rnNo (" +requestNoteDtl.getScRequestNote().getRequestNoteNo() +") ");
				log.info("Bug 32520 : " + "rnDtlsId (" + requestNoteDtl.getRequestNoteDtlId() + ") " 
					     + "reqItemId (" + requestNoteDtl.getRequestItem().getRequestNoteItemId() + ") "
					     + "reqSuppItemId (" + requestNoteDtl.getScRequestSupplierItem().getRequestSupplierItemId() + ") "
					     + "----> rnNo (" + requestNoteDtl.getScRequestNote().getRequestNoteNo() + ")");
		}	
		//32520 - End
		
		if(requestNoteDtl.getvCmSupplierItem() != null && requestNoteDtl.getvCmSupplierItem().getDocId() >0) {
            
			catReqDTO.setSupplierItemId(requestNoteDtl.getvCmSupplierItem().getDocId());
			CatalogueResDTO catResDTO = catalogueSO.findActivePricing(catReqDTO);
			ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();

			List<CmPricing> cmPricings = catResDTO.getPriceLs();
			if(cmPricings != null && cmPricings.size() > 0){
				CmSupplierItem cmSupplierItem = cmPricings.get(0).getCmSupplierItem();
				cmSupplierItem.setCmPricings(cmPricings);

				SmSupplier smSupplier = cmSupplierItem.getSmSupplier();
				requestSupplierItem = createScRequestSupplierItem(cmSupplierItem, requestNoteDtl.getRequestItem(), cmSupplierItem.getSmSupplier(), cmPricings);
				
				if(requestSupplierItem.getSupplierName() == null && smSupplier != null) {
					requestSupplierItem.setSupplierName(smSupplier.getCompanyName());
					requestSupplierItem.setMofNumber(getMofNoBySupplierId(smSupplier.getSupplierId()));
				}
				
				requestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
				
				if(requestNoteDtl.getPriceType() != null && requestNoteDtl.getPriceType().getParameterId() != null){
					supplierListener(null, requestNoteDtl.getPriceType().getParameterId(), requestNoteDtl , 
							requestNoteDtl.getAssignedUser().getUserId(), requestNoteDtl.getProcTypeCategoryId());
				}
			}
		}
		calculatePricePerItem(requestNoteDtl);
	}
	/**
	 * After save post listener.
	 * gets active pricing to get price type and offered price in request note.
	 * After save request note.
	 *
	 * @param requestNoteDtl the request note dtl
	 */
	public void afterSavePostListener(ScRequestNoteDtl requestNoteDtl) {
		debugLogger("afterSavePostLister");
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();

		//32520 - Start
		if(requestNoteDtl.getRequestItem() != null && requestNoteDtl.getScRequestSupplierItem() != null
				&& requestNoteDtl.getScRequestNote() != null){
			log.info("Bug 32520 : Method afterSavePostLister ----> rnNo (" +requestNoteDtl.getScRequestNote().getRequestNoteNo() +") ");
			log.info("Bug 32520 : " + "rnDtlsId (" + requestNoteDtl.getRequestNoteDtlId() + ") " 
			         + "reqItemId (" + requestNoteDtl.getRequestItem().getRequestNoteItemId() + ") "
			         + "reqSuppItemId (" + requestNoteDtl.getScRequestSupplierItem().getRequestSupplierItemId() + ") "
			         + "----> rnNo (" + requestNoteDtl.getScRequestNote().getRequestNoteNo() + ")");
		}	
		//32520 - End
		
		if(requestNoteDtl.getScRequestSupplierItem() != null && requestNoteDtl.getScRequestSupplierItem().getRequestSupplierItemId() != null){
			ScRequestSupplierItem requestSupplierItem = requestNoteDtl.getScRequestSupplierItem();
			requestNoteReqDTO.setSupplierItemId(requestSupplierItem.getRequestSupplierItemId());
			RequestNoteResDTO requestNoteResTO =  getRequestNoteSO().getPricesBySupplierItemId(requestNoteReqDTO);
			if(requestNoteResTO.getSupplierItemPrices() != null && requestNoteResTO.getSupplierItemPrices().size()>0){
				requestSupplierItem.setSupplierItemPrices(requestNoteResTO.getSupplierItemPrices());
			}
			
			if(requestNoteDtl.getPriceType() != null && requestNoteDtl.getPriceType().getParameterId() != null){
				supplierListener(null, requestNoteDtl.getPriceType().getParameterId(), requestNoteDtl , 
						null, null);
			}
			
		}
		calculatePricePerItem(requestNoteDtl);
	}

 /**
  * selected assigned user to apply supplier recommendation list for the simple quote zone.
  *
  * @param selectedReqDtl the selected req dtl
  * @param assignedUserId the assigned user id
  */
	public void applyPrepareByForSupplierRecs(ScRequestNoteDtl selectedReqDtl  , Long assignedUserId){ 
		debugLogger("applyPrepareByForSupplierRecs");
		List<ScRequestNoteDtl>	requestNoteDtls	= new ArrayList<ScRequestNoteDtl>();
		if(getManagedBean().getSupplierRecList() != null && getManagedBean().getSupplierRecList().size() >0) {
			for(ScRequestNoteDtl requestNoteDtl : getManagedBean().getSupplierRecList()) {

				if(requestNoteDtl.getRequestItem().getRequestNoteItemId().equals(selectedReqDtl.getRequestItem().getRequestNoteItemId())){
					requestNoteDtl.setAssignedUser(getUserById(assignedUserId));
				}
				requestNoteDtls.add(requestNoteDtl);
			}
			getManagedBean().setSupplierRecList(requestNoteDtls);
		} 
	}

	/**
	 * selected recommended to apply supplier recommendation list for the simple quote zone.
	 *
	 * @param supplierId the supplier id
	 */
	public void applyRecSupplierForSupplierRecList(Long supplierId){ 
		debugLogger("applyRecSupplierForSupplierRecList");
		List<ScRequestNoteDtl>	requestNoteDtls	= new ArrayList<ScRequestNoteDtl>();
		List<Long> selectedRequestNoteDtlIds  = new ArrayList<Long>();
		if(getManagedBean().getSelectedRequestNoteDtls() != null && getManagedBean().getSelectedRequestNoteDtls().size() >0) {
			for (ScRequestNoteDtl selectedReqNoteDtl : getManagedBean().getSelectedRequestNoteDtls()) {
				selectedRequestNoteDtlIds.add(selectedReqNoteDtl.getRequestNoteDtlId());
			}

		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}

		if(getManagedBean().getSupplierRecList() != null && getManagedBean().getSupplierRecList().size() >0) {
			for(ScRequestNoteDtl requestNoteDtl : getManagedBean().getSupplierRecList()) {

				if(selectedRequestNoteDtlIds.contains(requestNoteDtl.getRequestNoteDtlId())){

					if(requestNoteDtl.getRequestItem() != null && requestNoteDtl.getRequestItem().getSupplierItems() !=null 
							&& requestNoteDtl.getRequestItem().getSupplierItems().size()>0){
						List<ScRequestSupplierItem> supplierItems = requestNoteDtl.getRequestItem().getSupplierItems();

						for (ScRequestSupplierItem scRequestSupplierItem : supplierItems) {

							if(scRequestSupplierItem.getSmSupplier().getSupplierId().equals(supplierId)){
								scRequestSupplierItem.setSupplierId(supplierId);
								requestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);

								if(scRequestSupplierItem.getSupplierItemPrices()!= null && scRequestSupplierItem.getSupplierItemPrices().size()>0){
									for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {

										// zonal item price  
										if(requestNoteDtl.getTplSpecZoneDtl() != null){
											if(requestNoteDtl.getTplSpecZoneDtl().getSpecZoneId().equals(scSupplierItemPrice.getTplSpecZone().getSpecZoneId())){
												scRequestSupplierItem.setSupplierItemPrice(scSupplierItemPrice);
												break;
											}
										}
									}
								}	
							}
						}
					}
				}
				requestNoteDtls.add(requestNoteDtl);
			}
			getManagedBean().setSupplierRecList(requestNoteDtls);
			getManagedBean().getSelectedRequestNoteDtls().clear();
		} 
	}
	
	/**
	 * Re recommend.
	 *
	 * @param scRequestNote the sc request note
	 */
	public void reRecommend(ScRequestNote scRequestNote){
		try {
			if(scRequestNote.getCurrentStatus() == null){
				// WorkFlow Status 
				updateWorkFlowAndRemarks(scRequestNote);
			}

		} finally {
		}

		boolean isZone = Boolean.FALSE;
		if(scRequestNote.getScRNZone() != null){
			isZone =  Boolean.TRUE;;
		}

		retrieveRequestNoteDetails(scRequestNote, DPStatusConstants.RN_PENDING_SUBMISSION, isZone);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_PAGE);
	}

	/**
	 * Get parameter code select items by parameter type for radio buttons.
	 * if govt. seller , enable G2G radio button only.  
	 * if not govt. seller  enable normal, ad Hoc and Emergency. 
	 * @param parameterType 			the parameter type
	 * @param g2gSeller the g2g seller
	 * @return array of SelectItem
	 */
	public SelectItem[] getRadioButtonCodeValues(String parameterType , boolean g2gSeller) {
		return getRadioButtonCodeValuesBySearchBean(parameterType, null , g2gSeller);
	}
	
	/**
	 * Gets the radio button code values by search bean.
	 *
	 * @param parameterType the parameter type
	 * @param searchBean the search bean
	 * @param g2gSeller the g2g seller
	 * @return the radio button code values by search bean
	 */
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
	
	/**
	 * Check price offered.
	 * show offered price based on selected price type.
	 * @param priceTypeCode the price type code
	 * @return true, if successful
	 */
	public boolean  checkPriceOffered(String priceTypeCode) {
		log.debug("checkPriceOffered");
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

	/**
	 * Select all.
	 * boolean check box to select all items in request note .
	 *
	 * @param event the event
	 */
	public void selectAll(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScRequestNoteDtl> scRequestNoteDtls = getManagedBean()
				.getRequestNoteDtls();
		if (scRequestNoteDtls != null) {
			for (ScRequestNoteDtl ScRequestNoteDtl : scRequestNoteDtls) {
				if (flag) {
					ScRequestNoteDtl.setSelected(true);
				} else {
					ScRequestNoteDtl.setSelected(false);
				}
			}
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_PAGE);
	}
	
	/**
	 * Select single row.
	 *
	 * @param event the event
	 */
	public void selectSingleRow(AjaxBehaviorEvent event) {
		boolean isSelectedALL= Boolean.TRUE;
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		if (flag) {
			setFlag(Boolean.FALSE);
		} 

		List<ScRequestNoteDtl> scRequestNoteDtls = getManagedBean()
				.getRequestNoteDtls();
		if (scRequestNoteDtls != null) {
			for (ScRequestNoteDtl ScRequestNoteDtl : scRequestNoteDtls) {
				if (!ScRequestNoteDtl.isSelected()) {
					isSelectedALL= Boolean.FALSE;
					break;
				} 
			}
			
			if(isSelectedALL){
				setFlag(isSelectedALL);
			}
		}
	}
	
	/**
	 * Cancel.
	 * cancel  - cancel the request note.
	 * @return the string
	 */
	public String cancel() {
		debugLogger("cancel");
		// clear request note data
		 super.renewSesionObject();
		return "catalogueSearch";
	}

	public void cancelPendingItemCodification() {
		
		ScRequestNote requestNote = getManagedBean().getRequestNote();

		log.info("cancelPendingItemCodification");
		FacesScopes facesScopes = new FacesScopes();
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		requestNoteReqDTO.setRoleCode(RoleCode.RN_APPROVER.value());
		requestNoteReqDTO.setPmUser(facesScopes.getUserSessionBean());
		requestNoteReqDTO.setOutcome(BpmOutcomeConst.BPM_CANCEL);
		requestNoteReqDTO.setUserLocale(facesScopes.getUserLocale());
		requestNoteReqDTO.setRequestNote(requestNote);
		requestNoteReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());

		RequestNoteResDTO requestNoteResDTO = getRequestNoteSO().cancelPendingItemCodification(requestNoteReqDTO, null);
		if (requestNoteResDTO.isSuccessful()) {
			ScRequestNote scRequestNote = requestNoteResDTO.getRequestNoteDetail();
			successMessage(MessageConst.INFO_700010, ParameterTypeConst.DOCUMENT_TYPES,
					ParameterConst.DOCTYPE_REQUEST_NOTE, scRequestNote.getRequestNoteNo(), Boolean.TRUE);
			super.renewSesionObject();
		} else {
			throw new EpException(MessageConst.ERROR_700020);
		}
	}

	@SuppressWarnings("unchecked")
	public void cancelPendingItemCodificationDPM() {
		log.info("cancelPendingItemCodificationDPM");
		
		FacesScopes facesScopes = new FacesScopes();
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		requestNoteReqDTO.setRequestNote(getManagedBean().getRequestNote());
		requestNoteReqDTO.setUserLocale(facesScopes.getUserLocale());
		List<String> loginIdAssignees = getRequestNoteSO().getSuppliersPendingAssignItemCode(requestNoteReqDTO);
		// #36119 - start
		IRequestNoteSO requestNoteSO = null;
		List<ScRequestNoteDtl> rndtls = null;
		// #36119 - end	
		//Start: #35492#58
		boolean audit = false;
		PurchaseRequestResDTO auditResponse = null;
		//End: #35492#58
		
		Object[] prObj = getManagedBean().getSelectedPrDtls();
		if (null != prObj) {
			log.info("Getting count for docNo: " + getManagedBean().getRequestNote() + " status: " + DPStatusConstants.RN_PENDING_ITEM_CODIFICATION + " docType : " + ParameterConst.DOCTYPE_REQUEST_NOTE);
		    ISearchBean<Object[]> prSearchBean =  new SearchBean<>();
		    prSearchBean.getDataFilterBeans().add(new SearchFilterBean("docNo", SearchOperator.OPERATOR_EQUALS.value(), prObj[1]));
		    prSearchBean.getDataFilterBeans().add(new SearchFilterBean("status", SearchOperator.OPERATOR_EQUALS.value(), DPStatusConstants.RN_PENDING_ITEM_CODIFICATION));
		    prSearchBean.getDataFilterBeans().add(new SearchFilterBean("docType", SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.DOCTYPE_REQUEST_NOTE));
		    SourcingIntegrationReqDTO searchReqDTO = new SourcingIntegrationReqDTO();
		    searchReqDTO.setSourcingDocSearchBean(prSearchBean);
		    ISourcingIntegrationSO sourcingIntegrationSO = (ISourcingIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		    
			int prSize = sourcingIntegrationSO.getCountOfActivePR(searchReqDTO);
			
			ScPurchaseRequest purchaseRequest = (ScPurchaseRequest) prObj[2];
			List<ScRequestNoteDtl> requestNoteDtls = (List<ScRequestNoteDtl>) prObj[6];
			IPurchaseRequestSO purchaseRequestSO = null;
			
			try {
				purchaseRequestSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IPurchaseRequestSO.class);
				
				requestNoteSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IRequestNoteSO.class);
				
				//#36119 - start
				if(!getManagedBean().isSourcingDpNewBpmVer()){
					cancelRN(prObj, prSize);
				}
				
				if (getManagedBean().getRequestNoteDtls().size() > 0 && null != purchaseRequestSO) {
					
					rndtls = getManagedBean().getRequestNoteDtls();
					PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
					reqDTO.setRequestNoteDtls(rndtls);
					PurchaseRequestResDTO response = purchaseRequestSO.updateCancelCodifyPR(reqDTO);
					auditResponse = response; //#35492#58
					
					if(getManagedBean().isSourcingDpNewBpmVer()){
						RequestNoteReqDTO rnReqDTO = new RequestNoteReqDTO();
						if(getManagedBean().getSupplierId() != null && getManagedBean().getRequestNote() != null){
							rnReqDTO.setRequestNoteNo(getManagedBean().getRequestNote().getRequestNoteNo());
							rnReqDTO.setSupplierId(getManagedBean().getSupplierId());
							requestNoteSO.cancelCodifyPRFromDPMaintenance(rnReqDTO);
							
							//tracking diary
							ScRequestNote requestNote = getManagedBean().getRequestNote();
							rnReqDTO.setRoleCode(RoleCode.RN_APPROVER.value());
							rnReqDTO.setPmUser(facesScopes.getUserSessionBean());
							rnReqDTO.setOutcome(BpmOutcomeConst.BPM_COMPLETE);
							rnReqDTO.setUserLocale(facesScopes.getUserLocale());
							requestNote.setRequestNoteDtls(rndtls);
							rnReqDTO.setRequestNote(requestNote);
							rnReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
							getRequestNoteSO().prepareTrackingDiaryDPM(rnReqDTO, getManagedBean().getSelectedPrDtls());
						}				
					}
					//#36119 - end
					if (response.isSuccessful()) {
						successMessage(MessageConst.INFO_776041, (String) prObj[1],
								(String) prObj[3], Boolean.TRUE);
						audit = true;//#35492#58
					} else {
						throw new EpException(MessageConst.ERROR_700020);
					}
					
					if (null != requestNoteSO) {
						RequestNoteReqDTO reqNoteDTO = new RequestNoteReqDTO();
						reqNoteDTO.setRequestNoteDtls(rndtls);//36119
						requestNoteSO.updateRequestNoteDtls(reqNoteDTO);
					}
				}
			} finally {
				purchaseRequestSO = null;
			}
			
			if (CollectionUtils.isNotEmpty(loginIdAssignees)) {
				getRequestNoteSO().sendRequestNoteNotification(requestNoteReqDTO, loginIdAssignees);
			} else {
				log.error(this.getClass().getName() + ".cancelPendingItemCodificationDPM(): receipeints not found to send a notification");
			}
			
			//START - #35492#58
			if (audit) {
				// /************ AUDIT #28895_35492 *************/
				IAuditDPRequestNoteSO auditDPRequestNoteSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IAuditDPRequestNoteSO.class);

				AuditDPRequestNoteBackingBeanTemplate auTemplate = new AuditDPRequestNoteBackingBean();
				if (auditDPRequestNoteSO.isAuditConfig(AuditDPRequestNoteConst.SC_DP_AU_RN_CONF)) {
					getManagedBean().setPurchaseRequestResDTO(auditResponse);
					auTemplate.auditSaveData(getManagedBean(), getUserIdSessionFromLoginId());//#35492#58
				}
				// /************ END AUDIT #28895_35492 *************//
			}
			//END - #35492#58
			
			facesScopes.removeFromSession("directPurchaseManagedBean");
			super.renewSesionObject();
		}
	}

	/**
	 * @param prObj
	 * @param prSize 
	 */
	@SuppressWarnings("unchecked")
	private void cancelRN(Object[] prObj, int prSize) {
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		
		FacesScopes facesScopes = new FacesScopes();
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		requestNoteReqDTO.setRoleCode(RoleCode.RN_APPROVER.value());
		requestNoteReqDTO.setPmUser(facesScopes.getUserSessionBean());
		requestNoteReqDTO.setOutcome(BpmOutcomeConst.BPM_COMPLETE);
		requestNoteReqDTO.setUserLocale(facesScopes.getUserLocale());
		requestNote.setRequestNoteDtls((List<ScRequestNoteDtl>) prObj[6]);
		requestNoteReqDTO.setRequestNote(requestNote);
		requestNoteReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		
		RequestNoteResDTO requestNoteResDTO = null;
		//28878 - Start
		if(prSize > 1){
			requestNoteReqDTO.setPurchaseRequestId(getManagedBean().getPurchaseRequestId());
			getManagedBean().setLoginIdAssignees(getRequestNoteSO().getloginIdAssigneeSuppByPrId(requestNoteReqDTO));
			requestNoteResDTO = getRequestNoteSO().cancelPendingItemCodificationMulti(requestNoteReqDTO, getManagedBean().getSelectedPrDtls(), getManagedBean().getLoginIdAssignees());
		}else if (prSize == 1) { // 28878 - End
			requestNoteResDTO = getRequestNoteSO().cancelPendingItemCodification(requestNoteReqDTO, getManagedBean().getSelectedPrDtls());
		} else {
			requestNoteResDTO = getRequestNoteSO().prepareTrackingDiaryDPM(requestNoteReqDTO, getManagedBean().getSelectedPrDtls());
		}
		
		if (!requestNoteResDTO.isSuccessful()) {
			throw new EpException(MessageConst.ERROR_700020);
		}
	}

	
	/**
	 * Cancel item detail popup.
	 */
	public void cancelItemDetailPopup() {
		debugLogger("cancelItemDetailPopup");
		ScRequestNoteDtl oriRequestNoteDtl = getManagedBean().getOriSequestNoteDtl();
		ScRequestNoteDtl selectedRequestNoteDtl = getManagedBean().getRequestNoteDtl();
		
		selectedRequestNoteDtl.getRequestItem().setQuantity(
				oriRequestNoteDtl.getRequestItem().getQuantity());
		selectedRequestNoteDtl.setPriceType(oriRequestNoteDtl.getPriceType());
		calculatePricePerItem(selectedRequestNoteDtl);
		
		RequestContext.getCurrentInstance().execute("itemDtlPopup.hide()");
		getManagedBean().setAutoUpdate(true);
	}
	
	/**
	 * Cancel apply all items popup.
	 */
	public void cancelApplyAllItemsPopup() {
		debugLogger("cancelApplyAllPopup");
		
		getManagedBean().setAssignedToUserId(null);
		getManagedBean().setProcTypeCategoryId(null);
		getManagedBean().setProcTitle("");
		getManagedBean().setPeriodValue(null);
		
		RequestContext.getCurrentInstance().execute("applyAllPopUp.hide();");
	}

	
	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	/**
	 * Supplier view action.
	 * Recommended Supplier icon in request note to view supplier details.
	 */
	public void supplierViewAction() {
		debugLogger("supplierViewAction");
		RequestContext.getCurrentInstance().execute("suppDetailsPopup.show();");
	}
	
	/**
	 * Gets the status desc list.
	 *
	 * @return the status desc list
	 */
	public List<PmStatusDesc> getStatusDescList() {
		List<PmStatusDesc> statusDescs = getManagedBean().getStatusDescs();
		
		if (statusDescs == null || statusDescs.size() == 0) {
			try {
				
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setModuleCode(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase());
				reqDTO.setDocType(ParameterConst.DOCTYPE_REQUEST_NOTE);
				reqDTO.setLanguageCode(languageCode);

				StatusResDTO resDTO = getStatusSO().retrieveStatusDescList(reqDTO);
				if(resDTO.isSuccessful()) {
					statusDescs = resDTO.getStatusDescList();
					
					getManagedBean().setStatusDescs(statusDescs);
				}
			} finally {
				//statusSO = null;
			}	
		}
		return statusDescs;
	}
	
	/**
	 * Download market research summary.
	 *
	 * @param scRequestNoteDtl the sc request note dtl
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadMarketResearchSummary(ScRequestNoteDtl scRequestNoteDtl) throws IOException {
		debugLogger("downloadMarketResearchSummary");
		
		ScRequestItem requestItem = scRequestNoteDtl.getRequestItem();
		ScRequestItemAttachment requestItemAttachment = requestItem.getScRequestItemAttachment();
		
		if (requestItemAttachment == null) {
			try {
				requestItemAttachment = new ScRequestItemAttachment();
				requestItemAttachment.setScRequestItem(requestItem);
				requestItemAttachment.setDocNo(requestItem.getRequestNote() !=null ?requestItem.getRequestNote().getRequestNoteNo(): "");
				requestItemAttachment.setPrefixFileName(getItemCode(requestItem));
				
				generateMarketSummaryPdfFromVelocity((ScAttachment)requestItemAttachment, 
						scRequestNoteDtl);
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			}
			
			requestItem.setScRequestItemAttachment(requestItemAttachment);
		}
		
		downloadFile(requestItemAttachment);	
	}
	
	/**
	 * Download purchase inq report.
	 *
	 * @param scRequestNoteDtl the sc request note dtl
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadPurchaseInqReport(ScRequestNoteDtl scRequestNoteDtl) throws IOException {
		debugLogger("downloadPurchaseInqReport");
		ScRequestSupplierItem piRequestSupplierItem = null;
		List<ScSupplierItemPrice> supplierItemPrices = null;
		ScRequestItem requestItem = scRequestNoteDtl.getRequestItem();
		ScPrInquiry scPrInquiry = scRequestNoteDtl.getPiRequestSupplierItem().getScPrInquiy();
		
		if(requestItem.getSupplierItems() !=null && requestItem.getSupplierItems().size()>0 ){
			for (ScRequestSupplierItem requestSupplierItem : requestItem.getSupplierItems()) {
				if(requestSupplierItem.getScPrInquiy() != null){
					piRequestSupplierItem = requestSupplierItem;
					break;
				}
			}
		}
		
		// Filter selected request supplier item and pricing
		supplierItemPrices = filterSupplierItemPricesByPriceType(piRequestSupplierItem, scPrInquiry);
		ScPIReportAttachment scPIReportAttachment = scRequestNoteDtl.getScPIReportAttachment();
		
		if (scPIReportAttachment == null) {
			try {
				scPIReportAttachment = new ScPIReportAttachment();
				scPIReportAttachment.setScRequestNoteDtl(scRequestNoteDtl);
				scPIReportAttachment.setDocNo(scRequestNoteDtl.getScRequestNote().getRequestNoteNo());
				scPIReportAttachment.setPrefixFileName(getItemCode(requestItem));
				
				generatePurchaseInqPdfFromVelocity((ScAttachment)scPIReportAttachment, piRequestSupplierItem, 
						supplierItemPrices);
				
				scRequestNoteDtl.setScPIReportAttachment(scPIReportAttachment);
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
			}
		}
		
		downloadFile(scPIReportAttachment);
	}
	
	/**
	 * Filter supplier item prices by price type.
	 *
	 * @param piRequestSupplierItem the pi request supplier item
	 * @param scPrIifnquiry the sc pr iifnquiry
	 * @return the list
	 */
	private List<ScSupplierItemPrice> filterSupplierItemPricesByPriceType(ScRequestSupplierItem piRequestSupplierItem, ScPrInquiry scPrIifnquiry ){
		List<ScSupplierItemPrice> supplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		for (ScSupplierItemPrice scSupplierItemPrice : piRequestSupplierItem.getSupplierItemPrices()) {
			if(scSupplierItemPrice.getPriceType() != null && scPrIifnquiry.getPriceType() != null){
				if(scSupplierItemPrice.getPriceType().getParameterId().equals(scPrIifnquiry.getPriceType().getParameterId())){
					supplierItemPrices.add(scSupplierItemPrice);
				}  
			}
		}
		return supplierItemPrices;
	}


	/**
	 * Pid validity.
	 *
	 * @param purchaseInDefault the purchase in default
	 * @param scRequestNote the sc request note
	 */
	private void pidValidity(ScPurchaseInDefault purchaseInDefault, ScRequestNote scRequestNote) {
		Date today  = DateUtils.getCurrentDateWithoutTime();
		Date ExpDate = DateUtils.addDays(purchaseInDefault.getChangedDate(), purchaseInDefault.getPidValidDays());
		if(ExpDate.before(today)){

			throw new EpException(
					MessageConst.ERROR_760355,
					new String[] {purchaseInDefault.getPurchaseInDefaultNo()});
		}  
	}
	
	/**
	 * Pid validity.
	 *
	 * @param purchaseInDefault the purchase in default
	 * @param scRequestNote the sc request note
	 */
	private boolean pidValidityBoolean(ScPurchaseInDefault purchaseInDefault, ScRequestNote scRequestNote) {
		Date today  = DateUtils.getCurrentDateWithoutTime();
		Date ExpDate = DateUtils.addDays(purchaseInDefault.getChangedDate(), purchaseInDefault.getPidValidDays());
		boolean valid = true;
		if(ExpDate.before(today)){
			valid = false;
			getManagedBean().getRequestNoteStatus().setStatusId(DPStatusConstants.PID_EXPIRED);
			//throw new EpException(
			//		MessageConst.ERROR_760355,
			//		new String[] {purchaseInDefault.getPurchaseInDefaultNo()});
		}
		return valid;
	}


	/**
	 * Gets the item code.
	 *
	 * @param scRequestItem the sc request item
	 * @return the item code
	 */
	private String getItemCode(ScRequestItem scRequestItem) {
		CmItem cmItem = scRequestItem.getCmItem();
		
		if (cmItem != null) {
			return cmItem.getExtensionCode();
		} else {
			return scRequestItem.getTempUnspscCode();
		}
	}

	
	
	/**
	 * Proc type categories.
	 *
	 * @return the list
	 */
	public List<PmParameterDesc> getProcTypeCategories(){
		List<PmParameterDesc> procTypeCategories = ParameterHelper.getInstance().getParameterDescList(ParameterTypeConst.PROCUREMENT_TYPE_CATEGORY);
	   return procTypeCategories;	
	}


	/**
	 * Gets the delivery term type.
	 *
	 * @return the delivery term type
	 */
	private PmParameter getDeliveryTermType(){
		PmParameter deliveryTermType = getParameterByTypeAndCode(
				ParameterTypeConst.DELIVERY_TERM, ParameterConst.SQ_DURATION_DAY);
				return deliveryTermType;
	}

	/**
	 * Generate market summary pdf from velocity.
	 *
	 * @param attachment the attachment
	 * @param scRequestNoteDtl the sc request note dtl
	 * @throws Exception the exception
	 */
	private void generateMarketSummaryPdfFromVelocity(ScAttachment attachment, ScRequestNoteDtl scRequestNoteDtl) throws Exception {
		ScRequestItem scRequestItem = scRequestNoteDtl.getRequestItem();
		attachment.setFileName("market_summary.pdf");
		populateAttachmentDetails(attachment);
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		String imgUrl = getServerUrl()+ "NGeP-portlet/css/images/gov.png";
		
		String rightImgUrl = getServerUrl()+ "NGeP-portlet/css/images/eperolehan-footer.png";
		
		VelocityContext context = new VelocityContext();
		context.put("productCode", scRequestItem.getCmItem() !=null ? scRequestItem.getCmItem().getExtensionCode() : "");
		if(scRequestItem.getCmItem() !=null){
			context.put("productCode", scRequestItem.getCmItem().getExtensionCode());
			context.put("itemName", scRequestItem.getCmItem().getItemName());
		}else if(scRequestItem.getItemName() !=null){
			context.put("productCode", "");
			context.put("itemName", scRequestItem.getItemName());
		}else{
			context.put("productCode", "");
			context.put("itemName", "");
		}
		context.put("imgUrl", imgUrl);
		context.put("rightImgUrl", rightImgUrl);
		context.put("desc", scRequestItem.getItemDesc() != null ? scRequestItem.getItemDesc() : "" );
		context.put("uom", scRequestItem.getUom() != null ? scRequestItem.getUom() .getUomName() : "");
		List<MarketSummaryAndPIBean> requestNoteVOList = new ArrayList<MarketSummaryAndPIBean>();
		MarketSummaryAndPIBean marketSummaryAndPIBean = null;
		ScSupplierItemPrice supplierItemPrice= null;
		List<ScRequestSupplierItem> supplierItems = null;
		
		if (!scRequestItem.isBrandNew()) {
			try {				
				RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
				requestNoteReqDTO.setRequestItemId(scRequestItem.getRequestNoteItemId());
				RequestNoteResDTO requestNoteResDTO = getRequestNoteSO().getSuppItemWithPricesByReqItemId(requestNoteReqDTO);
				supplierItems = requestNoteResDTO.getSupplierItems();
				if (supplierItems != null) {
					for(ScRequestSupplierItem supplierItem: supplierItems){			
						for(int i = 0; i<supplierItem.getSupplierItemPrices().size();i++){
							supplierItemPrice = supplierItem.getSupplierItemPrices().get(i);
							marketSummaryAndPIBean = new MarketSummaryAndPIBean();
							marketSummaryAndPIBean.setSupplierId(supplierItem.getSmSupplier().getSupplierId());
							marketSummaryAndPIBean.setSupplierName(supplierItem.getSmSupplier().getCompanyName().replaceAll("&", "&#38;"));
							marketSummaryAndPIBean.setMinQty(scRequestItem.getMinqty() != null ? scRequestItem.getMinqty().toString() :"T/B");
							marketSummaryAndPIBean.setDeliveryTerm(supplierItemPrice.getPricing() !=null && supplierItemPrice.getPricing().getCmSupplierItem().getLeadTime() != null ? supplierItemPrice.getPricing().getCmSupplierItem().getLeadTime().toString() : "T/B");
							marketSummaryAndPIBean.setAddItemDesc(supplierItemPrice.getPricing() !=null && supplierItemPrice.getPricing().getCmSupplierItem().getItemDesc() != null ? supplierItemPrice.getPricing().getCmSupplierItem().getItemDesc().replaceAll("&", "&#38;") : "T/B");
							marketSummaryAndPIBean.setFrQty(supplierItemPrice.getFromQty() != null ? supplierItemPrice.getFromQty().toString() : "T/B");
							marketSummaryAndPIBean.setToQty(supplierItemPrice.getToQty() != null ? supplierItemPrice.getToQty().toString(): "T/B");
							marketSummaryAndPIBean.setPriceType(supplierItemPrice.getPriceType() != null ? getParameterDesc(supplierItemPrice.getPriceType()) : "T/B");
							marketSummaryAndPIBean.setUnitPrice(supplierItemPrice.getItemPrice() !=null ? amountDF.format(supplierItemPrice.getItemPrice()).toString() : "T/B");			    
							marketSummaryAndPIBean.setZone(supplierItemPrice.getTplSpecZone() !=null ? supplierItemPrice.getTplSpecZone().getZoneName().replaceAll("&", "&#38;") : "T/B");
							requestNoteVOList.add(marketSummaryAndPIBean);
						}
					}
				}
				
			} finally {
				//directPurchaseSO = null;
			}
		} else {
			CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();
			//construct search bean
			String conditionField = scRequestNoteDtl.getConditionField();
			
			SearchBean<VCmSupplierItem> searchBean = new SearchBean<VCmSupplierItem>();
			
			for (String filter: conditionField.split("\\|")){
				 log.debug(filter);
		         String searchField = null;
		         String searchValue = null;
		         int searchCondition = SearchOperator.OPERATOR_EQUALS.value();
		         String[] conditions = filter.split("\\^");
		         searchField = conditions[0];
	        	 searchValue = conditions[1];
		         if(conditions.length > 2) {
		        	 searchCondition = Integer.parseInt(conditions[2]);
		         }
		         SearchFilterBean filterBean = new SearchFilterBean(searchCondition);
		         filterBean.setCaseInsensitive(true);
		         filterBean.setSearchField(searchField);
		         filterBean.setSearchValue(searchValue);
		         searchBean.getDataFilterBeans().add(
		        		 filterBean);
		         log.debug(searchField + "|" + searchValue + "|" + searchCondition);
		    }
			//catalogueReqDTO.setSupplierItemId(scRequestNoteDtl.getvCmSupplierItem());
			IViewSupplierItemSO viewSupplierItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, IViewSupplierItemSO.class);
			//catalogueReqDTO.setExtensionCode(scRequestItem.getCmItem().getExtensionCode());
			//catalogueReqDTO.setUomId(scRequestItem.getUom().getUomId());
			catalogueReqDTO.setViewSearchBean(searchBean);
			CatalogueResDTO  catalogueResDTO = viewSupplierItemSO.getSuppliersItemPricingByExtensionCode(catalogueReqDTO);
			List<CmSupplierItem> cmSupplierItemList = catalogueResDTO.getSupplierItemList();

			for(CmSupplierItem supplierItem: cmSupplierItemList){			
				for(int i = 0; i<supplierItem.getItemPricings().size();i++){
					CmPricing cmPrice = supplierItem.getItemPricings().get(i);
					marketSummaryAndPIBean = new MarketSummaryAndPIBean();
					marketSummaryAndPIBean.setSupplierId(cmPrice.getCmSupplierItem().getSmSupplier().getSupplierId());
					marketSummaryAndPIBean.setSupplierName(cmPrice.getCmSupplierItem().getSmSupplier().getCompanyName().replaceAll("&", "&#38;"));
					marketSummaryAndPIBean.setMinQty(scRequestItem.getMinqty() != null ? scRequestItem.getMinqty().toString() :"T/B");
					marketSummaryAndPIBean.setDeliveryTerm(cmPrice.getCmSupplierItem().getLeadTime() != null ? cmPrice.getCmSupplierItem().getLeadTime().toString() : "T/B");
					marketSummaryAndPIBean.setAddItemDesc(cmPrice.getCmSupplierItem().getItemDesc() != null ? cmPrice.getCmSupplierItem().getItemDesc().replaceAll("&", "&#38;") : "T/B");
					marketSummaryAndPIBean.setFrQty(cmPrice.getFromQty() != null ? cmPrice.getFromQty().toString() : "T/B");
					marketSummaryAndPIBean.setToQty(cmPrice.getToQty() != null ? cmPrice.getToQty().toString() : "T/B");
					marketSummaryAndPIBean.setPriceType(cmPrice.getPricingType() != null ? getParameterDesc(
							ParameterTypeConst.PRICE_TYPE, cmPrice.getPricingType()) : "T/B");
					marketSummaryAndPIBean.setUnitPrice(cmPrice.getPrice() !=null ? amountDF.format(cmPrice.getPrice()).toString() : "T/B");			    
					marketSummaryAndPIBean.setZone((cmPrice.getCmSupplierZone() !=null && cmPrice.getCmSupplierZone().getSuppZoneId() > 0) ? cmPrice.getCmSupplierZone().getSuppZoneName().replaceAll("&", "&#38;") : "T/B");
					requestNoteVOList.add(marketSummaryAndPIBean);
				}
			}
		}
			
		sortMarketSummaryList(requestNoteVOList);
		context.put("requestNoteVOList", requestNoteVOList);		
    	
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
	    			"/sc/directPurchase/market-summary.vm", 
	    			context, 
	    			fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Generate purchase inq pdf from velocity.
	 *
	 * @param attachment the attachment
	 * @param scSupplierItemPrices the sc supplier item prices
	 * @throws Exception the exception
	 */
	private void generatePurchaseInqPdfFromVelocity(ScAttachment attachment,ScRequestSupplierItem supplierItem, 
			List<ScSupplierItemPrice> scSupplierItemPrices) throws Exception {
		attachment.setFileName("pi_report.pdf");
		populateAttachmentDetails(attachment);
		List<ScRequestNoteDtl> scRequestNoteDtl = getManagedBean().getRequestNoteDtls();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		String imgUrl = getServerUrl()+ "NGeP-portlet/css/images/gov.png";
		String rightImgUrl = getServerUrl()+ "NGeP-portlet/css/images/eperolehan-footer.png";
		
		VelocityContext context = new VelocityContext();
		context.put("imgUrl", imgUrl);
		context.put("rightImgUrl", rightImgUrl);
		List<MarketSummaryAndPIBean> requestNoteVOList = new ArrayList<MarketSummaryAndPIBean>();
		MarketSummaryAndPIBean marketSummaryAndPIBean = null;
		ScSupplierItemPrice supplierItemPrice= null;
			for(int i = 0; i< scSupplierItemPrices.size();i++){
				supplierItemPrice = scSupplierItemPrices.get(i);
				marketSummaryAndPIBean = new MarketSummaryAndPIBean();
				if(i==0){
					context.put("productCode", supplierItem.getRequestNoteItem().getCmItem().getExtensionCode());
					context.put("itemName", supplierItem.getRequestNoteItem().getCmItem().getItemName());
					context.put("desc", supplierItem.getRequestNoteItem().getItemDesc() != null ? supplierItem.getRequestNoteItem().getItemDesc() : "T/B" );
					context.put("uom", supplierItem.getRequestNoteItem().getUom() != null ? supplierItem.getRequestNoteItem().getUom().getUomName() : "T/B");
					context.put("priceType", supplierItemPrice.getPriceType() != null ? getParameterDesc(supplierItemPrice.getPriceType()) : "T/B");
					context.put("supplierName", supplierItem.getSmSupplier().getCompanyName().replaceAll("&", "&#38;"));
					if(supplierItem.getScPrInquiy().getIsStockAvailable() != null){
						context.put("stockAvailable", supplierItem.getScPrInquiy().getIsStockAvailable() == 0 ? "Tidak" : "Ya");						
					}
					else{
						context.put("stockAvailable", "T/B");
					}
				}
				marketSummaryAndPIBean.setSupplierId(supplierItem.getSmSupplier().getSupplierId());
				marketSummaryAndPIBean.setSupplierName(supplierItem.getSmSupplier().getCompanyName().replaceAll("&", "&#38;"));
				marketSummaryAndPIBean.setMinQty(supplierItem.getRequestNoteItem().getMinqty() != null ? supplierItem.getRequestNoteItem().getMinqty().toString() :"T/B");
				marketSummaryAndPIBean.setDeliveryTerm(scRequestNoteDtl.get(0).getDeliverTerm() != null ? scRequestNoteDtl.get(0).getDeliverTerm().toString() : "T/B");
				marketSummaryAndPIBean.setAddItemDesc(supplierItemPrice.getPricing().getCmSupplierItem().getItemDesc() != null ? supplierItemPrice.getPricing().getCmSupplierItem().getItemDesc().replaceAll("&", "&#38;") : "T/B");
				marketSummaryAndPIBean.setFrQty(supplierItemPrice.getFromQty() != null ? supplierItemPrice.getFromQty().toString() : "T/B");
				marketSummaryAndPIBean.setToQty(supplierItemPrice.getToQty() != null ? supplierItemPrice.getToQty().toString(): "T/B");
				marketSummaryAndPIBean.setPriceType(supplierItemPrice.getPriceType() != null ? getParameterDesc(supplierItemPrice.getPriceType()) : "T/B");
				marketSummaryAndPIBean.setCataloguePrice(supplierItemPrice.getPricing().getPrice() != null ? amountDF.format(supplierItemPrice.getPricing().getPrice()).toString() : "T/B");
				marketSummaryAndPIBean.setUnitPrice(supplierItemPrice.getItemPrice() !=null ? amountDF.format(supplierItemPrice.getItemPrice()).toString() : "T/B");			    
				marketSummaryAndPIBean.setZone(supplierItemPrice.getTplSpecZone() !=null ? supplierItemPrice.getTplSpecZone().getZoneName().replaceAll("&", "&#38;") : "T/B");
				if(supplierItem.getScPrInquiy().getIsStockAvailable() != null){
				   marketSummaryAndPIBean.setStockAvailability(supplierItem.getScPrInquiy().getIsStockAvailable() == 0 ? "Tidak" : "Ya");
				}else {
				   marketSummaryAndPIBean.setStockAvailability("T/B");
				}
				requestNoteVOList.add(marketSummaryAndPIBean);
			}
		sortMarketSummaryList(requestNoteVOList);
		context.put("requestNoteVOList", requestNoteVOList);		
    	
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
	    			"/sc/directPurchase/pi-report.vm", 
	    			context, 
	    			fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * The Class SupplierPK.
	 */
	private class SupplierPK {
		
		/** The sm supplier. */
		private SmSupplier smSupplier;
		
		/**
		 * Instantiates a new supplier pk.
		 *
		 * @param smSupplier the sm supplier
		 */
		public SupplierPK(SmSupplier smSupplier) {
			this.smSupplier = smSupplier;
		}
		
		/**
		 * Gets the sm supplier.
		 *
		 * @return the smSupplier
		 */
		public SmSupplier getSmSupplier() {
			return smSupplier;
		}

		/**
		 * Sets the sm supplier.
		 *
		 * @param smSupplier the smSupplier to set
		 */
		public void setSmSupplier(SmSupplier smSupplier) {
			this.smSupplier = smSupplier;
		}

		/**
		 * Equals.
		 *
		 * @param other the another SmSupplier instance
		 * @return true, if successful
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
		 * Hash code.
		 *
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
	
	/**
	 * Sort market summary list.
	 *
	 * @param marketSummaryList the market summary list
	 */
	public void sortMarketSummaryList(List<MarketSummaryAndPIBean> marketSummaryList) {
        Comparator<MarketSummaryAndPIBean> comparator = new Comparator<MarketSummaryAndPIBean>() {
            @Override
            public int compare(MarketSummaryAndPIBean o1, MarketSummaryAndPIBean o2) {
                return new CompareToBuilder()
	                .append(o1.getSupplierId(), o2.getSupplierId())
	                .append(o1.getPriceType(), o2.getPriceType())
	                .append(o1.getZone(), o2.getZone())
	                .append(o1.getFrQty(), o2.getFrQty())
	                .append(o1.getToQty(), o2.getToQty())
	                .toComparison();
            }
        };
        Collections.sort(marketSummaryList, comparator);
    }

	/**
	 * Show recent price.
	 *
	 * @param cmSupplierItem the cm supplier item
	 */
	public void showRecentPrice(CmSupplierItem cmSupplierItem, VCmSupplierItem vCmSupplierItem) {
		debugLogger("showRecentPrice");
		if(cmSupplierItem != null){
			getManagedBean().setCmSupplierItem(cmSupplierItem);
		}
		if(vCmSupplierItem != null){
			getManagedBean().setvCmSupplierItem(vCmSupplierItem);
		}
		getManagedBean().setRecentPriceDuration(ParameterConst.RECENT_PRICE_DURATION);
	}
	
	
	/**
	 * View recent price.
	 * to view recent price report in search catalogue  - comapare screen page
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void viewRecentPrice() throws IOException {
		debugLogger("viewRecentPrice");
		long itemId = 0 ;
		long cmSuppItemId = 0;
		long uomId = 0;
		FacesScopes facesScopes = new FacesScopes();
		if (getManagedBean().getCmSupplierItem() != null) {
			CmSupplierItem cmSupplierItem = getManagedBean().getCmSupplierItem();
			itemId = cmSupplierItem.getCmItem().getItemId();
			cmSuppItemId = cmSupplierItem.getSupplierItemId();
			uomId = cmSupplierItem.getPmUom().getUomId();
		}
		if (getManagedBean().getvCmSupplierItem() != null) {
			VCmSupplierItem vCmSupplierItem = getManagedBean().getvCmSupplierItem();
			itemId = vCmSupplierItem.getItemId();
			cmSuppItemId = vCmSupplierItem.getDocId();
			uomId = vCmSupplierItem.getUomId();
		}

		String noDays = this.getManagedBean().getRecentPriceDuration();
		String reportName = ReportConst.SC_RECENT_PRICE_RPT_JSP;

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_KEY_ITEM_ID, itemId + "");
		paramMap.put(ReportConst.PARAM_KEY_CM_SUPPLIER_ITEM_ID, cmSuppItemId + "");
		paramMap.put(ReportConst.PARAM_KEY_UOM_ID, uomId + "");
		paramMap.put(ReportConst.PARAM_KEY_NO_OF_DAYS, noDays + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, ReportConst.SC_S1_RCNT_PRICE_RPT_VAL);

		generateReport(reportName, paramMap);
	}

	/**
	 * View market research.
	 * to view market report in request note page.
	 * @param requestNoteDtl the request note dtl
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void viewMarketResearch(ScRequestNoteDtl requestNoteDtl) throws IOException {
		debugLogger("viewMarketResearch");
		
		if (requestNoteDtl != null) {
			FacesScopes facesScopes = new FacesScopes();	
			Map<String, String> paramMap = new HashMap<String, String>();
			String reportName = ReportConst.SC_MARKET_RESEARCH_RPT_JSP;
			
			paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
			paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, ReportConst.SC_ITEM_PRICING_SUM_RPT_VAL);
			
			if(requestNoteDtl.isBrandNew()) {
				ScRequestItem requestItem = requestNoteDtl.getRequestItem();
				String itemCode = StringUtils.EMPTY;
				long uomId = 0;
				if(requestItem != null && requestItem.getCmItem() != null) {
					itemCode = requestItem.getCmItem().getExtensionCode();
					uomId = requestItem.getUom().getUomId();
				}
				
				String conditionField = requestNoteDtl.getConditionField();
				
				for (String filter: conditionField.split("\\|")){
			         String searchField = null;
			         String searchValue = null;
			         int searchCondition = SearchOperator.OPERATOR_EQUALS.value();
			         String[] conditions = filter.split("\\^");
			         searchField = conditions[0];
		        	 searchValue = conditions[1];
			         if(conditions.length > 2) {
			        	 searchCondition = Integer.parseInt(conditions[2]);
			         }
			         if ("ITEM_NAME".equals(searchField)) {
			        	 paramMap.put(ReportConst.PARAM_ITEM_NAME, searchValue);	
			         } else if ("COMPANY_NAME".equals(searchField)) {
			        	 paramMap.put(ReportConst.PARAM_SUPPLIER_NAME, searchValue);	
			         } else if ("EP_NO".equals(searchField)) {
			        	 paramMap.put(ReportConst.PARAM_EP_NO, searchValue);	
			         } else if ("BIZ_REG_NO".equals(searchField)) {
			        	 paramMap.put(ReportConst.PARAM_BIZ_REG_NO, searchValue);	
			         } else if ("STATE_ID".equals(searchField)) {
			        	 paramMap.put(ReportConst.PARAM_STATE_ID, searchValue);	

			         } else if ("DISTRICT_ID".equals(searchField)) {
			        	 paramMap.put(ReportConst.PARAM_DISTRICT_ID, searchValue);	
			         } else if ("MOF_NO".equals(searchField)) {
			        	 paramMap.put(ReportConst.PARAM_MOF_NO, searchValue);	
			         } else if ("DOC_ID".equals(searchField)) {				
			        	 paramMap.put(ReportConst.PARAM_SUPP_ITEM_ID, searchValue);	
			         }	
			         paramMap.put(ReportConst.PARAM_ITEM_CODE, itemCode);	
			         BigDecimal quantity = BigDecimal.ZERO;
			         if(requestNoteDtl.getRequestItem().getQuantity() != null) {
			        	 quantity = requestNoteDtl.getRequestItem().getQuantity();
			         }
			         paramMap.put(ReportConst.PARAM_QUANTITY, quantity.toString());	
			         paramMap.put(ReportConst.PARAM_UOM_ID, uomId + "");	
			         log.debug(searchField + "|" + searchValue + "|" + searchCondition);
			    }
				
				paramMap.put(ReportConst.PARAM_IS_SAVED_FLAG, 0 + "");
				
			}
			else {
				paramMap.put(ReportConst.PARAM_REQUEST_ITEM_ID, requestNoteDtl.getRequestItem().getRequestNoteItemId() + "");
				paramMap.put(ReportConst.PARAM_IS_SAVED_FLAG, 1 + "");
			}
			
			generateReport(reportName, paramMap);
		}
	}
	
	/**
	 * Gets the parameter so.
	 *
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}
	
	/**
	 * Gets the user so.
	 *
	 * @return the user so
	 */
	private IUserSO getUserSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	}
	
	/**
	 * Gets the status so.
	 *
	 * @return the status so
	 */
	private IStatusSO getStatusSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
	}
	
	/**
	 * Gets the request note so.
	 *
	 * @return the request note so
	 */
	private IRequestNoteSO getRequestNoteSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
	}
	
	/**
	 * Gets the template spec so.
	 *
	 * @return the template spec so
	 */
	private ITemplateSpecSO getTemplateSpecSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
	}
	
	/**
	 * Gets the request item so.
	 *
	 * @return the request item so
	 */
	private IRequestItemSO getRequestItemSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestItemSO.class);
	}
	
	//#27545
	/**
	 * Gets the org profile so.
	 *
	 * @return the org profile so
	 */
	private IOrgProfileSO getOrgProfileSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
	}
	
	/**
	 * Gets the server url.
	 *
	 * @return the server url
	 */
	private String getServerUrl() {
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		String requestURL = httpServletRequest.getRequestURL().toString();
		String url = requestURL.substring(0, requestURL.length()- httpServletRequest.getRequestURI().length())+ httpServletRequest.getContextPath() + "/";

		return url;
	}

	/**
	 * Checks if is flag.
	 *
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * Sets the flag.
	 *
	 * @param flag the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 * Method get approver group and approver List.
	 */
	public void initializeApproverGroup() {
		RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
		RequestNoteResDTO resDTO = new RequestNoteResDTO();
		try {
			ScRequestNote requestNote = getManagedBean().getRequestNote();
			if(requestNote != null && requestNote.getPmOrgProfile() != null){
				Long orgProfileId = requestNote.getPmOrgProfile().getOrgProfileId();
				reqDTO.setOrgProfileId(orgProfileId);
				resDTO = getRequestNoteSO().retrieveGroupListByOrgId(reqDTO);
				if(resDTO.getUserGroupList().size()>0) {
					getManagedBean().setUserGroupList(resDTO.getUserGroupList());
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_745070, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}
				if(requestNote.getPmUserGroup() != null){
					getManagedBean().setUserGroupId(requestNote.getPmUserGroup().getUserGroupId());
				}
				if(requestNote.getApprover() != null){
					getManagedBean().setSelectedApproverId(requestNote.getApprover().getUserId());
				}
				userGroupChangeListener();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
		}
	}
	
	/**
	 * Gets the user group dropdown.
	 *
	 * @return the user group dropdown
	 */
	public SelectItem[] getUserGroupDropdown() {
		SelectItem[] items = null;
		
		if(!CollectionUtils.isEmpty(getManagedBean().getUserGroupList())) {
			items = new SelectItem[getManagedBean().getUserGroupList().size()];
			int i = 0;
			for(PmUserGroup userGroup : getManagedBean().getUserGroupList()) {
				items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName().replaceAll("\\s+", " ").trim());
			}
		} else {
			items = new SelectItem[0];
		}
		
	    return items;
	}
	
	/**
	 * User group change listener.
	 */
	public void userGroupChangeListener() {
		debugLogger("userGroupChangeListener");
		RequestNoteResDTO resDTO = null;

		ScRequestNote requestNote = getManagedBean().getRequestNote();
		Long userGroupId = getManagedBean().getUserGroupId();
		List<PmUser> approvers  = new ArrayList<PmUser>();
		
		if(userGroupId != null){
			if(getManagedBean().getUserGroupList() != null) {
				for (PmUserGroup pmUserGroup : getManagedBean().getUserGroupList()) {
					if(userGroupId.equals(pmUserGroup.getUserGroupId())){
						requestNote.setPmUserGroup(pmUserGroup);
					}
				}
			}
			RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
			reqDTO.setGroupId(userGroupId);
			reqDTO.setOrgProfileId(requestNote.getPmOrgProfile().getOrgProfileId());
			resDTO = getRequestNoteSO().retrieveApproverListByGroupId(reqDTO);
			List<PmUserGroupUser> groupUsers = resDTO.getApproverGroupUserList();

			if(!CollectionUtils.isEmpty(groupUsers)) {
				for(PmUserGroupUser groupUser : groupUsers) {
					approvers.add(groupUser.getPmUser());
				}
			}
			
		} else{
			requestNote.setPmUserGroup(null);
			requestNote.setApprover(null);
			
		}
		
		getManagedBean().setApproverList(approvers);
	}

	/**
	 * Gets the purchase in default expired message.
	 *
	 * @param statusId the status id
	 * @return the purchase in default expired message
	 */
	private void getPurchaseInDefaultExpiredMessage(Long statusId){
		if(DPStatusConstants.PID_EXPIRED.equals(statusId)){
			Locale locale = new FacesScopes().getUserLocale();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils
							.getMessage(
									ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default.exp.msg",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale),
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
		}
	}
	
	/**
	 * Gets the purchase in default expired message.
	 *
	 * @param statusId the status id
	 * @return the purchase in default expired message
	 */
	private void getPurchaseInDefaultExpiredMessageCodification(Long statusId){
		if(DPStatusConstants.PID_EXPIRED.equals(statusId)){
			Locale locale = new FacesScopes().getUserLocale();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils
							.getMessage(
									ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default.exp.msg2",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale),
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
		}
	}
	
	/**
	 * Update work flow and remarks.
	 *
	 * @param requestNote the request note
	 */
	private void updateWorkFlowAndRemarks(ScRequestNote requestNote){
		// remarks and workflow status
		if(requestNote.isBrandNew()){
			ScRNWorkFlowStatus scWorkFlowStatus = null;
			List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
			scWorkFlowStatus = new ScRNWorkFlowStatus();
			PmStatus currentStatus = getPmStatusByStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
			getCurrentStatus(currentStatus);
			scWorkFlowStatus.setCurrent(Boolean.TRUE);
			scWorkFlowStatus.setPmStatus(currentStatus);
			scWorkFlowStatus.setRequestNote(requestNote);
			workFlowStatuses.add(scWorkFlowStatus);
			requestNote.setWorkFlowStatuses(workFlowStatuses);
			// To avoid workflow status reset to null when got error during direct submit without save.
			List<ScRequestNoteRemark> remarks = requestNote.getRemarks();
			if (CollectionUtils.isNotEmpty(remarks)) {
				for (ScRequestNoteRemark remark : remarks) {
					remark.setScWorkFlowStatus(scWorkFlowStatus);
				}
			}
		}	
	}
	
	/**
	 * Apply all action.
	 */
	public void applyAllAction(){
		boolean isFullFillMentTypePeriodic = Boolean.FALSE;
		boolean isFullFillMentTypeOneOff = Boolean.FALSE;
		List<ScRequestNoteDtl>	selectedRequestNoteDtls	= new ArrayList<ScRequestNoteDtl>();

		if (CollectionUtils.isNotEmpty(getManagedBean().getRequestNoteDtls())) {
		
			for(ScRequestNoteDtl selectedReqDtl : getManagedBean().getRequestNoteDtls()) {
				if(selectedReqDtl.isSelected()){
					
					if (selectedReqDtl.getFulfilmentType().getParameterCode().
							equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
						isFullFillMentTypeOneOff = Boolean.TRUE;
					} if (selectedReqDtl.getFulfilmentType().getParameterCode().
							equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
						isFullFillMentTypePeriodic = Boolean.TRUE;
					}
					selectedRequestNoteDtls.add(selectedReqDtl);
				}
			}
		}
		
		// disabled get preparedBy for both fullfillment types
		if(isFullFillMentTypeOneOff && isFullFillMentTypePeriodic){
			getManagedBean().setFulFillmentType(Boolean.TRUE);
		} else{
			if (CollectionUtils.isNotEmpty(selectedRequestNoteDtls)) {
				getPreparedBy(selectedRequestNoteDtls.get(0));
			}
			getManagedBean().setFulFillmentType(Boolean.FALSE);
		}
	}
	
	/**
	 * Generate key according to itemId and uomId for requestItem
	 */
	private String generateItemUomKey(ScRequestItem requestItem) {
		StringBuilder sb = new StringBuilder();
		sb.append("I").
		append(requestItem.getCmItem().getItemId()).
		append("U").
		append(requestItem.getUom().getUomId());
		
		return sb.toString();
	}
	
	/**
	 * Calculate price per item
	 */
	public BigDecimal calculatePricePerItem(ScRequestNoteDtl requestNoteDtl) {
		ScRequestItem requestItem = requestNoteDtl.getRequestItem();
		String fulfilmentType = null;
		String priceType = null;
		if(requestNoteDtl.getFulfilmentType() != null) {
			fulfilmentType = requestNoteDtl.getFulfilmentType().getParameterCode();
		}
		if(requestNoteDtl.getPriceType() != null) {
			priceType = requestNoteDtl.getPriceType().getParameterCode();
		}
		
		ScRequestSupplierItem requestSupplierItem = requestNoteDtl.getScRequestSupplierItem();
		BigDecimal orderQuantity = requestItem.getQuantity();
		BigDecimal subTotalAmount = BigDecimal.ZERO;

		if (orderQuantity != null && requestSupplierItem != null && priceType != null) {
			if(!fulfilmentType.equals(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
				BigDecimal zonalSubTotalAmount = orderQuantity;
				TplSpecQuestion specQuestion = requestItem.getTplSpecQuestion();
				if(specQuestion != null) {
					BigDecimal uomFrequency = requestItem.getTplSpecQuestion().getClUomFrequency();
					BigDecimal uomPerDay = specQuestion.getClUomPerDay();
					BigDecimal uomPerMonth = specQuestion.getClUomPerMonth();
					BigDecimal itemDurationMonth = specQuestion.getClDurationMonth();

					if (uomFrequency != null) {
						zonalSubTotalAmount = zonalSubTotalAmount.multiply(uomFrequency);
					}

					if (uomPerDay != null) {
						zonalSubTotalAmount = zonalSubTotalAmount.multiply(uomPerDay);
					}

					if (uomPerMonth != null) {
						zonalSubTotalAmount = zonalSubTotalAmount.multiply(uomPerMonth);
					}

					// add fix for 24348 calculation for RN
					if (!fulfilmentType.equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)) {
						if (itemDurationMonth != null) {
							zonalSubTotalAmount = zonalSubTotalAmount.multiply(itemDurationMonth);
						} else {
							BigDecimal durationMonth = specQuestion.getTplSpecMaster().getDurationMonth();

							if (durationMonth != null) {
								zonalSubTotalAmount = zonalSubTotalAmount.multiply(durationMonth);
							}
						}
					}
				}
				// - start- #27797
				if (fulfilmentType.equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)
						|| fulfilmentType.equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)
						) {
					if (priceType.equals(ParameterConst.PRICE_TYPE_ZONAL_STANDARD)) {
						BigDecimal highestZonePrice = BigDecimal.ZERO;

						for (ScSupplierItemPrice spItemPrice : requestSupplierItem.getSupplierItemPrices()) {
							int result = spItemPrice.getItemPrice().compareTo(highestZonePrice);

							if (result == 1) {
								highestZonePrice = spItemPrice.getItemPrice();
							}
						}
						subTotalAmount = highestZonePrice.multiply(zonalSubTotalAmount);
					} else {
						for (ScSupplierItemPrice spItemPrice : requestSupplierItem.getSupplierItemPrices()) {
							subTotalAmount = subTotalAmount.add(spItemPrice.getItemPrice()
									.multiply(zonalSubTotalAmount));
						}
					}
				}

				else {
					for (ScSupplierItemPrice spItemPrice : requestSupplierItem.getSupplierItemPrices()) {
						subTotalAmount.add(spItemPrice.getItemPrice().multiply(zonalSubTotalAmount));
					}
				}
				// - end - #27797
			} else {
				ScSupplierItemPrice itemPrice = getTopZonalTierPrice(requestSupplierItem.getSupplierItemPrices(),
						orderQuantity,
						priceType,
						requestItem.getItemName());
				
				if(itemPrice != null) {
					subTotalAmount = itemPrice.getItemPrice().multiply(orderQuantity);
				}
			}
		}
		requestNoteDtl.setTotalPricePerItem(subTotalAmount);
		
		//#31528 - Start
		//rounding to 2 decimal places
		requestNoteDtl.setTotalPricePerItemRounded(subTotalAmount.setScale(2, RoundingMode.HALF_UP));
		//#31528 - End
		
		return subTotalAmount;
	}
	
	/**
	 * show Item Specifications Pop up
	 *
	 * @param scRequestNoteDtl the sc request note dtl
	 */
	public void showSpecificationPopup(ScRequestNoteDtl scRequestNoteDtl){
		debugLogger("showSpecificationPopup");
		ScRequestItem requestItem = scRequestNoteDtl.getRequestItem();
		List<TplSpecQuestion> specificationList = getManagedBean().getSpecificationMap().
				get(requestItem.getRequestNoteItemId());
		if(specificationList == null && null != requestItem.getTplSpecQuestion()) {
			TplSpecQuestion parentSpecQuestion = requestItem.getTplSpecQuestion();
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setSpecQuestionId(parentSpecQuestion.getSpecQuestionId());
			TemplateSpecResDTO resDTO = getTemplateSpecSO().getSpecificationsByQuestionId(reqDTO);
			List<TplSpecQuestion> specList = resDTO.getSpecQuestionList();
			getManagedBean().getSpecificationMap().put(requestItem.getRequestNoteItemId(), specList);
			getManagedBean().setSpecificationList(specList);
		} else {
			getManagedBean().setSpecificationList(specificationList);
		}
		getManagedBean().setAutoUpdate(Boolean.FALSE);
	}
	
	/**
	 * Retrieve fresh copy of ScRequestItem before assign product code
	 * @param requestNoteDtl
	 * @return
	 */
	public String assignProductCode(ScRequestNoteDtl requestNoteDtl) {
		log.info(">>>>>>>>>requestNoteBackingBean assignProductCode");
		ScRequestItem requestItem = requestNoteDtl.getRequestItem();
		RequestItemReqDTO reqDTO = new RequestItemReqDTO();
		reqDTO.setRequestItemId(requestItem.getRequestNoteItemId());
		ScRequestItem freshRequestItem = getRequestItemSO().retrieveItemByRequestItemId(reqDTO).getRequestItem();
		if(freshRequestItem != null) {
			requestItem = freshRequestItem;
		}
		requestNoteDtl.setRequestItem(requestItem);
		CodificationBackingBean codificationBB = new CodificationBackingBean();
		return codificationBB.assignProductCode(requestItem, null, false);
	}
	
	private ScSupplierItemPrice getTopZonalTierPrice(List<ScSupplierItemPrice> supplierItemPrices, 
			BigDecimal orderQuantity, String priceType, String itemName) {
		List<ScSupplierItemPrice> groupedItemPrice = new ArrayList<ScSupplierItemPrice>();
		boolean isTierPriceMatched = false;
		boolean isTierPrice = false;
		ScSupplierItemPrice topPricedItem = null;
		for(ScSupplierItemPrice itemPrice : supplierItemPrices) {
			if (itemPrice.getPriceType().getParameterCode().equals(priceType)) {
				BigDecimal toQty = itemPrice.getToQty();
				BigDecimal fromQty = itemPrice.getFromQty();
				if (fromQty != null
						&& orderQuantity != null) {
					isTierPrice = true;
					if (fromQty.compareTo(orderQuantity) <= 0
							&& (toQty == null || orderQuantity.compareTo(toQty) <= 0)) {
						isTierPriceMatched = true;
						groupedItemPrice.add(itemPrice);
					} 
				} else {
					groupedItemPrice.add(itemPrice);
				}
			}
		}
		if(isTierPrice && !isTierPriceMatched) {
			throw new EpException(MessageConst.ERROR_760399, new String[] {itemName});
		} else {
			if(CollectionUtils.isNotEmpty(groupedItemPrice)) {
				topPricedItem = findMaxPriceItem(groupedItemPrice);
			}
		}
		
		return topPricedItem;
	}
	
	/**
	 * Find max price item.
	 *
	 * @param items the items
	 * @return the sc loi loa item
	 */
	private ScSupplierItemPrice findMaxPriceItem(List<ScSupplierItemPrice> items) {
		Comparator<ScSupplierItemPrice> comparator = new Comparator<ScSupplierItemPrice>() {
			@Override
			public int compare(ScSupplierItemPrice o1, ScSupplierItemPrice o2) {
				if(o1.getItemPrice() != null && o2.getItemPrice() != null){
					return new CompareToBuilder().append(o1.getItemPrice(),
							o2.getItemPrice()).toComparison();
				}
				return 0;
			}
		};
		ScSupplierItemPrice scSupplierItemPrice = Collections.max(items, comparator);
		return scSupplierItemPrice;
	}
	
	/**
	 * check if no supplier respond 
	 *
	 * @param scRequestNoteDtl the sc request note dtl
	 * @return
	 */
	private void checkIsSupplierResponse(List<ScRequestNoteDtl>  requestNoteDtls, Long currentStatus){
		log.debug("checkSupplierResponse");
		
		boolean isResponse = Boolean.TRUE;
		
		if(DPStatusConstants.SQ_PENDING_RN_CREATION.equals(currentStatus)){
			for (ScRequestNoteDtl scRequestNoteDtl : requestNoteDtls) {
				
				List<ScRequestSupplierItem> reqSuppItems = scRequestNoteDtl.getRequestItem().getSupplierItems(); 
				int count = 0;
				for (ScRequestSupplierItem reqSuppItem: reqSuppItems){
					if(!reqSuppItem.isLoaRejected()){
						count++;
					}
				}

				if(count == 0){
					isResponse = Boolean.FALSE;
					break;
				}
			}
		}

		if(!isResponse){
			getManagedBean().setCloneReQuote(true);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, 
					MessageUtils.getMessage(MessageConst.ERROR_760475, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}else{
			getManagedBean().setCloneReQuote(false);
		}
	}
	
	public void cloneAction(){
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		ScQuote quote = requestNote.getQuote();
		
		PmUser user = new FacesScopes().getUserSessionBean();
		DPOsbReqDTO reqOsbDTO = new DPOsbReqDTO();
		reqOsbDTO.setRequestNoteId(requestNote.getRequestNoteId());
		reqOsbDTO.setUserId(user.getUserId());
		
		DPOsbResDTO resOsbDTO =	getDPOsbSO().cloneQuote(reqOsbDTO);
		
		if(resOsbDTO.isSuccessful()){
			successMessage(MessageConst.INFO_700058, ParameterTypeConst.DOCUMENT_TYPES,
					ParameterConst.DOCTYPE_SIMPLE_QUOTE,
					quote.getQuoteNo(), Boolean.TRUE);
		}
	}
	
	/**
	 * Gets the DPOsbSO.
	 *
	 * @return the DPOsbSO
	 */
	private IDPOsbSO getDPOsbSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDPOsbSO.class);
	}
	
	//26221 - Start
	public String getPreparedByApprove(ScRequestNoteDtl requestNoteDtl) {
		debugLogger("getPreparedByApprove");
		StringBuilder sb = new StringBuilder();
		UserReqDTO reqDTO = new UserReqDTO();
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

		if (requestNoteDtl.getFulfilmentType() != null) {
			if (requestNoteDtl.getFulfilmentType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
				reqDTO.setRoleCode(RoleCode.REQUISITIONER.value());
			} else {
				reqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
			}

			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setOrgProfileId(getManagedBean().getRequestNote().getPmOrgProfile().getOrgProfileId());
			orgProfileReqDTO.setGroupOrgType(ParameterConst.ORG_AUTH_TYPE_ORG_GROUP);
			OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveProfileGroupByGroupOrgType(orgProfileReqDTO);
	
			String orgProfileIds = String.valueOf(getManagedBean().getRequestNote().getPmOrgProfile().getOrgProfileId());
					
			if (CollectionUtils.isNotEmpty(orgProfileResDTO.getProfileGroups())) {
				for(PmProfileGroup group: orgProfileResDTO.getProfileGroups()){
					orgProfileIds = orgProfileIds + "," + group.getMainOrgProfile().getOrgProfileId();
				}
			}

			sb.append("pmUserOrgs.pmOrgProfile.orgProfileId^");				
			sb.append(orgProfileIds);
			sb.append("^9");
			sb.append("|pmUserOrgs.pmUserRoles.pmRole.roleCode^");
			sb.append(reqDTO.getRoleCode());
			sb.append("|pmUserOrgs.pmUserRoles.recordStatus^1");
		}
		return sb.toString();
	}
	//26221 - End
	
	/**
	 * Add/display only cancel/reject button if SQ not valid 
	 */
	public void addButton(){
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		//checking if SQ still valid, if not valid thus cancel/reject button will be added
		
		if(!isSqStillValid()){
			if(currentStatusId.equals(DPStatusConstants.RN_PENDING_APPROVAL)
			   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION_APPROVAL) ){
			   String[] addReject = new String[]{BpmOutcomeConst.BPM_REJECT};    		
			   getManagedBean().setOutComes(addReject);
			}
			else{
			   String[] addCancel = new String[]{BpmOutcomeConst.BPM_CANCEL};    		
			   getManagedBean().setOutComes(addCancel);
			}						
		}	
	}
	
	/**
	 * Check either SQ is valid or not (SQ only valid for 60 days after it closing date)
	 */
	public boolean isSqStillValid(){		
		
		Boolean validDate = true;	
		Long currentStatusId = null;
		ScQuote scQuote = null;
		
		if(getManagedBean().getRequestNote() != null){
			scQuote = getManagedBean().getRequestNote().getQuote();
			if(getManagedBean().getRequestNote().getCurrentStatus() == null){
				if(getManagedBean().getCurrentStatusId() != null){
					currentStatusId = getManagedBean().getCurrentStatusId();
				}
					
				if(scQuote != null && scQuote.getCurrentStatus() != null){
					currentStatusId =  scQuote.getCurrentStatus().getPmStatus().getStatusId();
				}			
			}else{
				currentStatusId = getManagedBean().getRequestNote().getCurrentStatus().getPmStatus().getStatusId();
			}	
		}	 
		
		// Refactor: #12177 - Start
		Integer sqTimeValidity = NgepApplicationBean.getSqTimeValidity();	
		log.info("Refactor 12177 (RequestNoteBackingBean) : getSqTimeValidity - " + sqTimeValidity);
		// Refactor: #12177 - End
		
		if(scQuote != null && currentStatusId != null){
			if(getManagedBean().getPurchaseInDefaultId() == null){
				Date currentDate = new Date();
				Date closingDate = scQuote.getEndDate();
				
				Calendar cal = Calendar.getInstance();
				cal.clear(); 
				cal.setTime(closingDate);
				cal.add(Calendar.DATE, sqTimeValidity); //add 60days to closing/end date
				
				if(currentStatusId.equals(DPStatusConstants.RN_PENDING_ITEM_CODIFICATION)
				   || currentStatusId.equals(DPStatusConstants.SQ_PENDING_RN_CREATION) 
				   || currentStatusId.equals(DPStatusConstants.RN_PENDING_APPROVAL)
				   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION) 
				   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION_APPROVAL) ){
					if(currentDate.compareTo(cal.getTime()) >= 0){
						validDate = false;
						sqExpiredMessageFlag = true;
						// 12177 - Start
						getManagedBean().setSqEndDate(toCalendar(scQuote.getEndDate()));
						// 12177 - End
					}
				}					
			}				
		}			
		return validDate;	
	}
	
	// 12177 - Start
	public static Calendar toCalendar(Date date){ 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	// 12177 - End
	
	
	//26284 - Start	
	public void supplierDisciplinaryActionInfo(SmSupplier smSupplier){
		SmSupplier currSupp = smSupplier;
		if(currSupp != null){
			List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(currSupp);
			if(!EntityHelper.isCollectionEmpty(voList)
					&& voList.size() > 0){
				getManagedBean().setDisciplineVOs(voList);
			} else {
				getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
			}
		} else {
			getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		}
		//#32643,32642 - Start
		RequestContext.getCurrentInstance().execute(
				"disciplinaryActionHistoryDlg.show();");
		//#32643,32642 - End
	}
		
	private List<DisciplinaryActionVO> getDisciplinaryActionVO(SmSupplier currSupp) {		
		ISupplierDataMaintenanceSO supplierDataMaintenanceSO = (ISupplierDataMaintenanceSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierDataMaintenanceSO.class);
			
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setSupplier(currSupp);
		List<DisciplinaryActionVO> voList = new ArrayList<DisciplinaryActionVO>();
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		
		//29749 - Start
		BlacklistDirectorReqDTO blackListReqDTO = new BlacklistDirectorReqDTO();
		BlacklistDirectorResDTO blackListResDTO = new BlacklistDirectorResDTO();
		//29749 - End
		
		perReqDTO.setSmSupplierId(currSupp.getSupplierId());
		//#30513 - Start
		perReqDTO.setRecordStatus(null);
		//#30513 - End
		StringBuilder str1 = new StringBuilder();
		StringBuilder str2 = new StringBuilder();
		//#30513 - Start (follow from SM (BaseReviewApplicationBackingBean.getDisciplinaryActionVO(SmSupplier)))
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelBySupplierIdAndRecordStatus(perReqDTO);
		//#30513 - End
			
		List<SmDisciplinaryAction> resultList = supplierDataMaintenanceSO.retrieveActiveDisciplinaryActionBySupplierDp(reqDTO).getSmDisciplinaryActions();
		if (resultList != null
				&& resultList.size() > 0) {
			for(SmDisciplinaryAction disciplinaryAction: resultList){
				if(disciplinaryAction.isWarning() || disciplinaryAction.isCancelCategory() 
						|| disciplinaryAction.isSuspendRegistration() || disciplinaryAction.isCancelRegistration() 
						|| disciplinaryAction.isBlacklistOwner() || disciplinaryAction.isManufacturerCancel()
						|| disciplinaryAction.isBumiputeraCancel()){
					DisciplinaryActionVO retVal = new DisciplinaryActionVO();
					if(disciplinaryAction.getStartDate() != null)
						retVal.setStartDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getStartDate()));
						retVal.setEndDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getEndDate()));
						str1.append(retVal.getStartDate()).append(" "); 
						getManagedBean().setDisciplinaryActionStartDate(str1.toString());
					if(disciplinaryAction.getEndDate() != null)
						retVal.setEndDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getEndDate()));
						str2.append(retVal.getEndDate()).append(".");
						getManagedBean().setDisciplinaryActionEndDate(str2.toString());
						
						//#32643,32642 - Start (follow from SM bug 28198))
						List<DisciplinaryActionVO> remList = new ArrayList<DisciplinaryActionVO>();
						
						List<SmDisciplinaryActionRemarks> remarksList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionRemarksByDA(disciplinaryAction);
						
						if(remarksList != null && remarksList.size() > 0){
							for (SmDisciplinaryActionRemarks remarks : remarksList) {
								DisciplinaryActionVO rem1 = new DisciplinaryActionVO();
								rem1.setRemarks(remarks.getRemark());
								remList.add(rem1);
							}
						}
						DisciplinaryActionVO rem = new DisciplinaryActionVO();
						rem.setRemarks(disciplinaryAction.getRemark());
						remList.add(rem);
						retVal.setRemarksList(remList);
						//32643,32642 - End
						
					if(disciplinaryAction.isWarning()){
						retVal.setWarning(true);
						getManagedBean().setDisciplinaryActionWarning(true);
					}
					if(disciplinaryAction.isCancelCategory())
						retVal.setCancelCategory(true);
					if(disciplinaryAction.isSuspendRegistration())
						retVal.setSuspendRegistration(true);
					if(disciplinaryAction.isCancelRegistration())
						retVal.setCancelRegistration(true);				
					if(disciplinaryAction.isBumiputeraCancel())
						retVal.setBumiputeraCancel(true);
					if(disciplinaryAction.isManufacturerCancel())
						retVal.setManufacturerCancel(true);
					if(disciplinaryAction.isBlacklistOwner())
						retVal.setBlacklistOwnerDirector(true);
					
					//29749 - Start (follow from SM (BaseReviewApplicationBackingBean.getDisciplinaryActionVO(SmSupplier)))
					if(disciplinaryAction.isCancelCategory() 
						|| disciplinaryAction.isManufacturerCancel()
						|| disciplinaryAction.isBumiputeraCancel()){
						List<DisciplinaryActionCategoryVO> catVoList = new ArrayList<DisciplinaryActionCategoryVO>();
						List<Object[]> catList = SmApplicationUtil.retrieveCancelCategoryByDisciplinaryActionId(disciplinaryAction.getDisciplinaryActionId());
							
						if(!EntityHelper.isCollectionEmpty(catList)
								&& catList.size() > 0){
							List<String> catCodeList = new ArrayList<String>();
							for(Object[] arr : catList){
								if(disciplinaryAction.getDisciplinaryActionId().toString().equalsIgnoreCase(arr[7].toString())){
									DisciplinaryActionCategoryVO catVo = new DisciplinaryActionCategoryVO();
									String catCode = StringUtils.EMPTY;
									catCode = arr[3].toString() + arr[4].toString() + arr[5].toString() ;
									catVo.setCategoryCode(catCode);
									if(!catCodeList.contains(catCode)){
										catVoList.add(catVo);
										catCodeList.add(catCode);
									}
								}
							}						
							retVal.setCategoryCodeList(catVoList);
						}
					}	
					//29749 - End
						
					if(disciplinaryAction.isBlacklistOwner()
							|| disciplinaryAction.isSuspendRegistration()
								|| disciplinaryAction.isCancelRegistration()){
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
						
						//29749 - Start (follow from SM (BaseReviewApplicationBackingBean.getDisciplinaryActionVO(SmSupplier)))
						if(disciplinaryAction.isSuspendRegistration()){
							if(personnelResDTO != null 
									&& personnelResDTO.getSmPersonnelList().size() > 0){
								for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
									BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
									blackListReqDTO.setDirectorIdNo(personnel.getIdentificationNo());
									blackListResDTO = SmServiceLookup.getBlacklistDirectorSO().retrieveAllPersonBlacklistByIdentificationNO(blackListReqDTO);
									
									if(blackListResDTO != null && blackListResDTO.getPersonBlacklist() != null){
										//#31017 - start
										if (personnel.isDirector() || personnel.isEquityOwner()){
											blacklistOwner.setIcNo(personnel.getIdentificationNo());
										}
										//#31017 - end
										blacklistownerDirectorList.add(blacklistOwner);
									}
								}
								retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
							}	
						}
						
						if(disciplinaryAction.isCancelRegistration() 
								&& !disciplinaryAction.isBlacklistOwner()){
							if(personnelResDTO != null 
									&& personnelResDTO.getSmPersonnelList().size() > 0){
								for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
									BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
									blackListReqDTO.setDirectorIdNo(personnel.getIdentificationNo());
									blackListResDTO = SmServiceLookup.getBlacklistDirectorSO().retrieveAllPersonBlacklistByIdentificationNO(blackListReqDTO);
									
									if(blackListResDTO != null && blackListResDTO.getPersonBlacklist() != null){
										//#31017 - start
										if (personnel.isDirector() || personnel.isEquityOwner()){
											blacklistOwner.setIcNo(personnel.getIdentificationNo());
										}
										//#31017 - end
										blacklistownerDirectorList.add(blacklistOwner);
									}
								}
								retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
							}						
						}
						
						if(disciplinaryAction.isBlacklistOwner()){
							if(personnelResDTO != null 
									&& personnelResDTO.getSmPersonnelList().size() > 0){
								for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
									BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
									blackListReqDTO.setDirectorIdNo(personnel.getIdentificationNo());
									blackListResDTO = SmServiceLookup.getBlacklistDirectorSO().retrieveAllPersonBlacklistByIdentificationNO(blackListReqDTO);
									
									if(blackListResDTO != null && blackListResDTO.getPersonBlacklist() != null){
										//#31017 - start
										if (personnel.isDirector() || personnel.isEquityOwner()){
											blacklistOwner.setIcNo(personnel.getIdentificationNo());
										}
										//#31017 - end
										blacklistownerDirectorList.add(blacklistOwner);
									}
								}
								retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
							}
						}		
						
					}
					//29749 - End
					
				voList.add(retVal);
				}				
			}
		}
		return voList;
	}
		
	public Boolean getDisciplinaryActionActive(SmSupplier supplier) {
		ISupplierDataMaintenanceSO supplierDataMaintenanceSO = (ISupplierDataMaintenanceSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierDataMaintenanceSO.class);
			
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setSupplier(supplier);
		return supplierDataMaintenanceSO.retrieveActiveDisciplinaryActionBySupplierDp(reqDTO).isDisciplinaryActionActiveDp();
	}
	
	public void checkSupplierDisciplinaryAction(ScRequestNote requestNote){
		ISupplierDataMaintenanceSO supplierDataMaintenanceSO = (ISupplierDataMaintenanceSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierDataMaintenanceSO.class);
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		List<SmDisciplinaryAction> smDisciplinaryAction = null;
			
		List<ScRequestNoteDtl> requestNoteDtls = requestNote.getRequestNoteDtls();
		if(requestNoteDtls != null){
			for (ScRequestNoteDtl rnDtl : requestNoteDtls) {
				if(rnDtl.getScRequestSupplierItem() != null && 
						rnDtl.getScRequestSupplierItem().getSmSupplier() != null){
						
					reqDTO.setSupplier(rnDtl.getScRequestSupplierItem().getSmSupplier());
					smDisciplinaryAction = supplierDataMaintenanceSO.retrieveActiveDisciplinaryActionBySupplierDp(reqDTO).getSmDisciplinaryActions();
						
					if(smDisciplinaryAction != null){
						for(SmDisciplinaryAction disciplinaryAction: smDisciplinaryAction){
							if(disciplinaryAction.isSuspendRegistration()
							   || disciplinaryAction.isCancelRegistration()
							   || disciplinaryAction.isBlacklistOwner()){
								throw new EpException(MessageConst.ERROR_760500);
							}			
						}
					}	
				}			
			}
		}
	}
	//26284 - End
	
	//12183 - Start
	
	// Refactor #18 - Start.
	public void getDocStatus(ScRequestNote requestNote){

		if(checkStatusPidExpired(requestNote)){
			getManagedBean().setDocStatus(getStatusDescById(DPStatusConstants.PID_EXPIRED)); 
		}else if(!isSqStillValid()){
			getManagedBean().setDocStatus(getStatusDescById(DPStatusConstants.SQ_EXPIRED));
		}else{
			getManagedBean().setDocStatus(getStatusDescById(getManagedBean().getRequestNoteStatus().getStatusId()));
		}
	}
	// Refactor #18 - End.
	
	// Refactor #18 - Start.
	public boolean checkStatusPidExpired(ScRequestNote requestNote){
		
		Long currentStatusId = null;
		ScPurchaseInDefault scPID = null;
		
		if(requestNote != null){
			
			if(requestNote.getCurrentStatus() != null){
				currentStatusId = requestNote.getCurrentStatus().getPmStatus().getStatusId();		
			}else{
				currentStatusId = getManagedBean().getCurrentStatusId();
			}	
			
			if(currentStatusId == null){
				return false;
			}
			
		    if(currentStatusId.equals(DPStatusConstants.RN_PENDING_SUBMISSION)
		    	  || currentStatusId.equals(DPStatusConstants.RN_PENDING_APPROVAL)
		    	  || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION) 
		    	  || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION_APPROVAL)
		    	  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)){
		    	
				if(CollectionUtils.isNotEmpty(requestNote.getRequestNoteDtls())
		    			&& null != requestNote.getRequestNoteDtls().get(0).getRequestItem()
		    			&& null != requestNote.getRequestNoteDtls().get(0).getRequestItem().getPurchaseInDefault()){
					
		    		scPID = requestNote.getRequestNoteDtls().get(0).getRequestItem().getPurchaseInDefault();
		    	} 		
		    }  
		}
		
		return pidValidityBoolean(scPID);
	}
	// Refactor #18 - End.
	
	private boolean pidValidityBoolean(ScPurchaseInDefault purchaseInDefault){
		boolean valid = false;
		
		if(purchaseInDefault!=null){
			Date today  = DateUtils.getCurrentDate();
			Date ExpDate = DateUtils.addDays(purchaseInDefault.getChangedDate(), purchaseInDefault.getPidValidDays());
			
			log.info("pID validity begin");
			
			if(today.after(ExpDate)){
				getManagedBean().setPidValidityExpired(true);
				valid = true;
			}
			log.info("pID validity end");
		}
		return valid;
	}
	//12183 - End
	
	// #28789 - start
	public void onChangePriceType() {
		if (StringUtils.isBlank(getManagedBean().getPriceType())) {
			return;
		}

		BigDecimal normalTierTotalPrice = BigDecimal.ZERO;
		BigDecimal itemQuantity = getManagedBean().getRequestSupplierItem().getRequestNoteItem().getQuantity();
		String selectedPriceType = getManagedBean().getPriceType();
		Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		NumberFormat formatter= NumberFormat.getCurrencyInstance(myLocale); 
		StringBuilder normalTierTotalPriceSB = new StringBuilder(ParameterConst.CATEGORY_TYPE_READING_MATERIAL);
		//#28789
		getManagedBean().setPriceTypeStringFlag(Boolean.FALSE);
		getManagedBean().setPriceTypeTableFlag(Boolean.FALSE);
		getManagedBean().setFromAndToQuantityFlag(Boolean.FALSE);

		if (Arrays.asList(ParameterConst.PRICE_TYPE_STANDARD, ParameterConst.PRICE_TYPE_PROMOTION).contains(
				getManagedBean().getPriceType())) {
			getManagedBean().setPriceTypeStringFlag(Boolean.TRUE); //#28789
			
			for (CmPricing cmp : getManagedBean().getCompleteCmPricingList()) {
				if (StringUtils.equals(cmp.getPricingType(), selectedPriceType) && (cmp.getPrice() != null)) {
					normalTierTotalPrice = normalTierTotalPrice.add(cmp.getPrice());
				}
			}
			if (normalTierTotalPrice.compareTo(BigDecimal.ZERO) == 0) {
				normalTierTotalPriceSB.append(StringUtils.EMPTY);
			} else {
				normalTierTotalPriceSB.append(formatter.format(normalTierTotalPrice).replace(ParameterConst.CATEGORY_TYPE_READING_MATERIAL, " "));
			}
			getManagedBean().setNormalAndTierPriceStr(normalTierTotalPriceSB.toString());
		} else if (Arrays.asList(ParameterConst.PRICE_TYPE_TIER, ParameterConst.PRICE_TYPE_PROMOTION_TIER).contains(
				getManagedBean().getPriceType())) {
			//#28789
			if (itemQuantity == null) {
				getManagedBean().setNormalAndTierPriceStr(null);
				return;
			}
			BigDecimal fromQtyBD = BigDecimal.ZERO;
			BigDecimal toQtyBD = BigDecimal.ZERO;
			getManagedBean().setPriceTypeStringFlag(Boolean.TRUE);//#28789
			
			for (CmPricing cmp : getManagedBean().getCompleteCmPricingList()) {
				if (StringUtils.equals(cmp.getPricingType(), selectedPriceType) && (cmp.getPrice() != null)
						&& cmp.getFromQty() != null && cmp.getToQty() != null) {
					fromQtyBD = BigDecimal.valueOf(cmp.getFromQty());
					toQtyBD = BigDecimal.valueOf(cmp.getToQty());
					if (itemQuantity.compareTo(fromQtyBD) >= 0 && itemQuantity.compareTo(toQtyBD) <= 0) {
						normalTierTotalPriceSB.append(formatter.format(cmp.getPrice()).replace(
								ParameterConst.CATEGORY_TYPE_READING_MATERIAL, " "));
						break;
					}
				}
			}
			//#28789
			if (normalTierTotalPriceSB.length() < 3) {
				normalTierTotalPriceSB = new StringBuilder(ParameterConst.SKUNO);
			}
			
			getManagedBean().setNormalAndTierPriceStr(normalTierTotalPriceSB.toString());
		} else if (Arrays.asList(ParameterConst.PRICE_TYPE_ZONAL_STANDARD, ParameterConst.PRICE_TYPE_ZONAL_PROMOTION,
				ParameterConst.PRICE_TYPE_ZONAL_TIER, ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER).contains(
				selectedPriceType)) {
			List<CmPricing> filterList = new ArrayList<>();
			//#28789
			getManagedBean().setPriceTypeTableFlag(Boolean.TRUE);
			if (Arrays.asList(ParameterConst.PRICE_TYPE_ZONAL_TIER, ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER)
					.contains(selectedPriceType)) {
				getManagedBean().setFromAndToQuantityFlag(Boolean.TRUE);
			}
			
			for (CmPricing cmp : getManagedBean().getCompleteCmPricingList()) {
				if (StringUtils.equals(cmp.getPricingType(), selectedPriceType)) {
					filterList.add(cmp);
				}
			}
			getManagedBean().setFilteredCmPricingList(filterList);
		}
	}
	// #28789 - end
	
	// 36362 - start
	/**
	 * To validate quantity not exceed tier max quantity
	 * 
	 * @param requestNoteDtl
	 */
	private void validateTierQuantityExceedMaxQuantity(ScRequestNoteDtl requestNoteDtl) {
		if (requestNoteDtl.getPriceType().getParameterId().equals(DPConstants.PRICE_TYPE_TIER_STANDARD)
				|| requestNoteDtl.getPriceType().getParameterId().equals(DPConstants.PRICE_TYPE_ZONAL_TIER_STANDARD)) {

			if (requestNoteDtl.getRequestItem().getTplSpecQuestion() != null) {
				validateFromSQ(requestNoteDtl);
			} else {
				validateFromCatalogue(requestNoteDtl);
			}
		}
	}

	/**
	 * To validate tier quantity change during RN Creation from SQ
	 * 
	 * @param requestNoteDtl
	 */
	private void validateFromSQ(ScRequestNoteDtl requestNoteDtl) {
		if (requestNoteDtl.getRequestItem().getQuantity()
				.compareTo(requestNoteDtl.getRequestItem().getTplSpecQuestion().getClQuantity()) > 0) {
			// Note for 29557, need to change this error message when merge.
			// Dev -> MessageConst.ERROR_760527
			// Prod -> MessageConst.ERROR_760399
			throw new EpException(MessageConst.ERROR_760399, new String[] { requestNoteDtl.getRequestItem()
					.getItemName() });
		}
	}

	/**
	 * To validate tier quantity change during RN Creation from Catalogue
	 * 
	 * @param requestNoteDtl
	 */
	private void validateFromCatalogue(ScRequestNoteDtl requestNoteDtl) {
		if (requestNoteDtl.getScRequestSupplierItem().getCmSupplierItem() != null
				&& requestNoteDtl.getScRequestSupplierItem().getCmSupplierItem().getCmPricings() != null) {
			// from catalogue
			List<CmPricing> cmPricings = requestNoteDtl.getScRequestSupplierItem().getCmSupplierItem().getCmPricings();

			if (cmPricings != null && !cmPricings.isEmpty()) {
				// Get the last element directly
				sortTierToQtyValueAscending(cmPricings);
				CmPricing lastCmPricing = cmPricings.get(cmPricings.size() - 1);
				BigDecimal lastToQty = new BigDecimal(lastCmPricing.getToQty());

				if (requestNoteDtl.getRequestItem().getQuantity().compareTo(lastToQty) > 0) {
					throw new EpException(MessageConst.ERROR_760399, new String[] { requestNoteDtl.getRequestItem()
							.getItemName() });
				}
			}
		}
	}

	/**
	 * Sort toQty value from CmPricing to make sure the last value toQty is the
	 * largest.
	 * 
	 * @param cmPricings
	 */
	private void sortTierToQtyValueAscending(List<CmPricing> cmPricings) {
		Collections.sort(cmPricings, new Comparator<CmPricing>() {
			@Override
			public int compare(CmPricing p1, CmPricing p2) {
				// Convert to BigDecimal for proper comparison
				BigDecimal toQty1 = (p1.getToQty() != null) ? new BigDecimal(p1.getToQty()) : BigDecimal.ZERO;
				BigDecimal toQty2 = (p2.getToQty() != null) ? new BigDecimal(p2.getToQty()) : BigDecimal.ZERO;
				return toQty1.compareTo(toQty2);
			}
		});
	}

	// 36362 - end
	
	// #36119 - start
	/**
	 * Act as reload RN for codification wrapper
	 * 
	 * @param RequestNoteResDTO
	 *            rnResDTO
	 * 
	 * @return void
	 */
	public void reloadRequestNoteForCodificationWrapper(RequestNoteResDTO requestNoteResDTO) {
		reloadRequestNoteForCodification(requestNoteResDTO);
	}
	// #36119 - end
}
