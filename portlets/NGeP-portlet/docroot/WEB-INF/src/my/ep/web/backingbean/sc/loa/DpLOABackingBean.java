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
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.loa.DpLOAManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author kenny
 * @since 27.05.2014
 * 
 */
@ManagedBean
@RequestScoped
public class DpLOABackingBean extends
		SourcingBaseBackingBean<DpLOAManagedBean> {
	public static Logger log = EpLogger.getLogger(DpLOABackingBean.class);

	public DpLOABackingBean() {
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}
	
	public void preRenderView() {
		debugLogger("preRenderView");
		
		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);
			
			retrieveLOAList(Long.valueOf(dpDataType.getDpId()));
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.DP_LOA_LIST_PAGE);	
		} catch (EpException epe) {
			handleException(epe);
		} catch (Exception e) {
			handleException(e);
		} finally {
			
		}
	}

	/**
	 * retrieving LOI LOA List
	 */
	public void retrieveLOAList(Long requestNoteId) {
		debugLogger("retrieveLOAList");

		IPurchaseRequestSO purchaseRequestSO = null;
		try {
			purchaseRequestSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseRequestSO.class);
			Long userId = new FacesScopes().getUserSessionBean().getUserId();
			
			PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
			reqDTO.setRequestNoteId(requestNoteId);
			List<String> fulfilmentTypes = new ArrayList<String>();
			fulfilmentTypes.add(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN);
			fulfilmentTypes.add(ParameterConst.FULFILMENT_TYPE_SCHEDULED);
			reqDTO.setFulfillmentTypes(fulfilmentTypes);
			reqDTO.setAssignedTo(userId);

			PurchaseRequestResDTO resDTO = purchaseRequestSO.retrievePurchaseRequestsByReqNote(reqDTO);
			
			if (resDTO.isSuccessful()) {
				List<ScPurchaseRequest> purchaseRequestList = resDTO.getListPurchaseRequest();
				if (purchaseRequestList != null && purchaseRequestList.size() > 0) {
					ScPurchaseRequest purchaseRequest = purchaseRequestList.iterator().next();
					
					getManagedBean().setScRequestNote(purchaseRequest.getRequestNote());
					getManagedBean().setPurchaseRequestList(purchaseRequestList);	
				}	
			}

		} finally {
			purchaseRequestSO = null;
		}
	}
}
