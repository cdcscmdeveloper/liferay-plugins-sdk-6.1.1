/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.loa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.loa.DpLOAManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class DpLOABackingBean.
 *
 * @author kenny
 * @since 27.05.2014
 */
@ManagedBean
@RequestScoped
public class DpLOABackingBean extends SourcingBaseBackingBean<DpLOAManagedBean> {
	private static final long serialVersionUID = 1L;
	/** The log. */
	public static Logger log = EpLogger.getLogger(DpLOABackingBean.class);
	
	/** The Flag */
	private boolean sqExpiredMessageFlag = false;

	/**
	 * Instantiates a new dp loa backing bean.
	 */
	public DpLOABackingBean() {
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
	}

	/**
	 * render the view based on the task id
	 * page navigation to Loa List page
	 */
	public void preRenderView() {
		debugLogger("preRenderView");

		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);

			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			getManagedBean().setSourceId(sourceId);
			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			loaDetailBackingBean.renewSesionObject();
			loaDetailBackingBean.getManagedBean().setSourceId(sourceId);
			loaDetailBackingBean.getManagedBean().setPayLoad(dpDataType);

			retrieveLOAList(sourceId);

			// retrieveLOAList(2122L);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.DP_LOA_LIST_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * retrieving LOI LOA List from database.
	 * 
	 * @param requestNoteId the request note id
	 */
	public void retrieveLOAList(Long requestNoteId) {
		debugLogger("retrieveLOAList");

		IPurchaseRequestSO purchaseRequestSO = null;
		try {
			purchaseRequestSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IPurchaseRequestSO.class);
			Long userId = new FacesScopes().getUserSessionBean().getUserId();

			PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
			reqDTO.setRequestNoteId(requestNoteId);
			List<String> fulfilmentTypes = new ArrayList<String>();
			fulfilmentTypes.add(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN);
			fulfilmentTypes.add(ParameterConst.FULFILMENT_TYPE_SCHEDULED);
			reqDTO.setFulfillmentTypes(fulfilmentTypes);
			reqDTO.setAssignedTo(userId);

			PurchaseRequestResDTO resDTO = purchaseRequestSO
					.retrievePurchaseRequestsByReqNote(reqDTO);

			if (resDTO.isSuccessful()) {
				List<ScPurchaseRequest> purchaseRequestList = resDTO
						.getListPurchaseRequest();
				if (purchaseRequestList != null
						&& purchaseRequestList.size() > 0) {
					ScPurchaseRequest purchaseRequest = purchaseRequestList
							.iterator().next();
					getManagedBean().setScRequestNote(
							purchaseRequest.getRequestNote());
					getManagedBean()
							.setPurchaseRequestList(purchaseRequestList);

					Long statusId = null;
					int completeCount = 0;
					boolean isCancelled = false;
					
					for (ScPurchaseRequest scPurchaseRequest : purchaseRequestList) {
						statusId = scPurchaseRequest.getScLoa().getPmStatus()
								.getStatusId();
						
						if (QTStatusConst.LOA_REJECTED.equals(statusId)) {
							isCancelled = true;
							//#32021 - Start
						} else if (!QTStatusConst.LOA_PENDING_VERIFICATION.equals(statusId) && 
								!QTStatusConst.LOA_COMPLETED.equals(statusId) && 
								!QTStatusConst.LOA_CANCELLED_DUE_TO_SIMPLE_QUOTE_VALIDITY_EXPIRED.equals(statusId) &&
								!QTStatusConst.LOA_REJECTED_DUE_TO_SIMPLE_QUOTE_VALIDITY_EXPIRED.equals(statusId) &&
								!QTStatusConst.LOA_CANCELLED.equals(statusId)) {
							//#32021 - End
							continue;
						}
						
						completeCount++;
					}
					
					if (isCancelled && completeCount == purchaseRequestList.size()) {
						getManagedBean().setShowBPMButtons(true);
					} else {
						getManagedBean().setShowBPMButtons(false);
					}
				}
			}

		} finally {
			purchaseRequestSO = null;
		}
	}
	
	/**
	 * update bpm - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */	
	public String updateBpmTaskAction(String outcome) {
		debugLogger("updateBpmTaskAction");

		IQTAwardSO qtAwardSO = null;

		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			String requestNoteNo = getManagedBean().getScRequestNote().getRequestNoteNo();
			boolean isSuccessful = qtAwardSO.updateBPMTask(
					requestNoteNo,
					getManagedBean().getSourceId(),
					new FacesScopes().getBPMAuthenticationCtxToken(),
					getManagedBean().getTaskId(), outcome);

			if (isSuccessful) {
				boolean isAcknowledgeRequired = Boolean.TRUE;
				if(BpmOutcomeConst.BPM_RERECOMMEND.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700059, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE,
							requestNoteNo, isAcknowledgeRequired);
				}
				if(BpmOutcomeConst.BPM_REQUOTE.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700058, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_SIMPLE_QUOTE,
							getManagedBean().getScRequestNote().getQuote().getQuoteNo(), isAcknowledgeRequired);
				}
				renewSesionObject();
			}
		} finally {
			qtAwardSO = null;
		}

		return null;
	}
	
	/**
	 * Check either SQ is valid or not (SQ only valid for 60 days after it closing date)
	 */
	public boolean isSqStillValid(Long currentStatusId){		
		
		Boolean validDate = true;	
		ScQuote scQuote = getManagedBean().getScRequestNote().getQuote();
			
		Integer sqTimeValidity = Integer.valueOf(EpPropertiesUtils.getInstance()
				.getProperty("sqTimeValidity"));
		
		if(scQuote != null){			
			Date currentDate = new Date();
			Date closingDate = scQuote.getEndDate();
				
			Calendar cal = Calendar.getInstance();
			cal.clear(); 
			cal.setTime(closingDate);
			cal.add(Calendar.DATE, sqTimeValidity); //add 60days to closing/end date
				
			if(currentStatusId.equals(QTStatusConst.LOA_PENDING_SUBMISSION)
			   || currentStatusId.equals(QTStatusConst.LOA_PENDING_APPROVAL)
			   || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION)
			   || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION_APPROVAL)){
				if(currentDate.compareTo(cal.getTime()) >= 0){
					validDate = false;
					sqExpiredMessageFlag = true;
				}
			}					
		}							
		return validDate;	
	}
}