/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.requestnote;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.fl.itemsummary.IFlItemSummarySO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.fl.itemsummary.FlItemSummaryReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryResDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
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
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScRNWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRNZone;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
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
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.sc.purchaseindefault.PurchaseInDefaultBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;
import my.ep.web.bean.sc.directpurchase.MarketSummaryAndPIBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sc.requestnote.RequestNoteManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.SourcingZoneHelper;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
// TODO: Auto-generated Javadoc

/**
 * The Class RequestNoteBackingBean.
 *
 * @author sri
 * @since 11.02.2014
 */
@ManagedBean(name = "requestNoteBackingBean")
@RequestScoped
public class RequestNoteBackingBean extends SourcingBaseBackingBean<RequestNoteManagedBean> {
	
	/** The log. */
	private static Logger log = Logger.getLogger(RequestNoteBackingBean.class);
	
	/** The flag. */
	private boolean flag;
	
	/** The Constant ZONE_SEPARATOR. */
	public static final String ZONE_SEPARATOR = "_";
	
	/** The request note page. */
	private String REQUEST_NOTE_PAGE = "requestNoteMain";
	
	/**
	 * Instantiates a new request note backing bean.
	 */
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
		RequestNoteResDTO requestNoteResDTO = getDirectPurchaseSO().getRequestNote(requestNoteReqDTO);
		if(requestNoteResDTO.getRequestNoteDetail() != null){
			ScRequestNote requestNote = requestNoteResDTO.getRequestNoteDetail();
			retrieveRequestNoteDetails(requestNote, null, Boolean.FALSE);
			getCurrentStatus(requestNote.getCurrentStatus().getPmStatus());	
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
		getManagedBean().setViewOnly(Boolean.TRUE);
		
		// page navigation 
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_PAGE);
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
		List<PmUser> userList = getManagedBean().getUserList();
		RequestNoteResDTO requestNoteResDTO = null;
    try {
    	
    	parseRequestNote(requestNote, outcome);
	    
    	// remarks
    	finalizeRemarks(requestNote.getRemarks());
    	
	    setUserDetails(requestNote);
	    requestNoteReqDTO.setRequestNote(requestNote);
	    FacesScopes face = new FacesScopes();
	    requestNoteReqDTO.setPmUser(face.getUserSessionBean());
	    requestNoteReqDTO.setRoleCode(getUserRoleCode());
	    
	    if(requestNote.getCurrentStatus() == null){
	    	// WorkFlow Status 
	    	List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
	    	ScRNWorkFlowStatus scWorkFlowStatus = new ScRNWorkFlowStatus();
	    	StatusReqDTO statusReqDTO = new StatusReqDTO();
	    	statusReqDTO.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
	    	StatusResDTO statusResDTO = getStatusSO().retrieveStatusByStatusId(statusReqDTO);
	    	getCurrentStatus(statusResDTO.getPmStatus());
	    	scWorkFlowStatus.setCurrent(Boolean.TRUE);
	    	scWorkFlowStatus.setPmStatus(statusResDTO.getPmStatus());
	    	scWorkFlowStatus.setRequestNote(requestNote);
	    	workFlowStatuses.add(scWorkFlowStatus);
	    	requestNote.setWorkFlowStatuses(workFlowStatuses);
	    } else{
	    	requestNote.setWorkFlowStatuses(null);
	    }

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

	    Long prInqId = getManagedBean().getPrInquiryId();
	    Long pidId = getManagedBean().getPurchaseInDefaultId();
	    if(getManagedBean().getPrInquiryId() != null){
			requestNoteReqDTO.setPurchaseInquiryId(prInqId);
		}
	    Object token = face.getBPMAuthenticationCtxToken();
	    if(outcome.isEmpty()){
	    	requestNoteReqDTO.setSubmit(Boolean.FALSE);
			requestNoteReqDTO.setToken(token);
			requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
		    requestNoteResDTO = getDirectPurchaseSO().saveRequestNote(requestNoteReqDTO);
		    
	     } else if(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)){
	    	 requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
	    	 requestNoteReqDTO.setToken(token);
	    	 requestNoteReqDTO.setOutcome("");
	    	 requestNoteResDTO = getDirectPurchaseSO().updateRequestNote(requestNoteReqDTO);
	    	 renewSesionObject();
	    	 getManagedBean().setPrInquiryId(prInqId);
	    	 getManagedBean().setPurchaseInDefaultId(pidId);
	     }
	    
