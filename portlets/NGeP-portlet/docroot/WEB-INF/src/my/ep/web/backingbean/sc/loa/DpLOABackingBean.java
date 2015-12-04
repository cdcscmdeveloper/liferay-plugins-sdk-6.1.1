/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.loa;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.requestnote.RequestNoteBackingBean;
import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;
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
	
	/** The log. */
	public static Logger log = EpLogger.getLogger(DpLOABackingBean.class);

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
							
						} else if (!QTStatusConst.LOA_PENDING_VERIFICATION.equals(statusId) && 
								!QTStatusConst.LOA_COMPLETED.equals(statusId)) {
							
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
		IPurchaseRequestSO purchaseRequestSO = null;
		purchaseRequestSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IPurchaseRequestSO.class);
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		Object token = new FacesScopes().getBPMAuthenticationCtxToken();
		PurchaseRequestReqDTO   requestReqDTO = new PurchaseRequestReqDTO();
		ScRequestNote requestNote  = getManagedBean().getScRequestNote();
		requestReqDTO.setAssignedTo(userId);
		requestReqDTO.setRequestNoteId(requestNote.getRequestNoteId());

		if(BpmOutcomeConst.BPM_RERECOMMEND.equalsIgnoreCase(outcome)){
			RequestNoteBackingBean requestNoteBackingBean  = new RequestNoteBackingBean();
			PurchaseRequestResDTO resDTO = purchaseRequestSO.reRecommendRejectedItems(requestReqDTO);
			ScRequestNote scRequestNote  = resDTO.getRequestNoteDetail();

			if(scRequestNote != null){
				requestNoteBackingBean.renewSesionObject();
				requestNoteBackingBean.getManagedBean().setRequestNote(scRequestNote);
				requestNoteBackingBean.getManagedBean().setEndLoaTaskId(getManagedBean().getTaskId());
				requestNoteBackingBean.getManagedBean().setEndLoaToken(token);
				requestNoteBackingBean.getManagedBean().setEndLoaOutcome(outcome);
				requestNoteBackingBean.getManagedBean().setEndLoaRN(requestNote);
				requestNoteBackingBean.reRecommend(scRequestNote);
			}
		}

		if(BpmOutcomeConst.BPM_REQUOTE.equalsIgnoreCase(outcome)){
			SimpleQuoteBackingBean simpleQuoteBackingBean = new SimpleQuoteBackingBean();
			PurchaseRequestResDTO resDTO =  purchaseRequestSO.reQuoteRejectedItems(requestReqDTO);
			ScQuote scQuote  = resDTO.getScQuote();

			if(scQuote != null){
				simpleQuoteBackingBean.renewSesionObject();
				simpleQuoteBackingBean.getManagedBean().setScQuote(scQuote);
				simpleQuoteBackingBean.getManagedBean().setEndLoaTaskId(getManagedBean().getTaskId());
				simpleQuoteBackingBean.getManagedBean().setEndLoaToken(token);
				simpleQuoteBackingBean.getManagedBean().setEndLoaOutcome(outcome);
				simpleQuoteBackingBean.getManagedBean().setEndLoaRN(requestNote);
				simpleQuoteBackingBean.reQuote(scQuote);
			}
		}

		return null;
	}
}
