/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.loa;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.loa.DpLOAManagedBean;
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
		retrieveLOAList(3L);
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}
	
	public String createLOAAction(ScPurchaseRequest purchaseRequest) {
		debugLogger("createLOAAction");
		
		ScLoiLoa scLoiLoa = new ScLoiLoa();
		ScRequestNoteDtl requestNoteDtl = purchaseRequest.getRequestNoteDtls().iterator().next();
		SmSupplier supplier = requestNoteDtl.getScRequestSupplierItem().getSmSupplier();
		
		scLoiLoa.setDocType("RN");
		scLoiLoa.setDocId(purchaseRequest.getPurchaseRequestId());
		scLoiLoa.setPmOrgProfile(getManagedBean().getScRequestNote().getPmOrgProfile());
		
		populateSupplierInfo(scLoiLoa, supplier);
		
		return new LOADetailBackingBean().createLOAAction(scLoiLoa);
	}
	
	public String editLOAAction(ScPurchaseRequest purchaseRequest) {
		debugLogger("editLOAAction");
		
		return null;
	}

	/**
	 * retrieving LOI LOA List
	 */
	private void retrieveLOAList(Long requestNoteId) {
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

	private void populateSupplierInfo(ScLoiLoa scLoiLoa, SmSupplier supplier) {
		scLoiLoa.setSmSupplier(supplier);
		scLoiLoa.setSupplierName(supplier.getCompanyName());
		//scLoiLoa.setMofNumber(supplier.getSmMofAccounts().iterator().next());
	}
}