	    if (requestNoteResDTO.isSuccessful()) {
	    	ScRequestNote scRequestNote = requestNoteResDTO.getRequestNoteDetail();
	    	if(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)){
	    		requestNoteReqDTO.setRequestNoteId(scRequestNote.getRequestNoteId());
	    		requestNoteResDTO = getDirectPurchaseSO().getRequestNote(requestNoteReqDTO);
	    		scRequestNote = requestNoteResDTO.getRequestNoteDetail();
	    		getManagedBean().setOutComes(outcomes);
	    		getManagedBean().setTaskId(taskId);
	    		getManagedBean().setUserList(userList);
	    		reloadRequestNote(scRequestNote);
	    		successMessage(MessageConst.INFO_700025,ParameterTypeConst.DOCUMENT_TYPES,
	    				ParameterConst.DOCTYPE_REQUEST_NOTE,scRequestNote.getRequestNoteNo(), Boolean.FALSE);
	    	} else{
	    		successMessage(MessageConst.INFO_700025,ParameterTypeConst.DOCUMENT_TYPES,
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
	 * Reload request note.
	 *
	 * @param requestNote the request note
	 */
	private void reloadRequestNote(ScRequestNote requestNote){
		debugLogger("reloadRequestNote");
		getManagedBean().setRequestNote(requestNote);
		boolean itemSearchBackBtn = Boolean.TRUE;	
		getManagedBean().setAutoUpdate(true);
		getCurrentStatus(requestNote.getCurrentStatus().getPmStatus());
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

				// set condition field for saved request note
				if (!scRequestNoteDtl.isReRecommend()) {
					StringBuilder sb = new StringBuilder();
					sb.append("requestNoteItem.requestNoteItemId^");
					sb.append(scRequestNoteDtl.getRequestItem()
							.getRequestNoteItemId());

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
			}
			if(scRequestNoteDtl.getScRequestSupplierItem() != null && scRequestNoteDtl.getPriceType() != null){
				supplierListener(scRequestNoteDtl.getScRequestSupplierItem().getSupplierId(), scRequestNoteDtl.getPriceType().getParameterId() ,
						scRequestNoteDtl,scRequestNoteDtl.getAssignedUser().getUserId(), scRequestNoteDtl.getProcurementTypeCategory().getParameterId());
			}

			requestNoteDtls.add(scRequestNoteDtl);
		}

		// item search back button 
		getManagedBean().setItemSearchBackBtn(itemSearchBackBtn);
		getManagedBean().setRequestNoteDtls(requestNoteDtls);
		getManagedBean().setRequestNoteDtl(requestNoteDtls.get(0));

		// remarks
		requestNote.setRemarks(initializeRemarks(
				requestNote,
				requestNote.getCurrentStatus(),
				requestNote.getRemarks(),
				ScRequestNoteRemark.class));

		initializeApproverGroup();
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

			Object ctxToken = facesScopes.getBPMAuthenticationCtxToken();
			setUserDetails(requestNote);
			requestNoteReqDTO.setRequestNote(requestNote);
			if(outcome.isEmpty()) {	
				requestNoteReqDTO.setToken(ctxToken);
				requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
				requestNoteReqDTO.setSubmit(Boolean.TRUE);
				requestNoteResDTO = getDirectPurchaseSO().saveRequestNote(requestNoteReqDTO);
			} else {
				requestNoteReqDTO.setTaskId(getManagedBean().getTaskId());
				requestNoteReqDTO.setToken(ctxToken);
				requestNoteReqDTO.setOutcome(outcome);
				requestNoteResDTO = getDirectPurchaseSO().updateRequestNote(requestNoteReqDTO);
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
		if(outcome.equals(BpmOutcomeConst.BPM_SUBMIT) || 
				outcome.isEmpty() || 
				outcome.equals(BpmOutcomeConst.BPM_APPROVE)){

			parseRequestNote(scRequestNote, outcome);

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
				isDeliveryTermPrompt  = getDirectPurchaseSO().validateDeliveryTerm(requestNoteReqDTO);

				if(isDeliveryTermPrompt){
					StringBuilder message = new StringBuilder();
					message.append("Same delivery term is required for each PR generated.");
					getManagedBean().setPromptMessage(message.toString());
					RequestContext.getCurrentInstance().execute("deliveryTermConform.show()");
				}
			}
		}

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

	/**
	 * Parses the request note.
	 *
	 * @param requestNote the request note
	 * @param outcome the outcome
	 */
	private void parseRequestNote(ScRequestNote requestNote, String outcome) {
		List<ScRequestNoteDtl>	requestNoteDtls  = this.getManagedBean().getRequestNoteDtls();
		// Target Approver
		formApprover(requestNote);

		// check empty Objects. and concatenation RN title
		if (requestNoteDtls != null && requestNoteDtls.size() > 0) {
			List<String> results = new ArrayList<String>();
			StringBuilder sb  = new StringBuilder();
			String spacer = " ";
			int counter = 1;
			for (ScRequestNoteDtl requestNoteDtl : requestNoteDtls) {
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
				
				if(!results.contains(requestNoteDtl.getProcurementTitle())){
					results.add(requestNoteDtl.getProcurementTitle());
					if(requestNoteDtls.size() != counter){
						results.add(spacer);	
					} 

					sb.append(spacer).append(requestNoteDtl.getProcurementTitle());
					++counter;
				} 
				
				if(!BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
					// title validation
					if(StringUtils.isBlank(requestNoteDtl.getProcurementTitle())){
						throw new EpException(
								MessageConst.ERROR_760315,
								new String[] {ResourceBundleUtils.getMessage("lbl.sc.direct.purchase.title",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}	
				}
			}	

			//procurement title limit  - 500 
			if(!sb.toString().isEmpty() && sb.toString().length() >= SourcingConstants.PROCUREMENT_TITLE_LIMIT){
				String procTitle = sb.substring(0, SourcingConstants.PROCUREMENT_TITLE_LIMIT);
				requestNote.setTitle(procTitle);
			} else{
				requestNote.setTitle(sb.toString());
			}
		}

		// adding request note delete list
		if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome) || 
				BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome) || BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)){
			if(getManagedBean().getDeleteRequestNoteDtls() != null && getManagedBean().getDeleteRequestNoteDtls().size()>0){
				requestNoteDtls.addAll(getManagedBean().getDeleteRequestNoteDtls());
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
		boolean isShowMessage = getManagedBean().isExceededAmountAlerted();
		// show message for first submission, skip the message for second time submission
		if(requestNote.getRequestNoteDtls() != null && !requestNote.getRequestNoteDtls().isEmpty()){

			for (ScRequestNoteDtl requestNoteDtl : requestNote.getRequestNoteDtls()) {
				if(!requestNoteDtl.isDeleted()) {
					String pType = requestNoteDtl.getProcurementType()
							.getParameterCode();
					if ((!pType.equals(ParameterConst.PROC_TYPE_G2G) && !pType
							.equals(ParameterConst.PROC_TYPE_EMERGENCY))) {
						// Check total procurement amount per ptj per item per year
						FlItemSummaryReqDTO flReqDTO = new FlItemSummaryReqDTO();
						if (requestNoteDtl.getRequestItem().getCmItem() != null) {
							Long itemId = requestNoteDtl.getRequestItem().getCmItem()
									.getItemId();
							flReqDTO.setItemId(itemId);
							flReqDTO.setYear(Calendar.getInstance().get(Calendar.YEAR));
							flReqDTO.setOrgProfileId(requestNote.getPmOrgProfile()
									.getOrgProfileId());
							flReqDTO.setSupplierId(requestNoteDtl.getScRequestSupplierItem().getSupplierId());
							IFlItemSummarySO itemSummarySO = (IFlItemSummarySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFlItemSummarySO.class);

							BigDecimal totalAmt = itemSummarySO
									.retrieveAccumulatedAmount(flReqDTO);

							IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
							RuleReqDTO ruleReqDTO = new RuleReqDTO();
							ruleReqDTO.setRuleType(RuleTypeConst.ITEM_THRESHOLD);
							List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList();

							if (ruleValueList != null && ruleValueList.size() > 0) {
								PmConfigRuleValue configRuleValue = ruleValueList.get(0);
								//Rule : ITEM_THRESHOLD For 20k
								if(totalAmt.compareTo(new BigDecimal(configRuleValue.getRuleValue())) > 0) {
									if(!isShowMessage){
										getManagedBean().setExceededAmountAlerted(Boolean.TRUE);
										throw new EpException(MessageConst.ERROR_760036,
												new String[] {requestNoteDtl.getRequestItem().getItemName(), configRuleValue.getRuleValue()});
									}
								}
							} else {
								throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
							}
						}
					}

					// title validation
					if(StringUtils.isBlank(requestNoteDtl.getProcurementTitle())){
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

					// validate price type
					if(null == requestNoteDtl.getPriceType() || null == requestNoteDtl.getPriceType().getParameterId()){
						throw new EpException(MessageConst.ERROR_760343, new String[]{requestNoteDtl.getRequestItem().getItemName()});
					}
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

	/**
	 * Pre render page navigation to request note page based on task id
	 * @return void
	 */
	public void preRenderView() {
		debugLogger("preRenderView");
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		SCDirectPurchaseDataType directPurchaseDataType = super.getBpmTaskDetails(SCDirectPurchaseDataType.class, null);
		Long dpId = null;
		boolean zoneFlag = Boolean.FALSE;
		ScRequestNote	requestNote = null;

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
		RequestNoteResDTO requestNoteResDTO = getDirectPurchaseSO().getRequestNote(requestNoteReqDTO);
		if(requestNoteResDTO.getRequestNoteDetail() != null){
			requestNote = requestNoteResDTO.getRequestNoteDetail();
		}

		if(requestNote != null){
			if (requestNote.getScRNZone() != null) {
				getManagedBean().setScRNZone(requestNote.getScRNZone());	
			} else {
				getManagedBean().setScRNZone(new ScRNZone());	
			}
			
			retrieveRequestNoteDetails(requestNote, getManagedBean().getCurrentStatusId(), zoneFlag);
		}
		// title checker
		getManagedBean().setTitleChecker(Boolean.FALSE);
		FacesContext fc = FacesContext.getCurrentInstance();
		// page nav. to common request note
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REQUEST_NOTE_PAGE);
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
				}
				
				// Purchase In Default ->  Simple Quote  -> Request Note
				if(scRequestNoteDtl.getRequestItem() != null && scRequestNoteDtl.getRequestItem().getQuote() != null){
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

				if(null == scRequestNoteDtl.getAssignedUser()){
					scRequestNoteDtl.setAssignedUser(new PmUser());	
				}

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
				if(!scRequestNoteDtl.isReRecommend()) {
					StringBuilder sb = new StringBuilder();
					sb.append("requestNoteItem.requestNoteItemId^");
					sb.append(scRequestNoteDtl.getRequestItem().getRequestNoteItemId());

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
				}

				supplierListener(scRequestNoteDtl.getScRequestSupplierItem().getSupplierId(), scRequestNoteDtl.getPriceType().getParameterId() ,
						scRequestNoteDtl,scRequestNoteDtl.getAssignedUser().getUserId(), scRequestNoteDtl.getProcurementTypeCategory().getParameterId());
				requestNoteDtls.add(scRequestNoteDtl);
			}

			fulfilmentTypeEvent(requestNote.getRequestNoteDtls().get(0));
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
				StatusReqDTO statusReqDTO = new StatusReqDTO();
				if (DPStatusConstants.PRI_PENDING_RN_CREATION.equals(docStatusId) || 
						DPStatusConstants.PID_PENDING_RN_CREATION.equals(docStatusId) ||
						DPStatusConstants.SQ_PENDING_RN_CREATION.equals(docStatusId)){
					statusReqDTO.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
				} else {
					statusReqDTO.setStatusId(docStatusId);
				}

				StatusResDTO statusResDTO = getStatusSO().retrieveStatusByStatusId(statusReqDTO);

				getCurrentStatus(statusResDTO.getPmStatus());

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
			requestNoteReqDTO.setTaskExpiredDate(getManagedBean().getTaskExpiryDate());
			requestNoteReqDTO.setPidApprovers(getManagedBean().getPidApprovers());
			requestNoteReqDTO.setPidNo(getManagedBean().getPidNo());
			requestNoteReqDTO.setPidId(getManagedBean().getPurchaseInDefaultId());
			
			requestNoteResDTO = getDirectPurchaseSO().updateRequestNote(requestNoteReqDTO);
			if (requestNoteResDTO.isSuccessful()) {
				ScRequestNote scRequestNote = requestNoteResDTO.getRequestNoteDetail();
				if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)){
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
		
		getDirectPurchaseSO().validateRNBeforeSubmitting(requestNoteReqDTO);
		
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
						//applyPrepareByForSupplierRecs(selectedReqDtl, getManagedBean().getAssignedToUserId());
					} 
					
					if(selectedReqDtl.getScRequestNote().getSourceMethod().equals(ParameterConst.REQUEST_NOTE_SC)){
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
					}
					// rec supplier
					if(getManagedBean().getSmSupplier() != null && getManagedBean().getSmSupplier().getSupplierId() > 0 ){

						if(selectedReqDtl.getRequestItem() != null){							
							reqDTO.setRequestItemId(selectedReqDtl.getRequestItem().getRequestNoteItemId());
							reqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());
							RequestNoteResDTO resDTO = getDirectPurchaseSO().getSuppItemByReqItemIdAndSupplierId(reqDTO);
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

			requestSupplierItem = createScRequestSupplierItem(cmSupplierItem, requestItem, cmSupplierItem.getSmSupplier(), cmPricings);
			//ScSupplierItemPrice supplierItemPrice = createSupplierItemPrice(requestSupplierItem, cmPricings.get(0));
			if(requestSupplierItem.getSupplierItemPrices() != null && requestSupplierItem.getSupplierItemPrices().size()>0){
				requestSupplierItem.setSupplierItemPrice(requestSupplierItem.getSupplierItemPrices().get(0));
			}
			
			SmSupplier smSupplier = cmSupplierItem.getSmSupplier();
			requestSupplierItem.setSupplierId(smSupplier.getSupplierId());
			requestSupplierItem.setSupplierName(smSupplier.getCompanyName());
			requestSupplierItem.setMofNumber(getMofNor(smSupplier.getSupplierId()));

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
		
		try {
			
			
			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
			
			StatusResDTO statusResDTO = getStatusSO().retrieveStatusByStatusId(statusReqDTO);
			
			getCurrentStatus(statusResDTO.getPmStatus());
			
		} finally {
			//statusSO = null;
		}		
		
		// clear the justification type, price type and justification remarks
		getManagedBean().setPriceType("");
		getManagedBean().setJustificationType("");
		getManagedBean().setJustification("");
		getManagedBean().setProcTypeOption(Boolean.FALSE);
		
		getManagedBean().setAddToCartFlag(cart);
		// update item details data table
		getManagedBean().setAutoUpdate(Boolean.FALSE);
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
		String ItemCode = cmItem.getExtensionCode();
		CharSequence charSeq = ItemCode.subSequence(0, 2);
		Integer itemCode = Integer.valueOf(charSeq.toString());
		
		//  checking first two digits in itemCode  whether service or product
		if (itemCode >= 70 && itemCode <= 94) { // for service  between  70 and 94  
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
	    getDirectPurchaseSO().validateAddToCart(requestNoteReqDTO);
	    
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
	
		try {
			if(requestNote.getCurrentStatus() == null){
				
				// WorkFlow Status 
				List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
				ScRNWorkFlowStatus scWorkFlowStatus = new ScRNWorkFlowStatus();
				StatusReqDTO statusReqDTO = new StatusReqDTO();
				statusReqDTO.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
				StatusResDTO statusResDTO = getStatusSO().retrieveStatusByStatusId(statusReqDTO);
				getCurrentStatus(statusResDTO.getPmStatus());
				scWorkFlowStatus.setCurrent(Boolean.TRUE);
				scWorkFlowStatus.setPmStatus(statusResDTO.getPmStatus());
				scWorkFlowStatus.setRequestNote(requestNote);
				workFlowStatuses.add(scWorkFlowStatus);
				requestNote.setWorkFlowStatuses(workFlowStatuses);
			}
			
		} finally {
			//geStatusSO() = null;
		}
		
		requestNote.setRemarks(initializeRemarks(
				requestNote,
				null,
				requestNote.getRemarks(),
				ScRequestNoteRemark.class));

		getManagedBean().setFulfillmentType(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
		fulfilmentTypeEvent(scRequestNoteDtl);
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
		return params;
	}

	/**
	 * Event for procurement type  drop down in Add to Cart.
	 *  to show justification drop down and justification remark based on procurement type.
	 */
	public void procurementTypeEvent() {
		debugLogger("procurementTypeEvent"); 
		if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL) || getManagedBean().getProcurementType().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);
			   getManagedBean().setJustRequired(Boolean.FALSE);
			
		} else if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
		    getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_AD_HOC.value()));
		    getManagedBean().setJustRequired(Boolean.TRUE);
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
			
			getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
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
		if(scRequestNoteDtl.getRequestItem() != null){
			ScRequestItem requestItem = scRequestNoteDtl.getRequestItem();
			
			if(requestItem.getCmItem() != null){
				String ItemCode = requestItem.getCmItem().getExtensionCode();
				CharSequence charSeq = ItemCode.subSequence(0, 2); 
				Integer itemCode = Integer.valueOf(charSeq.toString()); 

				if(ItemCode != null && !ItemCode.isEmpty()){
					//  checking first two digits in itemCode whether service or product 
					if (itemCode >= 70 && itemCode <= 94) { // for service  between  70 and 94  
						prodOrService = false;
					} else if (itemCode < 70 || itemCode > 94) { // for product  < 70 and  > 94  
						prodOrService = true;
					} 
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
	 * Event for fulfillment type drop down in request note.
	 * to get PR/LOA Prepared By users based on Fulfillment type
	 * @param requestNoteDtl the request note dtl
	 */
	public void fulfilmentTypeEvent(ScRequestNoteDtl requestNoteDtl) {
		debugLogger("fulfilmentTypeEvent"); 
		
		UserReqDTO reqDTO = new UserReqDTO();
		List<PmUser> userlist = new ArrayList<PmUser>();
		if(requestNoteDtl.getFulfilmentType() != null ){
			if (requestNoteDtl.getFulfilmentType().getParameterCode().
					equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
				reqDTO.setRoleCode(RoleCode.REQUISITIONER.value());

			} else {
				List<String> roleCodes= new ArrayList<String>();
				roleCodes.add(RoleCode.DESK_OFFICER.value());
				roleCodes.add(RoleCode.REQUISITIONER.value());
				reqDTO.setRoleCodes(roleCodes);
			}
			
			if(getManagedBean().getRequestNote().getPmOrgProfile()!= null){
				reqDTO.setOrgProfileId(getManagedBean().getRequestNote().getPmOrgProfile().getOrgProfileId());
			}

			UserResDTO resDTO = getUserSO().retrieveUserListByRole(reqDTO);
			userlist = resDTO.getUserList();
			if(userlist != null && !userlist.isEmpty()){
				Collections.sort(userlist, new usersComparator());
			}
			getManagedBean().setUserList(userlist);
		}
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
					//fulfilmentTypeEvent(scRequestNoteDtl);
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
		ScRequestSupplierItem scRequestSupplierItem = requestNoteDtl.getScRequestSupplierItem();
			
			if(priceTypeId != null && priceTypeId > 0){
				if(scRequestSupplierItem !=null 
						&& scRequestSupplierItem.getSupplierItemPrices() != null 
					    && scRequestSupplierItem.getSupplierItemPrices().size()>0){

					for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {
						// zonal item price  
						if(requestNoteDtl.getTplSpecZoneDtl() != null){
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
			}  else{
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
			if(scRequestSupplierItem.getSupplierItemPrices() != null && scRequestSupplierItem.getSupplierItemPrices().size()>0){

				for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {
					// zonal item price  
					if(requestNoteDtl.getTplSpecZoneDtl() != null){
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
								PmParameter priceType = scSupplierItemPrice.getPriceType();
								requestNoteDtl.setPriceType(priceType);
								requestNoteDtl.setPriceTypeId(priceType.getParameterId());
								break;
							}
						}
					}
				}
			}
		} else{
			requestNoteDtl.setPriceType(new PmParameter());
			scRequestSupplierItem.setSupplierItemPrice(null);
		}
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
		if(requestNoteDtl.getvCmSupplierItem() != null && requestNoteDtl.getvCmSupplierItem().getDocId() >0) {
            
			catReqDTO.setSupplierItemId(requestNoteDtl.getvCmSupplierItem().getDocId());
			CatalogueResDTO catResDTO = catalogueSO.findActivePricing(catReqDTO);
			ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();

			List<CmPricing> cmPricings = catResDTO.getPriceLs();
			if(cmPricings != null && cmPricings.size() > 0){
				CmSupplierItem cmSupplierItem = cmPricings.get(0).getCmSupplierItem();
				cmSupplierItem.setCmPricings(cmPricings);

				requestSupplierItem = createScRequestSupplierItem(cmSupplierItem, requestNoteDtl.getRequestItem(), cmSupplierItem.getSmSupplier(), cmPricings);
				
				requestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
				
				if(requestNoteDtl.getPriceType() != null && requestNoteDtl.getPriceType().getParameterId() != null){
					supplierListener(null, requestNoteDtl.getPriceType().getParameterId(), requestNoteDtl , 
							requestNoteDtl.getAssignedUser().getUserId(), requestNoteDtl.getProcurementType().getParameterId());
				}
			}
		}
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

		if(requestNoteDtl.getScRequestSupplierItem() != null && requestNoteDtl.getScRequestSupplierItem().getRequestSupplierItemId() != null){
			ScRequestSupplierItem requestSupplierItem = requestNoteDtl.getScRequestSupplierItem();
			requestNoteReqDTO.setSupplierItemId(requestSupplierItem.getRequestSupplierItemId());
			RequestNoteResDTO requestNoteResTO =  getDirectPurchaseSO().getPricesBySupplierItemId(requestNoteReqDTO);
			if(requestNoteResTO.getSupplierItemPrices() != null && requestNoteResTO.getSupplierItemPrices().size()>0){
				requestSupplierItem.setSupplierItemPrices(requestNoteResTO.getSupplierItemPrices());
			}
			
			if(requestNoteDtl.getPriceType() != null && requestNoteDtl.getPriceType().getParameterId() != null){
				supplierListener(null, requestNoteDtl.getPriceType().getParameterId(), requestNoteDtl , 
						null, null);
			}
			
		}
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
				List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
				ScRNWorkFlowStatus scWorkFlowStatus = new ScRNWorkFlowStatus();
				StatusReqDTO statusReqDTO = new StatusReqDTO();
				statusReqDTO.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
				StatusResDTO statusResDTO = getStatusSO().retrieveStatusByStatusId(statusReqDTO);
				getCurrentStatus(statusResDTO.getPmStatus());
				scWorkFlowStatus.setCurrent(Boolean.TRUE);
				scWorkFlowStatus.setPmStatus(statusResDTO.getPmStatus());
				scWorkFlowStatus.setRequestNote(scRequestNote);
				workFlowStatuses.add(scWorkFlowStatus);
				scRequestNote.setWorkFlowStatuses(workFlowStatuses);
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
	
	/**
	 * Cancel item detail popup.
	 */
	public void cancelItemDetailPopup() {
		debugLogger("cancelItemDetailPopup");
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
		
		ScRequestItem requestItem = scRequestNoteDtl.getRequestItem();
		ScPrInquiry scPrInquiry = scRequestNoteDtl.getScRequestSupplierItem().getScPrInquiy();
		ScPIReportAttachment scPIReportAttachment = scRequestNoteDtl.getScPIReportAttachment();
		
		if (scPIReportAttachment == null) {
			IPrInquirySO prInquirySO = null;
			
			try {
				prInquirySO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
				
				PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
				reqDTO.setPrInquiryId(scPrInquiry.getPrInquiryId());

				PrInquiryResDTO resDTO = prInquirySO.retrievePurchaseInquiryPricesById(reqDTO);
				
				scPIReportAttachment = new ScPIReportAttachment();
				scPIReportAttachment.setScRequestNoteDtl(scRequestNoteDtl);
				scPIReportAttachment.setDocNo(scRequestNoteDtl.getScRequestNote().getRequestNoteNo());
				scPIReportAttachment.setPrefixFileName(getItemCode(requestItem));
				
				generatePurchaseInqPdfFromVelocity((ScAttachment)scPIReportAttachment, 
						resDTO.getSupplierItemPrices());
				
				scRequestNoteDtl.setScPIReportAttachment(scPIReportAttachment);
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				prInquirySO = null;
			}
		}
		
		downloadFile(scPIReportAttachment);
	}


	/**
	 * Pid validity.
	 *
	 * @param purchaseInDefault the purchase in default
	 * @param scRequestNote the sc request note
	 */
	private void pidValidity(ScPurchaseInDefault purchaseInDefault, ScRequestNote scRequestNote) {
		Date today  = DateUtils.getCurrentDateWithoutTime();
		Date ExpDate = DateUtils.addDays(scRequestNote.getCreatedDate(), purchaseInDefault.getPidValidDays());
		if(ExpDate.before(today)){

			throw new EpException(
					MessageConst.ERROR_760355,
					new String[] {purchaseInDefault.getPurchaseInDefaultNo()});
		}  
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
				RequestNoteResDTO requestNoteResDTO = getDirectPurchaseSO().getSuppItemWithPricesByReqItemId(requestNoteReqDTO);
				supplierItems = requestNoteResDTO.getSupplierItems();
				if (supplierItems != null) {
					for(ScRequestSupplierItem supplierItem: supplierItems){			
						for(int i = 0; i<supplierItem.getSupplierItemPrices().size();i++){
							supplierItemPrice = supplierItem.getSupplierItemPrices().get(i);
							marketSummaryAndPIBean = new MarketSummaryAndPIBean();
							marketSummaryAndPIBean.setSupplierId(supplierItem.getSmSupplier().getSupplierId());
							marketSummaryAndPIBean.setSupplierName(supplierItem.getSmSupplier().getCompanyName().replaceAll("&", "&#38;"));
							marketSummaryAndPIBean.setMinQty(scRequestItem.getMinqty() != null ? scRequestItem.getMinqty().toString() :"N/A");
							marketSummaryAndPIBean.setDeliveryTerm(supplierItemPrice.getPricing() !=null && supplierItemPrice.getPricing().getCmSupplierItem().getLeadTime() != null ? supplierItemPrice.getPricing().getCmSupplierItem().getLeadTime().toString() : "N/A");
							marketSummaryAndPIBean.setAddItemDesc(supplierItemPrice.getPricing() !=null && supplierItemPrice.getPricing().getCmSupplierItem().getItemDesc() != null ? supplierItemPrice.getPricing().getCmSupplierItem().getItemDesc().replaceAll("&", "&#38;") : "N/A");
							marketSummaryAndPIBean.setFrQty(supplierItemPrice.getFromQty() != null ? supplierItemPrice.getFromQty().intValue() : 0);
							marketSummaryAndPIBean.setToQty(supplierItemPrice.getToQty() != null ? supplierItemPrice.getToQty().intValue() : 0);
							marketSummaryAndPIBean.setPriceType(supplierItemPrice.getPriceType() != null ? getParameterDesc(supplierItemPrice.getPriceType()) : "N/A");
							marketSummaryAndPIBean.setUnitPrice(supplierItemPrice.getItemPrice() !=null ? amountDF.format(supplierItemPrice.getItemPrice()).toString() : "N/A");			    
							marketSummaryAndPIBean.setZone(supplierItemPrice.getTplSpecZone() !=null ? supplierItemPrice.getTplSpecZone().getZoneName().replaceAll("&", "&#38;") : "N/A");
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
		         SearchFilterBean filterBean = new SearchFilterBean(searchCondition);
		         filterBean.setCaseInsensitive(true);
		         filterBean.setSearchField(searchField);
		         filterBean.setSearchValue(searchValue);
		         searchBean.getDataFilterBeans().add(
		        		 filterBean);
		         System.out.println(searchField + "|" + searchValue + "|" + searchCondition);
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
					marketSummaryAndPIBean.setMinQty(scRequestItem.getMinqty() != null ? scRequestItem.getMinqty().toString() :"N/A");
					marketSummaryAndPIBean.setDeliveryTerm(cmPrice.getCmSupplierItem().getLeadTime() != null ? cmPrice.getCmSupplierItem().getLeadTime().toString() : "N/A");
					marketSummaryAndPIBean.setAddItemDesc(cmPrice.getCmSupplierItem().getItemDesc() != null ? cmPrice.getCmSupplierItem().getItemDesc().replaceAll("&", "&#38;") : "N/A");
					marketSummaryAndPIBean.setFrQty(cmPrice.getFromQty() != null ? cmPrice.getFromQty().intValue() : 0);
					marketSummaryAndPIBean.setToQty(cmPrice.getToQty() != null ? cmPrice.getToQty().intValue() : 0);
					marketSummaryAndPIBean.setPriceType(cmPrice.getPricingType() != null ? getParameterDesc(
							ParameterTypeConst.PRICE_TYPE, cmPrice.getPricingType()) : "N/A");
					marketSummaryAndPIBean.setUnitPrice(cmPrice.getPrice() !=null ? amountDF.format(cmPrice.getPrice()).toString() : "N/A");			    
					marketSummaryAndPIBean.setZone((cmPrice.getCmSupplierZone() !=null && cmPrice.getCmSupplierZone().getSuppZoneId() > 0) ? cmPrice.getCmSupplierZone().getSuppZoneName().replaceAll("&", "&#38;") : "N/A");
					requestNoteVOList.add(marketSummaryAndPIBean);
				}
			}
		}
			
		sortMarketSummaryList(requestNoteVOList);
		context.put("requestNoteVOList", requestNoteVOList);		
    	DocumentGenerator.getInstance().generatePdfFromVelocity(
    			"/sc/directPurchase/market-summary.vm", 
    			context, 
    			new FileOutputStream(getActualFilePath(attachment)));
	}
	
	/**
	 * Generate purchase inq pdf from velocity.
	 *
	 * @param attachment the attachment
	 * @param scSupplierItemPrices the sc supplier item prices
	 * @throws Exception the exception
	 */
	private void generatePurchaseInqPdfFromVelocity(ScAttachment attachment, List<ScSupplierItemPrice> scSupplierItemPrices) throws Exception {
		attachment.setFileName("pi_report.pdf");
		populateAttachmentDetails(attachment);
		
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		
		VelocityContext context = new VelocityContext();
		List<MarketSummaryAndPIBean> requestNoteVOList = new ArrayList<MarketSummaryAndPIBean>();
		MarketSummaryAndPIBean marketSummaryAndPIBean = null;
		ScSupplierItemPrice supplierItemPrice= null;
		ScRequestSupplierItem supplierItem = null;	
			for(int i = 0; i< scSupplierItemPrices.size();i++){
				supplierItemPrice = scSupplierItemPrices.get(i);
				supplierItem = supplierItemPrice.getScRequestSupplierItem();
				marketSummaryAndPIBean = new MarketSummaryAndPIBean();
				if(i==0){
					context.put("productCode", supplierItem.getRequestNoteItem().getCmItem().getExtensionCode());
					context.put("itemName", supplierItem.getRequestNoteItem().getCmItem().getItemName());
					context.put("desc", supplierItem.getRequestNoteItem().getItemDesc() != null ? supplierItem.getRequestNoteItem().getItemDesc() : "" );
					context.put("uom", supplierItem.getRequestNoteItem().getUom() != null ? supplierItem.getRequestNoteItem().getUom().getUomName() : "");
				}	
				marketSummaryAndPIBean.setSupplierId(supplierItem.getSmSupplier().getSupplierId());
				marketSummaryAndPIBean.setSupplierName(supplierItem.getSmSupplier().getCompanyName().replaceAll("&", "&#38;"));
				marketSummaryAndPIBean.setMinQty(supplierItem.getRequestNoteItem().getMinqty() != null ? supplierItem.getRequestNoteItem().getMinqty().toString() :"");
				marketSummaryAndPIBean.setDeliveryTerm(supplierItemPrice.getPricing().getCmSupplierItem().getLeadTime() != null ? supplierItemPrice.getPricing().getCmSupplierItem().getLeadTime().toString() : "");
				marketSummaryAndPIBean.setAddItemDesc(supplierItemPrice.getPricing().getCmSupplierItem().getItemDesc() != null ? supplierItemPrice.getPricing().getCmSupplierItem().getItemDesc().replaceAll("&", "&#38;") : "");
				marketSummaryAndPIBean.setFrQty(supplierItemPrice.getFromQty() != null ? supplierItemPrice.getFromQty().intValue() : 0);
				marketSummaryAndPIBean.setToQty(supplierItemPrice.getToQty() != null ? supplierItemPrice.getToQty().intValue(): 0);
				marketSummaryAndPIBean.setPriceType(supplierItemPrice.getPriceType() != null ? getParameterDesc(supplierItemPrice.getPriceType()) : "");
				marketSummaryAndPIBean.setUnitPrice(supplierItemPrice.getItemPrice() !=null ? amountDF.format(supplierItemPrice.getItemPrice()).toString() : "");			    
				marketSummaryAndPIBean.setZone(supplierItemPrice.getTplSpecZone() !=null ? supplierItemPrice.getTplSpecZone().getZoneName().replaceAll("&", "&#38;") : "");
				if(supplierItem.getScPrInquiy().getIsStockAvailable() != null){
				   marketSummaryAndPIBean.setStockAvailability(supplierItem.getScPrInquiy().getIsStockAvailable() == 0 ? "No" : "Yes");
				}else {
				   marketSummaryAndPIBean.setStockAvailability("");
				}
				requestNoteVOList.add(marketSummaryAndPIBean);
			}
		sortMarketSummaryList(requestNoteVOList);
		context.put("requestNoteVOList", requestNoteVOList);		
    	DocumentGenerator.getInstance().generatePdfFromVelocity(
    			"/sc/directPurchase/pi-report.vm", 
    			context, 
    			new FileOutputStream(getActualFilePath(attachment)));
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
				ScRequestSupplierItem requestSupplierItem = requestNoteDtl.getScRequestSupplierItem();
				String itemCode = requestSupplierItem.getRequestNoteItem().getCmItem().getExtensionCode();
				long uomId = requestSupplierItem.getCmSupplierItem().getPmUom().getUomId();
				
				
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
			         paramMap.put(ReportConst.PARAM_QUANTITY, requestNoteDtl.getRequestItem().getQuantity().toString());	
			         paramMap.put(ReportConst.PARAM_UOM_ID, uomId + "");	
			         System.out.println(searchField + "|" + searchValue + "|" + searchCondition);
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
	 * Gets the direct purchase so.
	 *
	 * @return the direct purchase so
	 */
	private IDirectPurchaseSO getDirectPurchaseSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
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
				resDTO = getDirectPurchaseSO().retrieveGroupListByOrgId(reqDTO);
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
			resDTO = getDirectPurchaseSO().retrieveApproverListByGroupId(reqDTO);
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
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
							.getMessage(
									ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default.exp.msg",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale),
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
		}
	}
	
	/**
	 * Gets the mof nor.
	 * 
	 * @param supplierId
	 *            the supplier id
	 * @return the mof nor
	 */
	private String getMofNor(Long supplierId) {
		String mofNo = "";
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMofAccountSO.class);
		MofAccountReqDTO accountReqDTO = new MofAccountReqDTO();
		accountReqDTO.setSupplierId(supplierId);
		MofAccountResDTO mofAccountResDTO = mofAccountSO
				.retrieveMofNoBySupplierId(accountReqDTO);
		if (mofAccountResDTO.getMofAccount() != null) {
			mofNo = mofAccountResDTO.getMofAccount().getMofNo();
		}
		return mofNo;
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
}